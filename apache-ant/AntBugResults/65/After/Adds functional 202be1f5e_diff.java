diff --git a/WHATSNEW b/WHATSNEW
index 3f2ea59fa..064ade6c6 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1391 +1,1397 @@
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
 
 Fixed bugs:
 -----------
 * Filter readers were not handling line endings properly.  Bugzilla
   Report 18476.
 
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
 
 * Perforce tasks relying on output from the server such as <p4change> and <p4label>
   were hanging. Bugzilla Reports 18129 and 18956.
 
 * build.sh install had a problem on cygwin (with REALANTHOME).
   Bugzilla Report 17257
 
 * <replaceregexp> didn't work for multi-byte encodings if byline was false.
   Bugzilla Report 19187.
 
 * file names that include spaces need to be quoted inside the @argfile
   argument using forked <javac> and JDK 1.4.  Bugzilla Report 10499.
 
 * Setting filesonly to true in <zip> and related tasks would cause the
   archives to be always recreated.  Bugzilla Report 19449.
 
 * The Visual Age for Java tasks didn't work (at least for versions 3.0
   and higher).  Bugzilla Report 10016.
 
 * URL-encoding in <vaj*port> didn't work properly.
 
 * file names that include spaces need to be quoted inside the @argfile
   argument using <javadoc> and JDK 1.4.  Bugzilla Report 16871.
 
 * <junit> didn't work with custom formatters that were only available
   on the user specified classpath when a timeout occured.  Bugzilla
   Report 19953.
 
 * <different> selector : make ignoreFileTimes effectively default to true
   and fix a bug in the comparison of timestamps. Bugzilla Report 20205.
 
 * <different> selector can now be nested directly under a fileset
   Bugzilla Report 20220.
 
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
 The implementation only with JavaMail (encoding="MIME").
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
 
 * <apply> and <chmod> now support nested <filelist>s as well.
   Bugzilla Report 15929.
 
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
 
+* <ejbjar>  
+  cmpversion attribute added 
+  jboss element will look for jbosscmp-jdbc.xml descriptor 
+  if ejbjar has cmpversion="2.0" set
+  Bugzilla Reports 14707 and 14709.
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
diff --git a/docs/manual/OptionalTasks/ejb.html b/docs/manual/OptionalTasks/ejb.html
index adb95d7ab..ba55d24da 100644
--- a/docs/manual/OptionalTasks/ejb.html
+++ b/docs/manual/OptionalTasks/ejb.html
@@ -1,1732 +1,1750 @@
 <html>
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <title>EJB Tasks</title>
 
 </head>
 
 <body>
 
 <h1>Ant EJB Tasks User Manual</h1>
 <p>by</p>
 <!-- Names are in alphabetical order, on last name -->
 <ul>
   <li>Paul Austin (<a href="mailto:p_d_austin@yahoo.com">p_d_austin@yahoo.com</a>)</li>
   <li>Holger Engels (<a href="mailto:hengels@innovidata.com">hengels@innovidata.com</a>)</li>
   <li>Tim Fennell (<a href="mailto:tfenne@rcn.com">tfenne@rcn.com</a>)</li>
   <li>Martin Gee (<a href="mailto:martin.gee@icsynergy.com">martin.gee@icsynergy.com</a>)</li>
   <li>Conor MacNeill</li>
   <li>Cyrille Morvan (<a href="mailto:cmorvan@ingenosya.com">cmorvan@ingenosya.com</a>)</li>
   <li>Greg Nelson (<a href="mailto:gn@sun.com">gn@sun.com</a>)</li>
