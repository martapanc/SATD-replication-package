diff --git a/build.xml b/build.xml
index d02f432a5f..9ccdbf3950 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1392 +1,1392 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <project basedir="." default="jar" name="JRuby">
   <description>JRuby is a pure Java implementation of a Ruby interpreter.</description>
 
   <property name="base.dir" location="${basedir}"/>
 
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
     <uptodate property="docsNotNeeded" srcfile="${rdoc.archive}" targetfile="${basedir}/share/ri/1.8/system/created.rid"/>
   </target>
 
   <target name="extract-rdocs" depends="init" unless="docsNotNeeded">
       <untar src="${rdoc.archive}" dest="${basedir}" compression="gzip"/>
       <touch file="${basedir}/share/ri/1.8/system/created.rid"/>
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
 
   <target name="prepare-resources" depends="prepare">
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
         <include name="org/jruby/anno/FrameField.java"/>
         <include name="org/jruby/anno/AnnotationBinder.java"/>
         <include name="org/jruby/anno/JRubyMethod.java"/>
         <include name="org/jruby/anno/FrameField.java"/>
         <include name="org/jruby/CompatVersion.java"/>
         <include name="org/jruby/runtime/Visibility.java"/>
         <include name="org/jruby/util/CodegenUtils.java"/>
     </javac>
   </target>
 
   <target name="compile-jruby" depends="prepare-resources, compile-annotation-binder, check-for-optional-packages">
     <!-- Generate binding logic ahead of time -->
     <apt factory="org.jruby.anno.AnnotationBinder" destdir="${jruby.classes.dir}" debug="true" source="${javac.version}" target="${javac.version}" deprecation="true" encoding="UTF-8">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <src path="${src.dir}"/>
       <patternset refid="java.src.pattern"/>
       <compilerarg line="-XDignore.symbol.file=true"/>
       <compilerarg line="-J-Xmx${jruby.compile.memory}"/>
     </apt>
   </target>
 
   <target name="compile" depends="compile-jruby"
           description="Compile the source files for the project.">
   </target>
 
   <target name="generate-method-classes" depends="compile">
     <available file="src_gen/annotated_classes.txt" property="annotations.changed"/>
     <antcall target="_gmc_internal_"/>
   </target>
 
   <target name="_gmc_internal_" if="annotations.changed">
     <echo message="Generating invokers..."/>
     <java classname="org.jruby.anno.InvokerGenerator" fork="true" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <!-- uncomment this line when building on a JVM with invokedynamic
       <jvmarg line="-XX:+InvokeDynamic"/>
       -->
       <arg value="src_gen/annotated_classes.txt"/>
       <arg value="${jruby.classes.dir}"/>
     </java>
 
     <echo message="Compiling populators..."/>
     <javac destdir="${jruby.classes.dir}" debug="true" source="${javac.version}" target="${javac.version}" deprecation="true" encoding="UTF-8">
        <classpath refid="build.classpath"/>
        <classpath path="${jruby.classes.dir}"/>
        <src path="src_gen"/>
        <patternset refid="java.src.pattern"/>
     </javac>
 
     <delete file="src_gen/annotated_classes.txt"/>
   </target>
 
   <target name="generate-unsafe" depends="compile">
       <available file="${jruby.classes.dir}/org/jruby/util/unsafe/GeneratedUnsafe.class" property="unsafe.not.needed"/>
       <antcall target="_gu_internal_"/>
   </target>
 
   <target name="_gu_internal_" unless="unsafe.not.needed">
     <echo message="Generating Unsafe impl..."/>
     <java classname="org.jruby.util.unsafe.UnsafeGenerator" fork="true" failonerror="true">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <!-- uncomment this line when building on a JVM with invokedynamic
         <jvmarg line="-XX:+InvokeDynamic"/>
         -->
         <arg value="org.jruby.util.unsafe"/>
         <arg value="${jruby.classes.dir}/org/jruby/util/unsafe"/>
     </java>
   </target>
 
   <target name="_update_scm_revision_">
       <java classname="org.jruby.Main" resultproperty="snapshot.result" errorproperty="snapshot.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <arg value="tool/snapshot.rb"/>
         <arg value="${jruby.classes.dir}/org/jruby/jruby.properties"/>
       </java>
   </target>
 
   <target name="jar-jruby" depends="generate-method-classes, generate-unsafe" unless="jar-up-to-date">
       <antcall target="_update_scm_revision_"/>
 
       <jar destfile="${lib.dir}/jruby.jar" compress="true" index="true">
         <fileset dir="${jruby.classes.dir}"/>
         <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist-1.0.1.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jvyamlb-0.2.5.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jna.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jar>
   </target>
 
   <target name="jar-jruby-dist" depends="generate-method-classes, generate-unsafe" unless="jar-up-to-date">
       <antcall target="_update_scm_revision_"/>
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc8.jar"/>
       <jarjar destfile="${lib.dir}/jruby.jar" compress="true">
         <fileset dir="${jruby.classes.dir}"/>
         <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist-1.0.1.jar"/>
         <zipfileset src="${build.lib.dir}/jvyamlb-0.2.5.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jna.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       </jarjar>
       <antcall target="_osgify-jar_">
         <param name="bndfile" value="jruby.bnd" />
         <param name="jar.wrap" value="${lib.dir}/jruby.jar" />
       </antcall>
   </target>
 
   <!-- Use Bnd to wrap the JAR generated by jarjar in above task -->
   <target name="_osgify-jar_">
     <filter token="JRUBY_VERSION" value="${version.jruby}"/>
     <copy file="${basedir}/jruby.bnd.template" tofile="${build.dir}/${bndfile}" filtering="true"/>
     <taskdef resource="aQute/bnd/ant/taskdef.properties"
       classpath="${build.lib.dir}/bnd-0.0.249.jar"/>
     <bndwrap definitions="${build.dir}" output="${lib.dir}">
       <fileset file="${jar.wrap}" />
     </bndwrap>
     <move file="${jar.wrap}$" tofile="${jar.wrap}"
       overwrite="true" />
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
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc8.jar"/>
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
 
     <target name="jar-complete" depends="generate-method-classes, generate-unsafe" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
       <property name="mainclass" value="org.jruby.Main"/>
       <property name="filename" value="jruby-complete.jar"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc8.jar"/>
       <property name="jar-complete-home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
       <mkdir dir="${jar-complete-home}"/>
       <copy todir="${jar-complete-home}">
         <fileset dir="${basedir}">
           <patternset refid="dist.bindir.files"/>
           <patternset refid="dist.lib.files"/>
         </fileset>
       </copy>
       <copy todir="${build.dir}/jar-complete">
         <fileset dir="lib/ruby/1.8" includes="**/*"/>
       </copy>
 
       <java classname="${mainclass}" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
         <classpath>
           <pathelement location="${jruby.classes.dir}"/>
           <pathelement location="${build.dir}/jar-complete"/>
           <path refid="build.classpath"/>
         </classpath>
         <sysproperty key="jruby.home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
         <arg value="--command"/>
         <arg value="maybe_install_gems"/>
         <arg value="rspec"/>
         <arg value="rake"/>
         <arg value="--no-ri"/>
         <arg value="--no-rdoc"/>
         <arg value="--env-shebang"/>
       </java>
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0rc8.jar"/>
       <jarjar destfile="${lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}"/>
         <fileset dir="${build.dir}/jar-complete">
           <exclude name="META-INF/jruby.home/lib/ruby/1.8/**"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.0.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist-1.0.1.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jvyamlb-0.2.5.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jna.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="${mainclass}"/>
         </manifest>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       </jarjar>
       <antcall target="_osgify-jar_">
         <param name="bndfile" value="jruby-complete.bnd" />
         <param name="jar.wrap" value="${lib.dir}/jruby-complete.jar" />
       </antcall>
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
   <target name="jar-dist" depends="init" description="Create the jruby.jar file for distribution. This version uses JarJar Links to rewrite some packages.">
     <antcall target="jar-jruby-dist" inheritall="true"/>
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
       <src path="${spec.dir}"/>
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
 
   <target name="install-build-gems" depends="install-gems">
     <property name="jruby.home" value="${basedir}"/>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${jruby.home}"/>
       <arg value="--command"/>
       <arg value="maybe_install_gems"/>
       <arg value="mocha"/>
       <arg value="jruby-openssl"/>
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
     test-security-manager,
     test-rake-targets,
     coverage-report"
     description="Runs unit tests.">
   </target>
 
   <target name="test-compiled" depends="copy-test-files,run-junit-compiled,run-junit-precompiled"/>
   <target name="test-compiled-1.9" depends="copy-test-files,run-junit-compiled-1.9,run-junit-precompiled-1.9"/>
   <target name="test-interpreted" depends="copy-test-files,run-junit-interpreted,test-interpreted-1.9"/>
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
       run-junit-reflected-precompiled,
       run-junit-interpreted-threadpool,
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
   	description="Runs unit tests with 1.4-weaved jruby classes.">
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
       run-junit-reflected-precompiled,
       run-junit-precompiled-threadpool,
       compile-stdlib,
       coverage-report"
 	  description="Runs unit tests in all modes with weaved jruby classes."/>
 
   <!-- All junit permutations for 1.8 and 1.9 support -->
   <target name="run-junit-interpreted"><run-junit-1.8/></target>
   <target name="run-junit-interpreted-1.9"><run-junit-1.9/></target>
   <target name="run-junit-reflected-interpreted"><run-junit-1.8 reflection="true"/></target>
   <target name="run-junit-reflected-interpreted-1.9"><run-junit-1.9 reflection="true"/></target>
   <target name="run-junit-compiled"><run-junit-1.8 compile.mode="JIT" jit.threshold="0"/></target>
   <!-- temporarily disabled during compiler work -->
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
       <exclude name="jcodings.jar"/>
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
     <element name="junit-tests"/>
     <sequential>
       <echo message="compile=@{compile.mode}, jit.threshold=@{jit.threshold}, jit.max=@{jit.max}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection} version=@{jruby.version}"/>
       <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${build.lib.dir}/junit.jar"/>
 
       <junit jvm="${jruby.test.jvm}" fork="yes" forkMode="once" haltonfailure="true" dir="${basedir}" maxmemory="${jruby.test.memory}" showoutput="true" timeout="1800000">
 	<classpath refid="test.class.path"/>
 
 	<sysproperty key="java.awt.headless" value="true"/>
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.lib" value="${lib.dir}"/>
 	<sysproperty key="jruby.compile.mode" value="@{compile.mode}"/>
 	<sysproperty key="jruby.jit.threshold" value="@{jit.threshold}"/>
 	<sysproperty key="jruby.jit.max" value="@{jit.max}"/>
 	<sysproperty key="jruby.compat.version" value="@{jruby.version}"/>
 	<sysproperty key="jruby.objectspace.enabled" value="@{objectspace.enabled}"/>
 	<sysproperty key="jruby.thread.pool.enabled" value="@{thread.pooling}"/>
 	<sysproperty key="jruby.reflection" value="@{reflection}"/>
 	<sysproperty key="jruby.jit.logging.verbose" value="true"/>
 	<sysproperty key="jruby.compile.lazyHandles" value="true"/>
 	<sysproperty key="emma.coverage.out.file" value="${test.results.dir}/coverage.emma" />
 	<sysproperty key="emma.coverage.out.merge" value="true" />
 
 	<jvmarg value="-ea"/>
 
         <!-- force client VM to improve test run times -->
         <jvmarg value="-client"/>
 
         <!-- set a larger max permgen, since the tests load a lot of bytecode -->
         <jvmarg value="-XX:MaxPermSize=128M"/>
 
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
 	  <test name="org.jruby.test.ScriptTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.BFTSTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.JRubyTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MRITestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.RubiconTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.RubyTestTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MainTestSuite" todir="${test.results.dir}" unless="test"/>
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
 
   <target name="test-rake-targets" depends="install-gems">
       <run-rake rake.targets="spec:ji:quiet"/>
       <run-rake rake.targets="test:tracing" jruby.args="--debug"/>
       <run-rake rake.targets="spec:compiler"/>
       <run-rake rake.targets="spec:ffi"/>
   </target>
 
   <macrodef name="run-rake">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <attribute name="inproc" default="true"/>
     <attribute name="jruby.args" default=""/>
     <attribute name="rake.targets"/>
     <attribute name="dir" default="${base.dir}"/>
     <attribute name="jvm.args" default="-ea"/>
 
     <sequential>
       <echo message="compile=@{compile.mode}, threshold=@{jit.threshold}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection}"/>
 
       <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true" dir="@{dir}">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
 
         <jvmarg line="@{jvm.args}"/>
 
         <sysproperty key="jruby.home" value="${basedir}"/>
         <sysproperty key="jruby.launch.inproc" value="false"/>
 
         <!-- properties tweaked for individual runs -->
         <sysproperty key="jruby.compile.mode" value="@{compile.mode}"/>
         <sysproperty key="jruby.jit.threshold" value="@{jit.threshold}"/>
         <sysproperty key="jruby.jit.max" value="@{jit.max}"/>
         <sysproperty key="jruby.objectspace.enabled" value="@{objectspace.enabled}"/>
         <sysproperty key="jruby.thread.pool.enabled" value="@{thread.pooling}"/>
         <sysproperty key="jruby.reflection" value="@{reflection}"/>
         <sysproperty key="jruby.launch.inproc" value="@{inproc}"/>
 
         <arg line="@{jruby.args}"/>
         <arg line="${base.dir}/bin/rake"/>
         <arg line="@{rake.targets}"/>
       </java>
     </sequential>
   </macrodef>
 
   <target name="detect-stable-specs-need-update">
     <property file="${spec.dir}/rubyspecs.current.revision"/>
     <condition property="stable-specs-need-update">
       <or>
         <not> <available file="${rubyspec.dir}"/> </not>
         <not> <available file="${mspec.dir}"/> </not>
         <not>
           <equals
             arg1="${rubyspecs.revision}"
             arg2="${rubyspecs.current.revision}"/>
         </not>
         <not>
           <equals
             arg1="${mspec.revision}"
             arg2="${mspec.current.revision}"/>
         </not>
       </or>
     </condition>
   </target>
 
   <target name="fetch-stable-specs" depends="fetch-specs, detect-stable-specs-need-update" if="stable-specs-need-update">
       <echo message="Rolling rubyspec to stable version"/>
       <exec dir="${rubyspec.dir}" executable="git">
           <arg line="co ${rubyspecs.revision}"/>
       </exec>
 
       <echo message="Rolling mspec to stable version"/>
       <exec dir="${mspec.dir}" executable="git">
           <arg line="co ${mspec.revision}"/>
       </exec>
 
   	<!-- Write down the revision of downloaded specs -->
     <propertyfile file="${spec.dir}/rubyspecs.current.revision" comment="Revision of downloaded specs.">
       <entry key="rubyspecs.current.revision" value="${rubyspecs.revision}"/>
       <entry key="mspec.current.revision" value="${mspec.revision}"/>
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
   <target name="spec-short" depends="fetch-stable-specs, run-specs-short"
       description="Runs known good version of rubyspecs, excluding unstable library specs."/>
   <target name="spec-short-1.9" depends="fetch-stable-specs, run-specs-short-1.9"
       description="Runs known good version of rubyspecs, excluding unstable library specs."/>
   <target name="spec-ci" depends="fetch-stable-specs, run-specs-ci"
       description="Runs known good version of rubyspecs interpreted, compiled, and precompile, excluding unstable library specs."/>
   <target name="spec-ci-1.9" depends="fetch-stable-specs, run-specs-ci-1.9"
       description="Runs known good version of rubyspecs interpreted, compiled, and precompile, excluding unstable library specs."/>
   <target name="spec-all" depends="fetch-stable-specs, run-specs-all"
       description="Runs known good version of rubyspecs without exclusions."/>
   <target name="spec-all-interpreted" depends="fetch-stable-specs, run-specs-all-interpreted"
       description="Runs known good version of rubyspecs without exclusions."/>
 
   <!-- latest, unstable specs -->
   <target name="spec-latest" depends="fetch-specs, run-specs"
       description="Runs the very latest rubyspecs."/>
   <target name="spec-latest-all" depends="fetch-specs, run-specs-all"
 	  description="Runs the very latest rubyspecs without exclusions."/>
 
   <target name="fetch-specs">
       <condition property="rubyspec-repo-exists">
           <available file="${rubyspec.dir}/.git"/>
       </condition>
 
       <antcall target="do-fetch-specs"/>
       <antcall target="do-update-specs"/>
   </target>
 
   <target name="do-fetch-specs" unless="rubyspec-repo-exists">
       <!-- Stable specs might exist, so delete them -->
       <antcall target="clear-specs" inheritall="false"/>
 
       <echo message="Cloning rubyspec repo to: ${rubyspec.dir}"/>
       <exec dir="${spec.dir}" executable="git">
           <arg value="clone"/>
           <arg value="git://github.com/rubyspec/rubyspec.git"/>
           <arg value="${rubyspec.dir}"/>
       </exec>
 
       <echo message="Cloning mspec repo to: ${mspec.dir}"/>
       <exec dir="${spec.dir}" executable="git">
                 <arg value="clone"/>
                 <arg value="git://github.com/rubyspec/mspec.git"/>
                 <arg value="${mspec.dir}"/>
       </exec>
   </target>
 
   <target name="do-update-specs" if="rubyspec-repo-exists">
       <echo message="Updating rubyspec repo -- ${rubyspec.dir}"/>
       <exec dir="${rubyspec.dir}" executable="git">
         <arg value="fetch"/>
       </exec>
       <echo message="Updating mspec repo -- ${mspec.dir}"/>
       <exec dir="${mspec.dir}" executable="git">
         <arg value="fetch"/>
       </exec>
   </target>
 
   <target name="clear-specs">
       <delete dir="${rubyspec.dir}"/>
       <delete dir="${mspec.dir}"/>
       <delete file="${build.dir}/rubyspec.tgz"/>
       <delete file="${build.dir}/mspec.tgz"/>
       <delete file="${spec.dir}/rubyspecs.current.revision"/>
   </target>
 
   <target name="run-specs" depends="run-specs-precompiled, run-specs-compiled, run-specs-interpreted, jar">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}" arg2="0"/>
         <equals arg1="${spec.status.COMPILED}"    arg2="0"/>
         <equals arg1="${spec.status.INTERPRETED}" arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-short" depends="run-specs-interpreted-short, jar">
     <condition property="spec.status.combined.OK">
       <and>
