diff --git a/build.xml b/build.xml
index 48f813581a..2c8292663b 100644
--- a/build.xml
+++ b/build.xml
@@ -1,463 +1,463 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <project basedir="." default="jar" name="JRuby">
   <description>JRuby is a pure Java implementation of a Ruby interpreter.</description>
 
   <!-- First try to load machine-specific properties. -->
   <property file="build.properties"/>
 
   <!-- And then load the defaults. It seems backwards to set defaults AFTER 
        setting local overrides, but that's how Ant works. -->
   <property file="default.build.properties"/>
 
   <path id="build.classpath">
     <fileset dir="${lib.dir}" includes="*.jar" excludes="jruby.jar"/>
   </path>
 
   <!-- directory that contains emma.jar and emma_ant.jar: -->
   <property name="emma.dir" value="${lib.dir}" />
 
   <path id="emma.classpath">
     <pathelement location="${emma.dir}/emma.jar" />
     <pathelement location="${emma.dir}/emma_ant.jar" />
   </path>
 
   <patternset id="java.src.pattern">
     <include name="**/*.java"/>
     <exclude unless="bsf.present" name="org/jruby/javasupport/bsf/**/*.java"/>
     <exclude unless="jdk1.4+" name="**/XmlAstMarshal.java"/>
     <exclude unless="jdk1.4+" name="**/AstPersistenceDelegates.java"/>
   </patternset>
 
   <patternset id="ruby.src.pattern">
     <include name="**/*.rb"/>
   </patternset>
   
   <patternset id="other.src.pattern">
     <include name="**/*.properties"/>
   </patternset>
 
   <target name="init" description="initialize the build">
     <xmlproperty file="build-config.xml" keepRoot="false" collapseAttributes="true"/>
     <tstamp><format property="build.date" pattern="yyyy-MM-dd"/></tstamp>
     <property environment="env"/>
     <property name="version.ruby" value="${version.ruby.major}.${version.ruby.minor}"/>
     <!-- if ruby.home is not set, use env var -->
     <condition property="ruby.home" value="${env.RUBY_HOME}">
       <not><isset property="ruby.home"/></not>
     </condition>
   </target>
 
   <target name="prepare" depends="init" 
     description="Creates the directories needed for building">
     <mkdir dir="${build.dir}"/>
     <mkdir dir="${classes.dir}"/>
     <mkdir dir="${jruby.classes.dir}"/>
     <mkdir dir="${test.classes.dir}"/>
     <mkdir dir="${test.results.dir}"/>
     <mkdir dir="${html.test.results.dir}"/>
     <mkdir dir="${docs.dir}"/>
     <mkdir dir="${api.docs.dir}"/>
   </target>
 
   <target name="ragel" description="Standalone target that generates all our ragel based source files. Requires ragel and rlgen-java to be on the classpath">
     <exec executable="ragel" output="__ragel_out">
       <arg line="-J"/>
       <arg line="${src.dir}/org/jvyamlb/resolver_scanner.rl"/>
     </exec>
     <exec executable="rlgen-java" input="__ragel_out">
       <arg line="-o ${src.dir}/org/jvyamlb/ResolverScanner.java"/>
     </exec>
     <delete file="__ragel_out" quiet="true"/>
   </target>
 
   <target name="check-for-optional-java4-packages"
           description="check if specific libs and versions are avaiable"
           depends="init">
     <available property="jdk1.4+" classname="java.lang.CharSequence"/>
     <available property="jdk1.5+" classname="java.lang.StringBuilder"/>
     <available property="bsf.present" classname="org.apache.bsf.BSFManager"
                classpathref="build.classpath"/>
     <available property="junit.present" classname="junit.framework.TestCase"
                classpathref="build.classpath"/>
     <available property="cglib.present" 
                classname="net.sf.cglib.reflect.FastClass"
                classpathref="build.classpath"/>
   </target>
 
   <target name="check-for-optional-packages" if="jdk1.5+"
           description="check if specific libs and versions are avaiable"
           depends="check-for-optional-java4-packages">
   </target>
 
   <target name="compile.tasks" depends="prepare"
     description="Builds the Ant tasks that we need later on in the build">
     <javac destdir="${jruby.classes.dir}" debug="true" srcdir="${src.dir}"
            includes="org/jruby/util/ant/**/*.java" source="${javac.version}">
       <classpath refid="build.classpath"/>
     </javac>
 
     <taskdef name="jruby-serialize" classname="org.jruby.util.ant.JRubySerialize">
       <classpath path="${jruby.classes.dir}"/>
     </taskdef>
 
     <copy todir="${jruby.classes.dir}">
       <fileset dir="${src.dir}">
         <include name="**/*.properties"/>
         <include name="**/*.rb"/>
         <exclude name="builtin/**/*.rb"/>
       </fileset>
     </copy>
   </target>
 
   <target name="compile-jruby" depends="compile.tasks, check-for-optional-packages">
     <javac destdir="${jruby.classes.dir}" debug="true" source="${javac.version}">
        <classpath refid="build.classpath"/>
        <src path="${src.dir}"/>
        <patternset refid="java.src.pattern"/>
     </javac>
   </target>
 
   <target name="compile" depends="compile-jruby"
           description="Compile the source files for the project.">
   </target>
 
   <target name="serialize" depends="compile"
           description="Serializes builting Ruby libraries into Java format">
     <jruby-serialize destdir="${jruby.classes.dir}">
       <fileset dir="${src.dir}">
         <include name="builtin/**/*.rb"/>
       </fileset>
     </jruby-serialize>
   </target>
 
   <target name="generate-method-classes" depends="serialize">
     <touch file="${build.dir}/__empty.rb"/>
     <java classname="org.jruby.Main" fork="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.dump_invocations" value="${jruby.classes.dir}"/>
       <arg value="-rjava"/>
       <arg value="${build.dir}/__empty.rb"/>
     </java>
     <delete file="${build.dir}/__empty.rb" quiet="true"/>
   </target>
 
   <target name="serialize.rubylib" depends="compile"
           description="Serializes the core Ruby library into Java format">
     <jruby-serialize destdir="${jruby.classes.dir}" verbose="true">
       <fileset dir="${ruby.home}/lib/ruby/1.8">
         <patternset refid="ruby.src.pattern"/>
       </fileset>
     </jruby-serialize>
   </target>
 
   <target name="jar-jruby" depends="generate-method-classes">
