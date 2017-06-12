diff --git a/build.xml b/build.xml
index 0d2ad0ad7..c4d49c921 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1987 +1,1960 @@
 <?xml version="1.0"?>
 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <project name="apache-ant" default="main" basedir=".">
 
   <!-- Give user a chance to override without editing this file
        (and without typing -D on each invocation) -->
   <property file=".ant.properties"/>
   <property file="${user.home}/.ant.properties"/>
   <property environment="env"/>
 
 
   <!--
        ===================================================================
          Set the properties that control names and versions
        ===================================================================
   -->
   <property name="Name" value="Apache Ant"/>
   <property name="name" value="ant"/>
   <!-- this is the groupId of ant in the Maven repository -->
   <property name="groupid" value="org.apache.ant"/>
   <property name="project.version" value="1.8.0alpha"/>
   <!-- pom.version is used when doing a distribution and must match with what is checked in under src/etc/poms -->
   <property name="pom.version" value="1.8.0-SNAPSHOT"/>
   <property name="manifest-version" value="1.8.0"/>
   <property name="bootstrap.jar" value="ant-bootstrap.jar"/>
 
   <property name="ant.package" value="org/apache/tools/ant"/>
   <property name="taskdefs.package" value="${ant.package}/taskdefs"/>
   <property name="condition.package" value="${taskdefs.package}/condition"/>
   <property name="optional.package" value="${taskdefs.package}/optional"/>
   <property name="optional.condition.package" value="${optional.package}/condition"/>
   <property name="type.package" value="${ant.package}/types"/>
   <property name="optional.type.package" value="${type.package}/optional"/>
   <property name="apache.resolver.type.package" value="${ant.package}/types/resolver"/>
   <property name="util.package" value="${ant.package}/util"/>
   <property name="regexp.package" value="${util.package}/regexp"/>
 
   <property name="optional.jars.prefix" value="ant"/>
   <property name="optional.jars.whenmanifestonly" value="skip"/>
 
   <!--
        ===================================================================
          Set the properties related to the source tree
        ===================================================================
   -->
   <property name="src.dir" value="src"/>
   <property name="java.dir" value="${src.dir}/main"/>
   <property name="script.dir" value="${src.dir}/script"/>
   <property name="lib.dir" value="lib"/>
   <property name="docs.dir" value="docs"/>
   <property name="etc.dir" value="${src.dir}/etc"/>
   <property name="src.junit" value="${src.dir}/tests/junit"/>
   <property name="src.antunit" value="${src.dir}/tests/antunit"/>
   <property name="tests.etc.dir" value="${src.dir}/etc/testcases"/>
   <property name="manifest" value="${src.dir}/etc/manifest"/>
   <property name="resource.dir" value="${src.dir}/resources"/>
 
   <!--
        ===================================================================
          Set the properties for the build area
        ===================================================================
   -->
   <property name="build.dir" value="build"/>
   <property name="bootstrap.dir" value="bootstrap"/>
   <property name="build.classes" value="${build.dir}/classes"/>
   <property name="build.lib" value="${build.dir}/lib"/>
   <property name="build.javadocs" value="${build.dir}/javadocs"/>
   <property name="build.tests" value="${build.dir}/testcases"/>
   <property name="build.tests.javadocs" value="${build.dir}/javadocs.test/"/>
   <property name="build.junit.xml" location="${build.tests}/xml"/>
   <property name="antunit.xml" location="${build.dir}/antunit/xml"/>
   <property name="antunit.reports" location="${build.dir}/antunit/reports"/>
   <property name="build.junit.reports" location="${build.tests}/reports"/>
   <property name="manifest.tmp" value="${build.dir}/optional.manifest"/>
   <!-- the absolute path -->
   <property name="build.tests.value" location="${build.tests}"/>
 
   <!--
        ===================================================================
          Set the properties that control various build options
        ===================================================================
   -->
   <property name="debug" value="true"/>
   <property name="chmod.fail" value="true"/>
   <property name="chmod.maxparallel" value="250"/>
   <property name="deprecation" value="false"/>
   <property name="optimize" value="true"/>
   <property name="javac.target" value="1.2"/>
   <property name="javac.source" value="1.2"/>
   <property name="junit.fork" value="false"/>
   <property name="junit.filtertrace" value="off"/>
   <property name="junit.summary" value="no"/>
   <property name="test.haltonfailure" value="false"/>
   <property name="junit.forkmode" value="once"/>
   <property name="expandproperty.files"
             value="**/version.txt,**/defaultManifest.mf"/>
   <property name="junit.collector.dir" value="${build.dir}/failingTests"/>
   <property name="junit.collector.class" value="FailedTests"/>
 
   <!--
        ===================================================================
          Set the paths used in the build
        ===================================================================
   -->
   <path id="classpath">
     <fileset dir="lib/optional" includes="junit*.jar"/>
   </path>
 
   <path id="tests-classpath">
     <pathelement location="${build.classes}"/>
     <pathelement location="${build.tests}"/>
     <!--
         include the test source and test data dirs
         so that we can pick resources via getResource(AsStream)
      -->
     <pathelement location="${src.junit}"/>
     <pathelement location="${tests.etc.dir}"/>
     <path refid="classpath"/>
   </path>
 
   <!-- turn this path into a string which is passed to the tests -->
   <property name="tests-classpath.value"
     refid="tests-classpath"/>
 
   <!--
         ===================================================================
           Set up properties for the distribution area
         ===================================================================
    -->
   <property name="dist.name" value="apache-${name}-${project.version}"/>
   <property name="dist.base" value="distribution"/>
   <property name="dist.base.source" value="${dist.base}/source"/>
   <property name="dist.base.binaries" value="${dist.base}/binaries"/>
   <property name="dist.dir" value="dist"/>
   <property name="dist.bin" value="${dist.dir}/bin"/>
   <property name="dist.lib" value="${dist.dir}/lib"/>
   <property name="dist.docs" value="${dist.dir}/docs"/>
   <property name="dist.etc" value="${dist.dir}/etc"/>
   <property name="dist.javadocs" value="${dist.dir}/docs/manual/api"/>
 
   <property name="src.dist.dir" value="dist-src"/>
   <property name="src.dist.src" value="${src.dist.dir}/src"/>
   <property name="src.dist.docs" value="${src.dist.dir}/docs"/>
   <property name="src.dist.lib" value="${src.dist.dir}/lib"/>
 
   <property name="java-repository.dir" value="java-repository/${groupid}"/>
   <property name="java-repository.jars.dir" value="${java-repository.dir}/jars"/>
   <property name="java-repository.poms.dir" value="${java-repository.dir}/poms"/>
 
   <!--
        ===================================================================
          Set up selectors to be used by javac, junit and jar to exclude
          files that have dependencies that are not available
        ===================================================================
   -->
   <!-- depends on JDK version -->
-  <selector id="needs.jdk1.3+">
-    <or>
-      <filename name="${ant.package}/taskdefs/TestProcess*"/>
-      <filename name="${optional.package}/extension/**"/>
-    </or>
-  </selector>
-
-  <selector id="needs.jdk1.4+">
-    <or>
-      <filename name="${regexp.package}/Jdk14Regexp*"/>
-      <filename name="${ant.package}/types/AssertionsTest.java"/>
-      <filename name="${ant.package}/launch/LocatorTest*"/>
-    </or>
-  </selector>
-
   <selector id="needs.jdk1.5+">
     <or>
       <filename name="${taskdefs.package}/AptTest*"/>
       <filename name="${util.package}/java15/*"/>
     </or>
   </selector>
 
   <!-- Kaffe has some JDK 1.5 features including java.lang.Readable,
        but not all of them -->
   <selector id="not.in.kaffe">
     <or>
       <filename name="${condition.package}/IsReachable*"/>
     </or>
   </selector>
 
   <!-- depends on external libraries -->
   <selector id="needs.trax">
     <or>
       <filename name="${optional.package}/TraXLiaison*"/>
+      <filename name="${optional.package}/XSLTTraceSupport*"/>
       <filename name="${optional.package}/XsltTest*"/>
       <filename name="${type.package}/XMLCatalogBuildFileTest*"/>
     </or>
   </selector>
 
   <selector id="needs.apache-resolver">
     <filename name="${apache.resolver.type.package}/**"/>
   </selector>
 
   <selector id="needs.junit">
     <filename name="${optional.package}/junit/**"/>
   </selector>
 
   <selector id="needs.apache-regexp">
     <filename name="${regexp.package}/JakartaRegexp*"/>
   </selector>
 
   <selector id="needs.apache-oro">
     <or>
       <filename name="${regexp.package}/JakartaOro*"/>
       <filename name="${optional.package}/perforce/*"/>
     </or>
   </selector>
 
   <selector id="needs.apache-bcel">
     <or>
       <filename name="${ant.package}/filters/util/JavaClassHelper*"/>
       <filename name="${util.package}/depend/bcel/*"/>
       <filename name="${optional.type.package}/depend/ClassFileSetTest*"/>
     </or>
   </selector>
 
   <selector id="needs.apache-log4j">
     <filename name="${ant.package}/listener/Log4jListener*"/>
   </selector>
 
   <selector id="needs.commons-logging">
     <filename name="${ant.package}/listener/CommonsLoggingListener*"/>
   </selector>
 
   <selector id="needs.apache-bsf">
     <or>
       <filename name="${util.package}/ScriptRunner.*"/>
       <filename name="${util.package}/optional/ScriptRunner*"/>
     </or>
   </selector>
 
   <selector id="needs.stylebook">
     <filename name="${optional.package}/StyleBook*"/>
   </selector>
 
   <selector id="needs.javamail">
     <or>
       <filename name="${ant.package}/taskdefs/email/MimeMailer*"/>
     </or>
   </selector>
 
   <selector id="needs.netrexx">
     <filename name="${optional.package}/NetRexxC*"/>
   </selector>
 
   <selector id="needs.commons-net">
     <or>
       <filename name="${optional.package}/net/FTP*"/>
       <filename name="${optional.package}/net/RExec*"/>
       <filename name="${optional.package}/net/TelnetTask*"/>
     </or>
   </selector>
 
   <selector id="needs.antlr">
     <filename name="${optional.package}/ANTLR*"/>
   </selector>
 
   <selector id="needs.jmf">
     <filename name="${optional.package}/sound/*"/>
   </selector>
 
   <selector id="needs.jai">
     <or>
       <filename name="${optional.package}/image/*"/>
       <filename name="${optional.type.package}/image/*"/>
     </or>
   </selector>
 
   <selector id="needs.jdepend">
     <filename name="${optional.package}/jdepend/*"/>
   </selector>
 
   <selector id="needs.swing">
     <filename name="${optional.package}/splash/*"/>
   </selector>
 
   <selector id="needs.jsch">
     <filename name="${optional.package}/ssh/*"/>
   </selector>
 
+  <!-- needs TraceListenerEx3 interface implemented by PrintTraceListener -->
+  <selector id="needs.apache-xalan2">
+    <filename name="${optional.package}/Xalan2TraceSupport*"/>
+  </selector>
+
   <selector id="ant.launcher">
     <filename name="${ant.package}/launch/**/*"/>
   </selector>
 
   <patternset id="onlinetests">
     <exclude name="**/GetTest.java" if="offline"/>
   </patternset>
 
   <patternset id="teststhatfail">
     <!-- Property 'run.failing.tests' should force Ant to run these tests. -->
     <!-- Because the whole patternset can not be excluded, you have to add -->
     <!-- an unless-attribute on each exclude-element.                      -->
     <exclude unless="run.failing.tests" name="${optional.package}/BeanShellScriptTest.java"/>
     <exclude unless="run.failing.tests" name="${optional.package}/jdepend/JDependTest.java"/>
   </patternset>
 
   <!--tests that need an XML Schema-supporting parser to work-->
   <selector id="needs.xmlschema">
     <or>
       <filename name="${optional.package}/SchemaValidateTest.*"/>
       <filename name="${optional.package}/XmlValidateTest.*"/>
     </or>
   </selector>
 
   <!--
        ===================================================================
          Set up a patternsets that matches the parts of our JUnit testsuite
          that may be useful for task developers.
        ===================================================================
   -->
   <patternset id="useful.tests">
     <include name="${ant.package}/BuildFileTest*"/>
     <include name="${regexp.package}/RegexpMatcherTest*"/>
     <include name="${regexp.package}/RegexpTest*"/>
     <include name="${optional.package}/AbstractXSLTLiaisonTest*"/>
     <include name="${ant.package}/types/AbstractFileSetTest*"/>
   </patternset>
 
   <!--
        ===================================================================
          Set up a patternsets that matches the parts of our site that
          should not be part of the distribution.
        ===================================================================
   -->
   <patternset id="site.excludes">
     <exclude name="bindownload.html"/>
     <exclude name="srcdownload.html"/>
     <exclude name="*.cgi"/>
   </patternset>
 
   <!--
        ===================================================================
          Check to see what optional dependencies are available
        ===================================================================
   -->
   <target name="check_for_optional_packages">
-    <available property="jdk1.3+" classname="java.lang.StrictMath"/>
-    <available property="jdk1.4+" classname="java.lang.CharSequence"/>
     <available property="jdk1.5+" classname="java.net.Proxy"/>
     <available property="jdk1.6+" classname="java.util.ServiceLoader"/>
     <available property="kaffe" classname="kaffe.util.NotImplemented"/>
     <available property="bsf.present"
       classname="org.apache.bsf.BSFManager"
       classpathref="classpath"/>
     <available property="netrexx.present"
       classname="netrexx.lang.Rexx"
       classpathref="classpath"/>
     <available property="trax.present"
       classname="javax.xml.transform.Transformer"
       classpathref="classpath"/>
     <condition property="trax.impl.present">
       <or>
         <and>
           <isset property="javax.xml.transform.TransformerFactory"/>
           <available classname="${javax.xml.transform.TransformerFactory}"
             classpathref="classpath"/>
         </and>
         <available resource="META-INF/services/javax.xml.transform.TransformerFactory"/>
       </or>
     </condition>
     <available property="apache.resolver.present"
       classname="org.apache.xml.resolver.tools.CatalogResolver"
       classpathref="classpath"/>
     <available property="xalan2.present"
       classname="org.apache.xalan.transformer.TransformerImpl"
       classpathref="classpath"/>
+    <available property="recent.xalan2.present"
+      classname="org.apache.xalan.trace.TraceListenerEx3"
+      classpathref="classpath"/>
     <available property="junit.present"
       classname="junit.framework.TestCase"
       classpathref="classpath"/>
     <available property="antunit.present"
       classname="org.apache.ant.antunit.AntUnit"
       classpathref="classpath"/>
     <available property="commons.net.present"
       classname="org.apache.commons.net.ftp.FTPClient"
       classpathref="classpath"/>
     <available property="antlr.present" 
       classname="antlr.Tool" 
       classpathref="classpath"/>
     <available property="stylebook.present"
       classname="org.apache.stylebook.Engine"
       classpathref="classpath"/>
     <available property="apache.regexp.present"
       classname="org.apache.regexp.RE"
       classpathref="classpath"/>
     <available property="apache.oro.present"
       classname="org.apache.oro.text.regex.Perl5Matcher"
       classpathref="classpath"/>
     <available property="jmf.present"
       classname="javax.sound.sampled.Clip"
       classpathref="classpath"/>
     <available property="jai.present"
       classname="javax.media.jai.JAI"
       classpathref="classpath"/>
     <available property="jdepend.present"
       classname="jdepend.framework.JDepend"
       classpathref="classpath"/>
     <available property="log4j.present"
       classname="org.apache.log4j.Logger"
       classpathref="classpath"/>
     <available property="commons.logging.present"
       classname="org.apache.commons.logging.LogFactory"
       classpathref="classpath"/>
     <available property="xalan.envcheck"
       classname="org.apache.xalan.xslt.EnvironmentCheck"
       classpathref="classpath"/>
     <available property="which.present"
       classname="org.apache.env.Which"
       classpathref="classpath"/>
 
     <available property="xerces.present"
       classname="org.apache.xerces.parsers.SAXParser"
       classpathref="classpath"/>
     <available property="bcel.present"
       classname="org.apache.bcel.Constants"
       classpathref="classpath"/>
 
     <condition property="javamail.complete">
       <and>
         <available classname="javax.activation.DataHandler"
           classpathref="classpath"/>
         <available classname="javax.mail.Transport"
           classpathref="classpath"/>
       </and>
     </condition>
 
