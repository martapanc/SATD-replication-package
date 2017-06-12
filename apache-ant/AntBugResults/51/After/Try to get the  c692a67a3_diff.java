diff --git a/src/etc/testcases/taskdefs/ant.xml b/src/etc/testcases/taskdefs/ant.xml
index aa69d6488..dc570ab5b 100644
--- a/src/etc/testcases/taskdefs/ant.xml
+++ b/src/etc/testcases/taskdefs/ant.xml
@@ -1,217 +1,224 @@
 <?xml version="1.0"?>
 
 <project name="ant-test" basedir="." default="test1">
 
   <path id="inheritable">
     <pathelement path="${java.class.path}" />
   </path>
 
   <target name="cleanup">
     <delete file="test1.log" />
     <delete file="test2.log" />
     <delete file="ant/test3.log" />
     <delete file="ant/test4.log" />
   </target>
 
   <target name="all" depends="test1,test2,test3,test4"/>
 
   <target name="test1">
     <ant antfile="ant.xml" dir="." target="test1"/>
   </target>
 
   <target name="test2">
     <antcall/>
   </target>
 
   <target name="test3">
     <antcall target="test3"/>
   </target>
 
   <target name="test4">
     <antcall target=""/>
   </target>
 
   <target name="test4b">
     <antcall target="does-not-exist"/>
   </target>
 
   <target name="test5">
     <antcall target="dummy"/>
   </target>
 
   <target name="test6">
     <ant antfile="ant.xml" dir="." target="dummy"/>
   </target>
 
   <target name="dummy">
   </target>
 
   <target name="inheritBasedir">
     <ant antfile="ant/ant.xml" target="dummy" inheritAll="true" />
   </target>
 
   <target name="doNotInheritBasedir">
     <ant antfile="ant/ant.xml" target="dummy" inheritAll="false" />
   </target>
 
   <target name="explicitBasedir1">
     <ant antfile="taskdefs/ant/ant.xml" target="dummy" inheritAll="true" 
          dir=".." />
   </target>
 
   <target name="explicitBasedir2">
     <ant antfile="taskdefs/ant/ant.xml" target="dummy" inheritAll="false" 
          dir=".." />
   </target>
 
   <target name="tripleCall">
     <ant antfile="ant/ant.xml" target="callback" inheritAll="false" />
   </target>
 
   <target name="testInherit">
     <ant antfile="ant/references.xml" inheritRefs="true" target="dummy" />
   </target>
 
   <target name="testNoInherit">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy" />
   </target>
 
   <target name="testRename">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy">
       <reference refid="path" torefid="newpath" />
     </ant>
   </target>
 
   <target name="testLogfilePlacement">
     <ant antfile="ant.xml" target="dummy" output="test1.log"
          inheritall="false" />
     <ant antfile="ant.xml" target="dummy" output="test2.log" />
     <ant antfile="ant.xml" target="dummy" output="test3.log"
          inheritall="false" dir="ant" />
     <ant antfile="ant.xml" target="dummy" output="test4.log" 
          dir="ant" />
   </target>
 
   <target name="testRefid">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy">
       <property name="testprop" refid="inheritable" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-start">
     <property name="test" value="1" />
     <ant antfile="ant.xml"
          target="test-property-override-inheritall-level-2"
          inheritall="true">
       <property name="test" value="2" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-level-2">
     <property name="test" value="3" />
     <ant antfile="ant.xml"
          target="test-property-override-inheritall-level-3"
          inheritall="true">
       <property name="test" value="4" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-level-3">
     <property name="test" value="5" />
     <echo message="The value of test is ${test}" />
   </target>
 
   <target name="test-property-override-no-inheritall-start">
     <property name="test" value="1" />
     <ant antfile="ant.xml"
          target="test-property-override-no-inheritall-level-2"
          inheritall="false">
       <property name="test" value="2" />
     </ant>
   </target>
 
   <target name="test-property-override-no-inheritall-level-2">
     <property name="test" value="3" />
     <ant antfile="ant.xml"
          target="test-property-override-no-inheritall-level-3"
          inheritall="false">
       <property name="test" value="4" />
     </ant>
   </target>
 
   <target name="test-property-override-no-inheritall-level-3">
     <property name="test" value="5" />
     <echo message="The value of test is ${test}" />
   </target>
 
   <target name="test-propertyset">
     <property name="test1" value="1"/>
     <property name="test2" value="2"/>
     <propertyset id="set">
       <propertyref name="test1"/>
       <mapper type="glob" from="*" to="*.x"/>
     </propertyset>
     <ant antfile="ant.xml" target="echo-for-propertyset-test" 
          inheritall="false">
       <propertyset refid="set"/>
     </ant>
   </target>
 
   <target name="echo-for-propertyset-test">
     <echo>test1 is ${test1}</echo>
     <echo>test2 is ${test2}</echo>
     <echo>test1.x is ${test1.x}</echo>
   </target>
 
   <target name="infinite-loop-via-depends">
     <antcall target="dependent"/>
   </target>
 
   <target name="middleman" depends="infinite-loop-via-depends"/>
   <target name="dependent" depends="middleman"/>
   
   <target name="multi-same-property">
     <ant antfile="ant.xml" target="echo-for-multi-same">
       <property name="prop" value="one"/>
       <property name="prop" value="two"/>
     </ant>
   </target>
 
   <target name="echo-for-multi-same">
     <echo>prop is ${prop}</echo>
   </target>
 
   <target name="topleveltarget">
     <ant antfile="ant.topleveltest.xml"/>
   </target>
 
   <target name="multiple-property-file-children">
     <ant target="dummy" antfile="ant.xml">
       <property file="foo.properties"/>
       <property file="bar.properties"/>
     </ant>
   </target>
 
   <target name="blank-target">
     <ant antfile="ant.topleveltest.xml">
       <target name="" />
     </ant>
   </target>
 
   <target name="multiple-targets">
     <ant antfile="ant.xml">
       <target name="ta" />
       <target name="tb" />
       <target name="tc" />
     </ant>
   </target>
 
+  <target name="multiple-targets-2">
+    <ant antfile="ant.xml">
+      <target name="tb" />
+      <target name="da" />
+    </ant>
+  </target>
+
   <target name="ta"><echo>ta</echo></target>
   <target name="tb" depends="da,dc"><echo>tb</echo></target>
   <target name="tc" depends="db,dc"><echo>tc</echo></target>
 
   <target name="da"><echo>da</echo></target>
   <target name="db"><echo>db</echo></target>
   <target name="dc"><echo>dc</echo></target>
 
 </project>
