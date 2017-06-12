diff --git a/src/main/org/apache/tools/ant/DemuxOutputStream.java b/src/main/org/apache/tools/ant/DemuxOutputStream.java
index 7da083b09..8694ae4be 100644
--- a/src/main/org/apache/tools/ant/DemuxOutputStream.java
+++ b/src/main/org/apache/tools/ant/DemuxOutputStream.java
@@ -1,223 +1,236 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.Hashtable;
 
 /**
  * Logs content written by a thread and forwards the buffers onto the
  * project object which will forward the content to the appropriate
  * task.
  *
  * @since 1.4
  * @author Conor MacNeill
  */
 public class DemuxOutputStream extends OutputStream {
 
     /**
      * A data class to store information about a buffer. Such information
      * is stored on a per-thread basis.
      */
     private static class BufferInfo {
         /**
          * The per-thread output stream.
          */
         private ByteArrayOutputStream buffer;
         
         /** 
          * Whether or not the next line-terminator should be skipped in terms
          * of processing the buffer. Used to avoid \r\n invoking
          * processBuffer twice.
          */
          private boolean skip = false;
     }
     
     /** Maximum buffer size. */
     private static final int MAX_SIZE = 1024;
     
     /** Mapping from thread to buffer (Thread to BufferInfo). */
     private Hashtable buffers = new Hashtable();
 
     /**
      * The project to send output to.
      */
     private Project project;
 
     /**
      * Whether or not this stream represents an error stream.
      */
     private boolean isErrorStream;
     
     /**
      * Creates a new instance of this class.
      *
      * @param project The project instance for which output is being 
      *                demultiplexed. Must not be <code>null</code>.
      * @param isErrorStream <code>true</code> if this is the error string, 
      *                      otherwise a normal output stream. This is 
      *                      passed to the project so it knows
      *                      which stream it is receiving.
      */
     public DemuxOutputStream(Project project, boolean isErrorStream) {
         this.project = project;
         this.isErrorStream = isErrorStream;
     }
 
     /**
      * Returns the buffer associated with the current thread.
      * 
      * @return a BufferInfo for the current thread to write data to
      */
     private BufferInfo getBufferInfo() {
         Thread current = Thread.currentThread();
         BufferInfo bufferInfo = (BufferInfo) buffers.get(current);
         if (bufferInfo == null) {
             bufferInfo = new BufferInfo();
             bufferInfo.buffer = new ByteArrayOutputStream();
             bufferInfo.skip = false;
             buffers.put(current, bufferInfo);
         }
         return bufferInfo;
     }
 
     /**
      * Resets the buffer for the current thread.
      */
     private void resetBufferInfo() {    
         Thread current = Thread.currentThread();
         BufferInfo bufferInfo = (BufferInfo) buffers.get(current);
         try {
             bufferInfo.buffer.close();
         } catch (IOException e) {
             // Shouldn't happen
         }
         bufferInfo.buffer = new ByteArrayOutputStream();
         bufferInfo.skip = false;
     }
     
     /**
      * Removes the buffer for the current thread.
      */
     private void removeBuffer() {    
         Thread current = Thread.currentThread();
         buffers.remove (current);
     }
 
     /**
      * Writes the data to the buffer and flushes the buffer if a line
      * separator is detected or if the buffer has reached its maximum size.
      *
      * @param cc data to log (byte).
      * @exception IOException if the data cannot be written to the stream
      */
     public void write(int cc) throws IOException {
         final byte c = (byte) cc;
 
         BufferInfo bufferInfo = getBufferInfo();
         if ((c == '\n') || (c == '\r')) {
             if (!bufferInfo.skip) {
                 processBuffer(bufferInfo.buffer);
             }
         } else {
             bufferInfo.buffer.write(cc);
             if (bufferInfo.buffer.size() > MAX_SIZE) {
                 processBuffer(bufferInfo.buffer);
             }
         }
         bufferInfo.skip = (c == '\r');
     }
 
     /**
      * Converts the buffer to a string and sends it to the project.
      *
      * @param buffer the ByteArrayOutputStream used to collect the output
      * until a line separator is seen.
      * 
      * @see Project#demuxOutput(String,boolean)
      */
     protected void processBuffer(ByteArrayOutputStream buffer) {
+        processBuffer(buffer, true);
+    }
+
+    /**
+     * Converts the buffer to a string and sends it to the project.
+     *
+     * @param buffer the ByteArrayOutputStream used to collect the output
+     * until a line separator is seen.
+     * 
+     * @see Project#demuxOutput(String,boolean)
+     */
+    protected void processBuffer(ByteArrayOutputStream buffer, 
+                                 boolean terminated) {
         String output = buffer.toString();
-        project.demuxOutput(output, isErrorStream);
+        project.demuxOutput(output, isErrorStream, terminated);
         resetBufferInfo();
     }
 
     /**
      * Equivalent to flushing the stream.
      *
      * @exception IOException if there is a problem closing the stream.
      * 
      * @see #flush
      */
     public void close() throws IOException {
         flush();
         removeBuffer();
     }
 
     /**
      * Writes all remaining data in the buffer associated
      * with the current thread to the project.
      *
      * @exception IOException if there is a problem flushing the stream.
      */
     public void flush() throws IOException {
         BufferInfo bufferInfo = getBufferInfo();
         if (bufferInfo.buffer.size() > 0) {
-            processBuffer(bufferInfo.buffer);
+            processBuffer(bufferInfo.buffer, false);
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 719378daa..5e9cb6497 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -231,1912 +231,1929 @@ public class Project {
 
     /**
      * Initialises the project.
      *
      * This involves setting the default task definitions and loading the
      * system properties.
      *
      * @exception BuildException if the default task list cannot be loaded
      */
     public void init() throws BuildException {
         setJavaVersionProperty();
 
         String defs = "/org/apache/tools/ant/taskdefs/defaults.properties";
 
         try {
             Properties props = new Properties();
             InputStream in = this.getClass().getResourceAsStream(defs);
             if (in == null) {
                 throw new BuildException("Can't load default task list");
             }
             props.load(in);
             in.close();
             ((AntTaskTable)taskClassDefinitions).addDefinitions( props );
 
 
         } catch (IOException ioe) {
             throw new BuildException("Can't load default task list");
         }
 
         String dataDefs = "/org/apache/tools/ant/types/defaults.properties";
 
         try {
             Properties props = new Properties();
             InputStream in = this.getClass().getResourceAsStream(dataDefs);
             if (in == null) {
                 throw new BuildException("Can't load default datatype list");
             }
             props.load(in);
             in.close();
 
             ((AntTaskTable)dataClassDefinitions).addDefinitions(props);
 
 
         } catch (IOException ioe) {
             throw new BuildException("Can't load default datatype list");
         }
 
         setSystemProperties();
     }
 
     private AntClassLoader createClassLoader() {
         AntClassLoader loader = null;
         if (!JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             try {
                 // 1.2+ - create advanced helper dynamically
                 Class loaderClass
                     = Class.forName("org.apache.tools.ant.loader.AntClassLoader2");
                 loader = (AntClassLoader) loaderClass.newInstance();
             } catch (Exception e) {
                     log("Unable to create Class Loader: "
                         + e.getMessage(), Project.MSG_DEBUG);
             }
         }
 
         if (loader == null) {
             loader = new AntClassLoader();
         }
 
         loader.setProject(this);
         return loader;
     }
 
     public AntClassLoader createClassLoader(Path path) {
         AntClassLoader loader = createClassLoader();
         loader.setClassPath(path);
         return loader;
     }
 
     /**
      * Sets the core classloader for the project. If a <code>null</code>
      * classloader is specified, the parent classloader should be used.
      *
      * @param coreLoader The classloader to use for the project.
      *                   May be <code>null</code>.
      */
     public void setCoreLoader(ClassLoader coreLoader) {
         this.coreLoader = coreLoader;
     }
 
     /**
      * Returns the core classloader to use for this project.
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
      * Adds a build listener to the list. This listener will
      * be notified of build events for this project.
      *
      * @param listener The listener to add to the list.
      *                 Must not be <code>null</code>.
      */
     public void addBuildListener(BuildListener listener) {
         listeners.addElement(listener);
     }
 
     /**
      * Removes a build listener from the list. This listener
      * will no longer be notified of build events for this project.
      *
      * @param listener The listener to remove from the list.
      *                 Should not be <code>null</code>.
      */
     public void removeBuildListener(BuildListener listener) {
         listeners.removeElement(listener);
     }
 
     /**
      * Returns a list of build listeners for the project.
      *
      * @return a list of build listeners for the project
      */
     public Vector getBuildListeners() {
         return (Vector) listeners.clone();
     }
 
     /**
      * Writes a message to the log with the default log level
      * of MSG_INFO
      * @param message The text to log. Should not be <code>null</code>.
      */
 
     public void log(String message) {
         log(message, MSG_INFO);
     }
 
     /**
      * Writes a project level message to the log with the given log level.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(String message, int msgLevel) {
         fireMessageLogged(this, message, msgLevel);
     }
 
     /**
      * Writes a task level message to the log with the given log level.
      * @param task The task to use in the log. Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(Task task, String message, int msgLevel) {
         fireMessageLogged(task, message, msgLevel);
     }
 
     /**
      * Writes a target level message to the log with the given log level.
      * @param target The target to use in the log.
      *               Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(Target target, String message, int msgLevel) {
         fireMessageLogged(target, message, msgLevel);
     }
 
     /**
      * Returns the set of global filters.
      *
      * @return the set of global filters
      */
     public FilterSet getGlobalFilterSet() {
         return globalFilterSet;
     }
 
     /**
      * Sets a property. Any existing property of the same name
      * is overwritten, unless it is a user property.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      */
     public synchronized void setProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).
                 setProperty(null, name, value, true);
     }
 
     /**
      * Sets a property if no value currently exists. If the property
      * exists already, a message is logged and the method returns with
      * no other effect.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @since 1.5
      */
     public synchronized void setNewProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setNewProperty( null, name, value);
     }
 
     /**
      * Sets a user property, which cannot be overwritten by
      * set/unset property calls. Any previous value is overwritten.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @see #setProperty(String,String)
      */
     public synchronized void setUserProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setUserProperty( null, name, value);
     }
 
     /**
      * Sets a user property, which cannot be overwritten by set/unset
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
     public synchronized void setInheritedProperty(String name, String value) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         ph.setInheritedProperty(null, name, value);
     }
 
     /**
      * Sets a property unless it is already defined as a user property
      * (in which case the method returns silently).
      *
      * @param name The name of the property.
      *             Must not be <code>null</code>.
      * @param value The property value. Must not be <code>null</code>.
      */
     private void setPropertyInternal(String name, String value) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         ph.setProperty(null, name, value, false );
     }
 
     /**
      * Returns the value of a property, if it is set.
      *
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
     public String getProperty(String name) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         return (String)ph.getProperty(null, name);
     }
 
     /**
      * Replaces ${} style constructions in the given value with the
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
      *                           property name, e.g. <code>${xxx</code>
      */
     public String replaceProperties(String value)
         throws BuildException
     {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         return ph.replaceProperties(null, value, null);
     }
 
     /**
      * Returns the value of a user property, if it is set.
      *
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
      public String getUserProperty(String name) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         return (String)ph.getUserProperty( null, name );
     }
 
     /**
      * Returns a copy of the properties table.
      * @return a hashtable containing all properties
      *         (including user properties).
      */
     public Hashtable getProperties() {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         return ph.getProperties();
     }
 
     /**
      * Returns a copy of the user property hashtable
      * @return a hashtable containing just the user properties
      */
     public Hashtable getUserProperties() {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         return ph.getUserProperties();
     }
 
     /**
      * Copies all user properties that have been set on the command
      * line or a GUI tool from this instance to the Project instance
      * given as the argument.
      *
      * <p>To copy all "user" properties, you will also have to call
      * {@link #copyInheritedProperties copyInheritedProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyUserProperties(Project other) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         ph.copyUserProperties(other);
     }
 
     /**
      * Copies all user properties that have not been set on the
      * command line or a GUI tool from this instance to the Project
      * instance given as the argument.
      *
      * <p>To copy all "user" properties, you will also have to call
      * {@link #copyUserProperties copyUserProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyInheritedProperties(Project other) {
         PropertyHelper ph=PropertyHelper.getPropertyHelper(this);
         ph.copyInheritedProperties(other);
     }
 
     /**
      * Sets the default target of the project.
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
      * Returns the name of the default target of the project.
      * @return name of the default target or
      *         <code>null</code> if no default has been set.
      */
     public String getDefaultTarget() {
         return defaultTarget;
     }
 
     /**
      * Sets the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      */
     public void setDefault(String defaultTarget) {
         this.defaultTarget = defaultTarget;
     }
 
     /**
      * Sets the name of the project, also setting the user
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
      * Returns the project name, if one has been set.
      *
      * @return the project name, or <code>null</code> if it hasn't been set.
      */
     public String getName() {
         return name;
     }
 
     /**
      * Sets the project description.
      *
      * @param description The description of the project.
      *                    May be <code>null</code>.
      */
     public void setDescription(String description) {
         this.description = description;
     }
 
     /**
      * Returns the project description, if one has been set.
      *
      * @return the project description, or <code>null</code> if it hasn't
      *         been set.
      */
     public String getDescription() {
         if( description== null ) {
             description=Description.getDescription(this);
         }
 
         return description;
     }
 
     /**
      * Adds a filter to the set of global filters.
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
      * Returns a hashtable of global filters, mapping tokens to values.
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
      * Sets the base directory for the project, checking that
      * the given filename exists and is a directory.
      *
      * @param baseD The project base directory.
      *              Must not be <code>null</code>.
      *
      * @exception BuildException if the directory if invalid
      */
     public void setBasedir(String baseD) throws BuildException {
         setBaseDir(new File(baseD));
     }
 
     /**
      * Sets the base directory for the project, checking that
      * the given file exists and is a directory.
      *
      * @param baseDir The project base directory.
      *                Must not be <code>null</code>.
      * @exception BuildException if the specified file doesn't exist or
      *                           isn't a directory
      */
     public void setBaseDir(File baseDir) throws BuildException {
         baseDir = fileUtils.normalize(baseDir.getAbsolutePath());
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
      * Returns the base directory of the project as a file object.
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
      * Returns the version of Java this class is running under.
      * @return the version of Java as a String, e.g. "1.1"
      * @see org.apache.tools.ant.util.JavaEnvUtils#getJavaVersion
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static String getJavaVersion() {
         return JavaEnvUtils.getJavaVersion();
     }
 
     /**
      * Sets the <code>ant.java.version</code> property and tests for
      * unsupported JVM versions. If the version is supported,
      * verbose log messages are generated to record the Java version
      * and operating system name.
      *
      * @exception BuildException if this Java version is not supported
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
      * Adds all system properties which aren't already defined as
      * user properties to the project properties.
      */
     public void setSystemProperties() {
         Properties systemP = System.getProperties();
         Enumeration e = systemP.keys();
         while (e.hasMoreElements()) {
             Object name = e.nextElement();
             String value = systemP.get(name).toString();
             this.setPropertyInternal(name.toString(), value);
         }
     }
 
     /**
      * Adds a new task definition to the project.
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
         Class old = (Class) taskClassDefinitions.get(taskName);
         if (null != old) {
             if (old.equals(taskClass)) {
                 log("Ignoring override for task " + taskName
                     + ", it is already defined by the same class.",
                     MSG_VERBOSE);
                 return;
             } else {
                 int logLevel = MSG_WARN;
                 if (old.getName().equals(taskClass.getName())) {
                     ClassLoader oldLoader = old.getClassLoader();
                     ClassLoader newLoader = taskClass.getClassLoader();
                     // system classloader on older JDKs can be null
                     if (oldLoader != null
                         && newLoader != null
                         && oldLoader instanceof AntClassLoader
                         && newLoader instanceof AntClassLoader
                         && ((AntClassLoader) oldLoader).getClasspath()
                         .equals(((AntClassLoader) newLoader).getClasspath())
                         ) {
                         // same classname loaded from the same
                         // classpath components
                         logLevel = MSG_VERBOSE;
                     }
                 }
 
                 log("Trying to override old definition of task " + taskName,
                     logLevel);
                 invalidateCreatedTasks(taskName);
             }
         }
 
         String msg = " +User task: " + taskName + "     " + taskClass.getName();
         log(msg, MSG_DEBUG);
         checkTaskClass(taskClass);
         taskClassDefinitions.put(taskName, taskClass);
     }
 
     /**
      * Checks whether or not a class is suitable for serving as Ant task.
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
             taskClass.getConstructor(null);
             // don't have to check for public, since
             // getConstructor finds public constructors only.
         } catch (NoSuchMethodException e) {
             final String message = "No public no-arg constructor in "
                 + taskClass;
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         if (!Task.class.isAssignableFrom(taskClass)) {
             TaskAdapter.checkTaskClass(taskClass, this);
         }
     }
 
     /**
      * Returns the current task definition hashtable. The returned hashtable is
      * "live" and so should not be modified.
      *
      * @return a map of from task name to implementing class
      *         (String to Class).
      */
     public Hashtable getTaskDefinitions() {
         return taskClassDefinitions;
     }
 
     /**
      * Adds a new datatype definition.
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
         synchronized(dataClassDefinitions) {
             Class old = (Class) dataClassDefinitions.get(typeName);
             if (null != old) {
                 if (old.equals(typeClass)) {
                     log("Ignoring override for datatype " + typeName
                         + ", it is already defined by the same class.",
                         MSG_VERBOSE);
                     return;
                 } else {
                     log("Trying to override old definition of datatype "
                         + typeName, MSG_WARN);
                 }
             }
             dataClassDefinitions.put(typeName, typeClass);
         }
         String msg = " +User datatype: " + typeName + "     "
             + typeClass.getName();
         log(msg, MSG_DEBUG);
     }
 
     /**
      * Returns the current datatype definition hashtable. The returned
      * hashtable is "live" and so should not be modified.
      *
      * @return a map of from datatype name to implementing class
      *         (String to Class).
      */
     public Hashtable getDataTypeDefinitions() {
         return dataClassDefinitions;
     }
 
     /**
      * Adds a <em>new</em> target to the project.
      *
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project
      *
      * @see Project#addOrReplaceTarget
      */
     public void addTarget(Target target) throws BuildException {
         String name = target.getName();
         if (targets.get(name) != null) {
             throw new BuildException("Duplicate target: `" + name + "'");
         }
         addOrReplaceTarget(name, target);
     }
 
     /**
      * Adds a <em>new</em> target to the project.
      *
      * @param targetName The name to use for the target.
      *             Must not be <code>null</code>.
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project
      *
      * @see Project#addOrReplaceTarget
      */
      public void addTarget(String targetName, Target target)
          throws BuildException {
          if (targets.get(targetName) != null) {
              throw new BuildException("Duplicate target: `" + targetName + "'");
          }
          addOrReplaceTarget(targetName, target);
      }
 
     /**
      * Adds a target to the project, or replaces one with the same
      * name.
      *
      * @param target The target to be added or replaced in the project.
      *               Must not be <code>null</code>.
      */
     public void addOrReplaceTarget(Target target) {
         addOrReplaceTarget(target.getName(), target);
     }
 
     /**
      * Adds a target to the project, or replaces one with the same
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
      * Returns the hashtable of targets. The returned hashtable
      * is "live" and so should not be modified.
      * @return a map from name to target (String to Target).
      */
     public Hashtable getTargets() {
         return targets;
     }
 
     /**
      * Creates a new instance of a task, adding it to a list of
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
         Task task=createNewTask(taskType);
         if(task!=null) {
             addCreatedTask(taskType, task);
         }
         return task;
     }
 
     /**
      * Creates a new instance of a task. This task is not
      * cached in the createdTasks list.
      * @since ant1.6
      * @param taskType The name of the task to create an instance of.
      *                 Must not be <code>null</code>.
      *
      * @return an instance of the specified task, or <code>null</code> if
      *         the task name is not recognised.
      *
      * @exception BuildException if the task name is recognised but task
      *                           creation fails.
      */
     private Task createNewTask(String taskType) throws BuildException {
         Class c = (Class) taskClassDefinitions.get(taskType);
 
         if (c == null) {
             return null;
         }
 
         try {
             Object o = c.newInstance();
             Task task = null;
             if (o instanceof Task) {
                task = (Task) o;
             } else {
                 // "Generic" Bean - use the setter pattern
                 // and an Adapter
                 TaskAdapter taskA = new TaskAdapter();
                 taskA.setProxy(o);
                 task = taskA;
             }
             task.setProject(this);
             task.setTaskType(taskType);
 
             // set default value, can be changed by the user
             task.setTaskName(taskType);
 
             String msg = "   +Task: " + taskType;
             log (msg, MSG_DEBUG);
             return task;
         } catch (Throwable t) {
             String msg = "Could not create task of type: "
                  + taskType + " due to " + t;
             throw new BuildException(msg, t);
         }
     }
 
     /**
      * Keeps a record of all tasks that have been created so that they
      * can be invalidated if a new task definition overrides the current one.
      *
      * @param type The name of the type of task which has been created.
      *             Must not be <code>null</code>.
      *
      * @param task The freshly created task instance.
      *             Must not be <code>null</code>.
      */
     private void addCreatedTask(String type, Task task) {
         synchronized (createdTasks) {
             Vector v = (Vector) createdTasks.get(type);
             if (v == null) {
                 v = new Vector();
                 createdTasks.put(type, v);
             }
             v.addElement(WeakishReference.createReference(task));
         }
     }
 
     /**
      * Mark tasks as invalid which no longer are of the correct type
      * for a given taskname.
      *
      * @param type The name of the type of task to invalidate.
      *             Must not be <code>null</code>.
      */
     private void invalidateCreatedTasks(String type) {
         synchronized (createdTasks) {
             Vector v = (Vector) createdTasks.get(type);
             if (v != null) {
                 Enumeration enum = v.elements();
                 while (enum.hasMoreElements()) {
                     WeakishReference ref=
                             (WeakishReference) enum.nextElement();
                     Task t = (Task) ref.get();
                     //being a weak ref, it may be null by this point
                     if(t!=null) {
                         t.markInvalid();
                     }
                 }
                 v.removeAllElements();
                 createdTasks.remove(type);
             }
         }
     }
 
     /**
      * Creates a new instance of a data type.
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
         Class c = (Class) dataClassDefinitions.get(typeName);
 
         if (c == null) {
             return null;
         }
 
         try {
             java.lang.reflect.Constructor ctor = null;
             boolean noArg = false;
             // DataType can have a "no arg" constructor or take a single
             // Project argument.
             try {
                 ctor = c.getConstructor(new Class[0]);
                 noArg = true;
             } catch (NoSuchMethodException nse) {
                 ctor = c.getConstructor(new Class[] {Project.class});
                 noArg = false;
             }
 
             Object o = null;
             if (noArg) {
                  o = ctor.newInstance(new Object[0]);
             } else {
                  o = ctor.newInstance(new Object[] {this});
             }
             if (o instanceof ProjectComponent) {
                 ((ProjectComponent) o).setProject(this);
             }
             String msg = "   +DataType: " + typeName;
             log (msg, MSG_DEBUG);
             return o;
         } catch (java.lang.reflect.InvocationTargetException ite) {
             Throwable t = ite.getTargetException();
             String msg = "Could not create datatype of type: "
                  + typeName + " due to " + t;
             throw new BuildException(msg, t);
         } catch (Throwable t) {
             String msg = "Could not create datatype of type: "
                  + typeName + " due to " + t;
             throw new BuildException(msg, t);
         }
     }
 
     /**
      * Execute the specified sequence of targets, and the targets
      * they depend on.
      *
      * @param targetNames A vector of target name strings to execute.
      *                    Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed
      */
     public void executeTargets(Vector targetNames) throws BuildException {
 
         for (int i = 0; i < targetNames.size(); i++) {
             executeTarget((String) targetNames.elementAt(i));
         }
     }
 
     /**
      * Demultiplexes output so that each task receives the appropriate
      * messages. If the current thread is not currently executing a task,
      * the message is logged directly.
      *
      * @param line Message to handle. Should not be <code>null</code>.
      * @param isError Whether the text represents an error (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxOutput(String line, boolean isError) {
+        demuxOutput(line, isError, true);
+    }
+
+    /**
+     * Demultiplexes output so that each task receives the appropriate
+     * messages. If the current thread is not currently executing a task,
+     * the message is logged directly.
+     *
+     * @param line Message to handle. Should not be <code>null</code>.
+     * @param isError Whether the text represents an error (<code>true</code>)
+     *        or information (<code>false</code>).
+     * @param terminated true if this line should be terminated with an 
+     *        end-of-line marker
+     */
+    public void demuxOutput(String line, boolean isError, boolean terminated) {
         Task task = (Task) threadTasks.get(Thread.currentThread());
         if (task == null) {
             fireMessageLogged(this, line, isError ? MSG_ERR : MSG_INFO);
         } else {
             if (isError) {
-                task.handleErrorOutput(line);
+                task.handleErrorOutput(line, terminated);
             } else {
-                task.handleOutput(line);
+                task.handleOutput(line, terminated);
             }
         }
     }
 
+    
+    
     /**
      * Executes the specified target and any targets it depends on.
      *
      * @param targetName The name of the target to execute.
      *                   Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed
      */
     public void executeTarget(String targetName) throws BuildException {
 
         // sanity check ourselves, if we've been asked to build nothing
         // then we should complain
 
         if (targetName == null) {
             String msg = "No target specified";
             throw new BuildException(msg);
         }
 
         // Sort the dependency tree, and run everything from the
         // beginning until we hit our targetName.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
         Vector sortedTargets = topoSort(targetName, targets);
 
         int curidx = 0;
         Target curtarget;
 
         do {
             curtarget = (Target) sortedTargets.elementAt(curidx++);
             curtarget.performTasks();
         } while (!curtarget.getName().equals(targetName));
     }
 
     /**
      * Returns the canonical form of a filename.
      * <p>
      * If the specified file name is relative it is resolved
      * with respect to the given root directory.
      *
      * @param fileName The name of the file to resolve.
      *                 Must not be <code>null</code>.
      *
      * @param rootDir  The directory to resolve relative file names with
      *                 respect to. May be <code>null</code>, in which case
      *                 the current directory is used.
      *
      * @return the resolved File.
      *
      * @deprecated
      */
     public File resolveFile(String fileName, File rootDir) {
         return fileUtils.resolveFile(rootDir, fileName);
     }
 
     /**
      * Returns the canonical form of a filename.
      * <p>
      * If the specified file name is relative it is resolved
      * with respect to the project's base directory.
      *
      * @param fileName The name of the file to resolve.
      *                 Must not be <code>null</code>.
      *
      * @return the resolved File.
      *
      */
     public File resolveFile(String fileName) {
         return fileUtils.resolveFile(baseDir, fileName);
     }
 
     /**
      * Translates a path into its native (platform specific) format.
      * <p>
      * This method uses PathTokenizer to separate the input path
      * into its components. This handles DOS style paths in a relatively
      * sensible way. The file separators are then converted to their platform
      * specific versions.
      *
      * @param toProcess The path to be translated.
      *                  May be <code>null</code>.
      *
      * @return the native version of the specified path or
      *         an empty string if the path is <code>null</code> or empty.
      *
      * @see PathTokenizer
      */
     public static String translatePath(String toProcess) {
         if (toProcess == null || toProcess.length() == 0) {
             return "";
         }
 
         StringBuffer path = new StringBuffer(toProcess.length() + 50);
         PathTokenizer tokenizer = new PathTokenizer(toProcess);
         while (tokenizer.hasMoreTokens()) {
             String pathComponent = tokenizer.nextToken();
             pathComponent = pathComponent.replace('/', File.separatorChar);
             pathComponent = pathComponent.replace('\\', File.separatorChar);
             if (path.length() != 0) {
                 path.append(File.pathSeparatorChar);
             }
             path.append(pathComponent);
         }
 
         return path.toString();
     }
 
     /**
      * Convenience method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile)
           throws IOException {
         fileUtils.copyFile(sourceFile, destFile);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination
      * specifying if token filtering should be used.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used, if
      * source files may overwrite newer destination files, and if the
      * last modified time of the resulting file should be set to
      * that of the source file.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite, preserveLastModified);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile) throws IOException {
         fileUtils.copyFile(sourceFile, destFile);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination
      * specifying if token filtering should be used.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @exception IOException if the file cannot be copied.
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used, if
      * source files may overwrite newer destination files, and if the
      * last modified time of the resulting file should be set to
      * that of the source file.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @exception IOException if the file cannot be copied.
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite, preserveLastModified);
     }
 
     /**
      * Calls File.setLastModified(long time) on Java above 1.1, and logs
      * a warning on Java 1.1.
      *
      * @param file The file to set the last modified time on.
      *             Must not be <code>null</code>.
      *
      * @param time the required modification time.
      *
      * @deprecated
      *
      * @exception BuildException if the last modified time cannot be set
      *                           despite running on a platform with a version
      *                           above 1.1.
      */
     public void setFileLastModified(File file, long time)
          throws BuildException {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             log("Cannot change the modification time of " + file
                 + " in JDK 1.1", Project.MSG_WARN);
             return;
         }
         fileUtils.setFileLastModified(file, time);
         log("Setting modification time for " + file, MSG_VERBOSE);
     }
 
     /**
      * Returns the boolean equivalent of a string, which is considered
      * <code>true</code> if either <code>"on"</code>, <code>"true"</code>,
      * or <code>"yes"</code> is found, ignoring case.
      *
      * @param s The string to convert to a boolean value.
      *          Must not be <code>null</code>.
      *
      * @return <code>true</code> if the given string is <code>"on"</code>,
      *         <code>"true"</code> or <code>"yes"</code>, or
      *         <code>false</code> otherwise.
      */
     public static boolean toBoolean(String s) {
         return (s.equalsIgnoreCase("on") ||
                 s.equalsIgnoreCase("true") ||
                 s.equalsIgnoreCase("yes"));
     }
 
     /**
      * Topologically sorts a set of targets.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targets A map of names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @return a vector of strings with the names of the targets in
      *         sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      */
     public final Vector topoSort(String root, Hashtable targets)
         throws BuildException {
         Vector ret = new Vector();
         Hashtable state = new Hashtable();
         Stack visiting = new Stack();
 
         // We first run a DFS based sort using the root as the starting node.
         // This creates the minimum sequence of Targets to the root node.
         // We then do a sort on any remaining unVISITED targets.
         // This is unnecessary for doing our build, but it catches
         // circular dependencies or missing Targets on the entire
         // dependency tree, not just on the Targets that depend on the
         // build Target.
 
         tsort(root, targets, state, visiting, ret);
         log("Build sequence for target `" + root + "' is " + ret, MSG_VERBOSE);
         for (Enumeration en = targets.keys(); en.hasMoreElements();) {
             String curTarget = (String) en.nextElement();
             String st = (String) state.get(curTarget);
             if (st == null) {
                 tsort(curTarget, targets, state, visiting, ret);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + curTarget);
             }
         }
         log("Complete build sequence is " + ret, MSG_VERBOSE);
         return ret;
     }
 
     /**
      * Performs a single step in a recursive depth-first-search traversal of
      * the target dependency tree.
      * <p>
      * The current target is first set to the "visiting" state, and pushed
      * onto the "visiting" stack.
      * <p>
      * An exception is then thrown if any child of the current node is in the
      * visiting state, as that implies a circular dependency. The exception
      * contains details of the cycle, using elements of the "visiting" stack.
      * <p>
      * If any child has not already been "visited", this method is called
      * recursively on it.
      * <p>
      * The current target is then added to the ordered list of targets. Note
      * that this is performed after the children have been visited in order
      * to get the correct order. The current target is set to the "visited"
      * state.
      * <p>
      * By the time this method returns, the ordered list contains the sequence
      * of targets up to and including the current target.
      *
      * @param root The current target to inspect.
      *             Must not be <code>null</code>.
      * @param targets A mapping from names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @param state   A mapping from target names to states
      *                (String to String).
      *                The states in question are "VISITING" and "VISITED".
      *                Must not be <code>null</code>.
      * @param visiting A stack of targets which are currently being visited.
      *                 Must not be <code>null</code>.
      * @param ret     The list to add target names to. This will end up
      *                containing the complete list of depenencies in
      *                dependency order.
      *                Must not be <code>null</code>.
      *
      * @exception BuildException if a non-existent target is specified or if
      *                           a circular dependency is detected.
      */
     private final void tsort(String root, Hashtable targets,
                              Hashtable state, Stack visiting,
                              Vector ret)
         throws BuildException {
         state.put(root, VISITING);
         visiting.push(root);
 
         Target target = (Target) targets.get(root);
 
         // Make sure we exist
         if (target == null) {
             StringBuffer sb = new StringBuffer("Target `");
             sb.append(root);
             sb.append("' does not exist in this project. ");
             visiting.pop();
             if (!visiting.empty()) {
                 String parent = (String) visiting.peek();
                 sb.append("It is used from target `");
                 sb.append(parent);
                 sb.append("'.");
             }
 
             throw new BuildException(new String(sb));
         }
 
         for (Enumeration en = target.getDependencies(); en.hasMoreElements();) {
             String cur = (String) en.nextElement();
             String m = (String) state.get(cur);
             if (m == null) {
                 // Not been visited
                 tsort(cur, targets, state, visiting, ret);
             } else if (m == VISITING) {
                 // Currently visiting this node, so have a cycle
                 throw makeCircularException(cur, visiting);
             }
         }
 
         String p = (String) visiting.pop();
         if (root != p) {
             throw new RuntimeException("Unexpected internal error: expected to "
                 + "pop " + root + " but got " + p);
         }
         state.put(root, VISITED);
         ret.addElement(target);
     }
 
     /**
      * Builds an appropriate exception detailing a specified circular
      * dependency.
      *
      * @param end The dependency to stop at. Must not be <code>null</code>.
      * @param stk A stack of dependencies. Must not be <code>null</code>.
      *
      * @return a BuildException detailing the specified circular dependency.
      */
     private static BuildException makeCircularException(String end, Stack stk) {
         StringBuffer sb = new StringBuffer("Circular dependency: ");
         sb.append(end);
         String c;
         do {
             c = (String) stk.pop();
             sb.append(" <- ");
             sb.append(c);
         } while (!c.equals(end));
         return new BuildException(new String(sb));
     }
 
     /**
      * Adds a reference to the project.
      *
      * @param name The name of the reference. Must not be <code>null</code>.
      * @param value The value of the reference. Must not be <code>null</code>.
      */
     public void addReference(String name, Object value) {
         synchronized (references) {
             Object old = ((AntRefTable)references).getReal(name);
             if (old == value) {
                 // no warning, this is not changing anything
                 return;
             }
             if (old != null && !(old instanceof UnknownElement)) {
                 log("Overriding previous definition of reference to " + name,
                     MSG_WARN);
             }
 
             String valueAsString = "";
             try {
                 valueAsString = value.toString();
             } catch (Throwable t) {
                 log("Caught exception (" + t.getClass().getName() +")"
                     + " while expanding " + name + ": " + t.getMessage(),
                     MSG_WARN);
             }
             log("Adding reference: " + name + " -> " + valueAsString,
                 MSG_DEBUG);
             references.put(name, value);
         }
     }
 
     /**
      * Returns a map of the references in the project (String to Object).
      * The returned hashtable is "live" and so must not be modified.
      *
      * @return a map of the references in the project (String to Object).
      */
     public Hashtable getReferences() {
         return references;
     }
 
     /**
      * Looks up a reference by its key (ID).
      *
      * @param key The key for the desired reference.
      *            Must not be <code>null</code>.
      *
      * @return the reference with the specified ID, or <code>null</code> if
      *         there is no such reference in the project.
      */
     public Object getReference(String key) {
         return references.get(key);
     }
 
     /**
      * Returns a description of the type of the given element, with
      * special handling for instances of tasks and data types.
      * <p>
      * This is useful for logging purposes.
      *
      * @param element The element to describe.
      *                Must not be <code>null</code>.
      *
      * @return a description of the element type
      *
      * @since 1.95, Ant 1.5
      */
     public String getElementName(Object element) {
         Hashtable elements = taskClassDefinitions;
         Class elementClass = element.getClass();
         String typeName = "task";
         if (!elements.contains(elementClass)) {
             elements = dataClassDefinitions;
             typeName = "data type";
             if (!elements.contains(elementClass)) {
                 elements = null;
             }
         }
 
         if (elements != null) {
             Enumeration e = elements.keys();
             while (e.hasMoreElements()) {
                 String name = (String) e.nextElement();
                 Class clazz = (Class) elements.get(name);
                 if (elementClass.equals(clazz)) {
                     return "The <" + name + "> " + typeName;
                 }
             }
         }
 
         return "Class " + elementClass.getName();
     }
 
     /**
      * Sends a "build started" event to the build listeners for this project.
      */
     public void fireBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.buildStarted(event);
         }
     }
 
     /**
      * Sends a "build finished" event to the build listeners for this project.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     public void fireBuildFinished(Throwable exception) {
         BuildEvent event = new BuildEvent(this);
         event.setException(exception);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.buildFinished(event);
         }
     }
 
 
     /**
      * Sends a "target started" event to the build listeners for this project.
      *
      * @param target The target which is starting to build.
      *               Must not be <code>null</code>.
      */
     protected void fireTargetStarted(Target target) {
         BuildEvent event = new BuildEvent(target);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.targetStarted(event);
         }
     }
 
     /**
      * Sends a "target finished" event to the build listeners for this
      * project.
      *
      * @param target    The target which has finished building.
      *                  Must not be <code>null</code>.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     protected void fireTargetFinished(Target target, Throwable exception) {
         BuildEvent event = new BuildEvent(target);
         event.setException(exception);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.targetFinished(event);
         }
     }
 
     /**
      * Sends a "task started" event to the build listeners for this project.
      *
      * @param task The target which is starting to execute.
      *               Must not be <code>null</code>.
      */
     protected void fireTaskStarted(Task task) {
         // register this as the current task on the current thread.
         registerThreadTask(Thread.currentThread(), task);
         BuildEvent event = new BuildEvent(task);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.taskStarted(event);
         }
     }
 
     /**
      * Sends a "task finished" event to the build listeners for this
      * project.
      *
      * @param task      The task which has finished executing.
      *                  Must not be <code>null</code>.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     protected void fireTaskFinished(Task task, Throwable exception) {
         registerThreadTask(Thread.currentThread(), null);
         System.out.flush();
         System.err.flush();
         BuildEvent event = new BuildEvent(task);
         event.setException(exception);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.taskFinished(event);
         }
     }
 
     /**
      * Sends a "message logged" event to the build listeners for this project.
      *
      * @param event    The event to send. This should be built up with the
      *                 appropriate task/target/project by the caller, so that
      *                 this method can set the message and priority, then send
      *                 the event. Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     private void fireMessageLoggedEvent(BuildEvent event, String message,
                                         int priority) {
         event.setMessage(message, priority);
         Vector listeners = getBuildListeners();
         for (int i = 0; i < listeners.size(); i++) {
             BuildListener listener = (BuildListener) listeners.elementAt(i);
             listener.messageLogged(event);
         }
     }
 
     /**
      * Sends a "message logged" project level event to the build listeners for
      * this project.
      *
      * @param project  The project generating the event.
      *                 Should not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Project project, String message,
                                      int priority) {
         BuildEvent event = new BuildEvent(project);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Sends a "message logged" target level event to the build listeners for
      * this project.
      *
      * @param target   The target generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Target target, String message,
                                      int priority) {
         BuildEvent event = new BuildEvent(target);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Sends a "message logged" task level event to the build listeners for
      * this project.
      *
      * @param task     The task generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Task task, String message, int priority) {
         BuildEvent event = new BuildEvent(task);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Register a task as the current task for a thread.
      * If the task is null, the thread's entry is removed.
      *
      * @param thread the thread on which the task is registered.
      * @param task the task to be registered.
      * @since Ant 1.5
      */
     public synchronized void registerThreadTask(Thread thread, Task task) {
         if (task != null) {
             threadTasks.put(thread, task);
         } else {
             threadTasks.remove(thread);
         }
     }
 
     /**
      * Get the current task assopciated with a thread, if any
      *
      * @param thread the thread for which the task is required.
      * @return the task which is currently registered for the given thread or
      *         null if no task is registered.
      */
     public Task getThreadTask(Thread thread) {
         return (Task) threadTasks.get(thread);
     }
 
 
     // Should move to a separate public class - and have API to add
     // listeners, etc.
     private static class AntRefTable extends Hashtable {
         Project project;
         public AntRefTable(Project project) {
             super();
             this.project=project;
         }
 
         /** Returns the unmodified original object.
          * This method should be called internally to
          * get the 'real' object.
          * The normal get method will do the replacement
          * of UnknownElement ( this is similar with the JDNI
          * refs behavior )
          */
         public Object getReal(Object key ) {
             return super.get( key );
         }
 
         /** Get method for the reference table.
          *  It can be used to hook dynamic references and to modify
          * some references on the fly - for example for delayed
          * evaluation.
          *
          * It is important to make sure that the processing that is
          * done inside is not calling get indirectly.
          *
          * @param key
          * @return
          */
         public Object get(Object key) {
             //System.out.println("AntRefTable.get " + key);
             Object o=super.get(key);
             if( o instanceof UnknownElement ) {
                 // Make sure that
                 ((UnknownElement)o).maybeConfigure();
                 o=((UnknownElement)o).getTask();
             }
             return o;
         }
     }
 
     private static class AntTaskTable extends LazyHashtable {
         Project project;
         Properties props;
         boolean tasks=false;
 
         public AntTaskTable( Project p, boolean tasks ) {
             this.project=p;
             this.tasks=tasks;
         }
 
         public void addDefinitions( Properties props ) {
             this.props=props;
         }
 
         protected void initAll( ) {
             if( initAllDone ) return;
             project.log("InitAll", Project.MSG_DEBUG);
             if( props==null ) return;
             Enumeration enum = props.propertyNames();
             while (enum.hasMoreElements()) {
                 String key = (String) enum.nextElement();
                 Class taskClass=getTask( key );
                 if( taskClass!=null ) {
                     // This will call a get() and a put()
                     if( tasks )
                         project.addTaskDefinition(key, taskClass);
                     else
                         project.addDataTypeDefinition(key, taskClass );
                 }
             }
             initAllDone=true;
         }
 
         protected Class getTask(String key) {
             if( props==null ) return null; // for tasks loaded before init()
             String value=props.getProperty(key);
             if( value==null) {
                 //project.log( "No class name for " + key, Project.MSG_VERBOSE );
                 return null;
             }
             try {
                 Class taskClass=null;
                 if( project.getCoreLoader() != null &&
                     !("only".equals(project.getProperty("build.sysclasspath")))) {
                     try {
                         taskClass=project.getCoreLoader().loadClass(value);
                         if( taskClass != null ) return taskClass;
                     } catch( Exception ex ) {
                     }
                 }
                 taskClass = Class.forName(value);
                 return taskClass;
             } catch (NoClassDefFoundError ncdfe) {
                 project.log("Could not load a dependent class ("
                         + ncdfe.getMessage() + ") for task " + key, Project.MSG_DEBUG);
             } catch (ClassNotFoundException cnfe) {
                 project.log("Could not load class (" + value
                         + ") for task " + key, Project.MSG_DEBUG);
             }
             return null;
         }
 
         // Hashtable implementation
         public Object get( Object key ) {
             Object orig=super.get( key );
             if( orig!= null ) return orig;
             if( ! (key instanceof String) ) return null;
             project.log("Get task " + key, Project.MSG_DEBUG );
             Object taskClass=getTask( (String) key);
             if( taskClass != null)
                 super.put( key, taskClass );
             return taskClass;
         }
 
         public boolean containsKey(Object key) {
             return get(key) != null;
         }
 
     }
 }
