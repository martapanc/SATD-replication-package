diff --git a/WHATSNEW b/WHATSNEW
index 09c1a6df8..ba0a73f73 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1137 +1,1142 @@
 Changes from Ant 1.8.2 TO Ant 1.8.3
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * The Enumeration returned by AntClassLoader#getResources used to
    return null in nextElement after hasNextElement would return false.
    It has been changed to throw a NoSuchElementException instead so
    that it now adheres to the contract of java.util.Enumeration.
    Bugzilla Report 51579.
 
 Fixed bugs:
 -----------
 
  * <junitreport> did not work in embedded environments on JDK 7.
    Bugzilla Report 51668.
 
  * Encoding of unicode escape sequences by the property file task
    Bugzilla Report 50515.
 
  * The code that implicitly sets the -source switch if only -target
    has been specified in <javac> was broken for Java 5 and 6.
    Bugzilla Report 50578.
 
  * MailLogger ignore the Maillogger.starttls.enable property.
    Bugzilla Report 50668.
    
  * Delete task example does not work
    Bugzilla Report 50816.  
 
  * <splash>'s proxy handling has been delegated to <setproxy>
    internally so the two tasks are consistent.  <splash>'s way of not
    setting a proxy caused problems with other Java libraries.
    Bugzilla Report 50888.
 
  * Include task breaks dependencies or extension-points for multiple
    files.
    Bugzilla Report 50866.
 
  * Read on System.in hangs for forked java task.
    Bugzilla Report 50960.
 
  * FileResource specified using basedir/name attributes was non-functional.
 
  * Resource collection implementation of mapped PropertySet returned
    unusable resources.
 
  * The hasmethod condition failed with a NullPointerException when
    ignoresystemclasses is true and Ant tried to load a "restricted
    class" - i.e. a class that the Java VM will only accept when loaded
    via the bootclassloader (a java.* class).
    It will now fail with a more useful error message.
    Bugzilla Report 51035.
 
  * Exec task may mix the stderr and stdout output while logging it
    Bugzilla Report 50507.
    
  * Missing space between "finished" and timestamp in task/target 
    finish message from ProfileLogger.
    Bugzilla Report 51109.     
 
  * Redirecting the output of a java, exec or apply task could print in the
    error output stream some "Pipe broken" errors.
    Bugzilla Report 48789.
 
  * ZipFile failed to clean up some resources which could lead to
    OutOfMemoryException while unzipping large archives.
    A similar problem in ZipArchiveOutputStream has been fixed as well.
    Bugzilla Report 42969.
 
  * quiet attribute added to the copy and move tasks, to be used together
    with failonerror=false, so warnings won't get logged 
    Bugzilla Report 48789.
 
  * System.in was closed and not readable anymore by the DefaultInputHandler 
    when Ant is used via its Java API.
    Bugzilla Report 51161
 
  * <sync> only supported a single non-fileset resource collection even
    though the manual said it could be multiple.
 
  * <sync> didn't work properly when working on resource collections.
    Bugzilla Report 51462.
 
  * <augment> cause a NullPointerException if it was used in a target
    that was invoked by multiple targets from the command line.
    Bugzilla Report 50894.
 
  * The ZipFile class could read past the start of the file if the
    given file is not a ZIP archive and it is smaller than the size of
    a ZIP "end of central directory record".
 
  * <javac> would create the empty package-info.class file in the wrong
    directory if no destdir was specified.  Note it may still pick the
    wrong directory if you specify more than one source directory but
    no destDir.  It is highly recommended that you always explicitly
    specify the destDir attribute.
    Bugzilla Report 51947.
 
 Other changes:
 --------------
 
  * The <javacc>, <jjtree> and <jjdoc> now support a new maxmemory
    attribute.
    Bugzilla Report 50513.
 
  * the documented inputstring attribute of sshexec has been
    implemented and the actually existing attribute inputproperty
    documented.
    Bugzilla Report 50576.
 
  * The concat task now permits the name of its exposed resource
    by means of its 'resourcename' attribute.
 
  * The expandproperties filter now accepts a nested propertyset
    which, if specified, provides the properties for expansion.
    Bugzilla Report 51044.
 
  * <junit filtertrace="true"/> will no longer filter out the very
    first line of the stacktrace containing the original exception
    message even if it matches one of the filter expressions.
    
  * Upgraded to Apache AntUnit 1.2
 
  * Provide read access to Mkdir.dir.  Bugzilla Report 51684.
 
  * <delete> and <move> have a new attribute performGCOnFailedDelete
    that may - when set to true - help resolve some problems with
    deleting empty directories on NFS shares.
    Bugzilla Report 45807.
 
  * <loadfile> and <loadresource> used to log at level INFO to signal a
    property hasn't been set when the resource was empty even if the
    quiet attribute was set to true.  They will now use VERBOSE
    instead.