-    <condition property="some.regexp.support">
-      <or>
-        <isset property="jdk1.4+"/>
-        <isset property="apache.regexp.present"/>
-        <isset property="apache.oro.present"/>
-      </or>
-    </condition>
-
     <condition property="tests.and.ant.share.classloader">
       <or>
         <equals arg1="${junit.fork}" arg2="true"/>
         <equals arg1="${build.sysclasspath}" arg2="only"/>
       </or>
     </condition>
 
     <condition property="sun.tools.present">
       <and>
         <available classname="sun.tools.native2ascii.Main"/>
         <available classname="com.sun.tools.javah.Main"/>
       </and>
     </condition>
 
     <condition property="tests.are.on.system.classpath">
       <or>
         <resourcecount count="1">
           <intersect>
             <path path="${java.class.path}" />
             <file file="${build.tests}" />
           </intersect>
         </resourcecount>
         <istrue value="${junit.fork}"/>
       </or>
     </condition>
 
     <echo level="verbose"> tests.are.on.system.classpath=${tests.are.on.system.classpath}</echo>
 
     <condition property="jasper.present">
       <and>
         <available classname="org.apache.jasper.compiler.Compiler"/>
         <available classname="org.apache.jasper.JasperException"/>
       </and>
     </condition>
 
     <condition property="swing.present">
       <or>
         <not>
           <isset property="kaffe"/>
         </not>
         <available classname="javax.swing.ImageIcon"
           classpathref="classpath"/>
       </or>
     </condition>
 
     <!-- http client needs commons logging -->
     <condition property="apache-httpclient.present">
       <and>
         <available
           classname="org.apache.commons.httpclient.HttpClient"
           classpathref="classpath"/>
         <isset property="commons.logging.present"/>
       </and>
     </condition>
 
     <available property="rhino.present"
       classname="org.mozilla.javascript.Scriptable"
       classpathref="classpath"/>
     <available property="beanshell.present"
       classname="bsh.StringUtil"
       classpathref="classpath"/>
     <available property="xerces1.present"
       classname="org.apache.xerces.framework.XMLParser"
       classpathref="classpath"/>
     <available property="jsch.present"
       classname="com.jcraft.jsch.Session"
       classpathref="classpath"/>
 
-    <condition property="build.compiler" value="classic">
-      <not>
-        <isset property="jdk1.3+"/>
-      </not>
-    </condition>
     <property name="build.compiler" value="modern"/>
 
     <!--check for XSD support in the parser-->
     <condition property="xmlschema.present">
       <or>
         <parsersupports
           feature="http://apache.org/xml/features/validation/schema"/>
         <parsersupports
           feature="http://java.sun.com/xml/jaxp/properties/schemaSource"/>
       </or>
     </condition>
 
   </target>
 
 
   <!--
        ===================================================================
          Prepare the build
        ===================================================================
   -->
   <target name="prepare">
     <tstamp>
       <format property="year" pattern="yyyy"/>
     </tstamp>
     <filterchain id="ant.filters">
        <expandproperties/>
     </filterchain>
   </target>
 
   <!--
        ===================================================================
          Build the code
        ===================================================================
   -->
   <target name="build"
     depends="prepare, check_for_optional_packages"
     description="--> compiles the source code">
     <mkdir dir="${build.dir}"/>
     <mkdir dir="${build.classes}"/>
     <mkdir dir="${build.lib}"/>
 
     <javac srcdir="${java.dir}"
       destdir="${build.classes}"
       debug="${debug}"
       deprecation="${deprecation}"
       target="${javac.target}"
       source="${javac.source}"
       optimize="${optimize}">
       <classpath refid="classpath"/>
 
       <selector id="conditional-patterns">
         <not>
           <or>
-            <selector refid="needs.jdk1.3+" unless="jdk1.3+"/>
-            <selector refid="needs.jdk1.4+" unless="jdk1.4+"/>
             <selector refid="needs.jdk1.5+" unless="jdk1.5+"/>
             <selector refid="not.in.kaffe" if="kaffe"/>
 
             <selector refid="needs.trax" unless="trax.present"/>
             <selector refid="needs.apache-resolver" unless="apache.resolver.present"/>
             <selector refid="needs.junit" unless="junit.present"/>
             <selector refid="needs.apache-regexp"
               unless="apache.regexp.present"/>
             <selector refid="needs.apache-oro" unless="apache.oro.present"/>
             <selector refid="needs.apache-bcel" unless="bcel.present"/>
             <selector refid="needs.apache-log4j" unless="log4j.present"/>
             <selector refid="needs.commons-logging"
               unless="commons.logging.present"/>
             <selector refid="needs.apache-bsf" unless="bsf.present"/>
             <selector refid="needs.stylebook" unless="stylebook.present"/>
             <selector refid="needs.javamail" unless="javamail.complete"/>
             <selector refid="needs.netrexx" unless="netrexx.present"/>
             <selector refid="needs.commons-net" unless="commons.net.present"/>
             <selector refid="needs.antlr" unless="antlr.present"/>
             <selector refid="needs.jmf" unless="jmf.present"/>
             <selector refid="needs.jai" unless="jai.present"/>
             <selector refid="needs.jdepend" unless="jdepend.present"/>
             <selector refid="needs.swing" unless="swing.present"/>
             <selector refid="needs.jsch" unless="jsch.present"/>
             <selector refid="needs.xmlschema" unless="xmlschema.present"/>
+            <selector refid="needs.apache-xalan2"
+                      unless="recent.xalan2.present"/>
+            <!-- Java 1.4's built-in Xalan is first on the classpath -->
+            <selector refid="needs.apache-xalan2" unless="jdk1.5+"/>
           </or>
         </not>
       </selector>
     </javac>
 
     <copy todir="${build.classes}">
       <fileset dir="${java.dir}">
         <include name="**/*.properties"/>
         <include name="**/*.dtd"/>
         <include name="**/*.xml"/>
       </fileset>
       <fileset dir="${resource.dir}" />
     </copy>
 
     <copy todir="${build.classes}"
       overwrite="true" encoding="UTF-8">
       <fileset dir="${java.dir}">
         <include name="**/version.txt"/>
         <include name="**/defaultManifest.mf"/>
       </fileset>
       <filterchain refid="ant.filters"/>
     </copy>
 
     <copy todir="${build.classes}/${optional.package}/junit/xsl">
       <fileset dir="${etc.dir}">
         <include name="junit-frames.xsl"/>
         <include name="junit-noframes.xsl"/>
       </fileset>
     </copy>
   </target>
 
   <!--
        ===================================================================
          Create the all of the Apache Ant jars
        ===================================================================
   -->
   <target name="jars"
     depends="build"
     description="--> creates the Apache Ant jars">
 
     <copy todir="${build.dir}">
       <fileset dir="${basedir}">
         <include name="LICENSE"/>
         <include name="LICENSE.xerces"/>
         <include name="LICENSE.dom"/>
         <include name="LICENSE.sax"/>
         <include name="NOTICE"/>
       </fileset>
       <mapper type="glob" from="*" to="*.txt"/>
     </copy>
 
     <copy file="${manifest}" tofile="${manifest.tmp}"/>
     <manifest file="${manifest.tmp}">
       <section name="${optional.package}/">
         <attribute name="Extension-name"
           value="org.apache.tools.ant"/>
         <attribute name="Specification-Title"
           value="Apache Ant"/>
         <attribute name="Specification-Version"
           value="${manifest-version}"/>
         <attribute name="Specification-Vendor"
           value="Apache Software Foundation"/>
         <attribute name="Implementation-Title"
           value="org.apache.tools.ant"/>
         <attribute name="Implementation-Version"
           value="${manifest-version}"/>
         <attribute name="Implementation-Vendor"
           value="Apache Software Foundation"/>
       </section>
     </manifest>
 
     <jar destfile="${build.lib}/${name}-launcher.jar"
       basedir="${build.classes}"
       whenmanifestonly="fail">
       <selector refid="ant.launcher"/>
       <manifest>
         <attribute name="Main-Class" value="org.apache.tools.ant.launch.Launcher"/>
       </manifest>
     </jar>
 
     <jar destfile="${build.lib}/${name}.jar"
       basedir="${build.classes}"
       manifest="${manifest}"
       whenmanifestonly="fail">
       <not>
         <selector id="non-core">
           <or>
             <filename name="${optional.package}/**"/>
             <filename name="${optional.type.package}/**"/>
             <filename name="${util.package}/depend/**"/>
             <filename name="${util.package}/optional/**"/>
             <selector refid="needs.apache-log4j"/>
             <selector refid="needs.commons-logging"/>
             <selector refid="needs.apache-bcel"/>
             <selector refid="needs.apache-bsf"/>
             <selector refid="needs.apache-regexp"/>
             <selector refid="needs.apache-resolver"/>
             <selector refid="needs.apache-oro"/>
-            <selector refid="needs.jdk1.4+"/>
             <selector refid="needs.jdk1.5+"/>
             <selector refid="needs.javamail"/>
             <selector refid="ant.launcher"/>
           </or>
         </selector>
       </not>
       <metainf dir="${build.dir}">
         <include name="LICENSE.txt"/>
         <include name="NOTICE.txt"/>
       </metainf>
 
       <manifest>
         <section name="${ant.package}/">
           <attribute name="Extension-name"
             value="org.apache.tools.ant"/>
           <attribute name="Specification-Title"
             value="Apache Ant"/>
           <attribute name="Specification-Version"
             value="${manifest-version}"/>
           <attribute name="Specification-Vendor"
             value="Apache Software Foundation"/>
           <attribute name="Implementation-Title"
             value="org.apache.tools.ant"/>
           <attribute name="Implementation-Version"
             value="${manifest-version}"/>
           <attribute name="Implementation-Vendor"
             value="Apache Software Foundation"/>
         </section>
       </manifest>
 
       <fileset dir="${docs.dir}">
         <include name="images/ant_logo_large.gif"/>
       </fileset>
     </jar>
 
     <jar destfile="${build.lib}/${bootstrap.jar}"
       basedir="${build.classes}"
       manifest="${manifest}"
       whenmanifestonly="fail">
       <include name="${ant.package}/Main.class"/>
       <metainf dir="${build.dir}">
         <include name="LICENSE.txt"/>
         <include name="NOTICE.txt"/>
       </metainf>
       <manifest>
         <attribute name="Class-Path"
           value="ant.jar xml-apis.jar xercesImpl.jar xalan.jar"/>
       </manifest>
     </jar>
 
     <jar destfile="${build.lib}/ant-nodeps.jar"
       basedir="${build.classes}"
       manifest="${manifest.tmp}"
       whenmanifestonly="${optional.jars.whenmanifestonly}">
       <and>
         <selector refid="non-core"/>
         <not>
           <or>
             <selector refid="ant.launcher"/>
             <selector refid="needs.trax"/>
             <selector refid="needs.apache-resolver"/>
             <selector refid="needs.junit"/>
             <selector refid="needs.apache-regexp"/>
             <selector refid="needs.apache-oro"/>
             <selector refid="needs.apache-bcel"/>
             <selector refid="needs.apache-log4j"/>
             <selector refid="needs.commons-logging"/>
             <selector refid="needs.apache-bsf"/>
             <selector refid="needs.stylebook"/>
             <selector refid="needs.javamail"/>
             <selector refid="needs.netrexx"/>
             <selector refid="needs.commons-net"/>
             <selector refid="needs.antlr"/>
             <selector refid="needs.jmf"/>
             <selector refid="needs.jai"/>
             <selector refid="needs.jdepend"/>
             <selector refid="needs.swing"/>
             <selector refid="needs.jsch"/>
+            <selector refid="needs.apache-xalan2"/>
           </or>
         </not>
       </and>
       <metainf dir="${build.dir}">
         <include name="LICENSE.txt"/>
         <include name="NOTICE.txt"/>
       </metainf>
     </jar>
 
     <macrodef name="optional-jar">
       <attribute name="dep"/>
       <sequential>
         <jar destfile="${build.lib}/${optional.jars.prefix}-@{dep}.jar"
           basedir="${build.classes}"
           manifest="${manifest.tmp}"
           whenmanifestonly="${optional.jars.whenmanifestonly}">
           <selector refid="needs.@{dep}"/>
         </jar>
       </sequential>
     </macrodef>
 
     <optional-jar dep="trax"/>
     <optional-jar dep="apache-resolver"/>
     <optional-jar dep="junit"/>
     <optional-jar dep="apache-regexp"/>
     <optional-jar dep="apache-oro"/>
     <optional-jar dep="apache-bcel"/>
     <optional-jar dep="apache-log4j"/>
     <optional-jar dep="commons-logging"/>
     <optional-jar dep="apache-bsf"/>
     <optional-jar dep="stylebook"/>
     <optional-jar dep="javamail"/>
     <optional-jar dep="netrexx"/>
     <optional-jar dep="commons-net"/>
     <optional-jar dep="antlr"/>
     <optional-jar dep="jmf"/>
     <optional-jar dep="jai"/>
     <optional-jar dep="swing"/>
     <optional-jar dep="jsch"/>
     <optional-jar dep="jdepend"/>