diff --git a/src/main/org/apache/tools/ant/Task.java b/src/main/org/apache/tools/ant/Task.java
index c12cb5c9d..1f7a788d9 100644
--- a/src/main/org/apache/tools/ant/Task.java
+++ b/src/main/org/apache/tools/ant/Task.java
@@ -1,442 +1,464 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant;
 
 import java.util.Enumeration;
 
 /**
  * Base class for all tasks.
  *
  * Use Project.createTask to create a new task instance rather than
  * using this class directly for construction.
  *
  * @see Project#createTask
  */
 public abstract class Task extends ProjectComponent {
     /**
      * Target this task belongs to, if any.
      * @deprecated You should not be accessing this variable directly.
      *   Please use the {@link #getOwningTarget()} method.
      */
     protected Target target;
 
     /**
      * Description of this task, if any.
      * @deprecated You should not be accessing this variable directly.
      */
     protected String description;
 
     /**
      * Location within the build file of this task definition.
      * @deprecated You should not be accessing this variable directly.
      *   Please use the {@link #getLocation()} method.
      */
     protected Location location = Location.UNKNOWN_LOCATION;
 
     /**
      * Name of this task to be used for logging purposes.
      * This defaults to the same as the type, but may be
      * overridden by the user. For instance, the name "java"
      * isn't terribly descriptive for a task used within
      * another task - the outer task code can probably
      * provide a better one.
      * @deprecated You should not be accessing this variable directly.
      *   Please use the {@link #getTaskName()} method.
      */
     protected String taskName;
 
     /**
      * Type of this task.
      *
      * @deprecated You should not be accessing this variable directly.
      *   Please use the {@link #getTaskType()} method.
      */
     protected String taskType;
 
     /**
      * Wrapper for this object, used to configure it at runtime.
      *
      * @deprecated You should not be accessing this variable directly.
      *   Please use the {@link #getWrapper()} method.
      */
     protected RuntimeConfigurable wrapper;
 
     /**
      * Whether or not this task is invalid. A task becomes invalid
      * if a conflicting class is specified as the implementation for
      * its type.
      */
     private boolean invalid;
 
     /** Sole constructor. */
     public Task() {
     }
 
     /**
      * Sets the target container of this task.
      *
      * @param target Target in whose scope this task belongs.
      *               May be <code>null</code>, indicating a top-level task.
      */
     public void setOwningTarget(Target target) {
         this.target = target;
     }
 
     /**
      * Returns the container target of this task.
      *
      * @return The target containing this task, or <code>null</code> if
      *         this task is a top-level task.
      */
     public Target getOwningTarget() {
         return target;
     }
 
     /**
      * Sets the name to use in logging messages.
      *
      * @param name The name to use in logging messages.
      *             Should not be <code>null</code>.
      */
     public void setTaskName(String name) {
         this.taskName = name;
     }
 
     /**
      * Returns the name to use in logging messages.
      *
      * @return the name to use in logging messages.
      */
     public String getTaskName() {
         return taskName;
     }
 
     /**
      * Sets the name with which the task has been invoked.
      *
      * @param type The name the task has been invoked as.
      *             Should not be <code>null</code>.
      */
     public void setTaskType(String type) {
         this.taskType = type;
     }
 
     /**
      * Sets a description of the current action. This may be used for logging
      * purposes.
      *
      * @param desc Description of the current action.
      *             May be <code>null</code>, indicating that no description is
      *             available.
      *
      */
     public void setDescription(String desc) {
         description = desc;
     }
 
     /**
      * Returns the description of the current action.
      *
      * @return the description of the current action, or <code>null</code> if
      *         no description is available.
      */
     public String getDescription() {
         return description;
     }
 
     /**
      * Called by the project to let the task initialize properly.
      * The default implementation is a no-op.
      *
      * @exception BuildException if someting goes wrong with the build
      */
     public void init() throws BuildException {}
 
     /**
      * Called by the project to let the task do its work. This method may be
      * called more than once, if the task is invoked more than once.
      * For example,
      * if target1 and target2 both depend on target3, then running
      * "ant target1 target2" will run all tasks in target3 twice.
      *
      * @exception BuildException if something goes wrong with the build
      */
     public void execute() throws BuildException {}
 
     /**
      * Returns the file/location where this task was defined.
      *
      * @return the file/location where this task was defined.
      *         Should not return <code>null</code>. Location.UNKNOWN_LOCATION
      *         is used for unknown locations.
      *
      * @see Location#UNKNOWN_LOCATION
      */
     public Location getLocation() {
         return location;
     }
 
     /**
      * Sets the file/location where this task was defined.
      *
      * @param location The file/location where this task was defined.
      *                 Should not be <code>null</code> - use
      *                 Location.UNKNOWN_LOCATION if the location isn't known.
      *
      * @see Location#UNKNOWN_LOCATION
      */
     public void setLocation(Location location) {
         this.location = location;
     }
 
     /**
      * Returns the wrapper used for runtime configuration.
      *
      * @return the wrapper used for runtime configuration. This
      *         method will generate a new wrapper (and cache it)
      *         if one isn't set already.
      */
     public RuntimeConfigurable getRuntimeConfigurableWrapper() {
         if (wrapper == null) {
             wrapper = new RuntimeConfigurable(this, getTaskName());
         }
         return wrapper;
     }
 
     /**
      * Sets the wrapper to be used for runtime configuration.
      *
      * This method should be used only by the ProjectHelper and ant internals.
      * It is public to allow helper plugins to operate on tasks, normal tasks
      * should never use it.
      *
      * @param wrapper The wrapper to be used for runtime configuration.
      *                May be <code>null</code>, in which case the next call
      *                to getRuntimeConfigurableWrapper will generate a new
      *                wrapper.
      */
     public void setRuntimeConfigurableWrapper(RuntimeConfigurable wrapper) {
         this.wrapper = wrapper;
     }
 
     // XXX: (Jon Skeet) The comment "if it hasn't been done already" may
     // not be strictly true. wrapper.maybeConfigure() won't configure the same
     // attributes/text more than once, but it may well add the children again,
     // unless I've missed something.
     /**
      * Configures this task - if it hasn't been done already.
      * If the task has been invalidated, it is replaced with an
      * UnknownElement task which uses the new definition in the project.
      *
      * @exception BuildException if the task cannot be configured.
      */
     public void maybeConfigure() throws BuildException {
         if (!invalid) {
             if (wrapper != null) {
                 wrapper.maybeConfigure(getProject());
             }
         } else {
             getReplacement();
         }
     }
 
     /**
      * Handles a line of output by logging it with the INFO priority.
      *
      * @param line The line of output to log. Should not be <code>null</code>.
      */
     protected void handleOutput(String line) {
+        handleOutput(line + "X7", true);
+    }
+
+    /**
+     * Handles a line of output by logging it with the INFO priority.
+     *
+     * @param line The line of output to log. Should not be <code>null</code>.
+     * @param terminated true if this line should be terminated with an 
+     *        end-of-line marker
+     */
+    protected void handleOutput(String line, boolean terminated) {
         log(line, Project.MSG_INFO);
     }
 
     /**
      * Handles an error line by logging it with the INFO priority.
      *
      * @param line The error line to log. Should not be <code>null</code>.
      */
     protected void handleErrorOutput(String line) {
+        handleErrorOutput(line, true);
+    }
+
+    /**
+     * Handles an error line by logging it with the INFO priority.
+     *
+     * @param line The error line to log. Should not be <code>null</code>.
+     * @param terminated true if this line should be terminated with an 
+     *        end-of-line marker
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
         log(line, Project.MSG_ERR);
     }
 
     /**
      * Logs a message with the default (INFO) priority.
      *
      * @param msg The message to be logged. Should not be <code>null</code>.
      */
     public void log(String msg) {
         log(msg, Project.MSG_INFO);
     }
 
     /**
      * Logs a mesage with the given priority. This delegates
      * the actual logging to the project.
      *
      * @param msg The message to be logged. Should not be <code>null</code>.
      * @param msgLevel The message priority at which this message is to
      *                 be logged.
      */
     public void log(String msg, int msgLevel) {
         getProject().log(this, msg, msgLevel);
     }
 
     /**
      * Performs this task if it's still valid, or gets a replacement
      * version and performs that otherwise.
      *
      * Performing a task consists of firing a task started event,
      * configuring the task, executing it, and then firing task finished
      * event. If a runtime exception is thrown, the task finished event
      * is still fired, but with the exception as the cause.
      */
     public final void perform() {
         if (!invalid) {
             try {
                 getProject().fireTaskStarted(this);
                 maybeConfigure();
                 execute();
                 getProject().fireTaskFinished(this, null);
             } catch (RuntimeException exc) {
                 if (exc instanceof BuildException) {
                     BuildException be = (BuildException) exc;
                     if (be.getLocation() == Location.UNKNOWN_LOCATION) {
                         be.setLocation(getLocation());
                     }
                 }
                 getProject().fireTaskFinished(this, exc);
                 throw exc;
             }
         } else {
             UnknownElement ue = getReplacement();
             Task task = ue.getTask();
             task.perform();
         }
     }
 
     /**
      * Marks this task as invalid. Any further use of this task
      * will go through a replacement with the updated definition.
      */
     final void markInvalid() {
         invalid = true;
     }
 
     /**
      * Has this task been marked invalid?
      *
      * @since Ant 1.5
      */
     protected final boolean isInvalid() {
         return invalid;
     }
 
     /**
      * Replacement element used if this task is invalidated.
      */
     private UnknownElement replacement;
 
     /**
      * Creates an UnknownElement that can be used to replace this task.
      * Once this has been created once, it is cached and returned by
      * future calls.
      *
      * @return the UnknownElement instance for the new definition of this task.
      */
     private UnknownElement getReplacement() {
         if (replacement == null) {
             replacement = new UnknownElement(taskType);
             replacement.setProject(getProject());
             replacement.setTaskType(taskType);
             replacement.setTaskName(taskName);
             replacement.setLocation(location);
             replacement.setOwningTarget(target);
             replacement.setRuntimeConfigurableWrapper(wrapper);
             wrapper.setProxy(replacement);
             replaceChildren(wrapper, replacement);
             target.replaceChild(this, replacement);
             replacement.maybeConfigure();
         }
         return replacement;
     }
 
     /**
      * Recursively adds an UnknownElement instance for each child
      * element of replacement.
      *
      * @since Ant 1.5.1
      */
     private void replaceChildren(RuntimeConfigurable wrapper,
                                  UnknownElement parentElement) {
         Enumeration enum = wrapper.getChildren();
         while (enum.hasMoreElements()) {
             RuntimeConfigurable childWrapper =
                 (RuntimeConfigurable) enum.nextElement();
             UnknownElement childElement =
                 new UnknownElement(childWrapper.getElementTag());
             parentElement.addChild(childElement);
             childElement.setProject(getProject());
             childElement.setRuntimeConfigurableWrapper(childWrapper);
             childWrapper.setProxy(childElement);
             replaceChildren(childWrapper, childElement);
         }
     }
 
     protected String getTaskType() {
         return taskType;
     }
 
     protected RuntimeConfigurable getWrapper() {
         return wrapper;
     }
 }
