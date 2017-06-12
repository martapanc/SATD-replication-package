diff --git a/WHATSNEW b/WHATSNEW
index b3c3263c5..578dfde19 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1557 +1,1562 @@
 Changes from Ant 1.7.x TO current SVN version
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 * Ant now requires Java 1.4 or later.
 
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
    Bugzilla Reports 32200.
 
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
 
  * if the dir attribute of a <fileset> point to a symbolic link and
    followsymlinks is set to false, the fileset will no longer be
    scanned and always seem empty.
    Bugzilla Report 45741.
 
  * the .NET tasks that have been deprecated since Ant 1.7.0 have been
    removed, please use the stand-alone Antlib you can find at
    http://ant.apache.org/antlibs/dotnet/index.html
    instead.
 
  * the logic of closing streams connected to forked processes (read
    the input and output of <exec> and friends) has been changed to
    deal with cases where child processes of the forked processes live
    longer than their parents and keep Ant from exiting.
    It is unlikely but possible that the changed logic breaks stream
    handling on certain Java VMs.
    Bugzilla issue 5003.
 
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
    Bugzilla Reports 32200, 45836
 
  * The IPlanetDeploymentTool didn't use the configured DTD locations.
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
 
  * when checking whether a jar is signed, <signjar> ignored the
    sigfile attribute.
    Bugzilla Report 44805.
 
  * When using JavaMail all <mail> tasks used the same mail host
    regardless of their configuration.
    Bugzilla Report 37970.
 
  * <signjar> and <issigned> didn't handle aliases with characters other
    than numbers, letters, hyphen or underscore properly.
    Bugzilla Report 45820.
 
  * <filterset> could miss multi-character begin tokens in some cases.
    Bugzilla Report 45094.
 
  * <depend> didn't close JARs that were part of the classpath.
    Bugzilla Report 45955.
 
  * in some cases <depend> would delete class files even if it didn't
    find the corresponding source files.
    Bugzilla Report 45916.
 
  * <javadoc> failed if the nested <bottom> or <head> contained line
    breaks.
    Bugzilla Report 43342.
 
  * encoding="auto" has been broken in <mail> since Ant 1.7.0 and only
    worked if JavaMail was available.
    Bugzilla Report 42389.
 
  * MailLogger could cause a NullPointerException.
    Bugzilla Report 44009.
 
  * <junit> didn't recognize failed assertions as failures if they
    caused subclasses of AssertionError to be thrown (like
    org.junit.ComparisonFailure that is thrown when assertEquals
    fails).
    Bugzilla Report 45028.
 
  * the Unix "ant" wrapper script failed to protect wildcards in
    command line arguments in some cases.
    Bugzilla Report 31601.
 
  * <cvstagdiff> crippled file names and could miss some entries if
    multiple modules have been specified.
    Bugzilla Report 35301.
 
  * Tasks with a "public void add(SomeType)" method failed to work as
    TaskContainers at the same time.
    Bugzilla Report 41647.
 
  * Tasks that implementes DynamicElemen or DynamicElementNS failed to
    work as TaskContainers at the same time.
    Bugzilla Report 41647.
 
  * combining SSL and authentication in <mail> and MailLogger failed in
    some setups.
    Bugzilla Report 46063.
 
  * if an error occurs while logging the buildFinished event, the
    original error is now logged to System.err.
    Bugzilla Report 25086.
 
  * <copy> failed with a NullPointerException when copying a resource
    without a name.  It will now fail with a meaningful error message.
    Bugzilla Report 39960.
 
  * <xslt> now uses the configured classpath to load the factory (when
    using TraX) before falling back to Ant's own classpath.
    Bugzilla Report 46172.
 
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
 
  * <patch> has a new optional failOnError attribute.
    Bugzilla Report 44772.
 
  * Antlib descriptors will now be parsed by the configured
    ProjectHelper if the implementation overrides the new
    canParseAntlibDescriptor and parseAntlibDescriptor methods.  If the
    configured helper doesn't override the methods, a new instance of
    ProjectHelper2 will be used just like in ant 1.7.1.
    Bugzilla Report 42208.
 
  * It is now possible to explicitly set the executable used by
    <signjar>.
    Bugzilla Report 39189.
 
  * <compositemapper>'s order of results is now predictable.
    Bugzilla Report 44873
 
  * a new <firstmatchmapper> has been added, which works similar to
    <compositemapper> but only returns the results of the first nested
    mapper that matches.
    Bugzilla Report 44873
 
  * <get> has a new maxtime attribute that terminates downloads that
    are taking too long.
    Bugzilla Report 45181.
 
  * <ftp> now supports selectors for remote directories as well.
    Bugzilla Report 44726.
 
  * In some cases Ant fails to rename files if the source or target
    file has just recently been closed on Windows.  It will now try to
    delete the offending file once again after giving the Java VM time
    to really close the file.
    Bugzilla Report 45960.
 
  * two new properties can be used to set the MIME-Type and charset
    used by MailLogger.
    Bugzilla Report 27211.
 
  * a new attribute of <mail> allows the task to succeed if it can
    reach at least one given recipient.
    Bugzilla Report 36446.
 
  * two new properties allow MailLogger to send a fixed text instead of
    the log file.
    Bugzilla Report 38029.
 
  * <cvsversion> is supposed to support CVSNT now.
    Bugzilla Report 31409.
 
  * <cvs>' port attribute should now work for all clients that use the
    environment variable CVS_PSERVER_PORT instead of the "official"
    CVS_CLIENT_PORT.
    Bugzilla Report 30124.
 
  * <cvsversion> now works for local repositories as well.
 
  * <cvstagdiff> has an option to ignore removed files now.
    Bugzilla Report 26257.
 
  * <cvs> and friends now support modules with spaces in their names
    via nested <module> elements.
 
  * A new attribute "ignoreEmpty" controls how <concat> deals when
    there are no resources to concatenate.  If it is set to false, the
    destination file will be created regardless, which reinstates the
    behavior of Ant 1.7.0.
    Bugzilla Report 46010.
 
  * If the new remote attribute is set to true, <cvschangelog> can now
    work against a remote repository without any working copy.
    Bugzilla Report 27419.
 
  * start and end tags can now be used instead of dates in
    <cvschangelog>.
    Bugzilla Report 27419.
 
  * MailLogger and <mail> can now optionally enable support for
    STARTTLS.
    Bugzilla Report 46063.
 
  * <import> has new attributes "as" and "prefixSeparator" that can be
    used to control the prefix prepended to the imported target's
    names.
 
  * a new task <include> provides an alternative to <import> that
    should be preferred when you don't want to override any targets.
 
  * delete has a new attribute removeNotFollowedSymlink.  If set to
    true, symbolic links not followed (because followSymlinks was false
    or the number of symlinks was too big) will be removed.
    Bugzilla Report 36658.
 
  * the os and osfamily attributes of <chown>, <chgrp>, <chmod> and
    <attrib> can now be used to run the commands on operating systems
    other than their "native" environment, i.e. non-Unix or non-Windows
    operating systems repsectively.
    Bugzilla Report 7624.
 
  * a new resource collection <mappedresources> generalizes the prefix
    and fullpath attributes of <zipfileset> to arbitrary mappers that
    can be applied to arbitrary resource collections.
    Bugzilla Report 4240.
 
  * <tarfileset> and <zipfileset> have a new attribute
    errorOnMissingArchive that allows "optional" filesets that don't
    break the build if the archive doesn't exist.
    Bugzilla Report 46091.
 
  * <javadoc> has new attributes that correspond to the
    -docfilessubdirs and -excludedocfilessubdir command line arguments.
    Bugzilla Report 34455.
 
  * <xslt> now fails early if a specified stylesheet doesn't exist.
    Bugzilla Report 34525.
 
