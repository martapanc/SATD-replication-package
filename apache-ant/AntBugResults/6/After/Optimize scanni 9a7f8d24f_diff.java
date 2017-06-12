diff --git a/WHATSNEW b/WHATSNEW
index d6687ac32..ae6d47abb 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1272 +1,1277 @@
 Changes from Ant 1.5.4 to current CVS version
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * This version of Ant can not be built with JDK 1.1 and requires at
   least Java 1.2 at runtime as well.  Compiling for a 1.1 target is
   still supported.
 
 * Targets cannot have the empty string as their name any longer.
 
 * ant.jar's manifest does no longer include a Class-Path entry, so it
   is no longer possible to run Ant via "java -jar ant.jar" without
   manually altering the CLASSPATH.  Instead of that a file
   ant-bootstrap.jar is included in the etc directory of the binary
   distribution, copy this to the lib directory and use
   "java -jar ant-bootstrap.jar" instead if you want to run Ant without
   the wrapper script (not recommended).
 
 * The <script> task now requires Apache BSF instead of the older IBM
   version.  See <http://jakarta.apache.org/bsf/>
 
 * <xmlproperty> will no longer fail if the file to be loaded doesn't exist.
 
 * XML namespaces are now enabled in the XML parser, meaning XML namespace
   declarations no longer cause errors.
 
 * The <ftp> and <telnet> tasks now require Jakarta Commons Net instead
   of the older ORO Netcomponents version.  See
   <http://jakarta.apache.org/commons/net/index.html>.
 
 * <input> will no longer prompt the user and wait for input if the
   addproperty attribute is set to a property that has already been
   defined in the project.  If you rely on the task waiting for input,
   don't use the addproperty attribute.
 
 * The Class-Path attribute in manifests will no longer merge the
   entries of all manifests found, but will be treated like all other
   manifest attributes - the most recent attribute(s) will be used.
 
 * New Launch mechanism implemented. This moves some functionality from
   the batch files / shell scripts into Java. This removes environment
   limitations, for command issues, directory depth issues on Windows. Also
   allows a per-user library location to be used if the main Ant install
   is locked down.
 
 * The Entry nested element of PropertyFile will not any more have its value
   attribute (actually increment) overwritten with the new value of the entry
   after execution.
 
 * Output stored from a <java> or <exec> task is now exactly as generated. No
   conversion to platform end-of-line characters is performed.
 
 * <translate> will now preserve line endings.
 
 * <ftp> followsymlinks="false" in nested fileset definitions is explicitly
   required in order to exclude remote symbolic links (when doing a get, chmod,
   delete, rmdir).
 
 
 Fixed bugs:
 -----------
 * Filter readers were not handling line endings properly.  Bugzilla
   Report 18476.
 
 * Filtersets were also not handling line endings properly.
 
 * Expand tasks did not behave as expected with PatternSets.
 
 * <property environment=... /> now works on OS/400.
 
 * <cab> could hang listcab on large <fileset>s.
 
 * The starteam stcheckout, stcheckin tasks now correctly compute
   status of files against whatever local tree they are run against
   and, optionally, will not process a file if it is current.
   Previously you had to process everything unless you ran against the
   default folder which wasn't the normal use-case for ant-starteam.
   The stlist task now similarly displays that status correctly making
   it a more generally useful tool.
 
 * entity includes would cause exceptions if path names included spaces.
 
 * addConfiguredXXX would not work for TaskAdapter wrapped tasks
 
 * Fix <ilasm> outputfile testing so that the output file does not need
   to exist beforehand.
 
 * Ant will now exit with a return code of 1 if it encounters problems
   with the command line arguments.
 
 * ClassLoader creation changes to use a factory method in Project. A new
   class AntClassLoader2 implemented for 1.2+ specific features including
   Package information and addition of classes specified in the Class-Path
   element of a Jar's manifest.
 
 * It is now possible in <exec> to resolve the executable to a project
   basedir or execution dir relative executable. The resolveExecutable
   must be used to pick up such executables.
 
 * splash screen wouldn't disappear when build was finished.
 
 * <exec> output and error streams can now be redirected independently
   to either a property or a file (or both)
 
 * TarEntry's File-arg constructor would fail with a
   StringIndexOutOfBoundsException on all OSes where os.name is shorter
   than seven characters.  Bugzilla Report 18105.
 
 * <copy> and <move>'s failonerror didn't apply to filesets pointing to
   non-existant directories.  Bugzilla Report 18414.
 
 * The <stripjavacomments> filter sometimes removed parts of string
   constants.  Bugzilla Report 17441.
 
 * <antlr> will now recompile your grammar if the supergrammar has
   changed.  Bugzilla Report 12691.
 
 * <property env> will now work on Unices with /bin/env instead of
   /usr/bin/env.  Bugzilla Report 17642.
 
 * <jar index="on"> could include multiple index lists.  Bugzilla 10262.
 
 * The index created by <jar> didn't conform to the spec as it didn't
   include the top-level entries.  Bugzilla Report 16972.
 
 * <tar> and <zip> didn't honor the defaultexcludes attribute for the
   implicit fileset.  Bugzilla Report 18637.
 
 * The <replacetokens> filter would throw an exception if the token's
   value was an empty string.  Bugzilla Report 18625.
 
 * Perforce tasks relying on output from the server such as <p4change>
   and <p4label> were hanging. Bugzilla Reports 18129 and 18956.
 
 * Improve exception and logging behavior of Perforce tasks.
   Bugzilla report 18154.
 
 * build.sh install had a problem on cygwin (with REALANTHOME).
   Bugzilla Report 17257
 
 * <replaceregexp> didn't work for multi-byte encodings if byline was false.
   Bugzilla Report 19187.
 
 * file names that include spaces need to be quoted inside the @argfile
   argument using forked <javac> and (all JDKS).  Bugzilla Report 10499.
   NB : a first correction was only introducing quotes for JDK 1.4
   It has been changed to quote for all external compilers when paths
   contain spaces.
   Also the backslashes need to be converted to forward slashes
   Bugzilla Report 17683.
 
 * Setting filesonly to true in <zip> and related tasks would cause the
   archives to be always recreated.  Bugzilla Report 19449.
 
 * file names that include spaces need to be quoted inside the @argfile
   argument using <javadoc> and JDK 1.4.  Bugzilla Report 16871.
 
 * <junit> didn't work with custom formatters that were only available
   on the user specified classpath when a timeout occured.  Bugzilla
   Report 19953.
 
 * <different> selector : make ignoreFileTimes effectively default to true
   and fix a bug in the comparison of timestamps. Bugzilla Report 20205.
 
 * <different> selector can now be nested directly under a fileset
   Bugzilla Report 20220.
 
 * <cvstagdiff> had a problem with "dd-MM-yy hh:mm:ss" formats
   Bugzilla Report 15995.
 
 * <cvstagdiff> cvsroot and package attributes added to the root
   element tagdiff of the xml output
   Bugzilla Report 16081.
 
 * <fixcrlf> make fixcrlf create its temporary files in the default directory
   of FileUtils#createTempFile instead of the destination dir of fixcrlf.
   Bugzilla Report 20870.
 
 * <ejbjar> implementation for Borland.
   Prevent the task from being blocked by error messages coming from java2iiop.
   Bugzilla Report 19385.
 
 * <unzip>'s and <untar>'s nested patternsets didn't work as documented
   when the pattern ended in a slash or backslash.  Bugzilla Report 20969.
 
 * <fixcrlf> will now create the parent directories for the destination
   files if necessary.  Bugzilla Report 20840.
 
 * <xmlproperty> now handles CDATA sections. BugZilla Report 17195
 
 * <translate> now translate tokens that are placed close together.
   Bugzilla Report 17297
 
 * Nested websphere element for ejbjar does not support spaces in file name.
   Bugzilla Report 21298
 
 * Don't multiply Class-Path attributes when updating jars.  Bugzilla
   Report 21170.
 
 * Do not overwrite the value (increment) attribute of PropertyFile nested Entry element.
   Bugzilla Report 21505.
 
 * Prevent sysproperties with no key or no value from being added in <junit>.
   Bugzilla Report 21684.
 
 * Allow references to be properly inherited via antcall
   Bugzilla Report 21724.
 
 * ftp chmod failed when the remote system was UNIX and local system Windows
   Bugzilla Report 21865.
 
 * ftp did not set the ascii mode explicity, causing problems with ftp servers
   having binary as default
 
 * ftp was not able to download files when they were pointed to by symbolic links
   Bugzilla Report 14063.
 
 * ftp is able to download also directories pointed to by symbolic links.
 
 * replace would change \r\n into \r\r\n under Windows.
 
 * junitreport with frames did not display a link for classes without a package
   or in the top package.
   Bugzilla Report 21915.
 
 Other changes:
 --------------
 * Six new Clearcase tasks added.
 
 * A new filter reader namely tokenfilter has been added.  Bugzilla
   Report 18312.
 
 * A new attribute named skip is added to the TailFilter and
   HeadFilter filter readers.
 
 * The filesetmanifest attribute of <jar> has been reenabled.
 
 * The start and end tokens for <translate> may now be longer than a
   single character.
 
 * <setproxy> lets you set the username and password for proxies that
   want authentication
 
 * <loadproperties> has a new encoding attribute.
 
 * <echoproperties> can now create XML output.
 
 * <echoproperties> has a new srcfile attribute that can make it read
   properties files and output them instead of Ant's properties.
 
 * <filterset> will now resolve filters recursively.
 
 * <input> has a new attribute that allows you to specify a default value.
 
 * All tasks can be used outside of <target>s
 
 * Added <image> task (requires JAI).
 
 * New condition <isreference>
 
 * <ftp> now has a preservelastmodified attribute to preserve the
   timestamp of a downloaded file.
 
 * new rmdir action for <ftp> that removes directories from a fileset.
 
 * <ftp> has attributes timediffauto and timediffmillis to use together
   with the newer attribute to tell ant to take into account a time difference
   between client and remote side.
   Bugzilla Report 19358.
 
