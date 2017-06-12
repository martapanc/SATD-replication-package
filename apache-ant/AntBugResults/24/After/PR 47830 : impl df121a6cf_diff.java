diff --git a/WHATSNEW b/WHATSNEW
index fd5202377..1b260c79a 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1176 +1,1181 @@
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
 
  * globmapper didn't work properly if the "to" or "from" patterns
    didn't contain a "*".  In particular it implicitly added a * to the
    end of the pattern(s).  This is no longer the case.  If you relied
    on this behavior you will now need to explicitly specify the
    trailing "*".
    Bugzilla Report 46506.
 
  * <copy> silently ignored missing resources even with
    failOnError="true".  If your build tries to copy non-existant
    resources and you relied on this behavior you must now explicitly
    set failOnError to false.
    Bugzilla Report 47362.
 
  * Ant now prefers the java.runtime.version system property over
    java.vm.version for the Created-By Manifest attribute.
    Bugzilla Report 47632.
 
  * The <image> task now supports a nested mapper.  In order to
    implement this, the Java API of the task had to change so any
    custom subclass overriding the processFile method will need to
    adapt (by overriding the new two-arg processFile method).
    Bugzilla Report 23243.
 
  * A new property syntax can be used to set attributes from
    references: ${ant.ref:some-reference}
 
    In most cases this will yield the exact same result as 
    ${toString:some-reference} - only when an attribute setter method
    accepts an object type other than string and the project's
    reference is an Object of matching type the new syntax will pass in
    that object.
 
    If your build file already contains properties whose name starts
    with "ant.ref:" there is a potential for collision.  If your
    property has been set, normal property expansion will take
    precedence over the new syntax.  If the property has not been set
    and a reference with the postfix of your property name exists
    (i.e. in a very unlikely event) then the new syntax would yield a
    different result (an expanded property) than Ant 1.7.1 did.
 
+ * A ProjectHelper implementation can now provide the default build file
+   name it is expecting, and can specify if they can support a specific build
+   file. So Ant is now capable of supporting several ProjectHelper
+   implementations, deciding on which to use depending of the input build file.
+
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
 
  * when using custom filterreaders with the <filterreader classname="">
    syntax Ant could leak memory.
    The problem didn't occur when using <typedef> or <componentdef> to
    define the filterreader which is the recommended approach.
    Bugzilla Report 45439.
 
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
    
  * There is now a URLProvider interface for resources that act as a
    source of URLs. This should be used by tasks that require resources
    to provide URLs, rather than require that all resources are
    instances or subclasses of URLResource.
    
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
 
  * <javac>, <rmic>, <javah> and <native2ascii> now provide a nested
    element to specify the task's (compiler) adapter as an instance of
    a class that has been defined via typedef/componentdef.  This
    allows more control over the classpath and allows adapters to be
    defined in Antlibs easily.
 
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
 
  * <sql> has a new attribute rowcountproperty that can be used to set
    a property to the number of rows affected by a task execution.
    Bugzilla Report 40923.
 
  * when Ant copies files without filtering, it will now use NIO
    channels.
    Bugzilla Report 30094.
 
  * <get> has a new attribute that can be used to disable caching on
    HTTP connections at the HttpUrlConnection level.
    Bugzilla Report 41891.
 
  * <tar> and <zip> (and tasks derived from <zip>) will now create the
    parent directory of the destination archive if it doesn't exist.
    Bugzilla Report 45377.
 
  * A new filterreader <sortfilter> that sorts input lines has been
    added.
    Bugzilla Report 40504.
 
  * A new token filter <uniqfilter> that suppresses tokens that match
    their ancestor token has been added.
 
  * <rootfileset>s nested into <classfileset>s can now use a dir
    attribute different from the <classfileset>.
    Bugzilla Report 37763.
 
  * <path> can now optionally cache its contents.
 
  * <property> can now specify values as nested text.
    Bugzilla Report 32917.
 
  * a new parentFirst attribute on <javaresource> allows resources to
    be loaded from the specified classpath rather than the system
    classloader.
    Bugzilla Report 41369.
    
  * <property location="from" basedir="to" relative="true"/> can now
    calculate relative paths.
 
  * The <name> selector supports a new handleDirSep attribute that
    makes it ignore differences between / and \ separators.
    Bugzilla Report 47858.
 
  * <get> now supports resource collections (as long as the resources
    contained provide URLs) and can get multiple resources in a single
    task.
 
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
diff --git a/docs/manual/developlist.html b/docs/manual/developlist.html
index 1d8420e01..4aaadcb70 100644
--- a/docs/manual/developlist.html
+++ b/docs/manual/developlist.html
@@ -1,47 +1,48 @@
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
 <meta http-equiv="Content-Language" content="en-us"/>
 <link rel="stylesheet" type="text/css" href="stylesheets/style.css"/>
 <title>Apache Ant User Manual</title>
 <base target="mainFrame"/>
 </head>
 
 <body>
 
 <h2><a href="toc.html" target="navFrame">Table of Contents</a></h2>
 
 <h3>Developing with Ant</h3>
 
 <a href="../ant_in_anger.html">Ant in Anger</a><br/>
 <a href="../ant_task_guidelines.html">Ant Task Guidelines</a><br/>
 <a href="develop.html#writingowntask">Writing Your Own Task</a><br/>
 <a href="base_task_classes.html">Tasks Designed for Extension</a><br/>
 <a href="develop.html#buildevents">Build Events</a><br/>
 <a href="develop.html#integration">Source-code Integration</a><br/>
 <a href="inputhandler.html">InputHandler</a><br/>
 <a href="antexternal.html">Using Ant Tasks Outside of Ant</a><br/>
+<a href="projecthelper.html">The Ant frontend: ProjectHelper</a><br/>
 
 <br/>Tutorials<br/>
 <a href="tutorial-HelloWorldWithAnt.html">Hello World with Ant</a><br/>
 <a href="tutorial-writing-tasks.html">Writing Tasks</a><br/>
 <a href="tutorial-tasks-filesets-properties.html">Tasks using Properties, Filesets &amp; Paths</a><br/>
 
 </body>
 </html>
