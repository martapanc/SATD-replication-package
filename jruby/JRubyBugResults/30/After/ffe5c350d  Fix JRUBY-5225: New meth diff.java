diff --git a/build.xml b/build.xml
index 34fd1ac907..7b0b2f0af3 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1556 +1,1562 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <project basedir="." default="jar" name="JRuby">
   <description>JRuby is a pure Java implementation of a Ruby interpreter.</description>
 
   <property name="base.dir" location="${basedir}"/>
 
   <!-- First try to load machine-specific properties. -->
   <property file="build.properties"/>
 
   <!-- And then load the defaults. It seems backwards to set defaults AFTER
        setting local overrides, but that's how Ant works. -->
   <property file="default.build.properties"/>
 
   <property name="shared.lib.dir" value="lib/ruby/site_ruby/shared"/>
+  
+  <property name="asm.jar" value="${build.lib.dir}/asm-3.3.1.jar"/>
+  <property name="asm-commons.jar" value="${build.lib.dir}/asm-commons-3.3.1.jar"/>
+  <property name="asm-util.jar" value="${build.lib.dir}/asm-util-3.3.1.jar"/>
+  <property name="asm-analysis.jar" value="${build.lib.dir}/asm-analysis-3.3.1.jar"/>
+  <property name="asm-tree.jar" value="${build.lib.dir}/asm-tree-3.3.1.jar"/>
 
   <path id="build.classpath">
     <fileset dir="${build.lib.dir}" includes="*.jar"/>
   </path>
 
   <path id="jruby.execute.classpath">
     <path refid="build.classpath"/>
     <pathelement path="${jruby.classes.dir}"/>
   </path>
 
   <!-- directory that contains emma.jar and emma_ant.jar: -->
   <property name="emma.dir" value="${build.lib.dir}" />
 
   <path id="emma.classpath">
     <pathelement location="${emma.dir}/emma.jar" />
     <pathelement location="${emma.dir}/emma_ant.jar" />
   </path>
 
   <patternset id="java.src.pattern">
     <include name="**/*.java"/>
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
 
   <condition property="dev.null" value="/dev/null">
     <os family="unix"/>
   </condition>
   <condition property="dev.null" value="NUL">
     <os family="windows"/>
   </condition>
   <condition property="dev.null" value="dev_null">
     <not>
       <or>
         <os family="unix"/>
         <os family="windows"/>
       </or>
     </not>
   </condition>
 
   <!-- test launching: force encoding to UTF-8 because of stupid Macroman on OS X -->
   <condition property="test.sysprops.set" value="test.sysprops.mac">
     <equals arg1="${java.vendor}" arg2="Apple Inc."/>
   </condition>
   <condition property="test.sysprops.set" value="test.sysprops.other">
     <not><equals arg1="${java.vendor}" arg2="Apple Inc."/></not>
   </condition>
   <property name="mac.file.encoding" value="UTF-8"/>
   <propertyset id="test.sysprops.mac">
       <propertyref name="mac.file.encoding"/>
   </propertyset>
   <propertyset id="test.sysprops.other">
   </propertyset>
   <propertyset id="test.sysproperties">
     <propertyset refid="${test.sysprops.set}"/>
     <mapper type="glob" from="mac.*" to="*"/>
   </propertyset>
   
   <!-- if ruby.home is not set, use env var -->
   <condition property="ruby.home" value="${env.RUBY_HOME}">
     <not><isset property="ruby.home"/></not>
   </condition>
   <condition property="is.windows" value="true">
     <os family="windows"/>
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
   <condition property="spec.jvm.model.option" value="">
     <equals arg1="${run.jvm.model}" arg2=""/>
   </condition>
 
   <tstamp><format property="build.date" pattern="yyyy-MM-dd"/></tstamp>
   <property environment="env"/>
   <property name="version.ruby" value="${version.ruby.major}.${version.ruby.minor}"/>
   <property name="spec.jvm.model.option" value="-T -J${run.jvm.model}"/>
   <property name="rdoc.archive" value="docs/rdocs.tar.gz"/>
 
   <!-- Initializes the build -->
   <target name="init" unless="init.hasrun">
     <!-- do not unpack rdocs if they're already there -->
     <uptodate property="extract-rdocs.hasrun" srcfile="${rdoc.archive}" targetfile="${basedir}/share/ri.jar"/>
 
     <!-- do not generate ri cache if it's already generated -->
     <uptodate property="generate-ri-cache.hasrun" srcfile="${rdoc.archive}" targetfile="${basedir}/share/ri_cache.bin.gz"/>
 
     <!-- do not unpack native libs if they're already there -->
     <uptodate property="native-libs-uptodate" targetfile="${lib.dir}/native/libs.OK">
       <srcfiles dir= "build_lib" includes="jffi-*.jar"/>
     </uptodate>
 
     <!-- set appropriate spec tag filter if on windows -->
     <condition property="spec.windows.flag" value="-g windows">
       <os family="windows"/>
     </condition>
 
     <!-- if bin/jruby does not exist, copy from bin/jruby.bash -->
     <condition property="prepare-bin-jruby.hasrun" value="true">
       <available file="${basedir}/bin/jruby"/>
     </condition>
     <antcall target="prepare-bin-jruby"/>
 
     <!-- Checks if specific libs and versions are avaiable -->
     <available property="sun-misc-signal"
                classname="sun.misc.Signal"/>
 
     <property name="init.hasrun" value="true"/>
   </target>
 
   <!-- Prepares a bin/jruby executable from bin/jruby.bash -->
   <target name="prepare-bin-jruby" unless="prepare-bin-jruby.hasrun">
     <exec executable="/bin/sh" osfamily="unix">
       <arg line="-c 'test -f &quot;${basedir}/bin/jruby&quot; || cp &quot;${basedir}/bin/jruby.bash&quot; &quot;${basedir}/bin/jruby&quot;'"/>
     </exec>
     <chmod perm="755" file="${basedir}/bin/jruby"/>
   </target>
 
   <!-- Creates the directories needed for building -->
   <target name="prepare" depends="init, create-dirs, copy-resources, extract-rdocs, update-constants">
   </target>
   
   <target name="create-dirs" unless="create-dirs.hasrun">
     <mkdir dir="${build.dir}"/>
     <mkdir dir="${classes.dir}"/>
     <mkdir dir="${jruby.classes.dir}"/>
     <mkdir dir="${test.classes.dir}"/>
     <mkdir dir="${test.results.dir}"/>
     <mkdir dir="${html.test.results.dir}"/>
     <mkdir dir="${docs.dir}"/>
     <mkdir dir="${api.docs.dir}"/>
     <property name="create-dirs.hasrun" value="true"/>
   </target>
 
   <target name="copy-resources" unless="copy-resources.hasrun">
     <copy todir="${jruby.classes.dir}" preservelastmodified="true">
         <fileset dir="${src.dir}">
             <include name="**/*.rb"/>
         </fileset>
     </copy>
     <copy todir="${jruby.classes.dir}/builtin" preservelastmodified="true">
         <fileset dir="${lib.dir}/ruby/site_ruby/shared/builtin">
             <include name="**/*.rb"/>
             <include name="**/*.jar"/>
         </fileset>
     </copy>
     <property name="copy-resources.hasrun" value="true"/>
   </target>
 
   <!-- extract rdoc archive into share/ -->
   <target name="extract-rdocs" depends="init" unless="extract-rdocs.hasrun">
     <untar src="${rdoc.archive}" dest="${basedir}" compression="gzip"/>
     <jar file="${basedir}/share/ri.jar">
       <fileset dir="${basedir}/share" includes="ri/**/*"/>
     </jar>
     <property name="RDOCS_EXTRACTED" value="true"/>
   </target>
 
   <target name="update-constants" unless="update-constants.hasrun">
     <exec osfamily="unix" executable="git" outputproperty="jruby.revision" failonerror="false" failifexecutionfails="false">
       <arg line="log -1 --format=format:%h"/>
     </exec>
     <exec osfamily="windows" executable="cmd" outputproperty="jruby.revision" failonerror="false" failifexecutionfails="false">
       <arg line="/c git log -1 --format=format:%h"/>
     </exec>
     
     <!-- avoid overwriting if it hasn't changed, to avoid recompiles -->
     <copy file="${basedir}/src_gen/org/jruby/runtime/Constants.java"
         tofile="${basedir}/src_gen/org/jruby/runtime/Constants.java.old"
         preservelastmodified="true" failonerror="false"/>
     <copy file="${basedir}/src/org/jruby/runtime/Constants.java"
         overwrite="true"
         tofile="${basedir}/src_gen/org/jruby/runtime/Constants.java.gen" >
         <filterset>
             <filter token="version.ruby.major" value="${version.ruby.major}"/>
             <filter token="version.ruby" value="${version.ruby}"/>
             <filter token="version.ruby.patchlevel" value="${version.ruby.patchlevel}"/>
             <filter token="version.ruby1_9.major" value="${version.ruby1_9.major}"/>
             <filter token="version.ruby1_9" value="${version.ruby1_9}"/>
             <filter token="version.ruby1_9.patchlevel" value="${version.ruby1_9.patchlevel}"/>
             <filter token="version.ruby1_9.revision" value="${version.ruby1_9.revision}"/>
             <filter token="build.date" value="${build.date}"/>
             <filter token="version.jruby" value="${version.jruby}"/>
             <filter token="java.specification.version" value="${java.specification.version}"/>
             <filter token="javac.version" value="${javac.version}"/>
             <filter token="os.arch" value="${os.arch}"/>
             <filter token="jruby.revision" value="${jruby.revision}"/>
         </filterset>
     </copy>
 
     <condition property="constants.java.is.same">
       <filesmatch file1="${basedir}/src_gen/org/jruby/runtime/Constants.java.gen" file2="${basedir}/src_gen/org/jruby/runtime/Constants.java.old" textfile="true"/>
     </condition>
     <antcall target="_uc_internal_"/>
     
     <property name="update-constants.hasrun" value="true"/>
   </target>
   
   <target name="_uc_internal_" unless="constants.java.is.same">
     <copy file="${basedir}/src_gen/org/jruby/runtime/Constants.java.gen" tofile="${basedir}/src_gen/org/jruby/runtime/Constants.java"/>
     <javac destdir="${jruby.classes.dir}" debug="true" srcdir="${basedir}/src_gen"
         includes="org/jruby/runtime/Constants.java" source="${javac.version}"
         target="${javac.version}" deprecation="true" encoding="UTF-8"
         includeantruntime="false"/>
   </target>
 
   <target name="compile-annotation-binder">
     <mkdir dir="${basedir}/src_gen"/>
 
     <javac destdir="${jruby.classes.dir}" debug="true" srcdir="${src.dir}"
         sourcepath="" classpathref="build.classpath" source="${javac.version}"
         target="${javac.version}" deprecation="true" encoding="UTF-8"
         includeantruntime="false">
       <include name="org/jruby/anno/FrameField.java"/>
       <include name="org/jruby/anno/AnnotationBinder.java"/>
       <include name="org/jruby/anno/JRubyMethod.java"/>
       <include name="org/jruby/anno/FrameField.java"/>
       <include name="org/jruby/CompatVersion.java"/>
       <include name="org/jruby/runtime/Visibility.java"/>
       <include name="org/jruby/util/CodegenUtils.java"/>
     </javac>
   </target>
 
   <target name="compile" depends="compile-jruby, generate-method-classes, generate-unsafe"
           description="Compile the source files for the project.">
   </target>
 
   <target name="compile-jruby" depends="prepare, compile-annotation-binder" unless="compiled">
     <!-- Generate binding logic ahead of time -->
     <apt factory="org.jruby.anno.AnnotationBinder" destdir="${jruby.classes.dir}"
         debug="true" source="${javac.version}" target="${javac.version}"
         deprecation="true" encoding="UTF-8" includeantruntime="true">
       <classpath refid="jruby.execute.classpath"/>
       <src path="${src.dir}"/>
       <exclude name="org/jruby/runtime/Constants.java"/>
       <patternset refid="java.src.pattern"/>
       <compilerarg line="-XDignore.symbol.file=true"/>
       <compilerarg line="-J-Xmx${jruby.compile.memory}"/>
     </apt>
 
     <!-- Apply any instrumentation that is enabled (code coverage, etc) -->
     <antcall target="instrument"/>
 
     <property name="compiled" value="true"/>
   </target>
 
   <target name="generate-method-classes">
     <available file="src_gen/annotated_classes.txt" property="annotations.changed"/>
     <antcall target="_gmc_internal_"/>
   </target>
 
   <target name="_gmc_internal_" if="annotations.changed">
     <echo message="Generating invokers..."/>
     <java classname="org.jruby.anno.InvokerGenerator" fork="true" failonerror="true">
       <classpath refid="jruby.execute.classpath"/>
       <jvmarg value="${run.jvm.model}"/>
       <!-- uncomment this line when building on a JVM with invokedynamic
       <jvmarg line="-XX:+InvokeDynamic"/>
       -->
       <sysproperty key="jruby.bytecode.version" value="${javac.version}"/>
       <arg value="src_gen/annotated_classes.txt"/>
       <arg value="${jruby.classes.dir}"/>
     </java>
 
     <echo message="Compiling populators..."/>
     <javac destdir="${jruby.classes.dir}" debug="true" source="${javac.version}"
         target="${javac.version}" deprecation="true" encoding="UTF-8"
         includeantruntime="true">
       <classpath refid="jruby.execute.classpath"/>
       <src path="src_gen"/>
       <patternset refid="java.src.pattern"/>
     </javac>
 
     <delete file="src_gen/annotated_classes.txt"/>
   </target>
 
   <target name="generate-unsafe">
       <available file="${jruby.classes.dir}/org/jruby/util/unsafe/GeneratedUnsafe.class" property="unsafe.not.needed"/>
       <antcall target="_gu_internal_"/>
   </target>
 
   <target name="_gu_internal_" unless="unsafe.not.needed">
     <echo message="Generating Unsafe impl..."/>
     <java classname="org.jruby.util.unsafe.UnsafeGenerator" fork="true" failonerror="true">
       <classpath refid="jruby.execute.classpath"/>
       <jvmarg value="${run.jvm.model}"/>
       <!-- uncomment this line when building on a JVM with invokedynamic
            <jvmarg line="-XX:+InvokeDynamic"/>
            -->
       <arg value="org.jruby.util.unsafe"/>
       <arg value="${jruby.classes.dir}/org/jruby/util/unsafe"/>
     </java>
   </target>
 
   <target name="unzip-native-libs" unless="native-libs-uptodate">
       <unzip-native-libs destination.dir="${lib.dir}/native"/>
       <echo file="${lib.dir}/native/libs.OK"/>
   </target>
 
   <target name="jar-jruby" depends="init, compile, unzip-native-libs" unless="jar-up-to-date, jar-jruby.hasrun">
       <jar destfile="${lib.dir}/jruby.jar" compress="true" index="true" update="true">
         <fileset dir="${jruby.classes.dir}"/>
