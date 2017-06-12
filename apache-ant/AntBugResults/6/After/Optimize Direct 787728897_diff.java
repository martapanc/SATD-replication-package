diff --git a/WHATSNEW b/WHATSNEW
index 21869a87c..1b17250aa 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1488 +1,1491 @@
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
 
+* DirectoryScanner has been optimized for cases where include patterns do not start with wildcards
+  Bugzilla Report 20103.
+
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
 
 Changes that could break older environments:
 --------------------------------------------
 
 * Important: Single $ signs are no longer silently stripped!
   Before you panic that we have broken all your build files, we have kept
   the old "$$" -> "$" behaviour. So only build files which accidentally had
   a $ sign in a string that was being silently stripped may break.
   We added this fix to stop newbie confusion; if you want to write a
   build file which works on ant versions 1.4.1 or earlier, stay with
   the double $$ sign rule.
 
 * Shipped XML parser is now Xerces 2.0.1 along with the XML Parser APIs.
   XML Parser APIs is a separate jar that contains the necessary
   JAXP/DOM/SAX classes.
 
 * <telnet> was fixed to expand properties inside nested <read> and
   <write> elements; before this only happened when you assigned the text
   to the string attribute. If you had $ signs in the string, they may
   need escaping.
 
 * the RegexpMatcher interface has been extended to support case
   insensitive matches and other options - custom implementations of
   this interface won't work any longer.  We recommend to use the new
   Regexp interface that also supports substitution instead of the
   RegexpMatcher interface in the future.
 
 * <gzip> will throw an exception if your src attribute points to a directory.
 
 * Unjar, Unzip and Unwar will throw an exception if the Src attribute
   represents a directory.  Support for nested filesets is provided
   instead.
 
 * It is no longer possible to overwrite a property using tasks like
   <condition>, <exec>, <pathconvert>, or <tstamp>. In some exceptional
   cases it will generate a warning if you attempt to overwrite an
   existing property.
 
 * Taskwriters please note: Whenever tasks had any overloaded set* methods,
   Ant's introspection mechanism would select the last overloaded method
   provided to it by the Java Runtime.  A modification has now been made such
   that when the Java Runtime provides a method with a String as its argument,
   a check is made to see if there is another overloaded method that takes in
   some other type of argument.  If there is one such method, then the method
   that takes in String as an argument is not selected by the Introspector.
 
 * The pattern definition **/._* has been included into the Default
   Excludes list.
 
 * <propertyfile>'s <entry> element was modified to remove "never" as a value
   as its behavior was undocumented and flakey.
 
 * The -projecthelp flag now only prints out targets that include the
   'description' attribute, unless the -verbose or -debug flag is included
   on the Ant command line.
 
 * Ant's testcases now require JUnit 3.7 or above, as they now use the new
   assertTrue method instead of assert.
 
 * If the 'output' attribute of <ant> is set to a simple filename or a
   relative path, the file is created relative to ${basedir}, not ${user.dir}.
 
 * The default value for build.compiler is now javac1.x with x
   depending on the JDK that is running Ant instead of classic/modern.
 
 Fixed bugs:
 -----------
 * A bug existed that prevented generated log files from being deleted as
   part of the build process itself.  This has now been fixed.
 
 * Fixed bug where <move> ignored <filterset>s.
 
 * Ant works properly with the combination of Java1.4/WindowsXP.
 
 * Fixed bug where <java> used to sometimes invoke class constructors twice.
 
 * Fixed bug with 4NT shell support.
 
 * Fixed bug where ant would not perform ftp without remotedir being
   specified even though this was not mandatory.
 
 * Fixed bug where ant would not copy system properties into new Project
   in ant/antcall tasks when inheritall="false" is set.
 
 * <propertyfile> would not close the original property file.
 
 * <ant> will no longer override a subbuild's basedir with inheritall="true".
 
 * Fixed problem with the built-in <junit> formatters which assumed
   that only one test could be running at the same time - this is not
   necessarily true, see junit.extensions.ActiveTestSuite.
 
 * <jar>'s whenEmpty attribute is useless as JARs are never empty, they
   contain at least a manifest file, therefore it will now print a
   warning and do nothing.
 
 * <typedef> hasn't been all that useful as it couldn't be used outside
   of targets (it can now) and nested "unknown" elements have always
   been considered to be tasks (changed as well).
 
 * <fixcrlf> would fail for files that contained lines longer than 8kB.
 
 * Some junit formatters incorrectly assumed that all testcases would
   inherit from junit.framework.TestCase.
 
 * <fixcrlf> dropped the first characters from Mac files.
 
 Other changes:
 --------------
 * Selector Elements now provide a way to create filesets based on
   sophisticated selection criteria.
 
 * Gzip and Bzip2 files can now be constructed in the fly when using
   the tar task without having to create the intermediate tar file on
   disk.  The Untar task can also untar GZip and BZip2 files on the fly
   without creating the intermediate tar file.
 
 * New optional type, <classfileset> added.
 
 * <ejbjar> now allows control over which additional classes and interfaces
   are added to the generated EJB jars. A new attribute "dependency" can be
   defined which controls what classes are added. The addition of classes now uses
   the Jakarta-BCEL library rather than reflection, meaning bean classes are
   no longer loaded into Ant's JVM. The default dependency analyzer is known as
   the ancestor analyzer. It provides the same behaviour as the 1.4.1 version of
   <ejbjar>. If the BCEL library is not present, a warning will be issued stating
   the ancestor analyzer is not available. In this case <ejbjar> will continue
   to function but will not add super classes to the jar.
 
 * <available> has a new attribute named ignoreSystemClasses.
 
 * New task <cvschangelog/> generates an XML report of changes that occur
   on CVS repository.
 
 * New filter readers: ClassConstants, ExpandProperties, HeadFilter,
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index f374a31bd..849a92390 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1036 +1,1107 @@
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
 import java.util.Vector;
