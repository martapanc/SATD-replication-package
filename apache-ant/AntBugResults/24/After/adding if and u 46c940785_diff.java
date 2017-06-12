diff --git a/WHATSNEW b/WHATSNEW
index 8f990616c..dee636274 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1031 +1,1034 @@
 Changes from Ant 1.9.0 TO current
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 
 
 Fixed bugs:
 -----------
 
  * Corrected XSLTC error in <junitreport>.
    Bugzilla Report 54641.
 
  * Provide more control over Zip64 extensions created by <zip> and
    related tasks.  In particular no Zip64 extensions will be used at
    all by the <jar> task family by default - this is required for jars
    to be readably by Java5.
    Bugzilla Report 54762.
 
  * Fixed loading of external dependencies in JUnit task.
    Bugzilla Report 54835.
 
 Other changes:
 --------------
 
  * strict attribute added to <signjar>.
    Bugzilla Report 54889.
 
  * simplifying Execute.getEnvironmentVariables since we are only running on Java 1.5 or higher now
 
+ * Add conditional attributes
+   Bugzilla report 43362
+
 Changes from Ant 1.8.4 TO Ant 1.9.0
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * Ant now requires at least Java 1.5 to compile and to run
 
  * FixCRLF used to treat the EOL value ASIS to convert to the system property
    line.separator. Specified was that ASIS would leave the EOL characters alone,
    the task now really leaves the EOL characters alone. This also implies that
    EOL ASIS will not insert a newline even if fixlast is set to true.
    Bugzilla report 53036
 
  * The CommandLauncher hierarchy that used to be a set of inner
    classes of Execute has been extracted to the
    org.apache.tools.ant.taskdefs.launcher package.
 
  * Any FileResource whose represented File has a parent also has a basedir.
 
  * Removing the Perforce Ant tasks replaced by tasks supplied by Perforce Inc.
 
  * Setting the default encoding of StringResource to UTF-8 instead of null
 
  * Upgrade JUnit 4 to JUnit 4.11
 
 Fixed bugs:
 -----------
 
  * Made VectorSet faster.
    Bugzilla Report 53622.
 
  * Incorrect URLs in Ant child POMs.
    Bugzilla Report 53617.
 
  * Subclasses of JUnitTask did not correctly find junit.jar.
    Bugzilla Report 53571.
 
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
 
  * <packagemapper> failed to strip the non-matched parts with
    handledirsep="true".
    Bugzilla Report 53399.
 
  * <expandproperties> filter caused a NullPointerException when input
    was empty.
    Bugzilla Report 53626.
 
  * <get> now supports HTTP redirects using status code 307.
    Bugzilla Report 54374.
 
  * ssh tasks prompt for kerberos username/password under Java 7
    Bugzilla Report 53437.
 
  * Zip task on <mappedresources> that excludes certain files by way of the mapper resulted in a NullPointerException
    Bugzilla Report 54026
 
  * The ant launcher script should properly detect JAVA_HOME on
    MacOS X 10.7
    Bugzilla Report 52632
 
  * Depend task does not handle invokeDynamic constant pool entries - java.lang.ClassFormatError: Invalid Constant Pool entry Type 18
    Bugzilla Report 54090
 
  * Base64Converter not properly handling bytes with MSB set (not masking byte to int conversion)
    Bugzilla Report 54460
 
  * The size resource comparator would return wrong results if file
    sizes differed by more than 2 GB.
    Bugzilla Report 54623
 
  * Unable to encode properly into UTF-8 when the system property file.encoding is
    set to ANSI_X3.4-1968.
    Bugzilla Report 54606
 
  * JUnit4 tests marked @Ignore do not appear in XML output
    Bugzilla Report 43969
 
 Other changes:
 --------------
 
  * merged the ZIP package from Commons Compress, it can now read
    archives using Zip64 extensions (files and archives bigger that 4GB
    and with more that 64k entries).
 
  * a new task <commandlauncher> can be used to configure the
    CommandLauncher used by Ant when forking external programs or new
    Java VMs.
    Bugzilla Report 52706.
 
  * merged the TAR package from Commons Compress, it can now read
    archives using POSIX extension headers and STAR extensions.
 
  * merged the BZIP2 package from Commons Compress, it can now
    optionally read files that contain multiple streams properly.
 
  * <bunzip2> will now properly expand files created by pbzip2 and
    similar tools that create files with multiple bzip2 streams.
 
  * <tar> now supports a new "posix" option for longfile-mode which
    will make it create PAX extension headers for long file names.  PAX
    extension headers are supported by all modern implementations of
    tar including GNU tar.
    This option should now be used in preference to "warn" or "gnu" as
    it is more portable.  For backwards compatibility reasons "warn"
    will still create "gnu" extensions rather than "posix" extensions.
 
  * The ProjectHelper class now exposes a method to be used by third party
    implementations to properly resolve the binding between target extensions
    and extension points.
    Bugzilla Report 53549.
 
  * Make extension point bindable to imported prefixed targets
    Bugzilla Report 53550.
 
  * Add the possibility to register a custom command line argument processor.
    See org.apache.tools.ant.ArgumentProcessor and manual/argumentprocessor.html
 
  * add the possibility to suppress stdout in the sshexec task.
    Bugzilla Report 50270.
 
  * add an encoding attribute to the contains selector.
    This will be useful to use the contains selector if the encoding of the VM is different from the encoding
    of the files being selected.
 
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
diff --git a/manual/Types/namespace.html b/manual/Types/namespace.html
index 33a214245..feacc8a03 100644
--- a/manual/Types/namespace.html
+++ b/manual/Types/namespace.html
@@ -1,220 +1,224 @@
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
 <!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
 <html><head><link rel="stylesheet" type="text/css" href="../stylesheets/style.css">
 <title>XmlNamespaceSupport</title></head>
   <body>
     <h2><a name="namespace">XML Namespace Support</a></h2>
     Apache Ant 1.6 introduces support for XML namespaces. 
     <h3>History</h3>
     
     <p>
       All releases of Ant prior to Ant 1.6 do not support XML namespaces.
       No support basically implies two things here:
     </p>
     <ul>
       <li> Element names correspond to the "qname" of the tags, which is
         usually the same as the local name. But if the build file writer uses
         colons in names of defined tasks/types, those become part of the
         element name. Turning on namespace support gives colon-separated
         prefixes in tag names a special meaning, and thus build files using
         colons in user-defined tasks and types will break.
       </li>
       <li> Attributes with the names 'xmlns' and 'xmlns:<code>&lt;prefix&gt;</code>'
         are not treated specially, which means that custom tasks and types have
         actually been able to use such attributes as parameter names. Again,
         such tasks/types are going to break when namespace support is enabled
         on the parser.
       </li>
     </ul>
     <p>Use of colons in element names has been discouraged in the past,
       and using any attribute starting with "xml" is actually strongly
       discouraged by the XML spec to reserve such names for future use.
     </p>
     <h3>Motivation</h3>
 
     <p>In build files using a lot of custom and third-party tasks, it is
       easy to get into name conflicts. When individual types are defined, the
       build file writer can do some namespacing manually (for example, using
       "tomcat-deploy" instead of just "deploy"). But when defining whole
       libraries of types using the <code>&lt;typedef&gt;</code> 'resource' attribute, the
       build file writer has no chance to override or even prefix the names
       supplied by the library. </p>
     <h3>Assigning Namespaces</h3>
 
     <p>
       Adding a 'prefix' attribute to <code>&lt;typedef&gt;</code> might have been enough,
       but XML already has a well-known method for namespacing. Thus, instead
       of adding a 'prefix' attribute, the <code>&lt;typedef&gt;</code> and <code>&lt;taskdef&gt;</code>
       tasks get a 'uri' attribute, which stores the URI of the XML namespace
       with which the type should be associated:
     </p><pre> &lt;typedef resource="org/example/tasks.properties" uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
  &lt;my:task xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
     ...
  &lt;/my:task&gt;
 </pre>
     <p>As the above example demonstrates, the namespace URI needs to be
       specified at least twice: one time as the value of the 'uri' attribute,
       and another time to actually map the namespace to occurrences of
       elements from that namespace, by using the 'xmlns' attribute. This
       mapping can happen at any level in the build file:
     </p><pre> &lt;project name="test" xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt; 
    &lt;typedef resource="org/example/tasks.properties" uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
    &lt;my:task&gt;
      ...
    &lt;/my:task&gt;
  &lt;/project&gt;
 </pre>
     <p>
       Use of a namespace prefix is of course optional. Therefore
       the example could also look like this:
     </p><pre> &lt;project name="test"&gt; 
    &lt;typedef resource="org/example/tasks.properties" uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
    &lt;task xmlns="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
      ...
    &lt;/task&gt;
  &lt;/project&gt;
 </pre>
     <p>
       Here, the namespace is set as the default namespace for the <code>&lt;task&gt;</code>
       element and all its descendants.
     </p>
     <h3>Default namespace</h3>
     <p>
       The default namespace used by Ant is "antlib:org.apache.tools.ant".
     </p>
     <pre>
 &lt;typedef resource="org/example/tasks.properties" uri="antlib:org.apache.tools.ant"/&gt;
 &lt;task&gt;
       ....
 &lt;/task&gt;
     </pre>
 
      
 
     <h3>Namespaces and Nested Elements</h3>
 
     <p>
       Almost always in Ant 1.6, elements nested inside a namespaced
       element have the same namespace as their parent. So if 'task' in the
       example above allowed a nested 'config' element, the build file snippet
       would look like this:
     </p><pre> &lt;typedef resource="org/example/tasks.properties" uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
  &lt;my:task xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
    &lt;my:config a="foo" b="bar"/&gt;
    ...
  &lt;/my:task&gt;
 </pre>
     <p>If the element allows or requires a lot of nested elements, the
       prefix needs to be used for every nested element. Making the namespace
       the default can reduce the verbosity of the script:
     </p><pre> &lt;typedef resource="org/example/tasks.properties" uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
           &lt;task xmlns="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
           &lt;config a="foo" b="bar"/&gt;
    ...
           &lt;/task&gt;
         </pre>
     <p>
       From Ant 1.6.2, elements nested inside a namespaced element may also be
       in Ant's default namespace. This means that the following is now allowed:
     </p>
     </p><pre> &lt;typedef resource="org/example/tasks.properties"
    uri="<a href="http://example.org/tasks">http://example.org/tasks</a>"/&gt;
  &lt;my:task xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
    &lt;config a="foo" b="bar"/&gt;
    ...
  &lt;/my:task&gt;
 </pre>
       
     <h3>Namespaces and Attributes</h3>
 
     <p>
       Attributes are only used to configure the element they belong to if:
     </p>
     <ul>
       <li> they have no namespace (note that the default namespace does *not* apply to attributes)
       </li>
       <li> they are in the same namespace as the element they belong to
       </li>
     </ul>
     <p>