+    <optional-jar dep="apache-xalan2"/>
 
   </target>
 
   <!--   Creates jar of test utility classes -->
   <target name="test-jar"
     depends="compile-tests"
     description="--> creates the Apache Ant Test Utilities jar">
 
     <fail unless="junit.present">
       We cannot build the test jar unless JUnit is present,
       as JUnit is needed to compile the test classes.
     </fail>
     <jar destfile="${build.lib}/${name}-testutil.jar"
       basedir="${build.tests}">
       <patternset refid="useful.tests"/>
     </jar>
   </target>
 
   <!--
        ===================================================================
          Create the essential distribution that can run Apache Ant
        ===================================================================
   -->
   <target name="dist-lite"
     depends="jars,test-jar"
     description="--> creates a minimum distribution to run Apache Ant">
 
     <mkdir dir="${dist.dir}"/>
     <mkdir dir="${dist.bin}"/>
     <mkdir dir="${dist.lib}"/>
 
     <copy todir="${dist.lib}">
       <fileset dir="${build.lib}">
         <exclude name="${bootstrap.jar}"/>
       </fileset>
     </copy>
 
     <copy todir="${dist.lib}">
       <fileset dir="${lib.dir}">
         <include name="*.jar"/>
         <include name="*.zip"/>
       </fileset>
     </copy>
 
     <copy todir="${dist.bin}">
       <fileset dir="${script.dir}"/>
     </copy>
 
     <fixcrlf srcdir="${dist.bin}" eol="dos" includes="*.bat,*.cmd"/>
     <fixcrlf srcdir="${dist.bin}" eol="unix">
       <include name="ant"/>
       <include name="antRun"/>
       <include name="*.pl"/>
     </fixcrlf>
 
     <chmod perm="ugo+rx" dir="${dist.dir}" type="dir" includes="**"
       failonerror="${chmod.fail}"/>
     <chmod perm="ugo+r" dir="${dist.dir}" type="file" includes="**"
       failonerror="${chmod.fail}" maxparallel="${chmod.maxparallel}"/>
     <chmod perm="ugo+x" type="file" failonerror="${chmod.fail}">
       <fileset dir="${dist.bin}">
         <include name="**/ant"/>
         <include name="**/antRun"/>
         <include name="**/*.pl"/>
         <include name="**/*.py"/>
       </fileset>
     </chmod>
 
   </target>
 
   <!--
         ===================================================================
           Create the complete distribution
         ===================================================================
    -->
   <target name="dist" description="--> creates a complete distribution">
     <antcall inheritAll="false" target="internal_dist">
       <param name="dist.dir" value="${dist.name}"/>
     </antcall>
   </target>
 
   <target name="dist_javadocs" depends="javadocs">
     <mkdir dir="${dist.javadocs}"/>
     <copy todir="${dist.javadocs}" overwrite="true">
       <fileset dir="${build.javadocs}"/>
     </copy>
   </target>
 
 
   <target name="internal_dist" depends="dist-lite,dist_javadocs">
     <mkdir dir="${dist.docs}"/>
     <mkdir dir="${dist.etc}"/>
 
     <copy todir="${dist.lib}" file="${lib.dir}/README"/>
     <copy todir="${dist.lib}" file="${lib.dir}/libraries.properties"/>
 
     <copy todir="${dist.lib}">
         <fileset dir="${src.dir}/etc/poms">
             <include name="*/pom.xml"/>
         </fileset>
         <mapper type="regexp" from="^(.*)[/\\]pom.xml" to="\1-${project.version}.pom"/>
         <filterchain>
             <tokenfilter>
                 <replaceregex pattern="${pom.version}" replace="${project.version}"/>
             </tokenfilter>
         </filterchain>
     </copy>
     <copy todir="${dist.lib}">
         <fileset dir="${src.dir}/etc/poms">
             <include name="pom.xml"/>
         </fileset>
         <mapper type="glob" from="pom.xml" to="ant-parent-${project.version}.pom"/>
         <filterchain>
             <tokenfilter>
                 <replaceregex pattern="${pom.version}" replace="${project.version}"/>
             </tokenfilter>
         </filterchain>
     </copy>
     <checksum algorithm="md5">
         <fileset dir="${dist.lib}">
             <include name="*.pom"/>
         </fileset>
     </checksum>
     <checksum algorithm="sha1">
         <fileset dir="${dist.lib}">
             <include name="*.pom"/>
         </fileset>
     </checksum>
 
     <copy todir="${dist.docs}">
       <fileset dir="${docs.dir}" includes="${expandproperty.files}">
         <patternset refid="site.excludes"/>
       </fileset>
       <filterchain refid="ant.filters"/>
     </copy>
 
     <copy todir="${dist.docs}" filtering="false">
       <fileset dir="${docs.dir}" excludes="${expandproperty.files}">
         <patternset refid="site.excludes"/>
       </fileset>
     </copy>
 
     <copy todir="${dist.dir}">
       <fileset dir="${basedir}">
         <include name="README"/>
         <include name="INSTALL"/>
         <include name="LICENSE"/>
         <include name="LICENSE.xerces"/>
         <include name="LICENSE.dom"/>
         <include name="LICENSE.sax"/>
         <include name="NOTICE"/>
         <include name="TODO"/>
         <include name="WHATSNEW"/>
         <include name="KEYS"/>
         <include name="fetch.xml"/>
 	<include name="get-m2.xml"/>
       </fileset>
     </copy>
 
     <chmod perm="ugo+rx" dir="${dist.dir}" type="dir" includes="**"
       failonerror="${chmod.fail}"/>
     <chmod perm="ugo+r" dir="${dist.dir}" type="file" includes="**"
       failonerror="${chmod.fail}" maxparallel="${chmod.maxparallel}"/>
     <chmod perm="ugo+x" type="file" failonerror="${chmod.fail}">
       <fileset dir="${dist.bin}">
         <include name="**/ant"/>
         <include name="**/antRun"/>
         <include name="**/*.pl"/>
         <include name="**/*.py"/>
       </fileset>
     </chmod>
 
     <!-- publish some useful stylesheets -->
     <copy todir="${dist.etc}">
       <fileset dir="${etc.dir}">
         <include name="junit-frames.xsl"/>
         <include name="junit-noframes.xsl"/>
         <include name="junit-frames-xalan1.xsl"/>
         <include name="coverage-frames.xsl"/>
         <include name="maudit-frames.xsl"/>
         <include name="mmetrics-frames.xsl"/>
         <include name="changelog.xsl"/>
         <include name="jdepend.xsl"/>
         <include name="jdepend-frames.xsl"/>
         <include name="checkstyle/*.xsl"/>
         <include name="log.xsl"/>
         <include name="tagdiff.xsl"/>
       </fileset>
       <fileset dir="${build.lib}">
         <include name="${bootstrap.jar}"/>
       </fileset>
     </copy>
 
   </target>
 
 
   <!--
        ===================================================================
          Target to create bootstrap build
        ===================================================================
   -->
   <target name="bootstrap" description="--> creates a bootstrap build">
     <antcall inheritAll="false" target="dist-lite">
       <param name="dist.dir" value="${bootstrap.dir}"/>
     </antcall>
   </target>
 
 
   <!--
        ===================================================================
          Create the source distribution
        ===================================================================
   -->
   <target name="src-dist"
     description="--> creates a source distribution">
 
     <mkdir dir="${src.dist.dir}"/>
 
     <copy todir="${src.dist.lib}">
       <fileset dir="${lib.dir}">
         <include name="*.jar"/>
         <include name="*.zip"/>
         <include name="README"/>
         <include name="libraries.properties"/>
       </fileset>
     </copy>
 
     <copy todir="${src.dist.src}">
       <fileset dir="${src.dir}"/>
     </copy>
 
     <copy todir="${src.dist.docs}">
       <fileset dir="${docs.dir}">
         <exclude name="manual/api/**"/>
         <patternset refid="site.excludes"/>
       </fileset>
     </copy>
 
     <copy todir="${src.dist.dir}">
       <fileset dir="${basedir}">
         <include name="README"/>
         <include name="INSTALL"/>
         <include name="LICENSE"/>
         <include name="LICENSE.xerces"/>
         <include name="LICENSE.dom"/>
         <include name="LICENSE.sax"/>
         <include name="NOTICE"/>
         <include name="TODO"/>
         <include name="WHATSNEW"/>
         <include name="KEYS"/>
         <include name="build.bat"/>
         <include name="build.sh"/>
         <include name="bootstrap.bat"/>
         <include name="bootstrap.sh"/>
         <include name="build.xml"/>
         <include name="fetch.xml"/>
 	<include name="get-m2.xml"/>
       </fileset>
     </copy>
 
     <fixcrlf srcdir="${src.dist.dir}" eol="dos" includes="*.bat,*.cmd"/>
     <fixcrlf srcdir="${src.dist.dir}" eol="unix">
       <include name="**/*.sh"/>
       <include name="**/*.pl"/>
       <include name="**/ant"/>
       <include name="**/antRun"/>
     </fixcrlf>
     <fixcrlf srcdir="${src.dist.dir}">
       <include name="**/*.java"/>
       <exclude name="${tests.etc.dir}/taskdefs/fixcrlf/expected/Junk?.java"/>
       <exclude name="${tests.etc.dir}/taskdefs/fixcrlf/input/Junk?.java"/>
     </fixcrlf>
 
     <chmod perm="ugo+x" dir="${src.dist.dir}" type="dir"
       failonerror="${chmod.fail}"/>
     <chmod perm="ugo+r" dir="${src.dist.dir}" failonerror="${chmod.fail}"/>
     <chmod perm="ugo+x" failonerror="${chmod.fail}">
       <fileset dir="${src.dist.dir}">
         <include name="**/.sh"/>
         <include name="**/.pl"/>
         <include name="**/.py"/>
         <include name="**/ant"/>
         <include name="**/antRun"/>
       </fileset>
     </chmod>
 
   </target>
 
   <!--
        ===================================================================
          Create the binary distribution
        ===================================================================
   -->
   <target name="main_distribution"
     description="--> creates the zip and tar distributions">
     <delete dir="${dist.base}"/>
     <delete dir="${dist.name}"/>
     <delete dir="${java-repository.dir}"/>
     <mkdir dir="${dist.base}"/>
     <mkdir dir="${dist.base.source}"/>
     <mkdir dir="${dist.base.binaries}"/>
     <mkdir dir="${java-repository.jars.dir}"/>
     <mkdir dir="${java-repository.poms.dir}"/>
     <antcall inheritAll="false" target="internal_dist">
       <param name="dist.dir" value="${dist.name}"/>
     </antcall>
     <zip destfile="${dist.base.binaries}/${dist.name}-bin.zip">
       <zipfileset dir="${dist.name}/.." filemode="755">
         <include name="${dist.name}/bin/ant"/>
         <include name="${dist.name}/bin/antRun"/>
         <include name="${dist.name}/bin/*.pl"/>
         <include name="${dist.name}/bin/*.py"/>
       </zipfileset>
       <fileset dir="${dist.name}/..">
         <include name="${dist.name}/**"/>
         <exclude name="${dist.name}/bin/ant"/>
         <exclude name="${dist.name}/bin/antRun"/>
         <exclude name="${dist.name}/bin/*.pl"/>
         <exclude name="${dist.name}/bin/*.py"/>
       </fileset>
     </zip>
     <tar longfile="gnu"
       destfile="${dist.base.binaries}/${dist.name}-bin.tar">
       <!-- removes redundant definition of permissions, but seems to
            drop dirs (and to be slow)
       <zipfileset src="${dist.base.binaries}/${dist.name}-bin.zip"/>
       -->
       <tarfileset dir="${dist.name}/.." mode="755" username="ant" group="ant">
         <include name="${dist.name}/bin/ant"/>
         <include name="${dist.name}/bin/antRun"/>
         <include name="${dist.name}/bin/*.pl"/>
         <include name="${dist.name}/bin/*.py"/>
       </tarfileset>
       <tarfileset dir="${dist.name}/.." username="ant" group="ant">
         <include name="${dist.name}/**"/>
         <exclude name="${dist.name}/bin/ant"/>
         <exclude name="${dist.name}/bin/antRun"/>
         <exclude name="${dist.name}/bin/*.pl"/>
         <exclude name="${dist.name}/bin/*.py"/>
       </tarfileset>
     </tar>
     <gzip destfile="${dist.base.binaries}/${dist.name}-bin.tar.gz"
       src="${dist.base.binaries}/${dist.name}-bin.tar"/>
     <bzip2 destfile="${dist.base.binaries}/${dist.name}-bin.tar.bz2"
       src="${dist.base.binaries}/${dist.name}-bin.tar"/>
     <delete file="${dist.base.binaries}/${dist.name}-bin.tar"/>
 
     <copy todir="${java-repository.jars.dir}">
       <fileset dir="${dist.name}/lib">
         <include name="ant*.jar"/>
       </fileset>
       <mapper type="glob" from="*.jar" to="*-${project.version}.jar"/>
     </copy>
     <copy todir="${java-repository.poms.dir}">
       <fileset dir="${dist.name}/lib">
         <include name="*.pom"/>
         <include name="*.sha1"/>
         <include name="*.md5"/>
       </fileset>
       <mapper>
         <mapper type="glob" from="*.pom" to="*.pom"/>
         <mapper type="glob" from="*.pom.sha1" to="*.pom.sha1"/>
         <mapper type="glob" from="*.pom.md5" to="*.pom.md5"/>
       </mapper>
     </copy>
     <checksum fileext=".md5">
       <fileset dir="${java-repository.jars.dir}" includes="*${project.version}.jar"/>
     </checksum>
     <checksum fileext=".sha1" algorithm="SHA">
       <fileset dir="${java-repository.jars.dir}" includes="*${project.version}.jar"/>
     </checksum>
     <delete dir="${dist.name}"/>
     <checksum fileext=".md5">
       <fileset dir="${dist.base.binaries}/">
         <include name="**/*"/>
         <exclude name="**/*.asc"/>
         <exclude name="**/*.md5"/>
       </fileset>
     </checksum>
     <checksum fileext=".sha1" algorithm="SHA">
       <fileset dir="${dist.base.binaries}/">
         <include name="**/*"/>
         <exclude name="**/*.asc"/>
         <exclude name="**/*.md5"/>
       </fileset>
     </checksum>
 
     <antcall inheritAll="false" target="src-dist">
       <param name="src.dist.dir" value="${dist.name}"/>
     </antcall>
     <zip destfile="${dist.base.source}/${dist.name}-src.zip">
       <zipfileset dir="${dist.name}/.." filemode="755">
         <include name="${dist.name}/bootstrap.sh"/>
         <include name="${dist.name}/build.sh"/>
       </zipfileset>
       <fileset dir="${dist.name}/..">
         <include name="${dist.name}/**"/>
         <exclude name="${dist.name}/bootstrap.sh"/>
         <exclude name="${dist.name}/build.sh"/>
       </fileset>
     </zip>
     <tar longfile="gnu"
       destfile="${dist.base.source}/${dist.name}-src.tar">
       <!--
       <zipfileset src="${dist.base.source}/${dist.name}-src.zip"/>
       -->
       <tarfileset dir="${dist.name}/.." mode="755" username="ant" group="ant">
         <include name="${dist.name}/bootstrap.sh"/>
         <include name="${dist.name}/build.sh"/>
       </tarfileset>
       <tarfileset dir="${dist.name}/.." username="ant" group="ant">
         <include name="${dist.name}/**"/>
         <exclude name="${dist.name}/bootstrap.sh"/>
         <exclude name="${dist.name}/build.sh"/>
       </tarfileset>
     </tar>
     <gzip destfile="${dist.base.source}/${dist.name}-src.tar.gz"
       src="${dist.base.source}/${dist.name}-src.tar"/>
     <bzip2 destfile="${dist.base.source}/${dist.name}-src.tar.bz2"
       src="${dist.base.source}/${dist.name}-src.tar"/>
     <delete file="${dist.base.source}/${dist.name}-src.tar"/>
     <delete dir="${dist.name}"/>
     <checksum fileext=".md5">
       <fileset dir="${dist.base.source}/">
         <include name="**/*"/>
         <exclude name="**/*.asc"/>
         <exclude name="**/*.md5"/>
       </fileset>
     </checksum>
     <checksum fileext=".sha1" algorithm="SHA">
       <fileset dir="${dist.base.source}/">
         <include name="**/*"/>
         <exclude name="**/*.asc"/>
         <exclude name="**/*.md5"/>
       </fileset>
     </checksum>
   </target>
 
   <target name="distribution" depends="main_distribution"
     description="--> creates the full Apache Ant distribution">
   </target>
 
   <!--
        ===================================================================
          Upload the distribution to cvs.apache.org for final releases
        ===================================================================
   -->
 
   <target name="init-upload" >
     <fail unless="apache.user" message="set a property apache.user with your apache user"/>
     <fail unless="ssh.passphrase" message="set a property with your ssh passphrase"/>
     <fail unless="ssh.keyfile" message="set a property with your ssh keyfile"/>
     <property name="ssh.knownhosts" location="${user.home}/.ssh/known_hosts" />
     <property name="ssh.host" value="cvs.apache.org"/>
     <property name="ssh.verbose" value="false"/>
     <property name="ssh.base.directory" value="/www/www.apache.org/dist"/>
     <property name="ssh.dist.directory" value="${ssh.base.directory}/ant"/>
     <property name="ssh.java-repository.directory" value="/www/people.apache.org/repo/m1-ibiblio-rsync-repository/ant"/>
     <echo >
       Uploading Ant version ${project.version}
       to host ${ssh.host} as ${apache.user}
       distribution to ${ssh.dist.directory}
       JAR files to ${ssh.java-repository.directory}/jars
       POM files to ${ssh.java-repository.directory}/poms
       Known hosts = ${ssh.knownhosts}
     </echo>
   </target>
 
   <!-- create the directories if absent-->
   <target name="ssh-mkdirs"
     depends="init-upload">
     <sshexec username="${apache.user}" host="${ssh.host}"
       keyfile="${ssh.keyfile}" passphrase="${ssh.passphrase}"
       knownhosts="${ssh.knownhosts}"
       command="mkdir -p ${ssh.dist.directory}" />
     <sshexec username="${apache.user}" host="${ssh.host}"
       keyfile="${ssh.keyfile}" passphrase="${ssh.passphrase}"
       knownhosts="${ssh.knownhosts}"
       command="mkdir -p ${ssh.java-repository.directory}/jars"/>
     <sshexec username="${apache.user}" host="${ssh.host}"
       keyfile="${ssh.keyfile}" passphrase="${ssh.passphrase}"
       knownhosts="${ssh.knownhosts}"
       command="mkdir -p ${ssh.java-repository.directory}/poms"/>
   </target>
 
   <target name="upload" description="--> uploads the distribution"
       depends="init-upload,ssh-mkdirs">
     <scp todir="${apache.user}@${ssh.host}:${ssh.dist.directory}"
       keyfile="${ssh.keyfile}" passphrase="${ssh.passphrase}"
       knownhosts="${ssh.knownhosts}"
       verbose="${ssh.verbose}" >
       <fileset dir="${dist.base}">
         <include name="**/*${project.version}*"/>
       </fileset>
      </scp>
     <scp todir="${apache.user}@${ssh.host}:${ssh.java-repository.directory}"
       keyfile="${ssh.keyfile}" passphrase="${ssh.passphrase}"
       knownhosts="${ssh.knownhosts}"
       verbose="${ssh.verbose}">
       <fileset dir="java-repository/ant">
         <include name="*/*${project.version}*"/>
       </fileset>
      </scp>
   </target>
 
   <!--
        ===================================================================
          Cleans up build and distribution directories
        ===================================================================
   -->
   <target name="clean"
     description="--> cleans up build and dist directories">
     <delete dir="${build.dir}"/>
     <delete dir="${dist.base}"/>
     <delete dir="${dist.dir}"/>
     <delete>
       <fileset dir="." includes="**/*~" defaultexcludes="no"/>
     </delete>
   </target>
 
   <!--
        ===================================================================
          Cleans everything
        ===================================================================
   -->
   <target name="allclean"
     depends="clean"
     description="--> cleans up everything">
     <delete file="${bootstrap.dir}/bin/antRun"/>
     <delete file="${bootstrap.dir}/bin/antRun.bat"/>
     <delete file="${bootstrap.dir}/bin/*.pl"/>
     <delete file="${bootstrap.dir}/bin/*.py"/>
   </target>
 
   <!--
        ===================================================================
          Installs Apache Ant
        ===================================================================
   -->
   <target name="install">
     <fail message="You must set the property ant.install=/where/to/install" unless="ant.install"/>
     <antcall inheritAll="false" target="internal_dist">
       <param name="dist.dir" value="${ant.install}"/>
     </antcall>
   </target>
 
   <target name="install-lite">
     <fail message="You must set the property ant.install=/where/to/install" unless="ant.install"/>
     <antcall inheritAll="false" target="dist-lite">
       <param name="dist.dir" value="${ant.install}"/>
     </antcall>
   </target>
 
   <!--
        ===================================================================
          Creates the API documentation
        ===================================================================
   -->
   <target name="javadoc_check">
     <uptodate property="javadoc.notrequired"
       targetfile="${build.javadocs}/packages.html">
       <srcfiles dir="${java.dir}" includes="**/*.java"/>
     </uptodate>
     <uptodate property="tests.javadoc.notrequired"
       targetfile="${build.tests.javadocs}/packages.html">
       <srcfiles dir="${src.junit}">
         <patternset refid="useful.tests"/>
       </srcfiles>
     </uptodate>
   </target>
 
   <target name="javadocs" depends="prepare, javadoc_check"
     unless="javadoc.notrequired"
     description="--> creates the API documentation">
     <mkdir dir="${build.javadocs}"/>
     <javadoc useexternalfile="yes"
       destdir="${build.javadocs}"
       author="true"
       version="true"
       locale="en"
       windowtitle="${Name} API"
       doctitle="${Name}"
       verbose="${javadoc.verbose}">
 
       <packageset dir="${java.dir}"/>
 
       <!-- hide some meta information for javadoc -->
       <tag name="todo" description="To do:" scope="all"/>
       <tag name="ant.task" enabled="false" description="Task:" scope="types"/>
       <tag name="ant.datatype" enabled="false" description="Data type:" scope="types"/>
       <tag name="ant.attribute" enabled="false" description="Attribute:" scope="types"/>
       <tag name="ant.attribute.group" enabled="false" description="Attribute group:" scope="types"/>
       <tag name="ant.element" enabled="false" description="Nested element:" scope="types"/>
       <group title="Apache Ant Core" packages="org.apache.tools.ant*"/>
       <group title="Core Tasks" packages="org.apache.tools.ant.taskdefs*"/>
       <group title="Core Types" packages="org.apache.tools.ant.types*"/>
       <group title="Optional Tasks" packages="org.apache.tools.ant.taskdefs.optional*"/>
       <group title="Optional Types" packages="org.apache.tools.ant.types.optional*"/>
       <group title="Ant Utilities" packages="org.apache.tools.ant.util*"/>
 
     </javadoc>
   </target>
 
   <target name="test-javadocs" depends="prepare, javadoc_check"
     unless="tests.javadoc.notrequired"
     description="--> creates the API documentation for test utilities">
     <mkdir dir="${build.tests.javadocs}"/>
     <javadoc useexternalfile="yes"
       destdir="${build.tests.javadocs}"
       author="true"
       version="true"
       locale="en"
       windowtitle="${Name} Test Utilities"
       doctitle="${Name}">
 
       <!-- hide some meta information for javadoc -->
       <tag name="pre" description="Precondition:" scope="all"/>
 
       <fileset dir="${src.junit}">
         <patternset refid="useful.tests"/>
       </fileset>
 
     </javadoc>
   </target>
 
   <!--
        ===================================================================
          Compile testcases
        ===================================================================
   -->
   <target name="compile-tests" depends="build" if="junit.present">
     <mkdir dir="${build.tests}"/>
 
     <javac srcdir="${src.junit}"
       destdir="${build.tests}"
       debug="${debug}"
       target="${javac.target}"
       source="${javac.source}"
       deprecation="${deprecation}">
       <classpath refid="tests-classpath"/>
 
       <selector refid="conditional-patterns"/>
     </javac>
 
     <!-- Used by AntlibTest.testAntlibResource: -->
     <jar jarfile="${build.tests}/org/apache/tools/ant/taskdefs/test2-antlib.jar">
       <manifest>
         <attribute name="Extension-name"
           value="org.apache.tools.ant"/>
         <attribute name="Specification-Title"
           value="Apache Ant"/>
         <attribute name="Specification-Version"
           value="${manifest-version}"/>
         <attribute name="Specification-Vendor"
           value="Apache Software Foundation"/>
         <attribute name="Implementation-Title"
           value="org.apache.tools.ant"/>
         <attribute name="Implementation-Version"
           value="${manifest-version}"/>
         <attribute name="Implementation-Vendor"
           value="Apache Software Foundation"/>
       </manifest>
       <zipfileset dir="${tests.etc.dir}" fullpath="taskdefs/test.antlib.xml">
         <include name="taskdefs/test2.antlib.xml"/>
       </zipfileset>
     </jar>
   </target>
 
   <target name="dump-info" depends="dump-sys-properties,run-which"/>
 
   <target name="dump-sys-properties" unless="which.present"
     depends="xml-check">
     <echo message="java.vm.info=${java.vm.info}"/>
     <echo message="java.vm.name=${java.vm.name}"/>
     <echo message="java.vm.vendor=${java.vm.vendor}"/>
     <echo message="java.vm.version=${java.vm.version}"/>
     <echo message="os.arch=${os.arch}"/>
     <echo message="os.name=${os.name}"/>
     <echo message="os.version=${os.version}"/>
     <echo message="file.encoding=${file.encoding}"/>
     <echo message="user.language=${user.language}"/>
   	<echo message="ant.version=${ant.version}"/>
   </target>
 
   <!-- helper class from Xalan2 to check for jar versioning of xml/xsl processors -->
   <target name="xml-check" depends="check_for_optional_packages"
     if="xalan.envcheck" unless="which.present">
     <java classname="org.apache.xalan.xslt.EnvironmentCheck"/>
   </target>
 
   <target name="run-which" depends="check_for_optional_packages"
     if="which.present">
     <java classname="org.apache.env.Which" taskname="which"/>
   </target>
 
   <!-- test to see if we are online or not. can take a while when we are off line, so
     setting the property is a good shortcut-->
   <target name="probe-offline">
     <condition property="offline">
       <or>
         <isset property="offline"/>
         <not>
           <http url="http://www.apache.org/"/>
         </not>
       </or>
     </condition>
     <echo level="verbose"> offline=${offline}</echo>
   </target>
 
   <!--
        ===================================================================
          Run testcase
        ===================================================================
   -->
 
   <target name="check-failed">
     <condition property="tests.failed">
       <or>
         <isset property="junit.failed" />
         <isset property="antunit.failed" />
       </or>
     </condition>
   </target>
 
   <target name="test" description="--> run unit tests and reports"
           depends="dump-info,junit-report,antunit-report,check-failed">
     <fail if="tests.failed" unless="ignore.tests.failed">Unit tests failed;
 see ${build.junit.reports} / ${antunit.reports}
     </fail>
   </target>
 
   <target name="run-tests" depends="dump-info,junit-tests,antunit-tests,check-failed"
           description="--> run unit tests without reports">
     <fail if="tests.failed" message="Unit tests failed" />
   </target>
 
   <target name="test-init" depends="probe-offline,check_for_optional_packages">
     <macrodef name="test-junit">
       <element name="junit-nested" implicit="true" />
       <sequential>
         <!-- Delete 'old' collector classes -->
         <delete failonerror="false">
           <fileset dir="${junit.collector.dir}" includes="${junit.collector.class}*.class"/>
         </delete>
         <!-- compile the FailedTests class if present -->
         <mkdir dir="${junit.collector.dir}"/>
         <!-- FIXME: removed junit collector build code
         <javac srcdir="${junit.collector.dir}" destdir="${junit.collector.dir}">
           <classpath id="failure.cp">
             <pathelement location="${build.classes}"/>
             <pathelement location="${build.tests}"/>
           </classpath>
         </javac>
              -->
         <available file="${junit.collector.dir}/${junit.collector.class}.class"
                    property="hasFailingTests"/>
         <!-- run the tests -->
         <mkdir dir="${build.junit.xml}" />
         <property name="test.junit.vmargs" value=""/>
         <property name="ant.junit.failureCollector"
                   value="${junit.collector.dir}/${junit.collector.class}"/>
         <junit printsummary="${junit.summary}"
                haltonfailure="${test.haltonfailure}"
                fork="${junit.fork}"
                forkmode="${junit.forkmode}"
                failureproperty="junit.failed"
                errorproperty="junit.failed"
                filtertrace="${junit.filtertrace}">
           <sysproperty key="ant.home" value="${ant.home}"/>
           <sysproperty key="build.tests" file="${build.tests}"/>
           <sysproperty key="build.tests.value" value="${build.tests.value}"/>
           <sysproperty key="offline" value="${offline}"/>
           <sysproperty key="tests-classpath.value"
                        value="${tests-classpath.value}"/>
           <sysproperty key="root" file="${basedir}"/>
           <sysproperty key="build.compiler" value="${build.compiler}"/>
           <sysproperty key="tests.and.ant.share.classloader"
                        value="${tests.and.ant.share.classloader}"/>
           <classpath>
             <path refid="tests-classpath"/>
             <pathelement location="${junit.collector.dir}"/>
             <!-- FIXME: remove failure collector build code for the moment
                  <path refid="failure.cp"/> 
                  -->
           </classpath>
           <!-- FIXME: remove failure collector build code for the moment
           <formatter type="failure" usefile="false"/>
                -->
           <formatter type="xml"/>
           <jvmarg line="${test.junit.vmargs}"/>
           <!-- FIXME: remove failure collector build code for the moment
           <test name="${junit.collector.class}" if="hasFailingTests"/>
           -->
           <junit-nested />
         </junit>
       </sequential>
     </macrodef>
 
     <fail>"testcase" cannot be specified with "junit.testcase" or "antunit.testcase".
       <condition>
         <and>
           <isset property="testcase" />
           <or>
             <isset property="antunit.testcase" />
             <isset property="junit.testcase" />
           </or>
         </and>
       </condition>
     </fail>
 
     <condition property="antunit.testcase" value="${testcase}">
       <available file="${src.antunit}/${testcase}" />
     </condition>
 
     <condition property="junit.testcase" value="${testcase}">
       <available classname="${testcase}" classpathref="tests-classpath" />
     </condition>
 
     <fail>Cannot locate test ${testcase}
       <condition>
         <and>
           <isset property="testcase" />
           <not>
             <or>
               <isset property="antunit.testcase" />
               <isset property="junit.testcase" />
             </or>
           </not>
         </and>
       </condition>
     </fail>
 
     <condition property="run.junit">
       <and>
         <not><equals arg1="${testcase}" arg2="${antunit.testcase}" /></not>
         <isset property="junit.present" />
         <available file="${src.junit}" />
       </and>
     </condition>
 
     <condition property="junit.single">
       <and>
         <isset property="junit.testcase" />
         <isset property="run.junit" />
       </and>
     </condition>
 
     <condition property="junit.batch">
       <and>
         <not><isset property="junit.testcase" /></not>
         <isset property="run.junit" />
       </and>
     </condition>
 
     <condition property="run.antunit">
       <and>
         <not><equals arg1="${testcase}" arg2="${junit.testcase}" /></not>
         <isset property="antunit.present" />
         <available file="${src.antunit}" />
       </and>
     </condition>
 
     <condition property="run.antunit.report">
       <and>
         <isset property="run.antunit" />
         <isset property="trax.impl.present" />
       </and>
     </condition>
 
     <condition property="run.junit.report">
       <and>
         <isset property="run.junit" />
         <isset property="trax.impl.present" />
       </and>
     </condition>
   </target>
 
   <target name="junit-report" depends="junit-tests,junit-report-only" />
 
   <target name="junit-report-only" depends="test-init" if="run.junit.report">
     <mkdir dir="${build.junit.reports}" />
     <junitreport todir="${build.junit.reports}">
       <fileset dir="${build.junit.xml}">
         <include name="TEST-*.xml"/>
       </fileset>
       <report format="frames" todir="${build.junit.reports}"/>
     </junitreport>
   </target>
 
   <target name="junit-tests" depends="junit-batch,junit-single-test" />
 
   <target name="junit-batch" depends="compile-tests,test-init"
       if="junit.batch">
 
     <property name="junit.includes" value="**/*Test*" />
     <property name="junit.excludes" value="" />
 
     <test-junit>
       <formatter type="brief" usefile="false"/>
 
       <batchtest todir="${build.junit.xml}" unless="hasFailingTests">
         <fileset dir="${src.junit}"
                  includes="${junit.includes}" excludes="${junit.excludes}">
 
           <!-- abstract classes, not testcases -->
           <exclude name="${taskdefs.package}/TaskdefsTest.java"/>
           <exclude name="${ant.package}/BuildFileTest.java"/>
           <exclude name="${regexp.package}/RegexpMatcherTest.java"/>
           <exclude name="${regexp.package}/RegexpTest.java"/>
           <exclude name="${optional.package}/AbstractXSLTLiaisonTest.java"/>
           <exclude name="${ant.package}/types/AbstractFileSetTest.java"/>
           <exclude name="${ant.package}/types/selectors/BaseSelectorTest.java"/>
 
           <!-- helper classes, not testcases -->
           <exclude name="org/example/**"/>
           <exclude name="${taskdefs.package}/TaskdefTest*Task.java"/>
           <exclude name="${optional.package}/junit/TestFormatter.java"/>
 
           <!-- interactive tests -->
           <exclude name="${taskdefs.package}/TestProcess.java"/>
           <exclude name="${optional.package}/splash/SplashScreenTest.java"/>
 
           <!-- only run these tests if their required libraries are
                installed -->
           <selector refid="conditional-patterns"/>
 
           <!-- tests excluded if the test is run in offline mode -->
           <patternset refid="onlinetests"/>
 
           <!-- failing tests excluded unless run.failing.tests is set -->
           <patternset refid="teststhatfail"/>
 
