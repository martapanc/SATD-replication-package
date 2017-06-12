diff --git a/WHATSNEW b/WHATSNEW
index 30dc39db3..5ecb794d7 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1185 +1,1189 @@
 Changes from Ant 1.7.x TO current SVN version
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 * Improved handling of InterruptException (lets suppose someone/thing is
   trying to kill the thread when we receive an InterruptException),
   when an InterruptException is received, we do not wait anymore in a while
   loop till the end time has been reached. Bugzilla report 42924.
 
 * Refactor PropertyHelper and introspection APIs to make extension more
   granular and support setting task/type attribute values to objects
   decoded by custom PropertyEvaluator delegates. Also add <propertyhelper>
   task for registering delegates and/or replacing the registered PropertyHelper
   instance.  Bugzilla report 42736.
 
 * Added a restricted form of typedef called <componentdef>. This allows
   definition of elements that can only be within tasks or types. This
   method is now used to define conditions, selectors and selectors. This
   means that tasks may now have nested conditions just by implementing
   the Condition interface, rather than extending ConditionBase. It also
   means that the use of namespaces for some of the selectors introduced
   in Ant 1.7.0 is no longer necessary.
   Implementing this means that the DynamicElement work-around introduced
   in Ant 1.7.0 has been removed.
   Bugzilla report 40511.
 
 * In the <touch> task when a <mapper> is used, the millis and datetime
   attributes now override the time of the source resource if provisioned. 
   Bugzilla report 43235.
 
 * Remove fall-back mechanism for references that are not resolved
   during normal runtime execution.
 
 * FileUtils.createTempFile now actually creates the file.
   The TempFile task still does not create the file by default, can be instructed
   to do so however using a new parameter.
   Bugzilla report 33969.
   
 * A lock in Project ensured that a BuildListener's messageLogged
   method was only ever executed by a single thread at a time, while
   all other methods could be invoked by multiple threads
   simultaniously (while within <parallel>, for example).  This lock is
   no longer in place, messageLogged should be made thread-safe now.
 
  * <sql>'s onError="stop" no longer fails the build if an error
    occurs,  this is the main difference between stop and error and
    matches what the documentation implied.
    Bugzilla Report 24668.
 
  * Ant's configuration introspection mechanisms have been modified to prefer
    Resource and FileProvider attributes to plain java.io.File attributes;
    however the configuration-from-String behavior remains equivalent, rendering
    a FileResource.
 
  * CBZip2InputStream will now throw an IOException if
    passed in a null or empty InputStream to read from.
    Bugzilla Report 32200
 
  * <unzip> will now fail when trying to extract certain broken
    archives that would have been silently ignored in earlier version.
    Bugzilla report 35000.
 
  * Ant's <zip> family of tasks tries to preserve the existing Unix
    permissions when updating archives or copying entries from one
    archive to another.
    Since not all archiving tools support storing Unix permissions in
    the same way that is used by Ant, sometimes the permissions read by
    Ant seem to be 0, which means nobody is allowed to do anything to
    the file or directory.
    If Ant now encounters a permission set of 0 it will assume that
    this is not the intended value and instead apply its own default
    values.  Ant used to create entries with 0 permissions itself.
    The <zip> family of tasks has a new attribute preserve0permissions
    that can be set to restore the old behavior.
    Bugzilla Report 42122.
 
  * If a batch containing multiple JUnit tests running inside a forked
    Java VM caused the VM to crash (or caused a timeout), the
    formatters would receive an error message for the last test in the
    batch.
    Ant will now pass in a test with the name "Batch-With-Multiple-Tests"
    instead - this is supposed to show more clearly that the last test
    may not have started at all.
    Bugzilla Report 45227.
 
  * If the number of minutes a build takes is bigger then 1000 Ant will
    no longer print a thousands separator in the "elapsed time"
    message.  It used to be the thousands separator of the current
    locale.
    Bugzilla Report 44659.
 
 Fixed bugs:
 -----------
 
  * <symlink> task couldn't overwrite existing symlinks that pointed to nonexistent files
    Bugzilla report 38199.
 
  * <symlink> task couldn't overwrite files that were in the way of the symlink.
    Bugzilla report 43426.
    
  * <symlink> task failonerror="false" does not stop build from failing when 'ln' 
    command returns non-zero. Bugzilla report 43624  
 
  * <touch> task couldn't differentiate between "no resources specified" and "no resources
    matched."  Bugzilla report 43799.
 
  * ManifestClassPath throws when a relative path would traverse the file system root. Bugzilla
    report 44499.
 
  * <globmapper> had an indexoutofbounds when the prefix and postfix overlapped. Bugzilla report
    44731.
    
  * <typedef> and <taskdef> failed to accept file names with #
    characters in them.
    Bugzilla report 45190
 
  * A deadlock could occur if a BuildListener tried to access an Ant property
    within messageLogged while a different thread also accessed one.
    Bugzilla report 45194
 
  * Handle null result of system getProperty() in CommandlineJava.
    Similar to Bugzilla report 42334.
 
  * Length task did not process nonexistent Resources even though these might
    conceivably still carry file length information.  Bugzilla report 45271.
 
  * <javac>'s includeJavaRuntime="false" should work for gcj now.  Note
    that you may need to set includeAntRuntime to false in order to
    have full control.
    Bugzilla Report 34638.
 
  * <sql> would fail if the executed statment didn't return a result
    set with some JDBC driver that dissalow Statement.getResultSet to
    be called in such a situation.
    Bugzilla report 36265 
 
  * if the executed statement in <sql> returned a result set and an
    update count, the count would be lost.
 
  * if an executed statement in <sql> mixes update count and result set
    parts, some result sets wouldn't get printed.
    Bugzilla Report 32168.
 
  * XmlLogger could lose messages if <parallel> is used.
    Bugzilla Report 25734.
 
  * <scp> creates remoteToDir if it doesn't exist.
    Bugzilla Report 42781
 
  * CBZip2OutputStream threw an exception if it was closed prior to
    writing anything.
    Bugzilla Report 32200
 
  * The IPlanetDeploymentToll didn't use the configured DTD locations.
    Bugzilla Report 31876.
 
  * The ant shell script printed a warning unser Cygwin if JAVA_HOME
    was not set.
    Bugzilla Report 45245.
 
  * <filterset> sometimes incorrectly flagged infinite recursions of
    filter tokens
    Bugzilla Report 44226.
    
  * failures were treated as errors in forked JUnit tests when JUnit 4
    was used.
    Bugzilla Report 43892.
 
  * <jar> and <manifest> disallowed manifest attributes whose name
    contained the character '8'.
    Bugzilla Report 45675.
 
  * BigProjectLogger would set the project's basedir to the current
    working directory.
    Bugzilla Report 45607.
 
  * only <formatter>s that logged to a file were notified if forked VM
    crashed or a timeout occured in <junit>.
    Bugzilla Report 37312.
 
  * ant -v -version would print the version information twice.
    Bugzilla Report 45695.
 