-        <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
+        <zipfileset src="${asm.jar}"/>
+        <zipfileset src="${asm-commons.jar}"/>
+        <zipfileset src="${asm-util.jar}"/>
+        <zipfileset src="${asm-analysis.jar}"/>
+        <zipfileset src="${asm-tree.jar}"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-netdb.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jgrapht-jdk1.5.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-${joda.time.version}.jar"/>
         <zipfileset src="${build.lib.dir}/snakeyaml-1.7.jar"/>
         <zipfileset src="${build.lib.dir}/yydebug.jar"/>
         <zipfileset src="${build.lib.dir}/nailgun-0.7.1.jar"/>
         <metainf dir="${base.dir}/spi">
           <include name="services/**"/>
         </metainf>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jar>
 
       <antcall target="add-emma-jars"/>
       <antcall target="generate-ri-cache"/>
 
       <property name="jar-jruby.hasrun" value="true"/>
   </target>
 
   <target name="add-emma-jars" if="coverage.enabled">
     <jar destfile="${lib.dir}/jruby.jar" compress="true" index="true" update="true">
       <zipfileset src="${build.lib.dir}/emma.jar"/>
     </jar>
   </target>
 
   <target name="generate-ri-cache" unless="generate-ri-cache.hasrun">
       <echo message="Using JRuby to generate RI cache to ${basedir}/share"/>
       <property name="jruby.home" value="${basedir}"/>
       <java classname="org.jruby.Main" resultproperty="ri.output" output="false" failonerror="true">
         <classpath refid="jruby.execute.classpath"/>
         <sysproperty key="jruby.home" value="${basedir}"/>
         <arg value="-S"/><arg value="ri"/><arg value="String"/>
       </java>
       <property name="generate-ri-cache.hasrun" value="true"/>
   </target>
 
   <target name="jar-jruby-light" depends="compile" unless="jar-up-to-date">
       <jar destfile="${lib.dir}/jruby-light.jar" compress="true" index="true">
         <fileset dir="${jruby.classes.dir}">
             <exclude name="**/*$Populator.class"/>
             <exclude name="**/*INVOKER*.class"/>
             <exclude name="**/AnnotationBinder*.class"/>
             <exclude name="**/InvokerGenerator.class"/>
             <exclude name="org/jruby/ext/ffi/**/*"/>
             <exclude name="org/jruby/embed/**/*"/>
         </fileset>
-        <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
+        <zipfileset src="${asm.jar}"/>
+        <zipfileset src="${asm-util.jar}"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-netdb.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-${joda.time.version}.jar"/>
         <manifest>
           <attribute name="Built-By" value="${user.name}"/>
           <attribute name="Main-Class" value="org.jruby.Main"/>
         </manifest>
       </jar>
       
       <antcall target="generate-ri-cache"/>
   </target>
 
   <target name="jar-jruby-dist" depends="compile" unless="jar-up-to-date">
       <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
       <jarjar destfile="${lib.dir}/jruby.jar" compress="true">
         <fileset dir="${jruby.classes.dir}"/>
-        <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
-        <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
+        <zipfileset src="${asm.jar}"/>
+        <zipfileset src="${asm-commons.jar}"/>
+        <zipfileset src="${asm-util.jar}"/>
+        <zipfileset src="${asm-analysis.jar}"/>
+        <zipfileset src="${asm-tree.jar}"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-netdb.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/jffi.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-ppc64-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-x86_64-Windows.jar"/>
         <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
         <zipfileset src="${build.lib.dir}/jgrapht-jdk1.5.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-${joda.time.version}.jar"/>
         <zipfileset src="${build.lib.dir}/snakeyaml-1.7.jar"/>
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
         <param name="bar.wrap" value="${lib.dir}/jruby.bar" />
       </antcall>
       
       <antcall target="generate-ri-cache"/>
   </target>
 
   <!-- Use Bnd to wrap the JAR generated by jarjar in above task -->
   <target name="_osgify-jar_">
     <filter token="JRUBY_VERSION" value="${version.jruby}"/>
     <copy file="${basedir}/jruby.bnd.template" tofile="${build.dir}/${bndfile}" filtering="true"/>
     <taskdef resource="aQute/bnd/ant/taskdef.properties"
       classpath="${build.lib.dir}/bnd.jar"/>
     <bndwrap definitions="${build.dir}" output="${dest.lib.dir}">
       <fileset file="${jar.wrap}" />
     </bndwrap>
     <move file="${bar.wrap}" tofile="${jar.wrap}"
       overwrite="true" />
   </target>
 
   <target name="jar-complete" depends="compile" description="Create the 'complete' JRuby jar. Pass 'mainclass' and 'filename' to adjust.">
     <property name="mainclass" value="org.jruby.Main"/>
     <property name="filename" value="jruby-complete.jar"/>
     <property name="bilename" value="jruby-complete.bar"/>
     <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
     <property name="jar-complete-home" value="${build.dir}/jar-complete/META-INF/jruby.home"/>
     <mkdir dir="${jar-complete-home}"/>
     <copy todir="${jar-complete-home}">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
 
     <copy todir="${build.dir}/jar-complete/cext">
       <fileset dir="${lib.dir}/native">
         <patternset refid="dist.jruby-cext-native.files"/>
       </fileset>
     </copy>
 
     <java classname="${mainclass}" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath>
         <path refid="jruby.execute.classpath"/>
         <pathelement location="${build.dir}/jar-complete"/>
       </classpath>
       <sysproperty key="jruby.home" value="${jar-complete-home}"/>
       <env key="RUBYOPT" value=""/>
       <env key="GEM_HOME" value="${jar-complete-home}/lib/ruby/gems/1.8"/>
       <jvmarg value="${run.jvm.model}"/>
       <arg line="-S gem install ${complete.jar.gems}"/>
       <arg line="--no-ri --no-rdoc --ignore-dependencies --env-shebang"/>
     </java>
 
     <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
     <jarjar destfile="${dest.lib.dir}/${filename}">
       <fileset dir="${jruby.classes.dir}"/>
       <fileset dir="${build.dir}/jar-complete">
         <exclude name="META-INF/jruby.home/lib/ruby/site_ruby/shared/builtin/**"/>
       </fileset>
