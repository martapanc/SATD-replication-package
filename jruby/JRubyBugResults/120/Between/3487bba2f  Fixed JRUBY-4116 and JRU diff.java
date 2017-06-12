diff --git a/build.xml b/build.xml
index e65e3be536..b68b11b416 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1778 +1,1779 @@
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
 
   <!-- Gem file names -->
   <property name="rspec.gem" value="rspec-1.2.9.gem"/>
   <property name="rake.gem" value="rake-0.8.7.gem"/>
   <property name="ruby-debug.gem" value="ruby-debug-0.10.3.gem"/>
   <property name="ruby-debug-base.gem" value="ruby-debug-base-0.10.3.1-java.gem"/>
 
   <property name="shared.lib.dir" value="lib/ruby/site_ruby/shared"/>
 
   <path id="build.classpath">
     <fileset dir="${build.lib.dir}" includes="*.jar"/>
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
     <exclude unless="sun-misc-signal" name="**/SunSignalFacade.java"/>
   </patternset>
 
   <patternset id="ruby.src.pattern">
     <include name="**/*.rb"/>
   </patternset>
 
   <patternset id="other.src.pattern">
     <include name="**/*.properties"/>
   </patternset>
 
   <import file="netbeans-ant.xml" optional="true"/>
   <import file="ivy/build.xml" />
 
   <!-- Initializes the build -->
   <target name="init">
     <tstamp><format property="build.date" pattern="yyyy-MM-dd"/></tstamp>
     <property environment="env"/>
     <property name="version.ruby" value="${version.ruby.major}.${version.ruby.minor}"/>
     <!-- if ruby.home is not set, use env var -->
     <condition property="ruby.home" value="${env.RUBY_HOME}">
       <not><isset property="ruby.home"/></not>
     </condition>
     <condition property="run.jvm.model" value="">
         <os family="windows"/>
     </condition>
     <condition property="run.jvm.model" value="-d64">
       <or>
         <os arch="amd64"/>
         <os arch="x86_64"/>
         <os arch="sparcv9"/>
         <os arch="s390x"/>
       </or>
     </condition>
     <condition property="run.jvm.model" value="-d32">
       <or>
         <os arch="i386"/>
         <os arch="x86"/>
         <os arch="powerpc"/>
         <os arch="ppc"/>
         <os arch="sparc"/>
       </or>
     </condition>
     <condition property="run.jvm.model" value="">
         <not><isset property="run.jvm.model"/></not>
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
         <fileset dir="${lib.dir}/ruby/site_ruby/shared/builtin">
             <include name="**/*.rb"/>
             <include name="**/*.jar"/>
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
       <jvmarg value="${run.jvm.model}"/>
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
         <jvmarg value="${run.jvm.model}"/>
         <!-- uncomment this line when building on a JVM with invokedynamic
         <jvmarg line="-XX:+InvokeDynamic"/>
         -->
         <arg value="org.jruby.util.unsafe"/>
         <arg value="${jruby.classes.dir}/org/jruby/util/unsafe"/>
     </java>
   </target>
 
   <target name="_update_scm_revision_">
       <condition property="ruby.available">
           <available file="${ruby.executable}"/>
       </condition>
       <antcall target="_update_scm_revision_with_ruby_"/>
       <antcall target="_update_scm_revision_with_jruby_"/>
   </target>
 
   <target name="_update_scm_revision_with_ruby_" if="ruby.available">
       <echo message="Using '${ruby.executable}' to calculate revision"/>
       <exec failonerror="false" failifexecutionfails="false" errorproperty="ruby.command.failed" dir="${base.dir}" executable="${ruby.executable}">
           <arg value="${base.dir}/tool/snapshot.rb"/>
           <arg value="${jruby.classes.dir}/org/jruby/jruby.properties"/>
       </exec>
       <condition property="ruby.failed">
           <istrue value="${ruby.command.failed}"/>
       </condition>
   </target>
 
   <target name="_update_scm_revision_with_jruby_" unless="ruby.available">
       <echo message="Using JRuby to calculate revision..."/>
       <echo message="Adjust ruby.executable value in build.properties to speed things up!"/>
       <property name="jruby.home" value="${basedir}"/>
       <java classname="org.jruby.Main" resultproperty="snapshot.result" errorproperty="snapshot.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <jvmarg value="${run.jvm.model}"/>
         <arg value="${base.dir}/tool/snapshot.rb"/>
         <arg value="${jruby.classes.dir}/org/jruby/jruby.properties"/>
       </java>
   </target>
 
   <target name="jar-jruby" depends="generate-method-classes, generate-unsafe" unless="jar-up-to-date">
       <antcall target="_update_scm_revision_"/>
 
       <jar destfile="${lib.dir}/jruby.jar" compress="true" index="true">
         <fileset dir="${jruby.classes.dir}"/>
         <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.6.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <zipfileset src="${build.lib.dir}/nailgun-0.7.1.jar"/>
         <zipfileset src="${build.lib.dir}/emma.jar"/>
         <metainf dir="${base.dir}/spi">
           <include name="services/**"/>
         </metainf>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jar>
   </target>
 
   <target name="jar-jruby-light" depends="generate-method-classes, generate-unsafe" unless="jar-up-to-date">
       <antcall target="_update_scm_revision_"/>
 
       <jar destfile="${lib.dir}/jruby-light.jar" compress="true" index="true">
         <fileset dir="${jruby.classes.dir}">
             <exclude name="**/*$Populator.class"/>
             <exclude name="**/*INVOKER*.class"/>
             <exclude name="**/AnnotationBinder*.class"/>
             <exclude name="**/InvokerGenerator.class"/>
             <exclude name="org/jruby/ext/ffi/**/*"/>
             <exclude name="org/jruby/embed/**/*"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.6.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jar>
   </target>
 
   <target name="jar-jruby-dist" depends="generate-method-classes, generate-unsafe" unless="jar-up-to-date">
       <antcall target="_update_scm_revision_"/>
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <jarjar destfile="${lib.dir}/jruby.jar" compress="true">
         <fileset dir="${jruby.classes.dir}"/>
         <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.6.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <zipfileset src="${build.lib.dir}/nailgun-0.7.1.jar"/>
         <metainf dir="${base.dir}/spi">
           <include name="services/**"/>
         </metainf>
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
     <bndwrap definitions="${build.dir}" output="${dest.lib.dir}">
       <fileset file="${jar.wrap}" />
     </bndwrap>
     <move file="${jar.wrap}$" tofile="${jar.wrap}"
       overwrite="true" />
   </target>
 
     <target name="jar-complete" depends="generate-method-classes, generate-unsafe" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
       <property name="mainclass" value="org.jruby.Main"/>
       <property name="filename" value="jruby-complete.jar"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <property name="jar-complete-home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
       <mkdir dir="${jar-complete-home}"/>
       <copy todir="${jar-complete-home}">
         <fileset dir="${basedir}">
           <patternset refid="dist.bindir.files"/>
           <patternset refid="dist.lib.files"/>
         </fileset>
       </copy>
 
       <java classname="${mainclass}" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
         <classpath>
           <pathelement location="${jruby.classes.dir}"/>
           <pathelement location="${build.dir}/jar-complete"/>
           <path refid="build.classpath"/>
         </classpath>
         <sysproperty key="jruby.home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
         <env key="RUBYOPT" value=""/>
         <jvmarg value="${run.jvm.model}"/>
         <arg value="--command"/>
         <arg value="maybe_install_gems"/>
         <arg value="${build.lib.dir}/${rspec.gem}"/>
         <arg value="${build.lib.dir}/${rake.gem}"/>
         <arg value="${build.lib.dir}/${ruby-debug-base.gem}"/>
         <arg value="${build.lib.dir}/${ruby-debug.gem}"/>
         <arg value="--no-ri"/>
         <arg value="--no-rdoc"/>
         <arg value="--ignore-dependencies"/>
         <arg value="--env-shebang"/>
       </java>
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <jarjar destfile="${dest.lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}"/>
         <fileset dir="${build.dir}/jar-complete">
           <exclude name="META-INF/jruby.home/lib/ruby/1.9/**"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-1.6.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <zipfileset src="${build.lib.dir}/nailgun-0.7.1.jar"/>
         <zipfileset src="${shared.lib.dir}/yecht.jar"/>
         <metainf dir="${base.dir}/spi">
           <include name="services/**"/>
         </metainf>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="${mainclass}"/>
         </manifest>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       </jarjar>
       <antcall target="_osgify-jar_">
         <param name="bndfile" value="jruby-complete.bnd" />
         <param name="jar.wrap" value="${dest.lib.dir}/${filename}" />
       </antcall>
     </target>
 
     <target name="dist-jar-complete" depends="jar-complete">
       <mkdir dir="${dist.dir}"/>
       <move file="${dest.lib.dir}/jruby-complete.jar" tofile="${dist.dir}/jruby-complete-${version.jruby}.jar"/>
       <checksum file="${dist.dir}/jruby-complete-${version.jruby}.jar" algorithm="md5"/>
       <checksum file="${dist.dir}/jruby-complete-${version.jruby}.jar" algorithm="sha1"/>
     </target>
 
     <target name="jar-complete-1.9" depends="generate-method-classes, generate-unsafe" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
       <property name="mainclass" value="org.jruby.Main"/>
       <property name="filename" value="jruby-complete.jar"/>
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <property name="jar-complete-home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
       <mkdir dir="${jar-complete-home}"/>
       <copy todir="${jar-complete-home}">
         <fileset dir="${basedir}">
           <patternset refid="dist.bindir.files"/>
           <patternset refid="dist.lib.files"/>
         </fileset>
       </copy>
 
       <java classname="${mainclass}" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
         <classpath>
           <pathelement location="${jruby.classes.dir}"/>
           <pathelement location="${build.dir}/jar-complete"/>
           <path refid="build.classpath"/>
         </classpath>
         <sysproperty key="jruby.home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
         <env key="RUBYOPT" value=""/>
         <jvmarg value="${run.jvm.model}"/>
         <arg value="--command"/>
         <arg value="maybe_install_gems"/>
         <arg value="${build.lib.dir}/${rspec.gem}"/>
         <arg value="${build.lib.dir}/${rake.gem}"/>
         <arg value="${build.lib.dir}/${ruby-debug-base.gem}"/>
         <arg value="${build.lib.dir}/${ruby-debug.gem}"/>
         <arg value="--no-ri"/>
         <arg value="--no-rdoc"/>
         <arg value="--ignore-dependencies"/>
         <arg value="--env-shebang"/>
       </java>
 
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <jarjar destfile="${dest.lib.dir}/${filename}">
         <fileset dir="${jruby.classes.dir}"/>
         <fileset dir="${build.dir}/jar-complete">
           <exclude name="META-INF/jruby.home/lib/ruby/1.8/**"/>
         </fileset>
         <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>	
         <zipfileset src="${build.lib.dir}/joda-time-1.6.jar"/>
         <zipfileset src="${build.lib.dir}/dynalang-0.3.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <zipfileset src="${build.lib.dir}/nailgun-0.7.1.jar"/>
         <zipfileset src="${shared.lib.dir}/yecht.jar"/>
         <metainf dir="${base.dir}/spi">
           <include name="services/**"/>
         </metainf>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="${mainclass}"/>
         </manifest>
         <rule pattern="org.objectweb.asm.**" result="jruby.objectweb.asm.@1"/>
       </jarjar>
       <antcall target="_osgify-jar_">
         <param name="bndfile" value="jruby-complete.bnd" />
         <param name="jar.wrap" value="${dest.lib.dir}/${filename}" />
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
   <target name="jar-light" depends="init" description="Create the jruby-light.jar file">
     <antcall target="jar-jruby-light" inheritall="true"/>
   </target>
   <target name="jar-dist" depends="init" description="Create the jruby.jar file for distribution. This version uses JarJar Links to rewrite some packages.">
     <antcall target="jar-jruby-dist" inheritall="true"/>
   </target>
 
   <target name="compile-test" depends="jar" description="Compile the unit tests">
     <javac destdir="${test.classes.dir}" deprecation="true" encoding="UTF-8" debug="true"
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
     <property name="install.ri" value="--no-ri"/>
     <property name="install.rdoc" value="--no-rdoc"/>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${jruby.home}"/>
       <arg value="--command"/>
       <arg value="maybe_install_gems"/>
       <arg value="${build.lib.dir}/${rspec.gem}"/>
       <arg value="${build.lib.dir}/${rake.gem}"/>
       <arg value="${build.lib.dir}/${ruby-debug-base.gem}"/>
       <arg value="${build.lib.dir}/${ruby-debug.gem}"/>
       <arg value="${install.ri}"/>
       <arg value="${install.rdoc}"/>
       <arg value="--ignore-dependencies"/>
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
       <arg value="--no-ri"/>
       <arg value="--no-rdoc"/>
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
       <jvmarg value="${run.jvm.model}"/>
 
       <arg value="-I"/>
       <arg value="bin/"/>
       <arg value="-S"/>
       <arg value="jrubyc"/>
       <arg line="."/>
     </java>
   </target>
 
   <target name="emma" description="Turns on EMMA instrumentation/reporting; use with 'jar' to create an instrumented jar." >
     <available property="emma.present"
                classname="com.vladium.app.IAppVersion"
                classpathref="emma.classpath"/>
     <taskdef resource="emma_ant.properties" classpathref="emma.classpath" />
 
     <property name="emma.enabled" value="true" />
 
     <path id="classes_to_instrument" >
       <pathelement location="${jruby.classes.dir}" />
     </path>
   </target>
 
   <target name="instrument" depends="emma">
     <emma enabled="${emma.enabled}" >
       <instr instrpathref="classes_to_instrument"
              mode="overwrite"
              metadatafile="${test.results.dir}/metadata.emma"
              merge="false">
         <filter excludes="*INVOKER*"/>
       </instr>
     </emma>
     <antcall target="jar"/>
   </target>
 
   <target name="coverage-report" description="Generate a coverage report based on aggregated runs." depends="emma">
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
     run-junit-compiled-short,
     test-rake-targets"
     description="Runs unit tests.">
   </target>
 
   <target name="test-extended" depends="
     copy-test-files,
     run-junit-compiled,
     test-rake-targets"
     description="Runs unit tests.">
   </target>
 
   <target name="test-compiled" depends="copy-test-files,run-junit-compiled,run-junit-precompiled"/>
   <target name="test-compiled-1.9" depends="copy-test-files,run-junit-compiled-1.9,run-junit-precompiled-1.9"/>
   <target name="test-interpreted" depends="copy-test-files,run-junit-interpreted,test-interpreted-1.9"/>
   <target name="test-interpreted-1.9" depends="copy-test-files,run-junit-interpreted-1.9"/>
   <target name="test-reflected" depends="copy-test-files,run-junit-reflected-compiled,run-junit-reflected-precompiled,run-junit-reflected-interpreted"/>
   <target name="test-threadpool" depends="copy-test-files,run-junit-compiled-threadpool,run-junit-precompiled-threadpool,run-junit-interpreted-threadpool"/>
 
   <target name="test-1.9" depends="test-interpreted-1.9, test-compiled-1.9"
     description="Runs unit tests compatible with ruby 1.9"/>
 
   <target name="test-all" depends="
       copy-test-files,
       test-security-manager,
       run-junit-interpreted,
       run-junit-compiled,
       run-junit-precompiled,
       run-junit-interpreted-1.9,
       run-junit-reflected-precompiled,
       run-junit-interpreted-threadpool,
       compile-stdlib"
 	  description="Runs unit tests in all modes."/>
 
   <!-- All junit permutations for 1.8 and 1.9 support -->
   <target name="run-junit-interpreted"><run-junit-1.8/></target>
   <target name="run-junit-interpreted-1.9"><run-junit-1.9/></target>
   <target name="run-junit-reflected-interpreted"><run-junit-1.8 reflection="true"/></target>
   <target name="run-junit-reflected-interpreted-1.9"><run-junit-1.9 reflection="true"/></target>
   <target name="run-junit-compiled"><run-junit-1.8 compile.mode="JIT" jit.threshold="0"/></target>
   <target name="run-junit-compiled-short"><run-junit-1.8-short compile.mode="JIT" jit.threshold="0"/></target>
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
   <target name="run-junit-embed" depends="copy-test-files" description="Runs unit embedding API tests."><run-junit-embed-all/></target>
 
   <path id="test.class.path">
     <fileset dir="${build.lib.dir}" includes="*.jar">
       <exclude name="jcodings.jar"/>
       <exclude name="joni.jar"/>
     </fileset>
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
     <attribute name="jit.maxsize" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <element name="junit-tests"/>
     <sequential>
       <echo message="compile=@{compile.mode}, jit.threshold=@{jit.threshold}, jit.maxsize=@{jit.maxsize}, jit.max=@{jit.max}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection} version=@{jruby.version}"/>
       <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${build.lib.dir}/junit.jar"/>
 
       <junit jvm="${jruby.test.jvm}" fork="yes" forkMode="once" haltonfailure="true" dir="${basedir}" maxmemory="${jruby.test.memory}" showoutput="true" timeout="1800000">
 	<classpath refid="test.class.path"/>
 
 	<sysproperty key="java.awt.headless" value="true"/>
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.lib" value="${lib.dir}"/>
 	<sysproperty key="jruby.compile.mode" value="@{compile.mode}"/>
 	<sysproperty key="jruby.jit.threshold" value="@{jit.threshold}"/>
 	<sysproperty key="jruby.jit.maxsize" value="@{jit.maxsize}"/>
 	<sysproperty key="jruby.jit.max" value="@{jit.max}"/>
 	<sysproperty key="jruby.compat.version" value="@{jruby.version}"/>
 	<sysproperty key="jruby.objectspace.enabled" value="@{objectspace.enabled}"/>
 	<sysproperty key="jruby.thread.pool.enabled" value="@{thread.pooling}"/>
 	<sysproperty key="jruby.reflection" value="@{reflection}"/>
 	<sysproperty key="jruby.jit.logging.verbose" value="true"/>
 	<sysproperty key="jruby.compile.lazyHandles" value="true"/>
 
         <!-- emma coverage tool properties -->
 	<sysproperty key="emma.coverage.out.file" value="${test.results.dir}/coverage.emma" />
 	<sysproperty key="emma.coverage.out.merge" value="true" />
         <sysproperty key="emma.verbosity.level" value="silent" />
 
         <!-- force encoding to UTF-8 because of stupid Macroman on OS X -->
         <sysproperty key="file.encoding" value="UTF-8"/>
 
 	<jvmarg value="-ea"/>
 
         <jvmarg value="${run.jvm.model}"/>
 
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
   <macrodef name="run-junit-1.8-short">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.maxsize" default="1000000000"/>
     <attribute name="reflection" default="false"/>
     <attribute name="thread.pooling" default="false"/>
 
     <sequential>
       <run-junit compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}" 
                 reflection="@{reflection}" thread.pooling="@{thread.pooling}"
                 jit.maxsize="@{jit.maxsize}">
 	<junit-tests>
 	  <test name="${test}" if="test"/>
 	  <test name="org.jruby.test.ScriptTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.JRubyTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MRITestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MainTestSuite" todir="${test.results.dir}" unless="test"/>
 	</junit-tests>
       </run-junit>
     </sequential>
   </macrodef>
 
   <!-- runs junit tests for 1.8 functionality -->
   <macrodef name="run-junit-1.8">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.maxsize" default="1000000000"/>
     <attribute name="reflection" default="false"/>
     <attribute name="thread.pooling" default="false"/>
 
     <sequential>
       <run-junit compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}"
                 reflection="@{reflection}" thread.pooling="@{thread.pooling}"
                 jit.maxsize="@{jit.maxsize}">
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
     <attribute name="jit.maxsize" default="1000000000"/>
     <attribute name="reflection" default="false"/>
     <attribute name="thread.pooling" default="false"/>
 
     <sequential>
       <run-junit objectspace.enabled="false" jruby.version="ruby1_9"
                 compile.mode="@{compile.mode}" jit.threshold="@{jit.threshold}"
                 reflection="@{reflection}" thread.pooling="@{thread.pooling}"
                 jit.maxsize="@{jit.maxsize}">
 	<junit-tests>
 	  <test name="${test}" if="test"/>
 	  <test name="org.jruby.test.Ruby1_9TestSuite" todir="${test.results.dir}"  unless="test"/>
 	</junit-tests>
       </run-junit>
     </sequential>
   </macrodef>
 
   <macrodef name="run-junit-embed-all">
     <sequential>
       <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${build.lib.dir}/junit-4.7.jar"/>
       <junit jvm="${jruby.test.jvm}" fork="yes" forkMode="once" haltonfailure="true" dir="${basedir}" maxmemory="${jruby.test.memory}" showoutput="true" timeout="1800000">
         <classpath refid="test.class.path"/>
         <jvmarg value="-Dfile.encoding=UTF-8"/>
         <formatter type="xml"/>
         <formatter type="brief" usefile="false" />
         <batchtest fork="yes" todir="${test.results.dir}">
           <fileset dir="${test.dir}">
             <include name="org/jruby/embed/**/*Test*.java"/>
           </fileset>
         </batchtest>
       </junit>
     </sequential>
   </macrodef>
 
   <target name="test-security-manager" depends="jar-jruby">
     <java classname="org.jruby.Main" fork="true" failonerror="false">
       <classpath refid="test.class.path"/>
 
       <sysproperty key="java.security.manager" value=""/>
-      <sysproperty key="java.security.policy" value="file:test/restricted.policy"/>
+      <!-- Note the extra '=' in the value, it *is* intentional -->
+      <sysproperty key="java.security.policy" value="=file:test/restricted.policy"/>
       <sysproperty key="java.awt.headless" value="true"/>
       <jvmarg value="${run.jvm.model}"/>