diff --git a/docs/manual/projecthelper.html b/docs/manual/projecthelper.html
new file mode 100644
index 000000000..ac84f749b
--- /dev/null
+++ b/docs/manual/projecthelper.html
@@ -0,0 +1,131 @@
+<!--
+   Licensed to the Apache Software Foundation (ASF) under one or more
+   contributor license agreements.  See the NOTICE file distributed with
+   this work for additional information regarding copyright ownership.
+   The ASF licenses this file to You under the Apache License, Version 2.0
+   (the "License"); you may not use this file except in compliance with
+   the License.  You may obtain a copy of the License at
+
+       http://www.apache.org/licenses/LICENSE-2.0
+
+   Unless required by applicable law or agreed to in writing, software
+   distributed under the License is distributed on an "AS IS" BASIS,
+   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+   See the License for the specific language governing permissions and
+   limitations under the License.
+-->
+<html>
+
+<head>
+<meta http-equiv="Content-Language" content="en-us">
+<link rel="stylesheet" type="text/css" href="stylesheets/style.css">
+<title>The Ant frontend: ProjectHelper</title>
+</head>
+
+<body>
+<h1>The Ant frontend: ProjectHelper</h1>
+
+<h2><a name="definition">What is a ProjectHelper?</a></h2>
+
+<p>
+The <code>ProjectHelper</code> in Ant is responsible to parse the build file
+and create java instances representing the build workflow. It also declares which
+kind of file it can parse, and which file name it expects as default input file.
+</p>
+<p>
+So in Ant there is a default <code>ProjectHelper</code>
+(<code>org.apache.tools.ant.helper.ProjectHelper2</code>) which will parse the
+usual build.xml files. And if no build file is specified on the command line, it
+will expect to find a build.xml file.
+</p>
+
+<p>
+The immediate benefit of a such abstraction it that it is possible to make Ant
+understand other kind of descriptive language than XML. Some experiment have
+been done around a pure java frontend, and a groovy one too (ask the dev mailing
+list for further info about these).
+</p>
+
+<h2><a name="repository">How is Ant is selecting the proper ProjectHelper</a></h2>
+
+<p>
+Ant can now know about several implementations of <code>ProjectHelper</code>
+and have to decide which to use for each build file.
+</p>
+
+<p>So Ant at startup will list the found implementations and will keep it
+ordered as it finds them in an internal 'repository':
+<ul>
+    <li>the first to be searched for is the one declared by the system property
+        <code>org.apache.tools.ant.ProjectHelper</code> (see
+        <a href="running.html#sysprops">Java System Properties</a>);</li>
+    <li>then it searches with its class loader for a <code>ProjectHelper</code>
+        service declarations in the META-INF: it searches in the classpath for a
+        file <code>META-INF/services/org.apache.tools.ant.ProjectHelper</code>.
+        This file will just contain the fully qualified name of the
+        implementation of <code>ProjectHelper</code> to instanciate;</li>
+    <li>it will also search with the system class loader for
+        <code>ProjectHelper</code> service declarations in the META-INF;</li>
+    <li>last but not least it will add its default <code>ProjectHelper</code>
+        that can parse classical build.xml files.</li>
+</ul>
+In case of error while trying to instanciate a <code>ProjectHelper</code>, Ant
+will log an error but still won't stop. If you want further debugging
+info about the <code>ProjectHelper</code> internal 'repository', use the system
+property <code>ant.project-helper-repo.debug</code> and set it to
+<code>true</code>; the full stack trace will then also be printed.
+</p>
+
+<p>
+Then when Ant is expected to parse a file, it will ask the
+<code>ProjectHelper</code> repository to found an implementation that will be
+able to parse the input file. Actually it will just iterate on the ordered list
+and the first implementation that returns <code>true</code> to
+<code>supportsBuildFile(File buildFile)</code> will be selected.
+</p>
+
+<p>
+And when Ant is launching and there is no input file specified, it will search for
+a default input file. It will iterate on the list of <code>ProjectHelper</code>
+and will select the first one that expects a default file that actually exist.
+</p>
+
+<h2><a name="writing">Writing your own ProjectHelper</a></h2>
+
+<p>
+The class <code>org.apache.tools.ant.ProjectHelper</code> is the API expected to
+be implemented. So write your own <code>ProjectHelper</code> by extending that
+abstract class. You are then expected to implement at least the function
+<code>parse(Project project, Object source)</code>. Note also that your
+implementation will be instanciated by Ant, and it is expecting a default
+constructor with no arguments.
+</p>
+
+<p>
+Then there are some functions that will help you define what your helper is
+capable of and what is is expecting:
+<ul>
+    <li><code>getDefaultBuildFile()</code>: defines which file name is expected if
+    none provided</li>
+    <li><code>supportsBuildFile(File buildFile)</code>: defines if your parser
+    can parse the input file</li>
+</ul>
+</p>
+
+<p>
+Now that you have your implementation ready, you have to declare it to Ant. Two
+solutions here:
+<ul>
+    <li>use the system property <code>org.apache.tools.ant.ProjectHelper</code>
+        (see also the <a href="running.html#sysprops">Java System Properties</a>);</li>
+    <li>use the service file in META-INF: in the jar you will build with your
+        implementation, add a file
+        <code>META-INF/services/org.apache.tools.ant.ProjectHelper</code>.
+        And then in this file just put the fully qualified name of your
+        implementation</li>
+</ul>
+</p>
+
+</body>
+</html>
+
diff --git a/docs/manual/running.html b/docs/manual/running.html
index e8b99b579..502bbd21d 100644
--- a/docs/manual/running.html
+++ b/docs/manual/running.html
@@ -1,605 +1,611 @@
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
 <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
 <title>Running Apache Ant</title>
 </head>
 
 <body>
 
 <h1>Running Ant</h1>
 <h2><a name="commandline">Command Line</a></h2>
 <p> If you've installed Ant as described in the
 <a href="install.html"> Installing Ant</a> section,
 running Ant from the command-line is simple: just type
 <code>ant</code>.</p>
 <p>When no arguments are specified, Ant looks for a <code>build.xml</code>
 file in the current directory and, if found, uses that file as the
 build file and runs the target specified in the <code>default</code>
 attribute of the <code>&lt;project&gt;</code> tag.
 To make Ant use
 a build file other than <code>build.xml</code>, use the command-line
 option <nobr><code>-buildfile <i>file</i></code></nobr>,
 where <i>file</i> is the name of the build file you want to use.</p>
 If you use the <nobr><code>-find [<i>file</i>]</code></nobr> option,
 Ant will search for a build file first in the current directory, then in
 the parent directory, and so on, until either a build file is found or the root
 of the filesystem has been reached. By default, it will look for a build file
 called <code>build.xml</code>. To have it search for a build file other
 than <code>build.xml</code>, specify a file argument.
 <strong>Note:</strong> If you include any other flags or arguments
 on the command line after
 the <nobr><code>-find</code></nobr> flag, you must include the file argument
 for the <nobr><code>-find</code></nobr> flag, even if the name of the
 build file you want to find is <code>build.xml</code>.
 
 <p>You can also set <a href="using.html#properties">properties</a> on the
 command line.  This can be done with
 the <nobr><code>-D<i>property</i>=<i>value</i></code></nobr> option,
 where <i>property</i> is the name of the property,
 and <i>value</i> is the value for that property. If you specify a
 property that is also set in the build file
 (see the <a href="CoreTasks/property.html">property</a> task),
 the value specified on the
 command line will override the value specified in the
 build file.
 Defining properties on the command line can also be used to pass in
 the value of environment variables; just pass
 <nobr><code>-DMYVAR=%MYVAR%</code></nobr> (Windows) or
 <nobr><code>-DMYVAR=$MYVAR</code></nobr> (Unix)
 to Ant. You can then access
 these variables inside your build file as <code>${MYVAR}</code>.
 You can also access environment variables using the
 <a href="CoreTasks/property.html"> property</a> task's
 <code>environment</code> attribute.
 </p>
 
 <p>Options that affect the amount of logging output by Ant are:
 <nobr><code>-quiet</code></nobr>,
 which instructs Ant to print less
 information to the console;
 <nobr><code>-verbose</code></nobr>, which causes Ant to print
 additional information to the console; and <nobr><code>-debug</code></nobr>,
 which causes Ant to print considerably more additional information.
 </p>
 
 <p>It is also possible to specify one or more targets that should be executed.
 When omitted, the target that is specified in the
 <code>default</code> attribute of the
 <a href="using.html#projects"><code>project</code></a> tag is
 used.</p>
 
 <p>The <nobr><code>-projecthelp</code></nobr> option prints out a list
 of the build file's targets. Targets that include a
 <code>description</code> attribute are listed as &quot;Main targets&quot;,
 those without a <code>description</code> are listed as
 &quot;Other targets&quot;, then the &quot;Default&quot; target is listed
 ("Other targets" are only displayed if there are no main
 targets, or if Ant is invoked in -verbose or -debug mode).
 
 <h3><a name="options">Command-line Options Summary</a></h3>
 <pre>ant [options] [target [target2 [target3] ...]]
 Options:
   -help, -h              print this message
   -projecthelp, -p       print project help information
   -version               print the version information and exit
   -diagnostics           print information that might be helpful to
                          diagnose or report problems.
   -quiet, -q             be extra quiet
   -verbose, -v           be extra verbose
   -debug, -d             print debugging information
   -emacs, -e             produce logging information without adornments
   -lib &lt;path&gt;            specifies a path to search for jars and classes
   -logfile &lt;file&gt;        use given file for log
     -l     &lt;file&gt;                ''
   -logger &lt;classname&gt;    the class which is to perform logging
   -listener &lt;classname&gt;  add an instance of class as a project listener
   -noinput               do not allow interactive input
   -buildfile &lt;file&gt;      use given buildfile
     -file    &lt;file&gt;              ''
     -f       &lt;file&gt;              ''
   -D&lt;property&gt;=&lt;value&gt;   use value for given property
   -keep-going, -k        execute all targets that do not depend
                          on failed target(s)
   -propertyfile &lt;name&gt;   load all properties from file with -D
                          properties taking precedence
   -inputhandler &lt;class&gt;  the class which will handle input requests
   -find &lt;file&gt;           (s)earch for buildfile towards the root of
     -s  &lt;file&gt;           the filesystem and use it
   -nice  number          A niceness value for the main thread:
                          1 (lowest) to 10 (highest); 5 is the default
   -nouserlib             Run ant without using the jar files from ${user.home}/.ant/lib
   -noclasspath           Run ant without using CLASSPATH
   -autoproxy             Java 1.5+ : use the OS proxies
   -main &lt;class&gt;          override Ant's normal entry point
 </pre>
 <p>For more information about <code>-logger</code> and
 <code>-listener</code> see
 <a href="listeners.html">Loggers &amp; Listeners</a>.
 <p>For more information about <code>-inputhandler</code> see
 <a href="inputhandler.html">InputHandler</a>.
 <p>Easiest way of changing the exit-behaviour is subclassing the original main class:
 <pre>
 public class CustomExitCode extends org.apache.tools.ant.Main {
     protected void exit(int exitCode) {
         // implement your own behaviour, e.g. NOT exiting the JVM
     }
 }
 </pre> and starting Ant with access (<tt>-lib path-to-class</tt>) to this class.
 </p>
 
 <h3><a name="libs">Library Directories</a></h3>
 <p>
 Prior to Ant 1.6, all jars in the ANT_HOME/lib would be added to the CLASSPATH
 used to run Ant. This was done in the scripts that started Ant. From Ant 1.6,
 two directories are scanned by default and more can be added as required. The
 default directories scanned are ANT_HOME/lib and a user specific directory,
 ${user.home}/.ant/lib. This arrangement allows the Ant installation to be
 shared by many users while still allowing each user to deploy additional jars.
 Such additional jars could be support jars for Ant's optional tasks or jars
 containing third-party tasks to be used in the build. It also allows the main Ant installation to be locked down which will please system adminstrators.
 </p>
 
 <p>
 Additional directories to be searched may be added by using the -lib option.
 The -lib option specifies a search path. Any jars or classes in the directories
 of the path will be added to Ant's classloader. The order in which jars are
 added to the classpath is as follows:
 </p>
 
 <ul>
   <li>-lib jars in the order specified by the -lib elements on the command line</li>
   <li>jars from ${user.home}/.ant/lib (unless -nouserlib is set)</li>
   <li>jars from ANT_HOME/lib</li>
 </ul>
 
 <p>
 Note that the CLASSPATH environment variable is passed to Ant using a -lib
 option. Ant itself is started with a very minimalistic classpath.
 Ant should work perfectly well with an empty CLASSPATH environment variable,
 something the the -noclasspath option actually enforces. We get many more support calls related to classpath problems (especially quoting problems) than
 we like.
 
 </p>
 
 <p>
 The location of ${user.home}/.ant/lib is somewhat dependent on the JVM. On Unix
 systems ${user.home} maps to the user's home directory whilst on recent
 versions of Windows it will be somewhere such as
 C:\Documents&nbsp;and&nbsp;Settings\username\.ant\lib. You should consult your
 JVM documentation for more details.
 </p>
 
 <h3>Examples</h3>
 <blockquote>
   <pre>ant</pre>
 </blockquote>
 <p>runs Ant using the <code>build.xml</code> file in the current directory, on
 the default target.</p>
 
 <blockquote>
   <pre>ant -buildfile test.xml</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the default target.</p>
 
 <blockquote>
   <pre>ant -buildfile test.xml dist</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the target called <code>dist</code>.</p>
 
 <blockquote>
   <pre>ant -buildfile test.xml -Dbuild=build/classes dist</pre>
 </blockquote>
 <p>runs Ant using the <code>test.xml</code> file in the current directory, on
 the target called <code>dist</code>, setting the <code>build</code> property
 to the value <code>build/classes</code>.</p>
 
 <blockquote>
   <pre>ant -lib /home/ant/extras</pre>
 </blockquote>
 <p>runs Ant picking up additional task and support jars from the
 /home/ant/extras location</p>
 
 <blockquote>
   <pre>ant -lib one.jar;another.jar</pre>
   <pre>ant -lib one.jar -lib another.jar</pre>
 </blockquote>
 <p>adds two jars to Ants classpath.</p>
 
 
 
 <h3><a name="files">Files</a></h3>
 
 <p>The Ant wrapper script for Unix will source (read and evaluate) the
 file <code>~/.antrc</code> before it does anything. On Windows, the Ant
 wrapper batch-file invokes <code>%HOME%\antrc_pre.bat</code> at the start and
 <code>%HOME%\antrc_post.bat</code> at the end.  You can use these
 files, for example, to set/unset environment variables that should only be
 visible during the execution of Ant.  See the next section for examples.</p>
 
 <h3><a name="envvars">Environment Variables</a></h3>
 
 <p>The wrapper scripts use the following environment variables (if
 set):</p>
 
 <ul>
   <li><code>JAVACMD</code> - full path of the Java executable.  Use this
   to invoke a different JVM than <code>JAVA_HOME/bin/java(.exe)</code>.</li>
 
   <li><code>ANT_OPTS</code> - command-line arguments that should be
   passed to the JVM. For example, you can define system properties or set
   the maximum Java heap size here.</li>
 
   <li><code>ANT_ARGS</code> - Ant command-line arguments. For example,
   set <code>ANT_ARGS</code> to point to a different logger, include a
   listener, and to include the <code>-find</code> flag.</li>
   <strong>Note:</strong> If you include <code>-find</code>
   in <code>ANT_ARGS</code>, you should include the name of the build file
   to find, even if the file is called <code>build.xml</code>.
 </ul>
 
 <h3><a name="sysprops">Java System Properties</a></h3>
 <p>Some of Ant's core classes can be configured via system properties.</p>
 <p>Here is the result of a search through the codebase. Because system properties are
 available via Project instance, I searched for them with a
 <pre>
     grep -r -n "getPropert" * &gt; ..\grep.txt
 </pre>
 command. After that I filtered out the often-used but not-so-important values (most of them
 read-only values): <i>path.separator, ant.home, basedir, user.dir, os.name,
 line.separator, java.home, java.version, java.version, user.home, java.class.path</i><br>
 And I filtered out the <i>getPropertyHelper</i> access.</p>
 <table border="1">
 <tr>
   <th>property name</th>
   <th>valid values /default value</th>
   <th>description</th>
 </tr>
 <tr>
   <td><code>ant.build.javac.source</code></td>
   <td>Source-level version number</td>
   <td>Default <em>source</em> value for &lt;javac&gt;/&lt;javadoc&gt;</td>
 </tr>
 <tr>
   <td><code>ant.build.javac.target</code></td>
   <td>Class-compatibility version number</td>
   <td>Default <em>target</em> value for &lt;javac&gt;</td>
 </tr>
 <tr>
   <td><code>ant.executor.class</code></td>
   <td>classname; default is org. apache. tools. ant. helper. DefaultExecutor</td>
   <td><b>Since Ant 1.6.3</b> Ant will delegate Target invocation to the
 org.apache.tools.ant.Executor implementation specified here.
   </td>
 </tr>
 
 <tr>
   <td><code>ant.file</code></td>
   <td>read only: full filename of the build file</td>
   <td>This is set to the name of the build file. In
   <a href="CoreTasks/import.html">
   &lt;import&gt;-ed</a> files, this is set to the containing build file.
   </td>
 </tr>
 
 <tr>
   <td><code>ant.file.*</code></td>
   <td>read only: full filename of the build file of Ant projects
   </td>
   <td>This is set to the name of a file by project;
   this lets you determine the location of <a href="CoreTasks/import.html">
   &lt;import&gt;-ed</a> files,
   </td>
 </tr>
 
 <tr>
   <td><code>ant.input.properties</code></td>
   <td>filename (required)</td>
   <td>Name of the file holding the values for the
       <a href="inputhandler.html">PropertyFileInputHandler</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.logger.defaults</code></td>
   <!-- add the blank after the slash, so the browser can do a line break -->
   <td>filename (optional, default '/org/ apache/ tools/ ant/ listener/ defaults.properties')</td>
   <td>Name of the file holding the color mappings for the
       <a href="listeners.html#AnsiColorLogger">AnsiColorLogger</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.netrexxc.*</code></td>
   <td>several formats</td>
   <td>Use specified values as defaults for <a href="OptionalTasks/netrexxc.html">netrexxc</a>.
   </td>
 </tr>
 <tr>
   <td><code>ant.PropertyHelper</code></td>
   <td>ant-reference-name (optional)</td>
   <td>Specify the PropertyHelper to use. The object must be of the type
       org.apache.tools.ant.PropertyHelper. If not defined an object of
       org.apache.tools.ant.PropertyHelper will be used as PropertyHelper.
   </td>
 </tr>
 <tr>
   <td><code>ant.regexp.regexpimpl</code></td>
   <td>classname</td>
   <td>classname for a RegExp implementation; if not set Ant uses JDK 1.4's implementation;
       <a href="CoreTypes/mapper.html#regexp-mapper">RegExp-Mapper</a>
       "Choice of regular expression implementation"
   </td>
 </tr>
 <tr>
   <td><code>ant.reuse.loader</code></td>
   <td>boolean</td>
   <td>allow to reuse classloaders
       used in org.apache.tools.ant.util.ClasspathUtil
   </td>
 </tr>
 <tr>
   <td><code>ant.XmlLogger.stylesheet.uri</code></td>
   <td>filename (default 'log.xsl')</td>
   <td>Name for the stylesheet to include in the logfile by
       <a href="listeners.html#XmlLogger">XmlLogger</a>.
   </td>
 </tr>
 <tr>
   <td><code>build.compiler</code></td>
   <td>name</td>
   <td>Specify the default compiler to use.
       see <a href="CoreTasks/javac.html">javac</a>,
       <a href="OptionalTasks/ejb.html#ejbjar_weblogic">EJB Tasks</a>
       (compiler attribute),
       <a href="OptionalTasks/javah.html">javah</a>
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.emacs</code></td>
   <td>boolean (default false)</td>
   <td>Enable emacs-compatible error messages.
       see <a href="CoreTasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.fulldepend</code></td>
   <td>boolean (default false)</td>
   <td>Enable full dependency checking
       see <a href="CoreTasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.jvc.extensions</code></td>
   <td>boolean (default true)</td>
   <td>enable Microsoft extensions of their java compiler
       see <a href="CoreTasks/javac.html">javac</a> "Jvc Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.pedantic</code></td>
   <td>boolean (default false)</td>
   <td>Enable pedantic warnings.
       see <a href="CoreTasks/javac.html">javac</a> "Jikes Notes"
   </td>
 </tr>
 <tr>
   <td><code>build.compiler.warnings</code></td>
   <td>Deprecated flag</td>
   <td> see <a href="CoreTasks/javac.html">javac</a> "Jikes Notes" </td>
 </tr>
 <tr>
   <td><code>build.rmic</code></td>
   <td>name</td>
   <td>control the <a href="CoreTasks/rmic.html">rmic</a> compiler </td>
 </tr>
 <tr>
   <td><code>build.sysclasspath</code></td>
   <td>see <a href="sysclasspath.html">its dedicated page</a>, no
     default value</td>
   <td>see <a href="sysclasspath.html">its dedicated page</a></td>
-  </td>
 </tr>
 <tr>
   <td><code>file.encoding</code></td>
   <td>name of a supported character set (e.g. UTF-8, ISO-8859-1, US-ASCII)</td>
   <td>use as default character set of email messages; use as default for source-, dest- and bundleencoding
       in <a href="OptionalTasks/translate.html">translate</a> <br>
       see JavaDoc of <a target="_blank" href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">java.nio.charset.Charset</a>
       for more information about character sets (not used in Ant, but has nice docs).
   </td>
 </tr>
 <tr>
   <td><code>jikes.class.path</code></td>
   <td>path</td>
   <td>The specified path is added to the classpath if jikes is used as compiler.</td>
 </tr>
 <tr>
   <td><code>MailLogger.properties.file, MailLogger.*</code></td>
   <td>filename (optional, defaults derived from Project instance)</td>
   <td>Name of the file holding properties for sending emails by the
       <a href="listeners.html#MailLogger">MailLogger</a>. Override properties set
       inside the buildfile or via command line.
   </td>
 </tr>
 <tr>
   <td><code>org.apache.tools.ant.ProjectHelper</code></td>
   <!-- add the blank after the slash, so the browser can do a line break -->
   <td>classname (optional, default 'org.apache.tools.ant.ProjectHelper')</td>
   <td>specifies the classname to use as ProjectHelper. The class must extend
       org.apache.tools.ant.ProjectHelper.
   </td>
 </tr>
 <tr>
   <td><code>p4.port, p4.client, p4.user</code></td>
   <td>several formats</td>
   <td>Specify defaults for port-, client- and user-setting of the
       <a href="OptionalTasks/perforce.html">perforce</a> tasks.
   </td>
 </tr>
 <tr>
-  <td><code>websphere.home
+  <td><code>websphere.home</code></td>
   <td>path</td>
   <td>Points to home directory of websphere.
       see <a href="OptionalTasks/ejb.html#ejbjar_websphere">EJB Tasks</a>
   </td>
 </tr>
 <tr>
-  <td><code>XmlLogger.file
+  <td><code>XmlLogger.file</code></td>
   <td>filename (default 'log.xml')</td>
   <td>Name for the logfile for <a href="listeners.html#MailLogger">MailLogger</a>.
   </td>
 </tr>
