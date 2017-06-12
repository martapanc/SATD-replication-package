diff --git a/WHATSNEW b/WHATSNEW
index c31886b8e..799a63721 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1903 +1,1907 @@
 Changes from Ant 1.7.x TO current SVN version
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * Ant now requires Java 1.4 or later.
 
  * Improved handling of InterruptException (lets suppose someone/thing
    is trying to kill the thread when we receive an
    InterruptException), when an InterruptException is received, we do
    not wait anymore in a while loop till the end time has been
    reached.
    Bugzilla Report 42924.
 
  * Refactor PropertyHelper and introspection APIs to make extension
    more granular and support setting task/type attribute values to
    objects decoded by custom PropertyEvaluator delegates. Also add
    <propertyhelper> task for registering delegates and/or replacing
    the registered PropertyHelper instance.
    Bugzilla Report 42736.
 
  * Added a restricted form of typedef called <componentdef>. This
    allows definition of elements that can only be within tasks or
    types. This method is now used to define conditions, selectors and
    selectors. This means that tasks may now have nested conditions
    just by implementing the Condition interface, rather than extending
    ConditionBase. It also means that the use of namespaces for some of
    the selectors introduced in Ant 1.7.0 is no longer necessary.
    Implementing this means that the DynamicElement work-around
    introduced in Ant 1.7.0 has been removed.
    Bugzilla Report 40511.
 
  * In the <touch> task when a <mapper> is used, the millis and
    datetime attributes now override the time of the source resource if
    provisioned.
    Bugzilla Report 43235.
 
  * Remove fall-back mechanism for references that are not resolved
    during normal runtime execution.
 
  * FileUtils.createTempFile now actually creates the file.
    The TempFile task still does not create the file by default, can be
    instructed to do so however using a new parameter.
    Bugzilla Report 33969.
   
  * A lock in Project ensured that a BuildListener's messageLogged
    method was only ever executed by a single thread at a time, while
    all other methods could be invoked by multiple threads
    simultaniously (while within <parallel>, for example).  This lock
    is no longer in place, messageLogged should be made thread-safe
    now.
 
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
    Bugzilla Report 35000.
 
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
 
  * if the dir attribute of a <fileset> points to a symbolic link and
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
 
  * Ant now prefers the java.runtime.version system property over
    java.vm.version for the Created-By Manifest attribute.
    Bugzilla Report 47632.
 
 Fixed bugs:
 -----------
 
  * The default logger was failing to print complete stack traces for
    exceptions other than BuildException when inside <ant> or
    <antcall>, thus omitting often important diagnostic
    information.
    Bugzilla 43398 (continued).
 
  * Better handling of package-info.class.
    Bugzilla Report 43114.
 
  * RPM task needed an inserted space between the define and the value.
    Bugzilla Report 46659.
 
  * Got rid of deadlock between in, out and err in the Redirector. 
    Bugzilla Report 44544.
 
  * Caused by AssertionError no longer filtered.
    Bugzilla Report 45631.
  
  * <zip> would sometimes recreate JARs unnecessarily.
    Bugzilla Report 45902.
 
  * <symlink> task couldn't overwrite existing symlinks that pointed to
    nonexistent files
    Bugzilla Report 38199.
 
  * <symlink> task couldn't overwrite files that were in the way of the symlink.
    Bugzilla Report 43426.
    
  * <symlink> task failonerror="false" does not stop build from failing
    when 'ln' command returns non-zero.
    Bugzilla Report 43624  
 
  * <touch> task couldn't differentiate between "no resources
    specified" and "no resources matched."
    Bugzilla Report 43799.
 
  * ManifestClassPath failed when a relative path would traverse the
    file system root.
    Bugzilla Report 44499.
 
  * <globmapper> had an indexoutofbounds when the prefix and postfix
    overlapped.
    Bugzilla Report 44731.
    
  * <typedef> and <taskdef> failed to accept file names with #
    characters in them.
    Bugzilla Report 45190
 
  * A deadlock could occur if a BuildListener tried to access an Ant property
    within messageLogged while a different thread also accessed one.
    Bugzilla Report 45194
 
  * Handle null result of system getProperty() in CommandlineJava.
    Similar to Bugzilla Report 42334.
 
  * Length task did not process nonexistent Resources even though these might
    conceivably still carry file length information.
    Bugzilla Report 45271.
 
  * <javac>'s includeJavaRuntime="false" should work for gcj now.  Note
    that you may need to set includeAntRuntime to false in order to
    have full control.
    Bugzilla Report 34638.
 
  * <sql> would fail if the executed statment didn't return a result
    set with some JDBC driver that dissalow Statement.getResultSet to
    be called in such a situation.
    Bugzilla Report 36265 
 
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
 
  * The ant shell script printed a warning under Cygwin if JAVA_HOME
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
    the chosen Comparator.
    Bugzilla Report 46527.
 
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
 
  * Logging exceptions without a message would cause a
    NullPointerException.
    Bugzilla Report 47623.
 
  * WeblogicDeploymentTool could fail on platforms with a file
    separator other than "/".
    Bugzilla Report 35649.
 
  * The update attribute of the modified selector was ignored.
    Bugzilla Report 32597.
 
  * <manifest> and <jar> can now merge Class-Path attributes from
    multiple sources and optionally flatten them into a single
    attribute.
    The default behaviour still is to keep multiple Class-Path
    attributes if they have been specified and to only include the
    attributes of the last merged manifest.
    Bugzilla Report 39655.
 
  * <delete> didn't work correctly with a <modified> selector because
    it was scanning the same filesets more than once.
    Bugzilla Report 43574.
 
 Other changes:
 --------------
 
  * The get task now also follows redirects from http to https
    Bugzilla Report 47433
 
  * A HostInfo task was added performing information on hosts, including info on 
    the host ant is running on. 
    Bugzilla Reports 45861 and 31164.
 
  * There is now a FileProvider interface for resources that act as a source
    of filenames. This should be used by tasks that require resources
    to provide filenames, rather than require that all resources
    are instances or subclasses of FileResource.
    Bugzilla Report 43348
    
  * Fixcrlf now gives better error messages on bad directory attributes.
    Bugzilla Report 43936
    
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
 
  * Ant now supports scoped properties (see Local task).
    Bugzilla Report 23942.
 
  * <sql>'s CSV output can be controlled via the new attributes
    csvColumnSeparator and csvQuoteCharacter.
    Bugzilla Report 35627.
 
  * <ftp>'s logging has been improved.
    Bugzilla Reports 30932, 31743.
 
  * It is now possible to disable <ftp>'s remote verification.
    Bugzilla Report 35471.
 
  * <sshexec> now supports input in a way similar to <exec>
    Bugzilla Report 39197.
 
  * <scp> can now preserve the file modification time when downloading
    files.
    Bugzilla Report 33939.
 
  * the new task sshsession can run multiple tasks in the presence of
    an SSH session providing (local and remote) tunnels.
    Bugzilla Report 43083.
 
  * ZipOutputStream has been sped up for certain usage scenarios that
    are not used by Ant's family of zip tasks.
    Bugzilla Report 45396.
 
  * <echo> supports an "output" Resource attribute as an alternative to "file".
 
  * <sql> "output" attribute now supports any Resource in addition to a file.
 
  * <scp> no longer requires a passphrase when using key based
    authentication.
    Bugzilla Report 33718.
 
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
    Bugzilla Reports 45651 and 45665
 
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
    some other update was necessary.
    Bugzilla report 45098.
 
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
    ProjectHelper2 will be used just like in Ant 1.7.1.
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
    used to control the prefix prepended to the imported targets'
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
    operating systems respectively.
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
    collections of ZIP and TAR archives as sources.  It extracts them on
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
 
  * <sync>'s <preserveInTarget> has a new attribute that controls
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
 
  * <input ...><handler type="secure" /></input> now uses previously
    undocumented SecureInputHandler shipped with Ant 1.7.1.
 
  * Command line arguments for <exec> and similar tasks can now have
    optional prefix and suffix attributes.
    Bugzilla Report 47365
 
  * <apply>'s srcfile and targetfile child elements can now have
    optional prefix and suffix attributes.
    Bugzilla Report 45625
 
  * <jar> has a new attribute to enable indexing of META-INF
    directories which is disabled for backwards compatibility reasons.
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
    Bugzilla Report 11143.
 
  * A new subclass org.apache.tools.ant.loader.AntClassLoader5 of
    AntClassLoader has been added which overrides getResources
    which became non-final in ClassLoader with Java5+ so
    this method now behaves as expected.
    The new subclass will be used by Ant internally if it is available
    and Ant is running on Java5 or more recent.
    Bugzilla Report 46752.
 
  * a new attributes can chose a different request method than GET for
    the http condition.
    Bugzilla Report 30244
 
  * <splash> now supports a configurable display text and a regular
    expression based way to determine progress based on logged messages.
    Bugzilla Report 39957.
 
  * the number of retries on error in <get> is now configurable.  <get>
    can be told to not download files that already exist locally.
    Bugzilla Report 40058.
 
  * Ant now builds against commons-net 2.0 as well.
    Bugzilla Report 47669.
 
  * A new nested element connectionProperty of <sql> allows setting of
    arbitrary JDBC connection properties.
    Bugzilla Report 33452.
 
  * A new islastmodified condition can check the last modified date of
    resources.
 
  * <rmic> has a new destDir attribute that allows generated files to
    be written to a different location than the original classes.
    Bugzilla Report 20699.
 
  * <rmic> has a new listfiles attribute similar to the existing one of
    <javac>.
    Bugzilla Report 24359.
 
  * It is now possible to suppress the "FAILED" lines sent to Ant's
    logging system via <junit>'s new logFailedTests attribute.
    Bugzilla Report 35073.
    
  * <propertyfile> now can delete entries.
 
  * The <resources> resource collection can now optionally cache its
    contents.
 
  * A new <resourceexists> condition can check whether resources exists.
 
  * <sql> has two new attributes errorproperty and warningproperty that
    can be set if an error/warning occurs.
    Bugzilla Report 38807.
 
