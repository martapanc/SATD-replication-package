diff --git a/build.xml b/build.xml
index 7af91da9d2..ac73e323f2 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1085 +1,1054 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <project basedir="." default="jar" name="JRuby">
   <description>JRuby is a pure Java implementation of a Ruby interpreter.</description>
 
   <!-- First try to load machine-specific properties. -->
   <property file="build.properties"/>
   <!-- Load revision number for the ruby specs, in a known good state.
        There should be no spec failures with such revision. -->
   <property file="rubyspecs.revision"/>
 
   <!-- And then load the defaults. It seems backwards to set defaults AFTER 
        setting local overrides, but that's how Ant works. -->
   <property file="default.build.properties"/>
 
   <path id="build.classpath">
     <fileset dir="${build.lib.dir}" includes="*.jar"/>
     <fileset dir="${lib.dir}" includes="bsf.jar"/>
   </path>
 
   <!-- directory that contains emma.jar and emma_ant.jar: -->
   <property name="emma.dir" value="${build.lib.dir}" />
 
   <path id="emma.classpath">
     <pathelement location="${emma.dir}/emma.jar" />
     <pathelement location="${emma.dir}/emma_ant.jar" />
   </path>
 
   <patternset id="java.src.pattern">
     <include name="**/*.java"/>
     <exclude unless="bsf.present" name="org/jruby/javasupport/bsf/**/*.java"/>
     <exclude unless="jdk1.4+" name="**/XmlAstMarshal.java"/>
     <exclude unless="jdk1.4+" name="**/AstPersistenceDelegates.java"/>
     <exclude unless="sun-misc-signal" name="**/SunSignalFacade.java"/>
   </patternset>
 
   <patternset id="ruby.src.pattern">
     <include name="**/*.rb"/>
   </patternset>
   
   <patternset id="other.src.pattern">
     <include name="**/*.properties"/>
   </patternset>
   
   <taskdef name="retro" classname="net.sourceforge.retroweaver.ant.RetroWeaverTask" classpathref="build.classpath"/>
   
   <import file="netbeans-ant.xml" optional="true"/>
 
   <!-- Initializes the build -->
   <target name="init">
     <xmlproperty file="build-config.xml" keepRoot="false" collapseAttributes="true"/>
     <tstamp><format property="build.date" pattern="yyyy-MM-dd"/></tstamp>
     <property environment="env"/>
     <property name="version.ruby" value="${version.ruby.major}.${version.ruby.minor}"/>
     <!-- if ruby.home is not set, use env var -->
     <condition property="ruby.home" value="${env.RUBY_HOME}">
       <not><isset property="ruby.home"/></not>
     </condition>
     <property name="rdoc.archive" value="docs/rdocs.tar.gz"/>
     <uptodate property="docsNotNeeded" srcfile="${rdoc.archive}" targetfile="${basedir}/share/ri"/>
   </target>
   
   <target name="extract-rdocs" depends="init" unless="docsNotNeeded">
       <untar src="${rdoc.archive}" dest="${basedir}" compression="gzip"/>
   </target>
 
   <!-- Creates the directories needed for building -->
   <target name="prepare" depends="extract-rdocs">
     <mkdir dir="${build.dir}"/>
     <mkdir dir="${classes.dir}"/>
     <mkdir dir="${jruby.classes.dir}"/>
     <mkdir dir="${test.classes.dir}"/>
     <mkdir dir="${test.results.dir}"/>
     <mkdir dir="${html.test.results.dir}"/>
     <mkdir dir="${docs.dir}"/>
     <mkdir dir="${api.docs.dir}"/>
   </target>
 
   <!-- Checks if specific libs and versions are avaiable -->
   <target name="check-for-optional-java4-packages"
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
 
   <!-- Checks if specific libs and versions are avaiable -->
   <target name="check-for-optional-packages" if="jdk1.5+"
           depends="check-for-optional-java4-packages">
     <available property="sun-misc-signal"
                classname="sun.misc.Signal"/>
   </target>
 
   <!-- Builds the Ant tasks that we need later on in the build -->
   <target name="compile-tasks" depends="prepare">
     <copy todir="${jruby.classes.dir}">
         <fileset dir="${src.dir}">
             <include name="**/*.rb"/>
         </fileset>
     </copy>
     <copy todir="${jruby.classes.dir}/builtin">
         <fileset dir="${lib.dir}/ruby/site_ruby/1.8/builtin">
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
 
   <target name="compile-annotation-binder">
     <mkdir dir="${basedir}/src_gen"/>
         
     <javac destdir="${jruby.classes.dir}" debug="true" srcdir="${src.dir}" sourcepath="" classpathref="build.classpath" source="${javac.version}" target="${javac.version}" deprecation="true" encoding="UTF-8">
         <include name="org/jruby/anno/AnnotationBinder.java"/>
         <include name="org/jruby/anno/JRubyMethod.java"/>
         <include name="org/jruby/CompatVersion.java"/>
         <include name="org/jruby/runtime/Visibility.java"/>
         <include name="org/jruby/util/CodegenUtils.java"/>
     </javac>
   </target>
 
   <target name="compile-jruby" depends="compile-tasks, compile-annotation-binder, check-for-optional-packages">
     <!-- Generate binding logic ahead of time -->
     <apt factory="org.jruby.anno.AnnotationBinder" destdir="${jruby.classes.dir}" debug="true" source="${javac.version}" target="${javac.version}" deprecation="true" encoding="UTF-8">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <src path="${src.dir}"/>
       <patternset refid="java.src.pattern"/>
     </apt>
   </target>
 
   <target name="compile" depends="compile-jruby"
           description="Compile the source files for the project.">
   </target>
 
   <target name="generate-method-classes" depends="compile">
-    <!-- individual invokers for 1.8 -->
-    <echo message="Generating invokers for 1.8"/>
-    <java classname="org.jruby.Main" fork="false" failonerror="true">
+    <echo message="Generating invokers..."/>
+    <java classname="org.jruby.anno.InvokerGenerator" fork="true" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
-      <sysproperty key="jruby.dump_invocations" value="${jruby.classes.dir}"/>
-      <sysproperty key="jruby.native.net.protocol" value="true"/>
-      <arg value="-rjava"/>
-      <arg value="-rjruby"/>
-      <arg value="-rbigdecimal"/>
-      <arg value="-rdigest"/>
-      <arg value="-rzlib"/>
-      <arg value="-ryaml"/>
-      <arg value="-rstrscan"/>
-      <arg value="-riconv"/>
-      <arg value="-rweakref"/>
-      <arg value="-rthread"/>
-      <arg value="-rsocket"/>
-      <arg value="-rgenerator"/>
-      <arg value="-rnet/protocol"/>
-      <arg value="-rreadline"/>
-      <arg value="-retc"/>
-      <arg value="-renumerator"/>
-      <arg value="-rjruby/ext"/>
-      <arg value="-rnkf"/>
-      <arg value="-rio/wait"/>
-      <arg value="-e '1'"/>
-    </java>
-    
-    <!-- individual invokers for 1.9 -->
-    <echo message="Generating invokers for 1.9"/>
-    <java classname="org.jruby.Main" fork="false" failonerror="true">
-      <classpath refid="build.classpath"/>
-      <classpath path="${jruby.classes.dir}"/>
-      <sysproperty key="jruby.dump_invocations" value="${jruby.classes.dir}"/>
-      <sysproperty key="jruby.compat.version" value="RUBY1_9"/>
-      <arg value="-rfiber"/>
-      <arg value="-e '1'"/>
+      <arg value="src_gen/annotated_classes.txt"/>
+      <arg value="${jruby.classes.dir}"/>
     </java>
     