-   Bugzilla Report 52107.   
+   Bugzilla Report 52107.
+
+ * <javac> has a new attribute createMissingPackageInfoClass that can
+   be set to false to prevent Ant from creating empty dummy classes
+   used for up-to-date-ness checks.
+   Bugzilla Report 52096.
 
 Changes from Ant 1.8.1 TO Ant 1.8.2
 ===================================
 
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
    argument, even though the Javadoc says null is a valid value.
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
 
  * A new attribute allows targets to deal with nonexistent extension
    points, i.e. they can extend an extension-point if it has been
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
 
  * It is now possible to write a compiler adapter for <javac> that
    compiles sources with extensions other than .java (but that still
    compile to .class files).
    Bugzilla Report 48829.
 
  * The performance of VectorSet#add(Object) has been improved which
    should also benefit any operation that scans directories in Ant.
    Bugzilla Report 50200.
 
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
diff --git a/manual/Tasks/javac.html b/manual/Tasks/javac.html
index 168f6b9cb..40c4199fe 100644
--- a/manual/Tasks/javac.html
+++ b/manual/Tasks/javac.html
@@ -1,842 +1,858 @@
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
 <p>Note: Apache Ant uses only the names of the source and class files to find
 the classes that need a rebuild. It will not scan the source and therefore
 will have no knowledge about nested classes, classes that are named different
 from the source file, and so on. See the
 <a href="../Tasks/depend.html"><code>&lt;depend&gt;</code></a> task
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
 affect all <code>&lt;javac&gt;</code> tasks throughout the build, by
 setting the <code>compiler</code> attribute, specific to the current
 <code>&lt;javac&gt;</code> task or by using a nested element of any
 <a href="typedef.html">typedef</a>fed or
 <a href="componentdef.html">componentdef</a>fed type that implements
 <code>org.apache.tools.ant.taskdefs.compilers.CompilerAdapter</code>.
 <a name="compilervalues">Valid values for either the
 <code>build.compiler</code> property or the <code>compiler</code>
 attribute are:</a></p>
 <ul>
   <li><code>classic</code> (the standard compiler of JDK 1.1/1.2) &ndash;
       <code>javac1.1</code> and
       <code>javac1.2</code> can be used as aliases.</li>
   <li><code>modern</code> (the standard compiler of JDK 1.3/1.4/1.5/1.6/1.7) &ndash;
       <code>javac1.3</code> and
       <code>javac1.4</code> and
       <code>javac1.5</code> and
       <code>javac1.6</code> and
       <code>javac1.7</code> (<em>since Ant 1.8.2</em>) can be used as aliases.</li>
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
+  <tr>
+    <td valign="top">createMissingPackageInfoClass</td>
+    <td valign="top">
+      Some package level annotations in <code>package-info.java</code>
+      files don't create any <code>package-info.class</code> files so
+      Ant would recompile the same file every time.<br/>
+      Starting with Ant 1.8 Ant will create an
+      empty <code>package-info.class</code> for
+      each <code>package-info.java</code> if there isn't one created
+      by the compiler.<br/>
+      In some setups this additional class causes problems and it can
+      be suppressed by setting this attribute to "false".
+      <em>Since Ant 1.8.3</em>.
+    </td>
+    <td align="center" valign="top">No - default is "true"</td>
+  </tr>
 </table>
 
 <h3>Parameters specified as nested elements</h3>
 <p>This task forms an implicit <a href="../Types/fileset.html">FileSet</a> and
 supports most attributes of <code>&lt;fileset&gt;</code>
 (<code>dir</code> becomes <code>srcdir</code>) as well as the nested
 <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code> and
 <code>&lt;patternset&gt;</code> elements.</p>
 <h4><code>srcdir</code>, <code>classpath</code>, <code>sourcepath</code>,
 <code>bootclasspath</code> and <code>extdirs</code></h4>
 <p><code>&lt;javac&gt;</code>'s <code>srcdir</code>, <code>classpath</code>,
 <code>sourcepath</code>, <code>bootclasspath</code>, and
 <code>extdirs</code> attributes are
 <a href="../using.html#path">path-like structures</a>
 and can also be set via nested
 <code>&lt;src&gt;</code> (note the different name!),
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
 
 <h4>compilerclasspath <em>since Ant 1.8.0</em></h4>
 
 <p>A <a href="../using.html#path">PATH like structure</a> holding the
   classpath to use when loading the compiler implementation if a
   custom class has been specified.  Doesn't have any effect when
   using one of the built-in compilers.</p>
 
 <h4>Any nested element of a type that implements CompilerAdapter
   <em>since Ant 1.8.0</em></h4>
 
 <p>If a defined type implements the <code>CompilerAdapter</code>
   interface a nested element of that type can be used as an
   alternative to the <code>compiler</code> attribute.</p>
 
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
 
 <p>If you want to use a custom
   CompilerAdapter <code>org.example.MyAdapter</code> you can either
   use the compiler attribute:</p>
 <pre>
 &lt;javac srcdir="${src.dir}"
        destdir="${classes.dir}"
        compiler="org.example.MyAdapter"/&gt;
 </pre>
 <p>or a define a type and nest this into the task like in:</p>
 <pre>
 &lt;componentdef classname="org.example.MyAdapter"
               name="myadapter"/&gt;
 &lt;javac srcdir="${src.dir}"
        destdir="${classes.dir}"&gt;
   &lt;myadapter/&gt;
 &lt;/javac&gt;
 </pre>
 <p>in which case your compiler adapter can support attributes and
   nested elements of its own.</p>
 
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
     &lt;compilerarg arg="-Xbootclasspath/p:${toString:lib.path.ref}"/&gt;
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
   <p>With Ant 1.7.1 a different kind of logic was introduced that
     involved the timestamp of the directory that would normally
     contain the .class file.  This logic turned out to lead to Ant not
     recompiling <code>package-info.java</code> in certain setup.</p>
   <p>Starting with Ant 1.8.0 Ant will create
     "empty" <code>package-info.class</code> files if it compiles
     a <code>package-info.java</code> and
     no <code>package-info.class</code> file has been created by the
     compiler itself.</p>
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index dd80b727f..a5502524c 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,1241 +1,1256 @@
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
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
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
 
