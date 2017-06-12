diff --git a/WHATSNEW b/WHATSNEW
index a446214e7..04a413545 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1333 +1,1343 @@
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
 
 * new Perforce tasks <p4integrate> and <p4resolve>
 
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
 
 * Support for HP's NonStop (Tandem) OS has been added.
 
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
 
+* <javac>'s executable attribute can now also be used to specify the
+  executable for jikes, jvc, sj or gcj.  Bugzilla Report 13814.
+
+* <javac> has a new attribute tempdir that can control the placement
+  of temporary files.  Bugzilla Report 19765.
+
+* A new magic property build.compiler.jvc.extensions has been added
+  that can be used to turn of Microsoft extensions while using the jvc
+  compiler.  Bugzilla Report 19826.
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
diff --git a/docs/manual/CoreTasks/javac.html b/docs/manual/CoreTasks/javac.html
index 371834110..4fc9284bd 100644
--- a/docs/manual/CoreTasks/javac.html
+++ b/docs/manual/CoreTasks/javac.html
@@ -1,572 +1,587 @@
 <html lang="en-us">
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <title>Javac Task</title>
 </head>
 
 <body>
 
 <h2><a name="javac">Javac</a></h2>
 <h3>Description</h3>
 <p>Compiles a Java source tree.</p>
 <p>The source and destination directory will be recursively scanned for Java
 source files to compile. Only Java files that have no corresponding
 <code>.class</code> file
 or where the class file is older than the
 <code>.java</code> file will be compiled.</p>
 <p>Note: Ant uses only the names of the source and class files to find
 the classes that need a rebuild. It will not scan the source and therefore
 will have no knowledge about nested classes, classes that are named different
 from the source file, and so on. See the
 <a href="../OptionalTasks/depend.html"><code>&lt;depend&gt;</code></a> task
 for dependency checking based on other than just
 existence/modification times.</p>
 <p>When the source files are part of a package, the directory structure of
 the source tree should follow the package
 hierarchy.</p>
 <p>It is possible to refine the set of files that are being compiled.
 This can be done with the <code>includes</code>, <code>includesfile</code>,
 <code>excludes</code>, and <code>excludesfile</code>
 attributes. With the <code>includes</code> or
 <code>includesfile</code> attribute, you specify the files you want to
 have included.
 The <code>exclude</code> or <code>excludesfile</code> attribute is used
 to specify
 the files you want to have excluded. In both cases, the list of files 
 can be specified by either the filename, relative to the directory(s) specified
 in the <code>srcdir</code> attribute or nested <code>&lt;src&gt;</code>
 element(s), or by using wildcard patterns. See the section on
 <a href="../dirtasks.html#directorybasedtasks">directory-based tasks</a>,
 for information on how the
 inclusion/exclusion of files works, and how to write wildcard patterns.</p>
 <p>It is possible to use different compilers. This can be specified by
 either setting the global <code>build.compiler</code> property, which will
 affect all <code>&lt;javac&gt;</code> tasks throughout the build, or by
 setting the <code>compiler</code> attribute, specific to the current
 <code>&lt;javac&gt;</code> task.
 <a name="compilervalues">Valid values for either the
 <code>build.compiler</code> property or the <code>compiler</code>
 attribute are:</a></p>
 <ul>
   <li><code>classic</code> (the standard compiler of JDK 1.1/1.2) &ndash;
       <code>javac1.1</code> and
       <code>javac1.2</code> can be used as aliases.</li>
   <li><code>modern</code> (the standard compiler of JDK 1.3/1.4) &ndash;
       <code>javac1.3</code> and
       <code>javac1.4</code> can be used as aliases.</li>
   <li><code>jikes</code> (the <a
     href="http://oss.software.ibm.com/developerworks/opensource/jikes/" target="_top">Jikes</a>
     compiler).</li>
   <li><code>jvc</code> (the Command-Line Compiler from Microsoft's SDK
       for Java / Visual J++) &ndash; <code>microsoft</code> can be used
       as an alias.</li>
   <li><code>kjc</code> (the <a href="http://www.dms.at/kopi/" target="_top">kopi</a>
     compiler).</li>
   <li><code>gcj</code> (the gcj compiler from gcc).</li>
   <li><code>sj</code> (Symantec java compiler) &ndash;
       <code>symantec</code> can be used as an alias.</li>
   <li><code>extJavac</code> (run either modern or classic in a JVM of
       its own).</li>
 </ul>
 <p>The default is <code>javac1.x</code> with <code>x</code> depending
 on the JDK version you use while you are running Ant.
 If you wish to use a different compiler interface than those
 supplied, you can write a class that implements the CompilerAdapter interface
 (<code>package org.apache.tools.ant.taskdefs.compilers</code>). Supply the full
 classname in the <code>build.compiler</code> property or the
 <code>compiler</code> attribute.
 </p>
 <p>The fork attribute overrides the <code>build.compiler</code> property
 or <code>compiler</code> attribute setting and
 expects a JDK1.1 or higher to be set in <code>JAVA_HOME</code>.
 </p>
 <p>You can also use the <code>compiler</code> attribute to tell Ant
 which JDK version it shall assume when it puts together the command
 line switches - even if you set <code>fork=&quot;true&quot;</code>.
 This is useful if you want to run the compiler of JDK 1.1 while you
 current JDK is 1.2+.  If you use
 <code>compiler=&quot;javac1.1&quot;</code> and (for example)
 <code>depend=&quot;true&quot;</code> Ant will use the command line
 switch <code>-depend</code> instead of <code>-Xdepend</code>.</p>
 <p>This task will drop all entries that point to non-existent
 files/directories from the classpath it passes to the compiler.</p>
 <p><strong>Windows Note:</strong>When the modern compiler is used
 in unforked mode on Windows, it locks up the files present in the
 classpath of the <code>&lt;javac&gt;</code> task, and does not release them.
 The side effect of this is that you will not be able to delete or move
 those files later on in the build.  The workaround is to fork when
 invoking the compiler.</p>
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">srcdir</td>
     <td valign="top">Location of the java files. (See the
      <a href="#srcdirnote">note</a> below.)</td>
     <td align="center" valign="top">Yes, unless nested <code>&lt;src&gt;</code> elements are present.</td>
   </tr>
   <tr>
     <td valign="top">destdir</td>
     <td valign="top">Location to store the class files.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includes</td>
     <td valign="top">Comma- or space-separated list of files (may be specified using
       wildcard patterns) that must be
       included; all <code>.java</code> files are included when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">includesfile</td>
     <td valign="top">The name of a file that contains a list of files to
       include (may be specified using wildcard patterns).</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludes</td>
     <td valign="top">Comma- or space-separated list of files (may be specified using
       wildcard patterns) that must be excluded; no files (except default
       excludes) are excluded when omitted.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">excludesfile</td>
     <td valign="top">The name of a file that contains a list of files to
       exclude (may be specified using wildcard patterns).</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">classpath</td>
     <td valign="top">The classpath to use.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">sourcepath</td>
     <td valign="top">The sourcepath to use; defaults to the value of the srcdir attribute (or nested <code>&lt;src&gt;</code> elements).
         To suppress the sourcepath switch, use <code>sourcepath=&quot;&quot;</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">bootclasspath</td>
     <td valign="top">Location of bootstrap class files.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">classpathref</td>
     <td valign="top">The classpath to use, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">sourcepathref</td>
     <td valign="top">The sourcepath to use, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">bootclasspathref</td>
     <td valign="top">Location of bootstrap class files, given as a
       <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">extdirs</td>
     <td valign="top">Location of installed extensions.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">encoding</td>
     <td valign="top">Encoding of source files. (Note: gcj doesn't support
       this option yet.)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">nowarn</td>
     <td valign="top">Indicates whether the <code>-nowarn</code> switch
       should be passed to the compiler; defaults to <code>off</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">debug</td>
     <td valign="top">Indicates whether source should be compiled with
     debug information; defaults to <code>off</code>.  If set to
     <code>off</code>, <code>-g:none</code> will be passed on the
     command line for compilers that support it (for other compilers, no
     command line argument will be used).  If set to <code>true</code>,
     the value of the <code>debuglevel</code> attribute determines the
     command line argument.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">debuglevel</td>
     <td valign="top">Keyword list to be appended to the <code>-g</code>
       command-line switch.  This will be ignored by all implementations except
       <code>modern</code> and <code>classic(ver &gt;= 1.2)</code>.
       Legal values are <code>none</code> or a comma-separated list of the
       following keywords:
       <code>lines</code>, <code>vars</code>, and <code>source</code>.
       If <code>debuglevel</code> is not specified, by default,
       nothing will be
       appended to <code>-g</code>.  If <code>debug</code> is not turned on,
       this attribute will be ignored.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">optimize</td>
     <td valign="top">Indicates whether source should be compiled with
       optimization; defaults to <code>off</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">deprecation</td>
     <td valign="top">Indicates whether source should be compiled with
       deprecation information; defaults to <code>off</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">target</td>
     <td valign="top">Generate class files for specific VM version
     (e.g., <code>1.1</code> or <code>1.2</code>). <b>Note that the
     default value depends on the JVM that is running Ant.  In
     particular, if you use JDK 1.4 the generated classes will not be
     usable for a 1.1 Java VM unless you explicitly set this attribute
     to the value 1.1 (which is the default value for JDK 1.1 to
     1.3).</b></td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">verbose</td>
     <td valign="top">Asks the compiler for verbose output.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">depend</td> <td valign="top">Enables dependency-tracking
       for compilers that support this (<code>jikes</code> and
       <code>classic</code>).</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeAntRuntime</td>
     <td valign="top">Whether to include the Ant run-time libraries in the
       classpath; defaults to <code>yes</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">includeJavaRuntime</td>
     <td valign="top">Whether to include the default run-time
       libraries from the executing VM in the classpath;
       defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">fork</td>
     <td valign="top">Whether to execute <code>javac</code> using the
       JDK compiler externally; defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">executable</td>
     <td valign="top">Complete path to the <code>javac</code>
       executable to use in case of <code>fork=&quot;yes&quot;</code>.
       Defaults to the compiler of the Java version that is currently
