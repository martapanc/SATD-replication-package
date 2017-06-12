diff --git a/docs/manual/running.html b/docs/manual/running.html
index 9e11288d0..4a0a770ff 100644
--- a/docs/manual/running.html
+++ b/docs/manual/running.html
@@ -1,228 +1,230 @@
 <html>
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <title>Running Apache Ant</title>
 </head>
 
 <body>
 
 <h1>Running Ant</h1>
 <h2><a name="commandline">Command Line</a></h2>
 <p> If you've installed Ant as described in the
 <a href="install.html"> Installing Ant</a> section,
 running Ant from the command-line is simple: just type
 <code>ant</code>.</p>
 <p>When no arguments are specified, Ant looks for a <code>build.xml</code>
 file in the current directory and, if found, uses that file as the
 build file and runs the target specified in the <code>default</code>
 attribute of the <code>&lt;project&gt;</code> tag.
 To make Ant use
 a build file other than <code>build.xml</code>, use the command-line
 option <nobr><code>-buildfile <i>file</i></code></nobr>,
 where <i>file</i> is the name of the build file you want to use.</p>
 If you use the <nobr><code>-find [<i>file</i>]</code></nobr> option,
 Ant will search for a build file first in the current directory, then in
 the parent directory, and so on, until either a build file is found or the root
 of the filesystem has been reached. By default, it will look for a build file
 called <code>build.xml</code>. To have it search for a build file other
 than <code>build.xml</code>, specify a file argument.
 <strong>Note:</strong> If you include any other flags or arguments
 on the command line after
 the <nobr><code>-find</code></nobr> flag, you must include the file argument
 for the <nobr><code>-find</code></nobr> flag, even if the name of the
 build file you want to find is <code>build.xml</code>.
 
 <p>You can also set <a href="using.html#properties">properties</a> on the
 command line.  This can be done with
 the <nobr><code>-D<i>property</i>=<i>value</i></code></nobr> option,
 where <i>property</i> is the name of the property,
 and <i>value</i> is the value for that property. If you specify a
 property that is also set in the build file
 (see the <a href="CoreTasks/property.html">property</a> task),
 the value specified on the
 command line will override the value specified in the
 build file.
 Defining properties on the command line can also be used to pass in
 the value of environment variables - just pass
 <nobr><code>-DMYVAR=%MYVAR%</code></nobr> (Windows) or
 <nobr><code>-DMYVAR=$MYVAR</code></nobr> (Unix)
 to Ant. You can then access
 these variables inside your build file as <code>${MYVAR}</code>.
 You can also access environment variables using the
 <a href="CoreTasks/property.html"> property</a> task's
 <code>environment</code> attribute.
 </p>
 
 <p>Options that affect the amount of logging output by Ant are:
 <nobr><code>-quiet</code></nobr>,
 which instructs Ant to print less
 information to the console;
 <nobr><code>-verbose</code></nobr>, which causes Ant to print
 additional information to the console; and <nobr><code>-debug</code></nobr>,
 which causes Ant to print considerably more additional information.
 </p>
 
 <p>It is also possible to specify one or more targets that should be executed.
 When omitted, the target that is specified in the
 <code>default</code> attribute of the
 <a href="using.html#projects"><code>project</code></a> tag is
 used.</p>
 
 <p>The <nobr><code>-projecthelp</code></nobr> option prints out a list
 of the build file's targets. Targets that include a
 <code>description</code> attribute are listed as &quot;Main targets&quot;,
 those without a <code>description</code> are listed as
 &quot;Subtargets&quot;, then the &quot;Default&quot; target is listed.
 
 <h3><a name="options">Command-line Options Summary</a></h3>
 <pre>ant [options] [target [target2 [target3] ...]]
 Options:
   -help                  print this message
   -projecthelp           print project help information
   -version               print the version information and exit
   -diagnostics           print information that might be helpful to
                          diagnose or report problems.
   -quiet, -q             be extra quiet
   -verbose, -v           be extra verbose
   -debug                 print debugging information
   -emacs                 produce logging information without adornments
   -logfile &lt;file&gt;        use given file for log
     -l     &lt;file&gt;                ''
   -logger &lt;classname&gt;    the class which is to perform logging
   -listener &lt;classname&gt;  add an instance of class as a project listener
   -buildfile &lt;file&gt;      use given buildfile
     -file    &lt;file&gt;              ''
     -f       &lt;file&gt;              ''
   -D&lt;property&gt;=&lt;value&gt;   use value for given property
