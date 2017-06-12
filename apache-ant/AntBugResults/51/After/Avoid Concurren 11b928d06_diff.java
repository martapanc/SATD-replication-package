diff --git a/WHATSNEW b/WHATSNEW
index 7f4110d15..6e1e4dacd 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1012 +1,1021 @@
 Changes from Ant 1.8.0 TO current SVN version
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 Fixed bugs:
 -----------
 
+ * Tasks that iterate over task or type definitions, references or
+   targets now iterate over copies instead of the live maps to avoid
+   ConcurrentModificationExceptions if another thread changes the
+   maps.
+   Bugzilla Report 48310.
+
 Other changes:
 --------------
 
+ * Project provides new get methods that return copies instead of the
+   live maps of task and type definitions, references and targets.
+
 Changes from Ant 1.8.0RC1 TO Ant 1.8.0
 ======================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * the appendtolines filter has been renamed to suffixlines.
 
 Fixed bugs:
 -----------
 
  * stack traces were not reported at all by <junit/>
    when filtertrace="on", which is the default.
 
  * ant.bat can now also process the -noclasspath switch when it is 
    the first switch on a command line.
    Bugzilla Report 48186.
 
  * <fixcrlf> now tries to delete the created temporary files earlier.
    Bugzilla Report 48506.
 
  * the implementation of <zip> had been changed in a way that broke
    the jarjar links task and protentially other third-party subclasses
    as well.
    Bugzilla Report 48541.
    
  * <scp> task didn't report build file location when a remote operation failed
    Bugzilla Report 48578.  
 
  * <propertyfile> would add the same comment and a date line each time
    it updated an existing property file.
    Bugzilla Report 48558.
 
  * <sound> didn't work properly in recent Java VMs.
    Bugzilla Report 48637.
 
 Other changes:
 --------------
 
 Changes from Ant 1.7.1 TO Ant 1.8.0RC1
 ======================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * if and unless attributes (on <target> as well as various tasks and other
    elements) have long permitted ${property} interpolation. Now, if the result
    evaluates to "true" or "false" (or "yes", "no", "on", "off"), that boolean
    value will be used; otherwise the traditional behavior of treating the value
    as a property name (defined ~ true, undefined ~ false) is used. Existing
    scripts could be broken if they perversely defined a property named "false"
    and expected if="false" to be true, or used if="true" expecting this to be
    triggered only if a property named "true" were defined.
 
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
    types. This method is now used to define conditions, selectors,
    comparators and filterreaders. This means that tasks may now have
    nested conditions just by implementing the Condition interface,
    rather than extending ConditionBase. It also means that the use of
    namespaces for some of the selectors introduced in Ant 1.7.0 is no
    longer necessary.  Implementing this means that the DynamicElement
    work-around introduced in Ant 1.7.0 has been removed.
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
 
  * A ProjectHelper implementation can now provide the default build file
    name it is expecting, and can specify if they can support a specific build
    file. So Ant is now capable of supporting several ProjectHelper
    implementations, deciding on which to use depending of the input build file.
 
  * Mapper-aware selectors (depends, different, present) now accept typedef'd
    FileNameMappers.
 
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
 
  * Ant didn't set the proper "magic" value for tar entries containing
    long file names in GNU longfile mode.
    Bugzilla Report 47653.
 
  * The tar task failed to recognize that the archive had to be
    (re-)created in some cases where the sources are filesystem based
    resources but not filesets.
    Bugzilla Report 48035. 
 
  * <sshexec>'s outputproperty was prefixed by the executed command
    when the command attribute has been used, breaking backwards
    compatibility to Ant 1.7.0.
    Bugzilla Report 48040.
 
  * different task instances of the same <scriptdef>ed tasks could
    overwrite each others attributes/nested elements.
    Bugzilla Report 41602.
 
  * The Hashvalue algortihm implementation of the modified task could
    fail to read the file(s) completely.
    Bugzilla Report 48313.
 
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
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 875352fa3..b2446f267 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,2424 +1,2473 @@
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
         return AntClassLoader
             .newAntClassLoader(getClass().getClassLoader(), this, path, true);
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
         return AntClassLoader.newAntClassLoader(parent, this, path, true);
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
-         * Return a copy of the list of build listeners for the project.
-         * 
-         * @return a list of build listeners for the project
-         */
+     * Return a copy of the list of build listeners for the project.
+     * 
+     * @return a list of build listeners for the project
+     */
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
      * Return a copy of the inherited property hashtable.
      * @return a hashtable containing just the inherited properties.
      * @since Ant 1.8.0
      */
     public Hashtable getInheritedProperties() {
         return PropertyHelper.getPropertyHelper(this).getInheritedProperties();
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
         if (!JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_4))  {
             throw new BuildException("Ant cannot work on Java prior to 1.4");
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
+     * Return the current task definition map. The returned map is a
+     * copy of the &quot;live&quot; definitions.
+     *
+     * @return a map of from task name to implementing class
+     *         (String to Class).
+     *
+     * @since Ant 1.8.1
+     */
+    public Map getCopyOfTaskDefinitions() {
+        return new HashMap(getTaskDefinitions());
+    }
+
+    /**
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
+     * Return the current datatype definition map. The returned
+     * map is a copy pf the &quot;live&quot; definitions.
+     *
+     * @return a map of from datatype name to implementing class
+     *         (String to Class).
+     *
+     * @since Ant 1.8.1
+     */
+    public Map getCopyOfDataTypeDefinitions() {
+        return new HashMap(getDataTypeDefinitions());
+    }
+
+    /**
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
+     * Return the map of targets. The returned map
+     * is a copy of the &quot;live&quot; targets.
+     * @return a map from name to target (String to Target).
+     * @since Ant 1.8.1
+     */
+    public Map getCopyOfTargets() {
+        return new HashMap(targets);
+    }
+
+    /**
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
      * Does the project know this reference?
      *
      * @since Ant 1.8.0
      */
     public boolean hasReference(String key) {
         return references.containsKey(key);
     }
 
     /**
+     * Return a map of the references in the project (String to
+     * Object).  The returned hashtable is a copy of the
+     * &quot;live&quot; references.
+     *
+     * @return a map of the references in the project (String to Object).
+     *
+     * @since Ant 1.8.1
+     */
+    public Map getCopyOfReferences() {
+        return new HashMap(references);
+    }
+
+    /**
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
 
         if (message == null) {
             message = String.valueOf(message);
         }
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/AntStructure.java b/src/main/org/apache/tools/ant/taskdefs/AntStructure.java
index 5468305b0..475cc832f 100644
--- a/src/main/org/apache/tools/ant/taskdefs/AntStructure.java
+++ b/src/main/org/apache/tools/ant/taskdefs/AntStructure.java
@@ -1,484 +1,487 @@
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
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.UnsupportedEncodingException;
 import java.util.Enumeration;
 import java.util.Hashtable;
+import java.util.Iterator;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.IntrospectionHelper;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.TaskContainer;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.Reference;
 
 /**
  * Creates a partial DTD for Ant from the currently known tasks.
  *
  *
  * @since Ant 1.1
  *
  * @ant.task category="xml"
  */
 public class AntStructure extends Task {
 
     private static final String LINE_SEP
         = System.getProperty("line.separator");
 
     private File output;
     private StructurePrinter printer = new DTDPrinter();
 
     /**
      * The output file.
      * @param output the output file
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * The StructurePrinter to use.
      * @param p the printer to use.
      * @since Ant 1.7
      */
     public void add(StructurePrinter p) {
         printer = p;
     }
 
     /**
      * Build the antstructure DTD.
      *
      * @exception BuildException if the DTD cannot be written.
      */
     public void execute() throws BuildException {
 
         if (output == null) {
             throw new BuildException("output attribute is required", getLocation());
         }
 
         PrintWriter out = null;
         try {
             try {
                 out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF8"));
             } catch (UnsupportedEncodingException ue) {
                 /*
                  * Plain impossible with UTF8, see
                  * http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html
                  *
                  * fallback to platform specific anyway.
                  */
                 out = new PrintWriter(new FileWriter(output));
             }
 
             printer.printHead(out, getProject(),
-                              getProject().getTaskDefinitions(),
-                              getProject().getDataTypeDefinitions());
+                              new Hashtable(getProject().getTaskDefinitions()),
+                              new Hashtable(getProject().getDataTypeDefinitions()));
 
             printer.printTargetDecl(out);
 
-            Enumeration dataTypes = getProject().getDataTypeDefinitions().keys();
-            while (dataTypes.hasMoreElements()) {
-                String typeName = (String) dataTypes.nextElement();
+            Iterator dataTypes = getProject().getCopyOfDataTypeDefinitions()
+                .keySet().iterator();
+            while (dataTypes.hasNext()) {
+                String typeName = (String) dataTypes.next();
                 printer.printElementDecl(
                     out, getProject(), typeName,
                     (Class) getProject().getDataTypeDefinitions().get(typeName));
             }
 
-            Enumeration tasks = getProject().getTaskDefinitions().keys();
-            while (tasks.hasMoreElements()) {
-                String tName = (String) tasks.nextElement();
+            Iterator tasks = getProject().getCopyOfTaskDefinitions().keySet()
+                .iterator();
+            while (tasks.hasNext()) {
+                String tName = (String) tasks.next();
                 printer.printElementDecl(out, getProject(), tName,
                                          (Class) getProject().getTaskDefinitions().get(tName));
             }
 
             printer.printTail(out);
 
             if (out.checkError()) {
                 throw new IOException("Encountered an error writing Ant"
                                       + " structure");
             }
         } catch (IOException ioe) {
             throw new BuildException("Error writing "
                                      + output.getAbsolutePath(), ioe, getLocation());
         } finally {
             if (out != null) {
                 out.close();
             }
         }
     }
 
     /**
      * Writes the actual structure information.
      *
      * <p>{@link #printHead}, {@link #printTargetDecl} and {@link #printTail}
      * are called exactly once, {@link #printElementDecl} once for
      * each declared task and type.</p>
      */
     public static interface StructurePrinter {
         /**
          * Prints the header of the generated output.
          *
          * @param out PrintWriter to write to.
          * @param p Project instance for the current task
          * @param tasks map (name to implementing class)
          * @param types map (name to implementing class)
          * data types.
          */
         void printHead(PrintWriter out, Project p, Hashtable tasks,
                        Hashtable types);
 
         /**
          * Prints the definition for the target element.
          * @param out PrintWriter to write to.
          */
         void printTargetDecl(PrintWriter out);
 
         /**
          * Print the definition for a given element.
          *
          * @param out PrintWriter to write to.
          * @param p Project instance for the current task
          * @param name element name.
          * @param element class of the defined element.
          */
         void printElementDecl(PrintWriter out, Project p, String name,
                               Class element);
 
         /**
          * Prints the trailer.
          * @param out PrintWriter to write to.
          */
         void printTail(PrintWriter out);
     }
 
     private static class DTDPrinter implements StructurePrinter {
 
         private static final String BOOLEAN = "%boolean;";
         private static final String TASKS = "%tasks;";
         private static final String TYPES = "%types;";
 
         private Hashtable visited = new Hashtable();
 
         public void printTail(PrintWriter out) {
             visited.clear();
         }
 
         public void printHead(PrintWriter out, Project p, Hashtable tasks, Hashtable types) {
             printHead(out, tasks.keys(), types.keys());
         }
 
 
         /**
          * Prints the header of the generated output.
          *
          * <p>Basically this prints the XML declaration, defines some
          * entities and the project element.</p>
          */
         private void printHead(PrintWriter out, Enumeration tasks,
                                Enumeration types) {
             out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
             out.println("<!ENTITY % boolean \"(true|false|on|off|yes|no)\">");
             out.print("<!ENTITY % tasks \"");
             boolean first = true;
             while (tasks.hasMoreElements()) {
                 String tName = (String) tasks.nextElement();
                 if (!first) {
                     out.print(" | ");
                 } else {
                     first = false;
                 }
                 out.print(tName);
             }
             out.println("\">");
             out.print("<!ENTITY % types \"");
             first = true;
             while (types.hasMoreElements()) {
                 String typeName = (String) types.nextElement();
                 if (!first) {
                     out.print(" | ");
                 } else {
                     first = false;
                 }
                 out.print(typeName);
             }
             out.println("\">");
 
             out.println("");
 
             out.print("<!ELEMENT project (target | extension-point | ");
             out.print(TASKS);
             out.print(" | ");
             out.print(TYPES);
             out.println(")*>");
             out.println("<!ATTLIST project");
             out.println("          name    CDATA #IMPLIED");
             out.println("          default CDATA #IMPLIED");
             out.println("          basedir CDATA #IMPLIED>");
             out.println("");
         }
 
         /**
          * Prints the definition for the target element.
          */
         public void printTargetDecl(PrintWriter out) {
             out.print("<!ELEMENT target (");
             out.print(TASKS);
             out.print(" | ");
             out.print(TYPES);
             out.println(")*>");
             out.println("");
             printTargetAttrs(out, "target");
             out.println("<!ELEMENT extension-point EMPTY>");
             out.println("");
             printTargetAttrs(out, "extension-point");
         }
 
         /**
          * Prints the definition for the target element.
          */
         private void printTargetAttrs(PrintWriter out, String tag) {
             out.print("<!ATTLIST ");
             out.println(tag);
             out.println("          id          ID    #IMPLIED");
             out.println("          name        CDATA #REQUIRED");
             out.println("          if          CDATA #IMPLIED");
             out.println("          unless      CDATA #IMPLIED");
             out.println("          depends     CDATA #IMPLIED");
             out.println("          extensionOf CDATA #IMPLIED");
             out.println("          description CDATA #IMPLIED>");
             out.println("");
         }
 
         /**
          * Print the definition for a given element.
          */
         public void printElementDecl(PrintWriter out, Project p,
                                      String name, Class element) {
 
             if (visited.containsKey(name)) {
                 return;
             }
             visited.put(name, "");
 
             IntrospectionHelper ih = null;
             try {
                 ih = IntrospectionHelper.getHelper(p, element);
             } catch (Throwable t) {
                 /*
                  * XXX - failed to load the class properly.
                  *
                  * should we print a warning here?
                  */
                 return;
             }
 
             StringBuffer sb = new StringBuffer("<!ELEMENT ");
             sb.append(name).append(" ");
 
             if (org.apache.tools.ant.types.Reference.class.equals(element)) {
                 sb.append("EMPTY>").append(LINE_SEP);
                 sb.append("<!ATTLIST ").append(name);
                 sb.append(LINE_SEP).append("          id ID #IMPLIED");
                 sb.append(LINE_SEP).append("          refid IDREF #IMPLIED");
                 sb.append(">").append(LINE_SEP);
                 out.println(sb);
                 return;
             }
 
             Vector v = new Vector();
             if (ih.supportsCharacters()) {
                 v.addElement("#PCDATA");
             }
 
             if (TaskContainer.class.isAssignableFrom(element)) {
                 v.addElement(TASKS);
             }
 
             Enumeration e = ih.getNestedElements();
             while (e.hasMoreElements()) {
                 v.addElement(e.nextElement());
             }
 
             if (v.isEmpty()) {
                 sb.append("EMPTY");
             } else {
                 sb.append("(");
                 final int count = v.size();
                 for (int i = 0; i < count; i++) {
                     if (i != 0) {
                         sb.append(" | ");
                     }
                     sb.append(v.elementAt(i));
                 }
                 sb.append(")");
                 if (count > 1 || !v.elementAt(0).equals("#PCDATA")) {
                     sb.append("*");
                 }
             }
             sb.append(">");
             out.println(sb);
 
             sb = new StringBuffer("<!ATTLIST ");
             sb.append(name);
             sb.append(LINE_SEP).append("          id ID #IMPLIED");
 
             e = ih.getAttributes();
             while (e.hasMoreElements()) {
                 String attrName = (String) e.nextElement();
                 if ("id".equals(attrName)) {
                     continue;
                 }
 
                 sb.append(LINE_SEP).append("          ")
                     .append(attrName).append(" ");
                 Class type = ih.getAttributeType(attrName);
                 if (type.equals(java.lang.Boolean.class)
                     || type.equals(java.lang.Boolean.TYPE)) {
                     sb.append(BOOLEAN).append(" ");
                 } else if (Reference.class.isAssignableFrom(type)) {
                     sb.append("IDREF ");
                 } else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
                     try {
                         EnumeratedAttribute ea =
                             (EnumeratedAttribute) type.newInstance();
                         String[] values = ea.getValues();
                         if (values == null
                             || values.length == 0
                             || !areNmtokens(values)) {
                             sb.append("CDATA ");
                         } else {
                             sb.append("(");
                             for (int i = 0; i < values.length; i++) {
                                 if (i != 0) {
                                     sb.append(" | ");
                                 }
                                 sb.append(values[i]);
                             }
                             sb.append(") ");
                         }
                     } catch (InstantiationException ie) {
                         sb.append("CDATA ");
                     } catch (IllegalAccessException ie) {
                         sb.append("CDATA ");
                     }
                 } else if (type.getSuperclass() != null
                            && type.getSuperclass().getName().equals("java.lang.Enum")) {
                     try {
                         Object[] values = (Object[]) type.getMethod("values", (Class[])  null)
                             .invoke(null, (Object[]) null);
                         if (values.length == 0) {
                             sb.append("CDATA ");
                         } else {
                             sb.append('(');
                             for (int i = 0; i < values.length; i++) {
                                 if (i != 0) {
                                     sb.append(" | ");
                                 }
                                 sb.append(type.getMethod("name", (Class[]) null)
                                           .invoke(values[i], (Object[]) null));
                             }
                             sb.append(") ");
                         }
                     } catch (Exception x) {
                         sb.append("CDATA ");
                     }
                 } else {
                     sb.append("CDATA ");
                 }
                 sb.append("#IMPLIED");
             }
             sb.append(">").append(LINE_SEP);
             out.println(sb);
 
             final int count = v.size();
             for (int i = 0; i < count; i++) {
                 String nestedName = (String) v.elementAt(i);
                 if (!"#PCDATA".equals(nestedName)
                     && !TASKS.equals(nestedName)
                     && !TYPES.equals(nestedName)) {
                     printElementDecl(out, p, nestedName, ih.getElementType(nestedName));
                 }
             }
         }
 
         /**
          * Does this String match the XML-NMTOKEN production?
          * @param s the string to test
          * @return true if the string matches the XML-NMTOKEN
          */
         public static final boolean isNmtoken(String s) {
             final int length = s.length();
             for (int i = 0; i < length; i++) {
                 char c = s.charAt(i);
                 // XXX - we are committing CombiningChar and Extender here
                 if (!Character.isLetterOrDigit(c)
                     && c != '.' && c != '-' && c != '_' && c != ':') {
                     return false;
                 }
             }
             return true;
         }
 
         /**
          * Do the Strings all match the XML-NMTOKEN production?
          *
          * <p>Otherwise they are not suitable as an enumerated attribute,
          * for example.</p>
          * @param s the array of string to test
          * @return true if all the strings in the array math XML-NMTOKEN
          */
         public static final boolean areNmtokens(String[] s) {
             for (int i = 0; i < s.length; i++) {
                 if (!isNmtoken(s[i])) {
                     return false;
                 }
             }
             return true;
         }
     }
 
     /**
      * Does this String match the XML-NMTOKEN production?
      * @param s the string to test
      * @return true if the string matches the XML-NMTOKEN
      */
     protected boolean isNmtoken(String s) {
         return DTDPrinter.isNmtoken(s);
     }
 
     /**
      * Do the Strings all match the XML-NMTOKEN production?
      *
      * <p>Otherwise they are not suitable as an enumerated attribute,
      * for example.</p>
      * @param s the array of string to test
      * @return true if all the strings in the array math XML-NMTOKEN
      */
     protected boolean areNmtokens(String[] s) {
         return DTDPrinter.areNmtokens(s);
     }
 }
diff --git a/src/main/org/apache/tools/ant/util/ScriptRunnerBase.java b/src/main/org/apache/tools/ant/util/ScriptRunnerBase.java
index 1bba545ae..b6f39248b 100644
--- a/src/main/org/apache/tools/ant/util/ScriptRunnerBase.java
+++ b/src/main/org/apache/tools/ant/util/ScriptRunnerBase.java
@@ -1,363 +1,363 @@
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
 package org.apache.tools.ant.util;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.FileNotFoundException;
 import java.io.InputStreamReader;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ProjectComponent;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 
 import java.util.Map;
 import java.util.HashMap;
 import java.util.Iterator;
 
 /**
  * This is a common abstract base case for script runners.
  * These classes need to implement executeScript, evaluateScript
  * and supportsLanguage.
  * @since Ant 1.7.0
  */
 public abstract class ScriptRunnerBase {
     /** Whether to keep the engine between calls to execute/eval */
     private boolean keepEngine = false;
 
     /** Script language */
     private String language;
 
     /** Script content */
     private String script = "";
 
     /** Project this runner is used in */
     private Project project;
 
     /** Classloader to be used when running the script. */
     private ClassLoader scriptLoader;
 
     /** Beans to be provided to the script */
     private Map beans = new HashMap();
 
     /**
      * Add a list of named objects to the list to be exported to the script
      *
      * @param dictionary a map of objects to be placed into the script context
      *        indexed by String names.
      */
     public void addBeans(Map dictionary) {
         for (Iterator i = dictionary.keySet().iterator(); i.hasNext();) {
             String key = (String) i.next();
             try {
                 Object val = dictionary.get(key);
                 addBean(key, val);
             } catch (BuildException ex) {
                 // The key is in the dictionary but cannot be retrieved
                 // This is usually due references that refer to tasks
                 // that have not been taskdefed in the current run.
                 // Ignore
             }
         }
     }
 
     /**
      * Add a single object into the script context.
      *
      * @param key the name in the context this object is to stored under.
      * @param bean the object to be stored in the script context.
      */
     public void addBean(String key, Object bean) {
         boolean isValid = key.length() > 0
             && Character.isJavaIdentifierStart(key.charAt(0));
 
         for (int i = 1; isValid && i < key.length(); i++) {
             isValid = Character.isJavaIdentifierPart(key.charAt(i));
         }
 
         if (isValid) {
             beans.put(key, bean);
         }
     }
 
     /**
      * Get the beans used for the script.
      * @return the map of beans.
      */
     protected Map getBeans() {
         return beans;
     }
 
     /**
      * Do the work.
      * @param execName the name that will be passed to BSF for this script
      *        execution.
      */
     public abstract void executeScript(String execName);
 
     /**
      * Evaluate the script.
      * @param execName the name that will be passed to the
      *                 scripting engine for this script execution.
      * @return the result of evaluating the script.
      */
     public abstract Object evaluateScript(String execName);
 
     /**
      * Check if a script engine can be created for
      * this language.
      * @return true if a script engine can be created, false
      *              otherwise.
      */
     public abstract boolean supportsLanguage();
 
     /**
      * Get the name of the manager prefix used for this
      * scriptrunner.
      * @return the prefix string.
      */
     public abstract String getManagerName();
 
     /**
      * Defines the language (required).
      * @param language the scripting language name for the script.
      */
     public void setLanguage(String language) {
         this.language = language;
     }
 
     /**
      * Get the script language
      * @return the script language
      */
     public String getLanguage() {
         return language;
     }
 
     /**
      * Set the script classloader.
      * @param classLoader the classloader to use.
      */
     public void setScriptClassLoader(ClassLoader classLoader) {
         this.scriptLoader = classLoader;
     }
 
     /**
      * Get the classloader used to load the script engine.
      * @return the classloader.
      */
     protected ClassLoader getScriptClassLoader() {
         return scriptLoader;
     }
 
     /**
      * Whether to keep the script engine between calls.
      * @param keepEngine if true, keep the engine.
      */
     public void setKeepEngine(boolean keepEngine) {
         this.keepEngine = keepEngine;
     }
 
     /**
      * Get the keep engine attribute.
      * @return the attribute.
      */
     public boolean getKeepEngine() {
         return keepEngine;
     }
 
     /**
      * Load the script from an external file; optional.
      * @param file the file containing the script source.
      */
     public void setSrc(File file) {
         String filename = file.getPath();
         if (!file.exists()) {
             throw new BuildException("file " + filename + " not found.");
         }
         try {
             readSource(new FileReader(file), filename);
         } catch (FileNotFoundException e) {
             //this can only happen if the file got deleted a short moment ago
             throw new BuildException("file " + filename + " not found.");
         }
     }
 
     /**
      * Read some source in from the given reader
      * @param reader the reader; this is closed afterwards.
      * @param name the name to use in error messages
      */
     private void readSource(Reader reader, String name) {
         BufferedReader in = null;
         try {
             in = new BufferedReader(reader);
             script += FileUtils.safeReadFully(in);
         } catch (IOException ex) {
             throw new BuildException("Failed to read " + name, ex);
         } finally {
             FileUtils.close(in);
         }
     }
 
 
     /**
      * Add a resource to the source list.
      * @since Ant 1.7.1
      * @param sourceResource the resource to load
      * @throws BuildException if the resource cannot be read
      */
     public void loadResource(Resource sourceResource) {
         String name = sourceResource.toLongString();
         InputStream in = null;
         try {
             in = sourceResource.getInputStream();
         } catch (IOException e) {
             throw new BuildException("Failed to open " + name, e);
         } catch (UnsupportedOperationException e) {
             throw new BuildException(
                 "Failed to open " + name + " -it is not readable", e);
         }
         readSource(new InputStreamReader(in), name);
     }
 
     /**
      * Add all resources in a resource collection to the source list.
      * @since Ant 1.7.1
      * @param collection the resource to load
      * @throws BuildException if a resource cannot be read
      */
     public void loadResources(ResourceCollection collection) {
         Iterator resources = collection.iterator();
         while (resources.hasNext()) {
             Resource resource = (Resource) resources.next();
             loadResource(resource);
         }
     }
 
     /**
      * Set the script text. Properties in the text are not expanded!
      *
      * @param text a component of the script text to be added.
      */
     public void addText(String text) {
         script += text;
     }
 
     /**
      * Get the current script text content.
      * @return the script text.
      */
     public String getScript() {
         return script;
     }
 
     /**
      * Clear the current script text content.
      */
     public void clearScript() {
         this.script = "";
     }
 
     /**
      * Set the project for this runner.
      * @param project the project.
      */
     public void setProject(Project project) {
         this.project = project;
     }
 
     /**
      * Get the project for this runner.
      * @return the project.
      */
     public Project getProject() {
         return project;
     }
 
     /**
      * Bind the runner to a project component.
      * Properties, targets and references are all added as beans;
      * project is bound to project, and self to the component.
      * @param component to become <code>self</code>
      */
     public void bindToComponent(ProjectComponent component) {
         project = component.getProject();
         addBeans(project.getProperties());
         addBeans(project.getUserProperties());
-        addBeans(project.getTargets());
-        addBeans(project.getReferences());
+        addBeans(project.getCopyOfTargets());
+        addBeans(project.getCopyOfReferences());
         addBean("project", project);
         addBean("self", component);
     }
 
     /**
      * Bind the runner to a project component.
      * The project and self are the only beans set.
      * @param component to become <code>self</code>
      */
     public void bindToComponentMinimum(ProjectComponent component) {
         project = component.getProject();
         addBean("project", project);
         addBean("self", component);
     }
 
     /**
      * Check if the language attribute is set.
      * @throws BuildException if it is not.
      */
     protected void checkLanguage() {
         if (language == null) {
             throw new BuildException(
                 "script language must be specified");
         }
     }
 
     /**
      * Replace the current context classloader with the
      * script context classloader.
      * @return the current context classloader.
      */
     protected ClassLoader replaceContextLoader() {
         ClassLoader origContextClassLoader =
             Thread.currentThread().getContextClassLoader();
         if (getScriptClassLoader() == null) {
             setScriptClassLoader(getClass().getClassLoader());
         }
         Thread.currentThread().setContextClassLoader(getScriptClassLoader());
         return origContextClassLoader;
     }
 
     /**
      * Restore the context loader with the original context classloader.
      *
      * script context loader.
      * @param origLoader the original context classloader.
      */
     protected void restoreContextLoader(ClassLoader origLoader) {
         Thread.currentThread().setContextClassLoader(
                  origLoader);
     }
 
 }
