diff --git a/build.xml b/build.xml
index 3fc3c96a49..7cff83246f 100644
--- a/build.xml
+++ b/build.xml
@@ -1,674 +1,676 @@
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
     <copy todir="${jruby.classes.dir}">
         <fileset dir="${src.dir}">
             <include name="**/*.rb"/>
         </fileset>
     </copy>
     
     <tstamp>
         <format property="build.date" pattern="yyyy-MM-dd"/>
     </tstamp>
     
     <copy todir="${jruby.classes.dir}" overwrite="true">
         <fileset dir="${src.dir}">
             <include name="**/*.properties"/>
         </fileset>
         <filterset>
             <filter token="os.arch" value="${os.arch}"/>
             <filter token="java.specification.version" value="${java.specification.version}"/>
             <filter token="javac.version" value="${javac.version}"/>
             <filter token="build.date" value="${build.date}"/>
         </filterset>
     </copy>
   </target>
 
   <target name="compile-jruby" depends="compile.tasks, check-for-optional-packages">
     <javac destdir="${jruby.classes.dir}" debug="true" source="${javac.version}" deprecation="true">
        <classpath refid="build.classpath"/>
        <src path="${src.dir}"/>
        <patternset refid="java.src.pattern"/>
     </javac>
   </target>
 
   <target name="compile" depends="compile-jruby"
           description="Compile the source files for the project.">
   </target>
 
   <target name="generate-method-classes" depends="compile">
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
 
   <target name="jar-jruby" unless="jar-up-to-date">
     <antcall target="generate-method-classes" inheritall="true"/>
     <jar destfile="${lib.dir}/jruby.jar" compress="true">
       <fileset dir="${jruby.classes.dir}">
         <exclude name="org/jruby/util/ant/**/*.class"/>
       </fileset>
       <manifest>
         <attribute name="Built-By" value="${user.name}"/>
         <attribute name="Main-Class" value="org.jruby.Main"/>
         <attribute name="Class-Path" value="asm-2.2.3.jar asm-commons-2.2.3.jar jline-0.9.91.jar bsf.jar backport-util-concurrent.jar"/>
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
 
   <target name="jar" depends="init" description="Create the jruby.jar file">
     <antcall target="jar-jruby" inheritall="true"/>
   </target>
 
   <target name="jar.standalone" depends="generate-method-classes, jar"
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
 
   <target name="install-gems">
     <property name="jruby.home" value="${basedir}"/>
     <java classname="org.jruby.Main" fork="true" maxmemory="256M" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${jruby.home}"/>
       <arg value="--command"/>
       <arg value="maybe_install_gems"/>
       <arg value="rspec"/>
       <arg value="rake"/>
       <arg value="--env-shebang"/>
     </java>
   </target>
   
   <target name="run-spec">
     <echo message="Running spec(s) located at: ${SPEC}"/>
     <java classname="org.jruby.Main" fork="true" maxmemory="256M" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <arg value="-S"/>
       <arg value="spec"/>
       <arg value="--format"/>
       <arg value="e"/>
       <arg line="${SPEC}"/>
     </java>
   </target>
   
   <target name="run-spec-compiled">
     <echo message="Running spec(s) located at: ${SPEC}"/>
     <java classname="org.jruby.Main" fork="true" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.jit.threshold" value="0"/>
       <arg value="-S"/>
       <arg value="spec"/>
       <arg value="--format"/>
       <arg value="e"/>
       <arg line="${SPEC}"/>
     </java>
   </target>
 
   <target name="spec" depends="jar,install-gems">
     <antcall target="run-spec">
         <param name="SPEC" value="test/externals/rubinius/spec"/>
     </antcall>
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
 
   <!-- This now has enabled the compiler by default; we may want to run both with and without compilation later -->
   <target name="test" depends="copy-test-files,instrument,run-junit-compiled,run-junit-interpreted,test-spec-compiled,test-spec-interpreted,coverage-report">
   </target>
 
   <target name="test-compiled" depends="copy-test-files,run-junit-compiled"/>
   <target name="test-interpreted" depends="copy-test-files,run-junit-interpreted"/>
   
   <target name="test-spec" depends="test-spec-compiled, test-spec-interpreted"/>
   
   <target name="test-spec-compiled" depends="install-gems">
     <antcall target="run-spec-compiled"><param name="SPEC" value="
 test/externals/rubinius/spec/core/false_spec.rb
 test/externals/rubinius/spec/core/hash_spec.rb
 test/externals/rubinius/spec/core/integer_spec.rb
 test/externals/rubinius/spec/core/io_spec.rb
 test/externals/rubinius/spec/core/nil_spec.rb
 test/externals/rubinius/spec/core/objectspace_spec.rb
 test/externals/rubinius/spec/core/proc_spec.rb
 test/externals/rubinius/spec/core/range_spec.rb
 test/externals/rubinius/spec/core/symbol_spec.rb
 test/externals/rubinius/spec/core/true_spec.rb
 test/externals/rubinius/spec/language/assignment_spec.rb
 test/externals/rubinius/spec/language/class_spec.rb
 test/externals/rubinius/spec/language/case_spec.rb
 test/externals/rubinius/spec/language/splat_spec.rb
 test/externals/rubinius/spec/language/variables_spec.rb
 test/externals/rubinius/spec/language/expressions/for_spec.rb
 test/externals/rubinius/spec/language/expressions/if_spec.rb
 test/externals/rubinius/spec/language/expressions/unless_spec.rb
 test/externals/rubinius/spec/language/expressions/while_spec.rb
 test/externals/rubinius/spec/language/keywords/break_spec.rb
 test/externals/rubinius/spec/language/keywords/defined_spec.rb
