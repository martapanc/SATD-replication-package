diff --git a/WHATSNEW b/WHATSNEW
index 414441ada..088cec43c 100644
--- a/WHATSNEW
+++ b/WHATSNEW
@@ -1,1023 +1,1027 @@
 Changes from current Ant 1.6 CVS version to current CVS version
 =============================================
 
 Changes that could break older environments:
 --------------------------------------------
 
 Fixed bugs:
 -----------
 
 * Translate task does not remove tokens when a key is not found.
   It logs a verbose message.  Bugzilla Report 13936.
 
 Other changes:
 --------------
 
 * Enable to choose the regexp implementation without system property.
   Bugzilla Report 15390.
 
 * Changed default tempdir for <javac> from user.dir to java.io.tmpdir.
 
 * A new base class DispatchTask has been added to facilitate elegant 
   creation of tasks with multiple actions.
 
+* Added <target> nested elements to <ant> and <antcall> to allow
+  specification of multiple sub-build targets, which are executed
+  with a single dependency analysis.
+
 Changes from Ant 1.6.1 to current Ant 1.6 CVS version
 =====================================================
 
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
 
 Fixed bugs:
 -----------
 
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
 
diff --git a/docs/manual/CoreTasks/ant.html b/docs/manual/CoreTasks/ant.html
index 2924453a3..13e404c0c 100644
--- a/docs/manual/CoreTasks/ant.html
+++ b/docs/manual/CoreTasks/ant.html
@@ -1,250 +1,270 @@
 <html>
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <title>Ant Task</title>
 </head>
 
 <body>
 
 <h2><a name="ant">Ant</a></h2>
 <h3>Description</h3>
 
 <p>Runs Ant on a supplied buildfile. This can be used to build
 subprojects.  <strong>This task must not be used outside of a
 <code>target</code> if it invokes the same build file it is part
 of.</strong></p>
 
 <p>When the <i>antfile</i> attribute is omitted, the file &quot;build.xml&quot;
 in the supplied directory (<i>dir</i> attribute) is used.</p>
 <p>If no target attribute is supplied, the default target of the new project is
 used.</p>
 <p>By default, all of the properties of the current project will be
 available in the new project.  Alternatively, you can set the
 <i>inheritAll</i> attribute to <code>false</code> and only
 &quot;user&quot; properties (i.e., those passed on the command-line)
 will be passed to the new project.  In either case, the set of
 properties passed to the new project will override the properties that
 are set in the new project (See also the <a
 href="property.html">property task</a>).</p>
 
 <p>You can also set properties in the new project from the old project
 by using nested property tags. These properties are always passed
 to the new project and any project created in that project
 regardless of the setting of <i>inheritAll</i>.  This allows you to
 parameterize your subprojects.  Properties defined on the command line
 can not be overridden by nested &lt;property&gt; elements.</p>
 
 <p>References to data types can also be passed to the new project, but
 by default they are not.  If you set the inheritrefs attribute to
 true, all references will be copied, but they will not override
 references defined in the new project.</p>
 
 <p>Nested <a href="#reference"><i>&lt;reference&gt;</i></a> elements
 can also be used to copy references from the calling project to the
 new project, optionally under a different id.  References taken from
 nested elements will override existing references that have been
 defined outside of targets in the new project - but not those defined
 inside of targets.</p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">antfile</td>
     <td valign="top">the buildfile to use. Defaults to
       &quot;build.xml&quot;.  This file is expected to be a filename
       relative to the dir attribute given.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">dir</td>
          <td valign="top">the directory to use as a basedir for the new Ant project.
       Defaults to the current project's basedir, unless
       inheritall has been set to false, in which case it doesn't
       have a default value.  This will override the basedir
       setting of the called project.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">target</td>
     <td valign="top">the target of the new Ant project that should be executed.
       Defaults to the new project's default target.</td>
     <td valign="top" align="center">No</td>
   </tr>
   <tr>
     <td valign="top">output</td>
     <td valign="top">Filename to write the ant output to.  This is
     relative to the value of the dir attribute if it has been set or
     to the base directory of the current project otherwise.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">inheritAll</td>
     <td valign="top">If <code>true</code>, pass all properties to the
       new Ant project.  Defaults to <code>true</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">inheritRefs</td>
     <td valign="top">If <code>true</code>, pass all references to the
       new Ant project.  Defaults to <code>false</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
 </table>
 
 <h3>Parameters specified as nested elements</h3>
 
 <h4>property</h4>
 <p>See the description of the <a href="property.html">property
 task</a>.  Note that the <code>refid</code> attribute points to a
 reference in the calling project, not in the new one.</p>
 
 <h4><a name="reference">reference</a></h4>
 <p>Used to choose references that shall be copied into the new project,
 optionally changing their id.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">refid</td>
     <td valign="top">The id of the reference in the calling project.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">torefid</td>
     <td valign="top">The id of the reference in the new project.</td>
     <td valign="top" align="center">No, defaults to the value of refid.</td>
   </tr>
 </table>
 
 <h4>propertyset</h4>
 
 <p>You can specify a set of properties to be copied into the new
 project with <a
 href="../CoreTypes/propertyset.html">propertyset</a>s.</p>
 
 <p><em>since Ant 1.6</em>.</p>
 
+<h4>target</h4>
+
+<p>You can specify multiple targets using nested &lt;target&gt; elements
+instead of using the target attribute.  These will be executed as if
+Ant had been invoked with a single target whose dependencies are the
+targets so specified, in the order specified.</p>
+<table border="1" cellpadding="2" cellspacing="0">
+  <tr>
+    <td valign="top"><b>Attribute</b></td>
+    <td valign="top"><b>Description</b></td>
+    <td align="center" valign="top"><b>Required</b></td>
+  </tr>
+  <tr>
+    <td valign="top">name</td>
+    <td valign="top">The name of the called target.</td>
+    <td valign="top" align="center">Yes</td>
+  </tr>
+</table>
+<p><em>since Ant 1.6.2</em>.</p>
+
 <h3>Basedir of the new project</h3>
 
 <p>The basedir value of the new project is affected by the two
 attributes dir and inheritall, see the following table for
 details:</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>dir attribute</b></td>
     <td valign="top"><b>inheritAll attribute</b></td>
     <td valign="top"><b>new project's basedir</b></td>
   </tr>
   <tr>
     <td valign="top">value provided</td>
     <td valign="top">true</td>
     <td valign="top">value of dir attribute</td>
   </tr>
   <tr>
     <td valign="top">value provided</td>
     <td valign="top">false</td>
     <td valign="top">value of dir attribute</td>
   </tr>
   <tr>
     <td valign="top">omitted</td>
     <td valign="top">true</td>
     <td valign="top">basedir of calling project (the one whose build
         file contains the &lt;ant&gt; task).</td>
   </tr>
   <tr>
     <td valign="top">omitted</td>
     <td valign="top">false</td>
     <td valign="top">basedir attribute of the &lt;project&gt; element
         of the new project</td>
   </tr>
 </table>
 
 <h3>Examples</h3>
 <pre>
   &lt;ant antfile=&quot;subproject/subbuild.xml&quot; dir=&quot;subproject&quot; target=&quot;compile&quot;/&gt;
 
   &lt;ant dir=&quot;subproject&quot;/&gt;
 
   &lt;ant antfile=&quot;subproject/property_based_subbuild.xml&quot;&gt;
     &lt;property name=&quot;param1&quot; value=&quot;version 1.x&quot;/&gt;
     &lt;property file=&quot;config/subproject/default.properties&quot;/&gt;
   &lt;/ant&gt;
 
   &lt;ant inheritAll=&quot;false&quot; antfile=&quot;subproject/subbuild.xml&quot;&gt;
     &lt;property name=&quot;output.type&quot; value=&quot;html&quot;/&gt;
   &lt;/ant&gt;
 </pre>
 
 <p>The build file of the calling project defines some
 <code>&lt;path&gt;</code> elements like this:</p>
 
 <pre>
   &lt;path id="path1"&gt;
     ...
   &lt;/path&gt;
   &lt;path id="path2"&gt;
     ...
   &lt;/path&gt;
 </pre>
 
 <p>and the called build file (<code>subbuild.xml</code>) also defines
 a <code>&lt;path&gt;</code> with the id <code>path1</code>, but
 <code>path2</code> is not defined:</p>
 
 <pre>
   &lt;ant antfile=&quot;subbuild.xml&quot; inheritrefs=&quot;true&quot;/&gt;
 </pre>
 
 <p>will not override <code>subbuild</code>'s definition of
 <code>path1</code>, but make the parent's definition of
 <code>path2</code> available in the subbuild.</p>
 
 <pre>
   &lt;ant antfile=&quot;subbuild.xml&quot;/&gt;
 </pre>
 
 <p>as well as</p>
 
 <pre>
   &lt;ant antfile=&quot;subbuild.xml&quot; inheritrefs=&quot;false&quot;/&gt;
 </pre>
 
 <p>will neither override <code>path1</code> nor copy
 <code>path2</code>.</p>
 
 <pre>
   &lt;ant antfile=&quot;subbuild.xml&quot; inheritrefs=&quot;false&quot;&gt;
     &lt;reference refid=&quot;path1&quot;/&gt;
   &lt;/ant&gt;
 </pre>
 
 <p>will override <code>subbuild</code>'s definition of
 <code>path1</code>.</p>
 
 <pre>
   &lt;ant antfile=&quot;subbuild.xml&quot; inheritrefs=&quot;false&quot;&gt;
     &lt;reference refid=&quot;path1&quot; torefid=&quot;path2&quot;/&gt;
   &lt;/ant&gt;
 </pre>
 
 <p>will copy the parent's definition of <code>path1</code> into the
 new project using the id <code>path2</code>.</p>
 
 <hr>
 <p align="center">Copyright &copy; 2000-2004 The Apache Software Foundation. All rights
 Reserved.</p>
 
 </body>
 </html>
