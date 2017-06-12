diff --git a/manual/running.html b/manual/running.html
index 46005b553..529afc7dc 100644
--- a/manual/running.html
+++ b/manual/running.html
@@ -1,622 +1,622 @@
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <html>
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
 <title>Running Apache Ant</title>
 </head>
 
 <body>
 
 <h1>Running Apache Ant</h1>
 <h2><a name="commandline">Command Line</a></h2>
 <p> If you've installed Apache Ant as described in the
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
 where <i>file</i> is the name of the build file you want to use
 (or a directory containing a <code>build.xml</code> file).</p>
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
 (see the <a href="Tasks/property.html">property</a> task),
 the value specified on the
 command line will override the value specified in the
 build file.
 Defining properties on the command line can also be used to pass in
 the value of environment variables; just pass
 <nobr><code>-DMYVAR=%MYVAR%</code></nobr> (Windows) or
 <nobr><code>-DMYVAR=$MYVAR</code></nobr> (Unix)
 to Ant. You can then access
 these variables inside your build file as <code>${MYVAR}</code>.
 You can also access environment variables using the
 <a href="Tasks/property.html"> property</a> task's
 <code>environment</code> attribute.
 </p>
 
 <p>Options that affect the amount of logging output by Ant are:
 <nobr><code>-quiet</code></nobr>,
 which instructs Ant to print less
 information to the console;
 <nobr><code>-verbose</code></nobr>, which causes Ant to print
 additional information to the console; <nobr><code>-debug</code></nobr>,
 which causes Ant to print considerably more additional information; and
 <nobr><code>-silent</code></nobr> which makes Ant print nothing but task
 output and build failures (useful to capture Ant output by scripts).
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
 &quot;Other targets&quot;, then the &quot;Default&quot; target is listed
 ("Other targets" are only displayed if there are no main
 targets, or if Ant is invoked in -verbose or -debug mode).
 
 <h3><a name="options">Command-line Options Summary</a></h3>
 <pre>ant [options] [target [target2 [target3] ...]]
 Options:
-  -help, -h              print this message
-  -projecthelp, -p       print project help information
+  -help, -h              print this message and exit
+  -projecthelp, -p       print project help information and exit
   -version               print the version information and exit
   -diagnostics           print information that might be helpful to