+ * when nested into builds that have been invoked by <ant> tasks
+   <subant> might set the wrong basedir on the called projects.
+   Bugzilla Report 30569.
+
 Other changes:
 --------------
 
  * There is now a FileProvider interface for resources that act as a source
    of filenames. This should be used by tasks that require resources
    to provide filenames, rather than require that all resources
    are instances or subclasses of FileResource.
    Bugzilla report 43348
    
  * Fixcrlf now gives better error messages on bad directory attributes.
    Bugzilla report 43936
    
  * a new property ant.project.default-target holds the value of the
    current <project>'s default attribute.
 
  * a new property ant.project.invoked-targets holds a comma separated
    list of the targets that have been specified on the command line
    (the IDE, an <ant> task ...) when invoking the current project.
 
  * The <type> resource selector has had an "any" type added for better
    configurability.
 
  * Ant should detect the OS as both a Mac and a Unix system when
    running on OpenJDK.
    Bugzilla Report 44889.
 
  * new protected getConnection and getStatement methods allow
    subclasses of SQLExec more control - or access to the cached
    instances when overriding other methods like runStatements.
    Bugzilla Report 27178.
 
  * <sql> has a new failOnConnectionError attribute that can be used to
    keep a build going even if the task failed to connect to the
    database.
    Bugzilla Report 36712.
 
  * A new attribute strictDelimiterMatching can be used to ignore case
    or whitespace differences when <sql> searches for delimiters.
    This is useful if you execute a SQL script that has contains "GO"
    and "go" as delimiters.
    Bugzilla Report 26459.
 
  * A new showWarnings attribute of <sql> allows warnings to be logged.
    Bugzilla Report 41836.
 
  * A new treatWarningsAsErrors attribute of <sql> can be used to fail
    a build if a warning occurs.
    Bugzilla Report 41836.
 
  * Ant now supports scoped properties (see Local task). Bugzilla report 23942.
 
  * <sql>'s CSV output can be controlled via the new attributes
    csvColumnSeparator and csvQuoteCharacter.
    Bugzilla report 35627.
 
  * <ftp>'s logging has been improved.
    Bugzilla reports 30932, 31743.
 
  * It is now possible to disable <ftp>'s remote verification.
    Bugzilla report 35471.
 
  * <sshexec> now supports input in a way similar to <exec>
    Bugzilla report 39197.
 
  * <scp> can now preserve the file modification time when downloading
    files.
    Bugzilla Issue 33939.
 
  * the new task sshsession can run multiple tasks in the presence of
    an SSH session providing (local and remote) tunnels.
    Bugzilla Issue 43083.
 
  * ZipOutputStream has been sped up for certain usage scenarios that
    are not used by Ant's family of zip tasks.
    Bugzilla report 45396.
 
  * <echo> supports an "output" Resource attribute as an alternative to "file".
 
  * <sql> "output" attribute now supports any Resource in addition to a file.
 
  * <scp> no longer requires a passphrase when using key based
    authentication.
    Bugzilla report 33718.
 
  * a new failOnEmptyArchive attribute on <unzip> and <untar> can now
    make the task fail the build if it tries to extract an empty
    archive.
 
  * <unzip> and <untar> have a new attribute stripAbsolutePathSpec.
    When set to true, Ant will remove any leading path separator from
    the archived entry's name before extracting it (making the name a
    relative file name).
    Bugzilla Report 28911.
 
  * <unzip> will now detect that it was asked to extract a file that is
    not an archive earlier if the file is big.
    Bugzilla Report 45463.
 
  * New file and resource selectors <readable/> and <writable/> have
    been added that select file which the current process can read or
    write.
    Bugzilla Report 45081.
 
  * The filename file selector has a new attribute regex that allows
    files to be selected by matching their names against a regular
    expression.
    Bugzilla Report 45284
 
  * The name resource selector has a new attribute regex that allows
    resources to be selected by matching their names against a regular
    expression.
    Bugzilla Report 45284
 
  * Enhanced performance of Project.fireMessageLoggedEvent and DirectoryScanner 
    Bugzilla Report 45651 & 45665
 
  * The package list location for offline links can now be specified as
    an URL.
    Bugzilla Report 28881
 
  * <echoxml> now supports XML namespaces.
    Bugzilla Report 36804.
 
  * A new listener for <junit> has been added that tries to invoke the
    tearDown method of a TestCase if that TestCase was run in a forked
    VM and the VM crashed or a timeout occured.  See the <junit> task's
    manual page for details.
    Bugzilla Report 37241.
 
 Changes from Ant 1.7.0 TO Ant 1.7.1
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 * String resources only have properties single expanded. If you relied on
   <string> resources being expanded more than once, it no longer happens.
   Bugzilla report 42277.
 
 * A String resource's encoding attribute was only taken into account when
   set from the resource's OutputStream; the InputStream provided the String's
   binary content according to the platform's default encoding. Behavior has
   been modified to encode outgoing (InputStream) content as well as encoding
   incoming (OutputStream) content.
 
 * <java> with fork now returns gives -1 instead of 0 as result when failonerror
   is false and some exception (including timeout) occurs. Br 42377. 
 
 * ant-type attribute has been marked as deprecated and a warning has been
   issued if it is encountered in the build file.
 
 Fixed bugs:
 -----------
 
 * The default logger was failing to print complete stack traces for exceptions
   other than BuildException, thus omitting often important diagnostic
   information. Bugzilla 43398.
 
 * Error in FTP task
   Bugzilla report 41724
 
 * Regression: Locator fails with URI encoding problem when spaces in path
   Bugzilla report 42222
 
 * Regression in Locator: running Ant off a network share does not work:
   message "URI has authority component" appears
   Bugzilla report 42275
 
 * Improvements in AntClassLoader Speed.
   Bugzilla report 42259
 
 * Error in handling of some permissions, most notably the AllPermission on
   jdk 1.5
   Bugzilla report 41776
 
 * Replace task summary output incorrect.
   Bugzilla report 41544
 
 * Dependset crashes ant when timestamp on files change during Dependset
   execution.
   Bugzilla report 41284
 
 * Bug in org.apache.tools.ant.types.resources.comparators.Date
   Bugzilla report 41411
 
 * <junit> in Ant 1.7.0 could throw NPE if no <classpath> was defined.
   Bugzilla report 41422.
 
 * In Ant 1.7.0, <fileset> in <javadoc> does not by default include only
   **/*.java as the documentation claims and earlier revisions did.
   Bugzilla report 41264.
 
 * SPI support in jar was broken.
   Bugzilla report 41201.
   
 * jsch-0.1.30 causes SCP task to hang
   Bugzilla report 41090.
 
 * Target from imported file listed twice in projecthelp.
   Bugzilla report 41226.
 
 * <sql> task double-expands properties if expandproperties is true,
   and expands properties if expandproperties is false.
   Bugzilla report 41204.
 
 * Rolling back Bugzilla 32927 (set a default description for a javadoc tag
   if not set) as it caused a BC problem.
   Bugzilla report 41268.
 
 * <apt> forks properly and so memory settings are picked up.
   Bug report 41280.
 
 * Regression: NPE was thrown when using <pathconvert> against a
   (third-party instantiated) fileset with null Project reference.
 
 * Strip out all -J arguments to non forking rmic adapters, specifically
   the Sun and Weblogic compilers.
   Bug report 41349
 
 * Synchonization issues in PropertyHelper.  Bugzilla 41353.
 
 * <concat binary="true" append="true"> did not append.  Bugzilla 41399.
  
 * -autoproxy turns Java1.5+ automatic proxy support on. Bugzilla 41904
 
 * Handle null result of system getProperty(). Bugzilla 42334.
 
 * Regression: concat fixlastline="true" should not have applied to
   nested text, but did in Ant 1.7.0. Bugzilla 42369.
 
 * Regression: ant.version was not passed down in <ant>, <subant>.
   This worked in Ant 1.6.5, but not in 1.7.0.
   ant.core.lib (added in 1.7.0) was also not being propagated.
   Bugzilla bug 42263
 
 * Regression: bzip2 task created corrupted output files for some inputs.
   Bugzilla bug 41596.
 
 * Regression: <available> with <filepath> did not work.
   Bugzilla 42735.
 
 * ant script, cd may output to stdout.
   Bugzilla 42739.
 
 * Modified selector doesn't update the cache if only one file has changed.
   Bugzilla 42802.
 
 * Regression: Path subclasses that overrode list() stopped working in
   resourceCollection contexts in Ant 1.7.0. Bugzilla 42967.
 
 * <property> supports loading from xml based property definition.
   Bugzilla 42946
 
 * <junit> supports collecting and rerunning failed test cases
   (textXXX methods). Bugzilla 42984.  
 
 * War task failed with "No WEB-INF/web.xml file was added" when called
   a second time. Bugzilla 43121.
 
 * FilterMapper could throw an NPE.
   Bugzilla 43292.
 
 * Regession nested macrodefs with elements could cause StackOverFlow.
   Bugzilla 43324.
 
 * Some changes to <junit> broke third party tasks that extend it (like
   Apache Cactus' Ant task).  The changes have been modified so that
   subclases should now work again - without any changes to the
   subclass.
 
 Other changes:
 --------------
 
 * Various small optimizations speed up common tasks such as <javac> on large
   filesets, reducing both I/O and CPU usage.
 
 * Profiling logger has been added with basic profiling capabilities.
 
 * <script> now has basic support for JavaFX scripts
 
 * SSH task can now take a command parameter containing the commands to execute.
   This allows you to connect to a server and execute a number of commands
   without constantly reconnecting for each command.
 
 * Upgraded XML API to XML commons version 1.3.04.
 
 * Upgraded to Xerces 2.9.0
 
 * <script> can now work with bsf.jar and js.jar in its <classpath>.
 
 * add errorProperty and updatedProperty to <javac>
   Bugzilla 35637 and 28941.
 
 * add classpathref attribute to <whichresource>
   Bugzilla 41158.
 
 * reduce logging noise of <apply skipemptyfilesets="true">
   Bugzilla 29154
 
 * Show Previous Revision in the tagdiff.xsl stylesheet
   Bugzilla 29143
 
 * Allow <mapper refid> to refer directly to a FileNameMapper instance.
 
 * If you try and use a type in a namespace (or an antlib), and the type is not
   recognized but there are other definitions in that namespace, Ant lists what
   the known definitions are. This helps you find spelling errors.
 
 * Add a <last> resource collection, corresponding to <first>.
 
 * Add new <truncate> task.
 
 * <junitreport> xsl stylesheets allow setting the title used in <title> and <h1> tags by
   using <report><param> element.  Bugzilla 41742.
 
 * Add IgnoreDependenciesExecutor for weird cases when the user wants to run
   only the targets explicitly specified.
 
 * Patternset allows nested inverted patternsets using <invert>.
 
 * <manifest> checks for validity of attribute names.
 
 * JUnitVersionHelper.getTestCaseClassName is now public. Bugzilla 42231
 
 * <string> resource supports nested text. Bugzilla bug 42276
 
 * <scriptdef> now sources scripts from nested resources/resource collections. This lets you
   define scripts in JARs, remote URLs, or any other supported resource. Bugzilla report 41597.
 
 * <concat> is now usable as a single-element ResourceCollection.
 
 * It is now possible to provide the value of a <striplinecomments> filter's
   <comment> nested element as nested text instead of using the 'value'
   attribute.
 
 * A new logger, BigProjectLogger, lists the project name with every target   
 
 * Default text added to macrodef. Bugzilla report 42301.
 
 * "rawblobs" attribute added to SQL task.
 
 * Add new retry task container.
 
 * <jar> has a new strict attribute that checks if the jar complies with
   the jar packaging version specification.
 
 * <javac> has a new attribute - includeDestClasses.
   Bugzilla 40776.
 
 * <fileset> has a new attribute - errorOnMissingDir.
   Bugzilla 11270.
 
 * <javac> handles package-info.java files, there were repeatedly compiled.
   Bugzilla 43114.
 
 Changes from Ant 1.6.5 to Ant 1.7.0
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 * Initial support for JDK 6 (JSR 223) scripting.
   <*script*> tasks will now use javax.scripting if BSF is
   not available, or if explicitly requested by using
   a "manager" attribute.
 
 * Removed launcher classes from nodeps jar.
 
 * <classconstants> filter reader uses ISO-8859-1 encoding to read
   the java class file. Bugzilla report 33604.
 
 * Defer reference process. Bugzilla 36955, 34458, 37688.
   This may break build files in which a reference was set in a target which was
   never executed. Historically, Ant would set the reference early on, during parse
   time, so the datatype would be defined. Now it requires the reference to have
   been in a bit of the build file which was actually executed. If you get
   an error about an undefined reference, locate the reference and move it somewhere
   where it is used, or fix the depends attribute of the target in question to
   depend on the target which defines the reference/datatype.
   As a result of testing on real live build scripts, a fall-back mechanism
   was put it place to allow references that are out-of-band to be resolved. If
   this happens a big warning message is logged. This fall-back mechanism will
   be removed in Ant 1.8.0.
 
 * <script> and <scriptdef> now set the current thread context.
 
 * Unrestrict the dbvendor names in the websphere element of the ejbjar task.
   Bugzilla Report 40475.
 
 * <env> nested element in <java>, <exec> and others is now case-insensitive
   for windows OS. Bugzilla Report 28874.
 
 * Removed support for xalan1 completely. Users of Xalan1 for Ant builds will
   have to stay at ant 1.6.5 or upgrade to xalan2.
 
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
 
 * The <java fork="false"> now as per default installs a security manager
   using the default permissions. This is now independent of the
   failonerror attribute.  Bugzilla report 33361.
 
 * <signjar> now notices when the jar and signedjar are equal, and switches
   to the same dependency logic as when signedjar is omitted. This may break
   something that depended upon signing in this situation. However, since
   invoking the JDK jarsigner program with -signedjar set to the source jar
   actually crashes the JVM on our (Java1.5) systems, we don't think any
   build files which actually worked will be affected by the change.
 
 * <signjar> used to ignore a nested fileset when a jar was also provided as an
   attribute, printing a warning message; now it signs files in the fileset.
 
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
 
 * Support for the XSL:P XML parser has been removed.
   Bugzilla Report 23455.
 
 * Visual Age for Java optional tasks removed as the required library is no
   longer available.
 
 * Testlet (test) optional task removed as the required library is no
   longer available.
 
 * IContract optional task removed as the required library is no
   longer available.
 
 * Metamata (maudit, mmetrics, and mparse tasks) removed as the required 
   library is no longer available.
 
 * Sitraka (jpcoverage, jpcovmerge, jpcovreport) tasks suppressed as the 
   required library is no longer available.
 
 * <fixcrlf> used \r (Mac) line endings on OS X, whose proper line separator
   is \n (Unix).  Bugzilla report 39585.
 
 * <scp> now optionally supports the sftp protocol, you may need a
   newer jsch.jar.  Bugzilla Report 39373.
 
 * Ant launcher program prints errors to stderr, and exits with a 2 exit code
   value if, for any reason, it cannot actually start Ant proper. This will only
   affect programs/scripts that called the launcher and which did not want to
   receive an error if Ant itself would not start
 
 * All .NET tasks are now deprecated in favor of the new .NET Antlib:
   http://ant.apache.org/antlibs/dotnet/index.html
 
 Fixed bugs:
 -----------
 * Directory deletion did not work properly.
   Bugzilla 40972.
 
 * docletpath attribute of javadoc was ignored.
   Bugzilla 40900.
 
 * Fixed incorrect recursion in DOMUtil.listChildNodes().
   Bugzilla 40918.
 
 * CompressedResource.compareTo() did not
   take comparison with another CompressedResource into account.
   Bugzilla 40949.
 
 * Avoid possible NPE in Jar.java.
   Bugzilla 40847.
 
 * regression in attribute prefix (+ others) for refid in zipfileset and tarfileset.
   Bugzilla 41004, 30498.
 
 * dependset failed if the basedir of a target fileset did not exist.
   Bugzilla 40916.
 
 * Recursive filtering encountered NullPointerExceptions under certain
   circumstances.  Bugzilla 41086.
 
 * XmlProperty overrides previously set property value when handling duplicate
   elements. Bugzilla 41080.
 
 * Having many tasks causes OOM.  Bugzilla 41049.
 
 * Regression: <path> was evaluating nested content only once, so that it could
   not e.g. pick up files that didn't exist the first time through.
   Bugzilla 41151.
 
 * OOM caused by IH holding on to classes and thus their classloaders.
   Bugzilla 28283 and 33061.
 
 * <delete> doesnt delete when defaultexcludes="false" and no includes is set
   fixed. Bugzilla 40313.
 
 * Behavior change of DirectoryScanner/AbstractFileset when conditional include
   patterns are used. Bugzilla 40722.
 
 * <javac> fails with NPE when compiling with eclipse ecj 3.1.x.
   Bugzilla 40839.
 
 * JUnitTestRunner had a NPE when unable to create parser, the exception
   containing the error did not get reported. Bugzilla 36733.
 
 * <checksum> with file and todir option failed. Bugzilla report 37386.
 
 * <path location="loc"> was broken (Regression from beta1).
   Bugzilla report 40547.
 
 * Nested fileset in <cab> did not work. Bugzilla report 39439.
 
 * The ant wrapper script should now correctly locate the java
   executable in more recent IBM JDKs for AIX as well.
 
 * URLResource did not close jar files, and also did not disconnect HTTPConnection (s).
 
 * Error calling junitreport. Bugzilla 40595.
 
 * <junittask/> created junitvmwatcher*.properties files but did not close and delete them.
 
 * <xmlproperty> did not create properties for empty leaf elements.
   Bugzilla report 26286.
 
 * UnknownElement.maybeConfigure always configured.
   Bugzilla report 40641.
 
 * No check for refid when prefix attribute is set in zipfileset.
   Bugzilla report 30498.
 
 * Fix for junit4 issue introduced since beta2.
   Bugzilla report 40682.
 
 * Error in duplicate project name with <import> and <antcall>.
   Bugzilla report 39920.
 
 * junit4 did not work with fork=no and junit4 in $ANT_HOME/lib.
   Bugzilla report 40697.
 
 * PathConvert on Windows should process forward and back slashes equivalently.
   Bugzilla report 32884.
 
 * ant.bat now looks in %USERPROFILE% and %HOMEDRIVE%%HOMEPATH% in addition to
   %HOME% for pre/post batch files. Bugzilla report 39298.
 
 * The inheritance hierarchy of the legacy <path> type was changed; code built
   against Ant 1.7 would therefore no longer execute on older versions of Ant.
   Since <path> is historically heavily used this was undesirable, and since it
   is also avoidable, the change to <path>'s taxonomy was reverted.
 
 * <zip filesonly="true"> included empty directories.  Bugzilla report 40258.
 
 * Invalid hash code of Target causes XmlLogger to fail.
   Bugzilla report 40207.
 
 * Macro element did not include top level Text. Bugzilla report 36803.
 
 * AntClassLoader did not isolate resources when isolate was set. Bugzilla report 38747.
 
 * Diagnostics broken when using java 1.4. Bugzilla report 40395.
 
 * Exception reporting in <copy> was broken. Bugzilla report 40300.
 
 * Handling of corrupt tar files, TarInputStream.read() never returns EOF.
   Bugzilla report 39924.
 
 * Some bugs in ReaderInputStream. Bugzilla report 39635.
 
 * <antlr> did not recognise whether the target is up-to-date for html option.
   Bugzilla report 38451.
 
 * Documented minimal version of jsch now 0.1.29.
   Bugzilla report 40333.
 
 * <available> searched parent directories for files.
   Bugzilla report 37148.
 
 * The build could be halted if a file path contained more ".." components than
   the actual depth of the preceding path. Now such paths are left
   alone (meaning they will likely be treated as nonexistent
   files). Bugzilla Report 40281.
 
 * Converting a <dirset> to a string was broken. Bugzilla Report 39683.
 
 * Manifests have improved line length handling, taking care of encoding.
   Bug reports 37548 / 34425.
 
 * <manifest> now closes the inputstream explicitly. Bug report 39628.
 
 * <rpm> now also correctly searches the first element of the path.
   Bug report 39345.
 
 * ant.bat now handles classpath set to "". Bug report 38914.
 
 * <junit> now supports JUnit 4. Bugzilla Report 38811.
 
 * <junit> can now work with junit.jar in its <classpath>. Bugzilla
   Report 38799.
 
 * Some potential NullPointerExceptions, Bugzilla Reports 37765 and 38056.
 
 * Problem when adding multiple filter files, Bugzilla Report 37341.
 
 * Problem referencing jars specified by Class-Path attribute in manifest
   of a ant task jar file, when this ant task jar file is located in
   a directory with space, Bugzilla Report 37085.
 
 * Backward incompatible change in ZipFileSet, Bugzilla Report 35824.
 
 * Wrong replacement of file separator chars prevens junitbatchtest
   from running correctly on files from a zipfileset. Bugzilla Report 35499.
 
 * Calling close twice on ReaderInputStream gave a nullpointer exception.
   Bugzilla Report 35544.
 
 * Memory leak from IntrospectionHelper.getHelper(Class) in embedded
   environments. Bugzilla Report 30162.
 
 * Translate task does not remove tokens when a key is not found.
   It logs a verbose message.  Bugzilla Report 13936.
 
 * Incorrect task name with invalid "javac" task after a "presetdef".
   Bugzilla reports 31389 and 29499.
 
 * <manifest> was not printing warnings about invalid manifest elements.
   Bugzilla report 32190.
 
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
   an element starting with . Bugzilla report 33770.
 
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
   fileset, and so validate multiple files properly. Bugzilla Report 32791.
 
 * <tar> / <untar> now accepts files upto 8GB, <tar> gives an error if larger
   files are to be included. This is the POSIX size limit.
 
 * <junitreport> removed line-breaks from stack-traces.  Bugzilla
   Report 34963.
 
 * Off-by-one error in environment setup for execution under OpenVMS fixed.
 
 * Bugzilla report 36171: -noclasspath crashes ant if no system
   classpath is set.
 
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
 
 * Redirector called Thread.sleep in a synchronized block. Bugzilla
   report 37767.
 
 * CCUnlock's objselect attribute could exhibit unpredictable behavior;
   standardized improperly included objselect and objsel property accessors to
   delegate to the inherited objSelect property accessor. Bugzilla report 37766.
 
 * <unzip> and <untar> now correctly merge multiple nested patternsets.
   Bugzilla Report 38973.
 
 * On case-insensitive filesystems, a <move> to change filename case
   erroneously deleted the "destination" file before attempting to rename
   the source file.  Bugzilla 37701.
 
 * <scp> can now handle uris with @s other than the final one denoting the
   domain.  Bugzilla 38082.
 
 * If the class invoked by the <java> task threw a ClassNotFoundException,
   this was misinterpreted as the specified class itself not being found.
 
 * <echoproperties> setPrefix javadoc claimed null or empty prefix would be
   ignored; instead an error was thrown.  Bugzilla report 39954.
 
 * <get> would fetch files that were up to date, because it used > in a
   remote/local timestamp comparison, not >=. Bugzilla 35607.
 
 * <xslt> passes the current file (name + directory) to the
   stylesheet/transformation.  xsl-parameter name is configurable.
   Bugzilla report 21042.
 
 * The <zip> API allowed creation of directories in file-only archives; a
   habitual offender was the subclassed <jar>, which included META-INF/ in
   the destination file regardless of whether filesonly was set to true.
 
 * <rmic> has a new adapter, xnew, to use the -XNew back end on java1.5+.
   By forking rmic, this works on java1.6+. Bugzilla report 38732.
 
 * Copy of UnknownElement in macroinstance was not recursive.
   Bugzilla report 40238.
 
 * Mixing of add and addConfigured methods in Mapper/ChainedMapper
   causes incorrect chaining. Bugzilla report 40228.
 
 Other changes:
 --------------
 
 * Warn user when a reference in the form "${refid}" cannot be resolved as this
   is a sign they probably meant "refid" (misuse of property expansion syntax).
 
 * Add dtd to javadoc for junit.
   Bugzilla 40754.
 
 * Add quiet attribute to loadfile/resource.
   Bugzilla 38249.
 
 * Make Locator#fromURI also append the drive letter when running under Windows
   with JDK 1.3 or 1.2.
 
 * Do not uppercase the drive letters systematically in FileUtils#normalize.
 
 * Java 5 enumerations may now be used as values in XML attributes in place of
   EnumeratedAttribute. Bugzilla 41058.
 
 * Create a pom file for ant-testutil and add ant-testutil.jar to the ant
   distribution. Bugzilla 40980.
 
 * Roll back automatic proxy enabling on Java 1.5. It broke things like
   Oracle JDBC drivers, and Ant itself on IBM's JVM on AIX, and didnt
   seem to work to well the rest of the time.
   To enable the feature, use the -autoproxy command line option.
 
 * Upgraded XML API and parser to Xerces 2.8.1
 
 * A code review of some threaded logic has tightened up the synchronization
   of Watchdog, ExecuteWatchdog and ExecuteJava, which could reduce the occurence
   of race conditions here, especially on Java1.5+.
 
 * Allow broken reference build files. The defer reference processing would
   break too many files - so allow them with a warning.
 
 * Removed dependency on sun.misc.UUEncoder for UUMailer.
 
 * Added regex attribute to the echoproperties task.
   Bugzilla 40019.
 
 * <war> task now allows you to omit the web.xml file. as this is optional
   in the servlet 2.5 and Java EE 5 APIs. set needxmlfile="false" to
   avoid a missing web.xml file from halting the build.
 
 * Diagnostics catches and logs security exceptions when accessing system properties.
 
 * <javadoc> useexternalfile now applies to all command line arguments
   of javadoc. Bugzilla report 40852.
 
 * javadoc/tag@description is now set to the name if description is
   not specified. Bugzill report 32927.
 
 * Some performance improvements, including Bugzilla report 25778.
 
 * Add <matches> condition. Bugzilla report 28883.
 
 * Extending JAR-Task for SPI. Bugzilla report 31520.
 
 * Added <tokens> resource collection for convenient creation of string
   resources from other resources' content. Inspired by Bugzilla 40504.
 
 * Added <compare> resource selector to select resources based on the
   results of their comparison to other resources.
 
 * Added outputtoformatters attribute to <junit> to allow suppression
   of noisey tests. Bugzilla report 12817.
 
 * Log level of message 'Overriding previous definition of reference to'
   set to Verbose. Bugzilla report 17240.
 
 * Added setbeans attribute to <script> to allow <script>'s to be
   run without referencing all references.
   Bugzilla report 37688.
 
 * Added classpath attribute and nested element to <script> to allow
   the language jars to be specified in the build script.
   Bugzilla report 29676.
 
 * Trim the driver attribute on the <sql> task. Bugzilla report 21228.
 
 * Allow (jar) files as well as directories to be given to jdepend.
   Bugzilla report 28865.
 
 * Convert SplashTask to use NOT sun internal classes.
   Bugzilla report 35619.
 
 * Made PatternSet#hasPatterns public to allow custom filesets access.
   Bugzilla report 36772.
 
 * Added searchparents attribute to <available>. Bugzilla report 39549.
 
 * Tasks that don't extend Ant's Task class will now get the build file
   location reflected into a method of the signature void setLocation(Location)
   - if such a method exists.
 
 * Remove needless synchronization in DirectoryScanner.
   Bugzilla report 40237.
 
 * Improved recursion detection for lines with multiple matches of same token
   on a single line.  Bugzilla report 38456.
 
 * Task will now log correctly even if no project is set.
   Bugzilla report 38458.
 
 * Use alternative names for the command line arguments in javac. Bugzilla
   Report 37546.
 
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
 
 * A bug in SQLExec would prevent the execution of trailing,
   non-semicolon-delimited statements.  Bugzilla Report 37764.
 
 * InputHandler implementations may now call InputRequest.getDefaultValue()
   if they wish. The default handler uses this also. Bugzilla report 28621.
 
 * Took in bugzilla report 39320, "Simple code cleanups"
 
 * Improve compatibility with GNU Classpath and java versions prior to
   1.5. Bugzilla 39027.
 
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
   -nested <sysproperty> elements, which can be used for proxy setup
   and the like
 
 * The linecontains and linecontainsregexp filterreaders now support a
   negate attribute to select lines -not- containing specified text.
   Bugzilla Report 34374.
 
 * <os> condition adds "winnt" as a family which can be tested. This is
   all windows platforms other than the Win9x line or Windows CE.
 
 * <exec> (and hence, <apply> and any other derived classes) have an OsFamily
   attribute, which can restrict execution to a single OS family.
 
 * Added "backtrace" attribute to macrodef. Bugzilla report 27219.
 
 * Ant main provides some diagnostics if it ever sees a -cp or -lib option,
   as this is indicative of a script mismatch. Bugzilla report 34860
 
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 6c22eda99..307767869 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,1658 +1,1667 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 import java.util.Properties;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.WeakHashMap;
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.helper.DefaultExecutor;
 import org.apache.tools.ant.types.FilterSet;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Description;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.util.CollectionUtils;
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
 public class Project implements ResourceFactory {
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
      * @deprecated since 1.5.x.
      *             Use {@link JavaEnvUtils#JAVA_1_0} instead.
      */
     public static final String JAVA_1_0 = JavaEnvUtils.JAVA_1_0;
     /**
      * Version constant for Java 1.1 .
      *
      * @deprecated since 1.5.x.
      *             Use {@link JavaEnvUtils#JAVA_1_1} instead.
      */
     public static final String JAVA_1_1 = JavaEnvUtils.JAVA_1_1;
     /**
      * Version constant for Java 1.2 .
      *
      * @deprecated since 1.5.x.
      *             Use {@link JavaEnvUtils#JAVA_1_2} instead.
      */
     public static final String JAVA_1_2 = JavaEnvUtils.JAVA_1_2;
     /**
      * Version constant for Java 1.3 .
      *
      * @deprecated since 1.5.x.
      *             Use {@link JavaEnvUtils#JAVA_1_3} instead.
      */
     public static final String JAVA_1_3 = JavaEnvUtils.JAVA_1_3;
     /**
      * Version constant for Java 1.4 .
      *
      * @deprecated since 1.5.x.
      *             Use {@link JavaEnvUtils#JAVA_1_4} instead.
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
 
     /** Map of id references - used for indicating broken build files */
     private HashMap idReferences = new HashMap();
 
     /** the parent project for old id resolution (if inheritreferences is set) */
     private Project parentIdProject = null;
 
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
 
     /** lock object used when adding/removing listeners */
     private final Object listenersLock = new Object();
 
     /** List of listeners to notify of build events. */
     private volatile BuildListener[] listeners = new BuildListener[0];
 
     /** for each thread, record whether it is currently executing
         messageLogged */
     private final ThreadLocal isLoggingMessage = new ThreadLocal() {
             protected Object initialValue() {
                 return Boolean.FALSE;
             }
         };
 
     /**
      * The Ant core classloader--may be <code>null</code> if using
      * parent classloader.
      */
     private ClassLoader coreLoader = null;
 
     /** Records the latest task to be executed on a thread. */
     private final Map/*<Thread,Task>*/ threadTasks =
         Collections.synchronizedMap(new WeakHashMap());
 
     /** Records the latest task to be executed on a thread group. */
     private final Map/*<ThreadGroup,Task>*/ threadGroupTasks
         = Collections.synchronizedMap(new WeakHashMap());
 
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
      * Create and initialize a subproject. By default the subproject will be of
      * the same type as its parent. If a no-arg constructor is unavailable, the
      * <code>Project</code> class will be used.
      * @return a Project instance configured as a subproject of this Project.
      * @since Ant 1.7
      */
     public Project createSubProject() {
         Project subProject = null;
         try {
             subProject = (Project) (getClass().newInstance());
         } catch (Exception e) {
             subProject = new Project();
         }
         initSubProject(subProject);
         return subProject;
     }
 
     /**
      * Initialize a subproject.
      * @param subProject the subproject to initialize.
      */
     public void initSubProject(Project subProject) {
         ComponentHelper.getComponentHelper(subProject)
             .initSubProject(ComponentHelper.getComponentHelper(this));
         subProject.setDefaultInputStream(getDefaultInputStream());
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
         initProperties();
 
         ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
     }
 
     /**
      * Initializes the properties.
      * @exception BuildException if an vital property could not be set.
      * @since Ant 1.7
      */
     public void initProperties() throws BuildException {
         setJavaVersionProperty();
         setSystemProperties();
         setPropertyInternal(MagicNames.ANT_VERSION, Main.getAntVersion());
         setAntLib();
     }
 
     /**
      * Set a property to the location of ant.jar.
      * Use the locator to find the location of the Project.class, and
      * if this is not null, set the property {@link MagicNames#ANT_LIB}
      * to the result
      */
     private void setAntLib() {
         File antlib = org.apache.tools.ant.launch.Locator.getClassSource(
             Project.class);
         if (antlib != null) {
             setPropertyInternal(MagicNames.ANT_LIB, antlib.getAbsolutePath());
         }
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
         return new AntClassLoader(
             getClass().getClassLoader(), this, path);
     }
 
     /**
      * Factory method to create a class loader for loading classes from
      * a given path.
      *
      * @param parent the parent classloader for the new loader.
      * @param path the path from which classes are to be loaded.
      *
      * @return an appropriate classloader.
      */
     public AntClassLoader createClassLoader(
         ClassLoader parent, Path path) {
         return new AntClassLoader(parent, this, path);
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
     public void addBuildListener(BuildListener listener) {
         synchronized (listenersLock) {
             // If the listeners already has this listener, do nothing
             for (int i = 0; i < listeners.length; i++) {
                 if (listeners[i] == listener) {
                     return;
                 }
             }
             // copy on write semantics
             BuildListener[] newListeners =
                 new BuildListener[listeners.length + 1];
             System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
             newListeners[listeners.length] = listener;
             listeners = newListeners;
         }
     }
 
     /**
      * Remove a build listener from the list. This listener
      * will no longer be notified of build events for this project.
      *
      * @param listener The listener to remove from the list.
      *                 Should not be <code>null</code>.
      */
     public void removeBuildListener(BuildListener listener) {
         synchronized (listenersLock) {
             // copy on write semantics
             for (int i = 0; i < listeners.length; i++) {
                 if (listeners[i] == listener) {
                     BuildListener[] newListeners =
                         new BuildListener[listeners.length - 1];
                     System.arraycopy(listeners, 0, newListeners, 0, i);
                     System.arraycopy(listeners, i + 1, newListeners, i,
                                      listeners.length - i - 1);
                     listeners = newListeners;
                     break;
                 }
             }
         }
     }
 
     /**
          * Return a copy of the list of build listeners for the project.
          * 
          * @return a list of build listeners for the project
          */
     public Vector getBuildListeners() {
         synchronized (listenersLock) {
             Vector r = new Vector(listeners.length);
             for (int i = 0; i < listeners.length; i++) {
                 r.add(listeners[i]);
             }
             return r;
         }
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
         log(message, null, msgLevel);
     }
 
     /**
      * Write a project level message to the log with the given log level.
      * @param message The text to log. Should not be <code>null</code>.
      * @param throwable The exception causing this log, may be <code>null</code>.
      * @param msgLevel The log priority level to use.
      * @since 1.7
      */
     public void log(String message, Throwable throwable, int msgLevel) {
         fireMessageLogged(this, message, throwable, msgLevel);
     }
 
     /**
      * Write a task level message to the log with the given log level.
      * @param task The task to use in the log. Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The log priority level to use.
      */
     public void log(Task task, String message, int msgLevel) {
         fireMessageLogged(task, message, null, msgLevel);
     }
 
     /**
      * Write a task level message to the log with the given log level.
      * @param task The task to use in the log. Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param throwable The exception causing this log, may be <code>null</code>.
      * @param msgLevel The log priority level to use.
      * @since 1.7
      */
     public void log(Task task, String message, Throwable throwable, int msgLevel) {
         fireMessageLogged(task, message, throwable, msgLevel);
     }
 
     /**
      * Write a target level message to the log with the given log level.
      * @param target The target to use in the log.
      *               Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The log priority level to use.
      */
     public void log(Target target, String message, int msgLevel) {
         log(target, message, null, msgLevel);
     }
 
     /**
      * Write a target level message to the log with the given log level.
      * @param target The target to use in the log.
      *               Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param throwable The exception causing this log, may be <code>null</code>.
      * @param msgLevel The log priority level to use.
      * @since 1.7
      */
     public void log(Target target, String message, Throwable throwable,
             int msgLevel) {
         fireMessageLogged(target, message, throwable, msgLevel);
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
         PropertyHelper.getPropertyHelper(this).setProperty(name, value, true);
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
         PropertyHelper.getPropertyHelper(this).setNewProperty(name, value);
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
         PropertyHelper.getPropertyHelper(this).setUserProperty(name, value);
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
         PropertyHelper.getPropertyHelper(this).setInheritedProperty(name, value);
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
         PropertyHelper.getPropertyHelper(this).setProperty(name, value, false);
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
         Object value = PropertyHelper.getPropertyHelper(this).getProperty(propertyName);
         return value == null ? null : String.valueOf(value);
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
     public String replaceProperties(String value) throws BuildException {
         return PropertyHelper.getPropertyHelper(this).replaceProperties(null, value, null);
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
         return (String) PropertyHelper.getPropertyHelper(this).getUserProperty(propertyName);
     }
 
     /**
      * Return a copy of the properties table.
      * @return a hashtable containing all properties
      *         (including user properties).
      */
     public Hashtable getProperties() {
         return PropertyHelper.getPropertyHelper(this).getProperties();
     }
 
     /**
      * Return a copy of the user property hashtable.
      * @return a hashtable containing just the user properties.
      */
     public Hashtable getUserProperties() {
         return PropertyHelper.getPropertyHelper(this).getUserProperties();
     }
 
     /**
+     * Return a copy of the inherited property hashtable.
+     * @return a hashtable containing just the inherited properties.
+     * @since Ant 1.8.0
+     */
+    public Hashtable getInheritedProperties() {
+        return PropertyHelper.getPropertyHelper(this).getInheritedProperties();
+    }
+
+    /**
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
         PropertyHelper.getPropertyHelper(this).copyUserProperties(other);
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
         PropertyHelper.getPropertyHelper(this).copyInheritedProperties(other);
     }
 
     /**
      * Set the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      *
      * @deprecated since 1.5.x.
      *             Use setDefault.
      * @see #setDefault(String)
      */
     public void setDefaultTarget(String defaultTarget) {
         setDefault(defaultTarget);
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
         setUserProperty(MagicNames.PROJECT_DEFAULT_TARGET, defaultTarget);
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
         setUserProperty(MagicNames.PROJECT_NAME,  name);
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
      * @deprecated since 1.4.x.
      *             Use getGlobalFilterSet().addFilter(token,value)
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
      * @deprecated since 1.4.x
      *             Use getGlobalFilterSet().getFilterHash().
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
         setPropertyInternal(MagicNames.PROJECT_BASEDIR, this.baseDir.getPath());
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
      * @deprecated since 1.5.x.
      *             Use org.apache.tools.ant.util.JavaEnvUtils instead.
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
         setPropertyInternal(MagicNames.ANT_JAVA_VERSION, javaVersion);
 
         // sanity check
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_0)
                 || JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1))  {
             throw new BuildException("Ant cannot work on Java 1.0 / 1.1");
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
             if (value != null) {
                 this.setPropertyInternal(propertyName, value);
             }
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
         addReference(MagicNames.ANT_EXECUTOR_REFERENCE, e);
     }
 
     /**
      * Get this Project's Executor (setting it if necessary).
      * @return an Executor instance.
      */
     public Executor getExecutor() {
         Object o = getReference(MagicNames.ANT_EXECUTOR_REFERENCE);
         if (o == null) {
             String classname = getProperty(MagicNames.ANT_EXECUTOR_CLASSNAME);
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
         setUserProperty(MagicNames.PROJECT_INVOKED_TARGETS,
                         CollectionUtils.flattenToString(names));
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
      * Return the canonical form of a filename.
      * <p>
      * If the specified file name is relative it is resolved
      * with respect to the given root directory.
      *
      * @param fileName The name of the file to resolve.
      *                 Must not be <code>null</code>.
      *
      * @param rootDir  The directory respective to which relative file names
      *                 are resolved. May be <code>null</code>, in which case
      *                 the current directory is used.
      *
      * @return the resolved File.
      *
      * @deprecated since 1.4.x
      */
     public File resolveFile(String fileName, File rootDir) {
         return FILE_UTILS.resolveFile(rootDir, fileName);
     }
 
     /**
      * Return the canonical form of a filename.
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
         return FILE_UTILS.resolveFile(baseDir, fileName);
     }
 
     /**
      * Translate a path into its native (platform specific) format.
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
      * @deprecated since 1.7
      *             Use FileUtils.translatePath instead.
      *
      * @see PathTokenizer
      */
     public static String translatePath(String toProcess) {
         return FileUtils.translatePath(toProcess);
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(String sourceFile, String destFile)
           throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile);
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering)
         throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(File sourceFile, File destFile) throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile);
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
      * @exception IOException if the copying fails.
      *
      * @deprecated since 1.4.x
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering)
         throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
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
      * @deprecated since 1.4.x
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
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
      * @deprecated since 1.4.x
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         FILE_UTILS.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite, preserveLastModified);
     }
 
     /**
      * Call File.setLastModified(long time) on Java above 1.1, and logs
      * a warning on Java 1.1.
      *
      * @param file The file to set the last modified time on.
      *             Must not be <code>null</code>.
      *
      * @param time the required modification time.
      *
      * @deprecated since 1.4.x
      *
      * @exception BuildException if the last modified time cannot be set
      *                           despite running on a platform with a version
      *                           above 1.1.
      */
     public void setFileLastModified(File file, long time)
          throws BuildException {
         FILE_UTILS.setFileLastModified(file, time);
         log("Setting modification time for " + file, MSG_VERBOSE);
     }
