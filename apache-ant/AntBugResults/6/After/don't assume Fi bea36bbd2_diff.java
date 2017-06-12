diff --git a/WHATSNEW b/WHATSNEW
index 879f243d2..56336601a 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1022 +1,1028 @@
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
 
+ * DirectoryScanner and thus fileset/dirset will now silently drop all
+   filesystem objects that are neither files nor directories according
+   to java.io.File.  This prevents Ant from reading named pipes which
+   might lead to blocking or other undefined behavior.
+   Bugzilla Report 56149
+
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
 
  * Javadoc.postProcessGeneratedJavadocs() fails for Classes that
    extend Javadoc
    Bugzilla Report 56047
 
  * TarInputStream will now read archives created by tar
    implementations that encode big numbers by not adding a trailing
    NUL.
 
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
 
  * <redirector> has a new attribute binaryOutput that prevents Ant
    from splitting the output into lines.  This prevents binary output
    from being corrupted but may lead to error and normal output being
    mixed up.
    Bugzilla Report 55667
    Bugzilla Report 56156
 
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
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index 1c67f3f6f..f28f90944 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -25,1870 +25,1873 @@ import java.util.Arrays;
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
     private static final Set<String> defaultExcludes = new HashSet<String>();
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
     protected Vector<String> filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector<String> filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector<String> filesExcluded;
 
     /**
      * The directories which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector<String> dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector<String> dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector<String> dirsExcluded;
 
     /**
      * The files which matched at least one include and no excludes and
      * which a selector discarded.
      */
     protected Vector<String> filesDeselected;
 
     /**
      * The directories which matched at least one include and no excludes
      * but which a selector discarded.
      */
     protected Vector<String> dirsDeselected;
 
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
     private Set<String> scannedDirs = new HashSet<String>();
 
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
     private Map<String, TokenizedPath> includeNonPatterns = new HashMap<String, TokenizedPath>();
 
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
     private Map<String, TokenizedPath> excludeNonPatterns = new HashMap<String, TokenizedPath>();
 
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
     private Set<String> notFollowedSymlinks = new HashSet<String>();
 
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
         synchronized (defaultExcludes) {
             return (String[]) defaultExcludes.toArray(new String[defaultExcludes
                                                                  .size()]);
         }
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
         synchronized (defaultExcludes) {
             return defaultExcludes.add(s);
         }
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
         synchronized (defaultExcludes) {
             return defaultExcludes.remove(s);
         }
     }
 
     /**
      * Go back to the hardwired default exclude patterns.
      *
      * @since Ant 1.6
      */
     public static void resetDefaultExcludes() {
         synchronized (defaultExcludes) {
             defaultExcludes.clear();
             for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
                 defaultExcludes.add(DEFAULTEXCLUDES[i]);
             }
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
         Map<TokenizedPath, String> newroots = new HashMap<TokenizedPath, String>();
 
         // put in the newroots map the include patterns without
         // wildcard tokens
         for (int i = 0; i < includePatterns.length; i++) {
             String pattern = includePatterns[i].toString();
             if (!shouldSkipPattern(pattern)) {
                 newroots.put(includePatterns[i].rtrimWildcardTokens(),
                              pattern);
             }
         }
         for (Map.Entry<String, TokenizedPath> entry : includeNonPatterns.entrySet()) {
             String pattern = entry.getKey();
             if (!shouldSkipPattern(pattern)) {
                 newroots.put(entry.getValue(), pattern);
             }
         }
 
         if (newroots.containsKey(TokenizedPath.EMPTY_PATH)
             && basedir != null) {
             // we are going to scan everything anyway
             scandir(basedir, "", true);
         } else {
             File canonBase = null;
             if (basedir != null) {
                 try {
                     canonBase = basedir.getCanonicalFile();
                 } catch (IOException ex) {
                     throw new BuildException(ex);
                 }
             }
             // only scan directories that can include matched files or
             // directories
             for (Map.Entry<TokenizedPath, String> entry : newroots.entrySet()) {
                 TokenizedPath currentPath = entry.getKey();
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
                         accountForNotFollowedSymlink(currentPath, myfile);
                         continue;
                     }
                     if (myfile.isDirectory()) {
                         if (isIncluded(currentPath)
                             && currentelement.length() > 0) {
                             accountForIncludedDir(currentPath, myfile, true);
                         }  else {
                             scandir(myfile, currentPath, true);
                         }
-                    } else {
+                    } else if (myfile.isFile()) {
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
         filesIncluded    = new VectorSet<String>();
         filesNotIncluded = new VectorSet<String>();
         filesExcluded    = new VectorSet<String>();
         filesDeselected  = new VectorSet<String>();
         dirsIncluded     = new VectorSet<String>();
         dirsNotIncluded  = new VectorSet<String>();
         dirsExcluded     = new VectorSet<String>();
         dirsDeselected   = new VectorSet<String>();
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
         scandir(dir, path, fast, newfiles, new LinkedList<String>());
     }
 
     private void scandir(File dir, TokenizedPath path, boolean fast,
                          String[] newfiles, LinkedList<String> directoryNamesFollowed) {
         String vpath = path.toString();
         if (vpath.length() > 0 && !vpath.endsWith(File.separator)) {
             vpath += File.separator;
         }
 
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         if (!followSymlinks) {
             ArrayList<String> noLinks = new ArrayList<String>();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (SYMLINK_UTILS.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File file = new File(dir, newfiles[i]);
-                        (file.isDirectory()
-                            ? dirsExcluded : filesExcluded).addElement(name);
+                        if (file.isDirectory()) {
+                            dirsExcluded.addElement(name);
+                        } else if (file.isFile()) {
+                            filesExcluded.addElement(name);
+                        }
                         accountForNotFollowedSymlink(name, file);
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
-            } else { // dir
+            } else if (file.isDirectory()) { // dir
 
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
                                        LinkedList<String> directoryNamesFollowed) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name, fast, children, directoryNamesFollowed);
         }
     }
 
     private void accountForNotFollowedSymlink(String name, File file) {
         accountForNotFollowedSymlink(new TokenizedPath(name), file);
     }
 
     private void accountForNotFollowedSymlink(TokenizedPath name, File file) {
         if (!isExcluded(name) &&
             (isIncluded(name)
              || (file.isDirectory() && couldHoldIncluded(name)
                  && !contentsExcluded(name)))) {
             notFollowedSymlinks.add(file.getAbsolutePath());
         }
     }
 
     private void processIncluded(TokenizedPath path,
                                  File file, Vector<String> inc, Vector<String> exc,
                                  Vector<String> des) {
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
         for (Iterator<TokenizedPath> iter = includeNonPatterns.values().iterator();
              iter.hasNext(); ) {
             if (couldHoldIncluded(tokenizedName,
                                   iter.next().toPattern())) {
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
         String[] defaultExcludesTemp = getDefaultExcludes();
         newExcludes = new String[excludesLength + defaultExcludesTemp.length];
         if (excludesLength > 0) {
             System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
         }
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
     /* package-private */ Set<String> getScannedDirs() {
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
     private TokenizedPattern[] fillNonPatternSet(Map<String, TokenizedPath> map, String[] patterns) {
         ArrayList<TokenizedPattern> al = new ArrayList<TokenizedPattern>(patterns.length);
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
      * a symbolic link and enough other occurrences of the same name
      * higher up are symbolic links that point to the same place.</p>
      *
      * @since Ant 1.8.0
      */
     private boolean causesIllegalSymlinkLoop(String dirName, File parent,
                                              LinkedList<String> directoryNamesFollowed) {
         try {
             if (directoryNamesFollowed.size() >= maxLevelsOfSymlinks
                 && CollectionUtils.frequency(directoryNamesFollowed, dirName)
                    >= maxLevelsOfSymlinks
                 && SYMLINK_UTILS.isSymbolicLink(parent, dirName)) {
 
                 ArrayList<String> files = new ArrayList<String>();
                 File f = FILE_UTILS.resolveFile(parent, dirName);
                 String target = f.getCanonicalPath();
                 files.add(target);
 
                 String relPath = "";
                 for (String dir : directoryNamesFollowed) {
                     relPath += "../";
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