+ * <xslt> now has an option to supress transformer warnings.  This
+   option only has an effect for processors that support this feature;
+   the "trax" processor included with Ant does support it.
+   Bugzilla Report 18897.
+
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
 
 * New GreedyInputHandler added.
 
 * Add textfile attribute to the <filesmatch> condition. When true, the text
   contents of the two files are compared, ignoring line ending differences.
 
 * New <resourcesmatch> condition.
 
 * Added the onmissingfiltersfile attribute to filterset. Bugzilla report 19845.
 
 * Added the inline handler element to the input task.
 
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
 
 * <scp> now optionally supports the sftp protocol.  Bugzilla Report 39373.
 
 * Resources can now be used to indicate the location of the stylesheet to use
   in <xslt>. Bugzilla Report 39407.
 
 * New <antversion> condition. Bugzilla report 32804.
 
 * ReplaceTokens should allow properties files. Bugzilla report 39688.
 
 * FTP Account could not be specified in ant FTP task. Bugzilla report 39720.
 
 * Minor performance updates. Bugzilla report 39565.
 
 * New deleteonexit attribute for the <tempfile> task. Bugzilla report 39842.
   Remember that the exit of the JVM can be a long time coming,
   especially under an IDE. Don't rely on this being called.
 
 * <scriptdef>-created scripts have support for nested text. All text
   passed to a scripted task can be accessed via self.text.
 
 * <fixcrlf> now supports an outputencoding attribute.  Bugzilla report 39697.
 
 * <junitreport> now supports nested XSL parameters. Bugzilla report 39708.
 
 * <javacc> has a jdkversion attribute to pass the desired JDK version
   down to javacc.  Bugzilla report 38715.
 
 * <cvs> prints passfile info at -verbose level instead of -info. Bugzilla
   report 35268
 
 * When <javac> can't find the compiler class, it prints out java.home for
   immediate diagnostics
 
 * Ant launcher now supports a -main attribute so that you can specify
   an extension class to the built in org.apache.tools.ant.Main
   class. This class must implement the interface AntMain
 
 Changes from Ant 1.6.4 to Ant 1.6.5
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 Fixed bugs:
 -----------
 
diff --git a/docs/manual/CoreTasks/style.html b/docs/manual/CoreTasks/style.html
index f7961a353..36b56e8e3 100644
--- a/docs/manual/CoreTasks/style.html
+++ b/docs/manual/CoreTasks/style.html
@@ -1,485 +1,493 @@
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
 <link rel="stylesheet" type="text/css" href="../stylesheets/style.css">
 <title>XSLT Task</title>
 </head>
 
 <body>
 
 <h2><a name="style">XSLT</a></h2>
 <p><em>The name <code>style</code> is a deprecated name for the same task.</em></p>
 <h3>Description</h3>
 <p>Process a set of documents via XSLT.</p>
 <p>This is useful for building views of XML based documentation,
 or for generating code.</p>
 <p><b>Note:</b> If you are using JDK 1.4 or higher, this task does not require external libraries
 not supplied in the Ant distribution. However, often the built in XSL engine is not as up
 to date as a fresh download, so an update is still highly recommended.
 See <a href="../install.html#librarydependencies">Library Dependencies</a> for more information.</p>
 <p>It is possible to refine the set of files that are being processed. This can be
 done with the <i>includes</i>, <i>includesfile</i>, <i>excludes</i>, <i>excludesfile</i> and <i>defaultexcludes</i>
 attributes. With the <i>includes</i> or <i>includesfile</i> attribute you specify the files you want to
 have included by using patterns. The <i>exclude</i> or <i>excludesfile</i> attribute is used to specify
 the files you want to have excluded. This is also done with patterns. And
 finally with the <i>defaultexcludes</i> attribute, you can specify whether you
 want to use default exclusions or not. See the section on <a
 href="../dirtasks.html#directorybasedtasks">directory based tasks</a>, on how the
 inclusion/exclusion of files works, and how to write patterns.</p>
 <p>This task forms an implicit <a href="../CoreTypes/fileset.html">FileSet</a> and supports all
   attributes of <code>&lt;fileset&gt;</code> (<code>dir</code> becomes <code>basedir</code>)
   as well as the nested <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code>
   and <code>&lt;patternset&gt;</code> elements.</p>
 
 <p><b>Note</b>: Unlike other similar tasks, this task treats
 directories that have been matched by the include/exclude patterns of
 the implicit fileset in a special way.  It will apply the stylesheets
 to all files contain in them as well.  Since the default include
 pattern is <code>**</code> this means it will apply the stylesheet to
 all files.  If you specify an excludes pattern, it may still work on
 the files matched by those patterns because the parent directory has
 been matched.  If this behavior is not what you want, set the
 scanincludedirectories attribute to false.</p>
 
 <p>Starting with Ant 1.7 this task supports nested <a
 href="../CoreTypes/resources.html#collection">resource collection</a>s
 in addition to (or instead of, depending on the useImplicitFileset
 attribute) the implicit fileset formed by this task.</p>
 
 <p>This task supports the use of a nested <code>&lt;param&gt;</code> element which is used to pass values
   to an <code>&lt;xsl:param&gt;</code> declaration.</p>
 <p>This task supports the use of a nested <a href="../CoreTypes/xmlcatalog.html">xmlcatalog</a>
 element which is used to perform Entity and URI resolution.</p>
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">basedir</td>
     <td valign="top">where to find the source XML file, default is the
       project's basedir.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">directory in which to store the results.</td>
     <td align="center" valign="top">Yes, unless in and out have been
       specified.</td>
   </tr>
   <tr>
     <td valign="top">extension</td>
     <td valign="top">desired file extension to be used for the targets. If not
       specified, the default is &quot;.html&quot;.  Will be ignored if
       a nested <code>&lt;mapper&gt;</code> has been specified.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">style</td>
     <td valign="top">name of the stylesheet to use - given either relative
       to the project's basedir or as an absolute path.<br/>
       <br/>
       Alternatively, a nested element which ant can interpret as a resource
       can be used to indicate where to find the stylesheet<br/>
       <em>deprecated variation :</em> <br/>
       If the stylesheet cannot be found, and if you have specified the
       attribute basedir for the task, ant will assume that the style
       attribute is relative to the basedir of the task.
     </td>
     <td align="center" valign="top">No, if the location of
         the stylesheet is specified using a nested &lt;style&gt; element</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">the classpath to use when looking up the XSLT
       processor.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">classpathref</td>
     <td valign="top">the classpath to use, given as <a
       href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">force</td>
     <td valign="top">Recreate target files, even if they are newer
       than their corresponding source files or the stylesheet.</td>
     <td valign="top" align="center">No; default is false</td>
   </tr>
   <tr>
     <td valign="top">processor</td>
 
     <td valign="top">name of the XSLT processor to use.
       Permissible value is :<ul>
       <li>&quot;trax&quot; for a TraX compliant processor (ie JAXP interface
       implementation such as Xalan 2 or Saxon)</li></ul>
       Defaults to trax.
       <br/>
       Support for xalan1 has been removed in ant 1.7.
       </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includes</td>
     <td valign="top">comma- or space-separated list of patterns of files that must be included.
       All files are included when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">includesfile</td>
     <td valign="top">the name of a file. Each line of this file is taken to be
       an include pattern</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludes</td>
     <td valign="top">comma- or space-separated list of patterns of files that must be excluded.
       No files (except default excludes) are excluded when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludesfile</td>
     <td valign="top">the name of a file. Each line of this file is taken to be
       an exclude pattern</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">defaultexcludes</td>
     <td valign="top">indicates whether default excludes should be used or not
       (&quot;yes&quot;/&quot;no&quot;). Default excludes are used when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">in</td>
     <td valign="top">specifies a single XML document to be styled. Should be used
       with the out attribute.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">out</td>
     <td valign="top">specifies the output name for the styled result from the
       in attribute.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">scanincludeddirectories</td>
     <td valign="top">If any directories are matched by the
       includes/excludes patterns, try to transform all files in these
       directories.  Default is <code>true</code></td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">reloadstylesheet</td>
     <td valign="top">Control whether the stylesheet transformer is created
     anew for every transform opertaion. If you set this to true, performance may
     suffer, but you may work around a bug in certain Xalan-J versions.
     Default is <code>false</code>.  <em>Since Ant 1.5.2</em>.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">useImplicitFileset</td>
     <td valign="top">Whether the implicit fileset formed by this task
     shall be used.  If you set this to false you must use nested
     resource collections - or the in attribute, in which case this
     attribute has no impact anyway.  Default is <code>true</code>.
     <em>Since Ant 1.7</em>.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">filenameparameter</td>
     <td valign="top">Specifies a xsl parameter for accessing the name
     of the current processed file. If not set, the file name is not
     passed to the transformation.
     <em>Since Ant 1.7</em>.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">filedirparameter</td>
     <td valign="top">Specifies a xsl parameter for accessing the directory
     of the current processed file. For files in the current directory a
     value of '.' will be passed to the transformation.
     If not set, the directory is not passed to the transformation. 
     <em>Since Ant 1.7</em>.</td>
     <td valign="top" align="center">No</td>
   </tr>