+  -keep-going, -k        execute all targets that do not depend
+                         on failed target(s)
   -propertyfile &lt;name&gt;   load all properties from file with -D
                          properties taking precedence
   -inputhandler &lt;class&gt;  the class which will handle input requests
   -find &lt;file&gt;           search for buildfile towards the root of the
                          filesystem and use it
 </pre>
 <p>For more information about <code>-logger</code> and
 <code>-listener</code> see
 <a href="listeners.html">Loggers &amp; Listeners</a>.
 <p>For more information about <code>-inputhandler</code> see
 <a href="inputhandler.html">InputHandler</a>.
 
 <h3>Examples</h3>
 <blockquote>
   <pre>ant</pre>
 </blockquote>
 <p>runs Ant using the <code>build.xml</code> file in the current directory, on
 the default target.</p>
 <blockquote>
   <pre>ant -buildfile test.xml</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the default target.</p>
 <blockquote>
   <pre>ant -buildfile test.xml dist</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the target called <code>dist</code>.</p>
 <blockquote>
   <pre>ant -buildfile test.xml -Dbuild=build/classes dist</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the target called <code>dist</code>, setting the <code>build</code> property
 to the value <code>build/classes</code>.</p>
 
 <h3><a name="files">Files</a></h3>
 
 <p>The Ant wrapper script for Unix will source (read and evaluate) the
 file <code>~/.antrc</code> before it does anything. On Windows, the Ant
 wrapper batch-file invokes <code>%HOME%\antrc_pre.bat</code> at the start and
 <code>%HOME%\antrc_post.bat</code> at the end.  You can use these
 files, for example, to set/unset environment variables that should only be
 visible during the execution of Ant.  See the next section for examples.</p>
 
 <h3><a name="envvars">Environment Variables</a></h3>
 
 <p>The wrapper scripts use the following environment variables (if
 set):</p>
 
 <ul>
   <li><code>JAVACMD</code> - full path of the Java executable.  Use this
   to invoke a different JVM than <code>JAVA_HOME/bin/java(.exe)</code>.</li>
 
   <li><code>ANT_OPTS</code> - command-line arguments that should be
   passed to the JVM. For example, you can define system properties or set
   the maximum Java heap size here.</li>
 
   <li><code>ANT_ARGS</code> - Ant command-line arguments. For example,
   set <code>ANT_ARGS</code> to point to a different logger, include a
   listener, and to include the <code>-find</code> flag.</li>
   <strong>Note:</strong> If you include <code>-find</code>
   in <code>ANT_ARGS</code>, you should include the name of the build file
   to find, even if the file is called <code>build.xml</code>.
 </ul>
 
 <h2><a name="cygwin">Cygwin Users</a></h2>
 <p>The Unix launch script that come with Ant works correctly with Cygwin. You
 should not have any problems launching Ant form the Cygwin shell. It is important
 to note however, that once Ant is runing it is part of the JDK which operates as
 a native Windows application. The JDK is not a Cygwin executable, and it therefore
 has no knowledge of the Cygwin paths, etc. In particular when using the &lt;exec&gt;
 task, executable names such as &quot;/bin/sh&quot; will not work, even though these
 work from the Cygwin shell from which Ant was launched. You can use an executable
 name such as &quot;sh&quot; and rely on that command being available in the Windows
 path.
 </p>
 
 <h2><a name="os2">OS/2 Users</a></h2>
 <p>The OS/2 lanuch script was developed so as it can perform complex task. It has two parts:
 <code>ant.cmd</code> which calls Ant and <code>antenv.cmd</code> which sets environment for Ant.
 Most often you will just call <code>ant.cmd</code> using the same command line options as described
 above. The behaviour can be modified by a number of ways explained below.</p>
 
 <p>Script <code>ant.cmd</code> first verifies whether the Ant environment is set correctly. The
 requirements are:</p>
 <ol>
 <li>Environment variable <code>JAVA_HOME</code> is set.</li>
 <li>Environment variable <code>ANT_HOME</code> is set.</li>
 <li>environment variable <code>CLASSPATH</code> is set and contains at least one element from
 <code>JAVA_HOME</code> and at least one element from <code>ANT_HOME</code>.</li>
 </ol>
 
 <p>If any of these conditions is violated, script <code>antenv.cmd</code> is called. This script
 first invokes configuration scripts if there exist: the system-wide configuration
 <code>antconf.cmd</code> from the <code>%ETC%</code> directory and then the user comfiguration
 <code>antrc.cmd</code> from the <code>%HOME%</code> directory. At this moment both
 <code>JAVA_HOME</code> and <code>ANT_HOME</code> must be defined because <code>antenv.cmd</code>
 now adds <code>classes.zip</code> or <code>tools.jar</code> (depending on version of JVM) and
 everything from <code>%ANT_HOME%\lib</code> except <code>ant-*.jar</code> to
 <code>CLASSPATH</code>. Finally <code>ant.cmd</code> calls per-directory configuration
 <code>antrc.cmd</code>. All settings made by <code>ant.cmd</code> are local and are undone when the
 script ends. The settings made by <code>antenv.cmd</code> are persistent during the lifetime of the
 shell (of course unless called automaticaly from <code>ant.cmd</code>). It is thus possible to call
 <code>antenv.cmd</code> manually and modify some settings before calling <code>ant.cmd</code>.</p>
 
 <p>Scripts <code>envset.cmd</code> and <code>runrc.cmd</code> perform auxilliary tasks. All scripts
 have some documentation inside.</p>
 
 <h2><a name="viajava">Running Ant via Java</a></h2>
 <p>If you have installed Ant in the do-it-yourself way, Ant can be started
 with:</p>
 <blockquote>
   <pre>java -Dant.home=c:\ant org.apache.tools.ant.Main [options] [target]</pre>
 </blockquote>
 
 <p>These instructions actually do exactly the same as the <code>ant</code>
 command. The options and target are the same as when running Ant with the <code>ant</code>
 command. This example assumes you have set your classpath to include:</p>
 <ul>
 <li><code>ant.jar</code></li>
 <li>jars/classes for your XML parser</li>
 <li>the JDK's required jar/zip files</li>
 </ul>
 <br>
 <hr>
 <p align="center">Copyright &copy; 2000-2003 Apache Software Foundation. All rights
 Reserved.</p>
 
 </body>
 </html>
 
