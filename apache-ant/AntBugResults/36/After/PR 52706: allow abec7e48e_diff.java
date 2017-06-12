diff --git a/WHATSNEW b/WHATSNEW
index d6bcd4281..ddcc2eaa2 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1041 +1,1050 @@
 Changes from Ant 1.8.4 TO Ant 1.9.0
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * FixCRLF used to treat the EOL value ASIS to convert to the system property 
    line.separator. Specified was that ASIS would leave the EOL characters alone,
    the task now really leaves the EOL characters alone. This also implies that
    EOL ASIS will not insert a newline even if fixlast is set to true.
    Bugzilla report 53036
 
+ * The CommandLauncher hierarchy that used to be a set of inner
+   classes of Execute has been extracted to the
+   org.apache.tools.ant.taskdefs.launcher package.
+
 Fixed bugs:
 -----------
 
  * External XML catalog resolver failed to use project basedir when given an
    unmentioned relative path like the internal resolver does.
    Bugzilla Report 52754.
 
  * Fixed some potential stream leaks.
    Bugzilla Reports 52738, 52740, 52742, 52743.
 
  * Updated documentation to fix spelling errors / broken links.
    Bugzilla Reports 53215, 53291, 53202
    
  * Unable to override system properties. It was not possible not to override
    system properties from the command line (or from a property file).
    Bugzilla Report 51792
    
  * <javac> by default fails when run on JDK 8.
    Bugzilla Report 53347.
 
  * ExtensionPoint doesn't work with nested import/include
    Bugzilla Report 53405.
 
 Other changes:
 --------------
 
-* merged the ZIP package from Commons Compress, it can now read
-  archives using Zip64 extensions (files and archives bigger that 4GB
-  and with more that 64k entries).
+ * merged the ZIP package from Commons Compress, it can now read
+   archives using Zip64 extensions (files and archives bigger that 4GB
+   and with more that 64k entries).
+
+ * a new task <commandlaucher> can be used to configure the
+   CommandLauncher used by Ant when forking external programs or new
+   Java VMs.
+   Bugzilla Report 52706.
 
 Changes from Ant 1.8.3 TO Ant 1.8.4
 ===================================
 
 Fixed bugs:
 -----------
 
  * Ported libbzip2's fallback sort algorithm to CBZip2OutputStream to
    speed up compression in certain edge cases.  Merge from Commons
    Compress.
 
    Using specially crafted inputs this can be used as a denial of
    service attack.
    See http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2012-2098
 
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
 
  * Removed buggy duplicate JAR list in RPM mode.
    Bugzilla Report 52556.
 
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
    property expansion twice. Still true by default, but can be disabled.
    Bugzilla Report 42046.
 
  * jvc doesn't like it if source file names in argument files are
    quoted.
    Bugzilla Report 31667.
 
  * ZipFile didn't work properly for archives using unicode extra
    fields rather than UTF-8 filenames and the EFS-Flag.
 
  * Access to DirectoryScanner's default excludes wasn't synchronized.
    Bugzilla Report 52188.
 
  * When a Project instance was created by a custom tasks its
    createTask method didn't work.
    Bugzilla Report 50788.
 
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
 
  * Initial support for Java 8.
 
  * <sshexec> can optionally create a pseudo terminal (like ssh -t)
    Bugzilla Report 52554.
 
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
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/CommandLauncherTask.java b/src/main/org/apache/tools/ant/taskdefs/CommandLauncherTask.java
new file mode 100644
index 000000000..c0e60a8a0
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/CommandLauncherTask.java
@@ -0,0 +1,33 @@
+package org.apache.tools.ant.taskdefs;
+
+import org.apache.tools.ant.BuildException;
+import org.apache.tools.ant.Task;
+import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
+
+public class CommandLauncherTask extends Task {
+    private boolean vmLauncher;
+    private CommandLauncher commandLauncher;
+
+    public synchronized void addConfigured(CommandLauncher commandLauncher) {
+        if (this.commandLauncher != null) {
+            throw new BuildException("Only one CommandLauncher can be installed");
+        }
+        this.commandLauncher = commandLauncher;
+    }
+
+    @Override
+    public void execute() {
+        if (commandLauncher != null) {
+            if (vmLauncher) {
+                CommandLauncher.setVMLauncher(getProject(), commandLauncher);
+            } else {
+                CommandLauncher.setShellLauncher(getProject(), commandLauncher);
+            }
+        }
+    }
+
+    public void setVmLauncher(boolean vmLauncher) {
+        this.vmLauncher = vmLauncher;
+    }
+
+}
diff --git a/src/main/org/apache/tools/ant/taskdefs/Execute.java b/src/main/org/apache/tools/ant/taskdefs/Execute.java
index 521184754..bd06533be 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Execute.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Execute.java
@@ -1,1260 +1,742 @@
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
 
 import java.io.BufferedReader;
-import java.io.BufferedWriter;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
-import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.StringReader;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.Vector;
 
 import org.apache.tools.ant.BuildException;
-import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.condition.Os;
+import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.StringUtils;
 
 /**
  * Runs an external program.
- *
+ * 
  * @since Ant 1.2
- *
  */
 public class Execute {
 
     private static final int ONE_SECOND = 1000;
 
-    /** Invalid exit code.
-     * set to {@link Integer#MAX_VALUE}
+    /**
+     * Invalid exit code. set to {@link Integer#MAX_VALUE}
      */
     public static final int INVALID = Integer.MAX_VALUE;
 
-    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
-
     private String[] cmdl = null;
     private String[] env = null;
     private int exitValue = INVALID;
     private ExecuteStreamHandler streamHandler;
-    private ExecuteWatchdog watchdog;
+    private final ExecuteWatchdog watchdog;
     private File workingDirectory = null;
     private Project project = null;
     private boolean newEnvironment = false;
 
     /** Controls whether the VM is used to launch commands, where possible. */
     private boolean useVMLauncher = true;
 
     private static String antWorkingDirectory = System.getProperty("user.dir");
-    private static CommandLauncher vmLauncher = null;
-    private static CommandLauncher shellLauncher = null;
     private static Map/*<String, String>*/ procEnvironment = null;
 
     /** Used to destroy processes when the VM exits. */
     private static ProcessDestroyer processDestroyer = new ProcessDestroyer();
 
     /** Used for replacing env variables */
     private static boolean environmentCaseInSensitive = false;
 
-    /*
-     * Builds a command launcher for the OS and JVM we are running under.
-     */
     static {
-        // Try using a JDK 1.3 launcher
-        try {
-            if (!Os.isFamily("os/2")) {
-                vmLauncher = new Java13CommandLauncher();
-            }
-        } catch (NoSuchMethodException exc) {
-            // Ignore and keep trying
-        }
-        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
-            // Mac
-            shellLauncher = new MacCommandLauncher(new CommandLauncher());
-        } else if (Os.isFamily("os/2")) {
-            // OS/2
-            shellLauncher = new OS2CommandLauncher(new CommandLauncher());
-        } else if (Os.isFamily("windows")) {
+        if (Os.isFamily("windows")) {
             environmentCaseInSensitive = true;
-            CommandLauncher baseLauncher = new CommandLauncher();
-
-            if (!Os.isFamily("win9x")) {
-                // Windows XP/2000/NT
-                shellLauncher = new WinNTCommandLauncher(baseLauncher);
-            } else {
-                // Windows 98/95 - need to use an auxiliary script
-                shellLauncher
-                    = new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
-            }
-        } else if (Os.isFamily("netware")) {
-
-            CommandLauncher baseLauncher = new CommandLauncher();
-
-            shellLauncher
-                = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
-        } else if (Os.isFamily("openvms")) {
-            // OpenVMS
-            try {
-                shellLauncher = new VmsCommandLauncher();
-            } catch (NoSuchMethodException exc) {
-            // Ignore and keep trying
-            }
-        } else {
-            // Generic
-            shellLauncher = new ScriptCommandLauncher("bin/antRun",
-                new CommandLauncher());
         }
     }
 
     /**
      * Set whether or not you want the process to be spawned.
      * Default is not spawned.
-     *
+     * 
      * @param spawn if true you do not want Ant
      *              to wait for the end of the process.
      *              Has no influence in here, the calling task contains
      *              and acts accordingly
      *
      * @since Ant 1.6
      * @deprecated
      */
+    @Deprecated
     public void setSpawn(boolean spawn) {
         // Method did not do anything to begin with
     }
 
     /**
      * Find the list of environment variables for this process.
-     *
+     * 
      * @return a map containing the environment variables.
      * @since Ant 1.8.2
      */
     public static synchronized Map/*<String,String>*/ getEnvironmentVariables() {
         if (procEnvironment != null) {
             return procEnvironment;
         }
         if (JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_5)
             && !Os.isFamily("openvms")) {
             try {
                 procEnvironment = (Map) System.class
                     .getMethod("getenv", new Class[0])
                     .invoke(null, new Object[0]);
                 return procEnvironment;
             } catch (Exception x) {
                 x.printStackTrace();
             }
         }
 
         procEnvironment = new LinkedHashMap();
         try {
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             Execute exe = new Execute(new PumpStreamHandler(out));
             exe.setCommandline(getProcEnvCommand());
             // Make sure we do not recurse forever
             exe.setNewenvironment(true);
             int retval = exe.execute();
             if (retval != 0) {
                 // Just try to use what we got
             }
             BufferedReader in =
                 new BufferedReader(new StringReader(toString(out)));
 
             if (Os.isFamily("openvms")) {
                 procEnvironment = getVMSLogicals(in);
                 return procEnvironment;
             }
             String var = null;
             String line, lineSep = StringUtils.LINE_SEP;
             while ((line = in.readLine()) != null) {
                 if (line.indexOf('=') == -1) {
                     // Chunk part of previous env var (UNIX env vars can
                     // contain embedded new lines).
                     if (var == null) {
                         var = lineSep + line;
                     } else {
                         var += lineSep + line;
                     }
                 } else {
                     // New env var...append the previous one if we have it.
                     if (var != null) {
                         int eq = var.indexOf("=");
                         procEnvironment.put(var.substring(0, eq),
                                             var.substring(eq + 1));
                     }
                     var = line;
                 }
             }
             // Since we "look ahead" before adding, there's one last env var.
             if (var != null) {
                 int eq = var.indexOf("=");
                 procEnvironment.put(var.substring(0, eq), var.substring(eq + 1));
             }
         } catch (java.io.IOException exc) {
             exc.printStackTrace();
             // Just try to see how much we got
         }
         return procEnvironment;
     }
 
     /**
      * Find the list of environment variables for this process.
-     *
+     * 
      * @return a vector containing the environment variables.
      * The vector elements are strings formatted like variable = value.
      * @deprecated use #getEnvironmentVariables instead
      */