-        <equals arg1="${spec.status.PRECOMPILED}"    arg2="0"/>
+        <equals arg1="${spec.status.INTERPRETED}"    arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-short-1.9" depends="run-specs-precompiled-short-1.9, jar">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}"    arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-ci" depends="run-specs-compiled-short, run-specs-interpreted-short, run-specs-precompiled-short, jar">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}" arg2="0"/>
         <equals arg1="${spec.status.COMPILED}"    arg2="0"/>
         <equals arg1="${spec.status.INTERPRETED}" arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-ci-1.9" depends="run-specs-compiled-short-1.9, run-specs-interpreted-short-1.9, run-specs-precompiled-short-1.9, jar">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}" arg2="0"/>
         <equals arg1="${spec.status.COMPILED}"    arg2="0"/>
         <equals arg1="${spec.status.INTERPRETED}" arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-all" depends="jar"><antcall target="run-specs-all-precompiled"/></target>
 
   <target name="run-specs-compiled" depends="jar">
     <_run_specs_internal mode.name="COMPILED" compile.mode="JIT" jit.threshold="0"/>
   </target>
   <target name="run-specs-precompiled" depends="jar">
     <_run_specs_internal mode.name="PRECOMPILED" compile.mode="FORCE" jit.threshold="0"/>
   </target>
   <target name="run-specs-interpreted" depends="jar">
     <_run_specs_internal mode.name="INTERPRETED" compile.mode="OFF"/>
   </target>
 
   <target name="run-specs-compiled-short" depends="jar">
     <_run_specs_internal mode.name="COMPILED" compile.mode="JIT" jit.threshold="0" spec.config="${spec.dir}/no-library.mspec"/>
   </target>
   <target name="run-specs-precompiled-short" depends="jar">
     <_run_specs_internal mode.name="PRECOMPILED" compile.mode="FORCE" jit.threshold="0" spec.config="${spec.dir}/no-library.mspec"/>
   </target>
   <target name="run-specs-interpreted-short" depends="jar">
     <_run_specs_internal mode.name="INTERPRETED" compile.mode="OFF" spec.config="${spec.dir}/no-library.mspec"/>
   </target>
 
   <target name="run-specs-compiled-short-1.9" depends="jar">
     <_run_specs_internal mode.name="COMPILED" compile.mode="JIT" jit.threshold="0" spec.config="${spec.dir}/no-library.mspec" compat="RUBY1_9"/>
   </target>
   <target name="run-specs-precompiled-short-1.9" depends="jar">
     <_run_specs_internal mode.name="PRECOMPILED" compile.mode="FORCE" jit.threshold="0" spec.config="${spec.dir}/no-library.mspec" compat="RUBY1_9"/>
   </target>
   <target name="run-specs-interpreted-short-1.9" depends="jar">
     <_run_specs_internal mode.name="INTERPRETED" compile.mode="OFF" spec.config="${spec.dir}/no-library.mspec" compat="RUBY1_9"/>
   </target>
 
   <target name="run-specs-all-interpreted"><_run_specs_all_internal compile.mode="OFF"/></target>
   <target name="run-specs-all-precompiled"><_run_specs_all_internal compile.mode="FORCE" jit.threshold="0"/></target>
 
 
   <target name="update-excludes">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${spec.dir}">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.launch.inproc" value="false"/>
 
       <arg line="${mspec.dir}/bin/mspec tag"/>
       <arg line="--add fails --fail"/>
       <arg line="-B ${spec.dir}/default.mspec"/>
     </java>
   </target>
 
   <target name="spec-show-excludes" depends="prepare"
     description="Prints out all currently excluded rubyspecs.">
 
     <available property="mspec-available"
       file="${mspec.dir}/bin/mspec"/>
     <fail unless="mspec-available"
       message="No rubyspecs found. Download them via 'ant spec'."/>
 
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${spec.dir}">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.launch.inproc" value="false"/>
       <arg line="${mspec.dir}/bin/mspec"/>
       <arg line="-f s -g fails --dry-run"/>
       <arg line="-B ${spec.dir}/default.mspec"/>
       <arg value="${rubyspec.dir}/1.8"/>
     </java>
   </target>
 
   <macrodef name="run-specs">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <attribute name="mode.name"/>
     <attribute name="spec.config"/>
     <attribute name="compat" default="RUBY1_8"/>
 
     <sequential>
       <echo message="compile=@{compile.mode}, threshold=@{jit.threshold}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection} compat=@{compat}"/>
 
       <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" resultproperty="spec.status.@{mode.name}" dir="${base.dir}">
 	<classpath refid="build.classpath"/>
 	<classpath path="${jruby.classes.dir}"/>
 
 	<jvmarg value="-ea"/>
 
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.launch.inproc" value="false"/>
 
 	<arg line="${mspec.dir}/bin/mspec ci"/>
 	<arg line="-T -J-ea"/>
 	<arg line="-T -J-Djruby.launch.inproc=false"/>
 	<arg line="-T -J-Djruby.compile.mode=@{compile.mode}"/>
 	<arg line="-T -J-Djruby.jit.threshold=@{jit.threshold}"/>
 	<arg line="-T -J-Djruby.jit.max=@{jit.max}"/>
 	<arg line="-T -J-Djruby.objectspace.enabled=@{objectspace.enabled}"/>
 	<arg line="-T -J-Djruby.thread.pool.enabled=@{thread.pooling}"/>
 	<arg line="-T -J-Djruby.reflection=@{reflection}"/>
     <arg line="-T -J-Djruby.compat.version=@{compat}"/>
 	<arg line="-f m"/>
 	<arg line="-B @{spec.config}"/>
       </java>
     </sequential>
   </macrodef>
 
   <macrodef name="_run_specs_internal">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="mode.name"/>
     <attribute name="spec.config" default="${spec.dir}/default.mspec"/>
     <attribute name="compat" default="RUBY1_8"/>
 
     <sequential>
       <echo message="Excludes: ${spec.tags.dir}"/>
       <run-specs mode.name="@{mode.name}"
         compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}"
         spec.config="@{spec.config}" compat="@{compat}"/>
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
              windowtitle="JRuby API" source="${javac.version}" useexternalfile="true"
              maxmemory="128m">
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
     <exclude name="lib/jarjar-1.0rc8.jar"/>
     <exclude name="docs/rdocs.tar.gz"/>
     <exclude name="bench/**"/>
     <include name="share/**"/>
   </patternset>
 
   <patternset id="dist.src.files">
     <patternset refid="dist.files"/>
     <exclude name="share/**"/>
     <include name="bench/**"/>
     <include name="src/**"/>
     <include name="test/**"/>
     <include name="spec/**"/>
     <include name="tool/**"/>
     <include name="build_lib/**"/>
     <include name="Rakefile"/>
     <include name="build.xml"/>
     <include name="build-config.xml"/>
     <include name="nbproject/*"/>
     <include name=".project"/>
     <include name=".classpath"/>
     <include name="default.build.properties"/>
     <include name="jruby.bnd.template"/>
     <exclude name="lib/jruby.jar"/>
   </patternset>
   <target name="dist-bin" depends="jar-dist">
     <mkdir dir="dist"/>
     <copy todir="dist">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <mkdir dir="dist/lib/native"/>
     <unzip src="build_lib/jna.jar" dest="dist/lib/native">
         <patternset>
             <include name="**/libjnidispatch.*"/>
         </patternset>
         <mapper>
             <filtermapper>
                 <replacestring from="com/sun/jna" to="."/>
             </filtermapper>
         </mapper>
     </unzip>
     <unzip src="build_lib/jffi.jar" dest="dist/lib/native">
         <patternset>
             <include name="**/libjffi.*"/>
         </patternset>
         <mapper>
             <filtermapper>
                 <replacestring from="jni/" to="./"/>
             </filtermapper>
         </mapper>
     </unzip>
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
     <jar destfile="${lib.dir}/jruby.jar" update="true">
       <fileset dir="${jruby.classes.dir}">
         <include name="org/jruby/jruby.properties"/>
       </fileset>
     </jar>
     <property name="jar-up-to-date" value="true"/>
     <antcall target="dist-bin">
       <param file="${jruby.classes.dir}/org/jruby/jruby.properties"/>
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
       <param file="${jruby.classes.dir}/org/jruby/jruby.properties"/>
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
     <delete dir="${build.dir}"/>
     <delete dir="${dist.dir}"/>
     <delete quiet="false">
         <fileset dir="${lib.dir}" includes="jruby*.jar"/>
     </delete>
     <delete dir="${api.docs.dir}"/>
     <delete dir="src_gen"/>
   </target>
 
   <target name="clean-all" depends="clean, clear-specs" description="Cleans everything, including downloaded specs">
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
       classpath="${build.lib.dir}/jarjar-1.0rc8.jar"/>
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
       <zipfileset src="${build.lib.dir}/bytelist-1.0.1.jar"/>
       <zipfileset src="${build.lib.dir}/constantine.jar"/>
       <zipfileset src="${build.lib.dir}/jvyamlb-0.2.5.jar"/>
       <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
       <zipfileset src="${build.lib.dir}/jcodings.jar"/>
       <zipfileset src="${build.lib.dir}/joni.jar"/>
       <zipfileset src="${build.lib.dir}/jna-posix.jar"/>
       <zipfileset src="${build.lib.dir}/jna.jar"/>
       <zipfileset src="${build.lib.dir}/jffi.jar"/>
       <zipfileset src="${build.lib.dir}/joda-time-1.5.1.jar"/>
       <zipfileset src="${nailgun.home}/nailgun-0.7.1.jar"/>
       <zipfileset src="${build.lib.dir}/yydebug.jar"/>
       <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       <manifest>
         <attribute name="Built-By" value="${user.name}"/>
         <attribute name="Main-Class" value="org.jruby.Main"/>
       </manifest>
     </jarjar>
   </target>
 
   <target name="fetch-rails">
       <condition property="rails-repo-exists">
           <available file="${rails.dir}/.git"/>
       </condition>
 
       <antcall target="do-fetch-rails"/>
       <antcall target="do-update-rails"/>
   </target>
 
   <target name="do-fetch-rails" unless="rails-repo-exists">
       <!-- Rails repo might already have been pulled, so delete it -->
       <antcall target="clear-rails" inheritall="false"/>
 
       <echo message="Cloning rails repo to: ${rails.dir}"/>
       <exec executable="git">
           <arg value="clone"/>
           <arg value="--depth"/><arg value="1"/>
           <arg value="git://github.com/rails/rails.git"/>
           <arg value="${rails.dir}"/>
       </exec>
   </target>
 
   <target name="do-update-rails" if="rails-repo-exists">
       <echo message="Updating rubyspec repo -- ${rails.dir}"/>
       <exec dir="${rails.dir}" executable="git">
         <arg value="pull"/>
       </exec>
   </target>
 
   <target name="clear-rails">
       <delete dir="${rails.dir}"/>
   </target>
 
   <target name="test-rails-stable" depends="jar,install-build-gems,fetch-rails">
       <!-- Need to disable assertions because of a rogue one in OpenSSL -->
       <run-rake dir="${rails.dir}/activesupport" rake.targets="test" jvm.args="-da"/>
       
       <run-rake dir="${rails.dir}/actionmailer" rake.targets="test"/>
       <run-rake dir="${rails.dir}/activemodel" rake.targets="test"/>
       <run-rake dir="${rails.dir}/railties" rake.targets="test"/>
   </target>
 </project>
