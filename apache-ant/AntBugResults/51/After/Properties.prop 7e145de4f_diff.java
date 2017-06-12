diff --git a/WHATSNEW b/WHATSNEW
index cda6efca1..c0c5de6b2 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1137 +1,1140 @@
 Changes from current Ant 1.6 CVS version to current CVS version
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 Fixed bugs:
 -----------
 
 * Translate task does not remove tokens when a key is not found.
   It logs a verbose message.  Bugzilla Report 13936.
 
 * Wrapper scripts did not detect WINNT value of dynamic OS environment
   variable when logged into workstations using Novell authentication.
   Bugzilla Report 30366.
 
 * DependScanner.getResource() always returned nonexistent resources,
   even when the resource actually existed.  Bugzilla Report 30558.
 
 * <apply> was broken with classfilesets.  Bugzilla Report 30567.
 
 * The first file open that took place when using input files with the
   <exec>, <apply>, or <java> tasks was always logged to System.out
   instead of to the managing Task.
 
 * Incorrect task name with invalid "javac" task after a "presetdef.
   Bugzilla reports 31389 and 29499.
 
 Other changes:
 --------------
 
 * <echoproperties> now (alphanumerically) sorts the property list
   before echoing, when you request XML output (format="xml"). 
 
 * Changed default tempdir for <javac> from user.dir to java.io.tmpdir.
 
 * A new base class DispatchTask has been added to facilitate elegant 
   creation of tasks with multiple actions.
 
 * Added <target> nested elements to <ant> and <antcall> to allow
   specification of multiple sub-build targets, which are executed
   with a single dependency analysis.
   
 * Major revision of <wsdltodotnet>. Supports mono wsdl and the microsoft
   wsdl run on mono, as well as most of the .NET WSE2.0 options. Extra
   schemas (files or urls) can be named in the <schema> element.
   Compilers can be selected using the compiler attribute, which defaults
   to "microsoft" on windows, and "mono" on everything else.
 
 * Refactored Target invocation into org.apache.tools.ant.Executor
   implementations.  Bugzilla Reports 21421, 29248.
 
 * It is now possible to specify the pattern created/parsed by <checksum>.
   Bugzilla Report 16539.
 
 * Added searchpath attribute to <exec> for searching path variable(s)
   when resolveexecutable = true.
 
 * Added -nouserlib option to allow running ant without automatically loading
   up ${user.dir}/.lib/ant. This is useful when compiling ant, and antlibs.
   Modified the build.sh and build.bat to use the option.
 
 * Added -noclasspath option to allow running ant WITHOUT using CLASSPATH env
   variable. Modified ant.bat to do this so that %CLASSPATH% is not looked at.
 
 * Added revision and userid attributes to <pvcs> documentation.
 
 * Added a new "failall" value for the onerror attribute of <typedef>.
   Bugzilla report 31685.
 
 * New task <getlibraries> can retrieve library files from a maven repository.  
 
 Changes from Ant 1.6.2 to current Ant 1.6 CVS version
 =====================================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 Other changes:
 --------------
 
 * New attribute ignorecontents for <different> selector
 
 * Javadoc fixes for Location, Project, and RuntimeConfigurable
   Bugzilla 30160.
 
 * Enable to choose the regexp implementation without system property.
   Bugzilla Report 15390.
 
 * Expose objects and methods in IntrospectionHelper. Bugzilla Report 30794.
 
 * Allow file attribute of <move> to rename a directory.
   Bugzilla Report 22863.
 
 * Add xmlcatalog nested element to XmlProperty. Bugzilla report 27053.
 
 * New attribute alwayslog for <redirector> type.
 
 Fixed bugs:
 -----------
 
 * AbstractCvsTask prematurely closed its outputStream and errorStream.
   Bugzilla 30097.
 
 * Impossible to use implicit classpath for <taskdef>
   when Ant core loader != Java application loader and Path.systemClassPath taken from ${java.class.path}
   Bugzilla 30161.
 
 * MacroInstance did not clean up nested elements correctly in the execute method, causing
   multiple use of the same macro instance with nested elements to fail.
 
 * checksum fileext property doc wrong. Bugzilla 30787.
 
 * FTP task, getTimeDiff method was returning wrong value. Bugzilla 30595.
 
 * Zip task was not zipping when only empty directories were found. Bugzilla 30365.
 
 * Classpath was treated in the same way as -lib options. Bugzilla 28046.
 
 * Manual page for cvsversion contained incorrect attributes and did not say since 1.6.1.
   Bugzilla 31408.
 
 * Typo in definition of <cvsversion> task causing it not to be defined.
   Bugzilla 31403.
 
 * Execution of top level tasks in imported files get delayed by targets.
   Bugzilla report 31487.
 
 * ExecTask executes checkConfiguration() even though os does not match.
   Bugzilla report 31805.
 
 * Concat task instance could not be run twice.
   Bugzilla report 31814.
 
 * NPE using XmlLogger and antlib.
   Bugzilla report 31840.
 