-    <jar destfile="${lib.dir}/jruby.jar">
+    <jar destfile="${lib.dir}/jruby.jar" compress="true">
       <fileset dir="${jruby.classes.dir}">
         <exclude name="org/jruby/util/ant/**/*.class"/>
       </fileset>
       <manifest>
         <attribute name="Built-By" value="${user.name}"/>
         <attribute name="Main-Class" value="org.jruby.Main"/>
         <attribute name="Class-Path" value="asm-2.2.3.jar asm-commons-2.2.3.jar"/>
       </manifest>
     </jar>
   </target>
 
 
     <target name="jar-complete" depends="generate-method-classes" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
       <property name="mainclass" value="org.jruby.Main"/>
       <property name="filename" value="jruby-complete.jar"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${lib.dir}/jarjar-0.7.jar"/>
       <jarjar destfile="${lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}">
           <exclude name="org/jruby/util/ant/**/*.class"/>
         </fileset>
         <fileset dir="${basedir}/lib/ruby/1.8">
           <include name="**/*.rb"/>
         </fileset>
         <zipfileset src="${lib.dir}/asm-2.2.3.jar"/>
         <zipfileset src="${lib.dir}/asm-commons-2.2.3.jar"/>
         <zipfileset src="${lib.dir}/jline-0.9.91.jar"/>
       	<zipfileset src="${lib.dir}/backport-util-concurrent.jar"/>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
         <zipfileset dir="${basedir}" prefix="META-INF/jruby.home">
           <include name="bin/*"/>
           <include name="lib/ruby/gems/1.8/cache/sources*.gem"/>
           <include name="lib/ruby/gems/1.8/gems/sources*/**/*"/>
           <include name="lib/ruby/gems/1.8/specifications/sources*.gemspec"/>
           <include name="lib/ruby/site_ruby/**/*"/>
         </zipfileset>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="${mainclass}"/>
         </manifest>
       </jarjar>
     </target>
 
 
     <target name="jar-light" depends="generate-method-classes" description="Create the 'light' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
         <property name="mainclass" value="org.jruby.Main"/>
         <property name="filename" value="jruby-light.jar"/>
         <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${lib.dir}/jarjar-0.7.jar"/>
       <jarjar destfile="${lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}">
           <include name="**/*.class"/>
         <include name="**/*.properties"/>
         <include name="**/*.rb.ast.ser"/>
           <exclude name="org/jruby/util/ant/**/*.class"/>
         </fileset>
         <fileset dir="${basedir}/lib/ruby/1.8">
           <include name="**/*.rb"/>
               <exclude name="**/rdoc/**"/>
               <exclude name="**/cgi/**"/>
               <exclude name="**/bigdecimal/**"/>
               <exclude name="**/drb/**"/>
               <exclude name="**/io/**"/>
               <exclude name="**/net/**"/>
               <exclude name="**/racc/**"/>
               <exclude name="**/rexml/**"/>
               <exclude name="**/rinda/**"/>
               <exclude name="**/runit/**"/>
               <exclude name="**/shell/**"/>
               <exclude name="**/soap/**"/>
               <exclude name="**/test/**"/>
               <exclude name="**/uri/**"/>
               <exclude name="**/webrick/**"/>
               <exclude name="**/xmlrpc/**"/>
               <exclude name="**/xsd/**"/>
         </fileset>
           <zipfileset src="${lib.dir}/asm-2.2.3.jar"/>
           <zipfileset src="${lib.dir}/asm-commons-2.2.3.jar"/>
           <zipfileset src="${lib.dir}/jline-0.9.91.jar"/>
       	  <zipfileset src="${lib.dir}/backport-util-concurrent.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="${mainclass}"/>
         </manifest>
       </jarjar>
     </target>
 
     <target name="jar-console" depends="generate-method-classes" description="Create the jruby graphical console jar">
           <antcall target="jar-light">
               <param name="mainclass" value="org.jruby.demo.IRBConsole"/>
               <param name="filename" value="jruby-console.jar"/>
           </antcall>
     </target>
 
   <target name="jar" depends="jar-jruby" description="Create the jruby.jar file">
   </target>
 
   <target name="jar.standalone" depends="generate-method-classes, serialize.rubylib, jar"
     description="Create a standalone jruby.jar file using libraries from RUBY_HOME/lib/ruby/1.8"/>
 
   <target name="compile-test" depends="jar" description="Compile the unit tests">
     <javac destdir="${test.classes.dir}" deprecation="true" debug="true" 
            source="${javac.version}">
       <classpath>
         <path refid="build.classpath"/>
         <pathelement path="${jruby.classes.dir}"/>
         <pathelement path="${lib.dir}/jruby.jar"/>
       </classpath>
       <src path="${test.dir}"/>
       <patternset refid="java.src.pattern"/>
     </javac>
   </target>
 
   <target name="copy-test-files" depends="compile-test" 
     description="Make tests fails available as resources">
     <copy todir="${test.classes.dir}">
       <fileset dir="${test.dir}" includes="org/**/*.rb"/>
     </copy>
   </target>
 
   <target name="emma" description="turns on EMMA instrumentation/reporting" >
     <available property="emma.present"
                classname="com.vladium.app.IAppVersion"
                classpathref="emma.classpath"/>
     <taskdef resource="emma_ant.properties" classpathref="emma.classpath" />
   
     <property name="emma.enabled" value="true" />
 
     <path id="classes_to_instrument" >
       <pathelement location="${jruby.classes.dir}" />
     </path>   
   </target>
 
   <target name="instrument" if="emma.present">
     <emma enabled="${emma.enabled}" >
       <instr instrpathref="classes_to_instrument"
              destdir="${jruby.instrumented.classes.dir}"  
              metadatafile="${test.results.dir}/metadata.emma"
          merge="false" />
     </emma>
   </target>
 
   <target name="coverage-report" if="emma.present">
     <emma enabled="${emma.enabled}" >
       <report sourcepath="${src.dir}" >
         <fileset dir="${test.results.dir}" >
       <include name="*.emma" />
         </fileset>
         <html outfile="${html.test.coverage.results.dir}/coverage.html" />
       </report>
     </emma>
   </target>
 
   <target name="test" depends="copy-test-files,instrument,run-junit,coverage-report"/>
   <target name="run-junit" description="runs junit tests">
     <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${lib.dir}/junit.jar"/>
     <junit fork="yes" haltonfailure="true" dir="${basedir}" maxmemory="384M">
       <classpath>
         <pathelement location="${jruby.instrumented.classes.dir}" />
         <path refid="build.classpath"/>
         <pathelement path="${java.class.path}"/>
         <pathelement path="${lib.dir}/jruby.jar"/>
         <pathelement location="${test.classes.dir}"/>
         <pathelement path="${test.dir}/requireTest.jar"/>
         <pathelement location="${test.dir}"/>
       </classpath>
       <sysproperty key="java.awt.headless" value="true"/>
       <sysproperty key="jruby.base" value="${basedir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.lib" value="${lib.dir}"/>
       <sysproperty key="emma.coverage.out.file" value="${test.results.dir}/coverage.emma" />
       <sysproperty key="emma.coverage.out.merge" value="true" />
 
       <formatter type="xml"/>
       <formatter type="brief" usefile="false"/>
 
       <test name="org.jruby.test.MainTestSuite" todir="${test.results.dir}"/>
       <test name="org.jruby.test.ScriptTestSuite" todir="${test.results.dir}"/>
       <test name="org.jruby.test.TestUnitTestSuite" todir="${test.results.dir}"/>
       <test name="org.jvyamlb.YAMLLoadTest" todir="${test.results.dir}"/>
       <test name="org.jvyamlb.YAMLDumpTest" todir="${test.results.dir}"/>
     </junit>
 
     <junitreport todir="${test.results.dir}">
       <fileset dir="${test.results.dir}" includes="TEST-*.xml"/>
       <report format="frames" todir="${html.test.results.dir}"/>
     </junitreport>
   </target>
 
   <target name="create-apidocs" depends="prepare" 
           description="Creates the Java API docs">
     <javadoc destdir="${api.docs.dir}" author="true" version="true" use="true" 
              windowtitle="JRuby API" source="${javac.version}">
       <fileset dir="${src.dir}">
         <include name="**/*.java"/>
       </fileset>
       <fileset dir="${test.dir}">
     <include name="**/*.java"/>
       </fileset>
       <doctitle><![CDATA[<h1>JRuby</h1>]]></doctitle>
       <bottom><![CDATA[<i>Copyright &#169; 2002-2006 JRuby Team. All Rights Reserved.</i>]]></bottom>
     </javadoc>
   </target>
 
   <!-- dist-bin and dist-src use a very common tarfileset... Can tarfileset be 
        referenced? -->
   <target name="dist-bin" depends="jar">
     <tar destfile="jruby-bin-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="." mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="." prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
         <include name="samples/**"/>
         <include name="docs/**"/>
         <include name="COPYING*"/>
         <include name="README"/>
         <exclude name="lib/ant.jar"/>
       </tarfileset>
     </tar>
   </target>
 
   <target name="dist-src" depends="jar">
     <tar destfile="jruby-src-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="." mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="." prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
         <include name="samples/**"/>
         <include name="docs/**"/>
         <include name="src/**"/>
         <include name="test/**"/>
         <include name="build.xml"/>
         <include name="build-config.xml"/>
         <include name="COPYING*"/>
         <include name="README"/>
         <include name=".project"/>
         <include name=".classpath"/>
         <include name="default.build.properties"/>
         <exclude name="lib/jruby.jar"/>
       </tarfileset>
     </tar>
   </target>
 
   <target name="dist" depends="dist-bin,dist-src"/>
 
   <target name="clean" depends="init" description="clean almost everything">
     <delete dir="${build.dir}"/>
     <delete quiet="false">
         <fileset dir="${lib.dir}" includes="jruby*.jar"/>
     </delete>
     <delete dir="${api.docs.dir}"/>
   </target>
   
   <target name="profile-gem-install-rake" depends="jar" description="Profile a local gem installation of Rake">
     <fail unless="netbeans.home">This target can only run inside the NetBeans IDE.</fail>
 
     <nbprofiledirect>
         <classpath> <pathelement location="... specify ..."/> </classpath>
     </nbprofiledirect>
 
     <java classname="org.jruby.Main" maxmemory="256M" fork="true">
       <classpath>
         <pathelement location="${jruby.instrumented.classes.dir}" />
         <path refid="build.classpath"/>
         <pathelement path="${lib.dir}/jruby.jar"/>
         <pathelement path="${test.classes.dir}"/>
         <pathelement path="${test.dir}/requireTest.jar"/>
       </classpath>
       <jvmarg value="-Djruby.base=${basedir}"/>
       <jvmarg value="-Djruby.home=${basedir}"/>
       <jvmarg value="-Djruby.lib=${lib.dir}"/>
       <jvmarg value="-Djruby.shell=/bin/sh"/>
       <jvmarg value="-Djruby.script=jruby"/>
       <jvmarg value="${profiler.info.jvmargs.agent}"/>
       <arg value="bin/gem"/>
       <arg value="install"/>
       <arg value="lib/ruby/gems/1.8/cache/rake-0.7.1.gem"/>
     </java>
   </target>
   
   <target name="profile-rails-server" depends="jar" description="Profile a local gem installation of Rake">
     <fail unless="netbeans.home">This target can only run inside the NetBeans IDE.</fail>
 
     <nbprofiledirect>
         <classpath> <pathelement location="... specify ..."/> </classpath>
     </nbprofiledirect>
 
     <java classname="org.jruby.Main" maxmemory="256M" fork="true">
       <classpath>
         <pathelement location="${jruby.instrumented.classes.dir}" />
         <path refid="build.classpath"/>
         <pathelement path="${lib.dir}/jruby.jar"/>
         <pathelement path="${test.classes.dir}"/>
         <pathelement path="${test.dir}/requireTest.jar"/>
       </classpath>
       <jvmarg value="-Djruby.base=${basedir}"/>
       <jvmarg value="-Djruby.home=${basedir}"/>
       <jvmarg value="-Djruby.lib=${lib.dir}"/>
       <jvmarg value="-Djruby.shell=/bin/sh"/>
       <jvmarg value="-Djruby.script=jruby"/>
       <jvmarg value="-Djruby.thread.pooling=true"/>
       <jvmarg value="-server"/>
       <jvmarg value="${profiler.info.jvmargs.agent}"/>
       <arg value="testapp/script/server"/>
     </java>
   </target>
 </project>
diff --git a/src/org/jruby/Main.java b/src/org/jruby/Main.java
index ca9a8f1323..194b14709e 100644
--- a/src/org/jruby/Main.java
+++ b/src/org/jruby/Main.java
@@ -1,326 +1,327 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2005 Jason Voegele <jason@jvoegele.com>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Iterator;
 
 import org.jruby.ast.Node;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.parser.ParserSupport;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CommandlineParser;
 import org.jruby.ast.executable.YARVCompiledRunner;
 
 /**
  * Class used to launch the interpreter.
  * This is the main class as defined in the jruby.mf manifest.
  * It is very basic and does not support yet the same array of switches
  * as the C interpreter.
  *       Usage: java -jar jruby.jar [switches] [rubyfile.rb] [arguments]
  *           -e 'command'    one line of script. Several -e's allowed. Omit [programfile]
  * @author  jpetersen
  */
 public class Main {
     private CommandlineParser commandline;
     private boolean hasPrintedUsage = false;
     private RubyInstanceConfig config;
     // ENEBO: We used to have in, but we do not use it in this class anywhere
     private PrintStream out;
     private PrintStream err;
 
     public Main(RubyInstanceConfig config) {
         this.config = config;
         this.out    = config.getOutput();
         this.err    = config.getError();
     }
 
     public Main(final InputStream in, final PrintStream out, final PrintStream err) {
         this(new RubyInstanceConfig(){{
             setInput(in);
             setOutput(out);
             setError(err);
         }});
     }
 
     public Main() {
         this(new RubyInstanceConfig());
     }
 
     public static void main(String[] args) {
         Main main = new Main();
         int status = main.run(args);
         if (status != 0) {
             System.exit(status);
         }
     }
 
     public int run(String[] args) {
         commandline = new CommandlineParser(this, args);
 
         if (commandline.isShowVersion()) {
             showVersion();
         }
 
         if (! commandline.shouldRunInterpreter()) {
             return 0;
         }
 
         long now = -1;
         if (commandline.isBenchmarking()) {
             now = System.currentTimeMillis();
         }
 
         int status;
 
         try {
             status = runInterpreter(commandline);
         } catch (MainExitException mee) {
             err.println(mee.getMessage());
             if (mee.isUsageError()) {
                 printUsage();
             }
             status = mee.getStatus();
         }
 
         if (commandline.isBenchmarking()) {
             out.println("Runtime: " + (System.currentTimeMillis() - now) + " ms");
         }
 
         return status;
     }
 
     private void showVersion() {
         out.print("ruby ");
         out.print(Constants.RUBY_VERSION);
         out.print(" (");
         out.print(Constants.COMPILE_DATE);
         out.print(") [");
         out.print("java-jruby" + Constants.VERSION);
         out.println("]");
     }
 
     public void printUsage() {
         if (!hasPrintedUsage) {
             out.println("Usage: jruby [switches] [--] [rubyfile.rb] [arguments]");
             out.println("    -e 'command'    one line of script. Several -e's allowed. Omit [programfile]");
             out.println("    -b              benchmark mode, times the script execution");
             out.println("    -Idirectory     specify $LOAD_PATH directory (may be used more than once)");
             out.println("    --              optional -- before rubyfile.rb for compatibility with ruby");
             out.println("    -d              set debugging flags (set $DEBUG to true)");
             out.println("    -v              print version number, then turn on verbose mode");
             out.println("    -O              run with ObjectSpace disabled (improves performance)");
             out.println("    -S cmd          run the specified command in JRuby's bin dir");
             out.println("    -C              pre-compile scripts before running (EXPERIMENTAL)");
             out.println("    -y              read a YARV-compiled Ruby script and run that (EXPERIMENTAL)");
             out.println("    -Y              compile a Ruby script into YARV bytecodes and run this (EXPERIMENTAL)");
             out.println("    --command word  Execute ruby-related shell command (i.e., irb, gem)");
             hasPrintedUsage = true;
         }
     }
 
     private int runInterpreter(CommandlineParser commandline) {
         Reader reader   = commandline.getScriptSource();
         String filename = commandline.displayedFileName();
         config.updateWithCommandline(commandline);
         final Ruby runtime = Ruby.newInstance(config);
         runtime.setKCode(commandline.getKCode());
 
         // Add a shutdown hook that dumps the contents of the runtimeInformation map.
         // This map can be used at development-time to log profiling information
         // that must be updated as the execution runs.
-        Runtime.getRuntime().addShutdownHook(new Thread() {
-            public void run() {
-                if (!runtime.getRuntimeInformation().isEmpty()) {
-                    System.err.println("Runtime information dump:");
-
-                    for (Iterator iter = runtime.getRuntimeInformation().keySet().iterator(); iter.hasNext();) {
-                        Object key = iter.next();
-                        System.err.println("[" + key + "]: " + runtime.getRuntimeInformation().get(key));
+        if (!Ruby.isSecurityRestricted())
+            Runtime.getRuntime().addShutdownHook(new Thread() {
+                public void run() {
+                    if (!runtime.getRuntimeInformation().isEmpty()) {
+                        System.err.println("Runtime information dump:");
+                        
+                        for (Iterator iter = runtime.getRuntimeInformation().keySet().iterator(); iter.hasNext();) {
+                            Object key = iter.next();
+                            System.err.println("[" + key + "]: " + runtime.getRuntimeInformation().get(key));
+                        }
                     }
                 }
-            }
-        });
+            });
 
         try {
             runInterpreter(runtime, reader, filename);
             return 0;
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.RaiseJump) {
                 RubyException raisedException = ((RaiseException)je).getException();
                 if (raisedException.isKindOf(runtime.getClass("SystemExit"))) {
                     RubyFixnum status = (RubyFixnum)raisedException.getInstanceVariable("status");
 
                     if (status != null) {
                         return RubyNumeric.fix2int(status);
                     } else {
                         return 0;
                     }
                 } else {
                     runtime.printError(raisedException);
                     return 1;
                 }
             } else if (je.getJumpType() == JumpException.JumpType.ThrowJump) {
                 return 1;
             } else {
                 throw je;
             }
         } catch(MainExitException e) {
             if(e.isAborted()) {
                 return e.getStatus();
             } else {
                 throw e;
             }
         }
     }
 
     private void runInterpreter(Ruby runtime, Reader reader, String filename) {
         try {
             initializeRuntime(runtime, filename);
             if(commandline.isYARVEnabled()) {
                 new YARVCompiledRunner(runtime,reader,filename).run();
             } else {
                 Node parsedScript = getParsedScript(runtime, reader, filename);
                 if (commandline.isCompilerEnabled()) {
                     runtime.compileAndRun(parsedScript);
                 } else if(commandline.isYARVCompileEnabled()) {
                     runtime.ycompileAndRun(parsedScript);
                 } else {
                     runtime.eval(parsedScript);
                 }
             }
         } finally {
             runtime.tearDown();
         }
     }
 
     private Node getParsedScript(Ruby runtime, Reader reader, String filename) {
         // current scope is top-level scope (what we set TOPLEVEL_BINDING to).
         Node result = runtime.parse(reader, filename, runtime.getCurrentContext().getCurrentScope());
         if (commandline.isAssumePrinting()) {
             result = new ParserSupport().appendPrintToBlock(result);
         }
         if (commandline.isAssumeLoop()) {
             result = new ParserSupport().appendWhileLoopToBlock(result, commandline.isProcessLineEnds(), commandline.isSplit());
         }
         return result;
     }
 
     private void initializeRuntime(final Ruby runtime, String filename) {
         IRubyObject argumentArray = runtime.newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(runtime, commandline.getScriptArguments()));
         runtime.setVerbose(runtime.newBoolean(commandline.isVerbose()));
         runtime.setDebug(runtime.newBoolean(commandline.isDebug()));
 
         defineGlobalVERBOSE(runtime);
         defineGlobalDEBUG(runtime);
 
         runtime.getObject().setConstant("$VERBOSE",
                 commandline.isVerbose() ? runtime.getTrue() : runtime.getNil());
         runtime.defineGlobalConstant("ARGV", argumentArray);
 
         defineGlobal(runtime, "$-p", commandline.isAssumePrinting());
         defineGlobal(runtime, "$-n", commandline.isAssumeLoop());
         defineGlobal(runtime, "$-a", commandline.isSplit());
         defineGlobal(runtime, "$-l", commandline.isProcessLineEnds());
         runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(argumentArray));
         // TODO this is a fake cause we have no real process number in Java
         runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(runtime.hashCode())));
 
         IAccessor d = new ValueAccessor(runtime.newString(filename));
         runtime.getGlobalVariables().define("$PROGRAM_NAME", d);
         runtime.getGlobalVariables().define("$0", d);
 
         runtime.getLoadService().init(commandline.loadPaths());
         Iterator iter = commandline.requiredLibraries().iterator();
         while (iter.hasNext()) {
             String scriptName = (String) iter.next();
             RubyKernel.require(runtime.getTopSelf(), runtime.newString(scriptName), Block.NULL_BLOCK);
         }
     }
 
     private void defineGlobalVERBOSE(final Ruby runtime) {
         // $VERBOSE can be true, false, or nil.  Any non-false-nil value will get stored as true
         runtime.getGlobalVariables().define("$VERBOSE", new IAccessor() {
             public IRubyObject getValue() {
                 return runtime.getVerbose();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 if (newValue.isNil()) {
                     runtime.setVerbose(newValue);
                 } else {
                     runtime.setVerbose(runtime.newBoolean(newValue != runtime.getFalse()));
                 }
 
                 return newValue;
             }
         });
     }
 
     private void defineGlobalDEBUG(final Ruby runtime) {
         IAccessor d = new IAccessor() {
             public IRubyObject getValue() {
                 return runtime.getDebug();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 if (newValue.isNil()) {
                     runtime.setDebug(newValue);
                 } else {
                     runtime.setDebug(runtime.newBoolean(newValue != runtime.getFalse()));
                 }
 
                 return newValue;
             }
             };
         runtime.getGlobalVariables().define("$DEBUG", d);
         runtime.getGlobalVariables().define("$-d", d);
     }
 
     private void defineGlobal(Ruby runtime, String name, boolean value) {
         runtime.getGlobalVariables().defineReadonly(name, new ValueAccessor(value ? runtime.getTrue() : runtime.getNil()));
     }
 }
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 8d545314a3..1722f486d3 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1787 +1,1795 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
 import java.util.WeakHashMap;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.YARVCompiledRunner;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.yarv.StandardYARVCompiler;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.IConvLibrary;
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.libraries.BigDecimalLibrary;
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.libraries.ThreadLibrary;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.ext.Generator;
 import org.jruby.ext.Readline;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.MethodSelectorTable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.BindingMetaClass;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.builtin.meta.ModuleMetaClass;
 import org.jruby.runtime.builtin.meta.ObjectMetaClass;
 import org.jruby.runtime.builtin.meta.ProcMetaClass;
 import org.jruby.runtime.builtin.meta.SymbolMetaClass;
 import org.jruby.runtime.builtin.meta.TimeMetaClass;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "nkf", "yaml/syck" };
 
     private CacheMap cacheMap = new CacheMap(this);
     private ThreadService threadService = new ThreadService(this);
     private Hashtable runtimeInformation;
     private final MethodSelectorTable selectorTable = new MethodSelectorTable();
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable ioHandlers = new Hashtable();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private RubyProc traceFunction;
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private final boolean objectSpaceEnabled;
 
     private long globalState = 1;
 
     /** safe-level:
             0 - strings from streams/environment/ARGV are tainted (default)
             1 - no dangerous operation by tainted value
             2 - process/file operations prohibited
             3 - all genetated objects are tainted
             4 - no global (non-tainted) variable modification/no direct output
     */
     private int safeLevel = 0;
 
     // Default classes/objects
     private IRubyObject nilObject;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     private RubyClass objectClass;
     private RubyClass stringClass;
     private RubyClass systemCallError = null;
     private RubyModule errnoModule = null;
     private IRubyObject topSelf;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private RubyInstanceConfig config;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
     private IRubyObject verbose;
     private IRubyObject debug;
 
     // Java support
     private JavaSupport javaSupport;
     // FIXME: THIS IS WRONG. We need to correct the classloading problems.
