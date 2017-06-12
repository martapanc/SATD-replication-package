diff --git a/src/main/org/apache/tools/ant/taskdefs/Antlib.java b/src/main/org/apache/tools/ant/taskdefs/Antlib.java
index fdca8b881..ce3780b59 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Antlib.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Antlib.java
@@ -1,195 +1,194 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 import java.io.IOException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.apache.tools.ant.TaskContainer;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ComponentHelper;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.helper.ProjectHelper2;
 import org.apache.tools.ant.UnknownElement;
 
 
 /**
  * Antlib task.
  *
  * @author Peter Reilly
  *
  * @since Ant 1.6
  */
 public class Antlib extends Task implements TaskContainer {
     //
     // Static
     //
 
     /** The name of this task */
     public static final String TAG = "antlib";
 
     /**
      * Static method to read an ant lib definition from
      * a url.
      *
      * @param project   the current project
      * @param antlibUrl the url to read the definitions from
      * @return   the ant lib task
      */
     public static Antlib createAntlib(Project project, URL antlibUrl) {
         // Check if we can contact the URL
         try {
             antlibUrl.openConnection().connect();
         } catch (IOException ex) {
             throw new BuildException(
                 "Unable to find " + antlibUrl, ex);
         }
         // Should be safe to parse
         ProjectHelper2 parser = new ProjectHelper2();
         UnknownElement ue =
             parser.parseUnknownElement(project, antlibUrl);
         // Check name is "antlib"
         if (!(ue.getTag().equals(TAG))) {
             throw new BuildException(
                 "Unexpected tag " + ue.getTag() + " expecting "
                 + TAG, ue.getLocation());
         }
         Antlib antlib = new Antlib();
         antlib.setProject(project);
         antlib.setLocation(ue.getLocation());
         antlib.init();
         ue.configure(antlib);
         return antlib;
     }
 
 
     //
     // Instance
     //
     private ClassLoader classLoader;
-    private String      prefix;
     private String      uri = "";
     private List  tasks = new ArrayList();
 
     /**
      * Set the class loader for this antlib.
      * This class loader is used for any tasks that
      * derive from Definer.
      *
      * @param classLoader the class loader
      */
     protected void setClassLoader(ClassLoader classLoader) {
         this.classLoader = classLoader;
     }
 
     /**
      * Set the URI for this antlib.
      * @param uri the namespace uri
      */
     protected void  setURI(String uri) {
         this.uri = uri;
     }
 
     private ClassLoader getClassLoader() {
         if (classLoader == null) {
             classLoader = Antlib.class.getClassLoader();
         }
         return classLoader;
     }
 
     /**
      * add a task to the list of tasks
      *
      * @param nestedTask Nested task to execute in antlibe
      */
     public void addTask(Task nestedTask) {
         tasks.add(nestedTask);
     }
 
     /**
      * Execute the nested tasks, setting the classloader for
      * any tasks that derive from Definer.
      */
     public void execute() {
         ComponentHelper helper =
             ComponentHelper.getComponentHelper(getProject());
         helper.enterAntLib();
         try {
             for (Iterator i = tasks.iterator(); i.hasNext();) {
                 UnknownElement ue = (UnknownElement) i.next();
                 ue.maybeConfigure();
                 setLocation(ue.getLocation());
                 Task t = ue.getTask();
                 if (t == null) {
                     continue;
                 }
                 if (t instanceof AntlibInterface) {
                     AntlibInterface d = (AntlibInterface) t;
                     d.setURI(uri);
                     d.setAntlibClassLoader(getClassLoader());
                 }
                 t.init();
                 t.execute();
             }
         } finally {
             helper.exitAntLib();
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/BuildNumber.java b/src/main/org/apache/tools/ant/taskdefs/BuildNumber.java
index 579970585..086a87241 100644
--- a/src/main/org/apache/tools/ant/taskdefs/BuildNumber.java
+++ b/src/main/org/apache/tools/ant/taskdefs/BuildNumber.java
@@ -1,240 +1,240 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.util.Properties;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Read, increment, and write a build number in a file
  * It will first
  * attempt to read a build number from a file, then set the property
  * "build.number" to the value that was read in (or 0 if no such value). Then
  * it will increment the build number by one and write it back out into the
  * file.
  *
  * @author <a href="mailto:peter@apache.org">Peter Donald</a>
  * @version $Revision$ $Date$
  * @since Ant 1.5
  * @ant.task name="buildnumber"
  */
 public class BuildNumber
      extends Task {
     /**
      * The name of the property in which the build number is stored.
      */
     private static final String DEFAULT_PROPERTY_NAME = "build.number";
 
     /** The default filename to use if no file specified.  */
     private static final String DEFAULT_FILENAME = DEFAULT_PROPERTY_NAME;
 
     /** The File in which the build number is stored.  */
     private File myFile;
 
 
     /**
      * The file in which the build number is stored. Defaults to
      * "build.number" if not specified.
      *
      * @param file the file in which build number is stored.
      */
     public void setFile(final File file) {
         myFile = file;
     }
 
 
     /**
      * Run task.
      *
      * @exception BuildException if an error occurs
      */
     public void execute()
          throws BuildException {
         File savedFile = myFile; // may be altered in validate
 
         validate();
 
         final Properties properties = loadProperties();
         final int buildNumber = getBuildNumber(properties);
 
         properties.put(DEFAULT_PROPERTY_NAME,
             String.valueOf(buildNumber + 1));
 
         // Write the properties file back out
         FileOutputStream output = null;
 
         try {
             output = new FileOutputStream(myFile);
 
             final String header = "Build Number for ANT. Do not edit!";
 
-            properties.save(output, header);
+            properties.store(output, header);
         } catch (final IOException ioe) {
             final String message = "Error while writing " + myFile;
 
             throw new BuildException(message, ioe);
         } finally {
             if (null != output) {
                 try {
                     output.close();
                 } catch (final IOException ioe) {
                     getProject().log("error closing output stream " + ioe, Project.MSG_ERR);
                 }
             }
             myFile = savedFile;
         }
 
         //Finally set the property
         getProject().setNewProperty(DEFAULT_PROPERTY_NAME,
             String.valueOf(buildNumber));
     }
 
 
     /**
      * Utility method to retrieve build number from properties object.
      *
      * @param properties the properties to retrieve build number from
      * @return the build number or if no number in properties object
      * @throws BuildException if build.number property is not an integer
      */
     private int getBuildNumber(final Properties properties)
          throws BuildException {
         final String buildNumber =
             properties.getProperty(DEFAULT_PROPERTY_NAME, "0").trim();
 
         // Try parsing the line into an integer.
         try {
             return Integer.parseInt(buildNumber);
         } catch (final NumberFormatException nfe) {
             final String message =
                 myFile + " contains a non integer build number: " + buildNumber;
 
             throw new BuildException(message, nfe);
         }
     }
 
 
     /**
      * Utility method to load properties from file.
      *
      * @return the loaded properties
      * @throws BuildException
      */
     private Properties loadProperties()
          throws BuildException {
         FileInputStream input = null;
 
         try {
             final Properties properties = new Properties();
 
             input = new FileInputStream(myFile);
             properties.load(input);
             return properties;
         } catch (final IOException ioe) {
             throw new BuildException(ioe);
         } finally {
             if (null != input) {
                 try {
                     input.close();
                 } catch (final IOException ioe) {
                     getProject().log("error closing input stream " + ioe, Project.MSG_ERR);
                 }
             }
         }
     }
 
 
     /**
      * Validate that the task parameters are valid.
      *
      * @throws BuildException if parameters are invalid
      */
     private void validate()
          throws BuildException {
         if (null == myFile) {
             myFile = getProject().resolveFile(DEFAULT_FILENAME);
         }
 
         if (!myFile.exists()) {
             try {
                 FileUtils.newFileUtils().createNewFile(myFile);
             } catch (final IOException ioe) {
                 final String message =
                     myFile + " doesn't exist and new file can't be created.";
 
                 throw new BuildException(message, ioe);
             }
         }
 
         if (!myFile.canRead()) {
             final String message = "Unable to read from " + myFile + ".";
 
             throw new BuildException(message);
         }
 
         if (!myFile.canWrite()) {
             final String message = "Unable to write to " + myFile + ".";
 
             throw new BuildException(message);
         }
     }
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/Classloader.java b/src/main/org/apache/tools/ant/taskdefs/Classloader.java
index 070097c5d..009d66e13 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Classloader.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Classloader.java
@@ -1,260 +1,260 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.types.Path;
 
 import java.io.File;
 
 /**
  * EXPERIMENTAL
  * Create or modifies ClassLoader. The required pathRef parameter
  * will be used to add classpath elements.
  *
  * The classpath is a regular path. Currently only file components are
  * supported (future extensions may allow URLs).
  *
  * You can modify the core loader by not specifying any name or using
  * "ant.coreLoader". (the core loader is used to load system ant
  * tasks and for taskdefs that don't specify an explicit path).
  *
  * Taskdef and typedef can use the loader you create if the name follows
  * the "ant.loader.NAME" pattern. NAME will be used as a pathref when
  * calling taskdef.
  *
  * This tasks will not modify the core loader if "build.sysclasspath=only"
  *
  * The typical use is:
  * <pre>
  *  &lt;path id="ant.deps" &gt;
  *     &lt;fileset dir="myDir" &gt;
  *        &lt;include name="junit.jar, bsf.jar, js.jar, etc"/&gt;
  *     &lt;/fileset&gt;
  *  &lt;/path&gt;
  *
  *  &lt;classloader pathRef="ant.deps" /&gt;
  *
  * </pre>
  *
  * @author Costin Manolache
  */
 public class Classloader extends Task {
     /** @see MagicNames#SYSTEM_LOADER_REF */
     public static final String SYSTEM_LOADER_REF = MagicNames.SYSTEM_LOADER_REF;
 
     private String name = null;
     private Path classpath;
     private boolean reset = false;
     private boolean parentFirst = true;
     private String parentName = null;
 
     /**
      * Default constructor
      */
     public Classloader() {
     }
 
     /** Name of the loader. If none, the default loader will be modified
      *
      * @param name the name of this loader
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /**
      * Reset the classloader, if it already exists. A new loader will
      * be created and all the references to the old one will be removed.
      * (it is not possible to remove paths from a loader). The new
      * path will be used.
      *
      * @param b true if the loader is to be reset.
      */
     public void setReset(boolean b) {
         this.reset = b;
     }
 
     public void setReverse(boolean b) {
         this.parentFirst = !b;
     }
 
     public void setParentFirst(boolean b) {
         this.parentFirst = b;
     }
 
     // TODO: add exceptions for delegation or reverse
 
     // TODO
     public void setParentName(String name) {
         this.parentName = name;
     }
 
 
     /** Specify which path will be used. If the loader already exists
      *  and is an AntClassLoader (or any other loader we can extend),
      *  the path will be added to the loader.
      */
     public void setClasspathRef(Reference pathRef) throws BuildException {
-        classpath = (Path) pathRef.getReferencedObject(project);
+        classpath = (Path) pathRef.getReferencedObject(getProject());
     }
 
     /**
      * Set the classpath to be used when searching for component being defined
      *
      * @param classpath an Ant Path object containing the classpath.
      */
     public void setClasspath(Path classpath) {
         if (this.classpath == null) {
             this.classpath = classpath;
         } else {
             this.classpath.append(classpath);
         }
     }
 
     public Path createClasspath() {
         if (this.classpath == null) {
             this.classpath = new Path(null);
         }
         return this.classpath.createPath();
     }
 
 
     public void execute() {
         try {
             // Gump friendly - don't mess with the core loader if only classpath
-            if ("only".equals(project.getProperty("build.sysclasspath"))
+            if ("only".equals(getProject().getProperty("build.sysclasspath"))
                 && (name == null || SYSTEM_LOADER_REF.equals(name))) {
                 log("Changing the system loader is disabled "
                     + "by build.sysclasspath=only", Project.MSG_WARN);
                 return;
             }
 
             String loaderName = (name == null) ? SYSTEM_LOADER_REF : name;
 
-            Object obj = project.getReference(loaderName);
+            Object obj = getProject().getReference(loaderName);
             if (reset) {
                 // Are any other references held ? Can we 'close' the loader
                 // so it removes the locks on jars ?
                 obj = null; // a new one will be created.
             }
 
             // XXX maybe use reflection to addPathElement (other patterns ?)
             if (obj != null && !(obj instanceof AntClassLoader)) {
                 log("Referenced object is not an AntClassLoader",
                         Project.MSG_ERR);
                 return;
             }
 
             AntClassLoader acl = (AntClassLoader) obj;
 
             if (acl == null) {
                 // Construct a new class loader
                 Object parent = null;
                 if (parentName != null) {
-                    parent = project.getReference(parentName);
+                    parent = getProject().getReference(parentName);
                     if (!(parent instanceof ClassLoader)) {
                         parent = null;
                     }
                 }
                 // TODO: allow user to request the system or no parent
                 if (parent == null) {
                     parent = this.getClass().getClassLoader();
                 }
 
                 if (name == null) {
                     // The core loader must be reverse
                     //reverse=true;
                 }
-                project.log("Setting parent loader " + name + " "
+                getProject().log("Setting parent loader " + name + " "
                     + parent + " " + parentFirst, Project.MSG_DEBUG);
 
                 // The param is "parentFirst"
                 acl = new AntClassLoader((ClassLoader) parent,
-                        project, classpath, parentFirst);
+                         getProject(), classpath, parentFirst);
 
-                project.addReference(loaderName, acl);
+                getProject().addReference(loaderName, acl);
 
                 if (name == null) {
                     // This allows the core loader to load optional tasks
                     // without delegating
                     acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
-                    project.setCoreLoader(acl);
+                    getProject().setCoreLoader(acl);
                 }
             }
             if (classpath != null) {
                 String[] list = classpath.list();
                 for (int i = 0; i < list.length; i++) {
                     File f = new File(list[i]);
                     if (f.exists()) {
                         acl.addPathElement(f.getAbsolutePath());
                         log("Adding to class loader " +  acl + " " + f.getAbsolutePath(),
                                 Project.MSG_DEBUG);
                     }
                 }
             }
 
             // XXX add exceptions
 
         } catch (Exception ex) {
             ex.printStackTrace();
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Concat.java b/src/main/org/apache/tools/ant/taskdefs/Concat.java
index bb2dea4ac..d10ec3f69 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Concat.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Concat.java
@@ -1,871 +1,870 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.Reader;
 import java.io.StringReader;
 import java.io.Writer;
 import java.util.Enumeration;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.filters.util.ChainReaderHelper;
 import org.apache.tools.ant.types.FileList;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.FilterChain;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * This class contains the 'concat' task, used to concatenate a series
  * of files into a single stream. The destination of this stream may
  * be the system console, or a file. The following is a sample
  * invocation:
  *
  * <pre>
  * &lt;concat destfile=&quot;${build.dir}/index.xml&quot;
  *   append=&quot;false&quot;&gt;
  *
  *   &lt;fileset dir=&quot;${xml.root.dir}&quot;
  *     includes=&quot;*.xml&quot; /&gt;
  *
  * &lt;/concat&gt;
  * </pre>
  *
  * @author <a href="mailto:derek@activate.net">Derek Slager</a>
  * @author Peter Reilly
  */
 public class Concat extends Task {
 
     // The size of buffers to be used
     private static final int BUFFER_SIZE = 8192;
 
     // Attributes.
 
     /**
      * The destination of the stream. If <code>null</code>, the system
      * console is used.
      */
     private File destinationFile = null;
 
     /**
      * Whether or not the stream should be appended if the destination file
      * exists.
      * Defaults to <code>false</code>.
      */
     private boolean append = false;
 
     /**
      * Stores the input file encoding.
      */
     private String encoding = null;
 
     /** Stores the output file encoding. */
     private String outputEncoding = null;
 
     // Child elements.
 
     /**
      * This buffer stores the text within the 'concat' element.
      */
     private StringBuffer textBuffer;
 
     /**
      * Stores a collection of file sets and/or file lists, used to
      * select multiple files for concatenation.
      */
     private Vector sources = new Vector();
 
     /** for filtering the concatenated */
     private Vector        filterChains = null;
     /** ignore dates on input files */
     private boolean       forceOverwrite = true;
     /** String to place at the start of the concatented stream */
     private TextElement   footer;
     /** String to place at the end of the concatented stream */
     private TextElement   header;
     /** add missing line.separator to files **/
     private boolean       fixLastLine = false;
     /** endofline for fixlast line */
     private String       eolString = System.getProperty("line.separator");
     /** outputwriter */
     private Writer       outputWriter = null;
 
     /** internal variable - used to collect the source files from sources */
     private Vector        sourceFiles = new Vector();
 
     /** 1.1 utilities and copy utilities */
     private static FileUtils     fileUtils = FileUtils.newFileUtils();
 
     // Attribute setters.
 
     /**
      * Sets the destination file, or uses the console if not specified.
      * @param destinationFile the destination file
      */
     public void setDestfile(File destinationFile) {
         this.destinationFile = destinationFile;
     }
 
     /**
      * Sets the behavior when the destination file exists. If set to
      * <code>true</code> the stream data will be appended to the
      * existing file, otherwise the existing file will be
      * overwritten. Defaults to <code>false</code>.
      * @param append if true append to the file.
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
     /**
      * Sets the character encoding
      * @param encoding the encoding of the input stream and unless
      *        outputencoding is set, the outputstream.
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
         if (outputEncoding == null) {
             outputEncoding = encoding;
         }
     }
 
     /**
      * Sets the character encoding for outputting
      * @param outputEncoding the encoding for the output file
      * @since Ant 1.6
      */
     public void setOutputEncoding(String outputEncoding) {
         this.outputEncoding = outputEncoding;
     }
 
     /**
      * Force overwrite existing destination file
      * @param force if true always overwrite, otherwise only overwrite
      *              if the output file is older any of the input files.
      * @since Ant 1.6
      */
     public void setForce(boolean force) {
         this.forceOverwrite = force;
     }
 
     // Nested element creators.
 
     /**
      * Path of files to concatenate.
      * @return the path used for concatenating
      * @since Ant 1.6
      */
      public Path createPath() {
         Path path = new Path(getProject());
         sources.addElement(path);
         return path;
     }
 
     /**
      * Set of files to concatenate.
      * @param set the set of files
      */
     public void addFileset(FileSet set) {
         sources.addElement(set);
     }
 
     /**
      * List of files to concatenate.
      * @param list the list of files
      */
     public void addFilelist(FileList list) {
         sources.addElement(list);
     }
 
     /**
      * Adds a FilterChain.
      * @param filterChain a filterchain to filter the concatenated input
      * @since Ant 1.6
      */
     public void addFilterChain(FilterChain filterChain) {
         if (filterChains == null) {
             filterChains = new Vector();
         }
         filterChains.addElement(filterChain);
     }
 
     /**
      * This method adds text which appears in the 'concat' element.
      * @param text the text to be concated.
      */
     public void addText(String text) {
         if (textBuffer == null) {
             // Initialize to the size of the first text fragment, with
             // the hopes that it's the only one.
             textBuffer = new StringBuffer(text.length());
         }
 
         // Append the fragment -- we defer property replacement until
         // later just in case we get a partial property in a fragment.
         textBuffer.append(text);
     }
 
     /**
      * Add a header to the concatenated output
      * @param header the header
      * @since Ant 1.6
      */
     public void addHeader(TextElement header) {
         this.header = header;
     }
 
     /**
      * Add a footer to the concatenated output
      * @param footer the footer
      * @since Ant 1.6
      */
     public void addFooter(TextElement footer) {
         this.footer = footer;
     }
 
     /**
      * Append line.separator to files that do not end
      * with a line.separator, default false.
      * @param fixLastLine if true make sure each input file has
      *                    new line on the concatenated stream
      * @since Ant 1.6
      */
     public void setFixLastLine(boolean fixLastLine) {
         this.fixLastLine = fixLastLine;
     }
 
     /**
      * Specify the end of line to find and to add if
      * not present at end of each input file. This attribute
      * is used in conjuction with fixlastline.
      * @param crlf the type of new line to add -
      *              cr, mac, lf, unix, crlf, or dos
      * @since Ant 1.6
      */
     public void setEol(FixCRLF.CrLf crlf) {
         String s = crlf.getValue();
         if (s.equals("cr") || s.equals("mac")) {
             eolString = "\r";
         } else if (s.equals("lf") || s.equals("unix")) {
             eolString = "\n";
         } else if (s.equals("crlf") || s.equals("dos")) {
             eolString = "\r\n";
         }
     }
 
     /**
      * set the output writer, this is to allow
      * concat to be used as a nested element
      * @param outputWriter the output writer
      * @since Ant 1.6
      */
     public void setWriter(Writer outputWriter) {
         this.outputWriter = outputWriter;
     }
 
     /**
      * This method performs the concatenation.
      */
     public void execute() {
 
         // treat empty nested text as no text
         sanitizeText();
 
         if (destinationFile != null && outputWriter != null) {
             throw new BuildException(
                 "Cannot specify both a destination file and an output writer");
         }
 
         // Sanity check our inputs.
         if (sources.size() == 0 && textBuffer == null) {
             // Nothing to concatenate!
             throw new BuildException(
                 "At least one file must be provided, or some text.");
         }
 
         // If using filesets, disallow inline text. This is similar to
         // using GNU 'cat' with file arguments -- stdin is simply
         // ignored.
         if (sources.size() > 0 && textBuffer != null) {
             throw new BuildException(
                 "Cannot include inline text when using filesets.");
         }
 
         // Iterate thru the sources - paths, filesets and filelists
         for (Enumeration e = sources.elements(); e.hasMoreElements();) {
             Object o = e.nextElement();
             if (o instanceof Path) {
                 Path path = (Path) o;
                 checkAddFiles(null, path.list());
 
             } else if (o instanceof FileSet) {
                 FileSet fileSet = (FileSet) o;
                 DirectoryScanner scanner =
                     fileSet.getDirectoryScanner(getProject());
                 checkAddFiles(fileSet.getDir(getProject()),
                               scanner.getIncludedFiles());
 
             } else if (o instanceof FileList) {
                 FileList fileList = (FileList) o;
                 checkAddFiles(fileList.getDir(getProject()),
                               fileList.getFiles(getProject()));
             }
         }
 
         // check if the files are outofdate
         if (destinationFile != null && !forceOverwrite
             && (sourceFiles.size() > 0) && destinationFile.exists()) {
             boolean outofdate = false;
             for (int i = 0; i < sourceFiles.size(); ++i) {
                 File file = (File) sourceFiles.elementAt(i);
                 if (file.lastModified() > destinationFile.lastModified()) {
                     outofdate = true;
                     break;
                 }
             }
             if (!outofdate) {
                 log(destinationFile + " is up-to-date.", Project.MSG_VERBOSE);
                 return; // no need to do anything
             }
         }
 
         // Do nothing if all the sources are not present
         // And textBuffer is null
         if (textBuffer == null && sourceFiles.size() == 0
             && header == null && footer == null) {
             log("No existing files and no nested text, doing nothing",
                 Project.MSG_INFO);
             return;
         }
 
         cat();
     }
 
     /**
      * Reset state to default.
      */
     public void reset() {
         append = false;
         forceOverwrite = true;
         destinationFile = null;
         encoding = null;
         outputEncoding = null;
         fixLastLine = false;
         sources.removeAllElements();
         sourceFiles.removeAllElements();
         filterChains = null;
         footer = null;
         header = null;
     }
 
     private void checkAddFiles(File base, String[] filenames) {
         for (int i = 0; i < filenames.length; ++i) {
             File file = new File(base, filenames[i]);
             if (!file.exists()) {
                 log("File " + file + " does not exist.", Project.MSG_ERR);
                 continue;
             }
             if (destinationFile != null
                 && fileUtils.fileNameEquals(destinationFile, file)) {
                 throw new BuildException("Input file \""
                                          + file + "\" "
                                          + "is the same as the output file.");
             }
             sourceFiles.addElement(file);
         }
     }
 
     /** perform the concatenation */
     private void cat() {
         OutputStream os = null;
         Reader       reader = null;
         char[]       buffer = new char[BUFFER_SIZE];
 
         try {
 
             PrintWriter writer = null;
 
             if (outputWriter != null) {
                 writer = new PrintWriter(outputWriter);
             } else {
                 if (destinationFile == null) {
                     // Log using WARN so it displays in 'quiet' mode.
                     os = new LogOutputStream(this, Project.MSG_WARN);
                 } else {
                     // ensure that the parent dir of dest file exists
                     File parent = fileUtils.getParentFile(destinationFile);
                     if (!parent.exists()) {
                         parent.mkdirs();
                     }
 
                     os = new FileOutputStream(destinationFile.getAbsolutePath(),
                                               append);
                 }
 
                 if (outputEncoding == null) {
                     writer = new PrintWriter(
                         new BufferedWriter(
                             new OutputStreamWriter(os)));
                 } else {
                     writer = new PrintWriter(
                         new BufferedWriter(
                             new OutputStreamWriter(os, outputEncoding)));
                 }
             }
 
             if (header != null) {
                 if (header.getFiltering()) {
                     concatenate(
                         buffer, writer, new StringReader(header.getValue()));
                 } else {
                     writer.print(header.getValue());
                 }
             }
 
             if (textBuffer != null) {
                 reader = new StringReader(
                     getProject().replaceProperties(textBuffer.substring(0)));
             } else {
                 reader =  new MultiReader();
             }
 
             concatenate(buffer, writer, reader);
 
             if (footer != null) {
                 if (footer.getFiltering()) {
                     concatenate(
                         buffer, writer, new StringReader(footer.getValue()));
                 } else {
                     writer.print(footer.getValue());
                 }
             }
 
             writer.flush();
             if (os != null) {
                 os.flush();
             }
 
         } catch (IOException ioex) {
             throw new BuildException("Error while concatenating: "
                                      + ioex.getMessage(), ioex);
         } finally {
             if (reader != null) {
                 try {
                     reader.close();
                 } catch (IOException ignore) {
                     // ignore
                 }
             }
             if (os != null) {
                 try {
                     os.close();
                 } catch (IOException ignore) {
                     // ignore
                 }
             }
         }
     }
 
 
     /** Concatenate a single reader to the writer using buffer */
     private void concatenate(char[] buffer, Writer writer, Reader in)
         throws IOException {
         if (filterChains != null) {
             ChainReaderHelper helper = new ChainReaderHelper();
             helper.setBufferSize(BUFFER_SIZE);
             helper.setPrimaryReader(in);
             helper.setFilterChains(filterChains);
             helper.setProject(getProject());
             in = new BufferedReader(helper.getAssembledReader());
         }
 
         while (true) {
             int nRead = in.read(buffer, 0, buffer.length);
             if (nRead == -1) {
                 break;
             }
             writer.write(buffer, 0, nRead);
         }
 
         writer.flush();
     }
 
     /**
      * Treat empty nested text as no text.
      *
      * <p>Depending on the XML parser, addText may have been called
      * for &quot;ignorable whitespace&quot; as well.</p>
      */
     private void sanitizeText() {
         if (textBuffer != null) {
             if (textBuffer.substring(0).trim().length() == 0) {
                 textBuffer = null;
             }
         }
     }
 
     /**
      * sub element points to a file or contains text
      */
     public static class TextElement {
         private String   value = "";
         private boolean  trimLeading = false;
         private boolean  trim = false;
         private boolean  filtering = true;
         private String   encoding = null;
 
         /**
          * whether to filter the text in this element
          * or not.
          *
          * @param filtering true if the text should be filtered.
          *                  the default value is true.
          */
         public void setFiltering(boolean filtering) {
             this.filtering = filtering;
         }
 
         /** return the filtering attribute */
         private boolean getFiltering() {
             return filtering;
         }
 
         /**
          * The encoding of the text element
          *
          * @param encoding the name of the charset used to encode
          */
         public void setEncoding(String encoding) {
             this.encoding = encoding;
         }
 
         /**
          * set the text using a file
          * @param file the file to use
          * @throws BuildException if the file does not exist, or cannot be
          *                        read
          */
         public void setFile(File file) {
             // non-existing files are not allowed
             if (!file.exists()) {
                 throw new BuildException("File " + file + " does not exist.");
             }
 
             BufferedReader reader = null;
             try {
                 if (this.encoding == null) {
                     reader = new BufferedReader(new FileReader(file));
                 } else {
                     reader = new BufferedReader(
                         new InputStreamReader(new FileInputStream(file),
                                               this.encoding));
                 }
                 value = fileUtils.readFully(reader);
             } catch (IOException ex) {
                 throw new BuildException(ex);
             } finally {
                 if (reader != null) {
                     try {
                         reader.close();
                     } catch (Throwable t) {
                         // ignore
                     }
                 }
             }
         }
 
         /**
          * set the text using inline
          * @param value the text to place inline
          */
         public void addText(String value) {
             this.value += value;
         }
 
         /**
          * s:^\s*:: on each line of input
          * @param strip if true do the trim
          */
         public void setTrimLeading(boolean strip) {
             this.trimLeading = strip;
         }
 
         /**
          * whether to call text.trim()
          * @param trim if true trim the text
          */
         public void setTrim(boolean trim) {
             this.trim = trim;
         }
 
         /**
          * @return the text, after possible trimming
          */
         public String getValue() {
             if (value == null) {
                 value = "";
             }
             if (value.trim().length() == 0) {
                 value = "";
             }
             if (trimLeading) {
                 char[] current = value.toCharArray();
                 StringBuffer b = new StringBuffer(current.length);
                 boolean startOfLine = true;
                 int pos = 0;
                 while (pos < current.length) {
                     char ch = current[pos++];
                     if (startOfLine) {
                         if (ch == ' ' || ch == '\t') {
                             continue;
                         }
                         startOfLine = false;
                     }
                     b.append(ch);
                     if (ch == '\n' || ch == '\r') {
                         startOfLine = true;
                     }
                 }
                 value = b.toString();
             }
             if (trim) {
                 value = value.trim();
             }
             return value;
         }
     }
 
     /**
      * This class reads from each of the source files in turn.
      * The concatentated result can then be filtered as
      * a single stream.
      */
     private class MultiReader extends Reader {
         private int    pos = 0;
         private Reader reader = null;
         private int    lastPos = 0;
         private char[] lastChars = new char[eolString.length()];
         private boolean needAddSeparator = false;
 
         private Reader getReader() throws IOException {
             if (reader == null) {
                 if (encoding == null) {
                     reader = new BufferedReader(
                         new FileReader((File) sourceFiles.elementAt(pos)));
                 } else {
                     // invoke the zoo of io readers
                     reader = new BufferedReader(
                         new InputStreamReader(
                             new FileInputStream(
                                 (File) sourceFiles.elementAt(pos)),
                             encoding));
                 }
                 for (int i = 0; i < lastChars.length; ++i) {
                     lastChars[i] = 0;
                 }
             }
             return reader;
         }
 
         /**
          * Read a character from the current reader object. Advance
          * to the next if the reader is finished.
          * @return the character read, -1 for EOF on the last reader.
          * @exception IOException - possiblly thrown by the read for a reader
          *            object.
          */
         public int read() throws IOException {
             if (needAddSeparator) {
                 int ret = eolString.charAt(lastPos++);
                 if (lastPos >= eolString.length()) {
                     lastPos = 0;
                     needAddSeparator = false;
                 }
                 return ret;
             }
 
             while (pos < sourceFiles.size()) {
                 int ch = getReader().read();
                 if (ch == -1) {
                     reader.close();
                     reader = null;
                     if (fixLastLine && isMissingEndOfLine()) {
                         needAddSeparator = true;
                         lastPos = 0;
                     }
                 } else {
                     addLastChar((char) ch);
                     return ch;
                 }
                 pos++;
             }
             return -1;
         }
 
         /**
          * Read into the buffer <code>cbuf</code>.
          * @param cbuf The array to be read into.
          * @param off The offset.
          * @param len The length to read.
          * @exception IOException - possiblely thrown by the reads to the
          *            reader objects.
          */
         public int read(char[] cbuf, int off, int len)
             throws IOException {
 
             int amountRead = 0;
-            int iOff = off;
             while (pos < sourceFiles.size() || (needAddSeparator)) {
                 if (needAddSeparator) {
                     cbuf[off] = eolString.charAt(lastPos++);
                     if (lastPos >= eolString.length()) {
                         lastPos = 0;
                         needAddSeparator = false;
                         pos++;
                     }
                     len--;
                     off++;
                     amountRead++;
                     if (len == 0) {
                         return amountRead;
                     }
                     continue;
                 }
 
                 int nRead = getReader().read(cbuf, off, len);
                 if (nRead == -1 || nRead == 0) {
                     reader.close();
                     reader = null;
                     if (fixLastLine && isMissingEndOfLine()) {
                         needAddSeparator = true;
                         lastPos = 0;
                     } else {
                         pos++;
                     }
                 } else {
                     if (fixLastLine) {
                         for (int i = nRead;
                                  i > (nRead - lastChars.length);
                                  --i) {
                             if (i < 0) {
                                 break;
                             }
                             addLastChar(cbuf[off + i]);
                         }
                     }
                     len -= nRead;
                     off += nRead;
                     amountRead += nRead;
                     if (len == 0) {
                         return amountRead;
                     }
                 }
             }
             if (amountRead == 0) {
                 return -1;
             } else {
                 return amountRead;
             }
         }
 
         /**
          * Close the current reader
          */
         public void close() throws IOException {
             if (reader != null) {
                 reader.close();
             }
         }
         /**
          * if checking for end of line at end of file
          * add a character to the lastchars buffer
          */
         private void addLastChar(char ch) {
             for (int i = lastChars.length - 2; i >= 0; --i) {
                 lastChars[i] = lastChars[i + 1];
             }
             lastChars[lastChars.length - 1] = ch;
         }
 
         /**
          * return true if the lastchars buffer does
          * not contain the lineseparator
          */
         private boolean isMissingEndOfLine() {
             for (int i = 0; i < lastChars.length; ++i) {
                 if (lastChars[i] != eolString.charAt(i)) {
                     return true;
                 }
             }
             return false;
         }
     }
 
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/Definer.java b/src/main/org/apache/tools/ant/taskdefs/Definer.java
index 78940781a..e36cdc283 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Definer.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Definer.java
@@ -1,508 +1,507 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.Enumeration;
 import java.util.Locale;
 import java.util.Properties;
 
 import org.apache.tools.ant.AntTypeDefinition;
 import org.apache.tools.ant.ComponentHelper;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 
 /**
  * Base class for Taskdef and Typedef - handles all
  * the attributes for Typedef. The uri and class
  * handling is handled by DefBase
  *
  * @author Costin Manolache
  * @author Stefan Bodewig
  * @author Peter Reilly
  *
  * @since Ant 1.4
  */
 public abstract class Definer extends DefBase {
     private String name;
     private String classname;
     private File file;
     private String resource;
 
     private   int    format = Format.PROPERTIES;
     private   boolean definerSet = false;
-    private   ClassLoader internalClassLoader;
     private   int         onError = OnError.FAIL;
     private   String      adapter;
     private   String      adaptTo;
 
     private   Class       adapterClass;
     private   Class       adaptToClass;
 
     /**
      * Enumerated type for onError attribute
      *
      * @see EnumeratedAttribute
      */
     public static class OnError extends EnumeratedAttribute {
         /** Enumerated values */
         public static final int  FAIL = 0, REPORT = 1, IGNORE = 2;
         /**
          * Constructor
          */
         public OnError() {
             super();
         }
 
         /**
          * Constructor using a string.
          * @param value the value of the attribute
          */
         public OnError(String value) {
             setValue(value);
         }
 
         /**
          * get the values
          * @return an array of the allowed values for this attribute.
          */
         public String[] getValues() {
             return new String[] {"fail", "report", "ignore"};
         }
     }
 
     /**
      * Enumerated type for format attribute
      *
      * @see EnumeratedAttribute
      */
     public static class Format extends EnumeratedAttribute {
         /** Enumerated values */
         public static final int PROPERTIES = 0, XML = 1;
 
         /**
          * get the values
          * @return an array of the allowed values for this attribute.
          */
         public String[] getValues() {
             return new String[] {"properties", "xml"};
         }
     }
 
     /**
      * What to do if there is an error in loading the class.
      * <dl>
      *   <li>error - throw build exception</li>
      *   <li>report - output at warning level</li>
      *   <li>ignore - output at debug level</li>
      * </dl>
      *
      * @param onError an <code>OnError</code> value
      */
     public void setOnError(OnError onError) {
         this.onError = onError.getIndex();
     }
 
     /**
      * Sets the format of the file or resource
      * @param format the enumerated value - xml or properties
      */
     public void setFormat(Format format) {
         this.format = format.getIndex();
     }
 
     /**
      * @return the name for this definition
      */
     public String getName() {
         return name;
     }
 
     /**
      * @return the file containing definitions
      */
     public File getFile() {
         return file;
     }
 
     /**
      * @return the resource containing definitions
      */
     public String getResource() {
         return resource;
     }
 
 
     /**
      * Run the definition.
      *
      * @exception BuildException if an error occurs
      */
     public void execute() throws BuildException {
         ClassLoader al = createLoader();
 
         if (!definerSet) {
             throw new BuildException(
                 "name, file or resource attribute of "
                 + getTaskName() + " is undefined", getLocation());
         }
 
         if (name != null) {
             if (classname == null) {
                 throw new BuildException(
                     "classname attribute of " + getTaskName() + " element "
                     + "is undefined", getLocation());
             }
             addDefinition(al, name, classname);
         } else {
             if (classname != null) {
                 String msg = "You must not specify classname "
                     + "together with file or resource.";
                 throw new BuildException(msg, getLocation());
             }
             URL url = null;
             if (file != null) {
                 url = fileToURL();
             }
             if (resource != null) {
                 url = resourceToURL(al);
             }
 
             if (url == null) {
                 return;
             }
 
             if (url.toString().toLowerCase(Locale.US).endsWith(".xml")) {
                 format = Format.XML;
             }
 
             if (format == Format.PROPERTIES) {
                 loadProperties(al, url);
             } else {
                 loadAntlib(al, url);
             }
         }
     }
 
     private URL fileToURL() {
         if (!(file.exists())) {
             log("File " + file + " does not exist", Project.MSG_WARN);
             return null;
         }
         if (!(file.isFile())) {
             log("File " + file + " is not a file", Project.MSG_WARN);
             return null;
         }
         try {
             return file.toURL();
         } catch (Exception ex) {
             log("File " + file + " cannot use as URL: "
                 + ex.toString(), Project.MSG_WARN);
             return null;
         }
     }
 
     private URL resourceToURL(ClassLoader classLoader) {
         URL ret = classLoader.getResource(resource);
         if (ret == null) {
             if (onError != OnError.IGNORE) {
                 log("Could not load definitions from resource "
                     + resource + ". It could not be found.",
                     Project.MSG_WARN);
             }
         }
         return ret;
     }
 
     /**
      * Load type definitions as properties from a url.
      *
      * @param al the classloader to use
      * @param url the url to get the definitions from
      */
     protected void loadProperties(ClassLoader al, URL url) {
         InputStream is = null;
         try {
             is = url.openStream();
             if (is == null) {
                 log("Could not load definitions from " + url,
                     Project.MSG_WARN);
                 return;
             }
             Properties props = new Properties();
             props.load(is);
             Enumeration keys = props.keys();
             while (keys.hasMoreElements()) {
                 name = ((String) keys.nextElement());
                 classname = props.getProperty(name);
                 addDefinition(al, name, classname);
             }
         } catch (IOException ex) {
             throw new BuildException(ex, getLocation());
         } finally {
             if (is != null) {
                 try {
                     is.close();
                 } catch (IOException e) {
                     // ignore
                 }
             }
         }
     }
 
     /**
      * Load an antlib from a url.
      *
      * @param classLoader the classloader to use.
      * @param url the url to load the definitions from.
      */
     private void loadAntlib(ClassLoader classLoader, URL url) {
         try {
             Antlib antlib = Antlib.createAntlib(getProject(), url);
             antlib.setClassLoader(classLoader);
             antlib.setURI(getUri());
             antlib.perform();
         } catch (BuildException ex) {
             Location exLocation = ex.getLocation();
             if (exLocation == null) {
                 throw ex;
             }
             throw new BuildException(
                 "Error executing antlib"
                 + System.getProperty("line.separator")
                 + exLocation.toString()
                 + " " + ex.getMessage());
         }
     }
 
     /**
      * Name of the property file  to load
      * ant name/classname pairs from.
      * @param file the file
      */
     public void setFile(File file) {
         if (definerSet) {
             tooManyDefinitions();
         }
         definerSet = true;
         this.file = file;
     }
 
     /**
      * Name of the property resource to load
      * ant name/classname pairs from.
      * @param res the resource to use
      */
     public void setResource(String res) {
         if (definerSet) {
             tooManyDefinitions();
         }
         definerSet = true;
         this.resource = res;
     }
 
     /**
      * Name of the definition
      * @param name the name of the definition
      */
     public void setName(String name) {
         if (definerSet) {
             tooManyDefinitions();
         }
         definerSet = true;
         this.name = name;
     }
 
     /**
      * Returns the classname of the object we are defining.
      * May be <code>null</code>.
      * @return the class name
      */
     public String getClassname() {
         return classname;
     }
 
     /**
      * The full class name of the object being defined.
      * Required, unless file or resource have
      * been specified.
      * @param classname the name of the class
      */
     public void setClassname(String classname) {
         this.classname = classname;
     }
 
     /**
      * Set the class name of the adapter class.
      * An adapter class is used to proxy the
      * definition class. It is used if the
      * definition class is not assignable to
      * the adaptto class, or if the adaptto
      * class is not present.
      *
      * @param adapter the name of the adapter class
      */
 
     public void setAdapter(String adapter) {
         this.adapter = adapter;
     }
 
     /**
      * Set the adapter class.
      *
      * @param adapterClass the class to use to adapt the definition class
      */
     protected void setAdapterClass(Class adapterClass) {
         this.adapterClass = adapterClass;
     }
 
     /**
      * Set the classname of the class that the definition
      * must be compatible with, either directly or
      * by use of the adapter class.
      *
      * @param adaptTo the name of the adaptto class
      */
     public void setAdaptTo(String adaptTo) {
         this.adaptTo = adaptTo;
     }
 
     /**
      * Set the class for adaptToClass, to be
      * used by derived classes, used instead of
      * the adaptTo attribute.
      *
      * @param adaptToClass the class for adapto.
      */
     protected void setAdaptToClass(Class adaptToClass) {
         this.adaptToClass = adaptToClass;
     }
 
 
     /**
      * Add a definition using the attributes of Definer
      *
      * @param al the ClassLoader to use
      * @param name the name of the definition
      * @param classname the classname of the definition
      * @exception BuildException if an error occurs
      */
     protected void addDefinition(ClassLoader al, String name, String classname)
         throws BuildException {
         Class cl = null;
         try {
             try {
                 name = ProjectHelper.genComponentName(getUri(), name);
 
                 if (onError != OnError.IGNORE) {
                     cl = Class.forName(classname, true, al);
                 }
 
                 if (adapter != null) {
                     adapterClass = Class.forName(adapter, true, al);
                 }
 
                 if (adaptTo != null) {
                     adaptToClass = Class.forName(adaptTo, true, al);
                 }
 
                 AntTypeDefinition def = new AntTypeDefinition();
                 def.setName(name);
                 def.setClassName(classname);
                 def.setClass(cl);
                 def.setAdapterClass(adapterClass);
                 def.setAdaptToClass(adaptToClass);
                 def.setClassLoader(al);
                 if (cl != null) {
                     def.checkClass(getProject());
                 }
                 ComponentHelper.getComponentHelper(getProject())
                     .addDataTypeDefinition(def);
             } catch (ClassNotFoundException cnfe) {
                 String msg = getTaskName() + " class " + classname
                     + " cannot be found";
                 throw new BuildException(msg, cnfe, getLocation());
             } catch (NoClassDefFoundError ncdfe) {
                 String msg = getTaskName() + " A class needed by class "
                     + classname + " cannot be found: " + ncdfe.getMessage();
                 throw new BuildException(msg, ncdfe, getLocation());
             }
         } catch (BuildException ex) {
             switch (onError) {
                 case OnError.FAIL:
                     throw ex;
                 case OnError.REPORT:
                     log(ex.getLocation() + "Warning: " + ex.getMessage(),
                         Project.MSG_WARN);
                     break;
                 default:
                     log(ex.getLocation() + ex.getMessage(),
                         Project.MSG_DEBUG);
             }
         }
     }
 
     private void tooManyDefinitions() {
         throw new BuildException(
             "Only one of the attributes name,file,resource"
             + " can be set", getLocation());
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Execute.java b/src/main/org/apache/tools/ant/taskdefs/Execute.java
index 045e5d212..d26628db0 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Execute.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Execute.java
@@ -1,1154 +1,1154 @@
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
  * 4. The names "Ant" and "Apache Software
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
 
 import java.io.BufferedReader;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.StringReader;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Commandline;
 
 /**
  * Runs an external program.
  *
  * @author thomas.haas@softwired-inc.com
  * @author <a href="mailto:jtulley@novell.com">Jeff Tulley</a>
  * @author <a href="mailto:CHudak@arrowheadgrp.com">Charles Hudak</a>
  *
  * @since Ant 1.2
  *
  * @version $Revision$
  */
 public class Execute {
 
     /** Invalid exit code. **/
     public static final int INVALID = Integer.MAX_VALUE;
 
     private String[] cmdl = null;
     private String[] env = null;
     private int exitValue = INVALID;
     private ExecuteStreamHandler streamHandler;
     private ExecuteWatchdog watchdog;
     private File workingDirectory = null;
     private Project project = null;
     private boolean newEnvironment = false;
 
     /** Controls whether the VM is used to launch commands, where possible */
     private boolean useVMLauncher = true;
 
     private static String antWorkingDirectory = System.getProperty("user.dir");
     private static CommandLauncher vmLauncher = null;
     private static CommandLauncher shellLauncher = null;
     private static Vector procEnvironment = null;
     private boolean spawn = false;
 
     /** Used to destroy processes when the VM exits. */
     private static ProcessDestroyer processDestroyer = new ProcessDestroyer();
 
     /**
      * Builds a command launcher for the OS and JVM we are running under
      */
     static {
         // Try using a JDK 1.3 launcher
         try {
             if (Os.isFamily("openvms")) {
                 vmLauncher = new VmsCommandLauncher();
             } else if (!Os.isFamily("os/2")) {
                 vmLauncher = new Java13CommandLauncher();
             }
         } catch (NoSuchMethodException exc) {
             // Ignore and keep trying
         }
 
         if (Os.isFamily("mac")) {
             // Mac
             shellLauncher = new MacCommandLauncher(new CommandLauncher());
         } else if (Os.isFamily("os/2")) {
             // OS/2
             shellLauncher = new OS2CommandLauncher(new CommandLauncher());
         } else if (Os.isFamily("windows")) {
             // Windows.  Need to determine which JDK we're running in
 
             CommandLauncher baseLauncher;
             if (System.getProperty("java.version").startsWith("1.1")) {
                 // JDK 1.1
                 baseLauncher = new Java11CommandLauncher();
             } else {
                 // JDK 1.2
                 baseLauncher = new CommandLauncher();
             }
 
             if (!Os.isFamily("win9x")) {
                 // Windows XP/2000/NT
                 shellLauncher = new WinNTCommandLauncher(baseLauncher);
             } else {
                 // Windows 98/95 - need to use an auxiliary script
                 shellLauncher
                     = new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
             }
         } else if (Os.isFamily("netware")) {
             // NetWare.  Need to determine which JDK we're running in
             CommandLauncher baseLauncher;
             if (System.getProperty("java.version").startsWith("1.1")) {
                 // JDK 1.1
                 baseLauncher = new Java11CommandLauncher();
             } else {
                 // JDK 1.2
                 baseLauncher = new CommandLauncher();
             }
 
             shellLauncher
                 = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
         } else if (Os.isFamily("openvms")) {
             // the vmLauncher already uses the shell
             shellLauncher = vmLauncher;
         } else {
             // Generic
             shellLauncher = new ScriptCommandLauncher("bin/antRun",
                 new CommandLauncher());
         }
     }
 
     /**
      * set whether or not you want the process to be spawned
      * default is not spawned
      *
      * @param spawn if true you do not want ant to wait for the end of the process
      *
      * @since ant 1.6
      */
     public void setSpawn(boolean spawn) {
         this.spawn = spawn;
     }
 
     /**
      * Find the list of environment variables for this process.
      *
      * @return a vector containing the environment variables
      * the vector elements are strings formatted like variable = value
      */
     public static synchronized Vector getProcEnvironment() {
         if (procEnvironment != null) {
             return procEnvironment;
         }
 
         procEnvironment = new Vector();
         try {
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             Execute exe = new Execute(new PumpStreamHandler(out));
             exe.setCommandline(getProcEnvCommand());
             // Make sure we do not recurse forever
             exe.setNewenvironment(true);
             int retval = exe.execute();
             if (retval != 0) {
                 // Just try to use what we got
             }
 
             BufferedReader in =
                 new BufferedReader(new StringReader(toString(out)));
 
             if (Os.isFamily("openvms")) {
                 procEnvironment = addVMSLogicals(procEnvironment, in);
                 return procEnvironment;
             }
 
             String var = null;
             String line, lineSep = System.getProperty("line.separator");
             while ((line = in.readLine()) != null) {
                 if (line.indexOf('=') == -1) {
                     // Chunk part of previous env var (UNIX env vars can
                     // contain embedded new lines).
                     if (var == null) {
                         var = lineSep + line;
                     } else {
                         var += lineSep + line;
                     }
                 } else {
                     // New env var...append the previous one if we have it.
                     if (var != null) {
                         procEnvironment.addElement(var);
                     }
                     var = line;
                 }
             }
             // Since we "look ahead" before adding, there's one last env var.
             if (var != null) {
                 procEnvironment.addElement(var);
             }
         } catch (java.io.IOException exc) {
             exc.printStackTrace();
             // Just try to see how much we got
         }
         return procEnvironment;
     }
 
     private static String[] getProcEnvCommand() {
         if (Os.isFamily("os/2")) {
             // OS/2 - use same mechanism as Windows 2000
             String[] cmd = {"cmd", "/c", "set" };
             return cmd;
         } else if (Os.isFamily("windows")) {
             // Determine if we're running under XP/2000/NT or 98/95
             if (!Os.isFamily("win9x")) {
                 // Windows XP/2000/NT
                 String[] cmd = {"cmd", "/c", "set" };
                 return cmd;
             } else {
                 // Windows 98/95
                 String[] cmd = {"command.com", "/c", "set" };
                 return cmd;
             }
         } else if (Os.isFamily("z/os") || Os.isFamily("unix")) {
             // On most systems one could use: /bin/sh -c env
 
             // Some systems have /bin/env, others /usr/bin/env, just try
             String[] cmd = new String[1];
             if (new File("/bin/env").canRead()) {
                 cmd[0] = "/bin/env";
             } else if (new File("/usr/bin/env").canRead()) {
                 cmd[0] = "/usr/bin/env";
             } else {
                 // rely on PATH
                 cmd[0] = "env";
             }
             return cmd;
         } else if (Os.isFamily("netware") || Os.isFamily("os/400")) {
             // rely on PATH
             String[] cmd = {"env"};
             return cmd;
         } else if (Os.isFamily("openvms")) {
             String[] cmd = {"show", "logical"};
             return cmd;
         } else {
             // MAC OS 9 and previous
             //TODO: I have no idea how to get it, someone must fix it
             String[] cmd = null;
             return cmd;
         }
     }
 
     /**
      * ByteArrayOutputStream#toString doesn't seem to work reliably on
      * OS/390, at least not the way we use it in the execution
      * context.
      *
      * @param bos the output stream that one wants to read
      * @return the output stream as a string, read with
      * special encodings in the case of z/os and os/400
      *
      * @since Ant 1.5
      */
     public static String toString(ByteArrayOutputStream bos) {
         if (Os.isFamily("z/os")) {
             try {
                 return bos.toString("Cp1047");
             } catch (java.io.UnsupportedEncodingException e) {
             }
         } else if (Os.isFamily("os/400")) {
             try {
                 return bos.toString("Cp500");
             } catch (java.io.UnsupportedEncodingException e) {
             }
         }
         return bos.toString();
     }
 
     /**
      * Creates a new execute object using <code>PumpStreamHandler</code> for
      * stream handling.
      */
     public Execute() {
         this(new PumpStreamHandler(), null);
     }
 
 
     /**
      * Creates a new execute object.
      *
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler) {
         this(streamHandler, null);
     }
 
     /**
      * Creates a new execute object.
      *
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
      * @param watchdog a watchdog for the subprocess or <code>null</code> to
      *        to disable a timeout for the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler,
                    ExecuteWatchdog watchdog) {
         this.streamHandler = streamHandler;
         this.watchdog = watchdog;
     }
 
 
     /**
      * Returns the commandline used to create a subprocess.
      *
      * @return the commandline used to create a subprocess
      */
     public String[] getCommandline() {
         return cmdl;
     }
 
 
     /**
      * Sets the commandline of the subprocess to launch.
      *
      * @param commandline the commandline of the subprocess to launch
      */
     public void setCommandline(String[] commandline) {
         cmdl = commandline;
     }
 
     /**
      * Set whether to propagate the default environment or not.
      *
      * @param newenv whether to propagate the process environment.
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Returns the environment used to create a subprocess.
      *
      * @return the environment used to create a subprocess
      */
     public String[] getEnvironment() {
         if (env == null || newEnvironment) {
             return env;
         }
         return patchEnvironment();
     }
 
 
     /**
      * Sets the environment variables for the subprocess to launch.
      *
      * @param env array of Strings, each element of which has
      * an environment variable settings in format <em>key=value</em>
      */
     public void setEnvironment(String[] env) {
         this.env = env;
     }
 
     /**
      * Sets the working directory of the process to execute.
      *
      * <p>This is emulated using the antRun scripts unless the OS is
      * Windows NT in which case a cmd.exe is spawned,
      * or MRJ and setting user.dir works, or JDK 1.3 and there is
      * official support in java.lang.Runtime.
      *
      * @param wd the working directory of the process.
      */
     public void setWorkingDirectory(File wd) {
         if (wd == null || wd.getAbsolutePath().equals(antWorkingDirectory)) {
             workingDirectory = null;
         } else {
             workingDirectory = wd;
         }
     }
 
     /**
      * Set the name of the antRun script using the project's value.
      *
      * @param project the current project.
      *
      * @throws BuildException not clear when it is going to throw an exception, but
      * it is the method's signature
      */
     public void setAntRun(Project project) throws BuildException {
         this.project = project;
     }
 
     /**
      * Launch this execution through the VM, where possible, rather than through
      * the OS's shell. In some cases and operating systems using the shell will
      * allow the shell to perform additional processing such as associating an
      * executable with a script, etc
      *
      * @param useVMLauncher true if exec should launch through thge VM,
      *                   false if the shell should be used to launch the
      *                   command.
      */
     public void setVMLauncher(boolean useVMLauncher) {
         this.useVMLauncher = useVMLauncher;
     }
 
     /**
      * Creates a process that runs a command.
      *
      * @param project the Project, only used for logging purposes, may be null.
      * @param command the command to run
      * @param env the environment for the command
      * @param dir the working directory for the command
      * @param useVM use the built-in exec command for JDK 1.3 if available.
      * @return the process started
      * @throws IOException forwarded from the particular launcher used
      *
      * @since Ant 1.5
      */
     public static Process launch(Project project, String[] command,
                                  String[] env, File dir, boolean useVM)
         throws IOException {
         CommandLauncher launcher
             = vmLauncher != null ? vmLauncher : shellLauncher;
         if (!useVM) {
             launcher = shellLauncher;
         }
 
         return launcher.exec(project, command, env, dir);
     }
 
     /**
      * Runs a process defined by the command line and returns its exit status.
      *
      * @return the exit status of the subprocess or <code>INVALID</code>
      * @exception java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed
      */
     public int execute() throws IOException {
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
 
         try {
             streamHandler.setProcessInputStream(process.getOutputStream());
             streamHandler.setProcessOutputStream(process.getInputStream());
             streamHandler.setProcessErrorStream(process.getErrorStream());
         } catch (IOException e) {
             process.destroy();
             throw e;
         }
         streamHandler.start();
 
         try {
             // add the process to the list of those to destroy if the VM exits
             //
             processDestroyer.add(process);
 
             if (watchdog != null) {
                 watchdog.start(process);
             }
             waitFor(process);
 
             if (watchdog != null) {
                 watchdog.stop();
             }
             streamHandler.stop();
 
             if (watchdog != null) {
                 watchdog.checkException();
             }
             return getExitValue();
         } finally {
             // remove the process to the list of those to destroy if the VM exits
             //
             processDestroyer.remove(process);
         }
     }
 
     /**
      * Starts a process defined by the command line.
      * Ant will not wait for this process, nor log its output
      *
      * @throws java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed
      * @since ant 1.6
      */
     public void spawn() throws IOException {
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
         if (Os.isFamily("windows")) {
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 project.log("interruption in the sleep after having spawned a process",
                     Project.MSG_VERBOSE);
             }
         }
         project.log("spawned process " + process.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * wait for a given process
      *
      * @param process the process one wants to wait for
      */
     protected void waitFor(Process process) {
         try {
             process.waitFor();
             setExitValue(process.exitValue());
         } catch (InterruptedException e) {
             process.destroy();
         }
     }
 
     /**
      * set the exit value
      *
      * @param value exit value of the process
      */
     protected void setExitValue(int value) {
         exitValue = value;
     }
 
     /**
      * Query the exit value of the process.
      * @return the exit value, 1 if the process was killed,
      * or Execute.INVALID if no exit value has been received
      */
     public int getExitValue() {
         return exitValue;
     }
 
     /**
      * Checks whether <code>exitValue</code> signals a failure on the current
      * system (OS specific).
      *
      * <p><b>Note</b> that this method relies on the conventions of
      * the OS, it will return false results if the application you are
      * running doesn't follow these conventions.  One notable
      * exception is the Java VM provided by HP for OpenVMS - it will
      * return 0 if successful (like on any other platform), but this
      * signals a failure on OpenVMS.  So if you execute a new Java VM
      * on OpenVMS, you cannot trust this method.</p>
      *
      * @param exitValue the exit value (return code) to be checked
      * @return <code>true</code> if <code>exitValue</code> signals a failure
      */
     public static boolean isFailure(int exitValue) {
         if (Os.isFamily("openvms")) {
             // odd exit value signals failure
             return (exitValue % 2) == 0;
         } else {
             // non zero exit value signals failure
             return exitValue != 0;
         }
     }
 
     /**
      * test for an untimely death of the process
      * @return true iff a watchdog had to kill the process
      * @since Ant 1.5
      */
     public boolean killedProcess() {
         return watchdog != null && watchdog.killedProcess();
     }
 
     /**
      * Patch the current environment with the new values from the user.
      * @return the patched environment
      */
     private String[] patchEnvironment() {
         Vector osEnv = (Vector) getProcEnvironment().clone();
         for (int i = 0; i < env.length; i++) {
             int pos = env[i].indexOf('=');
             // Get key including "="
             String key = env[i].substring(0, pos + 1);
             int size = osEnv.size();
             for (int j = 0; j < size; j++) {
                 if (((String) osEnv.elementAt(j)).startsWith(key)) {
                     osEnv.removeElementAt(j);
                     break;
                 }
             }
             osEnv.addElement(env[i]);
         }
         String[] result = new String[osEnv.size()];
         osEnv.copyInto(result);
         return result;
     }
 
     /**
      * A utility method that runs an external command.  Writes the output and
      * error streams of the command to the project log.
      *
      * @param task      The task that the command is part of.  Used for logging
      * @param cmdline   The command to execute.
      *
      * @throws BuildException if the command does not return 0.
      */
     public static void runCommand(Task task, String[] cmdline)
         throws BuildException {
         try {
             task.log(Commandline.describeCommand(cmdline),
                      Project.MSG_VERBOSE);
             Execute exe = new Execute(new LogStreamHandler(task,
                                                            Project.MSG_INFO,
                                                            Project.MSG_ERR));
             exe.setAntRun(task.getProject());
             exe.setCommandline(cmdline);
             int retval = exe.execute();
             if (isFailure(retval)) {
                 throw new BuildException(cmdline[0]
                     + " failed with return code " + retval, task.getLocation());
             }
         } catch (java.io.IOException exc) {
             throw new BuildException("Could not launch " + cmdline[0] + ": "
                 + exc, task.getLocation());
         }
     }
 
     /**
      * This method is VMS specific and used by getProcEnvironment().
      *
      * Parses VMS logicals from <code>in</code> and adds them to
      * <code>environment</code>.  <code>in</code> is expected to be the
      * output of "SHOW LOGICAL".  The method takes care of parsing the output
      * correctly as well as making sure that a logical defined in multiple
      * tables only gets added from the highest order table.  Logicals with
      * multiple equivalence names are mapped to a variable with multiple
      * values separated by a comma (,).
      */
     private static Vector addVMSLogicals(Vector environment, BufferedReader in)
         throws IOException {
         HashMap logicals = new HashMap();
         String logName = null, logValue = null, newLogName;
-        String line, lineSep = System.getProperty("line.separator");
+        String line = null;
         while ((line = in.readLine()) != null) {
             // parse the VMS logicals into required format ("VAR=VAL[,VAL2]")
             if (line.startsWith("\t=")) {
                 // further equivalence name of previous logical
                 if (logName != null) {
                     logValue += "," + line.substring(4, line.length() - 1);
                 }
             } else if (line.startsWith("  \"")) {
                 // new logical?
                 if (logName != null) {
                     logicals.put(logName, logValue);
                 }
                 int eqIndex = line.indexOf('=');
                 newLogName = line.substring(3, eqIndex - 2);
                 if (logicals.containsKey(newLogName)) {
                     // already got this logical from a higher order table
                     logName = null;
                 } else {
                     logName = newLogName;
                     logValue = line.substring(eqIndex + 3, line.length() - 1);
                 }
             }
         }
         // Since we "look ahead" before adding, there's one last env var.
         if (logName != null) {
             logicals.put(logName, logValue);
         }
 
         for (Iterator i = logicals.keySet().iterator(); i.hasNext();) {
             String logical = (String) i.next();
             environment.add(logical + "=" + logicals.get(logical));
         }
         return environment;
     }
 
     /**
      * A command launcher for a particular JVM/OS platform.  This class is
      * a general purpose command launcher which can only launch commands in
      * the current working directory.
      */
     private static class CommandLauncher {
         /**
          * Launches the given command in a new process.
          *
          * @param project       The project that the command is part of
          * @param cmd           The command to execute
          * @param env           The environment for the new process.  If null,
          *                      the environment of the current proccess is used.
          * @throws IOException  if attempting to run a command in a specific directory
          */
         public Process exec(Project project, String[] cmd, String[] env)
              throws IOException {
             if (project != null) {
                 project.log("Execute:CommandLauncher: "
                     + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
             }
             return Runtime.getRuntime().exec(cmd, env);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          *
          * @param project       The project that the command is part of
          * @param cmd           The command to execute
          * @param env           The environment for the new process.  If null,
          *                      the environment of the current proccess is used.
          * @param workingDir    The directory to start the command in.  If null,
          *                      the current directory is used
          * @throws IOException  if trying to change directory
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (workingDir == null) {
                 return exec(project, cmd, env);
             }
             throw new IOException("Cannot execute a process in different "
                 + "directory under this JVM");
         }
     }
 
     /**
      * A command launcher for JDK/JRE 1.1 under Windows.  Fixes quoting problems
      * in Runtime.exec().  Can only launch commands in the current working
      * directory
      */
     private static class Java11CommandLauncher extends CommandLauncher {
         /**
          * Launches the given command in a new process.  Needs to quote
          * arguments
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @throws IOException probably forwarded from Runtime#exec
          */
         public Process exec(Project project, String[] cmd, String[] env)
              throws IOException {
             // Need to quote arguments with spaces, and to escape
             // quote characters
             String[] newcmd = new String[cmd.length];
             for (int i = 0; i < cmd.length; i++) {
                 newcmd[i] = Commandline.quoteArgument(cmd[i]);
             }
             if (project != null) {
                 project.log("Execute:Java11CommandLauncher: "
                     + Commandline.describeCommand(newcmd), Project.MSG_DEBUG);
             }
             return Runtime.getRuntime().exec(newcmd, env);
         }
     }
 
     /**
      * A command launcher for JDK/JRE 1.3 (and higher).  Uses the built-in
      * Runtime.exec() command
      */
     private static class Java13CommandLauncher extends CommandLauncher {
         public Java13CommandLauncher() throws NoSuchMethodException {
             // Locate method Runtime.exec(String[] cmdarray,
             //                            String[] envp, File dir)
             myExecWithCWD = Runtime.class.getMethod("exec",
                 new Class[] {String[].class, String[].class, File.class});
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @param workingDir the working directory where the command should run
          * @throws IOException probably forwarded from Runtime#exec
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             try {
                 if (project != null) {
                     project.log("Execute:Java13CommandLauncher: "
                         + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
                 }
                 Object[] arguments = {cmd, env, workingDir};
                 return (Process) myExecWithCWD.invoke(Runtime.getRuntime(),
                                                      arguments);
             } catch (InvocationTargetException exc) {
                 Throwable realexc = exc.getTargetException();
                 if (realexc instanceof ThreadDeath) {
                     throw (ThreadDeath) realexc;
                 } else if (realexc instanceof IOException) {
                     throw (IOException) realexc;
                 } else {
                     throw new BuildException("Unable to execute command",
                                              realexc);
                 }
             } catch (Exception exc) {
                 // IllegalAccess, IllegalArgument, ClassCast
                 throw new BuildException("Unable to execute command", exc);
             }
         }
 
         private Method myExecWithCWD;
     }
 
     /**
      * A command launcher that proxies another command launcher.
      *
      * Sub-classes override exec(args, env, workdir)
      */
     private static class CommandLauncherProxy extends CommandLauncher {
         CommandLauncherProxy(CommandLauncher launcher) {
             myLauncher = launcher;
         }
 
         /**
          * Launches the given command in a new process.  Delegates this
          * method to the proxied launcher
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @throws IOException forwarded from the exec method of the command launcher
          */
         public Process exec(Project project, String[] cmd, String[] env)
             throws IOException {
             return myLauncher.exec(project, cmd, env);
         }
 
         private CommandLauncher myLauncher;
     }
 
     /**
      * A command launcher for OS/2 that uses 'cmd.exe' when launching
      * commands in directories other than the current working
      * directory.
      *
      * <p>Unlike Windows NT and friends, OS/2's cd doesn't support the
      * /d switch to change drives and directories in one go.</p>
      */
     private static class OS2CommandLauncher extends CommandLauncherProxy {
         OS2CommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @param workingDir working directory where the command should run
          * @throws IOException forwarded from the exec method of the command launcher
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             File commandDir = workingDir;
             if (workingDir == null) {
                 if (project != null) {
                     commandDir = project.getBaseDir();
                 } else {
                     return exec(project, cmd, env);
                 }
             }
 
             // Use cmd.exe to change to the specified drive and
             // directory before running the command
             final int preCmdLength = 7;
             final String cmdDir = commandDir.getAbsolutePath();
             String[] newcmd = new String[cmd.length + preCmdLength];
             newcmd[0] = "cmd";
             newcmd[1] = "/c";
             newcmd[2] = cmdDir.substring(0, 2);
             newcmd[3] = "&&";
             newcmd[4] = "cd";
             newcmd[5] = cmdDir.substring(2);
             newcmd[6] = "&&";
             System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
 
             return exec(project, newcmd, env);
         }
     }
 
     /**
      * A command launcher for Windows XP/2000/NT that uses 'cmd.exe' when
      * launching commands in directories other than the current working
      * directory.
      */
     private static class WinNTCommandLauncher extends CommandLauncherProxy {
         WinNTCommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @param workingDir working directory where the command should run
          * @throws IOException forwarded from the exec method of the command launcher
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             File commandDir = workingDir;
             if (workingDir == null) {
                 if (project != null) {
                     commandDir = project.getBaseDir();
                 } else {
                     return exec(project, cmd, env);
                 }
             }
 
             // Use cmd.exe to change to the specified directory before running
             // the command
             final int preCmdLength = 6;
             String[] newcmd = new String[cmd.length + preCmdLength];
             newcmd[0] = "cmd";
             newcmd[1] = "/c";
             newcmd[2] = "cd";
             newcmd[3] = "/d";
             newcmd[4] = commandDir.getAbsolutePath();
             newcmd[5] = "&&";
             System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
 
             return exec(project, newcmd, env);
         }
     }
 
     /**
      * A command launcher for Mac that uses a dodgy mechanism to change
      * working directory before launching commands.
      */
     private static class MacCommandLauncher extends CommandLauncherProxy {
         MacCommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory
          * @param project the ant project
          * @param cmd the command line to execute as an array of strings
          * @param env the environment to set as an array of strings
          * @param workingDir working directory where the command should run
          * @throws IOException forwarded from the exec method of the command launcher
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (workingDir == null) {
                 return exec(project, cmd, env);
             }
 
             System.getProperties().put("user.dir", workingDir.getAbsolutePath());
             try {
                 return exec(project, cmd, env);
             } finally {
                 System.getProperties().put("user.dir", antWorkingDirectory);
             }
         }
     }
 
     /**
      * A command launcher that uses an auxiliary script to launch commands
      * in directories other than the current working directory.
      */
     private static class ScriptCommandLauncher extends CommandLauncherProxy {
         ScriptCommandLauncher(String script, CommandLauncher launcher) {
             super(launcher);
             myScript = script;
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (project == null) {
                 if (workingDir == null) {
                     return exec(project, cmd, env);
                 }
                 throw new IOException("Cannot locate antRun script: "
                     + "No project provided");
             }
 
             // Locate the auxiliary script
             String antHome = project.getProperty("ant.home");
             if (antHome == null) {
                 throw new IOException("Cannot locate antRun script: "
                     + "Property 'ant.home' not found");
             }
             String antRun = project.resolveFile(antHome + File.separator + myScript).toString();
 
             // Build the command
             File commandDir = workingDir;
             if (workingDir == null && project != null) {
                 commandDir = project.getBaseDir();
             }
 
             String[] newcmd = new String[cmd.length + 2];
             newcmd[0] = antRun;
             newcmd[1] = commandDir.getAbsolutePath();
             System.arraycopy(cmd, 0, newcmd, 2, cmd.length);
 
             return exec(project, newcmd, env);
         }
 
         private String myScript;
     }
 
     /**
      * A command launcher that uses an auxiliary perl script to launch commands
      * in directories other than the current working directory.
      */
     private static class PerlScriptCommandLauncher
         extends CommandLauncherProxy {
         PerlScriptCommandLauncher(String script, CommandLauncher launcher) {
             super(launcher);
             myScript = script;
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (project == null) {
                 if (workingDir == null) {
                     return exec(project, cmd, env);
                 }
                 throw new IOException("Cannot locate antRun script: "
                     + "No project provided");
             }
 
             // Locate the auxiliary script
             String antHome = project.getProperty("ant.home");
             if (antHome == null) {
                 throw new IOException("Cannot locate antRun script: "
                     + "Property 'ant.home' not found");
             }
             String antRun = project.resolveFile(antHome + File.separator + myScript).toString();
 
             // Build the command
             File commandDir = workingDir;
             if (workingDir == null && project != null) {
                 commandDir = project.getBaseDir();
             }
 
             String[] newcmd = new String[cmd.length + 3];
             newcmd[0] = "perl";
             newcmd[1] = antRun;
             newcmd[2] = commandDir.getAbsolutePath();
             System.arraycopy(cmd, 0, newcmd, 3, cmd.length);
 
             return exec(project, newcmd, env);
         }
 
         private String myScript;
     }
 
     /**
      * A command launcher for VMS that writes the command to a temporary DCL
      * script before launching commands.  This is due to limitations of both
      * the DCL interpreter and the Java VM implementation.
      */
     private static class VmsCommandLauncher extends Java13CommandLauncher {
 
         public VmsCommandLauncher() throws NoSuchMethodException {
             super();
         }
 
         /**
          * Launches the given command in a new process.
          */
         public Process exec(Project project, String[] cmd, String[] env)
             throws IOException {
             String[] vmsCmd = {createCommandFile(cmd).getPath()};
             return super.exec(project, vmsCmd, env);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.  Note that under Java 1.3.1, 1.4.0 and 1.4.1 on VMS this
          * method only works if <code>workingDir</code> is null or the logical
          * JAVA$FORK_SUPPORT_CHDIR needs to be set to TRUE.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             String[] vmsCmd = {createCommandFile(cmd).getPath()};
             return super.exec(project, vmsCmd, env, workingDir);
         }
 
         /*
          * Writes the command into a temporary DCL script and returns the
          * corresponding File object.  The script will be deleted on exit.
          */
         private File createCommandFile(String[] cmd) throws IOException {
             File script = File.createTempFile("ANT", ".COM");
             script.deleteOnExit();
             PrintWriter out = null;
             try {
                 out = new PrintWriter(new FileWriter(script));
                 StringBuffer dclCmd = new StringBuffer("$");
                 for (int i = 0; i < cmd.length; i++) {
                     dclCmd.append(' ').append(cmd[i]);
                 }
                 out.println(dclCmd.toString());
             } finally {
                 if (out != null) {
                     out.close();
                 }
             }
             return script;
         }
 
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/FixCRLF.java b/src/main/org/apache/tools/ant/taskdefs/FixCRLF.java
index 5563ee5fa..fee191b19 100644
--- a/src/main/org/apache/tools/ant/taskdefs/FixCRLF.java
+++ b/src/main/org/apache/tools/ant/taskdefs/FixCRLF.java
@@ -1,1030 +1,1029 @@
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
  * 4. The names "Ant" and "Apache Software
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
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.Reader;
 import java.io.Writer;
 import java.util.Enumeration;
 import java.util.NoSuchElementException;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Converts text source files to local OS formatting conventions, as
  * well as repair text files damaged by misconfigured or misguided editors or
  * file transfer programs.
  * <p>
  * This task can take the following arguments:
  * <ul>
  * <li>srcdir
  * <li>destdir
  * <li>include
  * <li>exclude
  * <li>cr
  * <li>eol
  * <li>tab
  * <li>eof
  * <li>encoding
  * </ul>
  * Of these arguments, only <b>sourcedir</b> is required.
  * <p>
  * When this task executes, it will scan the srcdir based on the include
  * and exclude properties.
  * <p>
  * This version generalises the handling of EOL characters, and allows
  * for CR-only line endings (which I suspect is the standard on Macs.)
  * Tab handling has also been generalised to accommodate any tabwidth
  * from 2 to 80, inclusive.  Importantly, it will leave untouched any
  * literal TAB characters embedded within string or character constants.
  * <p>
  * <em>Warning:</em> do not run on binary files.
  * <em>Caution:</em> run with care on carefully formatted files.
  * This may sound obvious, but if you don't specify asis, presume that
  * your files are going to be modified.  If "tabs" is "add" or "remove",
  * whitespace characters may be added or removed as necessary.  Similarly,
  * for CR's - in fact "eol"="crlf" or cr="add" can result in cr
  * characters being removed in one special case accommodated, i.e.,
  * CRCRLF is regarded as a single EOL to handle cases where other
  * programs have converted CRLF into CRCRLF.
  *
  * @author Sam Ruby <a href="mailto:rubys@us.ibm.com">rubys@us.ibm.com</a>
  * @author <a href="mailto:pbwest@powerup.com.au">Peter B. West</a>
  * @version $Revision$ $Name$
  * @since Ant 1.1
  *
  * @ant.task category="filesystem"
  */
 
 public class FixCRLF extends MatchingTask {
 
     private static final int UNDEF = -1;
     private static final int NOTJAVA = 0;
     private static final int LOOKING = 1;
     private static final int IN_CHAR_CONST = 2;
     private static final int IN_STR_CONST = 3;
     private static final int IN_SINGLE_COMMENT = 4;
     private static final int IN_MULTI_COMMENT = 5;
 
     private static final int ASIS = 0;
     private static final int CR = 1;
     private static final int LF = 2;
     private static final int CRLF = 3;
     private static final int ADD = 1;
     private static final int REMOVE = -1;
     private static final int SPACES = -1;
     private static final int TABS = 1;
 
     private static final int INBUFLEN = 8192;
     private static final int LINEBUFLEN = 200;
 
     private static final char CTRLZ = '\u001A';
 
     private int tablength = 8;
     private String spaces = "        ";
     private StringBuffer linebuf = new StringBuffer(1024);
     private StringBuffer linebuf2 = new StringBuffer(1024);
     private int eol;
     private String eolstr;
     private int ctrlz;
     private int tabs;
     private boolean javafiles = false;
 
     private File srcDir;
     private File destDir = null;
 
     private FileUtils fileUtils = FileUtils.newFileUtils();
 
     /**
      * Encoding to assume for the files
      */
     private String encoding = null;
 
     /**
      * Defaults the properties based on the system type.
      * <ul><li>Unix: eol="LF" tab="asis" eof="remove"
      *     <li>Mac: eol="CR" tab="asis" eof="remove"
      *     <li>DOS: eol="CRLF" tab="asis" eof="asis"</ul>
      */
     public FixCRLF () {
         tabs = ASIS;
         if (Os.isFamily("mac")) {
             ctrlz = REMOVE;
             eol = CR;
             eolstr = "\r";
         } else if (Os.isFamily("dos")) {
             ctrlz = ASIS;
             eol = CRLF;
             eolstr = "\r\n";
         } else {
             ctrlz = REMOVE;
             eol = LF;
             eolstr = "\n";
         }
     }
 
     /**
      * Set the source dir to find the source text files.
      */
     public void setSrcdir(File srcDir) {
         this.srcDir = srcDir;
     }
 
     /**
      * Set the destination where the fixed files should be placed.
      * Default is to replace the original file.
      */
     public void setDestdir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Set to true if modifying Java source files.
      */
     public void setJavafiles(boolean javafiles) {
         this.javafiles = javafiles;
     }
 
 
     /**
      * Specify how EndOfLine characters are to be handled.
      *
      * @param attr valid values:
      * <ul>
      * <li>asis: leave line endings alone
      * <li>cr: convert line endings to CR
      * <li>lf: convert line endings to LF
      * <li>crlf: convert line endings to CRLF
      * </ul>
      */
     public void setEol(CrLf attr) {
         String option = attr.getValue();
         if (option.equals("asis")) {
             eol = ASIS;
         } else if (option.equals("cr") || option.equals("mac")) {
             eol = CR;
             eolstr = "\r";
         } else if (option.equals("lf") || option.equals("unix")) {
             eol = LF;
             eolstr = "\n";
         } else {
             // Must be "crlf"
             eol = CRLF;
             eolstr = "\r\n";
         }
     }
 
     /**
      * Specify how carriage return (CR) characters are to be handled.
      *
      * @param attr valid values:
      * <ul>
      * <li>add: ensure that there is a CR before every LF
      * <li>asis: leave CR characters alone
      * <li>remove: remove all CR characters
      * </ul>
      *
      * @deprecated use {@link #setEol setEol} instead.
      */
     public void setCr(AddAsisRemove attr) {
         log("DEPRECATED: The cr attribute has been deprecated,",
             Project.MSG_WARN);
         log("Please use the eol attribute instead", Project.MSG_WARN);
         String option = attr.getValue();
         CrLf c = new CrLf();
         if (option.equals("remove")) {
             c.setValue("lf");
         } else if (option.equals("asis")) {
             c.setValue("asis");
         } else {
             // must be "add"
             c.setValue("crlf");
         }
         setEol(c);
     }
 
     /**
      * Specify how tab characters are to be handled.
      *
      * @param attr valid values:
      * <ul>
      * <li>add: convert sequences of spaces which span a tab stop to tabs
      * <li>asis: leave tab and space characters alone
      * <li>remove: convert tabs to spaces
      * </ul>
      */
     public void setTab(AddAsisRemove attr) {
         String option = attr.getValue();
         if (option.equals("remove")) {
             tabs = SPACES;
         } else if (option.equals("asis")) {
             tabs = ASIS;
         } else {
             // must be "add"
             tabs = TABS;
         }
     }
 
     /**
      * Specify tab length in characters.
      *
      * @param tlength specify the length of tab in spaces,
      */
     public void setTablength(int tlength) throws BuildException {
         if (tlength < 2 || tlength > 80) {
             throw new BuildException("tablength must be between 2 and 80",
                                      getLocation());
         }
         tablength = tlength;
         StringBuffer sp = new StringBuffer();
         for (int i = 0; i < tablength; i++) {
             sp.append(' ');
         }
         spaces = sp.toString();
     }
 
     /**
      * Specify how DOS EOF (control-z) characters are to be handled.
      *
      * @param attr valid values:
      * <ul>
      * <li>add: ensure that there is an eof at the end of the file
      * <li>asis: leave eof characters alone
      * <li>remove: remove any eof character found at the end
      * </ul>
      */
     public void setEof(AddAsisRemove attr) {
         String option = attr.getValue();
         if (option.equals("remove")) {
             ctrlz = REMOVE;
         } else if (option.equals("asis")) {
             ctrlz = ASIS;
         } else {
             // must be "add"
             ctrlz = ADD;
         }
     }
 
     /**
      * Specifies the encoding Ant expects the files to be in -
      * defaults to the platforms default encoding.
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Executes the task.
      */
     public void execute() throws BuildException {
         // first off, make sure that we've got a srcdir and destdir
 
         if (srcDir == null) {
             throw new BuildException("srcdir attribute must be set!");
         }
         if (!srcDir.exists()) {
             throw new BuildException("srcdir does not exist!");
         }
         if (!srcDir.isDirectory()) {
             throw new BuildException("srcdir is not a directory!");
         }
         if (destDir != null) {
             if (!destDir.exists()) {
                 throw new BuildException("destdir does not exist!");
             }
             if (!destDir.isDirectory()) {
                 throw new BuildException("destdir is not a directory!");
             }
         }
 
         // log options used
         log("options:"
             + " eol="
             + (eol == ASIS ? "asis" : eol == CR ? "cr" : eol == LF ? "lf" : "crlf")
             + " tab=" + (tabs == TABS ? "add" : tabs == ASIS ? "asis" : "remove")
             + " eof=" + (ctrlz == ADD ? "add" : ctrlz == ASIS ? "asis" : "remove")
             + " tablength=" + tablength
             + " encoding=" + (encoding == null ? "default" : encoding),
             Project.MSG_VERBOSE);
 
         DirectoryScanner ds = super.getDirectoryScanner(srcDir);
         String[] files = ds.getIncludedFiles();
 
         for (int i = 0; i < files.length; i++) {
             processFile(files[i]);
         }
     }
 
     /**
      * Creates a Reader reading from a given file an taking the user
      * defined encoding into account.
      */
     private Reader getReader(File f) throws IOException {
         return (encoding == null) ? new FileReader(f)
             : new InputStreamReader(new FileInputStream(f), encoding);
     }
 
 
     private void processFile(String file) throws BuildException {
         File srcFile = new File(srcDir, file);
         File destD = destDir == null ? srcDir : destDir;
         File tmpFile = null;
         BufferedWriter outWriter;
         OneLiner.BufferLine line;
 
         // read the contents of the file
         OneLiner lines = new OneLiner(srcFile);
 
         try {
             // Set up the output Writer
             try {
                 tmpFile = fileUtils.createTempFile("fixcrlf", "", null);
                 Writer writer = (encoding == null) ? new FileWriter(tmpFile)
                     : new OutputStreamWriter(new FileOutputStream(tmpFile),
                                              encoding);
                 outWriter = new BufferedWriter(writer);
             } catch (IOException e) {
                 throw new BuildException(e);
             }
 
             while (lines.hasMoreElements()) {
                 // In-line states
                 int endComment;
 
                 try {
                     line = (OneLiner.BufferLine) lines.nextElement();
                 } catch (NoSuchElementException e) {
                     throw new BuildException(e);
                 }
 
                 String lineString = line.getLineString();
                 int linelen = line.length();
 
                 // Note - all of the following processing NOT done for
                 // tabs ASIS
 
                 if (tabs == ASIS) {
                     // Just copy the body of the line across
                     try {
                         outWriter.write(lineString);
                     } catch (IOException e) {
                         throw new BuildException(e);
                     } // end of try-catch
 
                 } else { // (tabs != ASIS)
-                    int ptr;
-
-                    while ((ptr = line.getNext()) < linelen) {
+                   
+                    while (line.getNext() < linelen) {
 
                         switch (lines.getState()) {
 
                         case NOTJAVA:
                             notInConstant(line, line.length(), outWriter);
                             break;
 
                         case IN_MULTI_COMMENT:
                             endComment
                                 = lineString.indexOf("*/", line.getNext());
                             if (endComment >= 0) {
                                 // End of multiLineComment on this line
                                 endComment += 2;  // Include the end token
                                 lines.setState(LOOKING);
                             } else {
                                 endComment = linelen;
                             }
 
                             notInConstant(line, endComment, outWriter);
                             break;
 
                         case IN_SINGLE_COMMENT:
                             notInConstant(line, line.length(), outWriter);
                             lines.setState(LOOKING);
                             break;
 
                         case IN_CHAR_CONST:
                         case IN_STR_CONST:
                             // Got here from LOOKING by finding an
                             // opening "\'" next points to that quote
                             // character.
                             // Find the end of the constant.  Watch
                             // out for backslashes.  Literal tabs are
                             // left unchanged, and the column is
                             // adjusted accordingly.
 
                             int begin = line.getNext();
                             char terminator = (lines.getState() == IN_STR_CONST
                                                ? '\"'
                                                : '\'');
                             endOfCharConst(line, terminator);
                             while (line.getNext() < line.getLookahead()) {
                                 if (line.getNextCharInc() == '\t') {
                                     line.setColumn(line.getColumn()
                                         + tablength
                                         - (line.getColumn() % tablength));
                                 } else {
                                     line.incColumn();
                                 }
                             }
 
                             // Now output the substring
                             try {
                                 outWriter.write(line.substring(begin,
                                                                line.getNext()));
                             } catch (IOException e) {
                                 throw new BuildException(e);
                             }
 
                             lines.setState(LOOKING);
 
                             break;
 
 
                         case LOOKING:
                             nextStateChange(line);
                             notInConstant(line, line.getLookahead(), outWriter);
                             break;
 
                         } // end of switch (state)
 
                     } // end of while (line.getNext() < linelen)
 
                 } // end of else (tabs != ASIS)
 
                 try {
                     outWriter.write(eolstr);
                 } catch (IOException e) {
                     throw new BuildException(e);
                 } // end of try-catch
 
             } // end of while (lines.hasNext())
 
             try {
                 // Handle CTRLZ
                 if (ctrlz == ASIS) {
                     outWriter.write(lines.getEofStr());
                 } else if (ctrlz == ADD) {
                     outWriter.write(CTRLZ);
                 }
             } catch (IOException e) {
                 throw new BuildException(e);
             } finally {
                 try {
                     outWriter.close();
                 } catch (IOException e) {
                     throw new BuildException(e);
                 }
             }
 
 
             try {
                 lines.close();
                 lines = null;
             } catch (IOException e) {
                 throw new BuildException("Unable to close source file "
                                          + srcFile);
             }
 
             File destFile = new File(destD, file);
 
             boolean destIsWrong = true;
             if (destFile.exists()) {
                 // Compare the destination with the temp file
                 log("destFile exists", Project.MSG_DEBUG);
                 if (!fileUtils.contentEquals(destFile, tmpFile)) {
                     log(destFile + " is being written", Project.MSG_DEBUG);
                 } else {
                     log(destFile + " is not written, as the contents "
                         + "are identical", Project.MSG_DEBUG);
                     destIsWrong = false;
                 }
             }
 
             if (destIsWrong) {
                 fileUtils.rename(tmpFile, destFile);
                 tmpFile = null;
             }
 
         } catch (IOException e) {
             throw new BuildException(e);
         } finally {
             try {
                 if (lines != null) {
                     lines.close();
                 }
             } catch (IOException io) {
                 log("Error closing " + srcFile, Project.MSG_ERR);
             } // end of catch
 
             if (tmpFile != null) {
                 tmpFile.delete();
             }
         } // end of finally
     }
 
     /**
      * Scan a BufferLine for the next state changing token: the beginning
      * of a single or multi-line comment, a character or a string constant.
      *
      * As a side-effect, sets the buffer state to the next state, and sets
      * field lookahead to the first character of the state-changing token, or
      * to the next eol character.
      *
      * @param bufline       BufferLine containing the string
      *                                 to be processed
      * @exception org.apache.tools.ant.BuildException
      *                                 Thrown when end of line is reached
      *                                 before the terminator is found.
      */
     private void nextStateChange(OneLiner.BufferLine bufline)
         throws BuildException {
         int eol = bufline.length();
         int ptr = bufline.getNext();
 
 
         //  Look for next single or double quote, double slash or slash star
         while (ptr < eol) {
             switch (bufline.getChar(ptr++)) {
             case '\'':
                 bufline.setState(IN_CHAR_CONST);
                 bufline.setLookahead(--ptr);
                 return;
             case '\"':
                 bufline.setState(IN_STR_CONST);
                 bufline.setLookahead(--ptr);
                 return;
             case '/':
                 if (ptr < eol) {
                     if (bufline.getChar(ptr) == '*') {
                         bufline.setState(IN_MULTI_COMMENT);
                         bufline.setLookahead(--ptr);
                         return;
                     } else if (bufline.getChar(ptr) == '/') {
                         bufline.setState(IN_SINGLE_COMMENT);
                         bufline.setLookahead(--ptr);
                         return;
                     }
                 }
                 break;
             } // end of switch (bufline.getChar(ptr++))
 
         } // end of while (ptr < eol)
         // Eol is the next token
         bufline.setLookahead(ptr);
     }
 
 
     /**
      * Scan a BufferLine forward from the 'next' pointer
      * for the end of a character constant.  Set 'lookahead' pointer to the
      * character following the terminating quote.
      *
      * @param bufline       BufferLine containing the string
      *                                 to be processed
      * @param terminator          The constant terminator
      *
      * @exception org.apache.tools.ant.BuildException
      *                                 Thrown when end of line is reached
      *                                 before the terminator is found.
      */
     private void endOfCharConst(OneLiner.BufferLine bufline, char terminator)
         throws BuildException {
         int ptr = bufline.getNext();
         int eol = bufline.length();
         char c;
         ptr++;          // skip past initial quote
         while (ptr < eol) {
             if ((c = bufline.getChar(ptr++)) == '\\') {
                 ptr++;
             } else {
                 if (c == terminator) {
                     bufline.setLookahead(ptr);
                     return;
                 }
             }
         } // end of while (ptr < eol)
         // Must have fallen through to the end of the line
         throw new BuildException("endOfCharConst: unterminated char constant");
     }
 
 
     /**
      * Process a BufferLine string which is not part of of a string constant.
      * The start position of the string is given by the 'next' field.
      * Sets the 'next' and 'column' fields in the BufferLine.
      *
      * @param bufline       BufferLine containing the string
      *                                 to be processed
      * @param end                  Index just past the end of the
      *                                 string
      * @param outWriter Sink for the processed string
      */
     private void notInConstant(OneLiner.BufferLine bufline, int end,
                                 BufferedWriter outWriter) {
         // N.B. both column and string index are zero-based
         // Process a string not part of a constant;
         // i.e. convert tabs<->spaces as required
         // This is NOT called for ASIS tab handling
         int nextTab;
         int nextStop;
         int tabspaces;
         String line = bufline.substring(bufline.getNext(), end);
         int place = 0;          // Zero-based
         int col = bufline.getColumn();  // Zero-based
 
         // process sequences of white space
         // first convert all tabs to spaces
         linebuf = new StringBuffer();
         while ((nextTab = line.indexOf((int) '\t', place)) >= 0) {
             linebuf.append(line.substring(place, nextTab)); // copy to the TAB
             col += nextTab - place;
             tabspaces = tablength - (col % tablength);
             linebuf.append(spaces.substring(0, tabspaces));
             col += tabspaces;
             place = nextTab + 1;
         } // end of while
         linebuf.append(line.substring(place, line.length()));
         // if converting to spaces, all finished
         String linestring = new String(linebuf.substring(0));
         if (tabs == REMOVE) {
             try {
                 outWriter.write(linestring);
             } catch (IOException e) {
                 throw new BuildException(e);
             } // end of try-catch
         } else { // tabs == ADD
             int tabCol;
             linebuf2 = new StringBuffer();
             place = 0;
             col = bufline.getColumn();
             int placediff = col - 0;
             // for the length of the string, cycle through the tab stop
             // positions, checking for a space preceded by at least one
             // other space at the tab stop.  if so replace the longest possible
             // preceding sequence of spaces with a tab.
             nextStop = col + (tablength - col % tablength);
             if (nextStop - col < 2) {
                 linebuf2.append(linestring.substring(
                                         place, nextStop - placediff));
                 place = nextStop - placediff;
                 nextStop += tablength;
             }
 
             for (; nextStop - placediff <= linestring.length();
                     nextStop += tablength) {
                 for (tabCol = nextStop;
                              --tabCol - placediff >= place
                              && linestring.charAt(tabCol - placediff) == ' ';) {
                     ; // Loop for the side-effects
                 }
                 // tabCol is column index of the last non-space character
                 // before the next tab stop
                 if (nextStop - tabCol > 2) {
                     linebuf2.append(linestring.substring(
                                     place, ++tabCol - placediff));
                     linebuf2.append('\t');
                 } else {
                     linebuf2.append(linestring.substring(
                                     place, nextStop - placediff));
                 } // end of else
 
                 place = nextStop - placediff;
             } // end of for (nextStop ... )
 
             // pick up that last bit, if any
             linebuf2.append(linestring.substring(place, linestring.length()));
 
             try {
                 outWriter.write(linebuf2.substring(0));
             } catch (IOException e) {
                 throw new BuildException(e);
             } // end of try-catch
 
         } // end of else tabs == ADD
 
         // Set column position as modified by this method
         bufline.setColumn(bufline.getColumn() + linestring.length());
         bufline.setNext(end);
 
     }
 
 
     class OneLiner implements Enumeration {
 
         private int state = javafiles ? LOOKING : NOTJAVA;
 
         private StringBuffer eolStr = new StringBuffer(LINEBUFLEN);
         private StringBuffer eofStr = new StringBuffer();
 
         private BufferedReader reader;
         private StringBuffer line = new StringBuffer();
         private boolean reachedEof = false;
         private File srcFile;
 
         public OneLiner(File srcFile)
             throws BuildException {
             this.srcFile = srcFile;
             try {
                 reader = new BufferedReader
                         (getReader(srcFile), INBUFLEN);
                 nextLine();
             } catch (IOException e) {
                 throw new BuildException(srcFile + ": " + e.getMessage(),
                                          e, getLocation());
             }
         }
 
         protected void nextLine()
             throws BuildException {
             int ch = -1;
             int eolcount = 0;
 
             eolStr = new StringBuffer();
             line = new StringBuffer();
 
             try {
                 ch = reader.read();
                 while (ch != -1 && ch != '\r' && ch != '\n') {
                     line.append((char) ch);
                     ch = reader.read();
                 }
 
                 if (ch == -1 && line.length() == 0) {
                     // Eof has been reached
                     reachedEof = true;
                     return;
                 }
 
                 switch ((char) ch) {
                 case '\r':
                     // Check for \r, \r\n and \r\r\n
                     // Regard \r\r not followed by \n as two lines
                     ++eolcount;
                     eolStr.append('\r');
                     reader.mark(2);
                     switch ((ch = reader.read())) {
                     case '\r':
                         if ((char) (ch = reader.read()) == '\n') {
                             eolcount += 2;
                             eolStr.append("\r\n");
                         } else {
                             reader.reset();
                         }
                         break;
                     case '\n':
                         ++eolcount;
                         eolStr.append('\n');
                         break;
                     case -1:
                         // don't reposition when we've reached the end
                         // of the stream
                         break;
                     default:
                         reader.reset();
                         break;
                     } // end of switch ((char)(ch = reader.read()))
                     break;
 
                 case '\n':
                     ++eolcount;
                     eolStr.append('\n');
                     break;
 
                 } // end of switch ((char) ch)
 
                 // if at eolcount == 0 and trailing characters of string
                 // are CTRL-Zs, set eofStr
                 if (eolcount == 0) {
                     int i = line.length();
                     while (--i >= 0 && line.charAt(i) == CTRLZ) {
                         // keep searching for the first ^Z
                     }
                     if (i < line.length() - 1) {
                         // Trailing characters are ^Zs
                         // Construct new line and eofStr
                         eofStr.append(line.toString().substring(i + 1));
                         if (i < 0) {
                             line.setLength(0);
                             reachedEof = true;
                         } else {
                             line.setLength(i + 1);
                         }
                     }
 
                 } // end of if (eolcount == 0)
 
             } catch (IOException e) {
                 throw new BuildException(srcFile + ": " + e.getMessage(),
                                          e, getLocation());
             }
         }
 
         public String getEofStr() {
             return eofStr.substring(0);
         }
 
         public int getState() {
             return state;
         }
 
         public void setState(int state) {
             this.state = state;
         }
 
         public boolean hasMoreElements() {
             return !reachedEof;
         }
 
         public Object nextElement()
             throws NoSuchElementException {
             if (!hasMoreElements()) {
                 throw new NoSuchElementException("OneLiner");
             }
             BufferLine tmpLine =
                     new BufferLine(line.toString(), eolStr.substring(0));
             nextLine();
             return tmpLine;
         }
 
         public void close() throws IOException {
             if (reader != null) {
                 reader.close();
             }
         }
 
         class BufferLine {
             private int next = 0;
             private int column = 0;
             private int lookahead = UNDEF;
             private String line;
             private String eolStr;
 
             public BufferLine(String line, String eolStr)
                 throws BuildException {
                 next = 0;
                 column = 0;
                 this.line = line;
                 this.eolStr = eolStr;
             }
 
             public int getNext() {
                 return next;
             }
 
             public void setNext(int next) {
                 this.next = next;
             }
 
             public int getLookahead() {
                 return lookahead;
             }
 
             public void setLookahead(int lookahead) {
                 this.lookahead = lookahead;
             }
 
             public char getChar(int i) {
                 return line.charAt(i);
             }
 
             public char getNextChar() {
                 return getChar(next);
             }
 
             public char getNextCharInc() {
                 return getChar(next++);
             }
 
             public int getColumn() {
                 return column;
             }
 
             public void setColumn(int col) {
                 column = col;
             }
 
             public int incColumn() {
                 return column++;
             }
 
             public int length() {
                 return line.length();
             }
 
             public int getEolLength() {
                 return eolStr.length();
             }
 
             public String getLineString() {
                 return line;
             }
 
             public String getEol() {
                 return eolStr;
             }
 
             public String substring(int begin) {
                 return line.substring(begin);
             }
 
             public String substring(int begin, int end) {
                 return line.substring(begin, end);
             }
 
             public void setState(int state) {
                 OneLiner.this.setState(state);
             }
 
             public int getState() {
                 return OneLiner.this.getState();
             }
         }
     }
 
     /**
      * Enumerated attribute with the values "asis", "add" and "remove".
      */
     public static class AddAsisRemove extends EnumeratedAttribute {
         public String[] getValues() {
             return new String[] {"add", "asis", "remove"};
         }
     }
 
     /**
      * Enumerated attribute with the values "asis", "cr", "lf" and "crlf".
      */
     public static class CrLf extends EnumeratedAttribute {
         /**
          * @see EnumeratedAttribute#getValues
          */
         public String[] getValues() {
             return new String[] {"asis", "cr", "lf", "crlf",
                                  "mac", "unix", "dos"};
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/MacroDef.java b/src/main/org/apache/tools/ant/taskdefs/MacroDef.java
index 0197beb8d..cd3dd6ba4 100644
--- a/src/main/org/apache/tools/ant/taskdefs/MacroDef.java
+++ b/src/main/org/apache/tools/ant/taskdefs/MacroDef.java
@@ -1,530 +1,529 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.HashMap;
 
 import org.apache.tools.ant.AntTypeDefinition;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ComponentHelper;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.TaskContainer;
 import org.apache.tools.ant.UnknownElement;
 
 import org.apache.tools.ant.types.EnumeratedAttribute;
 
 /**
  * Describe class <code>MacroDef</code> here.
  *
  * @author Peter Reilly
  * @since Ant 1.6
  */
 public class MacroDef extends Task implements AntlibInterface, TaskContainer {
     private UnknownElement nestedTask;
     private String     name;
-    private String     componentName;
     private List       attributes = new ArrayList();
     private Map        elements = new HashMap();
     private String     uri;
     private int        attributeStyle = AttributeStyle.ANT;
 
     /**
      * Name of the definition
      * @param name the name of the definition
      */
      public void setName(String name) {
         this.name = name;
     }
 
     /**
      * The URI for this definition.
      * @param uri the namespace URI
      * @throws BuildException if uri is not allowed
      */
     public void setURI(String uri) throws BuildException {
         if (uri.equals(ProjectHelper.ANT_CORE_URI)) {
             uri = "";
         }
         if (uri.startsWith("ant:")) {
             throw new BuildException("Attempt to use a reserved URI " + uri);
         }
         this.uri = uri;
     }
 
     /**
      * Enumerated type for attributeStyle attribute
      *
      * @see EnumeratedAttribute
      */
     public static class AttributeStyle extends EnumeratedAttribute {
         /** Enumerated values */
         public static final int ANT = 0, XPATH = 1;
 
         /**
          * get the values
          * @return an array of the allowed values for this attribute.
          */
         public String[] getValues() {
             return new String[] {"ant", "xpath"};
         }
     }
 
     /**
      * <em>Expermential</em>
      * I am uncertain at the moment how to encode attributes
      * using ant style ${attribute} or xpath style @attribute.
      * The first may get mixed up with ant properties and
      * the second may get mixed up with xpath.
      * The default at the moment is ant s
      *
      * @param style an <code>AttributeStyle</code> value
      */
     public void setAttributeStyle(AttributeStyle style) {
         attributeStyle = style.getIndex();
     }
 
     /**
      * <em>Expermential</em>
      * @return the attribute style
      */
     public int getAttributeStyle() {
         return attributeStyle;
     }
 
     /**
      * Set the class loader.
      * Not used
      * @param classLoader a <code>ClassLoader</code> value
      */
     public void setAntlibClassLoader(ClassLoader classLoader) {
         // Ignore
     }
 
     /**
      * Add a nested task to ExtendType
      * @param nestedTask  Nested task/type to extend
      */
     public void addTask(Task nestedTask) {
         if (this.nestedTask != null) {
             throw new BuildException("Only one sequential/Parallel allowed");
         }
         UnknownElement ue = (UnknownElement) nestedTask;
         if (!ue.getNamespace().equals("")
             || (!ue.getTag().equals("sequential")
                 && !ue.getTag().equals("parallel"))) {
             throw new BuildException("Unsupported tag " + ue.getQName());
         }
         this.nestedTask = ue;
     }
 
     /**
      * @return the nested task
      */
     public UnknownElement getNestedTask() {
         return nestedTask;
     }
 
     /**
      * @return the nested Attributes
      */
     public List getAttributes() {
         return attributes;
     }
 
     /**
      * @return the nested elements
      */
     public Map getElements() {
         return elements;
     }
 
     /**
      * Check if a character is a valid character for an element or
      * attribute name
      * @param c the character to check
      * @return true if the character is a letter or digit or '.' or '-'
      *         attribute name
      */
     public static boolean isValidNameCharacter(char c) {
         // ? is there an xml api for this ?
         return Character.isLetterOrDigit(c) || c == '.' || c == '-';
     }
 
     /**
      * Check if a string is a valid name for an element or
      * attribute
      * @param name the string to check
      * @return true if the name consists of valid name characters
      */
     private static boolean isValidName(String name) {
         if (name.length() == 0) {
             return false;
         }
         for (int i = 0; i < name.length(); ++i) {
             if (!isValidNameCharacter(name.charAt(i))) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Add an attribute element.
      *
      * @param attribute an attribute nested element.
      */
     public void addConfiguredAttribute(Attribute attribute) {
         if (attribute.getName() == null) {
             throw new BuildException(
                 "the attribute nested element needed a \"name\" attribute");
         }
         attributes.add(attribute);
     }
 
     /**
      * Add an element element.
      *
      * @param element an element nested element.
      */
     public void addConfiguredElement(TemplateElement element) {
         if (element.getName() == null) {
             throw new BuildException(
                 "the element nested element needed a \"name\" attribute");
         }
         elements.put(element.getName(), element);
     }
 
     /**
      * Create a new ant type based on the embedded tasks and types.
      *
      */
     public void execute() {
         if (nestedTask == null) {
             throw new BuildException("Missing nested element");
         }
         if (name == null) {
             throw new BuildException("Name not specified");
         }
 
         name = ProjectHelper.genComponentName(uri, name);
 
         MyAntTypeDefinition def = new MyAntTypeDefinition(this);
         def.setName(name);
         def.setClass(MacroInstance.class);
 
         ComponentHelper helper = ComponentHelper.getComponentHelper(
             getProject());
 
         helper.addDataTypeDefinition(def);
     }
 
 
     /**
      * A nested element for the MacroDef task.
      *
      */
     public static class Attribute {
         private String name;
         private String defaultValue;
 
         /**
          * The name of the attribute.
          *
          * @param name the name of the attribute
          */
         public void setName(String name) {
             if (!isValidName(name)) {
                 throw new BuildException(
                     "Illegal name [" + name + "] for attribute");
             }
             this.name = name;
         }
 
         /**
          * @return the name of the attribute
          */
         public String getName() {
             return name;
         }
 
         /**
          * The default value to use if the parameter is not
          * used in the templated instance.
          *
          * @param defaultValue the default value
          */
         public void setDefault(String defaultValue) {
             this.defaultValue = defaultValue;
         }
 
         /**
          * @return the default value, null if not set
          */
         public String getDefault() {
             return defaultValue;
         }
 
         /**
          * equality method
          *
          * @param obj an <code>Object</code> value
          * @return a <code>boolean</code> value
          */
         public boolean equals(Object obj) {
             if (obj == null) {
                 return false;
             }
             if (obj.getClass() != getClass()) {
                 return false;
             }
             Attribute other = (Attribute) obj;
             if (name == null) {
                 return other.name == null;
             }
             if (!name.equals(other.name)) {
                 return false;
             }
             if (defaultValue == null) {
                 return other.defaultValue == null;
             }
             if (!name.equals(other.defaultValue)) {
                 return false;
             }
             return true;
         }
     }
 
     /**
      * A nested element for the MacroDef task.
      *
      */
     public static class TemplateElement {
         private String name;
         private boolean optional = false;
         /**
          * The name of the element.
          *
          * @param name the name of the element.
          */
         public void setName(String name) {
             if (!isValidName(name)) {
                 throw new BuildException(
                     "Illegal name [" + name + "] for attribute");
             }
             this.name = name;
         }
 
         /**
          * @return the name of the element.
          */
         public String getName() {
             return name;
         }
 
         /**
          * is this element optional ?
          *
          * @param optional if true this element may be left out, default
          *                 is false.
          */
         public void setOptional(boolean optional) {
             this.optional = optional;
         }
 
         /**
          * @return the optional attribute
          */
         public boolean isOptional() {
             return optional;
         }
 
         /**
          * equality method
          *
          * @param obj an <code>Object</code> value
          * @return a <code>boolean</code> value
          */
         public boolean equals(Object obj) {
             if (obj == null) {
                 return false;
             }
             if (obj.getClass() != getClass()) {
                 return false;
             }
             TemplateElement other = (TemplateElement) obj;
             if (name == null) {
                 return other.name == null;
             }
             if (!name.equals(other.name)) {
                 return false;
             }
             return optional == other.optional;
         }
     }
 
     /**
      * equality method for macrodef, ignores project and
      * runtime info.
      *
      * @param obj an <code>Object</code> value
      * @return a <code>boolean</code> value
      */
     public boolean equals(Object obj) {
         if (obj == null) {
             return false;
         }
         if (!obj.getClass().equals(getClass())) {
             return false;
         }
         MacroDef other = (MacroDef) obj;
         if (name == null) {
             return other.name == null;
         }
         if (!name.equals(other.name)) {
             return false;
         }
         if (uri == null || uri.equals("")
             || uri.equals(ProjectHelper.ANT_CORE_URI)) {
             return other.uri == null || other.uri.equals("")
                 || other.uri.equals(ProjectHelper.ANT_CORE_URI);
         }
         if (!uri.equals(other.uri)) {
             return false;
         }
 
         if (attributeStyle != other.attributeStyle) {
             return false;
         }
         if (!nestedTask.similar(other.nestedTask)) {
             return false;
         }
         if (!attributes.equals(other.attributes)) {
             return false;
         }
         if (!elements.equals(other.elements)) {
             return false;
         }
         return true;
     }
 
     /**
      * extends AntTypeDefinition, on create
      * of the object, the template macro definition
      * is given.
      */
     private static class MyAntTypeDefinition extends AntTypeDefinition {
         private MacroDef    macroDef;
 
         /**
          * Creates a new <code>MyAntTypeDefinition</code> instance.
          *
          * @param macroDef a <code>MacroDef</code> value
          */
         public MyAntTypeDefinition(MacroDef macroDef) {
             this.macroDef = macroDef;
         }
 
         /**
          * create an instance of the definition.
          * The instance may be wrapped in a proxy class.
          * @param project the current project
          * @return the created object
          */
         public Object create(Project project) {
             Object o = super.create(project);
             if (o == null) {
                 return null;
             }
             ((MacroInstance) o).setMacroDef(macroDef);
             return o;
         }
 
         /**
          * Equality method for this definition
          *
          * @param other another definition
          * @param project the current project
          * @return true if the definitions are the same
          */
         public boolean sameDefinition(AntTypeDefinition other, Project project) {
             if (!super.sameDefinition(other, project)) {
                 return false;
             }
             MyAntTypeDefinition otherDef = (MyAntTypeDefinition) other;
             return macroDef.equals(otherDef.macroDef);
         }
 
         /**
          * Similiar method for this definition
          *
          * @param other another definition
          * @param project the current project
          * @return true if the definitions are the same
          */
         public boolean similarDefinition(
             AntTypeDefinition other, Project project) {
             if (!super.similarDefinition(other, project)) {
                 return false;
             }
             MyAntTypeDefinition otherDef = (MyAntTypeDefinition) other;
             return macroDef.equals(otherDef.macroDef);
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/PreSetDef.java b/src/main/org/apache/tools/ant/taskdefs/PreSetDef.java
index 99c2b40f0..79e6a2a36 100644
--- a/src/main/org/apache/tools/ant/taskdefs/PreSetDef.java
+++ b/src/main/org/apache/tools/ant/taskdefs/PreSetDef.java
@@ -1,316 +1,315 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 import org.apache.tools.ant.AntTypeDefinition;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ComponentHelper;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.TaskContainer;
 import org.apache.tools.ant.UnknownElement;
 
 /**
  * The preset definition task generates a new definition
  * based on a current definition with some attributes or
  * elements preset.
  * <pre>
  * &lt;presetdef name="my.javac"&gt;
  *   &lt;javac deprecation="${deprecation}" debug="${debug}"/&gt;
  * &lt;/presetdef&gt;
  * &lt;my.javac srcdir="src" destdir="classes"/&gt;
  * </pre>
  *
  * @author Peter Reilly
  * @since Ant 1.6
  */
 public class PreSetDef extends Task implements AntlibInterface, TaskContainer {
     private UnknownElement nestedTask;
     private String         name;
-    private String         componentName;
     private String         uri;
 
     /**
      * Name of the definition
      * @param name the name of the definition
      */
      public void setName(String name) {
         this.name = name;
     }
     /**
      * The URI for this definition.
      * @param uri the namespace URI
      * @throws BuildException if uri is not allowed
      */
     public void setURI(String uri) throws BuildException {
         if (uri.equals(ProjectHelper.ANT_CORE_URI)) {
             uri = "";
         }
         if (uri.startsWith("ant:")) {
             throw new BuildException("Attempt to use a reserved URI " + uri);
         }
         this.uri = uri;
     }
     /**
      * Set the class loader.
      * Not used
      * @param classLoader a <code>ClassLoader</code> value
      */
     public void setAntlibClassLoader(ClassLoader classLoader) {
         // Ignore
     }
 
 
     /**
      * Add a nested task to predefine attributes and elements on
      * @param nestedTask  Nested task/type to extend
      */
     public void addTask(Task nestedTask) {
         if (this.nestedTask != null) {
             throw new BuildException("Only one nested element allowed");
         }
         if (!(nestedTask instanceof UnknownElement)) {
             throw new BuildException(
                 "addTask called with a task that is not an unknown element");
         }
         this.nestedTask = (UnknownElement) nestedTask;
     }
 
 
     /**
      * make a new definition
      */
     public void execute() {
         if (nestedTask == null) {
             throw new BuildException("Missing nested element");
         }
         if (name == null) {
             throw new BuildException("Name not specified");
         }
 
         name = ProjectHelper.genComponentName(uri, name);
 
         ComponentHelper helper = ComponentHelper.getComponentHelper(
             getProject());
 
         String componentName = ProjectHelper.genComponentName(
             nestedTask.getNamespace(), nestedTask.getTag());
 
         AntTypeDefinition def = helper.getDefinition(componentName);
         if (def == null) {
             throw new BuildException(
                 "Unable to find typedef " + componentName);
         }
 
         MyAntTypeDefinition newDef = new MyAntTypeDefinition(def, nestedTask);
 
         newDef.setName(name);
 
         helper.addDataTypeDefinition(newDef);
     }
 
     private static class MyAntTypeDefinition extends AntTypeDefinition {
         private AntTypeDefinition parent;
         private UnknownElement    element;
 
         public MyAntTypeDefinition(AntTypeDefinition parent, UnknownElement el) {
             this.parent = parent;
             this.element = el;
         }
 
         public void setClass(Class clazz) {
             throw new BuildException("Not supported");
         }
 
         public void setClassName(String className) {
             throw new BuildException("Not supported");
         }
 
         /**
          * get the classname of the definition
          * @return the name of the class of this definition
          */
         public String getClassName() {
             return parent.getClassName();
         }
 
         /**
          * set the adapter class for this definition.
          * NOTE Supported
          * @param adapterClass the adapterClass
          */
         public void setAdapterClass(Class adapterClass) {
             throw new BuildException("Not supported");
         }
 
         /**
          * set the assignable class for this definition.
          * NOT SUPPORTED
          * @param adaptToClass the assignable class
          */
 
         public void setAdaptToClass(Class adaptToClass) {
             throw new BuildException("Not supported");
         }
 
         /**
          * set the classloader to use to create an instance
          * of the definition
          * @param classLoader the classLoader
          */
         public void setClassLoader(ClassLoader classLoader) {
             throw new BuildException("Not supported");
         }
 
         /**
          * get the classloader for this definition
          * @return the classloader for this definition
          */
         public ClassLoader getClassLoader() {
             return parent.getClassLoader();
         }
 
         /**
          * get the exposed class for this definition.
          * @return the exposed class
          */
         public Class getExposedClass(Project project) {
             return parent.getExposedClass(project);
         }
 
         /**
          * get the definition class
          * @param project the current project
          * @return the type of the definition
          */
         public Class getTypeClass(Project project) {
             return parent.getTypeClass(project);
         }
 
 
         /**
          * check if the attributes are correct
          * @param project the current project
          */
         public void checkClass(Project project) {
             parent.checkClass(project);
         }
 
         /**
          * create an instance of the definition.
          * The instance may be wrapped in a proxy class.
          * @param project the current project
          * @return the created object
          */
         public Object create(Project project) {
             Object o = parent.create(project);
             if (o == null) {
                 return null;
             }
             element.configure(o);
             return o;
         }
 
         /**
          * Equality method for this definition
          *
          * @param other another definition
          * @param project the current project
          * @return true if the definitions are the same
          */
         public boolean sameDefinition(AntTypeDefinition other, Project project) {
             if (other == null) {
                 return false;
             }
             if (other.getClass() != getClass()) {
                 return false;
             }
             MyAntTypeDefinition otherDef = (MyAntTypeDefinition) other;
             if (!parent.sameDefinition(otherDef.parent, project)) {
                 return false;
             }
             if (!element.similar(otherDef.element)) {
                 return false;
             }
             return true;
         }
 
         /**
          * Similiar method for this definition
          *
          * @param other another definition
          * @param project the current project
          * @return true if the definitions are the same
          */
         public boolean similarDefinition(
             AntTypeDefinition other, Project project) {
             if (other == null) {
                 return false;
             }
             if (!other.getClass().getName().equals(getClass().getName())) {
                 return false;
             }
             MyAntTypeDefinition otherDef = (MyAntTypeDefinition) other;
             if (!parent.similarDefinition(otherDef.parent, project)) {
                 return false;
             }
             if (!element.similar(otherDef.element)) {
                 return false;
             }
             return true;
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index d6799e244..8b502958b 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,687 +1,687 @@
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
  * 4. The names "Ant" and "Apache Software
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
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.BufferedReader;
 import java.io.StringReader;
 import java.io.FileReader;
 import java.io.InputStreamReader;
 import java.io.FileInputStream;
 import java.util.Enumeration;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 
 /**
  * Executes a series of SQL statements on a database using JDBC.
  *
  * <p>Statements can
  * either be read in from a text file using the <i>src</i> attribute or from
  * between the enclosing SQL tags.</p>
  *
  * <p>Multiple statements can be provided, separated by semicolons (or the
  * defined <i>delimiter</i>). Individual lines within the statements can be
  * commented using either --, // or REM at the start of the line.</p>
  *
  * <p>The <i>autocommit</i> attribute specifies whether auto-commit should be
  * turned on or off whilst executing the statements. If auto-commit is turned
  * on each statement will be executed and committed. If it is turned off the
  * statements will all be executed as one transaction.</p>
  *
  * <p>The <i>onerror</i> attribute specifies how to proceed when an error occurs
  * during the execution of one of the statements.
  * The possible values are: <b>continue</b> execution, only show the error;
  * <b>stop</b> execution and commit transaction;
  * and <b>abort</b> execution and transaction and fail task.</p>
 
  *
  * @author <a href="mailto:jeff@custommonkey.org">Jeff Martin</a>
  * @author <A href="mailto:gholam@xtra.co.nz">Michael McCallum</A>
  * @author <A href="mailto:tim.stephenson@sybase.com">Tim Stephenson</A>
  *
  * @since Ant 1.2
  *
  * @ant.task name="sql" category="database"
  */
 public class SQLExec extends JDBCTask {
 
     /**
      * delimiters we support, "normal" and "row"
      */
     public static class DelimiterType extends EnumeratedAttribute {
         public static final String NORMAL = "normal";
         public static final String ROW = "row";
         public String[] getValues() {
             return new String[] {NORMAL, ROW};
         }
     }
 
 
 
     private int goodSql = 0;
 
     private int totalSql = 0;
 
     /**
      * Database connection
      */
     private Connection conn = null;
 
     /**
      * files to load
      */
     private Vector filesets = new Vector();
 
     /**
      * SQL statement
      */
     private Statement statement = null;
 
     /**
      * SQL input file
      */
     private File srcFile = null;
 
     /**
      * SQL input command
      */
     private String sqlCommand = "";
 
     /**
      * SQL transactions to perform
      */
     private Vector transactions = new Vector();
 
     /**
      * SQL Statement delimiter
      */
     private String delimiter = ";";
 
     /**
      * The delimiter type indicating whether the delimiter will
      * only be recognized on a line by itself
      */
     private String delimiterType = DelimiterType.NORMAL;
 
     /**
      * Print SQL results.
      */
     private boolean print = false;
 
     /**
      * Print header columns.
      */
     private boolean showheaders = true;
 
     /**
      * Results Output file.
      */
     private File output = null;
 
 
     /**
      * Action to perform if an error is found
      **/
     private String onError = "abort";
 
     /**
      * Encoding to use when reading SQL statements from a file
      */
     private String encoding = null;
 
     /**
      * Append to an existing file or overwrite it?
      */
     private boolean append = false;
 
     /**
      * Keep the format of a sql block?
      */
     private boolean keepformat = false;
 
     /**
      * Argument to Statement.setEscapeProcessing
      *
      * @since Ant 1.6
      */
     private boolean escapeProcessing = true;
 
     /**
      * Set the name of the SQL file to be run.
      * Required unless statements are enclosed in the build file
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text.
      */
     public void addText(String sql) {
         this.sqlCommand += sql;
     }
 
     /**
      * Adds a set of files (nested fileset attribute).
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
 
     /**
      * Add a SQL transaction to execute
      */
     public Transaction createTransaction() {
         Transaction t = new Transaction();
         transactions.addElement(t);
         return t;
     }
 
     /**
      * Set the file encoding to use on the SQL files read in
      *
      * @param encoding the encoding to use on the files
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Set the delimiter that separates SQL statements. Defaults to &quot;;&quot;;
      * optional
      *
      * <p>For example, set this to "go" and delimitertype to "ROW" for
      * Sybase ASE or MS SQL Server.</p>
      */
     public void setDelimiter(String delimiter) {
         this.delimiter = delimiter;
     }
 
     /**
      * Set the delimiter type: "normal" or "row" (default "normal").
      *
      * <p>The delimiter type takes two values - normal and row. Normal
      * means that any occurence of the delimiter terminate the SQL
      * command whereas with row, only a line containing just the
      * delimiter is recognized as the end of the command.</p>
      */
     public void setDelimiterType(DelimiterType delimiterType) {
         this.delimiterType = delimiterType.getValue();
     }
 
     /**
      * Print result sets from the statements;
      * optional, default false
      */
     public void setPrint(boolean print) {
         this.print = print;
     }
 
     /**
      * Print headers for result sets from the
      * statements; optional, default true.
      */
     public void setShowheaders(boolean showheaders) {
         this.showheaders = showheaders;
     }
 
     /**
      * Set the output file;
      * optional, defaults to the Ant log.
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * whether output should be appended to or overwrite
      * an existing file.  Defaults to false.
      *
      * @since Ant 1.5
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
 
     /**
      * Action to perform when statement fails: continue, stop, or abort
      * optional; default &quot;abort&quot;
      */
     public void setOnerror(OnError action) {
         this.onError = action.getValue();
     }
 
     /**
      * whether or not format should be preserved.
      * Defaults to false.
      *
      * @param keepformat The keepformat to set
      */
     public void setKeepformat(boolean keepformat) {
         this.keepformat = keepformat;
     }
 
     /**
      * Set escape processing for statements.
      *
      * @since Ant 1.6
      */
     public void setEscapeProcessing(boolean enable) {
         escapeProcessing = enable;
     }
 
     /**
      * Load the sql file and then execute it
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0
                 && filesets.isEmpty()) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or fileset, "
                                              + "transactions or sql statement "
-                                             + "must be set!", location);
+                                             + "must be set!", getLocation());
                 }
             }
 
             if (srcFile != null && !srcFile.exists()) {
-                throw new BuildException("Source file does not exist!", location);
+                throw new BuildException("Source file does not exist!", getLocation());
             }
 
             // deal with the filesets
             for (int i = 0; i < filesets.size(); i++) {
                 FileSet fs = (FileSet) filesets.elementAt(i);
-                DirectoryScanner ds = fs.getDirectoryScanner(project);
-                File srcDir = fs.getDir(project);
+                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
+                File srcDir = fs.getDir(getProject());
 
                 String[] srcFiles = ds.getIncludedFiles();
 
                 // Make a transaction for each file
                 for (int j = 0; j < srcFiles.length; j++) {
                     Transaction t = createTransaction();
                     t.setSrc(new File(srcDir, srcFiles[j]));
                 }
             }
 
             // Make a transaction group for the outer command
             Transaction t = createTransaction();
             t.setSrc(srcFile);
             t.addText(sqlCommand);
             conn = getConnection();
             if (!isValidRdbms(conn)) {
                 return;
             }
             try {
                 statement = conn.createStatement();
                 statement.setEscapeProcessing(escapeProcessing);
 
                 PrintStream out = System.out;
                 try {
                     if (output != null) {
                         log("Opening PrintStream to output file " + output,
                             Project.MSG_VERBOSE);
                         out = new PrintStream(
                                   new BufferedOutputStream(
                                       new FileOutputStream(output
                                                            .getAbsolutePath(),
                                                            append)));
                     }
 
                     // Process all transactions
                     for (Enumeration e = transactions.elements();
                          e.hasMoreElements();) {
 
                         ((Transaction) e.nextElement()).runTransaction(out);
                         if (!isAutocommit()) {
                             log("Commiting transaction", Project.MSG_VERBOSE);
                             conn.commit();
                         }
                     }
                 } finally {
                     if (out != null && out != System.out) {
                         out.close();
                     }
                 }
             } catch (IOException e) {
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {
                         // ignore
                     }
                 }
-                throw new BuildException(e, location);
+                throw new BuildException(e, getLocation());
             } catch (SQLException e) {
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {
                         // ignore
                     }
                 }
-                throw new BuildException(e, location);
+                throw new BuildException(e, getLocation());
             } finally {
                 try {
                     if (statement != null) {
                         statement.close();
                     }
                     if (conn != null) {
                         conn.close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
             }
 
             log(goodSql + " of " + totalSql
                 + " SQL statements executed successfully");
         } finally {
             transactions = savedTransaction;
             sqlCommand = savedSqlCommand;
         }
     }
 
     /**
      * read in lines and execute them
      */
     protected void runStatements(Reader reader, PrintStream out)
         throws SQLException, IOException {
         StringBuffer sql = new StringBuffer();
         String line = "";
 
         BufferedReader in = new BufferedReader(reader);
 
         while ((line = in.readLine()) != null) {
             if (!keepformat) {
                 line = line.trim();
             }
-            line = project.replaceProperties(line);
+            line = getProject().replaceProperties(line);
             if (!keepformat) {
                 if (line.startsWith("//")) {
                     continue;
                 }
                 if (line.startsWith("--")) {
                     continue;
                 }
                 StringTokenizer st = new StringTokenizer(line);
                 if (st.hasMoreTokens()) {
                     String token = st.nextToken();
                     if ("REM".equalsIgnoreCase(token)) {
                         continue;
                     }
                 }
             }
 
             if (!keepformat) {
                 sql.append(" " + line);
             } else {
                 sql.append("\n" + line);
             }
 
             // SQL defines "--" as a comment to EOL
             // and in Oracle it may contain a hint
             // so we cannot just remove it, instead we must end it
             if (!keepformat) {
                 if (line.indexOf("--") >= 0) {
                     sql.append("\n");
                 }
             }
             if ((delimiterType.equals(DelimiterType.NORMAL)
                  && sql.toString().endsWith(delimiter))
                 ||
                 (delimiterType.equals(DelimiterType.ROW)
                  && line.equals(delimiter))) {
                 execSQL(sql.substring(0, sql.length() - delimiter.length()),
                         out);
                 sql.replace(0, sql.length(), "");
             }
         }
         // Catch any statements not followed by ;
         if (!sql.equals("")) {
             execSQL(sql.toString(), out);
         }
     }
 
 
     /**
      * Exec the sql statement.
      */
     protected void execSQL(String sql, PrintStream out) throws SQLException {
         // Check and ignore empty statements
         if ("".equals(sql.trim())) {
             return;
         }
 
         try {
             totalSql++;
             log("SQL: " + sql, Project.MSG_VERBOSE);
 
             boolean ret;
             int updateCount = 0, updateCountTotal = 0;
             ResultSet resultSet = null;
 
             ret = statement.execute(sql);
             updateCount = statement.getUpdateCount();
             resultSet = statement.getResultSet();
             do {
                 if (!ret) {
                     if (updateCount != -1) {
                         updateCountTotal += updateCount;
                     }
                 } else {
                     if (print) {
                         printResults(out);
                     }
                 }
                 ret = statement.getMoreResults();
                 updateCount = statement.getUpdateCount();
                 resultSet = statement.getResultSet();
             } while ((resultSet != null) || (updateCount != -1));
 
             log(updateCountTotal + " rows affected",
                 Project.MSG_VERBOSE);
 
             if (print) {
                 StringBuffer line = new StringBuffer();
                 line.append(updateCountTotal + " rows affected");
                 out.println(line);
             }
 
             SQLWarning warning = conn.getWarnings();
             while (warning != null) {
                 log(warning + " sql warning", Project.MSG_VERBOSE);
                 warning = warning.getNextWarning();
             }
             conn.clearWarnings();
             goodSql++;
         } catch (SQLException e) {
             log("Failed to execute: " + sql, Project.MSG_ERR);
             if (!onError.equals("continue")) {
                 throw e;
             }
             log(e.toString(), Project.MSG_ERR);
         }
     }
 
     /**
      * print any results in the statement.
      */
     protected void printResults(PrintStream out) throws java.sql.SQLException {
         ResultSet rs = null;
         rs = statement.getResultSet();
         if (rs != null) {
             log("Processing new result set.", Project.MSG_VERBOSE);
             ResultSetMetaData md = rs.getMetaData();
             int columnCount = md.getColumnCount();
             StringBuffer line = new StringBuffer();
             if (showheaders) {
                 for (int col = 1; col < columnCount; col++) {
                      line.append(md.getColumnName(col));
                      line.append(",");
                 }
                 line.append(md.getColumnName(columnCount));
                 out.println(line);
                 line = new StringBuffer();
             }
             while (rs.next()) {
                 boolean first = true;
                 for (int col = 1; col <= columnCount; col++) {
                     String columnValue = rs.getString(col);
                     if (columnValue != null) {
                         columnValue = columnValue.trim();
                     }
 
                     if (first) {
                         first = false;
                     } else {
                         line.append(",");
                     }
                     line.append(columnValue);
                 }
                 out.println(line);
                 line = new StringBuffer();
             }
         }
         out.println();
     }
 
     /**
      * The action a task should perform on an error,
      * one of "continue", "stop" and "abort"
      */
     public static class OnError extends EnumeratedAttribute {
         public String[] getValues() {
             return new String[] {"continue", "stop", "abort"};
         }
     }
 
     /**
      * Contains the definition of a new transaction element.
      * Transactions allow several files or blocks of statements
      * to be executed using the same JDBC connection and commit
      * operation in between.
      */
     public class Transaction {
         private File tSrcFile = null;
         private String tSqlCommand = "";
 
         /**
          *
          */
         public void setSrc(File src) {
             this.tSrcFile = src;
         }
 
         /**
          *
          */
         public void addText(String sql) {
             this.tSqlCommand += sql;
         }
 
         /**
          *
          */
         private void runTransaction(PrintStream out)
             throws IOException, SQLException {
             if (tSqlCommand.length() != 0) {
                 log("Executing commands", Project.MSG_INFO);
                 runStatements(new StringReader(tSqlCommand), out);
             }
 
             if (tSrcFile != null) {
                 log("Executing file: " + tSrcFile.getAbsolutePath(),
                     Project.MSG_INFO);
                 Reader reader =
                     (encoding == null) ? new FileReader(tSrcFile)
                                        : new InputStreamReader(
                                              new FileInputStream(tSrcFile),
                                              encoding);
                 try {
                     runStatements(reader, out);
                 } finally {
                     reader.close();
                 }
             }
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/EchoProperties.java b/src/main/org/apache/tools/ant/taskdefs/optional/EchoProperties.java
index 2d713f20f..fb61438d3 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/EchoProperties.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/EchoProperties.java
@@ -1,487 +1,460 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.Writer;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Vector;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.util.CollectionUtils;
 import org.apache.tools.ant.util.DOMElementWriter;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 
 /**
  *  Displays all the current properties in the build. The output can be sent to
  *  a file if desired. <P>
  *
  *  Attribute "destfile" defines a file to send the properties to. This can be
  *  processed as a standard property file later. <P>
  *
  *  Attribute "prefix" defines a prefix which is used to filter the properties
  *  only those properties starting with this prefix will be echoed. <P>
  *
  *  By default, the "failonerror" attribute is enabled. If an error occurs while
  *  writing the properties to a file, and this attribute is enabled, then a
  *  BuildException will be thrown. If disabled, then IO errors will be reported
  *  as a log statement, but no error will be thrown. <P>
  *
  *  Examples: <pre>
  *  &lt;echoproperties  /&gt;
  * </pre> Report the current properties to the log. <P>
  *
  *  <pre>
  *  &lt;echoproperties destfile="my.properties" /&gt;
  * </pre> Report the current properties to the file "my.properties", and will
  *  fail the build if the file could not be created or written to. <P>
  *
  *  <pre>
  *  &lt;echoproperties destfile="my.properties" failonerror="false"
  *      prefix="ant" /&gt;
  * </pre> Report all properties beginning with 'ant' to the file
  *  "my.properties", and will log a message if the file could not be created or
  *  written to, but will still allow the build to continue.
  *
  *@author     Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">
  *      groboclown@users.sourceforge.net</a>
  *@author     Ingmar Stein <a href="mailto:stein@xtramind.com">
         stein@xtramind.com</a>
  *@since      Ant 1.5
  */
 public class EchoProperties extends Task {
 
     /**
      * the properties element.
      */
     private static final String PROPERTIES = "properties";
 
     /**
      * the property element.
      */
     private static final String PROPERTY = "property";
 
     /**
      * name attribute for property, testcase and testsuite elements.
      */
     private static final String ATTR_NAME = "name";
 
     /**
      * value attribute for property elements.
      */
     private static final String ATTR_VALUE = "value";
 
     /**
      * the input file.
      */
     private File inFile = null;
 
     /**
      *  File object pointing to the output file. If this is null, then
      *  we output to the project log, not to a file.
      */
     private File destfile = null;
 
     /**
      *  If this is true, then errors generated during file output will become
      *  build errors, and if false, then such errors will be logged, but not
      *  thrown.
      */
     private boolean failonerror = true;
 
     private Vector propertySets = new Vector();
 
     private String format = "text";
 
     /**
      * Sets the input file.
      *
      * @param file  the input file
      */
     public void setSrcfile(File file) {
         inFile = file;
     }
 
     /**
      *  Set a file to store the property output.  If this is never specified,
      *  then the output will be sent to the Ant log.
      *
      *@param destfile file to store the property output
      */
     public void setDestfile(File destfile) {
         this.destfile = destfile;
     }
 
 
     /**
      * If true, the task will fail if an error occurs writing the properties
      * file, otherwise errors are just logged.
      *
      *@param  failonerror  <tt>true</tt> if IO exceptions are reported as build
      *      exceptions, or <tt>false</tt> if IO exceptions are ignored.
      */
     public void setFailOnError(boolean failonerror) {
         this.failonerror = failonerror;
     }
 
 
     /**
      *  If the prefix is set, then only properties which start with this
      *  prefix string will be recorded.  If this is never set, or it is set
      *  to an empty string or <tt>null</tt>, then all properties will be
      *  recorded. <P>
      *
      *  For example, if the property is set as:
      *    <PRE>&lt;echoproperties  prefix="ant." /&gt;</PRE>
      *  then the property "ant.home" will be recorded, but "ant-example"
      *  will not.
      *
      *@param  prefix  The new prefix value
      */
     public void setPrefix(String prefix) {
         PropertySet ps = new PropertySet();
         ps.setProject(getProject());
         ps.appendPrefix(prefix);
         addPropertyset(ps);
     }
 
     /**
      * A set of properties to write.
      *
      * @since Ant 1.6
      */
     public void addPropertyset(PropertySet ps) {
         propertySets.addElement(ps);
     }
 
     public void setFormat(FormatAttribute ea) {
         format = ea.getValue();
     }
 
     public static class FormatAttribute extends EnumeratedAttribute {
         private String [] formats = new String[]{"xml", "text"};
 
         public String[] getValues() {
             return formats;
         }
     }
 
     /**
      *  Run the task.
      *
      *@exception  BuildException  trouble, probably file IO
      */
     public void execute() throws BuildException {
         //copy the properties file
         Hashtable allProps = new Hashtable();
 
         /* load properties from file if specified, otherwise
         use Ant's properties */
         if (inFile == null && propertySets.size() == 0) {
             // add ant properties
             CollectionUtils.putAll(allProps, getProject().getProperties());
         } else if (inFile != null) {
             if (inFile.exists() && inFile.isDirectory()) {
                 String message = "srcfile is a directory!";
                 if (failonerror) {
                     throw new BuildException(message, getLocation());
                 } else {
                     log(message, Project.MSG_ERR);
                 }
                 return;
             }
 
             if (inFile.exists() && !inFile.canRead()) {
                 String message = "Can not read from the specified srcfile!";
                 if (failonerror) {
                     throw new BuildException(message, getLocation());
                 } else {
                     log(message, Project.MSG_ERR);
                 }
                 return;
             }
 
             FileInputStream in = null;
             try {
                 in = new FileInputStream(inFile);
                 Properties props = new Properties();
                 props.load(in);
                 CollectionUtils.putAll(allProps, props);
             } catch (FileNotFoundException fnfe) {
                 String message =
                     "Could not find file " + inFile.getAbsolutePath();
                 if (failonerror) {
                     throw new BuildException(message, fnfe, getLocation());
                 } else {
                     log(message, Project.MSG_WARN);
                 }
                 return;
             } catch (IOException ioe) {
                 String message =
                     "Could not read file " + inFile.getAbsolutePath();
                 if (failonerror) {
                     throw new BuildException(message, ioe, getLocation());
                 } else {
                     log(message, Project.MSG_WARN);
                 }
                 return;
             } finally {
                 try {
                     if (null != in) {
                         in.close();
                     }
                 } catch (IOException ioe) {
                     //ignore
                 }
             }
         }
 
         Enumeration e = propertySets.elements();
         while (e.hasMoreElements()) {
             PropertySet ps = (PropertySet) e.nextElement();
             CollectionUtils.putAll(allProps, ps.getProperties());
         }
 
         OutputStream os = null;
         try {
             if (destfile == null) {
                 os = new ByteArrayOutputStream();
                 saveProperties(allProps, os);
                 log(os.toString(), Project.MSG_INFO);
             } else {
                 if (destfile.exists() && destfile.isDirectory()) {
                     String message = "destfile is a directory!";
                     if (failonerror) {
                         throw new BuildException(message, getLocation());
                     } else {
                         log(message, Project.MSG_ERR);
                     }
                     return;
                 }
 
                 if (destfile.exists() && !destfile.canWrite()) {
                     String message =
                         "Can not write to the specified destfile!";
                     if (failonerror) {
                         throw new BuildException(message, getLocation());
                     } else {
                         log(message, Project.MSG_ERR);
                     }
                     return;
                 }
                 os = new FileOutputStream(this.destfile);
                 saveProperties(allProps, os);
             }
         } catch (IOException ioe) {
             if (failonerror) {
                 throw new BuildException(ioe, getLocation());
             } else {
                 log(ioe.getMessage(), Project.MSG_INFO);
             }
         } finally {
             if (os != null) {
                 try {
                     os.close();
                 } catch (IOException ex) {
                     //ignore
                 }
             }
         }
     }
 
 
     /**
      *  Send the key/value pairs in the hashtable to the given output stream.
      *  Only those properties matching the <tt>prefix</tt> constraint will be
      *  sent to the output stream.
      *  The output stream will be closed when this method returns.
      *
      *@param  allProps         propfile to save
      *@param  os               output stream
      *@exception  IOException  trouble
      */
     protected void saveProperties(Hashtable allProps, OutputStream os)
              throws IOException, BuildException {
         Properties props = new Properties();
         Enumeration e = allProps.keys();
         while (e.hasMoreElements()) {
             String name = e.nextElement().toString();
             String value = allProps.get(name).toString();
             props.put(name, value);
         }
 
         if ("text".equals(format)) {
             jdkSaveProperties(props, os, "Ant properties");
         } else if ("xml".equals(format)) {
             xmlSaveProperties(props, os);
         }
     }
 
     protected void xmlSaveProperties(Properties props,
                                      OutputStream os) throws IOException {
         // create XML document
         Document doc = getDocumentBuilder().newDocument();
         Element rootElement = doc.createElement(PROPERTIES);
 
         // output properties
         String name;
         Enumeration e = props.propertyNames();
         while (e.hasMoreElements()) {
             name = (String) e.nextElement();
             Element propElement = doc.createElement(PROPERTY);
             propElement.setAttribute(ATTR_NAME, name);
             propElement.setAttribute(ATTR_VALUE, props.getProperty(name));
             rootElement.appendChild(propElement);
         }
 
         Writer wri = null;
         try {
             wri = new OutputStreamWriter(os, "UTF8");
             wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
             (new DOMElementWriter()).write(rootElement, wri, 0, "\t");
             wri.flush();
         } catch (IOException ioe) {
             throw new BuildException("Unable to write XML file", ioe);
         } finally {
             if (wri != null) {
                 wri.close();
             }
         }
     }
 
     /**
      *  JDK 1.2 allows for the safer method
      *  <tt>Properties.store(OutputStream, String)</tt>, which throws an
-     *  <tt>IOException</tt> on an output error.  This method attempts to
-     *  use the JDK 1.2 method first, and if that does not exist, then the
-     *  JDK 1.0 compatible method
-     *  <tt>Properties.save(OutputStream, String)</tt> is used instead.
+     *  <tt>IOException</tt> on an output error. 
      *
      *@param props the properties to record
      *@param os record the properties to this output stream
      *@param header prepend this header to the property output
-     *@exception IOException on an I/O error during a write.  Only thrown
-     *      for JDK 1.2+.
+     *@exception IOException on an I/O error during a write.  
      */
     protected void jdkSaveProperties(Properties props, OutputStream os,
                                      String header) throws IOException {
-        try {
-            java.lang.reflect.Method m = props.getClass().getMethod(
-                "store", new Class[]{OutputStream.class, String.class});
-            m.invoke(props, new Object[]{os, header});
-        } catch (java.lang.reflect.InvocationTargetException ite) {
-            Throwable t = ite.getTargetException();
-            if (t instanceof IOException) {
-                throw (IOException) t;
-            }
-            if (t instanceof RuntimeException) {
-                throw (RuntimeException) t;
-            }
-
-            // not an expected exception.  Resort to JDK 1.0 to execute
-            // this method
-            jdk10SaveProperties(props, os, header);
-        } catch (ThreadDeath td) {
-            // don't trap thread death errors.
-            throw td;
-        } catch (Throwable ex) {
-            // this 'store' method is not available, so resort to the JDK 1.0
-            // compatible method.
-            jdk10SaveProperties(props, os, header);
-        }
+       try {
+           props.store(os, header);
+
+       } catch (IOException ioe) {
+           throw new BuildException(ioe, getLocation());
+       } finally {
+           if (os != null) {
+               try {
+                   os.close();
+               } catch (IOException ioex) {
+                   log("Failed to close output stream");
+               }
+           }
+       }
     }
 
 
     /**
-     * Save the properties to the output stream using the JDK 1.0 compatible
-     * method.  This won't throw an <tt>IOException</tt> on an output error.
-     *
-     *@param props the properties to record
-     *@param os record the properties to this output stream
-     *@param header prepend this header to the property output
-     */
-    protected void jdk10SaveProperties(Properties props, OutputStream os,
-                                       String header) {
-        props.save(os, header);
-    }
-
-    /**
      * Uses the DocumentBuilderFactory to get a DocumentBuilder instance.
      *
      * @return   The DocumentBuilder instance
      */
     private static DocumentBuilder getDocumentBuilder() {
         try {
             return DocumentBuilderFactory.newInstance().newDocumentBuilder();
         } catch (Exception e) {
             throw new ExceptionInInitializerError(e);
         }
     }
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/ccm/CCMCheck.java b/src/main/org/apache/tools/ant/taskdefs/optional/ccm/CCMCheck.java
index 4dbc9ba94..b101c5e96 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/ccm/CCMCheck.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/ccm/CCMCheck.java
@@ -1,237 +1,237 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.taskdefs.optional.ccm;
 
 
 import java.io.File;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.FileSet;
 
 
 /**
  * Class common to all check commands (checkout, checkin,checkin default task);
  * @author Benoit Moussaud benoit.moussaud@criltelecom.com
  * @ant.task ignore="true"
  */
 public class CCMCheck extends Continuus {
 
     private File file = null;
     private String comment = null;
     private String task = null;
 
     protected Vector filesets = new Vector();
 
     public CCMCheck() {
         super();
     }
 
     /**
      * Get the value of file.
      * @return value of file.
      */
     public File getFile() {
         return file;
     }
 
     /**
      * Sets the path to the file that the command will operate on.
      * @param v  Value to assign to file.
      */
     public void setFile(File v) {
         log("working file " + v, Project.MSG_VERBOSE);
         this.file = v;
     }
 
     /**
      * Get the value of comment.
      * @return value of comment.
      */
     public String getComment() {
         return comment;
     }
 
     /**
      * Specifies a comment.
      * @param v  Value to assign to comment.
      */
     public void setComment(String v) {
         this.comment = v;
     }
 
 
     /**
      * Get the value of task.
      * @return value of task.
      */
     public String getTask() {
         return task;
     }
 
     /**
      * Specifies the task number used to check
      * in the file (may use 'default').
      * @param v  Value to assign to task.
      */
     public void setTask(String v) {
         this.task = v;
     }
 
 
     /**
      * Adds a set of files to copy.
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
 
     /**
      * Executes the task.
      * <p>
      * Builds a command line to execute ccm and then calls Exec's run method
      * to execute the command line.
      * </p>
      */
     public void execute() throws BuildException {
 
         if (file == null && filesets.size() == 0) {
             throw new BuildException(
                 "Specify at least one source - a file or a fileset.");
         }
 
         if (file != null && file.exists() && file.isDirectory()) {
             throw new BuildException("CCMCheck cannot be generated for directories");
         }
 
         if (file != null  && filesets.size() > 0) {
             throw new BuildException("Choose between file and fileset !");
         }
 
         if (getFile() != null) {
             doit();
             return;
         }
 
         int sizeofFileSet = filesets.size();
         for (int i = 0; i < sizeofFileSet; i++) {
             FileSet fs = (FileSet) filesets.elementAt(i);
-            DirectoryScanner ds = fs.getDirectoryScanner(project);
+            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
             String[] srcFiles = ds.getIncludedFiles();
             for (int j = 0; j < srcFiles.length; j++) {
-                File src = new File(fs.getDir(project), srcFiles[j]);
+                File src = new File(fs.getDir(getProject()), srcFiles[j]);
                 setFile(src);
                 doit();
             }
         }
     }
 
     /**
      * check the file given by getFile().
      */
     private void doit() {
         Commandline commandLine = new Commandline();
 
         // build the command line from what we got the format is
         // ccm co /t .. files
         // as specified in the CCM.EXE help
 
         commandLine.setExecutable(getCcmCommand());
         commandLine.createArgument().setValue(getCcmAction());
 
         checkOptions(commandLine);
 
         int result = run(commandLine);
         if (Execute.isFailure(result)) {
             String msg = "Failed executing: " + commandLine.toString();
             throw new BuildException(msg, getLocation());
         }
     }
 
 
     /**
      * Check the command line options.
      */
     private void checkOptions(Commandline cmd) {
         if (getComment() != null) {
             cmd.createArgument().setValue(FLAG_COMMENT);
             cmd.createArgument().setValue(getComment());
         }
 
         if (getTask() != null) {
             cmd.createArgument().setValue(FLAG_TASK);
             cmd.createArgument().setValue(getTask());
         }
 
         if (getFile() != null) {
             cmd.createArgument().setValue(file.getAbsolutePath());
         }
     }
 
     /**
      * -comment flag -- comment to attach to the file
      */
     public static final String FLAG_COMMENT = "/comment";
 
     /**
      *  -task flag -- associate checckout task with task
      */
     public static final String FLAG_TASK = "/task";
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/ccm/Continuus.java b/src/main/org/apache/tools/ant/taskdefs/optional/ccm/Continuus.java
index 925c03c75..82e83e4d7 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/ccm/Continuus.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/ccm/Continuus.java
@@ -1,169 +1,169 @@
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.taskdefs.optional.ccm;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 
 
 /**
  * A base class for creating tasks for executing commands on Continuus 5.1.
  * <p>
  * The class extends the  task as it operates by executing the ccm.exe program
  * supplied with Continuus/Synergy. By default the task expects the ccm executable to be
  * in the path,
  * you can override this be specifying the ccmdir attribute.
  * </p>
  *
  * @author Benoit Moussaud benoit.moussaud@criltelecom.com
  */
 public abstract class Continuus extends Task {
 
     private String ccmDir = "";
     private String ccmAction = "";
 
     /**
      * Get the value of ccmAction.
      * @return value of ccmAction.
      */
     public String getCcmAction() {
         return ccmAction;
     }
 
     /**
      * Set the value of ccmAction.
      * @param v  Value to assign to ccmAction.
      * @ant.attribute ignore="true"
      */
     public void setCcmAction(String v) {
         this.ccmAction = v;
     }
 
 
     /**
      * Set the directory where the ccm executable is located.
      *
      * @param dir the directory containing the ccm executable
      */
     public final void setCcmDir(String dir) {
-        ccmDir = getProject().translatePath(dir);
+        ccmDir = Project.translatePath(dir);
     }
 
     /**
      * Builds and returns the command string to execute ccm
      * @return String containing path to the executable
      */
     protected final String getCcmCommand() {
         String toReturn = ccmDir;
         if (!toReturn.equals("") && !toReturn.endsWith("/")) {
             toReturn += "/";
         }
 
         toReturn += CCM_EXE;
 
         return toReturn;
     }
 
 
     protected int run(Commandline cmd, ExecuteStreamHandler handler) {
         try {
             Execute exe = new Execute(handler);
             exe.setAntRun(getProject());
             exe.setWorkingDirectory(getProject().getBaseDir());
             exe.setCommandline(cmd.getCommandline());
             return exe.execute();
         } catch (java.io.IOException e) {
             throw new BuildException(e, getLocation());
         }
     }
 
     protected int run(Commandline cmd) {
         return run(cmd, new LogStreamHandler(this, Project.MSG_VERBOSE, Project.MSG_WARN));
     }
 
     /**
      * Constant for the thing to execute
      */
     private static final String CCM_EXE = "ccm";
 
     /**
      * The 'CreateTask' command
      */
     public static final String COMMAND_CREATE_TASK = "create_task";
     /**
      * The 'Checkout' command
      */
     public static final String COMMAND_CHECKOUT = "co";
     /**
      * The 'Checkin' command
      */
     public static final String COMMAND_CHECKIN = "ci";
     /**
      * The 'Reconfigure' command
      */
     public static final String COMMAND_RECONFIGURE = "reconfigure";
 
     /**
      * The 'Reconfigure' command
      */
     public static final String COMMAND_DEFAULT_TASK = "default_task";
 
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/i18n/Translate.java b/src/main/org/apache/tools/ant/taskdefs/optional/i18n/Translate.java
index 2f09ae3b9..a12703ca8 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/i18n/Translate.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/i18n/Translate.java
@@ -1,661 +1,660 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 package org.apache.tools.ant.taskdefs.optional.i18n;
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.util.Hashtable;
 import java.util.Locale;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
-import org.apache.tools.ant.filters.TokenFilter;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.LineTokenizer;
 
 /**
  * Translates text embedded in files using Resource Bundle files.
  * Since ant 1.6 preserves line endings
  *
  * @author Magesh Umasankar, Don Brown
  * @author <a href="mailto:tom@tbee.org">Tom Eugelink</a>
  */
 public class Translate extends MatchingTask {
     /**
      * search a bundle matching the specified language, the country and the variant
      */
     private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT = 0;
     /**
      * search a bundle matching the specified language, and the country
      */
     private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY = 1;
     /**
      * search a bundle matching the specified language only
      */
     private static final int BUNDLE_SPECIFIED_LANGUAGE = 2;
     /**
      * search a bundle matching nothing special
      */
     private static final int BUNDLE_NOMATCH = 3;
     /**
      * search a bundle matching the language, the country and the variant
      * of the current locale of the computer
      */
     private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT = 4;
     /**
      * search a bundle matching the language, and the country
      * of the current locale of the computer
      */
     private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY = 5;
     /**
      * search a bundle matching the language only
      * of the current locale of the computer
      */
     private static final int BUNDLE_DEFAULT_LANGUAGE = 6;
     /**
      * number of possibilities for the search
      */
      private static final int BUNDLE_MAX_ALTERNATIVES = BUNDLE_DEFAULT_LANGUAGE + 1;
     /**
      * Family name of resource bundle
      */
     private String bundle;
 
     /**
      * Locale specific language of the resource bundle
      */
     private String bundleLanguage;
 
     /**
      * Locale specific country of the resource bundle
      */
     private String bundleCountry;
 
     /**
      * Locale specific variant of the resource bundle
      */
     private String bundleVariant;
 
     /**
      * Destination directory
      */
     private File toDir;
 
     /**
      * Source file encoding scheme
      */
     private String srcEncoding;
 
     /**
      * Destination file encoding scheme
      */
     private String destEncoding;
 
     /**
      * Resource Bundle file encoding scheme, defaults to srcEncoding
      */
     private String bundleEncoding;
 
     /**
      * Starting token to identify keys
      */
     private String startToken;
 
     /**
      * Ending token to identify keys
      */
     private String endToken;
 
     /**
      * Whether or not to create a new destination file.
      * Defaults to <code>false</code>.
      */
     private boolean forceOverwrite;
 
     /**
      * Vector to hold source file sets.
      */
     private Vector filesets = new Vector();
 
     /**
      * Holds key value pairs loaded from resource bundle file
      */
     private Hashtable resourceMap = new Hashtable();
     /**
 
      * Used to resolve file names.
      */
     private FileUtils fileUtils = FileUtils.newFileUtils();
 
     /**
      * Last Modified Timestamp of resource bundle file being used.
      */
     private long[] bundleLastModified = new long[BUNDLE_MAX_ALTERNATIVES];
 
     /**
      * Last Modified Timestamp of source file being used.
      */
     private long srcLastModified;
 
     /**
      * Last Modified Timestamp of destination file being used.
      */
     private long destLastModified;
 
     /**
      * Has at least one file from the bundle been loaded?
      */
     private boolean loaded = false;
 
     /**
      * Sets Family name of resource bundle; required.
      * @param bundle family name of resource bundle
      */
     public void setBundle(String bundle) {
         this.bundle = bundle;
     }
 
     /**
      * Sets locale specific language of resource bundle; optional.
      * @param bundleLanguage langage of the bundle
      */
     public void setBundleLanguage(String bundleLanguage) {
         this.bundleLanguage = bundleLanguage;
     }
 
     /**
      * Sets locale specific country of resource bundle; optional.
      * @param bundleCountry country of the bundle
      */
     public void setBundleCountry(String bundleCountry) {
         this.bundleCountry = bundleCountry;
     }
 
     /**
      * Sets locale specific variant of resource bundle; optional.
      * @param bundleVariant locale variant of resource bundle
      */
     public void setBundleVariant(String bundleVariant) {
         this.bundleVariant = bundleVariant;
     }
 
     /**
      * Sets Destination directory; required.
      * @param toDir destination directory
      */
     public void setToDir(File toDir) {
         this.toDir = toDir;
     }
 
     /**
      * Sets starting token to identify keys; required.
      * @param startToken starting token to identify keys
      */
     public void setStartToken(String startToken) {
         this.startToken = startToken;
     }
 
     /**
      * Sets ending token to identify keys; required.
      * @param endToken ending token to identify keys
      */
     public void setEndToken(String endToken) {
         this.endToken = endToken;
     }
 
     /**
      * Sets source file encoding scheme; optional,
      * defaults to encoding of local system.
      * @param srcEncoding source file encoding
      */
     public void setSrcEncoding(String srcEncoding) {
         this.srcEncoding = srcEncoding;
     }
 
     /**
      * Sets destination file encoding scheme; optional.  Defaults to source file
      * encoding
      * @param destEncoding destination file encoding scheme
      */
     public void setDestEncoding(String destEncoding) {
         this.destEncoding = destEncoding;
     }
 
     /**
      * Sets Resource Bundle file encoding scheme; optional.  Defaults to source file
      * encoding
      * @param bundleEncoding bundle file encoding scheme
      */
     public void setBundleEncoding(String bundleEncoding) {
         this.bundleEncoding = bundleEncoding;
     }
 
     /**
      * Whether or not to overwrite existing file irrespective of
      * whether it is newer than the source file as well as the
      * resource bundle file.
      * Defaults to false.
      * @param forceOverwrite whether or not to overwrite existing files
      */
     public void setForceOverwrite(boolean forceOverwrite) {
         this.forceOverwrite = forceOverwrite;
     }
 
     /**
      * Adds a set of files to translate as a nested fileset element.
      * @param set the fileset to be added
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
     /**
      * Check attributes values, load resource map and translate
      * @throws BuildException if the required attributes are not set
      * Required : <ul>
      *       <li>bundle</li>
      *       <li>starttoken</li>
      *       <li>endtoken</li>
      *            </ul>
      */
     public void execute() throws BuildException {
         if (bundle == null) {
             throw new BuildException("The bundle attribute must be set.",
                                      getLocation());
         }
 
         if (startToken == null) {
             throw new BuildException("The starttoken attribute must be set.",
                                      getLocation());
         }
 
         if (endToken == null) {
             throw new BuildException("The endtoken attribute must be set.",
                                      getLocation());
         }
 
         if (bundleLanguage == null) {
             Locale l = Locale.getDefault();
             bundleLanguage  = l.getLanguage();
         }
 
         if (bundleCountry == null) {
             bundleCountry = Locale.getDefault().getCountry();
         }
 
         if (bundleVariant == null) {
             Locale l = new Locale(bundleLanguage, bundleCountry);
             bundleVariant = l.getVariant();
         }
 
         if (toDir == null) {
             throw new BuildException("The todir attribute must be set.",
                                      getLocation());
         }
 
         if (!toDir.exists()) {
             toDir.mkdirs();
         } else if (toDir.isFile()) {
             throw new BuildException(toDir + " is not a directory");
         }
 
         if (srcEncoding == null) {
             srcEncoding = System.getProperty("file.encoding");
         }
 
         if (destEncoding == null) {
             destEncoding = srcEncoding;
         }
 
         if (bundleEncoding == null) {
             bundleEncoding = srcEncoding;
         }
 
         loadResourceMaps();
 
         translate();
     }
 
     /**
      * Load resource maps based on resource bundle encoding scheme.
      * The resource bundle lookup searches for resource files with various
      * suffixes on the basis of (1) the desired locale and (2) the default
      * locale (basebundlename), in the following order from lower-level
      * (more specific) to parent-level (less specific):
      *
      * basebundlename + "_" + language1 + "_" + country1 + "_" + variant1
      * basebundlename + "_" + language1 + "_" + country1
      * basebundlename + "_" + language1
      * basebundlename
      * basebundlename + "_" + language2 + "_" + country2 + "_" + variant2
      * basebundlename + "_" + language2 + "_" + country2
      * basebundlename + "_" + language2
      *
      * To the generated name, a ".properties" string is appeneded and
      * once this file is located, it is treated just like a properties file
      * but with bundle encoding also considered while loading.
      */
     private void loadResourceMaps() throws BuildException {
         Locale locale = new Locale(bundleLanguage,
                                    bundleCountry,
                                    bundleVariant);
         String language = locale.getLanguage().length() > 0
             ? "_" + locale.getLanguage() : "";
         String country = locale.getCountry().length() > 0
             ? "_" + locale.getCountry() : "";
         String variant = locale.getVariant().length() > 0
             ? "_" + locale.getVariant() : "";
         String bundleFile = bundle + language + country + variant;
         processBundle(bundleFile, BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT, false);
 
         bundleFile = bundle + language + country;
         processBundle(bundleFile, BUNDLE_SPECIFIED_LANGUAGE_COUNTRY, false);
 
         bundleFile = bundle + language;
         processBundle(bundleFile, BUNDLE_SPECIFIED_LANGUAGE, false);
 
         bundleFile = bundle;
         processBundle(bundleFile, BUNDLE_NOMATCH, false);
 
         //Load default locale bundle files
         //using default file encoding scheme.
         locale = Locale.getDefault();
 
         language = locale.getLanguage().length() > 0
             ? "_" + locale.getLanguage() : "";
         country = locale.getCountry().length() > 0
             ? "_" + locale.getCountry() : "";
         variant = locale.getVariant().length() > 0
             ? "_" + locale.getVariant() : "";
         bundleEncoding = System.getProperty("file.encoding");
 
         bundleFile = bundle + language + country + variant;
         processBundle(bundleFile, BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT, false);
 
         bundleFile = bundle + language + country;
         processBundle(bundleFile, BUNDLE_DEFAULT_LANGUAGE_COUNTRY, false);
 
         bundleFile = bundle + language;
         processBundle(bundleFile, BUNDLE_DEFAULT_LANGUAGE, true);
     }
 
     /**
      * Process each file that makes up this bundle.
      */
     private void processBundle(final String bundleFile, final int i,
                                final boolean checkLoaded) throws BuildException {
         final File propsFile = getProject().resolveFile(bundleFile + ".properties");
         FileInputStream ins = null;
         try {
             ins = new FileInputStream(propsFile);
             loaded = true;
             bundleLastModified[i] = propsFile.lastModified();
             log("Using " + propsFile, Project.MSG_DEBUG);
             loadResourceMap(ins);
         } catch (IOException ioe) {
             log(propsFile + " not found.", Project.MSG_DEBUG);
             //if all resource files associated with this bundle
             //have been scanned for and still not able to
             //find a single resrouce file, throw exception
             if (!loaded && checkLoaded) {
                 throw new BuildException(ioe.getMessage(), getLocation());
             }
         }
     }
 
     /**
      * Load resourceMap with key value pairs.  Values of existing keys
      * are not overwritten.  Bundle's encoding scheme is used.
      */
     private void loadResourceMap(FileInputStream ins) throws BuildException {
         try {
             BufferedReader in = null;
             InputStreamReader isr = new InputStreamReader(ins, bundleEncoding);
             in = new BufferedReader(isr);
             String line = null;
             while ((line = in.readLine()) != null) {
                 //So long as the line isn't empty and isn't a comment...
                 if (line.trim().length() > 1 && '#' != line.charAt(0) && '!' != line.charAt(0)) {
                     //Legal Key-Value separators are :, = and white space.
                     int sepIndex = line.indexOf('=');
                     if (-1 == sepIndex) {
                         sepIndex = line.indexOf(':');
                     }
                     if (-1 == sepIndex) {
                         for (int k = 0; k < line.length(); k++) {
                             if (Character.isSpaceChar(line.charAt(k))) {
                                 sepIndex = k;
                                 break;
                             }
                         }
                     }
                     //Only if we do have a key is there going to be a value
                     if (-1 != sepIndex) {
                         String key = line.substring(0, sepIndex).trim();
                         String value = line.substring(sepIndex + 1).trim();
                         //Handle line continuations, if any
                         while (value.endsWith("\\")) {
                             value = value.substring(0, value.length() - 1);
                             if ((line = in.readLine()) != null) {
                                 value = value + line.trim();
                             } else {
                                 break;
                             }
                         }
                         if (key.length() > 0) {
                             //Has key already been loaded into resourceMap?
                             if (resourceMap.get(key) == null) {
                                 resourceMap.put(key, value);
                             }
                         }
                     }
                 }
             }
             if (in != null) {
                 in.close();
             }
         } catch (IOException ioe) {
             throw new BuildException(ioe.getMessage(), getLocation());
         }
     }
 
     /**
      * Reads source file line by line using the source encoding and
      * searches for keys that are sandwiched between the startToken
      * and endToken.  The values for these keys are looked up from
      * the hashtable and substituted.  If the hashtable doesn't
      * contain the key, they key itself is used as the value.
      * Detination files and directories are created as needed.
      * The destination file is overwritten only if
      * the forceoverwritten attribute is set to true if
      * the source file or any associated bundle resource file is
      * newer than the destination file.
      */
     private void translate() throws BuildException {
         for (int i = 0; i < filesets.size(); i++) {
             FileSet fs = (FileSet) filesets.elementAt(i);
             DirectoryScanner ds = fs.getDirectoryScanner(getProject());
             String[] srcFiles = ds.getIncludedFiles();
             for (int j = 0; j < srcFiles.length; j++) {
                 try {
                     File dest = fileUtils.resolveFile(toDir, srcFiles[j]);
                     //Make sure parent dirs exist, else, create them.
                     try {
                         File destDir = new File(dest.getParent());
                         if (!destDir.exists()) {
                             destDir.mkdirs();
                         }
                     } catch (Exception e) {
                         log("Exception occured while trying to check/create "
                             + " parent directory.  " + e.getMessage(),
                             Project.MSG_DEBUG);
                     }
                     destLastModified = dest.lastModified();
                     File src = fileUtils.resolveFile(ds.getBasedir(), srcFiles[j]);
                     srcLastModified = src.lastModified();
                     //Check to see if dest file has to be recreated
                     boolean needsWork = forceOverwrite
                         || destLastModified < srcLastModified;
                     if (!needsWork) {
                         for (int icounter = 0; icounter < BUNDLE_MAX_ALTERNATIVES; icounter++) {
                             needsWork = (destLastModified < bundleLastModified[icounter]);
                             if (needsWork) {
                                 break;
                             }
                         }
                     }
                     if (needsWork) {
                         log("Processing " + srcFiles[j],
                             Project.MSG_DEBUG);
                         FileOutputStream fos = new FileOutputStream(dest);
                         BufferedWriter out
                             = new BufferedWriter(new OutputStreamWriter(fos, destEncoding));
                         FileInputStream fis = new FileInputStream(src);
                         BufferedReader in
                             = new BufferedReader(new InputStreamReader(fis, srcEncoding));
                         String line;
                         LineTokenizer lineTokenizer = new LineTokenizer();
                         lineTokenizer.setIncludeDelims(true);
                         line = lineTokenizer.getToken(in);
                         while ((line) != null) {
                         // 2003-02-21 new replace algorithm by tbee (tbee@tbee.org)
                         // because it wasn't able to replace something like "@aaa;@bbb;"
 
                         // is there a startToken
                         // and there is still stuff following the startToken
                         int startIndex = line.indexOf(startToken);
                         while (startIndex >= 0
                             && (startIndex + startToken.length()) <= line.length()) {
                             // the new value, this needs to be here
                             // because it is required to calculate the next position to search from
                             // at the end of the loop
                             String replace = null;
 
                             // we found a starttoken, is there an endtoken following?
                             // start at token+tokenlength because start and end
                             // token may be indentical
                             int endIndex = line.indexOf(endToken, startIndex + startToken.length());
                             if (endIndex < 0) {
                                 startIndex += 1;
                             } else {
                                 // grab the token
                                 String token
                                     = line.substring(startIndex + startToken.length(), endIndex);
 
                                 // If there is a white space or = or :, then
                                 // it isn't to be treated as a valid key.
                                 boolean validToken = true;
                                 for (int k = 0; k < token.length() && validToken; k++) {
                                     char c = token.charAt(k);
                                     if (c == ':' || c == '='
                                         || Character.isSpaceChar(c)) {
                                         validToken = false;
                                     }
                                 }
                                 if (!validToken) {
                                     startIndex += 1;
                                 } else {
                                     // find the replace string
                                     if (resourceMap.containsKey(token)) {
                                         replace = (String) resourceMap.get(token);
                                     } else {
                                         replace = token;
                                     }
 
 
                                     // generate the new line
                                     line = line.substring(0, startIndex)
                                          + replace
                                          + line.substring(endIndex + endToken.length());
 
                                     // set start position for next search
                                     startIndex += replace.length();
                                 }
                             }
 
                             // find next starttoken
                             startIndex = line.indexOf(startToken, startIndex);
                         }
 
 
                             out.write(line);
                             line = lineTokenizer.getToken(in);
                         }
                         if (in != null) {
                             in.close();
                         }
                         if (out != null) {
                             out.close();
                         }
                     } else {
                         log("Skipping " + srcFiles[j]
                             + " as destination file is up to date",
                             Project.MSG_VERBOSE);
                     }
                 } catch (IOException ioe) {
                     throw new BuildException(ioe.getMessage(), getLocation());
                 }
             }
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/javacc/JJTree.java b/src/main/org/apache/tools/ant/taskdefs/optional/javacc/JJTree.java
index 82d6e95e7..26e8c40c3 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/javacc/JJTree.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/javacc/JJTree.java
@@ -1,425 +1,427 @@
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.taskdefs.optional.javacc;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  * Runs the JJTree compiler compiler.
  *
  * @author thomas.haas@softwired-inc.com
  * @author Michael Saunders
  *         <a href="mailto:michael@amtec.com">michael@amtec.com</a>
  */
 public class JJTree extends Task {
 
     // keys to optional attributes
     private static final String OUTPUT_FILE       = "OUTPUT_FILE";
     private static final String BUILD_NODE_FILES  = "BUILD_NODE_FILES";
     private static final String MULTI             = "MULTI";
     private static final String NODE_DEFAULT_VOID = "NODE_DEFAULT_VOID";
     private static final String NODE_FACTORY      = "NODE_FACTORY";
     private static final String NODE_SCOPE_HOOK   = "NODE_SCOPE_HOOK";
     private static final String NODE_USES_PARSER  = "NODE_USES_PARSER";
     private static final String STATIC            = "STATIC";
     private static final String VISITOR           = "VISITOR";
 
     private static final String NODE_PACKAGE      = "NODE_PACKAGE";
     private static final String VISITOR_EXCEPTION = "VISITOR_EXCEPTION";
     private static final String NODE_PREFIX       = "NODE_PREFIX";
 
     private final Hashtable optionalAttrs = new Hashtable();
 
     private String outputFile = null;
 
     private static final String DEFAULT_SUFFIX = ".jj";
 
     // required attributes
     private File outputDirectory = null;
     private File target          = null;
     private File javaccHome      = null;
 
     private CommandlineJava cmdl = new CommandlineJava();
 
 
     /**
      * Sets the BUILD_NODE_FILES grammar option.
      */
     public void setBuildnodefiles(boolean buildNodeFiles) {
         optionalAttrs.put(BUILD_NODE_FILES, new Boolean(buildNodeFiles));
     }
 
     /**
      * Sets the MULTI grammar option.
      */
     public void setMulti(boolean multi) {
         optionalAttrs.put(MULTI, new Boolean(multi));
     }
 
     /**
      * Sets the NODE_DEFAULT_VOID grammar option.
      */
     public void setNodedefaultvoid(boolean nodeDefaultVoid) {
         optionalAttrs.put(NODE_DEFAULT_VOID, new Boolean(nodeDefaultVoid));
     }
 
     /**
      * Sets the NODE_FACTORY grammar option.
      */
     public void setNodefactory(boolean nodeFactory) {
         optionalAttrs.put(NODE_FACTORY, new Boolean(nodeFactory));
     }
 
     /**
      * Sets the NODE_SCOPE_HOOK grammar option.
      */
     public void setNodescopehook(boolean nodeScopeHook) {
         optionalAttrs.put(NODE_SCOPE_HOOK, new Boolean(nodeScopeHook));
     }
 
     /**
      * Sets the NODE_USES_PARSER grammar option.
      */
     public void setNodeusesparser(boolean nodeUsesParser) {
         optionalAttrs.put(NODE_USES_PARSER, new Boolean(nodeUsesParser));
     }
 
     /**
      * Sets the STATIC grammar option.
      */
     public void setStatic(boolean staticParser) {
         optionalAttrs.put(STATIC, new Boolean(staticParser));
     }
 
     /**
      * Sets the VISITOR grammar option.
      */
     public void setVisitor(boolean visitor) {
         optionalAttrs.put(VISITOR, new Boolean(visitor));
     }
 
     /**
      * Sets the NODE_PACKAGE grammar option.
      */
     public void setNodepackage(String nodePackage) {
         optionalAttrs.put(NODE_PACKAGE, new String(nodePackage));
     }
 
     /**
      * Sets the VISITOR_EXCEPTION grammar option.
      */
     public void setVisitorException(String visitorException) {
         optionalAttrs.put(VISITOR_EXCEPTION, new String(visitorException));
     }
 
     /**
      * Sets the NODE_PREFIX grammar option.
      */
     public void setNodeprefix(String nodePrefix) {
         optionalAttrs.put(NODE_PREFIX, new String(nodePrefix));
     }
 
     /**
      * The directory to write the generated JavaCC grammar and node files to.
      * If not set, the files are written to the directory
      * containing the grammar file.
      */
     public void setOutputdirectory(File outputDirectory) {
         this.outputDirectory = outputDirectory;
     }
 
     /**
      * The outputfile to write the generated JavaCC grammar file to.
      * If not set, the file is written with the same name as
      * the JJTree grammar file with a suffix .jj.
      */
     public void setOutputfile(String outputFile) {
         this.outputFile = outputFile;
     }
 
     /**
      * The jjtree grammar file to process.
      */
     public void setTarget(File target) {
         this.target = target;
     }
 
     /**
      * The directory containing the JavaCC distribution.
      */
     public void setJavacchome(File javaccHome) {
         this.javaccHome = javaccHome;
     }
 
     public JJTree() {
         cmdl.setVm(JavaEnvUtils.getJreExecutable("java"));
     }
 
     public void execute() throws BuildException {
 
         // load command line with optional attributes
         Enumeration iter = optionalAttrs.keys();
         while (iter.hasMoreElements()) {
             String name  = (String) iter.nextElement();
             Object value = optionalAttrs.get(name);
             cmdl.createArgument().setValue("-" + name + ":" + value.toString());
         }
 
         if (target == null || !target.isFile()) {
             throw new BuildException("Invalid target: " + target);
         }
 
         File javaFile = null;
 
         // use the directory containing the target as the output directory
         if (outputDirectory == null) {
             // convert backslashes to slashes, otherwise jjtree will
             // put this as comments and this seems to confuse javacc
             cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:"
                                            + getDefaultOutputDirectory());
 
             javaFile = new File(createOutputFileName(target, outputFile,
                                                      null));
         } else {
             if (!outputDirectory.isDirectory()) {
                 throw new BuildException("'outputdirectory' " + outputDirectory
                                          + " is not a directory.");
             }
 
             // convert backslashes to slashes, otherwise jjtree will
             // put this as comments and this seems to confuse javacc
             cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:"
                                            + outputDirectory.getAbsolutePath()
                                              .replace('\\', '/'));
 
             javaFile = new File(createOutputFileName(target, outputFile,
                                                      outputDirectory
                                                      .getPath()));
         }
 
         if (javaFile.exists()
             && target.lastModified() < javaFile.lastModified()) {
             log("Target is already built - skipping (" + target + ")",
                 Project.MSG_VERBOSE);
             return;
         }
 
         if (outputFile != null) {
             cmdl.createArgument().setValue("-" + OUTPUT_FILE + ":"
                                            + outputFile.replace('\\', '/'));
         }
 
         cmdl.createArgument().setValue(target.getAbsolutePath());
 
         cmdl.setClassname(JavaCC.getMainClass(javaccHome,
                                               JavaCC.TASKDEF_TYPE_JJTREE));
 
         final Path classpath = cmdl.createClasspath(getProject());
         final File javaccJar = JavaCC.getArchiveFile(javaccHome);
         classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
         classpath.addJavaRuntime();
 
         final Commandline.Argument arg = cmdl.createVmArgument();
         arg.setValue("-mx140M");
         arg.setValue("-Dinstall.root=" + javaccHome.getAbsolutePath());
 
         final Execute process =
             new Execute(new LogStreamHandler(this,
                                              Project.MSG_INFO,
                                              Project.MSG_INFO),
                         null);
         log(cmdl.describeCommand(), Project.MSG_VERBOSE);
         process.setCommandline(cmdl.getCommandline());
 
         try {
             if (process.execute() != 0) {
                 throw new BuildException("JJTree failed.");
             }
         } catch (IOException e) {
             throw new BuildException("Failed to launch JJTree", e);
         }
     }
 
     private String createOutputFileName(File target, String optionalOutputFile,
                                         String outputDirectory) {
         optionalOutputFile = validateOutputFile(optionalOutputFile,
                                                 outputDirectory);
         String jjtreeFile = target.getAbsolutePath().replace('\\','/');
 
         if ((optionalOutputFile == null) || optionalOutputFile.equals("")) {
             int filePos = jjtreeFile.lastIndexOf("/");
 
             if (filePos >= 0) {
                 jjtreeFile = jjtreeFile.substring(filePos + 1);
             }
 
             int suffixPos = jjtreeFile.lastIndexOf('.');
 
             if (suffixPos == -1) {
                 optionalOutputFile = jjtreeFile + DEFAULT_SUFFIX;
             } else {
                 String currentSuffix = jjtreeFile.substring(suffixPos);
 
                 if (currentSuffix.equals(DEFAULT_SUFFIX)) {
                     optionalOutputFile = jjtreeFile + DEFAULT_SUFFIX;
                 } else {
                     optionalOutputFile = jjtreeFile.substring(0, suffixPos)
                         + DEFAULT_SUFFIX;
                 }
             }
         }
 
         if ((outputDirectory == null) || outputDirectory.equals("")) {
             outputDirectory = getDefaultOutputDirectory();
         }
 
         return (outputDirectory + "/" + optionalOutputFile).replace('\\', '/');
     }
 
+ /*   
+  * Not used anymore
     private boolean isAbsolute(String fileName) {
         return (fileName.startsWith("/") || (new File(fileName).isAbsolute()));
     }
-
+*/
     /**
      * When running JJTree from an Ant taskdesk the -OUTPUT_DIRECTORY must
      * always be set. But when -OUTPUT_DIRECTORY is set, -OUTPUT_FILE is
      * handled as if relative of this -OUTPUT_DIRECTORY. Thus when the
      * -OUTPUT_FILE is absolute or contains a drive letter we have a problem.
      *
      * @param outputFile
      * @param outputDirectory
      * @return
      * @throws BuildException
      */
     private String validateOutputFile(String outputFile, 
                                       String outputDirectory) 
         throws BuildException {
         if (outputFile == null) {
             return null;
         }
 
         if ((outputDirectory == null)
             && (outputFile.startsWith("/") || outputFile.startsWith("\\"))) {
             String relativeOutputFile = makeOutputFileRelative(outputFile);
             setOutputfile(relativeOutputFile);
 
             return relativeOutputFile;
         }
 
         String root = getRoot(new File(outputFile)).getAbsolutePath();
 
         if ((root.length() > 1)
             && outputFile.startsWith(root.substring(0, root.length() - 1))) {
             throw new BuildException("Drive letter in 'outputfile' not "
                                      + "supported: " + outputFile);
         }
 
         return outputFile;
     }
 
     private String makeOutputFileRelative(String outputFile) {
         StringBuffer relativePath = new StringBuffer();
         String defaultOutputDirectory = getDefaultOutputDirectory();
         int nextPos = defaultOutputDirectory.indexOf('/');
         int startPos = nextPos + 1;
 
         while (startPos > -1 && startPos < defaultOutputDirectory.length()) {
             relativePath.append("/..");
             nextPos = defaultOutputDirectory.indexOf('/', startPos);
 
             if (nextPos == -1) {
                 startPos = nextPos;
             } else {
                 startPos = nextPos + 1;
             }
         }
 
         relativePath.append(outputFile);
 
         return relativePath.toString();
     }
 
     private String getDefaultOutputDirectory() {
         return getProject().getBaseDir().getAbsolutePath().replace('\\', '/');
     }
 
     /**
      * Determine root directory for a given file.
      *
      * @param file
      * @return file's root directory
      */
     private File getRoot(File file) {
         File root = file.getAbsoluteFile();
 
         while (root.getParent() != null) {
             root = root.getParentFile();
         }
 
         return root;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/splash/SplashTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/splash/SplashTask.java
index c4c35c3ce..21a65e860 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/splash/SplashTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/splash/SplashTask.java
@@ -1,245 +1,245 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.taskdefs.optional.splash;
 
 import java.io.ByteArrayOutputStream;
 import java.io.DataInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.net.URLConnection;
 import javax.swing.ImageIcon;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 
 /**
  * Creates a splash screen. The splash screen is displayed
  * for the duration of the build and includes a handy progress bar as
  * well. Use in conjunction with the sound task to provide interest
  * whilst waiting for your builds to complete...
  * @since Ant1.5
  * @author Les Hughes (leslie.hughes@rubus.com)
  */
 public class SplashTask extends Task {
 
     private String imgurl = null;
     private String proxy = null;
     private String user = null;
     private String password = null;
     private String port = "80";
     private int showDuration = 5000;
     private boolean useProxy = false;
 
     private static SplashScreen splash = null;
 
     /**
      * A URL pointing to an image to display; optional, default antlogo.gif
      * from the classpath.
      */
     public void setImageURL(String imgurl) {
         this.imgurl = imgurl;
     }
 
     /**
      * flag to enable proxy settings; optional, deprecated : consider
      * using &lt;setproxy&gt; instead
      * @deprecated use org.apache.tools.ant.taskdefs.optional.SetProxy
      */
     public void setUseproxy(boolean useProxy) {
         this.useProxy = useProxy;
     }
 
     /**
      * name of proxy; optional.
      */
     public void setProxy(String proxy) {
         this.proxy = proxy;
     }
 
     /**
      * Proxy port; optional, default 80.
      */
     public void setPort(String port) {
         this.port = port;
     }
 
     /**
      * Proxy user; optional, default =none.
      */
     public void setUser(String user) {
         this.user = user;
     }
 
     /**
      * Proxy password; required if <tt>user</tt> is set.
      */
      public void setPassword(String password) {
         this.password = password;
     }
 
     /**
      * how long to show the splash screen in milliseconds,
      * optional; default 5000 ms.
      */
     public void setShowduration(int duration) {
         this.showDuration = duration;
     }
 
 
     public void execute() throws BuildException {
         if (splash != null) {
             splash.setVisible(false);
             getProject().removeBuildListener(splash);
             splash.dispose();
             splash = null;
         }
 
         log("Creating new SplashScreen", Project.MSG_VERBOSE);
         InputStream in = null;
 
         if (imgurl != null) {
             try {
                 URLConnection conn = null;
 
                 if (useProxy && (proxy != null && proxy.length() > 0)
                     && (port != null && port.length() > 0)) {
 
                     log("Using proxied Connection",  Project.MSG_DEBUG);
                     System.getProperties().put("http.proxySet", "true");
                     System.getProperties().put("http.proxyHost", proxy);
                     System.getProperties().put("http.proxyPort", port);
 
                     URL url = new URL(imgurl);
 
                     conn = url.openConnection();
                     if (user != null && user.length() > 0) {
                         String encodedcreds =
                             new sun.misc.BASE64Encoder().encode((new String(user + ":" + password)).getBytes());
                         conn.setRequestProperty("Proxy-Authorization",
                                                 encodedcreds);
                     }
 
                 } else {
                     System.getProperties().put("http.proxySet", "false");
                     System.getProperties().put("http.proxyHost", "");
                     System.getProperties().put("http.proxyPort", "");
                     log("Using Direction HTTP Connection", Project.MSG_DEBUG);
                     URL url = new URL(imgurl);
                     conn = url.openConnection();
                 }
                 conn.setDoInput(true);
                 conn.setDoOutput(false);
 
                 in = conn.getInputStream();
 
                 // Catch everything - some of the above return nulls,
                 // throw exceptions or generally misbehave
                 // in the event of a problem etc
 
             } catch (Throwable ioe) {
                 log("Unable to download image, trying default Ant Logo",
                     Project.MSG_DEBUG);
                 log("(Exception was \"" + ioe.getMessage() + "\"",
                     Project.MSG_DEBUG);
             }
         }
 
         if (in == null) {
             in = SplashTask.class.getClassLoader().getResourceAsStream("images/ant_logo_large.gif");
         }
 
         if (in != null) {
             DataInputStream din = new DataInputStream(in);
             boolean success = false;
             try {
                 ByteArrayOutputStream bout = new ByteArrayOutputStream();
                 int data;
                 while ((data = din.read()) != -1) {
                     bout.write((byte) data);
                 }
 
                 log("Got ByteArray, creating splash",  Project.MSG_DEBUG);
                 ImageIcon img = new ImageIcon(bout.toByteArray());
 
                 splash = new SplashScreen(img);
                 success = true;
             } catch (Exception e) {
                 throw new BuildException(e);
             } finally {
                 try {
                     din.close();
                 } catch (IOException ioe) {
                     // swallow if there was an error before so that
                     // original error will be passed up
                     if (success) {
                         throw new BuildException(ioe);
                     }
                 }
             }
         } else {
             splash = new SplashScreen("Image Unavailable.");
         }
 
         splash.setVisible(true);
         splash.toFront();
         getProject().addBuildListener(splash);
         try {
-            Thread.currentThread().sleep(showDuration);
+            Thread.sleep(showDuration);
         } catch (InterruptedException e) {
         }
 
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/unix/AbstractAccessTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/unix/AbstractAccessTask.java
index 48465f527..3542c5281 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/unix/AbstractAccessTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/unix/AbstractAccessTask.java
@@ -1,144 +1,144 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 /*
  * Since the initial version of this file was deveolped on the clock on
  * an NSF grant I should say the following boilerplate:
  *
  * This material is based upon work supported by the National Science
  * Foundaton under Grant No. EIA-0196404. Any opinions, findings, and
  * conclusions or recommendations expressed in this material are those
  * of the author and do not necessarily reflect the views of the
  * National Science Foundation.
  */
 
 package org.apache.tools.ant.taskdefs.optional.unix;
 
 import java.io.File;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.FileSet;
 
 /**
  * @author Patrick G. Heck
  *         <a href="mailto:gus.heck@olin.edu">gus.heck@olin.edu</a>
  * @since Ant 1.6
  *
  * @ant.task category="filesystem"
  */
 
 public abstract class AbstractAccessTask
     extends org.apache.tools.ant.taskdefs.ExecuteOn {
 
     /**
      * Chmod task for setting file and directory permissions.
      */
     public AbstractAccessTask() {
         super.setParallel(true);
         super.setSkipEmptyFilesets(true);
     }
 
     /**
      * Set the file which should have its access attributes modified.
      */
     public void setFile(File src) {
         FileSet fs = new FileSet();
         fs.setFile(src);
         addFileset(fs);
     }
 
     /**
      * Prevent the user from specifying a different command.
      *
      * @ant.attribute ignore="true"
      * @param cmdl A user supplied command line that we won't accept.
      */
     public void setCommand(Commandline cmdl) {
-        throw new BuildException(taskType
+        throw new BuildException(getTaskType()
                                  + " doesn\'t support the command attribute",
                                  getLocation());
     }
 
     /**
      * Prevent the skipping of empty filesets
      *
      * @ant.attribute ignore="true"
      * @param skip A user supplied boolean we won't accept.
      */
     public void setSkipEmptyFilesets(boolean skip) {
-        throw new BuildException(taskType + " doesn\'t support the "
+        throw new BuildException(getTaskType() + " doesn\'t support the "
                                  + "skipemptyfileset attribute",
                                  getLocation());
     }
 
     /**
      * Prevent the use of the addsourcefile atribute.
      *
      * @ant.attribute ignore="true"
      * @param b A user supplied boolean we won't accept.
      */
     public void setAddsourcefile(boolean b) {
         throw new BuildException(getTaskType()
             + " doesn\'t support the addsourcefile attribute", getLocation());
     }
 
     /**
      * Automatically approve Unix OS's.
      */
     protected boolean isValidOs() {
         return Os.isFamily("unix") && super.isValidOs();
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chgrp.java b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chgrp.java
index e427c1581..a2f39513d 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chgrp.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chgrp.java
@@ -1,123 +1,123 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 /*
  * Since the initial version of this file was deveolped on the clock on
  * an NSF grant I should say the following boilerplate:
  *
  * This material is based upon work supported by the National Science
  * Foundaton under Grant No. EIA-0196404. Any opinions, findings, and
  * conclusions or recommendations expressed in this material are those
  * of the author and do not necessarily reflect the views of the
  * National Science Foundation.
  */
 
 package org.apache.tools.ant.taskdefs.optional.unix;
 
 import org.apache.tools.ant.BuildException;
 
 /**
  * Chgrp equivalent for unix-like environments.
  *
  * @author Patrick G. Heck
  *         <a href="mailto:gus.heck@olin.edu">gus.heck@olin.edu</a>
  *
  * @since Ant 1.6
  *
  * @ant.task category="filesystem"
  */
 public class Chgrp extends AbstractAccessTask {
 
     private boolean haveGroup = false;
 
     /**
      * Chgrp task for setting unix group of a file.
      */
     public Chgrp() {
         super.setExecutable("chgrp");
     }
 
     /**
      * Set the group atribute.
      *
      * @param group    The new group for the file(s) or directory(ies)
      */
     public void setGroup(String group) {
         createArg().setValue(group);
         haveGroup = true;
     }
 
     /**
      * Ensure that all the required arguments and other conditions have
      * been set.
      */
     protected void checkConfiguration() {
         if (!haveGroup) {
             throw new BuildException("Required attribute group not set in "
                                      + "chgrp", getLocation());
         }
         super.checkConfiguration();
     }
 
     /**
      * We don't want to expose the executable atribute, so overide it.
      *
      * @param e User supplied executable that we won't accept.
      */
     public void setExecutable(String e) {
-        throw new BuildException(taskType
+        throw new BuildException(getTaskType()
                                  + " doesn\'t support the executable"
                                  + " attribute", getLocation());
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chown.java b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chown.java
index 075dda31f..29df13883 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chown.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Chown.java
@@ -1,123 +1,123 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 /*
  * Since the initial version of this file was deveolped on the clock on
  * an NSF grant I should say the following boilerplate:
  *
  * This material is based upon work supported by the National Science
  * Foundaton under Grant No. EIA-0196404. Any opinions, findings, and
  * conclusions or recommendations expressed in this material are those
  * of the author and do not necessarily reflect the views of the
  * National Science Foundation.
  */
 
 package org.apache.tools.ant.taskdefs.optional.unix;
 
 import org.apache.tools.ant.BuildException;
 
 /**
  * Chown equivalent for unix-like environments.
  *
  * @author Patrick G. Heck
  *         <a href="mailto:gus.heck@olin.edu">gus.heck@olin.edu</a>
  *
  * @since Ant 1.6
  *
  * @ant.task category="filesystem"
  */
 public class Chown extends AbstractAccessTask {
 
     private boolean haveOwner = false;
 
     /**
      * Chown task for setting file and directory permissions.
      */
     public Chown() {
         super.setExecutable("chown");
     }
 
     /**
      * Set the owner atribute.
      *
      * @param owner    The new owner for the file(s) or directory(ies)
      */
     public void setOwner(String owner) {
         createArg().setValue(owner);
         haveOwner = true;
     }
 
     /**
      * Ensure that all the required arguments and other conditions have
      * been set.
      */
     protected void checkConfiguration() {
         if (!haveOwner) {
             throw new BuildException("Required attribute owner not set in"
                                      + " chown", getLocation());
         }
         super.checkConfiguration();
     }
 
     /**
      * We don't want to expose the executable atribute, so overide it.
      *
      * @param e User supplied executable that we won't accept.
      */
     public void setExecutable(String e) {
-        throw new BuildException(taskType
+        throw new BuildException(getTaskType()
                                  + " doesn\'t support the executable"
                                  + " attribute", getLocation());
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Symlink.java b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Symlink.java
index 81737f06f..b1e341e18 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/unix/Symlink.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/unix/Symlink.java
@@ -1,811 +1,788 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 /*
  * Since the initial version of this file was deveolped on the clock on
  * an NSF grant I should say the following boilerplate:
  *
  * This material is based upon work supported by the National Science
  * Foundaton under Grant No. EIA-0196404. Any opinions, findings, and
  * conclusions or recommendations expressed in this material are those
  * of the author and do not necessarily reflect the views of the
  * National Science Foundation.
  */
 
 package org.apache.tools.ant.taskdefs.optional.unix;
 
 import java.io.File;
 import java.io.IOException;
-import java.io.OutputStream;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
-import java.io.BufferedOutputStream;
 import java.io.FileNotFoundException;
 
 import java.util.Vector;
 import java.util.Properties;
 import java.util.Enumeration;
 import java.util.Hashtable;
 
-import java.lang.reflect.InvocationTargetException;
-import java.lang.reflect.Method;
-
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 
 import org.apache.tools.ant.util.FileUtils;
 
 import org.apache.tools.ant.types.FileSet;
 
 import org.apache.tools.ant.taskdefs.Execute;
 
 /**
  * Creates, Records and Restores Symlinks.
  *
  * <p> This task performs several related operations. In the most trivial,
  * and default usage, it creates a link specified in the link atribute to
  * a resource specified in the resource atribute. The second usage of this
  * task is to traverses a directory structure specified by a fileset,
  * and write a properties file in each included directory describing the
  * links found in that directory. The third usage is to traverse a
  * directory structure specified by a fileset, looking for properties files
  * (also specified as included in the fileset) and recreate the links
  * that have been previously recorded for each directory. Finally, it can be
  * used to remove a symlink without deleting the file or directory it points
  * to.
  *
  * <p> Examples of use:
  *
  * <p> Make a link named "foo" to a resource named "bar.foo" in subdir:
  * <pre>
  * &lt;symlink link="${dir.top}/foo" resource="${dir.top}/subdir/bar.foo"/&gt;
  * </pre>
  *
  * <p> Record all links in subdir and it's descendants in files named
  * "dir.links"
  * <pre>
  * &lt;symlink action="record" linkfilename="dir.links"&gt;
  *    &lt;fileset dir="${dir.top}" includes="subdir&#47;**" /&gt;
  * &lt;/symlink&gt;
  * </pre>
  *
  * <p> Recreate the links recorded in the previous example:
  * <pre>
  * &lt;symlink action="recreate"&gt;
  *    &lt;fileset dir="${dir.top}" includes="subdir&#47;**&#47;dir.links" /&gt;
  * &lt;/symlink&gt;
  * </pre>
  *
  * <p> Delete a link named "foo" to a resource named "bar.foo" in subdir:
  * <pre>
  * &lt;symlink action="delete" link="${dir.top}/foo"/&gt;
  * </pre>
  *
  * <p><strong>LIMITATIONS:</strong> Because Java has no direct support for
  * handling symlinks this task divines them by comparing canoniacal and
  * absolute paths. On non-unix systems this may cause false positives.
  * Furthermore, any operating system on which the command
  * <code>ln -s link resource</code> is not a valid command on the comandline
  * will not be able to use action= "delete", action="single" or
  * action="recreate", but action="record" should still work. Finally, the
  * lack of support for symlinks in Java means that all links are recorded
  * as links to the <strong>canonical</strong> resource name. Therefore
  * the link: <code>link --> subdir/dir/../foo.bar</code> will be recorded
  * as <code>link=subdir/foo.bar</code> and restored as
  * <code>link --> subdir/foo.bar</code>
  *
  * @version $Revision$
  * @author  <a href="mailto:gus.heck@olin.edu">Patrick G. Heck</a>
  */
 
 public class Symlink extends Task {
 
     // Atributes with setter methods
     private String resource;
     private String link;
     private String action;
     private Vector fileSets = new Vector();
     private String linkFileName;
     private boolean overwrite;
     private boolean failonerror;
 
     /** Initialize the task. */
 
     public void init() throws BuildException {
         super.init();
         failonerror = true;   // default behavior is to fail on an error
         overwrite = false;    // devault behavior is to not overwrite
         action = "single";      // default behavior is make a single link
         fileSets = new Vector();
     }
 
     /** The standard method for executing any task. */
 
     public void execute() throws BuildException {
         try {
             if (action.equals("single")) {
                 doLink(resource, link);
             } else if (action.equals("delete")) {
                 try {
                     log("Removing symlink: " + link);
                     Symlink.deleteSymlink(link);
                 } catch (FileNotFoundException fnfe) {
                     handleError(fnfe.toString());
                 } catch (IOException ioe) {
                     handleError(ioe.toString());
                 }
             } else if (action.equals("recreate")) {
                 Properties listOfLinks;
                 Enumeration keys;
 
                 if (fileSets.size() == 0) {
                     handleError("File set identifying link file(s) "
                                 + "required for action recreate");
                     return;
                 }
 
                 listOfLinks = loadLinks(fileSets);
 
                 keys = listOfLinks.keys();
 
                 while (keys.hasMoreElements()) {
                     link = (String) keys.nextElement();
                     resource = listOfLinks.getProperty(link);
                     doLink(resource, link);
                 }
             } else if (action.equals("record")) {
                 Vector vectOfLinks;
                 Hashtable byDir = new Hashtable();
                 Enumeration links, dirs;
 
                 if (fileSets.size() == 0) {
                     handleError("File set identifying links to "
                                 + "record required");
                     return;
                 }
 
                 if (linkFileName == null) {
                     handleError("Name of file to record links in "
                                 + "required");
                     return;
                 }
 
                 // fill our vector with file objects representing
                 // links (canonical)
                 vectOfLinks = findLinks(fileSets);
 
                 // create a hashtable to group them by parent directory
                 links = vectOfLinks.elements();
                 while (links.hasMoreElements()) {
                     File thisLink = (File) links.nextElement();
                     String parent = thisLink.getParent();
                     if (byDir.containsKey(parent)) {
                         ((Vector) byDir.get(parent)).addElement(thisLink);
                     } else {
                         byDir.put(parent, new Vector());
                         ((Vector) byDir.get(parent)).addElement(thisLink);
                     }
                 }
 
                 // write a Properties file in each directory
                 dirs = byDir.keys();
                 while (dirs.hasMoreElements()) {
                     String dir = (String) dirs.nextElement();
                     Vector linksInDir = (Vector) byDir.get(dir);
                     Properties linksToStore = new Properties();
                     Enumeration eachlink = linksInDir.elements();
                     File writeTo;
 
                     // fill up a Properties object with link and resource
                     // names
                     while (eachlink.hasMoreElements()) {
                         File alink = (File) eachlink.nextElement();
                         try {
                             linksToStore.put(alink.getName(),
                                              alink.getCanonicalPath());
                         } catch (IOException ioe) {
                             handleError("Couldn't get canonical "
                                         + "name of a parent link");
                         }
                     }
 
 
                     // Get a place to record what we are about to write
                     writeTo = new File(dir + File.separator
                                        + linkFileName);
 
                     writePropertyFile(linksToStore, writeTo,
                                       "Symlinks from " + writeTo.getParent());
                 }
             } else {
                 handleError("Invalid action specified in symlink");
             }
         } finally {
             // return all variables to their default state,
             // ready for the next invocation.
             resource = null;
             link = null;
             action = "single";
             fileSets = new Vector();
             linkFileName = null;
             overwrite = false;
             failonerror = true;
         }
     }
 
     /* ********************************************************** *
       *              Begin Atribute Setter Methods               *
      * ********************************************************** */
 
     /**
      * The setter for the overwrite atribute. If set to false (default)
      * the task will not overwrite existing links, and may stop the build
      * if a link already exists depending on the setting of failonerror.
      *
      * @param owrite If true overwrite existing links.
      */
     public void setOverwrite(boolean owrite) {
         this.overwrite = owrite;
     }
 
     /**
      * The setter for the failonerror atribute. If set to true (default)
      * the entire build fails upon error. If set to false, the error is
      * logged and the build will continue.
      *
      * @param foe    If true throw build exception on error else log it.
      */
     public void setFailOnError(boolean foe) {
         this.failonerror = foe;
     }
 
 
     /**
      * The setter for the "action" attribute. May be "single" "multi"
      * or "record"
      *
      * @param typ    The action of action to perform
      */
     public void setAction(String typ) {
         this.action = typ;
     }
 
     /**
      * The setter for the "link" attribute. Only used for action = single.
      *
      * @param lnk     The name for the link
      */
     public void setLink(String lnk) {
         this.link = lnk;
     }
 
     /**
      * The setter for the "resource" attribute. Only used for action = single.
      *
      * @param src      The source of the resource to be linked.
      */
     public void setResource(String src) {
         this.resource = src;
     }
 
     /**
      * The setter for the "linkfilename" attribute. Only used for action=record.
      *
      * @param lf      The name of the file to write links to.
      */
     public void setLinkfilename(String lf) {
         this.linkFileName = lf;
     }
 
     /**
      * Adds a fileset to this task.
      *
      * @param set      The fileset to add.
      */
     public void addFileset(FileSet set) {
         fileSets.addElement(set);
     }
 
     /* ********************************************************** *
       *               Begin Public Utility Methods               *
      * ********************************************************** */
 
     /**
      * Deletes a symlink without deleteing the resource it points to.
      *
      * <p>This is a convenience method that simply invokes
      * <code>deleteSymlink(java.io.File)</code>
      *
      * @param path    A string containing the path of the symlink to delete
      *
      * @throws FileNotFoundException   When the path results in a
      *                                   <code>File</code> that doesn't exist.
      * @throws IOException             If calls to <code>File.rename</code>
      *                                   or <code>File.delete</code> fail.
      */
 
     public static void deleteSymlink(String path)
         throws IOException, FileNotFoundException {
 
         File linkfil = new File(path);
         deleteSymlink(linkfil);
     }
 
     /**
      * Deletes a symlink without deleteing the resource it points to.
      *
      * <p>This is a utility method that removes a unix symlink without removing
      * the resource that the symlink points to. If it is accidentally invoked
      * on a real file, the real file will not be harmed, but an exception
      * will be thrown when the deletion is attempted. This method works by
      * getting the canonical path of the link, using the canonical path to
      * rename the resource (breaking the link) and then deleting the link.
      * The resource is then returned to it's original name inside a finally
      * block to ensure that the resource is unharmed even in the event of
      * an exception.
      *
      * @param linkfil    A <code>File</code> object of the symlink to delete
      *
      * @throws FileNotFoundException   When the path results in a
      *                                   <code>File</code> that doesn't exist.
      * @throws IOException             If calls to <code>File.rename</code>,
      *                                   <code>File.delete</code> or
      *                                   <code>File.getCanonicalPath</code>
      *                                   fail.
      */
 
     public static void deleteSymlink(File linkfil)
         throws IOException, FileNotFoundException {
 
         if (!linkfil.exists()) {
             throw new FileNotFoundException("No such symlink: " + linkfil);
         }
 
         // find the resource of the existing link
         String canstr = linkfil.getCanonicalPath();
         File canfil = new File(canstr);
 
         // rename the resource, thus breaking the link
         String parentStr = canfil.getParent();
         File parentDir = new File(parentStr);
         FileUtils fu = FileUtils.newFileUtils();
         File temp = fu.createTempFile("symlink", ".tmp", parentDir);
         try {
             try {
                 fu.rename(canfil, temp);
             } catch (IOException e) {
                 throw new IOException("Couldn't rename resource when "
                                       + "attempting to delete " + linkfil);
             }
 
             // delete the (now) broken link
             if (!linkfil.delete()) {
                 throw new IOException("Couldn't delete symlink: " + linkfil
                                       + " (was it a real file? is this not a "
                                       + "UNIX system?)");
             }
         } finally {
             // return the resource to its original name.
             try {
                 fu.rename(temp, canfil);
             } catch (IOException e) {
                 throw new IOException("Couldn't return resource " + temp
                                       + " to its original name: " + canstr
                                       + "\n THE RESOURCE'S NAME ON DISK HAS "
                                       + "BEEN CHANGED BY THIS ERROR!\n");
             }
         }
     }
 
 
     /* ********************************************************** *
       *                  Begin Private Methods                   *
      * ********************************************************** */
 
     /**
      * Writes a properties file.
      *
-     * In jdk 1.2+ this method will use <code>Properties.store</code>
+     * This method use <code>Properties.store</code>
      * and thus report exceptions that occur while writing the file.
-     * In jdk 1.1 we are forced to use <code>Properties.save</code>
-     * and therefore all exceptions are masked. This method was lifted
-     * directly from the Proertyfile task with only slight editing.
-     * sticking something like this in FileUtils might  be
-     * a good idea to avoid duplication.
+     * 
+     * This is not jdk 1.1 compatible, but ant 1.6 is not anymore.
      *
      * @param properties     The properties object to be written.
      * @param propertyfile   The File to write to.
      * @param comment        The comment to place at the head of the file.
      */
 
     private void writePropertyFile(Properties properties,
                                    File propertyfile,
                                    String comment)
         throws BuildException {
 
-        BufferedOutputStream bos = null;
+        FileOutputStream fos = null;
         try {
-            bos = new BufferedOutputStream(new FileOutputStream(propertyfile));
-
-            // Properties.store is not available in JDK 1.1
-            Method m =
-                Properties.class.getMethod("store",
-                                           new Class[] {
-                                               OutputStream.class,
-                                               String.class});
-            m.invoke(properties, new Object[] {bos, comment});
-
-        } catch (NoSuchMethodException nsme) {
-            properties.save(bos, comment);
-        } catch (InvocationTargetException ite) {
-            Throwable t = ite.getTargetException();
-            throw new BuildException(t, location);
-        } catch (IllegalAccessException iae) {
-            // impossible
-            throw new BuildException(iae, location);
+            fos = new FileOutputStream(propertyfile);
+            properties.store(fos, comment);
+
         } catch (IOException ioe) {
-            throw new BuildException(ioe, location);
+            throw new BuildException(ioe, getLocation());
         } finally {
-            if (bos != null) {
+            if (fos != null) {
                 try {
-                    bos.close();
+                    fos.close();
                 } catch (IOException ioex) {
                     log("Failed to close output stream");
                 }
             }
         }
     }
 
     /**
      * Handles errors correctly based on the setting of failonerror.
      *
      * @param msg    The message to log, or include in the
      *                  <code>BuildException</code>
      */
 
     private void handleError(String msg) {
         if (failonerror) {
             throw new BuildException(msg);
         } else {
             log(msg);
         }
     }
 
 
     /**
      * Conducts the actual construction of a link.
      *
      * <p> The link is constructed by calling <code>Execute.runCommand</code>.
      *
      * @param resource   The path of the resource we are linking to.
      * @param link       The name of the link we wish to make
      */
 
     private void doLink(String resource, String link) throws BuildException {
 
         if (resource == null) {
             handleError("Must define the resource to symlink to!");
             return;
         }
         if (link == null) {
             handleError("Must define the link "
                         + "name for symlink!");
             return;
         }
 
         File linkfil = new File(link);
 
         String[] cmd = new String[4];
         cmd[0] = "ln";
         cmd[1] = "-s";
         cmd[2] = resource;
         cmd[3] = link;
 
         try {
             if (overwrite && linkfil.exists()) {
                 deleteSymlink(linkfil);
             }
         } catch (FileNotFoundException fnfe) {
             handleError("Symlink dissapeared before it was deleted:" + link);
         } catch (IOException ioe) {
             handleError("Unable to overwrite preexisting link " + link);
         }
 
         log(cmd[0] + " " + cmd[1] + " " + cmd[2] + " " + cmd[3]);
         Execute.runCommand(this, cmd);
     }
 
 
     /**
      * Simultaneously get included directories and included files.
      *
      * @param ds   The scanner with which to get the files and directories.
      * @return     A vector of <code>String</code> objects containing the
      *                included file names and directory names.
      */
 
     private Vector scanDirsAndFiles(DirectoryScanner ds) {
         String[] files, dirs;
         Vector list = new Vector();
 
         ds.scan();
 
         files = ds.getIncludedFiles();
         dirs = ds.getIncludedDirectories();
 
         for (int i = 0; i < files.length; i++) {
             list.addElement(files[i]);
         }
         for (int i = 0; i < dirs.length; i++) {
             list.addElement(dirs[i]);
         }
 
         return list;
     }
 
     /**
      * Finds all the links in all supplied filesets.
      *
      * <p> This method is invoked when the action atribute is is "record".
      * This means that filesets are interpreted as the directories in
      * which links may be found.
      *
      * <p> The basic method follwed here is, for each file set:
      *   <ol>
      *      <li> Compile a list of all matches </li>
      *      <li> Convert matches to <code>File</code> objects </li>
      *      <li> Remove all non-symlinks using
      *             <code>FileUtils.isSymbolicLink</code> </li>
      *      <li> Convert all parent directories to the canonical form </li>
      *      <li> Add the remaining links from each file set to a
      *             master list of links unless the link is already recorded
      *             in the list</li>
      *   </ol>
      *
      * @param fileSets   The filesets specified by the user.
      * @return           A vector of <code>File</code> objects containing the
      *                     links (with canonical parent directories)
      */
 
     private Vector findLinks(Vector fileSets) {
         Vector result = new Vector();
 
         // loop through the supplied file sets
         FSLoop: for (int i = 0; i < fileSets.size(); i++) {
             FileSet fsTemp = (FileSet) fileSets.elementAt(i);
             String workingDir = null;
             Vector links = new Vector();
             Vector linksFiles = new Vector();
             Enumeration enumLinks;
 
             DirectoryScanner ds;
 
             File tmpfil = null;
             try {
                 tmpfil = fsTemp.getDir(this.getProject());
                 workingDir = tmpfil.getCanonicalPath();
             } catch (IOException ioe) {
                 handleError("Exception caught getting "
                             + "canonical path of working dir " + tmpfil
                             + " in a FileSet passed to the symlink "
                             + "task. Further processing of this "
                             + "fileset skipped");
                 continue FSLoop;
             }
 
             // Get a vector of String with file names that match
             // the pattern
             ds = fsTemp.getDirectoryScanner(this.getProject());
             links = scanDirsAndFiles(ds);
 
             // Now convert the strings to File Objects
             // using the canonical version of the working dir
             enumLinks = links.elements();
 
             while (enumLinks.hasMoreElements()) {
                 linksFiles.addElement(new File(workingDir
                                                + File.separator
                                                + (String) enumLinks
                                                .nextElement()));
             }
 
             // Now loop through and remove the non-links
 
             enumLinks = linksFiles.elements();
 
             File parentNext, next;
             String nameParentNext;
             FileUtils fu = FileUtils.newFileUtils();
             Vector removals = new Vector();
             while (enumLinks.hasMoreElements()) {
                 next = (File) enumLinks.nextElement();
                 nameParentNext = next.getParent();
 
                 parentNext = new File(nameParentNext);
                 try {
                     if (!fu.isSymbolicLink(parentNext, next.getName())) {
                         removals.addElement(next);
                     }
                 } catch (IOException ioe) {
                     handleError("Failed checking " + next
                                 + " for symbolic link. FileSet skipped.");
                     continue FSLoop;
                     // Otherwise this file will be falsely recorded as a link,
                     // if failonerror = false, hence the warn and skip.
                 }
             }
 
             enumLinks = removals.elements();
 
             while (enumLinks.hasMoreElements()) {
                 linksFiles.removeElement(enumLinks.nextElement());
             }
 
             // Now we have what we want so add it to results, ensuring that
             // no link is returned twice and we have a canonical reference
             // to the link. (no symlinks in the parent dir)
 
             enumLinks = linksFiles.elements();
             while (enumLinks.hasMoreElements()) {
                 File temp, parent;
                 next = (File) enumLinks.nextElement();
                 try {
                     parent = new File(next.getParent());
                     parent = new File(parent.getCanonicalPath());
                     temp = new File(parent, next.getName());
                     if (!result.contains(temp)) {
                         result.addElement(temp);
                     }
                 } catch (IOException ioe) {
                     handleError("IOException: " + next + " omitted");
                 }
             }
 
             // Note that these links are now specified with a full
             // canonical path irrespective of the working dir of the
             // file set so it is ok to mix them in the same vector.
 
         }
         return result;
     }
 
     /**
      * Load the links from a properties file.
      *
      * <p> This method is only invoked when the action atribute is set to
      * "multi". The filesets passed in are assumed to specify the names
      * of the property files with the link information and the
      * subdirectories in which to look for them.
      *
      * <p> The basic method follwed here is, for each file set:
      *   <ol>
      *      <li> Get the canonical version of the dir atribute </li>
      *      <li> Scan for properties files </li>
      *      <li> load the contents of each properties file found. </li>
      *   </ol>
      *
      * @param fileSets    The <code>FileSet</code>s for this task
      * @return            The links to be made.
      */
 
     private Properties loadLinks(Vector fileSets) {
         Properties finalList = new Properties();
         Enumeration keys;
         String key, value;
         String[] includedFiles;
 
         // loop through the supplied file sets
         FSLoop: for (int i = 0; i < fileSets.size(); i++) {
             String workingDir;
             FileSet fsTemp = (FileSet) fileSets.elementAt(i);
 
             DirectoryScanner ds;
 
             try {
                 File linelength = fsTemp.getDir(this.getProject());
                 workingDir = linelength.getCanonicalPath();
             } catch (IOException ioe) {
                 handleError("Exception caught getting "
                             + "canonical path of working dir "
                             + "of a FileSet passed to symlink "
                             + "task. FileSet skipped.");
                 continue FSLoop;
             }
 
             ds = fsTemp.getDirectoryScanner(this.getProject());
             ds.setFollowSymlinks(false);
             ds.scan();
             includedFiles = ds.getIncludedFiles();
 
             // loop through the files identified by each file set
             // and load their contents.
             for (int j = 0; j < includedFiles.length; j++) {
                 File inc = new File(workingDir + File.separator
                                     + includedFiles[j]);
                 String inDir;
                 Properties propTemp = new Properties();
 
                 try {
                     propTemp.load(new FileInputStream(inc));
                     inDir = inc.getParent();
                     inDir = (new File(inDir)).getCanonicalPath();
                 } catch (FileNotFoundException fnfe) {
                     handleError("Unable to find " + includedFiles[j]
                                 + "FileSet skipped.");
                     continue FSLoop;
                 } catch (IOException ioe) {
                     handleError("Unable to open " + includedFiles[j]
                                 + " or it's parent dir"
                                 + "FileSet skipped.");
                     continue FSLoop;
                 }
 
                 keys = propTemp.keys();
                 propTemp.list(System.out);
                 // Write the contents to our master list of links
 
                 // This method assumes that all links are defined in
                 // terms of absolute paths, or paths relative to the
                 // working directory
                 while (keys.hasMoreElements()) {
                     key = (String) keys.nextElement();
                     value = propTemp.getProperty(key);
                     finalList.put(inDir + File.separator + key, value);
                 }
             }
         }
         return finalList;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/windows/Attrib.java b/src/main/org/apache/tools/ant/taskdefs/optional/windows/Attrib.java
index 4b740633e..b1bbb3f64 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/windows/Attrib.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/windows/Attrib.java
@@ -1,191 +1,191 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.taskdefs.optional.windows;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.taskdefs.ExecuteOn;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.FileSet;
 
 import java.io.File;
 
 /**
  * Attrib equivalent for Win32 environments.
  * Note: Attrib parameters /S and /D are not handled.
  *
  * @author skanga@bigfoot.com
  * @author <a href="mailto:Jerome@jeromelacoste.com">Jerome Lacoste</a>
  *
  * @since Ant 1.6
  */
 public class Attrib extends ExecuteOn {
 
     private static final String ATTR_READONLY = "R";
     private static final String ATTR_ARCHIVE  = "A";
     private static final String ATTR_SYSTEM   = "S";
     private static final String ATTR_HIDDEN   = "H";
     private static final String SET    = "+";
     private static final String UNSET  = "-";
 
     private boolean haveAttr = false;
 
     public Attrib() {
         super.setExecutable("attrib");
         super.setParallel(false);
     }
 
     public void setFile(File src) {
         FileSet fs = new FileSet();
         fs.setFile(src);
         addFileset(fs);
     }
 
     /** set the ReadOnly file attribute */
     public void setReadonly(boolean value) {
         addArg(value, ATTR_READONLY);
     }
 
     /** set the Archive file attribute */
     public void setArchive(boolean value) {
         addArg(value, ATTR_ARCHIVE);
     }
 
     /** set the System file attribute */
     public void setSystem(boolean value) {
         addArg(value, ATTR_SYSTEM);
     }
 
     /** set the Hidden file attribute */
     public void setHidden(boolean value) {
         addArg(value, ATTR_HIDDEN);
     }
 
     protected void checkConfiguration() {
         if (!haveAttr()) {
             throw new BuildException("Missing attribute parameter",
                                      getLocation());
         }
         super.checkConfiguration();
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setExecutable(String e) {
-        throw new BuildException(taskType
+        throw new BuildException(getTaskType()
             + " doesn\'t support the executable attribute", getLocation());
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setCommand(String e) {
-        throw new BuildException(taskType
+        throw new BuildException(getTaskType()
             + " doesn\'t support the command attribute", getLocation());
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setAddsourcefile(boolean b) {
         throw new BuildException(getTaskType()
             + " doesn\'t support the addsourcefile attribute", getLocation());
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setSkipEmptyFilesets(boolean skip) {
-        throw new BuildException(taskType + " doesn\'t support the "
+        throw new BuildException(getTaskType() + " doesn\'t support the "
                                  + "skipemptyfileset attribute",
                                  getLocation());
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setParallel(boolean parallel) {
         throw new BuildException(getTaskType()
                                  + " doesn\'t support the parallel attribute",
                                  getLocation());
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setMaxParallel(int max) {
         throw new BuildException(getTaskType()
                                  + " doesn\'t support the maxparallel attribute",
                                  getLocation());
     }
 
     protected boolean isValidOs() {
         return Os.isFamily("windows") && super.isValidOs();
     }
 
     private static String getSignString(boolean attr) {
         return (attr == true ? SET : UNSET);
     }
 
     private void addArg(boolean sign, String attribute) {
         createArg().setValue(getSignString(sign) + attribute);
         haveAttr = true;
     }
 
     private boolean haveAttr() {
         return haveAttr;
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/util/FileUtils.java b/src/main/org/apache/tools/ant/util/FileUtils.java
index 5089a5846..ea2f5c3d1 100644
--- a/src/main/org/apache/tools/ant/util/FileUtils.java
+++ b/src/main/org/apache/tools/ant/util/FileUtils.java
@@ -1,1083 +1,1082 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.util;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.Reader;
 import java.lang.reflect.Method;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.text.CharacterIterator;
 import java.text.DecimalFormat;
 import java.text.StringCharacterIterator;
 import java.util.Random;
 import java.util.Stack;
 import java.util.StringTokenizer;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.filters.util.ChainReaderHelper;
-import org.apache.tools.ant.filters.TokenFilter;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.launch.Locator;
 
 /**
  * This class also encapsulates methods which allow Files to be
  * refered to using abstract path names which are translated to native
  * system file paths at runtime as well as copying files or setting
  * there last modification time.
  *
  * @author duncan@x180.com
  * @author Conor MacNeill
  * @author Stefan Bodewig
  * @author Magesh Umasankar
  * @author <a href="mailto:jtulley@novell.com">Jeff Tulley</a>
  *
  * @version $Revision$
  */
 
 public class FileUtils {
     private static Random rand = new Random(System.currentTimeMillis());
     private static Object lockReflection = new Object();
     private static java.lang.reflect.Method setLastModified = null;
 
     private boolean onNetWare = Os.isFamily("netware");
 
     // for toURI
     private static boolean[] isSpecial = new boolean[256];
     private static char[] escapedChar1 = new char[256];
     private static char[] escapedChar2 = new char[256];
 
 
     // stolen from FilePathToURI of the Xerces-J team
     static {
         for (int i = 0; i <= 0x20; i++) {
             isSpecial[i] = true;
             escapedChar1[i] = Character.forDigit(i >> 4, 16);
             escapedChar2[i] = Character.forDigit(i & 0xf, 16);
         }
         isSpecial[0x7f] = true;
         escapedChar1[0x7f] = '7';
         escapedChar2[0x7f] = 'F';
         char[] escChs = {'<', '>', '#', '%', '"', '{', '}',
                          '|', '\\', '^', '~', '[', ']', '`'};
         int len = escChs.length;
         char ch;
         for (int i = 0; i < len; i++) {
             ch = escChs[i];
             isSpecial[ch] = true;
             escapedChar1[ch] = Character.forDigit(ch >> 4, 16);
             escapedChar2[ch] = Character.forDigit(ch & 0xf, 16);
         }
     }
 
     /**
      * Factory method.
      *
      * @return a new instance of FileUtils.
      */
     public static FileUtils newFileUtils() {
         return new FileUtils();
     }
 
     /**
      * Empty constructor.
      */
     protected FileUtils() {
     }
 
     /**
      * Get the URL for a file taking into account # characters
      *
      * @param file the file whose URL representation is required.
      * @return The FileURL value
      * @throws MalformedURLException if the URL representation cannot be
      *      formed.
      */
     public URL getFileURL(File file) throws MalformedURLException {
         return new URL(toURI(file.getAbsolutePath()));
     }
 
     /**
      * Convienence method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(String sourceFile, String destFile)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), null, false, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a destination
      * specifying if token filtering must be used.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(String sourceFile, String destFile,
                          FilterSetCollection filters)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  false, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(String sourceFile, String destFile, FilterSetCollection filters,
                          boolean overwrite) throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  overwrite, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * source files may overwrite newer destination files and the
      * last modified time of <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(String sourceFile, String destFile, FilterSetCollection filters,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  overwrite, preserveLastModified);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * source files may overwrite newer destination files and the
      * last modified time of <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param encoding the encoding used to read and write the files.
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.5
      */
     public void copyFile(String sourceFile, String destFile,
                          FilterSetCollection filters, boolean overwrite,
                          boolean preserveLastModified, String encoding)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  overwrite, preserveLastModified, encoding);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * filter chains must be used, if source files may overwrite
      * newer destination files and the last modified time of
      * <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param filterChains filterChains to apply during the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param encoding the encoding used to read and write the files.
      * @param project the project instance
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.5
      */
     public void copyFile(String sourceFile, String destFile,
                          FilterSetCollection filters, Vector filterChains,
                          boolean overwrite, boolean preserveLastModified,
                          String encoding, Project project)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  filterChains, overwrite, preserveLastModified,
                  encoding, project);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * filter chains must be used, if source files may overwrite
      * newer destination files and the last modified time of
      * <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param filterChains filterChains to apply during the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param inputEncoding the encoding used to read the files.
      * @param outputEncoding the encoding used to write the files.
      * @param project the project instance
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.6
      */
     public void copyFile(String sourceFile, String destFile,
                          FilterSetCollection filters, Vector filterChains,
                          boolean overwrite, boolean preserveLastModified,
                          String inputEncoding, String outputEncoding,
                          Project project)
         throws IOException {
         copyFile(new File(sourceFile), new File(destFile), filters,
                  filterChains, overwrite, preserveLastModified,
                  inputEncoding, outputEncoding, project);
     }
 
     /**
      * Convienence method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(File sourceFile, File destFile) throws IOException {
         copyFile(sourceFile, destFile, null, false, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a destination
      * specifying if token filtering must be used.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(File sourceFile, File destFile, FilterSetCollection filters)
         throws IOException {
         copyFile(sourceFile, destFile, filters, false, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(File sourceFile, File destFile, FilterSetCollection filters,
                          boolean overwrite) throws IOException {
         copyFile(sourceFile, destFile, filters, overwrite, false);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * source files may overwrite newer destination files and the
      * last modified time of <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @throws IOException if the copying fails
      */
     public void copyFile(File sourceFile, File destFile, FilterSetCollection filters,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         copyFile(sourceFile, destFile, filters, overwrite,
                  preserveLastModified, null);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * source files may overwrite newer destination files, the last
      * modified time of <code>destFile</code> file should be made
      * equal to the last modified time of <code>sourceFile</code> and
      * which character encoding to assume.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param encoding the encoding used to read and write the files.
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.5
      */
     public void copyFile(File sourceFile, File destFile,
                          FilterSetCollection filters, boolean overwrite,
                          boolean preserveLastModified, String encoding)
         throws IOException {
         copyFile(sourceFile, destFile, filters, null, overwrite,
                  preserveLastModified, encoding, null);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * filter chains must be used, if source files may overwrite
      * newer destination files and the last modified time of
      * <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param filterChains filterChains to apply during the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param encoding the encoding used to read and write the files.
      * @param project the project instance
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.5
      */
     public void copyFile(File sourceFile, File destFile,
                          FilterSetCollection filters, Vector filterChains,
                          boolean overwrite, boolean preserveLastModified,
                          String encoding, Project project)
         throws IOException {
         copyFile(sourceFile, destFile, filters, filterChains,
                  overwrite, preserveLastModified, encoding, encoding, project);
     }
 
     /**
      * Convienence method to copy a file from a source to a
      * destination specifying if token filtering must be used, if
      * filter chains must be used, if source files may overwrite
      * newer destination files and the last modified time of
      * <code>destFile</code> file should be made equal
      * to the last modified time of <code>sourceFile</code>.
      *
      * @param sourceFile the file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile the file to copy to.
      *                 Must not be <code>null</code>.
      * @param filters the collection of filters to apply to this copy
      * @param filterChains filterChains to apply during the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      * @param inputEncoding the encoding used to read the files.
      * @param outputEncoding the encoding used to write the files.
      * @param project the project instance
      *
      *
      * @throws IOException if the copying fails
      *
      * @since Ant 1.6
      */
     public void copyFile(File sourceFile, File destFile,
                          FilterSetCollection filters, Vector filterChains,
                          boolean overwrite, boolean preserveLastModified,
                          String inputEncoding, String outputEncoding,
                          Project project)
         throws IOException {
 
         if (overwrite || !destFile.exists()
             || destFile.lastModified() < sourceFile.lastModified()) {
 
             if (destFile.exists() && destFile.isFile()) {
                 destFile.delete();
             }
 
             // ensure that parent dir of dest file exists!
             // not using getParentFile method to stay 1.1 compat
             File parent = getParentFile(destFile);
             if (!parent.exists()) {
                 parent.mkdirs();
             }
 
             final boolean filterSetsAvailable = (filters != null
                                                  && filters.hasFilters());
             final boolean filterChainsAvailable = (filterChains != null
                                                    && filterChains.size() > 0);
 
             if (filterSetsAvailable) {
                 BufferedReader in = null;
                 BufferedWriter out = null;
 
                 try {
                     if (inputEncoding == null) {
                         in = new BufferedReader(new FileReader(sourceFile));
                     } else {
                         InputStreamReader isr
                             = new InputStreamReader(new FileInputStream(sourceFile),
                                                     inputEncoding);
                         in = new BufferedReader(isr);
                     }
 
                     if (outputEncoding == null) {
                         out = new BufferedWriter(new FileWriter(destFile));
                     } else {
                         OutputStreamWriter osw
                             = new OutputStreamWriter(new FileOutputStream(destFile),
                                                      outputEncoding);
                         out = new BufferedWriter(osw);
                     }
 
                     if (filterChainsAvailable) {
                         ChainReaderHelper crh = new ChainReaderHelper();
                         crh.setBufferSize(8192);
                         crh.setPrimaryReader(in);
                         crh.setFilterChains(filterChains);
                         crh.setProject(project);
                         Reader rdr = crh.getAssembledReader();
                         in = new BufferedReader(rdr);
                     }
 
                     LineTokenizer lineTokenizer = new LineTokenizer();
                     lineTokenizer.setIncludeDelims(true);
                     String newline = null;
                     String line = lineTokenizer.getToken(in);
                     while (line != null) {
                         if (line.length() == 0) {
                             // this should not happen, because the lines are
                             // returned with the end of line delimiter
                             out.newLine();
                         } else {
                             newline = filters.replaceTokens(line);
                             out.write(newline);
                         }
                         line = lineTokenizer.getToken(in);
                     }
                 } finally {
                     if (out != null) {
                         out.close();
                     }
                     if (in != null) {
                         in.close();
                     }
                 }
             } else if (filterChainsAvailable
                        || (inputEncoding != null
                            && !inputEncoding.equals(outputEncoding))
                        || (inputEncoding == null && outputEncoding != null)) {
                 BufferedReader in = null;
                 BufferedWriter out = null;
 
                  try {
                      if (inputEncoding == null) {
                          in = new BufferedReader(new FileReader(sourceFile));
                      } else {
                          in =
                              new BufferedReader(
                                  new InputStreamReader(
                                      new FileInputStream(sourceFile),
                                      inputEncoding));
                      }
 
                      if (outputEncoding == null) {
                          out = new BufferedWriter(new FileWriter(destFile));
                      } else {
                          out =
                              new BufferedWriter(
                                  new OutputStreamWriter(
                                      new FileOutputStream(destFile),
                                      outputEncoding));
                      }
 
                      if (filterChainsAvailable) {
                          ChainReaderHelper crh = new ChainReaderHelper();
                          crh.setBufferSize(8192);
                          crh.setPrimaryReader(in);
                          crh.setFilterChains(filterChains);
                          crh.setProject(project);
                          Reader rdr = crh.getAssembledReader();
                          in = new BufferedReader(rdr);
                      }
                      char[] buffer = new char[1024 * 8];
                      while (true) {
                          int nRead = in.read(buffer, 0, buffer.length);
                          if (nRead == -1) {
                              break;
                          }
                          out.write(buffer, 0, nRead);
                       }
                   } finally {
                       if (out != null) {
                          out.close();
                      }
                      if (in != null) {
                          in.close();
                      }
                  }
             } else {
                 FileInputStream in = null;
                 FileOutputStream out = null;
                 try {
                     in = new FileInputStream(sourceFile);
                     out = new FileOutputStream(destFile);
 
                     byte[] buffer = new byte[8 * 1024];
                     int count = 0;
                     do {
                         out.write(buffer, 0, count);
                         count = in.read(buffer, 0, buffer.length);
                     } while (count != -1);
                 } finally {
                     if (out != null) {
                         out.close();
                     }
                     if (in != null) {
                         in.close();
                     }
                 }
             }
 
             if (preserveLastModified) {
                 setFileLastModified(destFile, sourceFile.lastModified());
             }
         }
     }
 
     /**
      * see whether we have a setLastModified method in File and return it.
      *
      * @return a method to setLastModified.
      */
     protected final Method getSetLastModified() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             return null;
         }
         synchronized (lockReflection) {
             if (setLastModified == null) {
                 try {
                     setLastModified =
                         java.io.File.class.getMethod("setLastModified",
                                                      new Class[] {Long.TYPE});
                 } catch (NoSuchMethodException nse) {
                     throw new BuildException("File.setlastModified not in JDK > 1.1?",
                                              nse);
                 }
             }
         }
         return setLastModified;
     }
 
     /**
      * Calls File.setLastModified(long time) in a Java 1.1 compatible way.
      *
      * @param file the file whose modified time is to be set
      * @param time the time to which the last modified time is to be set.
      *
      * @throws BuildException if the time cannot be set.
      */
     public void setFileLastModified(File file, long time)
         throws BuildException {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             return;
         }
         Long[] times = new Long[1];
         if (time < 0) {
             times[0] = new Long(System.currentTimeMillis());
         } else {
             times[0] = new Long(time);
         }
 
         try {
             getSetLastModified().invoke(file, times);
         } catch (java.lang.reflect.InvocationTargetException ite) {
             Throwable nested = ite.getTargetException();
             throw new BuildException("Exception setting the modification time "
                                      + "of " + file, nested);
         } catch (Throwable other) {
             throw new BuildException("Exception setting the modification time "
                                      + "of " + file, other);
         }
     }
 
     /**
      * Interpret the filename as a file relative to the given file -
      * unless the filename already represents an absolute filename.
      *
      * @param file the "reference" file for relative paths. This
      * instance must be an absolute file and must not contain
      * &quot;./&quot; or &quot;../&quot; sequences (same for \ instead
      * of /).  If it is null, this call is equivalent to
      * <code>new java.io.File(filename)</code>.
      *
      * @param filename a file name
      *
      * @return an absolute file that doesn't contain &quot;./&quot; or
      * &quot;../&quot; sequences and uses the correct separator for
      * the current platform.
      */
     public File resolveFile(File file, String filename) {
         filename = filename.replace('/', File.separatorChar)
             .replace('\\', File.separatorChar);
 
         // deal with absolute files
         if (!onNetWare) {
             if (filename.startsWith(File.separator)
                 || (filename.length() >= 2
                     && Character.isLetter(filename.charAt(0))
                     && filename.charAt(1) == ':')) {
                 return normalize(filename);
             }
         } else {
             // the assumption that the : will appear as the second character in
             // the path name breaks down when NetWare is a supported platform.
             // Netware volumes are of the pattern: "data:\"
             int colon = filename.indexOf(":");
             if (filename.startsWith(File.separator)
                 || (colon > -1)) {
                 return normalize(filename);
             }
         }
 
         if (file == null) {
             return new File(filename);
         }
 
         File helpFile = new File(file.getAbsolutePath());
         StringTokenizer tok = new StringTokenizer(filename, File.separator);
         while (tok.hasMoreTokens()) {
             String part = tok.nextToken();
             if (part.equals("..")) {
                 helpFile = getParentFile(helpFile);
                 if (helpFile == null) {
                     String msg = "The file or path you specified ("
                         + filename + ") is invalid relative to "
                         + file.getPath();
                     throw new BuildException(msg);
                 }
             } else if (part.equals(".")) {
                 // Do nothing here
             } else {
                 helpFile = new File(helpFile, part);
             }
         }
 
         return new File(helpFile.getAbsolutePath());
     }
 
     /**
      * &quot;normalize&quot; the given absolute path.
      *
      * <p>This includes:
      * <ul>
      *   <li>Uppercase the drive letter if there is one.</li>
      *   <li>Remove redundant slashes after the drive spec.</li>
      *   <li>resolve all ./, .\, ../ and ..\ sequences.</li>
      *   <li>DOS style paths that start with a drive letter will have
      *     \ as the separator.</li>
      * </ul>
      *
      * @param path the path to be normalized
      * @return the normalized version of the path.
      *
      * @throws java.lang.NullPointerException if the file path is
      * equal to null.
      */
     public File normalize(String path) {
         String orig = path;
 
         path = path.replace('/', File.separatorChar)
             .replace('\\', File.separatorChar);
 
         // make sure we are dealing with an absolute path
         int colon = path.indexOf(":");
 
         if (!onNetWare) {
             if (!path.startsWith(File.separator)
                 && !(path.length() >= 2
                     && Character.isLetter(path.charAt(0))
                     && colon == 1)) {
                 String msg = path + " is not an absolute path";
                 throw new BuildException(msg);
             }
         } else {
             if (!path.startsWith(File.separator)
                 && (colon == -1)) {
                 String msg = path + " is not an absolute path";
                 throw new BuildException(msg);
             }
         }
 
         boolean dosWithDrive = false;
         String root = null;
         // Eliminate consecutive slashes after the drive spec
         if ((!onNetWare && path.length() >= 2
                 && Character.isLetter(path.charAt(0))
                 && path.charAt(1) == ':')
             || (onNetWare && colon > -1)) {
 
             dosWithDrive = true;
 
             char[] ca = path.replace('/', '\\').toCharArray();
             StringBuffer sbRoot = new StringBuffer();
             for (int i = 0; i < colon; i++) {
                 sbRoot.append(Character.toUpperCase(ca[i]));
             }
             sbRoot.append(':');
             if (colon + 1 < path.length()) {
                 sbRoot.append(File.separatorChar);
             }
             root = sbRoot.toString();
 
             // Eliminate consecutive slashes after the drive spec
             StringBuffer sbPath = new StringBuffer();
             for (int i = colon + 1; i < ca.length; i++) {
                 if ((ca[i] != '\\')
                     || (ca[i] == '\\' && ca[i - 1] != '\\')) {
                     sbPath.append(ca[i]);
                 }
             }
             path = sbPath.toString().replace('\\', File.separatorChar);
 
         } else {
             if (path.length() == 1) {
                 root = File.separator;
                 path = "";
             } else if (path.charAt(1) == File.separatorChar) {
                 // UNC drive
                 root = File.separator + File.separator;
                 path = path.substring(2);
             } else {
                 root = File.separator;
                 path = path.substring(1);
             }
         }
 
         Stack s = new Stack();
         s.push(root);
         StringTokenizer tok = new StringTokenizer(path, File.separator);
         while (tok.hasMoreTokens()) {
             String thisToken = tok.nextToken();
             if (".".equals(thisToken)) {
                 continue;
             } else if ("..".equals(thisToken)) {
                 if (s.size() < 2) {
                     throw new BuildException("Cannot resolve path " + orig);
                 } else {
                     s.pop();
                 }
             } else { // plain component
                 s.push(thisToken);
             }
         }
 
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < s.size(); i++) {
             if (i > 1) {
                 // not before the filesystem root and not after it, since root
                 // already contains one
                 sb.append(File.separatorChar);
             }
             sb.append(s.elementAt(i));
         }
 
 
         path = sb.toString();
         if (dosWithDrive) {
             path = path.replace('/', '\\');
         }
         return new File(path);
     }
 
     /**
      * Create a temporary file in a given directory.
      *
      * <p>The file denoted by the returned abstract pathname did not
      * exist before this method was invoked, any subsequent invocation
      * of this method will yield a different file name.</p>
      *
      * <p>This method is different to File.createTempFile of JDK 1.2
      * as it doesn't create the file itself.
      * It uses the location pointed to by java.io.tmpdir
      * when the parentDir attribute is
      * null.</p>
      *
      * @param parentDir Directory to create the temporary file in -
      * current working directory will be assumed if this parameter is
      * null.
      *
      * @return a File reference to the new temporary file.
      * @since ant 1.5
      */
     public File createTempFile(String prefix, String suffix, File parentDir) {
 
         File result = null;
         String parent = System.getProperty("java.io.tmpdir");
         if (parentDir != null) {
             parent = parentDir.getPath();
         }
         DecimalFormat fmt = new DecimalFormat("#####");
         synchronized (rand) {
             do {
                 result = new File(parent,
                                   prefix + fmt.format(Math.abs(rand.nextInt()))
                                   + suffix);
             } while (result.exists());
         }
         return result;
     }
 
     /**
      * Compares the contents of two files.
      *
      * <p>simple but sub-optimal comparision algorithm.  written for
      * working rather than fast. Better would be a block read into
      * buffers followed by long comparisions apart from the final 1-7
      * bytes.</p>
      *
      * @param f1 the file whose content is to be compared.
      * @param f2 the other file whose content is to be compared.
      *
      * @return true if the content of the files is the same.
      *
      * @throws IOException if the files cannot be read.
      *
      * @since 1.9
      */
     public boolean contentEquals(File f1, File f2) throws IOException {
         if (f1.exists() != f2.exists()) {
             return false;
         }
 
         if (!f1.exists()) {
             // two not existing files are equal
             return true;
         }
 
         if (f1.isDirectory() || f2.isDirectory()) {
             // don't want to compare directory contents for now
             return false;
         }
 
         if (fileNameEquals(f1, f2)) {
             // same filename => true
             return true;
         }
 
         if (f1.length() != f2.length()) {
             // different size =>false
             return false;
         }
 
         InputStream in1 = null;
         InputStream in2 = null;
         try {
             in1 = new BufferedInputStream(new FileInputStream(f1));
             in2 = new BufferedInputStream(new FileInputStream(f2));
 
             int expectedByte = in1.read();
             while (expectedByte != -1) {
                 if (expectedByte != in2.read()) {
                     return false;
                 }
                 expectedByte = in1.read();
             }
             if (in2.read() != -1) {
                 return false;
             }
             return true;
         } finally {
             if (in1 != null) {
                 try {
                     in1.close();
                 } catch (IOException e) {
                     // ignore
                 }
             }
             if (in2 != null) {
                 try {
                     in2.close();
                 } catch (IOException e) {
                     // ignore
                 }
             }
         }
     }
 
     /**
      * Emulation of File.getParentFile for JDK 1.1
      *
      *
      * @param f the file whose parent is required.
      * @return the given file's parent, or null if the file does not have a
      *         parent.
      * @since 1.10
      */
     public File getParentFile(File f) {
         if (f != null) {
             String p = f.getParent();
             if (p != null) {
                 return new File(p);
             }
         }
         return null;
     }
 
     /**
      * Read from reader till EOF
      * @param rdr the reader from which to read.
      * @return the contents read out of the given reader
      *
      * @throws IOException if the contents could not be read out from the
      *         reader.
      */
     public static final String readFully(Reader rdr) throws IOException {
         return readFully(rdr, 8192);
     }
 
     /**
      * Read from reader till EOF
diff --git a/src/main/org/apache/tools/ant/util/regexp/RegexpMatcherFactory.java b/src/main/org/apache/tools/ant/util/regexp/RegexpMatcherFactory.java
index dca7554c1..be93ded4b 100644
--- a/src/main/org/apache/tools/ant/util/regexp/RegexpMatcherFactory.java
+++ b/src/main/org/apache/tools/ant/util/regexp/RegexpMatcherFactory.java
@@ -1,144 +1,144 @@
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
  * 4. The names "Ant" and "Apache Software
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
 
 package org.apache.tools.ant.util.regexp;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 
 /**
  * Simple Factory Class that produces an implementation of
  * RegexpMatcher based on the system property
  * <code>ant.regexp.matcherimpl</code> and the classes
  * available.
  *
  * <p>In a more general framework this class would be abstract and
  * have a static newInstance method.</p>
  *
  * @author Stefan Bodewig
  */
 public class RegexpMatcherFactory {
 
     public RegexpMatcherFactory() {
     }
 
     /***
      * Create a new regular expression instance.
      */
     public RegexpMatcher newRegexpMatcher() throws BuildException {
         return newRegexpMatcher(null);
     }
 
     /***
      * Create a new regular expression instance.
      *
      * @param p Project whose ant.regexp.regexpimpl property will be used.
      */
     public RegexpMatcher newRegexpMatcher(Project p)
         throws BuildException {
         String systemDefault = null;
         if (p == null) {
             systemDefault = System.getProperty("ant.regexp.regexpimpl");
         } else {
             systemDefault = p.getProperty("ant.regexp.regexpimpl");
         }
 
         if (systemDefault != null) {
             return createInstance(systemDefault);
             // XXX     should we silently catch possible exceptions and try to
             //         load a different implementation?
         }
 
         try {
             testAvailability("java.util.regex.Matcher");
             return createInstance("org.apache.tools.ant.util.regexp.Jdk14RegexpMatcher");
         } catch (BuildException be) {
             // ignore
         }
 
         try {
             testAvailability("org.apache.oro.text.regex.Pattern");
             return createInstance("org.apache.tools.ant.util.regexp.JakartaOroMatcher");
         } catch (BuildException be) {
             // ignore
         }
 
         try {
             testAvailability("org.apache.regexp.RE");
             return createInstance("org.apache.tools.ant.util.regexp.JakartaRegexpMatcher");
         } catch (BuildException be) {
             // ignore
         }
 
         throw new BuildException("No supported regular expression matcher found");
    }
 
     protected RegexpMatcher createInstance(String className)
         throws BuildException {
         try {
             Class implClass = Class.forName(className);
             return (RegexpMatcher) implClass.newInstance();
         } catch (Throwable t) {
             throw new BuildException(t);
         }
     }
 
     protected void testAvailability(String className) throws BuildException {
         try {
-            Class implClass = Class.forName(className);
+            Class.forName(className);
         } catch (Throwable t) {
             throw new BuildException(t);
         }
     }
 }