+  <li>Rob van Oostrum(<a href="mailto:rvanoo@xs4all.nl">rvanoo@xs4all.nl</a>)</li>
 </ul>
 
 <p>Version @VERSION@<br>
 $Id$
 </p>
 <hr>
 <h2>Table of Contents</h2>
 <ul>
   <li><a href="#introduction">Introduction</a></li>
   <li><a href="#ejbtasks">EJB Tasks</a></li>
 </ul>
 
 <hr>
 <h2><a name="introduction">Introduction</a></h2>
 <p>Ant provides a number of optional tasks for developing
 <a href="http://java.sun.com/products/ejb" target="_top">Enterprise Java Beans (EJBs)</a>.
 In general these tasks are specific to the particular vendor's EJB Server.</p>
 
 <p> At present the tasks support:<br>
 
 <ul>
   <li><a href="http://www.borland.com">Borland </a>
   Application Server 4.5</li>
   <li><a href="http://www.iplanet.com">iPlanet </a>
   Application Server 6.0</li>
   <li><a href="http://www.jboss.org/" target="_top">
   JBoss 2.1</a> and above EJB servers</li>
   <li><a href="http://www.bea.com" target="_top">Weblogic</a>
    4.5.1 through to 7.0 EJB servers</li>
   <li><a href="http://www.objectweb.org/jonas/" target="_top">JOnAS</a>
    2.4.x and 2.5 Open Source EJB server</li>
   <li><a href="http://www.ibm.com/websphere">IBM WebSphere</a> 4.0</li>
 </ul>
   Over time we expect further optional tasks  to support additional EJB Servers.
 </p>
 
 <hr>
 <h2><a name="ejbtasks">EJB Tasks</a></h2>
 <table border="1" cellpadding="5">
  <tr><td>Task</td><td colspan="2">Application Servers</td></tr>
  <tr><td><a href="BorlandGenerateClient.html">blgenclient</a></td><td colspan="2">Borland Application Server 4.5</td></tr>
  <tr><td><a href="#ddcreator">ddcreator</a></td><td colspan="2">Weblogic 4.5.1</td></tr>
  <tr><td><a href="#ejbc">ejbc</a></td><td colspan="2">Weblogic 4.5.1</td></tr>
  <tr><td><a href="#iplanet-ejbc">iplanet-ejbc</a></td><td colspan="2">iPlanet Application Server 6.0</td></tr>
  <tr><td rowspan="7"><a href="#ejbjar">ejbjar</a></td><td colspan="2" align="center"><b>Nested Elements</b></td></tr>
  <tr><td><a href="BorlandEJBTasks.html">borland</a></td><td>Borland Application Server 4.5</td></tr>
  <tr><td><a href="#ejbjar_iplanet">iPlanet</a></td><td>iPlanet Application Server 6.0</td></tr>
  <tr><td><a href="#ejbjar_jboss">jboss</a></td><td>JBoss</td></tr>
  <tr><td><a href="#ejbjar_jonas">jonas</a></td><td>JOnAS 2.4.x and 2.5</td></tr>
  <tr><td><a href="#ejbjar_weblogic">weblogic</a></td><td>Weblogic 5.1 to 7.0</td></tr>
  <tr><td><a href="#ejbjar_websphere">websphere</a></td><td>IBM WebSphere 4.0</td></tr>
  <tr><td><a href="#wlrun">wlrun</a></td><td colspan="2">Weblogic 4.5.1 to 7.0</td></tr>
  <tr><td><a href="#wlstop">wlstop</a></td><td colspan="2">Weblogic 4.5.1 to 7.0</td></tr>
 
 </table>
 
 <hr>
 <h2><a name="ddcreator">ddcreator</a></h2>
 <h3><b>Description:</b></h3>
 <p>ddcreator will compile a set of Weblogic text-based deployment descriptors into a serialized
 EJB deployment descriptor. The selection of which of the text-based descriptors are to be compiled
 is based on the standard Ant include and exclude selection mechanisms.
 </p>
 
 <h3>Parameters:</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">descriptors</td>
     <td valign="top">This is the base directory from which descriptors are selected.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">dest</td>
     <td valign="top">The directory where the serialized deployment descriptors will be written</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">This is the classpath to use to run the underlying weblogic ddcreator tool.
                      This must include the <code>weblogic.ejb.utils.DDCreator</code> class</td>
     <td valign="top" align="center">No</td>
   </tr>
 </table>
 <h3>Examples</h3>
 <pre>&lt;ddcreator descriptors=&quot;${dd.dir}&quot;
            dest=&quot;${gen.classes}&quot;
            classpath=&quot;${descriptorbuild.classpath}&quot;&gt;
   &lt;include name=&quot;*.txt&quot;/&gt;
 &lt;/ddcreator&gt;
 </pre>
 
 <hr>
 <h2><a name="ejbc">ejbc</a></h2>
 <h3><b>Description:</b></h3>
 <p>The ejbc task will run Weblogic's ejbc tool. This tool will take a serialized deployment descriptor,
 examine the various EJB interfaces and bean classes and then generate the required support classes
 necessary to deploy the bean in a Weblogic EJB container. This will include the RMI stubs and skeletons
 as well as the classes which implement the bean's home and remote interfaces.</p>
 <p>
 The ant task which runs this tool is able to compile several beans in a single operation. The beans to be
 compiled are selected by including their serialized deployment descriptors. The standard ant
 <code>include</code> and <code>exclude</code> constructs can be used to select the deployment descriptors
 to be included. </p>
 <p>
 Each descriptor is examined to determine whether the generated classes are out of date and need to be
 regenerated. The deployment descriptor is de-serialized to discover the home, remote and
 implementation classes. The corresponding source files are determined and checked to see their
 modification times. These times and the modification time of the serialized descriptor itself are
 compared with the modification time of the generated classes. If the generated classes are not present
 or are out of date, the ejbc tool is run to generate new versions.</p>
 <h3>Parameters:</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">descriptors</td>
     <td valign="top">This is the base directory from which the serialized deployment descriptors are selected.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">dest</td>
     <td valign="top">The base directory where the generated classes, RIM stubs and RMI skeletons are written</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">manifest</td>
     <td valign="top">The name of a manifest file to be written. This manifest will contain an entry for each EJB processed</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">src</td>
     <td valign="top">The base directory of the source tree containing the source files of the home interface,
                      remote interface and bean implementation classes.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">This classpath must include both the <code>weblogic.ejbc</code> class and the
                      class files of the bean, home interface, remote interface, etc of the bean being
                      processed.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">keepgenerated</td>
     <td>Controls whether ejbc will keep the
         intermediate Java files used to build the class files. This can be
         useful when debugging.</td>
     <td>No, defaults to false.</td>
   </tr>
 </table>
 <h3>Examples</h3>
 <pre>&lt;ejbc descriptors=&quot;${gen.classes}&quot;
            src=&quot;${src.dir}&quot;
            dest=&quot;${gen.classes}&quot;
            manifest=&quot;${build.manifest}&quot;
            classpath=&quot;${descriptorbuild.classpath}&quot;&gt;
   &lt;include name=&quot;*.ser&quot;/&gt;
 &lt;/ejbc&gt;
 </pre>
 
 <hr>
 <h2>
 <a NAME="iplanet-ejbc"></a>iplanet-ejbc</h2>
 
 <h3>
 <b>Description:</b></h3>
 Task to compile EJB stubs and skeletons for the iPlanet Application Server
 6.0.  Given a standard EJB 1.1 XML descriptor as well as an iAS-specific
 EJB descriptor, this task will generate the stubs and skeletons required
 to deploy the EJB to iAS.  Since the XML descriptors can include multiple
 EJBs, this is a convenient way of specifying many EJBs in a single Ant
 task.
 <p>For each EJB specified, the task will locate the three classes that
 comprise the EJB in the destination directory.  If these class files
 cannot be located in the destination directory, the task will fail. The
 task will also attempt to locate the EJB stubs and skeletons in this directory.
 If found, the timestamps on the stubs and skeletons will be checked to
 ensure they are up to date. Only if these files cannot be found or if they
 are out of date will the iAS ejbc utility be called to generate new stubs
 and skeletons.</p>
 <h3>
 Parameters:</h3>
 
 <table BORDER CELLSPACING=0 CELLPADDING=2 >
 <tr>
 <td VALIGN=TOP><b>Attribute</b></td>
 
 <td VALIGN=TOP><b>Description</b></td>
 
 <td ALIGN=CENTER VALIGN=TOP><b>Required</b></td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>ejbdescriptor</td>
 
 <td VALIGN=TOP>Standard EJB 1.1 XML descriptor (typically titled "ejb-jar.xml").</td>
 
 <td ALIGN=CENTER VALIGN=TOP>Yes</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>iasdescriptor</td>
 
 <td VALIGN=TOP>iAS-specific EJB XML descriptor (typically titled "ias-ejb-jar.xml").</td>
 
 <td ALIGN=CENTER VALIGN=TOP>Yes</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>dest</td>
 
 <td VALIGN=TOP>The is the base directory where the RMI stubs and skeletons
 are written. In addition, the class files for each bean (home interface,
 remote interface, and EJB implementation) must be found in this directory.</td>
 
 <td ALIGN=CENTER VALIGN=TOP>Yes</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>classpath</td>
 
 <td VALIGN=TOP>The classpath used when generating EJB stubs and skeletons.
 If omitted, the classpath specified when Ant was started will be used.
 Nested "classpath" elements may also be used.</td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>keepgenerated</td>
 
 <td VALIGN=TOP>Indicates whether or not the Java source files which are
 generated by ejbc will be saved or automatically deleted. If "yes", the
 source files will be retained. If omitted, it defaults to "no". </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>debug</td>
 
 <td>Indicates whether or not the ejbc utility should log additional debugging
 statements to the standard output. If "yes", the additional debugging statements
 will be generated.  If omitted, it defaults to "no". </td>
 
 <td ALIGN=CENTER VALIGN=TOP>
 <center>No</center>
 </td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>iashome</td>
 
 <td>May be used to specify the "home" directory for this iAS installation.
 This is used to find the ejbc utility if it isn't included in the user's
 system path. If specified, it should refer to the "[install-location]/iplanet/ias6/ias"
 directory. If omitted, the ejbc utility must be on the user's system path. </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 </table>
 
 <h3>
 Examples</h3>
 
 <pre>&lt;iplanet-ejbc ejbdescriptor="ejb-jar.xml"
               iasdescriptor="ias-ejb-jar.xml"
               dest="${build.classesdir}"
               classpath="${ias.ejbc.cpath}"/&gt;
 
 
 &lt;iplanet-ejbc ejbdescriptor="ejb-jar.xml"
               iasdescriptor="ias-ejb-jar.xml"
               dest="${build.classesdir}"
               keepgenerated="yes"
               debug="yes"
               iashome="${ias.home}"&gt;
               &lt;classpath&gt;
                   &lt;pathelement path="."/&gt;
                   &lt;pathelement path="${build.classpath}"/&gt;
               &lt;/classpath&gt;
 &lt;/iplanet-ejbc&gt;
 
 
 </pre>
 
 <hr>
 <h2><a name="wlrun">wlrun</a></h2>
 <h3><b>Description:</b></h3>
 
 <p>The <code>wlrun</code> task is used to start a weblogic server. The task runs
 a weblogic instance in a separate Java Virtual Machine. A number of parameters
 are used to control the operation of the weblogic instance. Note that the task,
 and hence ant, will not complete until the weblogic instance is stopped.</p>
 
 <h3>Parameters:</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required for 4.5.1 and 5.1</b></td>
     <td align="center" valign="top"><b>Required for 6.0</b></td>
   </tr>
   <tr>
     <td valign="top">BEA Home</td>
     <td valign="top">The location of the BEA Home where the server's config is defined.
                      If this attribute is present, wlrun assumes that the server will
                      be running under Weblogic 6.0</td>
     <td valign="top" align="center">N/A</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">home</td>
     <td valign="top">The location of the weblogic home that is to be used. This is the location
                      where weblogic is installed.</td>
     <td valign="top" align="center">Yes</td>
     <td valign="top" align="center">Yes. Note this is the absolute location, not relative to
                                     BEA home.</td>
   </tr>
   <tr>
     <td valign="top">Domain</td>
     <td valign="top">The domain to which the server belongs.</td>
     <td valign="top" align="center">N/A</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to be used with the Java Virtual Machine that runs the Weblogic
                      Server. Prior to Weblogic 6.0, this is typically set to the Weblogic
                      boot classpath. Under Weblogic 6.0 this should include all the
                      weblogic jars</td>
     <td valign="top" align="center">Yes</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">wlclasspath</td>
     <td valign="top">The weblogic classpath used by the Weblogic Server.</td>
     <td valign="top" align="center">No</td>
     <td valign="top" align="center">N/A</td>
   </tr>
   <tr>
     <td valign="top">properties</td>
     <td valign="top">The name of the server's properties file within the weblogic home directory
                      used to control the weblogic instance.</td>
     <td valign="top" align="center">Yes</td>
     <td valign="top" align="center">N/A</td>
   </tr>
   <tr>
     <td valign="top">name</td>
     <td valign="top">The name of the weblogic server within the weblogic home which is to be run.
                      This defaults to &quot;myserver&quot;</td>
     <td valign="top" align="center">No</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">policy</td>
     <td valign="top">The name of the security policy file within the weblogic home directory that
                      is to be used. If not specified, the default policy file <code>weblogic.policy</code>
                      is used.</td>
     <td valign="top" align="center">No</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">username</td>
     <td valign="top">The management username used to manage the server</td>
     <td valign="top" align="center">N/A</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">password</td>
     <td valign="top">The server's management password</td>
     <td valign="top" align="center">N/A</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">pkPassword</td>
     <td valign="top">The private key password so the server can decrypt the SSL
                      private key file</td>
     <td valign="top" align="center">N/A</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">jvmargs</td>
     <td valign="top">Additional argument string passed to the Java Virtual Machine used to run the
                      Weblogic instance.</td>
     <td valign="top" align="center">No</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">weblogicMainClass</td>
     <td valign="top">name of the main class for weblogic</td>
     <td valign="top" align="center">No</td>
     <td valign="top" align="center">No</td>
   </tr>
 </table>
 
 <h3>Nested Elements</h3>
 
 <p>The wlrun task supports nested &lt;classpath&gt; and &lt;wlclasspath&gt;
 elements to set the repsective classpaths.</p>
 
 <h3>Examples</h3>
 
 <p>This example shows the use of wlrun to run a server under Weblogic 5.1</p>
 
 <pre>
     &lt;wlrun taskname=&quot;myserver&quot;
            classpath=&quot;${weblogic.boot.classpath}&quot;
            wlclasspath=&quot;${weblogic.classes}:${code.jars}&quot;
            name=&quot;myserver&quot;
            home=&quot;${weblogic.home}&quot;
            properties=&quot;myserver/myserver.properties&quot;/&gt;
 </pre>
 
 <p>This example shows wlrun being used to run the petstore server under
 Weblogic 6.0</p>
 
 <pre>
     &lt;wlrun taskname=&quot;petstore&quot;
            classpath=&quot;${weblogic.classes}&quot;
            name=&quot;petstoreServer&quot;
            domain=&quot;petstore&quot;
            home=&quot;${weblogic.home}&quot;
            password=&quot;petstorePassword&quot;
            beahome=&quot;${bea.home}&quot;/&gt;
 </pre>
 
 <hr>
 <h2><a name="wlstop">wlstop</a></h2>
 <h3><b>Description:</b></h3>
 
 <p>The <code>wlstop</code> task is used to stop a weblogic instance which is
 currently running. To shut down an instance you must supply both a username and
 a password. These will be stored in the clear in the build script used to stop
 the instance. For security reasons, this task is therefore only appropriate in a
 development environment. </p>
 
 <p>This task works for most version of Weblogic, including 6.0. You need to
 specify the BEA Home to have this task work correctly under 6.0</p>
 
 <h3>Parameters:</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">BEAHome</td>
     <td valign="top">This attribute selects Weblogic 6.0 shutdown.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to be used with the Java Virtual Machine that runs the Weblogic
                      Shutdown command.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">user</td>
     <td valign="top">The username of the account which will be used to shutdown the server</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">password</td>
     <td valign="top">The password for the account specified in the user parameter.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">url</td>
     <td valign="top">The URL which describes the port to which the server is listening for T3 connections.
                      For example, t3://localhost:7001</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">delay</td>
     <td valign="top">The delay in seconds after which the server will stop. This defaults to an
                      immediate shutdown.</td>
     <td valign="top" align="center">No</td>
   </tr>
 </table>
 
 <h3>Nested Element</h3>
 
 <p>The classpath of the wlstop task can be set by a &lt;classpath&gt; nested element.</p>
 
 <h3>Examples</h3>
 
 <p>This example show the shutdown for a Weblogic 6.0 server</p>
 
 <pre>
     &lt;wlstop classpath=&quot;${weblogic.classes}&quot;
             user=&quot;system&quot;
             url=&quot;t3://localhost:7001&quot;
             password=&quot;foobar&quot;
             beahome=&quot;${bea.home}&quot;/&gt;
 </pre>
 
 <hr>
 
 <h2><a name="ejbjar">ejbjar</a></h2>
 <h3><b>Description:</b></h3>
 
 <p>This task is designed to support building of EJB jar files (EJB 1.1 &amp; 2.0). 
 Support is currently provided for 'vanilla' EJB jar files - i.e. those containing only
 the user generated class files and the standard deployment descriptor. Nested
 elements provide support for vendor specific deployment tools. These currently
 include: </p>
 <ul>
   <li>Borland Application Server 4.5</li>
   <li>iPlanet Application Server 6.0</li>
   <li>JBoss 2.1 and above</li>
   <li>Weblogic 5.1/6.0 session/entity beans using the weblogic.ejbc tool</li>
   <li>IBM WebSphere 4.0</li>
   <li>TOPLink for WebLogic 2.5.1-enabled entity beans</li>
   <li><a href="http://www.objectweb.org/jonas/">JOnAS</a> 2.4.x and 2.5 Open Source EJB server</li>
 </ul>
 
 
 <p>The task works as a directory scanning task, and performs an action for each
 deployment descriptor found. As such the includes and excludes should be set
 to ensure that all desired EJB descriptors are found, but no application
 server descriptors are found. For each descriptor found, ejbjar will parse the
 deployment descriptor to determine the necessary class files which implement the
 bean. These files are assembled along with the deployment descriptors into a
 well formed EJB jar file. Any support files which need to be included in the
 generated jar can be added with the &lt;support&gt; nested element. For each
 class included in the jar, ejbjar will scan for any super classes or super
 interfaces. These will be added to the generated jar.</p>
 
 <p>If no nested vendor-specific deployment elements are present, the task will
 simply generate a generic EJB jar. Such jars are typically used as the input to
 vendor-specific deployment tools. For each nested deployment element, a vendor
 specific deployment tool is run to generate a jar file ready for deployment in
 that vendor's EJB container. </p>
 
 <p>The jar files are only built if they are out of date.  Each deployment tool
 element will examine its target jar file and determine if it is out of date with
 respect to the class files and deployment descriptors that make up the bean. If
 any of these files are newer than the jar file the jar will be rebuilt otherwise
 a message is logged that the jar file is up to date.</p>
 
 <p>The task uses the
 <a href="http://jakarta.apache.org/bcel"> jakarta-BCEL </a> framework
 to extract all dependent classes. This
 means that, in addition to the classes that are mentioned in the
 deployment descriptor, any classes that these depend on are also
 automatically included in the jar file.</p>
 
 
 <h3>Naming Convention</h3>
 
 Ejbjar handles the processing of multiple beans, and it uses a set of naming
 conventions to determine the name of the generated EJB jars. The naming convention
 that is used is controlled by the &quot;naming&quot; attribute. It supports the
 following values
 <ul>
 
 <li>descriptor</li>
 <p>This is the default naming scheme. The name of the generated bean is derived from the
 name of the deployment descriptor.  For an Account bean, for example, the deployment
 descriptor would be named <code>Account-ejb-jar.xml</code>. Vendor specific descriptors are
 located using the same naming convention. The weblogic bean, for example, would be named
 <code>Account-weblogic-ejb-jar.xml</code>. Under this arrangement, the deployment descriptors
 can be separated from the code implementing the beans, which can be useful when the same bean code
 is deployed in separate beans.
 </p>
 
 <p>This scheme is useful when you are using one bean per EJB jar and where you may be
 deploying the same bean classes in different beans, with different deployment characteristics.
 
 <li>ejb-name</li>
 <p> This naming scheme uses the &lt;ejb-name&gt; element from the deployment descriptor to
 determine the bean name. In this situation, the descriptors normally use the generic
 descriptor names, such as <code>ejb-jar.xml</code> along with any associated vendor specific descriptor
 names. For example, If the value of the &lt;ejb-name&gt; were to be given in the deployment descriptor
 as follows:
 <pre>
 &lt;ejb-jar&gt;
     &lt;enterprise-beans&gt;
         &lt;entity&gt;
             &lt;ejb-name&gt;Sample&lt;/ejb-name&gt;
             &lt;home&gt;org.apache.ant.ejbsample.SampleHome&lt;/home&gt;
 </pre>
 
 then the name of the generated bean would be <code>Sample.jar</code>
 </p>
 <p> This scheme is useful where you want to use the standard deployment descriptor names, which may be more
 compatible with other EJB tools. This scheme must have one bean per jar.
 </p>
 <li>directory</li>
 <p>
 In this mode, the name of the generated bean jar is derived from the directory
 containing the deployment descriptors. Again the deployment descriptors typically use
 the standard filenames. For example, if the path to the deployment descriptor is
 <code>/home/user/dev/appserver/dd/sample</code>, then the generated
 bean will be named <code>sample.jar</code>
 </p>
 <p>
 This scheme is also useful when you want to use standard style descriptor names. It is often
 most useful when the  descriptors are located in the same directory as the bean source code,
 although that is not mandatory. This scheme can handle multiple beans per jar.
 </p>
 
 <li>basejarname</li>
 <p>
 The final scheme supported by the &lt;ejbjar&gt; task is used when you want to specify the generated
 bean jar name directly. In this case the name of the generated jar is specified by the
 &quot;basejarname&quot; attribute. Since all generated beans will have the same name, this task should
 be only used when each descriptor is in its own directory.
 </p>
 
 <p>
 This scheme is most appropriate when you are using multiple beans per jar and only process a single
 deployment descriptor. You typically want to specify the name of the jar and not derive it from the
 beans in the jar.
 </p>
 
 </ul>
 
 <a name="ejbjar_deps"><h3>Dependencies</h3></a>
 <p>In addition to the bean classes, ejbjar is able to ad additional classes to the generated
 ejbjar. These classes are typically the support classes which are used by the bean's classes or as
 parameters to the bean's methods.</p>
 
 <p>In versions of Ant prior to 1.5, ejbjar used reflection and attempted to add the super
 classes and super interfaces of the bean classes. For this technique to work the bean
 classes had to be loaded into Ant's JVM. This was not always possible due to class dependencies.
 </p>
 
 <p>The ejbjar task in Ant releases 1.5 and later uses the
 <a href="http://jakarta.apache.org/bcel"> jakarta-BCEL </a> library
 to analyze the bean's class
 files directly, rather than loading them into the JVM. This also allows ejbjar to add all
 of the required support classes for a bean and not just super classes.
 </p>
 
 <p>In Ant 1.5, a new attribute, <code>dependency</code> has been introduced to allow the
 buildfile to control what additional classes are added to the generated jar. It takes three
 possible values</p>
 <ul>
 <li><code>none</code> - only the bean classes and interfaces described in the bean's
 descriptor are added to the jar.</li>
 <li><code>super</code> - this is the default value and replicates the original ejbjar
 behaviour where super classes and super interfaces are added to the jar</li>
 <li><code>full</code> - In this mode all classes used by the bean's classes and interfaces
 are added to the jar</li>
 </ul>
 <p>The <code>super</code> and <code>full</code> values require the
 <a href="http://jakarta.apache.org/bcel"> jakarta-BCEL </a> library
 to be available. If it is not, ejbjar will drop back to the behaviour corresponding to
 the value <code>none</code>.</p>
 
 <h3>Parameters:</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">descriptordir</td>
     <td valign="top">The base directory under which to scan for EJB
                      deployment descriptors. If this attribute is not
                      specified, then the deployment descriptors must be
                      located in the directory specified by the 'srcdir'
                      attribute.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">srcdir</td>
     <td valign="top">The base directory containing the .class files that
                      make up the bean. Included are the home- remote- pk-
                      and implementation- classes and all classes, that these
                      depend on. Note that this can be the same as the
                      descriptordir if all files are in the same directory
                      tree.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">The base directory into which generated jar files are
                      deposited. Jar files are deposited in directories
                      corresponding to their location within the descriptordir
                      namespace. Note that this attribute is only used if the
                      task is generating generic jars (i.e. no vendor-specific
                      deployment elements have been specified).</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