-    private static JRubyClassLoader jrubyClassLoader = new JRubyClassLoader(Ruby.class.getClassLoader());
+    private static JRubyClassLoader jrubyClassLoader; // = new JRubyClassLoader(Ruby.class.getClassLoader());
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack atExitBlocks = new Stack();
 
     private RubyModule kernelModule;
 
     private RubyClass nilClass;
 
     private RubyClass fixnumClass;
     
     private RubyClass arrayClass;
     
     private RubyClass hashClass;    
 
     private IRubyObject tmsStruct;
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 0;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
     
     /**
      * A list of finalizers, weakly referenced, to be executed on tearDown
      */
     private Map finalizers;
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static Ruby getDefaultInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured as provided.
      *
      * @param config the instance configuration
      * @return the JRuby runtime
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured with the given input, output and error streams.
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
 
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScript(String script) {
         return eval(parse(script, "<script>", getCurrentContext().getCurrentScope()));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
 
             return EvaluationState.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw newLocalJumpError("unexpected return");
                 //              return (IRubyObject)je.getSecondaryData();
             } else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw newLocalJumpError("unexpected break");
             }
 
             throw je;
         }
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
             Class scriptClass = compiler.loadClass(this);
 
             Script script = (Script)scriptClass.newInstance();
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } else {
                 throw je;
             }
         } catch (ClassNotFoundException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (InstantiationException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (IllegalAccessException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         }
     }
 
     public IRubyObject ycompileAndRun(Node node) {
         try {
             StandardYARVCompiler compiler = new StandardYARVCompiler(this);
             NodeCompilerFactory.getYARVCompiler().compile(node, compiler);
             org.jruby.lexer.yacc.ISourcePosition p = node.getPosition();
             if(p == null && node instanceof org.jruby.ast.RootNode) {
                 p = ((org.jruby.ast.RootNode)node).getBodyNode().getPosition();
             }
             return new YARVCompiledRunner(this,compiler.getInstructionSequence("<main>",p.getFile(),"toplevel")).run();
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } 
                 
             throw je;
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyModule getKernel() {
         return kernelModule;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
 
     public RubyClass getFixnum() {
         return fixnumClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }    
     
     public RubyClass getArray() {
         return arrayClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
 
     /** Returns the "true" instance from the instance pool.
      * @return The "true" instance.
      */
     public RubyBoolean getTrue() {
         return trueObject;
     }
 
     /** Returns the "false" instance from the instance pool.
      * @return The "false" instance.
      */
     public RubyBoolean getFalse() {
         return falseObject;
     }
 
     /** Returns the "nil" singleton instance.
      * @return "nil"
      */
     public IRubyObject getNil() {
         return nilObject;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
 
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /** Returns a class from the instance pool.
      *
      * @param name The name of the class.
      * @return The class.
      */
     public RubyClass getClass(String name) {
         try {
             return objectClass.getClass(name);
         } catch (ClassCastException e) {
             throw newTypeError(name + " is not a Class");
         }
     }
 
     /** Define a new class with name 'name' and super class 'superClass'.
      *
      * MRI: rb_define_class / rb_define_class_id
      *
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass.getCRef());
     }
 
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         if (superClass == null) {
             superClass = objectClass;
         }
 
         return superClass.newSubClass(name, allocator, parentCRef);
     }
 
     /** rb_define_module / rb_define_module_id
      *
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass.getCRef());
     }
 
     public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef) {
         RubyModule newModule = RubyModule.newModule(this, name, parentCRef);
 
         ((RubyModule)parentCRef.getValue()).setConstant(name, newModule);
 
         return newModule;
     }
 
     /**
      * In the current context, get the named module. If it doesn't exist a
      * new module is created.
      */
     public RubyModule getOrCreateModule(String name) {
         ThreadContext tc = getCurrentContext();
         RubyModule module = (RubyModule) tc.getRubyClass().getConstantAt(name);
 
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         }
 
         return module;
     }
 
 
     /** Getter for property securityLevel.
      * @return Value of property securityLevel.
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
     /** Setter for property securityLevel.
      * @param safeLevel New value of property securityLevel.
      */
     public void setSafeLevel(int safeLevel) {
         this.safeLevel = safeLevel;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public void secure(int level) {
         if (level <= safeLevel) {
             throw newSecurityError("Insecure operation '" + getCurrentContext().getFrameName() + "' at level " + safeLevel);
         }
     }
 
     // FIXME moved this hear to get what's obviously a utility method out of IRubyObject.
     // perhaps security methods should find their own centralized home at some point.
     public void checkSafeString(IRubyObject object) {
         if (getSafeLevel() > 0 && object.isTaint()) {
             ThreadContext tc = getCurrentContext();
             if (tc.getFrameName() != null) {
                 throw newSecurityError("Insecure operation - " + tc.getFrameName());
             }
             throw newSecurityError("Insecure operation: -r");
         }
         secure(4);
         if (!(object instanceof RubyString)) {
             throw newTypeError(
                 "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
         }
     }
 
     /**
      * Retrieve mappings of cached methods to where they have been cached.  When a cached
      * method needs to be invalidated this map can be used to remove all places it has been
      * cached.
      *
      * @return the mappings of where cached methods have been stored
      */
     public CacheMap getCacheMap() {
         return cacheMap;
     }
 
     /**
      * @see org.jruby.Ruby#getRuntimeInformation
      */
     public Map getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable() : runtimeInformation;
     }
 
     public MethodSelectorTable getSelectorTable() {
         return selectorTable;
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public IRubyObject getTopConstant(String name) {
         IRubyObject constant = getModule(name);
         if (constant == null) {
             constant = getLoadService().autoload(name);
         }
         return constant;
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** Getter for property rubyTopSelf.
      * @return Value of property rubyTopSelf.
      */
     public IRubyObject getTopSelf() {
         return topSelf;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     /** ruby_init
      *
      */
     // TODO: Figure out real dependencies between vars and reorder/refactor into better methods
     private void init() {
         ThreadContext tc = getCurrentContext();
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
 
         verbose = falseObject;
         debug = falseObject;
         
         // init selector table, now that classes are done adding methods
         selectorTable.init();
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         initBuiltinClasses();
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
     }
 
     private void initLibraries() {
         loadService = new LoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                 }
             });
         
         registerBuiltin("socket.rb", new RubySocket.Service());
         registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
             if(profile.allowBuiltin(BUILTIN_LIBRARIES[i])) {
                 loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
             }
         }
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
         registerBuiltin("stringio.rb", new StringIOLibrary());
         registerBuiltin("strscan.rb", new StringScannerLibrary());
         registerBuiltin("zlib.rb", new ZlibLibrary());
         registerBuiltin("yaml_internal.rb", new YamlLibrary());
         registerBuiltin("enumerator.rb", new EnumeratorLibrary());
         registerBuiltin("generator_internal.rb", new Generator.Service());
         registerBuiltin("readline.rb", new Readline.Service());
         registerBuiltin("thread.so", new ThreadLibrary());
         registerBuiltin("openssl.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("rubygems"));
                     runtime.getTopSelf().callMethod(runtime.getCurrentContext(),"gem",runtime.newString("jruby-openssl"));
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("jopenssl"));
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         ObjectMetaClass objectMetaClass = new ObjectMetaClass(this);
         objectMetaClass.initializeClass();
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = new ModuleMetaClass(this, objectClass);
         objectClass.setConstant("Module", moduleClass);
         RubyClass classClass = RubyClass.newClassClass(this, moduleClass);
         objectClass.setConstant("Class", classClass);
         
         classClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
 
         // I don't think the containment is correct here (parent cref)
         RubyClass metaClass = objectClass.makeMetaClass(classClass, objectMetaClass.getCRef());
         metaClass = moduleClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
         metaClass = classClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
 
         ((ObjectMetaClass) moduleClass).initializeBootstrapClass();
 
         kernelModule = RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         RubyClass.createClassClass(classClass);
 
         nilClass = RubyNil.createNilClass(this);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         
         nilObject = new RubyNil(this);
         trueObject = new RubyBoolean(this, true);
         falseObject = new RubyBoolean(this, false);
         
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         stringClass = RubyString.createStringClass(this);
         new SymbolMetaClass(this).initializeClass();
         if(profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if(profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if(profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if(profile.allowModule("Precision")) {
             RubyPrecision.createPrecisionModule(this);
         }
 
         if(profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
 
         if(profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
 
         if(profile.allowClass("Fixnum")) {
             fixnumClass = RubyFixnum.createFixnumClass(this);
         }
 
         if(profile.allowClass("Hash")) {
             hashClass = RubyHash.createHashClass(this);
         }
         
         new IOMetaClass(this).initializeClass();
 
         if(profile.allowClass("Array")) {
             arrayClass = RubyArray.createArrayClass(this);
         }
 
         RubyClass structClass = null;
         if(profile.allowClass("Struct")) {
             structClass = RubyStruct.createStructClass(this);
         }
 
         if(profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                                                new IRubyObject[] {
                                                    newString("Tms"),
                                                    newSymbol("utime"),
                                                    newSymbol("stime"),
                                                    newSymbol("cutime"),
                                                    newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if(profile.allowClass("Float")) {
            RubyFloat.createFloatClass(this);
         }
 
         if(profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         if(profile.allowClass("Binding")) {
             new BindingMetaClass(this).initializeClass();
         }
 
         if(profile.allowModule("Math")) {
             RubyMath.createMathModule(this); // depends on all numeric types
         }
         if(profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if(profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if(profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if(profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
 
         if(profile.allowClass("Proc")) {
             new ProcMetaClass(this).initializeClass();
         }
 
         if(profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
 
         if(profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if(profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
 
         if(profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
 
         if(profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
 
         if(profile.allowClass("File")) {
             new FileMetaClass(this).initializeClass(); // depends on IO, FileTest
         }
 
         if(profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if(profile.allowClass("Time")) {
             new TimeMetaClass(this).initializeClass();
         }
         if(profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
 
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
         RubyClass rangeError = null;
         if(profile.allowClass("StandardError")) {
             standardError = defineClass("StandardError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if(profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SystemExit")) {
             defineClass("SystemExit", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("Interrupt")) {
             defineClass("Interrupt", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("SignalException")) {
             defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("NoMethodError")) {
             defineClass("NoMethodError", nameError, nameError.getAllocator());
         }
         if(profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError, ioError.getAllocator());
         }
         if(profile.allowClass("LocalJumpError")) {
             defineClass("LocalJumpError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError, standardError.getAllocator());
         }
         // FIXME: Actually this somewhere <- fixed
         if(profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError, rangeError.getAllocator());
         }
         if(profile.allowClass("NativeException")) {
             NativeException.createClass(this, runtimeError);
         }
         if(profile.allowClass("SystemCallError")) {
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if(profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
         }
 
         initErrnoErrors();
 
         if(profile.allowClass("Data")) {
             defineClass("Data", objectClass, objectClass.getAllocator());
         }
 
         if(profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
         }
 
         if(profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     private void initBuiltinClasses() {
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrnoErrors() {
         createSysErr(IErrno.ENOTEMPTY, "ENOTEMPTY");
         createSysErr(IErrno.ERANGE, "ERANGE");
         createSysErr(IErrno.ESPIPE, "ESPIPE");
         createSysErr(IErrno.ENFILE, "ENFILE");
         createSysErr(IErrno.EXDEV, "EXDEV");
         createSysErr(IErrno.ENOMEM, "ENOMEM");
         createSysErr(IErrno.E2BIG, "E2BIG");
         createSysErr(IErrno.ENOENT, "ENOENT");
         createSysErr(IErrno.ENOSYS, "ENOSYS");
         createSysErr(IErrno.EDOM, "EDOM");
         createSysErr(IErrno.ENOSPC, "ENOSPC");
         createSysErr(IErrno.EINVAL, "EINVAL");
         createSysErr(IErrno.EEXIST, "EEXIST");
         createSysErr(IErrno.EAGAIN, "EAGAIN");
         createSysErr(IErrno.ENXIO, "ENXIO");
         createSysErr(IErrno.EILSEQ, "EILSEQ");
         createSysErr(IErrno.ENOLCK, "ENOLCK");
         createSysErr(IErrno.EPIPE, "EPIPE");
         createSysErr(IErrno.EFBIG, "EFBIG");
         createSysErr(IErrno.EISDIR, "EISDIR");
         createSysErr(IErrno.EBUSY, "EBUSY");
         createSysErr(IErrno.ECHILD, "ECHILD");
         createSysErr(IErrno.EIO, "EIO");
         createSysErr(IErrno.EPERM, "EPERM");
         createSysErr(IErrno.EDEADLOCK, "EDEADLOCK");
         createSysErr(IErrno.ENAMETOOLONG, "ENAMETOOLONG");
         createSysErr(IErrno.EMLINK, "EMLINK");
         createSysErr(IErrno.ENOTTY, "ENOTTY");
         createSysErr(IErrno.ENOTDIR, "ENOTDIR");
         createSysErr(IErrno.EFAULT, "EFAULT");
         createSysErr(IErrno.EBADF, "EBADF");
         createSysErr(IErrno.EINTR, "EINTR");
         createSysErr(IErrno.EWOULDBLOCK, "EWOULDBLOCK");
         createSysErr(IErrno.EDEADLK, "EDEADLK");
         createSysErr(IErrno.EROFS, "EROFS");
         createSysErr(IErrno.EMFILE, "EMFILE");
         createSysErr(IErrno.ENODEV, "ENODEV");
         createSysErr(IErrno.EACCES, "EACCES");
         createSysErr(IErrno.ENOEXEC, "ENOEXEC");
         createSysErr(IErrno.ESRCH, "ESRCH");
         createSysErr(IErrno.ECONNREFUSED, "ECONNREFUSED");
         createSysErr(IErrno.ECONNRESET, "ECONNRESET");
         createSysErr(IErrno.EADDRINUSE, "EADDRINUSE");
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             errnoModule.defineClassUnder(name, systemCallError, systemCallError.getAllocator()).defineConstant("Errno", newFixnum(i));
         }
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verbose;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug;
     }
 
     public JavaSupport getJavaSupport() {
         return javaSupport;
     }
 
     public JRubyClassLoader getJRubyClassLoader() {
+        if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null)
+            jrubyClassLoader = new JRubyClassLoader(Ruby.class.getClassLoader());
         return jrubyClassLoader;
     }
 
     /** Defines a global variable
      */
     public void defineVariable(final GlobalVariable variable) {
         globalVariables.define(variable.name(), new IAccessor() {
             public IRubyObject getValue() {
                 return variable.get();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 return variable.set(newValue);
             }
         });
     }
 
     /** defines a readonly global variable
      *
      */
     public void defineReadonlyVariable(String name, IRubyObject value) {
         globalVariables.defineReadonly(name, new ValueAccessor(value));
     }
 
     public Node parse(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope);
     }
 
     public Node parse(String content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope);
     }
 
     public ThreadService getThreadService() {
         return threadService;
     }
 
     public ThreadContext getCurrentContext() {
         return threadService.getCurrentContext();
     }
 
     /**
      * Returns the loadService.
      * @return ILoadService
      */
     public LoadService getLoadService() {
         return loadService;
     }
 
     public RubyWarnings getWarnings() {
         return warnings;
     }
 
     public PrintStream getErrorStream() {
         java.io.OutputStream os = ((RubyIO) getGlobalVariables().get("$stderr")).getOutStream();
         if(null != os) {
             return new PrintStream(os);
         } else {
             return new PrintStream(new org.jruby.util.SwallowingOutputStream());
         }
     }
 
     public InputStream getInputStream() {
         return ((RubyIO) getGlobalVariables().get("$stdin")).getInStream();
     }
 
     public PrintStream getOutputStream() {
         return new PrintStream(((RubyIO) getGlobalVariables().get("$stdout")).getOutStream());
     }
 
     public RubyModule getClassFromPath(String path) {
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
         }
         IRubyObject type = evalScript(path);
         if (!(type instanceof RubyModule)) {
             throw newTypeError("class path " + path + " does not point class");
         }
         return (RubyModule) type;
     }
 
     /** Prints an error with backtrace to the error stream.
      *
      * MRI: eval.c - error_print()
      *
      */
     public void printError(RubyException excp) {
         if (excp == null || excp.isNil()) {
             return;
         }
 
         ThreadContext tc = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(tc, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (tc.getSourceFile() != null) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first(IRubyObject.NULL_ARRAY);
 
             if (mesg.isNil()) {
                 printErrorPos(errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == getClass("RuntimeError") && (info == null || info.length() == 0)) {
             errorStream.print(": unhandled exception\n");
         } else {
             String path = type.getName();
 
             if (info.length() == 0) {
                 errorStream.print(": " + path + '\n');
             } else {
                 if (path.startsWith("#")) {
                     path = null;
                 }
 
                 String tail = null;
                 if (info.indexOf("\n") != -1) {
                     tail = info.substring(info.indexOf("\n") + 1);
                     info = info.substring(0, info.indexOf("\n"));
                 }
 
                 errorStream.print(": " + info);
 
                 if (path != null) {
                     errorStream.print(" (" + path + ")\n");
                 }
 
                 if (tail != null) {
                     errorStream.print(tail + '\n');
                 }
             }
         }
 
         excp.printBacktrace(errorStream);
     }
 
     private void printErrorPos(PrintStream errorStream) {
         ThreadContext tc = getCurrentContext();
         if (tc.getSourceFile() != null) {
             if (tc.getFrameName() != null) {
                 errorStream.print(tc.getPosition());
                 errorStream.print(":in '" + tc.getFrameName() + '\'');
             } else if (tc.getSourceLine() != 0) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceFile());
             }
         }
     }
 
     /** This method compiles and interprets a Ruby script.
      *
      *  It can be used if you want to use JRuby as a Macro language.
      *
      */
     public void loadScript(RubyString scriptName, RubyString source) {
         loadScript(scriptName.toString(), new StringReader(source.toString()));
     }
 
     public void loadScript(String scriptName, Reader source) {
-        File f = new File(scriptName);
-        if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
-            scriptName = "./" + scriptName;
+        if (!Ruby.isSecurityRestricted()) {
+            File f = new File(scriptName);
+            if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
+                scriptName = "./" + scriptName;
+            };
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parse(source, scriptName, null);
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
     public void loadNode(String scriptName, Node node) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
 
     /** Loads, compiles and interprets a Ruby file.
      *  Used by Kernel#require.
      *
      *  @mri rb_load
      */
     public void loadFile(File file) {
         assert file != null : "No such file to load";
         try {
             BufferedReader source = new BufferedReader(new FileReader(file));
             loadScript(file.getPath().replace(File.separatorChar, '/'), source);
             source.close();
         } catch (IOException ioExcptn) {
             throw newIOErrorFromException(ioExcptn);
         }
     }
 
     /** Call the trace function
      *
      * MRI: eval.c - call_trace_func
      *
      */
     public void callTraceFunction(ThreadContext context, String event, ISourcePosition position,
             IRubyObject self, String name, IRubyObject type) {
         if (traceFunction == null) return;
 
         if (!context.isWithinTrace()) {
             context.setWithinTrace(true);
 
             ISourcePosition savePosition = context.getPosition();
             String file = position.getFile();
 
             if (file == null) file = "(ruby)";
             if (type == null) type = getFalse();
 
             context.preTrace();
             try {
                 traceFunction.call(new IRubyObject[] {
                     newString(event), // event name
                     newString(file), // filename
                     newFixnum(position.getStartLine() + 1), // line numbers should be 1-based
                     name != null ? RubySymbol.newSymbol(this, name) : getNil(),
                     self != null ? self : getNil(),
                     type
                 });
             } finally {
                 context.postTrace();
                 context.setPosition(savePosition);
                 context.setWithinTrace(false);
             }
         }
     }
 
     public RubyProc getTraceFunction() {
         return traceFunction;
     }
 
     public void setTraceFunction(RubyProc traceFunction) {
         this.traceFunction = traceFunction;
     }
     public GlobalVariables getGlobalVariables() {
         return globalVariables;
     }
 
     // For JSR 223 support: see http://scripting.java.net/
     public void setGlobalVariables(GlobalVariables globalVariables) {
         this.globalVariables = globalVariables;
     }
 
     public CallbackFactory callbackFactory(Class type) {
         return CallbackFactory.createFactory(this, type);
     }
 
     /**
      * Push block onto exit stack.  When runtime environment exits
      * these blocks will be evaluated.
      *
      * @return the element that was pushed onto stack
      */
     public IRubyObject pushExitBlock(RubyProc proc) {
         atExitBlocks.push(proc);
         return proc;
     }
     
     public void addFinalizer(RubyObject.Finalizer finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap();
             }
         }
         
         synchronized (finalizers) {
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeFinalizer(RubyObject.Finalizer finalizer) {
         if (finalizers != null) {
             synchronized (finalizers) {
                 finalizers.remove(finalizer);
             }
         }
     }
 
     /**
      * Make sure Kernel#at_exit procs get invoked on runtime shutdown.
      * This method needs to be explicitly called to work properly.
      * I thought about using finalize(), but that did not work and I
      * am not sure the runtime will be at a state to run procs by the
      * time Ruby is going away.  This method can contain any other
      * things that need to be cleaned up at shutdown.
      */
     public void tearDown() {
         while (!atExitBlocks.empty()) {
             RubyProc proc = (RubyProc) atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator finalIter = finalizers.keySet().iterator(); finalIter.hasNext();) {
                     ((RubyObject.Finalizer)finalIter.next()).finalize();
                     finalIter.remove();
                 }
             }
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
     public RubyArray newArray() {
         return RubyArray.newArray(this);
     }
 
     public RubyArray newArrayLight() {
         return RubyArray.newArrayLight(this);
     }
 
     public RubyArray newArray(IRubyObject object) {
         return RubyArray.newArray(this, object);
     }
 
     public RubyArray newArray(IRubyObject car, IRubyObject cdr) {
         return RubyArray.newArray(this, car, cdr);
     }
 
     public RubyArray newArray(IRubyObject[] objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return RubyBoolean.newBoolean(this, value);
     }
 
     public RubyFileStat newRubyFileStat(String file) {
         return (RubyFileStat)getClass("File").getClass("Stat").callMethod(getCurrentContext(),"new",newString(file));
     }
 
     public RubyFixnum newFixnum(long value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFloat newFloat(double value) {
         return RubyFloat.newFloat(this, value);
     }
 
     public RubyNumeric newNumeric() {
         return RubyNumeric.newNumeric(this);
     }
 
     public RubyProc newProc(boolean isLambda, Block block) {
         if (!isLambda && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, isLambda);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
 
     public RubyBinding newBinding(Block block) {
         return RubyBinding.newBinding(this, block);
     }
 
     public RubyString newString() {
         return RubyString.newString(this, "");
     }
 
     public RubyString newString(String string) {
         return RubyString.newString(this, string);
     }
     
     public RubyString newString(ByteList byteList) {
         return RubyString.newString(this, byteList);
     }
     
     public RubyString newStringShared(ByteList byteList) {
         return RubyString.newStringShared(this, byteList);
     }    
 
     public RubySymbol newSymbol(String string) {
         return RubySymbol.newSymbol(this, string);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(getClass("RuntimeError"), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(getClass("ArgumentError"), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(getClass("ArgumentError"), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getModule("Errno").getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getModule("Errno").getClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getModule("Errno").getClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getModule("Errno").getClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getModule("Errno").getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getModule("Errno").getClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getModule("Errno").getClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getModule("Errno").getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getModule("Errno").getClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getModule("Errno").getClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getModule("Errno").getClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getModule("Errno").getClass("EDOM"), "Domain error - " + message);
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(getClass("IndexError"), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getClass("SecurityError"), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getClass("SystemCallError"), message);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(getClass("TypeError"), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(getClass("ThreadError"), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(getClass("SyntaxError"), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(getClass("RegexpError"), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getClass("RangeError"), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getClass("NotImplementedError"), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(getClass("Iconv").getClass("InvalidEncoding"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.getClass("NoMethodError"), message, name), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.getClass("NameError"), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String message) {
         return newRaiseException(getClass("LocalJumpError"), message);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getClass("LoadError"), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(getClass("TypeError"), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getClass("SystemStackError"), message);
     }
 
     public RaiseException newSystemExit(int status) {
         RaiseException re = newRaiseException(getClass("SystemExit"), "");
         re.getException().setInstanceVariable("status", newFixnum(status));
 
         return re;
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getClass("IOError"), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getClass("StandardError"), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         return newRaiseException(getClass("IOError"), ioe.getMessage());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(getClass("TypeError"), "wrong argument type " + receivedObject.getMetaClass() + " (expected " + expectedType);
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(getClass("EOFError"), "End of file reached");
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(getClass("ZeroDivisionError"), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(getClass("FloatDomainError"), message);
     }
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         return re;
     }
 
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
     }
 
     public void setStackTraces(int stackTraces) {
         this.stackTraces = stackTraces;
     }
 
     public int getStackTraces() {
         return stackTraces;
     }
 
     public void setRandomSeed(long randomSeed) {
         this.randomSeed = randomSeed;
     }
 
     public long getRandomSeed() {
         return randomSeed;
     }
 
     public Random getRandom() {
         return random;
     }
 
     public ObjectSpace getObjectSpace() {
         return objectSpace;
     }
 
     public Hashtable getIoHandlers() {
         return ioHandlers;
     }
 
     public RubyFixnum[] getFixnumCache() {
         return fixnumCache;
     }
 
     public long incrementRandomSeedSequence() {
         return randomSeedSequence++;
     }
 
     public InputStream getIn() {
         return in;
     }
 
     public PrintStream getOut() {
         return out;
     }
 
     public PrintStream getErr() {
         return err;
     }
 
     public boolean isGlobalAbortOnExceptionEnabled() {
         return globalAbortOnExceptionEnabled;
     }
 
     public void setGlobalAbortOnExceptionEnabled(boolean enable) {
         globalAbortOnExceptionEnabled = enable;
     }
 
     public boolean isDoNotReverseLookupEnabled() {
         return doNotReverseLookupEnabled;
     }
 
     public void setDoNotReverseLookupEnabled(boolean b) {
         doNotReverseLookupEnabled = b;
     }
 
     private ThreadLocal inspect = new ThreadLocal();
     public boolean registerInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
         if(null == val) {
             val = new java.util.IdentityHashMap();
             inspect.set(val);
         }
         if(val.containsKey(obj)) {
             return false;
         }
         val.put(obj, null);
         return true;
     }
 
     public void unregisterInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
         val.remove(obj);
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             jrubyHome = verifyHome(System.getProperty("jruby.home", System.getProperty("user.home") + "/.jruby"));
         }
         return jrubyHome;
     }
     
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         NormalizedFile f = new NormalizedFile(home);
         if (!f.isAbsolute()) {
             home = f.getAbsolutePath();
         }
         f.mkdirs();
         return home;
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
     }
 
     /** GET_VM_STATE_VERSION */
     public long getGlobalState() {
         synchronized(this) {
             return globalState;
         }
     }
 
     /** INC_VM_STATE_VERSION */
     public void incGlobalState() {
         synchronized(this) {
             globalState = (globalState+1) & 0x8fffffff;
         }
     }
+
+    public static boolean isSecurityRestricted() {
+        return (System.getSecurityManager() != null);
+    }
 }
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index bcfb8da634..731eddbcdd 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,117 +1,115 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.InputStream;
 import java.io.PrintStream;
-import java.security.AccessControlException;
 import java.util.Map;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.CommandlineParser;
 
 public class RubyInstanceConfig {
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled = true;
     private String currentDirectory;
     private Map environment;
 
     {
-        try {
+        if (Ruby.isSecurityRestricted())
+            currentDirectory = "/";
+        else {
             currentDirectory = JRubyFile.getFileProperty("user.dir");
             if (System.getProperty("jruby.objectspace.enabled") != null) {
                 objectSpaceEnabled = Boolean.getBoolean("jruby.objectspace.enabled");
             }
-        } catch (AccessControlException accessEx) {
-            // default to "/" as current dir for applets (which can't read from FS anyway)
-            currentDirectory   = "/";
         }
     }
 
     public void updateWithCommandline(CommandlineParser cmdline) {
         this.objectSpaceEnabled = cmdline.isObjectSpaceEnabled();
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public void setOutput(PrintStream newOutput) {
         output = newOutput;
     }
 
     public PrintStream getOutput() {
         return output;
     }
 
     public void setError(PrintStream newError) {
         error = newError;
     }
 
     public PrintStream getError() {
         return error;
     }
 
     public void setCurrentDirectory(String newCurrentDirectory) {
         currentDirectory = newCurrentDirectory;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setProfile(Profile newProfile) {
         profile = newProfile;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public void setObjectSpaceEnabled(boolean newObjectSpaceEnabled) {
         objectSpaceEnabled = newObjectSpaceEnabled;
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setEnvironment(Map newEnvironment) {
         environment = newEnvironment;
     }
 
     public Map getEnvironment() {
         return environment;
     }
 }
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 9501233702..a9e6e74fe3 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,721 +1,726 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002 Jason Voegele <jason@jvoegele.com>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
 import edu.emory.mathcs.backport.java.util.concurrent.TimeoutException;
 import org.jruby.runtime.Arity;
 
 /**
  * Implementation of Ruby's <code>Thread</code> class.  Each Ruby thread is
  * mapped to an underlying Java Virtual Machine thread.
  * <p>
  * Thread encapsulates the behavior of a thread of execution, including the main
  * thread of the Ruby script.  In the descriptions that follow, the parameter
  * <code>aSymbol</code> refers to a symbol, which is either a quoted string or a
  * <code>Symbol</code> (such as <code>:name</code>).
  * 
  * Note: For CVS history, see ThreadClass.java.
  *
  * @author Jason Voegele (jason@jvoegele.com)
  */
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private Map threadLocalVariables = new HashMap();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private ThreadService threadService;
     private Object hasStartedLock = new Object();
     private boolean hasStarted = false;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
     private RubyThread joinedByCriticalThread;
     
-    private static final boolean USE_POOLING = Boolean.getBoolean("jruby.thread.pooling");
+    private static final boolean USE_POOLING;
     
+   static {
+       if (Ruby.isSecurityRestricted()) USE_POOLING = false;
+       else USE_POOLING = Boolean.getBoolean("jruby.thread.pooling");
+   }
+   
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyThread.class);
 
         threadClass.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("abort_on_exception", callbackFactory.getFastMethod("abort_on_exception"));
         threadClass.defineFastMethod("abort_on_exception=", callbackFactory.getFastMethod("abort_on_exception_set", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("alive?", callbackFactory.getFastMethod("is_alive"));
         threadClass.defineFastMethod("group", callbackFactory.getFastMethod("group"));
         threadClass.defineFastMethod("join", callbackFactory.getFastOptMethod("join"));
         threadClass.defineFastMethod("value", callbackFactory.getFastMethod("value"));
         threadClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         threadClass.defineFastMethod("key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("keys", callbackFactory.getFastMethod("keys"));
         threadClass.defineFastMethod("priority", callbackFactory.getFastMethod("priority"));
         threadClass.defineFastMethod("priority=", callbackFactory.getFastMethod("priority_set", RubyKernel.IRUBY_OBJECT));
         threadClass.defineMethod("raise", callbackFactory.getOptMethod("raise"));
         //threadClass.defineFastMethod("raise", callbackFactory.getFastMethod("raise", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("run", callbackFactory.getFastMethod("run"));
         threadClass.defineFastMethod("status", callbackFactory.getFastMethod("status"));
         threadClass.defineFastMethod("stop?", callbackFactory.getFastMethod("isStopped"));
         threadClass.defineFastMethod("wakeup", callbackFactory.getFastMethod("wakeup"));
         //        threadClass.defineMethod("value", 
         //                callbackFactory.getMethod("value"));
         threadClass.defineFastMethod("kill", callbackFactory.getFastMethod("kill"));
         threadClass.defineMethod("exit", callbackFactory.getMethod("exit"));
         
         threadClass.getMetaClass().defineFastMethod("current", callbackFactory.getFastSingletonMethod("current"));
         threadClass.getMetaClass().defineMethod("fork", callbackFactory.getOptSingletonMethod("newInstance"));
         threadClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
         threadClass.getMetaClass().defineFastMethod("list", callbackFactory.getFastSingletonMethod("list"));
         threadClass.getMetaClass().defineFastMethod("pass", callbackFactory.getFastSingletonMethod("pass"));
         threadClass.getMetaClass().defineMethod("start", callbackFactory.getOptSingletonMethod("start"));
         threadClass.getMetaClass().defineFastMethod("critical=", callbackFactory.getFastSingletonMethod("critical_set", RubyBoolean.class));
         threadClass.getMetaClass().defineFastMethod("critical", callbackFactory.getFastSingletonMethod("critical"));
         threadClass.getMetaClass().defineFastMethod("stop", callbackFactory.getFastSingletonMethod("stop"));
         threadClass.getMetaClass().defineMethod("kill", callbackFactory.getSingletonMethod("s_kill", RubyThread.class));
         threadClass.getMetaClass().defineMethod("exit", callbackFactory.getSingletonMethod("s_exit"));
         threadClass.getMetaClass().defineFastMethod("abort_on_exception", callbackFactory.getFastSingletonMethod("abort_on_exception"));
         threadClass.getMetaClass().defineFastMethod("abort_on_exception=", callbackFactory.getFastSingletonMethod("abort_on_exception_set", RubyKernel.IRUBY_OBJECT));
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // set hasStarted to true, otherwise Thread.main.status freezes
         rubyThread.hasStarted = true;
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
         threadClass.getMetaClass().defineFastMethod("main", callbackFactory.getFastSingletonMethod("main"));
         
         return threadClass;
     }
 
     /**
      * <code>Thread.new</code>
      * <p>
      * Thread.new( <i>[ arg ]*</i> ) {| args | block } -> aThread
      * <p>
      * Creates a new thread to execute the instructions given in block, and
      * begins running it. Any arguments passed to Thread.new are passed into the
      * block.
      * <pre>
      * x = Thread.new { sleep .1; print "x"; print "y"; print "z" }
      * a = Thread.new { print "a"; print "b"; sleep .2; print "c" }
      * x.join # Let the threads finish before
      * a.join # main thread exits...
      * </pre>
      * <i>produces:</i> abxyzc
      */
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
     public static RubyThread start(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, false, block);
     }
     
     public static RubyThread adopt(IRubyObject recv, Thread t) {
         return adoptThread(recv, t, Block.NULL_BLOCK);
     }
 
     private static RubyThread adoptThread(final IRubyObject recv, Thread t, Block block) {
         final Ruby runtime = recv.getRuntime();
         final RubyThread rubyThread = new RubyThread(runtime, (RubyClass) recv, false);
         
         rubyThread.threadImpl = new NativeThread(rubyThread, t);
         runtime.getThreadService().registerNewThread(rubyThread);
         
         runtime.getCurrentContext().preAdoptThread();
         
         rubyThread.callInit(IRubyObject.NULL_ARRAY, block);
         
         rubyThread.notifyStarted();
         
         return rubyThread;
     }
 
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         if (!block.isGiven()) throw recv.getRuntime().newThreadError("must be called with a block");
 
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) rubyThread.callInit(IRubyObject.NULL_ARRAY, block);
 
         if (USE_POOLING) {
             rubyThread.threadImpl = new FutureThread(rubyThread, new RubyRunnable(rubyThread, args, block));
         } else {
             rubyThread.threadImpl = new NativeThread(rubyThread, new RubyNativeThread(rubyThread, args, block));
         }
         rubyThread.threadImpl.start();
         
         // make sure the thread has started before continuing, so it will appear "runnable" to the rest of Ruby
         rubyThread.ensureStarted();
         
         return rubyThread;
     }
     
     public void cleanTerminate(IRubyObject result) {
     	try {
     		synchronized (this) { finalResult = result; }
     		isStopped = true;
     		waitIfCriticalized();
     	} catch (InterruptedException ie) {
     		// ignore
     	}
     }
 	
 	public void waitIfCriticalized() throws InterruptedException {
         RubyThread criticalThread = getRuntime().getThreadService().getCriticalThread();
 		if (criticalThread != null && criticalThread != this && criticalThread != joinedByCriticalThread) {
 			synchronized (criticalThread) {
                 criticalThread.wait();
 			}
 		}
 	}
     
     public void notifyStarted() {
         assert isCurrent();
         synchronized (hasStartedLock) {
             hasStarted = true;
             hasStartedLock.notifyAll();
         }
     }
 
     public void pollThreadEvents() {
         // Asserts.isTrue(isCurrent());
         pollReceivedExceptions();
         
         // TODO: should exceptions trump thread control, or vice versa?
         criticalizeOrDieIfKilled();
     }
 
     private void pollReceivedExceptions() {
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getModule("Kernel");
             kernelModule.callMethod(getRuntime().getCurrentContext(), "raise", raiseException);
         }
     }
 
     public void criticalizeOrDieIfKilled() {
     	try {
     		waitIfCriticalized();
     	} catch (InterruptedException ie) {
     		throw new ThreadKill();
     	}
     	dieIfKilled();
     }
 
     private RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getClass("ThreadGroup").getConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
     }
 
     private RubyThread(Ruby runtime, RubyClass type, boolean narf) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getClass("ThreadGroup").getConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
     }
 
     /**
      * Returns the status of the global ``abort on exception'' condition. The
      * default is false. When set to true, will cause all threads to abort (the
      * process will exit(0)) if an exception is raised in any thread. See also
      * Thread.abort_on_exception= .
      */
     public static RubyBoolean abort_on_exception(IRubyObject recv) {
     	Ruby runtime = recv.getRuntime();
         return runtime.isGlobalAbortOnExceptionEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static IRubyObject abort_on_exception_set(IRubyObject recv, IRubyObject value) {
         recv.getRuntime().setGlobalAbortOnExceptionEnabled(value.isTrue());
         return value;
     }
 
     public static RubyThread current(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getThread();
     }
 
     public static RubyThread main(IRubyObject recv) {
         return recv.getRuntime().getThreadService().getMainThread();
     }
 
     public static IRubyObject pass(IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         ThreadService ts = runtime.getThreadService();
         RubyThread criticalThread = ts.getCriticalThread();
         RubyThread currentThread = ts.getCurrentContext().getThread();
         
         if (criticalThread == currentThread) {
             // we're currently the critical thread; decriticalize for pass
             ts.setCritical(false);
         }
         
         Thread.yield();
         
         if (criticalThread != null) {
             // recriticalize
             ts.setCritical(true);
         }
         
         return recv.getRuntime().getNil();
     }
 
     public static RubyArray list(IRubyObject recv) {
     	RubyThread[] activeThreads = recv.getRuntime().getThreadService().getActiveRubyThreads();
         
         return recv.getRuntime().newArrayNoCopy(activeThreads);
     }
     
     private IRubyObject getSymbolKey(IRubyObject originalKey) {
         if (originalKey instanceof RubySymbol) {
             return originalKey;
         } else if (originalKey instanceof RubyString) {
             return RubySymbol.newSymbol(getRuntime(), originalKey.asSymbol());
         } else if (originalKey instanceof RubyFixnum) {
             getRuntime().getWarnings().warn("Do not use Fixnums as Symbols");
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         } else {
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         }
     }
 
     public IRubyObject aref(IRubyObject key) {
         key = getSymbolKey(key);
         
         if (!threadLocalVariables.containsKey(key)) {
             return getRuntime().getNil();
         }
         return (IRubyObject) threadLocalVariables.get(key);
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
         return value;
     }
 
     public RubyBoolean abort_on_exception() {
         return abortOnException ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject abort_on_exception_set(IRubyObject val) {
         abortOnException = val.isTrue();
         return val;
     }
 
     public RubyBoolean is_alive() {
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyThread join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
                 return null;
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         ensureStarted();
         try {
             RubyThread criticalThread = getRuntime().getThreadService().getCriticalThread();
             if (criticalThread != null) {
                 // set the target thread's joinedBy, so it knows it can execute during a critical section
                 joinedByCriticalThread = criticalThread;
                 threadImpl.interrupt(); // break target thread out of critical
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException iExcptn) {
             assert false : iExcptn;
         } catch (TimeoutException iExcptn) {
             assert false : iExcptn;
         } catch (ExecutionException iExcptn) {
             assert false : iExcptn;
         }
         if (exitingException != null) {
             throw exitingException;
         }
         return null;
     }
 
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     public IRubyObject group() {
         if (threadGroup == null) {
         	return getRuntime().getNil();
         }
         
         return threadGroup;
     }
     
     void setThreadGroup(RubyThreadGroup rubyThreadGroup) {
     	threadGroup = rubyThreadGroup;
     }
     
     public IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuffer part = new StringBuffer();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         
         if (threadImpl.isAlive()) {
             if (isStopped) {
                 part.append(getRuntime().newString(" sleep"));
             } else if (killed) {
                 part.append(getRuntime().newString(" aborting"));
             } else {
                 part.append(getRuntime().newString(" run"));
             }
         } else {
             part.append(" dead");
         }
         
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     public RubyBoolean has_key(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     public static IRubyObject critical_set(IRubyObject receiver, RubyBoolean value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCriticalThread() != null);
     }
 
     public static IRubyObject stop(IRubyObject receiver) {
     	RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
     	Object stopLock = rubyThread.stopLock;
     	
     	synchronized (stopLock) {
     		try {
     			rubyThread.isStopped = true;
     			// attempt to decriticalize all if we're the critical thread
     			receiver.getRuntime().getThreadService().setCritical(false);
 
     			stopLock.wait();
     		} catch (InterruptedException ie) {
     			// ignore, continue;
     		}
     		rubyThread.isStopped = false;
     	}
     	
     	return receiver.getRuntime().getNil();
     }
     
     public static IRubyObject s_kill(IRubyObject receiver, RubyThread rubyThread, Block block) {
     	return rubyThread.kill();
     }
 
     public static IRubyObject s_exit(IRubyObject receiver, Block block) {
     	RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
     	
 		rubyThread.killed = true;
 		// attempt to decriticalize all if we're the critical thread
 		receiver.getRuntime().getThreadService().setCritical(false);
 		
 		throw new ThreadKill();
     }
 
     public RubyBoolean isStopped() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
     	return this;
     }
     
     public RubyFixnum priority() {
         return getRuntime().newFixnum(threadImpl.getPriority());
     }
 
     public IRubyObject priority_set(IRubyObject priority) {
         // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
         int iPriority = RubyNumeric.fix2int(priority);
         
         if (iPriority < Thread.MIN_PRIORITY) {
             iPriority = Thread.MIN_PRIORITY;
         } else if (iPriority > Thread.MAX_PRIORITY) {
             iPriority = Thread.MAX_PRIORITY;
         }
         
         threadImpl.setPriority(iPriority);
         return priority;
     }
 
     public IRubyObject raise(IRubyObject[] args, Block block) {
         Ruby runtime = getRuntime();
         
         receivedException = prepareRaiseException(runtime, args, block);
         
         // interrupt the target thread in case it's blocking or waiting
         threadImpl.interrupt();
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 3); 
 
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.getClass("RuntimeError").newInstance(args, block);
             }
             
             if(!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             return runtime.newTypeError("exception object expected").getException();
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         return exception;
     }
     
     public IRubyObject run() {
         // if stopped, unstop
         synchronized (stopLock) {
             if (isStopped) {
                 isStopped = false;
                 stopLock.notifyAll();
             }
         }
     	
     	// Abort any sleep()s
     	// CON: Sleep now waits on the same stoplock, so it will have been woken up by the notify above
     	//threadImpl.interrupt();
     	
     	return this;
     }
     
     public void sleep(long millis) throws InterruptedException {
         synchronized (stopLock) {
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
         	if (isStopped) {
             	return getRuntime().newString("sleep");
             } else if (killed) {
                 return getRuntime().newString("aborting");
             }
         	
             return getRuntime().newString("run");
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newBoolean(false);
         }
     }
 
     public IRubyObject kill() {
     	// need to reexamine this
         synchronized (this) {
             if (killed) return this;
             
             killed = true;
             
             threadImpl.interrupt(); // break out of wait states and blocking IO
             try {
                 if (!threadImpl.isInterrupted()) {
                     // we did not interrupt the thread, so wait for it to complete
                     // TODO: test that this is correct...should killer wait for killee to die?
                     threadImpl.join();
                 }
             } catch (InterruptedException ie) {
                 throw new ThreadKill();
             } catch (ExecutionException ie) {
                 throw new ThreadKill();
             }
         }
         
         return this;
     }
     
     public IRubyObject exit(Block block) {
     	return kill();
     }
     
     public void dieIfKilled() {
     	if (killed) throw new ThreadKill();
     }
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     private void ensureStarted() {
         // The JVM's join() method may return immediately
         // and isAlive() give the wrong result if the thread
         // hasn't started yet. We give it a chance to start
         // before we try to do anything.
         
         synchronized (hasStartedLock) {
             while (!hasStarted) {
                 try {
                     hasStartedLock.wait();
                 } catch (InterruptedException iExcptn) {
                     //                        assert false : iExcptn;
                 }
             }
         }
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         Ruby runtime = exception.getException().getRuntime();
         if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
         	// TODO: Doesn't SystemExit have its own method to make this less wordy..
             RubyException re = RubyException.newException(getRuntime(), getRuntime().getClass("SystemExit"), exception.getMessage());
             re.setInstanceVariable("status", getRuntime().newFixnum(1));
             threadService.getMainThread().raise(new IRubyObject[]{re}, Block.NULL_BLOCK);
         } else {
             exitingException = exception;
         }
     }
 
     private boolean abortOnException(Ruby runtime) {
         return (runtime.isGlobalAbortOnExceptionEnabled() || abortOnException);
     }
 
     public static RubyThread mainThread(IRubyObject receiver) {
         return receiver.getRuntime().getThreadService().getMainThread();
     }
 }
diff --git a/src/org/jruby/environment/OSEnvironment.java b/src/org/jruby/environment/OSEnvironment.java
index 41beffe312..a9891f106a 100644
--- a/src/org/jruby/environment/OSEnvironment.java
+++ b/src/org/jruby/environment/OSEnvironment.java
@@ -1,187 +1,184 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 
 package org.jruby.environment;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.security.AccessControlException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jruby.Ruby;
 
 public class OSEnvironment {
 
 
     /**
      * Handles exceptions from implementors of {@link IOSEnvironmentReader},
      * converting the exception to a {@link OSEnvironmentReaderExcepton}
      * @param e
      */
     void handleException(Exception e) {
         throw (OSEnvironmentReaderExcepton)
         	new OSEnvironmentReaderExcepton().initCause(e);
     }
 
     /**
     * Returns the OS environment variables as a Map<RubyString,RubyString>.
     * If the Java system  property "jruby.env.method" is set then
     *   the value is used as the classname of a class than implements the IOSEnvironmentReader
     *   interface and the environment is obtained via this class.
     * If the "jruby.env.method" is "org.jruby.environment.OSEnvironmentReaderFromFile" then
     *   the java system property "jruby.envfile" should give the location of a file from which
     *   the environment variables can be loaded.
     * Otherwise, other default implementations of  IOSEnvironmentReader are tried
     * to obtain the os environment variables.
     * @param runtime
     * @param System.getProperty("jruby.env.method")
     * @throws OSEnvironmentReaderExcepton
     */
     public Map getEnvironmentVariableMap(Ruby runtime) {
         Map envs = null;
 
         if (runtime.getInstanceConfig().getEnvironment() != null) {
             return getAsMapOfRubyStrings(runtime, runtime.getInstanceConfig().getEnvironment().entrySet());
         }
 
-        // try/catch to fall back on empty env when security disallows environment var access (like in an applet)
-        try {
+        // fall back on empty env when security disallows environment var access (like in an applet)
+        if (Ruby.isSecurityRestricted())
+            envs = new HashMap();
+        else {
             String jrubyEnvMethod = System.getProperty("jruby.env.method");
 
             IOSEnvironmentReader reader;
 
             if (jrubyEnvMethod == null || jrubyEnvMethod.length() < 1) {
                 // Try to get environment from Java5 System.getenv()
                 reader = getAccessibleOSEnvironment(runtime, OSEnvironmentReaderFromJava5SystemGetenv.class.getName());
                 // not Java5 so try getting environment using Runtime exec
                 if (reader == null) {
                     reader = getAccessibleOSEnvironment(runtime, OSEnvironmentReaderFromRuntimeExec.class.getName());
                     //runtime.getWarnings().warn("Getting environment variables using Runtime Exec");
                 }  else {
                     //runtime.getWarnings().warn("Getting environment variables using Java5 System.getenv()");
                 }
             } else {
                 // get environment from jruby command line property supplied class
                 runtime.getWarnings().warn("Getting environment variables using command line defined method: " + jrubyEnvMethod);
                 reader = getAccessibleOSEnvironment(runtime, jrubyEnvMethod);
             }
 
             envs = null;
             if (reader != null) {
                 Map variables = null;
                 variables = reader.getVariables(runtime);
                 envs = getAsMapOfRubyStrings(runtime,  variables.entrySet());
             }
-        } catch (AccessControlException accessEx) {
-            // default to empty env
-            envs = new HashMap();
         }
 
         return envs;
 
     }
 
     /**
     * Returns java system properties as a Map<RubyString,RubyString>.
      * @param runtime
      * @return the java system properties as a Map<RubyString,RubyString>.
      */
     public Map getSystemPropertiesMap(Ruby runtime) {
-        try {
-            return getAsMapOfRubyStrings(runtime, System.getProperties().entrySet());
-        } catch (AccessControlException accessEx) {
-            // default to empty env
-            return new HashMap();
-        }
+        if (Ruby.isSecurityRestricted())
+           return new HashMap();
+       else
+           return getAsMapOfRubyStrings(runtime, System.getProperties().entrySet());
     }
 
 
     private static IOSEnvironmentReader getAccessibleOSEnvironment(Ruby runtime, String classname) {
         IOSEnvironmentReader osenvironment = null;
         try {
             osenvironment = (IOSEnvironmentReader)Class.forName(classname).newInstance();
         } catch (Exception e) {
         	// This should only happen for a command line supplied IOSEnvironmentReader implementation
             runtime.getWarnings().warn(e.getMessage());
         }
 
         if (osenvironment != null && osenvironment.isAccessible(runtime)) {
             return osenvironment;
         }
         return null;
     }
 
 
 
 	private static Map getAsMapOfRubyStrings(Ruby runtime, Set entrySet) {
 		Map envs = new HashMap();
 		for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
 			Map.Entry entry  = (Map.Entry) iter.next();
             envs.put(runtime.newString((String)entry.getKey()),runtime.newString((String)entry.getValue()));
 		}
 		return envs;
 	}
 
 
 	/**
      * Returns a Map of the variables found in the reader of form var=value
 	 * @param reader
 	 * @return Map<String,String> of variables found by reading lines from reader.
 	 */
 	Map getVariablesFrom(BufferedReader reader) {
         Map envs = new HashMap();
 		try {
     		String line, envVarName, envVarValue;
     		int equalsPos;
     		while ((line = reader.readLine()) != null) {
     			equalsPos = line.indexOf('=');
     			if (equalsPos >= 1) {
     				envVarName = line.substring(0, equalsPos);
     				envVarValue = line.substring(equalsPos + 1);
     				envs.put(envVarName, envVarValue);
     			}
     		}
     	} catch (IOException e) {
     		envs = null;
     		handleException(e);
     	} finally {
     		try {
     			reader.close();
     		} catch (IOException e) {
     			envs = null;
     			handleException(e);
     		}
     	}
 		return envs;
 	}
 
 
 }
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 983c0d0172..e3cc43b0a3 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,314 +1,327 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.internal.runtime.methods;
 
 import java.util.ArrayList;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends DynamicMethod {
     
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
     private boolean hasBeenTargeted = false;
     private int callCount = 0;
     private Script jitCompiledScript;
 
-    private static final boolean JIT_ENABLED = Boolean.getBoolean("jruby.jit.enabled");
-    private static final boolean JIT_LOGGING = Boolean.getBoolean("jruby.jit.logging");
-    private static final boolean JIT_LOGGING_VERBOSE = Boolean.getBoolean("jruby.jit.logging.verbose");
-    private static final int JIT_THRESHOLD = Integer.parseInt(System.getProperty("jruby.jit.threshold", "50"));
-    
+    private static final boolean JIT_ENABLED;
+    private static final boolean JIT_LOGGING;
+    private static final boolean JIT_LOGGING_VERBOSE;
+    private static final int JIT_THRESHOLD;
+      
+    static {
+        if (Ruby.isSecurityRestricted()) {
+            JIT_ENABLED = false;
+            JIT_LOGGING = false;
+            JIT_LOGGING_VERBOSE = false;
+            JIT_THRESHOLD = 50;
+        } else {
+            JIT_ENABLED = Boolean.getBoolean("jruby.jit.enabled");
+            JIT_LOGGING = Boolean.getBoolean("jruby.jit.logging");
+            JIT_LOGGING_VERBOSE = Boolean.getBoolean("jruby.jit.logging.verbose");
+            JIT_THRESHOLD = Integer.parseInt(System.getProperty("jruby.jit.threshold", "50"));
+        }
+    }
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body, 
             ArgsNode argsNode, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
 		this.cref = cref;
 		
 		assert argsNode != null;
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
         context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, 
             RubyModule clazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         if (jitCompiledScript != null) {
             try {
                 context.preCompiledMethod(implementationClass, cref);
                 // FIXME: pass block when available
                 return jitCompiledScript.run(context, self, args, block);
             } finally {
                 context.postCompiledMethod();
             }
         } 
           
         return super.call(context, self, clazz, name, args, noSuper, block);
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         if (JIT_ENABLED) runJIT(runtime, name);
         
         if (JIT_ENABLED && jitCompiledScript != null) {
             return jitCompiledScript.run(context, self, args, block);
         }
         
         // set jump target for returns that show up later, like from evals
         context.setFrameJumpTarget(this);
 
         if (argsNode.getBlockArgNode() != null && block.isGiven()) {
             RubyProc blockArg;
             
             if (block.getProcObject() != null) {
                 blockArg = (RubyProc) block.getProcObject();
             } else {
                 blockArg = runtime.newProc(false, block);
                 blockArg.getBlock().isLambda = block.isLambda;
             }
             // We pass depth zero since we know this only applies to newly created local scope
             context.getCurrentScope().setValue(argsNode.getBlockArgNode().getCount(), blockArg, 0);
         }
 
         try {
             prepareArguments(context, runtime, args);
             
             getArity().checkArity(runtime, args);
 
             traceCall(context, runtime, self, name);
                     
             return EvaluationState.eval(runtime, context, body, self, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
             traceReturn(context, runtime, self, name);
         }
     }
 
     private void runJIT(Ruby runtime, String name) {
         if (callCount >= 0 && getArity().isFixed() && argsNode.getBlockArgNode() == null && argsNode.getOptArgs() == null && argsNode.getRestArg() == -1) {
             callCount++;
             if (callCount >= JIT_THRESHOLD) {
                 String className = null;
                 if (JIT_LOGGING) {
                     className = getImplementationClass().getBaseName();
                     if (className == null) {
                         className = "<anon class>";
                     }
                 }
                 
                 try {
                     String cleanName = CodegenUtils.cleanJavaIdentifier(name);
                     StandardASMCompiler compiler = new StandardASMCompiler(cleanName + hashCode(), body.getPosition().getFile());
                     compiler.startScript();
                     Object methodToken = compiler.beginMethod("__file__", getArity().getValue(), staticScope.getNumberOfVariables());
                     NodeCompilerFactory.getCompiler(body).compile(body, compiler);
                     compiler.endMethod(methodToken);
                     compiler.endScript();
                     Class sourceClass = compiler.loadClass(runtime);
                     jitCompiledScript = (Script)sourceClass.newInstance();
                     
                     if (JIT_LOGGING) System.err.println("compiled: " + className + "." + name);
                 } catch (Exception e) {
                     if (JIT_LOGGING_VERBOSE) System.err.println("could not compile: " + className + "." + name + " because of: \"" + e.getMessage() + '"');
                 } finally {
                     callCount = -1;
                 }
             }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         int expectedArgsCount = argsNode.getArgsCount();
 
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
 
         // FIXME: This seems redundant with the arity check in internalCall...is it actually different?
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (expectedArgsCount > 0) {
             context.getCurrentScope().setArgValues(args, expectedArgsCount);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == -1 && hasOptArgs) {
             int opt = expectedArgsCount + argsNode.getOptArgs().size();
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount;
         if (argsNode.getOptArgs() != null) {
             count += argsNode.getOptArgs().size();
         }
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
         
         if (hasOptArgs) {
             ListNode optArgs = argsNode.getOptArgs();
    
             int j = 0;
             for (int i = expectedArgsCount; i < args.length && j < optArgs.size(); i++, j++) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 AssignmentVisitor.assign(runtime, context, context.getFrameSelf(), optArgs.get(j), args[i], Block.NULL_BLOCK, true);
                 expectedArgsCount++;
             }
    
             // assign the default values, adding to the end of allArgs
             while (j < optArgs.size()) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 allArgs.add(EvaluationState.eval(runtime, context, optArgs.get(j++), context.getFrameSelf(), Block.NULL_BLOCK));
             }
         }
         
         // build an array from *rest type args, also adding to allArgs
         
         // ENEBO: Does this next comment still need to be done since I killed hasLocalVars:
         // move this out of the scope.hasLocalVariables() condition to deal
         // with anonymous restargs (* versus *rest)
         
         
         // none present ==> -1
         // named restarg ==> >=0
         // anonymous restarg ==> -2
         if (restArg != -1) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = context.getPreviousFramePosition();
         runtime.callTraceFunction(context, "return", position, self, name, getImplementationClass());
     }
 
     private void traceCall(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) return;
 
 		ISourcePosition position = body != null ? body.getPosition() : context.getPosition(); 
 
 		runtime.callTraceFunction(context, "call", position, self, name, getImplementationClass());
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), cref);
     }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 77d24dbd7f..c4a9907a1e 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,723 +1,725 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.javasupport;
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.collections.IntHashMap;
 
 public class JavaClass extends JavaObject {
 
     private JavaClass(Ruby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaClass"), javaClass);
     }
     
     public static synchronized JavaClass get(Ruby runtime, Class klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = new JavaClass(runtime, klass);
             runtime.getJavaSupport().putJavaClassIntoCache(javaClass);
         }
         return javaClass;
     }
 
     public static RubyClass createJavaClassClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Determine if a real allocator is needed here. Do people want to extend
         // JavaClass? Do we want them to do that? Can you Class.new(JavaClass)? Should
         // you be able to?
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.getClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
 
     	CallbackFactory callbackFactory = runtime.callbackFactory(JavaClass.class);
         
         result.includeModule(runtime.getModule("Comparable"));
         
         JavaObject.registerRubyMethods(runtime, result);
 
         result.getMetaClass().defineFastMethod("for_name", 
                 callbackFactory.getFastSingletonMethod("for_name", IRubyObject.class));
         result.defineFastMethod("public?", 
                 callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("protected?", 
                 callbackFactory.getFastMethod("protected_p"));
         result.defineFastMethod("private?", 
                 callbackFactory.getFastMethod("private_p"));
         result.defineFastMethod("final?", 
                 callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("interface?", 
                 callbackFactory.getFastMethod("interface_p"));
         result.defineFastMethod("array?", 
                 callbackFactory.getFastMethod("array_p"));
         result.defineFastMethod("name", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("simple_name",
                 callbackFactory.getFastMethod("simple_name"));
         result.defineFastMethod("to_s", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("superclass", 
                 callbackFactory.getFastMethod("superclass"));
         result.defineFastMethod("<=>", 
                 callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
         result.defineFastMethod("java_instance_methods", 
                 callbackFactory.getFastMethod("java_instance_methods"));
         result.defineFastMethod("java_class_methods", 
                 callbackFactory.getFastMethod("java_class_methods"));
         result.defineFastMethod("java_method", 
                 callbackFactory.getFastOptMethod("java_method"));
         result.defineFastMethod("constructors", 
                 callbackFactory.getFastMethod("constructors"));
         result.defineFastMethod("constructor", 
                 callbackFactory.getFastOptMethod("constructor"));
         result.defineFastMethod("array_class", 
                 callbackFactory.getFastMethod("array_class"));
         result.defineFastMethod("new_array", 
                 callbackFactory.getFastMethod("new_array", IRubyObject.class));
         result.defineFastMethod("fields", 
                 callbackFactory.getFastMethod("fields"));
         result.defineFastMethod("field", 
                 callbackFactory.getFastMethod("field", IRubyObject.class));
         result.defineFastMethod("interfaces", 
                 callbackFactory.getFastMethod("interfaces"));
         result.defineFastMethod("primitive?", 
                 callbackFactory.getFastMethod("primitive_p"));
         result.defineFastMethod("assignable_from?", 
                 callbackFactory.getFastMethod("assignable_from_p", IRubyObject.class));
         result.defineFastMethod("component_type", 
                 callbackFactory.getFastMethod("component_type"));
         result.defineFastMethod("declared_instance_methods", 
                 callbackFactory.getFastMethod("declared_instance_methods"));
         result.defineFastMethod("declared_class_methods", 
                 callbackFactory.getFastMethod("declared_class_methods"));
         result.defineFastMethod("declared_fields", 
                 callbackFactory.getFastMethod("declared_fields"));
         result.defineFastMethod("declared_field", 
                 callbackFactory.getFastMethod("declared_field", IRubyObject.class));
         result.defineFastMethod("declared_constructors", 
                 callbackFactory.getFastMethod("declared_constructors"));
         result.defineFastMethod("declared_constructor", 
                 callbackFactory.getFastOptMethod("declared_constructor"));
         result.defineFastMethod("declared_classes", 
                 callbackFactory.getFastMethod("declared_classes"));
         result.defineFastMethod("declared_method", 
                 callbackFactory.getFastOptMethod("declared_method"));
         result.defineFastMethod("define_instance_methods_for_proxy", 
                 callbackFactory.getFastMethod("define_instance_methods_for_proxy", IRubyObject.class));
         
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
     
     public static synchronized JavaClass for_name(IRubyObject recv, IRubyObject name) {
         String className = name.asSymbol();
         Class klass = recv.getRuntime().getJavaSupport().loadJavaClass(className);
         return JavaClass.get(recv.getRuntime(), klass);
     }
     
     /**
      *  Get all methods grouped by name (e.g. 'new => {new(), new(int), new(int, int)}, ...')
      *  @param isStatic determines whether you want static or instance methods from the class
      */
     private Map getMethodsClumped(boolean isStatic) {
         Map map = new HashMap();
         if(((Class)getValue()).isInterface()) {
             return map;
         }
 
         Method methods[] = javaClass().getMethods();
         
         for (int i = 0; i < methods.length; i++) {
             if (isStatic != Modifier.isStatic(methods[i].getModifiers())) {
                 continue;
             }
             
             String key = methods[i].getName();
             RubyArray methodsWithName = (RubyArray) map.get(key); 
             
             if (methodsWithName == null) {
                 methodsWithName = RubyArray.newArrayLight(getRuntime());
                 map.put(key, methodsWithName);
             }
             
             methodsWithName.append(JavaMethod.create(getRuntime(), methods[i]));
         }
         
         return map;
     }
     
     private Map getPropertysClumped() {
         Map map = new HashMap();
         BeanInfo info;
         
         try {
             info = Introspector.getBeanInfo(javaClass());
         } catch (IntrospectionException e) {
             return map;
         }
         
         PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
         
         for (int i = 0; i < descriptors.length; i++) {
             Method readMethod = descriptors[i].getReadMethod();
             
             if (readMethod != null) {
                 String key = readMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     
                     map.put(key, aliases);    
                 }
 
                 if (readMethod.getReturnType() == Boolean.class ||
                     readMethod.getReturnType() == boolean.class) {
                     aliases.add(descriptors[i].getName() + "?");
                 }
                 aliases.add(descriptors[i].getName());
             }
             
             Method writeMethod = descriptors[i].getWriteMethod();
 
             if (writeMethod != null) {
                 String key = writeMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     map.put(key, aliases);
                 }
                 
                 aliases.add(descriptors[i].getName()  + "=");
             }
         }
         
         return map;
     }
     
     private void define_instance_method_for_proxy(final RubyClass proxy, List names, 
             final RubyArray methods) {
         final RubyModule javaUtilities = getRuntime().getModule("JavaUtilities");
         Callback method;
         if(methods.size()>1) {
             method = new Callback() {
                     private IntHashMap matchingMethods = new IntHashMap();
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         int len = args.length;
                         IRubyObject[] argsArray = new IRubyObject[len + 1];
                 
                         argsArray[0] = self.getInstanceVariable("@java_object");
 
                         int argsTypeHash = 0;
                         for (int j = 0; j < len; j++) {
                             argsArray[j+1] = Java.ruby_to_java(proxy, args[j], Block.NULL_BLOCK);
                             argsTypeHash += 3*args[j].getMetaClass().id;
                         }
 
                         IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                         if (match == null) {
                             match = Java.matching_method_internal(javaUtilities, methods, argsArray, 1, len);
                             matchingMethods.put(argsTypeHash, match);
                         }
 
                         return Java.java_to_ruby(self, ((JavaMethod)match).invoke(argsArray), Block.NULL_BLOCK);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 };
         } else {
             final JavaMethod METHOD = (JavaMethod)methods.eltInternal(0);
             method = new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         int len = args.length;
                         IRubyObject[] argsArray = new IRubyObject[len + 1];
                         argsArray[0] = self.getInstanceVariable("@java_object");
                         for(int j = 0; j < len; j++) {
                             argsArray[j+1] = Java.ruby_to_java(proxy, args[j], Block.NULL_BLOCK);
                         }
                         return Java.java_to_ruby(self, METHOD.invoke(argsArray), Block.NULL_BLOCK);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 };
         }
         
         for(Iterator iter = names.iterator(); iter.hasNext(); ) {
             String methodName = (String) iter.next();
             
             // We do not override class since it is too important to be overridden by getClass
             // short name.
             if (!methodName.equals("class")) {
                 proxy.defineFastMethod(methodName, method);
                 
                 String rubyCasedName = getRubyCasedName(methodName);
                 if (rubyCasedName != null) {
                     proxy.defineAlias(rubyCasedName, methodName);
                 }
             }
         }
     }
     
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z])([A-Z])");
     
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
 
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         
         return rubyCasedName;
     }
     
     private static final Callback __jsend_method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 String methodSymbol = args[0].asSymbol();
                 RubyMethod method = (org.jruby.RubyMethod)self.getMetaClass().newMethod(self, methodSymbol, true);
                 int v = RubyNumeric.fix2int(method.arity());
                 String name = args[0].asSymbol();
 
                 IRubyObject[] newArgs = new IRubyObject[args.length - 1];
                 System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
                 if(v < 0 || v == (newArgs.length)) {
                     return self.callMethod(self.getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
                 } else {
                     return self.callMethod(self.getRuntime().getCurrentContext(),self.getMetaClass().getSuperClass(), name, newArgs, CallType.SUPER, block);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
 
     public IRubyObject define_instance_methods_for_proxy(IRubyObject arg) {
         assert arg instanceof RubyClass;
 
         Map aliasesClump = getPropertysClumped();
         Map methodsClump = getMethodsClumped(false);
         RubyClass proxy = (RubyClass) arg;
 
         proxy.defineFastMethod("__jsend!", __jsend_method);
         
         for (Iterator iter = methodsClump.keySet().iterator(); iter.hasNext(); ) {
             String name = (String) iter.next();
             RubyArray methods = (RubyArray) methodsClump.get(name);
             List aliases = (List) aliasesClump.get(name);
 
             if (aliases == null) {
                 aliases = new ArrayList();
             }
 
             aliases.add(name);
             
             define_instance_method_for_proxy(proxy, aliases, methods);
         }
         
         return getRuntime().getNil();
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(javaClass().getModifiers()));
     }
 
     public RubyBoolean protected_p() {
         return getRuntime().newBoolean(Modifier.isProtected(javaClass().getModifiers()));
     }
 
     public RubyBoolean private_p() {
         return getRuntime().newBoolean(Modifier.isPrivate(javaClass().getModifiers()));
     }
 
 	Class javaClass() {
 		return (Class) getValue();
 	}
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(javaClass().getModifiers()));
     }
 
     public RubyBoolean interface_p() {
         return getRuntime().newBoolean(javaClass().isInterface());
     }
 
     public RubyBoolean array_p() {
         return getRuntime().newBoolean(javaClass().isArray());
     }
     
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
     
 
     private static String getSimpleName(Class class_) {
  		if (class_.isArray()) {
  			return getSimpleName(class_.getComponentType()) + "[]";
  		}
  
  		String className = class_.getName();
  
         int i = className.lastIndexOf('$');
  		if (i != -1) {
             do {
  				i++;
  			} while (i < className.length() && Character.isDigit(className.charAt(i)));
  			return className.substring(i);
  		}
  
  		return className.substring(className.lastIndexOf('.') + 1);
  	}
 
     public RubyString simple_name() {
         return getRuntime().newString(getSimpleName(javaClass()));
     }
 
     public IRubyObject superclass() {
         Class superclass = javaClass().getSuperclass();
         if (superclass == null) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), superclass);
     }
 
     public RubyFixnum op_cmp(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("<=> requires JavaClass (" + other.getType() + " given)");
         }
         JavaClass otherClass = (JavaClass) other;
         if (this.javaClass() == otherClass.javaClass()) {
             return getRuntime().newFixnum(0);
         }
         if (otherClass.javaClass().isAssignableFrom(this.javaClass())) {
             return getRuntime().newFixnum(-1);
         }
         return getRuntime().newFixnum(1);
     }
 
     public RubyArray java_instance_methods() {
         return java_methods(javaClass().getMethods(), false);
     }
 
     public RubyArray declared_instance_methods() {
         return java_methods(javaClass().getDeclaredMethods(), false);
     }
 
 	private RubyArray java_methods(Method[] methods, boolean isStatic) {
         RubyArray result = getRuntime().newArray(methods.length);
         for (int i = 0; i < methods.length; i++) {
             Method method = methods[i];
             if (isStatic == Modifier.isStatic(method.getModifiers())) {
                 result.append(JavaMethod.create(getRuntime(), method));
             }
         }
         return result;
 	}
 
 	public RubyArray java_class_methods() {
 	    return java_methods(javaClass().getMethods(), true);
     }
 
 	public RubyArray declared_class_methods() {
 	    return java_methods(javaClass().getDeclaredMethods(), true);
     }
 
 	public JavaMethod java_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     private Class[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         Class[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type = for_name(this, args[i]);
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     public RubyArray constructors() {
         return buildConstructors(javaClass().getConstructors());
     }
     
     public RubyArray declared_classes() {
+       if (Ruby.isSecurityRestricted()) // Can't even get inner classes?
+           return getRuntime().newArray(0);
         Class[] classes = javaClass().getDeclaredClasses();
         List accessibleClasses = new ArrayList();
         for (int i = 0; i < classes.length; i++) {
             if (Modifier.isPublic(classes[i].getModifiers())) {
                 accessibleClasses.add(classes[i]);
             }
         }
         return buildClasses((Class[]) accessibleClasses.toArray(new Class[accessibleClasses.size()]));
     }
     
     private RubyArray buildClasses(Class [] classes) {
         RubyArray result = getRuntime().newArray(classes.length);
         for (int i = 0; i < classes.length; i++) {
             result.append(new JavaClass(getRuntime(), classes[i]));
         }
         return result;
     }
     
 
     public RubyArray declared_constructors() {
         return buildConstructors(javaClass().getDeclaredConstructors());
     }
 
     private RubyArray buildConstructors(Constructor[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class[] buildClassArgs(IRubyObject[] args) {
         Class[] parameterTypes = new Class[args.length];
         for (int i = 0; i < args.length; i++) {
             String name = args[i].asSymbol();
             parameterTypes[i] = getRuntime().getJavaSupport().loadJavaClass(name);
         }
         return parameterTypes;
     }
 
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
     public JavaObject new_array(IRubyObject lengthArgument) {
         if (lengthArgument instanceof RubyInteger) {
             // one-dimensional array
         int length = (int) ((RubyInteger) lengthArgument).getLongValue();
         return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
         } else if (lengthArgument instanceof RubyArray) {
             // n-dimensional array
             List list = ((RubyArray)lengthArgument).getList();
             int length = list.size();
             if (length == 0) {
                 throw getRuntime().newArgumentError("empty dimensions specifier for java array");
     }
             int[] dimensions = new int[length];
             for (int i = length; --i >= 0; ) {
                 IRubyObject dimensionLength = (IRubyObject)list.get(i);
                 if ( !(dimensionLength instanceof RubyInteger) ) {
                     throw getRuntime()
                         .newTypeError(dimensionLength, getRuntime().getClass("Integer"));
                 }
                 dimensions[i] = (int) ((RubyInteger) dimensionLength).getLongValue();
             }
             return new JavaArray(getRuntime(), Array.newInstance(javaClass(), dimensions));
         } else {
             throw getRuntime().newArgumentError(
                   "invalid length or dimensions specifier for java array" +
                   " - must be Integer or Array of Integer");
         }
     }
 
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
     public RubyArray declared_fields() {
         return buildFieldResults(javaClass().getDeclaredFields());
     }
     
 	private RubyArray buildFieldResults(Field[] fields) {
         RubyArray result = getRuntime().newArray(fields.length);
         for (int i = 0; i < fields.length; i++) {
             result.append(new JavaField(getRuntime(), fields[i]));
         }
         return result;
 	}
 
 	public JavaField field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
 	public JavaField declared_field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getDeclaredField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
     private RaiseException undefinedFieldError(String name) {
         return getRuntime().newNameError("undefined field '" + name + "' for class '" + javaClass().getName() + "'", name);
     }
 
     public RubyArray interfaces() {
         Class[] interfaces = javaClass().getInterfaces();
         RubyArray result = getRuntime().newArray(interfaces.length);
         for (int i = 0; i < interfaces.length; i++) {
             result.append(JavaClass.get(getRuntime(), interfaces[i]));
         }
         return result;
     }
 
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     static boolean assignable(Class thisClass, Class otherClass) {
         if(!thisClass.isPrimitive() && otherClass == Void.TYPE ||
             thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
 
         otherClass = JavaUtil.primitiveToWrapper(otherClass);
         thisClass = JavaUtil.primitiveToWrapper(thisClass);
 
         if(thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
         if(Number.class.isAssignableFrom(thisClass)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
             if(otherClass.equals(Character.class)) {
                 return true;
             }
         }
         if(thisClass.equals(Character.class)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
         }
         return false;
     }
 
     private boolean isPrimitive() {
         return javaClass().isPrimitive();
     }
 
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
 }
diff --git a/src/org/jruby/javasupport/JavaField.java b/src/org/jruby/javasupport/JavaField.java
index 94f87ac808..f00714c84e 100644
--- a/src/org/jruby/javasupport/JavaField.java
+++ b/src/org/jruby/javasupport/JavaField.java
@@ -1,157 +1,162 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.javasupport;
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Field;
 import java.lang.reflect.Modifier;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaField extends JavaAccessibleObject {
     private Field field;
 
     public static RubyClass createJavaFieldClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaField", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaField.class);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         result.defineFastMethod("value_type", callbackFactory.getFastMethod("value_type"));
         result.defineFastMethod("public?", callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("static?", callbackFactory.getFastMethod("static_p"));
         result.defineFastMethod("value", callbackFactory.getFastMethod("value", IRubyObject.class));
         result.defineFastMethod("set_value", callbackFactory.getFastMethod("set_value", IRubyObject.class, IRubyObject.class));
         result.defineFastMethod("final?", callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("static_value", callbackFactory.getFastMethod("static_value"));
         result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         result.defineFastMethod("==", callbackFactory.getFastMethod("equal", IRubyObject.class));
         result.defineAlias("===", "==");
 
         return result;
     }
 
     public JavaField(Ruby runtime, Field field) {
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaField"));
         this.field = field;
     }
 
     public RubyString value_type() {
         return getRuntime().newString(field.getType().getName());
     }
 
     public IRubyObject equal(IRubyObject other) {
     	if (!(other instanceof JavaField)) {
     		return getRuntime().getFalse();
     	}
     	
         return getRuntime().newBoolean(field.equals(((JavaField) other).field));
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(field.getModifiers()));
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(Modifier.isStatic(field.getModifiers()));
     }
 
     public JavaObject value(IRubyObject object) {
         if (! (object instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object");
         }
         Object javaObject = ((JavaObject) object).getValue();
         try {
             return JavaObject.wrap(getRuntime(), field.get(javaObject));
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         }
     }
 
     public JavaObject set_value(IRubyObject object, IRubyObject value) {
          if (! (object instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object: " + object);
         }
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         Object javaObject = ((JavaObject) object).getValue();
         try {
             Object convertedValue = JavaUtil.convertArgument(((JavaObject) value).getValue(),
                                                              field.getType());
 
             field.set(javaObject, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 ((JavaObject) value).getValue().getClass().getName());
         }
         return (JavaObject) value;
     }
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(field.getModifiers()));
     }
 
     public JavaObject static_value() {
-        try {
-	    // TODO: Only setAccessible to account for pattern found by
-	    // accessing constants included from a non-public interface.
-	    // (aka java.util.zip.ZipConstants being implemented by many
-	    // classes)
-	    field.setAccessible(true);
-            return JavaObject.wrap(getRuntime(), field.get(null));
-        } catch (IllegalAccessException iae) {
-	    throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
+        
+        if (Ruby.isSecurityRestricted())
+            return null;
+        else {
+            try {
+                // TODO: Only setAccessible to account for pattern found by
+                // accessing constants included from a non-public interface.
+                // (aka java.util.zip.ZipConstants being implemented by many
+                // classes)
+                field.setAccessible(true);
+                return JavaObject.wrap(getRuntime(), field.get(null));
+            } catch (IllegalAccessException iae) {
+                throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
+            }
         }
     }
 
     public RubyString name() {
         return getRuntime().newString(field.getName());
     }
     
     protected AccessibleObject accesibleObject() {
         return field;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index f40b4f09a5..60df44a010 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
@@ -1,156 +1,157 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.javasupport;
 
 import java.lang.ref.WeakReference;
 import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyProc;
 import org.jruby.util.WeakIdentityHashMap;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaSupport {
     private Ruby runtime;
 
     private Map exceptionHandlers = new HashMap();
 
     private JRubyClassLoader javaClassLoader;
 
     private Map instanceCache = Collections.synchronizedMap(new WeakIdentityHashMap(100));
 
     public JavaSupport(Ruby ruby) {
         this.runtime = ruby;
         this.javaClassLoader = ruby.getJRubyClassLoader();
     }
 
     public Class loadJavaClass(String className) {
         try {
             Class result = primitiveClass(className);
             if(result == null) {
-                return Class.forName(className, true, javaClassLoader);
+                return (Ruby.isSecurityRestricted()) ? Class.forName(className) :
+                   Class.forName(className, true, javaClassLoader);
             }
             return result;
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className);
         }
     }
 
     public JavaClass getJavaClassFromCache(Class clazz) {
         WeakReference ref = (WeakReference) instanceCache.get(clazz);
         
         return ref == null ? null : (JavaClass) ref.get();
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         instanceCache.put(clazz.javaClass(), new WeakReference(clazz));
     }
     
     public void addToClasspath(URL url) {
         //        javaClassLoader = URLClassLoader.newInstance(new URL[] { url }, javaClassLoader);
         javaClassLoader.addURL(url);
     }
 
     public void defineExceptionHandler(String exceptionClass, RubyProc handler) {
         exceptionHandlers.put(exceptionClass, handler);
     }
 
     public void handleNativeException(Throwable exception) {
         if (exception instanceof RaiseException) {
             throw (RaiseException) exception;
         }
         Class excptnClass = exception.getClass();
         RubyProc handler = (RubyProc)exceptionHandlers.get(excptnClass.getName());
         while (handler == null &&
                excptnClass != Throwable.class) {
             excptnClass = excptnClass.getSuperclass();
         }
         if (handler != null) {
             handler.call(new IRubyObject[]{JavaUtil.convertJavaToRuby(runtime, exception)});
         } else {
             throw createRaiseException(exception);
         }
     }
 
     private RaiseException createRaiseException(Throwable exception) {
         RaiseException re = RaiseException.createNativeRaiseException(runtime, exception);
         
         return re;
     }
 
     private static Class primitiveClass(String name) {
         if (name.equals("long")) {
             return Long.TYPE;
         } else if (name.equals("int")) {
             return Integer.TYPE;
         } else if (name.equals("boolean")) {
             return Boolean.TYPE;
         } else if (name.equals("char")) {
             return Character.TYPE;
         } else if (name.equals("short")) {
             return Short.TYPE;
         } else if (name.equals("byte")) {
             return Byte.TYPE;
         } else if (name.equals("float")) {
             return Float.TYPE;
         } else if (name.equals("double")) {
             return Double.TYPE;
         }
         return null;
     }
 
     public ClassLoader getJavaClassLoader() {
         return javaClassLoader;
     }
     
     public JavaObject getJavaObjectFromCache(Object object) {
     	WeakReference ref = (WeakReference)instanceCache.get(object);
     	if (ref == null) {
     		return null;
     	}
     	JavaObject javaObject = (JavaObject) ref.get();
     	if (javaObject != null && javaObject.getValue() == object) {
     		return javaObject;
     	}
         return null;
     }
     
     public void putJavaObjectIntoCache(JavaObject object) {
     	instanceCache.put(object.getValue(), new WeakReference(object));
     }
 }
diff --git a/src/org/jruby/runtime/CallbackFactory.java b/src/org/jruby/runtime/CallbackFactory.java
index b65b10a428..a3d3c695b3 100644
--- a/src/org/jruby/runtime/CallbackFactory.java
+++ b/src/org/jruby/runtime/CallbackFactory.java
@@ -1,162 +1,166 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.callback.ReflectionCallbackFactory;
 import org.jruby.runtime.callback.InvocationCallbackFactory;
 import org.jruby.runtime.callback.DumpingInvocationCallbackFactory;
 
 /**
  * Helper class to build Callback method.
  * This impements method corresponding to the signature of method most often found in
  * the Ruby library, for methods with other signature the appropriate Callback object
  * will need to be explicitly created.
  **/
 public abstract class CallbackFactory {
     public static final Class[] NULL_CLASS_ARRAY = new Class[0];
     
     /**
      * gets an instance method with no arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method);
     public abstract Callback getFastMethod(String method);
 
     /**
      * gets an instance method with 1 argument.
      * @param method name of the method
      * @param arg1 the class of the only argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1);
     public abstract Callback getFastMethod(String method, Class arg1);
 
     /**
      * gets an instance method with two arguments.
      * @param method name of the method
      * @param arg1 the java class of the first argument for this method
      * @param arg2 the java class of the second argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1, Class arg2);
     public abstract Callback getFastMethod(String method, Class arg1, Class arg2);
     
     /**
      * gets an instance method with two arguments.
      * @param method name of the method
      * @param arg1 the java class of the first argument for this method
      * @param arg2 the java class of the second argument for this method
      * @param arg3 the java class of the second argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getMethod(String method, Class arg1, Class arg2, Class arg3);
     public abstract Callback getFastMethod(String method, Class arg1, Class arg2, Class arg3);
 
     /**
      * gets a singleton (class) method without arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method);
     public abstract Callback getFastSingletonMethod(String method);
 
     /**
      * gets a singleton (class) method with 1 argument.
      * @param method name of the method
      * @param arg1 the class of the only argument for this method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1);
     public abstract Callback getFastSingletonMethod(String method, Class arg1);
 
     /**
      * gets a singleton (class) method with 2 arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1, Class arg2);
     public abstract Callback getFastSingletonMethod(String method, Class arg1, Class arg2);
 
     /**
      * gets a singleton (class) method with 3 arguments.
      * @param method name of the method
      * @return a CallBack object corresponding to the appropriate method
      **/
     public abstract Callback getSingletonMethod(String method, Class arg1, Class arg2, Class arg3);
     public abstract Callback getFastSingletonMethod(String method, Class arg1, Class arg2, Class arg3);
 
     public abstract Callback getBlockMethod(String method);
     public abstract CompiledBlockCallback getBlockCallback(String method);
 
     /**
     * gets a singleton (class) method with no mandatory argument and some optional arguments.
      * @param method name of the method
     * @return a CallBack object corresponding to the appropriate method
     **/
     public abstract Callback getOptSingletonMethod(String method);
     public abstract Callback getFastOptSingletonMethod(String method);
 
     /**
     * gets an instance method with no mandatory argument and some optional arguments.
      * @param method name of the method
     * @return a CallBack object corresponding to the appropriate method
     **/
     public abstract Callback getOptMethod(String method);
     public abstract Callback getFastOptMethod(String method);
 
     private static boolean reflection = false;
     private static boolean dumping = false;
     private static String dumpingPath = null;
 
     static {
-        if(System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
-            reflection = true;
-        }
-        if(System.getProperty("jruby.dump_invocations") != null) {
-            dumping = true;
-            dumpingPath = System.getProperty("jruby.dump_invocations").toString();
-        }
+       if (Ruby.isSecurityRestricted())
+           reflection = true;
+       else {
+           if(System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
+               reflection = true;
+           }
+           if(System.getProperty("jruby.dump_invocations") != null) {
+               dumping = true;
+               dumpingPath = System.getProperty("jruby.dump_invocations").toString();
+           }
+       }
     }
 
     public static CallbackFactory createFactory(Ruby runtime, Class type) {
         if(reflection) {
             return new ReflectionCallbackFactory(type);
         } else if(dumping) {
             return new DumpingInvocationCallbackFactory(runtime, type, dumpingPath);
         } else {
             return new InvocationCallbackFactory(runtime, type);
         }
     }
 }
diff --git a/src/org/jruby/runtime/MethodFactory.java b/src/org/jruby/runtime/MethodFactory.java
index 76b30a74a4..5cf58f5826 100644
--- a/src/org/jruby/runtime/MethodFactory.java
+++ b/src/org/jruby/runtime/MethodFactory.java
@@ -1,68 +1,73 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime;
 
+import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.ReflectionMethodFactory;
 import org.jruby.internal.runtime.methods.InvocationMethodFactory;
 import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public abstract class MethodFactory {
     public abstract DynamicMethod getFullMethod(RubyModule implementationClass, Class type, String methodName, Arity arity, Visibility visibility);
     public abstract DynamicMethod getSimpleMethod(RubyModule implementationClass, Class type, String methodName, Arity arity, Visibility visibility);
     public abstract DynamicMethod getCompiledMethod(RubyModule implementationClass, Class type, String method, Arity arity, Visibility visibility, SinglyLinkedList cref);
 
     private static boolean reflection = false;
     private static boolean dumping = false;
     private static String dumpingPath = null;
 
     static {
-        if(System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
-            reflection = true;
-        }
-        if(System.getProperty("jruby.dump_invocations") != null) {
-            dumping = true;
-            dumpingPath = System.getProperty("jruby.dump_invocations").toString();
-        }
+       if (Ruby.isSecurityRestricted())
+           reflection = true;
+       else {
+           if(System.getProperty("jruby.reflection") != null && Boolean.getBoolean("jruby.reflection")) {
+               reflection = true;
+           }
+           if(System.getProperty("jruby.dump_invocations") != null) {
+               dumping = true;
+               dumpingPath = System.getProperty("jruby.dump_invocations").toString();
+           }
+       }
     }
 
     public static MethodFactory createFactory() {
         if(reflection) {
             return new ReflectionMethodFactory();
         } else if(dumping) {
             return new DumpingInvocationMethodFactory(dumpingPath);
         } else {
             return new InvocationMethodFactory();
         }
     }
 }// MethodFactory
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 1e1612f26d..1c1b965ddc 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,545 +1,538 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime.load;
 
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.security.AccessControlException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.PreparsedScript;
 
 /**
  * <b>How require works in JRuby</b>
  * When requiring a name from Ruby, JRuby will first remove any file extension it knows about,
  * thereby making it possible to use this string to see if JRuby has already loaded
  * the name in question. If a .rb or .rb.ast.ser extension is specified, JRuby will only try
  * those extensions when searching. If a .so, .o, .dll, or .jar extension is specified, JRuby
  * will only try .so or .jar when searching. Otherwise, JRuby goes through the known suffixes
  * (.rb, .rb.ast.ser, .so, and .jar) and tries to find a library with this name. The process for finding a library follows this order
  * for all searchable extensions:
  * <ol>
  * <li>First, check if the name starts with 'jar:', then the path points to a jar-file resource which is returned.</li>
  * <li>Second, try searching for the file in the current dir</li>
  * <li>Then JRuby looks through the load path trying these variants:
  *   <ol>
  *     <li>See if the current load path entry starts with 'jar:', if so check if this jar-file contains the name</li>
  *     <li>Otherwise JRuby tries to construct a path by combining the entry and the current working directy, and then see if 
  *         a file with the correct name can be reached from this point.</li>
  *   </ol>
  * </li>
  * <li>If all these fail, try to load the name as a resource from classloader resources, using the bare name as
  *     well as the load path entries</li>
  * <li>When we get to this state, the normal JRuby loading has failed. At this stage JRuby tries to load 
  *     Java native extensions, by following this process:
  *   <ol>
  *     <li>First it checks that we haven't already found a library. If we found a library of type JarredScript, the method continues.</li>
  *     <li>The first step is translating the name given into a valid Java Extension class name. First it splits the string into 
  *     each path segment, and then makes all but the last downcased. After this it takes the last entry, removes all underscores
  *     and capitalizes each part separated by underscores. It then joins everything together and tacks on a 'Service' at the end.
  *     Lastly, it removes all leading dots, to make it a valid Java FWCN.</li>
  *     <li>If the previous library was of type JarredScript, we try to add the jar-file to the classpath</li>
  *     <li>Now JRuby tries to instantiate the class with the name constructed. If this works, we return a ClassExtensionLibrary. Otherwise,
  *     the old library is put back in place, if there was one.
  *   </ol>
  * </li>
  * <li>When all separate methods have been tried and there was no result, a LoadError will be raised.</li>
  * <li>Otherwise, the name will be added to the loaded features, and the library loaded</li>
  * </ol>
  *
  * <b>How to make a class that can get required by JRuby</b>
  * <p>First, decide on what name should be used to require the extension.
  * In this purely hypothetical example, this name will be 'active_record/connection_adapters/jdbc_adapter'.
  * Then create the class name for this require-name, by looking at the guidelines above. Our class should
  * be named active_record.connection_adapters.JdbcAdapterService, and implement one of the library-interfaces.
  * The easiest one is BasicLibraryService, where you define the basicLoad-method, which will get called
  * when your library should be loaded.</p>
  * <p>The next step is to either put your compiled class on JRuby's classpath, or package the class/es inside a
  * jar-file. To package into a jar-file, we first create the file, then rename it to jdbc_adapter.jar. Then 
  * we put this jar-file in the directory active_record/connection_adapters somewhere in JRuby's load path. For
  * example, copying jdbc_adapter.jar into JRUBY_HOME/lib/ruby/site_ruby/1.8/active_record/connection_adapters
  * will make everything work. If you've packaged your extension inside a RubyGem, write a setub.rb-script that 
  * copies the jar-file to this place.</p>
  * <p>If you don't want to have the name of your extension-class to be prescribed, you can also put a file called
  * jruby-ext.properties in your jar-files META-INF directory, where you can use the key <full-extension-name>.impl
  * to make the extension library load the correct class. An example for the above would have a jruby-ext.properties
  * that contained a ruby like: "active_record/connection_adapters/jdbc_adapter=org.jruby.ar.JdbcAdapter". (NOTE: THIS
  * FEATURE IS NOT IMPLEMENTED YET.)</p>
  *
  * @author jpetersen
  */
 public class LoadService {
     private static final String JRUBY_BUILTIN_SUFFIX = ".rb";
 
     private static final String[] sourceSuffixes = { ".rb", ".rb.ast.ser" };
     private static final String[] extensionSuffixes = { ".so", ".jar" };
     private static final String[] allSuffixes = { ".rb", ".rb.ast.ser", ".so", ".jar" };
     private static final Pattern sourcePattern = Pattern.compile("\\.(?:rb|rb\\.ast\\.ser)$");
     private static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|jar)$");
 
     private final RubyArray loadPath;
     private final RubyArray loadedFeatures;
     private final Set loadedFeaturesInternal = Collections.synchronizedSet(new HashSet());
     private final Set firstLineLoadedFeatures = Collections.synchronizedSet(new HashSet());
     private final Map builtinLibraries = new HashMap();
 
     private final Map jarFiles = new HashMap();
 
     private final Map autoloadMap = new HashMap();
 
     private final Ruby runtime;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);    
     }
 
     public void init(List additionalDirectories) {
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // wrap in try/catch for security exceptions in an applet
-        try {
+        if (!Ruby.isSecurityRestricted()) {
           String jrubyHome = runtime.getJRubyHome();
           if (jrubyHome != null) {
               char sep = '/';
               String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
               addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + "site_ruby");
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION + sep + "java");
 
               // Added to make sure we find default distribution files within jar file.
               // TODO: Either make jrubyHome become the jar file or allow "classpath-only" paths
               addPath("lib" + sep + "ruby" + sep + Constants.RUBY_MAJOR_VERSION);
           }
-        } catch (AccessControlException accessEx) {
-          // ignore, we're in an applet and can't access filesystem anyway
         }
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     private void addPath(String path) {
         synchronized(loadPath) {
             loadPath.add(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         Library library = null;
         
         library = findLibrary(file);
 
         if (library == null) {
             library = findLibraryWithClassloaders(file);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime);
         } catch (IOException e) {
             throw runtime.newLoadError("IO error -- " + file);
         }
     }
 
     public boolean smartLoad(String file) {
         if(firstLineLoadedFeatures.contains(file)) {
             return false;
         }
         Library library = null;
         String loadName = file;
         String[] extensionsToSearch = null;
         
         // if an extension is specified, try more targetted searches
         if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
             Matcher matcher = null;
             if ((matcher = sourcePattern.matcher(file)).find()) {
                 // source extensions
                 extensionsToSearch = sourceSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else if ((matcher = extensionPattern.matcher(file)).find()) {
                 // extension extensions
                 extensionsToSearch = extensionSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else {
                 // unknown extension, fall back to search with extensions
                 extensionsToSearch = allSuffixes;
             }
         } else {
             // try all extensions
             extensionsToSearch = allSuffixes;
         }
         
         // First try suffixes with normal loading
         for (int i = 0; i < extensionsToSearch.length; i++) {
             library = findLibrary(file + extensionsToSearch[i]);
             if (library != null) {
                 loadName = file + extensionsToSearch[i];
                 break;
             }
         }
 
         // Then try suffixes with classloader loading
         if (library == null) {
             for (int i = 0; i < extensionsToSearch.length; i++) {
                 library = findLibraryWithClassloaders(file + extensionsToSearch[i]);
                 if (library != null) {
                     loadName = file + extensionsToSearch[i];
                     break;
                 }
             }
         }
 
         library = tryLoadExtension(library,file);
 
         // no library or extension found, bail out
         if (library == null) {
             throw runtime.newLoadError("no such file to load -- " + file);
         }
         
         if (loadedFeaturesInternal.contains(loadName)) {
             return false;
         }
         
         // attempt to load the found library
         try {
             loadedFeaturesInternal.add(loadName);
             firstLineLoadedFeatures.add(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.add(runtime.newString(loadName));
             }
 
             library.load(runtime);
             return true;
         } catch (Exception e) {
             loadedFeaturesInternal.remove(loadName);
             firstLineLoadedFeatures.remove(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.remove(runtime.newString(loadName));
             }
             if (e instanceof RaiseException) throw (RaiseException) e;
 
             if(runtime.getDebug().isTrue()) e.printStackTrace();
 
             RaiseException re = runtime.newLoadError("IO error -- " + file);
             re.initCause(e);
             throw re;
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
         return smartLoad(file);
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public boolean isAutoloadDefined(String name) {
         return autoloadMap.containsKey(name);
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return (IAutoloadMethod)autoloadMap.get(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = (IAutoloadMethod)autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void registerBuiltin(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
 	public void registerRubyBuiltin(String libraryName) {
 		registerBuiltin(libraryName + JRUBY_BUILTIN_SUFFIX, new BuiltinScript(libraryName));
 	}
 
     private Library findLibrary(String file) {
         if (builtinLibraries.containsKey(file)) {
             return (Library) builtinLibraries.get(file);
         }
         
         LoadServiceResource resource = findFile(file);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".rb.ast.ser")) {
         	return new PreparsedScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     private Library findLibraryWithClassloaders(String file) {
         LoadServiceResource resource = findFileInClasspath(file);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".rb.ast.ser")) {
         	return new PreparsedScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFile(String name) {
         // if a jar URL, return load service resource directly without further searching
         if (name.startsWith("jar:")) {
             try {
                 return new LoadServiceResource(new URL(name), name);
             } catch (MalformedURLException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         }
 
         // check current directory; if file exists, retrieve URL and return resource
-        try {
-            JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(),name);
-            if(file.isFile() && file.isAbsolute()) {
-                try {
-                    return new LoadServiceResource(file.toURL(),name);
-                } catch (MalformedURLException e) {
-                    throw runtime.newIOErrorFromException(e);
+        if (!Ruby.isSecurityRestricted()) {
+            try {
+                JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(),name);
+                if(file.isFile() && file.isAbsolute()) {
+                    try {
+                        return new LoadServiceResource(file.toURL(),name);
+                    } catch (MalformedURLException e) {
+                        throw runtime.newIOErrorFromException(e);
+                    }
                 }
-            }
-        } catch (AccessControlException accessEx) {
-            // ignore, applet security
-        } catch (IllegalArgumentException illArgEx) {
+            } catch (IllegalArgumentException illArgEx) { }
         }
 
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             String entry = pathIter.next().toString();
             if (entry.startsWith("jar:")) {
                 JarFile current = (JarFile)jarFiles.get(entry);
                 if(null == current) {
                     try {
                         current = new JarFile(entry.substring(4));
                         jarFiles.put(entry,current);
                     } catch (FileNotFoundException ignored) {
                     } catch (IOException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
                 if (current.getJarEntry(name) != null) {
                     try {
                         return new LoadServiceResource(new URL(entry + name), entry + name);
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } 
 
-            try {
+            if (!Ruby.isSecurityRestricted()) {
                 JRubyFile current = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(),entry).getAbsolutePath(), name);
                 if (current.isFile()) {
                     try {
                         return new LoadServiceResource(current.toURL(), current.getPath());
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
-            } catch (AccessControlException accessEx) {
-                // ignore, we're in an applet
-            } catch (IllegalArgumentException illArgEx) {
-                // ignore; Applet under windows has issues with current dir = "/"
             }
         }
 
         return null;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
 
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             String entry = pathIter.next().toString();
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
             
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             URL loc = classLoader.getResource(entry + "/" + name);
 
             // Make sure this is not a directory or unavailable in some way
             if (isRequireable(loc)) {
                 return new LoadServiceResource(loc, loc.getPath());
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         URL loc = classLoader.getResource(name);
 
         return isRequireable(loc) ? new LoadServiceResource(loc, loc.getPath()) : null;
     }
 
     private Library tryLoadExtension(Library library, String file) {
         // This code exploits the fact that all .jar files will be found for the JarredScript feature.
         // This is where the basic extension mechanism gets fixed
         Library oldLibrary = library;
         
         if(library == null || library instanceof JarredScript) {
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = file.split("/");
             StringBuffer finName = new StringBuffer();
             for(int i=0, j=(all.length-1); i<j; i++) {
                 finName.append(all[i].toLowerCase()).append(".");
                 
             }
             
             // Make the class name look nice, by splitting on _ and capitalize each segment, then joining
             // the, together without anything separating them, and last put on "Service" at the end.
             String[] last = all[all.length-1].split("_");
             for(int i=0, j=last.length; i<j; i++) {
                 finName.append(Character.toUpperCase(last[i].charAt(0))).append(last[i].substring(1));
             }
             finName.append("Service");
 
             // We don't want a package name beginning with dots, so we remove them
             String className = finName.toString().replaceAll("^\\.*","");
 
             // If there is a jar-file with the required name, we add this to the class path.
             if(library instanceof JarredScript) {
                 // It's _really_ expensive to check that the class actually exists in the Jar, so
                 // we don't do that now.
                 runtime.getJavaSupport().addToClasspath(((JarredScript)library).getResource().getURL());
             }
 
             try {
                 Class theClass = runtime.getJavaSupport().loadJavaClass(className);
                 library = new ClassExtensionLibrary(theClass);
             } catch(Exception ee) {
                 library = null;
             }
         }
         
         // If there was a good library before, we go back to that
         if(library == null && oldLibrary != null) {
             library = oldLibrary;
         }
         return library;
     }
     
     /* Directories and unavailable resources are not able to open a stream. */
     private boolean isRequireable(URL loc) {
         if (loc != null) {
         	if (loc.getProtocol().equals("file") && new java.io.File(loc.getFile()).isDirectory()) {
         		return false;
         	}
         	
         	try {
                 loc.openConnection();
                 return true;
             } catch (Exception e) {}
         }
         return false;
     }
 }
