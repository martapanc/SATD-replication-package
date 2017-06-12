diff --git a/WHATSNEW b/WHATSNEW
index bedb6c0cf..50a824a1e 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1283 +1,1288 @@
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
 
  * DOMElementWriter#encode used to employ special code before encoding
    ampersands so that &#123; remained &#123; rather than being turned
    into &amp;#123;.  This is no longer the case, ampersands will now
    be encoded unconditionally.
    Also DOMElementWriter#encodeData will treat CDATA sections containing a
    literal "]]>" sequence different now - it will split the CDATA
    section between the second "]" and ">" and create two sections.
    This affects <echoxml> task as well as the XML logger or JUnit
    formatter where ampersands will now always get encoded.
    In addition DOMElementWriter will now replace the characters \t, \r
    and \n in attribute values by entity references.
    Bugzilla Report 49404.
 
  * The list elements returned by ProjectHelper#getExtensionStack are
    now String arrays of length 3 rather than 2 in order to support the
    onMissingExtensionPoint attribute.
    Bugzilla Report 49473.   
 
  * When using <property file="..." prefix="..."/> properties defined
    inside the same file will only get used in expansions if the ${}
    reference uses the same prefix.  This is different from Ant 1.8.1
    but is the same behavior Ant 1.8.0 and earlier exhibited.
    A new attribute prefixValues can be used to re-enable the behavior
    of Ant 1.8.1.
    Bugzilla Report 49373.
 
  * The files and directories used by Git, Mercurial and Bazaar to
    store their information are now excluded by the defaultexcludes.
    Bugzilla Report 49624.
 
  * The <junit> task no longer generates TestListener events - which
    have been introduced in ant 1.7.0 - by default.  The task has a new
    attribute enableTestListenerEvents and a new "magic" property
    ant.junit.enabletestlistenerevents has been added that can be used
    to reinstate the old behavior.
 
 Fixed bugs:
 -----------
 
  * hostinfo now prefers addresses with a hostname over addresses without 
    a hostname, provided the addresses have the same scope.
    For local lookup, no IP address will be put in NAME / DOMAIN anymore.
    For remote lookup, if a host name was provided and only an IP address is 
    found, the IP address will no longer overwrite the host name provided to the
    task.
    Bugzilla Report 49513
 
  * mmap-based file copy problems under JDK 1.4 on Linux.
    Bugzilla Report 49430.
 
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
 
  * Ant would often scan directories even though there were known to
    only hold excluded files when evaluating filesets.  This never
    resulted in wrong results but degraded performance of the scan
    itself.
    Bugzilla Report 49420.
 
  * <javac> failed for long command lines on OS/2.
    Bugzilla Report 49425.
 
  * <junitreport> did not handle encodings well for stdout/stderr.
    Bugzilla Report 49418.
 
  * <junit> could issue a warning about multiple versions of Ant on the
    CLASSPATH if two CLASSPATH entries differed in case on a
    case-insensitive file system.
    Bugzilla Report 49041.
 
  * The <restrict> resource collection was checking every resource even if
    we actually just want the first one, like in the example of use of
    resourcelist in the documentation (getting the first available resource
    from a mirror list).
 
  * A race condition could lead to build failures if multiple <mkdir>
    tasks were trying to create the same directory.
    Bugzilla Report 49572.
 
  * the toString() method of the Resources class - and thus any
    ${toString:} expansion of a reference to a <resources> element -
    didn't iterate over its nested elements if it hadn't done so prior
    to the toString invocation already.
    Bugzilla Report 49588.
 
  * <apply> in parallel mode didn't work together with a nested
    <redirector> if maxparallel was <= 0 (the default) or no source
    files matched.
    Bugzilla Report 49594.
 
  * <jar filesetmanifest="merge"> didn't work for manifests added via
    <zipfileset>s that used the prefix or fullpath attributes.
    Bugzilla Report 49605.
 
  * <tempfile createfile="true"> would cause an error unless the prefix
    attribute has been specified.
    Bugzilla Report 49755.
 
  * If forked, after finished <java> was still reading the input stream
    for a bunch of characters, then stealing them from a following <input>.
    Bugzilla Report 49119.
 
  * Ant could be leaking threads for each forked process (started by
    <exec>, <apply>, <java> or similar tasks) that didn't receive input
    from a resource or string explicitly.
    Bugzilla Report 49587.
 
  * Project#setDefault threw an exception when null was passed in as
    argument, even though the javadoc says, null is a valid value.
    Bugzilla Report 49803.
 
  * runant.py would swallow the first argument if CLASSPATH wasn't set.
    Bugzilla Report 49963.
 
  * <taskdef> failed to load resources from jar files contained in a
    directory that has a "!" in its name.
    Bugzilla Report 50007.
 
  * ant.bat exit strategy improvements and issues
    make the exit codes work in environments where 4NT or MKS are installed
    Bugzilla Report 41039.
 
  * <signjar> would fail if used via its Java API and the File passed
    into the setJar method was not "normalized" (i.e. contained ".."
    segments).
    Bugzilla Report 50081.
 
  * <delete> ignored <fileset>'s errorOnMissingDir attribute
    Bugzilla Report 50124.
 
  * <symlink> failed to close files when reading a list of symbolic
    links from a properties file.
    Bugzilla Report 50136.
 
  * <parallel> could allow tasks to start executing even if a task
    scheduled to run before them timed out.
    Bugzilla Report 49527.
 
  * If a <junit> batch with multiple tests times out Ant logs a message
    about a test named Batch-With-Multiple-Tests since 1.8.0 but the
    logic that determined the Java package of this pseudo-test has been
    wrong.
    Bugzilla Report 45227.
 
  * <propertyfile> didn't preserve the original linefeed style when
    updating a file.
    Bugzilla Report 50049.
 
  * <zip>'s whenEmpty behavior never consulted the non-fileset
    resources so the task could fail even though resources have been
    provided using non-fileset resource collections.
    Bugzilla Issue 50115.
 
 *  ftp chmod could throw a NPE.
    Bugzilla report 50217.
 
 *  The project help (-p option in the command line) will now print
    the dependencies of the targets in debug mode (-d on the command
    line)
 
 Other changes:
 --------------
 
  * <concat>'s force attribute has been deprecated in favor of a new
    overwrite attribute that is consistent with <copy>'s attribute
    names.
 
  * You can now specify a list of methods to run in a JUnit test case.
    Bugzilla Report 34748.
 
  * properties in files read because of the -propertyfile command line
    option will now get resolved against other properties that are
    defined before the project starts executing (those from the same or
    earlier -propertfiles or defined via the -D option).
    Bugzilla Report 18732.
 
  * <pathelement>s can now contain wildcards in order to use wildcard
    CLASSPATH entries introduced with Java6.
    The wildcards are not expanded or even evaluated by Ant and will be
    used literally.  The resulting path may be unusable as a CLASSPATH
    for Java versions prior to Java6 and likely doesn't mean anything
    when used in any other way than a CLASSPATH for a forked Java VM. 
    Bugzilla Report 46842.
 
  * A new attribute allows targets to deal with non-existant extensions
    points, i.e. they can extend and extension-point if it has been
    defined or silently work as plain targets if it hasn't.  This is
    useful for targets that get included/imported in different
    scenarios where a given extension-point may or may not exist.
    Bugzilla Report 49473.   
 
  * Ant now logs a warning message if it fails to change the file
    modification time in for example when using <touch> or preserving
    timestamps in various tasks.
    Bugzilla Report 49485.
 
  * ProjectHelpers can now be installed dynamically via the <projecthelper>
    Ant task.
 
  * <import> is now able to switch to the proper ProjectHelper to parse
    the imported resource. This means that several kinds of different build
    files can import each other.
 
  * <copy tofile=""> now also works for non-filesystem resources.
    Bugzilla Report 49756.
 
  * The <linecontainsregexp> filter now supports a casesensitive
    attribute.
 
  * The <containsregexp> selector now supports casesensitive, multiline
    and singleline attributes.
    Bugzilla Report 49764.
 
  * A new <cutdirsmapper> can be used like wget's --cut-dirs option to
    strip leading directories from file names.
 
  * <javah> now supports the GNU project's gcjh compiler.
    Bugzilla Report 50149.
 
  * <checksum> supports additional views of a file's path as elements
    for a custom pattern.
    Bugzilla Report 50114.
 
  * JUnit XMLResultAggregator logs the stack trace of caught IO exceptions
    in verbose runs.
    Bugzilla Report 48836.
 
  * StringUtils.parseHumanSizes() should turn parse failures into
    BuildExceptions.
    Bugzilla Report 48835.
 
  * New task <bindtargets> to make a list of targets bound to some
    specified extension point.
 
  * Initial support for OpenJDK7 has been added.
 
  * Ant now uses java.net.CookieStore rather than
    java.util.ServiceLocator to detect whether the environment is a
    Java 1.6 system.  This means releases of gcj/gij at the time of
    this release of Ant are detected as Java 1.5 and not 1.6.
    Bugzilla Report 50256.
 