+* Properties.propertyNames() should be used instead of .keys().
+  Bugzilla report 27261.
+
 Changes from Ant 1.6.1 to Ant 1.6.2
 ===================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * The import task used the canonical version of a file path. This
   has been changed to use the absolute path. Bugzilla 28505.
 
 * ant-xalan2.jar has been removed since the only class contained in it
   didn't depend on Xalan-J 2 at all.  Its sole dependency has always
   been TraX and so it has been merged into ant-trax.jar.
 
 * All exceptions thrown by tasks are now wrapped in a buildexception
   giving the location in the buildfile of the task.
 
 * Nested elements for namespaced tasks and types may belong to the
   Ant default namespace as well as the task's or type's namespace.
 
 * <junitreport> will very likely no longer work with Xalan-J 1.
 
   Note that Xalan-J 1 has been deprecated for a very long time and we
   highly recommend that you upgrade.
 
   If you really need to continue using Xalan-J 1, please copy the
   junit-frames-xalan1.xsl from the distribution's etc directory as
   junit-frames.xsl into a new directory and use the task's styledir
   attribute to point to.  This is the last version of the XSLT
   stylesheet that is expected to be compatible with Xalan-J 1.
 
 Fixed bugs:
 -----------
 
 * eliminate memory leak in AntClassLoader. Bugzilla Report 8689.
 
 * subant haltonfailure=false did not catch all failures. Bugzilla Report 27007.
 
 * macrodef @@ escaping was broken.  Bugzilla Report 27069.
 
 * SQL task did not work with Informix IDS 9.2. Bugzilla Report 27162.
 
 * MacroDef did not allow attributes named 'description'. Bugzilla Report 27175.
 
 * Throw build exception if name attribute missing from patternset#NameEntry.
   Bugzilla Report 25982.
 
 * Throw build exception if target repeated in build file, but allow targets
   to be repeated in imported files. 
 
 * <apply> didn't compare timestamps of source and targetfiles when
   using a nested <filelist>.  Bugzilla Report 26985.
 
 * tagdiff.xml was broken in ant 1.6.1. Bugzilla Report 27057.
 
 * if the basedir contained .. or . dirs, and the build file name contained
   .. or ., the basedir was set incorrectly. Bugzilla Report 26765.
 
 * regression from ant 1.5, exec task outputted two redundant trailing newlines.
   Bugzilla Report 27546.
 
 * NPE when running commons listener. Bugzilla Report 27373.
 
 * <java> swallowed the stack trace of exceptions thrown by the
   executed program if run in the same VM.
 
 * -projecthelp swallowed (configuration) errors silently.
   Bugzilla report 27732.
 
 * filterset used by filtertask doesn't respect loglevel. Bugzilla Report 27568.
 
 * wrong compare used in ProjectComponent for logging. Bugzilla Report 28070.
 
 * failOnAny attribute for <parallel> was broken. Bugzilla Report 28122.
 
 * If <javac> uses gcj and any of the nested <compilerarg>s implies
   compilation to native code (like -o or --main), Ant will not pass
   the -C switch to gcj.  This means you can now compile to native code
   with gcj which has been impossible in Ant < 1.6.2.
 
 * <import optional="false"> and <import optional="true">
   behaved identically.
 
 * <xslt> now sets the context classloader if you've specified a nested
   <classpath>.  Bugzilla Report 24802.
 
 * <zip> and friends would delete the original file when trying to update
   a read-only archive.  Bugzilla Report 28419.
 
 * <junit> and <assertions> are working together. Bugzilla report 27218
 
 * AntClassLoader#getResource could return invalid URLs.  Bugzilla
   Report 28060.
 
 * Ant failed to locate tools.jar if the jre directory name wasn't all
   lowercase.  Bugzilla Report 25798.
 
 * Redirector exhibited inconsistent behavior with regard to split
   output.  When sent to file only, files would be created in all
   cases; when split file-property, files were only created if
   writes were performed.
 
 * fixed case handling of scriptdef attributes and elements.
 
 * UNC pathnames did not work for ANT_HOME or -lib locations on Windows.
   Bugzilla report 27922.
 
 * replacestring tokenfilter only replaced the first occurrence.
 
 * AntLikeTasksAtTopLevelTest failed on cygwin.
 
 * I/O-intensive processes hung when executed via <exec spawn="true">.
   Bugzilla reports 23893/26852.
 
 * JDependTask did not close an output file. Bugzilla Report 28557.
 
 * Using <macrodef> could break XmlLogger. Bugzilla Report 28993.
 
 * <genkey> no longer requires keytool to be in your PATH.  Bugzilla
   Report 29382.
 
 * <symlink> could create cyclic links.  Bugzilla Report 25181.
 
 * <zip whenempty="skip"> didn't work in a common situation.  Bugzilla
   Report 22865.
 
 * <scp> now properly handles remote files and directories with spaces
   in their names.  Bugzilla Report 26097.
 
 * <scp> now has (local|remote)tofile attributes to rename files on the
   fly.  Bugzilla Report 26758.
 
 * <telnet> and <rexec> didn't close the session.  Bugzilla Report 25935.
 
 * <subant> and XmlLogger didn't play nicley together.
 
 Other changes:
 --------------
 * doc fix concerning the dependencies of the ftp task
   Bugzilla Report 29334.
 
 * <xmlvalidate> has now a property nested element,
   allowing to set string properties for the parser
   Bugzilla Report 23395.
 
 * Docs fixes for xmlvalidate.html, javadoc.html, starteam.
   Bugzilla Reports 27092, 27284, 27554.
 
 * <pathconvert> now accepts nested <mapper>s.  Bugzilla Report 26364.
 
 * Shipped XML parser is now Xerces-J 2.6.2.
 
 * Added nested file element to filelist.
 
 * spelling fixes, occurred. Bugzilla Report 27282.
 
 * add uid and gid to tarfileset. Bugzilla Report 19120.
 
 * <scp> has a verbose attribute to get some feedback during the
   transfer and new [local|remote][File|Todir] alternatives to file and
   todir that explicitly state the direction of the transfer.
 
 * The OS/2 wrapper scripts have been adapted to use the new launcher.
   Bugzilla Report 28226.
 
 * <sshexec> now also captures stderr output.  Bugzilla Report 28349.
 
 * <xslt> now supports a nested <mapper>.  Bugzilla Report 11249.
 
 * <touch> has filelist support.
 
 * <nice> task lets you set the priority of the current thread; non-forking
   <java> code will inherit this priority in their main thread.
 
 * New attribute "negate" on <propertyset> to invert selection criteria.
 
 * Target now supports a Location member.  Bugzilla Report 28599.
 
 * New "pattern" attribute for <date> selector.
 
 * <junit> has a new forkmode attribute that controls the number of
   Java VMs that get created when forking tests.  This allows you to
   run all tests in a single forked JVM reducing the overhead of VM
   creation a lot.  Bugzilla Report 24697.
 
 * <jar> can now optionally create an index for jars different than the
   one it currently builds as well.  See the new <indexjars> element
   for details.  Bugzilla Report 14255.
 
 * Permit building under JDK 1.5. Bugzilla Report 28996.
 
 * minor Javadoc changes. Bugzilla Report 28998.
 
 * Misc. corrections in SignJar.java. Bugzilla Report 28999.
 
 * Remove redundant <hr> from javah.html. Bugzilla Report 28995.
 
 * Ignore built distributions. Bugzilla Report 28997.
 
 * A new roundup attribute on <zip> and related task can be used to
   control whether the file modification times inside the archive will
   be rounded up or down (since zips only store modification times with
   a granularity of two seconds).  The default remains to round up.
   Bugzilla Report 17934.
 
 * A binary option has been added to <concat>. Bugzilla Report 26312.
 
 * Added DynamicConfiguratorNS, an namespace aware version of
   DynamicConfigurator. Bugzilla Report 28436.
 
 * Add implicit nested element to <macrodef>. Bugzilla Report 25633.
 
 * Add deleteonexit attribute to <delete>.
 
 * Added Target.getIf/Unless().  Bugzilla Report 29320.
 
 * <fail> has a status attribute that can be used to pass an exit
   status back to the command line.
 
 * <fail> accepts a nested <condition>.
 
 * <loadproperties> supports loading from a resource.
   Bugzilla Report 28340.
 
 * Nested file mappers and a container mapper implementation have been
   introduced.  Additionally, the <mapper> element now accepts "defined"
   nested FileNameMapper implementations directly, allowing a usage
   comparable to those of <condition>, <filter>, and <selector>.
 
 * New <redirector> type introduced to provide extreme I/O flexibility.
   Initial support for <exec>, <apply>, and <java> tasks.
 
 * <apply> has a new ignoremissing attribute (default true for BC)
   which will allow nonexistent files specified via <filelist>s to
   be passed to the executable.  Bugzilla Report 29585.
 
 * <junitreport> now also works with Xalan XSLTC and/or JDK 1.5.
   Bugzilla Report 27541.
 
 * <jspc> doesn't work properly with Tomcat 5.x.  We've implemented a
   work-around but don't intend to support future changes in Tomcat
   5.x.  Please use the jspc task that ships with Tomcat instead of
   Ant's.
 
 Changes from Ant 1.6.0 to Ant 1.6.1
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 * License is now Apache Software License 2.0
   see http://www.apache.org/licenses/ for more information
 
 Fixed bugs:
 -----------
 * Remove a recursive template call in the junit xsls that could trigger a stack
   overflow. It now uses Xalan extensions to call a Java class directly.
   Bugzilla Report 19301
   
 * Fix spurious infinite loop detection for filters (introduced in ant 1.6.0).
   Bugzilla Report 23154.
 
 * Fix handling of default ant namespace for nested elements.
 
 * Fix jboss element of ejb task (introduced in ant 1.6.0).
 
 * <whichresource> failed to load classes correctly.
 
 * Ant could fail to start with a NullPointerException if
   ANT_HOME/lib/ant-launcher.jar was part of the system CLASSPATH.
 
 * presetdef'ed types did not work with the ant-type attribute
 
 * fixed case handling of macrodef attributes and elements. Bugzilla
   Reports 25687 and 26225.
 
 * <java> ignored the append attribute, Bugzilla Report 26137.
 
 * The gcj compiler adapter for <javac> failed if the destination
   directory didn't exist.  Bugzilla Report 25856.
 
 * Ant now fails with a more useful message if a new process will be
   forked in a directory and that directory doesn't exist.
 
 * <splash> used to break the build on non-GUI environments.  Bugzilla
   report 11482.
 
 * Ant 1.6.0 cannot run build scripts in directories with non-ASCII names.
   Bugzilla Report 26642.
 
 Other changes:
 --------------
 * Shipped XML parser is now Xerces-J 2.6.1
 
 * Translate task logs a debug message specifying the number of files
   that it processed.  Bugzilla Report 13938.
 
 * <fixcrlf> has a new attribute - fixlast. Bugzilla Report 23262.
 
 * <p4submit> has 2 new attributes, needsresolveproperty and changeproperty.
   Bugzilla Report 25711.
 
 * add description attributes to macrodef attributes and elements.
   Bugzilla Report 24711.
 
 * Extending ClearCase Tasks :
  - Added an extra option to 'failonerr' to each ClearCase task/command.
  - Extended the functionality of cccheckout. It can check (notco) to see if
   the desired element is already checked out to the current view. Thus it
    won't attempt to check it out again.
  - Added three new ClearCase commands: ccmkattr, ccmkdir, ccmkelem
   Bugzilla Report 26253.
 
 * New condition <typefound> that can be used to probe for the declaration 
   and implementation of a task, type, preset, macro, scriptdef, whatever. 
   As it tests for the implementation, it can be used to check for optional
   tasks being available. 
 
 * added nested text support to <macrodef>
   
 * added initial support for Java 1.5.  Java 1.5 is now correctly
   detected by Ant and treated just like Java 1.4.  You can now specify
   source="1.5" in the <javac> task.
 
 * created new task <cvsversion>
 
 * added support for branch logging via the tag attribute in <cvschangelog>
   Bugzilla Report 13510.
 
 * added support the groovy language in the script and scriptdef tasks
 
 Changes from Ant 1.5.4 to Ant 1.6.0
 ===================================
 
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
   declarations no longer cause errors. However task names containing colons
   will cause errors unless there is a corresponding namespace uri.
 
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
 
 * The values of the Copy#fileCopyMap variable has changed from String to
   String[]. (In java 1.5 terms it was Hashtable<String, String> and
   is now Hashtable<String, String[]>). This will affect third party code
   that extend Copy and override Copy#doFileOperations.
 
 * <loadproperties> didn't expand properties while <property file="..."/>
   does, so they were not equivalent.  This has been fixed, which means
   that propetries may get expanded twice if you use an
   <expandproperties> filterreader.  Bugzilla Report 17782.
 
 * User defined tasks and typedefs are now handled internally in the
   same way as predefined tasks and typedefs. Also tasks and typedefs
   are resolved at a later stage. This causes some
   differences especially for user defined task containers.
 
 * <checksum> log message "Calculating checksum ..." has been degraded from INFO to VERBOSE.
 
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
 
 * <replaceregexp> was altering unnecessarily the timestamp of the directories
   containing the files to process
   Bugzilla Report 22541.
 
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
 
 * <cvstagdiff> had a problem with aliased modules and with requests for multiple modules
   Bugzilla Reports 21373 and 22877.
 
 * <cvstagdiff> could not parse properly the revision number of new files with CVS 1.11.9 or higher
   Bugzilla Report 24406.
 
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
 
 * ftp put with chmod failed when the remote system was UNIX and local system Windows
   Bugzilla Report 23143.
 
 * ftp did not set the ascii mode explicity, causing problems with ftp servers
   having binary as default
 
 * ftp was not able to download files when they were pointed to by symbolic links
   Bugzilla Report 14063.
 
 * ftp is able to download also directories pointed to by symbolic links.
 
 * replace would change \r\n into \r\r\n under Windows.
 
 * junitreport with frames did not display a link for classes without a package
   or in the top package.
   Bugzilla Report 21915.
 
 * Project.toBoolean(String) now handles null as argument and does not throw a
   NullPointerException any more.
 
 * The socket condition will now close the socket created to test.
   Bugzilla Report 23040.
 
 * <junit includeantruntime="true" fork="true"> replaced the CLASSPATH instead
   of adding to it.  Bugzilla Report 14971.
 
 * <splash> could fail on JVMs that use null to indicate the system classloader.
   Bugzilla Report 23320.
 
 * <xmlcatalog>s only worked when defined inside of tasks.  Bugzilla
   Report 20965.
 
 * <csc> and siblings (<vbc> <jsharpc>) handle large filesets by
 automatic use of response files.  Bugzilla report #19630
 
 Other changes:
 --------------
 
 * Shipped XML parser is now Xerces 2.6.0
 
 * All tasks can be used outside of <target>s.  Note that some tasks
   will not work at all outside of targets as they would cause infinite
   loops (<antcall> as well as <ant> and <subant> if they invoke the
   current build file).
 
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
 
 * Added <image> task (requires JAI).
 
 * <image> task has now proportions attribute in the <scale/> nested element
   instead of keepproportions (bringing in more functionality)
 
 * New condition <isreference>
 
 * <ftp> now has a preservelastmodified attribute to preserve the
   timestamp of a downloaded file.
 
 * new rmdir action for <ftp> that removes directories from a fileset.
 
 * <ftp> has attributes timediffauto and timediffmillis to use together
   with the newer attribute to tell ant to take into account a time difference
   between client and remote side.
   Bugzilla Report 19358.
 
 * <ftp> has been optimized to go directly to the include patterns.
   This reduces scanning time under UNIX when followsymlinks="true"
   and casesensitive="true" (the default)
   Bugzilla Report 20103.
 
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
 
 * <junit>'s XML formatter adds a new classname attribute to the <testcase>
   elements.
 
 * new <permissions> type add permission handling to the code
   this type can be nested in the <java> and <junit> tasks.
   Bugzilla Report 22533.
 
 * additional shortcuts for ant options (-d --> -debug, -e --> -emacs,
   -h --> -help, -p --> -projecthelp, -s --> -find).
 
 * new selector <modified>. "cache" was renamed to "modified".
   Bugzilla Report 20474.
 
 * <stcheckout> and <stlist> have a new asofdate attribute that can be
   used to checkout/list files based on a date instead of a label.
   Bugzilla Report 20578.
 
 * New filter <concatfilter>. Adds the content of file at the beginning
   or end of a file. Discussion started at
   http://marc.theaimsgroup.com/?l=ant-user&m=106366791228585&w=2
 
 * New task <import>
 
 * New task <macrodef>
 
 * New task <presetdef>
 
 * Ant libraries that can make use of namespaces to avoid name
   clashes of custom tasks
 
 
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
 