+  <tr>
+    <td valign="top">supressWarnings</td>
+    <td valign="top">Whether processor warnings shall be suppressed.
+    This option requires support by the processor, it is supported by
+    the trax processor bundled with Ant. 
+    <em>Since Ant 1.8.0</em>.</td>
+    <td valign="top" align="center">No, default is false.</td>
+  </tr>
 </table>
 <h3>Parameters specified as nested elements</h3>
 
 <h4>any <a href="../CoreTypes/resources.html#collection">resource
 collection</a></h4>
 
 <p><em>since Ant 1.7</em></p>
 
 <p>Use resource collections to specify resources that the stylesheet
 should be applied to.  Use a nested mapper and the task's destdir
 attribute to specify the output files.</p>
 
 <h4>classpath</h4>
 <p>The classpath to load the processor from can be specified via a
 nested <code>&lt;classpath&gt;</code>, as well - that is, a
 <a href="../using.html#path">path</a>-like structure.</p>
 
 <h4>xmlcatalog</h4>
 <p>The <a href="../CoreTypes/xmlcatalog.html">xmlcatalog</a>
 element is used to perform Entity and URI resolution.</p>
 
 <h4>param</h4>
 <p>Param is used to pass a parameter to the XSL stylesheet.</p>
 <blockquote>
 <h4>Parameters</h4>
 <table width="60%" border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">Name of the XSL parameter</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">expression</td>
     <td valign="top">Text value to be placed into the param.<br>
     Was originally intended to be an XSL expression.</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">if</td>
     <td valign="top">The param will only passed if this property is set.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">unless</td>
     <td valign="top">The param will only passed unless this property is set.</td>
     <td align="center" valign="top">No</td>
   </tr>
 
 </table>
 </blockquote>
 
 <h4>outputproperty ('trax' processors only)</h4>
 <p>Used to specify how you wish the result tree to be output
 as specified in the <a href="http://www.w3.org/TR/xslt#output">
 XSLT specifications</a>.
 <blockquote>
 <h4>Parameters</h4>
 <table width="60%" border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">Name of the property</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">value</td>
     <td valign="top">value of the property.</td>
     <td align="center" valign="top">Yes</td>
   </tr>
 </table>
 </blockquote>
 
 <h4>factory ('trax' processors only)</h4>
 Used to specify factory settings.
 <blockquote>
 <h4>Parameters</h4>
 <table width="60%" border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">fully qualified classname of the
     transformer factory to use. For example
       <tt>org.apache.xalan.processor.TransformerFactoryImpl</tt>
        or <tt>org.apache.xalan.xsltc.trax.TransformerFactoryImpl</tt>
        or <tt>net.sf.saxon.TransformerFactoryImpl</tt>...
     </td>
     <td align="center" valign="top">No. Defaults to the JAXP lookup mechanism.</td>
   </tr>
 </table>
 <h3>Parameters specified as nested elements</h3>
 <h4>attribute </h4>
 <p>Used to specify settings of the processor factory.
 The attribute names and values are entirely processor specific
 so you must be aware of the implementation to figure them out.
 Read the documentation of your processor.
 For example, in Xalan 2.x:
 <ul>
 <li>http://xml.apache.org/xalan/features/optimize (boolean)</li>
 <li>http://xml.apache.org/xalan/features/incremental (boolean)</li>
 <li>...</li>
 </ul>
 And in Saxon 7.x:
 <ul>
 <li>http://saxon.sf.net/feature/allow-external-functions (boolean)</li>
 <li>http://saxon.sf.net/feature/timing (boolean)</li>
 <li>http://saxon.sf.net/feature/traceListener (string)</li>
 <li>http://saxon.sf.net/feature/treeModel (integer)</li>
 <li>http://saxon.sf.net/feature/linenumbering (integer)</li>
 <li>...</li>
 </ul>
 <blockquote>
 <h4>Parameters</h4>
 <table width="60%" border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">Name of the attribute</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">value</td>
     <td valign="top">value of the attribute.</td>
     <td align="center" valign="top">Yes</td>
   </tr>
 </table>
 </blockquote>
 </blockquote>
 <h4>mapper</h4>
 
 <p><em>since Ant 1.6.2</em></p>
 
 <p>You can define filename transformations by using a nested <a
 href="../CoreTypes/mapper.html">mapper</a> element. The default mapper
 used by <code>&lt;xslt&gt;</code> removes the file extension from the
 source file and adds the extension specified via the extension
 attribute.</p>
 
 <h4>style</h4>
 
 <p><em>Since Ant 1.7</em></p>
 
 <p>The nested style element can be used to specify your stylesheet in terms
 of Ant's <a href="../CoreTypes/resources.html">resource</a> types.  With
 this element, the stylesheet should be specified as a nested resource or
 single-element collection.  Alternatively, use the <code>refid</code> to
 specify the resource or collection as a reference.</p>
 
 <h3>Examples</h3>
 <blockquote>
   <pre>
 &lt;xslt basedir=&quot;doc&quot; destdir=&quot;build/doc&quot;
        extension=&quot;.html&quot; style=&quot;style/apache.xsl&quot;/&gt;</pre>
   <h4>Using an xmlcatalog</h4>
   <pre>
 &lt;xslt basedir=&quot;doc&quot; destdir=&quot;build/doc&quot;
       extension=&quot;.html&quot; style=&quot;style/apache.xsl&quot;&gt;
   &lt;xmlcatalog refid=&quot;mycatalog&quot;/&gt;
 &lt;/xslt&gt;
 
 &lt;xslt basedir=&quot;doc&quot; destdir=&quot;build/doc&quot;
    extension=&quot;.html&quot; style=&quot;style/apache.xsl&quot;&gt;
    &lt;xmlcatalog&gt;
        &lt;dtd
          publicId=&quot;-//ArielPartners//DTD XML Article V1.0//EN&quot;
          location=&quot;com/arielpartners/knowledgebase/dtd/article.dtd&quot;/&gt;
    &lt;/xmlcatalog&gt;
 &lt;/xslt&gt;
 </pre>
   <h4>Using XSL parameters</h4>
 <pre>
 &lt;xslt basedir=&quot;doc&quot; destdir=&quot;build/doc&quot;
       extension=&quot;.html&quot; style=&quot;style/apache.xsl&quot;&gt;
   &lt;param name=&quot;date&quot; expression=&quot;07-01-2000&quot;/&gt;
 &lt;/xslt&gt;</pre>
 
   <p>Then if you declare a global parameter &quot;date&quot; with the top-level
   element &lt;xsl:param name=&quot;date&quot;/&gt;, the variable
   <code>$date</code> will subsequently have the value 07-01-2000.
   </p>
 
   <h4>Using output properties</h4>
 <pre>
 &lt;xslt in=&quot;doc.xml&quot; out=&quot;build/doc/output.xml&quot;
       style=&quot;style/apache.xsl&quot;&gt;
   &lt;outputproperty name=&quot;method&quot; value=&quot;xml&quot;/&gt;
   &lt;outputproperty name=&quot;standalone&quot; value=&quot;yes&quot;/&gt;
   &lt;outputproperty name=&quot;encoding&quot; value=&quot;iso8859_1&quot;/&gt;
   &lt;outputproperty name=&quot;indent&quot; value=&quot;yes&quot;/&gt;
 &lt;/xslt&gt;
 </pre>
 
   <h4>Using factory settings</h4>
 <pre>
 &lt;xslt in=&quot;doc.xml&quot; out=&quot;build/doc/output.xml&quot;
       style=&quot;style/apache.xsl&quot;&gt;
   &lt;factory name=&quot;org.apache.xalan.processor.TransformerFactoryImpl&quot;&gt;
     &lt;attribute name=&quot;http://xml.apache.org/xalan/features/optimize&quot; value=&quot;true&quot;/&gt;
   &lt;/factory&gt;
 &lt;/xslt&gt;</pre>
 
   <h4>Using a mapper</h4>
 <pre>
 &lt;xslt basedir=&quot;in&quot; destdir=&quot;out&quot;
       style=&quot;style/apache.xsl&quot;&gt;
   &lt;mapper type=&quot;glob&quot; from=&quot;*.xml.en&quot; to=&quot;*.html.en&quot;/&gt;
 &lt;/xslt&gt;</pre>
 
   <h4>Using a nested resource to define the stylesheet</h4>
     <pre>
 &lt;xslt in="data.xml" out="${out.dir}/out.xml"&gt;
     &lt;style&gt;
         &lt;url url="${printParams.xsl.url}"/&gt;
     &lt;/style&gt;
     &lt;param name="set" expression="value"/&gt;
 &lt;/xslt&gt;</pre>
 
   <h4>Print the current processed file name</h4>
 <pre>
 &lt;project&gt;
   &lt;xslt style=&quot;printFilename.xsl&quot; destdir=&quot;out&quot; basedir=&quot;in&quot; extension=&quot;.txt&quot;
         filenameparameter=&quot;filename&quot;
         filedirparameter=&quot;filedir&quot;
   /&gt;
 &lt;/project&gt;
 
 &lt;xsl:stylesheet
   version=&quot;1.0&quot;
   xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot;&gt;
 
     &lt;xsl:param name=&quot;filename&quot;&gt;&lt;/xsl:param&gt;
     &lt;xsl:param name=&quot;filedir&quot;&gt;.&lt;/xsl:param&gt;
 
 &lt;xsl:template match=&quot;/&quot;&gt;
   Current file is &lt;xsl:value-of select=&quot;$filename&quot;/&gt; in directory &lt;xsl:value-of select=&quot;$filedir&quot;/&gt;.
 &lt;/xsl:template&gt;
 
 &lt;/xsl:stylesheet&gt;
 </pre>
 
 </blockquote>
 
 
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java b/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
index bfb059abc..eb1d4b446 100644
--- a/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
+++ b/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
@@ -1,1198 +1,1223 @@
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
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.Vector;
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.DynamicConfigurator;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Mapper;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.XMLCatalog;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.Resources;
 import org.apache.tools.ant.types.resources.Union;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.ResourceUtils;
 
 /**
  * Processes a set of XML documents via XSLT. This is
  * useful for building views of XML based documentation.
  *
  *
  * @since Ant 1.1
  *
  * @ant.task name="xslt" category="xml"
  */
 
 public class XSLTProcess extends MatchingTask implements XSLTLogger {
     /** destination directory */
     private File destDir = null;
 
     /** where to find the source XML file, default is the project's basedir */
     private File baseDir = null;
 
     /** XSL stylesheet as a filename */
     private String xslFile = null;
 
     /** XSL stylesheet as a {@link org.apache.tools.ant.types.Resource} */
     private Resource xslResource = null;
 
     /** extension of the files produced by XSL processing */
     private String targetExtension = ".html";
 
     /** name for XSL parameter containing the filename */
     private String fileNameParameter = null;
 
     /** name for XSL parameter containing the file directory */
     private String fileDirParameter = null;
 
     /** additional parameters to be passed to the stylesheets */
     private Vector params = new Vector();
 
     /** Input XML document to be used */
     private File inFile = null;
 
     /** Output file */
     private File outFile = null;
 
     /** The name of the XSL processor to use */
     private String processor;
 
     /** Classpath to use when trying to load the XSL processor */
     private Path classpath = null;
 
     /** The Liason implementation to use to communicate with the XSL
      *  processor */
     private XSLTLiaison liaison;
 
     /** Flag which indicates if the stylesheet has been loaded into
      *  the processor */
     private boolean stylesheetLoaded = false;
 
     /** force output of target files even if they already exist */
     private boolean force = false;
 
     /** XSL output properties to be used */
     private Vector outputProperties = new Vector();
 
     /** for resolving entities such as dtds */
     private XMLCatalog xmlCatalog = new XMLCatalog();
 
     /** Name of the TRAX Liaison class */
     private static final String TRAX_LIAISON_CLASS =
                         "org.apache.tools.ant.taskdefs.optional.TraXLiaison";
 
     /** Utilities used for file operations */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * Whether to style all files in the included directories as well.
      *
      * @since Ant 1.5
      */
     private boolean performDirectoryScan = true;
 
     /**
      * factory element for TraX processors only
      * @since Ant 1.6
      */
     private Factory factory = null;
 
     /**
      * whether to reuse Transformer if transforming multiple files.
      * @since 1.5.2
      */
     private boolean reuseLoadedStylesheet = true;
 
     /**
      * AntClassLoader for the nested &lt;classpath&gt; - if set.
      *
      * <p>We keep this here in order to reset the context classloader
      * in execute.  We can't use liaison.getClass().getClassLoader()
      * since the actual liaison class may have been loaded by a loader
      * higher up (system classloader, for example).</p>
      *
      * @since Ant 1.6.2
      */
     private AntClassLoader loader = null;
 
     /**
      * Mapper to use when a set of files gets processed.
      *
      * @since Ant 1.6.2
      */
     private Mapper mapperElement = null;
 
     /**
      * Additional resource collections to process.
      *
      * @since Ant 1.7
      */
     private Union resources = new Union();
 
     /**
      * Whether to use the implicit fileset.
      *
      * @since Ant 1.7
      */
     private boolean useImplicitFileset = true;
 
     /**
      * The default processor is trax
      * @since Ant 1.7
      */
     public static final String PROCESSOR_TRAX = "trax";
 
     /**
+     * whether to suppress warnings.
+     *
+     * @since Ant 1.8.0
+     */
+    private boolean suppressWarnings = false;
+
+    /**
      * Creates a new XSLTProcess Task.
      */
     public XSLTProcess() {
     } //-- XSLTProcess
 
     /**
      * Whether to style all files in the included directories as well;
      * optional, default is true.
      *
      * @param b true if files in included directories are processed.
      * @since Ant 1.5
      */
     public void setScanIncludedDirectories(boolean b) {
         performDirectoryScan = b;
     }
 
     /**
      * Controls whether the stylesheet is reloaded for every transform.
      *
      * <p>Setting this to true may get around a bug in certain
      * Xalan-J versions, default is false.</p>
      * @param b a <code>boolean</code> value
      * @since Ant 1.5.2
      */
     public void setReloadStylesheet(boolean b) {
         reuseLoadedStylesheet = !b;
     }
 
     /**
      * Defines the mapper to map source to destination files.
      * @param mapper the mapper to use
      * @exception BuildException if more than one mapper is defined
      * @since Ant 1.6.2
      */
     public void addMapper(Mapper mapper) {
         if (mapperElement != null) {
             throw new BuildException("Cannot define more than one mapper", getLocation());
         }
         mapperElement = mapper;
     }
 
     /**
      * Adds a collection of resources to style in addition to the
      * given file or the implicit fileset.
      *
      * @param rc the collection of resources to style
      * @since Ant 1.7
      */
     public void add(ResourceCollection rc) {
         resources.add(rc);
     }
 
     /**
      * Add a nested &lt;style&gt; element.
      * @param rc the configured Resources object represented as &lt;style&gt;.
      * @since Ant 1.7
      */
     public void addConfiguredStyle(Resources rc) {
         if (rc.size() != 1) {
             throw new BuildException(
                     "The style element must be specified with exactly one nested resource.");
         }
         setXslResource((Resource) rc.iterator().next());
     }
 
     /**
      * API method to set the XSL Resource.
      * @param xslResource Resource to set as the stylesheet.
      * @since Ant 1.7
      */
     public void setXslResource(Resource xslResource) {
         this.xslResource = xslResource;
     }
 
     /**
      * Adds a nested filenamemapper.
      * @param fileNameMapper the mapper to add
      * @exception BuildException if more than one mapper is defined
      * @since Ant 1.7.0
      */
     public void add(FileNameMapper fileNameMapper) throws BuildException {
        Mapper mapper = new Mapper(getProject());
        mapper.add(fileNameMapper);
        addMapper(mapper);
     }
 
     /**
      * Executes the task.
      *
      * @exception BuildException if there is an execution problem.
      * @todo validate that if either in or our is defined, then both are
      */
     public void execute() throws BuildException {
         if ("style".equals(getTaskType())) {
             log("Warning: the task name <style> is deprecated. Use <xslt> instead.",
                     Project.MSG_WARN);
         }
         File savedBaseDir = baseDir;
 
         DirectoryScanner scanner;
         String[]         list;
         String[]         dirs;
 
         String baseMessage =
             "specify the stylesheet either as a filename in style attribute "
             + "or as a nested resource";
 
         if (xslResource == null && xslFile == null) {
             throw new BuildException(baseMessage, getLocation());
         }
         if (xslResource != null && xslFile != null) {
             throw new BuildException(baseMessage + " but not as both", getLocation());
         }
         if (inFile != null && !inFile.exists()) {
             throw new BuildException("input file " + inFile + " does not exist", getLocation());
         }
         try {
             Resource styleResource;
             if (baseDir == null) {
                 baseDir = getProject().getBaseDir();
             }
             liaison = getLiaison();
 
             // check if liaison wants to log errors using us as logger
             if (liaison instanceof XSLTLoggerAware) {
                 ((XSLTLoggerAware) liaison).setLogger(this);
             }
             log("Using " + liaison.getClass().toString(), Project.MSG_VERBOSE);
 
             if (xslFile != null) {
                 // If we enter here, it means that the stylesheet is supplied
                 // via style attribute
                 File stylesheet = getProject().resolveFile(xslFile);
                 if (!stylesheet.exists()) {
                     stylesheet = FILE_UTILS.resolveFile(baseDir, xslFile);
                     /*
                      * shouldn't throw out deprecation warnings before we know,
                      * the wrong version has been used.
                      */
                     if (stylesheet.exists()) {
                         log("DEPRECATED - the 'style' attribute should be "
                             + "relative to the project's");
                         log("             basedir, not the tasks's basedir.");
                     }
                 }
                 FileResource fr = new FileResource();
                 fr.setProject(getProject());
                 fr.setFile(stylesheet);
                 styleResource = fr;
             } else {
                 styleResource = xslResource;
             }
 
             if (!styleResource.isExists()) {
                 throw new BuildException("stylesheet " + styleResource
                                          + " doesn't exist.");
             }
 
             // if we have an in file and out then process them
             if (inFile != null && outFile != null) {
                 process(inFile, outFile, styleResource);
                 return;
             }
             /*
              * if we get here, in and out have not been specified, we are
              * in batch processing mode.
              */
 
             //-- make sure destination directory exists...
             checkDest();
 
             if (useImplicitFileset) {
                 scanner = getDirectoryScanner(baseDir);
                 log("Transforming into " + destDir, Project.MSG_INFO);
 
                 // Process all the files marked for styling
                 list = scanner.getIncludedFiles();
                 for (int i = 0; i < list.length; ++i) {
                     process(baseDir, list[i], destDir, styleResource);
                 }
                 if (performDirectoryScan) {
                     // Process all the directories marked for styling
                     dirs = scanner.getIncludedDirectories();
                     for (int j = 0; j < dirs.length; ++j) {
                         list = new File(baseDir, dirs[j]).list();
                         for (int i = 0; i < list.length; ++i) {
                             process(baseDir, dirs[j] + File.separator + list[i], destDir,
                                     styleResource);
                         }
                     }
                 }
             } else { // only resource collections, there better be some
                 if (resources.size() == 0) {
                     throw new BuildException("no resources specified");
                 }
             }
             processResources(styleResource);
         } finally {
             if (loader != null) {
                 loader.resetThreadContextLoader();
                 loader.cleanup();
                 loader = null;
             }
             liaison = null;
             stylesheetLoaded = false;
             baseDir = savedBaseDir;
         }
     }
 
     /**
      * Set whether to check dependencies, or always generate;
      * optional, default is false.
      *
      * @param force true if always generate.
      */
     public void setForce(boolean force) {
         this.force = force;
     }
 
     /**
      * Set the base directory;
      * optional, default is the project's basedir.
      *
      * @param dir the base directory
      **/
     public void setBasedir(File dir) {
         baseDir = dir;
     }
 
     /**
      * Set the destination directory into which the XSL result
      * files should be copied to;
      * required, unless <tt>in</tt> and <tt>out</tt> are
      * specified.
      * @param dir the name of the destination directory
      **/
     public void setDestdir(File dir) {
         destDir = dir;
     }
 
     /**
      * Set the desired file extension to be used for the target;
      * optional, default is html.
      * @param name the extension to use
      **/
     public void setExtension(String name) {
         targetExtension = name;
     }
 
     /**
      * Name of the stylesheet to use - given either relative
      * to the project's basedir or as an absolute path; required.
      *
      * @param xslFile the stylesheet to use
      */
     public void setStyle(String xslFile) {
         this.xslFile = xslFile;
     }
 
     /**
      * Set the optional classpath to the XSL processor
      *
      * @param classpath the classpath to use when loading the XSL processor
      */
     public void setClasspath(Path classpath) {
         createClasspath().append(classpath);
     }
 
     /**
      * Set the optional classpath to the XSL processor
      *
      * @return a path instance to be configured by the Ant core.
      */
     public Path createClasspath() {
         if (classpath == null) {
             classpath = new Path(getProject());
         }
         return classpath.createPath();
     }
 
     /**
      * Set the reference to an optional classpath to the XSL processor
      *
      * @param r the id of the Ant path instance to act as the classpath
      *          for loading the XSL processor
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Set the name of the XSL processor to use; optional, default trax.
      * Other values are "xalan" for Xalan1
      *
      * @param processor the name of the XSL processor
      */
     public void setProcessor(String processor) {
         this.processor = processor;
     }
 
     /**
      * Whether to use the implicit fileset.
      *
      * <p>Set this to false if you want explicit control with nested
      * resource collections.</p>
      * @param useimplicitfileset set to true if you want to use implicit fileset
      * @since Ant 1.7
      */
     public void setUseImplicitFileset(boolean useimplicitfileset) {
         useImplicitFileset = useimplicitfileset;
     }
 
     /**
      * Add the catalog to our internal catalog
      *
      * @param xmlCatalog the XMLCatalog instance to use to look up DTDs
      */
     public void addConfiguredXMLCatalog(XMLCatalog xmlCatalog) {
         this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
     }
 
     /**
      * Pass the filename of the current processed file as a xsl parameter
      * to the transformation. This value sets the name of that xsl parameter.
      *
      * @param fileNameParameter name of the xsl parameter retrieving the
      *                          current file name
      */
     public void setFileNameParameter(String fileNameParameter) {
         this.fileNameParameter = fileNameParameter;
     }
 
     /**
      * Pass the directory name of the current processed file as a xsl parameter
      * to the transformation. This value sets the name of that xsl parameter.
      *
      * @param fileDirParameter name of the xsl parameter retrieving the
      *                         current file directory
      */
     public void setFileDirParameter(String fileDirParameter) {
         this.fileDirParameter = fileDirParameter;
     }
 
     /**
+     * Whether to suppress warning messages of the processor.
+     *
+     * @since Ant 1.8.0
+     */
+    public void setSuppressWarnings(boolean b) {
+        suppressWarnings = b;
+    }
+
+    /**
+     * Whether to suppress warning messages of the processor.
+     *
+     * @since Ant 1.8.0
+     */
+    public boolean getSuppressWarnings() {
+        return suppressWarnings;
+    }    
+
+    /**
      * Load processor here instead of in setProcessor - this will be
      * called from within execute, so we have access to the latest
      * classpath.
      *
      * @param proc the name of the processor to load.
      * @exception Exception if the processor cannot be loaded.
      */
     private void resolveProcessor(String proc) throws Exception {
         String classname;
         if (proc.equals(PROCESSOR_TRAX)) {
             classname = TRAX_LIAISON_CLASS;
         } else {
             //anything else is a classname
             classname = proc;
         }
         Class clazz = loadClass(classname);
         liaison = (XSLTLiaison) clazz.newInstance();
     }
 
     /**
      * Load named class either via the system classloader or a given
      * custom classloader.
      *
      * As a side effect, the loader is set as the thread context classloader
      * @param classname the name of the class to load.
      * @return the requested class.
      * @exception Exception if the class could not be loaded.
      */
     private Class loadClass(String classname) throws Exception {
         if (classpath == null) {
             return Class.forName(classname);
         }
         loader = getProject().createClassLoader(classpath);
         loader.setThreadContextLoader();
         return Class.forName(classname, true, loader);
     }
 
     /**
      * Specifies the output name for the styled result from the
      * <tt>in</tt> attribute; required if <tt>in</tt> is set
      *
      * @param outFile the output File instance.
      */
     public void setOut(File outFile) {
         this.outFile = outFile;
     }
 
     /**
      * specifies a single XML document to be styled. Should be used
      * with the <tt>out</tt> attribute; ; required if <tt>out</tt> is set
      *
      * @param inFile the input file
      */
     public void setIn(File inFile) {
         this.inFile = inFile;
     }
 
     /**
      * Throws a BuildException if the destination directory hasn't
      * been specified.
      * @since Ant 1.7
      */
     private void checkDest() {
         if (destDir == null) {
             String msg = "destdir attributes must be set!";
             throw new BuildException(msg);
         }
     }
 
     /**
      * Styles all existing resources.
      *
      * @param stylesheet style sheet to use
      * @since Ant 1.7
      */
     private void processResources(Resource stylesheet) {
         Iterator iter = resources.iterator();
         while (iter.hasNext()) {
             Resource r = (Resource) iter.next();
             if (!r.isExists()) {
                 continue;
             }
             File base = baseDir;
             String name = r.getName();
             FileProvider fp = (FileProvider) r.as(FileProvider.class);
             if (fp != null) {
                 FileResource f = ResourceUtils.asFileResource(fp);
                 base = f.getBaseDir();
                 if (base == null) {
                     name = f.getFile().getAbsolutePath();
                 }
             }
             process(base, name, destDir, stylesheet);
         }
     }
 
     /**
      * Processes the given input XML file and stores the result
      * in the given resultFile.
      *
      * @param baseDir the base directory for resolving files.
      * @param xmlFile the input file
      * @param destDir the destination directory
      * @param stylesheet the stylesheet to use.
      * @exception BuildException if the processing fails.
      */
     private void process(File baseDir, String xmlFile, File destDir, Resource stylesheet)
             throws BuildException {
 
         File   outF = null;
         File   inF = null;
 
         try {
             long styleSheetLastModified = stylesheet.getLastModified();
             inF = new File(baseDir, xmlFile);
 
             if (inF.isDirectory()) {
                 log("Skipping " + inF + " it is a directory.", Project.MSG_VERBOSE);
                 return;
             }
             FileNameMapper mapper = null;
             if (mapperElement != null) {
                 mapper = mapperElement.getImplementation();
             } else {
                 mapper = new StyleMapper();
             }
 
             String[] outFileName = mapper.mapFileName(xmlFile);
             if (outFileName == null || outFileName.length == 0) {
                 log("Skipping " + inFile + " it cannot get mapped to output.", Project.MSG_VERBOSE);
                 return;
             } else if (outFileName == null || outFileName.length > 1) {
                 log("Skipping " + inFile + " its mapping is ambiguos.", Project.MSG_VERBOSE);
                 return;
             }
             outF = new File(destDir, outFileName[0]);
 
             if (force || inF.lastModified() > outF.lastModified()
                     || styleSheetLastModified > outF.lastModified()) {
                 ensureDirectoryFor(outF);
                 log("Processing " + inF + " to " + outF);
                 configureLiaison(stylesheet);
                 setLiaisonDynamicFileParameters(liaison, inF);
                 liaison.transform(inF, outF);
             }
         } catch (Exception ex) {
             // If failed to process document, must delete target document,
             // or it will not attempt to process it the second time
             log("Failed to process " + inFile, Project.MSG_INFO);
             if (outF != null) {
                 outF.delete();
             }
 
             throw new BuildException(ex);
         }
 
     } //-- processXML
 
     /**
      * Process the input file to the output file with the given stylesheet.
      *
      * @param inFile the input file to process.
      * @param outFile the destination file.
      * @param stylesheet the stylesheet to use.
      * @exception BuildException if the processing fails.
      */
     private void process(File inFile, File outFile, Resource stylesheet) throws BuildException {
         try {
             long styleSheetLastModified = stylesheet.getLastModified();
             log("In file " + inFile + " time: " + inFile.lastModified(), Project.MSG_DEBUG);
             log("Out file " + outFile + " time: " + outFile.lastModified(), Project.MSG_DEBUG);
             log("Style file " + xslFile + " time: " + styleSheetLastModified, Project.MSG_DEBUG);
             if (force || inFile.lastModified() >= outFile.lastModified()
                     || styleSheetLastModified >= outFile.lastModified()) {
                 ensureDirectoryFor(outFile);
                 log("Processing " + inFile + " to " + outFile, Project.MSG_INFO);
                 configureLiaison(stylesheet);
                 setLiaisonDynamicFileParameters(liaison, inFile);
                 liaison.transform(inFile, outFile);
             } else {
                 log("Skipping input file " + inFile + " because it is older than output file "
                         + outFile + " and so is the stylesheet " + stylesheet, Project.MSG_DEBUG);
             }
         } catch (Exception ex) {
             log("Failed to process " + inFile, Project.MSG_INFO);
             if (outFile != null) {
                 outFile.delete();
             }
             throw new BuildException(ex);
         }
     }
 
     /**
      * Ensure the directory exists for a given file
      *
      * @param targetFile the file for which the directories are required.
      * @exception BuildException if the directories cannot be created.
      */
     private void ensureDirectoryFor(File targetFile) throws BuildException {
         File directory = targetFile.getParentFile();
         if (!directory.exists()) {
             if (!directory.mkdirs()) {
                 throw new BuildException("Unable to create directory: "
                         + directory.getAbsolutePath());
             }
         }
     }
 
     /**
      * Get the factory instance configured for this processor
      *
      * @return the factory instance in use
      */
     public Factory getFactory() {
         return factory;
     }
 
     /**
      * Get the XML catalog containing entity definitions
      *
      * @return the XML catalog for the task.
      */
     public XMLCatalog getXMLCatalog() {
         xmlCatalog.setProject(getProject());
         return xmlCatalog;
     }
 
     /**
      * Get an enumeration on the outputproperties.
      * @return the outputproperties
      */
     public Enumeration getOutputProperties() {
         return outputProperties.elements();
     }
 
     /**
      * Get the Liason implementation to use in processing.
      *
      * @return an instance of the XSLTLiason interface.
      */
     protected XSLTLiaison getLiaison() {
         // if processor wasn't specified, see if TraX is available.  If not,
         // default it to xalan, depending on which is in the classpath
         if (liaison == null) {
             if (processor != null) {
                 try {
                     resolveProcessor(processor);
                 } catch (Exception e) {
                     throw new BuildException(e);
                 }
             } else {
                 try {
                     resolveProcessor(PROCESSOR_TRAX);
                 } catch (Throwable e1) {
                     e1.printStackTrace();
                     throw new BuildException(e1);
                 }
             }
         }
         return liaison;
     }
 
     /**
      * Create an instance of an XSL parameter for configuration by Ant.
      *
      * @return an instance of the Param class to be configured.
      */
     public Param createParam() {
         Param p = new Param();
         params.addElement(p);
         return p;
     }
 
     /**
      * The Param inner class used to store XSL parameters
      */
     public static class Param {
         /** The parameter name */
         private String name = null;
 
         /** The parameter's value */
         private String expression = null;
 
         private String ifProperty;
         private String unlessProperty;
         private Project project;
 
         /**
          * Set the current project
          *
          * @param project the current project
          */
         public void setProject(Project project) {
             this.project = project;
         }
 
         /**
          * Set the parameter name.
          *
          * @param name the name of the parameter.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * The parameter value
          * NOTE : was intended to be an XSL expression.
          * @param expression the parameter's value.
          */
         public void setExpression(String expression) {
             this.expression = expression;
         }
 
         /**
          * Get the parameter name
          *
          * @return the parameter name
          * @exception BuildException if the name is not set.
          */
         public String getName() throws BuildException {
             if (name == null) {
                 throw new BuildException("Name attribute is missing.");
             }
             return name;
         }
 
         /**
          * Get the parameter's value
          *
          * @return the parameter value
          * @exception BuildException if the value is not set.
          */
         public String getExpression() throws BuildException {
             if (expression == null) {
                 throw new BuildException("Expression attribute is missing.");
             }
             return expression;
         }
 
         /**
          * Set whether this param should be used.  It will be
          * used if the property has been set, otherwise it won't.
          * @param ifProperty name of property
          */
         public void setIf(String ifProperty) {
             this.ifProperty = ifProperty;
         }
 
         /**
          * Set whether this param should NOT be used. It
          * will not be used if the property has been set, otherwise it
          * will be used.
          * @param unlessProperty name of property
          */
         public void setUnless(String unlessProperty) {
             this.unlessProperty = unlessProperty;
         }
 
         /**
          * Ensures that the param passes the conditions placed
          * on it with <code>if</code> and <code>unless</code> properties.
          * @return true if the task passes the "if" and "unless" parameters
          */
         public boolean shouldUse() {
             if (ifProperty != null && project.getProperty(ifProperty) == null) {
                 return false;
             }
             if (unlessProperty != null && project.getProperty(unlessProperty) != null) {
                 return false;
             }
             return true;
         }
     } // Param
 
     /**
      * Create an instance of an output property to be configured.
      * @return the newly created output property.
      * @since Ant 1.5
      */
     public OutputProperty createOutputProperty() {
         OutputProperty p = new OutputProperty();
         outputProperties.addElement(p);
         return p;
     }
 
     /**
      * Specify how the result tree should be output as specified
      * in the <a href="http://www.w3.org/TR/xslt#output">
      * specification</a>.
      * @since Ant 1.5
      */
     public static class OutputProperty {
         /** output property name */
         private String name;
 
         /** output property value */
         private String value;
 
         /**
          * @return the output property name.
          */
         public String getName() {
             return name;
         }
 
         /**
          * set the name for this property
          * @param name A non-null String that specifies an
          * output property name, which may be namespace qualified.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * @return the output property value.
          */
         public String getValue() {
             return value;
         }
 
         /**
          * set the value for this property
          * @param value The non-null string value of the output property.
          */
         public void setValue(String value) {
             this.value = value;
         }
     }
 
     /**
      * Initialize internal instance of XMLCatalog
      * @throws BuildException on error
      */
     public void init() throws BuildException {
         super.init();
         xmlCatalog.setProject(getProject());
     }
 
     /**
      * Loads the stylesheet and set xsl:param parameters.
      *
      * @param stylesheet the file from which to load the stylesheet.
      * @exception BuildException if the stylesheet cannot be loaded.
      * @deprecated since Ant 1.7
      */
     protected void configureLiaison(File stylesheet) throws BuildException {
         FileResource fr = new FileResource();
         fr.setProject(getProject());
         fr.setFile(stylesheet);
         configureLiaison(fr);
     }
 
     /**
      * Loads the stylesheet and set xsl:param parameters.
      *
      * @param stylesheet the resource from which to load the stylesheet.
      * @exception BuildException if the stylesheet cannot be loaded.
      * @since Ant 1.7
      */
     protected void configureLiaison(Resource stylesheet) throws BuildException {
         if (stylesheetLoaded && reuseLoadedStylesheet) {
             return;
         }
         stylesheetLoaded = true;
 
         try {
             log("Loading stylesheet " + stylesheet, Project.MSG_INFO);
             // We call liason.configure() and then liaison.setStylesheet()
             // so that the internal variables of liaison can be set up
             if (liaison instanceof XSLTLiaison2) {
                 ((XSLTLiaison2) liaison).configure(this);
             }
             if (liaison instanceof XSLTLiaison3) {
                 // If we are here we can set the stylesheet as a
                 // resource
                 ((XSLTLiaison3) liaison).setStylesheet(stylesheet);
             } else {
                 // If we are here we cannot set the stylesheet as
                 // a resource, but we can set it as a file. So,
                 // we make an attempt to get it as a file
                 FileProvider fp =
                     (FileProvider) stylesheet.as(FileProvider.class);
                 if (fp != null) {
                     liaison.setStylesheet(fp.getFile());
                 } else {
                     throw new BuildException(liaison.getClass().toString()
                             + " accepts the stylesheet only as a file", getLocation());
                 }
             }
             for (Enumeration e = params.elements(); e.hasMoreElements();) {
                 Param p = (Param) e.nextElement();
                 if (p.shouldUse()) {
                     liaison.addParam(p.getName(), p.getExpression());
                 }
             }
         } catch (Exception ex) {
             log("Failed to transform using stylesheet " + stylesheet, Project.MSG_INFO);
             throw new BuildException(ex);
         }
     }
 
     /**
      * Sets file parameter(s) for directory and filename if the attribute
      * 'filenameparameter' or 'filedirparameter' are set in the task.
      *
      * @param  liaison    to change parameters for
      * @param  inFile     to get the additional file information from
      * @throws Exception  if an exception occurs on filename lookup
      *
      * @since Ant 1.7
      */
     private void setLiaisonDynamicFileParameters(
         XSLTLiaison liaison, File inFile) throws Exception {
         if (fileNameParameter != null) {
             liaison.addParam(fileNameParameter, inFile.getName());
         }
         if (fileDirParameter != null) {
             String fileName = FileUtils.getRelativePath(baseDir, inFile);
             File file = new File(fileName);
             // Give always a slash as file separator, so the stylesheet could be sure about that
             // Use '.' so a dir+"/"+name would not result in an absolute path
             liaison.addParam(fileDirParameter, file.getParent() != null ? file.getParent().replace(
                     '\\', '/') : ".");
         }
     }
 
     /**
      * Create the factory element to configure a trax liaison.
      * @return the newly created factory element.
      * @throws BuildException if the element is created more than one time.
      */
     public Factory createFactory() throws BuildException {
         if (factory != null) {
             throw new BuildException("'factory' element must be unique");
         }
         factory = new Factory();
         return factory;
     }
 
     /**
      * The factory element to configure a transformer factory
      * @since Ant 1.6
      */
     public static class Factory {
 
         /** the factory class name to use for TraXLiaison */
         private String name;
 
         /**
          * the list of factory attributes to use for TraXLiaison
          */
         private Vector attributes = new Vector();
 
         /**
          * @return the name of the factory.
          */
         public String getName() {
             return name;
         }
 
         /**
          * Set the name of the factory
          * @param name the name of the factory.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * Create an instance of a factory attribute.
          * @param attr the newly created factory attribute
          */
         public void addAttribute(Attribute attr) {
             attributes.addElement(attr);
         }
 
         /**
          * return the attribute elements.
          * @return the enumeration of attributes
          */
         public Enumeration getAttributes() {
             return attributes.elements();
         }
 
         /**
          * A JAXP factory attribute. This is mostly processor specific, for
          * example for Xalan 2.3+, the following attributes could be set:
          * <ul>
          *  <li>http://xml.apache.org/xalan/features/optimize (true|false) </li>
          *  <li>http://xml.apache.org/xalan/features/incremental (true|false) </li>
          * </ul>
          */
         public static class Attribute implements DynamicConfigurator {
 
             /** attribute name, mostly processor specific */
             private String name;
 
             /** attribute value, often a boolean string */
             private Object value;
 
             /**
              * @return the attribute name.
              */
             public String getName() {
                 return name;
             }
 
             /**
              * @return the output property value.
              */
             public Object getValue() {
                 return value;
             }
 
             /**
              * Not used.
              * @param name not used
              * @return null
              * @throws BuildException never
              */
             public Object createDynamicElement(String name) throws BuildException {
                 return null;
             }
 
             /**
              * Set an attribute.
              * Only "name" and "value" are supported as names.
              * @param name the name of the attribute
              * @param value the value of the attribute
              * @throws BuildException on error
              */
             public void setDynamicAttribute(String name, String value) throws BuildException {
                 // only 'name' and 'value' exist.
                 if ("name".equalsIgnoreCase(name)) {
                     this.name = value;
                 } else if ("value".equalsIgnoreCase(name)) {
                     // a value must be of a given type
                     // say boolean|integer|string that are mostly used.
                     if ("true".equalsIgnoreCase(value)) {
                         this.value = Boolean.TRUE;
                     } else if ("false".equalsIgnoreCase(value)) {
                         this.value = Boolean.FALSE;
                     } else {
                         try {
                             this.value = new Integer(value);
                         } catch (NumberFormatException e) {
                             this.value = value;
                         }
                     }
                 } else {
                     throw new BuildException("Unsupported attribute: " + name);
                 }
             }
         } // -- class Attribute
     } // -- class Factory
 
     /**
      * Mapper implementation of the "traditional" way &lt;xslt&gt;
      * mapped filenames.
      *
      * <p>If the file has an extension, chop it off.  Append whatever
      * the user has specified as extension or ".html".</p>
      *
      * @since Ant 1.6.2
      */
     private class StyleMapper implements FileNameMapper {
         public void setFrom(String from) {
         }
         public void setTo(String to) {
         }
         public String[] mapFileName(String xmlFile) {
             int dotPos = xmlFile.lastIndexOf('.');
             if (dotPos > 0) {
                 xmlFile = xmlFile.substring(0, dotPos);
             }
             return new String[] {xmlFile + targetExtension};
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
index 9ee7d0907..b0ef4cd03 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
@@ -1,592 +1,599 @@
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
 
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.Hashtable;
 import java.util.Vector;
 import java.util.Enumeration;
 import java.net.URL;
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
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.XSLTLiaison3;
 import org.apache.tools.ant.taskdefs.XSLTLogger;
 import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
 import org.apache.tools.ant.taskdefs.XSLTProcess;
 import org.apache.tools.ant.types.XMLCatalog;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.URLResource;
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
 public class TraXLiaison implements XSLTLiaison3, ErrorListener, XSLTLoggerAware {
 
     /**
      * Helper for transforming filenames to URIs.
      *
      * @since Ant 1.7
      */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * The current <code>Project</code>
      */
     private Project project;
 
     /**
      * the name of the factory implementation class to use
      * or null for default JAXP lookup.
      */
     private String factoryName = null;
 
     /** The trax TransformerFactory */
     private TransformerFactory tfactory = null;
 
     /** stylesheet to use for transformation */
     private Resource stylesheet;
 
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
     private Hashtable params = new Hashtable();
 
     /** factory attributes */
     private Vector attributes = new Vector();
 
+    /** whether to suppress warnings */
+    private boolean suppressWarnings = false;
+
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
         FileResource fr = new FileResource();
         fr.setProject(project);
         fr.setFile(stylesheet);
         setStylesheet(fr);
     }
 
     /**
      * Set the stylesheet file.
      * @param stylesheet a {@link org.apache.tools.ant.types.Resource} value
      * @throws Exception on error
      */
     public void setStylesheet(Resource stylesheet) throws Exception {
         if (this.stylesheet != null) {
             // resetting the stylesheet - reset transformer
             transformer = null;
 
             // do we need to reset templates as well
             if (!this.stylesheet.equals(stylesheet)
                 || (stylesheet.getLastModified() != templatesModTime)) {
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
 
             // set parameters on each transformation, maybe something has changed
             //(e.g. value of file name parameter)
             setTransformationParameters();
 
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
 
     private Source getSource(InputStream is, Resource resource)
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
         // The line below is a hack: the system id must an URI, but it is not
         // cleat to get the URI of an resource, so just set the name of the
         // resource as a system id
         src.setSystemId(resourceToURI(resource));
         return src;
     }
 
     private String resourceToURI(Resource resource) {
         // TODO turn URLResource into Provider
         FileProvider fp = (FileProvider) resource.as(FileProvider.class);
         if (fp != null) {
             return FILE_UTILS.toURI(fp.getFile().getAbsolutePath());
         }
         if (resource instanceof URLResource) {
             URL u = ((URLResource) resource).getURL();
             return String.valueOf(u);
         } else {
             return resource.getName();
         }
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
                 = new BufferedInputStream(stylesheet.getInputStream());
             templatesModTime = stylesheet.getLastModified();
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
         for (int i = 0; i < outputProperties.size(); i++) {
             final String[] pair = (String[]) outputProperties.elementAt(i);
             transformer.setOutputProperty(pair[0], pair[1]);
         }
     }
 
     /**
      * Sets the paramters for the transformer.
      */
     private void setTransformationParameters() {
         for (final Enumeration enumeration = params.keys();
              enumeration.hasMoreElements();) {
             final String name = (String) enumeration.nextElement();
             final String value = (String) params.get(name);
             transformer.setParameter(name, value);
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
                 Class clazz = null;
                 try {
                     clazz =
                         Class.forName(factoryName, true,
                                       Thread.currentThread()
                                       .getContextClassLoader());
                 } catch (ClassNotFoundException cnfe) {
                     String msg = "Failed to load " + factoryName
                         + " via the configured classpath, will try"
                         + " Ant's classpath instead.";
                     if (logger != null) {
                         logger.log(msg);
                     } else if (project != null) {
                         project.log(msg, Project.MSG_WARN);
                     } else {
                         System.err.println(msg);
                     }
                 }
 
                 if (clazz == null) {
                     clazz = Class.forName(factoryName);
                 }
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
         params.put(name, value);
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
-        logError(e, "Warning");
+        if (!suppressWarnings) {
+            logError(e, "Warning");
+        }
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
                 msg.append(":");
                 msg.append(line);
                 int column = locator.getColumnNumber();
                 if (column != -1) {
                     msg.append(":");
                     msg.append(column);
                 }
             }
         }
         msg.append(": ");
         msg.append(type);
         msg.append("! ");
         msg.append(e.getMessage());
         if (e.getCause() != null) {
             msg.append(" Cause: ");
             msg.append(e.getCause());
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
         project = xsltTask.getProject();
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
+
+        suppressWarnings = xsltTask.getSuppressWarnings();
     }
 }