-      <arg value="-e"/>
+      <arg value="-ve"/>
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
       <echo message="Running rake @{rake.targets}"/>
       <echo message="compile=@{compile.mode}, threshold=@{jit.threshold}, objectspace=@{objectspace.enabled} threadpool=@{thread.pooling} reflection=@{reflection}"/>
 
       <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true" dir="@{dir}">
         <classpath refid="test.class.path"/>
 
         <jvmarg line="@{jvm.args}"/>
         <jvmarg value="${run.jvm.model}"/>
 
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
 	<sysproperty key="emma.coverage.out.file" value="${test.results.dir}/coverage.emma" />
 	<sysproperty key="emma.coverage.out.merge" value="true" />
         <sysproperty key="emma.verbosity.level" value="silent" />
 
         <arg line="@{jruby.args}"/>
         <arg value="${base.dir}/bin/rake"/>
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
           <arg line="checkout ${rubyspecs.revision}"/>
       </exec>
 
       <echo message="Rolling mspec to stable version"/>
       <exec dir="${mspec.dir}" executable="git">
           <arg line="checkout ${mspec.revision}"/>
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
 
   <target name="run-specs" depends="run-specs-precompiled, run-specs-compiled, run-specs-interpreted">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}" arg2="0"/>
         <equals arg1="${spec.status.COMPILED}"    arg2="0"/>
         <equals arg1="${spec.status.INTERPRETED}" arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-short" depends="run-specs-interpreted-short">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.INTERPRETED}"    arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-short-1.9" depends="run-specs-interpreted-short-1.9">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.INTERPRETED}"    arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-ci" depends="run-specs-compiled-short, run-specs-interpreted-short, run-specs-precompiled-short">
     <condition property="spec.status.combined.OK">
       <and>
         <equals arg1="${spec.status.PRECOMPILED}" arg2="0"/>
         <equals arg1="${spec.status.COMPILED}"    arg2="0"/>
         <equals arg1="${spec.status.INTERPRETED}" arg2="0"/>
       </and>
     </condition>
     <fail message="RubySpecs FAILED" unless="spec.status.combined.OK"/>
   </target>
 
   <target name="run-specs-ci-1.9" depends="run-specs-compiled-short-1.9, run-specs-interpreted-short-1.9, run-specs-precompiled-short-1.9">
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
     <_run_specs_internal mode.name="COMPILED" compile.mode="JIT" jit.threshold="0" spec.config="${spec.dir}/jruby.1.8.mspec"/>
   </target>
   <target name="run-specs-precompiled-short" depends="jar">
     <_run_specs_internal mode.name="PRECOMPILED" compile.mode="FORCE" jit.threshold="0" spec.config="${spec.dir}/jruby.1.8.mspec"/>
   </target>
   <target name="run-specs-interpreted-short" depends="jar">
     <_run_specs_internal mode.name="INTERPRETED" compile.mode="OFF" spec.config="${spec.dir}/jruby.1.8.mspec"/>
   </target>
 
   <target name="run-specs-compiled-short-1.9" depends="jar">
     <_run_specs_internal mode.name="COMPILED" compile.mode="JIT" jit.threshold="0" spec.config="${spec.dir}/jruby.1.9.mspec" compat="RUBY1_9"/>
   </target>
   <target name="run-specs-precompiled-short-1.9" depends="jar">
     <_run_specs_internal mode.name="PRECOMPILED" compile.mode="FORCE" jit.threshold="0" spec.config="${spec.dir}/jruby.1.9.mspec" compat="RUBY1_9"/>
   </target>
   <target name="run-specs-interpreted-short-1.9" depends="jar">
     <_run_specs_internal mode.name="INTERPRETED" compile.mode="OFF" spec.config="${spec.dir}/jruby.1.9.mspec" compat="RUBY1_9"/>
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
 	<classpath refid="test.class.path"/>
 
 	<jvmarg value="-ea"/>
         <jvmarg value="${run.jvm.model}"/>
 
 	<sysproperty key="jruby.home" value="${basedir}"/>
 	<sysproperty key="jruby.launch.inproc" value="false"/>
         <sysproperty key="emma.verbosity.level" value="silent" />
 
         <env key="JAVA_OPTS" value="-Demma.verbosity.level=silent"/>
 
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
         <arg line="-T -J-Demma.coverage.out.file=${test.results.dir}/coverage.emma"/>
         <arg line="-T -J-Demma.coverage.out.merge=true"/>
         <arg line="-T -J-Demma.verbosity.level=silent" />
         <arg line="-T -J${run.jvm.model}" />
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
     <attribute name="dist-stage-dir" default="${dist.stage.bin.dir}"/>
     <sequential>
       <fixcrlf srcdir="@{dist-stage-dir}/bin" excludes="*.bat,*.exe,*.dll" eol="lf"/>
       <fixcrlf srcdir="@{dist-stage-dir}/bin" includes="*.bat" eol="crlf"/>
     </sequential>
   </macrodef>
 
   <target name="apidocs" depends="prepare"
           description="Creates the Java API docs">
     <javadoc destdir="${api.docs.dir}" author="true" version="true" use="true"
              windowtitle="JRuby API" source="${javac.version}" useexternalfile="true"
              maxmemory="128m">
       <fileset dir="${src.dir}">
         <include name="**/*.java"/>
       </fileset>
       <classpath refid="build.classpath"/>
       <doctitle><![CDATA[<h1>JRuby</h1>]]></doctitle>
       <bottom><![CDATA[<i>Copyright &#169; 2002-2009 JRuby Team. All Rights Reserved.</i>]]></bottom>
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
     <include name="bin/ast*"/>
     <include name="bin/spec.bat"/>
     <include name="bin/rake.bat"/>
   </patternset>
 
   <patternset id="dist.lib.files">
     <include name="lib/ruby/1.8/**"/>
     <include name="lib/ruby/1.9/**"/>
     <include name="lib/ruby/site_ruby/**"/>
     <include name="lib/ruby/gems/1.8/specifications/sources-0.0.1.gemspec"/>
     <include name="lib/ruby/gems/1.8/gems/sources-0.0.1/**"/>
   </patternset>
 
   <patternset id="dist.files">
     <include name="lib/*"/>
     <include name="samples/**"/>
     <include name="docs/**"/>
     <include name="COPYING*"/>
     <include name="LICENSE.RUBY"/>
     <include name="README"/>
     <exclude name="lib/ruby/**"/>
   </patternset>
 
   <patternset id="dist.bin.files">
     <patternset refid="dist.files"/>
     <exclude name="lib/emma.jar"/>
     <exclude name="lib/emma_ant.jar"/>
     <exclude name="lib/junit.jar"/>
     <exclude name="lib/jarjar-1.0.jar"/>
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
     <exclude name="tool/nailgun/**"/>
     <include name="build_lib/**"/>
     <include name="Rakefile"/>
     <include name="build.xml"/>
     <include name="ivy/**"/>
     <include name="nbproject/*"/>
     <include name=".project"/>
     <include name=".classpath"/>
     <include name="default.build.properties"/>
     <include name="jruby.bnd.template"/>
     <exclude name="lib/jruby.jar"/>
   </patternset>
 
   <!-- note: tool/nailgun/configure must be added separately -->
   <patternset id="dist.nailgun.files">
     <include name="tool/nailgun/Makefile.in"/>
     <include name="tool/nailgun/README.txt"/>
     <include name="tool/nailgun/ng.exe"/>
     <include name="tool/nailgun/src/**"/>
   </patternset>
 
   <target name="dist-bin" depends="jar-dist">
     <mkdir dir="${dist.stage.bin.dir}"/>
     <copy todir="${dist.stage.bin.dir}">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <mkdir dir="${dist.stage.bin.dir}/lib/native"/>
     <unzip dest="${dist.stage.bin.dir}/lib/native">
         <fileset dir="build_lib">
             <include name="jffi-*.jar"/>
         </fileset>
         <patternset>
             <include name="**/libjffi-*.so"/>
             <include name="**/libjffi-*.jnilib"/>
             <include name="**/jffi-*.dll"/>
         </patternset>
         <mapper>
             <filtermapper>
                 <replacestring from="jni/" to="./"/>
             </filtermapper>
         </mapper>
     </unzip>
     <fixEOLs dist-stage-dir="${dist.stage.bin.dir}"/>
     <antcall target="install-gems">
       <param name="jruby.home" value="${dist.stage.bin.dir}"/>
       <param name="install.ri" value=""/>
       <param name="install.rdoc" value=""/>
     </antcall>
     <tar destfile="${dist.dir}/jruby-bin-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="${dist.stage.bin.dir}" mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="${dist.stage.bin.dir}" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.bin.files"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.nailgun.files"/>
       </tarfileset>
       <tarfileset dir="${basedir}" mode="755" prefix="jruby-${version.jruby}">
         <include name="tool/nailgun/configure"/>
       </tarfileset>
     </tar>
     <zip destfile="${dist.dir}/jruby-bin-${version.jruby}.zip">
       <zipfileset dir="${dist.stage.bin.dir}" filemode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </zipfileset>
       <zipfileset dir="${dist.stage.bin.dir}" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.bin.files"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.nailgun.files"/>
       </zipfileset>
       <zipfileset dir="${basedir}" filemode="755" prefix="jruby-${version.jruby}">
         <include name="tool/nailgun/configure"/>
       </zipfileset>
     </zip>
 
     <checksum file="${dist.dir}/jruby-bin-${version.jruby}.zip" algorithm="md5"/>
     <checksum file="${dist.dir}/jruby-bin-${version.jruby}.zip" algorithm="sha1"/>
     <checksum file="${dist.dir}/jruby-bin-${version.jruby}.tar.gz" algorithm="md5"/>
     <checksum file="${dist.dir}/jruby-bin-${version.jruby}.tar.gz" algorithm="sha1"/>
   </target>
 
   <target name="dist-src" depends="jar">
     <mkdir dir="${dist.stage.src.dir}"/>
     <copy todir="${dist.stage.src.dir}">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <antcall target="install-gems">
       <param name="jruby.home" value="${dist.stage.src.dir}"/>
       <param name="install.ri" value=""/>
       <param name="install.rdoc" value=""/>
     </antcall>
     <fixEOLs dist-stage-dir="${dist.stage.src.dir}"/>
     <tar destfile="${dist.dir}/jruby-src-${version.jruby}.tar.gz" compression="gzip">
       <tarfileset dir="${dist.stage.src.dir}" mode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </tarfileset>
       <tarfileset dir="${dist.stage.src.dir}" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.src.files"/>
       </tarfileset>
       <tarfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.nailgun.files"/>
       </tarfileset>
       <tarfileset dir="${basedir}" mode="755" prefix="jruby-${version.jruby}">
         <include name="tool/nailgun/configure"/>
       </tarfileset>
     </tar>
     <zip destfile="${dist.dir}/jruby-src-${version.jruby}.zip">
       <zipfileset dir="${dist.stage.src.dir}" filemode="755" prefix="jruby-${version.jruby}">
         <include name="bin/**"/>
       </zipfileset>
       <zipfileset dir="${dist.stage.src.dir}" prefix="jruby-${version.jruby}">
         <include name="lib/**"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.src.files"/>
       </zipfileset>
       <zipfileset dir="${basedir}" prefix="jruby-${version.jruby}">
         <patternset refid="dist.nailgun.files"/>
       </zipfileset>
       <zipfileset dir="${basedir}" filemode="755" prefix="jruby-${version.jruby}">
         <include name="tool/nailgun/configure"/>
       </zipfileset>
     </zip>
 
     <checksum file="${dist.dir}/jruby-src-${version.jruby}.zip" algorithm="md5"/>
     <checksum file="${dist.dir}/jruby-src-${version.jruby}.zip" algorithm="sha1"/>
     <checksum file="${dist.dir}/jruby-src-${version.jruby}.tar.gz" algorithm="md5"/>
     <checksum file="${dist.dir}/jruby-src-${version.jruby}.tar.gz" algorithm="sha1"/>
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
 
   <target name="dist" depends="dist-bin,dist-src,dist-jar-complete"/>
 
   <target name="dist-clean">
     <delete dir="${dist.dir}"/>
   </target>
 
   <target name="clean" depends="init" description="Cleans almost everything, leaves downloaded specs">
     <delete dir="${build.dir}"/>
     <delete dir="${dist.dir}"/>
     <delete quiet="false">
         <fileset dir="${lib.dir}" includes="jruby*.jar"/>
     </delete>
     <delete dir="${api.docs.dir}"/>
     <delete dir="src_gen"/>
     <antcall target="clean-ng"/>
   </target>
 
   <target name="clean-all" depends="clean, clear-specs, dist-clean" description="Cleans everything, including dist files and specs">
       <delete dir="${base.dir}/build.eclipse"/>
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
       <echo message="Configuring ng client in ${nailgun.home}"/>
       <exec executable="./configure" dir="${nailgun.home}" failonerror="true" output="/dev/null"/>
       <echo message="Building ng client in ${nailgun.home}"/>
     <exec executable="make" dir="${nailgun.home}" output="/dev/null"/>
   </target>
 
   <target name="need-clean-ng">
     <condition property="should-clean-ng">
       <and>
         <available file="${nailgun.home}/Makefile"/>
         <available file="${nailgun.home}/ng"/>
       </and>
     </condition>
   </target>
 
   <target name="clean-ng" depends="need-clean-ng" if="should-clean-ng">
     <exec executable="make" dir="${nailgun.home}" osfamily="unix" failifexecutionfails="false" output="/dev/null">
         <arg value="clean"/>
     </exec>
   </target>
 
   <target name="jruby-nailgun" depends="generate-method-classes,build-ng"
     description="Set up JRuby to be run with Nailgun (jruby-ng, jruby-ng-server)">
     <mkdir dir="${build.dir}/nailmain"/>
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
 
   <target name="fetch-prawn">
       <condition property="prawn-repo-exists">
           <available file="test/prawn/.git"/>
       </condition>
 
       <antcall target="do-fetch-prawn"/>
       <antcall target="do-update-prawn"/>
   </target>
 
   <target name="do-fetch-prawn" unless="prawn-repo-exists">
       <!-- Rails repo might already have been pulled, so delete it -->
       <antcall target="clear-prawn" inheritall="false"/>
 
       <echo message="Cloning prawn repo to: test/prawn"/>
       <exec executable="git">
           <arg value="clone"/>
           <arg value="--depth"/><arg value="1"/>
           <arg value="git://github.com/sandal/prawn.git"/>
           <arg value="test/prawn"/>
       </exec>
       <exec executable="git" dir="test/prawn">
           <arg value="checkout"/>
           <arg value="0.4.1"/>
       </exec>
       <exec executable="git" dir="test/prawn">
           <arg value="submodule"/>
           <arg value="init"/>
       </exec>
       <exec executable="git" dir="test/prawn">
           <arg value="submodule"/>
           <arg value="update"/>
       </exec>
   </target>
 
   <target name="do-update-prawn" if="prawn-repo-exists">
       <echo message="Updating rubyspec repo -- test/prawn"/>
       <exec dir="test/prawn" executable="git">
         <arg value="pull"/>
       </exec>
       <exec executable="git" dir="test/prawn">
           <arg value="checkout"/>
           <arg value="0.4.1"/>
       </exec>
       <exec executable="git" dir="test/prawn">
           <arg value="submodule"/>
           <arg value="update"/>
       </exec>
   </target>
 
   <target name="clear-prawn">
       <delete dir="test/prawn"/>
   </target>
 
   <target name="test-prawn" depends="jar,install-build-gems,fetch-prawn">
       <run-rake dir="test/prawn" rake.targets="test examples"/>
   </target>
 
   <target name="bench-language">
       <java fork="true" classname="org.jruby.Main" output="bench.language.client.interpreted.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-client"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <arg value="-X-C"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
       <java fork="true" classname="org.jruby.Main" output="bench.language.client.jitted.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-client"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <sysproperty key="jruby.jit.threshold" value="5"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
       <java fork="true" classname="org.jruby.Main" output="bench.language.client.precompiled.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-client"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <arg value="-X+C"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
 
       <java fork="true" classname="org.jruby.Main" output="bench.language.server.interpreted.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-server"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <arg value="-X-C"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
       <java fork="true" classname="org.jruby.Main" output="bench.language.server.jitted.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-server"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <sysproperty key="jruby.jit.threshold" value="5"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
       <java fork="true" classname="org.jruby.Main" output="bench.language.server.precompiled.txt" errorproperty="bench.error" failonerror="false">
         <classpath refid="build.classpath"/>
         <classpath path="${jruby.classes.dir}"/>
         <jvmarg value="-server"/>
         <jvmarg value="${run.jvm.model}"/>
         <sysproperty key="jruby.home" value="${jruby.home}"/>
         <arg value="-X+C"/>
         <arg value="bench/language/bench_all.rb"/>
         <arg value="5"/>
       </java>
   </target>
 </project>
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index fdbbae7a22..a1b4c880a8 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -215,2006 +215,2002 @@ public final class Ruby {
      * from Ruby code to activate the current runtime as the global one.
      */
     public void useAsGlobalRuntime() {
         synchronized(Ruby.class) {
             globalRuntime = null;
             setGlobalRuntimeFirstTimeOnly(this);
         }
     }
 
     /**
      * Create and initialize a new JRuby runtime. The properties of the
      * specified RubyInstanceConfig will be used to determine various JRuby
      * runtime characteristics.
      * 
      * @param config The configuration to use for the new instance
      * @see org.jruby.RubyInstanceConfig
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.is1_9               = config.getCompatVersion() == CompatVersion.RUBY1_9;
         this.threadService      = new ThreadService(this);
         if(config.isSamplingEnabled()) {
             org.jruby.util.SimpleSampler.registerThreadContext(threadService.getCurrentContext());
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();
         this.kcode              = config.getKCode();
         this.beanManager        = BeanManagerFactory.create(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
     }
     
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      * 
      * @param script The scriptlet to run
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script) {
         ThreadContext context = getCurrentContext();
         DynamicScope currentScope = context.getCurrentScope();
         ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);
 
         return evalScriptlet(script, newScope);
     }
 
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      *
      * This version accepts a scope to use, so you can eval many times against
      * the same scope.
      *
      * @param script The scriptlet to run
      * @param scope The scope to execute against (ManyVarsDynamicScope is
      * recommended, so it can grow as needed)
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script, DynamicScope scope) {
         ThreadContext context = getCurrentContext();
         Node node = parseEval(script, "<script>", scope, 0);
 
         try {
             context.preEvalScriptlet(scope);
             return node.interpret(this, context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.RETURN, (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalScriptlet();
         }
     }
     
     /**
      * Parse and execute the specified script 
      * This differs from the other methods in that it accepts a string-based script and
      * parses and runs it as though it were loaded at a command-line. This is the preferred
      * way to start up a new script when calling directly into the Ruby object (which is
      * generally *dis*couraged.
      * 
      * @param script The contents of the script to run as a normal, root script
      * @return The last value of the script
      */
     public IRubyObject executeScript(String script, String filename) {
         byte[] bytes;
         
         try {
             bytes = script.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = script.getBytes();
         }
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(node.getPosition());
             return runNormally(node);
         } finally {
             context.setFileAndLine(oldFile, oldLine);
         }
     }
     
     /**
      * Run the script contained in the specified input stream, using the
      * specified filename as the name of the script being executed. The stream
      * will be read fully before being parsed and executed. The given filename
      * will be used for the ruby $PROGRAM_NAME and $0 global variables in this
      * runtime.
      * 
      * This method is intended to be called once per runtime, generally from
      * Main or from main-like top-level entry points.
      * 
      * As part of executing the script loaded from the input stream, various
      * RubyInstanceConfig properties will be used to determine whether to
      * compile the script before execution or run with various wrappers (for
      * looping, printing, and so on, see jruby -help).
      * 
      * @param inputStream The InputStream from which to read the script contents
      * @param filename The filename to use when parsing, and for $PROGRAM_NAME
      * and $0 ruby global variables.
      */
     public void runFromMain(InputStream inputStream, String filename) {
         IAccessor d = new ValueAccessor(newString(filename));
         getGlobalVariables().define("$PROGRAM_NAME", d);
         getGlobalVariables().define("$0", d);
 
         for (Iterator i = config.getOptionGlobals().entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             Object value = entry.getValue();
             IRubyObject varvalue;
             if (value != null) {
                 varvalue = newString(value.toString());
             } else {
                 varvalue = getTrue();
             }
             getGlobalVariables().set("$" + entry.getKey().toString(), varvalue);
         }
 
         if (filename.endsWith(".class")) {
             // we are presumably running a precompiled class; load directly
             Script script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
             if (script == null) {
                 throw new MainExitException(1, "error: .class file specified is not a compiled JRuby script");
             }
             script.setFilename(filename);
             runScript(script);
             return;
         }
         
         Node scriptNode = parseFromMain(inputStream, filename);
         ThreadContext context = getCurrentContext();
 
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit());
             } else {
                 runNormally(scriptNode);
             }
         } finally {
             context.setFileAndLine(oldFile, oldLine);
         }
     }
 
     /**
      * Parse the script contained in the given input stream, using the given
      * filename as the name of the script, and return the root Node. This
      * is used to verify that the script syntax is valid, for jruby -c. The
      * current scope (generally the top-level scope) is used as the parent
      * scope for parsing.
      * 
      * @param inputStream The input stream from which to read the script
      * @param filename The filename to use for parsing
      * @returns The root node of the parsed script
      */
     public Node parseFromMain(InputStream inputStream, String filename) {
         if (config.isInlineScript()) {
             return parseInline(inputStream, filename, getCurrentContext().getCurrentScope());
         } else {
             return parseFile(inputStream, filename, getCurrentContext().getCurrentScope());
         }
     }
 
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      *
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     @Deprecated
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean unused) {
         return runWithGetsLoop(scriptNode, printing, processLineEnds, split);
     }
     
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      * 
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
 
         // we do preand post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         RuntimeHelpers.preLoad(context, ((RootNode)scriptNode).getStaticScope().getVariables());
 
         try {
             while (RubyKernel.gets(context, getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                 loop: while (true) { // Used for the 'redo' command
                     try {
                         if (processLineEnds) {
                             getGlobalVariables().get("$_").callMethod(context, "chop!");
                         }
 
                         if (split) {
                             getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                         }
 
                         if (script != null) {
                             runScriptBody(script);
                         } else {
                             runInterpreterBody(scriptNode);
                         }
 
                         if (printing) RubyKernel.print(context, getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
                         break loop;
                     } catch (JumpException.RedoJump rj) {
                         // do nothing, this iteration restarts
                     } catch (JumpException.NextJump nj) {
                         // recheck condition
                         break loop;
                     } catch (JumpException.BreakJump bj) {
                         // end loop
                         return (IRubyObject) bj.getValue();
                     }
                 }
             }
         } finally {
             RuntimeHelpers.postLoad(context);
         }
         
         return getNil();
     }
 
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      *
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     @Deprecated
     public IRubyObject runNormally(Node scriptNode, boolean unused) {
         return runNormally(scriptNode);
     }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode) {
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (compile) {
             script = tryCompile(scriptNode, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
             if (forceCompile && script == null) {
                 return getNil();
             }
         }
         
         if (script != null) {
             if (config.isShowBytecode()) {
                 return nilObject;
             } else {
                 return runScript(script);
             }
         } else {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             return runInterpreter(scriptNode);
         }
     }
     
     public Script tryCompile(Node node) {
         return tryCompile(node, new JRubyClassLoader(getJRubyClassLoader()));
     }
     
     private Script tryCompile(Node node, JRubyClassLoader classLoader) {
         return tryCompile(node, classLoader, false);
     }
 
     private Script tryCompile(Node node, JRubyClassLoader classLoader, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
             StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     private IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         assert scriptNode != null : "scriptNode is not null";
         
         try {
             return scriptNode.interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         ThreadContext context = getCurrentContext();
 
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode;
 
         try {
             return ((RootNode)scriptNode).interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public Parser getParser() {
         return parser;
     }
     
     public BeanManager getBeanManager() {
         return beanManager;
     }
     
     public JITCompiler getJITCompiler() {
         return jitCompiler;
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
     
     @Deprecated
     public static Ruby getCurrentInstance() {
         return null;
     }
     
     @Deprecated
     public static void setCurrentInstance(Ruby runtime) {
     }
     
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace.
      * 
      * @param name The name of the module
      * @return The module or null if not found
      */
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName The name of the module; <em>must</em> be an interned String
      * @return The module or null if not found
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** 
      * Retrieve the class with the given name from the Object namespace.
      *
      * @param name The name of the class
      * @return The class
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * Retrieve the class with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** 
      * Define a new class under the Object namespace. Roughly equivalent to
      * rb_define_class in MRI.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass);
     }
 
     /** 
      * A variation of defineClass that allows passing in an array of subplementary
      * call sites for improving dynamic invocation performance.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator, CallSite[] callSites) {
         return defineClassUnder(name, superClass, allocator, objectClass, callSites);
     }
 
     /**
      * Define a new class with the given name under the given module or class
      * namespace. Roughly equivalent to rb_define_class_under in MRI.
      * 
      * If the name specified is already bound, its value will be returned if:
      * * It is a class
      * * No new superclass is being defined
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         return defineClassUnder(name, superClass, allocator, parent, null);
     }
 
     /**
      * A variation of defineClassUnder that allows passing in an array of
      * supplementary call sites to improve dynamic invocation.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @param callSites The array of call sites to add
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent, CallSite[] callSites) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) {
                 throw newNameError(name + " is already defined", name);
             }
             // If we define a class in Ruby, but later want to allow it to be defined in Java,
             // the allocator needs to be updated
             if (klazz.getAllocator() != allocator) {
                 klazz.setAllocator(allocator);
             }
             return klazz;
         }
         
         boolean parentIsObject = parent == objectClass;
 
         if (superClass == null) {
             String className = parentIsObject ? name : parent.getName() + "::" + name;  
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed", className);
             
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, !parentIsObject, callSites);
     }
 
     /** 
      * Define a new module under the Object namespace. Roughly equivalent to
      * rb_define_module in MRI.
      * 
      * @param name The name of the new module
      * @returns The new module
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass);
     }
 
     /**
      * Define a new module with the given name under the given module or
      * class namespace. Roughly equivalent to rb_define_module_under in MRI.
      * 
      * @param name The name of the new module
      * @param parent The class or module namespace under which to define the
      * module
      * @returns The new module
      */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
         
         boolean parentIsObject = parent == objectClass;
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             
             if (parentIsObject) {
                 throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
             } else {
                 throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
             }
         }
 
         return RubyModule.newModule(this, name, parent, !parentIsObject);
     }
 
     /**
      * From Object, retrieve the named module. If it doesn't exist a
      * new module is created.
      * 
      * @param name The name of the module
      * @returns The existing or new module
      */
     public RubyModule getOrCreateModule(String name) {
         IRubyObject module = objectClass.getConstantAt(name);
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!module.isModule()) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
     }
 
 
     /** 
      * Retrieve the current safe level.
      * 
      * @see org.jruby.Ruby#setSaveLevel
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
 
     /** 
      * Set the current safe level:
      * 
      * 0 - strings from streams/environment/ARGV are tainted (default)
      * 1 - no dangerous operation by tainted value
      * 2 - process/file operations prohibited
      * 3 - all generated objects are tainted
      * 4 - no global (non-tainted) variable modification/no direct output
      * 
      * The safe level is set using $SAFE in Ruby code. It is not particularly
      * well supported in JRuby.
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
 
     // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
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
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** 
      * This method is called immediately after constructing the Ruby instance.
      * The main thread is prepared for execution, all core classes and libraries
      * are initialized, and any libraries required on the command line are
      * loaded.
      */
     private void init() {
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
         safeLevel = config.getSafeLevel();
         
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         javaSupport = new JavaSupport(this);
         
         if (RubyInstanceConfig.POOLING_ENABLED) {
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot();
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.loadPaths());
 
         // initialize builtin libraries
         initBuiltins();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             RubyKernel.require(getTopSelf(), newString(scriptName), Block.NULL_BLOCK);
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initRoot() {
         boolean oneNine = is1_9();
         // Bootstrap the top of the hierarchy
         if (oneNine) {
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.OBJECT_ALLOCATOR);
             objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         } else {
             objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         }
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         if (oneNine) basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         if (oneNine) metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         if (oneNine) RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
         
         // set constants now that they're initialized
         if (oneNine) objectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
     }
 
     private void initCore() {
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             RubyEncoding.createEncodingClass(this);
             RubyConverter.createConverterClass(this);
             encodingService = new EncodingService(this);
         }
 
         RubySymbol.createSymbolClass(this);
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (!is1_9()) {
             if (profile.allowModule("Precision")) {
                 RubyPrecision.createPrecisionModule(this);
             }
         }
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         if (is1_9()) {
             if (profile.allowClass("Complex")) {
                 RubyComplex.createComplexClass(this);
             }
             if (profile.allowClass("Rational")) {
                 RubyRational.createRationalClass(this);
             }
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
         }
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass, new IRubyObject[]{newString("Tms"), newSymbol("utime"), newSymbol("stime"), newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) {
             RubyBinding.createBindingClass(this);
         }
         // Math depends on all numeric types
         if (profile.allowModule("Math")) {
             RubyMath.createMathModule(this);
         }
         if (profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if (profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if (profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if (profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
         if (profile.allowClass("Proc")) {
             RubyProc.createProcClass(this);
         }
         if (profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
         if (profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if (profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
         if (profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
         if (profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
         // depends on IO, FileTest
         if (profile.allowClass("File")) {
             RubyFile.createFileClass(this);
         }
         if (profile.allowClass("File::Stat")) {
             RubyFileStat.createFileStatClass(this);
         }
         if (profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if (profile.allowClass("Time")) {
             RubyTime.createTimeClass(this);
         }
         if (profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
-        if (!isSecurityRestricted()) {
-            // Signal uses sun.misc.* classes, this is not allowed
-            // in the security-sensitive environments
-            if (profile.allowModule("Signal")) {
-                RubySignal.createSignal(this);
-            }
+        if (profile.allowModule("Signal")) {
+            RubySignal.createSignal(this);
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     public static final int NIL_PREFILLED_ARRAY_SIZE = RubyArray.ARRAY_DEFAULT_SIZE * 8;
     private final IRubyObject nilPrefilledArray[] = new IRubyObject[NIL_PREFILLED_ARRAY_SIZE];
     public IRubyObject[] getNilPrefilledArray() {
         return nilPrefilledArray;
     }
 
     private void initExceptions() {
         standardError = defineClassIfAllowed("StandardError", exceptionClass);
         runtimeError = defineClassIfAllowed("RuntimeError", standardError);
         ioError = defineClassIfAllowed("IOError", standardError);
         scriptError = defineClassIfAllowed("ScriptError", exceptionClass);
         rangeError = defineClassIfAllowed("RangeError", standardError);
         signalException = defineClassIfAllowed("SignalException", exceptionClass);
         
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
             nameErrorMessage = RubyNameError.createNameErrorMessageClass(this, nameError);            
         }
         if (profile.allowClass("NoMethodError")) {
             noMethodError = RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }
         if (profile.allowClass("SystemExit")) {
             systemExit = RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("LocalJumpError")) {
             localJumpError = RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("NativeException")) {
             nativeException = NativeException.createClass(this, runtimeError);
         }
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
 
         fatal = defineClassIfAllowed("Fatal", exceptionClass);
         interrupt = defineClassIfAllowed("Interrupt", signalException);
         typeError = defineClassIfAllowed("TypeError", standardError);
         argumentError = defineClassIfAllowed("ArgumentError", standardError);
         indexError = defineClassIfAllowed("IndexError", standardError);
         stopIteration = defineClassIfAllowed("StopIteration", indexError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator()); 
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
                 invalidByteSequenceError = defineClassUnder("InvalidByteSequenceError", encodingError, encodingError.getAllocator(), encodingClass);
                 undefinedConversionError = defineClassUnder("UndefinedConversionError", encodingError, encodingError.getAllocator(), encodingClass);
                 converterNotFoundError = defineClassUnder("ConverterNotFoundError", encodingError, encodingError.getAllocator(), encodingClass);
             }
         }
 
         initErrno();
     }
     
     private RubyClass defineClassIfAllowed(String name, RubyClass superClass) {
 	// TODO: should probably apply the null object pattern for a
 	// non-allowed class, rather than null
         if (superClass != null && profile.allowClass(name)) {
             return defineClass(name, superClass, superClass.getAllocator());
         }
         return null;
     }
 
     private Map<Integer, RubyClass> errnos = new HashMap<Integer, RubyClass>();
 
     public RubyClass getErrno(int n) {
         return errnos.get(n);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrno() {
         if (profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
             try {
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.value(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 e.printStackTrace();
             }
         }
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
             errnos.put(i, errno);
             errno.defineConstant("Errno", newFixnum(i));
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         
         addLazyBuiltin("minijava.rb", "minijava", "org.jruby.java.MiniJava");
         
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.RubyJRuby$ExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.RubyJRuby$UtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.DigestLibrary$MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRef$WeakRefLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.Factory$Service");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
         }
         
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             LoadService.reflectedLoad(this, "fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader(), false);
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
         
         String[] builtins = {"yaml", 
                              "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
                              "yaml/compat", "yaml/constants", "yaml/dbm", 
                              "yaml/emitter", "yaml/encoding", "yaml/error", 
                              "yaml/rubytypes", "yaml/store", "yaml/stream", 
                              "yaml/stringio", "yaml/tag", "yaml/types", 
                              "yaml/yamlnode", "yaml/ypath", 
                              "jsignal", "generator", "prelude"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             getLoadService().require("builtin/prelude.rb");
         }
 
         getLoadService().require("builtin/core_ext/symbol");
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
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
 
     public void setCurrentLine(int line) {
         currentLine = line;
     }
 
     public int getCurrentLine() {
         return currentLine;
     }
 
     public void setArgsFile(IRubyObject argsFile) {
         this.argsFile = argsFile;
     }
 
     public IRubyObject getArgsFile() {
         return argsFile;
     }
     
     public RubyModule getEtc() {
         return etcModule;
     }
     
     public void setEtc(RubyModule etcModule) {
         this.etcModule = etcModule;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyClass getBasicObject() {
         return basicObjectClass;
     }
 
     public RubyClass getModule() {
         return moduleClass;
     }
 
     public RubyClass getClassClass() {
         return classClass;
     }
     
     public RubyModule getKernel() {
         return kernelModule;
     }
     void setKernel(RubyModule kernelModule) {
         this.kernelModule = kernelModule;
     }
 
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
     }
     
     public RubyClass getDummy() {
         return dummyClass;
     }
 
     public RubyModule getComparable() {
         return comparableModule;
     }
     void setComparable(RubyModule comparableModule) {
         this.comparableModule = comparableModule;
     }    
 
     public RubyClass getNumeric() {
         return numericClass;
     }
     void setNumeric(RubyClass numericClass) {
         this.numericClass = numericClass;
     }    
 
     public RubyClass getFloat() {
         return floatClass;
     }
     void setFloat(RubyClass floatClass) {
         this.floatClass = floatClass;
     }
     
     public RubyClass getInteger() {
         return integerClass;
     }
     void setInteger(RubyClass integerClass) {
         this.integerClass = integerClass;
     }    
     
     public RubyClass getFixnum() {
         return fixnumClass;
     }
     void setFixnum(RubyClass fixnumClass) {
         this.fixnumClass = fixnumClass;
     }
 
     public RubyClass getComplex() {
         return complexClass;
     }
     void setComplex(RubyClass complexClass) {
         this.complexClass = complexClass;
     }
 
     public RubyClass getRational() {
         return rationalClass;
     }
     void setRational(RubyClass rationalClass) {
         this.rationalClass = rationalClass;
     }
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }
 
     public RubyClass getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
     }
 
     public RubyClass getYielder() {
         return yielderClass;
     }
     void setYielder(RubyClass yielderClass) {
         this.yielderClass = yielderClass;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }
 
     public RubyClass getEncoding() {
         return encodingClass;
     }
     void setEncoding(RubyClass encodingClass) {
         this.encodingClass = encodingClass;
     }
 
     public RubyClass getConverter() {
         return converterClass;
     }
     void setConverter(RubyClass converterClass) {
         this.converterClass = converterClass;
     }
 
     public RubyClass getSymbol() {
         return symbolClass;
     }
     void setSymbol(RubyClass symbolClass) {
         this.symbolClass = symbolClass;
     }
 
     public RubyClass getArray() {
         return arrayClass;
     }    
     void setArray(RubyClass arrayClass) {
         this.arrayClass = arrayClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }
     void setHash(RubyClass hashClass) {
         this.hashClass = hashClass;
     }
 
     public RubyClass getRange() {
         return rangeClass;
     }
     void setRange(RubyClass rangeClass) {
         this.rangeClass = rangeClass;
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
 
     public IRubyObject[] getSingleNilArray() {
         return singleNilArray;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
     void setNilClass(RubyClass nilClass) {
         this.nilClass = nilClass;
     }
 
     public RubyClass getTrueClass() {
         return trueClass;
     }
     void setTrueClass(RubyClass trueClass) {
         this.trueClass = trueClass;
     }
 
     public RubyClass getFalseClass() {
         return falseClass;
     }
     void setFalseClass(RubyClass falseClass) {
         this.falseClass = falseClass;
     }
 
     public RubyClass getProc() {
         return procClass;
     }
     void setProc(RubyClass procClass) {
         this.procClass = procClass;
     }
 
     public RubyClass getBinding() {
         return bindingClass;
     }
     void setBinding(RubyClass bindingClass) {
         this.bindingClass = bindingClass;
     }
 
     public RubyClass getMethod() {
         return methodClass;
     }
     void setMethod(RubyClass methodClass) {
         this.methodClass = methodClass;
     }    
 
     public RubyClass getUnboundMethod() {
         return unboundMethodClass;
     }
     void setUnboundMethod(RubyClass unboundMethodClass) {
         this.unboundMethodClass = unboundMethodClass;
     }    
 
     public RubyClass getMatchData() {
         return matchDataClass;
     }
     void setMatchData(RubyClass matchDataClass) {
         this.matchDataClass = matchDataClass;
     }    
 
     public RubyClass getRegexp() {
         return regexpClass;
     }
     void setRegexp(RubyClass regexpClass) {
         this.regexpClass = regexpClass;
     }    
 
     public RubyClass getTime() {
         return timeClass;
     }
     void setTime(RubyClass timeClass) {
         this.timeClass = timeClass;
     }    
 
     public RubyModule getMath() {
         return mathModule;
     }
     void setMath(RubyModule mathModule) {
         this.mathModule = mathModule;
     }    
 
     public RubyModule getMarshal() {
         return marshalModule;
     }
     void setMarshal(RubyModule marshalModule) {
         this.marshalModule = marshalModule;
     }    
 
     public RubyClass getBignum() {
         return bignumClass;
     }
     void setBignum(RubyClass bignumClass) {
         this.bignumClass = bignumClass;
     }    
 
     public RubyClass getDir() {
         return dirClass;
     }
     void setDir(RubyClass dirClass) {
         this.dirClass = dirClass;
     }    
 
     public RubyClass getFile() {
         return fileClass;
     }
     void setFile(RubyClass fileClass) {
         this.fileClass = fileClass;
     }    
 
     public RubyClass getFileStat() {
         return fileStatClass;
     }
     void setFileStat(RubyClass fileStatClass) {
         this.fileStatClass = fileStatClass;
     }    
 
     public RubyModule getFileTest() {
         return fileTestModule;
     }
     void setFileTest(RubyModule fileTestModule) {
         this.fileTestModule = fileTestModule;
     }
     
     public RubyClass getIO() {
         return ioClass;
     }
     void setIO(RubyClass ioClass) {
         this.ioClass = ioClass;
     }    
 
     public RubyClass getThread() {
         return threadClass;
     }
     void setThread(RubyClass threadClass) {
         this.threadClass = threadClass;
     }    
 
     public RubyClass getThreadGroup() {
         return threadGroupClass;
     }
     void setThreadGroup(RubyClass threadGroupClass) {
         this.threadGroupClass = threadGroupClass;
     }
     
     public RubyThreadGroup getDefaultThreadGroup() {
         return defaultThreadGroup;
     }
     void setDefaultThreadGroup(RubyThreadGroup defaultThreadGroup) {
         this.defaultThreadGroup = defaultThreadGroup;
     }
 
     public RubyClass getContinuation() {
         return continuationClass;
     }
     void setContinuation(RubyClass continuationClass) {
         this.continuationClass = continuationClass;
     }    
 
     public RubyClass getStructClass() {
         return structClass;
     }
     void setStructClass(RubyClass structClass) {
         this.structClass = structClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
     void setTmsStruct(RubyClass tmsStruct) {
         this.tmsStruct = tmsStruct;
     }
     
     public IRubyObject getPasswdStruct() {
         return passwdStruct;
     }
     void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     void setGroupStruct(RubyClass groupStruct) {
         this.groupStruct = groupStruct;
     }
 
     public RubyModule getGC() {
         return gcModule;
     }
     void setGC(RubyModule gcModule) {
         this.gcModule = gcModule;
     }    
 
     public RubyModule getObjectSpaceModule() {
         return objectSpaceModule;
     }
     void setObjectSpaceModule(RubyModule objectSpaceModule) {
         this.objectSpaceModule = objectSpaceModule;
     }    
 
     public RubyModule getProcess() {
         return processModule;
     }
     void setProcess(RubyModule processModule) {
         this.processModule = processModule;
     }    
 
     public RubyClass getProcStatus() {
         return procStatusClass; 
     }
     void setProcStatus(RubyClass procStatusClass) {
         this.procStatusClass = procStatusClass;
     }
     
     public RubyModule getProcUID() {
         return procUIDModule;
     }
     void setProcUID(RubyModule procUIDModule) {
         this.procUIDModule = procUIDModule;
     }
     
     public RubyModule getProcGID() {
         return procGIDModule;
     }
     void setProcGID(RubyModule procGIDModule) {
         this.procGIDModule = procGIDModule;
     }
     
     public RubyModule getProcSysModule() {
         return procSysModule;
     }
     void setProcSys(RubyModule procSysModule) {
         this.procSysModule = procSysModule;
     }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }
 
     public RubyModule getErrno() {
         return errnoModule;
     }
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
 
     public RubyClass getNameError() {
         return nameError;
     }
 
     public RubyClass getNameErrorMessage() {
         return nameErrorMessage;
     }
 
     public RubyClass getNoMethodError() {
         return noMethodError;
     }
 
     public RubyClass getSignalException() {
         return signalException;
     }
 
     public RubyClass getRangeError() {
         return rangeError;
     }
 
     public RubyClass getSystemExit() {
         return systemExit;
     }
 
     public RubyClass getLocalJumpError() {
         return localJumpError;
     }
 
     public RubyClass getNativeException() {
         return nativeException;
     }
 
     public RubyClass getSystemCallError() {
         return systemCallError;
     }
 
     public RubyClass getFatal() {
         return fatal;
     }
     
     public RubyClass getInterrupt() {
         return interrupt;
     }
     
     public RubyClass getTypeError() {
         return typeError;
     }
 
     public RubyClass getArgumentError() {
         return argumentError;
     }
 
     public RubyClass getIndexError() {
         return indexError;
     }
 
     public RubyClass getStopIteration() {
         return stopIteration;
     }
 
     public RubyClass getSyntaxError() {
         return syntaxError;
     }
 
     public RubyClass getStandardError() {
         return standardError;
     }
     
     public RubyClass getRuntimeError() {
         return runtimeError;
     }
     
     public RubyClass getIOError() {
         return ioError;
     }
 
     public RubyClass getLoadError() {
         return loadError;
     }
 
     public RubyClass getNotImplementedError() {
         return notImplementedError;
     }
 
     public RubyClass getSecurityError() {
         return securityError;
     }
 
     public RubyClass getNoMemoryError() {
         return noMemoryError;
     }
 
     public RubyClass getRegexpError() {
         return regexpError;
     }
 
     public RubyClass getEOFError() {
         return eofError;
     }
 
     public RubyClass getThreadError() {
         return threadError;
     }
 
     public RubyClass getConcurrencyError() {
         return concurrencyError;
     }
 
     public RubyClass getSystemStackError() {
         return systemStackError;
     }
 
     public RubyClass getZeroDivisionError() {
         return zeroDivisionError;
     }
 
     public RubyClass getFloatDomainError() {
         return floatDomainError;
     }
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     private RubyHash charsetMap;
     public RubyHash getCharsetMap() {
         if (charsetMap == null) charsetMap = new RubyHash(this);
         return charsetMap;
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verbose;
     }
 
     public boolean isVerbose() {
         return isVerbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
         isVerbose = verbose.isTrue();
         warningsEnabled = !verbose.isNil();
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
 
     public static ClassLoader getClassLoader() {
         // we try to get the classloader that loaded JRuby, falling back on System
         ClassLoader loader = Ruby.class.getClassLoader();
         if (loader == null) {
             loader = ClassLoader.getSystemClassLoader();
         }
         
         return loader;
     }
 
     public synchronized JRubyClassLoader getJRubyClassLoader() {
         // FIXME: Get rid of laziness and handle restricted access elsewhere
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null) {
             jrubyClassLoader = new JRubyClassLoader(config.getLoader());
         }
         
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
 
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
     }
 
 
     public ThreadService getThreadService() {
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index a2a0b2f051..dec29b0c8f 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1799 +1,1800 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
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
 package org.jruby;
 
 import com.kenai.constantine.platform.Fcntl;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.CancelledKeyException;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import java.util.concurrent.atomic.AtomicInteger;
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.libraries.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
+import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject {
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     protected IRubyObject externalEncoding;
     protected IRubyObject internalEncoding;
     
 
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
         getRuntime().registerDescriptor(descriptor,isRetained);
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor) {
         registerDescriptor(descriptor,false); // default: don't retain
     }
     
     public void unregisterDescriptor(int aFileno) {
         getRuntime().unregisterDescriptor(aFileno);
     }
     
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return getRuntime().getDescriptorByFileno(aFileno);
     }
     
     // FIXME can't use static; would interfere with other runtimes in the same JVM
     protected static AtomicInteger filenoIndex = new AtomicInteger(2);
     
     public static int getNewFileno() {
         return filenoIndex.incrementAndGet();
     }
 
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(outputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(inputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channelpo");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             if (openFile.isReadable()) {
                 Channel inChannel;
                 if (process.getInput() != null) {
                     // NIO-based
                     inChannel = process.getInput();
                 } else {
                     // Stream-based
                     inChannel = Channels.newChannel(process.getInputStream());
                 }
                 
                 ChannelDescriptor main = new ChannelDescriptor(
                         inChannel,
                         getNewFileno(),
                         new FileDescriptor());
                 main.setCanBeSeekable(false);
                 
                 openFile.setMainStream(new ChannelStream(getRuntime(), main));
                 registerDescriptor(main);
             }
             
             if (openFile.isWritable() && process.hasOutput()) {
                 Channel outChannel;
                 if (process.getOutput() != null) {
                     // NIO-based
                     outChannel = process.getOutput();
                 } else {
                     outChannel = Channels.newChannel(process.getOutputStream());
                 }
 
                 ChannelDescriptor pipe = new ChannelDescriptor(
                         outChannel,
                         getNewFileno(),
                         new FileDescriptor());
                 pipe.setCanBeSeekable(false);
                 
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(new ChannelStream(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(new ChannelStream(getRuntime(), pipe));
                 }
                 
                 registerDescriptor(pipe);
             }
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
 
         try {
             switch (stdio) {
             case IN:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             // special constructor that accepts stream, not channel
                             new ChannelDescriptor(runtime.getIn(), 0, new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in),
                             FileDescriptor.in));
                 break;
             case OUT:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getOut()), 1, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out),
                             FileDescriptor.out));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getErr()), 2, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err), 
                             FileDescriptor.err));
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());        
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
         ioClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyIO;
             }
         };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         return getOpenFileChecked().getMainStream().newOutputStream();
     }
 
     public InputStream getInStream() {
         return getOpenFileChecked().getMainStream().newInputStream();
     }
 
     public Channel getChannel() {
         if (getOpenFileChecked().getMainStream() instanceof ChannelStream) {
             return ((ChannelStream) openFile.getMainStream()).getDescriptor().getChannel();
         } else {
             return null;
         }
     }
     
     public Stream getHandler() {
         return getOpenFileChecked().getMainStream();
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) throws InvalidValueException {
         Ruby runtime = context.getRuntime();
         
     	if (args.length < 1) {
             throw runtime.newArgumentError("wrong number of arguments");
     	}
     	
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             try {
                 RubyIO ios = (RubyIO) tmp;
 
                 if (ios.openFile == this.openFile) {
                     return this;
                 }
 
                 OpenFile originalFile = ios.getOpenFileChecked();
                 OpenFile selfFile = getOpenFileChecked();
 
                 long pos = 0;
                 if (originalFile.isReadable()) {
                     pos = originalFile.getMainStream().fgetpos();
                 }
 
                 if (originalFile.getPipeStream() != null) {
                     originalFile.getPipeStream().fflush();
                 } else if (originalFile.isWritable()) {
                     originalFile.getMainStream().fflush();
                 }
 
                 if (selfFile.isWritable()) {
                     selfFile.getWriteStream().fflush();
                 }
 
                 selfFile.setMode(originalFile.getMode());
                 selfFile.setProcess(originalFile.getProcess());
                 selfFile.setLineNumber(originalFile.getLineNumber());
                 selfFile.setPath(originalFile.getPath());
                 selfFile.setFinalizer(originalFile.getFinalizer());
 
                 ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
                 ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
 
                 // confirm we're not reopening self's channel
                 if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                     // check if we're a stdio IO, and ensure we're not badly mutilated
                     if (selfDescriptor.getFileno() >=0 && selfDescriptor.getFileno() <= 2) {
                         selfFile.getMainStream().clearerr();
                         
                         // dup2 new fd into self to preserve fileno and references to it
                         originalDescriptor.dup2Into(selfDescriptor);
                         
                         // re-register, since fileno points at something new now
                         registerDescriptor(selfDescriptor);
                     } else {
                         Stream pipeFile = selfFile.getPipeStream();
                         int mode = selfFile.getMode();
                         selfFile.getMainStream().fclose();
                         selfFile.setPipeStream(null);
 
                         // TODO: turn off readable? am I reading this right?
                         // This only seems to be used while duping below, since modes gets
                         // reset to actual modes afterward
                         //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                         if (pipeFile != null) {
                             selfFile.setMainStream(ChannelStream.fdopen(runtime, originalDescriptor, new ModeFlags()));
                             selfFile.setPipeStream(pipeFile);
                         } else {
                             selfFile.setMainStream(
                                     new ChannelStream(
                                         runtime,
                                         originalDescriptor.dup2(selfDescriptor.getFileno())));
                             
                             // re-register the descriptor
                             registerDescriptor(selfFile.getMainStream().getDescriptor());
                             
                             // since we're not actually duping the incoming channel into our handler, we need to
                             // copy the original sync behavior from the other handler
                             selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
                         }
                         selfFile.setMode(mode);
                     }
                     
                     // TODO: anything threads attached to original fd are notified of the close...
                     // see rb_thread_fd_close
                     
                     if (originalFile.isReadable() && pos >= 0) {
                         selfFile.seek(pos, Stream.SEEK_SET);
                         originalFile.seek(pos, Stream.SEEK_SET);
                     }
                 }
 
                 if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                     int fd = selfFile.getPipeStream().getDescriptor().getFileno();
                     
                     if (originalFile.getPipeStream() == null) {
                         selfFile.getPipeStream().fclose();
                         selfFile.setPipeStream(null);
                     } else if (fd != originalFile.getPipeStream().getDescriptor().getFileno()) {
                         selfFile.getPipeStream().fclose();
                         ChannelDescriptor newFD2 = originalFile.getPipeStream().getDescriptor().dup2(fd);
                         selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, getIOModes(runtime, "w")));
                         
                         // re-register, since fileno points at something new now
                         registerDescriptor(newFD2);
                     }
                 }
                 
                 // TODO: restore binary mode
     //            if (fptr->mode & FMODE_BINMODE) {
     //                rb_io_binmode(io);
     //            }
                 
                 // TODO: set our metaclass to target's class (i.e. scary!)
 
             } catch (IOException ex) { // TODO: better error handling
                 throw runtime.newIOError("could not reopen: " + ex.getMessage());
             } catch (BadDescriptorException ex) {
                 throw runtime.newIOError("could not reopen: " + ex.getMessage());
             } catch (PipeException ex) {
                 throw runtime.newIOError("could not reopen: " + ex.getMessage());
             }
         } else {
             IRubyObject pathString = args[0].convertToString();
             
             // TODO: check safe, taint on incoming string
             
             if (openFile == null) {
                 openFile = new OpenFile();
             }
             
             try {
                 ModeFlags modes;
                 if (args.length > 1) {
                     IRubyObject modeString = args[1].convertToString();
                     modes = getIOModes(runtime, modeString.toString());
 
                     openFile.setMode(modes.getOpenFileFlags());
                 } else {
                     modes = getIOModes(runtime, "r");
                 }
 
                 String path = pathString.toString();
                 
                 // Ruby code frequently uses a platform check to choose "NUL:" on windows
                 // but since that check doesn't work well on JRuby, we help it out
                 
                 openFile.setPath(path);
             
                 if (openFile.getMainStream() == null) {
                     try {
                         openFile.setMainStream(ChannelStream.fopen(runtime, path, modes));
                     } catch (FileExistsException fee) {
                         throw runtime.newErrnoEEXISTError(path);
                     }
                     
                     registerDescriptor(openFile.getMainStream().getDescriptor());
                     if (openFile.getPipeStream() != null) {
                         openFile.getPipeStream().fclose();
                         unregisterDescriptor(openFile.getPipeStream().getDescriptor().getFileno());
                         openFile.setPipeStream(null);
                     }
                     return this;
                 } else {
                     // TODO: This is an freopen in MRI, this is close, but not quite the same
                     openFile.getMainStream().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
 
                     // re-register
                     registerDescriptor(openFile.getMainStream().getDescriptor());
 
                     if (openFile.getPipeStream() != null) {
                         // TODO: pipe handler to be reopened with path and "w" mode
                     }
                 }
             } catch (PipeException pe) {
                 throw runtime.newErrnoEPIPEError();
             } catch (IOException ex) {
                 throw runtime.newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw runtime.newErrnoEBADFError();
             } catch (InvalidValueException e) {
             	throw runtime.newErrnoEINVALError();
             }
         }
         
         // A potentially previously close IO is being 'reopened'.
         return this;
     }
     
     public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
         return new ModeFlags(getIOModesIntFromString(runtime, modesString));
     }
         
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         int modes = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw runtime.newArgumentError("illegal access mode");
         }
 
         switch (modesString.charAt(0)) {
         case 'r' :
             modes |= ModeFlags.RDONLY;
             break;
         case 'a' :
             modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
             break;
         case 'w' :
             modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
             break;
         default :
             throw runtime.newArgumentError("illegal access mode " + modes);
         }
 
         ModifierLoop: for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             case 't' :
                 // FIXME: add text mode to mode flags
                 break;
             case ':':
                 break ModifierLoop;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modes);
             }
         }
 
         return modes;
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private static ByteList separator(Ruby runtime) {
         return separator(runtime.getRecordSeparatorVar().get());
     }
 
     private static ByteList separator(IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null && separator.realSize == 0) separator = Stream.PARAGRAPH_DELIMETER;
 
         return separator;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 IRubyObject str = readAll(null);
                 if (((RubyString)str).getByteList().length() == 0) {
                     return runtime.getNil();
                 }
                 incrementLineno(runtime, myOpenFile);
                 return str;
             } else if (limit == 0) {
                 return RubyString.newEmptyString(runtime);
             } else if (separator.length() == 1 && limit < 0) {
                 return getlineFast(runtime, separator.get(0), cache);
             } else {
                 Stream readStream = myOpenFile.getMainStream();
                 int c = -1;
                 int n = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     boolean update = false;
                     boolean limitReached = false;
 
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             }
 
                             if (n == -1) {
                                 if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
                                     checkDescriptor(runtime, ((ChannelStream) readStream).getDescriptor());
                                     continue;
                                 } else {
                                     break;
                                 }
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) break;
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.unsafeBytes(), buf.begin + buf.realSize - separator.length(), separator.unsafeBytes(), separator.begin, separator.realSize)) {
                             break;
                         }
                     }
                     
                     if (isParagraph && c != -1) swallow('\n');
                     
                     if (!update) {
                         return runtime.getNil();
                     } else {
                         incrementLineno(runtime, myOpenFile);
                         RubyString str = RubyString.newString(runtime, cache != null ? new ByteList(buf) : buf);
                         str.setTaint(true);
 
                         return str;
                     }
                 }
                 finally {
                     if(cache != null) {
                         cache.release(buf);
                     }
                 }
             }
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
-    static { String v = System.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
+    static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     public IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 }
 
                 if (n == -1) {
                     if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
                         checkDescriptor(runtime, ((ChannelStream)readStream).getDescriptor());
                         continue;
                     } else {
                         break;
                     }
                 }
 
                 update = true;
             } while (c != delim);
 
             if (!update) {
                 return runtime.getNil();
             } else {
                 incrementLineno(runtime, openFile);
                 RubyString str = RubyString.newString(runtime, cache != null ? new ByteList(buf) : buf);
                 str.setTaint(true);
                 return str;
             }
         }
         finally {
             if(cache != null) {
                 cache.release(buf);
             }
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(int fileno, ModeFlags modes) {
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
 
             if (descriptor == null) throw getRuntime().newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (modes == null) modes = descriptor.getOriginalModes();
 
             openFile.setMode(modes.getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes;
         if (second instanceof RubyHash) {
             modes = parseOptions(context, second, null);
         } else {
             modes = parseModes19(context, second);
         }
 
         return initializeCommon19(fileno, modes);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes = parseModes19(context, modeValue);
 
         modes = parseOptions(context, options, modes);
         return initializeCommon19(fileno, modes);
     }
 
     protected ModeFlags parseModes(IRubyObject arg) {
         try {
             if (arg instanceof RubyFixnum) return new ModeFlags(RubyFixnum.fix2long(arg));
 
             return getIOModes(getRuntime(), arg.convertToString().toString());
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
 
     protected ModeFlags parseModes19(ThreadContext context, IRubyObject arg) {
         ModeFlags modes = parseModes(arg);
 
         if (arg instanceof RubyString) {
             parseEncodingFromString(context, arg, 1);
         }
 
         return modes;
     }
 
     private void parseEncodingFromString(ThreadContext context, IRubyObject arg, int initialPosition) {
         RubyString modes19 = arg.convertToString();
         if (modes19.toString().contains(":")) {
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(context.getRuntime(), ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             IRubyObject internalEncodingOption = null;
             if (fullEncoding.length > (initialPosition + 1)) {
                 internalEncodingOption = fullEncoding[initialPosition + 1];
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ? externalEncoding : RubyEncoding.getDefaultExternal(context.getRuntime());
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ? internalEncoding : context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         IRubyObject internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption.toString().equals(external_encoding(context).toString())) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private IRubyObject getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         IRubyObject rubyEncoding = null;
         if (encoding instanceof RubyEncoding) {
             rubyEncoding = encoding;
         } else {
             Encoding encodingObj = RubyEncoding.getEncodingFromObject(context.getRuntime(), encoding);
             rubyEncoding = RubyEncoding.convertEncodingToRubyEncoding(context.getRuntime(), encodingObj);            
         }
         return rubyEncoding;
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         IRubyObject pathString = args[0].convertToString();
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
 
         ModeFlags modes = null;
         int perms = -1; // -1 == don't set permissions
         try {
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
             } else {
                 modes = getIOModes(runtime, "r");
             }
             if (args.length > 2) {
                 RubyInteger permsInt =
                     args.length >= 3 ? args[2].convertToInteger() : null;
                 perms = RubyNumeric.fix2int(permsInt);
             }
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes, perms, runtime.getPosix());
             runtime.registerDescriptor(descriptor,true); // isRetained=true
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
     // This appears to be some windows-only mode.  On a java platform this is a no-op
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) {
             throw getRuntime().newIOError("closed stream");
         }
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
     
     /** @deprecated will be removed in 1.2 */
     protected void checkInitialized() {
         if (openFile == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
     
     /** @deprecated will be removed in 1.2 */
     protected void checkClosed() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             throw getRuntime().newIOError("closed stream");
         }
     }
     
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(runtime);
             }
             
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newSystemCallError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         // MRI behavior: always check whether the file is writable
         // or not, even if we are to write 0 bytes.
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.getRuntime());
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.getRuntime().newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
             int written = myOpenFile.getWriteStream().getDescriptor().write(str.getByteList());
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         }  catch (PipeException ex) {
             throw context.getRuntime().newErrnoEPIPEError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         runtime.secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return runtime.newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
 
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return runtime.newFixnum(written);
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         }
     }
 
     protected boolean waitWritable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
                 selector = Selector.open();
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & SelectionKey.OP_WRITE;
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0);
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
     }
 
     /*
      * Throw bad file descriptor is we can not read on supplied descriptor.
      */
     private void checkDescriptor(Ruby runtime, ChannelDescriptor descriptor) throws IOException {
         if (!(waitReadable(descriptor))) throw runtime.newIOError("bad file descriptor: " + openFile.getPath());
     }
 
     protected boolean waitReadable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
                 selector = Selector.open();
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0);
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
     }
     
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
         if ((n = len) <= 0) return n;
 
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                
                 while(offset<len) {
                     l = n;
 
                     // TODO: Something about pipe buffer length here
 
                     r = writeStream.getDescriptor().write(buffer,offset,l);
 
                     if(r == len) {
                         return len; //Everything written
                     }
 
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         eagain = true;
                     }
 
                     if(eagain && waitWritable(writeStream.getDescriptor())) {
                         openFile.checkClosed(getRuntime());
                         if(offset >= buffer.length()) {
                             return -1;
                         }
                         eagain = false;
                     } else {
                         return -1;
                     }
                 }
 
 
                 // TODO: all this stuff...some pipe logic, some async thread stuff
     //          retry:
     //            l = n;
     //            if (PIPE_BUF < l &&
     //                !rb_thread_critical &&
     //                !rb_thread_alone() &&
     //                wsplit_p(fptr)) {
     //                l = PIPE_BUF;
     //            }
     //            TRAP_BEG;
     //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
     //            TRAP_END;
     //            if (r == n) return len;
     //            if (0 <= r) {
     //                offset += r;
     //                n -= r;
     //                errno = EAGAIN;
     //            }
     //            if (rb_io_wait_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //                if (offset < RSTRING(str)->len)
     //                    goto retry;
     //            }
     //            return -1L;
             }
 
             // TODO: handle errors in buffered write by retrying until finished or file is closed
             return writeStream.fwrite(buffer);
     //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
     //            if (ferror(f)
     //            ) {
     //                if (rb_io_wait_writable(fileno(f))) {
     //                    rb_io_check_closed(fptr);
     //                    clearerr(f);
     //                    if (offset < RSTRING(str)->len)
     //                        continue;
     //                }
     //                return -1L;
     //            }
     //        }
 
 //            return len - n;
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_append(ThreadContext context, IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno(ThreadContext context) {
         return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno(ThreadContext context) {
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
         return context.getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return context.getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
         
         return context.getRuntime().newFixnum(pid); 
     }
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /** Print some objects to the stream.
      *
      */
     public static IRubyObject print(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.getRuntime()) };
         }
 
         Ruby runtime = context.getRuntime();
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
 
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 maybeIO.callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 maybeIO.callMethod(context, "write", runtime.newString("nil"));
             } else {
                 maybeIO.callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             maybeIO.callMethod(context, "write", rs);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(context, this, args));
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         return putc(context, this, object);
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
         if (maybeIO instanceof RubyIO) {
             // FIXME we should probably still be dyncalling 'write' here
             RubyIO io = (RubyIO)maybeIO;
             try {
                 OpenFile myOpenFile = io.getOpenFileChecked();
                 myOpenFile.checkWritable(context.getRuntime());
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(RubyNumeric.num2chr(object));
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.getRuntime().newErrnoEINVALError();
             } catch (PipeException ex) {
                 throw context.getRuntime().newErrnoEPIPEError();
             }
         } else {
             maybeIO.callMethod(context, "write", RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = Stream.SEEK_SET;
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = RubyNumeric.fix2int(arg1.convertToInteger());
         
         return doSeek(context, offset, whence);
     }
     
     private RubyFixnum doSeek(ThreadContext context, long offset, int whence) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw context.getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index bc2d7d3855..93005e77fb 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,1387 +1,1387 @@
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
  * Copyright (C) 2007-2009 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2009 Joseph LaFata <joe@quibb.org>
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
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTCompiler19;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.Opcodes;
 
 public class RubyInstanceConfig {
 
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     private static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     private static final int JIT_MAX_SIZE_LIMIT = 10000;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     private static final int JIT_THRESHOLD = 50;
     
     /** The version to use for generated classes. Set to current JVM version by default */
     public static final int JAVA_VERSION;
     
     /**
      * Default size for chained compilation.
      */
     private static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = 500;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT
             = SafePropertyAccessor.getInt("jruby.compile.chainsize", CHAINED_COMPILE_LINE_COUNT_DEFAULT);
 
     public enum CompileMode {
         JIT, FORCE, OFF;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldJIT() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled
             = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
     private Map environment;
     private String[] argv = {};
 
     private final boolean jitLogging;
     private final boolean jitLoggingVerbose;
     private final int jitLogEvery;
     private final int jitThreshold;
     private final int jitMax;
     private final int jitMaxSize;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
 
     private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
     private ClassLoader loader = contextLoader == null ? RubyInstanceConfig.class.getClassLoader() : contextLoader;
 
     private ClassCache<Script> classCache;
 
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private Set<String> excludedMethods = new HashSet<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private List<String> requiredLibraries = new ArrayList<String>();
     private boolean benchmarking = false;
     private boolean argvGlobalsOn = false;
     private boolean assumeLoop = false;
     private boolean assumePrinting = false;
     private Map optionGlobals = new HashMap();
     private boolean processLineEnds = false;
     private boolean split = false;
     // This property is a Boolean, to allow three values, so it can match MRI's nil, false and true
     private Boolean verbose = Boolean.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = true;
     private String inPlaceBackupExtension = null;
     private boolean parserDebug = false;
     private String threadDumpSignal = null;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean PEEPHOLE_OPTZ
             = SafePropertyAccessor.getBoolean("jruby.compile.peephole", true);
     public static boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
     public static boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops");
     public static boolean FRAMELESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static boolean POSITIONLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless");
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static boolean FASTCASE_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastcase");
     public static boolean FASTSEND_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastsend");
     public static boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
     public static boolean INLINE_DYNCALL_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.inlineDyncalls");
     public static final boolean FORK_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.fork.enabled");
     public static final boolean POOLING_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.thread.pool.enabled");
     public static final int POOL_MAX
             = SafePropertyAccessor.getInt("jruby.thread.pool.max", Integer.MAX_VALUE);
     public static final int POOL_MIN
             = SafePropertyAccessor.getInt("jruby.thread.pool.min", 0);
     public static final int POOL_TTL
             = SafePropertyAccessor.getInt("jruby.thread.pool.ttl", 60);
 
     public static final boolean NATIVE_NET_PROTOCOL
             = SafePropertyAccessor.getBoolean("jruby.native.net.protocol", false);
 
     public static boolean FULL_TRACE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
 
     public static final String COMPILE_EXCLUDE
             = SafePropertyAccessor.getProperty("jruby.jit.exclude");
     public static boolean nativeEnabled = true;
 
     public static final boolean REIFY_RUBY_CLASSES
             = SafePropertyAccessor.getBoolean("jruby.reify.classes", false);
 
     public static final boolean USE_GENERATED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.java.handles", false);
 
     public static final boolean DEBUG_LOAD_SERVICE
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService", false);
 
     public static final boolean DEBUG_LOAD_TIMINGS
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService.timing", false);
 
     public static final boolean DEBUG_LAUNCHING
             = SafePropertyAccessor.getBoolean("jruby.debug.launch", false);
 
     public static final boolean JUMPS_HAVE_BACKTRACE
             = SafePropertyAccessor.getBoolean("jruby.jump.backtrace", false);
 
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
                 public LoadService create(Ruby runtime) {
                     return new LoadService(runtime);
                 }
             };
     }
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
 
 
     static {
         String specVersion = null;
         try {
             specVersion = System.getProperty("jruby.bytecode.version");
             if (specVersion == null) {
                 specVersion = System.getProperty("java.specification.version");
             }
             if (System.getProperty("jruby.native.enabled") != null) {
                 nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
             }
         } catch (SecurityException se) {
             nativeEnabled = false;
             specVersion = "1.5";
         }
         
         if (specVersion.equals("1.5")) {
             JAVA_VERSION = Opcodes.V1_5;
         } else {
             JAVA_VERSION = Opcodes.V1_6;
         }
     }
 
     public int characterIndex = 0;
 
     public RubyInstanceConfig(RubyInstanceConfig parentConfig) {
         setCurrentDirectory(parentConfig.getCurrentDirectory());
         samplingEnabled = parentConfig.samplingEnabled;
         compatVersion = parentConfig.compatVersion;
         compileMode = parentConfig.getCompileMode();
         jitLogging = parentConfig.jitLogging;
         jitLoggingVerbose = parentConfig.jitLoggingVerbose;
         jitLogEvery = parentConfig.jitLogEvery;
         jitThreshold = parentConfig.jitThreshold;
         jitMax = parentConfig.jitMax;
         jitMaxSize = parentConfig.jitMaxSize;
         managementEnabled = parentConfig.managementEnabled;
         runRubyInProcess = parentConfig.runRubyInProcess;
         excludedMethods = parentConfig.excludedMethods;
         threadDumpSignal = parentConfig.threadDumpSignal;
         
         classCache = new ClassCache<Script>(loader, jitMax);
     }
 
     public RubyInstanceConfig() {
         setCurrentDirectory(Ruby.isSecurityRestricted() ? "/" : JRubyFile.getFileProperty("user.dir"));
         samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
 
         String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             setCompatVersion(CompatVersion.RUBY1_8);
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             setCompatVersion(CompatVersion.RUBY1_9);
         } else {
             error.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             setCompatVersion(CompatVersion.RUBY1_8);
         }
 
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
             jitLoggingVerbose = false;
             jitLogEvery = 0;
             jitThreshold = -1;
             jitMax = 0;
             jitMaxSize = -1;
             managementEnabled = false;
         } else {
             String threshold = SafePropertyAccessor.getProperty("jruby.jit.threshold");
             String max = SafePropertyAccessor.getProperty("jruby.jit.max");
             String maxSize = SafePropertyAccessor.getProperty("jruby.jit.maxsize");
             
             if (COMPILE_EXCLUDE != null) {
                 String[] elements = COMPILE_EXCLUDE.split(",");
                 for (String element : elements) excludedMethods.add(element);
             }
             
             managementEnabled = SafePropertyAccessor.getBoolean("jruby.management.enabled", true);
             runRubyInProcess = SafePropertyAccessor.getBoolean("jruby.launch.inproc", true);
             boolean jitProperty = SafePropertyAccessor.getProperty("jruby.jit.enabled") != null;
             if (jitProperty) {
                 error.print("jruby.jit.enabled property is deprecated; use jruby.compile.mode=(OFF|JIT|FORCE) for -C, default, and +C flags");
                 compileMode = SafePropertyAccessor.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
             } else {
                 String jitModeProperty = SafePropertyAccessor.getProperty("jruby.compile.mode", "JIT");
 
                 if (jitModeProperty.equals("OFF")) {
                     compileMode = CompileMode.OFF;
                 } else if (jitModeProperty.equals("JIT")) {
                     compileMode = CompileMode.JIT;
                 } else if (jitModeProperty.equals("FORCE")) {
                     compileMode = CompileMode.FORCE;
                 } else {
                     error.print("jruby.compile.mode property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                     compileMode = CompileMode.JIT;
                 }
             }
             jitLogging = SafePropertyAccessor.getBoolean("jruby.jit.logging");
             jitLoggingVerbose = SafePropertyAccessor.getBoolean("jruby.jit.logging.verbose");
             String logEvery = SafePropertyAccessor.getProperty("jruby.jit.logEvery");
             jitLogEvery = logEvery == null ? 0 : Integer.parseInt(logEvery);
             jitThreshold = threshold == null ?
                     JIT_THRESHOLD : Integer.parseInt(threshold);
             jitMax = max == null ?
                     JIT_MAX_METHODS_LIMIT : Integer.parseInt(max);
             jitMaxSize = maxSize == null ?
                     JIT_MAX_SIZE_LIMIT : Integer.parseInt(maxSize);
         }
 
         // default ClassCache using jitMax as a soft upper bound
         classCache = new ClassCache<Script>(loader, jitMax);
         threadDumpSignal = SafePropertyAccessor.getProperty("jruby.thread.dump.signal", "USR2");
 
         if (FORK_ENABLED) {
             error.print("WARNING: fork is highly unlikely to be safe or stable on the JVM. Have fun!\n");
         }
     }
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return this.creator.create(runtime);
     }
 
     public String getBasicUsageHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("Usage: jruby [switches] [--] [programfile] [arguments]\n")
                 .append("  -0[octal]       specify record separator (\\0, if no argument)\n")
                 .append("  -a              autosplit mode with -n or -p (splits $_ into $F)\n")
                 .append("  -b              benchmark mode, times the script execution\n")
                 .append("  -c              check syntax only\n")
                 .append("  -Cdirectory     cd to directory, before executing your script\n")
                 .append("  -d              set debugging flags (set $DEBUG to true)\n")
                 .append("  -e 'command'    one line of script. Several -e's allowed. Omit [programfile]\n")
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 .append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode, -Ke for EUC and -Ks for SJIS)\n")
                 .append("  -l              enable line ending processing\n")
                 .append("  -n              assume 'while gets(); ... end' loop around your script\n")
                 .append("  -p              assume loop like -n but print line also like sed\n")
                 .append("  -rlibrary       require the library, before executing your script\n")
                 .append("  -s              enable some switch parsing for switches after script name\n")
                 .append("  -S              look for the script in bin or using PATH environment variable\n")
                 .append("  -T[level]       turn on tainting checks\n")
                 .append("  -v              print version number, then turn on verbose mode\n")
                 .append("  -w              turn warnings on for your script\n")
                 .append("  -W[level]       set warning level; 0=silence, 1=medium, 2=verbose (default)\n")
                 //.append("  -x[directory]   strip off text before #!ruby line and perhaps cd to directory\n")
                 .append("  -X[option]      enable extended option (omit option to list)\n")
                 .append("  -y              enable parsing debug output\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties (pass -J-Dproperty=value)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --client        use the non-optimizing \"client\" JVM (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM and JRuby\n")
                 .append("  --headless      do not launch a GUI window, no matter what\n")
                 .append("  --1.8           specify Ruby 1.8.x compatibility (default)\n")
                 .append("  --1.9           specify Ruby 1.9.x compatibility\n")
                 .append("  --bytecode      show the JVM bytecode produced by compiling specified code\n")
                 .append("  --version       print the version\n");
 
         return sb.toString();
     }
 
     public String getExtendedHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These flags are for extended JRuby options.\n")
                 .append("Specify them by passing -X<option>\n")
                 .append("  -O              run with ObjectSpace disabled (default; improves performance)\n")
                 .append("  +O              run with ObjectSpace enabled (reduces performance)\n")
                 .append("  -C              disable all compilation\n")
                 .append("  +C              force compilation of all scripts before they are run (except eval)\n");
 
         return sb.toString();
     }
 
     public String getPropertyHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.\n")
                 .append("Specify them by passing -J-D<property>=<value>\n")
                 .append("\nCOMPILER SETTINGS:\n")
                 .append("    jruby.compile.mode=JIT|FORCE|OFF\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables\n")
                 .append("    jruby.compile.fastest=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on all experimental compiler optimizations\n")
                 .append("    jruby.compile.frameless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible\n")
                 .append("    jruby.compile.positionless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation that avoids updating Ruby position info. Default is false\n")
                 .append("    jruby.compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    jruby.compile.fastops=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast operators for Fixnum. Default is false\n")
                 .append("    jruby.compile.fastcase=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast case/when for all-Fixnum whens. Default is false\n")
                 .append("    jruby.compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is " + CHAINED_COMPILE_LINE_COUNT_DEFAULT + "\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.\n")
                 .append("    jruby.compile.peephole=true|false\n")
                 .append("       Enable or disable peephole optimizations. Default is true (on).\n")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jruby.jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is " + JIT_THRESHOLD + ".\n")
                 .append("    jruby.jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is " + JIT_MAX_METHODS_LIMIT + " per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jruby.jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is Integer.MAX_VALUE\n")
                 .append("    jruby.jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jruby.jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jruby.jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("    jruby.jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    jruby.native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    jruby.native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("    jruby.fork.enabled=true|false\n")
                 .append("       (EXPERIMENTAL, maybe dangerous) Enable fork(2) on platforms that support it.\n")
                 .append("\nTHREAD POOLING:\n")
                 .append("    jruby.thread.pool.enabled=true|false\n")
                 .append("       Enable reuse of native backing threads via a thread pool. Default is false.\n")
                 .append("    jruby.thread.pool.min=<min thread count>\n")
                 .append("       The minimum number of threads to keep alive in the pool. Default is 0.\n")
                 .append("    jruby.thread.pool.max=<max thread count>\n")
                 .append("       The maximum number of threads to allow in the pool. Default is unlimited.\n")
                 .append("    jruby.thread.pool.ttl=<time to live, in seconds>\n")
                 .append("       The maximum number of seconds to keep alive an idle thread. Default is 60.\n")
                 .append("\nMISCELLANY:\n")
                 .append("    jruby.compat.version=RUBY1_8|RUBY1_9\n")
                 .append("       Specify the major Ruby version to be compatible with; Default is RUBY1_8\n")
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is true.\n")
                 .append("    jruby.jump.backtrace=true|false\n")
                 .append("       Make non-local flow jumps generate backtraces. Default is false.\n")
                 .append("\nDEBUGGING/LOGGING:\n")
                 .append("    jruby.debug.loadService=true|false\n")
                 .append("       LoadService logging\n")
                 .append("    jruby.debug.loadService.timing=true|false\n")
                 .append("       Print load timings for each require'd library. Default is false.\n")
                 .append("    jruby.debug.launch=true|false\n")
                 .append("       ShellLauncher logging\n")
                 .append("    jruby.debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = null;
         String patchDelimeter = null;
         int patchlevel = 0;
         switch (getCompatVersion()) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             patchlevel = Constants.RUBY_PATCHLEVEL;
             patchDelimeter = " patchlevel ";
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             patchlevel = Constants.RUBY1_9_PATCHLEVEL;
             patchDelimeter = " trunk ";
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s%s%d) (%s %s) (%s %s) [%s-java]",
                 Constants.VERSION, ver, patchDelimeter, patchlevel,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 System.getProperty("java.vm.name"), System.getProperty("java.version"),
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2009 The JRuby Community (and contribs)";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
         try {
             String rubyopt = System.getenv("RUBYOPT");
             if (rubyopt != null) {
                 String[] rubyoptArgs = rubyopt.split("\\s+");
                 for (int i = 0; i < rubyoptArgs.length; i++) {
                     if (!rubyoptArgs[i].startsWith("-")) {
                         rubyoptArgs[i] = "-" + rubyoptArgs[i];
                     }
                 }
                 new ArgumentProcessor(rubyoptArgs, false).processArguments();
             }
         } catch (SecurityException se) {
             // ignore and do nothing
         }
     }
 
     public CompileMode getCompileMode() {
         return compileMode;
     }
 
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public int getJitLogEvery() {
         return jitLogEvery;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
 
     public int getJitThreshold() {
         return jitThreshold;
     }
 
     public int getJitMax() {
         return jitMax;
     }
 
     public int getJitMaxSize() {
         return jitMaxSize;
     }
 
     public boolean isRunRubyInProcess() {
         return runRubyInProcess;
     }
 
     public void setRunRubyInProcess(boolean flag) {
         this.runRubyInProcess = flag;
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public CompatVersion getCompatVersion() {
         return compatVersion;
     }
 
     public void setCompatVersion(CompatVersion compatVersion) {
         // Until we get a little more solid on 1.9 support we will only run interpreted mode
         if (compatVersion == CompatVersion.RUBY1_9) compileMode = CompileMode.OFF;
         if (compatVersion == null) compatVersion = CompatVersion.RUBY1_8;
 
         this.compatVersion = compatVersion;
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
 
     public ClassLoader getLoader() {
         return loader;
     }
 
     public void setLoader(ClassLoader loader) {
         // Setting the loader needs to reset the class cache
         if(this.loader != loader) {
             this.classCache = new ClassCache<Script>(loader, this.classCache.getMax());
         }
         this.loader = loader;
     }
 
     public String[] getArgv() {
         return argv;
     }
 
     public void setArgv(String[] argv) {
         this.argv = argv;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             // try the normal property first
             if (!Ruby.isSecurityRestricted()) {
                 jrubyHome = SafePropertyAccessor.getProperty("jruby.home");
             }
 
             if (jrubyHome != null) {
                 // verify it if it's there
                 jrubyHome = verifyHome(jrubyHome);
             } else {
                 try {
                     // try loading from classloader resources
                     jrubyHome = getClass().getResource("/META-INF/jruby.home")
                         .toURI().getSchemeSpecificPart();
                 } catch (Exception e) {}
 
                 if (jrubyHome != null) {
                     // verify it if it's there
                     jrubyHome = verifyHome(jrubyHome);
                 } else {
                     // otherwise fall back on system temp location
-                    jrubyHome = System.getProperty("java.io.tmpdir");
+                    jrubyHome = SafePropertyAccessor.getProperty("java.io.tmpdir");
                 }
             }
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
-            home = System.getProperty("user.dir");
+            home = SafePropertyAccessor.getProperty("user.dir");
         }
         if (home.startsWith("cp:")) {
             home = home.substring(3);
         } else if (!home.startsWith("file:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             if (!f.exists()) {
-                System.err.println("Warning: JRuby home \"" + f + "\" does not exist, using " + System.getProperty("java.io.tmpdir"));
+                System.err.println("Warning: JRuby home \"" + f + "\" does not exist, using " + SafePropertyAccessor.getProperty("java.io.tmpdir"));
                 return System.getProperty("java.io.tmpdir");
             }
         }
         return home;
     }
 
     private class ArgumentProcessor {
         private String[] arguments;
         private int argumentIndex = 0;
         private boolean processArgv;
 
         public ArgumentProcessor(String[] arguments) {
             this(arguments, true);
         }
 
         public ArgumentProcessor(String[] arguments, boolean processArgv) {
             this.arguments = arguments;
             this.processArgv = processArgv;
         }
 
         public void processArguments() {
             while (argumentIndex < arguments.length && isInterpreterArgument(arguments[argumentIndex])) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript && scriptFileName == null) {
                 if (argumentIndex < arguments.length) {
                     setScriptFileName(arguments[argumentIndex]); //consume the file name
                     argumentIndex++;
                 }
             }
 
             if (processArgv) processArgv();
         }
 
         private void processArgv() {
             List<String> arglist = new ArrayList<String>();
             for (; argumentIndex < arguments.length; argumentIndex++) {
                 String arg = arguments[argumentIndex];
                 if (argvGlobalsOn && arg.startsWith("-")) {
                     arg = arg.substring(1);
                     if (arg.indexOf('=') > 0) {
                         String[] keyvalue = arg.split("=", 2);
                         optionGlobals.put(keyvalue[0], keyvalue[1]);
                     } else {
                         optionGlobals.put(arg, null);
                     }
                 } else {
                     argvGlobalsOn = false;
                     arglist.add(arg);
                 }
             }
 
             // Remaining arguments are for the script itself
             for (String arg : argv) arglist.add(arg);
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return argument.length() > 0 && (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private String getArgumentError(String additionalError) {
             return "jruby: invalid argument\n" + additionalError + "\n";
         }
 
         private void processArgument() {
             String argument = arguments[argumentIndex];
             FOR : for (characterIndex = 1; characterIndex < argument.length(); characterIndex++) {
                 switch (argument.charAt(characterIndex)) {
                 case '0': {
                     String temp = grabOptionalValue();
                     if (null == temp) {
                         recordSeparator = "\u0000";
                     } else if (temp.equals("0")) {
                         recordSeparator = "\n\n";
                     } else if (temp.equals("777")) {
                         recordSeparator = "\uFFFF"; // Specify something that can't separate
                     } else {
                         try {
                             int val = Integer.parseInt(temp, 8);
                             recordSeparator = "" + (char) val;
                         } catch (Exception e) {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -0 must be followed by either 0, 777, or a valid octal value"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     break FOR;
                 }
                 case 'a':
                     split = true;
                     break;
                 case 'b':
                     benchmarking = true;
                     break;
                 case 'c':
                     shouldCheckSyntax = true;
                     break;
                 case 'C':
                     try {
                         String saved = grabValue(getArgumentError(" -C must be followed by a directory expression"));
                         File base = new File(currentDirectory);
                         File newDir = new File(saved);
                         if (newDir.isAbsolute()) {
                             currentDirectory = newDir.getCanonicalPath();
                         } else {
                             currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                         }
                         if (!(new File(currentDirectory).isDirectory())) {
                             MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                             throw mee;
                         }
                     } catch (IOException e) {
                         MainExitException mee = new MainExitException(1, getArgumentError(" -C must be followed by a valid directory"));
                         throw mee;
                     }
                     break FOR;
                 case 'd':
                     debug = true;
                     verbose = Boolean.TRUE;
                     break;
                 case 'e':
                     inlineScript.append(grabValue(getArgumentError(" -e must be followed by an expression to evaluate")));
                     inlineScript.append('\n');
                     hasInlineScript = true;
                     break FOR;
                 case 'F':
                     inputFieldSeparator = grabValue(getArgumentError(" -F must be followed by a pattern for input field separation"));
                     break;
                 case 'h':
                     shouldPrintUsage = true;
                     shouldRunInterpreter = false;
                     break;
                 case 'i' :
                     inPlaceBackupExtension = grabOptionalValue();
                     if(inPlaceBackupExtension == null) inPlaceBackupExtension = "";
                     break FOR;
                 case 'I':
                     String s = grabValue(getArgumentError("-I must be followed by a directory name to add to lib path"));
                     String[] ls = s.split(java.io.File.pathSeparator);
                     for (int i = 0; i < ls.length; i++) {
                         loadPaths.add(ls[i]);
                     }
                     break FOR;
                 case 'K':
                     // FIXME: No argument seems to work for -K in MRI plus this should not
                     // siphon off additional args 'jruby -K ~/scripts/foo'.  Also better error
                     // processing.
                     String eArg = grabValue(getArgumentError("provide a value for -K"));
                     kcode = KCode.create(null, eArg);
                     break;
                 case 'l':
                     processLineEnds = true;
                     break;
                 case 'n':
                     assumeLoop = true;
                     break;
                 case 'p':
                     assumePrinting = true;
                     assumeLoop = true;
                     break;
                 case 'r':
                     requiredLibraries.add(grabValue(getArgumentError("-r must be followed by a package to require")));
                     break FOR;
                 case 's' :
                     argvGlobalsOn = true;
                     break;
                 case 'S':
                     runBinScript();
                     break FOR;
                 case 'T' :{
                     String temp = grabOptionalValue();
                     int value = 1;
 
                     if(temp!=null) {
                         try {
                             value = Integer.parseInt(temp, 8);
                         } catch(Exception e) {
                             value = 1;
                         }
                     }
 
                     safeLevel = value;
 
                     break FOR;
                 }
                 case 'v':
                     verbose = Boolean.TRUE;
                     setShowVersion(true);
                     break;
                 case 'w':
                     verbose = Boolean.TRUE;
                     break;
                 case 'W': {
                     String temp = grabOptionalValue();
                     int value = 2;
                     if (null != temp) {
                         if (temp.equals("2")) {
                             value = 2;
                         } else if (temp.equals("1")) {
                             value = 1;
                         } else if (temp.equals("0")) {
                             value = 0;
                         } else {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -W must be followed by either 0, 1, 2 or nothing"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     switch (value) {
                     case 0:
                         verbose = null;
                         break;
                     case 1:
                         verbose = Boolean.FALSE;
                         break;
                     case 2:
                         verbose = Boolean.TRUE;
                         break;
                     }
 
 
                     break FOR;
                 }
                 // FIXME: -x flag not supported
 //                    case 'x' :
 //                        break;
                 case 'X':
                     String extendedOption = grabOptionalValue();
 
                     if (extendedOption == null) {
                         throw new MainExitException(0, "jruby: missing extended option, listing available options\n" + getExtendedHelp());
                     } else if (extendedOption.equals("-O")) {
                         objectSpaceEnabled = false;
                     } else if (extendedOption.equals("+O")) {
                         objectSpaceEnabled = true;
                     } else if (extendedOption.equals("-C")) {
                         compileMode = CompileMode.OFF;
                     } else if (extendedOption.equals("+C")) {
                         compileMode = CompileMode.FORCE;
                     } else {
                         MainExitException mee =
                                 new MainExitException(1, "jruby: invalid extended option " + extendedOption + " (-X will list valid options)\n");
                         mee.setUsageError(true);
 
                         throw mee;
                     }
                     break FOR;
                 case 'y':
                     parserDebug = true;
                     break FOR;
                 case '-':
                     if (argument.equals("--command") || argument.equals("--bin")) {
                         characterIndex = argument.length();
                         runBinScript();
                         break;
                     } else if (argument.equals("--compat")) {
                         characterIndex = argument.length();
                         setCompatVersion(CompatVersion.getVersionFromString(grabValue(getArgumentError("--compat must be RUBY1_8 or RUBY1_9"))));
                         break FOR;
                     } else if (argument.equals("--copyright")) {
                         setShowCopyright(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--debug")) {
                         FULL_TRACE_ENABLED = true;
                         break FOR;
                     } else if (argument.equals("--jdb")) {
                         debug = true;
                         verbose = Boolean.TRUE;
                         break;
                     } else if (argument.equals("--help")) {
                         shouldPrintUsage = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--properties")) {
                         shouldPrintProperties = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--version")) {
                         setShowVersion(true);
                         break FOR;
                     } else if (argument.equals("--bytecode")) {
                         setShowBytecode(true);
                         break FOR;
                     } else if (argument.equals("--fast")) {
                         compileMode = CompileMode.FORCE;
                         FASTEST_COMPILE_ENABLED = true;
                         FASTOPS_COMPILE_ENABLED = true;
                         FRAMELESS_COMPILE_ENABLED = true;
                         POSITIONLESS_COMPILE_ENABLED = true;
                         FASTCASE_COMPILE_ENABLED = true;
                         FASTSEND_COMPILE_ENABLED = true;
                         INLINE_DYNCALL_ENABLED = true;
                         RubyException.TRACE_TYPE = RubyException.RUBY_COMPILED;
                         break FOR;
                     } else if (argument.equals("--1.9")) {
                         setCompatVersion(CompatVersion.RUBY1_9);
                         break FOR;
                     } else if (argument.equals("--1.8")) {
                         setCompatVersion(CompatVersion.RUBY1_8);
                         break FOR;
                     } else {
                         if (argument.equals("--")) {
                             // ruby interpreter compatibilty
                             // Usage: ruby [switches] [--] [programfile] [arguments])
                             endOfArguments = true;
                             break;
                         }
                     }
                 default:
                     throw new MainExitException(1, "jruby: unknown option " + argument);
                 }
             }
         }
 
         private void runBinScript() {
             String scriptName = grabValue("jruby: provide a bin script to execute");
             if (scriptName.equals("irb")) {
                 scriptName = "jirb";
             }
 
             scriptFileName = resolveScript(scriptName);
 
             if (scriptFileName.equals(scriptName)) {
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands." + scriptName);
                 inlineScript.append("\n");
                 hasInlineScript = true;
             }
 
             endOfArguments = true;
         }
 
         private String resolveScript(String scriptName) {
             // This try/catch is to allow failing over to the "commands" logic
             // when running from within a jruby-complete jar file, which has
             // jruby.home = a jar file URL that does not resolve correctly with
             // JRubyFile.create.
             try {
                 // try cwd first
                 File fullName = JRubyFile.create(currentDirectory, scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     return fullName.getAbsolutePath();
                 }
 
                 fullName = JRubyFile.create(getJRubyHome(), "bin/" + scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     return fullName.getAbsolutePath();
                 }
 
                 try {
                     String path = System.getenv("PATH");
                     if (path != null) {
                         String[] paths = path.split(System.getProperty("path.separator"));
                         for (int i = 0; i < paths.length; i++) {
                             fullName = JRubyFile.create(paths[i], scriptName);
                             if (fullName.exists() && fullName.isFile()) {
                                 return fullName.getAbsolutePath();
                             }
                         }
                     }
                 } catch (SecurityException se) {
                     // ignore and do nothing
                 }
             } catch (IllegalArgumentException iae) {
                 if (debug) System.err.println("warning: could not resolve -S script on filesystem: " + scriptName);
             }
             return scriptName;
         }
 
         private String grabValue(String errorMessage) {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             argumentIndex++;
             if (argumentIndex < arguments.length) {
                 return arguments[argumentIndex];
             }
 
             MainExitException mee = new MainExitException(1, errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
 
         private String grabOptionalValue() {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             return null;
         }
     }
 
     public byte[] inlineScript() {
         return inlineScript.toString().getBytes();
     }
 
     public List<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         if(isShowVersion() && (hasInlineScript || scriptFileName != null)) {
             return true;
         }
         return isShouldRunInterpreter();
     }
 
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     public InputStream getScriptSource() {
         try {
             // KCode.NONE is used because KCODE does not affect parse in Ruby 1.8
             // if Ruby 2.0 encoding pragmas are implemented, this will need to change
             if (hasInlineScript) {
                 return new ByteArrayInputStream(inlineScript());
             } else if (isSourceFromStdin()) {
                 // can't use -v and stdin
                 if (isShowVersion()) {
                     return null;
                 }
                 return getInput();
             } else {
                 File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                 return new BufferedInputStream(new FileInputStream(file), 8192);
             }
         } catch (IOException e) {
             // We haven't found any file directly on the file system,
             // now check for files inside the JARs.
             InputStream is = getJarScriptSource();
             if (is != null) {
                 return new BufferedInputStream(is, 8129);
             }
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     private InputStream getJarScriptSource() {
         String name = getScriptFileName();
         boolean looksLikeJarURL = name.startsWith("file:") && name.indexOf("!/") != -1;
         if (!looksLikeJarURL) {
             return null;
         }
 
         String before = name.substring("file:".length(), name.indexOf("!/"));
         String after =  name.substring(name.indexOf("!/") + 2);
 
         try {
             JarFile jFile = new JarFile(before);
             JarEntry entry = jFile.getJarEntry(after);
 
             if (entry != null && !entry.isDirectory()) {
                 return jFile.getInputStream(entry);
             }
         } catch (IOException ignored) {
         }
         return null;
     }
 
     public String displayedFileName() {
         if (hasInlineScript) {
             if (scriptFileName != null) {
                 return scriptFileName;
             } else {
                 return "-e";
             }
         } else if (isSourceFromStdin()) {
             return "-";
         } else {
             return getScriptFileName();
         }
     }
 
     private void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
 
     public boolean isSplit() {
         return split;
     }
 
     public boolean isVerbose() {
         return verbose == Boolean.TRUE;
     }
 
     public Boolean getVerbose() {
         return verbose;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     public boolean isParserDebug() {
         return parserDebug;
     }
 
     public boolean isShowVersion() {
         return showVersion;
     }
     
     public boolean isShowBytecode() {
         return showBytecode;
     }
 
     public boolean isShowCopyright() {
         return showCopyright;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     protected void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     protected void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
 
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     public boolean isShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
 
     public String getInputFieldSeparator() {
         return inputFieldSeparator;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
 
     public int getSafeLevel() {
         return safeLevel;
     }
 
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
 
     public String getInPlaceBackupExtention() {
         return inPlaceBackupExtension;
     }
 
     public void setClassCache(ClassCache classCache) {
         this.classCache = classCache;
     }
 
     public Map getOptionGlobals() {
         return optionGlobals;
     }
     
     public boolean isManagementEnabled() {
         return managementEnabled;
     }
     
     public Set getExcludedMethods() {
         return excludedMethods;
     }
 
     public ASTCompiler newCompiler() {
         if (getCompatVersion() == CompatVersion.RUBY1_8) {
             return new ASTCompiler();
         } else {
             return new ASTCompiler19();
         }
     }
 
     public String getThreadDumpSignal() {
         return threadDumpSignal;
     }
 }
diff --git a/src/org/jruby/ext/Readline.java b/src/org/jruby/ext/Readline.java
index 88b2e32c31..208d109970 100644
--- a/src/org/jruby/ext/Readline.java
+++ b/src/org/jruby/ext/Readline.java
@@ -1,598 +1,616 @@
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
  * Copyright (C) 2006 Damian Steer <pldms@mac.com>
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 package org.jruby.ext;
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.nio.CharBuffer;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 
 import jline.CandidateListCompletionHandler;
 import jline.Completor;
 import jline.ConsoleReader;
 import jline.CursorBuffer;
 import jline.FileNameCompletor;
 import jline.History;
 
 import org.jruby.CompatVersion;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyIO;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @author <a href="mailto:pldms@mac.com">Damian Steer</a>
  * @author <a href="mailto:koichiro@meadowy.org">Koichiro Ohba</a>
  */
 @JRubyModule(name = "Readline")
 public class Readline {
     public static final char ESC_KEY_CODE = (char)27;
     private final static boolean DEBUG = false;
     private static IRubyObject COMPLETION_CASE_FOLD = null;
 
     public static class Service implements Library {
 
         public void load(final Ruby runtime, boolean wrap) throws IOException {
             createReadline(runtime);
         }
     }
 
     public static class ReadlineHistory extends History {
         ArrayList historyList = null;
         Field index = null;
+        private boolean securityRestricted = false;
 
         public ReadlineHistory() {
             try {
                 Field list = History.class.getDeclaredField("history");
                 list.setAccessible(true);
                 historyList = (ArrayList) list.get(this);
                 index = History.class.getDeclaredField("currentIndex");
                 index.setAccessible(true);
             } catch (NoSuchFieldException ex) {
                 ex.printStackTrace();
             } catch (SecurityException ex) {
-                ex.printStackTrace();
+                securityRestricted = true;
             } catch (IllegalArgumentException ex) {
                 ex.printStackTrace();
             } catch (IllegalAccessException ex) {
                 ex.printStackTrace();
             }
         }
 
         public void setCurrentIndex(int i) {
+            if (securityRestricted) {
+                return; // do nothing
+            }
             try {
                 index.setInt(this, i);
             } catch (IllegalArgumentException ex) {
                 ex.printStackTrace();
             } catch (IllegalAccessException ex) {
                 ex.printStackTrace();
             }
         }
 
         public void set(int i, String s) {
+            if (securityRestricted) {
+                return; // do nothing
+            }
             historyList.set(i, s);
         }
 
+        @SuppressWarnings("unchecked")
         public String pop() {
+            if (securityRestricted) {
+                // Not fully implemented in security restricted environment.
+                // We just return the last value, without really popping it.
+                List histList = getHistoryList();
+                return (String)histList.get(histList.size() - 1);
+            }
             return remove(historyList.size() - 1);
         }
 
         public String remove(int i) {
+            if (securityRestricted) {
+                // do nothing, we can't modify the history without
+                // accessing private members of History.
+                return "";
+            }
             setCurrentIndex(historyList.size() - 2);
             return (String)historyList.remove(i);
         }
     }
 
     public static class ConsoleHolder {
-
         public ConsoleReader readline;
         public Completor currentCompletor;
         public ReadlineHistory history;
     }
 
     public static void createReadline(Ruby runtime) throws IOException {
         ConsoleHolder holder = new ConsoleHolder();
         holder.history = new ReadlineHistory();
         holder.currentCompletor = null;
         COMPLETION_CASE_FOLD = runtime.getNil();
 
         RubyModule mReadline = runtime.defineModule("Readline");
 
         mReadline.dataWrapStruct(holder);
 
         mReadline.defineAnnotatedMethods(Readline.class);
         IRubyObject hist = runtime.getObject().callMethod(runtime.getCurrentContext(), "new");
         mReadline.fastSetConstant("HISTORY", hist);
         hist.getSingletonClass().includeModule(runtime.getEnumerable());
         hist.getSingletonClass().defineAnnotatedMethods(HistoryMethods.class);
 
         // MRI does similar thing on MacOS X with 'EditLine wrapper'.
         mReadline.fastSetConstant("VERSION", runtime.newString("JLine wrapper"));
     }
 
     // We lazily initialize this in case Readline.readline has been overridden in ruby (s_readline)
     protected static void initReadline(Ruby runtime, final ConsoleHolder holder) throws IOException {
         holder.readline = new ConsoleReader();
         holder.readline.setUseHistory(false);
         holder.readline.setUsePagination(true);
         holder.readline.setBellEnabled(true);
         ((CandidateListCompletionHandler) holder.readline.getCompletionHandler()).setAlwaysIncludeNewline(false);
         if (holder.currentCompletor == null) {
             holder.currentCompletor = new RubyFileNameCompletor();
         }
         holder.readline.addCompletor(holder.currentCompletor);
         holder.readline.setHistory(holder.history);
 
         // JRUBY-852, ignore escape key (it causes IRB to quit if we pass it out through readline)
         holder.readline.addTriggeredAction(ESC_KEY_CODE, new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 try {
                     holder.readline.beep();
                 } catch (IOException ioe) {
                     // ignore
                 }
             }
         });
 
         if (DEBUG) holder.readline.setDebug(new PrintWriter(System.err));
     }
 
     public static History getHistory(ConsoleHolder holder) {
         return holder.history;
     }
 
     public static ConsoleHolder getHolder(Ruby runtime) {
         return (ConsoleHolder) (runtime.fastGetModule("Readline").dataGetStruct());
     }
 
     public static void setCompletor(ConsoleHolder holder, Completor completor) {
         if (holder.readline != null) {
             holder.readline.removeCompletor(holder.currentCompletor);
         }
         holder.currentCompletor = completor;
         if (holder.readline != null) {
             holder.readline.addCompletor(holder.currentCompletor);
         }
     }
 
     public static Completor getCompletor(ConsoleHolder holder) {
         return holder.currentCompletor;
     }
 
     public static IRubyObject s_readline(IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
         return s_readline(recv.getRuntime().getCurrentContext(), recv, prompt, add_to_hist);
     }
 
     @JRubyMethod(name = "readline", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_readline(ThreadContext context, IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
         Ruby runtime = context.getRuntime();
         ConsoleHolder holder = getHolder(runtime);
         if (holder.readline == null) {
             initReadline(runtime, holder); // not overridden, let's go
         }
         
         IRubyObject line = runtime.getNil();
         String v = null;
         while (true) {
             try {
                 holder.readline.getTerminal().disableEcho();
                 v = holder.readline.readLine(prompt.toString());
                 break;
             } catch (IOException ioe) {
                 if (RubyIO.restartSystemCall(ioe)) {
                     // This is for JRUBY-2988, since after a suspend the terminal seems
                     // to need to be reinitialized. Since we can't easily detect suspension,
                     // initialize after every readline. Probably not fast, but this is for
                     // interactive terminals anyway...so who cares?
                     try {holder.readline.getTerminal().initializeTerminal();} catch (Exception e) {}
                     continue;
                 }
                 throw runtime.newIOErrorFromException(ioe);
             } finally {
                 holder.readline.getTerminal().enableEcho();
             }
         }
         
         if (null != v) {
             if (add_to_hist.isTrue()) {
                 holder.readline.getHistory().addToHistory(v);
             }
 
             /* Explicitly use UTF-8 here. c.f. history.addToHistory using line.asUTF8() */
             line = RubyString.newUnicodeString(recv.getRuntime(), v);
         }
         return line;
     }
 
     @JRubyMethod(name = "input=", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject setInput(ThreadContext context, IRubyObject recv, IRubyObject input) {
         // FIXME: JRUBY-3604
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "output=", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject setOutput(ThreadContext context, IRubyObject recv, IRubyObject output) {
         // FIXME: JRUBY-3604
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_readline(IRubyObject recv, IRubyObject prompt) throws IOException {
         return s_readline(recv, prompt, recv.getRuntime().getFalse());
     }
 
     @JRubyMethod(name = "readline", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_readline(IRubyObject recv) throws IOException {
         return s_readline(recv, RubyString.newEmptyString(recv.getRuntime()), recv.getRuntime().getFalse());
     }
 
     @JRubyMethod(name = "basic_word_break_characters=", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_set_basic_word_break_character(IRubyObject recv, IRubyObject achar) throws Exception {
         Ruby runtime = recv.getRuntime();
         if (!achar.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + achar.getMetaClass() + " into String");
         }
         ProcCompletor.setDelimiter(achar.convertToString().toString());
         return achar;
     }
 
     @JRubyMethod(name = "basic_word_break_characters", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_get_basic_word_break_character(IRubyObject recv) throws Exception {
         return recv.getRuntime().newString(ProcCompletor.getDelimiter());
     }
 
     @JRubyMethod(name = "completion_append_character=", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_set_completion_append_character(IRubyObject recv, IRubyObject achar) throws Exception {
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "completion_proc=", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_set_completion_proc(IRubyObject recv, IRubyObject proc) throws Exception {
         if (!proc.respondsTo("call")) {
             throw recv.getRuntime().newArgumentError("argument must respond to call");
         }
         setCompletor(getHolder(recv.getRuntime()), new ProcCompletor(proc));
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = {
         "basic_quote_characters", "basic_quote_characters=",
         "completer_quote_characters", "completer_quote_characters=",
         "completer_word_break_characters", "completer_word_break_characters=",
         "completion_append_character",
         "completion_proc",
         "emacs_editing_mode",
         "filename_quote_characters", "filename_quote_characters=",
         "vi_editing_mode"}, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject unimplemented(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         String err = context.getFrameName() + "() function is unimplemented on this machine";
         throw runtime.newNotImplementedError(err);
     }
 
     @JRubyMethod(name = {
         "basic_quote_characters", "basic_quote_characters=",
         "completer_quote_characters", "completer_quote_characters=",
         "completer_word_break_characters", "completer_word_break_characters=",
         "completion_append_character",
         "completion_proc",
         "emacs_editing_mode", "emacs_editing_mode?",
         "filename_quote_characters", "filename_quote_characters=",
         "vi_editing_mode", "vi_editing_mode?",
         "set_screen_size"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject unimplemented19(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         String err = context.getFrameName() + "() function is unimplemented on this machine";
         throw runtime.newNotImplementedError(err);
     }
 
     @JRubyMethod(name = "completion_case_fold", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject s_get_completion_case_fold(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         return COMPLETION_CASE_FOLD;
     }
 
     @JRubyMethod(name = "completion_case_fold=", required = 1, module = true, visibility = Visibility.PRIVATE)
     // FIXME: this is really a noop
     public static IRubyObject s_set_completion_case_fold(ThreadContext context, IRubyObject recv,
             IRubyObject other) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         return COMPLETION_CASE_FOLD = other;
     }
 
     @JRubyMethod(name = "get_screen_size", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject s_get_screen_size(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         ConsoleHolder holder = getHolder(runtime);
         IRubyObject[] ary = new IRubyObject[2];
         ary[0] = runtime.newFixnum(holder.readline.getTermheight());
         ary[1] = runtime.newFixnum(holder.readline.getTermwidth());
         return RubyArray.newArray(runtime, ary);
 
     }
 
     @JRubyMethod(name = "line_buffer", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject s_get_line_buffer(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         ConsoleHolder holder = getHolder(runtime);
         if (holder.readline == null) {
             initReadline(runtime, holder);
         }
         CursorBuffer cb = holder.readline.getCursorBuffer();
         return runtime.newString(cb.toString()).taint(context);
     }
 
     @JRubyMethod(name = "point", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject s_get_point(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         ConsoleHolder holder = getHolder(runtime);
         if (holder.readline == null) {
             initReadline(runtime, holder);
         }
         CursorBuffer cb = holder.readline.getCursorBuffer();
         return runtime.newFixnum(cb.cursor);
     }
 
     @JRubyMethod(name = "refresh_line", module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject s_refresh_line(ThreadContext context, IRubyObject recv) throws Exception {
         Ruby runtime = context.getRuntime();
         runtime.secure(4);
         ConsoleHolder holder = getHolder(runtime);
         holder.readline.redrawLine(); // not quite the same as rl_refresh_line()
         return runtime.getNil();
     }
 
     public static class HistoryMethods {
         @JRubyMethod(name = {"push", "<<"}, rest = true)
         public static IRubyObject s_push(IRubyObject recv, IRubyObject[] lines) throws Exception {
             ConsoleHolder holder = getHolder(recv.getRuntime());
             for (int i = 0; i < lines.length; i++) {
                 RubyString line = lines[i].convertToString();
                 holder.history.addToHistory(line.getUnicodeValue());
             }
             return recv.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "pop")
         @SuppressWarnings("unchecked")
         public static IRubyObject s_pop(IRubyObject recv) throws Exception {
             Ruby runtime = recv.getRuntime();
             ConsoleHolder holder = getHolder(runtime);
 
             if (holder.history.size() == 0) return runtime.getNil();
 
             return runtime.newString((String)holder.history.pop()).taint(runtime.getCurrentContext());
         }
 
         @JRubyMethod(name = "to_a")
         public static IRubyObject s_hist_to_a(IRubyObject recv) throws Exception {
             Ruby runtime = recv.getRuntime();
             ConsoleHolder holder = getHolder(runtime);
             RubyArray histList = runtime.newArray();
 
             for (Iterator i = holder.history.getHistoryList().iterator(); i.hasNext();) {
                 histList.append(runtime.newString((String) i.next()));
             }
 
             return histList;
         }
 
         @JRubyMethod(name = "to_s")
         public static IRubyObject s_hist_to_s(IRubyObject recv) {
             return recv.getRuntime().newString("HISTORY");
         }
 
         @JRubyMethod(name = "[]")
         public static IRubyObject s_hist_get(IRubyObject recv, IRubyObject index) {
             Ruby runtime = recv.getRuntime();
             ConsoleHolder holder = getHolder(runtime);
             int i = (int) index.convertToInteger().getLongValue();
 
             if (i < 0) i += holder.history.size();
 
             try {
                 ThreadContext context = runtime.getCurrentContext();
 
                 return runtime.newString((String) holder.history.getHistoryList().get(i)).taint(context);
             } catch (IndexOutOfBoundsException ioobe) {
                 throw runtime.newIndexError("invalid history index: " + i);
             }
         }
 
         @JRubyMethod(name = "[]=")
         public static IRubyObject s_hist_set(IRubyObject recv, IRubyObject index, IRubyObject val) {
             Ruby runtime = recv.getRuntime();
             ConsoleHolder holder = getHolder(runtime);
             int i = (int) index.convertToInteger().getLongValue();
 
             if (i < 0) i += holder.history.size();
 
             try {
                 holder.history.set(i, val.asJavaString());
             } catch (IndexOutOfBoundsException ioobe) {
                 throw runtime.newIndexError("invalid history index: " + i);
         }
             return runtime.getNil();
         }
 
         @JRubyMethod(name = "shift")
         public static IRubyObject s_hist_shift(IRubyObject recv) {
             Ruby runtime = recv.getRuntime();
             ConsoleHolder holder = getHolder(runtime);
 
             if (holder.history.size() == 0) return runtime.getNil();
 
             try {
                 return runtime.newString(holder.history.remove(0)).taint(runtime.getCurrentContext());
             } catch (IndexOutOfBoundsException ioobe) {
                 throw runtime.newIndexError("history shift error");
             }
         }
 
         @JRubyMethod(name = {"length", "size"})
         public static IRubyObject s_hist_length(IRubyObject recv) {
             ConsoleHolder holder = getHolder(recv.getRuntime());
 
             return recv.getRuntime().newFixnum(holder.history.size());
         }
 
         @JRubyMethod(name = "empty?")
         public static IRubyObject s_hist_empty_p(IRubyObject recv) {
             ConsoleHolder holder = getHolder(recv.getRuntime());
 
             return recv.getRuntime().newBoolean(holder.history.size() == 0);
         }
 
         @JRubyMethod(name = "delete_at")
         public static IRubyObject s_hist_delete_at(IRubyObject recv, IRubyObject index) {
             Ruby runtime = recv.getRuntime();
             ThreadContext context = runtime.getCurrentContext();
 
             ConsoleHolder holder = getHolder(runtime);
             int i = RubyNumeric.num2int(index);
 
             if (i < 0) i += holder.history.size();
             
             try {
                 return runtime.newString(holder.history.remove(i)).taint(context);
             } catch (IndexOutOfBoundsException ioobe) {
                 throw runtime.newIndexError("invalid history index: " + i);
             }
         }
 
         @JRubyMethod(name = "each")
         public static IRubyObject s_hist_each(IRubyObject recv, Block block) {
             Ruby runtime = recv.getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             ConsoleHolder holder = getHolder(runtime);
 
             for (Iterator i = holder.history.getHistoryList().iterator(); i.hasNext();) {
                 block.yield(context, runtime.newString((String) i.next()).taint(context));
             }
             return recv;
         }
     }
 
     // Complete using a Proc object
     public static class ProcCompletor implements Completor {
 
         IRubyObject procCompletor;
         //\t\n\"\\'`@$><=;|&{(
         static private String[] delimiters = {" ", "\t", "\n", "\"", "\\", "'", "`", "@", "$", ">", "<", "=", ";", "|", "&", "{", "("};
 
         public ProcCompletor(IRubyObject procCompletor) {
             this.procCompletor = procCompletor;
         }
 
         public static String getDelimiter() {
             StringBuilder result = new StringBuilder(delimiters.length);
             for (String delimiter : delimiters) {
                 result.append(delimiter);
             }
             return result.toString();
         }
 
         public static void setDelimiter(String delimiter) {
             List<String> l = new ArrayList<String>();
             CharBuffer buf = CharBuffer.wrap(delimiter);
             while (buf.hasRemaining()) {
                 l.add(String.valueOf(buf.get()));
             }
             delimiters = l.toArray(new String[l.size()]);
         }
 
         private int wordIndexOf(String buffer) {
             int index = 0;
             for (String c : delimiters) {
                 index = buffer.lastIndexOf(c);
                 if (index != -1) return index;
             }
             return index;
         }
 
         public int complete(String buffer, int cursor, List candidates) {
             buffer = buffer.substring(0, cursor);
             int index = wordIndexOf(buffer);
             if (index != -1) buffer = buffer.substring(index + 1);
 
             Ruby runtime = procCompletor.getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             IRubyObject result = procCompletor.callMethod(context, "call", runtime.newString(buffer));
             IRubyObject comps = result.callMethod(context, "to_a");
             
             if (comps instanceof List) {
                 for (Iterator i = ((List) comps).iterator(); i.hasNext();) {
                     Object obj = i.next();
                     if (obj != null) {
                         candidates.add(obj.toString());
                     }
                 }
                 Collections.sort(candidates);
             }
             return cursor - buffer.length();
         }
     }
 
     // Fix FileNameCompletor to work mid-line
     public static class RubyFileNameCompletor extends FileNameCompletor {
         @Override
         public int complete(String buffer, int cursor, List candidates) {
             buffer = buffer.substring(0, cursor);
             int index = buffer.lastIndexOf(" ");
             if (index != -1) {
                 buffer = buffer.substring(index + 1);
             }
             return index + 1 + super.complete(buffer, cursor, candidates);
         }
     }
 }
diff --git a/src/org/jruby/libraries/RbConfigLibrary.java b/src/org/jruby/libraries/RbConfigLibrary.java
index ea1e5ccf5b..3d0034fc88 100644
--- a/src/org/jruby/libraries/RbConfigLibrary.java
+++ b/src/org/jruby/libraries/RbConfigLibrary.java
@@ -1,285 +1,285 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter
  * Copyright (C) 2006 Nick Sieger
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.NormalizedFile;
+import org.jruby.util.SafePropertyAccessor;
 import org.jruby.anno.JRubyModule;
 
 @JRubyModule(name="Config")
 public class RbConfigLibrary implements Library {
     // Ruby's designation for some platforms, minus version numbers in some cases
     private static final String RUBY_DARWIN = "darwin";
     private static final String RUBY_LINUX = "linux";
     private static final String RUBY_WIN32 = "mswin32";
     private static final String RUBY_SOLARIS = "solaris";
     private static final String RUBY_FREEBSD = "freebsd";
     private static final String RUBY_AIX = "aix";
     
     /** This is a map from Java's "friendly" OS names to those used by Ruby */
     public static final Map<String, String> RUBY_OS_NAMES = new HashMap<String, String>();
     static {
         RUBY_OS_NAMES.put("Mac OS X", RUBY_DARWIN);
         RUBY_OS_NAMES.put("Darwin", RUBY_DARWIN);
         RUBY_OS_NAMES.put("Linux", RUBY_LINUX);
         RUBY_OS_NAMES.put("Windows 95", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows 98", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows Me", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows NT", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows 2000", RUBY_WIN32);
         // that's what JDK5 produces on Windows Vista
         RUBY_OS_NAMES.put("Windows NT (unknown)", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows XP", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows 2003", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows Vista", RUBY_WIN32);
         RUBY_OS_NAMES.put("Windows 7", RUBY_WIN32);
         RUBY_OS_NAMES.put("Solaris", RUBY_SOLARIS);
         RUBY_OS_NAMES.put("FreeBSD", RUBY_FREEBSD);
         RUBY_OS_NAMES.put("AIX", RUBY_AIX);
     }
     
     public static String getOSName() {
         String OSName = Platform.getOSName();
         String theOSName = RUBY_OS_NAMES.get(OSName);
         
         return theOSName == null ? OSName : theOSName;
     }
     /**
      * Just enough configuration settings (most don't make sense in Java) to run the rubytests
      * unit tests. The tests use <code>bindir</code>, <code>RUBY_INSTALL_NAME</code> and
      * <code>EXEEXT</code>.
      */
     public void load(Ruby runtime, boolean wrap) {
         RubyModule configModule = runtime.defineModule("Config");
         
         configModule.defineAnnotatedMethods(RbConfigLibrary.class);
         
         RubyHash configHash = RubyHash.newHash(runtime);
         configModule.defineConstant("CONFIG", configHash);
         runtime.getObject().defineConstant("RbConfig", configModule);
 
         String[] versionParts = Constants.RUBY_VERSION.split("\\.");
         setConfig(configHash, "MAJOR", versionParts[0]);
         setConfig(configHash, "MINOR", versionParts[1]);
         setConfig(configHash, "TEENY", versionParts[2]);
         setConfig(configHash, "ruby_version", versionParts[0] + '.' + versionParts[1]);
         // Rubygems is too specific on host cpu so until we have real need lets default to universal
         //setConfig(configHash, "arch", System.getProperty("os.arch") + "-java" + System.getProperty("java.specification.version"));
         setConfig(configHash, "arch", "universal-java" + System.getProperty("java.specification.version"));
 
         String normalizedHome;
-        if (Ruby.isSecurityRestricted()) {
+        normalizedHome = runtime.getJRubyHome();
+        if ((normalizedHome == null) && Ruby.isSecurityRestricted()) {
             normalizedHome = "SECURITY RESTRICTED";
-        } else {
-            normalizedHome = runtime.getJRubyHome();
         }
         setConfig(configHash, "bindir", new NormalizedFile(normalizedHome, "bin").getPath());
         setConfig(configHash, "RUBY_INSTALL_NAME", jrubyScript());
         setConfig(configHash, "ruby_install_name", jrubyScript());
         setConfig(configHash, "SHELL", jrubyShell());
         setConfig(configHash, "prefix", normalizedHome);
         setConfig(configHash, "exec_prefix", normalizedHome);
 
         setConfig(configHash, "host_os", getOSName());
         setConfig(configHash, "host_vendor", System.getProperty("java.vendor"));
         setConfig(configHash, "host_cpu", Platform.ARCH);
         
         setConfig(configHash, "target_os", getOSName());
         
         setConfig(configHash, "target_cpu", Platform.ARCH);
         
         String jrubyJarFile = "jruby.jar";
         URL jrubyPropertiesUrl = Ruby.getClassLoader().getResource(Constants.JRUBY_PROPERTIES);
         if (jrubyPropertiesUrl != null) {
             Pattern jarFile = Pattern.compile("jar:file:.*?([a-zA-Z0-9.\\-]+\\.jar)!" + Constants.JRUBY_PROPERTIES);
             Matcher jarMatcher = jarFile.matcher(jrubyPropertiesUrl.toString());
             jarMatcher.find();
             if (jarMatcher.matches()) {
                 jrubyJarFile = jarMatcher.group(1);
             }
         }
         setConfig(configHash, "LIBRUBY", jrubyJarFile);
         setConfig(configHash, "LIBRUBY_SO", jrubyJarFile);
         
         setConfig(configHash, "build", Constants.BUILD);
         setConfig(configHash, "target", Constants.TARGET);
         
-        String libdir = System.getProperty("jruby.lib");
+        String libdir = SafePropertyAccessor.getProperty("jruby.lib");
         if (libdir == null) {
             libdir = new NormalizedFile(normalizedHome, "lib").getPath();
         } else {
             try {
             // Our shell scripts pass in non-canonicalized paths, but even if we didn't
             // anyone who did would become unhappy because Ruby apps expect no relative
             // operators in the pathname (rubygems, for example).
                 libdir = new NormalizedFile(libdir).getCanonicalPath();
             } catch (IOException e) {
                 libdir = new NormalizedFile(libdir).getAbsolutePath();
             }
         }
         String rubyLibDir = new NormalizedFile(libdir, "ruby/1.8").getPath();
         String siteDir = new NormalizedFile(libdir, "ruby/site_ruby").getPath();
         String siteLibDir = new NormalizedFile(libdir, "ruby/site_ruby/1.8").getPath();
         String siteArchDir = new NormalizedFile(libdir, "ruby/site_ruby/1.8/java").getPath();
         String archDir = new NormalizedFile(libdir, "ruby/1.8/java").getPath();
         String shareDir = new NormalizedFile(normalizedHome, "share").getPath();
 
         setConfig(configHash, "libdir", libdir);
         setConfig(configHash, "rubylibdir",     rubyLibDir);
         setConfig(configHash, "sitedir",        siteDir);
         setConfig(configHash, "sitelibdir",     siteLibDir);
         setConfig(configHash, "sitearchdir",    siteArchDir);
         setConfig(configHash, "archdir",   archDir);
         setConfig(configHash, "topdir",   archDir);
         setConfig(configHash, "configure_args", "");
         setConfig(configHash, "datadir", shareDir);
         setConfig(configHash, "mandir", new NormalizedFile(normalizedHome, "man").getPath());
         setConfig(configHash, "sysconfdir", new NormalizedFile(normalizedHome, "etc").getPath());
         setConfig(configHash, "localstatedir", new NormalizedFile(normalizedHome, "var").getPath());
         setConfig(configHash, "DLEXT", "jar");
 
         if (Platform.IS_WINDOWS) {
             setConfig(configHash, "EXEEXT", ".exe");
         } else {
             setConfig(configHash, "EXEEXT", "");
         }
 
         if (runtime.is1_9()) {
             setConfig(configHash, "ridir", new NormalizedFile(shareDir, "ri").getPath());
         }
         
         RubyHash mkmfHash = RubyHash.newHash(runtime);
         
 
         setConfig(mkmfHash, "libdir", libdir);
         setConfig(mkmfHash, "arch", "java");
         setConfig(mkmfHash, "rubylibdir",     rubyLibDir);
         setConfig(mkmfHash, "sitedir",        siteDir);
         setConfig(mkmfHash, "sitelibdir",     siteLibDir);
         setConfig(mkmfHash, "sitearch", "java");
         setConfig(mkmfHash, "sitearchdir",    siteArchDir);
         setConfig(mkmfHash, "archdir",    archDir);
         setConfig(mkmfHash, "topdir",    archDir);
         setConfig(mkmfHash, "configure_args", "");
         setConfig(mkmfHash, "datadir", new NormalizedFile(normalizedHome, "share").getPath());
         setConfig(mkmfHash, "mandir", new NormalizedFile(normalizedHome, "man").getPath());
         setConfig(mkmfHash, "sysconfdir", new NormalizedFile(normalizedHome, "etc").getPath());
         setConfig(mkmfHash, "localstatedir", new NormalizedFile(normalizedHome, "var").getPath());
         
         setupMakefileConfig(configModule, mkmfHash);
     }
     
     private static void setupMakefileConfig(RubyModule configModule, RubyHash mkmfHash) {
         Ruby ruby = configModule.getRuntime();
         
         String jflags = " -fno-omit-frame-pointer -fno-strict-aliasing -DNDEBUG ";
         String oflags = " -O2 " + jflags;
         String wflags = " -W -Werror -Wall -Wno-unused -Wno-parentheses ";
         String picflags = true ? "" : " -fPIC -pthread ";
         
         String iflags = " -I\"$(JDK_HOME)/include\" -I\"$(JDK_HOME)/include/$(OS)\" -I\"$(BUILD_DIR)\" ";
         
         String cflags = "";
         String soflags = true ? "" : " -shared -static-libgcc -mimpure-text -Wl,-O1 ";
         String ldflags = soflags;
         
         
         String archflags = " -arch i386 -arch ppc -arch x86_64 ";
         
         cflags += archflags;
 
         // this set is only for darwin
         cflags += " -isysroot /Developer/SDKs/MacOSX10.4u.sdk -DTARGET_RT_MAC_CFM=0 ";
         cflags += " -arch i386 -arch ppc -arch x86_64 ";
         ldflags += " -arch i386 -arch ppc -arch x86_64 -bundle -framework JavaVM -Wl,-syslibroot,$(SDKROOT) -mmacosx-version-min=10.4 -undefined dynamic_lookup ";
         String libext = "a";
         String objext = "o";
         
         setConfig(mkmfHash, "configure_args", "");
         setConfig(mkmfHash, "CFLAGS", cflags);
         setConfig(mkmfHash, "CPPFLAGS", "");
         setConfig(mkmfHash, "ARCH_FLAG", "");
         setConfig(mkmfHash, "LDFLAGS", ldflags);
         setConfig(mkmfHash, "DLDFLAGS", "");
         setConfig(mkmfHash, "LIBEXT", libext);
         setConfig(mkmfHash, "OBJEXT", objext);
         setConfig(mkmfHash, "LIBRUBYARG_STATIC", "");
         setConfig(mkmfHash, "LIBRUBYARG_SHARED", "");
         setConfig(mkmfHash, "LIBS", "");
         setConfig(mkmfHash, "DLDLIBS", "");
         setConfig(mkmfHash, "ENABLED_SHARED", "");
         setConfig(mkmfHash, "LIBRUBY", "");
         setConfig(mkmfHash, "LIBRUBY_A", "");
         setConfig(mkmfHash, "LIBRUBYARG", "");
         setConfig(mkmfHash, "prefix", "");
         setConfig(mkmfHash, "ruby_install_name", jrubyScript());
         setConfig(mkmfHash, "DLEXT", "bundle");
         setConfig(mkmfHash, "CC", "cc ");
         setConfig(mkmfHash, "LDSHARED", "cc ");
         setConfig(mkmfHash, "OUTFLAG", "-o ");
         setConfig(mkmfHash, "PATH_SEPARATOR", ":");
         setConfig(mkmfHash, "INSTALL", "install -c ");
         setConfig(mkmfHash, "RM", "rm -f");
         setConfig(mkmfHash, "CP", "cp ");
         setConfig(mkmfHash, "MAKEDIRS", "mkdir -p ");
         
         ruby.getObject().defineConstant("CROSS_COMPILING", ruby.getNil());
         
         configModule.defineConstant("MAKEFILE_CONFIG", mkmfHash);
     }
 
     private static void setConfig(RubyHash mkmfHash, String key, String value) {
         Ruby runtime = mkmfHash.getRuntime();
         mkmfHash.op_aset(runtime.getCurrentContext(), runtime.newString(key), runtime.newString(value));
     }
 
     public static String jrubyScript() {
-        return System.getProperty("jruby.script", "jruby").replace('\\', '/');
+        return SafePropertyAccessor.getProperty("jruby.script", "jruby").replace('\\', '/');
     }
 
     // TODO: note lack of command.com support for Win 9x...
     public static String jrubyShell() {
-        return System.getProperty("jruby.shell", Platform.IS_WINDOWS ? "cmd.exe" : "/bin/sh").replace('\\', '/');
+        return SafePropertyAccessor.getProperty("jruby.shell", Platform.IS_WINDOWS ? "cmd.exe" : "/bin/sh").replace('\\', '/');
     }
 
 }
diff --git a/src/org/jruby/platform/Platform.java b/src/org/jruby/platform/Platform.java
index 27cd0fe905..6829efd438 100644
--- a/src/org/jruby/platform/Platform.java
+++ b/src/org/jruby/platform/Platform.java
@@ -1,128 +1,128 @@
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
  * Copyright (C) 2008 JRuby project
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
 
 package org.jruby.platform;
 
 import java.nio.ByteOrder;
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  * Platform specific constants.
  */
 public class Platform {
     private static final Platform INSTANCE = new Platform();
     public static Platform getPlatform() {
         return INSTANCE;
     }
     protected Platform() {
     }
 
     public static void main(String[] args) {
         System.out.println(System.getProperties());
     }
 
     public String getPackageName() {
         return String.format("%s.%s.%s", Platform.class.getPackage().getName(), OS, ARCH);
     }
     public String getOSPackageName() {
         return String.format("%s.%s", Platform.class.getPackage().getName(), OS);
     }
     
     private static final String DARWIN = "darwin";
     private static final String WINDOWS = "windows";
     private static final String LINUX = "linux";
     private static final String FREEBSD = "freebsd";
     private static final String OPENBSD = "openbsd";
     private static final String SOLARIS = "solaris";
 
     private static final String GCJ = "GNU libgcj";
 
     public static final Map<String, String> OS_NAMES = new HashMap<String, String>() {{
         put("Mac OS X", DARWIN);
     }};
     public static final Map<String, String> ARCH_NAMES = new HashMap<String, String>() {{
         put("x86", "i386");
     }};
     private static final String initOperatingSystem() {
         String osname = getProperty("os.name", "unknown").toLowerCase();
         for (String s : OS_NAMES.keySet()) {
             if (s.equalsIgnoreCase(osname)) {
                 return OS_NAMES.get(s);
             }
         }
         if (osname.startsWith("windows")) {
             return WINDOWS;
         }
         return osname;
     }
     private static final String initArchitecture() {
         String arch = getProperty("os.arch", "unknown").toLowerCase();
         for (String s : ARCH_NAMES.keySet()) {
             if (s.equalsIgnoreCase(arch)) {
                 return ARCH_NAMES.get(s);
             }
         }
         return arch;
     }
     public static final String ARCH = initArchitecture();
     public static final String OS = initOperatingSystem();
-    public static final String JVM = System.getProperty("java.vm.name");
+    public static final String JVM = getProperty("java.vm.name", "unknown");
 
     public static final boolean IS_WINDOWS = OS.equals(WINDOWS);
 
     public static final boolean IS_MAC = OS.equals(DARWIN);
     public static final boolean IS_FREEBSD = OS.equals(FREEBSD);
     public static final boolean IS_OPENBSD = OS.equals(OPENBSD);
     public static final boolean IS_LINUX = OS.equals(LINUX);
     public static final boolean IS_SOLARIS = OS.equals(SOLARIS);
     public static final boolean IS_BSD = IS_MAC || IS_FREEBSD || IS_OPENBSD;
     public static final String NAME = String.format("%s-%s", ARCH, OS);
     public static final int BIG_ENDIAN = 4321;
     public static final int LITTLE_ENDIAN = 1234;
     public static final int BYTE_ORDER = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? BIG_ENDIAN : LITTLE_ENDIAN;
 
     public static final boolean IS_GCJ = JVM.equals(GCJ);
     
     /**
      * An extension over <code>System.getProperty</code> method.
      * Handles security restrictions, and returns the default
      * value if the access to the property is restricted.
      * @param property The system property name.
      * @param defValue The default value.
      * @return The value of the system property,
      *         or the default value.
      */
     public static String getProperty(String property, String defValue) {
         try {
             return System.getProperty(property, defValue);
         } catch (SecurityException se) {
             return defValue;
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 57ec162706..acb87bfee8 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,1164 +1,1162 @@
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
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import java.util.zip.ZipException;
 import org.jruby.CompatVersion;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFile;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 /**
  * <b>How require works in JRuby</b>
  * When requiring a name from Ruby, JRuby will first remove any file extension it knows about,
  * thereby making it possible to use this string to see if JRuby has already loaded
  * the name in question. If a .rb extension is specified, JRuby will only try
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
     private final LoadTimer loadTimer;
 
     public enum SuffixType {
         Source, Extension, Both, Neither;
         
         public static final String[] sourceSuffixes = { ".class", ".rb" };
         public static final String[] extensionSuffixes = { ".jar" };
         private static final String[] allSuffixes = { ".class", ".rb", ".jar" };
         private static final String[] emptySuffixes = { "" };
         
         public String[] getSuffixes() {
             switch (this) {
             case Source:
                 return sourceSuffixes;
             case Extension:
                 return extensionSuffixes;
             case Both:
                 return allSuffixes;
             case Neither:
                 return emptySuffixes;
             }
             throw new RuntimeException("Unknown SuffixType: " + this);
         }
     }
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|jar)$");
 
     protected RubyArray loadPath;
     protected RubyArray loadedFeatures;
     protected List loadedFeaturesInternal;
     protected final Map<String, Library> builtinLibraries = new HashMap<String, Library>();
 
     protected final Map<String, JarFile> jarFiles = new HashMap<String, JarFile>();
 
     protected final Map<String, IAutoloadMethod> autoloadMap = new HashMap<String, IAutoloadMethod>();
 
     protected final Ruby runtime;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         if (RubyInstanceConfig.DEBUG_LOAD_TIMINGS) {
             loadTimer = new TracingLoadTimer();
         } else {
             loadTimer = new LoadTimer();
         }
     }
 
     public void init(List additionalDirectories) {
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);
         loadedFeaturesInternal = Collections.synchronizedList(loadedFeatures);
         
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // add $RUBYLIB paths
-       RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
-       RubyString env_rubylib = runtime.newString("RUBYLIB");
-       if (env.has_key_p(env_rubylib).isTrue()) {
-           String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
-           String[] paths = rubylib.split(File.pathSeparator);
-           for (int i = 0; i < paths.length; i++) {
-               addPath(paths[i]);
-           }
-       }
-
-       // wrap in try/catch for security exceptions in an applet
-       if (!Ruby.isSecurityRestricted()) {
-           try {
-               String jrubyHome = runtime.getJRubyHome();
-               if (jrubyHome != null) {
-                   char sep = '/';
-                   String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
-
-                   // If we're running in 1.9 compat mode, add Ruby 1.9 libs to path before 1.8 libs
-                   if (runtime.is1_9()) {
-                       addPath(rubyDir + "site_ruby" + sep + Constants.RUBY1_9_MAJOR_VERSION);
-                       addPath(rubyDir + "site_ruby" + sep + "shared");
-                       addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
-                       addPath(rubyDir + Constants.RUBY1_9_MAJOR_VERSION);
-                   } else {
-                       // Add 1.8 libs
-                       addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
-                       addPath(rubyDir + "site_ruby" + sep + "shared");
-                       addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
-                   }
-               }
-           } catch(SecurityException e) {}
-       }
+        RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
+        RubyString env_rubylib = runtime.newString("RUBYLIB");
+        if (env.has_key_p(env_rubylib).isTrue()) {
+            String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
+            String[] paths = rubylib.split(File.pathSeparator);
+            for (int i = 0; i < paths.length; i++) {
+                addPath(paths[i]);
+            }
+        }
+
+        // wrap in try/catch for security exceptions in an applet
+        try {
+            String jrubyHome = runtime.getJRubyHome();
+            if (jrubyHome != null) {
+                char sep = '/';
+                String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
+
+                // If we're running in 1.9 compat mode, add Ruby 1.9 libs to path before 1.8 libs
+                if (runtime.is1_9()) {
+                    addPath(rubyDir + "site_ruby" + sep + Constants.RUBY1_9_MAJOR_VERSION);
+                    addPath(rubyDir + "site_ruby" + sep + "shared");
+                    addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
+                    addPath(rubyDir + Constants.RUBY1_9_MAJOR_VERSION);
+                } else {
+                    // Add 1.8 libs
+                    addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
+                    addPath(rubyDir + "site_ruby" + sep + "shared");
+                    addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
+                }
+            }
+        } catch(SecurityException ignore) {}
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     protected void addLoadedFeature(RubyString loadNameRubyString) {
         loadedFeaturesInternal.add(loadNameRubyString);
     }
 
     protected void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
         
         synchronized(loadPath) {
             loadPath.append(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file, boolean wrap) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         SearchState state = new SearchState(file);
         state.prepareLoadSearch(file);
         
         Library library = findBuiltinLibrary(state, state.searchFile, state.suffixType);
         if (library == null) library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
 
         if (library == null) {
             library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime, wrap);
         } catch (IOException e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             throw runtime.newLoadError("IO error -- " + file);
         }
     }
 
     public SearchState findFileForLoad(String file) throws AlreadyLoaded {
         SearchState state = new SearchState(file);
         state.prepareRequireSearch(file);
 
         for (LoadSearcher searcher : searchers) {
             if (searcher.shouldTrySearch(state)) {
                 searcher.trySearch(state);
             } else {
                 continue;
             }
         }
 
         return state;
     }
 
     public boolean lockAndRequire(String requireName) {
         Object requireLock;
         try {
             synchronized (requireLocks) {
                 requireLock = requireLocks.get(requireName);
                 if (requireLock == null) {
                     requireLock = new Object();
                     requireLocks.put(requireName, requireLock);
                 }
             }
 
             synchronized (requireLock) {
                 return require(requireName);
             }
         } finally {
             synchronized (requireLocks) {
                 requireLocks.remove(requireName);
             }
         }
     }
 
     protected Map requireLocks = new Hashtable();
 
     public boolean smartLoad(String file) {
         checkEmptyLoad(file);
 
         // We don't support .so, but some stdlib require .so directly
         // replace it with .jar to look for an extension type we do support
         if (file.endsWith(".so")) {
             file = file.replaceAll(".so$", ".jar");
         }
         if (Platform.IS_WINDOWS) {
             file = file.replace('\\', '/');
         }
         
         try {
             SearchState state = findFileForLoad(file);
             return tryLoadingLibraryOrScript(runtime, state);
         } catch (AlreadyLoaded al) {
             // Library has already been loaded in some form, bail out
             return false;
         }
     }
 
     private static class LoadTimer {
         public long startLoad(String file) { return 0L; }
         public void endLoad(String file, long startTime) {}
     }
 
     private static class TracingLoadTimer extends LoadTimer {
         private final AtomicInteger indent = new AtomicInteger(0);
         private String getIndentString() {
             StringBuilder buf = new StringBuilder();
             int i = indent.get();
             for (int j = 0; j < i; j++) {
                 buf.append("  ");
             }
             return buf.toString();
         }
         @Override
         public long startLoad(String file) {
             int i = indent.incrementAndGet();
             System.err.println(getIndentString() + "-> " + file);
             return System.currentTimeMillis();
         }
         @Override
         public void endLoad(String file, long startTime) {
             System.err.println(getIndentString() + "<- " + file + " - "
                     + (System.currentTimeMillis() - startTime) + "ms");
             indent.decrementAndGet();
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         long startTime = loadTimer.startLoad(file);
         try {
             return smartLoad(file);
         } finally {
             loadTimer.endLoad(file, startTime);
         }
 
     }
 
     /**
      * Load the org.jruby.runtime.load.Library implementation specified by
      * className. The purpose of using this method is to avoid having static
      * references to the given library class, thereby avoiding the additional
      * classloading when the library is not in use.
      * 
      * @param runtime The runtime in which to load
      * @param libraryName The name of the library, to use for error messages
      * @param className The class of the library
      * @param classLoader The classloader to use to load it
      * @param wrap Whether to wrap top-level in an anonymous module
      */
     public static void reflectedLoad(Ruby runtime, String libraryName, String className, ClassLoader classLoader, boolean wrap) {
         try {
             if (classLoader == null && Ruby.isSecurityRestricted()) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
 
             Library library = (Library) classLoader.loadClass(className).newInstance();
 
             library.load(runtime, false);
         } catch (RaiseException re) {
             throw re;
         } catch (Throwable e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace();
             throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
         }
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return autoloadMap.get(name);
     }
 
     public void removeAutoLoadFor(String name) {
         autoloadMap.remove(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void addBuiltinLibrary(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void removeBuiltinLibrary(String name) {
         builtinLibraries.remove(name);
     }
 
     public void removeInternalLoadedFeature(String name) {
         loadedFeaturesInternal.remove(name);
     }
 
     protected boolean featureAlreadyLoaded(RubyString loadNameRubyString) {
         return loadedFeaturesInternal.contains(loadNameRubyString);
     }
 
     protected boolean isJarfileLibrary(SearchState state, final String file) {
         return state.library instanceof JarredScript && file.endsWith(".jar");
     }
 
     protected void removeLoadedFeature(RubyString loadNameRubyString) {
 
         loadedFeaturesInternal.remove(loadNameRubyString);
     }
 
     protected void reraiseRaiseExceptions(Throwable e) throws RaiseException {
         if (e instanceof RaiseException) {
             throw (RaiseException) e;
         }
     }
     
     public interface LoadSearcher {
         public boolean shouldTrySearch(SearchState state);
         public void trySearch(SearchState state) throws AlreadyLoaded;
     }
     
     public class AlreadyLoaded extends Exception {
         private RubyString searchNameString;
         
         public AlreadyLoaded(RubyString searchNameString) {
             this.searchNameString = searchNameString;
         }
         
         public RubyString getSearchNameString() {
             return searchNameString;
         }
     }
     
     public class BailoutSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return true;
         }
     
         public void trySearch(SearchState state) throws AlreadyLoaded {
             for (String suffix : state.suffixType.getSuffixes()) {
                 String searchName = state.searchFile + suffix;
                 RubyString searchNameString = RubyString.newString(runtime, searchName);
                 if (featureAlreadyLoaded(searchNameString)) {
                     throw new AlreadyLoaded(searchNameString);
                 }
             }
         }
     }
 
     public class NormalSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ClassLoaderSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ExtensionSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return (state.library == null || state.library instanceof JarredScript) && !state.searchFile.equalsIgnoreCase("");
         }
         
         public void trySearch(SearchState state) {
             // This code exploits the fact that all .jar files will be found for the JarredScript feature.
             // This is where the basic extension mechanism gets fixed
             Library oldLibrary = state.library;
 
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = state.searchFile.split("/");
 
             StringBuilder finName = new StringBuilder();
             for(int i=0, j=(all.length-1); i<j; i++) {
                 finName.append(all[i].toLowerCase()).append(".");
             }
 
             try {
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
                 if(state.library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     runtime.getJRubyClassLoader().addURL(((JarredScript)state.library).getResource().getURL());
                 }
 
                 // quietly try to load the class
                 Class theClass = runtime.getJavaSupport().loadJavaClassQuiet(className);
                 state.library = new ClassExtensionLibrary(theClass);
             } catch (Exception ee) {
                 state.library = null;
                 runtime.getGlobalVariables().set("$!", runtime.getNil());
             }
 
             // If there was a good library before, we go back to that
             if(state.library == null && oldLibrary != null) {
                 state.library = oldLibrary;
             }
         }
     }
 
     public class ScriptClassSearcher implements LoadSearcher {
         public class ScriptClassLibrary implements Library {
             private Script script;
 
             public ScriptClassLibrary(Script script) {
                 this.script = script;
             }
 
             public void load(Ruby runtime, boolean wrap) {
                 runtime.loadScript(script);
             }
         }
         
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) throws RaiseException {
             // no library or extension found, try to load directly as a class
             Script script;
             String className = buildClassName(state.searchFile);
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 && lastSlashIndex < className.length() - 1 && !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script) scriptClass.newInstance();
             } catch (Exception cnfe) {
                 throw runtime.newLoadError("no such file to load -- " + state.searchFile);
             }
             state.library = new ScriptClassLibrary(script);
         }
     }
     
     public class SearchState {
         public Library library;
         public String loadName;
         public SuffixType suffixType;
         public String searchFile;
         
         public SearchState(String file) {
             loadName = file;
         }
 
         public void prepareRequireSearch(final String file) {
             // if an extension is specified, try more targetted searches
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else if ((matcher = extensionPattern.matcher(file)).find()) {
                     // extension extensions
                     suffixType = SuffixType.Extension;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to search with extensions
                     suffixType = SuffixType.Both;
                     searchFile = file;
                 }
             } else {
                 // try all extensions
                 suffixType = SuffixType.Both;
                 searchFile = file;
             }
         }
 
         public void prepareLoadSearch(final String file) {
             // if a source extension is specified, try all source extensions
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to exact search
                     suffixType = SuffixType.Neither;
                     searchFile = file;
                 }
             } else {
                 // try only literal search
                 suffixType = SuffixType.Neither;
                 searchFile = file;
             }
         }
     }
     
     protected boolean tryLoadingLibraryOrScript(Ruby runtime, SearchState state) {
         // attempt to load the found library
         RubyString loadNameRubyString = RubyString.newString(runtime, state.loadName);
         try {
             synchronized (loadedFeaturesInternal) {
                 if (loadedFeaturesInternal.contains(loadNameRubyString)) {
                     return false;
                 } else {
                     addLoadedFeature(loadNameRubyString);
                 }
             }
             
             // otherwise load the library we've found
             state.library.load(runtime, false);
             return true;
         } catch (MainExitException mee) {
             // allow MainExitException to propagate out for exec and friends
             throw mee;
         } catch (Throwable e) {
             if(isJarfileLibrary(state, state.searchFile)) {
                 return true;
             }
 
             removeLoadedFeature(loadNameRubyString);
             reraiseRaiseExceptions(e);
 
             if(runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             
             RaiseException re = runtime.newLoadError("IO error -- " + state.searchFile);
             re.initCause(e);
             throw re;
         }
     }
     
     protected final List<LoadSearcher> searchers = new ArrayList<LoadSearcher>();
     {
         searchers.add(new BailoutSearcher());
         searchers.add(new NormalSearcher());
         searchers.add(new ClassLoaderSearcher());
         searchers.add(new ExtensionSearcher());
         searchers.add(new ScriptClassSearcher());
     }
 
     protected String buildClassName(String className) {
         // Remove any relative prefix, e.g. "./foo/bar" becomes "foo/bar".
         className = className.replaceFirst("^\\.\\/", "");
         if (className.lastIndexOf(".") != -1) {
             className = className.substring(0, className.lastIndexOf("."));
         }
         className = className.replace("-", "_minus_").replace('.', '_');
         return className;
     }
 
     protected void checkEmptyLoad(String file) throws RaiseException {
         if (file.equals("")) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
     }
 
     protected void debugLogTry(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: trying " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: found " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound( LoadServiceResource resource ) {
         String resourceUrl;
         try {
             resourceUrl = resource.getURL().toString();
         } catch (IOException e) {
             resourceUrl = e.getMessage();
         }
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: found: " + resourceUrl );
         }
     }
     
     protected Library findBuiltinLibrary(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             debugLogTry( "builtinLib",  namePlusSuffix );
             if (builtinLibraries.containsKey(namePlusSuffix)) {
                 state.loadName = namePlusSuffix;
                 Library lib = builtinLibraries.get(namePlusSuffix);
                 debugLogFound( "builtinLib", namePlusSuffix );
                 return lib;
             }
         }
         return null;
     }
 
     protected Library findLibraryWithoutCWD(SearchState state, String baseName, SuffixType suffixType) {
         Library library = null;
         
         switch (suffixType) {
         case Both:
             library = findBuiltinLibrary(state, baseName, SuffixType.Source);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Source));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Source));
             // If we fail to find as a normal Ruby script, we try to find as an extension,
             // checking for a builtin first.
             if (library == null) library = findBuiltinLibrary(state, baseName, SuffixType.Extension);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Extension));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Extension));
             break;
         case Source:
         case Extension:
             // Check for a builtin first.
             library = findBuiltinLibrary(state, baseName, suffixType);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, suffixType));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, suffixType));
             break;
         case Neither:
             library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Neither));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Neither));
             break;
         }
 
         return library;
     }
 
     protected Library findLibraryWithClassloaders(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String file = baseName + suffix;
             LoadServiceResource resource = findFileInClasspath(file);
             if (resource != null) {
                 state.loadName = file;
                 return createLibrary(state, resource);
             }
         }
         return null;
     }
 
     protected Library createLibrary(SearchState state, LoadServiceResource resource) {
         if (resource == null) {
             return null;
         }
         String file = state.loadName;
         if (file.endsWith(".so")) {
             throw runtime.newLoadError("JRuby does not support .so libraries from filesystem");
         } else if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }      
     }
 
     protected LoadServiceResource tryResourceFromCWD(SearchState state, String baseName,SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
         
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromCWD", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
                     String s = namePlusSuffix;
                     if(!namePlusSuffix.startsWith("./")) {
                         s = "./" + s;
                     }
                     foundResource = new LoadServiceResource(file, s, absolute);
                     debugLogFound(foundResource);
                     state.loadName = namePlusSuffix;
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURL(SearchState state, String baseName, SuffixType suffixType) {
         // if a jar or file URL, return load service resource directly without further searching
         LoadServiceResource foundResource = null;
         if (baseName.startsWith("jar:")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     URL url = new URL(namePlusSuffix);
                     debugLogTry("resourceFromJarURL", url.toString());
                     if (url.openStream() != null) {
                         foundResource = new LoadServiceResource(url, namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch (FileNotFoundException e) {
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 } catch (IOException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
                 if (foundResource != null) {
                     state.loadName = namePlusSuffix;
                     break; // end suffix iteration
                 }
             }
         } else if(baseName.startsWith("file:") && baseName.indexOf("!/") != -1) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     String jarFile = namePlusSuffix.substring(5, namePlusSuffix.indexOf("!/"));
                     JarFile file = new JarFile(jarFile);
                     String filename = namePlusSuffix.substring(namePlusSuffix.indexOf("!/") + 2);
                     String canonicalFilename = canonicalizePath(filename);
                     
                     debugLogTry("resourceFromJarURL", canonicalFilename.toString());
                     if(file.getJarEntry(canonicalFilename) != null) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + jarFile + "!/" + canonicalFilename), namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch(Exception e) {}
                 if (foundResource != null) {
                     state.loadName = namePlusSuffix;
                     break; // end suffix iteration
                 }
             }    
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromLoadPathOrURL(SearchState state, String baseName, SuffixType suffixType) {
         LoadServiceResource foundResource = null;
 
         // if it's a ./ baseName, use CWD logic
         if (baseName.startsWith("./")) {
             foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = foundResource.getName();
                 return foundResource;
             }
         }
 
         // if given path is absolute, just try it as-is (with extensions) and no load path
         if (new File(baseName).isAbsolute() || baseName.startsWith("../")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 foundResource = tryResourceAsIs(namePlusSuffix);
 
                 if (foundResource != null) {
                     state.loadName = namePlusSuffix;
                     return foundResource;
                 }
             }
 
             return null;
         }
         
         Outer: for (Iterator pathIter = loadPath.iterator(); pathIter.hasNext();) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             String loadPathEntry = pathIter.next().toString();
 
             if (loadPathEntry.equals(".") || loadPathEntry.equals("")) {
                 foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
                 if (foundResource != null) {
                     String ss = foundResource.getName();
                     if(ss.startsWith("./")) {
                         ss = ss.substring(2);
                     }
                     state.loadName = ss;
                     break Outer;
                 }
             } else {
                 boolean looksLikeJarURL = loadPathLooksLikeJarURL(loadPathEntry);
                 for (String suffix : suffixType.getSuffixes()) {
                     String namePlusSuffix = baseName + suffix;
 
                     if (looksLikeJarURL) {
                         foundResource = tryResourceFromJarURLWithLoadPath(namePlusSuffix, loadPathEntry);
                     } else {
                         foundResource = tryResourceFromLoadPath(namePlusSuffix, loadPathEntry);
                     }
 
                     if (foundResource != null) {
                         String ss = namePlusSuffix;
                         if(ss.startsWith("./")) {
                             ss = ss.substring(2);
                         }
                         state.loadName = ss;
                         break Outer; // end suffix iteration
                     }
                 }
             }
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURLWithLoadPath(String namePlusSuffix, String loadPathEntry) {
         LoadServiceResource foundResource = null;
         
         JarFile current = jarFiles.get(loadPathEntry);
         boolean isFileJarUrl = loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1;
         String after = isFileJarUrl ? loadPathEntry.substring(loadPathEntry.indexOf("!/") + 2) + "/" : "";
         String before = isFileJarUrl ? loadPathEntry.substring(0, loadPathEntry.indexOf("!/")) : loadPathEntry;
 
         if(null == current) {
             try {
                 if(loadPathEntry.startsWith("jar:")) {
                     current = new JarFile(loadPathEntry.substring(4));
                 } else if (loadPathEntry.endsWith(".jar")) {
                     current = new JarFile(loadPathEntry);
                 } else {
                     current = new JarFile(loadPathEntry.substring(5,loadPathEntry.indexOf("!/")));
                 }
                 jarFiles.put(loadPathEntry,current);
             } catch (ZipException ignored) {
                 if (runtime.getInstanceConfig().isVerbose()) {
                     runtime.getErr().println("ZipException trying to access " + loadPathEntry + ", stack trace follows:");
                     ignored.printStackTrace(runtime.getErr());
                 }
             } catch (FileNotFoundException ignored) {
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         }
         String canonicalEntry = after+namePlusSuffix;
         if (current != null ) {
             debugLogTry("resourceFromJarURLWithLoadPath", current.getName() + "!/" + canonicalEntry);
             if (current.getJarEntry(canonicalEntry) != null) {
                 try {
                     if (loadPathEntry.endsWith(".jar")) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + loadPathEntry + "!/" + canonicalEntry), "/" + namePlusSuffix);
                     } else if (loadPathEntry.startsWith("file:")) {
                         foundResource = new LoadServiceResource(new URL("jar:" + before + "!/" + canonicalEntry), loadPathEntry + "/" + namePlusSuffix);
                     } else {
                         foundResource =  new LoadServiceResource(new URL("jar:file:" + loadPathEntry.substring(4) + "!/" + namePlusSuffix), loadPathEntry + namePlusSuffix);
                     }
                     debugLogFound(foundResource);
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
             }
         }
         
         return foundResource;
     }
 
     protected boolean loadPathLooksLikeJarURL(String loadPathEntry) {
         return loadPathEntry.startsWith("jar:") || loadPathEntry.endsWith(".jar") || (loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1);
     }
 
     protected LoadServiceResource tryResourceFromLoadPath( String namePlusSuffix,String loadPathEntry) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = loadPathEntry + "/" + namePlusSuffix;
                 JRubyFile actualPath;
                 boolean absolute = false;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     absolute = true;
                     // it's an absolute path, use it as-is
                     actualPath = JRubyFile.create(loadPathEntry, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     absolute = false;
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) != '.') {
                         reportedPath = "./" + reportedPath;
                     }
                     actualPath = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(), loadPathEntry).getAbsolutePath(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
                 debugLogTry("resourceFromLoadPath", actualPath.toString());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath, absolute);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceAsIs(String namePlusSuffix) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = namePlusSuffix;
                 File actualPath;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     // it's an absolute path, use it as-is
                     actualPath = new File(RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) == '.' && reportedPath.charAt(1) == '/') {
                         reportedPath = reportedPath.replaceFirst("\\./", runtime.getCurrentDirectory());
                     }
 //                     if (reportedPath.charAt(0) != '.') {
 //                         reportedPath = "./" + reportedPath;
 //                     }
                     actualPath = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                     //                    actualPath = new File(RubyFile.expandUserPath(runtime.getCurrentContext(), reportedPath));
                 }
                 debugLogTry("resourceAsIs", actualPath.toString());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     protected LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
         // handle security-sensitive case
         if (Ruby.isSecurityRestricted() && classLoader == null) {
             classLoader = runtime.getInstanceConfig().getLoader();
         }
 
         for (Iterator pathIter = loadPath.iterator(); pathIter.hasNext();) {
             String entry = pathIter.next().toString();
 
             // if entry is an empty string, skip it
             if (entry.length() == 0) continue;
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
             
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             debugLogTry("fileInClasspath", entry + "/" + name);
             URL loc = classLoader.getResource(entry + "/" + name);
 
             // Make sure this is not a directory or unavailable in some way
             if (isRequireable(loc)) {
                 LoadServiceResource foundResource = new LoadServiceResource(loc, loc.getPath());
                 debugLogFound(foundResource);
                 return foundResource;
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         debugLogTry("fileInClasspath", name);
         URL loc = classLoader.getResource(name);
 
         if (isRequireable(loc)) {
             LoadServiceResource foundResource = new LoadServiceResource(loc, loc.getPath());
             debugLogFound(foundResource);
             return foundResource;
         }
 
         return null;
     }
     
     /* Directories and unavailable resources are not able to open a stream. */
     protected boolean isRequireable(URL loc) {
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
     
     protected String canonicalizePath(String path) {
         try {
             String cwd = new File(runtime.getCurrentDirectory()).getCanonicalPath();
             return new File(path).getCanonicalPath()
                                  .substring(cwd.length() + 1)
                                  .replaceAll("\\\\","/");
       } catch(Exception e) {
         return path;
       }
     }
 }
diff --git a/test/restricted.policy b/test/restricted.policy
index 173677bb26..3c1e95d174 100644
--- a/test/restricted.policy
+++ b/test/restricted.policy
@@ -1,6 +1,11 @@
-// @(#)javaws.policy	1.7 00/09/18
 // intentionally light policy, to mirror javaws and applet security
 
 grant {
-  permission java.lang.RuntimePermission "accessDeclaredMembers";
+  // Needed by POSIXFactory.
+  permission java.util.PropertyPermission "os.name", "read";
+  permission java.util.PropertyPermission "os.arch", "read";
+
+  // Needed by RubyInstanceConfig.
+  permission java.util.PropertyPermission "java.vm.name", "read";
+  permission java.util.PropertyPermission "java.version", "read";
 };