-                         diagnose or report problems.
+                         diagnose or report problems and exit
   -quiet, -q             be extra quiet
   -silent, -S            print nothing but task outputs and build failures
   -verbose, -v           be extra verbose
   -debug, -d             print debugging information
   -emacs, -e             produce logging information without adornments
   -lib &lt;path&gt;            specifies a path to search for jars and classes
   -logfile &lt;file&gt;        use given file for log
     -l     &lt;file&gt;                ''
   -logger &lt;classname&gt;    the class which is to perform logging
   -listener &lt;classname&gt;  add an instance of class as a project listener
   -noinput               do not allow interactive input
   -buildfile &lt;file&gt;      use given buildfile
     -file    &lt;file&gt;              ''
     -f       &lt;file&gt;              ''
   -D&lt;property&gt;=&lt;value&gt;   use value for given property
   -keep-going, -k        execute all targets that do not depend
                          on failed target(s)
   -propertyfile &lt;name&gt;   load all properties from file with -D
                          properties taking precedence
   -inputhandler &lt;class&gt;  the class which will handle input requests
   -find &lt;file&gt;           (s)earch for buildfile towards the root of
     -s  &lt;file&gt;           the filesystem and use it
   -nice  number          A niceness value for the main thread:
                          1 (lowest) to 10 (highest); 5 is the default
   -nouserlib             Run ant without using the jar files from ${user.home}/.ant/lib
   -noclasspath           Run ant without using CLASSPATH
   -autoproxy             Java 1.5+ : use the OS proxies
   -main &lt;class&gt;          override Ant's normal entry point
 </pre>
 <p>For more information about <code>-logger</code> and
 <code>-listener</code> see
 <a href="listeners.html">Loggers &amp; Listeners</a>.
 <p>For more information about <code>-inputhandler</code> see
 <a href="inputhandler.html">InputHandler</a>.
 <p>Easiest way of changing the exit-behaviour is subclassing the original main class:
 <pre>
 public class CustomExitCode extends org.apache.tools.ant.Main {
     protected void exit(int exitCode) {
         // implement your own behaviour, e.g. NOT exiting the JVM
     }
 }
 </pre> and starting Ant with access (<tt>-lib path-to-class</tt>) to this class.
 </p>
 
 <h3><a name="libs">Library Directories</a></h3>
 <p>
 Prior to Ant 1.6, all jars in the ANT_HOME/lib would be added to the CLASSPATH
 used to run Ant. This was done in the scripts that started Ant. From Ant 1.6,
 two directories are scanned by default and more can be added as required. The
 default directories scanned are ANT_HOME/lib and a user specific directory,
 ${user.home}/.ant/lib. This arrangement allows the Ant installation to be
 shared by many users while still allowing each user to deploy additional jars.
 Such additional jars could be support jars for Ant's optional tasks or jars
 containing third-party tasks to be used in the build. It also allows the main Ant installation to be locked down which will please system administrators.
 </p>
 
 <p>
 Additional directories to be searched may be added by using the -lib option.
 The -lib option specifies a search path. Any jars or classes in the directories
 of the path will be added to Ant's classloader. The order in which jars are
 added to the classpath is as follows:
 </p>
 
 <ul>
   <li>-lib jars in the order specified by the -lib elements on the command line</li>
   <li>jars from ${user.home}/.ant/lib (unless -nouserlib is set)</li>
   <li>jars from ANT_HOME/lib</li>
 </ul>
 
 <p>
 Note that the CLASSPATH environment variable is passed to Ant using a -lib
 option. Ant itself is started with a very minimalistic classpath.
 Ant should work perfectly well with an empty CLASSPATH environment variable,
 something the the -noclasspath option actually enforces. We get many more support calls related to classpath problems (especially quoting problems) than
 we like.
 
 </p>
 
 <p>
 The location of ${user.home}/.ant/lib is somewhat dependent on the JVM. On Unix
 systems ${user.home} maps to the user's home directory whilst on recent
 versions of Windows it will be somewhere such as
 C:\Documents&nbsp;and&nbsp;Settings\username\.ant\lib. You should consult your
 JVM documentation for more details.
 </p>
 
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
 
 <blockquote>
   <pre>ant -lib /home/ant/extras</pre>
 </blockquote>
 <p>runs Ant picking up additional task and support jars from the
 /home/ant/extras location</p>
 
 <blockquote>
   <pre>ant -lib one.jar;another.jar</pre>
   <pre>ant -lib one.jar -lib another.jar</pre>
 </blockquote>
 <p>adds two jars to Ants classpath.</p>
 
 
 
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
 
 <h3><a name="sysprops">Java System Properties</a></h3>
 <p>Some of Ant's core classes can be configured via system properties.</p>
 <p>Here is the result of a search through the codebase. Because system properties are
 available via Project instance, I searched for them with a
 <pre>
     grep -r -n "getPropert" * &gt; ..\grep.txt
 </pre>
 command. After that I filtered out the often-used but not-so-important values (most of them
 read-only values): <i>path.separator, ant.home, basedir, user.dir, os.name,
 line.separator, java.home, java.version, java.version, user.home, java.class.path</i><br>
 And I filtered out the <i>getPropertyHelper</i> access.</p>
 <table border="1">
 <tr>
   <th>property name</th>
   <th>valid values /default value</th>
   <th>description</th>
 </tr>
 <tr>
   <td><code>ant.build.javac.source</code></td>
   <td>Source-level version number</td>
   <td>Default <em>source</em> value for &lt;javac&gt;/&lt;javadoc&gt;</td>
 </tr>
 <tr>
   <td><code>ant.build.javac.target</code></td>
   <td>Class-compatibility version number</td>
   <td>Default <em>target</em> value for &lt;javac&gt;</td>
 </tr>
 <tr>
   <td><code>ant.executor.class</code></td>
   <td>classname; default is org. apache. tools. ant. helper. DefaultExecutor</td>
   <td><b>Since Ant 1.6.3</b> Ant will delegate Target invocation to the
 org.apache.tools.ant.Executor implementation specified here.
   </td>
 </tr>
 
 <tr>
   <td><code>ant.file</code></td>
   <td>read only: full filename of the build file</td>
   <td>This is set to the name of the build file. In
   <a href="Tasks/import.html">
   &lt;import&gt;-ed</a> files, this is set to the containing build file.
   </td>
 </tr>
 
 <tr>
   <td><code>ant.file.*</code></td>
   <td>read only: full filename of the build file of Ant projects
   </td>
   <td>This is set to the name of a file by project;
   this lets you determine the location of <a href="Tasks/import.html">
   &lt;import&gt;-ed</a> files,
   </td>
 </tr>
 
 <tr>
   <td><code>ant.input.properties</code></td>
   <td>filename (required)</td>
   <td>Name of the file holding the values for the
       <a href="inputhandler.html">PropertyFileInputHandler</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.logger.defaults</code></td>
   <!-- add the blank after the slash, so the browser can do a line break -->
   <td>filename (optional, default '/org/ apache/ tools/ ant/ listener/ defaults.properties')</td>
   <td>Name of the file holding the color mappings for the
       <a href="listeners.html#AnsiColorLogger">AnsiColorLogger</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.netrexxc.*</code></td>
   <td>several formats</td>
   <td>Use specified values as defaults for <a href="Tasks/netrexxc.html">netrexxc</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.PropertyHelper</code></td>
   <td>ant-reference-name (optional)</td>
   <td>Specify the PropertyHelper to use. The object must be of the type
       org.apache.tools.ant.PropertyHelper. If not defined an object of
       org.apache.tools.ant.PropertyHelper will be used as PropertyHelper.
   </td>
 </tr>
 <tr>
   <td><code>ant.regexp.regexpimpl</code></td>
   <td>classname</td>
   <td>classname for a RegExp implementation; if not set Ant uses JDK 1.4's implementation;
       <a href="Types/mapper.html#regexp-mapper">RegExp-Mapper</a>
       "Choice of regular expression implementation"
   </td>
 </tr>
 <tr>
   <td><code>ant.reuse.loader</code></td>
   <td>boolean</td>
   <td>allow to reuse classloaders
       used in org.apache.tools.ant.util.ClasspathUtil
   </td>
 </tr>
 <tr>
   <td><code>ant.XmlLogger.stylesheet.uri</code></td>
   <td>filename (default 'log.xsl')</td>
   <td>Name for the stylesheet to include in the logfile by
       <a href="listeners.html#XmlLogger">XmlLogger</a>.
   </td>
 </tr>
 <tr>
   <td><code>build.compiler</code></td>
   <td>name</td>
   <td>Specify the default compiler to use.
       see <a href="Tasks/javac.html">javac</a>,
       <a href="Tasks/ejb.html#ejbjar_weblogic">EJB Tasks</a>
       (compiler attribute),
       <a href="Tasks/javah.html">javah</a>
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.emacs</code></td>
   <td>boolean (default false)</td>
   <td>Enable emacs-compatible error messages.
       see <a href="Tasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.fulldepend</code></td>
   <td>boolean (default false)</td>
   <td>Enable full dependency checking
       see <a href="Tasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.jvc.extensions</code></td>
   <td>boolean (default true)</td>
   <td>enable Microsoft extensions of their java compiler
       see <a href="Tasks/javac.html">javac</a> "Jvc Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.pedantic</code></td>
   <td>boolean (default false)</td>
   <td>Enable pedantic warnings.
       see <a href="Tasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.warnings</code></td>
   <td>Deprecated flag</td>
   <td> see <a href="Tasks/javac.html">javac</a> "Jikes Notes" </td>
 </tr>
 <tr>
   <td><code>build.rmic</code></td>
   <td>name</td>
   <td>control the <a href="Tasks/rmic.html">rmic</a> compiler </td>
 </tr>
 <tr>
   <td><code>build.sysclasspath</code></td>
   <td>see <a href="sysclasspath.html">its dedicated page</a>, no
     default value</td>
   <td>see <a href="sysclasspath.html">its dedicated page</a></td>
 </tr>
 <tr>
   <td><code>file.encoding</code></td>
   <td>name of a supported character set (e.g. UTF-8, ISO-8859-1, US-ASCII)</td>
   <td>use as default character set of email messages; use as default for source-, dest- and bundleencoding
       in <a href="Tasks/translate.html">translate</a> <br>
       see JavaDoc of <a target="_blank" href="http://docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">java.nio.charset.Charset</a>
       for more information about character sets (not used in Ant, but has nice docs).
   </td>
 </tr>
 <tr>
   <td><code>jikes.class.path</code></td>
   <td>path</td>
   <td>The specified path is added to the classpath if jikes is used as compiler.</td>
 </tr>
 <tr>
   <td><code>MailLogger.properties.file, MailLogger.*</code></td>
   <td>filename (optional, defaults derived from Project instance)</td>
   <td>Name of the file holding properties for sending emails by the
       <a href="listeners.html#MailLogger">MailLogger</a>. Override properties set
       inside the buildfile or via command line.
   </td>
 </tr>
 <tr>
   <td><code>org.apache.tools.ant.ProjectHelper</code></td>
   <!-- add the blank after the slash, so the browser can do a line break -->
   <td>classname (optional, default 'org.apache.tools.ant.ProjectHelper2')</td>
   <td>specifies the classname to use as ProjectHelper. The class must extend
       org.apache.tools.ant.ProjectHelper.
   </td>
 </tr>
 <tr>
   <td><code>org.apache.tools.ant.ArgumentProcessor</code></td>
   <td>classname (optional)</td>
   <td>specifies the classname to use as ArgumentProcessor. The class must extend
       org.apache.tools.ant.ArgumentProcessor.
   </td>
 </tr>
 
 <tr>
   <td><code>websphere.home</code></td>
   <td>path</td>
   <td>Points to home directory of websphere.
       see <a href="Tasks/ejb.html#ejbjar_websphere">EJB Tasks</a>
   </td>
 </tr>
 <tr>
   <td><code>XmlLogger.file</code></td>
   <td>filename (default 'log.xml')</td>
   <td>Name for the logfile for <a href="listeners.html#MailLogger">MailLogger</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.project-helper-repo.debug</code></td>
   <td>boolean (default 'false')</td>
   <td>Set it to true to enable debugging with Ant's
   <a href="projecthelper.html#repository">ProjectHelper internal repository</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.argument-processor-repo.debug</code></td>
   <td>boolean (default 'false')</td>
   <td>Set it to true to enable debugging with Ant's
   <a href="argumentprocessor.html#repository">ArgumentProcessor internal repository</a>.
   </td>
 </tr>
 </table>
 
 <p>
 If new properties get added (it happens), expect them to appear under the
 "ant." and "org.apache.tools.ant" prefixes, unless the developers have a
 very good reason to use another prefix. Accordingly, please avoid using
 properties that begin with these prefixes. This protects you from future
 Ant releases breaking your build file.
 </p>
 <h3>return code</h3>
 <p>the ant start up scripts (in their Windows and Unix version) return
 the return code of the java program. So a successful build returns 0,
 failed builds return other values.
 </p>
 
 <h2><a name="cygwin">Cygwin Users</a></h2>
 <p>The Unix launch script that come with Ant works correctly with Cygwin. You
 should not have any problems launching Ant from the Cygwin shell. It is
 important to note, however, that once Ant is running it is part of the JDK
 which operates as a native Windows application. The JDK is not a Cygwin
 executable, and it therefore has no knowledge of Cygwin paths, etc. In
 particular when using the <code>&lt;exec&gt;</code> task, executable names such
 as &quot;/bin/sh&quot; will not work, even though these work from the Cygwin
 shell from which Ant was launched. You can use an executable name such as
 &quot;sh&quot; and rely on that command being available in the Windows path.
 </p>
 
 <h2><a name="os2">OS/2 Users</a></h2>
 <p>The OS/2 launch script was developed to perform complex tasks. It has two parts:
 <code>ant.cmd</code> which calls Ant and <code>antenv.cmd</code> which sets the environment for Ant.
 Most often you will just call <code>ant.cmd</code> using the same command line options as described
 above. The behaviour can be modified by a number of ways explained below.</p>
 
 <p>Script <code>ant.cmd</code> first verifies whether the Ant environment is set correctly. The
 requirements are:</p>
 <ol>
 <li>Environment variable <code>JAVA_HOME</code> is set.</li>
 <li>Environment variable <code>ANT_HOME</code> is set.</li>
 <li>Environment variable <code>CLASSPATH</code> is set and contains at least one element from
 <code>JAVA_HOME</code> and at least one element from <code>ANT_HOME</code>.</li>
 </ol>
 
 <p>If any of these conditions is violated, script <code>antenv.cmd</code> is called. This script
 first invokes configuration scripts if there exist: the system-wide configuration
 <code>antconf.cmd</code> from the <code>%ETC%</code> directory and then the user configuration
 <code>antrc.cmd</code> from the <code>%HOME%</code> directory. At this moment both
 <code>JAVA_HOME</code> and <code>ANT_HOME</code> must be defined because <code>antenv.cmd</code>
 now adds <code>classes.zip</code> or <code>tools.jar</code> (depending on version of JVM) and
 everything from <code>%ANT_HOME%\lib</code> except <code>ant-*.jar</code> to
 <code>CLASSPATH</code>. Finally <code>ant.cmd</code> calls per-directory configuration
 <code>antrc.cmd</code>. All settings made by <code>ant.cmd</code> are local and are undone when the
 script ends. The settings made by <code>antenv.cmd</code> are persistent during the lifetime of the
 shell (of course unless called automatically from <code>ant.cmd</code>). It is thus possible to call
 <code>antenv.cmd</code> manually and modify some settings before calling <code>ant.cmd</code>.</p>
 
 <p>Scripts <code>envset.cmd</code> and <code>runrc.cmd</code> perform auxiliary tasks. All scripts
 have some documentation inside.</p>
 
 <h2><a name="background">Running Ant as a background process on
     Unix(-like) systems</a></h2>
 
 <p>If you start Ant as a background process (like in <code>ant
     &amp;</code>) and the build process creates another process, Ant will
     immediately try to read from standard input, which in turn will
     most likely suspend the process.  In order to avoid this, you must
     redirect Ant's standard input or explicitly provide input to each
     spawned process via the input related attributes of the
     corresponding tasks.</p>
 
 <p>Tasks that create such new processes
   include <code>&lt;exec&gt;</code>, <code>&lt;apply&gt;</code>
   or <code>&lt;java&gt;</code> when the <code>fork</code> attribute is
   <code>true</code>.</p>
 
 <h2><a name="viajava">Running Ant via Java</a></h2>
 <p>If you have installed Ant in the do-it-yourself way, Ant can be started
 from one of two entry points:</p>
 <blockquote>
   <pre>java -Dant.home=c:\ant org.apache.tools.ant.Main [options] [target]</pre>
 </blockquote>
 
 <blockquote>
   <pre>java -Dant.home=c:\ant org.apache.tools.ant.launch.Launcher [options] [target]</pre>
 </blockquote>
 
 <p>
 The first method runs Ant's traditional entry point. The second method uses
 the Ant Launcher introduced in Ant 1.6. The former method does not support
 the -lib option and all required classes are loaded from the CLASSPATH. You must
 ensure that all required jars are available. At a minimum the CLASSPATH should
 include:
 </p>
 
 <ul>
 <li><code>ant.jar</code> and <code>ant-launcher.jar</code></li>
 <li>jars/classes for your XML parser</li>
 <li>the JDK's required jar/zip files</li>
 </ul>
 
 <p>
 The latter method supports the -lib, -nouserlib, -noclasspath options and will
     load jars from the specified ANT_HOME. You should start the latter with the most minimal
 classpath possible, generally just the ant-launcher.jar.
 </p>
 
 <a name="viaant"/>
 
 Ant can be started in Ant via the <code>&lt;java&gt;</code> command.
 Here is an example:
 
 <pre>
 &lt;java
         classname="org.apache.tools.ant.launch.Launcher"
         fork="true"
         failonerror="true"
         dir="${sub.builddir}"
         timeout="4000000"
         taskname="startAnt"&gt;
     &lt;classpath&gt;
         &lt;pathelement location="${ant.home}/lib/ant-launcher.jar"/&gt;
     &lt;/classpath&gt;
     &lt;arg value="-buildfile"/&gt;
     &lt;arg file="${sub.buildfile}"/&gt;
     &lt;arg value="-Dthis=this"/&gt;
     &lt;arg value="-Dthat=that"/&gt;
     &lt;arg value="-Dbasedir=${sub.builddir}"/&gt;
     &lt;arg value="-Dthe.other=the.other"/&gt;
     &lt;arg value="${sub.target}"/&gt;
 &lt;/java&gt;
 </pre>
 <br>
 
 
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/Main.java b/src/main/org/apache/tools/ant/Main.java
index b9492acb1..a9c23a53a 100644
--- a/src/main/org/apache/tools/ant/Main.java
+++ b/src/main/org/apache/tools/ant/Main.java
@@ -12,1306 +12,1306 @@
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Properties;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.launch.AntMain;
 import org.apache.tools.ant.listener.SilentLogger;
 import org.apache.tools.ant.property.GetProperty;
 import org.apache.tools.ant.property.ResolvePropertyMap;
 import org.apache.tools.ant.util.ClasspathUtils;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.ProxySetup;
 
 
 /**
  * Command line entry point into Ant. This class is entered via the
  * canonical `public static void main` entry point and reads the
  * command line arguments. It then assembles and executes an Ant
  * project.
  * <p>
  * If you integrating Ant into some other tool, this is not the class
  * to use as an entry point. Please see the source code of this
  * class to see how it manipulates the Ant project classes.
  *
  */
 public class Main implements AntMain {
 
     /**
      * A Set of args that are handled by the launcher and should
      * not be seen by Main.
      */
     private static final Set<String> LAUNCH_COMMANDS = Collections
             .unmodifiableSet(new HashSet<String>(Arrays.asList("-lib", "-cp", "-noclasspath",
                     "--noclasspath", "-nouserlib", "-main")));
 
     /** The default build file name. {@value} */
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
     private final Vector<String> targets = new Vector<String>();
 
     /** Set of properties that can be used by tasks. */
     private final Properties definedProps = new Properties();
 
     /** Names of classes to add as listeners to project. */
     private final Vector<String> listeners = new Vector<String>(1);
 
     /** File names of property files to load on startup. */
     private final Vector<String> propertyFiles = new Vector<String>(1);
 
     /** Indicates whether this build is to support interactive input */
     private boolean allowInput = true;
 
     /** keep going mode */
     private boolean keepGoingMode = false;
 
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
      * Whether or not log output should be reduced to the minimum
      */
     private boolean silent = false;
 
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
      * optional thread priority
      */
     private Integer threadPriority = null;
 
     /**
      * proxy flag: default is false
      */
     private boolean proxy = false;
 
     private final Map<Class<?>, List<String>> extraArguments = new HashMap<Class<?>, List<String>>();
 
     private static final GetProperty NOPROPERTIES = new GetProperty() {
         public Object getProperty(final String aName) {
             // No existing property takes precedence
             return null;
         }
     };
 
 
 
 
     /**
      * Prints the message of the Throwable if it (the message) is not
      * <code>null</code>.
      *
      * @param t Throwable to print the message of.
      *          Must not be <code>null</code>.
      */
     private static void printMessage(final Throwable t) {
         final String message = t.getMessage();
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
     public static void start(final String[] args, final Properties additionalUserProperties,
                              final ClassLoader coreLoader) {
         final Main m = new Main();
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
     public void startAnt(final String[] args, final Properties additionalUserProperties,
                          final ClassLoader coreLoader) {
 
         try {
             processArgs(args);
         } catch (final Throwable exc) {
             handleLogfile();
             printMessage(exc);
             exit(1);
             return;
         }
 
         if (additionalUserProperties != null) {
             for (final Enumeration<?> e = additionalUserProperties.keys();
                     e.hasMoreElements();) {
                 final String key = (String) e.nextElement();
                 final String property = additionalUserProperties.getProperty(key);
                 definedProps.put(key, property);
             }
         }
 
         // expect the worst
         int exitCode = 1;
         try {
             try {
                 runBuild(coreLoader);
                 exitCode = 0;
             } catch (final ExitStatusException ese) {
                 exitCode = ese.getStatus();
                 if (exitCode != 0) {
                     throw ese;
                 }
             }
         } catch (final BuildException be) {
             if (err != System.err) {
                 printMessage(be);
             }
         } catch (final Throwable exc) {
             exc.printStackTrace();
             printMessage(exc);
         } finally {
             handleLogfile();
         }
         exit(exitCode);
     }
 
     /**
      * This operation is expected to call {@link System#exit(int)}, which
      * is what the base version does.
      * However, it is possible to do something else.
      * @param exitCode code to exit with
      */
     protected void exit(final int exitCode) {
         System.exit(exitCode);
     }
 
     /**
      * Close logfiles, if we have been writing to them.
      *
      * @since Ant 1.6
      */
     private static void handleLogfile() {
         if (isLogFileUsed) {
             FileUtils.close(out);
             FileUtils.close(err);
         }
     }
 
     /**
      * Command line entry point. This method kicks off the building
      * of a project object and executes a build using either a given
      * target or the default target.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      */
     public static void main(final String[] args) {
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
      *
      * @deprecated since 1.6.x
      */
     @Deprecated
     protected Main(final String[] args) throws BuildException {
         processArgs(args);
     }
 
     /**
      * Process command line arguments.
      * When ant is started from Launcher, launcher-only arguments do not get
      * passed through to this routine.
      *
      * @param args the command line arguments.
      *
      * @since Ant 1.6
      */
     private void processArgs(final String[] args) {
         String searchForThis = null;
         boolean searchForFile = false;
         PrintStream logTo = null;
 
         // cycle through given args
 
         boolean justPrintUsage = false;
         boolean justPrintVersion = false;
         boolean justPrintDiagnostics = false;
 
         final ArgumentProcessorRegistry processorRegistry = ArgumentProcessorRegistry.getInstance();
 
         for (int i = 0; i < args.length; i++) {
             final String arg = args[i];
 
             if (arg.equals("-help") || arg.equals("-h")) {
                 justPrintUsage = true;
             } else if (arg.equals("-version")) {
                 justPrintVersion = true;
             } else if (arg.equals("-diagnostics")) {
                 justPrintDiagnostics = true;
             } else if (arg.equals("-quiet") || arg.equals("-q")) {
                 msgOutputLevel = Project.MSG_WARN;
             } else if (arg.equals("-verbose") || arg.equals("-v")) {
                 msgOutputLevel = Project.MSG_VERBOSE;
             } else if (arg.equals("-debug") || arg.equals("-d")) {
                 msgOutputLevel = Project.MSG_DEBUG;
             } else if (arg.equals("-silent") || arg.equals("-S")) {
                 silent = true;
             } else if (arg.equals("-noinput")) {
                 allowInput = false;
             } else if (arg.equals("-logfile") || arg.equals("-l")) {
                 try {
                     final File logFile = new File(args[i + 1]);
                     i++;
                     logTo = new PrintStream(new FileOutputStream(logFile));
                     isLogFileUsed = true;
                 } catch (final IOException ioe) {
                     final String msg = "Cannot write on the specified log file. "
                         + "Make sure the path exists and you have write "
                         + "permissions.";
                     throw new BuildException(msg);
                 } catch (final ArrayIndexOutOfBoundsException aioobe) {
                     final String msg = "You must specify a log file when "
                         + "using the -log argument";
                     throw new BuildException(msg);
                 }
             } else if (arg.equals("-buildfile") || arg.equals("-file")
                        || arg.equals("-f")) {
                 i = handleArgBuildFile(args, i);
             } else if (arg.equals("-listener")) {
                 i = handleArgListener(args, i);
             } else if (arg.startsWith("-D")) {
                 i = handleArgDefine(args, i);
             } else if (arg.equals("-logger")) {
                 i = handleArgLogger(args, i);
             } else if (arg.equals("-inputhandler")) {
                 i = handleArgInputHandler(args, i);
             } else if (arg.equals("-emacs") || arg.equals("-e")) {
                 emacsMode = true;
             } else if (arg.equals("-projecthelp") || arg.equals("-p")) {
                 // set the flag to display the targets and quit
                 projectHelp = true;
             } else if (arg.equals("-find") || arg.equals("-s")) {
                 searchForFile = true;
                 // eat up next arg if present, default to build.xml
                 if (i < args.length - 1) {
                     searchForThis = args[++i];
                 }
             } else if (arg.startsWith("-propertyfile")) {
                 i = handleArgPropertyFile(args, i);
             } else if (arg.equals("-k") || arg.equals("-keep-going")) {
                 keepGoingMode = true;
             } else if (arg.equals("-nice")) {
                 i = handleArgNice(args, i);
             } else if (LAUNCH_COMMANDS.contains(arg)) {
                 //catch script/ant mismatch with a meaningful message
                 //we could ignore it, but there are likely to be other
                 //version problems, so we stamp down on the configuration now
                 final String msg = "Ant's Main method is being handed "
                         + "an option " + arg + " that is only for the launcher class."
                         + "\nThis can be caused by a version mismatch between "
                         + "the ant script/.bat file and Ant itself.";
                 throw new BuildException(msg);
             } else if (arg.equals("-autoproxy")) {
                 proxy = true;
             } else if (arg.startsWith("-")) {
                 boolean processed = false;
                 for (final ArgumentProcessor processor : processorRegistry.getProcessors()) {
                     final int newI = processor.readArguments(args, i);
                     if (newI != -1) {
                         List<String> extraArgs = extraArguments.get(processor.getClass());
                         if (extraArgs == null) {
                             extraArgs = new ArrayList<String>();
                             extraArguments.put(processor.getClass(), extraArgs);
                         }
                         for (; i < newI && i < args.length; i++) {
                             extraArgs.add(args[i]);
                         }
                         processed = true;
                         break;
                     }
                 }
                 if (!processed) {
                     // we don't have any more args to recognize!
                     final String msg = "Unknown argument: " + arg;
                     System.err.println(msg);
                     printUsage();
                     throw new BuildException("");
                 }
             } else {
                 // if it's no other arg, it may be the target
                 targets.addElement(arg);
             }
         }
 
         if (msgOutputLevel >= Project.MSG_VERBOSE || justPrintVersion) {
             printVersion(msgOutputLevel);
         }
 
         if (justPrintUsage || justPrintVersion || justPrintDiagnostics) {
             if (justPrintUsage) {
                 printUsage();
             }
             if (justPrintDiagnostics) {
                 Diagnostics.doReport(System.out, msgOutputLevel);
             }
             return;
         }
 
         // if buildFile was not specified on the command line,
         if (buildFile == null) {
             // but -find then search for it
             if (searchForFile) {
                 if (searchForThis != null) {
                     buildFile = findBuildFile(System.getProperty("user.dir"), searchForThis);
                     if (buildFile == null) {
                         throw new BuildException("Could not locate a build file!");
                     }
                 } else {
                     // no search file specified: so search an existing default file
                     final Iterator<ProjectHelper> it = ProjectHelperRepository.getInstance().getHelpers();
                     do {
                         final ProjectHelper helper = it.next();
                         searchForThis = helper.getDefaultBuildFile();
                         if (msgOutputLevel >= Project.MSG_VERBOSE) {
                             System.out.println("Searching the default build file: " + searchForThis);
                         }
                         buildFile = findBuildFile(System.getProperty("user.dir"), searchForThis);
                     } while (buildFile == null && it.hasNext());
                     if (buildFile == null) {
                         throw new BuildException("Could not locate a build file!");
                     }
                 }
             } else {
                 // no build file specified: so search an existing default file
                 final Iterator<ProjectHelper> it = ProjectHelperRepository.getInstance().getHelpers();
                 do {
                     final ProjectHelper helper = it.next();
                     buildFile = new File(helper.getDefaultBuildFile());
                     if (msgOutputLevel >= Project.MSG_VERBOSE) {
                         System.out.println("Trying the default build file: " + buildFile);
                     }
                 } while (!buildFile.exists() && it.hasNext());
             }
         }
 
         // make sure buildfile exists
         if (!buildFile.exists()) {
             System.out.println("Buildfile: " + buildFile + " does not exist!");
             throw new BuildException("Build failed");
         }
 
         if (buildFile.isDirectory()) {
             final File whatYouMeant = new File(buildFile, "build.xml");
             if (whatYouMeant.isFile()) {
                 buildFile = whatYouMeant;
             } else {
                 System.out.println("What? Buildfile: " + buildFile + " is a dir!");
                 throw new BuildException("Build failed");
             }
         }
 
         // Normalize buildFile for re-import detection
         buildFile =
             FileUtils.getFileUtils().normalize(buildFile.getAbsolutePath());
 
         // Load the property files specified by -propertyfile
         loadPropertyFiles();
 
         if (msgOutputLevel >= Project.MSG_INFO) {
             System.out.println("Buildfile: " + buildFile);
         }
 
         if (logTo != null) {
             out = logTo;
             err = logTo;
             System.setOut(out);
             System.setErr(err);
         }
         readyToRun = true;
     }
 
     // --------------------------------------------------------
     //    Methods for handling the command line arguments
     // --------------------------------------------------------
 
     /** Handle the -buildfile, -file, -f argument */
     private int handleArgBuildFile(final String[] args, int pos) {
         try {
             buildFile = new File(
                 args[++pos].replace('/', File.separatorChar));
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must specify a buildfile when using the -buildfile argument");
         }
         return pos;
     }
 
     /** Handle -listener argument */
     private int handleArgListener(final String[] args, int pos) {
         try {
             listeners.addElement(args[pos + 1]);
             pos++;
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             final String msg = "You must specify a classname when "
                 + "using the -listener argument";
             throw new BuildException(msg);
         }
         return pos;
     }
 
     /** Handler -D argument */
     private int handleArgDefine(final String[] args, int argPos) {
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
         final String arg = args[argPos];
         String name = arg.substring(2, arg.length());
         String value = null;
         final int posEq = name.indexOf("=");
         if (posEq > 0) {
             value = name.substring(posEq + 1);
             name = name.substring(0, posEq);
         } else if (argPos < args.length - 1) {
             value = args[++argPos];
         } else {
             throw new BuildException("Missing value for property "
                                      + name);
         }
         definedProps.put(name, value);
         return argPos;
     }
 
     /** Handle the -logger argument. */
     private int handleArgLogger(final String[] args, int pos) {
         if (loggerClassname != null) {
             throw new BuildException(
                 "Only one logger class may be specified.");
         }
         try {
             loggerClassname = args[++pos];
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must specify a classname when using the -logger argument");
         }
         return pos;
     }
 
     /** Handle the -inputhandler argument. */
     private int handleArgInputHandler(final String[] args, int pos) {
         if (inputHandlerClassname != null) {
             throw new BuildException("Only one input handler class may "
                                      + "be specified.");
         }
         try {
             inputHandlerClassname = args[++pos];
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException("You must specify a classname when"
                                      + " using the -inputhandler"
                                      + " argument");
         }
         return pos;
     }
 
     /** Handle the -propertyfile argument. */
     private int handleArgPropertyFile(final String[] args, int pos) {
         try {
             propertyFiles.addElement(args[++pos]);
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             final String msg = "You must specify a property filename when "
                 + "using the -propertyfile argument";
             throw new BuildException(msg);
         }
         return pos;
     }
 
     /** Handle the -nice argument. */
     private int handleArgNice(final String[] args, int pos) {
         try {
             threadPriority = Integer.decode(args[++pos]);
         } catch (final ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must supply a niceness value (1-10)"
                 + " after the -nice option");
         } catch (final NumberFormatException e) {
             throw new BuildException("Unrecognized niceness value: "
                                      + args[pos]);
         }
 
         if (threadPriority.intValue() < Thread.MIN_PRIORITY
             || threadPriority.intValue() > Thread.MAX_PRIORITY) {
             throw new BuildException(
                 "Niceness value is out of the range 1-10");
         }
         return pos;
     }
 
     // --------------------------------------------------------
     //    other methods
     // --------------------------------------------------------
 
     /** Load the property files specified by -propertyfile */
     private void loadPropertyFiles() {
         for (final String filename : propertyFiles) {
             final Properties props = new Properties();
             FileInputStream fis = null;
             try {
                 fis = new FileInputStream(filename);
                 props.load(fis);
             } catch (final IOException e) {
                 System.out.println("Could not load property file "
                                    + filename + ": " + e.getMessage());
             } finally {
                 FileUtils.close(fis);
             }
 
             // ensure that -D properties take precedence
             final Enumeration<?> propertyNames = props.propertyNames();
             while (propertyNames.hasMoreElements()) {
                 final String name = (String) propertyNames.nextElement();
                 if (definedProps.getProperty(name) == null) {
                     definedProps.put(name, props.getProperty(name));
                 }
             }
         }
     }
 
     /**
      * Helper to get the parent file for a given file.
      * <p>
      * Added to simulate File.getParentFile() from JDK 1.2.
      * @deprecated since 1.6.x
      *
      * @param file   File to find parent of. Must not be <code>null</code>.
      * @return       Parent file or null if none
      */
     @Deprecated
     private File getParentFile(final File file) {
         final File parent = file.getParentFile();
 
         if (parent != null && msgOutputLevel >= Project.MSG_VERBOSE) {
             System.out.println("Searching in " + parent.getAbsolutePath());
         }
 
         return parent;
     }
 
     /**
      * Search parent directories for the build file.
      * <p>
      * Takes the given target as a suffix to append to each
      * parent directory in search of a build file.  Once the
      * root of the file-system has been reached <code>null</code>
      * is returned.
      *
      * @param start  Leaf directory of search.
      *               Must not be <code>null</code>.
      * @param suffix  Suffix filename to look for in parents.
      *                Must not be <code>null</code>.
      *
      * @return A handle to the build file if one is found, <code>null</code> if not
      */
     private File findBuildFile(final String start, final String suffix) {
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
                 return null;
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
     private void runBuild(final ClassLoader coreLoader) throws BuildException {
 
         if (!readyToRun) {
             return;
         }
 
         final ArgumentProcessorRegistry processorRegistry = ArgumentProcessorRegistry.getInstance();
 
         for (final ArgumentProcessor processor : processorRegistry.getProcessors()) {
             final List<String> extraArgs = extraArguments.get(processor.getClass());
             if (extraArgs != null) {
                 if (processor.handleArg(extraArgs)) {
                     return;
                 }
             }
         }
 
         final Project project = new Project();
         project.setCoreLoader(coreLoader);
 
         Throwable error = null;
 
         try {
             addBuildListeners(project);
             addInputHandler(project);
 
             final PrintStream savedErr = System.err;
             final PrintStream savedOut = System.out;
             final InputStream savedIn = System.in;
 
             // use a system manager that prevents from System.exit()
             SecurityManager oldsm = null;
             oldsm = System.getSecurityManager();
 
                 //SecurityManager can not be installed here for backwards
                 //compatibility reasons (PD). Needs to be loaded prior to
                 //ant class if we are going to implement it.
                 //System.setSecurityManager(new NoExitSecurityManager());
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
 
                 // set the thread priorities
                 if (threadPriority != null) {
                     try {
                         project.log("Setting Ant's thread priority to "
                                 + threadPriority, Project.MSG_VERBOSE);
                         Thread.currentThread().setPriority(threadPriority.intValue());
                     } catch (final SecurityException swallowed) {
                         //we cannot set the priority here.
                         project.log("A security manager refused to set the -nice value");
                     }
                 }
 
                 setProperties(project);
 
                 project.setKeepGoingMode(keepGoingMode);
                 if (proxy) {
                     //proxy setup if enabled
                     final ProxySetup proxySetup = new ProxySetup(project);
                     proxySetup.enableProxies();
                 }
 
                 for (final ArgumentProcessor processor : processorRegistry.getProcessors()) {
                     final List<String> extraArgs = extraArguments.get(processor.getClass());
                     if (extraArgs != null) {
                         processor.prepareConfigure(project, extraArgs);
                     }
                 }
 
                 ProjectHelper.configureProject(project, buildFile);
 
                 for (final ArgumentProcessor processor : processorRegistry.getProcessors()) {
                     final List<String> extraArgs = extraArguments.get(processor.getClass());
                     if (extraArgs != null) {
                         if (processor.handleArg(project, extraArgs)) {
                             return;
                         }
                     }
                 }
 
                 if (projectHelp) {
                     printDescription(project);
                     printTargets(project, msgOutputLevel > Project.MSG_INFO,
                             msgOutputLevel > Project.MSG_VERBOSE);
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
                 if (oldsm != null) {
                     System.setSecurityManager(oldsm);
                 }
 
                 System.setOut(savedOut);
                 System.setErr(savedErr);
                 System.setIn(savedIn);
             }
         } catch (final RuntimeException exc) {
             error = exc;
             throw exc;
         } catch (final Error e) {
             error = e;
             throw e;
         } finally {
             if (!projectHelp) {
                 try {
                     project.fireBuildFinished(error);
                 } catch (final Throwable t) {
                     // yes, I know it is bad style to catch Throwable,
                     // but if we don't, we lose valuable information
                     System.err.println("Caught an exception while logging the"
                                        + " end of the build.  Exception was:");
                     t.printStackTrace();
                     if (error != null) {
                         System.err.println("There has been an error prior to"
                                            + " that:");
                         error.printStackTrace();
                     }
                     throw new BuildException(t);
                 }
             } else if (error != null) {
                 project.log(error.toString(), Project.MSG_ERR);
             }
         }
     }
 
     private void setProperties(final Project project) {
 
         project.init();
 
         // resolve properties
         final PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(project);
         @SuppressWarnings({ "rawtypes", "unchecked" })
         final Map raw = new HashMap(definedProps);
         @SuppressWarnings("unchecked")
         final Map<String, Object> props = raw;
 
         final ResolvePropertyMap resolver = new ResolvePropertyMap(project,
                 NOPROPERTIES, propertyHelper.getExpanders());
         resolver.resolveAllProperties(props, null, false);
 
         // set user-define properties
         for (final Entry<String, Object> ent : props.entrySet()) {
             final String arg = ent.getKey();
             final Object value = ent.getValue();
             project.setUserProperty(arg, String.valueOf(value));
         }
 
         project.setUserProperty(MagicNames.ANT_FILE,
                                 buildFile.getAbsolutePath());
         project.setUserProperty(MagicNames.ANT_FILE_TYPE,
                                 MagicNames.ANT_FILE_TYPE_FILE);
     }
 
     /**
      * Adds the listeners specified in the command line arguments,
      * along with the default listener, to the specified project.
      *
      * @param project The project to add listeners to.
      *                Must not be <code>null</code>.
      */
     protected void addBuildListeners(final Project project) {
 
         // Add the default listener
         project.addBuildListener(createLogger());
 
         final int count = listeners.size();
         for (int i = 0; i < count; i++) {
             final String className = listeners.elementAt(i);
             final BuildListener listener =
                     (BuildListener) ClasspathUtils.newInstance(className,
                             Main.class.getClassLoader(), BuildListener.class);
             project.setProjectReference(listener);
 
             project.addBuildListener(listener);
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
     private void addInputHandler(final Project project) throws BuildException {
         InputHandler handler = null;
         if (inputHandlerClassname == null) {
             handler = new DefaultInputHandler();
         } else {
             handler = (InputHandler) ClasspathUtils.newInstance(
                     inputHandlerClassname, Main.class.getClassLoader(),
                     InputHandler.class);
             project.setProjectReference(handler);
         }
         project.setInputHandler(handler);
     }
 
     // TODO: (Jon Skeet) Any reason for writing a message and then using a bare
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
         if (silent) {
             logger = new SilentLogger();
             msgOutputLevel = Project.MSG_WARN;
             emacsMode = true;
         } else if (loggerClassname != null) {
             try {
                 logger = (BuildLogger) ClasspathUtils.newInstance(
                         loggerClassname, Main.class.getClassLoader(),
                         BuildLogger.class);
             } catch (final BuildException e) {
                 System.err.println("The specified logger class "
                     + loggerClassname
                     + " could not be used because " + e.getMessage());
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
         System.out.println("ant [options] [target [target2 [target3] ...]]");
         System.out.println("Options: ");
-        System.out.println("  -help, -h              print this message");
-        System.out.println("  -projecthelp, -p       print project help information");
+        System.out.println("  -help, -h              print this message and exit");
+        System.out.println("  -projecthelp, -p       print project help information and exit");
         System.out.println("  -version               print the version information and exit");
         System.out.println("  -diagnostics           print information that might be helpful to");
-        System.out.println("                         diagnose or report problems.");
+        System.out.println("                         diagnose or report problems and exit");
         System.out.println("  -quiet, -q             be extra quiet");
         System.out.println("  -silent, -S            print nothing but task outputs and build failures");
         System.out.println("  -verbose, -v           be extra verbose");
         System.out.println("  -debug, -d             print debugging information");
         System.out.println("  -emacs, -e             produce logging information without adornments");
         System.out.println("  -lib <path>            specifies a path to search for jars and classes");
         System.out.println("  -logfile <file>        use given file for log");
         System.out.println("    -l     <file>                ''");
         System.out.println("  -logger <classname>    the class which is to perform logging");
         System.out.println("  -listener <classname>  add an instance of class as a project listener");
         System.out.println("  -noinput               do not allow interactive input");
         System.out.println("  -buildfile <file>      use given buildfile");
         System.out.println("    -file    <file>              ''");
         System.out.println("    -f       <file>              ''");
         System.out.println("  -D<property>=<value>   use value for given property");
         System.out.println("  -keep-going, -k        execute all targets that do not depend");
         System.out.println("                         on failed target(s)");
         System.out.println("  -propertyfile <name>   load all properties from file with -D");
         System.out.println("                         properties taking precedence");
         System.out.println("  -inputhandler <class>  the class which will handle input requests");
         System.out.println("  -find <file>           (s)earch for buildfile towards the root of");
         System.out.println("    -s  <file>           the filesystem and use it");
         System.out.println("  -nice  number          A niceness value for the main thread:"
                 + "                         1 (lowest) to 10 (highest); 5 is the default");
         System.out.println("  -nouserlib             Run ant without using the jar files from"
                 + "                         ${user.home}/.ant/lib");
         System.out.println("  -noclasspath           Run ant without using CLASSPATH");
         System.out.println("  -autoproxy             Java1.5+: use the OS proxy settings");
         System.out.println("  -main <class>          override Ant's normal entry point");
         for (final ArgumentProcessor processor : ArgumentProcessorRegistry.getInstance().getProcessors()) {
             processor.printUsage(System.out);
         }
     }
 
     /**
      * Prints the Ant version information to <code>System.out</code>.
      *
      * @exception BuildException if the version information is unavailable
      */
     private static void printVersion(final int logLevel) throws BuildException {
         System.out.println(getAntVersion());
     }
 
     /**
      * Cache of the Ant version information when it has been loaded.
      */
     private static String antVersion = null;
 
     /**
      * Cache of the short Ant version information when it has been loaded.
      */
     private static String shortAntVersion = null;
 
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
                 final Properties props = new Properties();
                 final InputStream in =
                     Main.class.getResourceAsStream("/org/apache/tools/ant/version.txt");
                 props.load(in);
                 in.close();
                 shortAntVersion = props.getProperty("VERSION");
 
                 final StringBuffer msg = new StringBuffer();
                 msg.append("Apache Ant(TM) version ");
                 msg.append(shortAntVersion);
                 msg.append(" compiled on ");
                 msg.append(props.getProperty("DATE"));
                 antVersion = msg.toString();
             } catch (final IOException ioe) {
                 throw new BuildException("Could not load the version information:"
                                          + ioe.getMessage());
             } catch (final NullPointerException npe) {
                 throw new BuildException("Could not load the version information.");
             }
         }
         return antVersion;
     }
 
     /**
      * Returns the short Ant version information, if available. Once the information
      * has been loaded once, it's cached and returned from the cache on future
      * calls.
      *
      * @return the short Ant version information as a String
      *         (always non-<code>null</code>)
      *
      * @throws BuildException BuildException if the version information is unavailable
      * @since Ant 1.9.3
      */
     public static String getShortAntVersion() throws BuildException {
         if (shortAntVersion == null) {
             getAntVersion();
         }
         return shortAntVersion;
     }
 
      /**
       * Prints the description of a project (if there is one) to
       * <code>System.out</code>.
       *
       * @param project The project to display a description of.
       *                Must not be <code>null</code>.
       */
     private static void printDescription(final Project project) {
        if (project.getDescription() != null) {
           project.log(project.getDescription());
        }
     }
 
     /**
      * Targets in imported files with a project name
      * and not overloaded by the main build file will
      * be in the target map twice. This method
      * removes the duplicate target.
      * @param targets the targets to filter.
      * @return the filtered targets.
      */
     private static Map<String, Target> removeDuplicateTargets(final Map<String, Target> targets) {
         final Map<Location, Target> locationMap = new HashMap<Location, Target>();
         for (final Entry<String, Target> entry : targets.entrySet()) {
             final String name = entry.getKey();
             final Target target = entry.getValue();
             final Target otherTarget = locationMap.get(target.getLocation());
             // Place this entry in the location map if
             //  a) location is not in the map
             //  b) location is in map, but its name is longer
             //     (an imported target will have a name. prefix)
             if (otherTarget == null
                 || otherTarget.getName().length() > name.length()) {
                 locationMap.put(
                     target.getLocation(), target); // Smallest name wins
             }
         }
         final Map<String, Target> ret = new HashMap<String, Target>();
         for (final Target target : locationMap.values()) {
             ret.put(target.getName(), target);
         }
         return ret;
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
     private static void printTargets(final Project project, boolean printSubTargets,
             final boolean printDependencies) {
         // find the target with the longest name
         int maxLength = 0;
         final Map<String, Target> ptargets = removeDuplicateTargets(project.getTargets());
         // split the targets in top-level and sub-targets depending
         // on the presence of a description
         final Vector<String> topNames = new Vector<String>();
         final Vector<String> topDescriptions = new Vector<String>();
         final Vector<Enumeration<String>> topDependencies = new Vector<Enumeration<String>>();
         final Vector<String> subNames = new Vector<String>();
         final Vector<Enumeration<String>> subDependencies = new Vector<Enumeration<String>>();
 
         for (final Target currentTarget : ptargets.values()) {
             final String targetName = currentTarget.getName();
             if (targetName.equals("")) {
                 continue;
             }
             final String targetDescription = currentTarget.getDescription();
             // maintain a sorted list of targets
             if (targetDescription == null) {
                 final int pos = findTargetPosition(subNames, targetName);
                 subNames.insertElementAt(targetName, pos);
                 if (printDependencies) {
                     subDependencies.insertElementAt(currentTarget.getDependencies(), pos);
                 }
             } else {
                 final int pos = findTargetPosition(topNames, targetName);
                 topNames.insertElementAt(targetName, pos);
                 topDescriptions.insertElementAt(targetDescription, pos);
                 if (targetName.length() > maxLength) {
                     maxLength = targetName.length();
                 }
                 if (printDependencies) {
                     topDependencies.insertElementAt(currentTarget.getDependencies(), pos);
                 }
             }
         }
 
         printTargets(project, topNames, topDescriptions, topDependencies,
                 "Main targets:", maxLength);
         //if there were no main targets, we list all subtargets
         //as it means nothing has a description
         if (topNames.size() == 0) {
             printSubTargets = true;
         }
         if (printSubTargets) {
             printTargets(project, subNames, null, subDependencies, "Other targets:", 0);
         }
 
         final String defaultTarget = project.getDefaultTarget();
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
     private static int findTargetPosition(final Vector<String> names, final String name) {
         final int size = names.size();
         int res = size;
         for (int i = 0; i < size && res == size; i++) {
             if (name.compareTo(names.elementAt(i)) < 0) {
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
      * @param topDependencies The list of dependencies for each target.
      *                        The dependencies are listed as a non null
      *                        enumeration of String.
      * @param heading The heading to display.
      *                Should not be <code>null</code>.
      * @param maxlen The maximum length of the names of the targets.
      *               If descriptions are given, they are padded to this
      *               position so they line up (so long as the names really
      *               <i>are</i> shorter than this).
      */
     private static void printTargets(final Project project, final Vector<String> names,
                                      final Vector<String> descriptions, final Vector<Enumeration<String>> dependencies,
                                      final String heading,
                                      final int maxlen) {
         // now, start printing the targets and their descriptions
         final String lSep = System.getProperty("line.separator");
         // got a bit annoyed that I couldn't find a pad function
         String spaces = "    ";
         while (spaces.length() <= maxlen) {
             spaces += spaces;
         }
         final StringBuilder msg = new StringBuilder();
         msg.append(heading + lSep + lSep);
         final int size = names.size();
         for (int i = 0; i < size; i++) {
             msg.append(" ");
             msg.append(names.elementAt(i));
             if (descriptions != null) {
                 msg.append(
                     spaces.substring(0, maxlen - names.elementAt(i).length() + 2));
                 msg.append(descriptions.elementAt(i));
             }
             msg.append(lSep);
             if (!dependencies.isEmpty()) {
                 final Enumeration<String> deps = dependencies.elementAt(i);
                 if (deps.hasMoreElements()) {
                     msg.append("   depends on: ");
                     while (deps.hasMoreElements()) {
                         msg.append(deps.nextElement());
                         if (deps.hasMoreElements()) {
                             msg.append(", ");
                         }
                     }
                     msg.append(lSep);
                 }
             }
         }
         project.log(msg.toString(), Project.MSG_WARN);
     }
 }
