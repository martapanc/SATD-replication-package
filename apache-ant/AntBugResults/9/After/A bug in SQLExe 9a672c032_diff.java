diff --git a/WHATSNEW b/WHATSNEW
index 0d621a252..f02e95d93 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1067 +1,1070 @@
 Changes from current Ant 1.6.5 version to current RCS version
 =============================================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Use alternative names for the command line arguments in javac. Bugzilla
   Report 37546.
 
 * Use org.apache.log4j.Logger instead of org.apache.log4j.Category.
   Category has been deprecated for ~2 years and has been removed from
   the log4j code.  Logger was introduced in log4j 1.2 so users of
   log4j 1.1 and log4j 1.0 need to upgrade to a newer version of log4j.
   Bugzilla Report 31951.
 
 * build.sysclasspath now also affects the bootclasspath handling of
   spawned Java VMs.  If you set build.sysclasspath to anything other
   than "ignore" (or leave it unset, since "ignore" is the default when
   it comes to bootclasspath handling), then the bootclasspath of the
   VM running Ant will be added to the bootclasspath you've specified.
 
 * The Reference class now has a project field that will get
   used (if set) in preference to the passed in project, when
   dereferencing the reference. Bugzilla Report 25777.
 
 * On DOS and Netware, filenames beginning with a drive letter
   and followed by a colon but with no directory separator following
   the colon are no longer (incorrectly) accepted as absolute pathnames
   by FileUtils.normalize() and FileUtils.isAbsolutePath().  Netware
   volumes can still be specified without an intervening separator.
   UNC pathnames on Windows must include a server and share name, i.e.
   "\\a\b" to be considered valid absolute paths.
   
 * The <java fork="false"> now as per default installs a security manager
   using the default permissions. This is now independent of the 
   failonerror attribute.
   Bugzilla report 33361.
 
 * <signjar> now notices when the jar and signedjar are equal, and switches
   to the same dependency logic as when signedjar is omitted. This may break
   something that depended upon signing in this situation. However, since
   invoking the JDK jarsigner program with -signedjar set to the source jar
   actually crashes the JVM on our (Java1.5) systems, we don't think any
   build files which actually worked will be affected by the change.
 
 * <signjar> used to ignore a nested fileset when a jar was also provided
   as an attribute, printing a warning message; now it signs files in the
   fileset.
 
 * An improved method of handling timestamp granularity differences between 
   client and server was added to the <ftp> task.  FTP servers typically
   have HH:mm timestamps whereas local filesystems have HH:mm:ss timestamps.
   Previously, this required tweaking with the timediffmillis attribute 
   which also was used to handle timezone differences.  Now, there is a new 
   timestampgranularity attribute.  The default value for get operations is 0
   since the user has the more powerful preservelastmodified attribute to work 
   with.  Since this is not available on put operations the default value 
   adds a minute to the server timestamp in order to account for this,
   Scripts which previously used timediffmillis to do this compensation may 
   need to be rewritten.  timediffmillis has now been deprecated.
 
 * On Java1.5+, Ant automatically sets the system property
   java.net.useSystemProxies to true, which gives it automatic use of the local
   IE (Windows) or Gnome2 (Unix/Linux) proxy settings. This may break any build
   file that somehow relied on content outside the firewall being unreachable:
   use the -noproxy command-line option to disable this new feature.
 