+ * It is now possible to write a compiler adapter for <javac> that
+   compiles sources with extensions other than .java (but that still
+   compile to .class files).
+   Bugzilla Report 48829.
+
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
 
  * <xslt> now fails early if a specified stylesheet doesn't exist.
    Bugzilla Report 34525.
 
  * <xslt> now has an option to supress transformer warnings.  This
    option only has an effect for processors that support this feature;
    the "trax" processor included with Ant does support it.
diff --git a/contributors.xml b/contributors.xml
index 8f8879a07..7f9bd361a 100644
--- a/contributors.xml
+++ b/contributors.xml
@@ -1,1074 +1,1078 @@
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
     <first>Adam</first>
     <last>Sotona</last>
   </name>
   <name>
     <first>Aleksandr</first>
     <last>Ishutin</last>
   </name>
   <name>
     <first>Alex</first>
     <last>Rosen</last>
   </name>
   <name>
     <first>Alexei</first>
     <last>Yudichev</last>
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
+    <last>Eisenberg</last>
+  </name>
+  <name>
+    <first>Andrew</first>
     <last>Everitt</last>
   </name>
   <name>
     <first>Andrew</first>
     <last>Stevens</last>
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
     <first>Bart</first>
     <last>Vanhaute</last>
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
     <first>Bernhard</first>
     <last>Rosenkraenzer</last>
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
     <last>Curnow</last>
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
     <first>Brian</first>
     <last>Repko</last>
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
     <first>Clark</first>
     <last>Archer</last>
   </name>
   <name>
     <first>Clemens</first>
     <last>Hammacher</last>
   </name>
   <name>
     <first>Clement</first>
     <last>OUDOT</last>
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
     <last>Richardson</last>
   </name>
   <name>
     <first>Craig</first>
     <last>Sandvik</last>
   </name>
   <name>
     <first>Curt</first>
     <last>Arnold</last>
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
     <first>Dale</first>
     <last>Sherwood</last>
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
     <first>Danny</first>
     <last>Yates</last>
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
     <last>Leal</last>
   </name>
   <name>
     <first>David</first>
     <middle>M.</middle>
     <last>Lloyd</last>
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
     <first>Devon</first>
     <middle>C.</middle>
     <last>Miller</last>
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
     <first>Dmitry</first>
     <middle>A.</middle>
     <last>Kuminov</last>
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
     <first>Gene-Sung</first>
     <last>Chung</last>
   </name>
   <name>
     <first>Georges-Etienne</first>
     <last>Legendre</last>
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
     <first>Gilbert</first>
     <last>Rebhan</last>
   </name>
   <name>
     <first>Gilles</first>
     <last>Scokart</last>
   </name>
   <name>
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
     <first>Greg</first>
     <last>Roodt</last>
   </name>
   <name>
     <first>Greg</first>
     <last>Schueler</last>
   </name>
   <name>
     <first>Günther</first>
     <last>Kögel</last>
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
     <first>Holger</first>
     <last>Joest</last>
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
     <first>J</first>
     <last>Bleijenbergh</last>
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
     <first>Joey</first>
     <last>Richey</last>
   </name>
   <name>
     <first>Johann</first>
     <last>Herunter</last>
   </name>
   <name>
     <first>John</first>
     <last>Sisson</last>
   </name>
   <name>
     <first>Jon</first>
     <last>Dickinson</last>
   </name>
   <name>
     <first>Jon</first>
     <last>Skeet</last>
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
     <middle>Connor</middle>
     <last>Arpe</last>
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
     <first>Kim</first>
     <last>Hansen</last>
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
     <last>Börger</last>
   </name>
   <name>
     <first>Mario</first>
     <last>Frasca</last>
   </name>
   <name>
     <first>Mariusz</first>
     <last>Nowostawski</last>
   </name>
   <name>
     <first>Mark</first>
     <last>DeLaFranier</last>
   </name>
   <name>
     <first>Mark</first>
     <last>Hecker</last>
   </name>
   <name>
     <first>Mark</first>
     <last>Salter</last>
   </name>
   <name>
     <first>Mark</first>
     <middle>R.</middle>
     <last>Diggory</last>
   </name>
   <name>
     <first>Mark</first>
     <middle>A.</middle>
     <last>Ziesemer</last>
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
     <first>Martin</first>
     <last>von Gagern</last>
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
     <last>Bayne</last>
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
     <first>Nathan</first>
     <last>Beyer</last>
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
     <first>Omer</first>
     <last>Shapira</last>
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
     <first>Pavan</first>
     <last>Bayyapu</last>
   </name>
   <name>
     <first>Pavel</first>
     <last>Jisl</last>
   </name>
   <name>
     <first>Paweł</first>
     <last>Zuzelski</last>
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index 8cad7544b..f74c5f92b 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,1207 +1,1238 @@
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
+import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
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
 
     private static final String JAVAC17 = "javac1.7";
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
     private CompilerAdapter nestedAdapter = null;
 
     /**
      * Javac task for compilation of Java files.
      */
     public Javac() {
         facade = new FacadeTaskHelper(assumedJavaVersion());
     }
 
     private String assumedJavaVersion() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4)) {
             return JAVAC14;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5)) {
             return JAVAC15;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6)) {
             return JAVAC16;
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_7)) {
             return JAVAC17;
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
      * "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "5", "6" and "7".
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
         return fork || EXTJAVAC.equalsIgnoreCase(getCompiler());
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
         if (JAVAC17.equalsIgnoreCase(anImplementation)
                 || JAVAC16.equalsIgnoreCase(anImplementation)
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
             if (JAVAC17.equalsIgnoreCase(nextSelected)
                     || JAVAC16.equalsIgnoreCase(nextSelected)
                     || JAVAC15.equalsIgnoreCase(nextSelected)
                     || JAVAC14.equalsIgnoreCase(nextSelected)
                     || JAVAC13.equalsIgnoreCase(nextSelected)) {
                 return nextSelected;
             }
         }
         if (CLASSIC.equalsIgnoreCase(anImplementation)) {
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
      * The property to set on compilation success.
      * This property will not be set if the compilation
      * fails, or if there are no files to compile.
      * @param updatedProperty the property name to use.
      * @since Ant 1.7.1.
      */
     public void setUpdatedProperty(String updatedProperty) {
         this.updatedProperty = updatedProperty;
     }
 
     /**
      * The property to set on compilation failure.
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
      * The classpath to use when loading the compiler implementation
      * if it is not a built-in one.
      *
      * @since Ant 1.8.0
      */
     public Path createCompilerClasspath() {
         return facade.getImplementationClasspath(getProject());
     }
 
     /**
      * Set the compiler adapter explicitly.
      * @since Ant 1.8.0
      */
     public void add(CompilerAdapter adapter) {
         if (nestedAdapter != null) {
             throw new BuildException("Can't have more than one compiler"
                                      + " adapter");
         }
         nestedAdapter = adapter;
     }
 
     /**
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
-        m.setFrom("*.java");
+        String[] extensions = findSupportedFileExtensions();
+        
+        for (int i = 0; i < extensions.length; i++) {
+            m.setFrom(extensions[i]);
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
+        }
+    }
+
+    private String[] findSupportedFileExtensions() {
+        String compilerImpl = getCompiler();
+        CompilerAdapter adapter =
+            nestedAdapter != null ? nestedAdapter :
+            CompilerAdapterFactory.getCompiler(compilerImpl, this,
+                                               createCompilerClasspath());
+        String[] extensions = null;
+        if (adapter instanceof CompilerAdapterExtension) {
+            extensions =
+                ((CompilerAdapterExtension) adapter).getSupportedFileExtensions();
+        } 
+
+        if (extensions == null) {
+            extensions = new String[] { "java" };
+        }
+
+        // now process the extensions to ensure that they are the
+        // right format
+        for (int i = 0; i < extensions.length; i++) {
+            if (!extensions[i].startsWith("*.")) {
+                extensions[i] = "*." + extensions[i];
+            }
+        }
+        return extensions; 
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
             || JAVAC17.equals(compilerImpl)
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
                 compilerImpl = EXTJAVAC;
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
                 nestedAdapter != null ? nestedAdapter :
                 CompilerAdapterFactory.getCompiler(compilerImpl, this,
                                                    createCompilerClasspath());
 
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
index 510dc4e09..b678984c5 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
@@ -1,676 +1,687 @@
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
 
 //Java5 style
 //import static org.apache.tools.ant.util.StringUtils.LINE_SEP;
 
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.Javac;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.taskdefs.condition.Os;
 
 /**
  * This is the default implementation for the CompilerAdapter interface.
  * Currently, this is a cut-and-paste of the original javac task.
  *
  * @since Ant 1.3
  */
-public abstract class DefaultCompilerAdapter implements CompilerAdapter {
+public abstract class DefaultCompilerAdapter
+    implements CompilerAdapter, CompilerAdapterExtension {
+
     private static final int COMMAND_LINE_LIMIT;
     static {
         if (Os.isFamily("os/2")) {
             // OS/2 CMD.EXE has a much smaller limit around 1K
             COMMAND_LINE_LIMIT = 1000;
         } else {
             COMMAND_LINE_LIMIT = 4096;  // 4K
         }
     }
     // CheckStyle:VisibilityModifier OFF - bc
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     protected Path src;
     protected File destDir;
     protected String encoding;
     protected boolean debug = false;
     protected boolean optimize = false;
     protected boolean deprecation = false;
     protected boolean depend = false;
     protected boolean verbose = false;
     protected String target;
     protected Path bootclasspath;
     protected Path extdirs;
     protected Path compileClasspath;
     protected Path compileSourcepath;
     protected Project project;
     protected Location location;
     protected boolean includeAntRuntime;
     protected boolean includeJavaRuntime;
     protected String memoryInitialSize;
     protected String memoryMaximumSize;
 
     protected File[] compileList;
     protected Javac attributes;
 
     //must keep for subclass BC, though unused:
     // CheckStyle:ConstantNameCheck OFF - bc
     protected static final String lSep = StringUtils.LINE_SEP;
 
     // CheckStyle:ConstantNameCheck ON
     // CheckStyle:VisibilityModifier ON
 
     /**
      * Set the Javac instance which contains the configured compilation
      * attributes.
      *
      * @param attributes a configured Javac task.
      */
     public void setJavac(Javac attributes) {
         this.attributes = attributes;
         src = attributes.getSrcdir();
         destDir = attributes.getDestdir();
         encoding = attributes.getEncoding();
         debug = attributes.getDebug();
         optimize = attributes.getOptimize();
         deprecation = attributes.getDeprecation();
         depend = attributes.getDepend();
         verbose = attributes.getVerbose();
         target = attributes.getTarget();
         bootclasspath = attributes.getBootclasspath();
         extdirs = attributes.getExtdirs();
         compileList = attributes.getFileList();
         compileClasspath = attributes.getClasspath();
         compileSourcepath = attributes.getSourcepath();
         project = attributes.getProject();
         location = attributes.getLocation();
         includeAntRuntime = attributes.getIncludeantruntime();
         includeJavaRuntime = attributes.getIncludejavaruntime();
         memoryInitialSize = attributes.getMemoryInitialSize();
         memoryMaximumSize = attributes.getMemoryMaximumSize();
     }
 
     /**
      * Get the Javac task instance associated with this compiler adapter
      *
      * @return the configured Javac task instance used by this adapter.
      */
     public Javac getJavac() {
         return attributes;
     }
 
     /**
+     * By default, only recognize files with a Java extension,
+     * but specialized compilers can recognize multiple kinds
+     * of files.
+     */
+    public String[] getSupportedFileExtensions() {
+        return new String[] { "java" };
+    }
+
+    /**
      * Get the project this compiler adapter was created in.
      * @return the owner project
      * @since Ant 1.6
      */
     protected Project getProject() {
         return project;
     }
 
     /**
      * Builds the compilation classpath.
      * @return the compilation class path
      */
     protected Path getCompileClasspath() {
         Path classpath = new Path(project);
 
         // add dest dir to classpath so that previously compiled and
         // untouched classes are on classpath
 
         if (destDir != null && getJavac().isIncludeDestClasses()) {
             classpath.setLocation(destDir);
         }
 
         // Combine the build classpath with the system classpath, in an
         // order determined by the value of build.sysclasspath
 
         Path cp = compileClasspath;
         if (cp == null) {
             cp = new Path(project);
         }
         if (includeAntRuntime) {
             classpath.addExisting(cp.concatSystemClasspath("last"));
         } else {
             classpath.addExisting(cp.concatSystemClasspath("ignore"));
         }
 
         if (includeJavaRuntime) {
             classpath.addJavaRuntime();
         }
 
         return classpath;
     }
 
     /**
      * Get the command line arguments for the switches.
      * @param cmd the command line
      * @return the command line
      */
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd) {
         return setupJavacCommandlineSwitches(cmd, false);
     }
 
     /**
      * Does the command line argument processing common to classic and
      * modern.  Doesn't add the files to compile.
      * @param cmd the command line
      * @param useDebugLevel if true set set the debug level with the -g switch
      * @return the command line
      */
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd,
                                                         boolean useDebugLevel) {
         Path classpath = getCompileClasspath();
         // For -sourcepath, use the "sourcepath" value if present.
         // Otherwise default to the "srcdir" value.
         Path sourcepath = null;
         if (compileSourcepath != null) {
             sourcepath = compileSourcepath;
         } else {
             sourcepath = src;
         }
 
         String memoryParameterPrefix = assumeJava11() ? "-J-" : "-J-X";
         if (memoryInitialSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryInitialSize setting.",
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "ms" + memoryInitialSize);
             }
         }
 
         if (memoryMaximumSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryMaximumSize setting.",
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "mx" + memoryMaximumSize);
             }
         }
 
         if (attributes.getNowarn()) {
             cmd.createArgument().setValue("-nowarn");
         }
 
         if (deprecation) {
             cmd.createArgument().setValue("-deprecation");
         }
 
         if (destDir != null) {
             cmd.createArgument().setValue("-d");
             cmd.createArgument().setFile(destDir);
         }
 
         cmd.createArgument().setValue("-classpath");
 
         // Just add "sourcepath" to classpath ( for JDK1.1 )
         // as well as "bootclasspath" and "extdirs"
         if (assumeJava11()) {
             Path cp = new Path(project);
 
             Path bp = getBootClassPath();
             if (bp.size() > 0) {
                 cp.append(bp);
             }
 
             if (extdirs != null) {
                 cp.addExtdirs(extdirs);
             }
             cp.append(classpath);
             cp.append(sourcepath);
             cmd.createArgument().setPath(cp);
         } else {
             cmd.createArgument().setPath(classpath);
             // If the buildfile specifies sourcepath="", then don't
             // output any sourcepath.
             if (sourcepath.size() > 0) {
                 cmd.createArgument().setValue("-sourcepath");
                 cmd.createArgument().setPath(sourcepath);
             }
             if (target != null) {
                 cmd.createArgument().setValue("-target");
                 cmd.createArgument().setValue(target);
             }
 
             Path bp = getBootClassPath();
             if (bp.size() > 0) {
                 cmd.createArgument().setValue("-bootclasspath");
                 cmd.createArgument().setPath(bp);
             }
 
             if (extdirs != null && extdirs.size() > 0) {
                 cmd.createArgument().setValue("-extdirs");
                 cmd.createArgument().setPath(extdirs);
             }
         }
 
         if (encoding != null) {
             cmd.createArgument().setValue("-encoding");
             cmd.createArgument().setValue(encoding);
         }
         if (debug) {
             if (useDebugLevel && !assumeJava11()) {
                 String debugLevel = attributes.getDebugLevel();
                 if (debugLevel != null) {
                     cmd.createArgument().setValue("-g:" + debugLevel);
                 } else {
                     cmd.createArgument().setValue("-g");
                 }
             } else {
                 cmd.createArgument().setValue("-g");
             }
         } else if (getNoDebugArgument() != null) {
             cmd.createArgument().setValue(getNoDebugArgument());
         }
         if (optimize) {
             cmd.createArgument().setValue("-O");
         }
 
         if (depend) {
             if (assumeJava11()) {
                 cmd.createArgument().setValue("-depend");
             } else if (assumeJava12()) {
                 cmd.createArgument().setValue("-Xdepend");
             } else {
                 attributes.log("depend attribute is not supported by the "
                                + "modern compiler", Project.MSG_WARN);
             }
         }
 
         if (verbose) {
             cmd.createArgument().setValue("-verbose");
         }
 
         addCurrentCompilerArgs(cmd);
 
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern.  Doesn't
      * add the files to compile.
      * @param cmd the command line
      * @return the command line
      */
     protected Commandline setupModernJavacCommandlineSwitches(Commandline cmd) {
         setupJavacCommandlineSwitches(cmd, true);
         if (attributes.getSource() != null && !assumeJava13()) {
             cmd.createArgument().setValue("-source");
             String source = attributes.getSource();
             if (source.equals("1.1") || source.equals("1.2")) {
                 // support for -source 1.1 and -source 1.2 has been
                 // added with JDK 1.4.2 - and isn't present in 1.5.0+
                 cmd.createArgument().setValue("1.3");
             } else {
                 cmd.createArgument().setValue(source);
             }
         } else if ((assumeJava15() || assumeJava16() || assumeJava17())
                    && attributes.getTarget() != null) {
             String t = attributes.getTarget();
             if (t.equals("1.1") || t.equals("1.2") || t.equals("1.3")
                 || t.equals("1.4")) {
                 String s = t;
                 if (t.equals("1.1")) {
                     // 1.5.0 doesn't support -source 1.1
                     s = "1.2";
                 }
                 setImplicitSourceSwitch((assumeJava15() || assumeJava16())
                                         ? "1.5 in JDK 1.5 and 1.6"
                                         : "1.7 in JDK 1.7",
                                         cmd, s, t);
             } else if (assumeJava17() && (t.equals("1.5") || t.equals("1.6"))) {
                 setImplicitSourceSwitch("1.7 in JDK 1.7", cmd, t, t);
             }
         }
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern and adds
      * the files to compile as well.
      * @return the command line
      */
     protected Commandline setupModernJavacCommand() {
         Commandline cmd = new Commandline();
         setupModernJavacCommandlineSwitches(cmd);
 
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     /**
      * Set up the command line.
      * @return the command line
      */
     protected Commandline setupJavacCommand() {
         return setupJavacCommand(false);
     }
 
     /**
      * Does the command line argument processing for classic and adds
      * the files to compile as well.
      * @param debugLevelCheck if true set the debug level with the -g switch
      * @return the command line
      */
     protected Commandline setupJavacCommand(boolean debugLevelCheck) {
         Commandline cmd = new Commandline();
         setupJavacCommandlineSwitches(cmd, debugLevelCheck);
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &quot;niceSourceList&quot;
      * @param cmd the command line
      */
     protected void logAndAddFilesToCompile(Commandline cmd) {
         attributes.log("Compilation " + cmd.describeArguments(),
                        Project.MSG_VERBOSE);
 
         StringBuffer niceSourceList = new StringBuffer("File");
         if (compileList.length != 1) {
             niceSourceList.append("s");
         }
         niceSourceList.append(" to be compiled:");
 
         niceSourceList.append(StringUtils.LINE_SEP);
 
         for (int i = 0; i < compileList.length; i++) {
             String arg = compileList[i].getAbsolutePath();
             cmd.createArgument().setValue(arg);
             niceSourceList.append("    ");
             niceSourceList.append(arg);
             niceSourceList.append(StringUtils.LINE_SEP);
         }
 
         attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * Do the compile with the specified arguments.
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      * @return the exit code of the compilation
      */
     protected int executeExternalCompile(String[] args, int firstFileName) {
         return executeExternalCompile(args, firstFileName, true);
     }
 
     /**
      * Do the compile with the specified arguments.
      *
      * <p>The working directory if the executed process will be the
      * project's base directory.</p>
      *
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      * @param quoteFiles - if set to true, filenames containing
      * spaces will be quoted when they appear in the external file.
      * This is necessary when running JDK 1.4's javac and probably
      * others.
      * @return the exit code of the compilation
      *
      * @since Ant 1.6
      */
     protected int executeExternalCompile(String[] args, int firstFileName,
                                          boolean quoteFiles) {
         String[] commandArray = null;
         File tmpFile = null;
 
         try {
             /*
              * Many system have been reported to get into trouble with
              * long command lines - no, not only Windows ;-).
              *
              * POSIX seems to define a lower limit of 4k, so use a temporary
              * file if the total length of the command line exceeds this limit.
              */
             if (Commandline.toString(args).length() > COMMAND_LINE_LIMIT
                 && firstFileName >= 0) {
                 BufferedWriter out = null;
                 try {
                     tmpFile = FILE_UTILS.createTempFile(
                         "files", "", getJavac().getTempdir(), true, true);
                     out = new BufferedWriter(new FileWriter(tmpFile));
                     for (int i = firstFileName; i < args.length; i++) {
                         if (quoteFiles && args[i].indexOf(" ") > -1) {
                             args[i] = args[i].replace(File.separatorChar, '/');
                             out.write("\"" + args[i] + "\"");
                         } else {
                             out.write(args[i]);
                         }
                         out.newLine();
                     }
                     out.flush();
                     commandArray = new String[firstFileName + 1];
                     System.arraycopy(args, 0, commandArray, 0, firstFileName);
                     commandArray[firstFileName] = "@" + tmpFile;
                 } catch (IOException e) {
                     throw new BuildException("Error creating temporary file",
                                              e, location);
                 } finally {
                     FileUtils.close(out);
                 }
             } else {
                 commandArray = args;
             }
 
             try {
                 Execute exe = new Execute(
                                   new LogStreamHandler(attributes,
                                                        Project.MSG_INFO,
                                                        Project.MSG_WARN));
                 if (Os.isFamily("openvms")) {
                     //Use the VM launcher instead of shell launcher on VMS
                     //for java
                     exe.setVMLauncher(true);
                 }
                 exe.setAntRun(project);
                 exe.setWorkingDirectory(project.getBaseDir());
                 exe.setCommandline(commandArray);
                 exe.execute();
                 return exe.getExitValue();
             } catch (IOException e) {
                 throw new BuildException("Error running " + args[0]
                         + " compiler", e, location);
             }
         } finally {
             if (tmpFile != null) {
                 tmpFile.delete();
             }
         }
     }
 
     /**
      * Add extdirs to classpath
      * @param classpath the classpath to use
      * @deprecated since 1.5.x.
      *             Use org.apache.tools.ant.types.Path#addExtdirs instead.
      */
     protected void addExtdirsToClasspath(Path classpath) {
         classpath.addExtdirs(extdirs);
     }
 
     /**
      * Adds the command line arguments specific to the current implementation.
      * @param cmd the command line to use
      */
     protected void addCurrentCompilerArgs(Commandline cmd) {
         cmd.addArguments(getJavac().getCurrentCompilerArgs());
     }
 
     /**
      * Shall we assume JDK 1.1 command line switches?
      * @return true if jdk 1.1
      * @since Ant 1.5
      */
     protected boolean assumeJava11() {
         return "javac1.1".equals(attributes.getCompilerVersion());
     }
 
     /**
      * Shall we assume JDK 1.2 command line switches?
      * @return true if jdk 1.2
      * @since Ant 1.5
      */
     protected boolean assumeJava12() {
         return "javac1.2".equals(attributes.getCompilerVersion());
     }
 
     /**
      * Shall we assume JDK 1.3 command line switches?
      * @return true if jdk 1.3
      * @since Ant 1.5
      */
     protected boolean assumeJava13() {
         return "javac1.3".equals(attributes.getCompilerVersion());
     }
 
     /**
      * Shall we assume JDK 1.4 command line switches?
      * @return true if jdk 1.4
      * @since Ant 1.6.3
      */
     protected boolean assumeJava14() {
         return "javac1.4".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4));
     }
 
     /**
      * Shall we assume JDK 1.5 command line switches?
      * @return true if JDK 1.5
      * @since Ant 1.6.3
      */
     protected boolean assumeJava15() {
         return "javac1.5".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5));
     }
 
     /**
      * Shall we assume JDK 1.6 command line switches?
      * @return true if JDK 1.6
      * @since Ant 1.7
      */
     protected boolean assumeJava16() {
         return "javac1.6".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6));
     }
 
     /**
      * Shall we assume JDK 1.7 command line switches?
      * @return true if JDK 1.7
      * @since Ant 1.8.2
      */
     protected boolean assumeJava17() {
         return "javac1.7".equals(attributes.getCompilerVersion())
             || ("classic".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_7))
             || ("modern".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_7))
             || ("extJavac".equals(attributes.getCompilerVersion())
                 && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_7));
     }
 
     /**
      * Combines a user specified bootclasspath with the system
      * bootclasspath taking build.sysclasspath into account.
      *
      * @return a non-null Path instance that combines the user
      * specified and the system bootclasspath.
      */
     protected Path getBootClassPath() {
         Path bp = new Path(project);
         if (bootclasspath != null) {
             bp.append(bootclasspath);
         }
         return bp.concatSystemBootClasspath("ignore");
     }
 
     /**
      * The argument the compiler wants to see if the debug attribute
      * has been set to false.
      *
      * <p>A return value of <code>null</code> means no argument at all.</p>
      *
      * @return "-g:none" unless we expect to invoke a JDK 1.1 compiler.
      *
      * @since Ant 1.6.3
      */
     protected String getNoDebugArgument() {
         return assumeJava11() ? null : "-g:none";
     }
 
     private void setImplicitSourceSwitch(String defaultDetails, Commandline cmd,
                                          String target, String source) {
         attributes.log("", Project.MSG_WARN);
         attributes.log("          WARNING", Project.MSG_WARN);
         attributes.log("", Project.MSG_WARN);
         attributes.log("The -source switch defaults to " + defaultDetails + ".",
                        Project.MSG_WARN);
         attributes.log("If you specify -target " + target
                        + " you now must also specify -source " + source
                        + ".", Project.MSG_WARN);
         attributes.log("Ant will implicitly add -source " + source
                        + " for you.  Please change your build file.",
                        Project.MSG_WARN);
         cmd.createArgument().setValue("-source");
         cmd.createArgument().setValue(source);
     }
 
 }
 