diff --git a/src/etc/testcases/taskdefs/calltarget.xml b/src/etc/testcases/taskdefs/calltarget.xml
index 17c9f0574..479f653c9 100644
--- a/src/etc/testcases/taskdefs/calltarget.xml
+++ b/src/etc/testcases/taskdefs/calltarget.xml
@@ -1,76 +1,83 @@
 <?xml version="1.0"?>
 <project name ="calltarget-test" default="testinheritreffileset" basedir=".">
     <property name="tmp.dir" value="tmp.dir" />
     <target name="setup">
         <mkdir dir="${tmp.dir}"/>
     </target>
     <target name="cleanup">
         <delete dir="${tmp.dir}" quiet="true"/>
     </target>
     <target name="mytarget">
       <pathconvert property="myproperty" targetos="unix" refid="myfileset"/>
       <echo message="myproperty=${myproperty}"/>
     </target>
     <target name="testinheritreffileset">
     <!-- this testcase should show that the fileset defined here
     can be read in the called target -->
       <fileset dir="." id="myfileset">
         <include name="calltarget.xml"/>
       </fileset>
       <antcall target="mytarget" inheritrefs="true"/>
     </target>
     <target name="copytest2">
        <copy file="${tmp.dir}/copytest.in" toFile="${tmp.dir}/copytest1.out" overwrite="true">
           <filterset refid="foo"/>
        </copy>
     </target>
     <target name="testinheritreffilterset" depends="setup">
        <echo file="${tmp.dir}/copytest.in">@@foo@@</echo>
        <filterset id="foo" begintoken="@@" endtoken="@@">
           <filter token="foo" value="bar"/>
        </filterset>
        <antcall target="copytest2" inheritrefs="true"/>
        <copy file="${tmp.dir}/copytest.in" toFile="${tmp.dir}/copytest2.out" overwrite="true">
           <filterset refid="foo"/>
        </copy>
        <loadfile srcFile="${tmp.dir}/copytest2.out" property="copytest2"/>
        <loadfile srcFile="${tmp.dir}/copytest1.out" property="copytest1"/>
        <condition property="success">
            <equals arg1="${copytest1}" arg2="${copytest2}"/>
        </condition>
        <fail message="filterset not properly passed across by antcall" unless="success"/>
     </target>
 
     <property name="multi" value="DEFAULT"/>
     <target name="multi">
         <echo>multi is ${multi}</echo>
     </target>
     <target name="call-multi">
         <antcall target="multi">
             <param name="multi" value="SET"/>
         </antcall>
     </target>
 
     <target name="blank-target">
         <antcall>
             <target name="" />
         </antcall>
     </target>
 
     <target name="multiple-targets">
         <antcall>
             <target name="ta" />
             <target name="tb" />
             <target name="tc" />
         </antcall>
     </target>
 
