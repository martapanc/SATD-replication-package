diff --git a/WHATSNEW b/WHATSNEW
index 0d1254dcd..d0b4d3bda 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1422 +1,1426 @@
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
    Bugzilla Report 39492.
 
  * AntClassLoader didn't set the proper CodeSource for loaded classes.
    Bugzilla Report 20174.
 
  * AntClassLoader.getResourceAsStream would return streams to
    resources it didn't return with getResource and to classes it
    failed to load.
    Bugzilla Report 44103.
 
+ * Logging exceptions without a message would cause a
+   NullPointerException.
+   Bugzilla Report 47623.
+
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
 
  * <javac>, <rmic>, <javah> and <native2ascii> now provide a nested
    element to specify a classpath that will be used when loading the
    task's (compiler) adapter class.
    Bugzilla Issue 11143.
 
  * A new subclass org.apache.tools.ant.loader.AntClassLoader5 of
    AntClassLoader has been added which overrides getResources
    which became non-final in ClassLoader with Java5+ so
    this method now behaves as expected.
    The new subclass will be used by Ant internally if it is available
    and Ant is running on Java5 or more recent.
    Bugzilla Issue 46752.
 
  * a new attributes can chose a different request method than GET for
    the http condition.
    Bugzilla Report 30244
 
  * <splash> now supports a configurable display text and a regular
    expression based way to determine progress based on logged messages.
    Bugzilla Report 39957.
 
  * the number of retries on error in <get> is now configurable.  <get>
    can be told to not download files that already exist locally.
    Bugzilla Report 40058.
 
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
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 52c9746a2..8487dbc0a 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1174,1249 +1174,1252 @@ public class Project implements ResourceFactory {
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
      * Does the project know this reference?
      *
      * @since Ant 1.8.0
      */
     public boolean hasReference(String key) {
         return references.containsKey(key);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].buildStarted(event);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].buildFinished(event);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             if (currListeners[i] instanceof SubBuildListener) {
                 ((SubBuildListener) currListeners[i]).subBuildStarted(event);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             if (currListeners[i] instanceof SubBuildListener) {
                 ((SubBuildListener) currListeners[i]).subBuildFinished(event);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].targetStarted(event);
         }
 
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].targetFinished(event);
         }
 
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].taskStarted(event);
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
         BuildListener[] currListeners = listeners;
         for (int i = 0; i < currListeners.length; i++) {
             currListeners[i].taskFinished(event);
         }
 
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
 