diff --git a/docs/manual/CoreTasks/antcall.html b/docs/manual/CoreTasks/antcall.html
index c8f8657b2..314759256 100644
--- a/docs/manual/CoreTasks/antcall.html
+++ b/docs/manual/CoreTasks/antcall.html
@@ -1,150 +1,170 @@
 <html>
 
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <title>AntCall Task</title>
 </head>
 
 <body>
 
 <h2><a name="antcall">AntCall</a></h2>
 <h3>Description</h3>
 
 <p>Call another target within the same buildfile optionally
 specifying some properties (params in this context).  <strong>This
 task must not be used outside of a <code>target</code>.</strong></p>
 
 <p>By default, all of the properties of the current project will be
 available in the new project.   Alternatively, you can
 set the <i>inheritAll</i> attribute to <code>false</code> and only
 &quot;user&quot; properties (i.e., those passed on the command-line)
 will be passed to the new project.  In either case, the set of
 properties passed to the new project will override the properties that
 are set in the new project (See also the <a href="property.html">property task</a>).</p>
 <p>You can also set properties in the new project from the old project
 by using nested param tags. These properties are always passed
 to the new project and any project created in that project
 regardless of the setting of <i>inheritAll</i>.  This allows you to
 parameterize your subprojects.  Properties defined on the command line
 can not be overridden by nested &lt;param&gt; elements.</p>
 
 <p>Nested <a href="#reference"><i>&lt;reference&gt;</i></a> elements can
 be used to copy references from the calling project to the new
 project, optionally under a different id.  References taken from
 nested elements will override existing references that have been
 defined outside of targets in the new project - but not those defined
 inside of targets.</p>
 
 <p>
 When a target is invoked by antcall, all of its dependent targets will
 also be called within the context of any new parameters. For example. if
 the target &quot;doSomethingElse&quot; depended on the target &quot;init&quot;, then the
 <i>antcall</i> of &quot;doSomethingElse&quot; will call &quot;init&quot; during the call.
 Of course, any properties defined in the antcall task or inherited from the calling target
 will be fixed and not overridable in the init task--or indeed in the &quot;doSomethingElse&quot; task.
 </p>
 
 <p>If the build file changes after you've started the build, the
 behavior of this task is undefined.</p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">target</td>
     <td valign="top">The target to execute.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">inheritAll</td>
     <td valign="top">If <code>true</code>, pass all properties to the new Ant
     project.  Defaults to <code>true</code>.
     </td>
     <td align="center" valign="top">No</td>
   </tr>
   <tr>
     <td valign="top">inheritRefs</td>
     <td valign="top">If <code>true</code>, pass all references to the
       new Ant project.  Defaults to <code>false</code>.</td>
     <td align="center" valign="top">No</td>
   </tr>
 </table>
 
 <h3>Note on <code>inheritRefs</code></h3>
 
 <p><code>&lt;antcall&gt;</code> will not override existing references,
 even if you set <code>inheritRefs</code> to true.  As the called build
 files is the same build file as the calling one, this means it will
 not override any reference set via an <code>id</code> attribute at
 all.  The only references that can be inherited by the child project
 are those defined by nested <code>&lt;reference&gt;</code> elements or
 references defined by tasks directly (not using the <code>id</code>
 attribute).</p>
 
 <h3>Parameters specified as nested elements</h3>
 <h4>param</h4>
 <p>Specifies the properties to set before running the specified target. See <a
 href="property.html">property</a> for usage guidelines.</p>
 
 <h4><a name="reference">reference</a></h4>
 <p>Used to choose references that shall be copied into the new project,
 optionally changing their id.</p>
 
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">refid</td>
     <td valign="top">The id of the reference in the calling project.</td>
     <td valign="top" align="center">Yes</td>
   </tr>
   <tr>
     <td valign="top">torefid</td>
     <td valign="top">The id of the reference in the new project.</td>
     <td valign="top" align="center">No, defaults to the value of refid.</td>
   </tr>
 </table>
 
 <h4>propertyset</h4>
 
 <p>You can specify a set of properties to be copied into the new
 project with <a
 href="../CoreTypes/propertyset.html">propertyset</a>s.</p>
 
 <p><em>since Ant 1.6</em>.</p>
 
+<h4>target</h4>
+
+<p>You can specify multiple targets using nested &lt;target&gt; elements
+instead of using the target attribute.  These will be executed as if
+Ant had been invoked with a single target whose dependencies are the
+targets so specified, in the order specified.</p>
+<table border="1" cellpadding="2" cellspacing="0">
+  <tr>
+    <td valign="top"><b>Attribute</b></td>
+    <td valign="top"><b>Description</b></td>
+    <td align="center" valign="top"><b>Required</b></td>
+  </tr>
+  <tr>
+    <td valign="top">name</td>
+    <td valign="top">The name of the called target.</td>
+    <td valign="top" align="center">Yes</td>
+  </tr>
+</table>
+<p><em>since Ant 1.6.2</em>.</p>
+
 <h3>Examples</h3>
 <pre>
   &lt;target name=&quot;default&quot;&gt;
     &lt;antcall target=&quot;doSomethingElse&quot;&gt;
       &lt;param name=&quot;param1&quot; value=&quot;value&quot;/&gt;
     &lt;/antcall&gt;
   &lt;/target&gt;
 
   &lt;target name=&quot;doSomethingElse&quot;&gt;
     &lt;echo message=&quot;param1=${param1}&quot;/&gt;
   &lt;/target&gt;
 </pre>
 <p>Will run the target 'doSomethingElse' and echo 'param1=value'.</p>
 
 <pre>
   &lt;antcall ... &gt;
     &lt;reference refid=&quot;path1&quot; torefid=&quot;path2&quot;/&gt;
   &lt;/antcall&gt;
 </pre>
 
 <p>will copy the parent's definition of <code>path1</code> into the
 new project using the id <code>path2</code>.</p>
 
 <hr><p align="center">Copyright &copy; 2000-2004 The Apache Software Foundation. All rights
 Reserved.</p>
 
 </body>
 </html>
diff --git a/src/etc/testcases/taskdefs/ant.xml b/src/etc/testcases/taskdefs/ant.xml
index bf4357b64..aa69d6488 100644
--- a/src/etc/testcases/taskdefs/ant.xml
+++ b/src/etc/testcases/taskdefs/ant.xml
@@ -1,195 +1,217 @@
 <?xml version="1.0"?>
 
 <project name="ant-test" basedir="." default="test1">
 
   <path id="inheritable">
     <pathelement path="${java.class.path}" />
   </path>
 
   <target name="cleanup">
     <delete file="test1.log" />
     <delete file="test2.log" />
     <delete file="ant/test3.log" />
     <delete file="ant/test4.log" />
   </target>
 
   <target name="all" depends="test1,test2,test3,test4"/>
 
   <target name="test1">
     <ant antfile="ant.xml" dir="." target="test1"/>
   </target>
 
   <target name="test2">
     <antcall/>
   </target>
 
   <target name="test3">
     <antcall target="test3"/>
   </target>
 
   <target name="test4">
     <antcall target=""/>
   </target>
 
   <target name="test4b">
     <antcall target="does-not-exist"/>
   </target>
 
   <target name="test5">
     <antcall target="dummy"/>
   </target>
 
   <target name="test6">
     <ant antfile="ant.xml" dir="." target="dummy"/>
   </target>
 
   <target name="dummy">
   </target>
 
   <target name="inheritBasedir">
     <ant antfile="ant/ant.xml" target="dummy" inheritAll="true" />
   </target>
 
   <target name="doNotInheritBasedir">
     <ant antfile="ant/ant.xml" target="dummy" inheritAll="false" />
   </target>
 
   <target name="explicitBasedir1">
     <ant antfile="taskdefs/ant/ant.xml" target="dummy" inheritAll="true" 
          dir=".." />
   </target>
 
   <target name="explicitBasedir2">
     <ant antfile="taskdefs/ant/ant.xml" target="dummy" inheritAll="false" 
          dir=".." />
   </target>
 
   <target name="tripleCall">
     <ant antfile="ant/ant.xml" target="callback" inheritAll="false" />
   </target>
 
   <target name="testInherit">
     <ant antfile="ant/references.xml" inheritRefs="true" target="dummy" />
   </target>
 
   <target name="testNoInherit">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy" />
   </target>
 
   <target name="testRename">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy">
       <reference refid="path" torefid="newpath" />
     </ant>
   </target>
 
   <target name="testLogfilePlacement">
     <ant antfile="ant.xml" target="dummy" output="test1.log"
          inheritall="false" />
     <ant antfile="ant.xml" target="dummy" output="test2.log" />
     <ant antfile="ant.xml" target="dummy" output="test3.log"
          inheritall="false" dir="ant" />
     <ant antfile="ant.xml" target="dummy" output="test4.log" 
          dir="ant" />
   </target>
 
   <target name="testRefid">
     <ant antfile="ant/references.xml" inheritRefs="false" target="dummy">
       <property name="testprop" refid="inheritable" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-start">
     <property name="test" value="1" />
     <ant antfile="ant.xml"
          target="test-property-override-inheritall-level-2"
          inheritall="true">
       <property name="test" value="2" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-level-2">
     <property name="test" value="3" />
     <ant antfile="ant.xml"
          target="test-property-override-inheritall-level-3"
          inheritall="true">
       <property name="test" value="4" />
     </ant>
   </target>
 
   <target name="test-property-override-inheritall-level-3">
     <property name="test" value="5" />
     <echo message="The value of test is ${test}" />
   </target>
 
   <target name="test-property-override-no-inheritall-start">
     <property name="test" value="1" />
     <ant antfile="ant.xml"
          target="test-property-override-no-inheritall-level-2"
          inheritall="false">
       <property name="test" value="2" />
     </ant>
   </target>
 
   <target name="test-property-override-no-inheritall-level-2">
     <property name="test" value="3" />
     <ant antfile="ant.xml"
          target="test-property-override-no-inheritall-level-3"
          inheritall="false">
       <property name="test" value="4" />
     </ant>
   </target>
 
   <target name="test-property-override-no-inheritall-level-3">
     <property name="test" value="5" />
     <echo message="The value of test is ${test}" />
   </target>
 
   <target name="test-propertyset">
     <property name="test1" value="1"/>
     <property name="test2" value="2"/>
     <propertyset id="set">
       <propertyref name="test1"/>
       <mapper type="glob" from="*" to="*.x"/>
     </propertyset>
     <ant antfile="ant.xml" target="echo-for-propertyset-test" 
          inheritall="false">
       <propertyset refid="set"/>
     </ant>
   </target>
 
   <target name="echo-for-propertyset-test">
     <echo>test1 is ${test1}</echo>
     <echo>test2 is ${test2}</echo>
     <echo>test1.x is ${test1.x}</echo>
   </target>
 
   <target name="infinite-loop-via-depends">
     <antcall target="dependent"/>
   </target>
 
   <target name="middleman" depends="infinite-loop-via-depends"/>
   <target name="dependent" depends="middleman"/>
   
   <target name="multi-same-property">
     <ant antfile="ant.xml" target="echo-for-multi-same">
       <property name="prop" value="one"/>
       <property name="prop" value="two"/>
     </ant>
   </target>
 
   <target name="echo-for-multi-same">
     <echo>prop is ${prop}</echo>
   </target>
 
   <target name="topleveltarget">
     <ant antfile="ant.topleveltest.xml"/>
   </target>
 
   <target name="multiple-property-file-children">
     <ant target="dummy" antfile="ant.xml">
       <property file="foo.properties"/>
       <property file="bar.properties"/>
     </ant>
   </target>
 
