diff --git a/WHATSNEW b/WHATSNEW
index 97d8558c7..4c982baa2 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1103 +1,1108 @@
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
 
  * <symlink action="delete"> used to fail if the link was broken (i.e.
    pointing to a file or directory that no longer existed).  It will now
    silently try to remove the link.
    Bugzilla Report 41285.
 
  * <delete file="..."> used to log a warning and not delete broken
    symbolic links.  <delete dir="..."/> didn't even log a warning.
    The task will now try to delete them in both cases.
    Bugzilla Report 41285.
 
+ * if the dir attribute of a <fileset> point to a symbolic link and
+   followsymlinks is set to false, the fileset will no longer be
+   scanned and always seem empty.
+   Bugzilla Report 45741.
+
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
 
  * when nested into builds that have been invoked by <ant> tasks
    <subant> might set the wrong basedir on the called projects.
    Bugzilla Report 30569.
 
  * If the message of the failed assertion of a forked JUnit test
    contained line feeds some excess output ended up in Ant's log.
    Bugzilla Report 45411.
 
  * <symlink action="delete"> failed to delete a link that pointed to
    a parent directory.
    Bugzilla Report 45743.
 
  * <symlink action="delete"> failed if ant lacked permission to rename
    the link's target.
    Bugzilla Report 41525.
 
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
 
  * The Jar task now supports the addition of a jar index file in update mode.
    Previously the absence of the index was not enough to trigger the rebuild;
    some other update was necessary.  Bugzilla report 45098.
 
  * <ant> has a new attribute "useNativeBasedir" that makes the child
    build use the same basedir it would have used if invoked from the
    command line.  No matter what other attributes/properties have been
    set.
    Bugzilla Report 45711.
 
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
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index 88c66003c..3074161c9 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1737 +1,1747 @@
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.PathPattern;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Class for scanning a directory for files/directories which match certain
  * criteria.
  * <p>
  * These criteria consist of selectors and patterns which have been specified.
  * With the selectors you can select which files you want to have included.
  * Files which are not selected are excluded. With patterns you can include
  * or exclude files based on their filename.
  * <p>
  * The idea is simple. A given directory is recursively scanned for all files
  * and directories. Each file/directory is matched against a set of selectors,
  * including special support for matching against filenames with include and
  * and exclude patterns. Only files/directories which match at least one
  * pattern of the include pattern list or other file selector, and don't match
  * any pattern of the exclude pattern list or fail to match against a required
  * selector will be placed in the list of files/directories found.
  * <p>
  * When no list of include patterns is supplied, "**" will be used, which
  * means that everything will be matched. When no list of exclude patterns is
  * supplied, an empty list is used, such that nothing will be excluded. When
  * no selectors are supplied, none are applied.
  * <p>
  * The filename pattern matching is done as follows:
  * The name to be matched is split up in path segments. A path segment is the
  * name of a directory or file, which is bounded by
  * <code>File.separator</code> ('/' under UNIX, '\' under Windows).
  * For example, "abc/def/ghi/xyz.java" is split up in the segments "abc",
  * "def","ghi" and "xyz.java".
  * The same is done for the pattern against which should be matched.
  * <p>
  * The segments of the name and the pattern are then matched against each
  * other. When '**' is used for a path segment in the pattern, it matches
  * zero or more path segments of the name.
  * <p>
  * There is a special case regarding the use of <code>File.separator</code>s
  * at the beginning of the pattern and the string to match:<br>
  * When a pattern starts with a <code>File.separator</code>, the string
  * to match must also start with a <code>File.separator</code>.
  * When a pattern does not start with a <code>File.separator</code>, the
  * string to match may not start with a <code>File.separator</code>.
  * When one of these rules is not obeyed, the string will not
  * match.
  * <p>
  * When a name path segment is matched against a pattern path segment, the
  * following special characters can be used:<br>
  * '*' matches zero or more characters<br>
  * '?' matches one character.
  * <p>
  * Examples:
  * <p>
  * "**\*.class" matches all .class files/dirs in a directory tree.
  * <p>
  * "test\a??.java" matches all files/dirs which start with an 'a', then two
  * more characters and then ".java", in a directory called test.
  * <p>
  * "**" matches everything in a directory tree.
  * <p>
  * "**\test\**\XYZ*" matches all files/dirs which start with "XYZ" and where
  * there is a parent directory called test (e.g. "abc\test\def\ghi\XYZ123").
  * <p>
  * Case sensitivity may be turned off if necessary. By default, it is
  * turned on.
  * <p>
  * Example of usage:
  * <pre>
  *   String[] includes = {"**\\*.class"};
  *   String[] excludes = {"modules\\*\\**"};
  *   ds.setIncludes(includes);
  *   ds.setExcludes(excludes);
  *   ds.setBasedir(new File("test"));
  *   ds.setCaseSensitive(true);
  *   ds.scan();
  *
  *   System.out.println("FILES:");
  *   String[] files = ds.getIncludedFiles();
  *   for (int i = 0; i < files.length; i++) {
  *     System.out.println(files[i]);
  *   }
  * </pre>
  * This will scan a directory called test for .class files, but excludes all
  * files in all proper subdirectories of a directory called "modules"
  *
  */
 public class DirectoryScanner
        implements FileScanner, SelectorScanner, ResourceFactory {
 
     /** Is OpenVMS the operating system we're running on? */
     private static final boolean ON_VMS = Os.isFamily("openvms");
 
     /**
      * Patterns which should be excluded by default.
      *
      * <p>Note that you can now add patterns to the list of default
      * excludes.  Added patterns will not become part of this array
      * that has only been kept around for backwards compatibility
      * reasons.</p>
      *
      * @deprecated since 1.6.x.
      *             Use the {@link #getDefaultExcludes getDefaultExcludes}
      *             method instead.
      */
     protected static final String[] DEFAULTEXCLUDES = {
         // Miscellaneous typical temporary files
         "**/*~",
         "**/#*#",
         "**/.#*",
         "**/%*%",
         "**/._*",
 
         // CVS
         "**/CVS",
         "**/CVS/**",
         "**/.cvsignore",
 
         // SCCS
         "**/SCCS",
         "**/SCCS/**",
 
         // Visual SourceSafe
         "**/vssver.scc",
 
         // Subversion
         "**/.svn",
         "**/.svn/**",
 
         // Mac
         "**/.DS_Store"
     };
 
     /** Helper. */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /** iterations for case-sensitive scanning. */
     private static final boolean[] CS_SCAN_ONLY = new boolean[] {true};
 
     /** iterations for non-case-sensitive scanning. */
     private static final boolean[] CS_THEN_NON_CS = new boolean[] {true, false};
 
     /**
      * Patterns which should be excluded by default.
      *
      * @see #addDefaultExcludes()
      */
     private static Vector defaultExcludes = new Vector();
     static {
         resetDefaultExcludes();
     }
 
     // CheckStyle:VisibilityModifier OFF - bc
 
     /** The base directory to be scanned. */
     protected File basedir;
 
     /** The patterns for the files to be included. */
     protected String[] includes;
 
     /** The patterns for the files to be excluded. */
     protected String[] excludes;
 
     /** Selectors that will filter which files are in our candidate list. */
     protected FileSelector[] selectors = null;
 
     /**
      * The files which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector filesExcluded;
 
     /**
      * The directories which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector dirsExcluded;
 
     /**
      * The files which matched at least one include and no excludes and
      * which a selector discarded.
      */
     protected Vector filesDeselected;
 
     /**
      * The directories which matched at least one include and no excludes
      * but which a selector discarded.
      */
     protected Vector dirsDeselected;
 
     /** Whether or not our results were built by a slow scan. */
     protected boolean haveSlowResults = false;
 
     /**
      * Whether or not the file system should be treated as a case sensitive
      * one.
      */
     protected boolean isCaseSensitive = true;
 
     /**
      * Whether a missing base directory is an error.
      * @since Ant 1.7.1
      */
     protected boolean errorOnMissingDir = true;
 
     /**
      * Whether or not symbolic links should be followed.
      *
      * @since Ant 1.5
      */
     private boolean followSymlinks = true;
 
     /** Whether or not everything tested so far has been included. */
     protected boolean everythingIncluded = true;
 
     // CheckStyle:VisibilityModifier ON
 
     /**
      * Temporary table to speed up the various scanning methods.
      *
      * @since Ant 1.6
      */
     private Map fileListMap = new HashMap();
 
     /**
      * List of all scanned directories.
      *
      * @since Ant 1.6
      */
     private Set scannedDirs = new HashSet();
 
     /**
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.6.3
      */
     private Set includeNonPatterns = new HashSet();
 
     /**
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.6.3
      */
     private Set excludeNonPatterns = new HashSet();
 
     /**
      * Array of all include patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private PathPattern[] includePatterns;
 
     /**
      * Array of all exclude patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private PathPattern[] excludePatterns;
 
     /**
      * Have the non-pattern sets and pattern arrays for in- and
      * excludes been initialized?
      *
      * @since Ant 1.6.3
      */
     private boolean areNonPatternSetsReady = false;
 
     /**
      * Scanning flag.
      *
      * @since Ant 1.6.3
      */
     private boolean scanning = false;
 
     /**
      * Scanning lock.
      *
      * @since Ant 1.6.3
      */
     private Object scanLock = new Object();
 
     /**
      * Slow scanning flag.
      *
      * @since Ant 1.6.3
      */
     private boolean slowScanning = false;
 
     /**
      * Slow scanning lock.
      *
      * @since Ant 1.6.3
      */
     private Object slowScanLock = new Object();
 
     /**
      * Exception thrown during scan.
      *
      * @since Ant 1.6.3
      */
     private IllegalStateException illegal = null;
 
     /**
      * Sole constructor.
      */
     public DirectoryScanner() {
     }
 
     /**
      * Test whether or not a given path matches the start of a given
      * pattern up to the first "**".
      * <p>
      * This is not a general purpose test and should only be used if you
      * can live with false positives. For example, <code>pattern=**\a</code>
      * and <code>str=b</code> will yield <code>true</code>.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      *
      * @return whether or not a given path matches the start of a given
      * pattern up to the first "**".
      */
     protected static boolean matchPatternStart(String pattern, String str) {
         return SelectorUtils.matchPatternStart(pattern, str);
     }
 
     /**
      * Test whether or not a given path matches the start of a given
      * pattern up to the first "**".
      * <p>
      * This is not a general purpose test and should only be used if you
      * can live with false positives. For example, <code>pattern=**\a</code>
      * and <code>str=b</code> will yield <code>true</code>.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      * @return whether or not a given path matches the start of a given
      * pattern up to the first "**".
      */
     protected static boolean matchPatternStart(String pattern, String str,
                                                boolean isCaseSensitive) {
         return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
     }
 
     /**
      * Test whether or not a given path matches a given pattern.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      *
      * @return <code>true</code> if the pattern matches against the string,
      *         or <code>false</code> otherwise.
      */
     protected static boolean matchPath(String pattern, String str) {
         return SelectorUtils.matchPath(pattern, str);
     }
 
     /**
      * Test whether or not a given path matches a given pattern.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      * @return <code>true</code> if the pattern matches against the string,
      *         or <code>false</code> otherwise.
      */
     protected static boolean matchPath(String pattern, String str,
                                        boolean isCaseSensitive) {
         return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
     }
 
     /**
      * Test whether or not a string matches against a pattern.
      * The pattern may contain two special characters:<br>
      * '*' means zero or more characters<br>
      * '?' means one and only one character
      *
      * @param pattern The pattern to match against.
      *                Must not be <code>null</code>.
      * @param str     The string which must be matched against the pattern.
      *                Must not be <code>null</code>.
      *
      * @return <code>true</code> if the string matches against the pattern,
      *         or <code>false</code> otherwise.
      */
     public static boolean match(String pattern, String str) {
         return SelectorUtils.match(pattern, str);
     }
 
     /**
      * Test whether or not a string matches against a pattern.
      * The pattern may contain two special characters:<br>
      * '*' means zero or more characters<br>
      * '?' means one and only one character
      *
      * @param pattern The pattern to match against.
      *                Must not be <code>null</code>.
      * @param str     The string which must be matched against the pattern.
      *                Must not be <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      *
      * @return <code>true</code> if the string matches against the pattern,
      *         or <code>false</code> otherwise.
      */
     protected static boolean match(String pattern, String str,
                                    boolean isCaseSensitive) {
         return SelectorUtils.match(pattern, str, isCaseSensitive);
     }
 
 
     /**
      * Get the list of patterns that should be excluded by default.
      *
      * @return An array of <code>String</code> based on the current
      *         contents of the <code>defaultExcludes</code>
      *         <code>Vector</code>.
      *
      * @since Ant 1.6
      */
     public static String[] getDefaultExcludes() {
         return (String[]) defaultExcludes.toArray(new String[defaultExcludes
                                                              .size()]);
     }
 
     /**
      * Add a pattern to the default excludes unless it is already a
      * default exclude.
      *
      * @param s   A string to add as an exclude pattern.
      * @return    <code>true</code> if the string was added;
      *            <code>false</code> if it already existed.
      *
      * @since Ant 1.6
      */
     public static boolean addDefaultExclude(String s) {
         if (defaultExcludes.indexOf(s) == -1) {
             defaultExcludes.add(s);
             return true;
         }
         return false;
     }
 
     /**
      * Remove a string if it is a default exclude.
      *
      * @param s   The string to attempt to remove.
      * @return    <code>true</code> if <code>s</code> was a default
      *            exclude (and thus was removed);
      *            <code>false</code> if <code>s</code> was not
      *            in the default excludes list to begin with.
      *
      * @since Ant 1.6
      */
     public static boolean removeDefaultExclude(String s) {
         return defaultExcludes.remove(s);
     }
 
     /**
      * Go back to the hardwired default exclude patterns.
      *
      * @since Ant 1.6
      */
     public static void resetDefaultExcludes() {
         defaultExcludes = new Vector();
         for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
             defaultExcludes.add(DEFAULTEXCLUDES[i]);
         }
     }
 
     /**
      * Set the base directory to be scanned. This is the directory which is
      * scanned recursively. All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not match
      * <code>File.separatorChar</code>.
      *
      * @param basedir The base directory to scan.
      */
     public void setBasedir(String basedir) {
         setBasedir(basedir == null ? (File) null
             : new File(basedir.replace('/', File.separatorChar).replace(
             '\\', File.separatorChar)));
     }
 
     /**
      * Set the base directory to be scanned. This is the directory which is
      * scanned recursively.
      *
      * @param basedir The base directory for scanning.
      */
     public synchronized void setBasedir(File basedir) {
         this.basedir = basedir;
     }
 
     /**
      * Return the base directory to be scanned.
      * This is the directory which is scanned recursively.
      *
      * @return the base directory to be scanned.
      */
     public synchronized File getBasedir() {
         return basedir;
     }
 
     /**
      * Find out whether include exclude patterns are matched in a
      * case sensitive way.
      * @return whether or not the scanning is case sensitive.
      * @since Ant 1.6
      */
     public synchronized boolean isCaseSensitive() {
         return isCaseSensitive;
     }
 
     /**
      * Set whether or not include and exclude patterns are matched
      * in a case sensitive way.
      *
      * @param isCaseSensitive whether or not the file system should be
      *                        regarded as a case sensitive one.
      */
     public synchronized void setCaseSensitive(boolean isCaseSensitive) {
         this.isCaseSensitive = isCaseSensitive;
     }
 
     /**
      * Sets whether or not a missing base directory is an error
      *
      * @param errorOnMissingDir whether or not a missing base directory
      *                        is an error
      * @since Ant 1.7.1
      */
     public void setErrorOnMissingDir(boolean errorOnMissingDir) {
         this.errorOnMissingDir = errorOnMissingDir;
     }
 
     /**
      * Get whether or not a DirectoryScanner follows symbolic links.
      *
      * @return flag indicating whether symbolic links should be followed.
      *
      * @since Ant 1.6
      */
     public synchronized boolean isFollowSymlinks() {
         return followSymlinks;
     }
 
     /**
      * Set whether or not symbolic links should be followed.
      *
      * @param followSymlinks whether or not symbolic links should be followed.
      */
     public synchronized void setFollowSymlinks(boolean followSymlinks) {
         this.followSymlinks = followSymlinks;
     }
 
     /**
      * Set the list of include patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param includes A list of include patterns.
      *                 May be <code>null</code>, indicating that all files
      *                 should be included. If a non-<code>null</code>
      *                 list is given, all elements must be
      *                 non-<code>null</code>.
      */
     public synchronized void setIncludes(String[] includes) {
         if (includes == null) {
             this.includes = null;
         } else {
             this.includes = new String[includes.length];
             for (int i = 0; i < includes.length; i++) {
                 this.includes[i] = normalizePattern(includes[i]);
             }
         }
     }
 
     /**
      * Set the list of exclude patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param excludes A list of exclude patterns.
      *                 May be <code>null</code>, indicating that no files
      *                 should be excluded. If a non-<code>null</code> list is
      *                 given, all elements must be non-<code>null</code>.
      */
     public synchronized void setExcludes(String[] excludes) {
         if (excludes == null) {
             this.excludes = null;
         } else {
             this.excludes = new String[excludes.length];
             for (int i = 0; i < excludes.length; i++) {
                 this.excludes[i] = normalizePattern(excludes[i]);
             }
         }
     }
 
     /**
      * Add to the list of exclude patterns to use. All '/' and '\'
      * characters are replaced by <code>File.separatorChar</code>, so
      * the separator used need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param excludes A list of exclude patterns.
      *                 May be <code>null</code>, in which case the
      *                 exclude patterns don't get changed at all.
      *
      * @since Ant 1.6.3
      */
     public synchronized void addExcludes(String[] excludes) {
         if (excludes != null && excludes.length > 0) {
             if (this.excludes != null && this.excludes.length > 0) {
                 String[] tmp = new String[excludes.length
                                           + this.excludes.length];
                 System.arraycopy(this.excludes, 0, tmp, 0,
                                  this.excludes.length);
                 for (int i = 0; i < excludes.length; i++) {
                     tmp[this.excludes.length + i] =
                         normalizePattern(excludes[i]);
                 }
                 this.excludes = tmp;
             } else {
                 setExcludes(excludes);
             }
         }
     }
 
     /**
      * All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not
      * match <code>File.separatorChar</code>.
      *
      * <p> When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @since Ant 1.6.3
      */
     private static String normalizePattern(String p) {
         String pattern = p.replace('/', File.separatorChar)
             .replace('\\', File.separatorChar);
         if (pattern.endsWith(File.separator)) {
             pattern += "**";
         }
         return pattern;
     }
 
     /**
      * Set the selectors that will select the filelist.
      *
      * @param selectors specifies the selectors to be invoked on a scan.
      */
     public synchronized void setSelectors(FileSelector[] selectors) {
         this.selectors = selectors;
     }
 
     /**
      * Return whether or not the scanner has included all the files or
      * directories it has come across so far.
      *
      * @return <code>true</code> if all files and directories which have
      *         been found so far have been included.
      */
     public synchronized boolean isEverythingIncluded() {
         return everythingIncluded;
     }
 
     /**
      * Scan for files which match at least one include pattern and don't match
      * any exclude patterns. If there are selectors then the files must pass
      * muster there, as well.  Scans under basedir, if set; otherwise the
      * include patterns without leading wildcards specify the absolute paths of
      * the files that may be included.
      *
      * @exception IllegalStateException if the base directory was set
      *            incorrectly (i.e. if it doesn't exist or isn't a directory).
      */
     public void scan() throws IllegalStateException {
         synchronized (scanLock) {
             if (scanning) {
                 while (scanning) {
                     try {
                         scanLock.wait();
                     } catch (InterruptedException e) {
                         continue;
                     }
                 }
                 if (illegal != null) {
                     throw illegal;
                 }
                 return;
             }
             scanning = true;
         }
+        File savedBase = basedir;
         try {
             synchronized (this) {
                 illegal = null;
                 clearResults();
 
                 // set in/excludes to reasonable defaults if needed:
                 boolean nullIncludes = (includes == null);
                 includes = nullIncludes ? new String[] {"**"} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
+                if (basedir != null && !followSymlinks
+                    && FILE_UTILS.isSymbolicLink(basedir.getParentFile(),
+                                                 basedir.getName())) {
+                    basedir = null;
+                }
+
                 if (basedir == null) {
                     // if no basedir and no includes, nothing to do:
                     if (nullIncludes) {
                         return;
                     }
                 } else {
                     if (!basedir.exists()) {
                         if (errorOnMissingDir) {
                             illegal = new IllegalStateException(
                                 "basedir " + basedir + " does not exist.");
                         } else {
                             // Nothing to do - basedir does not exist
                             return;
                         }
                     } else if (!basedir.isDirectory()) {
                         illegal = new IllegalStateException("basedir "
                                                             + basedir
                                                             + " is not a"
                                                             + " directory.");
                     }
                     if (illegal != null) {
                         throw illegal;
                     }
                 }
                 if (isIncluded("")) {
                     if (!isExcluded("")) {
                         if (isSelected("", basedir)) {
                             dirsIncluded.addElement("");
                         } else {
                             dirsDeselected.addElement("");
                         }
                     } else {
                         dirsExcluded.addElement("");
                     }
                 } else {
                     dirsNotIncluded.addElement("");
                 }
                 checkIncludePatterns();
                 clearCaches();
                 includes = nullIncludes ? null : includes;
                 excludes = nullExcludes ? null : excludes;
             }
+        } catch (IOException ex) {
+            throw new BuildException(ex);
         } finally {
+            basedir = savedBase;
             synchronized (scanLock) {
                 scanning = false;
                 scanLock.notifyAll();
             }
         }
     }
 
     /**
      * This routine is actually checking all the include patterns in
      * order to avoid scanning everything under base dir.
      * @since Ant 1.6
      */
     private void checkIncludePatterns() {
         Map newroots = new HashMap();
         // put in the newroots map the include patterns without
         // wildcard tokens
         for (int i = 0; i < includes.length; i++) {
             if (FileUtils.isAbsolutePath(includes[i])) {
                 //skip abs. paths not under basedir, if set:
                 if (basedir != null
                     && !SelectorUtils.matchPatternStart(includes[i],
                     basedir.getAbsolutePath(), isCaseSensitive())) {
                     continue;
                 }
             } else if (basedir == null) {
                 //skip non-abs. paths if basedir == null:
                 continue;
             }
             newroots.put(SelectorUtils.rtrimWildcardTokens(
                 includes[i]), includes[i]);
         }
         if (newroots.containsKey("") && basedir != null) {
             // we are going to scan everything anyway
             scandir(basedir, "", true);
         } else {
             // only scan directories that can include matched files or
             // directories
             Iterator it = newroots.entrySet().iterator();
 
             File canonBase = null;
             if (basedir != null) {
                 try {
                     canonBase = basedir.getCanonicalFile();
                 } catch (IOException ex) {
                     throw new BuildException(ex);
                 }
             }
             while (it.hasNext()) {
                 Map.Entry entry = (Map.Entry) it.next();
                 String currentelement = (String) entry.getKey();
                 if (basedir == null
                     && !FileUtils.isAbsolutePath(currentelement)) {
                     continue;
                 }
                 String originalpattern = (String) entry.getValue();
                 File myfile = new File(basedir, currentelement);
 
                 if (myfile.exists()) {
                     // may be on a case insensitive file system.  We want
                     // the results to show what's really on the disk, so
                     // we need to double check.
                     try {
                         String path = (basedir == null)
                             ? myfile.getCanonicalPath()
                             : FILE_UTILS.removeLeadingPath(canonBase,
                             myfile.getCanonicalFile());
                         if (!path.equals(currentelement) || ON_VMS) {
                             myfile = findFile(basedir, currentelement, true);
                             if (myfile != null && basedir != null) {
                                 currentelement = FILE_UTILS.removeLeadingPath(
                                     basedir, myfile);
                             }
                         }
                     } catch (IOException ex) {
                         throw new BuildException(ex);
                     }
                 }
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive()) {
                     File f = findFile(basedir, currentelement, false);
                     if (f != null && f.exists()) {
                         // adapt currentelement to the case we've
                         // actually found
                         currentelement = (basedir == null)
                             ? f.getAbsolutePath()
                             : FILE_UTILS.removeLeadingPath(basedir, f);
                         myfile = f;
                     }
                 }
                 if (myfile != null && myfile.exists()) {
                     if (!followSymlinks
                         && isSymlink(basedir, currentelement)) {
                         continue;
                     }
                     if (myfile.isDirectory()) {
                         if (isIncluded(currentelement)
                             && currentelement.length() > 0) {
                             accountForIncludedDir(currentelement, myfile, true);
                         }  else {
                             if (currentelement.length() > 0) {
                                 if (currentelement.charAt(currentelement
                                                           .length() - 1)
                                     != File.separatorChar) {
                                     currentelement =
                                         currentelement + File.separatorChar;
                                 }
                             }
                             scandir(myfile, currentelement, true);
                         }
                     } else {
                         boolean included = isCaseSensitive()
                             ? originalpattern.equals(currentelement)
                             : originalpattern.equalsIgnoreCase(currentelement);
                         if (included) {
                             accountForIncludedFile(currentelement, myfile);
                         }
                     }
                 }
             }
         }
     }
 
     /**
      * Clear the result caches for a scan.
      */
     protected synchronized void clearResults() {
         filesIncluded    = new Vector();
         filesNotIncluded = new Vector();
         filesExcluded    = new Vector();
         filesDeselected  = new Vector();
         dirsIncluded     = new Vector();
         dirsNotIncluded  = new Vector();
         dirsExcluded     = new Vector();
         dirsDeselected   = new Vector();
         everythingIncluded = (basedir != null);
         scannedDirs.clear();
     }
 
     /**
      * Top level invocation for a slow scan. A slow scan builds up a full
      * list of excluded/included files/directories, whereas a fast scan
      * will only have full results for included files, as it ignores
      * directories which can't possibly hold any included files/directories.
      * <p>
      * Returns immediately if a slow scan has already been completed.
      */
     protected void slowScan() {
         synchronized (slowScanLock) {
             if (haveSlowResults) {
                 return;
             }
             if (slowScanning) {
                 while (slowScanning) {
                     try {
                         slowScanLock.wait();
                     } catch (InterruptedException e) {
                         // Empty
                     }
                 }
                 return;
             }
             slowScanning = true;
         }
         try {
             synchronized (this) {
 
                 // set in/excludes to reasonable defaults if needed:
                 boolean nullIncludes = (includes == null);
                 includes = nullIncludes ? new String[] {"**"} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
                 String[] excl = new String[dirsExcluded.size()];
                 dirsExcluded.copyInto(excl);
 
                 String[] notIncl = new String[dirsNotIncluded.size()];
                 dirsNotIncluded.copyInto(notIncl);
 
                 processSlowScan(excl);
                 processSlowScan(notIncl);
                 clearCaches();
                 includes = nullIncludes ? null : includes;
                 excludes = nullExcludes ? null : excludes;
             }
         } finally {
             synchronized (slowScanLock) {
                 haveSlowResults = true;
                 slowScanning = false;
                 slowScanLock.notifyAll();
             }
         }
     }
 
     private void processSlowScan(String[] arr) {
         for (int i = 0; i < arr.length; i++) {
             if (!couldHoldIncluded(arr[i])) {
                 scandir(new File(basedir, arr[i]),
                         arr[i] + File.separator, false);
             }
         }
     }
 
     /**
      * Scan the given directory for files and directories. Found files and
      * directories are placed in their respective collections, based on the
      * matching of includes, excludes, and the selectors.  When a directory
      * is found, it is scanned recursively.
      *
      * @param dir   The directory to scan. Must not be <code>null</code>.
      * @param vpath The path relative to the base directory (needed to
      *              prevent problems with an absolute path when using
      *              dir). Must not be <code>null</code>.
      * @param fast  Whether or not this call is part of a fast scan.
      *
      * @see #filesIncluded
      * @see #filesNotIncluded
      * @see #filesExcluded
      * @see #dirsIncluded
      * @see #dirsNotIncluded
      * @see #dirsExcluded
      * @see #slowScan
      */
     protected void scandir(File dir, String vpath, boolean fast) {
         if (dir == null) {
             throw new BuildException("dir must not be null.");
         }
         String[] newfiles = list(dir);
         if (newfiles == null) {
             if (!dir.exists()) {
                 throw new BuildException(dir + " doesn't exist.");
             } else if (!dir.isDirectory()) {
                 throw new BuildException(dir + " is not a directory.");
             } else {
                 throw new BuildException("IO error scanning directory '"
                                          + dir.getAbsolutePath() + "'");
             }
         }
         scandir(dir, vpath, fast, newfiles);
     }
 
     private void scandir(File dir, String vpath, boolean fast,
                          String[] newfiles) {
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         if (!followSymlinks) {
             Vector noLinks = new Vector();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (FILE_UTILS.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File file = new File(dir, newfiles[i]);
                         (file.isDirectory()
                             ? dirsExcluded : filesExcluded).addElement(name);
                     } else {
                         noLinks.addElement(newfiles[i]);
                     }
                 } catch (IOException ioe) {
                     String msg = "IOException caught while checking "
                         + "for links, couldn't get canonical path!";
                     // will be caught and redirected to Ant's logging system
                     System.err.println(msg);
                     noLinks.addElement(newfiles[i]);
                 }
             }
             newfiles = (String[]) (noLinks.toArray(new String[noLinks.size()]));
         }
         for (int i = 0; i < newfiles.length; i++) {
             String name = vpath + newfiles[i];
             File file = new File(dir, newfiles[i]);
             String[] children = list(file);
             if (children == null) { // probably file
                 if (isIncluded(name)) {
                     accountForIncludedFile(name, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             } else { // dir
                 if (isIncluded(name)) {
                     accountForIncludedDir(name, file, fast, children);
                 } else {
                     everythingIncluded = false;
                     dirsNotIncluded.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         scandir(file, name + File.separator, fast, children);
                     }
                 }
                 if (!fast) {
                     scandir(file, name + File.separator, fast, children);
                 }
             }
         }
     }
 
     /**
      * Process included file.
      * @param name  path of the file relative to the directory of the FileSet.
      * @param file  included File.
      */
     private void accountForIncludedFile(String name, File file) {
         processIncluded(name, file, filesIncluded, filesExcluded,
                         filesDeselected);
     }
 
     /**
      * Process included directory.
      * @param name path of the directory relative to the directory of
      *             the FileSet.
      * @param file directory as File.
      * @param fast whether to perform fast scans.
      */
     private void accountForIncludedDir(String name, File file, boolean fast) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name + File.separator, fast);
         }
     }
 
     private void accountForIncludedDir(String name, File file, boolean fast,
                                        String[] children) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name + File.separator, fast, children);
         }
     }
 
     private void processIncluded(String name, File file, Vector inc,
                                  Vector exc, Vector des) {
 
         if (inc.contains(name) || exc.contains(name) || des.contains(name)) {
             return;
         }
 
         boolean included = false;
         if (isExcluded(name)) {
             exc.add(name);
         } else if (isSelected(name, file)) {
             included = true;
             inc.add(name);
         } else {
             des.add(name);
         }
         everythingIncluded &= included;
     }
 
     /**
      * Test whether or not a name matches against at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         include pattern, or <code>false</code> otherwise.
      */
     protected boolean isIncluded(String name) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? includeNonPatterns.contains(name)
             : includeNonPatterns.contains(name.toUpperCase())) {
             return true;
         }
         for (int i = 0; i < includePatterns.length; i++) {
             if (includePatterns[i].matchPath(name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether or not a name matches the start of at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of at
      *         least one include pattern, or <code>false</code> otherwise.
      */
     protected boolean couldHoldIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPatternStart(includes[i], name, isCaseSensitive())
                 && isMorePowerfulThanExcludes(name, includes[i])
                 && isDeeper(includes[i], name)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Verify that a pattern specifies files deeper
      * than the level of the specified file.
      * @param pattern the pattern to check.
      * @param name the name to check.
      * @return whether the pattern is deeper than the name.
      * @since Ant 1.6.3
      */
     private boolean isDeeper(String pattern, String name) {
         Vector p = SelectorUtils.tokenizePath(pattern);
         if (!p.contains("**")) {
             Vector n = SelectorUtils.tokenizePath(name);
             return p.size() > n.size();
         }
         return true;
     }
 
     /**
      *  Find out whether one particular include pattern is more powerful
      *  than all the excludes.
      *  Note:  the power comparison is based on the length of the include pattern
      *  and of the exclude patterns without the wildcards.
      *  Ideally the comparison should be done based on the depth
      *  of the match; that is to say how many file separators have been matched
      *  before the first ** or the end of the pattern.
      *
      *  IMPORTANT : this function should return false "with care".
      *
      *  @param name the relative path to test.
      *  @param includepattern one include pattern.
      *  @return true if there is no exclude pattern more powerful than
      *  this include pattern.
      *  @since Ant 1.6
      */
     private boolean isMorePowerfulThanExcludes(String name,
                                                String includepattern) {
         String soughtexclude = name + File.separator + "**";
         for (int counter = 0; counter < excludes.length; counter++) {
             if (excludes[counter].equals(soughtexclude))  {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Test whether all contents of the specified directory must be excluded.
      * @param name the directory name to check.
      * @return whether all the specified directory's contents are excluded.
      */
     private boolean contentsExcluded(String name) {
         name = (name.endsWith(File.separator)) ? name : name + File.separator;
         for (int i = 0; i < excludes.length; i++) {
             String e = excludes[i];
             if (e.endsWith("**") && SelectorUtils.matchPath(
                 e.substring(0, e.length() - 2), name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether or not a name matches against at least one exclude
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         exclude pattern, or <code>false</code> otherwise.
      */
     protected boolean isExcluded(String name) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? excludeNonPatterns.contains(name)
             : excludeNonPatterns.contains(name.toUpperCase())) {
             return true;
         }
         for (int i = 0; i < excludePatterns.length; i++) {
             if (excludePatterns[i].matchPath(name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether a file should be selected.
      *
      * @param name the filename to check for selecting.
      * @param file the java.io.File object for this filename.
      * @return <code>false</code> when the selectors says that the file
      *         should not be selected, <code>true</code> otherwise.
      */
     protected boolean isSelected(String name, File file) {
         if (selectors != null) {
             for (int i = 0; i < selectors.length; i++) {
                 if (!selectors[i].isSelected(basedir, name, file)) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     /**
      * Return the names of the files which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and none of the exclude patterns.
      */
     public String[] getIncludedFiles() {
         String[] files;
         synchronized (this) {
             if (filesIncluded == null) {
                 throw new IllegalStateException("Must call scan() first");
             }
             files = new String[filesIncluded.size()];
             filesIncluded.copyInto(files);
         }
         Arrays.sort(files);
         return files;
     }
 
     /**
      * Return the count of included files.
      * @return <code>int</code>.
      * @since Ant 1.6.3
      */
     public synchronized int getIncludedFilesCount() {
         if (filesIncluded == null) {
             throw new IllegalStateException("Must call scan() first");
         }
         return filesIncluded.size();
     }
 
     /**
      * Return the names of the files which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched none of the include
      *         patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getNotIncludedFiles() {
         slowScan();
         String[] files = new String[filesNotIncluded.size()];
         filesNotIncluded.copyInto(files);
         return files;
     }
 
     /**
      * Return the names of the files which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getExcludedFiles() {
         slowScan();
         String[] files = new String[filesExcluded.size()];
         filesExcluded.copyInto(files);
         return files;
     }
 
     /**
      * <p>Return the names of the files which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the files which were deselected.
      *
      * @see #slowScan
      */
     public synchronized String[] getDeselectedFiles() {
         slowScan();
         String[] files = new String[filesDeselected.size()];
         filesDeselected.copyInto(files);
         return files;
     }
 
     /**
      * Return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      */
     public String[] getIncludedDirectories() {
         String[] directories;
         synchronized (this) {
             if (dirsIncluded == null) {
                 throw new IllegalStateException("Must call scan() first");
             }
             directories = new String[dirsIncluded.size()];
             dirsIncluded.copyInto(directories);
         }
         Arrays.sort(directories);
         return directories;
     }
 
     /**
      * Return the count of included directories.
      * @return <code>int</code>.
      * @since Ant 1.6.3
      */
     public synchronized int getIncludedDirsCount() {
         if (dirsIncluded == null) {
             throw new IllegalStateException("Must call scan() first");
         }
         return dirsIncluded.size();
     }
 
     /**
      * Return the names of the directories which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched none of the include
      * patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getNotIncludedDirectories() {
         slowScan();
         String[] directories = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(directories);
         return directories;
     }
 
     /**
      * Return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getExcludedDirectories() {
         slowScan();
         String[] directories = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(directories);
         return directories;
     }
 
     /**
      * <p>Return the names of the directories which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the directories which were deselected.
      *
      * @see #slowScan
      */
     public synchronized String[] getDeselectedDirectories() {
         slowScan();
         String[] directories = new String[dirsDeselected.size()];
         dirsDeselected.copyInto(directories);
         return directories;
     }
 
     /**
      * Add default exclusions to the current exclusions set.
      */
     public synchronized void addDefaultExcludes() {
         int excludesLength = excludes == null ? 0 : excludes.length;
         String[] newExcludes;
         newExcludes = new String[excludesLength + defaultExcludes.size()];
         if (excludesLength > 0) {
             System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
         }
         String[] defaultExcludesTemp = getDefaultExcludes();
         for (int i = 0; i < defaultExcludesTemp.length; i++) {
             newExcludes[i + excludesLength] =
                 defaultExcludesTemp[i].replace('/', File.separatorChar)
                 .replace('\\', File.separatorChar);
         }
         excludes = newExcludes;
     }
 
     /**
      * Get the named resource.
      * @param name path name of the file relative to the dir attribute.
      *
      * @return the resource with the given name.
      * @since Ant 1.5.2
      */
     public synchronized Resource getResource(String name) {
         return new FileResource(basedir, name);
     }
 
     private static final String[] NULL_FILE_LIST = new String[0];
 
     /**
      * Return a cached result of list performed on file, if
      * available.  Invokes the method and caches the result otherwise.
      *
      * @param file File (dir) to list.
      * @since Ant 1.6
      */
     private String[] list(File file) {
         String[] files = (String[]) fileListMap.get(file);
         if (files == null) {
             files = file.list();
             if (files != null) {
                 fileListMap.put(file, files);
             } else {
                 fileListMap.put(file, NULL_FILE_LIST);
             }
         } else if (files == NULL_FILE_LIST) {
             files = null;
         }
         return files;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given name.
      *
      * @param base base File (dir).
      * @param path file path.
      * @param cs whether to scan case-sensitively.
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6.3
      */
     private File findFile(File base, String path, boolean cs) {
         if (FileUtils.isAbsolutePath(path)) {
             if (base == null) {
                 String[] s = FILE_UTILS.dissect(path);
                 base = new File(s[0]);
                 path = s[1];
             } else {
                 File f = FILE_UTILS.normalize(path);
                 String s = FILE_UTILS.removeLeadingPath(base, f);
                 if (s.equals(f.getAbsolutePath())) {
                     //removing base from path yields no change; path
                     //not child of base
                     return null;
                 }
                 path = s;
             }
         }
         return findFile(base, SelectorUtils.tokenizePath(path), cs);
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given stack of names.
      *
      * @param base base File (dir).
      * @param pathElements Vector of path elements (dirs...file).
      * @param cs whether to scan case-sensitively.
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6.3
      */
     private File findFile(File base, Vector pathElements, boolean cs) {
         if (pathElements.size() == 0) {
             return base;
         }
         String current = (String) pathElements.remove(0);
         if (base == null) {
             return findFile(new File(current), pathElements, cs);
         }
         if (!base.isDirectory()) {
             return null;
         }
         String[] files = list(base);
         if (files == null) {
             throw new BuildException("IO error scanning directory "
                                      + base.getAbsolutePath());
         }
         boolean[] matchCase = cs ? CS_SCAN_ONLY : CS_THEN_NON_CS;
         for (int i = 0; i < matchCase.length; i++) {
             for (int j = 0; j < files.length; j++) {
                 if (matchCase[i] ? files[j].equals(current)
                                  : files[j].equalsIgnoreCase(current)) {
                     return findFile(new File(base, files[j]), pathElements, cs);
                 }
             }
         }
         return null;
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @param base base File (dir).
      * @param path file path.
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, String path) {
         return isSymlink(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @param base base File (dir).
      * @param pathElements Vector of path elements (dirs...file).
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, Vector pathElements) {
         if (pathElements.size() > 0) {
             String current = (String) pathElements.remove(0);
             try {
                 return FILE_UTILS.isSymbolicLink(base, current)
                     || isSymlink(new File(base, current), pathElements);
             } catch (IOException ioe) {
                 String msg = "IOException caught while checking "
                     + "for links, couldn't get canonical path!";
                 // will be caught and redirected to Ant's logging system
                 System.err.println(msg);
             }
         }
         return false;
     }
 
     /**
      * Has the directory with the given path relative to the base
      * directory already been scanned?
      *
      * <p>Registers the given directory as scanned as a side effect.</p>
      *
      * @since Ant 1.6
      */
     private boolean hasBeenScanned(String vpath) {
         return !scannedDirs.add(vpath);
     }
 
     /**
      * This method is of interest for testing purposes.  The returned
      * Set is live and should not be modified.
      * @return the Set of relative directory names that have been scanned.
      */
     /* package-private */ Set getScannedDirs() {
         return scannedDirs;
     }
 
     /**
      * Clear internal caches.
      *
      * @since Ant 1.6
      */
     private synchronized void clearCaches() {
         fileListMap.clear();
         includeNonPatterns.clear();
         excludeNonPatterns.clear();
         includePatterns = null;
         excludePatterns = null;
         areNonPatternSetsReady = false;
     }
 
     /**
      * Ensure that the in|exclude &quot;patterns&quot;
      * have been properly divided up.
      *
      * @since Ant 1.6.3
      */
     private synchronized void ensureNonPatternSetsReady() {
         if (!areNonPatternSetsReady) {
             includePatterns = fillNonPatternSet(includeNonPatterns, includes);
             excludePatterns = fillNonPatternSet(excludeNonPatterns, excludes);
             areNonPatternSetsReady = true;
         }
     }
 
     /**
      * Add all patterns that are not real patterns (do not contain
      * wildcards) to the set and returns the real patterns.
      *
      * @param set Set to populate.
      * @param patterns String[] of patterns.
      * @since Ant 1.6.3
      */
     private PathPattern[] fillNonPatternSet(Set set, String[] patterns) {
         ArrayList al = new ArrayList(patterns.length);
         for (int i = 0; i < patterns.length; i++) {
             if (!SelectorUtils.hasWildcards(patterns[i])) {
                 set.add(isCaseSensitive() ? patterns[i]
                     : patterns[i].toUpperCase());
             } else {
                 al.add(new PathPattern(patterns[i]));
             }
         }
         return (PathPattern[]) al.toArray(new PathPattern[al.size()]);
     }
 
 }
diff --git a/src/tests/antunit/core/dirscanner-symlinks-test.xml b/src/tests/antunit/core/dirscanner-symlinks-test.xml
index 3e1251207..551338ebe 100644
--- a/src/tests/antunit/core/dirscanner-symlinks-test.xml
+++ b/src/tests/antunit/core/dirscanner-symlinks-test.xml
@@ -1,166 +1,166 @@
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
 <project xmlns:au="antlib:org.apache.ant.antunit" default="antunit">
 
   <import file="../antunit-base.xml"/>
 
   <target name="setUp">
     <property name="base" location="${input}/base"/>
     <mkdir dir="${base}"/>
   </target>
 
   <target name="checkOs">
     <condition property="unix"><os family="unix"/></condition>
   </target>
 
   <macrodef name="assertDirIsEmpty">
     <attribute name="dir" default="${output}"/>
     <sequential>
       <local name="resources"/>
       <resourcecount property="resources">
         <fileset dir="@{dir}"/>
       </resourcecount>
       <au:assertEquals expected="0" actual="${resources}"/>
     </sequential>
   </macrodef>
 
   <target name="testSymlinkToSiblingFollow"
           depends="checkOs, setUp, -sibling"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <au:assertFileExists file="${output}/B/file.txt"/>
   </target>
 
   <target name="testSymlinkToSiblingNoFollow"
           depends="checkOs, setUp, -sibling"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <au:assertFileDoesntExist file="${output}/B/file.txt"/>
   </target>
 
   <target name="testBasedirIsSymlinkFollow"
           depends="checkOs, setUp, -basedir-as-symlink"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <au:assertFileExists file="${output}/file.txt"/>
   </target>
 
-  <target name="FAILStestBasedirIsSymlinkNoFollow"
+  <target name="testBasedirIsSymlinkNoFollow"
           depends="checkOs, setUp, -basedir-as-symlink"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <au:assertFileDoesntExist file="${output}/file.txt"/>
   </target>
 
   <target name="INFINITEtestLinkToParentFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/B/file.txt"/>
   </target>
 
   <target name="testLinkToParentFollowWithInclude"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true">
         <include name="A/B/*"/>
       </fileset>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/B/file.txt"/>
   </target>
 
   <!-- supposed to fail? -->
   <target name="testLinkToParentFollowWithIncludeMultiFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true">
         <include name="A/base/A/B/*"/>
       </fileset>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/base/A/B/file.txt"/>
   </target>
 
   <target name="testLinkToParentNoFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileDoesntExist file="${output}/A/B/file.txt"/>
   </target>
 
   <target name="INFINITE testSillyLoopFollow"
           depends="checkOs, setUp, -silly-loop"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <symlink action="delete" link="${base}"/>
     <assertDirIsEmpty/>
   </target>
 
   <target name="testSillyLoopNoFollow"
           depends="checkOs, setUp, -silly-loop"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <symlink action="delete" link="${base}"/>
-    <assertDirIsEmpty/>
+    <au:assertFileDoesntExist file="${output}"/>
   </target>
 
   <target name="-sibling" if="unix">
     <mkdir dir="${base}/A"/>
     <touch file="${base}/A/file.txt"/>
     <symlink link="${base}/B" resource="${base}/A"/>
   </target>
 
   <target name="-basedir-as-symlink" if="unix">
     <delete dir="${base}"/>
     <mkdir dir="${input}/realdir"/>
     <touch file="${input}/realdir/file.txt"/>
     <symlink link="${base}" resource="${input}/realdir"/>
   </target>    
 
   <target name="-link-to-parent" if="unix">
     <mkdir dir="${input}/B"/>
     <touch file="${input}/B/file.txt"/>
     <symlink link="${base}/A" resource="${input}"/>
   </target>
 
   <target name="-silly-loop" if="unix">
     <delete dir="${base}"/>
     <symlink link="${base}" resource="${input}"/>
   </target>
 </project>