diff --git a/src/main/org/apache/tools/ant/UnknownElement.java b/src/main/org/apache/tools/ant/UnknownElement.java
index 03c9d6df4..e97f7c6aa 100644
--- a/src/main/org/apache/tools/ant/UnknownElement.java
+++ b/src/main/org/apache/tools/ant/UnknownElement.java
@@ -1,396 +1,423 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant;
 
 import java.util.Vector;
 
 /**
  * Wrapper class that holds all the information necessary to create a task
  * or data type that did not exist when Ant started, or one which
  * has had its definition updated to use a different implementation class.
  *
  * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
  */
 public class UnknownElement extends Task {
 
     /**
      * Holds the name of the task/type or nested child element of a
      * task/type that hasn't been defined at parser time or has
      * been redefined since original creation.
      */
     private String elementName;
 
     /**
      * The real object after it has been loaded.
      */
     private Object realThing;
 
     /**
      * List of child elements (UnknownElements).
      */
     private Vector children = new Vector();
 
     /**
      * Creates an UnknownElement for the given element name.
      *
      * @param elementName The name of the unknown element.
      *                    Must not be <code>null</code>.
      */
     public UnknownElement (String elementName) {
         this.elementName = elementName;
     }
 
     /**
      * Returns the name of the XML element which generated this unknown
      * element.
      *
      * @return the name of the XML element which generated this unknown
      *         element.
      */
     public String getTag() {
         return elementName;
     }
 
     public RuntimeConfigurable getWrapper() {
         return wrapper;
     }
 
     /**
      * Creates the real object instance and child elements, then configures
      * the attributes and text of the real object. This unknown element
      * is then replaced with the real object in the containing target's list
      * of children.
      *
      * @exception BuildException if the configuration fails
      */
     public void maybeConfigure() throws BuildException {
         //ProjectComponentHelper helper=ProjectComponentHelper.getProjectComponentHelper();
         //realThing = helper.createProjectComponent( this, getProject(), null,
         //                                           this.getTag());
 
         realThing = makeObject(this, getWrapper());
 
         getWrapper().setProxy(realThing);
         if (realThing instanceof Task) {
             Task task=(Task)realThing;
 
             task.setProject(project);
             task.setRuntimeConfigurableWrapper(getWrapper());
             task.setLocation(this.getLocation());
             // UnknownElement always has an associated target
             task.setOwningTarget(this.getOwningTarget());
             task.init();
 
             // For Script to work. Ugly
             // The reference is replaced by RuntimeConfigurable
             this.getOwningTarget().replaceChild(this, (Task)realThing);
         }
 
         handleChildren(realThing, getWrapper());
 
         getWrapper().maybeConfigure(getProject());
     }
 
     /**
      * Handles output sent to System.out by this task or its real task.
      *
      * @param line The line of output to log. Should not be <code>null</code>.
      */
     protected void handleOutput(String line) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleOutput(line);
         } else {
             super.handleOutput(line);
         }
     }
 
     /**
+     * Handles output sent to System.out by this task or its real task.
+     *
+     * @param line The line of output to log. Should not be <code>null</code>.
+     */
+    protected void handleOutput(String line, boolean terminated) {
+        if (realThing instanceof Task) {
+            ((Task) realThing).handleOutput(line, terminated);
+        } else {
+            super.handleOutput(line, terminated);
+        }
+    }
+
+    /**
      * Handles error output sent to System.err by this task or its real task.
      *
      * @param line The error line to log. Should not be <code>null</code>.
      */
     protected void handleErrorOutput(String line) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleErrorOutput(line);
         } else {
             super.handleErrorOutput(line);
         }
     }
 
