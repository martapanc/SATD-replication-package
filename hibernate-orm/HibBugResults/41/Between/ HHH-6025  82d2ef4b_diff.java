diff --git a/build.gradle b/build.gradle
index 4268fd6139..113337409a 100644
--- a/build.gradle
+++ b/build.gradle
@@ -1,303 +1,300 @@
 apply plugin: 'eclipse'
 apply plugin: 'idea'
 
 allprojects {
     repositories {
         mavenCentral()
         mavenLocal()
         mavenRepo name: 'jboss-nexus', urls: "https://repository.jboss.org/nexus/content/groups/public/"
         mavenRepo name: "jboss-snapshots", urls: "http://snapshots.jboss.org/maven2/"
     }
 }
 
 ideaProject {
     javaVersion = "1.6"
     withXml { provider ->
         def node = provider.asNode()
         def vcsConfig = node.component.find { it.'@name' == 'VcsDirectoryMappings' }
         vcsConfig.mapping[0].'@vcs' = 'Git'
     }
 }
 
 ideaModule {
 }
 
 // build a map of the dependency artifacts to use.  Allows centralized definition of the version of artifacts to
 // use.  In that respect it serves a role similar to <dependencyManagement> in Maven
 slf4jVersion = '1.5.8'
 libraries = [
         // Ant
         ant:            'ant:ant:1.6.5',
 
         // Antlr
         antlr:          'antlr:antlr:2.7.7',
 
         // Annotations
         commons_annotations:
                         'org.hibernate:hibernate-commons-annotations:3.2.0.Final',
 
-        // CGLIB
-        cglib:          'cglib:cglib:2.2',
-
         // Jakarta commons-collections  todo : get rid of commons-collections dependency
         commons_collections:
                         'commons-collections:commons-collections:3.1',
 
         // Dom4J
         dom4j:          'dom4j:dom4j:1.6.1@jar',
 
         // h2
         h2:             'com.h2database:h2:1.2.145',
 
         // Javassist
         javassist:      'javassist:javassist:3.12.0.GA',
 
         // javax
         jpa:            'org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.0.Final',
         jta:            'javax.transaction:jta:1.1',
         validation:     'javax.validation:validation-api:1.0.0.GA',
         validator:      'org.hibernate:hibernate-validator:4.0.2.GA',
         jacc:           'org.jboss.javaee:jboss-jacc-api:1.1.0.GA',
 
         // logging
         logging:        'org.jboss.logging:jboss-logging:3.0.0.Beta5',
         logging_tools:  'org.jboss.logging:jboss-logging-tools:1.0.0.Beta4',
         slf4j_api:      'org.slf4j:slf4j-api:' + slf4jVersion,
         slf4j_simple:   'org.slf4j:slf4j-simple:' + slf4jVersion,
         jcl_slf4j:      'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
         jcl_api:        'commons-logging:commons-logging-api:99.0-does-not-exist',
         jcl:            'commons-logging:commons-logging:99.0-does-not-exist',
 
         // testing
         atomikos:       'com.atomikos:transactions-jdbc:3.7.0',
         junit:          'junit:junit:4.8.2',
         testng:         'org.testng:testng:5.8:jdk15',
         jpa_modelgen:   'org.hibernate:hibernate-jpamodelgen:1.1.1.Final',
         shrinkwrap_api: 'org.jboss.shrinkwrap:shrinkwrap-api:1.0.0-alpha-6',
         shrinkwrap:     'org.jboss.shrinkwrap:shrinkwrap-impl-base:1.0.0-alpha-6'
 ]
 
 
 subprojects { subProject ->
     apply plugin: 'idea'
 	apply plugin: 'eclipse'
 
     defaultTasks 'build'
 
     group = 'org.hibernate'
     version = '4.0.0-SNAPSHOT'
 
     // minimize changes, at least for now (gradle uses 'build' by default)..
     buildDir = "target"
     
     if ( ! subProject.name.startsWith( 'release' ) ) {
         apply plugin: 'java'
         apply plugin: 'maven' // for install task as well as deploy dependencies
         apply plugin: org.hibernate.build.gradle.upload.UploadAuthenticationManager
 
         configurations {
             provided {
                 // todo : need to make sure these are non-exported
                 description = 'Non-exported compile-time dependencies.'
             }
             jbossLoggingTool {
                 description = "Dependencies for running the JBoss logging AnnotationProcessor tool"
             }
             hibernateJpaModelGenTool {
                 description = "Dependencies for running the Hibernate JPA Metamodel Generator AnnotationProcessor tool"
             }
             deployerJars {
                 description = 'Jars needed for doing deployment to JBoss Nexus repo'
             }
         }
 
         // appropriately inject the common dependencies into each sub-project
         dependencies {
 		    compile( libraries.logging )
             compile( libraries.slf4j_api )
             testCompile( libraries.junit )
             testCompile( libraries.atomikos )
             testRuntime( libraries.slf4j_simple )
             testRuntime( libraries.jcl_slf4j )
             testRuntime( libraries.jcl_api )
             testRuntime( libraries.jcl )
             testRuntime( libraries.javassist )
             testRuntime( libraries.h2 )
 		    jbossLoggingTool( libraries.logging_tools )
             hibernateJpaModelGenTool( libraries.jpa_modelgen )
             deployerJars "org.apache.maven.wagon:wagon-http:1.0-beta-6"
         }
 
         aptDumpDir = file( buildDirName + "/temp/apt" )
 
         sourceSets.main {
             compileClasspath += configurations.provided
         }
 
         sourceSets.all {
             originalJavaSrcDirs = java.srcDirs
             generatedLoggingSrcDir = file( "${buildDir}/generated-src/logging/${name}" )
             java.srcDir generatedLoggingSrcDir
         }
 
         task generateMainLoggingClasses(type: Compile) {
 		    classpath = compileJava.classpath + configurations.jbossLoggingTool
 		    source = sourceSets.main.originalJavaSrcDirs
             destinationDir = aptDumpDir
 		    options.define(
                     compilerArgs: [
                             "-nowarn",
                             "-proc:only",
                             "-processor", "org.jboss.logging.LoggingToolsProcessor",
                             "-s", "$sourceSets.main.generatedLoggingSrcDir.absolutePath"
                     ]
             );
             outputs.dir sourceSets.main.generatedLoggingSrcDir;
             doFirst {
                 sourceSets.main.generatedLoggingSrcDir.mkdirs()
             }
         }
 
         // for the time being eat the annoying output from running the annotation processors
         generateMainLoggingClasses.logging.captureStandardError(LogLevel.INFO)
 
 		compileJava.dependsOn generateMainLoggingClasses
         compileJava.options.define(compilerArgs: ["-proc:none"])
 
 
         task generateTestLoggingClasses(type: Compile) {
 		    classpath = compileTestJava.classpath + configurations.jbossLoggingTool
             source = sourceSets.test.originalJavaSrcDirs
             destinationDir = aptDumpDir
 		    options.define(
                     compilerArgs: [
                             "-nowarn",
                             "-proc:only",
                             "-processor", "org.jboss.logging.LoggingToolsProcessor",
                             "-s", "$sourceSets.test.generatedLoggingSrcDir.absolutePath"
                     ]
             );
             outputs.dir sourceSets.test.generatedLoggingSrcDir;
             doFirst {
                 sourceSets.test.generatedLoggingSrcDir.mkdirs()
             }
         }
 
         // for the time being eat the annoying output from running the annotation processors
         generateTestLoggingClasses.logging.captureStandardError(LogLevel.INFO)
 
 		compileTestJava.dependsOn generateTestLoggingClasses
         compileTestJava.options.define(compilerArgs: ["-proc:none"])
 
 
         manifest.mainAttributes(
                 provider: 'gradle',
                 'Implementation-Url': 'http://hibernate.org',
                 'Implementation-Version': version,
                 'Implementation-Vendor': 'Hibernate.org',
                 'Implementation-Vendor-Id': 'org.hibernate'
         )
 
         test {
             systemProperties['hibernate.test.validatefailureexpected'] = true
             maxHeapSize = "1024m"
         }
 
         processTestResources.doLast( {
             copy {
                 from( sourceSets.test.java.srcDirs ) {
                     include '**/*.properties'
                     include '**/*.xml'
                 }
                 into sourceSets.test.classesDir
             }
         } )
 
         assemble.doLast( { install } )
         uploadArchives.dependsOn install
 
         targetCompatibility = "1.6"
         sourceCompatibility = "1.6"
 
         ideaModule {
             // treat our "provided" configuration dependencies as "compile" scope dependencies in IntelliJ
             scopes.COMPILE.plus.add( configurations.provided )
             whenConfigured { module ->
                 module.dependencies*.exported = true
             }
 //	        testSourceDirs.add( file( generatedSrcDirName ) )
 //	        testSourceDirs.add( file( generatedTestSrcDirName ) )
         }
 		eclipseClasspath {
 			plusConfigurations.add( configurations.provided )
 		}
 
         // elements used to customize the generated POM used during upload
         def pomConfig = {
             url 'http://hibernate.org'
             organization {
                 name 'Hibernate.org'
                 url 'http://hibernate.org'
             }
             issueManagement {
                 system 'jira'
                 url 'http://opensource.atlassian.com/projects/hibernate/browse/HHH'
             }
             scm {
                 url "http://github.com/hibernate/hibernate-core"
                 connection "scm:git:http://github.com/hibernate/hibernate-core.git"
                 developerConnection "scm:git:git@github.com:hibernate/hibernate-core.git"
             }
             licenses {
                 license {
                     name 'GNU Lesser General Public License'
                     url 'http://www.gnu.org/licenses/lgpl-2.1.html'
                     comments 'See discussion at http://hibernate.org/license for more details.'
                     distribution 'repo'
                 }
             }
             developers {
             }
         }
 
         subProject.basePomConfig = pomConfig
 
         configure(install.repositories.mavenInstaller) {
             pom.project pomConfig
         }
 
         uploadArchives {
             repositories.mavenDeployer {
                 name = 'jbossDeployer'
                 configuration = configurations.deployerJars
                 pom.project pomConfig
                 repository(id: "jboss-releases-repository", url: "https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/")
                 snapshotRepository(id: "jboss-snapshots-repository", url: "https://repository.jboss.org/nexus/content/repositories/snapshots")
             }
         }
 
         task sourcesJar(type: Jar, dependsOn: compileJava) {
             from sourceSets.main.allSource
             classifier = 'sources'
         }
 
         artifacts {
             archives sourcesJar
         }
 
         uploadArchives.dependsOn sourcesJar
     }
 
 }
 
 dependsOnChildren()
 
 // This is a task that generates the gradlew scripts, allowing users to run gradle without having gradle installed
 // on their system.
 // This task should be run by "build master" and the resulting ouput committed to source control.  Its outputs include:
 //  1) /gradlew which is the *NIX shell script for executing builds
 //  2) /gradlew.bat which is the windows bat script for for executing builds
 //  3) /wrapper which is a directory named by the "jarPath" config which contains other needed files.
 task wrapper(type: Wrapper) {
     gradleVersion = '0.9.2'
 }
