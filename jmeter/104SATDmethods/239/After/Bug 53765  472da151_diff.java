diff --git a/build.properties b/build.properties
index e80f94403..f32cc3286 100644
--- a/build.properties
+++ b/build.properties
@@ -1,281 +1,287 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 #  **** External jars (not built as part of JMeter) and needed for build/release ****
 
 # N.B.
 #    When updating this file, please also update the versions in
 #    - res/maven/ApacheJMeter_parent.pom
 #    - eclipse.classpath
 #    - xdocs/changes.xml
 #
 #    Also, please update the lib/ directory ignore list
 #    [Please don't use wild-card versions, because that makes it harder to detect obsolete jars]
 
 # property name conventions:
 #
 # xxx.jar - name of the jar as used in JMeter
 #
 # The following properties are used to download the jars if necessary.
 #
 # xxx.loc - example location where the jar or zip can be found (omit trailing /)
 # xxx.md5 - MD5 hash of the jar (used to check downloads)
 #
 # xxx.zip - name of zip file (if the jar is not available as an independent download)
 # xxx.ent - the jar entry name in Zip file
 
 # Note that all the jars (apart from velocity and the Geronimo API jars)
 # are contained in the JMeter binary release.
 
 maven2.repo                 = http://repo2.maven.org/maven2
 
 apache-bsf.version          = 2.4.0
 apache-bsf.jar              = bsf-${apache-bsf.version}.jar
 apache-bsf.loc              = ${maven2.repo}/bsf/bsf/${apache-bsf.version}
 apache-bsf.md5              = 16e82d858c648962fb5c959f21959039
 
 apache-jsr223-api.version   = 3.1
 apache-jsr223-api.jar       = bsf-api-${apache-jsr223-api.version}.jar
 apache-jsr223-api.loc       = ${maven2.repo}/org/apache/bsf/bsf-api/${apache-jsr223-api.version}
 apache-jsr223-api.md5       = 147c6cb39f889f640036f096f8a4bf59
 
 avalon-framework.version    = 4.1.4
 avalon-framework.jar        = avalon-framework-${avalon-framework.version}.jar
 avalon-framework.loc        = ${maven2.repo}/avalon-framework/avalon-framework/${avalon-framework.version}
 avalon-framework.md5        = 2C5306A09B22BD06A78343C0B55D021F
 
 beanshell.version           = 2.0b5
 beanshell.jar               = bsh-${beanshell.version}.jar
 beanshell.loc               = ${maven2.repo}/org/beanshell/bsh/${beanshell.version}
 beanshell.md5               = 02F72336919D06A8491E82346E10B4D5
 
 # Bouncy Castle jars (compile and test only - not distributed)
 bcmail.version              = 1.45
 bcmail.jar                  = bcmail-jdk15-${bcmail.version}.jar
 bcmail.loc                  = ${maven2.repo}/org/bouncycastle/bcmail-jdk15/${bcmail.version}
 bcmail.md5                  = 13321fc7eff7bcada7b4fedfb592025c
 
 bcprov.version              = 1.45
 bcprov.jar                  = bcprov-jdk15-${bcprov.version}.jar
 bcprov.loc                  = ${maven2.repo}/org/bouncycastle/bcprov-jdk15/${bcprov.version}
 bcprov.md5                  = 2062f8e3d15748443ea60a94b266371c
 
 commons-codec.version       = 1.6
 commons-codec.jar           = commons-codec-${commons-codec.version}.jar
 commons-codec.loc           = ${maven2.repo}/commons-codec/commons-codec/${commons-codec.version}
 commons-codec.md5           = 5970f54883b4831b24b97f1125ba27e6
 
 commons-collections.version = 3.2.1
 commons-collections.jar     = commons-collections-${commons-collections.version}.jar
 commons-collections.loc     = ${maven2.repo}/commons-collections/commons-collections/${commons-collections.version}
 commons-collections.md5     = 13BC641AFD7FD95E09B260F69C1E4C91
 
 commons-httpclient.version  = 3.1
 commons-httpclient.jar      = commons-httpclient-${commons-httpclient.version}.jar
 commons-httpclient.loc      = ${maven2.repo}/commons-httpclient/commons-httpclient/${commons-httpclient.version}
 commons-httpclient.md5      = 8AD8C9229EF2D59AB9F59F7050E846A5
 
 commons-io.version          = 2.2
 commons-io.jar              = commons-io-${commons-io.version}.jar
 commons-io.loc              = ${maven2.repo}/commons-io/commons-io/${commons-io.version}
 commons-io.md5              = 6ad49e3e16c2342e9ee9599ce04775e6
 
 commons-jexl.version        = 1.1
 commons-jexl.jar            = commons-jexl-${commons-jexl.version}.jar
 commons-jexl.loc            = ${maven2.repo}/commons-jexl/commons-jexl/${commons-jexl.version}
 commons-jexl.md5            = 3F7735D20FCE1DBE05F62FF7A7B178DC
 
 commons-jexl2.version       = 2.1.1
 commons-jexl2.jar           = commons-jexl-${commons-jexl2.version}.jar
 commons-jexl2.loc           = ${maven2.repo}/org/apache/commons/commons-jexl/${commons-jexl2.version}
 commons-jexl2.md5           = 4ad8f5c161dd3a50e190334555675db9
 
 commons-lang.version        = 2.6
 commons-lang.jar            = commons-lang-${commons-lang.version}.jar
 commons-lang.loc            = ${maven2.repo}/commons-lang/commons-lang/${commons-lang.version}
 commons-lang.md5            = 4d5c1693079575b362edf41500630bbd
 
+commons-lang3.version        = 3.1
+commons-lang3.jar            = commons-lang3-${commons-lang3.version}.jar
+commons-lang3.loc            = ${maven2.repo}/org/apache/commons/commons-lang3/${commons-lang3.version}
+commons-lang3.md5            = 71b48e6b3e1b1dc73fe705604b9c7584
+
+
 commons-logging.version     = 1.1.1
 commons-logging.jar         = commons-logging-${commons-logging.version}.jar
 commons-logging.loc         = ${maven2.repo}/commons-logging/commons-logging/${commons-logging.version}
 # Checksum from binary release and Maven differ, but contents of jar are identical
 #commons-logging.md5         = E2C390FE739B2550A218262B28F290CE
 commons-logging.md5         = ed448347fc0104034aa14c8189bf37de
 
 commons-net.version         = 3.1
 commons-net.jar             = commons-net-${commons-net.version}.jar
 commons-net.loc             = ${maven2.repo}/commons-net/commons-net/${commons-net.version}
 commons-net.md5             = 23c94d51e72f341fb412d6a015e16313
 
 excalibur-datasource.version = 1.1.1
 excalibur-datasource.jar    = excalibur-datasource-${excalibur-datasource.version}.jar
 excalibur-datasource.loc    = ${maven2.repo}/excalibur-datasource/excalibur-datasource/${excalibur-datasource.version}
 excalibur-datasource.md5    = 59A9EDFF1005D70DFA638CF3A4D3AD6D
  
 excalibur-instrument.version = 1.0
 excalibur-instrument.jar    = excalibur-instrument-${excalibur-instrument.version}.jar
 excalibur-instrument.loc    = ${maven2.repo}/excalibur-instrument/excalibur-instrument/${excalibur-instrument.version}
 excalibur-instrument.md5    = 81BF95737C97A46836EA5F21F7C82719
 
 excalibur-logger.version    = 1.1
 excalibur-logger.jar        = excalibur-logger-${excalibur-logger.version}.jar
 excalibur-logger.loc        = ${maven2.repo}/excalibur-logger/excalibur-logger/${excalibur-logger.version}
 excalibur-logger.md5        = E8246C546B7B0CAFD65947E9B80BB884
 
 excalibur-pool.version      = 1.2
 excalibur-pool.jar          = excalibur-pool-${excalibur-pool.version}.jar
 excalibur-pool.loc          = ${maven2.repo}/excalibur-pool/excalibur-pool/${excalibur-pool.version}
 excalibur-pool.md5          = 0AF05C8811A2912D62D6E189799FD518
 
 # Common file containing both htmlparser and htmllexer jars
 htmlparser.version          = 2.1
 htmllexer.loc               = ${maven2.repo}/org/htmlparser/htmllexer/${htmlparser.version}
 htmllexer.jar               = htmllexer-${htmlparser.version}.jar
 htmllexer.md5               = 1cb7184766a0c52f4d98d671bb08be19
 
 htmlparser.loc              = ${maven2.repo}/org/htmlparser/htmlparser/${htmlparser.version}
 htmlparser.jar              = htmlparser-${htmlparser.version}.jar
 htmlparser.md5              = aa05b921026c228f92ef8b4a13c26f8d
 
 # Apache HttpClient 4.x
 httpclient.version          = 4.2.1
 #
 httpclient.jar              = httpclient-${httpclient.version}.jar
 httpclient.loc              = ${maven2.repo}/org/apache/httpcomponents/httpclient/${httpclient.version}
 httpclient.md5              = 54b09b6e45ff3e2adf0409aa6e652a8d
 
 # Required for HttpClient
 httpmime.jar               = httpmime-${httpclient.version}.jar
 httpmime.loc               = ${maven2.repo}/org/apache/httpcomponents/httpmime/${httpclient.version}
 httpmime.md5               = debbb029073ed28c3a69530cb8e14ef5
 
 # Required for HttpClient
 httpcore.version            = 4.2.1
 httpcore.jar                = httpcore-${httpcore.version}.jar
 httpcore.loc                = ${maven2.repo}/org/apache/httpcomponents/httpcore/${httpcore.version}
 httpcore.md5                = a777e8a2af991b8f1d07b23e77831aaf
 
 jakarta-oro.version         = 2.0.8
 jakarta-oro.jar             = oro-${jakarta-oro.version}.jar
 jakarta-oro.loc             = ${maven2.repo}/oro/oro/${jakarta-oro.version}
 jakarta-oro.md5             = 42E940D5D2D822F4DC04C65053E630AB
 
 jcharts.version             = 0.7.5
 jcharts.jar                 = jcharts-${jcharts.version}.jar
 jcharts.loc                 = ${maven2.repo}/jcharts/jcharts/${jcharts.version}
 jcharts.md5                 = 13927D8077C991E7EBCD8CB284746A7A
 
 jdom.version                = 1.1.2
 jdom.jar                    = jdom-${jdom.version}.jar
 jdom.loc                    = ${maven2.repo}/org/jdom/jdom/${jdom.version}
 jdom.md5                    = 742bb15c2eda90dff56e3d82cf40cd13
 
 js_rhino.version            = 1.7R3
 js_rhino.jar                = rhino-${js_rhino.version}.jar
 js_rhino.loc                = ${maven2.repo}/org/mozilla/rhino/${js_rhino.version}
 js_rhino.md5                = 9dbdb24663f20db43a2c29467c13a204
 
 junit.version               = 4.10
 junit.jar                   = junit-${junit.version}.jar
 junit.loc                   = ${maven2.repo}/junit/junit/${junit.version}
 junit.md5                   = 68380001b88006ebe49be50cef5bb23a
 
 logkit.version              = 2.0
 logkit.jar                  = logkit-${logkit.version}.jar
 logkit.loc                  = ${maven2.repo}/logkit/logkit/${logkit.version}
 logkit.md5                  = 8D82A3E91AAE216D0A2A40B837A232FF
 
 soap.version                = 2.3.1
 soap.jar                    = soap-${soap.version}.jar
 soap.loc                    = ${maven2.repo}/soap/soap/${soap.version}
 soap.md5                    = AA1845E01FEE94FE4A63BBCAA55AD486
 
 tidy.version                = r938
 tidy.jar                    = jtidy-${tidy.version}.jar
 tidy.loc                    = ${maven2.repo}/net/sf/jtidy/jtidy/${tidy.version}
 tidy.md5                    = 6A9121561B8F98C0A8FB9B6E57F50E6B
 
 # XStream can be found at: http://xstream.codehaus.org/
 xstream.version             = 1.4.2
 xstream.jar                 = xstream-${xstream.version}.jar
 xstream.loc                 = ${maven2.repo}/com/thoughtworks/xstream/xstream/${xstream.version}
 xstream.md5                 = 23947B036DD0D9CD23CB2F388C373181
 
 # XMLPull is required by XStream 1.4.x
 xmlpull.version             = 1.1.3.1
 xmlpull.jar                 = xmlpull-${xmlpull.version}.jar
 xmlpull.loc                 = ${maven2.repo}/xmlpull/xmlpull/${xmlpull.version}
 xmlpull.md5                 = cc57dacc720eca721a50e78934b822d2
 
 xpp3.version                = 1.1.4c
 xpp3.jar                    = xpp3_min-${xpp3.version}.jar
 xpp3.loc                    = ${maven2.repo}/xpp3/xpp3_min/${xpp3.version}
 xpp3.md5                    = DCD95BCB84B09897B2B66D4684C040DA
 
 # Xalan can be found at: http://xml.apache.org/xalan-j/
 xalan.version               = 2.7.1
 xalan.jar                   = xalan-${xalan.version}.jar
 xalan.loc                   = ${maven2.repo}/xalan/xalan/${xalan.version}
 xalan.md5                   = D43AAD24F2C143B675292CCFEF487F9C
 
 serializer.version          = 2.7.1
 serializer.jar              = serializer-${serializer.version}.jar
 serializer.loc              = ${maven2.repo}/xalan/serializer/${serializer.version}
 # Checksum from binary release and Maven differ, but contents of jar are identical (apart from non-essential comment)
 #serializer.md5              = F0FA654C1EA1186E9A5BD56E48E0D4A3
 serializer.md5              = a6b64dfe58229bdd810263fa0cc54cff
 
 # Xerces can be found at: http://xerces.apache.org/xerces2-j/
 xerces.version              = 2.9.1
 xerces.jar                  = xercesImpl-${xerces.version}.jar
 xerces.loc                  = ${maven2.repo}/xerces/xercesImpl/${xerces.version}
 # Checksum from binary release and Maven differ, but contents of jar are identical (apart from EOLs in text files)
 #xerces.md5                  = DA09B75B562CA9A8E9A535D2148BE8E4
 xerces.md5                  = f807f86d7d9db25edbfc782aca7ca2a9
 
 xml-apis.version            = 1.3.04
 xml-apis.jar                = xml-apis-${xml-apis.version}.jar
 xml-apis.loc                = ${maven2.repo}/xml-apis/xml-apis/${xml-apis.version}
 xml-apis.md5                = 9AE9C29E4497FC35A3EADE1E6DD0BBEB
 
 # Codecs were previously provided by Batik
 xmlgraphics-commons.version = 1.3.1
 xmlgraphics-commons.jar     = xmlgraphics-commons-${xmlgraphics-commons.version}.jar
 xmlgraphics-commons.loc     = ${maven2.repo}/org/apache/xmlgraphics/xmlgraphics-commons/${xmlgraphics-commons.version}
 xmlgraphics-commons.md5     = E63589601D939739349A50A029DAB120
 
 # JavaMail jars (N.B. these are available under CDDL)
 activation.version          = 1.1.1
 activation.jar              = activation-${activation.version}.jar
 activation.loc              = ${maven2.repo}/javax/activation/activation/${activation.version}
 activation.md5              = 46a37512971d8eca81c3fcf245bf07d2
 
 javamail.version            = 1.4.4
 javamail.jar                = mail-${javamail.version}.jar
 javamail.loc                = ${maven2.repo}/javax/mail/mail/${javamail.version}
 javamail.md5                = f30453ae9ee252c802d349009742065f
 
 # Geronimo JMS jar
 jms.version                 = 1.1.1
 jms.jar                     = geronimo-jms_1.1_spec-${jms.version}.jar
 jms.loc                     = ${maven2.repo}/org/apache/geronimo/specs/geronimo-jms_1.1_spec/${jms.version}
 jms.md5                     = d80ce71285696d36c1add1989b94f084
 
 # The following jars are only needed for source distributions
 # They are used for building the documentation
 velocity.version            = 1.7
 velocity.jar                = velocity-${velocity.version}.jar
 velocity.loc                = ${maven2.repo}/org/apache/velocity/velocity/${velocity.version}
 velocity.md5                = 3692dd72f8367cb35fb6280dc2916725
\ No newline at end of file
diff --git a/build.xml b/build.xml
index 25b87d66c..9c2753d03 100644
--- a/build.xml
+++ b/build.xml
@@ -1,1424 +1,1425 @@
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
 <project name="JMeter" default="install" basedir=".">
   <description>
 
   N.B. To build JMeter from a release you need both the binary and source archives,
   and these must be unpacked into the same directory structure.
 
   To download additional jars needed for building the code and documentation:
 
       ant download_jars
 
 
     To build JMeter from source:
       ant [install]
 
     To rebuild:
       ant clean install
 
     To update documentation
       ant docs-site
       ant docs-printable
     To build API documentation (Javadoc)
       ant docs-api
     To build all the docs
       ant docs-all
 
     To build all and package up the files for distribution
       ant distribution -Djmeter.version=vvvv [-Dsvn.revision=nnnnn]
   	
     Add -Ddisable-svnCheck=true to disable svn check, if you build from src archive or offline
   	Add -Ddisable-check-versions=true to disable matching current svn revision and JMeterVersion.java,
   	  if you want build your own custom JMeter package.
 
     To create a nightly build (separate bin/src/lib jars):
       ant nightly [-Dsvn.revision=nnnnn]
 
     To create tar and tgz of the web-site documentation (docs and api)
       ant site [ -Djmeter.version=vvvv ]
 
 
     For more info:
       ant -projecthelp
 
     To diagnose usage of deprecated APIs:
       ant -Ddeprecation=on clean compile
   </description>
 
   <!--
 
   Note
   ====
   As with most other Apache projects, Gump (http://gump.apache.org/) is used to
   perform automated builds and tests on JMeter.
 
   Gump uses its project/jmeter.xml file to determine which target to use.
   The current setting is: <ant target="dist">
 
   Any changes to the dependency list for dist may affect Gump.
 
   Now the dist target depends on "assume-libs-present", so if additional libraries are added to that,
   the Gump project file for JMeter must also be updated.
 
   Jars that are not required by the dist target do not need to be added to the Gump project file.
 
   -->
 
   <!-- Are we running under Gump? -->
   <property name="gump.run" value="false"/>
 
   <!-- The version of this file -->
   <property name="version.build" value="$Revision$"/>
 
   <property file="build-local.properties"/> <!-- allow local overrides -->
 
    <!-- Findbugs task and target -->
    <!--
    Findbugs is licensed under the Lesser GNU Public License
    HomePage: http://www.cs.umd.edu/~pugh/java/bugs/
 
    To use the findbugs target, download and install the findbugs binary distribution
    Set the value of findbugs.homedir according to where you installed findbugs, for example:
    ant findbugs -Dfindbugs.homedir=/etc/findbugs -Dfindbugs.level=medium
   [The defaults are /findbugs and medium]
    -->
   <property name="findbugs.homedir" value="/findbugs" />
   <property name="findbugs.level" value="medium" />
   <property name="findbugs.xsldir" value="${findbugs.homedir}/src/xsl" />
   <property name="findbugs.xslname" value="fancy" />
   <property name="findbugs.outName" value="reports/jmeter-fb" />
 
   <target name="findbugs" description="Run the stand-alone Findbugs detector">
     <echoproperties prefix="findbugs"/>
     <mkdir dir="reports"/>
     <taskdef name="findbugs"
         classpath="${findbugs.homedir}/lib/findbugs-ant.jar"
         classname="edu.umd.cs.findbugs.anttask.FindBugsTask"/>
     <findbugs home="${findbugs.homedir}"
               output="xml:withMessages"
               reportlevel="${findbugs.level}"
               excludeFilter="fb-excludes.xml"
               jvmargs="-Xms512m -Xmx512m"
               outputFile="${findbugs.outName}.xml" >
       <sourcePath path="${src.core}" />
       <sourcePath path="${src.http}" />
       <sourcePath path="${src.ftp}" />
       <sourcePath path="${src.java}" />
       <sourcePath path="${src.junit}" />
       <sourcePath path="${src.jdbc}" />
       <sourcePath path="${src.ldap}" />
       <sourcePath path="${src.mail}" />
       <sourcePath path="${src.components}" />
       <sourcePath path="${src.functions}" />
       <class location="${lib.dir}/jorphan.jar" />
       <class location="${dest.jar}/ApacheJMeter_components.jar"/>
       <class location="${dest.jar}/ApacheJMeter_components.jar"/>
       <class location="${dest.jar}/ApacheJMeter_core.jar"/>
       <class location="${dest.jar}/ApacheJMeter_ftp.jar"/>
       <class location="${dest.jar}/ApacheJMeter_functions.jar"/>
       <class location="${dest.jar}/ApacheJMeter_http.jar"/>
       <class location="${dest.jar}/ApacheJMeter_java.jar"/>
       <class location="${dest.jar}/ApacheJMeter_jdbc.jar"/>
       <class location="${dest.jar}/ApacheJMeter_jms.jar"/>
       <class location="${dest.jar}/ApacheJMeter_junit.jar"/>
       <class location="${dest.jar}/ApacheJMeter_ldap.jar"/>
       <class location="${dest.jar}/ApacheJMeter_mail.jar"/>
       <class location="${dest.jar}/ApacheJMeter_monitors.jar"/>
       <class location="${dest.jar}/ApacheJMeter_native.jar"/>
       <class location="${dest.jar}/ApacheJMeter_report.jar"/>
       <class location="${dest.jar}/ApacheJMeter_tcp.jar"/>
       <class location="${dest.jar.jmeter}/ApacheJMeter.jar" />
       <sourcePath path="${src.jorphan}" />
       <sourcePath path="${src.tcp}" />
       <sourcePath path="${src.jms}" />
       <sourcePath path="${src.native}" />
       <sourcePath path="${src.report}" />
       		
       <auxClasspath>
           <fileset dir="${lib.dir}">
               <include name="*.jar"/>
           </fileset>
       </auxClasspath>
 
       <auxClasspath>
           <fileset dir="${lib.opt}">
               <include name="*.jar"/>
           </fileset>
       </auxClasspath>
 
       <auxClasspath>
           <fileset dir="${lib.api}">
               <include name="*.jar"/>
           </fileset>
       </auxClasspath>
 
     </findbugs>
     <antcall target="findbugs-style"/>
     <antcall target="findbugs-xsl"/>
   </target>
 
   <!-- Convert findbugs XML output to CSV -->
    <target name="findbugs-style">
      <xslt style="fb-csv.xsl"
        force="true"
        in="${findbugs.outName}.xml"
        out="${findbugs.outName}.csv">
      </xslt>
    </target>
 
   <!-- Convert findbugs XML output to HTML -->
    <target name="findbugs-xsl">
      <xslt style="${findbugs.xsldir}/${findbugs.xslname}.xsl"
        force="true"
        in="${findbugs.outName}.xml"
        out="${findbugs.outName}_${findbugs.xslname}.html">
      </xslt>
    </target>
 
   <!-- Where the Sources live -->
   <property name="src.dir" value="src"/>
   <property name="src.core" value="src/core"/>
   <property name="src.http" value="src/protocol/http"/>
   <property name="src.ftp" value="src/protocol/ftp"/>
   <property name="src.test" value="test/src"/>
   <property name="src.jdbc" value="src/protocol/jdbc"/>
   <property name="src.java" value="src/protocol/java"/>
   <property name="src.junit" value="src/junit"/>
   <property name="src.components" value="src/components"/>
   <property name="src.functions" value="src/functions"/>
   <property name="src.jorphan" value="src/jorphan"/>
   <property name="src.ldap" value="src/protocol/ldap"/>
   <property name="src.tcp" value="src/protocol/tcp"/>
   <property name="src.examples" value="src/examples"/>
   <property name="src.mail" value="src/protocol/mail"/>
   <property name="src.monitor.components" value="src/monitor/components"/>
   <property name="src.monitor.model" value="src/monitor/model"/>
   <property name="src.jms" value="src/protocol/jms"/>
   <property name="src.native" value="src/protocol/native"/>
   <property name="src.report" value="src/reports"/>
 
   <!-- Where the documentation sources live -->
   <property name="src.docs" value="xdocs"/>
   <property name="src.css" value="xdocs/css"/>
   <property name="src.images" value="xdocs/images"/>
   <property name="src.demos" value="xdocs/demos"/>
 
   <!-- Javadoc sources -->
   <path id="srcpaths">
     <pathelement location="${src.core}"/>
     <pathelement location="${src.components}"/>
     <pathelement location="${src.functions}"/>
     <pathelement location="${src.http}"/>
     <pathelement location="${src.ftp}"/>
     <pathelement location="${src.jdbc}"/>
     <pathelement location="${src.java}"/>
     <pathelement location="${src.junit}"/>
     <pathelement location="${src.jorphan}"/>
     <pathelement location="${src.ldap}"/>
     <pathelement location="${src.tcp}"/>
     <pathelement location="${src.examples}"/>
     <pathelement location="${src.mail}"/>
     <pathelement location="${src.monitor.components}"/>
     <pathelement location="${src.monitor.model}"/>
     <pathelement location="${src.jms}"/>
     <pathelement location="${src.native}"/>
     <pathelement location="${src.report}"/>
   </path>
 
   <!-- Temporary build directories: where the .class live -->
   <property name="build.dir" value="build"/>
   <property name="build.core" value="build/core"/>
   <property name="build.http" value="build/protocol/http"/>
   <property name="build.ftp" value="build/protocol/ftp"/>
   <property name="build.jdbc" value="build/protocol/jdbc"/>
   <property name="build.java" value="build/protocol/java"/>
   <property name="build.junit" value="build/junit"/>
   <property name="build.components" value="build/components"/>
   <property name="build.functions" value="build/functions"/>
   <property name="build.jorphan" value="build/jorphan"/>
   <property name="build.ldap" value="build/protocol/ldap"/>
   <property name="build.mail" value="build/protocol/mail"/>
   <property name="build.tcp" value="build/protocol/tcp"/>
   <property name="build.examples" value="build/examples"/>
   <property name="build.monitor.components" value="build/monitor/components"/>
   <property name="build.monitor.model" value="build/monitor/model"/>
   <property name="build.jms" value="build/protocol/jms"/>
   <property name="build.native" value="build/protocol/native"/>
   <property name="build.report" value="build/reports"/>
   <property name="build.test" value="build/test"/>
   <property name="build.res" value="build/res"/>
 
   <!-- Path prefix to allow Anakia to find stylesheets if running under Eclipse -->
   <!--
   Anakia looks for stylesheets relative to the java launch directory.
   Use the External Tools properties page to define the variable
   as the relative path to the directory where this build file is found.
   For example:
                eclipse.anakia=workspace/jmeter
 
   An alternative is to define it as a command-line argument on the Main page;
   this allows one to use a macro name, so is more portable.
   For example:
                -Declipse.anakia=workspace/${project_name}
   WARNING: you must ensure that you have selected a file or directory in
   the Navigator pane before attempting to run the build, or Eclipse will
   complain that it cannot resolve the variable name, and it can mark the
   launch configuration as having failed.
   -->
   <property name="eclipse.anakia" value="."/>
 
   <!-- Where the build result .jars will be placed -->
   <property name="dest.jar" value="lib/ext"/>
   <property name="dest.jar.jmeter" value="bin"/>
 
   <!-- Where the API documentation lives -->
   <property name="dest.docs.api" value="docs/api"/>
 
   <!-- Where the doc results live -->
   <property name="dest.docs" value="docs"/>
   <property name="dest.printable_docs" value="printable_docs"/>
 
   <!-- Default is for Anakia to only rebuild if the target file is older than the source -->
   <property name="anakia.lastModifiedCheck" value="true"/>
 
   <!-- Directory where jars needed for creating documentation live -->
   <property name="lib.doc" value="lib/doc"/>
 
   <!-- Directory where these 3rd party libraries live -->
   <property name="lib.dir" value="lib"/>
 
   <!-- Directory where API spec libraries live -->
   <property name="lib.api" value="lib/api"/>
 
   <!-- Directory where Optional 3rd party libraries live -->
   <property name="lib.opt" value="lib/opt"/>
 
   <!-- Other stuff -->
   <property name="extras.dir" value="extras"/>
 
   <!-- Some resources to add to jars -->
   <property name="res.dir" value="res"/>
 
   <!-- Where the distribution packages will be created -->
   <property name="dist.dir" value="dist"/>
 
   <!-- Where the Maven artifacts will be created -->
   <property name="maven.dir" value="${dist.dir}/maven"/>
 
   <!-- Where the Maven template poms are located -->
   <property name="maven.poms" value="res/maven"/>
 
   <!-- Compilation parameters -->
   <property name="optimize" value="on"/>
   <property name="deprecation" value="off"/>
   <property name="target.java.version" value="1.5"/>
   <property name="src.java.version" value="1.5"/>
   <property name="encoding" value="UTF-8"/>
   <!-- Set test encoding to the same default, but allow override -->
   <property name="test.encoding" value="${encoding}"/>
   <property name="includeAntRuntime" value="false"/>
 
   <!-- 3rd party libraries to be included in the binary distribution -->
   <property file="build.properties"/> <!-- defines the library version numbers -->
 
   <property name="resources.meta-inf"      value="${build.res}/META-INF"/>
 	
   <patternset id="external.jars.notices">
         <include name="LICENSE"/>
         <include name="NOTICE"/>
         <include name="README"/>
   </patternset>
 
   <!-- Jars for binary release -->
   <patternset id="external.jars">
     <include name="${lib.dir}/${activation.jar}"/>
     <include name="${lib.dir}/${apache-bsf.jar}"/>
     <include name="${lib.dir}/${apache-jsr223-api.jar}"/>
     <include name="${lib.dir}/${beanshell.jar}"/>
     <include name="${lib.dir}/${avalon-framework.jar}"/>
     <include name="${lib.dir}/${xmlgraphics-commons.jar}"/>
     <include name="${lib.dir}/${commons-codec.jar}"/>
     <include name="${lib.dir}/${commons-collections.jar}"/>
     <include name="${lib.dir}/${commons-httpclient.jar}"/>
     <include name="${lib.dir}/${commons-io.jar}"/>
     <include name="${lib.dir}/${commons-jexl.jar}"/>
     <include name="${lib.dir}/${commons-jexl2.jar}"/>
     <include name="${lib.dir}/${commons-lang.jar}"/>
+    <include name="${lib.dir}/${commons-lang3.jar}"/>
     <include name="${lib.dir}/${commons-logging.jar}"/>
     <include name="${lib.dir}/${commons-net.jar}"/>
     <include name="${lib.dir}/${excalibur-datasource.jar}"/>
     <include name="${lib.dir}/${excalibur-instrument.jar}"/>
     <include name="${lib.dir}/${excalibur-logger.jar}"/>
     <include name="${lib.dir}/${excalibur-pool.jar}"/>
     <include name="${lib.dir}/${htmllexer.jar}"/>
     <include name="${lib.dir}/${htmlparser.jar}"/>
     <include name="${lib.dir}/${httpclient.jar}"/>
     <include name="${lib.dir}/${httpmime.jar}"/>
     <include name="${lib.dir}/${httpcore.jar}"/>
     <include name="${lib.dir}/${jakarta-oro.jar}"/>
     <include name="${lib.dir}/${javamail.jar}"/>
     <include name="${lib.dir}/${jcharts.jar}"/>
     <include name="${lib.dir}/${jdom.jar}"/>
     <include name="${lib.dir}/${jms.jar}"/>
     <include name="${lib.dir}/${js_rhino.jar}"/>
     <include name="${lib.dir}/${junit.jar}"/>
     <include name="${lib.dir}/${logkit.jar}"/>
     <include name="${lib.dir}/${serializer.jar}"/>
     <include name="${lib.dir}/${soap.jar}"/>
     <include name="${lib.dir}/${tidy.jar}"/>
     <include name="${lib.dir}/${xalan.jar}"/>
     <include name="${lib.dir}/${xerces.jar}"/>
     <include name="${lib.dir}/${xml-apis.jar}"/>
     <include name="${lib.dir}/${xpp3.jar}"/>
     <include name="${lib.dir}/${xstream.jar}"/>
   	<include name="${lib.dir}/${xmlpull.jar}"/>
   </patternset>
 
   <!--
   Optional jars, not included in distribution.
   Any such jars need to be downloaded separately.
   These can be put into ${lib.dir} or ${lib.opt}
   - both of these are included in the build classpath
 
   Any jars put into ${lib.dir} will be included in
   the classpath used by JMeter to load classes.
   Jars in ${lib.opt} are NOT normally included by JMeter.
 
   Placing an optional jar in ${lib.opt} means that it
   will be included in the build classpath, but it will
   not be included in the runtime classpath. This is intended
   for testing JMeter without the optional Jar file(s).
   -->
 
   <!-- Build classpath (includes the optional jar directory) -->
   <path id="classpath">
     <!-- Externally produced jars -->
     <pathelement location="${lib.dir}/${activation.jar}"/>
     <pathelement location="${lib.dir}/${apache-bsf.jar}"/>
     <pathelement location="${lib.dir}/${apache-jsr223-api.jar}"/>
     <pathelement location="${lib.dir}/${beanshell.jar}"/>
     <pathelement location="${lib.dir}/${avalon-framework.jar}"/>
     <pathelement location="${lib.dir}/${xmlgraphics-commons.jar}"/>
     <pathelement location="${lib.dir}/${commons-codec.jar}"/>
     <pathelement location="${lib.dir}/${commons-collections.jar}"/>
     <pathelement location="${lib.dir}/${commons-httpclient.jar}"/>
     <pathelement location="${lib.dir}/${commons-io.jar}"/>
     <pathelement location="${lib.dir}/${commons-jexl.jar}"/>
     <pathelement location="${lib.dir}/${commons-jexl2.jar}"/>
-    <pathelement location="${lib.dir}/${commons-lang.jar}"/>
+    <pathelement location="${lib.dir}/${commons-lang3.jar}"/>
     <pathelement location="${lib.dir}/${commons-logging.jar}"/>
     <pathelement location="${lib.dir}/${commons-net.jar}"/>
     <pathelement location="${lib.dir}/${excalibur-datasource.jar}"/>
     <pathelement location="${lib.dir}/${excalibur-instrument.jar}"/>
     <pathelement location="${lib.dir}/${excalibur-logger.jar}"/>
     <pathelement location="${lib.dir}/${excalibur-pool.jar}"/>
     <pathelement location="${lib.dir}/${htmllexer.jar}"/>
     <pathelement location="${lib.dir}/${htmlparser.jar}"/>
     <pathelement location="${lib.dir}/${httpclient.jar}"/>
     <pathelement location="${lib.dir}/${httpmime.jar}"/>
     <pathelement location="${lib.dir}/${httpcore.jar}"/>
     <pathelement location="${lib.dir}/${jakarta-oro.jar}"/>
     <pathelement location="${lib.dir}/${javamail.jar}"/>
     <pathelement location="${lib.dir}/${jcharts.jar}"/>
     <pathelement location="${lib.dir}/${jdom.jar}"/>
     <pathelement location="${lib.dir}/${jms.jar}"/>
     <pathelement location="${lib.dir}/${js_rhino.jar}"/>
     <pathelement location="${lib.dir}/${junit.jar}"/>
     <pathelement location="${lib.dir}/${logkit.jar}"/>
     <pathelement location="${lib.dir}/${serializer.jar}"/>
     <pathelement location="${lib.dir}/${soap.jar}"/>
     <pathelement location="${lib.dir}/${tidy.jar}"/>
     <pathelement location="${lib.dir}/${xalan.jar}"/>
     <pathelement location="${lib.dir}/${xerces.jar}"/>
     <pathelement location="${lib.dir}/${xml-apis.jar}"/>
     <pathelement location="${lib.dir}/${xpp3.jar}"/>
     <pathelement location="${lib.dir}/${xstream.jar}"/>
   	<pathelement location="${lib.dir}/${xmlpull.jar}"/>
     <!-- Generated jars -->
     <fileset dir="${lib.dir}" includes="jorphan.jar"/>
     <!-- API-only jars-->
     <fileset dir="${lib.api}" includes="*.jar"/>
     <!-- Optional jars -->
     <fileset dir="${lib.opt}" includes="*.jar"/>
   </path>
 
   <!-- Anakia classpath -->
   <path id="anakia.classpath">
     <pathelement location="${lib.doc}/${velocity.jar}"/>
     <pathelement location="${lib.dir}/${jdom.jar}"/>
     <pathelement location="${lib.dir}/${commons-collections.jar}"/>
     <pathelement location="${lib.dir}/${commons-lang.jar}"/>
     <pathelement location="${lib.dir}/${logkit.jar}"/>
   </path>
 
   <!-- Version info filter set -->
   <tstamp>
     <format property="year" pattern="yyyy" locale="en"/>
   </tstamp>
 
   <filterset id="version.filters">
     <filter token="YEAR" value="${year}"/>
   </filterset>
 	
   <target name="init-version">
     <tstamp/>
     <!--
         JMeter version
         This is overridden for formal releases.
     -->
     <property name="jmeter.version" value="2.8-SNAPSHOT"/>
     <!-- Remember to change "docversion" below if necessary -->
     <condition property="implementation.version"
           value="${jmeter.version} r${svn.revision}" else="${jmeter.version}.${DSTAMP}">
         <isset property="svn.revision"/>
     </condition>
     <property name="display.version" value="${implementation.version}"/>
     <echo level="info">jmeter.version = ${jmeter.version}</echo>
     <echo level="info">display.version = ${display.version}</echo>
     <echo level="info">implementation.version = ${implementation.version}</echo>
   </target>
 
   <!-- JMeter Javadoc version (own variable is used so can be overriden independently) -->
   <property name="docversion" value="${jmeter.version}"/>
 
     <!-- Get version from SVN status -->
   <target name="init-svnVersion" depends="svnCheck">
     <fail message="Could not get SVN revision" unless="svn.revision"/>
     <property name="jmeter.version" value="r${svn.revision}"/>
     <!-- Copy the value to avoid duplication of revision in Manifests -->
     <property name="implementation.version" value="${jmeter.version}"/>
     <property name="display.version" value="${jmeter.version}"/>
     <echo level="info">svn.revision = ${svn.revision}</echo>
     <echo level="info">jmeter.version = ${jmeter.version}</echo>
     <echo level="info">display.version = ${display.version}</echo>
     <echo level="info">implementation.version = ${implementation.version}</echo>
   </target>
 
 
   <target name="init-docs" depends="report-anakia-missing">
     <echo level="info">eclipse.anakia = ${eclipse.anakia}</echo>
     </target>
 
   <!--
     - Check for anakia task
     -->
   <target name="check-anakia">
     <available classpathref="anakia.classpath" classname="org.apache.velocity.anakia.AnakiaTask" property="AnakiaTask.present"/>
     <!-- Check for Velocity version 1.5 -->
     <available classpathref="anakia.classpath" classname="org.apache.velocity.io.UnicodeInputStream" property="velocity.version.15"/>
     <antcall target="report-old-velocity"></antcall>
   </target>
 
   <target name="report-anakia-missing" depends="check-anakia" unless="AnakiaTask.present">
     <echo>
       AnakiaTask is not present, documentation will not be generated.
     </echo>
   </target>
 
   <target name="report-old-velocity" unless="velocity.version.15" if="AnakiaTask.present">
     <echo>
       Velocity version appears to be older than 1.5: the documentation may be generated with incorrect line endings.
     </echo>
   </target>
 
   <target name="compile-core" depends="compile-jorphan" description="Compile JMeter core classes.">
     <mkdir dir="${build.core}"/>
     <javac srcdir="${src.core}" destdir="${build.core}" optimize="${optimize}" source="${src.java.version}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-components" depends="compile-jorphan,compile-core" description="Compile generic (protocol-independent) components.">
     <mkdir dir="${build.components}"/>
     <javac srcdir="${src.components}" destdir="${build.components}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-functions" depends="compile-jorphan,compile-core" description="Compile functions.">
     <mkdir dir="${build.functions}"/>
     <javac srcdir="${src.functions}" destdir="${build.functions}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-http" depends="compile-jorphan,compile-core,compile-components" description="Compile components specific to HTTP sampling.">
     <mkdir dir="${build.http}"/>
     <!-- Directory needs to exist, or jar will fail -->
     <javac srcdir="${src.http}" destdir="${build.http}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <pathelement location="${build.components}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-tests" description="Compile test components only">
     <mkdir dir="${build.test}"/>
     <javac srcdir="${src.test}" destdir="${build.test}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <pathelement location="${build.components}"/>
         <pathelement location="${build.http}"/>
         <pathelement location="${build.ftp}"/>
         <pathelement location="${build.functions}"/>
         <pathelement location="${build.java}"/>
         <pathelement location="${build.jdbc}"/>
         <pathelement location="${build.ldap}"/>
         <pathelement location="${build.mail}"/>
         <pathelement location="${build.monitor.components}"/>
         <pathelement location="${build.monitor.model}"/>
         <pathelement location="${build.report}"/>
         <pathelement location="${build.tcp}"/>
         <!-- Also include compiled jars to allow running tests without rebuilding source -->
         <fileset dir="${dest.jar}" includes="*.jar"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-ftp" depends="compile-jorphan,compile-core" description="Compile components specific to FTP sampling.">
     <mkdir dir="${build.ftp}"/>
     <javac srcdir="${src.ftp}" destdir="${build.ftp}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-jdbc" depends="compile-jorphan,compile-core" description="Compile components specific to JDBC sampling.">
     <mkdir dir="${build.jdbc}"/>
     <javac srcdir="${src.jdbc}" destdir="${build.jdbc}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-ldap" depends="compile-jorphan,compile-core"
     description="Compile components specific to LDAP sampling.">
     <mkdir dir="${build.ldap}"/>
     <javac srcdir="${src.ldap}" destdir="${build.ldap}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="create-mail-dir">
     <mkdir dir="${build.mail}"/>
   </target>
 
   <target name="compile-mail" depends="compile-jorphan,compile-core,create-mail-dir"
     description="Compile components specific to IMAP and POP3 sampling.">
     <javac srcdir="${src.mail}" destdir="${build.mail}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-java" depends="compile-jorphan,compile-core" description="Compile components specific to Java sampling.">
     <mkdir dir="${build.java}"/>
     <javac srcdir="${src.java}" destdir="${build.java}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-junit" depends="compile-jorphan,compile-core" description="Compile components specific to JUnit sampling.">
     <mkdir dir="${build.junit}"/>
     <javac srcdir="${src.junit}" destdir="${build.junit}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-report" depends="compile-jorphan,compile-core,compile-components"
     description="Compile report components.">
     <mkdir dir="${build.report}"/>
     <javac srcdir="${src.report}" destdir="${build.report}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <pathelement location="${build.components}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-tcp" depends="compile-jorphan,compile-core" description="Compile components specific to TCP sampling.">
     <mkdir dir="${build.tcp}"/>
     <javac srcdir="${src.tcp}" destdir="${build.tcp}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-protocols" depends="compile-http,compile-ftp,compile-jdbc,compile-java,compile-ldap,compile-mail,compile-tcp" description="Compile all protocol-specific components."/>
 
   <target name="compile-examples" depends="compile-jorphan,compile-core" description="Compile example components.">
     <mkdir dir="${build.examples}"/>
     <javac srcdir="${src.examples}" destdir="${build.examples}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-monitor" depends="compile-monitor-model,compile-monitor-components"/>
 
   <target name="compile-monitor-components"
     depends="compile-jorphan,compile-core,compile-components,compile-monitor-model">
     <mkdir dir="${build.monitor.components}"/>
     <javac srcdir="${src.monitor.components}" source="${src.java.version}" destdir="${build.monitor.components}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.monitor.model}"/>
         <pathelement location="${build.http}"/>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <pathelement location="${build.components}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-monitor-model" depends="compile-jorphan,compile-core">
     <mkdir dir="${build.monitor.model}"/>
     <javac srcdir="${src.monitor.model}" destdir="${build.monitor.model}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-jorphan" depends="init-version" description="Compile JOrphan utility classes.">
     <mkdir dir="${build.jorphan}"/>
     <javac srcdir="${src.jorphan}" destdir="${build.jorphan}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile-rmi" depends="compile-jorphan,compile-core" description="Compile RMI stubs and skeletons.">
     <rmic base="${build.core}" classname="org.apache.jmeter.engine.RemoteJMeterEngineImpl">
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </rmic>
     <rmic base="${build.core}" classname="org.apache.jmeter.samplers.RemoteSampleListenerImpl">
       <classpath>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </rmic>
   </target>
 
   <target name="compile-jms" depends="compile-jorphan,compile-core,compile-components"
         description="Compile components specific to JMS sampling.">
     <mkdir dir="${build.jms}"/>
     <javac srcdir="${src.jms}" destdir="${build.jms}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 	
   <target name="compile-native" depends="compile-jorphan,compile-core,compile-components"
         description="Compile components specific to Native sampling.">
     <mkdir dir="${build.native}"/>
     <javac srcdir="${src.native}" destdir="${build.native}" source="${src.java.version}" optimize="${optimize}" debug="on" target="${target.java.version}"
            includeAntRuntime="${includeAntRuntime}" deprecation="${deprecation}" encoding="${encoding}">
       <include name="**/*.java"/>
       <classpath>
         <pathelement location="${build.jorphan}"/>
         <pathelement location="${build.core}"/>
         <path refid="classpath"/>
       </classpath>
     </javac>
   </target>
 
   <target name="compile"
   depends="_message_3rdParty,compile-core,compile-components,compile-functions,compile-protocols,compile-rmi,compile-monitor,compile-junit,compile-jms,compile-native, compile-report"
   description="Compile everything."/>
 
   <target name="run_gui" depends="package" description="Run the JMeter GUI off the jar files">
     <java classname="org.apache.jmeter.NewDriver" fork="true">
       <classpath>
         <pathelement location="${dest.jar.jmeter}/ApacheJMeter.jar"/>
         <path refid="classpath"/>
       </classpath>
       <sysproperty key="jmeter.home" value="${basedir}"/>
     </java>
   </target>
 
   <target name="package" depends="compile, prepare-resources, package-only"
      description="Compile everything and create the jars"/>
 
   <target name="prepare-resources"
      description="Prepare some resources files, update date">
     <mkdir dir="${build.res}" />
   	<mkdir dir="${resources.meta-inf}" />
     <copy todir="${resources.meta-inf}" overwrite="yes" filtering="yes"
         encoding="${encoding}">
       <filterset refid="version.filters"/>
       <fileset dir="${res.dir}/META-INF" >
         <include name="*.license" />
         <include name="*.notice" />
       </fileset>
     </copy>
     <fixcrlf srcdir="${resources.meta-inf}" eol="crlf" includes="*.license *.notice"/>
   </target>
 
 <!--
 N.B. Eclipse (and perhaps other Java IDEs) copies all files to the build directory, unless
 told otherwise. This means that there might be copies of property and image files in the
 build directory. To avoid including the files twice in the jar file, we include only .class
 files in the list of files to be processed from the build tree.
 
 Eclipse has been fixed so that it no longer shows files in the build tree in the Open Resource dialogue,
 so having duplicates in the build tree no longer causes confusion.
 
 Note: if built from Eclipse, the build directory will include resources and images,
 and Eclipse will thus be able to run JMeter from the default path.
 
 If built using Ant, the build tree will not contain any resources, and thus Eclipse will not be able to
 run JMeter unless all the JMeter jars are added.
 
 -->
   <target name="package-only" description="Package already-compiled classes (shortcut for IDE users)">
     <manifest file="${build.dir}/MANIFEST_BIN.MF">
         <attribute name="Built-By" value="${user.name}"/>
         <attribute name="Extension-Name" value=" JMeter"/>
         <attribute name="Specification-Title" value=" Apache JMeter"/>
         <attribute name="Specification-Vendor" value=" Apache Software Foundation"/>
         <attribute name="Implementation-Vendor" value=" Apache Software Foundation"/>
         <attribute name="Implementation-Vendor-Id" value=" org.apache"/>
         <attribute name="Implementation-Version" value="${implementation.version}"/>
         <attribute name="X-Compile-Source-JDK" value="${src.java.version}"/>
         <attribute name="X-Compile-Target-JDK" value="${target.java.version}"/>
     </manifest>
 
     <manifest file="${build.dir}/MANIFEST_SRC.MF">
        <attribute name="Built-By" value="${user.name}"/>
        <attribute name="Extension-Name" value=" JMeter"/>
        <attribute name="Specification-Title" value=" Apache JMeter"/>
        <attribute name="Specification-Vendor" value=" Apache Software Foundation"/>
        <attribute name="Implementation-Vendor" value=" Apache Software Foundation"/>
        <attribute name="Implementation-Vendor-Id" value=" org.apache"/>
        <attribute name="Implementation-Version" value="${implementation.version}"/>
    </manifest>
 
     <mkdir dir="${dest.jar}"/>
 
     <!-- perhaps ought to include a basic jmeter.properties file in one of the jars,
     given that JMeterUtils looks for it if it cannot find the external one
     - otherwise, change utils to ignore it -->
 
     <!-- JMeter launch jar -->
     <jar jarfile="${dest.jar.jmeter}/ApacheJMeter.jar"
         includes="**/NewDriver*,**/DynamicClassLoader*,**/ShutdownClient.class"
         basedir="${build.core}"
         manifest="${build.dir}/MANIFEST_BIN.MF">
         <manifest>
            <attribute name="Main-Class" value="org.apache.jmeter.NewDriver"/>
         </manifest>
         <zipfileset file="${resources.meta-inf}/default.notice"
           fullpath="META-INF/NOTICE" />
         <zipfileset file="${resources.meta-inf}/default.license"
           fullpath="META-INF/LICENSE" />
     </jar>
 
     <!-- core -->
     <jar jarfile="${dest.jar}/ApacheJMeter_core.jar"
         manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <!-- Only include class files from build tree - see above -->
       <fileset dir="${build.core}" includes="**/*.class"
         excludes="**/BeanShellClient*.class,**/NewDriver*,**/DynamicClassLoader*"/>
       <fileset dir="${src.core}" includes="org/apache/jmeter/images/**"
         excludes="**/*.properties"/>
       <fileset dir="${src.core}" includes="**/*.properties">
         <exclude name="*eucJP*"/>
       </fileset>
       <!-- This file is used by the jmeter -h option -->
       <fileset dir="${src.core}" includes="org/apache/jmeter/help.txt"/>
     </jar>
 
     <!-- components -->
     <jar jarfile="${dest.jar}/ApacheJMeter_components.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.components}" includes="**/*.class" />
       <fileset dir="${src.components}" includes="**/*.properties" />
     </jar>
 
     <!-- functions -->
     <jar jarfile="${dest.jar}/ApacheJMeter_functions.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.functions}" includes="**/*.class" />
       <fileset dir="${src.functions}" includes="**/*.properties" />
     </jar>
 
     <!-- http -->
     <jar jarfile="${dest.jar}/ApacheJMeter_http.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.http}" includes="**/*.class"/>
       <fileset dir="${src.http}" includes="**/*.properties" />
     </jar>
 
     <!-- ftp -->
     <jar jarfile="${dest.jar}/ApacheJMeter_ftp.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.ftp}" includes="**/*.class" />
       <fileset dir="${src.ftp}" includes="**/*.properties" />
     </jar>
 
     <!-- jdbc -->
     <jar jarfile="${dest.jar}/ApacheJMeter_jdbc.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.jdbc}" includes="**/*.class" />
       <fileset dir="${src.jdbc}" includes="**/*.properties" />
     </jar>
 
     <!-- java -->
     <jar jarfile="${dest.jar}/ApacheJMeter_java.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.java}" includes="**/*.class" />
       <fileset dir="${src.java}" includes="**/*.properties" />
     </jar>
 
     <!-- BeanShell Client -->
     <jar jarfile="${lib.dir}/bshclient.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <manifest>
          <attribute name="Main-Class" value="org.apache.jmeter.util.BeanShellClient"/>
       </manifest>
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.core}" includes="**/BeanShellClient*.class" />
     </jar>
 
     <!-- junit -->
     <jar jarfile="${dest.jar}/ApacheJMeter_junit.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.junit}" includes="org/**/*.class" />
       <fileset dir="${src.junit}" includes="**/*.properties" />
     </jar>
 
     <!-- Build junit/test.jar sample -->
     <jar jarfile="${lib.dir}/junit/test.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.junit}" excludes="org/**/*" />
       <fileset dir="${src.junit}"   excludes="org/**/*" />
     </jar>
 
     <!-- report -->
     <jar jarfile="${dest.jar}/ApacheJMeter_report.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.report}" includes="**/*.class" />
       <fileset dir="${src.report}" includes="**/*.properties" />
     </jar>
 
     <!-- ldap -->
     <jar jarfile="${dest.jar}/ApacheJMeter_ldap.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.ldap}" includes="**/*.class" />
       <fileset dir="${src.ldap}" includes="**/*.properties" />
     </jar>
 
     <!-- mail -->
     <jar jarfile="${dest.jar}/ApacheJMeter_mail.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.mail}" includes="**/*.class"  />
       <fileset dir="${src.mail}" includes="**/*.properties" />
       <fileset dir="${src.mail}" includes="**/*.providers" />
     </jar>
 
     <!-- tcp -->
     <jar jarfile="${dest.jar}/ApacheJMeter_tcp.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.tcp}" includes="**/*.class" />
       <fileset dir="${src.tcp}" includes="**/*.properties" />
     </jar>
 
     <!-- monitor -->
     <jar jarfile="${dest.jar}/ApacheJMeter_monitors.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.monitor.model}" includes="**/*.class" />
       <fileset dir="${build.monitor.components}" includes="**/*.class" />
     </jar>
 
     <!-- jms -->
     <!-- Ensure that build dir exists, even if JMS has not been built -->
     <mkdir dir="${build.jms}"/>
     <jar jarfile="${dest.jar}/ApacheJMeter_jms.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.jms}" includes="**/*.class" />
       <fileset dir="${src.jms}" includes="**/*.properties" />
     </jar>
 
     <!-- native -->
     <!-- Ensure that build dir exists, even if Native has not been built -->
     <mkdir dir="${build.native}"/>
     <jar jarfile="${dest.jar}/ApacheJMeter_native.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.native}" includes="**/*.class" />
       <fileset dir="${src.native}" includes="**/*.properties" />
     </jar>
 
     <jar jarfile="${lib.dir}/jorphan.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
         <zipfileset file="${resources.meta-inf}/default.notice"
           fullpath="META-INF/NOTICE" />
         <zipfileset file="${resources.meta-inf}/default.license"
           fullpath="META-INF/LICENSE" />
       <fileset dir="${build.jorphan}" includes="**/*.class"/>
       <fileset dir="${src.jorphan}" includes="**/*.properties"/>
     </jar>
   </target>
 
   <!-- Check the Ant version -->
   <available property="Ant-1.8.0-or-later" classname="org.apache.tools.ant.taskdefs.Local"/>
   <fail message="This build requires Ant 1.8.0 or later" unless="Ant-1.8.0-or-later"/>
 
   <!-- Check that the 3rd party libraries are present -->
   <target name="_check_3rdparty">
     <condition property="3rdparty.present">
       <and>
           <!-- No need to check all jars; just check a few -->
           <available classpathref="classpath" classname="org.apache.bsf.BSFEngine"/>
           <available classpathref="classpath" classname="org.htmlparser.Parser"/>
           <available classpathref="classpath" classname="com.thoughtworks.xstream.XStream"/>
       </and>
     </condition>
   </target>
 
   <target name="_message_3rdParty" depends="_check_3rdparty" unless="3rdparty.present">
     <echo>Cannot find all the required 3rd party libraries.</echo>
     <echo>If building from a release, you can get most of them from the binary archive.</echo>
     <echo>Use "ant download_jars" to download any missing jars.</echo>
     <fail message="Cannot find required classes"/>
   </target>
 
   <target name="install" depends="package" description="Install JMeter. (Compiles code and creates jars)">
     <fixcrlf srcdir="." eol="lf" includes="bin/*.sh bin/jmeter bin/jmeter-server bin/jmeter-report"/>
   </target>
 
   <target name="install-examples" depends="compile-examples" description="Build and installs the example components.">
     <jar jarfile="${dest.jar}/ApacheJMeter_examples.jar" manifest="${build.dir}/MANIFEST_BIN.MF">
       <zipfileset file="${resources.meta-inf}/default.notice"
         fullpath="META-INF/NOTICE" />
       <zipfileset file="${resources.meta-inf}/default.license"
         fullpath="META-INF/LICENSE" />
       <fileset dir="${build.examples}" includes="**/*.class" />
       <fileset dir="${src.examples}" includes="**/*.properties" />
     </jar>
   </target>
 
   <!-- lists of files needed for a binary distribution (excluding library files) -->
   <!-- Source files also needed at runtime -->
   <patternset id="dist.common.native">
     <include name="${dest.jar.jmeter}/BeanShell*.bshrc"/>
     <include name="${dest.jar.jmeter}/hc.parameters"/>
     <include name="${dest.jar.jmeter}/log4j.conf"/>
     <include name="${dest.jar.jmeter}/logkit.xml"/>
     <include name="${dest.jar.jmeter}/jmeter.properties"/>
     <include name="${dest.jar.jmeter}/upgrade.properties"/>
     <include name="${dest.jar.jmeter}/saveservice.properties"/>
     <include name="${dest.jar.jmeter}/users.dtd"/>
     <include name="${dest.jar.jmeter}/users.xml"/>
     <!-- Sample override properties files -->
     <include name="${dest.jar.jmeter}/httpclient.parameters"/>
     <include name="${dest.jar.jmeter}/system.properties"/>
     <include name="${dest.jar.jmeter}/user.properties"/>
     <!-- Exclude any files that might be present from testing the release -->
     <exclude name="${dest.jar.jmeter}/*.log"/>
     <include name="${dest.jar.jmeter}/examples/**"/>
     <!-- JMX files are in the non-native section -->
     <exclude name="${dest.jar.jmeter}/examples/*.jmx"/>
     <include name="${extras.dir}/**"/>
     <!-- Binary file types -->
     <exclude name="${extras.dir}/*.jar"/>
     <exclude name="${extras.dir}/*.jpg"/>
     <exclude name="${extras.dir}/*.png"/>
     <!-- File types that need to retain their EOL setting -->
     <exclude name="${extras.dir}/*.jmx"/>
     <exclude name="${extras.dir}/*.cmd"/>
     <exclude name="${extras.dir}/*.sh"/>
   </patternset>
 
   <patternset id="dist.binaries.native">
     <include name="LICENSE"/>
     <include name="NOTICE"/>
     <include name="README"/>
     <patternset refid="dist.common.native"/>
     <!-- Help documentation -->
     <include name="${dest.printable_docs}/**"/>
     <!-- Binary file types -->
     <exclude name="${dest.printable_docs}/**/*.pdf"/>
     <exclude name="${dest.printable_docs}/**/*.jmx"/>
     <!-- We also need the shared CSS for the printable docs -->
     <include name="${dest.docs}/css/**"/>
   </patternset>
 
   <!-- Source files also needed at runtime -->
   <patternset id="dist.common.non.native">
     <include name="${dest.jar.jmeter}/jmeter*"/>
     <exclude name="${dest.jar.jmeter}/jmeter.properties"/>
     <include name="${dest.jar.jmeter}/mirror-server.*"/>
     <include name="${dest.jar.jmeter}/shutdown.*"/>
     <include name="${dest.jar.jmeter}/stoptest.*"/>
     <!-- Fake SSL cert for JMeter proxy recorder in https -->
     <include name="${dest.jar.jmeter}/proxyserver.jks"/>
     <!-- Exclude any files that might be present from testing the release -->
     <exclude name="${dest.jar.jmeter}/*.log"/>
     <include name="${dest.jar.jmeter}/examples/*.jmx"/>
     <include name="${extras.dir}/*.jar"/>
     <include name="${extras.dir}/*.jpg"/>
     <include name="${extras.dir}/*.png"/>
     <!-- File types that need to retain their EOL setting -->
     <include name="${extras.dir}/*.jmx"/>
     <include name="${extras.dir}/*.cmd"/>
     <include name="${extras.dir}/*.sh"/>
   </patternset>
 
   <patternset id="dist.binaries.non.native">
     <patternset refid="dist.common.non.native"/>
     <include name="${dest.jar}/"/>
     <include name="${lib.dir}/bshclient.jar"/>
     <include name="${lib.dir}/jorphan.jar"/>
     <include name="${lib.dir}/junit/test.jar"/>
     <include name="${dest.jar.jmeter}/ApacheJMeter.jar"/>
     <!-- Help documentation, binary files -->
     <include name="${dest.printable_docs}/**/*.pdf"/>
     <include name="${dest.printable_docs}/**/*.jmx"/>
     <!-- We also need the shared images for the printable docs -->
     <include name="${dest.docs}/images/**"/>
   </patternset>
 
   <!--
       List of Unix executable files in the binary distribution
       These need special handling to create the correct file mode
   -->
   <property name="dist.executables"
     value="${dest.jar.jmeter}/jmeter ${dest.jar.jmeter}/jmeter-server ${dest.jar.jmeter}/*.sh ${extras.dir}/*.sh"/>
 
   <!-- List of files in source distribution that are eol=native -->
   <!--
   N.B. dist.sources[.non].native sets exclude source files present in dist.binaries[.non].native
   so that the nightly build src archive does not duplicate stuff in the binary archive
   (This may change, as the overlap does not waste much space)
   -->
   <patternset id="dist.sources.native">
     <include name="LICENSE"/>
     <include name="NOTICE"/>
     <include name="README"/>
     <include name="${src.dir}/**"/>
     <!-- Exclude binary types -->
     <exclude name="**/*.gif"/>
     <exclude name="**/*.jpg"/>
     <exclude name="**/*.png"/>
     <include name="${src.docs}/**"/>
   	<!-- Include some resources -->
   	<include name="${res.dir}/**"/>
     <!-- Exclude binary types (and JMX/JTL, which are not OS-dependent) -->
     <exclude name="${src.docs}/images/**"/>
     <exclude name="${src.docs}/**/*.jmx"/>
     <exclude name="${src.docs}/**/*.odt"/>
     <exclude name="${src.docs}/**/*.pdf"/>
     <exclude name="${src.docs}/**/*.sxi"/>
     <exclude name="${src.docs}/**/*.sxw"/>
     <include name="${src.test}/**"/>
     <include name="build.xml"/>
     <include name="build.properties"/>
     <include name="${dest.jar.jmeter}/testfiles/**"/>
     <exclude name="${dest.jar.jmeter}/testfiles/*.jmx"/>
     <exclude name="${dest.jar.jmeter}/testfiles/*.jtl"/>
     <!-- These are generated with EOL=LF -->
     <exclude name="${dest.jar.jmeter}/testfiles/BatchTestLocal.xml"/>
     <exclude name="${dest.jar.jmeter}/testfiles/Bug50898.xml"/>
     <exclude name="${dest.jar.jmeter}/testfiles/Bug52310.xml"/>
     <exclude name="${dest.jar.jmeter}/testfiles/Bug52968.xml"/>
     <exclude name="${dest.jar.jmeter}/testfiles/HTMLParserTestFile_2.xml"/>
     <!-- Ignore unit test output -->
     <exclude name="${dest.jar.jmeter}/testfiles/*.out"/>
     <exclude name="${dest.jar.jmeter}/testfiles/Sample_*.png"/>
     <include name="eclipse.classpath"/>
     <include name="eclipse.readme"/>
     <include name="checkstyle.xml"/>
     <include name="${lib.dir}/aareadme.txt"/>
     <include name="fb-*.x*"/>
     <!-- Make sure that the lib/opt directory is included in the archives -->
     <include name="${lib.opt}/README.txt"/>
   </patternset>
 
   <!-- Non-native items -->
   <patternset id="dist.sources.non.native">
     <include name="${src.dir}/**/*.gif"/>
     <include name="${src.dir}/**/*.jpg"/>
     <include name="${src.dir}/**/*.png"/>
     <include name="${src.docs}/images/**"/>
     <include name="${src.docs}/**/*.jmx"/>
     <include name="${src.docs}/**/*.odt"/>
     <include name="${src.docs}/**/*.pdf"/>
     <include name="${src.docs}/**/*.sxi"/>
     <include name="${src.docs}/**/*.sxw"/>
     <include name="${dest.jar.jmeter}/testfiles/*.jmx"/>
     <include name="${dest.jar.jmeter}/testfiles/*.jtl"/>
     <!-- These are generated with EOL=LF -->
     <include name="${dest.jar.jmeter}/testfiles/BatchTestLocal.xml"/>
     <include name="${dest.jar.jmeter}/testfiles/Bug50898.xml"/>
     <include name="${dest.jar.jmeter}/testfiles/Bug52310.xml"/>
     <include name="${dest.jar.jmeter}/testfiles/Bug52968.xml"/>
     <include name="${dest.jar.jmeter}/testfiles/HTMLParserTestFile_2.xml"/>
     <!-- Include the image files used in parsing / embedded download tests -->
     <include name="${dest.jar.jmeter}/testfiles/**/*.gif"/>
     <include name="${dest.jar.jmeter}/testfiles/**/*.jpg"/>
     <include name="${dest.jar.jmeter}/testfiles/**/*.png"/>
   </patternset>
 
     <!-- Convert eol:native source files to appropriate format if required -->
     <target name="_filter" unless="native.${eoltype}">
         <property name="workdir" value="${dist.dir}/${eoltype}"/>
         <echo level="info">Converting work files to eol=${eoltype} in ${workdir}</echo>
         <mkdir dir="${workdir}"/>
         <copy includeemptydirs="false" todir="${workdir}">
             <fileset dir=".">
                 <patternset refid="${fileset}"/>
             </fileset>
             <filterchain>
                 <fixcrlf encoding="${encoding}" fixlast="false" eol="${eoltype}" srcdir="${workdir}"/>
             </filterchain>
         </copy>
     </target>
 
     <!-- Files to be included in full source download -->
     <patternset id="dist_src_files_native">
         <patternset refid="dist.sources.native"/>
         <patternset refid="dist.common.native"/>
     </patternset>
 
     <patternset id="dist_src_files_non_native">
         <patternset refid="dist.sources.non.native"/>
         <patternset refid="dist.common.non.native"/>
     </patternset>
 
     <!-- Files to be included in full binary download -->
     <patternset id="dist_bin_files_native">
         <patternset refid="dist.binaries.native"/>
         <patternset refid="external.jars.notices"/>
         <!-- We don't need the site docs, but we do want Javadoc (e.g. for BeanShell) -->
         <include name="${dest.docs.api}/**"/>
         <exclude name="${dest.docs.api}/resources/**"/>
     </patternset>
 
     <patternset id="dist_bin_files_non_native">
         <patternset refid="dist.binaries.non.native"/>
         <patternset refid="external.jars"/>
         <include name="${dest.docs.api}/resources/**"/>
     </patternset>
 
     <!-- NOTE: the site documents are not included in either archive -->
 
     <!-- Invoke with -Djmeter.version=m.n -Duser.name=xyz@apache.org [-Dsvn.revision=nnnnn] [-Ddisplay.version=xxxx]
     Creates clean build and all documentation
     Creates runtime and source distributions and site documentation
     -->
     <target name="distribution"
         depends="svnCheck,check-versions,clean,install,docs-printable,docs-api,test,_distribution"
         description="Build JMeter for end-user distribution"/>
 
     <target name="check-versions" unless="disable-check-versions">
         <fail message="jmeter.version must be defined" unless="jmeter.version"/>
         <fail message="svn.revision must be defined" unless="svn.revision"/>
         <local         name="version.match"/>
         <condition property="version.match">
             <resourcecontains resource="${src.core}/org/apache/jmeter/util/JMeterVersion.java"
             	substring='VERSION = "${jmeter.version}";'/>
         </condition>
         <fail message="jmeter.version must be same as JMeterVersion.VERSION" unless="version.match"/>
     </target>
 
     <target
         name="nightly"
         depends="init-svnVersion,package,docs-printable,pack-nightly"
         description="Build JMeter for nightly dir (package docs-printable pack-nightly)"/>
 
     <target name="_eolcheck">
         <!-- Determine if the native format is CRLF or LF (or neither) -->
         <condition property="native.lf">
             <os family="unix"/>
         </condition>
         <condition property="native.crlf">
             <os family="dos"/>
         </condition>
         <!-- Define native.dir.x as either the source or updated directory as appropriate -->
         <condition property="native.dir.lf" value="." else="${dist.dir}/lf">
             <isset property="native.lf"/>
         </condition>
         <condition property="native.dir.crlf" value="." else="${dist.dir}/crlf">
             <isset property="native.crlf"/>
         </condition>
         <echoproperties prefix="native"></echoproperties>
     </target>
 
     <!-- Internal target -->
     <target name="_distribution" depends="check-versions,_eolcheck,check_jars">
     <property name="dist.name" value="apache-jmeter-${jmeter.version}"/>
     <property name="pack.name" value="${dist.name}"/>
     <echo level="info">Creating JMeter distribution ${dist.name} ${svn.revision}</echo>
     <mkdir dir="${dist.dir}"/>
 
     <!-- Delete work directories just in case -->
     <delete dir="${dist.dir}/crlf" quiet="true"/>
     <delete dir="${dist.dir}/lf" quiet="true"/>
 
     <!-- Runtime archives -->
     <antcall target="_filter">
         <param name="eoltype" value="lf"/>
         <param name="fileset" value="dist_bin_files_native"/>
     </antcall>
 
     <tar destfile="${dist.dir}/${pack.name}.tar" longfile="gnu">
       <tarfileset dir="." prefix="${dist.name}" excludes="${dist.executables}" defaultexcludes="yes">
          <patternset refid="dist_bin_files_non_native"/>
       </tarfileset>
@@ -1468,1149 +1469,1150 @@ run JMeter unless all the JMeter jars are added.
         <patternset refid="dist_src_files_non_native"/>
       </tarfileset>
     </tar>
     <!-- Delete work directory (may not exist) -->
     <delete dir="${dist.dir}/lf" quiet="true"/>
 
     <gzip zipfile="${dist.dir}/${pack.name}_src.tgz" src="${dist.dir}/${pack.name}_src.tar" />
     <!-- no longer needed -->
     <delete file="${dist.dir}/${pack.name}_src.tar"/>
     <antcall target="_hash">
         <param name="path" value="${dist.dir}/${dist.name}_src.tgz"/>
     </antcall>
 
     <antcall target="_filter">
         <param name="eoltype" value="crlf"/>
         <param name="fileset" value="dist_src_files_native"/>
     </antcall>
 
     <zip  zipfile="${dist.dir}/${pack.name}_src.zip">
       <zipfileset dir="${native.dir.crlf}" prefix="${dist.name}">
          <patternset refid="dist_src_files_native"/>
       </zipfileset>
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist_src_files_non_native"/>
       </zipfileset>
     </zip>
     <antcall target="_hash">
         <param name="path" value="${dist.dir}/${dist.name}_src.zip"/>
     </antcall>
     <!-- Delete work directory (may not exist) -->
     <delete dir="${dist.dir}/crlf" quiet="true"/>
 </target>
 
   <!-- Set up files for distribution to Maven via Nexus -->
   <target name="_dist_maven" depends="init-version">
     <!-- Ensure there aren't any stale files left -->
     <delete dir="${maven.dir}" includes="*.pom" quiet="true"/>
     <echo>Updating POM files to version ${jmeter.version}</echo>
     <copy todir="${maven.dir}">
         <fileset dir="${maven.poms}" includes="*.pom"/>
         <filterset>
             <filter token="MAVEN.DEPLOY.VERSION" value="${jmeter.version}"/>
         </filterset>
     </copy>
     <delete dir="${maven.dir}" includes="*.jar" quiet="true"/>
     <echo>Copying jar files ready for signing</echo>
     <copy todir="${maven.dir}">
       <fileset dir="${dest.jar}" includes="ApacheJMeter*.jar"/>
       <fileset dir="${dest.jar.jmeter}" includes="ApacheJMeter.jar"/>
       <fileset dir="${lib.dir}" includes="jorphan.jar"/>
     </copy>
     <copy tofile="${maven.dir}/ApacheJMeter_junit-test.jar" file="${lib.dir}/junit/test.jar"/>
     <!-- 
         Create the Maven jar needed to hold configuration data
         Cannot be added to any of the other jars as that would cause problems for stand-alone JMeter usage.
     -->
     <jar jarfile="${maven.dir}/ApacheJMeter_config.jar" manifest="${build.dir}/MANIFEST_SRC.MF">
         <zipfileset file="${resources.meta-inf}/default.notice"
           fullpath="META-INF/NOTICE" />
         <zipfileset file="${resources.meta-inf}/default.license"
           fullpath="META-INF/LICENSE" />
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="*.bshrc"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="*.parameters"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="*.properties"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="log4j.conf"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="logkit.xml"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="proxyserver.jks"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="users.dtd"/>
       <zipfileset dir="${dest.jar.jmeter}" prefix="bin" includes="users.xml"/>
     </jar>
   </target>
 
   <!-- 
         Upload jars/poms/sigs to local (/target), snapshots or releases repos; default is local.
         By default, deploys the artifacts to target/deploy under JMeter home.
 
         Must have Maven 2.2.1 or Maven 3.0.x installed.
         The environment variable M2_HOME must point to a suitable Maven installation (2.2.1+)
 
         For remote deployment, username/password will need to be set up in the appropriate
         "server" entries in the Maven settings.xml file, i.e. under:
 
         apache.snapshots.https
         apache.releases.https
 
         Pre-requisite:
         The jars and poms need to be made available. If this has not already been done as part of
         creating the distribution (e.g. a snapshot release is desired), then invoke the following:
 
               ant _dist_maven -Djmeter.version=2.8-SNAPSHOT
 
         For non-SNAPSHOT releases, the jars and poms need to be signed (TODO document how!!)
 
         Usage:
               ant maven_upload [-DrepoType=snapshots|releases]
   -->
   <target name="maven_upload" description="Upload jars and poms (and sigs if present).">
 
     <property environment="env"/>
     <!-- According to http://maven.apache.org/download.html#Installation M2_HOME applies to Maven 2.2.1 and 3.0.x -->
     <condition property="maven.present">
       <or>
         <isset property="env.M2_HOME"/>
         <isset property="maven.home"/> <!-- Allow override on command-line -->
       </or>
     </condition>
     <fail unless="${maven.present}" 
       message="The environment variable M2_HOME (or property maven.home) must point to a Maven installation"/>
 
     <property name="maven.home" value="${env.M2_HOME}"/>
 
     <!-- file repo url -->
     <property name="file.url" value="file:${basedir}/target/deploy"/>
 
     <!-- Apache Nexus snapshots repo url -->
     <property name="snapshots.url" value="https://repository.apache.org/content/repositories/snapshots"/>
     <!-- Apache Nexus snapshots repo name for servers section of settings.xml -->
     <property name="snapshots.repositoryId" value="apache.snapshots.https"/>
 
     <!-- Apache Nexus releases repo url -->
     <property name="releases.url" value="https://repository.apache.org/service/local/staging/deploy/maven2"/>
     <!-- Apache Nexus releases repo name for servers section of settings.xml -->
     <property name="releases.repositoryId" value="apache.releases.https"/>
 
     <property name="repoType" value="file"/>
 
     <!-- Hack to skip defining files/types/classifiers -->
     <condition property="XX" value="" else="XX">
       <and>
         <available file="${maven.dir}/jorphan.jar.asc"/>
         <available file="${maven.dir}/jorphan.pom.asc"/>
         <!-- Don't upload sigs to snapshots repo (might mislead users) -->
         <not>
           <equals arg1="snapshots" arg2="@repoType"/>
         </not>
       </and>
     </condition>
 
     <!-- Derived from:
          http://maven.apache.org/ant-tasks/examples/mvn.html#Using_the_Java_Task 
     -->
     <macrodef name="deployfile">
       <attribute name="stem" />
       <attribute name="packaging" default="jar"/>
       <attribute name="type" default="${repoType}"/>
       <attribute name="url" default="${@{type}.url}"/>
       <attribute name="repositoryId" default="${@{type}.repositoryId}"/>
       <sequential>
         <java classname="org.codehaus.classworlds.Launcher"
               fork="true"
               dir="${basedir}"
               failonerror="true">
           <jvmarg value="-Xmx512m"/>
           <classpath>
             <fileset dir="${maven.home}/boot">
               <include name="*.jar" />
             </fileset>
             <fileset dir="${maven.home}/lib">
               <include name="*.jar" />
             </fileset>
           </classpath>
           <sysproperty key="classworlds.conf" value="${maven.home}/bin/m2.conf" />
           <sysproperty key="maven.home" value="${maven.home}" />
           <arg value="--batch-mode"/>
           <!--arg value="-X"/-->
           <arg value="-DgeneratePom=false"/>
           <arg value="-Durl=@{url}"/>
           <arg value="-DrepositoryId=@{repositoryId}"/>
           <arg value="-DpomFile=${maven.dir}/@{stem}.pom"/>
           <arg value="-Dpackaging=@{packaging}"/>
           <arg value="-Dfile=${maven.dir}/@{stem}.${packaging}"/>
           <!--
                The XX property is a hack to avoid creating conditional code.
                It will be empty if the sigs exist; if not it will be XX which will be ignored by Maven 
           -->
           <!-- If packaging == pom, this will just upload the pom twice. Simpler than trying to conditionalise. -->
           <arg value="-D${XX}files=${maven.dir}/@{stem}.${packaging}.asc,${maven.dir}/@{stem}.pom.asc"/>
           <arg value="-D${XX}types=${packaging}.asc,pom.asc"/>
           <arg value="-D${XX}classifiers=,"/>
           <!-- Need at least version 2.7 of the plugin to upload additional files-->
           <arg value="org.apache.maven.plugins:maven-deploy-plugin:2.7:deploy-file"/>
         </java>
       </sequential>
     </macrodef>
 
 
     <deployfile stem="ApacheJMeter_parent" packaging="pom"/>
     <deployfile stem="jorphan"/>
     <deployfile stem="ApacheJMeter"/>
     <deployfile stem="ApacheJMeter_components"/>
     <deployfile stem="ApacheJMeter_config"/>
     <deployfile stem="ApacheJMeter_core"/>
     <deployfile stem="ApacheJMeter_ftp"/>
     <deployfile stem="ApacheJMeter_functions"/>
     <deployfile stem="ApacheJMeter_http"/>
     <deployfile stem="ApacheJMeter_java"/>
     <deployfile stem="ApacheJMeter_jdbc"/>
     <deployfile stem="ApacheJMeter_jms"/>
     <deployfile stem="ApacheJMeter_junit"/>
     <deployfile stem="ApacheJMeter_junit-test"/>
     <deployfile stem="ApacheJMeter_ldap"/>
     <deployfile stem="ApacheJMeter_mail"/>
     <deployfile stem="ApacheJMeter_monitors"/>
     <deployfile stem="ApacheJMeter_native"/>
     <deployfile stem="ApacheJMeter_report"/>
     <deployfile stem="ApacheJMeter_tcp"/>
   </target>
 
   <!--
   Gump targets.
   There are separate build and test projects for the jmeter module.
   -->
   <!-- Used by project jmeter-cvs -->
   <target name="gump-build"
       depends="_gump_properties,clean,install"
       description="Build JMeter">
     <property name="dist.name" value="apache-jmeter-${jmeter.version}"/>
     <available file="${velocity.jar}" property="velocity.present"/>
     <!-- No need to create the archives for Gump
     <antcall target="_pack-binaries"/>
     <antcall target="_pack-libraries"/>
     <antcall target="_pack-source"/>
     -->
   </target>
 
   <!-- Used by project jmeter-test -->
   <target name="gump-test"
       depends="_gump_properties,compile-tests,_test"
       description="Test JMeter in Gump">
     <!-- Show the log file
     <concat>
       <filelist dir="bin" files="jmeter-test.log" />
     </concat>
      -->
   </target>
 
   <target name="_gump_properties">
   <echo level="info">
  Gump properties for this run
   jmeter.version      = ${jmeter.version}
   gump.run            = ${gump.run}
   date.projectfile    = ${date.projectfile}
   version.projectfile = ${version.projectfile}
  Build file:
   version.build       = ${version.build}
  Java properties:
   target.java.version = ${target.java.version}
   src.java.version    = ${src.java.version}
   optimize            = ${optimize}
   deprecation         = ${deprecation}
   encoding            = ${encoding}
   </echo>
   <echoproperties prefix="ant"/>
   <echoproperties prefix="gump"/>
   <echoproperties prefix="os"/>
   <echoproperties prefix="java"/>
 
   </target>
 
   <target name="pack-src" depends="init-version">
     <property name="dist.name" value="apache-jmeter-${jmeter.version}"/>
     <antcall target="_pack-source"/>
   </target>
 
   <target name="pack-dist" depends="init-version">
     <property name="dist.name" value="apache-jmeter-${jmeter.version}"/>
     <antcall target="_pack-binaries"/>
     <antcall target="_pack-libraries"/>
     <antcall target="_pack-javadoc"/>
     <antcall target="_pack-source"/>
   </target>
 
   <!-- As pack-dist but without javadoc -->
   <target name="pack-nightly" depends="init-version">
     <property name="dist.name" value="apache-jmeter-${jmeter.version}"/>
     <antcall target="_pack-binaries"/>
     <antcall target="_pack-libraries"/>
     <antcall target="_pack-source"/>
   </target>
 
    <target name="_pack-binaries">
     <property name="pack.name" value="${dist.name}_bin"/>
     <mkdir dir="${dist.dir}"/>
     <tar destfile="${dist.dir}/${pack.name}.tar" longfile="gnu">
       <tarfileset dir="." prefix="${dist.name}" excludes="${dist.executables}" defaultexcludes="yes">
         <patternset refid="dist.binaries.native"/>
       </tarfileset>
       <tarfileset dir="." prefix="${dist.name}" excludes="${dist.executables}" defaultexcludes="yes">
         <patternset refid="dist.binaries.non.native"/>
       </tarfileset>
       <tarfileset mode="755" includes="${dist.executables}" dir="." prefix="${dist.name}" defaultexcludes="yes"/>
     </tar>
     <gzip zipfile="${dist.dir}/${pack.name}.tgz" src="${dist.dir}/${pack.name}.tar" />
     <!-- no longer needed -->
     <delete file="${dist.dir}/${pack.name}.tar"/>
     <zip  zipfile="${dist.dir}/${pack.name}.zip">
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.binaries.native"/>
       </zipfileset>
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.binaries.non.native"/>
       </zipfileset>
     </zip>
     </target>
 
    <target name="_pack-libraries">
     <property name="pack.name" value="${dist.name}_lib"/>
     <mkdir dir="${dist.dir}"/>
     <tar destfile="${dist.dir}/${pack.name}.tar" longfile="gnu">
       <tarfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="external.jars.notices"/>
       </tarfileset>
       <tarfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="external.jars"/>
       </tarfileset>
     </tar>
     <gzip zipfile="${dist.dir}/${pack.name}.tgz" src="${dist.dir}/${pack.name}.tar" />
     <!-- no longer needed -->
     <delete file="${dist.dir}/${pack.name}.tar"/>
     <zip  zipfile="${dist.dir}/${pack.name}.zip">
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="external.jars.notices"/>
       </zipfileset>
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="external.jars"/>
       </zipfileset>
     </zip>
     </target>
 
    <target name="_pack-javadoc">
     <property name="pack.name" value="${dist.name}_api"/>
     <mkdir dir="${dist.dir}"/>
     <tar destfile="${dist.dir}/${pack.name}.tar" longfile="gnu">
       <tarfileset includes="${dest.docs.api}" dir="." prefix="${dist.name}" defaultexcludes="yes"/>
     </tar>
     <gzip zipfile="${dist.dir}/${pack.name}.tgz" src="${dist.dir}/${pack.name}.tar" />
     <!-- no longer needed -->
     <delete file="${dist.dir}/${pack.name}.tar"/>
     <zip  zipfile="${dist.dir}/${pack.name}.zip">
        <zipfileset includes="${dest.docs.api}" dir="." prefix="${dist.name}" defaultexcludes="yes"/>
     </zip>
     </target>
 
    <target name="_pack-source">
     <property name="pack.name" value="${dist.name}_src"/>
     <mkdir dir="${dist.dir}"/>
     <tar destfile="${dist.dir}/${pack.name}.tar" longfile="gnu">
       <tarfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.sources.native"/>
       </tarfileset>
       <tarfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.sources.non.native"/>
       </tarfileset>
     </tar>
     <gzip zipfile="${dist.dir}/${pack.name}.tgz" src="${dist.dir}/${pack.name}.tar" />
     <!-- no longer needed -->
     <delete file="${dist.dir}/${pack.name}.tar"/>
     <zip  zipfile="${dist.dir}/${pack.name}.zip">
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.sources.native"/>
       </zipfileset>
       <zipfileset dir="." prefix="${dist.name}" defaultexcludes="yes">
         <patternset refid="dist.sources.non.native"/>
       </zipfileset>
     </zip>
     </target>
 
     <!-- Create a zip of all resource files for translators -->
     <target name="pack-resources">
         <mkdir dir="${dist.dir}"/>
         <zip  zipfile="${dist.dir}/resources.zip">
           <zipfileset dir="." defaultexcludes="yes">
             <patternset excludes="${src.dir}/examples/**" />
             <patternset includes="${src.dir}/**/*Resources.properties" />
             <patternset includes="${src.dir}/**/messages.properties" />
             <patternset includes="${src.dir}/**/i18nedit.properties" />
           </zipfileset>
         </zip>
     </target>
 
 <!--
     Utility target to create MD5 checksums in standard format (with *filename)
     Usage:
     <antcall target="_hash">
         <param name="path" value="archive.jar|zip|gz"/>
     </antcall>
 -->
 
     <target name="_hash" unless="hash.skip">
         <echo message="Creating MD5 for ${path}"/>
         <basename property="_base" file="${path}"/>
         <checksum algorithm="MD5" file="${path}" property="md5"/>
         <echo message="${md5} *${_base}" file="${path}.md5"/>
         <echo message="Creating SHA for ${path}"/>
         <checksum algorithm="SHA" file="${path}" property="sha"/>
         <echo message="${sha} *${_base}" file="${path}.sha"/>
     </target>
 
   <!--
   Clean-docs and clean-apidocs can be used to empty the docs or docs/api directories.
   This should be done before regenerating the documents for a release so that any obsolete files are detected.
   -->
   <target name="clean-docs">
     <delete>
         <fileset dir="${dest.docs}" excludes=".svn/** api/**"/>
     </delete>
   </target>
 
   <!-- Use this before running docs-print to ensure image files are synchronised -->
   <target name="clean-docs-images">
       <delete>
           <fileset dir="${dest.docs}/images" excludes=".svn/**"/>
       </delete>
   </target>
 
   <target name="clean-apidocs">
     <delete>
         <fileset dir="${dest.docs.api}" excludes=".svn/**"/>
     </delete>
   </target>
 
   <target name="clean" description="Clean up to force a build from source.">
     <!-- Unfortunately Ant reports failure if a directory does not exist -->
     <delete quiet="false" failonerror="false">
         <fileset dir="${dest.jar.jmeter}" includes="ApacheJMeter.jar"/>
         <fileset dir="${lib.dir}" includes="jorphan.jar"/>
         <fileset dir="${lib.dir}" includes="bshclient.jar"/>
         <fileset dir="${dest.jar}" includes="*.jar"/>
         <fileset dir="${build.dir}"/>
         <fileset dir="${dest.printable_docs}"/>
         <fileset dir="${dist.dir}"/>
     </delete>
   </target>
 
   <target name="clean-dist" description="Clean up dist directory.">
     <delete quiet="true">
         <fileset dir="${dist.dir}"/>
     </delete>
   </target>
 
   <target name="docs-api" description="Generate the API documentation.">
     <tstamp>
       <!-- Used to ensure end-year is up to date -->
       <format property="THISYEAR" pattern="yyyy"/>
     </tstamp>
     <mkdir dir="${dest.docs.api}"/>
     <delete quiet="true">
         <fileset dir="${dest.docs.api}" includes="**/*.html"/>
     </delete>
     <echo level="info">Updating overview to ${docversion}</echo>
     <replaceregexp match="version [^\s]+"
                  encoding="${encoding}"
                  replace="version ${docversion}"
                  flags="g" byline="true">
        <fileset dir="${src.docs}" includes="overview.html" />
     </replaceregexp>
     <javadoc
       sourcepathref="srcpaths"
       overview="${src.docs}/overview.html"
       additionalparam="-breakiterator"
       destdir="${dest.docs.api}"
       verbose="false"
       protected="yes"
       version="yes"
       doctitle="Apache JMeter API"
       windowtitle="Apache JMeter API"
       header="&lt;b&gt;Apache JMeter&lt;/b&gt;"
       bottom="Copyright &amp;#xA9; 1998-${THISYEAR} Apache Software Foundation. All Rights Reserved."
       packagenames="org.apache.jmeter.*,org.apache.jorphan.*"
       excludepackagenames="org.apache.jorphan.timer">
       <classpath refid="classpath"/>
       <link href="http://download.oracle.com/javase/1.5.0/docs/api/"/>
     </javadoc>
   </target>
 
 <!--
     Run Doccheck: See http://java.sun.com/j2se/javadoc/doccheck/docs/DocCheck.html
     and http://java.sun.com/j2se/javadoc/doccheck/
     Download the doclet, and put the jar in lib/opt.
     Output is in reports/ directory
 -->
 <target name="docs-check">
     <javadoc sourcepathref="srcpaths"
     destdir="reports"
     docletpath="${lib.opt}/doccheck.jar"
     packagenames="org.apache.jmeter.*,org.apache.jorphan.*"
     excludepackagenames="org.apache.jmeter.util.keystore,org.apache.jorphan.timer">
       <classpath refid="classpath"/>
       <doclet name="com.sun.tools.doclets.doccheck.DocCheck">
         <!--
             -execDepth: 1=org.* 2=org.apache.* 3+=org.apache.jmeter.*
             -evident does not seem to work
          -->
         <param name="-execDepth" value="3"/>
         <param name="-evident" value="4"/>
       </doclet>
     </javadoc>
 </target>
 
   <target name="docs-site" depends="init-docs" if="AnakiaTask.present" description="Generate browsable HTML documentation in web-site format.">
     <taskdef name="anakia" classpathref="anakia.classpath" classname="org.apache.velocity.anakia.AnakiaTask"/>
     <!-- The extending pages are rather out of date (and not linked from elsewhere) -->
     <anakia basedir="${src.docs}" destdir="${dest.docs}/" extension=".html" style="${eclipse.anakia}/xdocs/stylesheets/site.vsl" projectFile="./stylesheets/project.xml"
         excludes="**/stylesheets/** extending.xml extending/*.xml"
         includes="**/*.xml" lastModifiedCheck="${anakia.lastModifiedCheck}" velocityPropertiesFile="${src.docs}/velocity.properties"/>
     <echo level="info">Fixing EOL</echo>
     <fixcrlf encoding="iso-8859-1" srcdir="${dest.docs}/" includes="**/*.html" excludes="api/**" fixlast="false"/>
     <echo level="info">Removing unnecessary &lt;/br> tags</echo>
     <replace encoding="iso-8859-1" dir="${dest.docs}/" includes="**/*.html" token="&lt;/br>" value=""/>
     <echo level="info">Copying files</echo>
     <copy todir="${dest.docs}/css">
       <fileset dir="${src.css}"/>
     </copy>
     <copy todir="${dest.docs}/images">
       <fileset dir="${src.images}"/>
     </copy>
     <copy todir="${dest.docs}/demos">
       <fileset dir="${src.demos}"/>
     </copy>
     <copy todir="${dest.docs}/usermanual">
       <fileset file="${src.docs}/usermanual/*.pdf"/>
     </copy>
     <copy todir="${dest.docs}/extending">
       <fileset file="${src.docs}/extending/jmeter_tutorial.pdf"/>
     </copy>
     <copy todir="${dest.docs}/">
       <fileset file="${src.docs}/download_jmeter.cgi"/>
     </copy>
   </target>
 
   <target name="docs-printable" depends="init-docs" if="AnakiaTask.present" description="Generate printable HTML documentation.">
     <taskdef name="anakia" classpathref="anakia.classpath" classname="org.apache.velocity.anakia.AnakiaTask"/>
     <!-- The extending pages are rather out of date (and not linked from elsewhere) -->
     <anakia basedir="${src.docs}" destdir="${dest.printable_docs}/" extension=".html" style="${eclipse.anakia}/xdocs/stylesheets/site_printable.vsl" projectFile="./stylesheets/printable_project.xml"
         excludes="**/stylesheets/** extending.xml extending/*.xml"
         includes="**/*.xml" lastModifiedCheck="${anakia.lastModifiedCheck}" velocityPropertiesFile="${src.docs}/velocity.properties"/>
     <echo level="info">Fixing EOL</echo>
     <fixcrlf encoding="iso-8859-1" srcdir="${dest.printable_docs}/" includes="**/*.html" fixlast="false"/>
     <echo level="info">Removing unnecessary &lt;/br> tags</echo>
     <replace encoding="iso-8859-1" dir="${dest.printable_docs}/" includes="**/*.html" token="&lt;/br>" value=""/>
     <!--
     Share images with non-printable version
     Means printable version won't work on web-site
     -->
     <echo level="info">Copying files</echo>
     <copy todir="${dest.docs}/css">
       <fileset dir="${src.css}"/>
     </copy>
     <copy todir="${dest.docs}/images">
       <fileset dir="${src.images}"/>
     </copy>
     <copy todir="${dest.printable_docs}/demos">
       <fileset dir="${src.demos}"/>
     </copy>
     <copy todir="${dest.printable_docs}/usermanual">
         <fileset file="${src.docs}/usermanual/*.pdf"/>
     </copy>
     <copy todir="${dest.printable_docs}/extending">
       <fileset file="${src.docs}/extending/jmeter_tutorial.pdf"/>
     </copy>
   </target>
 
   <target name="test" depends="compile-tests,_test,_allbatchtests"
     description="Run tests (use -Djava.awt.headless=true on systems without graphic displays)"/>
 
   <target name="test-both" depends="test-headless,test-headed"/>
 
   <target name="test-headless" depends="compile-tests">
     <antcall target="_test">
       <param name="test.headless" value="true"/>
     </antcall>
   </target>
 
   <target name="test-headed" depends="compile-tests">
     <antcall target="_test">
       <param name="test.headless" value="false"/>
     </antcall>
   </target>
 
   <target name="batchtestserver" description="Run the batch test using client-server mode">
     <property name="batchtestserver.out" location="${basedir}/bin"/>
     <property name="batchtestserver.log" value="BatchTestServer.log"/>
     <property name="rmi_port" value="2099"/>
     <parallel>
         <daemons>
             <java taskname="server" classname="org.apache.jmeter.NewDriver" fork="yes" dir="${batchtestserver.out}">
                <classpath>
                 <fileset dir="${dest.jar.jmeter}" includes="*.jar"/>
                 <fileset dir="${dest.jar}" includes="*.jar"/>
                 <path refid="classpath"/>
                </classpath>
                <sysproperty key="java.awt.headless" value="true"/>
                 <arg value="-ptestfiles/jmeter-batch.properties"/>
                 <arg value="-j"/>
                 <arg value="${batchtestserver.out}/${batchtestserver.log}"/>
                 <arg value="-Dserver_port=${rmi_port}"/>
                 <arg value="-s"/>
                 <arg value="-Jserver.exitaftertest=true"/>
              </java>
         </daemons>
         <sequential>
             <sleep seconds="1"/>
             <antcall target="batchtest">
                 <param name="remote" value="-Rlocalhost:${rmi_port}"/>
                 <param name="taskname" value="client"/>
             </antcall>
         </sequential>
     </parallel>
     <!-- Show the log file -->
     <concat>
       <filelist dir="${batchtestserver.out}" files="${batchtestserver.log}" />
     </concat>
 
     <local         name="BatchTestLocalServer.len"/>
     <condition property="BatchTestLocalServer.len">
         <length file="${batchtestserver.out}/${batchtestserver.log}" when="equal" length="0" />
     </condition>
     <fail unless="BatchTestLocalServer.len">
         Error detected in server log file. See above.
     </fail>
 
     <delete>
         <fileset dir="${batchtestserver.out}">
              <include name="${batchtestserver.log}"/>
         </fileset>
      </delete>
   </target>
 
   <target name="batchtest" description="Run the batch test and compare output files">
 
     <!-- will be overwritten by antcall paramaters -->
     <property name="taskname" value="jmeter"/>
     <property name="remote" value="-X"/>
     <property name="batchtest.inp" location="${basedir}/bin/testfiles"/>
     <property name="batchtest.out" location="${basedir}/bin"/>
     <property name="batchtest.name" value="BatchTestLocal"/>
 
     <!-- Fix the EOL in case the file was derived from the "wrong" archive type -->
     <fixcrlf srcdir="${batchtest.inp}" includes="${batchtest.name}.csv"/>
 
     <echo level="info" message="Starting ${batchtest.name} using ${remote}"/>
 
     <macrodef name="deleteworkfiles">
         <sequential>
         <delete>
             <fileset dir="${batchtest.out}">
                  <include name="${batchtest.name}.csv"/>
                  <include name="${batchtest.name}.xml"/>
                  <include name="${batchtest.name}.log"/>
                  <include name="${batchtest.name}.jtl"/>
             </fileset>
          </delete>
         </sequential>
     </macrodef>
 
     <macrodef name="checkfile">
        <attribute name="type" default=""/>
        <attribute name="file"/>
        <sequential>
           <local name="found"/>
           <!--echo>Looking for @{file}</echo-->
          <available property="found" file="@{file}"/>
          <fail message="Cannot find @{type} file @{file}" unless="found"/>
        </sequential>
     </macrodef>
 
     <checkfile type="input" file="${batchtest.inp}${file.separator}${batchtest.name}.csv"/>
     <checkfile type="input" file="${batchtest.inp}${file.separator}${batchtest.name}.xml"/>
 
     <deleteworkfiles/>
 
     <java taskname="${taskname}" classname="org.apache.jmeter.NewDriver" fork="yes" dir="${basedir}/bin">
        <classpath>
         <fileset dir="${dest.jar.jmeter}" includes="*.jar"/>
         <fileset dir="${dest.jar}" includes="*.jar"/>
         <path refid="classpath"/>
        </classpath>
         <!-- Detect if non-GUI runs OK headless by forcing it to try using non-headless mode -->
        <sysproperty key="java.awt.headless" value="false"/>
         <arg value="-ptestfiles/jmeter-batch.properties"/>
         <arg value="-n"/>
         <arg value="-ttestfiles/${batchtest.name}.jmx"/>
         <arg value="-j"/>
         <arg value="${batchtest.name}.log"/>
         <arg value="-l"/>
         <arg value="${batchtest.name}.jtl"/>
         <arg value="${remote}"/>
         <!-- Check properties can be passed to local/remote tests -->
         <arg value="-Jmodule=Module"/>
         <arg value="-Gmodule=Module"/>
         <!-- Check property can be used for filenames in local/remote tests (no need to defined as -G) -->
         <arg value="-JCSVFILE=${batchtest.name}.csv"/>
      </java>
 
     <checkfile type="output" file="${batchtest.out}${file.separator}${batchtest.name}.csv"/>
     <checkfile type="output" file="${batchtest.out}${file.separator}${batchtest.name}.xml"/>
     <checkfile type="output" file="${batchtest.out}${file.separator}${batchtest.name}.jtl"/>
 
     <local         name="BatchTestLocal.csv.OK"/>
     <condition property="BatchTestLocal.csv.OK">
         <filesmatch file1="${batchtest.inp}/${batchtest.name}.csv" file2="${batchtest.out}/${batchtest.name}.csv"/>
     </condition>
     <fail unless="BatchTestLocal.csv.OK">
         CSV Files are not identical.
         ${batchtest.inp}${file.separator}${batchtest.name}.csv
         ${batchtest.out}${file.separator}${batchtest.name}.csv
     </fail>
 
     <local         name="BatchTestLocal.xml.OK"/>
     <condition property="BatchTestLocal.xml.OK">
        <filesmatch file1="${batchtest.inp}/${batchtest.name}.xml" file2="${batchtest.out}/${batchtest.name}.xml"/>
     </condition>
     <fail unless="BatchTestLocal.xml.OK">
         XML Files are not identical.
         ${batchtest.inp}${file.separator}${batchtest.name}.xml
         ${batchtest.out}${file.separator}${batchtest.name}.xml
     </fail>
 
     <echo level="info">${batchtest.name} output files compared OK</echo>
 
     <!-- Show the log file -->
     <concat>
       <filelist dir="${batchtest.out}" files="${batchtest.name}.log" />
     </concat>
 
     <local         name="BatchTestLocal.len"/>
     <condition property="BatchTestLocal.len">
         <length file="${batchtest.out}/${batchtest.name}.log" when="equal" length="0" />
     </condition>
     <fail unless="BatchTestLocal.len">
         Error detected in log file. See above.
     </fail>
 
     <deleteworkfiles/>
 
   </target>
 
   <!-- Additional test scripts -->
   <target name="batch_scripts">
       <antcall target="batchtest">
           <param name="batchtest.name" value="HTMLParserTestFile_2"/>
       </antcall>
       <antcall target="batchtest">
           <param name="batchtest.name" value="Bug52310"/>
       </antcall>
       <antcall target="batchtest">
   	      <param name="batchtest.name" value="Bug52968"/>
   	  </antcall>
   	
       <antcall target="batchtest">
           <param name="batchtest.name" value="Bug50898"/>
       </antcall>
   </target>
 
   <!-- Run all batch tests; used by test target -->
   <target name="_allbatchtests" depends="batchtest,batchtestserver,batch_scripts">
   </target>
 
    <!-- Generic test target, not intended to be called directly -->
    <target name="_test">
    <!--
    The property java.awt.headless is not automatically passed on,
    because the tests are run in a separate JVM from the build.
 
    It is assumed that Gump sets java.awt.headless if required.
 
    Set test.headless from the java.awt property, if that
    is defined, otherwise it us set to "".
    N.B. if test.headless is already set, it will not be changed
    This allows the property to be over-ridden by test-headless etc.
    -->
     <condition property="test.headless" value="${java.awt.headless}">
       <isset property="java.awt.headless"/>
     </condition>
      <!-- make sure test.headless is set to something -->
      <condition property="test.headless" value="">
       <not><isset property="java.awt.headless"/></not>
      </condition>
 
    <!-- set build.test.gump to build.test if not already set -->
     <condition property="build.test.gump" value="${build.test}">
       <not><isset property="build.test.gump"/></not>
     </condition>
    <echo level="info">
    gump.run = ${gump.run}
    java.awt.headless = ${java.awt.headless}
    test.headless = ${test.headless}
    user.dir = ${user.dir}
    basedir = ${basedir}
    test dir = ${build.test}
    test dir gump = ${build.test.gump}
    testsaveservice.saveout = ${testsaveservice.saveout}
    test.encoding = ${test.encoding}
    </echo>
    <delete quiet="true">
       <fileset dir="${basedir}/bin/testfiles" includes="*.jmx.out"/>
    </delete>
    <!-- fork="yes" is required or dir attribute is ignored -->
    <java classname="org.apache.jorphan.test.AllTests" fork="yes" failonerror="true" dir="${basedir}/bin">
       <classpath>
         <fileset dir="${dest.jar.jmeter}" includes="ApacheJMeter.jar"/>
         <fileset dir="${dest.jar}" includes="*.jar"/>
         <pathelement location="${build.test}"/>
         <path refid="classpath"/>
       </classpath>
       <jvmarg value="-server"/>
       <jvmarg value="-Dfile.encoding=${test.encoding}"/>
       <sysproperty key="java.awt.headless" value="${test.headless}"/>
       <sysproperty key="testsaveservice.saveout" value="${testsaveservice.saveout}" />
       <sysproperty key="gump.run" value="${gump.run}" />
       <arg value="${build.test}"/>
       <arg value="${basedir}/bin/testfiles/jmetertest.properties"/>
       <arg value="org.apache.jmeter.util.JMeterUtils"/>
     </java>
   </target>
 
 <!--
     In order to run JUnit, both junit.jar and optional.jar need to be on the Ant classpath
     optional.jar is normally found in ANT_HOME/lib
 -->
   <target name="junit"  description="Run individual JUnit test">
   <mkdir dir="reports"/>
   <property name="test.format" value="plain"/>
   <property name="test.case" value="org.apache.jorphan.test.AllTests"/>
   <junit fork="true"
    dir="${basedir}/bin"
    showoutput="false"
    printsummary="on">
   <formatter type="${test.format}" usefile="yes"/>
   <formatter type="xml"/>
   <jvmarg value="-Dfile.encoding=${test.encoding}"/>
       <classpath>
         <fileset dir="${dest.jar}" includes="*.jar"/>
         <pathelement location="${build.test}"/>
         <path refid="classpath"/>
       </classpath>
   <test name="${test.case}" todir="reports"/>
   </junit>
   </target>
 
   <!-- Utility target to collate reports -->
   <target name="junitreport">
     <mkdir dir="reports"/>
     <junitreport todir="reports">
       <fileset dir="reports">
         <include name="TEST-*.xml"/>
       </fileset>
       <report format="frames" todir="reports"/>
     </junitreport>
   </target>
 
     <target name="generator_jar" depends="compile-tests" description="Create the test tree generator jar">
       <jar jarfile="${dest.jar}/ApacheJMeter_generator.jar"
           manifest="${build.dir}/MANIFEST_BIN.MF">
         <zipfileset file="${resources.meta-inf}/default.notice"
           fullpath="META-INF/NOTICE" />
         <zipfileset file="${resources.meta-inf}/default.license"
           fullpath="META-INF/LICENSE" />
         <fileset dir="${build.test}" includes="**/GenerateTreeGui*.class"/>
       </jar>
     </target>
 
   <target name="svnCheck" description="Use SVN to get the current revision" unless="disable-svnCheck">
     <exec executable="svn" logerror="true" outputproperty="svn.exec.result" failonerror="true" failifexecutionfails="true">
       <arg line="info"/>
     </exec>
     <loadresource property="svn.revision">
       <string value="${svn.exec.result}"/>
       <filterchain>
         <linecontains>
           <contains value="Last Changed Rev: "/>
         </linecontains>
         <tokenfilter>
             <!-- Remove all but the revision number -->
             <replaceregex pattern=".*: " replace=""/>
         </tokenfilter>
         <striplinebreaks/>
       </filterchain>
     </loadresource>
     <echo level="info" message="svn.revision=${svn.revision}"/>
   </target>
 
     <!-- Macro is needed to be able to perform indirect evaluation of property names -->
     <macrodef name="process_jarfile">
         <attribute name="jarname"/>
         <attribute name="dest.dir" default="${lib.dir}"/>
         <sequential>
             <!-- Call all possible targets; these are only executed if the appropriate property is set -->
             <antcall target="_check_exists">
                 <param name="file" value="@{dest.dir}/${@{jarname}.jar}"/>
                 <param name="loc" value="${@{jarname}.loc}"/>
                 <param name="jar" value="${@{jarname}.jar}"/>
                 <param name="path" value="@{dest.dir}"/>
                 <param name="md5"  value="${@{jarname}.md5}"/>
                 <param name="_checkMD5" value="true"/>
             </antcall>
             <antcall target="_check_jarfile">
                 <param name="loc" value="${@{jarname}.loc}"/>
                 <param name="jar" value="${@{jarname}.jar}"/>
                 <param name="path" value="@{dest.dir}"/>
                 <param name="md5"  value="${@{jarname}.md5}"/>
                 <param name="_checkMD5" value="true"/>
                 <param name="zip" value="${@{jarname}.zip}"/>
                 <param name="ent" value="${@{jarname}.ent}"/>
                 <param name="zipprop" value="@{jarname}.zip"/>
             </antcall>
         </sequential>
     </macrodef>
 
     <!-- Check if jarfile exists, and set properties for calling zip or jar download -->
     <target name="_check_jarfile" if="_get_file">
         <!-- Check if file exists -->
         <fail message="Error in build file or calling sequence" if="file.exists"/>
         <echo level="info">Checking ${jar}</echo>
         <available file="${path}/${jar}" property="file.exists"/>
         <condition property="_get_zipfile">
             <isset property="${zipprop}"/>
         </condition>
         <condition property="_get_jarfile">
             <not>
                 <isset property="${zipprop}"/>
             </not>
         </condition>
         <!-- Emulate conditional execution; targets use if/unless to suppress execution -->
         <antcall target="_get_jarfile"/>
         <antcall target="_get_zipfile"/>
     </target>
 
     <!-- Get a zip file and unpack it -->
     <target name="_get_zipfile" if="_get_zipfile" unless="file.exists">
         <get src="${loc}/${zip}"
              dest="${build.dir}/${zip}"
              usetimestamp="true" ignoreerrors="false"/>
         <unzip dest="${build.dir}" src="${build.dir}/${zip}">
             <patternset>
                 <include name="**/${ent}"/>
             </patternset>
             <!-- This requires Ant 1.7.0 or later -->
             <mapper type="flatten"/>
         </unzip>
         <antcall target="_checkMD5">
             <param name="file" value="${ent}"/>
             <param name="path" value="${build.dir}"/>
             <param name="md5"  value="${md5}"/>
         </antcall>
         <move preservelastmodified="true" overwrite="true"
             file="${build.dir}/${ent}" tofile="${path}/${jar}" verbose="true"/>
     </target>
 
     <!-- Download a jar file and check its hash; if correct, move to correct directory -->
     <target name="_get_jarfile" if="_get_jarfile" unless="file.exists">
         <echo message="Fetching: ${path}/${jar}" level="info"/>
         <get src="${loc}/${jar}"
              dest="${build.dir}/${jar}"
              usetimestamp="false" ignoreerrors="false"/>
         <antcall target="_checkMD5">
             <param name="file" value="${jar}"/>
             <param name="path" value="${build.dir}"/>
             <param name="md5"  value="${md5}"/>
         </antcall>
         <move preservelastmodified="true" overwrite="true"
             file="${build.dir}/${jar}" tofile="${path}/${jar}" verbose="true"/>
     </target>
 
     <!-- Ant subroutine, required to localise MD5OK property -->
     <target name="_checkMD5" if="_checkMD5">
         <!--
         @param path - location of file
         @param file - file name
         -->
         <checksum algorithm="MD5" file="${path}/${file}" property="MD5"/>
         <condition property="MD5OK">
             <equals arg1="${MD5}" arg2="${md5}" casesensitive="false"/>
         </condition>
         <fail unless="MD5OK">Bad Checksum: for ${file}
         expected ${md5}
         actual   ${MD5}</fail>
         <echo level="info" message="Checksum OK: ${file}"/>
     </target>
 
     <!--
     Generic target to process all external jars.
     The "process_jarfile" macro resolves the properties that begin with the jarname
     and calls all the possible targets. The targets use "if" and "unless" to provide
     conditional execution (it would be a lot easier if antcall supported if/unless).
     -->
     <target name="_process_all_jars">
         <process_jarfile jarname="apache-bsf"/>
         <process_jarfile jarname="apache-jsr223-api"/>
         <process_jarfile jarname="avalon-framework"/>
         <process_jarfile jarname="bcmail" dest.dir="${lib.api}"/>
         <process_jarfile jarname="bcprov" dest.dir="${lib.api}"/>
         <process_jarfile jarname="beanshell"/>
         <process_jarfile jarname="commons-codec"/>
         <process_jarfile jarname="commons-collections"/>
         <process_jarfile jarname="commons-httpclient"/>
         <process_jarfile jarname="commons-io"/>
         <process_jarfile jarname="commons-jexl"/>
         <process_jarfile jarname="commons-jexl2"/>
         <process_jarfile jarname="commons-lang"/>
+        <process_jarfile jarname="commons-lang3"/>
         <process_jarfile jarname="commons-logging"/>
         <process_jarfile jarname="commons-net"/>
         <process_jarfile jarname="excalibur-datasource"/>
         <process_jarfile jarname="excalibur-instrument"/>
         <process_jarfile jarname="excalibur-logger"/>
         <process_jarfile jarname="excalibur-pool"/>
         <process_jarfile jarname="htmllexer"/>
         <process_jarfile jarname="htmlparser"/>
         <process_jarfile jarname="httpclient"/>
         <process_jarfile jarname="httpmime"/>
         <process_jarfile jarname="httpcore"/>
         <process_jarfile jarname="jakarta-oro"/>
         <process_jarfile jarname="jdom"/>
         <process_jarfile jarname="js_rhino"/>
         <process_jarfile jarname="junit"/>
         <process_jarfile jarname="logkit"/>
         <process_jarfile jarname="soap"/>
         <process_jarfile jarname="tidy"/>
         <process_jarfile jarname="xstream"/>
     	<process_jarfile jarname="xmlpull"/>
     	<process_jarfile jarname="xpp3"/>
         <process_jarfile jarname="serializer"/>
         <process_jarfile jarname="xalan"/>
         <process_jarfile jarname="xerces"/>
         <process_jarfile jarname="xml-apis"/>
         <process_jarfile jarname="xmlgraphics-commons"/>
         <process_jarfile jarname="activation"/>
         <process_jarfile jarname="javamail"/>
         <process_jarfile jarname="jms"/>
         <process_jarfile jarname="velocity"   dest.dir="${lib.doc}"/>
         <process_jarfile jarname="jcharts"/>
     </target>
 
     <target name="_process_doc_jars">
       <process_jarfile jarname="commons-collections"/>
       <process_jarfile jarname="commons-lang"/>
       <process_jarfile jarname="jdom"/>
       <process_jarfile jarname="velocity"   dest.dir="${lib.doc}"/>
     </target>
 
     <!-- Download all missing jars.-->
     <target name="download_jars" description="Download any missing jar files">
         <!-- build.dir may be needed as a temporary work area -->
         <mkdir dir="${build.dir}" />
         <antcall target="_process_all_jars">
             <param name="_get_file" value="true"/>
         </antcall>
     </target>
 
     <target name="download_doc_jars">
       <!-- build.dir may be needed as a temporary work area -->
       <mkdir dir="${build.dir}" />
       <antcall target="_process_doc_jars">
           <param name="_get_file" value="true"/>
       </antcall>
     </target>
 
     <target name="_check_exists" if="_check_exists">
         <fail message="Invalid call sequence - file.exists should not be defined" if="file.exists"/>
         <available file="${file}" property="file.exists"/>
         <fail message="Could not find file ${file}" unless="file.exists"/>
         <antcall target="_checkMD5">
             <param name="file" value="${jar}"/>
             <param name="path" value="${path}"/>
             <param name="md5"  value="${md5}"/>
         </antcall>
         <!--echo level="info" message="Found ${file}"/-->
     </target>
 
     <target name="check_jars" description="Check that all required jar files are present" unless="no_check_jars">
         <antcall target="_process_all_jars">
             <param name="_check_exists" value="true"/>
         </antcall>
     </target>
 
     <target name="checkstyle">
         <taskdef resource="checkstyletask.properties"
                  classpath="${lib.opt}/checkstyle-5.3-all.jar"/>
         <checkstyle config="checkstyle.xml">
           <fileset dir="src" includes="**/*.java"/>
           <formatter type="plain"/>
           <formatter type="xml" toFile="build/checkstyle_errors.xml"/>
         </checkstyle>
 
     </target>
   
     <target name="sign_dist"
       description="Sign release artifacts in dist and dist/maven.  Usage: ant sign_dist -Dgpg.keyname=key-id [-Dgpg.secretKeyring=path-to-keyring]      ">
       <scriptdef name="gpg" language="beanshell">
         <classpath>
           <pathelement location="${lib.dir}/${beanshell.jar}"/>
           <!-- Needed to work with Java 1.5 -->
           <pathelement location="${lib.dir}/${apache-bsf.jar}"/>
           <pathelement location="${lib.dir}/${commons-logging.jar}"/>
         </classpath>
         <element name="fileset" type="fileset"/>
         <![CDATA[
         int execcode( String command ) // helper 
         {
             StringTokenizer st = new StringTokenizer(command);
             String[] cmdarray = new String[st.countTokens()];
             for (int i = 0; st.hasMoreTokens(); i++) {
                 cmdarray[i] = st.nextToken();
             }
             // Merge stderr with stdout so only have to fetch one stream
             proc = new ProcessBuilder(cmdarray).redirectErrorStream(true).start();
             din = new DataInputStream( proc.getInputStream() );
             while( (line=din.readLine()) != null ) {
                 print(line);
             }
             return this.proc.waitFor();
         }
           keyname = project.getProperty("gpg.keyname");
           // Java 1.6 returns void, Java 1.5 returns null
           if (keyname == void || keyname == null) {
             self.fail("Please provide the gpg.keyname property");
           }
           keyring = project.getProperty("gpg.secretKeyring");
           if (keyring == void || keyring == null) {
             keyring = "secring.gpg"; // the default
           }
           sep=java.io.File.separator;
           cmd="gpg2 --batch --yes -ba -u " + keyname + " --secret-keyring " + keyring;
           self.log("Command: "+cmd);
           filesets = elements.get("fileset");
           for (i = 0; i < filesets.size(); ++i) {
             fileset = filesets.get(i);
             basedir = fileset.getDir();
             self.log("Processing "+basedir);
             ds = fileset.getDirectoryScanner();
             srcFiles = ds.getIncludedFiles();
             for (j=0; j < srcFiles.length; j++) {
               srcFile=srcFiles[j];
               file=basedir + sep + srcFile;
               self.log("Signing "+srcFile);
               ret=execcode(cmd+ " " + file);
               if (ret != 0) {
                  self.fail("Command failed: "+ret);
               }
             }
           }
         ]]>
       </scriptdef>
       <gpg>
         <fileset dir="${dist.dir}"  includes="*.*" excludes="*.asc *.md5 *.sha1 *.sha"/>
         <fileset dir="${maven.dir}" includes="*.*" excludes="*.asc *.md5 *.sha1 *.sha"/>
       </gpg>
     </target>
 </project>
diff --git a/eclipse.classpath b/eclipse.classpath
index 57c1e0c5c..a3de5707a 100644
--- a/eclipse.classpath
+++ b/eclipse.classpath
@@ -1,89 +1,89 @@
 <?xml version="1.0" encoding="UTF-8"?>
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
 <!-- 
    This is a sample Eclipse .classpath file, which can be used to help set up the proper .classpath.
    Note that the "con" entry may be different on individual systems, but the other entries
    should generally be the same, unless you attach sources or javadoc to the jars.    
  -->
 <classpath>
     <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
 	<classpathentry kind="src" output="build/components" path="src/components"/>
 	<classpathentry kind="src" output="build/core" path="src/core"/>
 	<classpathentry kind="src" output="build/examples" path="src/examples"/>
 	<classpathentry kind="src" output="build/functions" path="src/functions"/>
 	<classpathentry kind="src" output="build/jorphan" path="src/jorphan"/>
 	<classpathentry kind="src" output="build/junit" path="src/junit"/>
 	<classpathentry kind="src" output="build/monitor/components" path="src/monitor/components"/>
 	<classpathentry kind="src" output="build/monitor/model" path="src/monitor/model"/>
 	<classpathentry kind="src" output="build/protocol/ftp" path="src/protocol/ftp"/>
 	<classpathentry kind="src" output="build/protocol/http" path="src/protocol/http"/>
 	<classpathentry kind="src" output="build/protocol/java" path="src/protocol/java"/>
 	<classpathentry kind="src" output="build/protocol/jdbc" path="src/protocol/jdbc"/>
 	<classpathentry kind="src" output="build/protocol/jms" path="src/protocol/jms"/>
 	<classpathentry kind="src" output="build/protocol/ldap" path="src/protocol/ldap"/>
 	<classpathentry kind="src" output="build/protocol/mail" path="src/protocol/mail"/>
     <classpathentry kind="src" output="build/protocol/native" path="src/protocol/native"/>
 	<classpathentry kind="src" output="build/protocol/tcp" path="src/protocol/tcp"/>
 	<classpathentry kind="src" output="build/reports" path="src/reports"/>
 	<classpathentry kind="src" output="build/test" path="test/src"/>
     <classpathentry kind="lib" path="lib/activation-1.1.1.jar"/>
 	<classpathentry kind="lib" path="lib/avalon-framework-4.1.4.jar"/>
 	<classpathentry kind="lib" path="lib/bsf-2.4.0.jar"/>
     <classpathentry kind="lib" path="lib/bsf-api-3.1.jar"/>
 	<classpathentry kind="lib" path="lib/bsh-2.0b5.jar"/>
 	<classpathentry kind="lib" path="lib/commons-codec-1.6.jar"/>
 	<classpathentry kind="lib" path="lib/commons-collections-3.2.1.jar"/>
 	<classpathentry kind="lib" path="lib/commons-httpclient-3.1.jar"/>
 	<classpathentry kind="lib" path="lib/commons-io-2.2.jar"/>
 	<classpathentry kind="lib" path="lib/commons-jexl-1.1.jar"/>
 	<classpathentry kind="lib" path="lib/commons-jexl-2.1.1.jar"/>
-	<classpathentry kind="lib" path="lib/commons-lang-2.6.jar"/>
+	<classpathentry kind="lib" path="lib/commons-lang3-3.1.jar"/>
 	<classpathentry kind="lib" path="lib/commons-logging-1.1.1.jar"/>
 	<classpathentry kind="lib" path="lib/commons-net-3.1.jar"/>
 	<classpathentry kind="lib" path="lib/excalibur-datasource-1.1.1.jar"/>
 	<classpathentry kind="lib" path="lib/excalibur-instrument-1.0.jar"/>
 	<classpathentry kind="lib" path="lib/excalibur-logger-1.1.jar"/>
 	<classpathentry kind="lib" path="lib/excalibur-pool-1.2.jar"/>
     <classpathentry kind="lib" path="lib/geronimo-jms_1.1_spec-1.1.1.jar"/>
 	<classpathentry kind="lib" path="lib/htmllexer-2.1.jar"/>
 	<classpathentry kind="lib" path="lib/htmlparser-2.1.jar"/>
     <classpathentry kind="lib" path="lib/httpclient-4.2.1.jar"/>
     <classpathentry kind="lib" path="lib/httpcore-4.2.1.jar"/>
     <classpathentry kind="lib" path="lib/httpmime-4.2.1.jar"/>
 	<classpathentry kind="lib" path="lib/jcharts-0.7.5.jar"/>
 	<classpathentry kind="lib" path="lib/jdom-1.1.2.jar"/>
 	<classpathentry kind="lib" path="lib/rhino-1.7R3.jar"/>
     <classpathentry kind="lib" path="lib/jtidy-r938.jar"/>
     <classpathentry kind="lib" path="lib/junit-4.10.jar"/>
 	<classpathentry kind="lib" path="lib/logkit-2.0.jar"/>
 	<classpathentry kind="lib" path="lib/mail-1.4.4.jar"/>
 	<classpathentry kind="lib" path="lib/oro-2.0.8.jar"/>
 	<classpathentry kind="lib" path="lib/serializer-2.7.1.jar"/>
 	<classpathentry kind="lib" path="lib/soap-2.3.1.jar"/>
 	<classpathentry kind="lib" path="lib/xalan-2.7.1.jar"/>
 	<classpathentry kind="lib" path="lib/xercesImpl-2.9.1.jar"/>
 	<classpathentry kind="lib" path="lib/xml-apis-1.3.04.jar"/>
 	<classpathentry kind="lib" path="lib/xmlgraphics-commons-1.3.1.jar"/>
     <classpathentry kind="lib" path="lib/xmlpull-1.1.3.1.jar"/>
 	<classpathentry kind="lib" path="lib/xpp3_min-1.1.4c.jar"/>
 	<classpathentry kind="lib" path="lib/xstream-1.4.2.jar"/>
     <!-- Needed for build and test -->
     <classpathentry kind="lib" path="lib/api/bcmail-jdk15-1.45.jar"/>
     <classpathentry kind="lib" path="lib/api/bcprov-jdk15-1.45.jar"/>
     <classpathentry kind="output" path="build"/>
 </classpath>
diff --git a/lib/aareadme.txt b/lib/aareadme.txt
index d927b3d58..5c7fa24c6 100644
--- a/lib/aareadme.txt
+++ b/lib/aareadme.txt
@@ -1,194 +1,198 @@
 Directories
 ===========
 lib - utility jars
 lib/api - Directory where API spec libraries live.
 lib/doc - jars needed for generating documentation. Not included with JMeter releases.
 lib/ext - JMeter jars only
 lib/junit - test jar for JUnit sampler
 lib/opt - Directory where Optional 3rd party libraries live
 lib/src - storage area for source and javadoc jars, e.g. for use in IDEs
           Excluded from SVN, not included in classpath
 
 Which jars are used by which modules?
 ====================================
 [not exhaustive]
 
 avalon-framework-4.1.4 (org.apache.avalon.framework)
 ----------------------
 - LogKit (LoggingManager)
 - Configuration (DataSourceElement)
 - OldSaveService
 
 bsf-2.4.0.jar (org.apache.bsf)
 -------------
 http://jakarta.apache.org/site/downloads/downloads_bsf.cgi
 - BSF test elements (sampler etc.)
 
 bsh-2.0b5.jar (org.bsh)
 -------------
 - BeanShell test elements
 
 commons-codec-1.6
 -----------------
 http://commons.apache.org/downloads/download_codec.cgi
 - used by commons-httpclient-3.1
 - also HtmlParserTester for Base64
 
 commons-collections-3.2.1
 -------------------------
 http://commons.apache.org/downloads/download_collections.cgi
 - ListenerNotifier
 - Anakia
 
 commons-httpclient-3.1
 ----------------------
 http://hc.apache.org/downloads.cgi
 - httpclient version of HTTP sampler
 - Cookie manager implementation
 
 commons-io-2.2
 --------------
 http://commons.apache.org/downloads/download_io.cgi
 - FTPSampler
 
 commons-jexl-1.1
 ----------------
 http://commons.apache.org/downloads/download_jexl.cgi
 - Jexl function and BSF test elements
 
 commons-lang-2.6
 ----------------
 http://commons.apache.org/downloads/download_lang.cgi
 - velocity (Anakia)
+
+commons-lang3-3.1
+----------------
+http://commons.apache.org/downloads/download_lang.cgi
 - URLCollection (unescapeXml)
 
 commons-logging-1.1.1
 ---------------------
 http://commons.apache.org/downloads/download_logging.cgi
 - httpclient
 
 commons-net-3.1
 -----------------
 http://commons.apache.org/downloads/download_net.cgi
 - FTPSampler
 
 excalibur-datasource-1.1.1 (org.apache.avalon.excalibur.datasource)
 --------------------------
 - DataSourceElement (JDBC)
 
 excalibur-instrument-1.0 (org.apache.excalibur.instrument)
 ------------------------
 - used by excalibur-datasource
 
 excalibur-logger-1.1 (org.apache.avalon.excalibur.logger)
 --------------------
 - LoggingManager
 
 excalibur-pool-1.2 (org.apache.avalon.excalibur.pool)
 ------------------
 - used by excalibur-datasource
 
 htmlparser-2.1
 htmllexer-2.1
 ----------------------
 http://htmlparser.sourceforge.net/
 - http: parsing html
 
 jCharts-0.7.5 (org.jCharts)
 -------------
 http://jcharts.sourceforge.net/downloads.html
 - AxisGraph,LineGraph,LineChart
 
 jdom-1.1.2
 --------
 http://www.jdom.org/downloads/index.html
 - XMLAssertion, JMeterTest ONLY
 - Anakia
 
 rhino-1.7R3
 --------
 http://www.mozilla.org/rhino/download.html
 - javascript function
 - BSF (Javascript)
 
 jTidy-r938
 ----
 - http: various modules for parsing html
 - org.xml.sax - various
 - XPathUtil (XPath assertion)
 
 junit 4.10
 -----------
 - unit tests, JUnit sampler
 
 HttpComponents (HttpComponents Core 4.x and HttpComponents Client 4.x)
 -----------
 http://hc.apache.org/
 - httpclient 4 implementation for HTTP sampler 
 
 logkit-2.0
 ----------
 - logging
 - Anakia
 
 oro-2.0.8
 ---------
 http://jakarta.apache.org/site/downloads/downloads_oro.cgi
 - regular expressions: various
 
 serialiser-2.7.1
 ----------------
 http://www.apache.org/dyn/closer.cgi/xml/xalan-j
 - xalan
 
 soap-2.3.1
 ----------
 - WebServiceSampler ONLY
 
 velocity-1.7
 --------------
 http://velocity.apache.org/download.cgi
 - Anakia (create documentation) Not used by JMeter runtime
 
 xalan_2.7.1
 -----------
 http://www.apache.org/dyn/closer.cgi/xml/xalan-j
 +org.apache.xalan|xml|xpath
 
 xercesimpl-2.9.1
 ----------------
 http://xerces.apache.org/xerces2-j/download.cgi
 +org.apache.html.dom|org.apache.wml|org.apache.xerces|org.apache.xml.serialize
 +org.w3c.dom.html|ls
 
 xml-apis-1.3.04
 --------------
 http://xerces.apache.org/xerces2-j/download.cgi
 +javax.xml
 +org.w3c.dom
 +org.xml.sax
 
 The x* jars above are used for XML handling
 
 xmlgraphics-commons-1.3.1 (org.apache.xmlgraphics.image.codec)
 ------------------
 http://xmlgraphics.apache.org/commons/download.html
 - SaveGraphicsService
 
 xmlpull-1.1.3.1
 ---------------
 http://www.xmlpull.org/impls.shtml
 - xstream
 
 
 xpp3_min-1.1.4c
 ---------------
 http://xstream.codehaus.org/download.html
 or
 http://www.extreme.indiana.edu/dist/java-repository/xpp3/distributions/
 - xstream
 
 xstream-1.4.2
 -------------
 http://xstream.codehaus.org/download.html
 - SaveService
\ No newline at end of file
diff --git a/res/maven/ApacheJMeter_parent.pom b/res/maven/ApacheJMeter_parent.pom
index dbe6c4eb8..0a805a84e 100644
--- a/res/maven/ApacheJMeter_parent.pom
+++ b/res/maven/ApacheJMeter_parent.pom
@@ -1,338 +1,338 @@
 <!--
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
   http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 -->
 <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     <modelVersion>4.0.0</modelVersion>
     <groupId>org.apache.jmeter</groupId>
     <artifactId>ApacheJMeter_parent</artifactId>
     <version>@MAVEN.DEPLOY.VERSION@</version>
     <name>Apache JMeter parent</name>
     <!-- Parent poms have to be packaged as pom, not jar -->
     <packaging>pom</packaging>
     
     <description>Apache JMeter is open source software, a 100% pure Java desktop application designed to load test
         functional behavior and measure performance. It was originally designed for testing Web Applications but has
         since expanded to other test functions.
     </description>
     <url>http://jmeter.apache.org/</url>
     <inceptionYear>1998</inceptionYear>
     <licenses>
         <license>
             <name>Apache 2</name>
             <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
             <distribution>repo</distribution>
             <comments>A business-friendly OSS license</comments>
         </license>
     </licenses>
     <issueManagement>
         <system>bugzilla</system>
         <url>https://issues.apache.org/bugzilla/describecomponents.cgi?product=JMeter</url>
     </issueManagement>
     <scm>
         <connection>http://svn.apache.org/repos/asf/jmeter/trunk/</connection>
         <developerConnection>https://svn.apache.org/repos/asf/jmeter/trunk/</developerConnection>
         <url>http://svn.apache.org/viewvc/jmeter/trunk/</url>
     </scm>
 
 
     <properties>
       <!-- these must agree with the definitions in build.properties -->
       <apache-bsf.version>2.4.0</apache-bsf.version>
       <apache-jsr223-api.version>3.1</apache-jsr223-api.version>
       <avalon-framework.version>4.1.4</avalon-framework.version>
       <beanshell.version>2.0b5</beanshell.version>
       <bcmail.version>1.45</bcmail.version>
       <bcprov.version>1.45</bcprov.version>
       <commons-codec.version>1.6</commons-codec.version>
       <commons-collections.version>3.2.1</commons-collections.version>
       <commons-httpclient.version>3.1</commons-httpclient.version>
       <commons-io.version>2.2</commons-io.version>
       <commons-jexl.version>1.1</commons-jexl.version>
       <commons-jexl2.version>2.1.1</commons-jexl2.version>
-      <commons-lang.version>2.6</commons-lang.version>
+      <commons-lang3.version>3.1</commons-lang3.version>
       <commons-logging.version>1.1.1</commons-logging.version>
       <commons-net.version>3.1</commons-net.version>
       <excalibur-datasource.version>1.1.1</excalibur-datasource.version>
       <excalibur-instrument.version>1.0</excalibur-instrument.version>
       <excalibur-logger.version>1.1</excalibur-logger.version>
       <excalibur-pool.version>1.2</excalibur-pool.version>
       <htmlparser.version>2.1</htmlparser.version>
       <httpclient.version>4.2</httpclient.version>
       <httpcore.version>4.2.1</httpcore.version>
       <jakarta-oro.version>2.0.8</jakarta-oro.version>
       <jcharts.version>0.7.5</jcharts.version>
       <jdom.version>1.1.2</jdom.version>
       <js_rhino.version>1.7R3</js_rhino.version>
       <junit.version>4.10</junit.version>
       <logkit.version>2.0</logkit.version>
       <soap.version>2.3.1</soap.version>
       <tidy.version>r938</tidy.version>
       <xmlpull.version>1.1.3.1</xmlpull.version>
       <xstream.version>1.4.2</xstream.version>
       <xpp3.version>1.1.4c</xpp3.version>
       <xalan.version>2.7.1</xalan.version>
       <serializer.version>2.7.1</serializer.version>
       <xerces.version>2.9.1</xerces.version>
       <xml-apis.version>1.3.04</xml-apis.version>
       <xmlgraphics-commons.version>1.3.1</xmlgraphics-commons.version>
       <activation.version>1.1.1</activation.version>
       <javamail.version>1.4.4</javamail.version>
       <jms.version>1.1.1</jms.version>
       <velocity.version>1.7</velocity.version>
     </properties>
 
     <dependencies>
       <dependency>
         <groupId>bsf</groupId>
         <artifactId>bsf</artifactId>
         <version>${apache-bsf.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.bsf</groupId>
         <artifactId>bsf-api</artifactId>
         <version>${apache-jsr223-api.version}</version>
       </dependency>
       <dependency>
         <groupId>avalon-framework</groupId>
         <artifactId>avalon-framework</artifactId>
         <version>${avalon-framework.version}</version>
       </dependency>
       <dependency>
         <groupId>org.beanshell</groupId>
         <artifactId>bsh</artifactId>
         <version>${beanshell.version}</version>
       </dependency>
       <dependency>
         <groupId>org.bouncycastle</groupId>
         <artifactId>bcmail-jdk15</artifactId>
         <version>${bcmail.version}</version>
       </dependency>
       <dependency>
         <groupId>org.bouncycastle</groupId>
         <artifactId>bcprov-jdk15</artifactId>
         <version>${bcprov.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-codec</groupId>
         <artifactId>commons-codec</artifactId>
         <version>${commons-codec.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-collections</groupId>
         <artifactId>commons-collections</artifactId>
         <version>${commons-collections.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-httpclient</groupId>
         <artifactId>commons-httpclient</artifactId>
         <version>${commons-httpclient.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-io</groupId>
         <artifactId>commons-io</artifactId>
         <version>${commons-io.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-jexl</groupId>
         <artifactId>commons-jexl</artifactId>
         <version>${commons-jexl.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.commons</groupId>
         <artifactId>commons-jexl</artifactId>
         <version>${commons-jexl2.version}</version>
       </dependency>
       <dependency>
-        <groupId>commons-lang</groupId>
-        <artifactId>commons-lang</artifactId>
-        <version>${commons-lang.version}</version>
+        <groupId>org.apache.commons-lang</groupId>
+        <artifactId>commons-lang3</artifactId>
+        <version>${commons-lang3.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-logging</groupId>
         <artifactId>commons-logging</artifactId>
         <version>${commons-logging.version}</version>
       </dependency>
       <dependency>
         <groupId>commons-net</groupId>
         <artifactId>commons-net</artifactId>
         <version>${commons-net.version}</version>
       </dependency>
       <dependency>
         <groupId>excalibur-datasource</groupId>
         <artifactId>excalibur-datasource</artifactId>
         <version>${excalibur-datasource.version}</version>
       </dependency>
       <dependency>
         <groupId>excalibur-instrument</groupId>
         <artifactId>excalibur-instrument</artifactId>
         <version>${excalibur-instrument.version}</version>
       </dependency>
       <dependency>
         <groupId>excalibur-logger</groupId>
         <artifactId>excalibur-logger</artifactId>
         <version>${excalibur-logger.version}</version>
       </dependency>
       <dependency>
         <groupId>excalibur-pool</groupId>
         <artifactId>excalibur-pool</artifactId>
         <version>${excalibur-pool.version}</version>
       </dependency>
       <dependency>
         <groupId>org.htmlparser</groupId>
         <artifactId>htmllexer</artifactId>
         <version>${htmlparser.version}</version>
       </dependency>
       <dependency>
         <groupId>org.htmlparser</groupId>
         <artifactId>htmlparser</artifactId>
         <version>${htmlparser.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.httpcomponents</groupId>
         <artifactId>httpclient</artifactId>
         <version>${httpclient.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.httpcomponents</groupId>
         <artifactId>httpmime</artifactId>
         <version>${httpclient.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.httpcomponents</groupId>
         <artifactId>httpcore</artifactId>
         <version>${httpcore.version}</version>
       </dependency>
       <dependency>
         <groupId>oro</groupId>
         <artifactId>oro</artifactId>
         <version>${jakarta-oro.version}</version>
       </dependency>
       <dependency>
         <groupId>jcharts</groupId>
         <artifactId>jcharts</artifactId>
         <version>0.7.5</version>
       </dependency>
       <dependency>
         <groupId>org.jdom</groupId>
         <artifactId>jdom</artifactId>
         <version>${jdom.version}</version>
         <!-- Jaxen brings in missing dependencies -->
         <exclusions>
           <exclusion>
             <artifactId>jaxen</artifactId>
             <groupId>jaxen</groupId>
           </exclusion>
         </exclusions>
       </dependency>
       <dependency>
         <groupId>org.mozilla</groupId>
         <artifactId>rhino</artifactId>
         <version>${js_rhino.version}</version>
       </dependency>
       <dependency>
         <groupId>junit</groupId>
         <artifactId>junit</artifactId>
         <version>${junit.version}</version>
       </dependency>
       <dependency>
         <groupId>logkit</groupId>
         <artifactId>logkit</artifactId>
         <version>${logkit.version}</version>
       </dependency>
       <dependency>
         <groupId>soap</groupId>
         <artifactId>soap</artifactId>
         <version>${soap.version}</version>
       </dependency>
       <dependency>
         <groupId>net.sf.jtidy</groupId>
         <artifactId>jtidy</artifactId>
         <version>${tidy.version}</version>
       </dependency>
       <dependency>
         <groupId>com.thoughtworks.xstream</groupId>
         <artifactId>xstream</artifactId>
         <version>${xstream.version}</version>
       </dependency>
       <dependency>
         <groupId>xmlpull</groupId>
         <artifactId>xmlpull</artifactId>
         <version>${xmlpull.version}</version>
       </dependency>
       <dependency>
         <groupId>xpp3</groupId>
         <artifactId>xpp3_min</artifactId>
         <version>${xpp3.version}</version>
       </dependency>
       <dependency>
         <groupId>xalan</groupId>
         <artifactId>xalan</artifactId>
         <version>${xalan.version}</version>
       </dependency>
       <dependency>
         <groupId>xalan</groupId>
         <artifactId>serializer</artifactId>
         <version>${serializer.version}</version>
       </dependency>
       <dependency>
         <groupId>xerces</groupId>
         <artifactId>xercesImpl</artifactId>
         <version>${xerces.version}</version>
       </dependency>
       <dependency>
         <groupId>xml-apis</groupId>
         <artifactId>xml-apis</artifactId>
         <version>${xml-apis.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.xmlgraphics</groupId>
         <artifactId>xmlgraphics-commons</artifactId>
         <version>${xmlgraphics-commons.version}</version>
       </dependency>
       <dependency>
         <groupId>javax.activation</groupId>
         <artifactId>activation</artifactId>
         <version>${activation.version}</version>
       </dependency>
       <dependency>
         <groupId>javax.mail</groupId>
         <artifactId>mail</artifactId>
         <version>${javamail.version}</version>
       </dependency>
       <dependency>
         <groupId>org.apache.geronimo.specs</groupId>
         <artifactId>geronimo-jms_1.1_spec</artifactId>
         <version>${jms.version}</version>
       </dependency>
       <!-- Docs only; not needed for source or binary archives 
       <dependency>
         <groupId>org.apache.velocity</groupId>
         <artifactId>velocity</artifactId>
         <version>${velocity.version}</version>
       </dependency>
        -->
     </dependencies>
 
     <repositories>
         <repository>
             <!--Required for bsh 2.0b5 -->
             <id>JBoss</id>
             <url>https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/</url>
         </repository>
     </repositories>
 </project>
diff --git a/src/components/org/apache/jmeter/config/KeystoreConfig.java b/src/components/org/apache/jmeter/config/KeystoreConfig.java
index ae1683c8b..f4e10f696 100644
--- a/src/components/org/apache/jmeter/config/KeystoreConfig.java
+++ b/src/components/org/apache/jmeter/config/KeystoreConfig.java
@@ -1,137 +1,137 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.config;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.log.Logger;
 
 /**
  * Configure Keystore
  */
 public class KeystoreConfig extends ConfigTestElement implements TestBean, TestStateListener {
 
     private static final long serialVersionUID = -5781402012242794890L;
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String KEY_STORE_START_INDEX = "https.keyStoreStartIndex"; // $NON-NLS-1$
     private static final String KEY_STORE_END_INDEX   = "https.keyStoreEndIndex"; // $NON-NLS-1$
 
     private String startIndex;
     private String endIndex;
     private String preload;
     
     public KeystoreConfig() {
         super();
     }
 
     public void testEnded() {
         testEnded(null);
     }
 
     public void testEnded(String host) {
         log.info("Destroying Keystore");         
         SSLManager.getInstance().destroyKeystore();
     }
 
     public void testStarted() {
         testStarted(null);
     }
 
     public void testStarted(String host) {
         String reuseSSLContext = JMeterUtils.getProperty("https.use.cached.ssl.context");
         if(StringUtils.isEmpty(reuseSSLContext)||"true".equals(reuseSSLContext)) {
             log.warn("https.use.cached.ssl.context property must be set to false to ensure Multiple Certificates are used");
         }
         int startIndexAsInt = JMeterUtils.getPropDefault(KEY_STORE_START_INDEX, 0);
         int endIndexAsInt = JMeterUtils.getPropDefault(KEY_STORE_END_INDEX, 0);
         
         if(!StringUtils.isEmpty(this.startIndex)) {
         	try {
         		startIndexAsInt = Integer.parseInt(this.startIndex);
         	} catch(NumberFormatException e) {
         		log.warn("Failed parsing startIndex :'"+this.startIndex+"', will default to:'"+startIndexAsInt+"', error message:"+ e.getMessage(), e);
         	}
         } 
         
         if(!StringUtils.isEmpty(this.endIndex)) {
         	try {
         		endIndexAsInt = Integer.parseInt(this.endIndex);
         	} catch(NumberFormatException e) {
         		log.warn("Failed parsing endIndex :'"+this.endIndex+"', will default to:'"+endIndexAsInt+"', error message:"+ e.getMessage(), e);
         	}
         } 
         if(startIndexAsInt>endIndexAsInt) {
         	throw new JMeterStopTestException("Keystore Config error : Alias start index must be lower than Alias end index");
         }
         log.info("Configuring Keystore with (preload:"+preload+", startIndex:"+
                 startIndexAsInt+", endIndex:"+endIndexAsInt+")");
 
         SSLManager.getInstance().configureKeystore(Boolean.parseBoolean(preload),
         		startIndexAsInt, 
                 endIndexAsInt);
     }
 
     /**
      * @return the endIndex
      */
     public String getEndIndex() {
         return endIndex;
     }
 
     /**
      * @param endIndex the endIndex to set
      */
     public void setEndIndex(String endIndex) {
         this.endIndex = endIndex;
     }
 
     /**
      * @return the startIndex
      */
     public String getStartIndex() {
         return startIndex;
     }
 
     /**
      * @param startIndex the startIndex to set
      */
     public void setStartIndex(String startIndex) {
         this.startIndex = startIndex;
     }
 
     /**
      * @return the preload
      */
     public String getPreload() {
         return preload;
     }
 
     /**
      * @param preload the preload to set
      */
     public void setPreload(String preload) {
         this.preload = preload;
     }
 }
diff --git a/src/components/org/apache/jmeter/config/RandomVariableConfig.java b/src/components/org/apache/jmeter/config/RandomVariableConfig.java
index 0812862fa..0a8b78095 100644
--- a/src/components/org/apache/jmeter/config/RandomVariableConfig.java
+++ b/src/components/org/apache/jmeter/config/RandomVariableConfig.java
@@ -1,239 +1,239 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.config;
 
 import java.text.DecimalFormat;
 import java.util.Random;
 
-import org.apache.commons.lang.math.NumberUtils;
+import org.apache.commons.lang3.math.NumberUtils;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.NoConfigMerge;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class RandomVariableConfig extends ConfigTestElement
     implements TestBean, LoopIterationListener, NoThreadClone, NoConfigMerge
 {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 233L;
 
     private String minimumValue;
 
     private String maximumValue;
 
     private String variableName;
 
     private String outputFormat;
 
     private String randomSeed;
 
     private boolean perThread;
 
     // This class is not cloned per thread, so this is shared
     private Random globalRandom = null;
 
     // Used for per-thread/user numbers
     // Cannot be static, as random numbers are not to be shared between instances
     private transient ThreadLocal<Random> perThreadRandom = initThreadLocal();
 
     private ThreadLocal<Random> initThreadLocal() {
         return new ThreadLocal<Random>() {
                 @Override
                 protected Random initialValue() {
                     init();
                     return new Random(getRandomSeedAsLong());
                 }};
     }
 
     private int n;
     private long minimum;
 
     private Object readResolve(){
         perThreadRandom = initThreadLocal();
         return this;
     }
 
     /*
      * nextInt(n) returns values in the range [0,n),
      * so n must be set to max-min+1
      */
     private void init(){
         final String minAsString = getMinimumValue();
         minimum = NumberUtils.toLong(minAsString);
         final String maxAsString = getMaximumValue();
         long maximum = NumberUtils.toLong(maxAsString);
         long rangeL=maximum-minimum+1; // This can overflow
         if (minimum >= maximum){
             log.error("maximum("+maxAsString+") must be > minimum"+minAsString+")");
             n=0;// This is used as an error indicator
             return;
         }
         if (rangeL > Integer.MAX_VALUE || rangeL <= 0){// check for overflow too
             log.warn("maximum("+maxAsString+") - minimum"+minAsString+") must be <="+Integer.MAX_VALUE);
             rangeL=Integer.MAX_VALUE;
         }
         n = (int)rangeL;
     }
 
     /** {@inheritDoc} */
     public void iterationStart(LoopIterationEvent iterEvent) {
         Random randGen=null;
         if (getPerThread()){
             randGen = perThreadRandom.get();
         } else {
             synchronized(this){
                 if (globalRandom == null){
                     init();
                     globalRandom = new Random(getRandomSeedAsLong());
                 }
                 randGen=globalRandom;
             }
         }
         if (n <=0){
             return;
         }
        long nextRand = minimum + randGen.nextInt(n);
        // Cannot use getThreadContext() as we are not cloned per thread
        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        variables.put(getVariableName(), formatNumber(nextRand));
     }
 
     // Use format to create number; if it fails, use the default
     private String formatNumber(long value){
         String format = getOutputFormat();
         if (format != null && format.length() > 0) {
             try {
                 DecimalFormat myFormatter = new DecimalFormat(format);
                 return myFormatter.format(value);
             } catch (NumberFormatException ignored) {
             	log.warn("Exception formatting value:"+value + " at format:"+format+", using default");
             } catch (IllegalArgumentException ignored) {
             	log.warn("Exception formatting value:"+value + " at format:"+format+", using default");
             }
         }
         return Long.toString(value);
     }
 
     /**
      * @return the minValue
      */
     public synchronized String getMinimumValue() {
         return minimumValue;
     }
 
     /**
      * @param minValue the minValue to set
      */
     public synchronized void setMinimumValue(String minValue) {
         this.minimumValue = minValue;
     }
 
     /**
      * @return the maxvalue
      */
     public synchronized String getMaximumValue() {
         return maximumValue;
     }
 
     /**
      * @param maxvalue the maxvalue to set
      */
     public synchronized void setMaximumValue(String maxvalue) {
         this.maximumValue = maxvalue;
     }
 
     /**
      * @return the variableName
      */
     public synchronized String getVariableName() {
         return variableName;
     }
 
     /**
      * @param variableName the variableName to set
      */
     public synchronized void setVariableName(String variableName) {
         this.variableName = variableName;
     }
 
     /**
      * @return the randomSeed
      */
     public synchronized String getRandomSeed() {
         return randomSeed;
     }
 
     /**
      * @return the randomSeed as a long
      */
     private synchronized long getRandomSeedAsLong() {
         long seed = 0;
         if (randomSeed.length()==0){
             seed = System.currentTimeMillis();
         }  else {
             try {
                 seed = Long.parseLong(randomSeed);
             } catch (NumberFormatException e) {
                 seed = System.currentTimeMillis();
                 log.warn("Cannot parse seed "+e.getLocalizedMessage());
             }
         }
         return seed;
     }
 
     /**
      * @param randomSeed the randomSeed to set
      */
     public synchronized void setRandomSeed(String randomSeed) {
         this.randomSeed = randomSeed;
     }
 
     /**
      * @return the perThread
      */
     public synchronized boolean getPerThread() {
         return perThread;
     }
 
     /**
      * @param perThread the perThread to set
      */
     public synchronized void setPerThread(boolean perThread) {
         this.perThread = perThread;
     }
     /**
      * @return the outputFormat
      */
     public synchronized String getOutputFormat() {
         return outputFormat;
     }
     /**
      * @param outputFormat the outputFormat to set
      */
     public synchronized void setOutputFormat(String outputFormat) {
         this.outputFormat = outputFormat;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index 37e2b3106..0a62add5d 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,468 +1,468 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.extractor;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcher;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 // @see org.apache.jmeter.extractor.TestRegexExtractor for unit tests
 
 public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // What to match against. N.B. do not change the string value or test plans will break!
     private static final String MATCH_AGAINST = "RegexExtractor.useHeaders"; // $NON-NLS-1$
     /*
      * Permissible values:
      *  true - match against headers
      *  false or absent - match against body (this was the original default)
      *  URL - match against URL
      *  These are passed to the setUseField() method
      *
      *  Do not change these values!
     */
     public static final String USE_HDRS = "true"; // $NON-NLS-1$
     public static final String USE_BODY = "false"; // $NON-NLS-1$
     public static final String USE_BODY_UNESCAPED = "unescaped"; // $NON-NLS-1$
     public static final String USE_URL = "URL"; // $NON-NLS-1$
     public static final String USE_CODE = "code"; // $NON-NLS-1$
     public static final String USE_MESSAGE = "message"; // $NON-NLS-1$
 
 
     private static final String REGEX = "RegexExtractor.regex"; // $NON-NLS-1$
 
     private static final String REFNAME = "RegexExtractor.refname"; // $NON-NLS-1$
 
     private static final String MATCH_NUMBER = "RegexExtractor.match_number"; // $NON-NLS-1$
 
     private static final String DEFAULT = "RegexExtractor.default"; // $NON-NLS-1$
 
     private static final String TEMPLATE = "RegexExtractor.template"; // $NON-NLS-1$
 
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
 
     private static final String UNDERSCORE = "_";  // $NON-NLS-1$
 
     private transient List<Object> template;
 
     /**
      * Parses the response data using regular expressions and saving the results
      * into variables for use later in the test.
      *
      * @see org.apache.jmeter.processor.PostProcessor#process()
      */
     public void process() {
         initTemplate();
         JMeterContext context = getThreadContext();
         SampleResult previousResult = context.getPreviousResult();
         if (previousResult == null) {
             return;
         }
         log.debug("RegexExtractor processing result");
 
         // Fetch some variables
         JMeterVariables vars = context.getVariables();
         String refName = getRefName();
         int matchNumber = getMatchNumber();
 
         final String defaultValue = getDefaultValue();
         if (defaultValue.length() > 0){// Only replace default if it is provided
             vars.put(refName, defaultValue);
         }
 
 
         String regex = getRegex();
         try {
             List<MatchResult> matches = processMatches(regex, previousResult, matchNumber, vars);
             int prevCount = 0;
             String prevString = vars.get(refName + REF_MATCH_NR);
             if (prevString != null) {
                 vars.remove(refName + REF_MATCH_NR);// ensure old value is not left defined
                 try {
                     prevCount = Integer.parseInt(prevString);
                 } catch (NumberFormatException e1) {
                     log.warn("Could not parse "+prevString+" "+e1);
                 }
             }
             int matchCount=0;// Number of refName_n variable sets to keep
             try {
                 MatchResult match;
                 if (matchNumber >= 0) {// Original match behaviour
                     match = getCorrectMatch(matches, matchNumber);
                     if (match != null) {
                         vars.put(refName, generateResult(match));
                         saveGroups(vars, refName, match);
                     } else {
                         // refname has already been set to the default (if present)
                         removeGroups(vars, refName);
                     }
                 } else // < 0 means we save all the matches
                 {
                     removeGroups(vars, refName); // remove any single matches
                     matchCount = matches.size();
                     vars.put(refName + REF_MATCH_NR, Integer.toString(matchCount));// Save the count
                     for (int i = 1; i <= matchCount; i++) {
                         match = getCorrectMatch(matches, i);
                         if (match != null) {
                             final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                             vars.put(refName_n, generateResult(match));
                             saveGroups(vars, refName_n, match);
                         }
                     }
                 }
                 // Remove any left-over variables
                 for (int i = matchCount + 1; i <= prevCount; i++) {
                     final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                     vars.remove(refName_n);
                     removeGroups(vars, refName_n);
                 }
             } catch (RuntimeException e) {
                 log.warn("Error while generating result");
             }
         } catch (MalformedCachePatternException e) {
             log.warn("Error in pattern: " + regex);
         }
     }
 
     private String getInputString(SampleResult result) {
         String inputString = useUrl() ? result.getUrlAsString() // Bug 39707
                 : useHeaders() ? result.getResponseHeaders()
                 : useCode() ? result.getResponseCode() // Bug 43451
                 : useMessage() ? result.getResponseMessage() // Bug 43451
-                : useUnescapedBody() ? StringEscapeUtils.unescapeHtml(result.getResponseDataAsString())
+                : useUnescapedBody() ? StringEscapeUtils.unescapeHtml4(result.getResponseDataAsString())
                 : result.getResponseDataAsString() // Bug 36898
                 ;
        if (log.isDebugEnabled()) {
            log.debug("Input = " + inputString);
        }
        return inputString;
     }
 
     private List<MatchResult> processMatches(String regex, SampleResult result, int matchNumber, JMeterVariables vars) {
         if (log.isDebugEnabled()) {
             log.debug("Regex = " + regex);
         }
 
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         Pattern pattern = JMeterUtils.getPatternCache().getPattern(regex, Perl5Compiler.READ_ONLY_MASK);
         List<MatchResult> matches = new ArrayList<MatchResult>();
         int found = 0;
 
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
             matchStrings(matchNumber, matcher, pattern, matches, found,
                     inputString);
         } else {
             List<SampleResult> sampleList = getSampleList(result);
             for (SampleResult sr : sampleList) {
                 String inputString = getInputString(sr);
                 found = matchStrings(matchNumber, matcher, pattern, matches, found,
                         inputString);
                 if (matchNumber > 0 && found == matchNumber){// no need to process further
                     break;
                 }
             }
         }
         return matches;
     }
 
     private int matchStrings(int matchNumber, Perl5Matcher matcher,
             Pattern pattern, List<MatchResult> matches, int found,
             String inputString) {
         PatternMatcherInput input = new PatternMatcherInput(inputString);
         while (matchNumber <=0 || found != matchNumber) {
             if (matcher.contains(input, pattern)) {
                 log.debug("RegexExtractor: Match found!");
                 matches.add(matcher.getMatch());
                 found++;
             } else {
                 break;
             }
         }
         return found;
     }
 
     /**
      * Creates the variables:<br/>
      * basename_gn, where n=0...# of groups<br/>
      * basename_g = number of groups (apart from g0)
      */
     private void saveGroups(JMeterVariables vars, String basename, MatchResult match) {
         StringBuilder buf = new StringBuilder();
         buf.append(basename);
         buf.append("_g"); // $NON-NLS-1$
         int pfxlen=buf.length();
         String prevString=vars.get(buf.toString());
         int previous=0;
         if (prevString!=null){
             try {
                 previous=Integer.parseInt(prevString);
             } catch (NumberFormatException e) {
                 log.warn("Could not parse "+prevString+" "+e);
             }
         }
         //Note: match.groups() includes group 0
         final int groups = match.groups();
         for (int x = 0; x < groups; x++) {
             buf.append(x);
             vars.put(buf.toString(), match.group(x));
             buf.setLength(pfxlen);
         }
         vars.put(buf.toString(), Integer.toString(groups-1));
         for (int i = groups; i <= previous; i++){
             buf.append(i);
             vars.remove(buf.toString());// remove the remaining _gn vars
             buf.setLength(pfxlen);
         }
     }
 
     /**
      * Removes the variables:<br/>
      * basename_gn, where n=0...# of groups<br/>
      * basename_g = number of groups (apart from g0)
      */
     private void removeGroups(JMeterVariables vars, String basename) {
         StringBuilder buf = new StringBuilder();
         buf.append(basename);
         buf.append("_g"); // $NON-NLS-1$
         int pfxlen=buf.length();
         // How many groups are there?
         int groups;
         try {
             groups=Integer.parseInt(vars.get(buf.toString()));
         } catch (NumberFormatException e) {
             groups=0;
         }
         vars.remove(buf.toString());// Remove the group count
         for (int i = 0; i <= groups; i++) {
             buf.append(i);
             vars.remove(buf.toString());// remove the g0,g1...gn vars
             buf.setLength(pfxlen);
         }
     }
 
     private String generateResult(MatchResult match) {
         StringBuilder result = new StringBuilder();
         for (Object obj : template) {
             if (log.isDebugEnabled()) {
                 log.debug("RegexExtractor: Template piece " + obj + " (" + obj.getClass().getSimpleName() + ")");
             }
             if (obj instanceof Integer) {
                 result.append(match.group(((Integer) obj).intValue()));
             } else {
                 result.append(obj);
             }
         }
         if (log.isDebugEnabled()) {
             log.debug("Regex Extractor result = " + result.toString());
         }
         return result.toString();
     }
 
     private void initTemplate() {
         if (template != null) {
             return;
         }
         // Contains Strings and Integers
         List<Object> combined = new ArrayList<Object>();
         String rawTemplate = getTemplate();
         PatternMatcher matcher = JMeterUtils.getMatcher();
         Pattern templatePattern = JMeterUtils.getPatternCache().getPattern("\\$(\\d+)\\$"  // $NON-NLS-1$
                 , Perl5Compiler.READ_ONLY_MASK
                 & Perl5Compiler.SINGLELINE_MASK);
         if (log.isDebugEnabled()) {
             log.debug("Pattern = " + templatePattern.getPattern());
             log.debug("template = " + rawTemplate);
         }
         int beginOffset = 0;
         MatchResult currentResult;
         PatternMatcherInput pinput = new PatternMatcherInput(rawTemplate);
         while(matcher.contains(pinput, templatePattern)) {
             currentResult = matcher.getMatch();
             final int beginMatch = currentResult.beginOffset(0);
             if (beginMatch > beginOffset) { // string is not empty
                 combined.add(rawTemplate.substring(beginOffset, beginMatch));
             }
             combined.add(Integer.valueOf(currentResult.group(1)));// add match as Integer
             beginOffset = currentResult.endOffset(0);
         }
 
         if (beginOffset < rawTemplate.length()) { // trailing string is not empty
             combined.add(rawTemplate.substring(beginOffset, rawTemplate.length()));
         }
         if (log.isDebugEnabled()){
             log.debug("Template item count: "+combined.size());
             for(Object o : combined){
                 log.debug(o.getClass().getSimpleName()+" '"+o.toString()+"'");
             }
         }
         template = combined;
     }
 
     /**
      * Grab the appropriate result from the list.
      *
      * @param matches
      *            list of matches
      * @param entry
      *            the entry number in the list
      * @return MatchResult
      */
     private MatchResult getCorrectMatch(List<MatchResult> matches, int entry) {
         int matchSize = matches.size();
 
         if (matchSize <= 0 || entry > matchSize){
             return null;
         }
 
         if (entry == 0) // Random match
         {
             return matches.get(JMeterUtils.getRandomInt(matchSize));
         }
 
         return matches.get(entry - 1);
     }
 
     public void setRegex(String regex) {
         setProperty(REGEX, regex);
     }
 
     public String getRegex() {
         return getPropertyAsString(REGEX);
     }
 
     public void setRefName(String refName) {
         setProperty(REFNAME, refName);
     }
 
     public String getRefName() {
         return getPropertyAsString(REFNAME);
     }
 
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or 0, which is interpreted as meaning random.
      *
      * @param matchNumber
      */
     public void setMatchNumber(int matchNumber) {
         setProperty(new IntegerProperty(MATCH_NUMBER, matchNumber));
     }
 
     public void setMatchNumber(String matchNumber) {
         setProperty(MATCH_NUMBER, matchNumber);
     }
 
     public int getMatchNumber() {
         return getPropertyAsInt(MATCH_NUMBER);
     }
 
     public String getMatchNumberAsString() {
         return getPropertyAsString(MATCH_NUMBER);
     }
 
     /**
      * Sets the value of the variable if no matches are found
      *
      * @param defaultValue
      */
     public void setDefaultValue(String defaultValue) {
         setProperty(DEFAULT, defaultValue);
     }
 
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
     }
 
     public void setTemplate(String template) {
         setProperty(TEMPLATE, template);
     }
 
     public String getTemplate() {
         return getPropertyAsString(TEMPLATE);
     }
 
     public boolean useHeaders() {
         return USE_HDRS.equalsIgnoreCase( getPropertyAsString(MATCH_AGAINST));
     }
 
     // Allow for property not yet being set (probably only applies to Test cases)
     public boolean useBody() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return prop.length()==0 || USE_BODY.equalsIgnoreCase(prop);// $NON-NLS-1$
     }
 
     public boolean useUnescapedBody() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_BODY_UNESCAPED.equalsIgnoreCase(prop);// $NON-NLS-1$
     }
 
     public boolean useUrl() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_URL.equalsIgnoreCase(prop);
     }
 
     public boolean useCode() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_CODE.equalsIgnoreCase(prop);
     }
 
     public boolean useMessage() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_MESSAGE.equalsIgnoreCase(prop);
     }
 
     public void setUseField(String actionCommand) {
         setProperty(MATCH_AGAINST,actionCommand);
     }
     
     /** 
      * {@inheritDoc}}
      */
     @Override
     public List<String> getSearchableTokens() throws Exception {
         List<String> result = super.getSearchableTokens();
         result.add(getRefName());
         result.add(getDefaultValue());
         result.add(getRegex());
         return result;
     }
 }
diff --git a/src/components/org/apache/jmeter/reporters/MailerModel.java b/src/components/org/apache/jmeter/reporters/MailerModel.java
index 5b24bbf8c..89ffc1566 100644
--- a/src/components/org/apache/jmeter/reporters/MailerModel.java
+++ b/src/components/org/apache/jmeter/reporters/MailerModel.java
@@ -1,526 +1,526 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.reporters;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import javax.mail.Authenticator;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.PasswordAuthentication;
 import javax.mail.Session;
 import javax.mail.Transport;
 import javax.mail.internet.AddressException;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeMessage;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The model for a MailerVisualizer.
  *
  */
 public class MailerModel extends AbstractTestElement implements Serializable {
     public static enum MailAuthType {
         SSL,
         TLS, 
         NONE;
     }
     
     private static final long serialVersionUID = 270L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String MAIL_SMTP_HOST = "mail.smtp.host"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_PORT = "mail.smtp.port"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_AUTH = "mail.smtp.auth"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class"; //$NON-NLS-1$
     
     private static final String MAIL_SMTP_STARTTLS = "mail.smtp.starttls.enable"; //$NON-NLS-1$
     
     private long failureCount = 0;
 
     private long successCount = 0;
 
     private boolean failureMsgSent = false;
 
     private boolean siteDown = false;
 
     private boolean successMsgSent = false;
 
     private static final String FROM_KEY = "MailerModel.fromAddress"; //$NON-NLS-1$
 
     private static final String TO_KEY = "MailerModel.addressie"; //$NON-NLS-1$
 
     private static final String HOST_KEY = "MailerModel.smtpHost"; //$NON-NLS-1$
 
     private static final String PORT_KEY = "MailerModel.smtpPort"; //$NON-NLS-1$
 
     private static final String SUCCESS_SUBJECT = "MailerModel.successSubject"; //$NON-NLS-1$
 
     private static final String FAILURE_SUBJECT = "MailerModel.failureSubject"; //$NON-NLS-1$
 
     private static final String FAILURE_LIMIT_KEY = "MailerModel.failureLimit"; //$NON-NLS-1$
 
     private static final String SUCCESS_LIMIT_KEY = "MailerModel.successLimit"; //$NON-NLS-1$
 
     private static final String LOGIN = "MailerModel.login"; //$NON-NLS-1$
 
     private static final String PASSWORD = "MailerModel.password"; //$NON-NLS-1$
 
     private static final String MAIL_AUTH_TYPE = "MailerModel.authType"; //$NON-NLS-1$
 
     private static final String DEFAULT_LIMIT = "2"; //$NON-NLS-1$
 
     private static final String DEFAULT_SMTP_PORT = "25";
 
     private static final String DEFAULT_PASSWORD_VALUE = ""; //$NON-NLS-1$
 
     private static final String DEFAULT_MAIL_AUTH_TYPE_VALUE = MailAuthType.NONE.toString(); //$NON-NLS-1$
 
     private static final String DEFAULT_LOGIN_VALUE = ""; //$NON-NLS-1$
 
     /** The listener for changes. */
     private transient ChangeListener changeListener;
 
     /**
      * Constructs a MailerModel.
      */
     public MailerModel() {
         super();
 
         setProperty(SUCCESS_LIMIT_KEY, JMeterUtils.getPropDefault("mailer.successlimit", DEFAULT_LIMIT)); //$NON-NLS-1$
         setProperty(FAILURE_LIMIT_KEY, JMeterUtils.getPropDefault("mailer.failurelimit", DEFAULT_LIMIT)); //$NON-NLS-1$
     }
 
     public void addChangeListener(ChangeListener list) {
         changeListener = list;
     }
 
     /** {@inheritDoc} */
     @Override
     public Object clone() {
         MailerModel m = (MailerModel) super.clone();
         m.changeListener = changeListener;
         return m;
     }
 
     public void notifyChangeListeners() {
         if (changeListener != null) {
             changeListener.stateChanged(new ChangeEvent(this));
         }
     }
 
     /**
      * Gets a List of String-objects. Each String is one mail-address of the
      * addresses-String set by <code>setToAddress(str)</code>. The addresses
      * must be seperated by commas. Only String-objects containing a "@" are
      * added to the returned List.
      *
      * @return a List of String-objects wherein each String represents a
      *         mail-address.
      */
     public List<String> getAddressList() {
         String addressees = getToAddress();
         List<String> addressList = new ArrayList<String>();
 
         if (addressees != null) {
 
             StringTokenizer next = new StringTokenizer(addressees, ","); //$NON-NLS-1$
 
             while (next.hasMoreTokens()) {
                 String theToken = next.nextToken().trim();
 
                 if (theToken.indexOf("@") > 0) { //$NON-NLS-1$
                 	addressList.add(theToken);
                 } else {
                     log.warn("Ignored unexpected e-mail address: "+theToken);
                 }
             }
         }
 
         return addressList;
     }
 
     /**
      * Adds a SampleResult for display in the Visualizer.
      *
      * @param sample
      *            the SampleResult encapsulating informations about the last
      *            sample.
      */
     public void add(SampleResult sample) {
         add(sample, false);
     }
 
     /**
      * Adds a SampleResult. If SampleResult represents a change concerning the
      * failure/success of the sampling a message might be sent to the addressies
      * according to the settings of <code>successCount</code> and
      * <code>failureCount</code>.
      *
      * @param sample
      *            the SampleResult encapsulating information about the last
      *            sample.
      * @param sendMails whether or not to send e-mails
      */
     public synchronized void add(SampleResult sample, boolean sendMails) {
 
         // -1 is the code for a failed sample.
         //
         if (!sample.isSuccessful()) {
             failureCount++;
             successCount = 0;
         } else {
             successCount++;
         }
 
         if (sendMails && (failureCount > getFailureLimit()) && !siteDown && !failureMsgSent) {
             // Send the mail ...
             List<String> addressList = getAddressList();
 
             if (addressList.size() != 0) {
                 try {
                     sendMail(getFromAddress(), addressList, getFailureSubject(), "URL Failed: "
                             + sample.getSampleLabel(), getSmtpHost(),
                             getSmtpPort(), getLogin(), getPassword(),
                             getMailAuthType(), false);
                 } catch (Exception e) {
                     log.error("Problem sending mail: "+e);
                 }
                 siteDown = true;
                 failureMsgSent = true;
                 successCount = 0;
                 successMsgSent = false;
             }
         }
 
         if (sendMails && siteDown && (sample.getTime() != -1) && !successMsgSent) {
             // Send the mail ...
             if (successCount > getSuccessLimit()) {
                 List<String> addressList = getAddressList();
 
                 try {
                     sendMail(getFromAddress(), addressList, getSuccessSubject(), "URL Restarted: "
                             + sample.getSampleLabel(), getSmtpHost(),
                             getSmtpPort(), getLogin(), getPassword(),
                             getMailAuthType(), false);
                 } catch (Exception e) {
                     log.error("Problem sending mail", e);
                 }
                 siteDown = false;
                 successMsgSent = true;
                 failureCount = 0;
                 failureMsgSent = false;
             }
         }
 
         if (successMsgSent && failureMsgSent) {
             clear();
         }
         notifyChangeListeners();
     }
 
     
 
     /**
      * Resets the state of this object to its default. But: This method does not
      * reset any mail-specific attributes (like sender, mail-subject...) since
      * they are independent of the sampling.
      */
     @Override
     public synchronized void clear() {// TODO: should this be clearData()?
         failureCount = 0;
         successCount = 0;
         siteDown = false;
         successMsgSent = false;
         failureMsgSent = false;
         notifyChangeListeners();
     }
 
     /**
      * Returns a String-representation of this object. Returns always
      * "E-Mail-Notification". Might be enhanced in future versions to return
      * some kind of String-representation of the mail-parameters (like sender,
      * addressies, smtpHost...).
      *
      * @return A String-representation of this object.
      */
     @Override
     public String toString() {
         return "E-Mail Notification";
     }
 
     /**
      * Sends a mail with the given parameters using SMTP.
      *
      * @param from
      *            the sender of the mail as shown in the mail-client.
      * @param vEmails
      *            all receivers of the mail. The receivers are seperated by
      *            commas.
      * @param subject
      *            the subject of the mail.
      * @param attText
      *            the message-body.
      * @param smtpHost
      *            the smtp-server used to send the mail.
      * @throws MessagingException 
      * @throws AddressException 
      */
     public void sendMail(String from, List<String> vEmails, String subject, String attText, String smtpHost) 
             throws AddressException, MessagingException {
         sendMail(from, vEmails, subject, attText, smtpHost, DEFAULT_SMTP_PORT, null, null, null, false);   
     }
     
     /**
      * Sends a mail with the given parameters using SMTP.
      *
      * @param from
      *            the sender of the mail as shown in the mail-client.
      * @param vEmails
      *            all receivers of the mail. The receivers are seperated by
      *            commas.
      * @param subject
      *            the subject of the mail.
      * @param attText
      *            the message-body.
      * @param smtpHost
      *            the smtp-server used to send the mail.
      * @param smtpPort the smtp-server port used to send the mail.
      * @param user the login used to authenticate
      * @param password the password used to authenticate
      * @param mailAuthType {@link MailAuthType} Security policy
      * @throws AddressException If mail address is wrong
      * @throws MessagingException If building MimeMessage fails
      */
     public void sendMail(String from, List<String> vEmails, String subject,
             String attText, String smtpHost, 
             String smtpPort,
             final String user,
             final String password,
             MailAuthType mailAuthType,
             boolean debug)
             throws AddressException, MessagingException{
 
         InternetAddress[] address = new InternetAddress[vEmails.size()];
 
         for (int k = 0; k < vEmails.size(); k++) {
             address[k] = new InternetAddress(vEmails.get(k));
         }
 
         // create some properties and get the default Session
         Properties props = new Properties();
 
         props.put(MAIL_SMTP_HOST, smtpHost);
         props.put(MAIL_SMTP_PORT, smtpPort); // property values are strings
         Authenticator authenticator = null;
         if(mailAuthType != MailAuthType.NONE) {
             props.put(MAIL_SMTP_AUTH, "true");
             switch (mailAuthType) {
                 case SSL:
                     props.put(MAIL_SMTP_SOCKETFACTORY_CLASS, 
                             "javax.net.ssl.SSLSocketFactory");
                     break;
                 case TLS:
                     props.put(MAIL_SMTP_STARTTLS, 
                             "true");
                     break;
     
                 default:
                     break;
                 }
         }
         
         if(!StringUtils.isEmpty(user)) {
             authenticator = 
                     new javax.mail.Authenticator() {
                         @Override
                         protected PasswordAuthentication getPasswordAuthentication() {
                             return new PasswordAuthentication(user,password);
                         }
                     };
         }
         Session session = Session.getInstance(props, authenticator);
         session.setDebug(debug);
 
         // create a message
         Message msg = new MimeMessage(session);
 
         msg.setFrom(new InternetAddress(from));
         msg.setRecipients(Message.RecipientType.TO, address);
         msg.setSubject(subject);
         msg.setText(attText);
         Transport.send(msg);
     }
 
     /**
      * Send a Test Mail to check configuration
      * @throws AddressException If mail address is wrong
      * @throws MessagingException If building MimeMessage fails
      */
     public synchronized void sendTestMail() throws AddressException, MessagingException {
         String to = getToAddress();
         String from = getFromAddress();
         String subject = "Testing mail-addresses";
         String smtpHost = getSmtpHost();
         String attText = "JMeter-Testmail" + "\n" + "To:  " + to + "\n" + "From: " + from + "\n" + "Via:  " + smtpHost
                 + "\n" + "Fail Subject:  " + getFailureSubject() + "\n" + "Success Subject:  " + getSuccessSubject();
 
         log.info(attText);
 
         sendMail(from, getAddressList(), subject, attText, smtpHost,
                 getSmtpPort(), 
                 getLogin(), 
                 getPassword(),
                 getMailAuthType(),
                 true);
         log.info("Test mail sent successfully!!");
     }
 
     // ////////////////////////////////////////////////////////////
     //
     // setter/getter - JavaDoc-Comments not needed...
     //
     // ////////////////////////////////////////////////////////////
 
     public void setToAddress(String str) {
         setProperty(TO_KEY, str);
     }
 
     public void setFromAddress(String str) {
         setProperty(FROM_KEY, str);
     }
 
     public void setSmtpHost(String str) {
         setProperty(HOST_KEY, str);
     }
 
     public void setSmtpPort(String value) {
         if(StringUtils.isEmpty(value)) {
             value = DEFAULT_SMTP_PORT;
         }
         setProperty(PORT_KEY, value, DEFAULT_SMTP_PORT);
     }
     
     public void setLogin(String login) {
         setProperty(LOGIN, login, DEFAULT_LOGIN_VALUE);
     }
     
     public void setPassword(String password) {
         setProperty(PASSWORD, password, DEFAULT_PASSWORD_VALUE);
     }
     
     public void setMailAuthType(String value) {
         setProperty(MAIL_AUTH_TYPE, value, DEFAULT_MAIL_AUTH_TYPE_VALUE);
     }
     
     public void setFailureSubject(String str) {
         setProperty(FAILURE_SUBJECT, str);
     }
 
     public void setSuccessSubject(String str) {
         setProperty(SUCCESS_SUBJECT, str);
     }
 
     public void setSuccessLimit(String limit) {
         setProperty(SUCCESS_LIMIT_KEY, limit);
     }
 
     // private void setSuccessCount(long count)
     // {
     // this.successCount = count;
     // }
 
     public void setFailureLimit(String limit) {
         setProperty(FAILURE_LIMIT_KEY, limit);
     }
 
     // private void setFailureCount(long count)
     // {
     // this.failureCount = count;
     // }
 
     public String getToAddress() {
         return getPropertyAsString(TO_KEY);
     }
 
     public String getFromAddress() {
         return getPropertyAsString(FROM_KEY);
     }
 
     public String getSmtpHost() {
         return getPropertyAsString(HOST_KEY);
     }
 
     public String getSmtpPort() {
         return getPropertyAsString(PORT_KEY, DEFAULT_SMTP_PORT);
     }
 
     public String getFailureSubject() {
         return getPropertyAsString(FAILURE_SUBJECT);
     }
 
     public String getSuccessSubject() {
         return getPropertyAsString(SUCCESS_SUBJECT);
     }
 
     public long getSuccessLimit() {
         return getPropertyAsLong(SUCCESS_LIMIT_KEY);
     }
 
     public long getSuccessCount() {
         return successCount;
     }
 
     public long getFailureLimit() {
         return getPropertyAsLong(FAILURE_LIMIT_KEY);
     }
 
     public long getFailureCount() {
         return this.failureCount;
     }
 
     public String getLogin() {
         return getPropertyAsString(LOGIN, DEFAULT_LOGIN_VALUE);
     }
 
     public String getPassword() {
         return getPropertyAsString(PASSWORD, DEFAULT_PASSWORD_VALUE);
     }
 
     public MailAuthType getMailAuthType() {
         String authType = getPropertyAsString(MAIL_AUTH_TYPE, DEFAULT_MAIL_AUTH_TYPE_VALUE);
         return MailAuthType.valueOf(authType);
     }
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/config/gui/ArgumentsPanel.java b/src/core/org/apache/jmeter/config/gui/ArgumentsPanel.java
index 95cd9122d..55241b3ee 100644
--- a/src/core/org/apache/jmeter/config/gui/ArgumentsPanel.java
+++ b/src/core/org/apache/jmeter/config/gui/ArgumentsPanel.java
@@ -1,702 +1,702 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.FlowLayout;
 import java.awt.Toolkit;
 import java.awt.datatransfer.Clipboard;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Iterator;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.TableCellEditor;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * A GUI panel allowing the user to enter name-value argument pairs. These
  * arguments (or parameters) are usually used to provide configuration values
  * for some other component.
  *
  */
 public class ArgumentsPanel extends AbstractConfigGui implements ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     /** The title label for this component. */
     private JLabel tableLabel;
 
     /** The table containing the list of arguments. */
     private transient JTable table;
 
     /** The model for the arguments table. */
     protected transient ObjectTableModel tableModel; // will only contain Argument or HTTPArgument
 
     /** A button for adding new arguments to the table. */
     private JButton add;
 
     /** A button for adding new arguments to the table from the clipboard. */
     private JButton addFromClipboard;
 
     /** A button for removing arguments from the table. */
     private JButton delete;
 
     /**
      * Added background support for reporting tool
      */
     private Color background;
 
     /**
      * Boolean indicating whether this component is a standalone component or it
      * is intended to be used as a subpanel for another component.
      */
     private final boolean standalone;
 
     /** Button to move a argument up*/
     private JButton up;
 
     /** Button to move a argument down*/
     private JButton down;
 
     private final boolean enableUpDown;
 
     private JButton showDetail;
 
     /** Command for adding a row to the table. */
     private static final String ADD = "add"; // $NON-NLS-1$
 
     /** Command for adding rows from the clipboard */
     private static final String ADD_FROM_CLIPBOARD = "addFromClipboard"; // $NON-NLS-1$
 
     /** Command for removing a row from the table. */
     private static final String DELETE = "delete"; // $NON-NLS-1$
 
     /** Command for moving a row up in the table. */
     private static final String UP = "up"; // $NON-NLS-1$
 
     /** Command for moving a row down in the table. */
     private static final String DOWN = "down"; // $NON-NLS-1$
 
     /** Command for showing detail. */
     private static final String DETAIL = "detail"; // $NON-NLS-1$
 
     public static final String COLUMN_RESOURCE_NAMES_0 = "name"; // $NON-NLS-1$
 
     public static final String COLUMN_RESOURCE_NAMES_1 = "value"; // $NON-NLS-1$
 
     public static final String COLUMN_RESOURCE_NAMES_2 = "description"; // $NON-NLS-1$
 
     /**
      * Create a new ArgumentsPanel as a standalone component.
      */
     public ArgumentsPanel() {
         this(JMeterUtils.getResString("user_defined_variables"),null, true, true);// $NON-NLS-1$
     }
 
     /**
      * Create a new ArgumentsPanel as an embedded component, using the specified
      * title.
      *
      * @param label
      *            the title for the component.
      */
     public ArgumentsPanel(String label) {
         this(label, null, true, false);
     }
     
     /**
      * Create a new ArgumentsPanel as an embedded component, using the specified
      * title.
      *
      * @param label
      *            the title for the component.
      * @param enableUpDown Add up/down buttons
      */
     public ArgumentsPanel(String label, boolean enableUpDown) {
         this(label, null, enableUpDown, false);
     }
 
     /**
      * Create a new ArgumentsPanel with a border and color background
      * @param label text for label
      * @param bkg background colour
      */
     public ArgumentsPanel(String label, Color bkg) {
         this(label, bkg, true, false);
     }
     
     /**
      * Create a new ArgumentsPanel with a border and color background
      * @param label text for label
      * @param bkg background colour
      * @param enableUpDown Add up/down buttons
      * @param standalone is standalone
      */
     public ArgumentsPanel(String label, Color bkg, boolean enableUpDown, boolean standalone) {
         this(label, bkg, enableUpDown, standalone, null);
     }
        
     /**
      * Create a new ArgumentsPanel with a border and color background
      * @param label text for label
      * @param bkg background colour
      * @param enableUpDown Add up/down buttons
      * @param standalone is standalone
      * @param model the table model to use
      */
     public ArgumentsPanel(String label, Color bkg, boolean enableUpDown, boolean standalone, ObjectTableModel model) {
         tableLabel = new JLabel(label);
         this.enableUpDown = enableUpDown;
         this.background = bkg;
         this.standalone = standalone;
         this.tableModel = model;
         init();
     }
 
     /**
      * This is the list of menu categories this gui component will be available
      * under.
      *
      * @return a Collection of Strings, where each element is one of the
      *         constants defined in MenuFactory
      */
     @Override
     public Collection<String> getMenuCategories() {
         if (standalone) {
             return super.getMenuCategories();
         }
         return null;
     }
 
     public String getLabelResource() {
         return "user_defined_variables"; // $NON-NLS-1$
     }
 
     /* Implements JMeterGUIComponent.createTestElement() */
     public TestElement createTestElement() {
         Arguments args = new Arguments();
         modifyTestElement(args);
         return args;
     }
 
     /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
     public void modifyTestElement(TestElement args) {
         stopTableEditing();
         Arguments arguments = null;
         if (args instanceof Arguments) {
             arguments = (Arguments) args;
             arguments.clear();
             @SuppressWarnings("unchecked") // only contains Argument (or HTTPArgument)
             Iterator<Argument> modelData = (Iterator<Argument>) tableModel.iterator();
             while (modelData.hasNext()) {
                 Argument arg = modelData.next();
                 if(StringUtils.isEmpty(arg.getName()) && StringUtils.isEmpty(arg.getValue())) {
                     continue;
                 }
                 arg.setMetaData("="); // $NON-NLS-1$
                 arguments.addArgument(arg);
             }
         }
         this.configureTestElement(args);
     }
 
     /**
      * A newly created component can be initialized with the contents of a Test
      * Element object by calling this method. The component is responsible for
      * querying the Test Element object for the relevant information to display
      * in its GUI.
      *
      * @param el
      *            the TestElement to configure
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         if (el instanceof Arguments) {
             tableModel.clearData();
             PropertyIterator iter = ((Arguments) el).iterator();
             while (iter.hasNext()) {
                 Argument arg = (Argument) iter.next().getObjectValue();
                 tableModel.addRow(arg);
             }
         }
         checkDeleteStatus();
     }
 
     /**
      * Get the table used to enter arguments.
      *
      * @return the table used to enter arguments
      */
     protected JTable getTable() {
         return table;
     }
 
     /**
      * Get the title label for this component.
      *
      * @return the title label displayed with the table
      */
     protected JLabel getTableLabel() {
         return tableLabel;
     }
 
     /**
      * Get the button used to delete rows from the table.
      *
      * @return the button used to delete rows from the table
      */
     protected JButton getDeleteButton() {
         return delete;
     }
 
     /**
      * Get the button used to add rows to the table.
      *
      * @return the button used to add rows to the table
      */
     protected JButton getAddButton() {
         return add;
     }
 
     /**
      * Enable or disable the delete button depending on whether or not there is
      * a row to be deleted.
      */
     protected void checkDeleteStatus() {
         // Disable DELETE if there are no rows in the table to delete.
         if (tableModel.getRowCount() == 0) {
             delete.setEnabled(false);
         } else {
             delete.setEnabled(true);
         }
         
         if(enableUpDown && tableModel.getRowCount()>1) {
             up.setEnabled(true);
             down.setEnabled(true);
         }
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         clear();
     }
 
     /**
      * Clear all rows from the table. T.Elanjchezhiyan(chezhiyan@siptech.co.in)
      */
     public void clear() {
         stopTableEditing();
         tableModel.clearData();
     }
 
     /**
      * Invoked when an action occurs. This implementation supports the add and
      * delete buttons.
      *
      * @param e
      *            the event that has occurred
      */
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
         if (action.equals(DELETE)) {
             deleteArgument();
         } else if (action.equals(ADD)) {
             addArgument();
         } else if (action.equals(ADD_FROM_CLIPBOARD)) {
             addFromClipboard();
         } else if (action.equals(UP)) {
             moveUp();
         } else if (action.equals(DOWN)) {
             moveDown();
         } else if (action.equals(DETAIL)) {
             showDetail();
         }
     }
 
     /**
      * Cancel cell editing if it is being edited
      */
     private void cancelEditing() {
         // If a table cell is being edited, we must cancel the editing before
         // deleting the row
         if (table.isEditing()) {
             TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
             cellEditor.cancelCellEditing();
         }
     }
     
     /**
      * Move a row down
      */
     private void moveDown() {
         cancelEditing();
 
         int[] rowsSelected = table.getSelectedRows();
         if (rowsSelected.length > 0 && rowsSelected[rowsSelected.length - 1] < table.getRowCount() - 1) {
             table.clearSelection();
             for (int i = rowsSelected.length - 1; i >= 0; i--) {
                 int rowSelected = rowsSelected[i];
                 tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected + 1);
             }
             for (int rowSelected : rowsSelected) {
                 table.addRowSelectionInterval(rowSelected + 1, rowSelected + 1);
             }
         }
     }
 
     /**
      *  Move a row down
      */
     private void moveUp() {
         cancelEditing();
 
         int[] rowsSelected = table.getSelectedRows();
         if (rowsSelected.length > 0 && rowsSelected[0] > 0) {
             table.clearSelection();
             for (int rowSelected : rowsSelected) {
                 tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected - 1);
             }
             for (int rowSelected : rowsSelected) {
                 table.addRowSelectionInterval(rowSelected - 1, rowSelected - 1);
             }
         }
     }
 
     /**
      * Show Row Detail
      */
     private void showDetail() {
         cancelEditing();
 
         int[] rowsSelected = table.getSelectedRows();
         if (rowsSelected.length == 1) {
             table.clearSelection();
             RowDetailDialog detailDialog = new RowDetailDialog(tableModel, rowsSelected[0]);
             detailDialog.setVisible(true);
         } 
     }
     
     /**
      * Remove the currently selected argument from the table.
      */
     protected void deleteArgument() {
         cancelEditing();
 
         int[] rowsSelected = table.getSelectedRows();
         int anchorSelection = table.getSelectionModel().getAnchorSelectionIndex();
         table.clearSelection();
         if (rowsSelected.length > 0) {
             for (int i = rowsSelected.length - 1; i >= 0; i--) {
                 tableModel.removeRow(rowsSelected[i]);
             }
 
             // Disable DELETE if there are no rows in the table to delete.
             if (tableModel.getRowCount() == 0) {
                 delete.setEnabled(false);
             }
             // Table still contains one or more rows, so highlight (select)
             // the appropriate one.
             else if (tableModel.getRowCount() > 0) {
                 if (anchorSelection >= tableModel.getRowCount()) {
                     anchorSelection = tableModel.getRowCount() - 1;
                 }
                 table.setRowSelectionInterval(anchorSelection, anchorSelection);
             }
             
             if(enableUpDown && tableModel.getRowCount()>1) {
                 up.setEnabled(true);
                 down.setEnabled(true);
             }
         }
     }
 
     /**
      * Add a new argument row to the table.
      */
     protected void addArgument() {
         // If a table cell is being edited, we should accept the current value
         // and stop the editing before adding a new row.
         stopTableEditing();
 
         tableModel.addRow(makeNewArgument());
 
         // Enable DELETE (which may already be enabled, but it won't hurt)
         delete.setEnabled(true);
         if(enableUpDown && tableModel.getRowCount()>1) {
             up.setEnabled(true);
             down.setEnabled(true);
         }
         // Highlight (select) the appropriate row.
         int rowToSelect = tableModel.getRowCount() - 1;
         table.setRowSelectionInterval(rowToSelect, rowToSelect);
     }
 
     /**
      * Add values from the clipboard
      */
     protected void addFromClipboard() {
         stopTableEditing();
         int rowCount = table.getRowCount();
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable trans = clipboard.getContents(null);
         DataFlavor[] flavourList = trans.getTransferDataFlavors();
         Collection<DataFlavor> flavours = new ArrayList<DataFlavor>(flavourList.length);
         if (Collections.addAll(flavours, flavourList) && flavours.contains(DataFlavor.stringFlavor)) {
             try {
                 String clipboardContent = (String) trans.getTransferData(DataFlavor.stringFlavor);
                 String[] clipboardLines = clipboardContent.split("\n");
                 for (String clipboardLine : clipboardLines) {
                     String[] clipboardCols = clipboardLine.split("\t");
                     if (clipboardCols.length > 0) {
                         Argument argument = makeNewArgument();
                         argument.setName(clipboardCols[0]);
                         if (clipboardCols.length > 1) {
                             argument.setValue(clipboardCols[1]);
                             if (clipboardCols.length > 2) {
                                 argument.setDescription(clipboardCols[2]);
                             }
                         }
                         tableModel.addRow(argument);
                     }
                 }
             } catch (IOException ioe) {
                 JOptionPane.showMessageDialog(this,
                         "Could not add read arguments from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                         JOptionPane.ERROR_MESSAGE);
             } catch (UnsupportedFlavorException ufe) {
                 JOptionPane.showMessageDialog(this,
                         "Could not add retrieve " + DataFlavor.stringFlavor.getHumanPresentableName()
                                 + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             }
             if (table.getRowCount() > rowCount) {
                 // Enable DELETE (which may already be enabled, but it won't hurt)
                 delete.setEnabled(true);
 
                 // Highlight (select) the appropriate rows.
                 int rowToSelect = tableModel.getRowCount() - 1;
                 table.setRowSelectionInterval(rowCount, rowToSelect);
             }
         }
     }
 
     /**
      * Create a new Argument object.
      *
      * @return a new Argument object
      */
     protected Argument makeNewArgument() {
         return new Argument("", ""); // $NON-NLS-1$ // $NON-NLS-2$
     }
 
     /**
      * Stop any editing that is currently being done on the table. This will
      * save any changes that have already been made.
      */
     protected void stopTableEditing() {
         if (table.isEditing()) {
             TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
             cellEditor.stopCellEditing();
         }
     }
 
     /**
      * Initialize the table model used for the arguments table.
      */
     protected void initializeTableModel() {
     if (tableModel == null) {
         if(standalone) {
             tableModel = new ObjectTableModel(new String[] { COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1, COLUMN_RESOURCE_NAMES_2 },
                     Argument.class,
                     new Functor[] {
                     new Functor("getName"), // $NON-NLS-1$
                     new Functor("getValue"),  // $NON-NLS-1$
                     new Functor("getDescription") },  // $NON-NLS-1$
                     new Functor[] {
                     new Functor("setName"), // $NON-NLS-1$
                     new Functor("setValue"), // $NON-NLS-1$
                     new Functor("setDescription") },  // $NON-NLS-1$
                     new Class[] { String.class, String.class, String.class });
         } else {
             tableModel = new ObjectTableModel(new String[] { COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1 },
                     Argument.class,
                     new Functor[] {
                     new Functor("getName"), // $NON-NLS-1$
                     new Functor("getValue") },  // $NON-NLS-1$
                     new Functor[] {
                     new Functor("setName"), // $NON-NLS-1$
                     new Functor("setValue") }, // $NON-NLS-1$
                     new Class[] { String.class, String.class });
             }
         }
     }
 
     public static boolean testFunctors(){
         ArgumentsPanel instance = new ArgumentsPanel();
         instance.initializeTableModel();
         return instance.tableModel.checkFunctors(null,instance.getClass());
     }
 
     /**
      * Resize the table columns to appropriate widths.
      *
      * @param _table
      *            the table to resize columns for
      */
     protected void sizeColumns(JTable _table) {
     }
 
     /**
      * Create the main GUI panel which contains the argument table.
      *
      * @return the main GUI panel
      */
     private Component makeMainPanel() {
         initializeTableModel();
         table = new JTable(tableModel);
         table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         if (this.background != null) {
             table.setBackground(this.background);
         }
         return makeScrollPane(table);
     }
 
     /**
      * Create a panel containing the title label for the table.
      *
      * @return a panel containing the title label
      */
     protected Component makeLabelPanel() {
         JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         labelPanel.add(tableLabel);
         if (this.background != null) {
             labelPanel.setBackground(this.background);
         }
         return labelPanel;
     }
 
     /**
      * Create a panel containing the add and delete buttons.
      *
      * @return a GUI panel containing the buttons
      */
     private JPanel makeButtonPanel() {
         showDetail = new JButton(JMeterUtils.getResString("detail")); // $NON-NLS-1$
         showDetail.setActionCommand(DETAIL);
         showDetail.setEnabled(true);
         
         add = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
         add.setActionCommand(ADD);
         add.setEnabled(true);
 
         addFromClipboard = new JButton(JMeterUtils.getResString("add_from_clipboard")); // $NON-NLS-1$
         addFromClipboard.setActionCommand(ADD_FROM_CLIPBOARD);
         addFromClipboard.setEnabled(true);
 
         delete = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
         delete.setActionCommand(DELETE);
 
         if(enableUpDown) {
             up = new JButton(JMeterUtils.getResString("up")); // $NON-NLS-1$
             up.setActionCommand(UP);
     
             down = new JButton(JMeterUtils.getResString("down")); // $NON-NLS-1$
             down.setActionCommand(DOWN);
         }
         checkDeleteStatus();
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
         if (this.background != null) {
             buttonPanel.setBackground(this.background);
         }
         showDetail.addActionListener(this);
         add.addActionListener(this);
         addFromClipboard.addActionListener(this);
         delete.addActionListener(this);
         buttonPanel.add(showDetail);
         buttonPanel.add(add);
         buttonPanel.add(addFromClipboard);
         buttonPanel.add(delete);
         if(enableUpDown) {
             up.addActionListener(this);
             down.addActionListener(this);
             buttonPanel.add(up);
             buttonPanel.add(down);
         }
         return buttonPanel;
     }
 
     /**
      * Initialize the components and layout of this component.
      */
     private void init() {
         JPanel p = this;
 
         if (standalone) {
             setLayout(new BorderLayout(0, 5));
             setBorder(makeBorder());
             add(makeTitlePanel(), BorderLayout.NORTH);
             p = new JPanel();
         }
 
         p.setLayout(new BorderLayout());
 
         p.add(makeLabelPanel(), BorderLayout.NORTH);
         p.add(makeMainPanel(), BorderLayout.CENTER);
         // Force a minimum table height of 70 pixels
         p.add(Box.createVerticalStrut(70), BorderLayout.WEST);
         p.add(makeButtonPanel(), BorderLayout.SOUTH);
 
         if (standalone) {
             add(p, BorderLayout.CENTER);
         }
 
         table.revalidate();
         sizeColumns(table);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/RawTextSearcher.java b/src/core/org/apache/jmeter/gui/action/RawTextSearcher.java
index 362ab6d05..866502e93 100644
--- a/src/core/org/apache/jmeter/gui/action/RawTextSearcher.java
+++ b/src/core/org/apache/jmeter/gui/action/RawTextSearcher.java
@@ -1,80 +1,80 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.util.List;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 
 /**
  * Searcher implementation that searches text as is
  */
 public class RawTextSearcher implements Searcher {
 	private boolean caseSensitive;
 	private String textToSearch;
 	
 
 	/**
 	 * Constructor
 	 * @param caseSensitive is search case sensitive
 	 * @param textToSearch Text to search
 	 */
 	public RawTextSearcher(boolean caseSensitive, String textToSearch) {
 		super();
 		this.caseSensitive = caseSensitive;
 		if(caseSensitive) {
 			this.textToSearch = textToSearch;
 		} else {
 			this.textToSearch = textToSearch.toLowerCase();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean search(List<String> textTokens) {
 		boolean result = false;
 		for (String searchableToken : textTokens) {
 			if(!StringUtils.isEmpty(searchableToken)) {
 				if(caseSensitive) {
 					result = searchableToken.indexOf(textToSearch)>=0;
 				} else {
 					result = searchableToken.toLowerCase().indexOf(textToSearch)>=0;
 				}
 				if (result) {
 					return result;
 				}
 			}
 		}
 		return false;
 	}
 
 	/**
      * Returns true if searchedTextLowerCase is in value
      * @param value
      * @param searchedTextLowerCase
      * @return true if searchedTextLowerCase is in value
      */
     protected boolean testField(String value, String searchedTextLowerCase) {
         if(!StringUtils.isEmpty(value)) {
             return value.toLowerCase().indexOf(searchedTextLowerCase)>=0;
         }
         return false;
     }
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/action/RegexpSearcher.java b/src/core/org/apache/jmeter/gui/action/RegexpSearcher.java
index eede41adb..b36197fe5 100644
--- a/src/core/org/apache/jmeter/gui/action/RegexpSearcher.java
+++ b/src/core/org/apache/jmeter/gui/action/RegexpSearcher.java
@@ -1,71 +1,71 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 
 /**
  * Regexp search implementation
  */
 public class RegexpSearcher implements Searcher {
 	private boolean caseSensitive;
 	private Pattern pattern;
 	
 
 	/**
 	 * Constructor
 	 * @param caseSensitive is search case sensitive
 	 * @param regexp Regexp to search
 	 */
 	public RegexpSearcher(boolean caseSensitive, String regexp) {
 		super();
 		this.caseSensitive = caseSensitive;
 		String newRegexp = ".*"+regexp+".*";
 		if(caseSensitive) {
 			pattern = Pattern.compile(newRegexp);
 		} else {
 			pattern = Pattern.compile(newRegexp.toLowerCase());
 		}
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean search(List<String> textTokens) {
 		for (String searchableToken : textTokens) {
 			if(!StringUtils.isEmpty(searchableToken)) {
 				Matcher matcher = null; 
 				if(caseSensitive) {
 					matcher = pattern.matcher(searchableToken);
 				} else {
 					matcher = pattern.matcher(searchableToken.toLowerCase());
 				}
 				if(matcher.find()) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java b/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
index 2ce637cc6..bb2effe99 100644
--- a/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
+++ b/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
@@ -1,197 +1,197 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.BorderLayout;
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import javax.swing.BorderFactory;
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * 
  */
 public class SearchTreeDialog extends JDialog implements ActionListener {
     /**
 	 * 
 	 */
 	private static final long serialVersionUID = -4436834972710248247L;
 
 	private Logger logger = LoggingManager.getLoggerForClass();
 
     private JButton searchButton;
     
 	private JLabeledTextField searchTF;
 	
 	private JCheckBox isRegexpCB;
 
 	private JCheckBox isCaseSensitiveCB;
 
 	private JButton cancelButton;
 
 	/**
 	 * Store last search
 	 */
 	private transient String lastSearch = null;
 
 	/**
 	 * Hide Window on ESC
 	 */
 	private transient ActionListener enterActionListener = new ActionListener() {
 		public void actionPerformed(ActionEvent actionEvent) {
 			doSearch(actionEvent);
 		}	
 	};
 	
 	/**
 	 * Do search on Enter
 	 */
 	private transient ActionListener escapeActionListener = new ActionListener() {
 		public void actionPerformed(ActionEvent actionEvent) {
 			setVisible(false);
 		}	
 	};
 	
 	public SearchTreeDialog() {
         super((JFrame) null, JMeterUtils.getResString("search_tree_title"), true); //$NON-NLS-1$
         init();
     }
 
     private void init() {
         this.getContentPane().setLayout(new BorderLayout(10,10));
 
         searchTF = new JLabeledTextField(JMeterUtils.getResString("search_text_field"), 20); //$NON-NLS-1$
         if(!StringUtils.isEmpty(lastSearch)) {
         	searchTF.setText(lastSearch);
         }
         isRegexpCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), false); //$NON-NLS-1$
         isCaseSensitiveCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); //$NON-NLS-1$
         Font font = new Font("SansSerif", Font.PLAIN, 10); // reduce font
         isRegexpCB.setFont(font);
         isCaseSensitiveCB.setFont(font);
 
         JPanel searchCriterionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         searchCriterionPanel.add(isCaseSensitiveCB);
         searchCriterionPanel.add(isRegexpCB);
         
         JPanel searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(7, 3, 3, 3));
         searchPanel.add(searchTF, BorderLayout.NORTH);
         searchPanel.add(searchCriterionPanel, BorderLayout.CENTER);
         JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         
         searchButton = new JButton(JMeterUtils.getResString("search")); //$NON-NLS-1$
         searchButton.addActionListener(this);
         cancelButton = new JButton(JMeterUtils.getResString("cancel")); //$NON-NLS-1$
         cancelButton.addActionListener(this);
         buttonsPanel.add(searchButton);
         buttonsPanel.add(cancelButton);
         searchPanel.add(buttonsPanel, BorderLayout.SOUTH);
         this.getContentPane().add(searchPanel);
         searchPanel.registerKeyboardAction(enterActionListener, KeyStrokes.ENTER, JComponent.WHEN_IN_FOCUSED_WINDOW);
         searchPanel.registerKeyboardAction(escapeActionListener, KeyStrokes.ESC, JComponent.WHEN_IN_FOCUSED_WINDOW);
     	searchTF.requestFocusInWindow();
 
         this.pack();
         ComponentUtil.centerComponentInWindow(this);
     }
 
     /**
      * Do search
      * @param e {@link ActionEvent}
      */
     public void actionPerformed(ActionEvent e) {
     	if(e.getSource()==cancelButton) {
     		this.setVisible(false);
     		return;
     	} 
     	doSearch(e);
     }
 
 	/**
 	 * @param e {@link ActionEvent}
 	 */
 	private void doSearch(ActionEvent e) {
 		String wordToSearch = searchTF.getText();
     	if(StringUtils.isEmpty(wordToSearch)) {
             return;
         } else {
         	this.lastSearch = wordToSearch;
         }
     	
     	// reset previous result
     	ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.SEARCH_RESET));
         // do search
     	Searcher searcher = null; 
     	if(isRegexpCB.isSelected()) {
     		searcher = new RegexpSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText());
     	} else {
     		searcher = new RawTextSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText());
     	}
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeModel jMeterTreeModel = guiPackage.getTreeModel();
         Set<JMeterTreeNode> nodes = new HashSet<JMeterTreeNode>();
         for (JMeterTreeNode jMeterTreeNode : jMeterTreeModel.getNodesOfType(Searchable.class)) {
             try {
                 if (jMeterTreeNode.getUserObject() instanceof Searchable){
                     Searchable searchable = (Searchable) jMeterTreeNode.getUserObject();
                     List<JMeterTreeNode> matchingNodes = jMeterTreeNode.getPathToThreadGroup();
                     List<String> searchableTokens = searchable.getSearchableTokens();
                     boolean result = searcher.search(searchableTokens);
                     if(result) {
                         nodes.addAll(matchingNodes);
                     }
                 }
             } catch (Exception ex) {
                 logger.error("Error occured searching for word:"+ wordToSearch, ex);
             }
         }
         for (Iterator<JMeterTreeNode> iterator = nodes.iterator(); iterator.hasNext();) {
             JMeterTreeNode jMeterTreeNode = iterator.next();
             jMeterTreeNode.setMarkedBySearch(true);
         }
         GuiPackage.getInstance().getMainFrame().repaint();
         this.setVisible(false);
 	}
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 9a34a391a..e3804c102 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,830 +1,830 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 /*
  * Created on Sep 7, 2004
  */
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Properties;
 
-import org.apache.commons.lang.CharUtils;
+import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.util.JMeterError;
 
 /*
  * N.B. to add a new field, remember the following
  * - static _xyz
  * - instance xyz=_xyz
  * - clone s.xyz = xyz (perhaps)
  * - setXyz(boolean)
  * - saveXyz()
  * - update SampleSaveConfigurationConverter to add new fields to marshall() and shouldSerialiseMember()
  * - update SampleResultConverter and/or HTTPSampleConverter
  * - update CSVSaveService: CSV_XXXX, makeResultFromDelimitedString, printableFieldNamesToString, static{}
  * - update messages.properties to add save_xyz entry
  * - update jmeter.properties to add new property
  * - update listeners.xml to add new property, CSV and XML names etc.
  * - take screenshot sample_result_config.png
  * - update listeners.xml and component_reference.xml with new dimensions (might not change)
  *
  */
 /**
  * Holds details of which sample attributes to save.
  *
  * The pop-up dialogue for this is created by the class SavePropertyDialog, which assumes:
  * For each field XXX
  * - methods have the signature "boolean saveXXX()"
  * - a corresponding "void setXXX(boolean)" method
  * - messages.properties contains the key save_XXX
  *
  *
  */
 public class SampleSaveConfiguration implements Cloneable, Serializable {
     private static final long serialVersionUID = 7L;
 
     // ---------------------------------------------------------------------
     // PROPERTY FILE CONSTANTS
     // ---------------------------------------------------------------------
 
     /** Indicates that the results file should be in XML format. * */
     private static final String XML = "xml"; // $NON_NLS-1$
 
     /** Indicates that the results file should be in CSV format. * */
     //NOTUSED private static final String CSV = "csv"; // $NON_NLS-1$
 
     /** Indicates that the results should be stored in a database. * */
     //NOTUSED private static final String DATABASE = "db"; // $NON_NLS-1$
 
     /** A properties file indicator for true. * */
     private static final String TRUE = "true"; // $NON_NLS-1$
 
     /** A properties file indicator for false. * */
     private static final String FALSE = "false"; // $NON_NLS-1$
 
     /** A properties file indicator for milliseconds. * */
     private static final String MILLISECONDS = "ms"; // $NON_NLS-1$
 
     /** A properties file indicator for none. * */
     private static final String NONE = "none"; // $NON_NLS-1$
 
     /** A properties file indicator for the first of a series. * */
     private static final String FIRST = "first"; // $NON_NLS-1$
 
     /** A properties file indicator for all of a series. * */
     private static final String ALL = "all"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     private static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
         "jmeter.save.saveservice.assertion_results_failure_message";  // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     private static final String ASSERTION_RESULTS_PROP = "jmeter.save.saveservice.assertion_results"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which delimiter should be used when
      * saving in a delimited values format.
      **************************************************************************/
     private static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which format should be used when
      * saving the results, e.g., xml or csv.
      **************************************************************************/
     private static final String OUTPUT_FORMAT_PROP = "jmeter.save.saveservice.output_format"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether field names should be printed
      * to a delimited file.
      **************************************************************************/
     private static final String PRINT_FIELD_NAMES_PROP = "jmeter.save.saveservice.print_field_names"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the data type should be
      * saved.
      **************************************************************************/
     private static final String SAVE_DATA_TYPE_PROP = "jmeter.save.saveservice.data_type"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the label should be saved.
      **************************************************************************/
     private static final String SAVE_LABEL_PROP = "jmeter.save.saveservice.label"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response code should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_CODE_PROP = "jmeter.save.saveservice.response_code"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response data should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_DATA_PROP = "jmeter.save.saveservice.response_data"; // $NON_NLS-1$
 
     private static final String SAVE_RESPONSE_DATA_ON_ERROR_PROP = "jmeter.save.saveservice.response_data.on_error"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response message should
      * be saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_MESSAGE_PROP = "jmeter.save.saveservice.response_message"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the success indicator should
      * be saved.
      **************************************************************************/
     private static final String SAVE_SUCCESSFUL_PROP = "jmeter.save.saveservice.successful"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the thread name should be
      * saved.
      **************************************************************************/
     private static final String SAVE_THREAD_NAME_PROP = "jmeter.save.saveservice.thread_name"; // $NON_NLS-1$
 
     // Save bytes read
     private static final String SAVE_BYTES_PROP = "jmeter.save.saveservice.bytes"; // $NON_NLS-1$
 
     // Save URL
     private static final String SAVE_URL_PROP = "jmeter.save.saveservice.url"; // $NON_NLS-1$
 
     // Save fileName for ResultSaver
     private static final String SAVE_FILENAME_PROP = "jmeter.save.saveservice.filename"; // $NON_NLS-1$
 
     // Save hostname for ResultSaver
     private static final String SAVE_HOSTNAME_PROP = "jmeter.save.saveservice.hostname"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the time should be saved.
      **************************************************************************/
     private static final String SAVE_TIME_PROP = "jmeter.save.saveservice.time"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property giving the format of the time stamp
      **************************************************************************/
     private static final String TIME_STAMP_FORMAT_PROP = "jmeter.save.saveservice.timestamp_format"; // $NON_NLS-1$
 
     private static final String SUBRESULTS_PROP      = "jmeter.save.saveservice.subresults"; // $NON_NLS-1$
     private static final String ASSERTIONS_PROP      = "jmeter.save.saveservice.assertions"; // $NON_NLS-1$
     private static final String LATENCY_PROP         = "jmeter.save.saveservice.latency"; // $NON_NLS-1$
     private static final String SAMPLERDATA_PROP     = "jmeter.save.saveservice.samplerData"; // $NON_NLS-1$
     private static final String RESPONSEHEADERS_PROP = "jmeter.save.saveservice.responseHeaders"; // $NON_NLS-1$
     private static final String REQUESTHEADERS_PROP  = "jmeter.save.saveservice.requestHeaders"; // $NON_NLS-1$
     private static final String ENCODING_PROP        = "jmeter.save.saveservice.encoding"; // $NON_NLS-1$
 
 
     // optional processing instruction for line 2; e.g.
     // <?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report_21.xsl"?>
     private static final String XML_PI               = "jmeter.save.saveservice.xml_pi"; // $NON_NLS-1$
 
     private static final String SAVE_THREAD_COUNTS   = "jmeter.save.saveservice.thread_counts"; // $NON_NLS-1$
 
     private static final String SAVE_SAMPLE_COUNT    = "jmeter.save.saveservice.sample_count"; // $NON_NLS-1$
 
     private static final String SAVE_IDLE_TIME       = "jmeter.save.saveservice.idle_time"; // $NON_NLS-1$
     // N.B. Remember to update the equals and hashCode methods when adding new variables.
 
     // Initialise values from properties
     private boolean time = _time, latency = _latency, timestamp = _timestamp, success = _success, label = _label,
             code = _code, message = _message, threadName = _threadName, dataType = _dataType, encoding = _encoding,
             assertions = _assertions, subresults = _subresults, responseData = _responseData,
             samplerData = _samplerData, xml = _xml, fieldNames = _fieldNames, responseHeaders = _responseHeaders,
             requestHeaders = _requestHeaders, responseDataOnError = _responseDataOnError;
 
     private boolean saveAssertionResultsFailureMessage = _saveAssertionResultsFailureMessage;
 
     private boolean url = _url, bytes = _bytes , fileName = _fileName;
 
     private boolean hostname = _hostname;
 
     private boolean threadCounts = _threadCounts;
 
     private boolean sampleCount = _sampleCount;
 
     private boolean idleTime = _idleTime;
 
     // Does not appear to be used (yet)
     private int assertionsResultsToSave = _assertionsResultsToSave;
 
 
     // Don't save this, as it is derived from the time format
     private boolean printMilliseconds = _printMilliseconds;
 
     /** A formatter for the time stamp. */
     private transient DateFormat formatter = _formatter;
     /* Make transient as we don't want to save the SimpleDataFormat class
      * Also, there's currently no way to change the value via the GUI, so changing it
      * later means editting the JMX, or recreating the Listener.
      */
 
     // Defaults from properties:
     private static final boolean _time, _timestamp, _success, _label, _code, _message, _threadName, _xml,
             _responseData, _dataType, _encoding, _assertions, _latency, _subresults, _samplerData, _fieldNames,
             _responseHeaders, _requestHeaders;
 
     private static final boolean _responseDataOnError;
 
     private static final boolean _saveAssertionResultsFailureMessage;
 
     private static final String _timeStampFormat;
 
     private static final int _assertionsResultsToSave;
 
     // TODO turn into method?
     public static final int SAVE_NO_ASSERTIONS = 0;
 
     public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
 
     public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
 
     private static final boolean _printMilliseconds;
 
     private static final boolean _bytes;
 
     private static final boolean _url;
 
     private static final boolean _fileName;
 
     private static final boolean _hostname;
 
     private static final boolean _threadCounts;
 
     private static final boolean _sampleCount;
 
     private static final DateFormat _formatter;
 
     /**
      * The string used to separate fields when stored to disk, for example, the
      * comma for CSV files.
      */
     private static final String _delimiter;
 
     private static final boolean _idleTime;
 
     private static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
         if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             dlm="\t";
         }
 
         if (dlm.length() != 1){
             throw new JMeterError("Delimiter '"+dlm+"' must be of length 1.");
         }
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         _delimiter = dlm;
 
         _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));
 
         _dataType = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));
 
         _label = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));
 
         _code = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));
 
         _responseData = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));
 
         _responseDataOnError = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));
 
         _message = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));
 
         _success = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));
 
         _threadName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));
 
         _bytes = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));
 
         _url = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));
 
         _fileName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));
 
         _hostname = TRUE.equalsIgnoreCase(props.getProperty(SAVE_HOSTNAME_PROP, FALSE));
 
         _time = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));
 
         _timeStampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
 
         _printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);
 
         // Prepare for a pretty date
         if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
             _formatter = new SimpleDateFormat(_timeStampFormat);
         } else {
             _formatter = null;
         }
 
         _timestamp = !NONE.equalsIgnoreCase(_timeStampFormat);// reversed compare allows for null
 
         _saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));
 
         String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
         if (NONE.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_NO_ASSERTIONS;
         } else if (FIRST.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_FIRST_ASSERTION;
         } else if (ALL.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
         } else {
             _assertionsResultsToSave = 0;
         }
 
         String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);
 
         if (XML.equals(howToSave)) {
             _xml = true;
         } else {
             _xml = false;
         }
 
         _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, FALSE));
 
         _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, FALSE));
     }
 
     // Don't save this, as not settable via GUI
     private String delimiter = _delimiter;
 
     // Don't save this - only needed for processing CSV headers currently
     private transient int varCount = 0;
 
     private static final SampleSaveConfiguration _static = new SampleSaveConfiguration();
 
     public int getVarCount() { // Only for use by CSVSaveService
         return varCount;
     }
 
     public void setVarCount(int varCount) { // Only for use by CSVSaveService
         this.varCount = varCount;
     }
 
     // Give access to initial configuration
     public static SampleSaveConfiguration staticConfig() {
         return _static;
     }
 
     public SampleSaveConfiguration() {
     }
 
     /**
      * Alternate constructor for use by OldSaveService
      *
      * @param value initial setting for boolean fields used in Config dialogue
      */
     public SampleSaveConfiguration(boolean value) {
         assertions = value;
         bytes = value;
         code = value;
         dataType = value;
         encoding = value;
         fieldNames = value;
         fileName = value;
         hostname = value;
         label = value;
         latency = value;
         message = value;
         printMilliseconds = _printMilliseconds;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         subresults = value;
         success = value;
         threadCounts = value;
         sampleCount = value;
         threadName = value;
         time = value;
         timestamp = value;
         url = value;
         xml = value;
     }
 
     private Object readResolve(){
        formatter = _formatter;
        return this;
     }
 
     @Override
     public Object clone() {
         try {
             SampleSaveConfiguration clone = (SampleSaveConfiguration)super.clone();
             if(this.formatter != null) {
                 clone.formatter = (SimpleDateFormat)this.formatter.clone();
             }
             return clone;
         }
         catch(CloneNotSupportedException e) {
             throw new RuntimeException("Should not happen",e);
         }
     }
 
     @Override
     public boolean equals(Object obj) {
         if(this == obj) {
             return true;
         }
         if((obj == null) || (obj.getClass() != this.getClass())) {
             return false;
         }
         // We know we are comparing to another SampleSaveConfiguration
         SampleSaveConfiguration s = (SampleSaveConfiguration)obj;
         boolean primitiveValues = s.time == time &&
             s.latency == latency &&
             s.timestamp == timestamp &&
             s.success == success &&
             s.label == label &&
             s.code == code &&
             s.message == message &&
             s.threadName == threadName &&
             s.dataType == dataType &&
             s.encoding == encoding &&
             s.assertions == assertions &&
             s.subresults == subresults &&
             s.responseData == responseData &&
             s.samplerData == samplerData &&
             s.xml == xml &&
             s.fieldNames == fieldNames &&
             s.responseHeaders == responseHeaders &&
             s.requestHeaders == requestHeaders &&
             s.assertionsResultsToSave == assertionsResultsToSave &&
             s.saveAssertionResultsFailureMessage == saveAssertionResultsFailureMessage &&
             s.printMilliseconds == printMilliseconds &&
             s.responseDataOnError == responseDataOnError &&
             s.url == url &&
             s.bytes == bytes &&
             s.fileName == fileName &&
             s.hostname == hostname &&
             s.sampleCount == sampleCount &&
             s.idleTime == idleTime &&
             s.threadCounts == threadCounts;
 
         boolean stringValues = false;
         if(primitiveValues) {
             stringValues = s.delimiter == delimiter || (delimiter != null && delimiter.equals(s.delimiter));
         }
         boolean complexValues = false;
         if(primitiveValues && stringValues) {
             complexValues = s.formatter == formatter || (formatter != null && formatter.equals(s.formatter));
         }
 
         return primitiveValues && stringValues && complexValues;
     }
 
     @Override
     public int hashCode() {
         int hash = 7;
         hash = 31 * hash + (time ? 1 : 0);
         hash = 31 * hash + (latency ? 1 : 0);
         hash = 31 * hash + (timestamp ? 1 : 0);
         hash = 31 * hash + (success ? 1 : 0);
         hash = 31 * hash + (label ? 1 : 0);
         hash = 31 * hash + (code ? 1 : 0);
         hash = 31 * hash + (message ? 1 : 0);
         hash = 31 * hash + (threadName ? 1 : 0);
         hash = 31 * hash + (dataType ? 1 : 0);
         hash = 31 * hash + (encoding ? 1 : 0);
         hash = 31 * hash + (assertions ? 1 : 0);
         hash = 31 * hash + (subresults ? 1 : 0);
         hash = 31 * hash + (responseData ? 1 : 0);
         hash = 31 * hash + (samplerData ? 1 : 0);
         hash = 31 * hash + (xml ? 1 : 0);
         hash = 31 * hash + (fieldNames ? 1 : 0);
         hash = 31 * hash + (responseHeaders ? 1 : 0);
         hash = 31 * hash + (requestHeaders ? 1 : 0);
         hash = 31 * hash + assertionsResultsToSave;
         hash = 31 * hash + (saveAssertionResultsFailureMessage ? 1 : 0);
         hash = 31 * hash + (printMilliseconds ? 1 : 0);
         hash = 31 * hash + (responseDataOnError ? 1 : 0);
         hash = 31 * hash + (url ? 1 : 0);
         hash = 31 * hash + (bytes ? 1 : 0);
         hash = 31 * hash + (fileName ? 1 : 0);
         hash = 31 * hash + (hostname ? 1 : 0);
         hash = 31 * hash + (threadCounts ? 1 : 0);
         hash = 31 * hash + (delimiter != null  ? delimiter.hashCode() : 0);
         hash = 31 * hash + (formatter != null  ? formatter.hashCode() : 0);
         hash = 31 * hash + (sampleCount ? 1 : 0);
         hash = 31 * hash + (idleTime ? 1 : 0);
 
         return hash;
     }
 
     ///////////////////// Start of standard save/set access methods /////////////////////
 
     public boolean saveResponseHeaders() {
         return responseHeaders;
     }
 
     public void setResponseHeaders(boolean r) {
         responseHeaders = r;
     }
 
     public boolean saveRequestHeaders() {
         return requestHeaders;
     }
 
     public void setRequestHeaders(boolean r) {
         requestHeaders = r;
     }
 
     public boolean saveAssertions() {
         return assertions;
     }
 
     public void setAssertions(boolean assertions) {
         this.assertions = assertions;
     }
 
     public boolean saveCode() {
         return code;
     }
 
     public void setCode(boolean code) {
         this.code = code;
     }
 
     public boolean saveDataType() {
         return dataType;
     }
 
     public void setDataType(boolean dataType) {
         this.dataType = dataType;
     }
 
     public boolean saveEncoding() {
         return encoding;
     }
 
     public void setEncoding(boolean encoding) {
         this.encoding = encoding;
     }
 
     public boolean saveLabel() {
         return label;
     }
 
     public void setLabel(boolean label) {
         this.label = label;
     }
 
     public boolean saveLatency() {
         return latency;
     }
 
     public void setLatency(boolean latency) {
         this.latency = latency;
     }
 
     public boolean saveMessage() {
         return message;
     }
 
     public void setMessage(boolean message) {
         this.message = message;
     }
 
     public boolean saveResponseData(SampleResult res) {
         return responseData || TestPlan.getFunctionalMode() || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveResponseData()
     {
         return responseData;
     }
 
     public void setResponseData(boolean responseData) {
         this.responseData = responseData;
     }
 
     public boolean saveSamplerData(SampleResult res) {
         return samplerData || TestPlan.getFunctionalMode() // as per 2.0 branch
                 || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveSamplerData()
     {
         return samplerData;
     }
 
     public void setSamplerData(boolean samplerData) {
         this.samplerData = samplerData;
     }
 
     public boolean saveSubresults() {
         return subresults;
     }
 
     public void setSubresults(boolean subresults) {
         this.subresults = subresults;
     }
 
     public boolean saveSuccess() {
         return success;
     }
 
     public void setSuccess(boolean success) {
         this.success = success;
     }
 
     public boolean saveThreadName() {
         return threadName;
     }
 
     public void setThreadName(boolean threadName) {
         this.threadName = threadName;
     }
 
     public boolean saveTime() {
         return time;
     }
 
     public void setTime(boolean time) {
         this.time = time;
     }
 
     public boolean saveTimestamp() {
         return timestamp;
     }
 
     public void setTimestamp(boolean timestamp) {
         this.timestamp = timestamp;
     }
 
     public boolean saveAsXml() {
         return xml;
     }
 
     public void setAsXml(boolean xml) {
         this.xml = xml;
     }
 
     public boolean saveFieldNames() {
         return fieldNames;
     }
 
     public void setFieldNames(boolean printFieldNames) {
         this.fieldNames = printFieldNames;
     }
 
     public boolean saveUrl() {
         return url;
     }
 
     public void setUrl(boolean save) {
         this.url = save;
     }
 
     public boolean saveBytes() {
         return bytes;
     }
 
     public void setBytes(boolean save) {
         this.bytes = save;
     }
 
     public boolean saveFileName() {
         return fileName;
     }
 
     public void setFileName(boolean save) {
         this.fileName = save;
     }
 
     public boolean saveAssertionResultsFailureMessage() {
         return saveAssertionResultsFailureMessage;
     }
 
     public void setAssertionResultsFailureMessage(boolean b) {
         saveAssertionResultsFailureMessage = b;
     }
 
     public boolean saveThreadCounts() {
         return threadCounts;
     }
 
     public void setThreadCounts(boolean save) {
         this.threadCounts = save;
     }
 
     public boolean saveSampleCount() {
         return sampleCount;
     }
 
     public void setSampleCount(boolean save) {
         this.sampleCount = save;
     }
 
     ///////////////// End of standard field accessors /////////////////////
 
     /**
      * Only intended for use by OldSaveService (and test cases)
      */
     public void setFormatter(DateFormat fmt){
         printMilliseconds = (fmt == null); // maintain relationship
         formatter = fmt;
     }
 
     public boolean printMilliseconds() {
         return printMilliseconds;
     }
 
     public DateFormat formatter() {
         return formatter;
     }
 
     public int assertionsResultsToSave() {
         return assertionsResultsToSave;
     }
 
     public String getDelimiter() {
         return delimiter;
     }
 
     public String getXmlPi() {
         return JMeterUtils.getJMeterProperties().getProperty(XML_PI, ""); // Defaults to empty;
     }
 
     // Used by old Save service
     public void setDelimiter(String delim) {
         delimiter=delim;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultDelimiter() {
         delimiter=_delimiter;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultTimeStampFormat() {
         printMilliseconds=_printMilliseconds;
         formatter=_formatter;
     }
 
     public boolean saveHostname(){
         return hostname;
     }
 
     public void setHostname(boolean save){
         hostname = save;
     }
 
     public boolean saveIdleTime() {
         return idleTime;
     }
 
     public void setIdleTime(boolean save) {
         idleTime = save;
     }
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/save/CSVSaveService.java b/src/core/org/apache/jmeter/save/CSVSaveService.java
index 3468d1994..349e47aeb 100644
--- a/src/core/org/apache/jmeter/save/CSVSaveService.java
+++ b/src/core/org/apache/jmeter/save/CSVSaveService.java
@@ -1,1038 +1,1038 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.save;
 
 import java.io.BufferedReader;
 import java.io.CharArrayWriter;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.StringReader;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import javax.swing.table.DefaultTableModel;
 
 import org.apache.commons.collections.map.LinkedMap;
-import org.apache.commons.lang.CharUtils;
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.CharUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.samplers.StatisticalSampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * This class provides a means for saving/reading test results as CSV files.
  */
 // For unit tests, @see TestCSVSaveService
 public final class CSVSaveService {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // ---------------------------------------------------------------------
     // XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     private static final String DATA_TYPE = "dataType"; // $NON-NLS-1$
     private static final String FAILURE_MESSAGE = "failureMessage"; // $NON-NLS-1$
     private static final String LABEL = "label"; // $NON-NLS-1$
     private static final String RESPONSE_CODE = "responseCode"; // $NON-NLS-1$
     private static final String RESPONSE_MESSAGE = "responseMessage"; // $NON-NLS-1$
     private static final String SUCCESSFUL = "success"; // $NON-NLS-1$
     private static final String THREAD_NAME = "threadName"; // $NON-NLS-1$
     private static final String TIME_STAMP = "timeStamp"; // $NON-NLS-1$
 
     // ---------------------------------------------------------------------
     // ADDITIONAL CSV RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     private static final String CSV_ELAPSED = "elapsed"; // $NON-NLS-1$
     private static final String CSV_BYTES = "bytes"; // $NON-NLS-1$
     private static final String CSV_THREAD_COUNT1 = "grpThreads"; // $NON-NLS-1$
     private static final String CSV_THREAD_COUNT2 = "allThreads"; // $NON-NLS-1$
     private static final String CSV_SAMPLE_COUNT = "SampleCount"; // $NON-NLS-1$
     private static final String CSV_ERROR_COUNT = "ErrorCount"; // $NON-NLS-1$
     private static final String CSV_URL = "URL"; // $NON-NLS-1$
     private static final String CSV_FILENAME = "Filename"; // $NON-NLS-1$
     private static final String CSV_LATENCY = "Latency"; // $NON-NLS-1$
     private static final String CSV_ENCODING = "Encoding"; // $NON-NLS-1$
     private static final String CSV_HOSTNAME = "Hostname"; // $NON-NLS-1$
     private static final String CSV_IDLETIME = "IdleTime"; // $NON-NLS-1$
 
     // Used to enclose variable name labels, to distinguish from any of the
     // above labels
     private static final String VARIABLE_NAME_QUOTE_CHAR = "\""; // $NON-NLS-1$
 
     // Initial config from properties
     static private final SampleSaveConfiguration _saveConfig = SampleSaveConfiguration
             .staticConfig();
 
     // Date format to try if the time format does not parse as milliseconds
     // (this is the suggested value in jmeter.properties)
     private static final String DEFAULT_DATE_FORMAT_STRING = "MM/dd/yy HH:mm:ss"; // $NON-NLS-1$
 
     private static final String LINE_SEP = System.getProperty("line.separator"); // $NON-NLS-1$
 
     /**
      * Private constructor to prevent instantiation.
      */
     private CSVSaveService() {
     }
 
     /**
      * Read Samples from a file; handles quoted strings.
      * 
      * @param filename
      *            input file
      * @param visualizer
      *            where to send the results
      * @param resultCollector
      *            the parent collector
      * @throws IOException
      */
     public static void processSamples(String filename, Visualizer visualizer,
             ResultCollector resultCollector) throws IOException {
         BufferedReader dataReader = null;
         final boolean errorsOnly = resultCollector.isErrorLogging();
         final boolean successOnly = resultCollector.isSuccessOnlyLogging();
         try {
             dataReader = new BufferedReader(new FileReader(filename)); // TODO Charset ?
             dataReader.mark(400);// Enough to read the header column names
             // Get the first line, and see if it is the header
             String line = dataReader.readLine();
             if (line == null) {
                 throw new IOException(filename + ": unable to read header line");
             }
             long lineNumber = 1;
             SampleSaveConfiguration saveConfig = CSVSaveService
                     .getSampleSaveConfiguration(line, filename);
             if (saveConfig == null) {// not a valid header
                 log.info(filename
                         + " does not appear to have a valid header. Using default configuration.");
                 saveConfig = (SampleSaveConfiguration) resultCollector
                         .getSaveConfig().clone(); // may change the format later
                 dataReader.reset(); // restart from beginning
                 lineNumber = 0;
             }
             String[] parts;
             final char delim = saveConfig.getDelimiter().charAt(0);
             // TODO: does it matter that an empty line will terminate the loop?
             // CSV output files should never contain empty lines, so probably
             // not
             // If so, then need to check whether the reader is at EOF
             SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);
             while ((parts = csvReadFile(dataReader, delim)).length != 0) {
                 lineNumber++;
                 SampleEvent event = CSVSaveService
                         .makeResultFromDelimitedString(parts, saveConfig,
                                 lineNumber, dateFormat);
                 if (event != null) {
                     final SampleResult result = event.getResult();
                     if (ResultCollector.isSampleWanted(result.isSuccessful(),
                             errorsOnly, successOnly)) {
                         visualizer.add(result);
                     }
                 }
             }
         } finally {
             JOrphanUtils.closeQuietly(dataReader);
         }
     }
 
     /**
      * Make a SampleResult given a set of tokens
      * 
      * @param parts
      *            tokens parsed from the input
      * @param saveConfig
      *            the save configuration (may be updated)
      * @param lineNumber
      * @param dateFormat
      * @return the sample result
      * 
      * @throws JMeterError
      */
     private static SampleEvent makeResultFromDelimitedString(
             final String[] parts, 
             final SampleSaveConfiguration saveConfig, // may be updated
             final long lineNumber, DateFormat dateFormat) {
 
         SampleResult result = null;
         String hostname = "";// $NON-NLS-1$
         long timeStamp = 0;
         long elapsed = 0;
         String text = null;
         String field = null; // Save the name for error reporting
         int i = 0;
         try {
             if (saveConfig.saveTimestamp()) {
                 field = TIME_STAMP;
                 text = parts[i++];
                 if (saveConfig.printMilliseconds()) {
                     try {
                         timeStamp = Long.parseLong(text);
                     } catch (NumberFormatException e) {// see if this works
                         log.warn(e.toString());
                         // method is only ever called from one thread at a time
                         // so it's OK to use a static DateFormat
                         Date stamp = dateFormat.parse(text);
                         timeStamp = stamp.getTime();
                         log.warn("Setting date format to: "
                                 + DEFAULT_DATE_FORMAT_STRING);
                         saveConfig.setFormatter(dateFormat);
                     }
                 } else if (saveConfig.formatter() != null) {
                     Date stamp = saveConfig.formatter().parse(text);
                     timeStamp = stamp.getTime();
                 } else { // can this happen?
                     final String msg = "Unknown timestamp format";
                     log.warn(msg);
                     throw new JMeterError(msg);
                 }
             }
 
             if (saveConfig.saveTime()) {
                 field = CSV_ELAPSED;
                 text = parts[i++];
                 elapsed = Long.parseLong(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 result = new StatisticalSampleResult(timeStamp, elapsed);
             } else {
                 result = new SampleResult(timeStamp, elapsed);
             }
 
             if (saveConfig.saveLabel()) {
                 field = LABEL;
                 text = parts[i++];
                 result.setSampleLabel(text);
             }
             if (saveConfig.saveCode()) {
                 field = RESPONSE_CODE;
                 text = parts[i++];
                 result.setResponseCode(text);
             }
 
             if (saveConfig.saveMessage()) {
                 field = RESPONSE_MESSAGE;
                 text = parts[i++];
                 result.setResponseMessage(text);
             }
 
             if (saveConfig.saveThreadName()) {
                 field = THREAD_NAME;
                 text = parts[i++];
                 result.setThreadName(text);
             }
 
             if (saveConfig.saveDataType()) {
                 field = DATA_TYPE;
                 text = parts[i++];
                 result.setDataType(text);
             }
 
             if (saveConfig.saveSuccess()) {
                 field = SUCCESSFUL;
                 text = parts[i++];
                 result.setSuccessful(Boolean.valueOf(text).booleanValue());
             }
 
             if (saveConfig.saveAssertionResultsFailureMessage()) {
                 i++;
                 // TODO - should this be restored?
             }
 
             if (saveConfig.saveBytes()) {
                 field = CSV_BYTES;
                 text = parts[i++];
                 result.setBytes(Integer.parseInt(text));
             }
 
             if (saveConfig.saveThreadCounts()) {
                 field = CSV_THREAD_COUNT1;
                 text = parts[i++];
                 result.setGroupThreads(Integer.parseInt(text));
 
                 field = CSV_THREAD_COUNT2;
                 text = parts[i++];
                 result.setAllThreads(Integer.parseInt(text));
             }
 
             if (saveConfig.saveUrl()) {
                 i++;
                 // TODO: should this be restored?
             }
 
             if (saveConfig.saveFileName()) {
                 field = CSV_FILENAME;
                 text = parts[i++];
                 result.setResultFileName(text);
             }
             if (saveConfig.saveLatency()) {
                 field = CSV_LATENCY;
                 text = parts[i++];
                 result.setLatency(Long.parseLong(text));
             }
 
             if (saveConfig.saveEncoding()) {
                 field = CSV_ENCODING;
                 text = parts[i++];
                 result.setEncodingAndType(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 field = CSV_SAMPLE_COUNT;
                 text = parts[i++];
                 result.setSampleCount(Integer.parseInt(text));
                 field = CSV_ERROR_COUNT;
                 text = parts[i++];
                 result.setErrorCount(Integer.parseInt(text));
             }
 
             if (saveConfig.saveHostname()) {
                 field = CSV_HOSTNAME;
                 hostname = parts[i++];
             }
 
             if (saveConfig.saveIdleTime()) {
                 field = CSV_IDLETIME;
                 text = parts[i++];
                 result.setIdleTime(Long.parseLong(text));
             }
 
             if (i + saveConfig.getVarCount() < parts.length) {
                 log.warn("Line: " + lineNumber + ". Found " + parts.length
                         + " fields, expected " + i
                         + ". Extra fields have been ignored.");
             }
 
         } catch (NumberFormatException e) {
             log.warn("Error parsing field '" + field + "' at line "
                     + lineNumber + " " + e);
             throw new JMeterError(e);
         } catch (ParseException e) {
             log.warn("Error parsing field '" + field + "' at line "
                     + lineNumber + " " + e);
             throw new JMeterError(e);
         } catch (ArrayIndexOutOfBoundsException e) {
             log.warn("Insufficient columns to parse field '" + field
                     + "' at line " + lineNumber);
             throw new JMeterError(e);
         }
         return new SampleEvent(result, "", hostname);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @return the field names as a string
      */
     public static String printableFieldNamesToString() {
         return printableFieldNamesToString(_saveConfig);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @return the field names as a string
      */
     public static String printableFieldNamesToString(
             SampleSaveConfiguration saveConfig) {
         StringBuilder text = new StringBuilder();
         String delim = saveConfig.getDelimiter();
 
         if (saveConfig.saveTimestamp()) {
             text.append(TIME_STAMP);
             text.append(delim);
         }
 
         if (saveConfig.saveTime()) {
             text.append(CSV_ELAPSED);
             text.append(delim);
         }
 
         if (saveConfig.saveLabel()) {
             text.append(LABEL);
             text.append(delim);
         }
 
         if (saveConfig.saveCode()) {
             text.append(RESPONSE_CODE);
             text.append(delim);
         }
 
         if (saveConfig.saveMessage()) {
             text.append(RESPONSE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(THREAD_NAME);
             text.append(delim);
         }
 
         if (saveConfig.saveDataType()) {
             text.append(DATA_TYPE);
             text.append(delim);
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(SUCCESSFUL);
             text.append(delim);
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             text.append(FAILURE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveBytes()) {
             text.append(CSV_BYTES);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(CSV_THREAD_COUNT1);
             text.append(delim);
             text.append(CSV_THREAD_COUNT2);
             text.append(delim);
         }
 
         if (saveConfig.saveUrl()) {
             text.append(CSV_URL);
             text.append(delim);
         }
 
         if (saveConfig.saveFileName()) {
             text.append(CSV_FILENAME);
             text.append(delim);
         }
 
         if (saveConfig.saveLatency()) {
             text.append(CSV_LATENCY);
             text.append(delim);
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(CSV_ENCODING);
             text.append(delim);
         }
 
         if (saveConfig.saveSampleCount()) {
             text.append(CSV_SAMPLE_COUNT);
             text.append(delim);
             text.append(CSV_ERROR_COUNT);
             text.append(delim);
         }
 
         if (saveConfig.saveHostname()) {
             text.append(CSV_HOSTNAME);
             text.append(delim);
         }
 
         if (saveConfig.saveIdleTime()) {
             text.append(CSV_IDLETIME);
             text.append(delim);
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(SampleEvent.getVarName(i));
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(delim);
         }
 
         String resultString = null;
         int size = text.length();
         int delSize = delim.length();
 
         // Strip off the trailing delimiter
         if (size >= delSize) {
             resultString = text.substring(0, size - delSize);
         } else {
             resultString = text.toString();
         }
         return resultString;
     }
 
     // Map header names to set() methods
     private static final LinkedMap headerLabelMethods = new LinkedMap();
 
     // These entries must be in the same order as columns are saved/restored.
 
     static {
         headerLabelMethods.put(TIME_STAMP, new Functor("setTimestamp"));
         headerLabelMethods.put(CSV_ELAPSED, new Functor("setTime"));
         headerLabelMethods.put(LABEL, new Functor("setLabel"));
         headerLabelMethods.put(RESPONSE_CODE, new Functor("setCode"));
         headerLabelMethods.put(RESPONSE_MESSAGE, new Functor("setMessage"));
         headerLabelMethods.put(THREAD_NAME, new Functor("setThreadName"));
         headerLabelMethods.put(DATA_TYPE, new Functor("setDataType"));
         headerLabelMethods.put(SUCCESSFUL, new Functor("setSuccess"));
         headerLabelMethods.put(FAILURE_MESSAGE, new Functor(
                 "setAssertionResultsFailureMessage"));
         headerLabelMethods.put(CSV_BYTES, new Functor("setBytes"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_THREAD_COUNT1,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_THREAD_COUNT2,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_URL, new Functor("setUrl"));
         headerLabelMethods.put(CSV_FILENAME, new Functor("setFileName"));
         headerLabelMethods.put(CSV_LATENCY, new Functor("setLatency"));
         headerLabelMethods.put(CSV_ENCODING, new Functor("setEncoding"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_SAMPLE_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_ERROR_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_HOSTNAME, new Functor("setHostname"));
         headerLabelMethods.put(CSV_IDLETIME, new Functor("setIdleTime"));
     }
 
     /**
      * Parse a CSV header line
      * 
      * @param headerLine
      *            from CSV file
      * @param filename
      *            name of file (for log message only)
      * @return config corresponding to the header items found or null if not a
      *         header line
      */
     public static SampleSaveConfiguration getSampleSaveConfiguration(
             String headerLine, String filename) {
         String[] parts = splitHeader(headerLine, _saveConfig.getDelimiter()); // Try
                                                                               // default
                                                                               // delimiter
 
         String delim = null;
 
         if (parts == null) {
             Perl5Matcher matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = new PatternMatcherInput(headerLine);
             Pattern pattern = JMeterUtils.getPatternCache()
             // This assumes the header names are all single words with no spaces
             // word followed by 0 or more repeats of (non-word char + word)
             // where the non-word char (\2) is the same
             // e.g. abc|def|ghi but not abd|def~ghi
                     .getPattern("\\w+((\\W)\\w+)?(\\2\\w+)*(\\2\"\\w+\")*", // $NON-NLS-1$
                             // last entries may be quoted strings
                             Perl5Compiler.READ_ONLY_MASK);
             if (matcher.matches(input, pattern)) {
                 delim = matcher.getMatch().group(2);
                 parts = splitHeader(headerLine, delim);// now validate the
                                                        // result
             }
         }
 
         if (parts == null) {
             return null; // failed to recognise the header
         }
 
         // We know the column names all exist, so create the config
         SampleSaveConfiguration saveConfig = new SampleSaveConfiguration(false);
 
         int varCount = 0;
         for (int i = 0; i < parts.length; i++) {
             String label = parts[i];
             if (isVariableName(label)) {
                 varCount++;
             } else {
                 Functor set = (Functor) headerLabelMethods.get(label);
                 set.invoke(saveConfig, new Boolean[] { Boolean.TRUE });
             }
         }
 
         if (delim != null) {
             log.warn("Default delimiter '" + _saveConfig.getDelimiter()
                     + "' did not work; using alternate '" + delim
                     + "' for reading " + filename);
             saveConfig.setDelimiter(delim);
         }
 
         saveConfig.setVarCount(varCount);
 
         return saveConfig;
     }
 
     private static String[] splitHeader(String headerLine, String delim) {
         String parts[] = headerLine.split("\\Q" + delim);// $NON-NLS-1$
         int previous = -1;
         // Check if the line is a header
         for (int i = 0; i < parts.length; i++) {
             final String label = parts[i];
             // Check for Quoted variable names
             if (isVariableName(label)) {
                 previous = Integer.MAX_VALUE; // they are always last
                 continue;
             }
             int current = headerLabelMethods.indexOf(label);
             if (current == -1) {
                 return null; // unknown column name
             }
             if (current <= previous) {
                 log.warn("Column header number " + (i + 1) + " name " + label
                         + " is out of order.");
                 return null; // out of order
             }
             previous = current;
         }
         return parts;
     }
 
     /**
      * Check if the label is a variable name, i.e. is it enclosed in
      * double-quotes?
      * 
      * @param label
      *            column name from CSV file
      * @return if the label is enclosed in double-quotes
      */
     private static boolean isVariableName(final String label) {
         return label.length() > 2 && label.startsWith(VARIABLE_NAME_QUOTE_CHAR)
                 && label.endsWith(VARIABLE_NAME_QUOTE_CHAR);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output file
      * @throws IOException
      */
     public static void saveCSVStats(List<?> data, FileWriter writer)
             throws IOException {
         saveCSVStats(data, writer, null);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output file
      * @param headers
      *            header names (if non-null)
      * @throws IOException
      */
     public static void saveCSVStats(List<?> data, FileWriter writer,
             String headers[]) throws IOException {
         final char DELIM = ',';
         final char SPECIALS[] = new char[] { DELIM, QUOTING_CHAR };
         if (headers != null) {
             for (int i = 0; i < headers.length; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(headers[i], SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (int idx = 0; idx < data.size(); idx++) {
             List<?> row = (List<?>) data.get(idx);
             for (int idy = 0; idy < row.size(); idy++) {
                 if (idy > 0) {
                     writer.write(DELIM);
                 }
                 Object item = row.get(idy);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Method saves aggregate statistics (with header names) as CSV from a table
      * model. Same as {@link #saveCSVStats(List, FileWriter, String[])} except
      * that there is no need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @throws IOException
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer)
             throws IOException {
         saveCSVStats(model, writer, true);
     }
 
     /**
      * Method saves aggregate statistics as CSV from a table model. Same as
      * {@link #saveCSVStats(List, FileWriter, String[])} except that there is
      * no need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @param saveHeaders
      *            whether or not to save headers
      * @throws IOException
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer,
             boolean saveHeaders) throws IOException {
         final char DELIM = ',';
         final char SPECIALS[] = new char[] { DELIM, QUOTING_CHAR };
         final int columns = model.getColumnCount();
         final int rows = model.getRowCount();
         if (saveHeaders) {
             for (int i = 0; i < columns; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(model.getColumnName(i), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (int row = 0; row < rows; row++) {
             for (int column = 0; column < columns; column++) {
                 if (column > 0) {
                     writer.write(DELIM);
                 }
                 Object item = model.getValueAt(row, column);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by the default delimiter.
      * 
      * @param event
      *            the sample event to be converted
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event) {
         return resultToDelimitedString(event, event.getResult().getSaveConfig()
                 .getDelimiter());
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by a specified String.
      * 
      * @param event
      *            the sample event to be converted
      * @param delimiter
      *            the separation string
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event,
             final String delimiter) {
 
         /*
          * Class to handle generating the delimited string. - adds the delimiter
          * if not the first call - quotes any strings that require it
          */
         final class StringQuoter {
             final StringBuilder sb = new StringBuilder();
             private final char[] specials;
             private boolean addDelim;
 
             public StringQuoter(char delim) {
                 specials = new char[] { delim, QUOTING_CHAR, CharUtils.CR,
                         CharUtils.LF };
                 addDelim = false; // Don't add delimiter first time round
             }
 
             private void addDelim() {
                 if (addDelim) {
                     sb.append(specials[0]);
                 } else {
                     addDelim = true;
                 }
             }
 
             // These methods handle parameters that could contain delimiters or
             // quotes:
             public void append(String s) {
                 addDelim();
                 // if (s == null) return;
                 sb.append(quoteDelimiters(s, specials));
             }
 
             public void append(Object obj) {
                 append(String.valueOf(obj));
             }
 
             // These methods handle parameters that cannot contain delimiters or
             // quotes
             public void append(int i) {
                 addDelim();
                 sb.append(i);
             }
 
             public void append(long l) {
                 addDelim();
                 sb.append(l);
             }
 
             public void append(boolean b) {
                 addDelim();
                 sb.append(b);
             }
 
             @Override
             public String toString() {
                 return sb.toString();
             }
         }
 
         StringQuoter text = new StringQuoter(delimiter.charAt(0));
 
         SampleResult sample = event.getResult();
         SampleSaveConfiguration saveConfig = sample.getSaveConfig();
 
         if (saveConfig.saveTimestamp()) {
             if (saveConfig.printMilliseconds()) {
                 text.append(sample.getTimeStamp());
             } else if (saveConfig.formatter() != null) {
                 String stamp = saveConfig.formatter().format(
                         new Date(sample.getTimeStamp()));
                 text.append(stamp);
             }
         }
 
         if (saveConfig.saveTime()) {
             text.append(sample.getTime());
         }
 
         if (saveConfig.saveLabel()) {
             text.append(sample.getSampleLabel());
         }
 
         if (saveConfig.saveCode()) {
             text.append(sample.getResponseCode());
         }
 
         if (saveConfig.saveMessage()) {
             text.append(sample.getResponseMessage());
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(sample.getThreadName());
         }
 
         if (saveConfig.saveDataType()) {
             text.append(sample.getDataType());
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(sample.isSuccessful());
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             String message = null;
             AssertionResult[] results = sample.getAssertionResults();
 
             if (results != null) {
                 // Find the first non-null message
                 for (int i = 0; i < results.length; i++) {
                     message = results[i].getFailureMessage();
                     if (message != null) {
                         break;
                     }
                 }
             }
 
             if (message != null) {
                 text.append(message);
             } else {
                 text.append(""); // Need to append something so delimiter is
                                  // added
             }
         }
 
         if (saveConfig.saveBytes()) {
             text.append(sample.getBytes());
         }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(sample.getGroupThreads());
             text.append(sample.getAllThreads());
         }
         if (saveConfig.saveUrl()) {
             text.append(sample.getURL());
         }
 
         if (saveConfig.saveFileName()) {
             text.append(sample.getResultFileName());
         }
 
         if (saveConfig.saveLatency()) {
             text.append(sample.getLatency());
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(sample.getDataEncodingWithDefault());
         }
 
         if (saveConfig.saveSampleCount()) {// Need both sample and error count
                                            // to be any use
             text.append(sample.getSampleCount());
             text.append(sample.getErrorCount());
         }
 
         if (saveConfig.saveHostname()) {
             text.append(event.getHostname());
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(event.getVarValue(i));
         }
 
         return text.toString();
     }
 
     // =================================== CSV quote/unquote handling
     // ==============================
 
     /*
      * Private versions of what might eventually be part of Commons-CSV or
      * Commons-Lang/Io...
      */
 
     /*
      * <p> Returns a <code>String</code> value for a character-delimited column
      * value enclosed in the quote character, if required. </p>
      * 
      * <p> If the value contains a special character, then the String value is
      * returned enclosed in the quote character. </p>
      * 
      * <p> Any quote characters in the value are doubled up. </p>
      * 
      * <p> If the value does not contain any special characters, then the String
      * value is returned unchanged. </p>
      * 
      * <p> N.B. The list of special characters includes the quote character.
      * </p>
      * 
      * @param input the input column String, may be null (without enclosing
      * delimiters)
      * 
      * @param specialChars special characters; second one must be the quote
      * character
      * 
      * @return the input String, enclosed in quote characters if the value
      * contains a special character, <code>null</code> for null string input
      */
     private static String quoteDelimiters(String input, char[] specialChars) {
         if (StringUtils.containsNone(input, specialChars)) {
             return input;
         }
         StringBuilder buffer = new StringBuilder(input.length() + 10);
         final char quote = specialChars[1];
         buffer.append(quote);
         for (int i = 0; i < input.length(); i++) {
             char c = input.charAt(i);
             if (c == quote) {
                 buffer.append(quote); // double the quote char
             }
             buffer.append(c);
         }
         buffer.append(quote);
         return buffer.toString();
     }
 
     // State of the parser
     private static final int INITIAL = 0, PLAIN = 1, QUOTED = 2,
             EMBEDDEDQUOTE = 3;
     public static final char QUOTING_CHAR = '"';
 
     /**
      * Reads from file and splits input into strings according to the delimiter,
      * taking note of quoted strings.
      * <p>
      * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
      * <p>
      * N.B. a blank line is returned as a zero length array, whereas "" is
      * returned as an empty string. This is inconsistent.
      * 
      * @param infile
      *            input file - must support mark(1)
      * @param delim
      *            delimiter (e.g. comma)
      * @return array of strings
      * @throws IOException
      *             also for unexpected quote characters
      */
     public static String[] csvReadFile(BufferedReader infile, char delim)
             throws IOException {
         int ch;
         int state = INITIAL;
         List<String> list = new ArrayList<String>();
         CharArrayWriter baos = new CharArrayWriter(200);
         boolean push = false;
         while (-1 != (ch = infile.read())) {
             push = false;
             switch (state) {
             case INITIAL:
                 if (ch == QUOTING_CHAR) {
                     state = QUOTED;
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                 } else {
                     baos.write(ch);
                     state = PLAIN;
                 }
                 break;
             case PLAIN:
                 if (ch == QUOTING_CHAR) {
                     baos.write(ch);
                     throw new IOException(
                             "Cannot have quote-char in plain field:["
                                     + baos.toString() + "]");
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                     state = INITIAL;
                 } else {
                     baos.write(ch);
                 }
                 break;
             case QUOTED:
diff --git a/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java b/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
index 611a5e1a8..6e9d293c8 100644
--- a/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
+++ b/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
@@ -1,99 +1,99 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.xml.utils.PrefixResolver;
 import org.apache.xml.utils.PrefixResolverDefault;
 import org.w3c.dom.Node;
 
 /**
  * {@link PrefixResolver} implementation that loads prefix configuration from jmeter property xpath.namespace.config
  */
 public class PropertiesBasedPrefixResolver extends PrefixResolverDefault {
 	private static final Logger logger = LoggingManager.getLoggerForClass();
 	private static final String XPATH_NAMESPACE_CONFIG = "xpath.namespace.config";
 	private static final Map<String, String> NAMESPACE_MAP = new HashMap<String, String>();
 	static {
 		String pathToNamespaceConfig = JMeterUtils.getPropDefault(XPATH_NAMESPACE_CONFIG, "");
 		if(!StringUtils.isEmpty(pathToNamespaceConfig)) {
 			Properties properties = new Properties();
 			InputStream inputStream = null;
 			try {
 				File pathToNamespaceConfigFile = JMeterUtils.findFile(pathToNamespaceConfig);
 				if(!pathToNamespaceConfigFile.exists()) {
 					logger.error("Cannot find configured file:'"+
 							pathToNamespaceConfig+"' in property:'"+XPATH_NAMESPACE_CONFIG+"', file does not exist");
 				} else { 
 					if(!pathToNamespaceConfigFile.canRead()) {
 						logger.error("Cannot read configured file:'"+
 								pathToNamespaceConfig+"' in property:'"+XPATH_NAMESPACE_CONFIG+"'");
 					} else {
 						inputStream = new BufferedInputStream(new FileInputStream(pathToNamespaceConfigFile));
 						properties.load(inputStream);
 						properties.entrySet();
 						for (Map.Entry<Object, Object> entry : properties.entrySet()) {
 							NAMESPACE_MAP.put((String) entry.getKey(), (String) entry.getValue());					
 						}
 						logger.info("Read following XPath namespace configuration "+ 
 								NAMESPACE_MAP);
 					}
 				}
 			} catch(IOException e) {
 				logger.error("Error loading namespaces from file:'"+
 						pathToNamespaceConfig+"', message:"+e.getMessage(),e);
 			} finally {
 				JOrphanUtils.closeQuietly(inputStream);
 			}
 		}
 	}
 	/**
 	 * @param xpathExpressionContext Node
 	 */
 	public PropertiesBasedPrefixResolver(Node xpathExpressionContext) {
 		super(xpathExpressionContext);
 	}
 
 	/**
 	 * Searches prefix in NAMESPACE_MAP, if it fails to find it defaults to parent implementation
 	 * @param prefix Prefix
 	 * @param namespaceContext Node
 	 */
 	@Override
 	public String getNamespaceForPrefix(String prefix, Node namespaceContext) {
 		String namespace = NAMESPACE_MAP.get(prefix);
 		if(namespace==null) {
 			return super.getNamespaceForPrefix(prefix, namespaceContext);
 		} else {
 			return namespace;
 		}
 	}
 }
\ No newline at end of file
diff --git a/src/functions/org/apache/jmeter/functions/EscapeHtml.java b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
index 4010e6c32..b546f0a44 100644
--- a/src/functions/org/apache/jmeter/functions/EscapeHtml.java
+++ b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
@@ -1,92 +1,92 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * <p>Function which escapes the characters in a <code>String</code> using HTML entities.</p>
  *
  * <p>
  * For example:
  * </p> 
  * <p><code>"bread" & "butter"</code></p>
  * becomes:
  * <p>
  * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
  * </p>
  *
  * <p>Supports all known HTML 4.0 entities.
  * Note that the commonly used apostrophe escape character (&amp;apos;)
  * is not a legal entity and so is not supported). </p>
  * 
  * @see StringEscapeUtils#escapeHtml(String) (Commons Lang)
  * @since 2.3.3
  */
 public class EscapeHtml extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__escapeHtml"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("escape_html_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public EscapeHtml() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String rawString = ((CompoundVariable) values[0]).execute();
-        return StringEscapeUtils.escapeHtml(rawString);
+        return StringEscapeUtils.escapeHtml4(rawString);
 
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/RandomString.java b/src/functions/org/apache/jmeter/functions/RandomString.java
index 27068d8aa..81de57000 100644
--- a/src/functions/org/apache/jmeter/functions/RandomString.java
+++ b/src/functions/org/apache/jmeter/functions/RandomString.java
@@ -1,130 +1,130 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
-import org.apache.commons.lang.RandomStringUtils;
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.RandomStringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Provides a RandomString function which returns a random String of length (first argument) 
  * using characters (second argument)
  * @since 2.6
  */
 public class RandomString extends AbstractFunction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__RandomString"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("random_string_length")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("random_string_chars_to_use")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private CompoundVariable[] values;
 
     private static final int MAX_PARAM_COUNT = 3;
 
     private static final int MIN_PARAM_COUNT = 1;
     
     private static final int CHARS = 2;
 
     private static final int PARAM_NAME = 3;
 
     /**
      * No-arg constructor.
      */
     public RandomString() {
         super();
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         int length = Integer.parseInt(values[0].execute());
 
         String charsToUse = null;//means no restriction
         if (values.length >= CHARS) {
             charsToUse = (values[CHARS - 1]).execute().trim();
             if (charsToUse.length() <= 0) { // empty chars, return to null
                 charsToUse = null;
             }
         }
 
         String myName = "";//$NON-NLS-1$
         if (values.length >= PARAM_NAME) {
             myName = (values[PARAM_NAME - 1]).execute().trim();
         }
 
         String myValue = null;
         if(StringUtils.isEmpty(charsToUse)) {
             myValue = RandomStringUtils.random(length);
         } else {
             myValue = RandomStringUtils.random(length, charsToUse);
         }
  
         if (myName.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null) {// Can be null if called from Config item testEnded() method
                 vars.put(myName, myValue);
             }
         }
 
         if (log.isDebugEnabled()) {
             String tn = Thread.currentThread().getName();
             log.debug(tn + " name:" //$NON-NLS-1$
                     + myName + " value:" + myValue);//$NON-NLS-1$
         }
 
         return myValue;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
         values = parameters.toArray(new CompoundVariable[parameters.size()]);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
\ No newline at end of file
diff --git a/src/functions/org/apache/jmeter/functions/UnEscape.java b/src/functions/org/apache/jmeter/functions/UnEscape.java
index f21fa5a7e..f78229fb1 100644
--- a/src/functions/org/apache/jmeter/functions/UnEscape.java
+++ b/src/functions/org/apache/jmeter/functions/UnEscape.java
@@ -1,82 +1,82 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.functions;
 
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to unescape any Java literals found in the String.
  * For example, it will turn a sequence of '\' and 'n' into a newline character,
  * unless the '\' is preceded by another '\'.
  * 
  * @see StringEscapeUtils#unescapeJava(String)
  * @since 2.3.3
  */
 public class UnEscape extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__unescape"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("unescape_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UnEscape() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String rawString = ((CompoundVariable) values[0]).execute();
         return StringEscapeUtils.unescapeJava(rawString);
 
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java b/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
index 5bb772b5e..4d6054926 100644
--- a/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
+++ b/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
@@ -1,88 +1,88 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.functions;
 
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to unescape a string containing entity escapes
  * to a string containing the actual Unicode characters corresponding to the escapes. 
  * Supports HTML 4.0 entities.
  * <p>
  * For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;" will become "&lt;Fran&ccedil;ais&gt;"
  * </p>
  * <p>
  * If an entity is unrecognized, it is left alone, and inserted verbatim into the result string.
  * e.g. "&amp;gt;&amp;zzzz;x" will become "&gt;&amp;zzzz;x".
  * </p>
- * @see org.apache.commons.lang.StringEscapeUtils#unescapeHtml(String)
+ * @see org.apache.commons.lang3.StringEscapeUtils#unescapeHtml(String)
  * @since 2.3.3
  */
 public class UnEscapeHtml extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__unescapeHtml"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("unescape_html_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UnEscapeHtml() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String escapedString = ((CompoundVariable) values[0]).execute();
-        return StringEscapeUtils.unescapeHtml(escapedString);
+        return StringEscapeUtils.unescapeHtml4(escapedString);
 
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/math/StatCalculator.java b/src/jorphan/org/apache/jorphan/math/StatCalculator.java
index 4388bb966..e5cdb4d4e 100644
--- a/src/jorphan/org/apache/jorphan/math/StatCalculator.java
+++ b/src/jorphan/org/apache/jorphan/math/StatCalculator.java
@@ -1,271 +1,271 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jorphan.math;
 
 import java.util.ConcurrentModificationException;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.TreeMap;
 import java.util.Map.Entry;
 
-import org.apache.commons.lang.mutable.MutableLong;
+import org.apache.commons.lang3.mutable.MutableLong;
 
 /**
  * This class serves as a way to calculate the median, max, min etc. of a list of values.
  * It is not threadsafe.
  *
  */
 public abstract class StatCalculator<T extends Number & Comparable<? super T>> {
 
     // key is the type to collect (usually long), value = count of entries
     private final TreeMap<T, MutableLong> valuesMap = new TreeMap<T, MutableLong>();
     // We use a TreeMap because we need the entries to be sorted
 
     // Running values, updated for each sample
     private double sum = 0;
 
     private double sumOfSquares = 0;
 
     private double mean = 0;
 
     private double deviation = 0;
 
     private long count = 0;
 
     private T min;
 
     private T max;
 
     private long bytes = 0;
 
     private final T ZERO;
 
     private final T MAX_VALUE; // e.g. Long.MAX_VALUE
 
     private final T MIN_VALUE; // e.g. Long.MIN_VALUE
 
     /**
      * This constructor is used to set up particular values for the generic class instance.
      *
      * @param zero - value to return for Median and PercentPoint if there are no values
      * @param min - value to return for minimum if there are no values
      * @param max - value to return for maximum if there are no values
      */
     public StatCalculator(final T zero, final T min, final T max) {
         super();
         ZERO = zero;
         MAX_VALUE = max;
         MIN_VALUE = min;
         this.min = MAX_VALUE;
         this.max = MIN_VALUE;
     }
 
     public void clear() {
         valuesMap.clear();
         sum = 0;
         sumOfSquares = 0;
         mean = 0;
         deviation = 0;
         count = 0;
         bytes = 0;
         max = MIN_VALUE;
         min = MAX_VALUE;
     }
 
 
     public void addBytes(long newValue) {
         bytes += newValue;
     }
 
     public void addAll(StatCalculator<T> calc) {
         for(Entry<T, MutableLong> ent : calc.valuesMap.entrySet()) {
             addEachValue(ent.getKey(), ent.getValue().longValue());
         }
     }
 
     public T getMedian() {
         return getPercentPoint(0.5);
     }
 
     public long getTotalBytes() {
         return bytes;
     }
 
     /**
      * Get the value which %percent% of the values are less than. This works
      * just like median (where median represents the 50% point). A typical
      * desire is to see the 90% point - the value that 90% of the data points
      * are below, the remaining 10% are above.
      *
      * @param percent
      * @return number of values less than the percentage
      */
     public T getPercentPoint(float percent) {
         return getPercentPoint((double) percent);
     }
 
     /**
      * Get the value which %percent% of the values are less than. This works
      * just like median (where median represents the 50% point). A typical
      * desire is to see the 90% point - the value that 90% of the data points
      * are below, the remaining 10% are above.
      *
      * @param percent
      * @return the value which %percent% of the values are less than
      */
     public T getPercentPoint(double percent) {
         if (count <= 0) {
                 return ZERO;
         }
         if (percent >= 1.0) {
             return getMax();
         }
 
         // use Math.round () instead of simple (long) to provide correct value rounding
         long target = Math.round (count * percent);
         try {
             for (Entry<T, MutableLong> val : valuesMap.entrySet()) {
                 target -= val.getValue().longValue();
                 if (target <= 0){
                     return val.getKey();
                 }
             }
         } catch (ConcurrentModificationException ignored) {
             // ignored. May happen occasionally, but no harm done if so.
         }
         return ZERO; // TODO should this be getMin()?
     }
 
     /**
      * Returns the distribution of the values in the list.
      *
      * @return map containing either Integer or Long keys; entries are a Number array containing the key and the [Integer] count.
      * TODO - why is the key value also stored in the entry array?
      */
     public synchronized Map<Number, Number[]> getDistribution() {
         HashMap<Number, Number[]> items = new HashMap <Number, Number[]> ();
         Number[] dis;
 
         for (T nx : valuesMap.keySet()) {
             dis = new Number[2];
             dis[0] = nx;
             dis[1] = valuesMap.get(nx);
             items.put(nx, dis);
         }
         return items;
     }
 
     public double getMean() {
         return mean;
     }
 
     public double getStandardDeviation() {
         return deviation;
     }
 
     public T getMin() {
         return min;
     }
 
     public T getMax() {
         return max;
     }
 
     public long getCount() {
         return count;
     }
 
     public double getSum() {
         return sum;
     }
 
     protected abstract T divide(T val, int n);
 
     protected abstract T divide(T val, long n);
 
     /**
      * Update the calculator with the values for a set of samples.
      * 
      * @param val the common value, normally the elapsed time
      * @param sampleCount the number of samples with the same value
      */
     void addEachValue(T val, long sampleCount) {
         count += sampleCount;
         double currentVal = val.doubleValue();
         sum += currentVal * sampleCount;
         // For n same values in sum of square is equal to n*val^2
         sumOfSquares += currentVal * currentVal * sampleCount;
         updateValueCount(val, sampleCount);
         calculateDerivedValues(val);
     }
 
     /**
      * Update the calculator with the value for an aggregated sample.
      * 
      * @param val the aggregate value, normally the elapsed time
      * @param sampleCount the number of samples contributing to the aggregate value
      */
     public void addValue(T val, long sampleCount) {
         count += sampleCount;
         double currentVal = val.doubleValue();
         sum += currentVal;
         T actualValue = val;
         if (sampleCount > 1){
             // For n values in an aggregate sample the average value = (val/n)
             // So need to add n * (val/n) * (val/n) = val * val / n
             sumOfSquares += currentVal * currentVal / sampleCount;
             actualValue = divide(val, sampleCount);
         } else { // no need to divide by 1
             sumOfSquares += currentVal * currentVal;
         }
         updateValueCount(actualValue, sampleCount);
         calculateDerivedValues(actualValue);
     }
 
     private void calculateDerivedValues(T actualValue) {
         mean = sum / count;
         deviation = Math.sqrt((sumOfSquares / count) - (mean * mean));
         if (actualValue.compareTo(max) > 0){
             max=actualValue;
         }
         if (actualValue.compareTo(min) < 0){
             min=actualValue;
         }
     }
 
     /**
      * Add a single value (normally elapsed time)
      * 
      * @param val the value to add, which should correspond with a single sample
      * @see #addValue(Number, long)
      */
     public void addValue(T val) {
         addValue(val, 1L);
     }
 
     private void updateValueCount(T actualValue, long sampleCount) {
         MutableLong count = valuesMap.get(actualValue);
         if (count != null) {
             count.add(sampleCount);
         } else {
             // insert new value
             valuesMap.put(actualValue, new MutableLong(sampleCount));
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/reflect/ClassTools.java b/src/jorphan/org/apache/jorphan/reflect/ClassTools.java
index 4e884defc..7488c4108 100644
--- a/src/jorphan/org/apache/jorphan/reflect/ClassTools.java
+++ b/src/jorphan/org/apache/jorphan/reflect/ClassTools.java
@@ -1,137 +1,137 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jorphan.reflect;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
-import org.apache.commons.lang.ClassUtils;
+import org.apache.commons.lang3.ClassUtils;
 import org.apache.jorphan.util.JMeterException;
 
 /**
  * Utility methods for handling dynamic access to classes.
  */
 public class ClassTools {
 
 
     /**
      * Call no-args constructor for a class.
      *
      * @param className
      * @return an instance of the class
      * @throws JMeterException if class cannot be created
      */
     public static Object construct(String className) throws JMeterException {
         Object instance = null;
         try {
             instance = ClassUtils.getClass(className).newInstance();
         } catch (ClassNotFoundException e) {
             throw new JMeterException(e);
         } catch (InstantiationException e) {
             throw new JMeterException(e);
         } catch (IllegalAccessException e) {
             throw new JMeterException(e);
         }
         return instance;
     }
 
     /**
      * Call a class constructor with an integer parameter
      * @param className
      * @param parameter (integer)
      * @return an instance of the class
      * @throws JMeterException if class cannot be created
      */
     public static Object construct(String className, int parameter) throws JMeterException
     {
         Object instance = null;
         try {
             Class<?> clazz = ClassUtils.getClass(className);
             clazz.getConstructor(new Class [] {Integer.TYPE});
             instance = ClassUtils.getClass(className).newInstance();
         } catch (ClassNotFoundException e) {
             throw new JMeterException(e);
         } catch (InstantiationException e) {
             throw new JMeterException(e);
         } catch (IllegalAccessException e) {
             throw new JMeterException(e);
         } catch (SecurityException e) {
             throw new JMeterException(e);
         } catch (NoSuchMethodException e) {
             throw new JMeterException(e);
         }
         return instance;
     }
 
     /**
      * Call a class constructor with an String parameter
      * @param className
      * @param parameter (String)
      * @return an instance of the class
      * @throws JMeterException if class cannot be created
      */
     public static Object construct(String className, String parameter)
             throws JMeterException {
         Object instance = null;
         try {
             Class<?> clazz = Class.forName(className);
             Constructor<?> constructor = clazz.getConstructor(String.class);
             instance = constructor.newInstance(parameter);
         } catch (ClassNotFoundException e) {
             throw new JMeterException(e);
         } catch (InstantiationException e) {
             throw new JMeterException(e);
         } catch (IllegalAccessException e) {
             throw new JMeterException(e);
         } catch (NoSuchMethodException e) {
             throw new JMeterException(e);
         } catch (IllegalArgumentException e) {
             throw new JMeterException(e);
         } catch (InvocationTargetException e) {
             throw new JMeterException(e);
         }
         return instance;
     }
 
     /**
      * Invoke a public method on a class instance
      *
      * @param instance
      * @param methodName
      * @throws SecurityException
      * @throws IllegalArgumentException
      * @throws JMeterException
      */
     public static void invoke(Object instance, String methodName)
     throws SecurityException, IllegalArgumentException, JMeterException
     {
         Method m;
         try {
             m = ClassUtils.getPublicMethod(instance.getClass(), methodName, new Class [] {});
             m.invoke(instance, (Object [])null);
         } catch (NoSuchMethodException e) {
             throw new JMeterException(e);
         } catch (IllegalAccessException e) {
             throw new JMeterException(e);
         } catch (InvocationTargetException e) {
             throw new JMeterException(e);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
index a38e0848a..ee0054196 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
@@ -1,211 +1,211 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 public class HttpDefaultsGui extends AbstractConfigGui {
 
     private static final long serialVersionUID = 240L;
 
     private JCheckBox imageParser;
     
     private JCheckBox concurrentDwn;
     
     private JTextField concurrentPool; 
 
     private UrlConfigGui urlConfig;
 
     private JLabeledTextField embeddedRE; // regular expression used to match against embedded resource URLs
 
     public HttpDefaultsGui() {
         super();
         init();
     }
 
     public String getLabelResource() {
         return "url_config_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     public TestElement createTestElement() {
         ConfigTestElement config = new ConfigTestElement();
         modifyTestElement(config);
         return config;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     public void modifyTestElement(TestElement config) {
         ConfigTestElement cfg = (ConfigTestElement ) config;
         ConfigTestElement el = (ConfigTestElement) urlConfig.createTestElement();
         cfg.clear(); // need to clear because the
         cfg.addConfigElement(el);
         super.configureTestElement(config);
         if (imageParser.isSelected()) {
             config.setProperty(new BooleanProperty(HTTPSamplerBase.IMAGE_PARSER, true));
             enableConcurrentDwn(true);
             if(!StringUtils.isEmpty(embeddedRE.getText())) {
                 config.setProperty(new StringProperty(HTTPSamplerBase.EMBEDDED_URL_RE, embeddedRE.getText()));            
             } else {
                 config.removeProperty(HTTPSamplerBase.EMBEDDED_URL_RE);
             }
         } else {
             config.removeProperty(HTTPSamplerBase.IMAGE_PARSER);
             config.removeProperty(HTTPSamplerBase.EMBEDDED_URL_RE);
             enableConcurrentDwn(false);
             
         }
         if (concurrentDwn.isSelected()) {
             config.setProperty(new BooleanProperty(HTTPSamplerBase.CONCURRENT_DWN, true));
         } else {
             // The default is false, so we can remove the property to simplify JMX files
             // This also allows HTTPDefaults to work for this checkbox
             config.removeProperty(HTTPSamplerBase.CONCURRENT_DWN);
         }
         if(!StringUtils.isEmpty(concurrentPool.getText())) {
         	config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
         			concurrentPool.getText()));
         } else {
         	config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
         			String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE)));
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         urlConfig.clear();
         imageParser.setSelected(false);
         concurrentDwn.setSelected(false);
         concurrentPool.setText(String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE));
         embeddedRE.setText(""); // $NON-NLS-1$
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         urlConfig.configure(el);
         imageParser.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.IMAGE_PARSER));
         concurrentDwn.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.CONCURRENT_DWN));
         concurrentPool.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.CONCURRENT_POOL));
         embeddedRE.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.EMBEDDED_URL_RE, ""));
     }
 
     private void init() {
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         urlConfig = new UrlConfigGui(false, true, false);
         add(urlConfig, BorderLayout.CENTER);
 
         // OPTIONAL TASKS
         final JPanel optionalTasksPanel = new VerticalPanel();
         optionalTasksPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("optional_tasks"))); // $NON-NLS-1$
 
         final JPanel checkBoxPanel = new HorizontalPanel();
         imageParser = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
         checkBoxPanel.add(imageParser);
         imageParser.addItemListener(new ItemListener() {
             public void itemStateChanged(final ItemEvent e) {
                 if (e.getStateChange() == ItemEvent.SELECTED) { enableConcurrentDwn(true); }
                 else { enableConcurrentDwn(false); }
             }
         });
         // Concurrent resources download
         concurrentDwn = new JCheckBox(JMeterUtils.getResString("web_testing_concurrent_download")); // $NON-NLS-1$
         concurrentDwn.addItemListener(new ItemListener() {
             public void itemStateChanged(final ItemEvent e) {
                 if (imageParser.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { concurrentPool.setEnabled(true); }
                 else { concurrentPool.setEnabled(false); }
             }
         });
         concurrentPool = new JTextField(2); // 2 columns size
         concurrentPool.setMaximumSize(new Dimension(30,20));
         checkBoxPanel.add(concurrentDwn);
         checkBoxPanel.add(concurrentPool);
         optionalTasksPanel.add(checkBoxPanel);
         
         // Embedded URL match regex
         embeddedRE = new JLabeledTextField(JMeterUtils.getResString("web_testing_embedded_url_pattern"),30); // $NON-NLS-1$
         optionalTasksPanel.add(embeddedRE);
 
         
         add(optionalTasksPanel, BorderLayout.SOUTH);
     }
 
     @Override
     public Dimension getPreferredSize() {
         return getMinimumSize();
     }
     
     private void enableConcurrentDwn(final boolean enable) {
         if (enable) {
             concurrentDwn.setEnabled(true);
             embeddedRE.setEnabled(true);
             if (concurrentDwn.isSelected()) {
                 concurrentPool.setEnabled(true);
             }
         } else {
             concurrentDwn.setEnabled(false);
             concurrentPool.setEnabled(false);
             embeddedRE.setEnabled(false);
         }
     }
 
     public void itemStateChanged(final ItemEvent event) {
         if (event.getStateChange() == ItemEvent.SELECTED) {
             enableConcurrentDwn(true);
         } else {
             enableConcurrentDwn(false);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 0fc84ff15..14039f089 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,744 +1,744 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FlowLayout;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextArea;
 
 /**
  * Basic URL / HTTP Request configuration:
  * - host and port
  * - connect and response timeouts
  * - path, method, encoding, parameters
  * - redirects & keepalive
  */
 public class UrlConfigGui extends JPanel implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final int TAB_PARAMETERS = 0;
     
     private static final int TAB_RAW_BODY = 1;
 
     private HTTPArgumentsPanel argsPanel;
 
     private JTextField domain;
 
     private JTextField port;
 
     private JTextField proxyHost;
 
     private JTextField proxyPort;
 
     private JTextField proxyUser;
 
     private JPasswordField proxyPass;
 
     private JTextField connectTimeOut;
 
     private JTextField responseTimeOut;
 
     private JTextField protocol;
 
     private JTextField contentEncoding;
 
     private JTextField path;
 
     private JCheckBox followRedirects;
 
     private JCheckBox autoRedirects;
 
     private JCheckBox useKeepAlive;
 
     private JCheckBox useMultipartForPost;
 
     private JCheckBox useBrowserCompatibleMultipartMode;
 
     private JLabeledChoice method;
     
     private JLabeledChoice httpImplementation;
 
     private final boolean notConfigOnly;
     // set this false to suppress some items for use in HTTP Request defaults
     
     private final boolean showImplementation; // Set false for AJP
 
     // Raw POST Body 
     private JLabeledTextArea postBodyContent;
 
     // Tabbed pane that contains parameters and raw body
     private ValidationTabbedPane postContentTabbedPane;
 
     private boolean showRawBodyPane;
 
     public UrlConfigGui() {
         this(true);
     }
 
     /**
      * @param showSamplerFields
      */
     public UrlConfigGui(boolean showSamplerFields) {
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
      * @param showImplementation Show HTTP Implementation
      * @param showRawBodyPane 
      */
     public UrlConfigGui(boolean showSamplerFields, boolean showImplementation, boolean showRawBodyPane) {
         notConfigOnly=showSamplerFields;
         this.showImplementation = showImplementation;
         this.showRawBodyPane = showRawBodyPane;
         init();
     }
 
     public void clear() {
         domain.setText(""); // $NON-NLS-1$
         if (notConfigOnly){
             followRedirects.setSelected(true);
             autoRedirects.setSelected(false);
             method.setText(HTTPSamplerBase.DEFAULT_METHOD);
             useKeepAlive.setSelected(true);
             useMultipartForPost.setSelected(false);
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             httpImplementation.setText(""); // $NON-NLS-1$
         }
         path.setText(""); // $NON-NLS-1$
         port.setText(""); // $NON-NLS-1$
         proxyHost.setText(""); // $NON-NLS-1$
         proxyPort.setText(""); // $NON-NLS-1$
         proxyUser.setText(""); // $NON-NLS-1$
         proxyPass.setText(""); // $NON-NLS-1$
         connectTimeOut.setText(""); // $NON-NLS-1$
         responseTimeOut.setText(""); // $NON-NLS-1$
         protocol.setText(""); // $NON-NLS-1$
         contentEncoding.setText(""); // $NON-NLS-1$
         argsPanel.clear();
         if(showRawBodyPane) {
             postBodyContent.setText("");// $NON-NLS-1$
         }
         postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
     }
 
     public TestElement createTestElement() {
         ConfigTestElement element = new ConfigTestElement();
 
         element.setName(this.getName());
         element.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
         element.setProperty(TestElement.TEST_CLASS, element.getClass().getName());
         modifyTestElement(element);
         return element;
     }
 
     /**
      * Save the GUI values in the sampler.
      *
      * @param element
      */
     public void modifyTestElement(TestElement element) {
         boolean useRaw = postContentTabbedPane.getSelectedIndex()==TAB_RAW_BODY;
         Arguments args;
         if(useRaw) {
             args = new Arguments();
             String text = postBodyContent.getText();
             /*
              * Textfield uses \n (LF) to delimit lines; we need to send CRLF.
              * Rather than change the way that arguments are processed by the
              * samplers for raw data, it is easier to fix the data.
              * On retrival, CRLF is converted back to LF for storage in the text field.
              * See
              */
             HTTPArgument arg = new HTTPArgument("", text.replaceAll("\n","\r\n"), false);
             arg.setAlwaysEncoded(false);
             args.addArgument(arg);
         } else {
             args = (Arguments) argsPanel.createTestElement();
             HTTPArgument.convertArgumentsToHTTP(args);
         }
         element.setProperty(HTTPSamplerBase.POST_BODY_RAW, useRaw, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
         element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
         element.setProperty(HTTPSamplerBase.PORT, port.getText());
         element.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
         element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
         element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
         element.setProperty(HTTPSamplerBase.PROTOCOL, protocol.getText());
         element.setProperty(HTTPSamplerBase.CONTENT_ENCODING, contentEncoding.getText());
         element.setProperty(HTTPSamplerBase.PATH, path.getText());
         if (notConfigOnly){
             element.setProperty(HTTPSamplerBase.METHOD, method.getText());
             element.setProperty(new BooleanProperty(HTTPSamplerBase.FOLLOW_REDIRECTS, followRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.AUTO_REDIRECTS, autoRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.USE_KEEPALIVE, useKeepAlive.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.DO_MULTIPART_POST, useMultipartForPost.isSelected()));
             element.setProperty(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, useBrowserCompatibleMultipartMode.isSelected(),HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             element.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
         }
     }
 
     // FIXME FACTOR WITH HTTPHC4Impl, HTTPHC3Impl
     // Just append all the parameter values, and use that as the post body
     /**
      * Compute Post body from arguments
      * @param arguments {@link Arguments}
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments) {
         return computePostBody(arguments, false);
     }
 
     /**
      * Compute Post body from arguments
      * @param arguments {@link Arguments}
      * @param crlfToLF whether to convert CRLF to LF
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         PropertyIterator args = arguments.iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             String value = arg.getValue();
             if (crlfToLF) {
                 value=value.replaceAll("\r\n", "\n"); // See modifyTestElement
             }
             postBody.append(value);
         }
         return postBody.toString();
     }
 
     /**
      * Set the text, etc. in the UI.
      *
      * @param el
      *            contains the data to be displayed
      */
     public void configure(TestElement el) {
         setName(el.getName());
         Arguments arguments = (Arguments) el.getProperty(HTTPSamplerBase.ARGUMENTS).getObjectValue();
 
         boolean useRaw = el.getPropertyAsBoolean(HTTPSamplerBase.POST_BODY_RAW, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         if(useRaw) {
             String postBody = computePostBody(arguments, true); // Convert CRLF to CR, see modifyTestElement
             postBodyContent.setText(postBody);   
             postContentTabbedPane.setSelectedIndex(TAB_RAW_BODY, false);
         } else {
             argsPanel.configure(arguments);
             postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
         }
 
         domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));
 
         String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);
 
         // Only display the port number if it is meaningfully specified
         if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
             port.setText(""); // $NON-NLS-1$
         } else {
             port.setText(portString);
         }
         proxyHost.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
         proxyPort.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
         proxyUser.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
         proxyPass.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
         connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
         responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
         protocol.setText(el.getPropertyAsString(HTTPSamplerBase.PROTOCOL));
         contentEncoding.setText(el.getPropertyAsString(HTTPSamplerBase.CONTENT_ENCODING));
         path.setText(el.getPropertyAsString(HTTPSamplerBase.PATH));
         if (notConfigOnly){
             method.setText(el.getPropertyAsString(HTTPSamplerBase.METHOD));
             followRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
             autoRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
             useKeepAlive.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
             useMultipartForPost.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
             useBrowserCompatibleMultipartMode.setSelected(el.getPropertyAsBoolean(
                     HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT));
         }
         if (showImplementation) {
             httpImplementation.setText(el.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
         }
     }
 
     private void init() {// called from ctor, so must not be overridable
         this.setLayout(new BorderLayout());
 
         // WEB REQUEST PANEL
         JPanel webRequestPanel = new JPanel();
         webRequestPanel.setLayout(new BorderLayout());
         webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
         northPanel.add(getProtocolAndMethodPanel());
         northPanel.add(getPathPanel());
 
         webRequestPanel.add(northPanel, BorderLayout.NORTH);
         webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
 
         this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
         this.add(webRequestPanel, BorderLayout.CENTER);
         this.add(getProxyServerPanel(), BorderLayout.SOUTH);
     }
 
     /**
      * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
      *
      * @return the panel
      */
     protected final JPanel getWebServerTimeoutPanel() {
         // WEB SERVER PANEL
         JPanel webServerPanel = new HorizontalPanel();
         webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
         final JPanel domainPanel = getDomainPanel();
         final JPanel portPanel = getPortPanel();
         webServerPanel.add(domainPanel, BorderLayout.CENTER);
         webServerPanel.add(portPanel, BorderLayout.EAST);
 
         JPanel timeOut = new HorizontalPanel();
         timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
         final JPanel connPanel = getConnectTimeOutPanel();
         final JPanel reqPanel = getResponseTimeOutPanel();
         timeOut.add(connPanel);
         timeOut.add(reqPanel);
 
         JPanel webServerTimeoutPanel = new VerticalPanel();
         webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
         webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
 
         JPanel bigPanel = new VerticalPanel();
         bigPanel.add(webServerTimeoutPanel);
         return bigPanel;
     }
 
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     protected final JPanel getProxyServerPanel(){
         JPanel proxyServer = new HorizontalPanel();
         proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
         proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
 
         JPanel proxyLogin = new HorizontalPanel();
         proxyLogin.add(getProxyUserPanel());
         proxyLogin.add(getProxyPassPanel());
 
         JPanel proxyServerPanel = new HorizontalPanel();
         proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
         proxyServerPanel.add(proxyServer, BorderLayout.CENTER);
         proxyServerPanel.add(proxyLogin, BorderLayout.EAST);
 
         return proxyServerPanel;
     }
 
     private JPanel getPortPanel() {
         port = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(port);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(port, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getConnectTimeOutPanel() {
         connectTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
         label.setLabelFor(connectTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(connectTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getResponseTimeOutPanel() {
         responseTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
         label.setLabelFor(responseTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(responseTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getDomainPanel() {
         domain = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(domain);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(domain, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyUserPanel() {
         proxyUser = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
         label.setLabelFor(proxyUser);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyUser, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyPassPanel() {
         proxyPass = new JPasswordField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
         label.setLabelFor(proxyPass);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPass, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * This method defines the Panel for the HTTP path, 'Follow Redirects'
      * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
      *
      * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
      *         KeepAlive' elements.
      */
     protected Component getPathPanel() {
         path = new JTextField(15);
 
         JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
         label.setLabelFor(path);
 
         if (notConfigOnly){
             followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
             followRedirects.setSelected(true);
             followRedirects.addChangeListener(this);
 
             autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
             autoRedirects.addChangeListener(this);
             autoRedirects.setSelected(false);// Default changed in 2.3 and again in 2.4
 
             useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
             useKeepAlive.setSelected(true);
 
             useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
             useMultipartForPost.setSelected(false);
 
             useBrowserCompatibleMultipartMode = new JCheckBox(JMeterUtils.getResString("use_multipart_mode_browser")); // $NON-NLS-1$
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
 
         }
 
         JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
         pathPanel.add(label, BorderLayout.WEST);
         pathPanel.add(path, BorderLayout.CENTER);
         pathPanel.setMinimumSize(pathPanel.getPreferredSize());
 
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(pathPanel);
         if (notConfigOnly){
             JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             optionPanel.add(autoRedirects);
             optionPanel.add(followRedirects);
             optionPanel.add(useKeepAlive);
             optionPanel.add(useMultipartForPost);
             optionPanel.add(useBrowserCompatibleMultipartMode);
             optionPanel.setMinimumSize(optionPanel.getPreferredSize());
             panel.add(optionPanel);
         }
 
         return panel;
     }
 
     protected JPanel getProtocolAndMethodPanel() {
 
         // Implementation
         
         if (showImplementation) {
             httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
                     HTTPSamplerFactory.getImplementations());
             httpImplementation.addValue("");
         }
         // PROTOCOL
         protocol = new JTextField(4);
         JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
         protocolLabel.setLabelFor(protocol);        
         
         // CONTENT_ENCODING
         contentEncoding = new JTextField(10);
         JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
         contentEncodingLabel.setLabelFor(contentEncoding);
 
         if (notConfigOnly){
             method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                     HTTPSamplerBase.getValidMethodsAsArray());
         }
 
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
 
         if (showImplementation) {
             panel.add(httpImplementation);
         }
         panel.add(protocolLabel);
         panel.add(protocol);
         panel.add(Box.createHorizontalStrut(5));
 
         if (notConfigOnly){
             panel.add(method);
         }
         panel.setMinimumSize(panel.getPreferredSize());
         panel.add(Box.createHorizontalStrut(5));
 
         panel.add(contentEncodingLabel);
         panel.add(contentEncoding);
         panel.setMinimumSize(panel.getPreferredSize());
         return panel;
     }
 
     protected JTabbedPane getParameterPanel() {
         postContentTabbedPane = new ValidationTabbedPane();
         argsPanel = new HTTPArgumentsPanel();
         postContentTabbedPane.add(JMeterUtils.getResString("post_as_parameters"), argsPanel);// $NON-NLS-1$
         if(showRawBodyPane) {
             postBodyContent = new JLabeledTextArea(JMeterUtils.getResString("post_body_raw"));// $NON-NLS-1$
             postContentTabbedPane.add(JMeterUtils.getResString("post_body"), postBodyContent);// $NON-NLS-1$
         }
         return postContentTabbedPane;
     }
 
     /**
      * 
      */
     class ValidationTabbedPane extends JTabbedPane{
 
         /**
          * 
          */
         private static final long serialVersionUID = 7014311238367882880L;
 
         /* (non-Javadoc)
          * @see javax.swing.JTabbedPane#setSelectedIndex(int)
          */
         @Override
         public void setSelectedIndex(int index) {
             setSelectedIndex(index, true);
         }
         /**
          * Apply some check rules if check is true
          */
         public void setSelectedIndex(int index, boolean check) {
             int oldSelectedIndex = getSelectedIndex();
             if(!check || oldSelectedIndex==-1) {
                 super.setSelectedIndex(index);
             }
             else if(index != this.getSelectedIndex())
             {
                 if(noData(getSelectedIndex())) {
                     // If there is no data, then switching between Parameters and Raw should be
                     // allowed with no further user interaction.
                     argsPanel.clear();
                     postBodyContent.setText("");
                     super.setSelectedIndex(index);
                 }
                 else { 
                     if(oldSelectedIndex == TAB_RAW_BODY) {
                         // If RAW data and Parameters match we allow switching
                         if(postBodyContent.getText().equals(computePostBody((Arguments)argsPanel.createTestElement()).trim())) {
                             super.setSelectedIndex(index);
                         }
                         else {
                             // If there is data in the Raw panel, then the user should be 
                             // prevented from switching (that would be easy to track).
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_switch_tab"), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                     else {
                         // If the Parameter data can be converted (i.e. no names), we 
                         // warn the user that the Parameter data will be lost.
                         if(canConvertParameters()) {
                             Object[] options = {
                                     JMeterUtils.getResString("confirm"),
                                     JMeterUtils.getResString("cancel")};
                             int n = JOptionPane.showOptionDialog(this,
                                 JMeterUtils.getResString("web_parameters_lost_message"),
                                 JMeterUtils.getResString("warning"),
                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                 JOptionPane.QUESTION_MESSAGE,
                                 null,
                                 options,
                                 options[1]);
                             if(n == JOptionPane.YES_OPTION) {
                                 convertParametersToRaw();
                                 super.setSelectedIndex(index);
                             }
                             else{
                                 return;
                             }
                         }
                         else {
                             // If the Parameter data cannot be converted to Raw, then the user should be
                             // prevented from doing so raise an error dialog
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_convert_parameters_to_raw"), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                 }
             }
         }   
     }
     // autoRedirects and followRedirects cannot both be selected
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() == autoRedirects){
             if (autoRedirects.isSelected()) {
                 followRedirects.setSelected(false);
             }
         }
         if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
     }
 
 
     /**
      * Convert Parameters to Raw Body
      */
     void convertParametersToRaw() {
         postBodyContent.setText(computePostBody((Arguments)argsPanel.createTestElement()));
     }
 
     /**
      * 
      * @return true if no argument has a name
      */
     boolean canConvertParameters() {
         Arguments arguments = (Arguments)argsPanel.createTestElement();
         for (int i = 0; i < arguments.getArgumentCount(); i++) {
             if(!StringUtils.isEmpty(arguments.getArgument(i).getName())) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * @return true if neither Parameters tab nor Raw Body tab contain data
      */
     boolean noData(int oldSelectedIndex) {
         if(oldSelectedIndex == TAB_RAW_BODY) {
             return StringUtils.isEmpty(postBodyContent.getText().trim());
         }
         else {
             Arguments element = (Arguments)argsPanel.createTestElement();
             return StringUtils.isEmpty(computePostBody(element));
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
index 948eaa0c6..05bd4792a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
@@ -1,428 +1,428 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 // For unit tests @see TestCookieManager
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.Map;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.URIException;
 import org.apache.commons.httpclient.util.DateParseException;
 import org.apache.commons.httpclient.util.DateUtil;
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.http.HttpResponse;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Handles HTTP Caching
  */
 public class CacheManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //+ JMX attributes, do not change values
     public static final String CLEAR = "clearEachIteration"; // $NON-NLS-1$
     public static final String USE_EXPIRES = "useExpires"; // $NON-NLS-1$
     public static final String MAX_SIZE = "maxSize";  // $NON-NLS-1$
     //-
 
     private transient InheritableThreadLocal<Map<String, CacheEntry>> threadCache;
 
     private transient boolean useExpires; // Cached value
 
     private static final int DEFAULT_MAX_SIZE = 5000;
 
     private static final long ONE_YEAR_MS = 365*86400*1000;
 
     public CacheManager() {
         setProperty(new BooleanProperty(CLEAR, false));
         setProperty(new BooleanProperty(USE_EXPIRES, false));
         clearCache();
         useExpires = false;
     }
 
     /*
      * Holder for storing cache details.
      * Perhaps add original response later?
      */
     // package-protected to allow access by unit-test cases
     static class CacheEntry{
         private final String lastModified;
         private final String etag;
         private final Date expires;
         public CacheEntry(String lastModified, Date expires, String etag){
            this.lastModified = lastModified;
            this.etag = etag;
            this.expires = expires;
        }
         public String getLastModified() {
             return lastModified;
         }
         public String getEtag() {
             return etag;
         }
         @Override
         public String toString(){
             return lastModified+" "+etag;
         }
         public Date getExpires() {
             return expires;
         }
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
      * Version for Java implementation.
      * @param conn connection
      * @param res result
      */
     public void saveDetails(URLConnection conn, SampleResult res){
         if (isCacheable(res)){
             String lastModified = conn.getHeaderField(HTTPConstants.LAST_MODIFIED);
             String expires = conn.getHeaderField(HTTPConstants.EXPIRES);
             String etag = conn.getHeaderField(HTTPConstants.ETAG);
             String url = conn.getURL().toString();
             String cacheControl = conn.getHeaderField(HTTPConstants.CACHE_CONTROL);
             String date = conn.getHeaderField(HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
      * Version for Commons HttpClient implementation.
      * @param method
      * @param res result
      */
     public void saveDetails(HttpMethod method, SampleResult res) throws URIException{
         if (isCacheable(res)){
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String url = method.getURI().toString();
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
      * Version for Apache HttpClient implementation.
      * @param method
      * @param res result
      */
     public void saveDetails(HttpResponse method, SampleResult res) {
         if (isCacheable(res)){
             method.getLastHeader(USE_EXPIRES);
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, res.getUrlAsString(), date); // TODO correct URL?
         }
     }
 
     // helper method to save the cache entry
     private void setCache(String lastModified, String cacheControl, String expires, String etag, String url, String date) {
         if (log.isDebugEnabled()){
             log.debug("setCache("
                   + lastModified + "," 
                   + cacheControl + ","
                   + expires + "," 
                   + etag + ","
                   + url + ","
                   + date
                   + ")");
         }
         Date expiresDate = null; // i.e. not using Expires
         if (useExpires) {// Check that we are processing Expires/CacheControl
             final String MAX_AGE = "max-age=";
             
             if (expires != null) {
                 try {
                     expiresDate = DateUtil.parseDate(expires);
                 } catch (DateParseException e) {
                     if (log.isDebugEnabled()){
                         log.debug("Unable to parse Expires: '"+expires+"' "+e);
                     }
                     expiresDate = new Date(0L); // invalid dates must be treated as expired
                 }
             }
             // if no-cache is present, ensure that expiresDate remains null, which forces revalidation
             if(cacheControl != null && !cacheControl.contains("no-cache")) {    
                 // the max-age directive overrides the Expires header,
                 if(cacheControl.contains(MAX_AGE)) {
                     long maxAgeInSecs = Long.parseLong(
                             cacheControl.substring(cacheControl.indexOf(MAX_AGE)+MAX_AGE.length())
                                 .split("[, ]")[0] // Bug 51932 - allow for optional trailing attributes
                             );
                     expiresDate=new Date(System.currentTimeMillis()+maxAgeInSecs*1000);
 
                 } else if(expires==null) { // No max-age && No expires
                     if(!StringUtils.isEmpty(lastModified) && !StringUtils.isEmpty(date)) {
                         try {
                             Date responseDate = DateUtil.parseDate( date );
                             Date lastModifiedAsDate = DateUtil.parseDate( lastModified );
                             // see https://developer.mozilla.org/en/HTTP_Caching_FAQ
                             // see http://www.ietf.org/rfc/rfc2616.txt#13.2.4 
                             expiresDate=new Date(System.currentTimeMillis()
                                     +Math.round((responseDate.getTime()-lastModifiedAsDate.getTime())*0.1));
                         } catch(DateParseException e) {
                             // date or lastModified may be null or in bad format
                             if(log.isWarnEnabled()) {
                                 log.warn("Failed computing expiration date with following info:"
                                     +lastModified + "," 
                                     + cacheControl + ","
                                     + expires + "," 
                                     + etag + ","
                                     + url + ","
                                     + date);
                             }
                             // TODO Can't see anything in SPEC
                             expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
                         }
                     } else {
                         // TODO Can't see anything in SPEC
                         expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
                     }
                 }  
                 // else expiresDate computed in (expires!=null) condition is used
             }
         }
         getCache().put(url, new CacheEntry(lastModified, expiresDate, etag));
     }
 
     // Helper method to deal with missing headers - Commons HttpClient
     private String getHeader(HttpMethod method, String name){
         org.apache.commons.httpclient.Header hdr = method.getResponseHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     // Apache HttpClient
     private String getHeader(HttpResponse method, String name) {
         org.apache.http.Header hdr = method.getLastHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     /*
      * Is the sample result OK to cache?
      * i.e is it in the 2xx range?
      */
     private boolean isCacheable(SampleResult res){
         final String responseCode = res.getResponseCode();
         return "200".compareTo(responseCode) <= 0  // $NON-NLS-1$
             && "299".compareTo(responseCode) >= 0; // $NON-NLS-1$
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:<br/>
      * If-Modified-Since<br/>
      * If-None-Match<br/>
      * Commons HttpClient version
      * @param url URL to look up in cache
      * @param method where to set the headers
      */
     public void setHeaders(URL url, HttpMethod method) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(method.getName()+"(OACH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 method.setRequestHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 method.setRequestHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:<br/>
      * If-Modified-Since<br/>
      * If-None-Match<br/>
      * Apache HttpClient version.
      * @param url URL to look up in cache
      * @param request where to set the headers
      */
     public void setHeaders(URL url, HttpRequestBase request) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(request.getMethod()+"(OAH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 request.setHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 request.setHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:<br/>
      * If-Modified-Since<br/>
      * If-None-Match<br/>
      * @param url URL to look up in cache
      * @param conn where to set the headers
      */
     public void setHeaders(HttpURLConnection conn, URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(conn.getRequestMethod()+"(Java) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 conn.addRequestProperty(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 conn.addRequestProperty(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, if the entry has an expires header and the entry has not expired, return true<br/>
      * @param url URL to look up in cache
      */
     public boolean inCache(URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug("inCache "+url.toString()+" "+entry);
         }
         if (entry != null){
             final Date expiresDate = entry.getExpires();
             if (expiresDate != null) {
                 if (expiresDate.after(new Date())) {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Valid)");
                     }
                     return true;
                 } else {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Expired)");
                     }
                 }
             }
         }
         return false;
     }
 
     private Map<String, CacheEntry> getCache(){
         return threadCache.get();
     }
 
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR, clear));
     }
 
     public boolean getUseExpires() {
         return getPropertyAsBoolean(USE_EXPIRES);
     }
 
     public void setUseExpires(boolean expires) {
         setProperty(new BooleanProperty(USE_EXPIRES, expires));
     }
     
     /**
      * @return int cache max size
      */
     public int getMaxSize() {
         return getPropertyAsInt(MAX_SIZE, DEFAULT_MAX_SIZE);
     }
 
     /**
      * @param size int cache max size
      */
     public void setMaxSize(int size) {
         setProperty(MAX_SIZE, size, DEFAULT_MAX_SIZE);
     }
     
 
     @Override
     public void clear(){
         super.clear();
         clearCache();
     }
 
     private void clearCache() {
         log.debug("Clear cache");
         threadCache = new InheritableThreadLocal<Map<String, CacheEntry>>(){
             @Override
             protected Map<String, CacheEntry> initialValue(){
                 // Bug 51942 - this map may be used from multiple threads
                 @SuppressWarnings("unchecked") // LRUMap is not generic currently
                 Map<String, CacheEntry> map = new LRUMap(getMaxSize());
                 return Collections.<String, CacheEntry>synchronizedMap(map);
             }
         };
     }
 
     public void testStarted() {
     }
 
     public void testEnded() {
     }
 
     public void testStarted(String host) {
     }
 
     public void testEnded(String host) {
     }
 
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             clearCache();
         }
         useExpires=getUseExpires(); // cache the value
     }
 
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
index b344a8972..10f8c0cae 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
@@ -1,526 +1,526 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
-import org.apache.commons.lang.ArrayUtils;
+import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.WebServiceSampler;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.WSDLHelper;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 
 /**
  * This is the GUI for the webservice samplers. It extends AbstractSamplerGui
  * and is modeled after the SOAP sampler GUI. I've added instructional notes to
  * the GUI for instructional purposes. XML parsing is pretty heavy weight,
  * therefore the notes address those situations. <br>
  * Created on: Jun 26, 2003
  *
  */
 public class WebServiceSamplerGui extends AbstractSamplerGui implements java.awt.event.ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private final JLabeledTextField domain = new JLabeledTextField(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
 
     private final JLabeledTextField protocol = new JLabeledTextField(JMeterUtils.getResString("protocol"), 4); // $NON-NLS-1$
 
     private final JLabeledTextField port = new JLabeledTextField(JMeterUtils.getResString("web_server_port"), 4); // $NON-NLS-1$
 
     private final JLabeledTextField path = new JLabeledTextField(JMeterUtils.getResString("path")); // $NON-NLS-1$
 
     private final JLabeledTextField soapAction = new JLabeledTextField(JMeterUtils.getResString("webservice_soap_action")); // $NON-NLS-1$
 
     /**
      * checkbox for Session maintenance.
      */
     private JCheckBox maintainSession = new JCheckBox(JMeterUtils.getResString("webservice_maintain_session"), true); // $NON-NLS-1$
 
     
     private JTextArea soapXml;
 
     private final JLabeledTextField wsdlField = new JLabeledTextField(JMeterUtils.getResString("wsdl_url")); // $NON-NLS-1$
 
     private final JButton wsdlButton = new JButton(JMeterUtils.getResString("load_wsdl")); // $NON-NLS-1$
 
     private final JButton selectButton = new JButton(JMeterUtils.getResString("configure_wsdl")); // $NON-NLS-1$
 
     private JLabeledChoice wsdlMethods = null;
 
     private transient WSDLHelper HELPER = null;
 
     private final FilePanel soapXmlFile = new FilePanel(JMeterUtils.getResString("get_xml_from_file"), ".xml"); // $NON-NLS-1$
 
     private final JLabeledTextField randomXmlFile = new JLabeledTextField(JMeterUtils.getResString("get_xml_from_random")); // $NON-NLS-1$
 
     private final JLabeledTextField connectTimeout = new JLabeledTextField(JMeterUtils.getResString("webservice_timeout"), 4); // $NON-NLS-1$
 
     /**
      * checkbox for memory cache.
      */
     private JCheckBox memCache = new JCheckBox(JMeterUtils.getResString("memory_cache"), true); // $NON-NLS-1$
 
     /**
      * checkbox for reading the response
      */
     private JCheckBox readResponse = new JCheckBox(JMeterUtils.getResString("read_soap_response")); // $NON-NLS-1$
 
     /**
      * checkbox for use proxy
      */
     private JCheckBox useProxy = new JCheckBox(JMeterUtils.getResString("webservice_use_proxy")); // $NON-NLS-1$
 
     /**
      * text field for the proxy host
      */
     private JTextField proxyHost;
 
     /**
      * text field for the proxy port
      */
     private JTextField proxyPort;
 
     /**
      * Text note about read response and its usage.
      */
     private String readToolTip = JMeterUtils.getResString("read_response_note") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("read_response_note2") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("read_response_note3"); // $NON-NLS-1$
 
     /**
      * Text note for proxy
      */
     private String proxyToolTip = JMeterUtils.getResString("webservice_proxy_note") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("webservice_proxy_note2") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("webservice_proxy_note3"); // $NON-NLS-1$
     public WebServiceSamplerGui() {
         init();
     }
 
     public String getLabelResource() {
         return "webservice_sampler_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     public TestElement createTestElement() {
         WebServiceSampler sampler = new WebServiceSampler();
         this.configureTestElement(sampler);
         this.modifyTestElement(sampler);
         return sampler;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     public void modifyTestElement(TestElement s) {
         WebServiceSampler sampler = (WebServiceSampler) s;
         this.configureTestElement(sampler);
         sampler.setDomain(domain.getText());
         sampler.setProperty(HTTPSamplerBase.PORT,port.getText());
         sampler.setProtocol(protocol.getText());
         sampler.setPath(path.getText());
         sampler.setWsdlURL(wsdlField.getText());
         sampler.setMethod(HTTPConstants.POST);
         sampler.setSoapAction(soapAction.getText());
         sampler.setMaintainSession(maintainSession.isSelected());
         sampler.setXmlData(soapXml.getText());
         sampler.setXmlFile(soapXmlFile.getFilename());
         sampler.setXmlPathLoc(randomXmlFile.getText());
         sampler.setTimeout(connectTimeout.getText());
         sampler.setMemoryCache(memCache.isSelected());
         sampler.setReadResponse(readResponse.isSelected());
         sampler.setUseProxy(useProxy.isSelected());
         sampler.setProxyHost(proxyHost.getText());
         sampler.setProxyPort(proxyPort.getText());
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         wsdlMethods.setValues(new String[0]);
         domain.setText(""); //$NON-NLS-1$
         protocol.setText(""); //$NON-NLS-1$
         port.setText(""); //$NON-NLS-1$
         path.setText(""); //$NON-NLS-1$
         soapAction.setText(""); //$NON-NLS-1$
         maintainSession.setSelected(WebServiceSampler.MAINTAIN_SESSION_DEFAULT);
         soapXml.setText(""); //$NON-NLS-1$
         wsdlField.setText(""); //$NON-NLS-1$
         randomXmlFile.setText(""); //$NON-NLS-1$
         connectTimeout.setText(""); //$NON-NLS-1$
         proxyHost.setText(""); //$NON-NLS-1$
         proxyPort.setText(""); //$NON-NLS-1$
         memCache.setSelected(true);
         readResponse.setSelected(false);
         useProxy.setSelected(false);
         soapXmlFile.setFilename(""); //$NON-NLS-1$
     }
 
     /**
      * init() adds soapAction to the mainPanel. The class reuses logic from
      * SOAPSampler, since it is common.
      */
     private void init() {
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         
         mainPanel.add(createTopPanel(), BorderLayout.NORTH);
         mainPanel.add(createMessagePanel(), BorderLayout.CENTER);
         mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
         this.add(mainPanel);
     }
 
     private final JPanel createTopPanel() {
         JPanel topPanel = new JPanel();
         topPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         
         JPanel wsdlHelper = new JPanel();
         wsdlHelper.setLayout(new BoxLayout(wsdlHelper, BoxLayout.Y_AXIS));
         wsdlHelper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("webservice_configuration_wizard"))); // $NON-NLS-1$
 
         // Button for browsing webservice wsdl
         JPanel wsdlEntry = new JPanel();
         wsdlEntry.setLayout(new BoxLayout(wsdlEntry, BoxLayout.X_AXIS));
         Border margin = new EmptyBorder(0, 5, 0, 5);
         wsdlEntry.setBorder(margin);
         wsdlHelper.add(wsdlEntry);
         wsdlEntry.add(wsdlField);
         wsdlEntry.add(wsdlButton);
         wsdlButton.addActionListener(this);
 
         // Web Methods
         JPanel listPanel = new JPanel();
         listPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
         JLabel selectLabel = new JLabel(JMeterUtils.getResString("webservice_methods")); // $NON-NLS-1$
         wsdlMethods = new JLabeledChoice();
         wsdlHelper.add(listPanel);
         listPanel.add(selectLabel);
         listPanel.add(wsdlMethods);
         listPanel.add(selectButton);
         selectButton.addActionListener(this);
 
         topPanel.add(wsdlHelper);
         
         JPanel urlPane = new JPanel();
         urlPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         urlPane.add(protocol);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(domain);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(port);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(connectTimeout);
         topPanel.add(urlPane);
         
         topPanel.add(createParametersPanel());
         
         return topPanel;
     }
 
     private final JPanel createParametersPanel() {
         JPanel paramsPanel = new JPanel();
         paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.X_AXIS));
         paramsPanel.add(path);
         paramsPanel.add(Box.createHorizontalGlue());        
         paramsPanel.add(soapAction);
         paramsPanel.add(Box.createHorizontalGlue());        
         paramsPanel.add(maintainSession);
         return paramsPanel;
     }
     
     private final JPanel createMessagePanel() {
         JPanel msgPanel = new JPanel();
         msgPanel.setLayout(new BorderLayout(5, 0));
         msgPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("webservice_message_soap"))); // $NON-NLS-1$
 
         JPanel soapXmlPane = new JPanel();
         soapXmlPane.setLayout(new BorderLayout(5, 0));
         soapXmlPane.setBorder(BorderFactory.createTitledBorder(
                 JMeterUtils.getResString("soap_data_title"))); // $NON-NLS-1$
         soapXmlPane.setPreferredSize(new Dimension(4, 4)); // Permit dynamic resize of TextArea
         soapXml = new JTextArea();
         soapXml.setLineWrap(true);
         soapXml.setWrapStyleWord(true);
         soapXml.setTabSize(4); // improve xml display
         soapXmlPane.add(new JScrollPane(soapXml), BorderLayout.CENTER);
         msgPanel.add(soapXmlPane, BorderLayout.CENTER);
         
         JPanel southPane = new JPanel();
         southPane.setLayout(new BoxLayout(southPane, BoxLayout.Y_AXIS));
         southPane.add(soapXmlFile);
         JPanel randomXmlPane = new JPanel();
         randomXmlPane.setLayout(new BorderLayout(5, 0));
         randomXmlPane.setBorder(BorderFactory.createTitledBorder(
                 JMeterUtils.getResString("webservice_get_xml_from_random_title"))); // $NON-NLS-1$
         randomXmlPane.add(randomXmlFile, BorderLayout.CENTER);
         southPane.add(randomXmlPane);
         msgPanel.add(southPane, BorderLayout.SOUTH);
         return msgPanel;
     }
     
     private final JPanel createBottomPanel() {
         JPanel optionPane = new JPanel();
         optionPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("option"))); // $NON-NLS-1$
         optionPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         JPanel ckboxPane = new HorizontalPanel();
         ckboxPane.add(memCache, BorderLayout.WEST);
         ckboxPane.add(readResponse, BorderLayout.CENTER);
         readResponse.setToolTipText(readToolTip);
         optionPane.add(ckboxPane);
 
         // add the proxy elements
         optionPane.add(getProxyServerPanel());
         return optionPane;
         
     }
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     private final JPanel getProxyServerPanel(){
         JPanel proxyServer = new JPanel();
         proxyServer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         proxyServer.add(useProxy);
         useProxy.addActionListener(this);
         useProxy.setToolTipText(proxyToolTip);
         proxyServer.add(Box.createRigidArea(new Dimension(5,0)));
         proxyServer.add(getProxyHostPanel());
         proxyServer.add(Box.createRigidArea(new Dimension(5,0)));
         proxyServer.add(getProxyPortPanel());
         return proxyServer;
     }
     
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(12);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
     
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     /**
      * the implementation loads the URL and the soap action for the request.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         WebServiceSampler sampler = (WebServiceSampler) el;
         wsdlField.setText(sampler.getWsdlURL());
         final String wsdlText = wsdlField.getText();
         if (wsdlText != null && wsdlText.length() > 0) {
             fillWsdlMethods(wsdlField.getText(), true, sampler.getSoapAction());
         }
         protocol.setText(sampler.getProtocol());
         domain.setText(sampler.getDomain());
         port.setText(sampler.getPropertyAsString(HTTPSamplerBase.PORT));
         path.setText(sampler.getPath());
         soapAction.setText(sampler.getSoapAction());
         maintainSession.setSelected(sampler.getMaintainSession());
         soapXml.setText(sampler.getXmlData());
         soapXml.setCaretPosition(0); // go to 1st line
         soapXmlFile.setFilename(sampler.getXmlFile());
         randomXmlFile.setText(sampler.getXmlPathLoc());
         connectTimeout.setText(sampler.getTimeout());
         memCache.setSelected(sampler.getMemoryCache());
         readResponse.setSelected(sampler.getReadResponse());
         useProxy.setSelected(sampler.getUseProxy());
         if (sampler.getProxyHost().length() == 0) {
             proxyHost.setEnabled(false);
         } else {
             proxyHost.setText(sampler.getProxyHost());
         }
         if (sampler.getProxyPort() == 0) {
             proxyPort.setEnabled(false);
         } else {
             proxyPort.setText(String.valueOf(sampler.getProxyPort()));
         }
     }
 
     /**
      * configure the sampler from the WSDL. If the WSDL did not include service
      * node, it will use the original URL minus the querystring. That may not be
      * correct, so we should probably add a note. For Microsoft webservices it
      * will work, since that's how IIS works.
      */
     public void configureFromWSDL() {
         if (HELPER != null) {
             if(HELPER.getBinding() != null) {
                 this.protocol.setText(HELPER.getProtocol());
                 this.domain.setText(HELPER.getBindingHost());
                 if (HELPER.getBindingPort() > 0) {
                     this.port.setText(String.valueOf(HELPER.getBindingPort()));
                 } else {
                     this.port.setText("80"); // $NON-NLS-1$
                 }
                 this.path.setText(HELPER.getBindingPath());
             }
             this.soapAction.setText(HELPER.getSoapAction(this.wsdlMethods.getText()));
         }
     }
 
     /**
      * The method uses WSDLHelper to get the information from the WSDL. Since
      * the logic for getting the description is isolated to this method, we can
      * easily replace it with a different WSDL driver later on.
      *
      * @param url
      * @param silent 
      * @return array of web methods
      */
     public String[] browseWSDL(String url, boolean silent) {
         try {
             // We get the AuthManager and pass it to the WSDLHelper
             // once the sampler is updated to Axis, all of this stuff
             // should not be necessary. Now I just need to find the
             // time and motivation to do it.
             WebServiceSampler sampler = (WebServiceSampler) this.createTestElement();
             AuthManager manager = sampler.getAuthManager();
             HELPER = new WSDLHelper(url, manager);
             HELPER.parse();
             return HELPER.getWebMethods();
         } catch (Exception exception) {
             if (!silent) {
                 JOptionPane.showConfirmDialog(this,
                         JMeterUtils.getResString("wsdl_helper_error") // $NON-NLS-1$
                         +"\n"+exception, // $NON-NLS-1$
                         JMeterUtils.getResString("warning"), // $NON-NLS-1$
                         JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
             }
             return ArrayUtils.EMPTY_STRING_ARRAY;
         }
     }
 
     /**
      * method from ActionListener
      *
      * @param event
      *            that occurred
      */
     public void actionPerformed(ActionEvent event) {
         final Object eventSource = event.getSource();
         if (eventSource == selectButton) {
             this.configureFromWSDL();
         } else if (eventSource == useProxy) {
             // if use proxy is checked, we enable
             // the text fields for the host and port
             boolean use = useProxy.isSelected();
             if (use) {
                 proxyHost.setEnabled(true);
                 proxyPort.setEnabled(true);
             } else {
                 proxyHost.setEnabled(false);
                 proxyPort.setEnabled(false);
             }
         } else if (eventSource == wsdlButton){
             final String wsdlText = wsdlField.getText();
             if (wsdlText != null && wsdlText.length() > 0) {
                 fillWsdlMethods(wsdlText, false, null);
             } else {
                 JOptionPane.showConfirmDialog(this,
                         JMeterUtils.getResString("wsdl_url_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("warning"), // $NON-NLS-1$
                         JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 
     /**
      * @param wsdlText
      * @param silent
      * @param soapAction 
      */
     private void fillWsdlMethods(final String wsdlText, boolean silent, String soapAction) {
         String[] wsdlData = browseWSDL(wsdlText, silent);
         if (wsdlData != null) {
             wsdlMethods.setValues(wsdlData);
             if (HELPER != null && soapAction != null) {
                 String selected = HELPER.getSoapActionName(soapAction);
                 if (selected != null) {
                     wsdlMethods.setText(selected);
                 }
             }
             wsdlMethods.repaint();
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
index a3d67800c..3bfc97ced 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
@@ -1,129 +1,129 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 
 /**
  * Collection class designed for handling URLs
  *
  * Before a URL is added to the collection, it is wrapped in a URLString class.
  * The iterator unwraps the URL before return.
  *
  * N.B. Designed for use by HTMLParser, so is not a full implementation - e.g.
  * does not support remove()
  *
  */
 public class URLCollection {
     private final Collection<URLString> coll;
 
     /**
      * Creates a new URLCollection from an existing Collection
      *
      */
     public URLCollection(Collection<URLString> c) {
         coll = c;
     }
 
     /**
      * Adds the URL to the Collection, first wrapping it in the URLString class
      *
      * @param u
      *            URL to add
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean add(URL u) {
         return coll.add(new URLString(u));
     }
 
     /*
      * Adds the string to the Collection, first wrapping it in the URLString
      * class
      *
      * @param s string to add @return boolean condition returned by the add()
      * method of the underlying collection
      */
     private boolean add(String s) {
         return coll.add(new URLString(s));
     }
 
     /**
      * Convenience method for adding URLs to the collection If the url parameter
      * is null or empty, nothing is done
      *
      * @param url
      *            String, may be null or empty
      * @param baseUrl
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean addURL(String url, URL baseUrl) {
         if (url == null || url.length() == 0) {
             return false;
         }
         //url.replace('+',' ');
         url=StringEscapeUtils.unescapeXml(url);
         boolean b = false;
         try {
             b = this.add(ConversionUtils.makeRelativeURL(baseUrl, url));
         } catch (MalformedURLException mfue) {
             // TODO log a warning message?
             b = this.add(url);// Add the string if cannot create the URL
         }
         return b;
     }
 
     public Iterator<URL> iterator() {
         return new UrlIterator(coll.iterator());
     }
 
     /*
      * Private iterator used to unwrap the URL from the URLString class
      *
      */
     private static class UrlIterator implements Iterator<URL> {
         private final Iterator<URLString> iter;
 
         UrlIterator(Iterator<URLString> i) {
             iter = i;
         }
 
         public boolean hasNext() {
             return iter.hasNext();
         }
 
         /*
          * Unwraps the URLString class to return the URL
          */
         public URL next() {
             return iter.next().getURL();
         }
 
         public void remove() {
             throw new UnsupportedOperationException();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
index bda970fda..74d6a8035 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
@@ -1,348 +1,348 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.File;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Map;
 
 import org.apache.commons.io.FileUtils;
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.sampler.PostWriter;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Default implementation that handles classical HTTP textual + Multipart requests
  */
 public class DefaultSamplerCreator extends AbstractSamplerCreator {
     private static final Logger log = LoggingManager.getLoggerForClass();
  
     /**
      * 
      */
     public DefaultSamplerCreator() {
     }
 
     /**
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#getManagedContentTypes()
      */
     public String[] getManagedContentTypes() {
         return new String[0];
     }
 
     /**
      * 
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#createSampler(org.apache.jmeter.protocol.http.proxy.HttpRequestHdr, java.util.Map, java.util.Map)
      */
     public HTTPSamplerBase createSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings) {
         // Instantiate the sampler
         HTTPSamplerBase sampler = HTTPSamplerFactory.newInstance(request.getHttpSamplerName());
 
         sampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
 
         // Defaults
         sampler.setFollowRedirects(false);
         sampler.setUseKeepAlive(true);
 
         if (log.isDebugEnabled()) {
             log.debug("getSampler: sampler path = " + sampler.getPath());
         }
         return sampler;
     }
 
     /**
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#populateSampler(org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase, org.apache.jmeter.protocol.http.proxy.HttpRequestHdr, java.util.Map, java.util.Map)
      */
     public final void populateSampler(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws Exception{
         computeFromHeader(sampler, request, pageEncodings, formEncodings);
 
         computeFromPostBody(sampler, request);
         if (log.isDebugEnabled()) {
             log.debug("sampler path = " + sampler.getPath());
         }
     }
 
     /**
      * Compute sampler informations from Request Header
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      * @param pageEncodings Map<String, String>
      * @param formEncodings Map<String, String>
      * @throws Exception
      */
     protected void computeFromHeader(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws Exception {
         computeDomain(sampler, request);
         
         computeMethod(sampler, request);
         
         computePort(sampler, request);
         
         computeProtocol(sampler, request);
 
         computeContentEncoding(sampler, request,
                 pageEncodings, formEncodings);
 
         computePath(sampler, request);
         
         computeSamplerName(sampler, request);
     }
 
     /**
      * Compute sampler informations from Request Header
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      * @throws Exception
      */
     protected void computeFromPostBody(HTTPSamplerBase sampler,
             HttpRequestHdr request) throws Exception {
         // If it was a HTTP GET request, then all parameters in the URL
         // has been handled by the sampler.setPath above, so we just need
         // to do parse the rest of the request if it is not a GET request
         if((!HTTPConstants.CONNECT.equals(request.getMethod())) && (!HTTPConstants.GET.equals(request.getMethod()))) {
             // Check if it was a multipart http post request
             final String contentType = request.getContentType();
             MultipartUrlConfig urlConfig = request.getMultipartConfig(contentType);
             String contentEncoding = sampler.getContentEncoding();
             // Get the post data using the content encoding of the request
             String postData = null;
             if (log.isDebugEnabled()) {
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     log.debug("Using encoding " + contentEncoding + " for request body");
                 }
                 else {
                     log.debug("No encoding found, using JRE default encoding for request body");
                 }
             }
             
             
             if (!StringUtils.isEmpty(contentEncoding)) {
                 postData = new String(request.getRawPostData(), contentEncoding);
             } else {
                 // Use default encoding
                 postData = new String(request.getRawPostData(), PostWriter.ENCODING);
             }
             
             if (urlConfig != null) {
                 urlConfig.parseArguments(postData);
                 // Tell the sampler to do a multipart post
                 sampler.setDoMultipartPost(true);
                 // Remove the header for content-type and content-length, since
                 // those values will most likely be incorrect when the sampler
                 // performs the multipart request, because the boundary string
                 // will change
                 request.getHeaderManager().removeHeaderNamed(HttpRequestHdr.CONTENT_TYPE);
                 request.getHeaderManager().removeHeaderNamed(HttpRequestHdr.CONTENT_LENGTH);
 
                 // Set the form data
                 sampler.setArguments(urlConfig.getArguments());
                 // Set the file uploads
                 sampler.setHTTPFiles(urlConfig.getHTTPFileArgs().asArray());
             // used when postData is pure xml (eg. an xml-rpc call) or for PUT
             } else if (postData.trim().startsWith("<?") || "PUT".equals(sampler.getMethod())) {
                 sampler.addNonEncodedArgument("", postData, "");
             } else if (contentType == null || contentType.startsWith(HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED) ){
                 // It is the most common post request, with parameter name and values
                 // We also assume this if no content type is present, to be most backwards compatible,
                 // but maybe we should only parse arguments if the content type is as expected
                 sampler.parseArguments(postData.trim(), contentEncoding); //standard name=value postData
             } else if (postData.length() > 0) {
                 if (isBinaryContent(contentType)) {
                     try {
                         File tempDir = new File(getBinaryDirectory());
                         File out = File.createTempFile(request.getMethod(), getBinaryFileSuffix(), tempDir);
                         FileUtils.writeByteArrayToFile(out,request.getRawPostData());
                         HTTPFileArg [] files = {new HTTPFileArg(out.getPath(),"",contentType)};
                         sampler.setHTTPFiles(files);
                     } catch (IOException e) {
                         log.warn("Could not create binary file: "+e);
                     }
                 } else {
                     // Just put the whole postbody as the value of a parameter
                     sampler.addNonEncodedArgument("", postData, ""); //used when postData is pure xml (ex. an xml-rpc call)
                 }
             }
         }
     }
 
     /**
      * Compute sampler name
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeSamplerName(HTTPSamplerBase sampler,
             HttpRequestHdr request) {
         if (!HTTPConstants.CONNECT.equals(request.getMethod()) && isNumberRequests()) {
             incrementRequestNumber();
             sampler.setName(getRequestNumber() + " " + sampler.getPath());
         } else {
             sampler.setName(sampler.getPath());
         }
     }
 
     /**
      * Set path on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computePath(HTTPSamplerBase sampler, HttpRequestHdr request) {
         if(sampler.getContentEncoding() != null) {
             sampler.setPath(request.getPath(), sampler.getContentEncoding());
         }
         else {
             // Although the spec says UTF-8 should be used for encoding URL parameters,
             // most browser use ISO-8859-1 for default if encoding is not known.
             // We use null for contentEncoding, then the url parameters will be added
             // with the value in the URL, and the "encode?" flag set to false
             sampler.setPath(request.getPath(), null);
         }
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting path: " + sampler.getPath());
         }
     }
 
     /**
      * Compute content encoding
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      * @param pageEncodings Map<String, String>
      * @param formEncodings Map<String, String>
      * @throws MalformedURLException
      */
     protected void computeContentEncoding(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws MalformedURLException {
         URL pageUrl = null;
         if(sampler.isProtocolDefaultPort()) {
             pageUrl = new URL(sampler.getProtocol(), sampler.getDomain(), request.getPath());
         }
         else {
             pageUrl = new URL(sampler.getProtocol(), sampler.getDomain(), 
                     sampler.getPort(), request.getPath());
         }
         String urlWithoutQuery = request.getUrlWithoutQuery(pageUrl);
 
 
         String contentEncoding = computeContentEncoding(request, pageEncodings,
                 formEncodings, urlWithoutQuery);
         
         // Set the content encoding
         if(!StringUtils.isEmpty(contentEncoding)) {
             sampler.setContentEncoding(contentEncoding);
         } 
     }
     
     /**
      * Computes content encoding from request and if not found uses pageEncoding 
      * and formEncoding to see if URL was previously computed with a content type
      * @param request {@link HttpRequestHdr}
      * @param pageEncodings Map<String, String>
      * @param formEncodings Map<String, String>
      * @return String content encoding
      */
     protected String computeContentEncoding(HttpRequestHdr request,
             Map<String, String> pageEncodings,
             Map<String, String> formEncodings, String urlWithoutQuery) {
         // Check if the request itself tells us what the encoding is
         String contentEncoding = null;
         String requestContentEncoding = ConversionUtils.getEncodingFromContentType(
                 request.getContentType());
         if(requestContentEncoding != null) {
             contentEncoding = requestContentEncoding;
         }
         else {
             // Check if we know the encoding of the page
             if (pageEncodings != null) {
                 synchronized (pageEncodings) {
                     contentEncoding = pageEncodings.get(urlWithoutQuery);
                 }
             }
             // Check if we know the encoding of the form
             if (formEncodings != null) {
                 synchronized (formEncodings) {
                     String formEncoding = formEncodings.get(urlWithoutQuery);
                     // Form encoding has priority over page encoding
                     if (formEncoding != null) {
                         contentEncoding = formEncoding;
                     }
                 }
             }
         }
         return contentEncoding;
     }
 
     /**
      * Set protocol on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeProtocol(HTTPSamplerBase sampler,
             HttpRequestHdr request) {
         sampler.setProtocol(request.getProtocol(sampler));
     }
 
     /**
      * Set Port on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computePort(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setPort(request.serverPort());
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting port: " + sampler.getPort());
         }
     }
 
     /**
      * Set method on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeMethod(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setMethod(request.getMethod());
         log.debug("Proxy: setting method: " + sampler.getMethod());
     }
 
     /**
      * Set domain on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeDomain(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setDomain(request.serverName());
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting server: " + sampler.getDomain());
         }
     }
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
index b4e460ddb..bdcae2ff9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
@@ -1,414 +1,414 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.StringTokenizer;
 
-import org.apache.commons.lang.CharUtils;
+import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.gui.HeaderPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 //For unit tests, @see TestHttpRequestHdr
 
 /**
  * The headers of the client HTTP request.
  *
  */
 public class HttpRequestHdr {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String HTTP = "http"; // $NON-NLS-1$
     private static final String HTTPS = "https"; // $NON-NLS-1$
     private static final String PROXY_CONNECTION = "proxy-connection"; // $NON-NLS-1$
     public static final String CONTENT_TYPE = "content-type"; // $NON-NLS-1$
     public static final String CONTENT_LENGTH = "content-length"; // $NON-NLS-1$
 
 
     /**
      * Http Request method, uppercased, e.g. GET or POST.
      */
     private String method = ""; // $NON-NLS-1$
 
     /** CONNECT url. */
     private String paramHttps = ""; // $NON-NLS-1$
 
     /**
      * The requested url. The universal resource locator that hopefully uniquely
      * describes the object or service the client is requesting.
      */
     private String url = ""; // $NON-NLS-1$
 
     /**
      * Version of http being used. Such as HTTP/1.0.
      */
     private String version = ""; // NOTREAD // $NON-NLS-1$
 
     private byte[] rawPostData;
 
     private final Map<String, Header> headers = new HashMap<String, Header>();
 
     private final String httpSamplerName;
 
     private HeaderManager headerManager;
 
     public HttpRequestHdr() {
         this.httpSamplerName = ""; // $NON-NLS-1$
     }
 
     /**
      * @param httpSamplerName the http sampler name
      */
     public HttpRequestHdr(String httpSamplerName) {
         this.httpSamplerName = httpSamplerName;
     }
 
     /**
      * Parses a http header from a stream.
      *
      * @param in
      *            the stream to parse.
      * @return array of bytes from client.
      */
     public byte[] parse(InputStream in) throws IOException {
         boolean inHeaders = true;
         int readLength = 0;
         int dataLength = 0;
         boolean firstLine = true;
         ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
         ByteArrayOutputStream line = new ByteArrayOutputStream();
         int x;
         while ((inHeaders || readLength < dataLength) && ((x = in.read()) != -1)) {
             line.write(x);
             clientRequest.write(x);
             if (firstLine && !CharUtils.isAscii((char) x)){// includes \n
                 throw new IllegalArgumentException("Only ASCII supported in headers (perhaps SSL was used?)");
             }
             if (inHeaders && (byte) x == (byte) '\n') { // $NON-NLS-1$
                 if (line.size() < 3) {
                     inHeaders = false;
                     firstLine = false; // cannot be first line either
                 }
                 if (firstLine) {
                     parseFirstLine(line.toString());
                     firstLine = false;
                 } else {
                     // parse other header lines, looking for Content-Length
                     final int contentLen = parseLine(line.toString());
                     if (contentLen > 0) {
                         dataLength = contentLen; // Save the last valid content length one
                     }
                 }
                 if (log.isDebugEnabled()){
                     log.debug("Client Request Line: " + line.toString());
                 }
                 line.reset();
             } else if (!inHeaders) {
                 readLength++;
             }
         }
         // Keep the raw post data
         rawPostData = line.toByteArray();
 
         if (log.isDebugEnabled()){
             log.debug("rawPostData in default JRE encoding: " + new String(rawPostData)); // TODO - charset?
             log.debug("Request: " + clientRequest.toString());
         }
         return clientRequest.toByteArray();
     }
 
     private void parseFirstLine(String firstLine) {
         if (log.isDebugEnabled()) {
             log.debug("browser request: " + firstLine);
         }
         StringTokenizer tz = new StringTokenizer(firstLine);
         method = getToken(tz).toUpperCase(java.util.Locale.ENGLISH);
         url = getToken(tz);
         version = getToken(tz);
         if (log.isDebugEnabled()) {
             log.debug("parser input:  " + firstLine);
             log.debug("parsed method: " + method);
             log.debug("parsed url:    " + url);
             log.debug("parsed version:" + version);
         }
         // SSL connection
         if (getMethod().startsWith(HTTPConstants.CONNECT)) {
             paramHttps = url;
         }
         if (url.startsWith("/")) {
             url = HTTPS + "://" + paramHttps + url; // $NON-NLS-1$
         }
         log.debug("First Line: " + url);
     }
 
     /*
      * Split line into name/value pairs and store in headers if relevant
      * If name = "content-length", then return value as int, else return 0
      */
     private int parseLine(String nextLine) {
         int colon = nextLine.indexOf(':');
         if (colon <= 0){
             return 0; // Nothing to do
         }
         String name = nextLine.substring(0, colon).trim();
         String value = nextLine.substring(colon+1).trim();
         headers.put(name.toLowerCase(java.util.Locale.ENGLISH), new Header(name, value));
         if (name.equalsIgnoreCase(CONTENT_LENGTH)) {
             return Integer.parseInt(value);
         }
         return 0;
     }
 
     private HeaderManager createHeaderManager() {
         HeaderManager manager = new HeaderManager();
         for (String key : headers.keySet()) {
             if (!key.equals(PROXY_CONNECTION)
              && !key.equals(CONTENT_LENGTH)
              && !key.equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION)) {
                 manager.add(headers.get(key));
             }
         }
         manager.setName(JMeterUtils.getResString("header_manager_title")); // $NON-NLS-1$
         manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
         manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
         return manager;
     }
 
     public HeaderManager getHeaderManager() {
         if(headerManager == null) {
             headerManager = createHeaderManager();
         }
         return headerManager;
     }
 
     public String getContentType() {
         Header contentTypeHeader = headers.get(CONTENT_TYPE);
         if (contentTypeHeader != null) {
             return contentTypeHeader.getValue();
         }
         return null;
     }
 
     private boolean isMultipart(String contentType) {
         if (contentType != null && contentType.startsWith(HTTPConstants.MULTIPART_FORM_DATA)) {
             return true;
         }
         return false;
     }
 
     public MultipartUrlConfig getMultipartConfig(String contentType) {
         if(isMultipart(contentType)) {
             // Get the boundary string for the multiparts from the content type
             String boundaryString = contentType.substring(contentType.toLowerCase(java.util.Locale.ENGLISH).indexOf("boundary=") + "boundary=".length());
             return new MultipartUrlConfig(boundaryString);
         }
         return null;
     }
 
     //
     // Parsing Methods
     //
 
     /**
      * Find the //server.name from an url.
      *
      * @return server's internet name
      */
     public String serverName() {
         // chop to "server.name:x/thing"
         String str = url;
         int i = str.indexOf("//"); // $NON-NLS-1$
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf("/"); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(":"); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // Handle IPv6 urls
         if(str.startsWith("[")&& str.endsWith("]")) {
         	return str.substring(1, str.length()-1);
         }
         return str;
     }
 
     // TODO replace repeated substr() above and below with more efficient method.
 
     /**
      * Find the :PORT from http://server.ect:PORT/some/file.xxx
      *
      * @return server's port (or UNSPECIFIED if not found)
      */
     public int serverPort() {
         String str = url;
         // chop to "server.name:x/thing"
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf("/");
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(":");
         if (0 < i) {
             return Integer.parseInt(str.substring(i + 1).trim());
         }
         return HTTPSamplerBase.UNSPECIFIED_PORT;
     }
 
     /**
      * Find the /some/file.xxxx from http://server.ect:PORT/some/file.xxx
      *
      * @return the path
      */
     public String getPath() {
         String str = url;
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         i = str.indexOf("/");
         if (i < 0) {
             return "";
         }
         return str.substring(i);
     }
 
     /**
      * Returns the url string extracted from the first line of the client request.
      *
      * @return the url
      */
     public String getUrl(){
         return url;
     }
 
     /**
      * Returns the method string extracted from the first line of the client request.
      *
      * @return the method (will always be upper case)
      */
     public String getMethod(){
         return method;
     }
 
     /**
      * Returns the next token in a string.
      *
      * @param tk
      *            String that is partially tokenized.
      * @return The remainder
      */
     private String getToken(StringTokenizer tk) {
         if (tk.hasMoreTokens()) {
             return tk.nextToken();
         }
         return "";// $NON-NLS-1$
     }
 
 //    /**
 //     * Returns the remainder of a tokenized string.
 //     *
 //     * @param tk
 //     *            String that is partially tokenized.
 //     * @return The remainder
 //     */
 //    private String getRemainder(StringTokenizer tk) {
 //        StringBuilder strBuff = new StringBuilder();
 //        if (tk.hasMoreTokens()) {
 //            strBuff.append(tk.nextToken());
 //        }
 //        while (tk.hasMoreTokens()) {
 //            strBuff.append(" "); // $NON-NLS-1$
 //            strBuff.append(tk.nextToken());
 //        }
 //        return strBuff.toString();
 //    }
 
     public String getUrlWithoutQuery(URL _url) {
         String fullUrl = _url.toString();
         String urlWithoutQuery = fullUrl;
         String query = _url.getQuery();
         if(query != null) {
             // Get rid of the query and the ?
             urlWithoutQuery = urlWithoutQuery.substring(0, urlWithoutQuery.length() - query.length() - 1);
         }
         return urlWithoutQuery;
     }
 
     /**
      * @return the httpSamplerName
      */
     public String getHttpSamplerName() {
         return httpSamplerName;
     }
 
     /**
      * @return byte[] Raw post data
      */
     public byte[] getRawPostData() {
         return rawPostData;
     }
 
     /**
      * @param sampler {@link HTTPSamplerBase}
      * @return String Protocol (http or https)
      */
     public String getProtocol(HTTPSamplerBase sampler) {
         if (url.indexOf("//") > -1) {
             String protocol = url.substring(0, url.indexOf(":"));
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to : " + protocol);
             }
             return protocol;
         } else if (sampler.getPort() == HTTPConstants.DEFAULT_HTTPS_PORT) {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to https");
             }
             return HTTPS;
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy setting default protocol to: http");
             }
             return HTTP;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 2313f2601..48544025f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1037 +1,1037 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.security.GeneralSecurityException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.http.Header;
 import org.apache.http.HttpConnection;
 import org.apache.http.HttpConnectionMetrics;
 import org.apache.http.HttpEntity;
 import org.apache.http.HttpException;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpRequestInterceptor;
 import org.apache.http.HttpResponse;
 import org.apache.http.HttpResponseInterceptor;
 import org.apache.http.NameValuePair;
 import org.apache.http.StatusLine;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.auth.UsernamePasswordCredentials;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.HttpRequestRetryHandler;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpDelete;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.client.params.CookiePolicy;
 import org.apache.http.client.protocol.ResponseContentEncoding;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.ContentType;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
 import org.apache.http.entity.mime.HttpMultipartMode;
 import org.apache.http.entity.mime.MultipartEntity;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.impl.client.RequestWrapper;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.ExecutionContext;
 import org.apache.http.protocol.HttpContext;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HC4TrustAllSSLSocketFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SSLSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  *
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 1); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient4.retrycount", 1);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack, to be removed later
 
     private static final HttpResponseInterceptor METRICS_SAVER = new HttpResponseInterceptor(){
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             HttpConnection conn = (HttpConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
             HttpConnectionMetrics metrics = conn.getMetrics();
             context.setAttribute(CONTEXT_METRICS, metrics);
         }
     };
     private static final HttpRequestInterceptor METRICS_RESETTER = new HttpRequestInterceptor() {
 		public void process(HttpRequest request, HttpContext context)
 				throws HttpException, IOException {
             HttpConnection conn = (HttpConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
 			HttpConnectionMetrics metrics = conn.getMetrics();
 			metrics.reset();
 		}
 	};
 
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS = 
         new ThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<HttpClientKey, HttpClient>();
         }
     };
 
     // Scheme used for slow HTTP sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     
     // We always want to override the HTTPS scheme, because we want to trust all certificates and hosts
     private static final Scheme HTTPS_SCHEME;
 
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
     
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
         
         // TODO use new setDefaultHttpParams(HttpParams params) static method when 4.1 is available
         final DefaultHttpClient dhc = new DefaultHttpClient();
         DEFAULT_HTTP_PARAMS = dhc.getParams(); // Get the default params
         dhc.getConnectionManager().shutdown(); // Tidy up
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         // Set up HTTP scheme override if necessary
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(HTTPConstants.PROTOCOL_HTTP, HTTPConstants.DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         
         // We always want to override the HTTPS scheme
         Scheme https = null;
         if (CPS_HTTPS > 0) {
             log.info("Setting up HTTPS SlowProtocol, cps="+CPS_HTTPS);
             try {
                 https = new Scheme(HTTPConstants.PROTOCOL_HTTPS, HTTPConstants.DEFAULT_HTTPS_PORT, new SlowHC4SSLSocketFactory(CPS_HTTPS));
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise SLOW_HTTPS scheme, cps="+CPS_HTTPS, e);
             }
         } else {
             log.info("Setting up HTTPS TrustAll scheme");
             try {
                 https = new Scheme(HTTPConstants.PROTOCOL_HTTPS, HTTPConstants.DEFAULT_HTTPS_PORT, new HC4TrustAllSSLSocketFactory());
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise HTTPS TrustAll scheme", e);
             }
         }
         HTTPS_SCHEME = https;
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         HttpClient httpClient = setupClient(url);
         
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(HTTPConstants.POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(HTTPConstants.PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HTTPConstants.HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(HTTPConstants.TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(HTTPConstants.OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(HTTPConstants.DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(HTTPConstants.GET)) {
                 httpRequest = new HttpGet(uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             errorResult(e, res);
             return res;
         }
 
         HttpContext localContext = new BasicHttpContext();
 
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                res.sampleEnd();
                res.setResponseNoContent();
                res.setSuccessful(true);
                return res;
            }
         }
 
         try {
             currentRequest = httpRequest;
             // Handle the various methods
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData((HttpPost)httpRequest);
                 res.setQueryString(postBody);
             } else if (method.equals(HTTPConstants.PUT)) {
                 String putBody = sendPutData((HttpPut)httpRequest);
                 res.setQueryString(putBody);
             }
             HttpResponse httpResponse = httpClient.execute(httpRequest, localContext); // perform the sample
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST)));
 
             Header contentType = httpResponse.getLastHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 InputStream instream = entity.getContent();
                 res.setResponseData(readResponse(res, instream, (int) entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             HttpConnectionMetrics  metrics = (HttpConnectionMetrics) localContext.getAttribute(CONTEXT_METRICS);
             long headerBytes = 
                 res.getResponseHeaders().length()   // condensed length (without \r)
               + httpResponse.getAllHeaders().length // Add \r for each header
               + 1 // Add \r for initial header
               + 2; // final \r\n before data
             long totalBytes = metrics.getReceivedBytesCount();
             res.setHeadersSize((int) headerBytes);
             res.setBodySize((int)(totalBytes - headerBytes));
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                 URI redirectURI = req.getURI();
                 if (redirectURI.isAbsolute()){
                     res.setURL(redirectURI.toURL());
                 } else {
                     res.setURL(new URL(new URL(target.toURI()),redirectURI.toString()));
                 }
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpResponse, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpResponse, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
         } catch (IOException e) {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } catch (RuntimeException e) {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } finally {
             currentRequest = null;
         }
         return res;
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final String target; // protocol://[user:pass@]host:[port]
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         public HttpClientKey(URL url, boolean b, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             // N.B. need to separate protocol from authority otherwise http://server would match https://erver
             // could use separate fields, but simpler to combine them
             this.target = url.getProtocol()+"://"+url.getAuthority();
             this.hasProxy = b;
             this.proxyHost = proxyHost;
             this.proxyPort = proxyPort;
             this.proxyUser = proxyUser;
             this.proxyPass = proxyPass;
             this.hashCode = getHash();
         }
         
         private int getHash() {
             int hash = 17;
             hash = hash*31 + (hasProxy ? 1 : 0);
             if (hasProxy) {
                 hash = hash*31 + getHash(proxyHost);
                 hash = hash*31 + proxyPort;
                 hash = hash*31 + getHash(proxyUser);
                 hash = hash*31 + getHash(proxyPass);
             }
             hash = hash*31 + target.hashCode();
             return hash;
         }
 
         // Allow for null strings
         private int getHash(String s) {
             return s == null ? 0 : s.hashCode(); 
         }
         
         @Override
         public boolean equals (Object obj){
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof HttpClientKey)) {
                 return false;
             }
             HttpClientKey other = (HttpClientKey) obj;
             if (this.hasProxy) { // otherwise proxy String fields may be null
                 return 
                 this.hasProxy == other.hasProxy &&
                 this.proxyPort == other.proxyPort &&
                 this.proxyHost.equals(other.proxyHost) &&
                 this.proxyUser.equals(other.proxyUser) &&
                 this.proxyPass.equals(other.proxyPass) &&
                 this.target.equals(other.target);
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.target.equals(other.target);
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
     }
 
     private HttpClient setupClient(URL url) {
 
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         
         final String host = url.getHost();
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, (useStaticProxy || useDynamicProxy), 
                 useDynamicProxy ? proxyHost : PROXY_HOST,
                 useDynamicProxy ? proxyPort : PROXY_PORT,
                 useDynamicProxy ? getProxyUser() : PROXY_USER,
                 useDynamicProxy ? getProxyPass() : PROXY_PASS);
         
         HttpClient httpClient = map.get(key);
 
         if (httpClient == null){ // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
             
             httpClient = new DefaultHttpClient(clientParams){
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false) {
                         // TODO HACK to fix https://issues.apache.org/jira/browse/HTTPCLIENT-1120
                         // can hopefully be removed when 4.1.3 or 4.2 are released
                         @Override
                         public boolean retryRequest(IOException ex, int count, HttpContext ctx) {
                             Object request = ctx.getAttribute(ExecutionContext.HTTP_REQUEST);
                             if(request instanceof HttpUriRequest){
                                 if (request instanceof RequestWrapper) {
                                     request = ((RequestWrapper) request).getOriginal();
                                 }
                                 if(((HttpUriRequest)request).isAborted()){
                                     log.warn("Workround for HTTPCLIENT-1120 request retry: "+ex);
                                     return false;
                                 }
                             }
                             /*
                              * When connect fails due to abort, the request is not in the context.
                              * Tried adding the request - with a new key - to the local context in the sample() method,
                              * but the request was not flagged as aborted, so that did not help.
                              * So we check for any specific exception that is triggered.
                              */
                             if (
                                    (ex instanceof java.net.BindException && 
                                     ex.getMessage().contains("Address already in use: connect"))    
                                 || 
                                     ex.getMessage().contains("Request aborted") // plain IOException                                
                                 ) {
                                 /*
                                  * The above messages may be generated by aborted connects.
                                  * If either occurs in other situations, retrying is unlikely to help,
                                  * so preventing retry should not cause a problem.
                                 */
                                 log.warn("Workround for HTTPCLIENT-1120 connect retry: "+ex);
                                 return false;
                             }
                             return super.retryRequest(ex, count, ctx);
                         } // end of hack
                     }; // set retry count
                 }
             };
             ((AbstractHttpClient) httpClient).addResponseInterceptor(new ResponseContentEncoding());
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             ((AbstractHttpClient) httpClient).addRequestInterceptor(METRICS_RESETTER); 
             
             // Override the defualt schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             if (HTTPS_SCHEME != null){
                 schemeRegistry.register(HTTPS_SCHEME);
             }
 
             // Set up proxy details
             if (useDynamicProxy){
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 String proxyUser = getProxyUser();
                 if (proxyUser.length() > 0) {
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new UsernamePasswordCredentials(proxyUser, getProxyPass()));
                 }
             } else if (useStaticProxy) {
                 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 if (PROXY_USER.length() > 0)
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(PROXY_HOST, PROXY_PORT),
                             new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
             }
 
             // Bug 52126 - we do our own cookie handling
             clientParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
 
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
 
             map.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager());
 
         return httpClient;
     }
 
     private void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final String ipSource = getIpSource();
     if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
         InetAddress inetAddr = InetAddress.getByName(ipSource);
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, inetAddr);
     } else if (localAddress != null){
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
     } else { // reset in case was set previously
         requestParams.removeParameter(ConnRoutePNames.LOCAL_ADDRESS);
     }
 
     int rto = getResponseTimeout();
     if (rto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, rto);
     }
 
     int cto = getConnectTimeout();
     if (cto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, cto);
     }
 
     requestParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, getAutoRedirects());
     
     // a well-behaved browser is supposed to send 'Connection: close'
     // with the last request to an HTTP server. Instead, most browsers
     // leave it to the server to close the connection after their
     // timeout period. Leave it to the JMeter user to decide.
     if (getUseKeepAlive()) {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
     }
 
     setConnectionHeaders(httpRequest, url, getHeaderManager(), getCacheManager());
 
     String cookies = setConnectionCookie(httpRequest, url, getCookieManager());
 
     if (res != null) {
         res.setCookies(cookies);
     }
 
 }
 
     
     /**
      * Set any default request headers to include
      *
      * @param request the HttpRequest to be used
      */
     protected void setDefaultRequestHeaders(HttpRequest request) {
      // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param response
      *            containing the headers
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response) {
         StringBuilder headerBuf = new StringBuilder();
         Header[] rh = response.getAllHeaders();
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             headerBuf.append(rh[i].getName());
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param request <code>HttpRequest</code> for the request
      * @param url <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     private String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
     
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param request
      *            <code>HttpRequest</code> which represents the request
      * @param url
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = url.getPort();
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             if (port != -1) {
                                 if (port == url.getDefaultPort()) {
                                     port = -1; // no need to specify the port if it is the default
                                 }
                             }
                             request.getParams().setParameter(ClientPNames.VIRTUAL_HOST, new HttpHost(v, port));
                         } else {
                             request.addHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(url, request);
         }
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getAllHeaders();
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(url);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " > D="+domain+" R="+realm);
                     }
                     credentialsProvider.setCredentials(
                             new AuthScope(url.getHost(), url.getPort(), realm.length()==0 ? null : realm),
                             new NTCredentials(username, auth.getPass(), localHost, domain));
             } else {
                 credentialsProvider.clear();
             }
         } else {
             credentialsProvider.clear();            
         }
     }
 
     // Helper class so we can generate request data without dumping entire file contents
     private static class ViewableFileBody extends FileBody {
         private boolean hideFileData;
         
         public ViewableFileBody(File file, String mimeType) {
             super(file, mimeType);
             hideFileData = false;
         }
 
         @Override
         public void writeTo(final OutputStream out) throws IOException {
             if (hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             } else {
                 super.writeTo(out);
             }
         }
     }
 
     // TODO needs cleaning up
     private String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg files[] = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(isNullOrEmptyTrimmed(contentEncoding)) {
                 contentEncoding = null;
             }
             Charset charset = null;
             if(contentEncoding != null) {
                 charset = Charset.forName(contentEncoding);
             }
 
             // Write the request to our own stream
             MultipartEntity multiPart = new MultipartEntity(
                     getDoBrowserCompatibleMultipart() ? HttpMultipartMode.BROWSER_COMPATIBLE : HttpMultipartMode.STRICT,
                             null, charset);
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                String parameterName = arg.getName();
                if (arg.isSkippable(parameterName)){
                    continue;
                }
                FormBodyPart formPart;
                StringBody stringBody = new StringBody(arg.getValue(),
                        Charset.forName(contentEncoding == null ? "US-ASCII" : contentEncoding));
                formPart = new FormBodyPart(arg.getName(), stringBody);                   
                multiPart.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 fileBodies[i] = new ViewableFileBody(new File(file.getPath()), file.getMimeType());
                 multiPart.addPart(file.getParamName(),fileBodies[i]);
             }
 
             post.setEntity(multiPart);
 
             if (multiPart.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 multiPart.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
             } else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
 
 //            // Set the content type TODO - needed?
 //            String multiPartContentType = multiPart.getContentType().getValue();
 //            post.setHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
         } else { // not multipart
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),(ContentType) null);// TODO is null correct?
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(isNullOrEmptyTrimmed(contentEncoding)) {
                     contentEncoding=null;
                 } else {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
                     haveContentEncoding = true;
                 }
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 if(getSendParameterValuesAsPostBody()) {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     ContentType contentType = 
                             ContentType.create(post.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     StringEntity requestEntity = new StringEntity(postBody.toString(), contentType);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString()); // TODO OK?
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                     String urlContentEncoding = contentEncoding;
                     if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                         // Use the default encoding for urls
                         urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                     }
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)){
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if(!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         nvps.add(new BasicNameValuePair(parameterName, parameterValue));
                     }
                     UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, urlContentEncoding);
                     post.setEntity(entity);
                     if (entity.isRepeatable()){
                         ByteArrayOutputStream bos = new ByteArrayOutputStream();
                         post.getEntity().writeTo(bos);
                         bos.flush();
                         // We get the posted bytes using the encoding used to create it
                         if (contentEncoding != null) {
                             postedBody.append(new String(bos.toByteArray(), contentEncoding));
                         } else {
                             postedBody.append(new String(bos.toByteArray(), SampleResult.DEFAULT_HTTP_ENCODING));
                         }
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
 
 //                // If the request entity is repeatable, we can send it first to
 //                // our own stream, so we can return it
 //                if(post.getEntity().isRepeatable()) {
 //                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
 //                    post.getEntity().writeTo(bos);
 //                    bos.flush();
 //                    // We get the posted bytes using the encoding used to create it
 //                    if (contentEncoding != null) {
 //                        postedBody.append(new String(bos.toByteArray(), contentEncoding));
 //                    } else {
 //                        postedBody.append(new String(bos.toByteArray()));
 //                    }
 //                    bos.close();
 //                }
 //                else {
 //                    postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
 //                }
             }
         }
         return postedBody.toString();
     }
 
     // TODO - implementation not fully tested
     private String sendPutData(HttpPut put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type
         Header contentTypeHeader = put.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
 
         // Check for local contentEncoding override
         final String contentEncoding = getContentEncoding();
         boolean haveContentEncoding = !isNullOrEmptyTrimmed(contentEncoding);
         
         HttpParams putParams = put.getParams();
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileEntity fileRequestEntity = new FileEntity(new File(files[0].getPath()), (ContentType) null); // TODO is null correct?
             put.setEntity(fileRequestEntity);
 
             // We just add placeholder text for file content
             putBody.append("<actual file content, not shown here>");
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index e0d240372..50428ae3b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1047 +1,1047 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.commons.io.IOUtils;
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"}));
     
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = HTTPConstants.PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
     
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
     
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     private static final String CONCURRENT_POOL_DEFAULT = "4"; // default for concurrent pool (do not change)
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
     
     private static final long AWAIT_TERMINATION_TIMEOUT = 
         JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
     
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES = 
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     public static final int CONCURRENT_POOL_SIZE = 4; // Default concurrent pool size for download embedded resources
     
     
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
         DEFAULT_METHOD, // i.e. GET
         HTTPConstants.POST,
         HTTPConstants.HEAD,
         HTTPConstants.PUT,
         HTTPConstants.OPTIONS,
         HTTPConstants.TRACE,
         HTTPConstants.DELETE,
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private static final String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere 
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
 	// Control reuse of cached SSL Context in subsequent iterations
 	private static final boolean USE_CACHED_SSL_CONTEXT = 
 	        JMeterUtils.getPropDefault("https.use.cached.ssl.context", true);//$NON-NLS-1$
     
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
         
 		log.info("Reuse SSL session context on subsequent iterations: "
 				+ USE_CACHED_SSL_CONTEXT);
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Post body,
      * i.e. without any additional wrapping
      *
      * @return true if specified file is to be sent as the body,
      * i.e. FileField is blank
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
             && (files[0].getPath().length() > 0)
             && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the post body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if(getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 if(arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(HTTPConstants.POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0 ) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments if the request is a GET or DELETE.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         if (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     public void setContentEncoding(String value) {
         setProperty(CONTENT_ENCODING, value);
     }
 
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
     }
 
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
    public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      * 
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if(nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren(){
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)  ? HTTPConstants.DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS) ? HTTPConstants.DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         if (port == UNSPECIFIED_PORT ||
                 (HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT) ||
                 (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE,"");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      * 
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error: " + res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER = 
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain=null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")){ // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(String contentEncoding) {
          // Check if the sampler has a specified content encoding
          if(contentEncoding == null || contentEncoding.trim().length() == 0) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(contentEncoding));
             }
             catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string; 
      *            if non-null then it is used to decode the 
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         for (int i = 0; i < args.length; i++) {
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if(HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementBeanInfo.java b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementBeanInfo.java
index 639e0725a..d7f025f56 100644
--- a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementBeanInfo.java
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElementBeanInfo.java
@@ -1,123 +1,123 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 /*
  * Created on May 15, 2004
  */
 package org.apache.jmeter.protocol.jdbc.config;
 
 import java.beans.PropertyDescriptor;
 import java.sql.Connection;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.testbeans.BeanInfoSupport;
 import org.apache.jmeter.testbeans.gui.TypeEditor;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class DataSourceElementBeanInfo extends BeanInfoSupport {
     private static final Logger log = LoggingManager.getLoggerForClass();
     private static Map<String,Integer> TRANSACTION_ISOLATION_MAP = new HashMap<String, Integer>(5);
     static {
         // Will use default isolation
         TRANSACTION_ISOLATION_MAP.put("DEFAULT", Integer.valueOf(-1));
         TRANSACTION_ISOLATION_MAP.put("TRANSACTION_NONE", Integer.valueOf(Connection.TRANSACTION_NONE));
         TRANSACTION_ISOLATION_MAP.put("TRANSACTION_READ_COMMITTED", Integer.valueOf(Connection.TRANSACTION_READ_COMMITTED));
         TRANSACTION_ISOLATION_MAP.put("TRANSACTION_READ_UNCOMMITTED", Integer.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED));
         TRANSACTION_ISOLATION_MAP.put("TRANSACTION_REPEATABLE_READ", Integer.valueOf(Connection.TRANSACTION_REPEATABLE_READ));
         TRANSACTION_ISOLATION_MAP.put("TRANSACTION_SERIALIZABLE", Integer.valueOf(Connection.TRANSACTION_SERIALIZABLE));
     }
     
     public DataSourceElementBeanInfo() {
         super(DataSourceElement.class);
     
         createPropertyGroup("varName", new String[] { "dataSource" });
 
         createPropertyGroup("pool", new String[] { "poolMax", "timeout", 
                 "trimInterval", "autocommit", "transactionIsolation"  });
 
         createPropertyGroup("keep-alive", new String[] { "keepAlive", "connectionAge", "checkQuery" });
 
         createPropertyGroup("database", new String[] { "dbUrl", "driver", "username", "password" });
 
         PropertyDescriptor p = property("dataSource");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "");
         p = property("poolMax");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "10");
         p = property("timeout");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "10000");
         p = property("trimInterval");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "60000");
         p = property("autocommit");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, Boolean.TRUE);
         p = property("transactionIsolation");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "DEFAULT");
         p.setValue(NOT_EXPRESSION, Boolean.TRUE);
         Set<String> modesSet = TRANSACTION_ISOLATION_MAP.keySet();
         String[] modes = modesSet.toArray(new String[modesSet.size()]);
         p.setValue(TAGS, modes);
         p = property("keepAlive");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, Boolean.TRUE);
         p = property("connectionAge");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "5000");
         p = property("checkQuery");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "Select 1");
         p = property("dbUrl");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "");
         p = property("driver");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "");
         p = property("username");
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "");
         p = property("password", TypeEditor.PasswordEditor);
         p.setValue(NOT_UNDEFINED, Boolean.TRUE);
         p.setValue(DEFAULT, "");
     }
 
     /**
      * @param tag 
      * @return int value for String
      */
     public static int getTransactionIsolationMode(String tag) {
         if (!StringUtils.isEmpty(tag)) {
             Integer isolationMode = TRANSACTION_ISOLATION_MAP.get(tag);
             if (isolationMode == null) {
                 try {
                     return Integer.parseInt(tag);
                 } catch (NumberFormatException e) {
                     log.warn("Illegal transaction isolation configuration '" + tag + "'");
                 }
             }
         }
         return -1;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
index bd5c75654..8448a0999 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
@@ -1,164 +1,164 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.util.Properties;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.naming.NamingException;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * InitialContextFactory is responsible for getting an instance of the initial context.
  */
 public class InitialContextFactory {
 
     private static final ConcurrentHashMap<String, Context> MAP = new ConcurrentHashMap<String, Context>();
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Look up the context from the local cache, creating it if necessary.
      * 
      * @param initialContextFactory used to set the property {@link Context#INITIAL_CONTEXT_FACTORY}
      * @param providerUrl used to set the property {@link Context#PROVIDER_URL}
      * @param useAuth set true if security is to be used.
      * @param securityPrincipal used to set the property {@link Context#SECURITY_PRINCIPAL}
      * @param securityCredentials used to set the property {@link Context#SECURITY_CREDENTIALS}
      * @return the context, never null
      * @throws NamingException 
      */
     public static Context lookupContext(String initialContextFactory, 
             String providerUrl, boolean useAuth, String securityPrincipal, String securityCredentials) throws NamingException {
         String cacheKey = createKey(Thread.currentThread().getId(),initialContextFactory ,providerUrl, securityPrincipal, securityCredentials);
         Context ctx = MAP.get(cacheKey);
         if (ctx == null) {
             Properties props = new Properties();
             props.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
             props.setProperty(Context.PROVIDER_URL, providerUrl);
             if (useAuth && securityPrincipal != null && securityCredentials != null
                     && securityPrincipal.length() > 0 && securityCredentials.length() > 0) {
                 props.setProperty(Context.SECURITY_PRINCIPAL, securityPrincipal);
                 props.setProperty(Context.SECURITY_CREDENTIALS, securityCredentials);
                 log.info("authentication properties set");
             }
             try {
                 ctx = new InitialContext(props);
             } catch (NoClassDefFoundError e){
                 throw new NamingException(e.toString());
             } catch (Exception e) {
                 throw new NamingException(e.toString());
             }
             // we want to return the context that is actually in the map
             // if it's the first put we will have a null result
             Context oldCtx = MAP.putIfAbsent(cacheKey, ctx);
             if(oldCtx != null) {
                 // There was an object in map, destroy the temporary and return one in map (oldCtx)
                 try {
                     ctx.close();
                 } catch (Exception e) {
                     // NOOP
                 }
                 ctx = oldCtx;
             }
             // else No object in Map, ctx is the one
         }
         return ctx;
     }
 
     /**
      * Create cache key
      * @param threadId Thread Id
      * @param initialContextFactory
      * @param providerUrl
      * @param securityPrincipal
      * @param securityCredentials
      * @return
      */
     private static String createKey(
             long threadId,
             String initialContextFactory,
             String providerUrl, String securityPrincipal,
             String securityCredentials) {
        StringBuilder builder = new StringBuilder();
        builder.append(threadId);
        builder.append("#");
        builder.append(initialContextFactory);
        builder.append("#");
        builder.append(providerUrl);
        builder.append("#");
        if(!StringUtils.isEmpty(securityPrincipal)) {
            builder.append(securityPrincipal);
            builder.append("#");
        }
        if(!StringUtils.isEmpty(securityCredentials)) {
            builder.append(securityCredentials);
        }
        return builder.toString();
     }
 
     /**
      * Initialize the JNDI initial context
      *
      * @param useProps if true, create a new InitialContext; otherwise use the other parameters to call
      * {@link #lookupContext(String, String, boolean, String, String)} 
      * @param initialContextFactory
      * @param providerUrl
      * @param useAuth
      * @param securityPrincipal
      * @param securityCredentials
      * @return  the context, never null
      * @throws NamingException 
      */
     public static Context getContext(boolean useProps, 
             String initialContextFactory, String providerUrl, 
             boolean useAuth, String securityPrincipal, String securityCredentials) throws NamingException {
         if (useProps) {
             try {
                 return new InitialContext();
             } catch (NoClassDefFoundError e){
                 throw new NamingException(e.toString());
             } catch (Exception e) {
                 throw new NamingException(e.toString());
             }
         } else {
             return lookupContext(initialContextFactory, providerUrl, useAuth, securityPrincipal, securityCredentials);
         }
     }
     
     /**
      * clear all the InitialContext objects.
      */
     public static void close() {
         for (Context ctx : MAP.values()) {
             try {
                 ctx.close();
             } catch (NamingException e) {
                 log.error(e.getMessage());
             }
         }
         MAP.clear();
         log.info("InitialContextFactory.close() called and Context instances cleaned up");
     }
 }
\ No newline at end of file
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
index ccf84772c..6ba9fb04c 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
@@ -1,145 +1,145 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import javax.jms.Connection;
 import javax.jms.ConnectionFactory;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.Session;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Receiver of pseudo-synchronous reply messages.
  *
  */
 public class Receiver implements Runnable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private volatile boolean active;
 
     private final Session session;
 
     private final MessageConsumer consumer;
 
     private final Connection conn;
 
     private final boolean useResMsgIdAsCorrelId;
 
 
     /**
      * Constructor
      * @param factory
      * @param receiveQueue Receive Queue
      * @param principal Username
      * @param credentials Password
      * @param useResMsgIdAsCorrelId
      * @param jmsSelector JMS Selector
      * @throws JMSException
      */
     private Receiver(ConnectionFactory factory, Destination receiveQueue, String principal, String credentials, boolean useResMsgIdAsCorrelId, String jmsSelector) throws JMSException {
         if (null != principal && null != credentials) {
             log.info("creating receiver WITH authorisation credentials. UseResMsgId="+useResMsgIdAsCorrelId);
             conn = factory.createConnection(principal, credentials);
         }else{
             log.info("creating receiver without authorisation credentials. UseResMsgId="+useResMsgIdAsCorrelId);
             conn = factory.createConnection(); 
         }
         session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         if(log.isDebugEnabled()) {
             log.debug("Receiver - ctor. Creating consumer with JMS Selector:"+jmsSelector);
         }
         if(StringUtils.isEmpty(jmsSelector)) {
             consumer = session.createConsumer(receiveQueue);
         } else {
             consumer = session.createConsumer(receiveQueue, jmsSelector);
         }
         this.useResMsgIdAsCorrelId = useResMsgIdAsCorrelId;
         log.debug("Receiver - ctor. Starting connection now");
         conn.start();
         log.info("Receiver - ctor. Connection to messaging system established");
     }
 
     /**
      * Create a receiver to process responses.
      * 
      * @param factory
      * @param receiveQueue
      * @param principal
      * @param credentials
      * @param useResMsgIdAsCorrelId true if should use JMSMessageId, false if should use JMSCorrelationId
      * @param jmsSelector JMS selector
      * @return the Receiver which will process the responses
      * @throws JMSException
      */
     public static Receiver createReceiver(ConnectionFactory factory, Destination receiveQueue,
             String principal, String credentials, boolean useResMsgIdAsCorrelId, String jmsSelector)
             throws JMSException {
         Receiver receiver = new Receiver(factory, receiveQueue, principal, credentials, useResMsgIdAsCorrelId, jmsSelector);
         Thread thread = new Thread(receiver, Thread.currentThread().getName()+"-JMS-Receiver");
         thread.start();
         return receiver;
     }
 
     public void run() {
         active = true;
         Message reply;
 
         while (active) {
             reply = null;
             try {
                 reply = consumer.receive(5000);
                 if (reply != null) {
                     String messageKey;
                     final MessageAdmin admin = MessageAdmin.getAdmin();
                     if (useResMsgIdAsCorrelId){
                         messageKey = reply.getJMSMessageID();
                         synchronized (admin) {// synchronize with FixedQueueExecutor
                             admin.putReply(messageKey, reply);                            
                         }
                     } else {
                         messageKey = reply.getJMSCorrelationID();
                         if (messageKey == null) {// JMSMessageID cannot be null
                             log.warn("Received message with correlation id null. Discarding message ...");
                         } else {
                             admin.putReply(messageKey, reply);
                         }
                     }
                 }
 
             } catch (JMSException e1) {
                 log.error("Error handling receive",e1);
             }
         }
         Utils.close(consumer, log);
         Utils.close(session, log);
         Utils.close(conn, log);
     }
 
     public void deactivate() {
         active = false;
     }
 
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
index 225752a24..f3bbc4754 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
@@ -1,477 +1,477 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Enumeration;
 
 import javax.jms.JMSException;
 import javax.jms.MapMessage;
 import javax.jms.Message;
 import javax.jms.TextMessage;
 import javax.naming.NamingException;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.protocol.jms.client.InitialContextFactory;
 import org.apache.jmeter.protocol.jms.client.ReceiveSubscriber;
 import org.apache.jmeter.protocol.jms.control.gui.JMSSubscriberGui;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This class implements the JMS Subscriber sampler.
  * It supports both receive and onMessage strategies via the ReceiveSubscriber class.
  * 
  */
 // TODO: do we need to implement any kind of connection pooling?
 // If so, which connections should be shared?
 // Should threads share connections to the same destination?
 // What about cross-thread sharing?
 
 // Note: originally the code did use the ClientPool to "share" subscribers, however since the
 // key was "this" and each sampler is unique - nothing was actually shared.
 
 public class SubscriberSampler extends BaseJMSSampler implements Interruptible, ThreadListener, TestStateListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Default wait (ms) for a message if timeouts are not enabled
     // This is the maximum time the sampler can be blocked.
     private static final long DEFAULT_WAIT = 500L;
 
     // No need to synch/ - only used by sampler and ClientPool (which does its own synch)
     private transient ReceiveSubscriber SUBSCRIBER = null;
 
     private transient volatile boolean interrupted = false;
 
     private transient long timeout;
     
     private transient boolean useReceive;
 
     // This will be null if initialization succeeds.
     private transient Exception exceptionDuringInit;
 
     // If true, start/stop subscriber for each sample
     private transient boolean stopBetweenSamples;
 
     // Don't change the string, as it is used in JMX files
     private static final String CLIENT_CHOICE = "jms.client_choice"; // $NON-NLS-1$
     private static final String TIMEOUT = "jms.timeout"; // $NON-NLS-1$
     private static final String TIMEOUT_DEFAULT = ""; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID = "jms.durableSubscriptionId"; // $NON-NLS-1$
     private static final String CLIENT_ID = "jms.clientId"; // $NON-NLS-1$
     private static final String JMS_SELECTOR = "jms.selector"; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID_DEFAULT = "";
     private static final String CLIENT_ID_DEFAULT = ""; // $NON-NLS-1$
     private static final String JMS_SELECTOR_DEFAULT = ""; // $NON-NLS-1$
     private static final String STOP_BETWEEN = "jms.stop_between_samples"; // $NON-NLS-1$
     private static final String SEPARATOR = "jms.separator"; // $NON-NLS-1$
     private static final String SEPARATOR_DEFAULT = ""; // $NON-NLS-1$
 
     
     private transient boolean START_ON_SAMPLE = false;
 
 	private transient String separator;
 
     public SubscriberSampler() {
         super();
     }
 
     /**
      * Create the OnMessageSubscriber client and set the sampler as the message
      * listener.
      * @throws JMSException 
      * @throws NamingException 
      *
      */
     private void initListenerClient() throws JMSException, NamingException {
         SUBSCRIBER = new ReceiveSubscriber(0, getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(),
                     getProviderUrl(), getConnectionFactory(), getDestination(), getDurableSubscriptionId(),
                     getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         setupSeparator();
         log.debug("SubscriberSampler.initListenerClient called");
     }
 
     /**
      * Create the ReceiveSubscriber client for the sampler.
      * @throws NamingException 
      * @throws JMSException 
      */
     private void initReceiveClient() throws NamingException, JMSException {
         SUBSCRIBER = new ReceiveSubscriber(getUseJNDIPropertiesAsBoolean(),
                 getJNDIInitialContextFactory(), getProviderUrl(), getConnectionFactory(), getDestination(),
                 getDurableSubscriptionId(), getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         setupSeparator();
         log.debug("SubscriberSampler.initReceiveClient called");
     }
 
     /**
      * sample method will check which client it should use and call the
      * appropriate client specific sample method.
      *
      * @return the appropriate sample result
      */
     // TODO - should we call start() and stop()?
     @Override
     public SampleResult sample() {
         // run threadStarted only if Destination setup on each sample
         if (!isDestinationStatic()) {
             threadStarted(true);
         }
         SampleResult result = new SampleResult();
         result.setDataType(SampleResult.TEXT);
         result.setSampleLabel(getName());
         result.sampleStart();
         if (exceptionDuringInit != null) {
             result.sampleEnd();
             result.setSuccessful(false);
             result.setResponseCode("000");
             result.setResponseMessage(exceptionDuringInit.toString());
             return result; 
         }
         if (stopBetweenSamples){ // If so, we need to start collection here
             try {
                 SUBSCRIBER.start();
             } catch (JMSException e) {
                 log.warn("Problem starting subscriber", e);
             }
         }
         StringBuilder buffer = new StringBuilder();
         StringBuilder propBuffer = new StringBuilder();
         
         int loop = getIterationCount();
         int read = 0;
         
         long until = 0L;
         long now = System.currentTimeMillis();
         if (timeout > 0) {
             until = timeout + now; 
         }
         while (!interrupted
                 && (until == 0 || now < until)
                 && read < loop) {
             Message msg;
             try {
                 msg = SUBSCRIBER.getMessage(calculateWait(until, now));
                 if (msg != null){
                     read++;
                     extractContent(buffer, propBuffer, msg, (read == loop));
                 }
             } catch (JMSException e) {
                 log.warn("Error "+e.toString());
             }
             now = System.currentTimeMillis();
         }
         result.sampleEnd();
         result.setResponseMessage(read + " samples messages received");
         if (getReadResponseAsBoolean()) {
             result.setResponseData(buffer.toString().getBytes()); // TODO - charset?
         } else {
             result.setBytes(buffer.toString().getBytes().length); // TODO - charset?
         }
         result.setResponseHeaders(propBuffer.toString());
         if (read == 0) {
             result.setResponseCode("404"); // Not found
             result.setSuccessful(false);
         } else { // TODO set different status if not enough messages found?
             result.setResponseCodeOK();
             result.setSuccessful(true);
         }
         result.setResponseMessage(read + " message(s) received successfully");
         result.setSamplerData(loop + " messages expected");
         result.setSampleCount(read);
         
         if (stopBetweenSamples){
             try {
                 SUBSCRIBER.stop();
             } catch (JMSException e) {
                 log.warn("Problem stopping subscriber", e);
             }
         }
         // run threadFinished only if Destination setup on each sample (stop Listen queue)
         if (!isDestinationStatic()) {
             threadFinished(true);
         }
         return result;
     }
 
     /**
      * Calculate the wait time, will never be more than DEFAULT_WAIT.
      * 
      * @param until target end time or 0 if timeouts not active
      * @param now current time
      * @return wait time
      */
     private long calculateWait(long until, long now) {
         if (until == 0) return DEFAULT_WAIT; // Timeouts not active
         long wait = until - now; // How much left
         return wait > DEFAULT_WAIT ? DEFAULT_WAIT : wait;
     }
 
     private void extractContent(StringBuilder buffer, StringBuilder propBuffer,
             Message msg, boolean isLast) {
         if (msg != null) {
             try {
                 if (msg instanceof TextMessage){
                     buffer.append(((TextMessage) msg).getText());
                 } else if (msg instanceof MapMessage){
                     MapMessage mapm = (MapMessage) msg;
                     @SuppressWarnings("unchecked") // MapNames are Strings
                     Enumeration<String> enumb = mapm.getMapNames();
                     while(enumb.hasMoreElements()){
                         String name = enumb.nextElement();
                         Object obj = mapm.getObject(name);
                         buffer.append(name);
                         buffer.append(",");
                         buffer.append(obj.getClass().getCanonicalName());
                         buffer.append(",");
                         buffer.append(obj);
                         buffer.append("\n");
                     }
                 }
                 Utils.messageProperties(propBuffer, msg);
                 if(!isLast && !StringUtils.isEmpty(separator)) {
                 	propBuffer.append(separator);
                 	buffer.append(separator);
                 }
             } catch (JMSException e) {
                 log.error(e.getMessage());
             }
         }
     }
 
     /**
      * Initialise the thread-local variables.
      * <br/>
      * {@inheritDoc}
      */
     public void threadStarted() {
         // Disabled thread start if listen on sample choice
         if (isDestinationStatic() || START_ON_SAMPLE) {
             timeout = getTimeoutAsLong();
             interrupted = false;
             exceptionDuringInit = null;
             useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
             stopBetweenSamples = isStopBetweenSamples();
             if (useReceive) {
                 try {
                     initReceiveClient();
                     if (!stopBetweenSamples){ // Don't start yet if stop between samples
                         SUBSCRIBER.start();
                     }
                 } catch (NamingException e) {
                     exceptionDuringInit = e;
                 } catch (JMSException e) {
                     exceptionDuringInit = e;
                 }
             } else {
                 try {
                     initListenerClient();
                     if (!stopBetweenSamples){ // Don't start yet if stop between samples
                         SUBSCRIBER.start();
                     }
                 } catch (JMSException e) {
                     exceptionDuringInit = e;
                 } catch (NamingException e) {
                     exceptionDuringInit = e;
                 }
             }
             if (exceptionDuringInit != null){
                 log.error("Could not initialise client",exceptionDuringInit);
             }
         }
     }
     
     public void threadStarted(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = true; // listen on sample 
         }
         threadStarted();
     }
 
     /**
      * Close subscriber.
      * <br/>
      * {@inheritDoc}
      */
     public void threadFinished() {
         if (SUBSCRIBER != null){ // Can be null if init fails
             SUBSCRIBER.close();
         }
     }
     
     public void threadFinished(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = false; // listen on sample
         }
         threadFinished();
     }
 
     /**
      * Handle an interrupt of the test.
      */
     public boolean interrupt() {
         boolean oldvalue = interrupted;
         interrupted = true;   // so we break the loops in SampleWithListener and SampleWithReceive
         return !oldvalue;
     }
 
     // ----------- get/set methods ------------------- //
     /**
      * Set the client choice. There are two options: ReceiveSusbscriber and
      * OnMessageSubscriber.
      */
     public void setClientChoice(String choice) {
         setProperty(CLIENT_CHOICE, choice);
     }
 
     /**
      * Return the client choice.
      *
      * @return the client choice, either RECEIVE_RSC or ON_MESSAGE_RSC
      */
     public String getClientChoice() {
         String choice = getPropertyAsString(CLIENT_CHOICE);
         // Convert the old test plan entry (which is the language dependent string) to the resource name
         if (choice.equals(RECEIVE_STR)){
             choice = JMSSubscriberGui.RECEIVE_RSC;
         } else if (!choice.equals(JMSSubscriberGui.RECEIVE_RSC)){
             choice = JMSSubscriberGui.ON_MESSAGE_RSC;
         }
         return choice;
     }
 
     public String getTimeout(){
         return getPropertyAsString(TIMEOUT, TIMEOUT_DEFAULT);
     }
 
     public long getTimeoutAsLong(){
         return getPropertyAsLong(TIMEOUT, 0L);
     }
 
     public void setTimeout(String timeout){
         setProperty(TIMEOUT, timeout, TIMEOUT_DEFAULT);        
     }
     
     public String getDurableSubscriptionId(){
         return getPropertyAsString(DURABLE_SUBSCRIPTION_ID);
     }
     
     /**
      * @return JMS Client ID
      */
     public String getClientId() {
         return getPropertyAsString(CLIENT_ID, CLIENT_ID_DEFAULT);
     }
     
     /**
      * @return JMS selector
      */
     public String getJmsSelector() {
         return getPropertyAsString(JMS_SELECTOR, JMS_SELECTOR_DEFAULT);
     }
 
     public void setDurableSubscriptionId(String durableSubscriptionId){
         setProperty(DURABLE_SUBSCRIPTION_ID, durableSubscriptionId, DURABLE_SUBSCRIPTION_ID_DEFAULT);        
     }
 
     /**
      * @param clientId JMS CLient id
      */
     public void setClientID(String clientId) {
         setProperty(CLIENT_ID, clientId, CLIENT_ID_DEFAULT);
     }
    
     /**
      * @param jmsSelector JMS Selector
      */
     public void setJmsSelector(String jmsSelector) {
         setProperty(JMS_SELECTOR, jmsSelector, JMS_SELECTOR_DEFAULT);
     }
 
     /**
      * @return Separator for sampler results
      */
     public String getSeparator() {
         return getPropertyAsString(SEPARATOR, SEPARATOR_DEFAULT);
     }
     
     /**
      * Separator for sampler results
      * @param text
      */
     public void setSeparator(String text) {
         setProperty(SEPARATOR, text, SEPARATOR_DEFAULT);
     }
     
     // This was the old value that was checked for
     private static final String RECEIVE_STR = JMeterUtils.getResString(JMSSubscriberGui.RECEIVE_RSC); // $NON-NLS-1$
 
     public boolean isStopBetweenSamples() {
         return getPropertyAsBoolean(STOP_BETWEEN, false);
     }
 
     public void setStopBetweenSamples(boolean selected) {
         setProperty(STOP_BETWEEN, selected, false);                
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded() {
         InitialContextFactory.close();
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     public void testStarted() {
     	testStarted("");
     }
 
     /**
      * {@inheritDoc}
      */
     public void testStarted(String host) {
     	// NOOP
     }
 
 	/**
 	 * 
 	 */
 	private void setupSeparator() {
 		separator = getSeparator();
 		separator = separator.replace("\\t", "\t");
 		separator = separator.replace("\\n", "\n");
 		separator = separator.replace("\\r", "\r");
 	}
 }
\ No newline at end of file
diff --git a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
index 848cfe9cc..cacf9ad64 100644
--- a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
+++ b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
@@ -1,1042 +1,1042 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.ldap.sampler;
 
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.NamingEnumeration;
 import javax.naming.NamingException;
 import javax.naming.directory.Attribute;
 import javax.naming.directory.Attributes;
 import javax.naming.directory.BasicAttribute;
 import javax.naming.directory.BasicAttributes;
 import javax.naming.directory.DirContext;
 import javax.naming.directory.ModificationItem;
 import javax.naming.directory.SearchResult;
 
-import org.apache.commons.lang.StringEscapeUtils;
+import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.protocol.ldap.config.gui.LDAPArgument;
 import org.apache.jmeter.protocol.ldap.config.gui.LDAPArguments;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.XMLBuffer;
 import org.apache.log.Logger;
 
 /*******************************************************************************
  * Ldap Sampler class is main class for the LDAP test. This will control all the
  * test available in the LDAP Test.
  ******************************************************************************/
 
 public class LDAPExtSampler extends AbstractSampler implements TestStateListener {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.protocol.ldap.config.gui.LdapConfigGui",
                     "org.apache.jmeter.protocol.ldap.config.gui.LdapExtConfigGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui"}));
 
     /*
      * The following strings are used in the test plan, and the values must not be changed
      * if test plans are to be upwardly compatible.
      */
     public static final String SERVERNAME = "servername"; // $NON-NLS-1$
 
     public static final String PORT = "port"; // $NON-NLS-1$
 
     public static final String SECURE = "secure"; // $NON-NLS-1$
 
     public static final String ROOTDN = "rootdn"; // $NON-NLS-1$
 
     public static final String TEST = "test"; // $NON-NLS-1$
 
     // These are values for the TEST attribute above
     public static final String ADD = "add"; // $NON-NLS-1$
 
     public static final String MODIFY = "modify"; // $NON-NLS-1$
 
     public static final String BIND = "bind"; // $NON-NLS-1$
 
     public static final String UNBIND = "unbind"; // $NON-NLS-1$
 
     public static final String DELETE = "delete"; // $NON-NLS-1$
 
     public static final String SEARCH = "search"; // $NON-NLS-1$
     // end of TEST values
 
     public static final String SEARCHBASE = "search"; // $NON-NLS-1$
 
     public static final String SEARCHFILTER = "searchfilter"; // $NON-NLS-1$
 
     public static final String ARGUMENTS = "arguments"; // $NON-NLS-1$
 
     public static final String LDAPARGUMENTS = "ldaparguments"; // $NON-NLS-1$
 
     public static final String BASE_ENTRY_DN = "base_entry_dn"; // $NON-NLS-1$
 
     public static final String SCOPE = "scope"; // $NON-NLS-1$
 
     public static final String COUNTLIM = "countlimit"; // $NON-NLS-1$
 
     public static final String TIMELIM = "timelimit"; // $NON-NLS-1$
 
     public static final String ATTRIBS = "attributes"; // $NON-NLS-1$
 
     public static final String RETOBJ = "return_object"; // $NON-NLS-1$
 
     public static final String DEREF = "deref_aliases"; // $NON-NLS-1$
 
     public static final String USERDN = "user_dn"; // $NON-NLS-1$
 
     public static final String USERPW = "user_pw"; // $NON-NLS-1$
 
     public static final String SBIND = "sbind"; // $NON-NLS-1$
 
     public static final String COMPARE = "compare"; // $NON-NLS-1$
 
     public static final String CONNTO = "connection_timeout"; // $NON-NLS-1$
 
     public static final String COMPAREDN = "comparedn"; // $NON-NLS-1$
 
     public static final String COMPAREFILT = "comparefilt"; // $NON-NLS-1$
 
     public static final String PARSEFLAG = "parseflag"; // $NON-NLS-1$
 
     public static final String RENAME = "rename"; // $NON-NLS-1$
 
     public static final String MODDDN = "modddn"; // $NON-NLS-1$
 
     public static final String NEWDN = "newdn"; // $NON-NLS-1$
 
     private static final String SEMI_COLON = ";"; // $NON-NLS-1$
 
 
     private static final ConcurrentHashMap<String, DirContext> ldapContexts =
         new ConcurrentHashMap<String, DirContext>();
 
     private static final int MAX_SORTED_RESULTS =
         JMeterUtils.getPropDefault("ldapsampler.max_sorted_results", 1000); // $NON-NLS-1$
 
     /***************************************************************************
      * !ToDo (Constructor description)
      **************************************************************************/
     public LDAPExtSampler() {
     }
 
     public void setConnTimeOut(String connto) {
         setProperty(new StringProperty(CONNTO, connto));
     }
 
     public String getConnTimeOut() {
         return getPropertyAsString(CONNTO);
     }
 
     public void setSecure(String sec) {
         setProperty(new StringProperty(SECURE, sec));
     }
 
     public boolean isSecure() {
         return getPropertyAsBoolean(SECURE);
     }
 
 
     public boolean isParseFlag() {
         return getPropertyAsBoolean(PARSEFLAG);
     }
 
     public void setParseFlag(String parseFlag) {
         setProperty(new StringProperty(PARSEFLAG, parseFlag));
     }
 
     /***************************************************************************
      * Gets the username attribute of the LDAP object
      *
      * @return The username
      **************************************************************************/
 
     public String getUserDN() {
         return getPropertyAsString(USERDN);
     }
 
     /***************************************************************************
      * Sets the username attribute of the LDAP object
      *
      **************************************************************************/
 
     public void setUserDN(String newUserDN) {
         setProperty(new StringProperty(USERDN, newUserDN));
     }
 
     /***************************************************************************
      * Gets the password attribute of the LDAP object
      *
      * @return The password
      **************************************************************************/
 
     public String getUserPw() {
         return getPropertyAsString(USERPW);
     }
 
     /***************************************************************************
      * Sets the password attribute of the LDAP object
      *
      **************************************************************************/
 
     public void setUserPw(String newUserPw) {
         setProperty(new StringProperty(USERPW, newUserPw));
     }
 
     /***************************************************************************
      * Sets the Servername attribute of the ServerConfig object
      *
      * @param servername
      *            The new servername value
      **************************************************************************/
     public void setServername(String servername) {
         setProperty(new StringProperty(SERVERNAME, servername));
     }
 
     /***************************************************************************
      * Sets the Port attribute of the ServerConfig object
      *
      * @param port
      *            The new Port value
      **************************************************************************/
     public void setPort(String port) {
         setProperty(new StringProperty(PORT, port));
     }
 
     /***************************************************************************
      * Gets the servername attribute of the LDAPSampler object
      *
      * @return The Servername value
      **************************************************************************/
 
     public String getServername() {
         return getPropertyAsString(SERVERNAME);
     }
 
     /***************************************************************************
      * Gets the Port attribute of the LDAPSampler object
      *
      * @return The Port value
      **************************************************************************/
 
     public String getPort() {
         return getPropertyAsString(PORT);
     }
 
     /***************************************************************************
      * Sets the Rootdn attribute of the LDAPSampler object
      *
      * @param newRootdn
      *            The new rootdn value
      **************************************************************************/
     public void setRootdn(String newRootdn) {
         this.setProperty(ROOTDN, newRootdn);
     }
 
     /***************************************************************************
      * Gets the Rootdn attribute of the LDAPSampler object
      *
      * @return The Rootdn value
      **************************************************************************/
     public String getRootdn() {
         return getPropertyAsString(ROOTDN);
     }
 
     /***************************************************************************
      * Gets the search scope attribute of the LDAPSampler object
      *
      * @return The scope value
      **************************************************************************/
     public String getScope() {
         return getPropertyAsString(SCOPE);
     }
 
     public int getScopeAsInt() {
         return getPropertyAsInt(SCOPE);
     }
 
     /***************************************************************************
      * Sets the search scope attribute of the LDAPSampler object
      *
      * @param newScope
      *            The new scope value
      **************************************************************************/
     public void setScope(String newScope) {
         this.setProperty(SCOPE, newScope);
     }
 
     /***************************************************************************
      * Gets the size limit attribute of the LDAPSampler object
      *
      * @return The size limit
      **************************************************************************/
     public String getCountlim() {
         return getPropertyAsString(COUNTLIM);
     }
 
     public long getCountlimAsLong() {
         return getPropertyAsLong(COUNTLIM);
     }
 
     /***************************************************************************
      * Sets the size limit attribute of the LDAPSampler object
      *
      * @param newClim
      *            The new size limit value
      **************************************************************************/
     public void setCountlim(String newClim) {
         this.setProperty(COUNTLIM, newClim);
     }
 
     /***************************************************************************
      * Gets the time limit attribute of the LDAPSampler object
      *
      * @return The time limit
      **************************************************************************/
     public String getTimelim() {
         return getPropertyAsString(TIMELIM);
     }
 
     public int getTimelimAsInt() {
         return getPropertyAsInt(TIMELIM);
     }
 
     /***************************************************************************
      * Sets the time limit attribute of the LDAPSampler object
      *
      * @param newTlim
      *            The new time limit value
      **************************************************************************/
     public void setTimelim(String newTlim) {
         this.setProperty(TIMELIM, newTlim);
     }
 
     /***************************************************************************
      * Gets the return objects attribute of the LDAPSampler object
      *
      * @return if the object(s) are to be returned
      **************************************************************************/
     public boolean isRetobj() {
         return getPropertyAsBoolean(RETOBJ);
     }
 
     /***************************************************************************
      * Sets the return objects attribute of the LDAPSampler object
      *
      **************************************************************************/
     public void setRetobj(String newRobj) {
         this.setProperty(RETOBJ, newRobj);
     }
 
     /***************************************************************************
      * Gets the deref attribute of the LDAPSampler object
      *
      * @return if dereferencing is required
      **************************************************************************/
     public boolean isDeref() {
         return getPropertyAsBoolean(DEREF);
     }
 
     /***************************************************************************
      * Sets the deref attribute of the LDAPSampler object
      *
      * @param newDref
      *            The new deref value
      **************************************************************************/
     public void setDeref(String newDref) {
         this.setProperty(DEREF, newDref);
     }
 
     /***************************************************************************
      * Sets the Test attribute of the LdapConfig object
      *
      * @param newTest
      *            The new test value(Add,Modify,Delete and search)
      **************************************************************************/
     public void setTest(String newTest) {
         this.setProperty(TEST, newTest);
     }
 
     /***************************************************************************
      * Gets the test attribute of the LDAPSampler object
      *
      * @return The test value (Add,Modify,Delete and search)
      **************************************************************************/
     public String getTest() {
         return getPropertyAsString(TEST);
     }
 
     /***************************************************************************
      * Sets the attributes of the LdapConfig object
      *
      * @param newAttrs
      *            The new attributes value
      **************************************************************************/
     public void setAttrs(String newAttrs) {
         this.setProperty(ATTRIBS, newAttrs);
     }
 
     /***************************************************************************
      * Gets the attributes of the LDAPSampler object
      *
      * @return The attributes
      **************************************************************************/
     public String getAttrs() {
         return getPropertyAsString(ATTRIBS);
     }
 
     /***************************************************************************
      * Sets the Base Entry DN attribute of the LDAPSampler object
      *
      * @param newbaseentry
      *            The new Base entry DN value
      **************************************************************************/
     public void setBaseEntryDN(String newbaseentry) {
         setProperty(new StringProperty(BASE_ENTRY_DN, newbaseentry));
     }
 
     /***************************************************************************
      * Gets the BaseEntryDN attribute of the LDAPSampler object
      *
      * @return The Base entry DN value
      **************************************************************************/
     public String getBaseEntryDN() {
         return getPropertyAsString(BASE_ENTRY_DN);
     }
 
     /***************************************************************************
      * Sets the Arguments attribute of the LdapConfig object This will collect
      * values from the table for user defined test case
      *
      * @param value
      *            The arguments
      **************************************************************************/
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     /***************************************************************************
      * Gets the Arguments attribute of the LdapConfig object
      *
      * @return The arguments user defined test case
      **************************************************************************/
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /***************************************************************************
      * Sets the Arguments attribute of the LdapConfig object This will collect
      * values from the table for user defined test case
      *
      * @param value
      *            The arguments
      **************************************************************************/
     public void setLDAPArguments(LDAPArguments value) {
         setProperty(new TestElementProperty(LDAPARGUMENTS, value));
     }
 
     /***************************************************************************
      * Gets the LDAPArguments attribute of the LdapConfig object
      *
      * @return The LDAParguments user defined modify test case
      **************************************************************************/
     public LDAPArguments getLDAPArguments() {
         return (LDAPArguments) getProperty(LDAPARGUMENTS).getObjectValue();
     }
 
     /***************************************************************************
      * Collect all the values from the table (Arguments), using this create the
      * Attributes, this will create the Attributes for the User
      * defined TestCase for Add Test
      *
      * @return The Attributes
      **************************************************************************/
     private Attributes getUserAttributes() {
         Attributes attrs = new BasicAttributes(true);
         Attribute attr;
         PropertyIterator iter = getArguments().iterator();
 
         while (iter.hasNext()) {
             Argument item = (Argument) iter.next().getObjectValue();
             attr = attrs.get(item.getName());
             if (attr == null) {
                 attr = getBasicAttribute(item.getName(), item.getValue());
             } else {
                 attr.add(item.getValue());
             }
             attrs.put(attr);
         }
         return attrs;
     }
 
     /***************************************************************************
      * Collect all the value from the table (Arguments), using this create the
      * basicAttributes This will create the Basic Attributes for the User
      * defined TestCase for Modify test
      *
      * @return The BasicAttributes
      **************************************************************************/
     private ModificationItem[] getUserModAttributes() {
         ModificationItem[] mods = new ModificationItem[getLDAPArguments().getArguments().size()];
         BasicAttribute attr;
         PropertyIterator iter = getLDAPArguments().iterator();
         int count = 0;
         while (iter.hasNext()) {
             LDAPArgument item = (LDAPArgument) iter.next().getObjectValue();
             if ((item.getValue()).length()==0) {
                 attr = new BasicAttribute(item.getName());
             } else {
                 attr = getBasicAttribute(item.getName(), item.getValue());
             }
 
             final String opcode = item.getOpcode();
             if ("add".equals(opcode)) { // $NON-NLS-1$
                 mods[count++] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
             } else if ("delete".equals(opcode) // $NON-NLS-1$
                    ||  "remove".equals(opcode)) { // $NON-NLS-1$
                     mods[count++] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
             } else if("replace".equals(opcode)) { // $NON-NLS-1$
                     mods[count++] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
             } else {
                     log.warn("Invalid opCode: "+opcode);
             }
         }
         return mods;
     }
 
     /***************************************************************************
      * Collect all the value from the table (Arguments), using this create the
      * Attributes This will create the Basic Attributes for the User defined
      * TestCase for search test
      *
      * @return The BasicAttributes
      **************************************************************************/
     private String[] getRequestAttributes(String reqAttr) {
         int index;
         String[] mods;
         int count = 0;
         if (reqAttr.length() == 0) {
             return null;
         }
         if (!reqAttr.endsWith(SEMI_COLON)) {
             reqAttr = reqAttr + SEMI_COLON;
         }
         String attr = reqAttr;
 
         while (attr.length() > 0) {
             index = attr.indexOf(SEMI_COLON);
             count += 1;
             attr = attr.substring(index + 1);
         }
         if (count > 0) {
             mods = new String[count];
             attr = reqAttr;
             count = 0;
             while (attr.length() > 0) {
                 index = attr.indexOf(SEMI_COLON);
                 mods[count] = attr.substring(0, index);
                 count += 1;
                 attr = attr.substring(index + 1);
             }
         } else {
             mods = null;
         }
         return mods;
     }
 
     /***************************************************************************
      * This will create the Basic Attribute for the give name value pair
      *
      * @return The BasicAttribute
      **************************************************************************/
     private BasicAttribute getBasicAttribute(String name, String value) {
         BasicAttribute attr = new BasicAttribute(name, value);
         return attr;
     }
 
     /**
      * Returns a formatted string label describing this sampler Example output:
      *
      * @return a formatted string label describing this sampler
      */
     public String getLabel() {
         return ("ldap://" + this.getServername()  //$NON-NLS-1$
                 + ":" + getPort()                 //$NON-NLS-1$
                 + "/" + this.getRootdn());        //$NON-NLS-1$
     }
 
     /***************************************************************************
      * This will do the add test for the User defined TestCase
      *
      **************************************************************************/
     private void addTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             DirContext ctx = LdapExtClient.createTest(dirContext, getUserAttributes(), getBaseEntryDN());
             ctx.close(); // the createTest() method creates an extra context which needs to be closed
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the delete test for the User defined TestCase
      *
      **************************************************************************/
     private void deleteTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.deleteTest(dirContext, getPropertyAsString(DELETE));
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the modify test for the User defined TestCase
      *
      **************************************************************************/
     private void modifyTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.modifyTest(dirContext, getUserModAttributes(), getBaseEntryDN());
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the bind for the User defined Thread, this bind is used for
      * the whole context
      *
      **************************************************************************/
     private void bindOp(DirContext dirContext, SampleResult res) throws NamingException {
         DirContext ctx = ldapContexts.remove(getThreadName());
         if (ctx != null) {
             log.warn("Closing previous context for thread: " + getThreadName());
             ctx.close();
         }
         try {
             res.sampleStart();
             ctx = LdapExtClient.connect(getServername(), getPort(), getRootdn(), getUserDN(), getUserPw(),getConnTimeOut(),isSecure());
         } finally {
             res.sampleEnd();
         }
         ldapContexts.put(getThreadName(), ctx);
     }
 
     /***************************************************************************
      * This will do the bind and unbind for the User defined TestCase
      *
      **************************************************************************/
     private void singleBindOp(SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             DirContext ctx = LdapExtClient.connect(getServername(), getPort(), getRootdn(), getUserDN(), getUserPw(),getConnTimeOut(),isSecure());
             LdapExtClient.disconnect(ctx);
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do a moddn Opp for the User new DN defined
      *
      **************************************************************************/
     private void renameTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.moddnOp(dirContext, getPropertyAsString(MODDDN), getPropertyAsString(NEWDN));
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the unbind for the User defined TestCase as well as inbuilt
      * test case
      *
      **************************************************************************/
     private void unbindOp(DirContext dirContext, SampleResult res) {
         try {
             res.sampleStart();
             LdapExtClient.disconnect(dirContext);
         } finally {
             res.sampleEnd();
         }
         ldapContexts.remove(getThreadName());
         log.info("context and LdapExtClients removed");
     }
 
     /***************************************************************************
      * !ToDo (Method description)
      *
      * @param e
      *            !ToDo (Parameter description)
      * @return !ToDo (Return description)
      **************************************************************************/
     public SampleResult sample(Entry e) {
         XMLBuffer xmlBuffer = new XMLBuffer();
         xmlBuffer.openTag("ldapanswer"); // $NON-NLS-1$
         SampleResult res = new SampleResult();
         res.setResponseData("successfull", null);
         res.setResponseMessage("Success"); // $NON-NLS-1$
         res.setResponseCode("0"); // $NON-NLS-1$
         res.setContentType("text/xml");// $NON-NLS-1$
         boolean isSuccessful = true;
         res.setSampleLabel(getName());
         DirContext dirContext = ldapContexts.get(getThreadName());
 
         try {
             xmlBuffer.openTag("operation"); // $NON-NLS-1$
             final String testType = getTest();
             xmlBuffer.tag("opertype", testType); // $NON-NLS-1$
             log.debug("performing test: " + testType);
             if (testType.equals(UNBIND)) {
                 res.setSamplerData("Unbind");
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 unbindOp(dirContext, res);
             } else if (testType.equals(BIND)) {
                 res.setSamplerData("Bind as "+getUserDN());
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 xmlBuffer.tag("connectionTO",getConnTimeOut()); // $NON-NLS-1$
                 bindOp(dirContext, res);
             } else if (testType.equals(SBIND)) {
                 res.setSamplerData("SingleBind as "+getUserDN());
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 xmlBuffer.tag("connectionTO",getConnTimeOut()); // $NON-NLS-1$
                 singleBindOp(res);
             } else if (testType.equals(COMPARE)) {
                 res.setSamplerData("Compare "+getPropertyAsString(COMPAREFILT) + " "
                                 + getPropertyAsString(COMPAREDN));
                 xmlBuffer.tag("comparedn",getPropertyAsString(COMPAREDN)); // $NON-NLS-1$
                 xmlBuffer.tag("comparefilter",getPropertyAsString(COMPAREFILT)); // $NON-NLS-1$
                 NamingEnumeration<SearchResult> cmp=null;
                 try {
                     res.sampleStart();
                     cmp = LdapExtClient.compare(dirContext, getPropertyAsString(COMPAREFILT),
                             getPropertyAsString(COMPAREDN));
                     if (!cmp.hasMore()) {
                         res.setResponseCode("5"); // $NON-NLS-1$
                         res.setResponseMessage("compareFalse");
                         isSuccessful = false;
                     }
                 } finally {
                     res.sampleEnd();
                     if (cmp != null) {
                         cmp.close();
                     }
                 }
             } else if (testType.equals(ADD)) {
                 res.setSamplerData("Add object " + getBaseEntryDN());
                 xmlBuffer.tag("attributes",getArguments().toString()); // $NON-NLS-1$
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 addTest(dirContext, res);
             } else if (testType.equals(DELETE)) {
                 res.setSamplerData("Delete object " + getBaseEntryDN());
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 deleteTest(dirContext, res);
             } else if (testType.equals(MODIFY)) {
                 res.setSamplerData("Modify object " + getBaseEntryDN());
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 xmlBuffer.tag("attributes",getLDAPArguments().toString()); // $NON-NLS-1$
                 modifyTest(dirContext, res);
             } else if (testType.equals(RENAME)) {
                 res.setSamplerData("ModDN object " + getPropertyAsString(MODDDN) + " to " + getPropertyAsString(NEWDN));
                 xmlBuffer.tag("dn",getPropertyAsString(MODDDN)); // $NON-NLS-1$
                 xmlBuffer.tag("newdn",getPropertyAsString(NEWDN)); // $NON-NLS-1$
                 renameTest(dirContext, res);
             } else if (testType.equals(SEARCH)) {
                 final String            scopeStr = getScope();
                 final int               scope = getScopeAsInt();
                 final String searchFilter = getPropertyAsString(SEARCHFILTER);
                 final String searchBase = getPropertyAsString(SEARCHBASE);
                 final String timeLimit = getTimelim();
                 final String countLimit = getCountlim();
 
                 res.setSamplerData("Search with filter " + searchFilter);
                 xmlBuffer.tag("searchfilter",searchFilter); // $NON-NLS-1$
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("searchbase",searchBase);// $NON-NLS-1$
                 xmlBuffer.tag("scope" , scopeStr); // $NON-NLS-1$
                 xmlBuffer.tag("countlimit",countLimit); // $NON-NLS-1$
                 xmlBuffer.tag("timelimit",timeLimit); // $NON-NLS-1$
 
                 NamingEnumeration<SearchResult> srch=null;
                 try {
                     res.sampleStart();
                     srch = LdapExtClient.searchTest(
                             dirContext, searchBase, searchFilter,
                             scope, getCountlimAsLong(),
                             getTimelimAsInt(),
                             getRequestAttributes(getAttrs()),
                             isRetobj(),
                             isDeref());
                     if (isParseFlag()) {
                         try {
                             xmlBuffer.openTag("searchresults"); // $NON-NLS-1$
                             writeSearchResults(xmlBuffer, srch);
                         } finally {
                             xmlBuffer.closeTag("searchresults"); // $NON-NLS-1$
                         }
                     } else {
                         xmlBuffer.tag("searchresults", // $NON-NLS-1$
                                 "hasElements="+srch.hasMoreElements()); // $NON-NLS-1$
                     }
                 } finally {
                     if (srch != null){
                         srch.close();
                     }
                     res.sampleEnd();
                 }
 
             }
 
         } catch (NamingException ex) {
             //log.warn("DEBUG",ex);
 // e.g. javax.naming.SizeLimitExceededException: [LDAP: error code 4 - Sizelimit Exceeded]; remaining name ''
 //                                                123456789012345678901
             // TODO: tidy this up
             String returnData = ex.toString();
             final int indexOfLDAPErrCode = returnData.indexOf("LDAP: error code");
             if (indexOfLDAPErrCode >= 0) {
                 res.setResponseMessage(returnData.substring(indexOfLDAPErrCode + 21, returnData
                         .indexOf("]"))); // $NON-NLS-1$
                 res.setResponseCode(returnData.substring(indexOfLDAPErrCode + 17, indexOfLDAPErrCode + 19));
             } else {
                 res.setResponseMessage(returnData);
                 res.setResponseCode("800"); // $NON-NLS-1$
             }
             isSuccessful = false;
         } finally {
             xmlBuffer.closeTag("operation"); // $NON-NLS-1$
             xmlBuffer.tag("responsecode",res.getResponseCode()); // $NON-NLS-1$
             xmlBuffer.tag("responsemessage",res.getResponseMessage()); // $NON-NLS-1$
             res.setResponseData(xmlBuffer.toString(), null);
             res.setDataType(SampleResult.TEXT);
             res.setSuccessful(isSuccessful);
         }
         return res;
     }
 
     /*
      *   Write out search results in a stable order (including order of all subelements which might
      * be reordered like attributes and their values) so that simple textual comparison can be done,
      * unless the number of results exceeds {@link #MAX_SORTED_RESULTS} in which case just stream
      * the results out without sorting.
      */
     private void writeSearchResults(final XMLBuffer xmlb, final NamingEnumeration<SearchResult> srch)
             throws NamingException
     {
 
         final ArrayList<SearchResult>     sortedResults = new ArrayList<SearchResult>(MAX_SORTED_RESULTS);
         final String        searchBase = getPropertyAsString(SEARCHBASE);
         final String        rootDn = getRootdn();
 
         // read all sortedResults into memory so we can guarantee ordering
         try {
             while (srch.hasMore() && (sortedResults.size() < MAX_SORTED_RESULTS)) {
                 final SearchResult    sr = srch.next();
 
                     // must be done prior to sorting
                 normaliseSearchDN(sr, searchBase, rootDn);
                 sortedResults.add(sr);
             }
         } finally { // show what we did manage to retrieve
 
             sortResults(sortedResults);
 
             for (Iterator<SearchResult> it = sortedResults.iterator(); it.hasNext();)
             {
                 final SearchResult  sr = it.next();
                 writeSearchResult(sr, xmlb);
             }
         }
 
         while (srch.hasMore()) { // If there's anything left ...
             final SearchResult    sr = srch.next();
 
             normaliseSearchDN(sr, searchBase, rootDn);
             writeSearchResult(sr, xmlb);
         }
     }
 
     private void writeSearchResult(final SearchResult sr, final XMLBuffer xmlb)
             throws NamingException
     {
         final Attributes    attrs = sr.getAttributes();
         final int           size = attrs.size();
         final ArrayList<Attribute>     sortedAttrs = new ArrayList<Attribute>(size);
 
         xmlb.openTag("searchresult"); // $NON-NLS-1$
         xmlb.tag("dn", sr.getName()); // $NON-NLS-1$
          xmlb.tag("returnedattr",Integer.toString(size)); // $NON-NLS-1$
          xmlb.openTag("attributes"); // $NON-NLS-1$
 
          try {
             for (NamingEnumeration<? extends Attribute> en = attrs.getAll(); en.hasMore(); )
             {
                 final Attribute     attr = en.next();
 
                 sortedAttrs.add(attr);
             }
             sortAttributes(sortedAttrs);
             for (Iterator<Attribute> ait = sortedAttrs.iterator(); ait.hasNext();)
             {
                 final Attribute     attr = ait.next();
 
                 StringBuilder sb = new StringBuilder();
                 if (attr.size() == 1) {
                     sb.append(getWriteValue(attr.get()));
                 } else {
                     final ArrayList<String>     sortedVals = new ArrayList<String>(attr.size());
                     boolean             first = true;
 
                     for (NamingEnumeration<?> ven = attr.getAll(); ven.hasMore(); )
                     {
                         final Object    value = getWriteValue(ven.next());
                         sortedVals.add(value.toString());
                     }
 
                     Collections.sort(sortedVals);
 
                     for (Iterator<String> vit = sortedVals.iterator(); vit.hasNext();)
                     {
                         final String    value = vit.next();
                         if (first) {
                             first = false;
                         } else {
                             sb.append(", "); // $NON-NLS-1$
                         }
                         sb.append(value);
                     }
                 }
                 xmlb.tag(attr.getID(),sb);
             }
         } finally {
              xmlb.closeTag("attributes"); // $NON-NLS-1$
             xmlb.closeTag("searchresult"); // $NON-NLS-1$
         }
     }
 
     private void sortAttributes(final ArrayList<Attribute> sortedAttrs) {
         Collections.sort(sortedAttrs, new Comparator<Attribute>()
         {
             public int compare(Attribute o1, Attribute o2)
             {
                 String      nm1 = o1.getID();
                 String      nm2 = o2.getID();
 
                 return nm1.compareTo(nm2);
             }
         });
     }
 
     private void sortResults(final ArrayList<SearchResult> sortedResults) {
         Collections.sort(sortedResults, new Comparator<SearchResult>()
         {
             private int compareToReverse(final String s1, final String s2)
             {
                 int     len1 = s1.length();
                 int     len2 = s2.length();
                 int     s1i = len1 - 1;
                 int     s2i = len2 - 1;
 
                 for ( ; (s1i >= 0) && (s2i >= 0); s1i--, s2i--)
                 {
                     char    c1 = s1.charAt(s1i);
                     char    c2 = s2.charAt(s2i);
 
                     if (c1 != c2) {
                         return c1 - c2;
                     }
                 }
                 return len1 - len2;
             }
 
             public int compare(SearchResult o1, SearchResult o2)
             {
                 String      nm1 = o1.getName();
                 String      nm2 = o2.getName();
 
                 if (nm1 == null) {
                     nm1 = "";
                 }
                 if (nm2 == null) {
                     nm2 = "";
                 }
                 return compareToReverse(nm1, nm2);
             }
         });
     }
 
     private String normaliseSearchDN(final SearchResult sr, final String searchBase, final String rootDn)
     {
         String      srName = sr.getName();
 
         if (!srName.endsWith(searchBase))
         {
             if (srName.length() > 0) {
                 srName = srName + ',';
             }
             srName = srName + searchBase;
         }
         if ((rootDn.length() > 0) && !srName.endsWith(rootDn))
         {
             if (srName.length() > 0) {
                 srName = srName + ',';
             }
             srName = srName + rootDn;
         }
         sr.setName(srName);
         return srName;
     }
 
     private String getWriteValue(final Object value)
     {
         if (value instanceof String) {
diff --git a/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java b/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
index 7dcdabd5c..fb69d2f99 100644
--- a/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
+++ b/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
@@ -1,281 +1,281 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.system;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A sampler for executing a System function. 
  */
 public class SystemSampler extends AbstractSampler {
     private static final long serialVersionUID = 1;
     
     public static final String COMMAND = "SystemSampler.command";
     
     public static final String DIRECTORY = "SystemSampler.directory";
 
     public static final String ARGUMENTS = "SystemSampler.arguments";
     
     public static final String ENVIRONMENT = "SystemSampler.environment";
 
     public static final String CHECK_RETURN_CODE = "SystemSampler.checkReturnCode";
     
     public static final String EXPECTED_RETURN_CODE = "SystemSampler.expectedReturnCode";
 
     /**
      * Logging
      */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.SimpleConfigGui"}));
 
     public static final int DEFAULT_RETURN_CODE = 0;
 
 
     /**
      * Create a SystemSampler.
      */
     public SystemSampler() {
         super();
     }
     
     /**
      * Performs a test sample.
      * 
      * @param entry
      *            the Entry for this sample
      * @return test SampleResult
      */
     public SampleResult sample(Entry entry) {
         SampleResult results = new SampleResult();
         results.setDataType(SampleResult.TEXT);
         
         try {
             String command = getCommand();
             Arguments args = getArguments();
             Arguments environment = getEnvironmentVariables();
             boolean checkReturnCode = getCheckReturnCode();
             int expectedReturnCode = getExpectedReturnCode();
             
             List<String> cmds = new ArrayList<String>(args.getArgumentCount()+1);
             StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
             cmds.add(command);
             for (int i=0;i<args.getArgumentCount();i++) {
                 Argument arg = args.getArgument(i);
                 cmds.add(arg.getPropertyAsString(Argument.VALUE));
                 cmdLine.append(" ");
                 cmdLine.append(cmds.get(i+1));
             }
 
             Map<String,String> env = new HashMap<String, String>();
             for (int i=0;i<environment.getArgumentCount();i++) {
                 Argument arg = environment.getArgument(i);
                 env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
             }
             
             File directory = null;
             if(StringUtils.isEmpty(getDirectory())) {
                 directory = new File(FileServer.getDefaultBase());
                 if(log.isDebugEnabled()) {
                     log.debug("Using default directory:"+directory.getAbsolutePath());
                 }
             } else {
                 directory = new File(getDirectory());
                 if(log.isDebugEnabled()) {
                     log.debug("Using configured directory:"+directory.getAbsolutePath());
                 }
             }
             results.setSamplerData("Working Directory:"+directory.getAbsolutePath()+
                     ", Environment:"+env+
                     ", Executing:" + cmdLine.toString());
             
             NativeCommand nativeCommand = new NativeCommand(directory, env);
             
             String responseData = null;
             try {
                 if(log.isDebugEnabled()) {
                     log.debug("Will run :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                             " with environment:"+env);
                 }
                 results.sampleStart();
                 int returnCode = nativeCommand.run(cmds);
                 if(log.isDebugEnabled()) {
                     log.debug("Ran :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                             " with execution environment:"+nativeCommand.getExecutionEnvironment());
                 }
                 results.sampleEnd();
 
                 if (checkReturnCode && (returnCode != expectedReturnCode)) {
                     results.setSuccessful(false);
                     responseData = "System did not return expected return code.  Expected ["+expectedReturnCode+"]. Returned ["+returnCode+"].";
                     results.setSampleLabel("FAILED: " + getName());
                 } else {
                     results.setSuccessful(true);
                     responseData = "System Call Complete.";
                     results.setSampleLabel(getName());
                 }
             } catch (IOException ioe) {
                 results.setSuccessful(false);
                 responseData = "Exception occured whilst executing System Call: "+ioe;
                 results.setSampleLabel("ERROR: " + getName());
             } catch (InterruptedException ie) {
                 results.setSuccessful(false);
                 responseData = "System Sampler Interupted whilst executing System Call: "+ie;
                 results.setSampleLabel("ERROR: " + getName());
             }
     
             results.setResponseData((responseData+"\nProcess Output:\n"+nativeCommand.getOutResult()).getBytes());
             
         } catch (Exception e) {
             results.setSuccessful(false);
             results.setResponseData(("Unknown Exception caught: "+e).getBytes());
             results.setSampleLabel("ERROR: " + getName());
         }
         return results;
     }
     
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 
     public String getDirectory() {
         return getPropertyAsString(DIRECTORY, FileServer.getDefaultBase());
     }
     
     /**
      * 
      * @param directory
      */
     public void setDirectory(String directory) {
         setProperty(DIRECTORY, directory, FileServer.getDefaultBase());
     }
 
     /**
      * Sets the Command attribute of the JavaConfig object
      * 
      * @param command
      *            the new Command value
      */
     public void setCommand(String command) {
         setProperty(COMMAND, command);
     }
 
     /**
      * Gets the Command attribute of the JavaConfig object
      * 
      * @return the Command value
      */
     public String getCommand() {
         return getPropertyAsString(COMMAND);
     }
     
     /**
      * Set the arguments (parameters) for the JavaSamplerClient to be executed
      * with.
      * 
      * @param args
      *            the new arguments. These replace any existing arguments.
      */
     public void setArguments(Arguments args) {
         setProperty(new TestElementProperty(ARGUMENTS, args));
     }
 
     /**
      * Get the arguments (parameters) for the JavaSamplerClient to be executed
      * with.
      * 
      * @return the arguments
      */
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
     
     /**
      * @param checkit boolean indicates if we check or not return code
      */
     public void setCheckReturnCode(boolean checkit) {
         setProperty(CHECK_RETURN_CODE, checkit);
     }
     
     /**
      * @return boolean indicating if we check or not return code
      */
     public boolean getCheckReturnCode() {
         return getPropertyAsBoolean(CHECK_RETURN_CODE);
     }
     
     /**
      * @param code expected return code
      */
     public void setExpectedReturnCode(int code) {
         setProperty(EXPECTED_RETURN_CODE, Integer.toString(code));
     }
     
     /**
      * @return expected return code
      */
     public int getExpectedReturnCode() {
         return getPropertyAsInt(EXPECTED_RETURN_CODE);
     }
 
     /**
      * @param arguments Env vars
      */
     public void setEnvironmentVariables(Arguments arguments) {
         setProperty(new TestElementProperty(ENVIRONMENT, arguments));
     }
     
     /**
      * Get the env variables
      * 
      * @return the arguments
      */
     public Arguments getEnvironmentVariables() {
         return (Arguments) getProperty(ENVIRONMENT).getObjectValue();
     }
 }
\ No newline at end of file
diff --git a/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java b/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
index 2c57eaa00..0f3eeace7 100644
--- a/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
+++ b/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
@@ -1,217 +1,217 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.system.gui;
 
 import java.awt.BorderLayout;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.system.SystemSampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * GUI for {@link SystemSampler}
  */
 public class SystemSamplerGui extends AbstractSamplerGui implements ItemListener {
 
     /**
      * 
      */
     private static final long serialVersionUID = -2413845772703695934L;
     
     private JCheckBox checkReturnCode;
     private JLabeledTextField desiredReturnCode;
     private JLabeledTextField directory;
     private JLabeledTextField command;
     private ArgumentsPanel argsPanel;
     private ArgumentsPanel envPanel;
     
     /**
      * Constructor for JavaTestSamplerGui
      */
     public SystemSamplerGui() {
         super();
         init();
     }
 
     public String getLabelResource() {
         return "system_sampler_title";
     }
 
     @Override
     public String getStaticLabel() {
         return JMeterUtils.getResString(getLabelResource());
     }
 
     /**
      * Initialize the GUI components and layout.
      */
     private void init() {
         setLayout(new BorderLayout());
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
        
         JPanel panelb = new VerticalPanel();
         panelb.add(makeReturnCodePanel());
         panelb.add(Box.createVerticalStrut(5));
         panelb.add(makeCommandPanel(), BorderLayout.CENTER);
         
         add(panelb, BorderLayout.CENTER);
     }
 
     /* Implements JMeterGuiComponent.createTestElement() */
     public TestElement createTestElement() {
         SystemSampler sampler = new SystemSampler();
         modifyTestElement(sampler);
         return sampler;
     }
 
     public void modifyTestElement(TestElement sampler) {
         super.configureTestElement(sampler);
         SystemSampler systemSampler = (SystemSampler)sampler;
         systemSampler.setCheckReturnCode(checkReturnCode.isSelected());
         if(checkReturnCode.isSelected()) {
             if(!StringUtils.isEmpty(desiredReturnCode.getText())) {
                 systemSampler.setExpectedReturnCode(Integer.parseInt(desiredReturnCode.getText()));
             } else {
                 systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
             }
         } else {
             systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
         }
         systemSampler.setCommand(command.getText());
         systemSampler.setArguments((Arguments)argsPanel.createTestElement());
         systemSampler.setEnvironmentVariables((Arguments)envPanel.createTestElement());
         systemSampler.setDirectory(directory.getText());
     }
 
     /* Overrides AbstractJMeterGuiComponent.configure(TestElement) */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         SystemSampler systemSampler = (SystemSampler) el;
         checkReturnCode.setSelected(systemSampler.getCheckReturnCode());
         desiredReturnCode.setText(Integer.toString(systemSampler.getExpectedReturnCode()));
         desiredReturnCode.setEnabled(checkReturnCode.isSelected());
         command.setText(systemSampler.getCommand());
         argsPanel.configure(systemSampler.getArguments());
         envPanel.configure(systemSampler.getEnvironmentVariables());
         directory.setText(systemSampler.getDirectory());
     }
 
     /**
      * @return JPanel return code config
      */
     private JPanel makeReturnCodePanel() {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("return_code_config_box_title")));
         checkReturnCode = new JCheckBox(JMeterUtils.getResString("check_return_code_title"));
         checkReturnCode.addItemListener(this);
         desiredReturnCode = new JLabeledTextField(JMeterUtils.getResString("expected_return_code_title"));
         desiredReturnCode.setSize(desiredReturnCode.getSize().height, 30);
         panel.add(checkReturnCode);
         panel.add(Box.createHorizontalStrut(5));
         panel.add(desiredReturnCode);
         checkReturnCode.setSelected(true);
         return panel;
     }
     
     /**
      * @return JPanel Command + directory
      */
     private JPanel makeCommandPanel() {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("command_config_box_title")));
         
         JPanel cmdPanel = new JPanel();
         cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));
         
         directory = new JLabeledTextField(JMeterUtils.getResString("directory_field_title"));
         cmdPanel.add(directory);
         cmdPanel.add(Box.createHorizontalStrut(5));
         command = new JLabeledTextField(JMeterUtils.getResString("command_field_title"));
         cmdPanel.add(command);
         panel.add(cmdPanel, BorderLayout.NORTH);
         panel.add(makeArgumentsPanel(), BorderLayout.CENTER);
         panel.add(makeEnvironmentPanel(), BorderLayout.SOUTH);
         return panel;
     }
     
     /**
      * @return JPanel Arguments Panel
      */
     private JPanel makeArgumentsPanel() {
         argsPanel = new ArgumentsPanel(JMeterUtils.getResString("arguments_panel_title"), null, true, false , 
                 new ObjectTableModel(new String[] { ArgumentsPanel.COLUMN_RESOURCE_NAMES_1 },
                         Argument.class,
                         new Functor[] {
                         new Functor("getValue") },  // $NON-NLS-1$
                         new Functor[] {
                         new Functor("setValue") }, // $NON-NLS-1$
                         new Class[] {String.class }));
         return argsPanel;
     }
     
     /**
      * @return JPanel Environment Panel
      */
     private JPanel makeEnvironmentPanel() {
         envPanel = new ArgumentsPanel(JMeterUtils.getResString("environment_panel_title"));
         return envPanel;
     }
 
     /**
      * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#clearGui()
      */
     @Override
     public void clearGui() {
         super.clearGui();
         directory.setText("");
         command.setText("");
         argsPanel.clearGui();
         envPanel.clearGui();
         desiredReturnCode.setText("");
         checkReturnCode.setSelected(false);
         desiredReturnCode.setEnabled(false);
     }
 
     public void itemStateChanged(ItemEvent e) {
         if(e.getSource()==checkReturnCode) {
             desiredReturnCode.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
         }
     }
 }
\ No newline at end of file
diff --git a/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPClientImpl.java b/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPClientImpl.java
index 8fdfd7010..006f36ab0 100644
--- a/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPClientImpl.java
+++ b/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPClientImpl.java
@@ -1,117 +1,117 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 /*
  * Basic TCP Sampler Client class
  *
  * Can be used to test the TCP Sampler against an HTTP server
  *
  * The protocol handler class name is defined by the property tcp.handler
  *
  */
 package org.apache.jmeter.protocol.tcp.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.charset.Charset;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Sample TCPClient implementation.
  * Reads data until the defined EOL byte is reached.
  * If there is no EOL byte defined, then reads until
  * the end of the stream is reached.
  * The EOL byte is defined by the property "tcp.eolByte".
  */
 public class TCPClientImpl extends AbstractTCPClient {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private int eolInt = JMeterUtils.getPropDefault("tcp.eolByte", 1000); // $NON-NLS-1$
     private String charset = JMeterUtils.getPropDefault("tcp.charset", Charset.defaultCharset().name()); // $NON-NLS-1$
     // default is not in range of a byte
 
     public TCPClientImpl() {
         super();
         setEolByte(eolInt);
         if (useEolByte) {
             log.info("Using eolByte=" + eolByte);
         }
         setCharset(charset);
         String configuredCharset = JMeterUtils.getProperty("tcp.charset");
         if(StringUtils.isEmpty(configuredCharset)) {
             log.info("Using platform default charset:"+charset);
         } else {
             log.info("Using charset:"+configuredCharset);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void write(OutputStream os, String s)  throws IOException{
         os.write(s.getBytes(charset)); 
         os.flush();
         if(log.isDebugEnabled()) {
             log.debug("Wrote: " + s);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void write(OutputStream os, InputStream is) throws IOException{
         byte buff[]=new byte[512];
         while(is.read(buff) > 0){
             os.write(buff);
             os.flush();
         }
     }
 
     /**
      * Reads data until the defined EOL byte is reached.
      * If there is no EOL byte defined, then reads until
      * the end of the stream is reached.
      */
     public String read(InputStream is) throws ReadException{
     	ByteArrayOutputStream w = new ByteArrayOutputStream();
         try {
 			byte[] buffer = new byte[4096];
 			int x = 0;
 			while ((x = is.read(buffer)) > -1) {
 			    w.write(buffer, 0, x);
 			    if (useEolByte && (buffer[x - 1] == eolByte)) {
 			        break;
 			    }
 			}
 
 			// do we need to close byte array (or flush it?)
 			if(log.isDebugEnabled()) {
 			    log.debug("Read: " + w.size() + "\n" + w.toString());
 			}
 			return w.toString(charset);
 		} catch (IOException e) {
 			throw new ReadException("", e, w.toString());
 		}
     }
 }
diff --git a/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPSampler.java b/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPSampler.java
index 009fac242..4e19aa51f 100644
--- a/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPSampler.java
+++ b/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPSampler.java
@@ -1,511 +1,511 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.tcp.sampler;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.net.SocketException;
 import java.net.UnknownHostException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
-import org.apache.commons.lang.StringUtils;
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * A sampler which understands Tcp requests.
  *
  */
 public class TCPSampler extends AbstractSampler implements ThreadListener {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.tcp.config.gui.TCPConfigGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui"}));
 
     //++ JMX file constants - do not change
     public static final String SERVER = "TCPSampler.server"; //$NON-NLS-1$
 
     public static final String PORT = "TCPSampler.port"; //$NON-NLS-1$
 
     public static final String FILENAME = "TCPSampler.filename"; //$NON-NLS-1$
 
     public static final String CLASSNAME = "TCPSampler.classname";//$NON-NLS-1$
 
     public static final String NODELAY = "TCPSampler.nodelay"; //$NON-NLS-1$
 
     public static final String TIMEOUT = "TCPSampler.timeout"; //$NON-NLS-1$
 
     public static final String TIMEOUT_CONNECT = "TCPSampler.ctimeout"; //$NON-NLS-1$
 
     public static final String REQUEST = "TCPSampler.request"; //$NON-NLS-1$
 
     public static final String RE_USE_CONNECTION = "TCPSampler.reUseConnection"; //$NON-NLS-1$
     //-- JMX file constants - do not change
 
     private static final String TCPKEY = "TCP"; //$NON-NLS-1$ key for HashMap
 
     private static final String ERRKEY = "ERR"; //$NON-NLS-1$ key for HashMap
 
     // If set, this is the regex that is used to extract the status from the
     // response
     // NOT implemented yet private static final String STATUS_REGEX =
     // JMeterUtils.getPropDefault("tcp.status.regex","");
 
     // Otherwise, the response is scanned for these strings
     private static final String STATUS_PREFIX = JMeterUtils.getPropDefault("tcp.status.prefix", ""); //$NON-NLS-1$
 
     private static final String STATUS_SUFFIX = JMeterUtils.getPropDefault("tcp.status.suffix", ""); //$NON-NLS-1$
 
     private static final String STATUS_PROPERTIES = JMeterUtils.getPropDefault("tcp.status.properties", ""); //$NON-NLS-1$
 
     private static final Properties statusProps = new Properties();
 
     private static final boolean haveStatusProps;
 
     static {
         boolean hsp = false;
         log.debug("Status prefix=" + STATUS_PREFIX); //$NON-NLS-1$
         log.debug("Status suffix=" + STATUS_SUFFIX); //$NON-NLS-1$
         log.debug("Status properties=" + STATUS_PROPERTIES); //$NON-NLS-1$
         if (STATUS_PROPERTIES.length() > 0) {
             File f = new File(STATUS_PROPERTIES);
             FileInputStream fis = null;
             try {
                 fis = new FileInputStream(f);
                 statusProps.load(fis);
                 log.debug("Successfully loaded properties"); //$NON-NLS-1$
                 hsp = true;
             } catch (FileNotFoundException e) {
                 log.debug("Property file not found"); //$NON-NLS-1$
             } catch (IOException e) {
                 log.debug("Property file error " + e.toString()); //$NON-NLS-1$
             } finally {
                 JOrphanUtils.closeQuietly(fis);
             }
         }
         haveStatusProps = hsp;
     }
 
     /** the cache of TCP Connections */
     // KEY = TCPKEY or ERRKEY, Entry= Socket or String
     private static final ThreadLocal<Map<String, Object>> tp =
         new ThreadLocal<Map<String, Object>>() {
         @Override
         protected Map<String, Object> initialValue() {
             return new HashMap<String, Object>();
         }
     };
 
     private transient TCPClient protocolHandler;
     
     private transient boolean firstSample; // Are we processing the first sample?
 
     public TCPSampler() {
         log.debug("Created " + this); //$NON-NLS-1$
     }
 
     private String getError() {
         Map<String, Object> cp = tp.get();
         return (String) cp.get(ERRKEY);
     }
 
     private Socket getSocket(String socketKey) {
         Map<String, Object> cp = tp.get();
         Socket con = null;
         if (isReUseConnection()) {
             con = (Socket) cp.get(socketKey);
             if (con != null) {
                 log.debug(this + " Reusing connection " + con); //$NON-NLS-1$
             }
         }
         if (con == null) {
             // Not in cache, so create new one and cache it
             try {
                 closeSocket(socketKey); // Bug 44910 - close previous socket (if any)
                 SocketAddress sockaddr = new InetSocketAddress(getServer(), getPort());
                 con = new Socket();
                 con.connect(sockaddr, getConnectTimeout());
                 if(log.isDebugEnabled()) {
                     log.debug("Created new connection " + con); //$NON-NLS-1$
                 }
                 cp.put(socketKey, con);
             } catch (UnknownHostException e) {
                 log.warn("Unknown host for " + getLabel(), e);//$NON-NLS-1$
                 cp.put(ERRKEY, e.toString());
                 return null;
             } catch (IOException e) {
                 log.warn("Could not create socket for " + getLabel(), e); //$NON-NLS-1$
                 cp.put(ERRKEY, e.toString());
                 return null;
             }     
         }
         // (re-)Define connection params - Bug 50977 
         try {
             con.setSoTimeout(getTimeout());
             con.setTcpNoDelay(getNoDelay());
             if(log.isDebugEnabled()) {
                 log.debug(this + "  Timeout " + getTimeout() + " NoDelay " + getNoDelay()); //$NON-NLS-1$
             }
         } catch (SocketException se) {
             log.warn("Could not set timeout or nodelay for " + getLabel(), se); //$NON-NLS-1$
             cp.put(ERRKEY, se.toString());
         }
         return con;
     }
 
     /**
      * @return String socket key in cache Map
      */
     private final String getSocketKey() {
 		return TCPKEY+"#"+getServer()+"#"+getPort()+"#"+getUsername()+"#"+getPassword();
 	}
 
 	public String getUsername() {
         return getPropertyAsString(ConfigTestElement.USERNAME);
     }
 
     public String getPassword() {
         return getPropertyAsString(ConfigTestElement.PASSWORD);
     }
 
     public void setServer(String newServer) {
         this.setProperty(SERVER, newServer);
     }
 
     public String getServer() {
         return getPropertyAsString(SERVER);
     }
 
     public void setReUseConnection(String reuse) {
         this.setProperty(RE_USE_CONNECTION, reuse);
     }
 
     public boolean isReUseConnection() {
         return getPropertyAsBoolean(RE_USE_CONNECTION);
     }
 
     public void setPort(String newFilename) {
         this.setProperty(PORT, newFilename);
     }
 
     public int getPort() {
         return getPropertyAsInt(PORT);
     }
 
     public void setFilename(String newFilename) {
         this.setProperty(FILENAME, newFilename);
     }
 
     public String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     public void setRequestData(String newRequestData) {
         this.setProperty(REQUEST, newRequestData);
     }
 
     public String getRequestData() {
         return getPropertyAsString(REQUEST);
     }
 
     public void setTimeout(String newTimeout) {
         this.setProperty(TIMEOUT, newTimeout);
     }
 
     public int getTimeout() {
         return getPropertyAsInt(TIMEOUT);
     }
 
     public void setConnectTimeout(String newTimeout) {
         this.setProperty(TIMEOUT_CONNECT, newTimeout, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(TIMEOUT_CONNECT, 0);
     }
 
     public void setNoDelay(String newNoDelay) {
         this.setProperty(NODELAY, newNoDelay);
     }
 
     public boolean getNoDelay() {
         return getPropertyAsBoolean(NODELAY);
     }
 
     public void setClassname(String classname) {
         this.setProperty(CLASSNAME, classname, ""); //$NON-NLS-1$
     }
 
     public String getClassname() {
         String clazz = getPropertyAsString(CLASSNAME,"");
         if (clazz==null || clazz.length()==0){
             clazz = JMeterUtils.getPropDefault("tcp.handler", "TCPClientImpl"); //$NON-NLS-1$ $NON-NLS-2$
         }
         return clazz;
     }
 
     /**
      * Returns a formatted string label describing this sampler Example output:
      * Tcp://Tcp.nowhere.com/pub/README.txt
      *
      * @return a formatted string label describing this sampler
      */
     public String getLabel() {
         return ("tcp://" + this.getServer() + ":" + this.getPort());//$NON-NLS-1$ $NON-NLS-2$
     }
 
     private static final String protoPrefix = "org.apache.jmeter.protocol.tcp.sampler."; //$NON-NLS-1$
 
     private Class<?> getClass(String className) {
         Class<?> c = null;
         try {
             c = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
         } catch (ClassNotFoundException e) {
             try {
                 c = Class.forName(protoPrefix + className, false, Thread.currentThread().getContextClassLoader());
             } catch (ClassNotFoundException e1) {
                 log.error("Could not find protocol class '" + className+"'"); //$NON-NLS-1$
             }
         }
         return c;
 
     }
 
     private TCPClient getProtocol() {
         TCPClient TCPClient = null;
         Class<?> javaClass = getClass(getClassname());
         if (javaClass == null){
             return null;
         }
         try {
             TCPClient = (TCPClient) javaClass.newInstance();
             if (log.isDebugEnabled()) {
                 log.debug(this + "Created: " + getClassname() + "@" + Integer.toHexString(TCPClient.hashCode())); //$NON-NLS-1$
             }
         } catch (Exception e) {
             log.error(this + " Exception creating: " + getClassname(), e); //$NON-NLS-1$
         }
         return TCPClient;
     }
 
     public SampleResult sample(Entry e)// Entry tends to be ignored ...
     {
         if (firstSample) { // Do stuff we cannot do as part of threadStarted()
             initSampling();
             firstSample=false;
         }
         String socketKey = getSocketKey();
         log.debug(getLabel() + " " + getFilename() + " " + getUsername() + " " + getPassword());
         SampleResult res = new SampleResult();
         boolean isSuccessful = false;
         res.setSampleLabel(getName());// Use the test element name for the label
         res.setSamplerData("Host: " + getServer() + " Port: " + getPort()); //$NON-NLS-1$ $NON-NLS-2$
         res.sampleStart();
         try {
             Socket sock = getSocket(socketKey);
             if (sock == null) {
                 res.setResponseCode("500"); //$NON-NLS-1$
                 res.setResponseMessage(getError());
             } else if (protocolHandler == null){
                 res.setResponseCode("500"); //$NON-NLS-1$
                 res.setResponseMessage("Protocol handler not found");
             } else {
                 InputStream is = sock.getInputStream();
                 OutputStream os = sock.getOutputStream();
                 String req = getRequestData();
                 // TODO handle filenames
                 res.setSamplerData(req);
                 protocolHandler.write(os, req);
                 String in = protocolHandler.read(is);
                 isSuccessful = setupSampleResult(res, in, null, protocolHandler.getCharset());
             }
         } catch (ReadException ex) {
             log.error("", ex);
             isSuccessful=setupSampleResult(res, ex.getPartialResponse(), ex,protocolHandler.getCharset());
             closeSocket(socketKey);
         } catch (Exception ex) {
             log.error("", ex);
             isSuccessful=setupSampleResult(res, "", ex, protocolHandler.getCharset());
             closeSocket(socketKey);
         } finally {
             // Calculate response time
             res.sampleEnd();
 
             // Set if we were successful or not
             res.setSuccessful(isSuccessful);
 
             if (!isReUseConnection()) {
                 closeSocket(socketKey);
             }
         }
         return res;
     }
 
 	/**
 	 * Fills SampleResult object
 	 * @param sampleResult {@link SampleResult}
 	 * @param readResponse Response read until error occured
 	 * @param exception Source exception
      * @param encoding sample encoding
 	 * @return boolean if sample is considered as successful
 	 */
 	private boolean setupSampleResult(SampleResult sampleResult,
 			String readResponse, 
 			Exception exception,
 			String encoding) {
 		sampleResult.setResponseData(readResponse, encoding);
 		sampleResult.setDataType(SampleResult.TEXT);
 		if(exception==null) {
 			sampleResult.setResponseCodeOK();
 			sampleResult.setResponseMessage("OK"); //$NON-NLS-1$
 		} else {
 			sampleResult.setResponseCode("500"); //$NON-NLS-1$
 			sampleResult.setResponseMessage(exception.toString()); //$NON-NLS-1$
 		}
 		boolean isSuccessful = exception == null;
 		// Reset the status code if the message contains one
 		if (!StringUtils.isEmpty(readResponse) && STATUS_PREFIX.length() > 0) {
 		    int i = readResponse.indexOf(STATUS_PREFIX);
 		    int j = readResponse.indexOf(STATUS_SUFFIX, i + STATUS_PREFIX.length());
 		    if (i != -1 && j > i) {
 		        String rc = readResponse.substring(i + STATUS_PREFIX.length(), j);
 		        sampleResult.setResponseCode(rc);
 		        isSuccessful = isSuccessful && checkResponseCode(rc);
 		        if (haveStatusProps) {
 		            sampleResult.setResponseMessage(statusProps.getProperty(rc, "Status code not found in properties")); //$NON-NLS-1$
 		        } else {
 		            sampleResult.setResponseMessage("No status property file");
 		        }
 		    } else {
 		        sampleResult.setResponseCode("999"); //$NON-NLS-1$
 		        sampleResult.setResponseMessage("Status value not found");
 		        isSuccessful = false;
 		    }
 		}
 		return isSuccessful;
 	}
 
     /**
      * @param rc response code
      * @return whether this represents success or not
      */
     private boolean checkResponseCode(String rc) {
         if (rc.compareTo("400") >= 0 && rc.compareTo("499") <= 0) { //$NON-NLS-1$ $NON-NLS-2$
             return false;
         }
         if (rc.compareTo("500") >= 0 && rc.compareTo("599") <= 0) { //$NON-NLS-1$ $NON-NLS-2$
             return false;
         }
         return true;
     }
 
     public void threadStarted() {
         log.debug("Thread Started"); //$NON-NLS-1$
         firstSample = true;
     }
 
     // Cannot do this as part of threadStarted() because the Config elements have not been processed.
     private void initSampling(){
         protocolHandler = getProtocol();
         log.debug("Using Protocol Handler: " +  //$NON-NLS-1$
                 (protocolHandler == null ? "NONE" : protocolHandler.getClass().getName())); //$NON-NLS-1$
         if (protocolHandler != null){
             protocolHandler.setupTest();
         }
     }
 
     /**
      * Close socket of current sampler
      */
     private void closeSocket(String socketKey) {
         Map<String, Object> cp = tp.get();
         Socket con = (Socket) cp.remove(socketKey);
         if (con != null) {
             log.debug(this + " Closing connection " + con); //$NON-NLS-1$
             try {
                 con.close();
             } catch (IOException e) {
                 log.warn("Error closing socket "+e); //$NON-NLS-1$
             }
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void threadFinished() {
         log.debug("Thread Finished"); //$NON-NLS-1$
         tearDown();
         if (protocolHandler != null){
             protocolHandler.teardownTest();
         }
     }
 
     /**
      * Closes all connections, clears Map and remove thread local Map
      */
 	private void tearDown() {
 		Map<String, Object> cp = tp.get();
 		for (Map.Entry<String, Object> element : cp.entrySet()) {
 			if(element.getKey().startsWith(TCPKEY)) {
 				try {
 					((Socket)element.getValue()).close();
 				} catch (IOException e) {
 					// NOOP
 				}
 			}
 		}
 		cp.clear();
 		tp.remove();
 	}
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
 	@Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 7152a40cc..d8f84d00c 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,186 +1,187 @@
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
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.8 =================== -->
 
 <h1>Version 2.8</h1>
 
 <h2>New and Noteworthy</h2>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see Bug 52496).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 When using CacheManager, JMeter now caches responses for GET queries provided header Cache-Control is different from "no-cache" as described in specification.
 Furthermore it doesn't put anymore in Cache deprecated entries for "no-cache" responses. See <bugzilla>53521</bugzilla> and <bugzilla>53522</bugzilla> 
 </p>
 
 <p>
 A major change has occured on JSR223 Test Elements, previously variables set up before script execution where stored in ScriptEngineManager which was created once per execution, 
 now ScriptEngineManager is a singleton shared by all JSR223 elements and only ScriptEngine is created once per execution, variables set up before script execution are now stored 
 in Bindings created on each execution, see <bugzilla>53365</bugzilla>.
 </p>
 
 <p>
 JSR223 Test Elements using Script file are now Compiled if ScriptEngine supports this feature, see <bugzilla>53520</bugzilla>.
 </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+F1 (CMD + F1 for Mac OS), CTRL+F (CMD+F1 for Mac OS) now opens Search Dialog.
 </p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>53521</bugzilla> - Cache Manager should cache content with Cache-control=private</li>
 <li><bugzilla>53522</bugzilla> - Cache Manager should not store at all response with header "no-cache" and store other types of Cache-Control having max-age value</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>53348</bugzilla> - JMeter JMS Point-to-Point Request-Response sampler doesn't work when Request-queue and Receive-queue are different</li>
 <li><bugzilla>53357</bugzilla> - JMS Point to Point reports too high response times in Request Response Mode</li>
 <li><bugzilla>53440</bugzilla> - SSL connection leads to ArrayStoreException on JDK 6 with some KeyManagerFactory SPI</li>
 <li><bugzilla>53511</bugzilla> - access log sampler SessionFilter throws NullPointerException - cookie manager not initialized properly</li>
 <li><bugzilla>53715</bugzilla> - JMeter does not load WSDL</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>53742</bugzilla> - When jmeter.save.saveservice.sample_count is set to true, elapsed time read by listener is always equal to 0</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>51512</bugzilla> - Cookies aren't inserted into HTTP request with IPv6 Host header</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53365</bugzilla> - JSR223TestElement should cache ScriptEngineManager</li>
 <li><bugzilla>53520</bugzilla> - JSR223 Elements : Use Compilable interface to improve performances on File scripts</li>
 <li><bugzilla>53501</bugzilla> - Synchronization timer blocks test end.</li>
 <li><bugzilla>53750</bugzilla> - TestCompiler saves unnecessary entries in pairing collection</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>55310</bugzilla> - TestAction should implement Interruptible</li>
 <li><bugzilla>53318</bugzilla> - Add Embedded URL Filter to HTTP Request Defaults Control </li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>53566</bugzilla> - Don't log partial responses to the jmeter log</li>
 <li><bugzilla>53716</bugzilla> - Small improvements in aggregate graph: legend at left or right is now on 1 column (instead of 1 large line), no border to the reference's square color, reduce width on some fields</li>
 <li><bugzilla>53718</bugzilla> - Add a new visualizer 'Response Time Graph' to draw a line graph showing the evolution of response time for a test</li>
 <li><bugzilla>53738</bugzilla> - Keep track of number of threads started and finished</li>
 <li><bugzilla>53753</bugzilla> -  Summariser: no point displaying fractional time in most cases</li>
 <li><bugzilla>53749</bugzilla> - TestListener interface could perhaps be split up. 
 This should reduce per-thread memory requirements and processing, 
 as only test elements that actually use testIterationStart functionality now need to be handled.</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>53755</bugzilla> - Adding a HttpClient 4 cookie implementation in JMeter. 
 Cookie Manager has now the default HC3.1 implementation and a new choice HC4 implementation (compliant with IPv6 address)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53364</bugzilla> - Sort list of Functions in Function Helper Dialog</li>
 <li><bugzilla>53418</bugzilla> - New Option "Delay thread creation until needed" that will create and start threads when needed instead of creating them on Test startup</li>
 <li><bugzilla>42245</bugzilla> - Show clear passwords in HTTP Authorization Manager</li>
 <li><bugzilla>53616</bugzilla> - Display 'Apache JMeter' title in app title bar in Gnome 3</li>
 <li><bugzilla>53759</bugzilla> - ClientJMeterEngine perfoms unnecessary traverse using SearchByClass(TestListener)</li>
 <li><bugzilla>52601</bugzilla> - CTRL + F for the new Find feature</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li><bugzilla>53311</bugzilla> - JMeterUtils#runSafe should not throw Error when interrupted</li>
 <li>Updated to commons-net-3.1 (from 3.0.1)</li>
 <li>Updated to HttpComponents Core 4.2.1 (from 4.1.4) and HttpComponents Client 4.2.1 (from 4.1.3)</li>
+<li><bugzilla>53765</bugzilla> - Switch to commons-lang3-3.1</li>
 </ul>
 
 </section> 
 </body> 
 </document>
\ No newline at end of file