diff --git a/src/main/org/apache/tools/ant/PropertyHelper.java b/src/main/org/apache/tools/ant/PropertyHelper.java
index 02a621167..cc87d1df4 100644
--- a/src/main/org/apache/tools/ant/PropertyHelper.java
+++ b/src/main/org/apache/tools/ant/PropertyHelper.java
@@ -1,978 +1,989 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 
 import java.text.ParsePosition;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import java.util.Vector;
 import java.util.Enumeration;
 import java.util.Collection;
 
 import org.apache.tools.ant.property.NullReturn;
 import org.apache.tools.ant.property.GetProperty;
 import org.apache.tools.ant.property.ParseNextProperty;
 import org.apache.tools.ant.property.PropertyExpander;
 import org.apache.tools.ant.property.ParseProperties;
 
 /* ISSUES:
  - ns param. It could be used to provide "namespaces" for properties, which
  may be more flexible.
  - Object value. In ant1.5 String is used for Properties - but it would be nice
  to support generic Objects (the property remains immutable - you can't change
  the associated object). This will also allow JSP-EL style setting using the
  Object if an attribute contains only the property (name="${property}" could
  avoid Object->String->Object conversion)
  - Currently we "chain" only for get and set property (probably most users
  will only need that - if they need more they can replace the top helper).
  Need to discuss this and find if we need more.
  */
 
 /* update for impending Ant 1.8:
 
    - I can't see any reason for ns and would like to deprecate it.
    - Replacing chaining with delegates for certain behavioral aspects.
    - Object value seems valuable as outlined.
 
  */
 
 /** NOT FINAL. API MAY CHANGE
  *
  * Deals with properties - substitution, dynamic properties, etc.
  *
  * This is the same code as in Ant1.5. The main addition is the ability
  * to chain multiple PropertyHelpers and to replace the default.
  *
  * @since Ant 1.6
  */
 public class PropertyHelper implements GetProperty {
 
     //  --------------------------------------------------------
     //
     //    The property delegate interfaces
     //
     //  --------------------------------------------------------
 
     /**
      * Marker interface for a PropertyHelper delegate.
      * @since Ant 1.8
      */
     public interface Delegate {
     }
 
     /**
      * Describes an entity capable of evaluating a property name for value.
      * @since Ant 1.8
      */
     public interface PropertyEvaluator extends Delegate {
         /**
          * Evaluate a property.
          * @param property the property's String "identifier".
          * @param propertyHelper the invoking PropertyHelper.
          * @return Object value.
          */
         Object evaluate(String property, PropertyHelper propertyHelper);
     }
 
     /**
      * Describes an entity capable of setting a property to a value.
      * @since Ant 1.8
      */
     public interface PropertySetter extends Delegate {
         /**
          * Set a *new" property.
          * @param property the property's String "identifier".
          * @param value    the value to set.
          * @param propertyHelper the invoking PropertyHelper.
          * @return true if this entity 'owns' the property.
          */
         boolean setNew(
             String property, Object value, PropertyHelper propertyHelper);
 
         /**
          * Set a property.
          * @param property the property's String "identifier".
          * @param value    the value to set.
          * @param propertyHelper the invoking PropertyHelper.
          * @return true if this entity 'owns' the property.
          */
         boolean set(
             String property, Object value, PropertyHelper propertyHelper);
     }
 
 
     //  --------------------------------------------------------
     //
     //    The predefined property delegates
     //
     //  --------------------------------------------------------
 
     private static final PropertyEvaluator TO_STRING = new PropertyEvaluator() {
         private String prefix = "toString:";
         public Object evaluate(String property, PropertyHelper propertyHelper) {
             Object o = null;
             if (property.startsWith(prefix) && propertyHelper.getProject() != null) {
                 o = propertyHelper.getProject().getReference(property.substring(prefix.length()));
             }
             return o == null ? null : o.toString();
         }
     };
 
     private static final PropertyExpander DEFAULT_EXPANDER = new PropertyExpander() {
         public String parsePropertyName(
             String s, ParsePosition pos, ParseNextProperty notUsed) {
             int index = pos.getIndex();
             if (s.indexOf("${", index) == index) {
                 int end = s.indexOf('}', index);
                 if (end < 0) {
                     throw new BuildException("Syntax error in property: " + s);
                 }
                 int start = index + 2;
                 pos.setIndex(end + 1);
                 return s.substring(start, end);
             }
             return null;
         }
     };
 
     /** dummy */
     private static final PropertyExpander SKIP_DOUBLE_DOLLAR
         = new PropertyExpander() {
             // CheckStyle:LineLengthCheck OFF see too long
             /**
              * {@inheritDoc}
              * @see org.apache.tools.ant.PropertyHelper.PropertyExpander#parsePropertyName(java.lang.String, java.text.ParsePosition, org.apache.tools.ant.PropertyHelper)
              */
             // CheckStyle:LineLengthCheck ON
             public String parsePropertyName(
                 String s, ParsePosition pos, ParseNextProperty notUsed) {
                 //System.out.println("parseproperty " + s);
                 int index = pos.getIndex();
                 if (s.indexOf("$$", index) == index) {
                     pos.setIndex(++index);
                 }
                 return null;
             }
         };
 
     private Project project;
     private PropertyHelper next;
     private Hashtable delegates = new Hashtable();
 
     /** Project properties map (usually String to String). */
     private Hashtable properties = new Hashtable();
 
     /**
      * Map of "user" properties (as created in the Ant task, for example).
      * Note that these key/value pairs are also always put into the
      * project properties, so only the project properties need to be queried.
      */
     private Hashtable userProperties = new Hashtable();
 
     /**
      * Map of inherited "user" properties - that are those "user"
      * properties that have been created by tasks and not been set
      * from the command line or a GUI tool.
      */
     private Hashtable inheritedProperties = new Hashtable();
 
     /**
      * Default constructor.
      */
     protected PropertyHelper() {
         add(TO_STRING);
         add(SKIP_DOUBLE_DOLLAR);
         add(DEFAULT_EXPANDER);
     }
 
     //  --------------------------------------------------------
     //
     //    Some helper static methods to get and set properties
     //
     //  --------------------------------------------------------
 
     /**
      * A helper static method to get a property
      * from a particular project.
      * @param project the project in question.
      * @param name the property name
      * @return the value of the property if present, null otherwise.
      * @since Ant 1.8
      */
     public static Object getProperty(Project project, String name) {
         return PropertyHelper.getPropertyHelper(project)
             .getProperty(name);
     }
 
     /**
      * A helper static method to set a property
      * from a particular project.
      * @param project the project in question.
      * @param name the property name
      * @param value the value to use.
      * @since Ant 1.8
      */
     public static void setProperty(Project project, String name, Object value) {
         PropertyHelper.getPropertyHelper(project)
             .setProperty(name, value, true);
     }
 
     /**
      * A helper static method to set a new property
      * from a particular project.
      * @param project the project in question.
      * @param name the property name
      * @param value the value to use.
      * @since Ant 1.8
      */
     public static void setNewProperty(
         Project project, String name, Object value) {
         PropertyHelper.getPropertyHelper(project)
             .setNewProperty(name, value);
     }
 
     //override facility for subclasses to put custom hashtables in
 
     // --------------------  Hook management  --------------------
 
     /**
      * Set the project for which this helper is performing property resolution.
      *
      * @param p the project instance.
      */
     public void setProject(Project p) {
         this.project = p;
     }
 
     /**
      * Get this PropertyHelper's Project.
      * @return Project
      */
     public Project getProject() {
         return project;
     }
 
     /**
      *  There are 2 ways to hook into property handling:
      *  - you can replace the main PropertyHelper. The replacement is required
      * to support the same semantics (of course :-)
      *
      *  - you can chain a property helper capable of storing some properties.
      *  Again, you are required to respect the immutability semantics (at
      *  least for non-dynamic properties)
      *
      * @param next the next property helper in the chain.
      * @deprecated
      */
     public void setNext(PropertyHelper next) {
         this.next = next;
     }
 
     /**
      * Get the next property helper in the chain.
      *
      * @return the next property helper.
      * @deprecated
      */
     public PropertyHelper getNext() {
         return next;
     }
 
     /**
      * Factory method to create a property processor.
      * Users can provide their own or replace it using "ant.PropertyHelper"
      * reference. User tasks can also add themselves to the chain, and provide
      * dynamic properties.
      *
      * @param project the project for which the property helper is required.
      *
      * @return the project's property helper.
      */
     public static synchronized PropertyHelper getPropertyHelper(Project project) {
         PropertyHelper helper
                 = (PropertyHelper) project.getReference(MagicNames.REFID_PROPERTY_HELPER);
         if (helper != null) {
             return helper;
         }
         helper = new PropertyHelper();
         helper.setProject(project);
 
         project.addReference(MagicNames.REFID_PROPERTY_HELPER, helper);
         return helper;
     }
 
     /**
      * Get the expanders.
      * @return the expanders.
      */
     public Collection getExpanders() {
         return getDelegates(PropertyExpander.class);
     }
 
 
     // --------------------  Methods to override  --------------------
 
     /**
      * Sets a property. Any existing property of the same name
      * is overwritten, unless it is a user property. Will be called
      * from setProperty().
      *
      * If all helpers return false, the property will be saved in
      * the default properties table by setProperty.
      *
      * @param ns   The namespace that the property is in (currently
      *             not used.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @param inherited True if this property is inherited (an [sub]ant[call] property).
      * @param user      True if this property is a user property.
      * @param isNew     True is this is a new property.
      * @return true if this helper has stored the property, false if it
      *    couldn't. Each helper should delegate to the next one (unless it
      *    has a good reason not to).
      * @deprecated PropertyHelper chaining is deprecated.
      */
     public boolean setPropertyHook(String ns, String name,
                                    Object value,
                                    boolean inherited, boolean user,
                                    boolean isNew) {
         if (getNext() != null) {
             boolean subst = getNext().setPropertyHook(ns, name, value, inherited, user, isNew);
             // If next has handled the property
             if (subst) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Get a property. If all hooks return null, the default
      * tables will be used.
      *
      * @param ns namespace of the sought property.
      * @param name name of the sought property.
      * @param user True if this is a user property.
      * @return The property, if returned by a hook, or null if none.
      * @deprecated PropertyHelper chaining is deprecated.
      */
     public Object getPropertyHook(String ns, String name, boolean user) {
         if (getNext() != null) {
             Object o = getNext().getPropertyHook(ns, name, user);
             if (o != null) {
                 return o;
             }
         }
         // Experimental/Testing, will be removed
         if (name.startsWith("toString:")) {
             name = name.substring("toString:".length());
             Object v = project.getReference(name);
             return (v == null) ? null : v.toString();
         }
         return null;
     }
 
     // -------------------- Optional methods   --------------------
     // You can override those methods if you want to optimize or
     // do advanced things (like support a special syntax).
     // The methods do not chain - you should use them when embedding ant
     // (by replacing the main helper)
 
     /**
      * Parses a string containing <code>${xxx}</code> style property
      * references into two lists. The first list is a collection
      * of text fragments, while the other is a set of string property names.
      * <code>null</code> entries in the first list indicate a property
      * reference from the second list.
      *
      * It can be overridden with a more efficient or customized version.
      *
      * @param value     Text to parse. Must not be <code>null</code>.
      * @param fragments List to add text fragments to.
      *                  Must not be <code>null</code>.
      * @param propertyRefs List to add property names to.
      *                     Must not be <code>null</code>.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @deprecated We can do better than this.
      */
     public void parsePropertyString(String value, Vector fragments,
                                     Vector propertyRefs) throws BuildException {
         parsePropertyStringDefault(value, fragments, propertyRefs);
     }
 
     /**
      * Replaces <code>${xxx}</code> style constructions in the given value
      * with the string value of the corresponding data types.
      *
      * @param ns    The namespace for the property.
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>, in which case this
      *              method returns immediately with no effect.
      * @param keys  Mapping (String to String) of property names to their
      *              values. If <code>null</code>, only project properties will
      *              be used.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @return the original string with the properties replaced, or
      *         <code>null</code> if the original string is <code>null</code>.
      */
     public String replaceProperties(String ns, String value, Hashtable keys) throws BuildException {
         return replaceProperties(value);
     }
 
     /**
      * Replaces <code>${xxx}</code> style constructions in the given value
      * with the string value of the corresponding data types.
      *
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>, in which case this
      *              method returns immediately with no effect.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @return the original string with the properties replaced, or
      *         <code>null</code> if the original string is <code>null</code>.
      */
     public String replaceProperties(String value) throws BuildException {
         Object o = parseProperties(value);
         return o == null || o instanceof String ? (String) o : o.toString();
     }
 
     /**
      * Decode properties from a String representation.  If the entire
      * contents of the String resolve to a single property, that value
      * is returned.  Otherwise a String is returned.
      *
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>, in which case this
      *              method returns immediately with no effect.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @return the original string with the properties replaced, or
      *         <code>null</code> if the original string is <code>null</code>.
      */
     public Object parseProperties(String value) throws BuildException {
         return new ParseProperties(getProject(), getExpanders(), this)
             .parseProperties(value);
     }
 
     /**
      * Learn whether a String contains replaceable properties.
      * @param value the String to check.
      * @return <code>true</code> if <code>value</code> contains property notation.
      */
     public boolean containsProperties(String value) {
         return new ParseProperties(getProject(), getExpanders(), this)
             .containsProperties(value);
     }
 
     // -------------------- Default implementation  --------------------
     // Methods used to support the default behavior and provide backward
     // compatibility. Some will be deprecated, you should avoid calling them.
 
     /**
      * Default implementation of setProperty. Will be called from Project.
      * This is the original 1.5 implementation, with calls to the hook
      * added.
      * @param ns      The namespace for the property (currently not used).
      * @param name    The name of the property.
      * @param value   The value to set the property to.
      * @param verbose If this is true output extra log messages.
      * @return true if the property is set.
      * @deprecated namespaces are unnecessary.
      */
     public boolean setProperty(String ns, String name, Object value, boolean verbose) {
         return setProperty(name, value, verbose);
     }
 
     /**
      * Default implementation of setProperty. Will be called from Project.
      *  This is the original 1.5 implementation, with calls to the hook
      *  added.
      *  @param name    The name of the property.
      *  @param value   The value to set the property to.
      *  @param verbose If this is true output extra log messages.
      *  @return true if the property is set.
      */
     public boolean setProperty(String name, Object value, boolean verbose) {
         for (Iterator iter = getDelegates(PropertySetter.class).iterator(); iter.hasNext();) {
             PropertySetter setter = (PropertySetter) iter.next();
             if (setter.set(name, value, this)) {
                 return true;
             }
         }
         synchronized (this) {
             // user (CLI) properties take precedence
             if (userProperties.containsKey(name)) {
                 if (verbose) {
                     project.log("Override ignored for user property \"" + name + "\"",
                             Project.MSG_VERBOSE);
                 }
                 return false;
             }
             if (verbose) {
                 if (properties.containsKey(name)) {
                     project.log("Overriding previous definition of property \"" + name + "\"",
                             Project.MSG_VERBOSE);
                 }
                 project.log("Setting project property: " + name + " -> " + value, Project.MSG_DEBUG);
             }
             if (name != null && value != null) {
                 properties.put(name, value);
             }
             return true;
         }
     }
 
     /**
      * Sets a property if no value currently exists. If the property
      * exists already, a message is logged and the method returns with
      * no other effect.
      *
      * @param ns   The namespace for the property (currently not used).
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @since Ant 1.6
      * @deprecated namespaces are unnecessary.
      */
     public void setNewProperty(String ns, String name, Object value) {
         setNewProperty(name, value);
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
      * @since Ant 1.8
      */
     public void setNewProperty(String name, Object value) {
         for (Iterator iter = getDelegates(PropertySetter.class).iterator();
              iter.hasNext();) {
             PropertySetter setter = (PropertySetter) iter.next();
             if (setter.setNew(name, value, this)) {
                 return;
             }
         }
         synchronized (this) {
             if (properties.containsKey(name)) {
                 project.log("Override ignored for property \"" + name + "\"", Project.MSG_VERBOSE);
                 return;
             }
             project.log("Setting project property: " + name + " -> " + value, Project.MSG_DEBUG);
             if (name != null && value != null) {
                 properties.put(name, value);
             }
         }
     }
 
     /**
      * Sets a user property, which cannot be overwritten by
      * set/unset property calls. Any previous value is overwritten.
      * @param ns   The namespace for the property (currently not used).
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @deprecated namespaces are unnecessary.
      */
     public void setUserProperty(String ns, String name, Object value) {
         setUserProperty(name, value);
     }
 
     /**
      * Sets a user property, which cannot be overwritten by
      * set/unset property calls. Any previous value is overwritten.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      */
     public void setUserProperty(String name, Object value) {
         project.log("Setting ro project property: " + name + " -> " + value, Project.MSG_DEBUG);
         synchronized (this) {
             userProperties.put(name, value);
             properties.put(name, value);
         }
     }
 
     /**
      * Sets an inherited user property, which cannot be overwritten by set/unset
      * property calls. Any previous value is overwritten. Also marks
      * these properties as properties that have not come from the
      * command line.
      *
      * @param ns   The namespace for the property (currently not used).
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @deprecated namespaces are unnecessary.
      */
     public void setInheritedProperty(String ns, String name, Object value) {
         setInheritedProperty(name, value);
     }
 
     /**
      * Sets an inherited user property, which cannot be overwritten by set/unset
      * property calls. Any previous value is overwritten. Also marks
      * these properties as properties that have not come from the
      * command line.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      */
     public void setInheritedProperty(String name, Object value) {
         project.log("Setting ro project property: " + name + " -> " + value, Project.MSG_DEBUG);
 
         synchronized (this) {
             inheritedProperties.put(name, value);
             userProperties.put(name, value);
             properties.put(name, value);
         }
     }
 
     // -------------------- Getting properties  --------------------
 
     /**
      * Returns the value of a property, if it is set.  You can override
      * this method in order to plug your own storage.
      *
      * @param ns   The namespace for the property (currently not used).
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      * @deprecated namespaces are unnecessary.
      */
     public Object getProperty(String ns, String name) {
         return getProperty(name);
     }
 
     /**
      * Returns the value of a property, if it is set.  You can override
      * this method in order to plug your own storage.
      *
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
     public Object getProperty(String name) {
         if (name == null) {
             return null;
         }
         for (Iterator iter = getDelegates(PropertyEvaluator.class).iterator(); iter.hasNext();) {
             Object o = ((PropertyEvaluator) iter.next()).evaluate(name, this);
             if (o != null) {
                 if (o instanceof NullReturn) {
                     return null;
                 }
                 return o;
             }
         }
         return properties.get(name);
     }
 
     /**
      * Returns the value of a user property, if it is set.
      *
      * @param ns   The namespace for the property (currently not used).
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      * @deprecated namespaces are unnecessary.
      */
     public Object getUserProperty(String ns, String name) {
         return getUserProperty(name);
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
     public Object getUserProperty(String name) {
         if (name == null) {
             return null;
         }
         return userProperties.get(name);
     }
 
     // -------------------- Access to property tables  --------------------
     // This is used to support ant call and similar tasks. It should be
     // deprecated, it is possible to use a better (more efficient)
     // mechanism to preserve the context.
 
     /**
      * Returns a copy of the properties table.
      * @return a hashtable containing all properties (including user properties).
      */
     public Hashtable getProperties() {
         //avoid concurrent modification:
         synchronized (properties) {
             return new Hashtable(properties);
         }
         // There is a better way to save the context. This shouldn't
         // delegate to next, it's for backward compatibility only.
     }
 
     /**
      * Returns a copy of the user property hashtable
      * @return a hashtable containing just the user properties
      */
     public Hashtable getUserProperties() {
         //avoid concurrent modification:
         synchronized (userProperties) {
             return new Hashtable(userProperties);
         }
     }
 
     /**
+     * Returns a copy of the inherited property hashtable
+     * @return a hashtable containing just the inherited properties
+     */
+    public Hashtable getInheritedProperties() {
+        //avoid concurrent modification:
+        synchronized (inheritedProperties) {
+            return new Hashtable(inheritedProperties);
+        }
+    }
+
+    /**
      * special back door for subclasses, internal access to the hashtables
      * @return the live hashtable of all properties
      */
     protected Hashtable getInternalProperties() {
         return properties;
     }
 
     /**
      * special back door for subclasses, internal access to the hashtables
      *
      * @return the live hashtable of user properties
      */
     protected Hashtable getInternalUserProperties() {
         return userProperties;
     }
 
     /**
      * special back door for subclasses, internal access to the hashtables
      *
      * @return the live hashtable inherited properties
      */
     protected Hashtable getInternalInheritedProperties() {
         return inheritedProperties;
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
      * @since Ant 1.6
      */
     public void copyInheritedProperties(Project other) {
         //avoid concurrent modification:
         synchronized (inheritedProperties) {
             Enumeration e = inheritedProperties.keys();
             while (e.hasMoreElements()) {
                 String arg = e.nextElement().toString();
                 if (other.getUserProperty(arg) != null) {
                     continue;
                 }
                 Object value = inheritedProperties.get(arg);
                 other.setInheritedProperty(arg, value.toString());
             }
         }
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
      * @since Ant 1.6
      */
     public void copyUserProperties(Project other) {
         //avoid concurrent modification:
         synchronized (userProperties) {
             Enumeration e = userProperties.keys();
             while (e.hasMoreElements()) {
                 Object arg = e.nextElement();
                 if (inheritedProperties.containsKey(arg)) {
                     continue;
                 }
                 Object value = userProperties.get(arg);
                 other.setUserProperty(arg.toString(), value.toString());
             }
         }
     }
 
     // -------------------- Property parsing  --------------------
     // Moved from ProjectHelper. You can override the static method -
     // this is used for backward compatibility (for code that calls
     // the parse method in ProjectHelper).
 
     /**
      * Default parsing method. It is here only to support backward compatibility
      * for the static ProjectHelper.parsePropertyString().
      */
     static void parsePropertyStringDefault(String value, Vector fragments, Vector propertyRefs)
             throws BuildException {
         int prev = 0;
         int pos;
         //search for the next instance of $ from the 'prev' position
         while ((pos = value.indexOf("$", prev)) >= 0) {
 
             //if there was any text before this, add it as a fragment
             //TODO, this check could be modified to go if pos>prev;
             //seems like this current version could stick empty strings
             //into the list
             if (pos > 0) {
                 fragments.addElement(value.substring(prev, pos));
             }
             //if we are at the end of the string, we tack on a $
             //then move past it
             if (pos == (value.length() - 1)) {
                 fragments.addElement("$");
                 prev = pos + 1;
             } else if (value.charAt(pos + 1) != '{') {
                 //peek ahead to see if the next char is a property or not
                 //not a property: insert the char as a literal
                 /*
                 fragments.addElement(value.substring(pos + 1, pos + 2));
                 prev = pos + 2;
                 */
                 if (value.charAt(pos + 1) == '$') {
                     //backwards compatibility two $ map to one mode
                     fragments.addElement("$");
                     prev = pos + 2;
                 } else {
                     //new behaviour: $X maps to $X for all values of X!='$'
                     fragments.addElement(value.substring(pos, pos + 2));
                     prev = pos + 2;
                 }
             } else {
                 //property found, extract its name or bail on a typo
                 int endName = value.indexOf('}', pos);
                 if (endName < 0) {
                     throw new BuildException("Syntax error in property: " + value);
                 }
                 String propertyName = value.substring(pos + 2, endName);
                 fragments.addElement(null);
                 propertyRefs.addElement(propertyName);
                 prev = endName + 1;
             }
         }
         //no more $ signs found
         //if there is any tail to the file, append it
         if (prev < value.length()) {
             fragments.addElement(value.substring(prev));
         }
     }
 
     /**
      * Add the specified delegate object to this PropertyHelper.
      * Delegates are processed in LIFO order.
      * @param delegate the delegate to add.
      * @since Ant 1.8
      */
     public void add(Delegate delegate) {
         synchronized (delegates) {
             for (Iterator iter = getDelegateInterfaces(delegate).iterator(); iter.hasNext();) {
                 Object key = iter.next();
                 List list = (List) delegates.get(key);
                 if (list == null) {
                     list = new ArrayList();
                 } else {
                     list = new ArrayList(list);
                     list.remove(delegate);
                 }
                 list.add(0, delegate);
                 delegates.put(key, Collections.unmodifiableList(list));
             }
         }
     }
 
     /**
      * Get the Collection of delegates of the specified type.
      * 
      * @param type
      *            delegate type.
      * @return Collection.
      * @since Ant 1.8
      */
     protected List getDelegates(Class type) {
         List r = (List) delegates.get(type);
         return r == null ? Collections.EMPTY_LIST : r;
     }
 
     /**
      * Get all Delegate interfaces (excluding Delegate itself) from the specified Delegate.
      * @param d the Delegate to inspect.
      * @return Set<Class>
      * @since Ant 1.8
      */
     protected static Set getDelegateInterfaces(Delegate d) {
         HashSet result = new HashSet();
         Class c = d.getClass();
         while (c != null) {
             Class[] ifs = c.getInterfaces();
             for (int i = 0; i < ifs.length; i++) {
                 if (Delegate.class.isAssignableFrom(ifs[i])) {
                     result.add(ifs[i]);
                 }
             }
             c = c.getSuperclass();
         }
         result.remove(Delegate.class);
         return result;
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index 87bf4da54..6da7eb2bd 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,804 +1,829 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Main;
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
      * Whether the basedir of the new project should be the same one
      * as it would be when running the build file directly -
      * independent of dir and/or inheritAll settings.
      *
      * @since Ant 1.8.0
      */
     private boolean allowNativeBasedir = false;
 
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
      * Whether the basedir of the new project should be the same one
      * as it would be when running the build file directly -
      * independent of dir and/or inheritAll settings.
      *
      * @since Ant 1.8.0
      */
     public void setAllowNativeBasedir(boolean b) {
         allowNativeBasedir = b;
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
         newProject = getProject().createSubProject();
         newProject.setJavaVersionProperty();
     }
 
     /**
      * Called in execute or createProperty (via getNewProject())
      * if newProject is null.
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
         // set user-defined properties
+        if (allowNativeBasedir) {
+            addAlmostAll(getProject().getUserProperties(), PropertyType.USER);
+        } else {
         getProject().copyUserProperties(newProject);
+        }
 
         if (!inheritAll) {
            // set Ant's built-in properties separately,
            // because they are not being inherited.
            newProject.initProperties();
 
         } else {
             // set all properties from calling project
-            addAlmostAll(getProject().getProperties());
+            addAlmostAll(getProject().getProperties(), PropertyType.PLAIN);
         }
 
         Enumeration e = propertySets.elements();
         while (e.hasMoreElements()) {
             PropertySet ps = (PropertySet) e.nextElement();
-            addAlmostAll(ps.getProperties());
+            addAlmostAll(ps.getProperties(), PropertyType.PLAIN);
         }
     }
 
     /**
      * Handles output.
      * Send it the the new project if is present, otherwise
      * call the super class.
      * @param outputToHandle The string output to output.
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
      * Handles input.
      * Deleate to the created project, if present, otherwise
      * call the super class.
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read.
      *
      * @return the number of bytes read.
      *
      * @exception IOException if the data cannot be read.
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
      * Handles output.
      * Send it the the new project if is present, otherwise
      * call the super class.
      * @param toFlush The string to output.
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
      * Handle error output.
      * Send it the the new project if is present, otherwise
      * call the super class.
      * @param errorOutputToHandle The string to output.
      *
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
      * Handle error output.
      * Send it the the new project if is present, otherwise
      * call the super class.
      * @param errorOutputToFlush The string to output.
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
             getNewProject();
 
             if (dir == null && inheritAll) {
                 dir = getProject().getBaseDir();
             }
 
             initializeProject();
 
             if (dir != null) {
                 if (!allowNativeBasedir) {
                     newProject.setBaseDir(dir);
                     if (savedDir != null) {
                         // has been set explicitly
                         newProject.setInheritedProperty(MagicNames.PROJECT_BASEDIR,
                                                         dir.getAbsolutePath());
                     }
                 }
             } else {
                 dir = getProject().getBaseDir();
             }
 
             overrideProperties();
 
             if (antFile == null) {
                 antFile = Main.DEFAULT_BUILD_FILENAME;
             }
 
             File file = FILE_UTILS.resolveFile(dir, antFile);
             antFile = file.getAbsolutePath();
 
             log("calling target(s) "
                 + ((locals.size() > 0) ? locals.toString() : "[default]")
                 + " in build file " + antFile, Project.MSG_VERBOSE);
             newProject.setUserProperty(MagicNames.ANT_FILE , antFile);
 
             String thisAntFile = getProject().getProperty(MagicNames.ANT_FILE);
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
 
             if (newProject.getProperty(MagicNames.ANT_FILE)
                 .equals(getProject().getProperty(MagicNames.ANT_FILE))
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
                                        && "".equals(locals.get(0)))) {
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
+        if (allowNativeBasedir) {
+            addAlmostAll(getProject().getInheritedProperties(),
+                         PropertyType.INHERITED);
+        } else {
         getProject().copyInheritedProperties(newProject);
+        }
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
                 newProject.inheritIDReferences(getProject());
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
-     * @since Ant 1.6
+     * @param the type of property to set (a plain Ant property, a
+     * user property or an inherited property).
+     * @since Ant 1.8.0
      */
-    private void addAlmostAll(Hashtable props) {
+    private void addAlmostAll(Hashtable props, PropertyType type) {
         Enumeration e = props.keys();
         while (e.hasMoreElements()) {
             String key = e.nextElement().toString();
-            if (MagicNames.PROJECT_BASEDIR.equals(key) || MagicNames.ANT_FILE.equals(key)) {
+            if (MagicNames.PROJECT_BASEDIR.equals(key)
+                || MagicNames.ANT_FILE.equals(key)) {
                 // basedir and ant.file get special treatment in execute()
                 continue;
             }
 
             String value = props.get(key).toString();
+            if (type == PropertyType.PLAIN) {
             // don't re-set user properties, avoid the warning message
             if (newProject.getProperty(key) == null) {
                 // no user property
                 newProject.setNewProperty(key, value);
             }
+            } else if (type == PropertyType.USER) {
+                newProject.setUserProperty(key, value);
+            } else if (type == PropertyType.INHERITED) {
+                newProject.setInheritedProperty(key, value);
+            }
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
         Property p = new Property(true, getProject());
         p.setProject(getNewProject());
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
      * Get the (sub)-Project instance currently in use.
      * @return Project
      * @since Ant 1.7
      */
     protected Project getNewProject() {
         if (newProject == null) {
             reinit();
         }
         return newProject;
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
+
+    private static final class PropertyType {
+        private PropertyType() {}
+        private static final PropertyType PLAIN = new PropertyType();
+        private static final PropertyType INHERITED = new PropertyType();
+        private static final PropertyType USER = new PropertyType();
+    }
 }
diff --git a/src/tests/antunit/taskdefs/subant-test.xml b/src/tests/antunit/taskdefs/subant-test.xml
index 11f7d9f76..dc8b08663 100644
--- a/src/tests/antunit/taskdefs/subant-test.xml
+++ b/src/tests/antunit/taskdefs/subant-test.xml
@@ -1,68 +1,68 @@
 <?xml version="1.0"?>
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
 <project default="antunit" xmlns:au="antlib:org.apache.ant.antunit">
 
   <description>
     Test that subant properly sets various properties
   </description>
   <import file="../antunit-base.xml" />
 
   <target name="tearDown">
     <delete file="binaryAppendDest" />
     <delete file="encodeStringDest" />
   </target>
 
   <target name="assertProperties">
     <au:assertPropertySet name="ant.version" />
     <au:assertPropertySet name="java.home" />
     <au:assertPropertySet name="java.class.path" />
   </target>
 
   <target name="testAntVersion">
     <subant inheritall="false" target="assertProperties">
       <fileset file="${ant.file}" />
     </subant>
   </target>
 
   <!-- four testcases for bug 30542, the last one failed -->
   <target name="testSubAntDoesntSetBasedir">
     <subant antfile="echo.xml">
       <dirset dir="." includes="subant-helper"/>
     </subant>
   </target>
 
   <target name="testSubAntDoesntSetBasedirAfterAntCall">
     <antcall target="testSubAntDoesntSetBasedir"/>
   </target>
 
   <target name="testSubAntDoesntSetBasedirAfterAnt">
     <ant antfile="${ant.file}" target="testSubAntDoesntSetBasedir"/>
   </target>
 
-  <target name="XtestSubAntDoesntSetBasedirAfterAntWithDir">
+  <target name="testSubAntDoesntSetBasedirAfterAntWithDir">
     <ant antfile="${ant.file}" dir="${basedir}"
          target="testSubAntDoesntSetBasedir"/>
   </target>
 
   <target name="testSubAntDoesntSetBasedirAfterAntWithDirWhenNativeDir">
     <ant antfile="${ant.file}" dir="${basedir}"
          target="testSubAntDoesntSetBasedir"
          allowNativeBaseDir="true"/>
   </target>
 
 </project>
