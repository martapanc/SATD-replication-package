diff --git a/build.xml b/build.xml
index 9d9315ac74..fccd5a41e2 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1615 +1,1619 @@
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
   
   <property name="asm.jar" value="${build.lib.dir}/asm-3.3.1.jar"/>
   <property name="asm-commons.jar" value="${build.lib.dir}/asm-commons-3.3.1.jar"/>
   <property name="asm-util.jar" value="${build.lib.dir}/asm-util-3.3.1.jar"/>
   <property name="asm-analysis.jar" value="${build.lib.dir}/asm-analysis-3.3.1.jar"/>
   <property name="asm-tree.jar" value="${build.lib.dir}/asm-tree-3.3.1.jar"/>
 
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
 
   <tstamp><format property="build.date" pattern="yyyy-MM-dd"/></tstamp>
   <property environment="env"/>
   
   <!-- Use JAVA_OPTS env var if set, -ea (supported by all JVMs) otherwise -->
   <condition property="java.opts" value="${env.JAVA_OPTS}"><isset property="env.JAVA_OPTS"/></condition>
   <condition property="java.opts" value="-ea"><not><isset property="java.opts"/></not></condition>
   
   <property name="version.ruby" value="${version.ruby.major}.${version.ruby.minor}"/>
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
   
   <macrodef name="update-constants">
     <attribute name="tzdata-version" default="${tzdata.distributed.version}"/>
     <sequential>
       <echo message="Updating constants with tzdata version @{tzdata-version}"/>
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
             <filter token="joda.time.version" value="${joda.time.version}"/>
             <filter token="tzdata.version" value="@{tzdata-version}"/>
           </filterset>
         </copy>
 
         <condition property="constants.java.is.same">
           <filesmatch file1="${basedir}/src_gen/org/jruby/runtime/Constants.java.gen" file2="${basedir}/src_gen/org/jruby/runtime/Constants.java.old" textfile="true"/>
         </condition>
         <antcall target="_uc_internal_"/>
     
         <property name="update-constants.hasrun" value="true"/>
         
     </sequential>
   </macrodef>
 
   <target name="update-constants" unless="update-constants.hasrun">
     <update-constants/>
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
       <jvmarg line="${java.opts}"/>
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
       <jvmarg line="${java.opts}"/>
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
         <zipfileset src="${asm.jar}"/>
         <zipfileset src="${asm-commons.jar}"/>
         <zipfileset src="${asm-util.jar}"/>
         <zipfileset src="${asm-analysis.jar}"/>
         <zipfileset src="${asm-tree.jar}"/>
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
+        <zipfileset src="${build.lib.dir}/yecht.jar"/>
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
 
   <target name="jar-jruby-nogpl" depends="init, compile, unzip-native-libs" unless="jar-up-to-date, jar-jruby.hasrun">
       <jar destfile="${lib.dir}/jruby.jar" compress="true" index="true" update="true">
         <fileset dir="${jruby.classes.dir}"/>
         <zipfileset src="${asm.jar}"/>
         <zipfileset src="${asm-commons.jar}"/>
         <zipfileset src="${asm-util.jar}"/>
         <zipfileset src="${asm-analysis.jar}"/>
         <zipfileset src="${asm-tree.jar}"/>
         <zipfileset src="${build.lib.dir}/bytelist.jar"/>
         <zipfileset src="${build.lib.dir}/constantine.jar"/>
         <zipfileset src="${build.lib.dir}/jline-0.9.93.jar"/>
         <zipfileset src="${build.lib.dir}/jcodings.jar"/>
         <zipfileset src="${build.lib.dir}/joni.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-netdb.jar"/>
         <zipfileset src="${build.lib.dir}/jnr-posix.jar"/>
         <zipfileset src="${build.lib.dir}/jaffl.jar"/>
         <zipfileset src="${build.lib.dir}/joda-time-${joda.time.version}.jar"/>
         <zipfileset src="${build.lib.dir}/snakeyaml-1.7.jar"/>