+<tr>
+  <td><code>ant.project-helper-repo.debug</code></td>
+  <td>boolean (default 'false')</td>
+  <td>Set it to true to enable debuging with Ant's
+  <a href="projecthelper.html#repository">ProjectHelper internal repository</a>.
+  </td>
+</tr>
 </table>
 
 <p>
 If new properties get added (it happens), expect them to appear under the
 "ant." and "org.apache.tools.ant" prefixes, unless the developers have a
 very good reason to use another prefix. Accordingly, please avoid using
 properties that begin with these prefixes. This protects you from future
 Ant releases breaking your build file.
 </p>
 <h3>return code</h3>
 <p>the ant start up scripts (in their Windows and Unix version) return
 the return code of the java program. So a successful build returns 0,
 failed builds return other values.
 </p>
 
 <h2><a name="cygwin">Cygwin Users</a></h2>
 <p>The Unix launch script that come with Ant works correctly with Cygwin. You
 should not have any problems launching Ant from the Cygwin shell. It is
 important to note, however, that once Ant is running it is part of the JDK
 which operates as a native Windows application. The JDK is not a Cygwin
 executable, and it therefore has no knowledge of Cygwin paths, etc. In
 particular when using the <code>&lt;exec&gt;</code> task, executable names such
 as &quot;/bin/sh&quot; will not work, even though these work from the Cygwin
 shell from which Ant was launched. You can use an executable name such as
 &quot;sh&quot; and rely on that command being available in the Windows path.
 </p>
 
 <h2><a name="os2">OS/2 Users</a></h2>
 <p>The OS/2 launch script was developed to perform complex tasks. It has two parts:
 <code>ant.cmd</code> which calls Ant and <code>antenv.cmd</code> which sets the environment for Ant.
 Most often you will just call <code>ant.cmd</code> using the same command line options as described
 above. The behaviour can be modified by a number of ways explained below.</p>
 
 <p>Script <code>ant.cmd</code> first verifies whether the Ant environment is set correctly. The
 requirements are:</p>
 <ol>
 <li>Environment variable <code>JAVA_HOME</code> is set.</li>
 <li>Environment variable <code>ANT_HOME</code> is set.</li>
 <li>Environment variable <code>CLASSPATH</code> is set and contains at least one element from
 <code>JAVA_HOME</code> and at least one element from <code>ANT_HOME</code>.</li>
 </ol>
 
 <p>If any of these conditions is violated, script <code>antenv.cmd</code> is called. This script
 first invokes configuration scripts if there exist: the system-wide configuration
 <code>antconf.cmd</code> from the <code>%ETC%</code> directory and then the user configuration
 <code>antrc.cmd</code> from the <code>%HOME%</code> directory. At this moment both
 <code>JAVA_HOME</code> and <code>ANT_HOME</code> must be defined because <code>antenv.cmd</code>
 now adds <code>classes.zip</code> or <code>tools.jar</code> (depending on version of JVM) and
 everything from <code>%ANT_HOME%\lib</code> except <code>ant-*.jar</code> to
 <code>CLASSPATH</code>. Finally <code>ant.cmd</code> calls per-directory configuration
 <code>antrc.cmd</code>. All settings made by <code>ant.cmd</code> are local and are undone when the
 script ends. The settings made by <code>antenv.cmd</code> are persistent during the lifetime of the
 shell (of course unless called automatically from <code>ant.cmd</code>). It is thus possible to call
 <code>antenv.cmd</code> manually and modify some settings before calling <code>ant.cmd</code>.</p>
 
 <p>Scripts <code>envset.cmd</code> and <code>runrc.cmd</code> perform auxiliary tasks. All scripts
 have some documentation inside.</p>
 
 <h2><a name="background">Running Ant as a background process on
     Unix(-like) systems</a></h2>
 
 <p>If you start Ant as a background process (like in <code>ant
-    &</code>) and the build process creates another process, Ant will
+    &amp;</code>) and the build process creates another process, Ant will
     immediately try to read from standard input, which in turn will
     most likely suspend the process.  In order to avoid this, you must
     redirect Ant's standard input or explicitly provide input to each
     spawned process via the input related attributes of the
     corresponding tasks.</p>
 
 <p>Tasks that create such new processes
   include <code>&lt;exec&gt;</code>, <code>&lt;apply&gt;</code>
   or <code>&lt;java&gt;</code> when the <code>fork</code> attribute is
   <code>true</code>.</p>
 
 <h2><a name="viajava">Running Ant via Java</a></h2>
 <p>If you have installed Ant in the do-it-yourself way, Ant can be started
 from one of two entry points:</p>
 <blockquote>
   <pre>java -Dant.home=c:\ant org.apache.tools.ant.Main [options] [target]</pre>
 </blockquote>
 
 <blockquote>
   <pre>java -Dant.home=c:\ant org.apache.tools.ant.launch.Launcher [options] [target]</pre>
 </blockquote>
 
 <p>
 The first method runs Ant's traditional entry point. The second method uses
 the Ant Launcher introduced in Ant 1.6. The former method does not support
 the -lib option and all required classes are loaded from the CLASSPATH. You must
 ensure that all required jars are available. At a minimum the CLASSPATH should
 include:
 </p>
 
 <ul>
 <li><code>ant.jar</code> and <code>ant-launcher.jar</code></li>
 <li>jars/classes for your XML parser</li>
 <li>the JDK's required jar/zip files</li>
 </ul>
 
 <p>
 The latter method supports the -lib, -nouserlib, -noclasspath options and will
     load jars from the specified ANT_HOME. You should start the latter with the most minimal
 classpath possible, generally just the ant-launcher.jar.
 </p>
 
 <a name="viaant"/>
 
 Ant can be started in Ant via the <code>&lt;java&gt;</code> command.
 Here is an example:
 
 <pre>
 &lt;java
         classname="org.apache.tools.ant.launch.Launcher"
         fork="true"
         failonerror="true"
         dir="${sub.builddir}"
         timeout="4000000"
         taskname="startAnt"
 &gt;
     &lt;classpath&gt;
         &lt;pathelement location="${ant.home}/lib/ant-launcher.jar"/&gt;
     &lt;/classpath&gt;
     &lt;arg value="-buildfile"/&gt;
     &lt;arg file="${sub.buildfile}"/&gt;
     &lt;arg value="-Dthis=this"/&gt;
     &lt;arg value="-Dthat=that"/&gt;
     &lt;arg value="-Dbasedir=${sub.builddir}"/&gt;
     &lt;arg value="-Dthe.other=the.other"/&gt;
     &lt;arg value="${sub.target}"/&gt;
 &lt;/java&gt;
 </pre>
 <br>
 
 
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/Main.java b/src/main/org/apache/tools/ant/Main.java
index 97a1e9046..820ebb95a 100644
--- a/src/main/org/apache/tools/ant/Main.java
+++ b/src/main/org/apache/tools/ant/Main.java
@@ -1,1165 +1,1189 @@
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
 import java.io.FileInputStream;
