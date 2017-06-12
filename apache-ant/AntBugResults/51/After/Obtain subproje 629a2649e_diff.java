diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 5b510102a..88db1a058 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,1243 +1,1262 @@
 /*
  * Copyright  2000-2005 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.EOFException;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.WeakHashMap;
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.helper.DefaultExecutor;
 import org.apache.tools.ant.types.FilterSet;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Description;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.StringUtils;
 
 /**
  * Central representation of an Ant project. This class defines an
  * Ant project with all of its targets, tasks and various other
  * properties. It also provides the mechanism to kick off a build using
  * a particular target name.
  * <p>
  * This class also encapsulates methods which allow files to be referred
  * to using abstract path names which are translated to native system
  * file paths at runtime.
  *
  */
 public class Project {
     /** Message priority of &quot;error&quot;. */
     public static final int MSG_ERR = 0;
     /** Message priority of &quot;warning&quot;. */
     public static final int MSG_WARN = 1;
     /** Message priority of &quot;information&quot;. */
     public static final int MSG_INFO = 2;
     /** Message priority of &quot;verbose&quot;. */
     public static final int MSG_VERBOSE = 3;
     /** Message priority of &quot;debug&quot;. */
     public static final int MSG_DEBUG = 4;
 
     /**
      * Constant for the &quot;visiting&quot; state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITING = "VISITING";
     /**
      * Constant for the &quot;visited&quot; state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITED = "VISITED";
 
     /**
      * Version constant for Java 1.0 .
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_0} instead.
      */
     public static final String JAVA_1_0 = JavaEnvUtils.JAVA_1_0;
     /**
      * Version constant for Java 1.1 .
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_1} instead.
      */
     public static final String JAVA_1_1 = JavaEnvUtils.JAVA_1_1;
     /**
      * Version constant for Java 1.2 .
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_2} instead.
      */
     public static final String JAVA_1_2 = JavaEnvUtils.JAVA_1_2;
     /**
      * Version constant for Java 1.3 .
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_3} instead.
      */
     public static final String JAVA_1_3 = JavaEnvUtils.JAVA_1_3;
     /**
      * Version constant for Java 1.4 .
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_4} instead.
      */
     public static final String JAVA_1_4 = JavaEnvUtils.JAVA_1_4;
 
     /** Default filter start token. */
     public static final String TOKEN_START = FilterSet.DEFAULT_TOKEN_START;
     /** Default filter end token. */
     public static final String TOKEN_END = FilterSet.DEFAULT_TOKEN_END;
 
     /** Instance of a utility class to use for file operations. */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /** Name of this project. */
     private String name;
     /** Description for this project (if any). */
     private String description;
 
 
     /** Map of references within the project (paths etc) (String to Object). */
     private Hashtable references = new AntRefTable();
 
     /** Name of the project's default target. */
     private String defaultTarget;
 
     /** Map from target names to targets (String to Target). */
     private Hashtable targets = new Hashtable();
     /** Set of global filters. */
     private FilterSet globalFilterSet = new FilterSet();
     {
         // Initialize the globalFileSet's project
         globalFilterSet.setProject(this);
     }
 
     /**
      * Wrapper around globalFilterSet. This collection only ever
      * contains one FilterSet, but the wrapper is needed in order to
      * make it easier to use the FileUtils interface.
      */
     private FilterSetCollection globalFilters
         = new FilterSetCollection(globalFilterSet);
 
     /** Project base directory. */
     private File baseDir;
 
     /** List of listeners to notify of build events. */
     private Vector listeners = new Vector();
 
     /**
      * The Ant core classloader--may be <code>null</code> if using
      * parent classloader.
      */
     private ClassLoader coreLoader = null;
 
     /** Records the latest task to be executed on a thread. */
     private Map/*<Thread,Task>*/ threadTasks = Collections.synchronizedMap(new WeakHashMap());
 
     /** Records the latest task to be executed on a thread group. */
     private Map/*<ThreadGroup,Task>*/ threadGroupTasks = Collections.synchronizedMap(new WeakHashMap());
 
     /**
      * Called to handle any input requests.
      */
     private InputHandler inputHandler = null;
 
     /**
      * The default input stream used to read any input.
      */
     private InputStream defaultInputStream = null;
 
     /**
      * Keep going flag.
      */
     private boolean keepGoingMode = false;
 
     /**
      * Flag which catches Listeners which try to use System.out or System.err .
      */
     private boolean loggingMessage = false;
 
     /**
      * Set the input handler.
      *
      * @param handler the InputHandler instance to use for gathering input.
      */
     public void setInputHandler(InputHandler handler) {
         inputHandler = handler;
     }
 
     /**
      * Set the default System input stream. Normally this stream is set to
      * System.in. This inputStream is used when no task input redirection is
      * being performed.
      *
      * @param defaultInputStream the default input stream to use when input
      *        is requested.
      * @since Ant 1.6
      */
     public void setDefaultInputStream(InputStream defaultInputStream) {
         this.defaultInputStream = defaultInputStream;
     }
 
     /**
      * Get this project's input stream.
      *
      * @return the InputStream instance in use by this Project instance to
      * read input.
      */
     public InputStream getDefaultInputStream() {
         return defaultInputStream;
     }
 
     /**
      * Retrieve the current input handler.
      *
      * @return the InputHandler instance currently in place for the project
      *         instance.
      */
     public InputHandler getInputHandler() {
         return inputHandler;
     }
 
     /**
      * Create a new Ant project.
      */
     public Project() {
         inputHandler = new DefaultInputHandler();
     }
 
     /**
-     * Init a sub project--used by taskdefs.Ant .
+     * Create and initialize a subproject. By default the subproject will be of
+     * the same type as its parent. If a no-arg constructor is unavailable, the
+     * <code>Project</code> class will be used.
+     * @return a Project instance configured as a subproject of this Project.
+     * @since Ant 1.7
+     */
+    public Project createSubProject() {
+        Project subProject = null;
+        try {
+            subProject = (Project) (getClass().newInstance());
+        } catch (Exception e) {
+            subProject = new Project();
+        }
+        initSubProject(subProject);
+        return subProject;
+    }
+
+    /**
+     * Initialize a subproject.
      * @param subProject the subproject to initialize.
      */
     public void initSubProject(Project subProject) {
         ComponentHelper.getComponentHelper(subProject)
             .initSubProject(ComponentHelper.getComponentHelper(this));
+        subProject.setDefaultInputStream(getDefaultInputStream());
         subProject.setKeepGoingMode(this.isKeepGoingMode());
         subProject.setExecutor(getExecutor().getSubProjectExecutor());
     }
 
     /**
      * Initialise the project.
      *
      * This involves setting the default task definitions and loading the
      * system properties.
      *
      * @exception BuildException if the default task list cannot be loaded.
      */
     public void init() throws BuildException {
         setJavaVersionProperty();
 
         ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
 
         setSystemProperties();
     }
 
     /**
      * Factory method to create a class loader for loading classes from
      * a given path.
      *
      * @param path the path from which classes are to be loaded.
      *
      * @return an appropriate classloader.
      */
     public AntClassLoader createClassLoader(Path path) {
         AntClassLoader loader = new AntClassLoader();
         loader.setProject(this);
         loader.setClassPath(path);
         return loader;
     }
 
     /**
      * Set the core classloader for the project. If a <code>null</code>
      * classloader is specified, the parent classloader should be used.
      *
      * @param coreLoader The classloader to use for the project.
      *                   May be <code>null</code>.
      */
     public void setCoreLoader(ClassLoader coreLoader) {
         this.coreLoader = coreLoader;
     }
 
     /**
      * Return the core classloader to use for this project.
      * This may be <code>null</code>, indicating that
      * the parent classloader should be used.
      *
      * @return the core classloader to use for this project.
      *
      */
     public ClassLoader getCoreLoader() {
         return coreLoader;
     }
 
     /**
      * Add a build listener to the list. This listener will
      * be notified of build events for this project.
      *
      * @param listener The listener to add to the list.
      *                 Must not be <code>null</code>.
      */
     public synchronized void addBuildListener(BuildListener listener) {
         // create a new Vector to avoid ConcurrentModificationExc when
         // the listeners get added/removed while we are in fire
         Vector newListeners = getBuildListeners();
         newListeners.addElement(listener);
         listeners = newListeners;
     }
 
     /**
      * Remove a build listener from the list. This listener
      * will no longer be notified of build events for this project.
      *
      * @param listener The listener to remove from the list.
      *                 Should not be <code>null</code>.
      */
     public synchronized void removeBuildListener(BuildListener listener) {
         // create a new Vector to avoid ConcurrentModificationExc when
         // the listeners get added/removed while we are in fire
         Vector newListeners = getBuildListeners();
         newListeners.removeElement(listener);
         listeners = newListeners;
     }
 
     /**
      * Return a copy of the list of build listeners for the project.
      *
      * @return a list of build listeners for the project
      */
     public Vector getBuildListeners() {
         return (Vector) listeners.clone();
     }
 
     /**
      * Write a message to the log with the default log level
      * of MSG_INFO .
      * @param message The text to log. Should not be <code>null</code>.
      */
 
     public void log(String message) {
         log(message, MSG_INFO);
     }
 
     /**
      * Write a project level message to the log with the given log level.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The log priority level to use.
      */
     public void log(String message, int msgLevel) {
         fireMessageLogged(this, message, msgLevel);
     }
 
     /**
      * Write a task level message to the log with the given log level.
      * @param task The task to use in the log. Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The log priority level to use.
      */
     public void log(Task task, String message, int msgLevel) {
         fireMessageLogged(task, message, msgLevel);
     }
 
     /**
      * Write a target level message to the log with the given log level.
      * @param target The target to use in the log.
      *               Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The log priority level to use.
      */
     public void log(Target target, String message, int msgLevel) {
         fireMessageLogged(target, message, msgLevel);
     }
 
     /**
      * Return the set of global filters.
      *
      * @return the set of global filters.
      */
     public FilterSet getGlobalFilterSet() {
         return globalFilterSet;
     }
 
     /**
      * Set a property. Any existing property of the same name
      * is overwritten, unless it is a user property.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      */
     public void setProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).
                 setProperty(null, name, value, true);
     }
 
     /**
      * Set a property if no value currently exists. If the property
      * exists already, a message is logged and the method returns with
      * no other effect.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @since 1.5
      */
     public void setNewProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setNewProperty(null, name,
                                                               value);
     }
 
     /**
      * Set a user property, which cannot be overwritten by
      * set/unset property calls. Any previous value is overwritten.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @see #setProperty(String,String)
      */
     public void setUserProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setUserProperty(null, name,
                                                                value);
     }
 
     /**
      * Set a user property, which cannot be overwritten by set/unset
      * property calls. Any previous value is overwritten. Also marks
      * these properties as properties that have not come from the
      * command line.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @see #setProperty(String,String)
      */
     public void setInheritedProperty(String name, String value) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.setInheritedProperty(null, name, value);
     }
 
     /**
      * Set a property unless it is already defined as a user property
      * (in which case the method returns silently).
      *
      * @param name The name of the property.
      *             Must not be <code>null</code>.
      * @param value The property value. Must not be <code>null</code>.
      */
     private void setPropertyInternal(String name, String value) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.setProperty(null, name, value, false);
     }
 
     /**
      * Return the value of a property, if it is set.
      *
      * @param propertyName The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
     public String getProperty(String propertyName) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getProperty(null, propertyName);
     }
 
     /**
      * Replace ${} style constructions in the given value with the
      * string value of the corresponding data types.
      *
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>.
      *
      * @return the given string with embedded property names replaced
      *         by values, or <code>null</code> if the given string is
      *         <code>null</code>.
      *
      * @exception BuildException if the given value has an unclosed
      *                           property name, e.g. <code>${xxx</code>.
      */
     public String replaceProperties(String value)
         throws BuildException {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.replaceProperties(null, value, null);
     }
 
     /**
      * Return the value of a user property, if it is set.
      *
      * @param propertyName The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
      public String getUserProperty(String propertyName) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getUserProperty(null, propertyName);
     }
 
     /**
      * Return a copy of the properties table.
      * @return a hashtable containing all properties
      *         (including user properties).
      */
     public Hashtable getProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.getProperties();
     }
 
     /**
      * Return a copy of the user property hashtable.
      * @return a hashtable containing just the user properties.
      */
     public Hashtable getUserProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.getUserProperties();
     }
 
     /**
      * Copy all user properties that have been set on the command
      * line or a GUI tool from this instance to the Project instance
      * given as the argument.
      *
      * <p>To copy all &quot;user&quot; properties, you will also have to call
      * {@link #copyInheritedProperties copyInheritedProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyUserProperties(Project other) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.copyUserProperties(other);
     }
 
     /**
      * Copy all user properties that have not been set on the
      * command line or a GUI tool from this instance to the Project
      * instance given as the argument.
      *
      * <p>To copy all &quot;user&quot; properties, you will also have to call
      * {@link #copyUserProperties copyUserProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyInheritedProperties(Project other) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.copyInheritedProperties(other);
     }
 
     /**
      * Set the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      *
      * @deprecated use setDefault
      * @see #setDefault(String)
      */
     public void setDefaultTarget(String defaultTarget) {
         this.defaultTarget = defaultTarget;
     }
 
     /**
      * Return the name of the default target of the project.
      * @return name of the default target or
      *         <code>null</code> if no default has been set.
      */
     public String getDefaultTarget() {
         return defaultTarget;
     }
 
     /**
      * Set the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      */
     public void setDefault(String defaultTarget) {
         this.defaultTarget = defaultTarget;
     }
 
     /**
      * Set the name of the project, also setting the user
      * property <code>ant.project.name</code>.
      *
      * @param name The name of the project.
      *             Must not be <code>null</code>.
      */
     public void setName(String name) {
         setUserProperty("ant.project.name",  name);
         this.name = name;
     }
 
     /**
      * Return the project name, if one has been set.
      *
      * @return the project name, or <code>null</code> if it hasn't been set.
      */
     public String getName() {
         return name;
     }
 
     /**
      * Set the project description.
      *
      * @param description The description of the project.
      *                    May be <code>null</code>.
      */
     public void setDescription(String description) {
         this.description = description;
     }
 
     /**
      * Return the project description, if one has been set.
      *
      * @return the project description, or <code>null</code> if it hasn't
      *         been set.
      */
     public String getDescription() {
         if (description == null) {
             description = Description.getDescription(this);
         }
         return description;
     }
 
     /**
      * Add a filter to the set of global filters.
      *
      * @param token The token to filter.
      *              Must not be <code>null</code>.
      * @param value The replacement value.
      *              Must not be <code>null</code>.
      * @deprecated Use getGlobalFilterSet().addFilter(token,value)
      *
      * @see #getGlobalFilterSet()
      * @see FilterSet#addFilter(String,String)
      */
     public void addFilter(String token, String value) {
         if (token == null) {
             return;
         }
         globalFilterSet.addFilter(new FilterSet.Filter(token, value));
     }
 
     /**
      * Return a hashtable of global filters, mapping tokens to values.
      *
      * @return a hashtable of global filters, mapping tokens to values
      *         (String to String).
      *
      * @deprecated Use getGlobalFilterSet().getFilterHash()
      *
      * @see #getGlobalFilterSet()
      * @see FilterSet#getFilterHash()
      */
     public Hashtable getFilters() {
         // we need to build the hashtable dynamically
         return globalFilterSet.getFilterHash();
     }
 
     /**
      * Set the base directory for the project, checking that
      * the given filename exists and is a directory.
      *
      * @param baseD The project base directory.
      *              Must not be <code>null</code>.
      *
      * @exception BuildException if the directory if invalid.
      */
     public void setBasedir(String baseD) throws BuildException {
         setBaseDir(new File(baseD));
     }
 
     /**
      * Set the base directory for the project, checking that
      * the given file exists and is a directory.
      *
      * @param baseDir The project base directory.
      *                Must not be <code>null</code>.
      * @exception BuildException if the specified file doesn't exist or
      *                           isn't a directory.
      */
     public void setBaseDir(File baseDir) throws BuildException {
         baseDir = FILE_UTILS.normalize(baseDir.getAbsolutePath());
         if (!baseDir.exists()) {
             throw new BuildException("Basedir " + baseDir.getAbsolutePath()
                 + " does not exist");
         }
         if (!baseDir.isDirectory()) {
             throw new BuildException("Basedir " + baseDir.getAbsolutePath()
                 + " is not a directory");
         }
         this.baseDir = baseDir;
         setPropertyInternal("basedir", this.baseDir.getPath());
         String msg = "Project base dir set to: " + this.baseDir;
          log(msg, MSG_VERBOSE);
     }
 
     /**
      * Return the base directory of the project as a file object.
      *
      * @return the project base directory, or <code>null</code> if the
      *         base directory has not been successfully set to a valid value.
      */
     public File getBaseDir() {
         if (baseDir == null) {
             try {
                 setBasedir(".");
             } catch (BuildException ex) {
                 ex.printStackTrace();
             }
         }
         return baseDir;
     }
 
     /**
      * Set &quot;keep-going&quot; mode. In this mode Ant will try to execute
      * as many targets as possible. All targets that do not depend
      * on failed target(s) will be executed.  If the keepGoing settor/getter
      * methods are used in conjunction with the <code>ant.executor.class</code>
      * property, they will have no effect.
      * @param keepGoingMode &quot;keep-going&quot; mode
      * @since Ant 1.6
      */
     public void setKeepGoingMode(boolean keepGoingMode) {
         this.keepGoingMode = keepGoingMode;
     }
 
     /**
      * Return the keep-going mode.  If the keepGoing settor/getter
      * methods are used in conjunction with the <code>ant.executor.class</code>
      * property, they will have no effect.
      * @return &quot;keep-going&quot; mode
      * @since Ant 1.6
      */
     public boolean isKeepGoingMode() {
         return this.keepGoingMode;
     }
 
     /**
      * Return the version of Java this class is running under.
      * @return the version of Java as a String, e.g. "1.1" .
      * @see org.apache.tools.ant.util.JavaEnvUtils#getJavaVersion
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static String getJavaVersion() {
         return JavaEnvUtils.getJavaVersion();
     }
 
     /**
      * Set the <code>ant.java.version</code> property and tests for
      * unsupported JVM versions. If the version is supported,
      * verbose log messages are generated to record the Java version
      * and operating system name.
      *
      * @exception BuildException if this Java version is not supported.
      *
      * @see org.apache.tools.ant.util.JavaEnvUtils#getJavaVersion
      */
     public void setJavaVersionProperty() throws BuildException {
         String javaVersion = JavaEnvUtils.getJavaVersion();
         setPropertyInternal("ant.java.version", javaVersion);
 
         // sanity check
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_0)) {
             throw new BuildException("Ant cannot work on Java 1.0");
         }
         log("Detected Java version: " + javaVersion + " in: "
             + System.getProperty("java.home"), MSG_VERBOSE);
 
         log("Detected OS: " + System.getProperty("os.name"), MSG_VERBOSE);
     }
 
     /**
      * Add all system properties which aren't already defined as
      * user properties to the project properties.
      */
     public void setSystemProperties() {
         Properties systemP = System.getProperties();
         Enumeration e = systemP.propertyNames();
         while (e.hasMoreElements()) {
             String propertyName = (String) e.nextElement();
             String value = systemP.getProperty(propertyName);
             this.setPropertyInternal(propertyName, value);
         }
     }
 
     /**
      * Add a new task definition to the project.
      * Attempting to override an existing definition with an
      * equivalent one (i.e. with the same classname) results in
      * a verbose log message. Attempting to override an existing definition
      * with a different one results in a warning log message and
      * invalidates any tasks which have already been created with the
      * old definition.
      *
      * @param taskName The name of the task to add.
      *                 Must not be <code>null</code>.
      * @param taskClass The full name of the class implementing the task.
      *                  Must not be <code>null</code>.
      *
      * @exception BuildException if the class is unsuitable for being an Ant
      *                           task. An error level message is logged before
      *                           this exception is thrown.
      *
      * @see #checkTaskClass(Class)
      */
     public void addTaskDefinition(String taskName, Class taskClass)
          throws BuildException {
         ComponentHelper.getComponentHelper(this).addTaskDefinition(taskName,
                 taskClass);
     }
 
     /**
      * Check whether or not a class is suitable for serving as Ant task.
      * Ant task implementation classes must be public, concrete, and have
      * a no-arg constructor.
      *
      * @param taskClass The class to be checked.
      *                  Must not be <code>null</code>.
      *
      * @exception BuildException if the class is unsuitable for being an Ant
      *                           task. An error level message is logged before
      *                           this exception is thrown.
      */
     public void checkTaskClass(final Class taskClass) throws BuildException {
         ComponentHelper.getComponentHelper(this).checkTaskClass(taskClass);
 
         if (!Modifier.isPublic(taskClass.getModifiers())) {
             final String message = taskClass + " is not public";
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         if (Modifier.isAbstract(taskClass.getModifiers())) {
             final String message = taskClass + " is abstract";
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         try {
             taskClass.getConstructor((Class[]) null);
             // don't have to check for public, since
             // getConstructor finds public constructors only.
         } catch (NoSuchMethodException e) {
             final String message = "No public no-arg constructor in "
                 + taskClass;
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         } catch (LinkageError e) {
             String message = "Could not load " + taskClass + ": " + e;
             log(message, Project.MSG_ERR);
             throw new BuildException(message, e);
         }
         if (!Task.class.isAssignableFrom(taskClass)) {
             TaskAdapter.checkTaskClass(taskClass, this);
         }
     }
 
     /**
      * Return the current task definition hashtable. The returned hashtable is
      * &quot;live&quot; and so should not be modified.
      *
      * @return a map of from task name to implementing class
      *         (String to Class).
      */
     public Hashtable getTaskDefinitions() {
         return ComponentHelper.getComponentHelper(this).getTaskDefinitions();
     }
 
     /**
      * Add a new datatype definition.
      * Attempting to override an existing definition with an
      * equivalent one (i.e. with the same classname) results in
      * a verbose log message. Attempting to override an existing definition
      * with a different one results in a warning log message, but the
      * definition is changed.
      *
      * @param typeName The name of the datatype.
      *                 Must not be <code>null</code>.
      * @param typeClass The full name of the class implementing the datatype.
      *                  Must not be <code>null</code>.
      */
     public void addDataTypeDefinition(String typeName, Class typeClass) {
         ComponentHelper.getComponentHelper(this).addDataTypeDefinition(typeName,
                 typeClass);
     }
 
     /**
      * Return the current datatype definition hashtable. The returned
      * hashtable is &quot;live&quot; and so should not be modified.
      *
      * @return a map of from datatype name to implementing class
      *         (String to Class).
      */
     public Hashtable getDataTypeDefinitions() {
         return ComponentHelper.getComponentHelper(this).getDataTypeDefinitions();
     }
 
     /**
      * Add a <em>new</em> target to the project.
      *
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project
      *
      * @see Project#addOrReplaceTarget(Target)
      */
     public void addTarget(Target target) throws BuildException {
         addTarget(target.getName(), target);
     }
 
     /**
      * Add a <em>new</em> target to the project.
      *
      * @param targetName The name to use for the target.
      *             Must not be <code>null</code>.
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project.
      *
      * @see Project#addOrReplaceTarget(String, Target)
      */
      public void addTarget(String targetName, Target target)
          throws BuildException {
          if (targets.get(targetName) != null) {
              throw new BuildException("Duplicate target: `" + targetName + "'");
          }
          addOrReplaceTarget(targetName, target);
      }
 
     /**
      * Add a target to the project, or replaces one with the same
      * name.
      *
      * @param target The target to be added or replaced in the project.
      *               Must not be <code>null</code>.
      */
     public void addOrReplaceTarget(Target target) {
         addOrReplaceTarget(target.getName(), target);
     }
 
     /**
      * Add a target to the project, or replaces one with the same
      * name.
      *
      * @param targetName The name to use for the target.
      *                   Must not be <code>null</code>.
      * @param target The target to be added or replaced in the project.
      *               Must not be <code>null</code>.
      */
     public void addOrReplaceTarget(String targetName, Target target) {
         String msg = " +Target: " + targetName;
         log(msg, MSG_DEBUG);
         target.setProject(this);
         targets.put(targetName, target);
     }
 
     /**
      * Return the hashtable of targets. The returned hashtable
      * is &quot;live&quot; and so should not be modified.
      * @return a map from name to target (String to Target).
      */
     public Hashtable getTargets() {
         return targets;
     }
 
     /**
      * Create a new instance of a task, adding it to a list of
      * created tasks for later invalidation. This causes all tasks
      * to be remembered until the containing project is removed
      * @param taskType The name of the task to create an instance of.
      *                 Must not be <code>null</code>.
      *
      * @return an instance of the specified task, or <code>null</code> if
      *         the task name is not recognised.
      *
      * @exception BuildException if the task name is recognised but task
      *                           creation fails.
      */
     public Task createTask(String taskType) throws BuildException {
         return ComponentHelper.getComponentHelper(this).createTask(taskType);
     }
 
     /**
      * Create a new instance of a data type.
      *
      * @param typeName The name of the data type to create an instance of.
      *                 Must not be <code>null</code>.
      *
      * @return an instance of the specified data type, or <code>null</code> if
      *         the data type name is not recognised.
      *
      * @exception BuildException if the data type name is recognised but
      *                           instance creation fails.
      */
     public Object createDataType(String typeName) throws BuildException {
         return ComponentHelper.getComponentHelper(this).createDataType(typeName);
     }
 
     /**
      * Set the Executor instance for this Project.
      * @param e the Executor to use.
      */
     public void setExecutor(Executor e) {
         addReference("ant.executor", e);
     }
 
     /**
      * Get this Project's Executor (setting it if necessary).
      * @return an Executor instance.
      */
     public Executor getExecutor() {
         Object o = getReference("ant.executor");
         if (o == null) {
             String classname = getProperty("ant.executor.class");
             if (classname == null) {
                 classname = DefaultExecutor.class.getName();
             }
             log("Attempting to create object of type " + classname, MSG_DEBUG);
             try {
                 o = Class.forName(classname, true, coreLoader).newInstance();
             } catch (ClassNotFoundException seaEnEfEx) {
                 //try the current classloader
                 try {
                     o = Class.forName(classname).newInstance();
                 } catch (Exception ex) {
                     log(ex.toString(), MSG_ERR);
                 }
             } catch (Exception ex) {
                 log(ex.toString(), MSG_ERR);
             }
             if (o == null) {
                 throw new BuildException(
                     "Unable to obtain a Target Executor instance.");
             }
             setExecutor((Executor) o);
         }
         return (Executor) o;
     }
 
     /**
      * Execute the specified sequence of targets, and the targets
      * they depend on.
      *
      * @param names A vector of target name strings to execute.
      *              Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed.
      */
     public void executeTargets(Vector names) throws BuildException {
         getExecutor().executeTargets(this,
             (String[]) (names.toArray(new String[names.size()])));
     }
 
     /**
      * Demultiplex output so that each task receives the appropriate
      * messages. If the current thread is not currently executing a task,
      * the message is logged directly.
      *
      * @param output Message to handle. Should not be <code>null</code>.
      * @param isWarning Whether the text represents an warning (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxOutput(String output, boolean isWarning) {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             log(output, isWarning ? MSG_WARN : MSG_INFO);
         } else {
             if (isWarning) {
                 task.handleErrorOutput(output);
             } else {
                 task.handleOutput(output);
             }
         }
     }
 
     /**
      * Read data from the default input stream. If no default has been
      * specified, System.in is used.
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read.
      *
      * @return the number of bytes read.
      *
      * @exception IOException if the data cannot be read.
      * @since Ant 1.6
      */
     public int defaultInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (defaultInputStream != null) {
             System.out.flush();
             return defaultInputStream.read(buffer, offset, length);
         } else {
             throw new EOFException("No input provided for project");
         }
     }
 
     /**
      * Demux an input request to the correct task.
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read.
      *
      * @return the number of bytes read.
      *
      * @exception IOException if the data cannot be read.
      * @since Ant 1.6
      */
     public int demuxInput(byte[] buffer, int offset, int length)
         throws IOException {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             return defaultInput(buffer, offset, length);
         } else {
             return task.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Demultiplex flush operations so that each task receives the appropriate
      * messages. If the current thread is not currently executing a task,
      * the message is logged directly.
      *
      * @since Ant 1.5.2
      *
      * @param output Message to handle. Should not be <code>null</code>.
      * @param isError Whether the text represents an error (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxFlush(String output, boolean isError) {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             fireMessageLogged(this, output, isError ? MSG_ERR : MSG_INFO);
         } else {
             if (isError) {
                 task.handleErrorFlush(output);
             } else {
                 task.handleFlush(output);
             }
         }
     }
 
     /**
      * Execute the specified target and any targets it depends on.
      *
      * @param targetName The name of the target to execute.
      *                   Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed.
      */
     public void executeTarget(String targetName) throws BuildException {
 
         // sanity check ourselves, if we've been asked to build nothing
         // then we should complain
 
         if (targetName == null) {
             String msg = "No target specified";
             throw new BuildException(msg);
         }
 
         // Sort and run the dependency tree.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
         executeSortedTargets(topoSort(targetName, targets, false));
     }
 
     /**
      * Execute a <code>Vector</code> of sorted targets.
      * @param sortedTargets   the aforementioned <code>Vector</code>.
      * @throws BuildException on error.
      */
     public void executeSortedTargets(Vector sortedTargets)
         throws BuildException {
         Set succeededTargets = new HashSet();
         BuildException buildException = null; // first build exception
         for (Enumeration iter = sortedTargets.elements();
              iter.hasMoreElements();) {
             Target curtarget = (Target) iter.nextElement();
             boolean canExecute = true;
             for (Enumeration depIter = curtarget.getDependencies();
                  depIter.hasMoreElements();) {
                 String dependencyName = ((String) depIter.nextElement());
                 if (!succeededTargets.contains(dependencyName)) {
                     canExecute = false;
                     log(curtarget,
                         "Cannot execute '" + curtarget.getName() + "' - '"
                         + dependencyName + "' failed or was not executed.",
                         MSG_ERR);
                     break;
                 }
             }
             if (canExecute) {
                 Throwable thrownException = null;
                 try {
                     curtarget.performTasks();
                     succeededTargets.add(curtarget.getName());
                 } catch (RuntimeException ex) {
                     if (!(keepGoingMode)) {
                         throw ex; // throw further
                     }
                     thrownException = ex;
                 } catch (Throwable ex) {
                     if (!(keepGoingMode)) {
                         throw new BuildException(ex);
                     }
                     thrownException = ex;
                 }
                 if (thrownException != null) {
                     if (thrownException instanceof BuildException) {
                         log(curtarget,
                             "Target '" + curtarget.getName()
                             + "' failed with message '"
                             + thrownException.getMessage() + "'.", MSG_ERR);
                         // only the first build exception is reported
                         if (buildException == null) {
                             buildException = (BuildException) thrownException;
                         }
                     } else {
                         log(curtarget,
                             "Target '" + curtarget.getName()
                             + "' failed with message '"
                             + thrownException.getMessage() + "'.", MSG_ERR);
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index ed0afea86..c3f30603b 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,750 +1,746 @@
 /*
  * Copyright  2000-2005 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.reflect.Method;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Vector;
 import java.util.Set;
 import java.util.HashSet;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.BuildListener;
 import org.apache.tools.ant.DefaultLogger;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectComponent;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Target;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Build a sub-project.
  *
  *  <pre>
  *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
  *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
  *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
  *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
  *    &lt;/ant&gt;</span>
  *  &lt;/target&gt;</span>
  *
  *  &lt;target name=&quot;bar&quot; depends=&quot;init&quot;&gt;
  *    &lt;echo message=&quot;prop is ${property1} ${foo}&quot; /&gt;
  *  &lt;/target&gt;
  * </pre>
  *
  *
  * @since Ant 1.1
  *
  * @ant.task category="control"
  */
 public class Ant extends Task {
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /** the basedir where is executed the build file */
     private File dir = null;
 
     /**
      * the build.xml file (can be absolute) in this case dir will be
      * ignored
      */
     private String antFile = null;
 
     /** the output */
     private String output = null;
 
     /** should we inherit properties from the parent ? */
     private boolean inheritAll = true;
 
     /** should we inherit references from the parent ? */
     private boolean inheritRefs = false;
 
     /** the properties to pass to the new project */
     private Vector properties = new Vector();
 
     /** the references to pass to the new project */
     private Vector references = new Vector();
 
     /** the temporary project created to run the build file */
     private Project newProject;
 
     /** The stream to which output is to be written. */
     private PrintStream out = null;
 
     /** the sets of properties to pass to the new project */
     private Vector propertySets = new Vector();
 
     /** the targets to call on the new project */
     private Vector targets = new Vector();
 
     /** whether the target attribute was specified **/
     private boolean targetAttributeSet = false;
 
     /**
      * simple constructor
      */
     public Ant() {
         //default
     }
 
     /**
      * create a task bound to its creator
      * @param owner owning task
      */
     public Ant(Task owner) {
         bindToOwner(owner);
     }
 
 
 
 
     /**
      * If true, pass all properties to the new Ant project.
      * Defaults to true.
      * @param value if true pass all properties to the new Ant project.
      */
     public void setInheritAll(boolean value) {
         inheritAll = value;
     }
 
     /**
      * If true, pass all references to the new Ant project.
      * Defaults to false.
      * @param value if true, pass all references to the new Ant project
      */
     public void setInheritRefs(boolean value) {
         inheritRefs = value;
     }
 
     /**
      * Creates a Project instance for the project to call.
      */
     public void init() {
-        newProject = new Project();
-        newProject.setDefaultInputStream(getProject().getDefaultInputStream());
+        newProject = getProject().createSubProject();
         newProject.setJavaVersionProperty();
     }
 
     /**
      * Called in execute or createProperty if newProject is null.
      *
      * <p>This can happen if the same instance of this task is run
      * twice as newProject is set to null at the end of execute (to
      * save memory and help the GC).</p>
      * <p>calls init() again</p>
      *
      */
     private void reinit() {
         init();
     }
 
     /**
      * Attaches the build listeners of the current project to the new
      * project, configures a possible logfile, transfers task and
      * data-type definitions, transfers properties (either all or just
      * the ones specified as user properties to the current project,
      * depending on inheritall), transfers the input handler.
      */
     private void initializeProject() {
         newProject.setInputHandler(getProject().getInputHandler());
 
         Iterator iter = getBuildListeners();
         while (iter.hasNext()) {
             newProject.addBuildListener((BuildListener) iter.next());
         }
 
         if (output != null) {
             File outfile = null;
             if (dir != null) {
                 outfile = FILE_UTILS.resolveFile(dir, output);
             } else {
                 outfile = getProject().resolveFile(output);
             }
             try {
                 out = new PrintStream(new FileOutputStream(outfile));
                 DefaultLogger logger = new DefaultLogger();
                 logger.setMessageOutputLevel(Project.MSG_INFO);
                 logger.setOutputPrintStream(out);
                 logger.setErrorPrintStream(out);
                 newProject.addBuildListener(logger);
             } catch (IOException ex) {
                 log("Ant: Can't set output to " + output);
             }
         }
-
-        getProject().initSubProject(newProject);
-
         // set user-defined properties
         getProject().copyUserProperties(newProject);
 
         if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            newProject.setSystemProperties();
 
         } else {
             // set all properties from calling project
             addAlmostAll(getProject().getProperties());
         }
 
         Enumeration e = propertySets.elements();
         while (e.hasMoreElements()) {
             PropertySet ps = (PropertySet) e.nextElement();
             addAlmostAll(ps.getProperties());
         }
     }
 
     /**
      * @see Task#handleOutput(String)
      * @since Ant 1.5
      */
     public void handleOutput(String outputToHandle) {
         if (newProject != null) {
             newProject.demuxOutput(outputToHandle, false);
         } else {
             super.handleOutput(outputToHandle);
         }
     }
 
     /**
      * @see Task#handleInput(byte[], int, int)
      * @since Ant 1.6
      */
     public int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (newProject != null) {
             return newProject.demuxInput(buffer, offset, length);
         }
         return super.handleInput(buffer, offset, length);
     }
 
     /**
      * @see Task#handleFlush(String)
      * @since Ant 1.5.2
      */
     public void handleFlush(String toFlush) {
         if (newProject != null) {
             newProject.demuxFlush(toFlush, false);
         } else {
             super.handleFlush(toFlush);
         }
     }
 
     /**
      * @see Task#handleErrorOutput(String)
      * @since Ant 1.5
      */
     public void handleErrorOutput(String errorOutputToHandle) {
         if (newProject != null) {
             newProject.demuxOutput(errorOutputToHandle, true);
         } else {
             super.handleErrorOutput(errorOutputToHandle);
         }
     }
 
     /**
      * @see Task#handleErrorFlush(String)
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String errorOutputToFlush) {
         if (newProject != null) {
             newProject.demuxFlush(errorOutputToFlush, true);
         } else {
             super.handleErrorFlush(errorOutputToFlush);
         }
     }
 
     /**
      * Do the execution.
      * @throws BuildException if a target tries to call itself;
      * probably also if a BuildException is thrown by the new project.
      */
     public void execute() throws BuildException {
         File savedDir = dir;
         String savedAntFile = antFile;
         Vector locals = new Vector(targets);
         try {
             if (newProject == null) {
                 reinit();
             }
 
             if ((dir == null) && (inheritAll)) {
                 dir = getProject().getBaseDir();
             }
 
             initializeProject();
 
             if (dir != null) {
                 newProject.setBaseDir(dir);
                 if (savedDir != null) {
                     // has been set explicitly
                     newProject.setInheritedProperty("basedir" ,
                                                     dir.getAbsolutePath());
                 }
             } else {
                 dir = getProject().getBaseDir();
             }
 
             overrideProperties();
 
             if (antFile == null) {
                 antFile = "build.xml";
             }
 
             File file = FILE_UTILS.resolveFile(dir, antFile);
             antFile = file.getAbsolutePath();
 
             log("calling target(s) "
                 + ((locals.size() > 0) ? locals.toString() : "[default]")
                 + " in build file " + antFile, Project.MSG_VERBOSE);
             newProject.setUserProperty("ant.file" , antFile);
 
             String thisAntFile = getProject().getProperty("ant.file");
             // Are we trying to call the target in which we are defined (or
             // the build file if this is a top level task)?
             if (thisAntFile != null
                 && file.equals(getProject().resolveFile(thisAntFile))
                 && getOwningTarget() != null) {
 
                 if (getOwningTarget().getName().equals("")) {
                     if (getTaskName().equals("antcall")) {
                         throw new BuildException("antcall must not be used at"
                                                  + " the top level.");
                     }
                     throw new BuildException(getTaskName() + " task at the"
                                 + " top level must not invoke"
                                 + " its own build file.");
                 }
             }
 
             try {
                 ProjectHelper.configureProject(newProject, file);
             } catch (BuildException ex) {
                 throw ProjectHelper.addLocationToBuildException(
                     ex, getLocation());
             }
 
             if (locals.size() == 0) {
                 String defaultTarget = newProject.getDefaultTarget();
                 if (defaultTarget != null) {
                     locals.add(defaultTarget);
                 }
             }
 
             if (newProject.getProperty("ant.file")
                 .equals(getProject().getProperty("ant.file"))
                 && getOwningTarget() != null) {
 
                 String owningTargetName = getOwningTarget().getName();
 
                 if (locals.contains(owningTargetName)) {
                     throw new BuildException(getTaskName() + " task calling "
                                              + "its own parent target.");
                 }
                 boolean circular = false;
                 for (Iterator it = locals.iterator(); 
                      !circular && it.hasNext();) {
                     Target other = 
                         (Target) (getProject().getTargets().get(it.next()));
                     circular |= (other != null
                                  && other.dependsOn(owningTargetName));
                 }
                 if (circular) {
                     throw new BuildException(getTaskName()
                                              + " task calling a target"
                                              + " that depends on"
                                              + " its parent target \'"
                                              + owningTargetName
                                              + "\'.");
                 }
             }
 
             addReferences();
 
             if (locals.size() > 0 && !(locals.size() == 1 
                                        && locals.get(0) == "")) {
                 BuildException be = null;
                 try {
                     log("Entering " + antFile + "...", Project.MSG_VERBOSE);
                     newProject.fireSubBuildStarted();
                     newProject.executeTargets(locals);
                 } catch (BuildException ex) {
                     be = ProjectHelper
                         .addLocationToBuildException(ex, getLocation());
                     throw be;
                 } finally {
                     log("Exiting " + antFile + ".", Project.MSG_VERBOSE);
                     newProject.fireSubBuildFinished(be);
                 }
             }
         } finally {
             // help the gc
             newProject = null;
             Enumeration e = properties.elements();
             while (e.hasMoreElements()) {
                 Property p = (Property) e.nextElement();
                 p.setProject(null);
             }
 
             if (output != null && out != null) {
                 try {
                     out.close();
                 } catch (final Exception ex) {
                     //ignore
                 }
             }
             dir = savedDir;
             antFile = savedAntFile;
         }
     }
 
     /**
      * Override the properties in the new project with the one
      * explicitly defined as nested elements here.
      * @throws BuildException under unknown circumstances.
      */
     private void overrideProperties() throws BuildException {
         // remove duplicate properties - last property wins
         // Needed for backward compatibility
         Set set = new HashSet();
         for (int i = properties.size() - 1; i >= 0; --i) {
             Property p = (Property) properties.get(i);
             if (p.getName() != null && !p.getName().equals("")) {
                 if (set.contains(p.getName())) {
                     properties.remove(i);
                 } else {
                     set.add(p.getName());
                 }
             }
         }
         Enumeration e = properties.elements();
         while (e.hasMoreElements()) {
             Property p = (Property) e.nextElement();
             p.setProject(newProject);
             p.execute();
         }
         getProject().copyInheritedProperties(newProject);
     }
 
     /**
      * Add the references explicitly defined as nested elements to the
      * new project.  Also copy over all references that don't override
      * existing references in the new project if inheritrefs has been
      * requested.
      * @throws BuildException if a reference does not have a refid.
      */
     private void addReferences() throws BuildException {
         Hashtable thisReferences
             = (Hashtable) getProject().getReferences().clone();
         Hashtable newReferences = newProject.getReferences();
         Enumeration e;
         if (references.size() > 0) {
             for (e = references.elements(); e.hasMoreElements();) {
                 Reference ref = (Reference) e.nextElement();
                 String refid = ref.getRefId();
                 if (refid == null) {
                     throw new BuildException("the refid attribute is required"
                                              + " for reference elements");
                 }
                 if (!thisReferences.containsKey(refid)) {
                     log("Parent project doesn't contain any reference '"
                         + refid + "'",
                         Project.MSG_WARN);
                     continue;
                 }
 
                 thisReferences.remove(refid);
                 String toRefid = ref.getToRefid();
                 if (toRefid == null) {
                     toRefid = refid;
                 }
                 copyReference(refid, toRefid);
             }
         }
 
         // Now add all references that are not defined in the
         // subproject, if inheritRefs is true
         if (inheritRefs) {
             for (e = thisReferences.keys(); e.hasMoreElements();) {
                 String key = (String) e.nextElement();
                 if (newReferences.containsKey(key)) {
                     continue;
                 }
                 copyReference(key, key);
             }
         }
     }
 
     /**
      * Try to clone and reconfigure the object referenced by oldkey in
      * the parent project and add it to the new project with the key newkey.
      *
      * <p>If we cannot clone it, copy the referenced object itself and
      * keep our fingers crossed.</p>
      * @param oldKey the reference id in the current project.
      * @param newKey the reference id in the new project.
      */
     private void copyReference(String oldKey, String newKey) {
         Object orig = getProject().getReference(oldKey);
         if (orig == null) {
             log("No object referenced by " + oldKey + ". Can't copy to "
                 + newKey,
                 Project.MSG_WARN);
             return;
         }
 
         Class c = orig.getClass();
         Object copy = orig;
         try {
             Method cloneM = c.getMethod("clone", new Class[0]);
             if (cloneM != null) {
                 copy = cloneM.invoke(orig, new Object[0]);
                 log("Adding clone of reference " + oldKey, Project.MSG_DEBUG);
             }
         } catch (Exception e) {
             // not Clonable
         }
 
 
         if (copy instanceof ProjectComponent) {
             ((ProjectComponent) copy).setProject(newProject);
         } else {
             try {
                 Method setProjectM =
                     c.getMethod("setProject", new Class[] {Project.class});
                 if (setProjectM != null) {
                     setProjectM.invoke(copy, new Object[] {newProject});
                 }
             } catch (NoSuchMethodException e) {
                 // ignore this if the class being referenced does not have
                 // a set project method.
             } catch (Exception e2) {
                 String msg = "Error setting new project instance for "
                     + "reference with id " + oldKey;
                 throw new BuildException(msg, e2, getLocation());
             }
         }
         newProject.addReference(newKey, copy);
     }
 
     /**
      * Copies all properties from the given table to the new project -
      * omitting those that have already been set in the new project as
      * well as properties named basedir or ant.file.
      * @param props properties <code>Hashtable</code> to copy to the
      * new project.
      * @since Ant 1.6
      */
     private void addAlmostAll(Hashtable props) {
         Enumeration e = props.keys();
         while (e.hasMoreElements()) {
             String key = e.nextElement().toString();
             if ("basedir".equals(key) || "ant.file".equals(key)) {
                 // basedir and ant.file get special treatment in execute()
                 continue;
             }
 
             String value = props.get(key).toString();
             // don't re-set user properties, avoid the warning message
             if (newProject.getProperty(key) == null) {
                 // no user property
                 newProject.setNewProperty(key, value);
             }
         }
     }
 
     /**
      * The directory to use as a base directory for the new Ant project.
      * Defaults to the current project's basedir, unless inheritall
      * has been set to false, in which case it doesn't have a default
      * value. This will override the basedir setting of the called project.
      * @param dir new directory as <code>File</code>.
      */
     public void setDir(File dir) {
         this.dir = dir;
     }
 
     /**
      * The build file to use. Defaults to "build.xml". This file is expected
      * to be a filename relative to the dir attribute given.
      * @param antFile the <code>String</code> build file name.
      */
     public void setAntfile(String antFile) {
         // @note: it is a string and not a file to handle relative/absolute
         // otherwise a relative file will be resolved based on the current
         // basedir.
         this.antFile = antFile;
     }
 
     /**
      * The target of the new Ant project to execute.
      * Defaults to the new project's default target.
      * @param targetToAdd the name of the target to invoke.
      */
     public void setTarget(String targetToAdd) {
         if (targetToAdd.equals("")) {
             throw new BuildException("target attribute must not be empty");
         }
         targets.add(targetToAdd);
         targetAttributeSet = true;
     }
 
     /**
      * Set the filename to write the output to. This is relative to the value
      * of the dir attribute if it has been set or to the base directory of the
      * current project otherwise.
      * @param outputFile the name of the file to which the output should go.
      */
     public void setOutput(String outputFile) {
         this.output = outputFile;
     }
 
     /**
      * Property to pass to the new project.
      * The property is passed as a 'user property'.
      * @return the created <code>Property</code> object.
      */
     public Property createProperty() {
         if (newProject == null) {
             reinit();
         }
         Property p = new Property(true, getProject());
         p.setProject(newProject);
         p.setTaskName("property");
         properties.addElement(p);
         return p;
     }
 
     /**
      * Add a Reference element identifying a data type to carry
      * over to the new project.
      * @param ref <code>Reference</code> to add.
      */
     public void addReference(Reference ref) {
         references.addElement(ref);
     }
 
     /**
      * Add a target to this Ant invocation.
      * @param t the <code>TargetElement</code> to add.
      * @since Ant 1.6.3
      */
     public void addConfiguredTarget(TargetElement t) {
         if (targetAttributeSet) {
             throw new BuildException(
                 "nested target is incompatible with the target attribute");
         }
         String name = t.getName();
         if (name.equals("")) {
             throw new BuildException("target name must not be empty");
         }
         targets.add(name);
     }
 
     /**
      * Add a set of properties to pass to the new project.
      *
      * @param ps <code>PropertySet</code> to add.
      * @since Ant 1.6
      */
     public void addPropertyset(PropertySet ps) {
         propertySets.addElement(ps);
     }
 
     /**
      * @since Ant 1.6.2
      */
     private Iterator getBuildListeners() {
         return getProject().getBuildListeners().iterator();
     }
 
     /**
      * Helper class that implements the nested &lt;reference&gt;
      * element of &lt;ant&gt; and &lt;antcall&gt;.
      */
     public static class Reference
         extends org.apache.tools.ant.types.Reference {
 
         /** Creates a reference to be configured by Ant. */
         public Reference() {
                 super();
         }
 
         private String targetid = null;
 
         /**
          * Set the id that this reference to be stored under in the
          * new project.
          *
          * @param targetid the id under which this reference will be passed to
          *        the new project. */
         public void setToRefid(String targetid) {
             this.targetid = targetid;
         }
 
         /**
          * Get the id under which this reference will be stored in the new
          * project.
          *
          * @return the id of the reference in the new project.
          */
         public String getToRefid() {
             return targetid;
         }
     }
 
     /**
      * Helper class that implements the nested &lt;target&gt;
      * element of &lt;ant&gt; and &lt;antcall&gt;.
      * @since Ant 1.6.3
      */
     public static class TargetElement {
         private String name;
 
         /**
          * Default constructor.
          */
         public TargetElement() {
                 //default
         }
 
         /**
          * Set the name of this TargetElement.
          * @param name   the <code>String</code> target name.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * Get the name of this TargetElement.
          * @return <code>String</code>.
          */
         public String getName() {
             return name;
         }
     }
 }