+        <zipfileset src="${build.lib.dir}/yecht.jar"/>
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
         <zipfileset src="${asm.jar}"/>
         <zipfileset src="${asm-util.jar}"/>
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
         <zipfileset src="${asm.jar}"/>
         <zipfileset src="${asm-commons.jar}"/>
         <zipfileset src="${asm-util.jar}"/>
         <zipfileset src="${asm-analysis.jar}"/>
         <zipfileset src="${asm-tree.jar}"/>
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
+        <zipfileset src="${build.lib.dir}/yecht.jar"/>
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
       <jvmarg line="${java.opts}"/>
       <arg line="-S gem install ${complete.jar.gems}"/>
       <arg line="--no-ri --no-rdoc --ignore-dependencies --env-shebang"/>
     </java>
 
     <taskdef name="jarjar" classname="com.tonicsystems.jarjar.JarJarTask" classpath="${build.lib.dir}/jarjar-1.0.jar"/>
     <jarjar destfile="${dest.lib.dir}/${filename}">
       <fileset dir="${jruby.classes.dir}"/>
       <fileset dir="${build.dir}/jar-complete">
         <exclude name="META-INF/jruby.home/lib/ruby/site_ruby/shared/builtin/**"/>
       </fileset>
       <zipfileset src="${asm.jar}"/>
       <zipfileset src="${asm-commons.jar}"/>
       <zipfileset src="${asm-util.jar}"/>
       <zipfileset src="${asm-analysis.jar}"/>
       <zipfileset src="${asm-tree.jar}"/>
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
+      <zipfileset src="${build.lib.dir}/yecht.jar"/>
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
     <java jar="lib/jruby-complete.jar" fork="true" failonerror="true">
       <arg line="-v test/test_jar_complete.rb"/>
     </java>
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
            source="${javac.version}" includeantruntime="false">
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
       <jvmarg line="-ea ${java.opts}"/>
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
 
   <target name="install-gems" depends="install-dev-gems,install-jruby-launcher-gem"/>
 
   <condition property="dev.gems.installed">
     <uptodate>
       <srcfiles dir="${basedir}" includes="${dev.gems}"/>
       <chainedmapper>
         <flattenmapper/>
         <globmapper from="*.gem" to="${basedir}/lib/ruby/gems/1.8/specifications/*.gemspec"/>
       </chainedmapper>
     </uptodate>
   </condition>
 
   <target name="install-dev-gems" depends="jar" unless="dev.gems.installed">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="-S gem uninstall ${dev.gem.names}"/>
     </java>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="-S gem install ${dev.gems}"/>
     </java>
   </target>
 
   <condition property="jruby-launcher.gem.installed">
     <or>
       <os family="windows"/> <!-- windows doesn't need the launcher -->
       <uptodate>
         <srcfiles dir="${basedir}" includes="${jruby.launcher.gem}"/>
         <chainedmapper>
           <flattenmapper/>
           <globmapper from="*.gem" to="${basedir}/lib/ruby/gems/1.8/specifications/*.gemspec"/>
         </chainedmapper>
       </uptodate>
     </or>
   </condition>
 
   <target name="install-jruby-launcher-gem" depends="jar" unless="jruby-launcher.gem.installed">
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="false">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="-S gem uninstall jruby-launcher"/>
     </java>
     <java classname="org.jruby.Main" fork="true" maxmemory="${jruby.launch.memory}" failonerror="true">
       <classpath refid="build.classpath"/>
       <classpath path="${jruby.classes.dir}"/>
       <sysproperty key="jruby.home" value="${basedir}"/>
       <env key="GEM_PATH" value=""/> <!-- to ignore any gems installed in ~/.gem -->
       <arg line="-S gem install ${jruby.launcher.gem}"/>
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
         <jvmarg line="-ea ${java.opts} -client -XX:MaxPermSize=256M -XX:+UnlockExperimentalVMOptions -XX:+EnableMethodHandles -XX:+EnableInvokeDynamic"/>
 
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
         <jvmarg line="-ea ${java.opts} -client -XX:MaxPermSize=256M"/>
 
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
         <sysproperty key="jruby.home" value="${base.dir}"/>
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
       <jvmarg line="${java.opts}"/>
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
         <jvmarg line="${java.opts}"/>
 
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
         <env key="JRUBY_OPTS" value=""/>
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
 
   <target name="dist" depends="install-dev-gems,dist-bin,dist-src,dist-jar-complete">
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
     </exec>
   </target>
 
   <target name="jruby-nailgun" depends="compile, build-ng"
     description="Set up JRuby to be run with Nailgun (jruby-ng, jruby-ng-server)">
     <mkdir dir="${build.dir}/nailmain"/>
   </target>
 
   <property name="jruby-cext-native.home" value="${basedir}/cext/src/"/>
 
   <target name="build-jruby-cext-native" depends="jar"
     description="Build JRuby cext support">
     <exec osfamily="unix" executable="make" dir="${jruby-cext-native.home}" failonerror="true"/>
     <exec osfamily="windows" executable="cmd" dir="${jruby-cext-native.home}" failonerror="true">
       <arg line='/c sh -c "make CC=gcc"'/>
     </exec>
   </target>
 
   <target name="clean-jruby-cext-native" depends="compile"
     description="Build JRuby cext support">
     <exec osfamily="unix" executable="make" dir="${jruby-cext-native.home}" failonerror="true">
       <arg value="dist-clean"/>
     </exec>
     <exec osfamily="windows" executable="cmd" dir="${jruby-cext-native.home}" failonerror="true">
       <arg line='/c sh -c "make dist-clean"'/>
     </exec>