-      running Ant.  Ignored if <code>fork=&quot;no&quot;</code></td>
+      running Ant.  Ignored if <code>fork=&quot;no&quot;</code>.<br>
+      Since Ant 1.6 this attribute can also be used to specify the
+      path to the executable when using jikes, jvc, gcj or sj.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">memoryInitialSize</td>
     <td valign="top">The initial size of the memory for the underlying VM,
       if <code>javac</code> is run externally; ignored otherwise. Defaults
       to the standard VM memory setting.
       (Examples: <code>83886080</code>, <code>81920k</code>, or
       <code>80m</code>)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">memoryMaximumSize</td>
     <td valign="top">The maximum size of the memory for the underlying VM,
       if <code>javac</code> is run externally; ignored otherwise. Defaults
       to the standard VM memory setting.
       (Examples: <code>83886080</code>, <code>81920k</code>, or
       <code>80m</code>)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">failonerror</td> <td valign="top">
         Indicates whether the build will continue even if there are compilation errors; defaults to <code>true</code>.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">source</td>
 
     <td valign="top">Value of the <code>-source</code> command-line
     switch; will be ignored by all implementations except
     <code>javac1.4</code> (or <code>modern</code> when Ant is not
     running in a 1.3 VM) and <code>jikes</code>.<br> If you use this
     attribute together with <code>jikes</code>, you must make sure
     that your version of jikes supports the <code>-source</code>
     switch.<br> Legal values are <code>1.3</code> and <code>1.4</code>
     &ndash; by default, no <code>-source</code> argument will be used
     at all.</td>
 
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td valign="top">The compiler implementation to use.
       If this attribute is not set, the value of the
       <code>build.compiler</code> property, if set, will be used.
       Otherwise, the default compiler for the current VM will be used.
       (See the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">listfiles</td>
     <td valign="top">Indicates whether the source files to be compiled will
       be listed; defaults to <code>no</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
+  <tr>
+    <td valign="top">tempdir</td>
+    <td valign="top">Where Ant should place temporary files.
+      <em>Since Ant 1.6</em>.</td>
+    <td align="center" valign="top">No; default is the current working
+       directory.</td>
+  </tr>
 </table>
 
 <h3>Parameters specified as nested elements</h3>
 <p>This task forms an implicit <a href="../CoreTypes/fileset.html">FileSet</a> and
 supports all attributes of <code>&lt;fileset&gt;</code>
 (<code>dir</code> becomes <code>srcdir</code>) as well as the nested
 <code>&lt;include&gt;</code>, <code>&lt;exclude&gt;</code> and
 <code>&lt;patternset&gt;</code> elements.</p>
 <h4><code>src</code>, <code>classpath</code>, <code>sourcepath</code>, 
 <code>bootclasspath</code> and <code>extdirs</code></h4>
 <p><code>&lt;javac&gt;</code>'s <code>srcdir</code>, <code>classpath</code>,
 <code>sourcepath</code>, <code>bootclasspath</code>, and
 <code>extdirs</code> attributes are
 <a href="../using.html#path">path-like structures</a>
 and can also be set via nested
 <code>&lt;src&gt;</code>,
 <code>&lt;classpath&gt;</code>,
 <code>&lt;sourcepath&gt;</code>,
 <code>&lt;bootclasspath&gt;</code> and
 <code>&lt;extdirs&gt;</code> elements, respectively.</p>
 
 <h4>compilerarg</h4>
 
 <p>You can specify additional command line arguments for the compiler
 with nested <code>&lt;compilerarg&gt;</code> elements.  These elements
 are specified like <a href="../using.html#arg">Command-line
 Arguments</a> but have an additional attribute that can be used to
 enable arguments only if a given compiler implementation will be
 used.</p>
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
   <tr>
     <td valign="top">value</td>
     <td align="center" rowspan="4">See
     <a href="../using.html#arg">Command-line Arguments</a>.</td>
     <td align="center" rowspan="4">Exactly one of these.</td>
   </tr>
   <tr>
     <td valign="top">line</td>
   </tr>
   <tr>
     <td valign="top">file</td>
   </tr>
   <tr>
     <td valign="top">path</td>
   </tr>
   <tr>
     <td valign="top">compiler</td>
     <td>Only pass the specified argument if the chosen
       compiler implementation matches the value of this attribute.
       Legal values are the
       same as those in the above <a href="#compilervalues">list</a> of valid
       compilers.)</td>
     <td align="center">No</td>
   </tr>
 </table>
 
 <h3>Examples</h3>
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores
 the <code>.class</code> files in the <code>${build}</code> directory.
 The classpath used includes <code>xyz.jar</code>, and compiling with
 debug information is on.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;true&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores the <code>.class</code> files in the
 <code>${build}</code> directory.  This will fork off the javac
 compiler using the default <code>javac</code> executable.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;java$$javac.exe&quot;
   /&gt;</pre>
 <p>compiles all <code>.java</code> files under the <code>${src}</code>
 directory, and stores the <code>.class</code> files in the
 <code>${build}</code> directory.  This will fork off the javac
 compiler, using the executable named <code>java$javac.exe</code>.  Note
 that the <code>$</code> sign needs to be escaped by a second one.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          includes=&quot;mypackage/p1/**,mypackage/p2/**&quot;
          excludes=&quot;mypackage/p1/testpackage/**&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
   /&gt;</pre>
 <p>compiles <code>.java</code> files under the <code>${src}</code>
 directory, and stores the
 <code>.class</code> files in the <code>${build}</code> directory.
 The classpath used includes <code>xyz.jar</code>, and debug information is on.
 Only files under <code>mypackage/p1</code> and <code>mypackage/p2</code> are
 used. All files in and below the <code>mypackage/p1/testpackage</code>
 directory are excluded from compilation.</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}:${src2}&quot;
          destdir=&quot;${build}&quot;
          includes=&quot;mypackage/p1/**,mypackage/p2/**&quot;
          excludes=&quot;mypackage/p1/testpackage/**&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;
   /&gt;</pre>
 
 <p>is the same as the previous example, with the addition of a second
 source path, defined by
 the property <code>src2</code>. This can also be represented using nested
 <code>&lt;src&gt;</code> elements as follows:</p>
 
 <pre>  &lt;javac destdir=&quot;${build}&quot;
          classpath=&quot;xyz.jar&quot;
          debug=&quot;on&quot;&gt;
     &lt;src path=&quot;${src}&quot;/&gt;
     &lt;src path=&quot;${src2}&quot;/&gt;
     &lt;include name=&quot;mypackage/p1/**&quot;/&gt;
     &lt;include name=&quot;mypackage/p2/**&quot;/&gt;
     &lt;exclude name=&quot;mypackage/p1/testpackage/**&quot;/&gt;
   &lt;/javac&gt;</pre>
 
 <p>If you want to run the javac compiler of a different JDK, you
 should tell Ant, where to find the compiler and which version of JDK
 you will be using so it can choose the correct command line switches.
 The following example executes a JDK 1.1 javac in a new process and
 uses the correct command line switches even when Ant is running in a
 Java VM of a different version:</p>
 
 <pre>  &lt;javac srcdir=&quot;${src}&quot;
          destdir=&quot;${build}&quot;
          fork=&quot;yes&quot;
          executable=&quot;/opt/java/jdk1.1/bin/javac&quot;
          compiler=&quot;javac1.1&quot;
   /&gt;</pre>
 
 <p><a name="srcdirnote"><b>Note:</b></a>
 If you wish to compile only source files located in certain packages below a
 common root, use the <code>include</code>/<code>exclude</code> attributes
 or <code>&lt;include&gt;</code>/<code>&lt;exclude&gt;</code> nested elements
 to filter for these packages. Do not include part of your package structure
 in the <code>srcdir</code> attribute
 (or nested <code>&lt;src&gt;</code> elements), or Ant will recompile your
 source files every time you run your compile target. See the
 <a href="http://ant.apache.org/faq.html#always-recompiles">Ant FAQ</a>
 for additional information.</p>
 
 <p><b>Note:</b> If you are using Ant on Windows and a new DOS window pops up
 for every use of an external compiler, this may be a problem of the JDK you are
 using.  This problem may occur with all JDKs &lt; 1.2.</p>
 
 <h3>Jikes Notes</h3>
 
 <p>Jikes supports some extra options, which can be set be defining
 the properties shown below prior to invoking the task. The setting
 for each property will be in affect for all <code>&lt;javac&gt;</code>
 tasks throughout the build.
 The Ant developers are aware that
 this is ugly and inflexible &ndash; expect a better solution in the future.
 All the options are boolean, and must be set to <code>true</code> or
 <code>yes</code> to be
 interpreted as anything other than false. By default,
 <code>build.compiler.warnings</code> is <code>true</code>,
 while all others are <code>false</code>.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Property</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Default</b></td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.emacs
 	</td>
 	<td valign="top">
 	Enable emacs-compatible error messages.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.fulldepend
 	</td>
 	<td valign="top">
 	Enable full dependency checking; see<br>
 	the <code>+F</code> switch in the Jikes manual.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.pedantic
 	</td>
 	<td valign="top">
 	Enable pedantic warnings.
     </td>
     <td valign="top">
 	<code>false</code>
 	</td>
   </tr>
   <tr>
     <td valign="top">
 	build.compiler.warnings<br>
         <strong>Deprecated</strong>. Use
   <code>&lt;javac&gt;</code>'s <code>nowarn</code>
   attribute instead.
 	</td>
 	<td valign="top">
 	Don't disable warning messages.
     </td>
     <td valign="top">
 	<code>true</code>
 	</td>
   </tr>
 </table>
 
+<h3>Jvc Notes</h3>
+
+<p>Jvc will enable Microsoft extensions unless you set the property
+<code>build.compiler.jvc.extensions</code> to false before invoking
+<code>&lt;javac&gt;</code>.</p>
+
 <hr>
 <p align="center">Copyright &copy; 2000-2003 Apache Software Foundation.
 All rights Reserved.</p>
 
 </body>
 </html>
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index 19996ee17..572e4f4fc 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,864 +1,899 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.GlobPatternMapper;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 
 /**
  * Compiles Java source files. This task can take the following
  * arguments:
  * <ul>
  * <li>sourcedir
  * <li>destdir
  * <li>deprecation
  * <li>classpath
  * <li>bootclasspath
  * <li>extdirs
  * <li>optimize
  * <li>debug
  * <li>encoding
  * <li>target
  * <li>depend
  * <li>vebose
  * <li>failonerror
  * <li>includeantruntime
  * <li>includejavaruntime
  * <li>source
  * <li>compiler
  * </ul>
  * Of these arguments, the <b>sourcedir</b> and <b>destdir</b> are required.
  * <p>
  * When this task executes, it will recursively scan the sourcedir and
  * destdir looking for Java source files to compile. This task makes its
  * compile decision based on timestamp.
  *
  * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
  * @author Robin Green
  *         <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com</a>
  * @author Stefan Bodewig
  * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
  *
  * @version $Revision$
  *
  * @since Ant 1.1
  *
  * @ant.task category="java"
  */
 
 public class Javac extends MatchingTask {
 
     private static final String FAIL_MSG
         = "Compile failed; see the compiler error output for details.";
 
     private Path src;
     private File destDir;
     private Path compileClasspath;
     private Path compileSourcepath;
     private String encoding;
     private boolean debug = false;
     private boolean optimize = false;
     private boolean deprecation = false;
     private boolean depend = false;
     private boolean verbose = false;
     private String target;
     private Path bootclasspath;
     private Path extdirs;
     private boolean includeAntRuntime = true;
     private boolean includeJavaRuntime = false;
     private boolean fork = false;
     private String forkedExecutable = null;
     private boolean nowarn = false;
     private String memoryInitialSize;
     private String memoryMaximumSize;
     private FacadeTaskHelper facade = null;
 
     protected boolean failOnError = true;
     protected boolean listFiles = false;
     protected File[] compileList = new File[0];
 
     private String source;
     private String debugLevel;
+    private File tmpDir;
 
     /**
      * Javac task for compilation of Java files.
      */
     public Javac() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             facade = new FacadeTaskHelper("javac1.1");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)) {
             facade = new FacadeTaskHelper("javac1.2");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
             facade = new FacadeTaskHelper("javac1.3");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4)) {
             facade = new FacadeTaskHelper("javac1.4");
         } else {
             facade = new FacadeTaskHelper("classic");
         }
     }
 
     /**
      * Get the value of debugLevel.
      * @return value of debugLevel.
      */
     public String getDebugLevel() {
         return debugLevel;
     }
 
     /**
      * Keyword list to be appended to the -g command-line switch.
      *
      * This will be ignored by all implementations except modern
      * and classic(ver >= 1.2). Legal values are none or a
      * comma-separated list of the following keywords: lines, vars,
      * and source. If debuglevel is not specified, by default, :none
      * will be appended to -g. If debug is not turned on, this attribute
      * will be ignored.
      *
      * @param v  Value to assign to debugLevel.
      */
     public void setDebugLevel(String  v) {
         this.debugLevel = v;
     }
 
     /**
      * Get the value of source.
      * @return value of source.
      */
     public String getSource() {
         return source;
     }
 
     /**
      * Value of the -source command-line switch; will be ignored
      * by all implementations except modern and jikes.
      *
      * If you use this attribute together with jikes, you must
      * make sure that your version of jikes supports the -source switch.
      * Legal values are 1.3 and 1.4 - by default, no -source argument
      * will be used at all.
      *
      * @param v  Value to assign to source.
      */
     public void setSource(String  v) {
         this.source = v;
     }
 
     /**
      * Adds a path for source compilation.
      *
      * @return a nested src element.
      */
     public Path createSrc() {
         if (src == null) {
             src = new Path(getProject());
         }
         return src.createPath();
     }
 
     /**
      * Recreate src.
      *
      * @return a nested src element.
      */
     protected Path recreateSrc() {
         src = null;
         return createSrc();
     }
 
     /**
      * Set the source directories to find the source Java files.
      */
     public void setSrcdir(Path srcDir) {
         if (src == null) {
             src = srcDir;
         } else {
             src.append(srcDir);
         }
     }
 
     /** Gets the source dirs to find the source java files. */
     public Path getSrcdir() {
         return src;
     }
 
     /**
      * Set the destination directory into which the Java source
      * files should be compiled.
      */
     public void setDestdir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Gets the destination directory into which the java source files
      * should be compiled.
      */
     public File getDestdir() {
         return destDir;
     }
 
     /**
      * Set the sourcepath to be used for this compilation.
      */
     public void setSourcepath(Path sourcepath) {
         if (compileSourcepath == null) {
             compileSourcepath = sourcepath;
         } else {
             compileSourcepath.append(sourcepath);
         }
     }
 
     /** Gets the sourcepath to be used for this compilation. */
     public Path getSourcepath() {
         return compileSourcepath;
     }
 
     /**
      * Adds a path to sourcepath.
      */
     public Path createSourcepath() {
         if (compileSourcepath == null) {
             compileSourcepath = new Path(getProject());
         }
         return compileSourcepath.createPath();
     }
 
     /**
      * Adds a reference to a source path defined elsewhere.
      */
     public void setSourcepathRef(Reference r) {
         createSourcepath().setRefid(r);
     }
 
     /**
      * Set the classpath to be used for this compilation.
      *
      * @param classpath an Ant Path object containing the compilation classpath.
      */
     public void setClasspath(Path classpath) {
         if (compileClasspath == null) {
             compileClasspath = classpath;
         } else {
             compileClasspath.append(classpath);
         }
     }
 
     /** Gets the classpath to be used for this compilation. */
     public Path getClasspath() {
         return compileClasspath;
     }
 
     /**
      * Adds a path to the classpath.
      */
     public Path createClasspath() {
         if (compileClasspath == null) {
             compileClasspath = new Path(getProject());
         }
         return compileClasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Sets the bootclasspath that will be used to compile the classes
      * against.
      */
     public void setBootclasspath(Path bootclasspath) {
         if (this.bootclasspath == null) {
             this.bootclasspath = bootclasspath;
         } else {
             this.bootclasspath.append(bootclasspath);
         }
     }
 
     /**
      * Gets the bootclasspath that will be used to compile the classes
      * against.
      */
     public Path getBootclasspath() {
         return bootclasspath;
     }
 
     /**
      * Adds a path to the bootclasspath.
      */
     public Path createBootclasspath() {
         if (bootclasspath == null) {
             bootclasspath = new Path(getProject());
         }
         return bootclasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      */
     public void setBootClasspathRef(Reference r) {
         createBootclasspath().setRefid(r);
     }
 
     /**
      * Sets the extension directories that will be used during the
      * compilation.
      */
     public void setExtdirs(Path extdirs) {
         if (this.extdirs == null) {
             this.extdirs = extdirs;
         } else {
             this.extdirs.append(extdirs);
         }
     }
 
     /**
      * Gets the extension directories that will be used during the
      * compilation.
      */
     public Path getExtdirs() {
         return extdirs;
     }
 
     /**
      * Adds a path to extdirs.
      */
     public Path createExtdirs() {
         if (extdirs == null) {
             extdirs = new Path(getProject());
         }
         return extdirs.createPath();
     }
 
     /**
      * If true, list the source files being handed off to the compiler.
      */
     public void setListfiles(boolean list) {
         listFiles = list;
     }
 
     /** Get the listfiles flag. */
     public boolean getListfiles() {
         return listFiles;
     }
 
     /**
      * Indicates whether the build will continue
      * even if there are compilation errors; defaults to true.
      */
     public void setFailonerror(boolean fail) {
         failOnError = fail;
     }
 
     /**
      * @ant.attribute ignore="true"
      */
     public void setProceed(boolean proceed) {
         failOnError = !proceed;
     }
 
     /**
      * Gets the failonerror flag.
      */
     public boolean getFailonerror() {
         return failOnError;
     }
 
     /**
      * Indicates whether source should be
      * compiled with deprecation information; defaults to off.
      */
     public void setDeprecation(boolean deprecation) {
         this.deprecation = deprecation;
     }
 
     /** Gets the deprecation flag. */
     public boolean getDeprecation() {
         return deprecation;
     }
 
     /**
      * The initial size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      */
     public void setMemoryInitialSize(String memoryInitialSize) {
         this.memoryInitialSize = memoryInitialSize;
     }
 
     /** Gets the memoryInitialSize flag. */
     public String getMemoryInitialSize() {
         return memoryInitialSize;
     }
 
     /**
      * The maximum size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      */
     public void setMemoryMaximumSize(String memoryMaximumSize) {
         this.memoryMaximumSize = memoryMaximumSize;
     }
 
     /** Gets the memoryMaximumSize flag. */
     public String getMemoryMaximumSize() {
         return memoryMaximumSize;
     }
 
     /**
      * Set the Java source file encoding name.
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /** Gets the java source file encoding name. */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Indicates whether source should be compiled
      * with debug information; defaults to off.
      */
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     /** Gets the debug flag. */
     public boolean getDebug() {
         return debug;
     }
 
     /**
      * If true, compiles with optimization enabled.
      */
     public void setOptimize(boolean optimize) {
         this.optimize = optimize;
     }
 
     /** Gets the optimize flag. */
     public boolean getOptimize() {
         return optimize;
     }
 
     /**
      * Enables dependency-tracking for compilers
      * that support this (jikes and classic).
      */
     public void setDepend(boolean depend) {
         this.depend = depend;
     }
 
     /** Gets the depend flag. */
     public boolean getDepend() {
         return depend;
     }
 
     /**
      * If true, asks the compiler for verbose output.
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
     /** Gets the verbose flag. */
     public boolean getVerbose() {
         return verbose;
     }
 
     /**
      * Sets the target VM that the classes will be compiled for. Valid
      * strings are "1.1", "1.2", and "1.3".
      */
     public void setTarget(String target) {
         this.target = target;
     }
 
     /** Gets the target VM that the classes will be compiled for. */
     public String getTarget() {
         return target;
     }
 
     /**
      * If true, includes Ant's own classpath in the classpath.
      */
     public void setIncludeantruntime(boolean include) {
         includeAntRuntime = include;
     }
 
     /**
      * Gets whether or not the ant classpath is to be included in the classpath.
      */
     public boolean getIncludeantruntime() {
         return includeAntRuntime;
     }
 
     /**
      * If true, includes the Java runtime libraries in the classpath.
      */
     public void setIncludejavaruntime(boolean include) {
         includeJavaRuntime = include;
     }
 
     /**
      * Gets whether or not the java runtime should be included in this
      * task's classpath.
      */
     public boolean getIncludejavaruntime() {
         return includeJavaRuntime;
     }
 
     /**
      * If true, forks the javac compiler.
      *
      * @param f "true|false|on|off|yes|no"
      */
     public void setFork(boolean f) {
         fork = f;
     }
 
     /**
      * Sets the the name of the javac executable.
      *
      * <p>Ignored unless fork is true or extJavac has been specified
      * as the compiler.</p>
      */
     public void setExecutable(String forkExec) {
         forkedExecutable = forkExec;
     }
 
     /**
+     * The value of the executable attribute, if any.
+     *
+     * @since Ant 1.6
+     */
+    public String getExecutable() {
+        return forkedExecutable;
+    }
+
+    /**
      * Is this a forked invocation of JDK's javac?
      */
     public boolean isForkedJavac() {
         return fork || "extJavac".equals(getCompiler());
     }
 
     /**
      * The name of the javac executable to use in fork-mode.
+     *
+     * <p>This is either the name specified with the executable
+     * attribute or the full path of the javac compiler of the VM Ant
+     * is currently running in - guessed by Ant.</p>
+     *
+     * <p>You should <strong>not</strong> invoke this method if you
+     * want to get the value of the executable command - use {@link
+     * #getExecutable getExecutable} for this.</p>
      */
     public String getJavacExecutable() {
         if (forkedExecutable == null && isForkedJavac()) {
             forkedExecutable = getSystemJavac();
         } else if (forkedExecutable != null && !isForkedJavac()) {
             forkedExecutable = null;
         }
         return forkedExecutable;
     }
 
     /**
      * If true, enables the -nowarn option.
      */
     public void setNowarn(boolean flag) {
         this.nowarn = flag;
     }
 
     /**
      * Should the -nowarn option be used.
      */
     public boolean getNowarn() {
         return nowarn;
     }
 
     /**
      * Adds an implementation specific command-line argument.
      */
     public ImplementationSpecificArgument createCompilerArg() {
         ImplementationSpecificArgument arg =
             new ImplementationSpecificArgument();
         facade.addImplementationArgument(arg);
         return arg;
     }
 
     /**
      * Get the additional implementation specific command line arguments.
      * @return array of command line arguments, guaranteed to be non-null.
      */
     public String[] getCurrentCompilerArgs() {
         String chosen = facade.getExplicitChoice();
         // make sure facade knows about magic properties and fork setting
         facade.setImplementation(getCompiler());
         try {
             return facade.getArgs();
         } finally {
             facade.setImplementation(chosen);
         }
     }
 
+    /**
+     * Where Ant should place temporary files.
+     *
+     * @since Ant 1.6
+     */
+    public void setTempdir(File tmpDir) {
+        this.tmpDir = tmpDir;
+    }
+
+    /**
+     * Where Ant should place temporary files.
+     *
+     * @since Ant 1.6
+     */
+    public File getTempdir() {
+        return tmpDir;
+    }
 
     /**
      * Executes the task.
      */
     public void execute() throws BuildException {
         checkParameters();
         resetFileLists();
 
         // scan source directories and dest directory to build up
         // compile lists
         String[] list = src.list();
         for (int i = 0; i < list.length; i++) {
             File srcDir = getProject().resolveFile(list[i]);
             if (!srcDir.exists()) {
                 throw new BuildException("srcdir \""
                                          + srcDir.getPath()
                                          + "\" does not exist!", getLocation());
             }
 
             DirectoryScanner ds = this.getDirectoryScanner(srcDir);
             String[] files = ds.getIncludedFiles();
 
             scanDir(srcDir, destDir != null ? destDir : srcDir, files);
         }
 
         compile();
     }
 
     /**
      * Clear the list of files to be compiled and copied..
      */
     protected void resetFileLists() {
         compileList = new File[0];
     }
 
     /**
      * Scans the directory looking for source files to be compiled.
      * The results are returned in the class variable compileList
      */
     protected void scanDir(File srcDir, File destDir, String[] files) {
         GlobPatternMapper m = new GlobPatternMapper();
         m.setFrom("*.java");
         m.setTo("*.class");
         SourceFileScanner sfs = new SourceFileScanner(this);
         File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);
 
         if (newFiles.length > 0) {
             File[] newCompileList = new File[compileList.length +
                 newFiles.length];
             System.arraycopy(compileList, 0, newCompileList, 0,
                     compileList.length);
             System.arraycopy(newFiles, 0, newCompileList,
                     compileList.length, newFiles.length);
             compileList = newCompileList;
         }
     }
 
     /** Gets the list of files to be compiled. */
     public File[] getFileList() {
         return compileList;
     }
 
     protected boolean isJdkCompiler(String compilerImpl) {
         return "modern".equals(compilerImpl) ||
             "classic".equals(compilerImpl) ||
             "javac1.1".equals(compilerImpl) ||
             "javac1.2".equals(compilerImpl) ||
             "javac1.3".equals(compilerImpl) ||
             "javac1.4".equals(compilerImpl);
     }
 
     protected String getSystemJavac() {
         return JavaEnvUtils.getJdkExecutable("javac");
     }
 
     /**
      * Choose the implementation for this particular task.
      *
      * @since Ant 1.5
      */
     public void setCompiler(String compiler) {
         facade.setImplementation(compiler);
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overriden
      * via the compiler and fork attributes.</p>
      *
      * <p>If fork has been set to true, the result will be extJavac
      * and not classic or java1.2 - no matter what the compiler
      * attribute looks like.</p>
      *
      * @see #getCompilerVersion
      *
      * @since Ant 1.5
      */
     public String getCompiler() {
         String compilerImpl = getCompilerVersion();
         if (fork) {
             if (isJdkCompiler(compilerImpl)) {
                 if (facade.hasBeenSet()) {
                     log("Since fork is true, ignoring compiler setting.",
                         Project.MSG_WARN);
                 }
                 compilerImpl = "extJavac";
             } else {
                 log("Since compiler setting isn't classic or modern,"
                     + "ignoring fork setting.", Project.MSG_WARN);
             }
         }
         return compilerImpl;
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overriden
      * via the compiler attribute.</p>
      *
      * <p>This method does not take the fork attribute into
      * account.</p>
      *
      * @see #getCompiler
      *
      * @since Ant 1.5
      */
     public String getCompilerVersion() {
         facade.setMagicValue(getProject().getProperty("build.compiler"));
         return facade.getImplementation();
     }
 
     /**
      * Check that all required attributes have been set and nothing
      * silly has been entered.
      *
      * @since Ant 1.5
      */
     protected void checkParameters() throws BuildException {
         if (src == null) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
         if (src.size() == 0) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
 
         if (destDir != null && !destDir.isDirectory()) {
             throw new BuildException("destination directory \""
                                      + destDir
                                      + "\" does not exist "
                                      + "or is not a directory", getLocation());
         }
     }
 
     /**
      * Perform the compilation.
      *
      * @since Ant 1.5
      */
     protected void compile() {
         String compilerImpl = getCompiler();
 
         if (compileList.length > 0) {
             log("Compiling " + compileList.length +
                 " source file"
                 + (compileList.length == 1 ? "" : "s")
                 + (destDir != null ? " to " + destDir : ""));
 
             if (listFiles) {
                 for (int i = 0 ; i < compileList.length ; i++) {
                   String filename = compileList[i].getAbsolutePath();
                   log(filename) ;
                 }
             }
 
             CompilerAdapter adapter =
                 CompilerAdapterFactory.getCompiler(compilerImpl, this);
 
             // now we need to populate the compiler adapter
             adapter.setJavac(this);
 
             // finally, lets execute the compiler!!
             if (!adapter.execute()) {
                 if (failOnError) {
                     throw new BuildException(FAIL_MSG, getLocation());
                 } else {
                     log(FAIL_MSG, Project.MSG_ERR);
                 }
             }
         }
     }
 
     /**
      * Adds an "compiler" attribute to Commandline$Attribute used to
      * filter command line attributes based on the current
      * implementation.
      */
     public class ImplementationSpecificArgument extends
         org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
 
         public void setCompiler(String impl) {
             super.setImplementation(impl);
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
index fe5daec1d..b4c2442a3 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/DefaultCompilerAdapter.java
@@ -1,536 +1,546 @@
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Location;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.Javac;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  * This is the default implementation for the CompilerAdapter interface.
  * Currently, this is a cut-and-paste of the original javac task.
  *
  * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
  * @author Robin Green 
  *         <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com</a>
  * @author Stefan Bodewig
  * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
  *
  * @since Ant 1.3
  */
 public abstract class DefaultCompilerAdapter implements CompilerAdapter {
 
     /* jdg - TODO - all these attributes are currently protected, but they
      * should probably be private in the near future.
      */
 
     protected Path src;
     protected File destDir;
     protected String encoding;
     protected boolean debug = false;
     protected boolean optimize = false;
     protected boolean deprecation = false;
     protected boolean depend = false;
     protected boolean verbose = false;
     protected String target;
     protected Path bootclasspath;
     protected Path extdirs;
     protected Path compileClasspath;
     protected Path compileSourcepath;
     protected Project project;
     protected Location location;
     protected boolean includeAntRuntime;
     protected boolean includeJavaRuntime;
     protected String memoryInitialSize;
     protected String memoryMaximumSize;
 
     protected File[] compileList;
     protected static String lSep = System.getProperty("line.separator");
     protected Javac attributes;
 
     private FileUtils fileUtils = FileUtils.newFileUtils();
 
     /**
      * Set the Javac instance which contains the configured compilation
      * attributes.
      *
      * @param attributes a configured Javac task.
      */
     public void setJavac(Javac attributes) {
         this.attributes = attributes;
         src = attributes.getSrcdir();
         destDir = attributes.getDestdir();
         encoding = attributes.getEncoding();
         debug = attributes.getDebug();
         optimize = attributes.getOptimize();
         deprecation = attributes.getDeprecation();
         depend = attributes.getDepend();
         verbose = attributes.getVerbose();
         target = attributes.getTarget();
         bootclasspath = attributes.getBootclasspath();
         extdirs = attributes.getExtdirs();
         compileList = attributes.getFileList();
         compileClasspath = attributes.getClasspath();
         compileSourcepath = attributes.getSourcepath();
         project = attributes.getProject();
         location = attributes.getLocation();
         includeAntRuntime = attributes.getIncludeantruntime();
         includeJavaRuntime = attributes.getIncludejavaruntime();
         memoryInitialSize = attributes.getMemoryInitialSize();
         memoryMaximumSize = attributes.getMemoryMaximumSize();
     }
 
     /**
      * Get the Javac task instance associated with this compiler adapter
      *
      * @return the configured Javac task instance used by this adapter.
      */
     public Javac getJavac() {
         return attributes;
     }
 
     /**
+     * @since Ant 1.6
+     */
+    protected Project getProject() {
+        return project;
+    }
+
+    /**
      * Builds the compilation classpath.
      *
      */
     protected Path getCompileClasspath() {
         Path classpath = new Path(project);
 
         // add dest dir to classpath so that previously compiled and
         // untouched classes are on classpath
 
         if (destDir != null) {
             classpath.setLocation(destDir);
         }
 
         // Combine the build classpath with the system classpath, in an
         // order determined by the value of build.sysclasspath
 
         Path cp = compileClasspath;
         if (cp == null) {
             cp = new Path(project);
         }
         if (includeAntRuntime) {
             classpath.addExisting(cp.concatSystemClasspath("last"));
         } else {
             classpath.addExisting(cp.concatSystemClasspath("ignore"));
         }
 
         if (includeJavaRuntime) {
             classpath.addJavaRuntime();
         }
 
         return classpath;
     }
 
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd) {
         return setupJavacCommandlineSwitches(cmd, false);
     }
 
     /**
      * Does the command line argument processing common to classic and
      * modern.  Doesn't add the files to compile.
      */
     protected Commandline setupJavacCommandlineSwitches(Commandline cmd,
                                                         boolean useDebugLevel) {
         Path classpath = getCompileClasspath();
         // For -sourcepath, use the "sourcepath" value if present.
         // Otherwise default to the "srcdir" value.
         Path sourcepath = null;
         if (compileSourcepath != null) {
             sourcepath = compileSourcepath;
         } else {
             sourcepath = src;
         }
 
         String memoryParameterPrefix = assumeJava11() ? "-J-" : "-J-X";
         if (memoryInitialSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryInitialSize setting.", 
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "ms" + memoryInitialSize);
             }
         }
 
         if (memoryMaximumSize != null) {
             if (!attributes.isForkedJavac()) {
                 attributes.log("Since fork is false, ignoring "
                                + "memoryMaximumSize setting.",
                                Project.MSG_WARN);
             } else {
                 cmd.createArgument().setValue(memoryParameterPrefix
                                               + "mx" + memoryMaximumSize);
             }
         }
 
         if (attributes.getNowarn()) {
             cmd.createArgument().setValue("-nowarn");
         }
 
         if (deprecation == true) {
             cmd.createArgument().setValue("-deprecation");
         }
 
         if (destDir != null) {
             cmd.createArgument().setValue("-d");
             cmd.createArgument().setFile(destDir);
         }
 
         cmd.createArgument().setValue("-classpath");
 
         // Just add "sourcepath" to classpath ( for JDK1.1 )
         // as well as "bootclasspath" and "extdirs"
         if (assumeJava11()) {
             Path cp = new Path(project);
             /*
              * XXX - This doesn't mix very well with build.systemclasspath,
              */
             if (bootclasspath != null) {
                 cp.append(bootclasspath);
             }
             if (extdirs != null) {
                 cp.addExtdirs(extdirs);
             }
             cp.append(classpath);
             cp.append(sourcepath);
             cmd.createArgument().setPath(cp);
         } else {
             cmd.createArgument().setPath(classpath);
             // If the buildfile specifies sourcepath="", then don't
             // output any sourcepath.
             if (sourcepath.size() > 0) {
                 cmd.createArgument().setValue("-sourcepath");
                 cmd.createArgument().setPath(sourcepath);
             }
             if (target != null) {
                 cmd.createArgument().setValue("-target");
                 cmd.createArgument().setValue(target);
             }
             if (bootclasspath != null && bootclasspath.size() > 0) {
                 cmd.createArgument().setValue("-bootclasspath");
                 cmd.createArgument().setPath(bootclasspath);
             }
             if (extdirs != null && extdirs.size() > 0) {
                 cmd.createArgument().setValue("-extdirs");
                 cmd.createArgument().setPath(extdirs);
             }
         }
 
         if (encoding != null) {
             cmd.createArgument().setValue("-encoding");
             cmd.createArgument().setValue(encoding);
         }
         if (debug) {
             if (useDebugLevel && !assumeJava11()) {
                 String debugLevel = attributes.getDebugLevel();
                 if (debugLevel != null) {
                     cmd.createArgument().setValue("-g:" + debugLevel);
                 } else {
                     cmd.createArgument().setValue("-g");
                 }
             } else {
                 cmd.createArgument().setValue("-g");
             }
         } else if (!assumeJava11()) {
             cmd.createArgument().setValue("-g:none");
         }
         if (optimize) {
             cmd.createArgument().setValue("-O");
         }
 
         if (depend) {
             if (assumeJava11()) {
                 cmd.createArgument().setValue("-depend");
             } else if (assumeJava12()) {
                 cmd.createArgument().setValue("-Xdepend");
             } else {
                 attributes.log("depend attribute is not supported by the "
                                + "modern compiler", Project.MSG_WARN);
             }
         }
 
         if (verbose) {
             cmd.createArgument().setValue("-verbose");
         }
 
         addCurrentCompilerArgs(cmd);
 
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern.  Doesn't
      * add the files to compile.
      */
     protected Commandline setupModernJavacCommandlineSwitches(Commandline cmd) {
         setupJavacCommandlineSwitches(cmd, true);
         if (attributes.getSource() != null && !assumeJava13()) {
             cmd.createArgument().setValue("-source");
             cmd.createArgument().setValue(attributes.getSource());
         }
         return cmd;
     }
 
     /**
      * Does the command line argument processing for modern and adds
      * the files to compile as well.
      */
     protected Commandline setupModernJavacCommand() {
         Commandline cmd = new Commandline();
         setupModernJavacCommandlineSwitches(cmd);
 
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     protected Commandline setupJavacCommand() {
         return setupJavacCommand(false);
     }
 
     /**
      * Does the command line argument processing for classic and adds
      * the files to compile as well.
      */
     protected Commandline setupJavacCommand(boolean debugLevelCheck) {
         Commandline cmd = new Commandline();
         setupJavacCommandlineSwitches(cmd, debugLevelCheck);
         logAndAddFilesToCompile(cmd);
         return cmd;
     }
 
     /**
      * Logs the compilation parameters, adds the files to compile and logs the
      * &qout;niceSourceList&quot;
      */
     protected void logAndAddFilesToCompile(Commandline cmd) {
         attributes.log("Compilation " + cmd.describeArguments(),
                        Project.MSG_VERBOSE);
 
         StringBuffer niceSourceList = new StringBuffer("File");
         if (compileList.length != 1) {
             niceSourceList.append("s");
         }
         niceSourceList.append(" to be compiled:");
 
         niceSourceList.append(lSep);
 
         for (int i = 0; i < compileList.length; i++) {
             String arg = compileList[i].getAbsolutePath();
             cmd.createArgument().setValue(arg);
             niceSourceList.append("    " + arg + lSep);
         }
 
         attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
     }
 
     /**
      * Do the compile with the specified arguments.
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      */
     protected int executeExternalCompile(String[] args, int firstFileName) {
         return executeExternalCompile(args, firstFileName, false);
     }
 
     /**
      * Do the compile with the specified arguments.
      * @param args - arguments to pass to process on command line
      * @param firstFileName - index of the first source file in args,
      * if the index is negative, no temporary file will ever be
      * created, but this may hit the command line length limit on your
      * system.
      * @param quoteFilenames - if set to true, filenames containing
      * spaces will be quoted when they appear in the external file.
      * This is necessary when running JDK 1.4's javac and probably
      * others.
      *
      * @since Ant 1.6
      */
     protected int executeExternalCompile(String[] args, int firstFileName,
                                          boolean quoteFiles) {
         String[] commandArray = null;
         File tmpFile = null;
 
         try {
             /*
              * Many system have been reported to get into trouble with
              * long command lines - no, not only Windows ;-).
              *
              * POSIX seems to define a lower limit of 4k, so use a temporary
              * file if the total length of the command line exceeds this limit.
              */
             if (Commandline.toString(args).length() > 4096 
                 && firstFileName >= 0) {
                 PrintWriter out = null;
                 try {
-                    String userDirName = System.getProperty("user.dir");
-                    File userDir = new File(userDirName);
+                    File userDir = getJavac().getTempdir();
+                    if (userDir == null) {
+                        String userDirName = System.getProperty("user.dir");
+                        userDir = new File(userDirName);
+                    }
                     tmpFile = fileUtils.createTempFile("files", "", userDir);
                     out = new PrintWriter(new FileWriter(tmpFile));
                     for (int i = firstFileName; i < args.length; i++) {
                         if (quoteFiles && args[i].indexOf(" ") > -1) {
                             out.println("\"" + args[i] + "\"");
                         } else {
                             out.println(args[i]);
                         }
                     }
                     out.flush();
                     commandArray = new String[firstFileName + 1];
                     System.arraycopy(args, 0, commandArray, 0, firstFileName);
                     commandArray[firstFileName] = "@" + tmpFile;
                 } catch (IOException e) {
                     throw new BuildException("Error creating temporary file", 
                                              e, location);
                 } finally {
                     if (out != null) {
                         try {out.close();} catch (Throwable t) {}
                     }
                 }
             } else {
                 commandArray = args;
             }
 
             try {
                 Execute exe = new Execute(
                                   new LogStreamHandler(attributes,
                                                        Project.MSG_INFO,
                                                        Project.MSG_WARN));
                 exe.setAntRun(project);
                 exe.setWorkingDirectory(project.getBaseDir());
                 exe.setCommandline(commandArray);
                 exe.execute();
                 return exe.getExitValue();
             } catch (IOException e) {
                 throw new BuildException("Error running " + args[0]
                         + " compiler", e, location);
             }
         } finally {
             if (tmpFile != null) {
                 tmpFile.delete();
             }
         }
     }
 
     /**
      * @deprecated use org.apache.tools.ant.types.Path#addExtdirs instead
      */
     protected void addExtdirsToClasspath(Path classpath) {
         classpath.addExtdirs(extdirs);
     }
 
     /**
      * Adds the command line arguments specifc to the current implementation.
      */
     protected void addCurrentCompilerArgs(Commandline cmd) {
         cmd.addArguments(getJavac().getCurrentCompilerArgs());
     }
 
     /**
      * Shall we assume JDK 1.1 command line switches?
      * @since Ant 1.5
      */
     protected boolean assumeJava11() {
         return "javac1.1".equals(attributes.getCompilerVersion()) ||
             ("classic".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) ||
             ("extJavac".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1));
     }
 
     /**
      * Shall we assume JDK 1.2 command line switches?
      * @since Ant 1.5
      */
     protected boolean assumeJava12() {
         return "javac1.2".equals(attributes.getCompilerVersion()) ||
             ("classic".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)) ||
             ("extJavac".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2));
     }
 
     /**
      * Shall we assume JDK 1.3 command line switches?
      * @since Ant 1.5
      */
     protected boolean assumeJava13() {
         return "javac1.3".equals(attributes.getCompilerVersion()) ||
             ("classic".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) ||
             ("modern".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) ||
             ("extJavac".equals(attributes.getCompilerVersion()) 
              && JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3));
     }
 
 }
 
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/Gcj.java b/src/main/org/apache/tools/ant/taskdefs/compilers/Gcj.java
index d54c92af4..cad1e1d0a 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/Gcj.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/Gcj.java
@@ -1,149 +1,150 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 
 /**
  * The implementation of the gcj compiler.
  * This is primarily a cut-and-paste from the jikes.
  *
  * @author <a href="mailto:tora@debian.org">Takashi Okamoto</a>
  * @since Ant 1.4
  */
 public class Gcj extends DefaultCompilerAdapter {
 
     /**
      * Performs a compile using the gcj compiler.
      */
     public boolean execute() throws BuildException {
         Commandline cmd;
         attributes.log("Using gcj compiler", Project.MSG_VERBOSE);
         cmd = setupGCJCommand();
 
         int firstFileName = cmd.size();
         logAndAddFilesToCompile(cmd);
 
         return 
             executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
     }
 
     protected Commandline setupGCJCommand() {
         Commandline cmd = new Commandline();
         Path classpath = new Path(project);
 
         // gcj doesn't support bootclasspath dir (-bootclasspath)
         // so we'll emulate it for compatibility and convenience.
         if (bootclasspath != null) {
             classpath.append(bootclasspath);
         }
 
         // gcj doesn't support an extension dir (-extdir)
         // so we'll emulate it for compatibility and convenience.
         classpath.addExtdirs(extdirs);
 
         if (bootclasspath == null || bootclasspath.size() == 0) {
             // no bootclasspath, therefore, get one from the java runtime
             includeJavaRuntime = true;
         }
         classpath.append(getCompileClasspath());
 
         // Gcj has no option for source-path so we
         // will add it to classpath.
         if (compileSourcepath != null) {
             classpath.append(compileSourcepath);
         } else {
             classpath.append(src);
         }
 
-        cmd.setExecutable("gcj");
+        String exec = getJavac().getExecutable();
+        cmd.setExecutable(exec == null ? "gcj" : exec);
 
         if (destDir != null) {
             cmd.createArgument().setValue("-d");
             cmd.createArgument().setFile(destDir);
             
             if (destDir.mkdirs()) {
                 throw new BuildException("Can't make output directories. "
                                          + "Maybe permission is wrong. ");
             };
         }
         
         cmd.createArgument().setValue("-classpath");
         cmd.createArgument().setPath(classpath);
 
         if (encoding != null) {
             cmd.createArgument().setValue("--encoding=" + encoding);
         }
         if (debug) {
             cmd.createArgument().setValue("-g1");
         }
         if (optimize) {
             cmd.createArgument().setValue("-O");
         }
 
         /**
          *  gcj should be set for generate class.
          */
         cmd.createArgument().setValue("-C");
 
         addCurrentCompilerArgs(cmd);
 
         return cmd;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/Jikes.java b/src/main/org/apache/tools/ant/taskdefs/compilers/Jikes.java
index db5e3c373..18c06848d 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/Jikes.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/Jikes.java
@@ -1,237 +1,238 @@
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 
 /**
  * The implementation of the jikes compiler.
  * This is primarily a cut-and-paste from the original javac task before it
  * was refactored.
  *
  * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
  * @author Robin Green 
  *         <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com</a>
  * @author Stefan Bodewig 
  * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
  * @since Ant 1.3
  */
 public class Jikes extends DefaultCompilerAdapter {
 
     /**
      * Performs a compile using the Jikes compiler from IBM.
      * Mostly of this code is identical to doClassicCompile()
      * However, it does not support all options like
      * bootclasspath, extdirs, deprecation and so on, because
      * there is no option in jikes and I don't understand
      * what they should do.
      *
      * It has been successfully tested with jikes >1.10
      */
     public boolean execute() throws BuildException {
         attributes.log("Using jikes compiler", Project.MSG_VERBOSE);
 
         Path classpath = new Path(project);
 
         // Jikes doesn't support bootclasspath dir (-bootclasspath)
         // so we'll emulate it for compatibility and convenience.
         if (bootclasspath != null) {
             classpath.append(bootclasspath);
         }
 
         // Jikes doesn't support an extension dir (-extdir)
         // so we'll emulate it for compatibility and convenience.
         classpath.addExtdirs(extdirs);
 
         if (bootclasspath == null || bootclasspath.size() == 0) {
             // no bootclasspath, therefore, get one from the java runtime
             includeJavaRuntime = true;
         } else {
             // there is a bootclasspath stated.  By default, the
             // includeJavaRuntime is false.  If the user has stated a
             // bootclasspath and said to include the java runtime, it's on
             // their head!
         }
         classpath.append(getCompileClasspath());
 
         // Jikes has no option for source-path so we
         // will add it to classpath.
         if (compileSourcepath != null) {
             classpath.append(compileSourcepath);
         } else {
             classpath.append(src);
         }
 
         // if the user has set JIKESPATH we should add the contents as well
         String jikesPath = System.getProperty("jikes.class.path");
         if (jikesPath != null) {
             classpath.append(new Path(project, jikesPath));
         }
         
         Commandline cmd = new Commandline();
-        cmd.setExecutable("jikes");
+        String exec = getJavac().getExecutable();
+        cmd.setExecutable(exec == null ? "jikes" : exec);
 
         if (deprecation == true) {
             cmd.createArgument().setValue("-deprecation");
         }
 
         if (destDir != null) {
             cmd.createArgument().setValue("-d");
             cmd.createArgument().setFile(destDir);
         }
         
         cmd.createArgument().setValue("-classpath");
         cmd.createArgument().setPath(classpath);
 
         if (encoding != null) {
             cmd.createArgument().setValue("-encoding");
             cmd.createArgument().setValue(encoding);
         }
         if (debug) {
             cmd.createArgument().setValue("-g");
         }
         if (optimize) {
             cmd.createArgument().setValue("-O");
         }
         if (verbose) {
             cmd.createArgument().setValue("-verbose");
         }
         if (depend) {
             cmd.createArgument().setValue("-depend");
         } 
         /**
          * XXX
          * Perhaps we shouldn't use properties for these
          * three options (emacs mode, warnings and pedantic),
          * but include it in the javac directive?
          */
 
         /**
          * Jikes has the nice feature to print error
          * messages in a form readable by emacs, so
          * that emacs can directly set the cursor
          * to the place, where the error occured.
          */
         String emacsProperty = project.getProperty("build.compiler.emacs");
         if (emacsProperty != null && Project.toBoolean(emacsProperty)) {
             cmd.createArgument().setValue("+E");
         }
 
         /**
          * Jikes issues more warnings that javac, for
          * example, when you have files in your classpath
          * that don't exist. As this is often the case, these
          * warning can be pretty annoying.
          */
         String warningsProperty = 
             project.getProperty("build.compiler.warnings");
         if (warningsProperty != null) {
             attributes.log("!! the build.compiler.warnings property is "
                            + "deprecated. !!", Project.MSG_WARN);
             attributes.log("!! Use the nowarn attribute instead. !!",
                            Project.MSG_WARN);
             if (!Project.toBoolean(warningsProperty)) {
                 cmd.createArgument().setValue("-nowarn");
             }
         } if (attributes.getNowarn()) {
             /* 
              * FIXME later
              *
              * let the magic property win over the attribute for backwards 
              * compatibility
              */
             cmd.createArgument().setValue("-nowarn");
         }
 
         /**
          * Jikes can issue pedantic warnings. 
          */
         String pedanticProperty = 
             project.getProperty("build.compiler.pedantic");
         if (pedanticProperty != null && Project.toBoolean(pedanticProperty)) {
             cmd.createArgument().setValue("+P");
         }
  
         /**
          * Jikes supports something it calls "full dependency
          * checking", see the jikes documentation for differences
          * between -depend and +F.
          */
         String fullDependProperty = 
             project.getProperty("build.compiler.fulldepend");
         if (fullDependProperty != null 
             && Project.toBoolean(fullDependProperty)) {
             cmd.createArgument().setValue("+F");
         }
 
         if (attributes.getSource() != null) {
             cmd.createArgument().setValue("-source");
             cmd.createArgument().setValue(attributes.getSource());
         }
 
         addCurrentCompilerArgs(cmd);
 
         int firstFileName = cmd.size();
         logAndAddFilesToCompile(cmd);
 
         return 
             executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
     }
 
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/Jvc.java b/src/main/org/apache/tools/ant/taskdefs/compilers/Jvc.java
index 96f5e4893..e5cbf4bb2 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/Jvc.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/Jvc.java
@@ -1,143 +1,153 @@
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 
 /**
  * The implementation of the jvc compiler from microsoft.
  * This is primarily a cut-and-paste from the original javac task before it
  * was refactored.
  *
  * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
  * @author Robin Green 
  *         <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com</a>
  * @author Stefan Bodewig 
  * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
  * @since Ant 1.3
  */
 public class Jvc extends DefaultCompilerAdapter {
 
     /**
      * Run the compilation.
      *
      * @exception BuildException if the compilation has problems.
      */
     public boolean execute() throws BuildException {
         attributes.log("Using jvc compiler", Project.MSG_VERBOSE);
 
         Path classpath = new Path(project);
 
         // jvc doesn't support bootclasspath dir (-bootclasspath)
         // so we'll emulate it for compatibility and convenience.
         if (bootclasspath != null) {
             classpath.append(bootclasspath);
         }
 
         // jvc doesn't support an extension dir (-extdir)
         // so we'll emulate it for compatibility and convenience.
         classpath.addExtdirs(extdirs);
 
         classpath.append(getCompileClasspath());
 
         // jvc has no option for source-path so we
         // will add it to classpath.
         if (compileSourcepath != null) {
             classpath.append(compileSourcepath);
         } else {
             classpath.append(src);
         }
 
         Commandline cmd = new Commandline();
-        cmd.setExecutable("jvc");
+        String exec = getJavac().getExecutable();
+        cmd.setExecutable(exec == null ? "jvc" : exec);
 
         if (destDir != null) {
             cmd.createArgument().setValue("/d");
             cmd.createArgument().setFile(destDir);
         }
         
         // Add the Classpath before the "internal" one.
         cmd.createArgument().setValue("/cp:p");
         cmd.createArgument().setPath(classpath);
 
-        // Enable MS-Extensions and ...
-        cmd.createArgument().setValue("/x-");
-        // ... do not display a Message about this.
-        cmd.createArgument().setValue("/nomessage");
+        boolean msExtensions = true;
+        String mse = getProject().getProperty("build.compiler.jvc.extensions");
+        if (mse != null) {
+            msExtensions = Project.toBoolean(mse);
+        }
+
+        if (msExtensions) {
+            // Enable MS-Extensions and ...
+            cmd.createArgument().setValue("/x-");
+            // ... do not display a Message about this.
+            cmd.createArgument().setValue("/nomessage");
+        }
+
         // Do not display Logo
         cmd.createArgument().setValue("/nologo");
 
         if (debug) {
             cmd.createArgument().setValue("/g");
         }
         if (optimize) {
             cmd.createArgument().setValue("/O");
         }
         if (verbose) {
             cmd.createArgument().setValue("/verbose");
         }
 
         addCurrentCompilerArgs(cmd);
 
         int firstFileName = cmd.size();
         logAndAddFilesToCompile(cmd);
 
         return 
             executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/compilers/Sj.java b/src/main/org/apache/tools/ant/taskdefs/compilers/Sj.java
index 257eeeb83..bc41434a5 100644
--- a/src/main/org/apache/tools/ant/taskdefs/compilers/Sj.java
+++ b/src/main/org/apache/tools/ant/taskdefs/compilers/Sj.java
@@ -1,87 +1,88 @@
 /*
  * The Apache Software License, Version 1.1
  *
- * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
+ * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 
 package org.apache.tools.ant.taskdefs.compilers;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 
 /**
  * The implementation of the sj compiler.
  * Uses the defaults for DefaultCompilerAdapter
  * 
  * @author <a href="mailto:don@bea.com">Don Ferguson</a>
  * @since Ant 1.4
  */
 public class Sj extends DefaultCompilerAdapter {
 
     /**
      * Performs a compile using the sj compiler from Symantec.
      */
     public boolean execute() throws BuildException {
         attributes.log("Using symantec java compiler", Project.MSG_VERBOSE);
 
         Commandline cmd = setupJavacCommand();
-        cmd.setExecutable("sj");
+        String exec = getJavac().getExecutable();
+        cmd.setExecutable(exec == null ? "sj" : exec);
 
         int firstFileName = cmd.size() - compileList.length;
 
         return 
             executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
     }
 
 
 }
 