+* <ftp> has been optimized to go directly to the include patterns.
+  This reduces scanning time under UNIX when followsymlinks="true"
+  and casesensitive="true" (the default)
+  Bugzilla Report 20103.
+
 * The SOS and VSS tasks will no longer unconditionally prepend a $ to
   vsspath or projectpath.
 
 * OS/400 now gets detected by the os condition.
 
 * <arg> has a new attribute pathref that can be used to reference
   previously defined paths.
 
 * <xmlproperty> has been improved, you can now expand ${properties},
   define ids or paths and use Ant's location magic for filename resolutions
   in the XML file.
 
 * <xmlcatalog> will now support external catalogs according to the
   OASIS "Open Catalog" standard - if resolver.jar (newer than version
   1.0) from Apache's xml-commons is in your CLASSPATH.
 
 * Starteam tasks now have support for revision labels and build labels.
   Checkouts now have the option of using repository timestamps, instead
   of current.
 
 * new task <symlink> that creates and maintains symbolic links.
 
 * new tasks <chown> and <chgrp> which are wrappers of the Unix commands.
 
 * new task <attrib> to change file attributes on Windows systems.
 
 * <style> has a new attribute reloadstylesheet to work around a
   bug in widespread Xalan versions.
 
 * <tarfileset> has a new dirmode attribute to specify the permissions
   for directories.
 
 * <fixcrlf>'s eol attribute now also understands "mac", "unix" and "dos".
 
 * <classfileset> now picks up dependencies of the form MyClass.class. This
   works for the code generated by the Sun java compiler. It may not work for
   all compilers.
 
 * a new attribute "globalopts" can be added to all Perforce tasks.
   You can put in it all the strings described by p4 help usage. Refer to
   the docs for more information.
 
 * new Perforce tasks <p4integrate> , <p4resolve>, and <p4labelsync>
 
 * <p4submit> will change the property p4.change if the Perforce server
   renumbers the change list.
   It will set the property p4.needsresolve if the submit fails,
   and the message says that file(s) need to be resolved.
 
 * <replaceregexp> now has an optional encoding attribute to support
   replacing in files that are in a different encoding than the
   platform's default.
 
 * The <exec> task may now have its input redirected from either a file
   or a string from the build file. The error output can be separated
   to a different file when outut is redirected. standard error may be
   logged to the Ant log when redirecting output to a file
 
 * The <java> task also supports the input redirection and separate
   error streams introduced to the <exec> task. In addition, it is now
   possible to save the output into a property for use within the build
   file as was possible with <exec> in Ant 1.5
 
 * The <javadoc> task <tag> subelement has been enhanced to allow files
   with tag mappings to be used.
 
 * New tasks: <scp> supports file transfers, <sshexec> executes a
   command over SSH.  They require jsch, a BSD licensed SSH library that
   can be found at http://www.jcraft.com/jsch/index.html
 
 * New filterreader <escapeunicode/>.
 
 * Support for HP's NonStop Kernel (Tandem) OS has been added.
 
 * <cab>'s basedir attribute is now optional if you specify nested
   filesets.  Bugzilla Report 18046.
 
 * New task <sync> that synchronizes two directory trees.
 
 * <apply> has new forwardslash attribute that can force filenames to
   use forward slashes (/) as file separators even on platforms with a
   different separator.  This is useful if you want to run certain
   ported Unix tools.
 
 * Copy has a new outputencoding attribute that can be used to change
   the encoding while copying files.  Bugzilla Report 18217.
 
 * The xml formatter for JUnit will now honor test case names set with
   setName.  Bugzilla Report 17040.
 
 * JUnit now has an attribute reloading, which, when set to false,
   makes the task reuse the same class loader for a series of tests.
 
 * <concat> now supports filtering and can check timestamps before
   overriding a file.  Bugzilla Report 18166.
 
 * <junit> has a new attribute tempdir that controls the placement of
   temporary files.  Bugzilla Report 15454.
 
 * <jdepend> now supports a new nested element <classespath> which is
   the same as <sourcespath> but point to compiled classes (the
   prefered mode of operation for JDepend > 2.5).  Additionally, nested
   <exclude> elements can be used to exclude certain packages from
   being parsed.  Bugzilla Report 17134.
 
 * The JProbe tasks now also work with JProbe 4.x.  Bugzilla Report 14849.
 
 * <javacc> and <jjtree> will now autodetect JavaCC 3.x and can use it.
 
 * <sql> has a new attribute to control escape processing.
 
 * <sql> is able to display properly several resultsets if you are
   running a compound sql statement. Bugzilla Report 21594.
 
 * A new <containsregexp> selector has been added, that selects files
   if their content matches a certain regular expression.
 
 * <antlr>'s debug attribute has been enabled.  Bugzilla Report 19051.
 
 * <mail> has a new attribute charset. Bugzilla Report 15434.
 
 * <mail> has new attributes user and password for SMTP auth.
   maillogger can also use this.
   The implementation only works with JavaMail (encoding="MIME").
   Implementation with plain mail remains to do.
   Bugzilla Report 5969.
 
 * <mail> and mailloger support SMTP over TLS/SSL
   Bugzilla Report 19180.
 
 * <mail> the attributes from, replyto ,tolist, cclist, bcclist
   can now contain email addresses of the form name <address@xyz.com>
   or (name) address@xyz.com
   Bugzilla Report 22474.
 
 * <mail> (version PlainMail)
   prevent blank headers from being sent,
   make the order of the headers of plain mail messages predictable
   Bugzilla Report 22088.
 
 * <zipfileset> can now be defined in the main body of a project
   and referred to with refid="xyz". Bugzilla Report 17007.
 
 * A wrapper script for OS/2 has been added.
 
 * <unzip> will now detect and successfully extract self-extracting
   archives.  Bugzilla Report 16213.
 
 * <stcheckout> has a new attribute "converteol" that can be used to
   control the automatic line-end conversion performed on ASCII files.
   Bugzilla Report 18884.
 
 * Users can now modify the list of default excludes using the new
   defaultexcludes task.  Bugzilla Report 12700.
 
 * There is a new data type <propertyset> that can be used to collect
   properties.  It is supported by <ant>, <antcall>, <subant>, <java>,
   <echoproperties> and <junit>.
 
 * <concat> can now control the encoding of the output as well and optionally
   add new-line characters at the end of files that get concatenated but
   don't end in newlines.  Bugzilla Report 12511.
 
 * <rpm> will detect the rpmbuild executable of RedHat 8.0 and newer
   and use that if it is on your PATH.  Bugzilla Report 14650.
 
 * A new task <rexec> has been added that requires commons-net to work.
   Bugzilla Report 19541.
 
 * <javadoc> now supports a nested <arg> element in addition to the
   additionalparams attribute.
 
 * You can now determine the order of standard tags in <javadoc> via
   <tag> elements - you must not use the description attribute for them.
   Bugzilla Report 18912.
 
 * <javadoc> now supports the -noqualifier switch.  Bugzilla Report 19288.
 
 * <javac>'s executable attribute can now also be used to specify the
   executable for jikes, jvc, sj or gcj.  Bugzilla Report 13814.
 
 * <javac> has a new attribute tempdir that can control the placement
   of temporary files.  Bugzilla Report 19765.
 
 * A new magic property build.compiler.jvc.extensions has been added
   that can be used to turn of Microsoft extensions while using the jvc
   compiler.  Bugzilla Report 19826.
 
 * You can now limit the parallelism of <apply> and <chmod> by using the new
   maxparallel attribute.
 
 * With the new addsourcefile attribute, you can make <apply> ommit the
   source file names from the command line.  Bugzilla Report 13654.
 
 * <apply> and <chmod> now support nested <filelist>s as well as <dirset>s.
   Bugzilla Reports 15929 and 20687.
 
 * <apply> and <chmod> will display a summary if you set the new
   verbose attribute to true.  Bugzilla Report 19883.
 
 * <copy>/<move>'s failonerror attribute can now also be used to
   continue the build if an I/O error caused a problem.  Bugzilla
   Report 12999.
 
 * new selector <type/> allowing to select only files or only directories.
   Bugzilla Report 20222.
 
 * <java> and <junit> now support a nested <bootclasspath> element that
   will be ignored if not forking a new VM.
 
 * <junit>'s nested <formatter> elements now support if/unless clauses.
 
 * <ejbjar>
   cmpversion attribute added
   jboss element will look for jbosscmp-jdbc.xml descriptor
   if ejbjar has cmpversion="2.0" set
   Bugzilla Reports 14707 and 14709.
 
 * <pvcs> config attribute added to set the location of a specific PVCS
   .cfg file
   Bugzilla Report 9752
 
 * <mapper> has an "unpackage" mapper
   Bugzilla Report 18908
 
 * Added <scriptdef> task allowing tasks to be defined using any BSF-supported
   scripting language.
 
 * <touch>'s datetime attribute can now accept time with a granularity
   of seconds as well.  Bugzilla Report 21014.
 
 * <checksum> has two new properties: totalproperty and todir.
 
 * FileUtils#createTempFile will now create temporary files in the
   directory pointed to by the property java.io.tmpdir
 
 * <unzip> and friends now supports an optional encoding attribute to
   enable it to expand archives created with filenames using an encoding
   other than UTF8.  Bugzilla Report 10504.
 
 * <patch> has a new attribute destfile that can be used to create a new
   file instead of patching files in place.
 
 * OpenVMS is detected as a valid OS family.
 
 * DirectoryScanner has been optimized for cases where include patterns do not
   start with wildcards.  Bugzilla Report 20103.
 
 * DirectoryScanner begins to be optimized not to scan excluded directories.
   Bugzilla Report 21941.
 
 * Added keep-going feature. Bugzilla Report 21144
 
 * The archives generated by <zip> and friends will now contain CRC and
   size information in the "local file header", thereby providing this
   information to applications that read the archives using
   java.util.ZipInputStream.  Bugzilla Report 19195.
 
 * <copy> and <move> can now handle mappers that return multiple
   mappings per source path. This behaviour is enabled by using
   an enablemultiplemapping attribute. Bugzilla Report 21320.
 
 * <exec> will now work on OpenVMS (please read the notes in
   <exec>'s manual page).  Bugzilla Report 21877.
 
 * <exec> will now have a new attribute spawn (default false).
 If set to true, the process will be spawned. Bugzilla Report 5907.
 
 * <java> will now have a new attribute spawn (default false).
 If set to true, the process will be spawned. Bugzilla Report 5907.
 
 * <parallel> now supports a timeout which can be used to recover
   from deadlocks, etc in the parallel threads. <parallel> also
   now supports a <daemons> nested element. This can be used to
   run tasks in daemon threads which the parallel task will not
   wait for before completing. A new attribute failonany will cause
   <parallel> to throw an exception if any thread fails without
   waiting for all other threads to complete.
 
 * <zip> and friends will consume far less memory than they used to
   when run with compress="false".  Bugzilla Report 21899.
 
 * <if/> and <unless/> attributes added to <param/> element of <style>
    Bugzilla Report 22044
 
 * <zip> and friends have a new attribute "keepcompression" that can be
   used to incrementally build an archive mixing compressed and uncompressed
   entries.
 
 Changes from Ant 1.5.3 to Ant 1.5.4
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * If the Visual Age tasks used to work for you, they may stop doing so
   now - and we'd like to know about it.  The current set of tasks is
   supposed to work with any version of VAJ starting with 3.0.
 
 Fixed bugs:
 -----------
 
 * The Visual Age for Java tasks didn't work (at least for versions 3.0
   and higher).  Bugzilla Report 10016.
 
 * URL-encoding in <vaj*port> didn't work properly.
 
 * VAJRemoteUtil called getAbsolutePath instead of getPath
   causing problems when using a Windows VAJ server from a UNIX server.
   Bugzilla Report 20457.
 
 * VAJImport task failed with NullPointerException when using DirectoryScanner.
   Bugzilla Report 22080.
 
 Other changes:
 --------------
 
 * Shipped XML parser is now Xerces 2.5.0
 
 * <javah> will invoke oldjavah on JDK 1.4.2.  Bugzilla Report 18667.
 
 * The VAJ tasks now support a haltonfailure attribute to conditionally
   keep building even if they fail.
 
 * It is now possible to use the latest (versioned or unversioned) edition
   in <vajload> by using special wildcard characters.  Also fixes
   Bugzilla Report 2236.
 
 Changes from Ant 1.5.2 to Ant 1.5.3
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * The <zip> task and friends have again changed a method signature
   (sorry, was necessary to fix bug 17780).  The return type of
   getResourcesToAdd has changed.
 
 Fixed bugs:
 -----------
 
 * <zipfileset>'s filemode would get ignored and the dirmode was used
   for the included files as well.  As a side effect, WinZIP was unable
   to extract or display the files, so they seemed to be missing from
   the archive.  Bugzilla Report 17648.
 
 * <ftp> could use the wrong path separator when trying to change the
   remote working directory.  Bugzilla Report 17735.
 
 * <jar update="true"> would loose all original files if you didn't
   specify any nested <(zip)fileset>s and the manifest had changed.
   Bugzilla Report 17780.
 
 * If you used a value starting with \ on Windows for the appxml
   attribute of <ear> or the webxml attribute of <war>, it would be
   ignored.  Bugzilla Report 17871.
 
 * Ant will no longer implicitly add Sun's rt.jar in <javac> when you
   use jvc and don't specify a bootclasspath.  Bugzilla Report 18055.
 
 * The prefix attribute of <zipfileset> would not generate directory
   entries for the prefix itself.  Bugzilla Report 18403.
 
 * starteam checkout can now handle deleted labels.  Bugzilla Report 17646.
 
 * The Unix wrapper script failed if you invoked it as a relative
   symlink and ANT_HOME has not been set.  Bugzilla Report 17721.
 
 Other Changes:
 --------------
 * Added ability to specify manifest encoding for the <jar> and
   <manifest> tasks
 
 Changes from Ant 1.5.1 to Ant 1.5.2
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 * ANT_OPTS environment variable is now applied at the start of the
   Java command line, allowing position specific parameters of some
   JVMs, such as -classic to be specified.
 
 * ZipScanner#getIncludedFiles will now return the names of the ZipEntries
   that have been matched instead of the name of the archive.
 
 * The <zip> task and friends have been heavily modified, almost every
   method signature of the Zip class has changed.  If you have subclassed
   Zip (or one of its subclasses), your class will most likely not
   compile against the current code base.  If it still compiles, it will
   probably not work as in Ant 1.5.1.
 
 Fixed bugs:
 -----------
 * <translate> was not ignoring comment lines.
 
 * <manifest> wouldn't update an existing manifest if only an attribute
   of an existing section changed.
 
 * ant.bat now supports the ANT_ARGS and JAVACMD environment variables
   again (like Ant 1.5 did).
 
 * The "plain" <junit> <formatter> could throw a NullPointerException
   if an error occured in setUp.
 
 * <junit> will now produce output when a test times out as well.
 
 * <replace> would count some internal character replacements when
   reporting the number of replaced tokens.
 
 * <concat> would cause an exception if a <filelist> pointed to files
   that do not exist.
 
 * <javadoc> will now pass -source to custom doclets as well.
 
 * <cvstagdiff> would throw a NullPointException if there had been no
   differences.
 
 * <cvschangelog> could miss today's changes.
 
 * <concat> could append newline characters between concatenated files.
 
 * <xmlvalidate> ignored the specified encoding of the files to
   validate.
 
 * the errorsbeginat attribute of the <http> condition didn't work.
 
 * Ant will try to force loading of certain packages like com.sun.*
   from the system classloader.  The packages are determined by the
   version of the JVM running Ant.
 
 * Ant didn't find the runtime libraries on IBM's JDK 1.4 for Linux.
 
 * random component of temporary files is now always a positive integer.
 
 * Ant could incorrectly try to use the 1.4 regexp implementation even
   if it isn't available if you run the JVM with -Xverify:none.
 
 * Ant would die with an exception if you used nested <reference>
   elements in Ant and the refid attribute didn't point to an existing
   project reference.
 
 * The <get> task can now be compiled (and Ant thus bootstrapped) using
   Kaffee.
 
 * build.sysclasspath will now be honored by more tasks.
 
 * The signjar keystore attribute has been reverted to a String allowing
   it to once again accept URLs. This should not affect current File based usage
   unless you are extending the Signjar task.
 
 * <jar update="true"> would remove the original manifest.
 
 * fix up folder creation in PVCS task
 
 * <tar>'s up-to-date check didn't work for nested <(tar)fileset>s.
 
 * Corrected a problem in XMLLogger where it would not associated
   messages with a taskdef'd task
 
 * <uptodate> now works when using attributes (i.e. not filesets) and pointing
   to the same file
 
 * Java task (and output system) now stores output which doos not end
   with a line feed.
 
 * splash screen wouldn't disappear when build was finished.
 
 * <exec> now supports OS/2.
 
 * <zip> and friends would only update/recreate existing archives if
   the files to add/update have been newer than the archive.
 
 * <javadoc>'s <link> element could fail for offline="true" on some JDKs.
 
 Other changes:
 --------------
 
 * MailLogger now sets the Date header correctly.
 
 * Shipped XML parser is now Xerces 2.3.0
 
 * signjar now accepts a maxmemory attribute to allow the memory allocated to the
   jarsigner tool to be specified. The jarsigner from the JDK's JAVA_HOME bin
   dir is now used rather than the first jarsigner on the path.
 
 * **/.DS_Store has been added to the list of default pattern excludes.
 
 * The Created-By header in the default manifest now contains the JVM
   vendor and version according to the jar specification. A new header,
   Ant-Version provides the Ant version used to create the jar.
 
 * <zip> can now store Unix permissions in a way that can be
   reconstructed by Info-Zip's unzip command.
 
 Changes from Ant 1.5.1Beta1 to 1.5.1
 ====================================
 
 Fixed bugs:
 -----------
 
 * <tstamp>'s prefix attribute failed to apply to nested <format> elements.
 
 * <junitreport> created an empty junit-noframes.html if no format had
   been specified.
 
 * <basename> would remove more than it should if the file name
   contained more than one dot.
 
 * <filterset>s nested into <filterset>s didn't work.
 
 Other changes:
 --------------
 
 * Shipped XML parser is now Xerces 2.2.0
 
 * Filesets now support a 'file' attribute, allowing a single-file
   fileset to be constructed without having to specify its parent
   directory separately.
 
 * <junit> will now return the result of a call to getName instead of
   "unknown" for Test implementations that don't extend TestCase but have
   a public String getName() method.
 
 Changes from Ant 1.5 to 1.5.1Beta1
 ==================================
 
 Fixed bugs:
 -----------
 * Date/time in CvsChangeLog was in local timezone and 12 hour format leading
   to a problem when sorting by time. It is now UTC (GMT) and in 24-hour
   format as per cvs 'specifications'.
 
 * CvsTagDiff now supports ampersand modules or modules that have a different
   root directory than their name.
 
 * EjbJar threw NPEs for the Websphere element. The property 'websphere.home'
   was not documented.
 
 * Mail example in the documentation was not correct.
 
 * Checksum was broken in the following scenario:
   (using verifyproperty OR in a condition) AND using filesets
   with multiple files.
 
 * The ExpandProperties filter threw NPEs when defined using
   the <filterreader> format.
 
 * The sh wrapper script didn't work under Cygwin if ANT_HOME wasn't
   set with a Unix style filename.
 
 * The sh wrapper script could fail if you started Ant from a directory
   with whitespace in its name.
 
 * ant -diagnostics was not working properly when the task dependency
   was missing and was just printing the missing dependency.
 
 * If a task got redefined via <taskdef>, it lost its child elements.
 
 * <property>'s classpathref attribute was broken.
 
 * <arg line="''" /> would result in no command line argument, will now
   be a single empty argument.  Use <arg value="''"/> if you need the
   quotes literally.
 
 * <replaceregexp> could append a newline character at the end of the
   file.
 
 Other changes:
 --------------
 
 * Appendix E of Java Development with Ant (Loughran/Hatcher) was
   contributed to the docs.
 
 * <available> will only print deprecration warnings if it is actually
   used to change the value of a property.
 
 Changes from Ant 1.5beta3 to Ant 1.5
 ====================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * The filesetmanifest attribute added to <jar> after the 1.4.1
   release has been removed for now.  This change may affect only
   the 1.5Beta/1.6Alpha users.  An attempt will be made to add this
   feature back into Ant 1.6.
 
 Fixed bugs:
 -----------
 
 * <zip> and friends would always update existing archive if you set
   the update attribute to true.
 
 * To support backward compatibility with older versions, <pathconvert>
   will once again set the property, even if the result is the empty
   string, unless the new 'setonempty' attribute is set to false|no|off
   (default is "true").
 
 * The manifest task would crash XmlLogger
 
 Other changes:
 --------------
 
 * added **/.svn and **/.svn/** to the default excludes
 
 Changes from Ant 1.5beta2 to Ant 1.5beta3
 =========================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Project.getBuildListeners now returns a clone of the listener
   list. Changes to the returned list will not affect the listeners
   currently attached to the Project. It also means that it is safe to
   iterate over the returned list if listeners are added or removed
   during the traversal.
 
 * <pvcs> default filenameformat has been different from Ant 1.4.1.
   Now it is different from 1.5beta1 and 1.5beta2.
 
 * Some messages that are printed during startup will not be
   written to the logfile specified via -logfile as they might destroy
   the format of the file for special BuildLoggers (like XmlLogger).
 
 * <pathconvert> won't set the property if the result is the empty string.
 
 Fixed bugs:
 -----------
 
 * <available> could fail to find files or directories that happen to
   start with the name of the project's basedir but are not children of
   the basedir.
 
 * Nested <property>'s inside <ant> can now be overriden by subsequent
   <ant> and <antcall> tasks.
 
 * <xslt>'s outputtype attribute wouldn't do anything.
 
 * <linecontains> filterreader could swallow lines.
 
 * <sequential> used to configure the tasks (set their attributes)
   before the first task has been executed.  This means that properties
   that have been set by nested task seemed to be unset for the other
   tasks in the same <sequential> element.
 
 * <javac>'s sourcepath setting has been ignored by some compiler
   implementations.
 
 * <javadoc>'s packagelist attribute didn't work.
 
 * the plain mailer would always use port 25 in <mail>.
 
 * Ant's default logger could swallow empty lines.
 
 * ejbjar's iPlanet nested element now can process multiple descriptors.
 
 * IPlanetEjbc was looking in the wrong place for four iiop files.
 
 * <javac> would pass the -source switch to JDK 1.3's javac, even
   though it doesn't support it.
 
 Other changes:
 --------------
 
 * <checksum> now uses a buffer (of configurable size).
 
 * The "Trying to override task definition" warning has been degraded
   to verbose level if the two task definitions only differ in the class
   loader instance that has loaded the definition.
 
 * Add a jvmargs to the ejbjar's weblogic element to allow additional
   arguments to be provided to the VM runnign ejbc. Document the
   jvmdebuglevel attribute which can be used to avoid warnings about
   interface classess being found on the classpath. Document the new
   <sysproperty> element which allows JVM properties to be defined.
   Added an outputdir attribute to allow the destination to be a
   directory into which the exploded jar is written.
 
 * ejbjar now supports Borland Enterprise Server 5 and Jonas 2.5
 
 Changes from Ant 1.5beta1 to Ant 1.5beta2
 =========================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Properties will now be expanded in mail message bodies.  This means
 that one $ sign will be stripped if your mail message contains the text
 $$.
 
 * org.apache.tools.ant.taskdefs.Expand no longer extends MatchingTask.
 
 * Available#setFile now again uses a File argument as it did in 1.4,
 this may break environments that have been adapted to the String
 argument version present in 1.5beta1.
 
 Fixed bugs:
 -----------
 * When <move> attempts a rename, it deletes the destination file, if it
   exists, before renaming the source file.  However, <move> was not
   checking if the destination file was actually a directory before
   trying to delete it.
 
 * Make CVS Tasks to work under Cygwin.
 
 * Fix LineContains to handle huge files elegantly without causing
 Stack Overflows.
 
 * if you ask for the "classic" compiler on Java1.4, you get upgraded to
 "modern" because there is no classic compiler any more.
 
 * the <http> condition was viewing 404 'not found' exceptions as success. Now
 it defaults to viewing any response >=400 as an error, and has an errorsBeginAt
 attribute you can use if you want a higher or lower value.
 
 * <get> throws a build exception on an http authorization error, unless you
 have set ignoreerrors to true.
 
 * <wsdltodotnet> was spelt in Wintel case: <WsdlToDotnet>. It is now lower
 case, though the old spelling is retained for anyone who used it.
 
 * Merging of Manifests in jar now works as documented.
 
 * paths that have been separated by colons would be incorrectly parsed
 on NetWare.
 
 * runant.pl now supports NetWare.
 
 * <tempfile> and <setproxy> tasks were in beta1, but not defined by
 default; They now are. <tempfile> fills a property with the name of a
 temporary file; <setproxy> lets you set the JVM's http, ftp and socks proxy
 settings.
 
 * <available classname="foo" ignoresystemclasses="true"> failed for
 JDK 1.1 and 1.2, even if the class could be found on the
 user-specified classpath.
 
 * <property environment=... /> now works on z/OS.
 
 * forked <javac> failed for the wrong reason on JDK 1.1 - Ant would
 use a temporary file to hold the names of the files to compile under
 some conditons, but 1.1 doesn't support this feature.  Ant will no
 longer try this, but you may run into problems with the length of the
 command line now.
 
 * the refid attribute for <property>s nested into <ant> or <param>s
 nested into <antcall> didn't work.
 
 * <replaceregexp> didn't work for nested <fileset>s.
 
 * <javadoc> dropped sourcepath entries if no "interesting" .java
 source files  could be found below them.  This has been backwards
 incompatible and caused problems with custom doclets like xdoclet.
 
 * Using the doclet, docletpath or docletpathref attributes of
 <javadoc> may have caused NullPointerExceptions.
 
 * nested <filesets> of <javadoc> would include too much.
 
 * <dependset> will no longer choke on <targetfileset>s that point to
 non-existing directories.
 
 * <patch> didn't work at all.
 
 * <replace> and <replaceregexp> now fail if the file they are working
 on is locked.
 
 * <javadoc> would pick up the wrong executable in the combination JDK
 1.2  and AIX.
 
 Other changes:
 --------------
 
 * z/OS now gets detected by the os condition.
 
 * <fileset> and <dirset> now have an optional followsymlink attribute
 that can prevent Ant from following symbolic links on some platforms.
 
 * BeanShell is now supported in the <script> task.
 
 * <ejbjar> under Weblogic attempts to use the ejbc20 compiler for 2.0 beans
   based on the deployment descriptor's DTD reference. Under weblogic 7.00 Beta
   this ejbc class has been deprecated. To avoid the deprecation warning use
   ejbcclass="weblogic.ejbc".
 
 * <ejbjar> will add a manifest to the generated jar based on the naming
   convention in use. This overrides the manifest specified in the
   <ejbjar> attribute
 
 
 Changes from Ant 1.4.1 to 1.5beta1
 ==================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Important: Single $ signs are no longer silently stripped!
 =======
 * Project.getBuildListeners now returns a clone of the listener
   list. Changes to the returned list will not affect the listeners
   currently attached to the Project. It also means that it is safe to
   iterate over the returned list if listeners are added or removed
   during the traversal.
 
 * <pvcs> default filenameformat has been different from Ant 1.4.1.
   Now it is different from 1.5beta1 and 1.5beta2.
 
 * Some messages that are printed during startup will not be
   written to the logfile specified via -logfile as they might destroy
   the format of the file for special BuildLoggers (like XmlLogger).
 
 Fixed bugs:
 -----------
 
 * <available> could fail to find files or directories that happen to
   start with the name of the project's basedir but are not children of
   the basedir.
 
 * Nested <property>'s inside <ant> can now be overriden by subsequent
   <ant> and <antcall> tasks.
 
 * <xslt>'s outputtype attribute wouldn't do anything.
 
 * <linecontains> filterreader could swallow lines.
 
 * <sequential> used to configure the tasks (set their attributes)
   before the first task has been executed.  This means that properties
   that have been set by nested task seemed to be unset for the other
   tasks in the same <sequential> element.
 
 * <javac>'s sourcepath setting has been ignored by some compiler
   implementations.
 
 * <javadoc>'s packagelist attribute didn't work.
 
 * the plain mailer would always use port 25 in <mail>.
 
 * Ant's default logger could swallow empty lines.
 
 * ejbjar's iPlanet nested element now can process multiple descriptors.
 
 * IPlanetEjbc was looking in the wrong place for four iiop files.
 
 * <javac> would pass the -source switch to JDK 1.3's javac, even
   though it doesn't support it.
 
 Other changes:
 --------------
 
 * <checksum> now uses a buffer (of configurable size).
 
 * The "Trying to override task definition" warning has been degraded
   to verbose level if the two task definitions only differ in the class
   loader instance that has loaded the definition.
 
 * Add a jvmargs to the ejbjar's weblogic element to allow additional
   arguments to be provided to the VM runnign ejbc. Document the
   jvmdebuglevel attribute which can be used to avoid warnings about
   interface classess being found on the classpath. Document the new
   <sysproperty> element which allows JVM properties to be defined.
   Added an outputdir attribute to allow the destination to be a
   directory into which the exploded jar is written.
 
 * ejbjar now supports Borland Enterprise Server 5 and Jonas 2.5
 
 Changes from Ant 1.5beta1 to Ant 1.5beta2
 =========================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Properties will now be expanded in mail message bodies.  This means
 that one $ sign will be stripped if your mail message contains the text
 $$.
 
 * org.apache.tools.ant.taskdefs.Expand no longer extends MatchingTask.
 
 * Available#setFile now again uses a File argument as it did in 1.4,
 this may break environments that have been adapted to the String
 argument version present in 1.5beta1.
 
 Fixed bugs:
 -----------
 * When <move> attempts a rename, it deletes the destination file, if it
   exists, before renaming the source file.  However, <move> was not
   checking if the destination file was actually a directory before
   trying to delete it.
 
 * Make CVS Tasks to work under Cygwin.
 
 * Fix LineContains to handle huge files elegantly without causing
 Stack Overflows.
 
 * if you ask for the "classic" compiler on Java1.4, you get upgraded to
 "modern" because there is no classic compiler any more.
 
 * the <http> condition was viewing 404 'not found' exceptions as success. Now
 it defaults to viewing any response >=400 as an error, and has an errorsBeginAt
 attribute you can use if you want a higher or lower value.
 
 * <get> throws a build exception on an http authorization error, unless you
 have set ignoreerrors to true.
 
 * <wsdltodotnet> was spelt in Wintel case: <WsdlToDotnet>. It is now lower
 case, though the old spelling is retained for anyone who used it.
 
 * Merging of Manifests in jar now works as documented.
 
 * paths that have been separated by colons would be incorrectly parsed
 on NetWare.
 
 * runant.pl now supports NetWare.
 
 * <tempfile> and <setproxy> tasks were in beta1, but not defined by
 default; They now are. <tempfile> fills a property with the name of a
 temporary file; <setproxy> lets you set the JVM's http, ftp and socks proxy
 settings.
 
 * <available classname="foo" ignoresystemclasses="true"> failed for
 JDK 1.1 and 1.2, even if the class could be found on the
 user-specified classpath.
 
 * <property environment=... /> now works on z/OS.
 
 * forked <javac> failed for the wrong reason on JDK 1.1 - Ant would
 use a temporary file to hold the names of the files to compile under
 some conditons, but 1.1 doesn't support this feature.  Ant will no
 longer try this, but you may run into problems with the length of the
 command line now.
 
 * the refid attribute for <property>s nested into <ant> or <param>s
 nested into <antcall> didn't work.
 
 * <replaceregexp> didn't work for nested <fileset>s.
 
 * <javadoc> dropped sourcepath entries if no "interesting" .java
 source files  could be found below them.  This has been backwards
 incompatible and caused problems with custom doclets like xdoclet.
 
 * Using the doclet, docletpath or docletpathref attributes of
 <javadoc> may have caused NullPointerExceptions.
 
 * nested <filesets> of <javadoc> would include too much.
 
 * <dependset> will no longer choke on <targetfileset>s that point to
 non-existing directories.
 
 * <patch> didn't work at all.
 
 * <replace> and <replaceregexp> now fail if the file they are working
 on is locked.
 
 * <javadoc> would pick up the wrong executable in the combination JDK
 1.2  and AIX.
 
 Other changes:
 --------------
 
 * z/OS now gets detected by the os condition.
 
 * <fileset> and <dirset> now have an optional followsymlink attribute
 that can prevent Ant from following symbolic links on some platforms.
 
 * BeanShell is now supported in the <script> task.
 
 * <ejbjar> under Weblogic attempts to use the ejbc20 compiler for 2.0 beans
   based on the deployment descriptor's DTD reference. Under weblogic 7.00 Beta
   this ejbc class has been deprecated. To avoid the deprecation warning use
   ejbcclass="weblogic.ejbc".
 
 * <ejbjar> will add a manifest to the generated jar based on the naming
   convention in use. This overrides the manifest specified in the
   <ejbjar> attribute
 
 
 Changes from Ant 1.4.1 to 1.5beta1
 ==================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Important: Single $ signs are no longer silently stripped!
 
 * The filesetmanifest attribute added to <jar> after the 1.4.1
   release has been removed for now.  This change may affect only
   the 1.5Beta/1.6Alpha users.  An attempt will be made to add this
   feature back into Ant 1.6.
 
 Fixed bugs:
 -----------
 
 * <zip> and friends would always update existing archive if you set
   the update attribute to true.
 
 * To support backward compatibility with older versions, <pathconvert>
   will once again set the property, even if the result is the empty
   string, unless the new 'setonempty' attribute is set to false|no|off
   (default is "true").
 
 * The manifest task would crash XmlLogger
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index d13130c68..ce62e282b 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1415 +1,1425 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "Ant" and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.util.FileUtils;
 
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
  * @author Arnout J. Kuiper
  * <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
  * @author Magesh Umasankar
  * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
  * @author <a href="mailto:levylambert@tiscali-dsl.de">Antoine Levy-Lambert</a>
  */
 public class DirectoryScanner
        implements FileScanner, SelectorScanner, ResourceFactory {
 
 
     /**
      * Patterns which should be excluded by default.
      *
      * <p>Note that you can now add patterns to the list of default
      * excludes.  Added patterns will not become part of this array
      * that has only been kept around for backwards compatibility
      * reasons.</p>
      *
      * @deprecated use the {@link #getDefaultExcludes
      * getDefaultExcludes} method instead.
      */
     protected static final String[] DEFAULTEXCLUDES = {
         // Miscellaneous typical temporary files
         "**/*~",
         "**/#*#",
         "**/.#*",
         "**/%*%",
         "**/._*",
 
         // CVS
         "**/CVS",
         "**/CVS/**",
         "**/.cvsignore",
 
         // SCCS
         "**/SCCS",
         "**/SCCS/**",
 
         // Visual SourceSafe
         "**/vssver.scc",
 
         // Subversion
         "**/.svn",
         "**/.svn/**",
 
         // Mac
         "**/.DS_Store"
     };
 
     /**
      * Patterns which should be excluded by default.
      *
      * @see #addDefaultExcludes()
      */
     private static Vector defaultExcludes = new Vector();
     static {
         resetDefaultExcludes();
     }
 
     /** The base directory to be scanned. */
     protected File basedir;
 
     /** The patterns for the files to be included. */
     protected String[] includes;
 
     /** The patterns for the files to be excluded. */
     protected String[] excludes;
 
     /** Selectors that will filter which files are in our candidate list. */
     protected FileSelector[] selectors = null;
 
     /** The files which matched at least one include and no excludes
      *  and were selected.
      */
     protected Vector filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector filesExcluded;
 
     /** The directories which matched at least one include and no excludes
      *  and were selected.
      */
     protected Vector dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector dirsExcluded;
 
     /** The files which matched at least one include and no excludes and
      *  which a selector discarded.
      */
     protected Vector filesDeselected;
 
     /** The directories which matched at least one include and no excludes
      *  but which a selector discarded.
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
      * Whether or not symbolic links should be followed.
      *
      * @since Ant 1.5
      */
     private boolean followSymlinks = true;
 
     /** Helper. */
     private static final FileUtils fileUtils = FileUtils.newFileUtils();
 
     /** Whether or not everything tested so far has been included. */
     protected boolean everythingIncluded = true;
 
     /**
      * Sole constructor.
      */
     public DirectoryScanner() {
     }
 
     /**
      * Tests whether or not a given path matches the start of a given
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
      * Tests whether or not a given path matches the start of a given
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
      * Tests whether or not a given path matches a given pattern.
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
      * Tests whether or not a given path matches a given pattern.
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
      * Tests whether or not a string matches against a pattern.
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
      * Tests whether or not a string matches against a pattern.
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
      *         <code>Vector</code>.
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
      * @return    <code>true</code> if the string was added
      *            <code>false</code> if it already
      *            existed.
      *
      * @since Ant 1.6
      */
     public static boolean addDefaultExclude(String s) {
         if (defaultExcludes.indexOf(s) == -1) {
             defaultExcludes.add(s);
             return true;
         }
         return false;
     }
 
     /**
      * Remove a string if it is a default exclude.
      *
      * @param s   The string to attempt to remove.
      * @return    <code>true</code> if <code>s</code> was a default
      *            exclude (and thus was removed),
      *            <code>false</code> if <code>s</code> was not
      *            in the default excludes list to begin with
      *
      * @since Ant 1.6
      */
     public static boolean removeDefaultExclude(String s) {
         return defaultExcludes.remove(s);
     }
 
     /**
      *  Go back to the hard wired default exclude patterns
      *
      * @since Ant 1.6
      */
     public static void resetDefaultExcludes() {
     defaultExcludes = new Vector();
 
         for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
             defaultExcludes.add(DEFAULTEXCLUDES[i]);
         }
     }
 
     /**
      * Sets the base directory to be scanned. This is the directory which is
      * scanned recursively. All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not match
      * <code>File.separatorChar</code>.
      *
      * @param basedir The base directory to scan.
      *                Must not be <code>null</code>.
      */
     public void setBasedir(String basedir) {
         setBasedir(new File(basedir.replace('/', File.separatorChar).replace(
                 '\\', File.separatorChar)));
     }
 
     /**
      * Sets the base directory to be scanned. This is the directory which is
      * scanned recursively.
      *
      * @param basedir The base directory for scanning.
      *                Should not be <code>null</code>.
      */
     public void setBasedir(File basedir) {
         this.basedir = basedir;
     }
 
     /**
      * Returns the base directory to be scanned.
      * This is the directory which is scanned recursively.
      *
      * @return the base directory to be scanned
      */
     public File getBasedir() {
         return basedir;
     }
 
     /**
-     * Sets whether or not the file system should be regarded as case sensitive.
+     * Find out whether include exclude patterns are matched in a
+     * case sensitive way
+     * @return whether or not the scanning is case sensitive
+     * @since ant 1.6
+     */
+    public boolean isCaseSensitive() {
+        return isCaseSensitive;
+    }
+    /**
+     * Sets whether or not include and exclude patterns are matched
+     * in a case sensitive way
      *
      * @param isCaseSensitive whether or not the file system should be
      *                        regarded as a case sensitive one
      */
     public void setCaseSensitive(boolean isCaseSensitive) {
         this.isCaseSensitive = isCaseSensitive;
     }
 
     /**
      * gets whether or not a DirectoryScanner follows symbolic links
      *
      * @return flag indicating whether symbolic links should be followed
      *
      * @since ant 1.6
      */
     public boolean isFollowSymlinks() {
         return followSymlinks;
     }
 
     /**
      * Sets whether or not symbolic links should be followed.
      *
      * @param followSymlinks whether or not symbolic links should be followed
      */
     public void setFollowSymlinks(boolean followSymlinks) {
         this.followSymlinks = followSymlinks;
     }
 
     /**
      * Sets the list of include patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param includes A list of include patterns.
      *                 May be <code>null</code>, indicating that all files
      *                 should be included. If a non-<code>null</code>
      *                 list is given, all elements must be
      * non-<code>null</code>.
      */
     public void setIncludes(String[] includes) {
         if (includes == null) {
             this.includes = null;
         } else {
             this.includes = new String[includes.length];
             for (int i = 0; i < includes.length; i++) {
                 String pattern;
                 pattern = includes[i].replace('/', File.separatorChar).replace(
                         '\\', File.separatorChar);
                 if (pattern.endsWith(File.separator)) {
                     pattern += "**";
                 }
                 this.includes[i] = pattern;
             }
         }
     }
 
 
     /**
      * Sets the list of exclude patterns to use. All '/' and '\' characters
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
     public void setExcludes(String[] excludes) {
         if (excludes == null) {
             this.excludes = null;
         } else {
             this.excludes = new String[excludes.length];
             for (int i = 0; i < excludes.length; i++) {
                 String pattern;
                 pattern = excludes[i].replace('/', File.separatorChar).replace(
                         '\\', File.separatorChar);
                 if (pattern.endsWith(File.separator)) {
                     pattern += "**";
                 }
                 this.excludes[i] = pattern;
             }
         }
     }
 
 
     /**
      * Sets the selectors that will select the filelist.
      *
      * @param selectors specifies the selectors to be invoked on a scan
      */
     public void setSelectors(FileSelector[] selectors) {
         this.selectors = selectors;
     }
 
 
     /**
      * Returns whether or not the scanner has included all the files or
      * directories it has come across so far.
      *
      * @return <code>true</code> if all files and directories which have
      *         been found so far have been included.
      */
     public boolean isEverythingIncluded() {
         return everythingIncluded;
     }
 
     /**
      * Scans the base directory for files which match at least one include
      * pattern and don't match any exclude patterns. If there are selectors
      * then the files must pass muster there, as well.
      *
      * @exception IllegalStateException if the base directory was set
      *            incorrectly (i.e. if it is <code>null</code>, doesn't exist,
      *            or isn't a directory).
      */
     public void scan() throws IllegalStateException {
         if (basedir == null) {
             throw new IllegalStateException("No basedir set");
         }
         if (!basedir.exists()) {
             throw new IllegalStateException("basedir " + basedir
                                             + " does not exist");
         }
         if (!basedir.isDirectory()) {
             throw new IllegalStateException("basedir " + basedir
                                             + " is not a directory");
         }
 
         if (includes == null) {
             // No includes supplied, so set it to 'matches all'
             includes = new String[1];
             includes[0] = "**";
         }
         if (excludes == null) {
             excludes = new String[0];
         }
 
         filesIncluded    = new Vector();
         filesNotIncluded = new Vector();
         filesExcluded    = new Vector();
         filesDeselected  = new Vector();
         dirsIncluded     = new Vector();
         dirsNotIncluded  = new Vector();
         dirsExcluded     = new Vector();
         dirsDeselected   = new Vector();
 
         if (isIncluded("")) {
             if (!isExcluded("")) {
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
     }
 
     /**
      * this routine is actually checking all the include patterns in
      * order to avoid scanning everything under base dir
      * @since ant1.6
      */
     private void checkIncludePatterns() {
         Hashtable newroots = new Hashtable();
         // put in the newroots vector the include patterns without
         // wildcard tokens
         for (int icounter = 0; icounter < includes.length; icounter++) {
             String newpattern =
                 SelectorUtils.rtrimWildcardTokens(includes[icounter]);
             newroots.put(newpattern, includes[icounter]);
         }
 
         if (newroots.containsKey("")) {
             // we are going to scan everything anyway
             scandir(basedir, "", true);
         } else {
             // only scan directories that can include matched files or
             // directories
             Enumeration enum2 = newroots.keys();
 
             File canonBase = null;
             try {
                 canonBase = basedir.getCanonicalFile();
             } catch (IOException ex) {
                 throw new BuildException(ex);
             }
 
             while (enum2.hasMoreElements()) {
                 String currentelement = (String) enum2.nextElement();
                 String originalpattern = (String) newroots.get(currentelement);
                 File myfile = new File(basedir, currentelement);
 
                 if (myfile.exists()) {
                     // may be on a case insensitive file system.  We want
                     // the results to show what's really on the disk, so
                     // we need to double check.
                     try {
                         File canonFile = myfile.getCanonicalFile();
                         String path = fileUtils.removeLeadingPath(canonBase,
                                                                   canonFile);
                         if (!path.equals(currentelement)) {
                             myfile = findFile(basedir, currentelement);
                             if (myfile != null) {
                                 currentelement =
                                     fileUtils.removeLeadingPath(basedir,
                                                                 myfile);
                             }
                         }
                     } catch (IOException ex) {
                         throw new BuildException(ex);
                     }
                 }
 
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive) {
                     File f = findFileCaseInsensitive(basedir, currentelement);
                     if (f.exists()) {
                         // adapt currentelement to the case we've
                         // actually found
                         currentelement = fileUtils.removeLeadingPath(basedir,
                                                                      f);
                         myfile = f;
                     }
                 }
 
                 if (myfile != null && myfile.exists()) {
                     if (!followSymlinks
                         && isSymlink(basedir, currentelement)) {
                         continue;
                     }
 
                     if (myfile.isDirectory()) {
                         if (isIncluded(currentelement)
                             && currentelement.length() > 0) {
                             accountForIncludedDir(currentelement, myfile, true);
                         }  else {
                             if (currentelement.length() > 0) {
                                 if (currentelement.charAt(currentelement
                                                           .length() - 1)
                                     != File.separatorChar) {
                                     currentelement =
                                         currentelement + File.separatorChar;
                                 }
                             }
                             scandir(myfile, currentelement, true);
                         }
                     } else {
                         if (isCaseSensitive
                             && originalpattern.equals(currentelement)) {
                             accountForIncludedFile(currentelement, myfile);
                         } else if (!isCaseSensitive
                                    && originalpattern
                                    .equalsIgnoreCase(currentelement)) {
                             accountForIncludedFile(currentelement, myfile);
                         }
                     }
                 }
             }
         }
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
         if (haveSlowResults) {
             return;
         }
 
         String[] excl = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(excl);
 
         String[] notIncl = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(notIncl);
 
         for (int i = 0; i < excl.length; i++) {
             if (!couldHoldIncluded(excl[i])) {
                 scandir(new File(basedir, excl[i]),
                         excl[i] + File.separator, false);
             }
         }
 
         for (int i = 0; i < notIncl.length; i++) {
             if (!couldHoldIncluded(notIncl[i])) {
                 scandir(new File(basedir, notIncl[i]),
                         notIncl[i] + File.separator, false);
             }
         }
 
         haveSlowResults  = true;
     }
 
     /**
      * Scans the given directory for files and directories. Found files and
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
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         String[] newfiles = dir.list();
 
         if (newfiles == null) {
             /*
              * two reasons are mentioned in the API docs for File.list
              * (1) dir is not a directory. This is impossible as
              *     we wouldn't get here in this case.
              * (2) an IO error occurred (why doesn't it throw an exception
              *     then???)
              */
             throw new BuildException("IO error scanning directory "
                                      + dir.getAbsolutePath());
         }
 
         if (!followSymlinks) {
             Vector noLinks = new Vector();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (fileUtils.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File   file = new File(dir, newfiles[i]);
                         if (file.isDirectory()) {
                             dirsExcluded.addElement(name);
                         } else {
                             filesExcluded.addElement(name);
                         }
                     } else {
                         noLinks.addElement(newfiles[i]);
                     }
                 } catch (IOException ioe) {
                     String msg = "IOException caught while checking "
                         + "for links, couldn't get cannonical path!";
                     // will be caught and redirected to Ant's logging system
                     System.err.println(msg);
                     noLinks.addElement(newfiles[i]);
                 }
             }
             newfiles = new String[noLinks.size()];
             noLinks.copyInto(newfiles);
         }
 
         for (int i = 0; i < newfiles.length; i++) {
             String name = vpath + newfiles[i];
             File   file = new File(dir, newfiles[i]);
             if (file.isDirectory()) {
                 if (isIncluded(name)) {
                     accountForIncludedDir(name, file, fast);
                 } else {
                     everythingIncluded = false;
                     dirsNotIncluded.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         scandir(file, name + File.separator, fast);
                     }
                 }
                 if (!fast) {
                     scandir(file, name + File.separator, fast);
                 }
             } else if (file.isFile()) {
                 if (isIncluded(name)) {
                     accountForIncludedFile(name, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             }
         }
     }
     /**
      * process included file
      * @param name  path of the file relative to the directory of the fileset
      * @param file  included file
      */
     private void accountForIncludedFile(String name, File file) {
         if (!filesIncluded.contains(name)
             && !filesExcluded.contains(name)
             && !filesDeselected.contains(name)) {
 
             if (!isExcluded(name)) {
                 if (isSelected(name, file)) {
                     filesIncluded.addElement(name);
                 } else {
                     everythingIncluded = false;
                     filesDeselected.addElement(name);
                 }
             } else {
                 everythingIncluded = false;
                 filesExcluded.addElement(name);
             }
         }
     }
 
     /**
      *
      * @param name path of the directory relative to the directory of
      * the fileset
      * @param file directory as file
      * @param fast
      */
     private void accountForIncludedDir(String name, File file, boolean fast) {
         if (!dirsIncluded.contains(name)
             && !dirsExcluded.contains(name)
             && !dirsDeselected.contains(name)) {
 
             if (!isExcluded(name)) {
                 if (isSelected(name, file)) {
                     dirsIncluded.addElement(name);
                     if (fast) {
                         scandir(file, name + File.separator, fast);
                     }
                 } else {
                     everythingIncluded = false;
                     dirsDeselected.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         scandir(file, name + File.separator, fast);
                     }
                 }
 
             } else {
                 everythingIncluded = false;
                 dirsExcluded.addElement(name);
                 if (fast && couldHoldIncluded(name)) {
                     scandir(file, name + File.separator, fast);
                 }
             }
         }
     }
     /**
      * Tests whether or not a name matches against at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         include pattern, or <code>false</code> otherwise.
      */
     protected boolean isIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPath(includes[i], name, isCaseSensitive)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Tests whether or not a name matches the start of at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of at
      *         least one include pattern, or <code>false</code> otherwise.
      */
     protected boolean couldHoldIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPatternStart(includes[i], name, isCaseSensitive)) {
                 if (isMorePowerfulThanExcludes(name, includes[i])) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     /**
      *  find out whether one particular include pattern is more powerful
      *  than all the excludes
      *  note : the power comparison is based on the length of the include pattern
      *  and of the exclude patterns without the wildcards
      *  ideally the comparison should be done based on the depth
      *  of the match, that is to say how many file separators have been matched
      *  before the first ** or the end of the pattern
      *
      *  IMPORTANT : this function should return false "with care"
      *
      *  @param name the relative path that one want to test
      *  @param includepattern  one include pattern
      *  @return true if there is no exclude pattern more powerful than this include pattern
      *  @since ant1.6
      */
     private boolean isMorePowerfulThanExcludes(String name, String includepattern) {
         String soughtexclude = name + File.separator + "**";
         for (int counter = 0; counter < excludes.length; counter++) {
             if (excludes[counter].equals(soughtexclude))  {
                 return false;
             }
         }
         return true;
     }
     /**
      * Tests whether or not a name matches against at least one exclude
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         exclude pattern, or <code>false</code> otherwise.
      */
     protected boolean isExcluded(String name) {
         for (int i = 0; i < excludes.length; i++) {
             if (matchPath(excludes[i], name, isCaseSensitive)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Tests whether a name should be selected.
      *
      * @param name the filename to check for selecting
      * @param file the java.io.File object for this filename
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
      * Returns the names of the files which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and none of the exclude patterns.
      */
     public String[] getIncludedFiles() {
         String[] files = new String[filesIncluded.size()];
         filesIncluded.copyInto(files);
         Arrays.sort(files);
         return files;
     }
 
     /**
      * Returns the names of the files which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched none of the include
      *         patterns.
      *
      * @see #slowScan
      */
     public String[] getNotIncludedFiles() {
         slowScan();
         String[] files = new String[filesNotIncluded.size()];
         filesNotIncluded.copyInto(files);
         return files;
     }
 
     /**
      * Returns the names of the files which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and at at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public String[] getExcludedFiles() {
         slowScan();
         String[] files = new String[filesExcluded.size()];
         filesExcluded.copyInto(files);
         return files;
     }
 
     /**
      * <p>Returns the names of the files which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the files which were deselected.
      *
      * @see #slowScan
      */
     public String[] getDeselectedFiles() {
         slowScan();
         String[] files = new String[filesDeselected.size()];
         filesDeselected.copyInto(files);
         return files;
     }
 
     /**
      * Returns the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      */
     public String[] getIncludedDirectories() {
         String[] directories = new String[dirsIncluded.size()];
         dirsIncluded.copyInto(directories);
         Arrays.sort(directories);
         return directories;
     }
 
     /**
      * Returns the names of the directories which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched none of the include
      * patterns.
      *
      * @see #slowScan
      */
     public String[] getNotIncludedDirectories() {
         slowScan();
         String[] directories = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(directories);
         return directories;
     }
 
     /**
      * Returns the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public String[] getExcludedDirectories() {
         slowScan();
         String[] directories = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(directories);
         return directories;
     }
 
     /**
      * <p>Returns the names of the directories which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the directories which were deselected.
      *
      * @see #slowScan
      */
     public String[] getDeselectedDirectories() {
         slowScan();
         String[] directories = new String[dirsDeselected.size()];
         dirsDeselected.copyInto(directories);
         return directories;
     }
 
     /**
      * Adds default exclusions to the current exclusions set.
      */
     public void addDefaultExcludes() {
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
      * Get the named resource
      * @param name path name of the file relative to the dir attribute.
      *
      * @return the resource with the given name.
      * @since Ant 1.5.2
      */
     public Resource getResource(String name) {
         File f = fileUtils.resolveFile(basedir, name);
         return new Resource(name, f.exists(), f.lastModified(),
                             f.isDirectory());
     }
 
     /**
      * temporary table to speed up the various scanning methods below
      *
      * @since Ant 1.6
      */
     private Map fileListMap = new HashMap();
 
     /**
      * Returns a cached result of list performed on file, if
      * available.  Invokes the method and caches the result otherwise.
      *
      * @since Ant 1.6
      */
     private String[] list(File file) {
         String[] files = (String[]) fileListMap.get(file);
         if (files == null) {
             files = file.list();
             if (files != null) {
                 fileListMap.put(file, files);
             }
         }
         return files;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in a case
      * insensitive manner in order to find a file that matches the
      * given name.
      *
      * @return File object that points to the file in question.  if it
      * hasn't been found it will simply be <code>new File(base,
      * path)</code>.
      *
      * @since Ant 1.6
      */
     private File findFileCaseInsensitive(File base, String path) {
         File f = findFileCaseInsensitive(base,
                                          SelectorUtils.tokenizePath(path));
         return  f == null ? new File(base, path) : f;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in a case
      * insensitive manner in order to find a file that matches the
      * given stack of names.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFileCaseInsensitive(File base, Vector pathElements) {
         if (pathElements.size() == 0) {
             return base;
         } else {
             if (!base.isDirectory()) {
                 return null;
             }
             String[] files = list(base);
             if (files == null) {
                 throw new BuildException("IO error scanning directory "
                                          + base.getAbsolutePath());
             }
             String current = (String) pathElements.remove(0);
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equals(current)) {
                     base = new File(base, files[i]);
                     return findFileCaseInsensitive(base, pathElements);
                 }
             }
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equalsIgnoreCase(current)) {
                     base = new File(base, files[i]);
                     return findFileCaseInsensitive(base, pathElements);
                 }
             }
         }
         return null;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given name.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFile(File base, String path) {
         return findFile(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given stack of names.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFile(File base, Vector pathElements) {
         if (pathElements.size() == 0) {
             return base;
         } else {
             if (!base.isDirectory()) {
                 return null;
             }
             String[] files = list(base);
             if (files == null) {
                 throw new BuildException("IO error scanning directory "
                                          + base.getAbsolutePath());
             }
             String current = (String) pathElements.remove(0);
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equals(current)) {
                     base = new File(base, files[i]);
                     return findFile(base, pathElements);
                 }
             }
         }
         return null;
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, String path) {
         return isSymlink(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, Vector pathElements) {
         if (pathElements.size() > 0) {
             String current = (String) pathElements.remove(0);
             try {
                 if (fileUtils.isSymbolicLink(base, current)) {
                     return true;
                 } else {
                     base = new File(base, current);
                     return isSymlink(base, pathElements);
                 }
             } catch (IOException ioe) {
                 String msg = "IOException caught while checking "
                     + "for links, couldn't get cannonical path!";
                 // will be caught and redirected to Ant's logging system
                 System.err.println(msg);
                 return false;
             }
         }
         return false;
     }
 
     /**
      * List of all scanned directories.
      *
      * @since Ant 1.6
      */
     private Set scannedDirs = new HashSet();
 
     /**
      * Has the directorty with the given path relative to the base
      * directory allready been scanned?
      *
      * <p>Registers the given directory as scanned as a side effect.</p>
      *
      * @since Ant 1.6
      */
     private boolean hasBeenScanned(String vpath) {
         return !scannedDirs.add(vpath);
     }
 
     /**
      * Clear internal caches.
      *
      * @since Ant 1.6
      */
     private void clearCaches() {
         fileListMap.clear();
         scannedDirs.clear();
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
index 2fe82ac61..738510512 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
@@ -1,1316 +1,1898 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "Ant" and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 package org.apache.tools.ant.taskdefs.optional.net;
 
 import org.apache.commons.net.ftp.FTPClient;
 import org.apache.commons.net.ftp.FTPFile;
 import org.apache.commons.net.ftp.FTPReply;
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
+import java.util.Enumeration;
+import java.util.HashMap;
+import java.util.Hashtable;
+import java.util.HashSet;
 import java.util.Locale;
+import java.util.Map;
+import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.Vector;
+
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Delete;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
+import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Basic FTP client. Performs the following actions:
  * <ul>
  *   <li> <strong>send</strong> - send files to a remote server. This is the
  *   default action.</li>
  *   <li> <strong>get</strong> - retrieve files from a remote server.</li>
  *   <li> <strong>del</strong> - delete files from a remote server.</li>
  *   <li> <strong>list</strong> - create a file listing.</li>
  *   <li> <strong>chmod</strong> - change unix file permissions.</li>
  *   <li> <strong>rmdir</strong> - remove directories, if empty, from a
  *   remote server.</li>
  * </ul>
  * <strong>Note:</strong> Some FTP servers - notably the Solaris server - seem
  * to hold data ports open after a "retr" operation, allowing them to timeout
  * instead of shutting them down cleanly. This happens in active or passive
  * mode, and the ports will remain open even after ending the FTP session. FTP
  * "send" operations seem to close ports immediately. This behavior may cause
  * problems on some systems when downloading large sets of files.
  *
  * @author Roger Vaughn <a href="mailto:rvaughn@seaconinc.com">
  *      rvaughn@seaconinc.com</a>
  * @author Glenn McAllister <a href="mailto:glennm@ca.ibm.com">
  *      glennm@ca.ibm.com</a>
  * @author Magesh Umasankar
  * @author <a href="mailto:kadams@gfs.com">Kyle Adams</a>
  * @since Ant 1.3
  */
 public class FTP
      extends Task {
     protected static final int SEND_FILES = 0;
     protected static final int GET_FILES = 1;
     protected static final int DEL_FILES = 2;
     protected static final int LIST_FILES = 3;
     protected static final int MK_DIR = 4;
     protected static final int CHMOD = 5;
     protected static final int RM_DIR = 6;
     /** return code of ftp - not implemented in commons-net version 1.0 */
     private static final int CODE_521 = 521;
     /** Default port for FTP */
     public static final int DEFAULT_FTP_PORT = 21;
 
     private String remotedir;
     private String server;
     private String userid;
     private String password;
     private File listing;
     private boolean binary = true;
     private boolean passive = false;
     private boolean verbose = false;
     private boolean newerOnly = false;
     private long timeDiffMillis = 0;
     private boolean timeDiffAuto = false;
     private int action = SEND_FILES;
     private Vector filesets = new Vector();
     private Vector dirCache = new Vector();
     private int transferred = 0;
     private String remoteFileSep = "/";
     private int port = DEFAULT_FTP_PORT;
     private boolean skipFailedTransfers = false;
     private int skipped = 0;
     private boolean ignoreNoncriticalErrors = false;
     private boolean preserveLastModified = false;
     private String chmod = null;
     private String umask = null;
     private FileUtils fileUtils = FileUtils.newFileUtils();
 
     protected static final String[] ACTION_STRS = {
         "sending",
         "getting",
         "deleting",
         "listing",
         "making directory",
         "chmod",
         "removing"
         };
 
     protected static final String[] COMPLETED_ACTION_STRS = {
         "sent",
         "retrieved",
         "deleted",
         "listed",
         "created directory",
         "mode changed",
         "removed"
         };
 
     protected static final String[] ACTION_TARGET_STRS = {
         "files",
         "files",
         "files",
         "files",
         "directory",
         "files",
         "directories"
         };
 
 
     /**
      * internal class allowing to read the contents of a remote file system
      * using the FTP protocol
      * used in particular for ftp get operations
      * differences with DirectoryScanner
      * "" (the root of the fileset) is never included in the included directories
      * followSymlinks defaults to false
      */
     protected class FTPDirectoryScanner extends DirectoryScanner {
         protected FTPClient ftp = null;
-
+        private String rootPath = null;
+        /**
+         * since ant 1.6
+         * this flag should be set to true on UNIX and can save scanning time
+         */
+        private boolean remoteSystemCaseSensitive = false;
+        private boolean remoteSensitivityChecked = false;
 
         /**
          * constructor
          * @param ftp  ftpclient object
          */
         public FTPDirectoryScanner(FTPClient ftp) {
             super();
             this.ftp = ftp;
             this.setFollowSymlinks(false);
         }
 
 
         /**
          * scans the remote directory,
          * storing internally the included files, directories, ...
          */
         public void scan() {
             if (includes == null) {
                 // No includes supplied, so set it to 'matches all'
                 includes = new String[1];
                 includes[0] = "**";
             }
             if (excludes == null) {
                 excludes = new String[0];
             }
 
             filesIncluded = new Vector();
             filesNotIncluded = new Vector();
             filesExcluded = new Vector();
             dirsIncluded = new Vector();
             dirsNotIncluded = new Vector();
             dirsExcluded = new Vector();
 
             try {
                 String cwd = ftp.printWorkingDirectory();
                 // always start from the current ftp working dir
 
-                scandir(".", "", true);
+                checkIncludePatterns();
+                clearCaches();
                 ftp.changeWorkingDirectory(cwd);
             } catch (IOException e) {
                 throw new BuildException("Unable to scan FTP server: ", e);
             }
         }
 
 
         /**
+         * this routine is actually checking all the include patterns in
+         * order to avoid scanning everything under base dir
+         * @since ant1.6
+         */
+        private void checkIncludePatterns() {
+            Hashtable newroots = new Hashtable();
+            // put in the newroots vector the include patterns without
+            // wildcard tokens
+            for (int icounter = 0; icounter < includes.length; icounter++) {
+                String newpattern =
+                    SelectorUtils.rtrimWildcardTokens(includes[icounter]);
+                newroots.put(newpattern, includes[icounter]);
+            }
+            if (remotedir == null) {
+                try {
+                    remotedir = ftp.printWorkingDirectory();
+                } catch (IOException e) {
+                    throw new BuildException("could not read current ftp directory",
+                        getLocation());
+                }
+            }
+            AntFTPFile baseFTPFile = new AntFTPRootFile(ftp, remotedir);
+            rootPath = baseFTPFile.getAbsolutePath();
+            // construct it
+            if (newroots.containsKey("")) {
+                // we are going to scan everything anyway
+                scandir(remotedir, "", true);
+            } else {
+                // only scan directories that can include matched files or
+                // directories
+                Enumeration enum2 = newroots.keys();
+
+                while (enum2.hasMoreElements()) {
+                    String currentelement = (String) enum2.nextElement();
+                    String originalpattern = (String) newroots.get(currentelement);
+                    AntFTPFile myfile = new AntFTPFile(baseFTPFile, currentelement);
+                    boolean isOK = true;
+                    boolean traversesSymlinks = false;
+                    String path = null;
+
+                    if (myfile.exists()) {
+                        if (remoteSensitivityChecked
+                            && remoteSystemCaseSensitive && isFollowSymlinks()) {
+                            // cool case,
+                            //we do not need to scan all the subdirs in the relative path
+                            path = myfile.getFastRelativePath();
+                        } else {
+                            // may be on a case insensitive file system.  We want
+                            // the results to show what's really on the disk, so
+                            // we need to double check.
+                            try {
+                                path = myfile.getRelativePath();
+                                traversesSymlinks = myfile.isTraverseSymlinks();
+                            }  catch (IOException be) {
+                                throw new BuildException(be, getLocation());
+                            } catch (BuildException be) {
+                                isOK = false;
+
+                            }
+                        }
+                    } else {
+                        isOK = false;
+                    }
+                    if (isOK) {
+                        currentelement = path.replace(remoteFileSep.charAt(0), File.separatorChar);
+                        if (!isFollowSymlinks()
+                            && traversesSymlinks) {
+                            continue;
+                        }
+
+                        if (myfile.isDirectory()) {
+                            if (isIncluded(currentelement)
+                                && currentelement.length() > 0) {
+                                accountForIncludedDir(currentelement, myfile, true);
+                            }  else {
+                                if (currentelement.length() > 0) {
+                                    if (currentelement.charAt(currentelement
+                                                              .length() - 1)
+                                        != File.separatorChar) {
+                                        currentelement =
+                                            currentelement + File.separatorChar;
+                                    }
+                                }
+                                scandir(myfile.getAbsolutePath(), currentelement, true);
+                            }
+                        } else {
+                            if (isCaseSensitive
+                                && originalpattern.equals(currentelement)) {
+                                accountForIncludedFile(currentelement);
+                            } else if (!isCaseSensitive
+                                       && originalpattern
+                                       .equalsIgnoreCase(currentelement)) {
+                                accountForIncludedFile(currentelement);
+                            }
+                        }
+                    }
+                }
+            }
+        }
+        /**
          * scans a particular directory
          * @param dir directory to scan
          * @param vpath  relative path to the base directory of the remote fileset
          * always ended with a File.separator
          * @param fast seems to be always true in practice
          */
         protected void scandir(String dir, String vpath, boolean fast) {
+            // avoid double scanning of directories, can only happen in fast mode
+            if (fast && hasBeenScanned(vpath)) {
+                return;
+            }
             try {
                 if (!ftp.changeWorkingDirectory(dir)) {
                     return;
                 }
-
-                FTPFile[] newfiles = ftp.listFiles();
+                String completePath = null;
+                if (!vpath.equals("")) {
+                    completePath = rootPath + remoteFileSep
+                        + vpath.replace(File.separatorChar, remoteFileSep.charAt(0));
+                } else {
+                    completePath = rootPath;
+                }
+                FTPFile[] newfiles = listFiles(completePath, false);
 
                 if (newfiles == null) {
                     ftp.changeToParentDirectory();
                     return;
                 }
                 for (int i = 0; i < newfiles.length; i++) {
                     FTPFile file = newfiles[i];
                     if (!file.getName().equals(".")
                          && !file.getName().equals("..")) {
                         if (isFunctioningAsDirectory(ftp, dir, file)) {
                             String name = vpath + file.getName();
                             boolean slowScanAllowed = true;
                             if (!isFollowSymlinks() && file.isSymbolicLink()) {
                                 dirsExcluded.addElement(name);
                                 slowScanAllowed = false;
                             } else if (isIncluded(name)) {
-                                if (!isExcluded(name)) {
-                                    if (fast) {
-                                        if (file.isSymbolicLink()) {
-                                            scandir(file.getLink(),
-                                                name + File.separator, fast);
-                                        } else {
-                                            scandir(file.getName(),
-                                                name + File.separator, fast);
-                                        }
-                                    }
-                                    dirsIncluded.addElement(name);
-                                } else {
-                                    dirsExcluded.addElement(name);
-                                    if (fast && couldHoldIncluded(name)) {
-                                        scandir(file.getName(),
-                                                name + File.separator, fast);
-                                    }
-                                }
+                                accountForIncludedDir(name,
+                                    new AntFTPFile(ftp, file, completePath) , fast);
                             } else {
                                 dirsNotIncluded.addElement(name);
                                 if (fast && couldHoldIncluded(name)) {
                                     scandir(file.getName(),
                                             name + File.separator, fast);
                                 }
                             }
                             if (!fast && slowScanAllowed) {
                                 scandir(file.getName(),
                                         name + File.separator, fast);
                             }
                         } else {
                             String name = vpath + file.getName();
                             if (!isFollowSymlinks() && file.isSymbolicLink()) {
                                 filesExcluded.addElement(name);
                             } else if (isFunctioningAsFile(ftp, dir, file)) {
-                                if (isIncluded(name)) {
-                                    if (!isExcluded(name)) {
-                                        filesIncluded.addElement(name);
-                                    } else {
-                                        filesExcluded.addElement(name);
-                                    }
-                                } else {
-                                    filesNotIncluded.addElement(name);
-                                }
+                                accountForIncludedFile(name);
                             }
                         }
                     }
                 }
                 ftp.changeToParentDirectory();
             } catch (IOException e) {
                 throw new BuildException("Error while communicating with FTP "
                      + "server: ", e);
             }
         }
+        /**
+         * process included file
+         * @param name  path of the file relative to the directory of the fileset
+         */
+        private void accountForIncludedFile(String name) {
+            if (!filesIncluded.contains(name)
+                && !filesExcluded.contains(name)) {
+
+                if (isIncluded(name)) {
+                    if (!isExcluded(name)) {
+                        filesIncluded.addElement(name);
+                    } else {
+                        filesExcluded.addElement(name);
+                    }
+                } else {
+                    filesNotIncluded.addElement(name);
+                }
+            }
+        }
+
+        /**
+         *
+         * @param name path of the directory relative to the directory of
+         * the fileset
+         * @param file directory as file
+         * @param fast
+         */
+        private void accountForIncludedDir(String name, AntFTPFile file, boolean fast) {
+            if (!dirsIncluded.contains(name)
+                && !dirsExcluded.contains(name)) {
+
+                if (!isExcluded(name)) {
+                    if (fast) {
+                        if (file.isSymbolicLink()) {
+                            try {
+                                file.getClient().changeWorkingDirectory(file.curpwd);
+                            } catch (IOException ioe) {
+                                throw new BuildException("could not change directory to curpwd");
+                            }
+                            scandir(file.getLink(),
+                                name + File.separator, fast);
+                        } else {
+                            try {
+                                file.getClient().changeWorkingDirectory(file.curpwd);
+                            } catch (IOException ioe) {
+                                throw new BuildException("could not change directory to curpwd");
+                            }
+                            scandir(file.getName(),
+                                name + File.separator, fast);
+                        }
+                    }
+                    dirsIncluded.addElement(name);
+                } else {
+                    dirsExcluded.addElement(name);
+                    if (fast && couldHoldIncluded(name)) {
+                        try {
+                            file.getClient().changeWorkingDirectory(file.curpwd);
+                        } catch (IOException ioe) {
+                            throw new BuildException("could not change directory to curpwd");
+                        }
+                        scandir(file.getName(),
+                                name + File.separator, fast);
+                    }
+                }
+            }
+        }
+        /**
+         * temporary table to speed up the various scanning methods below
+         *
+         * @since Ant 1.6
+         */
+        private Map fileListMap = new HashMap();
+        /**
+         * List of all scanned directories.
+         *
+         * @since Ant 1.6
+         */
+        private Set scannedDirs = new HashSet();
+
+        /**
+         * Has the directory with the given path relative to the base
+         * directory already been scanned?
+         *
+         * <p>Registers the given directory as scanned as a side effect.</p>
+         *
+         * @since Ant 1.6
+         */
+        private boolean hasBeenScanned(String vpath) {
+            return !scannedDirs.add(vpath);
+        }
+
+        /**
+         * Clear internal caches.
+         *
+         * @since Ant 1.6
+         */
+        private void clearCaches() {
+            fileListMap.clear();
+            scannedDirs.clear();
+        }
+        /**
+         * list the files present in one directory.
+         * @param directory full path on the remote side
+         * @param changedir if true change to directory directory before listing
+         * @return array of FTPFile
+         */
+        public FTPFile[] listFiles(String directory, boolean changedir) {
+            //getProject().log("listing files in directory " + directory, Project.MSG_DEBUG);
+            String currentPath = directory;
+            if (changedir) {
+                try {
+                    boolean result = ftp.changeWorkingDirectory(directory);
+                    if (!result) {
+                        return null;
+                    }
+                    currentPath = ftp.printWorkingDirectory();
+                } catch (IOException ioe) {
+                    throw new BuildException(ioe, getLocation());
+                }
+            }
+            if (fileListMap.containsKey(currentPath)) {
+                getProject().log("filelist map used in listing files", Project.MSG_DEBUG);
+                return ((FTPFile[]) fileListMap.get(currentPath));
+            }
+            FTPFile[] result = null;
+            try {
+                result = ftp.listFiles();
+            } catch (IOException ioe) {
+                throw new BuildException(ioe, getLocation());
+            }
+            fileListMap.put(currentPath, result);
+            if (!remoteSensitivityChecked) {
+                checkRemoteSensitivity(result, directory);
+            }
+            return result;
+        }
+        /**
+         * cd into one directory and
+         * list the files present in one directory.
+         * @param directory full path on the remote side
+         * @return array of FTPFile
+         */
+        public FTPFile[] listFiles(String directory) {
+            return listFiles(directory, true);
+        }
+        private void checkRemoteSensitivity(FTPFile[] array, String directory) {
+            boolean candidateFound = false;
+            String target = null;
+            for (int icounter = 0; icounter < array.length; icounter++) {
+                if (array[icounter].isDirectory()) {
+                    if (!array[icounter].getName().equals(".")
+                        && !array[icounter].getName().equals("..")) {
+                        candidateFound = true;
+                        target = fiddleName(array[icounter].getName());
+                        getProject().log("will try to cd to "
+                            + target + " where a directory called " + array[icounter].getName()
+                            + " exists", Project.MSG_DEBUG);
+                        for (int pcounter = 0; pcounter < array.length; pcounter++) {
+                            if (array[pcounter].getName().equals(target) && pcounter != icounter) {
+                                candidateFound = false;
+                            }
+                        }
+                        if (candidateFound) {
+                            break;
+                        }
+                    }
+                }
+            }
+            if (candidateFound) {
+                try {
+                    getProject().log("testing case sensitivity, attempting to cd to "
+                        + target, Project.MSG_DEBUG);
+                    remoteSystemCaseSensitive  = !ftp.changeWorkingDirectory(target);
+                } catch (IOException ioe) {
+                    remoteSystemCaseSensitive = true;
+                } finally {
+                    try {
+                        ftp.changeWorkingDirectory(directory);
+                    } catch (IOException ioe) {
+                        throw new BuildException(ioe, getLocation());
+                    }
+                }
+                getProject().log("remote system is case sensitive : " + remoteSystemCaseSensitive,
+                    Project.MSG_VERBOSE);
+                remoteSensitivityChecked = true;
+            }
+        }
+        private String fiddleName(String origin) {
+            StringBuffer result = new StringBuffer();
+            for (int icounter = 0; icounter < origin.length(); icounter++) {
+                if (Character.isLowerCase(origin.charAt(icounter))) {
+                    result.append(Character.toUpperCase(origin.charAt(icounter)));
+                } else if (Character.isUpperCase(origin.charAt(icounter))) {
+                    result.append(Character.toLowerCase(origin.charAt(icounter)));
+                } else {
+                    result.append(origin.charAt(icounter));
+                }
+            }
+            return result.toString();
+        }
+        /**
+         * an AntFTPFile is a representation of a remote file
+         * @since Ant 1.6
+         */
+        protected class AntFTPFile {
+            /**
+             * ftp client
+             */
+            private FTPClient client;
+            /**
+             * parent directory of the file
+             */
+            private String curpwd;
+            /**
+             * the file itself
+             */
+            private FTPFile ftpFile;
+            /**
+             *
+             */
+            private AntFTPFile parent = null;
+            private boolean relativePathCalculated = false;
+            private boolean traversesSymlinks = false;
+            private String relativePath = "";
+            /**
+             * constructor
+             * @param client ftp client variable
+             * @param ftpFile the file
+             * @param curpwd absolute remote path where the file is found
+             */
+            public AntFTPFile(FTPClient client, FTPFile ftpFile, String curpwd) {
+                this.client = client;
+                this.ftpFile = ftpFile;
+                this.curpwd = curpwd;
+            }
+            /**
+             * other constructor
+             * @param parent the parent file
+             * @param path  a relative path to the parent file
+             */
+            public AntFTPFile(AntFTPFile parent, String path) {
+                this.parent = parent;
+                this.client = parent.client;
+                Vector pathElements = SelectorUtils.tokenizePath(path);
+                try {
+                    this.client.changeWorkingDirectory(parent.getAbsolutePath());
+                    this.curpwd = parent.getAbsolutePath();
+                } catch (IOException ioe) {
+                    throw new BuildException("could not change working dir to "
+                    + parent.curpwd);
+                }
+                for (int fcount = 0; fcount < pathElements.size() - 1; fcount++) {
+                    try {
+                        this.client.changeWorkingDirectory((String) pathElements.elementAt(fcount));
+                        this.curpwd = this.curpwd + remoteFileSep
+                            + (String) pathElements.elementAt(fcount);
+                    } catch (IOException ioe) {
+                        throw new BuildException("could not change working dir to "
+                        + (String) pathElements.elementAt(fcount)
+                            + " from " + this.curpwd);
+                    }
+
+                }
+                String lastpathelement = (String) pathElements.elementAt(pathElements.size() - 1);
+                FTPFile [] theFiles = listFiles(this.curpwd);
+                this.ftpFile = getFile(theFiles, lastpathelement);
+            }
+            /**
+             * find out if the file exists
+             * @return  true if the file exists
+             */
+            public boolean exists() {
+                return (ftpFile != null);
+            }
+            /**
+             * if the file is a symbolic link, find out to what it is pointing
+             * @return the target of the symbolic link
+             */
+            public String getLink() {
+                return ftpFile.getLink();
+            }
+            /**
+             * get the name of the file
+             * @return the name of the file
+             */
+            public String getName() {
+                return ftpFile.getName();
+            }
+            /**
+             * find out the absolute path of the file
+             * @return absolute path as string
+             */
+            public String getAbsolutePath() {
+                return curpwd + remoteFileSep + ftpFile.getName();
+            }
+            /**
+             * find out the relative path assuming that the path used to construct
+             * this AntFTPFile was spelled properly with regards to case.
+             * This is OK on a case sensitive system such as UNIX
+             * @return relative path
+             */
+            public String getFastRelativePath() {
+                String absPath = getAbsolutePath();
+                if (absPath.indexOf(rootPath + remoteFileSep) == 0) {
+                    return absPath.substring(rootPath.length() + remoteFileSep.length());
+                }
+                return null;
+            }
+            /**
+             * find out the relative path to the rootPath of the enclosing scanner.
+             * this relative path is spelled exactly like on disk,
+             * for instance if the AntFTPFile has been instantiated as ALPHA,
+             * but the file is really called alpha, this method will return alpha.
+             * If a symbolic link is encountered, it is followed, but the name of the link
+             * rather than the name of the target is returned.
+             * (ie does not behave like File.getCanonicalPath())
+             * @return                relative path, separated by remoteFileSep
+             * @throws IOException    if a change directory fails, ...
+             * @throws BuildException if one of the components of the relative path cannot
+             * be found.
+             */
+            public String getRelativePath() throws IOException, BuildException {
+                if (!relativePathCalculated) {
+                    if (parent != null) {
+                        traversesSymlinks = parent.isTraverseSymlinks();
+                        relativePath = getRelativePath(parent.getAbsolutePath(),
+                            parent.getRelativePath());
+                    } else {
+                        relativePath = getRelativePath(rootPath, "");
+                        relativePathCalculated = true;
+                    }
+                }
+                return relativePath;
+            }
+            /**
+             * get thge relative path of this file
+             * @param currentPath          base path
+             * @param currentRelativePath  relative path of the base path with regards to remote dir
+             * @return relative path
+             */
+            private String getRelativePath(String currentPath, String currentRelativePath) {
+                Vector pathElements = SelectorUtils.tokenizePath(getAbsolutePath(), remoteFileSep);
+                Vector pathElements2 = SelectorUtils.tokenizePath(currentPath, remoteFileSep);
+                String relPath = currentRelativePath;
+                for (int pcount = pathElements2.size(); pcount < pathElements.size(); pcount++) {
+                    String currentElement = (String) pathElements.elementAt(pcount);
+                    FTPFile[] theFiles = listFiles(currentPath);
+                    FTPFile theFile = null;
+                    if (theFiles != null) {
+                        theFile = getFile(theFiles, currentElement);
+                    }
+                    if (theFile == null) {
+                        throw new BuildException("could not find " + currentElement
+                            + " from " + currentPath);
+                    } else {
+                        traversesSymlinks = traversesSymlinks || theFile.isSymbolicLink();
+                        if (!relPath.equals("")) {
+                            relPath = relPath + remoteFileSep;
+                        }
+                        relPath = relPath + theFile.getName();
+                        currentPath = currentPath + remoteFileSep + theFile.getName();
+                    }
+                }
+                return relPath;
+            }
+            /**
+             * find a file matching a string in an array of FTPFile.
+             * This method will find "alpha" when requested for "ALPHA"
+             * if and only if the caseSensitive attribute is set to false.
+             * When caseSensitive is set to true, only the exact match is returned.
+             * @param theFiles  array of files
+             * @param lastpathelement  the file name being sought
+             * @return null if the file cannot be found, otherwise return the matching file.
+             */
+            public FTPFile getFile(FTPFile[] theFiles, String lastpathelement) {
+                if (theFiles == null) {
+                    return null;
+                }
+                for (int fcount = 0; fcount < theFiles.length; fcount++) {
+                     if (theFiles[fcount].getName().equals(lastpathelement)) {
+                         return theFiles[fcount];
+                     } else if (!isCaseSensitive()
+                         && theFiles[fcount].getName().equalsIgnoreCase(lastpathelement)) {
+                         return theFiles[fcount];
+                     }
+                }
+                return null;
+            }
+            /**
+             * tell if a file is a directory.
+             * note that it will return false for symbolic links pointing to directories.
+             * @return <code>true</code> for directories
+             */
+            public boolean isDirectory() {
+                return ftpFile.isDirectory();
+            }
+            /**
+             * tell if a file is a symbolic link
+             * @return <code>true</code> for symbolic links
+             */
+            public boolean isSymbolicLink() {
+                return ftpFile.isSymbolicLink();
+            }
+            /**
+             * return the attached FTP client object.
+             * Warning : this instance is really shared with the enclosing class.
+             * @return  FTP client
+             */
+            protected FTPClient getClient() {
+                return client;
+            }
+
+            /**
+             * sets the current path of an AntFTPFile
+             * @param curpwd the current path one wants to set
+             */
+            protected void setCurpwd(String curpwd) {
+                this.curpwd = curpwd;
+            }
+            /**
+             * returns the path of the directory containing the AntFTPFile.
+             * of the full path of the file itself in case of AntFTPRootFile
+             * @return parent directory of the AntFTPFile
+             */
+            public String getCurpwd() {
+                return curpwd;
+            }
+            /**
+             * find out if a symbolic link is encountered in the relative path of this file
+             * from rootPath.
+             * @return <code>true</code> if a symbolic link is encountered in the relative path.
+             * @throws IOException if one of the change directory or directory listing operations
+             * fails
+             * @throws BuildException if a path component in the relative path cannot be found.
+             */
+            public boolean isTraverseSymlinks() throws IOException, BuildException {
+                if (!relativePathCalculated) {
+                    // getRelativePath also finds about symlinks
+                    String relpath = getRelativePath();
+                }
+                return traversesSymlinks;
+            }
+        }
+        /**
+         * special class to represent the remote directory itself
+         * @since Ant 1.6
+         */
+        protected class AntFTPRootFile extends AntFTPFile {
+             private String remotedir;
+            /**
+             * constructor
+             * @param aclient FTP client
+             * @param remotedir remote directory
+             */
+             public AntFTPRootFile(FTPClient aclient, String remotedir) {
+                 super(aclient, null, remotedir);
+                 this.remotedir = remotedir;
+                 try {
+                     this.getClient().changeWorkingDirectory(this.remotedir);
+                     this.setCurpwd(this.getClient().printWorkingDirectory());
+                 } catch (IOException ioe) {
+                     throw new BuildException(ioe, getLocation());
+                 }
+             }
+            /**
+             * find the absolute path
+             * @return absolute path
+             */
+            public String getAbsolutePath() {
+                return this.getCurpwd();
+            }
+            /**
+             * find out the relative path to root
+             * @return empty string
+             * @throws BuildException actually never
+             * @throws IOException  actually never
+             */
+            public String getRelativePath() throws BuildException, IOException {
+                 return "";
+            }
+        }
     }
     /**
      * check FTPFiles to check whether they function as directories too
      * the FTPFile API seem to make directory and symbolic links incompatible
      * we want to find out if we can cd to a symbolic link
      * @param dir  the parent directory of the file to test
      * @param file the file to test
      * @return true if it is possible to cd to this directory
      * @since ant 1.6
      */
     private boolean isFunctioningAsDirectory(FTPClient ftp, String dir, FTPFile file) {
         boolean result = false;
         String currentWorkingDir = null;
         if (file.isDirectory()) {
             return true;
         } else if (file.isFile()) {
             return false;
         }
         try {
             currentWorkingDir = ftp.printWorkingDirectory();
         } catch (IOException ioe) {
             getProject().log("could not find current working directory " + dir
                 + " while checking a symlink",
                 Project.MSG_DEBUG);
         }
         if (currentWorkingDir != null) {
             try {
                 result = ftp.changeWorkingDirectory(file.getLink());
             } catch (IOException ioe) {
                 getProject().log("could not cd to " + file.getLink() + " while checking a symlink",
                     Project.MSG_DEBUG);
             }
             if (result) {
                 boolean comeback = false;
                 try {
                     comeback = ftp.changeWorkingDirectory(currentWorkingDir);
                 } catch (IOException ioe) {
                     getProject().log("could not cd back to " + dir + " while checking a symlink",
                         Project.MSG_ERR);
                 } finally {
                     if (!comeback) {
                         throw new BuildException("could not cd back to " + dir
                             + " while checking a symlink");
                     }
                 }
             }
         }
         return result;
     }
     /**
      * check FTPFiles to check whether they function as directories too
      * the FTPFile API seem to make directory and symbolic links incompatible
      * we want to find out if we can cd to a symbolic link
      * @param dir  the parent directory of the file to test
      * @param file the file to test
      * @return true if it is possible to cd to this directory
      * @since ant 1.6
      */
     private boolean isFunctioningAsFile(FTPClient ftp, String dir, FTPFile file) {
         if (file.isDirectory()) {
             return false;
         } else if (file.isFile()) {
             return true;
         }
         return !isFunctioningAsDirectory(ftp, dir, file);
     }
     /**
      * Sets the remote directory where files will be placed. This may be a
      * relative or absolute path, and must be in the path syntax expected by
      * the remote server. No correction of path syntax will be performed.
      *
      * @param dir the remote directory name.
      */
     public void setRemotedir(String dir) {
         this.remotedir = dir;
     }
 
 
     /**
      * Sets the FTP server to send files to.
      *
      * @param server the remote server name.
      */
     public void setServer(String server) {
         this.server = server;
     }
 
 
     /**
      * Sets the FTP port used by the remote server.
      *
      * @param port the port on which the remote server is listening.
      */
     public void setPort(int port) {
         this.port = port;
     }
 
 
     /**
      * Sets the login user id to use on the specified server.
      *
      * @param userid remote system userid.
      */
     public void setUserid(String userid) {
         this.userid = userid;
     }
 
 
     /**
      * Sets the login password for the given user id.
      *
      * @param password the password on the remote system.
      */
     public void setPassword(String password) {
         this.password = password;
     }
 
 
     /**
      * If true, uses binary mode, otherwise text mode (default is binary).
      *
      * @param binary if true use binary mode in transfers.
      */
     public void setBinary(boolean binary) {
         this.binary = binary;
     }
 
 
     /**
      * Specifies whether to use passive mode. Set to true if you are behind a
      * firewall and cannot connect without it. Passive mode is disabled by
      * default.
      *
      * @param passive true is passive mode should be used.
      */
     public void setPassive(boolean passive) {
         this.passive = passive;
     }
 
 
     /**
      * Set to true to receive notification about each file as it is
      * transferred.
      *
      * @param verbose true if verbose notifications are required.
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
 
     /**
      * A synonym for <tt>depends</tt>. Set to true to transmit only new
      * or changed files.
      *
      * See the related attributes timediffmillis and timediffauto.
      *
      * @param newer if true only transfer newer files.
      */
     public void setNewer(boolean newer) {
         this.newerOnly = newer;
     }
 
     /**
      * number of milliseconds to add to the time on the remote machine
      * to get the time on the local machine.
      *
      * use in conjunction with <code>newer</code>
      *
      * @param timeDiffMillis number of milliseconds
      *
      * @since ant 1.6
      */
     public void setTimeDiffMillis(long timeDiffMillis) {
         this.timeDiffMillis = timeDiffMillis;
     }
 
     /**
      * &quot;true&quot; to find out automatically the time difference
      * between local and remote machine.
      *
      * This requires right to create
      * and delete a temporary file in the remote directory.
      *
      * @param timeDiffAuto true = find automatically the time diff
      *
      * @since ant 1.6
      */
     public void setTimeDiffAuto(boolean timeDiffAuto) {
         this.timeDiffAuto = timeDiffAuto;
     }
 
     /**
      * Set to true to preserve modification times for "gotten" files.
      *
      * @param preserveLastModified if true preserver modification times.
      */
     public void setPreserveLastModified(boolean preserveLastModified) {
         this.preserveLastModified = preserveLastModified;
     }
 
 
     /**
      * Set to true to transmit only files that are new or changed from their
      * remote counterparts. The default is to transmit all files.
      *
      * @param depends if true only transfer newer files.
      */
     public void setDepends(boolean depends) {
         this.newerOnly = depends;
     }
 
 
     /**
      * Sets the remote file separator character. This normally defaults to the
      * Unix standard forward slash, but can be manually overridden using this
      * call if the remote server requires some other separator. Only the first
      * character of the string is used.
      *
      * @param separator the file separator on the remote system.
      */
     public void setSeparator(String separator) {
         remoteFileSep = separator;
     }
 
 
     /**
      * Sets the file permission mode (Unix only) for files sent to the
      * server.
      *
      * @param theMode unix style file mode for the files sent to the remote
      *        system.
      */
     public void setChmod(String theMode) {
         this.chmod = theMode;
     }
 
 
     /**
      * Sets the default mask for file creation on a unix server.
      *
      * @param theUmask unix style umask for files created on the remote server.
      */
     public void setUmask(String theUmask) {
         this.umask = theUmask;
     }
 
 
     /**
      *  A set of files to upload or download
      *
      * @param set the set of files to be added to the list of files to be
      *        transferred.
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
 
     /**
      * Sets the FTP action to be taken. Currently accepts "put", "get", "del",
      * "mkdir" and "list".
      *
      * @deprecated setAction(String) is deprecated and is replaced with
      *      setAction(FTP.Action) to make Ant's Introspection mechanism do the
      *      work and also to encapsulate operations on the type in its own
      *      class.
      * @ant.attribute ignore="true"
      *
      * @param action the FTP action to be performed.
      *
      * @throws BuildException if the action is not a valid action.
      */
     public void setAction(String action) throws BuildException {
         log("DEPRECATED - The setAction(String) method has been deprecated."
              + " Use setAction(FTP.Action) instead.");
 
         Action a = new Action();
 
         a.setValue(action);
         this.action = a.getAction();
     }
 
 
     /**
      * Sets the FTP action to be taken. Currently accepts "put", "get", "del",
      * "mkdir", "chmod" and "list".
      *
      * @param action the FTP action to be performed.
      *
      * @throws BuildException if the action is not a valid action.
      */
     public void setAction(Action action) throws BuildException {
         this.action = action.getAction();
     }
 
 
     /**
      * The output file for the "list" action. This attribute is ignored for
      * any other actions.
      *
      * @param listing file in which to store the listing.
      */
     public void setListing(File listing) {
         this.listing = listing;
     }
 
 
     /**
      * If true, enables unsuccessful file put, delete and get
      * operations to be skipped with a warning and the remainder
      * of the files still transferred.
      *
      * @param skipFailedTransfers true if failures in transfers are ignored.
      */
     public void setSkipFailedTransfers(boolean skipFailedTransfers) {
         this.skipFailedTransfers = skipFailedTransfers;
     }
 
 
     /**
      * set the flag to skip errors on directory creation.
      * (and maybe later other server specific errors)
      *
      * @param ignoreNoncriticalErrors true if non-critical errors should not
      *        cause a failure.
      */
     public void setIgnoreNoncriticalErrors(boolean ignoreNoncriticalErrors) {
         this.ignoreNoncriticalErrors = ignoreNoncriticalErrors;
     }
 
 
     /**
      * Checks to see that all required parameters are set.
      *
      * @throws BuildException if the configuration is not valid.
      */
     protected void checkConfiguration() throws BuildException {
         if (server == null) {
             throw new BuildException("server attribute must be set!");
         }
         if (userid == null) {
             throw new BuildException("userid attribute must be set!");
         }
         if (password == null) {
             throw new BuildException("password attribute must be set!");
         }
 
         if ((action == LIST_FILES) && (listing == null)) {
             throw new BuildException("listing attribute must be set for list "
                  + "action!");
         }
 
         if (action == MK_DIR && remotedir == null) {
             throw new BuildException("remotedir attribute must be set for "
                  + "mkdir action!");
         }
 
         if (action == CHMOD && chmod == null) {
             throw new BuildException("chmod attribute must be set for chmod "
                  + "action!");
         }
     }
 
 
     /**
      * For each file in the fileset, do the appropriate action: send, get,
      * delete, or list.
      *
      * @param ftp the FTPClient instance used to perform FTP actions
      * @param fs the fileset on which the actions are performed.
      *
      * @return the number of files to be transferred.
      *
      * @throws IOException if there is a problem reading a file
      * @throws BuildException if there is a problem in the configuration.
      */
     protected int transferFiles(FTPClient ftp, FileSet fs)
          throws IOException, BuildException {
         DirectoryScanner ds;
 
         if (action == SEND_FILES) {
             ds = fs.getDirectoryScanner(getProject());
         } else {
             // warn that selectors are not supported
             if (fs.getSelectors(getProject()).length != 0) {
                 getProject().log("selectors are not supported in remote filesets",
                     Project.MSG_WARN);
             }
             ds = new FTPDirectoryScanner(ftp);
             fs.setupDirectoryScanner(ds, getProject());
             ds.setFollowSymlinks(fs.isFollowSymlinks());
             ds.scan();
         }
 
         String[] dsfiles = null;
         if (action == RM_DIR) {
             dsfiles = ds.getIncludedDirectories();
         } else {
             dsfiles = ds.getIncludedFiles();
         }
         String dir = null;
 
         if ((ds.getBasedir() == null)
              && ((action == SEND_FILES) || (action == GET_FILES))) {
             throw new BuildException("the dir attribute must be set for send "
                  + "and get actions");
         } else {
             if ((action == SEND_FILES) || (action == GET_FILES)) {
                 dir = ds.getBasedir().getAbsolutePath();
             }
         }
 
         // If we are doing a listing, we need the output stream created now.
         BufferedWriter bw = null;
 
         try {
             if (action == LIST_FILES) {
                 File pd = fileUtils.getParentFile(listing);
 
                 if (!pd.exists()) {
                     pd.mkdirs();
                 }
                 bw = new BufferedWriter(new FileWriter(listing));
             }
 
             for (int i = 0; i < dsfiles.length; i++) {
                 switch (action) {
                     case SEND_FILES:
                         sendFile(ftp, dir, dsfiles[i]);
                         break;
                     case GET_FILES:
                         getFile(ftp, dir, dsfiles[i]);
                         break;
                     case DEL_FILES:
                         delFile(ftp, dsfiles[i]);
                         break;
                     case LIST_FILES:
                         listFile(ftp, bw, dsfiles[i]);
                         break;
                     case CHMOD:
                         doSiteCommand(ftp, "chmod " + chmod + " " + resolveFile(dsfiles[i]));
                         transferred++;
                         break;
                     case RM_DIR:
                         rmDir(ftp, dsfiles[i]);
                         break;
                     default:
                         throw new BuildException("unknown ftp action " + action);
                 }
             }
         } finally {
             if (bw != null) {
                 bw.close();
             }
         }
 
         return dsfiles.length;
     }
 
 
     /**
      * Sends all files specified by the configured filesets to the remote
      * server.
      *
      * @param ftp the FTPClient instance used to perform FTP actions
      *
      * @throws IOException if there is a problem reading a file
      * @throws BuildException if there is a problem in the configuration.
      */
     protected void transferFiles(FTPClient ftp)
          throws IOException, BuildException {
         transferred = 0;
         skipped = 0;
 
         if (filesets.size() == 0) {
             throw new BuildException("at least one fileset must be specified.");
         } else {
             // get files from filesets
             for (int i = 0; i < filesets.size(); i++) {
                 FileSet fs = (FileSet) filesets.elementAt(i);
 
                 if (fs != null) {
                     transferFiles(ftp, fs);
                 }
             }
         }
 
         log(transferred + " " + ACTION_TARGET_STRS[action] + " "
             + COMPLETED_ACTION_STRS[action]);
         if (skipped != 0) {
             log(skipped + " " + ACTION_TARGET_STRS[action]
                 + " were not successfully " + COMPLETED_ACTION_STRS[action]);
         }
     }
 
 
     /**
      * Correct a file path to correspond to the remote host requirements. This
      * implementation currently assumes that the remote end can handle
      * Unix-style paths with forward-slash separators. This can be overridden
      * with the <code>separator</code> task parameter. No attempt is made to
      * determine what syntax is appropriate for the remote host.
      *
      * @param file the remote file name to be resolved
      *
      * @return the filename as it will appear on the server.
      */
     protected String resolveFile(String file) {
         return file.replace(System.getProperty("file.separator").charAt(0),
             remoteFileSep.charAt(0));
     }
 
 
     /**
      * Creates all parent directories specified in a complete relative
      * pathname. Attempts to create existing directories will not cause
      * errors.
      *
      * @param ftp the FTP client instance to use to execute FTP actions on
      *        the remote server.
      * @param filename the name of the file whose parents should be created.
      * @throws IOException under non documented circumstances
      * @throws BuildException if it is impossible to cd to a remote directory
      *
      */
     protected void createParents(FTPClient ftp, String filename)
          throws IOException, BuildException {
 
         File dir = new File(filename);
         if (dirCache.contains(dir)) {
             return;
         }
 
 
         Vector parents = new Vector();
         String dirname;
 
         while ((dirname = dir.getParent()) != null) {
             File checkDir = new File(dirname);
             if (dirCache.contains(checkDir)) {
                 break;
             }
             dir = checkDir;
             parents.addElement(dir);
         }
 
         // find first non cached dir
         int i = parents.size() - 1;
 
         if (i >= 0) {
             String cwd = ftp.printWorkingDirectory();
             String parent = dir.getParent();
             if (parent != null) {
                 if (!ftp.changeWorkingDirectory(resolveFile(parent))) {
                     throw new BuildException("could not change to "
                         + "directory: " + ftp.getReplyString());
                 }
             }
 
             while (i >= 0) {
                 dir = (File) parents.elementAt(i--);
                 // check if dir exists by trying to change into it.
                 if (!ftp.changeWorkingDirectory(dir.getName())) {
                     // could not change to it - try to create it
                     log("creating remote directory "
                         + resolveFile(dir.getPath()), Project.MSG_VERBOSE);
                     if (!ftp.makeDirectory(dir.getName())) {
                         handleMkDirFailure(ftp);
                     }
                     if (!ftp.changeWorkingDirectory(dir.getName())) {
                         throw new BuildException("could not change to "
                             + "directory: " + ftp.getReplyString());
                     }
                 }
                 dirCache.addElement(dir);
             }
             ftp.changeWorkingDirectory(cwd);
         }
     }
     /**
      * auto find the time difference between local and remote
      * @param ftp handle to ftp client
      * @return number of millis to add to remote time to make it comparable to local time
      * @since ant 1.6
      */
     private long getTimeDiff(FTPClient ftp) {
         long returnValue = 0;
         File tempFile = findFileName(ftp);
         try {
             // create a local temporary file
             fileUtils.createNewFile(tempFile);
             long localTimeStamp = tempFile.lastModified();
             BufferedInputStream instream = new BufferedInputStream(new FileInputStream(tempFile));
             ftp.storeFile(tempFile.getName(), instream);
             instream.close();
             boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());
             if (success) {
                 FTPFile [] ftpFiles = ftp.listFiles(tempFile.getName());
                 if (ftpFiles.length == 1) {
                     long remoteTimeStamp = ftpFiles[0].getTimestamp().getTime().getTime();
                     returnValue = remoteTimeStamp - localTimeStamp;
                 }
                 ftp.deleteFile(ftpFiles[0].getName());
             }
             // delegate the deletion of the local temp file to the delete task
             // because of race conditions occuring on Windows
             Delete mydelete = (Delete) getProject().createTask("delete");
             mydelete.setFile(tempFile.getCanonicalFile());
             mydelete.execute();
         } catch (Exception e) {
             throw new BuildException(e, getLocation());
         }
         return returnValue;
     }
     /**
      *  find a suitable name for local and remote temporary file
      */
     private File findFileName(FTPClient ftp) {
         FTPFile [] theFiles = null;
         final int maxIterations = 1000;
         for (int counter = 1; counter < maxIterations; counter++) {
             File localFile = fileUtils.createTempFile("ant" + Integer.toString(counter), ".tmp",
                 null);
             String fileName = localFile.getName();
             boolean found = false;
             try {
                 if (counter == 1) {
                     theFiles = ftp.listFiles();
                 }
                 for (int counter2 = 0; counter2 < theFiles.length; counter2++) {
                     if (theFiles[counter2].getName().equals(fileName)) {
                         found = true;
                         break;
                     }
                 }
             } catch (IOException ioe) {
                 throw new BuildException(ioe, getLocation());
             }
             if (!found) {
                 return localFile;
             }
         }
         return null;
     }
     /**
      * Checks to see if the remote file is current as compared with the local
      * file. Returns true if the target file is up to date.
      * @param ftp ftpclient
      * @param localFile local file
      * @param remoteFile remote file
      * @return true if the target file is up to date
      * @throws IOException  in unknown circumstances
      * @throws BuildException if the date of the remote files cannot be found and the action is
      * GET_FILES
      */
     protected boolean isUpToDate(FTPClient ftp, File localFile,
                                  String remoteFile)
          throws IOException, BuildException {
         log("checking date for " + remoteFile, Project.MSG_VERBOSE);
 
         FTPFile[] files = ftp.listFiles(remoteFile);
 
         // For Microsoft's Ftp-Service an Array with length 0 is
         // returned if configured to return listings in "MS-DOS"-Format
         if (files == null || files.length == 0) {
             // If we are sending files, then assume out of date.
             // If we are getting files, then throw an error
 
             if (action == SEND_FILES) {
                 log("Could not date test remote file: " + remoteFile
                      + "assuming out of date.", Project.MSG_VERBOSE);
                 return false;
             } else {
                 throw new BuildException("could not date test remote file: "
                     + ftp.getReplyString());
             }
         }
 
         long remoteTimestamp = files[0].getTimestamp().getTime().getTime();
         long localTimestamp = localFile.lastModified();
 
         if (this.action == SEND_FILES) {
             return remoteTimestamp + timeDiffMillis > localTimestamp;
         } else {
             return localTimestamp > remoteTimestamp + timeDiffMillis;
         }
     }
 
 
     /**
     * Sends a site command to the ftp server
     * @param ftp ftp client
     * @param theCMD command to execute
     * @throws IOException  in unknown circumstances
     * @throws BuildException in unknown circumstances
     */
     protected void doSiteCommand(FTPClient ftp, String theCMD)
          throws IOException, BuildException {
         boolean rc;
         String[] myReply = null;
 
         log("Doing Site Command: " + theCMD, Project.MSG_VERBOSE);
 
         rc = ftp.sendSiteCommand(theCMD);
 
         if (!rc) {
             log("Failed to issue Site Command: " + theCMD, Project.MSG_WARN);
         } else {
 
             myReply = ftp.getReplyStrings();
 
             for (int x = 0; x < myReply.length; x++) {
                 if (myReply[x].indexOf("200") == -1) {
                     log(myReply[x], Project.MSG_WARN);
                 }
             }
         }
     }
 
 
     /**
      * Sends a single file to the remote host. <code>filename</code> may
      * contain a relative path specification. When this is the case, <code>sendFile</code>
      * will attempt to create any necessary parent directories before sending
      * the file. The file will then be sent using the entire relative path
      * spec - no attempt is made to change directories. It is anticipated that
      * this may eventually cause problems with some FTP servers, but it
      * simplifies the coding.
      * @param ftp ftp client
      * @param dir base directory of the file to be sent (local)
      * @param filename relative path of the file to be send
      *        locally relative to dir
      *        remotely relative to the remotedir attribute
      * @throws IOException  in unknown circumstances
      * @throws BuildException in unknown circumstances
      */
     protected void sendFile(FTPClient ftp, String dir, String filename)
          throws IOException, BuildException {
         InputStream instream = null;
 
         try {
             // XXX - why not simply new File(dir, filename)?
             File file = getProject().resolveFile(new File(dir, filename).getPath());
 
             if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                 return;
             }
 
             if (verbose) {
                 log("transferring " + file.getAbsolutePath());
             }
 
             instream = new BufferedInputStream(new FileInputStream(file));
 
             createParents(ftp, filename);
 
             ftp.storeFile(resolveFile(filename), instream);
 
             boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());
 
             if (!success) {
                 String s = "could not put file: " + ftp.getReplyString();
 
                 if (skipFailedTransfers) {
                     log(s, Project.MSG_WARN);
                     skipped++;
                 } else {
                     throw new BuildException(s);
                 }
 
             } else {
                 // see if we should issue a chmod command
                 if (chmod != null) {
                     doSiteCommand(ftp, "chmod " + chmod + " " + filename);
                 }
                 log("File " + file.getAbsolutePath() + " copied to " + server,
                     Project.MSG_VERBOSE);
                 transferred++;
             }
         } finally {
             if (instream != null) {
                 try {
                     instream.close();
                 } catch (IOException ex) {
                     // ignore it
                 }
             }
         }
     }
 
 
     /**
      * Delete a file from the remote host.
      * @param ftp ftp client
      * @param filename file to delete
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is set to false
      * and the deletion could not be done
      */
     protected void delFile(FTPClient ftp, String filename)
          throws IOException, BuildException {
         if (verbose) {
             log("deleting " + filename);
         }
 
         if (!ftp.deleteFile(resolveFile(filename))) {
             String s = "could not delete file: " + ftp.getReplyString();
 
             if (skipFailedTransfers) {
                 log(s, Project.MSG_WARN);
                 skipped++;
             } else {
                 throw new BuildException(s);
             }
         } else {
             log("File " + filename + " deleted from " + server,
                 Project.MSG_VERBOSE);
             transferred++;
         }
     }
 
     /**
      * Delete a directory, if empty, from the remote host.
      * @param ftp ftp client
      * @param dirname directory to delete
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is set to false
      * and the deletion could not be done
      */
     protected void rmDir(FTPClient ftp, String dirname)
          throws IOException, BuildException {
         if (verbose) {
             log("removing " + dirname);
         }
 
         if (!ftp.removeDirectory(resolveFile(dirname))) {
             String s = "could not remove directory: " + ftp.getReplyString();
 
             if (skipFailedTransfers) {
                 log(s, Project.MSG_WARN);
                 skipped++;
             } else {
                 throw new BuildException(s);
             }
         } else {
             log("Directory " + dirname + " removed from " + server,
                 Project.MSG_VERBOSE);
             transferred++;
         }
     }
 
 
     /**
      * Retrieve a single file from the remote host. <code>filename</code> may
      * contain a relative path specification. <p>
      *
      * The file will then be retreived using the entire relative path spec -
      * no attempt is made to change directories. It is anticipated that this
      * may eventually cause problems with some FTP servers, but it simplifies
      * the coding.</p>
      * @param ftp the ftp client
      * @param dir local base directory to which the file should go back
      * @param filename relative path of the file based upon the ftp remote directory
      *        and/or the local base directory (dir)
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is false
      * and the file cannot be retrieved.
      */
     protected void getFile(FTPClient ftp, String dir, String filename)
          throws IOException, BuildException {
         OutputStream outstream = null;
 
         try {
             File file = getProject().resolveFile(new File(dir, filename).getPath());
 
             if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                 return;
             }
 
             if (verbose) {
                 log("transferring " + filename + " to "
                      + file.getAbsolutePath());
             }
 
             File pdir = fileUtils.getParentFile(file);
 
             if (!pdir.exists()) {
                 pdir.mkdirs();
             }
             outstream = new BufferedOutputStream(new FileOutputStream(file));
             ftp.retrieveFile(resolveFile(filename), outstream);
 
             if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                 String s = "could not get file: " + ftp.getReplyString();
 
                 if (skipFailedTransfers) {
                     log(s, Project.MSG_WARN);
                     skipped++;
                 } else {
                     throw new BuildException(s);
                 }
 
             } else {
                 log("File " + file.getAbsolutePath() + " copied from "
                      + server, Project.MSG_VERBOSE);
                 transferred++;
                 if (preserveLastModified) {
                     outstream.close();
                     outstream = null;
                     FTPFile[] remote = ftp.listFiles(resolveFile(filename));
                     if (remote.length > 0) {
                         fileUtils.setFileLastModified(file,
                                                       remote[0].getTimestamp()
                                                       .getTime().getTime());
                     }
                 }
             }
         } finally {
             if (outstream != null) {
                 try {
                     outstream.close();
                 } catch (IOException ex) {
                     // ignore it
                 }
             }
         }
     }
 
 
     /**
      * List information about a single file from the remote host. <code>filename</code>
      * may contain a relative path specification. <p>
      *
      * The file listing will then be retrieved using the entire relative path
      * spec - no attempt is made to change directories. It is anticipated that
      * this may eventually cause problems with some FTP servers, but it
      * simplifies the coding.</p>
      * @param ftp ftp client
      * @param bw buffered writer
      * @param filename the directory one wants to list
      * @throws IOException  in unknown circumstances
      * @throws BuildException in unknown circumstances
      */
     protected void listFile(FTPClient ftp, BufferedWriter bw, String filename)
          throws IOException, BuildException {
         if (verbose) {
             log("listing " + filename);
         }
 
         FTPFile ftpfile = ftp.listFiles(resolveFile(filename))[0];
 
         bw.write(ftpfile.toString());
         bw.newLine();
 
         transferred++;
     }
 
 
     /**
      * Create the specified directory on the remote host.
      *
      * @param ftp The FTP client connection
      * @param dir The directory to create (format must be correct for host
      *      type)
      * @throws IOException  in unknown circumstances
      * @throws BuildException if ignoreNoncriticalErrors has not been set to true
      *         and a directory could not be created, for instance because it was
      *         already existing. Precisely, the codes 521, 550 and 553 will trigger
      *         a BuildException
      */
     protected void makeRemoteDir(FTPClient ftp, String dir)
          throws IOException, BuildException {
         String workingDirectory = ftp.printWorkingDirectory();
         if (verbose) {
             log("Creating directory: " + dir);
         }
         if (dir.indexOf("/") == 0) {
             ftp.changeWorkingDirectory("/");
         }
         String subdir = new String();
         StringTokenizer st = new StringTokenizer(dir, "/");
         while (st.hasMoreTokens()) {
             subdir = st.nextToken();
             log("Checking " + subdir, Project.MSG_DEBUG);
             if (!ftp.changeWorkingDirectory(subdir)) {
                 if (!ftp.makeDirectory(subdir)) {
                     // codes 521, 550 and 553 can be produced by FTP Servers
                     //  to indicate that an attempt to create a directory has
                     //  failed because the directory already exists.
                     int rc = ftp.getReplyCode();
                     if (!(ignoreNoncriticalErrors
                         && (rc == FTPReply.CODE_550 || rc == FTPReply.CODE_553
                         || rc == CODE_521))) {
                         throw new BuildException("could not create directory: "
                             + ftp.getReplyString());
                     }
                     if (verbose) {
                         log("Directory already exists");
                     }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/optional/net/FTPTest.java b/src/testcases/org/apache/tools/ant/taskdefs/optional/net/FTPTest.java
index 8d4f78aa2..85a18d149 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/optional/net/FTPTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/optional/net/FTPTest.java
@@ -1,596 +1,597 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2003 The Apache Software Foundation.  All rights
  * reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  *
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in
  *    the documentation and/or other materials provided with the
  *    distribution.
  *
  * 3. The end-user documentation included with the redistribution, if
  *    any, must include the following acknowlegement:
  *       "This product includes software developed by the
  *        Apache Software Foundation (http://www.apache.org/)."
  *    Alternately, this acknowlegement may appear in the software itself,
  *    if and wherever such third-party acknowlegements normally appear.
  *
  * 4. The names "Ant" and "Apache Software
  *    Foundation" must not be used to endorse or promote products derived
  *    from this software without prior written permission. For written
  *    permission, please contact apache@apache.org.
  *
  * 5. Products derived from this software may not be called "Apache"
  *    nor may "Apache" appear in their names without prior written
  *    permission of the Apache Group.
  *
  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  * SUCH DAMAGE.
  * ====================================================================
  *
  * This software consists of voluntary contributions made by many
  * individuals on behalf of the Apache Software Foundation.  For more
  * information on the Apache Software Foundation, please see
  * <http://www.apache.org/>.
  */
 package org.apache.tools.ant.taskdefs.optional.net;
 
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.taskdefs.optional.net.FTP;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.taskdefs.condition.Os;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Arrays;
 
 import org.apache.commons.net.ftp.FTPClient;
 
 public class FTPTest extends BuildFileTest{
     // keep track of what operating systems are supported here.
     private boolean supportsSymlinks = Os.isFamily("unix")
         && !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1);
 
     private FTPClient ftp;
     private boolean connectionSucceeded = true;
     private boolean loginSuceeded = true;
     private String tmpDir = null;
     private String remoteTmpDir = null;
     private String ftpFileSep = null;
     private myFTP myFTPTask = new myFTP();
 
     public FTPTest(String name) {
         super(name);
     }
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/optional/net/ftp.xml");
         getProject().executeTarget("setup");
         tmpDir = getProject().getProperty("tmp.dir");
         ftp = new FTPClient();
         ftpFileSep = getProject().getProperty("ftp.filesep");
         myFTPTask.setSeparator(ftpFileSep);
+        myFTPTask.setProject(getProject());
         remoteTmpDir = myFTPTask.resolveFile(tmpDir);
         String remoteHost = getProject().getProperty("ftp.host");
         int port = Integer.parseInt(getProject().getProperty("ftp.port"));
         String remoteUser = getProject().getProperty("ftp.user");
         String password = getProject().getProperty("ftp.password");
         try {
             ftp.connect(remoteHost, port);
         } catch (Exception ex) {
             connectionSucceeded = false;
             loginSuceeded = false;
             System.out.println("could not connect to host " + remoteHost + " on port " + port);
         }
         if (connectionSucceeded) {
             try {
                 ftp.login(remoteUser, password);
             } catch (IOException ioe) {
                 loginSuceeded = false;
                 System.out.println("could not log on to " + remoteHost + " as user " + remoteUser);
             }
         }
     }
 
     public void tearDown() {
         getProject().executeTarget("cleanup");
     }
     private boolean changeRemoteDir(String remoteDir) {
         boolean result = true;
         try {
             ftp.cwd(remoteDir);
         }
         catch (Exception ex) {
             System.out.println("could not change directory to " + remoteTmpDir);
             result = false;
         }
         return result;
     }
     public void test1() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir))  {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.setIncludes(new String[] {"alpha"});
                 ds.scan();
                 compareFiles(ds, new String[] {} ,new String[] {"alpha"});
             }
         }
     }
 
     public void test2() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir)) {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.setIncludes(new String[] {"alpha/"});
                 ds.scan();
                 compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                                "alpha/beta/gamma/gamma.xml"},
                     new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
             }
         }
     }
 
     public void test3() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir)) {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.scan();
                 compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                                "alpha/beta/gamma/gamma.xml"},
                     new String[] {"alpha", "alpha/beta",
                                   "alpha/beta/gamma"});
             }
         }
     }
 
     public void testFullPathMatchesCaseSensitive() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir)) {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.setIncludes(new String[] {"alpha/beta/gamma/GAMMA.XML"});
                 ds.scan();
                 compareFiles(ds, new String[] {}, new String[] {});
             }
         }
     }
 
     public void testFullPathMatchesCaseInsensitive() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir)) {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setCaseSensitive(false);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.setIncludes(new String[] {"alpha/beta/gamma/GAMMA.XML"});
                 ds.scan();
                 compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                     new String[] {});
             }
         }
     }
 
     public void test2ButCaseInsensitive() {
         if (loginSuceeded) {
             if (changeRemoteDir(remoteTmpDir)) {
                 FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
                 ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
                 ds.setIncludes(new String[] {"ALPHA/"});
                 ds.setCaseSensitive(false);
                 ds.scan();
                 compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                                "alpha/beta/gamma/gamma.xml"},
                     new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
             }
         }
     }
     public void testGetWithSelector() {
         expectLogContaining("ftp-get-with-selector",
             "selectors are not supported in remote filesets");
         FileSet fsDestination = (FileSet) getProject().getReference("fileset-destination-without-selector");
         DirectoryScanner dsDestination = fsDestination.getDirectoryScanner(getProject());
         dsDestination.scan();
         String [] sortedDestinationDirectories = dsDestination.getIncludedDirectories();
         String [] sortedDestinationFiles = dsDestination.getIncludedFiles();
         for (int counter = 0; counter < sortedDestinationDirectories.length; counter++) {
             sortedDestinationDirectories[counter] =
                 sortedDestinationDirectories[counter].replace(File.separatorChar, '/');
         }
         for (int counter = 0; counter < sortedDestinationFiles.length; counter++) {
             sortedDestinationFiles[counter] =
                 sortedDestinationFiles[counter].replace(File.separatorChar, '/');
         }
         FileSet fsSource =  (FileSet) getProject().getReference("fileset-source-without-selector");
         DirectoryScanner dsSource = fsSource.getDirectoryScanner(getProject());
         dsSource.scan();
         compareFiles(dsSource, sortedDestinationFiles, sortedDestinationDirectories);
     }
     public void testGetFollowSymlinksTrue() {
         if (!supportsSymlinks) {
             return;
         }
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("ftp-get-directory-symbolic-link");
         FileSet fsDestination = (FileSet) getProject().getReference("fileset-destination-without-selector");
         DirectoryScanner dsDestination = fsDestination.getDirectoryScanner(getProject());
         dsDestination.scan();
         compareFiles(dsDestination, new String[] {"alpha/beta/gamma/gamma.xml"},
             new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
     }
     public void testGetFollowSymlinksFalse() {
         if (!supportsSymlinks) {
             return;
         }
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("ftp-get-directory-no-symbolic-link");
         FileSet fsDestination = (FileSet) getProject().getReference("fileset-destination-without-selector");
         DirectoryScanner dsDestination = fsDestination.getDirectoryScanner(getProject());
         dsDestination.scan();
         compareFiles(dsDestination, new String[] {},
             new String[] {});
     }
     public void testAllowSymlinks() {
         if (!supportsSymlinks) {
             return;
         }
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("symlink-setup");
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/beta/gamma/"});
         ds.setFollowSymlinks(true);
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha/beta/gamma"});
     }
 
     public void testProhibitSymlinks() {
         if (!supportsSymlinks) {
             return;
         }
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("symlink-setup");
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/beta/gamma/"});
         ds.setFollowSymlinks(false);
         ds.scan();
         compareFiles(ds, new String[] {}, new String[] {});
     }
     public void testFileSymlink() {
         if (!supportsSymlinks) {
             return;
         }
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("symlink-file-setup");
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/beta/gamma/"});
         ds.setFollowSymlinks(true);
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha/beta/gamma"});
     }
     // father and child pattern test
     public void testOrderOfIncludePatternsIrrelevant() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         String [] expectedFiles = {"alpha/beta/beta.xml",
                                    "alpha/beta/gamma/gamma.xml"};
         String [] expectedDirectories = {"alpha/beta", "alpha/beta/gamma" };
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/be?a/**", "alpha/beta/gamma/"});
         ds.scan();
         compareFiles(ds, expectedFiles, expectedDirectories);
         // redo the test, but the 2 include patterns are inverted
         ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/beta/gamma/", "alpha/be?a/**"});
         ds.scan();
         compareFiles(ds, expectedFiles, expectedDirectories);
     }
 
     public void testPatternsDifferInCaseScanningSensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/", "ALPHA/"});
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                        "alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
     }
 
     public void testPatternsDifferInCaseScanningInsensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/", "ALPHA/"});
         ds.setCaseSensitive(false);
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                        "alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
     }
 
     public void testFullpathDiffersInCaseScanningSensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {
             "alpha/beta/gamma/gamma.xml",
             "alpha/beta/gamma/GAMMA.XML"
         });
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                      new String[] {});
     }
 
     public void testFullpathDiffersInCaseScanningInsensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {
             "alpha/beta/gamma/gamma.xml",
             "alpha/beta/gamma/GAMMA.XML"
         });
         ds.setCaseSensitive(false);
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                      new String[] {});
     }
 
     public void testParentDiffersInCaseScanningSensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/", "ALPHA/beta/"});
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                        "alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
     }
 
     public void testParentDiffersInCaseScanningInsensitive() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {"alpha/", "ALPHA/beta/"});
         ds.setCaseSensitive(false);
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                        "alpha/beta/gamma/gamma.xml"},
                      new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
     }
 
     public void testExcludeOneFile() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {
             "**/*.xml"
         });
         ds.setExcludes(new String[] {
             "alpha/beta/b*xml"
         });
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                      new String[] {});
     }
     public void testExcludeHasPrecedence() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {
             "alpha/**"
         });
         ds.setExcludes(new String[] {
             "alpha/**"
         });
         ds.scan();
         compareFiles(ds, new String[] {},
                      new String[] {});
 
     }
     public void testAlternateIncludeExclude() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setIncludes(new String[] {
             "alpha/**",
             "alpha/beta/gamma/**"
         });
         ds.setExcludes(new String[] {
             "alpha/beta/**"
         });
         ds.scan();
         compareFiles(ds, new String[] {},
                      new String[] {"alpha"});
 
     }
     public void testAlternateExcludeInclude() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setExcludes(new String[] {
             "alpha/**",
             "alpha/beta/gamma/**"
         });
         ds.setIncludes(new String[] {
             "alpha/beta/**"
         });
         ds.scan();
         compareFiles(ds, new String[] {},
                      new String[] {});
 
     }
     /**
      * Test inspired by Bug#1415.
      */
     public void testChildrenOfExcludedDirectory() {
         if (!loginSuceeded) {
             return;
         }
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         getProject().executeTarget("children-of-excluded-dir-setup");
         FTP.FTPDirectoryScanner ds = myFTPTask.newScanner(ftp);
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setExcludes(new String[] {"alpha/**"});
         ds.scan();
         compareFiles(ds, new String[] {"delta/delta.xml"},
                     new String[] {"delta"});
 
         ds = myFTPTask.newScanner(ftp);
         if (!changeRemoteDir(remoteTmpDir)) {
             return;
         }
         ds.setBasedir(new File(getProject().getBaseDir(), "tmp"));
         ds.setExcludes(new String[] {"alpha"});
         ds.scan();
         compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                        "alpha/beta/gamma/gamma.xml",
                                         "delta/delta.xml"},
                      new String[] {"alpha/beta", "alpha/beta/gamma", "delta"});
 
     }
 
     private void compareFiles(DirectoryScanner ds, String[] expectedFiles,
                               String[] expectedDirectories) {
         String includedFiles[] = ds.getIncludedFiles();
         String includedDirectories[] = ds.getIncludedDirectories();
         assertEquals("file present: ", expectedFiles.length,
                      includedFiles.length);
         assertEquals("directories present: ", expectedDirectories.length,
                      includedDirectories.length);
 
         for (int counter=0; counter < includedFiles.length; counter++) {
             includedFiles[counter] = includedFiles[counter].replace(File.separatorChar, '/');
         }
         Arrays.sort(includedFiles);
         for (int counter=0; counter < includedDirectories.length; counter++) {
             includedDirectories[counter] = includedDirectories[counter]
                             .replace(File.separatorChar, '/');
         }
         Arrays.sort(includedDirectories);
         for (int counter=0; counter < includedFiles.length; counter++) {
             assertEquals(expectedFiles[counter], includedFiles[counter]);
         }
         for (int counter=0; counter < includedDirectories.length; counter++) {
             assertEquals(expectedDirectories[counter], includedDirectories[counter]);
             counter++;
         }
     }
     private static class myFTP extends FTP {
         public FTP.FTPDirectoryScanner newScanner(FTPClient client) {
             return new FTP.FTPDirectoryScanner(client);
         }
         // provide public visibility
         public String resolveFile(String file) {
             return super.resolveFile(file);
         }
 
     }
 }