+ * <sql> has a new attribute rowcountproperty that can be used to set
+   a property to the number of rows affected by a task execution.
+   Bugzilla Report 40923.
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
 
diff --git a/docs/manual/CoreTasks/sql.html b/docs/manual/CoreTasks/sql.html
index 091c9f18e..d3e3c0ee0 100644
--- a/docs/manual/CoreTasks/sql.html
+++ b/docs/manual/CoreTasks/sql.html
@@ -1,498 +1,505 @@
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
 <title>SQL Task</title>
 </head>
 <body>
 
 <h2><a name="sql">Sql</a></h2>
 <h3>Description</h3>
 <p>Executes a series of SQL statements via JDBC to a database. Statements can 
 either be read in from a text file using the <i>src</i> attribute or from 
 between the enclosing SQL tags.</p>
 
 <p>Multiple statements can be provided, separated by semicolons (or the 
 defined <i>delimiter</i>). Individual lines within the statements can be 
 commented using either --, // or REM at the start of the line.</p>
 
 <p>The <i>autocommit</i> attribute specifies whether auto-commit should be 
 turned on or off whilst executing the statements. If auto-commit is turned 
 on each statement will be executed and committed. If it is turned off the 
 statements will all be executed as one transaction.</p>
 
 <p>The <i>onerror</i> attribute specifies how to proceed when an error occurs 
 during the execution of one of the statements. 
 The possible values are: <b>continue</b> execution, only show the error;
 <b>stop</b> execution, log the error but don't fail the task
 and <b>abort</b> execution and transaction and fail task.</p>
 
 <p>
 <b>Proxies</b>. Some JDBC drivers (including the Oracle thin driver), 
     use the JVM's proxy settings to route their JDBC operations to the database.
     Since Ant1.7, Ant running on Java1.5 or later defaults to 
     <a href="../proxy.html">using
     the proxy settings of the operating system</a>. 
     Accordingly, the OS proxy settings need to be valid, or Ant's proxy
     support disabled with <code>-noproxy</code> option.
 </p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
 <tr>
   <td width="12%" valign="top">driver</td>
   <td width="78%" valign="top">Class name of the jdbc driver</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">url</td>
   <td width="78%" valign="top">Database connection url</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">userid</td>
   <td width="78%" valign="top">Database user name</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">password</td>
   <td width="78%" valign="top">Database password</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">src</td>
   <td width="78%" valign="top">File containing SQL statements</td>
   <td width="10%" valign="top">Yes, unless statements enclosed within tags</td>
 </tr>
 <tr>
   <td valign="top">encoding</td>
   <td valign="top">The encoding of the files containing SQL statements</td>
   <td align="center">No - defaults to default JVM encoding</td>
 </tr>
 <tr>
   <td width="12%" valign="top">delimiter</td>
   <td width="78%" valign="top">String that separates SQL statements</td>
   <td width="10%" valign="top">No, default &quot;;&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">autocommit</td>
   <td width="78%" valign="top">Auto commit flag for database connection (default false)</td>
   <td width="10%" valign="top">No, default &quot;false&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">print</td>
   <td width="78%" valign="top">Print result sets from the statements (default false)</td>
   <td width="10%" valign="top">No, default &quot;false&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">showheaders</td>
   <td width="78%" valign="top">Print headers for result sets from the statements (default true)</td>
   <td width="10%" valign="top">No, default &quot;true&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">showtrailers</td>
   <td width="78%" valign="top">Print trailer for number of rows affected (default true)</td>
   <td width="10%" valign="top">No, default &quot;true&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">output</td>
   <td width="78%" valign="top">Output file for result sets (defaults to System.out)
     <b>Since Ant 1.8</b> can specify any Resource that supports output (see
     <a href="../develop.html#set-magic">note</a>).
   </td>
   <td width="10%" valign="top">No (print to System.out by default)</td>
 </tr>
   <tr>
     <td valign="top">append</td>
     <td valign="top">whether output should be appended to or overwrite
     an existing file.  Defaults to false.</td>
     <td align="center" valign="top">No, ignored if <i>output</i> does not
       specify a filesystem destination.</td>
   </tr>
 <tr>
   <td width="12%" valign="top">classpath</td>
   <td width="78%" valign="top">Classpath used to load driver</td>
   <td width="10%" valign="top">No (use system classpath)</td>
 </tr>
   <tr>
     <td width="12%" valign="top">classpathref</td>
     <td width="78%" valign="top">The classpath to use, given as a <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
   <td width="10%" valign="top">No (use system classpath)</td>
   </tr>
 <tr>
   <td width="12%" valign="top">onerror</td>
   <td width="78%" valign="top">Action to perform when statement fails: continue, stop, abort</td>
   <td width="10%" valign="top">No, default &quot;abort&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">rdbms</td>
   <td width="78%" valign="top">Execute task only if this rdbms</td>
   <td width="10%" valign="top">No (no restriction)</td>
 </tr>
 <tr>
   <td width="12%" valign="top">version</td>
   <td width="78%" valign="top">Execute task only if rdbms version match</td>
   <td width="10%" valign="top">No (no restriction)</td>
 </tr>
 <tr>
   <td width="12%" valign="top">caching</td>
   <td width="78%" valign="top">Should the task cache loaders and the driver?</td>
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">delimitertype</td>
   <td width="78%" valign="top">Control whether the delimiter will only be recognized on a line by itself.<br>
     Can be "normal" -anywhere on the line, or "row", meaning it must be on a line by itself</td>
   <td width="10%" valign="top">No (default:normal)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">keepformat</td>
   <td width="78%" valign="top">Control whether the format of the sql will be preserved.<br>
     Useful when loading packages and procedures.
   <td width="10%" valign="top">No (default=false)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">escapeprocessing</td>
   <td width="78%" valign="top">Control whether the Java statement
     object will perform escape substitution.<br>
     See <a
     href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/Statement.html#setEscapeProcessing(boolean)">Statement's
     API docs</a> for details.  <em>Since Ant 1.6</em>.
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">expandproperties</td>
   <td width="78%" valign="top">Set to true to turn on property expansion in
   nested SQL, inline in the task or nested transactions. <em>Since Ant 1.7</em>.
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">rawblobs</td>
   <td width="78%" valign="top">If true, will write raw streams rather than hex encoding when
     printing BLOB results. <em>Since Ant 1.7.1</em>.</td>
   <td width="10%" valign="top">No, default <em>false</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">failOnConnectionError</td>
   <td width="78%" valign="top">If false, will only print a warning
     message and not execute any statement if the task fails to connect
     to the database.  <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default <em>true</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">strictDelimiterMatching</td>
   <td width="78%" valign="top">If false, delimiters will be searched
     for in a case-insesitive manner (i.e. delimer="go" matches "GO")
     and surrounding whitespace will be ignored (delimter="go" matches
     "GO ").  <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default <em>true</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">showWarnings</td>
   <td width="78%" valign="top">If true, SQLWarnings will be logged at
     the WARN level.  <em>Since Ant 1.8.0</em>.<br/>
     <b>Note:</b> even if the attribute is set to false, warnings that
     apply to the connection will be logged at the verbose level.</td>
   <td width="10%" valign="top">No, default <em>false</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">treatWarningsAsErrors</td>
   <td width="78%" valign="top">If true, SQLWarnings will be treated
     like errors - and the logic selected via the onError attribute
     applies.
     <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default <em>false</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">csvColumnSeparator</td>
   <td width="78%" valign="top">The column separator used when printing
     the results.
     <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default <em>','</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">csvQuoteCharacter</td>
   <td width="78%" valign="top">The character used to quote column
     values.<br/>
     If set, columns that contain either the column separator or the
     quote character itself will be surrounded by the quote character.
     The quote character itself will be doubled if it appears inside of
     the column's value.<br/>
     <b>Note:</b> BLOB values will never be quoted.
     <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default is not set (i.e. no quoting
     ever occurs)</td>
 </tr>
 
 <tr>
   <td valign="top">errorproperty</td>
   <td valign="top">The name of a property to set in the event of an
     error.  <em>Since Ant 1.8.0</em></td>
   <td align="center" valign="top">No</td>
 </tr>
 <tr>
   <td valign="top">warningproperty</td>
   <td valign="top">The name of a property to set in the event of an
     warning.  <em>Since Ant 1.8.0</em></td>
   <td align="center" valign="top">No</td>
 </tr>