+    @Deprecated
     public static synchronized Vector getProcEnvironment() {
         Vector v = new Vector();
         Iterator it = getEnvironmentVariables().entrySet().iterator();
         while (it.hasNext()) {
             Map.Entry entry = (Map.Entry) it.next();
             v.add(entry.getKey() + "=" + entry.getValue());
         }
         return v;
     }
 
     /**
      * This is the operation to get our environment.
      * It is a notorious troublespot pre-Java1.5, and should be approached
      * with extreme caution.
+     * 
      * @return
      */
     private static String[] getProcEnvCommand() {
         if (Os.isFamily("os/2")) {
             // OS/2 - use same mechanism as Windows 2000
-            return new String[] {"cmd", "/c", "set" };
+            return new String[] {"cmd", "/c", "set"};
         } else if (Os.isFamily("windows")) {
             // Determine if we're running under XP/2000/NT or 98/95
             if (Os.isFamily("win9x")) {
                 // Windows 98/95
-                return new String[] {"command.com", "/c", "set" };
+                return new String[] {"command.com", "/c", "set"};
             } else {
                 // Windows XP/2000/NT/2003
-                return new String[] {"cmd", "/c", "set" };
+                return new String[] {"cmd", "/c", "set"};
             }
         } else if (Os.isFamily("z/os") || Os.isFamily("unix")) {
             // On most systems one could use: /bin/sh -c env
 
             // Some systems have /bin/env, others /usr/bin/env, just try
             String[] cmd = new String[1];
             if (new File("/bin/env").canRead()) {
                 cmd[0] = "/bin/env";
             } else if (new File("/usr/bin/env").canRead()) {
                 cmd[0] = "/usr/bin/env";
             } else {
                 // rely on PATH
                 cmd[0] = "env";
             }
             return cmd;
         } else if (Os.isFamily("netware") || Os.isFamily("os/400")) {
             // rely on PATH
             return new String[] {"env"};
         } else if (Os.isFamily("openvms")) {
             return new String[] {"show", "logical"};
         } else {
             // MAC OS 9 and previous
-            //TODO: I have no idea how to get it, someone must fix it
+            // TODO: I have no idea how to get it, someone must fix it
             return null;
         }
     }
 
     /**
      * ByteArrayOutputStream#toString doesn't seem to work reliably on
      * OS/390, at least not the way we use it in the execution
      * context.
-     *
+     * 
      * @param bos the output stream that one wants to read.
      * @return the output stream as a string, read with
      * special encodings in the case of z/os and os/400.
-     *
      * @since Ant 1.5
      */
     public static String toString(ByteArrayOutputStream bos) {
         if (Os.isFamily("z/os")) {
             try {
                 return bos.toString("Cp1047");
             } catch (java.io.UnsupportedEncodingException e) {
-                //noop default encoding used
+                // noop default encoding used
             }
         } else if (Os.isFamily("os/400")) {
             try {
                 return bos.toString("Cp500");
             } catch (java.io.UnsupportedEncodingException e) {
-                //noop default encoding used
+                // noop default encoding used
             }
         }
         return bos.toString();
     }
 
     /**
      * Creates a new execute object using <code>PumpStreamHandler</code> for
      * stream handling.
      */
     public Execute() {
         this(new PumpStreamHandler(), null);
     }
 
     /**
      * Creates a new execute object.
-     *
+     * 
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler) {
         this(streamHandler, null);
     }
 
     /**
      * Creates a new execute object.
-     *
+     * 
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
-     * @param watchdog a watchdog for the subprocess or <code>null</code> to
+     * @param watchdog a watchdog for the subprocess or <code>null</code>
      *        to disable a timeout for the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler,
                    ExecuteWatchdog watchdog) {
         setStreamHandler(streamHandler);
         this.watchdog = watchdog;
-        //By default, use the shell launcher for VMS
+        // By default, use the shell launcher for VMS
         //
         if (Os.isFamily("openvms")) {
             useVMLauncher = false;
         }
     }
 
     /**
      * Set the stream handler to use.
+     * 
      * @param streamHandler ExecuteStreamHandler.
      * @since Ant 1.6
      */
     public void setStreamHandler(ExecuteStreamHandler streamHandler) {
         this.streamHandler = streamHandler;
     }
 
     /**
      * Returns the commandline used to create a subprocess.
-     *
+     * 
      * @return the commandline used to create a subprocess.
      */
     public String[] getCommandline() {
         return cmdl;
     }
 
     /**
      * Sets the commandline of the subprocess to launch.
-     *
+     * 
      * @param commandline the commandline of the subprocess to launch.
      */
     public void setCommandline(String[] commandline) {
         cmdl = commandline;
     }
 
     /**
      * Set whether to propagate the default environment or not.
-     *
+     * 
      * @param newenv whether to propagate the process environment.
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Returns the environment used to create a subprocess.
-     *
+     * 
      * @return the environment used to create a subprocess.
      */
     public String[] getEnvironment() {
         return (env == null || newEnvironment)
             ? env : patchEnvironment();
     }
 
     /**
      * Sets the environment variables for the subprocess to launch.
-     *
+     * 
      * @param env array of Strings, each element of which has
      * an environment variable settings in format <em>key=value</em>.
      */
     public void setEnvironment(String[] env) {
         this.env = env;
     }
 
     /**
      * Sets the working directory of the process to execute.
      *
      * <p>This is emulated using the antRun scripts unless the OS is
      * Windows NT in which case a cmd.exe is spawned,
      * or MRJ and setting user.dir works, or JDK 1.3 and there is
      * official support in java.lang.Runtime.
-     *
+     * 
      * @param wd the working directory of the process.
      */
     public void setWorkingDirectory(File wd) {
         workingDirectory =
             (wd == null || wd.getAbsolutePath().equals(antWorkingDirectory))
             ? null : wd;
     }
 
     /**
      * Return the working directory.
+     * 
      * @return the directory as a File.
      * @since Ant 1.7
      */
     public File getWorkingDirectory() {
         return workingDirectory == null ? new File(antWorkingDirectory)
                                         : workingDirectory;
     }
 
     /**
      * Set the name of the antRun script using the project's value.
-     *
+     * 
      * @param project the current project.
-     *
      * @throws BuildException not clear when it is going to throw an exception, but
      * it is the method's signature.
      */
     public void setAntRun(Project project) throws BuildException {
         this.project = project;
     }
 
     /**
      * Launch this execution through the VM, where possible, rather than through
      * the OS's shell. In some cases and operating systems using the shell will
      * allow the shell to perform additional processing such as associating an
      * executable with a script, etc.
-     *
+     * 
      * @param useVMLauncher true if exec should launch through the VM,
      *                   false if the shell should be used to launch the
      *                   command.
      */
     public void setVMLauncher(boolean useVMLauncher) {
         this.useVMLauncher = useVMLauncher;
     }
 
     /**
      * Creates a process that runs a command.
-     *
+     * 
      * @param project the Project, only used for logging purposes, may be null.
      * @param command the command to run.
      * @param env the environment for the command.
      * @param dir the working directory for the command.
      * @param useVM use the built-in exec command for JDK 1.3 if available.
      * @return the process started.
      * @throws IOException forwarded from the particular launcher used.
-     *
      * @since Ant 1.5
      */
     public static Process launch(Project project, String[] command,
                                  String[] env, File dir, boolean useVM)
         throws IOException {
         if (dir != null && !dir.exists()) {
             throw new BuildException(dir + " doesn't exist.");
         }
-        CommandLauncher launcher
-            = ((useVM && vmLauncher != null) ? vmLauncher : shellLauncher);
+
+        CommandLauncher vmLauncher = CommandLauncher.getVMLauncher(project);
+        CommandLauncher launcher = (useVM && vmLauncher != null)
+            ? vmLauncher : CommandLauncher.getShellLauncher(project);
         return launcher.exec(project, command, env, dir);
     }
 
     /**
      * Runs a process defined by the command line and returns its exit status.
-     *
+     * 
      * @return the exit status of the subprocess or <code>INVALID</code>.
      * @exception java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed.
      */
     public int execute() throws IOException {
         if (workingDirectory != null && !workingDirectory.exists()) {
             throw new BuildException(workingDirectory + " doesn't exist.");
         }
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
         try {
             streamHandler.setProcessInputStream(process.getOutputStream());
             streamHandler.setProcessOutputStream(process.getInputStream());
             streamHandler.setProcessErrorStream(process.getErrorStream());
         } catch (IOException e) {
             process.destroy();
             throw e;
         }
         streamHandler.start();
 
         try {
             // add the process to the list of those to destroy if the VM exits
             //
             processDestroyer.add(process);
 
             if (watchdog != null) {
                 watchdog.start(process);
             }
             waitFor(process);
 
             if (watchdog != null) {
                 watchdog.stop();
             }
             streamHandler.stop();
             closeStreams(process);
 
             if (watchdog != null) {
                 watchdog.checkException();
             }
             return getExitValue();
         } catch (ThreadDeath t) {
             // #31928: forcibly kill it before continuing.
             process.destroy();
             throw t;
         } finally {
             // remove the process to the list of those to destroy if
             // the VM exits
             //
             processDestroyer.remove(process);
         }
     }
 
     /**
      * Starts a process defined by the command line.
      * Ant will not wait for this process, nor log its output.
-     *
+     * 
      * @throws java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed.
      * @since Ant 1.6
      */
     public void spawn() throws IOException {
         if (workingDirectory != null && !workingDirectory.exists()) {
             throw new BuildException(workingDirectory + " doesn't exist.");
         }
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
         if (Os.isFamily("windows")) {
             try {
                 Thread.sleep(ONE_SECOND);
             } catch (InterruptedException e) {
                 project.log("interruption in the sleep after having spawned a"
                             + " process", Project.MSG_VERBOSE);
             }
         }
         OutputStream dummyOut = new OutputStream() {
+            @Override
             public void write(int b) throws IOException {
                 // Method intended to swallow whatever comes at it
             }
         };
 
         ExecuteStreamHandler handler = new PumpStreamHandler(dummyOut);
         handler.setProcessErrorStream(process.getErrorStream());
         handler.setProcessOutputStream(process.getInputStream());
         handler.start();
         process.getOutputStream().close();
 
         project.log("spawned process " + process.toString(),
                     Project.MSG_VERBOSE);
     }
 
     /**
      * Wait for a given process.
-     *
+     * 
      * @param process the process one wants to wait for.
      */
     protected void waitFor(Process process) {
         try {
             process.waitFor();
             setExitValue(process.exitValue());
         } catch (InterruptedException e) {
             process.destroy();
         }
     }
 
     /**
      * Set the exit value.
-     *
+     * 
      * @param value exit value of the process.
      */
     protected void setExitValue(int value) {
         exitValue = value;
     }
 
     /**
      * Query the exit value of the process.
+     * 
      * @return the exit value or Execute.INVALID if no exit value has
      * been received.
      */
     public int getExitValue() {
         return exitValue;
     }
 
     /**
      * Checks whether <code>exitValue</code> signals a failure on the current
      * system (OS specific).
      *
      * <p><b>Note</b> that this method relies on the conventions of
      * the OS, it will return false results if the application you are
-     * running doesn't follow these conventions.  One notable
+     * running doesn't follow these conventions. One notable
      * exception is the Java VM provided by HP for OpenVMS - it will
      * return 0 if successful (like on any other platform), but this
-     * signals a failure on OpenVMS.  So if you execute a new Java VM
+     * signals a failure on OpenVMS. So if you execute a new Java VM
      * on OpenVMS, you cannot trust this method.</p>
-     *
+     * 
      * @param exitValue the exit value (return code) to be checked.
      * @return <code>true</code> if <code>exitValue</code> signals a failure.
      */
     public static boolean isFailure(int exitValue) {
-        //on openvms even exit value signals failure;
+        // on openvms even exit value signals failure;
         // for other platforms nonzero exit value signals failure
         return Os.isFamily("openvms")
             ? (exitValue % 2 == 0) : (exitValue != 0);
     }
 
     /**
      * Did this execute return in a failure.
+     * 
      * @see #isFailure(int)
      * @return true if and only if the exit code is interpreted as a failure
      * @since Ant1.7
      */
     public boolean isFailure() {
         return isFailure(getExitValue());
     }
 
     /**
      * Test for an untimely death of the process.
+     * 
      * @return true if a watchdog had to kill the process.
      * @since Ant 1.5
      */
     public boolean killedProcess() {
         return watchdog != null && watchdog.killedProcess();
     }
 
     /**
      * Patch the current environment with the new values from the user.
+     * 
      * @return the patched environment.
      */
     private String[] patchEnvironment() {
         // On OpenVMS Runtime#exec() doesn't support the environment array,
         // so we only return the new values which then will be set in
         // the generated DCL script, inheriting the parent process environment
         if (Os.isFamily("openvms")) {
             return env;
         }
         Map/*<String, String>*/ osEnv =
             new LinkedHashMap(getEnvironmentVariables());
         for (int i = 0; i < env.length; i++) {
             String keyValue = env[i];
             String key = keyValue.substring(0, keyValue.indexOf('='));
             // Find the key in the current enviroment copy
             // and remove it.
 
             // Try without changing case first
             if (osEnv.remove(key) == null && environmentCaseInSensitive) {
                 // not found, maybe perform a case insensitive search
 
-                for (Iterator it = osEnv.keySet().iterator(); it.hasNext(); ) {
+                for (Iterator it = osEnv.keySet().iterator(); it.hasNext();) {
                     String osEnvItem = (String) it.next();
                     // Nb: using default locale as key is a env name
                     if (osEnvItem.toLowerCase().equals(key.toLowerCase())) {
                         // Use the original casiness of the key
                         key = osEnvItem;
                         break;
                     }
                 }
             }
 
             // Add the key to the enviromnent copy
             osEnv.put(key, keyValue.substring(key.length() + 1));
         }
 
         ArrayList l = new ArrayList();
-        for (Iterator it = osEnv.entrySet().iterator(); it.hasNext(); ) {
+        for (Iterator it = osEnv.entrySet().iterator(); it.hasNext();) {
             Map.Entry entry = (Map.Entry) it.next();
             l.add(entry.getKey() + "=" + entry.getValue());
         }
         return (String[]) (l.toArray(new String[osEnv.size()]));
     }
 
     /**
-     * A utility method that runs an external command.  Writes the output and
+     * A utility method that runs an external command. Writes the output and
      * error streams of the command to the project log.
-     *
-     * @param task      The task that the command is part of.  Used for logging
-     * @param cmdline   The command to execute.
-     *
+     * 
+     * @param task The task that the command is part of. Used for logging
+     * @param cmdline The command to execute.
      * @throws BuildException if the command does not exit successfully.
      */
     public static void runCommand(Task task, String[] cmdline)
         throws BuildException {
         try {
             task.log(Commandline.describeCommand(cmdline),
                      Project.MSG_VERBOSE);
             Execute exe = new Execute(
                 new LogStreamHandler(task, Project.MSG_INFO, Project.MSG_ERR));
             exe.setAntRun(task.getProject());
             exe.setCommandline(cmdline);
             int retval = exe.execute();
             if (isFailure(retval)) {
                 throw new BuildException(cmdline[0]
                     + " failed with return code " + retval, task.getLocation());
             }
         } catch (java.io.IOException exc) {
             throw new BuildException("Could not launch " + cmdline[0] + ": "
                 + exc, task.getLocation());
         }
     }
 
     /**
      * Close the streams belonging to the given Process.
-     * @param process   the <code>Process</code>.
+     * 
+     * @param process the <code>Process</code>.
      */
     public static void closeStreams(Process process) {
         FileUtils.close(process.getInputStream());
         FileUtils.close(process.getOutputStream());
         FileUtils.close(process.getErrorStream());
     }
 
     /**
      * This method is VMS specific and used by getEnvironmentVariables().
      *
      * Parses VMS logicals from <code>in</code> and returns them as a Map.
      * <code>in</code> is expected to be the
-     * output of "SHOW LOGICAL".  The method takes care of parsing the output
+     * output of "SHOW LOGICAL". The method takes care of parsing the output
      * correctly as well as making sure that a logical defined in multiple
-     * tables only gets added from the highest order table.  Logicals with
+     * tables only gets added from the highest order table. Logicals with
      * multiple equivalence names are mapped to a variable with multiple
      * values separated by a comma (,).
      */
     private static Map getVMSLogicals(BufferedReader in)
         throws IOException {
         HashMap logicals = new HashMap();
         String logName = null, logValue = null, newLogName;
         String line = null;
         // CheckStyle:MagicNumber OFF
         while ((line = in.readLine()) != null) {
             // parse the VMS logicals into required format ("VAR=VAL[,VAL2]")
             if (line.startsWith("\t=")) {
                 // further equivalence name of previous logical
                 if (logName != null) {
                     logValue += "," + line.substring(4, line.length() - 1);
                 }
             } else if (line.startsWith("  \"")) {
                 // new logical?
                 if (logName != null) {
                     logicals.put(logName, logValue);
                 }
                 int eqIndex = line.indexOf('=');
                 newLogName = line.substring(3, eqIndex - 2);
                 if (logicals.containsKey(newLogName)) {
                     // already got this logical from a higher order table
                     logName = null;
                 } else {
                     logName = newLogName;
                     logValue = line.substring(eqIndex + 3, line.length() - 1);
                 }
             }
         }
         // CheckStyle:MagicNumber ON
         // Since we "look ahead" before adding, there's one last env var.
         if (logName != null) {
             logicals.put(logName, logValue);
         }
         return logicals;
     }
-
-    /**
-     * A command launcher for a particular JVM/OS platform.  This class is
-     * a general purpose command launcher which can only launch commands in
-     * the current working directory.
-     */
-    private static class CommandLauncher {
-        /**
-         * Launches the given command in a new process.
-         *
-         * @param project       The project that the command is part of.
-         * @param cmd           The command to execute.
-         * @param env           The environment for the new process.  If null,
-         *                      the environment of the current process is used.
-         * @return the created Process.
-         * @throws IOException if attempting to run a command in a
-         * specific directory.
-         */
-        public Process exec(Project project, String[] cmd, String[] env)
-             throws IOException {
-            if (project != null) {
-                project.log("Execute:CommandLauncher: "
-                    + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
-            }
-            return Runtime.getRuntime().exec(cmd, env);
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         *
-         * @param project       The project that the command is part of.
-         * @param cmd           The command to execute.
-         * @param env           The environment for the new process.  If null,
-         *                      the environment of the current process is used.
-         * @param workingDir    The directory to start the command in.  If null,
-         *                      the current directory is used.
-         * @return the created Process.
-         * @throws IOException  if trying to change directory.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            if (workingDir == null) {
-                return exec(project, cmd, env);
-            }
-            throw new IOException("Cannot execute a process in different "
-                + "directory under this JVM");
-        }
-    }
-
-    /**
-     * A command launcher for JDK/JRE 1.3 (and higher).  Uses the built-in
-     * Runtime.exec() command.
-     */
-    private static class Java13CommandLauncher extends CommandLauncher {
-
-        public Java13CommandLauncher() throws NoSuchMethodException {
-            // Used to verify if Java13 is available, is prerequisite in ant 1.8
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir the working directory where the command
-         * should run.
-         * @return the created Process.
-         * @throws IOException probably forwarded from Runtime#exec.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            try {
-                if (project != null) {
-                    project.log("Execute:Java13CommandLauncher: "
-                        + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
-                }
-                return Runtime.getRuntime().exec(cmd, env, workingDir);
-            } catch (IOException ioex) {
-                throw ioex;
-            } catch (Exception exc) {
-                // IllegalAccess, IllegalArgument, ClassCast
-                throw new BuildException("Unable to execute command", exc);
-            }
-        }
-    }
-
-    /**
-     * A command launcher that proxies another command launcher.
-     *
-     * Sub-classes override exec(args, env, workdir).
-     */
-    private static class CommandLauncherProxy extends CommandLauncher {
-        private CommandLauncher myLauncher;
-
-        CommandLauncherProxy(CommandLauncher launcher) {
-            myLauncher = launcher;
-        }
-
-        /**
-         * Launches the given command in a new process.  Delegates this
-         * method to the proxied launcher.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env)
-            throws IOException {
-            return myLauncher.exec(project, cmd, env);
-        }
-    }
-
-    /**
-     * A command launcher for OS/2 that uses 'cmd.exe' when launching
-     * commands in directories other than the current working
-     * directory.
-     *
-     * <p>Unlike Windows NT and friends, OS/2's cd doesn't support the
-     * /d switch to change drives and directories in one go.</p>
-     */
-    private static class OS2CommandLauncher extends CommandLauncherProxy {
-        OS2CommandLauncher(CommandLauncher launcher) {
-            super(launcher);
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            File commandDir = workingDir;
-            if (workingDir == null) {
-                if (project != null) {
-                    commandDir = project.getBaseDir();
-                } else {
-                    return exec(project, cmd, env);
-                }
-            }
-            // Use cmd.exe to change to the specified drive and
-            // directory before running the command
-            final int preCmdLength = 7;
-            final String cmdDir = commandDir.getAbsolutePath();
-            String[] newcmd = new String[cmd.length + preCmdLength];
-            // CheckStyle:MagicNumber OFF - do not bother
-            newcmd[0] = "cmd";
-            newcmd[1] = "/c";
-            newcmd[2] = cmdDir.substring(0, 2);
-            newcmd[3] = "&&";
-            newcmd[4] = "cd";
-            newcmd[5] = cmdDir.substring(2);
-            newcmd[6] = "&&";
-            // CheckStyle:MagicNumber ON
-            System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
-
-            return exec(project, newcmd, env);
-        }
-    }
-
-    /**
-     * A command launcher for Windows XP/2000/NT that uses 'cmd.exe' when
-     * launching commands in directories other than the current working
-     * directory.
-     */
-    private static class WinNTCommandLauncher extends CommandLauncherProxy {
-        WinNTCommandLauncher(CommandLauncher launcher) {
-            super(launcher);
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            File commandDir = workingDir;
-            if (workingDir == null) {
-                if (project != null) {
-                    commandDir = project.getBaseDir();
-                } else {
-                    return exec(project, cmd, env);
-                }
-            }
-            // Use cmd.exe to change to the specified directory before running
-            // the command
-            final int preCmdLength = 6;
-            String[] newcmd = new String[cmd.length + preCmdLength];
-            // CheckStyle:MagicNumber OFF - do not bother
-            newcmd[0] = "cmd";
-            newcmd[1] = "/c";
-            newcmd[2] = "cd";
-            newcmd[3] = "/d";
-            newcmd[4] = commandDir.getAbsolutePath();
-            newcmd[5] = "&&";
-            // CheckStyle:MagicNumber ON
-            System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
-
-            return exec(project, newcmd, env);
-        }
-    }
-
-    /**
-     * A command launcher for Mac that uses a dodgy mechanism to change
-     * working directory before launching commands.
-     */
-    private static class MacCommandLauncher extends CommandLauncherProxy {
-        MacCommandLauncher(CommandLauncher launcher) {
-            super(launcher);
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            if (workingDir == null) {
-                return exec(project, cmd, env);
-            }
-            System.getProperties().put("user.dir", workingDir.getAbsolutePath());
-            try {
-                return exec(project, cmd, env);
-            } finally {
-                System.getProperties().put("user.dir", antWorkingDirectory);
-            }
-        }
-    }
-
-    /**
-     * A command launcher that uses an auxiliary script to launch commands
-     * in directories other than the current working directory.
-     */
-    private static class ScriptCommandLauncher extends CommandLauncherProxy {
-        ScriptCommandLauncher(String script, CommandLauncher launcher) {
-            super(launcher);
-            myScript = script;
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            if (project == null) {
-                if (workingDir == null) {
-                    return exec(project, cmd, env);
-                }
-                throw new IOException("Cannot locate antRun script: "
-                    + "No project provided");
-            }
-            // Locate the auxiliary script
-            String antHome = project.getProperty(MagicNames.ANT_HOME);
-            if (antHome == null) {
-                throw new IOException("Cannot locate antRun script: "
-                    + "Property '" + MagicNames.ANT_HOME + "' not found");
-            }
-            String antRun =
-                FILE_UTILS.resolveFile(project.getBaseDir(),
-                        antHome + File.separator + myScript).toString();
-
-            // Build the command
-            File commandDir = workingDir;
-            if (workingDir == null) {
-                commandDir = project.getBaseDir();
-            }
-            String[] newcmd = new String[cmd.length + 2];
-            newcmd[0] = antRun;
-            newcmd[1] = commandDir.getAbsolutePath();
-            System.arraycopy(cmd, 0, newcmd, 2, cmd.length);
-
-            return exec(project, newcmd, env);
-        }
-
-        private String myScript;
-    }
-
-    /**
-     * A command launcher that uses an auxiliary perl script to launch commands
-     * in directories other than the current working directory.
-     */
-    private static class PerlScriptCommandLauncher
-        extends CommandLauncherProxy {
-        private String myScript;
-
-        PerlScriptCommandLauncher(String script, CommandLauncher launcher) {
-            super(launcher);
-            myScript = script;
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            if (project == null) {
-                if (workingDir == null) {
-                    return exec(project, cmd, env);
-                }
-                throw new IOException("Cannot locate antRun script: "
-                    + "No project provided");
-            }
-            // Locate the auxiliary script
-            String antHome = project.getProperty(MagicNames.ANT_HOME);
-            if (antHome == null) {
-                throw new IOException("Cannot locate antRun script: "
-                    + "Property '" + MagicNames.ANT_HOME + "' not found");
-            }
-            String antRun =
-                FILE_UTILS.resolveFile(project.getBaseDir(),
-                        antHome + File.separator + myScript).toString();
-
-            // Build the command
-            File commandDir = workingDir;
-            if (workingDir == null) {
-                commandDir = project.getBaseDir();
-            }
-            // CheckStyle:MagicNumber OFF
-            String[] newcmd = new String[cmd.length + 3];
-            newcmd[0] = "perl";
-            newcmd[1] = antRun;
-            newcmd[2] = commandDir.getAbsolutePath();
-            System.arraycopy(cmd, 0, newcmd, 3, cmd.length);
-            // CheckStyle:MagicNumber ON
-
-            return exec(project, newcmd, env);
-        }
-    }
-
-    /**
-     * A command launcher for VMS that writes the command to a temporary DCL
-     * script before launching commands.  This is due to limitations of both
-     * the DCL interpreter and the Java VM implementation.
-     */
-    private static class VmsCommandLauncher extends Java13CommandLauncher {
-
-        public VmsCommandLauncher() throws NoSuchMethodException {
-            super();
-        }
-
-        /**
-         * Launches the given command in a new process.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env)
-            throws IOException {
-            File cmdFile = createCommandFile(cmd, env);
-            Process p
-                = super.exec(project, new String[] {cmdFile.getPath()}, env);
-            deleteAfter(cmdFile, p);
-            return p;
-        }
-
-        /**
-         * Launches the given command in a new process, in the given working
-         * directory.  Note that under Java 1.4.0 and 1.4.1 on VMS this
-         * method only works if <code>workingDir</code> is null or the logical
-         * JAVA$FORK_SUPPORT_CHDIR needs to be set to TRUE.
-         * @param project the Ant project.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @param workingDir working directory where the command should run.
-         * @return the created Process.
-         * @throws IOException forwarded from the exec method of the
-         * command launcher.
-         */
-        public Process exec(Project project, String[] cmd, String[] env,
-                            File workingDir) throws IOException {
-            File cmdFile = createCommandFile(cmd, env);
-            Process p = super.exec(project, new String[] {cmdFile.getPath()},
-                                   env, workingDir);
-            deleteAfter(cmdFile, p);
-            return p;
-        }
-
-        /*
-         * Writes the command into a temporary DCL script and returns the
-         * corresponding File object.  The script will be deleted on exit.
-         * @param cmd the command line to execute as an array of strings.
-         * @param env the environment to set as an array of strings.
-         * @return the command File.
-         * @throws IOException if errors are encountered creating the file.
-         */
-        private File createCommandFile(String[] cmd, String[] env)
-            throws IOException {
-            File script = FILE_UTILS.createTempFile("ANT", ".COM", null, true, true);
-            BufferedWriter out = null;
-            try {
-                out = new BufferedWriter(new FileWriter(script));
-
-                // add the environment as logicals to the DCL script
-                if (env != null) {
-                    int eqIndex;
-                    for (int i = 0; i < env.length; i++) {
-                        eqIndex = env[i].indexOf('=');
-                        if (eqIndex != -1) {
-                            out.write("$ DEFINE/NOLOG ");
-                            out.write(env[i].substring(0, eqIndex));
-                            out.write(" \"");
-                            out.write(env[i].substring(eqIndex + 1));
-                            out.write('\"');
-                            out.newLine();
-                        }
-                    }
-                }
-                out.write("$ " + cmd[0]);
-                for (int i = 1; i < cmd.length; i++) {
-                    out.write(" -");
-                    out.newLine();
-                    out.write(cmd[i]);
-                }
-            } finally {
-                FileUtils.close(out);
-            }
-            return script;
-        }
-
-        private void deleteAfter(final File f, final Process p) {
-            new Thread() {
-                public void run() {
-                    try {
-                        p.waitFor();
-                    } catch (InterruptedException e) {
-                        //ignore
-                    }
-                    FileUtils.delete(f);
-                }
-            }
-            .start();
-        }
-    }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/defaults.properties b/src/main/org/apache/tools/ant/taskdefs/defaults.properties
index e142259d5..7bb5cc12f 100644
--- a/src/main/org/apache/tools/ant/taskdefs/defaults.properties
+++ b/src/main/org/apache/tools/ant/taskdefs/defaults.properties
@@ -1,223 +1,224 @@
 # Licensed to the Apache Software Foundation (ASF) under one or more
 # contributor license agreements.  See the NOTICE file distributed with
 # this work for additional information regarding copyright ownership.
 # The ASF licenses this file to You under the Apache License, Version 2.0
 # (the "License"); you may not use this file except in compliance with
 # the License.  You may obtain a copy of the License at
 #
 #     http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 #
 # standard ant tasks
 ant=org.apache.tools.ant.taskdefs.Ant
 antcall=org.apache.tools.ant.taskdefs.CallTarget
 antstructure=org.apache.tools.ant.taskdefs.AntStructure
 antversion=org.apache.tools.ant.taskdefs.condition.AntVersion
 apply=org.apache.tools.ant.taskdefs.Transform
 apt=org.apache.tools.ant.taskdefs.Apt
 augment=org.apache.tools.ant.taskdefs.AugmentReference
 available=org.apache.tools.ant.taskdefs.Available
 basename=org.apache.tools.ant.taskdefs.Basename
 bindtargets=org.apache.tools.ant.taskdefs.BindTargets
 buildnumber=org.apache.tools.ant.taskdefs.BuildNumber
 bunzip2=org.apache.tools.ant.taskdefs.BUnzip2
 bzip2=org.apache.tools.ant.taskdefs.BZip2
 checksum=org.apache.tools.ant.taskdefs.Checksum
 chmod=org.apache.tools.ant.taskdefs.Chmod
 classloader=org.apache.tools.ant.taskdefs.Classloader
+commandlaucher=org.apache.tools.ant.taskdefs.CommandLauncherTask
 componentdef=org.apache.tools.ant.taskdefs.Componentdef
 concat=org.apache.tools.ant.taskdefs.Concat
 condition=org.apache.tools.ant.taskdefs.ConditionTask
 copy=org.apache.tools.ant.taskdefs.Copy
 cvs=org.apache.tools.ant.taskdefs.Cvs
 cvschangelog=org.apache.tools.ant.taskdefs.cvslib.ChangeLogTask
 cvspass=org.apache.tools.ant.taskdefs.CVSPass
 cvstagdiff=org.apache.tools.ant.taskdefs.cvslib.CvsTagDiff
 cvsversion=org.apache.tools.ant.taskdefs.cvslib.CvsVersion
 defaultexcludes=org.apache.tools.ant.taskdefs.DefaultExcludes
 delete=org.apache.tools.ant.taskdefs.Delete
 dependset=org.apache.tools.ant.taskdefs.DependSet
 diagnostics=org.apache.tools.ant.taskdefs.DiagnosticsTask
 dirname=org.apache.tools.ant.taskdefs.Dirname
 ear=org.apache.tools.ant.taskdefs.Ear
 echo=org.apache.tools.ant.taskdefs.Echo
 echoproperties=org.apache.tools.ant.taskdefs.optional.EchoProperties
 echoxml=org.apache.tools.ant.taskdefs.EchoXML
 exec=org.apache.tools.ant.taskdefs.ExecTask
 fail=org.apache.tools.ant.taskdefs.Exit
 filter=org.apache.tools.ant.taskdefs.Filter
 fixcrlf=org.apache.tools.ant.taskdefs.FixCRLF
 #funtest=org.apache.tools.ant.taskdefs.optional.testing.Funtest
 genkey=org.apache.tools.ant.taskdefs.GenerateKey
 get=org.apache.tools.ant.taskdefs.Get
 gunzip=org.apache.tools.ant.taskdefs.GUnzip
 gzip=org.apache.tools.ant.taskdefs.GZip
 hostinfo=org.apache.tools.ant.taskdefs.HostInfo
 import=org.apache.tools.ant.taskdefs.ImportTask
 include=org.apache.tools.ant.taskdefs.ImportTask
 input=org.apache.tools.ant.taskdefs.Input
 jar=org.apache.tools.ant.taskdefs.Jar
 java=org.apache.tools.ant.taskdefs.Java
 javac=org.apache.tools.ant.taskdefs.Javac
 javadoc=org.apache.tools.ant.taskdefs.Javadoc
 length=org.apache.tools.ant.taskdefs.Length
 loadfile=org.apache.tools.ant.taskdefs.LoadFile
 loadproperties=org.apache.tools.ant.taskdefs.LoadProperties
 loadresource=org.apache.tools.ant.taskdefs.LoadResource
 local=org.apache.tools.ant.taskdefs.Local
 macrodef=org.apache.tools.ant.taskdefs.MacroDef
 mail=org.apache.tools.ant.taskdefs.email.EmailTask
 makeurl=org.apache.tools.ant.taskdefs.MakeUrl
 manifest=org.apache.tools.ant.taskdefs.ManifestTask
 manifestclasspath=org.apache.tools.ant.taskdefs.ManifestClassPath
 mkdir=org.apache.tools.ant.taskdefs.Mkdir
 move=org.apache.tools.ant.taskdefs.Move
 nice=org.apache.tools.ant.taskdefs.Nice
 parallel=org.apache.tools.ant.taskdefs.Parallel
 patch=org.apache.tools.ant.taskdefs.Patch
 pathconvert=org.apache.tools.ant.taskdefs.PathConvert
 presetdef=org.apache.tools.ant.taskdefs.PreSetDef
 projecthelper=org.apache.tools.ant.taskdefs.ProjectHelperTask
 property=org.apache.tools.ant.taskdefs.Property
 propertyhelper=org.apache.tools.ant.taskdefs.PropertyHelperTask
 record=org.apache.tools.ant.taskdefs.Recorder
 replace=org.apache.tools.ant.taskdefs.Replace
 resourcecount=org.apache.tools.ant.taskdefs.ResourceCount
 retry=org.apache.tools.ant.taskdefs.Retry
 rmic=org.apache.tools.ant.taskdefs.Rmic
 sequential=org.apache.tools.ant.taskdefs.Sequential
 signjar=org.apache.tools.ant.taskdefs.SignJar
 sleep=org.apache.tools.ant.taskdefs.Sleep
 sql=org.apache.tools.ant.taskdefs.SQLExec
 subant=org.apache.tools.ant.taskdefs.SubAnt
 sync=org.apache.tools.ant.taskdefs.Sync
 tar=org.apache.tools.ant.taskdefs.Tar
 taskdef=org.apache.tools.ant.taskdefs.Taskdef
 tempfile=org.apache.tools.ant.taskdefs.TempFile
 touch=org.apache.tools.ant.taskdefs.Touch
 tstamp=org.apache.tools.ant.taskdefs.Tstamp
 truncate=org.apache.tools.ant.taskdefs.Truncate
 typedef=org.apache.tools.ant.taskdefs.Typedef
 unjar=org.apache.tools.ant.taskdefs.Expand
 untar=org.apache.tools.ant.taskdefs.Untar
 unwar=org.apache.tools.ant.taskdefs.Expand
 unzip=org.apache.tools.ant.taskdefs.Expand
 uptodate=org.apache.tools.ant.taskdefs.UpToDate
 waitfor=org.apache.tools.ant.taskdefs.WaitFor
 war=org.apache.tools.ant.taskdefs.War
 whichresource=org.apache.tools.ant.taskdefs.WhichResource
 xmlproperty=org.apache.tools.ant.taskdefs.XmlProperty
 xslt=org.apache.tools.ant.taskdefs.XSLTProcess
 zip=org.apache.tools.ant.taskdefs.Zip
 
 # optional tasks
 antlr=org.apache.tools.ant.taskdefs.optional.ANTLR
 attrib=org.apache.tools.ant.taskdefs.optional.windows.Attrib
 blgenclient=org.apache.tools.ant.taskdefs.optional.ejb.BorlandGenerateClient
 cab=org.apache.tools.ant.taskdefs.optional.Cab
 cccheckin=org.apache.tools.ant.taskdefs.optional.clearcase.CCCheckin
 cccheckout=org.apache.tools.ant.taskdefs.optional.clearcase.CCCheckout
 cclock=org.apache.tools.ant.taskdefs.optional.clearcase.CCLock
 ccmcheckin=org.apache.tools.ant.taskdefs.optional.ccm.CCMCheckin
 ccmcheckintask=org.apache.tools.ant.taskdefs.optional.ccm.CCMCheckinDefault
 ccmcheckout=org.apache.tools.ant.taskdefs.optional.ccm.CCMCheckout
 ccmcreatetask=org.apache.tools.ant.taskdefs.optional.ccm.CCMCreateTask
 ccmkattr=org.apache.tools.ant.taskdefs.optional.clearcase.CCMkattr
 ccmkbl=org.apache.tools.ant.taskdefs.optional.clearcase.CCMkbl
 ccmkdir=org.apache.tools.ant.taskdefs.optional.clearcase.CCMkdir
 ccmkelem=org.apache.tools.ant.taskdefs.optional.clearcase.CCMkelem
 ccmklabel=org.apache.tools.ant.taskdefs.optional.clearcase.CCMklabel
 ccmklbtype=org.apache.tools.ant.taskdefs.optional.clearcase.CCMklbtype
 ccmreconfigure=org.apache.tools.ant.taskdefs.optional.ccm.CCMReconfigure
 ccrmtype=org.apache.tools.ant.taskdefs.optional.clearcase.CCRmtype
 ccuncheckout=org.apache.tools.ant.taskdefs.optional.clearcase.CCUnCheckout
 ccunlock=org.apache.tools.ant.taskdefs.optional.clearcase.CCUnlock
 ccupdate=org.apache.tools.ant.taskdefs.optional.clearcase.CCUpdate
 chgrp=org.apache.tools.ant.taskdefs.optional.unix.Chgrp
 chown=org.apache.tools.ant.taskdefs.optional.unix.Chown
 depend=org.apache.tools.ant.taskdefs.optional.depend.Depend
 ejbjar=org.apache.tools.ant.taskdefs.optional.ejb.EjbJar
 ftp=org.apache.tools.ant.taskdefs.optional.net.FTP
 image=org.apache.tools.ant.taskdefs.optional.image.Image
 iplanet-ejbc=org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbcTask
 jarlib-available=org.apache.tools.ant.taskdefs.optional.extension.JarLibAvailableTask
 jarlib-display=org.apache.tools.ant.taskdefs.optional.extension.JarLibDisplayTask
 jarlib-manifest=org.apache.tools.ant.taskdefs.optional.extension.JarLibManifestTask
 jarlib-resolve=org.apache.tools.ant.taskdefs.optional.extension.JarLibResolveTask
 javacc=org.apache.tools.ant.taskdefs.optional.javacc.JavaCC
 javah=org.apache.tools.ant.taskdefs.optional.Javah
 jdepend=org.apache.tools.ant.taskdefs.optional.jdepend.JDependTask
 jjdoc=org.apache.tools.ant.taskdefs.optional.javacc.JJDoc
 jjtree=org.apache.tools.ant.taskdefs.optional.javacc.JJTree
 junit=org.apache.tools.ant.taskdefs.optional.junit.JUnitTask
 junitreport=org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator
 native2ascii=org.apache.tools.ant.taskdefs.optional.Native2Ascii
 netrexxc=org.apache.tools.ant.taskdefs.optional.NetRexxC
 p4add=org.apache.tools.ant.taskdefs.optional.perforce.P4Add
 p4change=org.apache.tools.ant.taskdefs.optional.perforce.P4Change
 p4counter=org.apache.tools.ant.taskdefs.optional.perforce.P4Counter
 p4delete=org.apache.tools.ant.taskdefs.optional.perforce.P4Delete
 p4edit=org.apache.tools.ant.taskdefs.optional.perforce.P4Edit
 p4fstat=org.apache.tools.ant.taskdefs.optional.perforce.P4Fstat
 p4have=org.apache.tools.ant.taskdefs.optional.perforce.P4Have
 p4integrate=org.apache.tools.ant.taskdefs.optional.perforce.P4Integrate
 p4label=org.apache.tools.ant.taskdefs.optional.perforce.P4Label
 p4labelsync=org.apache.tools.ant.taskdefs.optional.perforce.P4Labelsync
 p4reopen=org.apache.tools.ant.taskdefs.optional.perforce.P4Reopen
 p4resolve=org.apache.tools.ant.taskdefs.optional.perforce.P4Resolve
 p4revert=org.apache.tools.ant.taskdefs.optional.perforce.P4Revert
 p4submit=org.apache.tools.ant.taskdefs.optional.perforce.P4Submit
 p4sync=org.apache.tools.ant.taskdefs.optional.perforce.P4Sync
 propertyfile=org.apache.tools.ant.taskdefs.optional.PropertyFile
 pvcs=org.apache.tools.ant.taskdefs.optional.pvcs.Pvcs
 replaceregexp=org.apache.tools.ant.taskdefs.optional.ReplaceRegExp
 rexec=org.apache.tools.ant.taskdefs.optional.net.RExecTask
 rpm=org.apache.tools.ant.taskdefs.optional.Rpm
 schemavalidate=org.apache.tools.ant.taskdefs.optional.SchemaValidate
 scp=org.apache.tools.ant.taskdefs.optional.ssh.Scp
 script=org.apache.tools.ant.taskdefs.optional.Script
 scriptdef=org.apache.tools.ant.taskdefs.optional.script.ScriptDef
 serverdeploy=org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy
 setproxy=org.apache.tools.ant.taskdefs.optional.net.SetProxy
 soscheckin=org.apache.tools.ant.taskdefs.optional.sos.SOSCheckin
 soscheckout=org.apache.tools.ant.taskdefs.optional.sos.SOSCheckout
 sosget=org.apache.tools.ant.taskdefs.optional.sos.SOSGet
 soslabel=org.apache.tools.ant.taskdefs.optional.sos.SOSLabel
 sound=org.apache.tools.ant.taskdefs.optional.sound.SoundTask
 splash=org.apache.tools.ant.taskdefs.optional.splash.SplashTask
 sshexec=org.apache.tools.ant.taskdefs.optional.ssh.SSHExec
 sshsession=org.apache.tools.ant.taskdefs.optional.ssh.SSHSession
 symlink=org.apache.tools.ant.taskdefs.optional.unix.Symlink
 telnet=org.apache.tools.ant.taskdefs.optional.net.TelnetTask
 translate=org.apache.tools.ant.taskdefs.optional.i18n.Translate
 verifyjar=org.apache.tools.ant.taskdefs.VerifyJar
 vssadd=org.apache.tools.ant.taskdefs.optional.vss.MSVSSADD
 vsscheckin=org.apache.tools.ant.taskdefs.optional.vss.MSVSSCHECKIN
 vsscheckout=org.apache.tools.ant.taskdefs.optional.vss.MSVSSCHECKOUT
 vsscp=org.apache.tools.ant.taskdefs.optional.vss.MSVSSCP
 vsscreate=org.apache.tools.ant.taskdefs.optional.vss.MSVSSCREATE
 vssget=org.apache.tools.ant.taskdefs.optional.vss.MSVSSGET
 vsshistory=org.apache.tools.ant.taskdefs.optional.vss.MSVSSHISTORY
 vsslabel=org.apache.tools.ant.taskdefs.optional.vss.MSVSSLABEL
 wljspc=org.apache.tools.ant.taskdefs.optional.jsp.WLJspc
 xmlvalidate=org.apache.tools.ant.taskdefs.optional.XMLValidateTask
 
 
 # deprecated ant tasks (kept for back compatibility)
 copydir=org.apache.tools.ant.taskdefs.Copydir
 copyfile=org.apache.tools.ant.taskdefs.Copyfile
 copypath=org.apache.tools.ant.taskdefs.CopyPath
 deltree=org.apache.tools.ant.taskdefs.Deltree
 execon=org.apache.tools.ant.taskdefs.ExecuteOn
 javadoc2=org.apache.tools.ant.taskdefs.Javadoc
 jlink=org.apache.tools.ant.taskdefs.optional.jlink.JlinkTask
 jspc=org.apache.tools.ant.taskdefs.optional.jsp.JspC
 mimemail=org.apache.tools.ant.taskdefs.optional.net.MimeMail
 rename=org.apache.tools.ant.taskdefs.Rename
 renameext=org.apache.tools.ant.taskdefs.optional.RenameExtensions
 style=org.apache.tools.ant.taskdefs.XSLTProcess
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncher.java
new file mode 100644
index 000000000..33de2c88b
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncher.java
@@ -0,0 +1,184 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.taskdefs.condition.Os;
+import org.apache.tools.ant.types.Commandline;
+import org.apache.tools.ant.util.FileUtils;
+
+/**
+ * A command launcher for a particular JVM/OS platform. This class is
+ * a general purpose command launcher which can only launch commands
+ * in the current working directory.
+ */
+public class CommandLauncher {
+    private static final String ANT_SHELL_LAUNCHER_REF_ID = "ant.shellLauncher";
+    private static final String ANT_VM_LAUNCHER_REF_ID = "ant.vmLauncher";
+
+    protected static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
+
+    private static CommandLauncher vmLauncher = null;
+    private static CommandLauncher shellLauncher = null;
+
+    static {
+        // Try using a JDK 1.3 launcher
+        try {
+            if(!Os.isFamily("os/2")) {
+                vmLauncher = new Java13CommandLauncher();
+            }
+        } catch(NoSuchMethodException exc) {
+            // Ignore and keep trying
+        }
+
+        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
+            // Mac
+            shellLauncher = new MacCommandLauncher(new CommandLauncher());
+        } else if (Os.isFamily("os/2")) {
+            // OS/2
+            shellLauncher = new OS2CommandLauncher(new CommandLauncher());
+        } else if (Os.isFamily("windows")) {
+            CommandLauncher baseLauncher = new CommandLauncher();
+
+            if (!Os.isFamily("win9x")) {
+                // Windows XP/2000/NT
+                shellLauncher = new WinNTCommandLauncher(baseLauncher);
+            } else {
+                // Windows 98/95 - need to use an auxiliary script
+                shellLauncher = new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
+            }
+        } else if (Os.isFamily("netware")) {
+
+            CommandLauncher baseLauncher = new CommandLauncher();
+
+            shellLauncher = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
+        } else if (Os.isFamily("openvms")) {
+            // OpenVMS
+            try {
+                shellLauncher = new VmsCommandLauncher();
+            } catch(NoSuchMethodException exc) {
+                // Ignore and keep trying
+            }
+        } else {
+            // Generic
+            shellLauncher = new ScriptCommandLauncher("bin/antRun", new CommandLauncher());
+        }
+    }
+
+    /**
+     * Launches the given command in a new process.
+     * 
+     * @param project
+     *        The project that the command is part of.
+     * @param cmd
+     *        The command to execute.
+     * @param env
+     *        The environment for the new process. If null, the
+     *        environment of the current process is used.
+     * @return the created Process.
+     * @throws IOException
+     *         if attempting to run a command in a specific directory.
+     */
+    public Process exec(Project project, String[] cmd, String[] env) throws IOException {
+        if(project != null) {
+            project.log("Execute:CommandLauncher: " + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
+        }
+        return Runtime.getRuntime().exec(cmd, env);
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        The project that the command is part of.
+     * @param cmd
+     *        The command to execute.
+     * @param env
+     *        The environment for the new process. If null, the
+     *        environment of the current process is used.
+     * @param workingDir
+     *        The directory to start the command in. If null, the
+     *        current directory is used.
+     * @return the created Process.
+     * @throws IOException
+     *         if trying to change directory.
+     */
+    public Process exec(Project project, String[] cmd, String[] env, File workingDir) throws IOException {
+        if(workingDir == null) {
+            return exec(project, cmd, env);
+        }
+        throw new IOException("Cannot execute a process in different "
+                              + "directory under this JVM");
+    }
+
+    public static CommandLauncher getShellLauncher(Project project) {
+        CommandLauncher launcher = null;
+        if(project != null) {
+            launcher = (CommandLauncher) project
+                .getReference(ANT_SHELL_LAUNCHER_REF_ID);
+        }
+        if (launcher == null) {
+            launcher = getSystemLauncher(ANT_SHELL_LAUNCHER_REF_ID);
+        }
+        if (launcher == null) {
+            launcher = shellLauncher;
+        }
+
+        return launcher;
+    }
+
+    public static CommandLauncher getVMLauncher(Project project) {
+        CommandLauncher launcher = null;
+        if (project != null) {
+            launcher = (CommandLauncher)project.getReference(ANT_VM_LAUNCHER_REF_ID);
+        }
+
+        if (launcher == null) {
+            launcher = getSystemLauncher(ANT_VM_LAUNCHER_REF_ID);
+        }
+        if (launcher == null) {
+            launcher = vmLauncher;
+        }
+        return launcher;
+    }
+
+    private static CommandLauncher getSystemLauncher(String launcherRefId) {
+        CommandLauncher launcher = null;
+        String launcherClass = System.getProperty(launcherRefId);
+        if (launcherClass != null) {
+            try {
+                launcher = (CommandLauncher) Class.forName(launcherClass)
+                    .newInstance();
+            }
+            catch(InstantiationException e) {
+                System.err.println("Could not instantiate launcher class "
+                                   + launcherClass + ": " + e.getMessage());
+            }
+            catch(IllegalAccessException e) {
+                System.err.println("Could not instantiate launcher class "
+                                   + launcherClass + ": " + e.getMessage());
+            }
+            catch(ClassNotFoundException e) {
+                System.err.println("Could not instantiate launcher class "
+                                   + launcherClass + ": " + e.getMessage());
+            }
+        }
+
+        return launcher;
+    }
+
+    public static void setVMLauncher(Project project, CommandLauncher launcher) {
+        if (project != null) {
+            project.addReference(ANT_VM_LAUNCHER_REF_ID, launcher);
+        }
+    }
+
+    public static void setShellLauncher(Project project, CommandLauncher launcher) {
+        if (project != null) {
+            project.addReference(ANT_SHELL_LAUNCHER_REF_ID, launcher);
+        }
+    }
+
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncherProxy.java b/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncherProxy.java
new file mode 100644
index 000000000..312d386e1
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/CommandLauncherProxy.java
@@ -0,0 +1,37 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher that proxies another command
+ * launcher. Sub-classes override exec(args, env, workdir).
+ */
+public class CommandLauncherProxy extends CommandLauncher {
+    private final CommandLauncher myLauncher;
+
+    protected CommandLauncherProxy(CommandLauncher launcher) {
+        myLauncher = launcher;
+    }
+
+    /**
+     * Launches the given command in a new process. Delegates this
+     * method to the proxied launcher.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env)
+        throws IOException {
+        return myLauncher.exec(project, cmd, env);
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/Java13CommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/Java13CommandLauncher.java
new file mode 100644
index 000000000..6cd33af25
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/Java13CommandLauncher.java
@@ -0,0 +1,53 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.BuildException;
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.types.Commandline;
+
+/**
+ * A command launcher for JDK/JRE 1.3 (and higher). Uses the built-in
+ * Runtime.exec() command.
+ */
+public class Java13CommandLauncher extends CommandLauncher {
+
+    public Java13CommandLauncher() throws NoSuchMethodException {
+        // Used to verify if Java13 is available, is prerequisite in ant 1.8
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        the working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         probably forwarded from Runtime#exec.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        try {
+            if (project != null) {
+                project.log("Execute:Java13CommandLauncher: "
+                            + Commandline.describeCommand(cmd),
+                            Project.MSG_DEBUG);
+            }
+            return Runtime.getRuntime().exec(cmd, env, workingDir);
+        } catch(IOException ioex) {
+            throw ioex;
+        } catch(Exception exc) {
+            // IllegalAccess, IllegalArgument, ClassCast
+            throw new BuildException("Unable to execute command", exc);
+        }
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/MacCommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/MacCommandLauncher.java
new file mode 100644
index 000000000..42304f3f4
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/MacCommandLauncher.java
@@ -0,0 +1,47 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher for Mac that uses a dodgy mechanism to change
+ * working directory before launching commands.
+ */
+public class MacCommandLauncher extends CommandLauncherProxy {
+    public MacCommandLauncher(CommandLauncher launcher) {
+        super(launcher);
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        if (workingDir == null) {
+            return exec(project, cmd, env);
+        }
+        System.getProperties().put("user.dir", workingDir.getAbsolutePath());
+        try {
+            return exec(project, cmd, env);
+        }
+        finally {
+            System.getProperties().put("user.dir", System.getProperty("user.dir"));
+        }
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/OS2CommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/OS2CommandLauncher.java
new file mode 100644
index 000000000..5d9b0bbf2
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/OS2CommandLauncher.java
@@ -0,0 +1,65 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher for OS/2 that uses 'cmd.exe' when launching
+ * commands in directories other than the current working directory.
+ *
+ * <p>Unlike Windows NT and friends, OS/2's cd doesn't support the /d
+ * switch to change drives and directories in one go.</p>
+ */
+public class OS2CommandLauncher extends CommandLauncherProxy {
+    public OS2CommandLauncher(CommandLauncher launcher) {
+        super(launcher);
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        File commandDir = workingDir;
+        if (workingDir == null) {
+            if (project != null) {
+                commandDir = project.getBaseDir();
+            } else {
+                return exec(project, cmd, env);
+            }
+        }
+        // Use cmd.exe to change to the specified drive and
+        // directory before running the command
+        final int preCmdLength = 7;
+        final String cmdDir = commandDir.getAbsolutePath();
+        String[] newcmd = new String[cmd.length + preCmdLength];
+        // CheckStyle:MagicNumber OFF - do not bother
+        newcmd[0] = "cmd";
+        newcmd[1] = "/c";
+        newcmd[2] = cmdDir.substring(0, 2);
+        newcmd[3] = "&&";
+        newcmd[4] = "cd";
+        newcmd[5] = cmdDir.substring(2);
+        newcmd[6] = "&&";
+        // CheckStyle:MagicNumber ON
+        System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
+
+        return exec(project, newcmd, env);
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/PerlScriptCommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/PerlScriptCommandLauncher.java
new file mode 100644
index 000000000..201369784
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/PerlScriptCommandLauncher.java
@@ -0,0 +1,73 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.MagicNames;
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher that uses an auxiliary perl script to launch
+ * commands in directories other than the current working directory.
+ */
+public class PerlScriptCommandLauncher extends CommandLauncherProxy {
+    private final String myScript;
+
+    public PerlScriptCommandLauncher(String script, CommandLauncher launcher) {
+        super(launcher);
+        myScript = script;
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        if (project == null) {
+            if(workingDir == null) {
+                return exec(project, cmd, env);
+            }
+            throw new IOException("Cannot locate antRun script: "
+                                  + "No project provided");
+        }
+        // Locate the auxiliary script
+        String antHome = project.getProperty(MagicNames.ANT_HOME);
+        if (antHome == null) {
+            throw new IOException("Cannot locate antRun script: "
+                                  + "Property '" + MagicNames.ANT_HOME
+                                  + "' not found");
+        }
+        String antRun = FILE_UTILS.resolveFile(project.getBaseDir(),
+                                               antHome + File.separator
+                                               + myScript).toString();
+
+        // Build the command
+        File commandDir = workingDir;
+        if (workingDir == null) {
+            commandDir = project.getBaseDir();
+        }
+        // CheckStyle:MagicNumber OFF
+        String[] newcmd = new String[cmd.length + 3];
+        newcmd[0] = "perl";
+        newcmd[1] = antRun;
+        newcmd[2] = commandDir.getAbsolutePath();
+        System.arraycopy(cmd, 0, newcmd, 3, cmd.length);
+        // CheckStyle:MagicNumber ON
+
+        return exec(project, newcmd, env);
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/ScriptCommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/ScriptCommandLauncher.java
new file mode 100644
index 000000000..cf5a89c17
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/ScriptCommandLauncher.java
@@ -0,0 +1,71 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.MagicNames;
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher that uses an auxiliary script to launch commands
+ * in directories other than the current working directory.
+ */
+public class ScriptCommandLauncher extends CommandLauncherProxy {
+    private final String myScript;
+
+    public ScriptCommandLauncher(String script, CommandLauncher launcher) {
+        super(launcher);
+        myScript = script;
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        if (project == null) {
+            if (workingDir == null) {
+                return exec(project, cmd, env);
+            }
+            throw new IOException("Cannot locate antRun script: "
+                                  + "No project provided");
+        }
+        // Locate the auxiliary script
+        String antHome = project.getProperty(MagicNames.ANT_HOME);
+        if(antHome == null) {
+            throw new IOException("Cannot locate antRun script: "
+                                  + "Property '" + MagicNames.ANT_HOME
+                                  + "' not found");
+        }
+        String antRun = FILE_UTILS.resolveFile(project.getBaseDir(),
+                                               antHome + File.separator
+                                               + myScript).toString();
+
+        // Build the command
+        File commandDir = workingDir;
+        if(workingDir == null) {
+            commandDir = project.getBaseDir();
+        }
+        String[] newcmd = new String[cmd.length + 2];
+        newcmd[0] = antRun;
+        newcmd[1] = commandDir.getAbsolutePath();
+        System.arraycopy(cmd, 0, newcmd, 2, cmd.length);
+
+        return exec(project, newcmd, env);
+    }
+
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/VmsCommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/VmsCommandLauncher.java
new file mode 100644
index 000000000..216e72339
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/VmsCommandLauncher.java
@@ -0,0 +1,128 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.BufferedWriter;
+import java.io.File;
+import java.io.FileWriter;
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.util.FileUtils;
+
+/**
+ * A command launcher for VMS that writes the command to a temporary
+ * DCL script before launching commands. This is due to limitations of
+ * both the DCL interpreter and the Java VM implementation.
+ */
+public class VmsCommandLauncher extends Java13CommandLauncher {
+
+    public VmsCommandLauncher() throws NoSuchMethodException {
+        super();
+    }
+
+    /**
+     * Launches the given command in a new process.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env)
+        throws IOException {
+        File cmdFile = createCommandFile(cmd, env);
+        Process p = super.exec(project, new String[] {cmdFile.getPath()}, env);
+        deleteAfter(cmdFile, p);
+        return p;
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory. Note that under Java 1.4.0 and 1.4.1 on VMS
+     * this method only works if <code>workingDir</code> is null or
+     * the logical JAVA$FORK_SUPPORT_CHDIR needs to be set to TRUE.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        File cmdFile = createCommandFile(cmd, env);
+        Process p = super.exec(project, new String[] {
+                cmdFile.getPath()
+            }, env, workingDir);
+        deleteAfter(cmdFile, p);
+        return p;
+    }
+
+    /*
+     * Writes the command into a temporary DCL script and returns the
+     * corresponding File object.  The script will be deleted on exit.
+     * @param cmd the command line to execute as an array of strings.
+     * @param env the environment to set as an array of strings.
+     * @return the command File.
+     * @throws IOException if errors are encountered creating the file.
+     */
+    private File createCommandFile(String[] cmd, String[] env)
+        throws IOException {
+        File script = FILE_UTILS.createTempFile("ANT", ".COM", null, true, true);
+        BufferedWriter out = null;
+        try {
+            out = new BufferedWriter(new FileWriter(script));
+
+            // add the environment as logicals to the DCL script
+            if (env != null) {
+                int eqIndex;
+                for (int i = 0; i < env.length; i++) {
+                    eqIndex = env[i].indexOf('=');
+                    if (eqIndex != -1) {
+                        out.write("$ DEFINE/NOLOG ");
+                        out.write(env[i].substring(0, eqIndex));
+                        out.write(" \"");
+                        out.write(env[i].substring(eqIndex + 1));
+                        out.write('\"');
+                        out.newLine();
+                    }
+                }
+            }
+            out.write("$ " + cmd[0]);
+            for (int i = 1; i < cmd.length; i++) {
+                out.write(" -");
+                out.newLine();
+                out.write(cmd[i]);
+            }
+        } finally {
+            FileUtils.close(out);
+        }
+        return script;
+    }
+
+    private void deleteAfter(final File f, final Process p) {
+        new Thread() {
+            @Override
+            public void run() {
+                try {
+                    p.waitFor();
+                } catch(InterruptedException e) {
+                    // ignore
+                }
+                FileUtils.delete(f);
+            }
+        }.start();
+    }
+}
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/taskdefs/launcher/WinNTCommandLauncher.java b/src/main/org/apache/tools/ant/taskdefs/launcher/WinNTCommandLauncher.java
new file mode 100644
index 000000000..49432dc61
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/launcher/WinNTCommandLauncher.java
@@ -0,0 +1,61 @@
+package org.apache.tools.ant.taskdefs.launcher;
+
+import java.io.File;
+import java.io.IOException;
+
+import org.apache.tools.ant.Project;
+
+/**
+ * A command launcher for Windows XP/2000/NT that uses 'cmd.exe' when
+ * launching commands in directories other than the current working
+ * directory.
+ */
+public class WinNTCommandLauncher extends CommandLauncherProxy {
+    public WinNTCommandLauncher(CommandLauncher launcher) {
+        super(launcher);
+    }
+
+    /**
+     * Launches the given command in a new process, in the given
+     * working directory.
+     * 
+     * @param project
+     *        the Ant project.
+     * @param cmd
+     *        the command line to execute as an array of strings.
+     * @param env
+     *        the environment to set as an array of strings.
+     * @param workingDir
+     *        working directory where the command should run.
+     * @return the created Process.
+     * @throws IOException
+     *         forwarded from the exec method of the command launcher.
+     */
+    @Override
+    public Process exec(Project project, String[] cmd, String[] env,
+                        File workingDir) throws IOException {
+        File commandDir = workingDir;
+        if (workingDir == null) {
+            if (project != null) {
+                commandDir = project.getBaseDir();
+            } else {
+                return exec(project, cmd, env);
+            }
+        }
+        // Use cmd.exe to change to the specified directory before running
+        // the command
+        final int preCmdLength = 6;
+        String[] newcmd = new String[cmd.length + preCmdLength];
+        // CheckStyle:MagicNumber OFF - do not bother
+        newcmd[0] = "cmd";
+        newcmd[1] = "/c";
+        newcmd[2] = "cd";
+        newcmd[3] = "/d";
+        newcmd[4] = commandDir.getAbsolutePath();
+        newcmd[5] = "&&";
+        // CheckStyle:MagicNumber ON
+        System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
+
+        return exec(project, newcmd, env);
+    }
+}
\ No newline at end of file