+
+    /**
+     * Handles error output sent to System.err by this task or its real task.
+     *
+     * @param line The error line to log. Should not be <code>null</code>.
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (realThing instanceof Task) {
+            ((Task) realThing).handleErrorOutput(line, terminated);
+        } else {
+            super.handleErrorOutput(line, terminated);
+        }
+    }
+    
     /**
      * Executes the real object if it's a task. If it's not a task
      * (e.g. a data type) then this method does nothing.
      */
     public void execute() {
         if (realThing == null) {
             // plain impossible to get here, maybeConfigure should
             // have thrown an exception.
             throw new BuildException("Could not create task of type: "
                                      + elementName, getLocation());
         }
 
         if (realThing instanceof Task) {
             ((Task) realThing).execute();
         }
 
         // the task will not be reused ( a new init() will be called )
         // Let GC do its job
         realThing=null;
     }
 
     /**
      * Adds a child element to this element.
      *
      * @param child The child element to add. Must not be <code>null</code>.
      */
     public void addChild(UnknownElement child) {
         children.addElement(child);
     }
 
     /**
      * Creates child elements, creates children of the children
      * (recursively), and sets attributes of the child elements.
      *
      * @param parent The configured object for the parent.
      *               Must not be <code>null</code>.
      *
      * @param parentWrapper The wrapper containing child wrappers
      *                      to be configured. Must not be <code>null</code>
      *                      if there are any children.
      *
      * @exception BuildException if the children cannot be configured.
      */
     protected void handleChildren(Object parent,
                                   RuntimeConfigurable parentWrapper)
         throws BuildException
     {
         if (parent instanceof TaskAdapter) {
             parent = ((TaskAdapter) parent).getProxy();
         }
 
         Class parentClass = parent.getClass();
         IntrospectionHelper ih = IntrospectionHelper.getHelper(parentClass);
 
         for (int i = 0;  i < children.size(); i++) {
             RuntimeConfigurable childWrapper = parentWrapper.getChild(i);
             UnknownElement child = (UnknownElement) children.elementAt(i);
             Object realChild = null;
 
             if (parent instanceof TaskContainer) {
                 //ProjectComponentHelper helper=ProjectComponentHelper.getProjectComponentHelper();
                 //realChild = helper.createProjectComponent( child, getProject(), null,
                    //                                           child.getTag());
                 realChild=makeTask(child, childWrapper, false);
 
                 if (realChild == null ) {
                     throw getNotFoundException("task", child.getTag());
                 }
 
                 // XXX DataTypes will be wrapped or treated like normal components
                 if( realChild instanceof Task ) {
                     Task task=(Task)realChild;
                     ((TaskContainer) parent).addTask(task);
                     task.setLocation(child.getLocation());
                     // UnknownElement always has an associated target
                     task.setOwningTarget(this.getOwningTarget());
                     task.init();
                 } else {
                     // What ? Add data type ? createElement ?
                 }
             } else {
                 realChild = ih.createElement(getProject(), parent, child.getTag());
             }
 
             childWrapper.setProxy(realChild);
             if (parent instanceof TaskContainer) {
                 ((Task) realChild).setRuntimeConfigurableWrapper(childWrapper);
             }
 
             child.handleChildren(realChild, childWrapper);
 
 //            if (parent instanceof TaskContainer) {
 //                ((Task) realChild).maybeConfigure();
 //            }
         }
     }
 
     /**
      * Creates a named task or data type. If the real object is a task,
      * it is configured up to the init() stage.
      *
      * @param ue The unknown element to create the real object for.
      *           Must not be <code>null</code>.
      * @param w  Ignored in this implementation.
      *
      * @return the task or data type represented by the given unknown element.
      */
     protected Object makeObject(UnknownElement ue, RuntimeConfigurable w) {
         Object o = makeTask(ue, w, true);
         if (o == null) {
             o = getProject().createDataType(ue.getTag());
         }
         if (o == null) {
             throw getNotFoundException("task or type", ue.getTag());
         }
         return o;
     }
 
     /**
      * Creates a named task and configures it up to the init() stage.
      *
      * @param ue The UnknownElement to create the real task for.
      *           Must not be <code>null</code>.
      * @param w  Ignored.
      * @param onTopLevel Whether or not this is definitely trying to create
      *                   a task. If this is <code>true</code> and the
      *                   task name is not recognised, a BuildException
      *                   is thrown.
      *
      * @return the task specified by the given unknown element, or
      *         <code>null</code> if the task name is not recognised and
      *         onTopLevel is <code>false</code>.
      */
     protected Task makeTask(UnknownElement ue, RuntimeConfigurable w,
                             boolean onTopLevel) {
         Task task = getProject().createTask(ue.getTag());
         if (task == null && !onTopLevel) {
             throw getNotFoundException("task", ue.getTag());
         }
 
         if (task != null) {
             task.setLocation(getLocation());
             // UnknownElement always has an associated target
             task.setOwningTarget(getOwningTarget());
             task.init();
         }
         return task;
     }
 
     /**
      * Returns a very verbose exception for when a task/data type cannot
      * be found.
      *
      * @param what The kind of thing being created. For example, when
      *             a task name could not be found, this would be
      *             <code>"task"</code>. Should not be <code>null</code>.
      * @param elementName The name of the element which could not be found.
      *                    Should not be <code>null</code>.
      *
      * @return a detailed description of what might have caused the problem.
      */
     protected BuildException getNotFoundException(String what,
                                                   String elementName) {
         String lSep = System.getProperty("line.separator");
         String msg = "Could not create " + what + " of type: " + elementName
             + "." + lSep + lSep
             + "Ant could not find the task or a class this "
             + "task relies upon." + lSep + lSep
             + "This is common and has a number of causes; the usual " + lSep
             + "solutions are to read the manual pages then download and" + lSep
             + "install needed JAR files, or fix the build file: " + lSep
             + " - You have misspelt '" + elementName + "'." + lSep
             + "   Fix: check your spelling." + lSep
             + " - The task needs an external JAR file to execute" + lSep
             + "   and this is not found at the right place in the classpath." + lSep
             + "   Fix: check the documentation for dependencies." + lSep
             + "   Fix: declare the task." + lSep
             + " - The task is an Ant optional task and optional.jar is absent" + lSep
             + "   Fix: look for optional.jar in ANT_HOME/lib, download if needed" + lSep
             + " - The task was not built into optional.jar as dependent"  + lSep
             + "   libraries were not found at build time." + lSep
             + "   Fix: look in the JAR to verify, then rebuild with the needed" + lSep
             + "   libraries, or download a release version from apache.org" + lSep
             + " - The build file was written for a later version of Ant" + lSep
             + "   Fix: upgrade to at least the latest release version of Ant" + lSep
             + " - The task is not an Ant core or optional task " + lSep
             + "   and needs to be declared using <taskdef>." + lSep
             + lSep
             + "Remember that for JAR files to be visible to Ant tasks implemented" + lSep
             + "in ANT_HOME/lib, the files must be in the same directory or on the" + lSep
             + "classpath" + lSep
             + lSep
             + "Please neither file bug reports on this problem, nor email the" + lSep
             + "Ant mailing lists, until all of these causes have been explored," + lSep
             + "as this is not an Ant bug.";
 
 
         return new BuildException(msg, getLocation());
     }
 
     /**
      * Returns the name to use in logging messages.
      *
      * @return the name to use in logging messages.
      */
     public String getTaskName() {
         //return elementName;
         return realThing == null || !(realThing instanceof Task) ?
             super.getTaskName() : ((Task) realThing).getTaskName();
     }
 
     /**
      * Returns the task instance after it has been created and if it is a task.
      *
      * @return a task instance or <code>null</code> if the real object is not
      *         a task.
      */
     public Task getTask() {
         if (realThing instanceof Task) {
             return (Task) realThing;
         }
         return null;
     }
 
 }// UnknownElement
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index dc060307e..5cbffe269 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,612 +1,638 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.reflect.Method;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.BuildListener;
 import org.apache.tools.ant.DefaultLogger;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectComponent;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Build a sub-project.
  *
  *  <pre>
  *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
  *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
  *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
  *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
  *    &lt;/ant&gt;</SPAN>
  *  &lt;/target&gt;</SPAN>
  *
  *  &lt;target name=&quot;bar&quot; depends=&quot;init&quot;&gt;
  *    &lt;echo message=&quot;prop is ${property1} ${foo}&quot; /&gt;
  *  &lt;/target&gt;
  * </pre>
  *
  *
  * @author Costin Manolache
  *
  * @since Ant 1.1
  *
  * @ant.task category="control"
  */
 public class Ant extends Task {
 
     /** the basedir where is executed the build file */
     private File dir = null;
 
     /**
      * the build.xml file (can be absolute) in this case dir will be
      * ignored
      */
     private String antFile = null;
 
     /** the target to call if any */
     private String target = null;
 
     /** the output */
     private String output  = null;
 
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
 
     /**
      * If true, pass all properties to the new Ant project.
      * Defaults to true.
      */
     public void setInheritAll(boolean value) {
         inheritAll = value;
     }
 
     /**
      * If true, pass all references to the new Ant project.
      * Defaults to false.
      */
     public void setInheritRefs(boolean value) {
         inheritRefs = value;
     }
 
     /**
      * Creates a Project instance for the project to call.
      */
     public void init() {
         newProject = new Project();
         newProject.setJavaVersionProperty();
         newProject.addTaskDefinition("property",
                                      (Class) getProject().getTaskDefinitions()
                                              .get("property"));
     }
 
     /**
      * Called in execute or createProperty if newProject is null.
      *
      * <p>This can happen if the same instance of this task is run
      * twice as newProject is set to null at the end of execute (to
      * save memory and help the GC).</p>
      *
      * <p>Sets all properties that have been defined as nested
      * property elements.</p>
      */
     private void reinit() {
         init();
         final int count = properties.size();
         for (int i = 0; i < count; i++) {
             Property p = (Property) properties.elementAt(i);
             Property newP = (Property) newProject.createTask("property");
             newP.setName(p.getName());
             if (p.getValue() != null) {
                 newP.setValue(p.getValue());
             }
             if (p.getFile() != null) {
                 newP.setFile(p.getFile());
             }
             if (p.getResource() != null) {
                 newP.setResource(p.getResource());
             }
             if (p.getPrefix() != null) {
                 newP.setPrefix(p.getPrefix());
             }
             if (p.getRefid() != null) {
                 newP.setRefid(p.getRefid());
             }
             if (p.getEnvironment() != null) {
                 newP.setEnvironment(p.getEnvironment());
             }
             if (p.getClasspath() != null) {
                 newP.setClasspath(p.getClasspath());
             }
             properties.setElementAt(newP, i);
         }
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
 
         Vector listeners = getProject().getBuildListeners();
         final int count = listeners.size();
         for (int i = 0; i < count; i++) {
             newProject.addBuildListener((BuildListener) listeners.elementAt(i));
         }
 
         if (output != null) {
             File outfile = null;
             if (dir != null) {
                 outfile = FileUtils.newFileUtils().resolveFile(dir, output);
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
 
         Hashtable taskdefs = getProject().getTaskDefinitions();
         Enumeration et = taskdefs.keys();
         while (et.hasMoreElements()) {
             String taskName = (String) et.nextElement();
             if (taskName.equals("property")) {
                 // we have already added this taskdef in #init
                 continue;
             }
             Class taskClass = (Class) taskdefs.get(taskName);
             newProject.addTaskDefinition(taskName, taskClass);
         }
 
         Hashtable typedefs = getProject().getDataTypeDefinitions();
         Enumeration e = typedefs.keys();
         while (e.hasMoreElements()) {
             String typeName = (String) e.nextElement();
             Class typeClass = (Class) typedefs.get(typeName);
             newProject.addDataTypeDefinition(typeName, typeClass);
         }
 
         // set user-defined properties
         getProject().copyUserProperties(newProject);
 
         if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            newProject.setSystemProperties();
 
         } else {
             // set all properties from calling project
 
             Hashtable props = getProject().getProperties();
             e = props.keys();
             while (e.hasMoreElements()) {
                 String arg = e.nextElement().toString();
                 if ("basedir".equals(arg) || "ant.file".equals(arg)) {
                     // basedir and ant.file get special treatment in execute()
                     continue;
                 }
 
                 String value = props.get(arg).toString();
                 // don't re-set user properties, avoid the warning message
                 if (newProject.getProperty(arg) == null){
                     // no user property
                     newProject.setNewProperty(arg, value);
                 }
             }
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @since Ant 1.5
      */
     protected void handleOutput(String line) {
         if (newProject != null) {
             newProject.demuxOutput(line, false);
         } else {
             super.handleOutput(line);
         }
     }
 
     /**
+     * Pass output sent to System.out to the new project.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleOutput(String line, boolean terminated) {
+        if (newProject != null) {
+            newProject.demuxOutput(line, false, terminated);
+        } else {
+            super.handleOutput(line, terminated);
+        }
+    }
+
+    /**
      * Pass output sent to System.err to the new project.
      *
      * @since Ant 1.5
      */
     protected void handleErrorOutput(String line) {
         if (newProject != null) {
             newProject.demuxOutput(line, true);
         } else {
             super.handleErrorOutput(line);
         }
     }
 
     /**
+     * Pass output sent to System.err to the new project.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (newProject != null) {
+            newProject.demuxOutput(line, true, terminated);
+        } else {
+            super.handleErrorOutput(line, terminated);
+        }
+    }
+
+    /**
      * Do the execution.
      */
     public void execute() throws BuildException {
         File savedDir = dir;
         String savedAntFile = antFile;
         String savedTarget = target;
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
                 if (savedDir != null) { // has been set explicitly
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
 
             File file = FileUtils.newFileUtils().resolveFile(dir, antFile);
             antFile = file.getAbsolutePath();
 
             log("calling target " + (target != null ? target : "[default]")
                     + " in build file " +  antFile.toString(),
                     Project.MSG_VERBOSE);
             newProject.setUserProperty("ant.file" , antFile);
             ProjectHelper.configureProject(newProject, new File(antFile));
 
             if (target == null) {
                 target = newProject.getDefaultTarget();
             }
 
             // Are we trying to call the target in which we are defined (or 
             // the build file if this is a top level task)?
             if (newProject.getBaseDir().equals(getProject().getBaseDir()) &&
                 newProject.getProperty("ant.file").equals(getProject().getProperty("ant.file"))
                 && getOwningTarget() != null
                 && (getOwningTarget().getName().equals("") ||
                     getOwningTarget().getName().equals(target))) {
                 throw new BuildException("ant task calling its own parent "
                                          + "target");
             }
 
             addReferences();
 
             if (target != null) {
                 newProject.executeTarget(target);
             } else {
                 newProject.executeTarget("");
             }
         } finally {
             // help the gc
             newProject = null;
             Enumeration enum = properties.elements();
             while (enum.hasMoreElements()) {
                 Property p = (Property) enum.nextElement();
                 p.setProject(null);
             }
 
             if (output != null && out != null) {
                 try {
                     out.close();
                 } catch (final Exception e) {
                     //ignore
                 }
             }
             dir = savedDir;
             antFile = savedAntFile;
             target = savedTarget;
         }
     }
 
     /**
      * Override the properties in the new project with the one
      * explicitly defined as nested elements here.
      */
     private void overrideProperties() throws BuildException {
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
      */
     private void addReferences() throws BuildException {
         Hashtable thisReferences = (Hashtable) getProject().getReferences().clone();
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
      * the parent project and add it to the new project with the key
      * newkey.
      *
      * <p>If we cannot clone it, copy the referenced object itself and
      * keep our fingers crossed.</p>
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
      * The directory to use as a base directory for the new Ant project.
      * Defaults to the current project's basedir, unless inheritall
      * has been set to false, in which case it doesn't have a default
      * value. This will override the basedir setting of the called project.
      */
     public void setDir(File d) {
         this.dir = d;
     }
 
     /**
      * The build file to use.
      * Defaults to "build.xml". This file is expected to be a filename relative
      * to the dir attribute given.
      */
     public void setAntfile(String s) {
         // @note: it is a string and not a file to handle relative/absolute
         // otherwise a relative file will be resolved based on the current
         // basedir.
         this.antFile = s;
     }
 
     /**
      * The target of the new Ant project to execute.
      * Defaults to the new project's default target.
      */
     public void setTarget(String s) {
         if (s.equals("")) {
             throw new BuildException("target attribute must not be empty");
         }
         
         this.target = s;
     }
 
     /**
      * Filename to write the output to.
      * This is relative to the value of the dir attribute
      * if it has been set or to the base directory of the
      * current project otherwise.
      */
     public void setOutput(String s) {
         this.output = s;
     }
 
     /**
      * Property to pass to the new project.
      * The property is passed as a 'user property'
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
      * Reference element identifying a data type to carry
      * over to the new project.
      */
     public void addReference(Reference r) {
         references.addElement(r);
     }
 
     /**
      * Helper class that implements the nested &lt;reference&gt;
      * element of &lt;ant&gt; and &lt;antcall&gt;.
      */
     public static class Reference
         extends org.apache.tools.ant.types.Reference {
 
         /** Creates a reference to be configured by Ant */
         public Reference() {
             super();
         }
 
         private String targetid = null;
 
         /**
          * Set the id that this reference to be stored under in the
          * new project.
          *
          * @param targetid the id under which this reference will be passed to
          *        the new project */
         public void setToRefid(String targetid) {
             this.targetid = targetid;
         }
 
         /**
          * Get the id under which this reference will be stored in the new
          * project
          *
          * @return the id of the reference in the new project.
          */
         public String getToRefid() {
             return targetid;
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/CallTarget.java b/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
index ac61ead2b..b94b4a699 100644
--- a/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
+++ b/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
@@ -1,201 +1,226 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Task;
 
 /**
  * Call another target in the same project.
  *
  *  <pre>
  *    &lt;target name="foo"&gt;
  *      &lt;antcall target="bar"&gt;
  *        &lt;param name="property1" value="aaaaa" /&gt;
  *        &lt;param name="foo" value="baz" /&gt;
  *       &lt;/antcall&gt;
  *    &lt;/target&gt;
  *
  *    &lt;target name="bar" depends="init"&gt;
  *      &lt;echo message="prop is ${property1} ${foo}" /&gt;
  *    &lt;/target&gt;
  * </pre>
  *
  * <p>This only works as expected if neither property1 nor foo are
  * defined in the project itself.
  *
  *
  * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a> 
  *
  * @since Ant 1.2
  *
  * @ant.task name="antcall" category="control"
  */
 public class CallTarget extends Task {
 
     private Ant callee;
     private String subTarget;
     // must match the default value of Ant#inheritAll
     private boolean inheritAll = true;
     // must match the default value of Ant#inheritRefs
     private boolean inheritRefs = false;
 
     /**
      * If true, pass all properties to the new Ant project.
      * Defaults to true.
      */
     public void setInheritAll(boolean inherit) {
        inheritAll = inherit;
     }
 
     /**
      * If true, pass all references to the new Ant project.
      * Defaults to false
      * @param inheritRefs new value
      */
     public void setInheritRefs(boolean inheritRefs) {
         this.inheritRefs = inheritRefs;
     }
 
     /**
      * init this task by creating new instance of the ant task and
      * configuring it's by calling its own init method.
      */
     public void init() {
         callee = (Ant) getProject().createTask("ant");
         callee.setOwningTarget(getOwningTarget());
         callee.setTaskName(getTaskName());
         callee.setLocation(getLocation());
         callee.init();
     }
 
     /**
      * hand off the work to the ant task of ours, after setting it up
      * @throws BuildException on validation failure or if the target didn't
      * execute
      */
     public void execute() throws BuildException {
         if (callee == null) {
             init();
         }
         
         if (subTarget == null) {
             throw new BuildException("Attribute target is required.", 
                                      getLocation());
         }
         
         callee.setAntfile(getProject().getProperty("ant.file"));
         callee.setTarget(subTarget);
         callee.setInheritAll(inheritAll);
         callee.setInheritRefs(inheritRefs);
         callee.execute();
     }
 
     /**
      * Property to pass to the invoked target.
      */
     public Property createParam() {
         if (callee == null) {
             init();
         }
         return callee.createProperty();
     }
 
     /**
      * Reference element identifying a data type to carry
      * over to the invoked target.
      * @since Ant 1.5
      */
     public void addReference(Ant.Reference r) {
         if (callee == null) {
             init();
         }
         callee.addReference(r);
     }
 
     /**
      * Target to execute, required.
      */
     public void setTarget(String target) {
         subTarget = target;
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @since Ant 1.5
      */
     protected void handleOutput(String line) {
         if (callee != null) {
             callee.handleOutput(line);
         } else {
             super.handleOutput(line);
         }
     }
     
     /**
+     * Pass output sent to System.out to the new project.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleOutput(String line, boolean terminated) {
+        if (callee != null) {
+            callee.handleOutput(line, terminated);
+        } else {
+            super.handleOutput(line, terminated);
+        }
+    }
+    
+    /**
      * Pass output sent to System.err to the new project.
      *
      * @since Ant 1.5
      */
     protected void handleErrorOutput(String line) {
         if (callee != null) {
             callee.handleErrorOutput(line);
         } else {
             super.handleErrorOutput(line);
         }
     }
     
+    /**
+     * Pass output sent to System.err to the new project.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (callee != null) {
+            callee.handleErrorOutput(line, terminated);
+        } else {
+            super.handleErrorOutput(line, terminated);
+        }
+    }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Java.java b/src/main/org/apache/tools/ant/taskdefs/Java.java
index 675eb6dd7..845a5f33b 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Java.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Java.java
@@ -1,507 +1,543 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
+ * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ExitException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.Environment;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 
 /**
  * Launcher for Java applications. Allows use of
  * the same JVM for the called application thus resulting in much
  * faster operation.
  *
  * @author Stefano Mazzocchi 
  *         <a href="mailto:stefano@apache.org">stefano@apache.org</a>
  * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
  *
  * @since Ant 1.1
  *
  * @ant.task category="java"
  */
 public class Java extends Task {
 
     private CommandlineJava cmdl = new CommandlineJava();
     private Environment env = new Environment();
     private boolean fork = false;
     private boolean newEnvironment = false;
     private File dir = null;
     private File out;
     private PrintStream outStream = null;
     private boolean failOnError = false;
     private boolean append = false;
     private Long timeout = null;
     
     /**
      * Do the execution.
      */
     public void execute() throws BuildException {
         File savedDir = dir;
 
         int err = -1;
         try {
             if ((err = executeJava()) != 0) { 
                 if (failOnError) {
                     throw new BuildException("Java returned: " + err, getLocation());
                 } else {
                     log("Java Result: " + err, Project.MSG_ERR);
                 }
             }
         } finally {
             dir = savedDir;
         }
     }
 
     /**
      * Do the execution and return a return code.
      *
      * @return the return code from the execute java class if it was
      * executed in a separate VM (fork = "yes").
      */
     public int executeJava() throws BuildException {
         String classname = cmdl.getClassname();
         if (classname == null && cmdl.getJar() == null) {
             throw new BuildException("Classname must not be null.");
         }
 
         if (!fork && cmdl.getJar() != null){
             throw new BuildException("Cannot execute a jar in non-forked mode."
                                      + " Please set fork='true'. ");
         }
 
         if (fork) {
             log(cmdl.describeCommand(), Project.MSG_VERBOSE);
         } else {
             if (cmdl.getVmCommand().size() > 1) {
                 log("JVM args ignored when same JVM is used.", 
                     Project.MSG_WARN);
             }
             if (dir != null) {
                 log("Working directory ignored when same JVM is used.", 
                     Project.MSG_WARN);
             }
 
             if (newEnvironment || null != env.getVariables()) {
                 log("Changes to environment variables are ignored when same "
                     + "JVM is used.", Project.MSG_WARN);
             }
 
             log("Running in same VM " + cmdl.describeJavaCommand(), 
                 Project.MSG_VERBOSE);
         }
         
         try {
             if (fork) {
                 return run(cmdl.getCommandline());
             } else {
                 try {
                     run(cmdl);
                     return 0;
                 } catch (ExitException ex) {
                     return ex.getStatus();
                 }
             }
         } catch (BuildException e) {
             if (failOnError) {
                 throw e;
             } else {
                 log(e.getMessage(), Project.MSG_ERR);
                 return 0;
             }
         } catch (Throwable t) {
             if (failOnError) {
                 throw new BuildException(t);
             } else {
                 log(t.getMessage(), Project.MSG_ERR);
                 return 0;
             }
         }
     }
 
     /**
      * Set the classpath to be used when running the Java class
      * 
      * @param s an Ant Path object containing the classpath.
      */
     public void setClasspath(Path s) {
         createClasspath().append(s);
     }
     
     /**
      * Adds a path to the classpath.
      */
     public Path createClasspath() {
         return cmdl.createClasspath(getProject()).createPath();
     }
 
     /**
      * Classpath to use, by reference.
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * The location of the JAR file to execute.
      */
     public void setJar(File jarfile) throws BuildException {
         if (cmdl.getClassname() != null){
             throw new BuildException("Cannot use 'jar' and 'classname' "
                                      + "attributes in same command.");
         }
         cmdl.setJar(jarfile.getAbsolutePath());
     }
 
     /**
      * Sets the Java class to execute.
      */
     public void setClassname(String s) throws BuildException {
         if (cmdl.getJar() != null){
             throw new BuildException("Cannot use 'jar' and 'classname' "
                                      + "attributes in same command");
         }
         cmdl.setClassname(s);
     }
 
     /**
      * Deprecated: use nested arg instead.
      * Set the command line arguments for the class.
      * @ant.attribute ignore="true"
      */
     public void setArgs(String s) {
         log("The args attribute is deprecated. " +
             "Please use nested arg elements.",
             Project.MSG_WARN);
         cmdl.createArgument().setLine(s);
     }
 
     /**
      * Adds a command-line argument.
      */
     public Commandline.Argument createArg() {
         return cmdl.createArgument();
     }
 
     /**
      * If true, execute in a new VM.
      */
     public void setFork(boolean s) {
         this.fork = s;
     }
 
     /**
      * Set the command line arguments for the JVM.
      */
     public void setJvmargs(String s) {
         log("The jvmargs attribute is deprecated. " +
             "Please use nested jvmarg elements.",
             Project.MSG_WARN);
         cmdl.createVmArgument().setLine(s);
     }
         
     /**
      * Adds a JVM argument.
      */
     public Commandline.Argument createJvmarg() {
         return cmdl.createVmArgument();
     }
 
     /**
      * Set the command used to start the VM (only if not forking).
      */
     public void setJvm(String s) {
         cmdl.setVm(s);
     }
         
     /**
      * Adds a system property.
      */
     public void addSysproperty(Environment.Variable sysp) {
         cmdl.addSysproperty(sysp);
     }
 
     /**
      * If true, then fail if the command exits with a
      * returncode other than 0
      */
     public void setFailonerror(boolean fail) {
         failOnError = fail;
     }
 
     /**
      * The working directory of the process
      */
     public void setDir(File d) {
         this.dir = d;
     }
 
     /**
      * File the output of the process is redirected to.
      */
     public void setOutput(File out) {
         this.out = out;
     }
 
     /**
      * Corresponds to -mx or -Xmx depending on VM version.
      */
     public void setMaxmemory(String max){
         cmdl.setMaxmemory(max);
     }
 
     /**
      * Sets the JVM version.
      * @param value JVM version
      */
     public void setJVMVersion(String value) {
         cmdl.setVmversion(value);
     }
     
     /**
      * Adds an environment variable.
      *
      * <p>Will be ignored if we are not forking a new VM.
      *
      * @since Ant 1.5
      */
     public void addEnv(Environment.Variable var) {
         env.addVariable(var);
     }
 
     /**
      * If true, use a completely new environment.
      *
      * <p>Will be ignored if we are not forking a new VM.
      *
      * @since Ant 1.5
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * If true, append output to existing file.
      *
      * @since Ant 1.5
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
     /**
      * Timeout in milliseconds after which the process will be killed.
      *
      * @since Ant 1.5
      */
     public void setTimeout(Long value) {
         timeout = value;
     }
 
     /**
      * Pass output sent to System.out to specified output file.
      *
      * @since Ant 1.5
      */
     protected void handleOutput(String line) {
         if (outStream != null) {
             outStream.println(line);
         } else {
             super.handleOutput(line);
         }
     }
     
     /**
+     * Pass output sent to System.out to specified output file.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleOutput(String line, boolean terminated) {
+        if (outStream != null) {
+            if (terminated) {
+                outStream.println(line);
+            } else {
+                outStream.print(line);
+            }
+        } else {
+            super.handleOutput(line, terminated);
+        }
+    }
+    
+    /**
      * Pass output sent to System.err to specified output file.
      *
      * @since Ant 1.5
      */
     protected void handleErrorOutput(String line) {
         if (outStream != null) {
             outStream.println(line);
         } else {
             super.handleErrorOutput(line);
         }
     }
     
     /**
+     * Pass output sent to System.err to specified output file.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (outStream != null) {
+            if (terminated) {
+                outStream.println(line);
+            } else {
+                outStream.print(line);
+            }
+        } else {
+            super.handleErrorOutput(line, terminated);
+        }
+    }
+    
+    /**
      * Executes the given classname with the given arguments as it
      * was a command line application.
      */
     private void run(CommandlineJava command) throws BuildException {
         ExecuteJava exe = new ExecuteJava();
         exe.setJavaCommand(command.getJavaCommand());
         exe.setClasspath(command.getClasspath());
         exe.setSystemProperties(command.getSystemProperties());
         exe.setTimeout(timeout);
         if (out != null) {
             try {
                 outStream = 
                     new PrintStream(new FileOutputStream(out.getAbsolutePath(),
                                                          append));
                 exe.execute(getProject());
+                System.out.flush();
+                System.err.flush();
             } catch (IOException io) {
                 throw new BuildException(io, getLocation());
             } finally {
                 if (outStream != null) {
                     outStream.close();
                 }
             }
         } else {
             exe.execute(getProject());
         }
     }
 
     /**
      * Executes the given classname with the given arguments in a separate VM.
      */
     private int run(String[] command) throws BuildException {
         FileOutputStream fos = null;
         try {
             Execute exe = null;
             if (out == null) {
                 exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO,
                                                        Project.MSG_WARN), 
                                   createWatchdog());
             } else {
                 fos = new FileOutputStream(out.getAbsolutePath(), append);
                 exe = new Execute(new PumpStreamHandler(fos),
                                   createWatchdog());
             }
             
             exe.setAntRun(getProject());
             
             if (dir == null) {
                 dir = getProject().getBaseDir();
             } else if (!dir.exists() || !dir.isDirectory()) {
                 throw new BuildException(dir.getAbsolutePath()
                                          + " is not a valid directory",
                                          getLocation());
             }
             
             exe.setWorkingDirectory(dir);
             
             String[] environment = env.getVariables();
             if (environment != null) {
                 for (int i = 0; i < environment.length; i++) {
                     log("Setting environment variable: " + environment[i],
                         Project.MSG_VERBOSE);
                 }
             }
             exe.setNewenvironment(newEnvironment);
             exe.setEnvironment(environment);
 
             exe.setCommandline(command);
             try {
                 int rc = exe.execute();
                 if (exe.killedProcess()) {
                     log("Timeout: killed the sub-process", Project.MSG_WARN); 
                 }
                 return rc;
             } catch (IOException e) {
                 throw new BuildException(e, getLocation());
             }
         } catch (IOException io) {
             throw new BuildException(io, getLocation());
         } finally {
             if (fos != null) {
                 try {fos.close();} catch (IOException io) {}
             }
         }
     }
 
     /**
      * Executes the given classname with the given arguments as it
      * was a command line application.
      */
     protected void run(String classname, Vector args) throws BuildException {
         CommandlineJava cmdj = new CommandlineJava();
         cmdj.setClassname(classname);
         for (int i = 0; i < args.size(); i++) {
             cmdj.createArgument().setValue((String) args.elementAt(i));
         }
         run(cmdj);
     }
 
     /**
      * Clear out the arguments to this java task.
      */
     public void clearArgs() {
         cmdl.clearJavaArgs();
     }
 
     /**
      * Create the Watchdog to kill a runaway process.
      *
      * @since Ant 1.5
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         if (timeout == null) {
             return null;
         }
         return new ExecuteWatchdog(timeout.longValue());
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
index 79699e656..2afc74fd2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
@@ -1,968 +1,1003 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant.taskdefs.optional.junit;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.net.URL;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Vector;
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
 import org.apache.tools.ant.taskdefs.LogOutputStream;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.Environment;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestResult;
 
 /**
  * Runs JUnit tests.
  *
  * <p> JUnit is a framework to create unit test. It has been initially
  * created by Erich Gamma and Kent Beck.  JUnit can be found at <a
  * href="http://www.junit.org">http://www.junit.org</a>.
  *
  * <p> <code>JUnitTask</code> can run a single specific
  * <code>JUnitTest</code> using the <code>test</code> element.</p>
  * For example, the following target <code><pre>
  *   &lt;target name="test-int-chars" depends="jar-test"&gt;
  *       &lt;echo message="testing international characters"/&gt;
  *       &lt;junit printsummary="no" haltonfailure="yes" fork="false"&gt;
  *           &lt;classpath refid="classpath"/&gt;
  *           &lt;formatter type="plain" usefile="false" /&gt;
  *           &lt;test name="org.apache.ecs.InternationalCharTest" /&gt;
  *       &lt;/junit&gt;
  *   &lt;/target&gt;
  * </pre></code>
  * <p>runs a single junit test
  * (<code>org.apache.ecs.InternationalCharTest</code>) in the current
  * VM using the path with id <code>classpath</code> as classpath and
  * presents the results formatted using the standard
  * <code>plain</code> formatter on the command line.</p>
  *
  * <p> This task can also run batches of tests.  The
  * <code>batchtest</code> element creates a <code>BatchTest</code>
  * based on a fileset.  This allows, for example, all classes found in
  * directory to be run as testcases.</p>
  *
  * <p>For example,</p><code><pre>
  * &lt;target name="run-tests" depends="dump-info,compile-tests" if="junit.present"&gt;
  *   &lt;junit printsummary="no" haltonfailure="yes" fork="${junit.fork}"&gt;
  *     &lt;jvmarg value="-classic"/&gt;
  *     &lt;classpath refid="tests-classpath"/&gt;
  *     &lt;sysproperty key="build.tests" value="${build.tests}"/&gt;
  *     &lt;formatter type="brief" usefile="false" /&gt;
  *     &lt;batchtest&gt;
  *       &lt;fileset dir="${tests.dir}"&gt;
  *         &lt;include name="**&#047;*Test*" /&gt;
  *       &lt;/fileset&gt;
  *     &lt;/batchtest&gt;
  *   &lt;/junit&gt;
  * &lt;/target&gt;
  * </pre></code>
  * <p>this target finds any classes with a <code>test</code> directory
  * anywhere in their path (under the top <code>${tests.dir}</code>, of
  * course) and creates <code>JUnitTest</code>'s for each one.</p>
  *
  * <p> Of course, <code>&lt;junit&gt;</code> and
  * <code>&lt;batch&gt;</code> elements can be combined for more
  * complex tests. For an example, see the ant <code>build.xml</code>
  * target <code>run-tests</code> (the second example is an edited
  * version).</p>
  *
  * <p> To spawn a new Java VM to prevent interferences between
  * different testcases, you need to enable <code>fork</code>.  A
  * number of attributes and elements allow you to set up how this JVM
  * runs.
  * @author Thomas Haas
  * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
  * @author <a href="mailto:sbailliez@imediation.com">Stephane Bailliez</a>
  * @author <a href="mailto:Gerrit.Riessen@web.de">Gerrit Riessen</a>
  * @author <a href="mailto:ehatcher@apache.org">Erik Hatcher</a>
  * @author <a href="mailto:martijn@kruithof.xs4all.nl">Martijn Kruithof></a>
  *
  * @version $Revision$
  *
  * @since Ant 1.2
  *
  * @see JUnitTest
  * @see BatchTest
  */
 public class JUnitTask extends Task {
 
     private CommandlineJava commandline = new CommandlineJava();
     private Vector tests = new Vector();
     private Vector batchTests = new Vector();
     private Vector formatters = new Vector();
     private File dir = null;
 
     private Integer timeout = null;
     private boolean summary = false;
     private String summaryValue = "";
     private JUnitTestRunner runner = null;
 
     private boolean newEnvironment = false;
     private Environment env = new Environment();
 
     private boolean includeAntRuntime = true;
     private Path antRuntimeClasses = null;
 
     private boolean showOutput = false;
 
     /**
      * If true, smartly filter the stack frames of
      * JUnit errors and failures before reporting them.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test) however it can possibly be overridden by their
      * own properties.</p>
      * @param value <tt>false</tt> if it should not filter, otherwise
      * <tt>true<tt>
      *
      * @since Ant 1.5
      */
     public void setFiltertrace(boolean value) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setFiltertrace(value);
         }
     }
 
     /**
      * If true, stop the build process when there is an error in a test.
      * This property is applied on all BatchTest (batchtest) and JUnitTest
      * (test) however it can possibly be overridden by their own
      * properties.
      * @param value <tt>true</tt> if it should halt, otherwise
      * <tt>false</tt>
      *
      * @since Ant 1.2
      */
     public void setHaltonerror(boolean value) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setHaltonerror(value);
         }
     }
 
     /**
      * Property to set to "true" if there is a error in a test.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test), however, it can possibly be overriden by
      * their own properties.</p>
      * @param propertyName the name of the property to set in the
      * event of an error.
      *
      * @since Ant 1.4
      */
     public void setErrorProperty(String propertyName) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setErrorProperty(propertyName);
         }
     }
 
     /**
      * If true, stop the build process if a test fails
      * (errors are considered failures as well).
      * This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test) however it can possibly be overridden by their
      * own properties.
      * @param value <tt>true</tt> if it should halt, otherwise
      * <tt>false</tt>
      *
      * @since Ant 1.2
      */
     public void setHaltonfailure(boolean value) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setHaltonfailure(value);
         }
     }
 
     /**
      * Property to set to "true" if there is a failure in a test.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test), however, it can possibly be overriden by
      * their own properties.</p>
      * @param propertyName the name of the property to set in the
      * event of an failure.
      *
      * @since Ant 1.4
      */
     public void setFailureProperty(String propertyName) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setFailureProperty(propertyName);
         }
     }
 
     /**
      * If true, JVM should be forked for each test.
      *
      * <p>It avoids interference between testcases and possibly avoids
      * hanging the build.  this property is applied on all BatchTest
      * (batchtest) and JUnitTest (test) however it can possibly be
      * overridden by their own properties.</p>
      * @param value <tt>true</tt> if a JVM should be forked, otherwise
      * <tt>false</tt>
      * @see #setTimeout
      *
      * @since Ant 1.2
      */
     public void setFork(boolean value) {
         Enumeration enum = allTests();
         while (enum.hasMoreElements()) {
             BaseTest test = (BaseTest) enum.nextElement();
             test.setFork(value);
         }
     }
 
     /**
      * If true, print one-line statistics for each test, or "withOutAndErr"
      * to also show standard output and error.
      *
      * Can take the values on, off, and withOutAndErr.
      * @param value <tt>true</tt> to print a summary,
      * <tt>withOutAndErr</tt> to include the test&apos;s output as
      * well, <tt>false</tt> otherwise.
      * @see SummaryJUnitResultFormatter
      *
      * @since Ant 1.2
      */
     public void setPrintsummary(SummaryAttribute value) {
         summaryValue = value.getValue();
         summary = value.asBoolean();
     }
 
     /**
      * Print summary enumeration values.
      */
     public static class SummaryAttribute extends EnumeratedAttribute {
         public String[] getValues() {
             return new String[] {"true", "yes", "false", "no",
                                  "on", "off", "withOutAndErr"};
         }
 
         public boolean asBoolean() {
             String value = getValue();
             return "true".equals(value)
                 || "on".equals(value)
                 || "yes".equals(value)
                 || "withOutAndErr".equals(value);
         }
     }
 
     /**
      * Set the timeout value (in milliseconds).
      *
      * <p>If the test is running for more than this value, the test
      * will be canceled. (works only when in 'fork' mode).</p>
      * @param value the maximum time (in milliseconds) allowed before
      * declaring the test as 'timed-out'
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setTimeout(Integer value) {
         timeout = value;
     }
 
     /**
      * Set the maximum memory to be used by all forked JVMs.
      * @param   max     the value as defined by <tt>-mx</tt> or <tt>-Xmx</tt>
      *                  in the java command line options.
      *
      * @since Ant 1.2
      */
     public void setMaxmemory(String max) {
         commandline.setMaxmemory(max);
     }
 
     /**
      * The command used to invoke the Java Virtual Machine,
      * default is 'java'. The command is resolved by
      * java.lang.Runtime.exec(). Ignored if fork is disabled.
      *
      * @param   value   the new VM to use instead of <tt>java</tt>
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setJvm(String value) {
         commandline.setVm(value);
     }
 
     /**
      * Adds a JVM argument; ignored if not forking.
      *
      * @return create a new JVM argument so that any argument can be
      * passed to the JVM.
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public Commandline.Argument createJvmarg() {
         return commandline.createVmArgument();
     }
 
     /**
      * The directory to invoke the VM in. Ignored if no JVM is forked.
      * @param   dir     the directory to invoke the JVM from.
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setDir(File dir) {
         this.dir = dir;
     }
 
     /**
      * Adds a system property that tests can access.
      * This might be useful to tranfer Ant properties to the
      * testcases when JVM forking is not enabled.
      *
      * @since Ant 1.3
      */
     public void addSysproperty(Environment.Variable sysp) {
         commandline.addSysproperty(sysp);
     }
 
     /**
      * Adds path to classpath used for tests.
      *
      * @since Ant 1.2
      */
     public Path createClasspath() {
         return commandline.createClasspath(getProject()).createPath();
     }
 
     /**
      * Adds an environment variable; used when forking.
      *
      * <p>Will be ignored if we are not forking a new VM.</p>
      *
      * @since Ant 1.5
      */
     public void addEnv(Environment.Variable var) {
         env.addVariable(var);
     }
 
     /**
      * If true, use a new environment when forked.
      *
      * <p>Will be ignored if we are not forking a new VM.</p>
      *
      * @since Ant 1.5
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Add a new single testcase.
      * @param   test    a new single testcase
      * @see JUnitTest
      *
      * @since Ant 1.2
      */
     public void addTest(JUnitTest test) {
         tests.addElement(test);
     }
 
     /**
      * Adds a set of tests based on pattern matching.
      *
      * @return  a new instance of a batch test.
      * @see BatchTest
      *
      * @since Ant 1.2
      */
     public BatchTest createBatchTest() {
         BatchTest test = new BatchTest(getProject());
         batchTests.addElement(test);
         return test;
     }
 
     /**
      * Add a new formatter to all tests of this task.
      *
      * @since Ant 1.2
      */
     public void addFormatter(FormatterElement fe) {
         formatters.addElement(fe);
     }
 
     /**
      * If true, include ant.jar, optional.jar and junit.jar in the forked VM.
      *
      * @since Ant 1.5
      */
     public void setIncludeantruntime(boolean b) {
         includeAntRuntime = b;
     }
 
     /**
      * If true, send any output generated by tests to Ant's logging system
      * as well as to the formatters.
      * By default only the formatters receive the output.
      *
      * <p>Output will always be passed to the formatters and not by
      * shown by default.  This option should for example be set for
      * tests that are interactive and prompt the user to do
      * something.</p>
      *
      * @since Ant 1.5
      */
     public void setShowOutput(boolean showOutput) {
         this.showOutput = showOutput;
     }
 
     /**
      * Creates a new JUnitRunner and enables fork of a new Java VM.
      *
      * @since Ant 1.2
      */
     public JUnitTask() throws Exception {
         commandline
             .setClassname("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner");
     }
 
     /**
      * Adds the jars or directories containing Ant, this task and
      * JUnit to the classpath - this should make the forked JVM work
      * without having to specify them directly.
      *
      * @since Ant 1.4
      */
     public void init() {
         antRuntimeClasses = new Path(getProject());
         addClasspathEntry("/junit/framework/TestCase.class");
         addClasspathEntry("/org/apache/tools/ant/Task.class");
         addClasspathEntry("/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.class");
     }
 
     /**
      * Runs the testcase.
      *
      * @since Ant 1.2
      */
     public void execute() throws BuildException {
         Enumeration list = getIndividualTests();
         while (list.hasMoreElements()) {
             JUnitTest test = (JUnitTest) list.nextElement();
             if (test.shouldRun(getProject())) {
                 execute(test);
             }
         }
     }
 
     /**
      * Run the tests.
      */
     protected void execute(JUnitTest arg) throws BuildException {
         JUnitTest test = (JUnitTest) arg.clone();
         // set the default values if not specified
         //@todo should be moved to the test class instead.
         if (test.getTodir() == null) {
             test.setTodir(getProject().resolveFile("."));
         }
 
         if (test.getOutfile() == null) {
             test.setOutfile("TEST-" + test.getName());
         }
 
         // execute the test and get the return code
         int exitValue = JUnitTestRunner.ERRORS;
         boolean wasKilled = false;
         if (!test.getFork()) {
             exitValue = executeInVM(test);
         } else {
             ExecuteWatchdog watchdog = createWatchdog();
             exitValue = executeAsForked(test, watchdog);
             // null watchdog means no timeout, you'd better not check with null
             if (watchdog != null) {
                 wasKilled = watchdog.killedProcess();
             }
         }
 
         // if there is an error/failure and that it should halt, stop
         // everything otherwise just log a statement
         boolean errorOccurredHere = exitValue == JUnitTestRunner.ERRORS;
         boolean failureOccurredHere = exitValue != JUnitTestRunner.SUCCESS;
         if (errorOccurredHere || failureOccurredHere) {
             if ((errorOccurredHere && test.getHaltonerror())
                 || (failureOccurredHere && test.getHaltonfailure())) {
                 throw new BuildException("Test " + test.getName() + " failed"
                     + (wasKilled ? " (timeout)" : ""), getLocation());
             } else {
                 log("TEST " + test.getName() + " FAILED"
                     + (wasKilled ? " (timeout)" : ""), Project.MSG_ERR);
                 if (errorOccurredHere && test.getErrorProperty() != null) {
                     getProject().setNewProperty(test.getErrorProperty(), "true");
                 }
                 if (failureOccurredHere && test.getFailureProperty() != null) {
                     getProject().setNewProperty(test.getFailureProperty(), "true");
                 }
             }
         }
     }
 
     /**
      * Execute a testcase by forking a new JVM. The command will block until
      * it finishes. To know if the process was destroyed or not, use the
      * <tt>killedProcess()</tt> method of the watchdog class.
      * @param  test       the testcase to execute.
      * @param  watchdog   the watchdog in charge of cancelling the test if it
      * exceeds a certain amount of time. Can be <tt>null</tt>, in this case
      * the test could probably hang forever.
      */
     private int executeAsForked(JUnitTest test, ExecuteWatchdog watchdog)
         throws BuildException {
 
         CommandlineJava cmd = (CommandlineJava) commandline.clone();
 
         cmd.setClassname("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner");
         cmd.createArgument().setValue(test.getName());
         cmd.createArgument().setValue("filtertrace=" + test.getFiltertrace());
         cmd.createArgument().setValue("haltOnError=" + test.getHaltonerror());
         cmd.createArgument().setValue("haltOnFailure="
                                       + test.getHaltonfailure());
         if (includeAntRuntime) {
             log("Implicitly adding " + antRuntimeClasses + " to CLASSPATH",
                 Project.MSG_VERBOSE);
             cmd.createClasspath(getProject()).createPath()
                 .append(antRuntimeClasses);
         }
 
         if (summary) {
             log("Running " + test.getName(), Project.MSG_INFO);
             cmd.createArgument()
                 .setValue("formatter=org.apache.tools.ant.taskdefs.optional.junit.SummaryJUnitResultFormatter");
         }
 
         cmd.createArgument().setValue("showoutput="
                                       + String.valueOf(showOutput));
 
         StringBuffer formatterArg = new StringBuffer(128);
         final FormatterElement[] feArray = mergeFormatters(test);
         for (int i = 0; i < feArray.length; i++) {
             FormatterElement fe = feArray[i];
             formatterArg.append("formatter=");
             formatterArg.append(fe.getClassname());
             File outFile = getOutput(fe, test);
             if (outFile != null) {
                 formatterArg.append(",");
                 formatterArg.append(outFile);
             }
             cmd.createArgument().setValue(formatterArg.toString());
             formatterArg.setLength(0);
         }
 
         // Create a temporary file to pass the Ant properties to the
         // forked test
         File propsFile =
             FileUtils.newFileUtils().createTempFile("junit", ".properties",
                                                     getProject().getBaseDir());
         cmd.createArgument().setValue("propsfile="
                                       + propsFile.getAbsolutePath());
         Hashtable p = getProject().getProperties();
         Properties props = new Properties();
         for (Enumeration enum = p.keys(); enum.hasMoreElements();) {
             Object key = enum.nextElement();
             props.put(key, p.get(key));
         }
         try {
             FileOutputStream outstream = new FileOutputStream(propsFile);
             props.save(outstream, "Ant JUnitTask generated properties file");
             outstream.close();
         } catch (java.io.IOException e) {
             propsFile.delete();
             throw new BuildException("Error creating temporary properties "
                                      + "file.", e, getLocation());
         }
 
         Execute execute = new Execute(new LogStreamHandler(this,
                                                            Project.MSG_INFO,
                                                            Project.MSG_WARN),
                                       watchdog);
         execute.setCommandline(cmd.getCommandline());
         execute.setAntRun(getProject());
         if (dir != null) {
             execute.setWorkingDirectory(dir);
         }
 
         String[] environment = env.getVariables();
         if (environment != null) {
             for (int i = 0; i < environment.length; i++) {
                 log("Setting environment variable: " + environment[i],
                     Project.MSG_VERBOSE);
             }
         }
         execute.setNewenvironment(newEnvironment);
         execute.setEnvironment(environment);
 
         log(cmd.describeCommand(), Project.MSG_VERBOSE);
         int retVal;
         try {
             retVal = execute.execute();
         } catch (IOException e) {
             throw new BuildException("Process fork failed.", e, getLocation());
         } finally {
             if (watchdog != null && watchdog.killedProcess()) {
                 logTimeout(feArray, test);
             }
 
             if (!propsFile.delete()) {
                 throw new BuildException("Could not delete temporary "
                                          + "properties file.");
             }
         }
 
         return retVal;
     }
 
 
     /**
      * Pass output sent to System.out to the TestRunner so it can
      * collect ot for the formatters.
      *
      * @since Ant 1.5
      */
     protected void handleOutput(String line) {
         if (runner != null) {
             runner.handleOutput(line);
             if (showOutput) {
                 super.handleOutput(line);
             }
         } else {
             super.handleOutput(line);
         }
     }
 
     /**
+     * Pass output sent to System.out to the TestRunner so it can
+     * collect ot for the formatters.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleOutput(String line, boolean terminated) {
+        if (runner != null) {
+            runner.handleOutput(line, terminated);
+            if (showOutput) {
+                super.handleOutput(line, terminated);
+            }
+        } else {
+            super.handleOutput(line, terminated);
+        }
+    }
+
+    /**
      * Pass output sent to System.err to the TestRunner so it can
      * collect ot for the formatters.
      *
      * @since Ant 1.5
      */
     protected void handleErrorOutput(String line) {
         if (runner != null) {
             runner.handleErrorOutput(line);
             if (showOutput) {
                 super.handleErrorOutput(line);
             }
         } else {
             super.handleErrorOutput(line);
         }
     }
 