diff --git a/src/org/jruby/runtime/marshal/UnmarshalStream.java b/src/org/jruby/runtime/marshal/UnmarshalStream.java
index 66a708e8c2..c022c52d26 100644
--- a/src/org/jruby/runtime/marshal/UnmarshalStream.java
+++ b/src/org/jruby/runtime/marshal/UnmarshalStream.java
@@ -1,374 +1,378 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.marshal;
 
-import java.io.BufferedInputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubyStruct;
 import org.jruby.RubySymbol;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.util.ByteList;
 
 /**
- * Unmarshals objects from strings or streams in Ruby's marsal format.
+ * Unmarshals objects from strings or streams in Ruby's marshal format.
  *
  * @author Anders
  */
-public class UnmarshalStream extends BufferedInputStream {
+public class UnmarshalStream extends InputStream {
     protected final Ruby runtime;
     private final UnmarshalCache cache;
     private final IRubyObject proc;
+    private final InputStream inputStream;
 
     public UnmarshalStream(Ruby runtime, InputStream in, IRubyObject proc) throws IOException {
-        super(in);
         this.runtime = runtime;
         this.cache = new UnmarshalCache(runtime);
         this.proc = proc;
+        this.inputStream = in;
 
         int major = in.read(); // Major
         int minor = in.read(); // Minor
 
         if(major == -1 || minor == -1) {
             throw new EOFException("Unexpected end of stream");
         }
         
         if(major != Constants.MARSHAL_MAJOR || minor > Constants.MARSHAL_MINOR) {
             throw runtime.newTypeError(String.format("incompatible marshal file format (can't be read)\n\tformat version %d.%d required; %d.%d given", Constants.MARSHAL_MAJOR, Constants.MARSHAL_MINOR, major, minor));
         }
     }
 
     public IRubyObject unmarshalObject() throws IOException {
         int type = readUnsignedByte();
         IRubyObject result;
         if (cache.isLinkType(type)) {
             result = cache.readLink(this, type);
         } else {
             result = unmarshalObjectDirectly(type);
         }
         return result;
     }
 
     public void registerLinkTarget(IRubyObject newObject) {
         if (MarshalStream.shouldBeRegistered(newObject)) {
             cache.register(newObject);
         }
     }
 
     public static RubyModule getModuleFromPath(Ruby runtime, String path) {
         RubyModule value = runtime.getClassFromPath(path);
         if (!value.isModule()) throw runtime.newArgumentError(path + " does not refer module");
         return value;
     }
 
     public static RubyClass getClassFromPath(Ruby runtime, String path) {
         RubyModule value = runtime.getClassFromPath(path);
         if (!value.isClass()) throw runtime.newArgumentError(path + " does not refer class");
         return (RubyClass)value;
     }
 
     boolean ivarsWaiting = false;
 
     private IRubyObject unmarshalObjectDirectly(int type) throws IOException {
     	IRubyObject rubyObj = null;
         switch (type) {
             case 'I':
                 ivarsWaiting = true;
                 rubyObj = unmarshalObject();
                 if (ivarsWaiting) {
                     defaultVariablesUnmarshal(rubyObj);
                     ivarsWaiting = false;
                 }
                 break;
             case '0' :
                 rubyObj = runtime.getNil();
                 break;
             case 'T' :
                 rubyObj = runtime.getTrue();
                 break;
             case 'F' :
                 rubyObj = runtime.getFalse();
                 break;
             case '"' :
                 rubyObj = RubyString.unmarshalFrom(this);
                 break;
             case 'i' :
                 rubyObj = RubyFixnum.unmarshalFrom(this);
                 break;
             case 'f' :
             	rubyObj = RubyFloat.unmarshalFrom(this);
             	break;
             case '/' :
                 rubyObj = RubyRegexp.unmarshalFrom(this);
                 break;
             case ':' :
                 rubyObj = RubySymbol.unmarshalFrom(this);
                 break;
             case '[' :
                 rubyObj = RubyArray.unmarshalFrom(this);
                 break;
             case '{' :
                 rubyObj = RubyHash.unmarshalFrom(this, false);
                 break;
             case '}' :
                 // "hashdef" object, a hash with a default
                 rubyObj = RubyHash.unmarshalFrom(this, true);
                 break;
             case 'c' :
                 rubyObj = RubyClass.unmarshalFrom(this);
                 break;
             case 'm' :
                 rubyObj = RubyModule.unmarshalFrom(this);
                 break;
             case 'e':
                 RubySymbol moduleName = (RubySymbol) unmarshalObject();
                 RubyModule tp = null;
                 try {
                     tp = runtime.getClassFromPath(moduleName.asJavaString());
                 } catch (RaiseException e) {
                     if (runtime.fastGetModule("NameError").isInstance(e.getException())) {
                         throw runtime.newArgumentError("undefined class/module " + moduleName.asJavaString());
                     } 
                     throw e;
                 }
 
                 rubyObj = unmarshalObject();
                 
                 tp.extend_object(rubyObj);
                 tp.callMethod(runtime.getCurrentContext(),"extended", rubyObj);
                 break;
             case 'l' :
                 rubyObj = RubyBignum.unmarshalFrom(this);
                 break;
             case 'S' :
                 rubyObj = RubyStruct.unmarshalFrom(this);
                 break;
             case 'o' :
                 rubyObj = defaultObjectUnmarshal();
                 break;
             case 'u' :
                 rubyObj = userUnmarshal();
                 break;
             case 'U' :
                 rubyObj = userNewUnmarshal();
                 break;
             case 'C' :
             	rubyObj = uclassUnmarshall();
             	break;
             default :
                 throw getRuntime().newArgumentError("dump format error(" + (char)type + ")");
         }
         
         if (proc != null && type != ':') {
             // call the proc, but not for symbols
             RuntimeHelpers.invoke(getRuntime().getCurrentContext(), proc, "call", rubyObj);
         }
         return rubyObj;
     }
 
 
     public Ruby getRuntime() {
         return runtime;
     }
 
     public int readUnsignedByte() throws IOException {
         int result = read();
         if (result == -1) {
             throw new EOFException("Unexpected end of stream");
         }
         return result;
     }
 
     public byte readSignedByte() throws IOException {
         int b = readUnsignedByte();
         if (b > 127) {
             return (byte) (b - 256);
         }
 		return (byte) b;
     }
 
     public ByteList unmarshalString() throws IOException {
         int length = unmarshalInt();
         byte[] buffer = new byte[length];
         
         // FIXME: sooper inefficient, but it's working better...
         int b = 0;
         int i = 0;
         while (i < length && (b = read()) != -1) {
             buffer[i++] = (byte)b;
         }
         if (i < length) {
             throw getRuntime().newArgumentError("marshal data too short");
         }
         return new ByteList(buffer,false);
     }
 
     public int unmarshalInt() throws IOException {
         int c = readSignedByte();
         if (c == 0) {
             return 0;
         } else if (5 < c && c < 128) {
             return c - 5;
         } else if (-129 < c && c < -5) {
             return c + 5;
         }
         long result;
         if (c > 0) {
             result = 0;
             for (int i = 0; i < c; i++) {
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         } else {
             c = -c;
             result = -1;
             for (int i = 0; i < c; i++) {
                 result &= ~((long) 0xff << (8 * i));
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         }
         return (int) result;
     }
 
     private IRubyObject defaultObjectUnmarshal() throws IOException {
         RubySymbol className = (RubySymbol) unmarshalObject();
 
         RubyClass type = null;
         try {
             type = getClassFromPath(runtime, className.toString());
         } catch (RaiseException e) {
             if (runtime.fastGetModule("NameError").isInstance(e.getException())) {
                 throw runtime.newArgumentError("undefined class/module " + className.asJavaString());
             } 
                 
             throw e;
         }
 
         assert type != null : "type shouldn't be null.";
 
         IRubyObject result = (IRubyObject)type.unmarshal(this);
 
         return result;
     }
     
     public void defaultVariablesUnmarshal(IRubyObject object) throws IOException {
         int count = unmarshalInt();
         
         List<Variable<IRubyObject>> attrs = new ArrayList<Variable<IRubyObject>>(count);
         
         for (int i = count; --i >= 0; ) {
             String name = unmarshalObject().asJavaString();
             IRubyObject value = unmarshalObject();
             attrs.add(new VariableEntry<IRubyObject>(name, value));
         }
         
         object.syncVariables(attrs);
     }
     
     
     private IRubyObject uclassUnmarshall() throws IOException {
     	RubySymbol className = (RubySymbol)unmarshalObject();
     	
     	RubyClass type = (RubyClass)runtime.getClassFromPath(className.asJavaString());
     	
         // All "C" marshalled objects descend from core classes, which are all RubyObject
     	RubyObject result = (RubyObject)unmarshalObject();
     	
     	result.setMetaClass(type);
     	
     	return result;
     }
 
     private IRubyObject userUnmarshal() throws IOException {
         String className = unmarshalObject().asJavaString();
         ByteList marshaled = unmarshalString();
         RubyModule classInstance = findClass(className);
         if (!classInstance.respondsTo("_load")) {
             throw runtime.newTypeError("class " + classInstance.getName() + " needs to have method `_load'");
         }
         RubyString data = RubyString.newString(getRuntime(), marshaled);
         if (ivarsWaiting) {
             defaultVariablesUnmarshal(data);
             ivarsWaiting = false;
         }
         IRubyObject result = classInstance.callMethod(getRuntime().getCurrentContext(),
             "_load", data);
         registerLinkTarget(result);
         return result;
     }
 
     private IRubyObject userNewUnmarshal() throws IOException {
         String className = unmarshalObject().asJavaString();
         RubyClass classInstance = findClass(className);
         IRubyObject result = classInstance.allocate();
         registerLinkTarget(result);
         IRubyObject marshaled = unmarshalObject();
         result.callMethod(getRuntime().getCurrentContext(),"marshal_load", marshaled);
         return result;
     }
 
     private RubyClass findClass(String className) {
         RubyModule classInstance;
         try {
             classInstance = runtime.getClassFromPath(className);
         } catch (RaiseException e) {
             if (runtime.getModule("NameError").isInstance(e.getException())) {
                 throw runtime.newArgumentError("undefined class/module " + className);
             } 
             throw e;
         }
         if (! (classInstance instanceof RubyClass)) {
             throw runtime.newArgumentError(className + " does not refer class"); // sic
         }
         return (RubyClass) classInstance;
     }
+
+    public int read() throws IOException {
+        return inputStream.read();
+    }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 8f8216c636..82045ecb89 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,1142 +1,1141 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Damian Steer <pldms@mac.com>
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
 package org.jruby.util.io;
 
-import java.io.BufferedInputStream;
-import java.io.BufferedOutputStream;
+import static java.util.logging.Logger.getLogger;
+
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
+import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
-
-import java.nio.channels.Channel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.WritableByteChannel;
-import static java.util.logging.Logger.getLogger;
+
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
 /**
  * <p>This file implements a seekable IO file.</p>
  */
 public class ChannelStream implements Stream, Finalizable {
     private final static boolean DEBUG = false;
     
     /**
      * The size of the read/write buffer allocated for this stream.
      * 
      * This size has been scaled back from its original 16k because although
      * the larger buffer size results in raw File.open times being rather slow
      * (due to the cost of instantiating a relatively large buffer). We should
      * try to find a happy medium, or potentially pool buffers, or perhaps even
      * choose a value based on platform(??), but for now I am reducing it along
      * with changes for the "large read" patch from JRUBY-2657.
      */
     private final static int BUFSIZE = 4 * 1024;
     
     /**
      * The size at which a single read should turn into a chunkier bulk read.
      * Currently, this size is about 4x a normal buffer size.
      * 
      * This size was not really arrived at experimentally, and could potentially
      * be increased. However, it seems like a "good size" and we should
      * probably only adjust it if it turns out we would perform better with a
      * larger buffer for large bulk reads.
      */
     private final static int BULK_READ_SIZE = 16 * 1024;
     private final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
     
     private Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
     
     protected volatile ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
     private volatile boolean closedExplicitly = false;
 
     private volatile boolean eof = false;
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, FileDescriptor fileDescriptor) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         this.buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
         
         // this constructor is used by fdopen, so we don't increment descriptor ref count
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
         this(runtime, descriptor, descriptor.getFileDescriptor());
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, FileDescriptor fileDescriptor) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
     }
 
     public Ruby getRuntime() {
         return runtime;
     }
     
     public void checkReadable() throws IOException {
         if (!modes.isReadable()) throw new IOException("not opened for reading");
     }
 
     public void checkWritable() throws IOException {
         if (!modes.isWritable()) throw new IOException("not opened for writing");
     }
 
     public void checkPermissionsSubsetOf(ModeFlags subsetModes) {
         subsetModes.isSubsetOf(modes);
     }
     
     public ModeFlags getModes() {
     	return modes;
     }
     
     public boolean isSync() {
         return sync;
     }
 
     public void setSync(boolean sync) {
         this.sync = sync;
     }
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     public void waitUntilReady() throws IOException, InterruptedException {
         while (ready() == 0) {
             Thread.sleep(10);
         }
     }
     
     public boolean readDataBuffered() {
         return reading && buffer.hasRemaining();
     }
     
     public boolean writeDataBuffered() {
         return !reading && buffer.position() > 0;
     }
     private final int refillBuffer() throws IOException {
         buffer.clear();
         int n = ((ReadableByteChannel) descriptor.getChannel()).read(buffer);
         buffer.flip();
         return n;
     }
     public synchronized ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         if (separatorString == null) {
             return readall();
         }
 
         final ByteList separator = (separatorString == PARAGRAPH_DELIMETER) ?
             PARAGRAPH_SEPARATOR : separatorString;
 
         descriptor.checkOpen();
         
         if (feof()) {
             return null;
         }
         
         int c = read();
         
         if (c == -1) {
             return null;
         }
         
         // unread back
         buffer.position(buffer.position() - 1);
 
         ByteList buf = new ByteList(40);
         
         byte first = separator.bytes[separator.begin];
 
         LineLoop : while (true) {
             ReadLoop: while (true) {
                 byte[] bytes = buffer.array();
                 int offset = buffer.position();
                 int max = buffer.limit();
                 
                 // iterate over remainder of buffer until we find a match
                 for (int i = offset; i < max; i++) {
                     c = bytes[i];
                     if (c == first) {
                         // terminate and advance buffer when we find our char
                         buf.append(bytes, offset, i - offset);
                         if (i >= max) {
                             buffer.clear();
                         } else {
                             buffer.position(i + 1);
                         }
                         break ReadLoop;
                     }
                 }
                 
                 // no match, append remainder of buffer and continue with next block
                 buf.append(bytes, offset, buffer.remaining());
                 int read = refillBuffer();
                 if (read == -1) break LineLoop;
             }
             
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.realSize; i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.bytes[separator.begin + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.realSize - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.bytes[separator.begin]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
     
     /**
      * An version of read that reads all bytes up to and including a terminator byte.
      * <p>
      * If the terminator byte is found, it will be the last byte in the output buffer.
      * </p>
      *
      * @param dst The output buffer.
      * @param terminator The byte to terminate reading.
      * @return The number of bytes read, or -1 if EOF is reached.
      * 
      * @throws java.io.IOException
      * @throws org.jruby.util.io.BadDescriptorException
      */
     public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
         
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
     
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             invalidateBuffer();
             FileChannel channel = (FileChannel)descriptor.getChannel();
             long left = channel.size() - channel.position();
             if (left <= 0) {
                 eof = true;
                 return null;
             }
             left += ungotc != -1 ? 1 : 0;
             ByteList result = new ByteList((int) left);
             ByteBuffer buf = ByteBuffer.wrap(result.unsafeBytes(), 
                     result.begin(), (int) left);
             if (ungotc != -1) {
                 buf.put((byte) ungotc);
                 ungotc = -1;
             }
             while (buf.hasRemaining()) {
                 int n = ((ReadableByteChannel) descriptor.getChannel()).read(buf);
                 if (n <= 0) {
                     break;
                 }
             }
             eof = true;
             result.length(buf.position());
             return result;
         } else if (descriptor.isNull()) {
             return new ByteList(0);
         } else {
             checkReadable();
 
             ByteList byteList = new ByteList();
             ByteList read = fread(BUFSIZE);
             
             if (read == null) {
                 eof = true;
                 return byteList;
             }
 
             while (read != null) {
                 byteList.append(read);
                 read = fread(BUFSIZE);
             }
 
             return byteList;
         } 
     }
     
     /**
      * <p>Close IO handler resources.</p>
      * @throws IOException 
      * @throws BadDescriptorException 
      * 
      * @see org.jruby.util.IOHandler#close()
      */
     public synchronized void fclose() throws IOException, BadDescriptorException {
         closedExplicitly = true;
         close(false); // not closing from finalize
     }
 
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException
      * @throws BadDescriptorException
      */
     private void close(boolean finalizing) throws IOException, BadDescriptorException {
         try {
             flushWrite();
 
             descriptor.close();
             buffer = EMPTY_BUFFER;
 
             if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno "
                     + descriptor.getFileno() + " closed by stream");
         } finally {
             Ruby localRuntime = getRuntime();
             if (!finalizing && localRuntime != null) localRuntime.removeInternalFinalizer(this);
             
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
         }
     }
 
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException 
      * @throws BadDescriptorException
      */
     private void closeForFinalize() {
         try {
             close(true);
         } catch (BadDescriptorException ex) {
             // silence
         } catch (IOException ex) {
             // silence
         }
     }
 
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#flush()
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eof) {
             return -1;
         }
         return 0;
     }
     
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private void flushWrite() throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return; // Don't bother
         
         int len = buffer.position();
         buffer.flip();
         int n = descriptor.write(buffer);
 
         if(n != len) {
             // TODO: check the return value here
         }
         buffer.clear();
     }
     
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private boolean flushWrite(final boolean block) throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return false; // Don't bother
         int len = buffer.position();
         int nWritten = 0;
         buffer.flip();
 
         // For Sockets, only write as much as will fit.
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(block);
                     }
                     nWritten = descriptor.write(buffer);
                 } finally {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             nWritten = descriptor.write(buffer);
         }
         if (nWritten != len) {
             buffer.compact();
             return false;
         }
         buffer.clear();
         return true;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         if (in == null) {
-            return new BufferedInputStream(Channels.newInputStream((ReadableByteChannel)descriptor.getChannel()));
+            return Channels.newInputStream((ReadableByteChannel)descriptor.getChannel());
         } else {
             return in;
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
-        return new BufferedOutputStream(Channels.newOutputStream((WritableByteChannel)descriptor.getChannel()));
+        return Channels.newOutputStream((WritableByteChannel)descriptor.getChannel());
     }
     
     public void clearerr() {
         eof = false;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
         
         if (eof) {
             return true;
         } else {
             return false;
         }
     }
     
     /**
      * @throws IOException 
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             long pos = fileChannel.position();
             // Adjust for buffered data
             if (reading) {
                 pos -= buffer.remaining();
                 return pos - (pos > 0 && ungotc != -1 ? 1 : 0);
             } else {
                 return pos + buffer.position();
             }
         } else if (descriptor.isNull()) {
             return 0;
         } else {
             throw new PipeException();
         }
     }
     
     /**
      * Implementation of libc "lseek", which seeks on seekable streams, raises
      * EPIPE if the fd is assocated with a pipe, socket, or FIFO, and doesn't
      * do anything for other cases (like stdio).
      * 
      * @throws IOException 
      * @throws InvalidValueException 
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             ungotc = -1;
             int adj = 0;
             if (reading) {
                 // for SEEK_CUR, need to adjust for buffered data
                 adj = buffer.remaining();
                 buffer.clear();
                 buffer.flip();
             } else {
                 flushWrite();
             }
             try {
                 switch (type) {
                 case SEEK_SET:
                     fileChannel.position(offset);
                     break;
                 case SEEK_CUR:
                     fileChannel.position(fileChannel.position() - adj + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             } catch (IOException ioe) {
                 ioe.printStackTrace();
                 throw ioe;
             }
         } else if (descriptor.getChannel() instanceof SelectableChannel) {
             // TODO: It's perhaps just a coincidence that all the channels for
             // which we should raise are instanceof SelectableChannel, since
             // stdio is not...so this bothers me slightly. -CON
             throw new PipeException();
         } else {
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public void sync() throws IOException, BadDescriptorException {
         flushWrite();
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureRead() throws IOException, BadDescriptorException {
         if (reading) return;
         flushWrite();
         buffer.clear();
         buffer.flip();
         reading = true;
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureReadNonBuffered() throws IOException, BadDescriptorException {
         if (reading) {
             if (buffer.hasRemaining()) {
                 Ruby localRuntime = getRuntime();
                 if (localRuntime != null) {
                     throw localRuntime.newIOError("sysread for buffered IO");
                 } else {
                     throw new IOException("sysread for buffered IO");
                 }
             }
         } else {
             // libc flushes writes on any read from the actual file, so we flush here
             flushWrite();
             buffer.clear();
             buffer.flip();
             reading = true;
         }
     }
     
     private void resetForWrite() throws IOException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (buffer.hasRemaining()) { // we have read ahead, and need to back up
                 fileChannel.position(fileChannel.position() - buffer.remaining());
             }
         }
         // FIXME: Clearing read buffer here...is this appropriate?
         buffer.clear();
         reading = false;
     }
     
     /**
      * Ensure buffer is ready for writing.
      * @throws IOException
      */
     private void ensureWrite() throws IOException {
         if (!reading) return;
         resetForWrite();
     }
 
     public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureReadNonBuffered();
         
         ByteList byteList = new ByteList(number);
         
         // TODO this should entry into error handling somewhere
         int bytesRead = descriptor.read(number, byteList);
         
         if (bytesRead == -1) {
             eof = true;
         }
         
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         
         ByteList result = new ByteList(0);
         
         int len = -1;
         if (buffer.hasRemaining()) { // already have some bytes buffered
             len = (number <= buffer.remaining()) ? number : buffer.remaining();
             result.append(buffer, len);
         }
         boolean done = false;
         //
         // Avoid double-copying for reads that are larger than the buffer size
         //
         while ((number - result.length()) >= BUFSIZE) {
             //
             // limit each iteration to a max of BULK_READ_SIZE to avoid over-size allocations
             //
             int bytesToRead = Math.min(BULK_READ_SIZE, number - result.length());
             int n = descriptor.read(bytesToRead, result);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             }
         }
         
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && result.length() != number) {
             int read = refillBuffer();
             
             if (read == -1) {
                 eof = true;
                 break;
             } else if (read == 0) {
                 break;
             }
             
             // append what we read into our buffer and allow the loop to continue
             int desired = number - result.length();
             len = (desired < read) ? desired : read;
             result.append(buffer, len);
         }
         
         if (result.length() == 0 && number != 0) {
             if (eof) {
                 throw new EOFException();
             }
         }
         return result;
     }
     
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
         
         if (!buffer.hasRemaining()) {
             int len = refillBuffer();
             if (len == -1) {
                 eof = true;
                 return -1;
             } else if (len == 0) {
                 return -1;
             }
         }
         return buffer.get() & 0xFF;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
         
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
         
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
             
 
             int n = descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
             if(n != buf.length()) {
                 // TODO: check the return value here
             }
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
             
             buffer.put(buf.unsafeBytes(), buf.begin(), buf.length());
         }
         
         if (isSync()) sync();
         
         return buf.realSize;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
         
         buffer.put((byte) c);
             
         if (isSync()) sync();
             
         return 1;
     }
     
     public synchronized void ftruncate(long newLength) throws IOException,
             BadDescriptorException, InvalidValueException {
         Channel ch = descriptor.getChannel();
         if (!(ch instanceof FileChannel)) {
             throw new InvalidValueException();
         }
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)ch;
         if (newLength > fileChannel.size()) {
             // truncate can't lengthen files, so we save position, seek/write, and go back
             long position = fileChannel.position();
             int difference = (int)(newLength - fileChannel.size());
             
             fileChannel.position(fileChannel.size());
             // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
             fileChannel.write(ByteBuffer.allocate(difference));
             fileChannel.position(position);
         } else {
             fileChannel.truncate(newLength);
         }        
     }
     
     /**
      * Invalidate buffer before a position change has occurred (e.g. seek),
      * flushing writes if required, and correcting file position if reading
      * @throws IOException 
      */
     private void invalidateBuffer() throws IOException, BadDescriptorException {
         if (!reading) flushWrite();
         int posOverrun = buffer.remaining(); // how far ahead we are when reading
         buffer.clear();
         if (reading) {
             buffer.flip();
             // if the read buffer is ahead, back up
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (posOverrun != 0) fileChannel.position(fileChannel.position() - posOverrun);
         }
     }
 
     /**
      * Ensure close (especially flush) when we're finished with
      */
     @Override
     public synchronized void finalize() {
         if (closedExplicitly) return;
 
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isSeekable() && descriptor.isOpen()) {
             closeForFinalize(); // close without removing from finalizers
         }
     }
 
     public int ready() throws IOException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             int ready_stat = 0;
             java.nio.channels.Selector sel = java.nio.channels.Selector.open();
             SelectableChannel selchan = (SelectableChannel)descriptor.getChannel();
             synchronized (selchan.blockingLock()) {
                 boolean is_block = selchan.isBlocking();
                 try {
                     selchan.configureBlocking(false);
                     selchan.register(sel, java.nio.channels.SelectionKey.OP_READ);
                     ready_stat = sel.selectNow();
                     sel.close();
                 } catch (Throwable ex) {
                     ex.printStackTrace();
                 } finally {
                     if (sel != null) {
                         try {
                             sel.close();
                         } catch (Exception e) {
                         }
                     }
                     selchan.configureBlocking(is_block);
                 }
             }
             return ready_stat;
         } else {
             return newInputStream().available();
         }
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         bufferedWrite(c);
     }
 
     public int ungetc(int c) {
         if (c == -1) {
             return -1;
         }
         
         // putting a bit back, so we're not at EOF anymore
         eof = false;
 
         // save the ungot
         ungotc = c;
         
         return c;
     }
 
     public synchronized int fgetc() throws IOException, BadDescriptorException {
         if (eof) {
             return -1;
         }
         
         checkReadable();
 
         int c = read();
 
         if (c == -1) {
             eof = true;
             return c;
         }
         
         return c & 0xff;
     }
 
     public synchronized int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return bufferedWrite(string);
     }
     public synchronized int writenonblock(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
         
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
         
         if (buffer.position() != 0 && !flushWrite(false)) return 0;
         
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(false);
                     }
                     return descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
                 } finally {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             return descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
         }
     }
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             if (ungotc >= 0) {
                 ByteList buf2 = bufferedRead(number - 1);
                 buf2.prepend((byte)ungotc);
                 ungotc = -1;
                 return buf2;
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readnonblock(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     selectableChannel.configureBlocking(false);
                     return readpartial(number);
                 } finally {
                     selectableChannel.configureBlocking(oldBlocking);
                 }
             }
         } else if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         } else {
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
         if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         }
         // make sure that the ungotc is not forgotten
         if (ungotc >= 0) {
             number--;
             if (number == 0 || !buffer.hasRemaining()) {
                 ByteList result = new ByteList(new byte[] {(byte)ungotc}, false);
                 ungotc = -1;
                 return result;
             }
         }
 
         if (buffer.hasRemaining()) {
             // already have some bytes buffered, just return those
 
             ByteList result = bufferedRead(Math.min(buffer.remaining(), number));
 
             if (ungotc >= 0) {
                 result.prepend((byte)ungotc);
                 ungotc = -1;
             }
             return result;
         } else {
             // otherwise, we try an unbuffered read to get whatever's available
             return read(number);
         }        
     }
 
     public synchronized int read() throws IOException, BadDescriptorException {
         try {
             descriptor.checkOpen();
             
             if (ungotc >= 0) {
                 int c = ungotc;
                 ungotc = -1;
                 return c;
             }
 
             return bufferedRead();
         } catch (EOFException e) {
             eof = true;
             return -1;
         }
     }
     
     public ChannelDescriptor getDescriptor() {
         return descriptor;
     }
     
     public void setBlocking(boolean block) throws IOException {
         if (!(descriptor.getChannel() instanceof SelectableChannel)) {
             return;
         }
         synchronized (((SelectableChannel) descriptor.getChannel()).blockingLock()) {
             blocking = block;
             try {
                 ((SelectableChannel) descriptor.getChannel()).configureBlocking(block);
             } catch (IllegalBlockingModeException e) {
                 // ignore this; select() will set the correct mode when it is finished
             }
         }
     }
 
     public boolean isBlocking() {
         return blocking;
     }
 
     public synchronized void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         // flush first
         flushWrite();
 
         // reset buffer
         buffer.clear();
         if (reading) {
             buffer.flip();
         }
 
         this.modes = modes;
 
         if (descriptor.isOpen()) {
             descriptor.close();
         }
         
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             descriptor = new ChannelDescriptor(new NullChannel(), descriptor.getFileno(), modes, new FileDescriptor());
         } else {
             String cwd = runtime.getCurrentDirectory();
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
 
             if (modes.isCreate()) {
                 if (theFile.exists() && modes.isExclusive()) {
                     throw runtime.newErrnoEEXISTError("File exists - " + path);
                 }
                 theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw runtime.newErrnoENOENTError("file not found - " + path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
             if (modes.isTruncate()) file.setLength(0L);
             
             descriptor = new ChannelDescriptor(file.getChannel(), descriptor.getFileno(), modes, file.getFD());
         
             if (modes.isAppendable()) lseek(0, SEEK_END);
         }
     }
     
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         String cwd = runtime.getCurrentDirectory();
         
         ChannelDescriptor descriptor = ChannelDescriptor.open(cwd, path, modes);
         
         Stream stream = fdopen(runtime, descriptor, modes);
         
         if (modes.isAppendable()) stream.lseek(0, Stream.SEEK_END);
         
         return stream;
     }
     
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         Stream handler = new ChannelStream(runtime, descriptor, modes, descriptor.getFileDescriptor());
         
         return handler;
     }
 }
