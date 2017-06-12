diff --git a/WHATSNEW b/WHATSNEW
index 632253961..99fa30380 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1121 +1,1124 @@
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
 
  * Launcher fixed to pass the right class loader parent.
    Bugzilla Report 48633.
 
  * <junitreport> mishandled ${line.separator}.
    Bugzilla Report 51049.
 
  * <junitreport> did not work in embedded environments on JDK 7.
    Nor did <xslt> when using Xalan redirects.
    Bugzilla Report 51668, 52382.
 
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
    Bugzilla Report 42696.
 
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
 
  * packagemapper now honors the handleDirSep attribute.
    Bugzilla Report 51086.
 
  * the attributes of macrodef tasks had their values run through
    property expansion twice.
    Bugzilla Report 42046.
 
  * jvc doesn't like it if source file names in argument files are
    quoted.
    Bugzilla Report 31667.
 
  * ZipFile didn't work properly for archives using unicode extra
    fields rather than UTF-8 filenames and the EFS-Flag.
 
+ * Access to DirectoryScanner's default excludes wasn't synchronized.
+   BigZilla Report 52188.
+
 Other changes:
 --------------
 
  * -f/-file/-buildfile accepts a directory containing build.xml.
 
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
    Bugzilla Report 45786.
 
  * <loadfile> and <loadresource> used to log at level INFO to signal a
    property hasn't been set when the resource was empty even if the
    quiet attribute was set to true.  They will now use VERBOSE
    instead.
    Bugzilla Report 52107.
 
  * <javac> has a new attribute createMissingPackageInfoClass that can
    be set to false to prevent Ant from creating empty dummy classes
    used for up-to-date-ness checks.
    Bugzilla Report 52096.
 
  * URLResources#isExists has become less noisy.
    Bugzilla Report 51829.
 
  * The <retry> task has a new optional attribute retryDelay that can
    be used to make the task sleep between retry attempts.
    Bugzilla Report 52076.
 
  * <signjar> has new attributes that control the signature and digest
    algorithms.
    Bugzilla Report 52344.
 
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
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index cb98f35c3..1ece1183a 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1884 +1,1892 @@
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Map;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.types.selectors.TokenizedPath;
 import org.apache.tools.ant.types.selectors.TokenizedPattern;
 import org.apache.tools.ant.util.CollectionUtils;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.SymbolicLinkUtils;
 import org.apache.tools.ant.util.VectorSet;
 
 /**
  * Class for scanning a directory for files/directories which match certain
  * criteria.
  * <p>
  * These criteria consist of selectors and patterns which have been specified.
  * With the selectors you can select which files you want to have included.
  * Files which are not selected are excluded. With patterns you can include
  * or exclude files based on their filename.
  * <p>
  * The idea is simple. A given directory is recursively scanned for all files
  * and directories. Each file/directory is matched against a set of selectors,
  * including special support for matching against filenames with include and
  * and exclude patterns. Only files/directories which match at least one
  * pattern of the include pattern list or other file selector, and don't match
  * any pattern of the exclude pattern list or fail to match against a required
  * selector will be placed in the list of files/directories found.
  * <p>
  * When no list of include patterns is supplied, "**" will be used, which
  * means that everything will be matched. When no list of exclude patterns is
  * supplied, an empty list is used, such that nothing will be excluded. When
  * no selectors are supplied, none are applied.
  * <p>
  * The filename pattern matching is done as follows:
  * The name to be matched is split up in path segments. A path segment is the
  * name of a directory or file, which is bounded by
  * <code>File.separator</code> ('/' under UNIX, '\' under Windows).
  * For example, "abc/def/ghi/xyz.java" is split up in the segments "abc",
  * "def","ghi" and "xyz.java".
  * The same is done for the pattern against which should be matched.
  * <p>
  * The segments of the name and the pattern are then matched against each
  * other. When '**' is used for a path segment in the pattern, it matches
  * zero or more path segments of the name.
  * <p>
  * There is a special case regarding the use of <code>File.separator</code>s
  * at the beginning of the pattern and the string to match:<br>
  * When a pattern starts with a <code>File.separator</code>, the string
  * to match must also start with a <code>File.separator</code>.
  * When a pattern does not start with a <code>File.separator</code>, the
  * string to match may not start with a <code>File.separator</code>.
  * When one of these rules is not obeyed, the string will not
  * match.
  * <p>
  * When a name path segment is matched against a pattern path segment, the
  * following special characters can be used:<br>
  * '*' matches zero or more characters<br>
  * '?' matches one character.
  * <p>
  * Examples:
  * <p>
  * "**\*.class" matches all .class files/dirs in a directory tree.
  * <p>
  * "test\a??.java" matches all files/dirs which start with an 'a', then two
  * more characters and then ".java", in a directory called test.
  * <p>
  * "**" matches everything in a directory tree.
  * <p>
  * "**\test\**\XYZ*" matches all files/dirs which start with "XYZ" and where
  * there is a parent directory called test (e.g. "abc\test\def\ghi\XYZ123").
  * <p>
  * Case sensitivity may be turned off if necessary. By default, it is
  * turned on.
  * <p>
  * Example of usage:
  * <pre>
  *   String[] includes = {"**\\*.class"};
  *   String[] excludes = {"modules\\*\\**"};
  *   ds.setIncludes(includes);
  *   ds.setExcludes(excludes);
  *   ds.setBasedir(new File("test"));
  *   ds.setCaseSensitive(true);
  *   ds.scan();
  *
  *   System.out.println("FILES:");
  *   String[] files = ds.getIncludedFiles();
  *   for (int i = 0; i < files.length; i++) {
  *     System.out.println(files[i]);
  *   }
  * </pre>
  * This will scan a directory called test for .class files, but excludes all
  * files in all proper subdirectories of a directory called "modules"
  *
  */
 public class DirectoryScanner
        implements FileScanner, SelectorScanner, ResourceFactory {
 
     /** Is OpenVMS the operating system we're running on? */
     private static final boolean ON_VMS = Os.isFamily("openvms");
 
     /**
      * Patterns which should be excluded by default.
      *
      * <p>Note that you can now add patterns to the list of default
      * excludes.  Added patterns will not become part of this array
      * that has only been kept around for backwards compatibility
      * reasons.</p>
      *
      * @deprecated since 1.6.x.
      *             Use the {@link #getDefaultExcludes getDefaultExcludes}
      *             method instead.
      */
     protected static final String[] DEFAULTEXCLUDES = {
         // Miscellaneous typical temporary files
         SelectorUtils.DEEP_TREE_MATCH + "/*~",
         SelectorUtils.DEEP_TREE_MATCH + "/#*#",
         SelectorUtils.DEEP_TREE_MATCH + "/.#*",
         SelectorUtils.DEEP_TREE_MATCH + "/%*%",
         SelectorUtils.DEEP_TREE_MATCH + "/._*",
 
         // CVS
         SelectorUtils.DEEP_TREE_MATCH + "/CVS",
         SelectorUtils.DEEP_TREE_MATCH + "/CVS/" + SelectorUtils.DEEP_TREE_MATCH,
         SelectorUtils.DEEP_TREE_MATCH + "/.cvsignore",
 
         // SCCS
         SelectorUtils.DEEP_TREE_MATCH + "/SCCS",
         SelectorUtils.DEEP_TREE_MATCH + "/SCCS/" + SelectorUtils.DEEP_TREE_MATCH,
 
         // Visual SourceSafe
         SelectorUtils.DEEP_TREE_MATCH + "/vssver.scc",
 
         // Subversion
         SelectorUtils.DEEP_TREE_MATCH + "/.svn",
         SelectorUtils.DEEP_TREE_MATCH + "/.svn/" + SelectorUtils.DEEP_TREE_MATCH,
 
         // Git
         SelectorUtils.DEEP_TREE_MATCH + "/.git",
         SelectorUtils.DEEP_TREE_MATCH + "/.git/" + SelectorUtils.DEEP_TREE_MATCH,
         SelectorUtils.DEEP_TREE_MATCH + "/.gitattributes",
         SelectorUtils.DEEP_TREE_MATCH + "/.gitignore",
         SelectorUtils.DEEP_TREE_MATCH + "/.gitmodules",
 
         // Mercurial
         SelectorUtils.DEEP_TREE_MATCH + "/.hg",
         SelectorUtils.DEEP_TREE_MATCH + "/.hg/" + SelectorUtils.DEEP_TREE_MATCH,
         SelectorUtils.DEEP_TREE_MATCH + "/.hgignore",
         SelectorUtils.DEEP_TREE_MATCH + "/.hgsub",
         SelectorUtils.DEEP_TREE_MATCH + "/.hgsubstate",
         SelectorUtils.DEEP_TREE_MATCH + "/.hgtags",
 
         // Bazaar
         SelectorUtils.DEEP_TREE_MATCH + "/.bzr",
         SelectorUtils.DEEP_TREE_MATCH + "/.bzr/" + SelectorUtils.DEEP_TREE_MATCH,
         SelectorUtils.DEEP_TREE_MATCH + "/.bzrignore",
 
         // Mac
         SelectorUtils.DEEP_TREE_MATCH + "/.DS_Store"
     };
 
     /**
      * default value for {@link #maxLevelsOfSymlinks maxLevelsOfSymlinks}
      * @since Ant 1.8.0
      */
     public static final int MAX_LEVELS_OF_SYMLINKS = 5;
     /**
      * The end of the exception message if something that should be
      * there doesn't exist.
      */
     public static final String DOES_NOT_EXIST_POSTFIX = " does not exist.";
 
     /** Helper. */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /** Helper. */
     private static final SymbolicLinkUtils SYMLINK_UTILS =
         SymbolicLinkUtils.getSymbolicLinkUtils();
 
     /**
      * Patterns which should be excluded by default.
      *
      * @see #addDefaultExcludes()
      */
-    private static Set defaultExcludes = new HashSet();
+    private static final Set defaultExcludes = new HashSet();
     static {
         resetDefaultExcludes();
     }
 
     // CheckStyle:VisibilityModifier OFF - bc
 
     /** The base directory to be scanned. */
     protected File basedir;
 
     /** The patterns for the files to be included. */
     protected String[] includes;
 
     /** The patterns for the files to be excluded. */
     protected String[] excludes;
 
     /** Selectors that will filter which files are in our candidate list. */
     protected FileSelector[] selectors = null;
 
     /**
      * The files which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector filesExcluded;
 
     /**
      * The directories which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector dirsExcluded;
 
     /**
      * The files which matched at least one include and no excludes and
      * which a selector discarded.
      */
     protected Vector filesDeselected;
 
     /**
      * The directories which matched at least one include and no excludes
      * but which a selector discarded.
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
+        synchronized (defaultExcludes) {
         return (String[]) defaultExcludes.toArray(new String[defaultExcludes
                                                              .size()]);
+        }
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
+        synchronized (defaultExcludes) {
         return defaultExcludes.add(s);
+        }
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
+        synchronized (defaultExcludes) {
         return defaultExcludes.remove(s);
+        }
     }
 
     /**
      * Go back to the hardwired default exclude patterns.
      *
      * @since Ant 1.6
      */
     public static void resetDefaultExcludes() {
-        defaultExcludes = new HashSet();
+        synchronized (defaultExcludes) {
+            defaultExcludes.clear();
         for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
             defaultExcludes.add(DEFAULTEXCLUDES[i]);
         }
+        }
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
             if (children == null || (children.length == 0 && file.isFile())) {
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
                     if (fast && couldHoldIncluded(newPath)
                         && !contentsExcluded(newPath)) {
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
-        newExcludes = new String[excludesLength + defaultExcludes.size()];
+        String[] defaultExcludesTemp = getDefaultExcludes();
+        newExcludes = new String[excludesLength + defaultExcludesTemp.length];
         if (excludesLength > 0) {
             System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
         }
-        String[] defaultExcludesTemp = getDefaultExcludes();
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