+
+    /**
+     * Pass output sent to System.err to the TestRunner so it can
+     * collect ot for the formatters.
+     *
+     * @since Ant 1.6
+     */
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (runner != null) {
+            runner.handleErrorOutput(line, terminated);
+            if (showOutput) {
+                super.handleErrorOutput(line, terminated);
+            }
+        } else {
+            super.handleErrorOutput(line, terminated);
+        }
+    }
+
     // in VM is not very nice since it could probably hang the
     // whole build. IMHO this method should be avoided and it would be best
     // to remove it in future versions. TBD. (SBa)
 
     /**
      * Execute inside VM.
      */
     private int executeInVM(JUnitTest arg) throws BuildException {
         JUnitTest test = (JUnitTest) arg.clone();
         test.setProperties(getProject().getProperties());
         if (dir != null) {
             log("dir attribute ignored if running in the same VM",
                 Project.MSG_WARN);
         }
 
         if (newEnvironment || null != env.getVariables()) {
             log("Changes to environment variables are ignored if running in "
                 + "the same VM.", Project.MSG_WARN);
         }
 
         CommandlineJava.SysProperties sysProperties =
             commandline.getSystemProperties();
         if (sysProperties != null) {
             sysProperties.setSystem();
         }
         AntClassLoader cl = null;
         try {
             log("Using System properties " + System.getProperties(),
                 Project.MSG_VERBOSE);
             Path userClasspath = commandline.getClasspath();
             Path classpath = userClasspath == null
                                               ? null
                                               : (Path) userClasspath.clone();
             if (classpath != null) {
                 if (includeAntRuntime) {
                     log("Implicitly adding " + antRuntimeClasses
                         + " to CLASSPATH", Project.MSG_VERBOSE);
                     classpath.append(antRuntimeClasses);
                 }
 
                 cl = getProject().createClassLoader(classpath);
                 cl.setParentFirst(false);
                 cl.addJavaLibraries();
                 log("Using CLASSPATH " + cl.getClasspath(),
                     Project.MSG_VERBOSE);
 
                 // make sure the test will be accepted as a TestCase
                 cl.addSystemPackageRoot("junit");
                 // will cause trouble in JDK 1.1 if omitted
                 cl.addSystemPackageRoot("org.apache.tools.ant");
                 cl.setThreadContextLoader();
             }
             runner = new JUnitTestRunner(test, test.getHaltonerror(),
                                          test.getFiltertrace(),
                                          test.getHaltonfailure(), cl);
             if (summary) {
                 log("Running " + test.getName(), Project.MSG_INFO);
 
                 SummaryJUnitResultFormatter f =
                     new SummaryJUnitResultFormatter();
                 f.setWithOutAndErr("withoutanderr"
                                    .equalsIgnoreCase(summaryValue));
                 f.setOutput(getDefaultOutput());
                 runner.addFormatter(f);
             }
 
             final FormatterElement[] feArray = mergeFormatters(test);
             for (int i = 0; i < feArray.length; i++) {
                 FormatterElement fe = feArray[i];
                 File outFile = getOutput(fe, test);
                 if (outFile != null) {
                     fe.setOutfile(outFile);
                 } else {
                     fe.setOutput(getDefaultOutput());
                 }
                 runner.addFormatter(fe.createFormatter());
             }
 
             runner.run();
             return runner.getRetCode();
         } finally{
             if (sysProperties != null) {
                 sysProperties.restoreSystem();
             }
             if (cl != null) {
                 cl.resetThreadContextLoader();
             }
         }
     }
 
     /**
      * @return <tt>null</tt> if there is a timeout value, otherwise the
      * watchdog instance.
      *
      * @since Ant 1.2
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         if (timeout == null){
             return null;
         }
         return new ExecuteWatchdog(timeout.intValue());
     }
 
     /**
      * Get the default output for a formatter.
      *
      * @since Ant 1.3
      */
     protected OutputStream getDefaultOutput(){
         return new LogOutputStream(this, Project.MSG_INFO);
     }
 
     /**
      * Merge all individual tests from the batchtest with all individual tests
      * and return an enumeration over all <tt>JUnitTest</tt>.
      *
      * @since Ant 1.3
      */
     protected Enumeration getIndividualTests(){
         final int count = batchTests.size();
         final Enumeration[] enums = new Enumeration[ count + 1];
         for (int i = 0; i < count; i++) {
             BatchTest batchtest = (BatchTest) batchTests.elementAt(i);
             enums[i] = batchtest.elements();
         }
         enums[enums.length - 1] = tests.elements();
         return Enumerations.fromCompound(enums);
     }
 
     /**
      * @since Ant 1.3
      */
     protected Enumeration allTests() {
         Enumeration[] enums = { tests.elements(), batchTests.elements() };
         return Enumerations.fromCompound(enums);
     }
 
     /**
      * @since Ant 1.3
      */
     private FormatterElement[] mergeFormatters(JUnitTest test){
         Vector feVector = (Vector) formatters.clone();
         test.addFormattersTo(feVector);
         FormatterElement[] feArray = new FormatterElement[feVector.size()];
         feVector.copyInto(feArray);
         return feArray;
     }
 
     /**
      * If the formatter sends output to a file, return that file.
      * null otherwise.
      *
      * @since Ant 1.3
      */
     protected File getOutput(FormatterElement fe, JUnitTest test){
         if (fe.getUseFile()) {
             String filename = test.getOutfile() + fe.getExtension();
             File destFile = new File(test.getTodir(), filename);
             String absFilename = destFile.getAbsolutePath();
             return getProject().resolveFile(absFilename);
         }
         return null;
     }
 
     /**
      * Search for the given resource and add the directory or archive
      * that contains it to the classpath.
      *
      * <p>Doesn't work for archives in JDK 1.1 as the URL returned by
      * getResource doesn't contain the name of the archive.</p>
      *
      * @since Ant 1.4
      */
     protected void addClasspathEntry(String resource) {
         URL url = getClass().getResource(resource);
         if (url != null) {
             String u = url.toString();
             if (u.startsWith("jar:file:")) {
                 int pling = u.indexOf("!");
                 String jarName = u.substring(9, pling);
                 log("Found " + jarName, Project.MSG_DEBUG);
                 antRuntimeClasses.createPath()
                     .setLocation(new File((new File(jarName))
                                           .getAbsolutePath()));
             } else if (u.startsWith("file:")) {
                 int tail = u.indexOf(resource);
                 String dirName = u.substring(5, tail);
                 log("Found " + dirName, Project.MSG_DEBUG);
                 antRuntimeClasses.createPath()
                     .setLocation(new File((new File(dirName))
                                           .getAbsolutePath()));
             } else {
                 log("Don\'t know how to handle resource URL " + u,
                     Project.MSG_DEBUG);
             }
         } else {
             log("Couldn\'t find " + resource, Project.MSG_DEBUG);
         }
     }
 
     /**
      * Take care that some output is produced in report files if the
      * watchdog kills the test.
      *
      * @since Ant 1.5.2
      */
 
     private void logTimeout(FormatterElement[] feArray, JUnitTest test) {
         for (int i = 0; i < feArray.length; i++) {
             FormatterElement fe = feArray[i];
             File outFile = getOutput(fe, test);
             JUnitResultFormatter formatter = fe.createFormatter();
             if (outFile != null && formatter != null) {
                 try {
                     OutputStream out = new FileOutputStream(outFile);
                     formatter.setOutput(out);
                     formatter.startTestSuite(test);
                     test.setCounts(0,0,1);
                     Test t = new Test() {
                         public int countTestCases() { return 0; }
                         public void run(TestResult r) {
                             throw new AssertionFailedError("Timeout occurred");
                         }
                     };
                     formatter.startTest(t);
                     formatter
                         .addError(t,
                                   new AssertionFailedError("Timeout occurred"));
 
                     formatter.endTestSuite(test);
                 } catch (IOException e) {}
             }
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.java b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.java
index d79467171..59bb7783d 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.java
@@ -1,615 +1,635 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "The Jakarta Project", "Ant", and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant.taskdefs.optional.junit;
 
 import java.io.BufferedReader;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.lang.reflect.Method;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Vector;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestListener;
 import junit.framework.TestResult;
 import junit.framework.TestSuite;
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.StringUtils;
 
 /**
  * Simple Testrunner for JUnit that runs all tests of a testsuite.
  *
  * <p>This TestRunner expects a name of a TestCase class as its
  * argument. If this class provides a static suite() method it will be
  * called and the resulting Test will be run. So, the signature should be 
  * <pre><code>
  *     public static junit.framework.Test suite()
  * </code></pre>
  *
  * <p> If no such method exists, all public methods starting with
  * "test" and taking no argument will be run.
  *
  * <p> Summary output is generated at the end. 
  *
  * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
  * @author <a href="mailto:ehatcher@apache.org">Erik Hatcher</a>
  *
  * @since Ant 1.2
  */
 
 public class JUnitTestRunner implements TestListener {
 
     /**
      * No problems with this test.
      */
     public static final int SUCCESS = 0;
 
     /**
      * Some tests failed.
      */
     public static final int FAILURES = 1;
 
     /**
      * An error occurred.
      */
     public static final int ERRORS = 2;
 
     /**
      * Holds the registered formatters.
      */
     private Vector formatters = new Vector();
 
     /**
      * Collects TestResults.
      */
     private TestResult res;
 
     /**
      * Do we filter junit.*.* stack frames out of failure and error exceptions.
      */
     private static boolean filtertrace = true;
     
     /**
      * Do we send output to System.out/.err in addition to the formatters?
      */
     private boolean showOutput = false;
 
     private static final String[] DEFAULT_TRACE_FILTERS = new String[] {
                 "junit.framework.TestCase",
                 "junit.framework.TestResult",
                 "junit.framework.TestSuite",
                 "junit.framework.Assert.", // don't filter AssertionFailure
                 "junit.swingui.TestRunner",
                 "junit.awtui.TestRunner",
                 "junit.textui.TestRunner",
                 "java.lang.reflect.Method.invoke(",
                 "org.apache.tools.ant."
         };
 
     
     /**
      * Do we stop on errors.
      */
     private boolean haltOnError = false;
 
     /**
      * Do we stop on test failures.
      */
     private boolean haltOnFailure = false;
 
     /**
      * The corresponding testsuite.
      */
     private Test suite = null;
 
     /**
      * Exception caught in constructor.
      */
     private Exception exception;
 
     /**
      * Returncode
      */
     private int retCode = SUCCESS;
 
     /**
      * The TestSuite we are currently running.
      */
     private JUnitTest junitTest;
 
     /** output written during the test */
     private PrintStream systemError;
     
     /** Error output during the test */
     private PrintStream systemOut;    
     
     /** is this runner running in forked mode? */
     private boolean forked = false;
 
     /**
      * Constructor for fork=true or when the user hasn't specified a
      * classpath.  
      */
     public JUnitTestRunner(JUnitTest test, boolean haltOnError, 
                            boolean filtertrace, boolean haltOnFailure) {
         this(test, haltOnError, filtertrace, haltOnFailure, false);
     }
 
     /**
      * Constructor for fork=true or when the user hasn't specified a
      * classpath.  
      */
     public JUnitTestRunner(JUnitTest test, boolean haltOnError, 
                            boolean filtertrace, boolean haltOnFailure,
                            boolean showOutput) {
         this(test, haltOnError, filtertrace, haltOnFailure, showOutput, null);
     }
 
     /**
      * Constructor to use when the user has specified a classpath.
      */
     public JUnitTestRunner(JUnitTest test, boolean haltOnError, 
                            boolean filtertrace, boolean haltOnFailure, 
                            ClassLoader loader) {
         this(test, haltOnError, filtertrace, haltOnFailure, false, loader);
     }
 
     /**
      * Constructor to use when the user has specified a classpath.
      */
     public JUnitTestRunner(JUnitTest test, boolean haltOnError, 
                            boolean filtertrace, boolean haltOnFailure, 
                            boolean showOutput, ClassLoader loader) {
         this.filtertrace = filtertrace;
         this.junitTest = test;
         this.haltOnError = haltOnError;
         this.haltOnFailure = haltOnFailure;
         this.showOutput = showOutput;
 
         try {
             Class testClass = null;
             if (loader == null) {
                 testClass = Class.forName(test.getName());
             } else {
                 testClass = loader.loadClass(test.getName());
                 AntClassLoader.initializeClass(testClass);
             }
             
             Method suiteMethod = null;
             try {
                 // check if there is a suite method
                 suiteMethod = testClass.getMethod("suite", new Class[0]);
             } catch (Exception e) {
                 // no appropriate suite method found. We don't report any
                 // error here since it might be perfectly normal. We don't
                 // know exactly what is the cause, but we're doing exactly
                 // the same as JUnit TestRunner do. We swallow the exceptions.
             }
             if (suiteMethod != null){
                 // if there is a suite method available, then try
                 // to extract the suite from it. If there is an error
                 // here it will be caught below and reported.
                 suite = (Test) suiteMethod.invoke(null, new Class[0]);
             } else {
                 // try to extract a test suite automatically
                 // this will generate warnings if the class is no suitable Test
                 suite = new TestSuite(testClass);
             }
             
         } catch (Exception e) {
             retCode = ERRORS;
             exception = e;
         }
     }
 
     public void run() {
         res = new TestResult();
         res.addListener(this);
         for (int i = 0; i < formatters.size(); i++) {
             res.addListener((TestListener) formatters.elementAt(i));
         }
 
         long start = System.currentTimeMillis();
 
         fireStartTestSuite();
         if (exception != null) { // had an exception in the constructor
             for (int i = 0; i < formatters.size(); i++) {
                 ((TestListener) formatters.elementAt(i)).addError(null, 
                                                                  exception);
             }
             junitTest.setCounts(1, 0, 1);
             junitTest.setRunTime(0);
         } else {
 
 
             ByteArrayOutputStream errStrm = new ByteArrayOutputStream();
             systemError = new PrintStream(errStrm);
             
             ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
             systemOut = new PrintStream(outStrm);
 
             PrintStream savedOut = null;
             PrintStream savedErr = null;
 
             if (forked) {
                 savedOut = System.out;
                 savedErr = System.err;
                 if (!showOutput) {
                     System.setOut(systemOut);
                     System.setErr(systemError);
                 } else {
                     System.setOut(new PrintStream(
                                       new TeeOutputStream(
                                           new OutputStream[] {savedOut, 
                                                               systemOut}
                                           )
                                       )
                                   );
                     System.setErr(new PrintStream(
                                       new TeeOutputStream(
                                           new OutputStream[] {savedErr, 
                                                               systemError}
                                           )
                                       )
                                   );
                 }
             }
             
 
             try {
                 suite.run(res);
             } finally {
                 if (savedOut != null) {
                     System.setOut(savedOut);
                 }
                 if (savedErr != null) {
                     System.setErr(savedErr);
                 }
                 
                 systemError.close();
                 systemError = null;
                 systemOut.close();
                 systemOut = null;
                 sendOutAndErr(new String(outStrm.toByteArray()),
                               new String(errStrm.toByteArray()));
 
                 junitTest.setCounts(res.runCount(), res.failureCount(), 
                                     res.errorCount());
                 junitTest.setRunTime(System.currentTimeMillis() - start);
             }
         }
         fireEndTestSuite();
 
         if (retCode != SUCCESS || res.errorCount() != 0) {
             retCode = ERRORS;
         } else if (res.failureCount() != 0) {
             retCode = FAILURES;
         }
     }
 
     /**
      * Returns what System.exit() would return in the standalone version.
      *
      * @return 2 if errors occurred, 1 if tests failed else 0.
      */
     public int getRetCode() {
         return retCode;
     }
 
     /**
      * Interface TestListener.
      *
      * <p>A new Test is started.
      */
     public void startTest(Test t) {}
 
     /**
      * Interface TestListener.
      *
      * <p>A Test is finished.
      */
     public void endTest(Test test) {}
 
     /**
      * Interface TestListener for JUnit &lt;= 3.4.
      *
      * <p>A Test failed.
      */
     public void addFailure(Test test, Throwable t) {
         if (haltOnFailure) {
             res.stop();
         }
     }
 
     /**
      * Interface TestListener for JUnit &gt; 3.4.
      *
      * <p>A Test failed.
      */
     public void addFailure(Test test, AssertionFailedError t) {
         addFailure(test, (Throwable) t);
     }
 
     /**
      * Interface TestListener.
      *
      * <p>An error occurred while running the test.
      */
     public void addError(Test test, Throwable t) {
         if (haltOnError) {
             res.stop();
         }
     }
 
     protected void handleOutput(String line) {
         if (systemOut != null) {
             systemOut.println(line);
         }
     }
     
     protected void handleErrorOutput(String line) {
         if (systemError != null) {
             systemError.println(line);
         }
     }
     
+    protected void handleOutput(String line, boolean terminated) {
+        if (systemOut != null) {
+            if (terminated) {
+                systemOut.println(line);
+            } else {
+                systemOut.print(line);
+            }
+        }
+    }
+    
+    protected void handleErrorOutput(String line, boolean terminated) {
+        if (systemError != null) {
+            if (terminated) {
+                systemError.println(line);
+            } else {
+                systemError.print(line);
+            }
+        }
+    }
+    
     private void sendOutAndErr(String out, String err) {
         for (int i = 0; i < formatters.size(); i++) {
             JUnitResultFormatter formatter = 
                 ((JUnitResultFormatter) formatters.elementAt(i));
             
             formatter.setSystemOutput(out);
             formatter.setSystemError(err);
         }
     }
 
     private void fireStartTestSuite() {
         for (int i = 0; i < formatters.size(); i++) {
             ((JUnitResultFormatter) formatters.elementAt(i))
                 .startTestSuite(junitTest);
         }
     }
 
     private void fireEndTestSuite() {
         for (int i = 0; i < formatters.size(); i++) {
             ((JUnitResultFormatter) formatters.elementAt(i))
                 .endTestSuite(junitTest);
         }
     }
 
     public void addFormatter(JUnitResultFormatter f) {
         formatters.addElement(f);
     }
 
     /**
      * Entry point for standalone (forked) mode.
      *
      * Parameters: testcaseclassname plus parameters in the format
      * key=value, none of which is required.
      *
      * <table cols="4" border="1">
      * <tr><th>key</th><th>description</th><th>default value</th></tr>
      *
      * <tr><td>haltOnError</td><td>halt test on
      * errors?</td><td>false</td></tr>
      *
      * <tr><td>haltOnFailure</td><td>halt test on
      * failures?</td><td>false</td></tr>
      *
      * <tr><td>formatter</td><td>A JUnitResultFormatter given as
      * classname,filename. If filename is ommitted, System.out is
      * assumed.</td><td>none</td></tr>
      *
      * <tr><td>showoutput</td><td>send output to System.err/.out as
      * well as to the formatters?</td><td>false</td></tr>
      *
      * </table> 
      */
     public static void main(String[] args) throws IOException {
         boolean haltError = false;
         boolean haltFail = false;
         boolean stackfilter = true;
         Properties props = new Properties();
         boolean showOut = false;
 
         if (args.length == 0) {
             System.err.println("required argument TestClassName missing");
             System.exit(ERRORS);
         }
 
         for (int i = 1; i < args.length; i++) {
             if (args[i].startsWith("haltOnError=")) {
                 haltError = Project.toBoolean(args[i].substring(12));
             } else if (args[i].startsWith("haltOnFailure=")) {
                 haltFail = Project.toBoolean(args[i].substring(14));
             } else if (args[i].startsWith("filtertrace=")) {
                 stackfilter = Project.toBoolean(args[i].substring(12));
             } else if (args[i].startsWith("formatter=")) {
                 try {
                     createAndStoreFormatter(args[i].substring(10));
                 } catch (BuildException be) {
                     System.err.println(be.getMessage());
                     System.exit(ERRORS);
                 }
             } else if (args[i].startsWith("propsfile=")) {
                 FileInputStream in = new FileInputStream(args[i]
                                                          .substring(10));
                 props.load(in);
                 in.close();
             } else if (args[i].startsWith("showoutput=")) {
                 showOut = Project.toBoolean(args[i].substring(11));
             }
         }
         
         JUnitTest t = new JUnitTest(args[0]);
         
         // Add/overlay system properties on the properties from the Ant project
         Hashtable p = System.getProperties();
         for (Enumeration enum = p.keys(); enum.hasMoreElements();) {
             Object key = enum.nextElement();
             props.put(key, p.get(key));
         }
         t.setProperties(props);
 
         JUnitTestRunner runner = new JUnitTestRunner(t, haltError, stackfilter,
                                                      haltFail, showOut);
         runner.forked = true;
         transferFormatters(runner);
         runner.run();
         System.exit(runner.getRetCode());
     }
 
     private static Vector fromCmdLine = new Vector();
 
     private static void transferFormatters(JUnitTestRunner runner) {
         for (int i = 0; i < fromCmdLine.size(); i++) {
             runner.addFormatter((JUnitResultFormatter) fromCmdLine
                                 .elementAt(i));
         }
     }
 
     /**
      * Line format is: formatter=<classname>(,<pathname>)?
      */
     private static void createAndStoreFormatter(String line)
         throws BuildException {
         FormatterElement fe = new FormatterElement();
         int pos = line.indexOf(',');
         if (pos == -1) {
             fe.setClassname(line);
         } else {
             fe.setClassname(line.substring(0, pos));
             fe.setOutfile(new File(line.substring(pos + 1)));
         }
         fromCmdLine.addElement(fe.createFormatter());
     }
     
     /**
      * Returns a filtered stack trace.
      * This is ripped out of junit.runner.BaseTestRunner.
      * Scott M. Stirling.
      */
     public static String getFilteredTrace(Throwable t) {
         String trace = StringUtils.getStackTrace(t);
         return JUnitTestRunner.filterStack(trace);
     }
 
     /**
      * Filters stack frames from internal JUnit and Ant classes
      */
     public static String filterStack(String stack) {
         if (!filtertrace) {
             return stack;
         }
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         StringReader sr = new StringReader(stack);
         BufferedReader br = new BufferedReader(sr);
 
         String line;
         try {
             while ((line = br.readLine()) != null) {
                 if (!filterLine(line)) {
                     pw.println(line);
                 }
             }
         } catch (Exception IOException) {
             return stack; // return the stack unfiltered
         }
         return sw.toString();
     }
 
     private static boolean filterLine(String line) {
         for (int i = 0; i < DEFAULT_TRACE_FILTERS.length; i++) {
             if (line.indexOf(DEFAULT_TRACE_FILTERS[i]) > 0) {
                 return true;
             }
         }
         return false;
     }
     
     /**
      * Helper class that sends output sent to multiple streams.
      *
      * @since Ant 1.5
      */
     private class TeeOutputStream extends OutputStream {
 
         private OutputStream[] outs;
 
         private TeeOutputStream(OutputStream[] outs) {
             this.outs = outs;
         }
 
         public void write(int b) throws IOException {
             for (int i = 0; i  < outs.length; i++) {
                 outs[i].write(b);
             }
         }
 
     }
 
 } // JUnitTestRunner