+    <echo message="Compiling populators..."/>
     <javac destdir="${jruby.classes.dir}" debug="true" source="${javac.version}" target="${javac.version}" deprecation="true" encoding="UTF-8">
        <classpath refid="build.classpath"/>
        <classpath path="${jruby.classes.dir}"/>
        <src path="src_gen"/>
        <patternset refid="java.src.pattern"/>
     </javac>
   </target>
 
   <target name="jar-jruby" unless="jar-up-to-date">
       <antcall target="generate-method-classes" inheritall="true"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc7.jar"/>
       <jarjar destfile="${lib.dir}/jruby.jar">
         <fileset dir="${jruby.classes.dir}">
           <exclude name="org/jruby/util/ant/**/*.class"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist-0.1.jar"/>
         <zipfileset src="${build.lib.dir}/jvyamlb-0.1.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jna.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jarjar>
   </target>
 
   <path id="lib.classpath">
     <pathelement location="${build.lib.dir}/retroweaver-rt-2.0.5.jar"/>
     <pathelement location="${build.lib.dir}/backport-util-concurrent.jar"/>
     <pathelement location="/usr/java/j2sdk1.4.2_16/jre/lib/rt.jar" />
     <pathelement location="${lib.dir}/bsf.jar" />
   </path>
 
   <target name="jar-1.4" depends="jar-jruby">
     <copy file="${lib.dir}/jruby.jar" tofile="${lib.dir}/jruby1.5.jar"/>
     <retro inputjar="${lib.dir}/jruby1.5.jar" outputjar="${lib.dir}/jruby-weaved.jar" target="1.4" failonerror="true" verify="true">
       <classpath>
         <path refid="lib.classpath"/>
         <pathelement location="${lib.dir}/jruby-weaved.jar" />
       </classpath>
     </retro>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc7.jar"/>
       <jarjar destfile="${lib.dir}/jruby.jar">
         <zipfileset src="${lib.dir}/jruby-weaved.jar"/>
         <zipfileset src="${build.lib.dir}/retroweaver-rt-2.0.5.jar"/>
         <zipfileset src="${build.lib.dir}/backport-util-concurrent.jar"/>
         <rule pattern="edu.emory.mathcs.backport.**" result="jruby.backport.@1"/>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       </jarjar>
     <delete file="${lib.dir}/jruby-weaved.jar"/>
     <delete file="${lib.dir}/jruby1.5.jar"/>
   </target>
 
   <target name="jar-complete-1.4" depends="jar-complete">
     <copy file="${lib.dir}/jruby-complete.jar" tofile="${lib.dir}/jruby-complete1.5.jar"/>
     <retro inputjar="${lib.dir}/jruby-complete1.5.jar" outputjar="${lib.dir}/jruby-complete.jar" target="1.4" failonerror="false" verify="true">
       <classpath>
         <path refid="lib.classpath"/>
         <pathelement location="${lib.dir}/jruby-complete.jar" />
       </classpath>
     </retro>
     <delete file="${lib.dir}/jruby-complete1.5.jar"/>
   </target>
 
     <target name="jar-complete" depends="generate-method-classes" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
       <property name="mainclass" value="org.jruby.Main"/>
       <property name="filename" value="jruby-complete.jar"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc7.jar"/>
       <jarjar destfile="${lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}">
           <exclude name="org/jruby/util/ant/**/*.class"/>
         </fileset>
         <fileset dir="${basedir}/lib/ruby/1.8">
           <include name="**/*.rb"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist-0.1.jar"/>
         <zipfileset src="${build.lib.dir}/jvyamlb-0.1.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jna.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
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
 
 
     <target name="jar-console" depends="generate-method-classes" description="Create the jruby graphical console jar">
           <antcall target="jar-complete">
               <param name="mainclass" value="org.jruby.demo.IRBConsole"/>
               <param name="filename" value="jruby-console.jar"/>
           </antcall>
     </target>
 
   <target name="jar" depends="init" description="Create the jruby.jar file">
     <antcall target="jar-jruby" inheritall="true"/>
   </target>
 
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
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
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
   
   <target name="compile-stdlib" unless="test">
     <copy todir="${build.dir}/stdlib">
         <fileset dir="${basedir}/lib/ruby/1.8">
           <include name="**/*.rb"/>
         </fileset>
     </copy>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true" dir="${build.dir}/stdlib">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.objectspace.enabled" value="true"/>
       <jvmarg value="-ea"/>
       
       <arg value="-I"/>
       <arg value="bin/"/>
       <arg value="-S"/>
       <arg value="jrubyc"/>
       <arg line="."/>
     </java>
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
 
   <target name="test" depends="
     copy-test-files,
     instrument,
     run-junit-compiled,
     run-junit-compiled-1.9,
     test-security-manager,
     coverage-report"
     description="Runs unit tests.">
   </target>
 
   <target name="test-compiled" depends="copy-test-files,run-junit-compiled,run-junit-precompiled"/>
   <target name="test-compiled-1.9" depends="copy-test-files,run-junit-compiled-1.9,run-junit-precompiled-1.9"/>
   <target name="test-interpreted" depends="copy-test-files,run-junit-interpreted"/>
   <target name="test-interpreted-1.9" depends="copy-test-files,run-junit-interpreted-1.9"/>
   <target name="test-reflected" depends="copy-test-files,run-junit-reflected-compiled,run-junit-reflected-precompiled,run-junit-reflected-interpreted"/>
   <target name="test-threadpool" depends="copy-test-files,run-junit-compiled-threadpool,run-junit-precompiled-threadpool,run-junit-interpreted-threadpool"/>
   
   <target name="test-all" depends="
       copy-test-files,
       instrument,
       test-security-manager,
       run-junit-interpreted,
       run-junit-compiled,
       run-junit-precompiled,
       run-junit-interpreted-1.9,
       run-junit-compiled-1.9,
       run-junit-precompiled-1.9,
       run-junit-reflected-interpreted,
       run-junit-reflected-compiled,
       run-junit-reflected-precompiled,
       run-junit-compiled-threadpool,
       run-junit-precompiled-threadpool,
       run-junit-interpreted-threadpool,
       run-junit-object-persistence,
       compile-stdlib,
       coverage-report"
 	  description="Runs unit tests in all modes."/>
 
   <target name="weave-tests" description="weave the test classes">
     <retro srcdir="${test.classes.dir}" target="1.4" failonerror="true" verify="true">
       <classpath>
         <path refid="lib.classpath"/>
         <pathelement location="${lib.dir}/jruby.jar" />
         <pathelement location="${build.lib.dir}/junit.jar" />
         <pathelement location="${build.lib.dir}/asm-3.0.jar" />
         <pathelement location="${test.classes.dir}" />
       </classpath>
     </retro>
   </target>
  
   <target name="test-1.4" depends="
     copy-test-files,
     jar-1.4,
     weave-tests,
     instrument,
     run-junit-compiled,
     run-junit-compiled-1.9,
     coverage-report"
   	description="Runs unit tests with weaved jruby classes.">
   </target>
 
   <target name="test-all-1.4" depends="
       copy-test-files,
       jar-1.4,
       weave-tests,
       instrument,
       run-junit-interpreted,
       run-junit-compiled,
       run-junit-precompiled,
       run-junit-interpreted-1.9,
       run-junit-compiled-1.9,
       run-junit-precompiled-1.9,
       run-junit-reflected-interpreted,
       run-junit-reflected-compiled,
       run-junit-reflected-precompiled,
       run-junit-compiled-threadpool,
       run-junit-precompiled-threadpool,
       run-junit-interpreted-threadpool,
       run-junit-object-persistence,
       compile-stdlib,
       coverage-report"
 	  description="Runs unit tests in all modes with weaved jruby classes."/>
 
   <!-- All junit permutations for 1.8 and 1.9 support -->
   <target name="run-junit-interpreted"><run-junit-1.8/></target>
   <target name="run-junit-interpreted-1.9"><run-junit-1.9/></target>
   <target name="run-junit-reflected-interpreted"><run-junit-1.8 reflection="true"/></target>
   <target name="run-junit-reflected-interpreted-1.9"><run-junit-1.9 reflection="true"/></target>
   <target name="run-junit-compiled"><run-junit-1.8 compile.mode="JIT" jit.threshold="0"/></target>
   <target name="run-junit-compiled-1.9"><run-junit-1.9 compile.mode="JIT" jit.threshold="0"/></target>
   <target name="run-junit-reflected-compiled"><run-junit-1.8 compile.mode="JIT" jit.threshold="0" reflection="true"/></target>
   <target name="run-junit-reflected-compiled-1.9"><run-junit-1.9 compile.mode="JIT" jit.threshold="0" reflection="true"/></target>
   <target name="run-junit-precompiled"><run-junit-1.8 compile.mode="FORCE" jit.threshold="0"/></target>
   <target name="run-junit-precompiled-1.9"><run-junit-1.9 compile.mode="FORCE" jit.threshold="0"/></target>
   <target name="run-junit-reflected-precompiled"><run-junit-1.8 compile.mode="FORCE" jit.threshold="0" reflection="true"/></target>
   <target name="run-junit-reflected-precompiled-1.9"><run-junit-1.9 compile.mode="FORCE" jit.threshold="0" reflection="true"/></target>
   <target name="run-junit-threadpool" depends="run-junit-interpreted-threadpool,run-junit-compiled-threadpool"/>
   <target name="run-junit-interpreted-threadpool"><run-junit-1.8 thread.pooling="true"/></target>
   <target name="run-junit-compiled-threadpool"><run-junit-1.8 compile.mode="JIT" jit.threshold="0" thread.pooling="true"/></target>
   <target name="run-junit-precompiled-threadpool"><run-junit-1.8 compile.mode="FORCE" jit.threshold="0" thread.pooling="true"/></target>
   
   <path id="test.class.path">
     <pathelement location="${jruby.instrumented.classes.dir}" />
     <fileset dir="${build.lib.dir}" includes="*.jar">
       <exclude name="joni.jar"/>
     </fileset>
     <pathelement path="${lib.dir}/bsf.jar"/>
     <pathelement path="${java.class.path}"/>
     <pathelement path="${lib.dir}/jruby.jar"/>
     <pathelement location="${test.classes.dir}"/>
     <pathelement path="${test.dir}/requireTest.jar"/>
     <pathelement location="${test.dir}"/>
   </path>
 
   <macrodef name="run-junit">
     <attribute name="jruby.version" default="ruby1_8"/>
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <attribute name="threadlocal" default="false"/>
     <element name="junit-tests"/>
     <sequential>
       <echo message="compile=@{compile.mode}, jit.threshold=@{jit.threshold}, jit.max=@{jit.max}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection} version=@{jruby.version}"/>
       <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${build.lib.dir}/junit.jar"/>
 
       <junit jvm="${jruby.test.jvm}" fork="yes" forkMode="once" haltonfailure="true" dir="${basedir}" maxmemory="${jruby.test.memory}" showoutput="true" timeout="1200000">
 	<classpath refid="test.class.path"/>
       
 	<sysproperty key="java.awt.headless" value="true"/>
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.lib" value="${lib.dir}"/>
 	<sysproperty key="jruby.compile.mode" value="@{compile.mode}"/>
 	<sysproperty key="jruby.jit.threshold" value="@{jit.threshold}"/>
 	<sysproperty key="jruby.jit.max" value="@{jit.max}"/>
 	<sysproperty key="jruby.compat.version" value="@{jruby.version}"/>
 	<sysproperty key="jruby.objectspace.enabled" value="@{objectspace.enabled}"/>
 	<sysproperty key="jruby.runtime.threadlocal" value="@{threadlocal}"/>
 	<sysproperty key="jruby.thread.pool.enabled" value="@{thread.pooling}"/>
 	<sysproperty key="jruby.reflection" value="@{reflection}"/>
 	<sysproperty key="jruby.jit.logging.verbose" value="true"/>
 	<sysproperty key="emma.coverage.out.file" value="${test.results.dir}/coverage.emma" />
 	<sysproperty key="emma.coverage.out.merge" value="true" />
 
 	<jvmarg value="-ea"/>
 
 	<formatter type="xml"/>
 	<formatter type="brief" usefile="false" />
 
 	<junit-tests/>
       </junit>
 
       <junitreport todir="${test.results.dir}">
 	<fileset dir="${test.results.dir}" includes="TEST-*.xml"/>
 	<report format="frames" todir="${html.test.results.dir}"/>
       </junitreport>
     </sequential>
   </macrodef>
   
   <!-- runs junit tests for 1.8 functionality -->
   <macrodef name="run-junit-1.8">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="reflection" default="false"/>
     <attribute name="thread.pooling" default="false"/>
 
     <sequential>
       <run-junit compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}" reflection="@{reflection}" thread.pooling="@{thread.pooling}">
 	<junit-tests>
 	  <test name="${test}" if="test"/>
 	  <test name="org.jruby.test.MainTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.ScriptTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.BFTSTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.JRubyTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.DubyTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MRITestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.RubiconTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.RubyTestTestSuite" todir="${test.results.dir}" unless="test"/>
 	</junit-tests>
       </run-junit>
     </sequential>
   </macrodef>
 
   <macrodef name="run-junit-1.9">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="reflection" default="false"/>
     <attribute name="thread.pooling" default="false"/>
 
     <sequential>
       <run-junit objectspace.enabled="false" jruby.version="ruby1_9" compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}" reflection="@{reflection}" thread.pooling="@{thread.pooling}">
 	<junit-tests>
 	  <test name="${test}" if="test"/>
 	  <test name="org.jruby.test.Ruby1_9TestSuite" todir="${test.results.dir}"  unless="test"/>
 	</junit-tests>
       </run-junit>
     </sequential>
   </macrodef>
   
   <!-- Runs junit tests for object persistence support -->
   <target name="run-junit-object-persistence">
     <run-junit objectspace.enabled="false" threadlocal="true">
       <junit-tests>
 	<test name="${test}" if="test"/>
 	<test name="org.jruby.test.ObjectPersistenceTestSuite" todir="${test.results.dir}" unless="test"/>
       </junit-tests>
     </run-junit>
   </target>
 
   <target name="test-security-manager">
     <java classname="org.jruby.Main" fork="true" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="java.security.manager" value=""/>
       <sysproperty key="java.security.policy" value="file:test/restricted.policy"/>
       <arg value="-e"/>
       <arg value="puts 'Restricted policy looks ok'"/>
     </java>
   </target>
 
   <target name="detect-stable-specs-need-update">
     <property file="${build.dir}/rubyspecs.current.revision"/>
     <condition property="stable-specs-need-update">
       <or>
         <not><available file="${build.dir}/rubyspec"/></not>
         <not>
           <equals
             arg1="${rubyspecs.revision}"
             arg2="${rubyspecs.current.revision}"/>
         </not>
       </or>
     </condition>
   </target>
 
   <target name="fetch-stable-specs" depends="prepare, detect-stable-specs-need-update" if="stable-specs-need-update">
   	<echo message="Downlodaing stable rubyspecs..."/>
   	<get src="http://git.rubini.us/?p=code;a=snapshot;h=${rubyspecs.revision};sf=tgz" dest="${build.dir}/rubyspec.tgz"/>
   	<!-- Wipe the old specs, if any -->
   	<delete dir="${build.dir}/rubyspec"/>
   	<mkdir dir="${build.dir}/rubyspec"/>
   	<untar src="${build.dir}/rubyspec.tgz" dest="${build.dir}/rubyspec" compression="gzip"/>
 	<chmod file="${build.dir}/rubyspec/code/mspec/bin/mspec" perm="755"/>
   	
   	<!-- Write down the revision of downloaded specs -->
     <propertyfile file="${build.dir}/rubyspecs.current.revision" comment="Revision of downloaded specs.">
       <entry key="rubyspecs.current.revision" value="${rubyspecs.revision}"/>
     </propertyfile>
   </target>
 
   <!-- NOTE: There are two different rubyspecs versions: stable and unstable.
        Stable ones are in known good state, and all known JRuby failures are excluded.
        The idea is that the stable specs runs must be clean, no failures.
        Unstable specs are the very latest, and might have new failures.
 
        Stable specs are downloaded as a snapshot of particular revision.
        Unstable specs are downloaded as a git repo. -->
 
   <!-- stable specs -->
   <target name="spec" depends="fetch-stable-specs, run-specs"
       description="Runs known good version of rubyspecs."/>
   <target name="spec-all" depends="fetch-stable-specs, run-specs-all"
       description="Runs known good version of rubyspecs without exclusions."/>
 
   <!-- latest, unstable specs -->
   <target name="spec-latest" depends="fetch-specs, run-specs"
       description="Runs the very latest rubyspecs."/>
   <target name="spec-latest-all" depends="fetch-specs, run-specs-all"
 	  description="Runs the very latest rubyspecs without exclusions."/>
   
   <target name="fetch-specs">
       <condition property="rubyspec-repo-exists">
           <available file="${build.dir}/rubyspec/code/.git"/>
       </condition>
       
       <antcall target="do-fetch-specs"/>
       <antcall target="do-update-specs"/>
   </target>
   
   <target name="download-specs" description="Download the specs as a tgz snapshot and unpack to build/rubyspec/code">
       <get src="http://git.rubini.us/?p=code;a=snapshot;sf=tgz" dest="${build.dir}/rubyspec.tgz"/>
       
       <mkdir dir="${build.dir}/rubyspec"/>
       <untar src="${build.dir}/rubyspec.tgz" dest="${build.dir}/rubyspec" compression="gzip"/>
       <chmod file="${build.dir}/rubyspec/code/mspec/bin/mspec" perm="755"/>
   </target>
   
   <target name="do-fetch-specs" unless="rubyspec-repo-exists">
       <!-- Stable specs might exist, so delete them -->
       <antcall target="clear-specs" inheritall="false"/>
       <exec dir="${build.dir}" executable="git">
           <arg value="clone"/>
           <arg value="--depth"/><arg value="1"/>
           <arg value="git://git.rubini.us/code"/>
           <arg value="rubyspec/code"/>
       </exec>
   </target>
   
   <target name="do-update-specs" if="rubyspec-repo-exists">
       <exec dir="${build.dir}/rubyspec/code" executable="git">
           <arg value="pull"/>
       </exec>
   </target>
   
   <target name="clear-specs">
       <delete dir="${build.dir}/rubyspec"/>
       <delete file="${build.dir}/rubyspec.tgz"/>
       <delete file="${build.dir}/rubyspecs.current.revision"/>
   </target>
   
   <target name="run-specs" depends="jar">
       <antcall target="run-specs-precompiled"/>
       <antcall target="run-specs-compiled"/>
       <antcall target="run-specs-interpreted"/>
   </target>
   <target name="run-specs-all" depends="jar"><antcall target="run-specs-all-precompiled"/></target>
   <target name="run-specs-compiled"><_run_specs_internal compile.mode="JIT" jit.threshold="0"/></target>
   <target name="run-specs-precompiled"><_run_specs_internal compile.mode="FORCE" jit.threshold="0"/></target>
   <target name="run-specs-all-precompiled"><_run_specs_all_internal compile.mode="FORCE" jit.threshold="0"/></target>
   <target name="run-specs-interpreted"><_run_specs_internal/></target>
   
   <target name="update-excludes">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${build.dir}/rubyspec/code">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.launch.inproc" value="false"/>
       
       <arg line="bin/mspec tag"/>
       <arg line="-t ${basedir}/bin/jruby"/>
       <arg line="--add fails --fail"/>
       <arg line="-X ${basedir}/test/spec_excludes"/>
       <arg value="spec/ruby/1.8"/>
     </java>
   </target>
 
   <target name="spec-show-excludes" depends="prepare"
     description="Prints out all currently excluded rubyspecs.">
 
     <available property="mspec-available"
       file="${build.dir}/rubyspec/code/bin/mspec"/>
     <fail unless="mspec-available"
       message="No rubyspecs found. Download them via 'ant spec'."/>
 
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${build.dir}/rubyspec/code">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.launch.inproc" value="false"/>
       <arg line="bin/mspec"/>
       <arg line="-t ${basedir}/bin/jruby"/>
       <arg line="-f s -g fails --dry-run"/>
       <arg line="-X ${basedir}/test/spec_excludes"/>
       <arg value="spec/ruby/1.8"/>
     </java>
   </target>
 
   <macrodef name="run-specs">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <element name="extra-args" optional="true"/>
 
     <sequential>
       <echo message="compile=@{compile.mode}, threshold=@{jit.threshold}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection}"/>
     
       <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true" dir="${build.dir}/rubyspec/code">
 	<classpath refid="build.classpath"/>
 	<classpath path="${jruby.classes.dir}"/>
       
 	<jvmarg value="-ea"/>
       
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.launch.inproc" value="false"/>
       
 	<!-- properties tweaked for individual runs -->
 	<sysproperty key="jruby.compile.mode" value="@{compile.mode}"/>
 	<sysproperty key="jruby.jit.threshold" value="@{jit.threshold}"/>
 	<sysproperty key="jruby.jit.max" value="@{jit.max}"/>
 	<sysproperty key="jruby.objectspace.enabled" value="@{objectspace.enabled}"/>
 	<sysproperty key="jruby.thread.pool.enabled" value="@{thread.pooling}"/>
 	<sysproperty key="jruby.reflection" value="@{reflection}"/>
       
 	<arg line="bin/mspec ci"/>
 	<arg value="-t"/><arg value="${basedir}/bin/jruby"/>
 	<arg line="-T -J-ea"/>
 	<arg value="-f"/><arg value="m"/>
 	<extra-args/>
 
 	<arg value="spec/ruby/1.8"/>
       </java>
     </sequential>
   </macrodef>
 
   <macrodef name="_run_specs_internal">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
 
     <sequential>
       <run-specs compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}">
 	<extra-args>
 	  <arg value="-X"/><arg value="${basedir}/test/spec_excludes"/>
 	</extra-args>
       </run-specs>
     </sequential>
   </macrodef>
 
   <macrodef name="_run_specs_all_internal">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
 
     <sequential>
       <run-specs compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}"/>
     </sequential>
   </macrodef>
 
   <macrodef name="fixEOLs">
     <sequential>
       <fixcrlf srcdir="dist/bin" excludes="*.bat" eol="lf"/>
       <fixcrlf srcdir="dist/bin" includes="*.bat" eol="crlf"/>
     </sequential>
   </macrodef>
 
   <target name="create-apidocs" depends="prepare" 
           description="Creates the Java API docs">
     <javadoc destdir="${api.docs.dir}" author="true" version="true" use="true" 
              windowtitle="JRuby API" source="${javac.version}" useexternalfile="true">
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
 
   <patternset id="dist.bindir.files">
     <include name="bin/*jruby*"/>
     <include name="bin/*gem*"/>
     <include name="bin/*ri*"/>
     <include name="bin/*rdoc*"/>
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
 
   <patternset id="dist.bin.files">
     <patternset refid="dist.files"/>
     <exclude name="lib/emma.jar"/>
     <exclude name="lib/emma_ant.jar"/>
     <exclude name="lib/junit.jar"/>
     <exclude name="lib/jarjar-1.0rc7.jar"/>
     <exclude name="docs/rdocs.tar.gz"/>
     <include name="share/**"/>
   </patternset>
 
   <patternset id="dist.src.files">
     <patternset refid="dist.files"/>
     <exclude name="share/**"/>
     <include name="src/**"/>
     <include name="test/**"/>
     <include name="build_lib/**"/>
     <include name="Rakefile"/>
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
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <fixEOLs/>
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
         <patternset refid="dist.bin.files"/>
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
         <patternset refid="dist.bin.files"/>
       </zipfileset>
     </zip>
   </target>
 
   <target name="dist-src" depends="jar">
     <mkdir dir="dist"/>
     <copy todir="dist">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <antcall target="install-gems">
       <param name="jruby.home" value="dist"/>
     </antcall>
     <fixEOLs/>
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
 
   <target name="dist-snapshot" depends="jar">
     <exec executable="${basedir}/bin/jruby" dir="${basedir}">
       <arg value="tool/snapshot.rb"/>
       <arg value="${jruby.classes.dir}/jruby.properties"/>
     </exec>
     <jar destfile="${lib.dir}/jruby.jar" update="true">
       <fileset dir="${jruby.classes.dir}">
         <include name="jruby.properties"/>
       </fileset>
     </jar>
     <property name="jar-up-to-date" value="true"/>
     <antcall target="dist-bin">
       <param file="${jruby.classes.dir}/jruby.properties"/>
     </antcall>
   </target>
 
   <target name="dist-snapshot-install-stuff">
     <unzip dest="${snapshot.install.dir}" src="jruby-bin-${version.jruby}.zip"/>
     <chmod perm="755" file="${snapshot.install.dir}/jruby-${version.jruby}/bin/jruby"/>
     <exec executable="${snapshot.install.dir}/jruby-${version.jruby}/bin/jruby"
       dir="${snapshot.install.dir}/jruby-${version.jruby}">
       <arg value="-v"/>
       <arg value="-e"/>
       <arg value="system('rm -f ${snapshot.install.dir}/current &amp;&amp; ln -s ${snapshot.install.dir}/jruby-${version.jruby} ${snapshot.install.dir}/current'); puts 'Successfully installed snapshot'"/>
     </exec>
   </target>
 
   <target name="dist-snapshot-install" depends="dist-snapshot" if="snapshot.install.dir">
     <antcall target="dist-snapshot-install-stuff">
       <param file="${jruby.classes.dir}/jruby.properties"/>
     </antcall>
   </target>
 
   <target name="dist" depends="dist-bin,dist-src"/>
 
   <target name="dist-clean">
     <delete includeEmptyDirs="true" quiet="true">
       <fileset dir=".">
         <include name="jruby-*.tar.gz"/>
         <include name="jruby-*.zip"/>
       </fileset>
       <fileset dir="dist" includes="**/*"/>
     </delete>
   </target>
 
   <target name="clean" depends="init" description="Cleans almost everything, leaves downloaded specs">
     <delete includeemptydirs="true" quiet="true">
         <fileset dir="${build.dir}" excludes="rubyspec**"/>
     </delete>
     <delete dir="${dist.dir}"/>
     <delete quiet="false">
         <fileset dir="${lib.dir}" includes="jruby*.jar"/>
     </delete>
     <delete dir="${api.docs.dir}"/>
     <delete dir="src_gen"/>
   </target>
 
   <target name="clean-all" depends="clean" description="Cleans everything, including downloaded specs">
   	<delete dir="${build.dir}"/>
   </target>
 
   <property name="nailgun.home" value="${basedir}/tool/nailgun"/>
 
   <target name="need-ng">
     <condition property="should.build.ng">
       <and>
         <os family="unix"/>
         <not><available file="${nailgun.home}/ng"/></not>
       </and>
     </condition>
   </target>
 
   <target name="build-ng" depends="need-ng" if="should.build.ng">
     <exec executable="make" dir="${nailgun.home}"/>
   </target>
 
   <target name="jruby-nailgun" depends="generate-method-classes,build-ng"
     description="Set up JRuby to be run with Nailgun (jruby-ng, jruby-ng-server)">
     <mkdir dir="${build.dir}/nailmain"/>
     <javac srcdir="${nailgun.home}/src/java" destdir="${build.dir}/nailmain"
       classpath="${nailgun.home}/nailgun-0.7.1.jar:${jruby.classes.dir}" debug="true"
       source="${javac.version}" target="${javac.version}"
       deprecation="true" encoding="UTF-8" />
     <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask"
       classpath="${build.lib.dir}/jarjar-1.0rc7.jar"/>
     <jarjar destfile="${nailgun.home}/jruby-nailgun.jar">
       <fileset dir="${jruby.classes.dir}">
         <exclude name="org/jruby/util/ant/**/*.class"/>
       </fileset>
       <fileset dir="${build.dir}/nailmain"/>
       <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
       <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
       <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
       <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
       <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
       <zipfileset src="${build.lib.dir}/bytelist-0.1.jar"/>
       <zipfileset src="${build.lib.dir}/jvyamlb-0.1.jar"/>
       <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
       <zipfileset src="${build.lib.dir}/joni.jar"/>
       <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
       <zipfileset src="${build.lib.dir}/jna.jar"/>
       <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
       <zipfileset src="${nailgun.home}/nailgun-0.7.1.jar"/>
       <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       <manifest>
         <attribute name="Built-By" value="${user.name}"/>
         <attribute name="Main-Class" value="org.jruby.Main"/>
       </manifest>
     </jarjar>
   </target>
 </project>
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 783dc1ee73..22b657c7c8 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1609 +1,1609 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyConstant;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.anno.TypePopulator;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.Dispatcher;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Module")
 public class RubyModule extends RubyObject {
     private static final boolean DEBUG = false;
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyModule;
             }
         };
         
         moduleClass.defineAnnotatedMethods(RubyModule.class);
         moduleClass.defineAnnotatedMethods(ModuleKernelMethods.class);
 
         return moduleClass;
     }
     
     public static class ModuleKernelMethods {
         @JRubyMethod
         public static IRubyObject autoload(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return RubyKernel.autoload(recv, arg0, arg1);
         }
         
         @JRubyMethod(name = "autoload?")
         public static IRubyObject autoload_p(IRubyObject recv, IRubyObject arg0) {
             return RubyKernel.autoload_p(recv, arg0);
         }
     }
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }    
     
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
     
     public Dispatcher dispatcher = Dispatcher.DEFAULT_DISPATCHER;
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     public RubyModule parent;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     protected String classId;
 
 
     // CONSTANT TABLE
     
     // Lock used for variableTable/constantTable writes. The RubyObject variableTable
     // write methods are overridden here to use this lock rather than Java
     // synchronization for faster concurrent writes for modules/classes.
     protected final ReentrantLock variableWriteLock = new ReentrantLock();
     
     protected transient volatile ConstantTableEntry[] constantTable =
         new ConstantTableEntry[CONSTANT_TABLE_DEFAULT_CAPACITY];
 
     protected transient int constantTableSize;
 
     protected transient int constantTableThreshold = 
         (int)(CONSTANT_TABLE_DEFAULT_CAPACITY * CONSTANT_TABLE_LOAD_FACTOR);
 
     private final Map<String, DynamicMethod> methods = new ConcurrentHashMap<String, DynamicMethod>(12, 0.75f, 1);
     
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax. 
     private transient List<ClassProvider> classProviders;
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (classProviders == null) {
             List<ClassProvider> cp = Collections.synchronizedList(new ArrayList<ClassProvider>());
             cp.add(provider);
             classProviders = cp;
         } else {
             synchronized(classProviders) {
                 if (!classProviders.contains(provider)) {
                     classProviders.add(provider);
                 }
             }
         }
     }
 
     public void removeClassProvider(ClassProvider provider) {
         if (classProviders != null) {
             classProviders.remove(provider);
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyClass clazz;
                 for (ClassProvider classProvider: classProviders) {
                     if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                         return clazz;
                     }
                 }
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyModule module;
                 for (ClassProvider classProvider: classProviders) {
                     if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                         return module;
                     }
                 }
             }
         }
         return null;
     }
 
     public Dispatcher getDispatcher() {
         return dispatcher;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
 
     public Map<String, DynamicMethod> getMethods() {
         return methods;
     }
     
 
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         dispatcher.clearIndex(MethodIndex.getIndex(name));
         getMethods().put(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result.insert(0, "::").insert(0, pName);
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         doIncludeModule(module);
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if(Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal = getRuntime().getNil();
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             }
         } catch(Exception e) {}
 
         
         for(String name : names) {
             this.fastSetConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         if (RubyInstanceConfig.INDEXED_METHODS) {
             defineAnnotatedMethodsIndexed(clazz);
         } else {
             defineAnnotatedMethodsIndividually(clazz);
         }
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap();
         
-        public void clumpFromClass(Class cls) {
+        public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
                 
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         String x = clazz.getSimpleName();
         int a = 1 + 1;
         try {
             String qualifiedName = clazz.getCanonicalName().replace('.', '$');
             
             if (DEBUG) System.out.println("looking for " + qualifiedName + "$Populator");
             
             Class populatorClass = Class.forName(qualifiedName + "$Populator");
             TypePopulator populator = (TypePopulator)populatorClass.newInstance();
             populator.populate(this);
         } catch (Throwable t) {
             if (DEBUG) System.out.println("Could not find it!");
             // fallback on non-pregenerated logic
             MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
             
             MethodClumper clumper = new MethodClumper();
-            clumper.clumpFromClass(clazz);
+            clumper.clump(clazz);
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
                 defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
             }
         }
         
     }
     
     private void defineAnnotatedMethodsIndexed(Class clazz) {
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         methodFactory.defineIndexedAnnotatedMethods(this, clazz, methodDefiningCallback);
     }
     
     private static MethodFactory.MethodDefiningCallback methodDefiningCallback = new MethodFactory.MethodDefiningCallback() {
         public void define(RubyModule module, JavaMethodDescriptor desc, DynamicMethod dynamicMethod) {
             JRubyMethod jrubyMethod = desc.anno;
             if (jrubyMethod.frame()) {
                 for (String name : jrubyMethod.name()) {
                     ASTInspector.FRAME_AWARE_METHODS.add(name);
                 }
             }
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 RubyModule metaClass = module.metaClass;
 
                 if (jrubyMethod.meta()) {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         metaClass.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             metaClass.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             metaClass.defineAlias(alias, baseName);
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         module.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             module.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             module.defineAlias(alias, baseName);
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         // module/singleton methods are all defined public
                         DynamicMethod moduleMethod = dynamicMethod.dup();
                         moduleMethod.setVisibility(Visibility.PUBLIC);
 
                         RubyModule singletonClass = module.getSingletonClass();
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = desc.name;
                             singletonClass.addMethod(desc.name, moduleMethod);
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 singletonClass.addMethod(name, moduleMethod);
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 singletonClass.defineAlias(alias, baseName);
                             }
                         }
                     }
                 }
             }
         }
     };
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             methodDefiningCallback.define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_undefined", getRuntime().newSymbol(name));
         }
     }
     
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(IRubyObject arg) {
         if (!arg.isModule()) {
             throw getRuntime().newTypeError(arg, getRuntime().getModule());
         }
         
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if ((p instanceof IncludedModuleWrapper) && ((IncludedModuleWrapper) p).getNonIncludedClass() == arg) {
                 return getRuntime().newBoolean(true);
             }
         }
         
         return getRuntime().newBoolean(false);
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = getMethods().put(name, method);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(existingMethod);
             }
             // note: duplicating functionality from putMethod, since we
             // remove/put atomically here
             dispatcher.clearIndex(MethodIndex.getIndex(name));
         }
     }
 
     public void removeMethod(ThreadContext context, String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
             
             getRuntime().getCacheMap().remove(method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // See if current class has method or if it has been cached here already
             DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
 
             if (method != null) {
                 return method;
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         DynamicMethod oldMethod = searchMethod(name);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         cacheMap.remove(oldMethod);
         if (oldMethod != oldMethod.getRealMethod()) {
             cacheMap.remove(oldMethod.getRealMethod());
         }
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
             DynamicMethod oldMethod = searchMethod(name);
             cacheMap.remove(oldMethod);
             if (oldMethod != oldMethod.getRealMethod()) {
                 cacheMap.remove(oldMethod.getRealMethod());
             }
             putMethod(name, new AliasMethod(this, method, oldName));
         }
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAt(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAt(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     // FIXME: create AttrReaderMethod, AttrWriterMethod, for faster attr access
     private void addAccessor(ThreadContext context, String internedName, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = getRuntime();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = context.getCurrentVisibility();
         if (attributeScope == Visibility.PRIVATE) {
             //FIXME warning
         } else if (attributeScope == Visibility.MODULE_FUNCTION) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     if (args.length != 1) Arity.raiseArgumentError(runtime, args.length, 1, 1);
 
                     return self.getInstanceVariables().fastSetInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == Visibility.PRIVATE);
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, name, originModule, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, name, originModule, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     // What is argument 1 for in this method? A Method or Proc object /OB
     @JRubyMethod(name = "define_method", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == Visibility.MODULE_FUNCTION) visibility = Visibility.PRIVATE;
         if (args.length == 1) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = getRuntime().newProc(Block.Type.LAMBDA, block);
             body = proc;
             
             // a normal block passed to define_method changes to do arity checking; make it a lambda
             proc.getBlock().type = Block.Type.LAMBDA;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (args.length == 2) {
             if (getRuntime().getProc().isInstance(args[1])) {
                 // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
                 RubyProc proc = (RubyProc)args[1];
                 body = proc;
 
                 newMethod = createProcMethod(name, visibility, proc);
             } else if (getRuntime().getMethod().isInstance(args[1])) {
                 RubyMethod method = (RubyMethod)args[1];
                 body = method;
 
                 newMethod = new MethodMethod(this, method.unbind(null), visibility);
             } else {
                 throw getRuntime().newTypeError("wrong argument type " + args[1].getType().getName() + " (expected Proc/Method)");
             }
         } else {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = getRuntime().fastNewSymbol(name);
 
         if (context.getPreviousVisibility() == Visibility.MODULE_FUNCTION) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
             callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         proc.getBlock().getBinding().getFrame().setKlazz(this);
         proc.getBlock().getBinding().getFrame().setName(name);
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         proc.getBlock().getBody().getStaticScope().setArgumentScope(true);
 
         Arity arity = proc.getBlock().arity();
         // just using required is broken...but no more broken than before zsuper refactoring
         proc.getBlock().getBody().getStaticScope().setRequiredArgs(arity.required());
 
         if(!arity.isFixed()) {
             proc.getBlock().getBody().getStaticScope().setRestArg(arity.required());
         }
 
         return new ProcMethod(this, proc, visibility);
     }
 
     @Deprecated
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method instanceof UndefinedMethod) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
 
         if (originalModule.hasVariables()){
             syncVariables(originalModule.getVariableList());
         }
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuffer buffer = new StringBuffer("#<Class:");
             if (attached != null) { // FIXME: figure out why we get null sometimes
                 if(attached instanceof RubyClass || attached instanceof RubyModule){
                     buffer.append(attached.inspect());
                 }else{
                     buffer.append(attached.anyToString());
                 }
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     @JRubyMethod(name = "===", required = 1)
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(isInstance(obj));
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze() {
         to_s();
         return super.freeze();
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) {
             return getRuntime().getTrue();
         } else if (((RubyModule) obj).isKindOfModule(this)) {
             return getRuntime().getFalse();
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) return getRuntime().newFixnum(0);
         if (!(obj instanceof RubyModule)) return getRuntime().getNil();
 
         RubyModule module = (RubyModule) obj;
 
         if (module.isKindOfModule(this)) {
             return getRuntime().newFixnum(1);
         } else if (this.isKindOfModule(module)) {
             return getRuntime().newFixnum(-1);
         }
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.isSame(type)) {
                 return true;
             }
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(Visibility.PUBLIC);
             block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = Visibility.PRIVATE)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(context, args[0].asJavaString().intern(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
diff --git a/src/org/jruby/anno/AnnotationBinder.java b/src/org/jruby/anno/AnnotationBinder.java
index 171b1b77ef..0755892438 100644
--- a/src/org/jruby/anno/AnnotationBinder.java
+++ b/src/org/jruby/anno/AnnotationBinder.java
@@ -1,445 +1,451 @@
 package org.jruby.anno;
 
 import com.sun.mirror.apt.*;
 import com.sun.mirror.declaration.*;
 import com.sun.mirror.type.*;
 import com.sun.mirror.util.*;
 
 import java.io.ByteArrayOutputStream;
 import java.io.FileOutputStream;
+import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Set;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.CompatVersion;
 
 import org.jruby.util.CodegenUtils;
 import static java.util.Collections.*;
 import static com.sun.mirror.util.DeclarationVisitors.*;
 
 /*
  * This class is used to run an annotation processor that lists class
  * names.  The functionality of the processor is analogous to the
  * ListClass doclet in the Doclet Overview.
  */
 public class AnnotationBinder implements AnnotationProcessorFactory {
     // Process any set of annotations
     private static final Collection<String> supportedAnnotations
         = unmodifiableCollection(Arrays.asList("org.jruby.anno.JRubyMethod", "org.jruby.anno.JRubyClass"));
 
     // No supported options
     private static final Collection<String> supportedOptions = emptySet();
 
     public Collection<String> supportedAnnotationTypes() {
         return supportedAnnotations;
     }
 
     public Collection<String> supportedOptions() {
         return supportedOptions;
     }
 
     public AnnotationProcessor getProcessorFor(
             Set<AnnotationTypeDeclaration> atds,
             AnnotationProcessorEnvironment env) {
         return new AnnotationBindingProcessor(env);
     }
 
     private static class AnnotationBindingProcessor implements AnnotationProcessor {
         private final AnnotationProcessorEnvironment env;
+        private final List<String> classNames = new ArrayList();
+        
         AnnotationBindingProcessor(AnnotationProcessorEnvironment env) {
             this.env = env;
         }
 
         public void process() {
 	    for (TypeDeclaration typeDecl : env.getSpecifiedTypeDeclarations())
 		typeDecl.accept(getDeclarationScanner(new RubyClassVisitor(),
 						      NO_OP));
+            
+            try {
+                FileWriter fw = new FileWriter("src_gen/annotated_classes.txt");
+                for (String name : classNames) {
+                    fw.write(name);
+                    fw.write('\n');
+                }
+                fw.close();
+            } catch (Exception e) {
+                throw new RuntimeException(e);
+            }
         }
 
-	private static class RubyClassVisitor extends SimpleDeclarationVisitor {
+	private class RubyClassVisitor extends SimpleDeclarationVisitor {
             private PrintStream out;
             private static final boolean DEBUG = false;
             public void visitClassDeclaration(ClassDeclaration cd) {
                 try {
                     String qualifiedName = cd.getQualifiedName().replace('.', '$');
                     
-                    // FIXME: This is gross, and is temporary until method handles are generated offline too
-                    if (qualifiedName.contains("JRubyApplet")) return;
-                    
                     // skip anything not related to jruby
                     if (!qualifiedName.contains("org$jruby")) return;
                     
                     ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
                     out = new PrintStream(bytes);
 
                     // start a new populator
                     out.println("/* THIS FILE IS GENERATED. DO NOT EDIT */");
                     //out.println("package org.jruby.anno;");
 
                     out.println("import org.jruby.RubyModule;");
                     out.println("import org.jruby.CompatVersion;");
                     out.println("import org.jruby.anno.TypePopulator;");
                     out.println("import org.jruby.internal.runtime.methods.CallConfiguration;");
                     out.println("import org.jruby.internal.runtime.methods.JavaMethod;");
                     out.println("import org.jruby.internal.runtime.methods.DynamicMethod;");
                     out.println("import org.jruby.runtime.Arity;");
                     out.println("import org.jruby.runtime.Visibility;");
 
                     out.println("public class " + qualifiedName + "$Populator implements TypePopulator {");
                     out.println("    public void populate(RubyModule cls) {");
                     if (DEBUG) out.println("        System.out.println(\"Using pregenerated populator: \" + \"" + cd.getSimpleName() + "Populator\");");
                     out.println("        JavaMethod javaMethod;");
                     out.println("        DynamicMethod moduleMethod;");
                     out.println("        CompatVersion compatVersion = cls.getRuntime().getInstanceConfig().getCompatVersion();");
 
                     Map<String, List<MethodDeclaration>> annotatedMethods = new HashMap();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods = new HashMap();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_8 = new HashMap();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_8 = new HashMap();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_9 = new HashMap();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_9 = new HashMap();
 
                     int methodCount = 0;
                     for (MethodDeclaration md : cd.getMethods()) {
                         JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                         if (anno == null) continue;
 
                         methodCount++;
 
                         String name = anno.name().length == 0 ? md.getSimpleName() : anno.name()[0];
 
                         List<MethodDeclaration> methodDescs;
                         Map<String, List<MethodDeclaration>> methodsHash = null;
                         if (md.getModifiers().contains(Modifier.STATIC)) {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = staticAnnotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = staticAnnotatedMethods1_9;
                             } else {
                                 methodsHash = staticAnnotatedMethods;
                             }
                         } else {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = annotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = annotatedMethods1_9;
                             } else {
                                 methodsHash = annotatedMethods;
                             }
                         }
 
                         methodDescs = methodsHash.get(name);
                         if (methodDescs == null) {
                             methodDescs = new ArrayList();
                             methodsHash.put(name, methodDescs);
                         }
 
                         methodDescs.add(md);
                     }
                     
                     if (methodCount == 0) {
                         // no annotated methods found, skip
                         return;
                     }
+                    
+                    classNames.add(getActualQualifiedName(cd));
 
                     processMethodDeclarations(staticAnnotatedMethods);
                     
                     out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                     processMethodDeclarations(staticAnnotatedMethods1_8);
                     out.println("        }");
                     
                     out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                     processMethodDeclarations(staticAnnotatedMethods1_9);
                     out.println("        }");
                     
                     processMethodDeclarations(annotatedMethods);
                     
                     out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                     processMethodDeclarations(annotatedMethods1_8);
                     out.println("        }");
                     
                     out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                     processMethodDeclarations(annotatedMethods1_9);
                     out.println("        }");
 
                     out.println("    }");
                     out.println("}");
                     out.close();
                     out = null;
                     
                     FileOutputStream fos = new FileOutputStream("src_gen/" + qualifiedName + "$Populator.java");
                     fos.write(bytes.toByteArray());
                     fos.close();
                 } catch (IOException ioe) {
                     System.err.println("FAILED TO GENERATE:");
                     ioe.printStackTrace();
                     System.exit(1);
                 }
             }
             
             public void processMethodDeclarations(Map<String, List<MethodDeclaration>> declarations) {
                 for (Map.Entry<String, List<MethodDeclaration>> entry : declarations.entrySet()) {
                     List<MethodDeclaration> list = entry.getValue();
                     
                     if (list.size() == 1) {
                         // single method, use normal logic
                         processMethodDeclaration(list.get(0));
                     } else {
                         // multimethod, new logic
                         processMethodDeclarationMulti(list.get(0));
                     }
                 }
             }
             
             public void processMethodDeclaration(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
-                    
-                    // declared type returns the qualified name without $ for inner classes!!!
-                    String qualifiedName;
-                    if (md.getDeclaringType().getDeclaringType() != null) {
-                        // inner class, use $ to delimit
-                        if (md.getDeclaringType().getDeclaringType().getDeclaringType() != null) {
-                            qualifiedName = md.getDeclaringType().getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getDeclaringType().getSimpleName() + "$" + md.getDeclaringType().getSimpleName();
-                        } else {
-                            qualifiedName = md.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getSimpleName();
-                        }
-                    } else {
-                        qualifiedName = md.getDeclaringType().getQualifiedName();
-                    }
+                    String qualifiedName = getActualQualifiedName(md.getDeclaringType());
                     
                     boolean hasContext = false;
                     boolean hasBlock = false;
                     
                     for (ParameterDeclaration pd : md.getParameters()) {
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
                     
                     int actualRequired = calculateActualRequired(md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
                     
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             false);
                     
                     out.println("        javaMethod = new " + annotatedBindingName + "(cls, Visibility." + anno.visibility() + ");");
                     out.println("        javaMethod.setArity(Arity.createArity(" + getArityValue(anno, actualRequired) + "));");
                     out.println("        javaMethod.setJavaName(\"" + md.getSimpleName() + "\");");
                     out.println("        javaMethod.setSingleton(" + isStatic + ");");
                     out.println("        javaMethod.setCallConfig(CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
             
             public void processMethodDeclarationMulti(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
-                    
-                    // declared type returns the qualified name without $ for inner classes!!!
-                    String qualifiedName;
-                    if (md.getDeclaringType().getDeclaringType() != null) {
-                        // inner class, use $ to delimit
-                        if (md.getDeclaringType().getDeclaringType().getDeclaringType() != null) {
-                            qualifiedName = md.getDeclaringType().getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getDeclaringType().getSimpleName() + "$" + md.getDeclaringType().getSimpleName();
-                        } else {
-                            qualifiedName = md.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getSimpleName();
-                        }
-                    } else {
-                        qualifiedName = md.getDeclaringType().getQualifiedName();
-                    }
+                    String qualifiedName = getActualQualifiedName(md.getDeclaringType());
                     
                     boolean hasContext = false;
                     boolean hasBlock = false;
                     
                     for (ParameterDeclaration pd : md.getParameters()) {
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
                     
                     int actualRequired = calculateActualRequired(md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
                     
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             true);
                     
                     out.println("        javaMethod = new " + annotatedBindingName + "(cls, Visibility." + anno.visibility() + ");");
                     out.println("        javaMethod.setArity(Arity.OPTIONAL);");
                     out.println("        javaMethod.setJavaName(\"" + md.getSimpleName() + "\");");
                     out.println("        javaMethod.setSingleton(" + isStatic + ");");
                     out.println("        javaMethod.setCallConfig(CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
-            
-            public static int getArityValue(JRubyMethod anno, int actualRequired) {
-                if (anno.optional() > 0 || anno.rest()) {
-                    return -(actualRequired + 1);
-                }
-                return actualRequired;
-            }
-    
-            public static String getCallConfigNameByAnno(JRubyMethod anno) {
-                return getCallConfigName(anno.frame(), anno.scope(), anno.backtrace());
-            }
 
-            public static String getCallConfigName(boolean frame, boolean scope, boolean backtrace) {
-                if (frame) {
-                    if (scope) {
-                        return "FRAME_AND_SCOPE";
-                    } else {
-                        return "FRAME_ONLY";
-                    }
-                } else if (scope) {
-                    if (backtrace) {
-                        return "BACKTRACE_AND_SCOPE";
+            private String getActualQualifiedName(TypeDeclaration td) {
+                // declared type returns the qualified name without $ for inner classes!!!
+                String qualifiedName;
+                if (td.getDeclaringType() != null) {
+                    // inner class, use $ to delimit
+                    if (td.getDeclaringType().getDeclaringType() != null) {
+                        qualifiedName = td.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + td.getDeclaringType().getSimpleName() + "$" + td.getSimpleName();
                     } else {
-                        return "SCOPE_ONLY";
+                        qualifiedName = td.getDeclaringType().getQualifiedName() + "$" + td.getSimpleName();
                     }
-                } else if (backtrace) {
-                    return "BACKTRACE_ONLY";
                 } else {
-                    return "NO_FRAME_NO_SCOPE";
+                    qualifiedName = td.getQualifiedName();
                 }
+
+                return qualifiedName;
             }
             
             private boolean tryClass(String className) {
                 Class tryClass = null;
                 try {
                     tryClass = Class.forName(className);
                 } catch (ClassNotFoundException cnfe) {
                 }
                     
                 return tryClass != null;
             }
             
             private int calculateActualRequired(int paramsLength, int optional, boolean rest, boolean isStatic, boolean hasContext, boolean hasBlock) {
                 int actualRequired;
                 if (optional == 0 && !rest) {
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) args--;
                         if (hasContext) args--;
                         if (hasBlock) args--;
 
                         // TODO: confirm expected args are IRubyObject (or similar)
                         actualRequired = args;
                     }
                 } else {
                     // optional args, so we have IRubyObject[]
                     // TODO: confirm
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) args--;
                         if (hasContext) args--;
                         if (hasBlock) args--;
 
                         // minus one more for IRubyObject[]
                         args--;
 
                         // TODO: confirm expected args are IRubyObject (or similar)
                         actualRequired = args;
                     }
 
                     if (actualRequired != 0) {
                         throw new RuntimeException("Combining specific args with IRubyObject[] is not yet supported");
                     }
                 }
                 
                 return actualRequired;
             }
             
             public void generateMethodAddCalls(MethodDeclaration md, JRubyMethod jrubyMethod) {
                 // TODO: This information
                 if (jrubyMethod.frame()) {
                     for (String name : jrubyMethod.name()) {
                         out.println("        org.jruby.compiler.ASTInspector.FRAME_AWARE_METHODS.add(\"" + name + "\");");
                     }
                 }
                 // TODO: compat version
 //                if(jrubyMethod.compat() == CompatVersion.BOTH ||
 //                        module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 //RubyModule metaClass = module.metaClass;
 
                 if (jrubyMethod.meta()) {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = md.getSimpleName();
                         out.println("        cls.getMetaClass().addMethod(\"" + baseName + "\", javaMethod);");
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             out.println("        cls.getMetaClass().addMethod(\"" + name + "\", javaMethod);");
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             out.println("        cls.getMetaClass().defineAlias(\"" + alias +"\", \"" + baseName + "\");");
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = md.getSimpleName();
                         out.println("        cls.addMethod(\"" + baseName + "\", javaMethod);");
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             out.println("        cls.addMethod(\"" + name + "\", javaMethod);");
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             out.println("        cls.defineAlias(\"" + alias +"\", \"" + baseName + "\");");
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         // module/singleton methods are all defined public
                         out.println("        moduleMethod = javaMethod.dup();");
                         out.println("        moduleMethod.setVisibility(Visibility.PUBLIC);");
 
 //                        RubyModule singletonClass = module.getSingletonClass();
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = md.getSimpleName();
                             out.println("        cls.getSingletonClass().addMethod(\"" + baseName + "\", moduleMethod);");
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 out.println("        cls.getSingletonClass().addMethod(\"" + name + "\", moduleMethod);");
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 out.println("        cls.getSingletonClass().defineAlias(\"" + alias + "\", \"" + baseName + "\");");
                             }
                         }
                     }
                 }
 //                }
             }
 	}
+        
+        public static int getArityValue(JRubyMethod anno, int actualRequired) {
+            if (anno.optional() > 0 || anno.rest()) {
+                return -(actualRequired + 1);
+            }
+            return actualRequired;
+        }
+
+        public static String getCallConfigNameByAnno(JRubyMethod anno) {
+            return getCallConfigName(anno.frame(), anno.scope(), anno.backtrace());
+        }
+
+        public static String getCallConfigName(boolean frame, boolean scope, boolean backtrace) {
+            if (frame) {
+                if (scope) {
+                    return "FRAME_AND_SCOPE";
+                } else {
+                    return "FRAME_ONLY";
+                }
+            } else if (scope) {
+                if (backtrace) {
+                    return "BACKTRACE_AND_SCOPE";
+                } else {
+                    return "SCOPE_ONLY";
+                }
+            } else if (backtrace) {
+                return "BACKTRACE_ONLY";
+            } else {
+                return "NO_FRAME_NO_SCOPE";
+            }
+        }
     }
 }
diff --git a/src/org/jruby/anno/InvokerGenerator.java b/src/org/jruby/anno/InvokerGenerator.java
index 34ea34d6cb..6a3dec7cc8 100644
--- a/src/org/jruby/anno/InvokerGenerator.java
+++ b/src/org/jruby/anno/InvokerGenerator.java
@@ -1,49 +1,66 @@
 package org.jruby.anno;
 
+import java.io.BufferedReader;
+import java.io.FileReader;
+import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import org.jruby.RubyModule.MethodClumper;
 import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
 import org.jruby.util.JRubyClassLoader;
 
 public class InvokerGenerator {
+    public static final boolean DEBUG = false;
+    
     public static void main(String[] args) throws Exception {
-        Class classList = Class.forName("org.jruby.generated.GeneratedInvokerClassList");
-        String[] classNames = (String[])classList.getField("classNames").get(null);
+        FileReader fr = new FileReader(args[0]);
+        BufferedReader br = new BufferedReader(fr);
+        
+        List<String> classNames = new ArrayList();
+        String line = br.readLine();
+        while (line != null) {
+            classNames.add(line);
+            line = br.readLine();
+        }
 
-        MethodClumper clumper = new MethodClumper();
+        DumpingInvocationMethodFactory dumper = new DumpingInvocationMethodFactory(args[1], new JRubyClassLoader(ClassLoader.getSystemClassLoader()));
 
         for (String name : classNames) {
-            Class cls = Class.forName(name);
+            MethodClumper clumper = new MethodClumper();
+            
+            try {
+                if (DEBUG) System.out.println("generating for class " + name);
+                Class cls = Class.forName(name);
 
-            clumper.clumpFromClass(cls);
-        }
+                clumper.clump(cls);
 
-        DumpingInvocationMethodFactory dumper = new DumpingInvocationMethodFactory(args[0], new JRubyClassLoader(ClassLoader.getSystemClassLoader()));
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
 
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
 
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
-        }
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
 
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
-        }
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
 
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
-        }
-
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
-        }
-
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
-        }
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
 
-        for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
-            dumper.getAnnotatedMethodClass(entry.getValue());
+                for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
+                    dumper.getAnnotatedMethodClass(entry.getValue());
+                }
+            } catch (Exception e) {
+                e.printStackTrace();
+                throw e;
+            }
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DumpingInvocationMethodFactory.java b/src/org/jruby/internal/runtime/methods/DumpingInvocationMethodFactory.java
index 9416cd8abe..a3c0c117ee 100644
--- a/src/org/jruby/internal/runtime/methods/DumpingInvocationMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/DumpingInvocationMethodFactory.java
@@ -1,66 +1,67 @@
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
 package org.jruby.internal.runtime.methods;
 
 import java.io.File;
 import java.io.FileOutputStream;
 
 import org.jruby.Ruby;
 import org.objectweb.asm.ClassWriter;
 
 /**
  * This factory extends InvocationMethodFactory by also dumping the classes to
  * .class files at runtime. It is used during the build to save off all
  * generated method handles to avoid that expense during startup.
  * 
  * @see org.jruby.internal.runtime.methods.InvocationMethodFactory
  */
 public class DumpingInvocationMethodFactory extends InvocationMethodFactory {
 
     private String dumpPath;
     
     public DumpingInvocationMethodFactory(String path, ClassLoader classLoader) {
         super(classLoader);
         this.dumpPath = path;
     }
 
-    protected Class endClass(Ruby runtime, ClassWriter cw, String name) {
+    @Override
+    protected Class endClass(ClassWriter cw, String name) {
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         String cname = name.replace('.','/');
         File f = new File(dumpPath,cname+".class");
         f.getParentFile().mkdirs();
         try {
             FileOutputStream fos = new FileOutputStream(f);
             fos.write(code);
             fos.close();
         } catch(Exception e) {
         }
-        return runtime.getJRubyClassLoader().defineClass(name, code);
+        return classLoader.defineClass(name, code);
     }
 }// DumpingInvocationMethodFactory
diff --git a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
index 1e5b3a0683..40d3971412 100644
--- a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
@@ -1,1192 +1,1196 @@
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
  * Copyright (C) 2006 The JRuby Community <www.jruby.org>
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
 package org.jruby.internal.runtime.methods;
 
 import java.io.PrintWriter;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.parser.StaticScope;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import static org.jruby.util.CodegenUtils.*;
 import static java.lang.System.*;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  * In order to avoid the overhead with reflection-based method handles, this
  * MethodFactory uses ASM to generate tiny invoker classes. This allows for
  * better performance and more specialization per-handle than can be supported
  * via reflection. It also allows optimizing away many conditionals that can
  * be determined once ahead of time.
  * 
  * When running in secured environments, this factory may not function. When
  * this can be detected, MethodFactory will fall back on the reflection-based
  * factory instead.
  * 
  * @see org.jruby.internal.runtime.methods.MethodFactory
  */
 public class InvocationMethodFactory extends MethodFactory implements Opcodes {
     private static final boolean DEBUG = false;
     
     /** The pathname of the super class for compiled Ruby method handles. */ 
     private final static String COMPILED_SUPER_CLASS = p(CompiledMethod.class);
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class));
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
     
     /** The super constructor signature for compile Ruby method handles. */
     private final static String COMPILED_SUPER_SIG = 
             sig(Void.TYPE, RubyModule.class, Arity.class, Visibility.class, StaticScope.class, Object.class, CallConfiguration.class);
     
     /** The super constructor signature for Java-based method handles. */
     private final static String JAVA_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class));
     
     /** The super constructor signature for indexed Java-based method handles. */
     private final static String JAVA_INDEXED_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class, int.class));
     
     /** The lvar index of "this" */
     public static final int THIS_INDEX = 0;
     
     /** The lvar index of the passed-in ThreadContext */
     public static final int THREADCONTEXT_INDEX = 1;
     
     /** The lvar index of the method-receiving object */
     public static final int RECEIVER_INDEX = 2;
     
     /** The lvar index of the RubyClass being invoked against */
     public static final int CLASS_INDEX = 3;
     
     /** The lvar index method name being invoked */
     public static final int NAME_INDEX = 4;
     
     /** The lvar index of the method args on the call */
     public static final int ARGS_INDEX = 5;
     
     /** The lvar index of the passed-in Block on the call */
     public static final int BLOCK_INDEX = 6;
 
     /** The classloader to use for code loading */