+  <target name="blank-target">
+    <ant antfile="ant.topleveltest.xml">
+      <target name="" />
+    </ant>
+  </target>
+
+  <target name="multiple-targets">
+    <ant antfile="ant.xml">
+      <target name="ta" />
+      <target name="tb" />
+      <target name="tc" />
+    </ant>
+  </target>
+
+  <target name="ta"><echo>ta</echo></target>
+  <target name="tb" depends="da,dc"><echo>tb</echo></target>
+  <target name="tc" depends="db,dc"><echo>tc</echo></target>
+
+  <target name="da"><echo>da</echo></target>
+  <target name="db"><echo>db</echo></target>
+  <target name="dc"><echo>dc</echo></target>
+
 </project>
diff --git a/src/etc/testcases/taskdefs/calltarget.xml b/src/etc/testcases/taskdefs/calltarget.xml
index 380432ee1..17c9f0574 100644
--- a/src/etc/testcases/taskdefs/calltarget.xml
+++ b/src/etc/testcases/taskdefs/calltarget.xml
@@ -1,53 +1,76 @@
 <?xml version="1.0"?>
 <project name ="calltarget-test" default="testinheritreffileset" basedir=".">
     <property name="tmp.dir" value="tmp.dir" />
     <target name="setup">
         <mkdir dir="${tmp.dir}"/>
     </target>
     <target name="cleanup">
         <delete dir="${tmp.dir}" quiet="true"/>
     </target>
     <target name="mytarget">
       <pathconvert property="myproperty" targetos="unix" refid="myfileset"/>
       <echo message="myproperty=${myproperty}"/>
     </target>
     <target name="testinheritreffileset">
     <!-- this testcase should show that the fileset defined here
     can be read in the called target -->
       <fileset dir="." id="myfileset">
         <include name="calltarget.xml"/>
       </fileset>
       <antcall target="mytarget" inheritrefs="true"/>
     </target>
     <target name="copytest2">
        <copy file="${tmp.dir}/copytest.in" toFile="${tmp.dir}/copytest1.out" overwrite="true">
           <filterset refid="foo"/>
        </copy>
     </target>
     <target name="testinheritreffilterset" depends="setup">
        <echo file="${tmp.dir}/copytest.in">@@foo@@</echo>
        <filterset id="foo" begintoken="@@" endtoken="@@">
           <filter token="foo" value="bar"/>
        </filterset>
        <antcall target="copytest2" inheritrefs="true"/>
        <copy file="${tmp.dir}/copytest.in" toFile="${tmp.dir}/copytest2.out" overwrite="true">
           <filterset refid="foo"/>
        </copy>
        <loadfile srcFile="${tmp.dir}/copytest2.out" property="copytest2"/>
        <loadfile srcFile="${tmp.dir}/copytest1.out" property="copytest1"/>
        <condition property="success">
            <equals arg1="${copytest1}" arg2="${copytest2}"/>
        </condition>
        <fail message="filterset not properly passed across by antcall" unless="success"/>
     </target>
 
     <property name="multi" value="DEFAULT"/>
     <target name="multi">
         <echo>multi is ${multi}</echo>
     </target>
     <target name="call-multi">
         <antcall target="multi">
             <param name="multi" value="SET"/>
         </antcall>
     </target>
