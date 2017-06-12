diff --git a/WHATSNEW b/WHATSNEW
index b3935d3e6..3b2f3a84d 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1047 +1,1053 @@
 Changes from Ant 1.8.1 TO current SVN version
 =============================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * Prior to Ant 1.8.0 the <copy> task and several other tasks would
    overwrite read-only destination files.  Starting with 1.8.0 they
    would only do so under special circumstances.  Ant 1.8.2 now
    consistently won't replace a read-only file by default. The same is
    true for a number of other tasks.
    The <copy>, <move> and <echo> tasks now have a new force attribute
    and <concat> has a new forceReadonly attribute that can be used to
    make the task overwrite read-only destinations.
    Bugzilla Report 49261.
 
  * Removed ant-nodeps.jar; it is now merged into ant.jar.
 
 Fixed bugs:
 -----------
 
  * The Sun JVM tries to mmap the entire file during a copy. 
    For large files this is not feasible. 
    We now explicitly request to copy at most 16 MiB per request.
    Bugzilla Report 49326.
    
  * DemuxInputStream.read() should return unsigned values
    Bugzilla Report 49279.
 
  * The MIME mailer ignored the port parameter when using SSL.
    Bugzilla Report 49267.
 
  * <xslt> ignored the classpath when using the default TraX processor.
    Bugzilla Report 49271.
 
  * <checksum>'s totalproperty only worked reliably if the same file
    name didn't occur inside more than one directory.
    Bugzilla Report 36748.
 
  * <ftp> could fail to download files from remote subdirectories under
    certain circumstances.
    Bugzilla Report 49296.
 
  * <junit> will now produce better diagnostics when it fails to delete
    a temporary file.
    Bugzilla Report 49419.
 