+    <td valign="top">cmpversion</td>
+    <td valign="top">Either <code>1.0</code> or <code>2.0</code>.<br/>
+    Default is <code>1.0</code>.<br/>
+    A CMP 2.0 implementation exists currently only for JBoss.</td>
+    <td valign="top" align="center">No</td>
+  </tr>
+  <tr>
     <td valign="top">naming</td>
     <td valign="top">Controls the naming convention used to name generated
                      EJB jars. Please refer to the description above.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">basejarname</td>
     <td valign="top">The base name that is used for the generated jar files.
                      If this attribute is specified, the generic jar file name
                      will use this value as the prefix (followed by the value
                      specified in the 'genericjarsuffix' attribute) and the
                      resultant ejb jar file (followed by any suffix specified
                      in the nested element).</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">basenameterminator</td>
     <td valign="top">String value used to substring out a string from the name
                      of each deployment descriptor found, which is then used to
                      locate related deployment descriptors (e.g. the WebLogic
                      descriptors). For example, a basename of '.' and a
                      deployment descriptor called 'FooBean.ejb-jar.xml' would
                      result in a basename of 'FooBean' which would then be used
                      to find FooBean.weblogic-ejb-jar.xml and
                      FooBean.weblogic-cmp-rdbms-jar.xml, as well as to create
                      the filenames of the jar files as FooBean-generic.jar and
                      FooBean-wl.jar. This attribute is not used if the
                      'basejarname' attribute is specified.</td>
     <td valign="top" align="center">No, defaults to '-'.</td>
   </tr>
   <tr>
     <td valign="top">genericjarsuffix</td>
     <td valign="top">String value appended to the basename of the deployment
                      descriptor to create the filename of the generic EJB jar
                      file.</td>
     <td valign="top" align="center">No, defaults to '-generic.jar'.</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">This classpath is used when resolving classes which
                      are to be added to the jar. Typically nested deployment
                      tool elements will also support a classpath which
                      will be combined with this classpath when resolving
                      classes</td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">flatdestdir</td>
     <td valign="top">Set this attribute to true if you want all generated jars
                      to be placed in the root of the destdir, rather than
                      according to the location of the deployment descriptor
                      within the descriptor dir hierarchy.</td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">dependency</td>
     <td valign="top">This attribute controls which additional classes and interfaces
                      are added to the jar. Please refer to the description
                      <a href="#ejbjar_deps">above</a></td>
     <td valign="top" align="center">No.</td>
   </tr>
 </table>
 
 <h3>Nested Elements</h3>
 
 <p>In addition to the vendor specific nested elements, the ejbjar task provides
 three nested elements. </p>
 
 <h4>Classpath</h4>
 
 <p>The &lt;classpath&gt; nested element allows the classpath
 to be set. It is useful when setting the classpath from a reference path. In all
 other respects the behaviour is the same as the classpath attribute.</p>
 
 <a name="ejbjar-dtd"><h4>dtd</h4></a>
 
 <p>The &lt;dtd&gt; element is used to specify the local location of DTDs to be
 used when parsing the EJB deployment descriptor. Using a local DTD is much
 faster than loading the DTD across the net. If you are running ejbjar behind a
 firewall you may not even be able to access the remote DTD. The supported
 vendor-specific nested elements know the location of the required DTDs within
 the vendor class hierarchy and, in general, this means &lt;dtd&gt; elements are
 not required. It does mean, however, that the vendor's class hierarchy must be
 available in the classpath when Ant is started. If your want to run Ant without
 requiring the vendor classes in the classpath, you would need to use a
 &lt;dtd&gt; element.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">publicId</td>
     <td valign="top">The public Id of the DTD for which the location is being provided</td>
     <td align="center" valign="top">Yes</td>
   </tr>
   <tr>
     <td valign="top">location</td>
     <td valign="top">The location of the local copy of the DTD. This can either be a
                      file or a resource loadable from the classpath.</td>
     <td align="center" valign="top">Yes</td>
   </tr>
 </table>
 
 <h4>support</h4>
 
 <p>The &lt;support&gt; nested element is used to supply additional classes
 (files) to be included in the generated jars. The &lt;support&gt; element is a
 <a href="../CoreTypes/fileset.html">FileSet</a>, so it can either reference a fileset declared elsewhere or it can be
 defined in-place with the appropriate &lt;include&gt; and &lt;exclude&gt; nested
 elements. The files in the support fileset are added into the generated EJB jar
 in the same relative location as their location within the support fileset. Note
 that when ejbjar generates more than one jar file, the support files are added
 to each one.</p>
 
 <h3>Vendor-specific deployment elements</h3>
 
 Each vendor-specific nested element controls the generation of a deployable jar
 specific to that vendor's EJB container. The parameters for each supported
 deployment element are detailed here.
 
 
 <h3><a name="ejbjar_jboss">Jboss element</a></h3>
 
 <p>The jboss element searches for the JBoss specific deployment descriptors and adds them
