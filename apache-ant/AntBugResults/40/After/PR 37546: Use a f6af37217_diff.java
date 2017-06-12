diff --git a/WHATSNEW b/WHATSNEW
index 5da586b2a..79e6730f5 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1006 +1,1009 @@
 Changes from current Ant 1.6.5 version to current CVS version
 =============================================================
 
 Changes that could break older environments:
 --------------------------------------------
 
+* Use alternative names for the command line arguments in javac. Bugzilla
+  Report 37546.
+
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
   
 
 
 Fixed bugs:
 -----------
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
 
 Other changes:
 --------------
 * Fixed references to obsoleted CVS web site. Burzilla Report 36854.
 
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
   can be caused by the test JVM exiting during a test, either via a System.exit()
   call or a JVM crash.
 
 * project name is now used for *all* targets so one can write consistent import
   build file. bugzilla report 28444.
 
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
   the normal <?xml ?> processor instruction. UTF-8 encoding only.  
 
 * Try to make subprojects of custom Project subclasses instances of the
   same type. Bugzilla report 17901.
 
 * <ssh> and <scp> support keyboard-interactive authentication now.
 
 * <javadoc> now supports -breakiterator for custom doclets if Ant is
   running on JSE 5.0 or higher.  Bugzilla Report: 34580.
 
 * New logger, TimestampedLogger, that prints the wall time that a build finished/failed
   Use with  -logger org.apache.tools.ant.listener.TimestampedLogger
   
 * <junitreport> now generates pages alltests-errors.html and alltests-fails.html, 
 that list only the errors and failures, respectively. Bugzilla Report: 36226
 
 * New task <makeurl> that can turn a file reference into an absolute file:// url; and
   nested filesets/paths into a (space, comma, whatever) separated list of URLs. Useful
   for RMI classpath setup, amongst other things. 
 
 
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
   the directory name is logged on entry and exit of the sub-build. Bugzilla 33787.
 
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index bfead7142..3b4c8a451 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,957 +1,1009 @@
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
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.GlobPatternMapper;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 
 /**
  * Compiles Java source files. This task can take the following
  * arguments:
  * <ul>
  * <li>sourcedir
  * <li>destdir
  * <li>deprecation
  * <li>classpath
  * <li>bootclasspath
  * <li>extdirs
  * <li>optimize
  * <li>debug
  * <li>encoding
  * <li>target
  * <li>depend
  * <li>verbose
  * <li>failonerror
  * <li>includeantruntime
  * <li>includejavaruntime
  * <li>source
  * <li>compiler
  * </ul>
  * Of these arguments, the <b>sourcedir</b> and <b>destdir</b> are required.
  * <p>
  * When this task executes, it will recursively scan the sourcedir and
  * destdir looking for Java source files to compile. This task makes its
  * compile decision based on timestamp.
  *
  *
  * @since Ant 1.1
  *
  * @ant.task category="java"
  */
 
 public class Javac extends MatchingTask {
 
     private static final String FAIL_MSG
         = "Compile failed; see the compiler error output for details.";
 
+    private static final String JAVAC15 = "javac1.5";
+    private static final String JAVAC14 = "javac1.4";
+    private static final String JAVAC13 = "javac1.3";
+    private static final String JAVAC12 = "javac1.2";
+    private static final String JAVAC11 = "javac1.1";
+    private static final String MODERN = "modern";
+    private static final String CLASSIC = "classic";
+    private static final String EXTJAVAC = "extJavac";
+
     private Path src;
     private File destDir;
     private Path compileClasspath;
     private Path compileSourcepath;
     private String encoding;
     private boolean debug = false;
     private boolean optimize = false;
     private boolean deprecation = false;
     private boolean depend = false;
     private boolean verbose = false;
     private String targetAttribute;
     private Path bootclasspath;
     private Path extdirs;
     private boolean includeAntRuntime = true;
     private boolean includeJavaRuntime = false;
     private boolean fork = false;
     private String forkedExecutable = null;
     private boolean nowarn = false;
     private String memoryInitialSize;
     private String memoryMaximumSize;
     private FacadeTaskHelper facade = null;
 
     protected boolean failOnError = true;
     protected boolean listFiles = false;
     protected File[] compileList = new File[0];
 
     private String source;
     private String debugLevel;
     private File tmpDir;
 
     /**
      * Javac task for compilation of Java files.
      */
     public Javac() {
+        facade = new FacadeTaskHelper(assumedJavaVersion());
+    }
+
+    private String assumedJavaVersion() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
-            facade = new FacadeTaskHelper("javac1.1");
+            return JAVAC11;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)) {
-            facade = new FacadeTaskHelper("javac1.2");
+            return JAVAC12;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
-            facade = new FacadeTaskHelper("javac1.3");
+            return JAVAC13;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4)) {
-            facade = new FacadeTaskHelper("javac1.4");
+            return JAVAC14;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5)) {
-            facade = new FacadeTaskHelper("javac1.5");
+            return JAVAC15;
         } else {
-            facade = new FacadeTaskHelper("classic");
+            return CLASSIC;
         }
     }
 
     /**
      * Get the value of debugLevel.
      * @return value of debugLevel.
      */
     public String getDebugLevel() {
         return debugLevel;
     }
 
     /**
      * Keyword list to be appended to the -g command-line switch.
      *
      * This will be ignored by all implementations except modern
      * and classic(ver >= 1.2). Legal values are none or a
      * comma-separated list of the following keywords: lines, vars,
      * and source. If debuglevel is not specified, by default, :none
      * will be appended to -g. If debug is not turned on, this attribute
      * will be ignored.
      *
      * @param v  Value to assign to debugLevel.
      */
     public void setDebugLevel(String  v) {
         this.debugLevel = v;
     }
 
     /**
      * Get the value of source.
      * @return value of source.
      */
     public String getSource() {
         return source;
     }
 
     /**
      * Value of the -source command-line switch; will be ignored
      * by all implementations except modern and jikes.
      *
      * If you use this attribute together with jikes, you must
      * make sure that your version of jikes supports the -source switch.
      * Legal values are 1.3, 1.4 and 1.5 - by default, no -source argument
      * will be used at all.
      *
      * @param v  Value to assign to source.
      */
     public void setSource(String  v) {
         this.source = v;
     }
 
     /**
      * Adds a path for source compilation.
      *
      * @return a nested src element.
      */
     public Path createSrc() {
         if (src == null) {
             src = new Path(getProject());
         }
         return src.createPath();
     }
 
     /**
      * Recreate src.
      *
      * @return a nested src element.
      */
     protected Path recreateSrc() {
         src = null;
         return createSrc();
     }
 
     /**
      * Set the source directories to find the source Java files.
      * @param srcDir the source directories as a path
      */
     public void setSrcdir(Path srcDir) {
         if (src == null) {
             src = srcDir;
         } else {
             src.append(srcDir);
         }
     }
 
     /**
      * Gets the source dirs to find the source java files.
      * @return the source directories as a path
      */
     public Path getSrcdir() {
         return src;
     }
 
     /**
      * Set the destination directory into which the Java source
      * files should be compiled.
      * @param destDir the destination director
      */
     public void setDestdir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Gets the destination directory into which the java source files
      * should be compiled.
      * @return the destination directory
      */
     public File getDestdir() {
         return destDir;
     }
 
     /**
      * Set the sourcepath to be used for this compilation.
      * @param sourcepath the source path
      */
     public void setSourcepath(Path sourcepath) {
         if (compileSourcepath == null) {
             compileSourcepath = sourcepath;
         } else {
             compileSourcepath.append(sourcepath);
         }
     }
 
     /**
      * Gets the sourcepath to be used for this compilation.
      * @return the source path
      */
     public Path getSourcepath() {
         return compileSourcepath;
     }
 
     /**
      * Adds a path to sourcepath.
      * @return a sourcepath to be configured
      */
     public Path createSourcepath() {
         if (compileSourcepath == null) {
             compileSourcepath = new Path(getProject());
         }
         return compileSourcepath.createPath();
     }
 
     /**
      * Adds a reference to a source path defined elsewhere.
      * @param r a reference to a source path
      */
     public void setSourcepathRef(Reference r) {
         createSourcepath().setRefid(r);
     }
 
     /**
      * Set the classpath to be used for this compilation.
      *
      * @param classpath an Ant Path object containing the compilation classpath.
      */
     public void setClasspath(Path classpath) {
         if (compileClasspath == null) {
             compileClasspath = classpath;
         } else {
             compileClasspath.append(classpath);
         }
     }
 
     /**
      * Gets the classpath to be used for this compilation.
      * @return the class path
      */
     public Path getClasspath() {
         return compileClasspath;
     }
 
     /**
      * Adds a path to the classpath.
      * @return a class path to be configured
      */
     public Path createClasspath() {
         if (compileClasspath == null) {
             compileClasspath = new Path(getProject());
         }
         return compileClasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Sets the bootclasspath that will be used to compile the classes
      * against.
      * @param bootclasspath a path to use as a boot class path (may be more
      *                      than one)
      */
     public void setBootclasspath(Path bootclasspath) {
         if (this.bootclasspath == null) {
             this.bootclasspath = bootclasspath;
         } else {
             this.bootclasspath.append(bootclasspath);
         }
     }
 
     /**
      * Gets the bootclasspath that will be used to compile the classes
      * against.
      * @return the boot path
      */
     public Path getBootclasspath() {
         return bootclasspath;
     }
 
     /**
      * Adds a path to the bootclasspath.
      * @return a path to be configured
      */
     public Path createBootclasspath() {
         if (bootclasspath == null) {
             bootclasspath = new Path(getProject());
         }
         return bootclasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath
      */
     public void setBootClasspathRef(Reference r) {
         createBootclasspath().setRefid(r);
     }
 
     /**
      * Sets the extension directories that will be used during the
      * compilation.
      * @param extdirs a path
      */
     public void setExtdirs(Path extdirs) {
         if (this.extdirs == null) {
             this.extdirs = extdirs;
         } else {
             this.extdirs.append(extdirs);
         }
     }
 
     /**
      * Gets the extension directories that will be used during the
      * compilation.
      * @return the extension directories as a path
      */
     public Path getExtdirs() {
         return extdirs;
     }
 
     /**
      * Adds a path to extdirs.
      * @return a path to be configured
      */
     public Path createExtdirs() {
         if (extdirs == null) {
             extdirs = new Path(getProject());
         }
         return extdirs.createPath();
     }
 
     /**
      * If true, list the source files being handed off to the compiler.
      * @param list if true list the source files
      */
     public void setListfiles(boolean list) {
         listFiles = list;
     }
 
     /**
      * Get the listfiles flag.
      * @return the listfiles flag
      */
     public boolean getListfiles() {
         return listFiles;
     }
 
     /**
      * Indicates whether the build will continue
      * even if there are compilation errors; defaults to true.
      * @param fail if true halt the build on failure
      */
     public void setFailonerror(boolean fail) {
         failOnError = fail;
     }
 
     /**
      * @ant.attribute ignore="true"
      * @param proceed inverse of failoferror
      */
     public void setProceed(boolean proceed) {
         failOnError = !proceed;
     }
 
     /**
      * Gets the failonerror flag.
      * @return the failonerror flag
      */
     public boolean getFailonerror() {
         return failOnError;
     }
 
     /**
      * Indicates whether source should be
      * compiled with deprecation information; defaults to off.
      * @param deprecation if true turn on deprecation information
      */
     public void setDeprecation(boolean deprecation) {
         this.deprecation = deprecation;
     }
 
     /**
      * Gets the deprecation flag.
      * @return the deprecation flag
      */
     public boolean getDeprecation() {
         return deprecation;
     }
 
     /**
      * The initial size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      * @param memoryInitialSize string to pass to VM
      */
     public void setMemoryInitialSize(String memoryInitialSize) {
         this.memoryInitialSize = memoryInitialSize;
     }
 
     /**
      * Gets the memoryInitialSize flag.
      * @return the memoryInitialSize flag
      */
     public String getMemoryInitialSize() {
         return memoryInitialSize;
     }
 
     /**
      * The maximum size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      * @param memoryMaximumSize string to pass to VM
      */
     public void setMemoryMaximumSize(String memoryMaximumSize) {
         this.memoryMaximumSize = memoryMaximumSize;
     }
 
     /**
      * Gets the memoryMaximumSize flag.
      * @return the memoryMaximumSize flag
      */
     public String getMemoryMaximumSize() {
         return memoryMaximumSize;
     }
 
     /**
      * Set the Java source file encoding name.
      * @param encoding the source file encoding
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Gets the java source file encoding name.
      * @return the source file encoding name
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Indicates whether source should be compiled
      * with debug information; defaults to off.
      * @param debug if true compile with debug information
      */
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     /**
      * Gets the debug flag.
      * @return the debug flag
      */
     public boolean getDebug() {
         return debug;
     }
 
     /**
      * If true, compiles with optimization enabled.
      * @param optimize if true compile with optimization enabled
      */
     public void setOptimize(boolean optimize) {
         this.optimize = optimize;
     }
 
     /**
      * Gets the optimize flag.
      * @return the optimize flag
      */
     public boolean getOptimize() {
         return optimize;
     }
 
     /**
      * Enables dependency-tracking for compilers
      * that support this (jikes and classic).
      * @param depend if true enable dependency-tracking
      */
     public void setDepend(boolean depend) {
         this.depend = depend;
     }
 
     /**
      * Gets the depend flag.
      * @return the depend flag
      */
     public boolean getDepend() {
         return depend;
     }
 
     /**
      * If true, asks the compiler for verbose output.
      * @param verbose if true, asks the compiler for verbose output
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
     /**
      * Gets the verbose flag.
      * @return the verbose flag
      */
     public boolean getVerbose() {
         return verbose;
     }
 
     /**
      * Sets the target VM that the classes will be compiled for. Valid
      * values depend on the compiler, for jdk 1.4 the valid values are
      * "1.1", "1.2", "1.3", "1.4" and "1.5".
      * @param target the target VM
      */
     public void setTarget(String target) {
         this.targetAttribute = target;
     }
 
     /**
      * Gets the target VM that the classes will be compiled for.
      * @return the target VM
      */
     public String getTarget() {
         return targetAttribute;
     }
 
     /**
      * If true, includes Ant's own classpath in the classpath.
      * @param include if true, includes Ant's own classpath in the classpath
      */
     public void setIncludeantruntime(boolean include) {
         includeAntRuntime = include;
     }
 
     /**
      * Gets whether or not the ant classpath is to be included in the classpath.
      * @return whether or not the ant classpath is to be included in the classpath
      */
     public boolean getIncludeantruntime() {
         return includeAntRuntime;
     }
 
     /**
      * If true, includes the Java runtime libraries in the classpath.
      * @param include if true, includes the Java runtime libraries in the classpath
      */
     public void setIncludejavaruntime(boolean include) {
         includeJavaRuntime = include;
     }
 
     /**
      * Gets whether or not the java runtime should be included in this
      * task's classpath.
      * @return the includejavaruntime attribute
      */
     public boolean getIncludejavaruntime() {
         return includeJavaRuntime;
     }
 
     /**
      * If true, forks the javac compiler.
      *
      * @param f "true|false|on|off|yes|no"
      */
     public void setFork(boolean f) {
         fork = f;
     }
 
     /**
      * Sets the name of the javac executable.
      *
      * <p>Ignored unless fork is true or extJavac has been specified
      * as the compiler.</p>
      * @param forkExec the name of the executable
      */
     public void setExecutable(String forkExec) {
         forkedExecutable = forkExec;
     }
 
     /**
      * The value of the executable attribute, if any.
      *
      * @since Ant 1.6
      * @return the name of the java executable
      */
     public String getExecutable() {
         return forkedExecutable;
     }
 
     /**
      * Is this a forked invocation of JDK's javac?
      * @return true if this is a forked invocation
      */
     public boolean isForkedJavac() {
         return fork || "extJavac".equals(getCompiler());
     }
 
     /**
      * The name of the javac executable to use in fork-mode.
      *
      * <p>This is either the name specified with the executable
      * attribute or the full path of the javac compiler of the VM Ant
      * is currently running in - guessed by Ant.</p>
      *
      * <p>You should <strong>not</strong> invoke this method if you
      * want to get the value of the executable command - use {@link
      * #getExecutable getExecutable} for this.</p>
      * @return the name of the javac executable
      */
     public String getJavacExecutable() {
         if (forkedExecutable == null && isForkedJavac()) {
             forkedExecutable = getSystemJavac();
         } else if (forkedExecutable != null && !isForkedJavac()) {
             forkedExecutable = null;
         }
         return forkedExecutable;
     }
 
     /**
      * If true, enables the -nowarn option.
      * @param flag if true, enable the -nowarn option
      */
     public void setNowarn(boolean flag) {
         this.nowarn = flag;
     }
 
     /**
      * Should the -nowarn option be used.
      * @return true if the -nowarn option should be used
      */
     public boolean getNowarn() {
         return nowarn;
     }
 
     /**
      * Adds an implementation specific command-line argument.
      * @return a ImplementationSpecificArgument to be configured
      */
     public ImplementationSpecificArgument createCompilerArg() {
         ImplementationSpecificArgument arg =
             new ImplementationSpecificArgument();
         facade.addImplementationArgument(arg);
         return arg;
     }
 
     /**
      * Get the additional implementation specific command line arguments.
      * @return array of command line arguments, guaranteed to be non-null.
      */
     public String[] getCurrentCompilerArgs() {
         String chosen = facade.getExplicitChoice();
-        // make sure facade knows about magic properties and fork setting
-        facade.setImplementation(getCompiler());
         try {
-            return facade.getArgs();
+            // make sure facade knows about magic properties and fork setting
+            String appliedCompiler = getCompiler();
+            facade.setImplementation(appliedCompiler);
+
+            String[] result = facade.getArgs();
+
+            String altCompilerName = getAltCompilerName(facade.getImplementation());
+
+            if (result.length == 0 && altCompilerName != null) {
+                facade.setImplementation(altCompilerName);
+                result = facade.getArgs();
+            }
+
+            return result;
+
         } finally {
             facade.setImplementation(chosen);
         }
     }
 
+    private String getAltCompilerName(String anImplementation) {
+        if (JAVAC15.equalsIgnoreCase(anImplementation)
+                || JAVAC14.equalsIgnoreCase(anImplementation)
+                || JAVAC13.equalsIgnoreCase(anImplementation)) {
+            return MODERN;
+        }
+        if (JAVAC12.equalsIgnoreCase(anImplementation)
+                || JAVAC11.equalsIgnoreCase(anImplementation)) {
+            return CLASSIC;
+        }
+        if (MODERN.equalsIgnoreCase(anImplementation)) {
+            String nextSelected = assumedJavaVersion();
+            if (JAVAC15.equalsIgnoreCase(nextSelected)
+                    || JAVAC14.equalsIgnoreCase(nextSelected)
+                    || JAVAC13.equalsIgnoreCase(nextSelected)) {
+                return nextSelected;
+            }
+        }
+        if (CLASSIC.equals(anImplementation)) {
+            return assumedJavaVersion();
+        }
+        if (EXTJAVAC.equalsIgnoreCase(anImplementation)) {
+            return assumedJavaVersion();
+        }
+        return null;
+    }
+
     /**
      * Where Ant should place temporary files.
      *
      * @since Ant 1.6
      * @param tmpDir the temporary directory
      */
     public void setTempdir(File tmpDir) {
         this.tmpDir = tmpDir;
     }
 
     /**
      * Where Ant should place temporary files.
      *
      * @since Ant 1.6
      * @return the temporary directory
      */
     public File getTempdir() {
         return tmpDir;
     }
 
     /**
      * Executes the task.
      * @exception BuildException if an error occurs
      */
     public void execute() throws BuildException {
         checkParameters();
         resetFileLists();
 
         // scan source directories and dest directory to build up
         // compile lists
         String[] list = src.list();
         for (int i = 0; i < list.length; i++) {
             File srcDir = getProject().resolveFile(list[i]);
             if (!srcDir.exists()) {
                 throw new BuildException("srcdir \""
                                          + srcDir.getPath()
                                          + "\" does not exist!", getLocation());
             }
 
             DirectoryScanner ds = this.getDirectoryScanner(srcDir);
             String[] files = ds.getIncludedFiles();
 
             scanDir(srcDir, destDir != null ? destDir : srcDir, files);
         }
 
         compile();
     }
 
     /**
      * Clear the list of files to be compiled and copied..
      */
     protected void resetFileLists() {
         compileList = new File[0];
     }
 
     /**
      * Scans the directory looking for source files to be compiled.
      * The results are returned in the class variable compileList
      *
      * @param srcDir   The source directory
      * @param destDir  The destination directory
      * @param files    An array of filenames
      */
     protected void scanDir(File srcDir, File destDir, String[] files) {
         GlobPatternMapper m = new GlobPatternMapper();
         m.setFrom("*.java");
         m.setTo("*.class");
         SourceFileScanner sfs = new SourceFileScanner(this);
         File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);
 
         if (newFiles.length > 0) {
             File[] newCompileList
                 = new File[compileList.length + newFiles.length];
             System.arraycopy(compileList, 0, newCompileList, 0,
                     compileList.length);
             System.arraycopy(newFiles, 0, newCompileList,
                     compileList.length, newFiles.length);
             compileList = newCompileList;
         }
     }
 
     /**
      * Gets the list of files to be compiled.
      * @return the list of files as an array
      */
     public File[] getFileList() {
         return compileList;
     }
 
     /**
      * Is the compiler implementation a jdk compiler
      *
      * @param compilerImpl the name of the compiler implementation
      * @return true if compilerImpl is "modern", "classic", "javac1.1",
      *                 "javac1.2", "javac1.3", "javac1.4" or "javac1.5".
      */
     protected boolean isJdkCompiler(String compilerImpl) {
-        return "modern".equals(compilerImpl)
-            || "classic".equals(compilerImpl)
-            || "javac1.1".equals(compilerImpl)
-            || "javac1.2".equals(compilerImpl)
-            || "javac1.3".equals(compilerImpl)
-            || "javac1.4".equals(compilerImpl)
-            || "javac1.5".equals(compilerImpl);
+        return MODERN.equals(compilerImpl)
+            || CLASSIC.equals(compilerImpl)
+            || JAVAC15.equals(compilerImpl)
+            || JAVAC14.equals(compilerImpl)
+            || JAVAC13.equals(compilerImpl)
+            || JAVAC12.equals(compilerImpl)
+            || JAVAC11.equals(compilerImpl);
     }
 
     /**
      * @return the executable name of the java compiler
      */
     protected String getSystemJavac() {
         return JavaEnvUtils.getJdkExecutable("javac");
     }
 
     /**
      * Choose the implementation for this particular task.
      * @param compiler the name of the compiler
      * @since Ant 1.5
      */
     public void setCompiler(String compiler) {
         facade.setImplementation(compiler);
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overridden
      * via the compiler and fork attributes.</p>
      *
      * <p>If fork has been set to true, the result will be extJavac
      * and not classic or java1.2 - no matter what the compiler
      * attribute looks like.</p>
      *
      * @see #getCompilerVersion
      *
      * @since Ant 1.5
      */
     public String getCompiler() {
         String compilerImpl = getCompilerVersion();
         if (fork) {
             if (isJdkCompiler(compilerImpl)) {
                 compilerImpl = "extJavac";
             } else {
                 log("Since compiler setting isn't classic or modern,"
                     + "ignoring fork setting.", Project.MSG_WARN);
             }
         }
         return compilerImpl;
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overridden
      * via the compiler attribute.</p>
      *
      * <p>This method does not take the fork attribute into
      * account.</p>
      *
      * @see #getCompiler
      *
      * @since Ant 1.5
      */
     public String getCompilerVersion() {
         facade.setMagicValue(getProject().getProperty("build.compiler"));
         return facade.getImplementation();
     }
 
     /**
      * Check that all required attributes have been set and nothing
      * silly has been entered.
      *
      * @since Ant 1.5
      * @exception BuildException if an error occurs
      */
     protected void checkParameters() throws BuildException {
         if (src == null) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
         if (src.size() == 0) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
 
         if (destDir != null && !destDir.isDirectory()) {
             throw new BuildException("destination directory \""
                                      + destDir
                                      + "\" does not exist "
                                      + "or is not a directory", getLocation());
         }
     }
 
     /**
      * Perform the compilation.
      *
      * @since Ant 1.5
      */
     protected void compile() {
         String compilerImpl = getCompiler();
 
         if (compileList.length > 0) {
             log("Compiling " + compileList.length + " source file"
                 + (compileList.length == 1 ? "" : "s")
                 + (destDir != null ? " to " + destDir : ""));
 
             if (listFiles) {
                 for (int i = 0; i < compileList.length; i++) {
                   String filename = compileList[i].getAbsolutePath();
                   log(filename);
                 }
             }
 
             CompilerAdapter adapter =
                 CompilerAdapterFactory.getCompiler(compilerImpl, this);
 
             // now we need to populate the compiler adapter
             adapter.setJavac(this);
 
             // finally, lets execute the compiler!!
             if (!adapter.execute()) {
                 if (failOnError) {
                     throw new BuildException(FAIL_MSG, getLocation());
                 } else {
                     log(FAIL_MSG, Project.MSG_ERR);
                 }
             }
         }
     }
 
     /**
      * Adds an "compiler" attribute to Commandline$Attribute used to
      * filter command line attributes based on the current
      * implementation.
      */
     public class ImplementationSpecificArgument extends
         org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
 
         /**
          * @param impl the name of the compiler
          */
         public void setCompiler(String impl) {
             super.setImplementation(impl);
         }
     }
 
 }
