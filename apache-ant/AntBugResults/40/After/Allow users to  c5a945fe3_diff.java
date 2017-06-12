diff --git a/WHATSNEW b/WHATSNEW
index 332910beb..63ba8bc67 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1786 +1,1791 @@
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
 
  * <checksum>'s totalproperty was platform dependent because it relied
    on java.io.File#compareTo.  It has now been made platform
    independent, which means that totalPropery values obtained on
    Windows (and other systems where the sort order of File is not case
    sensitive) can be different from the values obtained with earlier
    versions of Ant.
    Bugzilla Report 36748.
 
  * globmapper didn't work properly if the "to" pattern didn't contain
    a "*".  In particular it implicitly added a * to the end of the
    pattern.  This is no longer the case.  If you relied on this
    behavior you will now need to explicitly specify the trailing *.
    Bugzilla Report 46506.
 
  * <copy> silently ignored missing resources even with
    failOnError="true".  If your build tries to copy non-existant
    resources and you relied on this behavior you must now explicitly
    set failOnError to false.
    Bugzilla Report 47362.
 
 Fixed bugs:
 -----------
 
  * Better handling of package-info.class. Bugzilla Report 43114.
 
  * RPM task needed an inserted space between the define and the value.
    bugzilla Report 46659.
 
  * Got rid of deadlock between in in, out and err in the Redirector. 
    Bugzilla Report 44544.
 
  * Caused by AssertionError no longer filtered. Bugzilla report 45631.
  
  * <zip> would sometimes recreate JARs unnecessarily. Bugzilla report 45902.
 
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
 
  * <dependset> complained about files being modified in the future if
    they had been just very recently (within Ant's assumed granularity
    of the file system).
    Bugzilla Report 43665.
 
  * <sshexec> didn't store the ouput in outputproperty if the remote
    command failed.
    Bugzilla Report 46340.
 
  * DirectoryScanner's slow-scanning algorithm that is used when you
    ask for excluded or not-included files and/or directories could
    miss some files and directories in the presence of recursive
    exclude patterns.
 
  * <sort> resource collection kept only one of entries deemed equal by
    the chosen Comparator.  Bugzilla report 46527.
 
  * the ZipFile class used by <unzip> and others could leave the
    archive open (making it undeletable on Windows as long as the java
    VM was running) for files with an unexpected internal structure.
    Bugzilla Report 46559.
 
  * The zip package now supports the extra fields invented by InfoZIP
    in order to store Unicode file names and comments.
 
  * The zip package detects the encoding bit set by more modern
    archivers when they write UTF-8 filenames and optionally sets it
    when writing zips or jars.
    Bugzilla Report 45548
 
  * <sync> could run into a NullPointerException when faced with broken
    symbolic links.
    Bugzilla Report 46747.
 
  * The ant shell script should now support MSYS/MinGW as well.
    Bugzilla Report 46936.
 
  * <signjar> has a new force attribute that allows re-signing of jars
    that are already signed.
    Bugzilla Report 46891.
 
  * <sshexec> now again honors failonerror in the face of connection
    errors.
    Bugzilla Report 46829.
 
  * The <replacetokens> filter threw an exception if the stream to
    filter ended with a begin token.
    Bugzilla Report 47306.
 
  * <scriptmapper>, <scriptfilter> and <scriptcondition> didn't support
    the setbeans attribute.
    Bugzilla Report 47336.
 
  * <loadproperties>' encoding attribute didn't work.
    Bugzilla Report 47382.
 
  * Ant created tar archives could contain random bytes at the end
    which confused some untar implementations.
    Bugzilla Report 47421.
 
  * various places where unchecked PrintWriters could hide exceptions
    have been revisited to now check the error status or not use a
    PrintWriter at all.
    Bugzilla Report 43537.
 
  * filesetmanifest="mergewithoutmain" in <jar> didn't treat inline
    manifests as expected.
    Bugzilla Report 29731.
 
  * <record> didn't work properly with nested builds.
    Bugzilla Report 41368. 
 
  * <jar> with filesetmanifest different from skip didn't work if the
    update attribute has been set to true.
    Bugzilla Report 30751.
 
  * The default stylesheets for <junitreport> failed to properly escape
    XML content in exception stack traces.
    Bugzilla Report 39492
 
 Other changes:
 --------------
  * The get task now also follows redirects from http to https
    Bugzilla report 47433
 
  * A HostInfo task was added performing information on hosts, including info on 
    the host ant is running on. 
    Bugzilla reports 45861 and 31164.
 
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
 
  * <xslt> now has an option to supress transformer warnings.  This
    option only has an effect for processors that support this feature;
    the "trax" processor included with Ant does support it.
    Bugzilla Report 18897.
 
  * <xslt> has two new attributes failOnError and
    failOnTransformationError that can be used to not make the build
    process proceed if an error occurs.
    Bugzilla Report 36260.
 
  * <xslt> has a new attribute failOnNoResources that can be used to 
    make the build fail/continue if the collection of resources to
    transform is empty.
    Bugzilla Report 46274.
 
  * It is now possible to define system properties that should be set
    during xslt's transformation.  This can be used to enable XInclude
    processing in Xerces, for example.
    Bugzilla Report 36653.
 
  * a new resource collection <archives> can be used to specify
    collections of ZIP and TAR archives as source and extracts them on
    the fly.  This is a generalization of the <zipgroupfileset> found
    as nested element of <zip> and friends.
    Bugzilla Report 46257.
 
  * <dependset> has a new verbose attribute that makes the task list
    all deleted targets and give a hint as to why it deleted them.
    Bugzilla Report 13681.
 
  * <replaceregexp> now supports arbitrary filesystem based resource
    collections.
    Bugzilla Report 46341.
 
  * <replace> now supports arbitrary filesystem based resource
    collections.
    Bugzilla Report 24062.
 
  * token and value of <replace>'s nested <replacefilter> can now also
    be specified as nested elements to allow multiline content more
    easily.
    Bugzilla Report 39568.
 
  * <replace> and <replaceregexp> can now optionally preserve the file
    timestamp even if the file is modified.
    Bugzilla Report 39002.
 
  * The <replace> child-elements <replacetoken> and <replacevalue> have
    a new attribute that controls whether properties in nested text get
    expanded.
    Bugzilla Report 11585.
 
  * <replace> has a new attribute failOnNoReplacements that makes the
    build fail if the task didn't do anything.
    Bugzilla Report 21064.
 
  * <sync>'s <preserveInTarget> has a new attribute that conrols
    whether empty directories should be kept.
    Bugzilla Report 43159.
 
  * ant -diagnostics now checks that it can read as much from the
    temporary directory as it has written.  This may help detecting a
    full filesystem.
    Bugzilla Report 32676.
 
  * <pathconvert> has a new preserveduplicates attribute--historically
    these were eliminated in the interest of behaving in the manner
    of a "path."
 
  * <javac>'s source and target attributes are no longer ignored when
    using gcj.
    Bugzilla Issue 46617.
 
  * ant -diagnostics now outputs information about the default XSLT
    processor.
    Bugzilla Issue 46612.
 
  * the ZIP library will now ignore ZIP extra fields that don't specify
    a size.
    Bugzilla Report 42940.
 
  * CBZip2OutputStream now has a finish method separate from close.
    Bugzilla Report 42713.
 
  * the <zip> and <unzip> family of tasks has new options to deal with
    file name and comment encoding.  Please see the zip tasks'
    documentation for details.
 
  * <input ...><handler type="secure" /></input> now uses previously undocumented
    SecureInputHandler shipped with Ant 1.7.1.
 
  * Command line arguments for <exec> and similar tasks can now have
    optional prefix and suffix attributes.
    Bugzilla Report 47365
 
  * <apply>'s srcfile and targetfile child elements can now have
    optional prefix and suffix attributes.
    Bugzilla Report 45625
 
  * <jar> has a new attribute to enable indexing of META-INF
    directories which is desabled for backwards compatibility reasons.
    Bugzilla Report 47457
 
  * <apt>'s executable attribute can be used to specify a different
    executable.
    Bugzilla Report 46230.
 
  * <rmic>'s new executable attribute can be used to specify a
    different executable.
    Bugzilla Report 42132.
 
+ * <javac>, <rmic>, <javah> and <native2ascci> now provide a nested
+   element to specify a classpath that will be used when loading the
+   task's (compiler) adapter class.
+   Bugzilla Issue 11143.
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
 
 * SecureInputHandler added to use Java 6 System.console().readPassword()
   when available.
 
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
 
