diff --git a/WHATSNEW b/WHATSNEW
index b07033ccc..e21d91d72 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1214 +1,1216 @@
 Changes from current Ant 1.6.5 version to current SVN version
 =============================================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Improved recursion detection for lines with multiple matches of same token on one single line.
   Bugzilla report 38456.
 
 * Task will now log correctly even if no project is set. 
   Bugzilla report 38458.
 
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
 
 * A bug in SQLExec would prevent the execution of trailing,
   non-semicolon-delimited statements.  Bugzilla Report 37764.
 
 * support for the XSL:P XML parser has been removed.
   Bugzilla Report 23455.
 
 * Visual Age for Java optional tasks removed.
 
 * Testlet (test) optional task removed.
 
 * Icontract optional task removed.  
 
 Fixed bugs:
 -----------
 
 * ant.bat now handles classpath set to "". Bug report 38914. 
 
 * <junit> now supports JUnit 4. Bugzilla Report 38811.
 
 * <junit> can now work with junit.jar in its <classpath>. Bugzilla Report 38799.
 
 * Some potential NullPointerExceptions, Bugzilla Reports 37765 and 38056
 
 * Problem when adding multiple filter files, Bugzilla Report 37341
 
 * problem referencing jars specified by Class-Path attribute in manifest
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
 
 * The same IntrospectionHelper instance was continuously added as a listener
   to project. Bugzilla report 37184.
 
 * FileUtils.toURI() was not encoding non-ASCII characters to ASCII,
   causing impossibility to process XML entities referenced by XML
   documents in non ASCII paths. Bugzilla report 37348.
 
 * > 1 ssh invocations to a given host would fail. Bugzilla report 36207.
 
 * EmailTask was eating SMTP error messages. Bugzilla report 37547.
 
 * PropertySet API setMapper(...) didn't properly set up the Mapper.
   Bugzilla report 37760.
 
 * Proper return code for ant.bat. Bugzilla report 13655.
 
 * Project not set on ChainReaderHelpers used by the Redirector.
   Bugzilla report 37958.
 
 * Copy task would fail on locked (or otherwise uncopyable) files even if
   failonerror set to false. Bugzilla report 38175.
 
 * <junit> task did not print all the Test names when using forkmode='once'.
   Bugzilla report 37426.
 
 * <available> could leak resources, Bugzilla Report 38260.
 
 * Redirector called Thread.sleep in a synchronized block. Bugzilla report 37767.
 
 * CCUnlock's objselect attribute could exhibit unpredictable behavior;
   standardized improperly included objselect and objsel property accessors to
   delegate to the inherited objSelect property accessor. Bugzilla report 37766.
 
 Other changes:
 --------------
+* took in bugzilla report 39320.
+
 * Improve compatibility with GNU Classpath and java versions prior to 1.5. Bugzilla 39027.
 
 * ${ant.core.lib} may now be used to refer to the library containing the
   Ant classes, for instance useful when compiling tasks.
 
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
 
 * New task <loadresource> that accompanies <loadfile> for non file resources.
 
 * <echo> now supports an encoding when saving to a file.
 
 * new GreedyInputHandler added.
 
 * add textfile attribute to the <filesmatch> condition. When true, the text
   contents of the two files are compared, ignoring line ending differences.
 
 * new <resourcesmatch> condition.
 
 * added the onmissingfiltersfile attribute to filterset. Bugzilla report 19845.
 
 * added the inline handler element to the input task.
 
 * <sql> supports property expansion if you set the expandProperties
   attribute. By default it does not expand properties, something we
   dare not change for fear of breaking complex SQL operations in
   existing files.
 
 * <javadoc>'s packagenames attribute is now optional and defaults to "*".
 
 * <javac>'s source and target attributes as well as <javadoc>'s source
   attribute will read default values from the properties
   ant.build.javac.source and ant.build.javac.target.
 
 * Handling of ' ', '#' in CLASSPATH and '#' in -lib (cannot use ' '
   in -lib on UNIX at the moment). Bugzilla Report 39295.
 
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
 
 * The <script> task now requires Apache BSF instead of the older IBM
   version.  See <http://jakarta.apache.org/bsf/>
 
 * <xmlproperty> will no longer fail if the file to be loaded doesn't exist.
 
 * XML namespaces are now enabled in the XML parser, meaning XML namespace
   declarations no longer cause errors. However task names containing colons
   will cause errors unless there is a corresponding namespace uri.
 
 * The <ftp> and <telnet> tasks now require Jakarta Commons Net instead
   of the older ORO Netcomponents version.  See
   <http://jakarta.apache.org/commons/net/index.html>.
 
 * <input> will no longer prompt the user and wait for input if the
   addproperty attribute is set to a property that has already been
   defined in the project.  If you rely on the task waiting for input,
   don't use the addproperty attribute.
 
 * The Class-Path attribute in manifests will no longer merge the
   entries of all manifests found, but will be treated like all other
   manifest attributes - the most recent attribute(s) will be used.
 
 * New Launch mechanism implemented. This moves some functionality from
   the batch files / shell scripts into Java. This removes environment
   limitations, for command issues, directory depth issues on Windows. Also
   allows a per-user library location to be used if the main Ant install
   is locked down.
 
 * The Entry nested element of PropertyFile will not any more have its value
   attribute (actually increment) overwritten with the new value of the entry
   after execution.
 
 * Output stored from a <java> or <exec> task is now exactly as generated. No
   conversion to platform end-of-line characters is performed.
 
 * <translate> will now preserve line endings.
 
 * <ftp> followsymlinks="false" in nested fileset definitions is explicitly
   required in order to exclude remote symbolic links (when doing a get, chmod,
   delete, rmdir).
 
 * The values of the Copy#fileCopyMap variable has changed from String to
   String[]. (In java 1.5 terms it was Hashtable<String, String> and
   is now Hashtable<String, String[]>). This will affect third party code
   that extend Copy and override Copy#doFileOperations.
 
 * <loadproperties> didn't expand properties while <property file="..."/>
   does, so they were not equivalent.  This has been fixed, which means
   that propetries may get expanded twice if you use an
   <expandproperties> filterreader.  Bugzilla Report 17782.
 
 * User defined tasks and typedefs are now handled internally in the
   same way as predefined tasks and typedefs. Also tasks and typedefs
   are resolved at a later stage. This causes some
   differences especially for user defined task containers.
 
 * <checksum> log message "Calculating checksum ..." has been degraded
   from INFO to VERBOSE.
 
 Fixed bugs:
 -----------
 * Filter readers were not handling line endings properly.  Bugzilla
   Report 18476.
 
 * Filtersets were also not handling line endings properly.
 
 * Expand tasks did not behave as expected with PatternSets.
 
 * <property environment=... /> now works on OS/400.
 
 * <cab> could hang listcab on large <fileset>s.
 
 * The starteam stcheckout, stcheckin tasks now correctly compute
   status of files against whatever local tree they are run against
   and, optionally, will not process a file if it is current.
   Previously you had to process everything unless you ran against the
   default folder which wasn't the normal use-case for ant-starteam.
   The stlist task now similarly displays that status correctly making
   it a more generally useful tool.
 
 * entity includes would cause exceptions if path names included spaces.
 
 * addConfiguredXXX would not work for TaskAdapter wrapped tasks
 
 * Fix <ilasm> outputfile testing so that the output file does not need
   to exist beforehand.
 
 * Ant will now exit with a return code of 1 if it encounters problems
diff --git a/src/main/org/apache/tools/ant/UnknownElement.java b/src/main/org/apache/tools/ant/UnknownElement.java
index d5b680d8c..bbcd818fb 100644
--- a/src/main/org/apache/tools/ant/UnknownElement.java
+++ b/src/main/org/apache/tools/ant/UnknownElement.java
@@ -1,613 +1,613 @@
 /*
- * Copyright  2000-2005 The Apache Software Foundation
+ * Copyright  2000-2006 The Apache Software Foundation
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
 import java.util.Iterator;
 import java.util.List;
 import java.io.IOException;
 import org.apache.tools.ant.taskdefs.PreSetDef;
 
 /**
  * Wrapper class that holds all the information necessary to create a task
  * or data type that did not exist when Ant started, or one which
  * has had its definition updated to use a different implementation class.
  *
  */
 public class UnknownElement extends Task {
 
     /**
      * Holds the name of the task/type or nested child element of a
      * task/type that hasn't been defined at parser time or has
      * been redefined since original creation.
      */
     private String elementName;
 
     /**
      * Holds the namespace of the element.
      */
     private String namespace = "";
 
     /**
      * Holds the namespace qname of the element.
      */
     private String qname;
 
     /**
      * The real object after it has been loaded.
      */
     private Object realThing;
 
     /**
      * List of child elements (UnknownElements).
      */
     private List/*<UnknownElement>*/ children = null;
 
     /** Specifies if a predefined definition has been done */
     private boolean presetDefed = false;
 
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
      * @return the list of nested UnknownElements for this UnknownElement.
      */
     public List getChildren() {
         return children;
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
 
     /** Return the namespace of the XML element associated with this component.
      *
      * @return Namespace URI used in the xmlns declaration.
      */
     public String getNamespace() {
         return namespace;
     }
 
     /**
      * Set the namespace of the XML element associated with this component.
      * This method is typically called by the XML processor.
      * If the namespace is "ant:current", the component helper
      * is used to get the current antlib uri.
      *
      * @param namespace URI used in the xmlns declaration.
      */
     public void setNamespace(String namespace) {
         if (namespace.equals(ProjectHelper.ANT_CURRENT_URI)) {
             ComponentHelper helper = ComponentHelper.getComponentHelper(
                 getProject());
             namespace = helper.getCurrentAntlibUri();
         }
         this.namespace = namespace == null ? "" : namespace;
     }
 
     /** Return the qname of the XML element associated with this component.
      *
      * @return namespace Qname used in the element declaration.
      */
     public String getQName() {
         return qname;
     }
 
     /** Set the namespace qname of the XML element.
      * This method is typically called by the XML processor.
      *
      * @param qname the qualified name of the element
      */
     public void setQName(String qname) {
         this.qname = qname;
     }
 
 
     /**
      * Get the RuntimeConfigurable instance for this UnknownElement, containing
      * the configuration information.
      *
      * @return the configuration info.
      */
     public RuntimeConfigurable getWrapper() {
         return super.getWrapper();
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
 
         configure(makeObject(this, getWrapper()));
     }
 
     /**
      * Configure the given object from this UnknownElement
      *
      * @param realObject the real object this UnknownElement is representing.
      *
      */
     public void configure(Object realObject) {
         realThing = realObject;
 
         getWrapper().setProxy(realThing);
         Task task = null;
         if (realThing instanceof Task) {
             task = (Task) realThing;
 
             task.setRuntimeConfigurableWrapper(getWrapper());
 
             // For Script to work. Ugly
             // The reference is replaced by RuntimeConfigurable
             this.getOwningTarget().replaceChild(this, (Task) realThing);
         }
 
         handleChildren(realThing, getWrapper());
 
         // configure attributes of the object and it's children. If it is
         // a task container, defer the configuration till the task container
         // attempts to use the task
 
         if (task != null) {
             task.maybeConfigure();
         } else {
             getWrapper().maybeConfigure(getProject());
         }
     }
 
     /**
      * Handles output sent to System.out by this task or its real task.
      *
      * @param output The output to log. Should not be <code>null</code>.
      */
     protected void handleOutput(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleOutput(output);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6
      */
     protected int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (realThing instanceof Task) {
             return ((Task) realThing).handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
 
     }
     /**
      * Handles output sent to System.out by this task or its real task.
      *
      * @param output The output to log. Should not be <code>null</code>.
      */
     protected void handleFlush(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleFlush(output);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Handles error output sent to System.err by this task or its real task.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      */
     protected void handleErrorOutput(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleErrorOutput(output);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
 
     /**
      * Handles error output sent to System.err by this task or its real task.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      */
     protected void handleErrorFlush(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleErrorOutput(output);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
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
         realThing = null;
         // FIXME: the following should be done as well, but is
         //        commented out for the moment as the unit tests fail
         //        if it is done
         //getWrapper().setProxy(null);
 
     }
 
     /**
      * Adds a child element to this element.
      *
      * @param child The child element to add. Must not be <code>null</code>.
      */
     public void addChild(UnknownElement child) {
         if (children == null) {
             children = new ArrayList();
         }
         children.add(child);
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
     protected void handleChildren(
         Object parent,
         RuntimeConfigurable parentWrapper)
         throws BuildException {
         if (parent instanceof TypeAdapter) {
             parent = ((TypeAdapter) parent).getProxy();
         }
 
         String parentUri = getNamespace();
         Class parentClass = parent.getClass();
         IntrospectionHelper ih = IntrospectionHelper.getHelper(getProject(), parentClass);
 
 
         if (children != null) {
             Iterator it = children.iterator();
             for (int i = 0; it.hasNext(); i++) {
                 RuntimeConfigurable childWrapper = parentWrapper.getChild(i);
                 UnknownElement child = (UnknownElement) it.next();
                 try {
                     if (!handleChild(
                             parentUri, ih, parent, child, childWrapper)) {
                         if (!(parent instanceof TaskContainer)) {
                             ih.throwNotSupported(getProject(), parent,
                                                  child.getTag());
                         } else {
                             // a task container - anything could happen - just add the
                             // child to the container
                             TaskContainer container = (TaskContainer) parent;
                             container.addTask(child);
                         }
                     }
                 } catch (UnsupportedElementException ex) {
                     throw new BuildException(
                         parentWrapper.getElementTag()
                         + " doesn't support the nested \"" + ex.getElement()
                         + "\" element.", ex);
                 }
             }
         }
     }
 
     /**
      * @return the component name - uses ProjectHelper#genComponentName()
      */
     protected String getComponentName() {
         return ProjectHelper.genComponentName(getNamespace(), getTag());
     }
 
     /**
      * This is used then the realobject of the UE is a PreSetDefinition.
      * This is also used when a presetdef is used on a presetdef
      * The attributes, elements and text are applied to this
      * UE.
      *
      * @param u an UnknownElement containing the attributes, elements and text
      */
     public void applyPreSet(UnknownElement u) {
         if (presetDefed) {
             return;
         }
         // Do the runtime
         getWrapper().applyPreSet(u.getWrapper());
         if (u.children != null) {
             List newChildren = new ArrayList();
             newChildren.addAll(u.children);
             if (children != null) {
                 newChildren.addAll(children);
             }
             children = newChildren;
         }
         presetDefed = true;
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
         ComponentHelper helper = ComponentHelper.getComponentHelper(
             getProject());
         String name = ue.getComponentName();
         Object o = helper.createComponent(ue, ue.getNamespace(), name);
         if (o == null) {
             throw getNotFoundException("task or type", name);
         }
         if (o instanceof PreSetDef.PreSetDefinition) {
             PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition) o;
             o = def.createObject(ue.getProject());
             if (o == null) {
                 throw getNotFoundException(
                     "preset " + name,
                     def.getPreSets().getComponentName());
             }
             ue.applyPreSet(def.getPreSets());
             if (o instanceof Task) {
                 Task task = (Task) o;
                 task.setTaskType(ue.getTaskType());
                 task.setTaskName(ue.getTaskName());
                 task.init();
             }
         }
         if (o instanceof UnknownElement) {
             o = ((UnknownElement) o).makeObject((UnknownElement) o, w);
         }
         if (o instanceof Task) {
             ((Task) o).setOwningTarget(getOwningTarget());
         }
         return o;
     }
 
     /**
      * Creates a named task and configures it up to the init() stage.
      *
      * @param ue The UnknownElement to create the real task for.
      *           Must not be <code>null</code>.
      * @param w  Ignored.
      *
      * @return the task specified by the given unknown element, or
      *         <code>null</code> if the task name is not recognised.
      */
     protected Task makeTask(UnknownElement ue, RuntimeConfigurable w) {
         Task task = getProject().createTask(ue.getTag());
 
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
      * @param name The name of the element which could not be found.
      *             Should not be <code>null</code>.
      *
      * @return a detailed description of what might have caused the problem.
      */
     protected BuildException getNotFoundException(String what,
                                                   String name) {
         ComponentHelper helper = ComponentHelper.getComponentHelper(getProject());
         String msg = helper.diagnoseCreationFailure(name, what);
         return new BuildException(msg, getLocation());
     }
 
     /**
      * Returns the name to use in logging messages.
      *
      * @return the name to use in logging messages.
      */
     public String getTaskName() {
         //return elementName;
         return realThing == null
             || !(realThing instanceof Task) ? super.getTaskName()
                                             : ((Task) realThing).getTaskName();
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
 
     /**
      * Return the configured object
      *
      * @return the real thing whatever it is
      *
      * @since ant 1.6
      */
     public Object getRealThing() {
         return realThing;
     }
 
     /**
      * Set the configured object
      * @param realThing the configured object
      * @since ant 1.7
      */
     public void setRealThing(Object realThing) {
         this.realThing = realThing;
     }
 
     /**
      * Try to create a nested element of <code>parent</code> for the
      * given tag.
      *
      * @return whether the creation has been successful
      */
     private boolean handleChild(
         String parentUri,
         IntrospectionHelper ih,
         Object parent, UnknownElement child,
         RuntimeConfigurable childWrapper) {
         String childName = ProjectHelper.genComponentName(
             child.getNamespace(), child.getTag());
         if (ih.supportsNestedElement(parentUri, childName)) {
             IntrospectionHelper.Creator creator =
                 ih.getElementCreator(
                     getProject(), parentUri, parent, childName, child);
             creator.setPolyType(childWrapper.getPolyType());
             Object realChild = creator.create();
             if (realChild instanceof PreSetDef.PreSetDefinition) {
                 PreSetDef.PreSetDefinition def =
                     (PreSetDef.PreSetDefinition) realChild;
                 realChild = creator.getRealObject();
                 child.applyPreSet(def.getPreSets());
             }
             childWrapper.setCreator(creator);
             childWrapper.setProxy(realChild);
             if (realChild instanceof Task) {
                 Task childTask = (Task) realChild;
                 childTask.setRuntimeConfigurableWrapper(childWrapper);
                 childTask.setTaskName(childName);
                 childTask.setTaskType(childName);
                 childTask.setLocation(child.getLocation());
             }
             child.handleChildren(realChild, childWrapper);
             return true;
         }
         return false;
     }
 
     /**
      * like contents equals, but ignores project
      * @param obj the object to check against
      * @return true if this unknownelement has the same contents the other
      */
     public boolean similar(Object obj) {
         if (obj == null) {
             return false;
         }
         if (!getClass().getName().equals(obj.getClass().getName())) {
             return false;
         }
         UnknownElement other = (UnknownElement) obj;
         // Are the names the same ?
         if (!equalsString(elementName, other.elementName)) {
             return false;
         }
         if (!namespace.equals(other.namespace)) {
             return false;
         }
         if (!qname.equals(other.qname)) {
             return false;
         }
         // Are attributes the same ?
         if (!getWrapper().getAttributeMap().equals(
                 other.getWrapper().getAttributeMap())) {
             return false;
         }
         // Is the text the same?
         //   Need to use equals on the string and not
         //   on the stringbuffer as equals on the string buffer
         //   does not compare the contents.
         if (!getWrapper().getText().toString().equals(
                 other.getWrapper().getText().toString())) {
             return false;
         }
         // Are the sub elements the same ?
         if (children == null || children.size() == 0) {
             return other.children == null || other.children.size() == 0;
         }
         if (other.children == null) {
             return false;
         }
         if (children.size() != other.children.size()) {
             return false;
         }
         for (int i = 0; i < children.size(); ++i) {
             UnknownElement child = (UnknownElement) children.get(i);
             if (!child.similar(other.children.get(i))) {
                 return false;
             }
         }
         return true;
     }
 
     private static boolean equalsString(String a, String b) {
-        return (a == null) ? (a == b) : a.equals(b);
+        return (a == null) ? (b == null) : a.equals(b);
     }
 }
diff --git a/src/main/org/apache/tools/ant/filters/BaseFilterReader.java b/src/main/org/apache/tools/ant/filters/BaseFilterReader.java
index 742701aa0..237a88c2e 100644
--- a/src/main/org/apache/tools/ant/filters/BaseFilterReader.java
+++ b/src/main/org/apache/tools/ant/filters/BaseFilterReader.java
@@ -1,197 +1,197 @@
 /*
- * Copyright  2002,2004-2005 The Apache Software Foundation
+ * Copyright  2002,2004-2006 The Apache Software Foundation
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
 package org.apache.tools.ant.filters;
 
 import java.io.FilterReader;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.StringReader;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Base class for core filter readers.
  *
  */
 public abstract class BaseFilterReader extends FilterReader {
     /** Buffer size used when reading */
     private static final int BUFFER_SIZE = 8192;
 
     /** Have the parameters passed been interpreted? */
     private boolean initialized = false;
 
     /** The Ant project this filter is part of. */
     private Project project = null;
 
     /**
      * Constructor used by Ant's introspection mechanism.
      * The original filter reader is only used for chaining
      * purposes, never for filtering purposes (and indeed
      * it would be useless for filtering purposes, as it has
      * no real data to filter). ChainedReaderHelper uses
      * this placeholder instance to create a chain of real filters.
      */
     public BaseFilterReader() {
-        super(new StringReader(new String()));
+        super(new StringReader(""));
         FileUtils.close(this);
     }
 
     /**
      * Creates a new filtered reader.
      *
      * @param in A Reader object providing the underlying stream.
      *           Must not be <code>null</code>.
      *
      */
     public BaseFilterReader(final Reader in) {
         super(in);
     }
 
     /**
      * Reads characters into a portion of an array.  This method will block
      * until some input is available, an I/O error occurs, or the end of the
      * stream is reached.
      *
      * @param      cbuf  Destination buffer to write characters to.
      *                   Must not be <code>null</code>.
      * @param      off   Offset at which to start storing characters.
      * @param      len   Maximum number of characters to read.
      *
      * @return     the number of characters read, or -1 if the end of the
      *             stream has been reached
      *
      * @exception  IOException  If an I/O error occurs
      */
     public final int read(final char[] cbuf, final int off,
                           final int len) throws IOException {
         for (int i = 0; i < len; i++) {
             final int ch = read();
             if (ch == -1) {
                 if (i == 0) {
                     return -1;
                 } else {
                     return i;
                 }
             }
             cbuf[off + i] = (char) ch;
         }
         return len;
     }
 
     /**
      * Skips characters.  This method will block until some characters are
      * available, an I/O error occurs, or the end of the stream is reached.
      *
      * @param  n  The number of characters to skip
      *
      * @return    the number of characters actually skipped
      *
      * @exception  IllegalArgumentException  If <code>n</code> is negative.
      * @exception  IOException  If an I/O error occurs
      */
     public final long skip(final long n)
         throws IOException, IllegalArgumentException {
         if (n < 0L) {
             throw new IllegalArgumentException("skip value is negative");
         }
 
         for (long i = 0; i < n; i++) {
             if (read() == -1) {
                 return i;
             }
         }
         return n;
     }
 
     /**
      * Sets the initialized status.
      *
      * @param initialized Whether or not the filter is initialized.
      */
     protected final void setInitialized(final boolean initialized) {
         this.initialized = initialized;
     }
 
     /**
      * Returns the initialized status.
      *
      * @return whether or not the filter is initialized
      */
     protected final boolean getInitialized() {
         return initialized;
     }
 
     /**
      * Sets the project to work with.
      *
      * @param project The project this filter is part of.
      *                Should not be <code>null</code>.
      */
     public final void setProject(final Project project) {
         this.project = project;
     }
 
     /**
      * Returns the project this filter is part of.
      *
      * @return the project this filter is part of
      */
     protected final Project getProject() {
         return project;
     }
 
     /**
      * Reads a line of text ending with '\n' (or until the end of the stream).
      * The returned String retains the '\n'.
      *
      * @return the line read, or <code>null</code> if the end of the stream
      * has already been reached
      *
      * @exception IOException if the underlying reader throws one during
      *                        reading
      */
     protected final String readLine() throws IOException {
         int ch = in.read();
 
         if (ch == -1) {
             return null;
         }
 
         StringBuffer line = new StringBuffer();
 
         while (ch != -1) {
             line.append ((char) ch);
             if (ch == '\n') {
                 break;
             }
             ch = in.read();
         }
         return line.toString();
     }
 
     /**
      * Reads to the end of the stream, returning the contents as a String.
      *
      * @return the remaining contents of the reader, as a String
      *
      * @exception IOException if the underlying reader throws one during
      *            reading
      */
     protected final String readFully() throws IOException {
         return FileUtils.readFully(in, BUFFER_SIZE);
     }
 }
diff --git a/src/main/org/apache/tools/ant/filters/FixCrLfFilter.java b/src/main/org/apache/tools/ant/filters/FixCrLfFilter.java
index 9715873c2..6c403de25 100755
--- a/src/main/org/apache/tools/ant/filters/FixCrLfFilter.java
+++ b/src/main/org/apache/tools/ant/filters/FixCrLfFilter.java
@@ -1,941 +1,949 @@
 /*
  * Copyright 2005-2006 The Apache Software Foundation
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
 package org.apache.tools.ant.filters;
 
 import java.io.IOException;
 import java.io.Reader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 
 /**
  * Converts text to local OS formatting conventions, as well as repair text
  * damaged by misconfigured or misguided editors or file transfer programs.
  * <p>
  * This filter can take the following arguments:
  * <ul>
  * <li>eof
  * <li>eol
  * <li>fixlast
  * <li>javafiles
  * <li>tab
  * <li>tablength
  * </ul>
  * None of which are required.
  * <p>
  * This version generalises the handling of EOL characters, and allows for
  * CR-only line endings (which I suspect is the standard on Macs.) Tab handling
  * has also been generalised to accommodate any tabwidth from 2 to 80,
  * inclusive. Importantly, it can leave untouched any literal TAB characters
  * embedded within Java string or character constants.
  * <p>
  * <em>Caution:</em> run with care on carefully formatted files. This may
  * sound obvious, but if you don't specify asis, presume that your files are
  * going to be modified. If "tabs" is "add" or "remove", whitespace characters
  * may be added or removed as necessary. Similarly, for EOL's - eol="asis"
  * actually means convert to your native O/S EOL convention while eol="crlf" or
  * cr="add" can result in CR characters being removed in one special case
  * accommodated, i.e., CRCRLF is regarded as a single EOL to handle cases where
  * other programs have converted CRLF into CRCRLF.
  *
  * <P>
  * Example:
  *
  * <pre>
  * &lt;&lt;fixcrlf tab=&quot;add&quot; eol=&quot;crlf&quot; eof=&quot;asis&quot;/&gt;
  * </pre>
  *
  * Or:
  *
  * <pre>
  * &lt;filterreader classname=&quot;org.apache.tools.ant.filters.FixCrLfFilter&quot;&gt;
  *   &lt;param eol=&quot;crlf&quot; tab=&quot;asis&quot;/&gt;
  *  &lt;/filterreader&gt;
  * </pre>
  *
  */
 public final class FixCrLfFilter extends BaseParamFilterReader implements ChainableReader {
     private static final char CTRLZ = '\u001A';
 
     private int tabLength = 8;
 
     private CrLf eol;
 
     private AddAsisRemove ctrlz;
 
     private AddAsisRemove tabs;
 
     private boolean javafiles = false;
 
     private boolean fixlast = true;
 
     private boolean initialized = false;
 
     /**
      * Constructor for "dummy" instances.
      *
      * @see BaseFilterReader#BaseFilterReader()
      */
     public FixCrLfFilter() {
         super();
     }
 
     /**
      * Create a new filtered reader.
      *
      * @param in
      *            A Reader object providing the underlying stream. Must not be
      *            <code>null</code>.
      */
     public FixCrLfFilter(final Reader in) throws IOException {
         super(in);
     }
 
     // Instance initializer: Executes just after the super() call in this
     // class's constructor.
     {
         tabs = AddAsisRemove.ASIS;
         if (Os.isFamily("mac")) {
             ctrlz = AddAsisRemove.REMOVE;
             setEol(CrLf.MAC);
         } else if (Os.isFamily("dos")) {
             ctrlz = AddAsisRemove.ASIS;
             setEol(CrLf.DOS);
         } else {
             ctrlz = AddAsisRemove.REMOVE;
             setEol(CrLf.UNIX);
         }
     }
 
     /**
      * Create a new FixCrLfFilter using the passed in Reader for instantiation.
      *
      * @param rdr
      *            A Reader object providing the underlying stream. Must not be
      *            <code>null</code>.
      *
      * @return a new filter based on this configuration, but filtering the
      *         specified reader.
      */
     public final Reader chain(final Reader rdr) {
         try {
             FixCrLfFilter newFilter = new FixCrLfFilter(rdr);
 
             newFilter.setJavafiles(getJavafiles());
             newFilter.setEol(getEol());
             newFilter.setTab(getTab());
             newFilter.setTablength(getTablength());
             newFilter.setEof(getEof());
             newFilter.setFixlast(getFixlast());
             newFilter.initInternalFilters();
 
             return newFilter;
         } catch (IOException e) {
             throw new BuildException(e);
         }
     }
 
     /**
      * Get how DOS EOF (control-z) characters are being handled.
      *
      * @return values:
      *         <ul>
      *         <li>add: ensure that there is an eof at the end of the file
      *         <li>asis: leave eof characters alone
      *         <li>remove: remove any eof character found at the end
      *         </ul>
      */
     public AddAsisRemove getEof() {
         // Return copy so that the call must call setEof() to change the state
         // of fixCRLF
         return ctrlz.newInstance();
     }
 
     /**
      * Get how EndOfLine characters are being handled.
      *
      * @return values:
      *         <ul>
      *         <li>asis: convert line endings to your O/S convention
      *         <li>cr: convert line endings to CR
      *         <li>lf: convert line endings to LF
      *         <li>crlf: convert line endings to CRLF
      *         </ul>
      */
     public CrLf getEol() {
         // Return copy so that the call must call setEol() to change the state
         // of fixCRLF
         return eol.newInstance();
     }
 
     /**
      * Get whether a missing EOL be added to the final line of the stream.
      *
      * @return true if a filtered file will always end with an EOL
      */
     public boolean getFixlast() {
         return fixlast;
     }
 
     /**
      * Get whether the stream is to be treated as though it contains Java
      * source.
      * <P>
      * This attribute is only used in assocation with the &quot;<i><b>tab</b></i>&quot;
      * attribute. Tabs found in Java literals are protected from changes by this
      * filter.
      *
      * @return true if whitespace in Java character and string literals is
      *         ignored.
      */
     public boolean getJavafiles() {
         return javafiles;
     }
 
     /**
      * Return how tab characters are being handled.
      *
      * @return values:
      *         <ul>
      *         <li>add: convert sequences of spaces which span a tab stop to
      *         tabs
      *         <li>asis: leave tab and space characters alone
      *         <li>remove: convert tabs to spaces
      *         </ul>
      */
     public AddAsisRemove getTab() {
         // Return copy so that the caller must call setTab() to change the state
         // of fixCRLF.
         return tabs.newInstance();
     }
 
     /**
      * Get the tab length to use.
      *
      * @return the length of tab in spaces
      */
     public int getTablength() {
         return tabLength;
     }
 
     private static String calculateEolString(CrLf eol) {
         // Calculate the EOL string per the current config
         if (eol == CrLf.ASIS) {
             return System.getProperty("line.separator");
         }
         if (eol == CrLf.CR || eol == CrLf.MAC) {
             return "\r";
         }
         if (eol == CrLf.CRLF || eol == CrLf.DOS) {
             return "\r\n";
         }
         // assume (eol == CrLf.LF || eol == CrLf.UNIX)
         return "\n";
     }
 
     /**
      * Wrap the input stream with the internal filters necessary to perform the
      * configuration settings.
      */
     private void initInternalFilters() {
 
         // If I'm removing an EOF character, do so first so that the other
         // filters don't see that character.
         in = (ctrlz == AddAsisRemove.REMOVE) ? new RemoveEofFilter(in) : in;
 
         // Change all EOL characters to match the calculated EOL string. If
         // configured to do so, append a trailing EOL so that the file ends on
         // a EOL.
         in = new NormalizeEolFilter(in, calculateEolString(eol), getFixlast());
 
         if (tabs != AddAsisRemove.ASIS) {
             // If filtering Java source, prevent changes to whitespace in
             // character and string literals.
             if (getJavafiles()) {
                 in = new MaskJavaTabLiteralsFilter(in);
             }
             // Add/Remove tabs
             in = (tabs == AddAsisRemove.ADD) ? (Reader) new AddTabFilter(in, getTablength())
                     : (Reader) new RemoveTabFilter(in, getTablength());
         }
         // Add missing EOF character
         in = (ctrlz == AddAsisRemove.ADD) ? new AddEofFilter(in) : in;
         initialized = true;
     }
 
     /**
      * Return the next character in the filtered stream.
      *
      * @return the next character in the resulting stream, or -1 if the end of
      *         the resulting stream has been reached.
      *
      * @exception IOException
      *                if the underlying stream throws an IOException during
      *                reading.
      */
     public synchronized final int read() throws IOException {
         if (!initialized) {
             initInternalFilters();
         }
         return in.read();
     }
 
     /**
      * Specify how DOS EOF (control-z) characters are to be handled.
      *
      * @param attr
      *            valid values:
      *            <ul>
      *            <li>add: ensure that there is an eof at the end of the file
      *            <li>asis: leave eof characters alone
      *            <li>remove: remove any eof character found at the end
      *            </ul>
      */
     public void setEof(AddAsisRemove attr) {
         ctrlz = attr.resolve();
     }
 
     /**
      * Specify how end of line (EOL) characters are to be handled.
      *
      * @param attr
      *            valid values:
      *            <ul>
      *            <li>asis: convert line endings to your O/S convention
      *            <li>cr: convert line endings to CR
      *            <li>lf: convert line endings to LF
      *            <li>crlf: convert line endings to CRLF
      *            </ul>
      */
     public void setEol(CrLf attr) {
         eol = attr.resolve();
     }
 
     /**
      * Specify whether a missing EOL will be added to the final line of input.
      *
      * @param fixlast
      *            if true a missing EOL will be appended.
      */
     public void setFixlast(boolean fixlast) {
         this.fixlast = fixlast;
     }
 
     /**
      * Indicate whether this stream contains Java source.
      *
      * This attribute is only used in assocation with the &quot;<i><b>tab</b></i>&quot;
      * attribute.
      *
      * @param javafiles
      *            set to true to prevent this filter from changing tabs found in
      *            Java literals.
      */
     public void setJavafiles(boolean javafiles) {
         this.javafiles = javafiles;
     }
 
     /**
      * Specify how tab characters are to be handled.
      *
      * @param attr
      *            valid values:
      *            <ul>
      *            <li>add: convert sequences of spaces which span a tab stop to
      *            tabs
      *            <li>asis: leave tab and space characters alone
      *            <li>remove: convert tabs to spaces
      *            </ul>
      */
     public void setTab(AddAsisRemove attr) {
         tabs = attr.resolve();
     }
 
     /**
      * Specify tab length in characters.
      *
      * @param tabLength
      *            specify the length of tab in spaces. Valid values are between
      *            2 and 80 inclusive. The default for this parameter is 8.
      */
     public void setTablength(int tabLength) throws IOException {
         if (tabLength < 2 || tabLength > 80) {
             throw new IOException("tablength must be between 2 and 80");
         }
         this.tabLength = tabLength;
     }
 
     /**
      * This filter reader redirects all read I/O methods through its own read()
      * method.
      *
      * <P>
      * The input stream is already buffered by the copy task so this doesn't
      * significantly impact performance while it makes writing the individual
      * fix filters much easier.
      * </P>
      */
     private static class SimpleFilterReader extends Reader {
         private Reader in;
 
         int[] preempt = new int[16];
 
         int preemptIndex = 0;
 
         public SimpleFilterReader(Reader in) {
             this.in = in;
         }
 
         public void push(char c) {
             push((int) c);
         }
 
         public void push(int c) {
             try {
                 preempt[preemptIndex++] = c;
             } catch (ArrayIndexOutOfBoundsException e) {
                 int[] p2 = new int[preempt.length * 2];
                 System.arraycopy(preempt, 0, p2, 0, preempt.length);
                 preempt = p2;
                 push(c);
             }
         }
 
         public void push(char[] cs, int start, int length) {
             for (int i = start + length - 1; i >= start;) {
                 push(cs[i--]);
             }
         }
 
         public void push(char[] cs) {
             push(cs, 0, cs.length);
         }
 
         public void push(String s) {
             push(s.toCharArray());
         }
 
         /**
          * Does this filter want to block edits on the last character returned
          * by read()?
          */
         public boolean editsBlocked() {
             return in instanceof SimpleFilterReader && ((SimpleFilterReader) in).editsBlocked();
         }
 
         public int read() throws java.io.IOException {
             return preemptIndex > 0 ? preempt[--preemptIndex] : in.read();
         }
 
         public void close() throws java.io.IOException {
             in.close();
         }
 
         public void reset() throws IOException {
             in.reset();
         }
 
         public boolean markSupported() {
             return in.markSupported();
         }
 
         public boolean ready() throws java.io.IOException {
             return in.ready();
         }
 
         public void mark(int i) throws java.io.IOException {
             in.mark(i);
         }
 
         public long skip(long i) throws java.io.IOException {
             return in.skip(i);
         }
 
         public int read(char[] buf) throws java.io.IOException {
             return read(buf, 0, buf.length);
         }
 
         public int read(char[] buf, int start, int length) throws java.io.IOException {
             int count = 0;
             int c = 0;
 
             while (length-- > 0 && (c = this.read()) != -1) {
                 buf[start++] = (char) c;
                 count++;
             }
             // if at EOF with no characters in the buffer, return EOF
             return (count == 0 && c == -1) ? -1 : count;
         }
     }
 
     private static class MaskJavaTabLiteralsFilter extends SimpleFilterReader {
         boolean editsBlocked = false;
 
         private static final int JAVA = 1;
 
         private static final int IN_CHAR_CONST = 2;
 
         private static final int IN_STR_CONST = 3;
 
         private static final int IN_SINGLE_COMMENT = 4;
 
         private static final int IN_MULTI_COMMENT = 5;
 
         private static final int TRANS_TO_COMMENT = 6;
 
         private static final int TRANS_FROM_MULTI = 8;
 
         private int state;
 
         public MaskJavaTabLiteralsFilter(Reader in) {
             super(in);
             state = JAVA;
         }
 
         public boolean editsBlocked() {
             return editsBlocked || super.editsBlocked();
         }
 
         public int read() throws IOException {
             int thisChar = super.read();
             // Mask, block from being edited, all characters in constants.
             editsBlocked = (state == IN_CHAR_CONST || state == IN_STR_CONST);
 
             switch (state) {
             case JAVA:
                 // The current character is always emitted.
                 switch (thisChar) {
                 case '\'':
                     state = IN_CHAR_CONST;
                     break;
                 case '"':
                     state = IN_STR_CONST;
                     break;
                 case '/':
                     state = TRANS_TO_COMMENT;
                     break;
                 }
                 break;
             case IN_CHAR_CONST:
                 switch (thisChar) {
                 case '\'':
                     state = JAVA;
                     break;
                 }
                 break;
             case IN_STR_CONST:
                 switch (thisChar) {
                 case '"':
                     state = JAVA;
                     break;
                 }
                 break;
             case IN_SINGLE_COMMENT:
                 // The current character is always emitted.
                 switch (thisChar) {
                 case '\n':
                 case '\r': // EOL
                     state = JAVA;
                     break;
                 }
                 break;
             case IN_MULTI_COMMENT:
                 // The current character is always emitted.
                 switch (thisChar) {
                 case '*':
                     state = TRANS_FROM_MULTI;
                     break;
                 }
                 break;
             case TRANS_TO_COMMENT:
                 // The current character is always emitted.
                 switch (thisChar) {
                 case '*':
                     state = IN_MULTI_COMMENT;
                     break;
                 case '/':
                     state = IN_SINGLE_COMMENT;
                     break;
                 case '\'':
                     state = IN_CHAR_CONST;
                     break;
                 case '"':
                     state = IN_STR_CONST;
                     break;
                 default:
                     state = JAVA;
                 }
                 break;
             case TRANS_FROM_MULTI:
                 // The current character is always emitted.
                 switch (thisChar) {
                 case '/':
                     state = JAVA;
                     break;
                 }
                 break;
             }
             return thisChar;
         }
     }
 
     private static class NormalizeEolFilter extends SimpleFilterReader {
         boolean previousWasEOL;
 
         boolean fixLast;
 
         int normalizedEOL = 0;
 
         char[] eol = null;
 
         public NormalizeEolFilter(Reader in, String eolString, boolean fixLast) {
             super(in);
             eol = eolString.toCharArray();
             this.fixLast = fixLast;
         }
 
         public int read() throws IOException {
             int thisChar = super.read();
 
             if (normalizedEOL == 0) {
                 int numEOL = 0;
                 boolean atEnd = false;
                 switch (thisChar) {
                 case CTRLZ:
                     int c = super.read();
                     if (c == -1) {
                         atEnd = true;
                         if (fixLast && !previousWasEOL) {
                             numEOL = 1;
                             push(thisChar);
                         }
                     } else {
                         push(c);
                     }
                     break;
                 case -1:
                     atEnd = true;
                     if (fixLast && !previousWasEOL) {
                         numEOL = 1;
                     }
                     break;
                 case '\n':
                     // EOL was "\n"
                     numEOL = 1;
                     break;
                 case '\r':
                     numEOL = 1;
                     int c1 = super.read();
                     int c2 = super.read();
 
                     if (c1 == '\r' && c2 == '\n') {
                         // EOL was "\r\r\n"
                     } else if (c1 == '\r') {
                         // EOL was "\r\r" - handle as two consecutive "\r" and
                         // "\r"
                         numEOL = 2;
                         push(c2);
                     } else if (c1 == '\n') {
                         // EOL was "\r\n"
                         push(c2);
                     } else {
                         // EOL was "\r"
                         push(c2);
                         push(c1);
                     }
                 }
                 if (numEOL > 0) {
                     while (numEOL-- > 0) {
                         push(eol);
                         normalizedEOL += eol.length;
                     }
                     previousWasEOL = true;
                     thisChar = read();
                 } else if (!atEnd) {
                     previousWasEOL = false;
                 }
             } else {
                 normalizedEOL--;
             }
             return thisChar;
         }
     }
 
     private static class AddEofFilter extends SimpleFilterReader {
         int lastChar = -1;
 
         public AddEofFilter(Reader in) {
             super(in);
         }
 
         public int read() throws IOException {
             int thisChar = super.read();
 
             // if source is EOF but last character was NOT ctrl-z, return ctrl-z
             if (thisChar == -1) {
                 if (lastChar != CTRLZ) {
                     lastChar = CTRLZ;
                     return lastChar;
                 }
             } else {
                 lastChar = thisChar;
             }
             return thisChar;
         }
     }
 
     private static class RemoveEofFilter extends SimpleFilterReader {
         int lookAhead = -1;
 
         public RemoveEofFilter(Reader in) {
             super(in);
 
             try {
                 lookAhead = in.read();
             } catch (IOException e) {
                 lookAhead = -1;
             }
         }
 
         public int read() throws IOException {
             int lookAhead2 = super.read();
 
             // If source at EOF and lookAhead is ctrl-z, return EOF (NOT ctrl-z)
             if (lookAhead2 == -1 && lookAhead == CTRLZ) {
                 return -1;
             }
             // Return current look-ahead
             int i = lookAhead;
             lookAhead = lookAhead2;
             return i;
         }
     }
 
     private static class AddTabFilter extends SimpleFilterReader {
         int columnNumber = 0;
 
         int tabLength = 0;
 
         public AddTabFilter(Reader in, int tabLength) {
             super(in);
             this.tabLength = tabLength;
         }
 
         public int read() throws IOException {
             int c = super.read();
 
             switch (c) {
             case '\r':
             case '\n':
                 columnNumber = 0;
                 break;
             case ' ':
                 columnNumber++;
                 if (!editsBlocked()) {
                     int colNextTab = ((columnNumber + tabLength - 1) / tabLength) * tabLength;
                     int countSpaces = 1;
                     int numTabs = 0;
 
                     scanWhitespace: while ((c = super.read()) != -1) {
                         switch (c) {
                         case ' ':
                             if (++columnNumber == colNextTab) {
                                 numTabs++;
                                 countSpaces = 0;
                                 colNextTab += tabLength;
                             } else {
                                 countSpaces++;
                             }
                             break;
                         case '\t':
                             columnNumber = colNextTab;
                             numTabs++;
                             countSpaces = 0;
                             colNextTab += tabLength;
                             break;
                         default:
                             push(c);
                             break scanWhitespace;
                         }
                     }
                     while (countSpaces-- > 0) {
                         push(' ');
                         columnNumber--;
                     }
                     while (numTabs-- > 0) {
                         push('\t');
                         columnNumber -= tabLength;
                     }
                     c = super.read();
                     switch (c) {
                     case ' ':
                         columnNumber++;
                         break;
                     case '\t':
                         columnNumber += tabLength;
                         break;
                     }
                 }
                 break;
             case '\t':
                 columnNumber = ((columnNumber + tabLength - 1) / tabLength) * tabLength;
                 break;
             default:
                 columnNumber++;
             }
             return c;
         }
     }
 
     private static class RemoveTabFilter extends SimpleFilterReader {
         int columnNumber = 0;
 
         int tabLength = 0;
 
         public RemoveTabFilter(Reader in, int tabLength) {
             super(in);
 
             this.tabLength = tabLength;
         }
 
         public int read() throws IOException {
             int c = super.read();
 
             switch (c) {
             case '\r':
             case '\n':
                 columnNumber = 0;
                 break;
             case '\t':
                 int width = tabLength - columnNumber % tabLength;
 
                 if (!editsBlocked()) {
                     for (; width > 1; width--) {
                         push(' ');
                     }
                     c = ' ';
                 }
                 columnNumber += width;
                 break;
             default:
                 columnNumber++;
             }
             return c;
         }
     }
 
     /**
      * Enumerated attribute with the values "asis", "add" and "remove".
      */
     public static class AddAsisRemove extends EnumeratedAttribute {
         private static final AddAsisRemove ASIS = newInstance("asis");
 
         private static final AddAsisRemove ADD = newInstance("add");
 
         private static final AddAsisRemove REMOVE = newInstance("remove");
 
         public String[] getValues() {
             return new String[] { "add", "asis", "remove" };
         }
 
         public boolean equals(Object other) {
             return other instanceof AddAsisRemove
                     && getIndex() == ((AddAsisRemove) other).getIndex();
         }
 
+        public int hashCode() {
+            return getIndex();
+        }
+
         AddAsisRemove resolve() throws IllegalStateException {
             if (this.equals(ASIS)) {
                 return ASIS;
             }
             if (this.equals(ADD)) {
                 return ADD;
             }
             if (this.equals(REMOVE)) {
                 return REMOVE;
             }
             throw new IllegalStateException("No replacement for " + this);
         }
 
         // Works like clone() but doesn't show up in the Javadocs
         private AddAsisRemove newInstance() {
             return newInstance(getValue());
         }
 
         public static AddAsisRemove newInstance(String value) {
             AddAsisRemove a = new AddAsisRemove();
             a.setValue(value);
             return a;
         }
     }
 
     /**
      * Enumerated attribute with the values "asis", "cr", "lf" and "crlf".
      */
     public static class CrLf extends EnumeratedAttribute {
         private static final CrLf ASIS = newInstance("asis");
 
         private static final CrLf CR = newInstance("cr");
 
         private static final CrLf CRLF = newInstance("crlf");
 
         private static final CrLf DOS = newInstance("dos");
 
         private static final CrLf LF = newInstance("lf");
 
         private static final CrLf MAC = newInstance("mac");
 
         private static final CrLf UNIX = newInstance("unix");
 
         /**
          * @see EnumeratedAttribute#getValues
          */
         public String[] getValues() {
             return new String[] {"asis", "cr", "lf", "crlf", "mac", "unix", "dos"};
         }
 
         public boolean equals(Object other) {
             return other instanceof CrLf && getIndex() == ((CrLf) other).getIndex();
         }
 
+        public int hashCode() {
+            return getIndex();
+        }
+
         CrLf resolve() {
             if (this.equals(ASIS)) {
                 return ASIS;
             }
             if (this.equals(CR) || this.equals(MAC)) {
                 return CR;
             }
             if (this.equals(CRLF) || this.equals(DOS)) {
                 return CRLF;
             }
             if (this.equals(LF) || this.equals(UNIX)) {
                 return LF;
             }
             throw new IllegalStateException("No replacement for " + this);
         }
 
         // Works like clone() but doesn't show up in the Javadocs
         private CrLf newInstance() {
             return newInstance(getValue());
         }
 
         public static CrLf newInstance(String value) {
             CrLf c = new CrLf();
             c.setValue(value);
             return c;
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Copy.java b/src/main/org/apache/tools/ant/taskdefs/Copy.java
index bdfc2d8da..35850a101 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Copy.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Copy.java
@@ -1,970 +1,970 @@
 /*
  * Copyright  2000-2006 The Apache Software Foundation
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
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Vector;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.types.Mapper;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.FilterSet;
 import org.apache.tools.ant.types.FilterChain;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.IdentityMapper;
 import org.apache.tools.ant.util.ResourceUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.FlatFileNameMapper;
 
 /**
  * Copies a file or directory to a new file
  * or directory.  Files are only copied if the source file is newer
  * than the destination file, or when the destination file does not
  * exist.  It is possible to explicitly overwrite existing files.</p>
  *
  * <p>This implementation is based on Arnout Kuiper's initial design
  * document, the following mailing list discussions, and the
  * copyfile/copydir tasks.</p>
  *
  *
  * @since Ant 1.2
  *
  * @ant.task category="filesystem"
  */
 public class Copy extends Task {
     static final File NULL_FILE_PLACEHOLDER = new File("/NULL_FILE");
 
     protected File file = null;     // the source file
     protected File destFile = null; // the destination file
     protected File destDir = null;  // the destination directory
     protected Vector rcs = new Vector();
 
     private boolean enableMultipleMappings = false;
     protected boolean filtering = false;
     protected boolean preserveLastModified = false;
     protected boolean forceOverwrite = false;
     protected boolean flatten = false;
     protected int verbosity = Project.MSG_VERBOSE;
     protected boolean includeEmpty = true;
     protected boolean failonerror = true;
 
     protected Hashtable fileCopyMap = new Hashtable();
     protected Hashtable dirCopyMap = new Hashtable();
     protected Hashtable completeDirMap = new Hashtable();
 
     protected Mapper mapperElement = null;
     protected FileUtils fileUtils;
     private Vector filterChains = new Vector();
     private Vector filterSets = new Vector();
     private String inputEncoding = null;
     private String outputEncoding = null;
     private long granularity = 0;
 
     /**
      * Copy task constructor.
      */
     public Copy() {
         fileUtils = FileUtils.getFileUtils();
         granularity = fileUtils.getFileTimestampGranularity();
     }
 
     /**
      * Get the FileUtils for this task.
      * @return the fileutils object.
      */
     protected FileUtils getFileUtils() {
         return fileUtils;
     }
 
     /**
      * Set a single source file to copy.
      * @param file the file to copy.
      */
     public void setFile(File file) {
         this.file = file;
     }
 
     /**
      * Set the destination file.
      * @param destFile the file to copy to.
      */
     public void setTofile(File destFile) {
         this.destFile = destFile;
     }
 
     /**
      * Set the destination directory.
      * @param destDir the destination directory.
      */
     public void setTodir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Add a FilterChain.
      * @return a filter chain object.
      */
     public FilterChain createFilterChain() {
         FilterChain filterChain = new FilterChain();
         filterChains.addElement(filterChain);
         return filterChain;
     }
 
     /**
      * Add a filterset.
      * @return a filter set object.
      */
     public FilterSet createFilterSet() {
         FilterSet filterSet = new FilterSet();
         filterSets.addElement(filterSet);
         return filterSet;
     }
 
     /**
      * Give the copied files the same last modified time as the original files.
      * @param preserve a boolean string.
      * @deprecated since 1.5.x. 
      *             setPreserveLastModified(String) has been deprecated and
      *             replaced with setPreserveLastModified(boolean) to
      *             consistently let the Introspection mechanism work.
      */
     public void setPreserveLastModified(String preserve) {
         setPreserveLastModified(Project.toBoolean(preserve));
     }
 
     /**
      * Give the copied files the same last modified time as the original files.
      * @param preserve if true preserve the modified time; default is false.
      */
     public void setPreserveLastModified(boolean preserve) {
         preserveLastModified = preserve;
     }
 
     /**
      * Get whether to give the copied files the same last modified time as
      * the original files.
      * @return the whether destination files will inherit the modification
      *         times of the corresponding source files.
      * @since 1.32, Ant 1.5
      */
     public boolean getPreserveLastModified() {
         return preserveLastModified;
     }
 
     /**
      * Get the filtersets being applied to this operation.
      *
      * @return a vector of FilterSet objects.
      */
     protected Vector getFilterSets() {
         return filterSets;
     }
 
     /**
      * Get the filterchains being applied to this operation.
      *
      * @return a vector of FilterChain objects.
      */
     protected Vector getFilterChains() {
         return filterChains;
     }
 
     /**
      * Set filtering mode.
      * @param filtering if true enable filtering; default is false.
      */
     public void setFiltering(boolean filtering) {
         this.filtering = filtering;
     }
 
     /**
      * Set overwrite mode regarding existing destination file(s).
      * @param overwrite if true force overwriting of destination file(s)
      *                  even if the destination file(s) are younger than
      *                  the corresponding source file. Default is false.
      */
     public void setOverwrite(boolean overwrite) {
         this.forceOverwrite = overwrite;
     }
 
     /**
      * Set whether files copied from directory trees will be "flattened"
      * into a single directory.  If there are multiple files with
      * the same name in the source directory tree, only the first
      * file will be copied into the "flattened" directory, unless
      * the forceoverwrite attribute is true.
      * @param flatten if true flatten the destination directory. Default
      *                is false.
      */
     public void setFlatten(boolean flatten) {
         this.flatten = flatten;
     }
 
     /**
      * Set verbose mode. Used to force listing of all names of copied files.
      * @param verbose whether to output the names of copied files.
      *                Default is false.
      */
     public void setVerbose(boolean verbose) {
         this.verbosity = verbose ? Project.MSG_INFO : Project.MSG_VERBOSE;
     }
 
     /**
      * Set whether to copy empty directories.
      * @param includeEmpty if true copy empty directories. Default is true.
      */
     public void setIncludeEmptyDirs(boolean includeEmpty) {
         this.includeEmpty = includeEmpty;
     }
 
     /**
      * Set method of handling mappers that return multiple
      * mappings for a given source path.
      * @param enableMultipleMappings If true the task will
      *        copy to all the mappings for a given source path, if
      *        false, only the first file or directory is
      *        processed.
      *        By default, this setting is false to provide backward
      *        compatibility with earlier releases.
      * @since Ant 1.6
      */
     public void setEnableMultipleMappings(boolean enableMultipleMappings) {
         this.enableMultipleMappings = enableMultipleMappings;
     }
 
     /**
      * Get whether multiple mapping is enabled.
      * @return true if multiple mapping is enabled; false otherwise.
      */
     public boolean isEnableMultipleMapping() {
         return enableMultipleMappings;
     }
 
     /**
      * Set whether to fail when errors are encountered. If false, note errors
      * to the output but keep going. Default is true.
      * @param failonerror true or false.
      */
     public void setFailOnError(boolean failonerror) {
         this.failonerror = failonerror;
     }
 
     /**
      * Add a set of files to copy.
      * @param set a set of files to copy.
      */
     public void addFileset(FileSet set) {
         add(set);
     }
     
     /**
      * Add a collection of files to copy.
      * @param res a resource collection to copy.
      * @since Ant 1.7
      */
     public void add(ResourceCollection res) {
         rcs.add(res);
     }
     
     /**
      * Define the mapper to map source to destination files.
      * @return a mapper to be configured.
      * @exception BuildException if more than one mapper is defined.
      */
     public Mapper createMapper() throws BuildException {
         if (mapperElement != null) {
             throw new BuildException("Cannot define more than one mapper",
                                      getLocation());
         }
         mapperElement = new Mapper(getProject());
         return mapperElement;
     }
 
     /**
      * Add a nested filenamemapper.
      * @param fileNameMapper the mapper to add.
      * @since Ant 1.6.3
      */
     public void add(FileNameMapper fileNameMapper) {
         createMapper().add(fileNameMapper);
     }
 
     /**
      * Set the character encoding.
      * @param encoding the character encoding.
      * @since 1.32, Ant 1.5
      */
     public void setEncoding(String encoding) {
         this.inputEncoding = encoding;
         if (outputEncoding == null) {
             outputEncoding = encoding;
         }
     }
 
     /**
      * Get the character encoding to be used.
      * @return the character encoding, <code>null</code> if not set.
      *
      * @since 1.32, Ant 1.5
      */
     public String getEncoding() {
         return inputEncoding;
     }
 
     /**
      * Set the character encoding for output files.
      * @param encoding the output character encoding.
      * @since Ant 1.6
      */
     public void setOutputEncoding(String encoding) {
         this.outputEncoding = encoding;
     }
 
     /**
      * Get the character encoding for output files.
      * @return the character encoding for output files,
      * <code>null</code> if not set.
      *
      * @since Ant 1.6
      */
     public String getOutputEncoding() {
         return outputEncoding;
     }
 
     /**
      * Set the number of milliseconds leeway to give before deciding a
      * target is out of date.
      *
      * <p>Default is 1 second, or 2 seconds on DOS systems.</p>
      * @param granularity the granularity used to decide if a target is out of
      *                    date.
      * @since Ant 1.6.2
      */
     public void setGranularity(long granularity) {
         this.granularity = granularity;
     }
 
     /**
      * Perform the copy operation.
      * @exception BuildException if an error occurs.
      */
     public void execute() throws BuildException {
         File savedFile = file; // may be altered in validateAttributes
         File savedDestFile = destFile;
         File savedDestDir = destDir;
         ResourceCollection savedRc = null;
         if (file == null && destFile != null && rcs.size() == 1) {
             // will be removed in validateAttributes
             savedRc = (ResourceCollection) rcs.elementAt(0);
         }
         // make sure we don't have an illegal set of options
         validateAttributes();
 
         try {
             // deal with the single file
             if (file != null) {
                 if (file.exists()) {
                     if (destFile == null) {
                         destFile = new File(destDir, file.getName());
                     }
                     if (forceOverwrite || !destFile.exists()
                         || (file.lastModified() - granularity
                                 > destFile.lastModified())) {
                         fileCopyMap.put(file.getAbsolutePath(),
                                         new String[] {destFile.getAbsolutePath()});
                     } else {
                         log(file + " omitted as " + destFile
                             + " is up to date.", Project.MSG_VERBOSE);
                     }
                 } else {
                     String message = "Warning: Could not find file "
                         + file.getAbsolutePath() + " to copy.";
                     if (!failonerror) {
                         log(message);
                     } else {
                         throw new BuildException(message);
                     }
                 }
             }
             // deal with the ResourceCollections
 
             /* for historical and performance reasons we have to do
                things in a rather complex way.
             
                (1) Move is optimized to move directories if a fileset
                has been included completely, therefore FileSets need a
                special treatment.  This is also required to support
                the failOnError semantice (skip filesets with broken
                basedir but handle the remaining collections).
 
                (2) We carry around a few protected methods that work
                on basedirs and arrays of names.  To optimize stuff, all
                resources with the same basedir get collected in
                separate lists and then each list is handled in one go.
             */
 
             HashMap filesByBasedir = new HashMap();
             HashMap dirsByBasedir = new HashMap();
             HashSet baseDirs = new HashSet();
             ArrayList nonFileResources = new ArrayList();
             for (int i = 0; i < rcs.size(); i++) {
                 ResourceCollection rc = (ResourceCollection) rcs.elementAt(i);
 
                 // Step (1) - beware of the ZipFileSet
                 if (rc instanceof FileSet && rc.isFilesystemOnly()) {
                     FileSet fs = (FileSet) rc;
                     DirectoryScanner ds = null;
                     try {
                         ds = fs.getDirectoryScanner(getProject());
                     } catch (BuildException e) {
                         if (failonerror
                             || !e.getMessage().endsWith(" not found.")) {
                             throw e;
                         } else {
                             log("Warning: " + e.getMessage());
                             continue;
                         }
                     }
                     File fromDir = fs.getDir(getProject());
 
                     String[] srcFiles = ds.getIncludedFiles();
                     String[] srcDirs = ds.getIncludedDirectories();
                     if (!flatten && mapperElement == null
                         && ds.isEverythingIncluded() && !fs.hasPatterns()) {
                         completeDirMap.put(fromDir, destDir);
                     }
                     add(fromDir, srcFiles, filesByBasedir);
                     add(fromDir, srcDirs, dirsByBasedir);
                     baseDirs.add(fromDir);
                 } else { // not a fileset or contains non-file resources
 
                     if (!rc.isFilesystemOnly() && !supportsNonFileResources()) {
                         throw new BuildException(
                                    "Only FileSystem resources are supported.");
                     }
 
                     Iterator resources = rc.iterator();
                     while (resources.hasNext()) {
                         Resource r = (Resource) resources.next();
                         if (!r.isExists()) {
                             continue;
                         }
 
                         File baseDir = NULL_FILE_PLACEHOLDER;
                         String name = r.getName();
                         if (r instanceof FileResource) {
                             FileResource fr = (FileResource) r;
                             baseDir = getKeyFile(fr.getBaseDir());
                             if (fr.getBaseDir() == null) {
                                 name = fr.getFile().getAbsolutePath();
                             }
                         }
 
                         // copying of dirs is trivial and can be done
                         // for non-file resources as well as for real
                         // files.
                         if (r.isDirectory() || r instanceof FileResource) {
                             add(baseDir, name,
                                 r.isDirectory() ? dirsByBasedir 
                                                 : filesByBasedir);
                             baseDirs.add(baseDir);
                         } else { // a not-directory file resource
                             // needs special treatment
                             nonFileResources.add(r);
                         }
                     }
                 }
             }
 
             Iterator iter = baseDirs.iterator();
             while (iter.hasNext()) {
                 File f = (File) iter.next();
                 List files = (List) filesByBasedir.get(f);
                 List dirs = (List) dirsByBasedir.get(f);
 
                 String[] srcFiles = new String[0];
                 if (files != null) {
                     srcFiles = (String[]) files.toArray(srcFiles);
                 }
                 String[] srcDirs = new String[0];
                 if (dirs != null) {
                     srcDirs = (String[]) dirs.toArray(srcDirs);
                 }
                 scan(f == NULL_FILE_PLACEHOLDER ? null : f, destDir, srcFiles,
                      srcDirs);
             }
 
             // do all the copy operations now...
             try {
                 doFileOperations();
             } catch (BuildException e) {
                 if (!failonerror) {
                     log("Warning: " + e.getMessage(), Project.MSG_ERR);
                 } else {
                     throw e;
                 }
             }
 
             if (nonFileResources.size() > 0) {
                 Resource[] nonFiles =
-                    (Resource[]) nonFileResources.toArray(new Resource[0]);
+                    (Resource[]) nonFileResources.toArray(new Resource[nonFileResources.size()]);
                 // restrict to out-of-date resources
                 Map map = scan(nonFiles, destDir);
                 try {
                     doResourceOperations(map);
                 } catch (BuildException e) {
                     if (!failonerror) {
                         log("Warning: " + e.getMessage(), Project.MSG_ERR);
                     } else {
                         throw e;
                     }
                 }
             }
         } finally {
             // clean up again, so this instance can be used a second
             // time
             file = savedFile;
             destFile = savedDestFile;
             destDir = savedDestDir;
             if (savedRc != null) {
                 rcs.insertElementAt(savedRc, 0);
             }
             fileCopyMap.clear();
             dirCopyMap.clear();
             completeDirMap.clear();
         }
     }
 
     /************************************************************************
      **  protected and private methods
      ************************************************************************/
 
     /**
      * Ensure we have a consistent and legal set of attributes, and set
      * any internal flags necessary based on different combinations
      * of attributes.
      * @exception BuildException if an error occurs.
      */
     protected void validateAttributes() throws BuildException {
         if (file == null && rcs.size() == 0) {
             throw new BuildException(
                 "Specify at least one source--a file or a resource collection.");
         }
         if (destFile != null && destDir != null) {
             throw new BuildException(
                 "Only one of tofile and todir may be set.");
         }
         if (destFile == null && destDir == null) {
             throw new BuildException("One of tofile or todir must be set.");
         }
         if (file != null && file.isDirectory()) {
             throw new BuildException("Use a resource collection to copy directories.");
         }
         if (destFile != null && rcs.size() > 0) {
             if (rcs.size() > 1) {
                 throw new BuildException(
                     "Cannot concatenate multiple files into a single file.");
             } else {
                 ResourceCollection rc = (ResourceCollection) rcs.elementAt(0);
                 if (!rc.isFilesystemOnly()) {
                     throw new BuildException("Only FileSystem resources are"
                                              + " supported when concatenating"
                                              + " files.");
                 }
                 if (rc.size() == 0) {
                     throw new BuildException(
                         "Cannot perform operation from directory to file.");
                 } else if (rc.size() == 1) {
                     FileResource r = (FileResource) rc.iterator().next();
                     if (file == null) {
                         file = r.getFile();
                         rcs.removeElementAt(0);
                     } else {
                         throw new BuildException(
                             "Cannot concatenate multiple files into a single file.");
                     }
                 } else {
                     throw new BuildException(
                         "Cannot concatenate multiple files into a single file.");
                 }
             }
         }
         if (destFile != null) {
             destDir = destFile.getParentFile();
         }
     }
 
     /**
      * Compares source files to destination files to see if they should be
      * copied.
      *
      * @param fromDir  The source directory.
      * @param toDir    The destination directory.
      * @param files    A list of files to copy.
      * @param dirs     A list of directories to copy.
      */
     protected void scan(File fromDir, File toDir, String[] files,
                         String[] dirs) {
         FileNameMapper mapper = getMapper();
         buildMap(fromDir, toDir, files, mapper, fileCopyMap);
 
         if (includeEmpty) {
             buildMap(fromDir, toDir, dirs, mapper, dirCopyMap);
         }
     }
 
     /**
      * Compares source resources to destination files to see if they
      * should be copied.
      *
      * @param fromResources  The source resources.
      * @param toDir          The destination directory.
      *
      * @return a Map with the out-of-date resources as keys and an
      * array of target file names as values.
      *
      * @since Ant 1.7
      */
     protected Map scan(Resource[] fromResources, File toDir) {
         return buildMap(fromResources, toDir, getMapper());
     }
 
     /**
      * Add to a map of files/directories to copy.
      *
      * @param fromDir the source directory.
      * @param toDir   the destination directory.
      * @param names   a list of filenames.
      * @param mapper  a <code>FileNameMapper</code> value.
      * @param map     a map of source file to array of destination files.
      */
     protected void buildMap(File fromDir, File toDir, String[] names,
                             FileNameMapper mapper, Hashtable map) {
         String[] toCopy = null;
         if (forceOverwrite) {
             Vector v = new Vector();
             for (int i = 0; i < names.length; i++) {
                 if (mapper.mapFileName(names[i]) != null) {
                     v.addElement(names[i]);
                 }
             }
             toCopy = new String[v.size()];
             v.copyInto(toCopy);
         } else {
             SourceFileScanner ds = new SourceFileScanner(this);
             toCopy = ds.restrict(names, fromDir, toDir, mapper, granularity);
         }
         for (int i = 0; i < toCopy.length; i++) {
             File src = new File(fromDir, toCopy[i]);
             String[] mappedFiles = mapper.mapFileName(toCopy[i]);
 
             if (!enableMultipleMappings) {
                 map.put(src.getAbsolutePath(),
                         new String[] {new File(toDir, mappedFiles[0]).getAbsolutePath()});
             } else {
                 // reuse the array created by the mapper
                 for (int k = 0; k < mappedFiles.length; k++) {
                     mappedFiles[k] = new File(toDir, mappedFiles[k]).getAbsolutePath();
                 }
                 map.put(src.getAbsolutePath(), mappedFiles);
             }
         }
     }
 
     /**
      * Create a map of resources to copy.
      *
      * @param fromResources  The source resources.
      * @param toDir   the destination directory.
      * @param mapper  a <code>FileNameMapper</code> value.
      * @return a map of source resource to array of destination files.
      * @since Ant 1.7
      */
     protected Map buildMap(Resource[] fromResources, final File toDir,
                            FileNameMapper mapper) {
         HashMap map = new HashMap();
         Resource[] toCopy = null;
         if (forceOverwrite) {
             Vector v = new Vector();
             for (int i = 0; i < fromResources.length; i++) {
                 if (mapper.mapFileName(fromResources[i].getName()) != null) {
                     v.addElement(fromResources[i]);
                 }
             }
             toCopy = new Resource[v.size()];
             v.copyInto(toCopy);
         } else {
             toCopy =
                 ResourceUtils.selectOutOfDateSources(this, fromResources,
                                                      mapper,
                                                      new ResourceFactory() {
                            public Resource getResource(String name) {
                                return new FileResource(toDir, name);
                            }
                                                      },
                                                      granularity);
         }
         for (int i = 0; i < toCopy.length; i++) {
             String[] mappedFiles = mapper.mapFileName(toCopy[i].getName());
 
             if (!enableMultipleMappings) {
                 map.put(toCopy[i],
                         new String[] {new File(toDir, mappedFiles[0]).getAbsolutePath()});
             } else {
                 // reuse the array created by the mapper
                 for (int k = 0; k < mappedFiles.length; k++) {
                     mappedFiles[k] = new File(toDir, mappedFiles[k]).getAbsolutePath();
                 }
                 map.put(toCopy[i], mappedFiles);
             }
         }
         return map;
     }
 
     /**
      * Actually does the file (and possibly empty directory) copies.
      * This is a good method for subclasses to override.
      */
     protected void doFileOperations() {
         if (fileCopyMap.size() > 0) {
             log("Copying " + fileCopyMap.size()
                 + " file" + (fileCopyMap.size() == 1 ? "" : "s")
                 + " to " + destDir.getAbsolutePath());
 
             Enumeration e = fileCopyMap.keys();
             while (e.hasMoreElements()) {
                 String fromFile = (String) e.nextElement();
                 String[] toFiles = (String[]) fileCopyMap.get(fromFile);
 
                 for (int i = 0; i < toFiles.length; i++) {
                     String toFile = toFiles[i];
 
                     if (fromFile.equals(toFile)) {
                         log("Skipping self-copy of " + fromFile, verbosity);
                         continue;
                     }
                     try {
                         log("Copying " + fromFile + " to " + toFile, verbosity);
 
                         FilterSetCollection executionFilters =
                             new FilterSetCollection();
                         if (filtering) {
                             executionFilters
                                 .addFilterSet(getProject().getGlobalFilterSet());
                         }
                         for (Enumeration filterEnum = filterSets.elements();
                             filterEnum.hasMoreElements();) {
                             executionFilters
                                 .addFilterSet((FilterSet) filterEnum.nextElement());
                         }
                         fileUtils.copyFile(fromFile, toFile, executionFilters,
                                            filterChains, forceOverwrite,
                                            preserveLastModified, inputEncoding,
                                            outputEncoding, getProject());
                     } catch (IOException ioe) {
                         String msg = "Failed to copy " + fromFile + " to " + toFile
                             + " due to " + ioe.getMessage();
                         File targetFile = new File(toFile);
                         if (targetFile.exists() && !targetFile.delete()) {
                             msg += " and I couldn't delete the corrupt " + toFile;
                         }
                         if (failonerror) {
                             throw new BuildException(msg, ioe, getLocation());
                         }
                         log(msg, Project.MSG_ERR);
                     }
                 }
             }
         }
         if (includeEmpty) {
             Enumeration e = dirCopyMap.elements();
             int createCount = 0;
             while (e.hasMoreElements()) {
                 String[] dirs = (String[]) e.nextElement();
                 for (int i = 0; i < dirs.length; i++) {
                     File d = new File(dirs[i]);
                     if (!d.exists()) {
                         if (!d.mkdirs()) {
                             log("Unable to create directory "
                                 + d.getAbsolutePath(), Project.MSG_ERR);
                         } else {
                             createCount++;
                         }
                     }
                 }
             }
             if (createCount > 0) {
                 log("Copied " + dirCopyMap.size()
                     + " empty director"
                     + (dirCopyMap.size() == 1 ? "y" : "ies")
                     + " to " + createCount
                     + " empty director"
                     + (createCount == 1 ? "y" : "ies") + " under "
                     + destDir.getAbsolutePath());
             }
         }
     }
 
     /**
      * Actually does the resource copies.
      * This is a good method for subclasses to override.
      * @param map a map of source resource to array of destination files.
      * @since Ant 1.7
      */
     protected void doResourceOperations(Map map) {
         if (map.size() > 0) {
             log("Copying " + map.size()
                 + " resource" + (map.size() == 1 ? "" : "s")
                 + " to " + destDir.getAbsolutePath());
 
             Iterator iter = map.keySet().iterator();
             while (iter.hasNext()) {
                 Resource fromResource = (Resource) iter.next();
                 String[] toFiles = (String[]) map.get(fromResource);
 
                 for (int i = 0; i < toFiles.length; i++) {
                     String toFile = toFiles[i];
 
                     try {
                         log("Copying " + fromResource + " to " + toFile,
                             verbosity);
 
                         FilterSetCollection executionFilters =
                             new FilterSetCollection();
                         if (filtering) {
                             executionFilters
                                 .addFilterSet(getProject().getGlobalFilterSet());
                         }
                         for (Enumeration filterEnum = filterSets.elements();
                             filterEnum.hasMoreElements();) {
                             executionFilters
                                 .addFilterSet((FilterSet) filterEnum.nextElement());
                         }
                         ResourceUtils.copyResource(fromResource,
                                                    new FileResource(destDir,
                                                                     toFile),
                                                    executionFilters,
                                                    filterChains,
                                                    forceOverwrite,
                                                    preserveLastModified,
                                                    inputEncoding,
                                                    outputEncoding,
                                                    getProject());
                     } catch (IOException ioe) {
                         String msg = "Failed to copy " + fromResource
                             + " to " + toFile
                             + " due to " + ioe.getMessage();
                         File targetFile = new File(toFile);
                         if (targetFile.exists() && !targetFile.delete()) {
                             msg += " and I couldn't delete the corrupt " + toFile;
                         }
                         if (failonerror) {
                             throw new BuildException(msg, ioe, getLocation());
                         }
                         log(msg, Project.MSG_ERR);
                     }
                 }
             }
         }
     }
 
     /**
      * Whether this task can deal with non-file resources.
      *
      * <p>&lt;copy&gt; can while &lt;move&gt; can't since we don't
      * know how to remove non-file resources.</p>
      *
      * <p>This implementation returns true only if this task is
      * &lt;copy&gt;.  Any subclass of this class that also wants to
      * support non-file resources needs to override this method.  We
      * need to do so for backwards compatibility reasons since we
      * can't expect subclasses to support resources.</p>
      *
      * @since Ant 1.7
      */
     protected boolean supportsNonFileResources() {
         return getClass().equals(Copy.class);
     }
 
     /**
      * Adds the given strings to a list contained in the given map.
      * The file is the key into the map.
      */
     private static void add(File baseDir, String[] names, Map m) {
         if (names != null) {
             baseDir = getKeyFile(baseDir);
             List l = (List) m.get(baseDir);
             if (l == null) {
                 l = new ArrayList(names.length);
                 m.put(baseDir, l);
             }
             l.addAll(java.util.Arrays.asList(names));
         }
     }
 
     /**
      * Adds the given string to a list contained in the given map.
      * The file is the key into the map.
      */
     private static void add(File baseDir, String name, Map m) {
         if (name != null) {
             add(baseDir, new String[] {name}, m);
         }
     }
 
     /**
      * Either returns its argument or a plaeholder if the argument is null.
      */
     private static File getKeyFile(File f) {
         return f == null ? NULL_FILE_PLACEHOLDER : f;
     }
 
     /**
      * returns the mapper to use based on nested elements or the
      * flatten attribute.
      */
     private FileNameMapper getMapper() {
         FileNameMapper mapper = null;
         if (mapperElement != null) {
             mapper = mapperElement.getImplementation();
         } else if (flatten) {
             mapper = new FlatFileNameMapper();
         } else {
             mapper = new IdentityMapper();
         }
         return mapper;
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/DefaultExcludes.java b/src/main/org/apache/tools/ant/taskdefs/DefaultExcludes.java
index cd7c58bfb..0cbe72b56 100644
--- a/src/main/org/apache/tools/ant/taskdefs/DefaultExcludes.java
+++ b/src/main/org/apache/tools/ant/taskdefs/DefaultExcludes.java
@@ -1,110 +1,114 @@
 /*
- * Copyright  2003-2004 The Apache Software Foundation
+ * Copyright  2003-2004,2006 The Apache Software Foundation
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
 
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
+import org.apache.tools.ant.util.StringUtils;
 
 /**
  * Alters the default excludes for the <strong>entire</strong> build..
  *
  * @since Ant 1.6
  *
  * @ant.task category="utility"
  */
 public class DefaultExcludes extends Task {
     private String add = "";
     private String remove = "";
     private boolean defaultrequested = false;
     private boolean echo = false;
 
     // by default, messages are always displayed
     private int logLevel = Project.MSG_WARN;
 
     /**
      * Does the work.
      *
      * @exception BuildException if something goes wrong with the build
      */
     public void execute() throws BuildException {
         if (!defaultrequested && add.equals("") && remove.equals("") && !echo) {
             throw new BuildException("<defaultexcludes> task must set "
                 + "at least one attribute (echo=\"false\""
                 + " doesn't count since that is the default");
         }
         if (defaultrequested) {
             DirectoryScanner.resetDefaultExcludes();
         }
         if (!add.equals("")) {
             DirectoryScanner.addDefaultExclude(add);
         }
         if (!remove.equals("")) {
             DirectoryScanner.removeDefaultExclude(remove);
         }
         if (echo) {
             StringBuffer message
-                = new StringBuffer("Current Default Excludes:\n");
+                = new StringBuffer("Current Default Excludes:");
+            message.append(StringUtils.LINE_SEP);
             String[] excludes = DirectoryScanner.getDefaultExcludes();
             for (int i = 0; i < excludes.length; i++) {
-                message.append("  " + excludes[i] + "\n");
+                message.append("  ");
+                message.append(excludes[i]);
+                message.append(StringUtils.LINE_SEP);
             }
             log(message.toString(), logLevel);
         }
     }
 
     /**
      * go back to standard default patterns
      *
      * @param def if true go back to default patterns
      */
     public void setDefault(boolean def) {
         defaultrequested = def;
     }
     /**
      * Pattern to add to the default excludes
      *
      * @param add Sets the value for the pattern to exclude.
      */
     public void setAdd(String add) {
         this.add = add;
     }
 
      /**
      * Pattern to remove from the default excludes.
      *
      * @param remove Sets the value for the pattern that
      *            should no longer be excluded.
      */
     public void setRemove(String remove) {
         this.remove = remove;
     }
 
     /**
      * If true, echo the default excludes.
      *
      * @param echo whether or not to echo the contents of
      *             the default excludes.
      */
     public void setEcho(boolean echo) {
         this.echo = echo;
     }
 
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Exit.java b/src/main/org/apache/tools/ant/taskdefs/Exit.java
index 7d367846e..e9202ea11 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Exit.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Exit.java
@@ -1,216 +1,216 @@
 /*
- * Copyright  2000-2005 The Apache Software Foundation
+ * Copyright  2000-2006 The Apache Software Foundation
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
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ExitStatusException;
 import org.apache.tools.ant.taskdefs.condition.Condition;
 import org.apache.tools.ant.taskdefs.condition.ConditionBase;
 
 /**
  * Exits the active build, giving an additional message
  * if available.
  *
  * The <code>if</code> and <code>unless</code> attributes make the
  * failure conditional -both probe for the named property being defined.
  * The <code>if</code> tests for the property being defined, the
  * <code>unless</code> for a property being undefined.
  *
  * If both attributes are set, then the test fails only if both tests
  * are true. i.e.
  * <pre>fail := defined(ifProperty) && !defined(unlessProperty)</pre>
  *
  * A single nested<code>&lt;condition&gt;</code> element can be specified
  * instead of using <code>if</code>/<code>unless</code> (a combined
  * effect can be achieved using <code>isset</code> conditions).
  *
  * @since Ant 1.2
  *
  * @ant.task name="fail" category="control"
  */
 public class Exit extends Task {
 
-    private class NestedCondition extends ConditionBase implements Condition {
+    private static class NestedCondition extends ConditionBase implements Condition {
         public boolean eval() {
             if (countConditions() != 1) {
                 throw new BuildException(
                     "A single nested condition is required.");
             }
             return ((Condition) (getConditions().nextElement())).eval();
         }
     }
 
     private String message;
     private String ifCondition, unlessCondition;
     private NestedCondition nestedCondition;
     private Integer status;
 
     /**
      * A message giving further information on why the build exited.
      *
      * @param value message to output
      */
     public void setMessage(String value) {
         this.message = value;
     }
 
     /**
      * Only fail if a property of the given name exists in the current project.
      * @param c property name
      */
     public void setIf(String c) {
         ifCondition = c;
     }
 
     /**
      * Only fail if a property of the given name does not
      * exist in the current project.
      * @param c property name
      */
     public void setUnless(String c) {
         unlessCondition = c;
     }
 
     /**
      * Set the status code to associate with the thrown Exception.
      * @param i   the <code>int</code> status
      */
     public void setStatus(int i) {
         status = new Integer(i);
     }
 
     /**
      * Throw a <code>BuildException</code> to exit (fail) the build.
      * If specified, evaluate conditions:
      * A single nested condition is accepted, but requires that the
      * <code>if</code>/<code>unless</code> attributes be omitted.
      * If the nested condition evaluates to true, or the
      * ifCondition is true or unlessCondition is false, the build will exit.
      * The error message is constructed from the text fields, from
      * the nested condition (if specified), or finally from
      * the if and unless parameters (if present).
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
         boolean fail = (nestedConditionPresent()) ? testNestedCondition()
                      : (testIfCondition() && testUnlessCondition());
         if (fail) {
             String text = null;
             if (message != null && message.trim().length() > 0) {
                 text = message.trim();
             } else {
                 if (ifCondition != null && ifCondition.length() > 0
                     && getProject().getProperty(ifCondition) != null) {
                     text = "if=" + ifCondition;
                 }
                 if (unlessCondition != null && unlessCondition.length() > 0
                     && getProject().getProperty(unlessCondition) == null) {
                     if (text == null) {
                         text = "";
                     } else {
                         text += " and ";
                     }
                     text += "unless=" + unlessCondition;
                 }
                 if (nestedConditionPresent()) {
                     text = "condition satisfied";
                 } else {
                     if (text == null) {
                         text = "No message";
                     }
                 }
             }
             log("failing due to " + text, Project.MSG_DEBUG);
             throw ((status == null) ? new BuildException(text)
              : new ExitStatusException(text, status.intValue()));
         }
     }
 
     /**
      * Set a multiline message.
      * @param msg the message to display
      */
     public void addText(String msg) {
         if (message == null) {
             message = "";
         }
         message += getProject().replaceProperties(msg);
     }
 
     /**
      * Add a condition element.
      * @return <code>ConditionBase</code>.
      * @since Ant 1.6.2
      */
     public ConditionBase createCondition() {
         if (nestedCondition != null) {
             throw new BuildException("Only one nested condition is allowed.");
         }
         nestedCondition = new NestedCondition();
         return nestedCondition;
     }
 
     /**
      * test the if condition
      * @return true if there is no if condition, or the named property exists
      */
     private boolean testIfCondition() {
         if (ifCondition == null || "".equals(ifCondition)) {
             return true;
         }
         return getProject().getProperty(ifCondition) != null;
     }
 
     /**
      * test the unless condition
      * @return true if there is no unless condition,
      *  or there is a named property but it doesn't exist
      */
     private boolean testUnlessCondition() {
         if (unlessCondition == null || "".equals(unlessCondition)) {
             return true;
         }
         return getProject().getProperty(unlessCondition) == null;
     }
 
     /**
      * test the nested condition
      * @return true if there is none, or it evaluates to true
      */
     private boolean testNestedCondition() {
         boolean result = nestedConditionPresent();
 
         if (result && ifCondition != null || unlessCondition != null) {
             throw new BuildException("Nested conditions "
                 + "not permitted in conjunction with if/unless attributes");
         }
 
         return result && nestedCondition.eval();
     }
 
     /**
      * test whether there is a nested condition.
      * @return <code>boolean</code>.
      */
     private boolean nestedConditionPresent() {
         return (nestedCondition != null);
     }
 
 }
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/MacroInstance.java b/src/main/org/apache/tools/ant/taskdefs/MacroInstance.java
index 5bfac3d5c..d24ab6c5c 100644
--- a/src/main/org/apache/tools/ant/taskdefs/MacroInstance.java
+++ b/src/main/org/apache/tools/ant/taskdefs/MacroInstance.java
@@ -1,395 +1,397 @@
 /*
  * Copyright  2003-2006 The Apache Software Foundation
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
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Iterator;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Enumeration;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DynamicAttribute;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.RuntimeConfigurable;
 import org.apache.tools.ant.Target;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.TaskContainer;
 import org.apache.tools.ant.UnknownElement;
 
 /**
  * The class to be placed in the ant type definition.
  * It is given a pointer to the template definition,
  * and makes a copy of the unknown element, substituting
  * the parameter values in attributes and text.
  * @since Ant 1.6
  */
 public class MacroInstance extends Task implements DynamicAttribute, TaskContainer {
     private MacroDef macroDef;
     private Map      map = new HashMap();
     private Map      nsElements = null;
     private Map      presentElements;
     private Hashtable localAttributes;
     private String    text = null;
     private String    implicitTag =     null;
     private List      unknownElements = new ArrayList();
 
     /**
      * Called from MacroDef.MyAntTypeDefinition#create()
      *
      * @param macroDef a <code>MacroDef</code> value
      */
     public void setMacroDef(MacroDef macroDef) {
         this.macroDef = macroDef;
     }
 
     /**
      * @return the macro definition object for this macro instance.
      */
     public MacroDef getMacroDef() {
         return macroDef;
     }
 
     /**
      * A parameter name value pair as a xml attribute.
      *
      * @param name the name of the attribute
      * @param value the value of the attribute
      */
     public void setDynamicAttribute(String name, String value) {
         map.put(name, value);
     }
 
     /**
      * Method present for BC purposes.
      * @param name not used
      * @return nothing
      * @deprecated since 1.6.x.
      * @throws BuildException always
      */
     public Object createDynamicElement(String name) throws BuildException {
         throw new BuildException("Not implemented any more");
     }
 
     private Map getNsElements() {
         if (nsElements == null) {
             nsElements = new HashMap();
             for (Iterator i = macroDef.getElements().entrySet().iterator();
                  i.hasNext();) {
                 Map.Entry entry = (Map.Entry) i.next();
                 nsElements.put((String) entry.getKey(),
                                entry.getValue());
                 MacroDef.TemplateElement te = (MacroDef.TemplateElement)
                     entry.getValue();
                 if (te.isImplicit()) {
                     implicitTag = te.getName();
                 }
             }
         }
         return nsElements;
     }
 
     /**
      * Add a unknownElement for the macro instances nested elements.
      *
      * @param nestedTask a nested element.
      */
     public void addTask(Task nestedTask) {
         unknownElements.add(nestedTask);
     }
 
     private void processTasks() {
         if (implicitTag != null) {
             return;
         }
         for (Iterator i = unknownElements.iterator(); i.hasNext();) {
             UnknownElement ue = (UnknownElement) i.next();
             String name = ProjectHelper.extractNameFromComponentName(
                 ue.getTag()).toLowerCase(Locale.US);
             if (getNsElements().get(name) == null) {
                 throw new BuildException("unsupported element " + name);
             }
             if (presentElements.get(name) != null) {
                 throw new BuildException("Element " + name + " already present");
             }
             presentElements.put(name, ue.getChildren());
         }
     }
 
     /**
      * Embedded element in macro instance
      */
     public static class Element implements TaskContainer {
         private List unknownElements = new ArrayList();
 
         /**
          * Add an unknown element (to be snipped into the macroDef instance)
          *
          * @param nestedTask an unknown element
          */
         public void addTask(Task nestedTask) {
             unknownElements.add(nestedTask);
         }
 
         /**
          * @return the list of unknown elements
          */
         public List getUnknownElements() {
             return unknownElements;
         }
     }
 
     private static final int STATE_NORMAL         = 0;
     private static final int STATE_EXPECT_BRACKET = 1;
     private static final int STATE_EXPECT_NAME    = 2;
 
     private String macroSubs(String s, Map macroMapping) {
         if (s == null) {
             return null;
         }
         StringBuffer ret = new StringBuffer();
         StringBuffer macroName = null;
 
         int state = STATE_NORMAL;
         for (int i = 0; i < s.length(); ++i) {
             char ch = s.charAt(i);
             switch (state) {
                 case STATE_NORMAL:
                     if (ch == '@') {
                         state = STATE_EXPECT_BRACKET;
                     } else {
                         ret.append(ch);
                     }
                     break;
                 case STATE_EXPECT_BRACKET:
                     if (ch == '{') {
                         state = STATE_EXPECT_NAME;
                         macroName = new StringBuffer();
                     } else if (ch == '@') {
                         state = STATE_NORMAL;
                         ret.append('@');
                     } else {
                         state = STATE_NORMAL;
                         ret.append('@');
                         ret.append(ch);
                     }
                     break;
                 case STATE_EXPECT_NAME:
                     if (ch == '}') {
                         state = STATE_NORMAL;
                         String name = macroName.toString().toLowerCase(Locale.US);
                         String value = (String) macroMapping.get(name);
                         if (value == null) {
-                            ret.append("@{" + name + "}");
+                            ret.append("@{");
+                            ret.append(name);
+                            ret.append("}");
                         } else {
                             ret.append(value);
                         }
                         macroName = null;
                     } else {
                         macroName.append(ch);
                     }
                     break;
                 default:
                     break;
             }
         }
         switch (state) {
             case STATE_NORMAL:
                 break;
             case STATE_EXPECT_BRACKET:
                 ret.append('@');
                 break;
             case STATE_EXPECT_NAME:
                 ret.append("@{");
                 ret.append(macroName.toString());
                 break;
             default:
                 break;
         }
 
         return ret.toString();
     }
 
     /**
      * Set the text contents for the macro.
      * @param text the text to be added to the macro.
      */
 
     public void addText(String text) {
         this.text = text;
     }
 
     private UnknownElement copy(UnknownElement ue) {
         UnknownElement ret = new UnknownElement(ue.getTag());
         ret.setNamespace(ue.getNamespace());
         ret.setProject(getProject());
         ret.setQName(ue.getQName());
         ret.setTaskType(ue.getTaskType());
         ret.setTaskName(ue.getTaskName());
         ret.setLocation(ue.getLocation());
         if (getOwningTarget() == null) {
             Target t = new Target();
             t.setProject(getProject());
             ret.setOwningTarget(t);
         } else {
             ret.setOwningTarget(getOwningTarget());
         }
         RuntimeConfigurable rc = new RuntimeConfigurable(
             ret, ue.getTaskName());
         rc.setPolyType(ue.getWrapper().getPolyType());
         Map m = ue.getWrapper().getAttributeMap();
         for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             rc.setAttribute(
                 (String) entry.getKey(),
                 macroSubs((String) entry.getValue(), localAttributes));
         }
         rc.addText(macroSubs(ue.getWrapper().getText().toString(),
                              localAttributes));
 
         Enumeration e = ue.getWrapper().getChildren();
         while (e.hasMoreElements()) {
             RuntimeConfigurable r = (RuntimeConfigurable) e.nextElement();
             UnknownElement unknownElement = (UnknownElement) r.getProxy();
             String tag = unknownElement.getTaskType();
             if (tag != null) {
                 tag = tag.toLowerCase(Locale.US);
             }
             MacroDef.TemplateElement templateElement =
                 (MacroDef.TemplateElement) getNsElements().get(tag);
             if (templateElement == null) {
                 UnknownElement child = copy(unknownElement);
                 rc.addChild(child.getWrapper());
                 ret.addChild(child);
             } else if (templateElement.isImplicit()) {
                 if (unknownElements.size() == 0 && !templateElement.isOptional()) {
                     throw new BuildException(
                         "Missing nested elements for implicit element "
                         + templateElement.getName());
                 }
                 for (Iterator i = unknownElements.iterator();
                      i.hasNext();) {
                     UnknownElement child = (UnknownElement) i.next();
                     rc.addChild(child.getWrapper());
                     ret.addChild(child);
                 }
             } else {
                 List list = (List) presentElements.get(tag);
                 if (list == null) {
                     if (!templateElement.isOptional()) {
                         throw new BuildException(
                             "Required nested element "
                             + templateElement.getName() + " missing");
                     }
                     continue;
                 }
                 for (Iterator i = list.iterator();
                      i.hasNext();) {
                     UnknownElement child = (UnknownElement) i.next();
                     rc.addChild(child.getWrapper());
                     ret.addChild(child);
                 }
             }
         }
         return ret;
     }
 
     /**
      * Execute the templates instance.
      * Copies the unknown element, substitutes the attributes,
      * and calls perform on the unknown element.
      *
      */
     public void execute() {
         presentElements = new HashMap();
         getNsElements();
         processTasks();
         localAttributes = new Hashtable();
         Set copyKeys = new HashSet(map.keySet());
         for (Iterator i = macroDef.getAttributes().iterator(); i.hasNext();) {
             MacroDef.Attribute attribute = (MacroDef.Attribute) i.next();
             String value = (String) map.get(attribute.getName());
             if (value == null && "description".equals(attribute.getName())) {
                 value = getDescription();
             }
             if (value == null) {
                 value = attribute.getDefault();
                 value = macroSubs(value, localAttributes);
             } else if (attribute instanceof MacroDef.DefineAttribute) {
                 // Do not process given value, will fail as unknown attribute
                 continue;
             }
             if (value == null) {
                 throw new BuildException(
                     "required attribute " + attribute.getName() + " not set");
             }
             localAttributes.put(attribute.getName(), value);
             copyKeys.remove(attribute.getName());
         }
         if (copyKeys.contains("id")) {
             copyKeys.remove("id");
         }
         if (macroDef.getText() != null) {
             if (text == null) {
                 if (!macroDef.getText().getOptional()) {
                     throw new BuildException(
                         "required text missing");
                 }
                 text = "";
             }
             if (macroDef.getText().getTrim()) {
                 text = text.trim();
             }
             localAttributes.put(macroDef.getText().getName(), text);
         } else {
             if (text != null && !text.trim().equals("")) {
                 throw new BuildException(
                     "The \"" + getTaskName() + "\" macro does not support"
                     + " nested text data.");
             }
         }
         if (copyKeys.size() != 0) {
             throw new BuildException(
                 "Unknown attribute" + (copyKeys.size() > 1 ? "s " : " ")
                 + copyKeys);
         }
 
         // need to set the project on unknown element
         UnknownElement c = copy(macroDef.getNestedTask());
         c.init();
         try {
             c.perform();
         } catch (BuildException ex) {
             if (macroDef.getBackTrace()) {
                 throw ProjectHelper.addLocationToBuildException(
                     ex, getLocation());
             } else {
                 ex.setLocation(getLocation());
                 throw ex;
             }
         } finally {
             presentElements = null;
             localAttributes = null;
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index 5022b6282..c07565255 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,785 +1,787 @@
 /*
  * Copyright  2000-2006 The Apache Software Foundation
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
      * Print SQL stats (rows affected)
      */
     private boolean showtrailers = true;
     
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
      * should properties be expanded in text?
      * false for backwards compatibility
      *
      * @since Ant 1.7
      */
     private boolean expandProperties = false;
 
     /**
      * Set the name of the SQL file to be run.
      * Required unless statements are enclosed in the build file
      * @param srcFile the file containing the SQL command.
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Enable property expansion inside nested text
      *
      * @param expandProperties
      * @since Ant 1.7
      */
     public void setExpandProperties(boolean expandProperties) {
         this.expandProperties = expandProperties;
     }
 
     /**
      * is property expansion inside inline text enabled?
      *
      * @return true if properties are to be expanded.
      * @since Ant 1.7
      */
     public boolean getExpandProperties() {
         return expandProperties;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text unless {@link #expandProperties}
      * is set.
      * @param sql an inline string containing the SQL command.
      */
     public void addText(String sql) {
         //there is no need to expand properties here as that happens when Transaction.addText is
         //called; to do so here would be an error.
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
      * @param rc a collection of resources containing SQL commands,
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
      * Print trailing info (rows affected) for the SQL
      * Addresses Bug/Request #27446
      * @param showtrailers if true prints the SQL rows affected
      * @since Ant 1.7
      */
     public void setShowtrailers(boolean showtrailers) {
         this.showtrailers = showtrailers;
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
                 closeQuietly();
                 throw new BuildException(e, getLocation());
             } catch (SQLException e) {
                 closeQuietly();
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
-                sql.append(" " + line);
+                sql.append(" ");
+                sql.append(line);
             } else {
-                sql.append("\n" + line);
+                sql.append("\n");
+                sql.append(line);
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
         if (sql.length() > 0) {
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
 
             if (print && showtrailers) {
                 out.println(updateCountTotal + " rows affected");
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
      * @deprecated since 1.6.x.
      *             Use {@link #printResults(java.sql.ResultSet, java.io.PrintStream)
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
 
     /*
      * Closes an unused connection after an error and doesn't rethrow 
      * a possible SQLException
      * @since Ant 1.7
      */
     private void closeQuietly() {
         if (!isAutocommit() && conn != null && onError.equals("abort")) {
             try {
                 conn.rollback();
             } catch (SQLException ex) {
                 // ignore
             }
         }
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
             //there are places (in this file, and perhaps elsewhere, where it is assumed
             //that null is an acceptable parameter.
             if(src!=null) {
                 setSrcResource(new FileResource(src));
             }
         }
 
         /**
          * Set the source resource attribute.
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
             if (sql != null) {
                 if (getExpandProperties()) {
                     sql = getProject().replaceProperties(sql);
                 }
                 this.tSqlCommand += sql;
             }
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/UpToDate.java b/src/main/org/apache/tools/ant/taskdefs/UpToDate.java
index 0699ae61d..741764aaa 100644
--- a/src/main/org/apache/tools/ant/taskdefs/UpToDate.java
+++ b/src/main/org/apache/tools/ant/taskdefs/UpToDate.java
@@ -1,269 +1,269 @@
 /*
- * Copyright  2000-2005 The Apache Software Foundation
+ * Copyright  2000-2006 The Apache Software Foundation
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
 import java.util.Enumeration;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.condition.Condition;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.resources.Union;
 import org.apache.tools.ant.types.Mapper;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.MergingMapper;
 import org.apache.tools.ant.util.ResourceUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 
 /**
  * Sets the given property if the specified target has a timestamp
  * greater than all of the source files.
  *
  * @since Ant 1.2
  *
  * @ant.task category="control"
  */
 
 public class UpToDate extends Task implements Condition {
 
     private String property;
     private String value;
     private File sourceFile;
     private File targetFile;
     private Vector sourceFileSets = new Vector();
     private Union sourceResources = new Union();
 
     protected Mapper mapperElement = null;
 
     /**
      * The property to set if the target file is more up-to-date than
      * (each of) the source file(s).
      *
      * @param property the name of the property to set if Target is up-to-date.
      */
     public void setProperty(final String property) {
         this.property = property;
     }
 
     /**
      * The value to set the named property to if the target file is more
      * up-to-date than (each of) the source file(s). Defaults to 'true'.
      *
      * @param value the value to set the property to if Target is up-to-date
      */
     public void setValue(final String value) {
         this.value = value;
     }
 
     /**
      * Returns the value, or "true" if a specific value wasn't provided.
      */
     private String getValue() {
         return (value != null) ? value : "true";
     }
 
     /**
      * The file which must be more up-to-date than (each of) the source file(s)
      * if the property is to be set.
      *
      * @param file the file we are checking against.
      */
     public void setTargetFile(final File file) {
         this.targetFile = file;
     }
 
     /**
      * The file that must be older than the target file
      * if the property is to be set.
      *
      * @param file the file we are checking against the target file.
      */
     public void setSrcfile(final File file) {
         this.sourceFile = file;
     }
 
     /**
      * Nested &lt;srcfiles&gt; element.
      * @param fs the source files
      */
     public void addSrcfiles(final FileSet fs) {
         sourceFileSets.addElement(fs);
     }
 
     /**
      * Nested resource collections as sources.
      * @since Ant 1.7
      */
     public Union createSrcResources() {
         return sourceResources;
     }
 
     /**
      * Defines the FileNameMapper to use (nested mapper element).
      * @return a mapper to be configured
      * @throws BuildException if more than one mapper is defined
      */
     public Mapper createMapper() throws BuildException {
         if (mapperElement != null) {
             throw new BuildException("Cannot define more than one mapper",
                                      getLocation());
         }
         mapperElement = new Mapper(getProject());
         return mapperElement;
     }
 
     /**
      * A nested filenamemapper
      * @param fileNameMapper the mapper to add
      * @since Ant 1.6.3
      */
     public void add(FileNameMapper fileNameMapper) {
         createMapper().add(fileNameMapper);
     }
 
     /**
      * Evaluate (all) target and source file(s) to
      * see if the target(s) is/are up-to-date.
      * @return true if the target(s) is/are up-to-date
      */
     public boolean eval() {
         if (sourceFileSets.size() == 0 && sourceResources.size() == 0
             && sourceFile == null) {
             throw new BuildException("At least one srcfile or a nested "
                                      + "<srcfiles> or <srcresources> element "
                                      + "must be set.");
         }
 
         if ((sourceFileSets.size() > 0 || sourceResources.size() > 0)
             && sourceFile != null) {
             throw new BuildException("Cannot specify both the srcfile "
                                      + "attribute and a nested <srcfiles> "
                                      + "or <srcresources> element.");
         }
 
         if (targetFile == null && mapperElement == null) {
             throw new BuildException("The targetfile attribute or a nested "
                                      + "mapper element must be set.");
         }
 
         // if the target file is not there, then it can't be up-to-date
         if (targetFile != null && !targetFile.exists()) {
             log("The targetfile \"" + targetFile.getAbsolutePath()
                     + "\" does not exist.", Project.MSG_VERBOSE);
             return false;
         }
 
         // if the source file isn't there, throw an exception
         if (sourceFile != null && !sourceFile.exists()) {
             throw new BuildException(sourceFile.getAbsolutePath()
                                      + " not found.");
         }
 
         boolean upToDate = true;
         if (sourceFile != null) {
             if (mapperElement == null) {
                 upToDate = upToDate
                     && (targetFile.lastModified() >= sourceFile.lastModified());
             } else {
                 SourceFileScanner sfs = new SourceFileScanner(this);
                 upToDate = upToDate
                     && (sfs.restrict(new String[] {sourceFile.getAbsolutePath()},
                                   null, null,
                                   mapperElement.getImplementation()).length == 0);
             }
         }
 
         // filesets are separate from the rest for performance
         // reasons.  If we use the code for union below, we'll always
         // scan all filesets, even if we know the target is out of
         // date after the first test.
         Enumeration e = sourceFileSets.elements();
         while (upToDate && e.hasMoreElements()) {
             FileSet fs = (FileSet) e.nextElement();
             DirectoryScanner ds = fs.getDirectoryScanner(getProject());
             upToDate = upToDate && scanDir(fs.getDir(getProject()),
                                            ds.getIncludedFiles());
         }
 
         if (upToDate) {
             Resource[] r = sourceResources.listResources();
             upToDate = upToDate &&
-                (ResourceUtils.selectOutOfDateSources(this, r, getMapper(),
-                                                      getProject()).length
+                (ResourceUtils.selectOutOfDateResources(this, r, getMapper(),
+                                                      getProject(), null).length
                  == 0);
         }
 
         return upToDate;
     }
 
 
     /**
      * Sets property to true if target file(s) have a more recent timestamp
      * than (each of) the corresponding source file(s).
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
         if (property == null) {
             throw new BuildException("property attribute is required.",
                                      getLocation());
         }
         boolean upToDate = eval();
         if (upToDate) {
             getProject().setNewProperty(property, getValue());
             if (mapperElement == null) {
                 log("File \"" + targetFile.getAbsolutePath()
                     + "\" is up-to-date.", Project.MSG_VERBOSE);
             } else {
                 log("All target files are up-to-date.",
                     Project.MSG_VERBOSE);
             }
         }
     }
 
     /**
      * Scan a directory for files to check for "up to date"ness
      * @param srcDir the directory
      * @param files the files to scan for
      * @return true if the files are up to date
      */
     protected boolean scanDir(File srcDir, String[] files) {
         SourceFileScanner sfs = new SourceFileScanner(this);
         FileNameMapper mapper = getMapper();
         File dir = srcDir;
         if (mapperElement == null) {
             dir = null;
         }
         return sfs.restrict(files, srcDir, dir, mapper).length == 0;
     }
 
     private FileNameMapper getMapper() {
         FileNameMapper mapper = null;
         if (mapperElement == null) {
             MergingMapper mm = new MergingMapper();
             mm.setTo(targetFile.getAbsolutePath());
             mapper = mm;
         } else {
             mapper = mapperElement.getImplementation();
         }
         return mapper;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/VerifyJar.java b/src/main/org/apache/tools/ant/taskdefs/VerifyJar.java
index 9a86fbe99..61c100342 100644
--- a/src/main/org/apache/tools/ant/taskdefs/VerifyJar.java
+++ b/src/main/org/apache/tools/ant/taskdefs/VerifyJar.java
@@ -1,207 +1,207 @@
 /*
- * Copyright  2000-2005 The Apache Software Foundation
+ * Copyright  2000-2006 The Apache Software Foundation
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
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.filters.ChainableReader;
 import org.apache.tools.ant.types.RedirectorElement;
 import org.apache.tools.ant.types.FilterChain;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.resources.FileResource;
 
 import java.util.Iterator;
 import java.io.File;
 import java.io.Reader;
 import java.io.IOException;
 
 /**
  * JAR verification task.
  * For every JAR passed in, we fork jarsigner to verify
  * that it is correctly signed. This is more rigorous than just checking for
  * the existence of a signature; the entire certification chain is tested
  * @since Ant 1.7
  */
 
 public class VerifyJar extends AbstractJarSignerTask {
     /**
      * no file message {@value}
      */
     public static final String ERROR_NO_FILE = "Not found :";
 
     /**
      * The string we look for in the text to indicate direct verification
      */
     private static final String VERIFIED_TEXT = "jar verified.";
 
     /**
      * certification flag
      */
     private boolean certificates=false;
     private BufferingOutputFilter outputCache = new BufferingOutputFilter();
     public static final String ERROR_NO_VERIFY = "Failed to verify ";
 
     /**
      * Ask for certificate information to be printed
      * @param certificates
      */
     public void setCertificates(boolean certificates) {
         this.certificates = certificates;
     }
 
     /**
      * verify our jar files
      * @throws BuildException
      */
     public void execute() throws BuildException {
         //validation logic
         final boolean hasJar = jar != null;
 
         if (!hasJar && !hasResources()) {
             throw new BuildException(ERROR_NO_SOURCE);
         }
 
         beginExecution();
 
         //patch the redirector to save output to a file
         RedirectorElement redirector = getRedirector();
         redirector.setAlwaysLog(true);
         FilterChain outputFilterChain = redirector.createOutputFilterChain();
         outputFilterChain.add(outputCache);
 
         try {
             Path sources = createUnifiedSourcePath();
             Iterator iter = sources.iterator();
             while (iter.hasNext()) {
                 FileResource fr = (FileResource) iter.next();
                 verifyOneJar(fr.getFile());
             }
 
         } finally {
             endExecution();
         }
 
     }
 
     /**
      * verify a JAR.
      * @param jar
      * @throws BuildException if the file could not be verified
      */
     private void verifyOneJar(File jar) {
         if(!jar.exists()) {
             throw new BuildException(ERROR_NO_FILE+jar);
         }
         final ExecTask cmd = createJarSigner();
 
         setCommonOptions(cmd);
         bindToKeystore(cmd);
 
         //verify special operations
         addValue(cmd, "-verify");
 
         if(certificates) {
             addValue(cmd, "-certs");
         }
 
         //JAR  is required
         addValue(cmd, jar.getPath());
 
         log("Verifying JAR: " +
                 jar.getAbsolutePath());
         outputCache.clear();
         BuildException ex = null;
         try {
             cmd.execute();
         } catch (BuildException e) {
             ex = e;
         }
         String results = outputCache.toString();
         //deal with jdk1.4.2 bug:
         if (ex != null) {
             if (results.indexOf("zip file closed") >= 0) {
                 log("You are running " + JARSIGNER_COMMAND + " against a JVM with"
                     + " a known bug that manifests as an IllegalStateException.",
                     Project.MSG_WARN);
             } else {
                 throw ex;
             }
         }
         if (results.indexOf(VERIFIED_TEXT) < 0) {
             throw new BuildException(ERROR_NO_VERIFY + jar);
         }
     }
 
     /**
      * we are not thread safe here. Do not use on multiple threads at the same time.
      */
-    private class BufferingOutputFilter implements ChainableReader {
+    private static class BufferingOutputFilter implements ChainableReader {
 
         private BufferingOutputFilterReader buffer;
 
         public Reader chain(Reader rdr) {
             buffer = new BufferingOutputFilterReader(rdr);
             return buffer;
         }
 
         public String toString() {
             return buffer.toString();
         }
 
         public void clear() {
             if(buffer!=null) {
                 buffer.clear();
             }
         }
     }
 
     /**
      * catch the output of the buffer
      */
-    private class BufferingOutputFilterReader extends Reader {
+    private static class BufferingOutputFilterReader extends Reader {
 
         private Reader next;
 
         private StringBuffer buffer=new StringBuffer();
 
         public BufferingOutputFilterReader(Reader next) {
             this.next = next;
         }
 
         public int read(char cbuf[], int off, int len) throws IOException {
             //hand down
             int result=next.read(cbuf,off,len);
             //cache
             buffer.append(cbuf,off,len);
             //return
             return result;
         }
 
         public void close() throws IOException {
             next.close();
         }
 
         public String toString() {
             return buffer.toString();
         }
 
         public void clear() {
             buffer=new StringBuffer();
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Zip.java b/src/main/org/apache/tools/ant/taskdefs/Zip.java
index b3a91a8b7..9d3386cc2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Zip.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Zip.java
@@ -1,1418 +1,1418 @@
 /*
  * Copyright  2000-2006 The Apache Software Foundation
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.zip.CRC32;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.FileScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.ArchiveFileSet;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.PatternSet;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.ZipFileSet;
 import org.apache.tools.ant.types.ZipScanner;
 import org.apache.tools.ant.types.resources.ArchiveResource;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.GlobPatternMapper;
 import org.apache.tools.ant.util.IdentityMapper;
 import org.apache.tools.ant.util.MergingMapper;
 import org.apache.tools.ant.util.ResourceUtils;
 import org.apache.tools.zip.ZipEntry;
 import org.apache.tools.zip.ZipExtraField;
 import org.apache.tools.zip.ZipFile;
 import org.apache.tools.zip.ZipOutputStream;
 
 /**
  * Create a Zip file.
  *
  * @since Ant 1.1
  *
  * @ant.task category="packaging"
  */
 public class Zip extends MatchingTask {
 
     protected File zipFile;
     // use to scan own archive
     private ZipScanner zs;
     private File baseDir;
     protected Hashtable entries = new Hashtable();
     private Vector groupfilesets = new Vector();
     private Vector filesetsFromGroupfilesets = new Vector();
     protected String duplicate = "add";
     private boolean doCompress = true;
     private boolean doUpdate = false;
     // shadow of the above if the value is altered in execute
     private boolean savedDoUpdate = false;
     private boolean doFilesonly = false;
     protected String archiveType = "zip";
 
     // For directories:
     private static final long EMPTY_CRC = new CRC32 ().getValue ();
     protected String emptyBehavior = "skip";
     private Vector resources = new Vector();
     protected Hashtable addedDirs = new Hashtable();
     private Vector addedFiles = new Vector();
 
     protected boolean doubleFilePass = false;
     protected boolean skipWriting = false;
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * true when we are adding new files into the Zip file, as opposed
      * to adding back the unchanged files
      */
     private boolean addingNewFiles = false;
 
     /**
      * Encoding to use for filenames, defaults to the platform's
      * default encoding.
      */
     private String encoding;
 
     /**
      * Whether the original compression of entries coming from a ZIP
      * archive should be kept (for example when updating an archive).
      *
      * @since Ant 1.6
      */
     private boolean keepCompression = false;
 
     /**
      * Whether the file modification times will be rounded up to the
      * next even number of seconds.
      *
      * @since Ant 1.6.2
      */
     private boolean roundUp = true;
 
     /**
      * Comment for the archive.
      * @since Ant 1.6.3
      */
     private String comment = "";
 
     private int level = ZipOutputStream.DEFAULT_COMPRESSION;
 
     /**
      * This is the name/location of where to
      * create the .zip file.
      * @param zipFile the path of the zipFile
      * @deprecated since 1.5.x.
      *             Use setDestFile(File) instead.
      * @ant.attribute ignore="true"
      */
     public void setZipfile(File zipFile) {
         setDestFile(zipFile);
     }
 
     /**
      * This is the name/location of where to
      * create the file.
      * @param file the path of the zipFile
      * @since Ant 1.5
      * @deprecated since 1.5.x.
      *             Use setDestFile(File) instead.
      * @ant.attribute ignore="true"
      */
     public void setFile(File file) {
         setDestFile(file);
     }
 
 
     /**
      * The file to create; required.
      * @since Ant 1.5
      * @param destFile The new destination File
      */
     public void setDestFile(File destFile) {
        this.zipFile = destFile;
     }
 
     /**
      * The file to create.
      * @return the destination file
      * @since Ant 1.5.2
      */
     public File getDestFile() {
         return zipFile;
     }
 
 
     /**
      * Directory from which to archive files; optional.
      * @param baseDir the base directory
      */
     public void setBasedir(File baseDir) {
         this.baseDir = baseDir;
     }
 
     /**
      * Whether we want to compress the files or only store them;
      * optional, default=true;
      * @param c if true, compress the files
      */
     public void setCompress(boolean c) {
         doCompress = c;
     }
 
     /**
      * Whether we want to compress the files or only store them;
      * @return true if the files are to be compressed
      * @since Ant 1.5.2
      */
     public boolean isCompress() {
         return doCompress;
     }
 
     /**
      * If true, emulate Sun's jar utility by not adding parent directories;
      * optional, defaults to false.
      * @param f if true, emulate sun's jar by not adding parent directories
      */
     public void setFilesonly(boolean f) {
         doFilesonly = f;
     }
 
     /**
      * If true, updates an existing file, otherwise overwrite
      * any existing one; optional defaults to false.
      * @param c if true, updates an existing zip file
      */
     public void setUpdate(boolean c) {
         doUpdate = c;
         savedDoUpdate = c;
     }
 
     /**
      * Are we updating an existing archive?
      * @return true if updating an existing archive
      */
     public boolean isInUpdateMode() {
         return doUpdate;
     }
 
     /**
      * Adds a set of files.
      * @param set the fileset to add
      */
     public void addFileset(FileSet set) {
         add(set);
     }
 
     /**
      * Adds a set of files that can be
      * read from an archive and be given a prefix/fullpath.
      * @param set the zipfileset to add
      */
     public void addZipfileset(ZipFileSet set) {
         add(set);
     }
 
     /**
      * Add a collection of resources to be archived.
      * @param a the resources to archive
      * @since Ant 1.7
      */
     public void add(ResourceCollection a) {
         resources.add(a);
     }
 
     /**
      * Adds a group of zip files.
      * @param set the group (a fileset) to add
      */
     public void addZipGroupFileset(FileSet set) {
         groupfilesets.addElement(set);
     }
 
     /**
      * Sets behavior for when a duplicate file is about to be added -
      * one of <code>keep</code>, <code>skip</code> or <code>overwrite</code>.
      * Possible values are: <code>keep</code> (keep both
      * of the files); <code>skip</code> (keep the first version
      * of the file found); <code>overwrite</code> overwrite the file
      * with the new file
      * Default for zip tasks is <code>keep</code>
      * @param df a <code>Duplicate</code> enumerated value
      */
     public void setDuplicate(Duplicate df) {
         duplicate = df.getValue();
     }
 
     /**
      * Possible behaviors when there are no matching files for the task:
      * "fail", "skip", or "create".
      */
     public static class WhenEmpty extends EnumeratedAttribute {
         /**
          * The string values for the enumerated value
          * @return the values
          */
         public String[] getValues() {
             return new String[] {"fail", "skip", "create"};
         }
     }
 
     /**
      * Sets behavior of the task when no files match.
      * Possible values are: <code>fail</code> (throw an exception
      * and halt the build); <code>skip</code> (do not create
      * any archive, but issue a warning); <code>create</code>
      * (make an archive with no entries).
      * Default for zip tasks is <code>skip</code>;
      * for jar tasks, <code>create</code>.
      * @param we a <code>WhenEmpty</code> enumerated value
      */
     public void setWhenempty(WhenEmpty we) {
         emptyBehavior = we.getValue();
     }
 
     /**
      * Encoding to use for filenames, defaults to the platform's
      * default encoding.
      *
      * <p>For a list of possible values see <a
      * href="http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html">http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html</a>.</p>
      * @param encoding the encoding name
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Encoding to use for filenames.
      * @return the name of the encoding to use
      * @since Ant 1.5.2
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Whether the original compression of entries coming from a ZIP
      * archive should be kept (for example when updating an archive).
      * Default is false.
      * @param keep if true, keep the original compression
      * @since Ant 1.6
      */
     public void setKeepCompression(boolean keep) {
         keepCompression = keep;
     }
 
     /**
      * Comment to use for archive.
      *
      * @param comment The content of the comment.
      * @since Ant 1.6.3
      */
     public void setComment(String comment) {
         this.comment = comment;
     }
 
     /**
      * Comment of the archive
      *
      * @return Comment of the archive.
      * @since Ant 1.6.3
      */
     public String getComment() {
         return comment;
     }
 
     /**
      * Set the compression level to use.  Default is
      * ZipOutputStream.DEFAULT_COMPRESSION.
      * @param level compression level.
      * @since Ant 1.7
      */
     public void setLevel(int level) {
         this.level = level;
     }
 
     /**
      * Get the compression level.
      * @return compression level.
      * @since Ant 1.7
      */
     public int getLevel() {
         return level;
     }
 
     /**
      * Whether the file modification times will be rounded up to the
      * next even number of seconds.
      *
      * <p>Zip archives store file modification times with a
      * granularity of two seconds, so the times will either be rounded
      * up or down.  If you round down, the archive will always seem
      * out-of-date when you rerun the task, so the default is to round
      * up.  Rounding up may lead to a different type of problems like
      * JSPs inside a web archive that seem to be slightly more recent
      * than precompiled pages, rendering precompilation useless.</p>
      * @param r a <code>boolean</code> value
      * @since Ant 1.6.2
      */
     public void setRoundUp(boolean r) {
         roundUp = r;
     }
 
     /**
      * validate and build
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
 
         if (doubleFilePass) {
             skipWriting = true;
             executeMain();
             skipWriting = false;
             executeMain();
         } else {
             executeMain();
         }
     }
 
     /**
      * Build the zip file.
      * This is called twice if doubleFilePass is true.
      * @throws BuildException on error
      */
     public void executeMain() throws BuildException {
 
-        if (baseDir == null & resources.size() == 0
+        if (baseDir == null && resources.size() == 0
             && groupfilesets.size() == 0 && "zip".equals(archiveType)) {
             throw new BuildException("basedir attribute must be set, "
                                      + "or at least one "
                                      + "resource collection must be given!");
         }
 
         if (zipFile == null) {
             throw new BuildException("You must specify the "
                                      + archiveType + " file to create!");
         }
 
         if (zipFile.exists() && !zipFile.isFile()) {
             throw new BuildException(zipFile + " is not a file.");
         }
 
         if (zipFile.exists() && !zipFile.canWrite()) {
             throw new BuildException(zipFile + " is read-only.");
         }
 
         // Renamed version of original file, if it exists
         File renamedFile = null;
         addingNewFiles = true;
 
         // Whether or not an actual update is required -
         // we don't need to update if the original file doesn't exist
         if (doUpdate && !zipFile.exists()) {
             doUpdate = false;
             log("ignoring update attribute as " + archiveType
                 + " doesn't exist.", Project.MSG_DEBUG);
         }
 
         // Add the files found in groupfileset to fileset
         for (int i = 0; i < groupfilesets.size(); i++) {
 
             log("Processing groupfileset ", Project.MSG_VERBOSE);
             FileSet fs = (FileSet) groupfilesets.elementAt(i);
             FileScanner scanner = fs.getDirectoryScanner(getProject());
             String[] files = scanner.getIncludedFiles();
             File basedir = scanner.getBasedir();
             for (int j = 0; j < files.length; j++) {
 
                 log("Adding file " + files[j] + " to fileset",
                     Project.MSG_VERBOSE);
                 ZipFileSet zf = new ZipFileSet();
                 zf.setProject(getProject());
                 zf.setSrc(new File(basedir, files[j]));
                 add(zf);
                 filesetsFromGroupfilesets.addElement(zf);
             }
         }
 
         // collect filesets to pass them to getResourcesToAdd
         Vector vfss = new Vector();
         if (baseDir != null) {
             FileSet fs = (FileSet) getImplicitFileSet().clone();
             fs.setDir(baseDir);
             vfss.addElement(fs);
         }
         for (int i = 0; i < resources.size(); i++) {
             ResourceCollection rc = (ResourceCollection) resources.elementAt(i);
             vfss.addElement(rc);
         }
 
         ResourceCollection[] fss = new ResourceCollection[vfss.size()];
         vfss.copyInto(fss);
         boolean success = false;
         try {
             // can also handle empty archives
             ArchiveState state = getResourcesToAdd(fss, zipFile, false);
 
             // quick exit if the target is up to date
             if (!state.isOutOfDate()) {
                 return;
             }
 
             if (!zipFile.exists() && state.isWithoutAnyResources()) {
                 createEmptyZip(zipFile);
                 return;
             }
             Resource[][] addThem = state.getResourcesToAdd();
 
             if (doUpdate) {
                 renamedFile =
                     FILE_UTILS.createTempFile("zip", ".tmp",
                                               zipFile.getParentFile());
                 renamedFile.deleteOnExit();
 
                 try {
                     FILE_UTILS.rename(zipFile, renamedFile);
                 } catch (SecurityException e) {
                     throw new BuildException(
                         "Not allowed to rename old file ("
                         + zipFile.getAbsolutePath()
                         + ") to temporary file");
                 } catch (IOException e) {
                     throw new BuildException(
                         "Unable to rename old file ("
                         + zipFile.getAbsolutePath()
                         + ") to temporary file");
                 }
             }
 
             String action = doUpdate ? "Updating " : "Building ";
 
             log(action + archiveType + ": " + zipFile.getAbsolutePath());
 
             ZipOutputStream zOut = null;
             try {
                 if (!skipWriting) {
                     zOut = new ZipOutputStream(zipFile);
 
                     zOut.setEncoding(encoding);
                     zOut.setMethod(doCompress
                         ? ZipOutputStream.DEFLATED : ZipOutputStream.STORED);
                     zOut.setLevel(level);
                 }
                 initZipOutputStream(zOut);
 
                 // Add the explicit resource collections to the archive.
                 for (int i = 0; i < fss.length; i++) {
                     if (addThem[i].length != 0) {
                         addResources(fss[i], addThem[i], zOut);
                     }
                 }
 
                 if (doUpdate) {
                     addingNewFiles = false;
                     ZipFileSet oldFiles = new ZipFileSet();
                     oldFiles.setProject(getProject());
                     oldFiles.setSrc(renamedFile);
                     oldFiles.setDefaultexcludes(false);
 
                     for (int i = 0; i < addedFiles.size(); i++) {
                         PatternSet.NameEntry ne = oldFiles.createExclude();
                         ne.setName((String) addedFiles.elementAt(i));
                     }
                     DirectoryScanner ds =
                         oldFiles.getDirectoryScanner(getProject());
                     ((ZipScanner) ds).setEncoding(encoding);
 
                     String[] f = ds.getIncludedFiles();
                     Resource[] r = new Resource[f.length];
                     for (int i = 0; i < f.length; i++) {
                         r[i] = ds.getResource(f[i]);
                     }
 
                     if (!doFilesonly) {
                         String[] d = ds.getIncludedDirectories();
                         Resource[] dr = new Resource[d.length];
                         for (int i = 0; i < d.length; i++) {
                             dr[i] = ds.getResource(d[i]);
                         }
                         Resource[] tmp = r;
                         r = new Resource[tmp.length + dr.length];
                         System.arraycopy(dr, 0, r, 0, dr.length);
                         System.arraycopy(tmp, 0, r, dr.length, tmp.length);
                     }
                     addResources(oldFiles, r, zOut);
                 }
                 if (zOut != null) {
                     zOut.setComment(comment);
                 }
                 finalizeZipOutputStream(zOut);
 
                 // If we've been successful on an update, delete the
                 // temporary file
                 if (doUpdate) {
                     if (!renamedFile.delete()) {
                         log ("Warning: unable to delete temporary file "
                             + renamedFile.getName(), Project.MSG_WARN);
                     }
                 }
                 success = true;
             } finally {
                 // Close the output stream.
                 try {
                     if (zOut != null) {
                         zOut.close();
                     }
                 } catch (IOException ex) {
                     // If we're in this finally clause because of an
                     // exception, we don't really care if there's an
                     // exception when closing the stream. E.g. if it
                     // throws "ZIP file must have at least one entry",
                     // because an exception happened before we added
                     // any files, then we must swallow this
                     // exception. Otherwise, the error that's reported
                     // will be the close() error, which is not the
                     // real cause of the problem.
                     if (success) {
                         throw ex;
                     }
                 }
             }
         } catch (IOException ioe) {
             String msg = "Problem creating " + archiveType + ": "
                 + ioe.getMessage();
 
             // delete a bogus ZIP file (but only if it's not the original one)
             if ((!doUpdate || renamedFile != null) && !zipFile.delete()) {
                 msg += " (and the archive is probably corrupt but I could not "
                     + "delete it)";
             }
 
             if (doUpdate && renamedFile != null) {
                 try {
                     FILE_UTILS.rename(renamedFile, zipFile);
                 } catch (IOException e) {
                     msg += " (and I couldn't rename the temporary file "
                             + renamedFile.getName() + " back)";
                 }
             }
 
             throw new BuildException(msg, ioe, getLocation());
         } finally {
             cleanUp();
         }
     }
 
     /**
      * Indicates if the task is adding new files into the archive as opposed to
      * copying back unchanged files from the backup copy
      * @return true if adding new files
      */
     protected final boolean isAddingNewFiles() {
         return addingNewFiles;
     }
 
     /**
      * Add the given resources.
      *
      * @param fileset may give additional information like fullpath or
      * permissions.
      * @param resources the resources to add
      * @param zOut the stream to write to
      * @throws IOException on error
      *
      * @since Ant 1.5.2
      */
     protected final void addResources(FileSet fileset, Resource[] resources,
                                       ZipOutputStream zOut)
         throws IOException {
 
         String prefix = "";
         String fullpath = "";
         int dirMode = ArchiveFileSet.DEFAULT_DIR_MODE;
         int fileMode = ArchiveFileSet.DEFAULT_FILE_MODE;
 
         ArchiveFileSet zfs = null;
         if (fileset instanceof ArchiveFileSet) {
             zfs = (ArchiveFileSet) fileset;
             prefix = zfs.getPrefix(getProject());
             fullpath = zfs.getFullpath(getProject());
             dirMode = zfs.getDirMode(getProject());
             fileMode = zfs.getFileMode(getProject());
         }
 
         if (prefix.length() > 0 && fullpath.length() > 0) {
             throw new BuildException("Both prefix and fullpath attributes must"
                                      + " not be set on the same fileset.");
         }
 
         if (resources.length != 1 && fullpath.length() > 0) {
             throw new BuildException("fullpath attribute may only be specified"
                                      + " for filesets that specify a single"
                                      + " file.");
         }
 
         if (prefix.length() > 0) {
             if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                 prefix += "/";
             }
             addParentDirs(null, prefix, zOut, "", dirMode);
         }
 
         ZipFile zf = null;
         try {
             boolean dealingWithFiles = false;
             File base = null;
 
             if (zfs == null || zfs.getSrc(getProject()) == null) {
                 dealingWithFiles = true;
                 base = fileset.getDir(getProject());
             } else if (zfs instanceof ZipFileSet) {
                 zf = new ZipFile(zfs.getSrc(getProject()), encoding);
             }
 
             for (int i = 0; i < resources.length; i++) {
                 String name = null;
                 if (fullpath.length() > 0) {
                     name = fullpath;
                 } else {
                     name = resources[i].getName();
                 }
                 name = name.replace(File.separatorChar, '/');
 
                 if ("".equals(name)) {
                     continue;
                 }
                 if (resources[i].isDirectory() && !name.endsWith("/")) {
                     name = name + "/";
                 }
 
                 if (!doFilesonly && !dealingWithFiles
                     && resources[i].isDirectory()
                     && !zfs.hasDirModeBeenSet()) {
                     int nextToLastSlash = name.lastIndexOf("/",
                                                            name.length() - 2);
                     if (nextToLastSlash != -1) {
                         addParentDirs(base, name.substring(0,
                                                            nextToLastSlash + 1),
                                       zOut, prefix, dirMode);
                     }
                     if (zf != null) {
                         ZipEntry ze = zf.getEntry(resources[i].getName());
                         addParentDirs(base, name, zOut, prefix,
                                       ze.getUnixMode());
                     } else {
                         ArchiveResource tr = (ArchiveResource) resources[i];
                         addParentDirs(base, name, zOut, prefix,
                                       tr.getMode());
                     }
 
                 } else {
                     addParentDirs(base, name, zOut, prefix, dirMode);
                 }
 
                 if (!resources[i].isDirectory() && dealingWithFiles) {
                     File f = FILE_UTILS.resolveFile(base,
                                                    resources[i].getName());
                     zipFile(f, zOut, prefix + name, fileMode);
                 } else if (!resources[i].isDirectory()) {
                     if (zf != null) {
                     ZipEntry ze = zf.getEntry(resources[i].getName());
 
                     if (ze != null) {
                         boolean oldCompress = doCompress;
                         if (keepCompression) {
                             doCompress = (ze.getMethod() == ZipEntry.DEFLATED);
                         }
                         try {
                             zipFile(zf.getInputStream(ze), zOut, prefix + name,
                                     ze.getTime(), zfs.getSrc(getProject()),
                                     zfs.hasFileModeBeenSet() ? fileMode
                                     : ze.getUnixMode());
                         } finally {
                             doCompress = oldCompress;
                         }
                     }
                     } else {
                         ArchiveResource tr = (ArchiveResource) resources[i];
                         InputStream is = null;
                         try {
                             is = tr.getInputStream();
                             zipFile(is, zOut, prefix + name,
                                     resources[i].getLastModified(),
                                     zfs.getSrc(getProject()),
                                     zfs.hasFileModeBeenSet() ? fileMode
                                     : tr.getMode());
                         } finally {
                             FileUtils.close(is);
                         }
                     }
                 }
             }
         } finally {
             if (zf != null) {
                 zf.close();
             }
         }
     }
 
     /**
      * Add the given resources.
      *
      * @param rc may give additional information like fullpath or
      * permissions.
      * @param resources the resources to add
      * @param zOut the stream to write to
      * @throws IOException on error
      *
      * @since Ant 1.7
      */
     protected final void addResources(ResourceCollection rc,
                                       Resource[] resources,
                                       ZipOutputStream zOut)
         throws IOException {
         if (rc instanceof FileSet) {
             addResources((FileSet) rc, resources, zOut);
             return;
         }
         for (int i = 0; i < resources.length; i++) {
             String name = resources[i].getName().replace(File.separatorChar,
                                                          '/');
             if ("".equals(name)) {
                 continue;
             }
             if (resources[i].isDirectory() && doFilesonly) {
                 continue;
             }
             File base = null;
             if (resources[i] instanceof FileResource) {
                 base = ((FileResource) resources[i]).getBaseDir();
             }
             if (resources[i].isDirectory()) {
                 if (!name.endsWith("/")) {
                     name = name + "/";
                 }
             }
 
             addParentDirs(base, name, zOut, "",
                           ArchiveFileSet.DEFAULT_DIR_MODE);
 
             if (!resources[i].isDirectory()) {
                 if (resources[i] instanceof FileResource) {
                     File f = ((FileResource) resources[i]).getFile();
                     zipFile(f, zOut, name, ArchiveFileSet.DEFAULT_FILE_MODE);
                 } else {
                     InputStream is = null;
                     try {
                         is = resources[i].getInputStream();
                         zipFile(is, zOut, name,
                                 resources[i].getLastModified(),
                                 null, ArchiveFileSet.DEFAULT_FILE_MODE);
                     } finally {
                         FileUtils.close(is);
                     }
                 }
             }
         }
     }
 
     /**
      * method for subclasses to override
      * @param zOut the zip output stream
      * @throws IOException on output error
      * @throws BuildException on other errors
      */
     protected void initZipOutputStream(ZipOutputStream zOut)
         throws IOException, BuildException {
     }
 
     /**
      * method for subclasses to override
      * @param zOut the zip output stream
      * @throws IOException on output error
      * @throws BuildException on other errors
      */
     protected void finalizeZipOutputStream(ZipOutputStream zOut)
         throws IOException, BuildException {
     }
 
     /**
      * Create an empty zip file
      * @param zipFile the zip file
      * @return true for historic reasons
      * @throws BuildException on error
      */
     protected boolean createEmptyZip(File zipFile) throws BuildException {
         // In this case using java.util.zip will not work
         // because it does not permit a zero-entry archive.
         // Must create it manually.
         log("Note: creating empty " + archiveType + " archive " + zipFile,
             Project.MSG_INFO);
         OutputStream os = null;
         try {
             os = new FileOutputStream(zipFile);
             // Cf. PKZIP specification.
             byte[] empty = new byte[22];
             empty[0] = 80; // P
             empty[1] = 75; // K
             empty[2] = 5;
             empty[3] = 6;
             // remainder zeros
             os.write(empty);
         } catch (IOException ioe) {
             throw new BuildException("Could not create empty ZIP archive "
                                      + "(" + ioe.getMessage() + ")", ioe,
                                      getLocation());
         } finally {
             if (os != null) {
                 try {
                     os.close();
                 } catch (IOException e) {
                     //ignore
                 }
             }
         }
         return true;
     }
 
     /**
      * @since Ant 1.5.2
      */
     private synchronized ZipScanner getZipScanner() {
         if (zs == null) {
             zs = new ZipScanner();
             zs.setEncoding(encoding);
             zs.setSrc(zipFile);
         }
         return zs;
     }
 
     /**
      * Collect the resources that are newer than the corresponding
      * entries (or missing) in the original archive.
      *
      * <p>If we are going to recreate the archive instead of updating
      * it, all resources should be considered as new, if a single one
      * is.  Because of this, subclasses overriding this method must
      * call <code>super.getResourcesToAdd</code> and indicate with the
      * third arg if they already know that the archive is
      * out-of-date.</p>
      *
      * <p>This method first delegates to getNonFileSetResourceToAdd
      * and then invokes the FileSet-arg version.  All this to keep
      * backwards compatibility for subclasses that don't know how to
      * deal with non-FileSet ResourceCollections.</p>
      *
      * @param rcs The resource collections to grab resources from
      * @param zipFile intended archive file (may or may not exist)
      * @param needsUpdate whether we already know that the archive is
      * out-of-date.  Subclasses overriding this method are supposed to
      * set this value correctly in their call to
      * <code>super.getResourcesToAdd</code>.
      * @return an array of resources to add for each fileset passed in as well
      *         as a flag that indicates whether the archive is uptodate.
      *
      * @exception BuildException if it likes
      * @since Ant 1.7
      */
     protected ArchiveState getResourcesToAdd(ResourceCollection[] rcs,
                                              File zipFile,
                                              boolean needsUpdate)
         throws BuildException {
         ArrayList filesets = new ArrayList();
         ArrayList rest = new ArrayList();
         for (int i = 0; i < rcs.length; i++) {
             if (rcs[i] instanceof FileSet) {
                 filesets.add(rcs[i]);
             } else {
                 rest.add(rcs[i]);
             }
         }
         ResourceCollection[] rc = (ResourceCollection[])
             rest.toArray(new ResourceCollection[rest.size()]);
         ArchiveState as = getNonFileSetResourcesToAdd(rc, zipFile,
                                                       needsUpdate);
 
         FileSet[] fs = (FileSet[]) filesets.toArray(new FileSet[filesets
                                                                 .size()]);
         ArchiveState as2 = getResourcesToAdd(fs, zipFile, as.isOutOfDate());
         if (!as.isOutOfDate() && as2.isOutOfDate()) {
             /*
              * Bad luck.
              *
              * There are resources in the filesets that make the
              * archive out of date, but not in the non-fileset
              * resources. We need to rescan the non-FileSets to grab
              * all of them now.
              */
             as = getNonFileSetResourcesToAdd(rc, zipFile, true);
         }
         
         Resource[][] toAdd = new Resource[rcs.length][];
         int fsIndex = 0;
         int restIndex = 0;
         for (int i = 0; i < rcs.length; i++) {
             if (rcs[i] instanceof FileSet) {
                 toAdd[i] = as2.getResourcesToAdd()[fsIndex++];
             } else {
                 toAdd[i] = as.getResourcesToAdd()[restIndex++];
             }
         }
         return new ArchiveState(as2.isOutOfDate(), toAdd);
     }
 
     /**
      * Collect the resources that are newer than the corresponding
      * entries (or missing) in the original archive.
      *
      * <p>If we are going to recreate the archive instead of updating
      * it, all resources should be considered as new, if a single one
      * is.  Because of this, subclasses overriding this method must
      * call <code>super.getResourcesToAdd</code> and indicate with the
      * third arg if they already know that the archive is
      * out-of-date.</p>
      *
      * @param filesets The filesets to grab resources from
      * @param zipFile intended archive file (may or may not exist)
      * @param needsUpdate whether we already know that the archive is
      * out-of-date.  Subclasses overriding this method are supposed to
      * set this value correctly in their call to
      * <code>super.getResourcesToAdd</code>.
      * @return an array of resources to add for each fileset passed in as well
      *         as a flag that indicates whether the archive is uptodate.
      *
      * @exception BuildException if it likes
      */
     protected ArchiveState getResourcesToAdd(FileSet[] filesets,
                                              File zipFile,
                                              boolean needsUpdate)
         throws BuildException {
 
         Resource[][] initialResources = grabResources(filesets);
         if (isEmpty(initialResources)) {
             if (needsUpdate && doUpdate) {
                 /*
                  * This is a rather hairy case.
                  *
                  * One of our subclasses knows that we need to update the
                  * archive, but at the same time, there are no resources
                  * known to us that would need to be added.  Only the
                  * subclass seems to know what's going on.
                  *
                  * This happens if <jar> detects that the manifest has changed,
                  * for example.  The manifest is not part of any resources
                  * because of our support for inline <manifest>s.
                  *
                  * If we invoke createEmptyZip like Ant 1.5.2 did,
                  * we'll loose all stuff that has been in the original
                  * archive (bugzilla report 17780).
                  */
                 return new ArchiveState(true, initialResources);
             }
 
             if (emptyBehavior.equals("skip")) {
                 if (doUpdate) {
                     log(archiveType + " archive " + zipFile
                         + " not updated because no new files were included.",
                         Project.MSG_VERBOSE);
                 } else {
                     log("Warning: skipping " + archiveType + " archive "
                         + zipFile + " because no files were included.",
                         Project.MSG_WARN);
                 }
             } else if (emptyBehavior.equals("fail")) {
                 throw new BuildException("Cannot create " + archiveType
                                          + " archive " + zipFile
                                          + ": no files were included.",
                                          getLocation());
             } else {
                 // Create.
                 if (!zipFile.exists())  {
                     needsUpdate = true;
                 }
             }
             return new ArchiveState(needsUpdate, initialResources);
         }
 
         // initialResources is not empty
 
         if (!zipFile.exists()) {
             return new ArchiveState(true, initialResources);
         }
 
         if (needsUpdate && !doUpdate) {
             // we are recreating the archive, need all resources
             return new ArchiveState(true, initialResources);
         }
 
         Resource[][] newerResources = new Resource[filesets.length][];
 
         for (int i = 0; i < filesets.length; i++) {
             if (!(fileset instanceof ZipFileSet)
                 || ((ZipFileSet) fileset).getSrc(getProject()) == null) {
                 File base = filesets[i].getDir(getProject());
 
                 for (int j = 0; j < initialResources[i].length; j++) {
                     File resourceAsFile =
                         FILE_UTILS.resolveFile(base,
                                               initialResources[i][j].getName());
                     if (resourceAsFile.equals(zipFile)) {
                         throw new BuildException("A zip file cannot include "
                                                  + "itself", getLocation());
                     }
                 }
             }
         }
 
         for (int i = 0; i < filesets.length; i++) {
             if (initialResources[i].length == 0) {
                 newerResources[i] = new Resource[] {};
                 continue;
             }
 
             FileNameMapper myMapper = new IdentityMapper();
             if (filesets[i] instanceof ZipFileSet) {
                 ZipFileSet zfs = (ZipFileSet) filesets[i];
                 if (zfs.getFullpath(getProject()) != null
                     && !zfs.getFullpath(getProject()).equals("")) {
                     // in this case all files from origin map to
                     // the fullPath attribute of the zipfileset at
                     // destination
                     MergingMapper fm = new MergingMapper();
                     fm.setTo(zfs.getFullpath(getProject()));
                     myMapper = fm;
 
                 } else if (zfs.getPrefix(getProject()) != null
                            && !zfs.getPrefix(getProject()).equals("")) {
                     GlobPatternMapper gm = new GlobPatternMapper();
                     gm.setFrom("*");
                     String prefix = zfs.getPrefix(getProject());
                     if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                         prefix += "/";
                     }
                     gm.setTo(prefix + "*");
                     myMapper = gm;
                 }
             }
 
             Resource[] resources = initialResources[i];
             if (doFilesonly) {
                 resources = selectFileResources(resources);
             }
 
             newerResources[i] =
                 ResourceUtils.selectOutOfDateSources(this,
                                                      resources,
                                                      myMapper,
                                                      getZipScanner());
             needsUpdate = needsUpdate || (newerResources[i].length > 0);
 
             if (needsUpdate && !doUpdate) {
                 // we will return initialResources anyway, no reason
                 // to scan further.
                 break;
             }
         }
 
         if (needsUpdate && !doUpdate) {
             // we are recreating the archive, need all resources
             return new ArchiveState(true, initialResources);
         }
 
         return new ArchiveState(needsUpdate, newerResources);
     }
 
     /**
      * Collect the resources that are newer than the corresponding
      * entries (or missing) in the original archive.
      *
      * <p>If we are going to recreate the archive instead of updating
      * it, all resources should be considered as new, if a single one
      * is.  Because of this, subclasses overriding this method must
      * call <code>super.getResourcesToAdd</code> and indicate with the
      * third arg if they already know that the archive is
      * out-of-date.</p>
      *
      * @param filesets The filesets to grab resources from
      * @param zipFile intended archive file (may or may not exist)
      * @param needsUpdate whether we already know that the archive is
      * out-of-date.  Subclasses overriding this method are supposed to
      * set this value correctly in their call to
      * <code>super.getResourcesToAdd</code>.
      * @return an array of resources to add for each fileset passed in as well
      *         as a flag that indicates whether the archive is uptodate.
      *
      * @exception BuildException if it likes
      */
     protected ArchiveState getNonFileSetResourcesToAdd(ResourceCollection[] rcs,
                                                        File zipFile,
                                                        boolean needsUpdate)
         throws BuildException {
         /*
          * Backwards compatibility forces us to repeat the logic of
          * getResourcesToAdd(FileSet[], ...) here once again.
          */
 
         Resource[][] initialResources = grabNonFileSetResources(rcs);
         if (isEmpty(initialResources)) {
             // no emptyBehavior handling since the FileSet version
             // will take care of it.
             return new ArchiveState(needsUpdate, initialResources);
         }
 
         // initialResources is not empty
 
         if (!zipFile.exists()) {
             return new ArchiveState(true, initialResources);
         }
 
         if (needsUpdate && !doUpdate) {
             // we are recreating the archive, need all resources
             return new ArchiveState(true, initialResources);
         }
 
         Resource[][] newerResources = new Resource[rcs.length][];
 
         for (int i = 0; i < rcs.length; i++) {
             if (initialResources[i].length == 0) {
                 newerResources[i] = new Resource[] {};
                 continue;
             }
 
             for (int j = 0; j < initialResources[i].length; j++) {
                 if (initialResources[i][j] instanceof FileResource
                     && zipFile.equals(((FileResource)
                                        initialResources[i][j]).getFile())) {
                     throw new BuildException("A zip file cannot include "
                                              + "itself", getLocation());
                 }
             }
 
             Resource[] rs = initialResources[i];
             if (doFilesonly) {
                 rs = selectFileResources(rs);
             }
 
             newerResources[i] =
                 ResourceUtils.selectOutOfDateSources(this,
                                                      rs,
                                                      new IdentityMapper(),
                                                      getZipScanner());
             needsUpdate = needsUpdate || (newerResources[i].length > 0);
 
             if (needsUpdate && !doUpdate) {
                 // we will return initialResources anyway, no reason
                 // to scan further.
                 break;
             }
         }
 
         if (needsUpdate && !doUpdate) {
             // we are recreating the archive, need all resources
             return new ArchiveState(true, initialResources);
         }
 
         return new ArchiveState(needsUpdate, newerResources);
     }
 
     /**
      * Fetch all included and not excluded resources from the sets.
      *
      * <p>Included directories will precede included files.</p>
      * @param filesets an array of filesets
      * @return the resources included
      * @since Ant 1.5.2
      */
     protected Resource[][] grabResources(FileSet[] filesets) {
         Resource[][] result = new Resource[filesets.length][];
         for (int i = 0; i < filesets.length; i++) {
             boolean skipEmptyNames = true;
             if (filesets[i] instanceof ZipFileSet) {
                 ZipFileSet zfs = (ZipFileSet) filesets[i];
                 skipEmptyNames = zfs.getPrefix(getProject()).equals("")
                     && zfs.getFullpath(getProject()).equals("");
             }
             DirectoryScanner rs =
                 filesets[i].getDirectoryScanner(getProject());
             if (rs instanceof ZipScanner) {
                 ((ZipScanner) rs).setEncoding(encoding);
             }
             Vector resources = new Vector();
             String[] directories = rs.getIncludedDirectories();
             for (int j = 0; j < directories.length; j++) {
                 if (!"".equals(directories[j]) || !skipEmptyNames) {
                     resources.addElement(rs.getResource(directories[j]));
                 }
             }
             String[] files = rs.getIncludedFiles();
             for (int j = 0; j < files.length; j++) {
                 if (!"".equals(files[j]) || !skipEmptyNames) {
                     resources.addElement(rs.getResource(files[j]));
                 }
             }
 
             result[i] = new Resource[resources.size()];
             resources.copyInto(result[i]);
         }
         return result;
     }
 
     /**
      * Fetch all included and not excluded resources from the collections.
      *
      * <p>Included directories will precede included files.</p>
      * @param rcs an array of resource collections
      * @return the resources included
      * @since Ant 1.7
      */
     protected Resource[][] grabNonFileSetResources(ResourceCollection[] rcs) {
         Resource[][] result = new Resource[rcs.length][];
         for (int i = 0; i < rcs.length; i++) {
             Iterator iter = rcs[i].iterator();
             ArrayList rs = new ArrayList();
             int lastDir = 0;
             while (iter.hasNext()) {
                 Resource r = (Resource) iter.next();
                 if (r.isExists()) {
                     if (r.isDirectory()) {
                         rs.add(lastDir++, r);
                     } else {
                         rs.add(r);
                     }
                 }
             }
             result[i] = (Resource[]) rs.toArray(new Resource[rs.size()]);
         }
         return result;
     }
 
     /**
      * Add a directory to the zip stream.
      * @param dir  the directort to add to the archive
      * @param zOut the stream to write to
      * @param vPath the name this entry shall have in the archive
      * @param mode the Unix permissions to set.
      * @throws IOException on error
      * @since Ant 1.5.2
      */
     protected void zipDir(File dir, ZipOutputStream zOut, String vPath,
                           int mode)
         throws IOException {
         zipDir(dir, zOut, vPath, mode, null);
     }
 
     /**
      * Add a directory to the zip stream.
      * @param dir  the directort to add to the archive
      * @param zOut the stream to write to
      * @param vPath the name this entry shall have in the archive
      * @param mode the Unix permissions to set.
      * @param extra ZipExtraFields to add
      * @throws IOException on error
      * @since Ant 1.6.3
      */
     protected void zipDir(File dir, ZipOutputStream zOut, String vPath,
                           int mode, ZipExtraField[] extra)
         throws IOException {
         if (addedDirs.get(vPath) != null) {
             // don't add directories we've already added.
             // no warning if we try, it is harmless in and of itself
             return;
         }
 
         log("adding directory " + vPath, Project.MSG_VERBOSE);
         addedDirs.put(vPath, vPath);
 
         if (!skipWriting) {
             ZipEntry ze = new ZipEntry (vPath);
             if (dir != null && dir.exists()) {
                 // ZIPs store time with a granularity of 2 seconds, round up
                 ze.setTime(dir.lastModified() + (roundUp ? 1999 : 0));
             } else {
                 // ZIPs store time with a granularity of 2 seconds, round up
                 ze.setTime(System.currentTimeMillis() + (roundUp ? 1999 : 0));
             }
             ze.setSize (0);
             ze.setMethod (ZipEntry.STORED);
             // This is faintly ridiculous:
             ze.setCrc (EMPTY_CRC);
             ze.setUnixMode(mode);
 
             if (extra != null) {
                 ze.setExtraFields(extra);
             }
 
             zOut.putNextEntry(ze);
         }
     }
 
     /**
      * Adds a new entry to the archive, takes care of duplicates as well.
      *
      * @param in the stream to read data for the entry from.
      * @param zOut the stream to write to.
      * @param vPath the name this entry shall have in the archive.
      * @param lastModified last modification time for the entry.
      * @param fromArchive the original archive we are copying this
      * entry from, will be null if we are not copying from an archive.
      * @param mode the Unix permissions to set.
      *
      * @since Ant 1.5.2
      * @throws IOException on error
      */
     protected void zipFile(InputStream in, ZipOutputStream zOut, String vPath,
                            long lastModified, File fromArchive, int mode)
         throws IOException {
         if (entries.contains(vPath)) {
 
             if (duplicate.equals("preserve")) {
                 log(vPath + " already added, skipping", Project.MSG_INFO);
                 return;
             } else if (duplicate.equals("fail")) {
                 throw new BuildException("Duplicate file " + vPath
                                          + " was found and the duplicate "
                                          + "attribute is 'fail'.");
             } else {
                 // duplicate equal to add, so we continue
                 log("duplicate file " + vPath
                     + " found, adding.", Project.MSG_VERBOSE);
             }
         } else {
             log("adding entry " + vPath, Project.MSG_VERBOSE);
         }
 
         entries.put(vPath, vPath);
 
         if (!skipWriting) {
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
index 7a1a28f65..7e806b9dd 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
@@ -1,643 +1,645 @@
 /*
  * Copyright  2001-2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 //Java5 style
 //import static org.apache.tools.ant.util.StringUtils.LINE_SEP; 
 
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.Javac;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.taskdefs.condition.Os;
 
 /**
  * This is the default implementation for the CompilerAdapter interface.
  * Currently, this is a cut-and-paste of the original javac task.
  *
  * @since Ant 1.3
  */
 public abstract class DefaultCompilerAdapter implements CompilerAdapter {
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /* jdg - TODO - all these attributes are currently protected, but they
      * should probably be private in the near future.
      */
 
     protected Path src;
     protected File destDir;
     protected String encoding;
     protected boolean debug = false;
     protected boolean optimize = false;
     protected boolean deprecation = false;
     protected boolean depend = false;
     protected boolean verbose = false;
     protected String target;
     protected Path bootclasspath;
     protected Path extdirs;
     protected Path compileClasspath;
     protected Path compileSourcepath;
     protected Project project;
     protected Location location;
     protected boolean includeAntRuntime;
     protected boolean includeJavaRuntime;
     protected String memoryInitialSize;
     protected String memoryMaximumSize;
 
     protected File[] compileList;
     //protected static final String lSep = System.getProperty("line.separator");
     protected Javac attributes;
 
 
     /**
      * Set the Javac instance which contains the configured compilation
      * attributes.
      *
      * @param attributes a configured Javac task.
      */
     public void setJavac(Javac attributes) {
         this.attributes = attributes;
         src = attributes.getSrcdir();
         destDir = attributes.getDestdir();
         encoding = attributes.getEncoding();
         debug = attributes.getDebug();
         optimize = attributes.getOptimize();
         deprecation = attributes.getDeprecation();
         depend = attributes.getDepend();
         verbose = attributes.getVerbose();
         target = attributes.getTarget();
         bootclasspath = attributes.getBootclasspath();
         extdirs = attributes.getExtdirs();
         compileList = attributes.getFileList();
         compileClasspath = attributes.getClasspath();
         compileSourcepath = attributes.getSourcepath();
         project = attributes.getProject();
         location = attributes.getLocation();
         includeAntRuntime = attributes.getIncludeantruntime();
         includeJavaRuntime = attributes.getIncludejavaruntime();
         memoryInitialSize = attributes.getMemoryInitialSize();
         memoryMaximumSize = attributes.getMemoryMaximumSize();
     }
 
     /**
      * Get the Javac task instance associated with this compiler adapter
      *
      * @return the configured Javac task instance used by this adapter.
      */
     public Javac getJavac() {
         return attributes;
     }
 
     /**
      * Get the project this compiler adapter was created in.
      * @return the owner project
      * @since Ant 1.6
      */
     protected Project getProject() {
         return project;
     }
 
     /**
      * Builds the compilation classpath.
      * @return the compilation class path
      */
     protected Path getCompileClasspath() {
         Path classpath = new Path(project);
 
         // add dest dir to classpath so that previously compiled and
         // untouched classes are on classpath
 
         if (destDir != null) {
             classpath.setLocation(destDir);
         }
 
         // Combine the build classpath with the system classpath, in an
         // order determined by the value of build.sysclasspath
 
         Path cp = compileClasspath;
         if (cp == null) {
             cp = new Path(project);
         }
         if (includeAntRuntime) {
             classpath.addExisting(cp.concatSystemClasspath("last"));
         } else {
             classpath.addExisting(cp.concatSystemClasspath("ignore"));
         }
 
         if (includeJavaRuntime) {
             classpath.addJavaRuntime();
         }
 
         return classpath;
     }
 
     /**
      * Get the command line arguments for the switches.
      * @param cmd the command line
      * @return the command line
      */
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd) {
         return setupJavacCommandlineSwitches(cmd, false);
     }
 
     /**
      * Does the command line argument processing common to classic and
      * modern.  Doesn't add the files to compile.
      * @param cmd the command line
      * @param useDebugLevel if true set set the debug level with the -g switch
      * @return the command line
      */
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd,
                                                         boolean useDebugLevel) {
         Path classpath = getCompileClasspath();
         // For -sourcepath, use the "sourcepath" value if present.
         // Otherwise default to the "srcdir" value.
         Path sourcepath = null;
         if (compileSourcepath != null) {
             sourcepath = compileSourcepath;
         } else {
             sourcepath = src;
         }
 
         String memoryParameterPrefix = assumeJava11() ? "-J-" : "-J-X";
         if (memoryInitialSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryInitialSize setting.",
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "ms" + memoryInitialSize);
             }
         }
 
         if (memoryMaximumSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryMaximumSize setting.",
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "mx" + memoryMaximumSize);
             }
         }
 
         if (attributes.getNowarn()) {
             cmd.createArgument().setValue("-nowarn");
         }
 
         if (deprecation) {
             cmd.createArgument().setValue("-deprecation");
         }
 
         if (destDir != null) {
             cmd.createArgument().setValue("-d");
             cmd.createArgument().setFile(destDir);
         }
 
         cmd.createArgument().setValue("-classpath");
 
         // Just add "sourcepath" to classpath ( for JDK1.1 )
         // as well as "bootclasspath" and "extdirs"
         if (assumeJava11()) {
             Path cp = new Path(project);
 
             Path bp = getBootClassPath();
             if (bp.size() > 0) {
                 cp.append(bp);
             }
 
             if (extdirs != null) {
                 cp.addExtdirs(extdirs);
             }
             cp.append(classpath);
             cp.append(sourcepath);
             cmd.createArgument().setPath(cp);
         } else {
             cmd.createArgument().setPath(classpath);
             // If the buildfile specifies sourcepath="", then don't
             // output any sourcepath.
             if (sourcepath.size() > 0) {
                 cmd.createArgument().setValue("-sourcepath");
                 cmd.createArgument().setPath(sourcepath);
             }
             if (target != null) {
                 cmd.createArgument().setValue("-target");
                 cmd.createArgument().setValue(target);
             }
 
             Path bp = getBootClassPath();
             if (bp.size() > 0) {
                 cmd.createArgument().setValue("-bootclasspath");
                 cmd.createArgument().setPath(bp);
             }
 
             if (extdirs != null && extdirs.size() > 0) {
                 cmd.createArgument().setValue("-extdirs");
                 cmd.createArgument().setPath(extdirs);
             }
         }
 
         if (encoding != null) {
             cmd.createArgument().setValue("-encoding");
             cmd.createArgument().setValue(encoding);
         }
         if (debug) {
             if (useDebugLevel && !assumeJava11()) {
                 String debugLevel = attributes.getDebugLevel();
                 if (debugLevel != null) {
                     cmd.createArgument().setValue("-g:" + debugLevel);
                 } else {
                     cmd.createArgument().setValue("-g");
                 }
             } else {
                 cmd.createArgument().setValue("-g");
             }
         } else if (getNoDebugArgument() != null) {
             cmd.createArgument().setValue(getNoDebugArgument());
         }
         if (optimize) {
             cmd.createArgument().setValue("-O");
         }
 
         if (depend) {
             if (assumeJava11()) {
                 cmd.createArgument().setValue("-depend");
             } else if (assumeJava12()) {
                 cmd.createArgument().setValue("-Xdepend");
             } else {
                 attributes.log("depend attribute is not supported by the "
                                + "modern compiler", Project.MSG_WARN);
             }
         }
 
         if (verbose) {
             cmd.createArgument().setValue("-verbose");
         }
 
         addCurrentCompilerArgs(cmd);
 
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern.  Doesn't
      * add the files to compile.
      * @param cmd the command line
      * @return the command line
      */
     protected Commandline setupModernJavacCommandlineSwitches(Commandline cmd) {
         setupJavacCommandlineSwitches(cmd, true);
         if (attributes.getSource() != null && !assumeJava13()) {
             cmd.createArgument().setValue("-source");
             String source = attributes.getSource();
             if ((assumeJava14() || assumeJava15())
                 && (source.equals("1.1") || source.equals("1.2"))) {
                 // support for -source 1.1 and -source 1.2 has been
                 // added with JDK 1.4.2 - and isn't present in 1.5.0 either
                 cmd.createArgument().setValue("1.3");
             } else {
                 cmd.createArgument().setValue(source);
             }
         } else if ((assumeJava15() || assumeJava16())
                    && attributes.getTarget() != null) {
             String t = attributes.getTarget();
             if (t.equals("1.1") || t.equals("1.2") || t.equals("1.3")
                 || t.equals("1.4")) {
                 String s = t;
                 if (t.equals("1.1")) {
                     // 1.5.0 doesn't support -source 1.1
                     s = "1.2";
                 }
                 attributes.log("", Project.MSG_WARN);
                 attributes.log("          WARNING", Project.MSG_WARN);
                 attributes.log("", Project.MSG_WARN);
                 attributes.log("The -source switch defaults to 1.5 in JDK 1.5 and 1.6.",
                                Project.MSG_WARN);
                 attributes.log("If you specify -target " + t
                                + " you now must also specify -source " + s
                                + ".", Project.MSG_WARN);
                 attributes.log("Ant will implicitly add -source " + s
                                + " for you.  Please change your build file.",
                                Project.MSG_WARN);
                 cmd.createArgument().setValue("-source");
                 cmd.createArgument().setValue(s);
             }
         }
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern and adds
      * the files to compile as well.
      * @return the command line
      */
     protected Commandline setupModernJavacCommand() {
         Commandline cmd = new Commandline();
         setupModernJavacCommandlineSwitches(cmd);
 
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     /**
      * Set up the command line.
      * @return the command line
      */
     protected Commandline setupJavacCommand() {
         return setupJavacCommand(false);
     }
 
     /**
      * Does the command line argument processing for classic and adds
      * the files to compile as well.
      * @param debugLevelCheck if true set the debug level with the -g switch
      * @return the command line
      */
     protected Commandline setupJavacCommand(boolean debugLevelCheck) {
         Commandline cmd = new Commandline();
         setupJavacCommandlineSwitches(cmd, debugLevelCheck);
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param cmd the command line
      */
     protected void logAndAddFilesToCompile(Commandline cmd) {
         attributes.log("Compilation " + cmd.describeArguments(),
                        Project.MSG_VERBOSE);
 
         StringBuffer niceSourceList = new StringBuffer("File");
         if (compileList.length != 1) {
             niceSourceList.append("s");
         }
         niceSourceList.append(" to be compiled:");
 
         niceSourceList.append(StringUtils.LINE_SEP);
 
         for (int i = 0; i < compileList.length; i++) {
             String arg = compileList[i].getAbsolutePath();
             cmd.createArgument().setValue(arg);
-            niceSourceList.append("    " + arg + StringUtils.LINE_SEP);
+            niceSourceList.append("    ");
+            niceSourceList.append(arg);
+            niceSourceList.append(StringUtils.LINE_SEP);
         }
 
         attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * Do the compile with the specified arguments.
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      * @return the exit code of the compilation
      */
     protected int executeExternalCompile(String[] args, int firstFileName) {
         return executeExternalCompile(args, firstFileName, true);
     }
 
     /**
      * Do the compile with the specified arguments.
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      * @param quoteFiles - if set to true, filenames containing
      * spaces will be quoted when they appear in the external file.
      * This is necessary when running JDK 1.4's javac and probably
      * others.
      * @return the exit code of the compilation
      *
      * @since Ant 1.6
      */
     protected int executeExternalCompile(String[] args, int firstFileName,
                                          boolean quoteFiles) {
         String[] commandArray = null;
         File tmpFile = null;
 
         try {
             /*
              * Many system have been reported to get into trouble with
              * long command lines - no, not only Windows ;-).
              *
              * POSIX seems to define a lower limit of 4k, so use a temporary
              * file if the total length of the command line exceeds this limit.
              */
             if (Commandline.toString(args).length() > 4096
                 && firstFileName >= 0) {
                 PrintWriter out = null;
                 try {
                     tmpFile = FILE_UTILS.createTempFile(
                         "files", "", getJavac().getTempdir());
                     tmpFile.deleteOnExit();
                     out = new PrintWriter(new FileWriter(tmpFile));
                     for (int i = firstFileName; i < args.length; i++) {
                         if (quoteFiles && args[i].indexOf(" ") > -1) {
                             args[i] = args[i].replace(File.separatorChar, '/');
                             out.println("\"" + args[i] + "\"");
                         } else {
                             out.println(args[i]);
                         }
                     }
                     out.flush();
                     commandArray = new String[firstFileName + 1];
                     System.arraycopy(args, 0, commandArray, 0, firstFileName);
                     commandArray[firstFileName] = "@" + tmpFile;
                 } catch (IOException e) {
                     throw new BuildException("Error creating temporary file",
                                              e, location);
                 } finally {
                     FileUtils.close(out);
                 }
             } else {
                 commandArray = args;
             }
 
             try {
                 Execute exe = new Execute(
                                   new LogStreamHandler(attributes,
                                                        Project.MSG_INFO,
                                                        Project.MSG_WARN));
                 if (Os.isFamily("openvms")) {
                     //Use the VM launcher instead of shell launcher on VMS
                     //for java
                     exe.setVMLauncher(true);
                 }
                 exe.setAntRun(project);
                 exe.setWorkingDirectory(project.getBaseDir());
                 exe.setCommandline(commandArray);
                 exe.execute();
                 return exe.getExitValue();
             } catch (IOException e) {
                 throw new BuildException("Error running " + args[0]
                         + " compiler", e, location);
             }
         } finally {
             if (tmpFile != null) {
                 tmpFile.delete();
             }
         }
     }
 
     /**
      * Add extdirs to classpath
      * @param classpath the classpath to use
      * @deprecated since 1.5.x. 
      *             Use org.apache.tools.ant.types.Path#addExtdirs instead.
      */
     protected void addExtdirsToClasspath(Path classpath) {
         classpath.addExtdirs(extdirs);
     }
 
     /**
      * Adds the command line arguments specific to the current implementation.
      * @param cmd the command line to use
      */
     protected void addCurrentCompilerArgs(Commandline cmd) {
         cmd.addArguments(getJavac().getCurrentCompilerArgs());
     }
 
     /**
      * Shall we assume JDK 1.1 command line switches?
      * @return true if jdk 1.1
      * @since Ant 1.5
      */
     protected boolean assumeJava11() {
         return "javac1.1".equals(attributes.getCompilerVersion());
     }
 
     /**
      * Shall we assume JDK 1.2 command line switches?
      * @return true if jdk 1.2
      * @since Ant 1.5
      */
     protected boolean assumeJava12() {
         return "javac1.2".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2));
     }
 
     /**
      * Shall we assume JDK 1.3 command line switches?
      * @return true if jdk 1.3
      * @since Ant 1.5
      */
     protected boolean assumeJava13() {
         return "javac1.3".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3));
     }
 
     /**
      * Shall we assume JDK 1.4 command line switches?
      * @return true if jdk 1.4
      * @since Ant 1.6.3
      */
     protected boolean assumeJava14() {
         return "javac1.4".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4));
     }
 
     /**
      * Shall we assume JDK 1.5 command line switches?
      * @return true if JDK 1.5
      * @since Ant 1.6.3
      */
     protected boolean assumeJava15() {
         return "javac1.5".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5));
     }
 
     /**
      * Shall we assume JDK 1.6 command line switches?
      * @return true if JDK 1.6
      * @since Ant 1.7
      */
     protected boolean assumeJava16() {
         return "javac1.6".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6));
     }
 
     /**
      * Combines a user specified bootclasspath with the system
      * bootclasspath taking build.sysclasspath into account.
      *
      * @return a non-null Path instance that combines the user
      * specified and the system bootclasspath.
      */
     protected Path getBootClassPath() {
         Path bp = new Path(project);
         if (bootclasspath != null) {
             bp.append(bootclasspath);
         }
         return bp.concatSystemBootClasspath("ignore");
     }
 
     /**
      * The argument the compiler wants to see if the debug attribute
      * has been set to false.
      *
      * <p>A return value of <code>null</code> means no argument at all.</p>
      *
      * @return "-g:none" unless we expect to invoke a JDK 1.1 compiler.
      *
      * @since Ant 1.6.3
      */
     protected String getNoDebugArgument() {
         return assumeJava11() ? null : "-g:none";
     }
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java b/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
index 279805063..5ad62fa5c 100755
--- a/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
@@ -1,458 +1,460 @@
 /*
- * Copyright  2000-2005 The Apache Software Foundation
+ * Copyright  2000-2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.StringTokenizer;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter;
 import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapterFactory;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
 
 /**
  * Generates JNI header files using javah.
  *
  * This task can take the following arguments:
  * <ul>
  * <li>classname - the fully-qualified name of a class</li>
  * <li>outputFile - Concatenates the resulting header or source files for all
  *     the classes listed into this file</li>
  * <li>destdir - Sets the directory where javah saves the header files or the
  *     stub files</li>
  * <li>classpath</li>
  * <li>bootclasspath</li>
  * <li>force - Specifies that output files should always be written
        (JDK1.2 only)</li>
  * <li>old - Specifies that old JDK1.0-style header files should be generated
  *     (otherwise output file contain JNI-style native method
  *      function prototypes) (JDK1.2 only)</li>
  * <li>stubs - generate C declarations from the Java object file (used with old)</li>
  * <li>verbose - causes javah to print a message to stdout concerning the status
  *     of the generated files</li>
  * <li>extdirs - Override location of installed extensions</li>
  * </ul>
  * Of these arguments, either <b>outputFile</b> or <b>destdir</b> is required,
  * but not both. More than one classname may be specified, using a comma-separated
  * list or by using <code>&lt;class name="xxx"&gt;</code> elements within the task.
  * <p>
  * When this task executes, it will generate C header and source files that
  * are needed to implement native methods.
  *
  */
 
 public class Javah extends Task {
 
     private Vector classes = new Vector(2);
     private String cls;
     private File destDir;
     private Path classpath = null;
     private File outputFile = null;
     private boolean verbose = false;
     private boolean force   = false;
     private boolean old     = false;
     private boolean stubs   = false;
     private Path bootclasspath;
     //private Path extdirs;
     private static String lSep = System.getProperty("line.separator");
     private FacadeTaskHelper facade = null;
 
     public Javah() {
         facade = new FacadeTaskHelper(JavahAdapterFactory.getDefault());
     }
 
     /**
      * the fully-qualified name of the class (or classes, separated by commas).
      * @param cls the classname (or classnames).
      */
     public void setClass(String cls) {
         this.cls = cls;
     }
 
     /**
      * Adds class to process.
      * @return a <code>ClassArgument</code> to be configured.
      */
     public ClassArgument createClass() {
         ClassArgument ga = new ClassArgument();
         classes.addElement(ga);
         return ga;
     }
 
     /**
      * A class corresponding the the nested "class" element.
      * It contains a "name" attribute.
      */
     public class ClassArgument {
         private String name;
 
         /** Constructor for ClassArgument. */
         public ClassArgument() {
         }
 
         /**
          * Set the name attribute.
          * @param name the name attribute.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * Get the name attribute.
          * @return the name attribute.
          */
         public String getName() {
             return name;
         }
     }
 
     /**
      * Names of the classes to process.
      *
      * @since Ant 1.6.3
      */
     public String[] getClasses() {
         ArrayList al = new ArrayList();
         if (cls != null) {
             StringTokenizer tok = new StringTokenizer(cls, ",", false);
             while (tok.hasMoreTokens()) {
                 al.add(tok.nextToken().trim());
             }
         }
 
         Enumeration e = classes.elements();
         while (e.hasMoreElements()) {
             ClassArgument arg = (ClassArgument) e.nextElement();
             al.add(arg.getName());
         }
         return (String[]) al.toArray(new String[al.size()]);
     }
 
     /**
      * Set the destination directory into which the Java source
      * files should be compiled.
      * @param destDir the destination directory.
      */
     public void setDestdir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * The destination directory, if any.
      *
      * @since Ant 1.6.3
      */
     public File getDestdir() {
         return destDir;
     }
 
     /**
      * the classpath to use.
      * @param src the classpath.
      */
     public void setClasspath(Path src) {
         if (classpath == null) {
             classpath = src;
         } else {
             classpath.append(src);
         }
     }
 
     /**
      * Path to use for classpath.
      * @return a path to be configured.
      */
     public Path createClasspath() {
         if (classpath == null) {
             classpath = new Path(getProject());
         }
         return classpath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath.
      * @todo this needs to be documented in the HTML docs.
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * The classpath to use.
      *
      * @since Ant 1.6.3
      */
     public Path getClasspath() {
         return classpath;
     }
 
     /**
      * location of bootstrap class files.
      * @param src the bootstrap classpath.
      */
     public void setBootclasspath(Path src) {
         if (bootclasspath == null) {
             bootclasspath = src;
         } else {
             bootclasspath.append(src);
         }
     }
 
     /**
      * Adds path to bootstrap class files.
      * @return a path to be configured.
      */
     public Path createBootclasspath() {
         if (bootclasspath == null) {
             bootclasspath = new Path(getProject());
         }
         return bootclasspath.createPath();
     }
 
     /**
      * To the bootstrap path, this adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath
      * @todo this needs to be documented in the HTML.
      */
     public void setBootClasspathRef(Reference r) {
         createBootclasspath().setRefid(r);
     }
 
     /**
      * The bootclasspath to use.
      *
      * @since Ant 1.6.3
      */
     public Path getBootclasspath() {
         return bootclasspath;
     }
 
     /**
      * Concatenates the resulting header or source files for all
      * the classes listed into this file.
      * @param outputFile the output file.
      */
     public void setOutputFile(File outputFile) {
         this.outputFile = outputFile;
     }
 
     /**
      * The destination file, if any.
      *
      * @since Ant 1.6.3
      */
     public File getOutputfile() {
         return outputFile;
     }
 
     /**
      * If true, output files should always be written (JDK1.2 only).
      * @param force the value to use.
      */
     public void setForce(boolean force) {
         this.force = force;
     }
 
     /**
      * Whether output files should always be written.
      *
      * @since Ant 1.6.3
      */
     public boolean getForce() {
         return force;
     }
 
     /**
      * If true, specifies that old JDK1.0-style header files should be
      * generated.
      * (otherwise output file contain JNI-style native method function
      *  prototypes) (JDK1.2 only).
      * @param old if true use old 1.0 style header files.
      */
     public void setOld(boolean old) {
         this.old = old;
     }
 
     /**
      * Whether old JDK1.0-style header files should be generated.
      *
      * @since Ant 1.6.3
      */
     public boolean getOld() {
         return old;
     }
 
     /**
      * If true, generate C declarations from the Java object file (used with old).
      * @param stubs if true, generated C declarations.
      */
     public void setStubs(boolean stubs) {
         this.stubs = stubs;
     }
 
     /**
      * Whether C declarations from the Java object file should be generated.
      *
      * @since Ant 1.6.3
      */
     public boolean getStubs() {
         return stubs;
     }
 
     /**
      * If true, causes Javah to print a message concerning
      * the status of the generated files.
      * @param verbose if true, do verbose printing.
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
     /**
      * Whether verbose output should get generated.
      *
      * @since Ant 1.6.3
      */
     public boolean getVerbose() {
         return verbose;
     }
 
     /**
      * Choose the implementation for this particular task.
      * @param impl the name of the implemenation.
      * @since Ant 1.6.3
      */
     public void setImplementation(String impl) {
         if ("default".equals(impl)) {
             facade.setImplementation(JavahAdapterFactory.getDefault());
         } else {
             facade.setImplementation(impl);
         }        
     }
 
     /**
      * Adds an implementation specific command-line argument.
      * @return a ImplementationSpecificArgument to be configured.
      *
      * @since Ant 1.6.3
      */
     public ImplementationSpecificArgument createArg() {
         ImplementationSpecificArgument arg =
             new ImplementationSpecificArgument();
         facade.addImplementationArgument(arg);
         return arg;
     }
 
     /**
      * Returns the (implementation specific) settings given as nested
      * arg elements.
      *
      * @since Ant 1.6.3
      */
     public String[] getCurrentArgs() {
         return facade.getArgs();
     }
 
     /**
      * Execute the task
      *
      * @throws BuildException is there is a problem in the task execution.
      */
     public void execute() throws BuildException {
         // first off, make sure that we've got a srcdir
 
         if ((cls == null) && (classes.size() == 0)) {
             throw new BuildException("class attribute must be set!",
                 getLocation());
         }
 
         if ((cls != null) && (classes.size() > 0)) {
             throw new BuildException("set class attribute or class element, "
                 + "not both.", getLocation());
         }
 
         if (destDir != null) {
             if (!destDir.isDirectory()) {
                 throw new BuildException("destination directory \"" + destDir
                     + "\" does not exist or is not a directory", getLocation());
             }
             if (outputFile != null) {
                 throw new BuildException("destdir and outputFile are mutually "
                     + "exclusive", getLocation());
             }
         }
 
         if (classpath == null) {
             classpath = (new Path(getProject())).concatSystemClasspath("last");
         } else {
             classpath = classpath.concatSystemClasspath("ignore");
         }
 
         JavahAdapter ad = 
             JavahAdapterFactory.getAdapter(facade.getImplementation(),
                                            this);
         if (!ad.compile(this)) {
             throw new BuildException("compilation failed");
         }
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      */
     public void logAndAddFiles(Commandline cmd) {
         logAndAddFilesToCompile(cmd);
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param cmd the command line to add parameters to.
      */
     protected void logAndAddFilesToCompile(Commandline cmd) {
         log("Compilation " + cmd.describeArguments(),
             Project.MSG_VERBOSE);
 
         StringBuffer niceClassList = new StringBuffer();
         String[] c = getClasses();
         for (int i = 0; i < c.length; i++) {
             cmd.createArgument().setValue(c[i]);
-            niceClassList.append("    " + c[i] + lSep);
+            niceClassList.append("    ");
+            niceClassList.append(c[i]);
+            niceClassList.append(lSep);
         }
 
         StringBuffer prefix = new StringBuffer("Class");
         if (c.length > 1) {
             prefix.append("es");
         }
         prefix.append(" to be compiled:");
         prefix.append(lSep);
 
         log(prefix.toString() + niceClassList.toString(), Project.MSG_VERBOSE);
     }
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
index cf816da93..60bf170e7 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
@@ -1,478 +1,483 @@
 /*
  * Copyright  2001-2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.Vector;
 import java.util.Enumeration;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.parsers.SAXParserFactory;
 import javax.xml.transform.ErrorListener;
 import javax.xml.transform.Source;
 import javax.xml.transform.SourceLocator;
 import javax.xml.transform.Templates;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.URIResolver;
 import javax.xml.transform.sax.SAXSource;
 import javax.xml.transform.stream.StreamResult;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.transform.TransformerConfigurationException;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.taskdefs.XSLTLiaison2;
 import org.apache.tools.ant.taskdefs.XSLTProcess;
 import org.apache.tools.ant.taskdefs.XSLTLogger;
 import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
 import org.apache.tools.ant.types.XMLCatalog;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JAXPUtils;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.XMLReader;
 
 /**
  * Concrete liaison for XSLT processor implementing TraX. (ie JAXP 1.1)
  *
  * @since Ant 1.3
  */
 public class TraXLiaison implements XSLTLiaison2, ErrorListener, XSLTLoggerAware {
 
     /**
      * the name of the factory implementation class to use
      * or null for default JAXP lookup.
      */
     private String factoryName = null;
 
     /** The trax TransformerFactory */
     private TransformerFactory tfactory = null;
 
     /** stylesheet to use for transformation */
     private File stylesheet;
 
     private XSLTLogger logger;
 
     /** possible resolver for publicIds */
     private EntityResolver entityResolver;
 
     /** transformer to use for processing files */
     private Transformer transformer;
 
     /** The In memory version of the stylesheet */
     private Templates templates;
 
     /**
      * The modification time of the stylesheet from which the templates
      * are read
      */
     private long templatesModTime;
 
     /** possible resolver for URIs */
     private URIResolver uriResolver;
 
     /** transformer output properties */
     private Vector outputProperties = new Vector();
 
     /** stylesheet parameters */
     private Vector params = new Vector();
 
     /** factory attributes */
     private Vector attributes = new Vector();
 
     /**
      * Constructor for TraXLiaison.
      * @throws Exception never
      */
     public TraXLiaison() throws Exception {
     }
 
     /**
      * Set the stylesheet file.
      * @param stylesheet a <code>File</code> value
      * @throws Exception on error
      */
     public void setStylesheet(File stylesheet) throws Exception {
         if (this.stylesheet != null) {
             // resetting the stylesheet - reset transformer
             transformer = null;
 
             // do we need to reset templates as well
             if (!this.stylesheet.equals(stylesheet)
                 || (stylesheet.lastModified() != templatesModTime)) {
                 templates = null;
             }
         }
         this.stylesheet = stylesheet;
     }
 
     /**
      * Transform an input file.
      * @param infile the file to transform
      * @param outfile the result file
      * @throws Exception on error
      */
     public void transform(File infile, File outfile) throws Exception {
         if (transformer == null) {
             createTransformer();
         }
 
         InputStream fis = null;
         OutputStream fos = null;
         try {
             fis = new BufferedInputStream(new FileInputStream(infile));
             fos = new BufferedOutputStream(new FileOutputStream(outfile));
             StreamResult res = new StreamResult(fos);
             // not sure what could be the need of this...
             res.setSystemId(JAXPUtils.getSystemId(outfile));
             Source src = getSource(fis, infile);
             transformer.transform(src, res);
         } finally {
             // make sure to close all handles, otherwise the garbage
             // collector will close them...whenever possible and
             // Windows may complain about not being able to delete files.
             try {
                 if (fis != null) {
                     fis.close();
                 }
             } catch (IOException ignored) {
                 // ignore
             }
             try {
                 if (fos != null) {
                     fos.close();
                 }
             } catch (IOException ignored) {
                 // ignore
             }
         }
     }
 
     /**
      * Get the source instance from the stream and id of the file.
      * @param is the stream containing the stylesheet data.
      * @param infile the file that will be used for the systemid.
      * @return the configured source instance matching the stylesheet.
      * @throws ParserConfigurationException if a parser cannot be created which
      * satisfies the requested configuration.
      * @throws SAXException in case of problem detected by the SAX parser.
      */
     private Source getSource(InputStream is, File infile)
         throws ParserConfigurationException, SAXException {
         // todo: is this comment still relevant ??
         // FIXME: need to use a SAXSource as the source for the transform
         // so we can plug in our own entity resolver
         Source src = null;
         if (entityResolver != null) {
             if (getFactory().getFeature(SAXSource.FEATURE)) {
                 SAXParserFactory spFactory = SAXParserFactory.newInstance();
                 spFactory.setNamespaceAware(true);
                 XMLReader reader = spFactory.newSAXParser().getXMLReader();
                 reader.setEntityResolver(entityResolver);
                 src = new SAXSource(reader, new InputSource(is));
             } else {
                 throw new IllegalStateException("xcatalog specified, but "
                     + "parser doesn't support SAX");
             }
         } else {
             // WARN: Don't use the StreamSource(File) ctor. It won't work with
             // xalan prior to 2.2 because of systemid bugs.
             src = new StreamSource(is);
         }
         src.setSystemId(JAXPUtils.getSystemId(infile));
         return src;
     }
 
     /**
      * Read in templates from the stylesheet
      */
     private void readTemplates()
         throws IOException, TransformerConfigurationException,
                ParserConfigurationException, SAXException {
 
         // Use a stream so that you can close it yourself quickly
         // and avoid keeping the handle until the object is garbaged.
         // (always keep control), otherwise you won't be able to delete
         // the file quickly on windows.
         InputStream xslStream = null;
         try {
             xslStream
                 = new BufferedInputStream(new FileInputStream(stylesheet));
             templatesModTime = stylesheet.lastModified();
             Source src = getSource(xslStream, stylesheet);
             templates = getFactory().newTemplates(src);
         } finally {
             if (xslStream != null) {
                 xslStream.close();
             }
         }
     }
 
     /**
      * Create a new transformer based on the liaison settings
      * @throws Exception thrown if there is an error during creation.
      * @see #setStylesheet(java.io.File)
      * @see #addParam(java.lang.String, java.lang.String)
      * @see #setOutputProperty(java.lang.String, java.lang.String)
      */
     private void createTransformer() throws Exception {
         if (templates == null) {
             readTemplates();
         }
 
         transformer = templates.newTransformer();
 
         // configure the transformer...
         transformer.setErrorListener(this);
         if (uriResolver != null) {
             transformer.setURIResolver(uriResolver);
         }
         for (int i = 0; i < params.size(); i++) {
             final String[] pair = (String[]) params.elementAt(i);
             transformer.setParameter(pair[0], pair[1]);
         }
         for (int i = 0; i < outputProperties.size(); i++) {
             final String[] pair = (String[]) outputProperties.elementAt(i);
             transformer.setOutputProperty(pair[0], pair[1]);
         }
     }
 
     /**
      * return the Transformer factory associated to this liaison.
      * @return the Transformer factory associated to this liaison.
      * @throws BuildException thrown if there is a problem creating
      * the factory.
      * @see #setFactory(String)
      * @since Ant 1.5.2
      */
     private TransformerFactory getFactory() throws BuildException {
         if (tfactory != null) {
             return tfactory;
         }
         // not initialized yet, so create the factory
         if (factoryName == null) {
             tfactory = TransformerFactory.newInstance();
         } else {
             try {
                 Class clazz = Class.forName(factoryName);
                 tfactory = (TransformerFactory) clazz.newInstance();
             } catch (Exception e) {
                 throw new BuildException(e);
             }
         }
         tfactory.setErrorListener(this);
 
         // specific attributes for the transformer
         for (int i = 0; i < attributes.size(); i++) {
             final Object[] pair = (Object[]) attributes.elementAt(i);
             tfactory.setAttribute((String) pair[0], pair[1]);
         }
 
         if (uriResolver != null) {
             tfactory.setURIResolver(uriResolver);
         }
         return tfactory;
     }
 
 
     /**
      * Set the factory name to use instead of JAXP default lookup.
      * @param name the fully qualified class name of the factory to use
      * or null for the default JAXP look up mechanism.
      * @since Ant 1.6
      */
     public void setFactory(String name) {
         factoryName = name;
     }
 
     /**
      * Set a custom attribute for the JAXP factory implementation.
      * @param name the attribute name.
      * @param value the value of the attribute, usually a boolean
      * string or object.
      * @since Ant 1.6
      */
     public void setAttribute(String name, Object value) {
         final Object[] pair = new Object[]{name, value};
         attributes.addElement(pair);
     }
 
     /**
      * Set the output property for the current transformer.
      * Note that the stylesheet must be set prior to calling
      * this method.
      * @param name the output property name.
      * @param value the output property value.
      * @since Ant 1.5
      * @since Ant 1.5
      */
     public void setOutputProperty(String name, String value) {
         final String[] pair = new String[]{name, value};
         outputProperties.addElement(pair);
     }
 
     /**
      * Set the class to resolve entities during the transformation.
      * @param aResolver the resolver class.
      */
     public void setEntityResolver(EntityResolver aResolver) {
         entityResolver = aResolver;
     }
 
     /**
      * Set the class to resolve URIs during the transformation
      * @param aResolver a <code>EntityResolver</code> value
      */
     public void setURIResolver(URIResolver aResolver) {
         uriResolver = aResolver;
     }
 
     /**
      * Add a parameter.
      * @param name the name of the parameter
      * @param value the value of the parameter
      */
     public void addParam(String name, String value) {
         final String[] pair = new String[]{name, value};
         params.addElement(pair);
     }
 
     /**
      * Set a logger.
      * @param l a logger.
      */
     public void setLogger(XSLTLogger l) {
         logger = l;
     }
 
     /**
      * Log an error.
      * @param e the exception to log.
      */
     public void error(TransformerException e) {
         logError(e, "Error");
     }
 
     /**
      * Log a fatal error.
      * @param e the exception to log.
      */
     public void fatalError(TransformerException e) {
         logError(e, "Fatal Error");
         throw new BuildException("Fatal error during transformation", e);
     }
 
     /**
      * Log a warning.
      * @param e the exception to log.
      */
     public void warning(TransformerException e) {
         logError(e, "Warning");
     }
 
     private void logError(TransformerException e, String type) {
         if (logger == null) {
             return;
         }
 
         StringBuffer msg = new StringBuffer();
         SourceLocator locator = e.getLocator();
         if (locator != null) {
             String systemid = locator.getSystemId();
             if (systemid != null) {
                 String url = systemid;
                 if (url.startsWith("file:")) {
                     url = FileUtils.getFileUtils().fromURI(url);
                 }
                 msg.append(url);
             } else {
                 msg.append("Unknown file");
             }
             int line = locator.getLineNumber();
             if (line != -1) {
-                msg.append(":" + line);
+                msg.append(":");
+                msg.append(line);
                 int column = locator.getColumnNumber();
                 if (column != -1) {
-                    msg.append(":" + column);
+                    msg.append(":");
+                    msg.append(column);
                 }
             }
         }
-        msg.append(": " + type + "! ");
+        msg.append(": ");
+        msg.append(type);
+        msg.append("! ");
         msg.append(e.getMessage());
         if (e.getCause() != null) {
-            msg.append(" Cause: " + e.getCause());
+            msg.append(" Cause: ");
+            msg.append(e.getCause());
         }
 
         logger.log(msg.toString());
     }
 
     // kept for backwards compatibility
     /**
      * @param file the filename to use for the systemid
      * @return the systemid
      * @deprecated since 1.5.x. 
      *             Use org.apache.tools.ant.util.JAXPUtils#getSystemId instead.
      */
     protected String getSystemId(File file) {
         return JAXPUtils.getSystemId(file);
     }
 
 
     /**
      * Specific configuration for the TRaX liaison.
      * @param xsltTask the XSLTProcess task instance from which this liasion
      *        is to be configured.
      */
     public void configure(XSLTProcess xsltTask) {
         XSLTProcess.Factory factory = xsltTask.getFactory();
         if (factory != null) {
             setFactory(factory.getName());
 
             // configure factory attributes
             for (Enumeration attrs = factory.getAttributes();
                     attrs.hasMoreElements();) {
                 XSLTProcess.Factory.Attribute attr =
                         (XSLTProcess.Factory.Attribute) attrs.nextElement();
                 setAttribute(attr.getName(), attr.getValue());
             }
         }
 
         XMLCatalog xmlCatalog = xsltTask.getXMLCatalog();
         // use XMLCatalog as the entity resolver and URI resolver
         if (xmlCatalog != null) {
             setEntityResolver(xmlCatalog);
             setURIResolver(xmlCatalog);
         }
 
 
         // configure output properties
         for (Enumeration props = xsltTask.getOutputProperties();
                 props.hasMoreElements();) {
             XSLTProcess.OutputProperty prop
                 = (XSLTProcess.OutputProperty) props.nextElement();
             setOutputProperty(prop.getName(), prop.getValue());
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/dotnet/DotnetCompile.java b/src/main/org/apache/tools/ant/taskdefs/optional/dotnet/DotnetCompile.java
index c63520326..367ea06e0 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/dotnet/DotnetCompile.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/dotnet/DotnetCompile.java
@@ -1,978 +1,977 @@
 /*
  * Copyright  2001-2005 The Apache Software Foundation
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
 
 /*
  *  build notes
  *  -The reference CD to listen to while editing this file is
  *  nap:Cream+Live+2001+CD+2
  */
 
 // place in the optional ant tasks package
 // but in its own dotnet group
 
 package org.apache.tools.ant.taskdefs.optional.dotnet;
 
 // imports
 
 import java.io.File;
 import java.util.Vector;
 import java.util.Enumeration;
 import java.util.Hashtable;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 
 
 /**
  *  Abstract superclass for dotnet compiler tasks.
  *
  *  History
  *  <table>
  *    <tr>
  *      <td>
  *        0.1
  *      </td>
  *      <td>
  *        First creation
  *      </td>
  *      <td>
  *        Most of the code here was copied verbatim from v0.3 of
  *        Steve Loughran's CSharp optional task. Abstracted functionality
  *        to allow subclassing of other dotnet compiler types.
  *      </td>
  *    </tr>
  *
  *  </table>
  *
  *
  * @version     0.1
  */
 
 public abstract class DotnetCompile
          extends DotnetBaseMatchingTask {
 
     /**
      *  list of reference classes. (pretty much a classpath equivalent)
      */
     private String references;
 
     /**
      *  flag to enable automatic reference inclusion
      */
     private boolean includeDefaultReferences = true;
 
     /**
      *  icon for incorporation into apps
      */
     private File win32icon;
 
     /**
      *  icon for incorporation into apps
      */
     private File win32res;
 
     /**
      *  flag to control action on execution trouble
      */
     private boolean failOnError;
 
     /**
      *  using the path approach didn't work as it could not handle the implicit
      *  execution path. Perhaps that could be extracted from the runtime and
      *  then the path approach would be viable
      */
     private Path referenceFiles;
 
     /**
      *  optimise flag
      */
     private boolean optimize;
 
     /**
      * a list of definitions to support;
      */
     protected Vector definitionList = new Vector();
 
     /**
      * our resources
      */
     protected Vector resources = new Vector();
 
     /**
      *  executable
      */
 
     protected String executable;
 
     protected static final String REFERENCE_OPTION = "/reference:";
 
     /**
      *  debug flag. Controls generation of debug information.
      */
     protected boolean debug;
 
     /**
      *  warning level: 0-4, with 4 being most verbose
      */
     private int warnLevel;
 
     /**
      *  main class (or null for automatic choice)
      */
     protected String mainClass;
 
     /**
      *  any extra command options?
      */
     protected String extraOptions;
 
     /**
      *  type of target. Should be one of exe|library|module|winexe|(null)
      *  default is exe; the actual value (if not null) is fed to the command
      *  line. <br>
      *  See /target
      */
     protected String targetType;
 
     /**
      *  utf out flag
      */
 
     protected boolean utf8output = false;
 
     /**
      *  list of extra modules to refer to
      */
     protected String additionalModules;
     /**
      * filesets of references
      */
     protected Vector referenceFilesets = new Vector();
 
     /**
      * flag to set to to use @file based command cache
      */
     private boolean useResponseFile = false;
     private static final int AUTOMATIC_RESPONSE_FILE_THRESHOLD = 64;
 
     /**
      *  constructor inits everything and set up the search pattern
      */
 
     public DotnetCompile() {
         clear();
         setIncludes(getFilePattern());
     }
 
     /**
      *  reset all contents.
      */
     public void clear() {
         targetType = null;
         win32icon = null;
         srcDir = null;
         mainClass = null;
         warnLevel = 3;
         optimize = false;
         debug = true;
         references = null;
         failOnError = true;
         additionalModules = null;
         includeDefaultReferences = true;
         extraOptions = null;
     }
 
 
     /**
      * Semicolon separated list of DLLs to refer to.
      *
      *@param  s  The new References value
      */
     public void setReferences(String s) {
         references = s;
     }
 
 
     /**
      *  get the reference string or null for no argument needed
      *
      *@return    The References Parameter to CSC
      */
     protected String getReferencesParameter() {
         //bail on no references
         if (notEmpty(references)) {
             if (isWindows) {
                 return '\"' + REFERENCE_OPTION + references + '\"';
             } else {
                 return REFERENCE_OPTION + references;
             }
         } else {
             return null;
         }
     }
 
     /**
      * Path of references to include.
      * Wildcards should work.
      *
      *@param  path  another path to append
      */
     public void setReferenceFiles(Path path) {
         //demand create pathlist
         if (referenceFiles == null) {
             referenceFiles = new Path(this.getProject());
         }
         referenceFiles.append(path);
     }
 
     /**
      * add a new reference fileset to the compilation
      * @param reference
      */
     public void addReference(FileSet reference) {
         referenceFilesets.add(reference);
     }
 
 
 
     /**
      *  turn the path list into a list of files and a /references argument
      *
      *@return    null or a string of references.
      */
     protected String getReferenceFilesParameter() {
         //bail on no references
         if (references == null) {
             return null;
         }
         //iterate through the ref list & generate an entry for each
         //or just rely on the fact that the toString operator does this, but
         //noting that the separator is ';' on windows, ':' on unix
-        String refpath = references.toString();
 
         //bail on no references listed
-        if (refpath.length() == 0) {
+        if (references.length() == 0) {
             return null;
         }
 
         StringBuffer s = new StringBuffer(REFERENCE_OPTION);
         if (isWindows) {
             s.append('\"');
         }
-        s.append(refpath);
+        s.append(references);
         if (isWindows) {
             s.append('\"');
         }
         return s.toString();
     }
 
 
     /**
      * If true, automatically includes the common assemblies
      * in dotnet, and tells the compiler to link in mscore.dll.
      *
      *  set the automatic reference inclusion flag on or off this flag controls
      *  the /nostdlib option in CSC
      *
      *@param  f  on/off flag
      */
     public void setIncludeDefaultReferences(boolean f) {
         includeDefaultReferences = f;
     }
 
 
     /**
      *  query automatic reference inclusion flag
      *
      *@return    true if flag is turned on
      */
     public boolean getIncludeDefaultReferences() {
         return includeDefaultReferences;
     }
 
 
     /**
      *  get the include default references flag or null for no argument needed
      *
      *@return    The Parameter to CSC
      */
     protected String getIncludeDefaultReferencesParameter() {
         return "/nostdlib" + (includeDefaultReferences ? "-" : "+");
     }
 
 
 
     /**
      * If true, enables optimization flag.
      *
      *@param  f  on/off flag
      */
     public void setOptimize(boolean f) {
         optimize = f;
     }
 
 
     /**
      *  query the optimise flag
      *
      *@return    true if optimise is turned on
      */
     public boolean getOptimize() {
         return optimize;
     }
 
 
     /**
      *  get the optimise flag or null for no argument needed
      *
      *@return    The Optimize Parameter to CSC
      */
     protected String getOptimizeParameter() {
         return "/optimize" + (optimize ? "+" : "-");
     }
 
 
     /**
      *  set the debug flag on or off.
      *
      *@param  f  on/off flag
      */
     public void setDebug(boolean f) {
         debug = f;
     }
 
 
     /**
      *  query the debug flag
      *
      *@return    true if debug is turned on
      */
     public boolean getDebug() {
         return debug;
     }
 
 
     /**
      *  get the debug switch argument
      *
      *@return    The Debug Parameter to CSC
      */
     protected String getDebugParameter() {
         return "/debug" + (debug ? "+" : "-");
     }
 
 
     /**
      * Level of warning currently between 1 and 4
      * with 4 being the strictest.
      *
      *@param  warnLevel  warn level -see .net docs for valid range (probably
      *      0-4)
      */
     public void setWarnLevel(int warnLevel) {
         this.warnLevel = warnLevel;
     }
 
 
     /**
      *  query warn level
      *
      *@return    current value
      */
     public int getWarnLevel() {
         return warnLevel;
     }
 
 
     /**
      *  get the warn level switch
      *
      *@return    The WarnLevel Parameter to CSC
      */
     protected String getWarnLevelParameter() {
         return "/warn:" + warnLevel;
     }
 
 
     /**
      *  Sets the name of main class for executables.
      *
      *@param  mainClass  The new MainClass value
      */
     public void setMainClass(String mainClass) {
         this.mainClass = mainClass;
     }
 
 
     /**
      *  Gets the MainClass attribute
      *
      *@return    The MainClass value
      */
     public String getMainClass() {
         return this.mainClass;
     }
 
 
     /**
      *  get the /main argument or null for no argument needed
      *
      *@return    The MainClass Parameter to CSC
      */
     protected String getMainClassParameter() {
         if (mainClass != null && mainClass.length() != 0) {
             return "/main:" + mainClass;
         } else {
             return null;
         }
     }
 
 
     /**
      * Any extra options which are not explicitly supported
      * by this task.
      *
      *@param  extraOptions  The new ExtraOptions value
      */
     public void setExtraOptions(String extraOptions) {
         this.extraOptions = extraOptions;
     }
 
 
     /**
      *  Gets the ExtraOptions attribute
      *
      *@return    The ExtraOptions value
      */
     public String getExtraOptions() {
         return this.extraOptions;
     }
 
 
     /**
      *  get any extra options or null for no argument needed
      *
      *@return    The ExtraOptions Parameter to CSC
      */
     protected String getExtraOptionsParameter() {
         if (extraOptions != null && extraOptions.length() != 0) {
             return extraOptions;
         } else {
             return null;
         }
     }
 
     /**
      *  get any extra options or null for no argument needed, split
      *  them if they represent multiple options.
      *
      * @return    The ExtraOptions Parameter to CSC
      */
     protected String[] getExtraOptionsParameters() {
         String extra = getExtraOptionsParameter();
         return extra == null ? null : Commandline.translateCommandline(extra);
     }
 
     /**
      * Set the destination directory of files to be compiled.
      *
      *@param  dirName  The new DestDir value
      */
     public void setDestDir(File dirName) {
         log("DestDir currently unused", Project.MSG_WARN);
     }
 
 
     /**
      * set the target type to one of exe|library|module|winexe
      * @param targetType
      */
     public void setTargetType(TargetTypes targetType) {
         this.targetType = targetType.getValue();
     }
     /**
      * Set the type of target.
      *
      *@param  ttype          The new TargetType value
      *@exception  BuildException  if target is not one of
      *      exe|library|module|winexe
      */
     public void setTargetType(String ttype)
              throws BuildException {
         ttype = ttype.toLowerCase();
         if (ttype.equals("exe") || ttype.equals("library")
             || ttype.equals("module") || ttype.equals("winexe")) {
             targetType = ttype;
         } else {
             throw new BuildException("targetType " + ttype
                     + " is not one of 'exe', 'module', 'winexe' or 'library'");
         }
     }
 
 
     /**
      *  Gets the TargetType attribute
      *
      *@return    The TargetType value
      */
     public String getTargetType() {
         return targetType;
     }
 
 
     /**
      *  get the argument or null for no argument needed
      *
      *@return    The TargetType Parameter to CSC
      */
     protected String getTargetTypeParameter() {
         if (notEmpty(targetType)) {
             return "/target:" + targetType;
         } else {
             return null;
         }
     }
 
 
     /**
      *  Set the filename of icon to include.
      *
      *@param  fileName  path to the file. Can be relative, absolute, whatever.
      */
     public void setWin32Icon(File fileName) {
         win32icon = fileName;
     }
 
 
     /**
      *  get the argument or null for no argument needed
      *
      *@return    The Win32Icon Parameter to CSC
      */
     protected String getWin32IconParameter() {
         if (win32icon != null) {
             return "/win32icon:" + win32icon.toString();
         } else {
             return null;
         }
     }
 
 
     /**
      * Sets the filename of a win32 resource (.RES) file to include.
      * This is not a .NET resource, but what Windows is used to.
      *
      *@param  fileName  path to the file. Can be relative, absolute, whatever.
      */
     public void setWin32Res(File fileName) {
         win32res = fileName;
     }
 
     /**
      * Gets the file of the win32 .res file to include.
      * @return path to the file.
      */
     public File getWin32Res() {
         return win32res;
     }
 
 
     /**
      *  get the argument or null for no argument needed
      *
      *@return    The Win32Res Parameter to CSC
      */
     protected String getWin32ResParameter() {
         if (win32res != null) {
             return "/win32res:" + win32res.toString();
         } else {
             return null;
         }
     }
 
 
     /**
      * If true, require all compiler output to be in UTF8 format.
      *
      *@param  enabled  The new utf8Output value
      */
     public void setUtf8Output(boolean enabled) {
         utf8output = enabled;
     }
 
 
     /**
      *  Gets the utf8OutpuParameter attribute of the CSharp object
      *
      *@return    The utf8OutpuParameter value
      */
     protected String getUtf8OutputParameter() {
         return utf8output ? "/utf8output" : null;
     }
 
 
     /**
      * add a define to the list of definitions
      * @param define
      */
     public void addDefine(DotnetDefine define) {
         definitionList.addElement(define);
     }
 
 
     /**
      * get a list of definitions or null
      * @return a string beginning /D: or null for no definitions
      */
     protected String getDefinitionsParameter() throws BuildException {
         StringBuffer defines = new StringBuffer();
         Enumeration defEnum = definitionList.elements();
         boolean firstDefinition = true;
         while (defEnum.hasMoreElements()) {
             //loop through all definitions
             DotnetDefine define = (DotnetDefine) defEnum.nextElement();
             if (define.isSet(this)) {
                 //add those that are set, and a delimiter
                 if (!firstDefinition) {
                     defines.append(getDefinitionsDelimiter());
                 }
                 defines.append(define.getValue(this));
                 firstDefinition = false;
             }
         }
         if (defines.length() == 0) {
             return null;
         } else {
             return "/d:" + defines;
         }
     }
 
 
     /**
      * Semicolon separated list of modules to refer to.
      *
      *@param  params  The new additionalModules value
      */
     public void setAdditionalModules(String params) {
         additionalModules = params;
     }
 
 
     /**
      *  get the argument or null for no argument needed
      *
      *@return    The AdditionalModules Parameter to CSC
      */
     protected String getAdditionalModulesParameter() {
         if (notEmpty(additionalModules)) {
             return "/addmodule:" + additionalModules;
         } else {
             return null;
         }
     }
 
 
     /**
      *  get the argument or null for no argument needed
      *
      *@return    The OutputFile Parameter to CSC
      */
     protected String getDestFileParameter() {
         if (outputFile != null) {
             return "/out:" + outputFile.toString();
         } else {
             return null;
         }
     }
 
 
     /**
      * If true, fail on compilation errors.
      *
      *@param  b  The new FailOnError value
      */
     public void setFailOnError(boolean b) {
         failOnError = b;
     }
 
 
     /**
      *  query fail on error flag
      *
      *@return    The FailFailOnError value
      */
     public boolean getFailOnError() {
         return failOnError;
     }
 
     /**
      * link or embed a resource
      * @param resource
      */
     public void addResource(DotnetResource resource) {
         resources.add(resource);
     }
 
     /**
      * This method gets the name of the executable.
      * @return the name of the executable
      */
     protected String getExecutable() {
         return executable;
     }
 
     /**
      * set the name of the program, overriding the defaults.
      * Can be used to set the full path to a program, or to switch
      * to an alternate implementation of the command, such as the Mono or Rotor
      * versions -provided they use the same command line arguments as the
      * .NET framework edition
      * @param executable
      */
     public void setExecutable(String executable) {
         this.executable = executable;
     }
 
     /**
      *  test for a string containing something useful
      *
      *@param  s  string in
      *@return    true if the argument is not null or empty
      */
     protected boolean notEmpty(String s) {
         return s != null && s.length() != 0;
     }
 
     /**
      * validation code
      * @throws  BuildException  if validation failed
      */
     protected void validate()
             throws BuildException {
         if (outputFile != null && outputFile.isDirectory()) {
             throw new BuildException("destFile cannot be a directory");
         }
         if (getExecutable() == null) {
             throw new BuildException("There is no executable defined for this task");
         }
     }
 
     /**
      * Get the pattern for files to compile.
      * @return The compilation file pattern.
      */
     public String getFilePattern() {
         return "**/*." + getFileExtension();
     }
 
     /**
      * getter for flag
      * @return The flag indicating whether the compilation is using a response file.
      */
     public boolean isUseResponseFile() {
         return useResponseFile;
     }
 
     /**
      * Flag to turn on response file use; default=false.
      * When set the command params are saved to a file and
      * this is passed in with @file. The task automatically switches
      * to this mode with big commands; this option is here for
      * testing and emergencies
      * @param useResponseFile
      */
     public void setUseResponseFile(boolean useResponseFile) {
         this.useResponseFile = useResponseFile;
     }
 
     /**
      *  do the work by building the command line and then calling it
      *
      *@throws  BuildException  if validation or execution failed
      */
     public void execute()
              throws BuildException {
         validate();
         NetCommand command = createNetCommand();
         //set up response file options
         command.setAutomaticResponseFileThreshold(AUTOMATIC_RESPONSE_FILE_THRESHOLD);
         command.setUseResponseFile(useResponseFile);
         //fill in args
         fillInSharedParameters(command);
         addResources(command);
         addCompilerSpecificOptions(command);
         int referencesOutOfDate
             = addReferenceFilesets(command, getOutputFileTimestamp());
         //if the refs are out of date, force a build.
         boolean forceBuild = referencesOutOfDate > 0;
         addFilesAndExecute(command, forceBuild);
 
     }
 
     /**
      * Get the delimiter that the compiler uses between references.
      * For example, c# will return ";"; VB.NET will return ","
      * @return The string delimiter for the reference string.
      */
     public abstract String getReferenceDelimiter();
 
     /**
      * Get the extension of filenames to compile.
      * @return The string extension of files to compile.
      */
     public abstract String getFileExtension();
 
 
     /**
      * fill in the common information
      * @param command
      */
     protected void fillInSharedParameters(NetCommand command) {
         command.setFailOnError(getFailOnError());
         //fill in args
         command.addArgument("/nologo");
         command.addArgument(getAdditionalModulesParameter());
         command.addArgument(getDebugParameter());
         command.addArgument(getDefinitionsParameter());
         command.addArguments(getExtraOptionsParameters());
         command.addArgument(getMainClassParameter());
         command.addArgument(getOptimizeParameter());
         command.addArgument(getDestFileParameter());
         command.addArgument(getReferencesParameter());
         command.addArgument(getTargetTypeParameter());
         command.addArgument(getUtf8OutputParameter());
         command.addArgument(getWin32IconParameter());
         command.addArgument(getWin32ResParameter());
     }
 
     /**
      * for every resource declared, we get the (language specific)
      * resource setting
      */
     protected void addResources(NetCommand command) {
         Enumeration e = resources.elements();
         while (e.hasMoreElements()) {
             DotnetResource resource = (DotnetResource) e.nextElement();
             createResourceParameter(command, resource);
         }
     }
 
     /**
      * from a resource, get the
      * @param resource
      * @return a string containing the resource param, or a null string
      * to conditionally exclude a resource.
      */
     protected abstract void createResourceParameter(NetCommand command, DotnetResource resource);
 
 
     /**
      * run through the list of reference files and add them to the command
      * @param outputTimestamp timestamp to compare against
      * @return number of files out of date
      */
 
     protected int addReferenceFilesets(NetCommand command, long outputTimestamp) {
         int filesOutOfDate = 0;
         Hashtable filesToBuild = new Hashtable();
         for (int i = 0; i < referenceFilesets.size(); i++) {
             FileSet fs = (FileSet) referenceFilesets.elementAt(i);
             filesOutOfDate += command.scanOneFileset(
                     fs.getDirectoryScanner(getProject()),
                     filesToBuild,
                     outputTimestamp);
         }
         //bail out early if there were no files
         if (filesToBuild.size() == 0) {
             return 0;
         }
         //now scan the hashtable and add the files
         Enumeration files = filesToBuild.elements();
         while (files.hasMoreElements()) {
             File file = (File) files.nextElement();
             if (isFileManagedBinary(file)) {
                 if (isWindows) command.addArgument('"'+REFERENCE_OPTION+file.toString()+'"');
                 else command.addArgument(REFERENCE_OPTION+file.toString());
             } else {
                 log("ignoring " + file + " as it is not a managed executable",
                         Project.MSG_VERBOSE);
             }
 
         }
 
         return filesOutOfDate;
     }
 
     /**
      * create our helper command
      * @return a command prefilled with the exe name and task name
      */
     protected NetCommand createNetCommand() {
         NetCommand command = new NetCommand(this, getTaskName(), getExecutable());
         return command;
     }
 
     /**
      * add any compiler specifics
      * @param command
      */
     protected abstract void addCompilerSpecificOptions(NetCommand command);
 
     /**
      * override point for delimiting definitions.
      * @return The definitions limiter, i.e., ";"
      */
     public String getDefinitionsDelimiter() {
         return ";";
     }
 
 
     /**
      * test for a file being managed or not
      * @return true if we think this is a managed executable, and thus OK
      * for linking
      * @todo look at the PE header of the exe and see if it is managed or not.
      */
     protected static boolean isFileManagedBinary(File file) {
         String filename = file.toString().toLowerCase();
         return filename.endsWith(".exe") || filename.endsWith(".dll")
                 || filename.endsWith(".netmodule");
     }
 
     /**
      * Target types to build.
      * valid build types are exe|library|module|winexe
      */
     public static class TargetTypes extends EnumeratedAttribute {
         public String[] getValues() {
             return new String[] {
                 "exe",
                 "library",
                 "module",
                 "winexe"
             };
         }
     }
 
 
 }
 
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/GenericDeploymentTool.java b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/GenericDeploymentTool.java
index 0eaaf04e0..5071411e2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/GenericDeploymentTool.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/GenericDeploymentTool.java
@@ -1,925 +1,925 @@
 /*
- * Copyright  2000-2004 The Apache Software Foundation
+ * Copyright  2000-2004,2006 The Apache Software Foundation
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
 package org.apache.tools.ant.taskdefs.optional.ejb;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.jar.JarOutputStream;
 import java.util.jar.Manifest;
 import java.util.zip.ZipEntry;
 
 import javax.xml.parsers.SAXParser;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.depend.DependencyAnalyzer;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 
 
 /**
  * A deployment tool which creates generic EJB jars. Generic jars contains
  * only those classes and META-INF entries specified in the EJB 1.1 standard
  *
  * This class is also used as a framework for the creation of vendor specific
  * deployment tools. A number of template methods are provided through which the
  * vendor specific tool can hook into the EJB creation process.
  *
  */
 public class GenericDeploymentTool implements EJBDeploymentTool {
     /** The standard META-INF directory in jar files */
     protected static final String META_DIR  = "META-INF/";
 
     /** The standard MANIFEST file */
     protected static final String MANIFEST  = META_DIR + "MANIFEST.MF";
 
     /** Name for EJB Deployment descriptor within EJB jars */
     protected static final String EJB_DD    = "ejb-jar.xml";
 
     /** A dependency analyzer name to find ancestor classes */
     public static final String ANALYZER_SUPER = "super";
     /** A dependency analyzer name to find all related classes */
     public static final String ANALYZER_FULL = "full";
     /** A dependency analyzer name for no analyzer */
     public static final String ANALYZER_NONE = "none";
 
     /** The default analyzer */
     public static final String DEFAULT_ANALYZER = ANALYZER_SUPER;
 
     /** The analyzer class for the super analyzer */
     public static final String ANALYZER_CLASS_SUPER
         = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
     /** The analyzer class for the super analyzer */
     public static final String ANALYZER_CLASS_FULL
         = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
 
     /**
      * The configuration from the containing task. This config combined
      * with the settings of the individual attributes here constitues the
      * complete config for this deployment tool.
      */
     private EjbJar.Config config;
 
     /** Stores a handle to the directory to put the Jar files in */
     private File destDir;
 
     /** The classpath to use with this deployment tool. This is appended to
         any paths from the ejbjar task itself.*/
     private Path classpath;
 
     /** Instance variable that stores the suffix for the generated jarfile. */
     private String genericJarSuffix = "-generic.jar";
 
     /**
      * The task to which this tool belongs. This is used to access services
      * provided by the ant core, such as logging.
      */
     private Task task;
 
     /**
      * The classloader generated from the given classpath to load
      * the super classes and super interfaces.
      */
     private ClassLoader classpathLoader = null;
 
      /**
      * Set of files have been loaded into the EJB jar
      */
     private Set addedfiles;
 
     /**
      * Handler used to parse the EJB XML descriptor
      */
     private DescriptorHandler handler;
 
     /**
      * Dependency analyzer used to collect class dependencies
      */
     private DependencyAnalyzer dependencyAnalyzer;
 
     public GenericDeploymentTool() {
     }
 
 
     /**
      * Set the destination directory; required.
      * @param inDir the destination directory.
      */
     public void setDestdir(File inDir) {
         this.destDir = inDir;
     }
 
     /**
      * Get the destination directory.
      *
      * @return the destination directory into which EJB jars are to be written
      */
     protected File getDestDir() {
         return destDir;
     }
 
 
     /**
      * Set the task which owns this tool
      *
      * @param task the Task to which this deployment tool is associated.
      */
     public void setTask(Task task) {
         this.task = task;
     }
 
     /**
      * Get the task for this tool.
      *
      * @return the Task instance this tool is associated with.
      */
     protected Task getTask() {
         return task;
     }
 
     /**
      * Get the basename terminator.
      *
      * @return an ejbjar task configuration
      */
     protected EjbJar.Config getConfig() {
         return config;
     }
 
     /**
      * Indicate if this build is using the base jar name.
      *
      * @return true if the name of the generated jar is coming from the
      *              basejarname attribute
      */
     protected boolean usingBaseJarName() {
         return config.baseJarName != null;
     }
 
     /**
      * Set the suffix for the generated jar file.
      * @param inString the string to use as the suffix.
      */
     public void setGenericJarSuffix(String inString) {
         this.genericJarSuffix = inString;
     }
 
     /**
      * Add the classpath for the user classes
      *
      * @return a Path instance to be configured by Ant.
      */
     public Path createClasspath() {
         if (classpath == null) {
             classpath = new Path(task.getProject());
         }
         return classpath.createPath();
     }
 
     /**
      * Set the classpath to be used for this compilation.
      *
      * @param classpath the classpath to be used for this build.
      */
     public void setClasspath(Path classpath) {
         this.classpath = classpath;
     }
 
     /**
      * Get the classpath by combining the one from the surrounding task, if any
      * and the one from this tool.
      *
      * @return the combined classpath
      */
     protected Path getCombinedClasspath() {
         Path combinedPath = classpath;
         if (config.classpath != null) {
             if (combinedPath == null) {
                 combinedPath = config.classpath;
             } else {
                 combinedPath.append(config.classpath);
             }
         }
 
         return combinedPath;
     }
 
     /**
      * Log a message to the Ant output.
      *
      * @param message the message to be logged.
      * @param level the severity of this message.
      */
     protected void log(String message, int level) {
         getTask().log(message, level);
     }
 
     /**
      * Get the build file location associated with this element's task.
      *
      * @return the task's location instance.
      */
     protected Location getLocation() {
         return getTask().getLocation();
     }
 
     private void createAnalyzer() {
         String analyzer = config.analyzer;
         if (analyzer == null) {
             analyzer = DEFAULT_ANALYZER;
         }
 
         if (analyzer.equals(ANALYZER_NONE)) {
             return;
         }
 
         String analyzerClassName = null;
         if (analyzer.equals(ANALYZER_SUPER)) {
             analyzerClassName = ANALYZER_CLASS_SUPER;
         } else if (analyzer.equals(ANALYZER_FULL)) {
             analyzerClassName = ANALYZER_CLASS_FULL;
         } else {
             analyzerClassName = analyzer;
         }
 
         try {
             Class analyzerClass = Class.forName(analyzerClassName);
             dependencyAnalyzer
                 = (DependencyAnalyzer) analyzerClass.newInstance();
             dependencyAnalyzer.addClassPath(new Path(task.getProject(),
                 config.srcDir.getPath()));
             dependencyAnalyzer.addClassPath(config.classpath);
         } catch (NoClassDefFoundError e) {
             dependencyAnalyzer = null;
             task.log("Unable to load dependency analyzer: " + analyzerClassName
                 + " - dependent class not found: " + e.getMessage(),
                 Project.MSG_WARN);
         } catch (Exception e) {
             dependencyAnalyzer = null;
             task.log("Unable to load dependency analyzer: " + analyzerClassName
                      + " - exception: " + e.getMessage(),
                 Project.MSG_WARN);
         }
     }
 
 
     /**
      * Configure this tool for use in the ejbjar task.
      *
      * @param config the configuration from the surrounding ejbjar task.
      */
     public void configure(EjbJar.Config config) {
         this.config = config;
 
         createAnalyzer();
         classpathLoader = null;
     }
 
     /**
      * Utility method that encapsulates the logic of adding a file entry to
      * a .jar file.  Used by execute() to add entries to the jar file as it is
      * constructed.
      * @param jStream A JarOutputStream into which to write the
      *        jar entry.
      * @param inputFile A File from which to read the
      *        contents the file being added.
      * @param logicalFilename A String representing the name, including
      *        all relevant path information, that should be stored for the entry
      *        being added.
      */
     protected void addFileToJar(JarOutputStream jStream,
                                 File inputFile,
                                 String logicalFilename)
         throws BuildException {
         FileInputStream iStream = null;
         try {
             if (!addedfiles.contains(logicalFilename)) {
                 iStream = new FileInputStream(inputFile);
                 // Create the zip entry and add it to the jar file
                 ZipEntry zipEntry = new ZipEntry(logicalFilename.replace('\\', '/'));
                 jStream.putNextEntry(zipEntry);
 
                 // Create the file input stream, and buffer everything over
                 // to the jar output stream
                 byte[] byteBuffer = new byte[2 * 1024];
                 int count = 0;
                 do {
                     jStream.write(byteBuffer, 0, count);
                     count = iStream.read(byteBuffer, 0, byteBuffer.length);
                 } while (count != -1);
 
                 //add it to list of files in jar
                 addedfiles.add(logicalFilename);
            }
         } catch (IOException ioe) {
             log("WARNING: IOException while adding entry "
                 + logicalFilename + " to jarfile from "
                 + inputFile.getPath() + " "  + ioe.getClass().getName()
                 + "-" + ioe.getMessage(), Project.MSG_WARN);
         } finally {
             // Close up the file input stream for the class file
             if (iStream != null) {
                 try {
                     iStream.close();
                 } catch (IOException closeException) {
                     // ignore
                 }
             }
         }
     }
 
     protected DescriptorHandler getDescriptorHandler(File srcDir) {
         DescriptorHandler h = new DescriptorHandler(getTask(), srcDir);
 
         registerKnownDTDs(h);
 
         // register any DTDs supplied by the user
         for (Iterator i = getConfig().dtdLocations.iterator(); i.hasNext();) {
             EjbJar.DTDLocation dtdLocation = (EjbJar.DTDLocation) i.next();
             h.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
         }
         return h;
     }
 
     /**
      * Register the locations of all known DTDs.
      *
      * vendor-specific subclasses should override this method to define
      * the vendor-specific locations of the EJB DTDs
      */
     protected void registerKnownDTDs(DescriptorHandler handler) {
         // none to register for generic
     }
 
     public void processDescriptor(String descriptorFileName, SAXParser saxParser) {
 
         checkConfiguration(descriptorFileName, saxParser);
 
         try {
             handler = getDescriptorHandler(config.srcDir);
 
             // Retrive the files to be added to JAR from EJB descriptor
             Hashtable ejbFiles = parseEjbFiles(descriptorFileName, saxParser);
 
             // Add any support classes specified in the build file
             addSupportClasses(ejbFiles);
 
             // Determine the JAR filename (without filename extension)
             String baseName = getJarBaseName(descriptorFileName);
 
             String ddPrefix = getVendorDDPrefix(baseName, descriptorFileName);
 
             File manifestFile = getManifestFile(ddPrefix);
             if (manifestFile != null) {
                 ejbFiles.put(MANIFEST, manifestFile);
             }
 
 
 
             // First the regular deployment descriptor
             ejbFiles.put(META_DIR + EJB_DD,
                          new File(config.descriptorDir, descriptorFileName));
 
             // now the vendor specific files, if any
             addVendorFiles(ejbFiles, ddPrefix);
 
             // add any dependent files
             checkAndAddDependants(ejbFiles);
 
             // Lastly create File object for the Jar files. If we are using
             // a flat destination dir, then we need to redefine baseName!
             if (config.flatDestDir && baseName.length() != 0) {
                 int startName = baseName.lastIndexOf(File.separator);
                 if (startName == -1) {
                     startName = 0;
                 }
 
                 int endName   = baseName.length();
                 baseName = baseName.substring(startName, endName);
             }
 
             File jarFile = getVendorOutputJarFile(baseName);
 
 
             // Check to see if we need a build and start doing the work!
             if (needToRebuild(ejbFiles, jarFile)) {
                 // Log that we are going to build...
                 log("building "
                               + jarFile.getName()
                               + " with "
                               + String.valueOf(ejbFiles.size())
                               + " files",
                               Project.MSG_INFO);
 
                 // Use helper method to write the jarfile
                 String publicId = getPublicId();
                 writeJar(baseName, jarFile, ejbFiles, publicId);
 
             } else {
                 // Log that the file is up to date...
                 log(jarFile.toString() + " is up to date.",
                               Project.MSG_VERBOSE);
             }
 
         } catch (SAXException se) {
             String msg = "SAXException while parsing '"
-                + descriptorFileName.toString()
+                + descriptorFileName
                 + "'. This probably indicates badly-formed XML."
                 + "  Details: "
                 + se.getMessage();
             throw new BuildException(msg, se);
         } catch (IOException ioe) {
             String msg = "IOException while parsing'"
                 + descriptorFileName.toString()
                 + "'.  This probably indicates that the descriptor"
                 + " doesn't exist. Details: "
                 + ioe.getMessage();
             throw new BuildException(msg, ioe);
         }
     }
 
     /**
      * This method is called as the first step in the processDescriptor method
      * to allow vendor-specific subclasses to validate the task configuration
      * prior to processing the descriptor.  If the configuration is invalid,
      * a BuildException should be thrown.
      *
      * @param descriptorFileName String representing the file name of an EJB
      *                           descriptor to be processed
      * @param saxParser          SAXParser which may be used to parse the XML
      *                           descriptor
      * @exception BuildException     Thrown if the configuration is invalid
      */
     protected void checkConfiguration(String descriptorFileName,
                                     SAXParser saxParser) throws BuildException {
 
         /*
          * For the GenericDeploymentTool, do nothing.  Vendor specific
          * subclasses should throw a BuildException if the configuration is
          * invalid for their server.
          */
     }
 
     /**
      * This method returns a list of EJB files found when the specified EJB
      * descriptor is parsed and processed.
      *
      * @param descriptorFileName String representing the file name of an EJB
      *                           descriptor to be processed
      * @param saxParser          SAXParser which may be used to parse the XML
      *                           descriptor
      * @return                   Hashtable of EJB class (and other) files to be
      *                           added to the completed JAR file
      * @throws SAXException      Any SAX exception, possibly wrapping another
      *                           exception
      * @throws IOException       An IOException from the parser, possibly from a
      *                           the byte stream or character stream
      */
     protected Hashtable parseEjbFiles(String descriptorFileName, SAXParser saxParser)
                             throws IOException, SAXException {
         FileInputStream descriptorStream = null;
         Hashtable ejbFiles = null;
 
         try {
 
             /* Parse the ejb deployment descriptor.  While it may not
              * look like much, we use a SAXParser and an inner class to
              * get hold of all the classfile names for the descriptor.
              */
             descriptorStream
                 = new FileInputStream(new File(config.descriptorDir, descriptorFileName));
             saxParser.parse(new InputSource(descriptorStream), handler);
 
             ejbFiles = handler.getFiles();
 
         } finally {
             if (descriptorStream != null) {
                 try {
                     descriptorStream.close();
                 } catch (IOException closeException) {
                     // ignore
                 }
             }
         }
 
         return ejbFiles;
     }
 
     /**
      * Adds any classes the user specifies using <i>support</i> nested elements
      * to the <code>ejbFiles</code> Hashtable.
      *
      * @param ejbFiles Hashtable of EJB classes (and other) files that will be
      *                 added to the completed JAR file
      */
     protected void addSupportClasses(Hashtable ejbFiles) {
         // add in support classes if any
         Project project = task.getProject();
         for (Iterator i = config.supportFileSets.iterator(); i.hasNext();) {
             FileSet supportFileSet = (FileSet) i.next();
             File supportBaseDir = supportFileSet.getDir(project);
             DirectoryScanner supportScanner = supportFileSet.getDirectoryScanner(project);
             supportScanner.scan();
             String[] supportFiles = supportScanner.getIncludedFiles();
             for (int j = 0; j < supportFiles.length; ++j) {
                 ejbFiles.put(supportFiles[j], new File(supportBaseDir, supportFiles[j]));
             }
         }
     }
 
 
     /**
      * Using the EJB descriptor file name passed from the <code>ejbjar</code>
      * task, this method returns the "basename" which will be used to name the
      * completed JAR file.
      *
      * @param descriptorFileName String representing the file name of an EJB
      *                           descriptor to be processed
      * @return                   The "basename" which will be used to name the
      *                           completed JAR file
      */
     protected String getJarBaseName(String descriptorFileName) {
 
         String baseName = "";
 
         // Work out what the base name is
         if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.BASEJARNAME)) {
             String canonicalDescriptor = descriptorFileName.replace('\\', '/');
             int index = canonicalDescriptor.lastIndexOf('/');
             if (index != -1) {
                 baseName = descriptorFileName.substring(0, index + 1);
             }
             baseName += config.baseJarName;
         } else if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.DESCRIPTOR)) {
             int lastSeparatorIndex = descriptorFileName.lastIndexOf(File.separator);
             int endBaseName = -1;
             if (lastSeparatorIndex != -1) {
                 endBaseName = descriptorFileName.indexOf(config.baseNameTerminator,
                                                             lastSeparatorIndex);
             } else {
                 endBaseName = descriptorFileName.indexOf(config.baseNameTerminator);
             }
 
             if (endBaseName != -1) {
                 baseName = descriptorFileName.substring(0, endBaseName);
             } else {
                 throw new BuildException("Unable to determine jar name "
                     + "from descriptor \"" + descriptorFileName + "\"");
             }
         } else if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.DIRECTORY)) {
             File descriptorFile = new File(config.descriptorDir, descriptorFileName);
             String path = descriptorFile.getAbsolutePath();
             int lastSeparatorIndex
                 = path.lastIndexOf(File.separator);
             if (lastSeparatorIndex == -1) {
                 throw new BuildException("Unable to determine directory name holding descriptor");
             }
             String dirName = path.substring(0, lastSeparatorIndex);
             int dirSeparatorIndex = dirName.lastIndexOf(File.separator);
             if (dirSeparatorIndex != -1) {
                 dirName = dirName.substring(dirSeparatorIndex + 1);
             }
 
             baseName = dirName;
         } else if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.EJB_NAME)) {
             baseName = handler.getEjbName();
         }
         return baseName;
     }
 
     /**
      * Get the prefix for vendor deployment descriptors.
      *
      * This will contain the path and the start of the descriptor name,
      * depending on the naming scheme
      */
     public String getVendorDDPrefix(String baseName, String descriptorFileName) {
         String ddPrefix = null;
 
         if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.DESCRIPTOR)) {
             ddPrefix = baseName + config.baseNameTerminator;
         } else if (config.namingScheme.getValue().equals(EjbJar.NamingScheme.BASEJARNAME)
             || config.namingScheme.getValue().equals(EjbJar.NamingScheme.EJB_NAME)
             || config.namingScheme.getValue().equals(EjbJar.NamingScheme.DIRECTORY)) {
             String canonicalDescriptor = descriptorFileName.replace('\\', '/');
             int index = canonicalDescriptor.lastIndexOf('/');
             if (index == -1) {
                 ddPrefix = "";
             } else {
                 ddPrefix = descriptorFileName.substring(0, index + 1);
             }
         }
         return ddPrefix;
     }
 
     /**
      * Add any vendor specific files which should be included in the
      * EJB Jar.
      */
     protected void addVendorFiles(Hashtable ejbFiles, String ddPrefix) {
         // nothing to add for generic tool.
     }
 
 
     /**
      * Get the vendor specific name of the Jar that will be output. The modification date
      * of this jar will be checked against the dependent bean classes.
      */
     File getVendorOutputJarFile(String baseName) {
         return new File(destDir, baseName + genericJarSuffix);
     }
 
     /**
      * This method checks the timestamp on each file listed in the <code>
      * ejbFiles</code> and compares them to the timestamp on the <code>jarFile
      * </code>.  If the <code>jarFile</code>'s timestamp is more recent than
      * each EJB file, <code>true</code> is returned.  Otherwise, <code>false
      * </code> is returned.
      * TODO: find a way to check the manifest-file, that is found by naming convention
      *
      * @param ejbFiles Hashtable of EJB classes (and other) files that will be
      *                 added to the completed JAR file
      * @param jarFile  JAR file which will contain all of the EJB classes (and
      *                 other) files
      * @return         boolean indicating whether or not the <code>jarFile</code>
      *                 is up to date
      */
     protected boolean needToRebuild(Hashtable ejbFiles, File jarFile) {
         if (jarFile.exists()) {
             long lastBuild = jarFile.lastModified();
 
             Iterator fileIter = ejbFiles.values().iterator();
 
             // Loop through the files seeing if any has been touched
             // more recently than the destination jar.
             while (fileIter.hasNext()) {
                 File currentFile = (File) fileIter.next();
                 if (lastBuild < currentFile.lastModified()) {
                     log("Build needed because " + currentFile.getPath() + " is out of date",
                         Project.MSG_VERBOSE);
                     return true;
                 }
             }
             return false;
         }
 
         return true;
     }
 
     /**
      * Returns the Public ID of the DTD specified in the EJB descriptor.  Not
      * every vendor-specific <code>DeploymentTool</code> will need to reference
      * this value or may want to determine this value in a vendor-specific way.
      *
      * @return Public ID of the DTD specified in the EJB descriptor.
      */
     protected String getPublicId() {
         return handler.getPublicId();
     }
 
     /**
      * Get the manifets file to use for building the generic jar.
      *
      * If the file does not exist the global manifest from the config is used
      * otherwise the default Ant manifest will be used.
      *
      * @param prefix the prefix where to llook for the manifest file based on
      *        the naming convention.
      *
      * @return the manifest file or null if the manifest file does not exist
      */
     protected File getManifestFile(String prefix) {
         File manifestFile
             = new File(getConfig().descriptorDir, prefix + "manifest.mf");
         if (manifestFile.exists()) {
             return manifestFile;
         }
 
         if (config.manifest != null) {
             return config.manifest;
         }
         return null;
     }
 
     /**
      * Method used to encapsulate the writing of the JAR file. Iterates over the
      * filenames/java.io.Files in the Hashtable stored on the instance variable
      * ejbFiles.
      */
     protected void writeJar(String baseName, File jarfile, Hashtable files,
                             String publicId) throws BuildException {
 
         JarOutputStream jarStream = null;
         try {
             // clean the addedfiles set
             if (addedfiles == null) {
                 addedfiles = new HashSet();
             } else {
                 addedfiles.clear();
             }
 
             /* If the jarfile already exists then whack it and recreate it.
              * Should probably think of a more elegant way to handle this
              * so that in case of errors we don't leave people worse off
              * than when we started =)
              */
             if (jarfile.exists()) {
                 jarfile.delete();
             }
             jarfile.getParentFile().mkdirs();
             jarfile.createNewFile();
 
             InputStream in = null;
             Manifest manifest = null;
             try {
                 File manifestFile = (File) files.get(MANIFEST);
                 if (manifestFile != null && manifestFile.exists()) {
                     in = new FileInputStream(manifestFile);
                 } else {
                     String defaultManifest = "/org/apache/tools/ant/defaultManifest.mf";
                     in = this.getClass().getResourceAsStream(defaultManifest);
                     if (in == null) {
                         throw new BuildException("Could not find "
                             + "default manifest: " + defaultManifest);
                     }
                 }
 
                 manifest = new Manifest(in);
             } catch (IOException e) {
                 throw new BuildException ("Unable to read manifest", e, getLocation());
             } finally {
                 if (in != null) {
                     in.close();
                 }
             }
 
             // Create the streams necessary to write the jarfile
 
             jarStream = new JarOutputStream(new FileOutputStream(jarfile), manifest);
             jarStream.setMethod(JarOutputStream.DEFLATED);
 
             // Loop through all the class files found and add them to the jar
             for (Iterator entryIterator = files.keySet().iterator(); entryIterator.hasNext();) {
                 String entryName = (String) entryIterator.next();
                 if (entryName.equals(MANIFEST)) {
                     continue;
                 }
 
                 File entryFile = (File) files.get(entryName);
 
                 log("adding file '" + entryName + "'",
                               Project.MSG_VERBOSE);
 
                 addFileToJar(jarStream, entryFile, entryName);
 
                 // See if there are any inner classes for this class and add them in if there are
                 InnerClassFilenameFilter flt = new InnerClassFilenameFilter(entryFile.getName());
                 File entryDir = entryFile.getParentFile();
                 String[] innerfiles = entryDir.list(flt);
                 if (innerfiles != null) {
                     for (int i = 0, n = innerfiles.length; i < n; i++) {
 
                         //get and clean up innerclass name
                         int entryIndex = entryName.lastIndexOf(entryFile.getName()) - 1;
                         if (entryIndex < 0) {
                             entryName = innerfiles[i];
                         } else {
                             entryName = entryName.substring(0, entryIndex)
                                 + File.separatorChar + innerfiles[i];
                         }
                         // link the file
                         entryFile = new File(config.srcDir, entryName);
 
                         log("adding innerclass file '" + entryName + "'",
                                 Project.MSG_VERBOSE);
 
                         addFileToJar(jarStream, entryFile, entryName);
 
                     }
                 }
             }
         } catch (IOException ioe) {
             String msg = "IOException while processing ejb-jar file '"
                 + jarfile.toString()
                 + "'. Details: "
                 + ioe.getMessage();
             throw new BuildException(msg, ioe);
         } finally {
             if (jarStream != null) {
                 try {
                     jarStream.close();
                 } catch (IOException closeException) {
                     // ignore
                 }
             }
         }
     } // end of writeJar
 
 
     /**
      * Add all available classes, that depend on Remote, Home, Bean, PK
      * @param checkEntries files, that are extracted from the deployment descriptor
      */
     protected void checkAndAddDependants(Hashtable checkEntries)
         throws BuildException {
 
         if (dependencyAnalyzer == null) {
             return;
         }
 
         dependencyAnalyzer.reset();
 
         Iterator i = checkEntries.keySet().iterator();
         while (i.hasNext()) {
             String entryName = (String) i.next();
             if (entryName.endsWith(".class")) {
                 String className = entryName.substring(0,
                     entryName.length() - ".class".length());
                 className = className.replace(File.separatorChar, '/');
                 className = className.replace('/', '.');
 
                 dependencyAnalyzer.addRootClass(className);
             }
         }
 
         Enumeration e = dependencyAnalyzer.getClassDependencies();
 
         while (e.hasMoreElements()) {
             String classname = (String) e.nextElement();
             String location
                 = classname.replace('.', File.separatorChar) + ".class";
             File classFile = new File(config.srcDir, location);
             if (classFile.exists()) {
                 checkEntries.put(location, classFile);
                 log("dependent class: " + classname + " - " + classFile,
                     Project.MSG_VERBOSE);
             }
         }
     }
 
 
     /**
      * Returns a Classloader object which parses the passed in generic EjbJar classpath.
      * The loader is used to dynamically load classes from javax.ejb.* and the classes
      * being added to the jar.
      *
      */
     protected ClassLoader getClassLoaderForBuild() {
         if (classpathLoader != null) {
             return classpathLoader;
         }
 
         Path combinedClasspath = getCombinedClasspath();
 
         // only generate a new ClassLoader if we have a classpath
         if (combinedClasspath == null) {
             classpathLoader = getClass().getClassLoader();
         } else {
             classpathLoader
                 = getTask().getProject().createClassLoader(combinedClasspath);
         }
 
         return classpathLoader;
     }
 
     /**
      * Called to validate that the tool parameters have been configured.
      *
      * @throws BuildException If the Deployment Tool's configuration isn't
      *                        valid
      */
     public void validateConfigured() throws BuildException {
         if ((destDir == null) || (!destDir.isDirectory())) {
             String msg = "A valid destination directory must be specified "
                             + "using the \"destdir\" attribute.";
             throw new BuildException(msg, getLocation());
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/extension/JarLibManifestTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/extension/JarLibManifestTask.java
index e3ca1f3b3..58ac6d062 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/extension/JarLibManifestTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/extension/JarLibManifestTask.java
@@ -1,312 +1,313 @@
 /*
  * Copyright  2002-2006 The Apache Software Foundation
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
 package org.apache.tools.ant.taskdefs.optional.extension;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.jar.Attributes;
 import java.util.jar.Manifest;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 
 /**
  * Generates a manifest that declares all the dependencies.
  * The dependencies are determined by looking in the
  * specified path and searching for Extension / "Optional Package"
  * specifications in the manifests of the jars.
  *
  * <p>Prior to JDK1.3, an "Optional Package" was known as an Extension.
  * The specification for this mechanism is available in the JDK1.3
  * documentation in the directory
  * $JDK_HOME/docs/guide/extensions/versioning.html. Alternatively it is
  * available online at <a href="http://java.sun.com/j2se/1.3/docs/guide/extensions/versioning.html">
  * http://java.sun.com/j2se/1.3/docs/guide/extensions/versioning.html</a>.</p>
  *
  * @ant.task name="jarlib-manifest"
  */
 public final class JarLibManifestTask extends Task {
     /**
      * Version of manifest spec that task generates.
      */
     private static final String MANIFEST_VERSION = "1.0";
 
     /**
      * "Created-By" string used when creating manifest.
      */
     private static final String CREATED_BY = "Created-By";
 
     /**
      * The library to display information about.
      */
     private File destFile;
 
     /**
      * The extension supported by this library (if any).
      */
     private Extension extension;
 
     /**
      * ExtensionAdapter objects representing
      * dependencies required by library.
      */
     private final ArrayList dependencies = new ArrayList();
 
     /**
      * ExtensionAdapter objects representing optional
      * dependencies required by library.
      */
     private final ArrayList optionals = new ArrayList();
 
     /**
      * Extra attributes the user specifies for main section
      * in manifest.
      */
     private final ArrayList extraAttributes = new ArrayList();
 
     /**
      * The location where generated manifest is placed.
      *
      * @param destFile The location where generated manifest is placed.
      */
     public void setDestfile(final File destFile) {
         this.destFile = destFile;
     }
 
     /**
      * Adds an extension that this library implements.
      *
      * @param extensionAdapter an extension that this library implements.
      *
      * @throws BuildException if there is multiple extensions detected
      *         in the library.
      */
     public void addConfiguredExtension(final ExtensionAdapter extensionAdapter)
         throws BuildException {
         if (null != extension) {
             final String message =
                 "Can not have multiple extensions defined in one library.";
             throw new BuildException(message);
         }
         extension = extensionAdapter.toExtension();
     }
 
     /**
      * Adds a set of extensions that this library requires.
      *
      * @param extensionSet a set of extensions that this library requires.
      */
     public void addConfiguredDepends(final ExtensionSet extensionSet) {
         dependencies.add(extensionSet);
     }
 
     /**
      * Adds a set of extensions that this library optionally requires.
      *
      * @param extensionSet a set of extensions that this library optionally requires.
      */
     public void addConfiguredOptions(final ExtensionSet extensionSet) {
         optionals.add(extensionSet);
     }
 
     /**
      * Adds an attribute that is to be put in main section of manifest.
      *
      * @param attribute an attribute that is to be put in main section of manifest.
      */
     public void addConfiguredAttribute(final ExtraAttribute attribute) {
         extraAttributes.add(attribute);
     }
 
     /**
      * Execute the task.
      *
      * @throws BuildException if the task fails.
      */
     public void execute() throws BuildException {
         validate();
 
         final Manifest manifest = new Manifest();
         final Attributes attributes = manifest.getMainAttributes();
 
         attributes.put(Attributes.Name.MANIFEST_VERSION, MANIFEST_VERSION);
         final String createdBy = "Apache Ant " + getProject().getProperty(MagicNames.ANT_VERSION);
         attributes.putValue(CREATED_BY, createdBy);
 
         appendExtraAttributes(attributes);
 
         if (null != extension) {
             Extension.addExtension(extension, attributes);
         }
 
         //Add all the dependency data to manifest for dependencies
         final ArrayList depends = toExtensions(dependencies);
         appendExtensionList(attributes,
                              Extension.EXTENSION_LIST,
                              "lib",
                              depends.size());
         appendLibraryList(attributes, "lib", depends);
 
         //Add all the dependency data to manifest for "optional"
         //dependencies
         final ArrayList option = toExtensions(optionals);
         appendExtensionList(attributes,
                              Extension.OPTIONAL_EXTENSION_LIST,
                              "opt",
                              option.size());
         appendLibraryList(attributes, "opt", option);
 
         try {
             final String message = "Generating manifest " + destFile.getAbsoluteFile();
             log(message, Project.MSG_INFO);
             writeManifest(manifest);
         } catch (final IOException ioe) {
             throw new BuildException(ioe.getMessage(), ioe);
         }
     }
 
     /**
      * Validate the tasks parameters.
      *
      * @throws BuildException if invalid parameters found
      */
     private void validate() throws BuildException {
         if (null == destFile) {
             final String message = "Destfile attribute not specified.";
             throw new BuildException(message);
         }
         if (destFile.exists() && !destFile.isFile()) {
             final String message = destFile + " is not a file.";
             throw new BuildException(message);
         }
     }
 
     /**
      * Add any extra attributes to the manifest.
      *
      * @param attributes the manifest section to write
      *        attributes to
      */
     private void appendExtraAttributes(final Attributes attributes) {
         final Iterator iterator = extraAttributes.iterator();
         while (iterator.hasNext()) {
             final ExtraAttribute attribute =
                 (ExtraAttribute) iterator.next();
             attributes.putValue(attribute.getName(),
                                  attribute.getValue());
         }
     }
 
     /**
      * Write out manifest to destfile.
      *
      * @param manifest the manifest
      * @throws IOException if error writing file
      */
     private void writeManifest(final Manifest manifest)
         throws IOException {
         FileOutputStream output = null;
         try {
             output = new FileOutputStream(destFile);
             manifest.write(output);
             output.flush();
         } finally {
             if (null != output) {
                 try {
                     output.close();
                 } catch (IOException e) {
                     // ignore
                 }
             }
         }
     }
 
     /**
      * Append specified extensions to specified attributes.
      * Use the extensionKey to list the extensions, usually "Extension-List:"
      * for required dependencies and "Optional-Extension-List:" for optional
      * dependencies. NOTE: "Optional" dependencies are not part of the
      * specification.
      *
      * @param attributes the attributes to add extensions to
      * @param extensions the list of extensions
      * @throws BuildException if an error occurs
      */
     private void appendLibraryList(final Attributes attributes,
                                     final String listPrefix,
                                     final ArrayList extensions)
         throws BuildException {
         final int size = extensions.size();
         for (int i = 0; i < size; i++) {
             final Extension ext = (Extension) extensions.get(i);
             final String prefix = listPrefix + i + "-";
             Extension.addExtension(ext, prefix, attributes);
         }
     }
 
     /**
      * Append an attribute such as "Extension-List: lib0 lib1 lib2"
      * using specified prefix and counting up to specified size.
      * Also use specified extensionKey so that can generate list of
      * optional dependencies aswell.
      *
      * @param size the number of librarys to list
      * @param listPrefix the prefix for all librarys
      * @param attributes the attributes to add key-value to
      * @param extensionKey the key to use
      */
     private void appendExtensionList(final Attributes attributes,
                                       final Attributes.Name extensionKey,
                                       final String listPrefix,
                                       final int size) {
         final StringBuffer sb = new StringBuffer();
         for (int i = 0; i < size; i++) {
-            sb.append(listPrefix + i);
+            sb.append(listPrefix);
+            sb.append(i);
             sb.append(' ');
         }
 
         //add in something like
         //"Extension-List: javahelp java3d"
         attributes.put(extensionKey, sb.toString());
     }
 
     /**
      * Convert a list of ExtensionSet objects to extensions.
      *
      * @param extensionSets the list of ExtensionSets to add to list
      * @throws BuildException if an error occurs
      */
     private ArrayList toExtensions(final ArrayList extensionSets)
         throws BuildException {
         final ArrayList results = new ArrayList();
 
         final int size = extensionSets.size();
         for (int i = 0; i < size; i++) {
             final ExtensionSet set = (ExtensionSet) extensionSets.get(i);
             final Extension[] extensions = set.toExtensions(getProject());
             for (int j = 0; j < extensions.length; j++) {
                 results.add(extensions[ j ]);
             }
         }
 
         return results;
     }
 }
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/jsp/compilers/DefaultJspCompilerAdapter.java b/src/main/org/apache/tools/ant/taskdefs/optional/jsp/compilers/DefaultJspCompilerAdapter.java
index ab8c06a2b..07487008b 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/jsp/compilers/DefaultJspCompilerAdapter.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/jsp/compilers/DefaultJspCompilerAdapter.java
@@ -1,145 +1,147 @@
 /*
- * Copyright  2001-2005 The Apache Software Foundation
+ * Copyright  2001-2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.optional.jsp.compilers;
 
 import java.io.File;
 import java.util.Enumeration;
 import java.util.Vector;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
 import org.apache.tools.ant.types.CommandlineJava;
 
 /**
  * This is the default implementation for the JspCompilerAdapter interface.
  * This is currently very light on the ground since only one compiler type is
  * supported.
  *
  */
 public abstract class DefaultJspCompilerAdapter
     implements JspCompilerAdapter {
 
     private static String lSep = System.getProperty("line.separator");
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param jspc the compiler task for logging
      * @param compileList the list of files to compile
      * @param cmd the command line used
      */
     protected void logAndAddFilesToCompile(JspC jspc,
                                            Vector compileList,
                                            CommandlineJava cmd) {
         jspc.log("Compilation " + cmd.describeJavaCommand(),
                  Project.MSG_VERBOSE);
 
         StringBuffer niceSourceList = new StringBuffer("File");
         if (compileList.size() != 1) {
             niceSourceList.append("s");
         }
         niceSourceList.append(" to be compiled:");
 
         niceSourceList.append(lSep);
 
         Enumeration e = compileList.elements();
         while (e.hasMoreElements()) {
             String arg = (String) e.nextElement();
             cmd.createArgument().setValue(arg);
-            niceSourceList.append("    " + arg + lSep);
+            niceSourceList.append("    ");
+            niceSourceList.append(arg);
+            niceSourceList.append(lSep);
         }
 
         jspc.log(niceSourceList.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * our owner
      */
     protected JspC owner;
 
     /**
      * set the owner
      * @param owner the owner JspC compiler
      */
     public void setJspc(JspC owner) {
         this.owner = owner;
     }
 
     /** get the owner
      * @return the owner; should never be null
      */
     public JspC getJspc() {
         return owner;
     }
 
 
     /**
      *  add an argument oneple to the argument list, if the value aint null
      * @param cmd the command line
      * @param  argument  The argument
      */
     protected void addArg(CommandlineJava cmd, String argument) {
         if (argument != null && argument.length() != 0) {
            cmd.createArgument().setValue(argument);
         }
     }
 
 
     /**
      *  add an argument tuple to the argument list, if the value aint null
      * @param cmd the command line
      * @param  argument  The argument
      * @param  value     the parameter
      */
     protected void addArg(CommandlineJava cmd, String argument, String value) {
         if (value != null) {
             cmd.createArgument().setValue(argument);
             cmd.createArgument().setValue(value);
         }
     }
 
     /**
      *  add an argument tuple to the arg list, if the file parameter aint null
      * @param cmd the command line
      * @param  argument  The argument
      * @param  file     the parameter
      */
     protected void addArg(CommandlineJava cmd, String argument, File file) {
         if (file != null) {
             cmd.createArgument().setValue(argument);
             cmd.createArgument().setFile(file);
         }
     }
 
     /**
      * ask if compiler can sort out its own dependencies
      * @return true if the compiler wants to do its own
      * depends
      */
     public boolean implementsOwnDependencyChecking() {
         return false;
     }
 
     /**
      * get our project
      * @return owner project data
      */
     public Project getProject() {
         return getJspc().getProject();
     }
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
index ac3a895e5..c31926834 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
@@ -452,1224 +452,1224 @@ public class JUnitTask extends Task {
      * @param sysp set of properties to be added
      * @since Ant 1.6
      */
     public void addSyspropertyset(PropertySet sysp) {
         getCommandline().addSyspropertyset(sysp);
     }
 
     /**
      * Adds path to classpath used for tests.
      *
      * @return reference to the classpath in the embedded java command line
      * @since Ant 1.2
      */
     public Path createClasspath() {
         return getCommandline().createClasspath(getProject()).createPath();
     }
 
     /**
      * Adds a path to the bootclasspath.
      * @return reference to the bootclasspath in the embedded java command line
      * @since Ant 1.6
      */
     public Path createBootclasspath() {
         return getCommandline().createBootclasspath(getProject()).createPath();
     }
 
     /**
      * Adds an environment variable; used when forking.
      *
      * <p>Will be ignored if we are not forking a new VM.</p>
      * @param var environment variable to be added
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
      * @param newenv boolean indicating if setting a new environment is wished
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
      * @param fe formatter element
      * @since Ant 1.2
      */
     public void addFormatter(FormatterElement fe) {
         formatters.addElement(fe);
     }
 
     /**
      * If true, include ant.jar, optional.jar and junit.jar in the forked VM.
      *
      * @param b include ant run time yes or no
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
      * @param showOutput if true, send output to Ant's logging system too
      * @since Ant 1.5
      */
     public void setShowOutput(boolean showOutput) {
         this.showOutput = showOutput;
     }
 
     /**
      * Assertions to enable in this program (if fork=true)
      * @since Ant 1.6
      * @param asserts assertion set
      */
     public void addAssertions(Assertions asserts) {
         if (getCommandline().getAssertions() != null) {
             throw new BuildException("Only one assertion declaration is allowed");
         }
         getCommandline().setAssertions(asserts);
     }
 
     /**
      * Sets the permissions for the application run inside the same JVM.
      * @since Ant 1.6
      * @return .
      */
     public Permissions createPermissions() {
         if (perm == null) {
             perm = new Permissions();
         }
         return perm;
     }
 
     /**
      * If set, system properties will be copied to the cloned VM - as
      * well as the bootclasspath unless you have explicitly specified
      * a bootclaspath.
      *
      * <p>Doesn't have any effect unless fork is true.</p>
      *
      * @since Ant 1.7
      */
     public void setCloneVm(boolean cloneVm) {
         getCommandline().setCloneVm(cloneVm);
     }
 
     /**
      * Creates a new JUnitRunner and enables fork of a new Java VM.
      *
      * @throws Exception under ??? circumstances
      * @since Ant 1.2
      */
     public JUnitTask() throws Exception {
         getCommandline()
             .setClassname("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner");
     }
 
     /**
      * Where Ant should place temporary files.
      *
      * @param tmpDir location where temporary files should go to
      * @since Ant 1.6
      */
     public void setTempdir(File tmpDir) {
         if (tmpDir != null) {
             if (!tmpDir.exists() || !tmpDir.isDirectory()) {
                 throw new BuildException(tmpDir.toString()
                                          + " is not a valid temp directory");
             }
         }
         this.tmpDir = tmpDir;
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
         splitJunit = !addClasspathEntry("/junit/framework/TestCase.class");
         addClasspathEntry("/org/apache/tools/ant/launch/AntMain.class");
         addClasspathEntry("/org/apache/tools/ant/Task.class");
         addClasspathEntry("/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.class");
     }
 
     private static JUnitTaskMirror createMirror(JUnitTask task, ClassLoader loader) {
         try {
             loader.loadClass("junit.framework.Test"); // sanity check
         } catch (ClassNotFoundException e) {
             throw new BuildException(
                     "The <classpath> for <junit> must include junit.jar if not in Ant's own classpath",
                     e, task.getLocation());
         }
         try {
             Class c = loader.loadClass(JUnitTaskMirror.class.getName() + "Impl");
             if (c.getClassLoader() != loader) {
                 throw new BuildException("Overdelegating loader", task.getLocation());
             }
             Constructor cons = c.getConstructor(new Class[] {JUnitTask.class});
             return (JUnitTaskMirror) cons.newInstance(new Object[] {task});
         } catch (Exception e) {
             throw new BuildException(e, task.getLocation());
         }
     }
 
     private final class SplitLoader extends AntClassLoader {
 
         public SplitLoader(ClassLoader parent, Path path) {
             super(parent, getProject(), path, true);
         }
 
         // forceLoadClass is not convenient here since it would not
         // properly deal with inner classes of these classes.
         protected synchronized Class loadClass(String classname, boolean resolve)
         throws ClassNotFoundException {
             Class theClass = findLoadedClass(classname);
             if (theClass != null) {
                 return theClass;
             }
             if (isSplit(classname)) {
                 theClass = findClass(classname);
                 if (resolve) {
                     resolveClass(theClass);
                 }
                 return theClass;
             } else {
                 return super.loadClass(classname, resolve);
             }
         }
 
         private final String[] SPLIT_CLASSES = {
             "BriefJUnitResultFormatter",
             "JUnitResultFormatter",
             "JUnitTaskMirrorImpl",
             "JUnitTestRunner",
             "JUnitVersionHelper",
             "OutErrSummaryJUnitResultFormatter",
             "PlainJUnitResultFormatter",
             "SummaryJUnitResultFormatter",
             "XMLJUnitResultFormatter",
         };
 
         private boolean isSplit(String classname) {
             String simplename = classname.substring(classname.lastIndexOf('.') + 1);
             for (int i = 0; i < SPLIT_CLASSES.length; i++) {
                 if (simplename.equals(SPLIT_CLASSES[i]) || simplename.startsWith(SPLIT_CLASSES[i] + '$')) {
                     return true;
                 }
             }
             return false;
         }
 
     }
     
     /**
      * Runs the testcase.
      *
      * @throws BuildException in case of test failures or errors
      * @since Ant 1.2
      */
     public void execute() throws BuildException {
         ClassLoader myLoader = JUnitTask.class.getClassLoader();
         ClassLoader mirrorLoader;
         if (splitJunit) {
             Path path = new Path(getProject());
             path.add(antRuntimeClasses);
             path.add(getCommandline().getClasspath());
             mirrorLoader = new SplitLoader(myLoader, path);
         } else {
             mirrorLoader = myLoader;
         }
         delegate = createMirror(this, mirrorLoader);
 
         List testLists = new ArrayList();
 
         boolean forkPerTest = forkMode.getValue().equals(ForkMode.PER_TEST);
         if (forkPerTest || forkMode.getValue().equals(ForkMode.ONCE)) {
             testLists.addAll(executeOrQueue(getIndividualTests(),
                                             forkPerTest));
         } else { /* forkMode.getValue().equals(ForkMode.PER_BATCH) */
             final int count = batchTests.size();
             for (int i = 0; i < count; i++) {
                 BatchTest batchtest = (BatchTest) batchTests.elementAt(i);
                 testLists.addAll(executeOrQueue(batchtest.elements(), false));
             }
             testLists.addAll(executeOrQueue(tests.elements(), forkPerTest));
         }
 
         try {
             Iterator iter = testLists.iterator();
             while (iter.hasNext()) {
                 List l = (List) iter.next();
                 if (l.size() == 1) {
                     execute((JUnitTest) l.get(0));
                 } else {
                     execute(l);
                 }
             }
         } finally {
             deleteClassLoader();
             if (mirrorLoader instanceof SplitLoader) {
                 ((SplitLoader) mirrorLoader).cleanup();
             }
             delegate = null;
         }
     }
 
     /**
      * Run the tests.
      * @param arg one JunitTest
      * @throws BuildException in case of test failures or errors
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
         TestResultHolder result = null;
         if (!test.getFork()) {
             result = executeInVM(test);
         } else {
             ExecuteWatchdog watchdog = createWatchdog();
             result = executeAsForked(test, watchdog, null);
             // null watchdog means no timeout, you'd better not check with null
         }
         actOnTestResult(result, test, "Test " + test.getName());
     }
 
     /**
      * Execute a list of tests in a single forked Java VM.
      */
     protected void execute(List tests) throws BuildException {
         JUnitTest test = null;
         // Create a temporary file to pass the test cases to run to
         // the runner (one test case per line)
         File casesFile = createTempPropertiesFile("junittestcases");
         PrintWriter writer = null;
         try {
             writer =
                 new PrintWriter(new BufferedWriter(new FileWriter(casesFile)));
             Iterator iter = tests.iterator();
             while (iter.hasNext()) {
                 test = (JUnitTest) iter.next();
                 writer.print(test.getName());
                 if (test.getTodir() == null) {
                     writer.print("," + getProject().resolveFile("."));
                 } else {
                     writer.print("," + test.getTodir());
                 }
 
                 if (test.getOutfile() == null) {
                     writer.println("," + "TEST-" + test.getName());
                 } else {
                     writer.println("," + test.getOutfile());
                 }
             }
             writer.flush();
             writer.close();
             writer = null;
 
             // execute the test and get the return code
             ExecuteWatchdog watchdog = createWatchdog();
             TestResultHolder result =
                 executeAsForked(test, watchdog, casesFile);
             actOnTestResult(result, test, "Tests");
         } catch (IOException e) {
             log(e.toString(), Project.MSG_ERR);
             throw new BuildException(e);
         } finally {
             if (writer != null) {
                 writer.close();
             }
 
             try {
                 casesFile.delete();
             } catch (Exception e) {
                 log(e.toString(), Project.MSG_ERR);
             }
         }
     }
 
     /**
      * Execute a testcase by forking a new JVM. The command will block
      * until it finishes. To know if the process was destroyed or not
      * or whether the forked Java VM exited abnormally, use the
      * attributes of the returned holder object.
      * @param  test       the testcase to execute.
      * @param  watchdog   the watchdog in charge of cancelling the test if it
      * exceeds a certain amount of time. Can be <tt>null</tt>, in this case
      * the test could probably hang forever.
      * @param casesFile list of test cases to execute. Can be <tt>null</tt>,
      * in this case only one test is executed.
      * @throws BuildException in case of error creating a temporary property file,
      * or if the junit process can not be forked
      */
     private TestResultHolder executeAsForked(JUnitTest test,
                                              ExecuteWatchdog watchdog,
                                              File casesFile)
         throws BuildException {
 
         if (perm != null) {
             log("Permissions ignored when running in forked mode!",
                 Project.MSG_WARN);
         }
 
         CommandlineJava cmd = null;
         try {
             cmd = (CommandlineJava) (getCommandline().clone());
         } catch (CloneNotSupportedException e) {
             throw new BuildException("This shouldn't happen", e, getLocation());
         }
         cmd.setClassname("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner");
         if (casesFile == null) {
             cmd.createArgument().setValue(test.getName());
         } else {
             log("Running multiple tests in the same VM", Project.MSG_VERBOSE);
             cmd.createArgument().setValue("testsfile=" + casesFile);
         }
 
         cmd.createArgument().setValue("filtertrace=" + test.getFiltertrace());
         cmd.createArgument().setValue("haltOnError=" + test.getHaltonerror());
         cmd.createArgument().setValue("haltOnFailure="
                                       + test.getHaltonfailure());
         if (includeAntRuntime) {
             Vector v = Execute.getProcEnvironment();
             Enumeration e = v.elements();
             while (e.hasMoreElements()) {
                 String s = (String) e.nextElement();
                 if (s.startsWith("CLASSPATH=")) {
                     cmd.createClasspath(getProject()).createPath()
                         .append(new Path(getProject(),
                                          s.substring(10 // "CLASSPATH=".length()
                                                      )));
                 }
             }
             log("Implicitly adding " + antRuntimeClasses + " to CLASSPATH",
                 Project.MSG_VERBOSE);
             cmd.createClasspath(getProject()).createPath()
                 .append(antRuntimeClasses);
         }
 
         if (summary) {
             String prefix = "";
             if ("withoutanderr".equalsIgnoreCase(summaryValue)) {
                 prefix = "OutErr";
             }
             cmd.createArgument()
                 .setValue("formatter"
                           + "=org.apache.tools.ant.taskdefs.optional.junit."
                           + prefix + "SummaryJUnitResultFormatter");
         }
 
         cmd.createArgument().setValue("showoutput="
                                       + String.valueOf(showOutput));
         cmd.createArgument().setValue("logtestlistenerevents=true"); // #31885
 
         StringBuffer formatterArg = new StringBuffer(STRING_BUFFER_SIZE);
         final FormatterElement[] feArray = mergeFormatters(test);
         for (int i = 0; i < feArray.length; i++) {
             FormatterElement fe = feArray[i];
             if (fe.shouldUse(this)) {
                 formatterArg.append("formatter=");
                 formatterArg.append(fe.getClassname());
                 File outFile = getOutput(fe, test);
                 if (outFile != null) {
                     formatterArg.append(",");
                     formatterArg.append(outFile);
                 }
                 cmd.createArgument().setValue(formatterArg.toString());
                 formatterArg = new StringBuffer();
             }
         }
 
         File vmWatcher = createTempPropertiesFile("junitvmwatcher");
         formatterArg.append("nocrashfile=");
         formatterArg.append(vmWatcher);
         cmd.createArgument().setValue(formatterArg.toString());
 
         File propsFile = createTempPropertiesFile("junit");
         cmd.createArgument().setValue("propsfile="
                                       + propsFile.getAbsolutePath());
         Hashtable p = getProject().getProperties();
         Properties props = new Properties();
         for (Enumeration e = p.keys(); e.hasMoreElements();) {
             Object key = e.nextElement();
             props.put(key, p.get(key));
         }
         try {
             FileOutputStream outstream = new FileOutputStream(propsFile);
             props.store(outstream, "Ant JUnitTask generated properties file");
             outstream.close();
         } catch (java.io.IOException e) {
             propsFile.delete();
             throw new BuildException("Error creating temporary properties "
                                      + "file.", e, getLocation());
         }
 
         Execute execute = new Execute(new JUnitLogStreamHandler(this,
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
         TestResultHolder result = new TestResultHolder();
         try {
             result.exitCode = execute.execute();
         } catch (IOException e) {
             throw new BuildException("Process fork failed.", e, getLocation());
         } finally {
             if (watchdog != null && watchdog.killedProcess()) {
                 result.timedOut = true;
                 logTimeout(feArray, test);
             } else if (vmWatcher.length() == 0) {
                 result.crashed = true;
                 logVmCrash(feArray, test);
             }
             vmWatcher.delete();
 
             if (!propsFile.delete()) {
                 throw new BuildException("Could not delete temporary "
                                          + "properties file.");
             }
         }
 
         return result;
     }
 
     /**
      * Create a temporary file to pass the properties to a new process.
      * Will auto-delete on (graceful) exit.
      * The file will be in the project basedir unless tmpDir declares
      * something else.
      * @param prefix
      * @return created file
      */
     private File createTempPropertiesFile(String prefix) {
         File propsFile =
             FILE_UTILS.createTempFile(prefix, ".properties",
                 tmpDir != null ? tmpDir : getProject().getBaseDir());
         propsFile.deleteOnExit();
         return propsFile;
     }
 
 
     /**
      * Pass output sent to System.out to the TestRunner so it can
      * collect ot for the formatters.
      *
      * @param output output coming from System.out
      * @since Ant 1.5
      */
     protected void handleOutput(String output) {
         if (output.startsWith(TESTLISTENER_PREFIX))
             log(output, Project.MSG_VERBOSE);
         else if (runner != null) {
             runner.handleOutput(output);
             if (showOutput) {
                 super.handleOutput(output);
             }
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6
      */
     protected int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (runner != null) {
             return runner.handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
 
     /**
      * Pass output sent to System.out to the TestRunner so it can
      * collect ot for the formatters.
      *
      * @param output output coming from System.out
      * @since Ant 1.5.2
      */
     protected void handleFlush(String output) {
         if (runner != null) {
             runner.handleFlush(output);
             if (showOutput) {
                 super.handleFlush(output);
             }
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the TestRunner so it can
      * collect it for the formatters.
      *
      * @param output output coming from System.err
      * @since Ant 1.5
      */
     public void handleErrorOutput(String output) {
         if (runner != null) {
             runner.handleErrorOutput(output);
             if (showOutput) {
                 super.handleErrorOutput(output);
             }
         } else {
             super.handleErrorOutput(output);
         }
     }
 
 
     /**
      * Pass output sent to System.err to the TestRunner so it can
      * collect it for the formatters.
      *
      * @param output coming from System.err
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String output) {
         if (runner != null) {
             runner.handleErrorFlush(output);
             if (showOutput) {
                 super.handleErrorFlush(output);
             }
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     // in VM is not very nice since it could probably hang the
     // whole build. IMHO this method should be avoided and it would be best
     // to remove it in future versions. TBD. (SBa)
 
     /**
      * Execute inside VM.
      * @param arg one JUnitTest
      * @throws BuildException under unspecified circumstances
      */
     private TestResultHolder executeInVM(JUnitTest arg) throws BuildException {
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
 
         if (getCommandline().getBootclasspath() != null) {
             log("bootclasspath is ignored if running in the same VM.",
                 Project.MSG_WARN);
         }
 
         CommandlineJava.SysProperties sysProperties =
                 getCommandline().getSystemProperties();
         if (sysProperties != null) {
             sysProperties.setSystem();
         }
 
         try {
             log("Using System properties " + System.getProperties(),
                 Project.MSG_VERBOSE);
             if (splitJunit) {
                 classLoader = (AntClassLoader) delegate.getClass().getClassLoader();
             } else {
                 createClassLoader();
             }
             if (classLoader != null) {
                 classLoader.setThreadContextLoader();
             }
             runner = delegate.newJUnitTestRunner(test, test.getHaltonerror(),
                                          test.getFiltertrace(),
                                          test.getHaltonfailure(), false,
                                          true, classLoader);
             if (summary) {
 
                 JUnitTaskMirror.SummaryJUnitResultFormatterMirror f =
                     delegate.newSummaryJUnitResultFormatter();
                 f.setWithOutAndErr("withoutanderr"
                                    .equalsIgnoreCase(summaryValue));
                 f.setOutput(getDefaultOutput());
                 runner.addFormatter(f);
             }
 
             runner.setPermissions(perm);
 
             final FormatterElement[] feArray = mergeFormatters(test);
             for (int i = 0; i < feArray.length; i++) {
                 FormatterElement fe = feArray[i];
                 if (fe.shouldUse(this)) {
                     File outFile = getOutput(fe, test);
                     if (outFile != null) {
                         fe.setOutfile(outFile);
                     } else {
                         fe.setOutput(getDefaultOutput());
                     }
                     runner.addFormatter(fe.createFormatter(classLoader));
                 }
             }
 
             runner.run();
             TestResultHolder result = new TestResultHolder();
             result.exitCode = runner.getRetCode();
             return result;
         } finally {
             if (sysProperties != null) {
                 sysProperties.restoreSystem();
             }
             if (classLoader != null) {
                 classLoader.resetThreadContextLoader();
             }
         }
     }
 
     /**
      * @return <tt>null</tt> if there is a timeout value, otherwise the
      * watchdog instance.
      *
      * @throws BuildException under unspecified circumstances
      * @since Ant 1.2
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         if (timeout == null) {
             return null;
         }
         return new ExecuteWatchdog((long) timeout.intValue());
     }
 
     /**
      * Get the default output for a formatter.
      *
      * @return default output stream for a formatter
      * @since Ant 1.3
      */
     protected OutputStream getDefaultOutput() {
         return new LogOutputStream(this, Project.MSG_INFO);
     }
 
     /**
      * Merge all individual tests from the batchtest with all individual tests
      * and return an enumeration over all <tt>JUnitTest</tt>.
      *
      * @return enumeration over individual tests
      * @since Ant 1.3
      */
     protected Enumeration getIndividualTests() {
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
      * return an enumeration listing each test, then each batchtest
      * @return enumeration
      * @since Ant 1.3
      */
     protected Enumeration allTests() {
         Enumeration[] enums = {tests.elements(), batchTests.elements()};
         return Enumerations.fromCompound(enums);
     }
 
     /**
      * @param test junit test
      * @return array of FormatterElement
      * @since Ant 1.3
      */
     private FormatterElement[] mergeFormatters(JUnitTest test) {
         Vector feVector = (Vector) formatters.clone();
         test.addFormattersTo(feVector);
         FormatterElement[] feArray = new FormatterElement[feVector.size()];
         feVector.copyInto(feArray);
         return feArray;
     }
 
     /**
      * If the formatter sends output to a file, return that file.
      * null otherwise.
      * @param fe  formatter element
      * @param test one JUnit test
      * @return file reference
      * @since Ant 1.3
      */
     protected File getOutput(FormatterElement fe, JUnitTest test) {
         if (fe.getUseFile()) {
             String base = test.getOutfile();
             if (base == null) {
                 base = JUnitTaskMirror.JUnitTestRunnerMirror.IGNORED_FILE_NAME;
             }
             String filename = base + fe.getExtension();
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
      * @param resource resource that one wants to lookup
      * @return true if something was in fact added
      * @since Ant 1.4
      */
     protected boolean addClasspathEntry(String resource) {
         /*
          * pre Ant 1.6 this method used to call getClass().getResource
          * while Ant 1.6 will call ClassLoader.getResource().
          *
          * The difference is that Class.getResource expects a leading
          * slash for "absolute" resources and will strip it before
          * delegating to ClassLoader.getResource - so we now have to
          * emulate Class's behavior.
          */
         if (resource.startsWith("/")) {
             resource = resource.substring(1);
         } else {
             resource = "org/apache/tools/ant/taskdefs/optional/junit/"
                 + resource;
         }
 
         File f = LoaderUtils.getResourceSource(getClass().getClassLoader(),
                                                resource);
         if (f != null) {
             log("Found " + f.getAbsolutePath(), Project.MSG_DEBUG);
             antRuntimeClasses.createPath().setLocation(f);
             return true;
         } else {
             log("Couldn\'t find " + resource, Project.MSG_DEBUG);
             return false;
         }
     }
 
     /**
      * Take care that some output is produced in report files if the
      * watchdog kills the test.
      *
      * @since Ant 1.5.2
      */
 
     private void logTimeout(FormatterElement[] feArray, JUnitTest test) {
         logVmExit(feArray, test, "Timeout occurred");
     }
 
     /**
      * Take care that some output is produced in report files if the
      * forked machine exited before the test suite finished but the
      * reason is not a timeout.
      *
      * @since Ant 1.7
      */
     private void logVmCrash(FormatterElement[] feArray, JUnitTest test) {
         logVmExit(feArray, test, "forked Java VM exited abnormally");
     }
 
     /**
      * Take care that some output is produced in report files if the
      * forked machine existed before the test suite finished
      *
      * @since Ant 1.7
      */
     private void logVmExit(FormatterElement[] feArray, JUnitTest test,
                            String message) {
         createClassLoader();
         test.setCounts(1, 0, 1);
         test.setProperties(getProject().getProperties());
         for (int i = 0; i < feArray.length; i++) {
             FormatterElement fe = feArray[i];
             File outFile = getOutput(fe, test);
             JUnitTaskMirror.JUnitResultFormatterMirror formatter = fe.createFormatter(classLoader);
             if (outFile != null && formatter != null) {
                 try {
                     OutputStream out = new FileOutputStream(outFile);
                     addVmExit(test, formatter, out, message);
                 } catch (IOException e) {
                     // ignore
                 }
             }
         }
         if (summary) {
             JUnitTaskMirror.SummaryJUnitResultFormatterMirror f = delegate.newSummaryJUnitResultFormatter();
             f.setWithOutAndErr("withoutanderr".equalsIgnoreCase(summaryValue));
             addVmExit(test, f, getDefaultOutput(), message);
         }
     }
 
     /**
      * Adds the actual error message to the formatter.
      * Only used from the logVmExit method.
      * @since Ant 1.7
      */
     private void addVmExit(JUnitTest test, JUnitTaskMirror.JUnitResultFormatterMirror formatter,
                            OutputStream out, final String message) {
         delegate.addVmExit(test, formatter, out, message);
     }
 
     /**
      * Creates and configures an AntClassLoader instance from the
      * nested classpath element.
      *
      * @since Ant 1.6
      */
     private void createClassLoader() {
         Path userClasspath = getCommandline().getClasspath();
         if (userClasspath != null) {
             if (reloading || classLoader == null) {
                 deleteClassLoader();
                 Path classpath = (Path) userClasspath.clone();
                 if (includeAntRuntime) {
                     log("Implicitly adding " + antRuntimeClasses
                         + " to CLASSPATH", Project.MSG_VERBOSE);
                     classpath.append(antRuntimeClasses);
                 }
                 classLoader = getProject().createClassLoader(classpath);
                 if (getClass().getClassLoader() != null
                     && getClass().getClassLoader() != Project.class.getClassLoader()) {
                     classLoader.setParent(getClass().getClassLoader());
                 }
                 classLoader.setParentFirst(false);
                 classLoader.addJavaLibraries();
                 log("Using CLASSPATH " + classLoader.getClasspath(),
                     Project.MSG_VERBOSE);
                 // make sure the test will be accepted as a TestCase
                 classLoader.addSystemPackageRoot("junit");
                 // will cause trouble in JDK 1.1 if omitted
                 classLoader.addSystemPackageRoot("org.apache.tools.ant");
             }
         }
     }
     
     /**
      * Removes a classloader if needed.
      * @since Ant 1.7
      */
     private void deleteClassLoader()
     {
         if (classLoader != null) {
             classLoader.cleanup();
             classLoader = null;
         }
     }
 
     /**
      * @since Ant 1.6.2
      */
     protected CommandlineJava getCommandline() {
         if (commandline == null) {
             commandline = new CommandlineJava();
         }
         return commandline;
     }
 
     /**
      * Forked test support
      * @since Ant 1.6.2
      */
-    private final class ForkedTestConfiguration {
+    private final static class ForkedTestConfiguration {
         private boolean filterTrace;
         private boolean haltOnError;
         private boolean haltOnFailure;
         private String errorProperty;
         private String failureProperty;
 
         /**
          * constructor for forked test configuration
          * @param filterTrace
          * @param haltOnError
          * @param haltOnFailure
          * @param errorProperty
          * @param failureProperty
          */
         ForkedTestConfiguration(boolean filterTrace, boolean haltOnError,
                                 boolean haltOnFailure, String errorProperty,
                                 String failureProperty) {
             this.filterTrace = filterTrace;
             this.haltOnError = haltOnError;
             this.haltOnFailure = haltOnFailure;
             this.errorProperty = errorProperty;
             this.failureProperty = failureProperty;
         }
 
         /**
          * configure from a test; sets member variables to attributes of the test
          * @param test
          */
         ForkedTestConfiguration(JUnitTest test) {
             this(test.getFiltertrace(),
                     test.getHaltonerror(),
                     test.getHaltonfailure(),
                     test.getErrorProperty(),
                     test.getFailureProperty());
         }
 
         /**
          * equality test checks all the member variables
          * @param other
          * @return true if everything is equal
          */
         public boolean equals(Object other) {
             if (other == null
                 || other.getClass() != ForkedTestConfiguration.class) {
                 return false;
             }
             ForkedTestConfiguration o = (ForkedTestConfiguration) other;
             return filterTrace == o.filterTrace
                 && haltOnError == o.haltOnError
                 && haltOnFailure == o.haltOnFailure
                 && ((errorProperty == null && o.errorProperty == null)
                     ||
                     (errorProperty != null
                      && errorProperty.equals(o.errorProperty)))
                 && ((failureProperty == null && o.failureProperty == null)
                     ||
                     (failureProperty != null
                      && failureProperty.equals(o.failureProperty)));
         }
 
         /**
          * hashcode is based only on the boolean members, and returns a value
          * in the range 0-7.
          * @return hash code value
          */
         public int hashCode() {
             return (filterTrace ? 1 : 0)
                 + (haltOnError ? 2 : 0)
                 + (haltOnFailure ? 4 : 0);
         }
     }
 
     /**
      * These are the different forking options
      * @since 1.6.2
      */
     public static final class ForkMode extends EnumeratedAttribute {
 
         /**
          * fork once only
          */
         public static final String ONCE = "once";
         /**
          * fork once per test class
          */
         public static final String PER_TEST = "perTest";
         /**
          * fork once per batch of tests
          */
         public static final String PER_BATCH = "perBatch";
 
         public ForkMode() {
             super();
         }
 
         public ForkMode(String value) {
             super();
             setValue(value);
         }
 
         public String[] getValues() {
             return new String[] {ONCE, PER_TEST, PER_BATCH};
         }
     }
 
     /**
      * Executes all tests that don't need to be forked (or all tests
      * if the runIndividual argument is true.  Returns a collection of
      * lists of tests that share the same VM configuration and haven't
      * been executed yet.
      *
      * @since 1.6.2
      */
     protected Collection executeOrQueue(Enumeration testList,
                                         boolean runIndividual) {
         Map testConfigurations = new HashMap();
         while (testList.hasMoreElements()) {
             JUnitTest test = (JUnitTest) testList.nextElement();
             if (test.shouldRun(getProject())) {
                 if (runIndividual || !test.getFork()) {
                     execute(test);
                 } else {
                     ForkedTestConfiguration c =
                         new ForkedTestConfiguration(test);
                     List l = (List) testConfigurations.get(c);
                     if (l == null) {
                         l = new ArrayList();
                         testConfigurations.put(c, l);
                     }
                     l.add(test);
                 }
             }
         }
         return testConfigurations.values();
     }
 
     /**
      * Logs information about failed tests, potentially stops
      * processing (by throwing a BuildException) if a failure/error
      * occurred or sets a property.
      *
      * @since Ant 1.6.2
      */
     protected void actOnTestResult(int exitValue, boolean wasKilled,
                                    JUnitTest test, String name) {
         TestResultHolder t = new TestResultHolder();
         t.exitCode = exitValue;
         t.timedOut = wasKilled;
         actOnTestResult(t, test, name);
     }
 
     /**
      * Logs information about failed tests, potentially stops
      * processing (by throwing a BuildException) if a failure/error
      * occurred or sets a property.
      *
      * @since Ant 1.7
      */
     protected void actOnTestResult(TestResultHolder result, JUnitTest test,
                                    String name) {
         // if there is an error/failure and that it should halt, stop
         // everything otherwise just log a statement
         boolean fatal = result.timedOut || result.crashed;
         boolean errorOccurredHere =
             result.exitCode == JUnitTaskMirror.JUnitTestRunnerMirror.ERRORS || fatal;
         boolean failureOccurredHere =
             result.exitCode != JUnitTaskMirror.JUnitTestRunnerMirror.SUCCESS || fatal;
         if (errorOccurredHere || failureOccurredHere) {
             if ((errorOccurredHere && test.getHaltonerror())
                 || (failureOccurredHere && test.getHaltonfailure())) {
                 throw new BuildException(name + " failed"
                     + (result.timedOut ? " (timeout)" : "")
                     + (result.crashed ? " (crashed)" : ""), getLocation());
             } else {
                 log(name + " FAILED"
                     + (result.timedOut ? " (timeout)" : "")
                     + (result.crashed ? " (crashed)" : ""), Project.MSG_ERR);
                 if (errorOccurredHere && test.getErrorProperty() != null) {
                     getProject().setNewProperty(test.getErrorProperty(), "true");
                 }
                 if (failureOccurredHere && test.getFailureProperty() != null) {
                     getProject().setNewProperty(test.getFailureProperty(), "true");
                 }
             }
         }
     }
 
     protected class TestResultHolder {
         public int exitCode = JUnitTaskMirror.JUnitTestRunnerMirror.ERRORS;
         public boolean timedOut = false;
         public boolean crashed = false;
     }
 
     /**
      * @since Ant 1.7
      */
     protected static class JUnitLogOutputStream extends LogOutputStream {
         private Task task; // local copy since LogOutputStream.task is private
         
         public JUnitLogOutputStream(Task task, int level) {
             super(task, level);
             this.task = task;
         }
         
         protected void processLine(String line, int level) {
             if (line.startsWith(TESTLISTENER_PREFIX)) {
                 task.log(line, Project.MSG_VERBOSE);
             } else {
                 super.processLine(line, level);
             }
         }
     }
 
     /**
      * @since Ant 1.7
      */
     protected static class JUnitLogStreamHandler extends PumpStreamHandler {
         public JUnitLogStreamHandler(Task task, int outlevel, int errlevel) {
             super(new JUnitLogOutputStream(task, outlevel),
                   new LogOutputStream(task, errlevel));
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/vss/MSVSS.java b/src/main/org/apache/tools/ant/taskdefs/optional/vss/MSVSS.java
index 613d81c22..2d2709520 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/vss/MSVSS.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/vss/MSVSS.java
@@ -1,691 +1,688 @@
 /*
  * Copyright  2000-2004, 2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.optional.vss;
 
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import java.io.File;
 import java.io.IOException;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * A base class for creating tasks for executing commands on Visual SourceSafe.
  * <p>
  * The class extends the 'exec' task as it operates by executing the ss.exe program
  * supplied with SourceSafe. By default the task expects ss.exe to be in the path,
  * you can override this be specifying the ssdir attribute.
  * </p>
  * <p>
  * This class provides set and get methods for 'login' and 'vsspath' attributes. It
  * also contains constants for the flags that can be passed to SS.
  * </p>
  *
  */
 public abstract class MSVSS extends Task implements MSVSSConstants {
 
     private String ssDir = null;
     private String vssLogin = null;
     private String vssPath = null;
     private String serverPath = null;
 
     /**  Version */
     private String version = null;
     /**  Date */
     private String date = null;
     /**  Label */
     private String label = null;
     /**  Auto response */
     private String autoResponse = null;
     /**  Local path */
     private String localPath = null;
     /**  Comment */
     private String comment = null;
     /**  From label */
     private String fromLabel = null;
     /**  To label */
     private String toLabel = null;
     /**  Output file name */
     private String outputFileName = null;
     /**  User */
     private String user = null;
     /**  From date */
     private String fromDate = null;
     /**  To date */
     private String toDate = null;
     /**  History style */
     private String style = null;
     /**  Quiet defaults to false */
     private boolean quiet = false;
     /**  Recursive defaults to false */
     private boolean recursive = false;
     /**  Writable defaults to false */
     private boolean writable = false;
     /**  Fail on error defaults to true */
     private boolean failOnError = true;
     /**  Get local copy for checkout defaults to true */
     private boolean getLocalCopy = true;
     /**  Number of days offset for History */
     private int numDays = Integer.MIN_VALUE;
     /**  Date format for History */
     private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
     /**  Timestamp for retreived files */
     private CurrentModUpdated timestamp = null;
     /**  Behaviour for writable files */
     private WritableFiles writableFiles = null;
 
     /**
      * Each sub-class must implemnt this method and return the constructed
      * command line to be executed. It is up to the sub-task to determine the
      * required attrubutes and their order.
      * @return    The Constructed command line.
      */
     abstract Commandline buildCmdLine();
 
     /**
      * Directory where <code>ss.exe</code> resides.
      * By default the task expects it to be in the PATH.
      * @param  dir  The directory containing ss.exe.
      */
     public final void setSsdir(String dir) {
         this.ssDir = FileUtils.translatePath(dir);
     }
 
     /**
      * Login to use when accessing VSS, formatted as "username,password".
      * <p>
      * You can omit the password if your database is not password protected.
      * If you have a password and omit it, Ant will hang.
      * @param  vssLogin  The login string to use.
      */
     public final void setLogin(final String vssLogin) {
         this.vssLogin = vssLogin;
     }
 
     /**
      * SourceSafe path which specifies the project/file(s) you wish to perform
      * the action on.
      * <p>
      * A prefix of 'vss://' will be removed if specified.
      * @param  vssPath  The VSS project path.
      * @ant.attribute group="required"
      */
     public final void setVsspath(final String vssPath) {
         String projectPath;
         if (vssPath.startsWith("vss://")) { //$NON-NLS-1$
             projectPath = vssPath.substring(5);
         } else {
             projectPath = vssPath;
         }
 
         if (projectPath.startsWith(PROJECT_PREFIX)) {
             this.vssPath = projectPath;
         } else {
             this.vssPath = PROJECT_PREFIX + projectPath;
         }
     }
 
     /**
      * Directory where <code>srssafe.ini</code> resides.
      * @param  serverPath  The path to the VSS server.
      */
     public final void setServerpath(final String serverPath) {
         this.serverPath = serverPath;
     }
 
     /**
      * Indicates if the build should fail if the Sourcesafe command does. Defaults to true.
      * @param failOnError True if task should fail on any error.
      */
     public final void setFailOnError(final boolean failOnError) {
         this.failOnError = failOnError;
     }
 
     /**
      * Executes the task. <br>
      * Builds a command line to execute ss.exe and then calls Exec's run method
      * to execute the command line.
      * @throws BuildException if the command cannot execute.
      */
     public void execute() throws BuildException {
         int result = 0;
         Commandline commandLine = buildCmdLine();
         result = run(commandLine);
         if (Execute.isFailure(result) && getFailOnError()) {
             String msg = "Failed executing: " + formatCommandLine(commandLine)
                      + " With a return code of " + result;
             throw new BuildException(msg, getLocation());
         }
     }
 
     // Special setters for the sub-classes
 
     protected void setInternalComment(final String comment) {
         this.comment = comment;
     }
 
     protected void setInternalAutoResponse(final String autoResponse) {
         this.autoResponse = autoResponse;
     }
 
     protected void setInternalDate(final String date) {
         this.date = date;
     }
 
     protected void setInternalDateFormat(final DateFormat dateFormat) {
         this.dateFormat = dateFormat;
     }
 
     protected void setInternalFailOnError(final boolean failOnError) {
         this.failOnError = failOnError;
     }
 
     protected void setInternalFromDate(final String fromDate) {
         this.fromDate = fromDate;
     }
 
     protected void setInternalFromLabel(final String fromLabel) {
         this.fromLabel = fromLabel;
     }
 
     protected void setInternalLabel(final String label) {
         this.label = label;
     }
 
     protected void setInternalLocalPath(final String localPath) {
         this.localPath = localPath;
     }
 
     protected void setInternalNumDays(final int numDays) {
         this.numDays = numDays;
     }
 
     protected void setInternalOutputFilename(final String outputFileName) {
         this.outputFileName = outputFileName;
     }
 
     protected void setInternalQuiet(final boolean quiet) {
         this.quiet = quiet;
     }
 
     protected void setInternalRecursive(final boolean recursive) {
         this.recursive = recursive;
     }
 
     protected void setInternalStyle(final String style) {
         this.style = style;
     }
 
     protected void setInternalToDate(final String toDate) {
         this.toDate = toDate;
     }
 
     protected void setInternalToLabel(final String toLabel) {
         this.toLabel = toLabel;
     }
 
     protected void setInternalUser(final String user) {
         this.user = user;
     }
 
     protected void setInternalVersion(final String version) {
         this.version = version;
     }
 
     protected void setInternalWritable(final boolean writable) {
         this.writable = writable;
     }
 
     protected void setInternalFileTimeStamp(final CurrentModUpdated timestamp) {
         this.timestamp = timestamp;
     }
 
     protected void setInternalWritableFiles(final WritableFiles writableFiles) {
         this.writableFiles = writableFiles;
     }
 
     protected void setInternalGetLocalCopy(final boolean getLocalCopy) {
         this.getLocalCopy = getLocalCopy;
     }
 
     /**
      * Gets the sscommand string. "ss" or "c:\path\to\ss"
      * @return    The path to ss.exe or just ss if sscommand is not set.
      */
     protected String getSSCommand() {
         if (ssDir == null) {
             return SS_EXE;
         }
         return ssDir.endsWith(File.separator) ? ssDir + SS_EXE : ssDir
                  + File.separator + SS_EXE;
     }
 
     /**
      * Gets the vssserverpath string.
      * @return    null if vssserverpath is not set.
      */
     protected String getVsspath() {
         return vssPath;
     }
 
     /**
      * Gets the quiet string. -O-
      * @return An empty string if quiet is not set or is false.
      */
     protected String getQuiet() {
         return quiet ? FLAG_QUIET : "";
     }
 
     /**
      * Gets the recursive string. "-R"
      * @return An empty string if recursive is not set or is false.
      */
     protected String getRecursive() {
         return recursive ? FLAG_RECURSION : "";
     }
 
     /**
      * Gets the writable string. "-W"
      * @return An empty string if writable is not set or is false.
      */
     protected String getWritable() {
         return writable ? FLAG_WRITABLE : "";
     }
 
     /**
      * Gets the label string. "-Lbuild1"
      * Max label length is 32 chars
      * @return An empty string if label is not set.
      */
     protected String getLabel() {
         String shortLabel = "";
         if (label != null && label.length() > 0) {
                 shortLabel = FLAG_LABEL + getShortLabel();
         }
         return shortLabel;
     }
     /**
      * Return at most the 30 first chars of the label,
      * logging a warning message about the truncation
      * @return at most the 30 first chars of the label
      */
     private String getShortLabel() {
         String shortLabel;
         if (label !=  null && label.length() > 31) {
             shortLabel = this.label.substring(0, 30);
             log("Label is longer than 31 characters, truncated to: " + shortLabel,
                 Project.MSG_WARN);
         } else {
             shortLabel = label;
         }
         return shortLabel;
     }
     /**
      * Gets the style string. "-Lbuild1"
      * @return An empty string if label is not set.
      */
     protected String getStyle() {
         return style != null ? style : "";
     }
 
     /**
      * Gets the version string. Returns the first specified of version "-V1.0",
      * date "-Vd01.01.01", label "-Vlbuild1".
      * @return An empty string if a version, date and label are not set.
      */
     protected String getVersionDateLabel() {
         String versionDateLabel = "";
         if (version != null) {
             versionDateLabel = FLAG_VERSION + version;
         } else if (date != null) {
             versionDateLabel = FLAG_VERSION_DATE + date;
         } else {
             // Use getShortLabel() so labels longer then 30 char are truncated
             // and the user is warned
             String shortLabel = getShortLabel();
             if (shortLabel != null && !shortLabel.equals("")) {
                 versionDateLabel = FLAG_VERSION_LABEL + shortLabel;
             }
         }
         return versionDateLabel;
     }
 
     /**
      * Gets the version string.
      * @return An empty string if a version is not set.
      */
     protected String getVersion() {
         return version != null ? FLAG_VERSION + version : "";
     }
 
     /**
      * Gets the localpath string. "-GLc:\source" <p>
      * The localpath is created if it didn't exist.
      * @return An empty string if localpath is not set.
      */
     protected String getLocalpath() {
         String lclPath = ""; //set to empty str if no local path return
         if (localPath != null) {
             //make sure m_LocalDir exists, create it if it doesn't
             File dir = getProject().resolveFile(localPath);
             if (!dir.exists()) {
                 boolean done = dir.mkdirs();
                 if (!done) {
                     String msg = "Directory " + localPath + " creation was not "
                             + "successful for an unknown reason";
                     throw new BuildException(msg, getLocation());
                 }
                 getProject().log("Created dir: " + dir.getAbsolutePath());
             }
             lclPath = FLAG_OVERRIDE_WORKING_DIR + localPath;
         }
         return lclPath;
     }
 
     /**
      * Gets the comment string. "-Ccomment text"
      * @return A comment of "-" if comment is not set.
      */
     protected String getComment() {
         return comment != null ? FLAG_COMMENT + comment : FLAG_COMMENT + "-";
     }
 
     /**
      * Gets the auto response string. This can be Y "-I-Y" or N "-I-N".
      * @return The default value "-I-" if autoresponse is not set.
      */
     protected String getAutoresponse() {
         if (autoResponse == null) {
             return FLAG_AUTORESPONSE_DEF;
         } else if (autoResponse.equalsIgnoreCase("Y")) {
             return FLAG_AUTORESPONSE_YES;
         } else if (autoResponse.equalsIgnoreCase("N")) {
             return FLAG_AUTORESPONSE_NO;
         } else {
             return FLAG_AUTORESPONSE_DEF;
         }
     }
 
     /**
      * Gets the login string. This can be user and password, "-Yuser,password"
      * or just user "-Yuser".
      * @return An empty string if login is not set.
      */
     protected String getLogin() {
         return vssLogin != null ? FLAG_LOGIN + vssLogin : "";
     }
 
     /**
      * Gets the output file string. "-Ooutput.file"
      * @return An empty string if user is not set.
      */
     protected String getOutput() {
         return outputFileName != null ? FLAG_OUTPUT + outputFileName : "";
     }
 
     /**
      * Gets the user string. "-Uusername"
      * @return An empty string if user is not set.
      */
     protected String getUser() {
         return user != null ? FLAG_USER + user : "";
     }
 
     /**
      * Gets the version string. This can be to-from "-VLbuild2~Lbuild1", from
      * "~Lbuild1" or to "-VLbuild2".
      * @return An empty string if neither tolabel or fromlabel are set.
      */
     protected String getVersionLabel() {
         if (fromLabel == null && toLabel == null) {
             return "";
         }
         if (fromLabel != null && toLabel != null) {
             if (fromLabel.length() > 31) {
                 fromLabel = fromLabel.substring(0, 30);
                 log("FromLabel is longer than 31 characters, truncated to: "
                     + fromLabel, Project.MSG_WARN);
             }
             if (toLabel.length() > 31) {
                 toLabel = toLabel.substring(0, 30);
                 log("ToLabel is longer than 31 characters, truncated to: "
                     + toLabel, Project.MSG_WARN);
             }
             return FLAG_VERSION_LABEL + toLabel + VALUE_FROMLABEL + fromLabel;
         } else if (fromLabel != null) {
             if (fromLabel.length() > 31) {
                 fromLabel = fromLabel.substring(0, 30);
                 log("FromLabel is longer than 31 characters, truncated to: "
                     + fromLabel, Project.MSG_WARN);
             }
             return FLAG_VERSION + VALUE_FROMLABEL + fromLabel;
         } else {
             if (toLabel.length() > 31) {
                 toLabel = toLabel.substring(0, 30);
                 log("ToLabel is longer than 31 characters, truncated to: "
                     + toLabel, Project.MSG_WARN);
             }
             return FLAG_VERSION_LABEL + toLabel;
         }
     }
 
     /**
      * Gets the Version date string.
      * @return An empty string if neither Todate or from date are set.
      * @throws BuildException
      */
     protected String getVersionDate() throws BuildException {
         if (fromDate == null && toDate == null
             && numDays == Integer.MIN_VALUE) {
             return "";
         }
         if (fromDate != null && toDate != null) {
             return FLAG_VERSION_DATE + toDate + VALUE_FROMDATE + fromDate;
         } else if (toDate != null && numDays != Integer.MIN_VALUE) {
             try {
                 return FLAG_VERSION_DATE + toDate + VALUE_FROMDATE
                         + calcDate(toDate, numDays);
             } catch (ParseException ex) {
                 String msg = "Error parsing date: " + toDate;
                 throw new BuildException(msg, getLocation());
             }
         } else if (fromDate != null && numDays != Integer.MIN_VALUE) {
             try {
                 return FLAG_VERSION_DATE + calcDate(fromDate, numDays)
                         + VALUE_FROMDATE + fromDate;
             } catch (ParseException ex) {
                 String msg = "Error parsing date: " + fromDate;
                 throw new BuildException(msg, getLocation());
             }
         } else {
             return fromDate != null ? FLAG_VERSION + VALUE_FROMDATE
                     + fromDate : FLAG_VERSION_DATE + toDate;
         }
     }
 
     /**
      * Builds and returns the -G- flag if required.
      * @return An empty string if get local copy is true.
      */
     protected String getGetLocalCopy() {
         return (!getLocalCopy) ? FLAG_NO_GET : "";
     }
 
     /**
      * Gets the value of the fail on error flag.
      * @return    True if the FailOnError flag has been set or if 'writablefiles=skip'.
      */
     private boolean getFailOnError() {
         return getWritableFiles().equals(WRITABLE_SKIP) ? false : failOnError;
     }
 
 
     /**
      * Gets the value set for the FileTimeStamp.
      * if it equals "current" then we return -GTC
      * if it equals "modified" then we return -GTM
      * if it equals "updated" then we return -GTU
      * otherwise we return -GTC
      *
      * @return The default file time flag, if not set.
      */
     public String getFileTimeStamp() {
         if (timestamp == null) {
             return "";
         } else if (timestamp.getValue().equals(TIME_MODIFIED)) {
             return FLAG_FILETIME_MODIFIED;
         } else if (timestamp.getValue().equals(TIME_UPDATED)) {
             return FLAG_FILETIME_UPDATED;
         } else {
             return FLAG_FILETIME_DEF;
         }
     }
 
 
     /**
      * Gets the value to determine the behaviour when encountering writable files.
      * @return An empty String, if not set.
      */
     public String getWritableFiles() {
         if (writableFiles == null) {
             return "";
         } else if (writableFiles.getValue().equals(WRITABLE_REPLACE)) {
             return FLAG_REPLACE_WRITABLE;
         } else if (writableFiles.getValue().equals(WRITABLE_SKIP)) {
             // ss.exe exits with '100', when files have been skipped
             // so we have to ignore the failure
             failOnError = false;
             return FLAG_SKIP_WRITABLE;
         } else {
             return "";
         }
     }
 
     /**
      *  Sets up the required environment and executes the command line.
      *
      * @param  cmd  The command line to execute.
      * @return      The return code from the exec'd process.
      */
     private int run(Commandline cmd) {
         try {
             Execute exe = new Execute(new LogStreamHandler(this,
                     Project.MSG_INFO,
                     Project.MSG_WARN));
 
             // If location of ss.ini is specified we need to set the
             // environment-variable SSDIR to this value
             if (serverPath != null) {
                 String[] env = exe.getEnvironment();
                 if (env == null) {
                     env = new String[0];
                 }
                 String[] newEnv = new String[env.length + 1];
-                for (int i = 0; i < env.length; i++) {
-                    newEnv[i] = env[i];
-                }
+                System.arraycopy(env, 0, newEnv, 0, env.length);
                 newEnv[env.length] = "SSDIR=" + serverPath;
 
                 exe.setEnvironment(newEnv);
             }
 
             exe.setAntRun(getProject());
             exe.setWorkingDirectory(getProject().getBaseDir());
             exe.setCommandline(cmd.getCommandline());
             // Use the OS launcher so we get environment variables
             exe.setVMLauncher(false);
             return exe.execute();
         } catch (IOException e) {
             throw new BuildException(e, getLocation());
         }
     }
 
      /**
      * Calculates the start date for version comparison.
      * <p>
      * Calculates the date numDay days earlier than startdate.
      * @param   startDate    The start date.
      * @param   daysToAdd     The number of days to add.
      * @return The calculated date.
      * @throws ParseException
      */
     private String calcDate(String startDate, int daysToAdd) throws ParseException {
-        Date currentDate = new Date();
         Calendar calendar = new GregorianCalendar();
-        currentDate = dateFormat.parse(startDate);
+        Date currentDate = dateFormat.parse(startDate);
         calendar.setTime(currentDate);
         calendar.add(Calendar.DATE, daysToAdd);
         return dateFormat.format(calendar.getTime());
     }
 
     /**
      * Changes the password to '***' so it isn't displayed on screen if the build fails
      *
      * @param cmd   The command line to clean
      * @return The command line as a string with out the password
      */
     private String formatCommandLine(Commandline cmd) {
         StringBuffer sBuff = new StringBuffer(cmd.toString());
         int indexUser = sBuff.substring(0).indexOf(FLAG_LOGIN);
         if (indexUser > 0) {
             int indexPass = sBuff.substring(0).indexOf(",", indexUser);
             int indexAfterPass = sBuff.substring(0).indexOf(" ", indexPass);
 
             for (int i = indexPass + 1; i < indexAfterPass; i++) {
                 sBuff.setCharAt(i, '*');
             }
         }
         return sBuff.toString();
     }
 
     /**
      * Extention of EnumeratedAttribute to hold the values for file time stamp.
      */
     public static class CurrentModUpdated extends EnumeratedAttribute {
         /**
          * Gets the list of allowable values.
          * @return The values.
          */
         public String[] getValues() {
             return new String[] {TIME_CURRENT, TIME_MODIFIED, TIME_UPDATED};
         }
     }
 
     /**
      * Extention of EnumeratedAttribute to hold the values for writable filess.
      */
     public static class WritableFiles extends EnumeratedAttribute {
         /**
          * Gets the list of allowable values.
          * @return The values.
          */
         public String[] getValues() {
             return new String[] {WRITABLE_REPLACE, WRITABLE_SKIP, WRITABLE_FAIL};
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/rmic/DefaultRmicAdapter.java b/src/main/org/apache/tools/ant/taskdefs/rmic/DefaultRmicAdapter.java
index 22b48a42d..c06d591e0 100644
--- a/src/main/org/apache/tools/ant/taskdefs/rmic/DefaultRmicAdapter.java
+++ b/src/main/org/apache/tools/ant/taskdefs/rmic/DefaultRmicAdapter.java
@@ -1,413 +1,414 @@
 /*
- * Copyright  2001-2005 The Apache Software Foundation
+ * Copyright  2001-2006 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs.rmic;
 
 import java.io.File;
 import java.util.Random;
 import java.util.Vector;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.Rmic;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileNameMapper;
 
 /**
  * This is the default implementation for the RmicAdapter interface.
  * Currently, this is a cut-and-paste of the original rmic task and
  * DefaultCopmpilerAdapter.
  *
  * @since Ant 1.4
  */
 public abstract class DefaultRmicAdapter implements RmicAdapter {
 
     private Rmic attributes;
     private FileNameMapper mapper;
     private static final Random RAND = new Random();
     /** suffix denoting a stub file */
     public static final String RMI_STUB_SUFFIX = "_Stub";
     /** suffix denoting a skel file */
     public static final String RMI_SKEL_SUFFIX = "_Skel";
     /** suffix denoting a tie file */
     public static final String RMI_TIE_SUFFIX = "_Tie";
     public static final String STUB_COMPAT = "-vcompat";
     public static final String STUB_1_1 = "-v1.1";
     public static final String STUB_1_2 = "-v1.2";
 
     /**
      * Default constructor
      */
     public DefaultRmicAdapter() {
     }
 
     /**
      * Sets Rmic attributes
      * @param attributes the rmic attributes
      */
     public void setRmic(final Rmic attributes) {
         this.attributes = attributes;
         mapper = new RmicFileNameMapper();
     }
 
     /**
      * Get the Rmic attributes
      * @return the attributes as a Rmic taskdef
      */
     public Rmic getRmic() {
         return attributes;
     }
 
     /**
      * Gets the stub class suffix
      * @return the stub suffix &quot;_Stub&quot;
      */
     protected String getStubClassSuffix() {
         return RMI_STUB_SUFFIX;
     }
 
     /**
      * Gets the skeleton class suffix
      * @return the skeleton suffix &quot;_Skel&quot;
      */
     protected String getSkelClassSuffix() {
         return RMI_SKEL_SUFFIX;
     }
 
     /**
      * Gets the tie class suffix
      * @return the tie suffix &quot;_Tie&quot;
      */
     protected String getTieClassSuffix() {
         return RMI_TIE_SUFFIX;
     }
 
     /**
      * This implementation returns a mapper that may return up to two
      * file names.
      *
      * <ul>
      *   <li>for JRMP it will return *_getStubClassSuffix (and
      *   *_getSkelClassSuffix if JDK 1.1 is used)</li>
      *
      *   <li>for IDL it will return a random name, causing &lt;rmic&gt; to
      *     always recompile.</li>
      *
      *   <li>for IIOP it will return _*_getStubClassSuffix for
      *   interfaces and _*_getStubClassSuffix for non-interfaces (and
      *   determine the interface and create _*_Stub from that).</li>
      * </ul>
      * @return a <code>FileNameMapper</code>
      */
     public FileNameMapper getMapper() {
         return mapper;
     }
 
     /**
      * Gets the CLASSPATH this rmic process will use.
      * @return the classpath
      */
     public Path getClasspath() {
         return getCompileClasspath();
     }
 
     /**
      * Builds the compilation classpath.
      * @return the classpath
      */
     protected Path getCompileClasspath() {
         Path classpath = new Path(attributes.getProject());
         // add dest dir to classpath so that previously compiled and
         // untouched classes are on classpath
         classpath.setLocation(attributes.getBase());
 
         // Combine the build classpath with the system classpath, in an
         // order determined by the value of build.sysclasspath
 
         Path cp = attributes.getClasspath();
         if (cp == null) {
             cp = new Path(attributes.getProject());
         }
         if (attributes.getIncludeantruntime()) {
             classpath.addExisting(cp.concatSystemClasspath("last"));
         } else {
             classpath.addExisting(cp.concatSystemClasspath("ignore"));
         }
 
         if (attributes.getIncludejavaruntime()) {
             classpath.addJavaRuntime();
         }
         return classpath;
     }
 
     /**
      * Setup rmic argument for rmic.
      * @return the command line
      */
     protected Commandline setupRmicCommand() {
         return setupRmicCommand(null);
     }
 
     /**
      * Setup rmic argument for rmic.
      * @param options additional parameters needed by a specific
      *                implementation.
      * @return the command line
      */
     protected Commandline setupRmicCommand(String[] options) {
         Commandline cmd = new Commandline();
 
         if (options != null) {
             for (int i = 0; i < options.length; i++) {
                 cmd.createArgument().setValue(options[i]);
             }
         }
 
         Path classpath = getCompileClasspath();
 
         cmd.createArgument().setValue("-d");
         cmd.createArgument().setFile(attributes.getBase());
 
         if (attributes.getExtdirs() != null) {
             cmd.createArgument().setValue("-extdirs");
             cmd.createArgument().setPath(attributes.getExtdirs());
         }
 
         cmd.createArgument().setValue("-classpath");
         cmd.createArgument().setPath(classpath);
 
         //handle the many different stub options.
         String stubVersion = attributes.getStubVersion();
         //default is compatibility
         String stubOption=STUB_COMPAT;
         if (null != stubVersion) {
             if ("1.1".equals(stubVersion)) {
                 stubOption = STUB_1_1;
             } else if ("1.2".equals(stubVersion)) {
                 stubOption = STUB_1_2;
             } else if ("compat".equals(stubVersion)) {
                 stubOption = STUB_COMPAT;
             } else {
                 //anything else
                 attributes.log("Unknown stub option "+stubVersion);
                 //do nothing with the value? or go -v+stubVersion??
             }
         }
         cmd.createArgument().setValue(stubOption);
 
         if (null != attributes.getSourceBase()) {
             cmd.createArgument().setValue("-keepgenerated");
         }
 
         if (attributes.getIiop()) {
             attributes.log("IIOP has been turned on.", Project.MSG_INFO);
             cmd.createArgument().setValue("-iiop");
             if (attributes.getIiopopts() != null) {
                 attributes.log("IIOP Options: " + attributes.getIiopopts(),
                                Project.MSG_INFO);
                 cmd.createArgument().setValue(attributes.getIiopopts());
             }
         }
 
         if (attributes.getIdl())  {
             cmd.createArgument().setValue("-idl");
             attributes.log("IDL has been turned on.", Project.MSG_INFO);
             if (attributes.getIdlopts() != null) {
                 cmd.createArgument().setValue(attributes.getIdlopts());
                 attributes.log("IDL Options: " + attributes.getIdlopts(),
                                Project.MSG_INFO);
             }
         }
 
         if (attributes.getDebug()) {
             cmd.createArgument().setValue("-g");
         }
 
         cmd.addArguments(attributes.getCurrentCompilerArgs());
 
         logAndAddFilesToCompile(cmd);
         return cmd;
      }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param cmd the commandline args
      */
     protected void logAndAddFilesToCompile(Commandline cmd) {
         Vector compileList = attributes.getCompileList();
 
         attributes.log("Compilation " + cmd.describeArguments(),
                        Project.MSG_VERBOSE);
 
         StringBuffer niceSourceList = new StringBuffer("File");
         int cListSize = compileList.size();
         if (cListSize != 1) {
             niceSourceList.append("s");
         }
         niceSourceList.append(" to be compiled:");
 
         for (int i = 0; i < cListSize; i++) {
             String arg = (String) compileList.elementAt(i);
             cmd.createArgument().setValue(arg);
-            niceSourceList.append("    " + arg);
+            niceSourceList.append("    ");
+            niceSourceList.append(arg);
         }
 
         attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * Mapper that may return up to two file names.
      *
      * <ul>
      *   <li>for JRMP it will return *_getStubClassSuffix (and
      *   *_getSkelClassSuffix if JDK 1.1 is used)</li>
      *
      *   <li>for IDL it will return a random name, causing <rmic> to
      *     always recompile.</li>
      *
      *   <li>for IIOP it will return _*_getStubClassSuffix for
      *   interfaces and _*_getStubClassSuffix for non-interfaces (and
      *   determine the interface and create _*_Stub from that).</li>
      * </ul>
      */
     private class RmicFileNameMapper implements FileNameMapper {
 
         RmicFileNameMapper() {
         }
 
         /**
          * Empty implementation.
          */
         public void setFrom(String s) {
         }
         /**
          * Empty implementation.
          */
         public void setTo(String s) {
         }
 
         public String[] mapFileName(String name) {
             if (name == null
                 || !name.endsWith(".class")
                 || name.endsWith(getStubClassSuffix() + ".class")
                 || name.endsWith(getSkelClassSuffix() + ".class")
                 || name.endsWith(getTieClassSuffix() + ".class")) {
                 // Not a .class file or the one we'd generate
                 return null;
             }
 
             // we know that name.endsWith(".class")
             String base = name.substring(0, name.length() - 6);
 
             String classname = base.replace(File.separatorChar, '.');
             if (attributes.getVerify()
                 && !attributes.isValidRmiRemote(classname)) {
                 return null;
             }
 
             /*
              * fallback in case we have trouble loading the class or
              * don't know how to handle it (there is no easy way to
              * know what IDL mode would generate.
              *
              * This is supposed to make Ant always recompile the
              * class, as a file of that name should not exist.
              */
             String[] target = new String[] {name + ".tmp." + RAND.nextLong()};
 
             if (!attributes.getIiop() && !attributes.getIdl()) {
                 // JRMP with simple naming convention
                 if ("1.2".equals(attributes.getStubVersion())) {
                     target = new String[] {
                         base + getStubClassSuffix() + ".class"
                     };
                 } else {
                     target = new String[] {
                         base + getStubClassSuffix() + ".class",
                         base + getSkelClassSuffix() + ".class",
                     };
                 }
             } else if (!attributes.getIdl()) {
                 int lastSlash = base.lastIndexOf(File.separatorChar);
 
                 String dirname = "";
                 /*
                  * I know, this is not necessary, but I prefer it explicit (SB)
                  */
                 int index = -1;
                 if (lastSlash == -1) {
                     // no package
                     index = 0;
                 } else {
                     index = lastSlash + 1;
                     dirname = base.substring(0, index);
                 }
 
                 String filename = base.substring(index);
 
                 try {
                     Class c = attributes.getLoader().loadClass(classname);
 
                     if (c.isInterface()) {
                         // only stub, no tie
                         target = new String[] {
                             dirname + "_" + filename + getStubClassSuffix()
                             + ".class"
                         };
                     } else {
                         /*
                          * stub is derived from implementation,
                          * tie from interface name.
                          */
                         Class interf = attributes.getRemoteInterface(c);
                         String iName = interf.getName();
                         String iDir = "";
                         int iIndex = -1;
                         int lastDot = iName.lastIndexOf(".");
                         if (lastDot == -1) {
                             // no package
                             iIndex = 0;
                         } else {
                             iIndex = lastDot + 1;
                             iDir = iName.substring(0, iIndex);
                             iDir = iDir.replace('.', File.separatorChar);
                         }
 
                         target = new String[] {
                             dirname + "_" + filename + getTieClassSuffix()
                             + ".class",
                             iDir + "_" + iName.substring(iIndex)
                             + getStubClassSuffix() + ".class"
                         };
                     }
                 } catch (ClassNotFoundException e) {
                     attributes.log("Unable to verify class " + classname
                                    + ". It could not be found.",
                                    Project.MSG_WARN);
                 } catch (NoClassDefFoundError e) {
                     attributes.log("Unable to verify class " + classname
                                    + ". It is not defined.", Project.MSG_WARN);
                 } catch (Throwable t) {
                     attributes.log("Unable to verify class " + classname
                                    + ". Loading caused Exception: "
                                    + t.getMessage(), Project.MSG_WARN);
                 }
             }
             return target;
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/types/resources/Sort.java b/src/main/org/apache/tools/ant/types/resources/Sort.java
index 53faadd8b..e51e7adde 100755
--- a/src/main/org/apache/tools/ant/types/resources/Sort.java
+++ b/src/main/org/apache/tools/ant/types/resources/Sort.java
@@ -1,177 +1,177 @@
 /*
- * Copyright 2005 The Apache Software Foundation
+ * Copyright 2005-2006 The Apache Software Foundation
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
 package org.apache.tools.ant.types.resources;
 
 import java.util.Stack;
 import java.util.Vector;
 import java.util.TreeMap;
 import java.util.Iterator;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.Collections;
 import java.util.AbstractCollection;
 import java.util.NoSuchElementException;
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.types.DataType;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
 
 /**
  * ResourceCollection that sorts another ResourceCollection.
  * @since Ant 1.7
  */
 public class Sort extends BaseResourceCollectionWrapper {
 
-    private class MultiComparator implements Comparator {
+    private static class MultiComparator implements Comparator {
         private Vector v = null;
         synchronized void add(ResourceComparator c) {
             if (c == null) {
                 return;
             }
             v = (v == null) ? new Vector() : v;
             v.add(c);
         }
         public synchronized int compare(Object o1, Object o2) {
             int result = 0;
             //if no nested, natural order:
             if (v == null || v.size() == 0) {
                 result = ((Comparable) o1).compareTo((Comparable) o2);
             } else {
                 for (Iterator i = v.iterator(); result == 0 && i.hasNext();) {
                     result = ((Comparator) i.next()).compare(o1, o2);
                 }
             }
             return result;
         }
     }
 
     //sorted bag impl. borrowed from commons-collections TreeBag:
-    private class SortedBag extends AbstractCollection {
+    private static class SortedBag extends AbstractCollection {
         private class MutableInt {
             int value = 0;
         }
         private class MyIterator implements Iterator {
             private Iterator keyIter = t.keySet().iterator();
             private Object current;
             private int occurrence;
             public synchronized boolean hasNext() {
                 return occurrence > 0 || keyIter.hasNext();
             }
             public synchronized Object next() {
                 if (!hasNext()) {
                     throw new NoSuchElementException();
                 }
                 if (occurrence == 0) {
                     current = keyIter.next();
                     occurrence = ((MutableInt) t.get(current)).value;
                 }
                 --occurrence;
                 return current;
             }
             public void remove() {
                 throw new UnsupportedOperationException();
             }
         }
         private TreeMap t;
         private int size;
 
         SortedBag(Comparator c) {
             t = new TreeMap(c);
         }
         public synchronized Iterator iterator() {
             return new MyIterator();
         }
         public synchronized boolean add(Object o) {
             if (size < Integer.MAX_VALUE) {
                 ++size;
             }
             MutableInt m = (MutableInt) (t.get(o));
             if (m == null) {
                 m = new MutableInt();
                 t.put(o, m);
             }
             m.value++;
             return true;
         }
         public synchronized int size() {
             return size;
         }
     }
 
     private MultiComparator comp = new MultiComparator();
 
     /**
      * Sort the contained elements.
      * @return a Collection of Resources.
      */
     protected synchronized Collection getCollection() {
         ResourceCollection rc = getResourceCollection();
         Iterator iter = rc.iterator();
         if (!(iter.hasNext())) {
             return Collections.EMPTY_SET;
         }
         SortedBag b = new SortedBag(comp);
         while (iter.hasNext()) {
             b.add(iter.next());
         }
         return b;
     }
 
     /**
      * Add a ResourceComparator to this Sort ResourceCollection.
      * If multiple ResourceComparators are added, they will be processed in LIFO order.
      * @param c the ResourceComparator to add.
      */
     public synchronized void add(ResourceComparator c) {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         comp.add(c);
         FailFast.invalidate(this);
     }
 
     /**
      * Overrides the BaseResourceCollectionContainer version
      * to recurse on nested ResourceComparators.
      * @param stk the stack of data types to use (recursively).
      * @param p   the project to use to dereference the references.
      * @throws BuildException on error.
      */
     protected synchronized void dieOnCircularReference(Stack stk, Project p)
         throws BuildException {
         if (isChecked()) {
             return;
         }
         if (isReference()) {
             super.dieOnCircularReference(stk, p);
         } else {
             if (comp.v != null && comp.v.size() > 0) {
                 for (Iterator i = comp.v.iterator(); i.hasNext();) {
                     Object o = i.next();
                     if (o instanceof DataType) {
                         stk.push(o);
                         invokeCircularReferenceCheck((DataType) o, stk, p);
                     }
                 }
             }
             setChecked(true);
         }
     }
 
 }