+      In Ant 1.9.1 two attribute namespaces ant:if and ant:unless are added in order to allow to insert elements
+      conditionally
+    </p>
+    <p>
       Other attributes are simply ignored.
     </p>
     <p>
       This means that both:
     </p>
     <p>
     </p><pre> &lt;my:task xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
    &lt;my:config a="foo" b="bar"/&gt;
    ...
  &lt;/my:task&gt;
 </pre>
     <p>
       and
     </p>
     <pre> &lt;my:task xmlns:my="<a href="http://example.org/tasks">http://example.org/tasks</a>"&gt;
    &lt;my:config my:a="foo" my:b="bar"/&gt;
    ...
  &lt;/my:task&gt;
 </pre>
     <p>
       result in the parameters "a" and "b" being used as parameters to configure the nested "config" element.
     </p>
     <p>It also means that you can use attributes from other namespaces
       to markup the build file with extra metadata, such as RDF and
       XML-Schema (whether that's a good thing or not). The same is not true
       for elements from unknown namespaces, which result in a error.
     </p>
     <h3>Mixing Elements from Different Namespaces</h3>
 
     <p>Now comes the difficult part: elements from different namespaces can
       be woven together under certain circumstances. This has a lot to do
       with the Ant 1.6
       <a href="../develop.html#nestedtype">add type introspection rules</a>:
       Ant types and tasks are now free to accept arbitrary named types as
       nested elements, as long as the concrete type implements the interface
       expected by the task/type. The most obvious example for this is the
       <code>&lt;condition&gt;</code> task, which supports various nested conditions, all
       of which extend the interface <tt>Condition</tt>. To integrate a
       custom condition in Ant, you can now simply <code>&lt;typedef&gt;</code> the
       condition, and then use it anywhere nested conditions are allowed
       (assuming the containing element has a generic <tt>add(Condition)</tt> or <tt>addConfigured(Condition)</tt> method):
 </p><pre> &lt;typedef resource="org/example/conditions.properties" uri="<a href="http://example.org/conditions">http://example.org/conditions</a>"/&gt;
  &lt;condition property="prop" xmlns="<a href="http://example.org/conditions">http://example.org/conditions</a>"&gt;
    &lt;and&gt;
      &lt;available file="bla.txt"/&gt;
      &lt;my:condition a="foo"/&gt;
    &lt;/and&gt;
  &lt;/condition&gt;
 </pre>
     <p>
       In Ant 1.6, this feature cannot be used as much as we'd all like to: a
       lot of code has not yet been adapted to the new introspection rules,
       and elements like Ant's built-in conditions and selectors are not
       really types in 1.6. This is expected to change in Ant 1.7.
     </p>
     <h3>Namespaces and Antlib</h3>
 
     <p>
       The new <a href="antlib.html">AntLib</a>
       feature is also very much integrated with the namespace support in Ant
       1.6. Basically, you can "import" Antlibs simply by using a special
       scheme for the namespace URI: the <tt>antlib</tt> scheme, which expects the package name in which a special <tt>antlib.xml</tt> file is located.
     </p>
 
 </body>
 </html>
diff --git a/manual/conceptstypeslist.html b/manual/conceptstypeslist.html
index 95dd79135..e4f3c1869 100644
--- a/manual/conceptstypeslist.html
+++ b/manual/conceptstypeslist.html
@@ -1,88 +1,89 @@
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
 
 <h3>Concepts</h3>
 <ul class="inlinelist">
 <li><a href="targets.html">Targets and Extension-Points</a></li>
 <li><a href="properties.html">Properties and PropertyHelpers</a></li>
 <li><a href="clonevm.html">ant.build.clonevm</a></li>
 <li><a href="sysclasspath.html">build.sysclasspath</a></li>
 <li><a href="javacprops.html">Ant properties controlling javac</a></li>
 <li><a href="Tasks/common.html">Common Attributes</a></li>
+<li><a href="ifunless.html">If and Unless Attributes</a></li>
 </ul>
 
 <h3>List of Types</h3>
 <ul class="inlinelist">
 <li><a href="Types/classfileset.html">Class Fileset</a></li>
 <li><a href="Types/description.html">Description Type</a></li>
 <li><a href="dirtasks.html">Directory-based Tasks</a></li>
 <li><a href="Types/dirset.html">DirSet</a></li>
 <li><a href="Types/extension.html">Extension Package</a></li>
 <li><a href="Types/extensionset.html">Set of Extension Packages</a></li>
 <li><a href="Types/filelist.html">FileList</a></li>
 <li><a href="Types/fileset.html">FileSet</a></li>
 <li><a href="Types/mapper.html">File Mappers</a></li>
 <li><a href="Types/filterchain.html">FilterChains and FilterReaders</a></li>
 <li><a href="Types/filterset.html">FilterSet</a></li>
 <li><a href="Types/patternset.html">PatternSet</a></li>
 <li><a href="using.html#path">Path-like Structures</a></li>
 <li><a href="Types/permissions.html">Permissions</a></li>
 <li><a href="Types/propertyset.html">PropertySet</a></li>
 <li><a href="Types/redirector.html">I/O Redirectors</a></li>
 <li><a href="Types/regexp.html">Regexp</a></li>
 <li><a href="Types/resources.html">Resources</a></li>
 <li><a href="Types/resources.html#collection">Resource Collections</a></li>
 <li><a href="Types/selectors.html">Selectors</a></li>
 <li><a href="Types/tarfileset.html">TarFileSet</a></li>
 <li><a href="Types/xmlcatalog.html">XMLCatalog</a></li>
 <li><a href="Types/zipfileset.html">ZipFileSet</a></li>
 </ul>
 
 <h3>Namespace</h3>
 <ul class="inlinelist">
 <li><a href="Types/namespace.html">Namespace Support</a></li>
 </ul>
 
 <h3>Antlib</h3>
 <ul class="inlinelist">
 <li><a href="Types/antlib.html">Antlib</a></li>
 <li><a href="Types/antlib.html#antlibnamespace">Antlib namespace</a></li>
 <li><a href="Types/antlib.html#currentnamespace">Current namespace</a></li>
 </ul>
 
 <h3>Custom Components</h3>
 <ul class="inlinelist">
 <li><a href="Types/custom-programming.html">Custom Components</a></li>
 <li><a href="Types/custom-programming.html#customconditions">Conditions</a></li>
 <li><a href="Types/custom-programming.html#customselectors">Selectors</a></li>
 <li><a href="Types/custom-programming.html#filterreaders">FilterReaders</a></li>
 </ul>
 
 </body>
 </html>
diff --git a/manual/ifunless.html b/manual/ifunless.html
new file mode 100644
index 000000000..02ea00785
--- /dev/null
+++ b/manual/ifunless.html
@@ -0,0 +1,64 @@
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
+  <meta http-equiv="Content-Language" content="en-us"/>
+  <link rel="stylesheet" type="text/css" href="stylesheets/style.css"/>
+  <title>If and Unless on all tasks/nested elements</title>
+</head>
+
+<body>
+  <h1><a name="if_and_unless">If And Unless</a></h1>
+
+  <p>Since Ant 1.9.1 it is possible to add if and unless attributes on all tasks and nested elements using special namespaces.</p>
+
+  <p>In order to use this feature you need to add the following namespace declarations</p>
+  <blockquote><pre>
+    xmlns:if=&quot;ant:if&quot;
+    xmlns:unless=&quot;ant:unless&quot;
+    </pre>
+  </blockquote>
+
+  <p>The if and unless namespaces support the following 3 conditions :
+  <ul>
+    <li>true</li>true if the value of the property evaluates to true
+    <li>blank</li>true if the value of the property is null or empty
+    <li>set</li>true if the property is set
+  </ul></p>
+
+<blockquote>
+<pre>
+&lt;project name=&quot;tryit&quot;
+ xmlns:if=&quot;ant:if&quot;
+ xmlns:unless=&quot;ant:unless&quot;
+&gt;
+ &lt;exec executable=&quot;java&quot;&gt;
+   &lt;arg line=&quot;-X&quot; if:true=&quot;showextendedparams&quot;/&gt;
+   &lt;arg line=&quot;-version&quot; unless:true=&quot;showextendedparams&quot;/&gt;
+ &lt;/exec&gt;
+ &lt;condition property=&quot;onmac&quot;&gt;
+   &lt;os family=&quot;mac&quot;/&gt;
+ &lt;/condition&gt;
+ &lt;echo if:set=&quot;onmac&quot;&gt;running on MacOS&lt;/echo&gt;
+ &lt;echo unless:set=&quot;onmac&quot;&gt;not running on MacOS&lt;/echo&gt;
+&lt;/project&gt;
+</pre>
+</blockquote>
+
+</body>
+</html>
diff --git a/src/etc/checkstyle/checkstyle-config b/src/etc/checkstyle/checkstyle-config
index 877fb9077..160b098d3 100644
--- a/src/etc/checkstyle/checkstyle-config
+++ b/src/etc/checkstyle/checkstyle-config
@@ -1,134 +1,134 @@
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
 <!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.1//EN" "http://www.puppycrawl.com/dtds/configuration_1_1.dtd">
 
 <module name="Checker">
   <module name="TreeWalker">
     <!-- Javadoc requirements -->
     <module name="JavadocType">
       <property name="scope" value="protected"/>
     </module>
     <module name="JavadocMethod">
       <property name="scope" value="protected"/>
       <property name="allowUndeclaredRTE" value="true"/>
     </module>
     <module name="JavadocVariable">
        <property name="scope" value="public"/>
     </module>
 
     <!-- element naming -->
     <module name="PackageName"/>
     <module name="TypeName"/>
     <module name="ConstantName"/>
     <module name="LocalFinalVariableName"/>
     <module name="LocalVariableName"/>
     <module name="MemberName"/>
     <module name="MethodName"/>
     <module name="ParameterName"/>
     <module name="StaticVariableName"/>
 
     <!-- required licence file -->
     <module name="Header">
         <property name="headerFile" value="${config.dir}/RequiredHeader.txt"/>
         <property name="ignoreLines" value="2"/>
     </module>
 
     <!-- Import conventions -->
     <module name="AvoidStarImport"/>
     <module name="IllegalImport"/>
     <module name="RedundantImport"/>
     <module name="UnusedImports"/>
 
     <!-- size limits -->
     <module name="FileLength"/>
     <module name="LineLength">
       <property name="max" value="100"/>
       <property name="ignorePattern" value="^ *\* *[^ ]+$"/>
     </module>
     <module name="MethodLength"/>
     <module name="ParameterNumber"/>
 
     <!-- whitespace checks -->
     <module name="EmptyForIteratorPad"/>
     <module name="NoWhitespaceAfter"/>
     <module name="NoWhitespaceBefore"/>
     <module name="OperatorWrap"/>
     <module name="ParenPad"/>
     <module name="TabCharacter"/>
     <module name="WhitespaceAfter"/>
     <module name="WhitespaceAround"/>
 
     <!-- Modifier Checks -->
     <module name="ModifierOrder"/>
     <module name="RedundantModifier"/>
 
 
     <!-- Checks for blocks -->
     <module name="AvoidNestedBlocks"/>
     <module name="EmptyBlock">
       <property name="option" value="text"/>
     </module>
     <module name="LeftCurly"/>
     <module name="NeedBraces"/>
     <module name="RightCurly"/>
 
 
     <!-- Checks for common coding problems -->
     <!--<module name="AvoidInlineConditionals"/> -->
     <module name="DoubleCheckedLocking"/>
     <module name="EmptyStatement"/>
     <module name="EqualsHashCode"/>
     <module name="IllegalInstantiation">
       <property name="classes" value="java.lang.Boolean"/>
     </module>
-    <module name="InnerAssignment"/>
-    <module name="MagicNumber"/>
+    <!-- <module name="InnerAssignment"/> -->
+    <!-- <module name="MagicNumber"/> -->
     <module name="MissingSwitchDefault"/>
     <!-- Allow redundant throw declarations for doc purposes 
     <module name="RedundantThrows">
       <property name="allowUnchecked" value="true"/>
     </module>
          -->
     <module name="SimplifyBooleanExpression"/>
     <module name="SimplifyBooleanReturn"/>
 
     <!-- Checks for class design -->
     <!-- <module name="DesignForExtension"/> -->
     <module name="FinalClass"/>
     <module name="HideUtilityClassConstructor"/>
     <module name="InterfaceIsType"/>
     <module name="VisibilityModifier"/>
 
     <!-- Miscellaneous other checks. -->
     <module name="ArrayTypeStyle"/>
     <module name="GenericIllegalRegexp">
       <property name="format" value="\s+$"/>
       <property name="message" value="Line has trailing spaces."/>
     </module>
-    <module name="TodoComment"/>
+    <!-- <module name="TodoComment"/> -->
     <module name="UpperEll"/>
     <!-- allow comment suppression of checks -->
     <module name="FileContentsHolder"/>
   </module>
   <!-- <module name="au.com.redhillconsulting.simian.SimianCheck"/> -->
   <module name="SuppressionCommentFilter">
     <property name="offCommentFormat" value="CheckStyle\:([\w\|]+) *OFF"/>
     <property name="onCommentFormat" value="CheckStyle\:([\w\|]+) *ON"/>
     <property name="checkFormat" value="$1"/>
   </module>
 </module>
diff --git a/src/main/org/apache/tools/ant/ComponentHelper.java b/src/main/org/apache/tools/ant/ComponentHelper.java
index c5823d1b8..44b7daa3c 100644
--- a/src/main/org/apache/tools/ant/ComponentHelper.java
+++ b/src/main/org/apache/tools/ant/ComponentHelper.java
@@ -1,1100 +1,1101 @@
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
 import java.io.InputStream;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.Stack;
 
 import org.apache.tools.ant.launch.Launcher;
 import org.apache.tools.ant.taskdefs.Definer;
 import org.apache.tools.ant.taskdefs.Typedef;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Component creation and configuration.
  *
  * The class is based around handing component
  * definitions in an AntTypeTable.
  *
  * The old task/type methods have been kept
  * for backward compatibly.
  * Project will just delegate its calls to this class.
  *
  * A very simple hook mechanism is provided that allows users to plug
  * in custom code. It is also possible to replace the default behavior
  * ( for example in an app embedding ant )
  *
  * @since Ant1.6
  */
 public class ComponentHelper  {
     /** Map of component name to lists of restricted definitions */
     private Map<String, List<AntTypeDefinition>>          restrictedDefinitions = new HashMap<String, List<AntTypeDefinition>>();
 
     /** Map from component name to anttypedefinition */
     private final Hashtable<String, AntTypeDefinition> antTypeTable = new Hashtable<String, AntTypeDefinition>();
 
     /** Map of tasks generated from antTypeTable */
     private final Hashtable<String, Class<?>> taskClassDefinitions = new Hashtable<String, Class<?>>();
 
     /** flag to rebuild taskClassDefinitions */
     private boolean rebuildTaskClassDefinitions = true;
 
     /** Map of types generated from antTypeTable */
     private final Hashtable<String, Class<?>> typeClassDefinitions = new Hashtable<String, Class<?>>();
 
     /** flag to rebuild typeClassDefinitions */
     private boolean rebuildTypeClassDefinitions = true;
 
     /** Set of namespaces that have been checked for antlibs */
     private final HashSet<String> checkedNamespaces = new HashSet<String>();
 
     /**
      * Stack of antlib contexts used to resolve definitions while
      *   processing antlib
      */
     private Stack<String> antLibStack = new Stack<String>();
 
     /** current antlib uri */
     private String antLibCurrentUri = null;
 
     /**
      * this does not appear to be used anywhere in the Ant codebase
      * even via its accessors
      */
     private ComponentHelper next;
 
     /**
      * Project that owns a component helper
      */
     private Project project;
 
     /**
      * Error string when the file taskdefs/defaults.properties cannot be found
      */
     private static final String ERROR_NO_TASK_LIST_LOAD = "Can't load default task list";
 
     /**
      * Error string when the typedefs/defaults.properties cannot be found
      */
     private static final String ERROR_NO_TYPE_LIST_LOAD = "Can't load default type list";
 
     /**
      * reference under which we register ourselves with a project -{@value}
      */
     public static final String COMPONENT_HELPER_REFERENCE = "ant.ComponentHelper";
 
     /**
      * string used to control build.syspath policy {@value}
      */
     private static final String BUILD_SYSCLASSPATH_ONLY = "only";
 
     /**
      * special name of ant's property task -{@value}. There is some
      * contrived work here to enable this early.
      */
     private static final String ANT_PROPERTY_TASK = "property";
 
     // {tasks, types}
     private static Properties[] defaultDefinitions = new Properties[2];
 
      /**
      * Get the project.
      * @return the project owner of this helper.
      */
      public Project getProject() {
          return project;
      }
 
     /**
      * Find a project component for a specific project, creating
      * it if it does not exist.
      * @param project the project.
      * @return the project component for a specific project.
      */
     public static ComponentHelper getComponentHelper(Project project) {
         if (project == null) {
             return null;
         }
         // Singleton for now, it may change ( per/classloader )
         ComponentHelper ph = (ComponentHelper) project.getReference(COMPONENT_HELPER_REFERENCE);
         if (ph != null) {
             return ph;
         }
         ph = new ComponentHelper();
         ph.setProject(project);
 
         project.addReference(COMPONENT_HELPER_REFERENCE, ph);
         return ph;
     }
 
     /**
      * Creates a new ComponentHelper instance.
      */
     protected ComponentHelper() {
     }
 
     /**
      * Set the next chained component helper.
      *
      * @param next the next chained component helper.
      */
     public void setNext(ComponentHelper next) {
         this.next = next;
     }
 
     /**
      * Get the next chained component helper.
      *
      * @return the next chained component helper.
      */
     public ComponentHelper getNext() {
         return next;
     }
 
     /**
      * Sets the project for this component helper.
      *
      * @param project the project for this helper.
      */
     public void setProject(Project project) {
         this.project = project;
 //        antTypeTable = new Hashtable<String, AntTypeDefinition>(project);
     }
 
     /**
      * @return A copy of the CheckedNamespace.
      */
     private synchronized Set<String> getCheckedNamespace() {
         @SuppressWarnings("unchecked")
         final Set<String> result = (Set<String>) checkedNamespaces.clone();
         return result;
     }
 
     /**
      * @return A deep copy of the restrictredDefinition
      */
     private Map<String, List<AntTypeDefinition>> getRestrictedDefinition() {
         final Map<String, List<AntTypeDefinition>> result = new HashMap<String, List<AntTypeDefinition>>();
         synchronized (restrictedDefinitions) {
             for (Map.Entry<String, List<AntTypeDefinition>> entry : restrictedDefinitions.entrySet()) {
                 List<AntTypeDefinition> entryVal = entry.getValue();
                 synchronized (entryVal) {
                     //copy the entryVal
                     entryVal = new ArrayList<AntTypeDefinition> (entryVal);
                 }
                 result.put(entry.getKey(), entryVal);
             }
         }
         return result;
     }
 
 
     /**
      * Used with creating child projects. Each child
      * project inherits the component definitions
      * from its parent.
      * @param helper the component helper of the parent project.
      */
     public void initSubProject(ComponentHelper helper) {
         // add the types of the parent project
         @SuppressWarnings("unchecked")
         final Hashtable<String, AntTypeDefinition> typeTable = (Hashtable<String, AntTypeDefinition>) helper.antTypeTable.clone();
         synchronized (antTypeTable) {
             for (AntTypeDefinition def : typeTable.values()) {
                 antTypeTable.put(def.getName(), def);
             }
         }
         // add the parsed namespaces of the parent project
         Set<String> inheritedCheckedNamespace = helper.getCheckedNamespace();
         synchronized (this) {
             checkedNamespaces.addAll(inheritedCheckedNamespace);
         }
         Map<String, List<AntTypeDefinition>> inheritedRestrictedDef = helper.getRestrictedDefinition();
         synchronized (restrictedDefinitions) {
             restrictedDefinitions.putAll(inheritedRestrictedDef);
         }
     }
 
     /**
      * Factory method to create the components.
      *
      * This should be called by UnknownElement.
      *
      * @param ue The Unknown Element creating this component.
      * @param ns Namespace URI. Also available as ue.getNamespace().
      * @param componentType The component type,
      *                       Also available as ue.getComponentName().
      * @return the created component.
      * @throws BuildException if an error occurs.
      */
     public Object createComponent(UnknownElement ue, String ns, String componentType)
             throws BuildException {
         Object component = createComponent(componentType);
         if (component instanceof Task) {
             Task task = (Task) component;
             task.setLocation(ue.getLocation());
             task.setTaskType(componentType);
             task.setTaskName(ue.getTaskName());
             task.setOwningTarget(ue.getOwningTarget());
             task.init();
         }
         return component;
     }
 
     /**
      * Create an object for a component.
      *
      * @param componentName the name of the component, if
      *                      the component is in a namespace, the
      *                      name is prefixed with the namespace uri and ":".
      * @return the class if found or null if not.
      */
     public Object createComponent(String componentName) {
         AntTypeDefinition def = getDefinition(componentName);
         return def == null ? null : def.create(project);
     }
 
     /**
      * Return the class of the component name.
      *
      * @param componentName the name of the component, if
      *                      the component is in a namespace, the
      *                      name is prefixed with the namespace uri and ":".
      * @return the class if found or null if not.
      */
     public Class<?> getComponentClass(String componentName) {
         AntTypeDefinition def = getDefinition(componentName);
         return def == null ? null : def.getExposedClass(project);
     }
 
     /**
      * Return the antTypeDefinition for a componentName.
      * @param componentName the name of the component.
      * @return the ant definition or null if not present.
      */
     public AntTypeDefinition getDefinition(String componentName) {
         checkNamespace(componentName);
         return antTypeTable.get(componentName);
     }
 
     /**
      * This method is initialization code implementing the original ant component
      * loading from /org/apache/tools/ant/taskdefs/default.properties
      * and /org/apache/tools/ant/types/default.properties.
      */
     public void initDefaultDefinitions() {
         initTasks();
         initTypes();
+        new DefaultDefinitions(this).execute();
     }
 
     /**
      * Adds a new task definition to the project.
      * Attempting to override an existing definition with an
      * equivalent one (i.e. with the same classname) results in
      * a verbose log message. Attempting to override an existing definition
      * with a different one results in a warning log message.
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
     public void addTaskDefinition(String taskName, Class<?> taskClass) {
         checkTaskClass(taskClass);
         AntTypeDefinition def = new AntTypeDefinition();
         def.setName(taskName);
         def.setClassLoader(taskClass.getClassLoader());
         def.setClass(taskClass);
         def.setAdapterClass(TaskAdapter.class);
         def.setClassName(taskClass.getName());
         def.setAdaptToClass(Task.class);
         updateDataTypeDefinition(def);
     }
 
     /**
      * Checks whether or not a class is suitable for serving as Ant task.
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
     public void checkTaskClass(final Class<?> taskClass) throws BuildException {
         if (!Modifier.isPublic(taskClass.getModifiers())) {
             final String message = taskClass + " is not public";
             project.log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         if (Modifier.isAbstract(taskClass.getModifiers())) {
             final String message = taskClass + " is abstract";
             project.log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         try {
             taskClass.getConstructor((Class[]) null);
             // don't have to check for public, since
             // getConstructor finds public constructors only.
         } catch (NoSuchMethodException e) {
             final String message = "No public no-arg constructor in " + taskClass;
             project.log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         if (!Task.class.isAssignableFrom(taskClass)) {
             TaskAdapter.checkTaskClass(taskClass, project);
         }
     }
 
     /**
      * Returns the current task definition hashtable. The returned hashtable is
      * "live" and so should not be modified.  Also, the returned table may be
      * modified asynchronously.
      *
      * @return a map of from task name to implementing class
      *         (String to Class).
      */
     public Hashtable<String, Class<?>> getTaskDefinitions() {
         synchronized (taskClassDefinitions) {
             synchronized (antTypeTable) {
                 if (rebuildTaskClassDefinitions) {
                     taskClassDefinitions.clear();
                     for (Map.Entry<String, AntTypeDefinition> e : antTypeTable.entrySet()) {
                         final Class<?> clazz = e.getValue().getExposedClass(project);
                         if (clazz == null) {
                             continue;
                         }
                         if (Task.class.isAssignableFrom(clazz)) {
                             taskClassDefinitions.put(e.getKey(), e.getValue().getTypeClass(project));
                         }
                     }
                     rebuildTaskClassDefinitions = false;
                 }
             }
         }
         return taskClassDefinitions;
     }
 
     /**
      * Returns the current type definition hashtable. The returned hashtable is
      * "live" and so should not be modified.
      *
      * @return a map of from type name to implementing class
      *         (String to Class).
      */
     public Hashtable<String, Class<?>> getDataTypeDefinitions() {
         synchronized (typeClassDefinitions) {
             synchronized (antTypeTable) {
                 if (rebuildTypeClassDefinitions) {
                     typeClassDefinitions.clear();
                     for (Map.Entry<String, AntTypeDefinition> e : antTypeTable.entrySet()) {
                         final Class<?> clazz = e.getValue().getExposedClass(project);
                         if (clazz == null) {
                             continue;
                         }
                         if (!Task.class.isAssignableFrom(clazz)) {
                             typeClassDefinitions.put(e.getKey(), e.getValue().getTypeClass(project));
                         }
                     }
                     rebuildTypeClassDefinitions = false;
                 }
             }
         }
         return typeClassDefinitions;
     }
 
     /**
      * This returns a list of restricted definitions for a name.
      * The returned List is "live" and so should not be modified.
      * Also, the returned list may be modified asynchronously.
      * Any access must be guarded with a lock on the list itself.
      *
      * @param componentName the name to use.
      * @return the list of restricted definitions for a particular name.
      */
     public List<AntTypeDefinition> getRestrictedDefinitions(String componentName) {
         synchronized (restrictedDefinitions) {
             return restrictedDefinitions.get(componentName);
         }
     }
 
     /**
      * Adds a new datatype definition.
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
     public void addDataTypeDefinition(String typeName, Class<?> typeClass) {
         final AntTypeDefinition def = new AntTypeDefinition();
         def.setName(typeName);
         def.setClass(typeClass);
         updateDataTypeDefinition(def);
         project.log(" +User datatype: " + typeName + "     " + typeClass.getName(),
                 Project.MSG_DEBUG);
     }
 
     /**
      * Describe <code>addDataTypeDefinition</code> method here.
      *
      * @param def an <code>AntTypeDefinition</code> value.
      */
     public void addDataTypeDefinition(AntTypeDefinition def) {
         if (!def.isRestrict()) {
            updateDataTypeDefinition(def);
         } else {
             updateRestrictedDefinition(def);
         }
     }
 
     /**
      * Returns the current datatype definition hashtable. The returned
      * hashtable is "live" and so should not be modified.
      *
      * @return a map of from datatype name to datatype definition
      *         (String to {@link AntTypeDefinition}).
      */
     public Hashtable<String, AntTypeDefinition> getAntTypeTable() {
         return antTypeTable;
     }
 
     /**
      * Creates a new instance of a task.
      *
      *  Called from Project.createTask(), which can be called by tasks.
      *
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
         Task task = createNewTask(taskType);
         if (task == null && taskType.equals(ANT_PROPERTY_TASK)) {
             // quick fix for Ant.java use of property before
             // initializing the project
             addTaskDefinition(ANT_PROPERTY_TASK, org.apache.tools.ant.taskdefs.Property.class);
             task = createNewTask(taskType);
         }
         return task;
     }
 
     /**
      * Creates a new instance of a task.
      * @since ant1.6
      * @param taskType The name of the task to create an instance of.
      *                 Must not be <code>null</code>.
      *
      * @return an instance of the specified task, or <code>null</code> if
      *         the task name is not recognised.
      *
      * @exception BuildException if the task name is recognised but task
      *                           creation fails.
      */
     private Task createNewTask(String taskType) throws BuildException {
         Class<?> c = getComponentClass(taskType);
         if (c == null || !(Task.class.isAssignableFrom(c))) {
             return null;
         }
         Object obj = createComponent(taskType);
         if (obj == null) {
             return null;
         }
         if (!(obj instanceof Task)) {
             throw new BuildException("Expected a Task from '" + taskType
                     + "' but got an instance of " + obj.getClass().getName() + " instead");
         }
         Task task = (Task) obj;
         task.setTaskType(taskType);
 
         // set default value, can be changed by the user
         task.setTaskName(taskType);
 
         project.log("   +Task: " + taskType, Project.MSG_DEBUG);
         return task;
     }
 
     /**
      * Creates a new instance of a data type.
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
         return createComponent(typeName);
     }
 
     /**
      * Returns a description of the type of the given element.
      * <p>
      * This is useful for logging purposes.
      *
      * @param element The element to describe.
      *                Must not be <code>null</code>.
      *
      * @return a description of the element type.
      *
      * @since Ant 1.6
      */
     public String getElementName(Object element) {
         return getElementName(element, false);
     }
 
     /**
      * Returns a description of the type of the given element.
      * <p>
      * This is useful for logging purposes.
      *
      * @param o     The element to describe.
      *              Must not be <code>null</code>.
      * @param brief whether to use a brief description.
      * @return a description of the element type.
      *
      * @since Ant 1.7
      */
     public String getElementName(Object o, boolean brief) {
         //  PR: I do not know what to do if the object class
         //      has multiple defines
         //      but this is for logging only...
         Class<?> elementClass = o.getClass();
         String elementClassname = elementClass.getName();
         synchronized (antTypeTable) {
             for (AntTypeDefinition def : antTypeTable.values()) {
                 if (elementClassname.equals(def.getClassName())
                         && (elementClass == def.getExposedClass(project))) {
                     String name = def.getName();
                     return brief ? name : "The <" + name + "> type";
                 }
             }
         }
         return getUnmappedElementName(o.getClass(), brief);
     }
 
     /**
      * Convenient way to get some element name even when you may not have a
      * Project context.
      * @param p       The optional Project instance.
      * @param o       The element to describe.
      *                Must not be <code>null</code>.
      * @param brief   whether to use a brief description.
      * @return a description of the element type.
      * @since Ant 1.7
      */
     public static String getElementName(Project p, Object o, boolean brief) {
         if (p == null) {
             p = Project.getProject(o);
         }
         return p == null ? getUnmappedElementName(o.getClass(), brief) : getComponentHelper(p)
                 .getElementName(o, brief);
     }
 
     private static String getUnmappedElementName(Class<?> c, boolean brief) {
         if (brief) {
             String name = c.getName();
             return name.substring(name.lastIndexOf('.') + 1);
         }
         return c.toString();
     }
 
     /**
      * Check if definition is a valid definition--it may be a
      * definition of an optional task that does not exist.
      * @param def the definition to test.
      * @return true if exposed type of definition is present.
      */
     private boolean validDefinition(AntTypeDefinition def) {
         return !(def.getTypeClass(project) == null || def.getExposedClass(project) == null);
     }
 
     /**
      * Check if two definitions are the same.
      * @param def  the new definition.
      * @param old the old definition.
      * @return true if the two definitions are the same.
      */
     private boolean sameDefinition(AntTypeDefinition def, AntTypeDefinition old) {
         boolean defValid = validDefinition(def);
         boolean sameValidity = (defValid == validDefinition(old));
         //must have same validity; then if they are valid they must also be the same:
         return sameValidity && (!defValid || def.sameDefinition(old, project));
     }
 
     /**
       * update the restricted definition table with a new or
       * modified definition.
       */
     private void updateRestrictedDefinition(AntTypeDefinition def) {
         String name = def.getName();
         List<AntTypeDefinition> list = null;
         synchronized (restrictedDefinitions) {
             list = restrictedDefinitions.get(name);
             if (list == null) {
                 list = new ArrayList<AntTypeDefinition>();
                 restrictedDefinitions.put(name, list);
             }
         }
         // Check if the classname is already present and remove it
         // if it is
         synchronized (list) {
             for (Iterator<AntTypeDefinition> i = list.iterator(); i.hasNext();) {
                 AntTypeDefinition current = i.next();
                 if (current.getClassName().equals(def.getClassName())) {
                     i.remove();
                     break;
                 }
             }
             list.add(def);
         }
     }
 
     /**
      * Update the component definition table with a new or
      * modified definition.
      * @param def the definition to update or insert.
      */
     private void updateDataTypeDefinition(AntTypeDefinition def) {
         String name = def.getName();
         synchronized (antTypeTable) {
             rebuildTaskClassDefinitions = true;
             rebuildTypeClassDefinitions = true;
             final AntTypeDefinition old = antTypeTable.get(name);
             if (old != null) {
                 if (sameDefinition(def, old)) {
                     return;
                 }
                 Class<?> oldClass = old.getExposedClass(project);
                 boolean isTask = oldClass != null && Task.class.isAssignableFrom(oldClass);
                 project.log("Trying to override old definition of "
                         + (isTask ? "task " : "datatype ") + name, (def.similarDefinition(old,
                         project)) ? Project.MSG_VERBOSE : Project.MSG_WARN);
             }
             project.log(" +Datatype " + name + " " + def.getClassName(), Project.MSG_DEBUG);
             antTypeTable.put(name, def);
         }
     }
 
     /**
      * Called at the start of processing an antlib.
      * @param uri the uri that is associated with this antlib.
      */
     public void enterAntLib(String uri) {
         antLibCurrentUri = uri;
         antLibStack.push(uri);
     }
 
     /**
      * @return the current antlib uri.
      */
     public String getCurrentAntlibUri() {
         return antLibCurrentUri;
     }
 
     /**
      * Called at the end of processing an antlib.
      */
     public void exitAntLib() {
         antLibStack.pop();
         antLibCurrentUri = (antLibStack.size() == 0) ? null : (String) antLibStack.peek();
     }
 
     /**
      * Load ant's tasks.
      */
     private void initTasks() {
         ClassLoader classLoader = getClassLoader(null);
         Properties props = getDefaultDefinitions(false);
         Enumeration<?> e = props.propertyNames();
         while (e.hasMoreElements()) {
             String name = (String) e.nextElement();
             String className = props.getProperty(name);
             AntTypeDefinition def = new AntTypeDefinition();
             def.setName(name);
             def.setClassName(className);
             def.setClassLoader(classLoader);
             def.setAdaptToClass(Task.class);
             def.setAdapterClass(TaskAdapter.class);
             antTypeTable.put(name, def);
         }
     }
 
     private ClassLoader getClassLoader(ClassLoader classLoader) {
         String buildSysclasspath = project.getProperty(MagicNames.BUILD_SYSCLASSPATH);
         if (project.getCoreLoader() != null
             && !(BUILD_SYSCLASSPATH_ONLY.equals(buildSysclasspath))) {
             classLoader = project.getCoreLoader();
         }
         return classLoader;
     }
 
     /**
      * Load default task or type definitions - just the names,
      *  no class loading.
      * Caches results between calls to reduce overhead.
      * @param type true for typedefs, false for taskdefs
      * @return a mapping from definition names to class names
      * @throws BuildException if there was some problem loading
      *                        or parsing the definitions list
      */
     private static synchronized Properties getDefaultDefinitions(boolean type)
             throws BuildException {
         int idx = type ? 1 : 0;
         if (defaultDefinitions[idx] == null) {
             String resource = type ? MagicNames.TYPEDEFS_PROPERTIES_RESOURCE
                     : MagicNames.TASKDEF_PROPERTIES_RESOURCE;
             String errorString = type ? ERROR_NO_TYPE_LIST_LOAD : ERROR_NO_TASK_LIST_LOAD;
             InputStream in = null;
             try {
                 in = ComponentHelper.class.getResourceAsStream(resource);
                 if (in == null) {
                     throw new BuildException(errorString);
                 }
                 Properties p = new Properties();
                 p.load(in);
                 defaultDefinitions[idx] = p;
             } catch (IOException e) {
                 throw new BuildException(errorString, e);
             } finally {
                 FileUtils.close(in);
             }
         }
         return defaultDefinitions[idx];
     }
 
     /**
      * Load ant's datatypes.
      */
     private void initTypes() {
         ClassLoader classLoader = getClassLoader(null);
         Properties props = getDefaultDefinitions(true);
         Enumeration<?> e = props.propertyNames();
         while (e.hasMoreElements()) {
             String name = (String) e.nextElement();
             String className = props.getProperty(name);
             AntTypeDefinition def = new AntTypeDefinition();
             def.setName(name);
             def.setClassName(className);
             def.setClassLoader(classLoader);
             antTypeTable.put(name, def);
         }
     }
 
     /**
      * Called for each component name, check if the
      * associated URI has been examined for antlibs.
      * @param componentName the name of the component, which should include a URI
      *                      prefix if it is in a namespace
      */
     private synchronized void checkNamespace(String componentName) {
         String uri = ProjectHelper.extractUriFromComponentName(componentName);
         if ("".equals(uri)) {
             uri = ProjectHelper.ANT_CORE_URI;
         }
         if (!uri.startsWith(ProjectHelper.ANTLIB_URI)) {
             return; // namespace that does not contain antlib
         }
         if (checkedNamespaces.contains(uri)) {
             return; // Already processed
         }
         checkedNamespaces.add(uri);
 
         if (antTypeTable.size() == 0) {
             // Project instance doesn't know the tasks and types
             // defined in defaults.properties, likely created by the
             // user - without those definitions it cannot parse antlib
             // files as taskdef, typedef and friends are unknown
             initDefaultDefinitions();
         }
         Typedef definer = new Typedef();
         definer.setProject(project);
         definer.init();
         definer.setURI(uri);
         //there to stop error messages being "null"
         definer.setTaskName(uri);
         //if this is left out, bad things happen. like all build files break
         //on the first element encountered.
         definer.setResource(Definer.makeResourceFromURI(uri));
         // a fishing expedition :- ignore errors if antlib not present
         definer.setOnError(new Typedef.OnError(Typedef.OnError.POLICY_IGNORE));
         definer.execute();
     }
 
     /**
      * Handler called to do decent diagnosis on instantiation failure.
      * @param componentName component name.
      * @param type component type, used in error messages
      * @return a string containing as much diagnostics info as possible.
      */
     public String diagnoseCreationFailure(String componentName, String type) {
         StringWriter errorText = new StringWriter();
         PrintWriter out = new PrintWriter(errorText);
         out.println("Problem: failed to create " + type + " " + componentName);
         //class of problem
         boolean lowlevel = false;
         boolean jars = false;
         boolean definitions = false;
         boolean antTask;
         String home = System.getProperty(Launcher.USER_HOMEDIR);
         File libDir = new File(home, Launcher.USER_LIBDIR);
         String antHomeLib;
         boolean probablyIDE = false;
         String anthome = System.getProperty(MagicNames.ANT_HOME);
         if (anthome != null) {
             File antHomeLibDir = new File(anthome, "lib");
             antHomeLib = antHomeLibDir.getAbsolutePath();
         } else {
             //running under an IDE that doesn't set ANT_HOME
             probablyIDE = true;
             antHomeLib = "ANT_HOME" + File.separatorChar + "lib";
         }
         StringBuffer dirListingText = new StringBuffer();
         final String tab = "        -";
         dirListingText.append(tab);
         dirListingText.append(antHomeLib);
         dirListingText.append('\n');
         if (probablyIDE) {
             dirListingText.append(tab);
             dirListingText.append("the IDE Ant configuration dialogs");
         } else {
             dirListingText.append(tab);
             dirListingText.append(libDir);
             dirListingText.append('\n');
             dirListingText.append(tab);
             dirListingText.append("a directory added on the command line with the -lib argument");
         }
         String dirListing = dirListingText.toString();
 
         //look up the name
         AntTypeDefinition def = getDefinition(componentName);
         if (def == null) {
             //not a known type
             printUnknownDefinition(out, componentName, dirListing);
             definitions = true;
         } else {
             //we are defined, so it is an instantiation problem
             final String classname = def.getClassName();
             antTask = classname.startsWith("org.apache.tools.ant.");
             boolean optional = classname.startsWith("org.apache.tools.ant.taskdefs.optional");
             optional |= classname.startsWith("org.apache.tools.ant.types.optional");
 
             //start with instantiating the class.
             Class<?> clazz = null;
             try {
                 clazz = def.innerGetTypeClass();
             } catch (ClassNotFoundException e) {
                 jars = true;
                 if (!optional) {
                     definitions = true;
                 }
                 printClassNotFound(out, classname, optional, dirListing);
             } catch (NoClassDefFoundError ncdfe) {
                 jars = true;
                 printNotLoadDependentClass(out, optional, ncdfe, dirListing);
             }
             //here we successfully loaded the class or failed.
             if (clazz != null) {
                 //success: proceed with more steps
                 try {
                     def.innerCreateAndSet(clazz, project);
                     //hey, there is nothing wrong with us
                     out.println("The component could be instantiated.");
                 } catch (NoSuchMethodException e) {
                     lowlevel = true;
                     out.println("Cause: The class " + classname
                             + " has no compatible constructor.");
 
                 } catch (InstantiationException e) {
                     lowlevel = true;
                     out.println("Cause: The class " + classname
                             + " is abstract and cannot be instantiated.");
                 } catch (IllegalAccessException e) {
                     lowlevel = true;
                     out.println("Cause: The constructor for " + classname
                             + " is private and cannot be invoked.");
                 } catch (InvocationTargetException ex) {
                     lowlevel = true;
                     Throwable t = ex.getTargetException();
                     out.println("Cause: The constructor threw the exception");
                     out.println(t.toString());
                     t.printStackTrace(out);
                 }  catch (NoClassDefFoundError ncdfe) {
                     jars = true;
                     out.println("Cause:  A class needed by class " + classname
                             + " cannot be found: ");
                     out.println("       " + ncdfe.getMessage());
                     out.println("Action: Determine what extra JAR files are"
                             + " needed, and place them in:");
                     out.println(dirListing);
                 }
             }
             out.println();
             out.println("Do not panic, this is a common problem.");
             if (definitions) {
                 out.println("It may just be a typographical error in the build file "
                         + "or the task/type declaration.");
             }
             if (jars) {
                 out.println("The commonest cause is a missing JAR.");
             }
             if (lowlevel) {
                 out.println("This is quite a low level problem, which may need "
                         + "consultation with the author of the task.");
                 if (antTask) {
                     out.println("This may be the Ant team. Please file a "
                             + "defect or contact the developer team.");
                 } else {
                     out.println("This does not appear to be a task bundled with Ant.");
                     out.println("Please take it up with the supplier of the third-party " + type
                             + ".");
                     out.println("If you have written it yourself, you probably have a bug to fix.");
                 }
             } else {
                 out.println();
                 out.println("This is not a bug; it is a configuration problem");
             }
         }
         out.flush();
         out.close();
         return errorText.toString();
     }
 
     /**
      * Print unknown definition.forking
      */
     private void printUnknownDefinition(PrintWriter out, String componentName, String dirListing) {
         boolean isAntlib = componentName.indexOf(MagicNames.ANTLIB_PREFIX) == 0;
         String uri = ProjectHelper.extractUriFromComponentName(componentName);
         out.println("Cause: The name is undefined.");
         out.println("Action: Check the spelling.");
         out.println("Action: Check that any custom tasks/types have been declared.");
         out.println("Action: Check that any <presetdef>/<macrodef>"
                 + " declarations have taken place.");
         if (uri.length() > 0) {
             final List<AntTypeDefinition> matches = findTypeMatches(uri);
             if (matches.size() > 0) {
                 out.println();
                 out.println("The definitions in the namespace " + uri + " are:");
                 for (AntTypeDefinition def : matches) {
                     String local = ProjectHelper.extractNameFromComponentName(def.getName());
                     out.println("    " + local);
                 }
             } else {
                 out.println("No types or tasks have been defined in this namespace yet");
                 if (isAntlib) {
                     out.println();
                     out.println("This appears to be an antlib declaration. ");
                     out.println("Action: Check that the implementing library exists in one of:");
                     out.println(dirListing);
                 }
             }
         }
     }
 
     /**
      * Print class not found.
      */
     private void printClassNotFound(PrintWriter out, String classname, boolean optional,
             String dirListing) {
         out.println("Cause: the class " + classname + " was not found.");
         if (optional) {
             out.println("        This looks like one of Ant's optional components.");
             out.println("Action: Check that the appropriate optional JAR exists in");
             out.println(dirListing);
         } else {
             out.println("Action: Check that the component has been correctly declared");
             out.println("        and that the implementing JAR is in one of:");
             out.println(dirListing);
         }
     }
 
     /**
      * Print could not load dependent class.
      */
     private void printNotLoadDependentClass(PrintWriter out, boolean optional,
             NoClassDefFoundError ncdfe, String dirListing) {
         out.println("Cause: Could not load a dependent class "
                     +  ncdfe.getMessage());
         if (optional) {
             out.println("       It is not enough to have Ant's optional JARs");
             out.println("       you need the JAR files that the" + " optional tasks depend upon.");
             out.println("       Ant's optional task dependencies are" + " listed in the manual.");
         } else {
             out.println("       This class may be in a separate JAR" + " that is not installed.");
         }
         out.println("Action: Determine what extra JAR files are"
                 + " needed, and place them in one of:");
         out.println(dirListing);
     }
 
     /**
      * Create a list of all definitions that match a prefix, usually the URI
      * of a library
      * @param prefix prefix to match off
      * @return the (possibly empty) list of definitions
      */
     private List<AntTypeDefinition> findTypeMatches(String prefix) {
         final List<AntTypeDefinition> result = new ArrayList<AntTypeDefinition>();
         synchronized (antTypeTable) {
             for (AntTypeDefinition def : antTypeTable.values()) {
                 if (def.getName().startsWith(prefix)) {
                     result.add(def);
                 }
             }
         }
         return result;
     }
 }
diff --git a/src/main/org/apache/tools/ant/DefaultDefinitions.java b/src/main/org/apache/tools/ant/DefaultDefinitions.java
new file mode 100644
index 000000000..0de0e1a27
--- /dev/null
+++ b/src/main/org/apache/tools/ant/DefaultDefinitions.java
@@ -0,0 +1,75 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant;
+
+/**
+ * Default definitions.
+ */
+public final class DefaultDefinitions {
+    private static final String IF_NAMESPACE = "ant:if";
+    private static final String UNLESS_NAMESPACE = "ant:unless";
+    private static final String OATA = "org.apache.tools.ant.";
+
+    private final ComponentHelper componentHelper;
+
+    /**
+     * Create a default definitions object.
+     * @param componentHelper the componenthelper to initialize.
+     */
+    public DefaultDefinitions(ComponentHelper componentHelper) {
+        this.componentHelper = componentHelper;
+    }
+
+    /**
+     * Register the defintions.
+     */
+    public void execute() {
+        attributeNamespaceDef(IF_NAMESPACE);
+        attributeNamespaceDef(UNLESS_NAMESPACE);
+
+        ifUnlessDef("true", "IfTrueAttribute");
+        ifUnlessDef("set", "IfSetAttribute");
+        ifUnlessDef("blank", "IfBlankAttribute");
+    }
+
+    private void attributeNamespaceDef(String ns) {
+        AntTypeDefinition def = new AntTypeDefinition();
+        def.setName(ProjectHelper.nsToComponentName(ns));
+        def.setClassName(OATA + "attribute.AttributeNamespace");
+        def.setClassLoader(getClass().getClassLoader());
+        def.setRestrict(true);
+        componentHelper.addDataTypeDefinition(def);
+    }
+
+    private void ifUnlessDef(String name, String base) {
+        String classname =  OATA + "attribute." + base;
+        componentDef(IF_NAMESPACE, name, classname);
+        componentDef(UNLESS_NAMESPACE, name, classname + "$Unless");
+    }
+
+    private void componentDef(String ns, String name, String classname) {
+        AntTypeDefinition def = new AntTypeDefinition();
+        String n = ProjectHelper.genComponentName(ns, name);
+        def.setName(ProjectHelper.genComponentName(ns, name));
+        def.setClassName(classname);
+        def.setClassLoader(getClass().getClassLoader());
+        def.setRestrict(true);
+        componentHelper.addDataTypeDefinition(def);
+    }
+}
diff --git a/src/main/org/apache/tools/ant/MagicNames.java b/src/main/org/apache/tools/ant/MagicNames.java
index 9d021c848..2871269cd 100644
--- a/src/main/org/apache/tools/ant/MagicNames.java
+++ b/src/main/org/apache/tools/ant/MagicNames.java
@@ -1,280 +1,284 @@
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
 
 import org.apache.tools.ant.launch.Launcher;
 
 /**
  * Magic names used within Ant.
  *
  * Not all magic names are here yet.
  *
  * @since Ant 1.6
  */
 public final class MagicNames {
 
     private MagicNames() {
     }
 
     /**
      * prefix for antlib URIs:
      * {@value}
      */
     public static final String ANTLIB_PREFIX = "antlib:";
 
     /**
      * Ant version property.
      * Value: {@value}
      */
     public static final String ANT_VERSION = "ant.version";
 
     /**
      * System classpath policy.
      * Value: {@value}
      */
     public static final String BUILD_SYSCLASSPATH = "build.sysclasspath";
 
     /**
      * The name of the script repository used by the script repo task.
      * Value {@value}
      */
     public static final String SCRIPT_REPOSITORY = "org.apache.ant.scriptrepo";
 
     /**
      * The name of the reference to the System Class Loader.
      * Value {@value}
      **/
     public static final String SYSTEM_LOADER_REF = "ant.coreLoader";
 
     /**
      * Name of the property which can provide an override of the repository dir.
      * for the libraries task
      * Value {@value}
      */
     public static final String REPOSITORY_DIR_PROPERTY = "ant.maven.repository.dir";
 
     /**
      * Name of the property which can provide an override of the repository URL.
      * for the libraries task
      * Value {@value}
      */
     public static final String REPOSITORY_URL_PROPERTY = "ant.maven.repository.url";
 
     /**
      * name of the resource that taskdefs are stored under.
      * Value: {@value}
      */
     public static final String TASKDEF_PROPERTIES_RESOURCE =
             "/org/apache/tools/ant/taskdefs/defaults.properties";
 
     /**
      * name of the resource that typedefs are stored under.
      * Value: {@value}
      */
     public static final String TYPEDEFS_PROPERTIES_RESOURCE =
             "/org/apache/tools/ant/types/defaults.properties";
 
     /**
      * Reference to the current Ant executor.
      * Value: {@value}
      */
     public static final String ANT_EXECUTOR_REFERENCE = "ant.executor";
 
     /**
      * Property defining the classname of an executor.
      * Value: {@value}
      */
     public static final String ANT_EXECUTOR_CLASSNAME = "ant.executor.class";
 
     /**
      * property name for basedir of the project.
      * Value: {@value}
      */
     public static final String PROJECT_BASEDIR = "basedir";
 
     /**
      * property for ant file name.
      * Value: {@value}
      */
     public static final String ANT_FILE = "ant.file";
 
     /**
      * property for type of ant build file (either file or url)
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String ANT_FILE_TYPE = "ant.file.type";
 
     /**
      * ant build file of type file
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String ANT_FILE_TYPE_FILE = "file";
 
     /**
      * ant build file of type url
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String ANT_FILE_TYPE_URL = "url";
 
     /**
      * Property used to store the java version ant is running in.
      * Value: {@value}
      * @since Ant 1.7
      */
     public static final String ANT_JAVA_VERSION = "ant.java.version";
 
     /**
      * Property used to store the location of ant.
      * Value: {@value}
      * @since Ant 1.7
      */
     public static final String ANT_HOME = Launcher.ANTHOME_PROPERTY;
 
     /**
      * Property used to store the location of the ant library (typically the ant.jar file.)
      * Value: {@value}
      * @since Ant 1.7
      */
     public static final String ANT_LIB = "ant.core.lib";
 
     /**
      * property for regular expression implementation.
      * Value: {@value}
      */
     public static final String REGEXP_IMPL = "ant.regexp.regexpimpl";
 
     /**
      * property that provides the default value for javac's and
      * javadoc's source attribute.
      * Value: {@value}
      * @since Ant 1.7
      */
     public static final String BUILD_JAVAC_SOURCE = "ant.build.javac.source";
 
     /**
      * property that provides the default value for javac's target attribute.
      * Value: {@value}
      * @since Ant 1.7
      */
     public static final String BUILD_JAVAC_TARGET = "ant.build.javac.target";
 
     /**
      * Name of the magic property that controls classloader reuse.
      * Value: {@value}
      * @since Ant 1.4.
      */
     public static final String REFID_CLASSPATH_REUSE_LOADER = "ant.reuse.loader";
 
     /**
      * Prefix used to store classloader references.
      * Value: {@value}
      */
     public static final String REFID_CLASSPATH_LOADER_PREFIX = "ant.loader.";
 
     /**
      * Reference used to store the property helper.
      * Value: {@value}
      */
     public static final String REFID_PROPERTY_HELPER = "ant.PropertyHelper";
 
     /**
      * Reference used to store the local properties.
      * Value: {@value}
      */
     public static final String REFID_LOCAL_PROPERTIES = "ant.LocalProperties";
 
     /**
      * Name of JVM system property which provides the name of the ProjectHelper class to use.
      * Value: {@value}
      */
     public static final String PROJECT_HELPER_CLASS = "org.apache.tools.ant.ProjectHelper";
 
     /**
      * The service identifier in jars which provide ProjectHelper implementations.
      * Value: {@value}
      */
     public static final String PROJECT_HELPER_SERVICE =
         "META-INF/services/org.apache.tools.ant.ProjectHelper";
 
     /**
      * Name of ProjectHelper reference that we add to a project.
      * Value: {@value}
      */
     public static final String REFID_PROJECT_HELPER = "ant.projectHelper";
 
     /**
      * Name of the property holding the name of the currently
      * executing project, if one has been specified.
      *
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String PROJECT_NAME = "ant.project.name";
 
     /**
      * Name of the property holding the default target of the
      * currently executing project, if one has been specified.
      *
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String PROJECT_DEFAULT_TARGET
         = "ant.project.default-target";
 
     /**
      * Name of the property holding a comma separated list of targets
      * that have been invoked (from the command line).
      *
      * Value: {@value}
      * @since Ant 1.8.0
      */
     public static final String PROJECT_INVOKED_TARGETS
         = "ant.project.invoked-targets";
 
     /**
      * Name of the project reference holding an instance of {@link
      * org.apache.tools.ant.taskdefs.launcher.CommandLauncher} to use
      * when executing commands with the help of an external skript.
      *
      * <p>Alternatively this is the name of a system property holding
      * the fully qualified class name of a {@link
      * org.apache.tools.ant.taskdefs.launcher.CommandLauncher}.</p>
      *
      * Value: {@value}
      * @since Ant 1.9.0
      */
     public static final String ANT_SHELL_LAUNCHER_REF_ID = "ant.shellLauncher";
 
     /**
      * Name of the project reference holding an instance of {@link
      * org.apache.tools.ant.taskdefs.launcher.CommandLauncher} to use
      * when executing commands without the help of an external skript.
      *
      * <p>Alternatively this is the name of a system property holding
      * the fully qualified class name of a {@link
      * org.apache.tools.ant.taskdefs.launcher.CommandLauncher}.</p>
      *
      * Value: {@value}
      * @since Ant 1.9.0
      */
     public static final String ANT_VM_LAUNCHER_REF_ID = "ant.vmLauncher";
+    /** Name of the namespace "type" (note: cannot be used as an element)
+     *  @since  Ant 1.9.1
+     * */
+    public static final String ATTRIBUTE_NAMESPACE = "attribute namespace";
 }
 
diff --git a/src/main/org/apache/tools/ant/ProjectHelper.java b/src/main/org/apache/tools/ant/ProjectHelper.java
index a3a88b889..b9df28cae 100644
--- a/src/main/org/apache/tools/ant/ProjectHelper.java
+++ b/src/main/org/apache/tools/ant/ProjectHelper.java
@@ -1,677 +1,692 @@
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
 import java.util.Hashtable;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Vector;
 
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.util.LoaderUtils;
 import org.xml.sax.AttributeList;
 
 /**
  * Configures a Project (complete with Targets and Tasks) based on
  * a build file. It'll rely on a plugin to do the actual processing
  * of the file.
  * <p>
  * This class also provide static wrappers for common introspection.
  */
 public class ProjectHelper {
     /** The URI for ant name space */
     public static final String ANT_CORE_URI    = "antlib:org.apache.tools.ant";
 
     /** The URI for antlib current definitions */
     public static final String ANT_CURRENT_URI      = "ant:current";
 
+    /** The URI for ant specific attributes
+     * @since Ant 1.9.1
+     * */
+    public static final String ANT_ATTRIBUTE_URI      = "ant:attribute";
+
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
      * Configures the project with the contents of the specified build file.
      *
      * @param project The project to configure. Must not be <code>null</code>.
      * @param buildFile A build file giving the project's configuration.
      *                  Must not be <code>null</code>.
      *
      * @exception BuildException if the configuration is invalid or cannot be read
      */
     public static void configureProject(Project project, File buildFile) throws BuildException {
         FileResource resource = new FileResource(buildFile);
         ProjectHelper helper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(resource);
         project.addReference(PROJECTHELPER_REFERENCE, helper);
         helper.parse(project, buildFile);
     }
 
     /**
      * Possible value for target's onMissingExtensionPoint attribute. It determines how to deal with
      * targets that want to extend missing extension-points.
      * <p>
      * This class behaves like a Java 1.5 Enum class.
      *
      * @since 1.8.2
      */
     public final static class OnMissingExtensionPoint {
 
         /** fail if the extension-point is not defined */
         public static final OnMissingExtensionPoint FAIL = new OnMissingExtensionPoint(
                 "fail");
 
         /** warn if the extension-point is not defined */
         public static final OnMissingExtensionPoint WARN = new OnMissingExtensionPoint(
                 "warn");
 
         /** ignore the extensionOf attribute if the extension-point is not defined */
         public static final OnMissingExtensionPoint IGNORE = new OnMissingExtensionPoint(
                 "ignore");
 
         private static final OnMissingExtensionPoint[] values = new OnMissingExtensionPoint[] {
                                 FAIL, WARN, IGNORE };
 
         private final String name;
 
         private OnMissingExtensionPoint(String name) {
             this.name = name;
         }
 
         public String name() {
             return name;
         }
 
         public String toString() {
             return name;
         }
 
         public static OnMissingExtensionPoint valueOf(String name) {
             if (name == null) {
                 throw new NullPointerException();
             }
             for (int i = 0; i < values.length; i++) {
                 if (name.equals(values[i].name())) {
                     return values[i];
                 }
             }
             throw new IllegalArgumentException(
                     "Unknown onMissingExtensionPoint " + name);
         }
     }
 
     /** Default constructor */
     public ProjectHelper() {
     }
 
     // -------------------- Common properties  --------------------
     // The following properties are required by import ( and other tasks
     // that read build files using ProjectHelper ).
 
     private Vector<Object> importStack = new Vector<Object>();
     private List<String[]> extensionStack = new LinkedList<String[]>();
 
     /**
      *  Import stack.
      *  Used to keep track of imported files. Error reporting should
      *  display the import path.
      *
      * @return the stack of import source objects.
      */
     public Vector<Object> getImportStack() {
         return importStack;
     }
 
     /**
      * Extension stack.
      * Used to keep track of targets that extend extension points.
      *
      * @return a list of three element string arrays where the first
      * element is the name of the extensionpoint, the second the name
      * of the target and the third the name of the enum like class
      * {@link OnMissingExtensionPoint}.
      */
     public List<String[]> getExtensionStack() {
         return extensionStack;
     }
 
     private final static ThreadLocal<String> targetPrefix = new ThreadLocal<String>();
 
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
         return targetPrefix.get();
     }
 
     /**
      * Sets the prefix to prepend to imported target names.
      *
      * @since Ant 1.8.0
      */
     public static void setCurrentTargetPrefix(String prefix) {
         targetPrefix.set(prefix);
     }
 
     private final static ThreadLocal<String> prefixSeparator = new ThreadLocal<String>() {
             protected String initialValue() {
                 return ".";
             }
         };
 
     /**
      * The separator between the prefix and the target name.
      *
      * <p>May be set by &lt;import&gt;'s prefixSeparator attribute.</p>
      *
      * @since Ant 1.8.0
      */
     public static String getCurrentPrefixSeparator() {
         return prefixSeparator.get();
     }
 
     /**
      * Sets the separator between the prefix and the target name.
      *
      * @since Ant 1.8.0
      */
     public static void setCurrentPrefixSeparator(String sep) {
         prefixSeparator.set(sep);
     }
 
     private final static ThreadLocal<Boolean> inIncludeMode = new ThreadLocal<Boolean>() {
             protected Boolean initialValue() {
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
         return Boolean.TRUE.equals(inIncludeMode.get());
     }
 
     /**
      * Sets whether the current file should be read in include as
      * opposed to import mode.
      *
      * @since Ant 1.8.0
      */
     public static void setInIncludeMode(boolean includeMode) {
         inIncludeMode.set(Boolean.valueOf(includeMode));
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
      * Get the first project helper found in the classpath
      *
      * @return an project helper, never <code>null</code>
      * @see org.apache.tools.ant.ProjectHelperRepository#getHelpers()
      */
     public static ProjectHelper getProjectHelper() {
         return (ProjectHelper) ProjectHelperRepository.getInstance().getHelpers().next();
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
                 ih.setAttribute(project, target, attrs.getName(i).toLowerCase(Locale.ENGLISH), value);
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
      * @param keys  Mapping (String to Object) of property names to their
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
      public static String replaceProperties(Project project, String value, Hashtable<String, Object> keys)
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
     public static void parsePropertyString(String value, Vector<String> fragments, Vector<String> propertyRefs)
             throws BuildException {
         PropertyHelper.parsePropertyStringDefault(value, fragments, propertyRefs);
     }
 
     /**
      * Map a namespaced {uri,name} to an internal string format.
      * For BC purposes the names from the ant core uri will be
      * mapped to "name", other names will be mapped to
      * uri + ":" + name.
      * @param uri   The namespace URI
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
+     * Convert an attribute namespace to a "component name".
+     * @param ns the xml namespace uri.
+     * @return the converted value.
+     * @since Ant 1.9.1
+     */
+    public static String nsToComponentName(String ns) {
+        return "attribute namespace:" + ns;
+    }
+
+    /**
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
     public boolean canParseAntlibDescriptor(Resource r) {
         return false;
     }
 
     /**
      * Parse the given URL as an antlib descriptor and return the
      * content as something that can be turned into an Antlib task.
      *
      * @since ant 1.8.0
      */
     public UnknownElement parseAntlibDescriptor(Project containingProject,
                                                 Resource source) {
         throw new BuildException("can't parse antlib descriptors");
     }
 
     /**
      * Check if the helper supports the kind of file. Some basic check on the
      * extension's file should be done here.
      *
      * @param buildFile
      *            the file expected to be parsed (never <code>null</code>)
      * @return true if the helper supports it
      * @since Ant 1.8.0
      */
     public boolean canParseBuildFile(Resource buildFile) {
         return true;
     }
 
     /**
      * The file name of the build script to be parsed if none specified on the command line
      *
      * @return the name of the default file (never <code>null</code>)
      * @since Ant 1.8.0
      */
     public String getDefaultBuildFile() {
         return Main.DEFAULT_BUILD_FILENAME;
     }
 
     /**
      * Check extensionStack and inject all targets having extensionOf attributes
      * into extensionPoint.
      * <p>
      * This method allow you to defer injection and have a powerful control of
      * extensionPoint wiring.
      * </p>
      * <p>
      * This should be invoked by each concrete implementation of ProjectHelper
      * when the root "buildfile" and all imported/included buildfile are loaded.
      * </p>
      *
      * @param project The project containing the target. Must not be
      *            <code>null</code>.
      * @exception BuildException if OnMissingExtensionPoint.FAIL and
      *                extensionPoint does not exist
      * @see OnMissingExtensionPoint
      * @since 1.9
      */
     public void resolveExtensionOfAttributes(Project project)
             throws BuildException {
         for (String[] extensionInfo : getExtensionStack()) {
             String extPointName = extensionInfo[0];
             String targetName = extensionInfo[1];
             OnMissingExtensionPoint missingBehaviour = OnMissingExtensionPoint.valueOf(extensionInfo[2]);
             // if the file has been included or imported, it may have a prefix
             // we should consider when trying to resolve the target it is
             // extending
             String prefixAndSep = extensionInfo.length > 3 ? extensionInfo[3] : null;
 
             // find the target we're extending
             Hashtable<String, Target> projectTargets = project.getTargets();
             Target extPoint = null;
             if (prefixAndSep == null) {
                 // no prefix - not from an imported/included build file
                 extPoint = projectTargets.get(extPointName);
             } else {
                 // we have a prefix, which means we came from an include/import
 
                 // FIXME: here we handle no particular level of include. We try
                 // the fully prefixed name, and then the non-prefixed name. But
                 // there might be intermediate project in the import stack,
                 // which prefix should be tested before testing the non-prefix
                 // root name.
 
                 extPoint = projectTargets.get(prefixAndSep + extPointName);
                 if (extPoint == null) {
                     extPoint = projectTargets.get(extPointName);
                 }
             }
 
             // make sure we found a point to extend on
             if (extPoint == null) {
                 String message = "can't add target " + targetName
                         + " to extension-point " + extPointName
                         + " because the extension-point is unknown.";
                 if (missingBehaviour == OnMissingExtensionPoint.FAIL) {
                     throw new BuildException(message);
                 } else if (missingBehaviour == OnMissingExtensionPoint.WARN) {
                     Target t = projectTargets.get(targetName);
                     project.log(t, "Warning: " + message, Project.MSG_WARN);
                 }
             } else {
                 if (!(extPoint instanceof ExtensionPoint)) {
                     throw new BuildException("referenced target " + extPointName
                             + " is not an extension-point");
                 }
                 extPoint.addDependency(targetName);
             }
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/RuntimeConfigurable.java b/src/main/org/apache/tools/ant/RuntimeConfigurable.java
index f8fb44b36..44e32edc2 100644
--- a/src/main/org/apache/tools/ant/RuntimeConfigurable.java
+++ b/src/main/org/apache/tools/ant/RuntimeConfigurable.java
@@ -1,494 +1,569 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map.Entry;
 
+import org.apache.tools.ant.attribute.EnableAttribute;
+
 import org.apache.tools.ant.util.CollectionUtils;
 import org.apache.tools.ant.taskdefs.MacroDef.Attribute;
 import org.apache.tools.ant.taskdefs.MacroInstance;
 import org.xml.sax.AttributeList;
 import org.xml.sax.helpers.AttributeListImpl;
 
 /**
  * Wrapper class that holds the attributes of an element, its children, and
  * any text within it. It then takes care of configuring that element at
  * runtime.
  */
 public class RuntimeConfigurable implements Serializable {
 
     /** Serialization version */
     private static final long serialVersionUID = 1L;
 
     /** Empty Hashtable. */
     private static final Hashtable<String, Object> EMPTY_HASHTABLE =
             new Hashtable<String, Object>(0);
 
     /** Name of the element to configure. */
     private String elementTag = null;
 
     /** List of child element wrappers. */
     private List<RuntimeConfigurable> children = null;
 
     /** The element to configure. It is only used during
      * maybeConfigure.
      */
     private transient Object wrappedObject = null;
 
     /**
      * XML attributes for the element.
      * @deprecated since 1.6.x
      */
     private transient AttributeList attributes;
 
+    // The following is set to true if any of the attributes are namespaced
+    private transient boolean namespacedAttribute = false;
+
     /** Attribute names and values. While the XML spec doesn't require
      *  preserving the order ( AFAIK ), some ant tests do rely on the
      *  exact order.
      * The only exception to this order is the treatment of
      * refid. A number of datatypes check if refid is set
      * when other attributes are set. This check will not
      * work if the build script has the other attribute before
      * the "refid" attribute, so now (ANT 1.7) the refid
      * attribute will be processed first.
      */
     private LinkedHashMap<String, Object> attributeMap = null;
 
     /** Text appearing within the element. */
     private StringBuffer characters = null;
 
     /** Indicates if the wrapped object has been configured */
     private boolean proxyConfigured = false;
 
     /** the polymorphic type */
     private String polyType = null;
 
     /** the "id" of this Element if it has one */
     private String id = null;
 
     /**
      * Sole constructor creating a wrapper for the specified object.
      *
      * @param proxy The element to configure. Must not be <code>null</code>.
      * @param elementTag The tag name generating this element.
      */
     public RuntimeConfigurable(Object proxy, String elementTag) {
         setProxy(proxy);
         setElementTag(elementTag);
         // Most likely an UnknownElement
         if (proxy instanceof Task) {
             ((Task) proxy).setRuntimeConfigurableWrapper(this);
         }
     }
 
     /**
      * Sets the element to configure.
      *
      * @param proxy The element to configure. Must not be <code>null</code>.
      */
     public synchronized void setProxy(Object proxy) {
         wrappedObject = proxy;
         proxyConfigured = false;
     }
 
+    private static class EnableAttributeConsumer {
+        public void add(EnableAttribute b) {
+            // Ignore
+        }
+    }
+
+    /**
+     * Check if an UE is enabled.
+     * This looks tru the attributes and checks if there
+     * are any Ant attributes, and if so, the method calls the
+     * isEnabled() method on them.
+     * @param owner the UE that owns this RC.
+     * @return true if enabled, false if any of the ant attribures return
+     *              false.
+     */
+    public boolean isEnabled(UnknownElement owner) {
+        if (!namespacedAttribute) {
+            return true;
+        }
+        ComponentHelper componentHelper = ComponentHelper
+            .getComponentHelper(owner.getProject());
+
+        IntrospectionHelper ih
+            = IntrospectionHelper.getHelper(
+                owner.getProject(), EnableAttributeConsumer.class);
+        for (int i = 0; i < attributeMap.keySet().size(); ++i) {
+            String name = (String) attributeMap.keySet().toArray()[i];
+            if (name.indexOf(':') == -1) {
+                continue;
+            }
+            String componentName = attrToComponent(name);
+            String ns = ProjectHelper.extractUriFromComponentName(componentName);
+            if (componentHelper.getRestrictedDefinitions(
+                    ProjectHelper.nsToComponentName(ns)) == null) {
+                continue;
+            }
+
+            String value = (String) attributeMap.get(name);
+
+            EnableAttribute enable = null;
+            try {
+                enable = (EnableAttribute)
+                    ih.createElement(
+                        owner.getProject(), new EnableAttributeConsumer(),
+                        componentName);
+            } catch (BuildException ex) {
+                throw new BuildException(
+                    "Unsupported attribute " + componentName);
+            }
+            if (enable == null) {
+                continue;
+            }
+            value = owner.getProject().replaceProperties(value); // FixMe: need to make config
+            if (!enable.isEnabled(owner, value)) {
+                return false;
+            }
+        }
+        return true;
+    }
+
+    private String attrToComponent(String a) {
+        // need to remove the prefix
+        int p1 = a.lastIndexOf(':');
+        int p2 = a.lastIndexOf(':', p1 - 1);
+        return a.substring(0, p2) + a.substring(p1);
+    }
+
     /**
      * Sets the creator of the element to be configured
      * used to store the element in the parent.
      *
      * @param creator the creator object.
      */
     synchronized void setCreator(IntrospectionHelper.Creator creator) {
     }
 
     /**
      * Get the object for which this RuntimeConfigurable holds the configuration
      * information.
      *
      * @return the object whose configure is held by this instance.
      */
     public synchronized Object getProxy() {
         return wrappedObject;
     }
 
     /**
      * Returns the id for this element.
      * @return the id.
      */
     public synchronized String getId() {
         return id;
     }
 
     /**
      * Get the polymorphic type for this element.
      * @return the ant component type name, null if not set.
      */
     public synchronized String getPolyType() {
         return polyType;
     }
 
     /**
      * Set the polymorphic type for this element.
      * @param polyType the ant component type name, null if not set.
      */
     public synchronized void setPolyType(String polyType) {
         this.polyType = polyType;
     }
 
     /**
      * Sets the attributes for the wrapped element.
      *
      * @deprecated since 1.6.x.
      * @param attributes List of attributes defined in the XML for this
      *                   element. May be <code>null</code>.
      */
     public synchronized void setAttributes(AttributeList attributes) {
         this.attributes = new AttributeListImpl(attributes);
         for (int i = 0; i < attributes.getLength(); i++) {
             setAttribute(attributes.getName(i), attributes.getValue(i));
         }
     }
 
     /**
      * Set an attribute to a given value.
      *
      * @param name the name of the attribute.
      * @param value the attribute's value.
      */
     public synchronized void setAttribute(String name, String value) {
+        if (name.indexOf(':') != -1) {
+            namespacedAttribute = true;
+        }
         setAttribute(name, (Object) value);
     }
 
     /**
      * Set an attribute to a given value.
      *
      * @param name the name of the attribute.
      * @param value the attribute's value.
      * @since 1.9
      */
     public synchronized void setAttribute(String name, Object value) {
         if (name.equalsIgnoreCase(ProjectHelper.ANT_TYPE)) {
             this.polyType = value == null ? null : value.toString();
         } else {
             if (attributeMap == null) {
                 attributeMap = new LinkedHashMap<String, Object>();
             }
             if (name.equalsIgnoreCase("refid") && !attributeMap.isEmpty()) {
                 LinkedHashMap<String, Object> newAttributeMap = new LinkedHashMap<String, Object>();
                 newAttributeMap.put(name, value);
                 newAttributeMap.putAll(attributeMap);
                 attributeMap = newAttributeMap;
             } else {
                 attributeMap.put(name, value);
             }
             if (name.equals("id")) {
                 this.id = value == null ? null : value.toString();
             }
         }
     }
 
     /**
      * Delete an attribute.  Not for the faint of heart.
      * @param name the name of the attribute to be removed.
      */
     public synchronized void removeAttribute(String name) {
         attributeMap.remove(name);
     }
 
     /**
      * Return the attribute map.
      *
      * @return Attribute name to attribute value map.
      * @since Ant 1.6
      */
     public synchronized Hashtable<String, Object> getAttributeMap() {
         return (attributeMap == null)
             ? EMPTY_HASHTABLE : new Hashtable<String, Object>(attributeMap);
     }
 
     /**
      * Returns the list of attributes for the wrapped element.
      *
      * @deprecated Deprecated since Ant 1.6 in favor of {@link #getAttributeMap}.
      * @return An AttributeList representing the attributes defined in the
      *         XML for this element. May be <code>null</code>.
      */
     public synchronized AttributeList getAttributes() {
         return attributes;
     }
 
     /**
      * Adds a child element to the wrapped element.
      *
      * @param child The child element wrapper to add to this one.
      *              Must not be <code>null</code>.
      */
     public synchronized void addChild(RuntimeConfigurable child) {
         children = (children == null) ? new ArrayList<RuntimeConfigurable>() : children;
         children.add(child);
     }
 
     /**
      * Returns the child wrapper at the specified position within the list.
      *
      * @param index The index of the child to return.
      *
      * @return The child wrapper at position <code>index</code> within the
      *         list.
      */
     synchronized RuntimeConfigurable getChild(int index) {
         return children.get(index);
     }
 
     /**
      * Returns an enumeration of all child wrappers.
      * @return an enumeration of the child wrappers.
      * @since Ant 1.6
      */
     public synchronized Enumeration<RuntimeConfigurable> getChildren() {
         return (children == null) ? new CollectionUtils.EmptyEnumeration<RuntimeConfigurable>()
             : Collections.enumeration(children);
     }
 
     /**
      * Adds characters from #PCDATA areas to the wrapped element.
      *
      * @param data Text to add to the wrapped element.
      *        Should not be <code>null</code>.
      */
     public synchronized void addText(String data) {
         if (data.length() == 0) {
             return;
         }
         characters = (characters == null)
             ? new StringBuffer(data) : characters.append(data);
     }
 
     /**
      * Adds characters from #PCDATA areas to the wrapped element.
      *
      * @param buf A character array of the text within the element.
      *            Must not be <code>null</code>.
      * @param start The start element in the array.
      * @param count The number of characters to read from the array.
      *
      */
     public synchronized void addText(char[] buf, int start, int count) {
         if (count == 0) {
             return;
         }
         characters = ((characters == null)
             ? new StringBuffer(count) : characters).append(buf, start, count);
     }
 
     /**
      * Get the text content of this element. Various text chunks are
      * concatenated, there is no way ( currently ) of keeping track of
      * multiple fragments.
      *
      * @return the text content of this element.
      * @since Ant 1.6
      */
     public synchronized StringBuffer getText() {
         return (characters == null) ? new StringBuffer(0) : characters;
     }
 
     /**
      * Set the element tag.
      * @param elementTag The tag name generating this element.
      */
     public synchronized void setElementTag(String elementTag) {
         this.elementTag = elementTag;
     }
 
     /**
      * Returns the tag name of the wrapped element.
      *
      * @return The tag name of the wrapped element. This is unlikely
      *         to be <code>null</code>, but may be.
      */
     public synchronized String getElementTag() {
         return elementTag;
     }
 
     /**
      * Configures the wrapped element and all its children.
      * The attributes and text for the wrapped element are configured,
      * and then each child is configured and added. Each time the
      * wrapper is configured, the attributes and text for it are
      * reset.
      *
      * If the element has an <code>id</code> attribute, a reference
      * is added to the project as well.
      *
      * @param p The project containing the wrapped element.
      *          Must not be <code>null</code>.
      *
      * @exception BuildException if the configuration fails, for instance due
      *            to invalid attributes or children, or text being added to
      *            an element which doesn't accept it.
      */
     public void maybeConfigure(Project p) throws BuildException {
         maybeConfigure(p, true);
     }
 
     /**
      * Configures the wrapped element.  The attributes and text for
      * the wrapped element are configured.  Each time the wrapper is
      * configured, the attributes and text for it are reset.
      *
      * If the element has an <code>id</code> attribute, a reference
      * is added to the project as well.
      *
      * @param p The project containing the wrapped element.
      *          Must not be <code>null</code>.
      *
      * @param configureChildren ignored.
 
      *
      * @exception BuildException if the configuration fails, for instance due
      *            to invalid attributes , or text being added to
      *            an element which doesn't accept it.
      */
     public synchronized void maybeConfigure(Project p, boolean configureChildren)
         throws BuildException {
 
         if (proxyConfigured) {
             return;
         }
 
         // Configure the object
         Object target = (wrappedObject instanceof TypeAdapter)
             ? ((TypeAdapter) wrappedObject).getProxy() : wrappedObject;
 
         IntrospectionHelper ih =
             IntrospectionHelper.getHelper(p, target.getClass());
 
         if (attributeMap != null) {
             for (Entry<String, Object> entry : attributeMap.entrySet()) {
                 String name = entry.getKey();
                 Object value = entry.getValue();
 
                 // reflect these into the target, defer for
                 // MacroInstance where properties are expanded for the
                 // nested sequential
                 Object attrValue;
                 if (value instanceof Evaluable) {
                     attrValue = ((Evaluable) value).eval();
                 } else {
                     attrValue = PropertyHelper.getPropertyHelper(p).parseProperties(value.toString());
                 }
                 if (target instanceof MacroInstance) {
                     for (Attribute attr : ((MacroInstance) target).getMacroDef().getAttributes()) {
                         if (attr.getName().equals(name)) {
                             if (!attr.isDoubleExpanding()) {
                                 attrValue = value;
                             }
                             break;
                         }
                     }
                 }
                 try {
                     ih.setAttribute(p, target, name, attrValue);
                 } catch (UnsupportedAttributeException be) {
                     // id attribute must be set externally
                     if (name.equals("id")) {
                         // Do nothing
                     } else if (getElementTag() == null) {
                         throw be;
                     } else {
                         throw new BuildException(
                             getElementTag() + " doesn't support the \""
                             + be.getAttribute() + "\" attribute", be);
                     }
                 } catch (BuildException be) {
                     if (name.equals("id")) {
                         // Assume that this is an not supported attribute type
                         // thrown for example by a dymanic attribute task
                         // Do nothing
                     } else {
                         throw be;
                     }
                 }
             }
         }
 
         if (characters != null) {
             ProjectHelper.addText(p, wrappedObject, characters.substring(0));
         }
 
         if (id != null) {
             p.addReference(id, wrappedObject);
         }
         proxyConfigured = true;
     }
 
     /**
      * Reconfigure the element, even if it has already been configured.
      *
      * @param p the project instance for this configuration.
      */
     public void reconfigure(Project p) {
         proxyConfigured = false;
         maybeConfigure(p);
     }
 
     /**
      * Apply presets, attributes and text are set if not currently set.
      * Nested elements are prepended.
      *
      * @param r a <code>RuntimeConfigurable</code> value.
      */
     public void applyPreSet(RuntimeConfigurable r) {
         // Attributes
         if (r.attributeMap != null) {
             for (String name : r.attributeMap.keySet()) {
                 if (attributeMap == null || attributeMap.get(name) == null) {
                     setAttribute(name, (String) r.attributeMap.get(name));
                 }
             }
         }
         // poly type
 
         polyType = (polyType == null) ? r.polyType : polyType;
 
         // Children (this is a shadow of UnknownElement#children)
         if (r.children != null) {
             List<RuntimeConfigurable> newChildren = new ArrayList<RuntimeConfigurable>();
             newChildren.addAll(r.children);
             if (children != null) {
                 newChildren.addAll(children);
             }
             children = newChildren;
         }
 
         // Text
         if (r.characters != null) {
             if (characters == null
                 || characters.toString().trim().length() == 0) {
                 characters = new StringBuffer(r.characters.toString());
             }
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/UnknownElement.java b/src/main/org/apache/tools/ant/UnknownElement.java
index c3df2e29f..fd919ab66 100644
--- a/src/main/org/apache/tools/ant/UnknownElement.java
+++ b/src/main/org/apache/tools/ant/UnknownElement.java
@@ -1,686 +1,698 @@
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
 
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.io.IOException;
 import org.apache.tools.ant.taskdefs.PreSetDef;
 
 /**
  * Wrapper class that holds all the information necessary to create a task
  * or data type that did not exist when Ant started, or one which
  * has had its definition updated to use a different implementation class.
  *
  */
 public class UnknownElement extends Task {
 
     /**
      * Holds the name of the task/type or nested child element of a
      * task/type that hasn't been defined at parser time or has
      * been redefined since original creation.
      */
     private final String elementName;
 
     /**
      * Holds the namespace of the element.
      */
     private String namespace = "";
 
     /**
      * Holds the namespace qname of the element.
      */
     private String qname;
 
     /**
      * The real object after it has been loaded.
      */
     private Object realThing;
 
     /**
      * List of child elements (UnknownElements).
      */
     private List<UnknownElement> children = null;
 
     /** Specifies if a predefined definition has been done */
     private boolean presetDefed = false;
 
     /**
      * Creates an UnknownElement for the given element name.
      *
      * @param elementName The name of the unknown element.
      *                    Must not be <code>null</code>.
      */
     public UnknownElement(String elementName) {
         this.elementName = elementName;
     }
 
     /**
      * @return the list of nested UnknownElements for this UnknownElement.
      */
     public List<UnknownElement> getChildren() {
         return children;
     }
 
     /**
      * Returns the name of the XML element which generated this unknown
      * element.
      *
      * @return the name of the XML element which generated this unknown
      *         element.
      */
     public String getTag() {
         return elementName;
     }
 
     /**
      * Return the namespace of the XML element associated with this component.
      *
      * @return Namespace URI used in the xmlns declaration.
      */
     public String getNamespace() {
         return namespace;
     }
 
     /**
      * Set the namespace of the XML element associated with this component.
      * This method is typically called by the XML processor.
      * If the namespace is "ant:current", the component helper
      * is used to get the current antlib uri.
      *
      * @param namespace URI used in the xmlns declaration.
      */
     public void setNamespace(String namespace) {
         if (namespace.equals(ProjectHelper.ANT_CURRENT_URI)) {
             ComponentHelper helper = ComponentHelper.getComponentHelper(
                 getProject());
             namespace = helper.getCurrentAntlibUri();
         }
         this.namespace = namespace == null ? "" : namespace;
     }
 
     /**
      * Return the qname of the XML element associated with this component.
      *
      * @return namespace Qname used in the element declaration.
      */
     public String getQName() {
         return qname;
     }
 
     /**
      * Set the namespace qname of the XML element.
      * This method is typically called by the XML processor.
      *
      * @param qname the qualified name of the element
      */
     public void setQName(String qname) {
         this.qname = qname;
     }
 
 
     /**
      * Get the RuntimeConfigurable instance for this UnknownElement, containing
      * the configuration information.
      *
      * @return the configuration info.
      */
     public RuntimeConfigurable getWrapper() {
         return super.getWrapper();
     }
 
     /**
      * Creates the real object instance and child elements, then configures
      * the attributes and text of the real object. This unknown element
      * is then replaced with the real object in the containing target's list
      * of children.
      *
      * @exception BuildException if the configuration fails
      */
     public void maybeConfigure() throws BuildException {
         if (realThing != null) {
             return;
         }
         configure(makeObject(this, getWrapper()));
     }
 
     /**
      * Configure the given object from this UnknownElement
      *
      * @param realObject the real object this UnknownElement is representing.
      *
      */
     public void configure(Object realObject) {
+        if (realObject == null) {
+            return;
+        }
         realThing = realObject;
 
         getWrapper().setProxy(realThing);
         Task task = null;
         if (realThing instanceof Task) {
             task = (Task) realThing;
 
             task.setRuntimeConfigurableWrapper(getWrapper());
 
             // For Script example that modifies id'ed tasks in other
             // targets to work. *very* Ugly
             // The reference is replaced by RuntimeConfigurable
             if (getWrapper().getId() != null) {
                 this.getOwningTarget().replaceChild(this, (Task) realThing);
             }
        }
 
 
         // configure attributes of the object and it's children. If it is
         // a task container, defer the configuration till the task container
         // attempts to use the task
 
         if (task != null) {
             task.maybeConfigure();
         } else {
             getWrapper().maybeConfigure(getProject());
         }
 
         handleChildren(realThing, getWrapper());
     }
 
     /**
      * Handles output sent to System.out by this task or its real task.
      *
      * @param output The output to log. Should not be <code>null</code>.
      */
     protected void handleOutput(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleOutput(output);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * Delegate to realThing if present and if it as task.
      * @see Task#handleInput(byte[], int, int)
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read.
      *
      * @return the number of bytes read.
      *
      * @exception IOException if the data cannot be read.
      * @since Ant 1.6
      */
     protected int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (realThing instanceof Task) {
             return ((Task) realThing).handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
 
     }
 
     /**
      * Handles output sent to System.out by this task or its real task.
      *
      * @param output The output to log. Should not be <code>null</code>.
      */
     protected void handleFlush(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleFlush(output);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Handles error output sent to System.err by this task or its real task.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      */
     protected void handleErrorOutput(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleErrorOutput(output);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
     /**
      * Handles error output sent to System.err by this task or its real task.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      */
     protected void handleErrorFlush(String output) {
         if (realThing instanceof Task) {
             ((Task) realThing).handleErrorFlush(output);
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     /**
      * Executes the real object if it's a task. If it's not a task
      * (e.g. a data type) then this method does nothing.
      */
     public void execute() {
         if (realThing == null) {
-            // plain impossible to get here, maybeConfigure should
-            // have thrown an exception.
-            throw new BuildException("Could not create task of type: "
-                                     + elementName, getLocation());
+            // Got here if the runtimeconfigurable is not enabled.
+            return;
         }
         try {
             if (realThing instanceof Task) {
                 ((Task) realThing).execute();
             }
         } finally {
             // Finished executing the task
             // null it (unless it has an ID) to allow
             // GC do its job
             // If this UE is used again, a new "realthing" will be made
             if (getWrapper().getId() == null) {
                 realThing = null;
                 getWrapper().setProxy(null);
             }
         }
     }
 
     /**
      * Adds a child element to this element.
      *
      * @param child The child element to add. Must not be <code>null</code>.
      */
     public void addChild(UnknownElement child) {
         if (children == null) {
             children = new ArrayList<UnknownElement>();
         }
         children.add(child);
     }
 
     /**
      * Creates child elements, creates children of the children
      * (recursively), and sets attributes of the child elements.
      *
      * @param parent The configured object for the parent.
      *               Must not be <code>null</code>.
      *
      * @param parentWrapper The wrapper containing child wrappers
      *                      to be configured. Must not be <code>null</code>
      *                      if there are any children.
      *
      * @exception BuildException if the children cannot be configured.
      */
     protected void handleChildren(
         Object parent,
         RuntimeConfigurable parentWrapper)
         throws BuildException {
         if (parent instanceof TypeAdapter) {
             parent = ((TypeAdapter) parent).getProxy();
         }
 
         String parentUri = getNamespace();
         Class<?> parentClass = parent.getClass();
         IntrospectionHelper ih = IntrospectionHelper.getHelper(getProject(), parentClass);
 
 
         if (children != null) {
             Iterator<UnknownElement> it = children.iterator();
             for (int i = 0; it.hasNext(); i++) {
                 RuntimeConfigurable childWrapper = parentWrapper.getChild(i);
                 UnknownElement child = it.next();
                 try {
+                    if (!childWrapper.isEnabled(child)) {
+                        if (ih.supportsNestedElement(
+                                parentUri, ProjectHelper.genComponentName(
+                                    child.getNamespace(), child.getTag()))) {
+                            continue;
+                        }
+                        // fall tru and fail in handlechild (unsupported element)
+                    }
                     if (!handleChild(
                             parentUri, ih, parent, child, childWrapper)) {
                         if (!(parent instanceof TaskContainer)) {
                             ih.throwNotSupported(getProject(), parent,
                                                  child.getTag());
                         } else {
                             // a task container - anything could happen - just add the
                             // child to the container
                             TaskContainer container = (TaskContainer) parent;
                             container.addTask(child);
                         }
                     }
                 } catch (UnsupportedElementException ex) {
                     throw new BuildException(
                         parentWrapper.getElementTag()
                         + " doesn't support the nested \"" + ex.getElement()
                         + "\" element.", ex);
                 }
             }
         }
     }
 
     /**
      * @return the component name - uses ProjectHelper#genComponentName()
      */
     protected String getComponentName() {
         return ProjectHelper.genComponentName(getNamespace(), getTag());
     }
 
     /**
      * This is used then the realobject of the UE is a PreSetDefinition.
      * This is also used when a presetdef is used on a presetdef
      * The attributes, elements and text are applied to this
      * UE.
      *
      * @param u an UnknownElement containing the attributes, elements and text
      */
     public void applyPreSet(UnknownElement u) {
         if (presetDefed) {
             return;
         }
         // Do the runtime
         getWrapper().applyPreSet(u.getWrapper());
         if (u.children != null) {
             List<UnknownElement> newChildren = new ArrayList<UnknownElement>();
             newChildren.addAll(u.children);
             if (children != null) {
                 newChildren.addAll(children);
             }
             children = newChildren;
         }
         presetDefed = true;
     }
 
     /**
      * Creates a named task or data type. If the real object is a task,
      * it is configured up to the init() stage.
      *
      * @param ue The unknown element to create the real object for.
      *           Must not be <code>null</code>.
      * @param w  Ignored in this implementation.
      *
      * @return the task or data type represented by the given unknown element.
      */
     protected Object makeObject(UnknownElement ue, RuntimeConfigurable w) {
+        if (!w.isEnabled(ue)) {
+            return null;
+        }
         ComponentHelper helper = ComponentHelper.getComponentHelper(
             getProject());
         String name = ue.getComponentName();
         Object o = helper.createComponent(ue, ue.getNamespace(), name);
         if (o == null) {
             throw getNotFoundException("task or type", name);
         }
         if (o instanceof PreSetDef.PreSetDefinition) {
             PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition) o;
             o = def.createObject(ue.getProject());
             if (o == null) {
                 throw getNotFoundException(
                     "preset " + name,
                     def.getPreSets().getComponentName());
             }
             ue.applyPreSet(def.getPreSets());
             if (o instanceof Task) {
                 Task task = (Task) o;
                 task.setTaskType(ue.getTaskType());
                 task.setTaskName(ue.getTaskName());
                 task.init();
             }
         }
         if (o instanceof UnknownElement) {
             o = ((UnknownElement) o).makeObject((UnknownElement) o, w);
         }
         if (o instanceof Task) {
             ((Task) o).setOwningTarget(getOwningTarget());
         }
         if (o instanceof ProjectComponent) {
             ((ProjectComponent) o).setLocation(getLocation());
         }
         return o;
     }
 
     /**
      * Creates a named task and configures it up to the init() stage.
      *
      * @param ue The UnknownElement to create the real task for.
      *           Must not be <code>null</code>.
      * @param w  Ignored.
      *
      * @return the task specified by the given unknown element, or
      *         <code>null</code> if the task name is not recognised.
      */
     protected Task makeTask(UnknownElement ue, RuntimeConfigurable w) {
         Task task = getProject().createTask(ue.getTag());
 
         if (task != null) {
             task.setLocation(getLocation());
             // UnknownElement always has an associated target
             task.setOwningTarget(getOwningTarget());
             task.init();
         }
         return task;
     }
 
     /**
      * Returns a very verbose exception for when a task/data type cannot
      * be found.
      *
      * @param what The kind of thing being created. For example, when
      *             a task name could not be found, this would be
      *             <code>"task"</code>. Should not be <code>null</code>.
      * @param name The name of the element which could not be found.
      *             Should not be <code>null</code>.
      *
      * @return a detailed description of what might have caused the problem.
      */
     protected BuildException getNotFoundException(String what,
                                                   String name) {
         ComponentHelper helper = ComponentHelper.getComponentHelper(getProject());
         String msg = helper.diagnoseCreationFailure(name, what);
         return new BuildException(msg, getLocation());
     }
 
     /**
      * Returns the name to use in logging messages.
      *
      * @return the name to use in logging messages.
      */
     public String getTaskName() {
         //return elementName;
         return realThing == null
             || !(realThing instanceof Task) ? super.getTaskName()
                                             : ((Task) realThing).getTaskName();
     }
 
     /**
      * Returns the task instance after it has been created and if it is a task.
      *
      * @return a task instance or <code>null</code> if the real object is not
      *         a task.
      */
     public Task getTask() {
         if (realThing instanceof Task) {
             return (Task) realThing;
         }
         return null;
     }
 
     /**
      * Return the configured object
      *
      * @return the real thing whatever it is
      *
      * @since ant 1.6
      */
     public Object getRealThing() {
         return realThing;
     }
 
     /**
      * Set the configured object
      * @param realThing the configured object
      * @since ant 1.7
      */
     public void setRealThing(Object realThing) {
         this.realThing = realThing;
     }
 
     /**
      * Try to create a nested element of <code>parent</code> for the
      * given tag.
      *
      * @return whether the creation has been successful
      */
     private boolean handleChild(
         String parentUri,
         IntrospectionHelper ih,
         Object parent, UnknownElement child,
         RuntimeConfigurable childWrapper) {
         String childName = ProjectHelper.genComponentName(
             child.getNamespace(), child.getTag());
         if (ih.supportsNestedElement(parentUri, childName, getProject(),
                                      parent)) {
             IntrospectionHelper.Creator creator = null;
             try {
                 creator = ih.getElementCreator(getProject(), parentUri,
                                                parent, childName, child);
             } catch (UnsupportedElementException use) {
                 if (!ih.isDynamic()) {
                     throw use;
                 }
                 // can't trust supportsNestedElement for dynamic elements
                 return false;
             }
             creator.setPolyType(childWrapper.getPolyType());
             Object realChild = creator.create();
             if (realChild instanceof PreSetDef.PreSetDefinition) {
                 PreSetDef.PreSetDefinition def =
                     (PreSetDef.PreSetDefinition) realChild;
                 realChild = creator.getRealObject();
                 child.applyPreSet(def.getPreSets());
             }
             childWrapper.setCreator(creator);
             childWrapper.setProxy(realChild);
             if (realChild instanceof Task) {
                 Task childTask = (Task) realChild;
                 childTask.setRuntimeConfigurableWrapper(childWrapper);
                 childTask.setTaskName(childName);
                 childTask.setTaskType(childName);
             }
             if (realChild instanceof ProjectComponent) {
                 ((ProjectComponent) realChild).setLocation(child.getLocation());
             }
             childWrapper.maybeConfigure(getProject());
             child.handleChildren(realChild, childWrapper);
             creator.store();
             return true;
         }
         return false;
     }
 
     /**
      * like contents equals, but ignores project
      * @param obj the object to check against
      * @return true if this unknownelement has the same contents the other
      */
     public boolean similar(Object obj) {
         if (obj == null) {
             return false;
         }
         if (!getClass().getName().equals(obj.getClass().getName())) {
             return false;
         }
         UnknownElement other = (UnknownElement) obj;
         // Are the names the same ?
         if (!equalsString(elementName, other.elementName)) {
             return false;
         }
         if (!namespace.equals(other.namespace)) {
             return false;
         }
         if (!qname.equals(other.qname)) {
             return false;
         }
         // Are attributes the same ?
         if (!getWrapper().getAttributeMap().equals(
                 other.getWrapper().getAttributeMap())) {
             return false;
         }
         // Is the text the same?
         //   Need to use equals on the string and not
         //   on the stringbuffer as equals on the string buffer
         //   does not compare the contents.
         if (!getWrapper().getText().toString().equals(
                 other.getWrapper().getText().toString())) {
             return false;
         }
         // Are the sub elements the same ?
         final int childrenSize = children == null ? 0 : children.size();
         if (childrenSize == 0) {
             return other.children == null || other.children.size() == 0;
         }
         if (other.children == null) {
             return false;
         }
         if (childrenSize != other.children.size()) {
             return false;
         }
         for (int i = 0; i < childrenSize; ++i) {
             UnknownElement child = (UnknownElement) children.get(i);
             if (!child.similar(other.children.get(i))) {
                 return false;
             }
         }
         return true;
     }
 
     private static boolean equalsString(String a, String b) {
         return (a == null) ? (b == null) : a.equals(b);
     }
 
     /**
      * Make a copy of the unknown element and set it in the new project.
      * @param newProject the project to create the UE in.
      * @return the copied UE.
      */
     public UnknownElement copy(Project newProject) {
         UnknownElement ret = new UnknownElement(getTag());
         ret.setNamespace(getNamespace());
         ret.setProject(newProject);
         ret.setQName(getQName());
         ret.setTaskType(getTaskType());
         ret.setTaskName(getTaskName());
         ret.setLocation(getLocation());
         if (getOwningTarget() == null) {
             Target t = new Target();
             t.setProject(getProject());
             ret.setOwningTarget(t);
         } else {
             ret.setOwningTarget(getOwningTarget());
         }
         RuntimeConfigurable copyRC = new RuntimeConfigurable(
             ret, getTaskName());
         copyRC.setPolyType(getWrapper().getPolyType());
         Map<String, Object> m = getWrapper().getAttributeMap();
         for (Map.Entry<String, Object> entry : m.entrySet()) {
             copyRC.setAttribute(entry.getKey(), (String) entry.getValue());
         }
         copyRC.addText(getWrapper().getText().toString());
 
         for (Enumeration<RuntimeConfigurable> e = getWrapper().getChildren(); e.hasMoreElements();) {
             RuntimeConfigurable r = e.nextElement();
             UnknownElement ueChild = (UnknownElement) r.getProxy();
             UnknownElement copyChild = ueChild.copy(newProject);
             copyRC.addChild(copyChild.getWrapper());
             ret.addChild(copyChild);
         }
         return ret;
     }
 }
diff --git a/src/main/org/apache/tools/ant/attribute/AttributeNamespace.java b/src/main/org/apache/tools/ant/attribute/AttributeNamespace.java
new file mode 100644
index 000000000..60cb62aca
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/AttributeNamespace.java
@@ -0,0 +1,26 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+/**
+ * This class is used to indicate that the xml namespace (uri)
+ * can be used to look for namespace attributes.
+ */
+public final class AttributeNamespace {
+}
diff --git a/src/main/org/apache/tools/ant/attribute/BaseIfAttribute.java b/src/main/org/apache/tools/ant/attribute/BaseIfAttribute.java
new file mode 100644
index 000000000..df119bf73
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/BaseIfAttribute.java
@@ -0,0 +1,85 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+
+import org.apache.tools.ant.ProjectComponent;
+import org.apache.tools.ant.RuntimeConfigurable;
+import org.apache.tools.ant.UnknownElement;
+
+
+/**
+ * An abstract class for if/unless attributes.
+ * This contains a boolean flag to specify whether this is an
+ * if or unless attribute.
+ */
+public abstract class BaseIfAttribute
+    extends ProjectComponent implements EnableAttribute {
+    private boolean positive = true;
+    /**
+     * Set the positive flag.
+     * @param positive the value to use.
+     */
+    protected void setPositive(boolean positive) {
+        this.positive = positive;
+    }
+
+    /**
+     * Get the positive flag.
+     * @return the flag.
+     */
+    protected boolean isPositive() {
+        return positive;
+    }
+
+    /**
+     * convert the result.
+     * @param val the result to convert
+     * @return val if positive or !val if not.
+     */
+    protected boolean convertResult(boolean val) {
+        return positive ? val : !val;
+    }
+
+    /**
+     * Get all the attributes in the ant-attribute:param
+     * namespace and place them in a map.
+     * @param el the element this attribute is in.
+     * @return a map of attributes.
+     */
+    protected Map getParams(UnknownElement el) {
+        Map ret = new HashMap();
+        RuntimeConfigurable rc = el.getWrapper();
+        Map attributes = rc.getAttributeMap(); // This does a copy!
+        for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
+            Map.Entry entry = (Map.Entry) i.next();
+            String key = (String) entry.getKey();
+            String value = (String) entry.getValue();
+            if (key.startsWith("ant-attribute:param")) {
+                int pos = key.lastIndexOf(':');
+                ret.put(key.substring(pos + 1),
+                        el.getProject().replaceProperties(value));
+            }
+        }
+        return ret;
+    }
+}
diff --git a/src/main/org/apache/tools/ant/attribute/EnableAttribute.java b/src/main/org/apache/tools/ant/attribute/EnableAttribute.java
new file mode 100644
index 000000000..d2f67fcb4
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/EnableAttribute.java
@@ -0,0 +1,34 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+import org.apache.tools.ant.UnknownElement;
+
+/**
+ * This interface is used by ant attributes.
+ */
+public interface EnableAttribute {
+    /**
+     * is enabled.
+     * @param el the unknown element this attribute is in.
+     * @param value the value of the attribute.
+     * @return true if the attribute enables the element, false otherwise.
+     */
+    boolean isEnabled(UnknownElement el, String value);
+}
diff --git a/src/main/org/apache/tools/ant/attribute/IfBlankAttribute.java b/src/main/org/apache/tools/ant/attribute/IfBlankAttribute.java
new file mode 100644
index 000000000..d0ff19c5a
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/IfBlankAttribute.java
@@ -0,0 +1,38 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+import org.apache.tools.ant.UnknownElement;
+
+/**
+ * Check if an attribute is blank or not.
+ */
+public class IfBlankAttribute extends BaseIfAttribute {
+    /** The unless version */
+    public static class Unless extends IfBlankAttribute {
+        { setPositive(false); }
+    }
+    /**
+     * check if the attribute value is blank or not
+     * {@inheritDoc}
+     */
+    public boolean isEnabled(UnknownElement el, String value) {
+        return convertResult((value == null || "".equals(value)));
+    }
+}
diff --git a/src/main/org/apache/tools/ant/attribute/IfSetAttribute.java b/src/main/org/apache/tools/ant/attribute/IfSetAttribute.java
new file mode 100644
index 000000000..3502793a0
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/IfSetAttribute.java
@@ -0,0 +1,38 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+import org.apache.tools.ant.UnknownElement;
+
+/**
+ * Check if an attribute value as a property is set or not
+ */
+public class IfSetAttribute extends BaseIfAttribute {
+    /** The unless version */
+    public static class Unless extends IfSetAttribute {
+        { setPositive(false); }
+    }
+    /**
+     * check if the attribute value is blank or not
+     * {@inheritDoc}
+     */
+    public boolean isEnabled(UnknownElement el, String value) {
+        return convertResult(getProject().getProperty(value) != null);
+    }
+}
diff --git a/src/main/org/apache/tools/ant/attribute/IfTrueAttribute.java b/src/main/org/apache/tools/ant/attribute/IfTrueAttribute.java
new file mode 100644
index 000000000..43bc8944c
--- /dev/null
+++ b/src/main/org/apache/tools/ant/attribute/IfTrueAttribute.java
@@ -0,0 +1,40 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.attribute;
+
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.UnknownElement;
+
+/**
+ * Check if an attribute value is true or not.
+ */
+public class IfTrueAttribute extends BaseIfAttribute {
+    /** The unless version */
+    public static class Unless extends IfTrueAttribute {
+        { setPositive(false); }
+    }
+
+    /**
+     * check if the attribute value is true or not
+     * {@inheritDoc}
+     */
+    public boolean isEnabled(UnknownElement el, String value) {
+        return convertResult(Project.toBoolean(value));
+    }
+}
diff --git a/src/main/org/apache/tools/ant/taskdefs/AttributeNamespaceDef.java b/src/main/org/apache/tools/ant/taskdefs/AttributeNamespaceDef.java
new file mode 100644
index 000000000..ce49d412f
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/AttributeNamespaceDef.java
@@ -0,0 +1,51 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.taskdefs;
+
+import org.apache.tools.ant.ProjectHelper;
+import org.apache.tools.ant.ComponentHelper;
+import org.apache.tools.ant.AntTypeDefinition;
+import org.apache.tools.ant.attribute.AttributeNamespace;
+
+/**
+ * Defintion to allow the uri to be considered for
+ * ant attributes.
+ *
+ * @since Ant 1.8.0
+ */
+public final class AttributeNamespaceDef  extends AntlibDefinition {
+
+    /**
+     * Run the definition.
+     * This registers the xml namespace (uri) as a namepace for
+     * attributes.
+     */
+    public void execute() {
+        String componentName = ProjectHelper.nsToComponentName(
+            getURI());
+        AntTypeDefinition def = new AntTypeDefinition();
+        def.setName(componentName);
+        def.setClassName(AttributeNamespace.class.getName());
+        def.setClass(AttributeNamespace.class);
+        def.setRestrict(true);
+        def.setClassLoader(AttributeNamespace.class.getClassLoader());
+        ComponentHelper.getComponentHelper(getProject())
+            .addDataTypeDefinition(def);
+    }
+}
diff --git a/src/main/org/apache/tools/ant/taskdefs/defaults.properties b/src/main/org/apache/tools/ant/taskdefs/defaults.properties
index 54c03ea19..9cf1499fe 100644
--- a/src/main/org/apache/tools/ant/taskdefs/defaults.properties
+++ b/src/main/org/apache/tools/ant/taskdefs/defaults.properties
@@ -1,209 +1,210 @@
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
+attributenamespacedef=org.apache.tools.ant.taskdefs.AttributeNamespaceDef
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
 commandlauncher=org.apache.tools.ant.taskdefs.CommandLauncherTask
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
diff --git a/src/tests/antunit/core/ant-attribute-test.xml b/src/tests/antunit/core/ant-attribute-test.xml
new file mode 100644
index 000000000..5d76c7a20
--- /dev/null
+++ b/src/tests/antunit/core/ant-attribute-test.xml
@@ -0,0 +1,60 @@
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
+<project name="ant-attribute-test" default="antunit"
+         xmlns:au="antlib:org.apache.ant.antunit"
+         xmlns:if="ant:if"
+         xmlns:unless="ant:unless"
+         >
+
+  <import file="../antunit-base.xml" />
+
+  <target name="test-if-set">
+    <echo message="message1" if:set="not-set"/>
+    <au:assertLogDoesntContain text="message1"/>
+  </target>
+
+  <target name="test-unless-set">
+    <echo message="message2" unless:set="not-set"/>
+    <au:assertLogContains text="message2"/>
+  </target>
+
+  <target name="test-if-true">
+    <property name="sample" value="true"/>
+    <echo message="message3" if:true="${sample}"/>
+    <au:assertLogContains text="message3"/>
+  </target>
+
+  <target name="test-if-true-false">
+    <property name="sample-1" value="false"/>
+    <echo message="message4" if:true="${sample-1}"/>
+    <au:assertLogDoesntContain text="message4"/>
+  </target>
+
+  <target name="test-unless-true-false">
+    <property name="sample-1" value="false"/>
+    <echo message="message5" unless:true="${sample-1}"/>
+    <au:assertLogContains text="message5"/>
+  </target>
+
+  <target name="test-if-blank">
+    <property name="sample-1" value=""/>
+    <echo message="message6" if:blank="${sample-1}"/>
+    <au:assertLogContains text="message6"/>
+  </target>
+
+</project>
