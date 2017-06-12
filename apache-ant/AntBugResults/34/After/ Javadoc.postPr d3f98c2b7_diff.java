diff --git a/WHATSNEW b/WHATSNEW
index b584694b3..3a65c2d38 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1082 +1,1086 @@
 Changes from Ant 1.9.3 TO current
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * the prefixValues attribute of <property> didn't work as expected
    when set to false (the default).
    It is quite likely existing build files relied on the wrong
    behavior and expect Ant to resolve the value side against the
    properties defined in the property file itself - these build files
    must now explicitly set the prefixValues attribute to true.
    Bugzilla Report 54769
 
  * when matching an entry of a zip/tarfileset against a pattern a
    leading slash will be stripped from the entry name.  Most archive
    don't contain paths with leading slashes anyway.
    This may cause include/exclude patterns that start with a / to stop
    matching anything.  Such patterns only used to work by accident and
    only on platforms with multiple file syste roots.
    Bugzilla Report 53949
 
 Fixed bugs:
 -----------
 
  * <import>/<include> failed when the importing file was loaded from an
    URI or a jar and it imported a file from the local file system via
    an absolute path.
    Bugzilla Report 50953
 
  * <import> could import the same resource twice when imported via
    different resource types.
    Bugzilla Report 55097
 
  * several calls to File#mkdirs could fall victim to a race condition
    where another thread already created the same directory.
    Bugzilla Report 55290
 
  * <manifestclasspath> created '/' rather than './' for the parent
    directory of the given jarfile.
    Bugzilla Report 55049
 
  * <concat>'s fixlastline="true" didn't work when using certain filter
    readers.
    Bugzilla Report 54672
 
  * several places where resources are read from jars will now
    explicitly disable caching to avoid problems with reloading jars.
    Bugzilla Report 54473
 
  * AntClassloader will now ignore files that are part of the classpath
    but not zip files when scanning for resources.  It used to throw an
    exception.
    Bugzilla Report 53964
 
  * <javadoc> caused a NullPointerException when no destdir was set.
    Bugzilla Report 55949
 
  * <jar filesetmanifest="mergewithoutmain"> would still include the
    Main section of the fileset manifests if there was no nested
    manifest or manifest attribute.
    Bugzilla Report 54171
 
  * reading of compiler args has become more defensive
    Bugzilla Report 53754
 
  * <copy> without force="true" would not only fail to overwrite a
    read-only file as expected but also remove the existing file.
    Bugzilla Report 53095
 
  * <delete removeNotFollowedSymlinks="true"> would remove symbolic
    links to not-included files.  It will still delete symlinks to
    directories that would have been followed even if they are not
    explicitly included.  exclude-Patterns can still be used to
    preserve symbolic links.
    Bugzilla Report 53959
 
  * Sometimes copy-operations using NIO FileChannels fail.  Ant will
    now try to use a Stream based copy operation as fallback when the
    Channel based copy fails.
    Bugzilla Reports 53102 and 54397
 