diff --git a/test/jruby_index b/test/jruby_index
index 512fc5c70d..b8a2ff4a2a 100644
--- a/test/jruby_index
+++ b/test/jruby_index
@@ -1,100 +1,101 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 test_globals
 test_argf
 test_array
 test_array_subclass_behavior
 test_autoload
 test_backquote
 test_backtraces
 test_big_decimal
 test_bignum
 test_binding_eval_yield
 test_block
 test_block_arg_processing
 test_cache_map_leak
 test_caller
 test_case
 test_class
 test_command_line_switches
 test_comparable
 test_core_arities
 test_crazy_blocks
 test_date_time
 test_defined
 test_default_constants
 test_dir
 #test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_etc
 test_eval
 test_eval_with_binding
 test_file
 test_flip
 test_frame_self
 test_hash
 test_higher_javasupport
 test_iconv
 test_included_in_object_space
 test_io
 test_load
 test_math
 test_method_cache
 test_method_override_and_caching
 test_nkf
 test_java_accessible_object
 test_java_extension
 test_java_wrapper_deadlock
 test_jruby_internals
 compiler/test_jrubyc
 test_launching_by_shell_script
 #test_local_jump_error
 test_marshal_with_instance_variables
 test_marshal_gemspec
 test_method_missing
 test_methods
 test_no_stack_trace_stomp
 test_pack
 test_primitive_to_java
 test_process
 test_proc_visibility
 test_parsing
 test_random
 test_rbconfig
 test_require_once
 test_socket
 test_string_java_bytes
 test_string_printf
 test_string_sub
 test_string_to_number
 test_symbol
 test_tb_yaml
 test_timeout
 test_thread
 test_threaded_nonlocal_return
 test_time_nil_ops