+    private boolean createMissingPackageInfoClass = true;
+
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
+     * Whether package-info.class files will be created by Ant
+     * matching package-info.java files that have been compiled but
+     * didn't create class files themselves.
+     *
+     * @since Ant 1.8.3
+     */
+    public void setCreateMissingPackageInfoClass(boolean b) {
+        createMissingPackageInfoClass = b;
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
         String[] extensions = findSupportedFileExtensions();
         
         for (int i = 0; i < extensions.length; i++) {
             m.setFrom(extensions[i]);
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
     }
 
     private String[] findSupportedFileExtensions() {
         String compilerImpl = getCompiler();
         CompilerAdapter adapter =
             nestedAdapter != null ? nestedAdapter :
             CompilerAdapterFactory.getCompiler(compilerImpl, this,
                                                createCompilerClasspath());
         String[] extensions = null;
         if (adapter instanceof CompilerAdapterExtension) {
             extensions =
                 ((CompilerAdapterExtension) adapter).getSupportedFileExtensions();
         } 
 
         if (extensions == null) {
             extensions = new String[] { "java" };
         }
 
         // now process the extensions to ensure that they are the
         // right format
         for (int i = 0; i < extensions.length; i++) {
             if (!extensions[i].startsWith("*.")) {
                 extensions[i] = "*." + extensions[i];
             }
         }
         return extensions; 
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
+                if (createMissingPackageInfoClass) {
                 try {
                     generateMissingPackageInfoClasses(destDir != null
                                                       ? destDir
                                                       : getProject()
                                                       .resolveFile(src.list()[0]));
                 } catch (IOException x) {
                     // Should this be made a nonfatal warning?
                     throw new BuildException(x, getLocation());
                 }
+                }
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
     private void generateMissingPackageInfoClasses(File dest) throws IOException {
         for (Iterator i = packageInfos.entrySet().iterator(); i.hasNext(); ) {
             Map.Entry entry = (Map.Entry) i.next();
             String pkg = (String) entry.getKey();
             Long sourceLastMod = (Long) entry.getValue();
             File pkgBinDir = new File(dest, pkg.replace('/', File.separatorChar));
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
diff --git a/src/tests/antunit/taskdefs/javac-test.xml b/src/tests/antunit/taskdefs/javac-test.xml
index 7201d2da1..3e5ceba04 100644
--- a/src/tests/antunit/taskdefs/javac-test.xml
+++ b/src/tests/antunit/taskdefs/javac-test.xml
@@ -1,188 +1,200 @@
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
 
   <property name="javac-dir" location="${output}/javac-dir" />
   <property name="build-dir" location="${javac-dir}/build" />
 
   <target name="test-includeDestClasses">
     <property name="DATE" value="09/10/1999 4:30 pm" />
     <delete dir="${javac-dir}/src" />
     <mkdir dir="${javac-dir}/src" />
     <echo file="${javac-dir}/src/A.java">
       public class A { B b;}
     </echo>
     <echo file="${javac-dir}/src/B.java">
       public class B { }
     </echo>
     <delete dir="${javac-dir}/classes" quiet="yes" />
     <mkdir dir="${javac-dir}/classes" />
     <javac srcdir="${javac-dir}/src" destdir="${javac-dir}/classes" />
     <touch file="${javac-dir}/src/B.java" datetime="${DATE}" />
     <touch file="${javac-dir}/classes/B.class" datetime="${DATE}" />
     <!-- following should not update B.class -->
     <delete quiet="yes" file="${javac-dir}/classes/A.class" />
     <javac srcdir="${javac-dir}/src" destdir="${javac-dir}/classes" />
     <au:assertTrue>
       <isfileselected file="${javac-dir}/classes/B.class">
         <date datetime="${DATE}" when="equal" />
       </isfileselected>
     </au:assertTrue>
     <!-- following should update B.class -->
     <delete quiet="yes" file="${javac-dir}/classes/A.class" />
     <javac srcdir="${javac-dir}/src" destdir="${javac-dir}/classes" includeDestClasses="no" />
     <au:assertFalse>
       <isfileselected file="${javac-dir}/classes/B.class">
         <date datetime="${DATE}" when="equal" />
       </isfileselected>
     </au:assertFalse>
   </target>
 
   <target name="test-updated-property">
     <delete quiet="yes" dir="${build-dir}" />
     <mkdir dir="${build-dir}" />
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}" updatedProperty="classes-updated" />
     <au:assertTrue>
       <equals arg1="${classes-updated}" arg2="true" />
     </au:assertTrue>
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}" updatedProperty="classes-updated-2" />
     <au:assertFalse>
       <isset property="classes-updated-2" />
     </au:assertFalse>
   </target>
 
   <target name="test-error-property">
     <delete quiet="yes" dir="${build-dir}" />
     <mkdir dir="${build-dir}" />
     <javac srcdir="javac-dir/good-src" destdir="${build-dir}" failOnError="false" errorProperty="compile-failed" />
     <au:assertTrue>
       <equals arg1="${compile-failed}" arg2="${compile-failed}" />
     </au:assertTrue>
     <javac srcdir="javac-dir/bad-src" destdir="${build-dir}" failOnError="false" errorProperty="compile-failed" />
     <au:assertTrue>
       <equals arg1="${compile-failed}" arg2="true" />
     </au:assertTrue>
   </target>
 
   <target name="setUpForPackageInfoJava">
     <mkdir dir="${javac-dir}/src/a" />
     <mkdir dir="${build-dir}" />
     <echo file="${javac-dir}/src/a/package-info.java">
       <![CDATA[
 /**
  * Some test javadocs at the package level.
  */
 ]]>
     </echo>
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}" updatedProperty="first-pass" />
     <au:assertPropertyEquals name="first-pass" value="true" />
   </target>
 
   <target name="testPackageInfoJava"
           depends="setUpForPackageInfoJava"
           description="https://issues.apache.org/bugzilla/show_bug.cgi?id=43114">
     <!-- no changes, shouldn't recompile, the initial bug -->
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}" updatedProperty="second-pass" />
     <au:assertFalse>
       <isset property="second-pass" />
     </au:assertFalse>
     <sleep seconds="2" />
 
     <!-- change package-info.java but make containing target dir even
          more recent - the regression in Ant 1.7.1 -->
     <touch file="${javac-dir}/src/a/package-info.java" />
     <sleep seconds="2" />
     <touch>
       <file file="${build-dir}/a" />
     </touch>
     <javac srcdir="${javac-dir}/src" destdir="${build-dir}" updatedProperty="third-pass" />
     <au:assertPropertyEquals name="third-pass" value="true" />
   </target>
 
   <target name="testPackageInfoJavaNoDest"
           depends="setUpForPackageInfoJava"
           description="https://issues.apache.org/bugzilla/show_bug.cgi?id=51947">
     <javac srcdir="${javac-dir}/src" updatedProperty="first-pass" />
     <au:assertPropertyEquals name="first-pass" value="true" />
 
     <!-- no changes, shouldn't recompile, the initial bug -->
     <javac srcdir="${javac-dir}/src" updatedProperty="second-pass" />
     <au:assertFalse>
       <isset property="second-pass" />
     </au:assertFalse>
     <sleep seconds="2" />
 
     <!-- change package-info.java but make containing target dir even
          more recent - the regression in Ant 1.7.1 -->
     <touch file="${javac-dir}/src/a/package-info.java" />
     <sleep seconds="2" />
     <touch>
       <file file="${javac-dir}/src/a" />
     </touch>
     <javac srcdir="${javac-dir}/src" updatedProperty="third-pass" />
     <au:assertPropertyEquals name="third-pass" value="true" />
   </target>
 