+<tr>
+  <td valign="top">rowcountproperty</td>
+  <td valign="top">The name of a property to set to the number of rows
+    updated by the first statement/transaction that actually returned
+    a row count.  <em>Since Ant 1.8.0</em></td>
+  <td align="center" valign="top">No</td>
+</tr>
 </table>
 
 <h3>Parameters specified as nested elements</h3>
 <h4>transaction</h4>
 <p>Use nested <code>&lt;transaction&gt;</code> 
 elements to specify multiple blocks of commands to the executed
 executed in the same connection but different transactions. This
 is particularly useful when there are multiple files to execute
 on the same schema.</p>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">src</td>
     <td valign="top">File containing SQL statements</td>
     <td valign="top" align="center">Yes, unless statements enclosed within tags</td>
   </tr>
 </table>
 <p>The <code>&lt;transaction&gt;</code> element supports any <a
 href="../CoreTypes/resources.html">resource</a> or single element
 resource collection as nested element to specify the resource
 containing the SQL statements.</p>
 
 <h4>any <a href="../CoreTypes/resources.html">resource</a> or resource
 collection</h4>
 
 <p>You can specify multiple sources via nested resource collection
 elements.  Each resource of the collection will be run in a
 transaction of its own.  Prior to Ant 1.7 only filesets were
 supported.  Use a sort resource collection to get a predictable order
 of transactions. </p>
 
 <h4>classpath</h4>
 <p><code>Sql</code>'s <em>classpath</em> attribute is a <a
 href="../using.html#path">PATH like structure</a> and can also be set via a nested
 <em>classpath</em> element. It is used to load the JDBC classes.</p>
 
 <h4>connectionProperty</h4>
 <p><em>Since Ant 1.8.0</em></p>
 <p>Use nested <code>&lt;connectionProperty&gt;</code> elements to
   specify additional JDBC properties that need to be set when
   connecting to the database.</p>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">Name of the property</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">value</td>
     <td valign="top">Value of the property</td>
     <td valign="top" align="center">Yes</td>
   </tr>
 </table>
 
 <h3>Examples</h3>
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
 /&gt;
 </pre></blockquote>
 
 <p>Connects to the database given in <i>url</i> as the sa user using the 
 org.database.jdbcDriver and executes the SQL statements contained within 
 the file data.sql</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;&gt;
   &lt;connectionProperty name=&quot;internal_logon&quot; value=&quot;SYSDBA&quot;&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>Connects to the database given in <i>url</i> as the sa user using
 the org.database.jdbcDriver and executes the SQL statements contained
 within the file data.sql.  Also sets the
 property <i>internal_logon</i> to the value <i>SYSDBA</i>.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     &gt;
 insert
 into table some_table
 values(1,2,3,4);
 
 truncate table some_other_table;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>Connects to the database given in <i>url</i> as the sa
  user using the org.database.jdbcDriver and executes the two SQL statements 
  inserting data into some_table and truncating some_other_table. Ant Properties
  in the nested text will not be expanded.</p>
 
 <p>Note that you may want to enclose your statements in
 <code>&lt;![CDATA[</code> ... <code>]]&gt;</code> sections so you don't
 need to escape <code>&lt;</code>, <code>&gt;</code> <code>&amp;</code>
 or other special characters. For example:</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     &gt;&lt;![CDATA[
 
 update some_table set column1 = column1 + 1 where column2 &lt; 42;
 
 ]]&gt;&lt;/sql&gt;
 </pre></blockquote>
 
 The following command turns property expansion in nested text on (it is off purely for backwards
 compatibility), then creates a new user in the HSQLDB database using Ant properties. 
 
 <blockquote><pre>&lt;sql
     driver="org.hsqldb.jdbcDriver";
     url="jdbc:hsqldb:file:${database.dir}"
     userid="sa"
     password=""
     expandProperties="true"
     &gt;
   &lt;transaction&gt;
     CREATE USER ${newuser} PASSWORD ${newpassword}
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 
 <p>The following connects to the database given in url as the sa user using 
 the org.database.jdbcDriver and executes the SQL statements contained within 
 the files data1.sql, data2.sql and data3.sql and then executes the truncate 
 operation on <i>some_other_table</i>.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot; &gt;
   &lt;transaction  src=&quot;data1.sql&quot;/&gt;
   &lt;transaction  src=&quot;data2.sql&quot;/&gt;
   &lt;transaction  src=&quot;data3.sql&quot;/&gt;
   &lt;transaction&gt;
     truncate table some_other_table;
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following example does the same as (and may execute additional
 SQL files if there are more files matching the pattern
 <code>data*.sql</code>) but doesn't guarantee that data1.sql will be
 run before <code>data2.sql</code>.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;&gt;
   &lt;path&gt;
     &lt;fileset dir=&quot;.&quot;&gt;
       &lt;include name=&quot;data*.sql&quot;/&gt;
     &lt;/fileset&gt;
   &lt;/path&gt;
   &lt;transaction&gt;
     truncate table some_other_table;
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following connects to the database given in url as the sa user using the 
 org.database.jdbcDriver and executes the SQL statements contained within the 
 file data.sql, with output piped to outputfile.txt, searching /some/jdbc.jar 
 as well as the system classpath for the driver class.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
     print=&quot;yes&quot;
     output=&quot;outputfile.txt&quot;
     &gt;
 &lt;classpath&gt;
 	&lt;pathelement location=&quot;/some/jdbc.jar&quot;/&gt;
 &lt;/classpath&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following will only execute if the RDBMS is &quot;oracle&quot; and the version 
 starts with &quot;8.1.&quot;</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
     rdbms=&quot;oracle&quot;
     version=&quot;8.1.&quot;
     &gt;
 insert
 into table some_table
 values(1,2,3,4);
 
 truncate table some_other_table;
 &lt;/sql&gt;
 </pre></blockquote>
 
 
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index 707830a29..910325b59 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,1117 +1,1140 @@
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
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.KeepAliveOutputStream;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.resources.Appendable;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.Union;
 
 import java.io.File;
 import java.io.OutputStream;
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
 import java.util.Locale;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import java.sql.Blob;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.Types;
 
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
     private Union resources;
 
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
      * Results Output Resource.
      */
     private Resource output = null;
 
     /**
      * Action to perform if an error is found
      */
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
     private boolean expandProperties = true;
 
     /**
      * should we print raw BLOB data?
      * @since Ant 1.7.1
      */
     private boolean rawBlobs;
 
     /**
      * delimers must match in case and whitespace is significant.
      * @since Ant 1.8.0
      */
     private boolean strictDelimiterMatching = true;
 
     /**
      * whether to show SQLWarnings as WARN messages.
      * @since Ant 1.8.0
      */
     private boolean showWarnings = false;
 
     /**
      * The column separator used when printing the results.
      *
      * <p>Defaults to ","</p>
      *
      * @since Ant 1.8.0
      */
     private String csvColumnSep = ",";
 
     /**
      * The character used to quote column values.
      *
      * <p>If set, columns that contain either the column separator or
      * the quote character itself will be surrounded by the quote
      * character.  The quote character itself will be doubled if it
      * appears inside of the column's value.</p>
      *
      * <p>If this value is not set (the default), no column values
      * will be quoted, not even if they contain the column
      * separator.</p>
      *
      * <p><b>Note:<b> BLOB values will never be quoted.</p>
      *
      * <p>Defaults to "not set"</p>
      *
      * @since Ant 1.8.0
      */
     private String csvQuoteChar = null;
 
     /**
      * Whether a warning is an error - in which case onError aplies.
      * @since Ant 1.8.0
      */
     private boolean treatWarningsAsErrors = false;
 
     /**
      * The name of the property to set in the event of an error
      * @since Ant 1.8.0
      */
     private String errorProperty = null;
 
     /**
      * The name of the property to set in the event of a warning
      * @since Ant 1.8.0
      */
     private String warningProperty = null;
 
     /**
+     * The name of the property that receives the number of rows
+     * returned
+     * @since Ant 1.8.0
+     */
+    private String rowCountProperty = null;
+
+    /**
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
      * @param expandProperties if true expand properties.
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
         if (rc == null) {
             throw new BuildException("Cannot add null ResourceCollection");
         }
         synchronized (this) {
             if (resources == null) {
                 resources = new Union();
             }
         }
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
         setOutput(new FileResource(getProject(), output));
     }
 
     /**
      * Set the output Resource;
      * optional, defaults to the Ant log.
      * @param output the output Resource to store results.
      * @since Ant 1.8
      */
     public void setOutput(Resource output) {
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
      * Set whether to print raw BLOBs rather than their string (hex) representations.
      * @param rawBlobs whether to print raw BLOBs.
      * @since Ant 1.7.1
      */
     public void setRawBlobs(boolean rawBlobs) {
         this.rawBlobs = rawBlobs;
     }
 
     /**
      * If false, delimiters will be searched for in a case-insesitive
      * manner (i.e. delimer="go" matches "GO") and surrounding
      * whitespace will be ignored (delimter="go" matches "GO ").
      * @since Ant 1.8.0
      */
     public void setStrictDelimiterMatching(boolean b) {
         strictDelimiterMatching = b;
     }
 
     /**
      * whether to show SQLWarnings as WARN messages.
      * @since Ant 1.8.0
      */
     public void setShowWarnings(boolean b) {
         showWarnings = b;
     }
 
     /**
      * Whether a warning is an error - in which case onError aplies.
      * @since Ant 1.8.0
      */
     public void setTreatWarningsAsErrors(boolean b) {
         treatWarningsAsErrors =  b;
     }
 
     /**
      * The column separator used when printing the results.
      *
      * <p>Defaults to ","</p>
      *
      * @since Ant 1.8.0
      */
     public void setCsvColumnSeparator(String s) {
         csvColumnSep = s;
     }
 
     /**
      * The character used to quote column values.
      *
      * <p>If set, columns that contain either the column separator or
      * the quote character itself will be surrounded by the quote
      * character.  The quote character itself will be doubled if it
      * appears inside of the column's value.</p>
      *
      * <p>If this value is not set (the default), no column values
      * will be quoted, not even if they contain the column
      * separator.</p>
      *
      * <p><b>Note:<b> BLOB values will never be quoted.</p>
      *
      * <p>Defaults to "not set"</p>
      *
      * @since Ant 1.8.0
      */
     public void setCsvQuoteCharacter(String s) {
         if (s != null && s.length() > 1) {
             throw new BuildException("The quote character must be a single"
                                      + " character.");
         }
         csvQuoteChar = s;
     }
 
     /**
      * Property to set to "true" if a statement throws an error.
      *
      * @param errorProperty the name of the property to set in the
      * event of an error.
      * @since Ant 1.8.0
      */
     public void setErrorProperty(String errorProperty) {
         this.errorProperty = errorProperty;
     }
 
     /**
      * Property to set to "true" if a statement produces a warning.
      *
      * @param warningProperty the name of the property to set in the
      * event of a warning.
      * @since Ant 1.8.0
      */
     public void setWarningProperty(String warningProperty) {
         this.warningProperty = warningProperty;
     }
 
     /**
+     * Sets a given property to the number of rows in the first
+     * statement that returned a row count.
+     * @since Ant 1.8.0
+     */
+    public void setRowCountProperty(String rowCountProperty) {
+        this.rowCountProperty = rowCountProperty;
+    }
+
+    /**
      * Load the sql file and then execute it
      * @throws BuildException on error.
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0 && resources == null) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or resource collection, "
                                              + "transactions or sql statement "
                                              + "must be set!", getLocation());
                 }
             }
 
             if (srcFile != null && !srcFile.isFile()) {
                 throw new BuildException("Source file " + srcFile
                         + " is not a file!", getLocation());
             }
 
             if (resources != null) {
                 // deal with the resources
                 Iterator iter = resources.iterator();
                 while (iter.hasNext()) {
                     Resource r = (Resource) iter.next();
                     // Make a transaction for each resource
                     Transaction t = createTransaction();
                     t.setSrcResource(r);
                 }
             }
 
             // Make a transaction group for the outer command
             Transaction t = createTransaction();
             t.setSrc(srcFile);
             t.addText(sqlCommand);
 
             if (getConnection() == null) {
                 // not a valid rdbms
                 return;
             }
 
             try {
                 PrintStream out = KeepAliveOutputStream.wrapSystemOut();
                 try {
                     if (output != null) {
                         log("Opening PrintStream to output Resource " + output, Project.MSG_VERBOSE);
                         OutputStream os = null;
                         FileProvider fp =
                             (FileProvider) output.as(FileProvider.class);
                         if (fp != null) {
                             os = new FileOutputStream(fp.getFile(), append);
                         } else {
                             if (append) {
                                 Appendable a =
                                     (Appendable) output.as(Appendable.class);
                                 if (a != null) {
                                     os = a.getAppendOutputStream();
                                 }
                             }
                             if (os == null) {
                                 os = output.getOutputStream();
                                 if (append) {
                                     log("Ignoring append=true for non-appendable"
                                         + " resource " + output,
                                         Project.MSG_WARN);
                                 }
                             }
                         }
                         out = new PrintStream(new BufferedOutputStream(os));
                     }
 
                     // Process all transactions
                     for (Enumeration e = transactions.elements();
                          e.hasMoreElements();) {
 
                         ((Transaction) e.nextElement()).runTransaction(out);
                         if (!isAutocommit()) {
                             log("Committing transaction", Project.MSG_VERBOSE);
                             getConnection().commit();
                         }
                     }
                 } finally {
                     FileUtils.close(out);
                 }
             } catch (IOException e) {
                 closeQuietly();
                 setErrorProperty();
                 if (onError.equals("abort")) {
                     throw new BuildException(e, getLocation());
                 }
             } catch (SQLException e) {
                 closeQuietly();
                 setErrorProperty();
                 if (onError.equals("abort")) {
                     throw new BuildException(e, getLocation());
                 }
             } finally {
                 try {
                     if (getStatement() != null) {
                         getStatement().close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
                 try {
                     if (getConnection() != null) {
                         getConnection().close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
             }
 
             log(goodSql + " of " + totalSql + " SQL statements executed successfully");
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
             if (expandProperties) {
                 line = getProject().replaceProperties(line);
             }
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
 
             sql.append(keepformat ? "\n" : " ").append(line);
 
             // SQL defines "--" as a comment to EOL
             // and in Oracle it may contain a hint
             // so we cannot just remove it, instead we must end it
             if (!keepformat && line.indexOf("--") >= 0) {
                 sql.append("\n");
             }
             int lastDelimPos = lastDelimiterPosition(sql, line);
             if (lastDelimPos > -1) {
                 execSQL(sql.substring(0, lastDelimPos), out);
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
 
             ret = getStatement().execute(sql);
             updateCount = getStatement().getUpdateCount();
             do {
                 if (updateCount != -1) {
                     updateCountTotal += updateCount;
                 }
                 if (ret) {
                     resultSet = getStatement().getResultSet();
                     printWarnings(resultSet.getWarnings(), false);
                     resultSet.clearWarnings();
                     if (print) {
                         printResults(resultSet, out);
                     }
                 }
                 ret = getStatement().getMoreResults();
                 updateCount = getStatement().getUpdateCount();
             } while (ret || updateCount != -1);
 
             printWarnings(getStatement().getWarnings(), false);
             getStatement().clearWarnings();
 
             log(updateCountTotal + " rows affected", Project.MSG_VERBOSE);
+            if (updateCountTotal != -1) {
+                setRowCountProperty(updateCountTotal);
+            }
 
             if (print && showtrailers) {
                 out.println(updateCountTotal + " rows affected");
             }
             SQLWarning warning = getConnection().getWarnings();
             printWarnings(warning, true);
             getConnection().clearWarnings();
             goodSql++;
         } catch (SQLException e) {
             log("Failed to execute: " + sql, Project.MSG_ERR);
             setErrorProperty();
             if (!onError.equals("abort")) {
                 log(e.toString(), Project.MSG_ERR);
             }
             if (!onError.equals("continue")) {
                 throw e;
             }
         } finally {
             if (resultSet != null) {
                 try {
                     resultSet.close();
                 } catch (SQLException e) {
                     //ignore
                 }
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
         ResultSet rs = getStatement().getResultSet();
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
     protected void printResults(ResultSet rs, PrintStream out) throws SQLException {
         if (rs != null) {
             log("Processing new result set.", Project.MSG_VERBOSE);
             ResultSetMetaData md = rs.getMetaData();
             int columnCount = md.getColumnCount();
             if (columnCount > 0) {
                 if (showheaders) {
                     out.print(md.getColumnName(1));
                     for (int col = 2; col <= columnCount; col++) {
                          out.print(csvColumnSep);
                          out.print(maybeQuote(md.getColumnName(col)));
                     }
                     out.println();
                 }
                 while (rs.next()) {
                     printValue(rs, 1, out);
                     for (int col = 2; col <= columnCount; col++) {
                         out.print(csvColumnSep);
                         printValue(rs, col, out);
                     }
                     out.println();
                     printWarnings(rs.getWarnings(), false);
                 }
             }
         }
         out.println();
     }
 
     private void printValue(ResultSet rs, int col, PrintStream out)
             throws SQLException {
         if (rawBlobs && rs.getMetaData().getColumnType(col) == Types.BLOB) {
             Blob blob = rs.getBlob(col);
             if (blob != null) {
                 new StreamPumper(rs.getBlob(col).getBinaryStream(), out).run();
             }
         } else {
             out.print(maybeQuote(rs.getString(col)));
         }
     }
 
     private String maybeQuote(String s) {
         if (csvQuoteChar == null || s == null
             || (s.indexOf(csvColumnSep) == -1 && s.indexOf(csvQuoteChar) == -1)
             ) {
             return s;
         }
         StringBuffer sb = new StringBuffer(csvQuoteChar);
         int len = s.length();
         char q = csvQuoteChar.charAt(0);
         for (int i = 0; i < len; i++) {
             char c = s.charAt(i);
             if (c == q) {
                 sb.append(q);
             }
             sb.append(c);
         }
         return sb.append(csvQuoteChar).toString();
     }
 
     /*
      * Closes an unused connection after an error and doesn't rethrow
      * a possible SQLException
      * @since Ant 1.7
      */
     private void closeQuietly() {
         if (!isAutocommit() && getConnection() != null && onError.equals("abort")) {
             try {
                 getConnection().rollback();
             } catch (SQLException ex) {
                 // ignore
             }
         }
     }
 
 
     /**
      * Caches the connection returned by the base class's getConnection method.
      *
      * <p>Subclasses that need to provide a different connection than
      * the base class would, should override this method but keep in
      * mind that this class expects to get the same connection
      * instance on consecutive calls.</p>
      *
      * <p>returns null if the connection does not connect to the
      * expected RDBMS.</p>
      */
     protected Connection getConnection() {
         if (conn == null) {
             conn = super.getConnection();
             if (!isValidRdbms(conn)) {
                 conn = null;
             }
         }
         return conn;
     }
 
     /**
      * Creates and configures a Statement instance which is then
      * cached for subsequent calls.
      *
      * <p>Subclasses that want to provide different Statement
      * instances, should override this method but keep in mind that
      * this class expects to get the same connection instance on
      * consecutive calls.</p>
      */
     protected Statement getStatement() throws SQLException {
         if (statement == null) {
             statement = getConnection().createStatement();
             statement.setEscapeProcessing(escapeProcessing);
         }
 
         return statement;
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
             if (src != null) {
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
                 this.tSqlCommand += sql;
             }
         }
 
         /**
          * Set the source resource.
          * @param a the source resource collection.
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
                     reader = (encoding == null) ? new InputStreamReader(is)
                         : new InputStreamReader(is, encoding);
                     runStatements(reader, out);
                 } finally {
                     FileUtils.close(is);
                     FileUtils.close(reader);
                 }
             }
         }
     }
 
     public int lastDelimiterPosition(StringBuffer buf, String currentLine) {
         if (strictDelimiterMatching) {
             if ((delimiterType.equals(DelimiterType.NORMAL)
                  && StringUtils.endsWith(buf, delimiter)) ||
                 (delimiterType.equals(DelimiterType.ROW)
                  && currentLine.equals(delimiter))) {
                 return buf.length() - delimiter.length();
             }
             // no match
             return -1;
         } else {
             String d = delimiter.trim().toLowerCase(Locale.US);
             if (delimiterType.equals(DelimiterType.NORMAL)) {
                 // still trying to avoid wasteful copying, see
                 // StringUtils.endsWith
                 int endIndex = delimiter.length() - 1;
                 int bufferIndex = buf.length() - 1;
                 while (bufferIndex >= 0
                        && Character.isWhitespace(buf.charAt(bufferIndex))) {
                     --bufferIndex;
                 }
                 if (bufferIndex < endIndex) {
                     return -1;
                 }
                 while (endIndex >= 0) {
                     if (buf.substring(bufferIndex, bufferIndex + 1)
                         .toLowerCase(Locale.US).charAt(0)
                         != d.charAt(endIndex)) {
                         return -1;
                     }
                     bufferIndex--;
                     endIndex--;
                 }
                 return bufferIndex + 1;
             } else {
                 return currentLine.trim().toLowerCase(Locale.US).equals(d)
                     ? buf.length() - currentLine.length() : -1;
             }
         }
     }
 
     private void printWarnings(SQLWarning warning, boolean force)
         throws SQLException {
         SQLWarning initialWarning = warning;
         if (showWarnings || force) {
             while (warning != null) {
                 log(warning + " sql warning",
                     showWarnings ? Project.MSG_WARN : Project.MSG_VERBOSE);
                 warning = warning.getNextWarning();
             }
         }
         if (initialWarning != null) {
             setWarningProperty();
         }
         if (treatWarningsAsErrors && initialWarning != null) {
             throw initialWarning;
         }
     }
 
     protected final void setErrorProperty() {
-        setProperty(errorProperty);
+        setProperty(errorProperty, "true");
     }
 
     protected final void setWarningProperty() {
-        setProperty(warningProperty);
+        setProperty(warningProperty, "true");
+    }
+
+    protected final void setRowCountProperty(int rowCount) {
+        setProperty(rowCountProperty, Integer.toString(rowCount));
     }
 
-    private void setProperty(String name) {
+    private void setProperty(String name, String value) {
         if (name != null) {
-            getProject().setNewProperty(name, "true");
+            getProject().setNewProperty(name, value);
         }
     }
 }