diff --git a/lib/ruby/site_ruby/shared/yecht.jar b/build_lib/yecht.jar
similarity index 100%
rename from lib/ruby/site_ruby/shared/yecht.jar
rename to build_lib/yecht.jar
diff --git a/lib/ruby/1.8/yaml/yecht.rb b/lib/ruby/1.8/yaml/yecht.rb
index f13a63601e..3d917144b0 100644
--- a/lib/ruby/1.8/yaml/yecht.rb
+++ b/lib/ruby/1.8/yaml/yecht.rb
@@ -1,19 +1,19 @@
 #
 # YAML::Yecht module
 # .. glues yecht and yaml.rb together ..
 #
-require 'yecht'
+require 'yecht.jar'
 require 'yaml/basenode'
 
 module YAML
     module Yecht
 
         #
         # Mixin BaseNode functionality
         #
         class Node
             include YAML::BaseNode
         end
 
     end
 end
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index ff8ca6f221..a9bd22f785 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -462,2000 +462,2001 @@ public final class Ruby {
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
         if (compile || config.isShowBytecode()) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
         }
 
         if (script != null) {
             if (config.isShowBytecode()) {
                 return getNil();
             }
             
             return runScript(script);
         } else {
             failForcedCompile(scriptNode);
             
             return runInterpreter(scriptNode);
         }
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled.
      *
      * @param node The node to attempt to compiled
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), false);
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled. This version accepts an ASTInspector instance assumed to
      * have appropriate flags set for compile optimizations, such as to turn
      * on heap-based local variables to share an existing scope.
      *
      * @param node The node to attempt to compiled
      * @param inspector The ASTInspector to use for making optimization decisions
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node, ASTInspector inspector) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), inspector, false);
     }
 
     private void failForcedCompile(Node scriptNode) throws RaiseException {
         if (config.getCompileMode().shouldPrecompileAll()) {
             throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getPosition().getFile());
         }
     }
 
     private void handeCompileError(Node node, Throwable t) {
         if (config.isJitLoggingVerbose() || config.isDebug()) {
             System.err.println("warning: could not compile: " + node.getPosition().getFile() + "; full trace follows");
             t.printStackTrace();
         }
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
 
         return tryCompile(node, cachedClassName, classLoader, inspector, dump);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, ASTInspector inspector, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             StandardASMCompiler asmCompiler = null;
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 asmCompiler = new StandardASMCompiler(cachedClassName.replace('.', '/'), filename);
             } else {
                 asmCompiler = new StandardASMCompiler(classname, filename);
             }
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
 
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 // save script off to disk
                 String pathName = cachedClassName.replace('.', '/');
                 JITCompiler.saveToCodeCache(this, asmCompiler.getClassByteArray(), "ruby/jit", new File(RubyInstanceConfig.JIT_CODE_CACHE, pathName + ".class"));
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (Throwable t) {
             handeCompileError(node, t);
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         try {
             if (getInstanceConfig().getCompileMode() == CompileMode.OFFIR) {
                 return Interpreter.interpret(this, rootNode, self);
             } else {
                 return ASTInterpreter.INTERPRET_ROOT(this, context, rootNode, getTopSelf(), Block.NULL_BLOCK);
             }
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(((RootNode) scriptNode).getBodyNode());
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
     public void addModule(RubyModule module) {
         synchronized (allModules) {
             allModules.add(module);
         }
     }
     public void eachModule(Function1<Object, IRubyObject> func) {
         synchronized (allModules) {
             for (RubyModule module : allModules) {
                 func.apply(module);
             }
         }
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
 
         // Set up the main thread in thread service
         threadService.initMainThread();
 
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
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
             loadService.smartLoad(scriptName);
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
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
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
         
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
     }
 
     private void initEncodings() {
         RubyEncoding.createEncodingClass(this);
         RubyConverter.createConverterClass(this);
         encodingService = new EncodingService(this);
 
         // External should always have a value, but Encoding.external_encoding{,=} will lazily setup
         String encoding = config.getExternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultExternalEncoding(loadedEncoding);
         } else {
             setDefaultExternalEncoding(encodingService.getLocaleEncoding());
         }
         
         encoding = config.getInternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultInternalEncoding(loadedEncoding);
         }
     }
 
     private void initCore() {
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             initEncodings();
             RubyRandom.createRandomClass(this);
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
         if (profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
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
                 fiberError = defineClass("FiberError", standardError, standardError.getAllocator());
             }
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             recursiveKey = newSymbol("__recursive_key__");
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
                 // define EAGAIN now, so that future EWOULDBLOCK will alias to it
                 // see MRI's error.c and its explicit ordering of Errno definitions.
                 createSysErr(Errno.EAGAIN.value(), Errno.EAGAIN.name());
                 
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
             if (errnos.get(i) == null) {
                 RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
                 errnos.put(i, errno);
                 errno.defineConstant("Errno", newFixnum(i));
             } else {
                 // already defined a class for this errno, reuse it (JRUBY-4747)
                 getErrno().setConstant(name, errnos.get(i));
             }
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.ext.jruby.JRubyExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.ext.jruby.JRubyCoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("jruby/synchronized.rb", "jruby/synchronized", "org.jruby.ext.jruby.JRubySynchronizedLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
+        addLazyBuiltin("yecht.jar", "yecht", "YechtService");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.libraries.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
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
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
         String[] builtins = {"jsignal_internal", "generator_internal"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             loadFile("builtin/prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/prelude.rb"), false);
             if (!config.isDisableGems()) {
                 // NOTE: This has been disabled because gem_prelude is terribly broken.
                 //       We just require 'rubygems' in gem_prelude, at least for now.
                 //defineModule("Gem"); // dummy Gem module for prelude
                 loadFile("builtin/gem_prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/gem_prelude.rb"), false);
             }
         }
 
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
 
     public RubyClass getMathDomainError() {
         return mathDomainError;
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
 
     public RubyClass getFiberError() {
         return fiberError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     public RubyClass getRandomClass() {
         return randomClass;
     }
 
     public void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
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
         return verboseValue;
     }
 
     public boolean isVerbose() {
         return verbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose.isTrue();
         this.verboseValue = verbose;
         warningsEnabled = !verbose.isNil();
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug ? trueObject : falseObject;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug.isTrue();
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
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
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
 
     public Encoding getDefaultInternalEncoding() {
         return defaultInternalEncoding;
     }
 
     public void setDefaultInternalEncoding(Encoding defaultInternalEncoding) {
         this.defaultInternalEncoding = defaultInternalEncoding;
     }
 
     public Encoding getDefaultExternalEncoding() {
         return defaultExternalEncoding;
     }
 
     public void setDefaultExternalEncoding(Encoding defaultExternalEncoding) {
         this.defaultExternalEncoding = defaultExternalEncoding;
     }
 
     public EncodingService getEncodingService() {
         return encodingService;
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
                 throw newTypeError("" + path + " does not refer to class/module");
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
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(excp));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             runInterpreter(context, parseFile(in, scriptName, null), self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
 
             Script script = null;
             String className = null;
 
             try {
                 // read full contents of file, hash it, and try to load that class first
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 byte[] buffer = new byte[1024];
                 int num;
                 while ((num = in.read(buffer)) > -1) {
                     baos.write(buffer, 0, num);
                 }
                 buffer = baos.toByteArray();
                 String hash = JITCompiler.getHashForBytes(buffer);
                 className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index f32d2fff7d..2f60fc15d9 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,1353 +1,1358 @@
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
  * Copyright (C) 2002-2010 JRuby Community
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
         public static final String[] extensionSuffixes = { ".jar", ".so", ".bundle", ".dll" };
         private static final String[] allSuffixes = { ".class", ".rb", ".jar", ".so", ".bundle", ".dll" };
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
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|bundle|jar)$");
 
     protected RubyArray loadPath;
     protected RubyArray loadedFeatures;
     protected List loadedFeaturesInternal;
     protected final Map<String, Library> builtinLibraries = new HashMap<String, Library>();
 
     protected final Map<String, JarFile> jarFiles = new HashMap<String, JarFile>();
 
     protected final Map<String, IAutoloadMethod> autoloadMap = new HashMap<String, IAutoloadMethod>();
 
     protected final Ruby runtime;
 
     protected boolean caseInsensitiveFS = false;
     
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
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_rubylib = runtime.newString("RUBYLIB");
         if (env.has_key_p(env_rubylib).isTrue()) {
             String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
             String[] paths = rubylib.split(File.pathSeparator);
             for (int i = 0; i < paths.length; i++) {
                 addPath(paths[i]);
             }
         }
 
         // wrap in try/catch for security exceptions in an applet
         try {
             String jrubyHome = runtime.getJRubyHome();
             if (jrubyHome != null) {
                 char sep = '/';
                 String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
                 // If we're running in 1.9 compat mode, add Ruby 1.9 libs to path before 1.8 libs
                 if (runtime.is1_9()) {
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY1_9_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + Constants.RUBY1_9_MAJOR_VERSION);
                 } else {
                     // Add 1.8 libs
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
                 }
             }
 
             String lowerCaseJRubyHome = jrubyHome.toLowerCase();
             String upperCaseJRubyHome = lowerCaseJRubyHome.toUpperCase();
 
             try {
                 String canonNormal = new File(jrubyHome).getCanonicalPath();
                 String canonLower = new File(lowerCaseJRubyHome).getCanonicalPath();
                 String canonUpper = new File(upperCaseJRubyHome).getCanonicalPath();
                 if (canonNormal.equals(canonLower) && canonLower.equals(canonUpper)) {
                     caseInsensitiveFS = true;
                 }
             } catch (Exception e) {}
 
         } catch(SecurityException ignore) {}
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (!runtime.is1_9() && runtime.getSafeLevel() == 0) {
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
             throw newLoadErrorFromThrowable(runtime, file, e);
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
         if (Platform.IS_WINDOWS) {
             file = file.replace('\\', '/');
         }
 
         SearchState state;
 
         try {
             // Even if we don't support .so, some stdlib require .so directly.
             // Replace it with .jar to look for a java extension
             // JRUBY-5033: The ExtensionSearcher will locate C exts, too, this way.
             if (file.endsWith(".so")) {
                 file = file.replaceAll(".so$", ".jar");
             }
             state = findFileForLoad(file);
 
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
             indent.incrementAndGet();
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
 
         if (featureAlreadyLoaded(RubyString.newString(runtime, file))) {
             return false;
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
 
-            Library library = (Library) classLoader.loadClass(className).newInstance();
-
-            library.load(runtime, false);
+            Object libObject = classLoader.loadClass(className).newInstance();
+            if (libObject instanceof Library) {
+                Library library = (Library)libObject;
+                library.load(runtime, false);
+            } else if (libObject instanceof BasicLibraryService) {
+                BasicLibraryService service = (BasicLibraryService)libObject;
+                service.basicLoad(runtime);
+            }
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
         if (caseInsensitiveFS) {
             // on a case-insensitive filesystem, we need to search case-insensitively
             // to remove the loaded feature
             for (Iterator iter = loadedFeaturesInternal.iterator(); iter.hasNext();) {
                 Object feature = iter.next();
                 if (feature.toString().equalsIgnoreCase(name)) {
                     iter.remove();
                 }
             }
         } else {
             loadedFeaturesInternal.remove(name);
         }
     }
 
     protected boolean featureAlreadyLoaded(RubyString loadNameRubyString) {
         if (caseInsensitiveFS) {
             String name = loadNameRubyString.toString();
             // on a case-insensitive filesystem, we need to search case-insensitively
             // to find the loaded feature
             for (Iterator iter = loadedFeaturesInternal.iterator(); iter.hasNext();) {
                 Object feature = iter.next();
                 if (feature.toString().equalsIgnoreCase(name)) {
                     return true;
                 }
             }
             return false;
         } else {
             return loadedFeaturesInternal.contains(loadNameRubyString);
         }
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
     
     public static class AlreadyLoaded extends Exception {
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
             return state.library == null;
         }
 
         protected void trySearch(String file, SuffixType suffixType) throws AlreadyLoaded {
             for (String suffix : suffixType.getSuffixes()) {
                 String searchName = file + suffix;
                 RubyString searchNameString = RubyString.newString(runtime, searchName);
                 if (featureAlreadyLoaded(searchNameString)) {
                     throw new AlreadyLoaded(searchNameString);
                 }
             }
         }
 
         public void trySearch(SearchState state) throws AlreadyLoaded {
             trySearch(state.searchFile, state.suffixType);
         }
     }
 
     public class SourceBailoutSearcher extends BailoutSearcher {
         public boolean shouldTrySearch(SearchState state) {
             // JRUBY-5032: Load extension files if they are required
             // explicitely, and even if an rb file of the same name
             // has already been loaded (effectively skipping the search for a source file).
             return !extensionPattern.matcher(state.loadName).find();
         }
 
         // According to Rubyspec, source files should be loaded even if an equally named
         // extension is loaded already. So we use the bailout search twice, once only
         // for source files and once for whatever suffix type the state determines
         public void trySearch(SearchState state) throws AlreadyLoaded {
             super.trySearch(state.searchFile, SuffixType.Source);
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
                 runtime.getGlobalVariables().clear("$!");
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
 
     public static class SearchState {
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
         
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(this.getClass().getName()).append(": ");
             sb.append("library=").append(library.toString());
             sb.append(", loadName=").append(loadName);
             sb.append(", suffixType=").append(suffixType.toString());
             sb.append(", searchFile=").append(searchFile);
             return sb.toString();
         }
     }
     
     protected boolean tryLoadingLibraryOrScript(Ruby runtime, SearchState state) {
         // attempt to load the found library
         RubyString loadNameRubyString = RubyString.newString(runtime, state.loadName);
         try {
             synchronized (loadedFeaturesInternal) {
                 if (featureAlreadyLoaded(loadNameRubyString)) {
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
             
             RaiseException re = newLoadErrorFromThrowable(runtime, state.searchFile, e);
             re.initCause(e);
             throw re;
         }
     }
 
     private static RaiseException newLoadErrorFromThrowable(Ruby runtime, String file, Throwable t) {
         return runtime.newLoadError(String.format("load error: %s -- %s: %s", file, t.getClass().getName(), t.getMessage()));
     }
 
     // Using the BailoutSearch twice, once only for source files and once for state suffixes,
     // in order to adhere to Rubyspec
     protected final List<LoadSearcher> searchers = new ArrayList<LoadSearcher>();
     {
         searchers.add(new SourceBailoutSearcher());
         searchers.add(new NormalSearcher());
         searchers.add(new ClassLoaderSearcher());
         searchers.add(new BailoutSearcher());
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
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             String resourceUrl;
             try {
                 resourceUrl = resource.getURL().toString();
             } catch (IOException e) {
                 resourceUrl = e.getMessage();
             }
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
                 state.loadName = resolveLoadName(resource, file);
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
         if (file.endsWith(".so") || file.endsWith(".dll") || file.endsWith(".bundle")) {
             return new CExtension(resource);
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
                     foundResource = new LoadServiceResource(file, getFileName(file, namePlusSuffix), absolute);
                     debugLogFound(foundResource);
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromHome(SearchState state, String baseName, SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_home = runtime.newString("HOME");
         if (env.has_key_p(env_home).isFalse()) {
             return null;
         }
         String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
         String path = baseName.substring(2);
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = path + suffix;
             // check home directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(home, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromHome", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
 
                     state.loadName = file.getPath();
                     foundResource = new LoadServiceResource(file, state.loadName, absolute);
                     debugLogFound(foundResource);
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
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }
         } else if(baseName.startsWith("file:") && baseName.indexOf("!/") != -1) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     String jarFile = namePlusSuffix.substring(5, namePlusSuffix.indexOf("!/"));
                     JarFile file = new JarFile(jarFile);
                     String expandedFilename = expandRelativeJarPath(namePlusSuffix.substring(namePlusSuffix.indexOf("!/") + 2));
 
                     debugLogTry("resourceFromJarURL", expandedFilename.toString());
                     if(file.getJarEntry(expandedFilename) != null) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + jarFile + "!/" + expandedFilename), namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch(Exception e) {}
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
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
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if it's a ~/ baseName use HOME logic
         if (baseName.startsWith("~/")) {
             foundResource = tryResourceFromHome(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if given path is absolute, just try it as-is (with extensions) and no load path
         if (new File(baseName).isAbsolute() || baseName.startsWith("../")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 foundResource = tryResourceAsIs(namePlusSuffix);
 
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     return foundResource;
                 }
             }
 
             return null;
         }
         
         Outer: for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             RubyString entryString = loadPath.eltInternal(i).convertToString();
             String loadPathEntry = entryString.asJavaString();
 
             if (loadPathEntry.equals(".") || loadPathEntry.equals("")) {
                 foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
                 if (foundResource != null) {
                     String ss = foundResource.getName();
                     if(ss.startsWith("./")) {
                         ss = ss.substring(2);
                     }
                     state.loadName = resolveLoadName(foundResource, ss);
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
                         state.loadName = resolveLoadName(foundResource, ss);
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
                 if (runtime.getInstanceConfig().isDebug()) {
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
                 debugLogTry("resourceFromLoadPath", "'" + actualPath.toString() + "' " + actualPath.isFile() + " " + actualPath.canRead());
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
 
                     actualPath = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
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
 
         // absolute classpath URI, no need to iterate over loadpaths
         if (name.startsWith("classpath:/")) {
             LoadServiceResource foundResource = getClassPathResource(classLoader, name);
             if (foundResource != null) {
                 return foundResource;
             }
         } else if (name.startsWith("classpath:")) {
             // "relative" classpath URI
             name = name.substring("classpath:".length());
         }
 
         for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             RubyString entryString = loadPath.eltInternal(i).convertToString();
             String entry = entryString.asJavaString();
 
             // if entry is an empty string, skip it
             if (entry.length() == 0) continue;
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
 
             if (entry.startsWith("classpath:/")) {
                 entry = entry.substring("classpath:/".length());
             } else if (entry.startsWith("classpath:")) {
                 entry = entry.substring("classpath:".length());
             }
 
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             LoadServiceResource foundResource = getClassPathResource(classLoader, entry + "/" + name);
             if (foundResource != null) {
                 return foundResource;
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         LoadServiceResource foundResource = getClassPathResource(classLoader, name);
         if (foundResource != null) {
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
 
     protected LoadServiceResource getClassPathResource(ClassLoader classLoader, String name) {
         boolean isClasspathScheme = false;
 
         // strip the classpath scheme first
         if (name.startsWith("classpath:/")) {
             isClasspathScheme = true;
             name = name.substring("classpath:/".length());
         } else if (name.startsWith("classpath:")) {
             isClasspathScheme = true;
             name = name.substring("classpath:".length());
         }
 
         debugLogTry("fileInClasspath", name);
         URL loc = classLoader.getResource(name);
 
         if (loc != null) { // got it
             String path = "classpath:/" + name;
             // special case for typical jar:file URLs, but only if the name didn't have
             // the classpath scheme explicitly
             if (!isClasspathScheme &&
                     (loc.getProtocol().equals("jar") || loc.getProtocol().equals("file"))
                     && isRequireable(loc)) {
                 path = loc.getPath();
             }
             LoadServiceResource foundResource = new LoadServiceResource(loc, path);
             debugLogFound(foundResource);
             return foundResource;
         }
         return null;
     }
 
     // Canonicalization here is only used to expand '.' and '..' in jar
     // paths, not for real files that exist on the filesystem
     private String expandRelativeJarPath(String path) {
         try {
             String cwd = new File(".").getCanonicalPath();
             return new File(path).getCanonicalPath()
                 .substring(cwd.length() + 1)
                 .replaceAll("\\\\","/");
         } catch(Exception e) {
             return path;
         }
     }
 
     protected String resolveLoadName(LoadServiceResource foundResource, String previousPath) {
         return previousPath;
     }
 
     protected String getFileName(JRubyFile file, String namePlusSuffix) {
         String s = namePlusSuffix;
         if(!namePlusSuffix.startsWith("./")) {
             s = "./" + s;
         }
         return s;
     }
 
     /**
      * Is the jruby home dir on a case-insensitive fs. Determined by comparing
      * a canonicalized jruby home with canonicalized lower and upper-case versions
      * of the same path.
      *
      * @return true if jruby home is on a case-insensitive FS; false otherwise
      */
     public boolean isCaseInsensitiveFS() {
         return caseInsensitiveFS;
     }
 }