-          <!-- runtime dependencies that are different from compile
-               time dependencies -->
-          <exclude name="${optional.package}/ReplaceRegExpTest.java"
-            unless="some.regexp.support"/>
-          <exclude name="${ant.package}/types/selectors/ContainsRegexpTest.java"
-            unless="some.regexp.support"/>
-          <exclude name="${ant.package}/types/mappers/RegexpPatternMapperTest.java"
-            unless="some.regexp.support"/>
-
           <!-- needs BSF to work -->
           <exclude name="${optional.package}/Rhino*.java"
             unless="bsf.present"/>
           <exclude name="${optional.package}/Rhino*.java"
             unless="rhino.present"/>
           <exclude name="${optional.package}/script/*.java"
             unless="bsf.present"/>
           <exclude name="${optional.package}/script/*.java"
             unless="rhino.present"/>
           <exclude name="${optional.package}/BeanShellScriptTest.java"
             unless="bsf.present"/>
           <exclude name="${optional.package}/BeanShellScriptTest.java"
             unless="beanshell.present"/>
           <exclude name="${optional.type.package}/Script*.java"
             unless="bsf.present"/>
           <exclude name="${optional.type.package}/Script*.java"
             unless="rhino.present"/>
 
           <!-- fail if testcases can be loaded from the system classloader -->
           <exclude name="${ant.package}/AntClassLoaderDelegationTest.java"
             if="tests.are.on.system.classpath"/>
           <exclude name="${optional.package}/junit/JUnitClassLoaderTest.java"
             if="tests.are.on.system.classpath"/>
 
           <!-- these tests need to be localised before being ran???? -->
           <exclude name="${optional.package}/PvcsTest.java"/>
 
           <!-- These tests need a TraX implementation like xalan2 or saxon -->
           <exclude name="${optional.package}/TraXLiaisonTest.java"
             unless="trax.impl.present"/>
           <exclude name="${optional.package}/XsltTest.java"
             unless="trax.impl.present"/>
           <exclude name="${ant.package}/types/XMLCatalogBuildFileTest.java"
             unless="trax.impl.present"/>
           <exclude name="${optional.package}/junit/JUnitReportTest.java"
             unless="run.junitreport"/>
           <exclude name="${taskdefs.package}/StyleTest.java"
             unless="trax.impl.present"/>
 
           <!-- needs xerces to work -->
           <exclude name="${ant.package}/IncludeTest.java"
             unless="xerces1.present"/>
           <exclude name="${type.package}/selectors/ModifiedSelectorTest.java"
             unless="xerces1.present"/>
 
           <!-- needs resolver.jar to work -->
           <exclude name="${optional.package}/XmlValidateCatalogTest.java"
             unless="apache.resolver.present"/>
 
           <!-- needs jasperc -->
           <exclude name="${optional.package}/JspcTest.java"
             unless="jasper.present"/>
 
           <!--  These tests only passes if testcases and Ant classes have
           been loaded by the same classloader - will throw
           IllegalAccessExceptions otherwise.  -->
           <exclude name="${taskdefs.package}/SQLExecTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${taskdefs.package}/cvslib/ChangeLogWriterTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${taskdefs.package}/cvslib/ChangeLogParserTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${optional.package}/sos/SOSTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${optional.package}/vss/MSVSSTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${optional.package}/TraXLiaisonTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${taskdefs.package}/ProcessDestroyerTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${taskdefs.package}/ProtectedJarMethodsTest.java"
             unless="tests.and.ant.share.classloader"/>
           <exclude name="${ant.package}/launch/LocatorTest.java"
             unless="tests.and.ant.share.classloader"/>
 
           <!-- can only run if cvs is installed on your machine
                enable by setting the property have.cvs
           -->
           <exclude name="${taskdefs.package}/AbstractCvsTaskTest.java"
             unless="have.cvs"/>
 
           <!-- needs a local ftp server and the entry of a user/password combination -->
           <exclude name="${optional.package}/net/FTPTest.java"/>
 
           <!-- test needs special setup -->
           <exclude name="${optional.package}/ssh/ScpTest.java"/>
 
           <!-- test fails if build/classes and ant.jar are using the same
                classloader  -->
           <exclude name="${ant.package}/util/ClasspathUtilsTest.java"
             if="tests.and.ant.share.classloader"/>
         </fileset>
       </batchtest>
     </test-junit>
   </target>
 
   <target name="junit-single-test" depends="compile-tests,junit-single-test-only"
     description="--> runs the single unit test at $${junit.testcase}" />
 
   <target name="junit-single-test-only" depends="test-init" if="junit.single"
     description="--> runs the single unit test at $${junit.testcase} (no compile)">
     <test-junit>
       <formatter type="plain" usefile="false"/>
       <test name="${junit.testcase}" todir="${build.junit.xml}"/>
     </test-junit>
   </target>
 
   <target name="interactive-tests" description="--> runs interactive tests"
     depends="compile-tests"