diff --git a/src/main/org/apache/tools/ant/Diagnostics.java b/src/main/org/apache/tools/ant/Diagnostics.java
index 7e3e3cc6b..93a4a5360 100644
--- a/src/main/org/apache/tools/ant/Diagnostics.java
+++ b/src/main/org/apache/tools/ant/Diagnostics.java
@@ -1,378 +1,378 @@
 /*
  * Copyright  2002-2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
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
 
 import org.apache.tools.ant.util.LoaderUtils;
 
 import javax.xml.parsers.SAXParserFactory;
 import javax.xml.parsers.SAXParser;
 import java.io.File;
 import java.io.FilenameFilter;
 import java.io.PrintStream;
 import java.io.InputStream;
 import java.io.IOException;
 import java.util.Enumeration;
 import java.util.Properties;
 import java.lang.reflect.Method;
 import java.lang.reflect.InvocationTargetException;
 
 /**
  * A little diagnostic helper that output some information that may help
  * in support. It should quickly give correct information about the
  * jar existing in ant.home/lib and the jar versions...
  *
  * @since Ant 1.5
  */
 public final class Diagnostics {
 
     private static final String TEST_CLASS
         = "org.apache.tools.ant.taskdefs.optional.Test";
 
     /** utility class */
     private Diagnostics() {
     }
 
     /**
      * Check if optional tasks are available. Not that it does not check
      * for implementation version. Use <tt>validateVersion()</tt> for this.
      * @return <tt>true</tt> if optional tasks are available.
      */
     public static boolean isOptionalAvailable() {
         try {
             Class.forName(TEST_CLASS);
         } catch (ClassNotFoundException e) {
             return false;
         }
         return true;
     }
 
     /**
      * Check if core and optional implementation version do match.
      * @throws BuildException if the implementation version of optional tasks
      * does not match the core implementation version.
      */
     public static void validateVersion() throws BuildException {
         try {
             Class optional
                 = Class.forName("org.apache.tools.ant.taskdefs.optional.Test");
             String coreVersion = getImplementationVersion(Main.class);
             String optionalVersion = getImplementationVersion(optional);
 
             if (coreVersion != null && !coreVersion.equals(optionalVersion)) {
                 throw new BuildException("Invalid implementation version "
                     + "between Ant core and Ant optional tasks.\n"
                     + " core    : " + coreVersion + "\n"
                     + " optional: " + optionalVersion);
             }
         } catch (ClassNotFoundException e) {
             // ignore
         }
     }
 
     /**
      * return the list of jar files existing in ANT_HOME/lib
      * and that must have been picked up by Ant script.
      * @return the list of jar files existing in ant.home/lib or
      * <tt>null</tt> if an error occurs.
      */
     public static File[] listLibraries() {
         String home = System.getProperty("ant.home");
         if (home == null) {
             return null;
         }
         File libDir = new File(home, "lib");
         FilenameFilter filter = new FilenameFilter() {
             public boolean accept(File dir, String name) {
                 return name.endsWith(".jar");
             }
         };
         // listFiles is JDK 1.2+ method...
         String[] filenames = libDir.list(filter);
         if (filenames == null) {
             return null;
         }
         File[] files = new File[filenames.length];
         for (int i = 0; i < filenames.length; i++) {
             files[i] = new File(libDir, filenames[i]);
         }
         return files;
     }
 
     /**
      * main entry point for command line
      * @param args command line arguments.
      */
     public static void main(String[] args) {
         doReport(System.out);
     }
 
 
     /**
      * Helper method to get the implementation version.
      * @param clazz the class to get the information from.
      * @return null if there is no package or implementation version.
      * '?.?' for JDK 1.0 or 1.1.
      */
     private static String getImplementationVersion(Class clazz) {
         try {
           // Package pkg = clazz.getPackage();
           Method method = Class.class.getMethod("getPackage", new Class[0]);
           Object pkg = method.invoke(clazz, null);
           if (pkg != null) {
               // pkg.getImplementationVersion();
               method = pkg.getClass().getMethod("getImplementationVersion", new Class[0]);
               Object version = method.invoke(pkg, null);
               return (String) version;
           }
         } catch (Exception e) {
           // JDK < 1.2 should land here because the methods above don't exist.
           return "?.?";
         }
         return null;
     }
 
     /**
      * what parser are we using.
      * @return the classname of the parser
      */
     private static String getXmlParserName() {
         SAXParser saxParser = getSAXParser();
         if (saxParser == null) {
             return "Could not create an XML Parser";
         }
 
         // check to what is in the classname
         String saxParserName = saxParser.getClass().getName();
         return saxParserName;
     }
 
     /**
      * Create a JAXP SAXParser
      * @return parser or null for trouble
      */
     private static SAXParser getSAXParser() {
         SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
         if (saxParserFactory == null) {
             return null;
         }
         SAXParser saxParser = null;
         try {
             saxParser = saxParserFactory.newSAXParser();
         } catch (Exception e) {
             // ignore
         }
         return saxParser;
     }
 
     /**
      * get the location of the parser
      * @return path or null for trouble in tracking it down
      */
 
     private static String getXMLParserLocation() {
         SAXParser saxParser = getSAXParser();
         if (saxParser == null) {
             return null;
         }
         String location = getClassLocation(saxParser.getClass());
         return location;
     }
 
     /**
      * get the location of a class. Stolen from axis/webapps/happyaxis.jsp
      * @param clazz
      * @return the jar file or path where a class was found, or null
      */
 
     private static String getClassLocation(Class clazz) {
         File f = LoaderUtils.getClassSource(clazz);
         return f == null ? null : f.getAbsolutePath();
     }
 
 
     /**
      * Print a report to the given stream.
      * @param out the stream to print the report to.
      */
     public static void doReport(PrintStream out) {
         out.println("------- Ant diagnostics report -------");
         out.println(Main.getAntVersion());
         out.println();
         out.println("-------------------------------------------");
         out.println(" Implementation Version (JDK1.2+ only)");
         out.println("-------------------------------------------");
         out.println("core tasks     : " + getImplementationVersion(Main.class));
 
         Class optional = null;
         try {
             optional = Class.forName(
                     "org.apache.tools.ant.taskdefs.optional.Test");
             out.println("optional tasks : "
                 + getImplementationVersion(optional));
         } catch (ClassNotFoundException e) {
             out.println("optional tasks : not available");
         }
 
         out.println();
         out.println("-------------------------------------------");
         out.println(" ANT_HOME/lib jar listing");
         out.println("-------------------------------------------");
         doReportLibraries(out);
 
         out.println();
         out.println("-------------------------------------------");
         out.println(" Tasks availability");
         out.println("-------------------------------------------");
         doReportTasksAvailability(out);
 
         out.println();
         out.println("-------------------------------------------");
         out.println(" org.apache.env.Which diagnostics");
         out.println("-------------------------------------------");
         doReportWhich(out);
 
 
         out.println();
         out.println("-------------------------------------------");
         out.println(" XML Parser information");
         out.println("-------------------------------------------");
         doReportParserInfo(out);
 
         out.println();
         out.println("-------------------------------------------");
         out.println(" System properties");
         out.println("-------------------------------------------");
         doReportSystemProperties(out);
 
         out.println();
     }
 
     /**
      * Report a listing of system properties existing in the current vm.
      * @param out the stream to print the properties to.
      */
     private static void doReportSystemProperties(PrintStream out) {
-        for (Enumeration keys = System.getProperties().keys();
+        for (Enumeration keys = System.getProperties().propertyNames();
             keys.hasMoreElements();) {
             String key = (String) keys.nextElement();
             out.println(key + " : " + System.getProperty(key));
         }
     }
 
 
     /**
      * Report the content of ANT_HOME/lib directory
      * @param out the stream to print the content to
      */
     private static void doReportLibraries(PrintStream out) {
         out.println("ant.home: " + System.getProperty("ant.home"));
         File[] libs = listLibraries();
         if (libs == null) {
             out.println("Unable to list libraries.");
             return;
         }
         for (int i = 0; i < libs.length; i++) {
             out.println(libs[i].getName()
                     + " (" + libs[i].length() + " bytes)");
         }
     }
 
 
     /**
      * Call org.apache.env.Which if available
      * @param out the stream to print the content to.
      */
     private static void doReportWhich(PrintStream out) {
         Throwable error = null;
         try {
             Class which = Class.forName("org.apache.env.Which");
             Method method
                 = which.getMethod("main", new Class[]{String[].class});
             method.invoke(null, new Object[]{new String[]{}});
         } catch (ClassNotFoundException e) {
             out.println("Not available.");
             out.println("Download it at http://xml.apache.org/commons/");
         } catch (InvocationTargetException e) {
             error = e.getTargetException() == null ? e : e.getTargetException();
         } catch (Throwable e) {
             error = e;
         }
         // report error if something weird happens...this is diagnostic.
         if (error != null) {
             out.println("Error while running org.apache.env.Which");
             error.printStackTrace();
         }
     }
 
     /**
      * Create a report about non-available tasks that are defined in the
      * mapping but could not be found via lookup. It might generally happen
      * because Ant requires multiple libraries to compile and one of them
      * was missing when compiling Ant.
      * @param out the stream to print the tasks report to
      * <tt>null</tt> for a missing stream (ie mapping).
      */
     private static void doReportTasksAvailability(PrintStream out) {
         InputStream is = Main.class.getResourceAsStream(
                 "/org/apache/tools/ant/taskdefs/defaults.properties");
         if (is == null) {
             out.println("None available");
         } else {
             Properties props = new Properties();
             try {
                 props.load(is);
                 for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
                     String key = (String) keys.nextElement();
                     String classname = props.getProperty(key);
                     try {
                         Class.forName(classname);
                         props.remove(key);
                     } catch (ClassNotFoundException e) {
                         out.println(key + " : Not Available");
                     } catch (NoClassDefFoundError e) {
                         String pkg = e.getMessage().replace('/', '.');
                         out.println(key + " : Missing dependency " + pkg);
                     } catch (Error e) {
                         out.println(key + " : Initialization error");
                     }
                 }
                 if (props.size() == 0) {
                     out.println("All defined tasks are available");
                 }
             } catch (IOException e) {
                 out.println(e.getMessage());
             }
         }
     }
 
     /**
      * tell the user about the XML parser
      * @param out
      */
     private static void doReportParserInfo(PrintStream out) {
         String parserName = getXmlParserName();
         String parserLocation = getXMLParserLocation();
         if (parserName == null) {
             parserName = "unknown";
         }
         if (parserLocation == null) {
             parserLocation = "unknown";
         }
         out.println("XML Parser : " + parserName);
         out.println("XML Parser Location: " + parserLocation);
     }
 }
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 03f72b4f2..f310f1cc5 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -1,1831 +1,1831 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
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
 import java.io.EOFException;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.Set;
 import java.util.HashSet;
 import org.apache.tools.ant.input.DefaultInputHandler;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.helper.DefaultExecutor;
 import org.apache.tools.ant.helper.KeepGoingExecutor;
 import org.apache.tools.ant.types.FilterSet;
 import org.apache.tools.ant.types.FilterSetCollection;
 import org.apache.tools.ant.types.Description;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.StringUtils;
 
 
 /**
  * Central representation of an Ant project. This class defines an
  * Ant project with all of its targets, tasks and various other
  * properties. It also provides the mechanism to kick off a build using
  * a particular target name.
  * <p>
  * This class also encapsulates methods which allow files to be referred
  * to using abstract path names which are translated to native system
  * file paths at runtime.
  *
  * @version $Revision$
  */
 
 public class Project {
     /** Message priority of "error". */
     public static final int MSG_ERR = 0;
     /** Message priority of "warning". */
     public static final int MSG_WARN = 1;
     /** Message priority of "information". */
     public static final int MSG_INFO = 2;
     /** Message priority of "verbose". */
     public static final int MSG_VERBOSE = 3;
     /** Message priority of "debug". */
     public static final int MSG_DEBUG = 4;
 
     /**
      * Constant for the "visiting" state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITING = "VISITING";
     /**
      * Constant for the "visited" state, used when
      * traversing a DFS of target dependencies.
      */
     private static final String VISITED = "VISITED";
 
     /**
      * The class name of the Ant class loader to use for
      * JDK 1.2 and above
      */
     private static final String ANTCLASSLOADER_JDK12
         = "org.apache.tools.ant.loader.AntClassLoader2";
 
     /**
      * Version constant for Java 1.0
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_0} instead.
      */
     public static final String JAVA_1_0 = JavaEnvUtils.JAVA_1_0;
     /**
      * Version constant for Java 1.1
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_1} instead.
      */
     public static final String JAVA_1_1 = JavaEnvUtils.JAVA_1_1;
     /**
      * Version constant for Java 1.2
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_2} instead.
      */
     public static final String JAVA_1_2 = JavaEnvUtils.JAVA_1_2;
     /**
      * Version constant for Java 1.3
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_3} instead.
      */
     public static final String JAVA_1_3 = JavaEnvUtils.JAVA_1_3;
     /**
      * Version constant for Java 1.4
      *
      * @deprecated Use {@link JavaEnvUtils#JAVA_1_4} instead.
      */
     public static final String JAVA_1_4 = JavaEnvUtils.JAVA_1_4;
 
     /** Default filter start token. */
     public static final String TOKEN_START = FilterSet.DEFAULT_TOKEN_START;
     /** Default filter end token. */
     public static final String TOKEN_END = FilterSet.DEFAULT_TOKEN_END;
 
     /** Name of this project. */
     private String name;
     /** Description for this project (if any). */
     private String description;
 
 
     /** Map of references within the project (paths etc) (String to Object). */
     private Hashtable references = new AntRefTable(this);
 
     /** Name of the project's default target. */
     private String defaultTarget;
 
     /** Map from target names to targets (String to Target). */
     private Hashtable targets = new Hashtable();
     /** Set of global filters. */
     private FilterSet globalFilterSet = new FilterSet();
     {
         // Initialize the globalFileSet's project
         globalFilterSet.setProject(this);
     }
 
     /**
      * Wrapper around globalFilterSet. This collection only ever
      * contains one FilterSet, but the wrapper is needed in order to
      * make it easier to use the FileUtils interface.
      */
     private FilterSetCollection globalFilters
         = new FilterSetCollection(globalFilterSet);
 
     /** Project base directory. */
     private File baseDir;
 
     /** List of listeners to notify of build events. */
     private Vector listeners = new Vector();
 
     /**
      * The Ant core classloader - may be <code>null</code> if using
      * parent classloader.
      */
     private ClassLoader coreLoader = null;
 
     /** Records the latest task to be executed on a thread (Thread to Task). */
     private Hashtable threadTasks = new Hashtable();
 
     /** Records the latest task to be executed on a thread Group. */
     private Hashtable threadGroupTasks = new Hashtable();
 
     /**
      * Called to handle any input requests.
      */
     private InputHandler inputHandler = null;
 
     /**
      * The default input stream used to read any input
      */
     private InputStream defaultInputStream = null;
 
     /**
      * Keep going flag
      */
     private boolean keepGoingMode = false;
 
     /**
      * Sets the input handler
      *
      * @param handler the InputHandler instance to use for gathering input.
      */
     public void setInputHandler(InputHandler handler) {
         inputHandler = handler;
     }
 
     /**
      * Set the default System input stream. Normally this stream is set to
      * System.in. This inputStream is used when no task input redirection is
      * being performed.
      *
      * @param defaultInputStream the default input stream to use when input
      *        is requested.
      * @since Ant 1.6
      */
     public void setDefaultInputStream(InputStream defaultInputStream) {
         this.defaultInputStream = defaultInputStream;
     }
 
     /**
      * Get this project's input stream
      *
      * @return the InputStream instance in use by this Project instance to
      * read input
      */
     public InputStream getDefaultInputStream() {
         return defaultInputStream;
     }
 
     /**
      * Retrieves the current input handler.
      *
      * @return the InputHandler instance currently in place for the project
      *         instance.
      */
     public InputHandler getInputHandler() {
         return inputHandler;
     }
 
     /** Instance of a utility class to use for file operations. */
     private FileUtils fileUtils;
 
     /**
      * Flag which catches Listeners which try to use System.out or System.err
      */
     private boolean loggingMessage = false;
 
     /**
      * Creates a new Ant project.
      */
     public Project() {
         fileUtils = FileUtils.newFileUtils();
         inputHandler = new DefaultInputHandler();
     }
 
     /**
      * inits a sub project - used by taskdefs.Ant
      * @param subProject the subproject to initialize
      */
     public void initSubProject(Project subProject) {
         ComponentHelper.getComponentHelper(subProject)
             .initSubProject(ComponentHelper.getComponentHelper(this));
         subProject.setKeepGoingMode(this.isKeepGoingMode());
     }
 
     /**
      * Initialises the project.
      *
      * This involves setting the default task definitions and loading the
      * system properties.
      *
      * @exception BuildException if the default task list cannot be loaded
      */
     public void init() throws BuildException {
         setJavaVersionProperty();
 
         ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
 
         setSystemProperties();
     }
 
     /**
      * Factory method to create a class loader for loading classes
      *
      * @return an appropriate classloader
      */
     private AntClassLoader createClassLoader() {
         AntClassLoader loader = null;
         try {
             // 1.2+ - create advanced helper dynamically
             Class loaderClass
                     = Class.forName(ANTCLASSLOADER_JDK12);
             loader = (AntClassLoader) loaderClass.newInstance();
         } catch (Exception e) {
             log("Unable to create Class Loader: "
                     + e.getMessage(), Project.MSG_DEBUG);
         }
 
         if (loader == null) {
             loader = new AntClassLoader();
         }
 
         loader.setProject(this);
         return loader;
     }
 
     /**
      * Factory method to create a class loader for loading classes from
      * a given path
      *
      * @param path the path from which classes are to be loaded.
      *
      * @return an appropriate classloader
      */
     public AntClassLoader createClassLoader(Path path) {
         AntClassLoader loader = createClassLoader();
         loader.setClassPath(path);
         return loader;
     }
 
     /**
      * Sets the core classloader for the project. If a <code>null</code>
      * classloader is specified, the parent classloader should be used.
      *
      * @param coreLoader The classloader to use for the project.
      *                   May be <code>null</code>.
      */
     public void setCoreLoader(ClassLoader coreLoader) {
         this.coreLoader = coreLoader;
     }
 
     /**
      * Returns the core classloader to use for this project.
      * This may be <code>null</code>, indicating that
      * the parent classloader should be used.
      *
      * @return the core classloader to use for this project.
      *
      */
     public ClassLoader getCoreLoader() {
         return coreLoader;
     }
 
     /**
      * Adds a build listener to the list. This listener will
      * be notified of build events for this project.
      *
      * @param listener The listener to add to the list.
      *                 Must not be <code>null</code>.
      */
     public synchronized void addBuildListener(BuildListener listener) {
         // create a new Vector to avoid ConcurrentModificationExc when
         // the listeners get added/removed while we are in fire
         Vector newListeners = getBuildListeners();
         newListeners.addElement(listener);
         listeners = newListeners;
     }
 
     /**
      * Removes a build listener from the list. This listener
      * will no longer be notified of build events for this project.
      *
      * @param listener The listener to remove from the list.
      *                 Should not be <code>null</code>.
      */
     public synchronized void removeBuildListener(BuildListener listener) {
         // create a new Vector to avoid ConcurrentModificationExc when
         // the listeners get added/removed while we are in fire
         Vector newListeners = getBuildListeners();
         newListeners.removeElement(listener);
         listeners = newListeners;
     }
 
     /**
      * Returns a copy of the list of build listeners for the project.
      *
      * @return a list of build listeners for the project
      */
     public Vector getBuildListeners() {
         return (Vector) listeners.clone();
     }
 
     /**
      * Writes a message to the log with the default log level
      * of MSG_INFO
      * @param message The text to log. Should not be <code>null</code>.
      */
 
     public void log(String message) {
         log(message, MSG_INFO);
     }
 
     /**
      * Writes a project level message to the log with the given log level.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(String message, int msgLevel) {
         fireMessageLogged(this, message, msgLevel);
     }
 
     /**
      * Writes a task level message to the log with the given log level.
      * @param task The task to use in the log. Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(Task task, String message, int msgLevel) {
         fireMessageLogged(task, message, msgLevel);
     }
 
     /**
      * Writes a target level message to the log with the given log level.
      * @param target The target to use in the log.
      *               Must not be <code>null</code>.
      * @param message The text to log. Should not be <code>null</code>.
      * @param msgLevel The priority level to log at.
      */
     public void log(Target target, String message, int msgLevel) {
         fireMessageLogged(target, message, msgLevel);
     }
 
     /**
      * Returns the set of global filters.
      *
      * @return the set of global filters
      */
     public FilterSet getGlobalFilterSet() {
         return globalFilterSet;
     }
 
     /**
      * Sets a property. Any existing property of the same name
      * is overwritten, unless it is a user property.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      */
     public void setProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).
                 setProperty(null, name, value, true);
     }
 
     /**
      * Sets a property if no value currently exists. If the property
      * exists already, a message is logged and the method returns with
      * no other effect.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @since 1.5
      */
     public void setNewProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setNewProperty(null, name,
                                                               value);
     }
 
     /**
      * Sets a user property, which cannot be overwritten by
      * set/unset property calls. Any previous value is overwritten.
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @see #setProperty(String,String)
      */
     public void setUserProperty(String name, String value) {
         PropertyHelper.getPropertyHelper(this).setUserProperty(null, name,
                                                                value);
     }
 
     /**
      * Sets a user property, which cannot be overwritten by set/unset
      * property calls. Any previous value is overwritten. Also marks
      * these properties as properties that have not come from the
      * command line.
      *
      * @param name The name of property to set.
      *             Must not be <code>null</code>.
      * @param value The new value of the property.
      *              Must not be <code>null</code>.
      * @see #setProperty(String,String)
      */
     public void setInheritedProperty(String name, String value) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.setInheritedProperty(null, name, value);
     }
 
     /**
      * Sets a property unless it is already defined as a user property
      * (in which case the method returns silently).
      *
      * @param name The name of the property.
      *             Must not be <code>null</code>.
      * @param value The property value. Must not be <code>null</code>.
      */
     private void setPropertyInternal(String name, String value) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.setProperty(null, name, value, false);
     }
 
     /**
      * Returns the value of a property, if it is set.
      *
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
     public String getProperty(String name) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getProperty(null, name);
     }
 
     /**
      * Replaces ${} style constructions in the given value with the
      * string value of the corresponding data types.
      *
      * @param value The string to be scanned for property references.
      *              May be <code>null</code>.
      *
      * @return the given string with embedded property names replaced
      *         by values, or <code>null</code> if the given string is
      *         <code>null</code>.
      *
      * @exception BuildException if the given value has an unclosed
      *                           property name, e.g. <code>${xxx</code>
      */
     public String replaceProperties(String value)
         throws BuildException {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.replaceProperties(null, value, null);
     }
 
     /**
      * Returns the value of a user property, if it is set.
      *
      * @param name The name of the property.
      *             May be <code>null</code>, in which case
      *             the return value is also <code>null</code>.
      * @return the property value, or <code>null</code> for no match
      *         or if a <code>null</code> name is provided.
      */
      public String getUserProperty(String name) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return (String) ph.getUserProperty(null, name);
     }
 
     /**
      * Returns a copy of the properties table.
      * @return a hashtable containing all properties
      *         (including user properties).
      */
     public Hashtable getProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.getProperties();
     }
 
     /**
      * Returns a copy of the user property hashtable
      * @return a hashtable containing just the user properties
      */
     public Hashtable getUserProperties() {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         return ph.getUserProperties();
     }
 
     /**
      * Copies all user properties that have been set on the command
      * line or a GUI tool from this instance to the Project instance
      * given as the argument.
      *
      * <p>To copy all "user" properties, you will also have to call
      * {@link #copyInheritedProperties copyInheritedProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyUserProperties(Project other) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.copyUserProperties(other);
     }
 
     /**
      * Copies all user properties that have not been set on the
      * command line or a GUI tool from this instance to the Project
      * instance given as the argument.
      *
      * <p>To copy all "user" properties, you will also have to call
      * {@link #copyUserProperties copyUserProperties}.</p>
      *
      * @param other the project to copy the properties to.  Must not be null.
      *
      * @since Ant 1.5
      */
     public void copyInheritedProperties(Project other) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(this);
         ph.copyInheritedProperties(other);
     }
 
     /**
      * Sets the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      *
      * @deprecated use setDefault
      * @see #setDefault(String)
      */
     public void setDefaultTarget(String defaultTarget) {
         this.defaultTarget = defaultTarget;
     }
 
     /**
      * Returns the name of the default target of the project.
      * @return name of the default target or
      *         <code>null</code> if no default has been set.
      */
     public String getDefaultTarget() {
         return defaultTarget;
     }
 
     /**
      * Sets the default target of the project.
      *
      * @param defaultTarget The name of the default target for this project.
      *                      May be <code>null</code>, indicating that there is
      *                      no default target.
      */
     public void setDefault(String defaultTarget) {
         this.defaultTarget = defaultTarget;
     }
 
     /**
      * Sets the name of the project, also setting the user
      * property <code>ant.project.name</code>.
      *
      * @param name The name of the project.
      *             Must not be <code>null</code>.
      */
     public void setName(String name) {
         setUserProperty("ant.project.name",  name);
         this.name = name;
     }
 
     /**
      * Returns the project name, if one has been set.
      *
      * @return the project name, or <code>null</code> if it hasn't been set.
      */
     public String getName() {
         return name;
     }
 
     /**
      * Sets the project description.
      *
      * @param description The description of the project.
      *                    May be <code>null</code>.
      */
     public void setDescription(String description) {
         this.description = description;
     }
 
     /**
      * Returns the project description, if one has been set.
      *
      * @return the project description, or <code>null</code> if it hasn't
      *         been set.
      */
     public String getDescription() {
         if (description == null) {
             description = Description.getDescription(this);
         }
 
         return description;
     }
 
     /**
      * Adds a filter to the set of global filters.
      *
      * @param token The token to filter.
      *              Must not be <code>null</code>.
      * @param value The replacement value.
      *              Must not be <code>null</code>.
      * @deprecated Use getGlobalFilterSet().addFilter(token,value)
      *
      * @see #getGlobalFilterSet()
      * @see FilterSet#addFilter(String,String)
      */
     public void addFilter(String token, String value) {
         if (token == null) {
             return;
         }
 
         globalFilterSet.addFilter(new FilterSet.Filter(token, value));
     }
 
     /**
      * Returns a hashtable of global filters, mapping tokens to values.
      *
      * @return a hashtable of global filters, mapping tokens to values
      *         (String to String).
      *
      * @deprecated Use getGlobalFilterSet().getFilterHash()
      *
      * @see #getGlobalFilterSet()
      * @see FilterSet#getFilterHash()
      */
     public Hashtable getFilters() {
         // we need to build the hashtable dynamically
         return globalFilterSet.getFilterHash();
     }
 
     /**
      * Sets the base directory for the project, checking that
      * the given filename exists and is a directory.
      *
      * @param baseD The project base directory.
      *              Must not be <code>null</code>.
      *
      * @exception BuildException if the directory if invalid
      */
     public void setBasedir(String baseD) throws BuildException {
         setBaseDir(new File(baseD));
     }
 
     /**
      * Sets the base directory for the project, checking that
      * the given file exists and is a directory.
      *
      * @param baseDir The project base directory.
      *                Must not be <code>null</code>.
      * @exception BuildException if the specified file doesn't exist or
      *                           isn't a directory
      */
     public void setBaseDir(File baseDir) throws BuildException {
         baseDir = fileUtils.normalize(baseDir.getAbsolutePath());
         if (!baseDir.exists()) {
             throw new BuildException("Basedir " + baseDir.getAbsolutePath()
                 + " does not exist");
         }
         if (!baseDir.isDirectory()) {
             throw new BuildException("Basedir " + baseDir.getAbsolutePath()
                 + " is not a directory");
         }
         this.baseDir = baseDir;
         setPropertyInternal("basedir", this.baseDir.getPath());
         String msg = "Project base dir set to: " + this.baseDir;
          log(msg, MSG_VERBOSE);
     }
 
     /**
      * Returns the base directory of the project as a file object.
      *
      * @return the project base directory, or <code>null</code> if the
      *         base directory has not been successfully set to a valid value.
      */
     public File getBaseDir() {
         if (baseDir == null) {
             try {
                 setBasedir(".");
             } catch (BuildException ex) {
                 ex.printStackTrace();
             }
         }
         return baseDir;
     }
 
     /**
      * Sets "keep-going" mode. In this mode ANT will try to execute
      * as many targets as possible. All targets that do not depend
      * on failed target(s) will be executed.  If the keepGoing settor/getter
      * methods are used in conjunction with the <code>ant.executor.class</code>
      * property, they will have no effect.
      * @param keepGoingMode "keep-going" mode
      * @since Ant 1.6
      */
     public void setKeepGoingMode(boolean keepGoingMode) {
         this.keepGoingMode = keepGoingMode;
     }
 
     /**
      * Returns the keep-going mode.  If the keepGoing settor/getter
      * methods are used in conjunction with the <code>ant.executor.class</code>
      * property, they will have no effect.
      * @return "keep-going" mode
      * @since Ant 1.6
      */
     public boolean isKeepGoingMode() {
         return this.keepGoingMode;
     }
 
     /**
      * Returns the version of Java this class is running under.
      * @return the version of Java as a String, e.g. "1.1"
      * @see org.apache.tools.ant.util.JavaEnvUtils#getJavaVersion
      * @deprecated use org.apache.tools.ant.util.JavaEnvUtils instead
      */
     public static String getJavaVersion() {
         return JavaEnvUtils.getJavaVersion();
     }
 
     /**
      * Sets the <code>ant.java.version</code> property and tests for
      * unsupported JVM versions. If the version is supported,
      * verbose log messages are generated to record the Java version
      * and operating system name.
      *
      * @exception BuildException if this Java version is not supported
      *
      * @see org.apache.tools.ant.util.JavaEnvUtils#getJavaVersion
      */
     public void setJavaVersionProperty() throws BuildException {
         String javaVersion = JavaEnvUtils.getJavaVersion();
         setPropertyInternal("ant.java.version", javaVersion);
 
         // sanity check
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_0)) {
             throw new BuildException("Ant cannot work on Java 1.0");
         }
 
         log("Detected Java version: " + javaVersion + " in: "
             + System.getProperty("java.home"), MSG_VERBOSE);
 
         log("Detected OS: " + System.getProperty("os.name"), MSG_VERBOSE);
     }
 
     /**
      * Adds all system properties which aren't already defined as
      * user properties to the project properties.
      */
     public void setSystemProperties() {
         Properties systemP = System.getProperties();
-        Enumeration e = systemP.keys();
+        Enumeration e = systemP.propertyNames();
         while (e.hasMoreElements()) {
             Object name = e.nextElement();
             String value = systemP.get(name).toString();
             this.setPropertyInternal(name.toString(), value);
         }
     }
 
     /**
      * Adds a new task definition to the project.
      * Attempting to override an existing definition with an
      * equivalent one (i.e. with the same classname) results in
      * a verbose log message. Attempting to override an existing definition
      * with a different one results in a warning log message and
      * invalidates any tasks which have already been created with the
      * old definition.
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
     public void addTaskDefinition(String taskName, Class taskClass)
          throws BuildException {
         ComponentHelper.getComponentHelper(this).addTaskDefinition(taskName,
                 taskClass);
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
     public void checkTaskClass(final Class taskClass) throws BuildException {
         ComponentHelper.getComponentHelper(this).checkTaskClass(taskClass);
 
         if (!Modifier.isPublic(taskClass.getModifiers())) {
             final String message = taskClass + " is not public";
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         if (Modifier.isAbstract(taskClass.getModifiers())) {
             final String message = taskClass + " is abstract";
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         }
         try {
             taskClass.getConstructor(null);
             // don't have to check for public, since
             // getConstructor finds public constructors only.
         } catch (NoSuchMethodException e) {
             final String message = "No public no-arg constructor in "
                 + taskClass;
             log(message, Project.MSG_ERR);
             throw new BuildException(message);
         } catch (LinkageError e) {
             String message = "Could not load " + taskClass + ": " + e;
             log(message, Project.MSG_ERR);
             throw new BuildException(message, e);
         }
         if (!Task.class.isAssignableFrom(taskClass)) {
             TaskAdapter.checkTaskClass(taskClass, this);
         }
     }
 
     /**
      * Returns the current task definition hashtable. The returned hashtable is
      * "live" and so should not be modified.
      *
      * @return a map of from task name to implementing class
      *         (String to Class).
      */
     public Hashtable getTaskDefinitions() {
         return ComponentHelper.getComponentHelper(this).getTaskDefinitions();
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
     public void addDataTypeDefinition(String typeName, Class typeClass) {
         ComponentHelper.getComponentHelper(this).addDataTypeDefinition(typeName,
                 typeClass);
     }
 
     /**
      * Returns the current datatype definition hashtable. The returned
      * hashtable is "live" and so should not be modified.
      *
      * @return a map of from datatype name to implementing class
      *         (String to Class).
      */
     public Hashtable getDataTypeDefinitions() {
         return ComponentHelper.getComponentHelper(this).getDataTypeDefinitions();
     }
 
     /**
      * Adds a <em>new</em> target to the project.
      *
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project
      *
      * @see Project#addOrReplaceTarget
      */
     public void addTarget(Target target) throws BuildException {
         addTarget(target.getName(), target);
     }
 
     /**
      * Adds a <em>new</em> target to the project.
      *
      * @param targetName The name to use for the target.
      *             Must not be <code>null</code>.
      * @param target The target to be added to the project.
      *               Must not be <code>null</code>.
      *
      * @exception BuildException if the target already exists in the project
      *
      * @see Project#addOrReplaceTarget
      */
      public void addTarget(String targetName, Target target)
          throws BuildException {
          if (targets.get(targetName) != null) {
              throw new BuildException("Duplicate target: `" + targetName + "'");
          }
          addOrReplaceTarget(targetName, target);
      }
 
     /**
      * Adds a target to the project, or replaces one with the same
      * name.
      *
      * @param target The target to be added or replaced in the project.
      *               Must not be <code>null</code>.
      */
     public void addOrReplaceTarget(Target target) {
         addOrReplaceTarget(target.getName(), target);
     }
 
     /**
      * Adds a target to the project, or replaces one with the same
      * name.
      *
      * @param targetName The name to use for the target.
      *                   Must not be <code>null</code>.
      * @param target The target to be added or replaced in the project.
      *               Must not be <code>null</code>.
      */
     public void addOrReplaceTarget(String targetName, Target target) {
         String msg = " +Target: " + targetName;
         log(msg, MSG_DEBUG);
         target.setProject(this);
         targets.put(targetName, target);
     }
 
     /**
      * Returns the hashtable of targets. The returned hashtable
      * is "live" and so should not be modified.
      * @return a map from name to target (String to Target).
      */
     public Hashtable getTargets() {
         return targets;
     }
 
     /**
      * Creates a new instance of a task, adding it to a list of
      * created tasks for later invalidation. This causes all tasks
      * to be remembered until the containing project is removed
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
         return ComponentHelper.getComponentHelper(this).createTask(taskType);
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
         return ComponentHelper.getComponentHelper(this).createDataType(typeName);
     }
 
     /**
      * Execute the specified sequence of targets, and the targets
      * they depend on.
      *
      * @param targetNames A vector of target name strings to execute.
      *                    Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed
      */
     public void executeTargets(Vector targetNames) throws BuildException {
 
         Object o = getReference("ant.executor");
         if (o == null) {
             String classname = getProperty("ant.executor.class");
             if (classname == null) {
                 classname = (keepGoingMode)
                     ? KeepGoingExecutor.class.getName()
                     : DefaultExecutor.class.getName();
             }
             log("Attempting to create object of type " + classname, MSG_DEBUG);
             try {
                 o = Class.forName(classname, true, coreLoader).newInstance();
             } catch (ClassNotFoundException seaEnEfEx) {
                 //try the current classloader
                 try {
                     o = Class.forName(classname).newInstance();
                 } catch (Exception ex) {
                     log(ex.toString(), MSG_ERR);
                 }
             } catch (Exception ex) {
                 log(ex.toString(), MSG_ERR);
             }
             if (o != null) {
                 addReference("ant.executor", o);
             }
         }
 
         if (o == null) {
             throw new BuildException("Unable to obtain a Target Executor instance.");
         } else {
             String[] targetNameArray = (String[])(targetNames.toArray(
                 new String[targetNames.size()]));
             ((Executor)o).executeTargets(this, targetNameArray);
         }
     }
 
     /**
      * Demultiplexes output so that each task receives the appropriate
      * messages. If the current thread is not currently executing a task,
      * the message is logged directly.
      *
      * @param output Message to handle. Should not be <code>null</code>.
      * @param isWarning Whether the text represents an warning (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxOutput(String output, boolean isWarning) {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             log(output, isWarning ? MSG_WARN : MSG_INFO);
         } else {
             if (isWarning) {
                 task.handleErrorOutput(output);
             } else {
                 task.handleOutput(output);
             }
         }
     }
 
     /**
      * Read data from the default input stream. If no default has been
      * specified, System.in is used.
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
      * @since Ant 1.6
      */
     public int defaultInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (defaultInputStream != null) {
             System.out.flush();
             return defaultInputStream.read(buffer, offset, length);
         } else {
             throw new EOFException("No input provided for project");
         }
     }
 
     /**
      * Demux an input request to the correct task.
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
      * @since Ant 1.6
      */
     public int demuxInput(byte[] buffer, int offset, int length)
         throws IOException {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             return defaultInput(buffer, offset, length);
         } else {
             return task.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Demultiplexes flush operation so that each task receives the appropriate
      * messages. If the current thread is not currently executing a task,
      * the message is logged directly.
      *
      * @since Ant 1.5.2
      *
      * @param output Message to handle. Should not be <code>null</code>.
      * @param isError Whether the text represents an error (<code>true</code>)
      *        or information (<code>false</code>).
      */
     public void demuxFlush(String output, boolean isError) {
         Task task = getThreadTask(Thread.currentThread());
         if (task == null) {
             fireMessageLogged(this, output, isError ? MSG_ERR : MSG_INFO);
         } else {
             if (isError) {
                 task.handleErrorFlush(output);
             } else {
                 task.handleFlush(output);
             }
         }
     }
 
 
 
     /**
      * Executes the specified target and any targets it depends on.
      *
      * @param targetName The name of the target to execute.
      *                   Must not be <code>null</code>.
      *
      * @exception BuildException if the build failed
      */
     public void executeTarget(String targetName) throws BuildException {
 
         // sanity check ourselves, if we've been asked to build nothing
         // then we should complain
 
         if (targetName == null) {
             String msg = "No target specified";
             throw new BuildException(msg);
         }
 
         // Sort and run the dependency tree.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
         executeSortedTargets(topoSort(targetName, targets, false));
     }
 
     /**
      * Executes a <CODE>Vector</CODE> of sorted targets.
      * @param sortedTargets   the aforementioned <CODE>Vector</CODE>.
      */
     public void executeSortedTargets(Vector sortedTargets)
         throws BuildException {
         Set succeededTargets = new HashSet();
         BuildException buildException = null; // first build exception
         for (Enumeration iter = sortedTargets.elements();
              iter.hasMoreElements();) {
             Target curtarget = (Target) iter.nextElement();
             boolean canExecute = true;
             for (Enumeration depIter = curtarget.getDependencies();
                  depIter.hasMoreElements();) {
                 String dependencyName = ((String) depIter.nextElement());
                 if (!succeededTargets.contains(dependencyName)) {
                     canExecute = false;
                     log(curtarget,
                         "Cannot execute '" + curtarget.getName() + "' - '"
                         + dependencyName + "' failed or was not executed.",
                         MSG_ERR);
                     break;
                 }
             }
             if (canExecute) {
                 Throwable thrownException = null;
                 try {
                     curtarget.performTasks();
                     succeededTargets.add(curtarget.getName());
                 } catch (RuntimeException ex) {
                     if (!(keepGoingMode)) {
                         throw ex; // throw further
                     }
                     thrownException = ex;
                 } catch (Throwable ex) {
                     if (!(keepGoingMode)) {
                         throw new BuildException(ex);
                     }
                     thrownException = ex;
                 }
                 if (thrownException != null) {
                     if (thrownException instanceof BuildException) {
                         log(curtarget,
                             "Target '" + curtarget.getName()
                             + "' failed with message '"
                             + thrownException.getMessage() + "'.", MSG_ERR);
                         // only the first build exception is reported
                         if (buildException == null) {
                             buildException = (BuildException) thrownException;
                         }
                     } else {
                         log(curtarget,
                             "Target '" + curtarget.getName()
                             + "' failed with message '"
                             + thrownException.getMessage() + "'.", MSG_ERR);
                         thrownException.printStackTrace(System.err);
                         if (buildException == null) {
                             buildException =
                                 new BuildException(thrownException);
                         }
                     }
                 }
             }
         }
         if (buildException != null) {
             throw buildException;
         }
     }
 
     /**
      * Returns the canonical form of a filename.
      * <p>
      * If the specified file name is relative it is resolved
      * with respect to the given root directory.
      *
      * @param fileName The name of the file to resolve.
      *                 Must not be <code>null</code>.
      *
      * @param rootDir  The directory to resolve relative file names with
      *                 respect to. May be <code>null</code>, in which case
      *                 the current directory is used.
      *
      * @return the resolved File.
      *
      * @deprecated
      */
     public File resolveFile(String fileName, File rootDir) {
         return fileUtils.resolveFile(rootDir, fileName);
     }
 
     /**
      * Returns the canonical form of a filename.
      * <p>
      * If the specified file name is relative it is resolved
      * with respect to the project's base directory.
      *
      * @param fileName The name of the file to resolve.
      *                 Must not be <code>null</code>.
      *
      * @return the resolved File.
      *
      */
     public File resolveFile(String fileName) {
         return fileUtils.resolveFile(baseDir, fileName);
     }
 
     /**
      * Translates a path into its native (platform specific) format.
      * <p>
      * This method uses PathTokenizer to separate the input path
      * into its components. This handles DOS style paths in a relatively
      * sensible way. The file separators are then converted to their platform
      * specific versions.
      *
      * @param toProcess The path to be translated.
      *                  May be <code>null</code>.
      *
      * @return the native version of the specified path or
      *         an empty string if the path is <code>null</code> or empty.
      *
      * @see PathTokenizer
      */
     public static String translatePath(String toProcess) {
         if (toProcess == null || toProcess.length() == 0) {
             return "";
         }
 
         StringBuffer path = new StringBuffer(toProcess.length() + 50);
         PathTokenizer tokenizer = new PathTokenizer(toProcess);
         while (tokenizer.hasMoreTokens()) {
             String pathComponent = tokenizer.nextToken();
             pathComponent = pathComponent.replace('/', File.separatorChar);
             pathComponent = pathComponent.replace('\\', File.separatorChar);
             if (path.length() != 0) {
                 path.append(File.pathSeparatorChar);
             }
             path.append(pathComponent);
         }
 
         return path.toString();
     }
 
     /**
      * Convenience method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile)
           throws IOException {
         fileUtils.copyFile(sourceFile, destFile);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination
      * specifying if token filtering should be used.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used, if
      * source files may overwrite newer destination files, and if the
      * last modified time of the resulting file should be set to
      * that of the source file.
      *
      * @param sourceFile Name of file to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile Name of file to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(String sourceFile, String destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite, preserveLastModified);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination.
      * No filtering is performed.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile) throws IOException {
         fileUtils.copyFile(sourceFile, destFile);
     }
 
     /**
      * Convenience method to copy a file from a source to a destination
      * specifying if token filtering should be used.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      *
      * @exception IOException if the copying fails
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used and if
      * source files may overwrite newer destination files.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      *
      * @exception IOException if the file cannot be copied.
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite) throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite);
     }
 
     /**
      * Convenience method to copy a file from a source to a
      * destination specifying if token filtering should be used, if
      * source files may overwrite newer destination files, and if the
      * last modified time of the resulting file should be set to
      * that of the source file.
      *
      * @param sourceFile File to copy from.
      *                   Must not be <code>null</code>.
      * @param destFile File to copy to.
      *                 Must not be <code>null</code>.
      * @param filtering Whether or not token filtering should be used during
      *                  the copy.
      * @param overwrite Whether or not the destination file should be
      *                  overwritten if it already exists.
      * @param preserveLastModified Whether or not the last modified time of
      *                             the resulting file should be set to that
      *                             of the source file.
      *
      * @exception IOException if the file cannot be copied.
      *
      * @deprecated
      */
     public void copyFile(File sourceFile, File destFile, boolean filtering,
                          boolean overwrite, boolean preserveLastModified)
         throws IOException {
         fileUtils.copyFile(sourceFile, destFile,
             filtering ? globalFilters : null, overwrite, preserveLastModified);
     }
 
     /**
      * Calls File.setLastModified(long time) on Java above 1.1, and logs
      * a warning on Java 1.1.
      *
      * @param file The file to set the last modified time on.
      *             Must not be <code>null</code>.
      *
      * @param time the required modification time.
      *
      * @deprecated
      *
      * @exception BuildException if the last modified time cannot be set
      *                           despite running on a platform with a version
      *                           above 1.1.
      */
     public void setFileLastModified(File file, long time)
          throws BuildException {
         fileUtils.setFileLastModified(file, time);
         log("Setting modification time for " + file, MSG_VERBOSE);
     }
 
     /**
      * Returns the boolean equivalent of a string, which is considered
      * <code>true</code> if either <code>"on"</code>, <code>"true"</code>,
      * or <code>"yes"</code> is found, ignoring case.
      *
      * @param s The string to convert to a boolean value.
      *
      * @return <code>true</code> if the given string is <code>"on"</code>,
      *         <code>"true"</code> or <code>"yes"</code>, or
      *         <code>false</code> otherwise.
      */
     public static boolean toBoolean(String s) {
         return ("on".equalsIgnoreCase(s)
                 || "true".equalsIgnoreCase(s)
                 || "yes".equalsIgnoreCase(s));
     }
 
     /**
      * Topologically sorts a set of targets.  Equivalent to calling
      * <CODE>topoSort(new String[] {root}, targets, true)</CODE>.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targets A Hashtable mapping names to Targets.
      *                Must not be <code>null</code>.
      * @return a Vector of ALL Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      */
     public final Vector topoSort(String root, Hashtable targets)
         throws BuildException {
         return topoSort(new String[] {root}, targets, true);
     }
 
     /**
      * Topologically sorts a set of targets.  Equivalent to calling
      * <CODE>topoSort(new String[] {root}, targets, returnAll)</CODE>.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targets A Hashtable mapping names to Targets.
      *                Must not be <code>null</code>.
      * @param returnAll <CODE>boolean</CODE> indicating whether to return all
      *                  targets, or the execution sequence only.
      * @return a Vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      * @since Ant 1.6.3
      */
     public final Vector topoSort(String root, Hashtable targets,
                                  boolean returnAll) throws BuildException {
         return topoSort(new String[] {root}, targets, returnAll);
     }
 
     /**
      * Topologically sorts a set of targets.
      *
      * @param root <CODE>String[]</CODE> containing the names of the root targets.
      *             The sort is created in such a way that the ordered sequence of
      *             Targets is the minimum possible such sequence to the specified
      *             root targets.
      *             Must not be <code>null</code>.
      * @param targets A map of names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @param returnAll <CODE>boolean</CODE> indicating whether to return all
      *                  targets, or the execution sequence only.
      * @return a Vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      * @since Ant 1.6.3
      */
     public final Vector topoSort(String[] root, Hashtable targets,
                                  boolean returnAll) throws BuildException {
         Vector ret = new Vector();
         Hashtable state = new Hashtable();
         Stack visiting = new Stack();
 
         // We first run a DFS based sort using each root as a starting node.
         // This creates the minimum sequence of Targets to the root node(s).
         // We then do a sort on any remaining unVISITED targets.
         // This is unnecessary for doing our build, but it catches
         // circular dependencies or missing Targets on the entire
         // dependency tree, not just on the Targets that depend on the
         // build Target.
 
         for (int i = 0; i < root.length; i++) {
             String st = (String)(state.get(root[i]));
             if (st == null) {
                 tsort(root[i], targets, state, visiting, ret);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + root[i]);
             }
         }
         StringBuffer buf = new StringBuffer("Build sequence for target(s)");
 
         for (int j = 0; j < root.length; j++) {
             buf.append((j == 0) ? " `" : ", `").append(root[j]).append('\'');
         }
         buf.append(" is " + ret);
         log(buf.toString(), MSG_VERBOSE);
 
         Vector complete = (returnAll) ? ret : new Vector(ret);
         for (Enumeration en = targets.keys(); en.hasMoreElements();) {
             String curTarget = (String) en.nextElement();
             String st = (String) state.get(curTarget);
             if (st == null) {
                 tsort(curTarget, targets, state, visiting, complete);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + curTarget);
             }
         }
         log("Complete build sequence is " + complete, MSG_VERBOSE);
         return ret;
     }
 
     /**
      * Performs a single step in a recursive depth-first-search traversal of
      * the target dependency tree.
      * <p>
      * The current target is first set to the "visiting" state, and pushed
      * onto the "visiting" stack.
      * <p>
      * An exception is then thrown if any child of the current node is in the
      * visiting state, as that implies a circular dependency. The exception
      * contains details of the cycle, using elements of the "visiting" stack.
      * <p>
      * If any child has not already been "visited", this method is called
      * recursively on it.
      * <p>
      * The current target is then added to the ordered list of targets. Note
      * that this is performed after the children have been visited in order
      * to get the correct order. The current target is set to the "visited"
      * state.
      * <p>
      * By the time this method returns, the ordered list contains the sequence
      * of targets up to and including the current target.
      *
      * @param root The current target to inspect.
      *             Must not be <code>null</code>.
      * @param targets A mapping from names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @param state   A mapping from target names to states
      *                (String to String).
      *                The states in question are "VISITING" and "VISITED".
      *                Must not be <code>null</code>.
      * @param visiting A stack of targets which are currently being visited.
      *                 Must not be <code>null</code>.
      * @param ret     The list to add target names to. This will end up
      *                containing the complete list of dependencies in
      *                dependency order.
      *                Must not be <code>null</code>.
      *
      * @exception BuildException if a non-existent target is specified or if
      *                           a circular dependency is detected.
      */
     private final void tsort(String root, Hashtable targets,
                              Hashtable state, Stack visiting,
                              Vector ret)
         throws BuildException {
         state.put(root, VISITING);
         visiting.push(root);
 
         Target target = (Target) targets.get(root);
 
         // Make sure we exist
         if (target == null) {
             StringBuffer sb = new StringBuffer("Target `");
             sb.append(root);
             sb.append("' does not exist in this project. ");
             visiting.pop();
             if (!visiting.empty()) {
                 String parent = (String) visiting.peek();
                 sb.append("It is used from target `");
                 sb.append(parent);
                 sb.append("'.");
             }
 
             throw new BuildException(new String(sb));
         }
 
         for (Enumeration en = target.getDependencies(); en.hasMoreElements();) {
             String cur = (String) en.nextElement();
             String m = (String) state.get(cur);
             if (m == null) {
                 // Not been visited
                 tsort(cur, targets, state, visiting, ret);
             } else if (m == VISITING) {
                 // Currently visiting this node, so have a cycle
                 throw makeCircularException(cur, visiting);
             }
         }
 
         String p = (String) visiting.pop();
         if (root != p) {
             throw new RuntimeException("Unexpected internal error: expected to "
                 + "pop " + root + " but got " + p);
         }
         state.put(root, VISITED);
         ret.addElement(target);
     }
 
     /**
      * Builds an appropriate exception detailing a specified circular
      * dependency.
      *
      * @param end The dependency to stop at. Must not be <code>null</code>.
      * @param stk A stack of dependencies. Must not be <code>null</code>.
      *
      * @return a BuildException detailing the specified circular dependency.
      */
     private static BuildException makeCircularException(String end, Stack stk) {
         StringBuffer sb = new StringBuffer("Circular dependency: ");
         sb.append(end);
         String c;
         do {
             c = (String) stk.pop();
             sb.append(" <- ");
             sb.append(c);
         } while (!c.equals(end));
         return new BuildException(new String(sb));
     }
 
     /**
      * Adds a reference to the project.
      *
      * @param name The name of the reference. Must not be <code>null</code>.
      * @param value The value of the reference. Must not be <code>null</code>.
      */
     public void addReference(String name, Object value) {
         synchronized (references) {
             Object old = ((AntRefTable) references).getReal(name);
             if (old == value) {
                 // no warning, this is not changing anything
                 return;
             }
             if (old != null && !(old instanceof UnknownElement)) {
                 log("Overriding previous definition of reference to " + name,
                     MSG_WARN);
             }
             log("Adding reference: " + name, MSG_DEBUG);
             references.put(name, value);
         }
     }
 
     /**
      * Returns a map of the references in the project (String to Object).
      * The returned hashtable is "live" and so must not be modified.
      *
      * @return a map of the references in the project (String to Object).
      */
     public Hashtable getReferences() {
         return references;
     }
 
     /**
      * Looks up a reference by its key (ID).
      *
      * @param key The key for the desired reference.
      *            Must not be <code>null</code>.
      *
      * @return the reference with the specified ID, or <code>null</code> if
      *         there is no such reference in the project.
diff --git a/src/main/org/apache/tools/ant/types/CommandlineJava.java b/src/main/org/apache/tools/ant/types/CommandlineJava.java
index 939926644..882f09025 100644
--- a/src/main/org/apache/tools/ant/types/CommandlineJava.java
+++ b/src/main/org/apache/tools/ant/types/CommandlineJava.java
@@ -1,669 +1,669 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
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
 
 package org.apache.tools.ant.types;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 import java.util.Enumeration;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Properties;
 import java.util.Vector;
 
 /**
  * A representation of a Java command line that is
  * a composite of 2 <tt>Commandline</tt>. One is used for the
  * vm/options and one for the classname/arguments. It provides
  * specific methods for a java command line.
  *
  */
 public class CommandlineJava implements Cloneable {
 
     /**
      * commands to the JVM
      */
     private Commandline vmCommand = new Commandline();
     /**
      * actual java commands
      */
     private Commandline javaCommand = new Commandline();
     /**
      * properties to add using -D
      */
     private SysProperties sysProperties = new SysProperties();
     private Path classpath = null;
     private Path bootclasspath = null;
     private String vmVersion;
     private String maxMemory = null;
     /**
      *  any assertions to make? Currently only supported in forked JVMs
      */
     private Assertions assertions = null;
 
     /**
      * Indicate whether it will execute a jar file or not, in this case
      * the first vm option must be a -jar and the 'executable' is a jar file.
      */
      private boolean executeJar = false;
 
     /**
      * Whether system properties and bootclasspath shall be cloned.
      * @since Ant 1.7
      */
     private boolean cloneVm = false;
 
     /**
      * Specialized Environment class for System properties
      */
     public static class SysProperties extends Environment implements Cloneable {
         private Properties sys = null;
         private Vector propertySets = new Vector();
 
         /**
          * get the properties as an array; this is an override of the
          * superclass, as it evaluates all the properties
          * @return the array of definitions; may be null
          * @throws BuildException
          */
         public String[] getVariables() throws BuildException {
 
             List definitions = new LinkedList();
             ListIterator list = definitions.listIterator();
             addDefinitionsToList(list);
             if (definitions.size() == 0) {
                 return null;
             } else {
                 return (String[]) definitions.toArray(new String[0]);
             }
         }
 
         /**
          * add all definitions (including property sets) to a list
          * @param listIt list iterator supporting add method
          */
         public void addDefinitionsToList(ListIterator listIt) {
             String[] props = super.getVariables();
             if (props != null) {
                 for (int i = 0; i < props.length; i++) {
                     listIt.add("-D" + props[i]);
                 }
             }
             Properties propertySets = mergePropertySets();
             for (Enumeration e = propertySets.keys(); e.hasMoreElements();) {
                 String key = (String) e.nextElement();
                 String value = propertySets.getProperty(key);
                 listIt.add("-D" + key + "=" + value);
             }
         }
 
         /**
          * This method gets the size of the sysproperties instance. This merges all
          * property sets, so is not an O(1) operation.
          * @return the size of the sysproperties instance
          */
         public int size() {
             Properties p = mergePropertySets();
             return variables.size() + p.size();
         }
 
         /**
          * cache the system properties and set the system properties to the
          * new values
          * @throws BuildException if Security prevented this operation
          */
         public void setSystem() throws BuildException {
             try {
                 sys = System.getProperties();
                 Properties p = new Properties();
-                for (Enumeration e = sys.keys(); e.hasMoreElements();) {
+                for (Enumeration e = sys.propertyNames(); e.hasMoreElements();) {
                     Object o = e.nextElement();
                     p.put(o, sys.get(o));
                 }
                 p.putAll(mergePropertySets());
                 for (Enumeration e = variables.elements(); e.hasMoreElements();) {
                     Environment.Variable v = (Environment.Variable) e.nextElement();
                     p.put(v.getKey(), v.getValue());
                 }
                 System.setProperties(p);
             } catch (SecurityException e) {
                 throw new BuildException("Cannot modify system properties", e);
             }
         }
 
         /**
          * restore the system properties to the cached value
          * @throws BuildException  if Security prevented this operation, or
          * there was no system properties to restore
          */
         public void restoreSystem() throws BuildException {
             if (sys == null) {
                 throw new BuildException("Unbalanced nesting of SysProperties");
             }
 
             try {
                 System.setProperties(sys);
                 sys = null;
             } catch (SecurityException e) {
                 throw new BuildException("Cannot modify system properties", e);
             }
         }
 
         /**
          *  deep clone
          * @return a cloned instance of SysProperties
          */
         public Object clone() throws CloneNotSupportedException {
             try {
                 SysProperties c = (SysProperties) super.clone();
                 c.variables = (Vector) variables.clone();
                 c.propertySets = (Vector) propertySets.clone();
                 return c;
             } catch (CloneNotSupportedException e) {
                 return null;
             }
         }
 
         /**
          * add a propertyset to the total set
          * @param ps the new property set
          */
         public void addSyspropertyset(PropertySet ps) {
             propertySets.addElement(ps);
         }
 
         /**
          * merge all property sets into a single Properties object
          * @return the merged object
          */
         private Properties mergePropertySets() {
             Properties p = new Properties();
             for (Enumeration e = propertySets.elements();
                  e.hasMoreElements();) {
                 PropertySet ps = (PropertySet) e.nextElement();
                 p.putAll(ps.getProperties());
             }
             return p;
         }
     }
 
     /**
      * constructor uses the VM we are running on now.
      */
     public CommandlineJava() {
         setVm(JavaEnvUtils.getJreExecutable("java"));
         setVmversion(JavaEnvUtils.getJavaVersion());
     }
 
     /**
      * create a new argument to the java program
      * @return an argument to be configured
      */
     public Commandline.Argument createArgument() {
         return javaCommand.createArgument();
     }
 
     /**
      * create a new JVM argument
      * @return an argument to be configured
      */
     public Commandline.Argument createVmArgument() {
         return vmCommand.createArgument();
     }
 
     /**
      * add a system property
      * @param sysp a property to be set in the JVM
      */
     public void addSysproperty(Environment.Variable sysp) {
         sysProperties.addVariable(sysp);
     }
 
     /**
      * add a set of system properties
      * @param sysp a set of properties
      */
     public void addSyspropertyset(PropertySet sysp) {
         sysProperties.addSyspropertyset(sysp);
     }
 
     /**
      * set the executable used to start the new JVM
      * @param vm
      */
     public void setVm(String vm) {
         vmCommand.setExecutable(vm);
     }
 
     /**
      * set the JVM version required
      * @param value
      */
     public void setVmversion(String value) {
         vmVersion = value;
     }
 
     /**
      * If set, system properties will be copied to the cloned VM - as
      * well as the bootclasspath unless you have explicitly specified
      * a bootclaspath.
      * @since Ant 1.7
      */
     public void setCloneVm(boolean cloneVm) {
         this.cloneVm = cloneVm;
     }
 
     /**
      * get the current assertions
      * @return assertions or null
      */
     public Assertions getAssertions() {
         return assertions;
     }
 
     /**
      *  add an assertion set to the command
      * @param assertions assertions to make
      */
     public void setAssertions(Assertions assertions) {
         this.assertions = assertions;
     }
 
     /**
      * set a jar file to execute via the -jar option.
      * @param jarpathname the pathname of the jar to execute
      */
     public void setJar(String jarpathname) {
         javaCommand.setExecutable(jarpathname);
         executeJar = true;
     }
 
     /**
      * @return the pathname of the jar file to run via -jar option
      * or <tt>null</tt> if there is no jar to run.
      * @see #getClassname()
      */
     public String getJar() {
         if (executeJar) {
             return javaCommand.getExecutable();
         }
         return null;
     }
 
     /**
      * set the classname to execute
      * @param classname the fully qualified classname.
      */
     public void setClassname(String classname) {
         javaCommand.setExecutable(classname);
         executeJar = false;
     }
 
     /**
      * @return the name of the class to run or <tt>null</tt> if there is no class.
      * @see #getJar()
      */
     public String getClassname() {
         if (!executeJar) {
             return javaCommand.getExecutable();
         }
         return null;
     }
 
     public Path createClasspath(Project p) {
         if (classpath == null) {
             classpath = new Path(p);
         }
         return classpath;
     }
 
     /**
      * @since Ant 1.6
      */
     public Path createBootclasspath(Project p) {
         if (bootclasspath == null) {
             bootclasspath = new Path(p);
         }
         return bootclasspath;
     }
 
     public String getVmversion() {
         return vmVersion;
     }
 
     /**
      * get the command line to run a java vm.
      * @return the list of all arguments necessary to run the vm.
      */
     public String[] getCommandline() {
         //create the list
         List commands = new LinkedList();
         final ListIterator listIterator = commands.listIterator();
         //fill it
         addCommandsToList(listIterator);
         //convert to an array
         return (String[]) commands.toArray(new String[0]);
     }
 
     /**
      * add all the commands to a list identified by the iterator passed in
      * @param listIterator an iterator that supports the add method
      * @since Ant1.6
      */
     private void addCommandsToList(final ListIterator listIterator) {
         //create the command to run Java, including user specified options
         getActualVMCommand().addCommandToList(listIterator);
         // properties are part of the vm options...
         sysProperties.addDefinitionsToList(listIterator);
 
         if (isCloneVm()) {
             SysProperties clonedSysProperties = new SysProperties();
             PropertySet ps = new PropertySet();
             PropertySet.BuiltinPropertySetName sys =
                 new PropertySet.BuiltinPropertySetName();
             sys.setValue("system");
             ps.appendBuiltin(sys);
             clonedSysProperties.addSyspropertyset(ps);
             clonedSysProperties.addDefinitionsToList(listIterator);
         }
 
         //boot classpath
         if (haveBootclasspath(true)) {
             listIterator.add("-Xbootclasspath:" + bootclasspath.toString());
         } else if (cloneBootclasspath()) {
             listIterator.add("-Xbootclasspath:" +
                              Path.systemBootClasspath.toString());
         }
 
         //main classpath
         if (haveClasspath()) {
             listIterator.add("-classpath");
             listIterator.add(
                     classpath.concatSystemClasspath("ignore").toString());
         }
 
         //now any assertions are added
         if (getAssertions() != null) {
             getAssertions().applyAssertions(listIterator);
         }
 
         // JDK usage command line says that -jar must be the first option, as there is
         // a bug in JDK < 1.4 that forces the jvm type to be specified as the first
         // option, it is appended here as specified in the docs even though there is
         // in fact no order.
         if (executeJar) {
             listIterator.add("-jar");
         }
         // this is the classname to run as well as its arguments.
         // in case of 'executeJar', the executable is a jar file.
         javaCommand.addCommandToList(listIterator);
     }
 
     /**
      * Specify max memory of the JVM
      * -mx or -Xmx depending on VM version
      */
     public void setMaxmemory(String max) {
         this.maxMemory = max;
     }
 
 
     /**
      * get a string description.
      * @return the command line as a string
      */
     public String toString() {
         return Commandline.toString(getCommandline());
     }
 
     /**
      * Returns a String that describes the command and arguments
      * suitable for verbose output before a call to
      * <code>Runtime.exec(String[])<code>
      *
      * @since Ant 1.5
      */
     public String describeCommand() {
         return Commandline.describeCommand(getCommandline());
     }
 
     /**
      * Returns a String that describes the java command and arguments
      * for in VM executions.
      *
      * <p>The class name is the executable in this context.</p>
      *
      * @since Ant 1.5
      */
     public String describeJavaCommand() {
         return Commandline.describeCommand(getJavaCommand());
     }
 
     /**
      * Get the VM command parameters, including memory settings
      * @return the VM command parameters
      */
     protected Commandline getActualVMCommand() {
         Commandline actualVMCommand = (Commandline) vmCommand.clone();
         if (maxMemory != null) {
             if (vmVersion.startsWith("1.1")) {
                 actualVMCommand.createArgument().setValue("-mx" + maxMemory);
             } else {
                 actualVMCommand.createArgument().setValue("-Xmx" + maxMemory);
             }
         }
         return actualVMCommand;
     }
 
     /**
      * The size of the java command line. This is a fairly intensive
      * operation, as it has to evaluate the size of many components.
      * @return the total number of arguments in the java command line.
      * @see #getCommandline()
      * @deprecated please dont use this -it effectively creates the entire command.
      */
     public int size() {
         int size = getActualVMCommand().size() + javaCommand.size()
             + sysProperties.size();
         // cloned system properties
         if (isCloneVm()) {
             size += System.getProperties().size();
         }
 
         // classpath is "-classpath <classpath>" -> 2 args
         if (haveClasspath()) {
             size += 2;
         }
         // bootclasspath is "-Xbootclasspath:<classpath>" -> 1 arg
         if (haveBootclasspath(true) || cloneBootclasspath()) {
             size++;
         }
         // jar execution requires an additional -jar option
         if (executeJar) {
             size++;
         }
         //assertions take up space too
         if (getAssertions() != null) {
             size += getAssertions().size();
         }
         return size;
     }
 
     /**
      * get the Java command to be used.
      * @return the java command -not a clone.
      */
     public Commandline getJavaCommand() {
         return javaCommand;
     }
 
     /**
      * Get the VM command, including memory.
      * @return A deep clone of the instance's VM command, with memory settings added
      */
     public Commandline getVmCommand() {
         return getActualVMCommand();
     }
 
     /**
      * get the classpath for the command
      * @return the classpath or null
      */
     public Path getClasspath() {
         return classpath;
     }
 
     /**
      * get the boot classpath
      * @return boot classpath or null
      */
     public Path getBootclasspath() {
         return bootclasspath;
     }
 
     /**
      * cache current system properties and set them to those in this
      * java command
      * @throws BuildException  if Security prevented this operation
      */
     public void setSystemProperties() throws BuildException {
         sysProperties.setSystem();
     }
 
     /**
      * @throws BuildException  if Security prevented this operation, or
      * there was no system properties to restore
      */
     public void restoreSystemProperties() throws BuildException {
         sysProperties.restoreSystem();
     }
 
     /**
      * get the system properties object
      * @return The system properties object
      */
     public SysProperties getSystemProperties() {
         return sysProperties;
     }
 
     /**
      * clone the object; clone of all fields in the class
      * @return a CommandlineJava object
      * @throws BuildException if anything went wrong.
      * @throws CloneNotSupportedException never
      */
     public Object clone() throws CloneNotSupportedException {
         try {
             CommandlineJava c = (CommandlineJava) super.clone();
             c.vmCommand = (Commandline) vmCommand.clone();
             c.javaCommand = (Commandline) javaCommand.clone();
             c.sysProperties = (SysProperties) sysProperties.clone();
             if (classpath != null) {
                 c.classpath = (Path) classpath.clone();
             }
             if (bootclasspath != null) {
                 c.bootclasspath = (Path) bootclasspath.clone();
             }
             if (assertions != null) {
                 c.assertions = (Assertions) assertions.clone();
             }
             return c;
         } catch (CloneNotSupportedException e) {
             throw new BuildException(e);
         }
     }
 
     /**
      * Clear out the java arguments.
      */
     public void clearJavaArgs() {
         javaCommand.clearArgs();
     }
 
     /**
      * Has the classpath been specified and shall it really be used or
      * will build.sysclasspath null it?
      *
      * @since Ant 1.6
      */
     protected boolean haveClasspath() {
         Path fullClasspath = classpath != null
             ? classpath.concatSystemClasspath("ignore") : null;
         return fullClasspath != null
             && fullClasspath.toString().trim().length() > 0;
     }
 
     /**
      * Has the bootclasspath been specified and shall it really be
      * used (build.sysclasspath could be set or the VM may not support
      * it)?
      *
      * @param log whether to log a warning if a bootclasspath has been
      * specified but will be ignored.
      *
      * @since Ant 1.6
      */
     protected boolean haveBootclasspath(boolean log) {
         if (bootclasspath != null
             && bootclasspath.toString().trim().length() > 0) {
 
             /*
              * XXX - need to log something, but there is no ProjectComponent
              *       around to log to.
              */
             if (!bootclasspath.toString()
                 .equals(bootclasspath.concatSystemClasspath("ignore")
                         .toString())) {
                 if (log) {
                     System.out.println("Ignoring bootclasspath as "
                                        + "build.sysclasspath has been set.");
                 }
             } else if (vmVersion.startsWith("1.1")) {
                 if (log) {
                     System.out.println("Ignoring bootclasspath as "
                                        + "the target VM doesn't support it.");
                 }
             } else {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Should a bootclasspath argument be created to clone the current
      * VM settings?
      *
      * @since Ant 1.7
      */
     private boolean cloneBootclasspath() {
         return isCloneVm() && !vmVersion.startsWith("1.1")
             && Path.systemBootClasspath.size() > 0;
     }
 
     /**
      * Has the cloneVm attribute or the magic property build.clonevm been set?
      *
      * @since 1.7
      */
     private boolean isCloneVm() {
         return cloneVm || "true".equals(System.getProperty("build.clonevm"));
     }
 }