+import java.util.Hashtable;
+import java.util.Enumeration;
+
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
         for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
             defaultExcludes.add(DEFAULTEXCLUDES[i]);
         }
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
      * Sets whether or not the file system should be regarded as case sensitive.
      *
      * @param isCaseSensitive whether or not the file system should be
      *                        regarded as a case sensitive one
      */
     public void setCaseSensitive(boolean isCaseSensitive) {
         this.isCaseSensitive = isCaseSensitive;
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
-        scandir(basedir, "", true);
+        checkIncludePatterns();
+    }
+    /**
+     *  this routine is actually checking all the include patterns
+     *  in order to avoid scanning everything under base dir
+     *  @since ant1.6
+     */
+    private void checkIncludePatterns() {
+      Hashtable newroots = new Hashtable();
+      // put in the newroots vector the include patterns without wildcard tokens
+      for (int icounter=0; icounter<includes.length; icounter++) {
+        String newpattern=SelectorUtils.rtrimWildcardTokens(includes[icounter]);
+        // check whether the candidate new pattern has a parent
+        boolean hasParent=false;
+        Enumeration myenum = newroots.keys();
+        while (myenum.hasMoreElements()) {
+          String existingpattern=(String)myenum.nextElement();
+          if (existingpattern.length() <= newpattern.length()) {
+            if (newpattern.indexOf(existingpattern)==0) {
+              hasParent=true;
+            }
+          }
+        }
+        if ( !hasParent) {
+          newroots.put(newpattern,includes[icounter]);
+        }
+      }
+      Enumeration enum2 = newroots.keys();
+      while (enum2.hasMoreElements()) {
+        String currentelement = (String) enum2.nextElement();
+        File myfile=new File(basedir,currentelement);
+        if (myfile.exists()) {
+          if (myfile.isDirectory()) {
+            if (isIncluded(currentelement) && currentelement.length()>0) {
+                accountForIncludedDir(currentelement,myfile,true);
+            }  else {
+                if (currentelement.length() > 0) {
+                    if (currentelement.charAt(currentelement.length()-1) != File.separatorChar) {
+                        currentelement = currentelement + File.separatorChar;
+                    }
+                }
+                scandir(myfile, currentelement, true);
+            }
+          }
+          else {
+            String originalpattern=(String)newroots.get(currentelement);
+            if (originalpattern.equals(currentelement)) {
+              accountForIncludedFile(currentelement,myfile);
+            }
+          }
+        }
+      }
     }