diff --git a/src/main/org/apache/tools/ant/Main.java b/src/main/org/apache/tools/ant/Main.java
index 30deedd9c..48d0d911f 100644
--- a/src/main/org/apache/tools/ant/Main.java
+++ b/src/main/org/apache/tools/ant/Main.java
@@ -1,997 +1,1006 @@
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
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.Enumeration;
 import java.util.Properties;
 import java.util.Vector;
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.launch.AntMain;
 
 
 /**
  * Command line entry point into Ant. This class is entered via the
  * cannonical `public static void main` entry point and reads the
  * command line arguments. It then assembles and executes an Ant
  * project.
  * <p>
  * If you integrating Ant into some other tool, this is not the class
  * to use as an entry point. Please see the source code of this
  * class to see how it manipulates the Ant project classes.
  *
  * @author duncan@x180.com
  */
 public class Main implements AntMain {
 
     /** The default build file name. */
     public static final String DEFAULT_BUILD_FILENAME = "build.xml";
 
     /** Our current message output status. Follows Project.MSG_XXX. */
     private int msgOutputLevel = Project.MSG_INFO;
 
     /** File that we are using for configuration. */
     private File buildFile; /* null */
 
     /** Stream to use for logging. */
     private static PrintStream out = System.out;
 
     /** Stream that we are using for logging error messages. */
     private static PrintStream err = System.err;
 
     /** The build targets. */
     private Vector targets = new Vector(5);
 
     /** Set of properties that can be used by tasks. */
     private Properties definedProps = new Properties();
 
     /** Names of classes to add as listeners to project. */
     private Vector listeners = new Vector(5);
 
     /** File names of property files to load on startup. */
     private Vector propertyFiles = new Vector(5);
 
     /** Indicates whether this build is to support interactive input */
     private boolean allowInput = true;
 
+    /** keep going mode */
+    private boolean keepGoingMode = false;
+
     /**
      * The Ant logger class. There may be only one logger. It will have
      * the right to use the 'out' PrintStream. The class must implements the
      * BuildLogger interface.
      */
     private String loggerClassname = null;
 
     /**
      * The Ant InputHandler class.  There may be only one input
      * handler.
      */
     private String inputHandlerClassname = null;
 
     /**
      * Whether or not output to the log is to be unadorned.
      */
     private boolean emacsMode = false;
 
     /**
      * Whether or not this instance has successfully been
      * constructed and is ready to run.
      */
     private boolean readyToRun = false;
 
     /**
      * Whether or not we should only parse and display the project help
      * information.
      */
     private boolean projectHelp = false;
 
     /**
      * Whether or not a logfile is being used. This is used to
      * check if the output streams must be closed.
      */
     private static boolean isLogFileUsed = false;
 
     /**
      * Prints the message of the Throwable if it (the message) is not
      * <code>null</code>.
      *
      * @param t Throwable to print the message of.
      *          Must not be <code>null</code>.
      */
     private static void printMessage(Throwable t) {
         String message = t.getMessage();
         if (message != null) {
             System.err.println(message);
         }
     }
 
     /**
      * Creates a new instance of this class using the
      * arguments specified, gives it any extra user properties which have been
      * specified, and then runs the build using the classloader provided.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      * @param additionalUserProperties Any extra properties to use in this
      *        build. May be <code>null</code>, which is the equivalent to
      *        passing in an empty set of properties.
      * @param coreLoader Classloader used for core classes. May be
      *        <code>null</code> in which case the system classloader is used.
      */
     public static void start(String[] args, Properties additionalUserProperties,
                              ClassLoader coreLoader) {
         Main m = new Main();
         m.startAnt(args, additionalUserProperties, coreLoader);
     }
 
     /**
      * Start Ant
      * @param args command line args
      * @param additionalUserProperties properties to set beyond those that
      *        may be specified on the args list
      * @param coreLoader - not used
      *
      * @since Ant 1.6
      */
     public void startAnt(String[] args, Properties additionalUserProperties,
                          ClassLoader coreLoader) {
 
         try {
             Diagnostics.validateVersion();
             processArgs(args);
         } catch (Throwable exc) {
             handleLogfile();
             printMessage(exc);
             System.exit(1);
         }
 
         if (additionalUserProperties != null) {
             for (Enumeration e = additionalUserProperties.keys();
                     e.hasMoreElements();) {
                 String key = (String) e.nextElement();
                 String property = additionalUserProperties.getProperty(key);
                 definedProps.put(key, property);
             }
         }
 
         // expect the worst
         int exitCode = 1;
         try {
             runBuild(coreLoader);
             exitCode = 0;
         } catch (BuildException be) {
             if (err != System.err) {
                 printMessage(be);
             }
         } catch (Throwable exc) {
             exc.printStackTrace();
             printMessage(exc);
         } finally {
             handleLogfile();
         }
         System.exit(exitCode);
     }
 
     /**
      * Close logfiles, if we have been writing to them.
      *
      * @since Ant 1.6
      */
     private static void handleLogfile() {
         if (isLogFileUsed) {
             if (out != null) {
                 try {
                     out.close();
                 } catch (final Exception e) {
                     //ignore
                 }
             }
             if (err != null) {
                 try {
                     err.close();
                 } catch (final Exception e) {
                     //ignore
                 }
             }
         }
     }
 
     /**
      * Command line entry point. This method kicks off the building
      * of a project object and executes a build using either a given
      * target or the default target.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      */
     public static void main(String[] args) {
         start(args, null, null);
     }
 
     /**
      * Constructor used when creating Main for later arg processing
      * and startup
      */
     public Main() {
     }
 
     /**
      * Sole constructor, which parses and deals with command line
      * arguments.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      *
      * @exception BuildException if the specified build file doesn't exist
      *                           or is a directory.
      */
     protected Main(String[] args) throws BuildException {
         processArgs(args);
     }
 
     /**
      * Process command line arguments
      *
      * @param args the command line arguments.
      *
      * @since Ant 1.6
      */
     private void processArgs(String[] args) {
         String searchForThis = null;
         PrintStream logTo = null;
 
         // cycle through given args
 
         for (int i = 0; i < args.length; i++) {
             String arg = args[i];
 
             if (arg.equals("-help")) {
                 printUsage();
                 return;
             } else if (arg.equals("-version")) {
                 printVersion();
                 return;
             } else if (arg.equals("-diagnostics")){
                 Diagnostics.doReport(System.out);
                 return;
             } else if (arg.equals("-quiet") || arg.equals("-q")) {
                 msgOutputLevel = Project.MSG_WARN;
             } else if (arg.equals("-verbose") || arg.equals("-v")) {
                 printVersion();
                 msgOutputLevel = Project.MSG_VERBOSE;
             } else if (arg.equals("-debug")) {
                 printVersion();
                 msgOutputLevel = Project.MSG_DEBUG;
             } else if (arg.equals("-noinput")) {
                 allowInput = false;
             } else if (arg.equals("-logfile") || arg.equals("-l")) {
                 try {
                     File logFile = new File(args[i + 1]);
                     i++;
                     logTo = new PrintStream(new FileOutputStream(logFile));
                     isLogFileUsed = true;
                 } catch (IOException ioe) {
                     String msg = "Cannot write on the specified log file. "
                         + "Make sure the path exists and you have write "
                         + "permissions.";
                     throw new BuildException(msg);
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     String msg = "You must specify a log file when " +
                         "using the -log argument";
                     throw new BuildException(msg);
                 }
             } else if (arg.equals("-buildfile") || arg.equals("-file")
                        || arg.equals("-f")) {
                 try {
                     buildFile = new File(args[i + 1].replace('/', File.separatorChar));
                     i++;
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     String msg = "You must specify a buildfile when " +
                         "using the -buildfile argument";
                     throw new BuildException(msg);
                 }
             } else if (arg.equals("-listener")) {
                 try {
                     listeners.addElement(args[i + 1]);
                     i++;
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     String msg = "You must specify a classname when " +
                         "using the -listener argument";
                     throw new BuildException(msg);
                 }
             } else if (arg.startsWith("-D")) {
 
                 /* Interestingly enough, we get to here when a user
                  * uses -Dname=value. However, in some cases, the OS
                  * goes ahead and parses this out to args
                  *   {"-Dname", "value"}
                  * so instead of parsing on "=", we just make the "-D"
                  * characters go away and skip one argument forward.
                  *
                  * I don't know how to predict when the JDK is going
                  * to help or not, so we simply look for the equals sign.
                  */
 
                 String name = arg.substring(2, arg.length());
                 String value = null;
                 int posEq = name.indexOf("=");
                 if (posEq > 0) {
                     value = name.substring(posEq + 1);
                     name = name.substring(0, posEq);
                 } else if (i < args.length - 1) {
                     value = args[++i];
                 }
 
                 definedProps.put(name, value);
             } else if (arg.equals("-logger")) {
                 if (loggerClassname != null) {
                     throw new BuildException("Only one logger class may "
                         + " be specified.");
                 }
                 try {
                     loggerClassname = args[++i];
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     throw new BuildException("You must specify a classname when"
                                              + " using the -logger argument");
                 }
             } else if (arg.equals("-inputhandler")) {
                 if (inputHandlerClassname != null) {
                     throw new BuildException("Only one input handler class may "
                                              + "be specified.");
                 }
                 try {
                     inputHandlerClassname = args[++i];
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     throw new BuildException("You must specify a classname when"
                                              + " using the -inputhandler"
                                              + " argument");
                 }
             } else if (arg.equals("-emacs")) {
                 emacsMode = true;
             } else if (arg.equals("-projecthelp")) {
                 // set the flag to display the targets and quit
                 projectHelp = true;
             } else if (arg.equals("-find")) {
                 // eat up next arg if present, default to build.xml
                 if (i < args.length - 1) {
                     searchForThis = args[++i];
                 } else {
                     searchForThis = DEFAULT_BUILD_FILENAME;
                 }
             } else if (arg.startsWith("-propertyfile")) {
                 try {
                     propertyFiles.addElement(args[i + 1]);
                     i++;
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     String msg = "You must specify a property filename when " +
                         "using the -propertyfile argument";
                     throw new BuildException(msg);
                 }
+            } else if (arg.equals("-k") || arg.equals("-keep-going")) {
+                keepGoingMode = true;
             } else if (arg.startsWith("-")) {
                 // we don't have any more args to recognize!
                 String msg = "Unknown argument: " + arg;
                 System.out.println(msg);
                 printUsage();
                 throw new BuildException("");
             } else {
                 // if it's no other arg, it may be the target
                 targets.addElement(arg);
             }
         }
 
         // if buildFile was not specified on the command line,
         if (buildFile == null) {
             // but -find then search for it
             if (searchForThis != null) {
                 buildFile = findBuildFile(System.getProperty("user.dir"),
                                           searchForThis);
             } else {
                 buildFile = new File(DEFAULT_BUILD_FILENAME);
             }
         }
 
         // make sure buildfile exists
         if (!buildFile.exists()) {
             System.out.println("Buildfile: " + buildFile + " does not exist!");
             throw new BuildException("Build failed");
         }
 
         // make sure it's not a directory (this falls into the ultra
         // paranoid lets check everything catagory
 
         if (buildFile.isDirectory()) {
             System.out.println("What? Buildfile: " + buildFile + " is a dir!");
             throw new BuildException("Build failed");
         }
 
         // Load the property files specified by -propertyfile
         for (int propertyFileIndex = 0;
              propertyFileIndex < propertyFiles.size();
              propertyFileIndex++) {
             String filename
                 = (String) propertyFiles.elementAt(propertyFileIndex);
             Properties props = new Properties();
             FileInputStream fis = null;
             try {
                 fis = new FileInputStream(filename);
                 props.load(fis);
             } catch (IOException e) {
                 System.out.println("Could not load property file "
                    + filename + ": " + e.getMessage());
             } finally {
                 if (fis != null) {
                     try {
                         fis.close();
                     } catch (IOException e){
                     }
                 }
             }
 
             // ensure that -D properties take precedence
             Enumeration propertyNames = props.propertyNames();
             while (propertyNames.hasMoreElements()) {
                 String name = (String) propertyNames.nextElement();
                 if (definedProps.getProperty(name) == null) {
                     definedProps.put(name, props.getProperty(name));
                 }
             }
         }
 
         if (msgOutputLevel >= Project.MSG_INFO) {
             System.out.println("Buildfile: " + buildFile);
         }
 
         if (logTo != null) {
             out = err = logTo;
             System.setOut(out);
             System.setErr(out);
         }
         readyToRun = true;
     }
 
     /**
      * Helper to get the parent file for a given file.
      * <p>
      * Added to simulate File.getParentFile() from JDK 1.2.
      *
      * @param file   File to find parent of. Must not be <code>null</code>.
      * @return       Parent file or null if none
      */
     private File getParentFile(File file) {
         String filename = file.getAbsolutePath();
         file = new File(filename);
         filename = file.getParent();
 
         if (filename != null && msgOutputLevel >= Project.MSG_VERBOSE) {
             System.out.println("Searching in " + filename);
         }
 
         return (filename == null) ? null : new File(filename);
     }
 
     /**
      * Search parent directories for the build file.
      * <p>
      * Takes the given target as a suffix to append to each
      * parent directory in seach of a build file.  Once the
      * root of the file-system has been reached an exception
      * is thrown.
      *
      * @param start  Leaf directory of search.
      *               Must not be <code>null</code>.
      * @param suffix  Suffix filename to look for in parents.
      *                Must not be <code>null</code>.
      *
      * @return A handle to the build file if one is found
      *
      * @exception BuildException if no build file is found
      */
     private File findBuildFile(String start, String suffix)
          throws BuildException {
         if (msgOutputLevel >= Project.MSG_INFO) {
             System.out.println("Searching for " + suffix + " ...");
         }
 
         File parent = new File(new File(start).getAbsolutePath());
         File file = new File(parent, suffix);
 
         // check if the target file exists in the current directory
         while (!file.exists()) {
             // change to parent directory
             parent = getParentFile(parent);
 
             // if parent is null, then we are at the root of the fs,
             // complain that we can't find the build file.
             if (parent == null) {
                 throw new BuildException("Could not locate a build file!");
             }
 
             // refresh our file handle
             file = new File(parent, suffix);
         }
 
         return file;
     }
 
     /**
      * Executes the build. If the constructor for this instance failed
      * (e.g. returned after issuing a warning), this method returns
      * immediately.
      *
      * @param coreLoader The classloader to use to find core classes.
      *                   May be <code>null</code>, in which case the
      *                   system classloader is used.
      *
      * @exception BuildException if the build fails
      */
     private void runBuild(ClassLoader coreLoader) throws BuildException {
 
         if (!readyToRun) {
             return;
         }
 
         final Project project = new Project();
         project.setCoreLoader(coreLoader);
 
         Throwable error = null;
 
         try {
             addBuildListeners(project);
             addInputHandler(project);
 
             PrintStream err = System.err;
             PrintStream out = System.out;
 
             // use a system manager that prevents from System.exit()
             // only in JDK > 1.1
             SecurityManager oldsm = null;
             if (!JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_0) &&
                 !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)){
                 oldsm = System.getSecurityManager();
 
                 //SecurityManager can not be installed here for backwards
                 //compatability reasons (PD). Needs to be loaded prior to
                 //ant class if we are going to implement it.
                 //System.setSecurityManager(new NoExitSecurityManager());
             }
             try {
                 if (allowInput) {
                     project.setDefaultInputStream(System.in);
                 }
                 System.setIn(new DemuxInputStream(project));
                 System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
                 System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
 
                 if (!projectHelp) {
                     project.fireBuildStarted();
                 }
                 project.init();
                 project.setUserProperty("ant.version", getAntVersion());
 
                 // set user-define properties
                 Enumeration e = definedProps.keys();
                 while (e.hasMoreElements()) {
                     String arg = (String) e.nextElement();
                     String value = (String) definedProps.get(arg);
                     project.setUserProperty(arg, value);
                 }
 
                 project.setUserProperty("ant.file",
                                         buildFile.getAbsolutePath());
 
+                project.setKeepGoingMode(keepGoingMode);
+
                 ProjectHelper.configureProject(project, buildFile);
 
                 if (projectHelp) {
                     printDescription(project);
                     printTargets(project, msgOutputLevel > Project.MSG_INFO);
                     return;
                 }
 
                 // make sure that we have a target to execute
                 if (targets.size() == 0) {
                     if (project.getDefaultTarget() != null) {
                         targets.addElement(project.getDefaultTarget());
                     }
                 }
 
                 project.executeTargets(targets);
             } finally {
                 // put back the original security manager
                 //The following will never eval to true. (PD)
                 if (oldsm != null){
                     System.setSecurityManager(oldsm);
                 }
 
                 System.setOut(out);
                 System.setErr(err);
             }
         } catch (RuntimeException exc) {
             error = exc;
             throw exc;
         } catch (Error err) {
             error = err;
             throw err;
         } finally {
             if (!projectHelp) {
                 project.fireBuildFinished(error);
             }
         }
     }
 
     /**
      * Adds the listeners specified in the command line arguments,
      * along with the default listener, to the specified project.
      *
      * @param project The project to add listeners to.
      *                Must not be <code>null</code>.
      */
     protected void addBuildListeners(Project project) {
 
         // Add the default listener
         project.addBuildListener(createLogger());
 
         for (int i = 0; i < listeners.size(); i++) {
             String className = (String) listeners.elementAt(i);
             try {
                 BuildListener listener =
                     (BuildListener) Class.forName(className).newInstance();
                 if (project != null) {
                     project.setProjectReference(listener);
                 }
                 project.addBuildListener(listener);
             } catch (Throwable exc) {
                 throw new BuildException("Unable to instantiate listener "
                     + className, exc);
             }
         }
     }
 
     /**
      * Creates the InputHandler and adds it to the project.
      *
      * @param project the project instance.
      *
      * @exception BuildException if a specified InputHandler
      *                           implementation could not be loaded.
      */
     private void addInputHandler(Project project) throws BuildException {
         InputHandler handler = null;
         if (inputHandlerClassname == null) {
             handler = new DefaultInputHandler();
         } else {
             try {
                 handler = (InputHandler)
                     (Class.forName(inputHandlerClassname).newInstance());
                 if (project != null) {
                     project.setProjectReference(handler);
                 }
             } catch (ClassCastException e) {
                 String msg = "The specified input handler class "
                     + inputHandlerClassname
                     + " does not implement the InputHandler interface";
                 throw new BuildException(msg);
             } catch (Exception e) {
                 String msg = "Unable to instantiate specified input handler "
                     + "class " + inputHandlerClassname + " : "
                     + e.getClass().getName();
                 throw new BuildException(msg);
             }
         }
         project.setInputHandler(handler);
     }
 
     // XXX: (Jon Skeet) Any reason for writing a message and then using a bare
     // RuntimeException rather than just using a BuildException here? Is it
     // in case the message could end up being written to no loggers (as the
     // loggers could have failed to be created due to this failure)?
     /**
      * Creates the default build logger for sending build events to the ant
      * log.
      *
      * @return the logger instance for this build.
      */
     private BuildLogger createLogger() {
         BuildLogger logger = null;
         if (loggerClassname != null) {
             try {
                 Class loggerClass = Class.forName(loggerClassname);
                 logger = (BuildLogger) (loggerClass.newInstance());
             } catch (ClassCastException e) {
                 System.err.println("The specified logger class "
                     + loggerClassname
                     + " does not implement the BuildLogger interface");
                 throw new RuntimeException();
             } catch (Exception e) {
                 System.err.println("Unable to instantiate specified logger "
                     + "class " + loggerClassname + " : "
                     + e.getClass().getName());
                 throw new RuntimeException();
             }
         } else {
             logger = new DefaultLogger();
         }
 
         logger.setMessageOutputLevel(msgOutputLevel);
         logger.setOutputPrintStream(out);
         logger.setErrorPrintStream(err);
         logger.setEmacsMode(emacsMode);
 
         return logger;
     }
 
     /**
      * Prints the usage information for this class to <code>System.out</code>.
      */
     private static void printUsage() {
         String lSep = System.getProperty("line.separator");
         StringBuffer msg = new StringBuffer();
         msg.append("ant [options] [target [target2 [target3] ...]]" + lSep);
         msg.append("Options: " + lSep);
         msg.append("  -help                  print this message" + lSep);
         msg.append("  -projecthelp           print project help information" + lSep);
         msg.append("  -version               print the version information and exit" + lSep);
         msg.append("  -diagnostics           print information that might be helpful to" + lSep);
         msg.append("                         diagnose or report problems." + lSep);
         msg.append("  -quiet, -q             be extra quiet" + lSep);
         msg.append("  -verbose, -v           be extra verbose" + lSep);
         msg.append("  -debug                 print debugging information" + lSep);
         msg.append("  -emacs                 produce logging information without adornments" + lSep);
         msg.append("  -logfile <file>        use given file for log" + lSep);
         msg.append("    -l     <file>                ''" + lSep);
         msg.append("  -logger <classname>    the class which is to perform logging" + lSep);
         msg.append("  -listener <classname>  add an instance of class as a project listener" + lSep);
         msg.append("  -noinput               do not allow interactive input" + lSep);
         msg.append("  -buildfile <file>      use given buildfile" + lSep);
         msg.append("    -file    <file>              ''" + lSep);
         msg.append("    -f       <file>              ''" + lSep);
         msg.append("  -D<property>=<value>   use value for given property" + lSep);
+        msg.append("  -keep-going, -k        execute all targets that do not depend" + lSep);
+        msg.append("                         on failed target(s)" + lSep);
         msg.append("  -propertyfile <name>   load all properties from file with -D" + lSep);
         msg.append("                         properties taking precedence" + lSep);
         msg.append("  -inputhandler <class>  the class which will handle input requests" + lSep);
         msg.append("  -find <file>           search for buildfile towards the root of the" + lSep);
         msg.append("                         filesystem and use it" + lSep);
         System.out.println(msg.toString());
     }
 
     /**
      * Prints the Ant version information to <code>System.out</code>.
      *
      * @exception BuildException if the version information is unavailable
      */
     private static void printVersion() throws BuildException {
         System.out.println(getAntVersion());
     }
 
     /**
      * Cache of the Ant version information when it has been loaded.
      */
     private static String antVersion = null;
 
     /**
      * Returns the Ant version information, if available. Once the information
      * has been loaded once, it's cached and returned from the cache on future
      * calls.
      *
      * @return the Ant version information as a String
      *         (always non-<code>null</code>)
      *
      * @exception BuildException if the version information is unavailable
      */
     public static synchronized String getAntVersion() throws BuildException {
         if (antVersion == null) {
             try {
                 Properties props = new Properties();
                 InputStream in =
                     Main.class.getResourceAsStream("/org/apache/tools/ant/version.txt");
                 props.load(in);
                 in.close();
 
                 StringBuffer msg = new StringBuffer();
                 msg.append("Apache Ant version ");
                 msg.append(props.getProperty("VERSION"));
                 msg.append(" compiled on ");
                 msg.append(props.getProperty("DATE"));
                 antVersion = msg.toString();
             } catch (IOException ioe) {
                 throw new BuildException("Could not load the version information:"
                                          + ioe.getMessage());
             } catch (NullPointerException npe) {
                 throw new BuildException("Could not load the version information.");
             }
         }
         return antVersion;
     }
 
      /**
       * Prints the description of a project (if there is one) to
       * <code>System.out</code>.
       *
       * @param project The project to display a description of.
       *                Must not be <code>null</code>.
       */
     private static void printDescription(Project project) {
        if (project.getDescription() != null) {
           project.log(project.getDescription());
        }
     }
 
     /**
      * Prints a list of all targets in the specified project to
      * <code>System.out</code>, optionally including subtargets.
      *
      * @param project The project to display a description of.
      *                Must not be <code>null</code>.
      * @param printSubTargets Whether or not subtarget names should also be
      *                        printed.
      */
     private static void printTargets(Project project, boolean printSubTargets) {
         // find the target with the longest name
         int maxLength = 0;
         Enumeration ptargets = project.getTargets().elements();
         String targetName;
         String targetDescription;
         Target currentTarget;
         // split the targets in top-level and sub-targets depending
         // on the presence of a description
         Vector topNames = new Vector();
         Vector topDescriptions = new Vector();
         Vector subNames = new Vector();
 
         while (ptargets.hasMoreElements()) {
             currentTarget = (Target) ptargets.nextElement();
             targetName = currentTarget.getName();
             if (targetName.equals("")) {
                 continue;
             }
             targetDescription = currentTarget.getDescription();
             // maintain a sorted list of targets
             if (targetDescription == null) {
                 int pos = findTargetPosition(subNames, targetName);
                 subNames.insertElementAt(targetName, pos);
             } else {
                 int pos = findTargetPosition(topNames, targetName);
                 topNames.insertElementAt(targetName, pos);
                 topDescriptions.insertElementAt(targetDescription, pos);
                 if (targetName.length() > maxLength) {
                     maxLength = targetName.length();
                 }
             }
         }
 
         printTargets(project, topNames, topDescriptions, "Main targets:",
                      maxLength);
         //if there were no main targets, we list all subtargets
         //as it means nothing has a description
         if (topNames.size() == 0) {
             printSubTargets = true;
         }
         if (printSubTargets) {
             printTargets(project, subNames, null, "Other targets:", 0);
         }
 
         String defaultTarget = project.getDefaultTarget();
         if (defaultTarget != null && !"".equals(defaultTarget)) {
             // shouldn't need to check but...
             project.log("Default target: " + defaultTarget);
         }
     }
 
     /**
      * Searches for the correct place to insert a name into a list so as
      * to keep the list sorted alphabetically.
      *
      * @param names The current list of names. Must not be <code>null</code>.
      * @param name  The name to find a place for.
      *              Must not be <code>null</code>.
      *
      * @return the correct place in the list for the given name
      */
     private static int findTargetPosition(Vector names, String name) {
         int res = names.size();
         for (int i = 0; i < names.size() && res == names.size(); i++) {
             if (name.compareTo((String) names.elementAt(i)) < 0) {
                 res = i;
             }
         }
         return res;
     }
 
     /**
      * Writes a formatted list of target names to <code>System.out</code>
      * with an optional description.
      *
      *
      * @param project the project instance.
      * @param names The names to be printed.
      *              Must not be <code>null</code>.
      * @param descriptions The associated target descriptions.
      *                     May be <code>null</code>, in which case
      *                     no descriptions are displayed.
      *                     If non-<code>null</code>, this should have
      *                     as many elements as <code>names</code>.
      * @param heading The heading to display.
      *                Should not be <code>null</code>.
      * @param maxlen The maximum length of the names of the targets.
      *               If descriptions are given, they are padded to this
      *               position so they line up (so long as the names really
      *               <i>are</i> shorter than this).
      */
     private static void printTargets(Project project, Vector names,
                                      Vector descriptions, String heading,
                                      int maxlen) {
         // now, start printing the targets and their descriptions
         String lSep = System.getProperty("line.separator");
         // got a bit annoyed that I couldn't find a pad function
         String spaces = "    ";
         while (spaces.length() <= maxlen) {
             spaces += spaces;
         }
         StringBuffer msg = new StringBuffer();
         msg.append(heading + lSep + lSep);
         for (int i = 0; i < names.size(); i++) {
             msg.append(" ");
             msg.append(names.elementAt(i));
             if (descriptions != null) {
                 msg.append(spaces.substring(0, maxlen - ((String) names.elementAt(i)).length() + 2));
                 msg.append(descriptions.elementAt(i));
             }
             msg.append(lSep);
         }
         project.log(msg.toString());
     }
 }
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 53f9332f5..f80a4b1e9 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,2003 +1,2088 @@
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
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.EOFException;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Stack;
 import java.util.Vector;