+        if (message == null) {
+            message = String.valueOf(message);
+        }
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
             BuildListener[] currListeners = listeners;
             for (int i = 0; i < currListeners.length; i++) {
                 currListeners[i].messageLogged(event);
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
diff --git a/src/tests/junit/org/apache/tools/ant/ProjectTest.java b/src/tests/junit/org/apache/tools/ant/ProjectTest.java
index ba741823a..fd4cff15d 100644
--- a/src/tests/junit/org/apache/tools/ant/ProjectTest.java
+++ b/src/tests/junit/org/apache/tools/ant/ProjectTest.java
@@ -1,327 +1,335 @@
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
 
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.input.PropertyFileInputHandler;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.*;
 
 import java.io.File;
 import junit.framework.TestCase;
 
 
 /**
  * Very limited test class for Project. Waiting to be extended.
  *
  */
 public class ProjectTest extends TestCase {
 
     private Project p;
     private String root;
     private MockBuildListener mbl;
 
     public ProjectTest(String name) {
         super(name);
     }
 
     public void setUp() {
         p = new Project();
         p.init();
         root = new File(File.separator).getAbsolutePath().toUpperCase();
         mbl = new MockBuildListener(p);
     }
 
     public void testDataTypes() throws BuildException {
         assertNull("dummy is not a known data type",
                    p.createDataType("dummy"));
         Object o = p.createDataType("fileset");
         assertNotNull("fileset is a known type", o);
         assertTrue("fileset creates FileSet", o instanceof FileSet);
         assertTrue("PatternSet",
                p.createDataType("patternset") instanceof PatternSet);
         assertTrue("Path", p.createDataType("path") instanceof Path);
     }
 
     /**
      * This test has been a starting point for moving the code to FileUtils.
      */
     public void testResolveFile() {
         if (Os.isFamily("netware") || Os.isFamily("dos")) {
             assertEqualsIgnoreDriveCase(localize(File.separator),
                 p.resolveFile("/", null).getPath());
             assertEqualsIgnoreDriveCase(localize(File.separator),
                 p.resolveFile("\\", null).getPath());
             /*
              * throw in drive letters
              */
             String driveSpec = "C:";
             String driveSpecLower = "c:";
             
             assertEqualsIgnoreDriveCase(driveSpecLower + "\\",
                          p.resolveFile(driveSpec + "/", null).getPath());
             assertEqualsIgnoreDriveCase(driveSpecLower + "\\",
                          p.resolveFile(driveSpec + "\\", null).getPath());
             assertEqualsIgnoreDriveCase(driveSpecLower + "\\",
                          p.resolveFile(driveSpecLower + "/", null).getPath());
             assertEqualsIgnoreDriveCase(driveSpecLower + "\\",
                          p.resolveFile(driveSpecLower + "\\", null).getPath());
             /*
              * promised to eliminate consecutive slashes after drive letter.
              */
             assertEqualsIgnoreDriveCase(driveSpec + "\\",
                          p.resolveFile(driveSpec + "/////", null).getPath());
             assertEqualsIgnoreDriveCase(driveSpec + "\\",
                          p.resolveFile(driveSpec + "\\\\\\\\\\\\", null).getPath());
         } else {
             /*
              * Start with simple absolute file names.
              */
             assertEquals(File.separator,
                          p.resolveFile("/", null).getPath());
             assertEquals(File.separator,
                          p.resolveFile("\\", null).getPath());
             /*
              * drive letters are not used, just to be considered as normal
              * part of a name
              */
             String driveSpec = "C:";
             String udir = System.getProperty("user.dir") + File.separatorChar;
             assertEquals(udir + driveSpec,
                          p.resolveFile(driveSpec + "/", null).getPath());
             assertEquals(udir + driveSpec,
                          p.resolveFile(driveSpec + "\\", null).getPath());
             String driveSpecLower = "c:";
             assertEquals(udir + driveSpecLower,
                          p.resolveFile(driveSpecLower + "/", null).getPath());
             assertEquals(udir + driveSpecLower,
                          p.resolveFile(driveSpecLower + "\\", null).getPath());
         }
         /*
          * Now test some relative file name magic.
          */
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("./4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile(".\\4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("./.\\4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("../3/4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("..\\3\\4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("../../5/.././2/./3/6/../4", new File(localize("/1/2/3"))).getPath());
         assertEquals(localize("/1/2/3/4"),
                      p.resolveFile("..\\../5/..\\./2/./3/6\\../4", new File(localize("/1/2/3"))).getPath());
 
     }
 
     /**
      * adapt file separators to local conventions
      */
     private String localize(String path) {
         path = root + path.substring(1);
         return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
     }
 
     /**
      * convenience method
      * the drive letter is in lower case under cygwin
      * calling this method allows tests where FileUtils.normalize
      * is called via resolveFile to pass under cygwin
      */
     private void assertEqualsIgnoreDriveCase(String s1, String s2) {
         if ((Os.isFamily("dos") || Os.isFamily("netware"))
             && s1.length() >= 1 && s2.length() >= 1) {
             StringBuffer sb1 = new StringBuffer(s1);
             StringBuffer sb2 = new StringBuffer(s2);
             sb1.setCharAt(0, Character.toUpperCase(s1.charAt(0)));
             sb2.setCharAt(0, Character.toUpperCase(s2.charAt(0)));
             assertEquals(sb1.toString(), sb2.toString());
         } else {
             assertEquals(s1, s2);
         }
     }
 
     private void assertTaskDefFails(final Class taskClass,
                                        final String message) {
         final String dummyName = "testTaskDefinitionDummy";
         try {
             mbl.addBuildEvent(message, Project.MSG_ERR);
             p.addTaskDefinition(dummyName, taskClass);
             fail("expected BuildException(\""+message+"\", Project.MSG_ERR) when adding task " + taskClass);
         }
         catch(BuildException e) {
             assertEquals(message, e.getMessage());
             mbl.assertEmpty();
             assertTrue(!p.getTaskDefinitions().containsKey(dummyName));
         }
     }
 
     public void testAddTaskDefinition() {
         p.addBuildListener(mbl);
 
         p.addTaskDefinition("Ok", DummyTaskOk.class);
         assertEquals(DummyTaskOk.class, p.getTaskDefinitions().get("Ok"));
         p.addTaskDefinition("OkNonTask", DummyTaskOkNonTask.class);
         assertEquals(DummyTaskOkNonTask.class, p.getTaskDefinitions().get("OkNonTask"));
         mbl.assertEmpty();
 
         assertTaskDefFails(DummyTaskPrivate.class,   DummyTaskPrivate.class   + " is not public");
 
         assertTaskDefFails(DummyTaskProtected.class,
                            DummyTaskProtected.class + " is not public");
 
         assertTaskDefFails(DummyTaskPackage.class,   DummyTaskPackage.class   + " is not public");
 
         assertTaskDefFails(DummyTaskAbstract.class,  DummyTaskAbstract.class  + " is abstract");
         assertTaskDefFails(DummyTaskInterface.class, DummyTaskInterface.class + " is abstract");
 
         assertTaskDefFails(DummyTaskWithoutDefaultConstructor.class, "No public no-arg constructor in " + DummyTaskWithoutDefaultConstructor.class);
         assertTaskDefFails(DummyTaskWithoutPublicConstructor.class,  "No public no-arg constructor in " + DummyTaskWithoutPublicConstructor.class);
 
         assertTaskDefFails(DummyTaskWithoutExecute.class,       "No public execute() in " + DummyTaskWithoutExecute.class);
         assertTaskDefFails(DummyTaskWithNonPublicExecute.class, "No public execute() in " + DummyTaskWithNonPublicExecute.class);
 
         mbl.addBuildEvent("return type of execute() should be void but was \"int\" in " + DummyTaskWithNonVoidExecute.class, Project.MSG_WARN);
         p.addTaskDefinition("NonVoidExecute", DummyTaskWithNonVoidExecute.class);
         mbl.assertEmpty();
         assertEquals(DummyTaskWithNonVoidExecute.class, p.getTaskDefinitions().get("NonVoidExecute"));
     }
 
     public void testInputHandler() {
         InputHandler ih = p.getInputHandler();
         assertNotNull(ih);
         assertTrue(ih instanceof DefaultInputHandler);
         InputHandler pfih = new PropertyFileInputHandler();
         p.setInputHandler(pfih);
         assertSame(pfih, p.getInputHandler());
     }
 
     public void testTaskDefinitionContainsKey() {
         assertTrue(p.getTaskDefinitions().containsKey("echo"));
     }
 
     public void testTaskDefinitionContains() {
         assertTrue(p.getTaskDefinitions().contains(org.apache.tools.ant.taskdefs.Echo.class));
     }
 
     public void testDuplicateTargets() {
         // fail, because buildfile contains two targets with the same name
         try {
             BFT bft = new BFT("", "core/duplicate-target.xml");
         } catch (BuildException ex) {
             assertEquals("specific message",
                          "Duplicate target 'twice'",
                          ex.getMessage());
             return;
         }
         fail("Should throw BuildException about duplicate target");
     }
 
     public void testDuplicateTargetsImport() {
         // overriding target from imported buildfile is allowed
         BFT bft = new BFT("", "core/duplicate-target2.xml");
         bft.expectLog("once", "once from buildfile");
     }
 
     public void testOutputDuringMessageLoggedIsSwallowed()
         throws InterruptedException {
         final String FOO = "foo", BAR = "bar";
         p.addBuildListener(new BuildListener() {
                 public void buildStarted(BuildEvent event) {}
                 public void buildFinished(BuildEvent event) {}
                 public void targetStarted(BuildEvent event) {}
                 public void targetFinished(BuildEvent event) {}
                 public void taskStarted(BuildEvent event) {}
                 public void taskFinished(BuildEvent event) {}
                 public void messageLogged(final BuildEvent actual) {
                     assertEquals(FOO, actual.getMessage());
                     // each of the following lines would cause an
                     // infinite loop if the message wasn't swallowed
                     System.err.println(BAR);
                     System.out.println(BAR);
                     p.log(BAR, Project.MSG_INFO);
                 }
             });
         final boolean[] done = new boolean[] {false};
         Thread t = new Thread() {
                 public void run() {
                     p.log(FOO, Project.MSG_INFO);
                     done[0] = true;
                 }
             };
         t.start();
         t.join(2000);
         assertTrue("Expected logging thread to finish successfully", done[0]);
     }
 
+    /**
+     * @see https://issues.apache.org/bugzilla/show_bug.cgi?id=47623
+     */
+    public void testNullThrowableMessageLog() {
+        p.log(new Task() {}, null, new Throwable(), Project.MSG_ERR);
+        // be content if no exception has been thrown
+    }
+
     private class DummyTaskPrivate extends Task {
         public DummyTaskPrivate() {}
         public void execute() {}
     }
 
     protected class DummyTaskProtected extends Task {
         public DummyTaskProtected() {}
         public void execute() {}
     }
 
     private class BFT extends org.apache.tools.ant.BuildFileTest {
         BFT(String name, String buildfile) {
             super(name);
             this.buildfile = buildfile;
             setUp();
         }
 
         // avoid multiple configurations
         boolean isConfigured = false;
 
         // the buildfile to use
         String buildfile = "";
 
         public void setUp() {
             if (!isConfigured) {
                 configureProject("src/etc/testcases/"+buildfile);
                 isConfigured = true;
             }
         }
 
         public void tearDown() { }
 
         // call a target
         public void doTarget(String target) {
             if (!isConfigured) setUp();
             executeTarget(target);
         }
 
         public org.apache.tools.ant.Project getProject() {
             return super.getProject();
         }
     }//class-BFT
 
 }
 
 class DummyTaskPackage extends Task {
     public DummyTaskPackage() {}
     public void execute() {}
 }