+test/externals/rubinius/spec/language/keywords/redo_spec.rb
 test/externals/rubinius/spec/language/literals
 test/externals/rubinius/spec/library
 test/externals/rubinius/spec/parser"/></antcall>
   </target>
   
   <target name="test-spec-interpreted" depends="install-gems">
     <antcall target="run-spec"><param name="SPEC" value="
 test/externals/rubinius/spec/core/false_spec.rb
 test/externals/rubinius/spec/core/hash_spec.rb
 test/externals/rubinius/spec/core/integer_spec.rb
 test/externals/rubinius/spec/core/io_spec.rb
 test/externals/rubinius/spec/core/nil_spec.rb
 test/externals/rubinius/spec/core/objectspace_spec.rb
 test/externals/rubinius/spec/core/proc_spec.rb
 test/externals/rubinius/spec/core/range_spec.rb
 test/externals/rubinius/spec/core/symbol_spec.rb
 test/externals/rubinius/spec/core/true_spec.rb
 test/externals/rubinius/spec/language/assignment_spec.rb
 test/externals/rubinius/spec/language/class_spec.rb
 test/externals/rubinius/spec/language/case_spec.rb
 test/externals/rubinius/spec/language/splat_spec.rb
 test/externals/rubinius/spec/language/variables_spec.rb
 test/externals/rubinius/spec/language/expressions/for_spec.rb
 test/externals/rubinius/spec/language/expressions/if_spec.rb
 test/externals/rubinius/spec/language/expressions/unless_spec.rb
 test/externals/rubinius/spec/language/expressions/while_spec.rb
 test/externals/rubinius/spec/language/keywords/break_spec.rb
 test/externals/rubinius/spec/language/keywords/defined_spec.rb