+ * Ant would often scan directories even though there were known to
+   only hold excluded files when evaluating filesets.  This never
+   resulted in worng results but degraded performance of the scan
+   itself.
+   Bugzilla Report 49420.
+
 Other changes:
 --------------
 
  * <concat>'s force attribute has been deprecated in favor of a new
    overwrite attribute that is consistent with <copy>'s attribute
    names.
 
 Changes from Ant 1.8.0 TO Ant 1.8.1 
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * ant-trax.jar is no longer produced since TrAX is included in JDK 1.4+.
 
  * Ant no longer ships with Apache Xerces-J or the XML APIs but relies
    on the Java runtime to provide a parser and matching API versions.
    
  * The stylebook ant task and the ant-stylebook.jar are removed.  
 
 Fixed bugs:
 -----------
 
  * Tasks that iterate over task or type definitions, references or
    targets now iterate over copies instead of the live maps to avoid
    ConcurrentModificationExceptions if another thread changes the
    maps.
    Bugzilla Report 48310.
 
  * The filesmatch condition threw a NullPointerException when
    comparing text files and the second file contained fewer lines than
    the first one.
    Bugzilla Report 48715.
 
  * Regression: The <ear> task would allow multiple
    META-INF/application.xml files to be added.
    Bugzilla Report 6836.
 
  * VectorSet#remove(Object) would fail if the size of the vector
    equaled its capacity.
    
  * Regression : ant -diagnostics was returning with exit code 1
    Bugzilla Report 48782
    
  * Fix for exec task sometimes inserts extraneous newlines
    Bugzilla Report 48746
    
  * SymlinkTest#testSymbolicLinkUtilsMethods failing on MacOS
    Bugzilla Report 48785.
 
  * If <concat>'s first resourcecollection child is a <resources>,
    any subsequently added child resourcecollection joins the first.
    Bugzilla Report 48816.        
    
  * <get> with an invalid URL could trigger an NPE in some JVMs.
    Bugzilla Report 48833
    
  * Broken Pipe issue under Ubuntu Linux
    Bugzilla Report 48789
    
  * Properties wrongly read from file or not update during read
    Bugzilla Report 48768       
 
  * AntClassLoader in Ant 1.8.0 has been considerably slower than in
    1.7.1
    Bugzilla Report 48853
    
  * ANT_CMD_LINE_ARGS are rippling through lower level Ant usage 
    Bugzilla Report 48876
    
  * email : IO error sending mail with plain mimetype
    Bugzilla Report 48932    
 
  * the complete-ant-cmd.pl script failed to create a proper cache of
    target if "ant -p" failed.
    Bugzilla Report 48980
 
  * <rmic>'s sourcebase attribute was broken.
    Bugzilla Report 48970
 
  * <copy>'s failonerror didn't work as expected when copying a single
    element resource collection to a file.
    Bugzilla Report 49070
 
  * <get> no longer followed redirects if the redirect URL was relative
    and not an absolute URL.
    Bugzilla Report 48972
 
  * fixed a performance degradation in the code that expands property
    references.
    Bugzilla Reports 48961 and 49079
 
  * <jar filesetmanifest="merge"> was broken on Windows.
    Bugzilla Report 49090
 
  * <symlink> delete failed if the link attribute was a relative path
    to a link inside the current directory without a leading ".".
    Bugzilla Report 49137
 
  * <telnet> and <rexec> failed to find the expected strings when
    waiting for responses and thus always failed.
    Bugzilla Report 49173
 
 Other changes:
 --------------
 
  * Project provides new get methods that return copies instead of the
    live maps of task and type definitions, references and targets.
 
  * Ant is now more lenient with ZIP extra fields and will be able to
    read archives that it failed to read in earlier versions.
    Bugzilla Report 48781.
 
  * The <zip> family of tasks has been sped up for bigger archives.
    Bugzilla Report 48755.
    
  * Add removeKeepExtension option to NetRexxC task.
    Bugzilla Report 48788.
 
  * Add prefix attribute to loadproperties task.
 
  * Add resource attribute to length task.
 
  * PropertyResource will effectively proxy another Resource if ${name}
    evaluates to a Resource object.
 
  * Added forcestring attribute to equals condition to force evaluation
    of Object args as strings; previously only API-level usage of the
    equals condition allowed Object args, but Ant 1.8.x+ property
    evaluation may yield values of any type.
    
  * BuildFileTest.assertPropertyUnset() fails with a slightly more 
    meaningful error message
    Bugzilla Report 48834
    
  * <junit> will now throw an exception if a test name is empty.  This
    used to manifest itself in unrelated errors like
    Bugzilla Report 43586.
 
  * A change that made <exec> more reliable on Windows (Bugzilla Report
    5003) strongly impacts the performance for commands that execute
    quickly, like attrib.  Basically no single execution of a command
    could take less than a second on Windows.
    A few timeouts have been tweaked to allow these commands to finish
    more quickly but still they will take longer than they did with Ant
    1.7.1.
    Bugzilla Report 48734.
 
  * Added SimpleBigProjectLogger, intermediate between NoBannerLogger and
    BigProjectLogger.
 
  * <mappedresources> supports new attributes enablemultiplemappings
    and cache.
 
  * Added the augment task to manipulate existing references via Ant's basic
    introspection mechanisms.
 
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
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index ecd4e8726..7531954d4 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -258,1607 +258,1608 @@ public class DirectoryScanner
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
      * List of all scanned directories.
      *
      * @since Ant 1.6
      */
     private Set scannedDirs = new HashSet();
 
     /**
      * Map of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>Maps pattern string to TokenizedPath.</p>
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.8.0
      */
     private Map includeNonPatterns = new HashMap();
 
     /**
      * Map of all exclude patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>Maps pattern string to TokenizedPath.</p>
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.8.0
      */
     private Map excludeNonPatterns = new HashMap();
 
     /**
      * Array of all include patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private TokenizedPattern[] includePatterns;
 
     /**
      * Array of all exclude patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private TokenizedPattern[] excludePatterns;
 
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
      * The maximum number of times a symbolic link may be followed
      * during a scan.
      *
      * @since Ant 1.8.0
      */
     private int maxLevelsOfSymlinks = MAX_LEVELS_OF_SYMLINKS;
 
 
     /**
      * Absolute paths of all symlinks that haven't been followed but
      * would have been if followsymlinks had been true or
      * maxLevelsOfSymlinks had been higher.
      *
      * @since Ant 1.8.0
      */
     private Set/*<String>*/ notFollowedSymlinks = new HashSet();
 
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
      *         <code>Set</code>.
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
         return defaultExcludes.add(s);
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
         defaultExcludes = new HashSet();
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
      * The maximum number of times a symbolic link may be followed
      * during a scan.
      *
      * @since Ant 1.8.0
      */
     public void setMaxLevelsOfSymlinks(int max) {
         maxLevelsOfSymlinks = max;
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
             pattern += SelectorUtils.DEEP_TREE_MATCH;
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
         File savedBase = basedir;
         try {
             synchronized (this) {
                 illegal = null;
                 clearResults();
 
                 // set in/excludes to reasonable defaults if needed:
                 boolean nullIncludes = (includes == null);
                 includes = nullIncludes
                     ? new String[] {SelectorUtils.DEEP_TREE_MATCH} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
                 if (basedir != null && !followSymlinks
                     && SYMLINK_UTILS.isSymbolicLink(basedir)) {
                     notFollowedSymlinks.add(basedir.getAbsolutePath());
                     basedir = null;
                 }
 
                 if (basedir == null) {
                     // if no basedir and no includes, nothing to do:
                     if (nullIncludes) {
                         return;
                     }
                 } else {
                     if (!basedir.exists()) {
                         if (errorOnMissingDir) {
                             illegal = new IllegalStateException("basedir "
                                                                 + basedir
                                                                 + DOES_NOT_EXIST_POSTFIX);
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
                 if (isIncluded(TokenizedPath.EMPTY_PATH)) {
                     if (!isExcluded(TokenizedPath.EMPTY_PATH)) {
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
         } catch (IOException ex) {
             throw new BuildException(ex);
         } finally {
             basedir = savedBase;
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
         ensureNonPatternSetsReady();
         Map newroots = new HashMap();
 
         // put in the newroots map the include patterns without
         // wildcard tokens
         for (int i = 0; i < includePatterns.length; i++) {
             String pattern = includePatterns[i].toString();
             if (!shouldSkipPattern(pattern)) {
                 newroots.put(includePatterns[i].rtrimWildcardTokens(),
                              pattern);
             }
         }
         for (Iterator iter = includeNonPatterns.entrySet().iterator();
              iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             String pattern = (String) entry.getKey();
             if (!shouldSkipPattern(pattern)) {
                 newroots.put((TokenizedPath) entry.getValue(), pattern);
             }
         }
 
         if (newroots.containsKey(TokenizedPath.EMPTY_PATH)
             && basedir != null) {
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
                 TokenizedPath currentPath = (TokenizedPath) entry.getKey();
                 String currentelement = currentPath.toString();
                 if (basedir == null
                     && !FileUtils.isAbsolutePath(currentelement)) {
                     continue;
                 }
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
                             myfile = currentPath.findFile(basedir, true);
                             if (myfile != null && basedir != null) {
                                 currentelement = FILE_UTILS.removeLeadingPath(
                                     basedir, myfile);
                                 if (!currentPath.toString()
                                     .equals(currentelement)) {
                                     currentPath =
                                         new TokenizedPath(currentelement);
                                 }
                             }
                         }
                     } catch (IOException ex) {
                         throw new BuildException(ex);
                     }
                 }
 
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive()) {
                     File f = currentPath.findFile(basedir, false);
                     if (f != null && f.exists()) {
                         // adapt currentelement to the case we've
                         // actually found
                         currentelement = (basedir == null)
                             ? f.getAbsolutePath()
                             : FILE_UTILS.removeLeadingPath(basedir, f);
                         myfile = f;
                         currentPath = new TokenizedPath(currentelement);
                     }
                 }
 
                 if (myfile != null && myfile.exists()) {
                     if (!followSymlinks && currentPath.isSymlink(basedir)) {
                         if (!isExcluded(currentPath)) {
                             notFollowedSymlinks.add(myfile.getAbsolutePath());
                         }
                         continue;
                     }
                     if (myfile.isDirectory()) {
                         if (isIncluded(currentPath)
                             && currentelement.length() > 0) {
                             accountForIncludedDir(currentPath, myfile, true);
                         }  else {
                             scandir(myfile, currentPath, true);
                         }
                     } else {
                         String originalpattern = (String) entry.getValue();
                         boolean included = isCaseSensitive()
                             ? originalpattern.equals(currentelement)
                             : originalpattern.equalsIgnoreCase(currentelement);
                         if (included) {
                             accountForIncludedFile(currentPath, myfile);
                         }
                     }
                 }
             }
         }
     }
 
     /**
      * true if the pattern specifies a relative path without basedir
      * or an absolute path not inside basedir.
      *
      * @since Ant 1.8.0
      */
     private boolean shouldSkipPattern(String pattern) {
         if (FileUtils.isAbsolutePath(pattern)) {
             //skip abs. paths not under basedir, if set:
             if (basedir != null
                 && !SelectorUtils.matchPatternStart(pattern,
                                                     basedir.getAbsolutePath(),
                                                     isCaseSensitive())) {
                 return true;
             }
         } else if (basedir == null) {
             //skip non-abs. paths if basedir == null:
             return true;
         }
         return false;
     }
 
     /**
      * Clear the result caches for a scan.
      */
     protected synchronized void clearResults() {
         filesIncluded    = new VectorSet();
         filesNotIncluded = new VectorSet();
         filesExcluded    = new VectorSet();
         filesDeselected  = new VectorSet();
         dirsIncluded     = new VectorSet();
         dirsNotIncluded  = new VectorSet();
         dirsExcluded     = new VectorSet();
         dirsDeselected   = new VectorSet();
         everythingIncluded = (basedir != null);
         scannedDirs.clear();
         notFollowedSymlinks.clear();
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
                 includes = nullIncludes
                     ? new String[] {SelectorUtils.DEEP_TREE_MATCH} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
                 String[] excl = new String[dirsExcluded.size()];
                 dirsExcluded.copyInto(excl);
 
                 String[] notIncl = new String[dirsNotIncluded.size()];
                 dirsNotIncluded.copyInto(notIncl);
 
                 ensureNonPatternSetsReady();
 
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
             TokenizedPath path  = new TokenizedPath(arr[i]);
             if (!couldHoldIncluded(path) || contentsExcluded(path)) {
                 scandir(new File(basedir, arr[i]), path, false);
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
         scandir(dir, new TokenizedPath(vpath), fast);
     }
 
     /**
      * Scan the given directory for files and directories. Found files and
      * directories are placed in their respective collections, based on the
      * matching of includes, excludes, and the selectors.  When a directory
      * is found, it is scanned recursively.
      *
      * @param dir   The directory to scan. Must not be <code>null</code>.
      * @param path The path relative to the base directory (needed to
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
     private void scandir(File dir, TokenizedPath path, boolean fast) {
         if (dir == null) {
             throw new BuildException("dir must not be null.");
         }
         String[] newfiles = dir.list();
         if (newfiles == null) {
             if (!dir.exists()) {
                 throw new BuildException(dir + DOES_NOT_EXIST_POSTFIX);
             } else if (!dir.isDirectory()) {
                 throw new BuildException(dir + " is not a directory.");
             } else {
                 throw new BuildException("IO error scanning directory '"
                                          + dir.getAbsolutePath() + "'");
             }
         }
         scandir(dir, path, fast, newfiles, new LinkedList());
     }
 
     private void scandir(File dir, TokenizedPath path, boolean fast,
                          String[] newfiles, LinkedList directoryNamesFollowed) {
         String vpath = path.toString();
         if (vpath.length() > 0 && !vpath.endsWith(File.separator)) {
             vpath += File.separator;
         }
 
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         if (!followSymlinks) {
             ArrayList noLinks = new ArrayList();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (SYMLINK_UTILS.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File file = new File(dir, newfiles[i]);
                         (file.isDirectory()
                             ? dirsExcluded : filesExcluded).addElement(name);
                         if (!isExcluded(name)) {
                             notFollowedSymlinks.add(file.getAbsolutePath());
                         }
                     } else {
                         noLinks.add(newfiles[i]);
                     }
                 } catch (IOException ioe) {
                     String msg = "IOException caught while checking "
                         + "for links, couldn't get canonical path!";
                     // will be caught and redirected to Ant's logging system
                     System.err.println(msg);
                     noLinks.add(newfiles[i]);
                 }
             }
             newfiles = (String[]) (noLinks.toArray(new String[noLinks.size()]));
         } else {
             directoryNamesFollowed.addFirst(dir.getName());
         }
 
         for (int i = 0; i < newfiles.length; i++) {
             String name = vpath + newfiles[i];
             TokenizedPath newPath = new TokenizedPath(path, newfiles[i]);
             File file = new File(dir, newfiles[i]);
             String[] children = file.list();
             if (children == null) { // probably file
                 if (isIncluded(newPath)) {
                     accountForIncludedFile(newPath, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             } else { // dir
 
                 if (followSymlinks
                     && causesIllegalSymlinkLoop(newfiles[i], dir,
                                                 directoryNamesFollowed)) {
                     // will be caught and redirected to Ant's logging system
                     System.err.println("skipping symbolic link "
                                        + file.getAbsolutePath()
                                        + " -- too many levels of symbolic"
                                        + " links.");
                     notFollowedSymlinks.add(file.getAbsolutePath());
                     continue;
                 }
 
                 if (isIncluded(newPath)) {
                     accountForIncludedDir(newPath, file, fast, children,
                                           directoryNamesFollowed);
                 } else {
                     everythingIncluded = false;
                     dirsNotIncluded.addElement(name);
-                    if (fast && couldHoldIncluded(newPath)) {
+                    if (fast && couldHoldIncluded(newPath)
+                        && !contentsExcluded(newPath)) {
                         scandir(file, newPath, fast, children,
                                 directoryNamesFollowed);
                     }
                 }
                 if (!fast) {
                     scandir(file, newPath, fast, children, directoryNamesFollowed);
                 }
             }
         }
 
         if (followSymlinks) {
             directoryNamesFollowed.removeFirst();
         }
     }
 
     /**
      * Process included file.
      * @param name  path of the file relative to the directory of the FileSet.
      * @param file  included File.
      */
     private void accountForIncludedFile(TokenizedPath name, File file) {
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
     private void accountForIncludedDir(TokenizedPath name, File file,
                                        boolean fast) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name, fast);
         }
     }
 
     private void accountForIncludedDir(TokenizedPath name,
                                        File file, boolean fast,
                                        String[] children,
                                        LinkedList directoryNamesFollowed) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name, fast, children, directoryNamesFollowed);
         }
     }
 
     private void processIncluded(TokenizedPath path,
                                  File file, Vector inc, Vector exc,
                                  Vector des) {
         String name = path.toString();
         if (inc.contains(name) || exc.contains(name) || des.contains(name)) {
             return;
         }
 
         boolean included = false;
         if (isExcluded(path)) {
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
         return isIncluded(new TokenizedPath(name));
     }
 
     /**
      * Test whether or not a name matches against at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         include pattern, or <code>false</code> otherwise.
      */
     private boolean isIncluded(TokenizedPath path) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? includeNonPatterns.containsKey(path.toString())
             : includeNonPatterns.containsKey(path.toString().toUpperCase())) {
             return true;
         }
         for (int i = 0; i < includePatterns.length; i++) {
             if (includePatterns[i].matchPath(path, isCaseSensitive())) {
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
         return couldHoldIncluded(new TokenizedPath(name));
     }
 
     /**
      * Test whether or not a name matches the start of at least one include
      * pattern.
      *
      * @param tokenizedName The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of at
      *         least one include pattern, or <code>false</code> otherwise.
      */
     private boolean couldHoldIncluded(TokenizedPath tokenizedName) {
         for (int i = 0; i < includePatterns.length; i++) {
             if (couldHoldIncluded(tokenizedName, includePatterns[i])) {
                 return true;
             }
         }
         for (Iterator iter = includeNonPatterns.values().iterator();
              iter.hasNext(); ) {
             if (couldHoldIncluded(tokenizedName,
                                   ((TokenizedPath) iter.next()).toPattern())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether or not a name matches the start of the given
      * include pattern.
      *
      * @param tokenizedName The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of the
      *         include pattern, or <code>false</code> otherwise.
      */
     private boolean couldHoldIncluded(TokenizedPath tokenizedName,
                                       TokenizedPattern tokenizedInclude) {
         return tokenizedInclude.matchStartOf(tokenizedName, isCaseSensitive())
             && isMorePowerfulThanExcludes(tokenizedName.toString())
             && isDeeper(tokenizedInclude, tokenizedName);
     }
 
     /**
      * Verify that a pattern specifies files deeper
      * than the level of the specified file.
      * @param pattern the pattern to check.
      * @param name the name to check.
      * @return whether the pattern is deeper than the name.
      * @since Ant 1.6.3
      */
     private boolean isDeeper(TokenizedPattern pattern, TokenizedPath name) {
         return pattern.containsPattern(SelectorUtils.DEEP_TREE_MATCH)
             || pattern.depth() > name.depth();
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
      *  @return true if there is no exclude pattern more powerful than
      *  this include pattern.
      *  @since Ant 1.6
      */
     private boolean isMorePowerfulThanExcludes(String name) {
         final String soughtexclude =
             name + File.separatorChar + SelectorUtils.DEEP_TREE_MATCH;
         for (int counter = 0; counter < excludePatterns.length; counter++) {
             if (excludePatterns[counter].toString().equals(soughtexclude))  {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Test whether all contents of the specified directory must be excluded.
      * @param path the path to check.
      * @return whether all the specified directory's contents are excluded.
      */
     /* package */ boolean contentsExcluded(TokenizedPath path) {
         for (int i = 0; i < excludePatterns.length; i++) {
             if (excludePatterns[i].endsWith(SelectorUtils.DEEP_TREE_MATCH)
                 && excludePatterns[i].withoutLastToken()
                    .matchPath(path, isCaseSensitive())) {
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
         return isExcluded(new TokenizedPath(name));
     }
 
     /**
      * Test whether or not a name matches against at least one exclude
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         exclude pattern, or <code>false</code> otherwise.
      */
     private boolean isExcluded(TokenizedPath name) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? excludeNonPatterns.containsKey(name.toString())
             : excludeNonPatterns.containsKey(name.toString().toUpperCase())) {
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
      * Absolute paths of all symbolic links that haven't been followed
      * but would have been followed had followsymlinks been true or
      * maxLevelsOfSymlinks been bigger.
      *
      * @since Ant 1.8.0
      */
     public synchronized String[] getNotFollowedSymlinks() {
         String[] links;
         synchronized (this) {
             links = (String[]) notFollowedSymlinks
                 .toArray(new String[notFollowedSymlinks.size()]);
         }
         Arrays.sort(links);
         return links;
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
     /* package */ synchronized void ensureNonPatternSetsReady() {
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
      * @param map Map to populate.
      * @param patterns String[] of patterns.
      * @since Ant 1.8.0
      */
     private TokenizedPattern[] fillNonPatternSet(Map map, String[] patterns) {
         ArrayList al = new ArrayList(patterns.length);
         for (int i = 0; i < patterns.length; i++) {
             if (!SelectorUtils.hasWildcards(patterns[i])) {
                 String s = isCaseSensitive()
                     ? patterns[i] : patterns[i].toUpperCase();
                 map.put(s, new TokenizedPath(s));
             } else {
                 al.add(new TokenizedPattern(patterns[i]));
             }
         }
         return (TokenizedPattern[]) al.toArray(new TokenizedPattern[al.size()]);
     }
 
     /**
      * Would following the given directory cause a loop of symbolic
      * links deeper than allowed?
      *
      * <p>Can only happen if the given directory has been seen at
      * least more often than allowed during the current scan and it is
      * a symbolic link and enough other occurences of the same name
      * higher up are symbolic links that point to the same place.</p>
      *
      * @since Ant 1.8.0
      */
     private boolean causesIllegalSymlinkLoop(String dirName, File parent,
                                              LinkedList directoryNamesFollowed) {
         try {
             if (directoryNamesFollowed.size() >= maxLevelsOfSymlinks
                 && CollectionUtils.frequency(directoryNamesFollowed, dirName)
                    >= maxLevelsOfSymlinks
                 && SYMLINK_UTILS.isSymbolicLink(parent, dirName)) {
 
                 ArrayList files = new ArrayList();
                 File f = FILE_UTILS.resolveFile(parent, dirName);
                 String target = f.getCanonicalPath();
                 files.add(target);
 
                 String relPath = "";
                 for (Iterator i = directoryNamesFollowed.iterator();
                      i.hasNext(); ) {
                     relPath += "../";
                     String dir = (String) i.next();
                     if (dirName.equals(dir)) {
                         f = FILE_UTILS.resolveFile(parent, relPath + dir);
                         files.add(f.getCanonicalPath());
                         if (files.size() > maxLevelsOfSymlinks
                             && CollectionUtils.frequency(files, target)
                                  > maxLevelsOfSymlinks) {
                             return true;
                         }
                     }
                 }
 
             }
             return false;
         } catch (IOException ex) {
             throw new BuildException("Caught error while checking for"
                                      + " symbolic links", ex);
         }
     }
 
 }
