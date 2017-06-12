diff --git a/WHATSNEW b/WHATSNEW
index 35b562a5b..d5479e389 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1159 +1,1162 @@
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
+   
+ * Enhance performance of Project.fireMessageLoggedEvent  
+   Bugzilla Report 45651.
 
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
 
 * <junitreport> prints a special message if supplied an empty XML File. This
   can be caused by the test JVM exiting during a test, either via a
   System.exit() call or a JVM crash.
 
 * Project name is now used for *all* targets so one can write consistent import
   build files. Bugzilla report 28444.
 
 * New condition <typefound> that can be used to probe for the declaration
   and implementation of a task, type, preset, macro, scriptdef, whatever.
   As it tests for the implementation, it can be used to check for optional
   tasks being available.
 
 * Check for 1.5.* Ant main class. (weblogic.jar in classpath reports)
 
 * New condition <isfailure> that tests the return-code of an executable. This
diff --git a/contributors.xml b/contributors.xml
index b76c1d252..6b216b172 100644
--- a/contributors.xml
+++ b/contributors.xml
@@ -1,1210 +1,1214 @@
 <?xml version="1.0" encoding="utf-8"?>
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
 <!DOCTYPE contributors
 [
 <!ELEMENT name (first?, middle?, last)>
 <!ELEMENT contributors (introduction, name+)>
 <!ELEMENT first (#PCDATA)>
 <!ELEMENT introduction (#PCDATA)>
 <!ELEMENT middle (#PCDATA)>
 <!ELEMENT last (#PCDATA)>
 ]
 >
 
 <contributors>
   <introduction>
   These are some of the many people who have helped Ant become so successful.
   </introduction>
   <name>
     <first>Adam</first>
     <last>Blinkinsop</last>
   </name>
   <name>
     <first>Adam</first>
     <last>Bryzak</last>
   </name>
   <name>
     <first>Aleksandr</first>
     <last>Ishutin</last>
   </name>
   <name>
     <first>Alexey</first>
     <last>Panchenko</last>
   </name>
   <name>
     <first>Alexey</first>
     <last>Solofnenko</last>
   </name>
   <name>
     <first>Alison</first>
     <last>Winters</last>
   </name>
   <name>
     <first>Andreas</first>
     <last>Ames</last>
   </name>
   <name>
     <first>Andrew</first>
     <last>Everitt</last>
   </name>
   <name>
     <first>Andrey</first>
     <last>Urazov</last>
   </name>
   <name>
     <first>Andy</first>
     <last>Wood</last>
   </name>
   <name>
     <first>Anil</first>
     <middle>K.</middle>
     <last>Vijendran</last>
   </name>
   <name>
     <first>Anli</first>
     <last>Shundi</last>
   </name>
   <name>
     <first>Anthony</first>
     <last>Green</last>
   </name>
   <name>
     <first>Antoine</first>
     <last>Baudoux</last>
   </name>
   <name>
     <first>Antoine</first>
     <last>Levy-Lambert</last>
   </name>
   <name>
     <first>Anton</first>
     <last>Mazkovoi</last>
   </name>
   <name>
     <first>Arnaud</first>
     <last>Vandyck</last>
   </name>
   <name>
     <first>Arnout</first>
     <middle>J.</middle>
     <last>Kuiper</last>
   </name>
   <name>
     <first>Aslak</first>
     <last>Helles&#244;y</last>
   </name>
   <name>
     <first>Atsuhiko</first>
     <last>Yamanaka</last>
   </name>
   <name>
     <first>Avik</first>
     <last>Sengupta</last>
   </name>
   <name>
     <first>Balazs</first>
     <last>Fejes 2</last>
   </name>
   <name>
     <first>Benjamin</first>
     <last>Burgess</last>
   </name>
   <name>
     <first>Ben</first>
     <last>Galbraith</last>
   </name>
   <name>
     <first>Benoit</first>
     <last>Moussaud</last>
   </name>
   <name>
     <first>Bernd</first>
     <last>Dutkowski</last>
   </name>
   <name>
     <first>Brad</first>
     <last>Clark</last>
   </name>
   <name>
     <first>Brant</first>
     <middle>Langer</middle>
     <last>Gurganus</last>
   </name>
   <name>
     <first>Brian</first>
     <last>Deitte</last>
   </name>
   <name>
     <first>Brian</first>
     <last>Felder</last>
   </name>
   <name>
     <first>Bruce</first>
     <last>Atherton</last>
   </name>
   <name>
     <first>Charles</first>
     <last>Hudak</last>
   </name>
   <name>
     <first>Charlie</first>
     <last>Hubbard</last>
   </name>
   <name>
     <first>Chris</first>
     <last>Povirk</last>
   </name>
   <name>
     <first>Christian</first>
     <last>Knorr</last>
   </name>
   <name>
     <first>Christoph</first>
     <last>Wilhelms</last>
   </name>
   <name>
     <first>Christophe</first>
     <last>Labouisse</last>
   </name>
   <name>
     <first>Christopher</first>
     <middle>A.</middle>
     <last>Longo</last>
   </name>
   <name>
     <first>Christopher</first>
     <last>Charlier</last>
   </name>
   <name>
     <first>Conor</first>
     <last>MacNeill</last>
   </name>
   <name>
     <first>Craeg</first>
     <last>Strong</last>
   </name>
   <name>
     <first>Craig</first>
     <last>Cottingham</last>
   </name>
   <name>
     <first>Craig</first>
     <middle>R.</middle>
     <last>McClanahan</last>
   </name>
   <name>
     <first>Craig</first>
     <last>Ryan</last>
   </name>
   <name>
     <first>Craig</first>
     <last>Sandvik</last>
   </name>
   <name>
     <first>Curtis</first>
     <last>White</last>
   </name>
   <name>
     <first>Cyrille</first>
     <last>Morvan</last>
   </name>
   <name>
     <first>D'Arcy</first>
     <last>Smith</last>
   </name>
   <name>
     <first>Dale</first>
     <last>Anson</last>
   </name>
   <name>
     <first>Dan</first>
     <last>Armbrust</last>
   </name>
   <name>
     <first>Daniel</first>
     <last>Henrique</last>
   </name>
   <name>
     <first>Daniel</first>
     <last>Ribagnac</last>
   </name>
   <name>
     <first>Daniel</first>
     <last>Spilker</last>
   </name>
   <name>
     <first>Danno</first>
     <last>Ferrin</last>
   </name>
   <name>
     <first>Dante</first>
     <last>Briones</last>
   </name>
   <name>
     <first>Davanum</first>
     <last>Srinivas</last>
   </name>
   <name>
     <first>Dave</first>
     <last>Brondsema</last>
   </name>
   <name>
     <first>Dave</first>
     <last>Brosius</last>
   </name>
   <name>
     <first>David</first>
     <last>A.</last>
   </name>
   <name>
     <first>David</first>
     <last>Crossley</last>
   </name>
   <name>
     <first>David</first>
     <last>G&#228;rtner</last>
   </name>
   <name>
     <first>David</first>
     <middle>S.</middle>
     <last>Johnson</last>
   </name>
   <name>
     <first>David</first>
     <last>Kavanagh</last>
   </name>
   <name>
     <first>David</first>
     <last>LeRoy</last>
   </name>
   <name>
     <first>David</first>
     <last>Maclean</last>
   </name>
   <name>
     <first>David</first>
     <last>Rees</last>
   </name>
   <name>
     <first>Denis</first>
     <last>Hennessy</last>
   </name>
   <name>
     <first>Derek</first>
     <last>Slager</last>
   </name>
   <name>
     <first>Diane</first>
     <last>Holt</last>
   </name>
   <name>
     <first>dIon</first>
     <last>Gillard</last>
   </name>
   <name>
     <first>Dominique</first>
     <last>Devienne</last>
   </name>
   <name>
     <first>Donal</first>
     <last>Quinlan</last>
   </name>
   <name>
     <first>Don</first>
     <last>Bnamen</last>
   </name>
   <name>
     <first>Don</first>
     <last>Ferguson</last>
   </name>
   <name>
     <first>Don</first>
     <last>Jeffery</last>
   </name>
   <name>
     <first>Drew</first>
     <last>Sudell</last>
   </name>
   <name>
     <first>Eduard</first>
     <last>Wirch</last>
   </name>
   <name>
     <first>Edwin</first>
     <last>Woudt</last>
   </name>
   <name>
     <first>Eli</first>
     <last>Tucker</last>
   </name>
   <name>
     <first>Emmanuel</first>
     <last>Bourg</last>
   </name>
   <name>
     <first>Eric</first>
     <last>Olsen</last>
   </name>
   <name>
     <first>Eric</first>
     <last>Pugh</last>
   </name>
   <name>
     <first>Erik</first>
     <last>Hatcher</last>
   </name>
   <name>
     <first>Erik</first>
     <last>Langenbach</last>
   </name>
   <name>
     <first>Erik</first>
     <last>Meade</last>
   </name>
   <name>
     <first>Ernst</first>
     <last>de Haan</last>
   </name>
   <name>
     <first>Frank</first>
     <last>Harnack</last>
   </name>
   <name>
     <first>Frank</first>
     <last>Somers</last>
   </name>
   <name>
     <first>Frank</first>
     <last>Zeyda</last>
   </name>
   <name>
     <first>Frédéric</first>
     <last>Bothamy</last>
   </name>
   <name>
     <first>Frederic</first>
     <last>Lavigne</last>
   </name>
   <name>
     <first>Gary</first>
     <middle>S.</middle>
     <last>Weaver</last>
   </name>
   <name>
     <first>Gautam</first>
     <last>Guliani</last>
   </name>
   <name>
     <first>Gero</first>
     <last>Vermaas</last>
   </name>
   <name>
     <first>Gerrit</first>
     <last>Riessen</last>
   </name>
   <name>
+    <first>Gilles</first>
+    <last>Scokart</last>
+  </name>
+  <name>
     <first>Glenn</first>
     <last>McAllister</last>
   </name>
   <name>
     <first>Glenn</first>
     <last>Twiggs</last>
   </name>
   <name>
     <first>Greg</first>
     <last>Nelson</last>
   </name>
   <name>
     <first>Harish</first>
     <last>Prabandham</last>
   </name>
   <name>
     <first>Haroon</first>
     <last>Rafique</last>
   </name>
   <name>
     <first>Hiroaki</first>
     <last>Nakamura</last>
   </name>
   <name>
     <first>Holger</first>
     <last>Engels</last>
   </name>
   <name>
     <first>Ignacio</first>
     <last>Coloma</last>
   </name>
   <name>
     <first>Ingenonsya</first>
     <last>France</last>
   </name>
   <name>
     <first>Ingmar</first>
     <last>Stein</last>
   </name>
   <name>
     <first>Irene</first>
     <last>Rusman</last>
   </name>
   <name>
     <first>Ivan</first>
     <last>Ivanov</last>
   </name>
   <name>
     <first>Jack</first>
     <middle>J.</middle>
     <last>Woehr</last>
   </name>
   <name>
     <first>James</first>
     <middle>Duncan</middle>
     <last>Davidson</last>
   </name>
   <name>
     <first>Jan</first>
     <last>Mat&#232;rne</last>
   </name>
     <name>
       <first>Jan</first>
       <last>Cumps</last>
     </name>
   <name>
     <first>Jan</first>
     <last>Mynarik</last>
   </name>
   <name>
     <first>Jason</first>
     <last>Hunter</last>
   </name>
   <name>
     <first>Jason</first>
     <last>Pettiss</last>
   </name>
   <name>
     <first>Jason</first>
     <last>Salter</last>
   </name>
   <name>
     <first>Jason</first>
     <last>Yip</last>
   </name>
   <name>
     <first>Jay</first>
     <middle>Dickon</middle>
     <last>Glanville</last>
   </name>
   <name>
     <first>Jay</first>
     <last>Peck</last>
   </name>
   <name>
     <first>Jay</first>
     <last>van der Meer</last>
   </name>
   <name>
     <first>JC</first>
     <last>Mann</last>
   </name>
   <name>
     <first>J</first>
     <last>D</last>
   </name>
   <name>
     <first>Jean-Francois</first>
     <last>Brousseau</last>
   </name>
   <name>
     <first>Jeff</first>
     <last>Gettle</last>
   </name>
   <name>
     <first>Jeff</first>
     <last>Martin</last>
   </name>
   <name>
     <first>Jeff</first>
     <last>Tulley</last>
   </name>
   <name>
     <first>Jeff</first>
     <last>Turner</last>
   </name>
   <name>
     <first>Jene</first>
     <last>Jasper</last>
   </name>
   <name>
     <first>Jeremy</first>
     <last>Mawson</last>
   </name>
   <name>
     <first>Jerome</first>
     <last>Lacoste</last>
   </name>
   <name>
     <first>Jesse</first>
     <last>Glick</last>
   </name>
   <name>
     <first>Jesse</first>
     <last>Stockall</last>
   </name>
   <name>
     <first>Jim</first>
     <last>Allers</last>
   </name>
   <name>
     <first>Joerg</first>
     <last>Wassmer</last>
   </name>
   <name>
     <first>Jon</first>
     <last>Dickinson</last>
   </name>
   <name>
     <first>Jon</first>
     <middle>S.</middle>
     <last>Stevens</last>
   </name>
   <name>
     <first>Jose</first>
     <middle>Alberto</middle>
     <last>Fernandez</last>
   </name>
   <name>
     <first>Josh</first>
     <last>Lucas</last>
   </name>
   <name>
     <first>Joseph</first>
     <last>Walton</last>
   </name>
   <name>
     <first>Juerg</first>
     <last>Wanner</last>
   </name>
   <name>
     <first>Julian</first>
     <last>Simpson</last>
   </name>
   <name>
     <first>Justin</first>
     <last>Vallon</last>
   </name>
   <name>
     <first>Keiron</first>
     <last>Liddle</last>
   </name>
   <name>
     <first>Keith</first>
     <last>Visco</last>
   </name>
   <name>
     <first>Kevin</first>
     <last>Greiner</last>
   </name>
   <name>
     <first>Kevin</first>
     <last>Jackson</last>
   </name>
   <name>
     <first>Kevin</first>
     <last>Ross</last>
   </name>
   <name>
     <first>Kevin</first>
     <middle>Z</middle>
     <last>Grey</last>
   </name>
   <name>
     <first>Kirk</first>
     <last>Wylie</last>
   </name>
   <name>
     <first>Kyle</first>
     <last>Adams</last>
   </name>
   <name>
     <first>Larry</first>
     <last>Shatzer</last>
   </name>
   <name>
     <first>Larry</first>
     <last>Streepy</last>
   </name>
   <name>
     <first>Les</first>
     <last>Hughes</last>
   </name>
   <name>
     <first>Levi</first>
     <last>Cook</last>
   </name>
   <name>
     <last>lucas</last>
   </name>
   <name>
     <first>Ludovic</first>
     <last>Claude</last>
   </name>
   <name>
     <first>Magesh</first>
     <last>Umasankar</last>
   </name>
   <name>
     <first>Maneesh</first>
     <last>Sahu</last>
   </name>
   <name>
     <first>Marcel</first>
     <last>Schutte</last>
   </name>
   <name>
     <first>Marcus</first>
     <last>B&amp;ouml;rger</last>
   </name>
   <name>
     <first>Mariusz</first>
     <last>Nowostawski</last>
   </name>
   <name>
     <first>Mark</first>
     <last>Hecker</last>
   </name>
   <name>
     <first>Mark</first>
     <middle>R.</middle>
     <last>Diggory</last>
   </name>
   <name>
     <first>Martijn</first>
     <last>Kruithof</last>
   </name>
   <name>
     <first>Martin</first>
     <last>Landers</last>
   </name>
   <name>
     <first>Martin</first>
     <last>Poeschl</last>
   </name>
   <name>
     <first>Martin</first>
     <last>van den Bemt</last>
   </name>
   <name>
     <first>Mathieu</first>
     <last>Champlon</last>
   </name>
   <name>
     <first>Mathieu</first>
     <last>Peltier</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Albrecht</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Benson</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Bishop</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Foemmel</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Grosso</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Humphrey</last>
   </name>
   <name>
     <first>Matt</first>
     <last>Small</last>
   </name>
   <name>
     <first>Matthew</first>
     <last>Hawthorne</last>
   </name>
   <name>
     <first>Matthew</first>
     <last>Inger</last>
   </name>
   <name>
     <first>Matthew</first>
     <middle>Kuperus</middle>
     <last>Heun</last>
   </name>
   <name>
     <first>Matthew</first>
     <last>Watson</last>
   </name>
   <name>
     <first>Michael</first>
     <last>Davey</last>
   </name>
   <name>
     <first>Michael</first>
     <middle>J.</middle>
     <last>Sikorsky</last>
   </name>
   <name>
     <first>Michael</first>
     <last>McCallum</last>
   </name>
   <name>
     <first>Michael</first>
     <last>Newcomb</last>
   </name>
   <name>
     <first>Michael</first>
     <last>Nygard</last>
   </name>
   <name>
     <first>Michael</first>
     <last>Saunders</last>
   </name>
   <name>
     <last>Miha</last>
   </name>
   <name>
     <first>Mike</first>
     <last>Davis</last>
   </name>
   <name>
     <first>Mike</first>
     <last>Roberts</last>
   </name>
   <name>
     <last>mnowostawski</last>
   </name>
   <name>
     <first>Nick</first>
     <last>Chalko</last>
   </name>
   <name>
     <first>Nick</first>
     <last>Crossley</last>
   </name>
   <name>
     <first>Nick</first>
     <last>Fortescue</last>
   </name>
   <name>
     <first>Nick</first>
     <last>Pellow</last>
   </name>
   <name>
     <first>Nicola</first>
     <last>Ken</last>
   </name>
   <name>
     <first>Nico</first>
     <last>Seessle</last>
   </name>
   <name>
     <first>Nigel</first>
     <last>Magnay</last>
   </name>
   <name>
     <first>Oliver</first>
     <last>Merkel</last>
   </name>
   <name>
     <first>Oliver</first>
     <last>Rossmueller</last>
   </name>
   <name>
     <first>&#216;ystein</first>
     <last>Gisn&#229;s</last>
   </name>
   <name>
     <first>Patrick</first>
     <last>C.</last>
   </name>
   <name>
     <first>Patrick</first>
     <last>Chanezon</last>
   </name>
   <name>
     <first>Patrick</first>
     <last>Gus</last>
   </name>
   <name>
     <first>Paul</first>
     <last>Austin</last>
   </name>
   <name>
     <first>Paul</first>
     <last>Christmann</last>
   </name>
   <name>
     <first>Paul</first>
     <last>Galbraith</last>
   </name>
   <name>
     <first>Paul</first>
     <last>King</last>
   </name>
   <name>
     <first>Paulo</first>
     <last>Gaspar</last>
   </name>
   <name>
     <first>Peter</first>
     <middle>B.</middle>
     <last>West</last>
   </name>
   <name>
     <first>Peter</first>
     <last>Donald</last>
   </name>
   <name>
     <first>Peter</first>
     <last>Doornbosch</last>
   </name>
   <name>
     <first>Peter</first>
     <last>Hulst</last>
   </name>
   <name>
     <first>Peter</first>
     <last>Reilly</last>
   </name>
   <name>
     <first>Phillip</first>
     <last>Wells</last>
   </name>
   <name>
     <first>Pierre</first>
     <last>Delisle</last>
   </name>
   <name>
     <first>Pierre</first>
     <last>Dittgen</last>
   </name>
   <name>
     <first>R</first>
     <last>Handerson</last>
   </name>
   <name>
     <first>Rami</first>
     <last>Ojares</last>
   </name>
   <name>
     <first>Randy</first>
     <last>Watler</last>
   </name>
   <name>
     <first>Raphael</first>
     <last>Pierquin</last>
   </name>
   <name>
     <first>Ray</first>
     <last>Waldin</last>
   </name>
   <name>
     <first>Richard</first>
     <last>Evans</last>
   </name>
   <name>
     <first>Rick</first>
     <last>Beton</last>
   </name>
   <name>
     <first>Robert</first>
     <last>Anderson</last>
   </name>
   <name>
     <first>Robert</first>
     <last>Shaw</last>
   </name>
   <name>
     <first>Robert</first>
     <last>Watkins</last>
   </name>
   <name>
     <first>Roberto</first>
     <last>Scaramuzzi</last>
   </name>
   <name>
     <first>Robin</first>
     <last>Green</last>
   </name>
   <name>
     <first>Rob</first>
     <last>Oxspring</last>
   </name>
   <name>
     <first>Rob</first>
     <last>van Oostrum</last>
   </name>
   <name>
     <first>Roger</first>
     <last>Vaughn</last>
   </name>
   <name>
     <first>Roman</first>
     <last>Ivashin</last>
   </name>
   <name>
     <first>Ronen</first>
     <last>Mashal</last>
   </name>
   <name>
     <first>Russell</first>
     <last>Gold</last>
   </name>
   <name>
     <first>Sam</first>
     <last>Ruby</last>
   </name>
   <name>
     <first>Sandra</first>
     <last>Metz</last>
   </name>
   <name>
     <first>Scott</first>
     <last>Carlson</last>
   </name>
   <name>
     <first>Scott</first>
     <last>Ellsworth</last>
   </name>
   <name>
     <first>Scott</first>
     <last>Johnson</last>
   </name>
   <name>
     <first>Scott</first>
     <middle>M.</middle>
     <last>Stirling</last>
   </name>
   <name>
     <first>Sean</first>
     <last>Egan</last>
   </name>
   <name>
     <first>Sean</first>
     <middle>P.</middle>
     <last>Kane</last>
   </name>
   <name>
     <first>Sebastien</first>
     <last>Arod</last>
   </name>
   <name>
     <first>Shiraz</first>
     <last>Kanga</last>
   </name>
   <name>
     <first>Sebastian</first>
     <last>Kantha</last>
   </name>
   <name>
       <first>Simon</first>
       <last>Law</last>
   </name>
   <name>
     <first>Stefan</first>
     <last>Bodewig</last>
   </name>
   <name>
     <first>Stefano</first>
     <last>Mazzocchi</last>
   </name>
   <name>
     <first>Stephan</first>
     <last>Strittmatter</last>
   </name>
   <name>
     <first>Stephane</first>
     <last>Bailliez</last>
   </name>
   <name>
     <last>stephan</last>
   </name>
   <name>
     <first>Stephan</first>
     <last>Michels</last>
   </name>
   <name>
     <first>Stephen</first>
     <last>Chin</last>
   </name>
   <name>
     <first>Steve</first>
     <last>Cohen</last>
   </name>
   <name>
     <first>Steve</first>
     <last>Loughran</last>
   </name>
   <name>
     <first>Steve</first>
     <last>Morin</last>
   </name>
   <name>
     <first>Steve</first>
     <last>Wadsworth</last>
   </name>
   <name>
     <first>Steven</first>
     <middle>E.</middle>
     <last>Newton</last>
   </name>
   <name>
     <first>Takashi</first>
     <last>Okamoto</last>
   </name>
   <name>
     <first>Taoufik</first>
     <last>Romdhane</last>
   </name>
   <name>
     <first>Tariq</first>
     <last>Master</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Aglassinger</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Butz</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Christen</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Christensen</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Haas</last>
   </name>
   <name>
     <first>Thomas</first>
     <last>Quas</last>
   </name>
   <name>
     <first>Tim</first>
     <last>Drury</last>
   </name>
   <name>
     <first>Tim</first>
     <last>Fennell</last>
   </name>
   <name>
     <first>Timothy</first>
     <middle>Gerard</middle>
     <last>Endres</last>
   </name>
   <name>
     <first>Tim</first>
     <last>Stephenson</last>
   </name>
   <name>
     <first>Tom</first>
     <last>Ball</last>
   </name>
   <name>
     <first>Tom</first>
     <last>Cunningham</last>
   </name>
   <name>
     <first>Tom</first>
     <last>Dimock</last>
   </name>
   <name>
     <first>Tom</first>
     <last>Eugelink</last>
   </name>
   <name>
     <first>Trejkaz</first>
     <last>Xaoz</last>
   </name>
   <name>
     <first>Ulrich</first>
     <last>Schmidt</last>
   </name>
   <name>
     <first>Victor</first>
     <last>Toni</last>
   </name>
   <name>
     <first>Will</first>
     <last>Wang</last>
   </name>
   <name>
     <first>William</first>
     <last>Ferguson</last>
   </name>
   <name>
     <first>Wolf</first>
     <last>Siberski</last>
   </name>
   <name>
     <first>Wolfgang</first>
     <last>Baer</last>
   </name>
   <name>
     <first>Wolfgang</first>
     <last>Frech</last>
   </name>
   <name>
     <first>Wolfgang</first>
     <last>Werner</last>
   </name>
   <name>
     <first>Xavier</first>
     <last>Hanin</last>
   </name>
   <name>
     <first>Xavier</first>
     <last>Witdouck</last>
   </name>
   <name>
     <first>Yohann</first>
     <last>Roussel</last>
   </name>
   <name>
     <first>Yuji</first>
     <last>Yamano</last>
   </name>
   <name>
     <first>Yves</first>
     <last>Martin</last>
   </name>
   <name>
     <first>Zach</first>
     <last>Garner</last>
   </name>
   <name>
     <first>Zdenek</first>
     <last>Wagner</last>
   </name>
 </contributors>
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index d588225d2..2be86930b 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,2393 +1,2401 @@
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
-import java.util.Iterator;
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
-    private Vector listeners = new Vector();
+    private volatile BuildListener[] listeners = new BuildListener[0];
 
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
-            if (listeners.contains(listener)) {
-                return;
+            for (int i = 0; i < listeners.length; i++) {
+                if (listeners[i] == listener) {
+                    return;
+                }
             }
             // copy on write semantics
-            Vector newListeners = getBuildListeners();
-            newListeners.addElement(listener);
+            BuildListener[] newListeners = new BuildListener[listeners.length + 1];
+            System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
+            newListeners[listeners.length] = listener;
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
-            Vector newListeners = getBuildListeners();
-            newListeners.removeElement(listener);
-            listeners = newListeners;
+            for (int i = 0; i < listeners.length; i++) {
+                if (listeners[i] == listener) {
+                    BuildListener[] newListeners = new BuildListener[listeners.length - 1];
+                    System.arraycopy(listeners, 0, newListeners, 0, i);
+                    System.arraycopy(listeners, i + 1, newListeners, i, listeners.length - i - 1);
+                    listeners = newListeners;
+                    break;
+                }
+            }
         }
     }
 
     /**
-     * Return a copy of the list of build listeners for the project.
-     *
-     * @return a list of build listeners for the project
-     */
+	 * Return a copy of the list of build listeners for the project.
+	 * 
+	 * @return a list of build listeners for the project
+	 */
     public Vector getBuildListeners() {
-        return (Vector) listeners.clone();
+        synchronized (listenersLock) {
+            Vector r = new Vector(listeners.length);
+            for (int i = 0; i < listeners.length; i++) {
+                r.add(listeners[i]);
+            }
+            return r;
+        }
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
 
     /**
      * Return the boolean equivalent of a string, which is considered
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
      * Get the Project instance associated with the specified object.
      * @param o the object to query.
      * @return Project instance, if any.
      * @since Ant 1.7.1
      */
     public static Project getProject(Object o) {
         if (o instanceof ProjectComponent) {
             return ((ProjectComponent) o).getProject();
         }
         try {
             Method m = o.getClass().getMethod("getProject", (Class[]) null);
             if (Project.class == m.getReturnType()) {
                 return (Project) m.invoke(o, (Object[]) null);
             }
         } catch (Exception e) {
             //too bad
         }
         return null;
     }
 
     /**
      * Topologically sort a set of targets.  Equivalent to calling
      * <code>topoSort(new String[] {root}, targets, true)</code>.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targetTable A Hashtable mapping names to Targets.
      *                Must not be <code>null</code>.
      * @return a Vector of ALL Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      */
     public final Vector topoSort(String root, Hashtable targetTable)
         throws BuildException {
         return topoSort(new String[] {root}, targetTable, true);
     }
 
     /**
      * Topologically sort a set of targets.  Equivalent to calling
      * <code>topoSort(new String[] {root}, targets, returnAll)</code>.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targetTable A Hashtable mapping names to Targets.
      *                Must not be <code>null</code>.
      * @param returnAll <code>boolean</code> indicating whether to return all
      *                  targets, or the execution sequence only.
      * @return a Vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      * @since Ant 1.6.3
      */
     public final Vector topoSort(String root, Hashtable targetTable,
                                  boolean returnAll) throws BuildException {
         return topoSort(new String[] {root}, targetTable, returnAll);
     }
 
     /**
      * Topologically sort a set of targets.
      *
      * @param root <code>String[]</code> containing the names of the root targets.
      *             The sort is created in such a way that the ordered sequence of
      *             Targets is the minimum possible such sequence to the specified
      *             root targets.
      *             Must not be <code>null</code>.
      * @param targetTable A map of names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @param returnAll <code>boolean</code> indicating whether to return all
      *                  targets, or the execution sequence only.
      * @return a Vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      * @since Ant 1.6.3
      */
     public final Vector topoSort(String[] root, Hashtable targetTable,
                                  boolean returnAll) throws BuildException {
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
             String st = (String) (state.get(root[i]));
             if (st == null) {
                 tsort(root[i], targetTable, state, visiting, ret);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + root[i]);
             }
         }
         StringBuffer buf = new StringBuffer("Build sequence for target(s)");
 
         for (int j = 0; j < root.length; j++) {
             buf.append((j == 0) ? " `" : ", `").append(root[j]).append('\'');
         }
         buf.append(" is " + ret);
         log(buf.toString(), MSG_VERBOSE);
 
         Vector complete = (returnAll) ? ret : new Vector(ret);
         for (Enumeration en = targetTable.keys(); en.hasMoreElements();) {
             String curTarget = (String) en.nextElement();
             String st = (String) state.get(curTarget);
             if (st == null) {
                 tsort(curTarget, targetTable, state, visiting, complete);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + curTarget);
             }
         }
         log("Complete build sequence is " + complete, MSG_VERBOSE);
         return ret;
     }
 
     /**
      * Perform a single step in a recursive depth-first-search traversal of
      * the target dependency tree.
      * <p>
      * The current target is first set to the &quot;visiting&quot; state, and
      * pushed onto the &quot;visiting&quot; stack.
      * <p>
      * An exception is then thrown if any child of the current node is in the
      * visiting state, as that implies a circular dependency. The exception
      * contains details of the cycle, using elements of the &quot;visiting&quot;
      * stack.
      * <p>
      * If any child has not already been &quot;visited&quot;, this method is
      * called recursively on it.
      * <p>
      * The current target is then added to the ordered list of targets. Note
      * that this is performed after the children have been visited in order
      * to get the correct order. The current target is set to the
      * &quot;visited&quot; state.
      * <p>
      * By the time this method returns, the ordered list contains the sequence
      * of targets up to and including the current target.
      *
      * @param root The current target to inspect.
      *             Must not be <code>null</code>.
      * @param targetTable A mapping from names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @param state   A mapping from target names to states (String to String).
      *                The states in question are &quot;VISITING&quot; and
      *                &quot;VISITED&quot;. Must not be <code>null</code>.
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
     private void tsort(String root, Hashtable targetTable,
                              Hashtable state, Stack visiting,
                              Vector ret)
         throws BuildException {
         state.put(root, VISITING);
         visiting.push(root);
 
         Target target = (Target) targetTable.get(root);
 
         // Make sure we exist
         if (target == null) {
             StringBuffer sb = new StringBuffer("Target \"");
             sb.append(root);
             sb.append("\" does not exist in the project \"");
             sb.append(name);
             sb.append("\". ");
             visiting.pop();
             if (!visiting.empty()) {
                 String parent = (String) visiting.peek();
                 sb.append("It is used from target \"");
                 sb.append(parent);
                 sb.append("\".");
             }
             throw new BuildException(new String(sb));
         }
         for (Enumeration en = target.getDependencies(); en.hasMoreElements();) {
             String cur = (String) en.nextElement();
             String m = (String) state.get(cur);
             if (m == null) {
                 // Not been visited
                 tsort(cur, targetTable, state, visiting, ret);
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
      * Build an appropriate exception detailing a specified circular
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
      * Inherit the id references.
      * @param parent the parent project of this project.
      */
     public void inheritIDReferences(Project parent) {
         parentIdProject = parent;
     }
 
     /**
      * Add an id reference.
      * Used for broken build files.
      * @param id the id to set.
      * @param value the value to set it to (Unknown element in this case.
      */
     public void addIdReference(String id, Object value) {
         idReferences.put(id, value);
     }
 
     /**
      * Add a reference to the project.
      *
      * @param referenceName The name of the reference. Must not be <code>null</code>.
      * @param value The value of the reference.
      */
     public void addReference(String referenceName, Object value) {
         Object old = ((AntRefTable) references).getReal(referenceName);
         if (old == value) {
             // no warning, this is not changing anything
             return;
         }
         if (old != null && !(old instanceof UnknownElement)) {
             log("Overriding previous definition of reference to " + referenceName,
                 MSG_VERBOSE);
         }
         log("Adding reference: " + referenceName, MSG_DEBUG);
         references.put(referenceName, value);
     }
 
     /**
      * Return a map of the references in the project (String to Object).
      * The returned hashtable is &quot;live&quot; and so must not be modified.
      *
      * @return a map of the references in the project (String to Object).
      */
     public Hashtable getReferences() {
         return references;
     }
 
     /**
      * Look up a reference by its key (ID).
      *
      * @param key The key for the desired reference.
      *            Must not be <code>null</code>.
      *
      * @return the reference with the specified ID, or <code>null</code> if
      *         there is no such reference in the project.
      */
     public Object getReference(String key) {
         Object ret = references.get(key);
         if (ret != null) {
             return ret;
         }
         if (!key.equals(MagicNames.REFID_PROPERTY_HELPER)) {
             try {
                 if (PropertyHelper.getPropertyHelper(this).containsProperties(key)) {
                     log("Unresolvable reference " + key
                             + " might be a misuse of property expansion syntax.", MSG_WARN);
                 }
             } catch (Exception e) {
                 //ignore
             }
         }
         return ret;
     }
 
     /**
      * Return a description of the type of the given element, with
      * special handling for instances of tasks and data types.
      * <p>
      * This is useful for logging purposes.
      *
      * @param element The element to describe.
      *                Must not be <code>null</code>.
      *
      * @return a description of the element type.
      *
      * @since 1.95, Ant 1.5
      */
     public String getElementName(Object element) {
         return ComponentHelper.getComponentHelper(this).getElementName(element);
     }
 
     /**
      * Send a &quot;build started&quot; event
      * to the build listeners for this project.
      */
     public void fireBuildStarted() {
         BuildEvent event = new BuildEvent(this);
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.buildStarted(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].buildStarted(event);
         }
     }
 
     /**
      * Send a &quot;build finished&quot; event to the build listeners
      * for this project.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     public void fireBuildFinished(Throwable exception) {
         BuildEvent event = new BuildEvent(this);
         event.setException(exception);
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.buildFinished(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].buildFinished(event);
         }
         // Inform IH to clear the cache
         IntrospectionHelper.clearCache();
     }
 
     /**
      * Send a &quot;subbuild started&quot; event to the build listeners for
      * this project.
      *
      * @since Ant 1.6.2
      */
     public void fireSubBuildStarted() {
         BuildEvent event = new BuildEvent(this);
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            Object listener = iter.next();
-            if (listener instanceof SubBuildListener) {
-                ((SubBuildListener) listener).subBuildStarted(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	if (currListeners[i] instanceof SubBuildListener) {
+                ((SubBuildListener) currListeners[i]).subBuildStarted(event);
             }
         }
     }
 
     /**
      * Send a &quot;subbuild finished&quot; event to the build listeners for
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
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            Object listener = iter.next();
-            if (listener instanceof SubBuildListener) {
-                ((SubBuildListener) listener).subBuildFinished(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	if (currListeners[i] instanceof SubBuildListener) {
+                ((SubBuildListener) currListeners[i]).subBuildFinished(event);
             }
         }
     }
 
     /**
      * Send a &quot;target started&quot; event to the build listeners
      * for this project.
      *
      * @param target The target which is starting to build.
      *               Must not be <code>null</code>.
      */
     protected void fireTargetStarted(Target target) {
         BuildEvent event = new BuildEvent(target);
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.targetStarted(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].targetStarted(event);
         }
+
     }
 
     /**
      * Send a &quot;target finished&quot; event to the build listeners
      * for this project.
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
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.targetFinished(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].targetFinished(event);
         }
+
     }
 
     /**
      * Send a &quot;task started&quot; event to the build listeners
      * for this project.
      *
      * @param task The target which is starting to execute.
      *               Must not be <code>null</code>.
      */
     protected void fireTaskStarted(Task task) {
         // register this as the current task on the current thread.
         registerThreadTask(Thread.currentThread(), task);
         BuildEvent event = new BuildEvent(task);
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.taskStarted(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].taskStarted(event);
         }
     }
 
     /**
      * Send a &quot;task finished&quot; event to the build listeners for this
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
-        Iterator iter = listeners.iterator();
-        while (iter.hasNext()) {
-            BuildListener listener = (BuildListener) iter.next();
-            listener.taskFinished(event);
+        BuildListener[] currListeners = listeners;
+        for (int i=0; i<currListeners.length; i++) {
+        	currListeners[i].targetFinished(event);
         }
+
     }
 
     /**
      * Send a &quot;message logged&quot; event to the build listeners
      * for this project.
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
         if (isLoggingMessage.get() != Boolean.FALSE) {
             /*
              * One of the Listeners has attempted to access
              * System.err or System.out.
              *
              * We used to throw an exception in this case, but
              * sometimes Listeners can't prevent it(like our own
              * Log4jListener which invokes getLogger() which in
              * turn wants to write to the console).
              *
              * @see http://marc.theaimsgroup.com/?t=110538624200006&r=1&w=2
              *
              * We now (Ant 1.6.3 and later) simply swallow the message.
              */
             return;
         }
         try {
             isLoggingMessage.set(Boolean.TRUE);
-            Iterator iter = listeners.iterator();
-            while (iter.hasNext()) {
-                BuildListener listener = (BuildListener) iter.next();
-                listener.messageLogged(event);
+            BuildListener[] currListeners = listeners;
+            for (int i=0; i<currListeners.length; i++) {
+            	currListeners[i].messageLogged(event);
             }
         } finally {
             isLoggingMessage.set(Boolean.FALSE);
         }
     }
 
     /**
      * Send a &quot;message logged&quot; project level event
      * to the build listeners for this project.
      *
      * @param project  The project generating the event.
      *                 Should not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Project project, String message,
                                      int priority) {
         fireMessageLogged(project, message, null, priority);
     }
 
     /**
      * Send a &quot;message logged&quot; project level event
      * to the build listeners for this project.
      *
      * @param project  The project generating the event.
      *                 Should not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param throwable The exception that caused this message. May be <code>null</code>.
      * @param priority The priority of the message.
      * @since 1.7
      */
     protected void fireMessageLogged(Project project, String message,
             Throwable throwable, int priority) {
         BuildEvent event = new BuildEvent(project);
         event.setException(throwable);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Send a &quot;message logged&quot; target level event
      * to the build listeners for this project.
      *
      * @param target   The target generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Target target, String message,
                                      int priority) {
         fireMessageLogged(target, message, null, priority);
     }
 
     /**
      * Send a &quot;message logged&quot; target level event
      * to the build listeners for this project.
      *
      * @param target   The target generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param throwable The exception that caused this message. May be <code>null</code>.
      * @param priority The priority of the message.
      * @since 1.7
      */
     protected void fireMessageLogged(Target target, String message,
             Throwable throwable, int priority) {
         BuildEvent event = new BuildEvent(target);
         event.setException(throwable);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Send a &quot;message logged&quot; task level event
      * to the build listeners for this project.
      *
      * @param task     The task generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Task task, String message, int priority) {
         fireMessageLogged(task, message, null, priority);
     }
 
     /**
      * Send a &quot;message logged&quot; task level event
      * to the build listeners for this project.
      *
      * @param task     The task generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param throwable The exception that caused this message. May be <code>null</code>.
      * @param priority The priority of the message.
      * @since 1.7
      */
     protected void fireMessageLogged(Task task, String message,
             Throwable throwable, int priority) {
         BuildEvent event = new BuildEvent(task);
         event.setException(throwable);
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
     public void registerThreadTask(Thread thread, Task task) {
         synchronized(threadTasks) {
             if (task != null) {
                 threadTasks.put(thread, task);
                 threadGroupTasks.put(thread.getThreadGroup(), task);
             } else {
                 threadTasks.remove(thread);
                 threadGroupTasks.remove(thread.getThreadGroup());
             }
         }
     }
 
     /**
      * Get the current task associated with a thread, if any.
      *
      * @param thread the thread for which the task is required.
      * @return the task which is currently registered for the given thread or
      *         null if no task is registered.
      */
     public Task getThreadTask(Thread thread) {
         synchronized(threadTasks) {
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
     }
 
 
     // Should move to a separate public class - and have API to add
     // listeners, etc.
     private static class AntRefTable extends Hashtable {
 
         AntRefTable() {
             super();
         }
 
         /** Returns the unmodified original object.
          * This method should be called internally to
          * get the &quot;real&quot; object.
          * The normal get method will do the replacement
          * of UnknownElement (this is similar with the JDNI
          * refs behavior).
          */
         private Object getReal(Object key) {
             return super.get(key);
         }
 
         /** Get method for the reference table.
          *  It can be used to hook dynamic references and to modify
          * some references on the fly--for example for delayed
          * evaluation.
          *
          * It is important to make sure that the processing that is
          * done inside is not calling get indirectly.
          *
          * @param key lookup key.
          * @return mapped value.
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
      * are called.
      * @param obj the object to invoke setProject(this) on.
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
 
     /**
      * Resolve the file relative to the project's basedir and return it as a
      * FileResource.
      * @param name the name of the file to resolve.
      * @return the file resource.
      * @since Ant 1.7
      */
     public Resource getResource(String name) {
         return new FileResource(getBaseDir(), name);
     }
 }