-    private JRubyClassLoader classLoader;
+    protected JRubyClassLoader classLoader;
     
     /**
      * Whether this factory has seen undefined methods already. This is used to
      * detect likely method handle collisions when we expect to create a new
      * handle for each call.
      */
     private boolean seenUndefinedClasses = false;
     
     /**
      * Construct a new InvocationMethodFactory using the specified classloader
      * to load code. If the target classloader is not an instance of
      * JRubyClassLoader, it will be wrapped with one.
      * 
      * @param classLoader The classloader to use, or to wrap if it is not a
      * JRubyClassLoader instance.
      */
     public InvocationMethodFactory(ClassLoader classLoader) {
         if (classLoader instanceof JRubyClassLoader) {
             this.classLoader = (JRubyClassLoader)classLoader;
         } else {
            this.classLoader = new JRubyClassLoader(classLoader);
         }
     }
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, Arity arity, 
             Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         String sup = COMPILED_SUPER_CLASS;
         Class scriptClass = scriptObject.getClass();
         String mname = scriptClass.getName() + "Invoker" + method + arity;
         synchronized (classLoader) {
             Class generatedClass = tryClass(mname);
 
             try {
                 if (generatedClass == null) {
                     String typePath = p(scriptClass);
                     String mnamePath = typePath + "Invoker" + method + arity;
                     ClassWriter cw = createCompiledCtor(mnamePath,sup);
                     SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null));
 
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
 
                     // invoke pre method stuff
                     if (!callConfig.isNoop()) {
                         invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, -1, true);
                     }
 
                     // store null for result var
                     mv.aconst_null();
                     mv.astore(8);
 
                     Label tryBegin = new Label();
                     Label tryEnd = new Label();
                     Label doFinally = new Label();
                     Label catchReturnJump = new Label();
                     Label catchRedoJump = new Label();
                     Label normalExit = new Label();
 
                     mv.trycatch(tryBegin, tryEnd, catchReturnJump, p(JumpException.ReturnJump.class));
                     mv.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
                     mv.trycatch(tryBegin, tryEnd, doFinally, null);
                     mv.trycatch(catchReturnJump, doFinally, doFinally, null);
                     mv.label(tryBegin);
 
                     mv.aload(0);
                     // FIXME we want to eliminate these type casts when possible
                     mv.getfield(mnamePath, "$scriptObject", ci(Object.class));
                     mv.checkcast(typePath);
                     mv.aload(THREADCONTEXT_INDEX);
                     mv.aload(RECEIVER_INDEX);
                     mv.aload(ARGS_INDEX);
                     mv.aload(BLOCK_INDEX);
                     mv.invokevirtual(typePath, method, sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class)));
 
                     // store result in temporary variable 8
                     mv.astore(8);
 
                     mv.label(tryEnd);
 
                     //call post method stuff (non-finally)
                     mv.label(normalExit);
                     if (!callConfig.isNoop()) {
                         invokeCallConfigPost(mv, COMPILED_SUPER_CLASS);
                     }
                     // reload and return result
                     mv.aload(8);
                     mv.visitInsn(ARETURN);
 
                     handleReturn(catchReturnJump,mv, doFinally, normalExit, COMPILED_SUPER_CLASS);
 
                     handleRedo(catchRedoJump, mv, doFinally);
 
                     // finally handling for abnormal exit
                     {
                         mv.label(doFinally);
 
                         //call post method stuff (exception raised)
                         if (!callConfig.isNoop()) {
                             invokeCallConfigPost(mv, COMPILED_SUPER_CLASS);
                         }
 
                         // rethrow exception
                         mv.athrow(); // rethrow it
                     }
 
                     generatedClass = endCall(cw,mv,mname);
                 }
 
                 return (DynamicMethod)generatedClass
                         .getConstructor(RubyModule.class, Arity.class, Visibility.class, StaticScope.class, Object.class, CallConfiguration.class)
                         .newInstance(implementationClass, arity, visibility, scope, scriptObject, callConfig);
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> descs) {
         JavaMethodDescriptor desc1 = descs.get(0);
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, true);
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
 
             try {
                 Class c = getAnnotatedMethodClass(descs);
                 int min = Integer.MAX_VALUE;
                 int max = 0;
                 boolean frame = false;
                 boolean scope = false;
                 boolean backtrace = false;
 
                 for (JavaMethodDescriptor desc: descs) {
                     int specificArity = -1;
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             if (desc.actualRequired <= 3) {
                                 specificArity = desc.actualRequired;
                             } else {
                                 specificArity = -1;
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             specificArity = desc.required;
                         }
                     }
 
                     if (specificArity < min) {
                         min = specificArity;
                     }
 
                     if (specificArity > max) {
                         max = specificArity;
                     }
                     
                     frame |= desc.anno.frame();
                     scope |= desc.anno.scope();
                     backtrace |= desc.anno.backtrace();
                 }
 
                 if (DEBUG) out.println(" min: " + min + ", max: " + max);
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc1.anno.visibility()});
 
                 ic.setArity(Arity.OPTIONAL);
                 ic.setJavaName(javaMethodName);
                 ic.setSingleton(desc1.isStatic);
                 ic.setCallConfig(CallConfiguration.getCallConfig(frame, scope, backtrace));
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method. Return the resulting generated or loaded class.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public Class getAnnotatedMethodClass(List<JavaMethodDescriptor> descs) throws Exception {
+        if (descs.size() == 1) {
+            // simple path, no multimethod
+            return getAnnotatedMethodClass(descs.get(0));
+        }
         JavaMethodDescriptor desc1 = descs.get(0);
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, true);
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             int min = Integer.MAX_VALUE;
             int max = 0;
             boolean block = false;
 
             for (JavaMethodDescriptor desc: descs) {
                 int specificArity = -1;
                 if (desc.optional == 0 && !desc.rest) {
                     if (desc.required == 0) {
                         if (desc.actualRequired <= 3) {
                             specificArity = desc.actualRequired;
                         } else {
                             specificArity = -1;
                         }
                     } else if (desc.required >= 0 && desc.required <= 3) {
                         specificArity = desc.required;
                     }
                 }
 
                 if (specificArity < min) {
                     min = specificArity;
                 }
 
                 if (specificArity > max) {
                     max = specificArity;
                 }
 
                 block |= desc.hasBlock;
             }
 
             if (DEBUG) out.println(" min: " + min + ", max: " + max);
 
             if (c == null) {
                 String superClass = null;
                 switch (min) {
                 case 0:
                     switch (max) {
                     case 1:
                         superClass = p(JavaMethod.JavaMethodZeroOrOne.class);
                         break;
                     case 2:
                         superClass = p(JavaMethod.JavaMethodZeroOrOneOrTwo.class);
                         break;
                     }
                     break;
                 case 1:
                     switch (max) {
                     case 2:
                         if (block) {
                             superClass = p(JavaMethod.JavaMethodOneOrTwoBlock.class);
                         } else {
                             superClass = p(JavaMethod.JavaMethodOneOrTwo.class);
                         }
                         break;
                     case 3:
                         superClass = p(JavaMethod.JavaMethodOneOrTwoOrThree.class);
                         break;
                     }
                     break;
                 case 2:
                     switch (max) {
                     case 3:
                         superClass = p(JavaMethod.JavaMethodTwoOrThree.class);
                         break;
                     }
                     break;
                 case -1:
                     // rest arg, use normal JavaMethod since N case will be defined
                     superClass = p(JavaMethod.JavaMethodNoBlock.class);
                     break;
                 }
                 if (superClass == null) throw new RuntimeException("invalid multi combination");
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, superClass);
 
                 for (JavaMethodDescriptor desc: descs) {
                     int specificArity = -1;
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             if (desc.actualRequired <= 3) {
                                 specificArity = desc.actualRequired;
                             } else {
                                 specificArity = -1;
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             specificArity = desc.required;
                         }
                     }
 
                     boolean hasBlock = desc.hasBlock;
                     SkinnyMethodAdapter mv = null;
 
                     mv = beginMethod(cw, "call", specificArity, hasBlock);
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
 
                     createAnnotatedMethodInvocation(desc, mv, superClass, specificArity, hasBlock);
 
                     endMethod(mv);
                 }
 
                 c = endClass(cw, generatedClassName);
             }
 
             return c;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc.declaringClassName, desc.isStatic, desc.actualRequired, desc.optional, false);
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             try {
                 Class c = getAnnotatedMethodClass(desc);
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc.anno.visibility()});
 
                 ic.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
                 ic.setJavaName(javaMethodName);
                 ic.setSingleton(desc.isStatic);
                 ic.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public Class getAnnotatedMethodClass(JavaMethodDescriptor desc) throws Exception {
         String javaMethodName = desc.name;
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc.declaringClassName, desc.isStatic, desc.actualRequired, desc.optional, false);
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             if (c == null) {
                 int specificArity = -1;
                 if (desc.optional == 0 && !desc.rest) {
                     if (desc.required == 0) {
                         if (desc.actualRequired <= 3) {
                             specificArity = desc.actualRequired;
                         } else {
                             specificArity = -1;
                         }
                     } else if (desc.required >= 0 && desc.required <= 3) {
                         specificArity = desc.required;
                     }
                 }
 
                 boolean block = desc.hasBlock;
 
                 String superClass = p(selectSuperClass(specificArity, block));
 
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, superClass);
                 SkinnyMethodAdapter mv = null;
 
                 mv = beginMethod(cw, "call", specificArity, block);
                 mv.visitCode();
                 Label line = new Label();
                 mv.visitLineNumber(0, line);
 
                 createAnnotatedMethodInvocation(desc, mv, superClass, specificArity, block);
 
                 endMethod(mv);
 
                 c = endClass(cw, generatedClassName);
             }
             
             return c;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public void prepareAnnotatedMethod(RubyModule implementationClass, JavaMethod javaMethod, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         javaMethod.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
         javaMethod.setJavaName(javaMethodName);
         javaMethod.setSingleton(desc.isStatic);
         javaMethod.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
     }
 
     /**
      * Use code generation to generate a set of method handles based on all
      * annotated methods in the target class.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#defineIndexedAnnotatedMethods
      */
     public void defineIndexedAnnotatedMethods(RubyModule implementationClass, Class type, MethodDefiningCallback callback) {
         String typePath = p(type);
         String superClass = p(JavaMethod.class);
         
         String generatedClassName = type.getName() + "Invoker";
         String generatedClassPath = typePath + "Invoker";
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             try {
                 ArrayList<Method> annotatedMethods = new ArrayList();
                 Method[] methods = type.getDeclaredMethods();
                 for (Method method : methods) {
                     JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
                     if (jrubyMethod == null) continue;
 
                     annotatedMethods.add(method);
                 }
                 // To ensure the method cases are generated the same way every time, we make a second sorted list
                 ArrayList<Method> sortedMethods = new ArrayList(annotatedMethods);
                 Collections.sort(sortedMethods, new Comparator<Method>() {
                     public int compare(Method a, Method b) {
                         return a.getName().compareTo(b.getName());
                     }
                 });
                 // But when binding the methods, we want to use the order from the original class, so we save the indices
                 HashMap<Method,Integer> indexMap = new HashMap();
                 for (int index = 0; index < sortedMethods.size(); index++) {
                     indexMap.put(sortedMethods.get(index), index);
                 }
 
                 if (c == null) {
                     ClassWriter cw = createIndexedJavaMethodCtor(generatedClassPath, superClass);
                     SkinnyMethodAdapter mv = null;
 
                     mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null));
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
 
                     Label defaultCase = new Label();
                     Label[] cases = new Label[sortedMethods.size()];
                     for (int i = 0; i < cases.length; i++) cases[i] = new Label();
 
                     // load method index
                     mv.aload(THIS_INDEX);
                     mv.getfield(generatedClassPath, "methodIndex", ci(int.class));
 
                     mv.tableswitch(0, cases.length - 1, defaultCase, cases);
 
                     for (int i = 0; i < sortedMethods.size(); i++) {
                         mv.label(cases[i]);
                         String callName = getAnnotatedMethodForIndex(cw, sortedMethods.get(i), i, superClass);
 
                         // invoke call#_method for method
                         mv.aload(THIS_INDEX);
                         mv.aload(THREADCONTEXT_INDEX);
                         mv.aload(RECEIVER_INDEX);
                         mv.aload(CLASS_INDEX);
                         mv.aload(NAME_INDEX);
                         mv.aload(ARGS_INDEX);
                         mv.aload(BLOCK_INDEX);
 
                         mv.invokevirtual(generatedClassPath, callName, COMPILED_CALL_SIG_BLOCK);
                         mv.areturn();
                     }
 
                     // if we fall off the switch, error.
                     mv.label(defaultCase);
                     mv.aload(THREADCONTEXT_INDEX);
                     mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                     mv.ldc("Error: fell off switched invoker for class: " + implementationClass.getBaseName());
                     mv.invokevirtual(p(Ruby.class), "newRuntimeError", sig(RaiseException.class, String.class));
                     mv.athrow();
 
                     c = endCall(cw, mv, generatedClassName);
                 }
 
                 for (int i = 0; i < annotatedMethods.size(); i++) {
                     Method method = annotatedMethods.get(i);
                     JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
                     if (jrubyMethod.frame()) {
                         for (String name : jrubyMethod.name()) {
                             ASTInspector.FRAME_AWARE_METHODS.add(name);
                         }
                     }
 
                     int index = indexMap.get(method);
                     JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class, int.class}).newInstance(new Object[]{implementationClass, jrubyMethod.visibility(), index});
 
                     ic.setArity(Arity.fromAnnotation(jrubyMethod));
                     ic.setJavaName(method.getName());
                     ic.setSingleton(Modifier.isStatic(method.getModifiers()));
                     ic.setCallConfig(CallConfiguration.getCallConfigByAnno(jrubyMethod));
 
                     callback.define(implementationClass, new JavaMethodDescriptor(method), ic);
                 }
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Emit code to check the arity of a call to a Java-based method.
      * 
      * @param jrubyMethod The annotation of the called method
      * @param method The code generator for the handle being created
      */
     private void checkArity(JRubyMethod jrubyMethod, SkinnyMethodAdapter method, int specificArity) {
         Label arityError = new Label();
         Label noArityError = new Label();
         
         switch (specificArity) {
         case 0:
         case 1:
         case 2:
         case 3:
             // for zero, one, two, three arities, JavaMethod.JavaMethod*.call(...IRubyObject[] args...) will check
             return;
         default:
             if (jrubyMethod.rest()) {
                 if (jrubyMethod.required() > 0) {
                     // just confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                 }
             } else if (jrubyMethod.optional() > 0) {
                 if (jrubyMethod.required() > 0) {
                     // confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                 }
 
                 // confirm maximum not greater than optional
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required() + jrubyMethod.optional());
                 method.if_icmpgt(arityError);
             } else {
                 // just confirm args length == required
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required());
                 method.if_icmpne(arityError);
             }
 
             method.go_to(noArityError);
 
             // Raise an error if arity does not match requirements
             method.label(arityError);
             method.aload(THREADCONTEXT_INDEX);
             method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             method.aload(ARGS_INDEX);
             method.ldc(jrubyMethod.required());
             method.ldc(jrubyMethod.required() + jrubyMethod.optional());
             method.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
             method.pop();
 
             method.label(noArityError);
         }
     }
 
     private ClassWriter createCompiledCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", COMPILED_SUPER_SIG, null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitVarInsn(ALOAD, 3);
         mv.visitVarInsn(ALOAD, 4);
         mv.visitVarInsn(ALOAD, 5);
         mv.visitVarInsn(ALOAD, 6);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", COMPILED_SUPER_SIG);
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createJavaMethodCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", JAVA_SUPER_SIG, null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_SUPER_SIG);
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createIndexedJavaMethodCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", JAVA_INDEXED_SUPER_SIG, null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitVarInsn(ILOAD, 3);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_INDEXED_SUPER_SIG);
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private void handleRedo(Label tryRedoJump, SkinnyMethodAdapter mv, Label tryFinally) {
         mv.label(tryRedoJump);
 
         // clear the redo
         mv.pop();
 
         // get runtime, dup it
         mv.aload(1);
         mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
         mv.invokevirtual(p(Ruby.class), "newRedoLocalJumpError", sig(RaiseException.class));
         mv.go_to(tryFinally);
     }
 
     private void handleReturn(Label catchReturnJump, SkinnyMethodAdapter mv, Label doFinally, Label normalExit, String typePath) {
         mv.label(catchReturnJump);
 
         mv.aload(0);
         mv.swap();
         mv.invokevirtual(typePath, "handleReturnJump", sig(IRubyObject.class, JumpException.ReturnJump.class));
 
         mv.astore(8);
         mv.go_to(normalExit);
     }
 
     private void invokeCallConfigPost(SkinnyMethodAdapter mv, String superClass) {
         //call post method stuff (non-finally)
         mv.aload(0);
         mv.aload(1);
         mv.invokevirtual(superClass, "post", sig(void.class, params(ThreadContext.class)));
     }
 
     private void invokeCallConfigPre(SkinnyMethodAdapter mv, String superClass, int specificArity, boolean block) {
         // invoke pre method stuff
         mv.aload(0); 
         mv.aload(THREADCONTEXT_INDEX); // tc
         mv.aload(RECEIVER_INDEX); // self
         mv.aload(NAME_INDEX); // name
         
         loadBlockForPre(mv, specificArity, block);
         
         mv.invokevirtual(superClass, "pre", sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class)));
     }
 
     private void loadArguments(SkinnyMethodAdapter mv, JRubyMethod jrubyMethod, int specificArity) {
         switch (specificArity) {
         default:
         case -1:
             mv.aload(ARGS_INDEX);
             break;
         case 0:
             // no args
             break;
         case 1:
             mv.aload(ARGS_INDEX);
             break;
         case 2:
             mv.aload(ARGS_INDEX);
             mv.aload(ARGS_INDEX + 1);
             break;
         case 3:
             mv.aload(ARGS_INDEX);
             mv.aload(ARGS_INDEX + 1);
             mv.aload(ARGS_INDEX + 2);
             break;
         }
     }
 
     private void loadBlockForPre(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         switch (specificArity) {
         default:
         case -1:
             if (getsBlock) {
                 // variable args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // variable args no block, load null block
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 0:
             if (getsBlock) {
                 // zero args with block
                 // FIXME: omit args index; subtract one from normal block index
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX - 1);
             } else {
                 // zero args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 1:
             if (getsBlock) {
                 // one arg with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // one arg, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 2:
             if (getsBlock) {
                 // two args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 1);
             } else {
                 // two args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 3:
             if (getsBlock) {
                 // three args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 2);
             } else {
                 // three args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         }
     }
 
     private void loadBlock(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         // load block if it accepts block
         switch (specificArity) {
         default:
         case -1:
             if (getsBlock) {
                 // all other arg cases with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // all other arg cases without block
             }
             break;
         case 0:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX - 1);
             } else {
                 // zero args, no block; do nothing
             }
             break;
         case 1:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // one arg, no block; do nothing
             }
             break;
         case 2:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 1);
             } else {
                 // two args, no block; do nothing
             }
             break;
         case 3:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 2);
             } else {
                 // three args, no block; do nothing
             }
             break;
         }
     }
 
     private void loadReceiver(String typePath, JavaMethodDescriptor desc, SkinnyMethodAdapter mv) {
         // load target for invocations
         if (Modifier.isStatic(desc.modifiers)) {
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
             
             // load self object as IRubyObject, for recv param
             mv.aload(RECEIVER_INDEX);
         } else {
             // load receiver as original type for virtual invocation
             mv.aload(RECEIVER_INDEX);
             mv.checkcast(typePath);
             
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
         }
     }
     private Class tryClass(String name) {
         try {
             Class c = null;
             if (classLoader == null) {
                 c = Class.forName(name, true, classLoader);
             } else {
                 c = classLoader.loadClass(name);
             }
             
             if (c != null && seenUndefinedClasses) {
                 System.err.println("WARNING: while creating new bindings, found an existing binding; likely a collision: " + name);
                 Thread.dumpStack();
             }
             
             return c;
         } catch(Exception e) {
             seenUndefinedClasses = true;
             return null;
         }
     }
 
     protected Class endCall(ClassWriter cw, MethodVisitor mv, String name) {
         endMethod(mv);
         return endClass(cw, name);
     }
 
     protected void endMethod(MethodVisitor mv) {
         mv.visitMaxs(0,0);
         mv.visitEnd();
     }
 
     protected Class endClass(ClassWriter cw, String name) {
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         CheckClassAdapter.verify(new ClassReader(code), false, new PrintWriter(System.err));
          
         return classLoader.defineClass(name, code);
     }
     
     private void loadArgument(MethodVisitor mv, int argsIndex, int argIndex) {
         mv.visitVarInsn(ALOAD, argsIndex);
         mv.visitLdcInsn(new Integer(argIndex));
         mv.visitInsn(AALOAD);
     }
     
     private SkinnyMethodAdapter beginMethod(ClassWriter cw, String methodName, int specificArity, boolean block) {
         switch (specificArity) {
         default:
         case -1:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG, null, null));
             }
         case 0:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO, null, null));
             }
         case 1:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE, null, null));
             }
         case 2:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO, null, null));
             }
         case 3:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE, null, null));
             }
         }
     }
     
     private Class selectSuperClass(int specificArity, boolean block) {
         switch (specificArity) {
         default:
         case -1:
             if (block) {
                 return JavaMethod.class;
             } else {
                 return JavaMethod.JavaMethodNoBlock.class;
             }
         case 0:
             if (block) {
                 return JavaMethod.JavaMethodZeroBlock.class;
             } else {
                 return JavaMethod.JavaMethodZero.class;
             }
         case 1:
             if (block) {
                 return JavaMethod.JavaMethodOneBlock.class;
             } else {
                 return JavaMethod.JavaMethodOne.class;
             }
         case 2:
             if (block) {
                 return JavaMethod.JavaMethodTwoBlock.class;
             } else {
                 return JavaMethod.JavaMethodTwo.class;
             }
         case 3:
             if (block) {
                 return JavaMethod.JavaMethodThreeBlock.class;
             } else {
                 return JavaMethod.JavaMethodThree.class;
             }
         }
     }
 
     private String getAnnotatedMethodForIndex(ClassWriter cw, Method method, int index, String superClass) {
         String methodName = "call" + index + "_" + method.getName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_BLOCK, null, null));
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         // TODO: indexed methods do not use specific arity yet
         createAnnotatedMethodInvocation(new JavaMethodDescriptor(method), mv, superClass, -1, true);
         endMethod(mv);
         
         return methodName;
     }
 
     private void createAnnotatedMethodInvocation(JavaMethodDescriptor desc, SkinnyMethodAdapter method, String superClass, int specificArity, boolean block) {
         String typePath = desc.declaringClassPath;
         String javaMethodName = desc.name;
 
         checkArity(desc.anno, method, specificArity);
         
         CallConfiguration callConfig = CallConfiguration.getCallConfigByAnno(desc.anno);
         if (!callConfig.isNoop()) {
             invokeCallConfigPre(method, superClass, specificArity, block);
         }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label doFinally = new Label();
         Label catchReturnJump = new Label();
         Label catchRedoJump = new Label();
         Label normalExit = new Label();
 
         if (!callConfig.isNoop() || block) {
             method.trycatch(tryBegin, tryEnd, catchReturnJump, p(JumpException.ReturnJump.class));
             method.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
             method.trycatch(tryBegin, tryEnd, doFinally, null);
             method.trycatch(catchReturnJump, doFinally, doFinally, null);
         }
         
         method.label(tryBegin);
         {
             loadReceiver(typePath, desc, method);
             
             loadArguments(method, desc.anno, specificArity);
             
             loadBlock(method, specificArity, block);
 
             if (Modifier.isStatic(desc.modifiers)) {
                 // static invocation
                 method.invokestatic(typePath, javaMethodName, desc.signature);
             } else {
                 // virtual invocation
                 method.invokevirtual(typePath, javaMethodName, desc.signature);
             }
         }
                 
         // store result in temporary variable 8
         if (!callConfig.isNoop() || block) {
             method.astore(8);
 
             method.label(tryEnd);
  
             method.label(normalExit);
 
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(method, superClass);
             }
 
             // reload and return result
             method.aload(8);
         }
         method.visitInsn(ARETURN);
 
         if (!callConfig.isNoop() || block) {
             handleReturn(catchReturnJump,method, doFinally, normalExit, superClass);
 
             handleRedo(catchRedoJump, method, doFinally);
 
             // finally handling for abnormal exit
             method.label(doFinally);
             {
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(method, superClass);
                 }
 
                 // rethrow exception
                 method.athrow(); // rethrow it
             }
         }
     }
 }