+ * Javadoc.postProcessGeneratedJavadocs() fails for Classes that
+   extend Javadoc
+   Bugzilla Report 56047
+
 Other changes:
 --------------
 
  * <sshexec> can optionally pass System.in to the remote process
    Bugzilla Report 55393
 
  * <sshexec> now supports capturing error output of the executed
    process and setting a property from the return code.
    Bugzilla Report 48478
 
  * <javadoc> now has an option to fail if javadoc issues warnings.
    Bugzilla Report 55015
 
  * <sql> has a new outputencoding attribute.
    Bugzilla Report 39541
 
  * changes to JUnitTestRunner and PlainJUnitResultFormatter to make
    OutOfMemoryErrors less likely.
    Bugzilla Report 45536
 
  * changes to DOMElementWriter to make OutOfMemoryErrors less likely.
    Bugzilla Report 54147
 
 Changes from Ant 1.9.2 TO Ant 1.9.3
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
 
 Fixed bugs:
 -----------
 
  * <parallel> swallowed the status code of nested <fail> tasks.
    Bugzilla Report 55539.
 
  * a race condition could make <fixcrlf> tasks of parallel builds to
    interfere with each other.
    Bugzilla Report 54393.
 
  * <mail>'s mailport still didn't work properly when using smtps.
    Bugzilla Report 49267.
 
  * using attributes belonging to the if and unless namespaces
    made macrodef fail.
    Bugzilla Report 55885.
 
  * Ant 1.8 exec task changes have slowed exec to a crawl
    Bugzilla Report 54128.
 
  * Apt is not available under JDK 1.8
    Bugzilla Report 55922.
 
 
 Other changes:
 --------------
 
  * Documentation fix for if/unless attributes.  PR 55359.
 
  * tar entries with long link names are now handled the same way as
    entries with long names.
 
  * Addition of 'skipNonTests' attribute to <junit> and <batchtest>
    tasks to allow the tasks to skip classes that don't contain tests.
 
  * <filterset> now supports a nested <propertyset> to specify filters.
    Bugzilla Report 55794.
 
  * <xslt>'s params can now be typed.
    Bugzilla Report 21525.
 
  * build of Mac OS X pkg installer
    Bugzilla Report 55899.
 
 Changes from Ant 1.9.1 TO Ant 1.9.2
 ===================================
 
 Fixed bugs:
 -----------
 
  * Parsing of zip64 extra fields has become more lenient in order to
    be able to read archives created by DotNetZip and maybe other
    archivers as well.
 
  * TarInputStream should now properly read GNU longlink entries' names.
    Bugzilla Report 55040.
 
  * <java> and <exec> used to be too restrictive when evaluating
    whether a given set of options is compatible with spawning the new
    process.
    Bugzilla Report 55112.
 
 Other changes:
 --------------
 
  * <javadoc> will now post-process the generated in order to mitigate
    the frame injection attack possible in javadocs generated by Oracle
    JDKs prior to Java7 Update 25.  The vulnerability is known as
    CVE-2013-1571.
    There is an option to turn off the post-processing but it is only
    recommended you do so if all your builds use a JDK that's not
    vulnerable.
    Bugzilla Report 55132.
 
 Changes from Ant 1.9.0 TO Ant 1.9.1
 ===================================
 
 Changes that could break older environments:
 -------------------------------------------
 
  * Users who have their own ProjectHelper implementation will need to change it because the import and include tasks
    will now default the targetPrefix to ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX.
    Users using the default ProjectHelper2 with ant need not worry about this change done to fix Bugzilla Report 54940.
 
 
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
 
  * Target rewriting for nested "include" only works when "as" is specified.
    See also "Changes that could break older environments"
    Bugzilla Report 54940.
 
 
 Other changes:
 --------------
 
  * strict attribute added to <signjar>.
    Bugzilla Report 54889.
 
  * simplifying Execute.getEnvironmentVariables since we are only running on Java 1.5 or higher now
 
  * Added conditional attributes.
    Bugzilla Report 43362
 
  * Recommending to upgrade jsch to 0.1.50, particularly if you are using Java 1.7.
    jsch is the library behind the sshexec and scp Ant tasks.
    Versions of jsch older than 0.1.50 fail randomly under Java 1.7 with an error message "verify: false"
 
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
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javadoc.java b/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
index f0e7bb166..4d6659f2d 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
@@ -1477,1142 +1477,1142 @@ public class Javadoc extends Task {
         }
 
         /**
          * Set the title attribute using a string.
          * @param src a <code>String</code> value
          */
         public void setTitle(String src) {
             Html h = new Html();
             h.addText(src);
             addTitle(h);
         }
         /**
          * Set the title attribute using a nested Html value.
          * @param text a <code>Html</code> value
          */
         public void addTitle(Html text) {
             title = text;
         }
 
         /**
          * Get the title.
          * @return the title
          */
         public String getTitle() {
             return title != null ? title.getText() : null;
         }
 
         /**
          * Set the packages to Javadoc on.
          * @param src a comma separated list of packages
          */
         public void setPackages(String src) {
             StringTokenizer tok = new StringTokenizer(src, ",");
             while (tok.hasMoreTokens()) {
                 String p = tok.nextToken();
                 PackageName pn = new PackageName();
                 pn.setName(p);
                 addPackage(pn);
             }
         }
         /**
          * Add a package nested element.
          * @param pn a nested element specifying the package.
          */
         public void addPackage(PackageName pn) {
             packages.addElement(pn);
         }
 
         /**
          * Get the packages as a colon separated list.
          * @return the packages as a string
          */
         public String getPackages() {
             StringBuffer p = new StringBuffer();
             final int size = packages.size();
             for (int i = 0; i < size; i++) {
                 if (i > 0) {
                     p.append(":");
                 }
                 p.append(packages.elementAt(i).toString());
             }
             return p.toString();
         }
     }
 
     /**
      * Charset for cross-platform viewing of generated documentation.
      * @param src the name of the charset
      */
     public void setCharset(String src) {
         this.addArgIfNotEmpty("-charset", src);
     }
 
     /**
      * Should the build process fail if Javadoc fails (as indicated by
      * a non zero return code)?
      *
      * <p>Default is false.</p>
      * @param b a <code>boolean</code> value
      */
     public void setFailonerror(boolean b) {
         failOnError = b;
     }
 
     /**
      * Should the build process fail if Javadoc warns (as indicated by
      * the word "warning" on stdout)?
      *
      * <p>Default is false.</p>
      * @param b a <code>boolean</code> value
      * @since Ant 1.9.4
      */
     public void setFailonwarning(boolean b) {
         failOnWarning = b;
     }
 
     /**
      * Enables the -source switch, will be ignored if Javadoc is not
      * the 1.4 version.
      * @param source a <code>String</code> value
      * @since Ant 1.5
      */
     public void setSource(String source) {
         this.source = source;
     }
 
     /**
      * Sets the actual executable command to invoke, instead of the binary
      * <code>javadoc</code> found in Ant's JDK.
      * @param executable the command to invoke.
      * @since Ant 1.6.3
      */
     public void setExecutable(String executable) {
         this.executable = executable;
     }
 
     /**
      * Adds a packageset.
      *
      * <p>All included directories will be translated into package
      * names be converting the directory separator into dots.</p>
      * @param packageSet a directory set
      * @since 1.5
      */
     public void addPackageset(DirSet packageSet) {
         packageSets.addElement(packageSet);
     }
 
     /**
      * Adds a fileset.
      *
      * <p>All included files will be added as sourcefiles.  The task
      * will automatically add
      * <code>includes=&quot;**&#47;*.java&quot;</code> to the
      * fileset.</p>
      * @param fs a file set
      * @since 1.5
      */
     public void addFileset(FileSet fs) {
         createSourceFiles().add(fs);
     }
 
     /**
      * Adds a container for resource collections.
      *
      * <p>All included files will be added as sourcefiles.</p>
      * @return the source files to configure.
      * @since 1.7
      */
     public ResourceCollectionContainer createSourceFiles() {
         return nestedSourceFiles;
     }
 
     /**
      * Enables the -linksource switch, will be ignored if Javadoc is not
      * the 1.4 version. Default is false
      * @param b a <code>String</code> value
      * @since Ant 1.6
      */
     public void setLinksource(boolean b) {
         this.linksource = b;
     }
 
     /**
      * Enables the -linksource switch, will be ignored if Javadoc is not
      * the 1.4 version. Default is false
      * @param b a <code>String</code> value
      * @since Ant 1.6
      */
     public void setBreakiterator(boolean b) {
         this.breakiterator = b;
     }
 
     /**
      * Enables the -noqualifier switch, will be ignored if Javadoc is not
      * the 1.4 version.
      * @param noqualifier the parameter to the -noqualifier switch
      * @since Ant 1.6
      */
     public void setNoqualifier(String noqualifier) {
         this.noqualifier = noqualifier;
     }
 
     /**
      * If set to true, Ant will also accept packages that only hold
      * package.html files but no Java sources.
      * @param b a <code>boolean</code> value.
      * @since Ant 1.6.3
      */
     public void setIncludeNoSourcePackages(boolean b) {
         this.includeNoSourcePackages = b;
     }
 
     /**
      * Enables deep-copying of <code>doc-files</code> directories.
      *
      * @since Ant 1.8.0
      */
     public void setDocFilesSubDirs(boolean b) {
         docFilesSubDirs = b;
     }
 
     /**
      * Colon-separated list of <code>doc-files</code> subdirectories
      * to skip if {@link #setDocFilesSubDirs docFilesSubDirs is true}.
      *
      * @since Ant 1.8.0
      */
     public void setExcludeDocFilesSubDir(String s) {
         excludeDocFilesSubDir = s;
     }
 
     /**
      * Whether to post-process the generated javadocs in order to mitigate CVE-2013-1571.
      * @since Ant 1.9.2
      */
     public void setPostProcessGeneratedJavadocs(boolean b) {
         postProcessGeneratedJavadocs = b;
     }
 
     /**
      * Execute the task.
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
         checkTaskName();
 
         Vector<String> packagesToDoc = new Vector<String>();
         Path sourceDirs = new Path(getProject());
 
         checkPackageAndSourcePath();
 
         if (sourcePath != null) {
             sourceDirs.addExisting(sourcePath);
         }
 
         parsePackages(packagesToDoc, sourceDirs);
         checkPackages(packagesToDoc, sourceDirs);
 
         @SuppressWarnings("unchecked")
         Vector<SourceFile> sourceFilesToDoc = (Vector<SourceFile>) sourceFiles.clone();
         addSourceFiles(sourceFilesToDoc);
 
         checkPackagesToDoc(packagesToDoc, sourceFilesToDoc);
 
         log("Generating Javadoc", Project.MSG_INFO);
 
         Commandline toExecute = (Commandline) cmd.clone();
         if (executable != null) {
             toExecute.setExecutable(executable);
         } else {
             toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
         }
 
         //  Javadoc arguments
         generalJavadocArguments(toExecute);  // general Javadoc arguments
         doSourcePath(toExecute, sourceDirs); // sourcepath
         doDoclet(toExecute);   // arguments for default doclet
         doBootPath(toExecute); // bootpath
         doLinks(toExecute);    // links arguments
         doGroup(toExecute);    // group attribute
         doGroups(toExecute);  // groups attribute
         doDocFilesSubDirs(toExecute); // docfilessubdir attribute
 
         doJava14(toExecute);
         if (breakiterator && (doclet == null || JAVADOC_5)) {
             toExecute.createArgument().setValue("-breakiterator");
         }
         // If using an external file, write the command line options to it
         if (useExternalFile) {
             writeExternalArgs(toExecute);
         }
 
         File tmpList = null;
         FileWriter wr = null;
         try {
             /**
              * Write sourcefiles and package names to a temporary file
              * if requested.
              */
             BufferedWriter srcListWriter = null;
             if (useExternalFile) {
                 tmpList = FILE_UTILS.createTempFile("javadoc", "", null, true, true);
                 toExecute.createArgument()
                     .setValue("@" + tmpList.getAbsolutePath());
                 wr = new FileWriter(tmpList.getAbsolutePath(), true);
                 srcListWriter = new BufferedWriter(wr);
             }
 
             doSourceAndPackageNames(
                 toExecute, packagesToDoc, sourceFilesToDoc,
                 useExternalFile, tmpList, srcListWriter);
 
             if (useExternalFile) {
                 srcListWriter.flush();
             }
         } catch (IOException e) {
             tmpList.delete();
             throw new BuildException("Error creating temporary file",
                                      e, getLocation());
         } finally {
             FileUtils.close(wr);
         }
 
         if (packageList != null) {
             toExecute.createArgument().setValue("@" + packageList);
         }
         log(toExecute.describeCommand(), Project.MSG_VERBOSE);
 
         log("Javadoc execution", Project.MSG_INFO);
 
         JavadocOutputStream out = new JavadocOutputStream(Project.MSG_INFO);
         JavadocOutputStream err = new JavadocOutputStream(Project.MSG_WARN);
         Execute exe = new Execute(new PumpStreamHandler(out, err));
         exe.setAntRun(getProject());
 
         /*
          * No reason to change the working directory as all filenames and
          * path components have been resolved already.
          *
          * Avoid problems with command line length in some environments.
          */
         exe.setWorkingDirectory(null);
         try {
             exe.setCommandline(toExecute.getCommandline());
             int ret = exe.execute();
             if (ret != 0 && failOnError) {
                 throw new BuildException("Javadoc returned " + ret,
                                          getLocation());
             }
             if (out.sawWarnings() && failOnWarning) {
                 throw new BuildException("Javadoc issued warnings.",
                                          getLocation());
             }
             postProcessGeneratedJavadocs();
         } catch (IOException e) {
             throw new BuildException("Javadoc failed: " + e, e, getLocation());
         } finally {
             if (tmpList != null) {
                 tmpList.delete();
                 tmpList = null;
             }
 
             out.logFlush();
             err.logFlush();
             try {
                 out.close();
                 err.close();
             } catch (IOException e) {
                 // ignore
             }
         }
     }
 
     private void checkTaskName() {
         if ("javadoc2".equals(getTaskType())) {
             log("Warning: the task name <javadoc2> is deprecated."
                 + " Use <javadoc> instead.",
                 Project.MSG_WARN);
         }
     }
 
     private void checkPackageAndSourcePath() {
         if (packageList != null && sourcePath == null) {
             String msg = "sourcePath attribute must be set when "
                 + "specifying packagelist.";
             throw new BuildException(msg);
         }
     }
 
     private void checkPackages(Vector<String> packagesToDoc, Path sourceDirs) {
         if (packagesToDoc.size() != 0 && sourceDirs.size() == 0) {
             String msg = "sourcePath attribute must be set when "
                 + "specifying package names.";
             throw new BuildException(msg);
         }
     }
 
     private void checkPackagesToDoc(
         Vector<String> packagesToDoc, Vector<SourceFile> sourceFilesToDoc) {
         if (packageList == null && packagesToDoc.size() == 0
             && sourceFilesToDoc.size() == 0) {
             throw new BuildException("No source files and no packages have "
                                      + "been specified.");
         }
     }
 
     private void doSourcePath(Commandline toExecute, Path sourceDirs) {
         if (sourceDirs.size() > 0) {
             toExecute.createArgument().setValue("-sourcepath");
             toExecute.createArgument().setPath(sourceDirs);
         }
     }
 
     private void generalJavadocArguments(Commandline toExecute) {
         if (doctitle != null) {
             toExecute.createArgument().setValue("-doctitle");
             toExecute.createArgument().setValue(expand(doctitle.getText()));
         }
         if (header != null) {
             toExecute.createArgument().setValue("-header");
             toExecute.createArgument().setValue(expand(header.getText()));
         }
         if (footer != null) {
             toExecute.createArgument().setValue("-footer");
             toExecute.createArgument().setValue(expand(footer.getText()));
         }
         if (bottom != null) {
             toExecute.createArgument().setValue("-bottom");
             toExecute.createArgument().setValue(expand(bottom.getText()));
         }
 
         if (classpath == null) {
             classpath = (new Path(getProject())).concatSystemClasspath("last");
         } else {
             classpath = classpath.concatSystemClasspath("ignore");
         }
 
         if (classpath.size() > 0) {
             toExecute.createArgument().setValue("-classpath");
             toExecute.createArgument().setPath(classpath);
         }
 
         if (version && doclet == null) {
             toExecute.createArgument().setValue("-version");
         }
         if (author && doclet == null) {
             toExecute.createArgument().setValue("-author");
         }
 
         if (doclet == null && destDir == null) {
             throw new BuildException("destdir attribute must be set!");
         }
     }
 
     private void doDoclet(Commandline toExecute) {
         if (doclet != null) {
             if (doclet.getName() == null) {
                 throw new BuildException("The doclet name must be "
                                          + "specified.", getLocation());
             } else {
                 toExecute.createArgument().setValue("-doclet");
                 toExecute.createArgument().setValue(doclet.getName());
                 if (doclet.getPath() != null) {
                     Path docletPath
                         = doclet.getPath().concatSystemClasspath("ignore");
                     if (docletPath.size() != 0) {
                         toExecute.createArgument().setValue("-docletpath");
                         toExecute.createArgument().setPath(docletPath);
                     }
                 }
                 for (Enumeration<DocletParam> e = doclet.getParams();
                      e.hasMoreElements();) {
                     DocletParam param = e.nextElement();
                     if (param.getName() == null) {
                         throw new BuildException("Doclet parameters must "
                                                  + "have a name");
                     }
 
                     toExecute.createArgument().setValue(param.getName());
                     if (param.getValue() != null) {
                         toExecute.createArgument()
                             .setValue(param.getValue());
                     }
                 }
             }
         }
     }
 
     private void writeExternalArgs(Commandline toExecute) {
         // If using an external file, write the command line options to it
         File optionsTmpFile = null;
         BufferedWriter optionsListWriter = null;
         try {
             optionsTmpFile = FILE_UTILS.createTempFile(
                 "javadocOptions", "", null, true, true);
             String[] listOpt = toExecute.getArguments();
             toExecute.clearArgs();
             toExecute.createArgument().setValue(
                 "@" + optionsTmpFile.getAbsolutePath());
             optionsListWriter = new BufferedWriter(
                 new FileWriter(optionsTmpFile.getAbsolutePath(), true));
             for (int i = 0; i < listOpt.length; i++) {
                 String string = listOpt[i];
                 if (string.startsWith("-J-")) {
                     toExecute.createArgument().setValue(string);
                 } else  {
                     if (string.startsWith("-")) {
                         optionsListWriter.write(string);
                         optionsListWriter.write(" ");
                     } else {
                         optionsListWriter.write(quoteString(string));
                         optionsListWriter.newLine();
                     }
                 }
             }
             optionsListWriter.close();
         } catch (IOException ex) {
             if (optionsTmpFile != null) {
                 optionsTmpFile.delete();
             }
             throw new BuildException(
                 "Error creating or writing temporary file for javadoc options",
                 ex, getLocation());
         } finally {
             FileUtils.close(optionsListWriter);
         }
     }
 
     private void doBootPath(Commandline toExecute) {
         Path bcp = new Path(getProject());
         if (bootclasspath != null) {
             bcp.append(bootclasspath);
         }
         bcp = bcp.concatSystemBootClasspath("ignore");
         if (bcp.size() > 0) {
             toExecute.createArgument().setValue("-bootclasspath");
             toExecute.createArgument().setPath(bcp);
         }
     }
 
     private void doLinks(Commandline toExecute) {
         if (links.size() != 0) {
             for (Enumeration<LinkArgument> e = links.elements(); e.hasMoreElements();) {
                 LinkArgument la = e.nextElement();
 
                 if (la.getHref() == null || la.getHref().length() == 0) {
                     log("No href was given for the link - skipping",
                         Project.MSG_VERBOSE);
                     continue;
                 }
                 String link = null;
                 if (la.shouldResolveLink()) {
                     File hrefAsFile =
                         getProject().resolveFile(la.getHref());
                     if (hrefAsFile.exists()) {
                         try {
                             link = FILE_UTILS.getFileURL(hrefAsFile)
                                 .toExternalForm();
                         } catch (MalformedURLException ex) {
                             // should be impossible
                             log("Warning: link location was invalid "
                                 + hrefAsFile, Project.MSG_WARN);
                         }
                     }
                 }
                 if (link == null) {
                     // is the href a valid URL
                     try {
                         URL base = new URL("file://.");
                         new URL(base, la.getHref());
                         link = la.getHref();
                     } catch (MalformedURLException mue) {
                         // ok - just skip
                         log("Link href \"" + la.getHref()
                             + "\" is not a valid url - skipping link",
                             Project.MSG_WARN);
                         continue;
                     }
                 }
 
                 if (la.isLinkOffline()) {
                     File packageListLocation = la.getPackagelistLoc();
                     URL packageListURL = la.getPackagelistURL();
                     if (packageListLocation == null
                         && packageListURL == null) {
                         throw new BuildException("The package list"
                                                  + " location for link "
                                                  + la.getHref()
                                                  + " must be provided "
                                                  + "because the link is "
                                                  + "offline");
                     }
                     if (packageListLocation != null) {
                         File packageListFile =
                             new File(packageListLocation, "package-list");
                         if (packageListFile.exists()) {
                             try {
                                 packageListURL =
                                     FILE_UTILS.getFileURL(packageListLocation);
                             } catch (MalformedURLException ex) {
                                 log("Warning: Package list location was "
                                     + "invalid " + packageListLocation,
                                     Project.MSG_WARN);
                             }
                         } else {
                             log("Warning: No package list was found at "
                                 + packageListLocation, Project.MSG_VERBOSE);
                         }
                     }
                     if (packageListURL != null) {
                         toExecute.createArgument().setValue("-linkoffline");
                         toExecute.createArgument().setValue(link);
                         toExecute.createArgument()
                             .setValue(packageListURL.toExternalForm());
                     }
                 } else {
                     toExecute.createArgument().setValue("-link");
                     toExecute.createArgument().setValue(link);
                 }
             }
         }
     }
 
     private void doGroup(Commandline toExecute) {
         // add the single group arguments
         // Javadoc 1.2 rules:
         //   Multiple -group args allowed.
         //   Each arg includes 3 strings: -group [name] [packagelist].
         //   Elements in [packagelist] are colon-delimited.
         //   An element in [packagelist] may end with the * wildcard.
 
         // Ant javadoc task rules for group attribute:
         //   Args are comma-delimited.
         //   Each arg is 2 space-delimited strings.
         //   E.g., group="XSLT_Packages org.apache.xalan.xslt*,
         //                XPath_Packages org.apache.xalan.xpath*"
         if (group != null) {
             StringTokenizer tok = new StringTokenizer(group, ",", false);
             while (tok.hasMoreTokens()) {
                 String grp = tok.nextToken().trim();
                 int space = grp.indexOf(" ");
                 if (space > 0) {
                     String name = grp.substring(0, space);
                     String pkgList = grp.substring(space + 1);
                     toExecute.createArgument().setValue("-group");
                     toExecute.createArgument().setValue(name);
                     toExecute.createArgument().setValue(pkgList);
                 }
             }
         }
     }
 
     // add the group arguments
     private void doGroups(Commandline toExecute) {
         if (groups.size() != 0) {
             for (Enumeration<GroupArgument> e = groups.elements(); e.hasMoreElements();) {
                 GroupArgument ga = e.nextElement();
                 String title = ga.getTitle();
                 String packages = ga.getPackages();
                 if (title == null || packages == null) {
                     throw new BuildException("The title and packages must "
                                              + "be specified for group "
                                              + "elements.");
                 }
                 toExecute.createArgument().setValue("-group");
                 toExecute.createArgument().setValue(expand(title));
                 toExecute.createArgument().setValue(packages);
             }
         }
     }
 
     // Do java1.4 arguments
     private void doJava14(Commandline toExecute) {
         for (Enumeration<Object> e = tags.elements(); e.hasMoreElements();) {
             Object element = e.nextElement();
             if (element instanceof TagArgument) {
                 TagArgument ta = (TagArgument) element;
                 File tagDir = ta.getDir(getProject());
                 if (tagDir == null) {
                     // The tag element is not used as a fileset,
                     // but specifies the tag directly.
                     toExecute.createArgument().setValue ("-tag");
                     toExecute.createArgument()
                         .setValue (ta.getParameter());
                 } else {
                     // The tag element is used as a
                     // fileset. Parse all the files and create
                     // -tag arguments.
                     DirectoryScanner tagDefScanner =
                         ta.getDirectoryScanner(getProject());
                     String[] files = tagDefScanner.getIncludedFiles();
                     for (int i = 0; i < files.length; i++) {
                         File tagDefFile = new File(tagDir, files[i]);
                         try {
                             BufferedReader in
                                 = new BufferedReader(
                                     new FileReader(tagDefFile)
                                                      );
                             String line = null;
                             while ((line = in.readLine()) != null) {
                                 toExecute.createArgument()
                                     .setValue("-tag");
                                 toExecute.createArgument()
                                     .setValue(line);
                             }
                             in.close();
                         } catch (IOException ioe) {
                             throw new BuildException(
                                 "Couldn't read "
                                 + " tag file from "
                                 + tagDefFile.getAbsolutePath(), ioe);
                         }
                     }
                 }
             } else {
                 ExtensionInfo tagletInfo = (ExtensionInfo) element;
                 toExecute.createArgument().setValue("-taglet");
                 toExecute.createArgument().setValue(tagletInfo
                                                     .getName());
                 if (tagletInfo.getPath() != null) {
                     Path tagletPath = tagletInfo.getPath()
                         .concatSystemClasspath("ignore");
                     if (tagletPath.size() != 0) {
                         toExecute.createArgument()
                             .setValue("-tagletpath");
                         toExecute.createArgument().setPath(tagletPath);
                     }
                 }
             }
         }
 
         String sourceArg = source != null ? source
             : getProject().getProperty(MagicNames.BUILD_JAVAC_SOURCE);
         if (sourceArg != null) {
             toExecute.createArgument().setValue("-source");
             toExecute.createArgument().setValue(sourceArg);
         }
 
         if (linksource && doclet == null) {
             toExecute.createArgument().setValue("-linksource");
         }
         if (noqualifier != null && doclet == null) {
             toExecute.createArgument().setValue("-noqualifier");
             toExecute.createArgument().setValue(noqualifier);
         }
     }
 
     private void doDocFilesSubDirs(Commandline toExecute) {
         if (docFilesSubDirs) {
             toExecute.createArgument().setValue("-docfilessubdirs");
             if (excludeDocFilesSubDir != null
                 && excludeDocFilesSubDir.trim().length() > 0) {
                 toExecute.createArgument().setValue("-excludedocfilessubdir");
                 toExecute.createArgument().setValue(excludeDocFilesSubDir);
             }
         }
     }
 
     private void doSourceAndPackageNames(
         Commandline toExecute,
         Vector<String> packagesToDoc,
         Vector<SourceFile> sourceFilesToDoc,
         boolean useExternalFile,
         File    tmpList,
         BufferedWriter srcListWriter)
         throws IOException {
         for (String packageName : packagesToDoc) {
             if (useExternalFile) {
                 srcListWriter.write(packageName);
                 srcListWriter.newLine();
             } else {
                 toExecute.createArgument().setValue(packageName);
             }
         }
 
         for (SourceFile sf : sourceFilesToDoc) {
             String sourceFileName = sf.getFile().getAbsolutePath();
             if (useExternalFile) {
                 // TODO what is the following doing?
                 //     should it run if !javadoc4 && executable != null?
                 if (sourceFileName.indexOf(" ") > -1) {
                     String name = sourceFileName;
                     if (File.separatorChar == '\\') {
                         name = sourceFileName.replace(File.separatorChar, '/');
                     }
                     srcListWriter.write("\"" + name + "\"");
                 } else {
                     srcListWriter.write(sourceFileName);
                 }
                 srcListWriter.newLine();
             } else {
                 toExecute.createArgument().setValue(sourceFileName);
             }
         }
     }
 
     /**
      * Quote a string to place in a @ file.
      * @param str the string to quote
      * @return the quoted string, if there is no need to quote the string,
      *         return the original string.
      */
     private String quoteString(final String str) {
         if (!containsWhitespace(str)
             && str.indexOf('\'') == -1
             && str.indexOf('"') == -1) {
             return str;
         }
         if (str.indexOf('\'') == -1) {
             return quoteString(str, '\'');
         } else {
             return quoteString(str, '"');
         }
     }
 
     private boolean containsWhitespace(final String s) {
         final int len = s.length();
         for (int i = 0; i < len; i++) {
             if (Character.isWhitespace(s.charAt(i))) {
                 return true;
             }
         }
         return false;
     }
 
     private String quoteString(final String str, final char delim) {
         StringBuffer buf = new StringBuffer(str.length() * 2);
         buf.append(delim);
         final int len = str.length();
         boolean lastCharWasCR = false;
         for (int i = 0; i < len; i++) {
             char c = str.charAt(i);
             if (c == delim) { // can't put the non-constant delim into a case
                 buf.append('\\').append(c);
                 lastCharWasCR = false;
             } else {
                 switch (c) {
                 case '\\':
                     buf.append("\\\\");
                     lastCharWasCR = false;
                     break;
                 case '\r':
                     // insert a line continuation marker
                     buf.append("\\\r");
                     lastCharWasCR = true;
                     break;
                 case '\n':
                     // insert a line continuation marker unless this
                     // is a \r\n sequence in which case \r already has
                     // created the marker
                     if (!lastCharWasCR) {
                         buf.append("\\\n");
                     } else {
                         buf.append("\n");
                     }
                     lastCharWasCR = false;
                     break;
                 default:
                     buf.append(c);
                     lastCharWasCR = false;
                     break;
                 }
             }
         }
         buf.append(delim);
         return buf.toString();
     }
 
     /**
      * Add the files matched by the nested source files to the Vector
      * as SourceFile instances.
      *
      * @since 1.7
      */
     private void addSourceFiles(Vector<SourceFile> sf) {
         Iterator<ResourceCollection> e = nestedSourceFiles.iterator();
         while (e.hasNext()) {
             ResourceCollection rc = e.next();
             if (!rc.isFilesystemOnly()) {
                 throw new BuildException("only file system based resources are"
                                          + " supported by javadoc");
             }
             if (rc instanceof FileSet) {
                 FileSet fs = (FileSet) rc;
                 if (!fs.hasPatterns() && !fs.hasSelectors()) {
                     FileSet fs2 = (FileSet) fs.clone();
                     fs2.createInclude().setName("**/*.java");
                     if (includeNoSourcePackages) {
                         fs2.createInclude().setName("**/package.html");
                     }
                     rc = fs2;
                 }
             }
             for (Resource r : rc) {
                 sf.addElement(new SourceFile(r.as(FileProvider.class).getFile()));
             }
         }
     }
 
     /**
      * Add the directories matched by the nested dirsets to the Vector
      * and the base directories of the dirsets to the Path.  It also
      * handles the packages and excludepackages attributes and
      * elements.
      *
      * @since 1.5
      */
     private void parsePackages(Vector<String> pn, Path sp) {
         HashSet<String> addedPackages = new HashSet<String>();
         @SuppressWarnings("unchecked")
         Vector<DirSet> dirSets = (Vector<DirSet>) packageSets.clone();
 
         // for each sourcePath entry, add a directoryset with includes
         // taken from packagenames attribute and nested package
         // elements and excludes taken from excludepackages attribute
         // and nested excludepackage elements
         if (sourcePath != null) {
             PatternSet ps = new PatternSet();
             ps.setProject(getProject());
             if (packageNames.size() > 0) {
                 Enumeration<PackageName> e = packageNames.elements();
                 while (e.hasMoreElements()) {
                     PackageName p = e.nextElement();
                     String pkg = p.getName().replace('.', '/');
                     if (pkg.endsWith("*")) {
                         pkg += "*";
                     }
                     ps.createInclude().setName(pkg);
                 }
             } else {
                 ps.createInclude().setName("**");
             }
 
             Enumeration<PackageName> e = excludePackageNames.elements();
             while (e.hasMoreElements()) {
                 PackageName p = e.nextElement();
                 String pkg = p.getName().replace('.', '/');
                 if (pkg.endsWith("*")) {
                     pkg += "*";
                 }
                 ps.createExclude().setName(pkg);
             }
 
 
             String[] pathElements = sourcePath.list();
             for (int i = 0; i < pathElements.length; i++) {
                 File dir = new File(pathElements[i]);
                 if (dir.isDirectory()) {
                     DirSet ds = new DirSet();
                     ds.setProject(getProject());
                     ds.setDefaultexcludes(useDefaultExcludes);
                     ds.setDir(dir);
                     ds.createPatternSet().addConfiguredPatternset(ps);
                     dirSets.addElement(ds);
                 } else {
                     log("Skipping " + pathElements[i]
                         + " since it is no directory.", Project.MSG_WARN);
                 }
             }
         }
 
         Enumeration<DirSet> e = dirSets.elements();
         while (e.hasMoreElements()) {
             DirSet ds = e.nextElement();
             File baseDir = ds.getDir(getProject());
             log("scanning " + baseDir + " for packages.", Project.MSG_DEBUG);
             DirectoryScanner dsc = ds.getDirectoryScanner(getProject());
             String[] dirs = dsc.getIncludedDirectories();
             boolean containsPackages = false;
             for (int i = 0; i < dirs.length; i++) {
                 // are there any java files in this directory?
                 File pd = new File(baseDir, dirs[i]);
                 String[] files = pd.list(new FilenameFilter () {
                         public boolean accept(File dir1, String name) {
                             return name.endsWith(".java")
                                 || (includeNoSourcePackages
                                     && name.equals("package.html"));
                         }
                     });
 
                 if (files.length > 0) {
                     if ("".equals(dirs[i])) {
                         log(baseDir
                             + " contains source files in the default package,"
                             + " you must specify them as source files"
                             + " not packages.",
                             Project.MSG_WARN);
                     } else {
                         containsPackages = true;
                         String packageName =
                             dirs[i].replace(File.separatorChar, '.');
                         if (!addedPackages.contains(packageName)) {
                             addedPackages.add(packageName);
                             pn.addElement(packageName);
                         }
                     }
                 }
             }
             if (containsPackages) {
                 // We don't need to care for duplicates here,
                 // Path.list does it for us.
                 sp.createPathElement().setLocation(baseDir);
             } else {
                 log(baseDir + " doesn\'t contain any packages, dropping it.",
                     Project.MSG_VERBOSE);
             }
         }
     }
 
     private void postProcessGeneratedJavadocs() throws IOException {
         if (!postProcessGeneratedJavadocs) {
             return;
         }
         if (destDir != null && !destDir.isDirectory()) {
             log("No javadoc created, no need to post-process anything",
                 Project.MSG_VERBOSE);
             return;
         }
         final String fixData;
-        final InputStream in = getClass()
+        final InputStream in = Javadoc.class
             .getResourceAsStream("javadoc-frame-injections-fix.txt");
         if (in == null) {
             throw new FileNotFoundException("Missing resource "
                                             + "'javadoc-frame-injections-fix.txt' in "
                                             + "classpath.");
         }
         try {
             fixData =
                 fixLineFeeds(FileUtils
                              .readFully(new InputStreamReader(in, "US-ASCII")))
                 .trim();
         } finally {
             FileUtils.close(in);
         }
 
         final DirectoryScanner ds = new DirectoryScanner();
         ds.setBasedir(destDir);
         ds.setCaseSensitive(false);
         ds.setIncludes(new String[] {
                 "**/index.html", "**/index.htm", "**/toc.html", "**/toc.htm"
             });
         ds.addDefaultExcludes();
         ds.scan();
         int patched = 0;
         for (String f : ds.getIncludedFiles()) {
             patched += postProcess(new File(destDir, f), fixData);
         }
         if (patched > 0) {
             log("Patched " + patched + " link injection vulnerable javadocs",
                 Project.MSG_INFO);
         }
     }
 
     private int postProcess(File file, String fixData) throws IOException {
         String enc = docEncoding != null ? docEncoding
             : FILE_UTILS.getDefaultEncoding();
         // we load the whole file as one String (toc/index files are
         // generally small, because they only contain frameset declaration):
         InputStream fin = new FileInputStream(file);
         String fileContents;
         try {
             fileContents =
                 fixLineFeeds(FileUtils
                              .safeReadFully(new InputStreamReader(fin, enc)));
         } finally {
             FileUtils.close(fin);
         }
 
         // check if file may be vulnerable because it was not
         // patched with "validURL(url)":
         if (fileContents.indexOf("function validURL(url) {") < 0) {
             // we need to patch the file!
             String patchedFileContents = patchContent(fileContents, fixData);
             if (!patchedFileContents.equals(fileContents)) {
                 FileOutputStream fos = new FileOutputStream(file);
                 try {
                     OutputStreamWriter w = new OutputStreamWriter(fos, enc);
                     w.write(patchedFileContents);
                     w.close();
                     return 1;
                 } finally {
                     FileUtils.close(fos);
                 }
             }
         }
         return 0;
     }
 
     private String fixLineFeeds(String orig) {
         return orig.replace("\r\n", "\n")
             .replace("\n", StringUtils.LINE_SEP);
     }
 
     private String patchContent(String fileContents, String fixData) {
         // using regexes here looks like overkill
         int start = fileContents.indexOf(LOAD_FRAME);
         if (start >= 0) {
             return fileContents.substring(0, start) + fixData
                 + fileContents.substring(start + LOAD_FRAME_LEN);
         }
         return fileContents;
     }
 
     private class JavadocOutputStream extends LogOutputStream {
         JavadocOutputStream(int level) {
             super(Javadoc.this, level);
         }
 
         //
         // Override the logging of output in order to filter out Generating
         // messages.  Generating messages are set to a priority of VERBOSE
         // unless they appear after what could be an informational message.
         //
         private String queuedLine = null;
         private boolean sawWarnings = false;
         protected void processLine(String line, int messageLevel) {
             if (line.contains("warning")) {
                 sawWarnings = true;
             }
             if (messageLevel == Project.MSG_INFO
                 && line.startsWith("Generating ")) {
                 if (queuedLine != null) {
                     super.processLine(queuedLine, Project.MSG_VERBOSE);
                 }
                 queuedLine = line;
             } else {
                 if (queuedLine != null) {
                     if (line.startsWith("Building ")) {
                         super.processLine(queuedLine, Project.MSG_VERBOSE);
                     } else {
                         super.processLine(queuedLine, Project.MSG_INFO);
                     }
                     queuedLine = null;
                 }
                 super.processLine(line, messageLevel);
             }
         }
 
 
         protected void logFlush() {
             if (queuedLine != null) {
                 super.processLine(queuedLine, Project.MSG_VERBOSE);
                 queuedLine = null;
             }
         }
         
         public boolean sawWarnings() {
             return sawWarnings;
         }
     }
 
     /**
      * Convenience method to expand properties.
      * @param content the string to expand
      * @return the converted string
      */
     protected String expand(String content) {
         return getProject().replaceProperties(content);
     }
 
 }