-    if="jdk1.3+">
+    >
     <java classpathref="tests-classpath"
       classname="org.apache.tools.ant.taskdefs.TestProcess"
       fork="true"/>
   </target>
 
   <target name="antunit-tests" depends="dump-info,build,test-init"
           if="run.antunit" description="--> run the antunit tests">
 
     <condition property="antunit.includes" value="${antunit.testcase}"
                else="**/test.xml,**/*-test.xml">
       <isset property="antunit.testcase" />
     </condition>
 
     <property name="antunit.excludes" value="" />
 
     <mkdir dir="${antunit.xml}" />
     <au:antunit xmlns:au="antlib:org.apache.ant.antunit"
                 failonerror="false" errorproperty="antunit.failed">
       <fileset dir="${src.antunit}" includes="${antunit.includes}"
                excludes="${antunit.excludes}" />
       <au:plainlistener />
       <au:xmllistener todir="${antunit.xml}" />
     </au:antunit>
   </target>
 
   <target name="antunit-report" depends="antunit-tests,antunit-report-only" />
 
   <target name="antunit-report-only" depends="test-init" if="run.antunit.report">
     <length>
       <fileset dir="${antunit.xml}" includes="TEST-*.xml" />
     </length>
     <mkdir dir="${antunit.reports}" />
     <junitreport todir="${antunit.reports}">
       <fileset dir="${antunit.xml}" includes="TEST-*.xml" />
       <report styledir="${src.antunit}" format="frames"
               todir="${antunit.reports}"/>
     </junitreport>
     <length>
       <fileset dir="${antunit.xml}" includes="TEST-*.xml" />
     </length>
   </target>
   
   
   <target name="printFailingTests">
     <property name="failingtests.dir" value="${build.dir}/errors"/>
     <mkdir dir=""/>
     <xslt 
       style="${etc.dir}/printFailingTests.xsl" 
       destdir="${failingtests.dir}" extension=".txt"
       basedir="${build.dir}" includes="testcases/**/TEST-*.xml,antunit/xml/TEST-*.xml"
     />
     <echo>+-------------------------------------------------------------------------------------</echo>
     <echo>| FAILING TESTS:</echo>
     <echo>+-------------------------------------------------------------------------------------</echo>
     <concat>
       <!-- generated message files if they arent empty -->
       <fileset dir="${failingtests.dir}">
         <size value="0" when="more"/>
       </fileset>
       <!-- 'skip' empty lines -->
       <filterchain>
         <linecontains>
           <contains value="|"/>
         </linecontains>
       </filterchain>
     </concat>
     <echo>+-------------------------------------------------------------------------------------</echo>
   </target>
 
   <!--
        ===================================================================
          Main target - runs dist-lite by default
        ===================================================================
   -->
   <target name="main"
     description="--> creates a minimum distribution in ./dist"
     depends="dist-lite"/>
 
 
   <!--
        ===================================================================
          MSI target - creates an MSI installer file with the help of
                       the WiX toolset and the dotnet Antlib.
        ===================================================================
   -->
   <target name="msi"
     description="--> creates an MSI file for Ant, requires WiX and the dotnet Antlib"
     depends="internal_dist"
     xmlns:dn="antlib:org.apache.ant.dotnet">
 
     <property name="msi.dir" value="${build.dir}"/>
     <property name="msi.name" value="${name}-${project.version}.msi"/>
     <property name="msi.file" value="${msi.dir}/${msi.name}"/>
     <property name="wix.home" value="${user.home}/wix"/>
     <property name="wixobj.dir" value="${build.dir}/wix"/>
 
     <property name="dist.dir.resolved" location="${dist.dir}"/>
 
     <mkdir dir="${wixobj.dir}"/>
 
     <dn:wix target="${msi.file}"
       mode="both" wixHome="${wix.home}" wixobjDestDir="${wixobj.dir}">
       <sources dir="${etc.dir}" includes="*.wxs"/>
       <moresources dir="${dist.dir}"/>
 
       <candleParameter name="dist.dir" value="${dist.dir.resolved}"/>
       <candleParameter name="version" value="${manifest-version}"/>
     </dn:wix>
   </target>
 
 </project>
diff --git a/lib/libraries.properties b/lib/libraries.properties
index 6db88b4b0..a00794897 100644
--- a/lib/libraries.properties
+++ b/lib/libraries.properties
@@ -1,67 +1,67 @@
 # Licensed to the Apache Software Foundation (ASF) under one or more
 # contributor license agreements.  See the NOTICE file distributed with
 # this work for additional information regarding copyright ownership.
 # The ASF licenses this file to You under the Apache License, Version 2.0
 # (the "License"); you may not use this file except in compliance with
 # the License.  You may obtain a copy of the License at
 #
 #     http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 
 #this file declares the libraries for use in 
 #a given release of the components
 
 #if you change this, change the checksum to match
 m2.version=2.0.4
 m2.url=http://ibiblio.org/maven2/
 m2.artifact-name=maven-artifact-ant
 m2.jar.name=${m2.artifact-name}-${m2.version}-dep.jar
 #this is the URL of the antlib library, that is pulled down for everything else.
 m2.antlib.url=${m2.url}/org/apache/maven/${m2.artifact-name}/${m2.version}/${m2.jar.name}
 #this is the sha1 checksum of the artifact
 m2.sha1.checksum=4e7ddfdb91600e9b59bb965ff8eef2f06015df50
 
 
 #versions of different libraries. Please keep in alphabetical order, except
 #when a specific dependency forces them to be out-of-order
 antlr.version=2.7.7
 bcel.version=5.1
 bsf.version=2.4.0
 bsh.version=2.0b4
 bsh-core.version=${bsh.version}
 commons-net.version=1.4.1
 commons-logging.version=1.1
 commons-logging-api.version=${commons-logging.version}
 jasper-compiler.version=4.1.36
 jasper-runtime.version=${jasper-compiler.version}
 jdepend.version=2.9.1
 jruby.version=0.9.8
 junit.version=3.8.2
 jsch.version=0.1.29
 jython.version=2.1
 #log4j 1.2.15 requires JMS and a few other Sun jars that are not in the m2 repo
 log4j.version=1.2.14
 #js is the javascript implementation of the rhino project
 #17R1 is compiled with Java5 so we can't use the jar when building with JDK 1.4
 js.version=1.6R7
 oro.version=2.0.8
 regexp.version=1.3
 servlet-api.version=2.3
 which.version=1.0
 xerces.version=2.9.0
 xercesImpl.version=${xerces.version}
 #should be in sync w/ xerces, but not in the maven repository
 #xmlParserAPIs.version=${xerces.version}
 #xmlParserAPIs.version=2.6.1
 xml-apis.version=2.0.2
-xalan.version=2.7.0
+xalan.version=2.7.1
 xml-resolver.version=1.2
 mail.version=1.4
 #paired
 jacl.version=1.2.6
 tcljava.version=${jacl.version}