+test/externals/rubinius/spec/language/keywords/redo_spec.rb
 test/externals/rubinius/spec/language/literals
 test/externals/rubinius/spec/library
 test/externals/rubinius/spec/parser"/></antcall>
   </target>
   
   <target name="run-junit-interpreted" description="runs junit tests">
     <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${lib.dir}/junit.jar"/>
     <junit fork="yes" haltonfailure="true" dir="${basedir}" maxmemory="384M" showoutput="true">
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
       <sysproperty key="jruby.jit.enabled" value="false"/>
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
   </target>
   
   <target name="run-junit-compiled" description="runs junit tests">
     <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${lib.dir}/junit.jar"/>
     <junit fork="yes" haltonfailure="true" dir="${basedir}" maxmemory="384M" showoutput="true">
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
       <sysproperty key="jruby.jit.enabled" value="true"/>
       <sysproperty key="jruby.jit.threshold" value="0"/>
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
       <bottom><![CDATA[<i>Copyright &#169; 2002-2007 JRuby Team. All Rights Reserved.</i>]]></bottom>
     </javadoc>
   </target>
 
   <patternset id="dist.bin.files">
     <include name="bin/*jruby*"/>
     <include name="bin/*gem*"/>
     <include name="bin/*jirb*"/>
     <include name="bin/generate_yaml_index.rb"/>
     <include name="bin/testrb"/>
   </patternset>
 
   <patternset id="dist.lib.files">
     <include name="lib/ruby/1.8/**"/>
     <include name="lib/ruby/site_ruby/1.8/**"/>
     <include name="lib/ruby/gems/1.8/specifications/sources-0.0.1.gemspec"/>
     <include name="lib/ruby/gems/1.8/cache/sources-0.0.1.gem"/>
     <include name="lib/ruby/gems/1.8/gems/sources-0.0.1/**"/>
   </patternset>
 
   <patternset id="dist.files">
     <include name="lib/*"/>
     <include name="samples/**"/>
     <include name="docs/**"/>
     <include name="COPYING*"/>
     <include name="README"/>
     <exclude name="lib/ruby/**"/>
   </patternset>
 
   <patternset id="dist.src.files">
     <patternset refid="dist.files"/>
     <include name="src/**"/>
     <include name="test/**"/>
     <include name="build.xml"/>
     <include name="build-config.xml"/>
     <include name="nbproject/*"/>
     <include name=".project"/>
     <include name=".classpath"/>
     <include name="default.build.properties"/>
     <exclude name="lib/jruby.jar"/>
   </patternset>
 
   <target name="dist-bin" depends="jar">
     <mkdir dir="dist"/>
     <copy todir="dist">
       <fileset dir="${basedir}">
         <patternset refid="dist.bin.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <antcall target="install-gems">
       <param name="jruby.home" value="dist"/>
     </antcall>
     <tar destfile="jruby-bin-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="dist" mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="dist" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.files"/>
       </tarfileset>
     </tar>
     <zip destfile="jruby-bin-${version.jruby}.zip">
       <zipfileset dir="dist" filemode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </zipfileset>
       <zipfileset dir="dist" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.files"/>
       </zipfileset>
     </zip>
   </target>
 
   <target name="dist-src" depends="jar">
     <mkdir dir="dist"/>
     <copy todir="dist">
       <fileset dir="${basedir}">
         <patternset refid="dist.bin.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <antcall target="install-gems">
       <param name="jruby.home" value="dist"/>
     </antcall>
     <tar destfile="jruby-src-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="dist" mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="dist" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.src.files"/>
       </tarfileset>
     </tar>
     <zip destfile="jruby-src-${version.jruby}.zip">
       <zipfileset dir="dist" filemode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </zipfileset>
       <zipfileset dir="dist" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.src.files"/>
       </zipfileset>
     </zip>
   </target>
 
   <target name="dist" depends="dist-bin,dist-src"/>
 
   <target name="dist-clean">
     <delete includeEmptyDirs="true">
       <fileset dir=".">
         <include name="jruby-*.tar.gz"/>
         <include name="jruby-*.zip"/>
       </fileset>
       <fileset dir="dist" includes="**/*"/>
     </delete>
   </target>
 
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
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 12e9e1c4ee..523c1c9b89 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1294 +1,1296 @@
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
 import java.util.ArrayList;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.LinkedList;
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
 import org.jruby.libraries.IConvLibrary;
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.NKFLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.libraries.BigDecimalLibrary;
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.libraries.ThreadLibrary;
 import org.jruby.libraries.IOWaitLibrary;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.ext.Generator;
 import org.jruby.ext.Readline;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.MethodCache;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "yaml/syck", "jsignal" };
 
     private CacheMap cacheMap = new CacheMap(this);
     private MethodCache methodCache = new MethodCache();
     private ThreadService threadService = new ThreadService(this);
     private Hashtable runtimeInformation;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable ioHandlers = new Hashtable();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private List eventHooks = new LinkedList();
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
     private RubyModule enumerableModule;
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
     private static JRubyClassLoader jrubyClassLoader;
 
     private static boolean securityRestricted = false;
 
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
     
     private IRubyObject undef;
 
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
 
     public IRubyObject evalScript(Reader reader, String name) {
         return eval(parse(reader, name, getCurrentContext().getCurrentScope(), 0, false));
     }
     
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScript(String script) {
         return eval(parse(script, "<script>", getCurrentContext().getCurrentScope(), 0));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
 
             return EvaluationState.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw newLocalJumpError("return", (IRubyObject)je.getValue(), "unexpected return");
                 //              return (IRubyObject)je.getSecondaryData();
             } else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
+            } else if(je.getJumpType() == JumpException.JumpType.RedoJump) {
+                throw newLocalJumpError("redo", (IRubyObject)je.getValue(), "unexpected redo");
             }
 
             throw je;
         }
     }
     
     public IRubyObject compileOrFallbackAndRun(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             
             // do the compile if JIT is enabled
             if (config.isJitEnabled() && !hasEventHooks()) {
             Script script = null;
                 try {
                     StandardASMCompiler compiler = new StandardASMCompiler(node);
                     NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
                     Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
 
                     script = (Script)scriptClass.newInstance();
                     if (config.isJitLogging()) {
                         System.err.println("compiled: " + node.getPosition().getFile());
                     }
                 } catch (Throwable t) {
                     if (config.isJitLoggingVerbose()) {
                         System.err.println("coult not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                     }
                     return eval(node);
                 }
             
                 // FIXME: Pass something better for args and block here?
                 return script.run(getCurrentContext(), tc.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
             } else {
                 return eval(node);
             }
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } else {
                 throw je;
             }
         }
         
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
             Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
 
             Script script = (Script)scriptClass.newInstance();
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), tc.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
             System.err.println("Error -- Not compileable: " + e.getMessage());
             return null;
         } catch (InstantiationException e) {
             System.err.println("Error -- Not compileable: " + e.getMessage());
             return null;
         } catch (IllegalAccessException e) {
             System.err.println("Error -- Not compileable: " + e.getMessage());
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
     
     public IRubyObject getUndef() {
         return undef;
     }
 
     public RubyModule getKernel() {
         return kernelModule;
     }
     
     public RubyModule getEnumerable() {
         return enumerableModule;
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
         if (superClass == null) superClass = objectClass;
 
         return superClass.newSubClass(name, allocator, parentCRef, true);
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
         IRubyObject module = objectClass.getConstantAt(name);
         
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!(module instanceof RubyModule)) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
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
      * Retrieve method cache.
      * 
      * @return method cache where cached methods have been stored
      */
     public MethodCache getMethodCache() {
         return methodCache;
     }
 
     /**
      * @see org.jruby.Ruby#getRuntimeInformation
      */
     public Map getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable() : runtimeInformation;
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
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
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
         
         methodCache.initialized();
     }
 
     private void initLibraries() {
         loadService = config.createLoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                     RubyClassPathVariable.createClassPathVariable(runtime);
                 }
             });
         
         registerBuiltin("socket.rb", new RubySocket.Service());
         registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
             if(profile.allowBuiltin(BUILTIN_LIBRARIES[i])) {
                 loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
             }
         }
 
         final Library NO_OP_LIBRARY = new Library() {
                 public void load(Ruby runtime) throws IOException {
                 }
             };
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
         registerBuiltin("nkf.rb", new NKFLibrary());
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
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("openssl.rb"));
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
         registerBuiltin("io/wait.so", new IOWaitLibrary());
         registerBuiltin("etc.so", NO_OP_LIBRARY);
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         undef = new RubyUndef();
 
         RubyClass objectMetaClass = RubyClass.createBootstrapMetaClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR, null);
         RubyObject.createObjectClass(this, objectMetaClass);
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = RubyClass.createBootstrapMetaClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR, objectClass.getCRef());
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
 
         RubyModule.createModuleClass(this, moduleClass);
 
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
         enumerableModule = RubyEnumerable.createEnumerableModule(this);
         stringClass = RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) fixnumClass = RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) hashClass = RubyHash.createHashClass(this);
         
         RubyIO.createIOClass(this);
 
         if (profile.allowClass("Array")) arrayClass = RubyArray.createArrayClass(this);
 
         RubyClass structClass = null;
         if (profile.allowClass("Struct")) structClass = RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
         if (profile.allowClass("Binding")) RubyBinding.createBindingClass(this);
         // Math depends on all numeric types
         if (profile.allowModule("Math")) RubyMath.createMathModule(this); 
         if (profile.allowClass("Regexp")) RubyRegexp.createRegexpClass(this);
         if (profile.allowClass("Range")) RubyRange.createRangeClass(this);
         if (profile.allowModule("ObjectSpace")) RubyObjectSpace.createObjectSpaceModule(this);
         if (profile.allowModule("GC")) RubyGC.createGCModule(this);
         if (profile.allowClass("Proc")) RubyProc.createProcClass(this);
         if (profile.allowClass("Method")) RubyMethod.createMethodClass(this);
         if (profile.allowClass("MatchData")) RubyMatchData.createMatchDataClass(this);
         if (profile.allowModule("Marshal")) RubyMarshal.createMarshalModule(this);
         if (profile.allowClass("Dir")) RubyDir.createDirClass(this);
         if (profile.allowModule("FileTest")) RubyFileTest.createFileTestModule(this);
         // depends on IO, FileTest
         if (profile.allowClass("File")) RubyFile.createFileClass(this);
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
         RubyClass signalException = null;
         
         RubyClass rangeError = null;
         if (profile.allowClass("StandardError")) {
             standardError = defineClass("StandardError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if (profile.allowClass("NoMethodError")) {
             RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }        
         if (profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemExit")) {
             RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("SignalException")) {
             signalException = defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Interrupt")) {
             defineClass("Interrupt", signalException, signalException.getAllocator());
         }
         if (profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError, ioError.getAllocator());
         }
         if (profile.allowClass("LocalJumpError")) {
             RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError, standardError.getAllocator());
         }
         // FIXME: Actually this somewhere <- fixed
         if (profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError, rangeError.getAllocator());
         }
         if (profile.allowClass("NativeException")) NativeException.createClass(this, runtimeError);
         if (profile.allowClass("SystemCallError")) {
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
         if (profile.allowModule("Signal")) RubySignal.createSignal(this);
         if (profile.allowClass("Continuation")) RubyContinuation.createContinuation(this);
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
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null)
             jrubyClassLoader = new JRubyClassLoader(Thread.currentThread().getContextClassLoader());
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
 
     public Node parse(Reader content, String file, DynamicScope scope, int lineNumber, boolean isInlineScript) {
         return parser.parse(file, content, scope, lineNumber, false, isInlineScript);
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, lineNumber);
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content, scope, lineNumber, extraPositionInformation);
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
         // FIXME: We can't guarantee this will always be a RubyIO...so the old code here is not safe
         /*java.io.OutputStream os = ((RubyIO) getGlobalVariables().get("$stderr")).getOutStream();
         if(null != os) {
             return new PrintStream(os);
         } else {
             return new PrintStream(new org.jruby.util.SwallowingOutputStream());
         }*/
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stderr")));
     }
 
     public InputStream getInputStream() {
         return new IOInputStream(getGlobalVariables().get("$stdin"));
     }
 
     public PrintStream getOutputStream() {
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stdout")));
     }
 
     public RubyModule getClassFromPath(String path) {
         RubyModule c = getObject();
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
         }
         int pbeg = 0, p = 0;
         for(int l=path.length(); p<l; ) {
             while(p<l && path.charAt(p) != ':') {
                 p++;
             }
             String str = path.substring(pbeg, p);
 
             if(p<l && path.charAt(p) == ':') {
                 if(p+1 < l && path.charAt(p+1) != ':') {
                     throw newTypeError("undefined class/module " + path.substring(pbeg,p));
                 }
                 p += 2;
                 pbeg = p;
             }
 
             IRubyObject cc = c.getConstant(str);
             if(!(cc instanceof RubyModule)) {
                 throw newTypeError("" + str + " does not refer to class/module");
             }
             c = (RubyModule)cc;
         }
         return c;
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
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             };
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parse(source, scriptName, null, 0, false);
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
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             script.run(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
         BufferedReader source = null;
         try {
             source = new BufferedReader(new FileReader(file));
             loadScript(file.getPath().replace(File.separatorChar, '/'), source);
         } catch (IOException ioExcptn) {
             throw newIOErrorFromException(ioExcptn);
         } finally {
             try {
                 if (source == null) {
                     source.close();
                 }
             } catch (IOException ioe) {}
         }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 33ff71cd97..60e7724078 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1459 +1,1461 @@
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
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 
 import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.ast.Node;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     
     private RubyObject(){};
     // An instance that never equals any other instance
     public static final IRubyObject NEVER = new RubyObject();
     
     // The class of this object
     protected RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     private transient Object dataStruct;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
     
     private Finalizer finalizer;
     
     public class Finalizer implements Finalizable {
         private long id;
         private List finalizers;
         private AtomicBoolean finalized;
         
         public Finalizer(long id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
         
         public void addFinalizer(RubyProc finalizer) {
             if (finalizers == null) {
                 finalizers = new ArrayList();
             }
             finalizers.add(finalizer);
         }
 
         public void removeFinalizers() {
             finalizers = null;
         }
     
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (finalizers != null) {
                     IRubyObject idFixnum = getRuntime().newFixnum(id);
                     for (int i = 0; i < finalizers.size(); i++) {
                         ((RubyProc)finalizers.get(i)).call(
                                 new IRubyObject[] {idFixnum});
                     }
                 }
             }
         }
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
     
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObject.class);   
         objectClass.index = ClassIndex.OBJECT;
         
         objectClass.definePrivateMethod("initialize", callbackFactory.getOptMethod("initialize"));
         objectClass.definePrivateMethod("inherited", callbackFactory.getMethod("inherited", IRubyObject.class));
         
         return objectClass;
     }
     
     public static ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             IRubyObject instance = new RubyObject(runtime, klass);
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || 
                 other instanceof IRubyObject && 
                 callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public final boolean isTrue() {
         return isTrue;
     }
 
     public final boolean isFalse() {
         return !isTrue;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         if (original.safeHasInstanceVariables()) {
             clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         }
         
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(self, callType))) {
             return callMethodMissing(context, this, method, name, args, self, callType, block);
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
     
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, int methodIndex,
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (methodIndex == MethodIndex.METHOD_MISSING) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, methodIndex, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return rubyclass.dispatcher.callMethod(context, this, rubyclass, methodIndex, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         
 
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(context.getFrameSelf(), callType))) {
             return callMethodMissing(context, this, method, name, args, context.getFrameSelf(), callType, block);
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return callMethod(context, getMetaClass(), methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, block);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getRealClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType(getRuntime().getArray(), MethodIndex.TO_ARY, true);
     }
 
     public RubyHash convertToHash() {
         return (RubyHash)convertToType(getRuntime().getHash(), MethodIndex.TO_HASH, "to_hash", true, true, false);
     }
     
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType(getRuntime().getClass("Float"), MethodIndex.TO_F, true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType(getRuntime().getClass("Integer"), MethodIndex.TO_INT, true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType(getRuntime().getString(), MethodIndex.TO_STR, true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(RubyClass targetType, int convertMethodIndex, String convertMethod) {
         return convertToType(targetType, convertMethodIndex, convertMethod, false, true, true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raise) {
         return convertToType(targetType, convertMethodIndex, convertMethod, raise, false, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, boolean raise) {
         return convertToType(targetType, convertMethodIndex, (String)MethodIndex.NAMES.get(convertMethodIndex), raise, true, false);
     }
     
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough) {
         if (isKindOf(targetType)) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raiseOnMissingMethod) {
                 throw getRuntime().newTypeError("can't convert " + trueFalseNil(this) + " into " + trueFalseNil(targetType.getName()));
             } 
 
             return getRuntime().getNil();
         }
         
         IRubyObject value = callMethod(getRuntime().getCurrentContext(), convertMethodIndex, convertMethod, IRubyObject.NULL_ARRAY);
         
         if (allowNilThrough && value.isNil()) {
             return value;
         }
 
         if (raiseOnWrongTypeResult && !value.isKindOf(targetType)) {
             throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod +
                     " should return " + targetType);
         }
         
         return value;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck(getRuntime().getString(), MethodIndex.TO_STR, "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck(getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, new IRubyObject[] { this }, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameName();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, filename.convertToString().toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, IRubyObject[] args, Block block) {
         final IRubyObject selfInYield = this;
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     boolean aValue;
                     if (args.length == 1) {
                         aValue = false;
                     } else {
                         aValue = true;
                     }
                     return block.yield(context, args, selfInYield, context.getRubyClass(), aValue);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, args, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
             String file, int lineNumber) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
         ISourcePosition savedPosition = threadContext.getPosition();
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             IRubyObject newSelf = threadContext.getFrameSelf();
             Node node = 
                 getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope(), lineNumber);
 
             return EvaluationState.eval(getRuntime(), threadContext, node, newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
+            } else if(je.getJumpType() == JumpException.JumpType.RedoJump) {
+                throw getRuntime().newLocalJumpError("redo", (IRubyObject)je.getValue(), "unexpected redo");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             Node node = getRuntime().parse(src.toString(), file, context.getCurrentScope(), 0);
             
             return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
             return getRuntime().getTrue();
 	}
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
     }
 
     /** rb_eql
      *  this method is not defind for Ruby objects directly.
      *  notably overriden by RubyFixnum, RubyString, RubySymbol - these do a short-circuit calls.
      *  see: rb_any_cmp() in hash.c
      *  do not confuse this method with eql_p methods (which it calls by default), eql is mainly used for hash key comparison 
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     public final boolean eqlInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return true;
         return callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return super.hashCode();
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone(Block unusedBlock) {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         initCopy(clone, this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         initCopy(dup, this);
 
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(sep);
                         part.append(" ");
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         
         if (isNil()) return RubyNil.inspect(this);
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().protected_instance_methods(args);
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().private_instance_methods(args);
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject instance_exec(IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw getRuntime().newArgumentError("block not supplied");
         }
         return yieldUnder(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             IRubyObject obj;
             if (!(((obj = args[i]) instanceof RubyModule) && ((RubyModule)obj).isModule())){
                 throw getRuntime().newTypeError(obj,getRuntime().getClass("Module"));
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
     	return getRuntime().getNil();
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
  
     public void addFinalizer(RubyProc finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 5264fa0977..98e7a316fb 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,343 +1,345 @@
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
 import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.ClosureCallback;
 import org.jruby.compiler.Compiler;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends DynamicMethod {
     
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
     private int callCount = 0;
     private Script jitCompiledScript;
     private int expectedArgsCount;
     private int restArg;
     private boolean hasOptArgs;
 
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body, 
             ArgsNode argsNode, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
 		this.cref = cref;
         this.expectedArgsCount = argsNode.getArgsCount();
         this.restArg = argsNode.getRestArg();
         this.hasOptArgs = argsNode.getOptArgs() != null;
 		
 		assert argsNode != null;
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
         context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         if (runtime.getInstanceConfig().isJitEnabled()) {
             runJIT(runtime, context, name);
         }
 
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
 
             if (runtime.hasEventHooks()) {
                 traceCall(context, runtime, name);
             }
                     
             if (jitCompiledScript != null && !runtime.hasEventHooks()) {
                 return jitCompiledScript.run(context, self, args, block);
             }
             
             return EvaluationState.eval(runtime, context, body, self, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
+            } else if(je.getJumpType() == JumpException.JumpType.RedoJump) {
+                throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
         	}
             
        		throw je;
         } finally {
             if (runtime.hasEventHooks()) {
                 traceReturn(context, runtime, name);
             }
         }
     }
 
     private void runJIT(Ruby runtime, ThreadContext context, String name) {
         if (callCount >= 0) {
             String className = null;
             if (runtime.getInstanceConfig().isJitLogging()) {
                 className = getImplementationClass().getBaseName();
                 if (className == null) {
                     className = "<anon class>";
                 }
             }
             
             try {
                 callCount++;
 
                 if (callCount >= runtime.getInstanceConfig().getJitThreshold()) {
                     NodeCompilerFactory.confirmNodeIsSafe(argsNode);
                     // FIXME: Total duplication from DefnNodeCompiler...need to refactor this
                     final ArrayCallback evalOptionalValue = new ArrayCallback() {
                         public void nextValue(Compiler context, Object object, int index) {
                             ListNode optArgs = (ListNode)object;
                             
                             Node node = optArgs.get(index);
 
                             NodeCompilerFactory.getCompiler(node).compile(node, context);
                         }
                     };
                     
                     ClosureCallback args = new ClosureCallback() {
                         public void compile(Compiler context) {
                             Arity arity = argsNode.getArity();
                             
                             context.lineNumber(argsNode.getPosition());
                             int required = expectedArgsCount;
                             
                             if (hasOptArgs) {
                                 if (restArg > -1) {
                                     int opt = argsNode.getOptArgs().size();
                                     context.processRequiredArgs(arity, required, opt, restArg);
                                     
                                     ListNode optArgs = argsNode.getOptArgs();
                                     context.assignOptionalArgs(optArgs, required, opt, evalOptionalValue);
                                     
                                     context.processRestArg(required + opt, restArg);
                                 } else {
                                     int opt = argsNode.getOptArgs().size();
                                     context.processRequiredArgs(arity, required, opt, restArg);
                                     
                                     ListNode optArgs = argsNode.getOptArgs();
                                     context.assignOptionalArgs(optArgs, required, opt, evalOptionalValue);
                                 }
                             } else {
                                 if (restArg > -1) {
                                     context.processRequiredArgs(arity, required, 0, restArg);
                                     
                                     context.processRestArg(required, restArg);
                                 } else {
                                     context.processRequiredArgs(arity, required, 0, restArg);
                                 }
                             }
                         }
                     };
                     
                     String cleanName = CodegenUtils.cg.cleanJavaIdentifier(name);
                     // FIXME: not handling empty bodies correctly...
                     StandardASMCompiler compiler = new StandardASMCompiler(cleanName + hashCode() + "_" + context.hashCode(), body.getPosition().getFile());
                     compiler.startScript();
                     Object methodToken = compiler.beginMethod("__file__", args);
                     NodeCompilerFactory.getCompiler(body).compile(body, compiler);
                     compiler.endMethod(methodToken);
                     compiler.endScript();
                     Class sourceClass = compiler.loadClass(new JRubyClassLoader(runtime.getJRubyClassLoader()));
                     jitCompiledScript = (Script)sourceClass.newInstance();
                     
                     if (runtime.getInstanceConfig().isJitLogging()) System.err.println("compiled: " + className + "." + name);
                     callCount = -1;
                 }
             } catch (Exception e) {
                 if (runtime.getInstanceConfig().isJitLoggingVerbose()) System.err.println("could not compile: " + className + "." + name + " because of: \"" + e.getMessage() + '"');
                 callCount = -1;
              }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (expectedArgsCount > 0) {
             context.getCurrentScope().setArgValues(args, expectedArgsCount);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         int localExpectedArgsCount = expectedArgsCount;
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
                 localExpectedArgsCount++;
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
             for (int i = localExpectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - localExpectedArgsCount);
                 for (int i = localExpectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, String name) {
         ISourcePosition position = context.getPreviousFramePosition();
         runtime.callEventHooks(context, EventHook.RUBY_EVENT_RETURN, position.getFile(), position.getStartLine(), name, getImplementationClass());
     }
     
     private void traceCall(ThreadContext context, Ruby runtime, String name) {
         ISourcePosition position = body != null ? body.getPosition() : context.getPosition();
         
         runtime.callEventHooks(context, EventHook.RUBY_EVENT_CALL, position.getFile(), position.getStartLine(), name, getImplementationClass());
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), cref);
     }
 }
