diff --git a/WHATSNEW b/WHATSNEW
index ab44cbcf3..3aa6b9f9c 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1358 +1,1361 @@
 Changes from Ant 1.5.3 to current CVS version
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
 
 * If the Visual Age tasks used to work for you, they may stop doing so
   now - and we'd like to know about it.  The current set of tasks is
   supposed to work with any version of VAJ starting with 3.0.
 
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
 
 * The Visual Age for Java tasks didn't work (at least for versions 3.0
   and higher).  Bugzilla Report 10016.
 
 * URL-encoding in <vaj*port> didn't work properly.
 
 * VAJRemoteUtil called getAbsolutePath instead of getPath
   causing problems when using a Windows VAJ server from a UNIX server.
   Bugzilla Report 20457.
 
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
 
 
 Other changes:
 --------------
 * Six new Clearcase tasks added.
 
 * A new filter reader namely tokenfilter has been added.  Bugzilla
   Report 18312.
 
 * A new attribute named skip is added to the TailFilter and
   HeadFilter filter readers.
 
 * Shipped XML parser is now Xerces 2.4.0
 
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
 
+* <sql> is able to display properly several resultsets if you are
+  running a compound sql statement. Bugzilla Report 21594.
+
 * <javah> will invoke oldjavah on JDK 1.4.2.  Bugzilla Report 18667.
 
 * A new <containsregexp> selector has been added, that selects files
   if their content matches a certain regular expression.
 
 * <antlr>'s debug attribute has been enabled.  Bugzilla Report 19051.
 
 * <mail> has a new attribute encoding. Bugzilla Report 15434.
 
 * <mail> has new attributes user and password for SMTP auth.
   maillogger can also use this.
   The implementation only works with JavaMail (encoding="MIME").
   Implementation with plain mail remains to do.
   Bugzilla Report 5969.
 
 * <mail> and mailloger support SMTP over TLS/SSL
   Bugzilla Report 19180.
 
 * <zipfileset> can now be defined in the main body of a project
   and referred to with refid="xyz". Bugzilla Report 17007.
 
 * A wrapper script for OS/2 has been added.
 
 * <unzip> will now detect and successfully extract self-extracting
   archives.  Bugzilla Report 16213.
 
 * <stcheckout> has a new attribute "converteol" that can be used to
   control the automatic line-end conversion performed on ASCII files.
   Bugzilla Report 18884.
 
 * The VAJ tasks now support a haltonfailure attribute to conditionally
   keep building even if they fail.
 
 * It is now possible to use the latest (versioned or unversioned) edition
   in <vajload> by using special wildcard characters.  Also fixes
   Bugzilla Report 2236.
 
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
 
 * DirectoryScanner has been optimized for cases where include patterns do not start with wildcards
   Bugzilla Report 20103.
 
 * Added keep-going feature. Bugzilla Report 21144
 
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index a981e4bb2..42311bf4c 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,661 +1,688 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.BufferedReader;
 import java.io.StringReader;
 import java.io.FileReader;
 import java.io.InputStreamReader;
 import java.io.FileInputStream;
 import java.util.Enumeration;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 
 /**
  * Executes a series of SQL statements on a database using JDBC.
  *
  * <p>Statements can
  * either be read in from a text file using the <i>src</i> attribute or from
  * between the enclosing SQL tags.</p>
  *
  * <p>Multiple statements can be provided, separated by semicolons (or the
  * defined <i>delimiter</i>). Individual lines within the statements can be
  * commented using either --, // or REM at the start of the line.</p>
  *
  * <p>The <i>autocommit</i> attribute specifies whether auto-commit should be
  * turned on or off whilst executing the statements. If auto-commit is turned
  * on each statement will be executed and committed. If it is turned off the
  * statements will all be executed as one transaction.</p>
  *
  * <p>The <i>onerror</i> attribute specifies how to proceed when an error occurs
  * during the execution of one of the statements.
  * The possible values are: <b>continue</b> execution, only show the error;
  * <b>stop</b> execution and commit transaction;
  * and <b>abort</b> execution and transaction and fail task.</p>
 
  *
  * @author <a href="mailto:jeff@custommonkey.org">Jeff Martin</a>
  * @author <A href="mailto:gholam@xtra.co.nz">Michael McCallum</A>
  * @author <A href="mailto:tim.stephenson@sybase.com">Tim Stephenson</A>
  *
  * @since Ant 1.2
  *
  * @ant.task name="sql" category="database"
  */
 public class SQLExec extends JDBCTask {
 
     /**
      * delimiters we support, "normal" and "row"
      */
     public static class DelimiterType extends EnumeratedAttribute {
         public static final String NORMAL = "normal";
         public static final String ROW = "row";
         public String[] getValues() {
             return new String[] {NORMAL, ROW};
         }
     }
 
 
 
     private int goodSql = 0;
 
     private int totalSql = 0;
 
     /**
      * Database connection
      */
     private Connection conn = null;
 
     /**
      * files to load
      */
     private Vector filesets = new Vector();
 
     /**
      * SQL statement
      */
     private Statement statement = null;
 
     /**
      * SQL input file
      */
     private File srcFile = null;
 
     /**
      * SQL input command
      */
     private String sqlCommand = "";
 
     /**
      * SQL transactions to perform
      */
     private Vector transactions = new Vector();
 
     /**
      * SQL Statement delimiter
      */
     private String delimiter = ";";
 
     /**
      * The delimiter type indicating whether the delimiter will
      * only be recognized on a line by itself
      */
     private String delimiterType = DelimiterType.NORMAL;
 
     /**
      * Print SQL results.
      */
     private boolean print = false;
 
     /**
      * Print header columns.
      */
     private boolean showheaders = true;
 
     /**
      * Results Output file.
      */
     private File output = null;
 
 
     /**
      * Action to perform if an error is found
      **/
     private String onError = "abort";
 
     /**
      * Encoding to use when reading SQL statements from a file
      */
     private String encoding = null;
 
     /**
      * Append to an existing file or overwrite it?
      */
     private boolean append = false;
 
     /**
      * Keep the format of a sql block?
      */
     private boolean keepformat = false;
 
     /**
      * Argument to Statement.setEscapeProcessing
      *
      * @since Ant 1.6
      */
     private boolean escapeProcessing = true;
 
     /**
      * Set the name of the SQL file to be run.
      * Required unless statements are enclosed in the build file
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text.
      */
     public void addText(String sql) {
         this.sqlCommand += sql;
     }
 
     /**
      * Adds a set of files (nested fileset attribute).
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
 
     /**
      * Add a SQL transaction to execute
      */
     public Transaction createTransaction() {
         Transaction t = new Transaction();
         transactions.addElement(t);
         return t;
     }
 
     /**
      * Set the file encoding to use on the SQL files read in
      *
      * @param encoding the encoding to use on the files
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Set the delimiter that separates SQL statements. Defaults to &quot;;&quot;;
      * optional
      *
      * <p>For example, set this to "go" and delimitertype to "ROW" for
      * Sybase ASE or MS SQL Server.</p>
      */
     public void setDelimiter(String delimiter) {
         this.delimiter = delimiter;
     }
 
     /**
      * Set the delimiter type: "normal" or "row" (default "normal").
      *
      * <p>The delimiter type takes two values - normal and row. Normal
      * means that any occurence of the delimiter terminate the SQL
      * command whereas with row, only a line containing just the
      * delimiter is recognized as the end of the command.</p>
      */
     public void setDelimiterType(DelimiterType delimiterType) {
         this.delimiterType = delimiterType.getValue();
     }
 
     /**
      * Print result sets from the statements;
      * optional, default false
      */
     public void setPrint(boolean print) {
         this.print = print;
     }
 
     /**
      * Print headers for result sets from the
      * statements; optional, default true.
      */
     public void setShowheaders(boolean showheaders) {
         this.showheaders = showheaders;
     }
 
     /**
      * Set the output file;
      * optional, defaults to the Ant log.
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * whether output should be appended to or overwrite
      * an existing file.  Defaults to false.
      *
      * @since Ant 1.5
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
 
     /**
      * Action to perform when statement fails: continue, stop, or abort
      * optional; default &quot;abort&quot;
      */
     public void setOnerror(OnError action) {
         this.onError = action.getValue();
     }
 
     /**
      * whether or not format should be preserved.
      * Defaults to false.
      *
      * @param keepformat The keepformat to set
      */
     public void setKeepformat(boolean keepformat) {
         this.keepformat = keepformat;
     }
 
     /**
      * Set escape processing for statements.
      *
      * @since Ant 1.6
      */
     public void setEscapeProcessing(boolean enable) {
         escapeProcessing = enable;
     }
 
     /**
      * Load the sql file and then execute it
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0
                 && filesets.isEmpty()) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or fileset, "
                                              + "transactions or sql statement "
                                              + "must be set!", location);
                 }
             }
 
             if (srcFile != null && !srcFile.exists()) {
                 throw new BuildException("Source file does not exist!", location);
             }
 
             // deal with the filesets
             for (int i = 0; i < filesets.size(); i++) {
                 FileSet fs = (FileSet) filesets.elementAt(i);
                 DirectoryScanner ds = fs.getDirectoryScanner(project);
                 File srcDir = fs.getDir(project);
 
                 String[] srcFiles = ds.getIncludedFiles();
 
                 // Make a transaction for each file
                 for (int j = 0 ; j < srcFiles.length ; j++) {
                     Transaction t = createTransaction();
                     t.setSrc(new File(srcDir, srcFiles[j]));
                 }
             }
 
             // Make a transaction group for the outer command
             Transaction t = createTransaction();
             t.setSrc(srcFile);
             t.addText(sqlCommand);
             conn = getConnection();
             if (!isValidRdbms(conn)) {
                 return;
             }
             try {
                 statement = conn.createStatement();
                 statement.setEscapeProcessing(escapeProcessing);
 
                 PrintStream out = System.out;
                 try {
                     if (output != null) {
                         log("Opening PrintStream to output file " + output,
                             Project.MSG_VERBOSE);
                         out = new PrintStream(
                                   new BufferedOutputStream(
                                       new FileOutputStream(output
                                                            .getAbsolutePath(),
                                                            append)));
                     }
 
                     // Process all transactions
                     for (Enumeration e = transactions.elements();
                          e.hasMoreElements();) {
 
                         ((Transaction) e.nextElement()).runTransaction(out);
                         if (!isAutocommit()) {
                             log("Commiting transaction", Project.MSG_VERBOSE);
                             conn.commit();
                         }
                     }
                 } finally {
                     if (out != null && out != System.out) {
                         out.close();
                     }
                 }
             } catch (IOException e){
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {}
                 }
                 throw new BuildException(e, location);
             } catch (SQLException e){
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {}
                 }
                 throw new BuildException(e, location);
             } finally {
                 try {
                     if (statement != null) {
                         statement.close();
                     }
                     if (conn != null) {
                         conn.close();
                     }
                 } catch (SQLException e) {}
             }
 
             log(goodSql + " of " + totalSql +
                 " SQL statements executed successfully");
         } finally {
             transactions = savedTransaction;
             sqlCommand = savedSqlCommand;
         }
     }
 
     /**
      * read in lines and execute them
      */
     protected void runStatements(Reader reader, PrintStream out)
         throws SQLException, IOException {
         StringBuffer sql = new StringBuffer();
         String line = "";
 
         BufferedReader in = new BufferedReader(reader);
 
         while ((line = in.readLine()) != null){
             if (!keepformat) {
                 line = line.trim();
             }
             line = project.replaceProperties(line);
             if (!keepformat) {
                 if (line.startsWith("//")) {
                     continue;
                 }
                 if (line.startsWith("--")) {
                     continue;
                 }
                 StringTokenizer st = new StringTokenizer(line);
                 if (st.hasMoreTokens()) {
                     String token = st.nextToken();
                     if ("REM".equalsIgnoreCase(token)) {
                         continue;
                     }
                 }
             }
 
             if (!keepformat) {
                 sql.append(" " + line);
             } else {
                 sql.append("\n" + line);
             }
 
             // SQL defines "--" as a comment to EOL
             // and in Oracle it may contain a hint
             // so we cannot just remove it, instead we must end it
             if (!keepformat) {
                 if (line.indexOf("--") >= 0) {
                     sql.append("\n");
                 }
             }
             if ((delimiterType.equals(DelimiterType.NORMAL)
                  && sql.toString().endsWith(delimiter))
                 ||
                 (delimiterType.equals(DelimiterType.ROW)
                  && line.equals(delimiter))) {
                 execSQL(sql.substring(0, sql.length() - delimiter.length()),
                         out);
                 sql.replace(0, sql.length(), "");
             }
         }
         // Catch any statements not followed by ;
         if (!sql.equals("")){
             execSQL(sql.toString(), out);
         }
     }
 
 
     /**
      * Exec the sql statement.
      */
     protected void execSQL(String sql, PrintStream out) throws SQLException {
         // Check and ignore empty statements
         if ("".equals(sql.trim())) {
             return;
         }
 
         try {
             totalSql++;
             log("SQL: " + sql, Project.MSG_VERBOSE);
-            if (!statement.execute(sql)) {
-                log(statement.getUpdateCount() + " rows affected",
-                    Project.MSG_VERBOSE);
-            } else {
-                if (print) {
-                    printResults(out);
+
+            boolean ret;
+            int updateCount = 0, updateCountTotal = 0;
+            ResultSet resultSet = null;
+
+            ret = statement.execute(sql);
+            updateCount = statement.getUpdateCount();
+            resultSet = statement.getResultSet();
+            do
+            {
+                if (!ret)
+                {
+                    if (updateCount != -1)
+                    {
+                        updateCountTotal += updateCount;
+                    }
                 }
+                else
+                {
+                    if (print)
+                    {
+                        printResults(out);
+                    }
+                }
+                ret = statement.getMoreResults();
+                updateCount = statement.getUpdateCount();
+                resultSet = statement.getResultSet();
+            } while ((resultSet != null) || (updateCount != -1));
+
+            log(updateCountTotal + " rows affected",
+                Project.MSG_VERBOSE);
+
+            if (print)
+            {
+                StringBuffer line = new StringBuffer();
+                line.append(updateCountTotal + " rows affected");
+                out.println(line);
             }
 
             SQLWarning warning = conn.getWarnings();
             while (warning != null){
                 log(warning + " sql warning", Project.MSG_VERBOSE);
                 warning = warning.getNextWarning();
             }
             conn.clearWarnings();
             goodSql++;
         } catch (SQLException e) {
             log("Failed to execute: " + sql, Project.MSG_ERR);
             if (!onError.equals("continue")) {
                 throw e;
             }
             log(e.toString(), Project.MSG_ERR);
         }
     }
 
     /**
      * print any results in the statement.
      */
     protected void printResults(PrintStream out) throws java.sql.SQLException {
         ResultSet rs = null;
-        do {
-            rs = statement.getResultSet();
-            if (rs != null) {
-                log("Processing new result set.", Project.MSG_VERBOSE);
-                ResultSetMetaData md = rs.getMetaData();
-                int columnCount = md.getColumnCount();
-                StringBuffer line = new StringBuffer();
-                if (showheaders) {
-                    for (int col = 1; col < columnCount; col++) {
-                         line.append(md.getColumnName(col));
-                         line.append(",");
-                    }
-                    line.append(md.getColumnName(columnCount));
-                    out.println(line);
-                    line = new StringBuffer();
+        rs = statement.getResultSet();
+        if (rs != null) {
+            log("Processing new result set.", Project.MSG_VERBOSE);
+            ResultSetMetaData md = rs.getMetaData();
+            int columnCount = md.getColumnCount();
+            StringBuffer line = new StringBuffer();
+            if (showheaders) {
+                for (int col = 1; col < columnCount; col++) {
+                     line.append(md.getColumnName(col));
+                     line.append(",");
                 }
-                while (rs.next()) {
-                    boolean first = true;
-                    for (int col = 1; col <= columnCount; col++) {
-                        String columnValue = rs.getString(col);
-                        if (columnValue != null) {
-                            columnValue = columnValue.trim();
-                        }
+                line.append(md.getColumnName(columnCount));
+                out.println(line);
+                line = new StringBuffer();
+            }
+            while (rs.next()) {
+                boolean first = true;
+                for (int col = 1; col <= columnCount; col++) {
+                    String columnValue = rs.getString(col);
+                    if (columnValue != null) {
+                        columnValue = columnValue.trim();
+                    }
 
-                        if (first) {
-                            first = false;
-                        } else {
-                            line.append(",");
-                        }
-                        line.append(columnValue);
+                    if (first) {
+                        first = false;
+                    } else {
+                        line.append(",");
                     }
-                    out.println(line);
-                    line = new StringBuffer();
+                    line.append(columnValue);
                 }
+                out.println(line);
+                line = new StringBuffer();
             }
         }
-        while (statement.getMoreResults());
         out.println();
     }
 
     /**
      * The action a task should perform on an error,
      * one of "continue", "stop" and "abort"
      */
     public static class OnError extends EnumeratedAttribute {
         public String[] getValues() {
             return new String[] {"continue", "stop", "abort"};
         }
     }
 
     /**
      * Contains the definition of a new transaction element.
      * Transactions allow several files or blocks of statements
      * to be executed using the same JDBC connection and commit
      * operation in between.
      */
     public class Transaction {
         private File tSrcFile = null;
         private String tSqlCommand = "";
 
         /**
          *
          */
         public void setSrc(File src) {
             this.tSrcFile = src;
         }
 
         /**
          *
          */
         public void addText(String sql) {
             this.tSqlCommand += sql;
         }
 
         /**
          *
          */
         private void runTransaction(PrintStream out)
             throws IOException, SQLException {
             if (tSqlCommand.length() != 0) {
                 log("Executing commands", Project.MSG_INFO);
                 runStatements(new StringReader(tSqlCommand), out);
             }
 
             if (tSrcFile != null) {
                 log("Executing file: " + tSrcFile.getAbsolutePath(),
                     Project.MSG_INFO);
                 Reader reader =
                     (encoding == null) ? new FileReader(tSrcFile)
                                        : new InputStreamReader(
                                              new FileInputStream(tSrcFile),
                                              encoding);
                 try {
                     runStatements(reader, out);
                 } finally {
                     reader.close();
                 }
             }
         }
     }
 
 }