-
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
-                    if (!isExcluded(name)) {
-                        if (isSelected(name, file)) {
-                            dirsIncluded.addElement(name);
-                            if (fast) {
-                                scandir(file, name + File.separator, fast);
-                            }
-                        } else {
-                            everythingIncluded = false;
-                            dirsDeselected.addElement(name);
-                            if (fast && couldHoldIncluded(name)) {
-                                scandir(file, name + File.separator, fast);
-                            }
-                        }
-
-                    } else {
-                        everythingIncluded = false;
-                        dirsExcluded.addElement(name);
-                        if (fast && couldHoldIncluded(name)) {
-                            scandir(file, name + File.separator, fast);
-                        }
-                    }
+                  accountForIncludedDir(name, file, fast);
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
-                    if (!isExcluded(name)) {
-                        if (isSelected(name, file)) {
-                            filesIncluded.addElement(name);
-                        } else {
-                            everythingIncluded = false;
-                            filesDeselected.addElement(name);
-                        }
-                    } else {
-                        everythingIncluded = false;
-                        filesExcluded.addElement(name);
-                    }
+                    accountForIncludedFile(name, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             }
         }
     }
+    /**
+     * process included file
+     * @param name  path of the file relative to the directory of the fileset
+     * @param file  included file
+     */
+    private void accountForIncludedFile(String name, File file) {
+        if (!isExcluded(name)) {
+            if (isSelected(name, file)) {
+                filesIncluded.addElement(name);
+            } else {
+                everythingIncluded = false;
+                filesDeselected.addElement(name);
+            }
+        } else {
+            everythingIncluded = false;
+            filesExcluded.addElement(name);
+        }
+
+    }
+    /**
+     *
+     * @param name path of the directory relative to the directory of the fileset
+     * @param file directory as file
+     * @param fast
+     */
+    private void accountForIncludedDir(String name, File file, boolean fast) {
+      if (!isExcluded(name)) {
+          if (isSelected(name, file)) {
+              dirsIncluded.addElement(name);
+              if (fast) {
+                  scandir(file, name + File.separator, fast);
+              }
+          } else {
+              everythingIncluded = false;
+              dirsDeselected.addElement(name);
+              if (fast && couldHoldIncluded(name)) {
+                  scandir(file, name + File.separator, fast);
+              }
+          }
+
+      } else {
+          everythingIncluded = false;
+          dirsExcluded.addElement(name);
+          if (fast && couldHoldIncluded(name)) {
+              scandir(file, name + File.separator, fast);
+          }
+      }
 
+    }
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
                 return true;
             }
         }
         return false;
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
 
 }