-to the final ejb jar file. JBoss has two deployment descriptors jboss.xml and jaws.xml
-(for container manager persistence only). The JBoss server uses hot deployment and does
+to the final ejb jar file. JBoss has two deployment descriptors:
+<ul><li>jboss.xml</li>
+<li>for container manager persistence:<br/>
+<table border="1">
+<tr><td><b>CMP version</b></td><td><b>File name</b></td></tr>
+<tr><td>CMP 1.0</td><td>jaws.xml</td></tr>
+<tr><td>CMP 2.0</td><td>jbosscmp-jdbc.xml</td></tr>
+</table>
+</li>
+</ul>
+<br/>
+. The JBoss server uses hot deployment and does
 not require compilation of additional stubs and skeletons.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">The base directory into which the generated weblogic ready
                      jar files are deposited. Jar files are deposited in
                      directories corresponding to their location within the
                      descriptordir namespace. </td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">genericjarsuffix</td>
     <td valign="top">A generic jar is generated as an intermediate step in
                      build the weblogic deployment jar. The suffix used to
                      generate the generic jar file is not particularly
                      important unless it is desired to keep the generic
                      jar file. It should not, however, be the same
                      as the suffix setting.</td>
     <td valign="top" align="center">No, defaults to '-generic.jar'.</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top">String value appended to the basename of the deployment
                      descriptor to create the filename of the JBoss EJB
                      jar file.</td>
     <td valign="top" align="center">No, defaults to '.jar'.</td>
   </tr>
   <tr>
     <td valign="top">keepgeneric</td>
     <td valign="top">This controls whether the generic file used as input to
                      ejbc is retained.</td>
     <td valign="top" align="center">No, defaults to false</td>
   </tr>
 </table>
 
 
 <h3><a name="ejbjar_weblogic">Weblogic element</a></h3>
 
 <p>The weblogic element is used to control the weblogic.ejbc compiler for
 generating weblogic EJB jars. Prior to Ant 1.3, the method of locating CMP
 descriptors was to use the ejbjar naming convention. So if your ejb-jar was
 called, Customer-ejb-jar.xml, your weblogic descriptor was called Customer-
 weblogic-ejb-jar.xml and your CMP descriptor had to be Customer-weblogic-cmp-
 rdbms-jar.xml. In addition, the &lt;type-storage&gt; element in the weblogic
 descriptor had to be set to the standard name META-INF/weblogic-cmp-rdbms-
 jar.xml, as that is where the CMP descriptor was mapped to in the generated
 jar.</p>
 
 <p>There are a few problems with this scheme. It does not allow for more than
 one CMP descriptor to be defined in a jar and it is not compatible with the
 deployment descriptors generated by some tools.</p>
 
 <p>In Ant 1.3, ejbjar parses the weblogic deployment descriptor to discover the
 CMP descriptors, which are then included automatically. This behaviour is
 controlled by the newCMP attribute. Note that if you move to the new method of
 determining CMP descriptors, you will need to update your weblogic deployment
 descriptor's &lt;type-storage&gt; element. In the above example, you would
 define this as META-INF/Customer-weblogic-cmp-rdbms-jar.xml.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">The base directory into which the generated weblogic ready
                      jar files are deposited. Jar files are deposited in
                      directories corresponding to their location within the
                      descriptordir namespace. </td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">genericjarsuffix</td>
     <td valign="top">A generic jar is generated as an intermediate step in
                      build the weblogic deployment jar. The suffix used to
                      generate the generic jar file is not particularly
                      important unless it is desired to keep the generic
                      jar file. It should not, however, be the same
                      as the suffix setting.</td>
     <td valign="top" align="center">No, defaults to '-generic.jar'.</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top">String value appended to the basename of the deployment
                      descriptor to create the filename of the WebLogic EJB
                      jar file.</td>
     <td valign="top" align="center">No, defaults to '.jar'.</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to be used when running the weblogic ejbc
                      tool. Note that this tool typically requires the classes
                      that make up the bean to be available on the classpath.
                      Currently, however, this will cause the ejbc tool to be
                      run in a separate VM</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">wlclasspath</td>
     <td valign="top">Weblogic 6.0 will give a warning if the home and remote interfaces
                      of a bean are on the system classpath used to run weblogic.ejbc.
                      In that case, the standard weblogic classes should be set with
                      this attribute (or equivalent nested element) and the
                      home and remote interfaces located with the standard classpath
                      attribute</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">keepgeneric</td>
     <td valign="top">This controls whether the generic file used as input to
                      ejbc is retained.</td>
     <td valign="top" align="center">No, defaults to false</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td valign="top">This allows for the selection of a different compiler
                      to be used for the compilation of the generated Java
                      files. This could be set, for example, to Jikes to
                      compile with the Jikes compiler. If this is not set
                      and the <code>build.compiler</code> property is set
                      to jikes, the Jikes compiler will be used. If this
                      is not desired, the value &quot;<code>default</code>&quot;
                      may be given to use the default compiler</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">rebuild</td>
     <td valign="top">This flag controls whether weblogic.ejbc is always
                      invoked to build the jar file. In certain circumstances,
                      such as when only a bean class has been changed, the jar
                      can be generated by merely replacing the changed classes
                      and not rerunning ejbc. Setting this to false will reduce
                      the time to run ejbjar.
                      </td>
     <td valign="top" align="center">No, defaults to true.</td>
   </tr>
   <tr>
     <td valign="top">keepgenerated</td>
     <td valign="top">Controls whether weblogic will keep the generated Java
                      files used to build the class files added to the
                      jar. This can be useful when debugging
                      </td>
     <td valign="top" align="center">No, defaults to false.</td>
   </tr>
   <tr>
     <td valign="top">args</td>
     <td valign="top">Any additional arguments to be passed to the weblogic.ejbc
                      tool.
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">weblogicdtd</td>
     <td valign="top"><b>Deprecated</b>. Defines the location of the ejb-jar DTD in
                      the weblogic class hierarchy. This should not be necessary if you
                      have weblogic in your classpath. If you do not, you should use a
                      nested &lt;dtd&gt; element, described above. If you do choose
                      to use an attribute, you should use a
                      nested &lt;dtd&gt; element.
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">wldtd</td>
     <td valign="top"><b>Deprecated</b>. Defines the location of the weblogic-ejb-jar
                      DTD which covers the Weblogic specific deployment descriptors.
                      This should not be necessary if you have weblogic in your
                      classpath. If you do not, you should use a nested &lt;dtd&gt;
                      element, described above.
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">ejbdtd</td>
     <td valign="top"><b>Deprecated</b>. Defines the location of the ejb-jar DTD in
                      the weblogic class hierarchy. This should not be necessary if you
                      have weblogic in your classpath. If you do not, you should use a
                      nested &lt;dtd&gt; element, described above.
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">newCMP</td>
     <td valign="top">If this is set to true, the new method for locating
                      CMP descriptors will be used.</td>
     <td valign="top" align="center">No. Defaults to false</td>
   </tr>
   <tr>
     <td valign="top">oldCMP</td>
     <td valign="top"><b>Deprecated</b> This is an antonym for newCMP which should be used instead.</td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">noEJBC</td>
     <td valign="top">If this attribute is set to true, Weblogic's ejbc will not be run on the EJB jar.
                      Use this if you prefer to run ejbc at deployment time.</td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">ejbcclass</td>
     <td valign="top">Specifies the classname of the ejbc compiler. Normally ejbjar determines
                      the appropriate class based on the DTD used for the EJB. The EJB 2.0 compiler
                      featured in weblogic 6 has, however, been deprecated in version 7. When
                      using with version 7 this attribute should be set to
                      &quot;weblogic.ejbc&quot; to avoid the deprecation warning.</td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">jvmargs</td>
     <td valign="top">Any additional arguments to be passed to the Virtual Machine
                      running weblogic.ejbc tool. For example to set the memory size,
                      this could be jvmargs=&quot;-Xmx128m&quot;
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">jvmdebuglevel</td>
     <td valign="top">Sets the weblogic.StdoutSeverityLevel to use when running
                      the Virtual Machine that executes ejbc. Set to 16 to avoid
                      the warnings about EJB Home and Remotes being in the classpath
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
   <tr>
     <td valign="top">outputdir</td>
     <td valign="top">If set ejbc will be given this directory as the output
                      destination rather than a jar file. This allows for the
                      generation of &quot;exploded&quot; jars.
                      </td>
     <td valign="top" align="center">No.</td>
   </tr>
 </table>
 
 <p>The weblogic nested element supports three nested elements. The
 first two, &lt;classpath&gt; and &lt;wlclasspath&gt;, are used to set the
 respective classpaths. These nested elements are useful when setting up
 class paths using reference Ids. The last, &lt;sysproperty&gt;, allows
 Java system properties to be set during the compiler run. This turns out
 to be necessary for supporting CMP EJB compilation in all environments.
 </p>
 
 <h3>TOPLink for Weblogic element</h3>
 
 <p><b><i>Deprecated</i></b></p>
 
 <p>The toplink element is no longer required. Toplink beans can now be built with the standard
 weblogic element, as long as the newCMP attribute is set to &quot;true&quot;
 </p>
 
 <p>The TopLink element is used to handle beans which use Toplink for the CMP operations. It
 is derived from the standard weblogic element so it supports the same set of attributes plus these
 additional attributes</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">toplinkdescriptor</td>
     <td valign="top">This specifies the name of the TOPLink deployment descriptor file contained in the
                      'descriptordir' directory.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">toplinkdtd</td>
     <td valign="top">This specifies the location of the TOPLink DTD file. This can be a file path or
                      a file URL. This attribute is not required, but using a local DTD is recommended.</td>
     <td valign="top" align="center">No, defaults to dtd file at www.objectpeople.com.</td>
   </tr>
 </table>
 
 
 <h3>Examples</h3>
 
 <p>This example shows ejbjar being used to generate deployment jars using a
 Weblogic EJB container. This example requires the naming standard to be used for
 the deployment descriptors. Using this format will create a ejb jar file for
 each variation of '*-ejb-jar.xml' that is found in the deployment descriptor
 directory.</p>
 
 <pre>
     &lt;ejbjar srcdir=&quot;${build.classes}&quot;
             descriptordir=&quot;${descriptor.dir}&quot;&gt;
       &lt;weblogic destdir=&quot;${deploymentjars.dir}&quot;
                 classpath=&quot;${descriptorbuild.classpath}&quot;/&gt;
       &lt;include name=&quot;**/*-ejb-jar.xml&quot;/&gt;
       &lt;exclude name=&quot;**/*weblogic*.xml&quot;/&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 <p>If weblogic is not in the Ant classpath, the following example
 shows how to specify the location of the weblogic DTDs. This
 example also show the use of a nested classpath element.</p>
 
 <pre>
     &lt;ejbjar descriptordir=&quot;${src.dir}&quot; srcdir=&quot;${build.classes}&quot;&gt;
        &lt;weblogic destdir=&quot;${deployment.webshop.dir}&quot;
                  keepgeneric=&quot;true&quot;
                  args=&quot;-g -keepgenerated ${ejbc.compiler}&quot;
                  suffix=&quot;.jar&quot;
                  oldCMP=&quot;false&quot;&gt;
          &lt;classpath&gt;
            &lt;pathelement path=&quot;${descriptorbuild.classpath}&quot;/&gt;
          &lt;/classpath&gt;
        &lt;/weblogic&gt;
        &lt;include name=&quot;**/*-ejb-jar.xml&quot;/&gt;
        &lt;exclude name=&quot;**/*-weblogic-ejb-jar.xml&quot;/&gt;
        &lt;dtd publicId=&quot;-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN&quot;
             location=&quot;${weblogic.home}/classes/weblogic/ejb/deployment/xml/ejb-jar.dtd&quot;/&gt;
        &lt;dtd publicId=&quot;-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN&quot;
             location=&quot;${weblogic.home}/classes/weblogic/ejb/deployment/xml/weblogic-ejb-jar.dtd&quot;/&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 
 <p>This example shows ejbjar being used to generate a single deployment jar
 using a Weblogic EJB container. This example does not require the deployment
 descriptors to use the naming standard. This will create only one ejb jar file -
 'TheEJBJar.jar'.</p>
 
 
 <pre>
     &lt;ejbjar srcdir=&quot;${build.classes}&quot;
             descriptordir=&quot;${descriptor.dir}&quot;
             basejarname=&quot;TheEJBJar&quot;&gt;
       &lt;weblogic destdir=&quot;${deploymentjars.dir}&quot;
                 classpath=&quot;${descriptorbuild.classpath}&quot;/&gt;
       &lt;include name=&quot;**/ejb-jar.xml&quot;/&gt;
       &lt;exclude name=&quot;**/weblogic*.xml&quot;/&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 <p>This example shows ejbjar being used to generate deployment jars for a TOPLink-enabled entity bean using a
 Weblogic EJB container. This example does not require the deployment descriptors to use the naming standard.
 This will create only one TOPLink-enabled ejb jar file - 'Address.jar'.</p>
 
 <pre>
     &lt;ejbjar srcdir=&quot;${build.dir}&quot;
             destdir=&quot;${solant.ejb.dir}&quot;
             descriptordir=&quot;${descriptor.dir}&quot;
             basejarname=&quot;Address&quot;&gt;
             &lt;weblogictoplink destdir=&quot;${solant.ejb.dir}&quot;
                     classpath=&quot;${java.class.path}&quot;
                     keepgeneric=&quot;false&quot;
                     toplinkdescriptor=&quot;Address.xml&quot;
                     toplinkdtd=&quot;file:///dtdfiles/toplink-cmp_2_5_1.dtd&quot;
                     suffix=&quot;.jar&quot;/&gt;
             &lt;include name=&quot;**/ejb-jar.xml&quot;/&gt;
             &lt;exclude name=&quot;**/weblogic-ejb-jar.xml&quot;/&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 <p>This final example shows how you would set-up ejbjar under Weblogic 6.0. It also shows the use of the
 &lt;support&gt; element to add support files</p>
 
 <pre>
     &lt;ejbjar descriptordir=&quot;${dd.dir}&quot; srcdir=&quot;${build.classes.server}&quot;&gt;
        &lt;include name=&quot;**/*-ejb-jar.xml&quot;/&gt;
        &lt;exclude name=&quot;**/*-weblogic-ejb-jar.xml&quot;/&gt;
        &lt;support dir=&quot;${build.classes.server}&quot;&gt;
             &lt;include name=&quot;**/*.class&quot;/&gt;
        &lt;/support&gt;
        &lt;weblogic destdir=&quot;${deployment.dir}&quot;
                  keepgeneric=&quot;true&quot;
                  suffix=&quot;.jar&quot;
                  rebuild=&quot;false&quot;&gt;
          &lt;classpath&gt;
             &lt;pathelement path=&quot;${build.classes.server}&quot;/&gt;
          &lt;/classpath&gt;
          &lt;wlclasspath&gt;
             &lt;pathelement path=&quot;${weblogic.classes}&quot;/&gt;
          &lt;/wlclasspath&gt;
        &lt;/weblogic&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 
 <h3><a name="ejbjar_websphere">WebSphere element</a></h3>
 
 <p>The websphere element searches for the websphere specific deployment descriptors and
 adds them to the final ejb jar file. Websphere has two specific descriptors for session
 beans:
 <ul>
    <li>ibm-ejb-jar-bnd.xmi</li>
    <li>ibm-ejb-jar-ext.xmi</li>
 </ul>
 and another two for container managed entity beans:
 <ul>
    <li>Map.mapxmi</li>
    <li>Schema.dbxmi</li>
 </ul>
 In terms of WebSphere, the generation of container code and stubs is called <code>deployment</code>.
 This step can be performed by the websphere element as part of the jar generation process. If the
 switch <code>ejbdeploy</code> is on, the ejbdeploy tool from the websphere toolset is called for
 every ejb-jar. Unfortunately, this step only works, if you use the ibm jdk. Otherwise, the rmic
 (called by ejbdeploy) throws a ClassFormatError. Be sure to switch ejbdeploy off, if run ant with
 sun jdk.
 </p>
 
 <p>
 For the websphere element to work, you have to provide a complete classpath, that contains all
 classes, that are required to reflect the bean classes. For ejbdeploy to work, you must also provide
 the classpath of the ejbdeploy tool and set the <i>websphere.home</i> property (look at the examples below).
 </p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">The base directory into which the generated weblogic ready
                      jar files are deposited. Jar files are deposited in
                      directories corresponding to their location within the
                      descriptordir namespace. </td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">ejbdeploy</td>
     <td valign="top">Decides wether ejbdeploy is called. When you set this to true,
                      be sure, to run ant with the ibm jdk.</td>
     <td valign="top" align="center">No, defaults to true</td>
   </tr>
   <tr>
     <td valign="top">suffix</td>
     <td valign="top">String value appended to the basename of the deployment
                      descriptor to create the filename of the WebLogic EJB
                      jar file.</td>
     <td valign="top" align="center">No, defaults to '.jar'.</td>
   </tr>
   <tr>
     <td valign="top">keepgeneric</td>
     <td valign="top">This controls whether the generic file used as input to
                      ejbdeploy is retained.</td>
     <td valign="top" align="center">No, defaults to false</td>
   </tr>
   <tr>
     <td valign="top">rebuild</td>
     <td valign="top">This controls whether ejbdeploy is called although no changes
                      have occurred.</td>
     <td valign="top" align="center">No, defaults to false</td>
   </tr>
   <tr>
     <td valign="top">tempdir</td>
     <td valign="top">A directory, where ejbdeploy will write temporary files</td>
     <td valign="top" align="center">No, defaults to '_ejbdeploy_temp'.</td>
   </tr>
   <tr>
     <td valign="top">dbName<br>dbSchema</td>
     <td valign="top">These options are passed to ejbdeploy.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">dbVendor</td>
     <td valign="top">This option is passed to ejbdeploy. Valid options are for example:
                      <ul>
                      <li>SQL92</li> <li>SQL99</li> <li>DB2UDBWIN_V71</li>
                      <li>DB2UDBOS390_V6</li> <li>DB2UDBAS400_V4R5</li> <li>ORACLE_V8</li>
                      <li>INFORMIX_V92</li> <li>SYBASE_V1192</li> <li>MYSQL_V323</li>
                      <li>MSSQLSERVER_V7</li>
                      </ul>
                      This is also used to determine the name of the Map.mapxmi and
                      Schema.dbxmi files, for example Account-DB2UDBWIN_V71-Map.mapxmi
                      and Account-DB2UDBWIN_V71-Schema.dbxmi.
                      </td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">codegen<br>quiet<br>novalidate<br>noinform<br>trace<br>
                      use35MappingRules</td>
     <td valign="top">These options are all passed to ejbdeploy. All options
                      except 'quiet' default to false.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">rmicOptions</td>
     <td valign="top">This option is passed to ejbdeploy and will be passed
                      on to rmic.</td>
     <td valign="top" align="center">No</td>
   </tr>
 </table>
 
 <p>This example shows ejbjar being used to generate deployment jars for all deployment descriptors
 in the descriptor dir:</p>
 
 <pre>
      &lt;property name=&quot;webpshere.home&quot; value=&quot;${was4.home}&quot;/&gt;
      &lt;ejbjar srcdir="${build.class}" descriptordir="etc/ejb"&gt;
       &lt;include name="*-ejb-jar.xml"/&gt;
       &lt;websphere dbvendor="DB2UDBOS390_V6"
                  ejbdeploy="true"
                  oldCMP="false"
                  tempdir="/tmp"
                  destdir="${dist.server}"&gt;
         &lt;wasclasspath&gt;
           &lt;pathelement location="${was4.home}/deploytool/itp/plugins/org.eclipse.core.boot/boot.jar"/&gt;
           &lt;pathelement location="${was4.home}/deploytool/itp/plugins/com.ibm.etools.ejbdeploy/runtime/batch.jar"/&gt;
           &lt;pathelement location="${was4.home}/lib/xerces.jar"/&gt;
           &lt;pathelement location="${was4.home}/lib/ivjejb35.jar"/&gt;
           &lt;pathelement location="${was4.home}/lib/j2ee.jar"/&gt;
           &lt;pathelement location="${was4.home}/lib/vaprt.jar"/&gt;
         &lt;/wasclasspath&gt;
       &lt;classpath&gt;
         &lt;path refid="build.classpath"/&gt;
       &lt;/classpath&gt;
       &lt;/websphere&gt;
       &lt;dtd publicId="-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN"
            location="${lib}/dtd/ejb-jar_1_1.dtd"/&gt;
     &lt;/ejbjar&gt;
 </pre>
 
 <h3><a name="ejbjar_iplanet">iPlanet Application Server (iAS) element</a></h3>
 
 The &lt;iplanet&lt; nested element is used to build iAS-specific stubs and
 
 skeletons and construct a JAR file which may be deployed to the iPlanet
 Application Server 6.0.  The build process will always determine if
 the EJB stubs/skeletons and the EJB-JAR file are up to date, and it will
 do the minimum amount of work required.
 <p>Like the WebLogic element, a naming convention for the EJB descriptors
 is most commonly used to specify the name for the completed JAR file.
 For example, if the EJB descriptor ejb/Account-ejb-jar.xml is found in
 the descriptor directory, the iplanet element will search for an iAS-specific
 EJB descriptor file named ejb/Account-ias-ejb-jar.xml (if it isn't found,
 the task will fail) and a JAR file named ejb/Account.jar will be written
 in the destination directory.  Note that when the EJB descriptors
 are added to the JAR file, they are automatically renamed META-INF/ejb-jar.xml
 and META-INF/ias-ejb-jar.xml.</p>
 <p>Of course, this naming behaviour can be modified by specifying attributes
 in the ejbjar task (for example, basejarname, basenameterminator, and flatdestdir)
 as well as the iplanet element (for example, suffix).  Refer to the
 appropriate documentation for more details.</p>
 <h3>
 Parameters:</h3>
 
 <table BORDER CELLSPACING=0 CELLPADDING=2 >
 <tr>
 <td VALIGN=TOP><b>Attribute</b></td>
 
 <td VALIGN=TOP><b>Description</b></td>
 
 <td ALIGN=CENTER VALIGN=TOP><b>Required</b></td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>destdir</td>
 
 <td VALIGN=TOP>The base directory into which the generated JAR files will
 be written. Each JAR file is written in directories which correspond to
 their location within the "descriptordir" namespace.</td>
 
 <td ALIGN=CENTER VALIGN=TOP>Yes</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>classpath</td>
 
 <td VALIGN=TOP>The classpath used when generating EJB stubs and skeletons.
 If omitted, the classpath specified in the "ejbjar" parent task will be
 used.  If specified, the classpath elements will be prepended to the
 classpath specified in the parent "ejbjar" task. Note that nested "classpath"
 elements may also be used.</td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>keepgenerated</td>
 
 <td VALIGN=TOP>Indicates whether or not the Java source files which are
 generated by ejbc will be saved or automatically deleted. If "yes", the
 source files will be retained.  If omitted, it defaults to "no". </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>debug</td>
 
 <td>Indicates whether or not the ejbc utility should log additional debugging
 statements to the standard output. If "yes", the additional debugging statements
 will be generated.  If omitted, it defaults to "no". </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>iashome</td>
 
 <td>May be used to specify the "home" directory for this iAS installation.
 This is used to find the ejbc utility if it isn't included in the user's
 system path.  If specified, it should refer to the [install-location]/iplanet/ias6/ias
 directory.  If omitted, the ejbc utility must be on the user's system
 path. </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 
 <tr>
 <td VALIGN=TOP>suffix</td>
 
 <td>String value appended to the JAR filename when creating each JAR.
 If omitted, it defaults to ".jar". </td>
 
 <td ALIGN=CENTER VALIGN=TOP>No</td>
 </tr>
 </table>
 
 <p>As noted above, the iplanet element supports additional &lt;classpath&gt;
 nested elements.</p>
 <h3>
 Examples</h3>
 This example demonstrates the typical use of the &lt;iplanet&gt; nested element.
 It will name each EJB-JAR using the "basename" prepended to each standard
 EJB descriptor.  For example, if the descriptor named "Account-ejb-jar.xml"
 is processed, the EJB-JAR will be named "Account.jar"
 <pre>    &lt;ejbjar srcdir="${build.classesdir}"
             descriptordir="${src}"&gt;
 
             &lt;iplanet destdir="${assemble.ejbjar}"
                      classpath="${ias.ejbc.cpath}"/&gt;
             &lt;include name="**/*-ejb-jar.xml"/&gt;
             &lt;exclude name="**/*ias-*.xml"/&gt;
     &lt;/ejbjar&gt;</pre>
 
 This example demonstrates the use of a nested classpath element as well
 as some of the other optional attributes.
 <pre>    &lt;ejbjar srcdir="${build.classesdir}"
             descriptordir="${src}"&gt;
 
             &lt;iplanet destdir="${assemble.ejbjar}"
                      iashome="${ias.home}"
                      debug="yes"
                      keepgenerated="yes"&gt;
                      &lt;classpath&gt;
                          &lt;pathelement path="."/&gt;
                          &lt;pathelement path="${build.classpath}"/&gt;
                      &lt;/classpath&gt;
             &lt;/iplanet&gt;
             &lt;include name="**/*-ejb-jar.xml"/&gt;
             &lt;exclude name="**/*ias-*.xml"/&gt;
     &lt;/ejbjar&gt;</pre>
 
 This example demonstrates the use of basejarname attribute.  In this
 case, the completed EJB-JAR will be named "HelloWorld.jar"  If multiple
 EJB descriptors might be found, care must be taken to ensure that the completed
 JAR files don't overwrite each other.
 <pre>    &lt;ejbjar srcdir="${build.classesdir}"
             descriptordir="${src}"
             basejarname="HelloWorld"&gt;
 
             &lt;iplanet destdir="${assemble.ejbjar}"
                      classpath="${ias.ejbc.cpath}"/&gt;
             &lt;include name="**/*-ejb-jar.xml"/&gt;
             &lt;exclude name="**/*ias-*.xml"/&gt;
     &lt;/ejbjar&gt;</pre>
 This example demonstrates the use of the dtd nested element. If the local
 copies of the DTDs are included in the classpath, they will be automatically
 referenced without the nested elements.  In iAS 6.0 SP2, these local DTDs are
 found in the [iAS-install-directory]/APPS directory.  In iAS 6.0 SP3, these
 local DTDs are found in the [iAS-install-directory]/dtd directory.
 <pre>    &lt;ejbjar srcdir="${build.classesdir}"
             descriptordir="${src}"&gt;
             &lt;iplanet destdir="${assemble.ejbjar}"&gt;
                      classpath="${ias.ejbc.cpath}"/&gt;
             &lt;include name="**/*-ejb-jar.xml"/&gt;
             &lt;exclude name="**/*ias-*.xml"/&gt;
 
             &lt;dtd publicId="-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN"
                  location="${ias.home}/APPS/ejb-jar_1_1.dtd"/&gt;
             &lt;dtd publicId="-//Sun Microsystems, Inc.//DTD iAS Enterprise JavaBeans 1.0//EN"
                  location="${ias.home}/APPS/IASEjb_jar_1_0.dtd"/&gt;
     &lt;/ejbjar&gt;</pre>
 
 <h3><a name="ejbjar_jonas">JOnAS (Java Open Application Server) element</a></h3>
 
 <p>The &lt;jonas&gt; nested element is used to build JOnAS-specific stubs and
 skeletons thanks to the <code>GenIC</code> specific tool, and construct a JAR
 file which may be deployed to the JOnAS Application Server. The build process
 will always determine if the EJB stubs/skeletons and the EJB-JAR file are up to
 date, and it will do the minimum amount of work required.</p>
 
 <p>Like the WebLogic element, a naming convention for the EJB descriptors is
 most commonly used to specify the name for the completed JAR file. For example,
 if the EJB descriptor <code>ejb/Account-ejb-jar.xml</code> is found in the
 descriptor directory, the &lt;jonas&gt; element will search for a JOnAS-specific
 EJB descriptor file named <code>ejb/Account-jonas-ejb-jar.xml</code> and a JAR
 file named <code>ejb/Account.jar</code> will be written in the destination
 directory. But the &lt;jonas&gt; element can also use the JOnAS naming
 convention. With the same example as below, the EJB descriptor can also be named
 <code>ejb/Account.xml</code> (no base name terminator here) in the descriptor
 directory. Then the &lt;jonas&gt; element will search for a JOnAS-specific EJB
 descriptor file called <code>ejb/jonas-Account.xml</code>. This convention do
 not follow strictly the ejb-jar naming convention recommendation but is
 supported for backward compatibility with previous version of JOnAS.</p>
 
 <p>Note that when the EJB descriptors are added to the JAR file, they are
 automatically renamed <code>META-INF/ejb-jar.xml</code> and
 <code>META-INF/jonas-ejb-jar.xml</code>.</p>
 
 <p>Of course, this naming behavior can be modified by specifying attributes in
 the ejbjar task (for example, basejarname, basenameterminator, and flatdestdir)
 as well as the iplanet element (for example, suffix). Refer to the appropriate
 documentation for more details.</p>
 
 <h3> Parameters:</h3>
 
 <table border="1" cellspacing="0" cellpadding="2">
   <tbody>
     <tr>
       <td valign="Top"><b>Attribute</b></td>
       <td valign="Top"><b>Description</b></td>
       <td align="Center" valign="Top"><b>Required</b></td>
     </tr>
     <tr>
       <td valign="Top">destdir</td>
       <td valign="Top">The base directory into which the generated JAR files
       will be written. Each JAR file is written in directories which correspond
       to their location within the "<code>descriptordir</code>" namespace.</td>
       <td align="Center" valign="Top">Yes</td>
     </tr>
     <tr>
       <td valign="Top">jonasroot</td>
       <td valign="Top">The root directory for JOnAS.</td>
       <td valign="Top" align="Center">Yes</td>
     </tr>
     <tr>
       <td valign="Top">classpath</td>
       <td valign="Top">The classpath used when generating EJB stubs and
       skeletons. If omitted, the classpath specified in the "ejbjar" parent
       task will be used. If specified, the classpath elements will be prepended
       to the classpath specified in the parent "ejbjar" task (see also the ORB
       attribute documentation below). Note that nested "classpath" elements may
       also be used.</td>
       <td valign="Top" align="Center">No</td>
     </tr>
     <tr>
       <td valign="Top">keepgenerated</td>
       <td valign="Top"><code>true</code> if the intermediate Java
       source files generated by GenIC must be deleted or not. If
       omitted, it defaults to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">nocompil</td>
       <td valign="Top"><code>true</code> if the generated source files
       must not be compiled via the java and rmi compilers. If omitted,
       it defaults to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">novalidation</td>
       <td valign="Top"><code>true</code> if the XML deployment descriptors must
       be parsed without validation. If omitted, it defaults to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">javac</td>
       <td valign="Top">Java compiler to use. If omitted, it defaults
       to the value of <code>build.compiler</code> property.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">javacopts</td>
       <td valign="Top">Options to pass to the java compiler.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">rmicopts</td>
       <td valign="Top">Options to pass to the rmi compiler.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="top">secpropag</td>
       <td valign="top"><code>true</code> if the RMI Skel. and
       Stub. must be modified to implement the implicit propagation of
       the security context (the transactional context is always
       provided). If omitted, it defaults to <code>false</code>.</td>
       <td valign="top" align="center">No</td>
     </tr>
     <tr>
       <td valign="Top">verbose</td>
       <td valign="Top">Indicates whether or not to use -verbose switch. If
       omitted, it defaults to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
       <td valign="Top">additionalargs</td>
       <td valign="Top">Add additional args to GenIC.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">keepgeneric</td>
       <td valign="Top"><code>true</code> if the generic JAR file used as input
       to GenIC must be retained. If omitted, it defaults to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">suffix</td>
       <td>String value appended to the JAR filename when creating each JAR.  If
       omitted, it defaults to ".jar". </td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">orb</td>
       <td>Choose your ORB : RMI, JEREMIE, DAVID. If omitted, it defaults to the
       one present in classpath. If specified, the corresponding JOnAS JAR is
       automatically added to the classpath.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
       <td valign="Top">nogenic</td>
       <td valign="Top">If this attribute is set to <code>true</code>,
       JOnAS's GenIC will not be run on the EJB JAR. Use this if you
       prefer to run GenIC at deployment time. If omitted, it defaults
       to <code>false</code>.</td>
       <td align="Center" valign="Top">No</td>
     </tr>
     <tr>
   </tbody>
 </table>
 
 <p>As noted above, the jonas element supports additional &lt;classpath&gt;
 nested elements.</p>
 
 <h3>Examples</h3>
 
 <p>This example shows ejbjar being used to generate deployment jars using a
 JOnAS EJB container. This example requires the naming standard to be used for
 the deployment descriptors. Using this format will create a EJB JAR file for
 each variation of &nbsp;'*-jar.xml' that is found in the deployment descriptor
 directory.&nbsp;</p>
 
 <pre>
       &lt;ejbjar srcdir="${build.classes}"
               descriptordir="${descriptor.dir}"&gt;
         &lt;jonas destdir="${deploymentjars.dir}"
              jonasroot="${jonas.root}"
              orb="RMI"/&gt;
         &lt;include name="**/*.xml"/&gt;
         &lt;exclude name="**/jonas-*.xml"/&gt;
         &lt;support dir="${build.classes}"&gt;
              &lt;include name="**/*.class"/&gt;
         &lt;/support&gt;
       &lt;/ejbjar&gt;
 </pre>
 
 <p>This example shows ejbjar being used to generate a single deployment jar
 using a JOnAS EJB container. This example does require the deployment
 descriptors to use the naming standard. This will create only one ejb jar file -
 'TheEJBJar.jar'.</p>
 
 <pre>
       &lt;ejbjar srcdir="${build.classes}"
               descriptordir="${descriptor.dir}"
               basejarname="TheEJBJar"&gt;
         &lt;jonas destdir="${deploymentjars.dir}"
                   jonasroot="${jonas.root}"
                   suffix=".jar"
                   classpath="${descriptorbuild.classpath}"/&gt;
         &lt;include name="**/ejb-jar.xml"/&gt;
         &lt;exclude name="**/jonas-ejb-jar.xml"/&gt;
       &lt;/ejbjar&gt;
 </pre>
 
 <hr>
 <p align="center">Copyright &copy; 2000-2003 Apache Software Foundation. All rights
 Reserved.</p>
 
 
 </body>
 
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/EjbJar.java b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/EjbJar.java
index a5a8a9948..4c8fcf95e 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/EjbJar.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/EjbJar.java
@@ -1,609 +1,660 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
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
 
 package org.apache.tools.ant.taskdefs.optional.ejb;
 
 // Standard java imports
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.parsers.SAXParser;
 import javax.xml.parsers.SAXParserFactory;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Path;
 import org.xml.sax.SAXException;
 
 /**
  * Provides automated EJB JAR file creation.
  * <p>
  * Extends the
  * MatchingTask class provided in the default ant distribution to provide a
  * directory scanning EJB jarfile generator.
  * </p>
  *
  * <p>
  * The task works by taking the deployment descriptors one at a time and
  * parsing them to locate the names of the classes which should be placed in
  * the jar. The classnames are translated to java.io.Files by replacing
  * periods with File.separatorChar and resolving the generated filename as a
  * relative path under the srcDir attribute. All necessary files are then
  * assembled into a jarfile. One jarfile is constructed for each deployment
  * descriptor found.
  * </p>
  *
  * @author <a href="mailto:tfennell@sapient.com">Tim Fennell</a>
  * @author Conor MacNeill
- */
+ * @author <a href="mailto:rvanoo@xs4all.nl">Rob van Oostrum</a>
+ * */
 public class EjbJar extends MatchingTask {
 
     /**
      * Inner class used to record information about the location of a local DTD
      */
     public static class DTDLocation
         extends org.apache.tools.ant.types.DTDLocation {
     }
 
     /**
      * A class which contains the configuration state of the ejbjar task.
      * This state is passed to the deployment tools for configuration
      */
     static class Config {
         /**
          * Stores a handle to the directory under which to search for class
          * files
          */
         public File srcDir;
 
         /**
          * Stores a handle to the directory under which to search for
          * deployment descriptors
          */
         public File descriptorDir;
 
         /** Instance variable that marks the end of the 'basename' */
         public String baseNameTerminator = "-";
 
         /** Stores a handle to the destination EJB Jar file */
         public String baseJarName;
 
         /**
          * Instance variable that determines whether to use a package structure
          * of a flat directory as the destination for the jar files.
          */
         public boolean flatDestDir = false;
 
         /**
          * The classpath to use when loading classes
          */
         public Path classpath;
 
         /**
          * A Fileset of support classes
          */
         public List supportFileSets = new ArrayList();
 
         /**
          * The list of configured DTD locations
          */
         public ArrayList dtdLocations = new ArrayList();
 
         /**
          * The naming scheme used to determine the generated jar name
          * from the descriptor information
          */
         public NamingScheme namingScheme;
 
         /**
          * The Manifest file
          */
         public File manifest;
 
         /**
          * The dependency analyzer to use to add additional classes to the jar
          */
         public String analyzer;
     }
 
     /**
      * An EnumeratedAttribute class for handling different EJB jar naming
      * schemes
      */
     public static class NamingScheme extends EnumeratedAttribute {
         /**
          * Naming scheme where generated jar is determined from the ejb-name in
          * the deployment descripor
          */
         public static final String EJB_NAME = "ejb-name";
 
         /**
          * Naming scheme where the generated jar name is based on the
          * name of the directory containing the deployment descriptor
          */
         public static final String DIRECTORY = "directory";
 
         /**
          * Naming scheme where the generated jar name is based on the name of
          * the deployment descriptor file
          */
         public static final String DESCRIPTOR = "descriptor";
 
         /**
          * Naming scheme where the generated jar is named by the basejarname
          * attribute
          */
         public static final String BASEJARNAME = "basejarname";
 
         /**
          * Gets the values of the NamingScheme
          *
          * @return an array of the values of this attribute class.
          */
         public String[] getValues() {
             return new String[] {EJB_NAME, DIRECTORY, DESCRIPTOR, BASEJARNAME};
         }
     }
 
     /**
+     * CMP versions supported
+     * valid CMP versions are 1.0 and 2.0
+     * @since ant 1.6
+     */
+    public static class CMPVersion extends EnumeratedAttribute {
+        public static final String CMP1_0 = "1.0";
+        public static final String CMP2_0 = "2.0";
+        public String[] getValues() {
+            return new String[]{
+                CMP1_0,
+                CMP2_0,
+            };
+        }
+    }
+    /**
      * The config which is built by this task and used by the various deployment
      * tools to access the configuration of the ejbjar task
      */
     private Config config = new Config();
 
 
     /**
      * Stores a handle to the directory to put the Jar files in. This is
      * only used by the generic deployment descriptor tool which is created
      * if no other deployment descriptor tools are provided. Normally each
      * deployment tool will specify the desitination dir itself.
      */
     private File destDir;
 
     /** Instance variable that stores the suffix for the generated jarfile. */
     private String genericJarSuffix = "-generic.jar";
 
+    /** Instance variable that stores the CMP version for the jboss jarfile. */
+    private String cmpVersion = CMPVersion.CMP1_0;
+
     /** The list of deployment tools we are going to run. */
     private ArrayList deploymentTools = new ArrayList();
 
-
     /**
      * Add a deployment tool to the list of deployment tools that will be
      * processed
      *
      * @param deploymentTool a deployment tool instance to which descriptors
      *        will be passed for processing.
      */
     protected void addDeploymentTool(EJBDeploymentTool deploymentTool) {
         deploymentTool.setTask(this);
         deploymentTools.add(deploymentTool);
     }
 
     /**
      * Adds a deployment tool for Weblogic server.
      *
      * @return the deployment tool instance to be configured.
      */
     public WeblogicDeploymentTool createWeblogic() {
         WeblogicDeploymentTool tool = new WeblogicDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for Websphere 4.0 server.
      *
      * @return the deployment tool instance to be configured.
      */
     public WebsphereDeploymentTool createWebsphere() {
         WebsphereDeploymentTool tool = new WebsphereDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for Borland server.
      *
      * @return the deployment tool instance to be configured.
      */
     public BorlandDeploymentTool createBorland() {
         log("Borland deployment tools",  Project.MSG_VERBOSE);
 
         BorlandDeploymentTool tool = new BorlandDeploymentTool();
         tool.setTask(this);
         deploymentTools.add(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for iPlanet Application Server.
      *
      * @return the deployment tool instance to be configured.
      */
     public IPlanetDeploymentTool createIplanet() {
         log("iPlanet Application Server deployment tools", Project.MSG_VERBOSE);
 
         IPlanetDeploymentTool tool = new IPlanetDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for JBoss server.
      *
      * @return the deployment tool instance to be configured.
      */
     public JbossDeploymentTool createJboss() {
         JbossDeploymentTool tool = new JbossDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for JOnAS server.
      *
      * @return the deployment tool instance to be configured.
      */
     public JonasDeploymentTool createJonas() {
         log("JOnAS deployment tools",  Project.MSG_VERBOSE);
 
         JonasDeploymentTool tool = new JonasDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds a deployment tool for Weblogic when using the Toplink
      * Object-Relational mapping.
      *
      * @return the deployment tool instance to be configured.
      */
     public WeblogicTOPLinkDeploymentTool createWeblogictoplink() {
         log("The <weblogictoplink> element is no longer required. Please use "
             + "the <weblogic> element and set newCMP=\"true\"",
             Project.MSG_INFO);
         WeblogicTOPLinkDeploymentTool tool
             = new WeblogicTOPLinkDeploymentTool();
         addDeploymentTool(tool);
         return tool;
     }
 
     /**
      * Adds to the classpath used to locate the super classes and
      * interfaces of the classes that will make up the EJB JAR.
      *
      * @return the path to be configured.
      */
     public Path createClasspath() {
         if (config.classpath == null) {
             config.classpath = new Path(getProject());
         }
         return config.classpath.createPath();
     }
 
     /**
      * Create a DTD location record. This stores the location of a DTD. The
      * DTD is identified by its public Id. The location may either be a file
      * location or a resource location.
      *
      * @return the DTD location object to be configured by Ant
      */
     public DTDLocation createDTD() {
         DTDLocation dtdLocation = new DTDLocation();
         config.dtdLocations.add(dtdLocation);
 
         return dtdLocation;
     }
 
     /**
      * Adds a fileset for support elements.
      *
      * @return a fileset which can be populated with support files.
      */
     public FileSet createSupport() {
         FileSet supportFileSet = new FileSet();
         config.supportFileSets.add(supportFileSet);
         return supportFileSet;
     }
 
 
     /**
      * Set the Manifest file to use when jarring. As of EJB 1.1, manifest
      * files are no longer used to configure the EJB. However, they still
      * have a vital importance if the EJB is intended to be packaged in an
      * EAR file. By adding "Class-Path" settings to a Manifest file, the EJB
      * can look for classes inside the EAR file itself, allowing for easier
      * deployment. This is outlined in the J2EE specification, and all J2EE
      * components are meant to support it.
      *
      * @param manifest the manifest to be used in the EJB jar
      */
      public void setManifest(File manifest) {
          config.manifest = manifest;
      }
 
     /**
      * Sets the source directory, which is the directory that
      * contains the classes that will be added to the EJB jar. Typically
      * this will include the home and remote interfaces and the bean class.
      *
      * @param inDir the source directory.
      */
     public void setSrcdir(File inDir) {
         config.srcDir = inDir;
     }
 
     /**
      * Set the descriptor directory. The descriptor directory contains the
      * EJB deployment descriptors. These are XML files that declare the
      * properties of a bean in a particular deployment scenario. Such
      * properties include, for example, the transactional nature of the bean
      * and the security access control to the bean's methods.
      *
      * @param inDir the directory containing the deployment descriptors.
      */
     public void setDescriptordir(File inDir) {
         config.descriptorDir = inDir;
     }
 
     /**
      * Set the analyzer to use when adding in dependencies to the JAR.
      *
      * @param analyzer the name of the dependency analyzer or a class.
      */
     public void setDependency(String analyzer) {
         config.analyzer = analyzer;
     }
 
     /**
      * Set the base name of the EJB JAR that is to be created if it is not
      * to be determined from the name of the deployment descriptor files.
      *
      * @param inValue the basename that will be used when writing the jar
      *      file containing the EJB
      */
     public void setBasejarname(String inValue) {
         config.baseJarName = inValue;
         if (config.namingScheme == null) {
             config.namingScheme = new NamingScheme();
             config.namingScheme.setValue(NamingScheme.BASEJARNAME);
         } else if (!config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME)) {
             throw new BuildException("The basejarname attribute is not compatible with the " +
                                      config.namingScheme.getValue() + " naming scheme");
         }
     }
 
     /**
      * Set the naming scheme used to determine the name of the generated jars
      * from the deployment descriptor
      *
      * @param namingScheme the naming scheme to be used
      */
     public void setNaming(NamingScheme namingScheme) {
         config.namingScheme = namingScheme;
         if (!config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME) &&
             config.baseJarName != null) {
             throw new BuildException("The basejarname attribute is not compatible with the " +
                                      config.namingScheme.getValue() + " naming scheme");
         }
     }
 
+    /**
+     * Gets the destination directory.
+     * 
+     * @return destination directory
+     * @since ant 1.6
+     */
+    public File getDestdir() {
+        return this.destDir;
+    }
 
     /**
      * Set the destination directory. The EJB jar files will be written into
      * this directory. The jar files that exist in this directory are also
      * used when determining if the contents of the jar file have changed.
      * Note that this parameter is only used if no deployment tools are
      * specified. Typically each deployment tool will specify its own
      * destination directory.
      *
      * @param inDir the destination directory in which to generate jars
      */
     public void setDestdir(File inDir) {
         this.destDir = inDir;
     }
 
     /**
+     * Gets the CMP version.
+     * 
+     * @return CMP version
+     * @since ant 1.6
+     */
+    public String getCmpversion() {
+        return this.cmpVersion;
+    }
+
+    /**
+     * Sets the CMP version.
+     * 
+     * @param version CMP version.
+     * Must be either <code>1.0</code> or <code>2.0</code>.<br/>
+     * Default is <code>1.0</code>.<br/>
+     * Initially, only the JBoss implementation does something specific for CMP 2.0.<br/>
+     * @since ant 1.6
+     */
+    public void setCmpversion( CMPVersion version ) {
+        this.cmpVersion = version.getValue();
+    }
+
+    /**
      * Set the classpath to use when resolving classes for inclusion in the jar.
      *
      * @param classpath the classpath to use.
      */
     public void setClasspath(Path classpath) {
         config.classpath = classpath;
     }
 
     /**
      * Controls whether the
      * destination JARs are written out in the destination directory with
      * the same hierarchical structure from which the deployment descriptors
      * have been read. If this is set to true the generated EJB jars are
      * written into the root of the destination directory, otherwise they
      * are written out in the same relative position as the deployment
      * descriptors in the descriptor directory.
      *
      * @param inValue the new value of the flatdestdir flag.
      */
     public void setFlatdestdir(boolean inValue) {
         config.flatDestDir = inValue;
     }
 
     /**
      * Set the suffix for the generated jar file. When generic jars are
      * generated, they have a suffix which is appended to the the bean name
      * to create the name of the jar file. Note that this suffix includes
      * the extension fo te jar file and should therefore end with an
      * appropriate extension such as .jar or .ear
      *
      * @param inString the string to use as the suffix.
      */
     public void setGenericjarsuffix(String inString) {
         this.genericJarSuffix = inString;
     }
 
     /**
      * The string which terminates the bean name.
      * The convention used by this task is
      * that bean descriptors are named as the BeanName with some suffix. The
      * baseNameTerminator string separates the bean name and the suffix and
      * is used to determine the bean name.
      *
      * @param inValue a string which marks the end of the basename.
      */
     public void setBasenameterminator(String inValue) {
         config.baseNameTerminator = inValue;
     }
 
     /**
      * Validate the config that has been configured from the build file
      *
      * @throws BuildException if the config is not valid
      */
     private void validateConfig() throws BuildException {
         if (config.srcDir == null) {
             throw new BuildException("The srcDir attribute must be specified");
         }
 
         if (config.descriptorDir == null) {
             config.descriptorDir = config.srcDir;
         }
 
         if (config.namingScheme == null) {
             config.namingScheme = new NamingScheme();
             config.namingScheme.setValue(NamingScheme.DESCRIPTOR);
         } else if (config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME) &&
                  config.baseJarName == null) {
             throw new BuildException("The basejarname attribute must be specified " +
                                      "with the basejarname naming scheme");
         }
     }
 
     /**
      * Invoked by Ant after the task is prepared, when it is ready to execute
      * this task.
      *
      * This will configure all of the nested deployment tools to allow them to
      * process the jar. If no deployment tools have been configured a generic
      * tool is created to handle the jar.
      *
      * A parser is configured and then each descriptor found is passed to all
      * the deployment tool elements for processing.
      *
      * @exception BuildException thrown whenever a problem is
      *            encountered that cannot be recovered from, to signal to ant
      *            that a major problem occurred within this task.
      */
     public void execute() throws BuildException {
         validateConfig();
 
         if (deploymentTools.size() == 0) {
             GenericDeploymentTool genericTool = new GenericDeploymentTool();
             genericTool.setTask(this);
             genericTool.setDestdir(destDir);
             genericTool.setGenericJarSuffix(genericJarSuffix);
             deploymentTools.add(genericTool);
         }
 
         for (Iterator i = deploymentTools.iterator(); i.hasNext();) {
             EJBDeploymentTool tool = (EJBDeploymentTool) i.next();
             tool.configure(config);
             tool.validateConfigured();
         }
 
         try {
             // Create the parser using whatever parser the system dictates
             SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
             saxParserFactory.setValidating(true);
             SAXParser saxParser = saxParserFactory.newSAXParser();
 
 
             DirectoryScanner ds = getDirectoryScanner(config.descriptorDir);
             ds.scan();
             String[] files = ds.getIncludedFiles();
 
             log(files.length + " deployment descriptors located.",
                 Project.MSG_VERBOSE);
 
             // Loop through the files. Each file represents one deployment
             // descriptor, and hence one bean in our model.
             for (int index = 0; index < files.length; ++index) {
                 // process the deployment descriptor in each tool
                 for (Iterator i = deploymentTools.iterator(); i.hasNext();) {
                     EJBDeploymentTool tool = (EJBDeploymentTool) i.next();
                     tool.processDescriptor(files[index], saxParser);
                 }
             }
         } catch (SAXException se) {
             String msg = "SAXException while creating parser."
                 + "  Details: "
                 + se.getMessage();
             throw new BuildException(msg, se);
         } catch (ParserConfigurationException pce) {
             String msg = "ParserConfigurationException while creating parser. "
                        + "Details: " + pce.getMessage();
             throw new BuildException(msg, pce);
         }
     } // end of execute()
+
 }
 
 
 
 
 
 
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/JbossDeploymentTool.java b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/JbossDeploymentTool.java
index ef8e59197..e98a61e59 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/ejb/JbossDeploymentTool.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/ejb/JbossDeploymentTool.java
@@ -1,110 +1,134 @@
 /*
  * The Apache Software License, Version 1.1
  *
  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 package org.apache.tools.ant.taskdefs.optional.ejb;
 
 import java.io.File;
 import java.util.Hashtable;
+import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 
 /**
  * The deployment tool to add the jboss specific deployment descriptor to the ejb jar file.
  * Jboss only requires one additional file jboss.xml and does not require any additional
  * compilation.
  *
  * @author <a href="mailto:p.austin@talk21.com">Paul Austin</a>
+ * @author <a href="mailto:rvanoo@xs4all.nl">Rob van Oostrum</a>
  * @version 1.0
  * @see EjbJar#createJboss
  */
 public class JbossDeploymentTool extends GenericDeploymentTool {
     protected static final String JBOSS_DD = "jboss.xml";
-    protected static final String JBOSS_CMPD = "jaws.xml";
+    protected static final String JBOSS_CMP10D = "jaws.xml";
+    protected static final String JBOSS_CMP20D = "jbosscmp-jdbc.xml";
 
     /** Instance variable that stores the suffix for the jboss jarfile. */
     private String jarSuffix = ".jar";
 
     /**
      * Setter used to store the suffix for the generated JBoss jar file.
      * @param inString the string to use as the suffix.
      */
     public void setSuffix(String inString) {
         jarSuffix = inString;
     }
     
     /**
      * Add any vendor specific files which should be included in the
      * EJB Jar.
      */
     protected void addVendorFiles(Hashtable ejbFiles, String ddPrefix) {
         File jbossDD = new File(getConfig().descriptorDir, ddPrefix + JBOSS_DD);
         if (jbossDD.exists()) {
             ejbFiles.put(META_DIR + JBOSS_DD, jbossDD);
         } else {
             log("Unable to locate jboss deployment descriptor. It was expected to be in " + jbossDD.getPath(), Project.MSG_WARN);
             return;
         }
-        
-        File jbossCMPD = new File(getConfig().descriptorDir, ddPrefix + JBOSS_CMPD);
+        String descriptorFileName = JBOSS_CMP10D;
+        if ( EjbJar.CMPVersion.CMP2_0.equals( getParent().getCmpversion() ) ) {
+            descriptorFileName = JBOSS_CMP20D;
+        }
+        File jbossCMPD = new File(getConfig().descriptorDir, ddPrefix + descriptorFileName);
+
         if (jbossCMPD.exists()) {
-            ejbFiles.put(META_DIR + JBOSS_CMPD, jbossCMPD);
+            ejbFiles.put(META_DIR + descriptorFileName, jbossCMPD);
+        } else {
+            log("Unable to locate jboss cmp descriptor. It was expected to be in " + jbossCMPD.getPath(), Project.MSG_WARN);
+            return;
         }
     }
 
     /**
      * Get the vendor specific name of the Jar that will be output. The modification date
      * of this jar will be checked against the dependent bean classes.
      */
     File getVendorOutputJarFile(String baseName) {
-        return new File(getDestDir(), baseName + jarSuffix);
+        return new File( getParent().getDestdir(), baseName + jarSuffix);
+    }
+
+    /**
+     * Called to validate that the tool parameters have been configured.
+     *
+     * @throws BuildException If the Deployment Tool's configuration isn't
+     *                        valid
+     * @since ant 1.6
+     */
+    public void validateConfigured() throws BuildException {
+    }
+
+    private EjbJar getParent() {
+        return ( EjbJar ) this.getTask();
     }
 }
