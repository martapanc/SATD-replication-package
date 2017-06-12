diff --git a/WHATSNEW b/WHATSNEW
index 138baa622..e157334c6 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1023 +1,1031 @@
 Changes from Ant 1.9.0 TO current
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
+ * Users who have their own ProjectHelper implementation will need to change it because the import and include tasks
+   will now default the targetPrefix to ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX.
+   Users using the default ProjectHelper2 with ant need not worry about this change done to fix Bugzilla Report 54940.
 
 
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
 
+ * Target rewriting for nested "include" only works when "as" is specified.
+   See also "Changes that could break older environments"
+   Bugzilla Report 54940.
+
+
 Other changes:
 --------------
 
  * strict attribute added to <signjar>.
    Bugzilla Report 54889.
 
  * simplifying Execute.getEnvironmentVariables since we are only running on Java 1.5 or higher now
 
  * Add conditional attributes
    Bugzilla report 43362
 
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
 
  * support for GNU Classpath.
    Bugzilla report 54760.
 
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
diff --git a/src/main/org/apache/tools/ant/ProjectHelper.java b/src/main/org/apache/tools/ant/ProjectHelper.java
index b9df28cae..fa54a9f24 100644
--- a/src/main/org/apache/tools/ant/ProjectHelper.java
+++ b/src/main/org/apache/tools/ant/ProjectHelper.java
@@ -1,692 +1,698 @@
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
 
     /** The URI for ant specific attributes
      * @since Ant 1.9.1
      * */
     public static final String ANT_ATTRIBUTE_URI      = "ant:attribute";
 
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
+     * constant to denote use project name as target prefix
+     * @since Ant 1.9.1
+     */
+    public static final String USE_PROJECT_NAME_AS_TARGET_PREFIX = "USE_PROJECT_NAME_AS_TARGET_PREFIX";
+
+    /**
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
      * Convert an attribute namespace to a "component name".
      * @param ns the xml namespace uri.
      * @return the converted value.
      * @since Ant 1.9.1
      */
     public static String nsToComponentName(String ns) {
         return "attribute namespace:" + ns;
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
diff --git a/src/main/org/apache/tools/ant/helper/ProjectHelper2.java b/src/main/org/apache/tools/ant/helper/ProjectHelper2.java
index 517f1ca5b..57936b7be 100644
--- a/src/main/org/apache/tools/ant/helper/ProjectHelper2.java
+++ b/src/main/org/apache/tools/ant/helper/ProjectHelper2.java
@@ -1,1237 +1,1235 @@
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
 package org.apache.tools.ant.helper;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.ExtensionPoint;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.RuntimeConfigurable;
 import org.apache.tools.ant.Target;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.UnknownElement;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.types.resources.URLProvider;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JAXPUtils;
 import org.apache.tools.zip.ZipFile;
 import org.xml.sax.Attributes;
 import org.xml.sax.InputSource;
 import org.xml.sax.Locator;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.DefaultHandler;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Stack;
 
 /**
  * Sax2 based project reader
  *
  */
 public class ProjectHelper2 extends ProjectHelper {
 
     /** Reference holding the (ordered) target Vector */
     public static final String REFID_TARGETS = "ant.targets";
 
     /* Stateless */
 
     // singletons - since all state is in the context
     private static AntHandler elementHandler = new ElementHandler();
     private static AntHandler targetHandler = new TargetHandler();
     private static AntHandler mainHandler = new MainHandler();
     private static AntHandler projectHandler = new ProjectHandler();
 
     /** Specific to ProjectHelper2 so not a true Ant "magic name:" */
     private static final String REFID_CONTEXT = "ant.parsing.context";
 
     /**
      * helper for path -> URI and URI -> path conversions.
      */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * Whether this instance of ProjectHelper can parse an Antlib
      * descriptor given by the URL and return its content as an
      * UnknownElement ready to be turned into an Antlib task.
      *
      * <p>This implementation returns true.</p>
      *
      * @since Ant 1.8.0
      */
     public boolean canParseAntlibDescriptor(Resource resource) {
         return true;
     }
 
     /**
      * Parse the given URL as an antlib descriptor and return the
      * content as something that can be turned into an Antlib task.
      *
      * <p>simply delegates to {@link #parseUnknownElement
      * parseUnknownElement} if the resource provides an URL and throws
      * an exception otherwise.</p>
      *
      * @since Ant 1.8.0
      */
     public UnknownElement parseAntlibDescriptor(Project containingProject,
                                                 Resource resource) {
         URLProvider up = resource.as(URLProvider.class);
         if (up == null) {
             throw new BuildException("Unsupported resource type: " + resource);
         }
         return parseUnknownElement(containingProject, up.getURL());
     }
 
     /**
      * Parse an unknown element from a url
      *
      * @param project the current project
      * @param source  the url containing the task
      * @return a configured task
      * @exception BuildException if an error occurs
      */
     public UnknownElement parseUnknownElement(Project project, URL source)
         throws BuildException {
         Target dummyTarget = new Target();
         dummyTarget.setProject(project);
 
         AntXMLContext context = new AntXMLContext(project);
         context.addTarget(dummyTarget);
         context.setImplicitTarget(dummyTarget);
 
         parse(context.getProject(), source, new RootHandler(context, elementHandler));
         Task[] tasks = dummyTarget.getTasks();
         if (tasks.length != 1) {
             throw new BuildException("No tasks defined");
         }
         return (UnknownElement) tasks[0];
     }
 
     /**
      * Parse a source xml input.
      *
      * @param project the current project
      * @param source  the xml source
      * @exception BuildException if an error occurs
      */
     public void parse(Project project, Object source) throws BuildException {
         getImportStack().addElement(source);
         AntXMLContext context = null;
         context = (AntXMLContext) project.getReference(REFID_CONTEXT);
         if (context == null) {
             context = new AntXMLContext(project);
             project.addReference(REFID_CONTEXT, context);
             project.addReference(REFID_TARGETS, context.getTargets());
         }
         if (getImportStack().size() > 1) {
             // we are in an imported file.
             context.setIgnoreProjectTag(true);
             Target currentTarget = context.getCurrentTarget();
             Target currentImplicit = context.getImplicitTarget();
             Map<String, Target>    currentTargets = context.getCurrentTargets();
             try {
                 Target newCurrent = new Target();
                 newCurrent.setProject(project);
                 newCurrent.setName("");
                 context.setCurrentTarget(newCurrent);
                 context.setCurrentTargets(new HashMap<String, Target>());
                 context.setImplicitTarget(newCurrent);
                 parse(project, source, new RootHandler(context, mainHandler));
                 newCurrent.execute();
             } finally {
                 context.setCurrentTarget(currentTarget);
                 context.setImplicitTarget(currentImplicit);
                 context.setCurrentTargets(currentTargets);
             }
         } else {
             // top level file
             context.setCurrentTargets(new HashMap<String, Target>());
             parse(project, source, new RootHandler(context, mainHandler));
             // Execute the top-level target
             context.getImplicitTarget().execute();
 
             // resolve extensionOf attributes
             resolveExtensionOfAttributes(project);
         }
     }
 
     /**
      * Parses the project file, configuring the project as it goes.
      *
      * @param project the current project
      * @param source  the xml source
      * @param handler the root handler to use (contains the current context)
      * @exception BuildException if the configuration is invalid or cannot
      *                           be read
      */
     public void parse(Project project, Object source, RootHandler handler) throws BuildException {
 
         AntXMLContext context = handler.context;
 
         File buildFile = null;
         URL  url = null;
         String buildFileName = null;
 
         if (source instanceof File) {
             buildFile = (File) source;
         } else if (source instanceof URL) {
             url = (URL) source;
         } else if (source instanceof Resource) {
             FileProvider fp =
                 ((Resource) source).as(FileProvider.class);
             if (fp != null) {
                 buildFile = fp.getFile();
             } else {
                 URLProvider up =
                     ((Resource) source).as(URLProvider.class);
                 if (up != null) {
                     url = up.getURL();
                 }
             }
         }
         if (buildFile != null) {
             buildFile = FILE_UTILS.normalize(buildFile.getAbsolutePath());
             context.setBuildFile(buildFile);
             buildFileName = buildFile.toString();
         } else if (url != null) {
             try {
                 context.setBuildFile((File) null);
                 context.setBuildFile(url);
             } catch (java.net.MalformedURLException ex) {
                 throw new BuildException(ex);
             }
             buildFileName = url.toString();
         } else {
             throw new BuildException("Source " + source.getClass().getName()
                                      + " not supported by this plugin");
         }
         InputStream inputStream = null;
         InputSource inputSource = null;
         ZipFile zf = null;
 
         try {
             /**
              * SAX 2 style parser used to parse the given file.
              */
             XMLReader parser = JAXPUtils.getNamespaceXMLReader();
 
             String uri = null;
             if (buildFile != null) {
                 uri = FILE_UTILS.toURI(buildFile.getAbsolutePath());
                 inputStream = new FileInputStream(buildFile);
             } else {
                 uri = url.toString();
                 int pling = -1;
                 if (uri.startsWith("jar:file")
                     && (pling = uri.indexOf("!/")) > -1) {
                     zf = new ZipFile(org.apache.tools.ant.launch.Locator
                                      .fromJarURI(uri), "UTF-8");
                     inputStream =
                         zf.getInputStream(zf.getEntry(uri.substring(pling + 1)));
                 } else {
                     inputStream = url.openStream();
                 }
             }
 
             inputSource = new InputSource(inputStream);
             if (uri != null) {
                 inputSource.setSystemId(uri);
             }
             project.log("parsing buildfile " + buildFileName + " with URI = "
                         + uri + (zf != null ? " from a zip file" : ""),
                         Project.MSG_VERBOSE);
 
             DefaultHandler hb = handler;
 
             parser.setContentHandler(hb);
             parser.setEntityResolver(hb);
             parser.setErrorHandler(hb);
             parser.setDTDHandler(hb);
             parser.parse(inputSource);
         } catch (SAXParseException exc) {
             Location location = new Location(exc.getSystemId(), exc.getLineNumber(), exc
                                              .getColumnNumber());
 
             Throwable t = exc.getException();
             if (t instanceof BuildException) {
                 BuildException be = (BuildException) t;
                 if (be.getLocation() == Location.UNKNOWN_LOCATION) {
                     be.setLocation(location);
                 }
                 throw be;
             }
             throw new BuildException(exc.getMessage(), t == null ? exc : t, location);
         } catch (SAXException exc) {
             Throwable t = exc.getException();
             if (t instanceof BuildException) {
                 throw (BuildException) t;
             }
             throw new BuildException(exc.getMessage(), t == null ? exc : t);
         } catch (FileNotFoundException exc) {
             throw new BuildException(exc);
         } catch (UnsupportedEncodingException exc) {
             throw new BuildException("Encoding of project file " + buildFileName + " is invalid.",
                                      exc);
         } catch (IOException exc) {
             throw new BuildException("Error reading project file " + buildFileName + ": "
                                      + exc.getMessage(), exc);
         } finally {
             FileUtils.close(inputStream);
             ZipFile.closeQuietly(zf);
         }
     }
 
     /**
      * Returns main handler
      * @return main handler
      */
     protected static AntHandler getMainHandler() {
         return mainHandler;
     }
 
     /**
      * Sets main handler
      * @param handler  new main handler
      */
     protected static void setMainHandler(AntHandler handler) {
         mainHandler = handler;
     }
 
     /**
      * Returns project handler
      * @return project handler
      */
     protected static AntHandler getProjectHandler() {
         return projectHandler;
     }
 
     /**
      * Sets project handler
      * @param handler  new project handler
      */
     protected static void setProjectHandler(AntHandler handler) {
         projectHandler = handler;
     }
 
     /**
      * Returns target handler
      * @return target handler
      */
     protected static AntHandler getTargetHandler() {
         return targetHandler;
     }
 
     /**
      * Sets target handler
      * @param handler  new target handler
      */
     protected static void setTargetHandler(AntHandler handler) {
         targetHandler = handler;
     }
 
     /**
      * Returns element handler
      * @return element handler
      */
     protected static AntHandler getElementHandler() {
         return elementHandler;
     }
 
     /**
      * Sets element handler
      * @param handler  new element handler
      */
     protected static void setElementHandler(AntHandler handler) {
         elementHandler = handler;
     }
 
     /**
      * The common superclass for all SAX event handlers used to parse
      * the configuration file.
      *
      * The context will hold all state information. At each time
      * there is one active handler for the current element. It can
      * use onStartChild() to set an alternate handler for the child.
      */
     public static class AntHandler  {
         /**
          * Handles the start of an element. This base implementation does
          * nothing.
          *
          * @param uri the namespace URI for the tag
          * @param tag The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name of the element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          * @param context The context that this element is in.
          *
          * @exception SAXParseException if this method is not overridden, or in
          *                              case of error in an overridden version
          */
         public void onStartElement(String uri, String tag, String qname, Attributes attrs,
                                    AntXMLContext context) throws SAXParseException {
         }
 
         /**
          * Handles the start of an element. This base implementation just
          * throws an exception - you must override this method if you expect
          * child elements.
          *
          * @param uri The namespace uri for this element.
          * @param tag The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          * @param context The current context.
          * @return a handler (in the derived classes)
          *
          * @exception SAXParseException if this method is not overridden, or in
          *                              case of error in an overridden version
          */
         public AntHandler onStartChild(String uri, String tag, String qname, Attributes attrs,
                                        AntXMLContext context) throws SAXParseException {
             throw new SAXParseException("Unexpected element \"" + qname + " \"", context
                                         .getLocator());
         }
 
         /**
          * Handle the end of a element.
          *
          * @param uri the namespace uri of the element
          * @param tag the tag of the element
          * @param qname the qualified name of the element
          * @param context the current context
          * @exception SAXParseException if an error occurs
          */
         public void onEndChild(String uri, String tag, String qname, AntXMLContext context)
             throws SAXParseException {
         }
 
         /**
          * This method is called when this element and all elements nested into it have been
          * handled. I.e., this happens at the &lt;/end_tag_of_the_element&gt;.
          * @param uri the namespace uri for this element
          * @param tag the element name
          * @param context the current context
          */
         public void onEndElement(String uri, String tag, AntXMLContext context) {
         }
 
         /**
          * Handles text within an element. This base implementation just
          * throws an exception, you must override it if you expect content.
          *
          * @param buf A character array of the text within the element.
          *            Will not be <code>null</code>.
          * @param start The start element in the array.
          * @param count The number of characters to read from the array.
          * @param context The current context.
          *
          * @exception SAXParseException if this method is not overridden, or in
          *                              case of error in an overridden version
          */
         public void characters(char[] buf, int start, int count, AntXMLContext context)
             throws SAXParseException {
             String s = new String(buf, start, count).trim();
 
             if (s.length() > 0) {
                 throw new SAXParseException("Unexpected text \"" + s + "\"", context.getLocator());
             }
         }
 
         /**
          * Will be called every time a namespace is reached.
          * It'll verify if the ns was processed, and if not load the task definitions.
          * @param uri The namespace uri.
          */
         protected void checkNamespace(String uri) {
         }
     }
 
     /**
      * Handler for ant processing. Uses a stack of AntHandlers to
      * implement each element ( the original parser used a recursive behavior,
      * with the implicit execution stack )
      */
     public static class RootHandler extends DefaultHandler {
         private Stack<AntHandler> antHandlers = new Stack<AntHandler>();
         private AntHandler currentHandler = null;
         private AntXMLContext context;
 
         /**
          * Creates a new RootHandler instance.
          *
          * @param context The context for the handler.
          * @param rootHandler The handler for the root element.
          */
         public RootHandler(AntXMLContext context, AntHandler rootHandler) {
             currentHandler = rootHandler;
             antHandlers.push(currentHandler);
             this.context = context;
         }
 
         /**
          * Returns the current ant handler object.
          * @return the current ant handler.
          */
         public AntHandler getCurrentAntHandler() {
             return currentHandler;
         }
 
         /**
          * Resolves file: URIs relative to the build file.
          *
          * @param publicId The public identifier, or <code>null</code>
          *                 if none is available. Ignored in this
          *                 implementation.
          * @param systemId The system identifier provided in the XML
          *                 document. Will not be <code>null</code>.
          * @return an inputsource for this identifier
          */
         public InputSource resolveEntity(String publicId, String systemId) {
 
             context.getProject().log("resolving systemId: " + systemId, Project.MSG_VERBOSE);
 
             if (systemId.startsWith("file:")) {
                 String path = FILE_UTILS.fromURI(systemId);
 
                 File file = new File(path);
                 if (!file.isAbsolute()) {
                     file = FILE_UTILS.resolveFile(context.getBuildFileParent(), path);
                     context.getProject().log(
                                              "Warning: '" + systemId + "' in " + context.getBuildFile()
                                              + " should be expressed simply as '" + path.replace('\\', '/')
                                              + "' for compliance with other XML tools", Project.MSG_WARN);
                 }
                 context.getProject().log("file=" + file, Project.MSG_DEBUG);
                 try {
                     InputSource inputSource = new InputSource(new FileInputStream(file));
                     inputSource.setSystemId(FILE_UTILS.toURI(file.getAbsolutePath()));
                     return inputSource;
                 } catch (FileNotFoundException fne) {
                     context.getProject().log(file.getAbsolutePath() + " could not be found",
                                              Project.MSG_WARN);
                 }
 
             }
             // use default if not file or file not found
             context.getProject().log("could not resolve systemId", Project.MSG_DEBUG);
             return null;
         }
 
         /**
          * Handles the start of a project element. A project handler is created
          * and initialised with the element name and attributes.
          *
          * @param uri The namespace uri for this element.
          * @param tag The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          *
          * @exception org.xml.sax.SAXParseException if the tag given is not
          *                              <code>"project"</code>
          */
         public void startElement(String uri, String tag, String qname, Attributes attrs)
             throws SAXParseException {
             AntHandler next = currentHandler.onStartChild(uri, tag, qname, attrs, context);
             antHandlers.push(currentHandler);
             currentHandler = next;
             currentHandler.onStartElement(uri, tag, qname, attrs, context);
         }
 
         /**
          * Sets the locator in the project helper for future reference.
          *
          * @param locator The locator used by the parser.
          *                Will not be <code>null</code>.
          */
         public void setDocumentLocator(Locator locator) {
             context.setLocator(locator);
         }
 
         /**
          * Handles the end of an element. Any required clean-up is performed
          * by the onEndElement() method and then the original handler is restored to the parser.
          *
          * @param uri  The namespace URI for this element.
          * @param name The name of the element which is ending.
          *             Will not be <code>null</code>.
          * @param qName The qualified name for this element.
          *
          * @exception SAXException in case of error (not thrown in this implementation)
          */
         public void endElement(String uri, String name, String qName) throws SAXException {
             currentHandler.onEndElement(uri, name, context);
             AntHandler prev = (AntHandler) antHandlers.pop();
             currentHandler = prev;
             if (currentHandler != null) {
                 currentHandler.onEndChild(uri, name, qName, context);
             }
         }
 
         /**
          * Handle text within an element, calls currentHandler.characters.
          *
          * @param buf  A character array of the test.
          * @param start The start offset in the array.
          * @param count The number of characters to read.
          * @exception SAXParseException if an error occurs
          */
         public void characters(char[] buf, int start, int count) throws SAXParseException {
             currentHandler.characters(buf, start, count, context);
         }
 
         /**
          * Start a namespace prefix to uri mapping
          *
          * @param prefix the namespace prefix
          * @param uri the namespace uri
          */
         public void startPrefixMapping(String prefix, String uri) {
             context.startPrefixMapping(prefix, uri);
         }
 
         /**
          * End a namespace prefix to uri mapping
          *
          * @param prefix the prefix that is not mapped anymore
          */
         public void endPrefixMapping(String prefix) {
             context.endPrefixMapping(prefix);
         }
     }
 
     /**
      * The main handler - it handles the &lt;project&gt; tag.
      *
      * @see org.apache.tools.ant.helper.ProjectHelper2.AntHandler
      */
     public static class MainHandler extends AntHandler {
 
         /**
          * Handle the project tag
          *
          * @param uri The namespace uri.
          * @param name The element tag.
          * @param qname The element qualified name.
          * @param attrs The attributes of the element.
          * @param context The current context.
          * @return The project handler that handles subelements of project
          * @exception SAXParseException if the qualified name is not "project".
          */
         public AntHandler onStartChild(String uri, String name, String qname, Attributes attrs,
                                        AntXMLContext context) throws SAXParseException {
             if (name.equals("project")
                 && (uri.equals("") || uri.equals(ANT_CORE_URI))) {
                 return ProjectHelper2.projectHandler;
             }
             if (name.equals(qname)) {
                 throw new SAXParseException("Unexpected element \"{" + uri
                                             + "}" + name + "\" {" + ANT_CORE_URI + "}" + name, context.getLocator());
             }
             throw new SAXParseException("Unexpected element \"" + qname
                                         + "\" " + name, context.getLocator());
         }
     }
 
     /**
      * Handler for the top level "project" element.
      */
     public static class ProjectHandler extends AntHandler {
 
         /**
          * Initialisation routine called after handler creation
          * with the element name and attributes. The attributes which
          * this handler can deal with are: <code>"default"</code>,
          * <code>"name"</code>, <code>"id"</code> and <code>"basedir"</code>.
          *
          * @param uri The namespace URI for this element.
          * @param tag Name of the element which caused this handler
          *            to be created. Should not be <code>null</code>.
          *            Ignored in this implementation.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element which caused this
          *              handler to be created. Must not be <code>null</code>.
          * @param context The current context.
          *
          * @exception SAXParseException if an unexpected attribute is
          *            encountered or if the <code>"default"</code> attribute
          *            is missing.
          */
         public void onStartElement(String uri, String tag, String qname, Attributes attrs,
                                    AntXMLContext context) throws SAXParseException {
             String baseDir = null;
             boolean nameAttributeSet = false;
 
             Project project = context.getProject();
             // Set the location of the implicit target associated with the project tag
             context.getImplicitTarget().setLocation(new Location(context.getLocator()));
 
             /** XXX I really don't like this - the XML processor is still
              * too 'involved' in the processing. A better solution (IMO)
              * would be to create UE for Project and Target too, and
              * then process the tree and have Project/Target deal with
              * its attributes ( similar with Description ).
              *
              * If we eventually switch to ( or add support for ) DOM,
              * things will work smoothly - UE can be avoided almost completely
              * ( it could still be created on demand, for backward compatibility )
              */
 
             for (int i = 0; i < attrs.getLength(); i++) {
                 String attrUri = attrs.getURI(i);
                 if (attrUri != null && !attrUri.equals("") && !attrUri.equals(uri)) {
                     continue; // Ignore attributes from unknown uris
                 }
                 String key = attrs.getLocalName(i);
                 String value = attrs.getValue(i);
 
                 if (key.equals("default")) {
                     if (value != null && !value.equals("")) {
                         if (!context.isIgnoringProjectTag()) {
                             project.setDefault(value);
                         }
                     }
                 } else if (key.equals("name")) {
                     if (value != null) {
                         context.setCurrentProjectName(value);
                         nameAttributeSet = true;
                         if (!context.isIgnoringProjectTag()) {
                             project.setName(value);
                             project.addReference(value, project);
                         } else if (isInIncludeMode()) {
-                            if (!"".equals(value)
-                                && (getCurrentTargetPrefix() == null
-                                    || getCurrentTargetPrefix().length() == 0)
-                                ) {
+                            if (!"".equals(value) && getCurrentTargetPrefix()!= null && getCurrentTargetPrefix().endsWith(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX))  {
+                                String newTargetPrefix = getCurrentTargetPrefix().replace(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX, value);
                                 // help nested include tasks
-                                setCurrentTargetPrefix(value);
+                                setCurrentTargetPrefix(newTargetPrefix);
                             }
                         }
                     }
                 } else if (key.equals("id")) {
                     if (value != null) {
                         // What's the difference between id and name ?
                         if (!context.isIgnoringProjectTag()) {
                             project.addReference(value, project);
                         }
                     }
                 } else if (key.equals("basedir")) {
                     if (!context.isIgnoringProjectTag()) {
                         baseDir = value;
                     }
                 } else {
                     // XXX ignore attributes in a different NS ( maybe store them ? )
                     throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i)
                                                 + "\"", context.getLocator());
                 }
             }
 
             // XXX Move to Project ( so it is shared by all helpers )
             String antFileProp =
                 MagicNames.ANT_FILE + "." + context.getCurrentProjectName();
             String dup = project.getProperty(antFileProp);
             String typeProp =
                 MagicNames.ANT_FILE_TYPE + "." + context.getCurrentProjectName();
             String dupType = project.getProperty(typeProp);
             if (dup != null && nameAttributeSet) {
                 Object dupFile = null;
                 Object contextFile = null;
                 if (MagicNames.ANT_FILE_TYPE_URL.equals(dupType)) {
                     try {
                         dupFile = new URL(dup);
                     } catch (java.net.MalformedURLException mue) {
                         throw new BuildException("failed to parse "
                                                  + dup + " as URL while looking"
                                                  + " at a duplicate project"
                                                  + " name.", mue);
                     }
                     contextFile = context.getBuildFileURL();
                 } else {
                     dupFile = new File(dup);
                     contextFile = context.getBuildFile();
                 }
 
                 if (context.isIgnoringProjectTag() && !dupFile.equals(contextFile)) {
                     project.log("Duplicated project name in import. Project "
                                 + context.getCurrentProjectName() + " defined first in " + dup
                                 + " and again in " + contextFile, Project.MSG_WARN);
                 }
             }
             if (nameAttributeSet) {
                 if (context.getBuildFile() != null) {
                     project.setUserProperty(antFileProp,
                                             context.getBuildFile().toString());
                     project.setUserProperty(typeProp,
                                             MagicNames.ANT_FILE_TYPE_FILE);
                 } else if (context.getBuildFileURL() != null) {
                     project.setUserProperty(antFileProp,
                                             context.getBuildFileURL().toString());
                     project.setUserProperty(typeProp,
                                             MagicNames.ANT_FILE_TYPE_URL);
                 }
             }
             if (context.isIgnoringProjectTag()) {
                 // no further processing
                 return;
             }
             // set explicitly before starting ?
             if (project.getProperty("basedir") != null) {
                 project.setBasedir(project.getProperty("basedir"));
             } else {
                 // Default for baseDir is the location of the build file.
                 if (baseDir == null) {
                     project.setBasedir(context.getBuildFileParent().getAbsolutePath());
                 } else {
                     // check whether the user has specified an absolute path
                     if ((new File(baseDir)).isAbsolute()) {
                         project.setBasedir(baseDir);
                     } else {
                         project.setBaseDir(FILE_UTILS.resolveFile(context.getBuildFileParent(),
                                                                   baseDir));
                     }
                 }
             }
             project.addTarget("", context.getImplicitTarget());
             context.setCurrentTarget(context.getImplicitTarget());
         }
 
         /**
          * Handles the start of a top-level element within the project. An
          * appropriate handler is created and initialised with the details
          * of the element.
          *
          * @param uri The namespace URI for this element.
          * @param name The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          * @param context The context for this element.
          * @return a target or an element handler.
          *
          * @exception org.xml.sax.SAXParseException if the tag given is not
          *            <code>"taskdef"</code>, <code>"typedef"</code>,
          *            <code>"property"</code>, <code>"target"</code>,
          *            <code>"extension-point"</code>
          *            or a data type definition
          */
         public AntHandler onStartChild(String uri, String name, String qname, Attributes attrs,
                                        AntXMLContext context) throws SAXParseException {
             return (name.equals("target") || name.equals("extension-point"))
                 && (uri.equals("") || uri.equals(ANT_CORE_URI))
                 ? ProjectHelper2.targetHandler : ProjectHelper2.elementHandler;
         }
     }
 
     /**
      * Handler for "target" and "extension-point" elements.
      */
     public static class TargetHandler extends AntHandler {
 
         /**
          * Initialisation routine called after handler creation
          * with the element name and attributes. The attributes which
          * this handler can deal with are: <code>"name"</code>,
          * <code>"depends"</code>, <code>"if"</code>,
          * <code>"unless"</code>, <code>"id"</code> and
          * <code>"description"</code>.
          *
          * @param uri The namespace URI for this element.
          * @param tag Name of the element which caused this handler
          *            to be created. Should not be <code>null</code>.
          *            Ignored in this implementation.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element which caused this
          *              handler to be created. Must not be <code>null</code>.
          * @param context The current context.
          *
          * @exception SAXParseException if an unexpected attribute is encountered
          *            or if the <code>"name"</code> attribute is missing.
          */
         public void onStartElement(String uri, String tag, String qname, Attributes attrs,
                                    AntXMLContext context) throws SAXParseException {
             String name = null;
             String depends = "";
             String extensionPoint = null;
             OnMissingExtensionPoint extensionPointMissing = null;
 
             Project project = context.getProject();
             Target target = "target".equals(tag)
                 ? new Target() : new ExtensionPoint();
             target.setProject(project);
             target.setLocation(new Location(context.getLocator()));
             context.addTarget(target);
 
             for (int i = 0; i < attrs.getLength(); i++) {
                 String attrUri = attrs.getURI(i);
                 if (attrUri != null && !attrUri.equals("") && !attrUri.equals(uri)) {
                     continue; // Ignore attributes from unknown uris
                 }
                 String key = attrs.getLocalName(i);
                 String value = attrs.getValue(i);
 
                 if (key.equals("name")) {
                     name = value;
                     if ("".equals(name)) {
                         throw new BuildException("name attribute must " + "not be empty");
                     }
                 } else if (key.equals("depends")) {
                     depends = value;
                 } else if (key.equals("if")) {
                     target.setIf(value);
                 } else if (key.equals("unless")) {
                     target.setUnless(value);
                 } else if (key.equals("id")) {
                     if (value != null && !value.equals("")) {
                         context.getProject().addReference(value, target);
                     }
                 } else if (key.equals("description")) {
                     target.setDescription(value);
                 } else if (key.equals("extensionOf")) {
                     extensionPoint = value;
                 } else if (key.equals("onMissingExtensionPoint")) {
                     try {
                         extensionPointMissing = OnMissingExtensionPoint.valueOf(value);
                     } catch (IllegalArgumentException e) {
                         throw new BuildException("Invalid onMissingExtensionPoint " + value);
                     }
                 } else {
                     throw new SAXParseException("Unexpected attribute \"" + key + "\"", context
                                                 .getLocator());
                 }
             }
 
             if (name == null) {
                 throw new SAXParseException("target element appears without a name attribute",
                                             context.getLocator());
             }
 
             String prefix = null;
             boolean isInIncludeMode =
                 context.isIgnoringProjectTag() && isInIncludeMode();
             String sep = getCurrentPrefixSeparator();
 
             if (isInIncludeMode) {
                 prefix = getTargetPrefix(context);
                 if (prefix == null) {
                     throw new BuildException("can't include build file "
                                              + context.getBuildFileURL()
                                              + ", no as attribute has been given"
                                              + " and the project tag doesn't"
                                              + " specify a name attribute");
                 }
                 name = prefix + sep + name;
             }
 
             // Check if this target is in the current build file
             if (context.getCurrentTargets().get(name) != null) {
                 throw new BuildException("Duplicate target '" + name + "'",
                                          target.getLocation());
             }
             Hashtable<String, Target> projectTargets = project.getTargets();
             boolean   usedTarget = false;
             // If the name has not already been defined define it
             if (projectTargets.containsKey(name)) {
                 project.log("Already defined in main or a previous import, ignore " + name,
                             Project.MSG_VERBOSE);
             } else {
                 target.setName(name);
                 context.getCurrentTargets().put(name, target);
                 project.addOrReplaceTarget(name, target);
                 usedTarget = true;
             }
 
             if (depends.length() > 0) {
                 if (!isInIncludeMode) {
                     target.setDepends(depends);
                 } else {
                     for (String string : Target.parseDepends(depends, name, "depends")) {
                         target.addDependency(prefix + sep + string);
                    }
                 }
             }
             if (!isInIncludeMode && context.isIgnoringProjectTag()
                 && (prefix = getTargetPrefix(context)) != null) {
                 // In an imported file (and not completely
                 // ignoring the project tag or having a preconfigured prefix)
                 String newName = prefix + sep + name;
                 Target newTarget = target;
                 if (usedTarget) {
                     newTarget = "target".equals(tag)
                             ? new Target(target) : new ExtensionPoint(target);
                 }
                 newTarget.setName(newName);
                 context.getCurrentTargets().put(newName, newTarget);
                 project.addOrReplaceTarget(newName, newTarget);
             }
             if (extensionPointMissing != null && extensionPoint == null) {
                 throw new BuildException("onMissingExtensionPoint attribute cannot " +
                                          "be specified unless extensionOf is specified",
                                          target.getLocation());
 
             }
             if (extensionPoint != null) {
                 ProjectHelper helper =
                     (ProjectHelper) context.getProject().
                     getReference(ProjectHelper.PROJECTHELPER_REFERENCE);
                 for (String extPointName : Target.parseDepends(extensionPoint, name, "extensionOf")) {
                     if (extensionPointMissing == null) {
                         extensionPointMissing = OnMissingExtensionPoint.FAIL;
                     }
                     // defer extensionpoint resolution until the full
                     // import stack has been processed
                     if (isInIncludeMode()) {
                         // if in include mode, provide prefix we're including by
                         // so that we can try and resolve extension point from
                         // the local file first
                         helper.getExtensionStack().add(
                                 new String[] {extPointName, target.getName(),
                                         extensionPointMissing.name(), prefix + sep});
                     } else {
                         helper.getExtensionStack().add(
                                 new String[] {extPointName, target.getName(),
                                         extensionPointMissing.name()});
                     }
                 }
             }
         }
 
         private String getTargetPrefix(AntXMLContext context) {
             String configuredValue = getCurrentTargetPrefix();
             if (configuredValue != null && configuredValue.length() == 0) {
                 configuredValue = null;
             }
             if (configuredValue != null) {
                 return configuredValue;
             }
 
             String projectName = context.getCurrentProjectName();
             if ("".equals(projectName)) {
                 projectName = null;
             }
 
             return projectName;
         }
 
         /**
          * Handles the start of an element within a target.
          *
          * @param uri The namespace URI for this element.
          * @param name The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          * @param context The current context.
          * @return an element handler.
          *
          * @exception SAXParseException if an error occurs when initialising
          *                              the appropriate child handler
          */
         public AntHandler onStartChild(String uri, String name, String qname, Attributes attrs,
                                        AntXMLContext context) throws SAXParseException {
             return ProjectHelper2.elementHandler;
         }
 
         /**
          * Handle the end of the project, sets the current target of the
          * context to be the implicit target.
          *
          * @param uri The namespace URI of the element.
          * @param tag The name of the element.
          * @param context The current context.
          */
         public void onEndElement(String uri, String tag, AntXMLContext context) {
             context.setCurrentTarget(context.getImplicitTarget());
         }
     }
 
     /**
      * Handler for all project elements ( tasks, data types )
      */
     public static class ElementHandler extends AntHandler {
 
         /**
          * Constructor.
          */
         public ElementHandler() {
         }
 
         /**
          * Initialisation routine called after handler creation
          * with the element name and attributes. This configures
          * the element with its attributes and sets it up with
          * its parent container (if any). Nested elements are then
          * added later as the parser encounters them.
          *
          * @param uri The namespace URI for this element.
          * @param tag Name of the element which caused this handler
          *            to be created. Must not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element which caused this
          *              handler to be created. Must not be <code>null</code>.
          * @param context The current context.
          *
          * @exception SAXParseException in case of error (not thrown in
          *                              this implementation)
          */
         public void onStartElement(String uri, String tag, String qname, Attributes attrs,
                                    AntXMLContext context) throws SAXParseException {
             RuntimeConfigurable parentWrapper = context.currentWrapper();
             Object parent = null;
 
             if (parentWrapper != null) {
                 parent = parentWrapper.getProxy();
             }
 
             /* UnknownElement is used for tasks and data types - with
                delayed eval */
             UnknownElement task = new UnknownElement(tag);
             task.setProject(context.getProject());
             task.setNamespace(uri);
             task.setQName(qname);
             task.setTaskType(ProjectHelper.genComponentName(task.getNamespace(), tag));
             task.setTaskName(qname);
 
             Location location = new Location(context.getLocator().getSystemId(), context
                                              .getLocator().getLineNumber(), context.getLocator().getColumnNumber());
             task.setLocation(location);
             task.setOwningTarget(context.getCurrentTarget());
 
             if (parent != null) {
                 // Nested element
                 ((UnknownElement) parent).addChild(task);
             }  else {
                 // Task included in a target ( including the default one ).
                 context.getCurrentTarget().addTask(task);
             }
 
             context.configureId(task, attrs);
 
             // container.addTask(task);
             // This is a nop in UE: task.init();
 
             RuntimeConfigurable wrapper = new RuntimeConfigurable(task, task.getTaskName());
 
             for (int i = 0; i < attrs.getLength(); i++) {
                 String name = attrs.getLocalName(i);
                 String attrUri = attrs.getURI(i);
                 if (attrUri != null && !attrUri.equals("") && !attrUri.equals(uri)) {
                     name = attrUri + ":" + attrs.getQName(i);
                 }
                 String value = attrs.getValue(i);
                 // PR: Hack for ant-type value
                 //  an ant-type is a component name which can
                 // be namespaced, need to extract the name
                 // and convert from qualified name to uri/name
                 if (ANT_TYPE.equals(name)
                     || (ANT_CORE_URI.equals(attrUri)
                         && ANT_TYPE.equals(attrs.getLocalName(i)))) {
                     name = ANT_TYPE;
                     int index = value.indexOf(":");
                     if (index >= 0) {
                         String prefix = value.substring(0, index);
                         String mappedUri = context.getPrefixMapping(prefix);
                         if (mappedUri == null) {
                             throw new BuildException("Unable to find XML NS prefix \"" + prefix
                                                      + "\"");
                         }
                         value = ProjectHelper.genComponentName(mappedUri, value
                                                                .substring(index + 1));
                     }
                 }
                 wrapper.setAttribute(name, value);
             }
             if (parentWrapper != null) {
                 parentWrapper.addChild(wrapper);
             }
             context.pushWrapper(wrapper);
         }
 
         /**
          * Adds text to the task, using the wrapper
          *
          * @param buf A character array of the text within the element.
          *            Will not be <code>null</code>.
          * @param start The start element in the array.
          * @param count The number of characters to read from the array.
          * @param context The current context.
          *
          * @exception SAXParseException if the element doesn't support text
          *
          * @see ProjectHelper#addText(Project,java.lang.Object,char[],int,int)
          */
         public void characters(char[] buf, int start, int count,
                                AntXMLContext context) throws SAXParseException {
             RuntimeConfigurable wrapper = context.currentWrapper();
             wrapper.addText(buf, start, count);
         }
 
         /**
          * Handles the start of an element within a target. Task containers
          * will always use another task handler, and all other tasks
          * will always use a nested element handler.
          *
          * @param uri The namespace URI for this element.
          * @param tag The name of the element being started.
          *            Will not be <code>null</code>.
          * @param qname The qualified name for this element.
          * @param attrs Attributes of the element being started.
          *              Will not be <code>null</code>.
          * @param context The current context.
          * @return The handler for elements.
          *
          * @exception SAXParseException if an error occurs when initialising
          *                              the appropriate child handler
          */
         public AntHandler onStartChild(String uri, String tag, String qname, Attributes attrs,
                                        AntXMLContext context) throws SAXParseException {
             return ProjectHelper2.elementHandler;
         }
 
         /**
          * Handles the end of the element. This pops the wrapper from
          * the context.
          *
          * @param uri The namespace URI for the element.
          * @param tag The name of the element.
          * @param context The current context.
          */
         public void onEndElement(String uri, String tag, AntXMLContext context) {
             context.popWrapper();
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/ImportTask.java b/src/main/org/apache/tools/ant/taskdefs/ImportTask.java
index d6de610b3..2da7f55b4 100644
--- a/src/main/org/apache/tools/ant/taskdefs/ImportTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/ImportTask.java
@@ -1,302 +1,304 @@
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
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectHelperRepository;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.URLResource;
 import org.apache.tools.ant.types.resources.Union;
 import org.apache.tools.ant.util.FileUtils;
 
 import java.io.File;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Vector;
 
 /**
  * Task to import another build file into the current project.
  * <p>
  * It must be 'top level'. On execution it will read another Ant file
  * into the same Project.
  * </p>
  * <p>
  * <b>Important</b>: Trying to understand how relative file references
  * resolved in deep/complex build hierarchies - such as what happens
  * when an imported file imports another file can be difficult. Use absolute references for
  * enhanced build file stability, especially in the imported files.
  * </p>
  * <p>Examples:</p>
  * <pre>
  * &lt;import file="../common-targets.xml"/&gt;
  * </pre>
  * <p>Import targets from a file in a parent directory.</p>
  * <pre>
  * &lt;import file="${deploy-platform}.xml"/&gt;
  * </pre>
  * <p>Import the project defined by the property <code>deploy-platform</code>.</p>
  *
  * @since Ant1.6
  * @ant.task category="control"
  */
 public class ImportTask extends Task {
     private String file;
     private boolean optional;
-    private String targetPrefix;
+    private String targetPrefix = ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX;
     private String prefixSeparator = ".";
     private final Union resources = new Union();
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     public ImportTask() {
         resources.setCache(true);
     }
 
     /**
      * sets the optional attribute
      *
      * @param optional if true ignore files that are not present,
      *                 default is false
      */
     public void setOptional(boolean optional) {
         this.optional = optional;
     }
 
     /**
      * the name of the file to import. How relative paths are resolved is still
      * in flux: use absolute paths for safety.
      * @param file the name of the file
      */
     public void setFile(String file) {
         // I don't think we can use File - different rules
         // for relative paths.
         this.file = file;
     }
 
     /**
      * The prefix to use when prefixing the imported target names.
      *
      * @since Ant 1.8.0
      */
     public void setAs(String prefix) {
         targetPrefix = prefix;
     }
 
     /**
      * The separator to use between prefix and target name, default is
      * ".".
      *
      * @since Ant 1.8.0
      */
     public void setPrefixSeparator(String s) {
         prefixSeparator = s;
     }
 
     /**
      * The resource to import.
      *
      * @since Ant 1.8.0
      */
     public void add(ResourceCollection r) {
         resources.add(r);
     }
 
     public void execute() {
         if (file == null && resources.size() == 0) {
             throw new BuildException("import requires file attribute or"
                                      + " at least one nested resource");
         }
         if (getOwningTarget() == null
             || !"".equals(getOwningTarget().getName())) {
             throw new BuildException("import only allowed as a top-level task");
         }
 
         ProjectHelper helper =
                 (ProjectHelper) getProject().
                     getReference(ProjectHelper.PROJECTHELPER_REFERENCE);
 
         if (helper == null) {
             // this happens if the projecthelper was not registered with the project.
             throw new BuildException("import requires support in ProjectHelper");
         }
 
         Vector<Object> importStack = helper.getImportStack();
 
         if (importStack.size() == 0) {
             // this happens if ant is used with a project
             // helper that doesn't set the import.
             throw new BuildException("import requires support in ProjectHelper");
         }
 
         if (getLocation() == null || getLocation().getFileName() == null) {
             throw new BuildException("Unable to get location of import task");
         }
 
         Union resourcesToImport = new Union(getProject(), resources);
         Resource fromFileAttribute = getFileAttributeResource();
         if (fromFileAttribute != null) {
             resources.add(fromFileAttribute);
         }
         for (Resource r : resourcesToImport) {
             importResource(helper, r);
         }
     }
 
     private void importResource(ProjectHelper helper,
                                 Resource importedResource) {
         Vector<Object> importStack = helper.getImportStack();
 
         getProject().log("Importing file " + importedResource + " from "
                          + getLocation().getFileName(), Project.MSG_VERBOSE);
 
         if (!importedResource.isExists()) {
             String message =
                 "Cannot find " + importedResource + " imported from "
                 + getLocation().getFileName();
             if (optional) {
                 getProject().log(message, Project.MSG_VERBOSE);
                 return;
             } else {
                 throw new BuildException(message);
             }
         }
 
         File importedFile = null;
         FileProvider fp = importedResource.as(FileProvider.class);
         if (fp != null) {
             importedFile = fp.getFile();
         }
 
         if (!isInIncludeMode() &&
             (importStack.contains(importedResource)
              || (importedFile != null && importStack.contains(importedFile))
              )
             ) {
             getProject().log(
                 "Skipped already imported file:\n   "
                 + importedResource + "\n", Project.MSG_VERBOSE);
             return;
         }
 
-        // nested invokations are possible like an imported file
+        // nested invocations are possible like an imported file
         // importing another one
         String oldPrefix = ProjectHelper.getCurrentTargetPrefix();
         boolean oldIncludeMode = ProjectHelper.isInIncludeMode();
         String oldSep = ProjectHelper.getCurrentPrefixSeparator();
         try {
             String prefix;
             if (isInIncludeMode() && oldPrefix != null
                 && targetPrefix != null) {
                 prefix = oldPrefix + oldSep + targetPrefix;
-            } else if (targetPrefix != null) {
+            } else if (isInIncludeMode()) {
+                prefix = targetPrefix;
+            } else if (!ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX.equals(targetPrefix)) {
                 prefix = targetPrefix;
             } else {
                 prefix = oldPrefix;
             }
             setProjectHelperProps(prefix, prefixSeparator,
                                   isInIncludeMode());
 
             ProjectHelper subHelper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(
                     importedResource);
 
             // push current stacks into the sub helper
             subHelper.getImportStack().addAll(helper.getImportStack());
             subHelper.getExtensionStack().addAll(helper.getExtensionStack());
             getProject().addReference(ProjectHelper.PROJECTHELPER_REFERENCE, subHelper);
 
             subHelper.parse(getProject(), importedResource);
 
             // push back the stack from the sub helper to the main one
             getProject().addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
             helper.getImportStack().clear();
             helper.getImportStack().addAll(subHelper.getImportStack());
             helper.getExtensionStack().clear();
             helper.getExtensionStack().addAll(subHelper.getExtensionStack());
         } catch (BuildException ex) {
             throw ProjectHelper.addLocationToBuildException(
                 ex, getLocation());
         } finally {
             setProjectHelperProps(oldPrefix, oldSep, oldIncludeMode);
         }
     }
 
     private Resource getFileAttributeResource() {
         // Paths are relative to the build file they're imported from,
         // *not* the current directory (same as entity includes).
 
         if (file != null) {
             File buildFile =
                 new File(getLocation().getFileName()).getAbsoluteFile();
             if (buildFile.exists()) {
                 File buildFileParent = new File(buildFile.getParent());
                 File importedFile =
                     FILE_UTILS.resolveFile(buildFileParent, file);
                 return new FileResource(importedFile);
             }
             // maybe this import tasks is inside an imported URL?
             try {
                 URL buildFileURL = new URL(getLocation().getFileName());
                 URL importedFile = new URL(buildFileURL, file);
                 return new URLResource(importedFile);
             } catch (MalformedURLException ex) {
                 log(ex.toString(), Project.MSG_VERBOSE);
             }
             throw new BuildException("failed to resolve " + file
                                      + " relative to "
                                      + getLocation().getFileName());
         }
         return null;
     }
 
     /**
      * Whether the task is in include (as opposed to import) mode.
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
     protected final boolean isInIncludeMode() {
         return "include".equals(getTaskType());
     }
 
     /**
      * Sets a bunch of Thread-local ProjectHelper properties.
      *
      * @since Ant 1.8.0
      */
     private static void setProjectHelperProps(String prefix,
                                               String prefixSep,
                                               boolean inIncludeMode) {
         ProjectHelper.setCurrentTargetPrefix(prefix);
         ProjectHelper.setCurrentPrefixSeparator(prefixSep);
         ProjectHelper.setInIncludeMode(inIncludeMode);
     }
 }
diff --git a/src/tests/antunit/taskdefs/importtests/w.xml b/src/tests/antunit/taskdefs/importtests/w.xml
new file mode 100644
index 000000000..e400c501d
--- /dev/null
+++ b/src/tests/antunit/taskdefs/importtests/w.xml
@@ -0,0 +1,22 @@
+<?xml version="1.0" encoding="UTF-8"?>
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
+<project name="w">
+    <echo>${ant.file.w}</echo>
+    <include file="x.xml"/>
+    <target name="ww" depends="x.xx, x.y.yy, x.y.z.zz"/>
+</project>
diff --git a/src/tests/antunit/taskdefs/importtests/x.xml b/src/tests/antunit/taskdefs/importtests/x.xml
new file mode 100644
index 000000000..a509e574e
--- /dev/null
+++ b/src/tests/antunit/taskdefs/importtests/x.xml
@@ -0,0 +1,22 @@
+<?xml version="1.0" encoding="UTF-8"?>
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
+<project name="x">
+    <echo>${ant.file.x}</echo>
+    <include file="y.xml"/>
+    <target name="xx" depends="y.yy, y.z.zz"/>
+</project>
diff --git a/src/tests/antunit/taskdefs/importtests/y.xml b/src/tests/antunit/taskdefs/importtests/y.xml
new file mode 100644
index 000000000..0e54fa838
--- /dev/null
+++ b/src/tests/antunit/taskdefs/importtests/y.xml
@@ -0,0 +1,22 @@
+<?xml version="1.0" encoding="UTF-8"?>
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
+<project name="y">
+    <echo>${ant.file.y}</echo>
+    <include file="z.xml" as="z"/>
+    <target name="yy" depends="z.zz"/>
+</project>
diff --git a/src/tests/antunit/taskdefs/importtests/z.xml b/src/tests/antunit/taskdefs/importtests/z.xml
new file mode 100644
index 000000000..607dee7e9
--- /dev/null
+++ b/src/tests/antunit/taskdefs/importtests/z.xml
@@ -0,0 +1,21 @@
+<?xml version="1.0" encoding="UTF-8"?>
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
+<project name="z">
+    <echo>${ant.file.z}</echo>
+    <target name="zz"/>
+</project>
diff --git a/src/tests/antunit/taskdefs/include-test.xml b/src/tests/antunit/taskdefs/include-test.xml
index 74da94e73..7e1a45231 100644
--- a/src/tests/antunit/taskdefs/include-test.xml
+++ b/src/tests/antunit/taskdefs/include-test.xml
@@ -1,53 +1,58 @@
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
 
-  <include>
-    <file file="importtests/a.xml"/>
-  </include>
+ <include>
+  <file file="importtests/a.xml"/>
+</include>
   <include file="importtests/b.xml" as="c"/>
 
   <target name="testNoExplicitPrefix" depends="a.a">
     <au:assertEquals expected="bar" actual="${foo}"/>
   </target>
 
   <target name="testExplicitPrefix" depends="c.b">
     <au:assertEquals expected="baz" actual="${foo}"/>
   </target>
 
   <include file="importtests/override.xml"/>
 
   <target name="setProperty">
     <property name="prop" value="in including/importing"/>
   </target>
 
   <target name="testNoOverride" depends="override.dummy">
     <au:assertEquals expected="in included/imported" actual="${prop}"/>
   </target>
 
   <include as="nested">
     <javaresource name="nested.xml">
       <classpath location="importtests"/>
     </javaresource>
   </include>
 
   <!-- really only tests that the targets have the expected names by
        forcing an exception if the dependencies don't exist -->
   <target name="testNesting" depends="nested.b::b, nested.aa"/>
+  <target name="testNoExplicitPrefixNested">
+    <ant target="x.y.z.zz" antfile="importtests/w.xml"/>
+  </target>
+
+
 </project>