diff --git a/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java b/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
index fac6608ab..147069b4c 100644
--- a/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
+++ b/src/main/org/apache/tools/ant/taskdefs/XSLTProcess.java
@@ -1,1361 +1,1491 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.Vector;
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.DynamicConfigurator;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.Environment;
 import org.apache.tools.ant.types.Mapper;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.XMLCatalog;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.Resources;
 import org.apache.tools.ant.types.resources.Union;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.ResourceUtils;
 
 /**
  * Processes a set of XML documents via XSLT. This is
  * useful for building views of XML based documentation.
  *
  *
  * @since Ant 1.1
  *
  * @ant.task name="xslt" category="xml"
  */
 
 public class XSLTProcess extends MatchingTask implements XSLTLogger {
     /** destination directory */
     private File destDir = null;
 
     /** where to find the source XML file, default is the project's basedir */
     private File baseDir = null;
 
     /** XSL stylesheet as a filename */
     private String xslFile = null;
 
     /** XSL stylesheet as a {@link org.apache.tools.ant.types.Resource} */
     private Resource xslResource = null;
 
     /** extension of the files produced by XSL processing */
     private String targetExtension = ".html";
 
     /** name for XSL parameter containing the filename */
     private String fileNameParameter = null;
 
     /** name for XSL parameter containing the file directory */
     private String fileDirParameter = null;
 
     /** additional parameters to be passed to the stylesheets */
     private Vector params = new Vector();
 
     /** Input XML document to be used */
     private File inFile = null;
 
     /** Output file */
     private File outFile = null;
 
     /** The name of the XSL processor to use */
     private String processor;
 
     /** Classpath to use when trying to load the XSL processor */
     private Path classpath = null;
 
     /** The Liason implementation to use to communicate with the XSL
      *  processor */
     private XSLTLiaison liaison;
 
     /** Flag which indicates if the stylesheet has been loaded into
      *  the processor */
     private boolean stylesheetLoaded = false;
 
     /** force output of target files even if they already exist */
     private boolean force = false;
 
     /** XSL output properties to be used */
     private Vector outputProperties = new Vector();
 
     /** for resolving entities such as dtds */
     private XMLCatalog xmlCatalog = new XMLCatalog();
 
     /** Name of the TRAX Liaison class */
     private static final String TRAX_LIAISON_CLASS =
                         "org.apache.tools.ant.taskdefs.optional.TraXLiaison";
 
     /** Utilities used for file operations */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * Whether to style all files in the included directories as well.
      *
      * @since Ant 1.5
      */
     private boolean performDirectoryScan = true;
 
     /**
      * factory element for TraX processors only
      * @since Ant 1.6
      */
     private Factory factory = null;
 
     /**
      * whether to reuse Transformer if transforming multiple files.
      * @since 1.5.2
      */
     private boolean reuseLoadedStylesheet = true;
 
     /**
      * AntClassLoader for the nested &lt;classpath&gt; - if set.
      *
      * <p>We keep this here in order to reset the context classloader
      * in execute.  We can't use liaison.getClass().getClassLoader()
      * since the actual liaison class may have been loaded by a loader
      * higher up (system classloader, for example).</p>
      *
      * @since Ant 1.6.2
      */
     private AntClassLoader loader = null;
 
     /**
      * Mapper to use when a set of files gets processed.
      *
      * @since Ant 1.6.2
      */
     private Mapper mapperElement = null;
 
     /**
      * Additional resource collections to process.
      *
      * @since Ant 1.7
      */
     private Union resources = new Union();
 
     /**
      * Whether to use the implicit fileset.
      *
      * @since Ant 1.7
      */
     private boolean useImplicitFileset = true;
 
     /**
      * The default processor is trax
      * @since Ant 1.7
      */
     public static final String PROCESSOR_TRAX = "trax";
 
     /**
      * whether to suppress warnings.
      *
      * @since Ant 1.8.0
      */
     private boolean suppressWarnings = false;
 
     /**
      * whether to fail the build if an error occurs during transformation.
      *
      * @since Ant 1.8.0
      */
     private boolean failOnTransformationError = true;
 
     /**
      * whether to fail the build if an error occurs.
      *
      * @since Ant 1.8.0
      */
     private boolean failOnError = true;
 
     /**
      * Whether the build should fail if the nested resource collection
      * is empty.
      *
      * @since Ant 1.8.0
      */
     private boolean failOnNoResources = true;
 
     /**
      * System properties to set during transformation.
      *
      * @since Ant 1.8.0
      */
     private CommandlineJava.SysProperties sysProperties =
         new CommandlineJava.SysProperties();
 
     /**
+     * Trace configuration for Xalan2.
+     *
+     * @since Ant 1.8.0
+     */
+    private TraceConfiguration traceConfiguration;
+
+    /**
      * Creates a new XSLTProcess Task.
      */
     public XSLTProcess() {
     } //-- XSLTProcess
 
     /**
      * Whether to style all files in the included directories as well;
      * optional, default is true.
      *
      * @param b true if files in included directories are processed.
      * @since Ant 1.5
      */
     public void setScanIncludedDirectories(boolean b) {
         performDirectoryScan = b;
     }
 
     /**
      * Controls whether the stylesheet is reloaded for every transform.
      *
      * <p>Setting this to true may get around a bug in certain
      * Xalan-J versions, default is false.</p>
      * @param b a <code>boolean</code> value
      * @since Ant 1.5.2
      */
     public void setReloadStylesheet(boolean b) {
         reuseLoadedStylesheet = !b;
     }
 
     /**
      * Defines the mapper to map source to destination files.
      * @param mapper the mapper to use
      * @exception BuildException if more than one mapper is defined
      * @since Ant 1.6.2
      */
     public void addMapper(Mapper mapper) {
         if (mapperElement != null) {
             handleError("Cannot define more than one mapper");
         } else {
             mapperElement = mapper;
         }
     }
 
     /**
      * Adds a collection of resources to style in addition to the
      * given file or the implicit fileset.
      *
      * @param rc the collection of resources to style
      * @since Ant 1.7
      */
     public void add(ResourceCollection rc) {
         resources.add(rc);
     }
 
     /**
      * Add a nested &lt;style&gt; element.
      * @param rc the configured Resources object represented as &lt;style&gt;.
      * @since Ant 1.7
      */
     public void addConfiguredStyle(Resources rc) {
         if (rc.size() != 1) {
             handleError("The style element must be specified with exactly one"
                         + " nested resource.");
         } else {
             setXslResource((Resource) rc.iterator().next());
         }
     }
 
     /**
      * API method to set the XSL Resource.
      * @param xslResource Resource to set as the stylesheet.
      * @since Ant 1.7
      */
     public void setXslResource(Resource xslResource) {
         this.xslResource = xslResource;
     }
 
     /**
      * Adds a nested filenamemapper.
      * @param fileNameMapper the mapper to add
      * @exception BuildException if more than one mapper is defined
      * @since Ant 1.7.0
      */
     public void add(FileNameMapper fileNameMapper) throws BuildException {
        Mapper mapper = new Mapper(getProject());
        mapper.add(fileNameMapper);
        addMapper(mapper);
     }
 
     /**
      * Executes the task.
      *
      * @exception BuildException if there is an execution problem.
      * @todo validate that if either in or our is defined, then both are
      */
     public void execute() throws BuildException {
         if ("style".equals(getTaskType())) {
             log("Warning: the task name <style> is deprecated. Use <xslt> instead.",
                     Project.MSG_WARN);
         }
         File savedBaseDir = baseDir;
 
         DirectoryScanner scanner;
         String[]         list;
         String[]         dirs;
 
         String baseMessage =
             "specify the stylesheet either as a filename in style attribute "
             + "or as a nested resource";
 
         if (xslResource == null && xslFile == null) {
             handleError(baseMessage);
             return;
         }
         if (xslResource != null && xslFile != null) {
             handleError(baseMessage + " but not as both");
             return;
         }
         if (inFile != null && !inFile.exists()) {
             handleError("input file " + inFile + " does not exist");
             return;
         }
         try {
             if (sysProperties.size() > 0) {
                 sysProperties.setSystem();
             }
 
             Resource styleResource;
             if (baseDir == null) {
                 baseDir = getProject().getBaseDir();
             }
             liaison = getLiaison();
 
             // check if liaison wants to log errors using us as logger
             if (liaison instanceof XSLTLoggerAware) {
                 ((XSLTLoggerAware) liaison).setLogger(this);
             }
             log("Using " + liaison.getClass().toString(), Project.MSG_VERBOSE);
 
             if (xslFile != null) {
                 // If we enter here, it means that the stylesheet is supplied
                 // via style attribute
                 File stylesheet = getProject().resolveFile(xslFile);
                 if (!stylesheet.exists()) {
                     stylesheet = FILE_UTILS.resolveFile(baseDir, xslFile);
                     /*
                      * shouldn't throw out deprecation warnings before we know,
                      * the wrong version has been used.
                      */
                     if (stylesheet.exists()) {
                         log("DEPRECATED - the 'style' attribute should be "
                             + "relative to the project's");
                         log("             basedir, not the tasks's basedir.");
                     }
                 }
                 FileResource fr = new FileResource();
                 fr.setProject(getProject());
                 fr.setFile(stylesheet);
                 styleResource = fr;
             } else {
                 styleResource = xslResource;
             }
 
             if (!styleResource.isExists()) {
                 handleError("stylesheet " + styleResource + " doesn't exist.");
                 return;
             }
 
             // if we have an in file and out then process them
             if (inFile != null && outFile != null) {
                 process(inFile, outFile, styleResource);
                 return;
             }
             /*
              * if we get here, in and out have not been specified, we are
              * in batch processing mode.
              */
 
             //-- make sure destination directory exists...
             checkDest();
 
             if (useImplicitFileset) {
                 scanner = getDirectoryScanner(baseDir);
                 log("Transforming into " + destDir, Project.MSG_INFO);
 
                 // Process all the files marked for styling
                 list = scanner.getIncludedFiles();
                 for (int i = 0; i < list.length; ++i) {
                     process(baseDir, list[i], destDir, styleResource);
                 }
                 if (performDirectoryScan) {
                     // Process all the directories marked for styling
                     dirs = scanner.getIncludedDirectories();
                     for (int j = 0; j < dirs.length; ++j) {
                         list = new File(baseDir, dirs[j]).list();
                         for (int i = 0; i < list.length; ++i) {
                             process(baseDir, dirs[j] + File.separator + list[i], destDir,
                                     styleResource);
                         }
                     }
                 }
             } else { // only resource collections, there better be some
                 if (resources.size() == 0) {
                     if (failOnNoResources) {
                         handleError("no resources specified");
                     }
                     return;
                 }
             }
             processResources(styleResource);
         } finally {
             if (loader != null) {
                 loader.resetThreadContextLoader();
                 loader.cleanup();
                 loader = null;
             }
             if (sysProperties.size() > 0) {
                 sysProperties.restoreSystem();
             }
             liaison = null;
             stylesheetLoaded = false;
             baseDir = savedBaseDir;
         }
     }
 
     /**
      * Set whether to check dependencies, or always generate;
      * optional, default is false.
      *
      * @param force true if always generate.
      */
     public void setForce(boolean force) {
         this.force = force;
     }
 
     /**
      * Set the base directory;
      * optional, default is the project's basedir.
      *
      * @param dir the base directory
      **/
     public void setBasedir(File dir) {
         baseDir = dir;
     }
 
     /**
      * Set the destination directory into which the XSL result
      * files should be copied to;
      * required, unless <tt>in</tt> and <tt>out</tt> are
      * specified.
      * @param dir the name of the destination directory
      **/
     public void setDestdir(File dir) {
         destDir = dir;
     }
 
     /**
      * Set the desired file extension to be used for the target;
      * optional, default is html.
      * @param name the extension to use
      **/
     public void setExtension(String name) {
         targetExtension = name;
     }
 
     /**
      * Name of the stylesheet to use - given either relative
      * to the project's basedir or as an absolute path; required.
      *
      * @param xslFile the stylesheet to use
      */
     public void setStyle(String xslFile) {
         this.xslFile = xslFile;
     }
 
     /**
      * Set the optional classpath to the XSL processor
      *
      * @param classpath the classpath to use when loading the XSL processor
      */
     public void setClasspath(Path classpath) {
         createClasspath().append(classpath);
     }
 
     /**
      * Set the optional classpath to the XSL processor
      *
      * @return a path instance to be configured by the Ant core.
      */
     public Path createClasspath() {
         if (classpath == null) {
             classpath = new Path(getProject());
         }
         return classpath.createPath();
     }
 
     /**
      * Set the reference to an optional classpath to the XSL processor
      *
      * @param r the id of the Ant path instance to act as the classpath
      *          for loading the XSL processor
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Set the name of the XSL processor to use; optional, default trax.
      * Other values are "xalan" for Xalan1
      *
      * @param processor the name of the XSL processor
      */
     public void setProcessor(String processor) {
         this.processor = processor;
     }
 
     /**
      * Whether to use the implicit fileset.
      *
      * <p>Set this to false if you want explicit control with nested
      * resource collections.</p>
      * @param useimplicitfileset set to true if you want to use implicit fileset
      * @since Ant 1.7
      */
     public void setUseImplicitFileset(boolean useimplicitfileset) {
         useImplicitFileset = useimplicitfileset;
     }
 
     /**
      * Add the catalog to our internal catalog
      *
      * @param xmlCatalog the XMLCatalog instance to use to look up DTDs
      */
     public void addConfiguredXMLCatalog(XMLCatalog xmlCatalog) {
         this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
     }
 
     /**
      * Pass the filename of the current processed file as a xsl parameter
      * to the transformation. This value sets the name of that xsl parameter.
      *
      * @param fileNameParameter name of the xsl parameter retrieving the
      *                          current file name
      */
     public void setFileNameParameter(String fileNameParameter) {
         this.fileNameParameter = fileNameParameter;
     }
 
     /**
      * Pass the directory name of the current processed file as a xsl parameter
      * to the transformation. This value sets the name of that xsl parameter.
      *
      * @param fileDirParameter name of the xsl parameter retrieving the
      *                         current file directory
      */
     public void setFileDirParameter(String fileDirParameter) {
         this.fileDirParameter = fileDirParameter;
     }
 
     /**
      * Whether to suppress warning messages of the processor.
      *
      * @since Ant 1.8.0
      */
     public void setSuppressWarnings(boolean b) {
         suppressWarnings = b;
     }
 
     /**
      * Whether to suppress warning messages of the processor.
      *
      * @since Ant 1.8.0
      */
     public boolean getSuppressWarnings() {
         return suppressWarnings;
     }    
 
     /**
      * Whether transformation errors should make the build fail.
      *
      * @since Ant 1.8.0
      */
     public void setFailOnTransformationError(boolean b) {
         failOnTransformationError = b;
     }
 
     /**
      * Whether any errors should make the build fail.
      *
      * @since Ant 1.8.0
      */
     public void setFailOnError(boolean b) {
         failOnError = b;
     }
 
     /**
      * Whether the build should fail if the nested resource collection is empty.
      *
      * @since Ant 1.8.0
      */
     public void setFailOnNoResources(boolean b) {
         failOnNoResources = b;
     }
 
     /**
      * A system property to set during transformation.
      *
      * @since Ant 1.8.0
      */
     public void addSysproperty(Environment.Variable sysp) {
         sysProperties.addVariable(sysp);
     }
 
     /**
      * A set of system properties to set during transformation.
      *
      * @since Ant 1.8.0
      */
     public void addSyspropertyset(PropertySet sysp) {
         sysProperties.addSyspropertyset(sysp);
     }
 
     /**
+     * Enables Xalan2 traces and uses the given configuration.
+     *
+     * <p>Note that this element doesn't have any effect with a
+     * processor other than trax or if the Transformer is not Xalan2's
+     * transformer implementation.</p>
+     *
+     * @since Ant 1.8.0
+     */
+    public TraceConfiguration createTrace() {
+        if (traceConfiguration != null) {
+            throw new BuildException("can't have more than one trace"
+                                     + " configuration");
+        }
+        traceConfiguration = new TraceConfiguration();
+        return traceConfiguration;
+    }
+
+    /**
+     * Configuration for Xalan2 traces.
+     *
+     * @since Ant 1.8.0
+     */
+    public TraceConfiguration getTraceConfiguration() {
+        return traceConfiguration;
+    }
+
+    /**
      * Load processor here instead of in setProcessor - this will be
      * called from within execute, so we have access to the latest
      * classpath.
      *
      * @param proc the name of the processor to load.
      * @exception Exception if the processor cannot be loaded.
      */
     private void resolveProcessor(String proc) throws Exception {
         String classname;
         if (proc.equals(PROCESSOR_TRAX)) {
             classname = TRAX_LIAISON_CLASS;
         } else {
             //anything else is a classname
             classname = proc;
         }
         Class clazz = loadClass(classname);
         liaison = (XSLTLiaison) clazz.newInstance();
     }
 
     /**
      * Load named class either via the system classloader or a given
      * custom classloader.
      *
      * As a side effect, the loader is set as the thread context classloader
      * @param classname the name of the class to load.
      * @return the requested class.
      * @exception Exception if the class could not be loaded.
      */
     private Class loadClass(String classname) throws Exception {
         if (classpath == null) {
             return Class.forName(classname);
         }
         loader = getProject().createClassLoader(classpath);
         loader.setThreadContextLoader();
         return Class.forName(classname, true, loader);
     }
 
     /**
      * Specifies the output name for the styled result from the
      * <tt>in</tt> attribute; required if <tt>in</tt> is set
      *
      * @param outFile the output File instance.
      */
     public void setOut(File outFile) {
         this.outFile = outFile;
     }
 
     /**
      * specifies a single XML document to be styled. Should be used
      * with the <tt>out</tt> attribute; ; required if <tt>out</tt> is set
      *
      * @param inFile the input file
      */
     public void setIn(File inFile) {
         this.inFile = inFile;
     }
 
     /**
      * Throws a BuildException if the destination directory hasn't
      * been specified.
      * @since Ant 1.7
      */
     private void checkDest() {
         if (destDir == null) {
             handleError("destdir attributes must be set!");
         }
     }
 
     /**
      * Styles all existing resources.
      *
      * @param stylesheet style sheet to use
      * @since Ant 1.7
      */
     private void processResources(Resource stylesheet) {
         Iterator iter = resources.iterator();
         while (iter.hasNext()) {
             Resource r = (Resource) iter.next();
             if (!r.isExists()) {
                 continue;
             }
             File base = baseDir;
             String name = r.getName();
             FileProvider fp = (FileProvider) r.as(FileProvider.class);
             if (fp != null) {
                 FileResource f = ResourceUtils.asFileResource(fp);
                 base = f.getBaseDir();
                 if (base == null) {
                     name = f.getFile().getAbsolutePath();
                 }
             }
             process(base, name, destDir, stylesheet);
         }
     }
 
     /**
      * Processes the given input XML file and stores the result
      * in the given resultFile.
      *
      * @param baseDir the base directory for resolving files.
      * @param xmlFile the input file
      * @param destDir the destination directory
      * @param stylesheet the stylesheet to use.
      * @exception BuildException if the processing fails.
      */
     private void process(File baseDir, String xmlFile, File destDir, Resource stylesheet)
             throws BuildException {
 
         File   outF = null;
         File   inF = null;
 
         try {
             long styleSheetLastModified = stylesheet.getLastModified();
             inF = new File(baseDir, xmlFile);
 
             if (inF.isDirectory()) {
                 log("Skipping " + inF + " it is a directory.", Project.MSG_VERBOSE);
                 return;
             }
             FileNameMapper mapper = null;
             if (mapperElement != null) {
                 mapper = mapperElement.getImplementation();
             } else {
                 mapper = new StyleMapper();
             }
 
             String[] outFileName = mapper.mapFileName(xmlFile);
             if (outFileName == null || outFileName.length == 0) {
                 log("Skipping " + inFile + " it cannot get mapped to output.", Project.MSG_VERBOSE);
                 return;
             } else if (outFileName == null || outFileName.length > 1) {
                 log("Skipping " + inFile + " its mapping is ambiguos.", Project.MSG_VERBOSE);
                 return;
             }
             outF = new File(destDir, outFileName[0]);
 
             if (force || inF.lastModified() > outF.lastModified()
                     || styleSheetLastModified > outF.lastModified()) {
                 ensureDirectoryFor(outF);
                 log("Processing " + inF + " to " + outF);
                 configureLiaison(stylesheet);
                 setLiaisonDynamicFileParameters(liaison, inF);
                 liaison.transform(inF, outF);
             }
         } catch (Exception ex) {
             // If failed to process document, must delete target document,
             // or it will not attempt to process it the second time
             log("Failed to process " + inFile, Project.MSG_INFO);
             if (outF != null) {
                 outF.delete();
             }
             handleTransformationError(ex);
         }
 
     } //-- processXML
 
     /**
      * Process the input file to the output file with the given stylesheet.
      *
      * @param inFile the input file to process.
      * @param outFile the destination file.
      * @param stylesheet the stylesheet to use.
      * @exception BuildException if the processing fails.
      */
     private void process(File inFile, File outFile, Resource stylesheet) throws BuildException {
         try {
             long styleSheetLastModified = stylesheet.getLastModified();
             log("In file " + inFile + " time: " + inFile.lastModified(), Project.MSG_DEBUG);
             log("Out file " + outFile + " time: " + outFile.lastModified(), Project.MSG_DEBUG);
             log("Style file " + xslFile + " time: " + styleSheetLastModified, Project.MSG_DEBUG);
             if (force || inFile.lastModified() >= outFile.lastModified()
                     || styleSheetLastModified >= outFile.lastModified()) {
                 ensureDirectoryFor(outFile);
                 log("Processing " + inFile + " to " + outFile, Project.MSG_INFO);
                 configureLiaison(stylesheet);
                 setLiaisonDynamicFileParameters(liaison, inFile);
                 liaison.transform(inFile, outFile);
             } else {
                 log("Skipping input file " + inFile + " because it is older than output file "
                         + outFile + " and so is the stylesheet " + stylesheet, Project.MSG_DEBUG);
             }
         } catch (Exception ex) {
             log("Failed to process " + inFile, Project.MSG_INFO);
             if (outFile != null) {
                 outFile.delete();
             }
             handleTransformationError(ex);
         }
     }
 
     /**
      * Ensure the directory exists for a given file
      *
      * @param targetFile the file for which the directories are required.
      * @exception BuildException if the directories cannot be created.
      */
     private void ensureDirectoryFor(File targetFile) throws BuildException {
         File directory = targetFile.getParentFile();
         if (!directory.exists()) {
             if (!directory.mkdirs()) {
                 handleError("Unable to create directory: "
                             + directory.getAbsolutePath());
             }
         }
     }
 
     /**
      * Get the factory instance configured for this processor
      *
      * @return the factory instance in use
      */
     public Factory getFactory() {
         return factory;
     }
 
     /**
      * Get the XML catalog containing entity definitions
      *
      * @return the XML catalog for the task.
      */
     public XMLCatalog getXMLCatalog() {
         xmlCatalog.setProject(getProject());
         return xmlCatalog;
     }
 
     /**
      * Get an enumeration on the outputproperties.
      * @return the outputproperties
      */
     public Enumeration getOutputProperties() {
         return outputProperties.elements();
     }
 
     /**
      * Get the Liason implementation to use in processing.
      *
      * @return an instance of the XSLTLiason interface.
      */
     protected XSLTLiaison getLiaison() {
         // if processor wasn't specified, see if TraX is available.  If not,
         // default it to xalan, depending on which is in the classpath
         if (liaison == null) {
             if (processor != null) {
                 try {
                     resolveProcessor(processor);
                 } catch (Exception e) {
                     handleError(e);
                 }
             } else {
                 try {
                     resolveProcessor(PROCESSOR_TRAX);
                 } catch (Throwable e1) {
                     e1.printStackTrace();
                     handleError(e1);
                 }
             }
         }
         return liaison;
     }
 
     /**
      * Create an instance of an XSL parameter for configuration by Ant.
      *
      * @return an instance of the Param class to be configured.
      */
     public Param createParam() {
         Param p = new Param();
         params.addElement(p);
         return p;
     }
 
     /**
      * The Param inner class used to store XSL parameters
      */
     public static class Param {
         /** The parameter name */
         private String name = null;
 
         /** The parameter's value */
         private String expression = null;
 
         private String ifProperty;
         private String unlessProperty;
         private Project project;
 
         /**
          * Set the current project
          *
          * @param project the current project
          */
         public void setProject(Project project) {
             this.project = project;
         }
 
         /**
          * Set the parameter name.
          *
          * @param name the name of the parameter.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * The parameter value
          * NOTE : was intended to be an XSL expression.
          * @param expression the parameter's value.
          */
         public void setExpression(String expression) {
             this.expression = expression;
         }
 
         /**
          * Get the parameter name
          *
          * @return the parameter name
          * @exception BuildException if the name is not set.
          */
         public String getName() throws BuildException {
             if (name == null) {
                 throw new BuildException("Name attribute is missing.");
             }
             return name;
         }
 
         /**
          * Get the parameter's value
          *
          * @return the parameter value
          * @exception BuildException if the value is not set.
          */
         public String getExpression() throws BuildException {
             if (expression == null) {
                 throw new BuildException("Expression attribute is missing.");
             }
             return expression;
         }
 
         /**
          * Set whether this param should be used.  It will be
          * used if the property has been set, otherwise it won't.
          * @param ifProperty name of property
          */
         public void setIf(String ifProperty) {
             this.ifProperty = ifProperty;
         }
 
         /**
          * Set whether this param should NOT be used. It
          * will not be used if the property has been set, otherwise it
          * will be used.
          * @param unlessProperty name of property
          */
         public void setUnless(String unlessProperty) {
             this.unlessProperty = unlessProperty;
         }
 
         /**
          * Ensures that the param passes the conditions placed
          * on it with <code>if</code> and <code>unless</code> properties.
          * @return true if the task passes the "if" and "unless" parameters
          */
         public boolean shouldUse() {
             if (ifProperty != null && project.getProperty(ifProperty) == null) {
                 return false;
             }
             if (unlessProperty != null && project.getProperty(unlessProperty) != null) {
                 return false;
             }
             return true;
         }
     } // Param
 
     /**
      * Create an instance of an output property to be configured.
      * @return the newly created output property.
      * @since Ant 1.5
      */
     public OutputProperty createOutputProperty() {
         OutputProperty p = new OutputProperty();
         outputProperties.addElement(p);
         return p;
     }
 
     /**
      * Specify how the result tree should be output as specified
      * in the <a href="http://www.w3.org/TR/xslt#output">
      * specification</a>.
      * @since Ant 1.5
      */
     public static class OutputProperty {
         /** output property name */
         private String name;
 
         /** output property value */
         private String value;
 
         /**
          * @return the output property name.
          */
         public String getName() {
             return name;
         }
 
         /**
          * set the name for this property
          * @param name A non-null String that specifies an
          * output property name, which may be namespace qualified.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * @return the output property value.
          */
         public String getValue() {
             return value;
         }
 
         /**
          * set the value for this property
          * @param value The non-null string value of the output property.
          */
         public void setValue(String value) {
             this.value = value;
         }
     }
 
     /**
      * Initialize internal instance of XMLCatalog
      * @throws BuildException on error
      */
     public void init() throws BuildException {
         super.init();
         xmlCatalog.setProject(getProject());
     }
 
     /**
      * Loads the stylesheet and set xsl:param parameters.
      *
      * @param stylesheet the file from which to load the stylesheet.
      * @exception BuildException if the stylesheet cannot be loaded.
      * @deprecated since Ant 1.7
      */
     protected void configureLiaison(File stylesheet) throws BuildException {
         FileResource fr = new FileResource();
         fr.setProject(getProject());
         fr.setFile(stylesheet);
         configureLiaison(fr);
     }
 
     /**
      * Loads the stylesheet and set xsl:param parameters.
      *
      * @param stylesheet the resource from which to load the stylesheet.
      * @exception BuildException if the stylesheet cannot be loaded.
      * @since Ant 1.7
      */
     protected void configureLiaison(Resource stylesheet) throws BuildException {
         if (stylesheetLoaded && reuseLoadedStylesheet) {
             return;
         }
         stylesheetLoaded = true;
 
         try {
             log("Loading stylesheet " + stylesheet, Project.MSG_INFO);
             // We call liason.configure() and then liaison.setStylesheet()
             // so that the internal variables of liaison can be set up
             if (liaison instanceof XSLTLiaison2) {
                 ((XSLTLiaison2) liaison).configure(this);
             }
             if (liaison instanceof XSLTLiaison3) {
                 // If we are here we can set the stylesheet as a
                 // resource
                 ((XSLTLiaison3) liaison).setStylesheet(stylesheet);
             } else {
                 // If we are here we cannot set the stylesheet as
                 // a resource, but we can set it as a file. So,
                 // we make an attempt to get it as a file
                 FileProvider fp =
                     (FileProvider) stylesheet.as(FileProvider.class);
                 if (fp != null) {
                     liaison.setStylesheet(fp.getFile());
                 } else {
                     handleError(liaison.getClass().toString()
                                 + " accepts the stylesheet only as a file");
                     return;
                 }
             }
             for (Enumeration e = params.elements(); e.hasMoreElements();) {
                 Param p = (Param) e.nextElement();
                 if (p.shouldUse()) {
                     liaison.addParam(p.getName(), p.getExpression());
                 }
             }
         } catch (Exception ex) {
             log("Failed to transform using stylesheet " + stylesheet, Project.MSG_INFO);
             handleTransformationError(ex);
         }
     }
 
     /**
      * Sets file parameter(s) for directory and filename if the attribute
      * 'filenameparameter' or 'filedirparameter' are set in the task.
      *
      * @param  liaison    to change parameters for
      * @param  inFile     to get the additional file information from
      * @throws Exception  if an exception occurs on filename lookup
      *
      * @since Ant 1.7
      */
     private void setLiaisonDynamicFileParameters(
         XSLTLiaison liaison, File inFile) throws Exception {
         if (fileNameParameter != null) {
             liaison.addParam(fileNameParameter, inFile.getName());
         }
         if (fileDirParameter != null) {
             String fileName = FileUtils.getRelativePath(baseDir, inFile);
             File file = new File(fileName);
             // Give always a slash as file separator, so the stylesheet could be sure about that
             // Use '.' so a dir+"/"+name would not result in an absolute path
             liaison.addParam(fileDirParameter, file.getParent() != null ? file.getParent().replace(
                     '\\', '/') : ".");
         }
     }
 
     /**
      * Create the factory element to configure a trax liaison.
      * @return the newly created factory element.
      * @throws BuildException if the element is created more than one time.
      */
     public Factory createFactory() throws BuildException {
         if (factory != null) {
             handleError("'factory' element must be unique");
         } else {
             factory = new Factory();
         }
         return factory;
     }
 
     /**
      * Throws an exception with the given message if failOnError is
      * true, otherwise logs the message using the WARN level.
      *
      * @since Ant 1.8.0
      */
     protected void handleError(String msg) {
         if (failOnError) {
             throw new BuildException(msg, getLocation());
         }
         log(msg, Project.MSG_WARN);
     }
 
 
     /**
      * Throws an exception with the given nested exception if
      * failOnError is true, otherwise logs the message using the WARN
      * level.
      *
      * @since Ant 1.8.0
      */
     protected void handleError(Throwable ex) {
         if (failOnError) {
             throw new BuildException(ex);
         } else {
             log("Caught an exception: " + ex, Project.MSG_WARN);
         }
     }
 
     /**
      * Throws an exception with the given nested exception if
      * failOnError and failOnTransformationError are true, otherwise
      * logs the message using the WARN level.
      *
      * @since Ant 1.8.0
      */
     protected void handleTransformationError(Exception ex) {
         if (failOnError && failOnTransformationError) {
             throw new BuildException(ex);
         } else {
             log("Caught an error during transformation: " + ex,
                 Project.MSG_WARN);
         }
     }
 
     /**
      * The factory element to configure a transformer factory
      * @since Ant 1.6
      */
     public static class Factory {
 
         /** the factory class name to use for TraXLiaison */
         private String name;
 
         /**
          * the list of factory attributes to use for TraXLiaison
          */
         private Vector attributes = new Vector();
 
         /**
          * @return the name of the factory.
          */
         public String getName() {
             return name;
         }
 
         /**
          * Set the name of the factory
          * @param name the name of the factory.
          */
         public void setName(String name) {
             this.name = name;
         }
 
         /**
          * Create an instance of a factory attribute.
          * @param attr the newly created factory attribute
          */
         public void addAttribute(Attribute attr) {
             attributes.addElement(attr);
         }
 
         /**
          * return the attribute elements.
          * @return the enumeration of attributes
          */
         public Enumeration getAttributes() {
             return attributes.elements();
         }
 
         /**
          * A JAXP factory attribute. This is mostly processor specific, for
          * example for Xalan 2.3+, the following attributes could be set:
          * <ul>
          *  <li>http://xml.apache.org/xalan/features/optimize (true|false) </li>
          *  <li>http://xml.apache.org/xalan/features/incremental (true|false) </li>
          * </ul>
          */
         public static class Attribute implements DynamicConfigurator {
 
             /** attribute name, mostly processor specific */
             private String name;
 
             /** attribute value, often a boolean string */
             private Object value;
 
             /**
              * @return the attribute name.
              */
             public String getName() {
                 return name;
             }
 
             /**
              * @return the output property value.
              */
             public Object getValue() {
                 return value;
             }
 
             /**
              * Not used.
              * @param name not used
              * @return null
              * @throws BuildException never
              */
             public Object createDynamicElement(String name) throws BuildException {
                 return null;
             }
 
             /**
              * Set an attribute.
              * Only "name" and "value" are supported as names.
              * @param name the name of the attribute
              * @param value the value of the attribute
              * @throws BuildException on error
              */
             public void setDynamicAttribute(String name, String value) throws BuildException {
                 // only 'name' and 'value' exist.
                 if ("name".equalsIgnoreCase(name)) {
                     this.name = value;
                 } else if ("value".equalsIgnoreCase(name)) {
                     // a value must be of a given type
                     // say boolean|integer|string that are mostly used.
                     if ("true".equalsIgnoreCase(value)) {
                         this.value = Boolean.TRUE;
                     } else if ("false".equalsIgnoreCase(value)) {
                         this.value = Boolean.FALSE;
                     } else {
                         try {
                             this.value = new Integer(value);
                         } catch (NumberFormatException e) {
                             this.value = value;
                         }
                     }
                 } else {
                     throw new BuildException("Unsupported attribute: " + name);
                 }
             }
         } // -- class Attribute
     } // -- class Factory
 
     /**
      * Mapper implementation of the "traditional" way &lt;xslt&gt;
      * mapped filenames.
      *
      * <p>If the file has an extension, chop it off.  Append whatever
      * the user has specified as extension or ".html".</p>
      *
      * @since Ant 1.6.2
      */
     private class StyleMapper implements FileNameMapper {
         public void setFrom(String from) {
         }
         public void setTo(String to) {
         }
         public String[] mapFileName(String xmlFile) {
             int dotPos = xmlFile.lastIndexOf('.');
             if (dotPos > 0) {
                 xmlFile = xmlFile.substring(0, dotPos);
             }
             return new String[] {xmlFile + targetExtension};
         }
     }
 
+    /**
+     * Configuration for Xalan2 traces.
+     *
+     * @since Ant 1.8.0
+     */
+    public final class TraceConfiguration {
+        private boolean elements, extension, generation, selection, templates;
+
+        /**
+         * Set to true if the listener is to print events that occur
+         * as each node is 'executed' in the stylesheet.
+         */
+        public void setElements(boolean b) {
+            elements = b;
+        }
+
+        /**
+         * True if the listener is to print events that occur as each
+         * node is 'executed' in the stylesheet.
+         */
+        public boolean getElements() {
+            return elements;
+        }
+
+        /**
+         * Set to true if the listener is to print information after
+         * each extension event.
+         */
+        public void setExtension(boolean b) {
+            extension = b;
+        }
+
+        /**
+         * True if the listener is to print information after each
+         * extension event.
+         */
+        public boolean getExtension() {
+            return extension;
+        }
+
+        /**
+         * Set to true if the listener is to print information after
+         * each result-tree generation event.
+         */
+        public void setGeneration(boolean b) {
+            generation = b;
+        }
+
+        /**
+         * True if the listener is to print information after each
+         * result-tree generation event.
+         */
+        public boolean getGeneration() {
+            return generation;
+        }
+
+        /**
+         * Set to true if the listener is to print information after
+         * each selection event.
+         */
+        public void setSelection(boolean b) {
+            selection = b;
+        }
+
+        /**
+         * True if the listener is to print information after each
+         * selection event.
+         */
+        public boolean getSelection() {
+            return selection;
+        }
+
+        /**
+         * Set to true if the listener is to print an event whenever a
+         * template is invoked.
+         */
+        public void setTemplates(boolean b) {
+            templates = b;
+        }
+
+        /**
+         * True if the listener is to print an event whenever a
+         * template is invoked.
+         */
+        public boolean getTemplates() {
+            return templates;
+        }
+
+        /**
+         * The stream to write traces to.
+         */
+        public java.io.OutputStream getOutputStream() {
+            return new LogOutputStream(XSLTProcess.this);
+        }
+    }
+
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
index 5a5e0b356..a22fee083 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/TraXLiaison.java
@@ -1,587 +1,623 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.Hashtable;
 import java.util.Vector;
 import java.util.Enumeration;
 import java.net.URL;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.parsers.SAXParserFactory;
 import javax.xml.transform.ErrorListener;
 import javax.xml.transform.Source;
 import javax.xml.transform.SourceLocator;
 import javax.xml.transform.Templates;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.URIResolver;
 import javax.xml.transform.sax.SAXSource;
 import javax.xml.transform.stream.StreamResult;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.transform.TransformerConfigurationException;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.XSLTLiaison3;
 import org.apache.tools.ant.taskdefs.XSLTLogger;
 import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
 import org.apache.tools.ant.taskdefs.XSLTProcess;
 import org.apache.tools.ant.types.XMLCatalog;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.resources.FileProvider;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.URLResource;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JAXPUtils;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.XMLReader;
 
 /**
  * Concrete liaison for XSLT processor implementing TraX. (ie JAXP 1.1)
  *
  * @since Ant 1.3
  */
 public class TraXLiaison implements XSLTLiaison3, ErrorListener, XSLTLoggerAware {
 
     /**
      * Helper for transforming filenames to URIs.
      *
      * @since Ant 1.7
      */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * The current <code>Project</code>
      */
     private Project project;
 
     /**
      * the name of the factory implementation class to use
      * or null for default JAXP lookup.
      */
     private String factoryName = null;
 
     /** The trax TransformerFactory */
     private TransformerFactory tfactory = null;
 
     /** stylesheet to use for transformation */
     private Resource stylesheet;
 
     private XSLTLogger logger;
 
     /** possible resolver for publicIds */
     private EntityResolver entityResolver;
 
     /** transformer to use for processing files */
     private Transformer transformer;
 
     /** The In memory version of the stylesheet */
     private Templates templates;
 
     /**
      * The modification time of the stylesheet from which the templates
      * are read
      */
     private long templatesModTime;
 
     /** possible resolver for URIs */
     private URIResolver uriResolver;
 
     /** transformer output properties */
     private Vector outputProperties = new Vector();
 
     /** stylesheet parameters */
     private Hashtable params = new Hashtable();
 
     /** factory attributes */
     private Vector attributes = new Vector();
 
     /** whether to suppress warnings */
     private boolean suppressWarnings = false;
 
+    /** optional trace configuration. */
+    private XSLTProcess.TraceConfiguration traceConfiguration = null;
+
     /**
      * Constructor for TraXLiaison.
      * @throws Exception never
      */
     public TraXLiaison() throws Exception {
     }
 
     /**
      * Set the stylesheet file.
      * @param stylesheet a <code>File</code> value
      * @throws Exception on error
      */
     public void setStylesheet(File stylesheet) throws Exception {
         FileResource fr = new FileResource();
         fr.setProject(project);
         fr.setFile(stylesheet);
         setStylesheet(fr);
     }
 
     /**
      * Set the stylesheet file.
      * @param stylesheet a {@link org.apache.tools.ant.types.Resource} value
      * @throws Exception on error
      */
     public void setStylesheet(Resource stylesheet) throws Exception {
         if (this.stylesheet != null) {
             // resetting the stylesheet - reset transformer
             transformer = null;
 
             // do we need to reset templates as well
             if (!this.stylesheet.equals(stylesheet)
                 || (stylesheet.getLastModified() != templatesModTime)) {
                 templates = null;
             }
         }
         this.stylesheet = stylesheet;
     }
 
     /**
      * Transform an input file.
      * @param infile the file to transform
      * @param outfile the result file
      * @throws Exception on error
      */
     public void transform(File infile, File outfile) throws Exception {
         if (transformer == null) {
             createTransformer();
         }
 
         InputStream fis = null;
         OutputStream fos = null;
         try {
             fis = new BufferedInputStream(new FileInputStream(infile));
             fos = new BufferedOutputStream(new FileOutputStream(outfile));
             StreamResult res = new StreamResult(fos);
             // not sure what could be the need of this...
             res.setSystemId(JAXPUtils.getSystemId(outfile));
             Source src = getSource(fis, infile);
 
             // set parameters on each transformation, maybe something has changed
             //(e.g. value of file name parameter)
             setTransformationParameters();
 
             transformer.transform(src, res);
         } finally {
             // make sure to close all handles, otherwise the garbage
             // collector will close them...whenever possible and
             // Windows may complain about not being able to delete files.
             FileUtils.close(fis);
             FileUtils.close(fos);
         }
     }
 
     /**
      * Get the source instance from the stream and id of the file.
      * @param is the stream containing the stylesheet data.
      * @param infile the file that will be used for the systemid.
      * @return the configured source instance matching the stylesheet.
      * @throws ParserConfigurationException if a parser cannot be created which
      * satisfies the requested configuration.
      * @throws SAXException in case of problem detected by the SAX parser.
      */
     private Source getSource(InputStream is, File infile)
         throws ParserConfigurationException, SAXException {
         // todo: is this comment still relevant ??
         // FIXME: need to use a SAXSource as the source for the transform
         // so we can plug in our own entity resolver
         Source src = null;
         if (entityResolver != null) {
             if (getFactory().getFeature(SAXSource.FEATURE)) {
                 SAXParserFactory spFactory = SAXParserFactory.newInstance();
                 spFactory.setNamespaceAware(true);
                 XMLReader reader = spFactory.newSAXParser().getXMLReader();
                 reader.setEntityResolver(entityResolver);
                 src = new SAXSource(reader, new InputSource(is));
             } else {
                 throw new IllegalStateException("xcatalog specified, but "
                     + "parser doesn't support SAX");
             }
         } else {
             // WARN: Don't use the StreamSource(File) ctor. It won't work with
             // xalan prior to 2.2 because of systemid bugs.
             src = new StreamSource(is);
         }
         src.setSystemId(JAXPUtils.getSystemId(infile));
         return src;
     }
 
     private Source getSource(InputStream is, Resource resource)
         throws ParserConfigurationException, SAXException {
         // todo: is this comment still relevant ??
         // FIXME: need to use a SAXSource as the source for the transform
         // so we can plug in our own entity resolver
         Source src = null;
         if (entityResolver != null) {
             if (getFactory().getFeature(SAXSource.FEATURE)) {
                 SAXParserFactory spFactory = SAXParserFactory.newInstance();
                 spFactory.setNamespaceAware(true);
                 XMLReader reader = spFactory.newSAXParser().getXMLReader();
                 reader.setEntityResolver(entityResolver);
                 src = new SAXSource(reader, new InputSource(is));
             } else {
                 throw new IllegalStateException("xcatalog specified, but "
                     + "parser doesn't support SAX");
             }
         } else {
             // WARN: Don't use the StreamSource(File) ctor. It won't work with
             // xalan prior to 2.2 because of systemid bugs.
             src = new StreamSource(is);
         }
         // The line below is a hack: the system id must an URI, but it is not
         // cleat to get the URI of an resource, so just set the name of the
         // resource as a system id
         src.setSystemId(resourceToURI(resource));
         return src;
     }
 
     private String resourceToURI(Resource resource) {
         // TODO turn URLResource into Provider
         FileProvider fp = (FileProvider) resource.as(FileProvider.class);
         if (fp != null) {
             return FILE_UTILS.toURI(fp.getFile().getAbsolutePath());
         }
         if (resource instanceof URLResource) {
             URL u = ((URLResource) resource).getURL();
             return String.valueOf(u);
         } else {
             return resource.getName();
         }
     }
 
     /**
      * Read in templates from the stylesheet
      */
     private void readTemplates()
         throws IOException, TransformerConfigurationException,
                ParserConfigurationException, SAXException {
 
         // Use a stream so that you can close it yourself quickly
         // and avoid keeping the handle until the object is garbaged.
         // (always keep control), otherwise you won't be able to delete
         // the file quickly on windows.
         InputStream xslStream = null;
         try {
             xslStream
                 = new BufferedInputStream(stylesheet.getInputStream());
             templatesModTime = stylesheet.getLastModified();
             Source src = getSource(xslStream, stylesheet);
             templates = getFactory().newTemplates(src);
         } finally {
             if (xslStream != null) {
                 xslStream.close();
             }
         }
     }
 
     /**
      * Create a new transformer based on the liaison settings
      * @throws Exception thrown if there is an error during creation.
      * @see #setStylesheet(java.io.File)
      * @see #addParam(java.lang.String, java.lang.String)
      * @see #setOutputProperty(java.lang.String, java.lang.String)
      */
     private void createTransformer() throws Exception {
         if (templates == null) {
             readTemplates();
         }
 
         transformer = templates.newTransformer();
 
         // configure the transformer...
         transformer.setErrorListener(this);
         if (uriResolver != null) {
             transformer.setURIResolver(uriResolver);
         }
         for (int i = 0; i < outputProperties.size(); i++) {
             final String[] pair = (String[]) outputProperties.elementAt(i);
             transformer.setOutputProperty(pair[0], pair[1]);
         }
+
+        if (traceConfiguration != null) {
+            if ("org.apache.xalan.transformer.TransformerImpl"
+                .equals(transformer.getClass().getName())) {
+                try {
+                    Class traceSupport =
+                        Class.forName("org.apache.tools.ant.taskdefs.optional."
+                                      + "Xalan2TraceSupport", true,
+                                      Thread.currentThread()
+                                      .getContextClassLoader());
+                    XSLTTraceSupport ts =
+                        (XSLTTraceSupport) traceSupport.newInstance();
+                    ts.configureTrace(transformer, traceConfiguration);
+                } catch (Exception e) {
+                    String msg = "Failed to enable tracing because of " + e;
+                    if (project != null) {
+                        project.log(msg, Project.MSG_WARN);
+                    } else {
+                        System.err.println(msg);
+                    }
+                }
+            } else {
+                String msg = "Not enabling trace support for transformer"
+                    + " implementation" + transformer.getClass().getName();
+                if (project != null) {
+                    project.log(msg, Project.MSG_WARN);
+                } else {
+                    System.err.println(msg);
+                }
+            }
+        }
     }
 
     /**
      * Sets the paramters for the transformer.
      */
     private void setTransformationParameters() {
         for (final Enumeration enumeration = params.keys();
              enumeration.hasMoreElements();) {
             final String name = (String) enumeration.nextElement();
             final String value = (String) params.get(name);
             transformer.setParameter(name, value);
         }
     }
 
     /**
      * return the Transformer factory associated to this liaison.
      * @return the Transformer factory associated to this liaison.
      * @throws BuildException thrown if there is a problem creating
      * the factory.
      * @see #setFactory(String)
      * @since Ant 1.5.2
      */
     private TransformerFactory getFactory() throws BuildException {
         if (tfactory != null) {
             return tfactory;
         }
         // not initialized yet, so create the factory
         if (factoryName == null) {
             tfactory = TransformerFactory.newInstance();
         } else {
             try {
                 Class clazz = null;
                 try {
                     clazz =
                         Class.forName(factoryName, true,
                                       Thread.currentThread()
                                       .getContextClassLoader());
                 } catch (ClassNotFoundException cnfe) {
                     String msg = "Failed to load " + factoryName
                         + " via the configured classpath, will try"
                         + " Ant's classpath instead.";
                     if (logger != null) {
                         logger.log(msg);
                     } else if (project != null) {
                         project.log(msg, Project.MSG_WARN);
                     } else {
                         System.err.println(msg);
                     }
                 }
 
                 if (clazz == null) {
                     clazz = Class.forName(factoryName);
                 }
                 tfactory = (TransformerFactory) clazz.newInstance();
             } catch (Exception e) {
                 throw new BuildException(e);
             }
         }
         tfactory.setErrorListener(this);
 
         // specific attributes for the transformer
         for (int i = 0; i < attributes.size(); i++) {
             final Object[] pair = (Object[]) attributes.elementAt(i);
             tfactory.setAttribute((String) pair[0], pair[1]);
         }
 
         if (uriResolver != null) {
             tfactory.setURIResolver(uriResolver);
         }
         return tfactory;
     }
 
 
     /**
      * Set the factory name to use instead of JAXP default lookup.
      * @param name the fully qualified class name of the factory to use
      * or null for the default JAXP look up mechanism.
      * @since Ant 1.6
      */
     public void setFactory(String name) {
         factoryName = name;
     }
 
     /**
      * Set a custom attribute for the JAXP factory implementation.
      * @param name the attribute name.
      * @param value the value of the attribute, usually a boolean
      * string or object.
      * @since Ant 1.6
      */
     public void setAttribute(String name, Object value) {
         final Object[] pair = new Object[]{name, value};
         attributes.addElement(pair);
     }
 
     /**
      * Set the output property for the current transformer.
      * Note that the stylesheet must be set prior to calling
      * this method.
      * @param name the output property name.
      * @param value the output property value.
      * @since Ant 1.5
      * @since Ant 1.5
      */
     public void setOutputProperty(String name, String value) {
         final String[] pair = new String[]{name, value};
         outputProperties.addElement(pair);
     }
 
     /**
      * Set the class to resolve entities during the transformation.
      * @param aResolver the resolver class.
      */
     public void setEntityResolver(EntityResolver aResolver) {
         entityResolver = aResolver;
     }
 
     /**
      * Set the class to resolve URIs during the transformation
      * @param aResolver a <code>EntityResolver</code> value
      */
     public void setURIResolver(URIResolver aResolver) {
         uriResolver = aResolver;
     }
 
     /**
      * Add a parameter.
      * @param name the name of the parameter
      * @param value the value of the parameter
      */
     public void addParam(String name, String value) {
         params.put(name, value);
     }
 
     /**
      * Set a logger.
      * @param l a logger.
      */
     public void setLogger(XSLTLogger l) {
         logger = l;
     }
 
     /**
      * Log an error.
      * @param e the exception to log.
      */
     public void error(TransformerException e) {
         logError(e, "Error");
     }
 
     /**
      * Log a fatal error.
      * @param e the exception to log.
      */
     public void fatalError(TransformerException e) {
         logError(e, "Fatal Error");
         throw new BuildException("Fatal error during transformation", e);
     }
 
     /**
      * Log a warning.
      * @param e the exception to log.
      */
     public void warning(TransformerException e) {
         if (!suppressWarnings) {
             logError(e, "Warning");
         }
     }
 
     private void logError(TransformerException e, String type) {
         if (logger == null) {
             return;
         }
 
         StringBuffer msg = new StringBuffer();
         SourceLocator locator = e.getLocator();
         if (locator != null) {
             String systemid = locator.getSystemId();
             if (systemid != null) {
                 String url = systemid;
                 if (url.startsWith("file:")) {
                     url = FileUtils.getFileUtils().fromURI(url);
                 }
                 msg.append(url);
             } else {
                 msg.append("Unknown file");
             }
             int line = locator.getLineNumber();
             if (line != -1) {
                 msg.append(":");
                 msg.append(line);
                 int column = locator.getColumnNumber();
                 if (column != -1) {
                     msg.append(":");
                     msg.append(column);
                 }
             }
         }
         msg.append(": ");
         msg.append(type);
         msg.append("! ");
         msg.append(e.getMessage());
         if (e.getCause() != null) {
             msg.append(" Cause: ");
             msg.append(e.getCause());
         }
 
         logger.log(msg.toString());
     }
 
     // kept for backwards compatibility
     /**
      * @param file the filename to use for the systemid
      * @return the systemid
      * @deprecated since 1.5.x.
      *             Use org.apache.tools.ant.util.JAXPUtils#getSystemId instead.
      */
     protected String getSystemId(File file) {
         return JAXPUtils.getSystemId(file);
     }
 
 
     /**
      * Specific configuration for the TRaX liaison.
      * @param xsltTask the XSLTProcess task instance from which this liasion
      *        is to be configured.
      */
     public void configure(XSLTProcess xsltTask) {
         project = xsltTask.getProject();
         XSLTProcess.Factory factory = xsltTask.getFactory();
         if (factory != null) {
             setFactory(factory.getName());
 
             // configure factory attributes
             for (Enumeration attrs = factory.getAttributes();
                     attrs.hasMoreElements();) {
                 XSLTProcess.Factory.Attribute attr =
                         (XSLTProcess.Factory.Attribute) attrs.nextElement();
                 setAttribute(attr.getName(), attr.getValue());
             }
         }
 
         XMLCatalog xmlCatalog = xsltTask.getXMLCatalog();
         // use XMLCatalog as the entity resolver and URI resolver
         if (xmlCatalog != null) {
             setEntityResolver(xmlCatalog);
             setURIResolver(xmlCatalog);
         }
 
 
         // configure output properties
         for (Enumeration props = xsltTask.getOutputProperties();
                 props.hasMoreElements();) {
             XSLTProcess.OutputProperty prop
                 = (XSLTProcess.OutputProperty) props.nextElement();
             setOutputProperty(prop.getName(), prop.getValue());
         }
 
         suppressWarnings = xsltTask.getSuppressWarnings();
+
+        traceConfiguration = xsltTask.getTraceConfiguration();
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/XSLTTraceSupport.java b/src/main/org/apache/tools/ant/taskdefs/optional/XSLTTraceSupport.java
new file mode 100644
index 000000000..98ca15669
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/XSLTTraceSupport.java
@@ -0,0 +1,31 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.taskdefs.optional;
+
+import org.apache.tools.ant.taskdefs.XSLTProcess;
+import javax.xml.transform.Transformer;
+
+/**
+ * Sets up trace support for a given transformer.
+ *
+ * @since Ant 1.8.0
+ */
+public interface XSLTTraceSupport {
+    void configureTrace(Transformer t, XSLTProcess.TraceConfiguration conf);
+}
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/Xalan2TraceSupport.java b/src/main/org/apache/tools/ant/taskdefs/optional/Xalan2TraceSupport.java
new file mode 100644
index 000000000..0e66cc450
--- /dev/null
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/Xalan2TraceSupport.java
@@ -0,0 +1,52 @@
+/*
+ *  Licensed to the Apache Software Foundation (ASF) under one or more
+ *  contributor license agreements.  See the NOTICE file distributed with
+ *  this work for additional information regarding copyright ownership.
+ *  The ASF licenses this file to You under the Apache License, Version 2.0
+ *  (the "License"); you may not use this file except in compliance with
+ *  the License.  You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ *  Unless required by applicable law or agreed to in writing, software
+ *  distributed under the License is distributed on an "AS IS" BASIS,
+ *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ *  See the License for the specific language governing permissions and
+ *  limitations under the License.
+ *
+ */
+
+package org.apache.tools.ant.taskdefs.optional;
+
+import java.io.PrintWriter;
+import java.util.TooManyListenersException;
+import javax.xml.transform.Transformer;
+import org.apache.tools.ant.BuildException;
+import org.apache.tools.ant.taskdefs.XSLTProcess;
+import org.apache.xalan.trace.PrintTraceListener;
+import org.apache.xalan.transformer.TransformerImpl;
+
+/**
+ * Sets up trace support for a given transformer.
+ *
+ * @since Ant 1.8.0
+ */
+public class Xalan2TraceSupport implements XSLTTraceSupport {
+    public void configureTrace(Transformer t,
+                               XSLTProcess.TraceConfiguration conf) {
+        if (t instanceof TransformerImpl && conf != null) {
+            PrintWriter w = new PrintWriter(conf.getOutputStream(), false);
+            PrintTraceListener tl = new PrintTraceListener(w);
+            tl.m_traceElements = conf.getElements();
+            tl.m_traceExtension = conf.getExtension();
+            tl.m_traceGeneration = conf.getGeneration();
+            tl.m_traceSelection = conf.getSelection();
+            tl.m_traceTemplates = conf.getTemplates();
+            try {
+                ((TransformerImpl) t).getTraceManager().addTraceListener(tl);
+            } catch (TooManyListenersException tml) {
+                throw new BuildException(tml);
+            }
+        }
+    }
+}
diff --git a/src/tests/antunit/taskdefs/xslt-test.xml b/src/tests/antunit/taskdefs/xslt-test.xml
index 2bb0e9877..17576a4d2 100644
--- a/src/tests/antunit/taskdefs/xslt-test.xml
+++ b/src/tests/antunit/taskdefs/xslt-test.xml
@@ -1,149 +1,173 @@
 <?xml version="1.0"?>
 <!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 -->
 <project default="antunit" xmlns:au="antlib:org.apache.ant.antunit">
   <import file="../antunit-base.xml" />
 
   <target name="setUp">
     <mkdir dir="${output}"/>
     <property name="legacy.dir"
               location="../../../etc/testcases/taskdefs/style/"/>
   </target>
 
   <target name="testParameterPropagation" depends="setUp">
     <xslt in="${legacy.dir}/data.xml"
           out="${output}/out.xml"
           style="${legacy.dir}/printParams.xsl">
       <param name="set" expression="myvalue"/>
     </xslt>
     <au:assertResourceContains
        resource="${output}/out.xml"
        value="set='myvalue'"/>
   </target>
 
   <target name="testInlineStyleSheet" depends="setUp">
     <xslt in="${legacy.dir}/data.xml"
           out="${output}/out.xml">
       <param name="set" expression="somevalue"/>
       <style>
         <string><![CDATA[<xsl:stylesheet
   version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:fo="http://www.w3.org/1999/XSL/Format">
 
 <!-- get the xsl-parameter -->
 <xsl:param name="set">set default value</xsl:param>
 <xsl:param name="empty">empty default value</xsl:param>
 <xsl:param name="undefined">undefined default value</xsl:param>
 
 <!-- use the xsl-parameter -->
 <xsl:template match="/">
 set='<xsl:value-of select="$set"/>'
 empty='<xsl:value-of select="$empty"/>'
 undefined='<xsl:value-of select="$undefined"/>'
 </xsl:template>
 
 </xsl:stylesheet>
 ]]></string>
       </style>
     </xslt>
     <au:assertResourceContains
        resource="${output}/out.xml"
        value="set='somevalue'"/>
   </target>
 
   <target name="testStyleDoesntExist" depends="setUp">
     <au:expectfailure expectedmessage="i-m-not-there.xslt doesn't exist.">
       <xslt in="${legacy.dir}/data.xml"
             out="${output}/out.xml"
             style="i-m-not-there.xslt"/>
     </au:expectfailure>
   </target>
 
   <target name="testStyleDoesntExistNoError" depends="setUp">
     <xslt in="${legacy.dir}/data.xml"
           out="${output}/out.xml"
           style="i-m-not-there.xslt"
           failOnError="false"/>
     <au:assertFileDoesntExist file="${output}/out.xml"/>
   </target>
 
   <target name="testStyleDoesntExistNoTransformationError" depends="setUp">
     <au:expectfailure expectedmessage="i-m-not-there.xslt doesn't exist.">
       <xslt in="${legacy.dir}/data.xml"
             out="${output}/out.xml"
             style="i-m-not-there.xslt"
             failOnTransformationError="false"/>
     </au:expectfailure>
   </target>
 
   <target name="testTransformationError" depends="setUp">
     <au:expectfailure expectedmessage="Fatal error during transformation">
       <xslt in="${legacy.dir}/../input.stdin"
             out="${output}/out.xml"
             style="${legacy.dir}/printParams.xsl"
             />
     </au:expectfailure>
   </target>
 
   <target name="testTransformationErrorNoFail" depends="setUp">
     <xslt in="${legacy.dir}/../input.stdin"
           out="${output}/out.xml"
           style="${legacy.dir}/printParams.xsl"
           failOnError="false"/>
     <au:assertFileDoesntExist file="${output}/out.xml"/>
   </target>
 
   <target name="testTransformationErrorNoFailOnTransformation" depends="setUp">
     <xslt in="${legacy.dir}/../input.stdin"
           out="${output}/out.xml"
           style="${legacy.dir}/printParams.xsl"
           failOnTransformationError="false"/>
     <au:assertFileDoesntExist file="${output}/out.xml"/>
   </target>
 
   <target name="testNoResources" depends="setUp">
     <au:expectfailure expectedmessage="no resources specified">
       <xslt destdir="${output}" style="${legacy.dir}/printParams.xsl"
             useImplicitFileset="false">
         <fileset dir=".">
           <include name="I don't exist"/>
         </fileset>
       </xslt>
     </au:expectfailure>
   </target>
 
   <target name="testNoResourcesNoFail" depends="setUp">
     <xslt destdir="${output}" style="${legacy.dir}/printParams.xsl"
           useImplicitFileset="false"
           failOnNoResources="false">
       <fileset dir=".">
         <include name="I don't exist"/>
       </fileset>
     </xslt>
   </target>
 
   <target name="testNoResourcesNoError" depends="setUp">
     <xslt destdir="${output}" style="${legacy.dir}/printParams.xsl"
           useImplicitFileset="false"
           failOnError="false">
       <fileset dir=".">
         <include name="I don't exist"/>
       </fileset>
     </xslt>
   </target>
+
+  <available property="jdk1.5+" classname="java.net.Proxy"/>
+
+  <target name="testTraceJdk14" unless="jdk1.5+" depends="setUp">
+    <xslt in="${legacy.dir}/data.xml"
+          out="${output}/out.xml"
+          style="${legacy.dir}/printParams.xsl">
+      <param name="set" expression="myvalue"/>
+      <trace templates="true"/>
+    </xslt>
+    <au:assertLogContains text="Failed to enable tracing" level="warning"/>
+  </target>
+
+  <target name="testTraceJdk15+" if="jdk1.5+" depends="setUp">
+    <xslt in="${legacy.dir}/data.xml"
+          out="${output}/out.xml"
+          style="${legacy.dir}/printParams.xsl">
+      <param name="set" expression="myvalue"/>
+      <trace templates="true" elements="true" generation="true"
+             selection="true" extension="true"/>
+    </xslt>
+    <au:assertLogDoesntContain text="Failed to enable tracing"/>
+  </target>
+
 </project>