+import java.util.Set;
+import java.util.HashSet;
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.types.FilterSet;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Description;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
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
  * @author duncan@x180.com
  *
  * @version $Revision$
  */
 
 public class Project {
     /** Message priority of "error". */
     public static final int MSG_ERR = 0;
     /** Message priority of "warning". */
     public static final int MSG_WARN = 1;
     /** Message priority of "information". */
     public static final int MSG_INFO = 2;
     /** Message priority of "verbose". */
     public static final int MSG_VERBOSE = 3;
     /** Message priority of "debug". */
     public static final int MSG_DEBUG = 4;
 
     /**
      * Constant for the "visiting" state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITING = "VISITING";
     /**
      * Constant for the "visited" state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITED = "VISITED";
 
     /**
      * The class name of the Ant class loader to use for
      * JDK 1.2 and above
      */
     private static final String ANTCLASSLOADER_JDK12
         = "org.apache.tools.ant.loader.AntClassLoader2";
 
     /**
      * Version constant for Java 1.0
      *
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static final String JAVA_1_0 = JavaEnvUtils.JAVA_1_0;
     /**
      * Version constant for Java 1.1
      *
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static final String JAVA_1_1 = JavaEnvUtils.JAVA_1_1;
     /**
      * Version constant for Java 1.2
      *
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static final String JAVA_1_2 = JavaEnvUtils.JAVA_1_2;
     /**
      * Version constant for Java 1.3
      *
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static final String JAVA_1_3 = JavaEnvUtils.JAVA_1_3;
     /**
      * Version constant for Java 1.4
      *
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static final String JAVA_1_4 = JavaEnvUtils.JAVA_1_4;
 
     /** Default filter start token. */
     public static final String TOKEN_START = FilterSet.DEFAULT_TOKEN_START;
     /** Default filter end token. */
     public static final String TOKEN_END = FilterSet.DEFAULT_TOKEN_END;
 
     /** Name of this project. */
     private String name;
     /** Description for this project (if any). */
     private String description;
 
 
     /** Map of references within the project (paths etc) (String to Object). */
     private Hashtable references = new AntRefTable(this);
 
     /** Name of the project's default target. */
     private String defaultTarget;
 
     /** Map from target names to targets (String to Target). */
     private Hashtable targets = new Hashtable();
     /** Set of global filters. */
     private FilterSet globalFilterSet = new FilterSet();
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
      * The Ant core classloader - may be <code>null</code> if using
      * parent classloader.
      */
     private ClassLoader coreLoader = null;
 
     /** Records the latest task to be executed on a thread (Thread to Task). */
     private Hashtable threadTasks = new Hashtable();
 
     /** Records the latest task to be executed on a thread Group. */
     private Hashtable threadGroupTasks = new Hashtable();
 
     /**
      * Called to handle any input requests.
      */
     private InputHandler inputHandler = null;
 
     /**
      * The default input stream used to read any input
      */
     private InputStream defaultInputStream = null;
 
     /**
+     * Keep going flag
+     */
+    private boolean keepGoingMode = false;
+
+    /**
      * Sets the input handler
      *
      * @param handler the InputHandler instance to use for gathering input.
      */
     public void setInputHandler(InputHandler handler) {
         inputHandler = handler;
     }
 
     /**
      * Set the default System input stream. Normally this stream is set to
      * System.in. This inputStream is used when no task inptu redirection is
      * being performed.
      *
      * @param defaultInputStream the default input stream to use when input
      *        is reuested.
      * @since Ant 1.6
      */
     public void setDefaultInputStream(InputStream defaultInputStream) {
         this.defaultInputStream = defaultInputStream;
     }
 
     /**
      * Get this project's input stream
      *
      * @return the InputStream instance in use by this Porject instance to
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
+        subProject.setKeepGoingMode(this.isKeepGoingMode());
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
         if (!JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             try {
                 // 1.2+ - create advanced helper dynamically
                 Class loaderClass
                     = Class.forName(ANTCLASSLOADER_JDK12);
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
 
     /**
      * Factory method to create a class loader for loading classes from
      * a given path
      *
      * @param path the path from whcih clases are to be loaded.
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
     public synchronized void setUserProperty(String name, String value) {
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
     public synchronized void setInheritedProperty(String name, String value) {
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
+     * Sets "keep-going" mode. In this mode ANT will try to execute
+     * as many targets as possible. All targets that do not depend
+     * on failed target(s) will be executed.
+     * @param keepGoingMode "keep-going" mode
+     * @since Ant 1.6
+     */
+    public void setKeepGoingMode(boolean keepGoingMode) {
+        this.keepGoingMode = keepGoingMode;
+    }
+
+    /**
+     * Returns the keep-going mode.
+     * @return "keep-going" mode
+     * @since Ant 1.6
+     */
+    public boolean isKeepGoingMode() {
+        return this.keepGoingMode;
+    }
+
+    /**
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
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             fireMessageLogged(this, line, isError ? MSG_ERR : MSG_INFO);
         } else {
             if (isError) {
                 task.handleErrorOutput(line);
             } else {
                 task.handleOutput(line);
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
      * @param line Message to handle. Should not be <code>null</code>.
      * @param isError Whether the text represents an error (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxFlush(String line, boolean isError) {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             fireMessageLogged(this, line, isError ? MSG_ERR : MSG_INFO);
         } else {
             if (isError) {
                 task.handleErrorFlush(line);
             } else {
                 task.handleFlush(line);
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
 
         // Sort the dependency tree, and run everything from the
         // beginning until we hit our targetName.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
         Vector sortedTargets = topoSort(targetName, targets);
 
-        int curidx = 0;
-        Target curtarget;
-
-        do {
-            curtarget = (Target) sortedTargets.elementAt(curidx++);
-            curtarget.performTasks();
-        } while (!curtarget.getName().equals(targetName));
+        Set succeededTargets = new HashSet();
+        BuildException buildException = null; // first build exception
+        for (Enumeration iter = sortedTargets.elements();
+             iter.hasMoreElements();) {
+            Target curtarget = (Target) iter.nextElement();
+            boolean canExecute = true;
+            for (Enumeration depIter = curtarget.getDependencies();
+                 depIter.hasMoreElements();) {
+                String dependencyName = ((String) depIter.nextElement());
+                if (!succeededTargets.contains(dependencyName)) {
+                    canExecute = false;
+                    log(curtarget,
+                        "Cannot execute '" + curtarget.getName() + "' - '"
+                        + dependencyName + "' failed or was not executed.",
+                        MSG_ERR);
+                    break;
+                }
+            }
+            if (canExecute) {
+                Throwable thrownException = null;
+                try {
+                    curtarget.performTasks();
+                    succeededTargets.add(curtarget.getName());
+                } catch (RuntimeException ex) {
+                    if (!(keepGoingMode)) {
+                        throw ex; // throw further
+                    }
+                    thrownException = ex;
+                } catch (Throwable ex) {
+                    if (!(keepGoingMode)) {
+                        throw new BuildException(ex);
+                    }
+                    thrownException = ex;
+                }
+                if (thrownException != null) {
+                    if (thrownException instanceof BuildException) {
+                        log(curtarget,
+                            "Target '" + curtarget.getName()
+                            + "' failed with message '"
+                            + thrownException.getMessage() + "'.", MSG_ERR);
+                        // only the first build exception is reported
+                        if (buildException == null) {
+                            buildException = (BuildException) thrownException;
+                        }
+                    } else {
+                        log(curtarget,
+                            "Target '" + curtarget.getName()
+                            + "' failed with message '"
+                            + thrownException.getMessage() + "'.", MSG_ERR);
+                        thrownException.printStackTrace(System.err);
+                        if (buildException == null) {
+                            buildException =
+                                new BuildException(thrownException);
+                        }
+                    }
+                }
+            }
+            if (curtarget.getName().equals(targetName)) { // old exit condition
+                break;
+            }
+        }
+        if (buildException != null) {
+            throw buildException;
+        }
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
         return (s.equalsIgnoreCase("on")
                 || s.equalsIgnoreCase("true")
                 || s.equalsIgnoreCase("yes"));
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
             Object old = ((AntRefTable) references).getReal(name);
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
                 log("Caught exception (" + t.getClass().getName() + ")"
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
         return ComponentHelper.getComponentHelper(this).getElementName(element);
     }
 
     /**
      * Sends a "build started" event to the build listeners for this project.
      */
     public void fireBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Vector listeners = getBuildListeners();
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         int size = listeners.size();
         for (int i = 0; i < size; i++) {
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
         synchronized (this) {
             if (loggingMessage) {
                 throw new BuildException("Listener attempted to access "
                     + (priority == MSG_ERR ? "System.err" : "System.out")
                     + " - infinite loop terminated");
             }
             try {
                 loggingMessage = true;
                 int size = listeners.size();
                 for (int i = 0; i < size; i++) {
                     BuildListener listener = (BuildListener) listeners.elementAt(i);
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
      * Get the current task assopciated with a thread, if any
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
         Project project;
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
             Object o = super.get(key);
             if (o instanceof UnknownElement) {
                 // Make sure that
                 ((UnknownElement) o).maybeConfigure();
                 o = ((UnknownElement) o).getTask();
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/SubAnt.java b/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
index 83ec721e0..82d59fa34 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
@@ -1,458 +1,497 @@
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
 
 import java.io.File;
 import java.io.IOException;
 
 import java.util.Vector;
 import java.util.Enumeration;
 
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.DirSet;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.FileList;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.types.Reference;
 
 
 /**
  * <i>EXPERIMENTAL:</i> This task is experimental and may be under continual
  * change till Ant1.6 ships; it may even be omitted from the product.
  * <p>
  * Calls a given target for all defined sub-builds. This is an extension
  * of ant for bulk project execution.
  * <p>
  * <h2> Use with directories </h2>
  * <p>
  * subant can be used with directory sets to execute a build from different directories.
  * 2 different options are offered
  * </p>
  * <ul>
  * <li>
  * run the same build file /somepath/otherpath/mybuild.xml
  * with different base directories use the genericantfile attribute
  * </li>
  * <li>if you want to run directory1/build.xml, directory2/build.xml, ....
  * use the antfile attribute. The base directory does not get set by the subant task in this case,
  * because you can specify it in each build file.
  * </li>
  * </ul>
  * @since Ant1.6
  * @author <a href="mailto:ddevienne@lgc.com">Dominique Devienne</a>
  * @author <A HREF="mailto:andreas.ames@tenovis.com">Andreas Ames</A>
  * @ant.task name="subant" category="control"
  */
 public class SubAnt
              extends Task {
 
     private Path buildpath;
 
     private String target = null;
     private String antfile = "build.xml";
     private File genericantfile = null;
     private boolean inheritAll = false;
     private boolean inheritRefs = false;
     private boolean failOnError = true;
     private String output  = null;
 
     private Vector properties = new Vector();
     private Vector references = new Vector();
     private Vector propertySets = new Vector();
     /**
      * Runs the various sub-builds.
      */
     public void execute() {
         if (buildpath == null) {
             throw new BuildException("No buildpath specified");
         }
 
         final String[] filenames = buildpath.list();
         final int count = filenames.length;
         if (count < 1) {
             log("No sub-builds to iterate on", Project.MSG_WARN);
             return;
         }
 /*
     //REVISIT: there must be cleaner way of doing this, if it is merited at all
         if (target == null) {
             target = getOwningTarget().getName();
         }
 */
+        BuildException buildException = null;
         for (int i = 0; i < count; ++i) {
-            File directory = null;
-            File file = new File(filenames[i]);
-            if (file.isDirectory()) {
-                if (genericantfile != null) {
-                    directory = file;
-                    file = genericantfile;
+            File file = null;
+            Throwable thrownException = null;
+            try {
+                File directory = null;
+                file = new File(filenames[i]);
+                if (file.isDirectory()) {
+                    if (genericantfile != null) {
+                        directory = file;
+                        file = genericantfile;
+                    } else {
+                        file = new File(file, antfile);
+                    }
+                }
+                execute(file, directory);
+            } catch (RuntimeException ex) {
+                if (!(getProject().isKeepGoingMode())) {
+                    throw ex; // throw further
+                }
+                thrownException = ex;
+            } catch (Throwable ex) {
+                if (!(getProject().isKeepGoingMode())) {
+                    throw new BuildException(ex);
+                }
+                thrownException = ex;
+            }
+            if (thrownException != null) {
+                if (thrownException instanceof BuildException) {
+                    log("File '" + file
+                        + "' failed with message '"
+                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
+                    // only the first build exception is reported
+                    if (buildException == null) {
+                        buildException = (BuildException) thrownException;
+                    }
                 } else {
-                    file = new File(file, antfile);
+                    log("Target '" + file
+                        + "' failed with message '"
+                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
+                    thrownException.printStackTrace(System.err);
+                    if (buildException == null) {
+                        buildException =
+                            new BuildException(thrownException);
+                    }
                 }
             }
-            execute(file, directory);
+        }
+        // check if one of the builds failed in keep going mode
+        if (buildException != null) {
+            throw buildException;
         }
     }
 
     /**
      * Runs the given target on the provided build file.
      *
      * @param  file the build file to execute
      * @param  directory the directory of the current iteration
      * @throws BuildException is the file cannot be found, read, is
      *         a directory, or the target called failed, but only if
      *         <code>failOnError</code> is <code>true</code>. Otherwise,
      *         a warning log message is simply output.
      */
     private void execute(File file, File directory)
                 throws BuildException {
         if (!file.exists() || file.isDirectory() || !file.canRead()) {
             String msg = "Invalid file: " + file;
             if (failOnError) {
                 throw new BuildException(msg);
             }
             log(msg, Project.MSG_WARN);
             return;
         }
 
         Ant ant = createAntTask(directory);
         String antfilename = null;
         try {
             antfilename = file.getCanonicalPath();
         } catch (IOException e) {
             throw new BuildException(e);
         }
 
         ant.setAntfile(antfilename);
         try {
             ant.execute();
         } catch (BuildException e) {
             if (failOnError) {
                 throw e;
             }
             log("Failure for target '" + target
                + "' of: " +  antfilename + "\n"
                + e.getMessage(), Project.MSG_WARN);
         }
     }
 
     /**
      * Build file name, to use in conjunction with directories.<br/>
      * Defaults to "build.xml".<br/>
      * If <code>genericantfile</code> is set, this attribute is ignored.
      *
      * @param  antfile the short build file name. Defaults to "build.xml".
      */
     public void setAntfile(String antfile) {
         this.antfile = antfile;
     }
 
     /**
      * Build file path, to use in conjunction with directories.<br/>
      * Use <code>genericantfile</code>, in order to run the same build file
      * with different basedirs.<br/>
      * If this attribute is set, <code>antfile</code> is ignored.
      *
      * @param afile (path of the generic ant file, absolute or relative to
      *               project base directory)
      * */
     public void setGenericAntfile(File afile) {
         this.genericantfile = afile;
     }
 
     /**
      * Sets whether to fail with a build exception on error, or go on.
      *
      * @param  failOnError the new value for this boolean flag.
      */
     public void setFailonerror(boolean failOnError) {
         this.failOnError = failOnError;
     }
 
     /**
      * The target to call on the different sub-builds. Set to "" to execute
      * the default target.
      * @param target the target
      * <p>
      */
     //     REVISIT: Defaults to the target name that contains this task if not specified.
     public void setTarget(String target) {
         this.target = target;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>output</code> attribute.
      *
      * @param  s the filename to write the output to.
      */
     public void setOutput(String s) {
         this.output = s;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>inheritall</code> attribute.
      *
      * @param  b the new value for this boolean flag.
      */
     public void setInheritall(boolean b) {
         this.inheritAll = b;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>inheritrefs</code> attribute.
      *
      * @param  b the new value for this boolean flag.
      */
     public void setInheritrefs(boolean b) {
         this.inheritRefs = b;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;property&gt;</code> element.
      *
      * @param  p the property to pass on explicitly to the sub-build.
      */
     public void addProperty(Property p) {
         properties.addElement(p);
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;reference&gt;</code> element.
      *
      * @param  r the reference to pass on explicitly to the sub-build.
      */
     public void addReference(Ant.Reference r) {
         references.addElement(r);
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;propertyset&gt;</code> element.
      * @param ps the propertset
      */
     public void addPropertyset(PropertySet ps) {
         propertySets.addElement(ps);
     }
 
     /**
      * Adds a directory set to the implicit build path.
      * <p>
      * <em>Note that the directories will be added to the build path
      * in no particular order, so if order is significant, one should
      * use a file list instead!</em>
      *
      * @param  set the directory set to add.
      */
     public void addDirset(DirSet set) {
         getBuildpath().addDirset(set);
     }
 
     /**
      * Adds a file set to the implicit build path.
      * <p>
      * <em>Note that the directories will be added to the build path
      * in no particular order, so if order is significant, one should
      * use a file list instead!</em>
      *
      * @param  set the file set to add.
      */
     public void addFileset(FileSet set) {
         getBuildpath().addFileset(set);
     }
 
     /**
      * Adds an ordered file list to the implicit build path.
      * <p>
      * <em>Note that contrary to file and directory sets, file lists
      * can reference non-existent files or directories!</em>
      *
      * @param  list the file list to add.
      */
     public void addFilelist(FileList list) {
         getBuildpath().addFilelist(list);
     }
 
     /**
      * Set the buildpath to be used to find sub-projects.
      *
      * @param  s an Ant Path object containing the buildpath.
      */
     public void setBuildpath(Path s) {
         getBuildpath().append(s);
     }
 
     /**
      * Creates a nested build path, and add it to the implicit build path.
      *
      * @return the newly created nested build path.
      */
     public Path createBuildpath() {
         return getBuildpath().createPath();
     }
 
     /**
      * Creates a nested <code>&lt;buildpathelement&gt;</code>,
      * and add it to the implicit build path.
      *
      * @return the newly created nested build path element.
      */
     public Path.PathElement createBuildpathElement() {
         return getBuildpath().createPathElement();
     }
 
     /**
      * Gets the implicit build path, creating it if <code>null</code>.
      *
      * @return the implicit build path.
      */
     private Path getBuildpath() {
         if (buildpath == null) {
             buildpath = new Path(getProject());
         }
         return buildpath;
     }
 
     /**
      * Buildpath to use, by reference.
      *
      * @param  r a reference to an Ant Path object containing the buildpath.
      */
     public void setBuildpathRef(Reference r) {
         createBuildpath().setRefid(r);
     }
 
     /**
      * Creates the &lt;ant&gt; task configured to run a specific target.
      *
      * @param directory : if not null the directory where the build should run
      *
      * @return the ant task, configured with the explicit properties and
      *         references necessary to run the sub-build.
      */
     private Ant createAntTask(File directory) {
         Ant ant = (Ant) getProject().createTask("ant");
         ant.setOwningTarget(getOwningTarget());
         ant.init();
         if (target != null && target.length() > 0) {
             ant.setTarget(target);
         }
 
 
         if (output != null) {
             ant.setOutput(output);
         }
 
         if (directory != null) {
             ant.setDir(directory);
         }
 
         ant.setInheritAll(inheritAll);
         for (Enumeration i = properties.elements(); i.hasMoreElements();) {
             copyProperty(ant.createProperty(), (Property) i.nextElement());
         }
 
         for (Enumeration i = propertySets.elements(); i.hasMoreElements();) {
             ant.addPropertyset((PropertySet) i.nextElement());
         }
 
         ant.setInheritRefs(inheritRefs);
         for (Enumeration i = references.elements(); i.hasMoreElements();) {
             ant.addReference((Ant.Reference) i.nextElement());
         }
 
         return ant;
     }
 
     /**
      * Assigns an Ant property to another.
      *
      * @param  to the destination property whose content is modified.
      * @param  from the source property whose content is copied.
      */
     private static void copyProperty(Property to, Property from) {
         to.setName(from.getName());
 
         if (from.getValue() != null) {
             to.setValue(from.getValue());
         }
         if (from.getFile() != null) {
             to.setFile(from.getFile());
         }
         if (from.getResource() != null) {
             to.setResource(from.getResource());
         }
         if (from.getPrefix() != null) {
             to.setPrefix(from.getPrefix());
         }
         if (from.getRefid() != null) {
             to.setRefid(from.getRefid());
         }
         if (from.getEnvironment() != null) {
             to.setEnvironment(from.getEnvironment());
         }
         if (from.getClasspath() != null) {
             to.setClasspath(from.getClasspath());
         }
     }
 
 } // END class SubAnt