-      <zipfileset src="${build.lib.dir}/asm-3.2.jar"/>
-      <zipfileset src="${build.lib.dir}/asm-commons-3.2.jar"/>
-      <zipfileset src="${build.lib.dir}/asm-util-3.2.jar"/>
-      <zipfileset src="${build.lib.dir}/asm-analysis-3.2.jar"/>
-      <zipfileset src="${build.lib.dir}/asm-tree-3.2.jar"/>
+      <zipfileset src="${asm.jar}"/>
+      <zipfileset src="${asm-commons.jar}"/>
+      <zipfileset src="${asm-util.jar}"/>
+      <zipfileset src="${asm-analysis.jar}"/>
+      <zipfileset src="${asm-tree.jar}"/>
       <zipfileset src="${build.lib.dir}/bytelist.jar"/>
       <zipfileset src="${build.lib.dir}/constantine.jar"/>
       <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
       <zipfileset src="${build.lib.dir}/jcodings.jar"/>
       <zipfileset src="${build.lib.dir}/joni.jar"/>
       <zipfileset src="${build.lib.dir}/jnr-netdb.jar"/>
       <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
       <zipfileset src="${build.lib.dir}/jaffl.jar"/>
       <zipfileset src="${build.lib.dir}/jffi.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-i386-Linux.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-x86_64-Linux.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-Darwin.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-i386-SunOS.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-x86_64-SunOS.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-ppc-AIX.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-ppc-Linux.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-ppc64-Linux.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-sparc-SunOS.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-sparcv9-SunOS.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-i386-FreeBSD.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-x86_64-FreeBSD.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-i386-OpenBSD.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-x86_64-OpenBSD.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-i386-Windows.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-x86_64-Windows.jar"/>
       <zipfileset src="${build.lib.dir}/jffi-s390x-Linux.jar"/>
       <zipfileset src="${build.lib.dir}/jgrapht-jdk1.5.jar"/>
       <zipfileset src="${build.lib.dir}/joda-time-${joda.time.version}.jar"/>
       <zipfileset src="${build.lib.dir}/snakeyaml-1.7.jar"/>
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
       <param name="bar.wrap" value="${dest.lib.dir}/${bilename}" />
     </antcall>
   </target>
 
   <target name="test-jar-complete" depends="init,unzip-native-libs,jar-complete"
           description="Do some sanity checks on the complete jar.">
     <java jar="lib/jruby-complete.jar" fork="true" failonerror="true" outputproperty="jar-complete-gems">
       <arg line="-v -S gem list"/>
     </java>
     <echo message="${jar-complete-gems}"/>
     <fail>
       <condition>
         <not><and>
           <contains string="${jar-complete-gems}" substring="rake"/>
           <contains string="${jar-complete-gems}" substring="rspec"/>
           <contains string="${jar-complete-gems}" substring="sources"/>
         </and></not>
       </condition>
     </fail>
     <java jar="lib/jruby-complete.jar" fork="true" failonerror="true" outputproperty="jar-complete-rake">
       <arg line="-v -S rake --help"/>
     </java>
     <echo message="${jar-complete-rake}"/>
     <fail>
       <condition>
         <not><and>
           <contains string="${jar-complete-rake}" substring="rakefile"/>
           <contains string="${jar-complete-rake}" substring="dry-run"/>
         </and></not>
       </condition>
     </fail>
   </target>
 
   <target name="dist-jar-complete" depends="jar-complete">
     <mkdir dir="${dist.dir}"/>
     <move file="${dest.lib.dir}/jruby-complete.jar" tofile="${dist.dir}/jruby-complete-${version.jruby}.jar"/>
     <checksum file="${dist.dir}/jruby-complete-${version.jruby}.jar" algorithm="md5"/>
     <checksum file="${dist.dir}/jruby-complete-${version.jruby}.jar" algorithm="sha1"/>
   </target>
 
   <target name="jar-console" depends="compile" description="Create the jruby graphical console jar">
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
         <path refid="jruby.execute.classpath"/>
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
 
   <target name="compile-stdlib" unless="test">
     <copy todir="${build.dir}/stdlib">
         <fileset dir="${basedir}/lib/ruby/1.8">
           <include name="**/*.rb"/>
         </fileset>
     </copy>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true" dir="${build.dir}/stdlib">
       <classpath refid="jruby.execute.classpath"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.objectspace.enabled" value="true"/>
       <jvmarg line="-ea ${run.jvm.model}"/>
       <arg line="-I bin/ -S jrubyc ."/>
     </java>
   </target>
 
   <target name="coverage" description="Enable code coverage reporting for built artifacts.">
     <property name="coverage.enabled" value="true"/>
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
 
   <target name="instrument" depends="emma" if="coverage.enabled">
     <emma enabled="${emma.enabled}" >
       <instr instrpathref="classes_to_instrument"
              mode="overwrite"
              metadatafile="${test.results.dir}/metadata.emma"
              merge="false">
         <filter excludes="*INVOKER*"/>
       </instr>
     </emma>
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
 
   <target name="install-gems" depends="install-dist-gems,install-jruby-launcher-gem"/>
 
   <target name="install-dist-gems" depends="jar" unless="install-dist-gems.hasrun">
     <available property="dist.gems.installed" value="true"
         file="${basedir}/lib/ruby/gems/1.8/gems/rake-0.8.7"/>
     <antcall target="_idg_internal_"/>
     <property name="install-dist-gems.hasrun" value="true"/>
   </target>
 
   <target name="_idg_internal_" unless="dist.gems.installed">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="--command maybe_install_gems ${dev.gems}"/>
     </java>
   </target>
 
   <target name="install-jruby-launcher-gem" depends="jar" unless="install-jruby-launcher-gem.hasrun">
     <available property="dist.gems.installed" value="true"
         file="${basedir}/lib/ruby/gems/1.8/gems/jruby-launcher-1.0.3-java"/>
     <antcall target="_idg_internal_"/>
     <property name="install-jruby-launcher-gem.hasrun" value="true"/>
   </target>
 
   <target name="_ijlg_internal_" unless="jruby.launcher.gem.installed">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="--command maybe_install_gems ${jruby.launcher.gem}"/>
     </java>
   </target>
 
   <target name="test" depends="
     copy-test-files,
     install-gems,
     run-junit-interpreted-short,
     test-rake-targets"
     description="Runs unit tests.">
   </target>
 
   <target name="test-indy" depends="
     copy-test-files,
     install-gems,
     run-junit-indy-short,
     test-rake-targets"
     description="Runs unit tests.">
   </target>
 
   <target name="test-extended" depends="
     copy-test-files,
     install-gems,
     run-junit-compiled,
     run-junit-embed,
     test-rake-targets"
     description="Runs unit tests.">
   </target>
 
   <target name="test-compiled" depends="copy-test-files,run-junit-compiled,run-junit-precompiled"/>
   <target name="test-precompiled" depends="copy-test-files,run-junit-precompiled"/>
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
   <target name="run-junit-interpreted-short"><run-junit-1.8-short/></target>
   <target name="run-junit-indy-short"><run-junit-1.8-indy-short/></target>
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
     <pathelement path="${build.lib.dir}/junit.jar"/>
     <pathelement path="${build.lib.dir}/livetribe-jsr223-2.0.6.jar"/>
     <pathelement path="${build.lib.dir}/bsf.jar"/>
     <pathelement path="${build.lib.dir}/commons-logging-1.1.1.jar"/>
     <pathelement path="${java.class.path}"/>
     <pathelement path="${lib.dir}/jruby.jar"/>
     <pathelement location="${test.classes.dir}"/>
     <pathelement path="${test.dir}/requireTest.jar"/>
     <pathelement location="${test.dir}"/>
   </path>
 
   <macrodef name="run-junit-indy">
     <attribute name="jruby.version" default="ruby1_8"/>
     <attribute name="compile.mode" default="-X+O"/>
     <attribute name="jit.threshold" default="0"/>
     <attribute name="jit.maxsize" default="1000000000"/>
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
 
         <sysproperty key="jruby.compile.invokedynamic" value="true"/>
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
 
         <syspropertyset refid="test.sysproperties"/>
 
         <!-- force client VM to improve test run times AND -->
         <!-- set a larger max permgen, since the tests load a lot of bytecode -->
         <jvmarg line="-ea ${run.jvm.model} -client -XX:MaxPermSize=256M -XX:+UnlockExperimentalVMOptions -XX:+EnableMethodHandles -XX:+EnableInvokeDynamic"/>
 
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
 
         <syspropertyset refid="test.sysproperties"/>
 
         <!-- force client VM to improve test run times AND -->
         <!-- set a larger max permgen, since the tests load a lot of bytecode -->
         <jvmarg line="-ea ${run.jvm.model} -client -XX:MaxPermSize=256M"/>
 
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
   <macrodef name="run-junit-1.8-indy-short">
     <sequential>
       <run-junit-indy>
 	<junit-tests>
 	  <test name="${test}" if="test"/>
 	  <test name="org.jruby.test.ScriptTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.JRubyTestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MRITestSuite" todir="${test.results.dir}" unless="test"/>
 	  <test name="org.jruby.test.MainTestSuite" todir="${test.results.dir}" unless="test"/>
 	</junit-tests>
       </run-junit-indy>
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
       <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" classpath="${build.lib.dir}/junit.jar"/>
       <junit jvm="${jruby.test.jvm}" fork="yes" forkMode="once" haltonfailure="true" dir="${basedir}" maxmemory="${jruby.test.memory}" showoutput="true" timeout="1800000">
         <classpath refid="test.class.path"/>
         <syspropertyset refid="test.sysproperties"/>
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
       <!-- Note the extra '=' in the value, it *is* intentional -->
       <sysproperty key="java.security.policy" value="=file:test/restricted.policy"/>
       <sysproperty key="java.awt.headless" value="true"/>
       <jvmarg value="${run.jvm.model}"/>
       <arg line="-ve"/>
       <arg value="puts 'Restricted policy looks ok'"/>
     </java>
   </target>
 
   <target name="test-rake-targets" depends="init"><rake task="test:rake_targets"/></target>
 
   <macrodef name="rake">
     <attribute name="compile.mode" default="OFF"/>
     <attribute name="jit.threshold" default="20"/>
     <attribute name="jit.max" default="-1"/>
     <attribute name="objectspace.enabled" default="true"/>
     <attribute name="thread.pooling" default="false"/>
     <attribute name="reflection" default="false"/>
     <attribute name="inproc" default="true"/>
     <attribute name="jruby.args" default=""/>
     <attribute name="task"/>
     <attribute name="dir" default="${base.dir}"/>
     <attribute name="jvm.args" default="-ea"/>
 
     <sequential>
       <echo message="Running rake @{task}"/>
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
 
         <arg line="@{jruby.args} -S rake @{task} ${rake.args}"/>
       </java>
     </sequential>
   </macrodef>
 
   <!-- Utilities to manage rubyspecs/mspec files -->
   <target name="fetch-stable-specs" depends="init">
     <rake task="spec:fetch_stable_specs"/>
   </target>
   <target name="fetch-latest-specs" depends="init">
     <rake task="spec:fetch_latest_specs"/>
   </target>
 
   <!-- Main spec runs (See rakelib/spec.rake for definitions/descriptions) -->
   <target name="spec:ci_18" depends="install-gems"><rake task="spec:ci_18"/></target>
   <target name="spec:ci_19" depends="install-gems"><rake task="spec:ci_19"/></target>
   <target name="spec:ci_interpreted_18_19" depends="install-gems"><rake task="spec:ci_interpreted_18_19"/></target>
   <target name="spec:ci_interpreted_18" depends="install-gems"><rake task="spec:ci_interpreted_18"/></target>
   <target name="spec:ci_compiled_18" depends="install-gems"><rake task="spec:ci_compiled_18"/></target>
   <target name="spec:ci_precompiled_18" depends="install-gems"><rake task="spec:ci_precompiled_18"/></target>
   <target name="spec:ci_interpreted_19" depends="install-gems"><rake task="spec:ci_interpreted_19"/></target>
   <target name="spec:ci_all_precompiled_18" depends="install-gems"><rake task="spec:ci_all_precompiled_18"/></target>
   <target name="spec:ci_all_interpreted_18" depends="install-gems"><rake task="spec:ci_all_interpreted_18"/></target>
   <target name="spec:ci_all" depends="install-gems"><rake task="spec:ci_all"/></target>
   <target name="spec:ci_latest" depends="install-gems"><rake task="spec:ci_latest"/></target>
   <target name="spec:ci_latest_19" depends="install-gems"><rake task="spec:ci_latest_19"/></target>
   <target name="spec:ci_cext" depends="install-gems,build-jruby-cext-native"><rake task="spec:ci_cext"/></target>
 
   <!-- Old compatibility targets which call to newer Rake-based ones -->
   <target name="spec" depends="spec:ci_18"/>
   <target name="spec-ci" depends="spec:ci_18"/>
   <target name="spec-short" depends="spec:ci_interpreted_18_19"/>
   <target name="spec-ci-1.9" depends="spec:ci_19"/>
   <target name="spec-short-1.9" depends="spec:ci_interpreted_19"/>
   <target name="spec-all" depends="spec:ci_all_precompiled_18"/>
   <target name="spec-all-interpreted" depends="spec:ci_all_interpreted_18"/>
 
   <!-- For CI, because target names become filesystem paths with matrix builds -->
   <target name="spec-ci_interpreted_18" depends="spec:ci_interpreted_18"></target>
   <target name="spec-ci_compiled_18" depends="spec:ci_compiled_18"></target>
   <target name="spec-ci_precompiled_18" depends="spec:ci_precompiled_18"></target>
   <target name="spec-ci_interpreted_19" depends="spec:ci_interpreted_19"></target>
 
   <!-- latest, unstable specs -->
   <target name="spec-latest-all" depends="spec:ci_all"/>
   <target name="spec-latest" depends="spec:ci_latest"/>
   <target name="spec-cext" depends="spec:ci_cext"/>
 
   <target name="update-excludes">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${spec.dir}">
       <classpath refid="jruby.execute.classpath"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <sysproperty key="jruby.launch.inproc" value="false"/>
 
       <arg line="${mspec.dir}/bin/mspec tag"/>
       <arg line="--add fails --fail -B ${spec.dir}/default.mspec"/>
     </java>
   </target>
 
   <target name="spec-show-excludes" depends="prepare"
     description="Prints out all currently excluded rubyspecs.">
     <spec-show-excludes/>
   </target>
 
   <target name="spec-show-excludes-1.9" depends="prepare"
     description="Prints out all currently excluded rubyspecs in 1.9 mode.">
     <spec-show-excludes spec.mode="1.9"/>
   </target>
 
   <macrodef name="spec-show-excludes">
     <attribute name="spec.mode" default="1.8"/>
 
     <sequential>
       <available property="mspec-available" file="${mspec.dir}/bin/mspec"/>
       <fail unless="mspec-available"
         message="No rubyspecs found. Download them via 'ant spec'."/>
 
       <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false" dir="${spec.dir}">
         <classpath refid="jruby.execute.classpath"/>
         <sysproperty key="jruby.home" value="${basedir}"/>
         <sysproperty key="jruby.launch.inproc" value="false"/>
         <arg line="${mspec.dir}/bin/mspec"/>
         <arg line="-f s -g fails -g critical ${spec.windows.flag} --dry-run"/>
         <arg line="-T--@{spec.mode}"/>
         <arg line="-B ${spec.dir}/jruby.@{spec.mode}.mspec"/>
         <arg value=":ci_files"/>
       </java>
     </sequential>
   </macrodef>
 
   <macrodef name="fixEOLs">
     <attribute name="dist-stage-dir" default="${dist.stage.bin.dir}"/>
     <sequential>
       <fixcrlf srcdir="@{dist-stage-dir}/bin" excludes="*.bat,*.exe,*.dll" eol="lf"/>
       <fixcrlf srcdir="@{dist-stage-dir}/bin" includes="*.bat" eol="crlf"/>
     </sequential>
   </macrodef>
 
   <macrodef name="unzip-native-libs">
     <attribute name="destination.dir"/>
     <sequential>
       <mkdir dir="@{destination.dir}"/>
       <unzip dest="@{destination.dir}">
         <fileset dir="build_lib">
             <include name="jffi-*.jar"/>
         </fileset>
         <patternset>
             <include name="**/libjffi-*.so"/>
             <include name="**/libjffi-*.a"/>
             <include name="**/libjffi-*.jnilib"/>
             <include name="**/jffi-*.dll"/>
         </patternset>
         <mapper>
             <filtermapper>
                 <replacestring from="jni/" to="./"/>
             </filtermapper>
         </mapper>
       </unzip>
     </sequential>
   </macrodef>
 
   <target name="apidocs" depends="prepare"
           description="Creates the Java API docs">
     <javadoc destdir="${api.docs.dir}" author="true" version="true" use="true"
              windowtitle="JRuby API" source="${javac.version}" useexternalfile="true"
              maxmemory="256m">
       <fileset dir="${src.dir}">
         <include name="**/*.java"/>
       </fileset>
       <classpath refid="build.classpath"/>
       <doctitle><![CDATA[<h1>JRuby</h1>]]></doctitle>
       <bottom><![CDATA[<i>Copyright &#169; 2002-2009 JRuby Team. All Rights Reserved.</i>]]></bottom>
     </javadoc>
   </target>
 
   <patternset id="dist.bindir.files">
     <exclude name="bin/jruby"/>
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
     <exclude name="test/testapp/testapp"/>
     <exclude name="test/testapp/testapp.exe"/>
     <include name="spec/**"/>
     <exclude name="spec/mspec"/>
     <exclude name="spec/ruby"/>
     <include name="tool/**"/>
     <exclude name="tool/nailgun/**"/>
     <include name="build_lib/**"/>
     <include name="Rakefile"/>
     <include name="build.xml"/>
     <include name="ivy/**"/>
     <include name="rakelib/**"/>
     <include name="nbproject/*"/>
     <include name=".project"/>
     <include name=".classpath"/>
     <include name="default.build.properties"/>
     <include name="jruby.bnd.template"/>
     <include name="pom.xml"/>
     <include name="maven/**"/>
     <include name="gem/**"/>
     <exclude name="lib/jruby*.jar"/>
   </patternset>
 
   <!-- note: tool/nailgun/configure must be added separately -->
   <patternset id="dist.nailgun.files">
     <include name="tool/nailgun/Makefile.in"/>
     <include name="tool/nailgun/README.txt"/>
     <include name="tool/nailgun/ng.exe"/>
     <include name="tool/nailgun/src/**"/>
   </patternset>
 
   <patternset id="dist.jruby-cext-native.files">
     <include name="**/libjruby-cext.so"/>
     <include name="**/libjruby-cext.a"/>
     <include name="**/libjruby-cext.jnilib"/>
     <include name="**/jruby-cext.dll"/>
   </patternset>
 
   <target name="dist-bin" depends="build-jruby-cext-native,jar-dist">
     <mkdir dir="${dist.stage.bin.dir}"/>
     <copy todir="${dist.stage.bin.dir}">
       <fileset dir="${basedir}">
         <patternset refid="dist.bindir.files"/>
         <patternset refid="dist.lib.files"/>
       </fileset>
     </copy>
     <!-- dist-bin by default does not come w/ unix native launcher, so -->
     <!-- move ${dist.stage.bin.dir}/jruby.bash to ${dist.stage.bin.dir}/jruby -->
     <move file="${dist.stage.bin.dir}/bin/jruby.bash" tofile="${dist.stage.bin.dir}/bin/jruby"/>
     <mkdir dir="${dist.stage.bin.dir}/lib/native"/>
     <unzip-native-libs destination.dir="${dist.stage.bin.dir}/lib/native"/>
     <fixEOLs dist-stage-dir="${dist.stage.bin.dir}"/>
     <rake task="install_dist_gems"/>
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
     <rake task="install_dist_gems"/>
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
       <tarfileset dir="${base.dir}" prefix="jruby-${version.jruby}">
           <include name="spi/**"/>
       </tarfileset>
       <tarfileset dir="${basedir}" mode="755" prefix="jruby-${version.jruby}">
           <include name="test/testapp/testapp"/>
           <include name="test/testapp/testapp.exe"/>
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
       <zipfileset dir="${base.dir}" prefix="jruby-${version.jruby}">
 	<include name="spi/**"/>
       </zipfileset>
       <zipfileset dir="${basedir}" filemode="755" prefix="jruby-${version.jruby}">
         <include name="test/testapp/testapp"/>
         <include name="test/testapp/testapp.exe"/>
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
     <unzip dest="${snapshot.install.dir}" src="${dist.dir}/jruby-bin-${version.jruby}.zip"/>
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
 
   <target name="dist" depends="install-dist-gems,dist-bin,dist-src,dist-jar-complete">
     <rake task="gem"/>
     <rake task="installer"/>
   </target>
 
   <target name="dist-clean">
     <delete dir="${dist.dir}"/>
     <delete dir="${basedir}/gem/pkg"/>
   </target>
 
   <target name="gem" depends="dist-jar-complete"><rake task="gem"/></target>
   <target name="installer"><rake task="installer"/></target>
 
   <target name="clean" depends="init" description="Cleans almost everything, leaves downloaded specs">
     <delete dir="${build.dir}"/>
     <delete dir="${dist.dir}"/>
     <delete quiet="false">
         <fileset dir="${lib.dir}" includes="jruby*.jar"/>
     </delete>
     <delete dir="${api.docs.dir}"/>
     <delete dir="src_gen"/>
     <antcall target="clean-ng"/>
     <antcall target="clean-tzdata" />
   </target>
 
   <!-- FIXME: Cannot run Rake on clean until minijruby so keep this target -->
   <target name="clear-specs">
       <delete dir="${rubyspec.dir}"/>
       <delete dir="${mspec.dir}"/>
       <delete file="${rubyspec.tar.file}"/>
       <delete file="${mspec.tar.file}"/>
       <delete file="${spec.dir}/rubyspecs.current.revision"/>
   </target>
 
   <target name="clean-all" depends="clean, dist-clean, clear-specs" description="Cleans everything, including dist files and specs">
       <delete dir="${base.dir}/build.eclipse"/>
       <delete dir="${base.dir}/lib/native"/>
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
       <exec executable="./configure" osfamily="unix" dir="${nailgun.home}" failonerror="true" output="${dev.null}"/>
       <echo message="Building ng client in ${nailgun.home}"/>
     <exec executable="make" osfamily="unix" dir="${nailgun.home}" output="${dev.null}"/>
   </target>
 
   <target name="need-clean-ng">
     <condition property="should-clean-ng">
       <and>
         <os family="unix"/>
         <available file="${nailgun.home}/Makefile"/>
         <available file="${nailgun.home}/ng"/>
       </and>
     </condition>
   </target>
 
   <target name="clean-ng" depends="need-clean-ng" if="should-clean-ng">
     <exec executable="make" dir="${nailgun.home}" osfamily="unix" failifexecutionfails="false" output="${dev.null}">
         <arg value="clean"/>
diff --git a/build_lib/asm-3.2.jar b/build_lib/asm-3.2.jar
deleted file mode 100644
index 334e7fdc7f..0000000000
Binary files a/build_lib/asm-3.2.jar and /dev/null differ
diff --git a/build_lib/asm-3.3.1.jar b/build_lib/asm-3.3.1.jar
new file mode 100644
index 0000000000..349f0d42bb
Binary files /dev/null and b/build_lib/asm-3.3.1.jar differ
diff --git a/build_lib/asm-analysis-3.2.jar b/build_lib/asm-analysis-3.2.jar
deleted file mode 100644
index 40ee3151cb..0000000000
Binary files a/build_lib/asm-analysis-3.2.jar and /dev/null differ
diff --git a/build_lib/asm-analysis-3.3.1.jar b/build_lib/asm-analysis-3.3.1.jar
new file mode 100644
index 0000000000..c478b6b581
Binary files /dev/null and b/build_lib/asm-analysis-3.3.1.jar differ
diff --git a/build_lib/asm-commons-3.2.jar b/build_lib/asm-commons-3.2.jar
deleted file mode 100644
index 8dfed0a9b7..0000000000
Binary files a/build_lib/asm-commons-3.2.jar and /dev/null differ
diff --git a/build_lib/asm-commons-3.3.1.jar b/build_lib/asm-commons-3.3.1.jar
new file mode 100644
index 0000000000..8a588467b0
Binary files /dev/null and b/build_lib/asm-commons-3.3.1.jar differ
diff --git a/build_lib/asm-tree-3.2.jar b/build_lib/asm-tree-3.3.1.jar
similarity index 56%
rename from build_lib/asm-tree-3.2.jar
rename to build_lib/asm-tree-3.3.1.jar
index b21fb86a92..48550be7a4 100644
Binary files a/build_lib/asm-tree-3.2.jar and b/build_lib/asm-tree-3.3.1.jar differ
diff --git a/build_lib/asm-util-3.2.jar b/build_lib/asm-util-3.3.1.jar
similarity index 50%
rename from build_lib/asm-util-3.2.jar
rename to build_lib/asm-util-3.3.1.jar
index 499d229034..0230bbcfb7 100644
Binary files a/build_lib/asm-util-3.2.jar and b/build_lib/asm-util-3.3.1.jar differ
diff --git a/maven/jruby-complete/pom.xml b/maven/jruby-complete/pom.xml
index 33f4478a98..9cb67d86b0 100644
--- a/maven/jruby-complete/pom.xml
+++ b/maven/jruby-complete/pom.xml
@@ -1,141 +1,141 @@
 <?xml version="1.0"?>
 <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
   <parent>
     <groupId>org.jruby</groupId>
     <artifactId>shared</artifactId>
     <version>1.6.0.dev</version>
     <relativePath>../../pom.xml</relativePath>
   </parent>
   <modelVersion>4.0.0</modelVersion>
   <groupId>org.jruby</groupId>
   <artifactId>jruby-complete</artifactId>
   <packaging>jar</packaging>
   <version>1.6.0.dev</version>
   <name>JRuby Complete</name>
 
   <dependencies>
     <dependency>
       <groupId>jline</groupId>
       <artifactId>jline</artifactId>
       <version>0.9.93</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.joni</groupId>
       <artifactId>joni</artifactId>
       <version>1.1.4</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm</artifactId>
-      <version>3.2</version>
+      <version>3.3.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-commons</artifactId>
-      <version>3.2</version>
+      <version>3.3.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-util</artifactId>
-      <version>3.2</version>
+      <version>3.3.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-analysis</artifactId>
-      <version>3.2</version>
+      <version>3.3.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-tree</artifactId>
-      <version>3.2</version>
+      <version>3.3.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>joda-time</groupId>
       <artifactId>joda-time</artifactId>
       <version>1.6</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jnr-netdb</artifactId>
       <version>1.0</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.ext.posix</groupId>
       <artifactId>jnr-posix</artifactId>
       <version>1.1.6</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>bytelist</artifactId>
       <version>1.0.6</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>constantine</artifactId>
       <version>0.6</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jffi</artifactId>
       <version>1.0.1</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jaffl</artifactId>
       <version>0.5.5</version>
       <scope>provided</scope>
     </dependency>
   </dependencies>
 
   <build>
     <plugins>
       <plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>exec-maven-plugin</artifactId>
         <version>1.1</version>
         <executions>
           <execution>
             <phase>process-classes</phase>
             <goals><goal>exec</goal></goals>
           </execution>
         </executions>
         <configuration>
           <executable>ant</executable>
           <workingDirectory>${project.basedir}/../..</workingDirectory>
           <arguments><argument>jar-complete</argument></arguments>
         </configuration>
       </plugin>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-antrun-plugin</artifactId>
         <executions>
           <execution>
             <id>copy-jar</id>
             <phase>package</phase>
             <goals><goal>run</goal></goals>
             <configuration>
               <tasks>
                 <echo>copy ${project.basedir}/../../lib/jruby-complete.jar to ${project.build.directory}/${project.build.finalName}.jar</echo>
                 <copy overwrite="true" file="${project.basedir}/../../lib/jruby-complete.jar" tofile="${project.build.directory}/${project.build.finalName}.jar"/>
               </tasks>
             </configuration>
           </execution>
         </executions>
       </plugin>
     </plugins>
   </build>
 </project>
diff --git a/maven/jruby/pom.xml b/maven/jruby/pom.xml
index 016a6ea927..6b2c04ca34 100644
--- a/maven/jruby/pom.xml
+++ b/maven/jruby/pom.xml
@@ -1,140 +1,140 @@
 <?xml version="1.0"?>
 <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
   <parent>
     <groupId>org.jruby</groupId>
     <artifactId>shared</artifactId>
     <version>1.6.0.dev</version>
     <relativePath>../../pom.xml</relativePath>
   </parent>
   <modelVersion>4.0.0</modelVersion>
   <groupId>org.jruby</groupId>
   <artifactId>jruby</artifactId>
   <packaging>jar</packaging>
   <version>1.6.0.dev</version>
   <name>JRuby</name>
 
   <properties>
-    <asm.version>3.2</asm.version>
+    <asm.version>3.3.1</asm.version>
   </properties>
 
   <dependencies>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm</artifactId>
       <version>${asm.version}</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-commons</artifactId>
       <version>${asm.version}</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-util</artifactId>
       <version>${asm.version}</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-analysis</artifactId>
       <version>${asm.version}</version>
       <scope>provided</scope>
     </dependency>
     <dependency>
       <groupId>asm</groupId>
       <artifactId>asm-tree</artifactId>
       <version>${asm.version}</version>
       <scope>provided</scope>
     </dependency>
 
     <!-- external dependencies -->
     <dependency>
       <groupId>jline</groupId>
       <artifactId>jline</artifactId>
       <version>0.9.93</version>
     </dependency>
     <dependency>
       <groupId>joda-time</groupId>
       <artifactId>joda-time</artifactId>
       <version>1.6</version>
     </dependency>
 
     <!-- jruby dependencies -->
     <dependency>
       <groupId>org.jruby.joni</groupId>
       <artifactId>joni</artifactId>
       <version>1.1.4</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jnr-netdb</artifactId>
       <version>1.0</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.ext.posix</groupId>
       <artifactId>jnr-posix</artifactId>
       <version>1.1.6</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>bytelist</artifactId>
       <version>1.0.6</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>constantine</artifactId>
       <version>0.6</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jffi</artifactId>
       <version>1.0.1</version>
     </dependency>
     <dependency>
       <groupId>org.jruby.extras</groupId>
       <artifactId>jaffl</artifactId>
       <version>0.5.5</version>
     </dependency>
   </dependencies>
 
   <build>
     <plugins>
       <plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>exec-maven-plugin</artifactId>
         <version>1.1</version>
         <executions>
           <execution>
             <phase>process-classes</phase>
             <goals><goal>exec</goal></goals>
           </execution>
         </executions>
         <configuration>
           <executable>ant</executable>
           <workingDirectory>${project.basedir}/../..</workingDirectory>
           <arguments><argument>jar</argument></arguments>
         </configuration>
       </plugin>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-antrun-plugin</artifactId>
         <executions>
           <execution>
             <id>copy-jar</id>
             <phase>package</phase>
             <goals><goal>run</goal></goals>
             <configuration>
               <tasks>
                 <echo>copy ${project.basedir}/../../lib/jruby.jar to ${project.build.directory}/${project.build.finalName}.jar</echo>
                 <copy overwrite="true" file="${project.basedir}/../../lib/jruby.jar" tofile="${project.build.directory}/${project.build.finalName}.jar"/>
               </tasks>
             </configuration>
           </execution>
         </executions>
       </plugin>
     </plugins>
   </build>
 </project>
diff --git a/nbproject/project.xml b/nbproject/project.xml
index 7d1b8287df..a82b82ab55 100644
--- a/nbproject/project.xml
+++ b/nbproject/project.xml
@@ -1,312 +1,312 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <project xmlns="http://www.netbeans.org/ns/project/1">
     <type>org.netbeans.modules.ant.freeform</type>
     <configuration>
         <general-data xmlns="http://www.netbeans.org/ns/freeform-project/1">
             <name>JRuby trunk</name>
         </general-data>
         <general-data xmlns="http://www.netbeans.org/ns/freeform-project/2">
             <!-- Do not use Project Properties customizer when editing this file manually. -->
             <name>JRuby trunk</name>
             <properties>
                 <property-file>build.properties</property-file>
                 <property-file>default.build.properties</property-file>
             </properties>
             <folders>
                 <source-folder>
                     <label>JRuby trunk</label>
                     <location>.</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>src</label>
                     <type>java</type>
                     <location>${src.dir}</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>test</label>
                     <type>java</type>
                     <location>${test.dir}</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>spec</label>
                     <type>java</type>
                     <location>spec</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>bin</label>
                     <type>java</type>
                     <location>bin</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>bench</label>
                     <type>java</type>
                     <location>bench</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>lib/ruby/1.8</label>
                     <type>java</type>
                     <location>lib/ruby/1.8</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>lib/ruby/1.9</label>
                     <type>java</type>
                     <location>lib/ruby/1.9</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>lib/ruby/site_ruby</label>
                     <type>java</type>
                     <location>lib/ruby/site_ruby</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
                 <source-folder>
                     <label>cext/src</label>
                     <type>java</type>
                     <location>cext/src</location>
                     <encoding>UTF-8</encoding>
                 </source-folder>
             </folders>
             <ide-actions>
                 <action name="build">
                     <script>nbproject/nbjdk.xml</script>
                     <target>jar</target>
                 </action>
                 <action name="clean">
                     <script>nbproject/nbjdk.xml</script>
                     <target>clean</target>
                 </action>
                 <action name="javadoc">
                     <script>nbproject/nbjdk.xml</script>
                     <target>create-apidocs</target>
                 </action>
                 <action name="test">
                     <script>nbproject/nbjdk.xml</script>
                     <target>test</target>
                 </action>
                 <action name="rebuild">
                     <script>nbproject/nbjdk.xml</script>
                     <target>clean</target>
                     <target>jar</target>
                 </action>
                 <action name="debug">
                     <script>nbproject/nbjdk.xml</script>
                     <target>debug-nb</target>
                 </action>
                 <action name="run">
                     <script>nbproject/nbjdk.xml</script>
                     <target>run</target>
                 </action>
                 <action name="compile.single">
                     <script>nbproject/ide-file-targets.xml</script>
                     <target>compile-selected-files-in-test</target>
                     <context>
                         <property>files</property>
                         <folder>test</folder>
                         <pattern>\.java$</pattern>
                         <format>relative-path</format>
                         <arity>
                             <separated-files>,</separated-files>
                         </arity>
                     </context>
                 </action>
                 <action name="test.single">
                     <script>nbproject/ide-file-targets.xml</script>
                     <target>run-selected-files-in-test</target>
                     <context>
                         <property>classname</property>
                         <folder>test</folder>
                         <pattern>\.java$</pattern>
                         <format>java-name</format>
                         <arity>
                             <one-file-only/>
                         </arity>
                     </context>
                 </action>
                 <action name="compile.single">
                     <script>nbproject/ide-file-targets.xml</script>
                     <target>compile-selected-files-in-src</target>
                     <context>
                         <property>files</property>
                         <folder>${src.dir}</folder>
                         <pattern>\.java$</pattern>
                         <format>relative-path</format>
                         <arity>
                             <separated-files>,</separated-files>
                         </arity>
                     </context>
                 </action>
                 <action name="debug.single">
                     <script>nbproject/ide-file-targets.xml</script>
                     <target>debug-selected-files-in-test</target>
                     <context>
                         <property>classname</property>
                         <folder>test</folder>
                         <pattern>\.java$</pattern>
                         <format>java-name</format>
                         <arity>
                             <one-file-only/>
                         </arity>
                     </context>
                 </action>
             </ide-actions>
             <export>
                 <type>folder</type>
                 <location>${jruby.classes.dir}</location>
                 <script>nbproject/nbjdk.xml</script>
                 <build-target>jar</build-target>
             </export>
             <export>
                 <type>jar</type>
                 <location>${lib.dir}/jruby.jar</location>
                 <script>nbproject/nbjdk.xml</script>
                 <build-target>jar</build-target>
             </export>
             <export>
                 <type>folder</type>
                 <location>${test.classes.dir}</location>
                 <script>nbproject/nbjdk.xml</script>
                 <build-target>jar</build-target>
             </export>
             <view>
                 <items>
                     <source-folder style="packages">
                         <label>src</label>
                         <location>${src.dir}</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>test</label>
                         <location>${test.dir}</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>spec</label>
                         <location>spec</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>bin</label>
                         <location>bin</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>bench</label>
                         <location>bench</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>lib/ruby/1.8</label>
                         <location>lib/ruby/1.8</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>lib/ruby/1.9</label>
                         <location>lib/ruby/1.9</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>lib/ruby/site_ruby</label>
                         <location>lib/ruby/site_ruby</location>
                     </source-folder>
                     <source-folder style="packages">
                         <label>cext/src</label>
                         <location>cext/src</location>
                     </source-folder>
                     <source-file>
                         <location>build.xml</location>
                     </source-file>
                 </items>
                 <context-menu>
                     <ide-action name="build"/>
                     <ide-action name="rebuild"/>
                     <ide-action name="clean"/>
                     <ide-action name="javadoc"/>
                     <ide-action name="run"/>
                     <ide-action name="test"/>
                     <ide-action name="debug"/>
                     <action>
                         <label>Findbugs</label>
                         <target>findbugs</target>
                     </action>
                 </context-menu>
             </view>
             <subprojects/>
         </general-data>
         <java-data xmlns="http://www.netbeans.org/ns/freeform-project-java/2">
             <compilation-unit>
                 <package-root>${src.dir}</package-root>
-                <classpath mode="compile">build_lib/junit.jar:build_lib/jline-0.9.93.jar:build_lib/jna.jar:build_lib/nailgun-0.7.1.jar:build_lib/joni.jar:build_lib/dynalang-0.3.jar:build_lib/invokedynamic.jar:build_lib/jcodings.jar:build_lib/constantine.jar:build_lib/bytelist.jar:build_lib/jffi.jar:build_lib/yydebug.jar:build_lib/bsf.jar:build_lib/jaffl.jar:build_lib/asm-3.2.jar:build_lib/asm-analysis-3.2.jar:build_lib/asm-commons-3.2.jar:build_lib/asm-tree-3.2.jar:build_lib/asm-util-3.2.jar:build_lib/jsr292-mock.jar:build_lib/jgrapht-jdk1.5.jar:build_lib/jnr-netdb.jar:build_lib/jnr-posix.jar:build_lib/joda-time-1.6.1.jar:build_lib/livetribe-jsr223-2.0.6.jar:build_lib/snakeyaml-1.7.jar:build_lib/ant.jar</classpath>
+                <classpath mode="compile">build_lib/junit.jar:build_lib/jline-0.9.93.jar:build_lib/jna.jar:build_lib/nailgun-0.7.1.jar:build_lib/joni.jar:build_lib/dynalang-0.3.jar:build_lib/invokedynamic.jar:build_lib/jcodings.jar:build_lib/constantine.jar:build_lib/bytelist.jar:build_lib/jffi.jar:build_lib/yydebug.jar:build_lib/bsf.jar:build_lib/jaffl.jar:build_lib/jsr292-mock.jar:build_lib/jgrapht-jdk1.5.jar:build_lib/jnr-netdb.jar:build_lib/jnr-posix.jar:build_lib/joda-time-1.6.1.jar:build_lib/livetribe-jsr223-2.0.6.jar:build_lib/snakeyaml-1.7.jar:build_lib/ant.jar:build_lib/asm-3.3.1.jar:build_lib/asm-analysis-3.3.1.jar:build_lib/asm-commons-3.3.1.jar:build_lib/asm-tree-3.3.1.jar:build_lib/asm-util-3.3.1.jar</classpath>
                 <built-to>${jruby.classes.dir}</built-to>
                 <built-to>${lib.dir}/jruby.jar</built-to>
                 <javadoc-built-to>docs/api</javadoc-built-to>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>${test.dir}</package-root>
                 <unit-tests/>
-                <classpath mode="compile">build_lib/bsf.jar:build/classes/jruby:build_lib/junit.jar:build_lib/joda-time-1.5.1.jar:build_lib/jna.jar:build_lib/bytelist.jar:build_lib/asm-3.2.jar:build_lib/jnr-posix.jar:build_lib/livetribe-jsr223-2.0.6.jar</classpath>
+                <classpath mode="compile">build_lib/bsf.jar:build/classes/jruby:build_lib/junit.jar:build_lib/joda-time-1.5.1.jar:build_lib/jna.jar:build_lib/bytelist.jar:build_lib/jnr-posix.jar:build_lib/livetribe-jsr223-2.0.6.jar:build_lib/asm-3.3.1.jar</classpath>
                 <built-to>${test.classes.dir}</built-to>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>spec</package-root>
                 <unit-tests/>
                 <classpath mode="compile">build_lib/junit.jar:build/classes/jruby</classpath>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>bin</package-root>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>bench</package-root>
                 <unit-tests/>
                 <classpath mode="compile">build/classes/jruby</classpath>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>lib/ruby/1.8</package-root>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>lib/ruby/1.9</package-root>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>lib/ruby/site_ruby</package-root>
                 <source-level>1.5</source-level>
             </compilation-unit>
             <compilation-unit>
                 <package-root>cext/src</package-root>
                 <source-level>1.5</source-level>
             </compilation-unit>
         </java-data>
         <preferences xmlns="http://www.netbeans.org/ns/auxiliary-configuration-preferences/1">
             <module name="org-netbeans-modules-editor-indent">
                 <node name="CodeStyle">
                     <node name="project">
                         <property name="spaces-per-tab" value="2"/>
                         <property name="expand-tabs" value="true"/>
                         <property name="text-limit-width" value="80"/>
                         <property name="indent-shift-width" value="2"/>
                         <property name="tab-size" value="2"/>
                     </node>
                     <property name="usedProfile" value="project"/>
                 </node>
                 <node name="text">
                     <node name="x-java">
                         <node name="CodeStyle">
                             <node name="project">
                                 <property name="spaces-per-tab" value="4"/>
                                 <property name="expand-tabs" value="true"/>
                                 <property name="text-limit-width" value="80"/>
                                 <property name="indent-shift-width" value="4"/>
                                 <property name="tab-size" value="4"/>
                             </node>
                         </node>
                     </node>
                 </node>
             </module>
         </preferences>
     </configuration>
 </project>
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 052f58407f..66d5c3aece 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,778 +1,778 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler.impl;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.CacheCompiler;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.BodyCompiler;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.util.CheckClassAdapter;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 /**
  *
  * @author headius
  */
 public class StandardASMCompiler implements ScriptCompiler, Opcodes {
     public static final String THREADCONTEXT = p(ThreadContext.class);
     public static final String RUBY = p(Ruby.class);
     public static final String IRUBYOBJECT = p(IRubyObject.class);
-    public static final boolean VERIFY_CLASSFILES = false;
+    public static final boolean VERIFY_CLASSFILES = true;
 
     public static String getStaticMethodSignature(String classname, int args) {
         switch (args) {
         case 0:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, Block.class);
         case 1:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 2:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 3:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 4:
             return sig(IRubyObject.class, "L" + classname + ";", ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
         default:
             throw new RuntimeException("unsupported arity: " + args);
         }
     }
 
     public static String getMethodSignature(int args) {
         switch (args) {
         case 0:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, Block.class);
         case 1:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 2:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 3:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class);
         case 4:
             return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
         default:
             throw new RuntimeException("unsupported arity: " + args);
         }
     }
 
     public static String getStaticClosureSignature(String classdesc) {
         return sig(IRubyObject.class, "L" + classdesc + ";", ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
     }
 
     public static String getStaticClosure19Signature(String classdesc) {
         return sig(IRubyObject.class, "L" + classdesc + ";", ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
     }
 
     public static String getClosureSignature() {
         return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
     }
 
     public static String getClosure19Signature() {
         return sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
     }
 
     public static final int THIS = 0;
     public static final int THREADCONTEXT_INDEX = 1;
     public static final int SELF_INDEX = 2;
     public static final int ARGS_INDEX = 3;
     
     public static final int CLOSURE_OFFSET = 0;
     public static final int DYNAMIC_SCOPE_OFFSET = 1;
     public static final int VARS_ARRAY_OFFSET = 2;
     public static final int EXCEPTION_OFFSET = 3;
     public static final int PREVIOUS_EXCEPTION_OFFSET = 4;
     public static final int FIRST_TEMP_OFFSET = 5;
 
     public static final int STARTING_DSTR_SIZE = 20;
     
     private String classname;
     private String sourcename;
 
     private Integer javaVersion;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     private int methodIndex = 0;
     private int innerIndex = 0;
     private int rescueNumber = 1;
     private int ensureNumber = 1;
     StaticScope topLevelScope;
     
     private CacheCompiler cacheCompiler;
     
     public static final Constructor invDynInvCompilerConstructor;
     public static final Method invDynSupportInstaller;
 
     private List<InvokerDescriptor> invokerDescriptors = new ArrayList<InvokerDescriptor>();
     private List<BlockCallbackDescriptor> blockCallbackDescriptors = new ArrayList<BlockCallbackDescriptor>();
     private List<BlockCallbackDescriptor> blockCallback19Descriptors = new ArrayList<BlockCallbackDescriptor>();
 
     static {
         Constructor compilerConstructor = null;
         Method installerMethod = null;
         try {
             if (SafePropertyAccessor.getBoolean("jruby.compile.invokedynamic")) {
                 // if that succeeds, the others should as well
                 Class compiler =
                         Class.forName("org.jruby.compiler.impl.InvokeDynamicInvocationCompiler");
                 Class support =
                         Class.forName("org.jruby.runtime.invokedynamic.InvokeDynamicSupport");
                 compilerConstructor = compiler.getConstructor(BaseBodyCompiler.class, SkinnyMethodAdapter.class);
                 installerMethod = support.getDeclaredMethod("installBytecode", MethodVisitor.class, String.class);
             }
         } catch (Exception e) {
             e.printStackTrace();
             // leave it null and fall back on our normal invocation logic
         }
         invDynInvCompilerConstructor = compilerConstructor;
         invDynSupportInstaller = installerMethod;
     }
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public void setJavaVersion(Integer javaVersion) {
         this.javaVersion = javaVersion;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(c(getClassname()), classWriter.toByteArray());
         return classLoader.loadClass(c(getClassname()));
     }
     
     public void dumpClass(PrintStream out) {
         PrintWriter pw = new PrintWriter(out);
 
         try {
             TraceClassVisitor tcv = new TraceClassVisitor(pw);
             new ClassReader(classWriter.toByteArray()).accept(tcv, 0);
         } finally {
             pw.close();
         }
     }
 
     public void writeClass(File destination) throws IOException {
         writeClass(getClassname(), destination, classWriter);
     }
 
     public void writeInvokers(File destination) throws IOException {
         for (InvokerDescriptor descriptor : invokerDescriptors) {
             byte[] invokerBytes = RuntimeHelpers.defOffline(
                     descriptor.getName(),
                     descriptor.getClassname(),
                     descriptor.getInvokerName(),
                     descriptor.getArity(),
                     descriptor.getScope(),
                     descriptor.getCallConfig(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(invokerBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, invokerBytes, descriptor.getInvokerName());
         }
 
         for (BlockCallbackDescriptor descriptor : blockCallbackDescriptors) {
             byte[] callbackBytes = RuntimeHelpers.createBlockCallbackOffline(
                     descriptor.getClassname(),
                     descriptor.getMethod(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(callbackBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, callbackBytes, descriptor.getCallbackName());
         }
 
         for (BlockCallbackDescriptor descriptor : blockCallback19Descriptors) {
             byte[] callbackBytes = RuntimeHelpers.createBlockCallback19Offline(
                     descriptor.getClassname(),
                     descriptor.getMethod(),
                     descriptor.getFile(),
                     descriptor.getLine());
 
             if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(callbackBytes), false, new PrintWriter(System.err));
 
             writeClassFile(destination, callbackBytes, descriptor.getCallbackName());
         }
     }
 
     private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
         // verify the class
         byte[] bytecode = writer.toByteArray();
         if (VERIFY_CLASSFILES) CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(System.err));
 
         writeClassFile(destination, bytecode, classname);
     }
 
     private void writeClassFile(File destination, byte[] bytecode, String classname) throws IOException {
         String fullname = classname + ".class";
         String filename = null;
         String path = null;
 
         if (fullname.lastIndexOf("/") == -1) {
             filename = fullname;
             path = "";
         } else {
             filename = fullname.substring(fullname.lastIndexOf("/") + 1);
             path = fullname.substring(0, fullname.lastIndexOf("/"));
         }
         // create dir if necessary
         File pathfile = new File(destination, path);
         pathfile.mkdirs();
 
         FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
 
         try {
             out.write(bytecode);
         } finally {
             out.close();
         }
     }
 
     public static class InvokerDescriptor {
         private final String name;
         private final String classname;
         private final String invokerName;
         private final Arity arity;
         private final StaticScope scope;
         private final CallConfiguration callConfig;
         private final String file;
         private final int line;
         
         public InvokerDescriptor(String name, String classname, String invokerName, Arity arity, StaticScope scope, CallConfiguration callConfig, String file, int line) {
             this.name = name;
             this.classname = classname;
             this.invokerName = invokerName;
             this.arity = arity;
             this.scope = scope;
             this.callConfig = callConfig;
             this.file = file;
             this.line = line;
         }
 
         public Arity getArity() {
             return arity;
         }
 
         public CallConfiguration getCallConfig() {
             return callConfig;
         }
 
         public String getClassname() {
             return classname;
         }
 
         public String getFile() {
             return file;
         }
 
         public String getInvokerName() {
             return invokerName;
         }
 
         public int getLine() {
             return line;
         }
 
         public String getName() {
             return name;
         }
 
         public StaticScope getScope() {
             return scope;
         }
     }
 
     private static class BlockCallbackDescriptor {
         private final String method;
         private final String classname;
         private final String callbackName;
         private final String file;
         private final int line;
 
         public BlockCallbackDescriptor(String method, String classname, String file, int line) {
             this.method = method;
             this.classname = classname;
             this.callbackName = classname + "BlockCallback$" + method + "xx1";
             this.file = file;
             this.line = line;
         }
 
         public String getClassname() {
             return classname;
         }
 
         public String getMethod() {
             return method;
         }
 
         public String getCallbackName() {
             return callbackName;
         }
 
         public String getFile() {
             return file;
         }
 
         public int getLine() {
             return line;
         }
     }
 
     public void addInvokerDescriptor(String newMethodName, int methodArity, StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         String classPath = classname.replaceAll("/", "_");
         Arity arity = Arity.createArity(methodArity);
         String invokerName = classPath + "Invoker" + newMethodName + arity;
         InvokerDescriptor descriptor = new InvokerDescriptor(newMethodName, classname, invokerName, arity, scope, callConfig, filename, line);
 
         invokerDescriptors.add(descriptor);
     }
 
     public void addBlockCallbackDescriptor(String method, String file, int line) {
         blockCallbackDescriptors.add(new BlockCallbackDescriptor(method, classname, file, line));
     }
 
     public void addBlockCallback19Descriptor(String method, String file, int line) {
         blockCallback19Descriptors.add(new BlockCallbackDescriptor(method, classname, file, line));
     }
 
     public String getClassname() {
         return classname;
     }
 
     public String getSourcename() {
         return sourcename;
     }
 
     public ClassVisitor getClassVisitor() {
         return classWriter;
     }
     
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(javaVersion == null ? RubyInstanceConfig.JAVA_VERSION : javaVersion,
                 ACC_PUBLIC + ACC_SUPER,getClassname(), null, p(AbstractScript.class), null);
 
         // add setPosition impl, which stores filename as constant to speed updates
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "setPosition", sig(Void.TYPE, params(ThreadContext.class, int.class)), null, null);
         method.start();
 
         method.aload(0); // thread context
         method.ldc(sourcename);
         method.iload(1); // line number
         method.invokevirtual(p(ThreadContext.class), "setFileAndLine", sig(void.class, String.class, int.class));
         method.voidreturn();
         method.end();
         
         topLevelScope = scope;
 
         beginInit();
         
         cacheCompiler = new InheritedCacheCompiler(this);
 
         // This code was originally used to provide debugging info using JSR-45
         // "SMAP" format. However, it breaks using normal Java traces to
         // generate Ruby traces, since the original path is lost. Reverting
         // to full path for now.
 //        String sourceNoPath;
 //        if (sourcename.indexOf("/") >= 0) {
 //            String[] pathElements = sourcename.split("/");
 //            sourceNoPath = pathElements[pathElements.length - 1];
 //        } else if (sourcename.indexOf("\\") >= 0) {
 //            String[] pathElements = sourcename.split("\\\\");
 //            sourceNoPath = pathElements[pathElements.length - 1];
 //        } else {
 //            sourceNoPath = sourcename;
 //        }
 
         final File sourceFile = new File(getSourcename());
         // Revert to using original sourcename here, so that jitted traces match
         // interpreted traces.
         classWriter.visitSource(sourcename, sourceFile.getAbsolutePath());
     }
 
     public void endScript(boolean generateLoad, boolean generateMain) {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         
         if (generateLoad || generateMain) {
             // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "load", getMethodSignature(4), null, null);
             method.start();
 
             // invoke __file__ with threadcontext, self, args (null), and block (null)
             Label tryBegin = new Label();
             Label tryFinally = new Label();
 
             method.label(tryBegin);
             method.aload(THREADCONTEXT_INDEX);
             String scopeNames = RuntimeHelpers.encodeScope(topLevelScope);
             method.ldc(scopeNames);
             method.invokestatic(p(RuntimeHelpers.class), "preLoad", sig(void.class, ThreadContext.class, String.class));
 
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             method.aload(ARGS_INDEX);
             // load always uses IRubyObject[], so simple closure offset calculation here
             method.aload(ARGS_INDEX + 1 + CLOSURE_OFFSET);
 
             method.invokestatic(getClassname(),methodName, getStaticMethodSignature(getClassname(), 4));
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.areturn();
 
             method.label(tryFinally);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.athrow();
 
             method.trycatch(tryBegin, tryFinally, tryFinally, null);
 
             method.end();
         }
         
         if (generateMain) {
             // add main impl, used for detached or command-line execution of this script with a new runtime
             // root method of a script is always in stub0, method0
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC | ACC_STATIC, "main", sig(Void.TYPE, params(String[].class)), null, null);
             method.start();
 
             // new instance to invoke run against
             method.newobj(getClassname());
             method.dup();
             method.invokespecial(getClassname(), "<init>", sig(Void.TYPE));
 
             // set filename for the loaded script class (JRUBY-4825)
             method.dup();
             method.ldc(Type.getType("L" + getClassname() + ";"));
             method.invokevirtual(p(Class.class), "getClassLoader", sig(ClassLoader.class));
             method.ldc(getClassname() + ".class");
             method.invokevirtual(p(ClassLoader.class), "getResource", sig(URL.class, String.class));
             method.invokevirtual(p(Object.class), "toString", sig(String.class));
             method.astore(1);
             method.aload(1);
             method.invokevirtual(p(AbstractScript.class), "setFilename", sig(void.class, String.class));
 
             // instance config for the script run
             method.newobj(p(RubyInstanceConfig.class));
             method.dup();
             method.invokespecial(p(RubyInstanceConfig.class), "<init>", "()V");
 
             // set argv from main's args
             method.dup();
             method.aload(0);
             method.invokevirtual(p(RubyInstanceConfig.class), "setArgv", sig(void.class, String[].class));
 
             // set script filename ($0)
             method.dup();
             method.aload(1);
             method.invokevirtual(p(RubyInstanceConfig.class), "setScriptFileName", sig(void.class, String.class));
 
             // invoke run with threadcontext and topself
             method.invokestatic(p(Ruby.class), "newInstance", sig(Ruby.class, RubyInstanceConfig.class));
             method.dup();
 
             method.invokevirtual(RUBY, "getCurrentContext", sig(ThreadContext.class));
             method.swap();
             method.invokevirtual(RUBY, "getTopSelf", sig(IRubyObject.class));
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokevirtual(getClassname(), "load", getMethodSignature(4));
             method.voidreturn();
             method.end();
         }
 
         getCacheCompiler().finish();
         
         endInit();
         endClassInit();
     }
 
     public static String buildStaticScopeNames(StaticScope scope) {
         return RuntimeHelpers.encodeScope(scope);
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv, ACC_PUBLIC, "<init>", sig(Void.TYPE), null, null);
         initMethod.start();
         initMethod.aload(THIS);
         initMethod.invokespecial(p(AbstractScript.class), "<init>", sig(Void.TYPE));
         
         // JRUBY-3014: make __FILE__ dynamically determined at load time, but
         // we provide a reasonable default here
         initMethod.aload(THIS);
         initMethod.ldc(getSourcename());
         initMethod.putfield(getClassname(), "filename", ci(String.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv, ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null);
         clinitMethod.start();
 
         if (invDynSupportInstaller != null) {
             // install invokedynamic bootstrapper
             // TODO need to abstract this setup behind another compiler interface
             try {
                 invDynSupportInstaller.invoke(null, clinitMethod, getClassname());
             } catch (IllegalAccessException ex) {
                 ex.printStackTrace();
                 // ignore; we won't use invokedynamic
             } catch (IllegalArgumentException ex) {
                 ex.printStackTrace();
                 // ignore; we won't use invokedynamic
             } catch (InvocationTargetException ex) {
                 ex.printStackTrace();
                 // ignore; we won't use invokedynamic
             }
         }
     }
 
     private void endClassInit() {
         if (clinitMethod != null) {
             clinitMethod.voidreturn();
             clinitMethod.end();
         }
     }
     
     public SkinnyMethodAdapter getInitMethod() {
         return initMethod;
     }
     
     public SkinnyMethodAdapter getClassInitMethod() {
         // lazily create class init only if necessary
         if (clinitMethod == null) {
             beginClassInit();
         }
         return clinitMethod;
     }
     
     public CacheCompiler getCacheCompiler() {
         return cacheCompiler;
     }
     
     public BodyCompiler startMethod(String rubyName, String javaName, CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         RootScopedBodyCompiler methodCompiler = new MethodBodyCompiler(this, rubyName, javaName, inspector, scope);
         
         methodCompiler.beginMethod(args, scope);
         
         return methodCompiler;
     }
 
     public BodyCompiler startFileMethod(CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         MethodBodyCompiler methodCompiler = new MethodBodyCompiler(this, "__file__", "__file__", inspector, scope);
         
         methodCompiler.beginMethod(args, scope);
         
         // boxed arg list __file__
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "__file__", getMethodSignature(4), null, null);
         method.start();
 
         // invoke static __file__
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(ARGS_INDEX + 1); // block
         method.invokestatic(getClassname(), "__file__", getStaticMethodSignature(getClassname(), 4));
 
         method.areturn();
         method.end();
         
         if (methodCompiler.isSpecificArity()) {
             // exact arg list __file__
             method = new SkinnyMethodAdapter(getClassVisitor(), ACC_PUBLIC, "__file__", getMethodSignature(scope.getRequiredArgs()), null, null);
             method.start();
 
             // invoke static __file__
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             for (int i = 0; i < scope.getRequiredArgs(); i++) {
                 method.aload(ARGS_INDEX + i);
             }
             method.aload(ARGS_INDEX + scope.getRequiredArgs()); // block
             method.invokestatic(getClassname(), "__file__", getStaticMethodSignature(getClassname(), scope.getRequiredArgs()));
 
             method.areturn();
             method.end();
         }
 
         return methodCompiler;
     }
 
     public BodyCompiler startRoot(String rubyName, String javaName, StaticScope scope, ASTInspector inspector) {
         RootScopedBodyCompiler methodCompiler = new MethodBodyCompiler(this, rubyName, javaName, inspector, scope);
 
         methodCompiler.beginMethod(null, scope);
 
         return methodCompiler;
     }
 
     public int getMethodIndex() {
         return methodIndex;
     }
     
     public int getAndIncrementMethodIndex() {
         return methodIndex++;
     }
 
     public int getInnerIndex() {
         return innerIndex;
     }
 
     public int getAndIncrementInnerIndex() {
         return innerIndex++;
     }
 
     public int getRescueNumber() {
         return rescueNumber;
     }
 
     public int getAndIncrementRescueNumber() {
         return rescueNumber++;
     }
 
     public int getEnsureNumber() {
         return ensureNumber;
     }
 
     public int getAndIncrementEnsureNumber() {
         return ensureNumber++;
     }
 
     private int constants = 0;
 
     public String getNewConstant(String type, String name_prefix) {
         return getNewConstant(type, name_prefix, null);
     }
 
     public synchronized String getNewConstantName() {
         return "_" + constants++;
     }
 
     public String getNewConstant(String type, String name_prefix, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         String realName = getNewConstantName();
 
         // declare the field
         cv.visitField(ACC_PRIVATE, realName, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(getClassname(),realName, type);
         }
 
         return realName;
     }
 
     public String getNewField(String type, String name, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         // declare the field
         cv.visitField(ACC_PRIVATE, name, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(getClassname(),name, type);
         }
 
         return name;
     }
 
     public String getNewStaticConstant(String type, String name_prefix) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "__" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, realName, type, null, null).visitEnd();
         return realName;
     }
 }