diff --git a/src/main/org/apache/tools/ant/types/selectors/SelectorUtils.java b/src/main/org/apache/tools/ant/types/selectors/SelectorUtils.java
index 62009dbef..0ee81243d 100644
--- a/src/main/org/apache/tools/ant/types/selectors/SelectorUtils.java
+++ b/src/main/org/apache/tools/ant/types/selectors/SelectorUtils.java
@@ -1,654 +1,693 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 
 package org.apache.tools.ant.types.selectors;
 
 import java.io.File;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import org.apache.tools.ant.types.Resource;
 
 /**
  * <p>This is a utility class used by selectors and DirectoryScanner. The
  * functionality more properly belongs just to selectors, but unfortunately
  * DirectoryScanner exposed these as protected methods. Thus we have to
  * support any subclasses of DirectoryScanner that may access these methods.
  * </p>
  * <p>This is a Singleton.</p>
  *
  * @author Arnout J. Kuiper
  * <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
  * @author Magesh Umasankar
  * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
  * @since 1.5
  */
 public final class SelectorUtils {
 
     private static SelectorUtils instance = new SelectorUtils();
 
     /**
      * Private Constructor
      */
     private SelectorUtils() {
     }
 
     /**
      * Retrieves the instance of the Singleton.
      * @return singleton instance
      */
     public static SelectorUtils getInstance() {
         return instance;
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
     public static boolean matchPatternStart(String pattern, String str) {
         return matchPatternStart(pattern, str, true);
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
     public static boolean matchPatternStart(String pattern, String str,
                                             boolean isCaseSensitive) {
         // When str starts with a File.separator, pattern has to start with a
         // File.separator.
         // When pattern starts with a File.separator, str has to start with a
         // File.separator.
         if (str.startsWith(File.separator)
                 != pattern.startsWith(File.separator)) {
             return false;
         }
 
         String[] patDirs = tokenizePathAsArray(pattern);
         String[] strDirs = tokenizePathAsArray(str);
 
         int patIdxStart = 0;
         int patIdxEnd = patDirs.length - 1;
         int strIdxStart = 0;
         int strIdxEnd = strDirs.length - 1;
 
         // up to first '**'
         while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
             String patDir = patDirs[patIdxStart];
             if (patDir.equals("**")) {
                 break;
             }
             if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                 return false;
             }
             patIdxStart++;
             strIdxStart++;
         }
 
         if (strIdxStart > strIdxEnd) {
             // String is exhausted
             return true;
         } else if (patIdxStart > patIdxEnd) {
             // String not exhausted, but pattern is. Failure.
             return false;
         } else {
             // pattern now holds ** while string is not exhausted
             // this will generate false positives but we can live with that.
             return true;
         }
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
     public static boolean matchPath(String pattern, String str) {
         return matchPath(pattern, str, true);
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
     public static boolean matchPath(String pattern, String str,
                                     boolean isCaseSensitive) {
         // When str starts with a File.separator, pattern has to start with a
         // File.separator.
         // When pattern starts with a File.separator, str has to start with a
         // File.separator.
         if (str.startsWith(File.separator)
                 != pattern.startsWith(File.separator)) {
             return false;
         }
 
         String[] patDirs = tokenizePathAsArray(pattern);
         String[] strDirs = tokenizePathAsArray(str);
 
         int patIdxStart = 0;
         int patIdxEnd = patDirs.length - 1;
         int strIdxStart = 0;
         int strIdxEnd = strDirs.length - 1;
 
         // up to first '**'
         while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
             String patDir = patDirs[patIdxStart];
             if (patDir.equals("**")) {
                 break;
             }
             if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                 patDirs = null;
                 strDirs = null;
                 return false;
             }
             patIdxStart++;
             strIdxStart++;
         }
         if (strIdxStart > strIdxEnd) {
             // String is exhausted
             for (int i = patIdxStart; i <= patIdxEnd; i++) {
                 if (!patDirs[i].equals("**")) {
                     patDirs = null;
                     strDirs = null;
                     return false;
                 }
             }
             return true;
         } else {
             if (patIdxStart > patIdxEnd) {
                 // String not exhausted, but pattern is. Failure.
                 patDirs = null;
                 strDirs = null;
                 return false;
             }
         }
 
         // up to last '**'
         while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
             String patDir = patDirs[patIdxEnd];
             if (patDir.equals("**")) {
                 break;
             }
             if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
                 patDirs = null;
                 strDirs = null;
                 return false;
             }
             patIdxEnd--;
             strIdxEnd--;
         }
         if (strIdxStart > strIdxEnd) {
             // String is exhausted
             for (int i = patIdxStart; i <= patIdxEnd; i++) {
                 if (!patDirs[i].equals("**")) {
                     patDirs = null;
                     strDirs = null;
                     return false;
                 }
             }
             return true;
         }
 
         while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
             int patIdxTmp = -1;
             for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                 if (patDirs[i].equals("**")) {
                     patIdxTmp = i;
                     break;
                 }
             }
             if (patIdxTmp == patIdxStart + 1) {
                 // '**/**' situation, so skip one
                 patIdxStart++;
                 continue;
             }
             // Find the pattern between padIdxStart & padIdxTmp in str between
             // strIdxStart & strIdxEnd
             int patLength = (patIdxTmp - patIdxStart - 1);
             int strLength = (strIdxEnd - strIdxStart + 1);
             int foundIdx = -1;
             strLoop:
                         for (int i = 0; i <= strLength - patLength; i++) {
                             for (int j = 0; j < patLength; j++) {
                                 String subPat = patDirs[patIdxStart + j + 1];
                                 String subStr = strDirs[strIdxStart + i + j];
                                 if (!match(subPat, subStr, isCaseSensitive)) {
                                     continue strLoop;
                                 }
                             }
 
                             foundIdx = strIdxStart + i;
                             break;
                         }
 
             if (foundIdx == -1) {
                 patDirs = null;
                 strDirs = null;
                 return false;
             }
 
             patIdxStart = patIdxTmp;
             strIdxStart = foundIdx + patLength;
         }
 
         for (int i = patIdxStart; i <= patIdxEnd; i++) {
             if (!patDirs[i].equals("**")) {
                 patDirs = null;
                 strDirs = null;
                 return false;
             }
         }
 
         return true;
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
         return match(pattern, str, true);
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
     public static boolean match(String pattern, String str,
                                 boolean isCaseSensitive) {
         char[] patArr = pattern.toCharArray();
         char[] strArr = str.toCharArray();
         int patIdxStart = 0;
         int patIdxEnd = patArr.length - 1;
         int strIdxStart = 0;
         int strIdxEnd = strArr.length - 1;
         char ch;
 
         boolean containsStar = false;
         for (int i = 0; i < patArr.length; i++) {
             if (patArr[i] == '*') {
                 containsStar = true;
                 break;
             }
         }
 
         if (!containsStar) {
             // No '*'s, so we make a shortcut
             if (patIdxEnd != strIdxEnd) {
                 return false; // Pattern and string do not have the same size
             }
             for (int i = 0; i <= patIdxEnd; i++) {
                 ch = patArr[i];
                 if (ch != '?') {
                     if (isCaseSensitive && ch != strArr[i]) {
                         return false; // Character mismatch
                     }
                     if (!isCaseSensitive && Character.toUpperCase(ch)
                             != Character.toUpperCase(strArr[i])) {
                         return false;  // Character mismatch
                     }
                 }
             }
             return true; // String matches against pattern
         }
 
         if (patIdxEnd == 0) {
             return true; // Pattern contains only '*', which matches anything
         }
 
         // Process characters before first star
         while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
             if (ch != '?') {
                 if (isCaseSensitive && ch != strArr[strIdxStart]) {
                     return false; // Character mismatch
                 }
                 if (!isCaseSensitive && Character.toUpperCase(ch)
                         != Character.toUpperCase(strArr[strIdxStart])) {
                     return false; // Character mismatch
                 }
             }
             patIdxStart++;
             strIdxStart++;
         }
         if (strIdxStart > strIdxEnd) {
             // All characters in the string are used. Check if only '*'s are
             // left in the pattern. If so, we succeeded. Otherwise failure.
             for (int i = patIdxStart; i <= patIdxEnd; i++) {
                 if (patArr[i] != '*') {
                     return false;
                 }
             }
             return true;
         }
 
         // Process characters after last star
         while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
             if (ch != '?') {
                 if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                     return false; // Character mismatch
                 }
                 if (!isCaseSensitive && Character.toUpperCase(ch)
                         != Character.toUpperCase(strArr[strIdxEnd])) {
                     return false; // Character mismatch
                 }
             }
             patIdxEnd--;
             strIdxEnd--;
         }
         if (strIdxStart > strIdxEnd) {
             // All characters in the string are used. Check if only '*'s are
             // left in the pattern. If so, we succeeded. Otherwise failure.
             for (int i = patIdxStart; i <= patIdxEnd; i++) {
                 if (patArr[i] != '*') {
                     return false;
                 }
             }
             return true;
         }
 
         // process pattern between stars. padIdxStart and patIdxEnd point
         // always to a '*'.
         while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
             int patIdxTmp = -1;
             for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                 if (patArr[i] == '*') {
                     patIdxTmp = i;
                     break;
                 }
             }
             if (patIdxTmp == patIdxStart + 1) {
                 // Two stars next to each other, skip the first one.
                 patIdxStart++;
                 continue;
             }
             // Find the pattern between padIdxStart & padIdxTmp in str between
             // strIdxStart & strIdxEnd
             int patLength = (patIdxTmp - patIdxStart - 1);
             int strLength = (strIdxEnd - strIdxStart + 1);
             int foundIdx = -1;
             strLoop:
             for (int i = 0; i <= strLength - patLength; i++) {
                 for (int j = 0; j < patLength; j++) {
                     ch = patArr[patIdxStart + j + 1];
                     if (ch != '?') {
                         if (isCaseSensitive && ch != strArr[strIdxStart + i
                                 + j]) {
                             continue strLoop;
                         }
                         if (!isCaseSensitive && Character.toUpperCase(ch) !=
                                 Character.toUpperCase(strArr[strIdxStart + i
                                 + j])) {
                             continue strLoop;
                         }
                     }
                 }
 
                 foundIdx = strIdxStart + i;
                 break;
             }
 
             if (foundIdx == -1) {
                 return false;
             }
 
             patIdxStart = patIdxTmp;
             strIdxStart = foundIdx + patLength;
         }
 
         // All characters in the string are used. Check if only '*'s are left
         // in the pattern. If so, we succeeded. Otherwise failure.
         for (int i = patIdxStart; i <= patIdxEnd; i++) {
             if (patArr[i] != '*') {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Breaks a path up into a Vector of path elements, tokenizing on
      * <code>File.separator</code>.
      *
      * @param path Path to tokenize. Must not be <code>null</code>.
      *
-     * @return a Vector of path elements from the tokenized path
-     */
-    public static Vector tokenizePath(String path) {
-        Vector ret = new Vector();
-        StringTokenizer st = new StringTokenizer(path, File.separator);
-        while (st.hasMoreTokens()) {
-            ret.addElement(st.nextToken());
-        }
-        return ret;
-    }
+    * @return a Vector of path elements from the tokenized path
+    */
+   public static Vector tokenizePath (String path) {
+     return tokenizePath(path, File.separator);
+   }
+ /**
+  * Breaks a path up into a Vector of path elements, tokenizing on
+  *
+  * @param path Path to tokenize. Must not be <code>null</code>.
+  * @param separator the separator against which to tokenize.
+  *
+  * @return a Vector of path elements from the tokenized path
+  * @since ant 1.6
+  */
+   public static Vector tokenizePath (String path, String separator) {
+     Vector ret = new Vector();
+     StringTokenizer st = new StringTokenizer(path,separator);
+     while (st.hasMoreTokens()) {
+         ret.addElement(st.nextToken());
+     }
+     return ret;
+
+   }
 
     /**
      * Same as {@link #tokenizePath tokenizePath} but hopefully faster.
      */
     private static String[] tokenizePathAsArray(String path) {
         char sep = File.separatorChar;
         int start = 0;
         int len = path.length();
         int count = 0;
         for (int pos = 0; pos < len; pos++) {
             if (path.charAt(pos) == sep) {
                 if (pos != start) {
                     count++;
                 }
                 start = pos + 1;
             }
         }
         if (len != start) {
             count++;
         }
         String[] l = new String[count];
         count = 0;
         start = 0;
         for (int pos = 0; pos < len; pos++) {
             if (path.charAt(pos) == sep) {
                 if (pos != start) {
                     String tok = path.substring(start, pos);
                     l[count++] = tok;
                 }
                 start = pos + 1;
             }
         }
         if (len != start) {
             String tok = path.substring(start);
             l[count/*++*/] = tok;
         }
         return l;
     }
 
 
     /**
      * Returns dependency information on these two files. If src has been
      * modified later than target, it returns true. If target doesn't exist,
      * it likewise returns true. Otherwise, target is newer than src and
      * is not out of date, thus the method returns false. It also returns
      * false if the src file doesn't even exist, since how could the
      * target then be out of date.
      *
      * @param src the original file
      * @param target the file being compared against
      * @param granularity the amount in seconds of slack we will give in
      *        determining out of dateness
      * @return whether the target is out of date
      */
     public static boolean isOutOfDate(File src, File target, int granularity) {
         if (!src.exists()) {
             return false;
         }
         if (!target.exists()) {
             return true;
         }
         if ((src.lastModified() - granularity) > target.lastModified()) {
             return true;
         }
         return false;
     }
 
     /**
      * Returns dependency information on these two resources. If src has been
      * modified later than target, it returns true. If target doesn't exist,
      * it likewise returns true. Otherwise, target is newer than src and
      * is not out of date, thus the method returns false. It also returns
      * false if the src file doesn't even exist, since how could the
      * target then be out of date.
      *
      * @param src the original resource
      * @param target the resource being compared against
      * @param granularity the amount in seconds of slack we will give in
      *        determining out of dateness
      * @return whether the target is out of date
      */
     public static boolean isOutOfDate(Resource src, Resource target,
                                       int granularity) {
         if (!src.isExists()) {
             return false;
         }
         if (!target.isExists()) {
             return true;
         }
         if ((src.getLastModified() - granularity) > target.getLastModified()) {
             return true;
         }
         return false;
     }
 
     /**
      * "Flattens" a string by removing all whitespace (space, tab, linefeed,
      * carriage return, and formfeed). This uses StringTokenizer and the
      * default set of tokens as documented in the single arguement constructor.
      *
      * @param input a String to remove all whitespace.
      * @return a String that has had all whitespace removed.
      */
     public static String removeWhitespace(String input) {
         StringBuffer result = new StringBuffer();
         if (input != null) {
             StringTokenizer st = new StringTokenizer(input);
             while (st.hasMoreTokens()) {
                 result.append(st.nextToken());
             }
         }
         return result.toString();
     }
-
+    /**
+     * Tests if a string contains stars or question marks
+     *  @param input a String which one wants to test for containing wildcard
+     * @return true if the string contains at least a star or a question mark
+     */
+    public static boolean hasWildcards(String input) {
+        return (input.indexOf('*')!=-1 || input.indexOf('?')!=-1);
+    }
+    /**
+     * removes from a pattern all tokens to the right containing wildcards
+     * @param input
+     * @return the leftmost part of the pattern without wildcards
+     */
+    public static String rtrimWildcardTokens(String input) {
+        Vector v = tokenizePath(input, File.separator);
+        StringBuffer sb = new StringBuffer();
+        for (int counter=0; counter < v.size() ; counter++) {
+            if ( hasWildcards((String)v.elementAt(counter))) {
+                break;
+            }
+            if (counter >0) {
+                sb.append(File.separator);
+            }
+            sb.append((String)v.elementAt(counter));
+        }
+        return sb.toString();
+    }
 }
 