diff --git a/docs/manual/CoreTasks/javac.html b/docs/manual/CoreTasks/javac.html
index 328031e3c..6c3736a1b 100644
--- a/docs/manual/CoreTasks/javac.html
+++ b/docs/manual/CoreTasks/javac.html
@@ -1,818 +1,826 @@
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
 <html lang="en-us">
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <link rel="stylesheet" type="text/css" href="../stylesheets/style.css">
 <title>Javac Task</title>
 </head>
 
 <body>
 
 <h2><a name="javac">Javac</a></h2>
 <h3>Description</h3>
 <p>Compiles a Java source tree.</p>
 <p>The source and destination directory will be recursively scanned for Java
 source files to compile. Only Java files that have no corresponding
 <code>.class</code> file
 or where the class file is older than the
 <code>.java</code> file will be compiled.</p>
 <p>Note: Ant uses only the names of the source and class files to find
 the classes that need a rebuild. It will not scan the source and therefore
 will have no knowledge about nested classes, classes that are named different
 from the source file, and so on. See the
 <a href="../OptionalTasks/depend.html"><code>&lt;depend&gt;</code></a> task
 for dependency checking based on other than just
 existence/modification times.</p>
 <p>When the source files are part of a package, the directory structure of
 the source tree should follow the package
 hierarchy.</p>
 <p>It is possible to refine the set of files that are being compiled.
 This can be done with the <code>includes</code>, <code>includesfile</code>,
 <code>excludes</code>, and <code>excludesfile</code>
 attributes. With the <code>includes</code> or
 <code>includesfile</code> attribute, you specify the files you want to
 have included.
 The <code>exclude</code> or <code>excludesfile</code> attribute is used
 to specify
 the files you want to have excluded. In both cases, the list of files
 can be specified by either the filename, relative to the directory(s) specified
 in the <code>srcdir</code> attribute or nested <code>&lt;src&gt;</code>
 element(s), or by using wildcard patterns. See the section on
 <a href="../dirtasks.html#directorybasedtasks">directory-based tasks</a>,
 for information on how the
 inclusion/exclusion of files works, and how to write wildcard patterns.</p>
 <p>It is possible to use different compilers. This can be specified by
 either setting the global <code>build.compiler</code> property, which will
 affect all <code>&lt;javac&gt;</code> tasks throughout the build, or by
 setting the <code>compiler</code> attribute, specific to the current
 <code>&lt;javac&gt;</code> task.
 <a name="compilervalues">Valid values for either the
 <code>build.compiler</code> property or the <code>compiler</code>
 attribute are:</a></p>
 <ul>
   <li><code>classic</code> (the standard compiler of JDK 1.1/1.2) &ndash;
       <code>javac1.1</code> and
       <code>javac1.2</code> can be used as aliases.</li>
   <li><code>modern</code> (the standard compiler of JDK 1.3/1.4/1.5/1.6) &ndash;
       <code>javac1.3</code> and
       <code>javac1.4</code> and
       <code>javac1.5</code> and
       <code>javac1.6</code> can be used as aliases.</li>
   <li><code>jikes</code> (the <a
     href="http://jikes.sourceforge.net/" target="_top">Jikes</a>
     compiler).</li>
   <li><code>jvc</code> (the Command-Line Compiler from Microsoft's SDK
       for Java / Visual J++) &ndash; <code>microsoft</code> can be used
       as an alias.</li>
   <li><code>kjc</code> (the <a href="http://www.dms.at/kopi/" target="_top">kopi</a>
     compiler).</li>
   <li><code>gcj</code> (the gcj compiler from gcc).</li>
   <li><code>sj</code> (Symantec java compiler) &ndash;
       <code>symantec</code> can be used as an alias.</li>
   <li><code>extJavac</code> (run either modern or classic in a JVM of
       its own).</li>
 </ul>
 <p>The default is <code>javac1.x</code> with <code>x</code> depending
 on the JDK version you use while you are running Ant.
 If you wish to use a different compiler interface than those
 supplied, you can write a class that implements the CompilerAdapter interface
 (<code>package org.apache.tools.ant.taskdefs.compilers</code>). Supply the full
 classname in the <code>build.compiler</code> property or the
 <code>compiler</code> attribute.
 </p>
 <p>The fork attribute overrides the <code>build.compiler</code> property
 or <code>compiler</code> attribute setting and
 expects a JDK1.1 or higher to be set in <code>JAVA_HOME</code>.
 </p>
 <p>You can also use the <code>compiler</code> attribute to tell Ant
 which JDK version it shall assume when it puts together the command
 line switches - even if you set <code>fork=&quot;true&quot;</code>.
 This is useful if you want to run the compiler of JDK 1.1 while you
 current JDK is 1.2+.  If you use
 <code>compiler=&quot;javac1.1&quot;</code> and (for example)
 <code>depend=&quot;true&quot;</code> Ant will use the command line
 switch <code>-depend</code> instead of <code>-Xdepend</code>.</p>
 <p>This task will drop all entries that point to non-existent
 files/directories from the classpath it passes to the compiler.</p>
 <p>The working directory for a forked executable (if any) is the
   project's base directory.</p>
 <p><strong>Windows Note:</strong>When the modern compiler is used
 in unforked mode on Windows, it locks up the files present in the
 classpath of the <code>&lt;javac&gt;</code> task, and does not release them.
 The side effect of this is that you will not be able to delete or move
 those files later on in the build.  The workaround is to fork when
 invoking the compiler.</p>
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">srcdir</td>
     <td valign="top">Location of the java files. (See the
      <a href="#srcdirnote">note</a> below.)</td>
     <td align="center" valign="top">Yes, unless nested <code>&lt;src&gt;</code> elements are present.</td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">Location to store the class files.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includes</td>
     <td valign="top">Comma- or space-separated list of files (may be specified using
       wildcard patterns) that must be
       included; all <code>.java</code> files are included when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">includesfile</td>
     <td valign="top">The name of a file that contains a list of files to
       include (may be specified using wildcard patterns).</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludes</td>
     <td valign="top">Comma- or space-separated list of files (may be specified using
       wildcard patterns) that must be excluded; no files (except default
       excludes) are excluded when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludesfile</td>
     <td valign="top">The name of a file that contains a list of files to
       exclude (may be specified using wildcard patterns).</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to use.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">sourcepath</td>
     <td valign="top">The sourcepath to use; defaults to the value of the srcdir attribute (or nested <code>&lt;src&gt;</code> elements).
         To suppress the sourcepath switch, use <code>sourcepath=&quot;&quot;</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">bootclasspath</td>
     <td valign="top">
       Location of bootstrap class files. (See <a href="#bootstrap">below</a>
       for using the -X and -J-X parameters for specifing
       the bootstrap classpath).
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">classpathref</td>
     <td valign="top">The classpath to use, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">sourcepathref</td>
     <td valign="top">The sourcepath to use, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">bootclasspathref</td>
     <td valign="top">Location of bootstrap class files, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">extdirs</td>
     <td valign="top">Location of installed extensions.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">encoding</td>
     <td valign="top">Encoding of source files. (Note: gcj doesn't support
       this option yet.)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">nowarn</td>
     <td valign="top">Indicates whether the <code>-nowarn</code> switch
       should be passed to the compiler; defaults to <code>off</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">debug</td>
     <td valign="top">Indicates whether source should be compiled with
     debug information; defaults to <code>off</code>.  If set to
     <code>off</code>, <code>-g:none</code> will be passed on the
     command line for compilers that support it (for other compilers, no
     command line argument will be used).  If set to <code>true</code>,
     the value of the <code>debuglevel</code> attribute determines the
     command line argument.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">debuglevel</td>
     <td valign="top">Keyword list to be appended to the <code>-g</code>
       command-line switch.  This will be ignored by all implementations except
       <code>modern</code>, <code>classic(ver &gt;= 1.2)</code> and <code>jikes</code>.
       Legal values are <code>none</code> or a comma-separated list of the
       following keywords:
       <code>lines</code>, <code>vars</code>, and <code>source</code>.
       If <code>debuglevel</code> is not specified, by default,
       nothing will be
       appended to <code>-g</code>.  If <code>debug</code> is not turned on,
       this attribute will be ignored.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">optimize</td>
     <td valign="top">
       Indicates whether source should be compiled with
       optimization; defaults to <code>off</code>. <strong>Note</strong>
       that this flag is just ignored by Sun's <code>javac</code> starting
       with JDK 1.3 (since compile-time optimization is unnecessary).
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">deprecation</td>
     <td valign="top">Indicates whether source should be compiled with
       deprecation information; defaults to <code>off</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">target</td>
     <td valign="top">Generate class files for specific VM version
     (e.g., <code>1.1</code> or <code>1.2</code>). <b>Note that the
     default value depends on the JVM that is running Ant.  In
     particular, if you use JDK 1.4+ the generated classes will not be
     usable for a 1.1 Java VM unless you explicitly set this attribute
     to the value 1.1 (which is the default value for JDK 1.1 to
     1.3).  We highly recommend to always specify this
     attribute.</b><br>
     A default value for this attribute can be provided using the magic
     <a
     href="../javacprops.html#target"><code>ant.build.javac.target</code></a>
     property.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">verbose</td>
     <td valign="top">Asks the compiler for verbose output; defaults to
       <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">depend</td> <td valign="top">Enables dependency-tracking
       for compilers that support this (<code>jikes</code> and
       <code>classic</code>).</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeAntRuntime</td>
     <td valign="top">Whether to include the Ant run-time libraries in the
       classpath; defaults to <code>yes</code>, unless
       <a href="../sysclasspath.html"><code>build.sysclasspath</code></a> is set.
       <em>It is usually best to set this to false</em> so the script's behavior is not
       sensitive to the environment in which it is run.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeJavaRuntime</td>
     <td valign="top">Whether to include the default run-time
       libraries from the executing VM in the classpath;
       defaults to <code>no</code>.<br/>
       <b>Note:</b> In some setups the run-time libraries may be part
       of the "Ant run-time libraries" so you may need to explicitly
       set includeAntRuntime to false to ensure that the Java
       run-time libraries are not included.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">fork</td>
     <td valign="top">Whether to execute <code>javac</code> using the
       JDK compiler externally; defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">executable</td>
     <td valign="top">Complete path to the <code>javac</code>
       executable to use in case of <code>fork=&quot;yes&quot;</code>.
       Defaults to the compiler of the Java version that is currently
       running Ant.  Ignored if <code>fork=&quot;no&quot;</code>.<br>
       Since Ant 1.6 this attribute can also be used to specify the
       path to the executable when using jikes, jvc, gcj or sj.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">memoryInitialSize</td>
     <td valign="top">The initial size of the memory for the underlying VM,
       if <code>javac</code> is run externally; ignored otherwise. Defaults
       to the standard VM memory setting.
       (Examples: <code>83886080</code>, <code>81920k</code>, or
       <code>80m</code>)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">memoryMaximumSize</td>
     <td valign="top">The maximum size of the memory for the underlying VM,
       if <code>javac</code> is run externally; ignored otherwise. Defaults
       to the standard VM memory setting.
       (Examples: <code>83886080</code>, <code>81920k</code>, or
       <code>80m</code>)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">failonerror</td>
     <td valign="top">Indicates whether compilation errors
         will fail the build; defaults to <code>true</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">errorProperty</td>
     <td valign="top">
       The property to set (to the value "true") if compilation fails.
       <em>Since Ant 1.7.1</em>.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">source</td>
 
     <td valign="top">Value of the <code>-source</code> command-line
     switch; will be ignored by all implementations prior to
     <code>javac1.4</code> (or <code>modern</code> when Ant is not
     running in a 1.3 VM), <code>gcj</code> and <code>jikes</code>.<br>
     If you use this attribute together with <code>gcj</code>
     or <code>jikes</code>, you must make sure that your version
     supports the <code>-source</code> (or <code>-fsource</code> for
     gcj)
     switch.  By default, no <code>-source</code> argument will be used
     at all.<br>
     <b>Note that the default value depends on the JVM that is running
     Ant.  We highly recommend to always specify this
     attribute.</b><br>
     A default value for this attribute can be provided using the magic
     <a
     href="../javacprops.html#source"><code>ant.build.javac.source</code></a>
     property.</td>
 
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td valign="top">The compiler implementation to use.
       If this attribute is not set, the value of the
       <code>build.compiler</code> property, if set, will be used.
       Otherwise, the default compiler for the current VM will be used.
       (See the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">listfiles</td>
     <td valign="top">Indicates whether the source files to be compiled will
       be listed; defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">tempdir</td>
     <td valign="top">Where Ant should place temporary files.
       This is only used if the task is forked and the
       command line args length exceeds 4k.
       <em>Since Ant 1.6</em>.</td>
     <td align="center" valign="top">
       No; default is <i>java.io.tmpdir</i>.
     </td>
   </tr>
   <tr>
     <td valign="top">updatedProperty</td>
     <td valign="top">
       The property to set (to the value "true") 
       if compilation has taken place
       and has been successful.
       <em>Since Ant 1.7.1</em>.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeDestClasses</td>
     <td valign="top">
       This attribute controls whether to include the
       destination classes directory in the classpath
       given to the compiler.
       The default value of this is "true" and this
       means that previously compiled classes are on
       the classpath for the compiler. This means that "greedy" compilers
       will not recompile dependant classes that are already compiled.
       In general this is a good thing as it stops the compiler
       for doing unnecessary work. However, for some edge cases,
       involving generics, the javac compiler
       needs to compile the dependant classes to get the generics
       information. One example is documented in the bug report:
       <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=40776">
         Bug 40776 - a problem compiling a Java 5 project with generics</a>.
       Setting the attribute to "false" will cause the compiler
       to recompile dependent classes.
       <em>Since Ant 1.7.1</em>.
     </td>
     <td align="center" valign="top">No - default is "true"</td>
   </tr>
 </table>
 
 <h3>Parameters specified as nested elements</h3>
 <p>This task forms an implicit <a href="../CoreTypes/fileset.html">FileSet</a> and
 supports most attributes of <code>&lt;fileset&gt;</code>
 (<code>dir</code> becomes <code>srcdir</code>) as well as the nested
 <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code> and
 <code>&lt;patternset&gt;</code> elements.</p>
 <h4><code>src</code>, <code>classpath</code>, <code>sourcepath</code>,
 <code>bootclasspath</code> and <code>extdirs</code></h4>
 <p><code>&lt;javac&gt;</code>'s <code>srcdir</code>, <code>classpath</code>,
 <code>sourcepath</code>, <code>bootclasspath</code>, and
 <code>extdirs</code> attributes are
 <a href="../using.html#path">path-like structures</a>
 and can also be set via nested
 <code>&lt;src&gt;</code>,
 <code>&lt;classpath&gt;</code>,
 <code>&lt;sourcepath&gt;</code>,
 <code>&lt;bootclasspath&gt;</code> and
 <code>&lt;extdirs&gt;</code> elements, respectively.</p>
 
 <h4>compilerarg</h4>
 
 <p>You can specify additional command line arguments for the compiler
 with nested <code>&lt;compilerarg&gt;</code> elements.  These elements
 are specified like <a href="../using.html#arg">Command-line
 Arguments</a> but have an additional attribute that can be used to
 enable arguments only if a given compiler implementation will be
 used.</p>
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
   <tr>
     <td valign="top">value</td>
     <td align="center" rowspan="4">See
     <a href="../using.html#arg">Command-line Arguments</a>.</td>
     <td align="center" rowspan="4">Exactly one of these.</td>
   </tr>
   <tr>
     <td valign="top">line</td>
   </tr>
   <tr>
     <td valign="top">file</td>
   </tr>
   <tr>
     <td valign="top">path</td>
   </tr>
   <tr>
     <td valign="top">prefix</td>
     <td align="center" rowspan="2">See
     <a href="../using.html#arg">Command-line Arguments</a>.
     <em>Since Ant 1.8.</em></td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td>Only pass the specified argument if the chosen
       compiler implementation matches the value of this attribute.
       Legal values are the
       same as those in the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center">No</td>
   </tr>
 </table>
 
+<h4>compilerclasspath <em>since Ant 1.8.0</em></h4>
+
+<p>A <a href="../using.html#path">PATH like structure</a> holding the
+  classpath to use when loading the compiler implementation if a
+  custom class has been specified.  Doesn't have any effect when
+  using one of the built-in compilers.</p>
+
+
 <h3>Examples</h3>
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
          source=&quot;1.4&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores
 the <code>.class</code> files in the <code>${build}</code> directory.
 The classpath used includes <code>xyz.jar</code>, and compiling with
 debug information is on. The source level is 1.4,
 so you can use <code>assert</code> statements.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;true&quot;
          source=&quot;1.2&quot;
          target=&quot;1.2&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores the <code>.class</code> files in the
 <code>${build}</code> directory.  This will fork off the javac
 compiler using the default <code>javac</code> executable.
 The source level is 1.2 (similar to 1.1 or 1.3) and
 the class files should be runnable under JDK 1.2+ as well.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;java$$javac.exe&quot;
          source=&quot;1.5&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores the <code>.class</code> files in the
 <code>${build}</code> directory.  This will fork off the javac
 compiler, using the executable named <code>java$javac.exe</code>.  Note
 that the <code>$</code> sign needs to be escaped by a second one.
 The source level is 1.5, so you can use generics.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          includes=&quot;mypackage/p1/**,mypackage/p2/**&quot;
          excludes=&quot;mypackage/p1/testpackage/**&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
   /&gt;</pre>
 <p>compiles <code>.java</code> files under the <code>${src}</code>
 directory, and stores the
 <code>.class</code> files in the <code>${build}</code> directory.
 The classpath used includes <code>xyz.jar</code>, and debug information is on.
 Only files under <code>mypackage/p1</code> and <code>mypackage/p2</code> are
 used. All files in and below the <code>mypackage/p1/testpackage</code>
 directory are excluded from compilation.
 You didn't specify a source or target level,
 so the actual values used will depend on which JDK you ran Ant with.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}:${src2}&quot;
          destdir=&quot;${build}&quot;
          includes=&quot;mypackage/p1/**,mypackage/p2/**&quot;
          excludes=&quot;mypackage/p1/testpackage/**&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
   /&gt;</pre>
 
 <p>is the same as the previous example, with the addition of a second
 source path, defined by
 the property <code>src2</code>. This can also be represented using nested
 <code>&lt;src&gt;</code> elements as follows:</p>
 
 <pre>  &lt;javac destdir=&quot;${build}&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;&gt;
     &lt;src path=&quot;${src}&quot;/&gt;
     &lt;src path=&quot;${src2}&quot;/&gt;
     &lt;include name=&quot;mypackage/p1/**&quot;/&gt;
     &lt;include name=&quot;mypackage/p2/**&quot;/&gt;
     &lt;exclude name=&quot;mypackage/p1/testpackage/**&quot;/&gt;
   &lt;/javac&gt;</pre>
 
 <p>If you want to run the javac compiler of a different JDK, you
 should tell Ant, where to find the compiler and which version of JDK
 you will be using so it can choose the correct command line switches.
 The following example executes a JDK 1.1 javac in a new process and
 uses the correct command line switches even when Ant is running in a
 Java VM of a different version:</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;yes&quot;
          executable=&quot;/opt/java/jdk1.1/bin/javac&quot;
          compiler=&quot;javac1.1&quot;
   /&gt;</pre>
 
 <p><a name="srcdirnote"><b>Note:</b></a>
 If you wish to compile only source files located in certain packages below a
 common root, use the <code>include</code>/<code>exclude</code> attributes
 or <code>&lt;include&gt;</code>/<code>&lt;exclude&gt;</code> nested elements
 to filter for these packages. Do not include part of your package structure
 in the <code>srcdir</code> attribute
 (or nested <code>&lt;src&gt;</code> elements), or Ant will recompile your
 source files every time you run your compile target. See the
 <a href="http://ant.apache.org/faq.html#always-recompiles">Ant FAQ</a>
 for additional information.</p>
 
 <p>
 If you wish to compile only files explicitly specified and disable
 javac's default searching mechanism then you can unset the sourcepath
 attribute:
 <pre>  &lt;javac sourcepath=&quot;&quot; srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot; &gt;
     &lt;include name="**/*.java"/&gt;
     &lt;exclude name="**/Example.java"/&gt;
   &lt;/javac&gt;</pre>
 That way the javac will compile all java source files under &quot;${src}&quot;
 directory but skip the examples. The compiler will even produce errors if some of
 the non-example files refers to them.
 </p>
 
 <p>
 If you wish to compile with a special JDK (another than the one Ant is currently using),
 set the <code>executable</code> and <code>fork</code> attribute. Using <code>taskname</code>
 could show in the log, that these settings are fix.
 <pre>  &lt;javac srcdir=&quot;&quot; 
          destdir=&quot;&quot;
          executable=&quot;path-to-java14-home/bin/javac&quot; 
          fork=&quot;true&quot;
          taskname=&quot;javac1.4&quot; /&gt;</pre>
 </p>
 
 
 <p><b>Note:</b> If you are using Ant on Windows and a new DOS window pops up
 for every use of an external compiler, this may be a problem of the JDK you are
 using.  This problem may occur with all JDKs &lt; 1.2.</p>
 
 
 <p>
 If you want to activate other compiler options like <i>lint</i> you could use
 the <tt>&lt;compilerarg&gt;</tt> element:
 <pre>  &lt;javac srcdir="${src.dir}"
          destdir="${classes.dir}"
          classpathref="libraries"&gt;
     &lt;compilerarg value="-Xlint"/&gt;
   &lt;/javac&gt; </pre>
 </p>  
 
 
 <h3>Jikes Notes</h3>
 
 <p>You need Jikes 1.15 or later.</p>
 
 <p>Jikes supports some extra options, which can be set be defining
 the properties shown below prior to invoking the task. The setting
 for each property will be in affect for all <code>&lt;javac&gt;</code>
 tasks throughout the build.
 The Ant developers are aware that
 this is ugly and inflexible &ndash; expect a better solution in the future.
 All the options are boolean, and must be set to <code>true</code> or
 <code>yes</code> to be
 interpreted as anything other than false. By default,
 <code>build.compiler.warnings</code> is <code>true</code>,
 while all others are <code>false</code>.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Property</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Default</b></td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.emacs
 	</td>
 	<td valign="top">
 	Enable emacs-compatible error messages.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.fulldepend
 	</td>
 	<td valign="top">
 	Enable full dependency checking; see<br>
 	the <code>+F</code> switch in the Jikes manual.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.pedantic
 	</td>
 	<td valign="top">
 	Enable pedantic warnings.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.warnings<br>
         <strong>Deprecated</strong>. Use
   <code>&lt;javac&gt;</code>'s <code>nowarn</code>
   attribute instead.
 	</td>
 	<td valign="top">
 	Don't disable warning messages.
     </td>
     <td valign="top">
 	<code>true</code>
 	</td>
   </tr>
 </table>
 
 <h3>Jvc Notes</h3>
 
 <p>Jvc will enable Microsoft extensions unless you set the property
 <code>build.compiler.jvc.extensions</code> to false before invoking
 <code>&lt;javac&gt;</code>.</p>
 
 <h3><a name="bootstrap">Bootstrap Options</h3>
 <p>
   The Sun javac compiler has a <em>bootclasspath</em> command
   line option - this corresponds to the "bootclasspath" attribute/element
   of the &lt;javac&gt; task. The Sun compiler also allows more
   control over the boot classpath using the -X and -J-X attributes.
   One can set these by using the &lt;compilerarg&gt;. Since Ant 1.6.0,
   there is a shortcut to convert path references to strings that
   can by used in an OS independent fashion (see
   <a href="../using.html#pathshortcut">pathshortcut</a>). For example:
 </p>
 <pre>
   &lt;path id="lib.path.ref"&gt;
     &lt;fileset dir="lib" includes="*.jar"/&gt;
   &lt;/path&gt;
   &lt;javac srcdir="src" destdir="classes"&gt;
     &lt;compilerarg arg="-Xbootstrap/p:${toString:lib.path.ref}"/&gt;
   &lt;/javac&gt;
 </pre>
 
   
 </p>
 
 <h3>OpenJDK Notes</h3>
 <p>
   The <a href="https://openjdk.dev.java.net/">openjdk</a>
   project has provided the javac
   <a href="https://openjdk.dev.java.net/compiler/">compiler</a>
   as an opensource project. The output of this project is a
   <code>javac.jar</code> which contains the javac compiler.
   This compiler may be used with the <code>&lt;javac&gt;</code> task with
   the use of a <code>-Xbootclasspath/p</code> java argument. The argument needs
   to be given to the runtime system of the javac executable, so it needs
   to be prepended with a "-J". For example:
 
 <blockquote><pre>
   &lt;property name="patched.javac.jar"
             location="${my.patched.compiler}/dist/lib/javac.jar"/&gt;
 
   &lt;presetdef name="patched.javac"&gt;
     &lt;javac fork="yes"&gt;
       &lt;compilerarg value="-J-Xbootclasspath/p:${patched.javac.jar}"/&gt;
     &lt;/javac&gt;
   &lt;/presetdef&gt;
 
 
   &lt;patched.javac srcdir="src/java" destdir="build/classes"
                  debug="yes"/&gt;
 </pre></blockquote>
 
   <h3>Note on package-info.java</h3>
   <p>
     <code>package-info.java</code> files were introduced in Java5 to
     allow package level annotations. On compilation, if the java file
     does not contain runtime annotations, there will be no .class file
     for the java file. Up to <b>Ant 1.7.1</b>, when the &lt;javac&gt;
     task is run again, the
     task will try to compile the package-info java files again.
   </p>
   <p>
     In <b>Ant 1.7.1</b> the package-info.java will only be compiled if:
     <ol>
       <li>
         If a <code>package-info.class</code> file exists and is older than
         the <code>package-info.java</code> file.
       </li>
       <li>
         If the directory for the 
         <code>package-info.class</code> file does not exist.
       </li>
       <li>
         If the directory for the
         <code>package-info.class</code> file exists, and has an older
         modification time than the
         the <code>package-info.java</code> file. In this case
         &lt;javac&gt; will touch the corresponding .class directory
         on successful compilation.
       </li>
     </ol>
   </p>
 
 </body>
 </html>
diff --git a/docs/manual/CoreTasks/rmic.html b/docs/manual/CoreTasks/rmic.html
index 817f22848..2f1926668 100644
--- a/docs/manual/CoreTasks/rmic.html
+++ b/docs/manual/CoreTasks/rmic.html
@@ -1,293 +1,300 @@
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
 <title>Rmic Task</title>
 </head>
 
 <body>
 
 <h2><a name="rmic">Rmic</a></h2>
 <h3>Description</h3>
 <p>Runs the rmic compiler for a certain class.</p>
 <p>Rmic can be run on a single class (as specified with the classname
 attribute) or a number of classes at once (all classes below base that
 are neither _Stub nor _Skel classes).  If you want to rmic a single
 class and this class is a class nested into another class, you have to
 specify the classname in the form <code>Outer$$Inner</code> instead of
 <code>Outer.Inner</code>.</p>
 <p>It is possible to refine the set of files that are being rmiced. This can be
 done with the <i>includes</i>, <i>includesfile</i>, <i>excludes</i>, <i>excludesfile</i> and <i>defaultexcludes</i>
 attributes. With the <i>includes</i> or <i>includesfile</i> attribute you specify the files you want to
 have included by using patterns. The <i>exclude</i> or <i>excludesfile</i> attribute is used to specify
 the files you want to have excluded. This is also done with patterns. And
 finally with the <i>defaultexcludes</i> attribute, you can specify whether you
 want to use default exclusions or not. See the section on <a
 href="../dirtasks.html#directorybasedtasks">directory based tasks</a>, on how the
 inclusion/exclusion of files works, and how to write patterns.</p>
 <p>This task forms an implicit <a href="../CoreTypes/fileset.html">FileSet</a> and
 supports most attributes of <code>&lt;fileset&gt;</code>
 (<code>dir</code> becomes <code>base</code>) as well as the nested
 <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code> and
 <code>&lt;patternset&gt;</code> elements.</p>
 <p>It is possible to use different compilers. This can be selected
 with the &quot;build.rmic&quot; property or the <code>compiler</code>
 attribute.
 <a name="compilervalues">Here are the choices</a>:</p>
 <ul>
   <li>default -the default compiler (kaffe or sun) for the platform.
   <li>sun (the standard compiler of the JDK)</li>
   <li>kaffe (the standard compiler of <a href="http://www.kaffe.org" target="_top">Kaffe</a>)</li>
   <li>weblogic</li>
   <li>forking - the sun compiler forked into a separate process (since Ant 1.7)</li>
   <li>xnew - the sun compiler forked into a separate process,
       with the -Xnew option (since Ant 1.7).
       This is the most reliable way to use -Xnew</li>
     <li> "" (empty string). This has the same behaviour as not setting the compiler attribute.
     First the value of <tt>build.rmic</tt> is used if defined, and if not, the default
     for the platform is chosen. If build.rmic is set to this, you get the default.
 
 </ul>
 
 <p>The <a href="http://dione.zcu.cz/~toman40/miniRMI/">miniRMI</a>
 project contains a compiler implementation for this task as well,
 please consult miniRMI's documentation to learn how to use it.</p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">base</td>
     <td valign="top">the location to store the compiled files.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">classname</td>
     <td valign="top">the class for which to run <code>rmic</code>.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">filtering</td>
     <td valign="top">indicates whether token filtering should take place</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">sourcebase</td>
     <td valign="top">Pass the &quot;-keepgenerated&quot; flag to rmic and
  move the generated source file to the given sourcebase directory.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">stubversion</td>
     <td valign="top">Specify the JDK version for the generated stub code.
  Specify &quot;1.1&quot; to pass the &quot;-v1.1&quot; option to rmic,
  "1.2" for -v12, compat for -vcompat. <br>
         Since Ant1.7, if you do not specify a version, and do not ask
         for iiop or idl files, "compat" is selected.
  
  </td>
     <td align="center" valign="top">No, default="compat"</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to use during compilation</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">classpathref</td>
     <td valign="top">The classpath to use during compilation, given as <a
       href="../using.html#references">reference</a> to a PATH defined elsewhere</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includes</td>
     <td valign="top">comma- or space-separated list of patterns of files that must be
       included. All files are included when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">includesfile</td>
     <td valign="top">the name of a file. Each line of this file is
       taken to be an include pattern</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludes</td>
     <td valign="top">comma- or space-separated list of patterns of files that must be
       excluded. No files (except default excludes) are excluded when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludesfile</td>
     <td valign="top">the name of a file. Each line of this file is
       taken to be an exclude pattern</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">defaultexcludes</td>
     <td valign="top">indicates whether default excludes should be used or not
       (&quot;yes&quot;/&quot;no&quot;). Default excludes are used when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">verify</td>
     <td valign="top">check that classes implement Remote before handing them 
         to rmic (default is false)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">iiop</td>
     <td valign="top">indicates that portable (RMI/IIOP) stubs should be generated</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">iiopopts</td>
     <td valign="top">additional arguments for IIOP class generation</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">idl</td>
     <td valign="top">indicates that IDL output files should be generated</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">idlopts</td>
     <td valign="top">additional arguments for IDL file generation</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">debug</td>
     <td valign="top">generate debug info (passes -g to rmic). Defaults to false.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeAntRuntime</td> 
     <td valign="top">whether to include the Ant run-time libraries;
       defaults to <code>yes</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeJavaRuntime</td> 
     <td valign="top">whether to include the default run-time
       libraries from the executing VM; defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">extdirs</td>
     <td valign="top">location of installed extensions.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td valign="top">The compiler implementation to use.
       If this attribute is not set, the value of the
       <code>build.rmic</code> property, if set, will be used.
       Otherwise, the default compiler for the current VM will be used.
       (See the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">executable</td>
     <td valign="top">Complete path to the <code>rmic</code>
       executable to use in case of the <code>forking</code>
       or <code>xnew</code> compiler.
       Defaults to the rmic compiler of the Java version that is currently
       running Ant.<br/>
       <em>Since Ant 1.8.0</em>.</td>
     <td align="center" valign="top">No</td>
   </tr>
 </table>
 <h3>Parameters specified as nested elements</h3>
 <h4>classpath and extdirs</h4>
 <p><code>Rmic</code>'s <i>classpath</i> and <i>extdirs</i> attributes are <a
 href="../using.html#path">PATH like structure</a> and can also be set via a nested
 <i>classpath</i> and <i>extdirs</i> elements.</p>
 
 <h4>compilerarg</h4>
 
 <p>You can specify additional command line arguments for the compiler
 with nested <code>&lt;compilerarg&gt;</code> elements.  These elements
 are specified like <a href="../using.html#arg">Command-line
 Arguments</a> but have an additional attribute that can be used to
 enable arguments only if a given compiler implementation will be
 used.</p>
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
   <tr>
     <td valign="top">value</td>
     <td align="center" rowspan="4">See
     <a href="../using.html#arg">Command-line Arguments</a>.</td>
     <td align="center" rowspan="4">Exactly one of these.</td>
   </tr>
   <tr>
     <td valign="top">line</td>
   </tr>
   <tr>
     <td valign="top">file</td>
   </tr>
   <tr>
     <td valign="top">path</td>
   </tr>
   <tr>
     <td valign="top">prefix</td>
     <td align="center" rowspan="2">See
     <a href="../using.html#arg">Command-line Arguments</a>.
     <em>Since Ant 1.8.</em></td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td>Only pass the specified argument if the chosen
       compiler implementation matches the value of this attribute.
       Legal values are the
       same as those in the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center">No</td>
   </tr>
 </table>
 
+<h4>compilerclasspath <em>since Ant 1.8.0</em></h4>
+
+<p>A <a href="../using.html#path">PATH like structure</a> holding the
+  classpath to use when loading the compiler implementation if a
+  custom class has been specified.  Doesn't have any effect when
+  using one of the built-in compilers.</p>
+
 <h3>Examples</h3>
 <pre>  &lt;rmic classname=&quot;com.xyz.FooBar&quot; base=&quot;${build}/classes&quot;/&gt;</pre>
 <p>runs the rmic compiler for the class <code>com.xyz.FooBar</code>. The
 compiled files will be stored in the directory <code>${build}/classes</code>.</p>
 <pre>  &lt;rmic base=&quot;${build}/classes&quot; includes=&quot;**/Remote*.class&quot;/&gt;</pre>
 <p>runs the rmic compiler for all classes with <code>.class</code>
 files below <code>${build}/classes</code> whose classname starts with
 <i>Remote</i>. The compiled files will be stored in the directory
 <code>${build}/classes</code>.</p>
 
 
 
 </body>
 </html>
 
diff --git a/docs/manual/OptionalTasks/javah.html b/docs/manual/OptionalTasks/javah.html
index abbefea25..7e2f3729f 100644
--- a/docs/manual/OptionalTasks/javah.html
+++ b/docs/manual/OptionalTasks/javah.html
@@ -1,209 +1,216 @@
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
 <title>Javah Task</title>
 </head>
 
 <body>
 
 <h2><a name="javah">Javah</a></h2>
 <h3>Description</h3>
 <p>Generates JNI headers from a Java class.</p>
 <p> When this task executes, it will generate the C header and source files that
 are needed to implement native methods. JNI operates differently depending on
 whether <a href="http://java.sun.com/j2se/1.3/docs/tooldocs/win32/javah.html">JDK1.2</a>
 (or later) or <a href="http://java.sun.com/products/jdk/1.1/docs/tooldocs/win32/javah.html">pre-JDK1.2</a>
 systems are used.</p>
 
 <p>It is possible to use different compilers. This can be selected
 with the <code>implementation</code> attribute.  <a
 name="implementationvalues">Here are the choices</a>:</p>
 <ul>
   <li>default - the default compiler (kaffeh or sun) for the platform.</li>
   <li>sun (the standard compiler of the JDK)</li>
   <li>kaffeh (the native standard compiler of <a href="http://www.kaffe.org" target="_top">Kaffe</a>)</li>
 </ul>
 
 <p><b>Note:</b> if you are using this task to work on multiple files
   the command line may become too long on some operating systems.
   Unfortunately the javah command doesn't support command argument
   files the way javac (for example) does, so all that can be done is
   breaking the amount of classes to compile into smaller chunks.</p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td valign="top" align="center"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">class</td>
     <td valign="top">the fully-qualified name of the class (or classes,
       separated by commas)</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">outputFile</td>
     <td valign="top">concatenates the resulting header or source files for all the classes listed into this file</td>
     <td align="center" valign="middle" rowspan="2">Yes</td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">sets the directory where javah saves the header files or the
       stub files.</td>
   </tr>
   <tr>
     <td valign="top">force</td>
     <td valign="top">specifies that output files should always be written (JDK1.2 only)</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">old</td>
     <td valign="top">specifies that old JDK1.0-style header files should be generated
       (otherwise output file contain JNI-style native method      function prototypes) (JDK1.2 only)</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">stubs</td>
     <td valign="top">generate C declarations from the Java object file (used with old)</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">verbose</td>
     <td valign="top">causes Javah to print a message concerning the status     of the generated files</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">the classpath to use.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">bootclasspath</td>
     <td valign="top">location of bootstrap class files.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">extdirs</td>
     <td valign="top"> location of installed extensions.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">implementation</td>
     <td valign="top">The compiler implementation to use.  If this
     attribute is not set, the default compiler for the current VM
     will be used.  (See the above <a
     href="#implementationvalues">list</a> of valid compilers.)</td>
     <td align="center" valign="top">No</td>
   </tr>
 </table>
 <p>Either outputFile or destdir must be supplied, but not both.&nbsp;</p>
 
 <h3>Parameters specified as nested elements</h3>
 
 <h4>arg</h4>
 
 <p>You can specify additional command line arguments for the compiler
 with nested <code>&lt;arg&gt;</code> elements.  These elements are
 specified like <a href="../using.html#arg">Command-line Arguments</a>
 but have an additional attribute that can be used to enable arguments
 only if a given compiler implementation will be used.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
   <tr>
     <td valign="top">value</td>
     <td align="center" rowspan="4">See
     <a href="../using.html#arg">Command-line Arguments</a>.</td>
     <td align="center" rowspan="4">Exactly one of these.</td>
   </tr>
   <tr>
     <td valign="top">line</td>
   </tr>
   <tr>
     <td valign="top">file</td>
   </tr>
   <tr>
     <td valign="top">path</td>
   </tr>
   <tr>
     <td valign="top">prefix</td>
     <td align="center" rowspan="2">See
     <a href="../using.html#arg">Command-line Arguments</a>.
     <em>Since Ant 1.8.</em></td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">implementation</td>
     <td>Only pass the specified argument if the chosen compiler
     implementation matches the value of this attribute.  Legal values
     are the same as those in the above <a
     href="#implementationvalues">list</a> of valid compilers.)</td>
     <td align="center">No</td>
   </tr>
 </table>
 
+<h4>implementationclasspath <em>since Ant 1.8.0</em></h4>
+
+<p>A <a href="../using.html#path">PATH like structure</a> holding the
+  classpath to use when loading the compiler implementation if a
+  custom class has been specified.  Doesn't have any effect when
+  using one of the built-in compilers.</p>
+
 <h3>Examples</h3>
 <pre>  &lt;javah destdir=&quot;c&quot; class=&quot;org.foo.bar.Wibble&quot;/&gt;</pre>
 <p>makes a JNI header of the named class, using the JDK1.2 JNI model. Assuming
 the directory 'c' already exists, the file <tt>org_foo_bar_Wibble.h</tt>
 is created there. If this file already exists, it is left unchanged.</p>
 <pre>  &lt;javah outputFile=&quot;wibble.h&quot;&gt;
     &lt;class name=&quot;org.foo.bar.Wibble,org.foo.bar.Bobble&quot;/&gt;
   &lt;/javah&gt;</pre>
 <p>is similar to the previous example, except the output is written to a file
 called <tt>wibble.h</tt>
 in the current directory.</p>
 <pre>  &lt;javah destdir=&quot;c&quot; force=&quot;yes&quot;&gt;
     &lt;class name=&quot;org.foo.bar.Wibble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Bobble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Tribble&quot;/&gt;
   &lt;/javah&gt;</pre>
 <p>writes three header files, one for each of the classes named. Because the
 force option is set, these header files are always written when the Javah task
 is invoked, even if they already exist.</p>
 <pre>  &lt;javah destdir=&quot;c&quot; verbose=&quot;yes&quot; old=&quot;yes&quot; force=&quot;yes&quot;&gt;
     &lt;class name=&quot;org.foo.bar.Wibble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Bobble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Tribble&quot;/&gt;
   &lt;/javah&gt;
   &lt;javah destdir=&quot;c&quot; verbose=&quot;yes&quot; stubs=&quot;yes&quot; old=&quot;yes&quot; force=&quot;yes&quot;&gt;
     &lt;class name=&quot;org.foo.bar.Wibble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Bobble&quot;/&gt;
     &lt;class name=&quot;org.foo.bar.Tribble&quot;/&gt;
   &lt;/javah&gt;</pre>
 <p>writes the headers for the three classes using the 'old' JNI format, then
 writes the corresponding .c stubs. The verbose option will cause Javah to
 describe its progress.</p>
 
 
 </body>
 
 </html>
diff --git a/docs/manual/OptionalTasks/native2ascii.html b/docs/manual/OptionalTasks/native2ascii.html
index 2cefeca36..3b3ce5c61 100644
--- a/docs/manual/OptionalTasks/native2ascii.html
+++ b/docs/manual/OptionalTasks/native2ascii.html
@@ -1,212 +1,219 @@
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
   <head><link rel="stylesheet" type="text/css" href="../stylesheets/style.css">
 <title>Native2Ascii Task</title></head>
   <body>
     <h2>Native2Ascii</h2>
 
     <h3>Description:</h3>
 
     <p>
       Converts files from native encodings to ASCII with escaped Unicode.
       A common usage is to convert source files maintained in a native
       operating system encoding, to ASCII prior to compilation.
     </p>
 
     <p>
       Files in the directory <em>src</em>
       are converted from a native encoding to ASCII.
       By default, all files in the directory are converted.
       However, conversion may be limited to selected files using
       <em>includes</em> and <em>excludes</em> attributes.
       For more information on file matching patterns,
       see the section on
       <a href="../dirtasks.html#directorybasedtasks">directory based tasks</a>.
       If no <em>encoding</em> is specified,
       the default encoding for the JVM is used.
       If <em>ext</em> is specified, then output files are renamed
       to use it as a new extension.
       More sophisticated file name translations can be achieved using a nested
       <em><code>&lt;mapper&gt;</code></em> element. By default an
       <a href="../CoreTypes/mapper.html#identity-mapper">identity mapper</a> will be used.
       If <em>dest</em> and <em>src</em> point to the same directory,
       the <em>ext</em> attribute or a nested <em><code>&lt;mapper&gt;</code></em>
       is required.
     </p>
 
     <p>
       This task forms an implicit <a href="../CoreTypes/fileset.html">File Set</a>,
       and supports most attributes of <code>&lt;fileset&gt;</code>
       (<code>dir</code> becomes <code>src</code>) as well as
       nested <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code>,
       and <code>&lt;patternset&gt;</code> elements.
     </p>
 
     <p>It is possible to use different converters. This can be selected
       with the <code>implementation</code> attribute.
       <a name="implementationvalues">Here are the choices</a>:</p>
     <ul>
       <li>default - the default converter (kaffe or sun) for the platform.</li>
       <li>sun (the standard converter of the JDK)</li>
       <li>kaffe (the standard converter of <a href="http://www.kaffe.org" target="_top">Kaffe</a>)</li>
     </ul>
 
     <table border="1" cellpadding="2" cellspacing="0">
       <tr>
         <td><b>Attribute</b></td>
         <td><b>Description</b></td>
         <td><b>Required</b></td>
       </tr>
       <tr>
         <td>reverse</td>
         <td>Reverse the sense of the conversion,
           i.e. convert from ASCII to native <b>only supported by the
             sun converter</b></td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>encoding</td>
         <td>The native encoding the files are in
           (default is the default encoding for the JVM)</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>src</td>
         <td>The directory to find files in (default is <em>basedir</em>)</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>dest</td>
         <td>The directory to output file to</td>
         <td align="center">Yes</td>
       </tr>
       <tr>
         <td>ext</td>
         <td>File extension to use in renaming output files</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>defaultexcludes</td>
         <td>indicates whether default excludes should be used or not
           (&quot;yes&quot;/&quot;no&quot;).
           Default excludes are used when omitted.
         </td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>includes</td>
         <td>comma- or space-separated list of patterns of files that must be
           included. All files are included when omitted.</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>includesfile</td>
         <td>the name of a file. Each line of this file is
           taken to be an include pattern</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>excludes</td>
         <td>comma- or space-separated list of patterns of files that must be excluded.
           No files (except default excludes) are excluded when omitted.</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td>excludesfile</td>
         <td>the name of a file. Each line of this file is
           taken to be an exclude pattern</td>
         <td align="center">No</td>
       </tr>
       <tr>
         <td valign="top">implementation</td>
         <td valign="top">The converter implementation to use.
           If this attribute is not set, the default converter for the
           current VM will be used.  (See the above <a
           href="#implementationvalues">list</a> of valid converters.)</td>
         <td align="center" valign="top">No</td>
       </tr>
     </table>
 
 <h3>Parameters specified as nested elements</h3>
 
 <h4>arg</h4>
 
 <p>You can specify additional command line arguments for the converter
 with nested <code>&lt;arg&gt;</code> elements.  These elements are
 specified like <a href="../using.html#arg">Command-line Arguments</a>
 but have an additional attribute that can be used to enable arguments
 only if a given converter implementation will be used.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
   <tr>
     <td valign="top">value</td>
     <td align="center" rowspan="4">See
     <a href="../using.html#arg">Command-line Arguments</a>.</td>
     <td align="center" rowspan="4">Exactly one of these.</td>
   </tr>
   <tr>
     <td valign="top">line</td>
   </tr>
   <tr>
     <td valign="top">file</td>
   </tr>
   <tr>
     <td valign="top">path</td>
   </tr>
   <tr>
     <td valign="top">implementation</td>
     <td>Only pass the specified argument if the chosen converter
     implementation matches the value of this attribute.  Legal values
     are the same as those in the above <a
     href="#implementationvalues">list</a> of valid compilers.)</td>
     <td align="center">No</td>
   </tr>
 </table>
 