+test_unmarshal
 test_variables
 test_vietnamese_charset
 #test_trace_func
 test_zlib
 test_yaml
 test_system_error
 
 # these tests are last because they pull in libraries that can affect others
 test_base64_strangeness
 test_loading_builtin_libraries
 test_rewriter
 test_load_compiled_ruby_class_from_classpath
 test_null_channel
 test_irubyobject_java_passing
 test_jruby_object_input_stream
 test_jar_on_load_path
 test_jruby_ext
 test_jruby_core_ext
 test_thread_context_frame_dereferences_unreachable_variables
 test_context_classloader
 test_rexml_document
 test_load_compiled_ruby
 test_openssl_stub
-test_missing_jruby_home
\ No newline at end of file
+test_missing_jruby_home
diff --git a/test/test_unmarshal.rb b/test/test_unmarshal.rb
new file mode 100644
index 0000000000..d04fd9c7c1
--- /dev/null
+++ b/test/test_unmarshal.rb
@@ -0,0 +1,26 @@
+require 'test/unit'
+require 'stringio'
+
+# Test for issue JIRA-2506
+# Fails with an EOF error in JRuby 1.1.1, works in MRI 1.8.6
+# Author: steen.lehmann@gmail.com
+
+class TestUnmarshal < Test::Unit::TestCase
+
+  def testUnmarshal
+    dump = ''
+    dump << Marshal.dump("hey")
+    dump << Marshal.dump("there")
+
+    result = "none"
+    StringIO.open(dump) do |f|
+      result = Marshal.load(f)
+      assert_equal "hey", result, "first string unmarshalled"      
+      result = Marshal.load(f)
+    end
+    assert_equal "there", result, "second string unmarshalled"
+  rescue EOFError
+    flunk "Unmarshalling failed with EOF error at " + result + " string."
+  end
+
+end