+import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.launch.AntMain;
 import org.apache.tools.ant.util.ClasspathUtils;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.ProxySetup;
 
 
 /**
  * Command line entry point into Ant. This class is entered via the
  * canonical `public static void main` entry point and reads the
  * command line arguments. It then assembles and executes an Ant
  * project.
  * <p>
  * If you integrating Ant into some other tool, this is not the class
  * to use as an entry point. Please see the source code of this
  * class to see how it manipulates the Ant project classes.
  *
  */
 public class Main implements AntMain {
 
     /**
      * A Set of args are are handled by the launcher and should
      * not be seen by Main.
      */
     private static final Set LAUNCH_COMMANDS = new HashSet();
     static {
         LAUNCH_COMMANDS.add("-lib");
         LAUNCH_COMMANDS.add("-cp");
         LAUNCH_COMMANDS.add("-noclasspath");
         LAUNCH_COMMANDS.add("--noclasspath");
         LAUNCH_COMMANDS.add("-nouserlib");
         LAUNCH_COMMANDS.add("-main");
     }
 
     /** The default build file name. {@value} */
     public static final String DEFAULT_BUILD_FILENAME = "build.xml";
 
     /** Our current message output status. Follows Project.MSG_XXX. */
     private int msgOutputLevel = Project.MSG_INFO;
 
     /** File that we are using for configuration. */
     private File buildFile; /* null */
 
     /** Stream to use for logging. */
     private static PrintStream out = System.out;
 
     /** Stream that we are using for logging error messages. */
     private static PrintStream err = System.err;
 
     /** The build targets. */
     private Vector targets = new Vector();
 
     /** Set of properties that can be used by tasks. */
     private Properties definedProps = new Properties();
 
     /** Names of classes to add as listeners to project. */
     private Vector listeners = new Vector(1);
 
     /** File names of property files to load on startup. */
     private Vector propertyFiles = new Vector(1);
 
     /** Indicates whether this build is to support interactive input */
     private boolean allowInput = true;
 
     /** keep going mode */
     private boolean keepGoingMode = false;
 
     /**
      * The Ant logger class. There may be only one logger. It will have
      * the right to use the 'out' PrintStream. The class must implements the
      * BuildLogger interface.
      */
     private String loggerClassname = null;
 
     /**
      * The Ant InputHandler class.  There may be only one input
      * handler.
      */
     private String inputHandlerClassname = null;
 
     /**
      * Whether or not output to the log is to be unadorned.
      */
     private boolean emacsMode = false;
 
     /**
      * Whether or not this instance has successfully been
      * constructed and is ready to run.
      */
     private boolean readyToRun = false;
 
     /**
      * Whether or not we should only parse and display the project help
      * information.
      */
     private boolean projectHelp = false;
 
     /**
      * Whether or not a logfile is being used. This is used to
      * check if the output streams must be closed.
      */
     private static boolean isLogFileUsed = false;
 
     /**
      * optional thread priority
      */
     private Integer threadPriority = null;
 
     /**
      * proxy flag: default is false
      */
     private boolean proxy = false;
 
     /**
      * Prints the message of the Throwable if it (the message) is not
      * <code>null</code>.
      *
      * @param t Throwable to print the message of.
      *          Must not be <code>null</code>.
      */
     private static void printMessage(Throwable t) {
         String message = t.getMessage();
         if (message != null) {
             System.err.println(message);
         }
     }
 
     /**
      * Creates a new instance of this class using the
      * arguments specified, gives it any extra user properties which have been
      * specified, and then runs the build using the classloader provided.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      * @param additionalUserProperties Any extra properties to use in this
      *        build. May be <code>null</code>, which is the equivalent to
      *        passing in an empty set of properties.
      * @param coreLoader Classloader used for core classes. May be
      *        <code>null</code> in which case the system classloader is used.
      */
     public static void start(String[] args, Properties additionalUserProperties,
                              ClassLoader coreLoader) {
         Main m = new Main();
         m.startAnt(args, additionalUserProperties, coreLoader);
     }
 
     /**
      * Start Ant
      * @param args command line args
      * @param additionalUserProperties properties to set beyond those that
      *        may be specified on the args list
      * @param coreLoader - not used
      *
      * @since Ant 1.6
      */
     public void startAnt(String[] args, Properties additionalUserProperties,
                          ClassLoader coreLoader) {
 
         try {
             Diagnostics.validateVersion();
             processArgs(args);
         } catch (Throwable exc) {
             handleLogfile();
             printMessage(exc);
             exit(1);
             return;
         }
 
         if (additionalUserProperties != null) {
             for (Enumeration e = additionalUserProperties.keys();
                     e.hasMoreElements();) {
                 String key = (String) e.nextElement();
                 String property = additionalUserProperties.getProperty(key);
                 definedProps.put(key, property);
             }
         }
 
         // expect the worst
         int exitCode = 1;
         try {
             try {
                 runBuild(coreLoader);
                 exitCode = 0;
             } catch (ExitStatusException ese) {
                 exitCode = ese.getStatus();
                 if (exitCode != 0) {
                     throw ese;
                 }
             }
         } catch (BuildException be) {
             if (err != System.err) {
                 printMessage(be);
             }
         } catch (Throwable exc) {
             exc.printStackTrace();
             printMessage(exc);
         } finally {
             handleLogfile();
         }
         exit(exitCode);
     }
 
     /**
      * This operation is expected to call {@link System#exit(int)}, which
      * is what the base version does.
      * However, it is possible to do something else.
      * @param exitCode code to exit with
      */
     protected void exit(int exitCode) {
         System.exit(exitCode);
     }
 
     /**
      * Close logfiles, if we have been writing to them.
      *
      * @since Ant 1.6
      */
     private static void handleLogfile() {
         if (isLogFileUsed) {
             FileUtils.close(out);
             FileUtils.close(err);
         }
     }
 
     /**
      * Command line entry point. This method kicks off the building
      * of a project object and executes a build using either a given
      * target or the default target.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      */
     public static void main(String[] args) {
         start(args, null, null);
     }
 
     /**
      * Constructor used when creating Main for later arg processing
      * and startup
      */
     public Main() {
     }
 
     /**
      * Sole constructor, which parses and deals with command line
      * arguments.
      *
      * @param args Command line arguments. Must not be <code>null</code>.
      *
      * @exception BuildException if the specified build file doesn't exist
      *                           or is a directory.
      *
      * @deprecated since 1.6.x
      */
     protected Main(String[] args) throws BuildException {
         processArgs(args);
     }
 
     /**
      * Process command line arguments.
      * When ant is started from Launcher, launcher-only arguments do not get
      * passed through to this routine.
      *
      * @param args the command line arguments.
      *
      * @since Ant 1.6
      */
     private void processArgs(String[] args) {
         String searchForThis = null;
+        boolean searchForFile = false;
         PrintStream logTo = null;
 
         // cycle through given args
 
         boolean justPrintUsage = false;
         boolean justPrintVersion = false;
         boolean justPrintDiagnostics = false;
 
         for (int i = 0; i < args.length; i++) {
             String arg = args[i];
 
             if (arg.equals("-help") || arg.equals("-h")) {
                 justPrintUsage = true;
             } else if (arg.equals("-version")) {
                 justPrintVersion = true;
             } else if (arg.equals("-diagnostics")) {
                 justPrintDiagnostics = true;
             } else if (arg.equals("-quiet") || arg.equals("-q")) {
                 msgOutputLevel = Project.MSG_WARN;
             } else if (arg.equals("-verbose") || arg.equals("-v")) {
                 msgOutputLevel = Project.MSG_VERBOSE;
             } else if (arg.equals("-debug") || arg.equals("-d")) {
                 msgOutputLevel = Project.MSG_DEBUG;
             } else if (arg.equals("-noinput")) {
                 allowInput = false;
             } else if (arg.equals("-logfile") || arg.equals("-l")) {
                 try {
                     File logFile = new File(args[i + 1]);
                     i++;
                     logTo = new PrintStream(new FileOutputStream(logFile));
                     isLogFileUsed = true;
                 } catch (IOException ioe) {
                     String msg = "Cannot write on the specified log file. "
                         + "Make sure the path exists and you have write "
                         + "permissions.";
                     throw new BuildException(msg);
                 } catch (ArrayIndexOutOfBoundsException aioobe) {
                     String msg = "You must specify a log file when "
                         + "using the -log argument";
                     throw new BuildException(msg);
                 }
             } else if (arg.equals("-buildfile") || arg.equals("-file")
                        || arg.equals("-f")) {
                 i = handleArgBuildFile(args, i);
             } else if (arg.equals("-listener")) {
                 i = handleArgListener(args, i);
             } else if (arg.startsWith("-D")) {
                 i = handleArgDefine(args, i);
             } else if (arg.equals("-logger")) {
                 i = handleArgLogger(args, i);
             } else if (arg.equals("-inputhandler")) {
                 i = handleArgInputHandler(args, i);
             } else if (arg.equals("-emacs") || arg.equals("-e")) {
                 emacsMode = true;
             } else if (arg.equals("-projecthelp") || arg.equals("-p")) {
                 // set the flag to display the targets and quit
                 projectHelp = true;
             } else if (arg.equals("-find") || arg.equals("-s")) {
+                searchForFile = true;
                 // eat up next arg if present, default to build.xml
                 if (i < args.length - 1) {
                     searchForThis = args[++i];
-                } else {
-                    searchForThis = DEFAULT_BUILD_FILENAME;
                 }
             } else if (arg.startsWith("-propertyfile")) {
                 i = handleArgPropertyFile(args, i);
             } else if (arg.equals("-k") || arg.equals("-keep-going")) {
                 keepGoingMode = true;
             } else if (arg.equals("-nice")) {
                 i = handleArgNice(args, i);
             } else if (LAUNCH_COMMANDS.contains(arg)) {
                 //catch script/ant mismatch with a meaningful message
                 //we could ignore it, but there are likely to be other
                 //version problems, so we stamp down on the configuration now
                 String msg = "Ant's Main method is being handed "
                         + "an option " + arg + " that is only for the launcher class."
                         + "\nThis can be caused by a version mismatch between "
                         + "the ant script/.bat file and Ant itself.";
                 throw new BuildException(msg);
             } else if (arg.equals("-autoproxy")) {
                 proxy = true;
             } else if (arg.startsWith("-")) {
                 // we don't have any more args to recognize!
                 String msg = "Unknown argument: " + arg;
                 System.err.println(msg);
                 printUsage();
                 throw new BuildException("");
             } else {
                 // if it's no other arg, it may be the target
                 targets.addElement(arg);
             }
         }
 
         if (msgOutputLevel >= Project.MSG_VERBOSE || justPrintVersion) {
             printVersion(msgOutputLevel);
         }
 
         if (justPrintUsage || justPrintVersion || justPrintDiagnostics) {
             if (justPrintUsage) {
                 printUsage();
             }
             if (justPrintDiagnostics) {
                 Diagnostics.doReport(System.out, msgOutputLevel);
             }
             return;
         }
 
         // if buildFile was not specified on the command line,
         if (buildFile == null) {
             // but -find then search for it
-            if (searchForThis != null) {
-                buildFile = findBuildFile(System.getProperty("user.dir"),
-                                          searchForThis);
+            if (searchForFile) {
+                if (searchForThis != null) {
+                    buildFile = findBuildFile(System.getProperty("user.dir"), searchForThis);
+                    if (buildFile == null) {
+                        throw new BuildException("Could not locate a build file!");
+                    }
+                } else {
+                    // no search file specified: so search an existing default file
+                    Iterator it = ProjectHelperRepository.getInstance().getHelpers();
+                    do {
+                        ProjectHelper helper = (ProjectHelper) it.next();
+                        searchForThis = helper.getDefaultBuildFile();
+                        if (msgOutputLevel >= Project.MSG_VERBOSE) {
+                            System.out.println("Searching the default build file: " + searchForThis);
+                        }
+                        buildFile = findBuildFile(System.getProperty("user.dir"), searchForThis);
+                    } while (buildFile == null && it.hasNext());
+                    if (buildFile == null) {
+                        throw new BuildException("Could not locate a build file!");
+                    }
+                }
             } else {
-                buildFile = new File(DEFAULT_BUILD_FILENAME);
+                // no build file specified: so search an existing default file
+                Iterator it = ProjectHelperRepository.getInstance().getHelpers();
+                do {
+                    ProjectHelper helper = (ProjectHelper) it.next();
+                    buildFile = new File(helper.getDefaultBuildFile());
+                    if (msgOutputLevel >= Project.MSG_VERBOSE) {
+                        System.out.println("Trying the default build file: " + buildFile);
+                    }
+                } while (!buildFile.exists() && it.hasNext());
             }
         }
 
         // make sure buildfile exists
         if (!buildFile.exists()) {
             System.out.println("Buildfile: " + buildFile + " does not exist!");
             throw new BuildException("Build failed");
         }
 
         // make sure it's not a directory (this falls into the ultra
         // paranoid lets check everything category
 
         if (buildFile.isDirectory()) {
             System.out.println("What? Buildfile: " + buildFile + " is a dir!");
             throw new BuildException("Build failed");
         }
 
         // Normalize buildFile for re-import detection
         buildFile =
             FileUtils.getFileUtils().normalize(buildFile.getAbsolutePath());
 
         // Load the property files specified by -propertyfile
         loadPropertyFiles();
 
         if (msgOutputLevel >= Project.MSG_INFO) {
             System.out.println("Buildfile: " + buildFile);
         }
 
         if (logTo != null) {
             out = logTo;
             err = logTo;
             System.setOut(out);
             System.setErr(err);
         }
         readyToRun = true;
     }
 
     // --------------------------------------------------------
     //    Methods for handling the command line arguments
     // --------------------------------------------------------
 
     /** Handle the -buildfile, -file, -f argument */
     private int handleArgBuildFile(String[] args, int pos) {
         try {
             buildFile = new File(
                 args[++pos].replace('/', File.separatorChar));
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must specify a buildfile when using the -buildfile argument");
         }
         return pos;
     }
 
     /** Handle -listener argument */
     private int handleArgListener(String[] args, int pos) {
         try {
             listeners.addElement(args[pos + 1]);
             pos++;
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             String msg = "You must specify a classname when "
                 + "using the -listener argument";
             throw new BuildException(msg);
         }
         return pos;
     }
 
     /** Handler -D argument */
     private int handleArgDefine(String[] args, int argPos) {
         /* Interestingly enough, we get to here when a user
          * uses -Dname=value. However, in some cases, the OS
          * goes ahead and parses this out to args
          *   {"-Dname", "value"}
          * so instead of parsing on "=", we just make the "-D"
          * characters go away and skip one argument forward.
          *
          * I don't know how to predict when the JDK is going
          * to help or not, so we simply look for the equals sign.
          */
         String arg = args[argPos];
         String name = arg.substring(2, arg.length());
         String value = null;
         int posEq = name.indexOf("=");
         if (posEq > 0) {
             value = name.substring(posEq + 1);
             name = name.substring(0, posEq);
         } else if (argPos < args.length - 1) {
             value = args[++argPos];
         } else {
             throw new BuildException("Missing value for property "
                                      + name);
         }
         definedProps.put(name, value);
         return argPos;
     }
 
     /** Handle the -logger argument. */
     private int handleArgLogger(String[] args, int pos) {
         if (loggerClassname != null) {
             throw new BuildException(
                 "Only one logger class may be specified.");
         }
         try {
             loggerClassname = args[++pos];
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must specify a classname when using the -logger argument");
         }
         return pos;
     }
 
     /** Handle the -inputhandler argument. */
     private int handleArgInputHandler(String[] args, int pos) {
         if (inputHandlerClassname != null) {
             throw new BuildException("Only one input handler class may "
                                      + "be specified.");
         }
         try {
             inputHandlerClassname = args[++pos];
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException("You must specify a classname when"
                                      + " using the -inputhandler"
                                      + " argument");
         }
         return pos;
     }
 
     /** Handle the -propertyfile argument. */
     private int handleArgPropertyFile(String[] args, int pos) {
         try {
             propertyFiles.addElement(args[++pos]);
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             String msg = "You must specify a property filename when "
                 + "using the -propertyfile argument";
             throw new BuildException(msg);
         }
         return pos;
     }
 
     /** Handle the -nice argument. */
     private int handleArgNice(String[] args, int pos) {
         try {
             threadPriority = Integer.decode(args[++pos]);
         } catch (ArrayIndexOutOfBoundsException aioobe) {
             throw new BuildException(
                 "You must supply a niceness value (1-10)"
                 + " after the -nice option");
         } catch (NumberFormatException e) {
             throw new BuildException("Unrecognized niceness value: "
                                      + args[pos]);
         }
 
         if (threadPriority.intValue() < Thread.MIN_PRIORITY
             || threadPriority.intValue() > Thread.MAX_PRIORITY) {
             throw new BuildException(
                 "Niceness value is out of the range 1-10");
         }
         return pos;
     }
 
     // --------------------------------------------------------
     //    other methods
     // --------------------------------------------------------
 
     /** Load the property files specified by -propertyfile */
     private void loadPropertyFiles() {
         for (int propertyFileIndex = 0;
              propertyFileIndex < propertyFiles.size();
              propertyFileIndex++) {
             String filename
                 = (String) propertyFiles.elementAt(propertyFileIndex);
             Properties props = new Properties();
             FileInputStream fis = null;
             try {
                 fis = new FileInputStream(filename);
                 props.load(fis);
             } catch (IOException e) {
                 System.out.println("Could not load property file "
                                    + filename + ": " + e.getMessage());
             } finally {
                 FileUtils.close(fis);
             }
 
             // ensure that -D properties take precedence
             Enumeration propertyNames = props.propertyNames();
             while (propertyNames.hasMoreElements()) {
                 String name = (String) propertyNames.nextElement();
                 if (definedProps.getProperty(name) == null) {
                     definedProps.put(name, props.getProperty(name));
                 }
             }
         }
     }
 
     /**
      * Helper to get the parent file for a given file.
      * <p>
      * Added to simulate File.getParentFile() from JDK 1.2.
      * @deprecated since 1.6.x
      *
      * @param file   File to find parent of. Must not be <code>null</code>.
      * @return       Parent file or null if none
      */
     private File getParentFile(File file) {
         File parent = file.getParentFile();
 
         if (parent != null && msgOutputLevel >= Project.MSG_VERBOSE) {
             System.out.println("Searching in " + parent.getAbsolutePath());
         }
 
         return parent;
     }
 
     /**
      * Search parent directories for the build file.
      * <p>
      * Takes the given target as a suffix to append to each
      * parent directory in search of a build file.  Once the
-     * root of the file-system has been reached an exception
-     * is thrown.
+     * root of the file-system has been reached <code>null</code>
+     * is returned.
      *
      * @param start  Leaf directory of search.
      *               Must not be <code>null</code>.
      * @param suffix  Suffix filename to look for in parents.
      *                Must not be <code>null</code>.
      *
-     * @return A handle to the build file if one is found
-     *
-     * @exception BuildException if no build file is found
+     * @return A handle to the build file if one is found, <code>null</code> if not
      */
-    private File findBuildFile(String start, String suffix)
-         throws BuildException {
+    private File findBuildFile(String start, String suffix) {
         if (msgOutputLevel >= Project.MSG_INFO) {
             System.out.println("Searching for " + suffix + " ...");
         }
 
         File parent = new File(new File(start).getAbsolutePath());
         File file = new File(parent, suffix);
 
         // check if the target file exists in the current directory
         while (!file.exists()) {
             // change to parent directory
             parent = getParentFile(parent);
 
             // if parent is null, then we are at the root of the fs,
             // complain that we can't find the build file.
             if (parent == null) {
-                throw new BuildException("Could not locate a build file!");
+                return null;
             }
 
             // refresh our file handle
             file = new File(parent, suffix);
         }
 
         return file;
     }
 
     /**
      * Executes the build. If the constructor for this instance failed
      * (e.g. returned after issuing a warning), this method returns
      * immediately.
      *
      * @param coreLoader The classloader to use to find core classes.
      *                   May be <code>null</code>, in which case the
      *                   system classloader is used.
      *
      * @exception BuildException if the build fails
      */
     private void runBuild(ClassLoader coreLoader) throws BuildException {
 
         if (!readyToRun) {
             return;
         }
 
         final Project project = new Project();
         project.setCoreLoader(coreLoader);
 
         Throwable error = null;
 
         try {
             addBuildListeners(project);
             addInputHandler(project);
 
             PrintStream savedErr = System.err;
             PrintStream savedOut = System.out;
             InputStream savedIn = System.in;
 
             // use a system manager that prevents from System.exit()
             SecurityManager oldsm = null;
             oldsm = System.getSecurityManager();
 
                 //SecurityManager can not be installed here for backwards
                 //compatibility reasons (PD). Needs to be loaded prior to
                 //ant class if we are going to implement it.
                 //System.setSecurityManager(new NoExitSecurityManager());
             try {
                 if (allowInput) {
                     project.setDefaultInputStream(System.in);
                 }
                 System.setIn(new DemuxInputStream(project));
                 System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
                 System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
 
 
                 if (!projectHelp) {
                     project.fireBuildStarted();
                 }
 
                 // set the thread priorities
                 if (threadPriority != null) {
                     try {
                         project.log("Setting Ant's thread priority to "
                                 + threadPriority, Project.MSG_VERBOSE);
                         Thread.currentThread().setPriority(threadPriority.intValue());
                     } catch (SecurityException swallowed) {
                         //we cannot set the priority here.
                         project.log("A security manager refused to set the -nice value");
                     }
                 }
 
 
 
                 project.init();
 
                 // set user-define properties
                 Enumeration e = definedProps.keys();
                 while (e.hasMoreElements()) {
                     String arg = (String) e.nextElement();
                     String value = (String) definedProps.get(arg);
                     project.setUserProperty(arg, value);
                 }
 
                 project.setUserProperty(MagicNames.ANT_FILE,
                                         buildFile.getAbsolutePath());
 
                 project.setKeepGoingMode(keepGoingMode);
                 if (proxy) {
                     //proxy setup if enabled
                     ProxySetup proxySetup = new ProxySetup(project);
                     proxySetup.enableProxies();
                 }
 
                 ProjectHelper.configureProject(project, buildFile);
 
                 if (projectHelp) {
                     printDescription(project);
                     printTargets(project, msgOutputLevel > Project.MSG_INFO);
                     return;
                 }
 
                 // make sure that we have a target to execute
                 if (targets.size() == 0) {
                     if (project.getDefaultTarget() != null) {
                         targets.addElement(project.getDefaultTarget());
                     }
                 }
 
                 project.executeTargets(targets);
             } finally {
                 // put back the original security manager
                 //The following will never eval to true. (PD)
                 if (oldsm != null) {
                     System.setSecurityManager(oldsm);
                 }
 
                 System.setOut(savedOut);
                 System.setErr(savedErr);
                 System.setIn(savedIn);
             }
         } catch (RuntimeException exc) {
             error = exc;
             throw exc;
         } catch (Error e) {
             error = e;
             throw e;
         } finally {
             if (!projectHelp) {
                 try {
                     project.fireBuildFinished(error);
                 } catch (Throwable t) {
                     // yes, I know it is bad style to catch Throwable,
                     // but if we don't, we lose valuable information
                     System.err.println("Caught an exception while logging the"
                                        + " end of the build.  Exception was:");
                     t.printStackTrace();
                     if (error != null) {
                         System.err.println("There has been an error prior to"
                                            + " that:");
                         error.printStackTrace();
                     }
                     throw new BuildException(t);
                 }
             } else if (error != null) {
                 project.log(error.toString(), Project.MSG_ERR);
             }
         }
     }
 
     /**
      * Adds the listeners specified in the command line arguments,
      * along with the default listener, to the specified project.
      *
      * @param project The project to add listeners to.
      *                Must not be <code>null</code>.
      */
     protected void addBuildListeners(Project project) {
 
         // Add the default listener
         project.addBuildListener(createLogger());
 
         for (int i = 0; i < listeners.size(); i++) {
             String className = (String) listeners.elementAt(i);
             BuildListener listener =
                     (BuildListener) ClasspathUtils.newInstance(className,
                             Main.class.getClassLoader(), BuildListener.class);
             project.setProjectReference(listener);
 
             project.addBuildListener(listener);
         }
     }
 
     /**
      * Creates the InputHandler and adds it to the project.
      *
      * @param project the project instance.
      *
      * @exception BuildException if a specified InputHandler
      *                           implementation could not be loaded.
      */
     private void addInputHandler(Project project) throws BuildException {
         InputHandler handler = null;
         if (inputHandlerClassname == null) {
             handler = new DefaultInputHandler();
         } else {
             handler = (InputHandler) ClasspathUtils.newInstance(
                     inputHandlerClassname, Main.class.getClassLoader(),
                     InputHandler.class);
             project.setProjectReference(handler);
         }
         project.setInputHandler(handler);
     }
 
     // XXX: (Jon Skeet) Any reason for writing a message and then using a bare
     // RuntimeException rather than just using a BuildException here? Is it
     // in case the message could end up being written to no loggers (as the
     // loggers could have failed to be created due to this failure)?
     /**
      * Creates the default build logger for sending build events to the ant
      * log.
      *
      * @return the logger instance for this build.
      */
     private BuildLogger createLogger() {
         BuildLogger logger = null;
         if (loggerClassname != null) {
             try {
                 logger = (BuildLogger) ClasspathUtils.newInstance(
                         loggerClassname, Main.class.getClassLoader(),
                         BuildLogger.class);
             } catch (BuildException e) {
                 System.err.println("The specified logger class "
                     + loggerClassname
                     + " could not be used because " + e.getMessage());
                 throw new RuntimeException();
             }
         } else {
             logger = new DefaultLogger();
         }
 
         logger.setMessageOutputLevel(msgOutputLevel);
         logger.setOutputPrintStream(out);
         logger.setErrorPrintStream(err);
         logger.setEmacsMode(emacsMode);
 
         return logger;
     }
 
     /**
      * Prints the usage information for this class to <code>System.out</code>.
      */
     private static void printUsage() {
         String lSep = System.getProperty("line.separator");
         StringBuffer msg = new StringBuffer();
         msg.append("ant [options] [target [target2 [target3] ...]]" + lSep);
         msg.append("Options: " + lSep);
         msg.append("  -help, -h              print this message" + lSep);
         msg.append("  -projecthelp, -p       print project help information" + lSep);
         msg.append("  -version               print the version information and exit" + lSep);
         msg.append("  -diagnostics           print information that might be helpful to" + lSep);
         msg.append("                         diagnose or report problems." + lSep);
         msg.append("  -quiet, -q             be extra quiet" + lSep);
         msg.append("  -verbose, -v           be extra verbose" + lSep);
         msg.append("  -debug, -d             print debugging information" + lSep);
         msg.append("  -emacs, -e             produce logging information without adornments"
                    + lSep);
         msg.append("  -lib <path>            specifies a path to search for jars and classes"
                    + lSep);
         msg.append("  -logfile <file>        use given file for log" + lSep);
         msg.append("    -l     <file>                ''" + lSep);
         msg.append("  -logger <classname>    the class which is to perform logging" + lSep);
         msg.append("  -listener <classname>  add an instance of class as a project listener"
                    + lSep);
         msg.append("  -noinput               do not allow interactive input" + lSep);
         msg.append("  -buildfile <file>      use given buildfile" + lSep);
         msg.append("    -file    <file>              ''" + lSep);
         msg.append("    -f       <file>              ''" + lSep);
         msg.append("  -D<property>=<value>   use value for given property" + lSep);
         msg.append("  -keep-going, -k        execute all targets that do not depend" + lSep);
         msg.append("                         on failed target(s)" + lSep);
         msg.append("  -propertyfile <name>   load all properties from file with -D" + lSep);
         msg.append("                         properties taking precedence" + lSep);
         msg.append("  -inputhandler <class>  the class which will handle input requests" + lSep);
         msg.append("  -find <file>           (s)earch for buildfile towards the root of" + lSep);
         msg.append("    -s  <file>           the filesystem and use it" + lSep);
         msg.append("  -nice  number          A niceness value for the main thread:" + lSep
                    + "                         1 (lowest) to 10 (highest); 5 is the default"
                    + lSep);
         msg.append("  -nouserlib             Run ant without using the jar files from" + lSep
                    + "                         ${user.home}/.ant/lib" + lSep);
         msg.append("  -noclasspath           Run ant without using CLASSPATH" + lSep);
         msg.append("  -autoproxy             Java1.5+: use the OS proxy settings"
                 + lSep);
         msg.append("  -main <class>          override Ant's normal entry point");
         System.out.println(msg.toString());
     }
 
     /**
      * Prints the Ant version information to <code>System.out</code>.
      *
      * @exception BuildException if the version information is unavailable
      */
     private static void printVersion(int logLevel) throws BuildException {
         System.out.println(getAntVersion());
     }
 
     /**
      * Cache of the Ant version information when it has been loaded.
      */
     private static String antVersion = null;
 
     /**
      * Returns the Ant version information, if available. Once the information
      * has been loaded once, it's cached and returned from the cache on future
      * calls.
      *
      * @return the Ant version information as a String
      *         (always non-<code>null</code>)
      *
      * @exception BuildException if the version information is unavailable
      */
     public static synchronized String getAntVersion() throws BuildException {
         if (antVersion == null) {
             try {
                 Properties props = new Properties();
                 InputStream in =
                     Main.class.getResourceAsStream("/org/apache/tools/ant/version.txt");
                 props.load(in);
                 in.close();
 
                 StringBuffer msg = new StringBuffer();
                 msg.append("Apache Ant version ");
                 msg.append(props.getProperty("VERSION"));
                 msg.append(" compiled on ");
                 msg.append(props.getProperty("DATE"));
                 antVersion = msg.toString();
             } catch (IOException ioe) {
                 throw new BuildException("Could not load the version information:"
                                          + ioe.getMessage());
             } catch (NullPointerException npe) {
                 throw new BuildException("Could not load the version information.");
             }
         }
         return antVersion;
     }
 
      /**
       * Prints the description of a project (if there is one) to
       * <code>System.out</code>.
       *
       * @param project The project to display a description of.
       *                Must not be <code>null</code>.
       */
     private static void printDescription(Project project) {
        if (project.getDescription() != null) {
           project.log(project.getDescription());
        }
     }
 
     /**
      * Targets in imported files with a project name
      * and not overloaded by the main build file will
      * be in the target map twice. This method
      * removes the duplicate target.
      * @param targets the targets to filter.
      * @return the filtered targets.
      */
     private static Map removeDuplicateTargets(Map targets) {
         Map locationMap = new HashMap();
         for (Iterator i = targets.entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             String name = (String) entry.getKey();
             Target target = (Target) entry.getValue();
             Target otherTarget =
                 (Target) locationMap.get(target.getLocation());
             // Place this entry in the location map if
             //  a) location is not in the map
             //  b) location is in map, but it's name is longer
             //     (an imported target will have a name. prefix)
             if (otherTarget == null
                 || otherTarget.getName().length() > name.length()) {
                 locationMap.put(
                     target.getLocation(), target); // Smallest name wins
             }
         }
         Map ret = new HashMap();
         for (Iterator i = locationMap.values().iterator(); i.hasNext();) {
             Target target = (Target) i.next();
             ret.put(target.getName(), target);
         }
         return ret;
     }
 
     /**
      * Prints a list of all targets in the specified project to
      * <code>System.out</code>, optionally including subtargets.
      *
      * @param project The project to display a description of.
      *                Must not be <code>null</code>.
      * @param printSubTargets Whether or not subtarget names should also be
      *                        printed.
      */
     private static void printTargets(Project project, boolean printSubTargets) {
         // find the target with the longest name
         int maxLength = 0;
         Map ptargets = removeDuplicateTargets(project.getTargets());
         String targetName;
         String targetDescription;
         Target currentTarget;
         // split the targets in top-level and sub-targets depending
         // on the presence of a description
         Vector topNames = new Vector();
         Vector topDescriptions = new Vector();
         Vector subNames = new Vector();
 
         for (Iterator i = ptargets.values().iterator(); i.hasNext();) {
             currentTarget = (Target) i.next();
             targetName = currentTarget.getName();
             if (targetName.equals("")) {
                 continue;
             }
             targetDescription = currentTarget.getDescription();
             // maintain a sorted list of targets
             if (targetDescription == null) {
                 int pos = findTargetPosition(subNames, targetName);
                 subNames.insertElementAt(targetName, pos);
             } else {
                 int pos = findTargetPosition(topNames, targetName);
                 topNames.insertElementAt(targetName, pos);
                 topDescriptions.insertElementAt(targetDescription, pos);
                 if (targetName.length() > maxLength) {
                     maxLength = targetName.length();
                 }
             }
         }
 
         printTargets(project, topNames, topDescriptions, "Main targets:",
                      maxLength);
         //if there were no main targets, we list all subtargets
         //as it means nothing has a description
         if (topNames.size() == 0) {
             printSubTargets = true;
         }
         if (printSubTargets) {
             printTargets(project, subNames, null, "Other targets:", 0);
         }
 
         String defaultTarget = project.getDefaultTarget();
         if (defaultTarget != null && !"".equals(defaultTarget)) {
             // shouldn't need to check but...
             project.log("Default target: " + defaultTarget);
         }
     }
 
     /**
      * Searches for the correct place to insert a name into a list so as
      * to keep the list sorted alphabetically.
      *
      * @param names The current list of names. Must not be <code>null</code>.
      * @param name  The name to find a place for.
      *              Must not be <code>null</code>.
      *
      * @return the correct place in the list for the given name
      */
     private static int findTargetPosition(Vector names, String name) {
         int res = names.size();
         for (int i = 0; i < names.size() && res == names.size(); i++) {
             if (name.compareTo((String) names.elementAt(i)) < 0) {
                 res = i;
             }
         }
         return res;
     }
 
     /**
      * Writes a formatted list of target names to <code>System.out</code>
      * with an optional description.
      *
      *
      * @param project the project instance.
      * @param names The names to be printed.
      *              Must not be <code>null</code>.
      * @param descriptions The associated target descriptions.
      *                     May be <code>null</code>, in which case
      *                     no descriptions are displayed.
      *                     If non-<code>null</code>, this should have
      *                     as many elements as <code>names</code>.
      * @param heading The heading to display.
      *                Should not be <code>null</code>.
      * @param maxlen The maximum length of the names of the targets.
      *               If descriptions are given, they are padded to this
      *               position so they line up (so long as the names really
      *               <i>are</i> shorter than this).
      */
     private static void printTargets(Project project, Vector names,
                                      Vector descriptions, String heading,
                                      int maxlen) {
         // now, start printing the targets and their descriptions
         String lSep = System.getProperty("line.separator");
         // got a bit annoyed that I couldn't find a pad function
         String spaces = "    ";
         while (spaces.length() <= maxlen) {
             spaces += spaces;
         }
         StringBuffer msg = new StringBuffer();
         msg.append(heading + lSep + lSep);
         for (int i = 0; i < names.size(); i++) {
             msg.append(" ");
             msg.append(names.elementAt(i));
             if (descriptions != null) {
                 msg.append(
                     spaces.substring(0, maxlen - ((String) names.elementAt(i)).length() + 2));
                 msg.append(descriptions.elementAt(i));
             }
             msg.append(lSep);
         }
         project.log(msg.toString(), Project.MSG_WARN);
     }
 }
diff --git a/src/main/org/apache/tools/ant/ProjectHelper.java b/src/main/org/apache/tools/ant/ProjectHelper.java
index fc49c2cf6..f03a23dbd 100644
--- a/src/main/org/apache/tools/ant/ProjectHelper.java
+++ b/src/main/org/apache/tools/ant/ProjectHelper.java
@@ -1,621 +1,538 @@
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
 
-import java.io.BufferedReader;
 import java.io.File;
-import java.io.InputStream;
-import java.io.InputStreamReader;
 import java.net.URL;
 import java.util.Hashtable;
 import java.util.Locale;
 import java.util.Vector;
 
-import org.xml.sax.AttributeList;
-
-import org.apache.tools.ant.helper.ProjectHelper2;
 import org.apache.tools.ant.util.LoaderUtils;
+import org.xml.sax.AttributeList;
 
 /**
  * Configures a Project (complete with Targets and Tasks) based on
- * a XML build file. It'll rely on a plugin to do the actual processing
- * of the xml file.
- *
+ * a build file. It'll rely on a plugin to do the actual processing
+ * of the file.
+ * <p>
  * This class also provide static wrappers for common introspection.
- *
- * All helper plugins must provide backward compatibility with the
- * original ant patterns, unless a different behavior is explicitly
- * specified. For example, if namespace is used on the &lt;project&gt; tag
- * the helper can expect the entire build file to be namespace-enabled.
- * Namespaces or helper-specific tags can provide meta-information to
- * the helper, allowing it to use new ( or different policies ).
- *
- * However, if no namespace is used the behavior should be exactly
- * identical with the default helper.
- *
  */
 public class ProjectHelper {
     /** The URI for ant name space */
     public static final String ANT_CORE_URI    = "antlib:org.apache.tools.ant";
 
     /** The URI for antlib current definitions */
     public static final String ANT_CURRENT_URI      = "ant:current";
 
     /** The URI for defined types/tasks - the format is antlib:<package> */
     public static final String ANTLIB_URI     = "antlib:";
 
     /** Polymorphic attribute  */
     public static final String ANT_TYPE = "ant-type";
 
     /**
      * Name of JVM system property which provides the name of the
      * ProjectHelper class to use.
      */
     public static final String HELPER_PROPERTY = MagicNames.PROJECT_HELPER_CLASS;
 
     /**
      * The service identifier in jars which provide Project Helper
      * implementations.
      */
     public static final String SERVICE_ID = MagicNames.PROJECT_HELPER_SERVICE;
 
     /**
      * name of project helper reference that we add to a project
      */
     public static final String PROJECTHELPER_REFERENCE = MagicNames.REFID_PROJECT_HELPER;
 
     /**
      * Configures the project with the contents of the specified XML file.
      *
      * @param project The project to configure. Must not be <code>null</code>.
      * @param buildFile An XML file giving the project's configuration.
      *                  Must not be <code>null</code>.
      *
      * @exception BuildException if the configuration is invalid or cannot be read
      */
     public static void configureProject(Project project, File buildFile) throws BuildException {
-        ProjectHelper helper = ProjectHelper.getProjectHelper();
+        ProjectHelper helper = ProjectHelperRepository.getInstance().getProjectHelper(buildFile);
         project.addReference(PROJECTHELPER_REFERENCE, helper);
         helper.parse(project, buildFile);
     }
 
     /** Default constructor */
     public ProjectHelper() {
     }
 
     // -------------------- Common properties  --------------------
     // The following properties are required by import ( and other tasks
     // that read build files using ProjectHelper ).
 
     private Vector importStack = new Vector();
 
     /**
      *  Import stack.
      *  Used to keep track of imported files. Error reporting should
      *  display the import path.
      *
      * @return the stack of import source objects.
      */
     public Vector getImportStack() {
         return importStack;
     }
 
     private final static ThreadLocal targetPrefix = new ThreadLocal() {
             protected Object initialValue() {
                 return (String) null;
             }
         };
 
     /**
      * The prefix to prepend to imported target names.
      *
      * <p>May be set by &lt;import&gt;'s as attribute.</p>
      *
      * @return the configured prefix or null
      *
      * @since Ant 1.8.0
      */
     public static String getCurrentTargetPrefix() {
         return (String) targetPrefix.get();
     }
 
     /**
      * Sets the prefix to prepend to imported target names.
      *
      * @since Ant 1.8.0
      */
     public static void setCurrentTargetPrefix(String prefix) {
         targetPrefix.set(prefix);
     }
 
     private final static ThreadLocal prefixSeparator = new ThreadLocal() {
             protected Object initialValue() {
                 return ".";
             }
         };
 
     /**
      * The separator between the prefix and the target name.
      *
      * <p>May be set by &lt;import&gt;'s prefixSeperator attribute.</p>
      *
      * @since Ant 1.8.0
      */
     public static String getCurrentPrefixSeparator() {
         return (String) prefixSeparator.get();
     }
 
     /**
      * Sets the separator between the prefix and the target name.
      *
      * @since Ant 1.8.0
      */
     public static void setCurrentPrefixSeparator(String sep) {
         prefixSeparator.set(sep);
     }
 
     private final static ThreadLocal inIncludeMode = new ThreadLocal() {
             protected Object initialValue() {
                 return Boolean.FALSE;
             }
         };
 
     /**
      * Whether the current file should be read in include as opposed
      * to import mode.
      *
      * <p>In include mode included targets are only known by their
      * prefixed names and their depends lists get rewritten so that
      * all dependencies get the prefix as well.</p>
      *
      * <p>In import mode imported targets are known by an adorned as
      * well as a prefixed name and the unadorned target may be
      * overwritten in the importing build file.  The depends list of
      * the imported targets is not modified at all.</p>
      *
      * @since Ant 1.8.0
      */
     public static boolean isInIncludeMode() {
         return inIncludeMode.get() == Boolean.TRUE;
     }
 
     /**
      * Sets whether the current file should be read in include as
      * opposed to import mode.
      *
      * @since Ant 1.8.0
      */
     public static void setInIncludeMode(boolean includeMode) {
         inIncludeMode.set(includeMode ? Boolean.TRUE : Boolean.FALSE);
     }
 
     // --------------------  Parse method  --------------------
     /**
      * Parses the project file, configuring the project as it goes.
      *
      * @param project The project for the resulting ProjectHelper to configure.
      *                Must not be <code>null</code>.
      * @param source The source for XML configuration. A helper must support
      *               at least File, for backward compatibility. Helpers may
      *               support URL, InputStream, etc or specialized types.
      *
      * @since Ant1.5
      * @exception BuildException if the configuration is invalid or cannot
      *                           be read
      */
     public void parse(Project project, Object source) throws BuildException {
         throw new BuildException("ProjectHelper.parse() must be implemented "
             + "in a helper plugin " + this.getClass().getName());
     }
 
     /**
-     * Discovers a project helper instance. Uses the same patterns
-     * as JAXP, commons-logging, etc: a system property, a JDK1.3
-     * service discovery, default.
-     *
-     * @return a ProjectHelper, either a custom implementation
-     * if one is available and configured, or the default implementation
-     * otherwise.
-     *
-     * @exception BuildException if a specified helper class cannot
-     * be loaded/instantiated.
-     */
-    public static ProjectHelper getProjectHelper() throws BuildException {
-        // Identify the class loader we will be using. Ant may be
-        // in a webapp or embedded in a different app
-        ProjectHelper helper = null;
-
-        // First, try the system property
-        String helperClass = System.getProperty(HELPER_PROPERTY);
-        try {
-            if (helperClass != null) {
-                helper = newHelper(helperClass);
-            }
-        } catch (SecurityException e) {
-            System.out.println("Unable to load ProjectHelper class \""
-                + helperClass + " specified in system property "
-                + HELPER_PROPERTY);
-        }
-
-        // A JDK1.3 'service' ( like in JAXP ). That will plug a helper
-        // automatically if in CLASSPATH, with the right META-INF/services.
-        if (helper == null) {
-            try {
-                ClassLoader classLoader = LoaderUtils.getContextClassLoader();
-                InputStream is = null;
-                if (classLoader != null) {
-                    is = classLoader.getResourceAsStream(SERVICE_ID);
-                }
-                if (is == null) {
-                    is = ClassLoader.getSystemResourceAsStream(SERVICE_ID);
-                }
-                if (is != null) {
-                    // This code is needed by EBCDIC and other strange systems.
-                    // It's a fix for bugs reported in xerces
-                    InputStreamReader isr;
-                    try {
-                        isr = new InputStreamReader(is, "UTF-8");
-                    } catch (java.io.UnsupportedEncodingException e) {
-                        isr = new InputStreamReader(is);
-                    }
-                    BufferedReader rd = new BufferedReader(isr);
-
-                    String helperClassName = rd.readLine();
-                    rd.close();
-
-                    if (helperClassName != null && !"".equals(helperClassName)) {
-                        helper = newHelper(helperClassName);
-                    }
-                }
-            } catch (Exception ex) {
-                System.out.println("Unable to load ProjectHelper from service " + SERVICE_ID);
-            }
-        }
-        return helper == null ? new ProjectHelper2() : helper;
-    }
-
-    /**
-     * Creates a new helper instance from the name of the class.
-     * It'll first try the thread class loader, then Class.forName()
-     * will load from the same loader that loaded this class.
-     *
-     * @param helperClass The name of the class to create an instance
-     *                    of. Must not be <code>null</code>.
-     *
-     * @return a new instance of the specified class.
-     *
-     * @exception BuildException if the class cannot be found or
-     * cannot be appropriate instantiated.
+     * Get the first project helper found in the classpath
+     * 
+     * @return an project helper, never <code>null</code>
+     * @see #getHelpers()
      */
-    private static ProjectHelper newHelper(String helperClass)
-        throws BuildException {
-        ClassLoader classLoader = LoaderUtils.getContextClassLoader();
-        try {
-            Class clazz = null;
-            if (classLoader != null) {
-                try {
-                    clazz = classLoader.loadClass(helperClass);
-                } catch (ClassNotFoundException ex) {
-                    // try next method
-                }
-            }
-            if (clazz == null) {
-                clazz = Class.forName(helperClass);
-            }
-            return ((ProjectHelper) clazz.newInstance());
-        } catch (Exception e) {
-            throw new BuildException(e);
-        }
+    public static ProjectHelper getProjectHelper() {
+        return (ProjectHelper) ProjectHelperRepository.getInstance().getHelpers().next();
     }
 
     /**
      * JDK1.1 compatible access to the context class loader. Cut & paste from JAXP.
      *
      * @deprecated since 1.6.x.
      *             Use LoaderUtils.getContextClassLoader()
      *
      * @return the current context class loader, or <code>null</code>
      * if the context class loader is unavailable.
      */
     public static ClassLoader getContextClassLoader() {
         return LoaderUtils.isContextLoaderAvailable() ? LoaderUtils.getContextClassLoader() : null;
     }
 
     // -------------------- Static utils, used by most helpers ----------------
 
     /**
      * Configures an object using an introspection handler.
      *
      * @param target The target object to be configured.
      *               Must not be <code>null</code>.
      * @param attrs  A list of attributes to configure within the target.
      *               Must not be <code>null</code>.
      * @param project The project containing the target.
      *                Must not be <code>null</code>.
      *
      * @deprecated since 1.6.x.
      *             Use IntrospectionHelper for each property.
      *
      * @exception BuildException if any of the attributes can't be handled by
      *                           the target
      */
     public static void configure(Object target, AttributeList attrs,
                                  Project project) throws BuildException {
         if (target instanceof TypeAdapter) {
             target = ((TypeAdapter) target).getProxy();
         }
         IntrospectionHelper ih = IntrospectionHelper.getHelper(project, target.getClass());
 
         for (int i = 0, length = attrs.getLength(); i < length; i++) {
             // reflect these into the target
             String value = replaceProperties(project, attrs.getValue(i), project.getProperties());
             try {
                 ih.setAttribute(project, target, attrs.getName(i).toLowerCase(Locale.US), value);
             } catch (BuildException be) {
                 // id attribute must be set externally
                 if (!attrs.getName(i).equals("id")) {
                     throw be;
                 }
             }
         }
     }
 
     /**
      * Adds the content of #PCDATA sections to an element.
      *
      * @param project The project containing the target.
      *                Must not be <code>null</code>.
      * @param target  The target object to be configured.
      *                Must not be <code>null</code>.
      * @param buf A character array of the text within the element.
      *            Will not be <code>null</code>.
      * @param start The start element in the array.
      * @param count The number of characters to read from the array.
      *
      * @exception BuildException if the target object doesn't accept text
      */
     public static void addText(Project project, Object target, char[] buf,
         int start, int count) throws BuildException {
         addText(project, target, new String(buf, start, count));
     }
 
     /**
      * Adds the content of #PCDATA sections to an element.
      *
      * @param project The project containing the target.
      *                Must not be <code>null</code>.
      * @param target  The target object to be configured.
      *                Must not be <code>null</code>.
      * @param text    Text to add to the target.
      *                May be <code>null</code>, in which case this
      *                method call is a no-op.
      *
      * @exception BuildException if the target object doesn't accept text
      */
     public static void addText(Project project, Object target, String text)
         throws BuildException {
 
         if (text == null) {
             return;
         }
         if (target instanceof TypeAdapter) {
             target = ((TypeAdapter) target).getProxy();
         }
         IntrospectionHelper.getHelper(project, target.getClass()).addText(project, target, text);
     }
 
     /**
      * Stores a configured child element within its parent object.
      *
      * @param project Project containing the objects.
      *                May be <code>null</code>.
      * @param parent  Parent object to add child to.
      *                Must not be <code>null</code>.
      * @param child   Child object to store in parent.
      *                Should not be <code>null</code>.
      * @param tag     Name of element which generated the child.
      *                May be <code>null</code>, in which case
      *                the child is not stored.
      */
     public static void storeChild(Project project, Object parent, Object child, String tag) {
         IntrospectionHelper ih = IntrospectionHelper.getHelper(project, parent.getClass());
         ih.storeElement(project, parent, child, tag);
     }
 
     /**
      * Replaces <code>${xxx}</code> style constructions in the given value with
      * the string value of the corresponding properties.
      *
      * @param project The project containing the properties to replace.
      *                Must not be <code>null</code>.
      *
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @return the original string with the properties replaced, or
      *         <code>null</code> if the original string is <code>null</code>.
      *
      * @deprecated since 1.6.x.
      *             Use project.replaceProperties().
      * @since 1.5
      */
      public static String replaceProperties(Project project, String value) throws BuildException {
         // needed since project properties are not accessible
          return project.replaceProperties(value);
      }
 
     /**
      * Replaces <code>${xxx}</code> style constructions in the given value
      * with the string value of the corresponding data types.
      *
      * @param project The container project. This is used solely for
      *                logging purposes. Must not be <code>null</code>.
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>, in which case this
      *              method returns immediately with no effect.
      * @param keys  Mapping (String to String) of property names to their
      *              values. Must not be <code>null</code>.
      *
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing
      *                           <code>}</code>
      * @return the original string with the properties replaced, or
      *         <code>null</code> if the original string is <code>null</code>.
      * @deprecated since 1.6.x.
      *             Use PropertyHelper.
      */
      public static String replaceProperties(Project project, String value, Hashtable keys)
              throws BuildException {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(project);
         return ph.replaceProperties(null, value, keys);
     }
 
     /**
      * Parses a string containing <code>${xxx}</code> style property
      * references into two lists. The first list is a collection
      * of text fragments, while the other is a set of string property names.
      * <code>null</code> entries in the first list indicate a property
      * reference from the second list.
      *
      * <p>As of Ant 1.8.0 this method is never invoked by any code
      * inside of Ant itself.</p>
      *
      * @param value     Text to parse. Must not be <code>null</code>.
      * @param fragments List to add text fragments to.
      *                  Must not be <code>null</code>.
      * @param propertyRefs List to add property names to.
      *                     Must not be <code>null</code>.
      *
      * @deprecated since 1.6.x.
      *             Use PropertyHelper.
      * @exception BuildException if the string contains an opening
      *                           <code>${</code> without a closing <code>}</code>
      */
     public static void parsePropertyString(String value, Vector fragments, Vector propertyRefs)
             throws BuildException {
         PropertyHelper.parsePropertyStringDefault(value, fragments, propertyRefs);
     }
 
     /**
      * Map a namespaced {uri,name} to an internal string format.
      * For BC purposes the names from the ant core uri will be
      * mapped to "name", other names will be mapped to
      * uri + ":" + name.
      * @param uri   The namepace URI
      * @param name  The localname
      * @return      The stringified form of the ns name
      */
     public static String genComponentName(String uri, String name) {
         if (uri == null || uri.equals("") || uri.equals(ANT_CORE_URI)) {
             return name;
         }
         return uri + ":" + name;
     }
 
     /**
      * extract a uri from a component name
      *
      * @param componentName  The stringified form for {uri, name}
      * @return               The uri or "" if not present
      */
     public static String extractUriFromComponentName(String componentName) {
         if (componentName == null) {
             return "";
         }
         int index = componentName.lastIndexOf(':');
         if (index == -1) {
             return "";
         }
         return componentName.substring(0, index);
     }
 
     /**
      * extract the element name from a component name
      *
      * @param componentName  The stringified form for {uri, name}
      * @return               The element name of the component
      */
     public static String extractNameFromComponentName(String componentName) {
         int index = componentName.lastIndexOf(':');
         if (index == -1) {
             return componentName;
         }
         return componentName.substring(index + 1);
     }
 
     /**
      * Add location to build exception.
      * @param ex the build exception, if the build exception
      *           does not include
      * @param newLocation the location of the calling task (may be null)
      * @return a new build exception based in the build exception with
      *         location set to newLocation. If the original exception
      *         did not have a location, just return the build exception
      */
     public static BuildException addLocationToBuildException(
             BuildException ex, Location newLocation) {
         if (ex.getLocation() == null || ex.getMessage() == null) {
             return ex;
         }
         String errorMessage
             = "The following error occurred while executing this line:"
             + System.getProperty("line.separator")
             + ex.getLocation().toString()
             + ex.getMessage();
         if (newLocation == null) {
             return new BuildException(errorMessage, ex);
         }
         return new BuildException(errorMessage, ex, newLocation);
     }
 
     /**
      * Whether this instance of ProjectHelper can parse an Antlib
      * descriptor given by the URL and return its content as an
      * UnknownElement ready to be turned into an Antlib task.
      *
      * <p>This method should not try to parse the content of the
      * descriptor, the URL is only given as an argument to allow
      * subclasses to decide whether they can support a given URL
      * scheme or not.</p>
      *
      * <p>Subclasses that return true in this method must also
      * override {@link #parseAntlibDescriptor
      * parseAntlibDescriptor}.</p>
      *
      * <p>This implementation returns false.</p>
      *
      * @since Ant 1.8.0
      */
     public boolean canParseAntlibDescriptor(URL url) {
         return false;
     }
 
     /**
      * Parse the given URL as an antlib descriptor and return the
      * content as something that can be turned into an Antlib task.
      *
      * @since ant 1.8.0
      */
     public UnknownElement parseAntlibDescriptor(Project containingProject,
                                                 URL source) {
         throw new BuildException("can't parse antlib descriptors");
     }
+
+    /**
+     * Check if the helper supports the kind of file. Some basic check on the
+     * extension's file should be done here.
+     * 
+     * @param buildFile
+     *            the file expected to be parsed (never <code>null</code>)
+     * @return true if the helper supports it
+     * @since Ant 1.8.0
+     */
+    public boolean supportsBuildFile(File buildFile) {
+        return true;
+    }
+
+    /**
+     * The file name of the build script to be parsed if none specified on the command line
+     * 
+     * @return the name of the default file (never <code>null</code>)
+     * @since Ant 1.8.0
+     */
+    public String getDefaultBuildFile() {
+        return Main.DEFAULT_BUILD_FILENAME;
+    }
 }
diff --git a/src/main/org/apache/tools/ant/ProjectHelperRepository.java b/src/main/org/apache/tools/ant/ProjectHelperRepository.java
new file mode 100644
index 000000000..b0d6dd22d
--- /dev/null
+++ b/src/main/org/apache/tools/ant/ProjectHelperRepository.java
@@ -0,0 +1,203 @@
+package org.apache.tools.ant;
+
+import java.io.BufferedReader;
+import java.io.File;
+import java.io.InputStream;
+import java.io.InputStreamReader;
+import java.net.URL;
+import java.util.ArrayList;
+import java.util.Enumeration;
+import java.util.Iterator;
+import java.util.List;
+
+import org.apache.tools.ant.helper.ProjectHelper2;
+import org.apache.tools.ant.util.LoaderUtils;
+
+/**
+ * Repository of {@link ProjectHelper} found in the classpath or via some System
+ * properties.
+ * <p>
+ * See the ProjectHelper documentation in the manual.
+ * 
+ * @since Ant 1.8.0
+ */
+public class ProjectHelperRepository {
+
+    private static final String DEBUG_PROJECT_HELPER_REPOSITORY = "ant.project-helper-repo.debug";
+
+    // The message log level is not accessible here because everything is instanciated statically
+    private static final boolean DEBUG = "true".equals(System.getProperty(DEBUG_PROJECT_HELPER_REPOSITORY));
+
+    private static ProjectHelperRepository instance = new ProjectHelperRepository();
+
+    private List/* <ProjectHelper> */helpers = new ArrayList();
+
+    public static ProjectHelperRepository getInstance() {
+        return instance;
+    }
+
+    private ProjectHelperRepository() {
+        collectProjectHelpers();
+    }
+
+    private void collectProjectHelpers() {
+        // First, try the system property
+        ProjectHelper projectHelper = getProjectHelperBySystemProperty();
+        registerProjectHelper(projectHelper);
+
+        // A JDK1.3 'service' ( like in JAXP ). That will plug a helper
+        // automatically if in CLASSPATH, with the right META-INF/services.
+        try {
+            ClassLoader classLoader = LoaderUtils.getContextClassLoader();
+            if (classLoader != null) {
+                Enumeration resources = classLoader.getResources(ProjectHelper.SERVICE_ID);
+                while (resources.hasMoreElements()) {
+                    URL resource = (URL) resources.nextElement();
+                    projectHelper = getProjectHelperBySerice(resource.openStream());
+                    registerProjectHelper(projectHelper);
+                }
+            }
+
+            InputStream systemResource = ClassLoader.getSystemResourceAsStream(ProjectHelper.SERVICE_ID);
+            if (systemResource != null) {
+                projectHelper = getProjectHelperBySerice(systemResource);
+                registerProjectHelper(projectHelper);
+            }
+        } catch (Exception e) {
+            System.err.println("Unable to load ProjectHelper from service "
+                    + ProjectHelper.SERVICE_ID + " (" + e.getClass().getName() + ": "
+                    + e.getMessage() + ")");
+            if (DEBUG) {
+                e.printStackTrace(System.err);
+            }
+        }
+
+        // last but not least, ant default project helper
+        projectHelper = new ProjectHelper2();
+        registerProjectHelper(projectHelper);
+    }
+
+    private void registerProjectHelper(ProjectHelper projectHelper) {
+        if (projectHelper == null) {
+            return;
+        }
+        if (DEBUG) {
+            System.out.println("ProjectHelper " +
+                    projectHelper.getClass().getName() + " registered.");
+        }
+        helpers.add(projectHelper);
+    }
+
+    private ProjectHelper getProjectHelperBySystemProperty() {
+        String helperClass = System.getProperty(ProjectHelper.HELPER_PROPERTY);
+        try {
+            if (helperClass != null) {
+                return newHelper(helperClass);
+            }
+        } catch (SecurityException e) {
+            System.err.println("Unable to load ProjectHelper class \""
+                    + helperClass + " specified in system property "
+                    + ProjectHelper.HELPER_PROPERTY + " (" + e.getMessage() + ")");
+            if (DEBUG) {
+                e.printStackTrace(System.err);
+            }
+        }
+        return null;
+    }
+
+    private ProjectHelper getProjectHelperBySerice(InputStream is) {
+        try {
+            // This code is needed by EBCDIC and other strange systems.
+            // It's a fix for bugs reported in xerces
+            InputStreamReader isr;
+            try {
+                isr = new InputStreamReader(is, "UTF-8");
+            } catch (java.io.UnsupportedEncodingException e) {
+                isr = new InputStreamReader(is);
+            }
+            BufferedReader rd = new BufferedReader(isr);
+
+            String helperClassName = rd.readLine();
+            rd.close();
+
+            if (helperClassName != null && !"".equals(helperClassName)) {
+                return newHelper(helperClassName);
+            }
+        } catch (Exception e) {
+            System.out.println("Unable to load ProjectHelper from service "
+                    + ProjectHelper.SERVICE_ID + " (" + e.getMessage() + ")");
+            if (DEBUG) {
+                e.printStackTrace(System.err);
+            }
+        }
+        return null;
+    }
+
+    /**
+     * Creates a new helper instance from the name of the class. It'll first try
+     * the thread class loader, then Class.forName() will load from the same
+     * loader that loaded this class.
+     * 
+     * @param helperClass
+     *            The name of the class to create an instance of. Must not be
+     *            <code>null</code>.
+     * 
+     * @return a new instance of the specified class.
+     * 
+     * @exception BuildException
+     *                if the class cannot be found or cannot be appropriate
+     *                instantiated.
+     */
+    private ProjectHelper newHelper(String helperClass) throws BuildException {
+        ClassLoader classLoader = LoaderUtils.getContextClassLoader();
+        try {
+            Class clazz = null;
+            if (classLoader != null) {
+                try {
+                    clazz = classLoader.loadClass(helperClass);
+                } catch (ClassNotFoundException ex) {
+                    // try next method
+                }
+            }
+            if (clazz == null) {
+                clazz = Class.forName(helperClass);
+            }
+            return ((ProjectHelper) clazz.newInstance());
+        } catch (Exception e) {
+            throw new BuildException(e);
+        }
+    }
+
+    /**
+     * Get the helper that will be able to parse the specified file. The helper
+     * will be chosen among the ones found in the classpath
+     * 
+     * @return the first ProjectHelper that fit the requirement (never <code>null</code>).
+     */
+    public ProjectHelper getProjectHelper(File buildFile) throws BuildException {
+        Iterator it = helpers.iterator();
+        while (it.hasNext()) {
+            ProjectHelper helper = (ProjectHelper) it.next();
+            if (helper.supportsBuildFile(buildFile)) {
+                if (DEBUG) {
+                    System.out.println("ProjectHelper "
+                            + helper.getClass().getName() + " selected for the file "
+                            + buildFile);
+                }
+                return helper;
+            }
+        }
+        throw new RuntimeException("BUG: at least the ProjectHelper2 should have supported the file " + buildFile);
+    }
+
+    /**
+     * Get an iterator on the list of project helpers configured. The iterator
+     * will always return at least one element as there will always be the
+     * default project helper configured.
+     * 
+     * @return an iterator of {@link ProjectHelper}
+     */
+    public Iterator getHelpers() {
+        return helpers.iterator();
+    }
+}
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index 58e165049..4f0dc08da 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,829 +1,842 @@
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
     private boolean useNativeBasedir = false;
 
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
     public void setUseNativeBasedir(boolean b) {
         useNativeBasedir = b;
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
         if (useNativeBasedir) {
             addAlmostAll(getProject().getUserProperties(), PropertyType.USER);
         } else {
             getProject().copyUserProperties(newProject);
         }
 
         if (!inheritAll) {
            // set Ant's built-in properties separately,
            // because they are not being inherited.
            newProject.initProperties();
 
         } else {
             // set all properties from calling project
             addAlmostAll(getProject().getProperties(), PropertyType.PLAIN);
         }
 
         Enumeration e = propertySets.elements();
         while (e.hasMoreElements()) {
             PropertySet ps = (PropertySet) e.nextElement();
             addAlmostAll(ps.getProperties(), PropertyType.PLAIN);
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
                 if (!useNativeBasedir) {
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
-                antFile = Main.DEFAULT_BUILD_FILENAME;
+                antFile = getDefaultBuildFile();
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
+     * Get the default build file name to use when launching the task.
+     * <p>
+     * This function may be overrided by providers of custom ProjectHelper so they can implement easily their sub
+     * launcher.
+     * 
+     * @return the name of the default file
+     * @since Ant 1.8.0
+     */
+    protected String getDefaultBuildFile() {
+        return Main.DEFAULT_BUILD_FILENAME;
+    }
+
+    /**
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
         if (useNativeBasedir) {
             addAlmostAll(getProject().getInheritedProperties(),
                          PropertyType.INHERITED);
         } else {
             getProject().copyInheritedProperties(newProject);
         }
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
      * @param the type of property to set (a plain Ant property, a
      * user property or an inherited property).
      * @since Ant 1.8.0
      */
     private void addAlmostAll(Hashtable props, PropertyType type) {
         Enumeration e = props.keys();
         while (e.hasMoreElements()) {
             String key = e.nextElement().toString();
             if (MagicNames.PROJECT_BASEDIR.equals(key)
                 || MagicNames.ANT_FILE.equals(key)) {
                 // basedir and ant.file get special treatment in execute()
                 continue;
             }
 
             String value = props.get(key).toString();
             if (type == PropertyType.PLAIN) {
                 // don't re-set user properties, avoid the warning message
                 if (newProject.getProperty(key) == null) {
                     // no user property
                     newProject.setNewProperty(key, value);
                 }
             } else if (type == PropertyType.USER) {
                 newProject.setUserProperty(key, value);
             } else if (type == PropertyType.INHERITED) {
                 newProject.setInheritedProperty(key, value);
             }
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
 
     private static final class PropertyType {
         private PropertyType() {}
         private static final PropertyType PLAIN = new PropertyType();
         private static final PropertyType INHERITED = new PropertyType();
         private static final PropertyType USER = new PropertyType();
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/SubAnt.java b/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
index 358bd5dad..f3c72e4f2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SubAnt.java
@@ -1,609 +1,622 @@
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
 
 import java.util.Vector;
 import java.util.Enumeration;
 
 import org.apache.tools.ant.Main;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.DirSet;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.FileList;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.types.ResourceCollection;
 
 import org.apache.tools.ant.taskdefs.Ant.TargetElement;
 
 
 /**
  * Calls a given target for all defined sub-builds. This is an extension
  * of ant for bulk project execution.
  * <p>
  * <h2> Use with directories </h2>
  * <p>
  * subant can be used with directory sets to execute a build from different directories.
  * 2 different options are offered
  * </p>
  * <ul>
  * <li>
  * run the same build file /somepath/otherpath/mybuild.xml
  * with different base directories use the genericantfile attribute
  * </li>
  * <li>if you want to run directory1/build.xml, directory2/build.xml, ....
  * use the antfile attribute. The base directory does not get set by the subant task in this case,
  * because you can specify it in each build file.
  * </li>
  * </ul>
  * @since Ant1.6
  * @ant.task name="subant" category="control"
  */
 public class SubAnt extends Task {
 
     private Path buildpath;
 
     private Ant ant = null;
     private String subTarget = null;
-    private String antfile = Main.DEFAULT_BUILD_FILENAME;
+    private String antfile = getDefaultBuildFile();
     private File genericantfile = null;
     private boolean verbose = false;
     private boolean inheritAll = false;
     private boolean inheritRefs = false;
     private boolean failOnError = true;
     private String output  = null;
 
     private Vector properties = new Vector();
     private Vector references = new Vector();
     private Vector propertySets = new Vector();
 
     /** the targets to call on the new project */
     private Vector/*<TargetElement>*/ targets = new Vector();
 
     /**
+     * Get the default build file name to use when launching the task.
+     * <p>
+     * This function may be overrided by providers of custom ProjectHelper so they can implement easily their sub
+     * launcher.
+     * 
+     * @return the name of the default file
+     * @since Ant 1.8.0
+     */
+    protected String getDefaultBuildFile() {
+        return Main.DEFAULT_BUILD_FILENAME;
+    }
+
+    /**
      * Pass output sent to System.out to the new project.
      *
      * @param output a line of output
      * @since Ant 1.6.2
      */
     public void handleOutput(String output) {
         if (ant != null) {
             ant.handleOutput(output);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * Process input into the ant task
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
      *
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6.2
      */
     public int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (ant != null) {
             return ant.handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @param output The output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.6.2
      */
     public void handleFlush(String output) {
         if (ant != null) {
             ant.handleFlush(output);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.6.2
      */
     public void handleErrorOutput(String output) {
         if (ant != null) {
             ant.handleErrorOutput(output);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.6.2
      */
     public void handleErrorFlush(String output) {
         if (ant != null) {
             ant.handleErrorFlush(output);
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     /**
      * Runs the various sub-builds.
      */
     public void execute() {
         if (buildpath == null) {
             throw new BuildException("No buildpath specified");
         }
 
         final String[] filenames = buildpath.list();
         final int count = filenames.length;
         if (count < 1) {
             log("No sub-builds to iterate on", Project.MSG_WARN);
             return;
         }
 /*
     //REVISIT: there must be cleaner way of doing this, if it is merited at all
         if (subTarget == null) {
             subTarget = getOwningTarget().getName();
         }
 */
         BuildException buildException = null;
         for (int i = 0; i < count; ++i) {
             File file = null;
             String subdirPath = null;
             Throwable thrownException = null;
             try {
                 File directory = null;
                 file = new File(filenames[i]);
                 if (file.isDirectory()) {
                     if (verbose) {
                         subdirPath = file.getPath();
                         log("Entering directory: " + subdirPath + "\n", Project.MSG_INFO);
                     }
                     if (genericantfile != null) {
                         directory = file;
                         file = genericantfile;
                     } else {
                         file = new File(file, antfile);
                     }
                 }
                 execute(file, directory);
                 if (verbose && subdirPath != null) {
                     log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                 }
             } catch (RuntimeException ex) {
                 if (!(getProject().isKeepGoingMode())) {
                     if (verbose && subdirPath != null) {
                         log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                     }
                     throw ex; // throw further
                 }
                 thrownException = ex;
             } catch (Throwable ex) {
                 if (!(getProject().isKeepGoingMode())) {
                     if (verbose && subdirPath != null) {
                         log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                     }
                     throw new BuildException(ex);
                 }
                 thrownException = ex;
             }
             if (thrownException != null) {
                 if (thrownException instanceof BuildException) {
                     log("File '" + file
                         + "' failed with message '"
                         + thrownException.getMessage() + "'.", Project.MSG_ERR);
                     // only the first build exception is reported
                     if (buildException == null) {
                         buildException = (BuildException) thrownException;
                     }
                 } else {
                     log("Target '" + file
                         + "' failed with message '"
                         + thrownException.getMessage() + "'.", Project.MSG_ERR);
                     thrownException.printStackTrace(System.err);
                     if (buildException == null) {
                         buildException =
                             new BuildException(thrownException);
                     }
                 }
                 if (verbose && subdirPath != null) {
                     log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                 }
             }
         }
         // check if one of the builds failed in keep going mode
         if (buildException != null) {
             throw buildException;
         }
     }
 
     /**
      * Runs the given target on the provided build file.
      *
      * @param  file the build file to execute
      * @param  directory the directory of the current iteration
      * @throws BuildException is the file cannot be found, read, is
      *         a directory, or the target called failed, but only if
      *         <code>failOnError</code> is <code>true</code>. Otherwise,
      *         a warning log message is simply output.
      */
     private void execute(File file, File directory)
                 throws BuildException {
         if (!file.exists() || file.isDirectory() || !file.canRead()) {
             String msg = "Invalid file: " + file;
             if (failOnError) {
                 throw new BuildException(msg);
             }
             log(msg, Project.MSG_WARN);
             return;
         }
 
         ant = createAntTask(directory);
         String antfilename = file.getAbsolutePath();
         ant.setAntfile(antfilename);
         for (int i = 0; i < targets.size(); i++) {
             TargetElement targetElement = (TargetElement) targets.get(i);
             ant.addConfiguredTarget(targetElement);
         }
 
         try {
             ant.execute();
         } catch (BuildException e) {
             if (failOnError) {
                 throw e;
             }
             log("Failure for target '" + subTarget
                + "' of: " +  antfilename + "\n"
                + e.getMessage(), Project.MSG_WARN);
         } catch (Throwable e) {
             if (failOnError) {
                 throw new BuildException(e);
             }
             log("Failure for target '" + subTarget
                 + "' of: " + antfilename + "\n"
                 + e.toString(),
                 Project.MSG_WARN);
         } finally {
             ant = null;
         }
     }
 
     /**
      * This method builds the file name to use in conjunction with directories.
      *
      * <p>Defaults to "build.xml".
      * If <code>genericantfile</code> is set, this attribute is ignored.</p>
      *
      * @param  antfile the short build file name. Defaults to "build.xml".
      */
     public void setAntfile(String antfile) {
         this.antfile = antfile;
     }
 
     /**
      * This method builds a file path to use in conjunction with directories.
      *
      * <p>Use <code>genericantfile</code>, in order to run the same build file
      * with different basedirs.</p>
      * If this attribute is set, <code>antfile</code> is ignored.
      *
      * @param afile (path of the generic ant file, absolute or relative to
      *               project base directory)
      * */
     public void setGenericAntfile(File afile) {
         this.genericantfile = afile;
     }
 
     /**
      * Sets whether to fail with a build exception on error, or go on.
      *
      * @param  failOnError the new value for this boolean flag.
      */
     public void setFailonerror(boolean failOnError) {
         this.failOnError = failOnError;
     }
 
     /**
      * The target to call on the different sub-builds. Set to "" to execute
      * the default target.
      * @param target the target
      * <p>
      */
     //     REVISIT: Defaults to the target name that contains this task if not specified.
     public void setTarget(String target) {
         this.subTarget = target;
     }
 
     /**
      * Add a target to this Ant invocation.
      * @param t the <code>TargetElement</code> to add.
      * @since Ant 1.7
      */
     public void addConfiguredTarget(TargetElement t) {
         String name = t.getName();
         if ("".equals(name)) {
             throw new BuildException("target name must not be empty");
         }
         targets.add(t);
     }
 
     /**
      * Enable/ disable verbose log messages showing when each sub-build path is entered/ exited.
      * The default value is "false".
      * @param on true to enable verbose mode, false otherwise (default).
      */
     public void setVerbose(boolean on) {
         this.verbose = on;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>output</code> attribute.
      *
      * @param  s the filename to write the output to.
      */
     public void setOutput(String s) {
         this.output = s;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>inheritall</code> attribute.
      *
      * @param  b the new value for this boolean flag.
      */
     public void setInheritall(boolean b) {
         this.inheritAll = b;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * <code>inheritrefs</code> attribute.
      *
      * @param  b the new value for this boolean flag.
      */
     public void setInheritrefs(boolean b) {
         this.inheritRefs = b;
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;property&gt;</code> element.
      *
      * @param  p the property to pass on explicitly to the sub-build.
      */
     public void addProperty(Property p) {
         properties.addElement(p);
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;reference&gt;</code> element.
      *
      * @param  r the reference to pass on explicitly to the sub-build.
      */
     public void addReference(Ant.Reference r) {
         references.addElement(r);
     }
 
     /**
      * Corresponds to <code>&lt;ant&gt;</code>'s
      * nested <code>&lt;propertyset&gt;</code> element.
      * @param ps the propertset
      */
     public void addPropertyset(PropertySet ps) {
         propertySets.addElement(ps);
     }
 
     /**
      * Adds a directory set to the implicit build path.
      * <p>
      * <em>Note that the directories will be added to the build path
      * in no particular order, so if order is significant, one should
      * use a file list instead!</em>
      *
      * @param  set the directory set to add.
      */
     public void addDirset(DirSet set) {
         add(set);
     }
 
     /**
      * Adds a file set to the implicit build path.
      * <p>
      * <em>Note that the directories will be added to the build path
      * in no particular order, so if order is significant, one should
      * use a file list instead!</em>
      *
      * @param  set the file set to add.
      */
     public void addFileset(FileSet set) {
         add(set);
     }
 
     /**
      * Adds an ordered file list to the implicit build path.
      * <p>
      * <em>Note that contrary to file and directory sets, file lists
      * can reference non-existent files or directories!</em>
      *
      * @param  list the file list to add.
      */
     public void addFilelist(FileList list) {
         add(list);
     }
 
     /**
      * Adds a resource collection to the implicit build path.
      *
      * @param  rc the resource collection to add.
      * @since Ant 1.7
      */
     public void add(ResourceCollection rc) {
         getBuildpath().add(rc);
     }
 
     /**
      * Set the buildpath to be used to find sub-projects.
      *
      * @param  s an Ant Path object containing the buildpath.
      */
     public void setBuildpath(Path s) {
         getBuildpath().append(s);
     }
 
     /**
      * Creates a nested build path, and add it to the implicit build path.
      *
      * @return the newly created nested build path.
      */
     public Path createBuildpath() {
         return getBuildpath().createPath();
     }
 
     /**
      * Creates a nested <code>&lt;buildpathelement&gt;</code>,
      * and add it to the implicit build path.
      *
      * @return the newly created nested build path element.
      */
     public Path.PathElement createBuildpathElement() {
         return getBuildpath().createPathElement();
     }
 
     /**
      * Gets the implicit build path, creating it if <code>null</code>.
      *
      * @return the implicit build path.
      */
     private Path getBuildpath() {
         if (buildpath == null) {
             buildpath = new Path(getProject());
         }
         return buildpath;
     }
 
     /**
      * Buildpath to use, by reference.
      *
      * @param  r a reference to an Ant Path object containing the buildpath.
      */
     public void setBuildpathRef(Reference r) {
         createBuildpath().setRefid(r);
     }
 
     /**
      * Creates the &lt;ant&gt; task configured to run a specific target.
      *
      * @param directory : if not null the directory where the build should run
      *
      * @return the ant task, configured with the explicit properties and
      *         references necessary to run the sub-build.
      */
     private Ant createAntTask(File directory) {
         Ant antTask = new Ant(this);
         antTask.init();
         if (subTarget != null && subTarget.length() > 0) {
             antTask.setTarget(subTarget);
         }
 
 
         if (output != null) {
             antTask.setOutput(output);
         }
 
         if (directory != null) {
             antTask.setDir(directory);
         } else {
             antTask.setUseNativeBasedir(true);
         }
 
         antTask.setInheritAll(inheritAll);
         for (Enumeration i = properties.elements(); i.hasMoreElements();) {
             copyProperty(antTask.createProperty(), (Property) i.nextElement());
         }
 
         for (Enumeration i = propertySets.elements(); i.hasMoreElements();) {
             antTask.addPropertyset((PropertySet) i.nextElement());
         }
 
         antTask.setInheritRefs(inheritRefs);
         for (Enumeration i = references.elements(); i.hasMoreElements();) {
             antTask.addReference((Ant.Reference) i.nextElement());
         }
 
         return antTask;
     }
 
     /**
      * Assigns an Ant property to another.
      *
      * @param  to the destination property whose content is modified.
      * @param  from the source property whose content is copied.
      */
     private static void copyProperty(Property to, Property from) {
         to.setName(from.getName());
 
         if (from.getValue() != null) {
             to.setValue(from.getValue());
         }
         if (from.getFile() != null) {
             to.setFile(from.getFile());
         }
         if (from.getResource() != null) {
             to.setResource(from.getResource());
         }
         if (from.getPrefix() != null) {
             to.setPrefix(from.getPrefix());
         }
         if (from.getRefid() != null) {
             to.setRefid(from.getRefid());
         }
         if (from.getEnvironment() != null) {
             to.setEnvironment(from.getEnvironment());
         }
         if (from.getClasspath() != null) {
             to.setClasspath(from.getClasspath());
         }
     }
 
 } // END class SubAnt
diff --git a/src/main/org/apache/tools/ant/types/Description.java b/src/main/org/apache/tools/ant/types/Description.java
index 27f9cea36..cb0004ff8 100644
--- a/src/main/org/apache/tools/ant/types/Description.java
+++ b/src/main/org/apache/tools/ant/types/Description.java
@@ -1,122 +1,122 @@
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
 package org.apache.tools.ant.types;
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.helper.ProjectHelper2;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.UnknownElement;
 import org.apache.tools.ant.Target;
 import org.apache.tools.ant.helper.ProjectHelperImpl;
 
 import java.util.Vector;
 
 
 /**
  * Description is used to provide a project-wide description element
  * (that is, a description that applies to a buildfile as a whole).
  * If present, the &lt;description&gt; element is printed out before the
  * target descriptions.
  *
  * Description has no attributes, only text.  There can only be one
  * project description per project.  A second description element will
  * overwrite the first.
  *
  *
  * @ant.datatype ignore="true"
  */
 public class Description extends DataType {
 
     /**
      * Adds descriptive text to the project.
      *
      * @param text the descriptive text
      */
     public void addText(String text) {
 
-        ProjectHelper ph = ProjectHelper.getProjectHelper();
+        ProjectHelper ph = (ProjectHelper) getProject().getReference(ProjectHelper.PROJECTHELPER_REFERENCE);
         if (!(ph instanceof ProjectHelperImpl)) {
             // New behavior for delayed task creation. Description
             // will be evaluated in Project.getDescription()
             return;
         }
         String currentDescription = getProject().getDescription();
         if (currentDescription == null) {
             getProject().setDescription(text);
         } else {
             getProject().setDescription(currentDescription + text);
         }
     }
 
     /**
      * Return the descriptions from all the targets of
      * a project.
      *
      * @param project the project to get the descriptions for.
      * @return a string containing the concatenated descriptions of
      *         the targets.
      */
     public static String getDescription(Project project) {
         Vector targets = (Vector) project.getReference(ProjectHelper2.REFID_TARGETS);
         if (targets == null) {
             return null;
         }
         StringBuffer description = new StringBuffer();
         for (int i = 0; i < targets.size(); i++) {
             Target t = (Target) targets.elementAt(i);
             concatDescriptions(project, t, description);
         }
         return description.toString();
     }
 
     private static void concatDescriptions(Project project, Target t,
                                            StringBuffer description) {
         if (t == null) {
             return;
         }
         Vector tasks = findElementInTarget(project, t, "description");
         if (tasks == null) {
             return;
         }
         for (int i = 0; i < tasks.size(); i++) {
             Task task = (Task) tasks.elementAt(i);
             if (!(task instanceof UnknownElement)) {
                 continue;
             }
             UnknownElement ue = ((UnknownElement) task);
             String descComp = ue.getWrapper().getText().toString();
             if (descComp != null) {
                 description.append(project.replaceProperties(descComp));
             }
         }
     }
 
     private static Vector findElementInTarget(Project project,
                                               Target t, String name) {
         Task[] tasks = t.getTasks();
         Vector elems = new Vector();
         for (int i = 0; i < tasks.length; i++) {
             if (name.equals(tasks[i].getTaskName())) {
                 elems.addElement(tasks[i]);
             }
         }
         return elems;
     }
 
 }