-</project>
\ No newline at end of file
+
+    <target name="blank-target">
+        <antcall>
+            <target name="" />
+        </antcall>
+    </target>
+
+    <target name="multiple-targets">
+        <antcall>
+            <target name="ta" />
+            <target name="tb" />
+            <target name="tc" />
+        </antcall>
+    </target>
+
+    <target name="ta"><echo>ta</echo></target>
+    <target name="tb" depends="da,dc"><echo>tb</echo></target>
+    <target name="tc" depends="db,dc"><echo>tc</echo></target>
+
+    <target name="da"><echo>da</echo></target>
+    <target name="db"><echo>db</echo></target>
+    <target name="dc"><echo>dc</echo></target>
+
+</project>
diff --git a/src/main/org/apache/tools/ant/Project.java b/src/main/org/apache/tools/ant/Project.java
index 7e66c7608..82b3a9f03 100644
--- a/src/main/org/apache/tools/ant/Project.java
+++ b/src/main/org/apache/tools/ant/Project.java
@@ -190,1906 +190,1939 @@ public class Project {
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
      * on failed target(s) will be executed.
      * @param keepGoingMode "keep-going" mode
      * @since Ant 1.6
      */
     public void setKeepGoingMode(boolean keepGoingMode) {
         this.keepGoingMode = keepGoingMode;
     }
 
     /**
      * Returns the keep-going mode.
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
         Enumeration e = systemP.keys();
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
 
         BuildException thrownException = null;
         for (int i = 0; i < targetNames.size(); i++) {
             try {
                 executeTarget((String) targetNames.elementAt(i));
             } catch (BuildException ex) {
                 if (!(keepGoingMode)) {
                     throw ex; // Throw further
                 }
                 thrownException = ex;
             }
         }
         if (thrownException != null) {
             throw thrownException;
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
 
         // Sort the dependency tree, and run everything from the
         // beginning until we hit our targetName.
         // Sorting checks if all the targets (and dependencies)
         // exist, and if there is any cycle in the dependency
         // graph.
         Vector sortedTargets = topoSort(targetName, targets);
+        sortedTargets.setSize(sortedTargets.indexOf(targets.get(targetName)) + 1);
+        executeSortedTargets(sortedTargets);
+    }
 
+    /**
+     * Executes a <CODE>Vector</CODE> of sorted targets.
+     * @param sortedTargets   the aforementioned <CODE>Vector</CODE>.
+     */
+    public void executeSortedTargets(Vector sortedTargets)
+        throws BuildException {
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
-            if (curtarget.getName().equals(targetName)) { // old exit condition
-                break;
-            }
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
      * Topologically sorts a set of targets.
      *
      * @param root The name of the root target. The sort is created in such
      *             a way that the sequence of Targets up to the root
      *             target is the minimum possible such sequence.
      *             Must not be <code>null</code>.
      * @param targets A map of names to targets (String to Target).
      *                Must not be <code>null</code>.
      * @return a vector of Target objects in sorted order.
      * @exception BuildException if there is a cyclic dependency among the
      *                           targets, or if a named target does not exist.
      */
     public final Vector topoSort(String root, Hashtable targets)
         throws BuildException {
+        return topoSort(new String[] {root}, targets);
+    }
+
+    /**
+     * Topologically sorts a set of targets.
+     *
+     * @param root <CODE>String[]</CODE> containing the names of the root targets.
+     *             The sort is created in such a way that the sequence of Targets
+     *             up to the root target is the minimum possible such sequence.
+     *             Must not be <code>null</code>.
+     * @param targets A map of names to targets (String to Target).
+     *                Must not be <code>null</code>.
+     * @return a vector of Target objects in sorted order.
+     * @exception BuildException if there is a cyclic dependency among the
+     *                           targets, or if a named target does not exist.
+     */
+    public final Vector topoSort(String[] root, Hashtable targets)
+        throws BuildException {
         Vector ret = new Vector();
         Hashtable state = new Hashtable();
         Stack visiting = new Stack();
 
-        // We first run a DFS based sort using the root as the starting node.
-        // This creates the minimum sequence of Targets to the root node.
+        // We first run a DFS based sort using each root as a starting node.
+        // This creates the minimum sequence of Targets to the root node(s).
         // We then do a sort on any remaining unVISITED targets.
         // This is unnecessary for doing our build, but it catches
         // circular dependencies or missing Targets on the entire
         // dependency tree, not just on the Targets that depend on the
         // build Target.
 
-        tsort(root, targets, state, visiting, ret);
-        log("Build sequence for target `" + root + "' is " + ret, MSG_VERBOSE);
+        for (int i = 0; i < root.length; i++) {
+            tsort(root[i], targets, state, visiting, ret);
+        }
+        StringBuffer buf = new StringBuffer("Build sequence for target(s)");
+
+        for (int j = 0; j < root.length; j++) {
+            buf.append((j == 0) ? " `" : ", `").append(root[j]).append('\'');
+        }
+        buf.append(" is " + ret);
+        log(buf.toString(), MSG_VERBOSE);
+
         for (Enumeration en = targets.keys(); en.hasMoreElements();) {
             String curTarget = (String) en.nextElement();
             String st = (String) state.get(curTarget);
             if (st == null) {
                 tsort(curTarget, targets, state, visiting, ret);
             } else if (st == VISITING) {
                 throw new RuntimeException("Unexpected node in visiting state: "
                     + curTarget);
             }
         }
         log("Complete build sequence is " + ret, MSG_VERBOSE);
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
      */
     public Object getReference(String key) {
         return references.get(key);
     }
 
     /**
      * Returns a description of the type of the given element, with
      * special handling for instances of tasks and data types.
      * <p>
      * This is useful for logging purposes.
      *
      * @param element The element to describe.
      *                Must not be <code>null</code>.
      *
      * @return a description of the element type
      *
      * @since 1.95, Ant 1.5
      */
     public String getElementName(Object element) {
         return ComponentHelper.getComponentHelper(this).getElementName(element);
     }
 
     /**
      * Sends a "build started" event to the build listeners for this project.
      */
     public void fireBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.buildStarted(event);
         }
     }
 
     /**
      * Sends a "build finished" event to the build listeners for this project.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     public void fireBuildFinished(Throwable exception) {
         BuildEvent event = new BuildEvent(this);
         event.setException(exception);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.buildFinished(event);
         }
     }
 
     /**
      * Sends a "subbuild started" event to the build listeners for
      * this project.
      *
      * @since Ant 1.6.2
      */
     public void fireSubBuildStarted() {
         BuildEvent event = new BuildEvent(this);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             Object listener = iter.next();
             if (listener instanceof SubBuildListener) {
                 ((SubBuildListener) listener).subBuildStarted(event);
             }
         }
     }
 
     /**
      * Sends a "subbuild finished" event to the build listeners for
      * this project.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      *
      * @since Ant 1.6.2
      */
     public void fireSubBuildFinished(Throwable exception) {
         BuildEvent event = new BuildEvent(this);
         event.setException(exception);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             Object listener = iter.next();
             if (listener instanceof SubBuildListener) {
                 ((SubBuildListener) listener).subBuildFinished(event);
             }
         }
     }
 
     /**
      * Sends a "target started" event to the build listeners for this project.
      *
      * @param target The target which is starting to build.
      *               Must not be <code>null</code>.
      */
     protected void fireTargetStarted(Target target) {
         BuildEvent event = new BuildEvent(target);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.targetStarted(event);
         }
     }
 
     /**
      * Sends a "target finished" event to the build listeners for this
      * project.
      *
      * @param target    The target which has finished building.
      *                  Must not be <code>null</code>.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     protected void fireTargetFinished(Target target, Throwable exception) {
         BuildEvent event = new BuildEvent(target);
         event.setException(exception);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.targetFinished(event);
         }
     }
 
     /**
      * Sends a "task started" event to the build listeners for this project.
      *
      * @param task The target which is starting to execute.
      *               Must not be <code>null</code>.
      */
     protected void fireTaskStarted(Task task) {
         // register this as the current task on the current thread.
         registerThreadTask(Thread.currentThread(), task);
         BuildEvent event = new BuildEvent(task);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.taskStarted(event);
         }
     }
 
     /**
      * Sends a "task finished" event to the build listeners for this
      * project.
      *
      * @param task      The task which has finished executing.
      *                  Must not be <code>null</code>.
      * @param exception an exception indicating a reason for a build
      *                  failure. May be <code>null</code>, indicating
      *                  a successful build.
      */
     protected void fireTaskFinished(Task task, Throwable exception) {
         registerThreadTask(Thread.currentThread(), null);
         System.out.flush();
         System.err.flush();
         BuildEvent event = new BuildEvent(task);
         event.setException(exception);
         Iterator iter = listeners.iterator();
         while (iter.hasNext()) {
             BuildListener listener = (BuildListener) iter.next();
             listener.taskFinished(event);
         }
     }
 
     /**
      * Sends a "message logged" event to the build listeners for this project.
      *
      * @param event    The event to send. This should be built up with the
      *                 appropriate task/target/project by the caller, so that
      *                 this method can set the message and priority, then send
      *                 the event. Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     private void fireMessageLoggedEvent(BuildEvent event, String message,
                                         int priority) {
 
         if (message.endsWith(StringUtils.LINE_SEP)) {
             int endIndex = message.length() - StringUtils.LINE_SEP.length();
             event.setMessage(message.substring(0, endIndex), priority);
         } else {
             event.setMessage(message, priority);
         }
         synchronized (this) {
             if (loggingMessage) {
                 throw new BuildException("Listener attempted to access "
                     + (priority == MSG_ERR ? "System.err" : "System.out")
                     + " - infinite loop terminated");
             }
             try {
                 loggingMessage = true;
                 Iterator iter = listeners.iterator();
                 while (iter.hasNext()) {
                     BuildListener listener = (BuildListener) iter.next();
                     listener.messageLogged(event);
                 }
             } finally {
                 loggingMessage = false;
             }
         }
     }
 
     /**
      * Sends a "message logged" project level event to the build listeners for
      * this project.
      *
      * @param project  The project generating the event.
      *                 Should not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Project project, String message,
                                      int priority) {
         BuildEvent event = new BuildEvent(project);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Sends a "message logged" target level event to the build listeners for
      * this project.
      *
      * @param target   The target generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Target target, String message,
                                      int priority) {
         BuildEvent event = new BuildEvent(target);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Sends a "message logged" task level event to the build listeners for
      * this project.
      *
      * @param task     The task generating the event.
      *                 Must not be <code>null</code>.
      * @param message  The message to send. Should not be <code>null</code>.
      * @param priority The priority of the message.
      */
     protected void fireMessageLogged(Task task, String message, int priority) {
         BuildEvent event = new BuildEvent(task);
         fireMessageLoggedEvent(event, message, priority);
     }
 
     /**
      * Register a task as the current task for a thread.
      * If the task is null, the thread's entry is removed.
      *
      * @param thread the thread on which the task is registered.
      * @param task the task to be registered.
      * @since Ant 1.5
      */
     public synchronized void registerThreadTask(Thread thread, Task task) {
         if (task != null) {
             threadTasks.put(thread, task);
             threadGroupTasks.put(thread.getThreadGroup(), task);
         } else {
             threadTasks.remove(thread);
             threadGroupTasks.remove(thread.getThreadGroup());
         }
     }
 
     /**
      * Get the current task associated with a thread, if any
      *
      * @param thread the thread for which the task is required.
      * @return the task which is currently registered for the given thread or
      *         null if no task is registered.
      */
     public Task getThreadTask(Thread thread) {
         Task task = (Task) threadTasks.get(thread);
         if (task == null) {
             ThreadGroup group = thread.getThreadGroup();
             while (task == null && group != null) {
                 task = (Task) threadGroupTasks.get(group);
                 group = group.getParent();
             }
         }
         return task;
     }
 
 
     // Should move to a separate public class - and have API to add
     // listeners, etc.
     private static class AntRefTable extends Hashtable {
         private Project project;
 
         public AntRefTable(Project project) {
             super();
             this.project = project;
         }
 
         /** Returns the unmodified original object.
          * This method should be called internally to
          * get the 'real' object.
          * The normal get method will do the replacement
          * of UnknownElement ( this is similar with the JDNI
          * refs behavior )
          */
         public Object getReal(Object key) {
             return super.get(key);
         }
 
         /** Get method for the reference table.
          *  It can be used to hook dynamic references and to modify
          * some references on the fly - for example for delayed
          * evaluation.
          *
          * It is important to make sure that the processing that is
          * done inside is not calling get indirectly.
          *
          * @param key
          * @return
          */
         public Object get(Object key) {
             //System.out.println("AntRefTable.get " + key);
             Object o = getReal(key);
             if (o instanceof UnknownElement) {
                 // Make sure that
                 UnknownElement ue = (UnknownElement) o;
                 ue.maybeConfigure();
                 o = ue.getRealThing();
             }
             return o;
         }
     }
 
     /**
      * Set a reference to this Project on the parameterized object.
      * Need to set the project before other set/add elements
      * are called
      * @param obj the object to invoke setProject(this) on
      */
     public final void setProjectReference(final Object obj) {
         if (obj instanceof ProjectComponent) {
             ((ProjectComponent) obj).setProject(this);
             return;
         }
         try {
             Method method =
                 obj.getClass().getMethod(
                     "setProject", new Class[] {Project.class});
             if (method != null) {
                 method.invoke(obj, new Object[] {this});
             }
         } catch (Throwable e) {
             // ignore this if the object does not have
             // a set project method or the method
             // is private/protected.
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Ant.java b/src/main/org/apache/tools/ant/taskdefs/Ant.java
index 4d9131386..3a06830a6 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Ant.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Ant.java
@@ -1,694 +1,761 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.reflect.Method;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Vector;
 import java.util.Set;
 import java.util.HashSet;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.BuildListener;
 import org.apache.tools.ant.DefaultLogger;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.ProjectComponent;
 import org.apache.tools.ant.ProjectHelper;
 import org.apache.tools.ant.Target;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Build a sub-project.
  *
  *  <pre>
  *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
  *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
  *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
  *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
  *    &lt;/ant&gt;</SPAN>
  *  &lt;/target&gt;</SPAN>
  *
  *  &lt;target name=&quot;bar&quot; depends=&quot;init&quot;&gt;
  *    &lt;echo message=&quot;prop is ${property1} ${foo}&quot; /&gt;
  *  &lt;/target&gt;
  * </pre>
  *
  *
  * @since Ant 1.1
  *
  * @ant.task category="control"
  */
 public class Ant extends Task {
 
     /** the basedir where is executed the build file */
     private File dir = null;
 
     /**
      * the build.xml file (can be absolute) in this case dir will be
      * ignored
      */
     private String antFile = null;
 
-    /** the target to call if any */
-    private String target = null;
-
     /** the output */
     private String output  = null;
 
     /** should we inherit properties from the parent ? */
     private boolean inheritAll = true;
 
     /** should we inherit references from the parent ? */
     private boolean inheritRefs = false;
 
     /** the properties to pass to the new project */
     private Vector properties = new Vector();
 
     /** the references to pass to the new project */
     private Vector references = new Vector();
 
     /** the temporary project created to run the build file */
     private Project newProject;
 
     /** The stream to which output is to be written. */
     private PrintStream out = null;
 
     /** the sets of properties to pass to the new project */
     private Vector propertySets = new Vector();
 
+    /** the targets to call on the new project */
+    private Vector targets = new Vector();
+
+    /** whether the target attribute was specified **/
+    private boolean targetAttributeSet = false;
+
     /**
      * If true, pass all properties to the new Ant project.
      * Defaults to true.
      * @param value if true pass all properties to the new Ant project.
      */
     public void setInheritAll(boolean value) {
         inheritAll = value;
     }
 
     /**
      * If true, pass all references to the new Ant project.
      * Defaults to false.
      * @param value if true, pass all references to the new Ant project
      */
     public void setInheritRefs(boolean value) {
         inheritRefs = value;
     }
 
     /**
      * Creates a Project instance for the project to call.
      */
     public void init() {
         newProject = new Project();
         newProject.setDefaultInputStream(getProject().getDefaultInputStream());
         newProject.setJavaVersionProperty();
     }
 
     /**
      * Called in execute or createProperty if newProject is null.
      *
      * <p>This can happen if the same instance of this task is run
      * twice as newProject is set to null at the end of execute (to
      * save memory and help the GC).</p>
      * <p>calls init() again</p>
      *
      */
     private void reinit() {
         init();
     }
 
     /**
      * Attaches the build listeners of the current project to the new
      * project, configures a possible logfile, transfers task and
      * data-type definitions, transfers properties (either all or just
      * the ones specified as user properties to the current project,
      * depending on inheritall), transfers the input handler.
      */
     private void initializeProject() {
         newProject.setInputHandler(getProject().getInputHandler());
 
         Iterator iter = getBuildListeners();
         while (iter.hasNext()) {
             newProject.addBuildListener((BuildListener) iter.next());
         }
 
         if (output != null) {
             File outfile = null;
             if (dir != null) {
                 outfile = FileUtils.newFileUtils().resolveFile(dir, output);
             } else {
                 outfile = getProject().resolveFile(output);
             }
             try {
                 out = new PrintStream(new FileOutputStream(outfile));
                 DefaultLogger logger = new DefaultLogger();
                 logger.setMessageOutputLevel(Project.MSG_INFO);
                 logger.setOutputPrintStream(out);
                 logger.setErrorPrintStream(out);
                 newProject.addBuildListener(logger);
             } catch (IOException ex) {
                 log("Ant: Can't set output to " + output);
             }
         }
 
         getProject().initSubProject(newProject);
 
         // set user-defined properties
         getProject().copyUserProperties(newProject);
 
         if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            newProject.setSystemProperties();
 
         } else {
             // set all properties from calling project
             addAlmostAll(getProject().getProperties());
         }
 
         Enumeration e = propertySets.elements();
         while (e.hasMoreElements()) {
             PropertySet ps = (PropertySet) e.nextElement();
             addAlmostAll(ps.getProperties());
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @param output a line of output
      * @since Ant 1.5
      */
     public void handleOutput(String output) {
         if (newProject != null) {
             newProject.demuxOutput(output, false);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * Process input into the ant task
      *
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read
      *
      * @return the number of bytes read
      *
      * @exception IOException if the data cannot be read
      *
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6
      */
     public int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (newProject != null) {
             return newProject.demuxInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @param output The output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5.2
      */
     public void handleFlush(String output) {
         if (newProject != null) {
             newProject.demuxFlush(output, false);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5
      */
     public void handleErrorOutput(String output) {
         if (newProject != null) {
             newProject.demuxOutput(output, true);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @param output The error output to log. Should not be <code>null</code>.
      *
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String output) {
         if (newProject != null) {
             newProject.demuxFlush(output, true);
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     /**
      * Do the execution.
      * @throws BuildException if a target tries to call itself
      * probably also if a BuildException is thrown by the new project
      */
     public void execute() throws BuildException {
         File savedDir = dir;
         String savedAntFile = antFile;
-        String savedTarget = target;
+        Vector locals = new Vector(targets);
         try {
             if (newProject == null) {
                 reinit();
             }
 
             if ((dir == null) && (inheritAll)) {
                 dir = getProject().getBaseDir();
             }
 
             initializeProject();
 
             if (dir != null) {
                 newProject.setBaseDir(dir);
                 if (savedDir != null) {
                     // has been set explicitly
                     newProject.setInheritedProperty("basedir" ,
                                                     dir.getAbsolutePath());
                 }
             } else {
                 dir = getProject().getBaseDir();
             }
 
             overrideProperties();
 
             if (antFile == null) {
                 antFile = "build.xml";
             }
 
             File file = FileUtils.newFileUtils().resolveFile(dir, antFile);
             antFile = file.getAbsolutePath();
 
-            log("calling target " + (target != null ? target : "[default]")
-                    + " in build file " +  antFile, Project.MSG_VERBOSE);
+            log("calling target(s) "
+                + ((locals.size() == 0) ? locals.toString() : "[default]")
+                + " in build file " + antFile, Project.MSG_VERBOSE);
             newProject.setUserProperty("ant.file" , antFile);
 
             String thisAntFile = getProject().getProperty("ant.file");
             // Are we trying to call the target in which we are defined (or
             // the build file if this is a top level task)?
             if (thisAntFile != null
                 && newProject.resolveFile(newProject.getProperty("ant.file"))
                 .equals(getProject().resolveFile(thisAntFile)) 
                 && getOwningTarget() != null) {
 
                 if (getOwningTarget().getName().equals("")) {
                     if (getTaskName().equals("antcall")) {
                         throw new BuildException("antcall must not be used at"
                                                  + " the top level.");
                     } else {
                         throw new BuildException(getTaskName() + " task at the"
                                                  + " top level must not invoke"
                                                  + " its own build file.");
                     }
                 }
             }
 
             try {
                 ProjectHelper.configureProject(newProject, new File(antFile));
             } catch (BuildException ex) {
                 throw ProjectHelper.addLocationToBuildException(
                     ex, getLocation());
             }
 
-            if (target == null) {
-                target = newProject.getDefaultTarget();
+            if (locals.size() == 0) {
+                String defaultTarget = newProject.getDefaultTarget();
+                if (defaultTarget != null) {
+                    locals.add(defaultTarget);
+                }
             }
 
             if (newProject.getProperty("ant.file")
                 .equals(getProject().getProperty("ant.file"))
                 && getOwningTarget() != null) {
 
                 String owningTargetName = getOwningTarget().getName();
 
-                if (owningTargetName.equals(target)) {
+                if (locals.contains(owningTargetName)) {
                     throw new BuildException(getTaskName() + " task calling "
                                              + "its own parent target.");
                 } else {
-                    Target other =
-                        (Target) getProject().getTargets().get(target);
-                    if (other != null && other.dependsOn(owningTargetName)) {
+                    boolean circular = false;
+                    for (Iterator it = locals.iterator(); !circular && it.hasNext();) {
+                        Target other = (Target)(getProject().getTargets().get(
+                            (String)(it.next())));
+                        circular |= (other != null
+                            && other.dependsOn(owningTargetName));
+                    }
+                    if (circular) {
                         throw new BuildException(getTaskName()
                                                  + " task calling a target"
                                                  + " that depends on"
                                                  + " its parent target \'"
                                                  + owningTargetName
                                                  + "\'.");
                     }
                 }
             }
 
             addReferences();
 
-            if (target != null && !"".equals(target)) {
+            if (locals.size() > 0 && !(locals.size() == 1 && locals.get(0) == "")) {
                 Throwable t = null;
                 try {
                     log("Entering " + antFile + "...", Project.MSG_VERBOSE);
                     newProject.fireSubBuildStarted();
-                    newProject.executeTarget(target);
+                    String[] nameArray =
+                        (String[])(locals.toArray(new String[locals.size()]));
+
+                    Hashtable targets = newProject.getTargets();
+                    Vector sortedTargets = newProject.topoSort(nameArray, targets);
+
+                    sortedTargets.setSize(sortedTargets.indexOf(targets.get(
+                        locals.lastElement())) + 1);
+                    newProject.executeSortedTargets(sortedTargets);
                 } catch (BuildException ex) {
                     t = ProjectHelper
                         .addLocationToBuildException(ex, getLocation());
                     throw (BuildException) t;
                 } finally {
                     log("Exiting " + antFile + ".", Project.MSG_VERBOSE);
                     newProject.fireSubBuildFinished(t);
                 }
             }
         } finally {
             // help the gc
             newProject = null;
             Enumeration e = properties.elements();
             while (e.hasMoreElements()) {
                 Property p = (Property) e.nextElement();
                 p.setProject(null);
             }
 
             if (output != null && out != null) {
                 try {
                     out.close();
                 } catch (final Exception ex) {
                     //ignore
                 }
             }
             dir = savedDir;
             antFile = savedAntFile;
-            target = savedTarget;
         }
     }
 
     /**
      * Override the properties in the new project with the one
      * explicitly defined as nested elements here.
      * @throws BuildException under unknown circumstances
      */
     private void overrideProperties() throws BuildException {
         // remove duplicate properties - last property wins
         // Needed for backward compatibility
         Set set = new HashSet();
         for (int i = properties.size() - 1; i >= 0; --i) {
             Property p = (Property) properties.get(i);
             if (p.getName() != null && !p.getName().equals("")) {
                 if (set.contains(p.getName())) {
                     properties.remove(i);
                 } else {
                     set.add(p.getName());
                 }
             }
         }
         Enumeration e = properties.elements();
         while (e.hasMoreElements()) {
             Property p = (Property) e.nextElement();
             p.setProject(newProject);
             p.execute();
         }
         getProject().copyInheritedProperties(newProject);
     }
 
     /**
      * Add the references explicitly defined as nested elements to the
      * new project.  Also copy over all references that don't override
      * existing references in the new project if inheritrefs has been
      * requested.
      * @throws BuildException if a reference does not have a refid
      */
     private void addReferences() throws BuildException {
         Hashtable thisReferences
             = (Hashtable) getProject().getReferences().clone();
         Hashtable newReferences = newProject.getReferences();
         Enumeration e;
         if (references.size() > 0) {
             for (e = references.elements(); e.hasMoreElements();) {
                 Reference ref = (Reference) e.nextElement();
                 String refid = ref.getRefId();
                 if (refid == null) {
                     throw new BuildException("the refid attribute is required"
                                              + " for reference elements");
                 }
                 if (!thisReferences.containsKey(refid)) {
                     log("Parent project doesn't contain any reference '"
                         + refid + "'",
                         Project.MSG_WARN);
                     continue;
                 }
 
                 thisReferences.remove(refid);
                 String toRefid = ref.getToRefid();
                 if (toRefid == null) {
                     toRefid = refid;
                 }
                 copyReference(refid, toRefid);
             }
         }
 
         // Now add all references that are not defined in the
         // subproject, if inheritRefs is true
         if (inheritRefs) {
             for (e = thisReferences.keys(); e.hasMoreElements();) {
                 String key = (String) e.nextElement();
                 if (newReferences.containsKey(key)) {
                     continue;
                 }
                 copyReference(key, key);
             }
         }
     }
 
     /**
      * Try to clone and reconfigure the object referenced by oldkey in
      * the parent project and add it to the new project with the key
      * newkey.
      *
      * <p>If we cannot clone it, copy the referenced object itself and
      * keep our fingers crossed.</p>
      */
     private void copyReference(String oldKey, String newKey) {
         Object orig = getProject().getReference(oldKey);
         if (orig == null) {
             log("No object referenced by " + oldKey + ". Can't copy to "
                 + newKey,
                 Project.MSG_WARN);
             return;
         }
 
         Class c = orig.getClass();
         Object copy = orig;
         try {
             Method cloneM = c.getMethod("clone", new Class[0]);
             if (cloneM != null) {
                 copy = cloneM.invoke(orig, new Object[0]);
                 log("Adding clone of reference " + oldKey, Project.MSG_DEBUG);
             }
         } catch (Exception e) {
             // not Clonable
         }
 
 
         if (copy instanceof ProjectComponent) {
             ((ProjectComponent) copy).setProject(newProject);
         } else {
             try {
                 Method setProjectM =
                     c.getMethod("setProject", new Class[] {Project.class});
                 if (setProjectM != null) {
                     setProjectM.invoke(copy, new Object[] {newProject});
                 }
             } catch (NoSuchMethodException e) {
                 // ignore this if the class being referenced does not have
                 // a set project method.
             } catch (Exception e2) {
                 String msg = "Error setting new project instance for "
                     + "reference with id " + oldKey;
                 throw new BuildException(msg, e2, getLocation());
             }
         }
         newProject.addReference(newKey, copy);
     }
 
     /**
      * Copies all properties from the given table to the new project -
      * omitting those that have already been set in the new project as
      * well as properties named basedir or ant.file.
      * @param props properties to copy to the new project
      * @since Ant 1.6
      */
     private void addAlmostAll(Hashtable props) {
         Enumeration e = props.keys();
         while (e.hasMoreElements()) {
             String key = e.nextElement().toString();
             if ("basedir".equals(key) || "ant.file".equals(key)) {
                 // basedir and ant.file get special treatment in execute()
                 continue;
             }
 
             String value = props.get(key).toString();
             // don't re-set user properties, avoid the warning message
             if (newProject.getProperty(key) == null) {
                 // no user property
                 newProject.setNewProperty(key, value);
             }
         }
     }
 
     /**
      * The directory to use as a base directory for the new Ant project.
      * Defaults to the current project's basedir, unless inheritall
      * has been set to false, in which case it doesn't have a default
      * value. This will override the basedir setting of the called project.
      * @param d new directory
      */
     public void setDir(File d) {
         this.dir = d;
     }
 
     /**
      * The build file to use.
      * Defaults to "build.xml". This file is expected to be a filename relative
      * to the dir attribute given.
      * @param s build file to use
      */
     public void setAntfile(String s) {
         // @note: it is a string and not a file to handle relative/absolute
         // otherwise a relative file will be resolved based on the current
         // basedir.
         this.antFile = s;
     }
 
     /**
      * The target of the new Ant project to execute.
      * Defaults to the new project's default target.
      * @param s target to invoke
      */
     public void setTarget(String s) {
         if (s.equals("")) {
             throw new BuildException("target attribute must not be empty");
         }
 
-        this.target = s;
+        targets.add(s);
+        targetAttributeSet = true;
     }
 
     /**
      * Filename to write the output to.
      * This is relative to the value of the dir attribute
      * if it has been set or to the base directory of the
      * current project otherwise.
      * @param s file to which the output should go to
      */
     public void setOutput(String s) {
         this.output = s;
     }
 
     /**
      * Property to pass to the new project.
      * The property is passed as a 'user property'
      * @return new property created
      */
     public Property createProperty() {
         if (newProject == null) {
             reinit();
         }
         Property p = new Property(true, getProject());
         p.setProject(newProject);
         p.setTaskName("property");
         properties.addElement(p);
         return p;
     }
 
     /**
      * Reference element identifying a data type to carry
      * over to the new project.
      * @param r reference to add
      */
     public void addReference(Reference r) {
         references.addElement(r);
     }
 
     /**
+     * Add a target to this Ant invocation.
+     * @param target   the <CODE>TargetElement</CODE> to add.
+     * @since Ant 1.7
+     */
+    public void addConfiguredTarget(TargetElement t) {
+        if (targetAttributeSet) {
+            throw new BuildException(
+                "nested target is incompatible with the target attribute");
+        }
+        String name = t.getName();
+        if (name.equals("")) {
+            throw new BuildException("target name must not be empty");
+        }
+        targets.add(name);
+    }
+
+    /**
      * Set of properties to pass to the new project.
      *
      * @param ps property set to add
      * @since Ant 1.6
      */
     public void addPropertyset(PropertySet ps) {
         propertySets.addElement(ps);
     }
 
     /**
      * @since Ant 1.6.2
      */
     private Iterator getBuildListeners() {
         return getProject().getBuildListeners().iterator();
     }
 
     /**
      * Helper class that implements the nested &lt;reference&gt;
      * element of &lt;ant&gt; and &lt;antcall&gt;.
      */
     public static class Reference
         extends org.apache.tools.ant.types.Reference {
 
         /** Creates a reference to be configured by Ant */
         public Reference() {
             super();
         }
 
         private String targetid = null;
 
         /**
          * Set the id that this reference to be stored under in the
          * new project.
          *
          * @param targetid the id under which this reference will be passed to
          *        the new project */
         public void setToRefid(String targetid) {
             this.targetid = targetid;
         }
 
         /**
          * Get the id under which this reference will be stored in the new
          * project
          *
          * @return the id of the reference in the new project.
          */
         public String getToRefid() {
             return targetid;
         }
     }
+
+    /**
+     * Helper class that implements the nested &lt;target&gt;
+     * element of &lt;ant&gt; and &lt;antcall&gt;.
+     * @since Ant 1.7
+     */
+    public static class TargetElement {
+        private String name;
+
+        /**
+         * Default constructor.
+         */
+        public TargetElement() {}
+
+        /**
+         * Set the name of this TargetElement.
+         * @param name   the <CODE>String</CODE> target name.
+         */
+        public void setName(String name) {
+            this.name = name;
+        }
+
+        /**
+         * Get the name of this TargetElement.
+         * @return <CODE>String</CODE>.
+         */
+        public String getName() {
+            return name;
+        }
+    }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/CallTarget.java b/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
index abf108063..a0e17f971 100644
--- a/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
+++ b/src/main/org/apache/tools/ant/taskdefs/CallTarget.java
@@ -1,214 +1,232 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Task;
 import java.io.IOException;
 
 /**
  * Call another target in the same project.
  *
  *  <pre>
  *    &lt;target name="foo"&gt;
  *      &lt;antcall target="bar"&gt;
  *        &lt;param name="property1" value="aaaaa" /&gt;
  *        &lt;param name="foo" value="baz" /&gt;
  *       &lt;/antcall&gt;
  *    &lt;/target&gt;
  *
  *    &lt;target name="bar" depends="init"&gt;
  *      &lt;echo message="prop is ${property1} ${foo}" /&gt;
  *    &lt;/target&gt;
  * </pre>
  *
  * <p>This only works as expected if neither property1 nor foo are
  * defined in the project itself.
  *
  *
  * @since Ant 1.2
  *
  * @ant.task name="antcall" category="control"
  */
 public class CallTarget extends Task {
 
     private Ant callee;
-    private String subTarget;
     // must match the default value of Ant#inheritAll
     private boolean inheritAll = true;
     // must match the default value of Ant#inheritRefs
     private boolean inheritRefs = false;
 
+    private boolean targetSet = false;
+
     /**
      * If true, pass all properties to the new Ant project.
      * Defaults to true.
      */
     public void setInheritAll(boolean inherit) {
        inheritAll = inherit;
     }
 
     /**
      * If true, pass all references to the new Ant project.
      * Defaults to false
      * @param inheritRefs new value
      */
     public void setInheritRefs(boolean inheritRefs) {
         this.inheritRefs = inheritRefs;
     }
 
     /**
      * init this task by creating new instance of the ant task and
      * configuring it's by calling its own init method.
      */
     public void init() {
         callee = (Ant) getProject().createTask("ant");
         callee.setOwningTarget(getOwningTarget());
         callee.setTaskName(getTaskName());
         callee.setLocation(getLocation());
         callee.init();
     }
 
     /**
      * hand off the work to the ant task of ours, after setting it up
      * @throws BuildException on validation failure or if the target didn't
      * execute
      */
     public void execute() throws BuildException {
         if (callee == null) {
             init();
         }
 
-        if (subTarget == null) {
-            throw new BuildException("Attribute target is required.",
-                                     getLocation());
+        if (!targetSet) {
+            throw new BuildException(
+                "Attribute target or at least one nested target is required.",
+                 getLocation());
         }
 
         callee.setAntfile(getProject().getProperty("ant.file"));
-        callee.setTarget(subTarget);
         callee.setInheritAll(inheritAll);
         callee.setInheritRefs(inheritRefs);
         callee.execute();
     }
 
     /**
      * Property to pass to the invoked target.
      */
     public Property createParam() {
         if (callee == null) {
             init();
         }
         return callee.createProperty();
     }
 
     /**
      * Reference element identifying a data type to carry
      * over to the invoked target.
      * @since Ant 1.5
      */
     public void addReference(Ant.Reference r) {
         if (callee == null) {
             init();
         }
         callee.addReference(r);
     }
 
     /**
      * Set of properties to pass to the new project.
      *
      * @since Ant 1.6
      */
     public void addPropertyset(org.apache.tools.ant.types.PropertySet ps) {
         if (callee == null) {
             init();
         }
         callee.addPropertyset(ps);
     }
 
     /**
      * Target to execute, required.
      */
     public void setTarget(String target) {
-        subTarget = target;
+        if (callee == null) {
+            init();
+        }
+        callee.setTarget(target);
+        targetSet = true;
+    }
+
+    /**
+     * Target element identifying a data type to carry
+     * over to the invoked target.
+     * @since Ant 1.6.2
+     */
+    public void addConfiguredTarget(Ant.TargetElement t) {
+        if (callee == null) {
+            init();
+        }
+        callee.addConfiguredTarget(t);
+        targetSet = true;
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @since Ant 1.5
      */
     public void handleOutput(String output) {
         if (callee != null) {
             callee.handleOutput(output);
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * @see Task#handleInput(byte[], int, int)
      *
      * @since Ant 1.6
      */
     public int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (callee != null) {
             return callee.handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
     /**
      * Pass output sent to System.out to the new project.
      *
      * @since Ant 1.5.2
      */
     public void handleFlush(String output) {
         if (callee != null) {
             callee.handleFlush(output);
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project.
      *
      * @since Ant 1.5
      */
     public void handleErrorOutput(String output) {
         if (callee != null) {
             callee.handleErrorOutput(output);
         } else {
             super.handleErrorOutput(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the new project and flush stream.
      *
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String output) {
         if (callee != null) {
             callee.handleErrorFlush(output);
         } else {
             super.handleErrorFlush(output);
         }
     }
 }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java b/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
index c144d311a..23c67f98e 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/AntTest.java
@@ -1,496 +1,504 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 
 import junit.framework.AssertionFailedError;
 
 import org.apache.tools.ant.BuildEvent;
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.BuildListener;
 import org.apache.tools.ant.input.InputHandler;
 import org.apache.tools.ant.input.PropertyFileInputHandler;
 import org.apache.tools.ant.types.Path;
 
 /**
  * @version $Revision$
  */
 public class AntTest extends BuildFileTest {
 
     public AntTest(String name) {
         super(name);
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/ant.xml");
     }
 
     public void tearDown() {
         executeTarget("cleanup");
     }
 
     public void test1() {
         expectBuildException("test1", "recursive call");
     }
 
     // target must be specified
     public void test2() {
         expectBuildException("test2", "required argument not specified");
     }
 
     // Should fail since a recursion will occur...
     public void test3() {
         expectBuildException("test1", "recursive call");
     }
 
     public void test4() {
         expectBuildException("test4", "target attribute must not be empty");
     }
 
     public void test4b() {
         expectBuildException("test4b", "target doesn't exist");
     }
 
     public void test5() {
         executeTarget("test5");
     }
 
     public void test6() {
         executeTarget("test6");
     }
 
     public void testExplicitBasedir1() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("..");
         testBaseDirs("explicitBasedir1",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testExplicitBasedir2() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("..");
         testBaseDirs("explicitBasedir2",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testInheritBasedir() {
         String basedir = getProjectDir().getAbsolutePath();
         testBaseDirs("inheritBasedir", new String[] {basedir, basedir});
     }
 
     public void testDoNotInheritBasedir() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("ant");
         String basedir = getProjectDir().getAbsolutePath();
         testBaseDirs("doNotInheritBasedir",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath()
                      });
     }
 
     public void testBasedirTripleCall() {
         File dir1 = getProjectDir();
         File dir2 = project.resolveFile("ant");
         testBaseDirs("tripleCall",
                      new String[] {dir1.getAbsolutePath(),
                                    dir2.getAbsolutePath(),
                                    dir1.getAbsolutePath()
                      });
     }
 
     protected void testBaseDirs(String target, String[] dirs) {
         BasedirChecker bc = new BasedirChecker(dirs);
         project.addBuildListener(bc);
         executeTarget(target);
         AssertionFailedError ae = bc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(bc);
     }
 
     public void testReferenceInheritance() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         project.addReference("no-override", p);
         testReference("testInherit", new String[] {"path", "path"},
                       new boolean[] {true, true}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {true, false}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {false, false}, null);
     }
 
     public void testReferenceNoInheritance() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         project.addReference("no-override", p);
         testReference("testNoInherit", new String[] {"path", "path"},
                       new boolean[] {true, false}, p);
         testReference("testNoInherit", new String[] {"path", "path"},
                       new boolean[] {false, true}, null);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {true, false}, p);
         testReference("testInherit",
                       new String[] {"no-override", "no-override"},
                       new boolean[] {false, false}, null);
     }
 
     public void testReferenceRename() {
         Path p = Path.systemClasspath;
         p.setProject(project);
         project.addReference("path", p);
         testReference("testRename", new String[] {"path", "path"},
                       new boolean[] {true, false}, p);
         testReference("testRename", new String[] {"path", "path"},
                       new boolean[] {false, true}, null);
         testReference("testRename", new String[] {"newpath", "newpath"},
                       new boolean[] {false, true}, p);
     }
 
     protected void testReference(String target, String[] keys,
                                  boolean[] expect, Object value) {
         ReferenceChecker rc = new ReferenceChecker(keys, expect, value);
         project.addBuildListener(rc);
         executeTarget(target);
         AssertionFailedError ae = rc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(rc);
     }
 
     public void testLogfilePlacement() {
         File[] logFiles = new File[] {
             getProject().resolveFile("test1.log"),
             getProject().resolveFile("test2.log"),
             getProject().resolveFile("ant/test3.log"),
             getProject().resolveFile("ant/test4.log")
         };
         for (int i=0; i<logFiles.length; i++) {
             assertTrue(logFiles[i].getName()+" doesn\'t exist",
                        !logFiles[i].exists());
         }
 
         executeTarget("testLogfilePlacement");
 
         for (int i=0; i<logFiles.length; i++) {
             assertTrue(logFiles[i].getName()+" exists",
                        logFiles[i].exists());
         }
     }
 
     public void testInputHandlerInheritance() {
         InputHandler ih = new PropertyFileInputHandler();
         getProject().setInputHandler(ih);
         InputHandlerChecker ic = new InputHandlerChecker(ih);
         getProject().addBuildListener(ic);
         executeTarget("tripleCall");
         AssertionFailedError ae = ic.getError();
         if (ae != null) {
             throw ae;
         }
         getProject().removeBuildListener(ic);
     }
 
     public void testRefId() {
         Path testPath = new Path(project);
         testPath.createPath().setPath(System.getProperty("java.class.path"));
         PropertyChecker pc =
             new PropertyChecker("testprop",
                                 new String[] {null,
                                               testPath.toString()});
         project.addBuildListener(pc);
         executeTarget("testRefid");
         AssertionFailedError ae = pc.getError();
         if (ae != null) {
             throw ae;
         }
         project.removeBuildListener(pc);
     }
 
     public void testUserPropertyWinsInheritAll() {
         getProject().setUserProperty("test", "7");
         expectLogContaining("test-property-override-inheritall-start",
                             "The value of test is 7");
     }
 
     public void testUserPropertyWinsNoInheritAll() {
         getProject().setUserProperty("test", "7");
         expectLogContaining("test-property-override-no-inheritall-start",
                             "The value of test is 7");
     }
 
     public void testOverrideWinsInheritAll() {
         expectLogContaining("test-property-override-inheritall-start",
                             "The value of test is 4");
     }
 
     public void testOverrideWinsNoInheritAll() {
         expectLogContaining("test-property-override-no-inheritall-start",
                             "The value of test is 4");
     }
 
     public void testPropertySet() {
         executeTarget("test-propertyset");
         assertTrue(getLog().indexOf("test1 is ${test1}") > -1);
         assertTrue(getLog().indexOf("test2 is ${test2}") > -1);
         assertTrue(getLog().indexOf("test1.x is 1") > -1);
     }
 
     public void testInfiniteLoopViaDepends() {
         expectBuildException("infinite-loop-via-depends", "recursive call");
     }
 
     public void testMultiSameProperty() {
         expectLog("multi-same-property", "prop is two");
     }
 
     public void testTopLevelTarget() {
         expectLog("topleveltarget", "Hello world");
     }
 
     public void testMultiplePropertyFileChildren() {
         PropertyChecker pcBar = new PropertyChecker("bar",
                                                     new String[] {null, "Bar"});
         PropertyChecker pcFoo = new PropertyChecker("foo",
                                                     new String[] {null, "Foo"});
         project.addBuildListener(pcBar);
         project.addBuildListener(pcFoo);
         executeTarget("multiple-property-file-children");
         AssertionFailedError aeBar = pcBar.getError();
         if (aeBar != null) {
             throw aeBar;
         }
         AssertionFailedError aeFoo = pcFoo.getError();
         if (aeFoo != null) {
             throw aeFoo;
         }
         project.removeBuildListener(pcBar);
         project.removeBuildListener(pcFoo);
     }
 
+    public void testBlankTarget() {
+        expectBuildException("blank-target", "target name must not be empty");
+    }
+
+    public void testMultipleTargets() {
+        expectLog("multiple-targets", "tadadctbdbtc");
+    }
+
     private class BasedirChecker implements BuildListener {
         private String[] expectedBasedirs;
         private int calls = 0;
         private AssertionFailedError error;
 
         BasedirChecker(String[] dirs) {
             expectedBasedirs = dirs;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (error == null) {
                 try {
                     assertEquals(expectedBasedirs[calls++],
                                  event.getProject().getBaseDir().getAbsolutePath());
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class ReferenceChecker implements BuildListener {
         private String[] keys;
         private boolean[] expectSame;
         private Object value;
         private int calls = 0;
         private AssertionFailedError error;
 
         ReferenceChecker(String[] keys, boolean[] expectSame, Object value) {
             this.keys = keys;
             this.expectSame = expectSame;
             this.value = value;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (error == null) {
                 try {
                     String msg =
                         "Call " + calls + " refid=\'" + keys[calls] + "\'";
                     if (value == null) {
                         Object o = event.getProject().getReference(keys[calls]);
                         if (expectSame[calls++]) {
                             assertNull(msg, o);
                         } else {
                             assertNotNull(msg, o);
                         }
                     } else {
                         // a rather convoluted equals() test
                         Path expect = (Path) value;
                         Path received = (Path) event.getProject().getReference(keys[calls]);
                         boolean shouldBeEqual = expectSame[calls++];
                         if (received == null) {
                             assertTrue(msg, !shouldBeEqual);
                         } else {
                             String[] l1 = expect.list();
                             String[] l2 = received.list();
                             if (l1.length == l2.length) {
                                 for (int i=0; i<l1.length; i++) {
                                     if (!l1[i].equals(l2[i])) {
                                         assertTrue(msg, !shouldBeEqual);
                                     }
                                 }
                                 assertTrue(msg, shouldBeEqual);
                             } else {
                                 assertTrue(msg, !shouldBeEqual);
                             }
                         }
                     }
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class InputHandlerChecker implements BuildListener {
         private InputHandler ih;
         private AssertionFailedError error;
 
         InputHandlerChecker(InputHandler value) {
             ih = value;
         }
 
         public void buildStarted(BuildEvent event) {
             check(event);
         }
         public void buildFinished(BuildEvent event) {
             check(event);
         }
         public void targetFinished(BuildEvent event) {
             check(event);
         }
         public void taskStarted(BuildEvent event) {
             check(event);
         }
         public void taskFinished(BuildEvent event) {
             check(event);
         }
         public void messageLogged(BuildEvent event) {
             check(event);
         }
 
         public void targetStarted(BuildEvent event) {
             check(event);
         }
 
         private void check(BuildEvent event) {
             if (error == null) {
                 try {
                     assertNotNull(event.getProject().getInputHandler());
                     assertSame(ih, event.getProject().getInputHandler());
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
     private class PropertyChecker implements BuildListener {
         private String[] expectedValues;
         private String key;
         private int calls = 0;
         private AssertionFailedError error;
 
         PropertyChecker(String key, String[] values) {
             this.key = key;
             this.expectedValues = values;
         }
 
         public void buildStarted(BuildEvent event) {}
         public void buildFinished(BuildEvent event) {}
         public void targetFinished(BuildEvent event){}
         public void taskStarted(BuildEvent event) {}
         public void taskFinished(BuildEvent event) {}
         public void messageLogged(BuildEvent event) {}
 
         public void targetStarted(BuildEvent event) {
             if (event.getTarget().getName().equals("")) {
                 return;
             }
             if (calls >= expectedValues.length) {
                 error = new AssertionFailedError("Unexpected invocation of"
                                                  + " target "
                                                  + event.getTarget().getName());
             }
 
             if (error == null) {
                 try {
                     assertEquals(expectedValues[calls++],
                                  event.getProject().getProperty(key));
                 } catch (AssertionFailedError e) {
                     error = e;
                 }
             }
         }
 
         AssertionFailedError getError() {
             return error;
         }
 
     }
 
 
 }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java b/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
index 433c33e6c..bd2d07a99 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/CallTargetTest.java
@@ -1,61 +1,69 @@
 /*
  * Copyright  2003-2004 The Apache Software Foundation
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.util.Vector;
 
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.JavaEnvUtils;
 
 /**
  */
 public class CallTargetTest extends BuildFileTest {
 
     public CallTargetTest(String name) {
         super(name);
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/calltarget.xml");
     }
 
     // see bugrep 21724 (references not passing through with antcall)
     public void testInheritRefFileSet() {
         expectLogContaining("testinheritreffileset", "calltarget.xml");
     }
 
     // see bugrep 21724 (references not passing through with antcall)
     public void testInheritFilterset() {
         project.executeTarget("testinheritreffilterset");
     }
 
     // see bugrep 11418 (In repeated calls to the same target,
     // params will not be passed in)
     public void testMultiCall() {
         Vector v = new Vector();
         v.add("call-multi");
         v.add("call-multi");
         project.executeTargets(v);
         assertLogContaining("multi is SETmulti is SET");
     }
 
+    public void testBlankTarget() {
+        expectBuildException("blank-target", "target name must not be empty");
+    }
+
+    public void testMultipleTargets() {
+        expectLog("multiple-targets", "tadadctbdbtc");
+    }
+
     public void tearDown() {
         project.executeTarget("cleanup");
     }
 }