+<h4>implementationclasspath <em>since Ant 1.8.0</em></h4>
+
+<p>A <a href="../using.html#path">PATH like structure</a> holding the
+  classpath to use when loading the converter implementation if a
+  custom class has been specified.  Doesn't have any effect when
+  using one of the built-in converters.</p>
+
     <h3>Examples</h3>
 
     <pre>
 &lt;native2ascii encoding=&quot;EUCJIS&quot; src=&quot;srcdir&quot; dest=&quot;srcdir&quot;
    includes=&quot;**/*.eucjis&quot; ext=&quot;.java&quot;/&gt;
     </pre>
 
     <p>
       Converts all files in the directory <em>srcdir</em>
       ending in <code>.eucjis</code> from the EUCJIS encoding to ASCII
       and renames them to end in <code>.java</code>.
     </p>
 
 <pre>
 &lt;native2ascii encoding=&quot;EUCJIS&quot; src=&quot;native/japanese&quot; dest=&quot;src&quot;
    includes=&quot;**/*.java&quot;/&gt;
 </pre>
 
     <p>
       Converts all the files ending in <code>.java</code>
       in the directory <em>native/japanese</em> to ASCII,
       placing the results in the directory <em>src</em>.
       The names of the files remain the same.
     </p>
   </body>
 
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index 3b2be7de5..6ddcefd04 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,1180 +1,1191 @@
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
 import java.io.OutputStream;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.FileUtils;
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
 
     private static final String JAVAC16 = "javac1.6";
     private static final String JAVAC15 = "javac1.5";
     private static final String JAVAC14 = "javac1.4";
     private static final String JAVAC13 = "javac1.3";
     private static final String JAVAC12 = "javac1.2";
     private static final String JAVAC11 = "javac1.1";
     private static final String MODERN = "modern";
     private static final String CLASSIC = "classic";
     private static final String EXTJAVAC = "extJavac";
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
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
     private Boolean includeAntRuntime;
     private boolean includeJavaRuntime = false;
     private boolean fork = false;
     private String forkedExecutable = null;
     private boolean nowarn = false;
     private String memoryInitialSize;
     private String memoryMaximumSize;
     private FacadeTaskHelper facade = null;
 
     // CheckStyle:VisibilityModifier OFF - bc
     protected boolean failOnError = true;
     protected boolean listFiles = false;
     protected File[] compileList = new File[0];
     private Map/*<String,Long>*/ packageInfos = new HashMap();
     // CheckStyle:VisibilityModifier ON
 
     private String source;
     private String debugLevel;
     private File tmpDir;
     private String updatedProperty;
     private String errorProperty;
     private boolean taskSuccess = true; // assume the best
     private boolean includeDestClasses = true;
 
     /**
      * Javac task for compilation of Java files.
      */
     public Javac() {
         facade = new FacadeTaskHelper(assumedJavaVersion());
     }
 
     private String assumedJavaVersion() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)) {
             return JAVAC12;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
             return JAVAC13;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4)) {
             return JAVAC14;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5)) {
             return JAVAC15;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6)) {
             return JAVAC16;
         } else {
             return CLASSIC;
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
         return source != null
             ? source : getProject().getProperty(MagicNames.BUILD_JAVAC_SOURCE);
     }
 
     /**
      * Value of the -source command-line switch; will be ignored by
      * all implementations except modern, jikes and gcj (gcj uses
      * -fsource).
      *
      * <p>If you use this attribute together with jikes or gcj, you
      * must make sure that your version of jikes supports the -source
      * switch.</p>
      *
      * <p>Legal values are 1.3, 1.4, 1.5, and 5 - by default, no
      * -source argument will be used at all.</p>
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
      * "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "5" and "6".
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
         return targetAttribute != null
             ? targetAttribute
             : getProject().getProperty(MagicNames.BUILD_JAVAC_TARGET);
     }
 
     /**
      * If true, includes Ant's own classpath in the classpath.
      * @param include if true, includes Ant's own classpath in the classpath
      */
     public void setIncludeantruntime(boolean include) {
         includeAntRuntime = Boolean.valueOf(include);
     }
 
     /**
      * Gets whether or not the ant classpath is to be included in the classpath.
      * @return whether or not the ant classpath is to be included in the classpath
      */
     public boolean getIncludeantruntime() {
         return includeAntRuntime != null ? includeAntRuntime.booleanValue() : true;
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
         try {
             // make sure facade knows about magic properties and fork setting
             String appliedCompiler = getCompiler();
             facade.setImplementation(appliedCompiler);
 
             String[] result = facade.getArgs();
 
             String altCompilerName = getAltCompilerName(facade.getImplementation());
 
             if (result.length == 0 && altCompilerName != null) {
                 facade.setImplementation(altCompilerName);
                 result = facade.getArgs();
             }
 
             return result;
 
         } finally {
             facade.setImplementation(chosen);
         }
     }
 
     private String getAltCompilerName(String anImplementation) {
         if (JAVAC16.equalsIgnoreCase(anImplementation)
                 || JAVAC15.equalsIgnoreCase(anImplementation)
                 || JAVAC14.equalsIgnoreCase(anImplementation)
                 || JAVAC13.equalsIgnoreCase(anImplementation)) {
             return MODERN;
         }
         if (JAVAC12.equalsIgnoreCase(anImplementation)
                 || JAVAC11.equalsIgnoreCase(anImplementation)) {
             return CLASSIC;
         }
         if (MODERN.equalsIgnoreCase(anImplementation)) {
             String nextSelected = assumedJavaVersion();
             if (JAVAC16.equalsIgnoreCase(nextSelected)
                     || JAVAC15.equalsIgnoreCase(nextSelected)
                     || JAVAC14.equalsIgnoreCase(nextSelected)
                     || JAVAC13.equalsIgnoreCase(nextSelected)) {
                 return nextSelected;
             }
         }
         if (CLASSIC.equals(anImplementation)) {
             return assumedJavaVersion();
         }
         if (EXTJAVAC.equalsIgnoreCase(anImplementation)) {
             return assumedJavaVersion();
         }
         return null;
     }
 
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
      * The property to set on compliation success.
      * This property will not be set if the compilation
      * fails, or if there are no files to compile.
      * @param updatedProperty the property name to use.
      * @since Ant 1.7.1.
      */
     public void setUpdatedProperty(String updatedProperty) {
         this.updatedProperty = updatedProperty;
     }
 
     /**
      * The property to set on compliation failure.
      * This property will be set if the compilation
      * fails.
      * @param errorProperty the property name to use.
      * @since Ant 1.7.1.
      */
     public void setErrorProperty(String errorProperty) {
         this.errorProperty = errorProperty;
     }
 
     /**
      * This property controls whether to include the
      * destination classes directory in the classpath
      * given to the compiler.
      * The default value is "true".
      * @param includeDestClasses the value to use.
      */
     public void setIncludeDestClasses(boolean includeDestClasses) {
         this.includeDestClasses = includeDestClasses;
     }
 
     /**
      * Get the value of the includeDestClasses property.
      * @return the value.
      */
     public boolean isIncludeDestClasses() {
         return includeDestClasses;
     }
 
     /**
      * Get the result of the javac task (success or failure).
      * @return true if compilation succeeded, or
      *         was not neccessary, false if the compilation failed.
      */
     public boolean getTaskSuccess() {
         return taskSuccess;
     }
 
     /**
+     * The classpath to use when loading the compiler implementation
+     * if it is not a built-in one.
+     *
+     * @since Ant 1.8.0
+     */
+    public Path createCompilerClasspath() {
+        return facade.getImplementationClasspath(getProject());
+    }
+
+    /**
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
         if (updatedProperty != null
             && taskSuccess
             && compileList.length != 0) {
             getProject().setNewProperty(updatedProperty, "true");
         }
     }
 
     /**
      * Clear the list of files to be compiled and copied..
      */
     protected void resetFileLists() {
         compileList = new File[0];
         packageInfos = new HashMap();
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
             lookForPackageInfos(srcDir, newFiles);
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
      * @return true if compilerImpl is "modern", "classic",
      * "javac1.1", "javac1.2", "javac1.3", "javac1.4", "javac1.5" or
      * "javac1.6".
      */
     protected boolean isJdkCompiler(String compilerImpl) {
         return MODERN.equals(compilerImpl)
             || CLASSIC.equals(compilerImpl)
             || JAVAC16.equals(compilerImpl)
             || JAVAC15.equals(compilerImpl)
             || JAVAC14.equals(compilerImpl)
             || JAVAC13.equals(compilerImpl)
             || JAVAC12.equals(compilerImpl)
             || JAVAC11.equals(compilerImpl);
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
      * @return the compiler.
      * @since Ant 1.5
      */
     public String getCompiler() {
         String compilerImpl = getCompilerVersion();
         if (fork) {
             if (isJdkCompiler(compilerImpl)) {
                 compilerImpl = "extJavac";
             } else {
                 log("Since compiler setting isn't classic or modern, "
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
      * @return the compiler.
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
         if (includeAntRuntime == null && getProject().getProperty("build.sysclasspath") == null) {
             log(getLocation() + "warning: 'includeantruntime' was not set, " +
                     "defaulting to build.sysclasspath=last; set to false for repeatable builds",
                     Project.MSG_WARN);
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
-                CompilerAdapterFactory.getCompiler(compilerImpl, this);
+                CompilerAdapterFactory.getCompiler(compilerImpl, this,
+                                                   createCompilerClasspath());
 
             // now we need to populate the compiler adapter
             adapter.setJavac(this);
 
             // finally, lets execute the compiler!!
             if (adapter.execute()) {
                 // Success
                 try {
                     generateMissingPackageInfoClasses();
                 } catch (IOException x) {
                     // Should this be made a nonfatal warning?
                     throw new BuildException(x, getLocation());
                 }
             } else {
                 // Fail path
                 this.taskSuccess = false;
                 if (errorProperty != null) {
                     getProject().setNewProperty(
                         errorProperty, "true");
                 }
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
 
     private void lookForPackageInfos(File srcDir, File[] newFiles) {
         for (int i = 0; i < newFiles.length; i++) {
             File f = newFiles[i];
             if (!f.getName().equals("package-info.java")) {
                 continue;
             }
             String path = FILE_UTILS.removeLeadingPath(srcDir, f).
                     replace(File.separatorChar, '/');
             String suffix = "/package-info.java";
             if (!path.endsWith(suffix)) {
                 log("anomalous package-info.java path: " + path, Project.MSG_WARN);
                 continue;
             }
             String pkg = path.substring(0, path.length() - suffix.length());
             packageInfos.put(pkg, new Long(f.lastModified()));
         }
     }
 
     /**
      * Ensure that every {@code package-info.java} produced a {@code package-info.class}.
      * Otherwise this task's up-to-date tracking mechanisms do not work.
      * @see <a href="https://issues.apache.org/bugzilla/show_bug.cgi?id=43114">Bug #43114</a>
      */
     private void generateMissingPackageInfoClasses() throws IOException {
         for (Iterator i = packageInfos.entrySet().iterator(); i.hasNext(); ) {
             Map.Entry entry = (Map.Entry) i.next();
             String pkg = (String) entry.getKey();
             Long sourceLastMod = (Long) entry.getValue();
             File pkgBinDir = new File(destDir, pkg.replace('/', File.separatorChar));
             pkgBinDir.mkdirs();
             File pkgInfoClass = new File(pkgBinDir, "package-info.class");
             if (pkgInfoClass.isFile() && pkgInfoClass.lastModified() >= sourceLastMod.longValue()) {
                 continue;
             }
             log("Creating empty " + pkgInfoClass);
             OutputStream os = new FileOutputStream(pkgInfoClass);
             try {
                 os.write(PACKAGE_INFO_CLASS_HEADER);
                 byte[] name = pkg.getBytes("UTF-8");
                 int length = name.length + /* "/package-info" */ 13;
                 os.write((byte) length / 256);
                 os.write((byte) length % 256);
                 os.write(name);
                 os.write(PACKAGE_INFO_CLASS_FOOTER);
             } finally {
                 os.close();
             }
         }
     }
     private static final byte[] PACKAGE_INFO_CLASS_HEADER = {
         (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe, 0x00, 0x00, 0x00,
         0x31, 0x00, 0x07, 0x07, 0x00, 0x05, 0x07, 0x00, 0x06, 0x01, 0x00, 0x0a,
         0x53, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x46, 0x69, 0x6c, 0x65, 0x01, 0x00,
         0x11, 0x70, 0x61, 0x63, 0x6b, 0x61, 0x67, 0x65, 0x2d, 0x69, 0x6e, 0x66,
         0x6f, 0x2e, 0x6a, 0x61, 0x76, 0x61, 0x01
     };
     private static final byte[] PACKAGE_INFO_CLASS_FOOTER = {
         0x2f, 0x70, 0x61, 0x63, 0x6b, 0x61, 0x67, 0x65, 0x2d, 0x69, 0x6e, 0x66,
         0x6f, 0x01, 0x00, 0x10, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e,
         0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74, 0x02, 0x00, 0x00, 0x01,
         0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03,
         0x00, 0x00, 0x00, 0x02, 0x00, 0x04
     };
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Rmic.java b/src/main/org/apache/tools/ant/taskdefs/Rmic.java
index a69f87360..fcc9e6d46 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Rmic.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Rmic.java
@@ -1,738 +1,749 @@
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
 import java.io.IOException;
 import java.rmi.Remote;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
 import org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 
 /**
  * Runs the rmic compiler against classes.</p>
  * <p>Rmic can be run on a single class (as specified with the classname
  * attribute) or a number of classes at once (all classes below base that
  * are neither _Stub nor _Skel classes).  If you want to rmic a single
  * class and this class is a class nested into another class, you have to
  * specify the classname in the form <code>Outer$$Inner</code> instead of
  * <code>Outer.Inner</code>.</p>
  * <p>It is possible to refine the set of files that are being rmiced. This can
  * be done with the <i>includes</i>, <i>includesfile</i>, <i>excludes</i>,
  * <i>excludesfile</i> and <i>defaultexcludes</i>
  * attributes. With the <i>includes</i> or <i>includesfile</i> attribute you
  * specify the files you want to have included by using patterns. The
  * <i>exclude</i> or <i>excludesfile</i> attribute is used to specify
  * the files you want to have excluded. This is also done with patterns. And
  * finally with the <i>defaultexcludes</i> attribute, you can specify whether
  * you want to use default exclusions or not. See the section on
  * directory based tasks</a>, on how the
  * inclusion/exclusion of files works, and how to write patterns.</p>
  * <p>This task forms an implicit FileSet and
  * supports all attributes of <code>&lt;fileset&gt;</code>
  * (<code>dir</code> becomes <code>base</code>) as well as the nested
  * <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code> and
  * <code>&lt;patternset&gt;</code> elements.</p>
  * <p>It is possible to use different compilers. This can be selected
  * with the &quot;build.rmic&quot; property or the <code>compiler</code>
  * attribute. <a name="compilervalues">There are three choices</a>:</p>
  * <ul>
  *   <li>sun (the standard compiler of the JDK)</li>
  *   <li>kaffe (the standard compiler of
  *       {@link <a href="http://www.kaffe.org">Kaffe</a>})</li>
  *   <li>weblogic</li>
  * </ul>
  *
  * <p> The <a href="http://dione.zcu.cz/~toman40/miniRMI/">miniRMI</a>
  * project contains a compiler implementation for this task as well,
  * please consult miniRMI's documentation to learn how to use it.</p>
  *
  * @since Ant 1.1
  *
  * @ant.task category="java"
  */
 
 public class Rmic extends MatchingTask {
 
     /** rmic failed message */
     public static final String ERROR_RMIC_FAILED
             = "Rmic failed; see the compiler error output for details.";
 
     private File baseDir;
     private String classname;
     private File sourceBase;
     private String stubVersion;
     private Path compileClasspath;
     private Path extDirs;
     private boolean verify = false;
     private boolean filtering = false;
 
     private boolean iiop = false;
     private String  iiopOpts;
     private boolean idl  = false;
     private String  idlOpts;
     private boolean debug  = false;
     private boolean includeAntRuntime = true;
     private boolean includeJavaRuntime = false;
 
     private Vector compileList = new Vector();
 
     private ClassLoader loader = null;
 
     private FacadeTaskHelper facade;
     /** unable to verify message */
     public static final String ERROR_UNABLE_TO_VERIFY_CLASS = "Unable to verify class ";
     /** could not be found message */
     public static final String ERROR_NOT_FOUND = ". It could not be found.";
     /** not defined message */
     public static final String ERROR_NOT_DEFINED = ". It is not defined.";
     /** loaded error message */
     public static final String ERROR_LOADING_CAUSED_EXCEPTION = ". Loading caused Exception: ";
     /** base not exists message */
     public static final String ERROR_NO_BASE_EXISTS = "base does not exist: ";
     /** base not a directory message */
     public static final String ERROR_NOT_A_DIR = "base is not a directory:";
     /** base attribute not set message */
     public static final String ERROR_BASE_NOT_SET = "base attribute must be set!";
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     private String executable = null;
 
     /**
      * Constructor for Rmic.
      */
     public Rmic() {
         facade = new FacadeTaskHelper(RmicAdapterFactory.DEFAULT_COMPILER);
     }
 
     /**
      * Sets the location to store the compiled files; required
      * @param base the location to store the compiled files
      */
     public void setBase(File base) {
         this.baseDir = base;
     }
 
     /**
      * Gets the base directory to output generated class.
      * @return the location of the compiled files
      */
 
     public File getBase() {
         return this.baseDir;
     }
 
     /**
      * Sets the class to run <code>rmic</code> against;
      * optional
      * @param classname the name of the class for rmic to create code for
      */
     public void setClassname(String classname) {
         this.classname = classname;
     }
 
     /**
      * Gets the class name to compile.
      * @return the name of the class to compile
      */
     public String getClassname() {
         return classname;
     }
 
     /**
      * optional directory to save generated source files to.
      * @param sourceBase the directory to save source files to.
      */
     public void setSourceBase(File sourceBase) {
         this.sourceBase = sourceBase;
     }
 
     /**
      * Gets the source dirs to find the source java files.
      * @return sourceBase the directory containing the source files.
      */
     public File getSourceBase() {
         return sourceBase;
     }
 
     /**
      * Specify the JDK version for the generated stub code.
      * Specify &quot;1.1&quot; to pass the &quot;-v1.1&quot; option to rmic.</td>
      * @param stubVersion the JDK version
      */
     public void setStubVersion(String stubVersion) {
         this.stubVersion = stubVersion;
     }
 
     /**
      * Gets the JDK version for the generated stub code.
      * @return stubVersion
      */
     public String getStubVersion() {
         return stubVersion;
     }
 
     /**
      * Sets token filtering [optional], default=false
      * @param filter turn on token filtering
      */
     public void setFiltering(boolean filter) {
         this.filtering = filter;
     }
 
     /**
      * Gets whether token filtering is set
      * @return filtering
      */
     public boolean getFiltering() {
         return filtering;
     }
 
     /**
      * Generate debug info (passes -g to rmic);
      * optional, defaults to false
      * @param debug turn on debug info
      */
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     /**
      * Gets the debug flag.
      * @return debug
      */
     public boolean getDebug() {
         return debug;
     }
 
     /**
      * Set the classpath to be used for this compilation.
      * @param classpath the classpath used for this compilation
      */
     public synchronized void setClasspath(Path classpath) {
         if (compileClasspath == null) {
             compileClasspath = classpath;
         } else {
             compileClasspath.append(classpath);
         }
     }
 
     /**
      * Creates a nested classpath element.
      * @return classpath
      */
     public synchronized Path createClasspath() {
         if (compileClasspath == null) {
             compileClasspath = new Path(getProject());
         }
         return compileClasspath.createPath();
     }
 
     /**
      * Adds to the classpath a reference to
      * a &lt;path&gt; defined elsewhere.
      * @param pathRef the reference to add to the classpath
      */
     public void setClasspathRef(Reference pathRef) {
         createClasspath().setRefid(pathRef);
     }
 
     /**
      * Gets the classpath.
      * @return the classpath
      */
     public Path getClasspath() {
         return compileClasspath;
     }
 
     /**
      * Flag to enable verification so that the classes
      * found by the directory match are
      * checked to see if they implement java.rmi.Remote.
      * optional; This defaults to false if not set.
      * @param verify turn on verification for classes
      */
     public void setVerify(boolean verify) {
         this.verify = verify;
     }
 
     /**
      * Get verify flag.
      * @return verify
      */
     public boolean getVerify() {
         return verify;
     }
 
     /**
      * Indicates that IIOP compatible stubs should
      * be generated; optional, defaults to false
      * if not set.
      * @param iiop generate IIOP compatible stubs
      */
     public void setIiop(boolean iiop) {
         this.iiop = iiop;
     }
 
     /**
      * Gets iiop flags.
      * @return iiop
      */
     public boolean getIiop() {
         return iiop;
     }
 
     /**
      * Set additional arguments for iiop
      * @param iiopOpts additional arguments for iiop
      */
     public void setIiopopts(String iiopOpts) {
         this.iiopOpts = iiopOpts;
     }
 
     /**
      * Gets additional arguments for iiop.
      * @return iiopOpts
      */
     public String getIiopopts() {
         return iiopOpts;
     }
 
     /**
      * Indicates that IDL output should be
      * generated.  This defaults to false
      * if not set.
      * @param idl generate IDL output
      */
     public void setIdl(boolean idl) {
         this.idl = idl;
     }
 
     /**
      * Gets IDL flags.
      * @return the idl flag
      */
     public boolean getIdl() {
         return idl;
     }
 
     /**
      * pass additional arguments for IDL compile
      * @param idlOpts additional IDL arguments
      */
     public void setIdlopts(String idlOpts) {
         this.idlOpts = idlOpts;
     }
 
     /**
      * Gets additional arguments for idl compile.
      * @return the idl options
      */
     public String getIdlopts() {
         return idlOpts;
     }
 
     /**
      * Gets file list to compile.
      * @return the list of files to compile.
      */
     public Vector getFileList() {
         return compileList;
     }
 
     /**
      * Sets whether or not to include ant's own classpath in this task's
      * classpath.
      * Optional; default is <code>true</code>.
      * @param include if true include ant's classpath
      */
     public void setIncludeantruntime(boolean include) {
         includeAntRuntime = include;
     }
 
     /**
      * Gets whether or not the ant classpath is to be included in the
      * task's classpath.
      * @return true if ant's classpath is to be included
      */
     public boolean getIncludeantruntime() {
         return includeAntRuntime;
     }
 
     /**
      * task's classpath.
      * Enables or disables including the default run-time
      * libraries from the executing VM; optional,
      * defaults to false
      * @param include if true include default run-time libraries
      */
     public void setIncludejavaruntime(boolean include) {
         includeJavaRuntime = include;
     }
 
     /**
      * Gets whether or not the java runtime should be included in this
      * task's classpath.
      * @return true if default run-time libraries are included
      */
     public boolean getIncludejavaruntime() {
         return includeJavaRuntime;
     }
 
     /**
      * Sets the extension directories that will be used during the
      * compilation; optional.
      * @param extDirs the extension directories to be used
      */
     public synchronized void setExtdirs(Path extDirs) {
         if (this.extDirs == null) {
             this.extDirs = extDirs;
         } else {
             this.extDirs.append(extDirs);
         }
     }
 
     /**
      * Maybe creates a nested extdirs element.
      * @return path object to be configured with the extension directories
      */
     public synchronized Path createExtdirs() {
         if (extDirs == null) {
             extDirs = new Path(getProject());
         }
         return extDirs.createPath();
     }
 
     /**
      * Gets the extension directories that will be used during the
      * compilation.
      * @return the extension directories to be used
      */
     public Path getExtdirs() {
         return extDirs;
     }
 
     /**
      * @return the compile list.
      */
     public Vector getCompileList() {
         return compileList;
     }
 
     /**
      * Sets the compiler implementation to use; optional,
      * defaults to the value of the <code>build.rmic</code> property,
      * or failing that, default compiler for the current VM
      * @param compiler the compiler implemention to use
      * @since Ant 1.5
      */
     public void setCompiler(String compiler) {
         if (compiler.length() > 0) {
             facade.setImplementation(compiler);
         }
     }
 
     /**
      * get the name of the current compiler
      * @return the name of the compiler
      * @since Ant 1.5
      */
     public String getCompiler() {
         facade.setMagicValue(getProject().getProperty("build.rmic"));
         return facade.getImplementation();
     }
 
     /**
      * Adds an implementation specific command line argument.
      * @return an object to be configured with a command line argument
      * @since Ant 1.5
      */
     public ImplementationSpecificArgument createCompilerArg() {
         ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
         facade.addImplementationArgument(arg);
         return arg;
     }
 
     /**
      * Get the additional implementation specific command line arguments.
      * @return array of command line arguments, guaranteed to be non-null.
      * @since Ant 1.5
      */
     public String[] getCurrentCompilerArgs() {
         getCompiler();
         return facade.getArgs();
     }
 
     /**
      * Name of the executable to use when forking.
      *
      * @since Ant 1.8.0
      */
     public void setExecutable(String ex) {
         executable = ex;
     }
 
     /**
      * Explicitly specified name of the executable to use when forking
      * - if any.
      *
      * @since Ant 1.8.0
      */
     public String getExecutable() {
         return executable;
     }
 
     /**
+     * The classpath to use when loading the compiler implementation
+     * if it is not a built-in one.
+     *
+     * @since Ant 1.8.0
+     */
+    public Path createCompilerClasspath() {
+        return facade.getImplementationClasspath(getProject());
+    }
+
+    /**
      * execute by creating an instance of an implementation
      * class and getting to do the work
      * @throws org.apache.tools.ant.BuildException
      * if there's a problem with baseDir or RMIC
      */
     public void execute() throws BuildException {
         if (baseDir == null) {
             throw new BuildException(ERROR_BASE_NOT_SET, getLocation());
         }
         if (!baseDir.exists()) {
             throw new BuildException(ERROR_NO_BASE_EXISTS + baseDir, getLocation());
         }
         if (!baseDir.isDirectory()) {
             throw new BuildException(ERROR_NOT_A_DIR + baseDir, getLocation());
         }
         if (verify) {
             log("Verify has been turned on.", Project.MSG_VERBOSE);
         }
-        RmicAdapter adapter = RmicAdapterFactory.getRmic(getCompiler(), this);
+        RmicAdapter adapter = RmicAdapterFactory.getRmic(getCompiler(), this,
+                                                         createCompilerClasspath());
 
         // now we need to populate the compiler adapter
         adapter.setRmic(this);
 
         Path classpath = adapter.getClasspath();
         loader = getProject().createClassLoader(classpath);
 
         try {
             // scan base dirs to build up compile lists only if a
             // specific classname is not given
             if (classname == null) {
                 DirectoryScanner ds = this.getDirectoryScanner(baseDir);
                 String[] files = ds.getIncludedFiles();
                 scanDir(baseDir, files, adapter.getMapper());
             } else {
                 // otherwise perform a timestamp comparison - at least
                 String path = classname.replace('.', File.separatorChar) + ".class";
                 File f = new File(baseDir, path);
                 if (f.isFile()) {
                     scanDir(baseDir, new String[] {path}, adapter.getMapper());
                 } else {
                     // Does not exist, so checking whether it is up to date makes no sense.
                     // Compilation will fail later anyway, but tests expect a certain output.
                     compileList.add(classname);
                 }
             }
             int fileCount = compileList.size();
             if (fileCount > 0) {
                 log("RMI Compiling " + fileCount + " class" + (fileCount > 1 ? "es" : "") + " to "
                         + baseDir, Project.MSG_INFO);
                 // finally, lets execute the compiler!!
                 if (!adapter.execute()) {
                     throw new BuildException(ERROR_RMIC_FAILED, getLocation());
                 }
             }
             /*
              * Move the generated source file to the base directory.  If
              * base directory and sourcebase are the same, the generated
              * sources are already in place.
              */
             if (null != sourceBase && !baseDir.equals(sourceBase)
                 && fileCount > 0) {
                 if (idl) {
                     log("Cannot determine sourcefiles in idl mode, ", Project.MSG_WARN);
                     log("sourcebase attribute will be ignored.", Project.MSG_WARN);
                 } else {
                     for (int j = 0; j < fileCount; j++) {
                         moveGeneratedFile(baseDir, sourceBase, (String) compileList.elementAt(j),
                                 adapter);
                     }
                 }
             }
         } finally {
             compileList.removeAllElements();
         }
     }
 
     /**
      * Move the generated source file(s) to the base directory
      *
      * @throws org.apache.tools.ant.BuildException When error
      * copying/removing files.
      */
     private void moveGeneratedFile(File baseDir, File sourceBaseFile, String classname,
             RmicAdapter adapter) throws BuildException {
         String classFileName = classname.replace('.', File.separatorChar) + ".class";
         String[] generatedFiles = adapter.getMapper().mapFileName(classFileName);
 
         for (int i = 0; i < generatedFiles.length; i++) {
             final String generatedFile = generatedFiles[i];
             if (!generatedFile.endsWith(".class")) {
                 // don't know how to handle that - a IDL file doesn't
                 // have a corresponding Java source for example.
                 continue;
             }
             String sourceFileName = StringUtils.removeSuffix(generatedFile, ".class");
 
             File oldFile = new File(baseDir, sourceFileName);
             if (!oldFile.exists()) {
                 // no source file generated, nothing to move
                 continue;
             }
 
             File newFile = new File(sourceBaseFile, sourceFileName);
             try {
                 if (filtering) {
                     FILE_UTILS.copyFile(oldFile, newFile, new FilterSetCollection(getProject()
                             .getGlobalFilterSet()));
                 } else {
                     FILE_UTILS.copyFile(oldFile, newFile);
                 }
                 oldFile.delete();
             } catch (IOException ioe) {
                 String msg = "Failed to copy " + oldFile + " to " + newFile + " due to "
                         + ioe.getMessage();
                 throw new BuildException(msg, ioe, getLocation());
             }
         }
     }
 
     /**
      * Scans the directory looking for class files to be compiled.
      * The result is returned in the class variable compileList.
      * @param baseDir the base direction
      * @param files   the list of files to scan
      * @param mapper  the mapper of files to target files
      */
     protected void scanDir(File baseDir, String[] files, FileNameMapper mapper) {
         String[] newFiles = files;
         if (idl) {
             log("will leave uptodate test to rmic implementation in idl mode.",
                 Project.MSG_VERBOSE);
         } else if (iiop && iiopOpts != null && iiopOpts.indexOf("-always") > -1) {
             log("no uptodate test as -always option has been specified", Project.MSG_VERBOSE);
         } else {
             SourceFileScanner sfs = new SourceFileScanner(this);
             newFiles = sfs.restrict(files, baseDir, baseDir, mapper);
         }
         for (int i = 0; i < newFiles.length; i++) {
             String name = newFiles[i].replace(File.separatorChar, '.');
             name = name.substring(0, name.lastIndexOf(".class"));
             compileList.addElement(name);
         }
     }
 
     /**
      * Load named class and test whether it can be rmic'ed
      * @param classname the name of the class to be tested
      * @return true if the class can be rmic'ed
      */
     public boolean isValidRmiRemote(String classname) {
         try {
             Class testClass = loader.loadClass(classname);
             // One cannot RMIC an interface for "classic" RMI (JRMP)
             if (testClass.isInterface() && !iiop && !idl) {
                 return false;
             }
             return isValidRmiRemote(testClass);
         } catch (ClassNotFoundException e) {
             log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_NOT_FOUND, Project.MSG_WARN);
         } catch (NoClassDefFoundError e) {
             log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_NOT_DEFINED, Project.MSG_WARN);
         } catch (Throwable t) {
             log(ERROR_UNABLE_TO_VERIFY_CLASS + classname + ERROR_LOADING_CAUSED_EXCEPTION
                     + t.getMessage(), Project.MSG_WARN);
         }
         // we only get here if an exception has been thrown
         return false;
     }
 
     /**
      * Returns the topmost interface that extends Remote for a given
      * class - if one exists.
      * @param testClass the class to be tested
      * @return the topmost interface that extends Remote, or null if there
      *         is none.
      */
     public Class getRemoteInterface(Class testClass) {
         if (Remote.class.isAssignableFrom(testClass)) {
             Class [] interfaces = testClass.getInterfaces();
             if (interfaces != null) {
                 for (int i = 0; i < interfaces.length; i++) {
                     if (Remote.class.isAssignableFrom(interfaces[i])) {
                         return interfaces[i];
                     }
                 }
             }
         }
         return null;
     }
 
     /**
      * Check to see if the class or (super)interfaces implement
      * java.rmi.Remote.
      */
     private boolean isValidRmiRemote (Class testClass) {
         return getRemoteInterface(testClass) != null;
     }
 
     /**
      * Classloader for the user-specified classpath.
      * @return the classloader
      */
     public ClassLoader getLoader() {
         return loader;
     }
 
     /**
      * Adds an "compiler" attribute to Commandline$Attribute used to
      * filter command line attributes based on the current
      * implementation.
      */
     public class ImplementationSpecificArgument extends
             org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
         /**
          * Only pass the specified argument if the
          * chosen compiler implementation matches the
          * value of this attribute. Legal values are
          * the same as those in the above list of
          * valid compilers.)
          * @param impl the compiler to be used.
          */
         public void setCompiler(String impl) {
             super.setImplementation(impl);
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/CompilerAdapterFactory.java b/src/main/org/apache/tools/ant/taskdefs/compilers/CompilerAdapterFactory.java
index 8cf2bb43e..38ebb1120 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/CompilerAdapterFactory.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/CompilerAdapterFactory.java
@@ -1,176 +1,215 @@
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
+import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.ClasspathUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  * Creates the necessary compiler adapter, given basic criteria.
  *
  * @since Ant 1.3
  */
 public final class CompilerAdapterFactory {
     private static final String MODERN_COMPILER = "com.sun.tools.javac.Main";
 
     /** This is a singleton -- can't create instances!! */
     private CompilerAdapterFactory() {
     }
 
     /**
      * Based on the parameter passed in, this method creates the necessary
      * factory desired.
      *
      * The current mapping for compiler names are as follows:
      * <ul><li>jikes = jikes compiler
      * <li>classic, javac1.1, javac1.2 = the standard compiler from JDK
      * 1.1/1.2
      * <li>modern, javac1.3, javac1.4, javac1.5 = the compiler of JDK 1.3+
      * <li>jvc, microsoft = the command line compiler from Microsoft's SDK
      * for Java / Visual J++
      * <li>kjc = the kopi compiler</li>
      * <li>gcj = the gcj compiler from gcc</li>
      * <li>sj, symantec = the Symantec Java compiler</li>
      * <li><i>a fully qualified classname</i> = the name of a compiler
      * adapter
      * </ul>
      *
      * @param compilerType either the name of the desired compiler, or the
      * full classname of the compiler's adapter.
      * @param task a task to log through.
      * @return the compiler adapter
      * @throws BuildException if the compiler type could not be resolved into
      * a compiler adapter.
      */
     public static CompilerAdapter getCompiler(String compilerType, Task task)
         throws BuildException {
+        return getCompiler(compilerType, task, null);
+    }
+
+    /**
+     * Based on the parameter passed in, this method creates the necessary
+     * factory desired.
+     *
+     * The current mapping for compiler names are as follows:
+     * <ul><li>jikes = jikes compiler
+     * <li>classic, javac1.1, javac1.2 = the standard compiler from JDK
+     * 1.1/1.2
+     * <li>modern, javac1.3, javac1.4, javac1.5 = the compiler of JDK 1.3+
+     * <li>jvc, microsoft = the command line compiler from Microsoft's SDK
+     * for Java / Visual J++
+     * <li>kjc = the kopi compiler</li>
+     * <li>gcj = the gcj compiler from gcc</li>
+     * <li>sj, symantec = the Symantec Java compiler</li>
+     * <li><i>a fully qualified classname</i> = the name of a compiler
+     * adapter
+     * </ul>
+     *
+     * @param compilerType either the name of the desired compiler, or the
+     * full classname of the compiler's adapter.
+     * @param task a task to log through.
+     * @param classpath the classpath to use when looking up an
+     * adapter class
+     * @return the compiler adapter
+     * @throws BuildException if the compiler type could not be resolved into
+     * a compiler adapter.
+     * @since Ant 1.8.0
+     */
+    public static CompilerAdapter getCompiler(String compilerType, Task task,
+                                              Path classpath)
+        throws BuildException {
             boolean isClassicCompilerSupported = true;
             //as new versions of java come out, add them to this test
             if (!JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)
                 && !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
                 isClassicCompilerSupported = false;
             }
 
             if (compilerType.equalsIgnoreCase("jikes")) {
                 return new Jikes();
             }
             if (compilerType.equalsIgnoreCase("extJavac")) {
                 return new JavacExternal();
             }
             if (compilerType.equalsIgnoreCase("classic")
                 || compilerType.equalsIgnoreCase("javac1.1")
                 || compilerType.equalsIgnoreCase("javac1.2")) {
                 if (isClassicCompilerSupported) {
                     return new Javac12();
                 } else {
                     task.log("This version of java does "
                                              + "not support the classic "
                                              + "compiler; upgrading to modern",
                                              Project.MSG_WARN);
                     compilerType = "modern";
                 }
             }
             //on java<=1.3 the modern falls back to classic if it is not found
             //but on java>=1.4 we just bail out early
             if (compilerType.equalsIgnoreCase("modern")
                 || compilerType.equalsIgnoreCase("javac1.3")
                 || compilerType.equalsIgnoreCase("javac1.4")
                 || compilerType.equalsIgnoreCase("javac1.5")
                 || compilerType.equalsIgnoreCase("javac1.6")) {
                 // does the modern compiler exist?
                 if (doesModernCompilerExist()) {
                     return new Javac13();
                 } else {
                     if (isClassicCompilerSupported) {
                         task.log("Modern compiler not found - looking for "
                                  + "classic compiler", Project.MSG_WARN);
                         return new Javac12();
                     } else {
                         throw new BuildException("Unable to find a javac "
                                                  + "compiler;\n"
                                                  + MODERN_COMPILER
                                                  + " is not on the "
                                                  + "classpath.\n"
                                                  + "Perhaps JAVA_HOME does not"
                                                  + " point to the JDK.\n"
                                 + "It is currently set to \""
                                 + JavaEnvUtils.getJavaHome()
                                 + "\"");
                     }
                 }
             }
 
             if (compilerType.equalsIgnoreCase("jvc")
                 || compilerType.equalsIgnoreCase("microsoft")) {
                 return new Jvc();
             }
             if (compilerType.equalsIgnoreCase("kjc")) {
                 return new Kjc();
             }
             if (compilerType.equalsIgnoreCase("gcj")) {
                 return new Gcj();
             }
             if (compilerType.equalsIgnoreCase("sj")
                 || compilerType.equalsIgnoreCase("symantec")) {
                 return new Sj();
             }
-            return resolveClassName(compilerType);
+            return resolveClassName(compilerType,
+                                task.getProject().createClassLoader(classpath));
         }
 
     /**
      * query for the Modern compiler existing
      * @return true if classic os on the classpath
      */
     private static boolean doesModernCompilerExist() {
         try {
             Class.forName(MODERN_COMPILER);
             return true;
         } catch (ClassNotFoundException cnfe) {
             try {
                 ClassLoader cl = CompilerAdapterFactory.class.getClassLoader();
                 if (cl != null) {
                     cl.loadClass(MODERN_COMPILER);
                     return true;
                 }
             } catch (ClassNotFoundException cnfe2) {
                 // Ignore Exception
             }
         }
         return false;
     }
 
     /**
      * Tries to resolve the given classname into a compiler adapter.
      * Throws a fit if it can't.
      *
      * @param className The fully qualified classname to be created.
+     * @param loader the classloader to use
      * @throws BuildException This is the fit that is thrown if className
      * isn't an instance of CompilerAdapter.
      */
-    private static CompilerAdapter resolveClassName(String className)
+    private static CompilerAdapter resolveClassName(String className,
+                                                    ClassLoader loader)
         throws BuildException {
         return (CompilerAdapter) ClasspathUtils.newInstance(className,
+                loader != null ? loader :
                 CompilerAdapterFactory.class.getClassLoader(),
                 CompilerAdapter.class);
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java b/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
index bd5e3de9f..704636c42 100755
--- a/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/Javah.java
@@ -1,488 +1,499 @@
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
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.StringUtils;
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
     private FacadeTaskHelper facade = null;
     private Vector files = new Vector();
 
     /**
      * No arg constructor.
      */
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
      * Add a fileset.
      * @param fs the fileset to add.
      */
     public void addFileSet(FileSet fs) {
         files.add(fs);
     }
 
     /**
      * Names of the classes to process.
      * @return the array of classes.
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
 
         if (files.size() > 0) {
             for (Enumeration e = files.elements(); e.hasMoreElements();) {
                 FileSet fs = (FileSet) e.nextElement();
                 String[] includedClasses = fs.getDirectoryScanner(
                     getProject()).getIncludedFiles();
                 for (int i = 0; i < includedClasses.length; i++) {
                     String className =
                         includedClasses[i].replace('\\', '.').replace('/', '.')
                         .substring(0, includedClasses[i].length() - 6);
                     al.add(className);
                 }
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
      * @return the destination directory.
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
      * @return the classpath.
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
      * @return the bootclass path.
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
      * @return the destination file.
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
      * @return the force attribute.
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
      * @return the old attribute.
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
      * @return the stubs attribute.
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
      * @return the verbose attribute.
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
      * @return the arguments.
      * @since Ant 1.6.3
      */
     public String[] getCurrentArgs() {
         return facade.getArgs();
     }
 
     /**
+     * The classpath to use when loading the javah implementation
+     * if it is not a built-in one.
+     *
+     * @since Ant 1.8.0
+     */
+    public Path createImplementationClasspath() {
+        return facade.getImplementationClasspath(getProject());
+    }
+
+    /**
      * Execute the task
      *
      * @throws BuildException is there is a problem in the task execution.
      */
     public void execute() throws BuildException {
         // first off, make sure that we've got a srcdir
 
         if ((cls == null) && (classes.size() == 0) && (files.size() == 0)) {
             throw new BuildException("class attribute must be set!",
                 getLocation());
         }
 
         if ((cls != null) && (classes.size() > 0) && (files.size() > 0)) {
             throw new BuildException("set class attribute OR class element OR fileset, "
                 + "not 2 or more of them.", getLocation());
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
-                                           this);
+                                           this,
+                                           createImplementationClasspath());
         if (!ad.compile(this)) {
             throw new BuildException("compilation failed");
         }
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param cmd the command line.
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
             niceClassList.append("    ");
             niceClassList.append(c[i]);
             niceClassList.append(StringUtils.LINE_SEP);
         }
 
         StringBuffer prefix = new StringBuffer("Class");
         if (c.length > 1) {
             prefix.append("es");
         }
         prefix.append(" to be compiled:");
         prefix.append(StringUtils.LINE_SEP);
 
         log(prefix.toString() + niceClassList.toString(), Project.MSG_VERBOSE);
     }
 }
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/Native2Ascii.java b/src/main/org/apache/tools/ant/taskdefs/optional/Native2Ascii.java
index 1282cebe6..e2eb06be2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/Native2Ascii.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/Native2Ascii.java
@@ -1,300 +1,312 @@
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
 
 import java.io.File;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
 import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapterFactory;
 import org.apache.tools.ant.types.Mapper;
+import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.IdentityMapper;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
 
 /**
  * Converts files from native encodings to ASCII.
  *
  * @since Ant 1.2
  */
 public class Native2Ascii extends MatchingTask {
 
     private boolean reverse = false;  // convert from ascii back to native
     private String encoding = null;   // encoding to convert to/from
     private File srcDir = null;       // Where to find input files
     private File destDir = null;      // Where to put output files
     private String extension = null;  // Extension of output files if different
 
     private Mapper mapper;
     private FacadeTaskHelper facade = null;
 
     /** No args constructor */
     public Native2Ascii() {
         facade = new FacadeTaskHelper(Native2AsciiAdapterFactory.getDefault());
     }
 
     /**
      * Flag the conversion to run in the reverse sense,
      * that is Ascii to Native encoding.
      *
      * @param reverse True if the conversion is to be reversed,
      *                otherwise false;
      */
     public void setReverse(boolean reverse) {
         this.reverse = reverse;
     }
 
     /**
      * The value of the reverse attribute.
      * @return the reverse attribute.
      * @since Ant 1.6.3
      */
     public boolean getReverse() {
         return reverse;
     }
 
     /**
      * Set the encoding to translate to/from.
      * If unset, the default encoding for the JVM is used.
      *
      * @param encoding String containing the name of the Native
      *                 encoding to convert from or to.
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * The value of the encoding attribute.
      * @return the encoding attribute.
      * @since Ant 1.6.3
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Set the source directory in which to find files to convert.
      *
      * @param srcDir directory to find input file in.
      */
     public void setSrc(File srcDir) {
         this.srcDir = srcDir;
     }
 
 
     /**
      * Set the destination directory to place converted files into.
      *
      * @param destDir directory to place output file into.
      */
     public void setDest(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Set the extension which converted files should have.
      * If unset, files will not be renamed.
      *
      * @param ext File extension to use for converted files.
      */
     public void setExt(String ext) {
         this.extension = ext;
     }
 
     /**
      * Choose the implementation for this particular task.
      * @param impl the name of the implemenation
      * @since Ant 1.6.3
      */
     public void setImplementation(String impl) {
         if ("default".equals(impl)) {
             facade.setImplementation(Native2AsciiAdapterFactory.getDefault());
         } else {
             facade.setImplementation(impl);
         }
     }
 
     /**
      * Defines the FileNameMapper to use (nested mapper element).
      *
      * @return the mapper to use for file name translations.
      *
      * @throws BuildException if more than one mapper is defined.
      */
     public Mapper createMapper() throws BuildException {
         if (mapper != null) {
             throw new BuildException("Cannot define more than one mapper",
                                      getLocation());
         }
         mapper = new Mapper(getProject());
         return mapper;
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
      * Adds an implementation specific command-line argument.
      * @return a ImplementationSpecificArgument to be configured
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
+     * The classpath to use when loading the native2ascii
+     * implementation if it is not a built-in one.
+     *
+     * @since Ant 1.8.0
+     */
+    public Path createImplementationClasspath() {
+        return facade.getImplementationClasspath(getProject());
+    }
+
+    /**
      * Execute the task
      *
      * @throws BuildException is there is a problem in the task execution.
      */
     public void execute() throws BuildException {
 
         DirectoryScanner scanner = null; // Scanner to find our inputs
         String[] files;                  // list of files to process
 
         // default srcDir to basedir
         if (srcDir == null) {
             srcDir = getProject().resolveFile(".");
         }
 
         // Require destDir
         if (destDir == null) {
             throw new BuildException("The dest attribute must be set.");
         }
 
         // if src and dest dirs are the same, require the extension
         // to be set, so we don't stomp every file.  One could still
         // include a file with the same extension, but ....
         if (srcDir.equals(destDir) && extension == null && mapper == null) {
             throw new BuildException("The ext attribute or a mapper must be set if"
                                      + " src and dest dirs are the same.");
         }
 
         FileNameMapper m = null;
         if (mapper == null) {
             if (extension == null) {
                 m = new IdentityMapper();
             } else {
                 m = new ExtMapper();
             }
         } else {
             m = mapper.getImplementation();
         }
 
         scanner = getDirectoryScanner(srcDir);
         files = scanner.getIncludedFiles();
         SourceFileScanner sfs = new SourceFileScanner(this);
         files = sfs.restrict(files, srcDir, destDir, m);
         int count = files.length;
         if (count == 0) {
             return;
         }
         String message = "Converting " + count + " file"
             + (count != 1 ? "s" : "") + " from ";
         log(message + srcDir + " to " + destDir);
         for (int i = 0; i < files.length; i++) {
             convert(files[i], m.mapFileName(files[i])[0]);
         }
     }
 
     /**
      * Convert a single file.
      *
      * @param srcName name of the input file.
      * @param destName name of the input file.
      */
     private void convert(String srcName, String destName)
         throws BuildException {
         File srcFile;                         // File to convert
         File destFile;                        // where to put the results
 
         // Build the full file names
         srcFile = new File(srcDir, srcName);
         destFile = new File(destDir, destName);
 
         // Make sure we're not about to clobber something
         if (srcFile.equals(destFile)) {
             throw new BuildException("file " + srcFile
                                      + " would overwrite its self");
         }
 
         // Make intermediate directories if needed
         // XXX JDK 1.1 doesn't have File.getParentFile,
         String parentName = destFile.getParent();
         if (parentName != null) {
             File parentFile = new File(parentName);
 
             if ((!parentFile.exists()) && (!parentFile.mkdirs())) {
                 throw new BuildException("cannot create parent directory "
                                          + parentName);
             }
         }
 
         log("converting " + srcName, Project.MSG_VERBOSE);
         Native2AsciiAdapter ad =
             Native2AsciiAdapterFactory.getAdapter(facade.getImplementation(),
-                                                  this);
+                                                  this,
+                                                  createImplementationClasspath());
         if (!ad.convert(this, srcFile, destFile)) {
             throw new BuildException("conversion failed");
         }
     }
 
     /**
      * Returns the (implementation specific) settings given as nested
      * arg elements.
      * @return the arguments.
      * @since Ant 1.6.3
      */
     public String[] getCurrentArgs() {
         return facade.getArgs();
     }
 
     private class ExtMapper implements FileNameMapper {
 
         public void setFrom(String s) {
         }
         public void setTo(String s) {
         }
 
         public String[] mapFileName(String fileName) {
             int lastDot = fileName.lastIndexOf('.');
             if (lastDot >= 0) {
                 return new String[] {fileName.substring(0, lastDot)
                                          + extension};
             } else {
                 return new String[] {fileName + extension};
             }
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/javah/JavahAdapterFactory.java b/src/main/org/apache/tools/ant/taskdefs/optional/javah/JavahAdapterFactory.java
index ad3aa87b4..8fc55f0f4 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/javah/JavahAdapterFactory.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/javah/JavahAdapterFactory.java
@@ -1,88 +1,114 @@
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
 package org.apache.tools.ant.taskdefs.optional.javah;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ProjectComponent;
+import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.ClasspathUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  * Creates the JavahAdapter based on the user choice and
  * potentially the VM vendor.
  *
  * @since Ant 1.6.3
  */
 // CheckStyle:HideUtilityClassConstructorCheck OFF (bc)
 public class JavahAdapterFactory {
 
     /**
      * Determines the default choice of adapter based on the VM
      * vendor.
      *
      * @return the default choice of adapter based on the VM
      * vendor
      */
     public static String getDefault() {
         if (JavaEnvUtils.isKaffe()) {
             return Kaffeh.IMPLEMENTATION_NAME;
         }
         return SunJavah.IMPLEMENTATION_NAME;
     }
 
     /**
      * Creates the JavahAdapter based on the user choice and
      * potentially the VM vendor.
      *
      * @param choice the user choice (if any).
      * @param log a ProjectComponent instance used to access Ant's
      * logging system.
      * @return The adapter to use.
      * @throws BuildException if there is an error.
      */
     public static JavahAdapter getAdapter(String choice,
                                           ProjectComponent log)
         throws BuildException {
+        return getAdapter(choice, log, null);
+    }
+
+    /**
+     * Creates the JavahAdapter based on the user choice and
+     * potentially the VM vendor.
+     *
+     * @param choice the user choice (if any).
+     * @param log a ProjectComponent instance used to access Ant's
+     * logging system.
+     * @param classpath the classpath to use when looking up an
+     * adapter class
+     * @return The adapter to use.
+     * @throws BuildException if there is an error.
+     * @since Ant 1.8.0
+     */
+    public static JavahAdapter getAdapter(String choice,
+                                          ProjectComponent log,
+                                          Path classpath)
+        throws BuildException {
         if ((JavaEnvUtils.isKaffe() && choice == null)
             || Kaffeh.IMPLEMENTATION_NAME.equals(choice)) {
             return new Kaffeh();
         } else if (SunJavah.IMPLEMENTATION_NAME.equals(choice)) {
             return new SunJavah();
         } else if (choice != null) {
-            return resolveClassName(choice);
+            return resolveClassName(choice,
+                                    log.getProject()
+                                    .createClassLoader(classpath));
         }
 
         // This default has been good enough until Ant 1.6.3, so stick
         // with it
         return new SunJavah();
     }
 
     /**
      * Tries to resolve the given classname into a javah adapter.
      * Throws a fit if it can't.
      *
      * @param className The fully qualified classname to be created.
+     * @param loader the classloader to use
      * @throws BuildException This is the fit that is thrown if className
      * isn't an instance of JavahAdapter.
      */
-    private static JavahAdapter resolveClassName(String className)
+    private static JavahAdapter resolveClassName(String className,
+                                                 ClassLoader loader)
             throws BuildException {
         return (JavahAdapter) ClasspathUtils.newInstance(className,
+                loader != null ? loader :
                 JavahAdapterFactory.class.getClassLoader(), JavahAdapter.class);
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/native2ascii/Native2AsciiAdapterFactory.java b/src/main/org/apache/tools/ant/taskdefs/optional/native2ascii/Native2AsciiAdapterFactory.java
index abbaec61d..e19ce7f36 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/native2ascii/Native2AsciiAdapterFactory.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/native2ascii/Native2AsciiAdapterFactory.java
@@ -1,89 +1,115 @@
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
 package org.apache.tools.ant.taskdefs.optional.native2ascii;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ProjectComponent;
+import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.ClasspathUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  * Creates the Native2AsciiAdapter based on the user choice and
  * potentially the VM vendor.
  *
  * @since Ant 1.6.3
  */
 // CheckStyle:HideUtilityClassConstructorCheck OFF (bc)
 public class Native2AsciiAdapterFactory {
 
     /**
      * Determines the default choice of adapter based on the VM
      * vendor.
      *
      * @return the default choice of adapter based on the VM
      * vendor
      */
     public static String getDefault() {
         if (JavaEnvUtils.isKaffe()) {
             return KaffeNative2Ascii.IMPLEMENTATION_NAME;
         }
         return SunNative2Ascii.IMPLEMENTATION_NAME;
     }
 
     /**
-     * Creates the Native2AsciiAdapter based on the user choice and *
+     * Creates the Native2AsciiAdapter based on the user choice and
      * potentially the VM vendor.
      *
      * @param choice the user choice (if any).
      * @param log a ProjectComponent instance used to access Ant's
      * logging system.
      * @return The adapter to use.
      * @throws BuildException if there was a problem.
      */
     public static Native2AsciiAdapter getAdapter(String choice,
                                                  ProjectComponent log)
         throws BuildException {
+        return getAdapter(choice, log, null);
+    }
+
+    /**
+     * Creates the Native2AsciiAdapter based on the user choice and
+     * potentially the VM vendor.
+     *
+     * @param choice the user choice (if any).
+     * @param log a ProjectComponent instance used to access Ant's
+     * logging system.
+     * @param classpath the classpath to use when looking up an
+     * adapter class
+     * @return The adapter to use.
+     * @throws BuildException if there was a problem.
+     * @since Ant 1.8.0
+     */
+    public static Native2AsciiAdapter getAdapter(String choice,
+                                                 ProjectComponent log,
+                                                 Path classpath)
+        throws BuildException {
         if ((JavaEnvUtils.isKaffe() && choice == null)
             || KaffeNative2Ascii.IMPLEMENTATION_NAME.equals(choice)) {
             return new KaffeNative2Ascii();
         } else if (SunNative2Ascii.IMPLEMENTATION_NAME.equals(choice)) {
             return new SunNative2Ascii();
         } else if (choice != null) {
-            return resolveClassName(choice);
+            return resolveClassName(choice,
+                                    log.getProject()
+                                    .createClassLoader(classpath));
         }
 
         // This default has been good enough until Ant 1.6.3, so stick
         // with it
         return new SunNative2Ascii();
     }
 
     /**
      * Tries to resolve the given classname into a native2ascii adapter.
      * Throws a fit if it can't.
      *
      * @param className The fully qualified classname to be created.
+     * @param loader the classloader to use
      * @throws BuildException This is the fit that is thrown if className
      * isn't an instance of Native2AsciiAdapter.
      */
-    private static Native2AsciiAdapter resolveClassName(String className)
+    private static Native2AsciiAdapter resolveClassName(String className,
+                                                        ClassLoader loader)
         throws BuildException {
         return (Native2AsciiAdapter) ClasspathUtils.newInstance(className,
+            loader != null ? loader :
             Native2AsciiAdapterFactory.class.getClassLoader(),
             Native2AsciiAdapter.class);
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/rmic/RmicAdapterFactory.java b/src/main/org/apache/tools/ant/taskdefs/rmic/RmicAdapterFactory.java
index 275b916d5..fc625b70f 100644
--- a/src/main/org/apache/tools/ant/taskdefs/rmic/RmicAdapterFactory.java
+++ b/src/main/org/apache/tools/ant/taskdefs/rmic/RmicAdapterFactory.java
@@ -1,106 +1,140 @@
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
 
 package org.apache.tools.ant.taskdefs.rmic;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Task;
+import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.ClasspathUtils;
 
 import java.util.Locale;
 
 
 /**
  * Creates the necessary rmic adapter, given basic criteria.
  *
  * @since 1.4
  */
 public final class RmicAdapterFactory {
     /** The error message to be used when the compiler cannot be found. */
     public static final String ERROR_UNKNOWN_COMPILER = "Class not found: ";
 
     /** The error message to be used when the class is not an rmic adapter. */
     public static final String ERROR_NOT_RMIC_ADAPTER = "Class of unexpected Type: ";
 
     /** If the compiler has this name use a default compiler. */
     public static final String DEFAULT_COMPILER = "default";
 
     /** This is a singleton -- can't create instances!! */
     private RmicAdapterFactory() {
     }
 
     /**
      * Based on the parameter passed in, this method creates the necessary
      * factory desired.
      *
      * <p>The current mapping for rmic names are as follows:</p>
      * <ul><li>sun = SUN's rmic
      * <li>kaffe = Kaffe's rmic
      * <li><i>a fully qualified classname</i> = the name of a rmic
      * adapter
      * <li>weblogic = weblogic compiler
      * <li>forking = Sun's RMIC by forking a new JVM
      * </ul>
      *
      * @param rmicType either the name of the desired rmic, or the
      * full classname of the rmic's adapter.
      * @param task a task to log through.
      * @return the compiler adapter
      * @throws BuildException if the rmic type could not be resolved into
      * a rmic adapter.
      */
     public static RmicAdapter getRmic(String rmicType, Task task)
         throws BuildException {
+        return getRmic(rmicType, task, null);
+    }
+
+    /**
+     * Based on the parameter passed in, this method creates the necessary
+     * factory desired.
+     *
+     * <p>The current mapping for rmic names are as follows:</p>
+     * <ul><li>sun = SUN's rmic
+     * <li>kaffe = Kaffe's rmic
+     * <li><i>a fully qualified classname</i> = the name of a rmic
+     * adapter
+     * <li>weblogic = weblogic compiler
+     * <li>forking = Sun's RMIC by forking a new JVM
+     * </ul>
+     *
+     * @param rmicType either the name of the desired rmic, or the
+     * full classname of the rmic's adapter.
+     * @param task a task to log through.
+     * @param classpath the classpath to use when looking up an
+     * adapter class
+     * @return the compiler adapter
+     * @throws BuildException if the rmic type could not be resolved into
+     * a rmic adapter.
+     * @since Ant 1.8.0
+     */
+    public static RmicAdapter getRmic(String rmicType, Task task,
+                                      Path classpath)
+        throws BuildException {
         //convert to lower case in the English locale,
         String compiler = rmicType.toLowerCase(Locale.ENGLISH);
 
         //handle default specially by choosing the sun or kaffe compiler
         if (DEFAULT_COMPILER.equals(compiler) || compiler.length() == 0) {
             compiler = KaffeRmic.isAvailable()
                 ? KaffeRmic.COMPILER_NAME
                 : SunRmic.COMPILER_NAME;
         }
         if (SunRmic.COMPILER_NAME.equals(compiler)) {
             return new SunRmic();
         } else if (KaffeRmic.COMPILER_NAME.equals(compiler)) {
             return new KaffeRmic();
         } else if (WLRmic.COMPILER_NAME.equals(compiler)) {
             return new WLRmic();
         } else if (ForkingSunRmic.COMPILER_NAME.equals(compiler)) {
             return new ForkingSunRmic();
         } else if (XNewRmic.COMPILER_NAME.equals(compiler)) {
             return new XNewRmic();
         }
         //no match? ask for the non-lower-cased type
-        return resolveClassName(rmicType);
+        return resolveClassName(rmicType,
+                                task.getProject().createClassLoader(classpath));
     }
 
     /**
      * Tries to resolve the given classname into a rmic adapter.
      * Throws a fit if it can't.
      *
      * @param className The fully qualified classname to be created.
+     * @param loader the classloader to use
      * @throws BuildException This is the fit that is thrown if className
      * isn't an instance of RmicAdapter.
      */
-    private static RmicAdapter resolveClassName(String className)
+    private static RmicAdapter resolveClassName(String className,
+                                                ClassLoader loader)
             throws BuildException {
         return (RmicAdapter) ClasspathUtils.newInstance(className,
+                loader != null ? loader :
                 RmicAdapterFactory.class.getClassLoader(), RmicAdapter.class);
     }
 }
diff --git a/src/main/org/apache/tools/ant/util/facade/FacadeTaskHelper.java b/src/main/org/apache/tools/ant/util/facade/FacadeTaskHelper.java
index 6f3bd60c6..55a3f18a3 100644
--- a/src/main/org/apache/tools/ant/util/facade/FacadeTaskHelper.java
+++ b/src/main/org/apache/tools/ant/util/facade/FacadeTaskHelper.java
@@ -1,144 +1,165 @@
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
 
 package org.apache.tools.ant.util.facade;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.types.Path;
 
 /**
  * Helper class for facade implementations - encapsulates treatment of
  * explicit implementation choices, magic properties and
  * implementation specific command line arguments.
  *
  *
  * @since Ant 1.5
  */
 public class FacadeTaskHelper {
 
     /**
      * Command line arguments.
      */
     private List args = new ArrayList();
 
     /**
      * The explicitly chosen implementation.
      */
     private String userChoice;
 
     /**
      * The magic property to consult.
      */
     private String magicValue;
 
     /**
      * The default value.
      */
     private String defaultValue;
 
     /**
+     * User specified path used as classpath when loading the implementation.
+     */
+    private Path implementationClasspath;
+
+    /**
      * @param defaultValue The default value for the implementation.
      * Must not be null.
      */
     public FacadeTaskHelper(String defaultValue) {
         this(defaultValue, null);
     }
 
     /**
      * @param defaultValue The default value for the implementation.
      * Must not be null.
      * @param magicValue the value of a magic property that may hold a user.
      * choice.  May be null.
      */
     public FacadeTaskHelper(String defaultValue, String magicValue) {
         this.defaultValue = defaultValue;
         this.magicValue = magicValue;
     }
 
     /**
      * Used to set the value of the magic property.
      * @param magicValue the value of a magic property that may hold a user.
      */
     public void setMagicValue(String magicValue) {
         this.magicValue = magicValue;
     }
 
     /**
      * Used for explicit user choices.
      * @param userChoice the explicitly chosen implementation.
      */
     public void setImplementation(String userChoice) {
         this.userChoice = userChoice;
     }
 
     /**
      * Retrieves the implementation.
      * @return the implementation.
      */
     public String getImplementation() {
         return userChoice != null ? userChoice
                                   : (magicValue != null ? magicValue
                                                         : defaultValue);
     }
 
     /**
      * Retrieves the explicit user choice.
      * @return the explicit user choice.
      */
     public String getExplicitChoice() {
         return userChoice;
     }
 
     /**
      * Command line argument.
      * @param arg an argument to add.
      */
     public void addImplementationArgument(ImplementationSpecificArgument arg) {
         args.add(arg);
     }
 
     /**
      * Retrieves the command line arguments enabled for the current
      * facade implementation.
      * @return an array of command line arguements.
      */
     public String[] getArgs() {
         List tmp = new ArrayList(args.size());
         for (Iterator e = args.iterator(); e.hasNext();) {
             ImplementationSpecificArgument arg =
                 ((ImplementationSpecificArgument) e.next());
             String[] curr = arg.getParts(getImplementation());
             for (int i = 0; i < curr.length; i++) {
                 tmp.add(curr[i]);
             }
         }
         String[] res = new String[tmp.size()];
         return (String[]) tmp.toArray(res);
     }
 
     /**
      * Tests whether the implementation has been chosen by the user
      * (either via a magic property or explicitly.
      * @return true if magic or user choice has be set.
      * @since Ant 1.5.2
      */
     public boolean hasBeenSet() {
         return userChoice != null || magicValue != null;
     }
+
+    /**
+     * The classpath to use when loading the implementation.
+     *
+     * @param project the current project
+     * @return a Path instance that may be appended to
+     * @since Ant 1.8.0
+     */
+    public Path getImplementationClasspath(Project project) {
+        if (implementationClasspath == null) {
+            implementationClasspath = new Path(project);
+        }
+        return implementationClasspath;
+    }
 }
diff --git a/src/tests/antunit/taskdefs/javac-test.xml b/src/tests/antunit/taskdefs/javac-test.xml
index 3180dcb72..55e794be6 100644
--- a/src/tests/antunit/taskdefs/javac-test.xml
+++ b/src/tests/antunit/taskdefs/javac-test.xml
@@ -1,122 +1,158 @@
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
   <import file="../antunit-base.xml" />
 
   <property name="javac-dir" location="${output}/javac-dir"/>
   <property name="build-dir" location="${javac-dir}/build"/>
   
   <target name="test-includeDestClasses">
     <property name="DATE" value="09/10/1999 4:30 pm"/>
     <delete dir="${javac-dir}/src"/>
     <mkdir dir="${javac-dir}/src"/>
     <echo file="${javac-dir}/src/A.java">
       public class A { B b;}
     </echo>
     <echo file="${javac-dir}/src/B.java">
       public class B { }
     </echo>
     <delete dir="${javac-dir}/classes" quiet="yes"/>
     <mkdir dir="${javac-dir}/classes"/>
     <javac srcdir="${javac-dir}/src" destdir="${javac-dir}/classes"/>
     <touch file="${javac-dir}/src/B.java" datetime="${DATE}"/>
     <touch file="${javac-dir}/classes/B.class" datetime="${DATE}"/>
     <!-- following should not update B.class -->
     <delete quiet="yes" file="${javac-dir}/classes/A.class"/>
     <javac srcdir="${javac-dir}/src" destdir="${javac-dir}/classes"/>
     <au:assertTrue>
       <isfileselected file="${javac-dir}/classes/B.class">
         <date datetime="${DATE}" when="equal"/>
       </isfileselected>
     </au:assertTrue>
     <!-- following should update B.class -->
     <delete quiet="yes" file="${javac-dir}/classes/A.class"/>
     <javac srcdir="${javac-dir}/src"
            destdir="${javac-dir}/classes" includeDestClasses="no"/>
     <au:assertFalse>
       <isfileselected file="${javac-dir}/classes/B.class">
         <date datetime="${DATE}" when="equal"/>
       </isfileselected>
     </au:assertFalse>
   </target>
 
   <target name="test-updated-property">
     <delete quiet="yes" dir="${build-dir}"/>
     <mkdir dir="${build-dir}"/>
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}"
            updatedProperty="classes-updated"/>
     <au:assertTrue>
       <equals arg1="${classes-updated}" arg2="true"/>
     </au:assertTrue>
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}"
            updatedProperty="classes-updated-2"/>
     <au:assertFalse>
       <isset property="classes-updated-2"/>
     </au:assertFalse>
   </target>
 
   <target name="test-error-property">
     <delete quiet="yes" dir="${build-dir}"/>
     <mkdir dir="${build-dir}"/>
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}"
            failOnError="false"
            errorProperty="compile-failed"/>
     <au:assertTrue>
       <equals arg1="${compile-failed}" arg2="${compile-failed}"/>
     </au:assertTrue>
     <javac srcdir="javac-dir/bad-src" destdir="${build-dir}"
            failOnError="false"
            errorProperty="compile-failed"/>
     <au:assertTrue>
       <equals arg1="${compile-failed}" arg2="true"/>
     </au:assertTrue>
   </target>
 
   <target name="testPackageInfoJava"
           description="https://issues.apache.org/bugzilla/show_bug.cgi?id=43114">
     <mkdir dir="${javac-dir}/src/a"/>
     <mkdir dir="${build-dir}"/>
     <echo file="${javac-dir}/src/a/package-info.java"><![CDATA[
 /**
  * Some test javadocs at the package level.
  */
 ]]></echo>
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}"
            updatedProperty="first-pass"/>
     <au:assertPropertyEquals name="first-pass" value="true"/>
 
     <!-- no changes, shouldn't recompile, the initial bug -->
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}"
            updatedProperty="second-pass"/>
     <au:assertFalse>
       <isset property="second-pass"/>
     </au:assertFalse>
     <sleep seconds="2"/>
 
     <!-- change package-info.java but make containing target dir even
          more recent - the regression in Ant 1.7.1 -->
     <touch file="${javac-dir}/src/a/package-info.java"/>
     <sleep seconds="2"/>
     <touch>
       <file file="${build-dir}/a"/>
     </touch>
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}"
            updatedProperty="third-pass"/>
     <au:assertPropertyEquals name="third-pass" value="true"/>
   </target>
+
+  <target name="-create-javac-adapter">
+    <property name="adapter.dir" location="${output}/adapter"/>
+    <mkdir dir="${input}/org/example"/>
+    <echo file="${input}/org/example/Adapter.java"><![CDATA[
+package org.example;
+import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
+import org.apache.tools.ant.taskdefs.Javac;
+
+public class Adapter implements CompilerAdapter {
+    public void setJavac(Javac attributes) {}
+    public boolean execute() {
+        System.err.println("adapter called");
+        return true;
+    }
+}]]></echo>
+    <mkdir dir="${adapter.dir}"/>
+    <javac srcdir="${input}" destdir="${adapter.dir}"/>
+  </target>
+
+  <target name="testCompilerNotFound" depends="-create-javac-adapter">
+    <au:expectfailure>
+      <javac srcdir="${input}" destdir="${output}"
+             compiler="org.example.Adapter"/>
+    </au:expectfailure>
+    <au:assertLogDoesntContain text="adapter called"/>
+  </target>
+
+  <target name="testCompilerClasspath" depends="-create-javac-adapter"
+          description="https://issues.apache.org/bugzilla/show_bug.cgi?id=11143">
+    <javac srcdir="${input}" destdir="${output}"
+           compiler="org.example.Adapter">
+      <compilerclasspath location="${adapter.dir}"/>
+    </javac>
+    <au:assertLogContains text="adapter called"/>
+  </target>
 </project>
diff --git a/src/tests/antunit/taskdefs/optional/javah-test.xml b/src/tests/antunit/taskdefs/optional/javah-test.xml
new file mode 100644
index 000000000..9779ea4bc
--- /dev/null
+++ b/src/tests/antunit/taskdefs/optional/javah-test.xml
@@ -0,0 +1,55 @@
+<?xml version="1.0"?>
+<!--
+  Licensed to the Apache Software Foundation (ASF) under one or more
+  contributor license agreements.  See the NOTICE file distributed with
+  this work for additional information regarding copyright ownership.
+  The ASF licenses this file to You under the Apache License, Version 2.0
+  (the "License"); you may not use this file except in compliance with
+  the License.  You may obtain a copy of the License at
+
+      http://www.apache.org/licenses/LICENSE-2.0
+
+  Unless required by applicable law or agreed to in writing, software
+  distributed under the License is distributed on an "AS IS" BASIS,
+  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+  See the License for the specific language governing permissions and
+  limitations under the License.
+-->
+<project default="antunit" xmlns:au="antlib:org.apache.ant.antunit">
+  <import file="../../antunit-base.xml" />
+
+  <target name="-create-javah-adapter">
+    <property name="adapter.dir" location="${output}/adapter"/>
+    <mkdir dir="${input}/org/example"/>
+    <echo file="${input}/org/example/Adapter.java"><![CDATA[
+package org.example;
+import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter;
+import org.apache.tools.ant.taskdefs.optional.Javah;
+
+public class Adapter implements JavahAdapter {
+    public boolean compile(Javah javah) {
+        System.err.println("adapter called");
+        return true;
+    }
+}]]></echo>
+    <mkdir dir="${adapter.dir}"/>
+    <javac srcdir="${input}" destdir="${adapter.dir}"/>
+  </target>
+
+  <target name="testAdapterNotFound" depends="-create-javah-adapter">
+    <au:expectfailure>
+      <javah class="org.example.Adapter" destdir="${output}"
+             implementation="org.example.Adapter"/>
+    </au:expectfailure>
+    <au:assertLogDoesntContain text="adapter called"/>
+  </target>
+
+  <target name="testImplementationClasspath" depends="-create-javah-adapter"
+          description="https://issues.apache.org/bugzilla/show_bug.cgi?id=11143">
+    <javah class="org.example.Adapter" destdir="${output}"
+           implementation="org.example.Adapter">
+      <implementationclasspath location="${adapter.dir}"/>
+    </javah>
+    <au:assertLogContains text="adapter called"/>
+  </target>
+</project>
diff --git a/src/tests/antunit/taskdefs/optional/native2ascci-test.xml b/src/tests/antunit/taskdefs/optional/native2ascci-test.xml
new file mode 100644
index 000000000..1bd192169
--- /dev/null
+++ b/src/tests/antunit/taskdefs/optional/native2ascci-test.xml
@@ -0,0 +1,56 @@
+<?xml version="1.0"?>
+<!--
+  Licensed to the Apache Software Foundation (ASF) under one or more
+  contributor license agreements.  See the NOTICE file distributed with
+  this work for additional information regarding copyright ownership.
+  The ASF licenses this file to You under the Apache License, Version 2.0
+  (the "License"); you may not use this file except in compliance with
+  the License.  You may obtain a copy of the License at
+
+      http://www.apache.org/licenses/LICENSE-2.0
+
+  Unless required by applicable law or agreed to in writing, software
+  distributed under the License is distributed on an "AS IS" BASIS,
+  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+  See the License for the specific language governing permissions and
+  limitations under the License.
+-->
+<project default="antunit" xmlns:au="antlib:org.apache.ant.antunit">
+  <import file="../../antunit-base.xml" />
+
+  <target name="-create-native2ascii-adapter">
+    <property name="adapter.dir" location="${output}/adapter"/>
+    <mkdir dir="${input}/org/example"/>
+    <echo file="${input}/org/example/Adapter.java"><![CDATA[
+package org.example;
+import java.io.File;
+import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
+import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
+
+public class Adapter implements Native2AsciiAdapter {
+    public boolean convert(Native2Ascii native2ascii, File f1, File f2) {
+        System.err.println("adapter called");
+        return true;
+    }
+}]]></echo>
+    <mkdir dir="${adapter.dir}"/>
+    <javac srcdir="${input}" destdir="${adapter.dir}"/>
+  </target>
+
+  <target name="testAdapterNotFound" depends="-create-native2ascii-adapter">
+    <au:expectfailure>
+      <native2ascii src="${input}" dest="${output}" includes="**/*.java"
+                    implementation="org.example.Adapter"/>
+    </au:expectfailure>
+    <au:assertLogDoesntContain text="adapter called"/>
+  </target>
+
+  <target name="testImplementationClasspath" depends="-create-native2ascii-adapter"
+          description="https://issues.apache.org/bugzilla/show_bug.cgi?id=11143">
+    <native2ascii src="${input}" dest="${output}" includes="**/*.java"
+                  implementation="org.example.Adapter">
+      <implementationclasspath location="${adapter.dir}"/>
+    </native2ascii>
+    <au:assertLogContains text="adapter called"/>
+  </target>
+</project>
diff --git a/src/tests/antunit/taskdefs/rmic-test.xml b/src/tests/antunit/taskdefs/rmic-test.xml
new file mode 100644
index 000000000..f0cc36440
--- /dev/null
+++ b/src/tests/antunit/taskdefs/rmic-test.xml
@@ -0,0 +1,69 @@
+<?xml version="1.0"?>
+<!--
+  Licensed to the Apache Software Foundation (ASF) under one or more
+  contributor license agreements.  See the NOTICE file distributed with
+  this work for additional information regarding copyright ownership.
+  The ASF licenses this file to You under the Apache License, Version 2.0
+  (the "License"); you may not use this file except in compliance with
+  the License.  You may obtain a copy of the License at
+
+      http://www.apache.org/licenses/LICENSE-2.0
+
+  Unless required by applicable law or agreed to in writing, software
+  distributed under the License is distributed on an "AS IS" BASIS,
+  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+  See the License for the specific language governing permissions and
+  limitations under the License.
+-->
+<project default="antunit" xmlns:au="antlib:org.apache.ant.antunit">
+  <import file="../antunit-base.xml" />
+
+  <target name="-create-rmic-adapter">
+    <property name="adapter.dir" location="${output}/adapter"/>
+    <mkdir dir="${input}/org/example"/>
+    <echo file="${input}/org/example/Adapter.java"><![CDATA[
+package org.example;
+import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
+import org.apache.tools.ant.taskdefs.Rmic;
+import org.apache.tools.ant.types.Path;
+import org.apache.tools.ant.util.FileNameMapper;
+import org.apache.tools.ant.util.GlobPatternMapper;
+
+public class Adapter implements RmicAdapter {
+    public void setRmic(Rmic attributes) {}
+    public boolean execute() {
+        System.err.println("adapter called");
+        return true;
+    }
+    public FileNameMapper getMapper() {
+        GlobPatternMapper m = new GlobPatternMapper();
+        m.setFrom("*.class");
+        m.setTo("*_foo.class");
+        return m;
+    }
+
+    public Path getClasspath() {
+        return new Path(null);
+    }
+}]]></echo>
+    <mkdir dir="${adapter.dir}"/>
+    <javac srcdir="${input}" destdir="${adapter.dir}"/>
+  </target>
+
+  <target name="testCompilerNotFound" depends="-create-rmic-adapter">
+    <au:expectfailure>
+      <rmic base="${adapter.dir}" includes="**/*.class"
+            compiler="org.example.Adapter"/>
+    </au:expectfailure>
+    <au:assertLogDoesntContain text="adapter called"/>
+  </target>
+
+  <target name="testCompilerClasspath" depends="-create-rmic-adapter"
+          description="https://issues.apache.org/bugzilla/show_bug.cgi?id=11143">
+    <rmic base="${adapter.dir}" includes="**/*.class"
+          compiler="org.example.Adapter">
+      <compilerclasspath location="${adapter.dir}"/>
+    </rmic>
+    <au:assertLogContains text="adapter called"/>
+  </target>
+</project>