+  <target name="testSuppressPackageInfoClass"
+          depends="setUpForPackageInfoJava"
+          description="https://issues.apache.org/bugzilla/show_bug.cgi?id=52096">
+    <au:assertFileExists file="${build-dir}/a/package-info.class"/>
+    <delete file="${build-dir}/a/package-info.class"/>
+    <javac srcdir="${javac-dir}/src" destdir="${build-dir}"
+           createMissingPackageInfoClass="false"
+           updatedProperty="second-pass" />
+    <au:assertPropertyEquals name="second-pass" value="true" />
+    <au:assertFileDoesntExist file="${build-dir}/a/package-info.class"/>
+  </target>
+
   <target name="-create-javac-adapter">
     <property name="adapter.dir" location="${output}/adapter" />
     <mkdir dir="${input}/org/example" />
     <echo file="${input}/org/example/Adapter.java">
       <![CDATA[
 package org.example;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
 import org.apache.tools.ant.taskdefs.Javac;
 
 public class Adapter implements CompilerAdapter {
     public void setJavac(Javac attributes) {}
     public boolean execute() {
         System.err.println("adapter called");
         return true;
     }
 }]]>
     </echo>
     <mkdir dir="${resources}" />
     <javac srcdir="${input}" destdir="${resources}" />
   </target>
 
   <target name="testCompilerNotFound" depends="-create-javac-adapter">
     <au:expectfailure>
       <javac srcdir="${input}" destdir="${output}" compiler="org.example.Adapter" />
     </au:expectfailure>
     <au:assertLogDoesntContain text="adapter called" />
   </target>
 
   <target name="testCompilerClasspath" depends="-create-javac-adapter" description="https://issues.apache.org/bugzilla/show_bug.cgi?id=11143">
     <mkdir dir="${output}" />
     <javac srcdir="${input}" destdir="${output}" compiler="org.example.Adapter">
       <compilerclasspath location="${resources}" />
     </javac>
     <au:assertLogContains text="adapter called" />
   </target>
 
   <target name="testCompilerAsNestedElement" depends="-create-javac-adapter">
     <componentdef classname="org.example.Adapter" name="myjavac">
       <classpath location="${resources}" />
     </componentdef>
     <mkdir dir="${output}" />
     <javac srcdir="${input}" destdir="${output}">
       <myjavac />
     </javac>
     <au:assertLogContains text="adapter called" />
   </target>
 </project>