diff --git a/hibernate-core/hibernate-core.gradle b/hibernate-core/hibernate-core.gradle
index 19cd7ef59f..e777062524 100644
--- a/hibernate-core/hibernate-core.gradle
+++ b/hibernate-core/hibernate-core.gradle
@@ -1,101 +1,95 @@
 apply plugin: 'java'
 apply plugin: 'antlr'
 
 dependencies {
 	compile( libraries.commons_collections )
     compile( libraries.jta )
     compile( libraries.dom4j ) {
         transitive = false
     }
     compile( libraries.commons_annotations )
     compile( libraries.jpa )
     antlr( libraries.antlr )
     provided( libraries.javassist )
-    provided( libraries.cglib ) {
-        transitive = true
-    }
     provided( libraries.ant )
     provided( libraries.jacc )
     provided( libraries.validation )
     testCompile( project(':hibernate-testing') )
     testCompile( libraries.validation )
     testCompile( libraries.validator ) {
         // for test runtime
         transitive = true
     }
     testRuntime( 'jaxen:jaxen:1.1' )
     testRuntime( libraries.javassist )
-    testRuntime( libraries.cglib ) {
-        transitive = true
-    }
 }
 
 manifest.mainAttributes(
         'Main-Class': 'org.hibernate.Version'
 )
 
 sourceSets {
     test {
         // resources inherently exclude sources
         resources {
             setSrcDirs( ['src/test/java','src/test/resources'] )
         }
     }
 }
 
 ideaModule {
     sourceDirs.add( file( '$buildDir/generated-src/antlr/main' ) )
 }
 
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 // This entire section below is all about creating, installing and uploading the hibernate-testing artifacts.
 // This is obviously extremely verbose.  Check back once Gradle goes m2 for 1.0 to see about the MavenPublication
 // approach Adam talked about as a means to make this more succinct and natural.
 // todo : check back once Gradle goes m2 for 1.0 to see about the MavenPublication approach
 configurations {
     testing
 }
 
 task testingJar(type: Jar, dependsOn: compileTestJava) {
     from sourceSets.test.classes
     includes += "org/hibernate/testing/**"
     baseName = 'hibernate-testing'
 }
 
 task testingSourcesJar(type: Jar, dependsOn: compileTestJava) {
     from sourceSets.test.allSource
     includes += "org/hibernate/testing/**"
     baseName = 'hibernate-testing'
     classifier = 'sources'
 }
 
 artifacts {
     testing testingJar, testingSourcesJar
 }
 
 // ugh, lots of duplication with uploadArchives
 uploadTesting {
     repositories.mavenDeployer {
         name = 'jbossDeployer'
         configuration = configurations.deployerJars
         pom.project basePomConfig
         pom.artifactId = 'hibernate-testing'
         repository(id: "jboss-releases-repository", url: "https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/")
         snapshotRepository(id: "jboss-snapshots-repository", url: "https://repository.jboss.org/nexus/content/repositories/snapshots")
     }
 }
 
 uploadArchives.dependsOn uploadTesting
 
 task installTesting(type:Upload, dependsOn: [testingJar,testingSourcesJar]) {
     configuration = configurations.testing
     repositories.mavenInstaller {
         name = RepositoryHandler.DEFAULT_MAVEN_INSTALLER_NAME
         pom.project basePomConfig
         pom.artifactId = 'hibernate-testing'
     }
 }
 
 install.dependsOn installTesting
 uploadTesting.dependsOn installTesting
 
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java
deleted file mode 100644
index 42b8e6cabe..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java
+++ /dev/null
@@ -1,101 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.bytecode.buildtime;
-
-import java.io.ByteArrayInputStream;
-import java.util.Set;
-import net.sf.cglib.core.ClassNameReader;
-import net.sf.cglib.transform.impl.InterceptFieldEnabled;
-import org.hibernate.bytecode.ClassTransformer;
-import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
-import org.hibernate.bytecode.util.BasicClassFilter;
-import org.hibernate.bytecode.util.ClassDescriptor;
-import org.objectweb.asm.ClassReader;
-
-/**
- * Strategy for performing build-time instrumentation of persistent classes in order to enable
- * field-level interception using CGLIB.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public class CGLIBInstrumenter extends AbstractInstrumenter {
-	private static final BasicClassFilter CLASS_FILTER = new BasicClassFilter();
-
-	private final BytecodeProviderImpl provider = new BytecodeProviderImpl();
-
-	public CGLIBInstrumenter(Logger logger, Options options) {
-		super( logger, options );
-	}
-
-	@Override
-    protected ClassDescriptor getClassDescriptor(byte[] byecode) throws Exception {
-		return new CustomClassDescriptor( byecode );
-	}
-
-	@Override
-    protected ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames) {
-		if ( descriptor.isInstrumented() ) {
-			logger.debug( "class [" + descriptor.getName() + "] already instrumented" );
-			return null;
-		}
-		else {
-			return provider.getTransformer( CLASS_FILTER, new CustomFieldFilter( descriptor, classNames ) );
-		}
-	}
-
-	private static class CustomClassDescriptor implements ClassDescriptor {
-		private final byte[] bytecode;
-		private final String name;
-		private final boolean isInstrumented;
-
-		public CustomClassDescriptor(byte[] bytecode) throws Exception {
-			this.bytecode = bytecode;
-			ClassReader reader = new ClassReader( new ByteArrayInputStream( bytecode ) );
-			String[] names = ClassNameReader.getClassInfo( reader );
-			this.name = names[0];
-			boolean instrumented = false;
-			for ( int i = 1; i < names.length; i++ ) {
-				if ( InterceptFieldEnabled.class.getName().equals( names[i] ) ) {
-					instrumented = true;
-					break;
-				}
-			}
-			this.isInstrumented = instrumented;
-		}
-
-		public String getName() {
-			return name;
-		}
-
-		public boolean isInstrumented() {
-			return isInstrumented;
-		}
-
-		public byte[] getBytes() {
-			return bytecode;
-		}
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java
deleted file mode 100644
index e53465eaad..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java
+++ /dev/null
@@ -1,126 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
-import java.io.Serializable;
-import net.sf.cglib.beans.BulkBean;
-import net.sf.cglib.beans.BulkBeanException;
-import org.hibernate.PropertyAccessException;
-import org.hibernate.bytecode.ReflectionOptimizer;
-
-/**
- * The {@link ReflectionOptimizer.AccessOptimizer} implementation for CGLIB
- * which simply acts as an adpater to the {@link BulkBean} class.
- *
- * @author Steve Ebersole
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class AccessOptimizerAdapter implements ReflectionOptimizer.AccessOptimizer, Serializable {
-
-	public static final String PROPERTY_GET_EXCEPTION =
-			"exception getting property value with CGLIB (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
-
-	public static final String PROPERTY_SET_EXCEPTION =
-			"exception setting property value with CGLIB (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
-
-	private Class mappedClass;
-	private BulkBean bulkBean;
-
-	public AccessOptimizerAdapter(BulkBean bulkBean, Class mappedClass) {
-		this.bulkBean = bulkBean;
-		this.mappedClass = mappedClass;
-	}
-
-	public String[] getPropertyNames() {
-		return bulkBean.getGetters();
-	}
-
-	public Object[] getPropertyValues(Object object) {
-		try {
-			return bulkBean.getPropertyValues( object );
-		}
-		catch ( Throwable t ) {
-			throw new PropertyAccessException(
-					t,
-			        PROPERTY_GET_EXCEPTION,
-			        false,
-			        mappedClass,
-			        getterName( t, bulkBean )
-			);
-		}
-	}
-
-	public void setPropertyValues(Object object, Object[] values) {
-		try {
-			bulkBean.setPropertyValues( object, values );
-		}
-		catch ( Throwable t ) {
-			throw new PropertyAccessException(
-					t,
-			        PROPERTY_SET_EXCEPTION,
-			        true,
-			        mappedClass,
-			        setterName( t, bulkBean )
-			);
-		}
-	}
-
-	private static String setterName(Throwable t, BulkBean optimizer) {
-		if ( t instanceof BulkBeanException ) {
-			return optimizer.getSetters()[( ( BulkBeanException ) t ).getIndex()];
-		}
-		else {
-			return "?";
-		}
-	}
-
-	private static String getterName(Throwable t, BulkBean optimizer) {
-		if ( t instanceof BulkBeanException ) {
-			return optimizer.getGetters()[( ( BulkBeanException ) t ).getIndex()];
-		}
-		else {
-			return "?";
-		}
-	}
-
-	private void writeObject(ObjectOutputStream out) throws IOException {
-		out.writeObject( mappedClass );
-		out.writeObject( bulkBean.getGetters() );
-		out.writeObject( bulkBean.getSetters() );
-		out.writeObject( bulkBean.getPropertyTypes() );
-	}
-
-	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
-		Class beanClass = ( Class ) in.readObject();
-		String[] getters = ( String[] ) in.readObject();
-		String[] setters = ( String[] ) in.readObject();
-		Class[] types = ( Class[] ) in.readObject();
-		bulkBean = BulkBean.create( beanClass, getters, setters, types );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
deleted file mode 100644
index 2c163be1c8..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
+++ /dev/null
@@ -1,106 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-
-import java.lang.reflect.Modifier;
-import net.sf.cglib.beans.BulkBean;
-import net.sf.cglib.beans.BulkBeanException;
-import net.sf.cglib.reflect.FastClass;
-import org.hibernate.HibernateLogger;
-import org.hibernate.bytecode.BytecodeProvider;
-import org.hibernate.bytecode.ProxyFactoryFactory;
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.bytecode.util.FieldFilter;
-import org.hibernate.internal.util.StringHelper;
-import org.jboss.logging.Logger;
-
-/**
- * Bytecode provider implementation for CGLIB.
- *
- * @author Steve Ebersole
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class BytecodeProviderImpl implements BytecodeProvider {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BytecodeProviderImpl.class.getName());
-
-    public BytecodeProviderImpl() {
-        LOG.deprecated();
-    }
-
-    public ProxyFactoryFactory getProxyFactoryFactory() {
-        return new ProxyFactoryFactoryImpl();
-    }
-
-    public ReflectionOptimizer getReflectionOptimizer(
-                                                      Class clazz,
-                                                      String[] getterNames,
-                                                      String[] setterNames,
-                                                      Class[] types) {
-        FastClass fastClass;
-        BulkBean bulkBean;
-        try {
-            fastClass = FastClass.create( clazz );
-            bulkBean = BulkBean.create( clazz, getterNames, setterNames, types );
-            if ( !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
-                if ( fastClass == null ) {
-                    bulkBean = null;
-                }
-                else {
-                    //test out the optimizer:
-                    Object instance = fastClass.newInstance();
-                    bulkBean.setPropertyValues( instance, bulkBean.getPropertyValues( instance ) );
-                }
-            }
-        }
-        catch( Throwable t ) {
-            fastClass = null;
-            bulkBean = null;
-            if (LOG.isDebugEnabled()) {
-                int index = 0;
-                if (t instanceof BulkBeanException) index = ((BulkBeanException)t).getIndex();
-                if (index >= 0) LOG.debugf("Reflection optimizer disabled for: %s [%s: %s (property %s)",
-                                           clazz.getName(),
-                                           StringHelper.unqualify(t.getClass().getName()),
-                                           t.getMessage(),
-                                           setterNames[index]);
-                else LOG.debugf("Reflection optimizer disabled for: %s [%s: %s",
-                                clazz.getName(),
-                                StringHelper.unqualify(t.getClass().getName()),
-                                t.getMessage());
-            }
-        }
-
-        if (fastClass != null && bulkBean != null) return new ReflectionOptimizerImpl(new InstantiationOptimizerAdapter(fastClass),
-                                                                                      new AccessOptimizerAdapter(bulkBean, clazz));
-        return null;
-    }
-
-    public org.hibernate.bytecode.ClassTransformer getTransformer(org.hibernate.bytecode.util.ClassFilter classFilter, FieldFilter fieldFilter) {
-        return new CglibClassTransformer( classFilter, fieldFilter );
-    }
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java
deleted file mode 100644
index b72aa1a60e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java
+++ /dev/null
@@ -1,136 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.security.ProtectionDomain;
-import net.sf.cglib.core.ClassNameReader;
-import net.sf.cglib.core.DebuggingClassWriter;
-import net.sf.cglib.transform.ClassReaderGenerator;
-import net.sf.cglib.transform.ClassTransformer;
-import net.sf.cglib.transform.TransformingClassGenerator;
-import net.sf.cglib.transform.impl.InterceptFieldEnabled;
-import net.sf.cglib.transform.impl.InterceptFieldFilter;
-import net.sf.cglib.transform.impl.InterceptFieldTransformer;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.bytecode.AbstractClassTransformerImpl;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
-import org.jboss.logging.Logger;
-import org.objectweb.asm.ClassReader;
-import org.objectweb.asm.ClassWriter;
-import org.objectweb.asm.Type;
-
-/**
- * Enhance the classes allowing them to implements InterceptFieldEnabled
- * This interface is then used by Hibernate for some optimizations.
- *
- * @author Emmanuel Bernard
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class CglibClassTransformer extends AbstractClassTransformerImpl {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, CglibClassTransformer.class.getName());
-
-	public CglibClassTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
-		super( classFilter, fieldFilter );
-	}
-
-	@Override
-    protected byte[] doTransform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer) {
-		ClassReader reader;
-		try {
-			reader = new ClassReader( new ByteArrayInputStream( classfileBuffer ) );
-		}
-		catch (IOException e) {
-            LOG.unableToReadClass(e.getMessage());
-			throw new HibernateException( "Unable to read class: " + e.getMessage() );
-		}
-
-		String[] names = ClassNameReader.getClassInfo( reader );
-		ClassWriter w = new DebuggingClassWriter( ClassWriter.COMPUTE_MAXS  );
-		ClassTransformer t = getClassTransformer( names );
-		if ( t != null ) {
-            LOG.debugf("Enhancing %s", className);
-			ByteArrayOutputStream out;
-			byte[] result;
-			try {
-				reader = new ClassReader( new ByteArrayInputStream( classfileBuffer ) );
-				new TransformingClassGenerator(
-						new ClassReaderGenerator( reader, skipDebug() ), t
-				).generateClass( w );
-				out = new ByteArrayOutputStream();
-				out.write( w.toByteArray() );
-				result = out.toByteArray();
-				out.close();
-			}
-			catch (Exception e) {
-                LOG.unableToTransformClass(e.getMessage());
-				throw new HibernateException( "Unable to transform class: " + e.getMessage() );
-			}
-			return result;
-		}
-		return classfileBuffer;
-	}
-
-	private int skipDebug() {
-		return ClassReader.SKIP_DEBUG;
-	}
-
-	private ClassTransformer getClassTransformer(final String[] classInfo) {
-		if ( isAlreadyInstrumented( classInfo ) ) {
-			return null;
-		}
-		return new InterceptFieldTransformer(
-				new InterceptFieldFilter() {
-					public boolean acceptRead(Type owner, String name) {
-						return fieldFilter.shouldTransformFieldAccess( classInfo[0], owner.getClassName(), name );
-					}
-
-					public boolean acceptWrite(Type owner, String name) {
-						return fieldFilter.shouldTransformFieldAccess( classInfo[0], owner.getClassName(), name );
-					}
-				}
-		);
-	}
-
-	private boolean isAlreadyInstrumented(String[] classInfo) {
-		for ( int i = 1; i < classInfo.length; i++ ) {
-			if ( InterceptFieldEnabled.class.getName().equals( classInfo[i] ) ) {
-				return true;
-			}
-		}
-		return false;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java
deleted file mode 100644
index b49b6ff2ec..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java
+++ /dev/null
@@ -1,71 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
-import java.io.Serializable;
-import net.sf.cglib.reflect.FastClass;
-import org.hibernate.InstantiationException;
-import org.hibernate.bytecode.ReflectionOptimizer;
-
-/**
- * The {@link ReflectionOptimizer.InstantiationOptimizer} implementation for CGLIB
- * which simply acts as an adapter to the {@link FastClass} class.
- *
- * @author Steve Ebersole
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class InstantiationOptimizerAdapter implements ReflectionOptimizer.InstantiationOptimizer, Serializable {
-	private FastClass fastClass;
-
-	public InstantiationOptimizerAdapter(FastClass fastClass) {
-		this.fastClass = fastClass;
-	}
-
-	public Object newInstance() {
-		try {
-			return fastClass.newInstance();
-		}
-		catch ( Throwable t ) {
-			throw new InstantiationException(
-					"Could not instantiate entity with CGLIB optimizer: ",
-			        fastClass.getJavaClass(),
-			        t
-			);
-		}
-	}
-
-	private void writeObject(ObjectOutputStream out) throws IOException {
-		out.writeObject( fastClass.getJavaClass() );
-	}
-
-	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
-		Class beanClass = ( Class ) in.readObject();
-		fastClass = FastClass.create( beanClass );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
deleted file mode 100644
index 5e93aaa224..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
+++ /dev/null
@@ -1,166 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-import java.lang.reflect.Method;
-import java.util.HashMap;
-import net.sf.cglib.proxy.Callback;
-import net.sf.cglib.proxy.CallbackFilter;
-import net.sf.cglib.proxy.Enhancer;
-import net.sf.cglib.proxy.Factory;
-import net.sf.cglib.proxy.MethodInterceptor;
-import net.sf.cglib.proxy.MethodProxy;
-import net.sf.cglib.proxy.NoOp;
-import org.hibernate.AssertionFailure;
-import org.hibernate.HibernateException;
-import org.hibernate.bytecode.BasicProxyFactory;
-import org.hibernate.bytecode.ProxyFactoryFactory;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.proxy.pojo.cglib.CGLIBProxyFactory;
-
-/**
- * A factory for CGLIB-based {@link ProxyFactory} instances.
- *
- * @author Steve Ebersole
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class ProxyFactoryFactoryImpl implements ProxyFactoryFactory {
-
-	/**
-	 * Builds a CGLIB-based proxy factory.
-	 *
-	 * @return a new CGLIB-based proxy factory.
-	 */
-	public ProxyFactory buildProxyFactory() {
-		return new CGLIBProxyFactory();
-	}
-
-	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces) {
-		return new BasicProxyFactoryImpl( superClass, interfaces );
-	}
-
-	public static class BasicProxyFactoryImpl implements BasicProxyFactory {
-		private final Class proxyClass;
-		private final Factory factory;
-
-		public BasicProxyFactoryImpl(Class superClass, Class[] interfaces) {
-			if ( superClass == null && ( interfaces == null || interfaces.length < 1 ) ) {
-				throw new AssertionFailure( "attempting to build proxy without any superclass or interfaces" );
-			}
-
-			Enhancer en = new Enhancer();
-			en.setUseCache( false );
-			en.setInterceptDuringConstruction( false );
-			en.setUseFactory( true );
-			en.setCallbackTypes( CALLBACK_TYPES );
-			en.setCallbackFilter( FINALIZE_FILTER );
-			if ( superClass != null ) {
-				en.setSuperclass( superClass );
-			}
-			if ( interfaces != null && interfaces.length > 0 ) {
-				en.setInterfaces( interfaces );
-			}
-			proxyClass = en.createClass();
-			try {
-				factory = ( Factory ) proxyClass.newInstance();
-			}
-			catch ( Throwable t ) {
-				throw new HibernateException( "Unable to build CGLIB Factory instance" );
-			}
-		}
-
-		public Object getProxy() {
-			try {
-				return factory.newInstance(
-						new Callback[] { new PassThroughInterceptor( proxyClass.getName() ), NoOp.INSTANCE }
-				);
-			}
-			catch ( Throwable t ) {
-				throw new HibernateException( "Unable to instantiate proxy instance" );
-			}
-		}
-	}
-
-	private static final CallbackFilter FINALIZE_FILTER = new CallbackFilter() {
-		public int accept(Method method) {
-			if ( method.getParameterTypes().length == 0 && method.getName().equals("finalize") ){
-				return 1;
-			}
-			else {
-				return 0;
-			}
-		}
-	};
-
-	private static final Class[] CALLBACK_TYPES = new Class[] { MethodInterceptor.class, NoOp.class };
-
-	private static class PassThroughInterceptor implements MethodInterceptor {
-		private HashMap data = new HashMap();
-		private final String proxiedClassName;
-
-		public PassThroughInterceptor(String proxiedClassName) {
-			this.proxiedClassName = proxiedClassName;
-		}
-
-		public Object intercept(
-				Object obj,
-		        Method method,
-		        Object[] args,
-		        MethodProxy proxy) throws Throwable {
-			String name = method.getName();
-			if ( "toString".equals( name ) ) {
-				return proxiedClassName + "@" + System.identityHashCode( obj );
-			}
-			else if ( "equals".equals( name ) ) {
-				return args[0] instanceof Factory && ( ( Factory ) args[0] ).getCallback( 0 ) == this
-						? Boolean.TRUE
-			            : Boolean.FALSE;
-			}
-			else if ( "hashCode".equals( name ) ) {
-				return new Integer( System.identityHashCode( obj ) );
-			}
-			boolean hasGetterSignature = method.getParameterTypes().length == 0 && method.getReturnType() != null;
-			boolean hasSetterSignature = method.getParameterTypes().length == 1 && ( method.getReturnType() == null || method.getReturnType() == void.class );
-			if ( name.startsWith( "get" ) && hasGetterSignature ) {
-				String propName = name.substring( 3 );
-				return data.get( propName );
-			}
-			else if ( name.startsWith( "is" ) && hasGetterSignature ) {
-				String propName = name.substring( 2 );
-				return data.get( propName );
-			}
-			else if ( name.startsWith( "set" ) && hasSetterSignature) {
-				String propName = name.substring( 3 );
-				data.put( propName, args[0] );
-				return null;
-			}
-			else {
-				// todo : what else to do here?
-				return null;
-			}
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java
deleted file mode 100644
index 1f0fa0b267..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java
+++ /dev/null
@@ -1,56 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.bytecode.cglib;
-import java.io.Serializable;
-import org.hibernate.bytecode.ReflectionOptimizer;
-
-/**
- * ReflectionOptimizer implementation for CGLIB.
- *
- * @author Steve Ebersole
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class ReflectionOptimizerImpl implements ReflectionOptimizer, Serializable {
-	private transient InstantiationOptimizerAdapter instantiationOptimizer;
-	private transient AccessOptimizerAdapter accessOptimizer;
-
-	public ReflectionOptimizerImpl(
-			InstantiationOptimizerAdapter instantiationOptimizer,
-	        AccessOptimizerAdapter accessOptimizer) {
-		this.instantiationOptimizer = instantiationOptimizer;
-		this.accessOptimizer = accessOptimizer;
-	}
-
-	public InstantiationOptimizer getInstantiationOptimizer() {
-		return instantiationOptimizer;
-	}
-
-	public AccessOptimizer getAccessOptimizer() {
-		return accessOptimizer;
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index d70c0c2988..945ce45fc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,810 +1,805 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.Timestamp;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Version;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.jboss.logging.Logger;
 
 
 /**
  * Provides access to configuration info passed in <tt>Properties</tt> objects.
  * <br><br>
  * Hibernate has two property scopes:
  * <ul>
  * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
  * instantiated. Each instance might have different property values. If no
  * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
  * <li><b>System-level</b> properties are shared by all factory instances and are always
  * determined by the <tt>Environment</tt> properties.
  * </ul>
  * The only system-level properties are
  * <ul>
  * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
  * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
  * </ul>
  * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
  * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
  * properties override properties specified in <tt>hibernate.properties</tt>.<br>
  * <br>
  * The <tt>SessionFactory</tt> is controlled by the following properties.
  * Properties may be either be <tt>System</tt> properties, properties
  * defined in a resource named <tt>/hibernate.properties</tt> or an instance of
  * <tt>java.util.Properties</tt> passed to
  * <tt>Configuration.buildSessionFactory()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.cache.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.cache.CacheProvider</tt>
  *   subclass (if not specified EHCache is used)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</tt>
  *   subclass (if not specified hueristics are used)</td>
  * </tr>
  * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
  * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
  * <tr>
  *   <td><tt>hibernate.connection.url</tt></td>
  *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.driver_class</tt></td>
  *   <td>classname of JDBC driver</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.isolation</tt></td>
  *   <td>JDBC transaction isolation level (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  *   <td><tt>hibernate.connection.pool_size</tt></td>
  *   <td>the maximum size of the connection pool (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.datasource</tt></td>
  *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.max_fetch_depth</tt></td>
  *   <td>maximum depth of outer join fetching</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.batch_size</tt></td>
  *   <td>enable use of JDBC2 batch API for drivers which support it</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
  *   <td>set the JDBC fetch size</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
  *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
  *   this property when using user supplied connections)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
  *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve
  *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
  *   <td>enable auto DDL export</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_schema</tt></td>
  *   <td>use given schema name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_catalog</tt></td>
  *   <td>use given catalog name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.session_factory_name</tt></td>
  *   <td>If set, the factory attempts to bind this name to itself in the
  *   JNDI context. This name is also used to support cross JVM <tt>
  *   Session</tt> (de)serialization.</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
  *   <td>classname of <tt>org.hibernate.transaction.TransactionManagerLookup</tt>
  *   implementor</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.factory_class</tt></td>
  *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
  *   (Defaults to <tt>JdbcTransactionFactory</tt>.)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
  * </tr>
  * </table>
  *
  * @see org.hibernate.SessionFactory
  * @author Gavin King
  */
 public final class Environment {
 	/**
 	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 	/**
 	 * JDBC driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 	/**
 	 * JDBC transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 	/**
 	 * JDBC URL
 	 */
 	public static final String URL ="hibernate.connection.url";
 	/**
 	 * JDBC user
 	 */
 	public static final String USER ="hibernate.connection.username";
 	/**
 	 * JDBC password
 	 */
 	public static final String PASS ="hibernate.connection.password";
 	/**
 	 * JDBC autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 	/**
 	 * Maximum number of inactive connections for Hibernate's connection pool
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 	/**
 	 * <tt>java.sql.Datasource</tt> JNDI name
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 	/**
 	 * prefix for arbitrary JDBC connection properties
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 	/**
 	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 	/**
 	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 	/**
 	 * JNDI name to bind to <tt>SessionFactory</tt>
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Hibernate SQL {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} classes to register with the
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}
 	 */
 	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	public static final String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	public static final String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 */
 	public static final String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	public static final String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 	/**
 	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
 	 */
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 */
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
 
 	/**
 	 * The {@link org.hibernate.cache.RegionFactory} implementation class
 	 */
 	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enable statistics collection
 	 */
 	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	public static final String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	public static final String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
     /**
      * The jacc context id of the deployment
      */
     public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
 	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Environment.class.getName());
 
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param props The specified properties.
 	 */
 	public static void verifyProperties(Properties props) {
 		Iterator iter = props.keySet().iterator();
 		Map propertiesToAdd = new HashMap();
 		while ( iter.hasNext() ) {
 			final Object propertyName = iter.next();
 			Object newPropertyName = OBSOLETE_PROPERTIES.get( propertyName );
             if (newPropertyName != null) LOG.unsupportedProperty(propertyName, newPropertyName);
 			newPropertyName = RENAMED_PROPERTIES.get( propertyName );
 			if ( newPropertyName != null ) {
                 LOG.renamedProperty(propertyName, newPropertyName);
 				if ( ! props.containsKey( newPropertyName ) ) {
 					propertiesToAdd.put( newPropertyName, props.get( propertyName ) );
 				}
 			}
 		}
 		props.putAll(propertiesToAdd);
 	}
 
 	static {
 
         LOG.version(Version.getVersionString());
 
-		RENAMED_PROPERTIES.put( "hibernate.cglib.use_reflection_optimizer", USE_REFLECTION_OPTIMIZER );
-
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_NONE), "NONE" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
 
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
 			try {
 				GLOBAL_PROPERTIES.load(stream);
                 LOG.propertiesLoaded(ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, PASS));
 			}
 			catch (Exception e) {
                 LOG.unableToloadProperties();
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
                     LOG.unableToCloseStreamError(ioe);
 				}
 			}
 		}
 		catch (HibernateException he) {
             LOG.propertiesNotFound();
 		}
 
 		try {
 			GLOBAL_PROPERTIES.putAll( System.getProperties() );
 		}
 		catch (SecurityException se) {
             LOG.unableToCopySystemProperties();
 		}
 
 		verifyProperties(GLOBAL_PROPERTIES);
 
 		ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
 
         if (ENABLE_BINARY_STREAMS) LOG.usingStreams();
         if (ENABLE_REFLECTION_OPTIMIZER) LOG.usingReflectionOptimizer();
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		boolean getGeneratedKeysSupport;
 		try {
 			Statement.class.getMethod("getGeneratedKeys", (Class[])null);
 			getGeneratedKeysSupport = true;
 		}
 		catch (NoSuchMethodException nsme) {
 			getGeneratedKeysSupport = false;
 		}
 		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
         if (!JVM_SUPPORTS_GET_GENERATED_KEYS) LOG.generatedKeysNotSupported();
 
 		boolean linkedHashSupport;
 		try {
 			Class.forName("java.util.LinkedHashSet");
 			linkedHashSupport = true;
 		}
 		catch (ClassNotFoundException cnfe) {
 			linkedHashSupport = false;
 		}
 		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
         if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) LOG.linkedMapsAndSetsNotSupported();
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
         if (JVM_HAS_TIMESTAMP_BUG) LOG.usingTimestampWorkaround();
 
 		Timestamp t = new Timestamp(0);
 		t.setNanos(5 * 1000000);
 		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
         if (JVM_HAS_JDK14_TIMESTAMP) LOG.usingJdk14TimestampHandling();
         else LOG.usingPreJdk14TimestampHandling();
 	}
 
 	public static BytecodeProvider getBytecodeProvider() {
 		return BYTECODE_PROVIDER_INSTANCE;
 	}
 
 	/**
 	 * Does this JVM's implementation of {@link java.sql.Timestamp} have a bug in which the following is true:<code>
 	 * new java.sql.Timestamp( x ).getTime() != x
 	 * </code>
 	 * <p/>
 	 * NOTE : IBM JDK 1.3.1 the only known JVM to exhibit this behavior.
 	 *
 	 * @return True if the JVM's {@link Timestamp} implementa
 	 */
 	public static boolean jvmHasTimestampBug() {
 		return JVM_HAS_TIMESTAMP_BUG;
 	}
 
 	/**
 	 * Does this JVM handle {@link java.sql.Timestamp} in the JDK 1.4 compliant way wrt to nano rolling>
 	 *
 	 * @return True if the JDK 1.4 (JDBC3) specification for {@link java.sql.Timestamp} nano rolling is adhered to.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmHasJDK14Timestamp() {
 		return JVM_HAS_JDK14_TIMESTAMP;
 	}
 
 	/**
 	 * Does this JVM support {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap} are available.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 * @see java.util.LinkedHashSet
 	 * @see java.util.LinkedHashMap
 	 */
 	@Deprecated
     public static boolean jvmSupportsLinkedHashCollections() {
 		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	}
 
 	/**
 	 * Does this JDK/JVM define the JDBC {@link Statement} interface with a 'getGeneratedKeys' method?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if generated keys can be retrieved via Statement; false otherwise.
 	 *
 	 * @see Statement
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmSupportsGetGeneratedKeys() {
 		return JVM_SUPPORTS_GET_GENERATED_KEYS;
 	}
 
 	/**
 	 * Should we use streams to bind binary types to JDBC IN parameters?
 	 *
 	 * @return True if streams should be used for binary data handling; false otherwise.
 	 *
 	 * @see #USE_STREAMS_FOR_BINARY
 	 */
 	public static boolean useStreamsForBinary() {
 		return ENABLE_BINARY_STREAMS;
 	}
 
 	/**
 	 * Should we use reflection optimization?
 	 *
 	 * @return True if reflection optimization should be used; false otherwise.
 	 *
 	 * @see #USE_REFLECTION_OPTIMIZER
 	 * @see #getBytecodeProvider()
 	 * @see BytecodeProvider#getReflectionOptimizer
 	 */
 	public static boolean useReflectionOptimizer() {
 		return ENABLE_REFLECTION_OPTIMIZER;
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private Environment() {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Return <tt>System</tt> properties, extended by any properties specified
 	 * in <tt>hibernate.properties</tt>.
 	 * @return Properties
 	 */
 	public static Properties getProperties() {
 		Properties copy = new Properties();
 		copy.putAll(GLOBAL_PROPERTIES);
 		return copy;
 	}
 
 	/**
 	 * Get the name of a JDBC transaction isolation level
 	 *
 	 * @see java.sql.Connection
 	 * @param isolation as defined by <tt>java.sql.Connection</tt>
 	 * @return a human-readable name
 	 */
 	public static String isolationLevelToString(int isolation) {
 		return (String) ISOLATION_LEVELS.get( new Integer(isolation) );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
         LOG.bytecodeProvider(provider);
 		return buildBytecodeProvider( provider );
 	}
 
 	private static BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
-		else if ( "cglib".equals( providerName ) ) {
-			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
-		}
 
-        LOG.unknownBytecodeProvider(providerName);
+        LOG.unknownBytecodeProvider( providerName );
 		return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index b1fc9cbd37..ab409ec5e4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,329 +1,326 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
 import org.jboss.logging.Logger;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
         LOG.autoFlush(enabledDisabled(flushBeforeCompletion));
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.autoSessionClose(enabledDisabled(autoCloseSession));
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
 		if (batchSize>0) LOG.jdbcBatchSize(batchSize);
 		settings.setJdbcBatchSize(batchSize);
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if (batchSize > 0) LOG.jdbcBatchUpdates(enabledDisabled(jdbcBatchVersionedData));
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
         LOG.scrollabelResultSets(enabledDisabled(useScrollableResultSets));
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.wrapResultSets(enabledDisabled(wrapResultSets));
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.jdbc3GeneratedKeys(enabledDisabled(useGetGeneratedKeys));
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) LOG.jdbcResultSetFetchSize(statementFetchSize);
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.connectionReleaseMode(releaseModeName);
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
 		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
         if (defaultSchema != null) LOG.defaultSchema(defaultSchema);
         if (defaultCatalog != null) LOG.defaultCatalog(defaultCatalog);
 		settings.setDefaultSchemaName(defaultSchema);
 		settings.setDefaultCatalogName(defaultCatalog);
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
         if (maxFetchDepth != null) LOG.maxOuterJoinFetchDepth(maxFetchDepth);
 		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.defaultBatchFetchSize(batchFetchSize);
 		settings.setDefaultBatchFetchSize(batchFetchSize);
 
 		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
         LOG.generateSqlWithComments(enabledDisabled(comments));
 		settings.setCommentsEnabled(comments);
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
         LOG.orderSqlUpdatesByPrimaryKey(enabledDisabled(orderUpdates));
 		settings.setOrderUpdatesEnabled(orderUpdates);
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.orderSqlInsertsForBatching(enabledDisabled(orderInserts));
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
         Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
         LOG.queryLanguageSubstitutions(querySubstitutions);
 		settings.setQuerySubstitutions(querySubstitutions);
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
         LOG.jpaQlStrictCompliance(enabledDisabled(jpaqlCompliance));
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
         LOG.secondLevelCache(enabledDisabled(useSecondLevelCache));
 		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.queryCache(enabledDisabled(useQueryCache));
 		settings.setQueryCacheEnabled(useQueryCache);
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.optimizeCacheForMinimalInputs(enabledDisabled(useMinimalPuts));
 		settings.setMinimalPutsEnabled(useMinimalPuts);
 
 		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
 		if ( StringHelper.isEmpty(prefix) ) prefix=null;
         if (prefix != null) LOG.cacheRegionPrefix(prefix);
 		settings.setCacheRegionPrefix(prefix);
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
         LOG.structuredSecondLevelCacheEntries(enabledDisabled(useStructuredCacheEntries));
 		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
 
 		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
 		LOG.statistics( enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled(useStatistics);
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
         LOG.deletedEntitySyntheticIdentifierRollback(enabledDisabled(useIdentifierRollback));
 		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
 		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
 		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
 		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
 		if ( "create-drop".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema(true);
 			settings.setAutoDropSchema(true);
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.defaultEntityMode(defaultEntityMode);
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.namedQueryChecking(enabledDisabled(namedQueryChecking));
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.checkNullability(enabledDisabled(checkNullability));
 		settings.setCheckNullability(checkNullability);
 
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
-	protected BytecodeProvider buildBytecodeProvider(String providerName) {
-		if ( "javassist".equals( providerName ) ) {
-			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
-		}
-		else if ( "cglib".equals( providerName ) ) {
-			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
-		}
-		else {
-            LOG.debugf("Using javassist as bytecode provider by default");
-			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
-		}
-	}
+//	protected BytecodeProvider buildBytecodeProvider(String providerName) {
+//		if ( "javassist".equals( providerName ) ) {
+//			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+//		}
+//		else {
+//            LOG.debugf("Using javassist as bytecode provider by default");
+//			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+//		}
+//	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
         LOG.queryCacheFactory(queryCacheFactoryClassName);
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.cacheRegionFactory(regionFactoryClassName);
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException nsme ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.constructorWithPropertiesNotFound(regionFactoryClassName);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
         LOG.queryTranslator(className);
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
index 020bf1b289..a23192c450 100644
--- a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
@@ -1,115 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  *
  */
 package org.hibernate.intercept;
 import java.util.Set;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.cglib.CGLIBHelper;
 import org.hibernate.intercept.javassist.JavassistHelper;
 
 /**
  * Helper class for dealing with enhanced entity classes.
  *
  * @author Steve Ebersole
  */
 public class FieldInterceptionHelper {
 
 	// VERY IMPORTANT!!!! - This class needs to be free of any static references
 	// to any CGLIB or Javassist classes.  Otherwise, users will always need both
 	// on their classpaths no matter which (if either) they use.
 	//
 	// Another option here would be to remove the Hibernate.isPropertyInitialized()
 	// method and have the users go through the SessionFactory to get this information.
 
 	private FieldInterceptionHelper() {
 	}
 
 	public static boolean isInstrumented(Class entityClass) {
 		Class[] definedInterfaces = entityClass.getInterfaces();
 		for ( int i = 0; i < definedInterfaces.length; i++ ) {
 			if ( "net.sf.cglib.transform.impl.InterceptFieldEnabled".equals( definedInterfaces[i].getName() )
 			     || "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public static boolean isInstrumented(Object entity) {
 		return entity != null && isInstrumented( entity.getClass() );
 	}
 
 	public static FieldInterceptor extractFieldInterceptor(Object entity) {
 		if ( entity == null ) {
 			return null;
 		}
 		Class[] definedInterfaces = entity.getClass().getInterfaces();
 		for ( int i = 0; i < definedInterfaces.length; i++ ) {
-			if ( "net.sf.cglib.transform.impl.InterceptFieldEnabled".equals( definedInterfaces[i].getName() ) ) {
-				// we have a CGLIB enhanced entity
-				return CGLIBHelper.extractFieldInterceptor( entity );
-			}
-			else if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
+			if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
 				// we have a Javassist enhanced entity
 				return JavassistHelper.extractFieldInterceptor( entity );
 			}
 		}
 		return null;
 	}
 
 	public static FieldInterceptor injectFieldInterceptor(
 			Object entity,
 	        String entityName,
 	        Set uninitializedFieldNames,
 	        SessionImplementor session) {
 		if ( entity != null ) {
 			Class[] definedInterfaces = entity.getClass().getInterfaces();
 			for ( int i = 0; i < definedInterfaces.length; i++ ) {
-				if ( "net.sf.cglib.transform.impl.InterceptFieldEnabled".equals( definedInterfaces[i].getName() ) ) {
-					// we have a CGLIB enhanced entity
-					return CGLIBHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
-				}
-				else if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
+				if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
 					// we have a Javassist enhanced entity
 					return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
 				}
 			}
 		}
 		return null;
 	}
 
 	public static void clearDirty(Object entity) {
 		FieldInterceptor interceptor = extractFieldInterceptor( entity );
 		if ( interceptor != null ) {
 			interceptor.clearDirty();
 		}
 	}
 
 	public static void markDirty(Object entity) {
 		FieldInterceptor interceptor = extractFieldInterceptor( entity );
 		if ( interceptor != null ) {
 			interceptor.dirty();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java
deleted file mode 100644
index 4c44b11a2e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java
+++ /dev/null
@@ -1,54 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.intercept.cglib;
-import java.util.Set;
-import net.sf.cglib.transform.impl.InterceptFieldEnabled;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.FieldInterceptor;
-
-/**
- * @author Steve Ebersole
- */
-public class CGLIBHelper {
-	private CGLIBHelper() {
-	}
-
-	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-		return ( FieldInterceptor ) ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
-	}
-
-	public static FieldInterceptor injectFieldInterceptor(
-			Object entity,
-	        String entityName,
-	        Set uninitializedFieldNames,
-	        SessionImplementor session) {
-		FieldInterceptorImpl fieldInterceptor = new FieldInterceptorImpl(
-				session, uninitializedFieldNames, entityName
-		);
-		( ( InterceptFieldEnabled ) entity ).setInterceptFieldCallback( fieldInterceptor );
-		return fieldInterceptor;
-
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java
deleted file mode 100644
index d640f24fcd..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java
+++ /dev/null
@@ -1,167 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.intercept.cglib;
-import java.io.Serializable;
-import java.util.Set;
-import net.sf.cglib.transform.impl.InterceptFieldCallback;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.AbstractFieldInterceptor;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.LazyInitializer;
-
-/**
- * A field-level interceptor that initializes lazily fetched properties.
- * This interceptor can be attached to classes instrumented by CGLIB.
- * Note that this implementation assumes that the instance variable
- * name is the same as the name of the persistent property that must
- * be loaded.
- *
- * @author Gavin King
- */
-public final class FieldInterceptorImpl extends AbstractFieldInterceptor implements InterceptFieldCallback, Serializable {
-
-	/**
-	 * Package-protected constructor
-	 *
-	 * @param session The Hibernate session
-	 * @param uninitializedFields Names of the fields we need to initialize on load
-	 * @param entityName The entity name to which we are being bound
-	 */
-	FieldInterceptorImpl(SessionImplementor session, Set uninitializedFields, String entityName) {
-		super( session, uninitializedFields, entityName );
-	}
-
-	public boolean readBoolean(Object target, String name, boolean oldValue) {
-		return ( ( Boolean ) intercept( target, name, oldValue  ? Boolean.TRUE : Boolean.FALSE ) )
-				.booleanValue();
-	}
-
-	public byte readByte(Object target, String name, byte oldValue) {
-		return ( ( Byte ) intercept( target, name, new Byte( oldValue ) ) ).byteValue();
-	}
-
-	public char readChar(Object target, String name, char oldValue) {
-		return ( ( Character ) intercept( target, name, new Character( oldValue ) ) )
-				.charValue();
-	}
-
-	public double readDouble(Object target, String name, double oldValue) {
-		return ( ( Double ) intercept( target, name, new Double( oldValue ) ) )
-				.doubleValue();
-	}
-
-	public float readFloat(Object target, String name, float oldValue) {
-		return ( ( Float ) intercept( target, name, new Float( oldValue ) ) )
-				.floatValue();
-	}
-
-	public int readInt(Object target, String name, int oldValue) {
-		return ( ( Integer ) intercept( target, name, new Integer( oldValue ) ) )
-				.intValue();
-	}
-
-	public long readLong(Object target, String name, long oldValue) {
-		return ( ( Long ) intercept( target, name, new Long( oldValue ) ) ).longValue();
-	}
-
-	public short readShort(Object target, String name, short oldValue) {
-		return ( ( Short ) intercept( target, name, new Short( oldValue ) ) )
-				.shortValue();
-	}
-
-	public Object readObject(Object target, String name, Object oldValue) {
-		Object value = intercept( target, name, oldValue );
-		if (value instanceof HibernateProxy) {
-			LazyInitializer li = ( (HibernateProxy) value ).getHibernateLazyInitializer();
-			if ( li.isUnwrap() ) {
-				value = li.getImplementation();
-			}
-		}
-		return value;
-	}
-
-	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {
-		dirty();
-		intercept( target, name, oldValue ? Boolean.TRUE : Boolean.FALSE );
-		return newValue;
-	}
-
-	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
-		dirty();
-		intercept( target, name, new Byte( oldValue ) );
-		return newValue;
-	}
-
-	public char writeChar(Object target, String name, char oldValue, char newValue) {
-		dirty();
-		intercept( target, name, new Character( oldValue ) );
-		return newValue;
-	}
-
-	public double writeDouble(Object target, String name, double oldValue, double newValue) {
-		dirty();
-		intercept( target, name, new Double( oldValue ) );
-		return newValue;
-	}
-
-	public float writeFloat(Object target, String name, float oldValue, float newValue) {
-		dirty();
-		intercept( target, name, new Float( oldValue ) );
-		return newValue;
-	}
-
-	public int writeInt(Object target, String name, int oldValue, int newValue) {
-		dirty();
-		intercept( target, name, new Integer( oldValue ) );
-		return newValue;
-	}
-
-	public long writeLong(Object target, String name, long oldValue, long newValue) {
-		dirty();
-		intercept( target, name, new Long( oldValue ) );
-		return newValue;
-	}
-
-	public short writeShort(Object target, String name, short oldValue, short newValue) {
-		dirty();
-		intercept( target, name, new Short( oldValue ) );
-		return newValue;
-	}
-
-	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-
-	public String toString() {
-		return "FieldInterceptorImpl(" +
-			"entityName=" + getEntityName() +
-			",dirty=" + isDirty() +
-			",uninitializedFields=" + getUninitializedFields() +
-			')';
-	}
-
-}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
deleted file mode 100644
index e4cb891de4..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
+++ /dev/null
@@ -1,237 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.proxy.pojo.cglib;
-
-import java.io.Serializable;
-import java.lang.reflect.InvocationTargetException;
-import java.lang.reflect.Method;
-import net.sf.cglib.proxy.Callback;
-import net.sf.cglib.proxy.CallbackFilter;
-import net.sf.cglib.proxy.Enhancer;
-import net.sf.cglib.proxy.InvocationHandler;
-import net.sf.cglib.proxy.NoOp;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.LazyInitializationException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.pojo.BasicLazyInitializer;
-import org.hibernate.type.CompositeType;
-import org.jboss.logging.Logger;
-
-/**
- * A <tt>LazyInitializer</tt> implemented using the CGLIB bytecode generation library
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public final class CGLIBLazyInitializer extends BasicLazyInitializer implements InvocationHandler {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, CGLIBLazyInitializer.class.getName());
-
-	private static final CallbackFilter FINALIZE_FILTER = new CallbackFilter() {
-		public int accept(Method method) {
-			if ( method.getParameterTypes().length == 0 && method.getName().equals("finalize") ){
-				return 1;
-			}
-			else {
-				return 0;
-			}
-		}
-	};
-
-	private Class[] interfaces;
-	private boolean constructed = false;
-
-	static HibernateProxy getProxy(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			CompositeType componentIdType,
-			final Serializable id,
-			final SessionImplementor session) throws HibernateException {
-		// note: interfaces is assumed to already contain HibernateProxy.class
-
-		try {
-			final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
-					entityName,
-					persistentClass,
-					interfaces,
-					id,
-					getIdentifierMethod,
-					setIdentifierMethod,
-					componentIdType,
-					session
-				);
-
-			final HibernateProxy proxy;
-			Class factory = getProxyFactory(persistentClass,  interfaces);
-			proxy = getProxyInstance(factory, instance);
-			instance.constructed = true;
-			return proxy;
-		}
-		catch (Throwable t) {
-            LOG.error(LOG.cglibEnhancementFailed(entityName), t);
-            throw new HibernateException(LOG.cglibEnhancementFailed(entityName), t);
-		}
-	}
-
-	public static HibernateProxy getProxy(
-			final Class factory,
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			final CompositeType componentIdType,
-			final Serializable id,
-			final SessionImplementor session) throws HibernateException {
-		final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
-				entityName,
-				persistentClass,
-				interfaces,
-				id,
-				getIdentifierMethod,
-				setIdentifierMethod,
-				componentIdType,
-				session
-			);
-
-		final HibernateProxy proxy;
-		try {
-			proxy = getProxyInstance(factory, instance);
-		}
-		catch (Exception e) {
-			throw new HibernateException( "CGLIB Enhancement failed: " + persistentClass.getName(), e );
-		}
-		instance.constructed = true;
-
-		return proxy;
-	}
-
-    private static HibernateProxy getProxyInstance(Class factory, CGLIBLazyInitializer instance) throws InstantiationException, IllegalAccessException {
-		HibernateProxy proxy;
-		try {
-			Enhancer.registerCallbacks(factory, new Callback[]{ instance, null });
-			proxy = (HibernateProxy)factory.newInstance();
-		} finally {
-			// HHH-2481 make sure the callback gets cleared, otherwise the instance stays in a static thread local.
-			Enhancer.registerCallbacks(factory, null);
-		}
-		return proxy;
-	}
-
-	public static Class getProxyFactory(Class persistentClass, Class[] interfaces)
-			throws HibernateException {
-		Enhancer e = new Enhancer();
-		e.setSuperclass( interfaces.length == 1 ? persistentClass : null );
-		e.setInterfaces(interfaces);
-		e.setCallbackTypes(new Class[]{
-			InvocationHandler.class,
-			NoOp.class,
-	  		});
-  		e.setCallbackFilter(FINALIZE_FILTER);
-  		e.setUseFactory(false);
-		e.setInterceptDuringConstruction( false );
-		return e.createClass();
-	}
-
-	private CGLIBLazyInitializer(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Serializable id,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			final CompositeType componentIdType,
-			final SessionImplementor session) {
-		super(
-				entityName,
-				persistentClass,
-				id,
-				getIdentifierMethod,
-				setIdentifierMethod,
-				componentIdType,
-				session
-			);
-		this.interfaces = interfaces;
-	}
-
-	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
-		if ( constructed ) {
-			Object result = invoke( method, args, proxy );
-			if ( result == INVOKE_IMPLEMENTATION ) {
-				Object target = getImplementation();
-				try {
-					final Object returnValue;
-					if ( ReflectHelper.isPublic( persistentClass, method ) ) {
-						if ( !method.getDeclaringClass().isInstance( target ) ) {
-							throw new ClassCastException( target.getClass().getName() );
-						}
-						returnValue = method.invoke( target, args );
-					}
-					else {
-						if ( !method.isAccessible() ) {
-							method.setAccessible( true );
-						}
-						returnValue = method.invoke( target, args );
-					}
-					return returnValue == target ? proxy : returnValue;
-				}
-				catch ( InvocationTargetException ite ) {
-					throw ite.getTargetException();
-				}
-			}
-			else {
-				return result;
-			}
-		}
-		else {
-			// while constructor is running
-			if ( method.getName().equals( "getHibernateLazyInitializer" ) ) {
-				return this;
-			}
-			else {
-				throw new LazyInitializationException( "unexpected case hit, method=" + method.getName() );
-			}
-		}
-	}
-
-	@Override
-    protected Object serializableProxy() {
-		return new SerializableProxy(
-				getEntityName(),
-				persistentClass,
-				interfaces,
-				getIdentifier(),
-				( isReadOnlySettingAvailable() ? Boolean.valueOf( isReadOnly() ) : isReadOnlyBeforeAttachedToSession() ),
-				getIdentifierMethod,
-				setIdentifierMethod,
-				componentIdType
-			);
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java
deleted file mode 100644
index ea5c2c6111..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java
+++ /dev/null
@@ -1,85 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.proxy.pojo.cglib;
-import java.io.Serializable;
-import java.lang.reflect.Method;
-import java.util.Set;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.type.CompositeType;
-
-/**
- * @author Gavin King
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public class CGLIBProxyFactory implements ProxyFactory {
-
-	protected static final Class[] NO_CLASSES = new Class[0];
-
-	private Class persistentClass;
-	private String entityName;
-	private Class[] interfaces;
-	private Method getIdentifierMethod;
-	private Method setIdentifierMethod;
-	private CompositeType componentIdType;
-	private Class factory;
-
-	public void postInstantiate(
-		final String entityName,
-		final Class persistentClass,
-		final Set interfaces,
-		final Method getIdentifierMethod,
-		final Method setIdentifierMethod,
-		CompositeType componentIdType)
-	throws HibernateException {
-		this.entityName = entityName;
-		this.persistentClass = persistentClass;
-		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
-		this.getIdentifierMethod = getIdentifierMethod;
-		this.setIdentifierMethod = setIdentifierMethod;
-		this.componentIdType = componentIdType;
-		factory = CGLIBLazyInitializer.getProxyFactory(persistentClass, this.interfaces);
-	}
-
-	public HibernateProxy getProxy(Serializable id, SessionImplementor session)
-		throws HibernateException {
-
-		return CGLIBLazyInitializer.getProxy(
-				factory, 
-				entityName, 
-				persistentClass, 
-				interfaces, 
-				getIdentifierMethod, 
-				setIdentifierMethod,
-				componentIdType,
-				id, 
-				session
-			);
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java
deleted file mode 100644
index ed358af133..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java
+++ /dev/null
@@ -1,101 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.proxy.pojo.cglib;
-import java.io.Serializable;
-import java.lang.reflect.Method;
-import org.hibernate.HibernateException;
-import org.hibernate.proxy.AbstractSerializableProxy;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.type.CompositeType;
-
-/**
- * Serializable placeholder for <tt>CGLIB</tt> proxies
- *
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
- */
-@Deprecated
-public final class SerializableProxy extends AbstractSerializableProxy {
-
-	private Class persistentClass;
-	private Class[] interfaces;
-	private Class getIdentifierMethodClass;
-	private Class setIdentifierMethodClass;
-	private String getIdentifierMethodName;
-	private String setIdentifierMethodName;
-	private Class[] setIdentifierMethodParams;
-	private CompositeType componentIdType;
-
-	public SerializableProxy() {
-	}
-
-	public SerializableProxy(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Serializable id,
-			final Boolean readOnly,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			CompositeType componentIdType) {
-		super( entityName, id, readOnly );
-		this.persistentClass = persistentClass;
-		this.interfaces = interfaces;
-		if (getIdentifierMethod!=null) {
-			getIdentifierMethodClass = getIdentifierMethod.getDeclaringClass();
-			getIdentifierMethodName = getIdentifierMethod.getName();
-		}
-		if (setIdentifierMethod!=null) {
-			setIdentifierMethodClass = setIdentifierMethod.getDeclaringClass();
-			setIdentifierMethodName = setIdentifierMethod.getName();
-			setIdentifierMethodParams = setIdentifierMethod.getParameterTypes();
-		}
-		this.componentIdType = componentIdType;
-	}
-
-	private Object readResolve() {
-		try {
-			HibernateProxy proxy = CGLIBLazyInitializer.getProxy(
-					getEntityName(),
-					persistentClass,
-					interfaces,
-					getIdentifierMethodName==null
-							? null
-							: getIdentifierMethodClass.getDeclaredMethod( getIdentifierMethodName, (Class[]) null ),
-					setIdentifierMethodName==null
-							? null
-							: setIdentifierMethodClass.getDeclaredMethod(setIdentifierMethodName, setIdentifierMethodParams),
-					componentIdType,
-					getId(),
-					null
-			);
-
-			setReadOnlyBeforeAttachedToSession( ( CGLIBLazyInitializer ) proxy.getHibernateLazyInitializer() );
-			return proxy;
-		}
-		catch (NoSuchMethodException nsme) {
-			throw new HibernateException("could not create proxy for entity: " + getEntityName(), nsme);
-		}
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java
deleted file mode 100644
index db367b5470..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java
+++ /dev/null
@@ -1,79 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.tool.instrument.cglib;
-
-import org.hibernate.bytecode.buildtime.CGLIBInstrumenter;
-import org.hibernate.bytecode.buildtime.Instrumenter;
-import org.hibernate.bytecode.buildtime.Logger;
-import org.hibernate.tool.instrument.BasicInstrumentationTask;
-
-/**
- * An Ant task for instrumenting persistent classes in order to enable
- * field-level interception using CGLIB.
- * <p/>
- * In order to use this task, typically you would define a a taskdef
- * similar to:<pre>
- * <taskdef name="instrument" classname="org.hibernate.tool.instrument.cglib.InstrumentTask">
- *     <classpath refid="lib.class.path"/>
- * </taskdef>
- * </pre>
- * where <tt>lib.class.path</tt> is an ANT path reference containing all the
- * required Hibernate and CGLIB libraries.
- * <p/>
- * And then use it like:<pre>
- * <instrument>
- *     <fileset dir="${testclasses.dir}/org/hibernate/test">
- *         <include name="yadda/yadda/**"/>
- *         ...
- *     </fileset>
- * </instrument>
- * </pre>
- * where the nested ANT fileset includes the class you would like to have
- * instrumented.
- * <p/>
- * Optionally you can chose to enable "Extended Instrumentation" if desired
- * by specifying the extended attribute on the task:<pre>
- * <instrument extended="true">
- *     ...
- * </instrument>
- * </pre>
- * See the Hibernate manual regarding this option.
- *
- * @author Gavin King
- * @author Steve Ebersole
- * 
- * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated; use
- * {@link org.hibernate.tool.instrument.javassist.InstrumentTask} instead
- */
-@Deprecated
-public class InstrumentTask extends BasicInstrumentationTask {
-	public InstrumentTask() {
-		System.err.println( "Per HHH-5451 support for cglib as a bytecode provider has been deprecated." );
-	}
-
-	@Override
-    protected Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options) {
-		return new CGLIBInstrumenter( logger, options );
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/CGLIBThreadLocalTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/CGLIBThreadLocalTest.java
deleted file mode 100644
index e189e56f03..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/CGLIBThreadLocalTest.java
+++ /dev/null
@@ -1,102 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.test.bytecode.cglib;
-import java.lang.reflect.Field;
-
-import org.hibernate.Session;
-import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
-import org.hibernate.cfg.Environment;
-import org.hibernate.proxy.HibernateProxy;
-
-import org.junit.Test;
-
-import org.hibernate.testing.Skip;
-import org.hibernate.testing.TestForIssue;
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-import org.hibernate.test.bytecode.ProxyBean;
-
-import static org.junit.Assert.assertTrue;
-import static org.junit.Assert.fail;
-
-/**
- * Test that the static thread local callback object is cleared out of the proxy class after instantiated.
- * This tests that the memory leak reported by HHH-2481 hasn't been re-introduced.
- *
- * @author Paul Malolepsy
- */
-@TestForIssue(jiraKey = "HHH-2481")
-public class CGLIBThreadLocalTest extends BaseCoreFunctionalTestCase {
-	public String[] getMappings() {
-		return new String[] {"bytecode/Bean.hbm.xml"};
-	}
-
-	public static class LocalSkipCheck implements Skip.Matcher {
-		@Override
-		public boolean isMatch() {
-			return !BytecodeProviderImpl.class.isInstance( Environment.getBytecodeProvider() );
-		}
-	}
-
-	@Test
-	@Skip(
-			condition = LocalSkipCheck.class,
-			message = "Environment not configured for CGLIB bytecode provider"
-	)
-	public void testCglibClearing() {
-		//create the object for the test
-		Session s = openSession();
-		s.beginTransaction();
-		ProxyBean proxyBean = new ProxyBean();
-		proxyBean.setSomeString( "my-bean" );
-		proxyBean.setSomeLong( 1234 );
-		s.save( proxyBean );
-		s.getTransaction().commit();
-		s.close();
-
-		// read the object as a proxy
-		s = openSession();
-		s.beginTransaction();
-		proxyBean = (ProxyBean) s.load( ProxyBean.class, proxyBean.getSomeString() );
-		assertTrue( proxyBean instanceof HibernateProxy );
-		try {
-			//check that the static thread callbacks thread local has been cleared out
-			Field field = proxyBean.getClass().getDeclaredField( "CGLIB$THREAD_CALLBACKS" );
-			field.setAccessible( true );
-			ThreadLocal threadCallbacksThreadLocal = (ThreadLocal) field.get( null );
-			assertTrue( threadCallbacksThreadLocal.get() == null );
-		}
-		catch (NoSuchFieldException e1) {
-			fail( "unable to find CGLIB$THREAD_CALLBACKS field in proxy." );
-		}
-		catch (Throwable t) {
-			fail( "unexpected exception type : " + t );
-		}
-		finally {
-			//clean up
-			s.delete( proxyBean );
-			s.getTransaction().commit();
-			s.close();
-		}
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/InvocationTargetExceptionTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/InvocationTargetExceptionTest.java
deleted file mode 100644
index d34203d8f2..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/InvocationTargetExceptionTest.java
+++ /dev/null
@@ -1,93 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.test.bytecode.cglib;
-
-import java.text.ParseException;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
-import org.hibernate.cfg.Environment;
-
-import org.junit.Test;
-
-import org.hibernate.testing.Skip;
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-import org.hibernate.test.bytecode.Bean;
-
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.fail;
-
-/**
- * Test that the Javassist-based lazy initializer properly handles InvocationTargetExceptions
- *
- * @author Steve Ebersole
- */
-@Skip(
-		condition = InvocationTargetExceptionTest.LocalSkipMatcher.class,
-		message = "environment not configured for cglib bytecode provider"
-)
-public class InvocationTargetExceptionTest extends BaseCoreFunctionalTestCase {
-	public static class LocalSkipMatcher implements Skip.Matcher {
-		@Override
-		public boolean isMatch() {
-			return ! BytecodeProviderImpl.class.isInstance( Environment.getBytecodeProvider() );
-		}
-	}
-
-	@Override
-	public String[] getMappings() {
-		return new String[] { "bytecode/Bean.hbm.xml" };
-	}
-
-	@Test
-	public void testProxiedInvocationException() {
-		Session s = openSession();
-		s.beginTransaction();
-		Bean bean = new Bean();
-		bean.setSomeString( "my-bean" );
-		s.save( bean );
-		s.getTransaction().commit();
-		s.close();
-
-		s = openSession();
-		s.beginTransaction();
-		bean = ( Bean ) s.load( Bean.class, bean.getSomeString() );
-		assertFalse( Hibernate.isInitialized( bean ) );
-		try {
-			bean.throwException();
-			fail( "exception not thrown" );
-		}
-		catch ( ParseException e ) {
-			// expected behavior
-		}
-		catch( Throwable t ) {
-			fail( "unexpected exception type : " + t );
-		}
-
-		s.delete( bean );
-		s.getTransaction().commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/ReflectionOptimizerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/ReflectionOptimizerTest.java
deleted file mode 100644
index 3eae32b076..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/cglib/ReflectionOptimizerTest.java
+++ /dev/null
@@ -1,71 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.test.bytecode.cglib;
-
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-import org.hibernate.test.bytecode.Bean;
-import org.hibernate.test.bytecode.BeanReflectionHelper;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertNotNull;
-
-/**
- * @author Steve Ebersole
- */
-public class ReflectionOptimizerTest extends BaseUnitTestCase {
-	@Test
-	public void testReflectionOptimization() {
-		BytecodeProviderImpl provider = new BytecodeProviderImpl();
-		ReflectionOptimizer optimizer = provider.getReflectionOptimizer(
-				Bean.class,
-		        BeanReflectionHelper.getGetterNames(),
-		        BeanReflectionHelper.getSetterNames(),
-		        BeanReflectionHelper.getTypes()
-		);
-		assertNotNull( optimizer );
-		assertNotNull( optimizer.getInstantiationOptimizer() );
-		assertNotNull( optimizer.getAccessOptimizer() );
-
-		Object instance = optimizer.getInstantiationOptimizer().newInstance();
-		assertEquals( instance.getClass(), Bean.class );
-		Bean bean = ( Bean ) instance;
-
-		optimizer.getAccessOptimizer().setPropertyValues( bean, BeanReflectionHelper.TEST_VALUES );
-		assertEquals( bean.getSomeString(), BeanReflectionHelper.TEST_VALUES[0] );
-		Object[] values = optimizer.getAccessOptimizer().getPropertyValues( bean );
-		assertEquivalent( values, BeanReflectionHelper.TEST_VALUES );
-	}
-
-	private void assertEquivalent(Object[] checkValues, Object[] values) {
-		assertEquals( "Different lengths", checkValues.length, values.length );
-		for ( int i = 0; i < checkValues.length; i++ ) {
-			assertEquals( "different values at index [" + i + "]", checkValues[i], values[i] );
-		}
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/CGLIBInstrumentationTest.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/CGLIBInstrumentationTest.java
deleted file mode 100644
index 4938684e44..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/CGLIBInstrumentationTest.java
+++ /dev/null
@@ -1,36 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.test.instrument.runtime;
-
-import org.hibernate.bytecode.BytecodeProvider;
-import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
-
-/**
- * @author Steve Ebersole
- */
-public class CGLIBInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
-	protected BytecodeProvider buildBytecodeProvider() {
-		return new BytecodeProviderImpl();
-	}
-}