+    <target name="multiple-targets-2">
+      <ant antfile="ant.xml">
+        <target name="tb" />
+        <target name="da" />
+      </ant>
+    </target>
+
     <target name="ta"><echo>ta</echo></target>
     <target name="tb" depends="da,dc"><echo>tb</echo></target>
     <target name="tc" depends="db,dc"><echo>tc</echo></target>
 
     <target name="da"><echo>da</echo></target>
     <target name="db"><echo>db</echo></target>
     <target name="dc"><echo>dc</echo></target>
 
 </project>
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 1f55cf08b..0a337939b 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -184,1945 +184,1976 @@ public class Project {
     private boolean keepGoingMode = false;
 
     /**
      * Sets the input handler
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
      * Get this project's input stream
      *
      * @return the InputStream instance in use by this Project instance to
      * read input
      */
     public InputStream getDefaultInputStream() {
         return defaultInputStream;
     }
 
     /**
      * Retrieves the current input handler.
      *
      * @return the InputHandler instance currently in place for the project
      *         instance.
      */
     public InputHandler getInputHandler() {
         return inputHandler;
     }
 
     /** Instance of a utility class to use for file operations. */
     private FileUtils fileUtils;
 
     /**
      * Flag which catches Listeners which try to use System.out or System.err
      */
     private boolean loggingMessage = false;
 
     /**
      * Creates a new Ant project.
      */
     public Project() {
         fileUtils = FileUtils.newFileUtils();
         inputHandler = new DefaultInputHandler();
     }
 
     /**
      * inits a sub project - used by taskdefs.Ant
      * @param subProject the subproject to initialize
      */
     public void initSubProject(Project subProject) {
         ComponentHelper.getComponentHelper(subProject)
             .initSubProject(ComponentHelper.getComponentHelper(this));
         subProject.setKeepGoingMode(this.isKeepGoingMode());
     }
 
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
 
         ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
 
         setSystemProperties();
     }
 
     /**
      * Factory method to create a class loader for loading classes
      *
      * @return an appropriate classloader
      */
     private AntClassLoader createClassLoader() {
         AntClassLoader loader = null;
         try {
             // 1.2+ - create advanced helper dynamically
             Class loaderClass
                     = Class.forName(ANTCLASSLOADER_JDK12);
             loader = (AntClassLoader) loaderClass.newInstance();
         } catch (Exception e) {
             log("Unable to create Class Loader: "
                     + e.getMessage(), Project.MSG_DEBUG);
         }
 
         if (loader == null) {
             loader = new AntClassLoader();
         }
 
         loader.setProject(this);
         return loader;
     }
 
     /**
      * Factory method to create a class loader for loading classes from
      * a given path
      *
      * @param path the path from which classes are to be loaded.
      *
      * @return an appropriate classloader
      */
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
     public synchronized void addBuildListener(BuildListener listener) {
         // create a new Vector to avoid ConcurrentModificationExc when
         // the listeners get added/removed while we are in fire
         Vector newListeners = getBuildListeners();
         newListeners.addElement(listener);
         listeners = newListeners;
     }
 
     /**
      * Removes a build listener from the list. This listener
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
      * Returns a copy of the list of build listeners for the project.
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
     public void setProperty(String name, String value) {
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
     public void setNewProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setNewProperty(null, name,
                                                               value);
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
     public void setUserProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setUserProperty(null, name,
                                                                value);
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
     public void setInheritedProperty(String name, String value) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
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
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.setProperty(null, name, value, false);
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
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getProperty(null, name);
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
         throws BuildException {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
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
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getUserProperty(null, name);
     }
 
     /**
      * Returns a copy of the properties table.
      * @return a hashtable containing all properties
      *         (including user properties).
      */
     public Hashtable getProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.getProperties();
     }
 
     /**
      * Returns a copy of the user property hashtable
      * @return a hashtable containing just the user properties
      */
     public Hashtable getUserProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
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
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
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
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
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
         if (description == null) {
             description = Description.getDescription(this);
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
      * Sets "keep-going" mode. In this mode ANT will try to execute
      * as many targets as possible. All targets that do not depend
      * on failed target(s) will be executed.
      * @param keepGoingMode "keep-going" mode
      * @since Ant 1.6
      */
     public void setKeepGoingMode(boolean keepGoingMode) {
         this.keepGoingMode = keepGoingMode;
     }
 
     /**
      * Returns the keep-going mode.
      * @return "keep-going" mode
      * @since Ant 1.6
      */
     public boolean isKeepGoingMode() {
         return this.keepGoingMode;
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
         ComponentHelper.getComponentHelper(this).addTaskDefinition(taskName,
                 taskClass);
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
             taskClass.getConstructor(null);
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
      * Returns the current task definition hashtable. The returned hashtable is
      * "live" and so should not be modified.
      *
      * @return a map of from task name to implementing class
      *         (String to Class).
      */
     public Hashtable getTaskDefinitions() {
         return ComponentHelper.getComponentHelper(this).getTaskDefinitions();
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
         ComponentHelper.getComponentHelper(this).addDataTypeDefinition(typeName,
                 typeClass);
     }
 
     /**
      * Returns the current datatype definition hashtable. The returned
      * hashtable is "live" and so should not be modified.
      *
      * @return a map of from datatype name to implementing class
      *         (String to Class).
      */
     public Hashtable getDataTypeDefinitions() {
         return ComponentHelper.getComponentHelper(this).getDataTypeDefinitions();
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
         addTarget(target.getName(), target);
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
         return ComponentHelper.getComponentHelper(this).createTask(taskType);
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
         return ComponentHelper.getComponentHelper(this).createDataType(typeName);
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
 
         BuildException thrownException = null;
         for (int i = 0; i < targetNames.size(); i++) {
             try {
                 executeTarget((String) targetNames.elementAt(i));
             } catch (BuildException ex) {
                 if (!(keepGoingMode)) {
                     throw ex; // Throw further
                 }
                 thrownException = ex;
             }
         }
         if (thrownException != null) {
             throw thrownException;
         }
     }
 
     /**
      * Demultiplexes output so that each task receives the appropriate
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
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
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
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
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
      * Demultiplexes flush operation so that each task receives the appropriate
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
 
-        // Sort the dependency tree, and run everything from the
-        // beginning until we hit our targetName.
+        // Sort and run the dependency tree.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
-        Vector sortedTargets = topoSort(targetName, targets);
-        sortedTargets.setSize(sortedTargets.indexOf(targets.get(targetName)) + 1);
-        executeSortedTargets(sortedTargets);
+        executeSortedTargets(topoSort(targetName, targets, false));
     }
 
     /**
      * Executes a <CODE>Vector</CODE> of sorted targets.
      * @param sortedTargets   the aforementioned <CODE>Vector</CODE>.
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
                         thrownException.printStackTrace(System.err);
                         if (buildException == null) {
                             buildException =
                                 new BuildException(thrownException);
                         }
                     }
                 }
             }
         }
         if (buildException != null) {
             throw buildException;
         }
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
         fileUtils.setFileLastModified(file, time);
         log("Setting modification time for " + file, MSG_VERBOSE);
     }
 
     /**
      * Returns the boolean equivalent of a string, which is considered
      * <code>true</code> if either <code>"on"</code>, <code>"true"</code>,
      * or <code>"yes"</code> is found, ignoring case.
      *
      * @param s The string to convert to a boolean value.
      *
      * @return <code>true</code> if the given string is <code>"on"</code>,
      *         <code>"true"</code> or <code>"yes"</code>, or
      *         <code>false</code> otherwise.
      */
     public static boolean toBoolean(String s) {
         return ("on".equalsIgnoreCase(s)
                 || "true".equalsIgnoreCase(s)
                 || "yes".equalsIgnoreCase(s));
     }
 
     /**
-     * Topologically sorts a set of targets.
+     * Topologically sorts a set of targets.  Equivalent to calling
+     * <CODE>topoSort(new String[] {root}, targets, true)</CODE>.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
-     * @param targets A map of names to targets (String to Target).
+     * @param targets A Hashtable mapping names to Targets.
      *                Must not be <code>null</code>.
-     * @return a vector of Target objects in sorted order.
+     * @return a Vector of ALL Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      */
     public final Vector topoSort(String root, Hashtable targets)
         throws BuildException {
-        return topoSort(new String[] {root}, targets);
+        return topoSort(new String[] {root}, targets, true);
+    }
+
+    /**
+     * Topologically sorts a set of targets.  Equivalent to calling
+     * <CODE>topoSort(new String[] {root}, targets, returnAll)</CODE>.
+     *
+     * @param root The name of the root target. The sort is created in such
+     *             a way that the sequence of Targets up to the root
+     *             target is the minimum possible such sequence.
+     *             Must not be <code>null</code>.
+     * @param targets A Hashtable mapping names to Targets.
+     *                Must not be <code>null</code>.
+     * @param returnAll <CODE>boolean</CODE> indicating whether to return all
+     *                  targets, or the execution sequence only.
+     * @return a Vector of Target objects in sorted order.
+     * @exception BuildException if there is a cyclic dependency among the
+     *                           targets, or if a named target does not exist.
+     * @since Ant 1.6.3
+     */
+    public final Vector topoSort(String root, Hashtable targets,
+                                 boolean returnAll) throws BuildException {
+        return topoSort(new String[] {root}, targets, returnAll);
     }
 
     /**
      * Topologically sorts a set of targets.
      *
      * @param root <CODE>String[]</CODE> containing the names of the root targets.
-     *             The sort is created in such a way that the sequence of Targets
-     *             up to the root target is the minimum possible such sequence.
+     *             The sort is created in such a way that the ordered sequence of
+     *             Targets is the minimum possible such sequence to the specified
+     *             root targets.
      *             Must not be <code>null</code>.
      * @param targets A map of names to targets (String to Target).
      *                Must not be <code>null</code>.
-     * @return a vector of Target objects in sorted order.
+     * @param returnAll <CODE>boolean</CODE> indicating whether to return all
+     *                  targets, or the execution sequence only.
+     * @return a Vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
+     * @since Ant 1.6.3
      */
-    public final Vector topoSort(String[] root, Hashtable targets)
-        throws BuildException {
+    public final Vector topoSort(String[] root, Hashtable targets,
+                                 boolean returnAll) throws BuildException {
         Vector ret = new Vector();
         Hashtable state = new Hashtable();
         Stack visiting = new Stack();
 
         // We first run a DFS based sort using each root as a starting node.
         // This creates the minimum sequence of Targets to the root node(s).
         // We then do a sort on any remaining unVISITED targets.
         // This is unnecessary for doing our build, but it catches
         // circular dependencies or missing Targets on the entire
         // dependency tree, not just on the Targets that depend on the
         // build Target.
 
         for (int i = 0; i < root.length; i++) {
-            tsort(root[i], targets, state, visiting, ret);
+            String st = (String)(state.get(root[i]));
+            if (st == null) {
+                tsort(root[i], targets, state, visiting, ret);
+            } else if (st == VISITING) {
+                throw new RuntimeException("Unexpected node in visiting state: "
+                    + root[i]);
+            }
         }
         StringBuffer buf = new StringBuffer("Build sequence for target(s)");
 
         for (int j = 0; j < root.length; j++) {
             buf.append((j == 0) ? " `" : ", `").append(root[j]).append('\'');
         }
         buf.append(" is " + ret);
         log(buf.toString(), MSG_VERBOSE);
 
+        Vector complete = (returnAll) ? ret : new Vector(ret);
         for (Enumeration en = targets.keys(); en.hasMoreElements();) {
             String curTarget = (String) en.nextElement();
             String st = (String) state.get(curTarget);
             if (st == null) {
-                tsort(curTarget, targets, state, visiting, ret);
+                tsort(curTarget, targets, state, visiting, complete);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + curTarget);
             }
         }
-        log("Complete build sequence is " + ret, MSG_VERBOSE);
+        log("Complete build sequence is " + complete, MSG_VERBOSE);
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
      *                containing the complete list of dependencies in
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
             Object old = ((AntRefTable) references).getReal(name);
             if (old == value) {
                 // no warning, this is not changing anything
                 return;
             }
             if (old != null && !(old instanceof UnknownElement)) {
                 log("Overriding previous definition of reference to " + name,
                     MSG_WARN);
             }
             log("Adding reference: " + name, MSG_DEBUG);
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
         return ComponentHelper.getComponentHelper(this).getElementName(element);
     }
 
     /**
      * Sends a "build started" event to the build listeners for this project.
      */
     public void fireBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
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
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.buildFinished(event);
         }
     }
 
     /**
      * Sends a "subbuild started" event to the build listeners for
      * this project.
      *
      * @since Ant 1.6.2
      */
     public void fireSubBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             Object listener = iter.next();
             if (listener instanceof SubBuildListener) {
                 ((SubBuildListener) listener).subBuildStarted(event);
             }
         }
     }
 
     /**
      * Sends a "subbuild finished" event to the build listeners for
      * this project.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      *
      * @since Ant 1.6.2
      */
     public void fireSubBuildFinished(Throwable exception) {
         BuildEvent event = new BuildEvent(this);
         event.setException(exception);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             Object listener = iter.next();
             if (listener instanceof SubBuildListener) {
                 ((SubBuildListener) listener).subBuildFinished(event);
             }
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
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
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
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
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
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
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
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
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
 
         if (message.endsWith(StringUtils.LINE_SEP)) {
             int endIndex = message.length() - StringUtils.LINE_SEP.length();
             event.setMessage(message.substring(0, endIndex), priority);
         } else {
             event.setMessage(message, priority);
         }
         synchronized (this) {
             if (loggingMessage) {
                 throw new BuildException("Listener attempted to access "
                     + (priority == MSG_ERR ? "System.err" : "System.out")
                     + " - infinite loop terminated");
             }
             try {
                 loggingMessage = true;
                 Iterator iter = listeners.iterator();
                 while (iter.hasNext()) {
                     BuildListener listener = (BuildListener) iter.next();
                     listener.messageLogged(event);
                 }
             } finally {
                 loggingMessage = false;
             }
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
             threadGroupTasks.put(thread.getThreadGroup(), task);
         } else {
             threadTasks.remove(thread);
             threadGroupTasks.remove(thread.getThreadGroup());
         }
     }
 
     /**
      * Get the current task associated with a thread, if any
      *
      * @param thread the thread for which the task is required.
      * @return the task which is currently registered for the given thread or
      *         null if no task is registered.
      */
     public Task getThreadTask(Thread thread) {
         Task task = (Task) threadTasks.get(thread);
         if (task == null) {
             ThreadGroup group = thread.getThreadGroup();
             while (task == null && group != null) {
                 task = (Task) threadGroupTasks.get(group);
                 group = group.getParent();
             }
         }
         return task;
     }
 
 
     // Should move to a separate public class - and have API to add
     // listeners, etc.
     private static class AntRefTable extends Hashtable {
         private Project project;
 
         public AntRefTable(Project project) {
             super();
             this.project = project;
         }
 
         /** Returns the unmodified original object.
          * This method should be called internally to
          * get the 'real' object.
          * The normal get method will do the replacement
          * of UnknownElement ( this is similar with the JDNI
          * refs behavior )
          */
         public Object getReal(Object key) {
             return super.get(key);
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
             Object o = getReal(key);
             if (o instanceof UnknownElement) {
                 // Make sure that
                 UnknownElement ue = (UnknownElement) o;
                 ue.maybeConfigure();
                 o = ue.getRealThing();
             }
             return o;
         }
     }
 
     /**
      * Set a reference to this Project on the parameterized object.
      * Need to set the project before other set/add elements
      * are called
      * @param obj the object to invoke setProject(this) on
      */
     public final void setProjectReference(final Object obj) {
         if (obj instanceof ProjectComponent) {
             ((ProjectComponent) obj).setProject(this);
             return;
         }
         try {
             Method method =
                 obj.getClass().getMethod(
                     "setProject", new Class[] {Project.class});
             if (method != null) {
                 method.invoke(obj, new Object[] {this});
             }
         } catch (Throwable e) {
             // ignore this if the object does not have
             // a set project method or the method
             // is private/protected.
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/Target.java b/src/main/org/apache/tools/ant/Target.java
index beecdf964..8d375c1bd 100644
--- a/src/main/org/apache/tools/ant/Target.java
+++ b/src/main/org/apache/tools/ant/Target.java
@@ -1,442 +1,437 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.List;
 import java.util.StringTokenizer;
 
 import org.apache.tools.ant.util.CollectionUtils;
 
 /**
  * Class to implement a target object with required parameters.
  *
  */
 public class Target implements TaskContainer {
 
     /** Name of this target. */
     private String name;
     /** The "if" condition to test on execution. */
     private String ifCondition = "";
     /** The "unless" condition to test on execution. */
     private String unlessCondition = "";
     /** List of targets this target is dependent on. */
     private List dependencies = null;
     /** Children of this target (tasks and data types). */
     private List children = new ArrayList();
     /** Since Ant 1.6.2 */
     private Location location = Location.UNKNOWN_LOCATION;
 
     /** Project this target belongs to. */
     private Project project;
 
     /** Description of this target, if any. */
     private String description = null;
 
     /** Sole constructor. */
     public Target() {
     }
 
     /**
      * Sets the project this target belongs to.
      *
      * @param project The project this target belongs to.
      *                Must not be <code>null</code>.
      */
     public void setProject(Project project) {
         this.project = project;
     }
 
     /**
      * Returns the project this target belongs to.
      *
      * @return The project this target belongs to, or <code>null</code> if
      *         the project has not been set yet.
      */
     public Project getProject() {
         return project;
     }
 
     /**
      * Sets the location of this target's definition.
      *
      * @param location   <CODE>Location</CODE>
      */
     public void setLocation(Location location) {
         this.location = location;
     }
 
     /**
      * Get the location of this target's definition.
      *
      * @return <CODE>Location</CODE>
      */
     public Location getLocation() {
         return location;
     }
 
     /**
      * Sets the list of targets this target is dependent on.
      * The targets themselves are not resolved at this time.
      *
      * @param depS A comma-separated list of targets this target
      *             depends on. Must not be <code>null</code>.
      */
     public void setDepends(String depS) {
         if (depS.length() > 0) {
             StringTokenizer tok =
                 new StringTokenizer(depS, ",", true);
             while (tok.hasMoreTokens()) {
                 String token = tok.nextToken().trim();
 
                 // Make sure the dependency is not empty string
                 if (token.equals("") || token.equals(",")) {
                     throw new BuildException("Syntax Error: Depend "
                         + "attribute for target \"" + getName()
                         + "\" has an empty string for dependency.");
                 }
 
                 addDependency(token);
 
                 // Make sure that depends attribute does not
                 // end in a ,
                 if (tok.hasMoreTokens()) {
                     token = tok.nextToken();
                     if (!tok.hasMoreTokens() || !token.equals(",")) {
                         throw new BuildException("Syntax Error: Depend "
                             + "attribute for target \"" + getName()
                             + "\" ends with a , character");
                     }
                 }
             }
         }
     }
 
     /**
      * Sets the name of this target.
      *
      * @param name The name of this target. Should not be <code>null</code>.
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /**
      * Returns the name of this target.
      *
      * @return the name of this target, or <code>null</code> if the
      *         name has not been set yet.
      */
     public String getName() {
         return name;
     }
 
     /**
      * Adds a task to this target.
      *
      * @param task The task to be added. Must not be <code>null</code>.
      */
     public void addTask(Task task) {
         children.add(task);
     }
 
     /**
      * Adds the wrapper for a data type element to this target.
      *
      * @param r The wrapper for the data type element to be added.
      *          Must not be <code>null</code>.
      */
     public void addDataType(RuntimeConfigurable r) {
         children.add(r);
     }
 
     /**
      * Returns the current set of tasks to be executed by this target.
      *
      * @return an array of the tasks currently within this target
      */
     public Task[] getTasks() {
         List tasks = new ArrayList(children.size());
         Iterator it = children.iterator();
         while (it.hasNext()) {
             Object o = it.next();
             if (o instanceof Task) {
                 tasks.add(o);
             }
         }
 
         return (Task[]) tasks.toArray(new Task[tasks.size()]);
     }
 
     /**
      * Adds a dependency to this target.
      *
      * @param dependency The name of a target this target is dependent on.
      *                   Must not be <code>null</code>.
      */
     public void addDependency(String dependency) {
         if (dependencies == null) {
             dependencies = new ArrayList(2);
         }
         dependencies.add(dependency);
     }
 
     /**
      * Returns an enumeration of the dependencies of this target.
      *
      * @return an enumeration of the dependencies of this target
      */
     public Enumeration getDependencies() {
         if (dependencies != null) {
             return Collections.enumeration(dependencies);
         } else {
             return new CollectionUtils.EmptyEnumeration();
         }
     }
 
     /**
      * Does this target depend on the named target?
      * @param other the other named target.
      * @return true if the target does depend on the named target
      * @since Ant 1.6
      */
     public boolean dependsOn(String other) {
-        if (getProject() != null) {
-            List l = getProject().topoSort(getName(),
-                                           getProject().getTargets());
-            int myIdx = l.indexOf(this);
-            int otherIdx = l.indexOf(getProject().getTargets().get(other));
-            return myIdx >= otherIdx;
-        }
-        return false;
+        Project p = getProject();
+        Hashtable t = (p == null) ? null : p.getTargets();
+        return (p != null && p.topoSort(getName(), t, false).contains(t.get(other)));
     }
 
     /**
      * Sets the "if" condition to test on execution. This is the
      * name of a property to test for existence - if the property
      * is not set, the task will not execute. The property goes
      * through property substitution once before testing, so if
      * property <code>foo</code> has value <code>bar</code>, setting
      * the "if" condition to <code>${foo}_x</code> will mean that the
      * task will only execute if property <code>bar_x</code> is set.
      *
      * @param property The property condition to test on execution.
      *                 May be <code>null</code>, in which case
      *                 no "if" test is performed.
      */
     public void setIf(String property) {
         this.ifCondition = (property == null) ? "" : property;
     }
 
     /**
      * Returns the "if" property condition of this target.
      *
      * @return the "if" property condition or <code>null</code> if no
      *         "if" condition had been defined.
      */
     public String getIf() {
         return ("".equals(ifCondition) ? null : ifCondition);
     }
 
     /**
      * Sets the "unless" condition to test on execution. This is the
      * name of a property to test for existence - if the property
      * is set, the task will not execute. The property goes
      * through property substitution once before testing, so if
      * property <code>foo</code> has value <code>bar</code>, setting
      * the "unless" condition to <code>${foo}_x</code> will mean that the
      * task will only execute if property <code>bar_x</code> isn't set.
      *
      * @param property The property condition to test on execution.
      *                 May be <code>null</code>, in which case
      *                 no "unless" test is performed.
      */
     public void setUnless(String property) {
         this.unlessCondition = (property == null) ? "" : property;
     }
 
     /**
      * Returns the "unless" property condition of this target.
      *
      * @return the "unless" property condition or <code>null</code>
      *         if no "unless" condition had been defined.
      */
     public String getUnless() {
         return ("".equals(unlessCondition) ? null : unlessCondition);
     }
 
     /**
      * Sets the description of this target.
      *
      * @param description The description for this target.
      *                    May be <code>null</code>, indicating that no
      *                    description is available.
      */
     public void setDescription(String description) {
         this.description = description;
     }
 
     /**
      * Returns the description of this target.
      *
      * @return the description of this target, or <code>null</code> if no
      *         description is available.
      */
     public String getDescription() {
         return description;
     }
 
     /**
      * Returns the name of this target.
      *
      * @return the name of this target, or <code>null</code> if the
      *         name has not been set yet.
      */
     public String toString() {
         return name;
     }
 
     /**
      * Executes the target if the "if" and "unless" conditions are
      * satisfied. Dependency checking should be done before calling this
      * method, as it does no checking of its own. If either the "if"
      * or "unless" test prevents this target from being executed, a verbose
      * message is logged giving the reason. It is recommended that clients
      * of this class call performTasks rather than this method so that
      * appropriate build events are fired.
      *
      * @exception BuildException if any of the tasks fail or if a data type
      *                           configuration fails.
      *
      * @see #performTasks()
      * @see #setIf(String)
      * @see #setUnless(String)
      */
     public void execute() throws BuildException {
         if (testIfCondition() && testUnlessCondition()) {
             for (int taskPosition = 0;
                  taskPosition < children.size();
                  ++taskPosition) {
                 Object o = children.get(taskPosition);
                 if (o instanceof Task) {
                     Task task = (Task) o;
                     task.perform();
                 } else {
                     RuntimeConfigurable r = (RuntimeConfigurable) o;
                     r.maybeConfigure(project);
                 }
             }
         } else if (!testIfCondition()) {
             project.log(this, "Skipped because property '"
                         + project.replaceProperties(this.ifCondition)
                         + "' not set.", Project.MSG_VERBOSE);
         } else {
             project.log(this, "Skipped because property '"
                         + project.replaceProperties(this.unlessCondition)
                         + "' set.", Project.MSG_VERBOSE);
         }
     }
 
     /**
      * Performs the tasks within this target (if the conditions are met),
      * firing target started/target finished messages around a call to
      * execute.
      *
      * @see #execute()
      */
     public final void performTasks() {
         RuntimeException thrown = null;
         project.fireTargetStarted(this);
         try {
             execute();
         } catch (RuntimeException exc) {
             thrown = exc;
             throw exc;
         } finally {
             project.fireTargetFinished(this, thrown);
         }
     }
 
     /**
      * Replaces all occurrences of the given task in the list
      * of children with the replacement data type wrapper.
      *
      * @param el The task to replace.
      *           Must not be <code>null</code>.
      * @param o  The data type wrapper to replace <code>el</code> with.
      */
     void replaceChild(Task el, RuntimeConfigurable o) {
         int index;
         while ((index = children.indexOf(el)) >= 0) {
             children.set(index, o);
         }
     }
 
     /**
      * Replaces all occurrences of the given task in the list
      * of children with the replacement task.
      *
      * @param el The task to replace.
      *           Must not be <code>null</code>.
      * @param o  The task to replace <code>el</code> with.
      */
     void replaceChild(Task el, Task o) {
         int index;
         while ((index = children.indexOf(el)) >= 0) {
             children.set(index, o);
         }
     }
 
     /**
      * Tests whether or not the "if" condition is satisfied.
      *
      * @return whether or not the "if" condition is satisfied. If no
      *         condition (or an empty condition) has been set,
      *         <code>true</code> is returned.
      *
      * @see #setIf(String)
      */
     private boolean testIfCondition() {
         if ("".equals(ifCondition)) {
             return true;
         }
 
         String test = project.replaceProperties(ifCondition);
         return project.getProperty(test) != null;
     }
 
     /**
      * Tests whether or not the "unless" condition is satisfied.
      *
      * @return whether or not the "unless" condition is satisfied. If no
      *         condition (or an empty condition) has been set,
      *         <code>true</code> is returned.
      *
      * @see #setUnless(String)
      */
     private boolean testUnlessCondition() {
         if ("".equals(unlessCondition)) {
             return true;
         }
         String test = project.replaceProperties(unlessCondition);
         return project.getProperty(test) == null;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index 3a06830a6..0bc0b7af5 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,761 +1,758 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
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
  *    &lt;/ant&gt;</SPAN>
  *  &lt;/target&gt;</SPAN>
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
 
     /** the basedir where is executed the build file */
     private File dir = null;
 
     /**
      * the build.xml file (can be absolute) in this case dir will be
      * ignored
      */
     private String antFile = null;
 
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
 
     /** the sets of properties to pass to the new project */
     private Vector propertySets = new Vector();
 
     /** the targets to call on the new project */
     private Vector targets = new Vector();
 
     /** whether the target attribute was specified **/
     private boolean targetAttributeSet = false;
 
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
         newProject = new Project();
         newProject.setDefaultInputStream(getProject().getDefaultInputStream());
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
 
         getProject().initSubProject(newProject);
 
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
      * Pass output sent to System.out to the new project.
      *
      * @param output a line of output
      * @since Ant 1.5
      */
     public void handleOutput(String output) {
         if (newProject != null) {
             newProject.demuxOutput(output, false);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * Process input into the ant task
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
      *
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6
      */
     public int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (newProject != null) {
             return newProject.demuxInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @param output The output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5.2
      */
     public void handleFlush(String output) {
         if (newProject != null) {
             newProject.demuxFlush(output, false);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5
      */
     public void handleErrorOutput(String output) {
         if (newProject != null) {
             newProject.demuxOutput(output, true);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String output) {
         if (newProject != null) {
             newProject.demuxFlush(output, true);
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     /**
      * Do the execution.
      * @throws BuildException if a target tries to call itself
      * probably also if a BuildException is thrown by the new project
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
 
             File file = FileUtils.newFileUtils().resolveFile(dir, antFile);
             antFile = file.getAbsolutePath();
 
             log("calling target(s) "
                 + ((locals.size() == 0) ? locals.toString() : "[default]")
                 + " in build file " + antFile, Project.MSG_VERBOSE);
             newProject.setUserProperty("ant.file" , antFile);
 
             String thisAntFile = getProject().getProperty("ant.file");
             // Are we trying to call the target in which we are defined (or
             // the build file if this is a top level task)?
             if (thisAntFile != null
                 && newProject.resolveFile(newProject.getProperty("ant.file"))
                 .equals(getProject().resolveFile(thisAntFile)) 
                 && getOwningTarget() != null) {
 
                 if (getOwningTarget().getName().equals("")) {
                     if (getTaskName().equals("antcall")) {
                         throw new BuildException("antcall must not be used at"
                                                  + " the top level.");
                     } else {
                         throw new BuildException(getTaskName() + " task at the"
                                                  + " top level must not invoke"
                                                  + " its own build file.");
                     }
                 }
             }
 
             try {
                 ProjectHelper.configureProject(newProject, new File(antFile));
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
                 } else {
                     boolean circular = false;
                     for (Iterator it = locals.iterator(); !circular && it.hasNext();) {
                         Target other = (Target)(getProject().getTargets().get(
                             (String)(it.next())));
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
             }
 
             addReferences();
 
             if (locals.size() > 0 && !(locals.size() == 1 && locals.get(0) == "")) {
                 Throwable t = null;
                 try {
                     log("Entering " + antFile + "...", Project.MSG_VERBOSE);
                     newProject.fireSubBuildStarted();
                     String[] nameArray =
                         (String[])(locals.toArray(new String[locals.size()]));
 
-                    Hashtable targets = newProject.getTargets();
-                    Vector sortedTargets = newProject.topoSort(nameArray, targets);
+                    newProject.executeSortedTargets(newProject.topoSort(
+                        nameArray, newProject.getTargets(), false));
 
-                    sortedTargets.setSize(sortedTargets.indexOf(targets.get(
-                        locals.lastElement())) + 1);
-                    newProject.executeSortedTargets(sortedTargets);
                 } catch (BuildException ex) {
                     t = ProjectHelper
                         .addLocationToBuildException(ex, getLocation());
                     throw (BuildException) t;
                 } finally {
                     log("Exiting " + antFile + ".", Project.MSG_VERBOSE);
                     newProject.fireSubBuildFinished(t);
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
      * @throws BuildException under unknown circumstances
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
      * @throws BuildException if a reference does not have a refid
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
      * Copies all properties from the given table to the new project -
      * omitting those that have already been set in the new project as
      * well as properties named basedir or ant.file.
      * @param props properties to copy to the new project
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
      * @param d new directory
      */
     public void setDir(File d) {
         this.dir = d;
     }
 
     /**
      * The build file to use.
      * Defaults to "build.xml". This file is expected to be a filename relative
      * to the dir attribute given.
      * @param s build file to use
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
      * @param s target to invoke
      */
     public void setTarget(String s) {
         if (s.equals("")) {
             throw new BuildException("target attribute must not be empty");
         }
 
         targets.add(s);
         targetAttributeSet = true;
     }
 
     /**
      * Filename to write the output to.
      * This is relative to the value of the dir attribute
      * if it has been set or to the base directory of the
      * current project otherwise.
      * @param s file to which the output should go to
      */
     public void setOutput(String s) {
         this.output = s;
     }
 
     /**
      * Property to pass to the new project.
      * The property is passed as a 'user property'
      * @return new property created
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
      * @param r reference to add
      */
     public void addReference(Reference r) {
         references.addElement(r);
     }
 
     /**
      * Add a target to this Ant invocation.
      * @param target   the <CODE>TargetElement</CODE> to add.
      * @since Ant 1.7
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
      * Set of properties to pass to the new project.
      *
      * @param ps property set to add
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
 
     /**
      * Helper class that implements the nested &lt;target&gt;
      * element of &lt;ant&gt; and &lt;antcall&gt;.
      * @since Ant 1.7
      */
     public static class TargetElement {
         private String name;
 
         /**
          * Default constructor.
          */
         public TargetElement() {}
 
         /**
          * Set the name of this TargetElement.
          * @param name   the <CODE>String</CODE> target name.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * Get the name of this TargetElement.
          * @return <CODE>String</CODE>.
          */
         public String getName() {
             return name;
         }
     }
 }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java b/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
index 23c67f98e..61c2eb625 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
@@ -1,504 +1,508 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
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
 
 import junit.framework.AssertionFailedError;
 
 import org.apache.tools.ant.BuildEvent;
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.BuildListener;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.input.PropertyFileInputHandler;
 import org.apache.tools.ant.types.Path;
 
 /**
  * @version $Revision$
  */
 public class AntTest extends BuildFileTest {
 
     public AntTest(String name) {
         super(name);
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/ant.xml");
     }
 
     public void tearDown() {
         executeTarget("cleanup");
     }
 
     public void test1() {
         expectBuildException("test1", "recursive call");
     }
 
     // target must be specified
     public void test2() {
         expectBuildException("test2", "required argument not specified");
     }
 
     // Should fail since a recursion will occur...
     public void test3() {
         expectBuildException("test1", "recursive call");
     }
 
     public void test4() {
         expectBuildException("test4", "target attribute must not be empty");
     }
 
     public void test4b() {
         expectBuildException("test4b", "target doesn't exist");
     }
 
     public void test5() {
         executeTarget("test5");
     }
 
     public void test6() {
         executeTarget("test6");
     }
 
     public void testExplicitBasedir1() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("..");
         testBaseDirs("explicitBasedir1",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testExplicitBasedir2() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("..");
         testBaseDirs("explicitBasedir2",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testInheritBasedir() {
         String basedir = getProjectDir().getAbsolutePath();
         testBaseDirs("inheritBasedir", new String[] {basedir, basedir});
     }
 
     public void testDoNotInheritBasedir() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("ant");
         String basedir = getProjectDir().getAbsolutePath();
         testBaseDirs("doNotInheritBasedir",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testBasedirTripleCall() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("ant");
         testBaseDirs("tripleCall",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath(),
                                    dir1.getAbsolutePath()
                      });
     }
 
     protected void testBaseDirs(String target, String[] dirs) {
         BasedirChecker bc = new BasedirChecker(dirs);
         project.addBuildListener(bc);
         executeTarget(target);
         AssertionFailedError ae = bc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(bc);
     }
 
     public void testReferenceInheritance() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         project.addReference("no-override", p);
         testReference("testInherit", new String[] {"path", "path"},
                       new boolean[] {true, true}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {true, false}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {false, false}, null);
     }
 
     public void testReferenceNoInheritance() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         project.addReference("no-override", p);
         testReference("testNoInherit", new String[] {"path", "path"},
                       new boolean[] {true, false}, p);
         testReference("testNoInherit", new String[] {"path", "path"},
                       new boolean[] {false, true}, null);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {true, false}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {false, false}, null);
     }
 
     public void testReferenceRename() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         testReference("testRename", new String[] {"path", "path"},
                       new boolean[] {true, false}, p);
         testReference("testRename", new String[] {"path", "path"},
                       new boolean[] {false, true}, null);
         testReference("testRename", new String[] {"newpath", "newpath"},
                       new boolean[] {false, true}, p);
     }
 
     protected void testReference(String target, String[] keys,
                                  boolean[] expect, Object value) {
         ReferenceChecker rc = new ReferenceChecker(keys, expect, value);
         project.addBuildListener(rc);
         executeTarget(target);
         AssertionFailedError ae = rc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(rc);
     }
 
     public void testLogfilePlacement() {
         File[] logFiles = new File[] {
             getProject().resolveFile("test1.log"),
             getProject().resolveFile("test2.log"),
             getProject().resolveFile("ant/test3.log"),
             getProject().resolveFile("ant/test4.log")
         };
         for (int i=0; i<logFiles.length; i++) {
             assertTrue(logFiles[i].getName()+" doesn\'t exist",
                        !logFiles[i].exists());
         }
 
         executeTarget("testLogfilePlacement");
 
         for (int i=0; i<logFiles.length; i++) {
             assertTrue(logFiles[i].getName()+" exists",
                        logFiles[i].exists());
         }
     }
 
     public void testInputHandlerInheritance() {
         InputHandler ih = new PropertyFileInputHandler();
         getProject().setInputHandler(ih);
         InputHandlerChecker ic = new InputHandlerChecker(ih);
         getProject().addBuildListener(ic);
         executeTarget("tripleCall");
         AssertionFailedError ae = ic.getError();
         if (ae != null) {
             throw ae;
         }
         getProject().removeBuildListener(ic);
     }
 
     public void testRefId() {
         Path testPath = new Path(project);
         testPath.createPath().setPath(System.getProperty("java.class.path"));
         PropertyChecker pc =
             new PropertyChecker("testprop",
                                 new String[] {null,
                                               testPath.toString()});
         project.addBuildListener(pc);
         executeTarget("testRefid");
         AssertionFailedError ae = pc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(pc);
     }
 
     public void testUserPropertyWinsInheritAll() {
         getProject().setUserProperty("test", "7");
         expectLogContaining("test-property-override-inheritall-start",
                             "The value of test is 7");
     }
 
     public void testUserPropertyWinsNoInheritAll() {
         getProject().setUserProperty("test", "7");
         expectLogContaining("test-property-override-no-inheritall-start",
                             "The value of test is 7");
     }
 
     public void testOverrideWinsInheritAll() {
         expectLogContaining("test-property-override-inheritall-start",
                             "The value of test is 4");
     }
 
     public void testOverrideWinsNoInheritAll() {
         expectLogContaining("test-property-override-no-inheritall-start",
                             "The value of test is 4");
     }
 
     public void testPropertySet() {
         executeTarget("test-propertyset");
         assertTrue(getLog().indexOf("test1 is ${test1}") > -1);
         assertTrue(getLog().indexOf("test2 is ${test2}") > -1);
         assertTrue(getLog().indexOf("test1.x is 1") > -1);
     }
 
     public void testInfiniteLoopViaDepends() {
         expectBuildException("infinite-loop-via-depends", "recursive call");
     }
 
     public void testMultiSameProperty() {
         expectLog("multi-same-property", "prop is two");
     }
 
     public void testTopLevelTarget() {
         expectLog("topleveltarget", "Hello world");
     }
 
     public void testMultiplePropertyFileChildren() {
         PropertyChecker pcBar = new PropertyChecker("bar",
                                                     new String[] {null, "Bar"});
         PropertyChecker pcFoo = new PropertyChecker("foo",
                                                     new String[] {null, "Foo"});
         project.addBuildListener(pcBar);
         project.addBuildListener(pcFoo);
         executeTarget("multiple-property-file-children");
         AssertionFailedError aeBar = pcBar.getError();
         if (aeBar != null) {
             throw aeBar;
         }
         AssertionFailedError aeFoo = pcFoo.getError();
         if (aeFoo != null) {
             throw aeFoo;
         }
         project.removeBuildListener(pcBar);
         project.removeBuildListener(pcFoo);
     }
 
     public void testBlankTarget() {
         expectBuildException("blank-target", "target name must not be empty");
     }
 
     public void testMultipleTargets() {
         expectLog("multiple-targets", "tadadctbdbtc");
     }
 
+    public void testMultipleTargets2() {
+        expectLog("multiple-targets-2", "dadctb");
+    }
+
     private class BasedirChecker implements BuildListener {
         private String[] expectedBasedirs;
         private int calls = 0;
         private AssertionFailedError error;
 
         BasedirChecker(String[] dirs) {
             expectedBasedirs = dirs;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (error == null) {
                 try {
                     assertEquals(expectedBasedirs[calls++],
                                  event.getProject().getBaseDir().getAbsolutePath());
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class ReferenceChecker implements BuildListener {
         private String[] keys;
         private boolean[] expectSame;
         private Object value;
         private int calls = 0;
         private AssertionFailedError error;
 
         ReferenceChecker(String[] keys, boolean[] expectSame, Object value) {
             this.keys = keys;
             this.expectSame = expectSame;
             this.value = value;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (error == null) {
                 try {
                     String msg =
                         "Call " + calls + " refid=\'" + keys[calls] + "\'";
                     if (value == null) {
                         Object o = event.getProject().getReference(keys[calls]);
                         if (expectSame[calls++]) {
                             assertNull(msg, o);
                         } else {
                             assertNotNull(msg, o);
                         }
                     } else {
                         // a rather convoluted equals() test
                         Path expect = (Path) value;
                         Path received = (Path) event.getProject().getReference(keys[calls]);
                         boolean shouldBeEqual = expectSame[calls++];
                         if (received == null) {
                             assertTrue(msg, !shouldBeEqual);
                         } else {
                             String[] l1 = expect.list();
                             String[] l2 = received.list();
                             if (l1.length == l2.length) {
                                 for (int i=0; i<l1.length; i++) {
                                     if (!l1[i].equals(l2[i])) {
                                         assertTrue(msg, !shouldBeEqual);
                                     }
                                 }
                                 assertTrue(msg, shouldBeEqual);
                             } else {
                                 assertTrue(msg, !shouldBeEqual);
                             }
                         }
                     }
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class InputHandlerChecker implements BuildListener {
         private InputHandler ih;
         private AssertionFailedError error;
 
         InputHandlerChecker(InputHandler value) {
             ih = value;
         }
 
         public void buildStarted(BuildEvent event) {
             check(event);
         }
         public void buildFinished(BuildEvent event) {
             check(event);
         }
         public void targetFinished(BuildEvent event) {
             check(event);
         }
         public void taskStarted(BuildEvent event) {
             check(event);
         }
         public void taskFinished(BuildEvent event) {
             check(event);
         }
         public void messageLogged(BuildEvent event) {
             check(event);
         }
 
         public void targetStarted(BuildEvent event) {
             check(event);
         }
 
         private void check(BuildEvent event) {
             if (error == null) {
                 try {
                     assertNotNull(event.getProject().getInputHandler());
                     assertSame(ih, event.getProject().getInputHandler());
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class PropertyChecker implements BuildListener {
         private String[] expectedValues;
         private String key;
         private int calls = 0;
         private AssertionFailedError error;
 
         PropertyChecker(String key, String[] values) {
             this.key = key;
             this.expectedValues = values;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (calls >= expectedValues.length) {
                 error = new AssertionFailedError("Unexpected invocation of"
                                                  + " target "
                                                  + event.getTarget().getName());
             }
 
             if (error == null) {
                 try {
                     assertEquals(expectedValues[calls++],
                                  event.getProject().getProperty(key));
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
 
 }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java b/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
index bd2d07a99..c15dd4858 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
@@ -1,69 +1,73 @@
 /*
  * Copyright  2003-2004 The Apache Software Foundation
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
 
 import java.util.Vector;
 
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  */
 public class CallTargetTest extends BuildFileTest {
 
     public CallTargetTest(String name) {
         super(name);
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/calltarget.xml");
     }
 
     // see bugrep 21724 (references not passing through with antcall)
     public void testInheritRefFileSet() {
         expectLogContaining("testinheritreffileset", "calltarget.xml");
     }
 
     // see bugrep 21724 (references not passing through with antcall)
     public void testInheritFilterset() {
         project.executeTarget("testinheritreffilterset");
     }
 
     // see bugrep 11418 (In repeated calls to the same target,
     // params will not be passed in)
     public void testMultiCall() {
         Vector v = new Vector();
         v.add("call-multi");
         v.add("call-multi");
         project.executeTargets(v);
         assertLogContaining("multi is SETmulti is SET");
     }
 
     public void testBlankTarget() {
         expectBuildException("blank-target", "target name must not be empty");
     }
 
     public void testMultipleTargets() {
         expectLog("multiple-targets", "tadadctbdbtc");
     }
 
+    public void testMultipleTargets2() {
+        expectLog("multiple-targets-2", "dadctb");
+    }
+
     public void tearDown() {
         project.executeTarget("cleanup");
     }
 }