+* A bug in SQLExec would prevent the execution of trailing,
+  non-semicolon-delimited statements.  Bugzilla Report 37764.
+
 Fixed bugs:
 -----------
 
 * Some potential NullPointerExceptions, Bugzilla Reports 37765 and 38056
 
 * Problem when adding multiple filter files, Bugzilla Report 37341
 
 * problem refering jars specfied by Class-Path attribute in manifest
   of a ant task jar file, when this ant task jar file is located in
   a directory with space, Bugzilla Report 37085 
 
 * Backward incompatible change in ZipFileSet, Bugzilla Report 35824.
 
 * Wrong replacement of file separator chars prevens junitbatchtest
   from running correctly on files from a zipfileset. Bugzilla Report 35499
 
 * Calling close twice on ReaderInputStream gave a nullpointer exception.
   Bugzilla Report 35544.
 
 * Memory leak from IntrospectionHelper.getHelper(Class) in embedded
   environments. Bugzilla Report 30162.
 
 * Translate task does not remove tokens when a key is not found.
   It logs a verbose message.  Bugzilla Report 13936.
 
 * Incorrect task name with invalid "javac" task after a "presetdef".
   Bugzilla reports 31389 and 29499.
   
 * <manifest> was not printing warnings about invalid manifest elements.
   Bugzilla report 32190
 
 * <replace> got out of memory on large files (part of report 32566). 
   <replace> can now handle files as long as there is enough disk space
   available.
 
 * Commandline.describeCommand() methods would attempt to describe
   arguments even when none, other than the executable name, were present.
 
 * Create signjar's helper ExecTask instance directly rather than by
   typedef discovery mechanisms. Bugzilla report 33433.
 
 * FileUtils.resolveFile() promised to return absolute files but
   did not always do so.
   
 * <ftp> failed to retrieve a file when the path towards the file contained
   an element starting with . Bugzilla report 33770
   
 * "<rmic> always compiles on Java1.5" bugzilla report=33862. Fixed default
   stub version to always be "compat", even on Java1.5+. 
 
 * The .NET compilation tasks failed if filenames given as references
   contained spaces.  Bugzilla Report 27170.
 
 * SQL task would try access result sets of statements that didn't
   return any, causing problems with Informix IDS 9.2 and IBM DB2 8.1
   FixPak 6 (or later). Bugzilla Reports 27162 and 29954.
 
 * Task.init() was called twice for most tasks.  Bugzilla Report 34411.
 
 * JavaTest testcases sometimes fail on windows. Bugzilla Report 34502.
 
 * Targets with identical name work in imported project. Bugzilla Report 34566.
 
 * DemuxOutputStream now uses a WeakHashMap to store the thread-stream mapping,
   to avoid holding on to thread references after they terminate.
 
 * <xmlvalidate> and <schemavalidate> create a new parser for every file in a
   fileset, and so validate multiple files properly. Bugzilla Report 32791
   
 * <tar> / <untar> now accepts files upto 8GB, <tar> gives an error if larger 
   files are to be included. This is the POSIX size limit.
 
 * <junitreport> removed line-breaks from stack-traces.  Bugzilla
   Report 34963.
 
 * off-by-one error in environment setup for execution under OpenVMS fixed.
 
 * Bugzilla report 36171: -noclasspath crashes ant if no system classpath is set.
 
 * <pvcs> used wrong switch for retrieving revisions by label.
   Bugzilla Report 36359.
 
 * <sshexec> closed System.out, disabling output on second and subsequent
   invocations.  Bugzilla report 36302.
 
 * <cvschangelog> was crashing with CVS versions >= 1.12.x due to change in
   the date format. Bugzilla report 30962.
 
 * The same IntrospectionHelper instance was contineously added as a listener
   to project. Bugzilla report 37184.
 
 * FileUtils.toURI() was not encoding non ASCII characters to ASCII,
   causing impossibility to process XML entities referenced by XML
   documents in non ASCII paths. Bugzilla report 37348.
 
 * > 1 ssh invocations to a given host would fail. Bugzilla report 36207.
 
 * EmailTask was eating SMTP error messages. Bugzilla report 37547.
 
 * PropertySet API setMapper(...) didn't properly set up the Mapper.
   Bugzilla report 37760.
 
 * Proper return code for ant.bat. Bugzilla report 13655.
 
 * Project not set on ChainReaderHelpers used by the Redirector.
   Bugzilla report 37958.
 
 Other changes:
 --------------
 * Minor performance improvements Bugzilla report 37777
 
 * New task <manifestclasspath> converts a path into a property
   suitable as the value for a manifest's Class-Path attribute.
 
 * Fixed references to obsoleted CVS web site. Bugzilla Report 36854.
 
 * Log fine-grained events at verbose level from JUnit. Bugzilla report 31885.
 
 * <WsdlToDotnet> and <style> are now deprecated in favor of <wsdltodotnet> and
   <xslt>, respectively. Bugzilla report 25832.
 
 * <echoproperties> now (alphanumerically) sorts the property list
   before echoing. Bugzilla report 18976.
 
 * A new base class DispatchTask has been added to facilitate elegant 
   creation of tasks with multiple actions.
 
 * Major revision of <wsdltodotnet>. Supports mono wsdl and the microsoft
   wsdl run on mono, as well as most of the .NET WSE2.0 options. Extra
   schemas (files or urls) can be named in the <schema> element.
   Compilers can be selected using the compiler attribute, which defaults
   to "microsoft" on windows, and "mono" on everything else.
 
 * It is now possible to specify the pattern created/parsed by <checksum>.
   Bugzilla Report 16539.
 
 * Added a new "failall" value for the onerror attribute of <typedef>.
   Bugzilla report 31685.
 
 * New task <libraries> can retrieve library files from a maven repository.
 
 * unzip/unwar/unjar/untar now supports a nested mapper, which lets you unzip
   in useful ways.
 
 * Junit task -- display suite first.
   Bugzilla report 31962.
 
 * Added isSigned condition and signedselector selector
   Bugzilla report 32126.
 
 * Added preserveLastModified attribute to signjar task.
   Bugzilla report 30987.
 
 * Added <scriptcondition> condition, for inline scripted conditions
 
 * Added <xor> condition for exclusive-or combining of nested conditions.
 
 * Added <scriptselector> selector for scripted file selection
 
 * ant -diagnostics lists contents of ${user.home}/.ant/lib , and
   checks that the java.io.tmpdir directory exists and is writeable. 
 
 * mail task accepts nested header element.  Bugzilla report 24713.
 
 * zip/jar/war/ear supports level attribute for deflate compression level.
   Bugzilla report 25513.
 
 * Added loginputstring attribute to the redirector type.
 
 * Tighten security by sending storepass and keypass to signjar
   via the input stream of the forked process.
 
 * New task <schemavalidate> extends <xmlvalidate> with extra support
   for XML Schema (XSD) files.
 
 * <fixcrlf> supports a file attribute for easy fixup of a single file.
 
 * New condition <parsersupports> which can look for XML parser feature or
   property support in the parser Ant is using.
 
 * fixcrlf can be used in a filterchain.
 
 * <sync> has a new nested element <preserveInTarget> that can be used
   to protect extra-content in the target directory.  Bugzilla Report
   21832.
   
 * <signjar> now supports:
   -nested filesets at the same time as the jar attribute
   -a destDir attribute with the appropriate dependency logic, which
    can be used with the jar attribute or nested filesets
   -a mapper to permit filename remapping on signing
   -tsaurl and tsacert attributes for timestamped JAR signing
   -nested <sysproperty> elements, which can be used for proxy setup and the like
 
 * The linecontains and linecontainsregexp filterreaders now support a
   negate attribute to select lines -not- containing specified text.
   Bugzilla Report 34374.
 
 * <os> condition adds "winnt" as a family which can be tested. This is
   all windows platforms other than the Win9x line or Windows CE.  
 
 * <exec> (and hence, <apply> and any other derived classes) have an OsFamily
   attribute, which can restrict execution to a single OS family.
 
 * added "backtrace" attribute to macrodef. Bugzilla report 27219.
 
 * Ant main provides some diagnostics if it ever sees a -cp or -lib option,
   as this is indicative of a script mismatch. Bugzilla report 34860
 
 * <junitreport> prints a special message if supplied an empty XML File. This
   can be caused by the test JVM exiting during a test, either via a
   System.exit() call or a JVM crash.
 
 * project name is now used for *all* targets so one can write consistent import
   build files. Bugzilla report 28444.
 
 * New condition <typefound> that can be used to probe for the declaration 
   and implementation of a task, type, preset, macro, scriptdef, whatever. 
   As it tests for the implementation, it can be used to check for optional
   tasks being available. 
 
 * check for 1.5.* Ant main class. (weblogic.jar in classpath reports)
 
 * New condition <isfailure> that tests the return-code of an executable. This
   contains platform-specific logic and is better than comparing the result with
   "0".
 
 * Added initial support for Resource Collections, including the
   resourcecount task.
 
 * property attribute of pathconvert is now optional. If omitted the
   result will be written to the log.
 
 * New mapper, <scriptmapper>, supports scripted mapping of source files/strings
   to destination strings.
 
 * Add the echoxml task. This will echo nested XML to a file, with
   the normal <?xml ?> processor instruction. UTF-8 encoding only; no-namespace
   support.
 
 * Try to make subprojects of custom Project subclasses instances of the
   same type. Bugzilla report 17901.
 
 * <ssh> and <scp> support keyboard-interactive authentication now.
 
 * <javadoc> now supports -breakiterator for custom doclets if Ant is
   running on JSE 5.0 or higher.  Bugzilla Report: 34580.
 
 * New logger, TimestampedLogger, that prints the wall time that a build
   finished/failed. Use with
   -logger org.apache.tools.ant.listener.TimestampedLogger
   
 * <junitreport> now generates pages alltests-errors.html and
   alltests-fails.html, that list only the errors and failures, respectively.
   Bugzilla Report: 36226
 
 * New task <makeurl> that can turn a file reference into an absolute file://
   url; and nested filesets/paths into a (space, comma, whatever) separated
   list of URLs. Useful for RMI classpath setup, amongst other things.
 
 * <xslt> now accepts nested FileNameMappers e.g. <globmapper>.
   Bugzilla report 37604.
 
 * New task loadresource that accompanies loadfile for non file resources.
 
 * <echo> now supports an encoding when saving to a file.
 
 * new GreedyInputHandler added.
 
 * add textfile attribute to the filesmatch condition.
 
 * new resourcesmatch condition.
 
 * added the onmissingfiltersfile attribute to filterset. Bugzilla report 19845.
 
 * added the inline handler element to the input task.
 
 Changes from Ant 1.6.4 to Ant 1.6.5
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 Fixed bugs:
 -----------
 
 * <move> was unable to replace existing files or write into
   existing directories.  Bugzilla report 34962.
 
 * <macrodef> with redefined default values was incorrect. (Fix for
    31215 had a bug). Bugzilla report 35109.
 
 * <javadoc> will convert backslashes to forwardslashes when generating file
   list by useexternalfile. Bugzilla report 27814.
 
 Changes from Ant 1.6.3 to Ant 1.6.4
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 * <ftp> task has had a number of changes.  Uptodate calculation previously 
   did not call a file uptodate if the source timestamp and the destination 
   timestamp were equal. Bugzilla report 34941.  Any script that attempted
   to compensate for this by using the timediffmillis attribute might need
   to be tweaked.  
   
 
 Fixed bugs:
 -----------
 
 * Sun javah failed with java.lang.NoClassDefFoundError.
   Bugzilla report 34681.
 
 * DirectoryScanner.slowScan() was broken. Bugzilla report 34722.
 
 * DirectoryScanner.scan() could throw a NullPointerException on
   case-insensitive filesystems (read Windows or MacOS X).
 
 * Get w/authentication failed with ArrayOutOfBoundsExceptions.
   Bugzilla report 34734.
 
 * Granularity attribute for <sync> task was undocumented.
   Bugzilla report 34871.
 
 * <unzip> and <untar> could leave file handles open on invalid
   archives.  Bugzilla report 34893.
 
 * propertyset threw NPE with nested, mapped propertysets.
 
 Other changes:
 --------------
 
 * AntXMLContext.setCurrentTargets() is now public. Bugzilla report 34680. 
 
 Changes from Ant 1.6.2 to Ant 1.6.3
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * The subant task used the canonical version of a file path. This
   has been changed to use the absolute path. Bugzilla 30438.
 
 * Tar now writes two EOF blocks rather than one.
   Bugzilla report 28776
 
 * The Reference object now has a project field which it uses in preference
   to the project passed in. This allows composite references to be
   handled to nested projects.
   Bugzilla report 25777
 
 * <junit> with filtertrace="true" will now also swallow lines for the
   sun.reflect package.  If you need to see them in your stack trace,
   you must set filtertrace to false.
   Bugzilla Report 22758
 
 * The jikes compiler adapter now supports -bootclasspath, -extdirs and
   -sourcepath and also uses the same logic for debug flags as javac.
   This means, the jikes compiler adapter now requires Jikes 1.15 or later.
   Bugzilla Reports 25868, 26404 and 32609.
 
 * The gcj compiler adapter used to include the Java runtime classes
   even if includeJavaRuntime was set to false, unless the
   bootclasspath has been specified as well.  It will now always adhere
   to includeJavaRuntime, you may need to set it to true explicitly now
   if you relied on the old behavior.
 
 Other changes:
 --------------
 
 * <javadoc> can now take an attribute 'executable'. Bugzilla report 30606.
 
 * New attribute ignorecontents for <different> selector
 
 * Javadoc fixes for Location, Project, and RuntimeConfigurable
   Bugzilla 30160.
 
 * Enable to choose the regexp implementation without system property.
   Bugzilla Report 15390.
 
 * Expose objects and methods in IntrospectionHelper. Bugzilla Report 30794.
 
 * Allow file attribute of <move> to rename a directory.
   Bugzilla Report 22863.
 
 * Add xmlcatalog nested element to XmlProperty. Bugzilla report 27053.
 
 * New attribute alwayslog for <redirector> type.
 
 * Added <target> nested elements to <ant> and <antcall> to allow
   specification of multiple sub-build targets, which are executed
   with a single dependency analysis.
 
 * Refactored Target invocation into org.apache.tools.ant.Executor
   implementations.  Bugzilla Reports 21421, 29248.
 
 * <rmic> now also supports Kaffe's rmic version shipping with Kaffe
   1.1.2 and above.
 
 * added casesensitive attribute to <globmapper> and <regexpmapper>
   Bugzilla report 16686
 
 * added handledirsep attribute to <globmapper> and <regexpmapper>
   Bugzilla report 32487
 
 * added a new mapper <filtermapper>
 
 * When a BuildListener tried to access System.err or System.out, Ant
   would have thrown an exception - this has been changed.  Ant now
   silently ignores the message.  BuildListeners still should avoid
   accessing either stream.
 
 * Added a comment attribute to the zip task.
   Bugzilla report 22793.
 
 * Overloaded FileUtils.createNewFile with a boolean mkdirs attribute
   to create nonexistent parent directories.
 
 * <apply> has a new "force" attribute that, when true, disables
   checking of target files.
 
 * Made the dest attribute of the apply task optional; mapped target
   filenames will be interpreted as absolute pathnames when dest is omitted.
 
 * Changed default tempdir for <javac> from user.dir to java.io.tmpdir.
 
 * Added searchpath attribute to <exec> for searching path variable(s)
   when resolveexecutable = true.
 
 * Added revision and userid attributes to <pvcs> documentation.
 
 * Added support to the touch task for a mkdirs attribute to create
   nonexistent parent directories before touching new files.
 
 * Added support to the touch task for a pattern attribute to allow
   alternate datetime formats.
 
 * Added support to the touch task to map touched files using a nested
   mapper element.
 
 * Added support to the touch task for a verbose attribute to suppress
   logging of new file creation.
 
 * bad link in docs to the enhancement page in bugzilla.
   Bugzilla report 33252.
 
 * Added length task to get strings' and files' lengths.
 
 * <native2ascii> and <javah> now also support Kaffe's versions.
 
 * Recursive token expansion in a filterset can now be disabled by
   setting its recurse attribute to false.
 
 * Pathconvert no longer requires that one of (targetos|pathsep|dirsep)
   be set; platform defaults are used when this is the case.
 
 * Added preservelastmodified attribute to fixcrlf task. Bugzilla 25770.
 
 * Added isfileselected condition.
 
 * Added verbose="true|false" attribute to <subant>. When verbose is enabled,
   the directory name is logged on entry and exit of the sub-build.
   Bugzilla 33787.
 
 * Added -nouserlib option to allow running ant without automatically loading
   up ${user.home}/.lib/ant. This is useful when compiling ant, and antlibs.
   Modified the build.sh and build.bat to use the option.
 
 * Added -noclasspath option to allow running ant WITHOUT using CLASSPATH env
   variable. Modified ant.bat to do this so that %CLASSPATH% is not looked at.
 
 * Add else attribute to the condition task, which specifies an
   optional alternate value to set the property to if the nested
   condition evaluates to false. Bugzilla report 33074.
 
 * Ant generated jar files should now be detected as jar files by
   Solaris.  Bugzilla Report 32649.
   
 * <rexec> with a single command should now work with unusal login
   dialogs without special read/write pairs.  Bugzilla Report 26632.
 
 * <csc>'s extraoptions can now contain multiple arguments.
   Bugzilla Report 23599.
 
 * <macrodef> with default values set by properties would be
   seen as new definitions when called twice with different properties.
   This was confusing so the definitions are now treated as similar.
   Bugzilla Report 31215.
 
 * <javadoc> has a new attribute "includenosourcepackages" that can be
   used to document packages that don't hold source files but a
   package.html file.  Bugzilla Report 25339.
 
 * <rpm> has new attributes failonerror and quiet.
 
 * Added two tutorials
   - beginner: introduction into Ant
   - task developers: using path, fileset etc
 
 * a number of new attributes that allow the user to handle non-standard 
   server listing formats and time zone differences have been added in 
   the <ftp> task.
 
 
 Fixed bugs:
 -----------
 
 * Do not pass on ThreadDeath when halting <java fork="false">. Bugzilla
   32941.
 
 * Killing a thread running <java fork="true"> (e.g. from an IDE) would
   not stop the forked process. Bugzilla 31928.
 
 * Programs run with <java fork="true"> can now accept standard input
   from the Ant console.  (Programs run with <java fork="false"> could
   already do so.)  Bugzilla 24918.
 
 * AbstractCvsTask prematurely closed its outputStream and errorStream.
   Bugzilla 30097.
 
 * Impossible to use implicit classpath for <taskdef>
   when Ant core loader != Java application loader and
   Path.systemClassPath taken from ${java.class.path} Bugzilla 30161.
 
 * MacroInstance did not clean up nested elements correctly in the execute 
   method, causing multiple use of the same macro instance with nested
   elements to fail.
 
 * checksum fileext property doc wrong. Bugzilla 30787.
 
 * FTP task, getTimeDiff method was returning wrong value. Bugzilla 30595.
 
 * make sure that Zip and its derivates call the createEmptyZip method when
  there are no resources to zip/jar/...
 
 * Zip task was not zipping when only empty directories were found.
   Bugzilla 30365.
 
 * Jar task was not including manifest files when duplicate="preserve" was
   chosen. Bugzilla 32802.
 
 * ant.bat was missing runAntNoClasspath label for goto.
   Bugzilla 34510.
 
 * Classpath was treated in the same way as -lib options. Bugzilla 28046.
 
 * Manual page for cvsversion contained incorrect attributes and did not
   say since 1.6.1. Bugzilla 31408.
 
 * Typo in definition of <cvsversion> task causing it not to be defined.
   Bugzilla 31403.
 
 * Execution of top level tasks in imported files get delayed by targets.
   Bugzilla report 31487.
 
 * ExecTask executes checkConfiguration() even though os does not match.
   Bugzilla report 31805.
 
 * Concat task instance could not be run twice.
   Bugzilla report 31814.
 
 * NPE using XmlLogger and antlib.
   Bugzilla report 31840.
 
 * Properties.propertyNames() should be used instead of .keys().
   Bugzilla report 27261.
 
 * Target location is not set for default target.
   Bugzilla report 32267.
 
 * Incorrect classloader parent in junittask when using with
   ant-junit.jar and junit.jar not in the project classloader. Bugzilla
   report 28474.
 
 * getResources() on the classloader returned by ClasspathUtils would
   see each resource twice - if the resource is in the project
   classpath and if the classloader is requested with a null path.
 
 * XMLValidate used URL#getFile rather than the ant method FileUtils#fromURI
   Bugzilla report 32508
 
 * fixed Regexp-Mapper docs which gave outdated instructions (optional.jar)
   Bugzilla report 28584
 
 * <scp> using <fileset> didn't work with OpenSSH 3.9 and later.
   Bugzilla report 31939
 
 * <setproxy> failed to set user/password on some JDKs.
   Bugzilla report 32667
 
 * untar would go into infinite loop for some invalid tar files.
   Bugzilla report 29877
 
 * forked <javac> won't pass -source to a JDK 1.1 or 1.2 javac anymore.
   Bugzilla report 32948
 
 * propertyset references did not handle nested propertyset references.
 
 * oata.types.Description.getDescription(Project) would throw a
   NullPointerException when the "ant.targets" reference was unset.
 
 * Wrapper scripts did not detect WINNT value of dynamic OS environment
   variable when logged into workstations using Novell authentication.
   Bugzilla Report 30366.
 
 * DependScanner.getResource() always returned nonexistent resources,
   even when the resource actually existed.  Bugzilla Report 30558.
 
 * <apply> was broken with classfilesets.  Bugzilla Report 30567.
 
 * <available> returned false positives when checking a file
   passed in with the current basedir leading twice:
   e.g. ${basedir}${file.separator}${basedir}${file.separator}foo .
 
 * The first file open that took place when using input files with the
   <exec>, <apply>, or <java> tasks was always logged to System.out
   instead of to the managing Task.
 
 * <telnet> and <rexec> would try to disconnect from servers they never
   connetced to, potentially leading to exceptions in commons-net.
   Bugzilla Report 33618.
 
 * <zip> would drop files matched by defaultexcludes during updates.
   Bugzilla Report 33412.
 
 * <zip> couldn't store files with size between 2GB and 4GB (the
   upper limit set by the ZIP format itself).  Bugzilla Report 33310.
 
 * NPE when when <presetdef> tries to configure a task that
   cannot be instantiated. Bugzilla Report 33689.
 
 * <javac debug="false"> created an invalid command line when running
   the Symantec Java compiler.
 
 * Get with usetimestamp did not work on Java 1.2.
 
 * Get with usetimestamp did not work when local timestamp roughly >= now.
 
 * The framed JUnit report now handles multiple reports for the same
   testcase properly.  Bugzilla Report 32745.
 
 * <cab> didn't work for files with spaces in their names on Windows.
   Bugzilla Report 17182.
 
 * The VAJ tasks could fail if the project name contained characters
   that need to get URL encoded.  Bugzilla Report 23322.
 
 * TarInputStream#read() wasn't implemented correctly.  Bugzilla Report
   34097.
 
 * <xslt> failed to process file-hierarchies of more than one level if
   scanincludeddirectories was true.  Bugzilla Report 24866.
 
 * forkmode="perBatch" or "once" would ignore extension attributes that
   had been specified for <formatter>s.  Bugzilla Report 32973.
 
 * The refid attribute of the I/O redirector was not functional.
 
 Changes from Ant 1.6.1 to Ant 1.6.2
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * The import task used the canonical version of a file path. This
   has been changed to use the absolute path. Bugzilla 28505.
 
 * ant-xalan2.jar has been removed since the only class contained in it
   didn't depend on Xalan-J 2 at all.  Its sole dependency has always
   been TraX and so it has been merged into ant-trax.jar.
 
 * All exceptions thrown by tasks are now wrapped in a buildexception
   giving the location in the buildfile of the task.
 
 * Nested elements for namespaced tasks and types may belong to the
   Ant default namespace as well as the task's or type's namespace.
 
 * <junitreport> will very likely no longer work with Xalan-J 1.
 
   Note that Xalan-J 1 has been deprecated for a very long time and we
   highly recommend that you upgrade.
 
   If you really need to continue using Xalan-J 1, please copy the
   junit-frames-xalan1.xsl from the distribution's etc directory as
   junit-frames.xsl into a new directory and use the task's styledir
   attribute to point to.  This is the last version of the XSLT
   stylesheet that is expected to be compatible with Xalan-J 1.
 
 Fixed bugs:
 -----------
 
 * eliminate memory leak in AntClassLoader. Bugzilla Report 8689.
 
 * subant haltonfailure=false did not catch all failures. Bugzilla Report 27007.
 
 * macrodef @@ escaping was broken.  Bugzilla Report 27069.
 
 * MacroDef did not allow attributes named 'description'. Bugzilla Report 27175.
 
 * Throw build exception if name attribute missing from patternset#NameEntry.
   Bugzilla Report 25982.
 
 * Throw build exception if target repeated in build file, but allow targets
   to be repeated in imported files. 
 
 * <apply> didn't compare timestamps of source and targetfiles when
   using a nested <filelist>.  Bugzilla Report 26985.
 
 * tagdiff.xml was broken in ant 1.6.1. Bugzilla Report 27057.
 
 * if the basedir contained .. or . dirs, and the build file name contained
   .. or ., the basedir was set incorrectly. Bugzilla Report 26765.
 
 * regression from ant 1.5, exec task outputted two redundant trailing newlines.
   Bugzilla Report 27546.
 
 * NPE when running commons listener. Bugzilla Report 27373.
 
 * <java> swallowed the stack trace of exceptions thrown by the
   executed program if run in the same VM.
 
 * -projecthelp swallowed (configuration) errors silently.
   Bugzilla report 27732.
 
 * filterset used by filtertask doesn't respect loglevel. Bugzilla Report 27568.
 
 * wrong compare used in ProjectComponent for logging. Bugzilla Report 28070.
 
 * failOnAny attribute for <parallel> was broken. Bugzilla Report 28122.
 
 * If <javac> uses gcj and any of the nested <compilerarg>s implies
   compilation to native code (like -o or --main), Ant will not pass
   the -C switch to gcj.  This means you can now compile to native code
   with gcj which has been impossible in Ant < 1.6.2.
 
 * <import optional="false"> and <import optional="true">
   behaved identically.
 
 * <xslt> now sets the context classloader if you've specified a nested
   <classpath>.  Bugzilla Report 24802.
 
 * <zip> and friends would delete the original file when trying to update
   a read-only archive.  Bugzilla Report 28419.
 
 * <junit> and <assertions> are working together. Bugzilla report 27218
 
 * AntClassLoader#getResource could return invalid URLs.  Bugzilla
   Report 28060.
 
 * Ant failed to locate tools.jar if the jre directory name wasn't all
   lowercase.  Bugzilla Report 25798.
 
 * Redirector exhibited inconsistent behavior with regard to split
   output.  When sent to file only, files would be created in all
   cases; when split file-property, files were only created if
   writes were performed.
 
 * fixed case handling of scriptdef attributes and elements.
 
 * UNC pathnames did not work for ANT_HOME or -lib locations on Windows.
   Bugzilla report 27922.
 
 * replacestring tokenfilter only replaced the first occurrence.
 
 * AntLikeTasksAtTopLevelTest failed on cygwin.
 
 * I/O-intensive processes hung when executed via <exec spawn="true">.
   Bugzilla reports 23893/26852.
 
 * JDependTask did not close an output file. Bugzilla Report 28557.
 
 * Using <macrodef> could break XmlLogger. Bugzilla Report 28993.
 
 * <genkey> no longer requires keytool to be in your PATH.  Bugzilla
   Report 29382.
 
 * <symlink> could create cyclic links.  Bugzilla Report 25181.
 
 * <zip whenempty="skip"> didn't work in a common situation.  Bugzilla
   Report 22865.
 
 * <scp> now properly handles remote files and directories with spaces
   in their names.  Bugzilla Report 26097.
 
 * <scp> now has (local|remote)tofile attributes to rename files on the
   fly.  Bugzilla Report 26758.
 
 * <telnet> and <rexec> didn't close the session.  Bugzilla Report 25935.
 
 * <subant> and XmlLogger didn't play nicley together.
 
 Other changes:
 --------------
 * doc fix concerning the dependencies of the ftp task
   Bugzilla Report 29334.
 
 * <xmlvalidate> has now a property nested element,
   allowing to set string properties for the parser
   Bugzilla Report 23395.
 
 * Docs fixes for xmlvalidate.html, javadoc.html, starteam.
   Bugzilla Reports 27092, 27284, 27554.
 
 * <pathconvert> now accepts nested <mapper>s.  Bugzilla Report 26364.
 
 * Shipped XML parser is now Xerces-J 2.6.2.
 
 * Added nested file element to filelist.
 
 * spelling fixes, occurred. Bugzilla Report 27282.
 
 * add uid and gid to tarfileset. Bugzilla Report 19120.
 
 * <scp> has a verbose attribute to get some feedback during the
   transfer and new [local|remote][File|Todir] alternatives to file and
   todir that explicitly state the direction of the transfer.
 
 * The OS/2 wrapper scripts have been adapted to use the new launcher.
   Bugzilla Report 28226.
 
 * <sshexec> now also captures stderr output.  Bugzilla Report 28349.
 
 * <xslt> now supports a nested <mapper>.  Bugzilla Report 11249.
 
 * <touch> has filelist support.
 
 * <nice> task lets you set the priority of the current thread; non-forking
   <java> code will inherit this priority in their main thread.
 
 * New attribute "negate" on <propertyset> to invert selection criteria.
 
 * Target now supports a Location member.  Bugzilla Report 28599.
 
 * New "pattern" attribute for <date> selector.
 
 * <junit> has a new forkmode attribute that controls the number of
   Java VMs that get created when forking tests.  This allows you to
   run all tests in a single forked JVM reducing the overhead of VM
   creation a lot.  Bugzilla Report 24697.
 
 * <jar> can now optionally create an index for jars different than the
   one it currently builds as well.  See the new <indexjars> element
   for details.  Bugzilla Report 14255.
 
 * Permit building under JDK 1.5. Bugzilla Report 28996.
 
 * minor Javadoc changes. Bugzilla Report 28998.
 
 * Misc. corrections in SignJar.java. Bugzilla Report 28999.
 
 * Remove redundant <hr> from javah.html. Bugzilla Report 28995.
 
 * Ignore built distributions. Bugzilla Report 28997.
 
 * A new roundup attribute on <zip> and related task can be used to
   control whether the file modification times inside the archive will
   be rounded up or down (since zips only store modification times with
   a granularity of two seconds).  The default remains to round up.
   Bugzilla Report 17934.
 
 * A binary option has been added to <concat>. Bugzilla Report 26312.
 
 * Added DynamicConfiguratorNS, an namespace aware version of
   DynamicConfigurator. Bugzilla Report 28436.
 
 * Add implicit nested element to <macrodef>. Bugzilla Report 25633.
 
 * Add deleteonexit attribute to <delete>.
 
 * Added Target.getIf/Unless().  Bugzilla Report 29320.
 
 * <fail> has a status attribute that can be used to pass an exit
   status back to the command line.
 
 * <fail> accepts a nested <condition>.
 
 * <loadproperties> supports loading from a resource.
   Bugzilla Report 28340.
 
 * Nested file mappers and a container mapper implementation have been
   introduced.  Additionally, the <mapper> element now accepts "defined"
   nested FileNameMapper implementations directly, allowing a usage
   comparable to those of <condition>, <filter>, and <selector>.
 
 * New <redirector> type introduced to provide extreme I/O flexibility.
   Initial support for <exec>, <apply>, and <java> tasks.
 
 * <apply> has a new ignoremissing attribute (default true for BC)
   which will allow nonexistent files specified via <filelist>s to
   be passed to the executable.  Bugzilla Report 29585.
 
 * <junitreport> now also works with Xalan XSLTC and/or JDK 1.5.
   Bugzilla Report 27541.
 
 * <jspc> doesn't work properly with Tomcat 5.x.  We've implemented a
   work-around but don't intend to support future changes in Tomcat
   5.x.  Please use the jspc task that ships with Tomcat instead of
   Ant's.
 
 Changes from Ant 1.6.0 to Ant 1.6.1
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * License is now Apache Software License 2.0
   see http://www.apache.org/licenses/ for more information
 
 Fixed bugs:
 -----------
 * Remove a recursive template call in the junit xsls that could trigger a stack
   overflow. It now uses Xalan extensions to call a Java class directly.
   Bugzilla Report 19301
   
 * Fix spurious infinite loop detection for filters (introduced in ant 1.6.0).
   Bugzilla Report 23154.
 
 * Fix handling of default ant namespace for nested elements.
 
 * Fix jboss element of ejb task (introduced in ant 1.6.0).
 
 * <whichresource> failed to load classes correctly.
 
 * Ant could fail to start with a NullPointerException if
   ANT_HOME/lib/ant-launcher.jar was part of the system CLASSPATH.
 
 * presetdef'ed types did not work with the ant-type attribute
 
 * fixed case handling of macrodef attributes and elements. Bugzilla
   Reports 25687 and 26225.
 
 * <java> ignored the append attribute, Bugzilla Report 26137.
 
 * The gcj compiler adapter for <javac> failed if the destination
   directory didn't exist.  Bugzilla Report 25856.
 
 * Ant now fails with a more useful message if a new process will be
   forked in a directory and that directory doesn't exist.
 
 * <splash> used to break the build on non-GUI environments.  Bugzilla
   report 11482.
 
 * Ant 1.6.0 cannot run build scripts in directories with non-ASCII names.
   Bugzilla Report 26642.
 
 Other changes:
 --------------
 * Shipped XML parser is now Xerces-J 2.6.1
 
 * Translate task logs a debug message specifying the number of files
   that it processed.  Bugzilla Report 13938.
 
 * <fixcrlf> has a new attribute - fixlast. Bugzilla Report 23262.
 
 * <p4submit> has 2 new attributes, needsresolveproperty and changeproperty.
   Bugzilla Report 25711.
 
 * add description attributes to macrodef attributes and elements.
   Bugzilla Report 24711.
 
 * Extending ClearCase Tasks :
  - Added an extra option to 'failonerr' to each ClearCase task/command.
  - Extended the functionality of cccheckout. It can check (notco) to see if
   the desired element is already checked out to the current view. Thus it
    won't attempt to check it out again.
  - Added three new ClearCase commands: ccmkattr, ccmkdir, ccmkelem
   Bugzilla Report 26253.
 
 * added nested text support to <macrodef>
   
 * added initial support for Java 1.5.  Java 1.5 is now correctly
   detected by Ant and treated just like Java 1.4.  You can now specify
   source="1.5" in the <javac> task.
 
 * created new task <cvsversion>
 
 * added support for branch logging via the tag attribute in <cvschangelog>
   Bugzilla Report 13510.
 
 * added support the groovy language in the script and scriptdef tasks
 
 Changes from Ant 1.5.4 to Ant 1.6.0
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * This version of Ant can not be built with JDK 1.1 and requires at
   least Java 1.2 at runtime as well.  Compiling for a 1.1 target is
   still supported.
 
 * Targets cannot have the empty string as their name any longer.
 
 * ant.jar's manifest does no longer include a Class-Path entry, so it
   is no longer possible to run Ant via "java -jar ant.jar" without
   manually altering the CLASSPATH.  Instead of that a file
   ant-bootstrap.jar is included in the etc directory of the binary
   distribution, copy this to the lib directory and use
   "java -jar ant-bootstrap.jar" instead if you want to run Ant without
   the wrapper script (not recommended).
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index b1da95b24..385ab6191 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,731 +1,731 @@
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
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.Union;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.BufferedReader;
 import java.io.StringReader;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.Enumeration;
 import java.util.Iterator;
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
  * @since Ant 1.2
  *
  * @ant.task name="sql" category="database"
  */
 public class SQLExec extends JDBCTask {
 
     /**
      * delimiters we support, "normal" and "row"
      */
     public static class DelimiterType extends EnumeratedAttribute {
         /** The enumerated strings */
         public static final String NORMAL = "normal", ROW = "row";
         /** @return the enumerated strings */
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
     private Union resources = new Union();
 
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
      * @param srcFile the file containing the SQL command.
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text.
      * @param sql a inline string containing the SQL command.
      */
     public void addText(String sql) {
         this.sqlCommand += sql;
     }
 
     /**
      * Adds a set of files (nested fileset attribute).
      * @param set a set of files contains SQL commands, each File is run in
      *            a separate transaction.
      */
     public void addFileset(FileSet set) {
         add(set);
     }
 
     /**
      * Adds a collection of resources (nested element).
      * @param set a collection of resources containing SQL commands,
      * each resource is run in a separate transaction.
      * @since Ant 1.7
      */
     public void add(ResourceCollection rc) {
         resources.add(rc);
     }
 
     /**
      * Add a SQL transaction to execute
      * @return a Transaction to be configured.
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
      * @param delimiter the separator.
      */
     public void setDelimiter(String delimiter) {
         this.delimiter = delimiter;
     }
 
     /**
      * Set the delimiter type: "normal" or "row" (default "normal").
      *
      * <p>The delimiter type takes two values - normal and row. Normal
      * means that any occurrence of the delimiter terminate the SQL
      * command whereas with row, only a line containing just the
      * delimiter is recognized as the end of the command.</p>
      * @param delimiterType the type of delimiter - "normal" or "row".
      */
     public void setDelimiterType(DelimiterType delimiterType) {
         this.delimiterType = delimiterType.getValue();
     }
 
     /**
      * Print result sets from the statements;
      * optional, default false
      * @param print if true print result sets.
      */
     public void setPrint(boolean print) {
         this.print = print;
     }
 
     /**
      * Print headers for result sets from the
      * statements; optional, default true.
      * @param showheaders if true print headers of result sets.
      */
     public void setShowheaders(boolean showheaders) {
         this.showheaders = showheaders;
     }
 
     /**
      * Set the output file;
      * optional, defaults to the Ant log.
      * @param output the output file to use for logging messages.
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * whether output should be appended to or overwrite
      * an existing file.  Defaults to false.
      *
      * @since Ant 1.5
      * @param append if true append to an existing file.
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
 
     /**
      * Action to perform when statement fails: continue, stop, or abort
      * optional; default &quot;abort&quot;
      * @param action the action to perform on statement failure.
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
      * @param enable if true enable escape processing, default is true.
      * @since Ant 1.6
      */
     public void setEscapeProcessing(boolean enable) {
         escapeProcessing = enable;
     }
 
     /**
      * Load the sql file and then execute it
      * @throws BuildException on error.
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0
                 && resources.size() == 0) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or resource "
                                              + "collection, "
                                              + "transactions or sql statement "
                                              + "must be set!", getLocation());
                 }
             }
 
             if (srcFile != null && !srcFile.exists()) {
                 throw new BuildException("Source file does not exist!", getLocation());
             }
 
             // deal with the resources
             Iterator iter = resources.iterator();
             while (iter.hasNext()) {
                 Resource r = (Resource) iter.next();
                 // Make a transaction for each resource
                 Transaction t = createTransaction();
                 t.setSrcResource(r);
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
                             log("Committing transaction", Project.MSG_VERBOSE);
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
                 throw new BuildException(e, getLocation());
             } catch (SQLException e) {
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {
                         // ignore
                     }
                 }
                 throw new BuildException(e, getLocation());
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
      * @param reader the reader contains sql lines.
      * @param out the place to output results.
      * @throws SQLException on sql problems
      * @throws IOException on io problems
      */
     protected void runStatements(Reader reader, PrintStream out)
         throws SQLException, IOException {
         StringBuffer sql = new StringBuffer();
         String line;
 
         BufferedReader in = new BufferedReader(reader);
 
         while ((line = in.readLine()) != null) {
             if (!keepformat) {
                 line = line.trim();
             }
             line = getProject().replaceProperties(line);
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
                  && StringUtils.endsWith(sql, delimiter))
                 ||
                 (delimiterType.equals(DelimiterType.ROW)
                  && line.equals(delimiter))) {
                 execSQL(sql.substring(0, sql.length() - delimiter.length()),
                         out);
                 sql.replace(0, sql.length(), "");
             }
         }
         // Catch any statements not followed by ;
-        if (!sql.equals("")) {
+        if (sql.length() > 0) {
             execSQL(sql.toString(), out);
         }
     }
 
 
     /**
      * Exec the sql statement.
      * @param sql the SQL statement to execute
      * @param out the place to put output
      * @throws SQLException on SQL problems
      */
     protected void execSQL(String sql, PrintStream out) throws SQLException {
         // Check and ignore empty statements
         if ("".equals(sql.trim())) {
             return;
         }
 
         ResultSet resultSet = null;
         try {
             totalSql++;
             log("SQL: " + sql, Project.MSG_VERBOSE);
 
             boolean ret;
             int updateCount = 0, updateCountTotal = 0;
 
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
                         printResults(resultSet, out);
                     }
                 }
                 ret = statement.getMoreResults();
                 if (ret) {
                     updateCount = statement.getUpdateCount();
                     resultSet = statement.getResultSet();
                 }
             } while (ret);
 
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
         } finally {
             if (resultSet != null) {
                 resultSet.close();
             }
         }
     }
 
     /**
      * print any results in the statement
      * @deprecated use {@link #printResults(java.sql.ResultSet, java.io.PrintStream)
      *             the two arg version} instead.
      * @param out the place to print results
      * @throws SQLException on SQL problems.
      */
     protected void printResults(PrintStream out) throws SQLException {
         ResultSet rs = statement.getResultSet();
         try {
             printResults(rs, out);
         } finally {
             if (rs != null) {
                 rs.close();
             }
         }
     }
 
     /**
      * print any results in the result set.
      * @param rs the resultset to print information about
      * @param out the place to print results
      * @throws SQLException on SQL problems.
      * @since Ant 1.6.3
      */
     protected void printResults(ResultSet rs, PrintStream out)
         throws SQLException {
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
         /** @return the enumerated values */
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
         private Resource tSrcResource = null;
         private String tSqlCommand = "";
 
         /**
          * Set the source file attribute.
          * @param src the source file
          */
         public void setSrc(File src) {
             setSrcResource(new FileResource(src));
         }
 
         /**
          * Set the source file attribute.
          * @param src the source file
          * @since Ant 1.7
          */
         public void setSrcResource(Resource src) {
             if (tSrcResource != null) {
                 throw new BuildException("only one resource per transaction");
             }
             tSrcResource = src;
         }
 
         /**
          * Set inline text
          * @param sql the inline text
          */
         public void addText(String sql) {
             this.tSqlCommand += sql;
         }
 
         /**
          * Set the source resource.
          * @since Ant 1.7
          */
         public void addConfigured(ResourceCollection a) {
             if (a.size() != 1) {
                 throw new BuildException("only single argument resource "
                                          + "collections are supported.");
             }
             setSrcResource((Resource) a.iterator().next());
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
 
             if (tSrcResource != null) {
                 log("Executing resource: " + tSrcResource.toString(),
                     Project.MSG_INFO);
                 InputStream is = null;
                 Reader reader = null;
                 try {
                     is = tSrcResource.getInputStream();
                     reader =
                         (encoding == null) ? new InputStreamReader(is)
                         : new InputStreamReader(is, encoding);
                     runStatements(reader, out);
                 } finally {
                     FileUtils.close(is);
                     FileUtils.close(reader);
                 }
             }
         }
     }
 
 }
