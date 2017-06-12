diff --git a/build.gradle b/build.gradle
index 635607a90a..dcedcdd7db 100644
--- a/build.gradle
+++ b/build.gradle
@@ -1,407 +1,421 @@
 apply plugin: 'eclipse'
 apply plugin: 'idea'
 apply from: "./libraries.gradle"
 
 allprojects {
     repositories {
         mavenCentral()
         mavenLocal()
 
         mavenRepo name: 'jboss-nexus', url: "http://repository.jboss.org/nexus/content/groups/public/"
         mavenRepo name: "jboss-snapshots", url: "http://snapshots.jboss.org/maven2/"
     }
 }
 
 buildscript {
     repositories {
         mavenCentral()
         mavenLocal()
 
         mavenRepo name: 'jboss-nexus', url: "http://repository.jboss.org/nexus/content/groups/public/"
         mavenRepo name: "jboss-snapshots", url: "http://snapshots.jboss.org/maven2/"
     }
     dependencies {
         classpath 'org.hibernate.build.gradle:gradle-maven-publish-auth:2.0.1'
     }
 }
 
 ext.hibernateTargetVersion = '4.3.0-SNAPSHOT'
 ext.javaLanguageLevel = "1.6"
 
 task wrapper(type: Wrapper) {
     gradleVersion = '1.5'
 }
 
 
 idea {
     project {
         languageLevel = javaLanguageLevel
         ipr {
             withXml { provider ->
                 provider.node.component.find { it.@name == 'VcsDirectoryMappings' }.mapping.@vcs = 'Git'
                 def maxHeapSizeConfig =  provider.node.component.find { it.@name == 'JavacSettings' }
                 if( maxHeapSizeConfig == null ){
                     def javacSettingsNode =  provider.node.appendNode('component',[name: 'JavacSettings'])
                     javacSettingsNode.appendNode('option', [name:"MAXIMUM_HEAP_SIZE", value:"512"])
                 }
             }
             beforeMerged { project ->
                 project.modulePaths.clear()
             }
         }
     }
     module {
         name = "hibernate-orm"
     }
 }
 
 // Used in POM customization.  Each sub-project overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 def pomName() {
     return "A Hibernate O/RM module"
 }
 def pomDescription() {
     return "A module of the Hibernate O/RM project"
 }
 // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 subprojects { subProject ->
     apply plugin: 'idea'
     apply plugin: 'eclipse'
 
     defaultTasks 'build'
 
     group = 'org.hibernate'
     version = rootProject.hibernateTargetVersion
 
     // minimize changes, at least for now (gradle uses 'build' by default)..
     buildDir = "target"
     if ( ! subProject.name.startsWith( 'release' )  && ! subProject.name.startsWith( 'documentation' ) ) {
         apply plugin: 'java'
         apply plugin: 'maven-publish'
         apply plugin: 'maven-publish-auth'
         apply plugin: 'osgi'
 
         apply from: "../utilities.gradle"
 
         apply plugin: 'findbugs'
         apply plugin: 'checkstyle'
         apply plugin: 'build-dashboard'
         apply plugin: 'project-report'
 
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
             jaxb {
                 description = 'Dependencies for running ant xjc (jaxb class generation)'
             }
             configurations {
                 all*.exclude group: 'xml-apis', module: 'xml-apis'
             }
         }
 
         // appropriately inject the common dependencies into each sub-project
         dependencies {
             compile( libraries.logging )
 
             testCompile( libraries.junit )
             testCompile( libraries.byteman )
             testCompile( libraries.byteman_install )
             testCompile( libraries.byteman_bmunit )
             
             testRuntime( libraries.slf4j_api )
             testRuntime( libraries.slf4j_log4j12 )
             testRuntime( libraries.jcl_slf4j )
             testRuntime( libraries.jcl_api )
             testRuntime( libraries.jcl )
             testRuntime( libraries.javassist )
             testRuntime( libraries.h2 )
 
             jbossLoggingTool( libraries.logging_processor )
 
             hibernateJpaModelGenTool( libraries.jpa_modelgen )
 
             jaxb( libraries.jaxb ){
                 exclude group: "javax.xml.stream"
             }
             jaxb( libraries.jaxb2_basics )
             jaxb( libraries.jaxb2_ant )
         }
 
         // mac-specific stuff ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         ext.toolsJar = file("${System.getProperty('java.home')}/../lib/tools.jar")
         if ( ext.toolsJar.exists() ) {
             dependencies{
                 testCompile files( toolsJar )
             }
         }
         // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
         targetCompatibility = rootProject.javaLanguageLevel
         sourceCompatibility = rootProject.javaLanguageLevel
 
         task compile
         compile.dependsOn compileJava, processResources, compileTestJava, processTestResources
 
         sourceSets.main {
             compileClasspath += configurations.provided
         }
 
         sourceSets.all {
             ext.originalJavaSrcDirs = java.srcDirs
             ext.generatedLoggingSrcDir = file( "${buildDir}/generated-src/logging/${name}" )
             java.srcDir generatedLoggingSrcDir
         }
 
         task generateMainLoggingClasses(type: JavaCompile) {
             ext.aptDumpDir = subProject.file( "${buildDir}/tmp/apt/logging" )
             classpath = compileJava.classpath + configurations.jbossLoggingTool
             source = sourceSets.main.originalJavaSrcDirs
             destinationDir = aptDumpDir
             options.define(
                     compilerArgs: [
                             "-nowarn",
                             "-proc:only",
                             "-encoding", "UTF-8",
                             "-processor", "org.jboss.logging.processor.apt.LoggingToolsProcessor",
                             "-s", "$sourceSets.main.generatedLoggingSrcDir.absolutePath",
                             "-AloggingVersion=3.0",
                             "-source", rootProject.javaLanguageLevel,
                             "-target", rootProject.javaLanguageLevel,
                             "-AtranslationFilesPath=${project.rootDir}/src/main/resources"
 
                     ]
             );
             outputs.dir sourceSets.main.generatedLoggingSrcDir;
             doFirst {
 //                source = sourceSets.main.originalJavaSrcDirs
                 sourceSets.main.generatedLoggingSrcDir.mkdirs()
             }
             doLast {
                 aptDumpDir.delete()
             }
         }
 
         // for the time being eat the annoying output from running the annotation processors
         generateMainLoggingClasses.logging.captureStandardError(LogLevel.INFO)
 
         task generateSources( type: Task )
         generateSources.dependsOn generateMainLoggingClasses
 
         compileJava.dependsOn generateMainLoggingClasses
         compileJava.options.define(compilerArgs: ["-proc:none", "-encoding", "UTF-8"])
         compileTestJava.options.define(compilerArgs: ["-proc:none", "-encoding", "UTF-8"])
 
         jar {
             Set<String> exportPackages = new HashSet<String>()
             Set<String> privatePackages = new HashSet<String>()
 
             // TODO: Could more of this be pulled into utilities.gradle?
             sourceSets.each { sourceSet ->
                 // skip certain source sets
                 if ( ! ['test','matrix'].contains( sourceSet.name ) ) {
                     sourceSet.java.each { javaFile ->
                         // - org.hibernate.boot.registry.classloading.internal
                         // until EntityManagerFactoryBuilderImpl no longer imports ClassLoaderServiceImpl
                         // - .util for external module use (especially envers)
                         final String[] temporaryExports = [
                             'org.hibernate.boot.registry.classloading.internal',
                             'org.hibernate.internal.util' ]
 
                         final String packageName = determinePackageName( sourceSet.java, javaFile );
                         if ( ! temporaryExports.contains( packageName )
                                 && ( packageName.endsWith( ".internal" )
                                 || packageName.contains( ".internal." )
                                 || packageName.endsWith( ".test" )
                                 || packageName.contains( ".test." ) ) ) {
                             privatePackages.add( packageName );
                         }
                         else {
                             exportPackages.add( packageName );
                         }
                     }
                 }
             }
 
             manifest = osgiManifest {
                 // GRADLE-1411: Even if we override Imports and Exports
                 // auto-generation with instructions, classesDir and classpath
                 // need to be here (temporarily).
                 classesDir = sourceSets.main.output.classesDir
                 classpath = configurations.runtime
 
                 instruction 'Import-Package',
                     // Temporarily support JTA 1.1 -- Karaf and other frameworks still
                     // use it.  Without this, the plugin generates [1.2,2).
                     'javax.transaction;version="[1.1,2)"',
                     // Tell Gradle OSGi to still dynamically import the other packages.
                     // IMPORTANT: Do not include the * in the modules' .gradle files.
                     // If it exists more than once, the manifest will physically contain a *.
                     '*'
                 
                 instruction 'Export-Package', exportPackages.toArray(new String[0])
                 instruction 'Private-Package', privatePackages.toArray(new String[0])
                 
                 instruction 'Bundle-Vendor', 'Hibernate.org'
                 instruction 'Implementation-Url', 'http://hibernate.org'
                 instruction 'Implementation-Version', version
                 instruction 'Implementation-Vendor', 'Hibernate.org'
                 instruction 'Implementation-Vendor-Id', 'org.hibernate'
             }
         }
 
         test {
             systemProperties['hibernate.test.validatefailureexpected'] = true
             systemProperties += System.properties.findAll { it.key.startsWith( "hibernate.") }
             maxHeapSize = "1024m"
             // Not strictly needed but useful to attach a profiler:
             jvmArgs '-XX:MaxPermSize=256m'
         }
 
         processTestResources.doLast( {
             copy {
                 from( sourceSets.test.java.srcDirs ) {
                     include '**/*.properties'
                     include '**/*.xml'
                 }
                 into sourceSets.test.output.classesDir
             }
         } )
 
         idea {
             module {
                 iml {
                     beforeMerged { module ->
                         module.dependencies.clear()
                         module.excludeFolders.clear()
                     }
                     whenMerged { module ->
                         module.dependencies*.exported = true
                         module.excludeFolders += module.pathFactory.path(file(".gradle"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/bundles"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/classes"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/dependency-cache"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/libs"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/reports"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/test-results"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/tmp"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/matrix"))
                         module.excludeFolders += module.pathFactory.path(file("$buildDir/resources"))
                         module.excludeFolders -= module.pathFactory.path(file("$buildDir"))
                     }
                 }
                 downloadSources = true
                 scopes.COMPILE.plus += configurations.provided
             }
         }
 
         eclipse {
             classpath {
                 plusConfigurations.add( configurations.provided )
             }
         }
 
     	// eclipseClasspath will not add sources to classpath unless the dirs actually exist.
 	    eclipseClasspath.dependsOn("generateSources")
 
         // specialized API/SPI checkstyle tasks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         task checkstylePublicSources(type: Checkstyle) {
             checkstyleClasspath = checkstyleMain.checkstyleClasspath
             classpath = checkstyleMain.classpath
-            configFile = rootProject.file( 'shared/config/checkstyle/public_checks.xml' )
+            configFile = rootProject.file( 'shared/config/checkstyle/checkstyle.xml' )
             source subProject.sourceSets.main.originalJavaSrcDirs
+            // exclude generated sources
+            exclude '**/generated-src/**'
+            // because cfg package is a mess mainly from annotation stuff
+            exclude '**/org/hibernate/cfg/**'
+            exclude '**/org/hibernate/cfg/*'
+            // because this should only report on api/spi
             exclude '**/internal/**'
             exclude '**/internal/*'
             ignoreFailures = false
             showViolations = true
             reports {
                 xml {
                     destination "$buildDir/reports/checkstyle/public.xml"
                 }
             }
         }
         // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
         // Report configs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         checkstyle {
+            sourceSets = [ subProject.sourceSets.main ]
             configFile = rootProject.file( 'shared/config/checkstyle/checkstyle.xml' )
             showViolations = false
             ignoreFailures = true
         }
+        // exclude generated sources
+        checkstyleMain.exclude '**/generated-src/**'
+        // because cfg package is a mess mainly from annotation stuff
+        checkstyleMain.exclude '**/org/hibernate/cfg/**'
+        checkstyleMain.exclude '**/org/hibernate/cfg/*'
+
         findbugs {
             ignoreFailures = true
         }
+
         buildDashboard.dependsOn check
         // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
         publishing {
             publications {
                 mavenJava(MavenPublication) {
                     from components.java
 
                     artifact sourcesJar {
                         classifier "sources"
                     }
 
                     pom.withXml {
                         def root = asNode();
                         root.appendNode( 'name', subProject.pomName() )
                         root.appendNode( 'description', subProject.pomDescription() )
                         root.appendNode( 'url', 'http://hibernate.org' )
                         def org = root.appendNode( 'organization' )
                         org.appendNode( 'name', 'Hibernate.org' )
                         org.appendNode( 'url', 'http://hibernate.org' )
                         def jira = root.appendNode( 'issueManagement' )
                         jira.appendNode( 'system', 'jira' )
                         jira.appendNode( 'url', 'https://hibernate.atlassian.net/browse/HHH' )
                         def scm = root.appendNode( 'scm' )
                         scm.appendNode( 'url', 'http://github.com/hibernate/hibernate-orm' )
                         scm.appendNode( 'connection', 'scm:git:http://github.com/hibernate/hibernate-orm.git' )
                         scm.appendNode( 'developerConnection', 'scm:git:git@github.com:hibernate/hibernate-orm.git' )
                         def license = root.appendNode( 'licenses' ).appendNode( 'license' );
                         license.appendNode( 'name', 'GNU Lesser General Public License' )
                         license.appendNode( 'url', 'http://www.gnu.org/licenses/lgpl-2.1.html' )
                         license.appendNode( 'comments', 'See discussion at http://hibernate.org/license for more details.' )
                         license.appendNode( 'distribution', 'repo' )
                         def dev = root.appendNode( 'developers' ).appendNode( 'developer' );
                         dev.appendNode( 'id', 'hibernate-team' )
                         dev.appendNode( 'name', 'The Hibernate Development Team' )
                         dev.appendNode( 'organization', 'Hibernate.org' )
                         dev.appendNode( 'organizationUrl', 'http://hibernate.org' )
                     }
                 }
             }
 
             repositories {
                 maven {
                     if ( subProject.version.endsWith( 'SNAPSHOT' ) ) {
                         name 'jboss-snapshots-repository'
                         url 'https://repository.jboss.org/nexus/content/repositories/snapshots'
                     }
                     else {
                         name 'jboss-releases-repository'
                         url 'https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/'
                     }
                 }
             }
 
             generatePomFileForMavenJavaPublication {
                 destination = file("$buildDir/generated-pom.xml")
             }
         }
 
         task sourcesJar(type: Jar, dependsOn: compileJava) {
             from sourceSets.main.allSource
             classifier = 'sources'
         }
     }
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/AssertionFailure.java b/hibernate-core/src/main/java/org/hibernate/AssertionFailure.java
index cc36bccf64..880ba44a9a 100644
--- a/hibernate-core/src/main/java/org/hibernate/AssertionFailure.java
+++ b/hibernate-core/src/main/java/org/hibernate/AssertionFailure.java
@@ -1,60 +1,63 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Indicates failure of an assertion: a possible bug in Hibernate.
  *
  * @author Gavin King
  */
 public class AssertionFailure extends RuntimeException {
-    private static final long serialVersionUID = 1L;
+	private static final long serialVersionUID = 1L;
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AssertionFailure.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			AssertionFailure.class.getName()
+	);
 
 	/**
 	 * Creates an instance of AssertionFailure using the given message.
 	 *
 	 * @param message The message explaining the reason for the exception
 	 */
-    public AssertionFailure(String message) {
-        super( message );
-        LOG.failed( this );
-    }
+	public AssertionFailure(String message) {
+		super( message );
+		LOG.failed( this );
+	}
 
 	/**
 	 * Creates an instance of AssertionFailure using the given message and underlying cause.
 	 *
 	 * @param message The message explaining the reason for the exception
 	 * @param cause The underlying cause.
 	 */
-    public AssertionFailure(String message, Throwable cause) {
-        super( message, cause );
-        LOG.failed( cause );
-    }
+	public AssertionFailure(String message, Throwable cause) {
+		super( message, cause );
+		LOG.failed( cause );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java b/hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java
index 700e0b1bd5..25309ca10e 100644
--- a/hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java
@@ -1,66 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Defines the various policies by which Hibernate might release its underlying
  * JDBC connection.
  *
  * @author Steve Ebersole
  */
 public enum ConnectionReleaseMode{
 	/**
 	 * Indicates that JDBC connection should be aggressively released after each 
 	 * SQL statement is executed. In this mode, the application <em>must</em>
 	 * explicitly close all iterators and scrollable results. This mode may
 	 * only be used with a JTA datasource.
 	 */
 	AFTER_STATEMENT,
 
 	/**
 	 * Indicates that JDBC connections should be released after each transaction 
 	 * ends (works with both JTA-registered synch and HibernateTransaction API).
 	 * This mode may not be used with an application server JTA datasource.
 	 * <p/>
 	 * This is the default mode starting in 3.1; was previously {@link #ON_CLOSE}.
 	 */
 	AFTER_TRANSACTION,
 
 	/**
 	 * Indicates that connections should only be released when the Session is explicitly closed 
 	 * or disconnected; this is the legacy (Hibernate2 and pre-3.1) behavior.
 	 */
 	ON_CLOSE;
 
 	/**
 	 * Alias for {@link ConnectionReleaseMode#valueOf(String)} using upper-case version of the incoming name.
 	 *
 	 * @param name The name to parse
 	 *
 	 * @return The matched enum value.
 	 */
-	public static ConnectionReleaseMode parse(final String name){
+	public static ConnectionReleaseMode parse(final String name) {
 		return ConnectionReleaseMode.valueOf( name.toUpperCase() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Criteria.java b/hibernate-core/src/main/java/org/hibernate/Criteria.java
index 17152825dc..84b0beb000 100644
--- a/hibernate-core/src/main/java/org/hibernate/Criteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/Criteria.java
@@ -1,570 +1,570 @@
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
 package org.hibernate;
 import java.util.List;
 
 import org.hibernate.criterion.CriteriaSpecification;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * <tt>Criteria</tt> is a simplified API for retrieving entities
  * by composing <tt>Criterion</tt> objects. This is a very
  * convenient approach for functionality like "search" screens
  * where there is a variable number of conditions to be placed
  * upon the result set.<br>
  * <br>
  * The <tt>Session</tt> is a factory for <tt>Criteria</tt>.
  * <tt>Criterion</tt> instances are usually obtained via
  * the factory methods on <tt>Restrictions</tt>. eg.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .add( Restrictions.like("name", "Iz%") )
  *     .add( Restrictions.gt( "weight", new Float(minWeight) ) )
  *     .addOrder( Order.asc("age") )
  *     .list();
  * </pre>
  * You may navigate associations using <tt>createAlias()</tt> or
  * <tt>createCriteria()</tt>.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .createCriteria("kittens")
  *         .add( Restrictions.like("name", "Iz%") )
  *     .list();
  * </pre>
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .createAlias("kittens", "kit")
  *     .add( Restrictions.like("kit.name", "Iz%") )
  *     .list();
  * </pre>
  * You may specify projection and aggregation using <tt>Projection</tt>
  * instances obtained via the factory methods on <tt>Projections</tt>.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .setProjection( Projections.projectionList()
  *         .add( Projections.rowCount() )
  *         .add( Projections.avg("weight") )
  *         .add( Projections.max("weight") )
  *         .add( Projections.min("weight") )
  *         .add( Projections.groupProperty("color") )
  *     )
  *     .addOrder( Order.asc("color") )
  *     .list();
  * </pre>
  *
  * @see Session#createCriteria(java.lang.Class)
  * @see org.hibernate.criterion.Restrictions
  * @see org.hibernate.criterion.Projections
  * @see org.hibernate.criterion.Order
  * @see org.hibernate.criterion.Criterion
  * @see org.hibernate.criterion.Projection
  * @see org.hibernate.criterion.DetachedCriteria a disconnected version of this API
  * @author Gavin King
  */
 public interface Criteria extends CriteriaSpecification {
 
 	/**
 	 * Get the alias of the entity encapsulated by this criteria instance.
 	 *
 	 * @return The alias for the encapsulated entity.
 	 */
 	public String getAlias();
 
 	/**
 	 * Used to specify that the query results will be a projection (scalar in
 	 * nature).  Implicitly specifies the {@link #PROJECTION} result transformer.
 	 * <p/>
 	 * The individual components contained within the given
 	 * {@link Projection projection} determines the overall "shape" of the
 	 * query result.
 	 *
 	 * @param projection The projection representing the overall "shape" of the
 	 * query results.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setProjection(Projection projection);
 
 	/**
 	 * Add a {@link Criterion restriction} to constrain the results to be
 	 * retrieved.
 	 *
 	 * @param criterion The {@link Criterion criterion} object representing the
 	 * restriction to be applied.
 	 * @return this (for method chaining)
 	 */
 	public Criteria add(Criterion criterion);
-	
+
 	/**
 	 * Add an {@link Order ordering} to the result set.
 	 *
 	 * @param order The {@link Order order} object representing an ordering
 	 * to be applied to the results.
 	 * @return this (for method chaining)
 	 */
 	public Criteria addOrder(Order order);
 
 	/**
 	 * Specify an association fetching strategy for an association or a
 	 * collection of values.
 	 *
 	 * @param associationPath a dot seperated property path
 	 * @param mode The fetch mode for the referenced association
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem applying the given fetch mode
 	 */
 	public Criteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException;
 
 	/**
 	 * Set the lock mode of the current entity.
 	 *
 	 * @param lockMode The lock mode to be applied
 	 *
 	 * @return this (for method chaining)
 	 */
 	public Criteria setLockMode(LockMode lockMode);
 
 	/**
 	 * Set the lock mode of the aliased entity.
 	 *
 	 * @param alias The previously assigned alias representing the entity to
 	 *			which the given lock mode should apply.
 	 * @param lockMode The lock mode to be applied
 	 *
 	 * @return this (for method chaining)
 	 */
 	public Criteria setLockMode(String alias, LockMode lockMode);
 
 	/**
 	 * Join an association, assigning an alias to the joined association.
 	 * <p/>
 	 * Functionally equivalent to {@link #createAlias(String, String, JoinType )} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link JoinType#INNER_JOIN} (the default),
 	 * {@link JoinType#FULL_JOIN}, or {@link JoinType#LEFT_OUTER_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link #INNER_JOIN} (the default),
 	 * {@link #FULL_JOIN}, or {@link #LEFT_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link JoinType#INNER_JOIN} (the default),
 	 * {@link JoinType#FULL_JOIN}, or {@link JoinType#LEFT_OUTER_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link #INNER_JOIN} (the default),
 	 * {@link #FULL_JOIN}, or {@link #LEFT_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion)}
 	 */
 	@Deprecated
 	public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity.
 	 * <p/>
 	 * Functionally equivalent to {@link #createCriteria(String, org.hibernate.sql.JoinType)} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity, using the
 	 * specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity, using the
 	 * specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, int joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias.
 	 * <p/>
 	 * Functionally equivalent to {@link #createCriteria(String, String, org.hibernate.sql.JoinType)} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createCriteria(String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException;
 
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return the created "sub criteria"
 	 * 
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createCriteria(String, String, org.hibernate.sql.JoinType, org.hibernate.criterion.Criterion)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Set a strategy for handling the query results. This determines the
 	 * "shape" of the query result.
 	 *
 	 * @param resultTransformer The transformer to apply
 	 * @return this (for method chaining)
 	 *
 	 * @see #ROOT_ENTITY
 	 * @see #DISTINCT_ROOT_ENTITY
 	 * @see #ALIAS_TO_ENTITY_MAP
 	 * @see #PROJECTION
 	 */
 	public Criteria setResultTransformer(ResultTransformer resultTransformer);
 
 	/**
 	 * Set a limit upon the number of objects to be retrieved.
 	 *
 	 * @param maxResults the maximum number of results
 	 * @return this (for method chaining)
 	 */
 	public Criteria setMaxResults(int maxResults);
-	
+
 	/**
 	 * Set the first result to be retrieved.
 	 *
 	 * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
 	 * @return this (for method chaining)
 	 */
 	public Criteria setFirstResult(int firstResult);
 
 	/**
 	 * Was the read-only/modifiable mode explicitly initialized?
 	 *
 	 * @return true, the read-only/modifiable mode was explicitly initialized; false, otherwise.
 	 *
 	 * @see Criteria#setReadOnly(boolean)
 	 */
 	public boolean isReadOnlyInitialized();
 
 	/**
 	 * Should entities and proxies loaded by this Criteria be put in read-only mode? If the
 	 * read-only/modifiable setting was not initialized, then the default
 	 * read-only/modifiable setting for the persistence context is returned instead.
 	 * @see Criteria#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * Criteria that existed in the session before the Criteria was executed.
 	 *
 	 * @return true, entities and proxies loaded by the criteria will be put in read-only mode
 	 *         false, entities and proxies loaded by the criteria will be put in modifiable mode
 	 * @throws IllegalStateException if <code>isReadOnlyInitialized()</code> returns <code>false</code>
 	 * and this Criteria is not associated with a session.
 	 * @see Criteria#isReadOnlyInitialized()
 	 */
 	public boolean isReadOnly();
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies
 	 * loaded by this Criteria. This setting overrides the default setting
 	 * for the persistence context.
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies
 	 * returned by the criteria that existed in the session before the criteria was executed.
 	 *
 	 * @param readOnly true, entities and proxies loaded by the criteria will be put in read-only mode
 	 *                 false, entities and proxies loaded by the criteria will be put in modifiable mode
 	 * @return {@code this}, for method chaining
 	 */
 	public Criteria setReadOnly(boolean readOnly);
 
 	/**
 	 * Set a fetch size for the underlying JDBC query.
 	 *
 	 * @param fetchSize the fetch size
 	 * @return this (for method chaining)
 	 *
 	 * @see java.sql.Statement#setFetchSize
 	 */
 	public Criteria setFetchSize(int fetchSize);
 
 	/**
 	 * Set a timeout for the underlying JDBC query.
 	 *
 	 * @param timeout The timeout value to apply.
 	 * @return this (for method chaining)
 	 *
 	 * @see java.sql.Statement#setQueryTimeout
 	 */
 	public Criteria setTimeout(int timeout);
 
 	/**
 	 * Enable caching of this query result, provided query caching is enabled
 	 * for the underlying session factory.
 	 *
 	 * @param cacheable Should the result be considered cacheable; default is
 	 * to not cache (false).
 	 * @return this (for method chaining)
 	 */
 	public Criteria setCacheable(boolean cacheable);
 
 	/**
 	 * Set the name of the cache region to use for query result caching.
 	 *
 	 * @param cacheRegion the name of a query cache region, or <tt>null</tt>
 	 * for the default query cache
 	 * @return this (for method chaining)
 	 *
 	 * @see #setCacheable
 	 */
 	public Criteria setCacheRegion(String cacheRegion);
 
 	/**
 	 * Add a comment to the generated SQL.
 	 *
 	 * @param comment a human-readable string
 	 * @return this (for method chaining)
 	 */
 	public Criteria setComment(String comment);
 
 	/**
 	 * Override the flush mode for this particular query.
 	 *
 	 * @param flushMode The flush mode to use.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setFlushMode(FlushMode flushMode);
 
 	/**
 	 * Override the cache mode for this particular query.
 	 *
 	 * @param cacheMode The cache mode to use.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Get the results.
 	 *
 	 * @return The list of matched query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public List list() throws HibernateException;
-	
+
 	/**
 	 * Get the results as an instance of {@link ScrollableResults}.
 	 *
 	 * @return The {@link ScrollableResults} representing the matched
 	 * query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public ScrollableResults scroll() throws HibernateException;
 
 	/**
 	 * Get the results as an instance of {@link ScrollableResults} based on the
 	 * given scroll mode.
 	 *
 	 * @param scrollMode Indicates the type of underlying database cursor to
 	 * request.
 	 *
 	 * @return The {@link ScrollableResults} representing the matched
 	 * query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException;
 
 	/**
 	 * Convenience method to return a single instance that matches
 	 * the query, or null if the query returns no results.
 	 *
 	 * @return the single result or <tt>null</tt>
 	 * @throws HibernateException if there is more than one matching result
 	 */
 	public Object uniqueResult() throws HibernateException;
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/HibernateError.java b/hibernate-core/src/main/java/org/hibernate/HibernateError.java
index 4376031c02..26b76b73b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/HibernateError.java
+++ b/hibernate-core/src/main/java/org/hibernate/HibernateError.java
@@ -1,43 +1,50 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Marks a group of exceptions that generally indicate an internal Hibernate error or bug.
  *
  * @author Steve Ebersole
  */
-public abstract class HibernateError extends HibernateException {
+public class HibernateError extends HibernateException {
+	/**
+	 * Constructs HibernateError with the condition message.
+	 *
+	 * @param message Message explaining the exception/error condition
+	 */
 	public HibernateError(String message) {
 		super( message );
 	}
 
-	public HibernateError(Throwable root) {
-		super( root );
-	}
-
-	public HibernateError(String message, Throwable root) {
-		super( message, root );
+	/**
+	 * Constructs HibernateError with the condition message and cause.
+	 *
+	 * @param message Message explaining the exception/error condition
+	 * @param cause The underlying cause.
+	 */
+	public HibernateError(String message, Throwable cause) {
+		super( message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java b/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
index 474f295882..6a486a4050 100644
--- a/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
+++ b/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
@@ -1,149 +1,149 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 20082011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.util.xml.XmlDocument;
 
 /**
  * Thrown when a mapping is found to be invalid.
  *
  * Similar to MappingException, but this contains more info about the path and type of
  * mapping (e.g. file, resource or url)
  * 
  * @author Max Rydahl Andersen
  * @author Steve Ebersole
  */
 public class InvalidMappingException extends MappingException {
 	private final String path;
 	private final String type;
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param type The type of invalid mapping document
 	 * @param path The path (type specific) of the invalid mapping document
 	 * @param cause The underlying cause
 	 */
 	public InvalidMappingException(String customMessage, String type, String path, Throwable cause) {
 		super( customMessage, cause );
 		this.type = type;
 		this.path = path;
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param type The type of invalid mapping document
 	 * @param path The path (type specific) of the invalid mapping document
 	 */
 	public InvalidMappingException(String customMessage, String type, String path) {
-		super(customMessage);
+		super( customMessage );
 		this.type=type;
 		this.path=path;
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param xmlDocument The document that was invalid
 	 * @param cause The underlying cause
 	 */
 	public InvalidMappingException(String customMessage, XmlDocument xmlDocument, Throwable cause) {
 		this( customMessage, xmlDocument.getOrigin().getType(), xmlDocument.getOrigin().getName(), cause );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param xmlDocument The document that was invalid
 	 */
 	public InvalidMappingException(String customMessage, XmlDocument xmlDocument) {
 		this( customMessage, xmlDocument.getOrigin().getType(), xmlDocument.getOrigin().getName() );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param origin The origin of the invalid mapping document
 	 */
 	public InvalidMappingException(String customMessage, Origin origin) {
 		this( customMessage, origin.getType().toString(), origin.getName() );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information and a standard message.
 	 *
 	 * @param type The type of invalid mapping document
 	 * @param path The path (type specific) of the invalid mapping document
 	 */
 	public InvalidMappingException(String type, String path) {
-		this("Could not parse mapping document from " + type + (path==null?"":" " + path), type, path);
+		this( "Could not parse mapping document from " + type + (path==null?"":" " + path), type, path );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information and a standard message.
 	 *
 	 * @param type The type of invalid mapping document
 	 * @param path The path (type specific) of the invalid mapping document
 	 * @param cause The underlying cause
 	 */
 	public InvalidMappingException(String type, String path, Throwable cause) {
-		this("Could not parse mapping document from " + type + (path==null?"":" " + path), type, path, cause);		
+		this( "Could not parse mapping document from " + type + (path==null?"":" " + path), type, path, cause );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param origin The origin of the invalid mapping document
 	 * @param cause The underlying cause
 	 */
 	public InvalidMappingException(String customMessage, org.hibernate.internal.util.xml.Origin origin, Exception cause) {
 		this( customMessage, origin.getType(), origin.getName(), cause );
 	}
 
 	/**
 	 * Constructs an InvalidMappingException using the given information.
 	 *
 	 * @param customMessage The custom message explaining the exception condition
 	 * @param origin The origin of the invalid mapping document
 	 */
 	public InvalidMappingException(String customMessage, org.hibernate.internal.util.xml.Origin origin) {
 		this( customMessage, origin, null );
 	}
 
 	public String getType() {
 		return type;
 	}
 	
 	public String getPath() {
 		return path;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/LockMode.java b/hibernate-core/src/main/java/org/hibernate/LockMode.java
index cde6b6bdf0..01a569e257 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockMode.java
@@ -1,156 +1,154 @@
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
 package org.hibernate;
 
 /**
  * Instances represent a lock mode for a row of a relational
  * database table. It is not intended that users spend much
  * time worrying about locking since Hibernate usually
  * obtains exactly the right lock level automatically.
  * Some "advanced" users may wish to explicitly specify lock
  * levels.
  *
  * @author Gavin King
  * @see Session#lock(Object, LockMode)
  */
 public enum LockMode {
 	/**
 	 * No lock required. If an object is requested with this lock
 	 * mode, a <tt>READ</tt> lock will be obtained if it is
 	 * necessary to actually read the state from the database,
 	 * rather than pull it from a cache.<br>
 	 * <br>
 	 * This is the "default" lock mode.
 	 */
 	NONE( 0 ),
 	/**
 	 * A shared lock. Objects in this lock mode were read from
 	 * the database in the current transaction, rather than being
 	 * pulled from a cache.
 	 */
 	READ( 5 ),
 	/**
 	 * An upgrade lock. Objects loaded in this lock mode are
 	 * materialized using an SQL <tt>select ... for update</tt>.
 	 *
 	 * @deprecated instead use PESSIMISTIC_WRITE
 	 */
-    @Deprecated
+	@Deprecated
 	UPGRADE( 10 ),
 	/**
 	 * Attempt to obtain an upgrade lock, using an Oracle-style
 	 * <tt>select for update nowait</tt>. The semantics of
 	 * this lock mode, once obtained, are the same as
 	 * <tt>UPGRADE</tt>.
 	 */
 	UPGRADE_NOWAIT( 10 ),
 
 	/**
 	 * Attempt to obtain an upgrade lock, using an Oracle-style
 	 * <tt>select for update skip locked</tt>. The semantics of
 	 * this lock mode, once obtained, are the same as
 	 * <tt>UPGRADE</tt>.
 	 */
 	UPGRADE_SKIPLOCKED( 10 ),
 
 	/**
 	 * A <tt>WRITE</tt> lock is obtained when an object is updated
 	 * or inserted.   This lock mode is for internal use only and is
 	 * not a valid mode for <tt>load()</tt> or <tt>lock()</tt> (both
 	 * of which throw exceptions if WRITE is specified).
 	 */
 	WRITE( 10 ),
 
 	/**
 	 * Similiar to {@link #UPGRADE} except that, for versioned entities,
 	 * it results in a forced version increment.
 	 *
 	 * @deprecated instead use PESSIMISTIC_FORCE_INCREMENT
 	 */
-    @Deprecated
+	@Deprecated
 	FORCE( 15 ),
 
 	/**
 	 *  start of javax.persistence.LockModeType equivalent modes
 	 */
 
 	/**
 	 * Optimisticly assume that transaction will not experience contention for
 	 * entities.  The entity version will be verified near the transaction end.
 	 */
 	OPTIMISTIC( 6 ),
 
 	/**
 	 * Optimisticly assume that transaction will not experience contention for
 	 * entities.  The entity version will be verified and incremented near the transaction end.
 	 */
 	OPTIMISTIC_FORCE_INCREMENT( 7 ),
 
 	/**
 	 * Implemented as PESSIMISTIC_WRITE.
 	 * TODO:  introduce separate support for PESSIMISTIC_READ
 	 */
 	PESSIMISTIC_READ( 12 ),
 
 	/**
 	 * Transaction will obtain a database lock immediately.
 	 * TODO:  add PESSIMISTIC_WRITE_NOWAIT
 	 */
 	PESSIMISTIC_WRITE( 13 ),
 
 	/**
 	 * Transaction will immediately increment the entity version.
 	 */
 	PESSIMISTIC_FORCE_INCREMENT( 17 );
 	private final int level;
 
 	private LockMode(int level) {
 		this.level = level;
 	}
 
 	/**
 	 * Check if this lock mode is more restrictive than the given lock mode.
 	 *
 	 * @param mode LockMode to check
 	 *
 	 * @return true if this lock mode is more restrictive than given lock mode
 	 */
 	public boolean greaterThan(LockMode mode) {
 		return level > mode.level;
 	}
 
 	/**
 	 * Check if this lock mode is less restrictive than the given lock mode.
 	 *
 	 * @param mode LockMode to check
 	 *
 	 * @return true if this lock mode is less restrictive than given lock mode
 	 */
 	public boolean lessThan(LockMode mode) {
 		return level < mode.level;
 	}
-
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/MappingException.java b/hibernate-core/src/main/java/org/hibernate/MappingException.java
index 31d6aec618..6e62096188 100644
--- a/hibernate-core/src/main/java/org/hibernate/MappingException.java
+++ b/hibernate-core/src/main/java/org/hibernate/MappingException.java
@@ -1,67 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * An exception that occurs while reading mapping sources (xml/annotations),usually as a result of something
  * screwy in the O-R mappings.
  *
  * @author Gavin King
  */
 public class MappingException extends HibernateException {
 	/**
 	 * Constructs a MappingException using the given information.
 	 *
 	 * @param message A message explaining the exception condition
 	 * @param cause The underlying cause
 	 */
 	public MappingException(String message, Throwable cause) {
 		super( message, cause );
 	}
 
 	/**
 	 * Constructs a MappingException using the given information.
 	 *
 	 * @param cause The underlying cause
 	 */
 	public MappingException(Throwable cause) {
-		super(cause);
+		super( cause );
 	}
 
 	/**
 	 * Constructs a MappingException using the given information.
 	 *
 	 * @param message A message explaining the exception condition
 	 */
 	public MappingException(String message) {
-		super(message);
+		super( message );
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java
index 73680e32eb..5be7da0087 100644
--- a/hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java
+++ b/hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java
@@ -1,90 +1,90 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Thrown when a resource for a mapping could not be found.
  * 
  * @author Max Rydahl Andersen
  */
 public class MappingNotFoundException extends MappingException {
 	private final String path;
 	private final String type;
 
 	/**
 	 * Constructs a MappingNotFoundException using the given information.
 	 *
 	 * @param customMessage A message explaining the exception condition
 	 * @param type The type of mapping that could not be found
 	 * @param path The path (type specific) of the mapping that could not be found
 	 * @param cause The underlying cause
 	 */
 	public MappingNotFoundException(String customMessage, String type, String path, Throwable cause) {
-		super(customMessage, cause);
-		this.type=type;
-		this.path=path;
+		super( customMessage, cause );
+		this.type = type;
+		this.path = path;
 	}
 
 	/**
 	 * Constructs a MappingNotFoundException using the given information.
 	 *
 	 * @param customMessage A message explaining the exception condition
 	 * @param type The type of mapping that could not be found
 	 * @param path The path (type specific) of the mapping that could not be found
 	 */
 	public MappingNotFoundException(String customMessage, String type, String path) {
-		super(customMessage);
-		this.type=type;
-		this.path=path;
+		super( customMessage );
+		this.type = type;
+		this.path = path;
 	}
 
 	/**
 	 * Constructs a MappingNotFoundException using the given information, using a standard message.
 	 *
 	 * @param type The type of mapping that could not be found
 	 * @param path The path (type specific) of the mapping that could not be found
 	 */
 	public MappingNotFoundException(String type, String path) {
-		this(type + ": " + path + " not found", type, path);
+		this( type + ": " + path + " not found", type, path );
 	}
 
 	/**
 	 * Constructs a MappingNotFoundException using the given information, using a standard message.
 	 *
 	 * @param type The type of mapping that could not be found
 	 * @param path The path (type specific) of the mapping that could not be found
 	 * @param cause The underlying cause
 	 */
 	public MappingNotFoundException(String type, String path, Throwable cause) {
-		this(type + ": " + path + " not found", type, path, cause);
+		this( type + ": " + path + " not found", type, path, cause );
 	}
 
 	public String getType() {
 		return type;
 	}
 	
 	public String getPath() {
 		return path;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java
index a69dd6e551..08eb066282 100644
--- a/hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java
+++ b/hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java
@@ -1,51 +1,51 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 /**
  * Thrown when <tt>Session.load()</tt> fails to select a row with
  * the given primary key (identifier value). This exception might not
  * be thrown when <tt>load()</tt> is called, even if there was no
  * row on the database, because <tt>load()</tt> returns a proxy if
  * possible. Applications should use <tt>Session.get()</tt> to test if
  * a row exists in the database.<br>
  * <br> 
  * Like all Hibernate exceptions, this exception is considered 
  * unrecoverable.
  *
  * @author Gavin King
  */
 public class ObjectNotFoundException extends UnresolvableObjectException {
 	/**
 	 * Constructs a ObjectNotFoundException using the given information.
 	 *
 	 * @param identifier The identifier of the entity
 	 * @param entityName The name of the entity
 	 */
 	public ObjectNotFoundException(Serializable identifier, String entityName) {
-		super(identifier, entityName);
+		super( identifier, entityName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java b/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
index 2c0620e7a9..95d31f5145 100644
--- a/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
+++ b/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
@@ -1,86 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A problem occurred accessing a property of an instance of a
  * persistent class by reflection, or via CGLIB. There are a
  * number of possible underlying causes, including
  * <ul>
  * <li>failure of a security check
  * <li>an exception occurring inside the getter or setter method
  * <li>a nullable database column was mapped to a primitive-type property
  * <li>the Hibernate type was not castable to the property type (or vice-versa)
  * </ul>
  * @author Gavin King
  */
 public class PropertyAccessException extends HibernateException {
 	private final Class persistentClass;
 	private final String propertyName;
 	private final boolean wasSetter;
 
 	/**
 	 * Constructs a PropertyAccessException using the specified information.
 	 *
 	 * @param cause The underlying cause
 	 * @param message A message explaining the exception condition
 	 * @param wasSetter Was the attempting to access the setter the cause of the exception?
 	 * @param persistentClass The class which is supposed to contain the property in question
 	 * @param propertyName The name of the property.
 	 */
 	public PropertyAccessException(
 			Throwable cause,
 			String message,
 			boolean wasSetter,
 			Class persistentClass,
 			String propertyName) {
-		super(message, cause);
+		super( message, cause );
 		this.persistentClass = persistentClass;
 		this.wasSetter = wasSetter;
 		this.propertyName = propertyName;
 	}
 
 	public Class getPersistentClass() {
 		return persistentClass;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	@Override
-    public String getMessage() {
-		return super.getMessage() +
-		( wasSetter ? " setter of " : " getter of ") +
-		StringHelper.qualify( persistentClass.getName(), propertyName );
+	public String getMessage() {
+		return super.getMessage()
+				+ ( wasSetter ? " setter of " : " getter of " )
+				+ StringHelper.qualify( persistentClass.getName(), propertyName );
 	}
 }
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java
index b2498525fa..deaae34ada 100644
--- a/hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java
+++ b/hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Indicates that an expected getter or setter method could not be
  * found on a class.
  *
  * @author Gavin King
  */
 public class PropertyNotFoundException extends MappingException {
 	/**
 	 * Constructs a PropertyNotFoundException given the specified message.
 	 *
 	 * @param message A message explaining the exception condition
 	 */
 	public PropertyNotFoundException(String message) {
-		super(message);
+		super( message );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java b/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
index 9634fd5c0c..052cc52617 100644
--- a/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
+++ b/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
@@ -1,66 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Thrown when the (illegal) value of a property can not be persisted.
  * There are two main causes:
  * <ul>
  * <li>a property declared <tt>not-null="true"</tt> is null
  * <li>an association references an unsaved transient instance
  * </ul>
  * @author Gavin King
  */
 public class PropertyValueException extends HibernateException {
 	private final String entityName;
 	private final String propertyName;
 
 	/**
 	 * Constructs a PropertyValueException using the specified information.
 	 *
 	 * @param message A message explaining the exception condition
 	 * @param entityName The name of the entity, containing the property
 	 * @param propertyName The name of the property being accessed.
 	 */
 	public PropertyValueException(String message, String entityName, String propertyName) {
-		super(message);
+		super( message );
 		this.entityName = entityName;
 		this.propertyName = propertyName;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	@Override
-    public String getMessage() {
+	public String getMessage() {
 		return super.getMessage() + " : " + StringHelper.qualify( entityName, propertyName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/QueryException.java b/hibernate-core/src/main/java/org/hibernate/QueryException.java
index fd20591a22..6a0a27d752 100644
--- a/hibernate-core/src/main/java/org/hibernate/QueryException.java
+++ b/hibernate-core/src/main/java/org/hibernate/QueryException.java
@@ -1,110 +1,110 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.jboss.logging.Logger;
 
 /**
  * A problem occurred translating a Hibernate query to SQL due to invalid query syntax, etc.
  */
 public class QueryException extends HibernateException {
 	private static final Logger log = Logger.getLogger( QueryException.class );
 
 	private String queryString;
 
 	/**
 	 * Constructs a QueryException using the specified exception message.
 	 *
 	 * @param message A message explaining the exception condition
 	 */
 	public QueryException(String message) {
-		super(message);
+		super( message );
 	}
 
 	/**
 	 * Constructs a QueryException using the specified exception message and cause.
 	 *
 	 * @param message A message explaining the exception condition
 	 * @param cause The underlying cause
 	 */
 	public QueryException(String message, Throwable cause) {
-		super(message, cause);
+		super( message, cause );
 	}
 
 	/**
 	 * Constructs a QueryException using the specified exception message and query-string.
 	 *
 	 * @param message A message explaining the exception condition
 	 * @param queryString The query being evaluated when the exception occurred
 	 */
 	public QueryException(String message, String queryString) {
-		super(message);
+		super( message );
 		this.queryString = queryString;
 	}
 
 	/**
 	 * Constructs a QueryException using the specified cause.
 	 *
 	 * @param cause The underlying cause
 	 */
 	public QueryException(Exception cause) {
-		super(cause);
+		super( cause );
 	}
 
 	/**
 	 * Retrieve the query being evaluated when the exception occurred.  May be null, but generally should not.
 	 *
 	 * @return The query string
 	 */
 	public String getQueryString() {
 		return queryString;
 	}
 
 	/**
 	 * Set the query string.  Even an option since often the part of the code generating the exception does not
 	 * have access to the query overall.
 	 *
 	 * @param queryString The query string.
 	 */
 	public void setQueryString(String queryString) {
 		if ( this.queryString != null ) {
 			log.debugf(
 					"queryString overriding non-null previous value [%s] : %s",
 					this.queryString,
 					queryString
 			);
 		}
 		this.queryString = queryString;
 	}
 
 	@Override
 	public String getMessage() {
 		String msg = super.getMessage();
 		if ( queryString!=null ) {
 			msg += " [" + queryString + ']';
 		}
 		return msg;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java b/hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java
index 666833fbb3..b29cce8337 100644
--- a/hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java
+++ b/hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java
@@ -1,45 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.sql.SQLException;
 
 /**
  * Thrown when a database query timeout occurs.
  *
  * @author Scott Marlow
  */
 public class QueryTimeoutException extends JDBCException {
 	/**
 	 * Constructs a QueryTimeoutException using the supplied information.
 	 *
 	 * @param message The message explaining the exception condition
 	 * @param sqlException The underlying SQLException
 	 * @param sql The sql being executed when the exception occurred.
 	 */
 	public QueryTimeoutException(String message, SQLException sqlException, String sql) {
-		super(message, sqlException, sql);
+		super( message, sqlException, sql );
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
index d363fdf099..91eb409f96 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
@@ -1,404 +1,404 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 
 import javax.naming.Referenceable;
 
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.stat.Statistics;
 
 /**
  * The main contract here is the creation of {@link Session} instances.  Usually
  * an application has a single {@link SessionFactory} instance and threads
  * servicing client requests obtain {@link Session} instances from this factory.
  * <p/>
  * The internal state of a {@link SessionFactory} is immutable.  Once it is created
  * this internal state is set.  This internal state includes all of the metadata
  * about Object/Relational Mapping.
  * <p/>
  * Implementors <strong>must</strong> be threadsafe.
  *
  * @see org.hibernate.cfg.Configuration
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionFactory extends Referenceable, Serializable {
 	/**
 	 * Aggregator of special options used to build the SessionFactory.
 	 */
 	public interface SessionFactoryOptions {
 		/**
 		 * The service registry to use in building the factory.
 		 *
 		 * @return The service registry to use.
 		 */
 		public StandardServiceRegistry getServiceRegistry();
 
 		/**
 		 * Get the interceptor to use by default for all sessions opened from this factory.
 		 *
 		 * @return The interceptor to use factory wide.  May be {@code null}
 		 */
 		public Interceptor getInterceptor();
 
 		/**
 		 * Get the delegate for handling entity-not-found exception conditions.
 		 *
 		 * @return The specific EntityNotFoundDelegate to use,  May be {@code null}
 		 */
 		public EntityNotFoundDelegate getEntityNotFoundDelegate();
 	}
 
 	/**
 	 * Get the special options used to build the factory.
 	 *
 	 * @return The special options used to build the factory.
 	 */
 	public SessionFactoryOptions getSessionFactoryOptions();
 
 	/**
 	 * Obtain a {@link Session} builder.
 	 *
 	 * @return The session builder
 	 */
 	public SessionBuilder withOptions();
 
 	/**
 	 * Open a {@link Session}.
 	 * <p/>
 	 * JDBC {@link Connection connection(s} will be obtained from the
 	 * configured {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} as needed
 	 * to perform requested work.
 	 *
 	 * @return The created session.
 	 *
 	 * @throws HibernateException Indicates a problem opening the session; pretty rare here.
 	 */
 	public Session openSession() throws HibernateException;
 
 	/**
 	 * Obtains the current session.  The definition of what exactly "current"
 	 * means controlled by the {@link org.hibernate.context.spi.CurrentSessionContext} impl configured
 	 * for use.
 	 * <p/>
 	 * Note that for backwards compatibility, if a {@link org.hibernate.context.spi.CurrentSessionContext}
 	 * is not configured but JTA is configured this will default to the {@link org.hibernate.context.internal.JTASessionContext}
 	 * impl.
 	 *
 	 * @return The current session.
 	 *
 	 * @throws HibernateException Indicates an issue locating a suitable current session.
 	 */
 	public Session getCurrentSession() throws HibernateException;
 
 	/**
 	 * Obtain a {@link StatelessSession} builder.
 	 *
 	 * @return The stateless session builder
 	 */
 	public StatelessSessionBuilder withStatelessOptions();
 
 	/**
 	 * Open a new stateless session.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession();
 
 	/**
 	 * Open a new stateless session, utilizing the specified JDBC
 	 * {@link Connection}.
 	 *
 	 * @param connection Connection provided by the application.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession(Connection connection);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityClass The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public ClassMetadata getClassMetadata(Class entityClass);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityName The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 * @since 3.0
 	 */
 	public ClassMetadata getClassMetadata(String entityName);
 
 	/**
 	 * Get the {@link CollectionMetadata} associated with the named collection role.
 	 *
 	 * @param roleName The collection role (in form [owning-entity-name].[collection-property-name]).
 	 *
 	 * @return The metadata associated with the given collection; may be null if no such
 	 * collection was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public CollectionMetadata getCollectionMetadata(String roleName);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} for all mapped entities.
 	 *
 	 * @return A map containing all {@link ClassMetadata} keyed by the
 	 * corresponding {@link String} entity-name.
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 *
 	 * @since 3.0 changed key from {@link Class} to {@link String}.
 	 */
 	public Map<String,ClassMetadata> getAllClassMetadata();
 
 	/**
 	 * Get the {@link CollectionMetadata} for all mapped collections.
 	 *
 	 * @return a map from <tt>String</tt> to <tt>CollectionMetadata</tt>
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 */
 	public Map getAllCollectionMetadata();
 
 	/**
 	 * Retrieve the statistics fopr this factory.
 	 *
 	 * @return The statistics.
 	 */
 	public Statistics getStatistics();
 
 	/**
 	 * Destroy this <tt>SessionFactory</tt> and release all resources (caches,
 	 * connection pools, etc).
 	 * <p/>
 	 * It is the responsibility of the application to ensure that there are no
 	 * open {@link Session sessions} before calling this method as the impact
 	 * on those {@link Session sessions} is indeterminate.
 	 * <p/>
 	 * No-ops if already {@link #isClosed closed}.
 	 *
 	 * @throws HibernateException Indicates an issue closing the factory.
 	 */
 	public void close() throws HibernateException;
 
 	/**
 	 * Is this factory already closed?
 	 *
 	 * @return True if this factory is already closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Obtain direct access to the underlying cache regions.
 	 *
 	 * @return The direct cache access API.
 	 */
 	public Cache getCache();
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(Class)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evict(Class persistentClass) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#containsEntity(Class, Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evict(Class persistentClass, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictEntity(String entityName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntity(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictEntity(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role whose regions should be evicted
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollectionRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictCollection(String roleName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role
 	 * @param id The id of the collection owner
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollection(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictCollection(String roleName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the named query cache region.
 	 *
 	 * @param cacheRegion The named query cache region from which to evict.
 	 *
 	 * @throws HibernateException Since a not-found 'cacheRegion' simply no-ops,
 	 * this should indicate a problem communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictQueries(String cacheRegion) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the default query cache region.
 	 *
 	 * @throws HibernateException Indicate a problem communicating with
 	 * underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegions} accessed through
 	 * {@link #getCache()} instead.
 	 */
-    @Deprecated
+	@Deprecated
 	public void evictQueries() throws HibernateException;
 
 	/**
 	 * Obtain a set of the names of all filters defined on this SessionFactory.
 	 *
 	 * @return The set of filter names.
 	 */
 	public Set getDefinedFilterNames();
 
 	/**
 	 * Obtain the definition of a filter by name.
 	 *
 	 * @param filterName The name of the filter for which to obtain the definition.
 	 * @return The filter definition.
 	 * @throws HibernateException If no filter defined with the given name.
 	 */
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;
 
 	/**
 	 * Determine if this session factory contains a fetch profile definition
 	 * registered under the given name.
 	 *
 	 * @param name The name to check
 	 * @return True if there is such a fetch profile; false otherwise.
 	 */
 	public boolean containsFetchProfileDefinition(String name);
 
 	/**
 	 * Retrieve this factory's {@link TypeHelper}.
 	 *
 	 * @return The factory's {@link TypeHelper}
 	 */
 	public TypeHelper getTypeHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java b/hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java
index e10ccd6cb2..19cc4964fb 100644
--- a/hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java
+++ b/hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java
@@ -1,71 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * A specialized StaleStateException that carries information about the particular entity
  * instance that was the source of the failure.
  *
  * @author Gavin King
  */
 public class StaleObjectStateException extends StaleStateException {
 	private final String entityName;
 	private final Serializable identifier;
 
 	/**
 	 * Constructs a StaleObjectStateException using the supplied information.
 	 *
 	 * @param entityName The name of the entity
 	 * @param identifier The identifier of the entity
 	 */
 	public StaleObjectStateException(String entityName, Serializable identifier) {
-		super("Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)");
+		super( "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)" );
 		this.entityName = entityName;
 		this.identifier = identifier;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public Serializable getIdentifier() {
 		return identifier;
 	}
 
 	public String getMessage() {
 		return super.getMessage() + " : " + MessageHelper.infoString( entityName, identifier );
 	}
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/StaleStateException.java b/hibernate-core/src/main/java/org/hibernate/StaleStateException.java
index f7ace1a9f0..909d0e7b30 100755
--- a/hibernate-core/src/main/java/org/hibernate/StaleStateException.java
+++ b/hibernate-core/src/main/java/org/hibernate/StaleStateException.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Thrown when a version number or timestamp check failed, indicating that the Session contained
  * stale data (when using long transactions with versioning). Also occurs if we try delete or update
  * a row that does not exist.
  *
  * Note that this exception often indicates that the user failed to specify the correct
  * {@code unsaved-value} strategy for an entity
  *
  * @author Gavin King
  */
 public class StaleStateException extends HibernateException {
 	/**
 	 * Constructs a StaleStateException using the supplied message.
 	 *
 	 * @param message The message explaining the exception condition
 	 */
 	public StaleStateException(String message) {
-		super(message);
+		super( message );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/TransactionException.java b/hibernate-core/src/main/java/org/hibernate/TransactionException.java
index 61a2bb11e5..cdaea0f7e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/TransactionException.java
+++ b/hibernate-core/src/main/java/org/hibernate/TransactionException.java
@@ -1,52 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Indicates that a transaction could not be begun, committed
  * or rolled back.
  *
  * @author Anton van Straaten
  */
 public class TransactionException extends HibernateException {
 	/**
 	 * Constructs a TransactionException using the specified information.
 	 *
 	 * @param message The message explaining the exception condition
 	 * @param cause The underlying cause
 	 */
 	public TransactionException(String message, Throwable cause) {
-		super(message,cause);
+		super( message, cause );
 	}
 
 	/**
 	 * Constructs a TransactionException using the specified information.
 	 *
 	 * @param message The message explaining the exception condition
 	 */
 	public TransactionException(String message) {
-		super(message);
+		super( message );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/TransientObjectException.java b/hibernate-core/src/main/java/org/hibernate/TransientObjectException.java
index f51070a552..d110c88aba 100644
--- a/hibernate-core/src/main/java/org/hibernate/TransientObjectException.java
+++ b/hibernate-core/src/main/java/org/hibernate/TransientObjectException.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Thrown when the user passes a transient instance to a Session method that expects a persistent instance.
  *
  * @author Gavin King
  */
 public class TransientObjectException extends HibernateException {
 	/**
 	 * Constructs a TransientObjectException using the supplied message.
 	 *
 	 * @param message The message explaining the exception condition
 	 */
 	public TransientObjectException(String message) {
-		super(message);
+		super( message );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/TransientPropertyValueException.java b/hibernate-core/src/main/java/org/hibernate/TransientPropertyValueException.java
index eec3347269..079fb9f1c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/TransientPropertyValueException.java
+++ b/hibernate-core/src/main/java/org/hibernate/TransientPropertyValueException.java
@@ -1,90 +1,90 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Thrown when a property cannot be persisted because it is an association
  * with a transient unsaved entity instance.
  *
  * @author Gail Badner
  */
 public class TransientPropertyValueException extends TransientObjectException {
 	private final String transientEntityName;
 	private final String propertyOwnerEntityName;
 	private final String propertyName;
 
 	/**
 	 * Constructs an {@link TransientPropertyValueException} instance.
 	 *
 	 * @param message - the exception message;
 	 * @param transientEntityName - the entity name for the transient entity
 	 * @param propertyOwnerEntityName - the entity name for entity that owns
 	 * the association property.
 	 * @param propertyName - the property name
 	 */
 	public TransientPropertyValueException(
 			String message, 
 			String transientEntityName, 
 			String propertyOwnerEntityName, 
 			String propertyName) {
-		super(message);
+		super( message );
 		this.transientEntityName = transientEntityName;
 		this.propertyOwnerEntityName = propertyOwnerEntityName;
 		this.propertyName = propertyName;
 	}
 
 	/**
 	 * Returns the entity name for the transient entity.
 	 * @return the entity name for the transient entity.
 	 */
 	public String getTransientEntityName() {
 		return transientEntityName;
 	}
 
 	/**
 	 * Returns the entity name for entity that owns the association
 	 * property.
 	 * @return the entity name for entity that owns the association
 	 * property
 	 */
 	public String getPropertyOwnerEntityName() {
 		return propertyOwnerEntityName;
 	}
 
 	/**
 	 * Returns the property name.
 	 * @return the property name.
 	 */
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	@Override
 	public String getMessage() {
 		return super.getMessage() + " : "
 				+ StringHelper.qualify( propertyOwnerEntityName, propertyName ) + " -> " + transientEntityName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java b/hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java
index 1d19e27b5a..73a9237dc6 100644
--- a/hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java
+++ b/hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java
@@ -1,93 +1,92 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Thrown when Hibernate could not resolve an object by id, especially when
  * loading an association.
  *
  * @author Gavin King
  */
 public class UnresolvableObjectException extends HibernateException {
 	private final Serializable identifier;
 	private final String entityName;
 
 	/**
 	 * Constructs an UnresolvableObjectException using the specified information.
 	 *
 	 * @param identifier The identifier of the entity which could not be resolved
 	 * @param entityName The name of the entity which could not be resolved
 	 */
 	public UnresolvableObjectException(Serializable identifier, String entityName) {
 		this( "No row with the given identifier exists", identifier, entityName );
 	}
 
 	protected UnresolvableObjectException(String message, Serializable identifier, String clazz) {
-		super(message);
+		super( message );
 		this.identifier = identifier;
 		this.entityName = clazz;
 	}
 
 	/**
 	 * Factory method for building and throwing an UnresolvableObjectException if the entity is null.
 	 *
 	 * @param entity The entity to check for nullness
 	 * @param identifier The identifier of the entity
 	 * @param entityName The name of the entity
 	 *
 	 * @throws UnresolvableObjectException Thrown if entity is null
 	 */
 	public static void throwIfNull(Object entity, Serializable identifier, String entityName)
 			throws UnresolvableObjectException {
 		if ( entity == null ) {
 			throw new UnresolvableObjectException( identifier, entityName );
 		}
 	}
 
 	public Serializable getIdentifier() {
 		return identifier;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public String getMessage() {
-		return super.getMessage() + ": " +
-			MessageHelper.infoString(entityName, identifier);
+		return super.getMessage() + ": " + MessageHelper.infoString( entityName, identifier );
 	}
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
index 612ffd55ed..3f411d4396 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
@@ -1,218 +1,220 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.NonNullableTransientDependencies;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A base class for entity insert actions.
  *
  * @author Gail Badner
  */
 public abstract class AbstractEntityInsertAction extends EntityAction {
 	private transient Object[] state;
 	private final boolean isVersionIncrementDisabled;
 	private boolean isExecuted;
 	private boolean areTransientReferencesNullified;
 
 	/**
 	 * Constructs an AbstractEntityInsertAction object.
 	 *
 	 * @param id - the entity ID
 	 * @param state - the entity state
 	 * @param instance - the entity
 	 * @param isVersionIncrementDisabled - true, if version increment should
 	 *                                     be disabled; false, otherwise
 	 * @param persister - the entity persister
 	 * @param session - the session
 	 */
 	protected AbstractEntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			boolean isVersionIncrementDisabled,
 			EntityPersister persister,
 			SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.isVersionIncrementDisabled = isVersionIncrementDisabled;
 		this.isExecuted = false;
 		this.areTransientReferencesNullified = false;
 
-		if (id != null) {
+		if ( id != null ) {
 			handleNaturalIdPreSaveNotifications();
 		}
 	}
 
 	/**
 	 * Returns the entity state.
 	 *
 	 * NOTE: calling {@link #nullifyTransientReferencesIfNotAlready} can modify the
 	 *       entity state.
 	 * @return the entity state.
 	 *
 	 * @see {@link #nullifyTransientReferencesIfNotAlready}
 	 */
 	public Object[] getState() {
 		return state;
 	}
 
 	/**
 	 * Does this insert action need to be executed as soon as possible
 	 * (e.g., to generate an ID)?
 	 * @return true, if it needs to be executed as soon as possible;
 	 *         false, otherwise.
 	 */
 	public abstract boolean isEarlyInsert();
 
 	/**
 	 * Find the transient unsaved entity dependencies that are non-nullable.
 	 * @return the transient unsaved entity dependencies that are non-nullable,
 	 *         or null if there are none.
 	 */
 	public NonNullableTransientDependencies findNonNullableTransientEntities() {
 		return ForeignKeys.findNonNullableTransientEntities(
 				getPersister().getEntityName(),
 				getInstance(),
 				getState(),
 				isEarlyInsert(),
 				getSession()
 		);
 	}
 
 	/**
 	 * Nullifies any references to transient entities in the entity state
 	 * maintained by this action. References to transient entities
 	 * should be nullified when an entity is made "managed" or when this
 	 * action is executed, whichever is first.
 	 * <p/>
 	 * References will only be nullified the first time this method is
 	 * called for a this object, so it can safely be called both when
 	 * the entity is made "managed" and when this action is executed.
 	 *
 	 * @see {@link #makeEntityManaged() }
 	 */
 	protected final void nullifyTransientReferencesIfNotAlready() {
 		if ( ! areTransientReferencesNullified ) {
 			new ForeignKeys.Nullifier( getInstance(), false, isEarlyInsert(), getSession() )
 					.nullifyTransientReferences( getState(), getPersister().getPropertyTypes() );
 			new Nullability( getSession() ).checkNullability( getState(), getPersister(), false );
 			areTransientReferencesNullified = true;
 		}
 	}
 
 	/**
 	 * Make the entity "managed" by the persistence context.
 	 */
 	public final void makeEntityManaged() {
 		nullifyTransientReferencesIfNotAlready();
-		Object version = Versioning.getVersion( getState(), getPersister() );
+		final Object version = Versioning.getVersion( getState(), getPersister() );
 		getSession().getPersistenceContext().addEntity(
 				getInstance(),
 				( getPersister().isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				getState(),
 				getEntityKey(),
 				version,
 				LockMode.WRITE,
 				isExecuted,
 				getPersister(),
 				isVersionIncrementDisabled,
 				false
 		);
 	}
 
 	/**
 	 * Indicate that the action has executed.
 	 */
 	protected void markExecuted() {
 		this.isExecuted = true;
 	}
 
 	/**
 	 * Returns the {@link EntityKey}.
 	 * @return the {@link EntityKey}.
 	 */
 	protected abstract EntityKey getEntityKey();
 
 	@Override
-    public void afterDeserialize(SessionImplementor session) {
+	public void afterDeserialize(SessionImplementor session) {
 		super.afterDeserialize( session );
 		// IMPL NOTE: non-flushed changes code calls this method with session == null...
 		// guard against NullPointerException
 		if ( session != null ) {
-			EntityEntry entityEntry = session.getPersistenceContext().getEntry( getInstance() );
+			final EntityEntry entityEntry = session.getPersistenceContext().getEntry( getInstance() );
 			this.state = entityEntry.getLoadedState();
 		}
 	}
 
 	/**
 	 * Handle sending notifications needed for natural-id before saving
 	 */
 	protected void handleNaturalIdPreSaveNotifications() {
 		// before save, we need to add a local (transactional) natural id cross-reference
 		getSession().getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 				getPersister(),
 				getId(),
 				state,
 				null,
 				CachedNaturalIdValueSource.INSERT
 		);
 	}
 
 	/**
 	 * Handle sending notifications needed for natural-id after saving
+	 *
+	 * @param generatedId The generated entity identifier
 	 */
 	public void handleNaturalIdPostSaveNotifications(Serializable generatedId) {
-		if (isEarlyInsert()) {
+		if ( isEarlyInsert() ) {
 			// with early insert, we still need to add a local (transactional) natural id cross-reference
 			getSession().getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 					getPersister(),
 					generatedId,
 					state,
 					null,
 					CachedNaturalIdValueSource.INSERT
 			);
 		}
 		// after save, we need to manage the shared cache entries
 		getSession().getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
 				getPersister(),
 				getId(),
 				state,
 				null,
 				CachedNaturalIdValueSource.INSERT
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
index f8917f4653..7423691c76 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
@@ -1,264 +1,264 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * An {@link org.hibernate.engine.spi.ActionQueue} {@link org.hibernate.action.spi.Executable} for ensuring
  * shared cache cleanup in relation to performed bulk HQL queries.
  * <p/>
  * NOTE: currently this executes for <tt>INSERT</tt> queries as well as
  * <tt>UPDATE</tt> and <tt>DELETE</tt> queries.  For <tt>INSERT</tt> it is
  * really not needed as we'd have no invalid entity/collection data to
  * cleanup (we'd still nee to invalidate the appropriate update-timestamps
  * regions) as a result of this query.
  *
  * @author Steve Ebersole
  */
 public class BulkOperationCleanupAction implements Executable, Serializable {
 	private final Serializable[] affectedTableSpaces;
 
 	private final Set<EntityCleanup> entityCleanups = new HashSet<EntityCleanup>();
 	private final Set<CollectionCleanup> collectionCleanups = new HashSet<CollectionCleanup>();
 	private final Set<NaturalIdCleanup> naturalIdCleanups = new HashSet<NaturalIdCleanup>();
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on the
 	 * affected entity persisters.  The affected regions are defined as the
 	 * region (if any) of the entity persisters themselves, plus the
 	 * collection regions for any collection in which those entity
 	 * persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param affectedQueryables The affected entity persisters.
 	 */
 	public BulkOperationCleanupAction(SessionImplementor session, Queryable... affectedQueryables) {
-		SessionFactoryImplementor factory = session.getFactory();
-		LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
+		final SessionFactoryImplementor factory = session.getFactory();
+		final LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		for ( Queryable persister : affectedQueryables ) {
 			spacesList.addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
 
 			if ( persister.hasCache() ) {
 				entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 			}
 			if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 				naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 			}
 
-			Set<String> roles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
+			final Set<String> roles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
 			if ( roles != null ) {
 				for ( String role : roles ) {
-					CollectionPersister collectionPersister = factory.getCollectionPersister( role );
+					final CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 					if ( collectionPersister.hasCache() ) {
 						collectionCleanups.add( new CollectionCleanup( collectionPersister.getCacheAccessStrategy() ) );
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on a
 	 * set of affected table spaces.  This differs from {@link #BulkOperationCleanupAction(SessionImplementor, Queryable[])}
 	 * in that here we have the affected <strong>table names</strong>.  From those
 	 * we deduce the entity persisters which are affected based on the defined
 	 * {@link EntityPersister#getQuerySpaces() table spaces}; and from there, we
 	 * determine the affected collection regions based on any collections
 	 * in which those entity persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param tableSpaces The table spaces.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public BulkOperationCleanupAction(SessionImplementor session, Set tableSpaces) {
-		LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
+		final LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		spacesList.addAll( tableSpaces );
 
-		SessionFactoryImplementor factory = session.getFactory();
+		final SessionFactoryImplementor factory = session.getFactory();
 		for ( String entityName : factory.getAllClassMetadata().keySet() ) {
 			final EntityPersister persister = factory.getEntityPersister( entityName );
 			final String[] entitySpaces = (String[]) persister.getQuerySpaces();
 			if ( affectedEntity( tableSpaces, entitySpaces ) ) {
 				spacesList.addAll( Arrays.asList( entitySpaces ) );
 
 				if ( persister.hasCache() ) {
 					entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 				}
 				if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 					naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 				}
 
-				Set<String> roles = session.getFactory().getCollectionRolesByEntityParticipant( persister.getEntityName() );
+				final Set<String> roles = session.getFactory().getCollectionRolesByEntityParticipant( persister.getEntityName() );
 				if ( roles != null ) {
 					for ( String role : roles ) {
-						CollectionPersister collectionPersister = factory.getCollectionPersister( role );
+						final CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 						if ( collectionPersister.hasCache() ) {
 							collectionCleanups.add(
 									new CollectionCleanup( collectionPersister.getCacheAccessStrategy() )
 							);
 						}
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 
 	/**
 	 * Check to determine whether the table spaces reported by an entity
 	 * persister match against the defined affected table spaces.
 	 *
 	 * @param affectedTableSpaces The table spaces reported to be affected by
 	 * the query.
 	 * @param checkTableSpaces The table spaces (from the entity persister)
 	 * to check against the affected table spaces.
 	 *
 	 * @return True if there are affected table spaces and any of the incoming
 	 * check table spaces occur in that set.
 	 */
 	private boolean affectedEntity(Set affectedTableSpaces, Serializable[] checkTableSpaces) {
 		if ( affectedTableSpaces == null || affectedTableSpaces.isEmpty() ) {
 			return true;
 		}
 
 		for ( Serializable checkTableSpace : checkTableSpaces ) {
 			if ( affectedTableSpaces.contains( checkTableSpace ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public Serializable[] getPropertySpaces() {
 		return affectedTableSpaces;
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return new AfterTransactionCompletionProcess() {
 			@Override
 			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 				for ( EntityCleanup cleanup : entityCleanups ) {
 					cleanup.release();
 				}
 				entityCleanups.clear();
 
 				for ( NaturalIdCleanup cleanup : naturalIdCleanups ) {
 					cleanup.release();
 
 				}
 				entityCleanups.clear();
 
 				for ( CollectionCleanup cleanup : collectionCleanups ) {
 					cleanup.release();
 				}
 				collectionCleanups.clear();
 			}
 		};
 	}
 
 	@Override
 	public void beforeExecutions() throws HibernateException {
 		// nothing to do
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		// nothing to do		
 	}
 
 	private static class EntityCleanup {
 		private final EntityRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private EntityCleanup(EntityRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
 	private static class CollectionCleanup {
 		private final CollectionRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private CollectionCleanup(CollectionRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
 	private class NaturalIdCleanup {
 		private final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy;
 		private final SoftLock cacheLock;
 
 		public NaturalIdCleanup(NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy) {
 			this.naturalIdCacheAccessStrategy = naturalIdCacheAccessStrategy;
 			this.cacheLock = naturalIdCacheAccessStrategy.lockRegion();
 			naturalIdCacheAccessStrategy.removeAll();
 		}
 
 		private void release() {
 			naturalIdCacheAccessStrategy.unlockRegion( cacheLock );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
index 55884de903..dc36d245c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
@@ -1,216 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Any action relating to insert/update/delete of a collection
  *
  * @author Gavin King
  */
 public abstract class CollectionAction implements Executable, Serializable, Comparable {
 	private transient CollectionPersister persister;
 	private transient SessionImplementor session;
 	private final PersistentCollection collection;
 
 	private final Serializable key;
 	private final String collectionRole;
 
-	public CollectionAction(
-			final CollectionPersister persister, 
+	protected CollectionAction(
+			final CollectionPersister persister,
 			final PersistentCollection collection, 
 			final Serializable key, 
 			final SessionImplementor session) {
 		this.persister = persister;
 		this.session = session;
 		this.key = key;
 		this.collectionRole = persister.getRole();
 		this.collection = collection;
 	}
 
 	protected PersistentCollection getCollection() {
 		return collection;
 	}
 
 	/**
 	 * Reconnect to session after deserialization...
 	 *
 	 * @param session The session being deserialized
 	 */
 	public void afterDeserialize(SessionImplementor session) {
 		if ( this.session != null || this.persister != null ) {
 			throw new IllegalStateException( "already attached to a session." );
 		}
 		// IMPL NOTE: non-flushed changes code calls this method with session == null...
 		// guard against NullPointerException
 		if ( session != null ) {
 			this.session = session;
 			this.persister = session.getFactory().getCollectionPersister( collectionRole );
 		}
 	}
 
 	@Override
 	public final void beforeExecutions() throws CacheException {
 		// we need to obtain the lock before any actions are executed, since this may be an inverse="true"
 		// bidirectional association and it is one of the earlier entity actions which actually updates
 		// the database (this action is responsible for second-level cache invalidation only)
 		if ( persister.hasCache() ) {
 			final CacheKey ck = session.generateCacheKey(
 					key,
 					persister.getKeyType(),
 					persister.getRole()
 			);
 			final SoftLock lock = persister.getCacheAccessStrategy().lockItem( ck, null );
 			// the old behavior used key as opposed to getKey()
 			afterTransactionProcess = new CacheCleanupProcess( key, persister, lock );
 		}
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	private AfterTransactionCompletionProcess afterTransactionProcess;
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return afterTransactionProcess;
 	}
 
 	@Override
 	public Serializable[] getPropertySpaces() {
 		return persister.getCollectionSpaces();
 	}
 
 	protected final CollectionPersister getPersister() {
 		return persister;
 	}
 
 	protected final Serializable getKey() {
 		Serializable finalKey = key;
 		if ( key instanceof DelayedPostInsertIdentifier ) {
 			// need to look it up from the persistence-context
 			finalKey = session.getPersistenceContext().getEntry( collection.getOwner() ).getId();
 			if ( finalKey == key ) {
 				// we may be screwed here since the collection action is about to execute
 				// and we do not know the final owner key value
 			}
 		}
 		return finalKey;
 	}
 
 	protected final SessionImplementor getSession() {
 		return session;
 	}
 
 	protected final void evict() throws CacheException {
 		if ( persister.hasCache() ) {
-			CacheKey ck = session.generateCacheKey(
+			final CacheKey ck = session.generateCacheKey(
 					key, 
 					persister.getKeyType(), 
 					persister.getRole()
 			);
 			persister.getCacheAccessStrategy().remove( ck );
 		}
 	}
 
 	@Override
 	public String toString() {
-		return StringHelper.unqualify( getClass().getName() ) + 
-				MessageHelper.infoString( collectionRole, key );
+		return StringHelper.unqualify( getClass().getName() ) + MessageHelper.infoString( collectionRole, key );
 	}
 
 	@Override
 	public int compareTo(Object other) {
-		CollectionAction action = ( CollectionAction ) other;
-		//sort first by role name
-		int roleComparison = collectionRole.compareTo( action.collectionRole );
+		final CollectionAction action = (CollectionAction) other;
+
+		// sort first by role name
+		final int roleComparison = collectionRole.compareTo( action.collectionRole );
 		if ( roleComparison != 0 ) {
 			return roleComparison;
 		}
 		else {
 			//then by fk
-			return persister.getKeyType()
-					.compare( key, action.key );
+			return persister.getKeyType().compare( key, action.key );
 		}
 	}
 
 	private static class CacheCleanupProcess implements AfterTransactionCompletionProcess {
 		private final Serializable key;
 		private final CollectionPersister persister;
 		private final SoftLock lock;
 
 		private CacheCleanupProcess(Serializable key, CollectionPersister persister, SoftLock lock) {
 			this.key = key;
 			this.persister = persister;
 			this.lock = lock;
 		}
 
 		@Override
 		public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 			final CacheKey ck = session.generateCacheKey(
 					key,
 					persister.getKeyType(),
 					persister.getRole()
 			);
 			persister.getCacheAccessStrategy().unlockItem( ck, lock );
 		}
 	}
 
 	protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
 		return getSession()
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( eventType );
 	}
 
 	protected EventSource eventSource() {
 		return (EventSource) getSession();
 	}
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
index ea7767fd4b..5d6e64707f 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
@@ -1,102 +1,105 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
-import org.hibernate.cache.CacheException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCollectionRecreateEvent;
 import org.hibernate.event.spi.PostCollectionRecreateEventListener;
 import org.hibernate.event.spi.PreCollectionRecreateEvent;
 import org.hibernate.event.spi.PreCollectionRecreateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 
+/**
+ * The action for recreating a collection
+ */
 public final class CollectionRecreateAction extends CollectionAction {
 
+	/**
+	 * Constructs a CollectionRecreateAction
+	 *
+	 * @param collection The collection being recreated
+	 * @param persister The collection persister
+	 * @param id The collection key
+	 * @param session The session
+	 */
 	public CollectionRecreateAction(
 			final PersistentCollection collection,
 			final CollectionPersister persister,
 			final Serializable id,
-			final SessionImplementor session) throws CacheException {
+			final SessionImplementor session) {
 		super( persister, collection, id, session );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		// this method is called when a new non-null collection is persisted
 		// or when an existing (non-null) collection is moved to a new owner
 		final PersistentCollection collection = getCollection();
 		
 		preRecreate();
-
 		getPersister().recreate( collection, getKey(), getSession() );
-		
-		getSession().getPersistenceContext()
-				.getCollectionEntry(collection)
-				.afterAction(collection);
-		
+		getSession().getPersistenceContext().getCollectionEntry( collection ).afterAction( collection );
 		evict();
-
 		postRecreate();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
-			getSession().getFactory().getStatisticsImplementor()
-					.recreateCollection( getPersister().getRole() );
+			getSession().getFactory().getStatisticsImplementor().recreateCollection( getPersister().getRole() );
 		}
 	}
 
 	private void preRecreate() {
-		EventListenerGroup<PreCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_RECREATE );
+		final EventListenerGroup<PreCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_RECREATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionRecreateEvent event = new PreCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
 		for ( PreCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreRecreateCollection( event );
 		}
 	}
 
 	private void postRecreate() {
-		EventListenerGroup<PostCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_RECREATE );
+		final EventListenerGroup<PostCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_RECREATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionRecreateEvent event = new PostCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
 		for ( PostCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostRecreateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
index e341e18474..f77b55eec0 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
@@ -1,162 +1,160 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCollectionRemoveEvent;
 import org.hibernate.event.spi.PostCollectionRemoveEventListener;
 import org.hibernate.event.spi.PreCollectionRemoveEvent;
 import org.hibernate.event.spi.PreCollectionRemoveEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 
+/**
+ * The action for removing a collection
+ */
 public final class CollectionRemoveAction extends CollectionAction {
-
-	private boolean emptySnapshot;
 	private final Object affectedOwner;
-	
+	private boolean emptySnapshot;
+
 	/**
 	 * Removes a persistent collection from its loaded owner.
 	 *
 	 * Use this constructor when the collection is non-null.
 	 *
 	 * @param collection The collection to to remove; must be non-null
 	 * @param persister  The collection's persister
 	 * @param id The collection key
 	 * @param emptySnapshot Indicates if the snapshot is empty
 	 * @param session The session
 	 *
 	 * @throws AssertionFailure if collection is null.
 	 */
 	public CollectionRemoveAction(
 				final PersistentCollection collection,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, collection, id, session );
-		if (collection == null) {
+		if ( collection == null ) {
 			throw new AssertionFailure("collection == null");
 		}
 		this.emptySnapshot = emptySnapshot;
 		// the loaded owner will be set to null after the collection is removed,
 		// so capture its value as the affected owner so it is accessible to
 		// both pre- and post- events
 		this.affectedOwner = session.getPersistenceContext().getLoadedCollectionOwnerOrNull( collection );
 	}
 
 	/**
 	 * Removes a persistent collection from a specified owner.
 	 *
 	 * Use this constructor when the collection to be removed has not been loaded.
 	 *
 	 * @param affectedOwner The collection's owner; must be non-null
 	 * @param persister  The collection's persister
 	 * @param id The collection key
 	 * @param emptySnapshot Indicates if the snapshot is empty
 	 * @param session The session
 	 *
 	 * @throws AssertionFailure if affectedOwner is null.
 	 */
 	public CollectionRemoveAction(
 				final Object affectedOwner,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, null, id, session );
-		if (affectedOwner == null) {
+		if ( affectedOwner == null ) {
 			throw new AssertionFailure("affectedOwner == null");
 		}
 		this.emptySnapshot = emptySnapshot;
 		this.affectedOwner = affectedOwner;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		preRemove();
 
 		if ( !emptySnapshot ) {
 			// an existing collection that was either non-empty or uninitialized
 			// is replaced by null or a different collection
 			// (if the collection is uninitialized, hibernate has no way of
 			// knowing if the collection is actually empty without querying the db)
 			getPersister().remove( getKey(), getSession() );
 		}
 		
 		final PersistentCollection collection = getCollection();
-		if (collection!=null) {
-			getSession().getPersistenceContext()
-				.getCollectionEntry(collection)
-				.afterAction(collection);
+		if ( collection != null ) {
+			getSession().getPersistenceContext().getCollectionEntry( collection ).afterAction( collection );
 		}
-		
-		evict();
 
-		postRemove();		
+		evict();
+		postRemove();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
-			getSession().getFactory().getStatisticsImplementor()
-					.removeCollection( getPersister().getRole() );
+			getSession().getFactory().getStatisticsImplementor().removeCollection( getPersister().getRole() );
 		}
 	}
 
 	private void preRemove() {
-		EventListenerGroup<PreCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_REMOVE );
+		final EventListenerGroup<PreCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_REMOVE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionRemoveEvent event = new PreCollectionRemoveEvent(
 				getPersister(),
 				getCollection(),
 				eventSource(),
 				affectedOwner
 		);
 		for ( PreCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreRemoveCollection( event );
 		}
 	}
 
 	private void postRemove() {
-		EventListenerGroup<PostCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_REMOVE );
+		final EventListenerGroup<PostCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_REMOVE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionRemoveEvent event = new PostCollectionRemoveEvent(
 				getPersister(),
 				getCollection(),
 				eventSource(),
 				affectedOwner
 		);
 		for ( PostCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostRemoveCollection( event );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
index 31c565ce15..655bed0eff 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,139 +1,150 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCollectionUpdateEvent;
 import org.hibernate.event.spi.PostCollectionUpdateEventListener;
 import org.hibernate.event.spi.PreCollectionUpdateEvent;
 import org.hibernate.event.spi.PreCollectionUpdateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
+/**
+ * The action for updating a collection
+ */
 public final class CollectionUpdateAction extends CollectionAction {
-
 	private final boolean emptySnapshot;
 
+	/**
+	 * Constructs a CollectionUpdateAction
+	 *
+	 * @param collection The collection to update
+	 * @param persister The collection persister
+	 * @param id The collection key
+	 * @param emptySnapshot Indicates if the snapshot is empty
+	 * @param session The session
+	 */
 	public CollectionUpdateAction(
 				final PersistentCollection collection,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, collection, id, session );
 		this.emptySnapshot = emptySnapshot;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final Serializable id = getKey();
 		final SessionImplementor session = getSession();
 		final CollectionPersister persister = getPersister();
 		final PersistentCollection collection = getCollection();
-		boolean affectedByFilters = persister.isAffectedByEnabledFilters(session);
+		final boolean affectedByFilters = persister.isAffectedByEnabledFilters( session );
 
 		preUpdate();
 
 		if ( !collection.wasInitialized() ) {
-			if ( !collection.hasQueuedOperations() ) throw new AssertionFailure( "no queued adds" );
+			if ( !collection.hasQueuedOperations() ) {
+				throw new AssertionFailure( "no queued adds" );
+			}
 			//do nothing - we only need to notify the cache...
 		}
 		else if ( !affectedByFilters && collection.empty() ) {
-			if ( !emptySnapshot ) persister.remove( id, session );
+			if ( !emptySnapshot ) {
+				persister.remove( id, session );
+			}
 		}
-		else if ( collection.needsRecreate(persister) ) {
-			if (affectedByFilters) {
+		else if ( collection.needsRecreate( persister ) ) {
+			if ( affectedByFilters ) {
 				throw new HibernateException(
-					"cannot recreate collection while filter is enabled: " + 
-					MessageHelper.collectionInfoString(persister, collection,
-							id, session )
+						"cannot recreate collection while filter is enabled: " +
+								MessageHelper.collectionInfoString( persister, collection, id, session )
 				);
 			}
-			if ( !emptySnapshot ) persister.remove( id, session );
+			if ( !emptySnapshot ) {
+				persister.remove( id, session );
+			}
 			persister.recreate( collection, id, session );
 		}
 		else {
 			persister.deleteRows( collection, id, session );
 			persister.updateRows( collection, id, session );
 			persister.insertRows( collection, id, session );
 		}
 
-		getSession().getPersistenceContext()
-			.getCollectionEntry(collection)
-			.afterAction(collection);
-
+		getSession().getPersistenceContext().getCollectionEntry( collection ).afterAction( collection );
 		evict();
-
 		postUpdate();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
-			getSession().getFactory().getStatisticsImplementor().
-					updateCollection( getPersister().getRole() );
+			getSession().getFactory().getStatisticsImplementor().updateCollection( getPersister().getRole() );
 		}
 	}
 	
 	private void preUpdate() {
-		EventListenerGroup<PreCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_UPDATE );
+		final EventListenerGroup<PreCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionUpdateEvent event = new PreCollectionUpdateEvent(
 				getPersister(),
 				getCollection(),
 				eventSource()
 		);
 		for ( PreCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreUpdateCollection( event );
 		}
 	}
 
 	private void postUpdate() {
-		EventListenerGroup<PostCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_UPDATE );
+		final EventListenerGroup<PostCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionUpdateEvent event = new PostCollectionUpdateEvent(
 				getPersister(),
 				getCollection(),
 				eventSource()
 		);
 		for ( PostCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostUpdateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java b/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java
index 37a29f374c..785e38f25c 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java
@@ -1,75 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 /**
  * Acts as a stand-in for an entity identifier which is supposed to be
  * generated on insert (like an IDENTITY column) where the insert needed to
  * be delayed because we were outside a transaction when the persist
  * occurred (save currently still performs the insert).
  * <p/>
  * The stand-in is only used within the {@link org.hibernate.engine.spi.PersistenceContext}
  * in order to distinguish one instance from another; it is never injected into
  * the entity instance or returned to the client...
  *
  * @author Steve Ebersole
  */
 public class DelayedPostInsertIdentifier implements Serializable {
-	private static long SEQUENCE = 0;
-	private final long sequence;
+	private static long sequence;
 
+	private final long identifier;
+
+	/**
+	 * Constructs a DelayedPostInsertIdentifier
+	 */
 	public DelayedPostInsertIdentifier() {
 		synchronized( DelayedPostInsertIdentifier.class ) {
-			if ( SEQUENCE == Long.MAX_VALUE ) {
-				SEQUENCE = 0;
+			if ( sequence == Long.MAX_VALUE ) {
+				sequence = 0;
 			}
-			this.sequence = SEQUENCE++;
+			this.identifier = sequence++;
 		}
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
-		final DelayedPostInsertIdentifier that = ( DelayedPostInsertIdentifier ) o;
-		return sequence == that.sequence;
+		final DelayedPostInsertIdentifier that = (DelayedPostInsertIdentifier) o;
+		return identifier == that.identifier;
 	}
 
 	@Override
 	public int hashCode() {
-		return ( int ) ( sequence ^ ( sequence >>> 32 ) );
+		return (int) ( identifier ^ ( identifier >>> 32 ) );
 	}
 
 	@Override
 	public String toString() {
-		return "<delayed:" + sequence + ">";
+		return "<delayed:" + identifier + ">";
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
index ddaa7f44d4..5b29ddd326 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
@@ -1,205 +1,205 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Base class for actions relating to insert/update/delete of an entity
  * instance.
  *
  * @author Gavin King
  */
 public abstract class EntityAction
 		implements Executable, Serializable, Comparable, AfterTransactionCompletionProcess {
 
 	private final String entityName;
 	private final Serializable id;
 
 	private transient Object instance;
 	private transient SessionImplementor session;
 	private transient EntityPersister persister;
 
 	/**
 	 * Instantiate an action.
 	 *
 	 * @param session The session from which this action is coming.
 	 * @param id The id of the entity
 	 * @param instance The entity instance
 	 * @param persister The entity persister
 	 */
 	protected EntityAction(SessionImplementor session, Serializable id, Object instance, EntityPersister persister) {
 		this.entityName = persister.getEntityName();
 		this.id = id;
 		this.instance = instance;
 		this.session = session;
 		this.persister = persister;
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return needsAfterTransactionCompletion()
 				? this
 				: null;
 	}
 
 	protected abstract boolean hasPostCommitEventListeners();
 
-	public boolean needsAfterTransactionCompletion() {
+	protected boolean needsAfterTransactionCompletion() {
 		return persister.hasCache() || hasPostCommitEventListeners();
 	}
 
 	/**
 	 * entity name accessor
 	 *
 	 * @return The entity name
 	 */
 	public String getEntityName() {
 		return entityName;
 	}
 
 	/**
 	 * entity id accessor
 	 *
 	 * @return The entity id
 	 */
 	public final Serializable getId() {
 		if ( id instanceof DelayedPostInsertIdentifier ) {
-			Serializable eeId = session.getPersistenceContext().getEntry( instance ).getId();
+			final Serializable eeId = session.getPersistenceContext().getEntry( instance ).getId();
 			return eeId instanceof DelayedPostInsertIdentifier ? null : eeId;
 		}
 		return id;
 	}
 
 	public final DelayedPostInsertIdentifier getDelayedId() {
-		return DelayedPostInsertIdentifier.class.isInstance( id ) ?
-				DelayedPostInsertIdentifier.class.cast( id ) :
-				null;
+		return DelayedPostInsertIdentifier.class.isInstance( id )
+				? DelayedPostInsertIdentifier.class.cast( id )
+				: null;
 	}
 
 	/**
 	 * entity instance accessor
 	 *
 	 * @return The entity instance
 	 */
 	public final Object getInstance() {
 		return instance;
 	}
 
 	/**
 	 * originating session accessor
 	 *
 	 * @return The session from which this action originated.
 	 */
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	/**
 	 * entity persister accessor
 	 *
 	 * @return The entity persister
 	 */
 	public final EntityPersister getPersister() {
 		return persister;
 	}
 
 	@Override
 	public final Serializable[] getPropertySpaces() {
 		return persister.getPropertySpaces();
 	}
 
 	@Override
 	public void beforeExecutions() {
 		throw new AssertionFailure( "beforeExecutions() called for non-collection action" );
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + MessageHelper.infoString( entityName, id );
 	}
 
 	@Override
 	public int compareTo(Object other) {
-		EntityAction action = ( EntityAction ) other;
+		final EntityAction action = (EntityAction) other;
 		//sort first by entity name
-		int roleComparison = entityName.compareTo( action.entityName );
+		final int roleComparison = entityName.compareTo( action.entityName );
 		if ( roleComparison != 0 ) {
 			return roleComparison;
 		}
 		else {
 			//then by id
 			return persister.getIdentifierType().compare( id, action.id );
 		}
 	}
 
 	/**
 	 * Reconnect to session after deserialization...
 	 *
 	 * @param session The session being deserialized
 	 */
 	public void afterDeserialize(SessionImplementor session) {
 		if ( this.session != null || this.persister != null ) {
 			throw new IllegalStateException( "already attached to a session." );
 		}
 		// IMPL NOTE: non-flushed changes code calls this method with session == null...
 		// guard against NullPointerException
 		if ( session != null ) {
 			this.session = session;
 			this.persister = session.getFactory().getEntityPersister( entityName );
 			this.instance = session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 		}
 	}
 
 	protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
 		return getSession()
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( eventType );
 	}
 
 	protected EventSource eventSource() {
 		return (EventSource) getSession();
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
index ed1ae724f8..9cd13d1524 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
@@ -1,202 +1,214 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostDeleteEvent;
 import org.hibernate.event.spi.PostDeleteEventListener;
-import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PreDeleteEvent;
 import org.hibernate.event.spi.PreDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
+/**
+ * The action for performing an entity deletion.
+ */
 public final class EntityDeleteAction extends EntityAction {
 	private final Object version;
 	private final boolean isCascadeDeleteEnabled;
 	private final Object[] state;
 
 	private SoftLock lock;
 	private Object[] naturalIdValues;
 
+	/**
+	 * Constructs an EntityDeleteAction.
+	 *
+	 * @param id The entity identifier
+	 * @param state The current (extracted) entity state
+	 * @param version The current entity version
+	 * @param instance The entity instance
+	 * @param persister The entity persister
+	 * @param isCascadeDeleteEnabled Whether cascade delete is enabled
+	 * @param session The session
+	 */
 	public EntityDeleteAction(
 			final Serializable id,
-	        final Object[] state,
-	        final Object version,
-	        final Object instance,
-	        final EntityPersister persister,
-	        final boolean isCascadeDeleteEnabled,
-	        final SessionImplementor session) {
+			final Object[] state,
+			final Object version,
+			final Object instance,
+			final EntityPersister persister,
+			final boolean isCascadeDeleteEnabled,
+			final SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.version = version;
 		this.isCascadeDeleteEnabled = isCascadeDeleteEnabled;
 		this.state = state;
 
 		// before remove we need to remove the local (transactional) natural id cross-reference
 		naturalIdValues = session.getPersistenceContext().getNaturalIdHelper().removeLocalNaturalIdCrossReference(
 				getPersister(),
 				getId(),
 				state
 		);
 	}
 
 	@Override
 	public void execute() throws HibernateException {
-		Serializable id = getId();
-		EntityPersister persister = getPersister();
-		SessionImplementor session = getSession();
-		Object instance = getInstance();
+		final Serializable id = getId();
+		final EntityPersister persister = getPersister();
+		final SessionImplementor session = getSession();
+		final Object instance = getInstance();
 
-		boolean veto = preDelete();
+		final boolean veto = preDelete();
 
 		Object version = this.version;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			version = persister.getVersion( instance );
 		}
 
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 			lock = persister.getCacheAccessStrategy().lockItem( ck, version );
 		}
 		else {
 			ck = null;
 		}
 
 		if ( !isCascadeDeleteEnabled && !veto ) {
 			persister.delete( id, version, instance, session );
 		}
 		
 		//postDelete:
 		// After actually deleting a row, record the fact that the instance no longer 
 		// exists on the database (needed for identity-column key generation), and
 		// remove it from the session cache
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
-		EntityEntry entry = persistenceContext.removeEntry( instance );
+		final EntityEntry entry = persistenceContext.removeEntry( instance );
 		if ( entry == null ) {
 			throw new AssertionFailure( "possible nonthreadsafe access to session" );
 		}
 		entry.postDelete();
 
 		persistenceContext.removeEntity( entry.getEntityKey() );
 		persistenceContext.removeProxy( entry.getEntityKey() );
 		
 		if ( persister.hasCache() ) {
 			persister.getCacheAccessStrategy().remove( ck );
 		}
 
 		persistenceContext.getNaturalIdHelper().removeSharedNaturalIdCrossReference( persister, id, naturalIdValues );
 
 		postDelete();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() && !veto ) {
 			getSession().getFactory().getStatisticsImplementor().deleteEntity( getPersister().getEntityName() );
 		}
 	}
 
 	private boolean preDelete() {
 		boolean veto = false;
-		EventListenerGroup<PreDeleteEventListener> listenerGroup = listenerGroup( EventType.PRE_DELETE );
+		final EventListenerGroup<PreDeleteEventListener> listenerGroup = listenerGroup( EventType.PRE_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreDeleteEvent event = new PreDeleteEvent( getInstance(), getId(), state, getPersister(), eventSource() );
 		for ( PreDeleteEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreDelete( event );
 		}
 		return veto;
 	}
 
 	private void postDelete() {
-		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_DELETE );
+		final EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostDeleteEvent event = new PostDeleteEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostDeleteEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostDelete( event );
 		}
 	}
 
 	private void postCommitDelete() {
-		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_DELETE );
+		final EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostDeleteEvent event = new PostDeleteEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for( PostDeleteEventListener listener : listenerGroup.listeners() ){
 			listener.onPostDelete( event );
 		}
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
 		if ( getPersister().hasCache() ) {
 			final CacheKey ck = getSession().generateCacheKey(
 					getId(),
 					getPersister().getIdentifierType(),
 					getPersister().getRootEntityName()
 			);
 			getPersister().getCacheAccessStrategy().unlockItem( ck, lock );
 		}
 		postCommitDelete();
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostDeleteEventListener> group = listenerGroup( EventType.POST_COMMIT_DELETE );
 		for ( PostDeleteEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
index 54b325782f..2edcd836a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
@@ -1,219 +1,249 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PreInsertEvent;
 import org.hibernate.event.spi.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
+/**
+ * The action for performing entity insertions when entity is using IDENTITY column identifier generation
+ *
+ * @see EntityInsertAction
+ */
 public final class EntityIdentityInsertAction extends AbstractEntityInsertAction  {
 
 	private final boolean isDelayed;
 	private final EntityKey delayedEntityKey;
 	private EntityKey entityKey;
-	//private CacheEntry cacheEntry;
 	private Serializable generatedId;
 
+	/**
+	 * Constructs an EntityIdentityInsertAction
+	 *
+	 * @param state The current (extracted) entity state
+	 * @param instance The entity instance
+	 * @param persister The entity persister
+	 * @param isVersionIncrementDisabled Whether version incrementing is disabled
+	 * @param session The session
+	 * @param isDelayed Are we in a situation which allows the insertion to be delayed?
+	 *
+	 * @throws HibernateException Indicates an illegal state
+	 */
 	public EntityIdentityInsertAction(
 			Object[] state,
 			Object instance,
 			EntityPersister persister,
 			boolean isVersionIncrementDisabled,
 			SessionImplementor session,
-			boolean isDelayed) throws HibernateException {
+			boolean isDelayed) {
 		super(
 				( isDelayed ? generateDelayedPostInsertIdentifier() : null ),
 				state,
 				instance,
 				isVersionIncrementDisabled,
 				persister,
 				session
 		);
 		this.isDelayed = isDelayed;
 		this.delayedEntityKey = isDelayed ? generateDelayedEntityKey() : null;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		nullifyTransientReferencesIfNotAlready();
 
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 
-		boolean veto = preInsert();
+		final boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			generatedId = persister.insert( getState(), instance, session );
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( generatedId, instance, getState(), session );
 			}
 			//need to do that here rather than in the save event listener to let
 			//the post insert events to have a id-filled entity when IDENTITY is used (EJB3)
 			persister.setIdentifier( instance, generatedId, session );
 			session.getPersistenceContext().registerInsertedKey( getPersister(), generatedId );
 			entityKey = session.generateEntityKey( generatedId, persister );
 			session.getPersistenceContext().checkUniqueness( entityKey, getInstance() );
 		}
 
 
 		//TODO: this bit actually has to be called after all cascades!
 		//      but since identity insert is called *synchronously*,
 		//      instead of asynchronously as other actions, it isn't
 		/*if ( persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
 			cacheEntry = new CacheEntry(object, persister, session);
 			persister.getCache().insert(generatedId, cacheEntry);
 		}*/
 
 		postInsert();
 
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() && !veto ) {
 			session.getFactory().getStatisticsImplementor().insertEntity( getPersister().getEntityName() );
 		}
 
 		markExecuted();
 	}
 
 	@Override
-    public boolean needsAfterTransactionCompletion() {
+	public boolean needsAfterTransactionCompletion() {
 		//TODO: simply remove this override if we fix the above todos
 		return hasPostCommitEventListeners();
 	}
 
 	@Override
-    protected boolean hasPostCommitEventListeners() {
+	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostInsertEventListener> group = listenerGroup( EventType.POST_COMMIT_INSERT );
 		for ( PostInsertEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 		//TODO: reenable if we also fix the above todo
 		/*EntityPersister persister = getEntityPersister();
 		if ( success && persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
 			persister.getCache().afterInsert( getGeneratedId(), cacheEntry );
 		}*/
 		postCommitInsert();
 	}
 
 	private void postInsert() {
 		if ( isDelayed ) {
 			getSession().getPersistenceContext().replaceDelayedEntityIdentityInsertKeys( delayedEntityKey, generatedId );
 		}
 
-		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
+		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				generatedId,
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
-		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
+		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				generatedId,
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
-		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
+		final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
 		if ( listenerGroup.isEmpty() ) {
-			return false; // NO_VETO
+			// NO_VETO
+			return false;
 		}
 		boolean veto = false;
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), null, getState(), getPersister(), eventSource() );
 		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreInsert( event );
 		}
 		return veto;
 	}
 
+	/**
+	 * Access to the generated identifier
+	 *
+	 * @return The generated identifier
+	 */
 	public final Serializable getGeneratedId() {
 		return generatedId;
 	}
 
-	// TODO: nothing seems to use this method; can it be renmoved?
+	/**
+	 * Access to the delayed entity key
+	 *
+	 * @return The delayed entity key
+	 *
+	 * @deprecated No Hibernate code currently uses this method
+	 */
+	@Deprecated
+	@SuppressWarnings("UnusedDeclaration")
 	public EntityKey getDelayedEntityKey() {
 		return delayedEntityKey;
 	}
 
 	@Override
 	public boolean isEarlyInsert() {
 		return !isDelayed;
 	}
 
 	@Override
 	protected EntityKey getEntityKey() {
 		return entityKey != null ? entityKey : delayedEntityKey;
 	}
 
-	private synchronized static DelayedPostInsertIdentifier generateDelayedPostInsertIdentifier() {
+	private static synchronized DelayedPostInsertIdentifier generateDelayedPostInsertIdentifier() {
 		return new DelayedPostInsertIdentifier();
 	}
 
 	private EntityKey generateDelayedEntityKey() {
 		if ( !isDelayed ) {
 			throw new AssertionFailure( "cannot request delayed entity-key for early-insert post-insert-id generation" );
 		}
 		return getSession().generateEntityKey( getDelayedId(), getPersister() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java
index d50532c801..ca06a02de8 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java
@@ -1,58 +1,63 @@
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
 package org.hibernate.action.internal;
 
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
- * Verify/Increment the entity version
+ * A BeforeTransactionCompletionProcess impl to verify and increment an entity version as party
+ * of before-transaction-completion processing
  *
  * @author Scott Marlow
  */
 public class EntityIncrementVersionProcess implements BeforeTransactionCompletionProcess {
 	private final Object object;
 	private final EntityEntry entry;
 
+	/**
+	 * Constructs an EntityIncrementVersionProcess for the given entity.
+	 *
+	 * @param object The entity instance
+	 * @param entry The entity's EntityEntry reference
+	 */
 	public EntityIncrementVersionProcess(Object object, EntityEntry entry) {
 		this.object = object;
 		this.entry = entry;
 	}
 
 	/**
 	 * Perform whatever processing is encapsulated here before completion of the transaction.
 	 *
 	 * @param session The session on which the transaction is preparing to complete.
 	 */
 	@Override
 	public void doBeforeTransactionCompletion(SessionImplementor session) {
 		final EntityPersister persister = entry.getPersister();
-		Object nextVersion = persister.forceVersionIncrement(
-				entry.getId(), entry.getVersion(), session
-		);
+		final Object nextVersion = persister.forceVersionIncrement( entry.getId(), entry.getVersion(), session );
 		entry.forceLocked( object, nextVersion );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
index ea645fcdc8..fba5c3e6ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,219 +1,235 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PreInsertEvent;
 import org.hibernate.event.spi.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
+/**
+ * The action for performing an entity insertion, for entities not defined to use IDENTITY generation.
+ *
+ * @see EntityIdentityInsertAction
+ */
 public final class EntityInsertAction extends AbstractEntityInsertAction {
 
 	private Object version;
 	private Object cacheEntry;
 
+	/**
+	 * Constructs an EntityInsertAction.
+	 *
+	 * @param id The entity identifier
+	 * @param state The current (extracted) entity state
+	 * @param instance The entity instance
+	 * @param version The current entity version value
+	 * @param persister The entity's persister
+	 * @param isVersionIncrementDisabled Whether version incrementing is disabled.
+	 * @param session The session
+	 */
 	public EntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			Object version,
 			EntityPersister persister,
 			boolean isVersionIncrementDisabled,
-			SessionImplementor session) throws HibernateException {
+			SessionImplementor session) {
 		super( id, state, instance, isVersionIncrementDisabled, persister, session );
 		this.version = version;
 	}
 
 	@Override
 	public boolean isEarlyInsert() {
 		return false;
 	}
 
 	@Override
 	protected EntityKey getEntityKey() {
 		return getSession().generateEntityKey( getId(), getPersister() );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		nullifyTransientReferencesIfNotAlready();
 
-		EntityPersister persister = getPersister();
-		SessionImplementor session = getSession();
-		Object instance = getInstance();
-		Serializable id = getId();
+		final EntityPersister persister = getPersister();
+		final SessionImplementor session = getSession();
+		final Object instance = getInstance();
+		final Serializable id = getId();
 
-		boolean veto = preInsert();
+		final boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			
 			persister.insert( id, getState(), instance, session );
-		
-			EntityEntry entry = session.getPersistenceContext().getEntry( instance );
+
+			final EntityEntry entry = session.getPersistenceContext().getEntry( instance );
 			if ( entry == null ) {
 				throw new AssertionFailure( "possible non-threadsafe access to session" );
 			}
 			
 			entry.postInsert( getState() );
 	
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( id, instance, getState(), session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					version = Versioning.getVersion( getState(), persister );
 				}
-				entry.postUpdate(instance, getState(), version);
+				entry.postUpdate( instance, getState(), version );
 			}
 
 			getSession().getPersistenceContext().registerInsertedKey( getPersister(), getId() );
 		}
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 
 		if ( isCachePutEnabled( persister, session ) ) {
-			CacheEntry ce = persister.buildCacheEntry(
+			final CacheEntry ce = persister.buildCacheEntry(
 					instance,
 					getState(),
 					version,
 					session
 			);
 			cacheEntry = persister.getCacheEntryStructure().structure(ce);
 			final CacheKey ck = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
-			boolean put = persister.getCacheAccessStrategy().insert( ck, cacheEntry, version );
+			final boolean put = persister.getCacheAccessStrategy().insert( ck, cacheEntry, version );
 			
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 			}
 		}
 
-		handleNaturalIdPostSaveNotifications(id);
+		handleNaturalIdPostSaveNotifications( id );
 
 		postInsert();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor()
 					.insertEntity( getPersister().getEntityName() );
 		}
 
 		markExecuted();
 	}
 
 	private void postInsert() {
-		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
+		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
-		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
+		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
 		boolean veto = false;
 
-		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
+		final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), getId(), getState(), getPersister(), eventSource() );
 		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreInsert( event );
 		}
 		return veto;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
-		EntityPersister persister = getPersister();
+		final EntityPersister persister = getPersister();
 		if ( success && isCachePutEnabled( persister, getSession() ) ) {
 			final CacheKey ck = getSession().generateCacheKey( getId(), persister.getIdentifierType(), persister.getRootEntityName() );
-			boolean put = persister.getCacheAccessStrategy().afterInsert( ck, cacheEntry, version );
+			final boolean put = persister.getCacheAccessStrategy().afterInsert( ck, cacheEntry, version );
 			
 			if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 				getSession().getFactory().getStatisticsImplementor()
 						.secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 			}
 		}
 		postCommitInsert();
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostInsertEventListener> group = listenerGroup( EventType.POST_COMMIT_INSERT );
 		for ( PostInsertEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 	
 	private boolean isCachePutEnabled(EntityPersister persister, SessionImplementor session) {
 		return persister.hasCache()
 				&& !persister.isCacheInvalidationRequired()
 				&& session.getCacheMode().isPutEnabled();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
index fd875fbb27..9b1c763693 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,309 +1,326 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostUpdateEvent;
 import org.hibernate.event.spi.PostUpdateEventListener;
 import org.hibernate.event.spi.PreUpdateEvent;
 import org.hibernate.event.spi.PreUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.TypeHelper;
 
+/**
+ * The action for performing entity updates.
+ */
 public final class EntityUpdateAction extends EntityAction {
 	private final Object[] state;
 	private final Object[] previousState;
 	private final Object previousVersion;
 	private final int[] dirtyFields;
 	private final boolean hasDirtyCollection;
 	private final Object rowId;
 	private final Object[] previousNaturalIdValues;
 	private Object nextVersion;
 	private Object cacheEntry;
 	private SoftLock lock;
 
+	/**
+	 * Constructs an EntityUpdateAction
+	 *
+	 * @param id The entity identifier
+	 * @param state The current (extracted) entity state
+	 * @param dirtyProperties The indexes (in reference to state) properties with dirty state
+	 * @param hasDirtyCollection Were any collections dirty?
+	 * @param previousState The previous (stored) state
+	 * @param previousVersion The previous (stored) version
+	 * @param nextVersion The incremented version
+	 * @param instance The entity instance
+	 * @param rowId The entity's rowid
+	 * @param persister The entity's persister
+	 * @param session The session
+	 */
 	public EntityUpdateAction(
-	        final Serializable id,
-	        final Object[] state,
-	        final int[] dirtyProperties,
-	        final boolean hasDirtyCollection,
-	        final Object[] previousState,
-	        final Object previousVersion,
-	        final Object nextVersion,
-	        final Object instance,
-	        final Object rowId,
-	        final EntityPersister persister,
-	        final SessionImplementor session) throws HibernateException {
+			final Serializable id,
+			final Object[] state,
+			final int[] dirtyProperties,
+			final boolean hasDirtyCollection,
+			final Object[] previousState,
+			final Object previousVersion,
+			final Object nextVersion,
+			final Object instance,
+			final Object rowId,
+			final EntityPersister persister,
+			final SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.previousState = previousState;
 		this.previousVersion = previousVersion;
 		this.nextVersion = nextVersion;
 		this.dirtyFields = dirtyProperties;
 		this.hasDirtyCollection = hasDirtyCollection;
 		this.rowId = rowId;
 
 		this.previousNaturalIdValues = determinePreviousNaturalIdValues( persister, previousState, session, id );
 		session.getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	private Object[] determinePreviousNaturalIdValues(
 			EntityPersister persister,
 			Object[] previousState,
 			SessionImplementor session,
 			Serializable id) {
 		if ( ! persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		if ( previousState != null ) {
 			return session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( previousState, persister );
 		}
 
 		return session.getPersistenceContext().getNaturalIdSnapshot( id, persister );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
-		Serializable id = getId();
-		EntityPersister persister = getPersister();
-		SessionImplementor session = getSession();
-		Object instance = getInstance();
+		final Serializable id = getId();
+		final EntityPersister persister = getPersister();
+		final SessionImplementor session = getSession();
+		final Object instance = getInstance();
 
-		boolean veto = preUpdate();
+		final boolean veto = preUpdate();
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 		Object previousVersion = this.previousVersion;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			previousVersion = persister.getVersion( instance );
 		}
 		
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = session.generateCacheKey(
 					id, 
 					persister.getIdentifierType(), 
 					persister.getRootEntityName()
 			);
 			lock = persister.getCacheAccessStrategy().lockItem( ck, previousVersion );
 		}
 		else {
 			ck = null;
 		}
 
 		if ( !veto ) {
 			persister.update( 
 					id, 
 					state, 
 					dirtyFields, 
 					hasDirtyCollection, 
 					previousState, 
 					previousVersion, 
 					instance, 
 					rowId, 
 					session 
 			);
 		}
 
-		EntityEntry entry = getSession().getPersistenceContext().getEntry( instance );
+		final EntityEntry entry = getSession().getPersistenceContext().getEntry( instance );
 		if ( entry == null ) {
 			throw new AssertionFailure( "possible nonthreadsafe access to session" );
 		}
 		
 		if ( entry.getStatus()==Status.MANAGED || persister.isVersionPropertyGenerated() ) {
 			// get the updated snapshot of the entity state by cloning current state;
 			// it is safe to copy in place, since by this time no-one else (should have)
 			// has a reference  to the array
 			TypeHelper.deepCopy(
 					state,
 					persister.getPropertyTypes(),
 					persister.getPropertyCheckability(),
 					state,
 					session
 			);
 			if ( persister.hasUpdateGeneratedProperties() ) {
 				// this entity defines proeprty generation, so process those generated
 				// values...
 				persister.processUpdateGeneratedProperties( id, instance, state, session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					nextVersion = Versioning.getVersion( state, persister );
 				}
 			}
 			// have the entity entry doAfterTransactionCompletion post-update processing, passing it the
 			// update state and the new version (if one).
 			entry.postUpdate( instance, state, nextVersion );
 		}
 
 		if ( persister.hasCache() ) {
 			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!= Status.MANAGED ) {
 				persister.getCacheAccessStrategy().remove( ck );
 			}
 			else {
 				//TODO: inefficient if that cache is just going to ignore the updated state!
-				CacheEntry ce = persister.buildCacheEntry( instance,state, nextVersion, getSession() );
+				final CacheEntry ce = persister.buildCacheEntry( instance,state, nextVersion, getSession() );
 				cacheEntry = persister.getCacheEntryStructure().structure( ce );
-				boolean put = persister.getCacheAccessStrategy().update( ck, cacheEntry, nextVersion, previousVersion );
+				final boolean put = persister.getCacheAccessStrategy().update( ck, cacheEntry, nextVersion, previousVersion );
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		session.getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 
 		postUpdate();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
-			factory.getStatisticsImplementor()
-					.updateEntity( getPersister().getEntityName() );
+			factory.getStatisticsImplementor().updateEntity( getPersister().getEntityName() );
 		}
 	}
 
 	private boolean preUpdate() {
 		boolean veto = false;
-		EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
+		final EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreUpdateEvent event = new PreUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				getPersister(),
 				eventSource()
 		);
 		for ( PreUpdateEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreUpdate( event );
 		}
 		return veto;
 	}
 
 	private void postUpdate() {
-		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
+		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostUpdateEvent event = new PostUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				dirtyFields,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostUpdate( event );
 		}
 	}
 
 	private void postCommitUpdate() {
-		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
+		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostUpdateEvent event = new PostUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				dirtyFields,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostUpdate( event );
 		}
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostUpdateEventListener> group = listenerGroup( EventType.POST_COMMIT_UPDATE );
 		for ( PostUpdateEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws CacheException {
-		EntityPersister persister = getPersister();
+		final EntityPersister persister = getPersister();
 		if ( persister.hasCache() ) {
 			
 			final CacheKey ck = getSession().generateCacheKey(
-					getId(), 
+					getId(),
 					persister.getIdentifierType(), 
 					persister.getRootEntityName()
-				);
+			);
 			
 			if ( success && cacheEntry!=null /*!persister.isCacheInvalidationRequired()*/ ) {
-				boolean put = persister.getCacheAccessStrategy().afterUpdate( ck, cacheEntry, nextVersion, previousVersion, lock );
+				final boolean put = persister.getCacheAccessStrategy().afterUpdate( ck, cacheEntry, nextVersion, previousVersion, lock );
 				
 				if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 					getSession().getFactory().getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 			else {
 				persister.getCacheAccessStrategy().unlockItem( ck, lock );
 			}
 		}
 		postCommitUpdate();
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java
index 85b99a30da..6c1d694195 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java
@@ -1,62 +1,68 @@
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
 package org.hibernate.action.internal;
 
 import org.hibernate.OptimisticLockException;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
- * Verify/Increment the entity version
+ * A BeforeTransactionCompletionProcess impl to verify an entity version as part of
+ * before-transaction-completion processing
  *
  * @author Scott Marlow
  */
 public class EntityVerifyVersionProcess implements BeforeTransactionCompletionProcess {
-	@SuppressWarnings( {"FieldCanBeLocal", "UnusedDeclaration"})
 	private final Object object;
 	private final EntityEntry entry;
 
+	/**
+	 * Constructs an EntityVerifyVersionProcess
+	 *
+	 * @param object The entity instance
+	 * @param entry The entity's referenced EntityEntry
+	 */
 	public EntityVerifyVersionProcess(Object object, EntityEntry entry) {
 		this.object = object;
 		this.entry = entry;
 	}
 
 	@Override
 	public void doBeforeTransactionCompletion(SessionImplementor session) {
 		final EntityPersister persister = entry.getPersister();
 
-		Object latestVersion = persister.getCurrentVersion( entry.getId(), session );
+		final Object latestVersion = persister.getCurrentVersion( entry.getId(), session );
 		if ( !entry.getVersion().equals( latestVersion ) ) {
 			throw new OptimisticLockException(
 					object,
 					"Newer version [" + latestVersion +
 							"] of entity [" + MessageHelper.infoString( entry.getEntityName(), entry.getId() ) +
 							"] found in database"
 			);
 		}
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java b/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
index e6e18e9acf..99fbaae36f 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/UnresolvedEntityInsertActions.java
@@ -1,329 +1,329 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.action.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.PropertyValueException;
 import org.hibernate.TransientPropertyValueException;
 import org.hibernate.engine.internal.NonNullableTransientDependencies;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Tracks unresolved entity insert actions.
  *
  * An entity insert action is unresolved if the entity
  * to be inserted has at least one non-nullable association with
  * an unsaved transient entity, and the foreign key points to that
  * unsaved transient entity.
  *
  * These references must be resolved before an insert action can be
  * executed.
  *
  * @author Gail Badner
  */
 public class UnresolvedEntityInsertActions {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 				CoreMessageLogger.class,
 				UnresolvedEntityInsertActions.class.getName()
-		);
+	);
+
 	private static final int INIT_SIZE = 5;
 
 	private final Map<AbstractEntityInsertAction,NonNullableTransientDependencies> dependenciesByAction =
 			new IdentityHashMap<AbstractEntityInsertAction,NonNullableTransientDependencies>( INIT_SIZE );
 	private final Map<Object,Set<AbstractEntityInsertAction>> dependentActionsByTransientEntity =
 			new IdentityHashMap<Object,Set<AbstractEntityInsertAction>>( INIT_SIZE );
 
 	/**
 	 * Add an unresolved insert action.
 	 *
 	 * @param insert - unresolved insert action.
 	 * @param dependencies - non-nullable transient dependencies
 	 *                       (must be non-null and non-empty).
 	 *
 	 * @throws IllegalArgumentException if {@code dependencies is null or empty}.
 	 */
 	public void addUnresolvedEntityInsertAction(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
 		if ( dependencies == null || dependencies.isEmpty() ) {
 			throw new IllegalArgumentException(
 					"Attempt to add an unresolved insert action that has no non-nullable transient entities."
 			);
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Adding insert with non-nullable, transient entities; insert=[{0}], dependencies=[{1}]",
 					insert,
 					dependencies.toLoggableString( insert.getSession() )
 			);
 		}
 		dependenciesByAction.put( insert, dependencies );
 		addDependenciesByTransientEntity( insert, dependencies );
 	}
 
 	/**
 	 * Returns the unresolved insert actions.
 	 * @return the unresolved insert actions.
 	 */
 	public Iterable<AbstractEntityInsertAction> getDependentEntityInsertActions() {
 		return dependenciesByAction.keySet();
 	}
 
 	/**
 	 * Throws {@link org.hibernate.PropertyValueException} if there are any unresolved
 	 * entity insert actions that depend on non-nullable associations with
 	 * a transient entity. This method should be called on completion of
 	 * an operation (after all cascades are completed) that saves an entity.
 	 *
 	 * @throws org.hibernate.PropertyValueException if there are any unresolved entity
 	 * insert actions; {@link org.hibernate.PropertyValueException#getEntityName()}
 	 * and {@link org.hibernate.PropertyValueException#getPropertyName()} will
 	 * return the entity name and property value for the first unresolved
 	 * entity insert action.
 	 */
 	public void checkNoUnresolvedActionsAfterOperation() throws PropertyValueException {
 		if ( isEmpty() ) {
 			LOG.trace( "No entity insert actions have non-nullable, transient entity dependencies." );
 		}
 		else {
-			AbstractEntityInsertAction firstDependentAction =
+			final AbstractEntityInsertAction firstDependentAction =
 					dependenciesByAction.keySet().iterator().next();
 
 			logCannotResolveNonNullableTransientDependencies( firstDependentAction.getSession() );
 
-			NonNullableTransientDependencies nonNullableTransientDependencies =
+			final NonNullableTransientDependencies nonNullableTransientDependencies =
 					dependenciesByAction.get( firstDependentAction );
-			Object firstTransientDependency =
+			final Object firstTransientDependency =
 					nonNullableTransientDependencies.getNonNullableTransientEntities().iterator().next();
-			String firstPropertyPath =
+			final String firstPropertyPath =
 					nonNullableTransientDependencies.getNonNullableTransientPropertyPaths( firstTransientDependency ).iterator().next();
+
 			throw new TransientPropertyValueException(
 					"Not-null property references a transient value - transient instance must be saved before current operation",
 					firstDependentAction.getSession().guessEntityName( firstTransientDependency ),
 					firstDependentAction.getEntityName(),
 					firstPropertyPath
 			);
 		}
 	}
 
 	private void logCannotResolveNonNullableTransientDependencies(SessionImplementor session) {
 		for ( Map.Entry<Object,Set<AbstractEntityInsertAction>> entry : dependentActionsByTransientEntity.entrySet() ) {
-			Object transientEntity = entry.getKey();
-			String transientEntityName = session.guessEntityName( transientEntity );
-			Serializable transientEntityId = session.getFactory().getEntityPersister( transientEntityName ).getIdentifier( transientEntity, session );
-			String transientEntityString = MessageHelper.infoString( transientEntityName, transientEntityId );
-			Set<String> dependentEntityStrings = new TreeSet<String>();
-			Set<String> nonNullableTransientPropertyPaths = new TreeSet<String>();
+			final Object transientEntity = entry.getKey();
+			final String transientEntityName = session.guessEntityName( transientEntity );
+			final Serializable transientEntityId = session.getFactory().getEntityPersister( transientEntityName ).getIdentifier( transientEntity, session );
+			final String transientEntityString = MessageHelper.infoString( transientEntityName, transientEntityId );
+			final Set<String> dependentEntityStrings = new TreeSet<String>();
+			final Set<String> nonNullableTransientPropertyPaths = new TreeSet<String>();
 			for ( AbstractEntityInsertAction dependentAction : entry.getValue() ) {
 				dependentEntityStrings.add( MessageHelper.infoString( dependentAction.getEntityName(), dependentAction.getId() ) );
 				for ( String path : dependenciesByAction.get( dependentAction ).getNonNullableTransientPropertyPaths( transientEntity ) ) {
-					String fullPath = new StringBuilder( dependentAction.getEntityName().length() + path.length() + 1 )
-							.append( dependentAction.getEntityName() )
-							.append( '.' )
-							.append( path )
-							.toString();
+					final String fullPath = dependentAction.getEntityName() + '.' + path;
 					nonNullableTransientPropertyPaths.add( fullPath );
 				}
 			}
+
 			LOG.cannotResolveNonNullableTransientDependencies(
 					transientEntityString,
 					dependentEntityStrings,
 					nonNullableTransientPropertyPaths
 			);
 		}
 	}
 
 	/**
 	 * Returns true if there are no unresolved entity insert actions.
 	 * @return true, if there are no unresolved entity insert actions; false, otherwise.
 	 */
 	public boolean isEmpty() {
 		return dependenciesByAction.isEmpty();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addDependenciesByTransientEntity(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
 		for ( Object transientEntity : dependencies.getNonNullableTransientEntities() ) {
 			Set<AbstractEntityInsertAction> dependentActions = dependentActionsByTransientEntity.get( transientEntity );
 			if ( dependentActions == null ) {
 				dependentActions = new IdentitySet();
 				dependentActionsByTransientEntity.put( transientEntity, dependentActions );
 			}
 			dependentActions.add( insert );
 		}
 	}
 
 	/**
 	 * Resolve any dependencies on {@code managedEntity}.
 	 *
 	 * @param managedEntity - the managed entity name
 	 * @param session - the session
 	 *
 	 * @return the insert actions that depended only on the specified entity.
 	 *
 	 * @throws IllegalArgumentException if {@code managedEntity} did not have managed or read-only status.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Set<AbstractEntityInsertAction> resolveDependentActions(Object managedEntity, SessionImplementor session) {
-		EntityEntry entityEntry = session.getPersistenceContext().getEntry( managedEntity );
+		final EntityEntry entityEntry = session.getPersistenceContext().getEntry( managedEntity );
 		if ( entityEntry.getStatus() != Status.MANAGED && entityEntry.getStatus() != Status.READ_ONLY ) {
 			throw new IllegalArgumentException( "EntityEntry did not have status MANAGED or READ_ONLY: " + entityEntry );
 		}
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		// Find out if there are any unresolved insertions that are waiting for the
 		// specified entity to be resolved.
-		Set<AbstractEntityInsertAction> dependentActions = dependentActionsByTransientEntity.remove( managedEntity );
+		final Set<AbstractEntityInsertAction> dependentActions = dependentActionsByTransientEntity.remove( managedEntity );
 		if ( dependentActions == null ) {
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"No unresolved entity inserts that depended on [{0}]",
 						MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 				);
 			}
-			return Collections.emptySet();  //NOTE EARLY EXIT!
+			// NOTE EARLY EXIT!
+			return Collections.emptySet();
 		}
-		Set<AbstractEntityInsertAction> resolvedActions = new IdentitySet(  );
+		final Set<AbstractEntityInsertAction> resolvedActions = new IdentitySet(  );
 		if ( traceEnabled  ) {
 			LOG.tracev(
 					"Unresolved inserts before resolving [{0}]: [{1}]",
 					MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() ),
 					toString()
 			);
 		}
 		for ( AbstractEntityInsertAction dependentAction : dependentActions ) {
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"Resolving insert [{0}] dependency on [{1}]",
 						MessageHelper.infoString( dependentAction.getEntityName(), dependentAction.getId() ),
 						MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 				);
 			}
-			NonNullableTransientDependencies dependencies = dependenciesByAction.get( dependentAction );
+			final NonNullableTransientDependencies dependencies = dependenciesByAction.get( dependentAction );
 			dependencies.resolveNonNullableTransientEntity( managedEntity );
 			if ( dependencies.isEmpty() ) {
 				if ( traceEnabled ) {
 					LOG.tracev(
 							"Resolving insert [{0}] (only depended on [{1}])",
 							dependentAction,
 							MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() )
 					);
 				}
 				// dependentAction only depended on managedEntity..
 				dependenciesByAction.remove( dependentAction );
 				resolvedActions.add( dependentAction );
 			}
 		}
 		if ( traceEnabled  ) {
 			LOG.tracev(
 					"Unresolved inserts after resolving [{0}]: [{1}]",
 					MessageHelper.infoString( entityEntry.getEntityName(), entityEntry.getId() ),
 					toString()
 			);
 		}
 		return resolvedActions;
 	}
 
 	/**
 	 * Clear this {@link UnresolvedEntityInsertActions}.
 	 */
 	public void clear() {
 		dependenciesByAction.clear();
 		dependentActionsByTransientEntity.clear();
 	}
 
 	@Override
 	public String toString() {
-		StringBuilder sb = new StringBuilder( getClass().getSimpleName() )
+		final StringBuilder sb = new StringBuilder( getClass().getSimpleName() )
 				.append( '[' );
 		for ( Map.Entry<AbstractEntityInsertAction,NonNullableTransientDependencies> entry : dependenciesByAction.entrySet() ) {
-			AbstractEntityInsertAction insert = entry.getKey();
-			NonNullableTransientDependencies dependencies = entry.getValue();
+			final AbstractEntityInsertAction insert = entry.getKey();
+			final NonNullableTransientDependencies dependencies = entry.getValue();
 			sb.append( "[insert=" )
 					.append( insert )
 					.append( " dependencies=[" )
 					.append( dependencies.toLoggableString( insert.getSession() ) )
 					.append( "]" );
 		}
 		sb.append( ']');
 		return sb.toString();
 	}
 
 	/**
 	 * Serialize this {@link UnresolvedEntityInsertActions} object.
 	 * @param oos - the output stream
 	 * @throws IOException if there is an error writing this object to the output stream.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
-		int queueSize = dependenciesByAction.size();
+		final int queueSize = dependenciesByAction.size();
 		LOG.tracev( "Starting serialization of [{0}] unresolved insert entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( AbstractEntityInsertAction unresolvedAction : dependenciesByAction.keySet() ) {
 			oos.writeObject( unresolvedAction );
 		}
 	}
 
 	/**
 	 * Deerialize a {@link UnresolvedEntityInsertActions} object.
 	 *
 	 * @param ois - the input stream.
 	 * @param session - the session.
 	 *
 	 * @return the deserialized  {@link UnresolvedEntityInsertActions} object
 	 * @throws IOException if there is an error writing this object to the output stream.
 	 * @throws ClassNotFoundException if there is a class that cannot be loaded.
 	 */
 	public static UnresolvedEntityInsertActions deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 
-		UnresolvedEntityInsertActions rtn = new UnresolvedEntityInsertActions();
+		final UnresolvedEntityInsertActions rtn = new UnresolvedEntityInsertActions();
 
-		int queueSize = ois.readInt();
+		final int queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] unresolved insert entries", queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
-			AbstractEntityInsertAction unresolvedAction = ( AbstractEntityInsertAction ) ois.readObject();
+			final AbstractEntityInsertAction unresolvedAction = (AbstractEntityInsertAction) ois.readObject();
 			unresolvedAction.afterDeserialize( session );
 			rtn.addUnresolvedEntityInsertAction(
 					unresolvedAction,
 					unresolvedAction.findNonNullableTransientEntities()
 			);
 		}
 		return rtn;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/action/internal/package-info.java
new file mode 100644
index 0000000000..7d1d434ea9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Internals for action processing.
+ */
+package org.hibernate.action.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
index d16e8015a5..15bd9a1ac8 100644
--- a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
+++ b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
@@ -1,113 +1,113 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.annotations;
 
 /**
  * Consolidation of hints available to Hibernate JPA queries.  Mainly used to define features available on
  * Hibernate queries that have no corollary in JPA queries.
  */
 public class QueryHints {
 	/**
 	 * Disallow instantiation.
 	 */
 	private QueryHints() {
 	}
 
 	/**
 	 * The cache mode to use.
 	 *
 	 * @see org.hibernate.Query#setCacheMode
 	 * @see org.hibernate.SQLQuery#setCacheMode
 	 */
-    public static final String CACHE_MODE = "org.hibernate.cacheMode";
+	public static final String CACHE_MODE = "org.hibernate.cacheMode";
 
 	/**
 	 * The cache region to use.
 	 *
 	 * @see org.hibernate.Query#setCacheRegion
 	 * @see org.hibernate.SQLQuery#setCacheRegion
 	 */
-    public static final String CACHE_REGION = "org.hibernate.cacheRegion";
+	public static final String CACHE_REGION = "org.hibernate.cacheRegion";
 
 	/**
 	 * Are the query results cacheable?
 	 *
 	 * @see org.hibernate.Query#setCacheable
 	 * @see org.hibernate.SQLQuery#setCacheable
 	 */
-    public static final String CACHEABLE = "org.hibernate.cacheable";
+	public static final String CACHEABLE = "org.hibernate.cacheable";
 
 	/**
 	 * Is the query callable?  Note: only valid for named native sql queries.
 	 */
-    public static final String CALLABLE = "org.hibernate.callable";
+	public static final String CALLABLE = "org.hibernate.callable";
 
 	/**
 	 * Defines a comment to be applied to the SQL sent to the database.
 	 *
 	 * @see org.hibernate.Query#setComment
 	 * @see org.hibernate.SQLQuery#setComment
 	 */
-    public static final String COMMENT = "org.hibernate.comment";
+	public static final String COMMENT = "org.hibernate.comment";
 
 	/**
 	 * Defines the JDBC fetch size to use.
 	 *
 	 * @see org.hibernate.Query#setFetchSize
 	 * @see org.hibernate.SQLQuery#setFetchSize
 	 */
-    public static final String FETCH_SIZE = "org.hibernate.fetchSize";
+	public static final String FETCH_SIZE = "org.hibernate.fetchSize";
 
 	/**
 	 * The flush mode to associate with the execution of the query.
 	 *
 	 * @see org.hibernate.Query#setFlushMode
 	 * @see org.hibernate.SQLQuery#setFlushMode
 	 * @see org.hibernate.Session#setFlushMode
 	 */
-    public static final String FLUSH_MODE = "org.hibernate.flushMode";
+	public static final String FLUSH_MODE = "org.hibernate.flushMode";
 
 	/**
 	 * Should entities returned from the query be set in read only mode?
 	 *
 	 * @see org.hibernate.Query#setReadOnly
 	 * @see org.hibernate.SQLQuery#setReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 */
-    public static final String READ_ONLY = "org.hibernate.readOnly";
+	public static final String READ_ONLY = "org.hibernate.readOnly";
 
 	/**
 	 * Apply a Hibernate query timeout, which is defined in <b>seconds</b>.
 	 *
 	 * @see org.hibernate.Query#setTimeout
 	 * @see org.hibernate.SQLQuery#setTimeout
 	 */
-    public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";
+	public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";
 
 	/**
 	 * Apply a JPA query timeout, which is defined in <b>milliseconds</b>.
 	 */
-    public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";
+	public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
index ec3da4699c..775ea0f4cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
@@ -1,227 +1,227 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.registry;
 
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.boot.registry.selector.Availability;
 import org.hibernate.boot.registry.selector.AvailabilityAnnouncer;
 import org.hibernate.boot.registry.selector.internal.StrategySelectorBuilder;
 import org.hibernate.integrator.internal.IntegratorServiceImpl;
 import org.hibernate.integrator.spi.Integrator;
 
 /**
  * Builder for {@link BootstrapServiceRegistry} instances.  Provides registry for services needed for
  * most operations.  This includes {@link Integrator} handling and ClassLoader handling.
  *
  * Additionally responsible for building and managing the {@link org.hibernate.boot.registry.selector.spi.StrategySelector}
  *
  * @author Steve Ebersole
  * @author Brett Meyer
  *
  * @see StandardServiceRegistryBuilder
  */
 public class BootstrapServiceRegistryBuilder {
 	private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet<Integrator>();
 	private List<ClassLoader> providedClassLoaders;
 	private ClassLoaderService providedClassLoaderService;
 	private StrategySelectorBuilder strategySelectorBuilder = new StrategySelectorBuilder();
 	
 	/**
 	 * Add an {@link Integrator} to be applied to the bootstrap registry.
 	 *
 	 * @param integrator The integrator to add.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public BootstrapServiceRegistryBuilder with(Integrator integrator) {
 		providedIntegrators.add( integrator );
 		return this;
 	}
 
 	/**
 	 * Adds a provided {@link ClassLoader} for use in class-loading and resource-lookup.
 	 *
 	 * @param classLoader The class loader to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public BootstrapServiceRegistryBuilder with(ClassLoader classLoader) {
 		if ( providedClassLoaders == null ) {
 			providedClassLoaders = new ArrayList<ClassLoader>();
 		}
 		providedClassLoaders.add( classLoader );
 		return this;
 	}
 
 	/**
 	 * Adds a provided {@link ClassLoaderService} for use in class-loading and resource-lookup.
 	 *
 	 * @param classLoaderService The class loader service to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public BootstrapServiceRegistryBuilder with(ClassLoaderService classLoaderService) {
 		providedClassLoaderService = classLoaderService;
 		return this;
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the application class loader for the bootstrap registry.
 	 *
 	 * @param classLoader The class loader to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
 	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withApplicationClassLoader(ClassLoader classLoader) {
 		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the resource class loader for the bootstrap registry.
 	 *
 	 * @param classLoader The class loader to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
 	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withResourceClassLoader(ClassLoader classLoader) {
 		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the Hibernate class loader for the bootstrap registry.
 	 *
 	 * @param classLoader The class loader to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
 	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withHibernateClassLoader(ClassLoader classLoader) {
 		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the environment (or system) class loader for the bootstrap registry.
 	 *
 	 * @param classLoader The class loader to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
 	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withEnvironmentClassLoader(ClassLoader classLoader) {
 		return with( classLoader );
 	}
 
 	/**
 	 * Applies a named strategy implementation to the bootstrap registry.
 	 *
 	 * @param strategy The strategy
 	 * @param name The registered name
 	 * @param implementation The strategy implementation Class
 	 * @param <T> Defines the strategy type and makes sure that the strategy and implementation are of
 	 * compatible types.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.boot.registry.selector.spi.StrategySelector#registerStrategyImplementor(Class, String, Class)
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public <T> BootstrapServiceRegistryBuilder withStrategySelector(Class<T> strategy, String name, Class<? extends T> implementation) {
 		this.strategySelectorBuilder.addExplicitAvailability( strategy, implementation, name );
 		return this;
 	}
 
 	/**
 	 * Applies one or more strategy selectors announced as available by the passed announcer.
 	 *
 	 * @param availabilityAnnouncer An announcer for one or more available selectors
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.boot.registry.selector.spi.StrategySelector#registerStrategyImplementor(Class, String, Class)
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withStrategySelectors(AvailabilityAnnouncer availabilityAnnouncer) {
 		for ( Availability availability : availabilityAnnouncer.getAvailabilities() ) {
 			this.strategySelectorBuilder.addExplicitAvailability( availability );
 		}
 		return this;
 	}
 
 	/**
 	 * Build the bootstrap registry.
 	 *
 	 * @return The built bootstrap registry
 	 */
 	public BootstrapServiceRegistry build() {
 		final ClassLoaderService classLoaderService;
 		if ( providedClassLoaderService == null ) {
 			// Use a set.  As an example, in JPA, OsgiClassLoader may be in both
 			// the providedClassLoaders and the overridenClassLoader.
 			final Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
 
-            if ( providedClassLoaders != null )  {
-                classLoaders.addAll( providedClassLoaders );
-            }
+			if ( providedClassLoaders != null )  {
+				classLoaders.addAll( providedClassLoaders );
+			}
 			
 			classLoaderService = new ClassLoaderServiceImpl( classLoaders );
 		}
 		else {
 			classLoaderService = providedClassLoaderService;
 		}
 
 		final IntegratorServiceImpl integratorService = new IntegratorServiceImpl(
 				providedIntegrators,
 				classLoaderService
 		);
 
 
 		return new BootstrapServiceRegistryImpl(
 				classLoaderService,
 				strategySelectorBuilder.buildSelector( classLoaderService ),
 				integratorService
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
index 5c51256e1f..7e7c4ada58 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,338 +1,378 @@
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
 package org.hibernate.boot.registry.classloading.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.ServiceLoader;
 
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
 	private static final Logger log = Logger.getLogger( ClassLoaderServiceImpl.class );
 
 	private final ClassLoader aggregatedClassLoader;
 
+	/**
+	 * Constructs a ClassLoaderServiceImpl with standard set-up
+	 */
 	public ClassLoaderServiceImpl() {
 		this( ClassLoaderServiceImpl.class.getClassLoader() );
 	}
 
+	/**
+	 * Constructs a ClassLoaderServiceImpl with the given ClassLoader
+	 *
+	 * @param classLoader The ClassLoader to use
+	 */
 	public ClassLoaderServiceImpl(ClassLoader classLoader) {
 		this( Collections.singletonList( classLoader ) );
 	}
 
+	/**
+	 * Constructs a ClassLoaderServiceImpl with the given ClassLoader instances
+	 *
+	 * @param providedClassLoaders The ClassLoader instances to use
+	 */
 	public ClassLoaderServiceImpl(Collection<ClassLoader> providedClassLoaders) {
 		final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();
 
 		// first add all provided class loaders, if any
 		if ( providedClassLoaders != null ) {
 			for ( ClassLoader classLoader : providedClassLoaders ) {
 				if ( classLoader != null ) {
 					orderedClassLoaderSet.add( classLoader );
 				}
 			}
 		}
 
 		// normalize adding known class-loaders...
 		// first, the Hibernate class loader
 		orderedClassLoaderSet.add( ClassLoaderServiceImpl.class.getClassLoader() );
 		// then the TCCL, if one...
 		final ClassLoader tccl = locateTCCL();
 		if ( tccl != null ) {
 			orderedClassLoaderSet.add( tccl );
 		}
 		// finally the system classloader
 		final ClassLoader sysClassLoader = locateSystemClassLoader();
 		if ( sysClassLoader != null ) {
 			orderedClassLoaderSet.add( sysClassLoader );
 		}
 
 		// now build the aggregated class loader...
 		this.aggregatedClassLoader = new AggregatedClassLoader( orderedClassLoaderSet );
 	}
 
-	@SuppressWarnings({"UnusedDeclaration", "unchecked", "deprecation"})
+	/**
+	 * No longer used/supported!
+	 *
+	 * @param configValues The config values
+	 *
+	 * @return The built service
+	 *
+	 * @deprecated No longer used/supported!
+	 */
 	@Deprecated
-	public static ClassLoaderServiceImpl fromConfigSettings(Map configVales) {
+	@SuppressWarnings({"UnusedDeclaration", "unchecked", "deprecation"})
+	public static ClassLoaderServiceImpl fromConfigSettings(Map configValues) {
 		final List<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();
 
-		final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configVales.get( AvailableSettings.CLASSLOADERS );
+		final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configValues.get( AvailableSettings.CLASSLOADERS );
 		if ( classLoaders != null ) {
 			for ( ClassLoader classLoader : classLoaders ) {
 				providedClassLoaders.add( classLoader );
 			}
 		}
 
-		addIfSet( providedClassLoaders, AvailableSettings.APP_CLASSLOADER, configVales );
-		addIfSet( providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER, configVales );
-		addIfSet( providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER, configVales );
-		addIfSet( providedClassLoaders, AvailableSettings.ENVIRONMENT_CLASSLOADER, configVales );
+		addIfSet( providedClassLoaders, AvailableSettings.APP_CLASSLOADER, configValues );
+		addIfSet( providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER, configValues );
+		addIfSet( providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER, configValues );
+		addIfSet( providedClassLoaders, AvailableSettings.ENVIRONMENT_CLASSLOADER, configValues );
 
 		if ( providedClassLoaders.isEmpty() ) {
 			log.debugf( "Incoming config yielded no classloaders; adding standard SE ones" );
 			final ClassLoader tccl = locateTCCL();
 			if ( tccl != null ) {
 				providedClassLoaders.add( tccl );
 			}
 			providedClassLoaders.add( ClassLoaderServiceImpl.class.getClassLoader() );
 		}
 
 		return new ClassLoaderServiceImpl( providedClassLoaders );
 	}
 
 	private static void addIfSet(List<ClassLoader> providedClassLoaders, String name, Map configVales) {
 		final ClassLoader providedClassLoader = (ClassLoader) configVales.get( name );
 		if ( providedClassLoader != null ) {
 			providedClassLoaders.add( providedClassLoader );
 		}
 	}
 
 	private static ClassLoader locateSystemClassLoader() {
 		try {
 			return ClassLoader.getSystemClassLoader();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	private static ClassLoader locateTCCL() {
 		try {
 			return ClassLoaderHelper.getContextClassLoader();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	private static class AggregatedClassLoader extends ClassLoader {
 		private final ClassLoader[] individualClassLoaders;
 
 		private AggregatedClassLoader(final LinkedHashSet<ClassLoader> orderedClassLoaderSet) {
 			super( null );
 			individualClassLoaders = orderedClassLoaderSet.toArray( new ClassLoader[ orderedClassLoaderSet.size() ] );
 		}
 
 		@Override
 		public Enumeration<URL> getResources(String name) throws IOException {
 			final HashSet<URL> resourceUrls = new HashSet<URL>();
 
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				final Enumeration<URL> urls = classLoader.getResources( name );
 				while ( urls.hasMoreElements() ) {
 					resourceUrls.add( urls.nextElement() );
 				}
 			}
 
 			return new Enumeration<URL>() {
 				final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();
 				@Override
 				public boolean hasMoreElements() {
 					return resourceUrlIterator.hasNext();
 				}
 
 				@Override
 				public URL nextElement() {
 					return resourceUrlIterator.next();
 				}
 			};
 		}
 
 		@Override
 		protected URL findResource(String name) {
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				final URL resource = classLoader.getResource( name );
 				if ( resource != null ) {
 					return resource;
 				}
 			}
 			return super.findResource( name );
 		}
 
 		@Override
 		protected Class<?> findClass(String name) throws ClassNotFoundException {
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				try {
 					return classLoader.loadClass( name );
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			throw new ClassNotFoundException( "Could not load requested class : " + name );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> classForName(String className) {
 		try {
 			return (Class<T>) Class.forName( className, true, aggregatedClassLoader );
 		}
 		catch (Exception e) {
 			throw new ClassLoadingException( "Unable to load class [" + className + "]", e );
 		}
 	}
 
 	@Override
 	public URL locateResource(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			return aggregatedClassLoader.getResource( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			log.tracef( "trying via [new URL(\"%s\")]", name );
 			return new URL( name ).openStream();
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", name );
-			InputStream stream =  aggregatedClassLoader.getResourceAsStream( name );
+			final InputStream stream =  aggregatedClassLoader.getResourceAsStream( name );
 			if ( stream != null ) {
 				return stream;
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
-		final String stripped = name.startsWith( "/" ) ? name.substring(1) : null;
+		final String stripped = name.startsWith( "/" ) ? name.substring( 1 ) : null;
 
 		if ( stripped != null ) {
 			try {
 				log.tracef( "trying via [new URL(\"%s\")]", stripped );
 				return new URL( stripped ).openStream();
 			}
 			catch ( Exception ignore ) {
 			}
 
 			try {
 				log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", stripped );
-				InputStream stream = aggregatedClassLoader.getResourceAsStream( stripped );
+				final InputStream stream = aggregatedClassLoader.getResourceAsStream( stripped );
 				if ( stream != null ) {
 					return stream;
 				}
 			}
 			catch ( Exception ignore ) {
 			}
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
-		ArrayList<URL> urls = new ArrayList<URL>();
+		final ArrayList<URL> urls = new ArrayList<URL>();
 		try {
-			Enumeration<URL> urlEnumeration = aggregatedClassLoader.getResources( name );
+			final Enumeration<URL> urlEnumeration = aggregatedClassLoader.getResources( name );
 			if ( urlEnumeration != null && urlEnumeration.hasMoreElements() ) {
 				while ( urlEnumeration.hasMoreElements() ) {
 					urls.add( urlEnumeration.nextElement() );
 				}
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return urls;
 	}
 
 	@Override
 	public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract) {
 		final ServiceLoader<S> loader = ServiceLoader.load( serviceContract, aggregatedClassLoader );
 		final LinkedHashSet<S> services = new LinkedHashSet<S>();
 		for ( S service : loader ) {
 			services.add( service );
 		}
 
 		return services;
 	}
 
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// completely temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 
+	/**
+	 * Hack around continued (temporary) need to sometimes set the TCCL for code we call that expects it.
+	 *
+	 * @param <T> The result type
+	 */
 	public static interface Work<T> {
+		/**
+		 * The work to be performed with the TCCL set
+		 *
+		 * @return The result of the work
+		 */
 		public T perform();
 	}
 
+	/**
+	 * Perform some discrete work with with the TCCL set to our aggregated ClassLoader
+	 *
+	 * @param work The discrete work to be done
+	 * @param <T> The type of the work result
+	 *
+	 * @return The work result.
+	 */
 	public <T> T withTccl(Work<T> work) {
 		final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 
 		boolean set = false;
 
 		try {
 			Thread.currentThread().setContextClassLoader( aggregatedClassLoader );
 			set = true;
 		}
 		catch (Exception ignore) {
 		}
 
 		try {
 			return work.perform();
 		}
 		finally {
 			if ( set ) {
 				Thread.currentThread().setContextClassLoader( tccl );
 			}
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/package-info.java
new file mode 100644
index 0000000000..f08d497ee5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * The class loading service internals.
+ */
+package org.hibernate.boot.registry.classloading.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
index 2780846ea3..048733e3da 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
@@ -1,172 +1,205 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.registry.internal;
 
 import java.util.LinkedHashSet;
 
 import org.hibernate.integrator.internal.IntegratorServiceImpl;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.internal.StrategySelectorImpl;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * {@link ServiceRegistry} implementation containing specialized "bootstrap" services, specifically:<ul>
- * <li>{@link ClassLoaderService}</li>
- * <li>{@link IntegratorService}</li>
+ *     <li>{@link ClassLoaderService}</li>
+ *     <li>{@link IntegratorService}</li>
+ *     <li>{@link StrategySelector}</li>
  * </ul>
  *
  * IMPL NOTE : Currently implements the deprecated {@link org.hibernate.service.BootstrapServiceRegistry} contract
  * so that the registry returned from the builder works on the deprecated sense.  Once
  * {@link org.hibernate.service.BootstrapServiceRegistry} goes away, this should be updated to instead implement
  * {@link org.hibernate.boot.registry.BootstrapServiceRegistry}.
  *
  * @author Steve Ebersole
  */
 public class BootstrapServiceRegistryImpl
 		implements ServiceRegistryImplementor, BootstrapServiceRegistry, ServiceBinding.ServiceLifecycleOwner {
 	private static final LinkedHashSet<Integrator> NO_INTEGRATORS = new LinkedHashSet<Integrator>();
 
 	private final ServiceBinding<ClassLoaderService> classLoaderServiceBinding;
 	private final ServiceBinding<StrategySelector> strategySelectorBinding;
 	private final ServiceBinding<IntegratorService> integratorServiceBinding;
 
+	/**
+	 * Constructs a BootstrapServiceRegistryImpl.
+	 *
+	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
+	 * instead.
+	 *
+	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
+	 */
 	public BootstrapServiceRegistryImpl() {
 		this( new ClassLoaderServiceImpl(), NO_INTEGRATORS );
 	}
 
+	/**
+	 * Constructs a BootstrapServiceRegistryImpl.
+	 *
+	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
+	 * instead.
+	 *
+	 * @param classLoaderService The ClassLoaderService to use
+	 * @param providedIntegrators The group of explicitly provided integrators
+	 *
+	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
+	 */
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			LinkedHashSet<Integrator> providedIntegrators) {
 		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
 				this,
 				ClassLoaderService.class,
 				classLoaderService
 		);
 
 		final StrategySelectorImpl strategySelector = new StrategySelectorImpl( classLoaderService );
 		this.strategySelectorBinding = new ServiceBinding<StrategySelector>(
 				this,
 				StrategySelector.class,
 				strategySelector
 		);
 
 		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
 				this,
 				IntegratorService.class,
 				new IntegratorServiceImpl( providedIntegrators, classLoaderService )
 		);
 	}
 
+
+	/**
+	 * Constructs a BootstrapServiceRegistryImpl.
+	 *
+	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
+	 * instead.
+	 *
+	 * @param classLoaderService The ClassLoaderService to use
+	 * @param strategySelector The StrategySelector to use
+	 * @param integratorService The IntegratorService to use
+	 *
+	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
+	 */
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			StrategySelector strategySelector,
 			IntegratorService integratorService) {
 		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
 				this,
 				ClassLoaderService.class,
 				classLoaderService
 		);
 
 		this.strategySelectorBinding = new ServiceBinding<StrategySelector>(
 				this,
 				StrategySelector.class,
 				strategySelector
 		);
 
 		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
 				this,
 				IntegratorService.class,
 				integratorService
 		);
 	}
 
 
 
 	@Override
 	public <R extends Service> R getService(Class<R> serviceRole) {
 		final ServiceBinding<R> binding = locateServiceBinding( serviceRole );
 		return binding == null ? null : binding.getService();
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
 		if ( ClassLoaderService.class.equals( serviceRole ) ) {
 			return (ServiceBinding<R>) classLoaderServiceBinding;
 		}
 		else if ( StrategySelector.class.equals( serviceRole) ) {
 			return (ServiceBinding<R>) strategySelectorBinding;
 		}
 		else if ( IntegratorService.class.equals( serviceRole ) ) {
 			return (ServiceBinding<R>) integratorServiceBinding;
 		}
 
 		return null;
 	}
 
 	@Override
 	public void destroy() {
 	}
 
 	@Override
 	public ServiceRegistry getParentServiceRegistry() {
 		return null;
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void configureService(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void injectDependencies(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void startService(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void stopService(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
index 966f2bebd9..d1b3790d42 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
@@ -1,81 +1,92 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.registry.internal;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceInitiator;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.internal.AbstractServiceRegistryImpl;
 import org.hibernate.service.internal.ProvidedService;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceInitiator;
 
 /**
- * Hibernate implementation of the standard service registry.
+ * Standard Hibernate implementation of the standard service registry.
  *
  * @author Steve Ebersole
  */
 public class StandardServiceRegistryImpl extends AbstractServiceRegistryImpl implements StandardServiceRegistry {
 	private final Map configurationValues;
 
+	/**
+	 * Constructs a StandardServiceRegistryImpl.  Should not be instantiated directly; use
+	 * {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder} instead
+	 *
+	 * @param bootstrapServiceRegistry The bootstrap service registry.
+	 * @param serviceInitiators Any StandardServiceInitiators provided by the user to the builder
+	 * @param providedServices Any standard services provided directly to the builder
+	 * @param configurationValues Configuration values
+	 *
+	 * @see org.hibernate.boot.registry.StandardServiceRegistryBuilder
+	 */
 	@SuppressWarnings( {"unchecked"})
 	public StandardServiceRegistryImpl(
 			BootstrapServiceRegistry bootstrapServiceRegistry,
 			List<StandardServiceInitiator> serviceInitiators,
 			List<ProvidedService> providedServices,
 			Map<?, ?> configurationValues) {
 		super( bootstrapServiceRegistry );
 
 		this.configurationValues = configurationValues;
 
 		// process initiators
 		for ( ServiceInitiator initiator : serviceInitiators ) {
 			createServiceBinding( initiator );
 		}
 
 		// then, explicitly provided service instances
 		for ( ProvidedService providedService : providedServices ) {
 			createServiceBinding( providedService );
 		}
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		// todo : add check/error for unexpected initiator types?
 		return ( (StandardServiceInitiator<R>) serviceInitiator ).initiateService( configurationValues, this );
 	}
 
 	@Override
 	public <R extends Service> void configureService(ServiceBinding<R> serviceBinding) {
 		if ( Configurable.class.isInstance( serviceBinding.getService() ) ) {
 			( (Configurable) serviceBinding.getService() ).configure( configurationValues );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/package-info.java
new file mode 100644
index 0000000000..1f1adfd33e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * The internals for building service registries.
+ */
+package org.hibernate.boot.registry.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/package-info.java
new file mode 100644
index 0000000000..e1eaeb1535
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Internals for building StrategySelector
+ */
+package org.hibernate.boot.registry.selector.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
index 1182ed9d18..6cb07124a5 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
@@ -1,102 +1,108 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.internal;
 
 import java.io.ByteArrayInputStream;
 import java.io.DataInputStream;
 import java.io.IOException;
 import java.util.Set;
 
 import javassist.bytecode.ClassFile;
 
 import org.hibernate.bytecode.buildtime.spi.AbstractInstrumenter;
 import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
 import org.hibernate.bytecode.buildtime.spi.ClassDescriptor;
 import org.hibernate.bytecode.buildtime.spi.Logger;
 import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
 import org.hibernate.bytecode.internal.javassist.FieldHandled;
 import org.hibernate.bytecode.spi.ClassTransformer;
 
 /**
  * Strategy for performing build-time instrumentation of persistent classes in order to enable
  * field-level interception using Javassist.
  *
  * @author Steve Ebersole
  * @author Muga Nishizawa
  */
 public class JavassistInstrumenter extends AbstractInstrumenter {
 
 	private static final BasicClassFilter CLASS_FILTER = new BasicClassFilter();
 
 	private final BytecodeProviderImpl provider = new BytecodeProviderImpl();
 
+	/**
+	 * Constructs the Javassist-based instrumenter.
+	 *
+	 * @param logger Logger to use
+	 * @param options Instrumentation options
+	 */
 	public JavassistInstrumenter(Logger logger, Options options) {
 		super( logger, options );
 	}
 
 	@Override
-    protected ClassDescriptor getClassDescriptor(byte[] bytecode) throws IOException {
+	protected ClassDescriptor getClassDescriptor(byte[] bytecode) throws IOException {
 		return new CustomClassDescriptor( bytecode );
 	}
 
 	@Override
-    protected ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames) {
+	protected ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames) {
 		if ( descriptor.isInstrumented() ) {
 			logger.debug( "class [" + descriptor.getName() + "] already instrumented" );
 			return null;
 		}
 		else {
 			return provider.getTransformer( CLASS_FILTER, new CustomFieldFilter( descriptor, classNames ) );
 		}
 	}
 
 	private static class CustomClassDescriptor implements ClassDescriptor {
 		private final byte[] bytes;
 		private final ClassFile classFile;
 
 		public CustomClassDescriptor(byte[] bytes) throws IOException {
 			this.bytes = bytes;
 			this.classFile = new ClassFile( new DataInputStream( new ByteArrayInputStream( bytes ) ) );
 		}
 
 		public String getName() {
 			return classFile.getName();
 		}
 
 		public boolean isInstrumented() {
-			String[] interfaceNames = classFile.getInterfaces();
+			final String[] interfaceNames = classFile.getInterfaces();
 			for ( String interfaceName : interfaceNames ) {
 				if ( FieldHandled.class.getName().equals( interfaceName ) ) {
 					return true;
 				}
 			}
 			return false;
 		}
 
 		public byte[] getBytes() {
 			return bytes;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java
new file mode 100644
index 0000000000..cf297b2957
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Javassist support internals
+ */
+package org.hibernate.bytecode.buildtime.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
index 190b592e4c..dbccc69765 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
@@ -1,437 +1,438 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 import java.io.ByteArrayInputStream;
 import java.io.DataInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;
 import java.util.zip.ZipOutputStream;
 
 import org.hibernate.bytecode.spi.ByteCodeHelper;
 import org.hibernate.bytecode.spi.ClassTransformer;
 
 /**
  * Provides the basic templating of how instrumentation should occur.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractInstrumenter implements Instrumenter {
 	private static final int ZIP_MAGIC = 0x504B0304;
 	private static final int CLASS_MAGIC = 0xCAFEBABE;
-	
+
 	protected final Logger logger;
 	protected final Options options;
 
 	/**
 	 * Creates the basic instrumentation strategy.
 	 *
 	 * @param logger The bridge to the environment's logging system.
 	 * @param options User-supplied options.
 	 */
 	public AbstractInstrumenter(Logger logger, Options options) {
 		this.logger = logger;
 		this.options = options;
 	}
 
 	/**
 	 * Given the bytecode of a java class, retrieve the descriptor for that class.
 	 *
 	 * @param byecode The class bytecode.
 	 *
 	 * @return The class's descriptor
 	 *
 	 * @throws Exception Indicates problems access the bytecode.
 	 */
 	protected abstract ClassDescriptor getClassDescriptor(byte[] byecode) throws Exception;
 
 	/**
 	 * Create class transformer for the class.
 	 *
 	 * @param descriptor The descriptor of the class to be instrumented.
 	 * @param classNames The names of all classes to be instrumented; the "pipeline" if you will.
 	 *
 	 * @return The transformer for the given class; may return null to indicate that transformation should
 	 * be skipped (ala already instrumented).
 	 */
 	protected abstract ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames);
 
 	/**
 	 * The main instrumentation entry point.  Given a set of files, perform instrumentation on each discovered class
 	 * file.
 	 *
 	 * @param files The files.
 	 */
 	public void execute(Set<File> files) {
-		Set<String> classNames = new HashSet<String>();
+		final Set<String> classNames = new HashSet<String>();
 
 		if ( options.performExtendedInstrumentation() ) {
 			logger.debug( "collecting class names for extended instrumentation determination" );
 			try {
 				for ( Object file1 : files ) {
 					final File file = (File) file1;
 					collectClassNames( file, classNames );
 				}
 			}
 			catch ( ExecutionException ee ) {
 				throw ee;
 			}
 			catch ( Exception e ) {
 				throw new ExecutionException( e );
 			}
 		}
 
 		logger.info( "starting instrumentation" );
 		try {
 			for ( File file : files ) {
 				processFile( file, classNames );
 			}
 		}
 		catch ( ExecutionException ee ) {
 			throw ee;
 		}
 		catch ( Exception e ) {
 			throw new ExecutionException( e );
 		}
 	}
 
 	/**
 	 * Extract the names of classes from file, adding them to the classNames collection.
 	 * <p/>
 	 * IMPL NOTE : file here may be either a class file or a jar.  If a jar, all entries in the jar file are
 	 * processed.
 	 *
 	 * @param file The file from which to extract class metadata (descriptor).
 	 * @param classNames The collected class name collection.
 	 *
 	 * @throws Exception indicates problems accessing the file or its contents.
 	 */
 	private void collectClassNames(File file, final Set<String> classNames) throws Exception {
-	    if ( isClassFile( file ) ) {
-			byte[] bytes = ByteCodeHelper.readByteCode( file );
-			ClassDescriptor descriptor = getClassDescriptor( bytes );
-		    classNames.add( descriptor.getName() );
-	    }
-	    else if ( isJarFile( file ) ) {
-		    ZipEntryHandler collector = new ZipEntryHandler() {
-			    public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
+		if ( isClassFile( file ) ) {
+			final byte[] bytes = ByteCodeHelper.readByteCode( file );
+			final ClassDescriptor descriptor = getClassDescriptor( bytes );
+			classNames.add( descriptor.getName() );
+		}
+		else if ( isJarFile( file ) ) {
+			final ZipEntryHandler collector = new ZipEntryHandler() {
+				public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
 					if ( !entry.isDirectory() ) {
 						// see if the entry represents a class file
-						DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
+						final DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
 						if ( din.readInt() == CLASS_MAGIC ) {
-				            classNames.add( getClassDescriptor( byteCode ).getName() );
+							classNames.add( getClassDescriptor( byteCode ).getName() );
 						}
 					}
-			    }
-		    };
-			ZipFileProcessor processor = new ZipFileProcessor( collector );
-		    processor.process( file );
-	    }
+				}
+			};
+			final ZipFileProcessor processor = new ZipFileProcessor( collector );
+			processor.process( file );
+		}
 	}
 
 	/**
 	 * Does this file represent a compiled class?
 	 *
 	 * @param file The file to check.
 	 *
 	 * @return True if the file is a class; false otherwise.
 	 *
 	 * @throws IOException Indicates problem access the file.
 	 */
 	protected final boolean isClassFile(File file) throws IOException {
-        return checkMagic( file, CLASS_MAGIC );
-    }
+		return checkMagic( file, CLASS_MAGIC );
+	}
 
 	/**
 	 * Does this file represent a zip file of some format?
 	 *
 	 * @param file The file to check.
 	 *
 	 * @return True if the file is n archive; false otherwise.
 	 *
 	 * @throws IOException Indicates problem access the file.
 	 */
-    protected final boolean isJarFile(File file) throws IOException {
-        return checkMagic(file, ZIP_MAGIC);
-    }
+	protected final boolean isJarFile(File file) throws IOException {
+		return checkMagic( file, ZIP_MAGIC );
+	}
 
 	protected final boolean checkMagic(File file, long magic) throws IOException {
-        DataInputStream in = new DataInputStream( new FileInputStream( file ) );
-        try {
-            int m = in.readInt();
-            return magic == m;
-        }
-        finally {
-            in.close();
-        }
-    }
+		final DataInputStream in = new DataInputStream( new FileInputStream( file ) );
+		try {
+			final int m = in.readInt();
+			return magic == m;
+		}
+		finally {
+			in.close();
+		}
+	}
 
 	/**
 	 * Actually process the file by applying instrumentation transformations to any classes it contains.
 	 * <p/>
 	 * Again, just like with {@link #collectClassNames} this method can handle both class and archive files.
 	 *
 	 * @param file The file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
 	protected void processFile(File file, Set<String> classNames) throws Exception {
-	    if ( isClassFile( file ) ) {
+		if ( isClassFile( file ) ) {
 			logger.debug( "processing class file : " + file.getAbsolutePath() );
-	        processClassFile( file, classNames );
-	    }
-	    else if ( isJarFile( file ) ) {
+			processClassFile( file, classNames );
+		}
+		else if ( isJarFile( file ) ) {
 			logger.debug( "processing jar file : " + file.getAbsolutePath() );
-	        processJarFile( file, classNames );
-	    }
-	    else {
-		    logger.debug( "ignoring file : " + file.getAbsolutePath() );
-	    }
+			processJarFile( file, classNames );
+		}
+		else {
+			logger.debug( "ignoring file : " + file.getAbsolutePath() );
+		}
 	}
 
 	/**
 	 * Process a class file.  Delegated to from {@link #processFile} in the case of a class file.
 	 *
 	 * @param file The class file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
 	protected void processClassFile(File file, Set<String> classNames) throws Exception {
-		byte[] bytes = ByteCodeHelper.readByteCode( file );
-		ClassDescriptor descriptor = getClassDescriptor( bytes );
-		ClassTransformer transformer = getClassTransformer( descriptor, classNames );
+		final byte[] bytes = ByteCodeHelper.readByteCode( file );
+		final ClassDescriptor descriptor = getClassDescriptor( bytes );
+		final ClassTransformer transformer = getClassTransformer( descriptor, classNames );
 		if ( transformer == null ) {
 			logger.debug( "no trasformer for class file : " + file.getAbsolutePath() );
 			return;
 		}
 
 		logger.info( "processing class : " + descriptor.getName() + ";  file = " + file.getAbsolutePath() );
-		byte[] transformedBytes = transformer.transform(
+		final byte[] transformedBytes = transformer.transform(
 				getClass().getClassLoader(),
 				descriptor.getName(),
 				null,
 				null,
 				descriptor.getBytes()
 		);
 
-		OutputStream out = new FileOutputStream( file );
+		final OutputStream out = new FileOutputStream( file );
 		try {
 			out.write( transformedBytes );
 			out.flush();
 		}
 		finally {
 			try {
 				out.close();
 			}
 			catch ( IOException ignore) {
 				// intentionally empty
 			}
 		}
 	}
 
 	/**
 	 * Process an archive file.  Delegated to from {@link #processFile} in the case of an archive file.
 	 *
 	 * @param file The archive file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
 	protected void processJarFile(final File file, final Set<String> classNames) throws Exception {
-        File tempFile = File.createTempFile(
-		        file.getName(),
-		        null,
-		        new File( file.getAbsoluteFile().getParent() )
-        );
-
-        try {
-			FileOutputStream fout = new FileOutputStream( tempFile, false );
+		final File tempFile = File.createTempFile(
+				file.getName(),
+				null,
+				new File( file.getAbsoluteFile().getParent() )
+		);
+
+		try {
+			final FileOutputStream fout = new FileOutputStream( tempFile, false );
 			try {
 				final ZipOutputStream out = new ZipOutputStream( fout );
-				ZipEntryHandler transformer = new ZipEntryHandler() {
+				final ZipEntryHandler transformer = new ZipEntryHandler() {
 					public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
-								logger.debug( "starting zip entry : " + entry.toString() );
-								if ( !entry.isDirectory() ) {
-									// see if the entry represents a class file
-									DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
-									if ( din.readInt() == CLASS_MAGIC ) {
-										ClassDescriptor descriptor = getClassDescriptor( byteCode );
-										ClassTransformer transformer = getClassTransformer( descriptor, classNames );
-										if ( transformer == null ) {
-											logger.debug( "no transformer for zip entry :  " + entry.toString() );
-										}
-										else {
-											logger.info( "processing class : " + descriptor.getName() + ";  entry = " + file.getAbsolutePath() );
-											byteCode = transformer.transform(
-													getClass().getClassLoader(),
-													descriptor.getName(),
-													null,
-													null,
-													descriptor.getBytes()
-											);
-										}
-									}
-									else {
-										logger.debug( "ignoring zip entry : " + entry.toString() );
-									}
+						logger.debug( "starting zip entry : " + entry.toString() );
+						if ( !entry.isDirectory() ) {
+							// see if the entry represents a class file
+							final DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
+							if ( din.readInt() == CLASS_MAGIC ) {
+								final ClassDescriptor descriptor = getClassDescriptor( byteCode );
+								final ClassTransformer transformer = getClassTransformer( descriptor, classNames );
+								if ( transformer == null ) {
+									logger.debug( "no transformer for zip entry :  " + entry.toString() );
 								}
+								else {
+									logger.info( "processing class : " + descriptor.getName() + ";  entry = " + file.getAbsolutePath() );
+									byteCode = transformer.transform(
+											getClass().getClassLoader(),
+											descriptor.getName(),
+											null,
+											null,
+											descriptor.getBytes()
+									);
+								}
+							}
+							else {
+								logger.debug( "ignoring zip entry : " + entry.toString() );
+							}
+						}
 
-								ZipEntry outEntry = new ZipEntry( entry.getName() );
-								outEntry.setMethod( entry.getMethod() );
-								outEntry.setComment( entry.getComment() );
-								outEntry.setSize( byteCode.length );
+						final ZipEntry outEntry = new ZipEntry( entry.getName() );
+						outEntry.setMethod( entry.getMethod() );
+						outEntry.setComment( entry.getComment() );
+						outEntry.setSize( byteCode.length );
 
-								if ( outEntry.getMethod() == ZipEntry.STORED ){
-									CRC32 crc = new CRC32();
-									crc.update( byteCode );
-									outEntry.setCrc( crc.getValue() );
-									outEntry.setCompressedSize( byteCode.length );
-								}
-								out.putNextEntry( outEntry );
-								out.write( byteCode );
-								out.closeEntry();
+						if ( outEntry.getMethod() == ZipEntry.STORED ){
+							final CRC32 crc = new CRC32();
+							crc.update( byteCode );
+							outEntry.setCrc( crc.getValue() );
+							outEntry.setCompressedSize( byteCode.length );
+						}
+						out.putNextEntry( outEntry );
+						out.write( byteCode );
+						out.closeEntry();
 					}
 				};
-				ZipFileProcessor processor = new ZipFileProcessor( transformer );
+
+				final ZipFileProcessor processor = new ZipFileProcessor( transformer );
 				processor.process( file );
 				out.close();
 			}
 			finally{
 				fout.close();
 			}
 
-            if ( file.delete() ) {
-	            File newFile = new File( tempFile.getAbsolutePath() );
-                if( !newFile.renameTo( file ) ) {
-	                throw new IOException( "can not rename " + tempFile + " to " + file );
-                }
-            }
-            else {
-	            throw new IOException( "can not delete " + file );
-            }
-        }
-        finally {
-	        if ( ! tempFile.delete() ) {
+			if ( file.delete() ) {
+				final File newFile = new File( tempFile.getAbsolutePath() );
+				if( ! newFile.renameTo( file ) ) {
+					throw new IOException( "can not rename " + tempFile + " to " + file );
+				}
+			}
+			else {
+				throw new IOException( "can not delete " + file );
+			}
+		}
+		finally {
+			if ( ! tempFile.delete() ) {
 				logger.info( "Unable to cleanup temporary jar file : " + tempFile.getAbsolutePath() );
 			}
-        }
+		}
 	}
 
 	/**
 	 * Allows control over what exactly to transform.
 	 */
 	protected class CustomFieldFilter implements FieldFilter {
 		private final ClassDescriptor descriptor;
 		private final Set classNames;
 
 		public CustomFieldFilter(ClassDescriptor descriptor, Set classNames) {
 			this.descriptor = descriptor;
 			this.classNames = classNames;
 		}
 
 		public boolean shouldInstrumentField(String className, String fieldName) {
 			if ( descriptor.getName().equals( className ) ) {
 				logger.trace( "accepting transformation of field [" + className + "." + fieldName + "]" );
 				return true;
 			}
 			else {
 				logger.trace( "rejecting transformation of field [" + className + "." + fieldName + "]" );
 				return false;
 			}
 		}
 
 		public boolean shouldTransformFieldAccess(
 				String transformingClassName,
 				String fieldOwnerClassName,
 				String fieldName) {
 			if ( descriptor.getName().equals( fieldOwnerClassName ) ) {
 				logger.trace( "accepting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
 				return true;
 			}
 			else if ( options.performExtendedInstrumentation() && classNames.contains( fieldOwnerClassName ) ) {
 				logger.trace( "accepting extended transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
 				return true;
 			}
 			else {
 				logger.trace( "rejecting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]; caller = " + transformingClassName  );
 				return false;
 			}
 		}
 	}
 
 	/**
 	 * General strategy contract for handling entries in an archive file.
 	 */
 	private static interface ZipEntryHandler {
 		/**
 		 * Apply strategy to the given archive entry.
 		 *
 		 * @param entry The archive file entry.
 		 * @param byteCode The bytes making up the entry
 		 *
 		 * @throws Exception Problem handling entry
 		 */
 		public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception;
 	}
 
 	/**
 	 * Applies {@link ZipEntryHandler} strategies to the entries of an archive file.
 	 */
 	private static class ZipFileProcessor {
 		private final ZipEntryHandler entryHandler;
 
 		public ZipFileProcessor(ZipEntryHandler entryHandler) {
 			this.entryHandler = entryHandler;
 		}
 
 		public void process(File file) throws Exception {
-			ZipInputStream zip = new ZipInputStream( new FileInputStream( file ) );
+			final ZipInputStream zip = new ZipInputStream( new FileInputStream( file ) );
 
 			try {
 				ZipEntry entry;
 				while ( (entry = zip.getNextEntry()) != null ) {
-					byte bytes[] = ByteCodeHelper.readByteCode( zip );
+					final byte[] bytes = ByteCodeHelper.readByteCode( zip );
 					entryHandler.handleEntry( entry, bytes );
 					zip.closeEntry();
 				}
-            }
-            finally {
-	            zip.close();
-            }
+			}
+			finally {
+				zip.close();
+			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
index 642fb3811f..a62b16eeb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
@@ -1,72 +1,82 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 /**
  * BasicClassFilter provides class filtering based on a series of packages to
  * be included and/or a series of explicit class names to be included.  If
  * neither is specified, then no restrictions are applied.
  *
  * @author Steve Ebersole
  */
 public class BasicClassFilter implements ClassFilter {
 	private final String[] includedPackages;
 	private final Set<String> includedClassNames = new HashSet<String>();
 	private final boolean isAllEmpty;
 
+	/**
+	 * Constructs a BasicClassFilter with given configuration.
+	 */
 	public BasicClassFilter() {
 		this( null, null );
 	}
 
+	/**
+	 * Constructs a BasicClassFilter with standard set of configuration.
+	 *
+	 * @param includedPackages Name of packages whose classes should be accepted.
+	 * @param includedClassNames Name of classes that should be accepted.
+	 */
 	public BasicClassFilter(String[] includedPackages, String[] includedClassNames) {
 		this.includedPackages = includedPackages;
 		if ( includedClassNames != null ) {
 			this.includedClassNames.addAll( Arrays.asList( includedClassNames ) );
 		}
 
 		isAllEmpty = ( this.includedPackages == null || this.includedPackages.length == 0 )
-		             && ( this.includedClassNames.isEmpty() );
+				&& ( this.includedClassNames.isEmpty() );
 	}
 
+	@Override
 	public boolean shouldInstrumentClass(String className) {
 		return isAllEmpty ||
 				includedClassNames.contains( className ) ||
 				isInIncludedPackage( className );
 	}
 
 	private boolean isInIncludedPackage(String className) {
 		if ( includedPackages != null ) {
 			for ( String includedPackage : includedPackages ) {
 				if ( className.startsWith( includedPackage ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
index 935e4a5912..62f4352264 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
@@ -1,40 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 /**
  * Used to determine whether a class should be instrumented.
  *
  * @author Steve Ebersole
  */
 public interface ClassFilter {
 	/**
-	 * Should this class be included in instrumentation
+	 * Should this class be included in instrumentation.
 	 *
 	 * @param className The name of the class to check
 	 *
 	 * @return {@literal true} to include class in instrumentation; {@literal false} otherwise.
 	 */
 	public boolean shouldInstrumentClass(String className);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
index 0b09fe61e8..b9d61a8c14 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
@@ -1,44 +1,60 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 /**
  * Indicates problem performing the instrumentation execution.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class ExecutionException extends RuntimeException {
+	/**
+	 * Constructs an ExecutionException.
+	 *
+	 * @param message The message explaining the exception condition
+	 */
 	public ExecutionException(String message) {
 		super( message );
 	}
 
+	/**
+	 * Constructs an ExecutionException.
+	 *
+	 * @param cause The underlying cause.
+	 */
 	public ExecutionException(Throwable cause) {
 		super( cause );
 	}
 
+	/**
+	 * Constructs an ExecutionException.
+	 *
+	 * @param message The message explaining the exception condition
+	 * @param cause The underlying cause.
+	 */
 	public ExecutionException(String message, Throwable cause) {
 		super( message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
index 3a281b91f0..a9be338877 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
@@ -1,53 +1,53 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 import java.io.File;
 import java.util.Set;
 
 /**
- * Basic contract for performing instrumentation
+ * Basic contract for performing instrumentation.
  *
  * @author Steve Ebersole
  */
 public interface Instrumenter {
 	/**
-	 * Perform the instrumentation
+	 * Perform the instrumentation.
 	 *
 	 * @param files The file on which to perform instrumentation
 	 */
 	public void execute(Set<File> files);
 
 	/**
-	 * Instrumentation options
+	 * Instrumentation options.
 	 */
 	public static interface Options {
 		/**
 		 * Should we enhance references to class fields outside the class itself?
 		 *
 		 * @return {@literal true}/{@literal false}
 		 */
 		public boolean performExtendedInstrumentation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
index 7a489687cc..41f8ec1fb0 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
@@ -1,42 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.buildtime.spi;
 
 /**
  * Provides an abstraction for how instrumentation does logging because it is usually run in environments (Ant/Maven)
  * with their own logging infrastructure.  This abstraction allows proper bridging.
  *
  * @author Steve Ebersole
  */
 public interface Logger {
+	/**
+	 * Log a message with TRACE semantics.
+	 *
+	 * @param message The message to log.
+	 */
 	public void trace(String message);
 
+	/**
+	 * Log a message with DEBUG semantics.
+	 *
+	 * @param message The message to log.
+	 */
 	public void debug(String message);
 
+	/**
+	 * Log a message with INFO semantics.
+	 *
+	 * @param message The message to log.
+	 */
 	public void info(String message);
 
+	/**
+	 * Log a message with WARN semantics.
+	 *
+	 * @param message The message to log.
+	 */
 	public void warn(String message);
 
+	/**
+	 * Log a message with ERROR semantics.
+	 *
+	 * @param message The message to log.
+	 */
 	public void error(String message);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java
new file mode 100644
index 0000000000..a37e4dffcf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java
@@ -0,0 +1,6 @@
+/**
+ * Package defining build-time bytecode code enhancement (instrumentation) support.
+ *
+ * This package should mostly be considered deprecated in favor of {@link org.hibernate.bytecode.enhance}
+ */
+package org.hibernate.bytecode.buildtime.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/EnhancementException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/EnhancementException.java
index 2955cbcec6..129f78cd9c 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/EnhancementException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/EnhancementException.java
@@ -1,39 +1,43 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance;
 
 import org.hibernate.HibernateException;
 
 /**
+ * An exception indicating some kind of problem performing bytecode enhancement.
+ *
  * @author Steve Ebersole
  */
 public class EnhancementException extends HibernateException {
-	public EnhancementException(String message) {
-		super( message );
-	}
-
-	public EnhancementException(String message, Throwable root) {
-		super( message, root );
+	/**
+	 * Constructs an EnhancementException
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param cause The underlying cause.
+	 */
+	public EnhancementException(String message, Throwable cause) {
+		super( message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/package-info.java
new file mode 100644
index 0000000000..b8e84966d6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Package defining bytecode code enhancement (instrumentation) support.
+ */
+package org.hibernate.bytecode.enhance;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
index f42c3ae83d..dc9cd7479d 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancementContext.java
@@ -1,98 +1,124 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.spi;
 
 import javassist.CtClass;
 import javassist.CtField;
 
 /**
- * todo : not sure its a great idea to expose Javassist classes this way.
- * 		maybe wrap them in our own contracts?
+ * The context for performing an enhancement.  Enhancement can happen in any number of ways:<ul>
+ *     <li>Build time, via Ant</li>
+ *     <li>Build time, via Maven</li>
+ *     <li>Build time, via Gradle</li>
+ *     <li>Runtime, via agent</li>
+ *     <li>Runtime, via JPA constructs</li>
+ * </ul>
+ *
+ * This interface isolates the code that actually does the enhancement from the underlying context in which
+ * the enhancement is being performed.
+ *
+ * @todo Not sure its a great idea to expose Javassist classes this way.  maybe wrap them in our own contracts?
  *
  * @author Steve Ebersole
  */
 public interface EnhancementContext {
 	/**
 	 * Obtain access to the ClassLoader that can be used to load Class references.  In JPA SPI terms, this
 	 * should be a "temporary class loader" as defined by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}
+	 *
+	 * @return The class loader that the enhancer can use.
 	 */
 	public ClassLoader getLoadingClassLoader();
 
 	/**
 	 * Does the given class descriptor represent a entity class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} if the class is an entity; {@code false} otherwise.
 	 */
 	public boolean isEntityClass(CtClass classDescriptor);
 
 	/**
 	 * Does the given class name represent an embeddable/component class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} if the class is an embeddable/component; {@code false} otherwise.
 	 */
 	public boolean isCompositeClass(CtClass classDescriptor);
 
 	/**
 	 * Should we in-line dirty checking for persistent attributes for this class?
 	 *
 	 * @param classDescriptor The descriptor of the class to check.
 	 *
 	 * @return {@code true} indicates that dirty checking should be in-lined within the entity; {@code false}
 	 * indicates it should not.  In-lined is more easily serializable and probably more performant.
 	 */
 	public boolean doDirtyCheckingInline(CtClass classDescriptor);
 
+	/**
+	 * Does the given class define any lazy loadable attributes?
+	 *
+	 * @param classDescriptor The class to check
+	 *
+	 * @return true/false
+	 */
 	public boolean hasLazyLoadableAttributes(CtClass classDescriptor);
 
 	// todo : may be better to invert these 2 such that the context is asked for an ordered list of persistent fields for an entity/composite
 
 	/**
 	 * Does the field represent persistent state?  Persistent fields will be "enhanced".
 	 * <p/>
-	 // 		may be better to perform basic checks in the caller (non-static, etc) and call out with just the
-	 // 		Class name and field name...
-
+	 * may be better to perform basic checks in the caller (non-static, etc) and call out with just the
+	 * Class name and field name...
+	 *
 	 * @param ctField The field reference.
 	 *
 	 * @return {@code true} if the field is ; {@code false} otherwise.
 	 */
- 	public boolean isPersistentField(CtField ctField);
+	public boolean isPersistentField(CtField ctField);
 
 	/**
 	 * For fields which are persistent (according to {@link #isPersistentField}), determine the corresponding ordering
 	 * maintained within the Hibernate metamodel.
 
 	 * @param persistentFields The persistent field references.
 	 *
 	 * @return The ordered references.
 	 */
 	public CtField[] order(CtField[] persistentFields);
 
+	/**
+	 * Determine if a field is lazy loadable.
+	 *
+	 * @param field The field to check
+	 *
+	 * @return {@code true} if the field is lazy loadable; {@code false} otherwise.
+	 */
 	public boolean isLazyLoadable(CtField field);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
index 4473739e92..b3455b6734 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/Enhancer.java
@@ -1,986 +1,971 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.enhance.spi;
 
 import javax.persistence.Transient;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.util.ArrayList;
-import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.List;
-import java.util.Map;
 
 import javassist.CannotCompileException;
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 import javassist.CtMethod;
 import javassist.CtNewMethod;
 import javassist.LoaderClassPath;
 import javassist.Modifier;
 import javassist.NotFoundException;
 import javassist.bytecode.AnnotationsAttribute;
 import javassist.bytecode.BadBytecode;
 import javassist.bytecode.CodeAttribute;
 import javassist.bytecode.CodeIterator;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.FieldInfo;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
 import javassist.bytecode.StackMapTable;
 import javassist.bytecode.annotation.Annotation;
 import javassist.bytecode.stackmap.MapMaker;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.enhance.EnhancementException;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.ManagedComposite;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.mapping.PersistentClass;
 
 /**
+ * Class responsible for performing enhancement.
+ *
  * @author Steve Ebersole
  * @author Jason Greene
  */
 public class Enhancer  {
 	private static final CoreMessageLogger log = Logger.getMessageLogger( CoreMessageLogger.class, Enhancer.class.getName() );
 
-	public static final String PERSISTENT_FIELD_READER_PREFIX = "$$_hibernate_read_";
-	public static final String PERSISTENT_FIELD_WRITER_PREFIX = "$$_hibernate_write_";
-
-	public static final String ENTITY_INSTANCE_GETTER_NAME = "$$_hibernate_getEntityInstance";
-
-	public static final String ENTITY_ENTRY_FIELD_NAME = "$$_hibernate_entityEntryHolder";
-	public static final String ENTITY_ENTRY_GETTER_NAME = "$$_hibernate_getEntityEntry";
-	public static final String ENTITY_ENTRY_SETTER_NAME = "$$_hibernate_setEntityEntry";
-
-	public static final String PREVIOUS_FIELD_NAME = "$$_hibernate_previousManagedEntity";
-	public static final String PREVIOUS_GETTER_NAME = "$$_hibernate_getPreviousManagedEntity";
-	public static final String PREVIOUS_SETTER_NAME = "$$_hibernate_setPreviousManagedEntity";
-
-	public static final String NEXT_FIELD_NAME = "$$_hibernate_nextManagedEntity";
-	public static final String NEXT_GETTER_NAME = "$$_hibernate_getNextManagedEntity";
-	public static final String NEXT_SETTER_NAME = "$$_hibernate_setNextManagedEntity";
-
-	public static final String INTERCEPTOR_FIELD_NAME = "$$_hibernate_attributeInterceptor";
-	public static final String INTERCEPTOR_GETTER_NAME = "$$_hibernate_getInterceptor";
-	public static final String INTERCEPTOR_SETTER_NAME = "$$_hibernate_setInterceptor";
-
 	private final EnhancementContext enhancementContext;
 
 	private final ClassPool classPool;
 	private final CtClass managedEntityCtClass;
 	private final CtClass managedCompositeCtClass;
 	private final CtClass attributeInterceptorCtClass;
 	private final CtClass attributeInterceptableCtClass;
 	private final CtClass entityEntryCtClass;
 	private final CtClass objectCtClass;
 
+	/**
+	 * Constructs the Enhancer, using the given context.
+	 *
+	 * @param enhancementContext Describes the context in which enhancement will occur so as to give access
+	 * to contextual/environmental information.
+	 */
 	public Enhancer(EnhancementContext enhancementContext) {
 		this.enhancementContext = enhancementContext;
 		this.classPool = buildClassPool( enhancementContext );
 
 		try {
 			// add ManagedEntity contract
 			this.managedEntityCtClass = classPool.makeClass(
 					ManagedEntity.class.getClassLoader().getResourceAsStream(
 							ManagedEntity.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add ManagedComposite contract
 			this.managedCompositeCtClass = classPool.makeClass(
 					ManagedComposite.class.getClassLoader().getResourceAsStream(
 							ManagedComposite.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add PersistentAttributeInterceptable contract
 			this.attributeInterceptableCtClass = classPool.makeClass(
 					PersistentAttributeInterceptable.class.getClassLoader().getResourceAsStream(
 							PersistentAttributeInterceptable.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// add PersistentAttributeInterceptor contract
 			this.attributeInterceptorCtClass = classPool.makeClass(
 					PersistentAttributeInterceptor.class.getClassLoader().getResourceAsStream(
 							PersistentAttributeInterceptor.class.getName().replace( '.', '/' ) + ".class"
 					)
 			);
 
 			// "add" EntityEntry
 			this.entityEntryCtClass = classPool.makeClass( EntityEntry.class.getName() );
 		}
 		catch (IOException e) {
 			throw new EnhancementException( "Could not prepare Javassist ClassPool", e );
 		}
 
 		try {
 			this.objectCtClass = classPool.getCtClass( Object.class.getName() );
 		}
 		catch (NotFoundException e) {
 			throw new EnhancementException( "Could not prepare Javassist ClassPool", e );
 		}
 	}
 
 	private ClassPool buildClassPool(EnhancementContext enhancementContext) {
-		ClassPool classPool = new ClassPool( false );
-		ClassLoader loadingClassLoader = enhancementContext.getLoadingClassLoader();
+		final ClassPool classPool = new ClassPool( false );
+		final ClassLoader loadingClassLoader = enhancementContext.getLoadingClassLoader();
 		if ( loadingClassLoader != null ) {
 			classPool.appendClassPath( new LoaderClassPath( loadingClassLoader ) );
 		}
 		return classPool;
 	}
 
 	/**
 	 * Performs the enhancement.
 	 *
 	 * @param className The name of the class whose bytecode is being enhanced.
 	 * @param originalBytes The class's original (pre-enhancement) byte code
 	 *
 	 * @return The enhanced bytecode.  Could be the same as the original bytecode if the original was
 	 * already enhanced or we could not enhance it for some reason.
 	 *
-	 * @throws EnhancementException
+	 * @throws EnhancementException Indicates a problem performing the enhancement
 	 */
 	public byte[] enhance(String className, byte[] originalBytes) throws EnhancementException {
 		final CtClass managedCtClass;
 		try {
 			managedCtClass = classPool.makeClassIfNew( new ByteArrayInputStream( originalBytes ) );
 		}
 		catch (IOException e) {
 			log.unableToBuildEnhancementMetamodel( className );
 			return originalBytes;
 		}
 
 		enhance( managedCtClass );
 
-		DataOutputStream out = null;
+		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
+		final DataOutputStream out;
 		try {
-			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
 			out = new DataOutputStream( byteStream );
-			managedCtClass.toBytecode( out );
-			return byteStream.toByteArray();
-		}
-		catch (Exception e) {
-			log.unableToTransformClass( e.getMessage() );
-			throw new HibernateException( "Unable to transform class: " + e.getMessage() );
-		}
-		finally {
 			try {
-				if ( out != null ) {
+				managedCtClass.toBytecode( out );
+				return byteStream.toByteArray();
+			}
+			finally {
+				try {
 					out.close();
 				}
-			}
-			catch (IOException e) {
-				//swallow
+				catch (IOException e) {
+					//swallow
+				}
 			}
 		}
+		catch (Exception e) {
+			log.unableToTransformClass( e.getMessage() );
+			throw new HibernateException( "Unable to transform class: " + e.getMessage() );
+		}
 	}
 
 	private void enhance(CtClass managedCtClass) {
 		final String className = managedCtClass.getName();
 		log.debugf( "Enhancing %s", className );
 
 		// can't effectively enhance interfaces
 		if ( managedCtClass.isInterface() ) {
 			log.debug( "skipping enhancement : interface" );
 			return;
 		}
 
 		// skip already enhanced classes
 		final String[] interfaceNames = managedCtClass.getClassFile2().getInterfaces();
 		for ( String interfaceName : interfaceNames ) {
 			if ( ManagedEntity.class.getName().equals( interfaceName )
 					|| ManagedComposite.class.getName().equals( interfaceName ) ) {
 				log.debug( "skipping enhancement : already enhanced" );
 				return;
 			}
 		}
 
 		if ( enhancementContext.isEntityClass( managedCtClass ) ) {
 			enhanceAsEntity( managedCtClass );
 		}
 		else if ( enhancementContext.isCompositeClass( managedCtClass ) ) {
 			enhanceAsComposite( managedCtClass );
 		}
 		else {
 			log.debug( "skipping enhancement : not entity or composite" );
 		}
 	}
 
 	private void enhanceAsEntity(CtClass managedCtClass) {
 		// add the ManagedEntity interface
 		managedCtClass.addInterface( managedEntityCtClass );
 
 		enhancePersistentAttributes( managedCtClass );
 
 		addEntityInstanceHandling( managedCtClass );
 		addEntityEntryHandling( managedCtClass );
 		addLinkedPreviousHandling( managedCtClass );
 		addLinkedNextHandling( managedCtClass );
 	}
 
 	private void enhanceAsComposite(CtClass managedCtClass) {
 		enhancePersistentAttributes( managedCtClass );
 	}
 
 	private void addEntityInstanceHandling(CtClass managedCtClass) {
 		// add the ManagedEntity#$$_hibernate_getEntityInstance method
 		try {
 			managedCtClass.addMethod(
 					CtNewMethod.make(
 							objectCtClass,
-							ENTITY_INSTANCE_GETTER_NAME,
+							EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME,
 							new CtClass[0],
 							new CtClass[0],
 							"{ return this; }",
 							managedCtClass
 					)
 			);
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add EntityEntry getter",
 							managedCtClass.getName()
 					),
 					e
 			);
 		}
 	}
 
 	private void addEntityEntryHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				entityEntryCtClass,
-				ENTITY_ENTRY_FIELD_NAME,
-				ENTITY_ENTRY_GETTER_NAME,
-				ENTITY_ENTRY_SETTER_NAME
+				EnhancerConstants.ENTITY_ENTRY_FIELD_NAME,
+				EnhancerConstants.ENTITY_ENTRY_GETTER_NAME,
+				EnhancerConstants.ENTITY_ENTRY_SETTER_NAME
 		);
 	}
 
 	private void addLinkedPreviousHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				managedEntityCtClass,
-				PREVIOUS_FIELD_NAME,
-				PREVIOUS_GETTER_NAME,
-				PREVIOUS_SETTER_NAME
+				EnhancerConstants.PREVIOUS_FIELD_NAME,
+				EnhancerConstants.PREVIOUS_GETTER_NAME,
+				EnhancerConstants.PREVIOUS_SETTER_NAME
 		);
 	}
 
 	private void addLinkedNextHandling(CtClass managedCtClass) {
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				managedEntityCtClass,
-				NEXT_FIELD_NAME,
-				NEXT_GETTER_NAME,
-				NEXT_SETTER_NAME
+				EnhancerConstants.NEXT_FIELD_NAME,
+				EnhancerConstants.NEXT_GETTER_NAME,
+				EnhancerConstants.NEXT_SETTER_NAME
 		);
 	}
 
 	private AnnotationsAttribute getVisibleAnnotations(FieldInfo fieldInfo) {
 		AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) fieldInfo.getAttribute( AnnotationsAttribute.visibleTag );
 		if ( annotationsAttribute == null ) {
 			annotationsAttribute = new AnnotationsAttribute( fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag );
 			fieldInfo.addAttribute( annotationsAttribute );
 		}
 		return annotationsAttribute;
 	}
 
 	private void enhancePersistentAttributes(CtClass managedCtClass) {
 		addInterceptorHandling( managedCtClass );
 		if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) ) {
 			addInLineDirtyHandling( managedCtClass );
 		}
 
 		final IdentityHashMap<String,PersistentAttributeDescriptor> attrDescriptorMap
 				= new IdentityHashMap<String, PersistentAttributeDescriptor>();
 
 		for ( CtField persistentField : collectPersistentFields( managedCtClass ) ) {
 			attrDescriptorMap.put(
 					persistentField.getName(),
 					enhancePersistentAttribute( managedCtClass, persistentField )
 			);
 		}
 
 		// lastly, find all references to the transformed fields and replace with calls to the added reader/writer
 		transformFieldAccessesIntoReadsAndWrites( managedCtClass, attrDescriptorMap );
 	}
 
 	private PersistentAttributeDescriptor enhancePersistentAttribute(CtClass managedCtClass, CtField persistentField) {
 		try {
 			final AttributeTypeDescriptor typeDescriptor = resolveAttributeTypeDescriptor( persistentField );
 			return new PersistentAttributeDescriptor(
 					persistentField,
 					generateFieldReader( managedCtClass, persistentField, typeDescriptor ),
 					generateFieldWriter( managedCtClass, persistentField, typeDescriptor ),
 					typeDescriptor
 			);
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Unable to enhance persistent attribute [%s:%s]",
 							managedCtClass.getName(),
 							persistentField.getName()
 					),
 					e
 			);
 		}
 	}
 
 	private CtField[] collectPersistentFields(CtClass managedCtClass) {
 		// todo : drive this from the Hibernate metamodel instance...
 
 		final List<CtField> persistentFieldList = new ArrayList<CtField>();
 		for ( CtField ctField : managedCtClass.getDeclaredFields() ) {
 			// skip static fields
 			if ( Modifier.isStatic( ctField.getModifiers() ) ) {
 				continue;
 			}
 			// skip fields added by enhancement
 			if ( ctField.getName().startsWith( "$" ) ) {
 				continue;
 			}
 			if ( enhancementContext.isPersistentField( ctField ) ) {
 				persistentFieldList.add( ctField );
 			}
 		}
 
 		return enhancementContext.order( persistentFieldList.toArray( new CtField[persistentFieldList.size()]) );
 	}
 
 	private void addInterceptorHandling(CtClass managedCtClass) {
 		// interceptor handling is only needed if either:
 		//		a) in-line dirty checking has *not* been requested
 		//		b) class has lazy-loadable attributes
 		if ( enhancementContext.doDirtyCheckingInline( managedCtClass )
 				&& ! enhancementContext.hasLazyLoadableAttributes( managedCtClass ) ) {
 			return;
 		}
 
 		log.debug( "Weaving in PersistentAttributeInterceptable implementation" );
 
 
 		// add in the PersistentAttributeInterceptable contract
 		managedCtClass.addInterface( attributeInterceptableCtClass );
 
 		addFieldWithGetterAndSetter(
 				managedCtClass,
 				attributeInterceptorCtClass,
-				INTERCEPTOR_FIELD_NAME,
-				INTERCEPTOR_GETTER_NAME,
-				INTERCEPTOR_SETTER_NAME
+				EnhancerConstants.INTERCEPTOR_FIELD_NAME,
+				EnhancerConstants.INTERCEPTOR_GETTER_NAME,
+				EnhancerConstants.INTERCEPTOR_SETTER_NAME
 		);
 	}
 
 	private void addInLineDirtyHandling(CtClass managedCtClass) {
 		// todo : implement
 	}
 
 	private void addFieldWithGetterAndSetter(
 			CtClass targetClass,
 			CtClass fieldType,
 			String fieldName,
 			String getterName,
 			String setterName) {
 		final CtField theField = addField( targetClass, fieldType, fieldName, true );
 		addGetter( targetClass, theField, getterName );
 		addSetter( targetClass, theField, setterName );
 	}
 
 	private CtField addField(CtClass targetClass, CtClass fieldType, String fieldName, boolean makeTransient) {
 		final ConstPool constPool = targetClass.getClassFile().getConstPool();
 
 		final CtField theField;
 		try {
 			theField = new CtField( fieldType, fieldName, targetClass );
 			targetClass.addField( theField );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance class [%s] to add field [%s]",
 							targetClass.getName(),
 							fieldName
 					),
 					e
 			);
 		}
 
 		// make that new field (1) private, (2) transient and (3) @Transient
 		if ( makeTransient ) {
 			theField.setModifiers( theField.getModifiers() | Modifier.TRANSIENT );
 		}
 		theField.setModifiers( Modifier.setPrivate( theField.getModifiers() ) );
-		AnnotationsAttribute annotationsAttribute = getVisibleAnnotations( theField.getFieldInfo() );
+
+		final AnnotationsAttribute annotationsAttribute = getVisibleAnnotations( theField.getFieldInfo() );
 		annotationsAttribute.addAnnotation( new Annotation( Transient.class.getName(), constPool ) );
 		return theField;
 	}
 
 	private void addGetter(CtClass targetClass, CtField theField, String getterName) {
 		try {
 			targetClass.addMethod( CtNewMethod.getter( getterName, theField ) );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add getter method [%s]",
 							targetClass.getName(),
 							getterName
 					),
 					e
 			);
 		}
 	}
 
 	private void addSetter(CtClass targetClass, CtField theField, String setterName) {
 		try {
 			targetClass.addMethod( CtNewMethod.setter( setterName, theField ) );
 		}
 		catch (CannotCompileException e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add setter method [%s]",
 							targetClass.getName(),
 							setterName
 					),
 					e
 			);
 		}
 	}
 
 	private CtMethod generateFieldReader(
 			CtClass managedCtClass,
 			CtField persistentField,
 			AttributeTypeDescriptor typeDescriptor)
 			throws BadBytecode, CannotCompileException {
 
 		final FieldInfo fieldInfo = persistentField.getFieldInfo();
 		final String fieldName = fieldInfo.getName();
-		final String readerName = PERSISTENT_FIELD_READER_PREFIX + fieldName;
+		final String readerName = EnhancerConstants.PERSISTENT_FIELD_READER_PREFIX + fieldName;
 
 		// read attempts only have to deal lazy-loading support, not dirty checking; so if the field
 		// is not enabled as lazy-loadable return a plain simple getter as the reader
 		if ( ! enhancementContext.isLazyLoadable( persistentField ) ) {
 			// not lazy-loadable...
 			// EARLY RETURN!!!
 			try {
-				CtMethod reader = CtNewMethod.getter( readerName, persistentField );
+				final CtMethod reader = CtNewMethod.getter( readerName, persistentField );
 				managedCtClass.addMethod( reader );
 				return reader;
 			}
 			catch (CannotCompileException e) {
 				throw new EnhancementException(
 						String.format(
 								"Could not enhance entity class [%s] to add field reader method [%s]",
 								managedCtClass.getName(),
 								readerName
 						),
 						e
 				);
 			}
 		}
 
 		// temporary solution...
-		String methodBody = typeDescriptor.buildReadInterceptionBodyFragment( fieldName )
+		final String methodBody = typeDescriptor.buildReadInterceptionBodyFragment( fieldName )
 				+ " return this." + fieldName + ";";
 
 		try {
-			CtMethod reader = CtNewMethod.make(
+			final CtMethod reader = CtNewMethod.make(
 					Modifier.PRIVATE,
 					persistentField.getType(),
 					readerName,
 					null,
 					null,
 					"{" + methodBody + "}",
 					managedCtClass
 			);
 			managedCtClass.addMethod( reader );
 			return reader;
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add field reader method [%s]",
 							managedCtClass.getName(),
 							readerName
 					),
 					e
 			);
 		}
 	}
 
 	private CtMethod generateFieldWriter(
 			CtClass managedCtClass,
 			CtField persistentField,
 			AttributeTypeDescriptor typeDescriptor) {
 
 		final FieldInfo fieldInfo = persistentField.getFieldInfo();
 		final String fieldName = fieldInfo.getName();
-		final String writerName = PERSISTENT_FIELD_WRITER_PREFIX + fieldName;
+		final String writerName = EnhancerConstants.PERSISTENT_FIELD_WRITER_PREFIX + fieldName;
 
 		final CtMethod writer;
 
 		try {
 			if ( ! enhancementContext.isLazyLoadable( persistentField ) ) {
 				// not lazy-loadable...
 				writer = CtNewMethod.setter( writerName, persistentField );
 			}
 			else {
-				String methodBody = typeDescriptor.buildWriteInterceptionBodyFragment( fieldName );
+				final String methodBody = typeDescriptor.buildWriteInterceptionBodyFragment( fieldName );
 				writer = CtNewMethod.make(
 						Modifier.PRIVATE,
 						CtClass.voidType,
 						writerName,
 						new CtClass[] { persistentField.getType() },
 						null,
 						"{" + methodBody + "}",
 						managedCtClass
 				);
 			}
 
 			if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) ) {
 				writer.insertBefore( typeDescriptor.buildInLineDirtyCheckingBodyFragment( fieldName ) );
 			}
 
 			managedCtClass.addMethod( writer );
 			return writer;
 		}
 		catch (Exception e) {
 			throw new EnhancementException(
 					String.format(
 							"Could not enhance entity class [%s] to add field writer method [%s]",
 							managedCtClass.getName(),
 							writerName
 					),
 					e
 			);
 		}
 	}
 
 	private void transformFieldAccessesIntoReadsAndWrites(
 			CtClass managedCtClass,
 			IdentityHashMap<String, PersistentAttributeDescriptor> attributeDescriptorMap) {
 
 		final ConstPool constPool = managedCtClass.getClassFile().getConstPool();
 
 		for ( Object oMethod : managedCtClass.getClassFile().getMethods() ) {
 			final MethodInfo methodInfo = (MethodInfo) oMethod;
 			final String methodName = methodInfo.getName();
 
 			// skip methods added by enhancement
-			if ( methodName.startsWith( PERSISTENT_FIELD_READER_PREFIX )
-					|| methodName.startsWith( PERSISTENT_FIELD_WRITER_PREFIX )
-					|| methodName.equals( ENTITY_INSTANCE_GETTER_NAME )
-					|| methodName.equals( ENTITY_ENTRY_GETTER_NAME )
-					|| methodName.equals( ENTITY_ENTRY_SETTER_NAME )
-					|| methodName.equals( PREVIOUS_GETTER_NAME )
-					|| methodName.equals( PREVIOUS_SETTER_NAME )
-					|| methodName.equals( NEXT_GETTER_NAME )
-					|| methodName.equals( NEXT_SETTER_NAME ) ) {
+			if ( methodName.startsWith( EnhancerConstants.PERSISTENT_FIELD_READER_PREFIX )
+					|| methodName.startsWith( EnhancerConstants.PERSISTENT_FIELD_WRITER_PREFIX )
+					|| methodName.equals( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME )
+					|| methodName.equals( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME )
+					|| methodName.equals( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME )
+					|| methodName.equals( EnhancerConstants.PREVIOUS_GETTER_NAME )
+					|| methodName.equals( EnhancerConstants.PREVIOUS_SETTER_NAME )
+					|| methodName.equals( EnhancerConstants.NEXT_GETTER_NAME )
+					|| methodName.equals( EnhancerConstants.NEXT_SETTER_NAME ) ) {
 				continue;
 			}
 
 			final CodeAttribute codeAttr = methodInfo.getCodeAttribute();
 			if ( codeAttr == null ) {
 				// would indicate an abstract method, continue to next method
 				continue;
 			}
 
 			try {
-				CodeIterator itr = codeAttr.iterator();
+				final CodeIterator itr = codeAttr.iterator();
 				while ( itr.hasNext() ) {
-					int index = itr.next();
-					int op = itr.byteAt( index );
+					final int index = itr.next();
+					final int op = itr.byteAt( index );
 					if ( op != Opcode.PUTFIELD && op != Opcode.GETFIELD ) {
 						continue;
 					}
 
-					int constIndex = itr.u16bitAt( index+1 );
+					final int constIndex = itr.u16bitAt( index+1 );
 
 					final String fieldName = constPool.getFieldrefName( constIndex );
 					final PersistentAttributeDescriptor attributeDescriptor = attributeDescriptorMap.get( fieldName );
 
 					if ( attributeDescriptor == null ) {
 						// its not a field we have enhanced for interception, so skip it
 						continue;
 					}
 
 					log.tracef(
 							"Transforming access to field [%s] from method [%s]",
 							fieldName,
 							methodName
 					);
 
 					if ( op == Opcode.GETFIELD ) {
-						int read_method_index = constPool.addMethodrefInfo(
+						final int readMethodIndex = constPool.addMethodrefInfo(
 								constPool.getThisClassInfo(),
 								attributeDescriptor.getReader().getName(),
 								attributeDescriptor.getReader().getSignature()
 						);
 						itr.writeByte( Opcode.INVOKESPECIAL, index );
-						itr.write16bit( read_method_index, index+1 );
+						itr.write16bit( readMethodIndex, index+1 );
 					}
 					else {
-						int write_method_index = constPool.addMethodrefInfo(
+						final int writeMethodIndex = constPool.addMethodrefInfo(
 								constPool.getThisClassInfo(),
 								attributeDescriptor.getWriter().getName(),
 								attributeDescriptor.getWriter().getSignature()
 						);
 						itr.writeByte( Opcode.INVOKESPECIAL, index );
-						itr.write16bit( write_method_index, index+1 );
+						itr.write16bit( writeMethodIndex, index+1 );
 					}
 				}
 
-				StackMapTable smt = MapMaker.make( classPool, methodInfo );
-				methodInfo.getCodeAttribute().setAttribute(smt);
+				final StackMapTable smt = MapMaker.make( classPool, methodInfo );
+				methodInfo.getCodeAttribute().setAttribute( smt );
 			}
 			catch (BadBytecode e) {
 				throw new EnhancementException(
 						"Unable to perform field access transformation in method : " + methodName,
 						e
 				);
 			}
 		}
 	}
 
 	private static class PersistentAttributeDescriptor {
 		private final CtField field;
 		private final CtMethod reader;
 		private final CtMethod writer;
 		private final AttributeTypeDescriptor typeDescriptor;
 
 		private PersistentAttributeDescriptor(
 				CtField field,
 				CtMethod reader,
 				CtMethod writer,
 				AttributeTypeDescriptor typeDescriptor) {
 			this.field = field;
 			this.reader = reader;
 			this.writer = writer;
 			this.typeDescriptor = typeDescriptor;
 		}
 
 		public CtField getField() {
 			return field;
 		}
 
 		public CtMethod getReader() {
 			return reader;
 		}
 
 		public CtMethod getWriter() {
 			return writer;
 		}
 
 		public AttributeTypeDescriptor getTypeDescriptor() {
 			return typeDescriptor;
 		}
 	}
 
 	private static interface AttributeTypeDescriptor {
 		public String buildReadInterceptionBodyFragment(String fieldName);
 		public String buildWriteInterceptionBodyFragment(String fieldName);
 		public String buildInLineDirtyCheckingBodyFragment(String fieldName);
 	}
 
 	private AttributeTypeDescriptor resolveAttributeTypeDescriptor(CtField persistentField) throws NotFoundException {
 		// for now cheat... we know we only have Object fields
 		if ( persistentField.getType() == CtClass.booleanType ) {
 			return BOOLEAN_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.byteType ) {
 			return BYTE_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.charType ) {
 			return CHAR_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.shortType ) {
 			return SHORT_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.intType ) {
 			return INT_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.longType ) {
 			return LONG_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.doubleType ) {
 			return DOUBLE_DESCRIPTOR;
 		}
 		else if ( persistentField.getType() == CtClass.floatType ) {
 			return FLOAT_DESCRIPTOR;
 		}
 		else {
 			return new ObjectAttributeTypeDescriptor( persistentField.getType() );
 		}
 	}
 
-	private static abstract class AbstractAttributeTypeDescriptor implements AttributeTypeDescriptor {
+	private abstract static class AbstractAttributeTypeDescriptor implements AttributeTypeDescriptor {
 		@Override
 		public String buildInLineDirtyCheckingBodyFragment(String fieldName) {
 			// for now...
 			// todo : hook-in in-lined dirty checking
 			return String.format(
 					"System.out.println( \"DIRTY CHECK (%1$s) : \" + this.%1$s + \" -> \" + $1 + \" (dirty=\" + (this.%1$s != $1) +\")\" );",
 					fieldName
 			);
 		}
 	}
 
 	private static class ObjectAttributeTypeDescriptor extends AbstractAttributeTypeDescriptor {
 		private final CtClass concreteType;
 
 		private ObjectAttributeTypeDescriptor(CtClass concreteType) {
 			this.concreteType = concreteType;
 		}
 
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = (%2$s) $$_hibernate_getInterceptor().readObject(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName,
 					concreteType.getName()
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"%2$s localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = (%2$s) $$_hibernate_getInterceptor().writeObject(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName,
 					concreteType.getName()
 			);
 		}
 	}
 
 	private static final AttributeTypeDescriptor BOOLEAN_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = $$_hibernate_getInterceptor().readBoolean(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"boolean localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = $$_hibernate_getInterceptor().writeBoolean(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor BYTE_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 							"this.%1$s = $$_hibernate_getInterceptor().readByte(this, \"%1$s\", this.%1$s); " +
 							"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"byte localVar = $1;" +
 							"if ( $$_hibernate_getInterceptor() != null ) {" +
 							"localVar = $$_hibernate_getInterceptor().writeByte(this, \"%1$s\", this.%1$s, $1);" +
 							"}" +
 							"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor CHAR_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readChar(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"char localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeChar(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor SHORT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readShort(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"short localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeShort(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor INT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readInt(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"int localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeInt(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor LONG_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readLong(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"long localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeLong(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor DOUBLE_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readDouble(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"double localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeDouble(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 
 	private static final AttributeTypeDescriptor FLOAT_DESCRIPTOR = new AbstractAttributeTypeDescriptor() {
 		@Override
 		public String buildReadInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"if ( $$_hibernate_getInterceptor() != null ) { " +
 						"this.%1$s = $$_hibernate_getInterceptor().readFloat(this, \"%1$s\", this.%1$s); " +
 					"}",
 					fieldName
 			);
 		}
 
 		@Override
 		public String buildWriteInterceptionBodyFragment(String fieldName) {
 			return String.format(
 					"float localVar = $1;" +
 					"if ( $$_hibernate_getInterceptor() != null ) {" +
 						"localVar = $$_hibernate_getInterceptor().writeFloat(this, \"%1$s\", this.%1$s, $1);" +
 					"}" +
 					"this.%1$s = localVar;",
 					fieldName
 			);
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java
new file mode 100644
index 0000000000..5c80f7a068
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/EnhancerConstants.java
@@ -0,0 +1,133 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.bytecode.enhance.spi;
+
+/**
+ * Constants used during enhancement.
+ *
+ * @author Steve Ebersole
+ */
+public class EnhancerConstants {
+	/**
+	 * Prefix for persistent-field reader methods.
+	 */
+	public static final String PERSISTENT_FIELD_READER_PREFIX = "$$_hibernate_read_";
+
+	/**
+	 * Prefix for persistent-field writer methods.
+	 */
+	public static final String PERSISTENT_FIELD_WRITER_PREFIX = "$$_hibernate_write_";
+
+	/**
+	 * Name of the method used to get reference the the entity instance (this in the case of enhanced classes).
+	 */
+	public static final String ENTITY_INSTANCE_GETTER_NAME = "$$_hibernate_getEntityInstance";
+
+	/**
+	 * Name of the field used to hold the {@link org.hibernate.engine.spi.EntityEntry}
+	 */
+	public static final String ENTITY_ENTRY_FIELD_NAME = "$$_hibernate_entityEntryHolder";
+
+	/**
+	 * Name of the method used to read the {@link org.hibernate.engine.spi.EntityEntry} field.
+	 *
+	 * @see #ENTITY_ENTRY_FIELD_NAME
+	 */
+	public static final String ENTITY_ENTRY_GETTER_NAME = "$$_hibernate_getEntityEntry";
+
+	/**
+	 * Name of the method used to write the {@link org.hibernate.engine.spi.EntityEntry} field.
+	 *
+	 * @see #ENTITY_ENTRY_FIELD_NAME
+	 */
+	public static final String ENTITY_ENTRY_SETTER_NAME = "$$_hibernate_setEntityEntry";
+
+	/**
+	 * Name of the field used to hold the previous {@link org.hibernate.engine.spi.ManagedEntity}.
+	 *
+	 * Together, previous/next are used to define a "linked list"
+	 *
+	 * @see #NEXT_FIELD_NAME
+	 */
+	public static final String PREVIOUS_FIELD_NAME = "$$_hibernate_previousManagedEntity";
+
+	/**
+	 * Name of the method used to read the previous {@link org.hibernate.engine.spi.ManagedEntity} field
+	 *
+	 * @see #PREVIOUS_FIELD_NAME
+	 */
+	public static final String PREVIOUS_GETTER_NAME = "$$_hibernate_getPreviousManagedEntity";
+
+	/**
+	 * Name of the method used to write the previous {@link org.hibernate.engine.spi.ManagedEntity} field
+	 *
+	 * @see #PREVIOUS_FIELD_NAME
+	 */
+	public static final String PREVIOUS_SETTER_NAME = "$$_hibernate_setPreviousManagedEntity";
+
+	/**
+	 * Name of the field used to hold the previous {@link org.hibernate.engine.spi.ManagedEntity}.
+	 *
+	 * Together, previous/next are used to define a "linked list"
+	 *
+	 * @see #PREVIOUS_FIELD_NAME
+	 */
+	public static final String NEXT_FIELD_NAME = "$$_hibernate_nextManagedEntity";
+
+	/**
+	 * Name of the method used to read the next {@link org.hibernate.engine.spi.ManagedEntity} field
+	 *
+	 * @see #NEXT_FIELD_NAME
+	 */
+	public static final String NEXT_GETTER_NAME = "$$_hibernate_getNextManagedEntity";
+
+	/**
+	 * Name of the method used to write the next {@link org.hibernate.engine.spi.ManagedEntity} field
+	 *
+	 * @see #NEXT_FIELD_NAME
+	 */
+	public static final String NEXT_SETTER_NAME = "$$_hibernate_setNextManagedEntity";
+
+	/**
+	 * Name of the field used to store the {@link org.hibernate.engine.spi.PersistentAttributeInterceptable}.
+	 */
+	public static final String INTERCEPTOR_FIELD_NAME = "$$_hibernate_attributeInterceptor";
+
+	/**
+	 * Name of the method used to read the interceptor
+	 *
+	 * @see #INTERCEPTOR_FIELD_NAME
+	 */
+	public static final String INTERCEPTOR_GETTER_NAME = "$$_hibernate_getInterceptor";
+
+	/**
+	 * Name of the method used to write the interceptor
+	 *
+	 * @see #INTERCEPTOR_FIELD_NAME
+	 */
+	public static final String INTERCEPTOR_SETTER_NAME = "$$_hibernate_setInterceptor";
+
+	private EnhancerConstants() {
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/package-info.java
new file mode 100644
index 0000000000..ab08393eee
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Package defining bytecode code enhancement (instrumentation) support.
+ */
+package org.hibernate.bytecode.enhance.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
index 8d87441a24..49a9c47465 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
@@ -1,147 +1,181 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.instrumentation.internal;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Helper class for dealing with enhanced entity classes.
  *
  * These operations are expensive.  They are only meant to be used when code does not have access to a
  * SessionFactory (namely from the instrumentation tasks).  When code has access to a SessionFactory,
  * {@link org.hibernate.bytecode.spi.EntityInstrumentationMetadata} should be used instead to query the
  * instrumentation state.  EntityInstrumentationMetadata is accessed from the
  * {@link org.hibernate.persister.entity.EntityPersister} via the
  * {@link org.hibernate.persister.entity.EntityPersister#getInstrumentationMetadata()} method.
  *
  * @author Steve Ebersole
  */
 public class FieldInterceptionHelper {
 	private static final Set<Delegate> INSTRUMENTATION_DELEGATES = buildInstrumentationDelegates();
 
 	private static Set<Delegate> buildInstrumentationDelegates() {
-		HashSet<Delegate> delegates = new HashSet<Delegate>();
+		final HashSet<Delegate> delegates = new HashSet<Delegate>();
 		delegates.add( JavassistDelegate.INSTANCE );
 		return delegates;
 	}
 
-	private FieldInterceptionHelper() {
-	}
-
+	/**
+	 * Utility to check to see if a given entity class is instrumented.
+	 *
+	 * @param entityClass The entity class to check
+	 *
+	 * @return {@code true} if it has been instrumented; {@code false} otherwise
+	 */
 	public static boolean isInstrumented(Class entityClass) {
 		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
 			if ( delegate.isInstrumented( entityClass ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
-	public static boolean isInstrumented(Object entity) {
-		return entity != null && isInstrumented( entity.getClass() );
+	/**
+	 * Utility to check to see if a given object is an instance of an instrumented class.  If the instance
+	 * is {@code null}, the check returns {@code false}
+	 *
+	 * @param object The object to check
+	 *
+	 * @return {@code true} if it has been instrumented; {@code false} otherwise
+	 */
+	public static boolean isInstrumented(Object object) {
+		return object != null && isInstrumented( object.getClass() );
 	}
 
-	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-		if ( entity == null ) {
+	/**
+	 * Assuming the given object is an enhanced entity, extract and return its interceptor.  Will
+	 * return {@code null} if object is {@code null}, or if the object was deemed to not be
+	 * instrumented
+	 *
+	 * @param object The object from which to extract the interceptor
+	 *
+	 * @return The extracted interceptor, or {@code null}
+	 */
+	public static FieldInterceptor extractFieldInterceptor(Object object) {
+		if ( object == null ) {
 			return null;
 		}
 		FieldInterceptor interceptor = null;
 		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
-			interceptor = delegate.extractInterceptor( entity );
+			interceptor = delegate.extractInterceptor( object );
 			if ( interceptor != null ) {
 				break;
 			}
 		}
 		return interceptor;
 	}
 
-
+	/**
+	 * Assuming the given object is an enhanced entity, inject a field interceptor.
+	 *
+	 * @param entity The entity instance
+	 * @param entityName The entity name
+	 * @param uninitializedFieldNames The names of any uninitialized fields
+	 * @param session The session
+	 *
+	 * @return The injected interceptor
+	 */
 	public static FieldInterceptor injectFieldInterceptor(
 			Object entity,
-	        String entityName,
-	        Set uninitializedFieldNames,
-	        SessionImplementor session) {
+			String entityName,
+			Set uninitializedFieldNames,
+			SessionImplementor session) {
 		if ( entity == null ) {
 			return null;
 		}
 		FieldInterceptor interceptor = null;
 		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
 			interceptor = delegate.injectInterceptor( entity, entityName, uninitializedFieldNames, session );
 			if ( interceptor != null ) {
 				break;
 			}
 		}
 		return interceptor;
 	}
 
 	private static interface Delegate {
 		public boolean isInstrumented(Class classToCheck);
 		public FieldInterceptor extractInterceptor(Object entity);
 		public FieldInterceptor injectInterceptor(Object entity, String entityName, Set uninitializedFieldNames, SessionImplementor session);
 	}
 
 	private static class JavassistDelegate implements Delegate {
 		public static final JavassistDelegate INSTANCE = new JavassistDelegate();
 		public static final String MARKER = "org.hibernate.bytecode.internal.javassist.FieldHandled";
 
 		@Override
 		public boolean isInstrumented(Class classToCheck) {
 			for ( Class definedInterface : classToCheck.getInterfaces() ) {
 				if ( MARKER.equals( definedInterface.getName() ) ) {
 					return true;
 				}
 			}
 			return false;
 		}
 
 		@Override
 		public FieldInterceptor extractInterceptor(Object entity) {
 			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
 				if ( MARKER.equals( definedInterface.getName() ) ) {
 					return JavassistHelper.extractFieldInterceptor( entity );
 				}
 			}
 			return null;
 		}
 
 		@Override
 		public FieldInterceptor injectInterceptor(
 				Object entity,
 				String entityName,
 				Set uninitializedFieldNames,
 				SessionImplementor session) {
 			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
 				if ( MARKER.equals( definedInterface.getName() ) ) {
 					return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
 				}
 			}
 			return null;
 		}
 	}
+
+	private FieldInterceptionHelper() {
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
index 4e245b5fdc..1d37ebd961 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
@@ -1,169 +1,163 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.instrumentation.internal.javassist;
 
 import java.io.Serializable;
 import java.util.Set;
 
 import org.hibernate.bytecode.instrumentation.spi.AbstractFieldInterceptor;
 import org.hibernate.bytecode.internal.javassist.FieldHandler;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * A field-level interceptor that initializes lazily fetched properties.
  * This interceptor can be attached to classes instrumented by Javassist.
  * Note that this implementation assumes that the instance variable
  * name is the same as the name of the persistent property that must
  * be loaded.
  * </p>
  * Note: most of the interesting functionality here is farmed off
  * to the super-class.  The stuff here mainly acts as an adapter to the
  * Javassist-specific functionality, routing interception through
  * the super-class's intercept() method
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnnecessaryUnboxing", "UnnecessaryBoxing"})
-public final class FieldInterceptorImpl extends AbstractFieldInterceptor implements FieldHandler, Serializable {
+final class FieldInterceptorImpl extends AbstractFieldInterceptor implements FieldHandler, Serializable {
 
 	FieldInterceptorImpl(SessionImplementor session, Set uninitializedFields, String entityName) {
 		super( session, uninitializedFields, entityName );
 	}
 
 
 	// FieldHandler impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean readBoolean(Object target, String name, boolean oldValue) {
-		return ( ( Boolean ) intercept( target, name, oldValue ) )
-				.booleanValue();
+		return ( (Boolean) intercept( target, name, oldValue ) ).booleanValue();
 	}
 
 	public byte readByte(Object target, String name, byte oldValue) {
-		return ( ( Byte ) intercept( target, name, Byte.valueOf( oldValue ) ) ).byteValue();
+		return ( (Byte) intercept( target, name, Byte.valueOf( oldValue ) ) ).byteValue();
 	}
 
 	public char readChar(Object target, String name, char oldValue) {
-		return ( ( Character ) intercept( target, name, Character.valueOf( oldValue ) ) )
-				.charValue();
+		return ( (Character) intercept( target, name, Character.valueOf( oldValue ) ) ).charValue();
 	}
 
 	public double readDouble(Object target, String name, double oldValue) {
-		return ( ( Double ) intercept( target, name, Double.valueOf( oldValue ) ) )
-				.doubleValue();
+		return ( (Double) intercept( target, name, Double.valueOf( oldValue ) ) ).doubleValue();
 	}
 
 	public float readFloat(Object target, String name, float oldValue) {
-		return ( ( Float ) intercept( target, name, Float.valueOf( oldValue ) ) )
-				.floatValue();
+		return ( (Float) intercept( target, name, Float.valueOf( oldValue ) ) ).floatValue();
 	}
 
 	public int readInt(Object target, String name, int oldValue) {
-		return ( ( Integer ) intercept( target, name, Integer.valueOf( oldValue ) ) );
+		return ( (Integer) intercept( target, name, Integer.valueOf( oldValue ) ) );
 	}
 
 	public long readLong(Object target, String name, long oldValue) {
-		return ( ( Long ) intercept( target, name, Long.valueOf( oldValue ) ) ).longValue();
+		return ( (Long) intercept( target, name, Long.valueOf( oldValue ) ) ).longValue();
 	}
 
 	public short readShort(Object target, String name, short oldValue) {
-		return ( ( Short ) intercept( target, name, Short.valueOf( oldValue ) ) )
-				.shortValue();
+		return ( (Short) intercept( target, name, Short.valueOf( oldValue ) ) ).shortValue();
 	}
 
 	public Object readObject(Object target, String name, Object oldValue) {
 		Object value = intercept( target, name, oldValue );
-		if (value instanceof HibernateProxy) {
-			LazyInitializer li = ( (HibernateProxy) value ).getHibernateLazyInitializer();
+		if ( value instanceof HibernateProxy ) {
+			final LazyInitializer li = ( (HibernateProxy) value ).getHibernateLazyInitializer();
 			if ( li.isUnwrap() ) {
 				value = li.getImplementation();
 			}
 		}
 		return value;
 	}
 
 	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {
 		dirty();
 		intercept( target, name, oldValue );
 		return newValue;
 	}
 
 	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
 		dirty();
 		intercept( target, name, Byte.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public char writeChar(Object target, String name, char oldValue, char newValue) {
 		dirty();
 		intercept( target, name, Character.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public double writeDouble(Object target, String name, double oldValue, double newValue) {
 		dirty();
 		intercept( target, name, Double.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public float writeFloat(Object target, String name, float oldValue, float newValue) {
 		dirty();
 		intercept( target, name, Float.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public int writeInt(Object target, String name, int oldValue, int newValue) {
 		dirty();
 		intercept( target, name, Integer.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public long writeLong(Object target, String name, long oldValue, long newValue) {
 		dirty();
 		intercept( target, name, Long.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public short writeShort(Object target, String name, short oldValue, short newValue) {
 		dirty();
 		intercept( target, name, Short.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {
 		dirty();
 		intercept( target, name, oldValue );
 		return newValue;
 	}
 
 	public String toString() {
-		return "FieldInterceptorImpl(" +
-		       "entityName=" + getEntityName() +
-		       ",dirty=" + isDirty() +
-		       ",uninitializedFields=" + getUninitializedFields() +
-		       ')';
+		return "FieldInterceptorImpl(entityName=" + getEntityName() +
+				",dirty=" + isDirty() +
+				",uninitializedFields=" + getUninitializedFields() +
+				')';
 	}
 
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
index acd49547c0..07cb95e35d 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
@@ -1,52 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.instrumentation.internal.javassist;
 
 import java.util.Set;
 
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.internal.javassist.FieldHandled;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
+ * Javassist specific helper
+ *
  * @author Steve Ebersole
  */
 public class JavassistHelper {
 	private JavassistHelper() {
 	}
 
+	/**
+	 * Perform the Javassist-specific field interceptor extraction
+	 *
+	 * @param entity The entity from which to extract the interceptor
+	 *
+	 * @return The extracted interceptor
+	 */
 	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-		return ( FieldInterceptor ) ( ( FieldHandled ) entity ).getFieldHandler();
+		return (FieldInterceptor) ( (FieldHandled) entity ).getFieldHandler();
 	}
 
+	/**
+	 * Perform the Javassist-specific field interceptor injection
+	 *
+	 * @param entity The entity instance
+	 * @param entityName The entity name
+	 * @param uninitializedFieldNames The names of any uninitialized fields
+	 * @param session The session
+	 *
+	 * @return The generated and injected interceptor
+	 */
 	public static FieldInterceptor injectFieldInterceptor(
 			Object entity,
-	        String entityName,
-	        Set uninitializedFieldNames,
-	        SessionImplementor session) {
-		FieldInterceptorImpl fieldInterceptor = new FieldInterceptorImpl( session, uninitializedFieldNames, entityName );
-		( ( FieldHandled ) entity ).setFieldHandler( fieldInterceptor );
+			String entityName,
+			Set uninitializedFieldNames,
+			SessionImplementor session) {
+		final FieldInterceptorImpl fieldInterceptor = new FieldInterceptorImpl( session, uninitializedFieldNames, entityName );
+		( (FieldHandled) entity ).setFieldHandler( fieldInterceptor );
 		return fieldInterceptor;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java
new file mode 100644
index 0000000000..ff7d51f4c7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Javassist support internals
+ */
+package org.hibernate.bytecode.instrumentation.internal.javassist;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java
new file mode 100644
index 0000000000..601ddd6b55
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Bytecode instrumentation internals
+ */
+package org.hibernate.bytecode.instrumentation.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
index 5aaaa8b687..0cd75cebe4 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
@@ -1,125 +1,162 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.instrumentation.spi;
 import java.io.Serializable;
 import java.util.Set;
 
 import org.hibernate.LazyInitializationException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
+ * Base support for FieldInterceptor implementations.
+ *
  * @author Steve Ebersole
  */
 public abstract class AbstractFieldInterceptor implements FieldInterceptor, Serializable {
 
 	private transient SessionImplementor session;
 	private Set uninitializedFields;
 	private final String entityName;
 
 	private transient boolean initializing;
 	private boolean dirty;
 
 	protected AbstractFieldInterceptor(SessionImplementor session, Set uninitializedFields, String entityName) {
 		this.session = session;
 		this.uninitializedFields = uninitializedFields;
 		this.entityName = entityName;
 	}
 
 
 	// FieldInterceptor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public final void setSession(SessionImplementor session) {
 		this.session = session;
 	}
 
+	@Override
 	public final boolean isInitialized() {
 		return uninitializedFields == null || uninitializedFields.size() == 0;
 	}
 
+	@Override
 	public final boolean isInitialized(String field) {
 		return uninitializedFields == null || !uninitializedFields.contains( field );
 	}
 
+	@Override
 	public final void dirty() {
 		dirty = true;
 	}
 
+	@Override
 	public final boolean isDirty() {
 		return dirty;
 	}
 
+	@Override
 	public final void clearDirty() {
 		dirty = false;
 	}
 
 
 	// subclass accesses ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	/**
+	 * Interception of access to the named field
+	 *
+	 * @param target The call target
+	 * @param fieldName The name of the field.
+	 * @param value The value.
+	 *
+	 * @return ?
+	 */
 	protected final Object intercept(Object target, String fieldName, Object value) {
 		if ( initializing ) {
 			return value;
 		}
 
 		if ( uninitializedFields != null && uninitializedFields.contains( fieldName ) ) {
 			if ( session == null ) {
 				throw new LazyInitializationException( "entity with lazy properties is not associated with a session" );
 			}
 			else if ( !session.isOpen() || !session.isConnected() ) {
 				throw new LazyInitializationException( "session is not connected" );
 			}
 
 			final Object result;
 			initializing = true;
 			try {
-				result = ( ( LazyPropertyInitializer ) session.getFactory()
-						.getEntityPersister( entityName ) )
+				result = ( (LazyPropertyInitializer) session.getFactory().getEntityPersister( entityName ) )
 						.initializeLazyProperty( fieldName, target, session );
 			}
 			finally {
 				initializing = false;
 			}
-			uninitializedFields = null; //let's assume that there is only one lazy fetch group, for now!
+			// let's assume that there is only one lazy fetch group, for now!
+			uninitializedFields = null;
 			return result;
 		}
 		else {
 			return value;
 		}
 	}
 
+	/**
+	 * Access to the session
+	 *
+	 * @return The associated session
+	 */
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
+	/**
+	 * Access to all currently uninitialized fields
+	 *
+	 * @return The name of all currently uninitialized fields
+	 */
 	public final Set getUninitializedFields() {
 		return uninitializedFields;
 	}
 
+	/**
+	 * Access to the intercepted entity name
+	 *
+	 * @return The entity name
+	 */
 	public final String getEntityName() {
 		return entityName;
 	}
 
+	/**
+	 * Is the instance currently initializing?
+	 *
+	 * @return true/false.
+	 */
 	public final boolean isInitializing() {
 		return initializing;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
index c637672004..4dbc2b01cb 100755
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
@@ -1,60 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.instrumentation.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Contract for controlling how lazy properties get initialized.
  * 
  * @author Gavin King
  */
 public interface LazyPropertyInitializer {
 
 	/**
-	 * Marker value for uninitialized properties
+	 * Marker value for uninitialized properties.
 	 */
 	public static final Serializable UNFETCHED_PROPERTY = new Serializable() {
+		@Override
 		public String toString() {
 			return "<lazy>";
 		}
+
 		public Object readResolve() {
 			return UNFETCHED_PROPERTY;
 		}
 	};
 
 	/**
-	 * Initialize the property, and return its new value
+	 * Initialize the property, and return its new value.
 	 *
 	 * @param fieldName The name of the field being initialized
 	 * @param entity The entity on which the initialization is occurring
 	 * @param session The session from which the initialization originated.
 	 *
 	 * @return ?
 	 */
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java
new file mode 100644
index 0000000000..3b0e974256
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java
@@ -0,0 +1,6 @@
+/**
+ * Package defining bytecode code enhancement (instrumentation) support.
+ *
+ * This package should mostly be considered deprecated in favor of {@link org.hibernate.bytecode.enhance}
+ */
+package org.hibernate.bytecode.instrumentation.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
index 5ffd661467..9cba73ae3d 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
@@ -1,104 +1,118 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.Serializable;
 
 import org.hibernate.PropertyAccessException;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
+import org.hibernate.cfg.AvailableSettings;
 
 /**
  * The {@link org.hibernate.bytecode.spi.ReflectionOptimizer.AccessOptimizer} implementation for Javassist
  * which simply acts as an adapter to the {@link BulkAccessor} class.
  *
  * @author Steve Ebersole
  */
 public class AccessOptimizerAdapter implements ReflectionOptimizer.AccessOptimizer, Serializable {
 
-	public static final String PROPERTY_GET_EXCEPTION =
-		"exception getting property value with Javassist (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
+	private static final String PROPERTY_GET_EXCEPTION = String.format(
+			"exception getting property value with Javassist (set %s to false for more info)",
+			AvailableSettings.USE_REFLECTION_OPTIMIZER
+	);
 
-	public static final String PROPERTY_SET_EXCEPTION =
-		"exception setting property value with Javassist (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
+	private static final String PROPERTY_SET_EXCEPTION =  String.format(
+			"exception setting property value with Javassist (set %s to false for more info)",
+			AvailableSettings.USE_REFLECTION_OPTIMIZER
+	);
 
 	private final BulkAccessor bulkAccessor;
 	private final Class mappedClass;
 
+	/**
+	 * Constructs an AccessOptimizerAdapter
+	 *
+	 * @param bulkAccessor The bulk accessor to use
+	 * @param mappedClass The mapped class
+	 */
 	public AccessOptimizerAdapter(BulkAccessor bulkAccessor, Class mappedClass) {
 		this.bulkAccessor = bulkAccessor;
 		this.mappedClass = mappedClass;
 	}
 
+	@Override
 	public String[] getPropertyNames() {
 		return bulkAccessor.getGetters();
 	}
 
+	@Override
 	public Object[] getPropertyValues(Object object) {
 		try {
 			return bulkAccessor.getPropertyValues( object );
 		}
 		catch ( Throwable t ) {
 			throw new PropertyAccessException(
 					t,
-			        PROPERTY_GET_EXCEPTION,
-			        false,
-			        mappedClass,
-			        getterName( t, bulkAccessor )
-				);
+					PROPERTY_GET_EXCEPTION,
+					false,
+					mappedClass,
+					getterName( t, bulkAccessor )
+			);
 		}
 	}
 
+	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		try {
 			bulkAccessor.setPropertyValues( object, values );
 		}
 		catch ( Throwable t ) {
 			throw new PropertyAccessException(
 					t,
-			        PROPERTY_SET_EXCEPTION,
-			        true,
-			        mappedClass,
-			        setterName( t, bulkAccessor )
+					PROPERTY_SET_EXCEPTION,
+					true,
+					mappedClass,
+					setterName( t, bulkAccessor )
 			);
 		}
 	}
 
 	private static String setterName(Throwable t, BulkAccessor accessor) {
 		if (t instanceof BulkAccessorException ) {
 			return accessor.getSetters()[ ( (BulkAccessorException) t ).getIndex() ];
 		}
 		else {
 			return "?";
 		}
 	}
 
 	private static String getterName(Throwable t, BulkAccessor accessor) {
 		if (t instanceof BulkAccessorException ) {
 			return accessor.getGetters()[ ( (BulkAccessorException) t ).getIndex() ];
 		}
 		else {
 			return "?";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
index 940c1a64dc..47fcdcb97c 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
@@ -1,114 +1,119 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.Serializable;
 
 /**
- * A JavaBean accessor.
- * <p/>
- * <p>This object provides methods that set/get multiple properties
- * of a JavaBean at once.  This class and its support classes have been
- * developed for the comaptibility with cglib
- * (<tt>http://cglib.sourceforge.net/</tt>).
+ * A JavaBean bulk accessor, which provides methods capable of getting/setting multiple properties
+ * of a JavaBean at once.
  *
  * @author Muga Nishizawa
- * @author modified by Shigeru Chiba
+ * @author Shigeru Chiba
  */
 public abstract class BulkAccessor implements Serializable {
 	protected Class target;
 	protected String[] getters, setters;
 	protected Class[] types;
 
-	protected BulkAccessor() {
+	/**
+	 * Creates a new instance of BulkAccessor.  The created instance provides methods for setting/getting
+	 * specified properties at once.
+	 *
+	 * @param beanClass the class of the JavaBeans accessed
+	 *                  through the created object.
+	 * @param getters   the names of setter methods for specified properties.
+	 * @param setters   the names of getter methods for specified properties.
+	 * @param types     the types of specified properties.
+	 *
+	 * @return The created bulk accessor
+	 */
+	public static BulkAccessor create(
+			Class beanClass,
+			String[] getters,
+			String[] setters,
+			Class[] types) {
+		final BulkAccessorFactory factory = new BulkAccessorFactory( beanClass, getters, setters, types );
+		return factory.create();
 	}
 
 	/**
 	 * Obtains the values of properties of a given bean.
 	 *
 	 * @param bean   JavaBean.
 	 * @param values the obtained values are stored in this array.
 	 */
 	public abstract void getPropertyValues(Object bean, Object[] values);
 
 	/**
 	 * Sets properties of a given bean to specified values.
 	 *
 	 * @param bean   JavaBean.
 	 * @param values the values assinged to properties.
 	 */
 	public abstract void setPropertyValues(Object bean, Object[] values);
 
 	/**
 	 * Returns the values of properties of a given bean.
 	 *
 	 * @param bean JavaBean.
+	 *
+	 * @return The property values
 	 */
 	public Object[] getPropertyValues(Object bean) {
-		Object[] values = new Object[getters.length];
+		final Object[] values = new Object[getters.length];
 		getPropertyValues( bean, values );
 		return values;
 	}
 
 	/**
 	 * Returns the types of properties.
+	 *
+	 * @return The property types
 	 */
 	public Class[] getPropertyTypes() {
 		return types.clone();
 	}
 
 	/**
 	 * Returns the setter names of properties.
+	 *
+	 * @return The getter names
 	 */
 	public String[] getGetters() {
 		return getters.clone();
 	}
 
 	/**
 	 * Returns the getter names of the properties.
+	 *
+	 * @return The setter names
 	 */
 	public String[] getSetters() {
 		return setters.clone();
 	}
 
-	/**
-	 * Creates a new instance of <code>BulkAccessor</code>.
-	 * The created instance provides methods for setting/getting
-	 * specified properties at once.
-	 *
-	 * @param beanClass the class of the JavaBeans accessed
-	 *                  through the created object.
-	 * @param getters   the names of setter methods for specified properties.
-	 * @param setters   the names of getter methods for specified properties.
-	 * @param types     the types of specified properties.
-	 */
-	public static BulkAccessor create(
-			Class beanClass,
-	        String[] getters,
-	        String[] setters,
-	        Class[] types) {
-		BulkAccessorFactory factory = new BulkAccessorFactory( beanClass, getters, setters, types );
-		return factory.create();
+	private BulkAccessor() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
index 0b2a23f29e..2ec4559a98 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
@@ -1,101 +1,86 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
+import org.hibernate.HibernateException;
+
 /**
  * An exception thrown while generating a bulk accessor.
- * 
+ *
  * @author Muga Nishizawa
  * @author modified by Shigeru Chiba
  */
-public class BulkAccessorException extends RuntimeException {
-    private Throwable myCause;
-
-    /**
-     * Gets the cause of this throwable.
-     * It is for JDK 1.3 compatibility.
-     */
-    public Throwable getCause() {
-        return (myCause == this ? null : myCause);
-    }
-
-    /**
-     * Initializes the cause of this throwable.
-     * It is for JDK 1.3 compatibility.
-     */
-    public synchronized Throwable initCause(Throwable cause) {
-        myCause = cause;
-        return this;
-    }
-
-    private int index;
+public class BulkAccessorException extends HibernateException {
+	private final int index;
 
-    /**
-     * Constructs an exception.
-     */
-    public BulkAccessorException(String message) {
-        super(message);
-        index = -1;
-        initCause(null);
-    }
+	/**
+	 * Constructs an exception.
+	 *
+	 * @param message Message explaining the exception condition
+	 */
+	public BulkAccessorException(String message) {
+		this( message, -1 );
+	}
 
-    /**
-     * Constructs an exception.
-     *
-     * @param index     the index of the property that causes an exception.
-     */
-    public BulkAccessorException(String message, int index) {
-        this(message + ": " + index);
-        this.index = index;
-    }
+	/**
+	 * Constructs an exception.
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param index The index of the property that causes an exception.
+	 */
+	public BulkAccessorException(String message, int index) {
+		this( message, index, null );
+	}
 
-    /**
-     * Constructs an exception.
-     */
-    public BulkAccessorException(String message, Throwable cause) {
-        super(message);
-        index = -1;
-        initCause(cause);
-    }
+	/**
+	 * Constructs an exception.
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param cause The underlying cause
+	 */
+	public BulkAccessorException(String message, Exception cause) {
+		this( message, -1, cause );
+	}
 
-    /**
-     * Constructs an exception.
-     *
-     * @param index     the index of the property that causes an exception.
-     */
-    public BulkAccessorException(Throwable cause, int index) {
-        this("Property " + index);
-        this.index = index;
-        initCause(cause);
-    }
+	/**
+	 * Constructs an exception.
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param index The index of the property that causes an exception.
+	 * @param cause The underlying cause
+	 */
+	public BulkAccessorException(String message, int index, Exception cause) {
+		super( message + " : @" + index, cause );
+		this.index = index;
+	}
 
-    /**
-     * Returns the index of the property that causes this exception.
-     *
-     * @return -1 if the index is not specified.
-     */
-    public int getIndex() {
-        return this.index;
-    }
+	/**
+	 * Returns the index of the property that causes this exception.
+	 *
+	 * @return -1 if the index is not specified.
+	 */
+	public int getIndex() {
+		return this.index;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
index 40be75c89a..b8e2d5b4ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
@@ -1,426 +1,440 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.security.ProtectionDomain;
 
 import javassist.CannotCompileException;
 import javassist.bytecode.AccessFlag;
 import javassist.bytecode.Bytecode;
 import javassist.bytecode.ClassFile;
 import javassist.bytecode.CodeAttribute;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
 import javassist.bytecode.StackMapTable;
 import javassist.util.proxy.FactoryHelper;
 import javassist.util.proxy.RuntimeSupport;
 
 /**
  * A factory of bulk accessors.
  *
  * @author Muga Nishizawa
  * @author modified by Shigeru Chiba
  */
 class BulkAccessorFactory {
 	private static final String PACKAGE_NAME_PREFIX = "org.javassist.tmp.";
 	private static final String BULKACESSOR_CLASS_NAME = BulkAccessor.class.getName();
 	private static final String OBJECT_CLASS_NAME = Object.class.getName();
 	private static final String GENERATED_GETTER_NAME = "getPropertyValues";
 	private static final String GENERATED_SETTER_NAME = "setPropertyValues";
 	private static final String GET_SETTER_DESC = "(Ljava/lang/Object;[Ljava/lang/Object;)V";
 	private static final String THROWABLE_CLASS_NAME = Throwable.class.getName();
 	private static final String BULKEXCEPTION_CLASS_NAME = BulkAccessorException.class.getName();
-	private static int counter = 0;
+
+	private static int counter;
 
 	private Class targetBean;
 	private String[] getterNames;
 	private String[] setterNames;
 	private Class[] types;
 	public String writeDirectory;
 
 	BulkAccessorFactory(
 			Class target,
-	        String[] getterNames,
-	        String[] setterNames,
-	        Class[] types) {
+			String[] getterNames,
+			String[] setterNames,
+			Class[] types) {
 		this.targetBean = target;
 		this.getterNames = getterNames;
 		this.setterNames = setterNames;
 		this.types = types;
 		this.writeDirectory = null;
 	}
 
 	BulkAccessor create() {
-		Method[] getters = new Method[getterNames.length];
-		Method[] setters = new Method[setterNames.length];
+		final Method[] getters = new Method[getterNames.length];
+		final Method[] setters = new Method[setterNames.length];
 		findAccessors( targetBean, getterNames, setterNames, types, getters, setters );
 
-		Class beanClass;
+		final Class beanClass;
 		try {
-			ClassFile classfile = make( getters, setters );
-			ClassLoader loader = this.getClassLoader();
+			final ClassFile classfile = make( getters, setters );
+			final ClassLoader loader = this.getClassLoader();
 			if ( writeDirectory != null ) {
 				FactoryHelper.writeFile( classfile, writeDirectory );
 			}
 
 			beanClass = FactoryHelper.toClass( classfile, loader, getDomain() );
-			return ( BulkAccessor ) this.newInstance( beanClass );
+			return (BulkAccessor) this.newInstance( beanClass );
 		}
 		catch ( Exception e ) {
 			throw new BulkAccessorException( e.getMessage(), e );
 		}
 	}
 
 	private ProtectionDomain getDomain() {
-		Class cl;
+		final Class cl;
 		if ( this.targetBean != null ) {
 			cl = this.targetBean;
 		}
 		else {
 			cl = this.getClass();
 		}
 		return cl.getProtectionDomain();
 	}
 
 	private ClassFile make(Method[] getters, Method[] setters) throws CannotCompileException {
 		String className = targetBean.getName();
 		// set the name of bulk accessor.
 		className = className + "_$$_bulkaccess_" + counter++;
 		if ( className.startsWith( "java." ) ) {
-			className = "org.javassist.tmp." + className;
+			className = PACKAGE_NAME_PREFIX + className;
 		}
 
-		ClassFile classfile = new ClassFile( false, className, BULKACESSOR_CLASS_NAME );
+		final ClassFile classfile = new ClassFile( false, className, BULKACESSOR_CLASS_NAME );
 		classfile.setAccessFlags( AccessFlag.PUBLIC );
 		addDefaultConstructor( classfile );
 		addGetter( classfile, getters );
 		addSetter( classfile, setters );
 		return classfile;
 	}
 
 	private ClassLoader getClassLoader() {
 		if ( targetBean != null && targetBean.getName().equals( OBJECT_CLASS_NAME ) ) {
 			return targetBean.getClassLoader();
 		}
 		else {
 			return getClass().getClassLoader();
 		}
 	}
 
 	private Object newInstance(Class type) throws Exception {
-		BulkAccessor instance = ( BulkAccessor ) type.newInstance();
+		final BulkAccessor instance = (BulkAccessor) type.newInstance();
 		instance.target = targetBean;
-		int len = getterNames.length;
+		final int len = getterNames.length;
 		instance.getters = new String[len];
 		instance.setters = new String[len];
 		instance.types = new Class[len];
 		for ( int i = 0; i < len; i++ ) {
 			instance.getters[i] = getterNames[i];
 			instance.setters[i] = setterNames[i];
 			instance.types[i] = types[i];
 		}
 
 		return instance;
 	}
 
 	/**
 	 * Declares a constructor that takes no parameter.
 	 *
-	 * @param classfile
-	 * @throws CannotCompileException
+	 * @param classfile The class descriptor
+	 *
+	 * @throws CannotCompileException Indicates trouble with the underlying Javassist calls
 	 */
 	private void addDefaultConstructor(ClassFile classfile) throws CannotCompileException {
-		ConstPool cp = classfile.getConstPool();
-		String cons_desc = "()V";
-		MethodInfo mi = new MethodInfo( cp, MethodInfo.nameInit, cons_desc );
+		final ConstPool constPool = classfile.getConstPool();
+		final String constructorSignature = "()V";
+		final MethodInfo constructorMethodInfo = new MethodInfo( constPool, MethodInfo.nameInit, constructorSignature );
 
-		Bytecode code = new Bytecode( cp, 0, 1 );
+		final Bytecode code = new Bytecode( constPool, 0, 1 );
 		// aload_0
 		code.addAload( 0 );
 		// invokespecial
-		code.addInvokespecial( BulkAccessor.class.getName(), MethodInfo.nameInit, cons_desc );
+		code.addInvokespecial( BulkAccessor.class.getName(), MethodInfo.nameInit, constructorSignature );
 		// return
 		code.addOpcode( Opcode.RETURN );
 
-		mi.setCodeAttribute( code.toCodeAttribute() );
-		mi.setAccessFlags( AccessFlag.PUBLIC );
-		classfile.addMethod( mi );
+		constructorMethodInfo.setCodeAttribute( code.toCodeAttribute() );
+		constructorMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		classfile.addMethod( constructorMethodInfo );
 	}
 
 	private void addGetter(ClassFile classfile, final Method[] getters) throws CannotCompileException {
-		ConstPool cp = classfile.getConstPool();
-		int target_type_index = cp.addClassInfo( this.targetBean.getName() );
-		String desc = GET_SETTER_DESC;
-		MethodInfo mi = new MethodInfo( cp, GENERATED_GETTER_NAME, desc );
+		final ConstPool constPool = classfile.getConstPool();
+		final int targetBeanConstPoolIndex = constPool.addClassInfo( this.targetBean.getName() );
+		final String desc = GET_SETTER_DESC;
+		final MethodInfo getterMethodInfo = new MethodInfo( constPool, GENERATED_GETTER_NAME, desc );
 
-		Bytecode code = new Bytecode( cp, 6, 4 );
+		final Bytecode code = new Bytecode( constPool, 6, 4 );
 		/* | this | bean | args | raw bean | */
 		if ( getters.length >= 0 ) {
 			// aload_1 // load bean
 			code.addAload( 1 );
 			// checkcast // cast bean
 			code.addCheckcast( this.targetBean.getName() );
 			// astore_3 // store bean
 			code.addAstore( 3 );
 			for ( int i = 0; i < getters.length; ++i ) {
 				if ( getters[i] != null ) {
-					Method getter = getters[i];
+					final Method getter = getters[i];
 					// aload_2 // args
 					code.addAload( 2 );
 					// iconst_i // continue to aastore
-					code.addIconst( i ); // growing stack is 1
-					Class returnType = getter.getReturnType();
+					// growing stack is 1
+					code.addIconst( i );
+					final Class returnType = getter.getReturnType();
 					int typeIndex = -1;
 					if ( returnType.isPrimitive() ) {
 						typeIndex = FactoryHelper.typeIndex( returnType );
 						// new
 						code.addNew( FactoryHelper.wrapperTypes[typeIndex] );
 						// dup
 						code.addOpcode( Opcode.DUP );
 					}
 
 					// aload_3 // load the raw bean
 					code.addAload( 3 );
-					String getter_desc = RuntimeSupport.makeDescriptor( getter );
-					String getterName = getter.getName();
+					final String getterSignature = RuntimeSupport.makeDescriptor( getter );
+					final String getterName = getter.getName();
 					if ( this.targetBean.isInterface() ) {
 						// invokeinterface
-						code.addInvokeinterface( target_type_index, getterName, getter_desc, 1 );
+						code.addInvokeinterface( targetBeanConstPoolIndex, getterName, getterSignature, 1 );
 					}
 					else {
 						// invokevirtual
-						code.addInvokevirtual( target_type_index, getterName, getter_desc );
+						code.addInvokevirtual( targetBeanConstPoolIndex, getterName, getterSignature );
 					}
 
-					if ( typeIndex >= 0 ) {       // is a primitive type
+					if ( typeIndex >= 0 ) {
+						// is a primitive type
 						// invokespecial
 						code.addInvokespecial(
 								FactoryHelper.wrapperTypes[typeIndex],
-						        MethodInfo.nameInit,
-						        FactoryHelper.wrapperDesc[typeIndex]
+								MethodInfo.nameInit,
+								FactoryHelper.wrapperDesc[typeIndex]
 						);
 					}
 
 					// aastore // args
 					code.add( Opcode.AASTORE );
 					code.growStack( -3 );
 				}
 			}
 		}
 		// return
 		code.addOpcode( Opcode.RETURN );
 
-		mi.setCodeAttribute( code.toCodeAttribute() );
-		mi.setAccessFlags( AccessFlag.PUBLIC );
-		classfile.addMethod( mi );
+		getterMethodInfo.setCodeAttribute( code.toCodeAttribute() );
+		getterMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		classfile.addMethod( getterMethodInfo );
 	}
 
 	private void addSetter(ClassFile classfile, final Method[] setters) throws CannotCompileException {
-		ConstPool cp = classfile.getConstPool();
-		int target_type_index = cp.addClassInfo( this.targetBean.getName() );
-		String desc = GET_SETTER_DESC;
-		MethodInfo mi = new MethodInfo( cp, GENERATED_SETTER_NAME, desc );
+		final ConstPool constPool = classfile.getConstPool();
+		final int targetTypeConstPoolIndex = constPool.addClassInfo( this.targetBean.getName() );
+		final String desc = GET_SETTER_DESC;
+		final MethodInfo setterMethodInfo = new MethodInfo( constPool, GENERATED_SETTER_NAME, desc );
 
-		Bytecode code = new Bytecode( cp, 4, 6 );
+		final Bytecode code = new Bytecode( constPool, 4, 6 );
 		StackMapTable stackmap = null;
 		/* | this | bean | args | i | raw bean | exception | */
 		if ( setters.length > 0 ) {
-			int start, end; // required to exception table
+			// required to exception table
+			int start;
+			int end;
 			// iconst_0 // i
 			code.addIconst( 0 );
 			// istore_3 // store i
 			code.addIstore( 3 );
 			// aload_1 // load the bean
 			code.addAload( 1 );
 			// checkcast // cast the bean into a raw bean
 			code.addCheckcast( this.targetBean.getName() );
 			// astore 4 // store the raw bean
 			code.addAstore( 4 );
 			/* current stack len = 0 */
 			// start region to handling exception (BulkAccessorException)
 			start = code.currentPc();
 			int lastIndex = 0;
 			for ( int i = 0; i < setters.length; ++i ) {
 				if ( setters[i] != null ) {
-					int diff = i - lastIndex;
+					final int diff = i - lastIndex;
 					if ( diff > 0 ) {
 						// iinc 3, 1
 						code.addOpcode( Opcode.IINC );
 						code.add( 3 );
 						code.add( diff );
 						lastIndex = i;
 					}
 				}
 				/* current stack len = 0 */
 				// aload 4 // load the raw bean
 				code.addAload( 4 );
 				// aload_2 // load the args
 				code.addAload( 2 );
 				// iconst_i
 				code.addIconst( i );
 				// aaload
 				code.addOpcode( Opcode.AALOAD );
 				// checkcast
-				Class[] setterParamTypes = setters[i].getParameterTypes();
-				Class setterParamType = setterParamTypes[0];
+				final Class[] setterParamTypes = setters[i].getParameterTypes();
+				final Class setterParamType = setterParamTypes[0];
 				if ( setterParamType.isPrimitive() ) {
 					// checkcast (case of primitive type)
 					// invokevirtual (case of primitive type)
-					this.addUnwrapper( classfile, code, setterParamType );
+					this.addUnwrapper( code, setterParamType );
 				}
 				else {
 					// checkcast (case of reference type)
 					code.addCheckcast( setterParamType.getName() );
 				}
 				/* current stack len = 2 */
-				String rawSetterMethod_desc = RuntimeSupport.makeDescriptor( setters[i] );
+				final String rawSetterMethodDesc = RuntimeSupport.makeDescriptor( setters[i] );
 				if ( !this.targetBean.isInterface() ) {
 					// invokevirtual
-					code.addInvokevirtual( target_type_index, setters[i].getName(), rawSetterMethod_desc );
+					code.addInvokevirtual( targetTypeConstPoolIndex, setters[i].getName(), rawSetterMethodDesc );
 				}
 				else {
 					// invokeinterface
-					Class[] params = setters[i].getParameterTypes();
+					final Class[] params = setters[i].getParameterTypes();
 					int size;
 					if ( params[0].equals( Double.TYPE ) || params[0].equals( Long.TYPE ) ) {
 						size = 3;
 					}
 					else {
 						size = 2;
 					}
 
-					code.addInvokeinterface( target_type_index, setters[i].getName(), rawSetterMethod_desc, size );
+					code.addInvokeinterface( targetTypeConstPoolIndex, setters[i].getName(), rawSetterMethodDesc, size );
 				}
 			}
 
 			// end region to handling exception (BulkAccessorException)
 			end = code.currentPc();
 			// return
 			code.addOpcode( Opcode.RETURN );
 			/* current stack len = 0 */
 			// register in exception table
-			int throwableType_index = cp.addClassInfo( THROWABLE_CLASS_NAME );
-			int handler_pc = code.currentPc();
-			code.addExceptionHandler( start, end, handler_pc, throwableType_index );
+			final int throwableTypeIndex = constPool.addClassInfo( THROWABLE_CLASS_NAME );
+			final int handlerPc = code.currentPc();
+			code.addExceptionHandler( start, end, handlerPc, throwableTypeIndex );
 			// astore 5 // store exception
 			code.addAstore( 5 );
 			// new // BulkAccessorException
 			code.addNew( BULKEXCEPTION_CLASS_NAME );
 			// dup
 			code.addOpcode( Opcode.DUP );
 			// aload 5 // load exception
 			code.addAload( 5 );
 			// iload_3 // i
 			code.addIload( 3 );
 			// invokespecial // BulkAccessorException.<init>
-			String cons_desc = "(Ljava/lang/Throwable;I)V";
-			code.addInvokespecial( BULKEXCEPTION_CLASS_NAME, MethodInfo.nameInit, cons_desc );
+			final String consDesc = "(Ljava/lang/Throwable;I)V";
+			code.addInvokespecial( BULKEXCEPTION_CLASS_NAME, MethodInfo.nameInit, consDesc );
 			// athrow
 			code.addOpcode( Opcode.ATHROW );
-			StackMapTable.Writer writer = new StackMapTable.Writer(32);
-			int[] localTags = { StackMapTable.OBJECT, StackMapTable.OBJECT, StackMapTable.OBJECT, StackMapTable.INTEGER };
-			int[] localData = { cp.getThisClassInfo(), cp.addClassInfo("java/lang/Object"),
-                        	cp.addClassInfo("[Ljava/lang/Object;"), 0};
-			int[] stackTags = { StackMapTable.OBJECT };
-			int[] stackData = { throwableType_index };
-			writer.fullFrame(handler_pc, localTags, localData, stackTags, stackData);
-			stackmap = writer.toStackMapTable(cp);
+			final StackMapTable.Writer writer = new StackMapTable.Writer(32);
+			final int[] localTags = {
+					StackMapTable.OBJECT,
+					StackMapTable.OBJECT,
+					StackMapTable.OBJECT,
+					StackMapTable.INTEGER
+			};
+			final int[] localData = {
+					constPool.getThisClassInfo(),
+					constPool.addClassInfo( "java/lang/Object" ),
+					constPool.addClassInfo( "[Ljava/lang/Object;" ),
+					0
+			};
+			final int[] stackTags = {
+					StackMapTable.OBJECT
+			};
+			final int[] stackData = {
+					throwableTypeIndex
+			};
+			writer.fullFrame( handlerPc, localTags, localData, stackTags, stackData );
+			stackmap = writer.toStackMapTable( constPool );
 		}
 		else {
 			// return
 			code.addOpcode( Opcode.RETURN );
 		}
-		CodeAttribute ca = code.toCodeAttribute();
-		if (stackmap != null) {
-			ca.setAttribute(stackmap);
+		final CodeAttribute ca = code.toCodeAttribute();
+		if ( stackmap != null ) {
+			ca.setAttribute( stackmap );
 		}
-		mi.setCodeAttribute( ca );
-		mi.setAccessFlags( AccessFlag.PUBLIC );
-		classfile.addMethod( mi );
+		setterMethodInfo.setCodeAttribute( ca );
+		setterMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		classfile.addMethod( setterMethodInfo );
 	}
 
-	private void addUnwrapper(
-			ClassFile classfile,
-	        Bytecode code,
-	        Class type) {
-		int index = FactoryHelper.typeIndex( type );
-		String wrapperType = FactoryHelper.wrapperTypes[index];
+	private void addUnwrapper(Bytecode code, Class type) {
+		final int index = FactoryHelper.typeIndex( type );
+		final String wrapperType = FactoryHelper.wrapperTypes[index];
 		// checkcast
 		code.addCheckcast( wrapperType );
 		// invokevirtual
 		code.addInvokevirtual( wrapperType, FactoryHelper.unwarpMethods[index], FactoryHelper.unwrapDesc[index] );
 	}
 
 	private static void findAccessors(
 			Class clazz,
-	        String[] getterNames,
-	        String[] setterNames,
-	        Class[] types,
-	        Method[] getters,
-	        Method[] setters) {
-		int length = types.length;
+			String[] getterNames,
+			String[] setterNames,
+			Class[] types,
+			Method[] getters,
+			Method[] setters) {
+		final int length = types.length;
 		if ( setterNames.length != length || getterNames.length != length ) {
 			throw new BulkAccessorException( "bad number of accessors" );
 		}
 
-		Class[] getParam = new Class[0];
-		Class[] setParam = new Class[1];
+		final Class[] getParam = new Class[0];
+		final Class[] setParam = new Class[1];
 		for ( int i = 0; i < length; i++ ) {
 			if ( getterNames[i] != null ) {
-				Method getter = findAccessor( clazz, getterNames[i], getParam, i );
+				final Method getter = findAccessor( clazz, getterNames[i], getParam, i );
 				if ( getter.getReturnType() != types[i] ) {
 					throw new BulkAccessorException( "wrong return type: " + getterNames[i], i );
 				}
 
 				getters[i] = getter;
 			}
 
 			if ( setterNames[i] != null ) {
 				setParam[0] = types[i];
 				setters[i] = findAccessor( clazz, setterNames[i], setParam, i );
 			}
 		}
 	}
 
-	private static Method findAccessor(
-			Class clazz,
-	        String name,
-	        Class[] params,
-	        int index) throws BulkAccessorException {
+	@SuppressWarnings("unchecked")
+	private static Method findAccessor(Class clazz, String name, Class[] params, int index)
+			throws BulkAccessorException {
 		try {
-			Method method = clazz.getDeclaredMethod( name, params );
+			final Method method = clazz.getDeclaredMethod( name, params );
 			if ( Modifier.isPrivate( method.getModifiers() ) ) {
 				throw new BulkAccessorException( "private property", index );
 			}
 
 			return method;
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new BulkAccessorException( "cannot find an accessor", index );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
index 4fac7c1677..a529a5ef7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
@@ -1,175 +1,188 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Modifier;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.bytecode.buildtime.spi.ClassFilter;
 import org.hibernate.bytecode.buildtime.spi.FieldFilter;
 import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.bytecode.spi.ClassTransformer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Bytecode provider implementation for Javassist.
  *
  * @author Steve Ebersole
  */
 public class BytecodeProviderImpl implements BytecodeProvider {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BytecodeProviderImpl.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			BytecodeProviderImpl.class.getName()
+	);
 
 	@Override
 	public ProxyFactoryFactory getProxyFactoryFactory() {
 		return new ProxyFactoryFactoryImpl();
 	}
 
 	@Override
 	public ReflectionOptimizer getReflectionOptimizer(
 			Class clazz,
-	        String[] getterNames,
-	        String[] setterNames,
-	        Class[] types) {
+			String[] getterNames,
+			String[] setterNames,
+			Class[] types) {
 		FastClass fastClass;
 		BulkAccessor bulkAccessor;
 		try {
 			fastClass = FastClass.create( clazz );
 			bulkAccessor = BulkAccessor.create( clazz, getterNames, setterNames, types );
 			if ( !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
 				if ( fastClass == null ) {
 					bulkAccessor = null;
 				}
 				else {
 					//test out the optimizer:
-					Object instance = fastClass.newInstance();
+					final Object instance = fastClass.newInstance();
 					bulkAccessor.setPropertyValues( instance, bulkAccessor.getPropertyValues( instance ) );
 				}
 			}
 		}
 		catch ( Throwable t ) {
 			fastClass = null;
 			bulkAccessor = null;
-            if (LOG.isDebugEnabled()) {
-                int index = 0;
-                if (t instanceof BulkAccessorException) index = ((BulkAccessorException)t).getIndex();
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
+			if ( LOG.isDebugEnabled() ) {
+				int index = 0;
+				if (t instanceof BulkAccessorException) {
+					index = ( (BulkAccessorException) t ).getIndex();
+				}
+				if ( index >= 0 ) {
+					LOG.debugf(
+							"Reflection optimizer disabled for %s [%s: %s (property %s)]",
+							clazz.getName(),
+							StringHelper.unqualify( t.getClass().getName() ),
+							t.getMessage(),
+							setterNames[index]
+					);
+				}
+				else {
+					LOG.debugf(
+							"Reflection optimizer disabled for %s [%s: %s]",
+							clazz.getName(),
+							StringHelper.unqualify( t.getClass().getName() ),
+							t.getMessage()
+					);
+				}
+			}
 		}
 
 		if ( fastClass != null && bulkAccessor != null ) {
 			return new ReflectionOptimizerImpl(
 					new InstantiationOptimizerAdapter( fastClass ),
-			        new AccessOptimizerAdapter( bulkAccessor, clazz )
+					new AccessOptimizerAdapter( bulkAccessor, clazz )
 			);
 		}
-        return null;
+
+		return null;
 	}
 
 	@Override
 	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
 		return new JavassistClassTransformer( classFilter, fieldFilter );
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass) {
 		return new EntityInstrumentationMetadataImpl( entityClass );
 	}
 
 	private class EntityInstrumentationMetadataImpl implements EntityInstrumentationMetadata {
 		private final Class entityClass;
 		private final boolean isInstrumented;
 
 		private EntityInstrumentationMetadataImpl(Class entityClass) {
 			this.entityClass = entityClass;
 			this.isInstrumented = FieldHandled.class.isAssignableFrom( entityClass );
 		}
 
 		@Override
 		public String getEntityName() {
 			return entityClass.getName();
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return isInstrumented;
 		}
 
 		@Override
 		public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
 			if ( !entityClass.isInstance( entity ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Passed entity instance [%s] is not of expected type [%s]",
 								entity,
 								getEntityName()
 						)
 				);
 			}
 			if ( ! isInstrumented() ) {
 				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
 			}
 			return JavassistHelper.extractFieldInterceptor( entity );
 		}
 
 		@Override
 		public FieldInterceptor injectInterceptor(
 				Object entity,
 				String entityName,
 				Set uninitializedFieldNames,
 				SessionImplementor session) throws NotInstrumentedException {
 			if ( !entityClass.isInstance( entity ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Passed entity instance [%s] is not of expected type [%s]",
 								entity,
 								getEntityName()
 						)
 				);
 			}
 			if ( ! isInstrumented() ) {
 				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
 			}
 			return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
index 82069f103a..e629dfeec3 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
@@ -1,193 +1,264 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 /**
+ * Fast access to class information
+ *
  * @author Muga Nishizawa
  */
 public class FastClass implements Serializable {
-
 	private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
 
-	private Class type;
+	private final Class type;
 
-	private FastClass() {
+	/**
+	 * Constructs a FastClass
+	 *
+	 * @param type The class to optimize
+	 *
+	 * @return The fast class access to the given class
+	 */
+	public static FastClass create(Class type) {
+		return new FastClass( type );
 	}
 
 	private FastClass(Class type) {
 		this.type = type;
 	}
 
+	/**
+	 * Access to invoke a method on the class that this fast class handles
+	 *
+	 * @param name The name of the method to invoke,
+	 * @param parameterTypes The method parameter types
+	 * @param obj The instance on which to invoke the method
+	 * @param args The parameter arguments
+	 *
+	 * @return The method result
+	 *
+	 * @throws InvocationTargetException Indicates a problem performing invocation
+	 */
 	public Object invoke(
 			String name,
-	        Class[] parameterTypes,
-	        Object obj,
-	        Object[] args) throws InvocationTargetException {
+			Class[] parameterTypes,
+			Object obj,
+			Object[] args) throws InvocationTargetException {
 		return this.invoke( this.getIndex( name, parameterTypes ), obj, args );
 	}
 
+	/**
+	 * Access to invoke a method on the class that this fast class handles by its index
+	 *
+	 * @param index The method index
+	 * @param obj The instance on which to invoke the method
+	 * @param args The parameter arguments
+	 *
+	 * @return The method result
+	 *
+	 * @throws InvocationTargetException Indicates a problem performing invocation
+	 */
 	public Object invoke(
 			int index,
-	        Object obj,
-	        Object[] args) throws InvocationTargetException {
-		Method[] methods = this.type.getMethods();
+			Object obj,
+			Object[] args) throws InvocationTargetException {
+		final Method[] methods = this.type.getMethods();
 		try {
 			return methods[index].invoke( obj, args );
 		}
 		catch ( ArrayIndexOutOfBoundsException e ) {
 			throw new IllegalArgumentException(
 					"Cannot find matching method/constructor"
 			);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new InvocationTargetException( e );
 		}
 	}
 
+	/**
+	 * Invoke the default constructor
+	 *
+	 * @return The constructed instance
+	 *
+	 * @throws InvocationTargetException Indicates a problem performing invocation
+	 */
 	public Object newInstance() throws InvocationTargetException {
 		return this.newInstance( this.getIndex( EMPTY_CLASS_ARRAY ), null );
 	}
 
+	/**
+	 * Invoke a parameterized constructor
+	 *
+	 * @param parameterTypes The parameter types
+	 * @param args The parameter arguments to pass along
+	 *
+	 * @return The constructed instance
+	 *
+	 * @throws InvocationTargetException Indicates a problem performing invocation
+	 */
 	public Object newInstance(
 			Class[] parameterTypes,
-	        Object[] args) throws InvocationTargetException {
+			Object[] args) throws InvocationTargetException {
 		return this.newInstance( this.getIndex( parameterTypes ), args );
 	}
 
+	/**
+	 * Invoke a constructor by its index
+	 *
+	 * @param index The constructor index
+	 * @param args The parameter arguments to pass along
+	 *
+	 * @return The constructed instance
+	 *
+	 * @throws InvocationTargetException Indicates a problem performing invocation
+	 */
 	public Object newInstance(
 			int index,
-	        Object[] args) throws InvocationTargetException {
-		Constructor[] conss = this.type.getConstructors();
+			Object[] args) throws InvocationTargetException {
+		final Constructor[] constructors = this.type.getConstructors();
 		try {
-			return conss[index].newInstance( args );
+			return constructors[index].newInstance( args );
 		}
 		catch ( ArrayIndexOutOfBoundsException e ) {
 			throw new IllegalArgumentException( "Cannot find matching method/constructor" );
 		}
 		catch ( InstantiationException e ) {
 			throw new InvocationTargetException( e );
 		}
 		catch ( IllegalAccessException e ) {
 			throw new InvocationTargetException( e );
 		}
 	}
 
+	/**
+	 * Locate the index of a method
+	 *
+	 * @param name The method name
+	 * @param parameterTypes The method parameter types
+	 *
+	 * @return The index
+	 */
 	public int getIndex(String name, Class[] parameterTypes) {
-		Method[] methods = this.type.getMethods();
-		boolean eq = true;
+		final Method[] methods = this.type.getMethods();
+		boolean eq;
+
 		for ( int i = 0; i < methods.length; ++i ) {
 			if ( !Modifier.isPublic( methods[i].getModifiers() ) ) {
 				continue;
 			}
 			if ( !methods[i].getName().equals( name ) ) {
 				continue;
 			}
-			Class[] params = methods[i].getParameterTypes();
+			final Class[] params = methods[i].getParameterTypes();
 			if ( params.length != parameterTypes.length ) {
 				continue;
 			}
 			eq = true;
 			for ( int j = 0; j < params.length; ++j ) {
 				if ( !params[j].equals( parameterTypes[j] ) ) {
 					eq = false;
 					break;
 				}
 			}
 			if ( eq ) {
 				return i;
 			}
 		}
 		return -1;
 	}
 
+	/**
+	 * Locate the index of a constructor
+	 *
+	 * @param parameterTypes The constructor parameter types
+	 *
+	 * @return The index
+	 */
 	public int getIndex(Class[] parameterTypes) {
-		Constructor[] conss = this.type.getConstructors();
-		boolean eq = true;
-		for ( int i = 0; i < conss.length; ++i ) {
-			if ( !Modifier.isPublic( conss[i].getModifiers() ) ) {
+		final Constructor[] constructors = this.type.getConstructors();
+		boolean eq;
+
+		for ( int i = 0; i < constructors.length; ++i ) {
+			if ( !Modifier.isPublic( constructors[i].getModifiers() ) ) {
 				continue;
 			}
-			Class[] params = conss[i].getParameterTypes();
+			final Class[] params = constructors[i].getParameterTypes();
 			if ( params.length != parameterTypes.length ) {
 				continue;
 			}
 			eq = true;
 			for ( int j = 0; j < params.length; ++j ) {
 				if ( !params[j].equals( parameterTypes[j] ) ) {
 					eq = false;
 					break;
 				}
 			}
 			if ( eq ) {
 				return i;
 			}
 		}
 		return -1;
 	}
 
-	public int getMaxIndex() {
-		Method[] methods = this.type.getMethods();
-		int count = 0;
-		for ( int i = 0; i < methods.length; ++i ) {
-			if ( Modifier.isPublic( methods[i].getModifiers() ) ) {
-				count++;
-			}
-		}
-		return count;
-	}
-
+	/**
+	 * Get the wrapped class name
+	 *
+	 * @return The class name
+	 */
 	public String getName() {
 		return this.type.getName();
 	}
 
+	/**
+	 * Get the wrapped java class reference
+	 *
+	 * @return The class reference
+	 */
 	public Class getJavaClass() {
 		return this.type;
 	}
 
+	@Override
 	public String toString() {
 		return this.type.toString();
 	}
 
+	@Override
 	public int hashCode() {
 		return this.type.hashCode();
 	}
 
+	@Override
 	public boolean equals(Object o) {
-		if (! ( o instanceof FastClass ) ) {
-			return false;
-		}
-		return this.type.equals( ( ( FastClass ) o ).type );
-	}
-
-	public static FastClass create(Class type) {
-		FastClass fc = new FastClass( type );
-		return fc;
+		return o instanceof FastClass
+				&& this.type.equals( ((FastClass) o).type );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
index 6d7d975f8a..c1241c4e85 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
@@ -1,57 +1,76 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
-
 /**
  * Contract for deciding whether fields should be read and/or write intercepted.
  *
  * @author Muga Nishizawa
  * @author Steve Ebersole
  */
 public interface FieldFilter {
 	/**
 	 * Should the given field be read intercepted?
 	 *
-	 * @param desc
-	 * @param name
+	 * @param desc The field descriptor
+	 * @param name The field name
+	 *
 	 * @return true if the given field should be read intercepted; otherwise
 	 * false.
 	 */
 	boolean handleRead(String desc, String name);
 
 	/**
 	 * Should the given field be write intercepted?
 	 *
-	 * @param desc
-	 * @param name
+	 * @param desc The field descriptor
+	 * @param name The field name
+	 *
 	 * @return true if the given field should be write intercepted; otherwise
 	 * false.
 	 */
 	boolean handleWrite(String desc, String name);
 
+	/**
+	 * Should read access to the given field be intercepted?
+	 *
+	 * @param fieldOwnerClassName The class where the field being accessed is defined
+	 * @param fieldName The name of the field being accessed
+	 *
+	 * @return true if the given field read access should be write intercepted; otherwise
+	 * false.
+	 */
 	boolean handleReadAccess(String fieldOwnerClassName, String fieldName);
 
+	/**
+	 * Should write access to the given field be intercepted?
+	 *
+	 * @param fieldOwnerClassName The class where the field being accessed is defined
+	 * @param fieldName The name of the field being accessed
+	 *
+	 * @return true if the given field write access should be write intercepted; otherwise
+	 * false.
+	 */
 	boolean handleWriteAccess(String fieldOwnerClassName, String fieldName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
index 135f6433ec..98225bd1a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
@@ -1,79 +1,241 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 /**
  * The interface defining how interception of a field should be handled.
  *
  * @author Muga Nishizawa
  */
+@SuppressWarnings("UnusedDeclaration")
 public interface FieldHandler {
 
 	/**
 	 * Called to handle writing an int value to a given field.
 	 *
-	 * @param obj ?
+	 * @param obj The object instance on which the write was invoked
 	 * @param name The name of the field being written
 	 * @param oldValue The old field value
 	 * @param newValue The new field value.
-	 * @return ?
+	 *
+	 * @return The new value, typically the same as the newValue argument
 	 */
 	int writeInt(Object obj, String name, int oldValue, int newValue);
 
+	/**
+	 * Called to handle writing a char value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	char writeChar(Object obj, String name, char oldValue, char newValue);
 
+	/**
+	 * Called to handle writing a byte value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	byte writeByte(Object obj, String name, byte oldValue, byte newValue);
 
-	boolean writeBoolean(Object obj, String name, boolean oldValue,
-			boolean newValue);
+	/**
+	 * Called to handle writing a boolean value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
+	boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue);
 
+	/**
+	 * Called to handle writing a short value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	short writeShort(Object obj, String name, short oldValue, short newValue);
 
+	/**
+	 * Called to handle writing a float value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	float writeFloat(Object obj, String name, float oldValue, float newValue);
 
+	/**
+	 * Called to handle writing a double value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	double writeDouble(Object obj, String name, double oldValue, double newValue);
 
+	/**
+	 * Called to handle writing a long value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument
+	 */
 	long writeLong(Object obj, String name, long oldValue, long newValue);
 
+	/**
+	 * Called to handle writing an Object value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 * @param newValue The new field value.
+	 *
+	 * @return The new value, typically the same as the newValue argument; may be different for entity references
+	 */
 	Object writeObject(Object obj, String name, Object oldValue, Object newValue);
 
+	/**
+	 * Called to handle reading an int value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	int readInt(Object obj, String name, int oldValue);
 
+	/**
+	 * Called to handle reading a char value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	char readChar(Object obj, String name, char oldValue);
 
+	/**
+	 * Called to handle reading a byte value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	byte readByte(Object obj, String name, byte oldValue);
 
+	/**
+	 * Called to handle reading a boolean value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	boolean readBoolean(Object obj, String name, boolean oldValue);
 
+	/**
+	 * Called to handle reading a short value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	short readShort(Object obj, String name, short oldValue);
 
+	/**
+	 * Called to handle reading a float value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	float readFloat(Object obj, String name, float oldValue);
 
+	/**
+	 * Called to handle reading a double value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	double readDouble(Object obj, String name, double oldValue);
 
+	/**
+	 * Called to handle reading a long value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	long readLong(Object obj, String name, long oldValue);
 
+	/**
+	 * Called to handle reading an Object value to a given field.
+	 *
+	 * @param obj The object instance on which the write was invoked
+	 * @param name The name of the field being written
+	 * @param oldValue The old field value
+	 *
+	 * @return The field value
+	 */
 	Object readObject(Object obj, String name, Object oldValue);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
index eb586fde0c..6e3b43b6e5 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
@@ -1,632 +1,751 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.DataInputStream;
 import java.io.DataOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
-import java.util.Iterator;
 import java.util.List;
 
 import javassist.CannotCompileException;
 import javassist.ClassPool;
 import javassist.bytecode.AccessFlag;
 import javassist.bytecode.BadBytecode;
 import javassist.bytecode.Bytecode;
 import javassist.bytecode.ClassFile;
 import javassist.bytecode.CodeAttribute;
 import javassist.bytecode.CodeIterator;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.Descriptor;
 import javassist.bytecode.FieldInfo;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
 import javassist.bytecode.StackMapTable;
 import javassist.bytecode.stackmap.MapMaker;
 
 /**
  * The thing that handles actual class enhancement in regards to
  * intercepting field accesses.
  *
  * @author Muga Nishizawa
  * @author Steve Ebersole
  * @author Dustin Schultz
  */
 public class FieldTransformer {
 
 	private static final String EACH_READ_METHOD_PREFIX = "$javassist_read_";
 
 	private static final String EACH_WRITE_METHOD_PREFIX = "$javassist_write_";
 
-	private static final String FIELD_HANDLED_TYPE_NAME = FieldHandled.class
-			.getName();
+	private static final String FIELD_HANDLED_TYPE_NAME = FieldHandled.class.getName();
 
 	private static final String HANDLER_FIELD_NAME = "$JAVASSIST_READ_WRITE_HANDLER";
 
-	private static final String FIELD_HANDLER_TYPE_NAME = FieldHandler.class
-			.getName();
+	private static final String FIELD_HANDLER_TYPE_NAME = FieldHandler.class.getName();
 
-	private static final String HANDLER_FIELD_DESCRIPTOR = 'L' + FIELD_HANDLER_TYPE_NAME
-			.replace('.', '/') + ';';
+	private static final String HANDLER_FIELD_DESCRIPTOR = 'L' + FIELD_HANDLER_TYPE_NAME.replace( '.', '/' ) + ';';
 
 	private static final String GETFIELDHANDLER_METHOD_NAME = "getFieldHandler";
 
 	private static final String SETFIELDHANDLER_METHOD_NAME = "setFieldHandler";
 
-	private static final String GETFIELDHANDLER_METHOD_DESCRIPTOR = "()"
-	                                                                + HANDLER_FIELD_DESCRIPTOR;
+	private static final String GETFIELDHANDLER_METHOD_DESCRIPTOR = "()" + HANDLER_FIELD_DESCRIPTOR;
 
-	private static final String SETFIELDHANDLER_METHOD_DESCRIPTOR = "("
-	                                                                + HANDLER_FIELD_DESCRIPTOR + ")V";
+	private static final String SETFIELDHANDLER_METHOD_DESCRIPTOR = "(" + HANDLER_FIELD_DESCRIPTOR + ")V";
 
-	private FieldFilter filter;
-	
-	private ClassPool classPool;
+	private final FieldFilter filter;
+	private final ClassPool classPool;
 
-	public FieldTransformer() {
-		this(null, null);
-	}
-
-	public FieldTransformer(FieldFilter f, ClassPool c) {
+	FieldTransformer(FieldFilter f, ClassPool c) {
 		filter = f;
 		classPool = c;
 	}
-	
-	public void setClassPool(ClassPool c) {
-		classPool = c;
-	}
-
-	public void setFieldFilter(FieldFilter f) {
-		filter = f;
-	}
 
+	/**
+	 * Transform the class contained in the given file, writing the result back to the same file.
+	 *
+	 * @param file The file containing the class to be transformed
+	 *
+	 * @throws Exception Indicates a problem performing the transformation
+	 */
 	public void transform(File file) throws Exception {
-		DataInputStream in = new DataInputStream(new FileInputStream(file));
-		ClassFile classfile = new ClassFile(in);
-		transform(classfile);
-		DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
+		final DataInputStream in = new DataInputStream( new FileInputStream( file ) );
+		final ClassFile classfile = new ClassFile( in );
+		transform( classfile );
+
+		final DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
 		try {
-			classfile.write(out);
-		} finally {
+			classfile.write( out );
+		}
+		finally {
 			out.close();
 		}
 	}
 
-	public void transform(ClassFile classfile) throws Exception {
-		if (classfile.isInterface()) {
+	/**
+	 * Transform the class defined by the given ClassFile descriptor.  The ClassFile descriptor itself is mutated
+	 *
+	 * @param classFile The class file descriptor
+	 *
+	 * @throws Exception Indicates a problem performing the transformation
+	 */
+	public void transform(ClassFile classFile) throws Exception {
+		if ( classFile.isInterface() ) {
 			return;
 		}
 		try {
-			addFieldHandlerField(classfile);
-			addGetFieldHandlerMethod(classfile);
-			addSetFieldHandlerMethod(classfile);
-			addFieldHandledInterface(classfile);
-			addReadWriteMethods(classfile);
-			transformInvokevirtualsIntoPutAndGetfields(classfile);
-		} catch (CannotCompileException e) {
-			throw new RuntimeException(e.getMessage(), e);
+			addFieldHandlerField( classFile );
+			addGetFieldHandlerMethod( classFile );
+			addSetFieldHandlerMethod( classFile );
+			addFieldHandledInterface( classFile );
+			addReadWriteMethods( classFile );
+			transformInvokevirtualsIntoPutAndGetfields( classFile );
+		}
+		catch (CannotCompileException e) {
+			throw new RuntimeException( e.getMessage(), e );
 		}
 	}
 
-	private void addFieldHandlerField(ClassFile classfile)
-			throws CannotCompileException {
-		ConstPool cp = classfile.getConstPool();
-		FieldInfo finfo = new FieldInfo(cp, HANDLER_FIELD_NAME,
-		                                HANDLER_FIELD_DESCRIPTOR);
-		finfo.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.TRANSIENT);
-		classfile.addField(finfo);
+	private void addFieldHandlerField(ClassFile classfile) throws CannotCompileException {
+		final ConstPool constPool = classfile.getConstPool();
+		final FieldInfo fieldInfo = new FieldInfo( constPool, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
+		fieldInfo.setAccessFlags( AccessFlag.PRIVATE | AccessFlag.TRANSIENT );
+		classfile.addField( fieldInfo );
 	}
 
-	private void addGetFieldHandlerMethod(ClassFile classfile)
-			throws CannotCompileException, BadBytecode {
-		ConstPool cp = classfile.getConstPool();
-		int this_class_index = cp.getThisClassInfo();
-		MethodInfo minfo = new MethodInfo(cp, GETFIELDHANDLER_METHOD_NAME,
-		                                  GETFIELDHANDLER_METHOD_DESCRIPTOR);
+	private void addGetFieldHandlerMethod(ClassFile classfile) throws CannotCompileException, BadBytecode {
+		final ConstPool constPool = classfile.getConstPool();
+		final int thisClassInfo = constPool.getThisClassInfo();
+		final MethodInfo getterMethodInfo = new MethodInfo(
+				constPool,
+				GETFIELDHANDLER_METHOD_NAME,
+				GETFIELDHANDLER_METHOD_DESCRIPTOR
+		);
+
 		/* local variable | this | */
-		Bytecode code = new Bytecode(cp, 2, 1);
+		final Bytecode code = new Bytecode( constPool, 2, 1 );
 		// aload_0 // load this
-		code.addAload(0);
+		code.addAload( 0 );
 		// getfield // get field "$JAVASSIST_CALLBACK" defined already
-		code.addOpcode(Opcode.GETFIELD);
-		int field_index = cp.addFieldrefInfo(this_class_index,
-		                                     HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR);
-		code.addIndex(field_index);
+		code.addOpcode( Opcode.GETFIELD );
+		final int fieldIndex = constPool.addFieldrefInfo( thisClassInfo, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
+		code.addIndex( fieldIndex );
 		// areturn // return the value of the field
-		code.addOpcode(Opcode.ARETURN);
-		minfo.setCodeAttribute(code.toCodeAttribute());
-		minfo.setAccessFlags(AccessFlag.PUBLIC);
-		CodeAttribute codeAttribute = minfo.getCodeAttribute();
-		if (codeAttribute != null) {
-			StackMapTable smt = MapMaker.make(classPool, minfo);
-			codeAttribute.setAttribute(smt);
-		}
-		classfile.addMethod(minfo);
+		code.addOpcode( Opcode.ARETURN );
+		getterMethodInfo.setCodeAttribute( code.toCodeAttribute() );
+		getterMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		final CodeAttribute codeAttribute = getterMethodInfo.getCodeAttribute();
+		if ( codeAttribute != null ) {
+			final StackMapTable smt = MapMaker.make( classPool, getterMethodInfo );
+			codeAttribute.setAttribute( smt );
+		}
+		classfile.addMethod( getterMethodInfo );
 	}
 
-	private void addSetFieldHandlerMethod(ClassFile classfile)
-			throws CannotCompileException, BadBytecode {
-		ConstPool cp = classfile.getConstPool();
-		int this_class_index = cp.getThisClassInfo();
-		MethodInfo minfo = new MethodInfo(cp, SETFIELDHANDLER_METHOD_NAME,
-		                                  SETFIELDHANDLER_METHOD_DESCRIPTOR);
+	private void addSetFieldHandlerMethod(ClassFile classfile) throws CannotCompileException, BadBytecode {
+		final ConstPool constPool = classfile.getConstPool();
+		final int thisClassInfo = constPool.getThisClassInfo();
+		final MethodInfo methodInfo = new MethodInfo(
+				constPool,
+				SETFIELDHANDLER_METHOD_NAME,
+				SETFIELDHANDLER_METHOD_DESCRIPTOR
+		);
+
 		/* local variables | this | callback | */
-		Bytecode code = new Bytecode(cp, 3, 3);
-		// aload_0 // load this
-		code.addAload(0);
-		// aload_1 // load callback
-		code.addAload(1);
+		final Bytecode code = new Bytecode(constPool, 3, 3);
+		// aload_0 : load this
+		code.addAload( 0 );
+		// aload_1 : load callback
+		code.addAload( 1 );
 		// putfield // put field "$JAVASSIST_CALLBACK" defined already
-		code.addOpcode(Opcode.PUTFIELD);
-		int field_index = cp.addFieldrefInfo(this_class_index,
-		                                     HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR);
-		code.addIndex(field_index);
+		code.addOpcode( Opcode.PUTFIELD );
+		final int fieldIndex = constPool.addFieldrefInfo( thisClassInfo, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
+		code.addIndex( fieldIndex );
 		// return
-		code.addOpcode(Opcode.RETURN);
-		minfo.setCodeAttribute(code.toCodeAttribute());
-		minfo.setAccessFlags(AccessFlag.PUBLIC);
-		CodeAttribute codeAttribute = minfo.getCodeAttribute();
-		if (codeAttribute != null) {
-			StackMapTable smt = MapMaker.make(classPool, minfo);
-			codeAttribute.setAttribute(smt);
-		}
-		classfile.addMethod(minfo);
+		code.addOpcode( Opcode.RETURN );
+		methodInfo.setCodeAttribute( code.toCodeAttribute() );
+		methodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
+		if ( codeAttribute != null ) {
+			final StackMapTable smt = MapMaker.make( classPool, methodInfo );
+			codeAttribute.setAttribute( smt );
+		}
+		classfile.addMethod( methodInfo );
 	}
 
 	private void addFieldHandledInterface(ClassFile classfile) {
-		String[] interfaceNames = classfile.getInterfaces();
-		String[] newInterfaceNames = new String[interfaceNames.length + 1];
-		System.arraycopy(interfaceNames, 0, newInterfaceNames, 0,
-		                 interfaceNames.length);
+		final String[] interfaceNames = classfile.getInterfaces();
+		final String[] newInterfaceNames = new String[interfaceNames.length + 1];
+		System.arraycopy( interfaceNames, 0, newInterfaceNames, 0, interfaceNames.length );
 		newInterfaceNames[newInterfaceNames.length - 1] = FIELD_HANDLED_TYPE_NAME;
-		classfile.setInterfaces(newInterfaceNames);
+		classfile.setInterfaces( newInterfaceNames );
 	}
 
-	private void addReadWriteMethods(ClassFile classfile)
-			throws CannotCompileException, BadBytecode {
-		List fields = classfile.getFields();
-		for (Iterator field_iter = fields.iterator(); field_iter.hasNext();) {
-			FieldInfo finfo = (FieldInfo) field_iter.next();
-			if ((finfo.getAccessFlags() & AccessFlag.STATIC) == 0
-			    && (!finfo.getName().equals(HANDLER_FIELD_NAME))) {
+	private void addReadWriteMethods(ClassFile classfile) throws CannotCompileException, BadBytecode {
+		final List fields = classfile.getFields();
+		for ( Object field : fields ) {
+			final FieldInfo finfo = (FieldInfo) field;
+			if ( (finfo.getAccessFlags() & AccessFlag.STATIC) == 0 && (!finfo.getName().equals( HANDLER_FIELD_NAME )) ) {
 				// case of non-static field
-				if (filter.handleRead(finfo.getDescriptor(), finfo
-						.getName())) {
-					addReadMethod(classfile, finfo);
+				if ( filter.handleRead( finfo.getDescriptor(), finfo.getName() ) ) {
+					addReadMethod( classfile, finfo );
 				}
-				if (filter.handleWrite(finfo.getDescriptor(), finfo
-						.getName())) {
-					addWriteMethod(classfile, finfo);
+				if ( filter.handleWrite( finfo.getDescriptor(), finfo.getName() ) ) {
+					addWriteMethod( classfile, finfo );
 				}
 			}
 		}
 	}
 
-	private void addReadMethod(ClassFile classfile, FieldInfo finfo)
-			throws CannotCompileException, BadBytecode {
-		ConstPool cp = classfile.getConstPool();
-		int this_class_index = cp.getThisClassInfo();
-		String desc = "()" + finfo.getDescriptor();
-		MethodInfo minfo = new MethodInfo(cp, EACH_READ_METHOD_PREFIX
-		                                      + finfo.getName(), desc);
+	private void addReadMethod(ClassFile classfile, FieldInfo finfo) throws CannotCompileException, BadBytecode {
+		final ConstPool constPool = classfile.getConstPool();
+		final int thisClassInfo = constPool.getThisClassInfo();
+		final String readMethodDescriptor = "()" + finfo.getDescriptor();
+		final MethodInfo readMethodInfo = new MethodInfo(
+				constPool,
+				EACH_READ_METHOD_PREFIX + finfo.getName(),
+				readMethodDescriptor
+		);
+
 		/* local variables | target obj | each oldvalue | */
-		Bytecode code = new Bytecode(cp, 5, 3);
+		final Bytecode code = new Bytecode(constPool, 5, 3);
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// getfield // get each field
-		code.addOpcode(Opcode.GETFIELD);
-		int base_field_index = cp.addFieldrefInfo(this_class_index, finfo
-				.getName(), finfo.getDescriptor());
-		code.addIndex(base_field_index);
+		code.addOpcode( Opcode.GETFIELD );
+		final int baseFieldIndex = constPool.addFieldrefInfo( thisClassInfo, finfo.getName(), finfo.getDescriptor() );
+		code.addIndex( baseFieldIndex );
 		// aload_0
-		code.addAload(0);
-		// invokeinterface // invoke Enabled.getInterceptFieldCallback()
-		int enabled_class_index = cp.addClassInfo(FIELD_HANDLED_TYPE_NAME);
-		code.addInvokeinterface(enabled_class_index,
-		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-		                        1);
+		code.addAload( 0 );
+		// invokeinterface : invoke Enabled.getInterceptFieldCallback()
+		final int enabledClassIndex = constPool.addClassInfo( FIELD_HANDLED_TYPE_NAME );
+		code.addInvokeinterface(
+				enabledClassIndex,
+				GETFIELDHANDLER_METHOD_NAME,
+				GETFIELDHANDLER_METHOD_DESCRIPTOR,
+				1
+		);
 		// ifnonnull
-		code.addOpcode(Opcode.IFNONNULL);
-		code.addIndex(4);
+		code.addOpcode( Opcode.IFNONNULL );
+		code.addIndex( 4 );
 		// *return // each type
-		addTypeDependDataReturn(code, finfo.getDescriptor());
+		addTypeDependDataReturn( code, finfo.getDescriptor() );
 		// *store_1 // each type
-		addTypeDependDataStore(code, finfo.getDescriptor(), 1);
+		addTypeDependDataStore( code, finfo.getDescriptor(), 1 );
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// invokeinterface // invoke Enabled.getInterceptFieldCallback()
-		code.addInvokeinterface(enabled_class_index,
-		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-		                        1);
+		code.addInvokeinterface(
+				enabledClassIndex,
+				GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
+				1
+		);
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// ldc // name of the field
-		code.addLdc(finfo.getName());
+		code.addLdc( finfo.getName() );
 		// *load_1 // each type
-		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
+		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
 		// invokeinterface // invoke Callback.read*() // each type
-		addInvokeFieldHandlerMethod(classfile, code, finfo.getDescriptor(),
-		                            true);
+		addInvokeFieldHandlerMethod(
+				classfile, code, finfo.getDescriptor(),
+				true
+		);
 		// *return // each type
-		addTypeDependDataReturn(code, finfo.getDescriptor());
-
-		minfo.setCodeAttribute(code.toCodeAttribute());
-		minfo.setAccessFlags(AccessFlag.PUBLIC);
-		CodeAttribute codeAttribute = minfo.getCodeAttribute();
-		if (codeAttribute != null) {
-			StackMapTable smt = MapMaker.make(classPool, minfo);
-			codeAttribute.setAttribute(smt);
+		addTypeDependDataReturn( code, finfo.getDescriptor() );
+
+		readMethodInfo.setCodeAttribute( code.toCodeAttribute() );
+		readMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		final CodeAttribute codeAttribute = readMethodInfo.getCodeAttribute();
+		if ( codeAttribute != null ) {
+			final StackMapTable smt = MapMaker.make( classPool, readMethodInfo );
+			codeAttribute.setAttribute( smt );
 		}
-		classfile.addMethod(minfo);
+		classfile.addMethod( readMethodInfo );
 	}
 
-	private void addWriteMethod(ClassFile classfile, FieldInfo finfo)
-			throws CannotCompileException, BadBytecode {
-		ConstPool cp = classfile.getConstPool();
-		int this_class_index = cp.getThisClassInfo();
-		String desc = "(" + finfo.getDescriptor() + ")V";
-		MethodInfo minfo = new MethodInfo(cp, EACH_WRITE_METHOD_PREFIX
-		                                      + finfo.getName(), desc);
+	private void addWriteMethod(ClassFile classfile, FieldInfo finfo) throws CannotCompileException, BadBytecode {
+		final ConstPool constPool = classfile.getConstPool();
+		final int thisClassInfo = constPool.getThisClassInfo();
+		final String writeMethodDescriptor = "(" + finfo.getDescriptor() + ")V";
+		final MethodInfo writeMethodInfo = new MethodInfo(
+				constPool,
+				EACH_WRITE_METHOD_PREFIX+ finfo.getName(),
+				writeMethodDescriptor
+		);
+
 		/* local variables | target obj | each oldvalue | */
-		Bytecode code = new Bytecode(cp, 6, 3);
+		final Bytecode code = new Bytecode(constPool, 6, 3);
 		// aload_0
-		code.addAload(0);
-		// invokeinterface // enabled.getInterceptFieldCallback()
-		int enabled_class_index = cp.addClassInfo(FIELD_HANDLED_TYPE_NAME);
-		code.addInvokeinterface(enabled_class_index,
-		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-		                        1);
+		code.addAload( 0 );
+		// invokeinterface : enabled.getInterceptFieldCallback()
+		final int enabledClassIndex = constPool.addClassInfo( FIELD_HANDLED_TYPE_NAME );
+		code.addInvokeinterface(
+				enabledClassIndex,
+				GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
+				1
+		);
 		// ifnonnull (label1)
-		code.addOpcode(Opcode.IFNONNULL);
-		code.addIndex(9);
+		code.addOpcode( Opcode.IFNONNULL );
+		code.addIndex( 9 );
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// *load_1
-		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
+		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
 		// putfield
-		code.addOpcode(Opcode.PUTFIELD);
-		int base_field_index = cp.addFieldrefInfo(this_class_index, finfo
-				.getName(), finfo.getDescriptor());
-		code.addIndex(base_field_index);
-		code.growStack(-Descriptor.dataSize(finfo.getDescriptor()));
+		code.addOpcode( Opcode.PUTFIELD );
+		final int baseFieldIndex = constPool.addFieldrefInfo( thisClassInfo, finfo.getName(), finfo.getDescriptor() );
+		code.addIndex( baseFieldIndex );
+		code.growStack( -Descriptor.dataSize( finfo.getDescriptor() ) );
 		// return ;
-		code.addOpcode(Opcode.RETURN);
+		code.addOpcode( Opcode.RETURN );
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// dup
-		code.addOpcode(Opcode.DUP);
+		code.addOpcode( Opcode.DUP );
 		// invokeinterface // enabled.getInterceptFieldCallback()
-		code.addInvokeinterface(enabled_class_index,
-		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-		                        1);
+		code.addInvokeinterface(
+				enabledClassIndex,
+				GETFIELDHANDLER_METHOD_NAME,
+				GETFIELDHANDLER_METHOD_DESCRIPTOR,
+				1
+		);
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// ldc // field name
-		code.addLdc(finfo.getName());
+		code.addLdc( finfo.getName() );
 		// aload_0
-		code.addAload(0);
+		code.addAload( 0 );
 		// getfield // old value of the field
-		code.addOpcode(Opcode.GETFIELD);
-		code.addIndex(base_field_index);
-		code.growStack(Descriptor.dataSize(finfo.getDescriptor()) - 1);
+		code.addOpcode( Opcode.GETFIELD );
+		code.addIndex( baseFieldIndex );
+		code.growStack( Descriptor.dataSize( finfo.getDescriptor() ) - 1 );
 		// *load_1
-		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
+		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
 		// invokeinterface // callback.write*(..)
-		addInvokeFieldHandlerMethod(classfile, code, finfo.getDescriptor(),
-		                            false);
+		addInvokeFieldHandlerMethod( classfile, code, finfo.getDescriptor(), false );
 		// putfield // new value of the field
-		code.addOpcode(Opcode.PUTFIELD);
-		code.addIndex(base_field_index);
-		code.growStack(-Descriptor.dataSize(finfo.getDescriptor()));
+		code.addOpcode( Opcode.PUTFIELD );
+		code.addIndex( baseFieldIndex );
+		code.growStack( -Descriptor.dataSize( finfo.getDescriptor() ) );
 		// return
-		code.addOpcode(Opcode.RETURN);
-
-		minfo.setCodeAttribute(code.toCodeAttribute());
-		minfo.setAccessFlags(AccessFlag.PUBLIC);
-		CodeAttribute codeAttribute = minfo.getCodeAttribute();
-		if (codeAttribute != null) {
-			StackMapTable smt = MapMaker.make(classPool, minfo);
-			codeAttribute.setAttribute(smt);
+		code.addOpcode( Opcode.RETURN );
+
+		writeMethodInfo.setCodeAttribute( code.toCodeAttribute() );
+		writeMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
+		final CodeAttribute codeAttribute = writeMethodInfo.getCodeAttribute();
+		if ( codeAttribute != null ) {
+			final StackMapTable smt = MapMaker.make( classPool, writeMethodInfo );
+			codeAttribute.setAttribute( smt );
 		}
-		classfile.addMethod(minfo);
+		classfile.addMethod( writeMethodInfo );
 	}
 
-	private void transformInvokevirtualsIntoPutAndGetfields(ClassFile classfile)
-			throws CannotCompileException, BadBytecode {
-		List methods = classfile.getMethods();
-		for (Iterator method_iter = methods.iterator(); method_iter.hasNext();) {
-			MethodInfo minfo = (MethodInfo) method_iter.next();
-			String methodName = minfo.getName();
-			if (methodName.startsWith(EACH_READ_METHOD_PREFIX)
-			    || methodName.startsWith(EACH_WRITE_METHOD_PREFIX)
-			    || methodName.equals(GETFIELDHANDLER_METHOD_NAME)
-			    || methodName.equals(SETFIELDHANDLER_METHOD_NAME)) {
+	private void transformInvokevirtualsIntoPutAndGetfields(ClassFile classfile) throws CannotCompileException, BadBytecode {
+		for ( Object o : classfile.getMethods() ) {
+			final MethodInfo methodInfo = (MethodInfo) o;
+			final String methodName = methodInfo.getName();
+			if ( methodName.startsWith( EACH_READ_METHOD_PREFIX )
+					|| methodName.startsWith( EACH_WRITE_METHOD_PREFIX )
+					|| methodName.equals( GETFIELDHANDLER_METHOD_NAME )
+					|| methodName.equals( SETFIELDHANDLER_METHOD_NAME ) ) {
 				continue;
 			}
-			CodeAttribute codeAttr = minfo.getCodeAttribute();
-			if (codeAttr == null) {
+
+			final CodeAttribute codeAttr = methodInfo.getCodeAttribute();
+			if ( codeAttr == null ) {
 				continue;
 			}
-			CodeIterator iter = codeAttr.iterator();
-			while (iter.hasNext()) {
+
+			final CodeIterator iter = codeAttr.iterator();
+			while ( iter.hasNext() ) {
 				int pos = iter.next();
-				pos = transformInvokevirtualsIntoGetfields(classfile, iter, pos);
-				pos = transformInvokevirtualsIntoPutfields(classfile, iter, pos);
+				pos = transformInvokevirtualsIntoGetfields( classfile, iter, pos );
+				transformInvokevirtualsIntoPutfields( classfile, iter, pos );
 			}
-			StackMapTable smt = MapMaker.make(classPool, minfo);
-			codeAttr.setAttribute(smt);
+			final StackMapTable smt = MapMaker.make( classPool, methodInfo );
+			codeAttr.setAttribute( smt );
 		}
 	}
 
 	private int transformInvokevirtualsIntoGetfields(ClassFile classfile, CodeIterator iter, int pos) {
-		ConstPool cp = classfile.getConstPool();
-		int c = iter.byteAt(pos);
-		if (c != Opcode.GETFIELD) {
+		final ConstPool constPool = classfile.getConstPool();
+		final int c = iter.byteAt( pos );
+		if ( c != Opcode.GETFIELD ) {
 			return pos;
 		}
-		int index = iter.u16bitAt(pos + 1);
-		String fieldName = cp.getFieldrefName(index);
-		String className = cp.getFieldrefClassName(index);
+
+		final int index = iter.u16bitAt( pos + 1 );
+		final String fieldName = constPool.getFieldrefName( index );
+		final String className = constPool.getFieldrefClassName( index );
 		if ( !filter.handleReadAccess( className, fieldName ) ) {
 			return pos;
 		}
-		String desc = "()" + cp.getFieldrefType( index );
-		int read_method_index = cp.addMethodrefInfo(
-				cp.getThisClassInfo(),
+
+		final String fieldReaderMethodDescriptor = "()" + constPool.getFieldrefType( index );
+		final int fieldReaderMethodIndex = constPool.addMethodrefInfo(
+				constPool.getThisClassInfo(),
 				EACH_READ_METHOD_PREFIX + fieldName,
-				desc
+				fieldReaderMethodDescriptor
 		);
-		iter.writeByte(Opcode.INVOKEVIRTUAL, pos);
-		iter.write16bit(read_method_index, pos + 1);
+		iter.writeByte( Opcode.INVOKEVIRTUAL, pos );
+		iter.write16bit( fieldReaderMethodIndex, pos + 1 );
 		return pos;
 	}
 
-	private int transformInvokevirtualsIntoPutfields(
-			ClassFile classfile,
-			CodeIterator iter, int pos) {
-		ConstPool cp = classfile.getConstPool();
-		int c = iter.byteAt(pos);
-		if (c != Opcode.PUTFIELD) {
+	private int transformInvokevirtualsIntoPutfields(ClassFile classfile, CodeIterator iter, int pos) {
+		final ConstPool constPool = classfile.getConstPool();
+		final int c = iter.byteAt( pos );
+		if ( c != Opcode.PUTFIELD ) {
 			return pos;
 		}
-		int index = iter.u16bitAt(pos + 1);
-		String fieldName = cp.getFieldrefName(index);
-		String className = cp.getFieldrefClassName(index);
+
+		final int index = iter.u16bitAt( pos + 1 );
+		final String fieldName = constPool.getFieldrefName( index );
+		final String className = constPool.getFieldrefClassName( index );
 		if ( !filter.handleWriteAccess( className, fieldName ) ) {
 			return pos;
 		}
-		String desc = "(" + cp.getFieldrefType( index ) + ")V";
-		int write_method_index = cp.addMethodrefInfo(
-				cp.getThisClassInfo(),
+
+		final String fieldWriterMethodDescriptor = "(" + constPool.getFieldrefType( index ) + ")V";
+		final int fieldWriterMethodIndex = constPool.addMethodrefInfo(
+				constPool.getThisClassInfo(),
 				EACH_WRITE_METHOD_PREFIX + fieldName,
-				desc
+				fieldWriterMethodDescriptor
 		);
-		iter.writeByte(Opcode.INVOKEVIRTUAL, pos);
-		iter.write16bit(write_method_index, pos + 1);
+		iter.writeByte( Opcode.INVOKEVIRTUAL, pos );
+		iter.write16bit( fieldWriterMethodIndex, pos + 1 );
 		return pos;
 	}
 
-	private static void addInvokeFieldHandlerMethod(ClassFile classfile,
-	                                                Bytecode code, String typeName, boolean isReadMethod) {
-		ConstPool cp = classfile.getConstPool();
+	private static void addInvokeFieldHandlerMethod(
+			ClassFile classfile,
+			Bytecode code,
+			String typeName,
+			boolean isReadMethod) {
+		final ConstPool constPool = classfile.getConstPool();
 		// invokeinterface
-		int callback_type_index = cp.addClassInfo(FIELD_HANDLER_TYPE_NAME);
-		if ((typeName.charAt(0) == 'L')
-		    && (typeName.charAt(typeName.length() - 1) == ';')
-		    || (typeName.charAt(0) == '[')) {
+		final int callbackTypeIndex = constPool.addClassInfo( FIELD_HANDLER_TYPE_NAME );
+		if ( ( typeName.charAt( 0 ) == 'L' )
+				&& ( typeName.charAt( typeName.length() - 1 ) == ';' )
+				|| ( typeName.charAt( 0 ) == '[' ) ) {
 			// reference type
-			int indexOfL = typeName.indexOf('L');
+			final int indexOfL = typeName.indexOf( 'L' );
 			String type;
-			if (indexOfL == 0) {
+			if ( indexOfL == 0 ) {
 				// not array
-				type = typeName.substring(1, typeName.length() - 1);
-				type = type.replace('/', '.');
-			} else if (indexOfL == -1) {
+				type = typeName.substring( 1, typeName.length() - 1 );
+				type = type.replace( '/', '.' );
+			}
+			else if ( indexOfL == -1 ) {
 				// array of primitive type
 				// do nothing
 				type = typeName;
-			} else {
+			}
+			else {
 				// array of reference type
-				type = typeName.replace('/', '.');
+				type = typeName.replace( '/', '.' );
 			}
-			if (isReadMethod) {
-				code
-						.addInvokeinterface(
-								callback_type_index,
-								"readObject",
-								"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
-								4);
+
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readObject",
+						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
+						4
+				);
 				// checkcast
-				code.addCheckcast(type);
-			} else {
-				code
-						.addInvokeinterface(
-								callback_type_index,
-								"writeObject",
-								"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
-								5);
+				code.addCheckcast( type );
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeObject",
+						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
+						5
+				);
 				// checkcast
-				code.addCheckcast(type);
+				code.addCheckcast( type );
 			}
-		} else if (typeName.equals("Z")) {
+		}
+		else if ( typeName.equals( "Z" ) ) {
 			// boolean
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readBoolean",
-				                        "(Ljava/lang/Object;Ljava/lang/String;Z)Z", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeBoolean",
-				                        "(Ljava/lang/Object;Ljava/lang/String;ZZ)Z", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readBoolean",
+						"(Ljava/lang/Object;Ljava/lang/String;Z)Z",
+						4
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeBoolean",
+						"(Ljava/lang/Object;Ljava/lang/String;ZZ)Z",
+						5
+				);
 			}
-		} else if (typeName.equals("B")) {
+		}
+		else if ( typeName.equals( "B" ) ) {
 			// byte
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readByte",
-				                        "(Ljava/lang/Object;Ljava/lang/String;B)B", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeByte",
-				                        "(Ljava/lang/Object;Ljava/lang/String;BB)B", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readByte",
+						"(Ljava/lang/Object;Ljava/lang/String;B)B",
+						4
+				);
 			}
-		} else if (typeName.equals("C")) {
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeByte",
+						"(Ljava/lang/Object;Ljava/lang/String;BB)B",
+						5
+				);
+			}
+		}
+		else if ( typeName.equals( "C" ) ) {
 			// char
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readChar",
-				                        "(Ljava/lang/Object;Ljava/lang/String;C)C", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeChar",
-				                        "(Ljava/lang/Object;Ljava/lang/String;CC)C", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readChar",
+						"(Ljava/lang/Object;Ljava/lang/String;C)C",
+						4
+				);
 			}
-		} else if (typeName.equals("I")) {
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeChar",
+						"(Ljava/lang/Object;Ljava/lang/String;CC)C",
+						5
+				);
+			}
+		}
+		else if ( typeName.equals( "I" ) ) {
 			// int
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readInt",
-				                        "(Ljava/lang/Object;Ljava/lang/String;I)I", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeInt",
-				                        "(Ljava/lang/Object;Ljava/lang/String;II)I", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readInt",
+						"(Ljava/lang/Object;Ljava/lang/String;I)I",
+						4
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeInt",
+						"(Ljava/lang/Object;Ljava/lang/String;II)I",
+						5
+				);
 			}
-		} else if (typeName.equals("S")) {
+		}
+		else if ( typeName.equals( "S" ) ) {
 			// short
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readShort",
-				                        "(Ljava/lang/Object;Ljava/lang/String;S)S", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeShort",
-				                        "(Ljava/lang/Object;Ljava/lang/String;SS)S", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readShort",
+						"(Ljava/lang/Object;Ljava/lang/String;S)S",
+						4
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeShort",
+						"(Ljava/lang/Object;Ljava/lang/String;SS)S",
+						5
+				);
 			}
-		} else if (typeName.equals("D")) {
+		}
+		else if ( typeName.equals( "D" ) ) {
 			// double
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readDouble",
-				                        "(Ljava/lang/Object;Ljava/lang/String;D)D", 5);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeDouble",
-				                        "(Ljava/lang/Object;Ljava/lang/String;DD)D", 7);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readDouble",
+						"(Ljava/lang/Object;Ljava/lang/String;D)D",
+						5
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeDouble",
+						"(Ljava/lang/Object;Ljava/lang/String;DD)D",
+						7
+				);
 			}
-		} else if (typeName.equals("F")) {
+		}
+		else if ( typeName.equals( "F" ) ) {
 			// float
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readFloat",
-				                        "(Ljava/lang/Object;Ljava/lang/String;F)F", 4);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeFloat",
-				                        "(Ljava/lang/Object;Ljava/lang/String;FF)F", 5);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readFloat",
+						"(Ljava/lang/Object;Ljava/lang/String;F)F",
+						4
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeFloat",
+						"(Ljava/lang/Object;Ljava/lang/String;FF)F",
+						5
+				);
 			}
-		} else if (typeName.equals("J")) {
+		}
+		else if ( typeName.equals( "J" ) ) {
 			// long
-			if (isReadMethod) {
-				code.addInvokeinterface(callback_type_index, "readLong",
-				                        "(Ljava/lang/Object;Ljava/lang/String;J)J", 5);
-			} else {
-				code.addInvokeinterface(callback_type_index, "writeLong",
-				                        "(Ljava/lang/Object;Ljava/lang/String;JJ)J", 7);
+			if ( isReadMethod ) {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"readLong",
+						"(Ljava/lang/Object;Ljava/lang/String;J)J",
+						5
+				);
+			}
+			else {
+				code.addInvokeinterface(
+						callbackTypeIndex,
+						"writeLong",
+						"(Ljava/lang/Object;Ljava/lang/String;JJ)J",
+						7
+				);
 			}
-		} else {
+		}
+		else {
 			// bad type
-			throw new RuntimeException("bad type: " + typeName);
+			throw new RuntimeException( "bad type: " + typeName );
 		}
 	}
 
-	private static void addTypeDependDataLoad(Bytecode code, String typeName,
-	                                          int i) {
-		if ((typeName.charAt(0) == 'L')
-		    && (typeName.charAt(typeName.length() - 1) == ';')
-		    || (typeName.charAt(0) == '[')) {
+	private static void addTypeDependDataLoad(Bytecode code, String typeName, int i) {
+		if ( typeName.charAt( 0 ) == 'L'
+				&& typeName.charAt( typeName.length() - 1 ) == ';'
+				|| typeName.charAt( 0 ) == '[' ) {
 			// reference type
-			code.addAload(i);
-		} else if (typeName.equals("Z") || typeName.equals("B")
-		           || typeName.equals("C") || typeName.equals("I")
-		           || typeName.equals("S")) {
+			code.addAload( i );
+		}
+		else if ( typeName.equals( "Z" )
+				|| typeName.equals( "B" )
+				|| typeName.equals( "C" )
+				|| typeName.equals( "I" )
+				|| typeName.equals( "S" ) ) {
 			// boolean, byte, char, int, short
-			code.addIload(i);
-		} else if (typeName.equals("D")) {
+			code.addIload( i );
+		}
+		else if ( typeName.equals( "D" ) ) {
 			// double
-			code.addDload(i);
-		} else if (typeName.equals("F")) {
+			code.addDload( i );
+		}
+		else if ( typeName.equals( "F" ) ) {
 			// float
-			code.addFload(i);
-		} else if (typeName.equals("J")) {
+			code.addFload( i );
+		}
+		else if ( typeName.equals( "J" ) ) {
 			// long
-			code.addLload(i);
-		} else {
+			code.addLload( i );
+		}
+		else {
 			// bad type
-			throw new RuntimeException("bad type: " + typeName);
+			throw new RuntimeException( "bad type: " + typeName );
 		}
 	}
 
-	private static void addTypeDependDataStore(Bytecode code, String typeName,
-	                                           int i) {
-		if ((typeName.charAt(0) == 'L')
-		    && (typeName.charAt(typeName.length() - 1) == ';')
-		    || (typeName.charAt(0) == '[')) {
+	private static void addTypeDependDataStore(Bytecode code, String typeName, int i) {
+		if ( typeName.charAt( 0 ) == 'L'
+				&& typeName.charAt( typeName.length() - 1 ) == ';'
+				|| typeName.charAt( 0 ) == '[' ) {
 			// reference type
-			code.addAstore(i);
-		} else if (typeName.equals("Z") || typeName.equals("B")
-		           || typeName.equals("C") || typeName.equals("I")
-		           || typeName.equals("S")) {
+			code.addAstore( i );
+		}
+		else if ( typeName.equals( "Z" )
+				|| typeName.equals( "B" )
+				|| typeName.equals( "C" )
+				|| typeName.equals( "I" )
+				|| typeName.equals( "S" ) ) {
 			// boolean, byte, char, int, short
-			code.addIstore(i);
-		} else if (typeName.equals("D")) {
+			code.addIstore( i );
+		}
+		else if ( typeName.equals( "D" ) ) {
 			// double
-			code.addDstore(i);
-		} else if (typeName.equals("F")) {
+			code.addDstore( i );
+		}
+		else if ( typeName.equals( "F" ) ) {
 			// float
-			code.addFstore(i);
-		} else if (typeName.equals("J")) {
+			code.addFstore( i );
+		}
+		else if ( typeName.equals( "J" ) ) {
 			// long
-			code.addLstore(i);
-		} else {
+			code.addLstore( i );
+		}
+		else {
 			// bad type
-			throw new RuntimeException("bad type: " + typeName);
+			throw new RuntimeException( "bad type: " + typeName );
 		}
 	}
 
 	private static void addTypeDependDataReturn(Bytecode code, String typeName) {
-		if ((typeName.charAt(0) == 'L')
-		    && (typeName.charAt(typeName.length() - 1) == ';')
-		    || (typeName.charAt(0) == '[')) {
+		if ( typeName.charAt( 0 ) == 'L'
+				&& typeName.charAt( typeName.length() - 1 ) == ';'
+				|| typeName.charAt( 0 ) == '[') {
 			// reference type
-			code.addOpcode(Opcode.ARETURN);
-		} else if (typeName.equals("Z") || typeName.equals("B")
-		           || typeName.equals("C") || typeName.equals("I")
-		           || typeName.equals("S")) {
+			code.addOpcode( Opcode.ARETURN );
+		}
+		else if ( typeName.equals( "Z" )
+				|| typeName.equals( "B" )
+				|| typeName.equals( "C" )
+				|| typeName.equals( "I" )
+				|| typeName.equals( "S" ) ) {
 			// boolean, byte, char, int, short
-			code.addOpcode(Opcode.IRETURN);
-		} else if (typeName.equals("D")) {
+			code.addOpcode( Opcode.IRETURN );
+		}
+		else if ( typeName.equals( "D" ) ) {
 			// double
-			code.addOpcode(Opcode.DRETURN);
-		} else if (typeName.equals("F")) {
+			code.addOpcode( Opcode.DRETURN );
+		}
+		else if ( typeName.equals( "F" ) ) {
 			// float
-			code.addOpcode(Opcode.FRETURN);
-		} else if (typeName.equals("J")) {
+			code.addOpcode( Opcode.FRETURN );
+		}
+		else if ( typeName.equals( "J" ) ) {
 			// long
-			code.addOpcode(Opcode.LRETURN);
-		} else {
+			code.addOpcode( Opcode.LRETURN );
+		}
+		else {
 			// bad type
-			throw new RuntimeException("bad type: " + typeName);
+			throw new RuntimeException( "bad type: " + typeName );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
index a66000b0af..c064b82e66 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
@@ -1,55 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.Serializable;
 
 import org.hibernate.InstantiationException;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 
 /**
  * The {@link org.hibernate.bytecode.spi.ReflectionOptimizer.InstantiationOptimizer} implementation for Javassist
  * which simply acts as an adapter to the {@link FastClass} class.
  *
  * @author Steve Ebersole
  */
 public class InstantiationOptimizerAdapter implements ReflectionOptimizer.InstantiationOptimizer, Serializable {
 	private final FastClass fastClass;
 
+	/**
+	 * Constructs the InstantiationOptimizerAdapter
+	 *
+	 * @param fastClass The fast class for the class to be instantiated here.
+	 */
 	public InstantiationOptimizerAdapter(FastClass fastClass) {
 		this.fastClass = fastClass;
 	}
 
+	@Override
 	public Object newInstance() {
 		try {
 			return fastClass.newInstance();
 		}
-		catch ( Throwable t ) {
+		catch ( Exception e ) {
 			throw new InstantiationException(
 					"Could not instantiate entity with Javassist optimizer: ",
-			        fastClass.getJavaClass(), t
+					fastClass.getJavaClass(),
+					e
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
index d8f440117e..d8d725b678 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
@@ -1,147 +1,161 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataInputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.security.ProtectionDomain;
 
 import javassist.ClassClassPath;
 import javassist.ClassPool;
 import javassist.bytecode.ClassFile;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.buildtime.spi.ClassFilter;
 import org.hibernate.bytecode.spi.AbstractClassTransformerImpl;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Enhance the classes allowing them to implements InterceptFieldEnabled
  * This interface is then used by Hibernate for some optimizations.
  *
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Dustin Schultz
  */
 public class JavassistClassTransformer extends AbstractClassTransformerImpl {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			JavassistClassTransformer.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       JavassistClassTransformer.class.getName());
-
+	/**
+	 * Constructs the JavassistClassTransformer
+	 *
+	 * @param classFilter The filter used to determine which classes to transform
+	 * @param fieldFilter The filter used to determine which fields to transform
+	 */
 	public JavassistClassTransformer(ClassFilter classFilter, org.hibernate.bytecode.buildtime.spi.FieldFilter fieldFilter) {
 		super( classFilter, fieldFilter );
 	}
 
 	@Override
 	protected byte[] doTransform(
 			ClassLoader loader,
 			String className,
 			Class classBeingRedefined,
 			ProtectionDomain protectionDomain,
 			byte[] classfileBuffer) {
 		ClassFile classfile;
 		try {
 			// WARNING: classfile only
 			classfile = new ClassFile( new DataInputStream( new ByteArrayInputStream( classfileBuffer ) ) );
 		}
 		catch (IOException e) {
 			LOG.unableToBuildEnhancementMetamodel( className );
 			return classfileBuffer;
 		}
-		// This is the same as ClassPool.getDefault() but ensures a new ClassPool per
-		ClassPool cp = new ClassPool();
+
+		final ClassPool cp = new ClassPool();
 		cp.appendSystemPath();
-		cp.appendClassPath(new ClassClassPath(this.getClass()));
-		cp.appendClassPath(new ClassClassPath(classfile.getClass()));
+		cp.appendClassPath( new ClassClassPath( this.getClass() ) );
+		cp.appendClassPath( new ClassClassPath( classfile.getClass() ) );
+
 		try {
-			cp.makeClassIfNew(new ByteArrayInputStream(classfileBuffer));
-		} catch (IOException e) {
-			throw new RuntimeException(e.getMessage(), e);
+			cp.makeClassIfNew( new ByteArrayInputStream( classfileBuffer ) );
+		}
+		catch (IOException e) {
+			throw new RuntimeException( e.getMessage(), e );
 		}
-		FieldTransformer transformer = getFieldTransformer( classfile, cp );
+
+		final FieldTransformer transformer = getFieldTransformer( classfile, cp );
 		if ( transformer != null ) {
 			LOG.debugf( "Enhancing %s", className );
+
 			DataOutputStream out = null;
 			try {
 				transformer.transform( classfile );
-				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
+				final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
 				out = new DataOutputStream( byteStream );
 				classfile.write( out );
 				return byteStream.toByteArray();
 			}
 			catch (Exception e) {
 				LOG.unableToTransformClass( e.getMessage() );
 				throw new HibernateException( "Unable to transform class: " + e.getMessage() );
 			}
 			finally {
 				try {
-					if ( out != null ) out.close();
+					if ( out != null ) {
+						out.close();
+					}
 				}
 				catch (IOException e) {
 					//swallow
 				}
 			}
 		}
 		return classfileBuffer;
 	}
 
 	protected FieldTransformer getFieldTransformer(final ClassFile classfile, final ClassPool classPool) {
 		if ( alreadyInstrumented( classfile ) ) {
 			return null;
 		}
 		return new FieldTransformer(
 				new FieldFilter() {
 					public boolean handleRead(String desc, String name) {
 						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
 					}
 
 					public boolean handleWrite(String desc, String name) {
 						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
 					}
 
 					public boolean handleReadAccess(String fieldOwnerClassName, String fieldName) {
 						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
 					}
 
 					public boolean handleWriteAccess(String fieldOwnerClassName, String fieldName) {
 						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
 					}
-				}, classPool
+				},
+				classPool
 		);
 	}
 
 	private boolean alreadyInstrumented(ClassFile classfile) {
-		String[] intfs = classfile.getInterfaces();
-		for ( int i = 0; i < intfs.length; i++ ) {
-			if ( FieldHandled.class.getName().equals( intfs[i] ) ) {
+		final String[] interfaces = classfile.getInterfaces();
+		for ( String anInterface : interfaces ) {
+			if ( FieldHandled.class.getName().equals( anInterface ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
index 73db828ef9..abb5cbb04d 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
@@ -1,147 +1,161 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Method;
 import java.util.HashMap;
 
 import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
 import javassist.util.proxy.ProxyObject;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.spi.BasicProxyFactory;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.proxy.pojo.javassist.JavassistProxyFactory;
 
 /**
  * A factory for Javassist-based {@link ProxyFactory} instances.
  *
  * @author Steve Ebersole
  */
 public class ProxyFactoryFactoryImpl implements ProxyFactoryFactory {
 
 	/**
 	 * Builds a Javassist-based proxy factory.
 	 *
 	 * @return a new Javassist-based proxy factory.
 	 */
 	public ProxyFactory buildProxyFactory() {
 		return new JavassistProxyFactory();
 	}
 
+	/**
+	 * Constructs a BasicProxyFactoryImpl
+	 *
+	 * @param superClass The abstract super class (or null if none).
+	 * @param interfaces Interfaces to be proxied (or null if none).
+	 *
+	 * @return The constructed BasicProxyFactoryImpl
+	 */
 	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces) {
 		return new BasicProxyFactoryImpl( superClass, interfaces );
 	}
 
 	private static class BasicProxyFactoryImpl implements BasicProxyFactory {
 		private final Class proxyClass;
 
 		public BasicProxyFactoryImpl(Class superClass, Class[] interfaces) {
 			if ( superClass == null && ( interfaces == null || interfaces.length < 1 ) ) {
 				throw new AssertionFailure( "attempting to build proxy without any superclass or interfaces" );
 			}
-			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
+
+			final javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
 			factory.setFilter( FINALIZE_FILTER );
 			if ( superClass != null ) {
 				factory.setSuperclass( superClass );
 			}
 			if ( interfaces != null && interfaces.length > 0 ) {
 				factory.setInterfaces( interfaces );
 			}
 			proxyClass = factory.createClass();
 		}
 
 		public Object getProxy() {
 			try {
-				ProxyObject proxy = ( ProxyObject ) proxyClass.newInstance();
+				final ProxyObject proxy = (ProxyObject) proxyClass.newInstance();
 				proxy.setHandler( new PassThroughHandler( proxy, proxyClass.getName() ) );
 				return proxy;
 			}
 			catch ( Throwable t ) {
 				throw new HibernateException( "Unable to instantiated proxy instance" );
 			}
 		}
 
 		public boolean isInstance(Object object) {
 			return proxyClass.isInstance( object );
 		}
 	}
 
 	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
 		public boolean isHandled(Method m) {
 			// skip finalize methods
 			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
 		}
 	};
 
 	private static class PassThroughHandler implements MethodHandler {
 		private HashMap data = new HashMap();
 		private final Object proxiedObject;
 		private final String proxiedClassName;
 
 		public PassThroughHandler(Object proxiedObject, String proxiedClassName) {
 			this.proxiedObject = proxiedObject;
 			this.proxiedClassName = proxiedClassName;
 		}
 
+		@SuppressWarnings("unchecked")
 		public Object invoke(
 				Object object,
-		        Method method,
-		        Method method1,
-		        Object[] args) throws Exception {
-			String name = method.getName();
+				Method method,
+				Method method1,
+				Object[] args) throws Exception {
+			final String name = method.getName();
 			if ( "toString".equals( name ) ) {
 				return proxiedClassName + "@" + System.identityHashCode( object );
 			}
 			else if ( "equals".equals( name ) ) {
 				return proxiedObject == object;
 			}
 			else if ( "hashCode".equals( name ) ) {
 				return System.identityHashCode( object );
 			}
-			boolean hasGetterSignature = method.getParameterTypes().length == 0 && method.getReturnType() != null;
-			boolean hasSetterSignature = method.getParameterTypes().length == 1 && ( method.getReturnType() == null || method.getReturnType() == void.class );
+
+			final boolean hasGetterSignature = method.getParameterTypes().length == 0
+					&& method.getReturnType() != null;
+			final boolean hasSetterSignature = method.getParameterTypes().length == 1
+					&& ( method.getReturnType() == null || method.getReturnType() == void.class );
+
 			if ( name.startsWith( "get" ) && hasGetterSignature ) {
-				String propName = name.substring( 3 );
+				final String propName = name.substring( 3 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "is" ) && hasGetterSignature ) {
-				String propName = name.substring( 2 );
+				final String propName = name.substring( 2 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "set" ) && hasSetterSignature) {
-				String propName = name.substring( 3 );
+				final String propName = name.substring( 3 );
 				data.put( propName, args[0] );
 				return null;
 			}
 			else {
 				// todo : what else to do here?
 				return null;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
index e95bc67254..ce754dc798 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
@@ -1,55 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.Serializable;
 
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 
 /**
  * ReflectionOptimizer implementation for Javassist.
  *
  * @author Steve Ebersole
  */
 public class ReflectionOptimizerImpl implements ReflectionOptimizer, Serializable {
-
 	private final InstantiationOptimizer instantiationOptimizer;
 	private final AccessOptimizer accessOptimizer;
 
+	/**
+	 * Constructs a ReflectionOptimizerImpl
+	 *
+	 * @param instantiationOptimizer The instantiation optimizer to use
+	 * @param accessOptimizer The property access optimizer to use.
+	 */
 	public ReflectionOptimizerImpl(
 			InstantiationOptimizer instantiationOptimizer,
-	        AccessOptimizer accessOptimizer) {
+			AccessOptimizer accessOptimizer) {
 		this.instantiationOptimizer = instantiationOptimizer;
 		this.accessOptimizer = accessOptimizer;
 	}
 
+	@Override
 	public InstantiationOptimizer getInstantiationOptimizer() {
 		return instantiationOptimizer;
 	}
 
+	@Override
 	public AccessOptimizer getAccessOptimizer() {
 		return accessOptimizer;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
index 0c03fa93be..0e96020a59 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
@@ -1,80 +1,87 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.io.IOException;
 
 import javassist.CannotCompileException;
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.NotFoundException;
 
 import org.hibernate.HibernateException;
 
 /**
+ * A ClassLoader implementation applying Class transformations as they are being loaded.
+ *
  * @author Steve Ebersole
  */
+@SuppressWarnings("UnusedDeclaration")
 public class TransformingClassLoader extends ClassLoader {
 	private ClassLoader parent;
 	private ClassPool classPool;
 
-	/*package*/ TransformingClassLoader(ClassLoader parent, String[] classpath) {
+	TransformingClassLoader(ClassLoader parent, String[] classpaths) {
 		this.parent = parent;
-		classPool = new ClassPool( true );
-		for ( int i = 0; i < classpath.length; i++ ) {
+		this.classPool = new ClassPool( true );
+		for ( String classpath : classpaths ) {
 			try {
-				classPool.appendClassPath( classpath[i] );
+				classPool.appendClassPath( classpath );
 			}
-			catch ( NotFoundException e ) {
+			catch (NotFoundException e) {
 				throw new HibernateException(
 						"Unable to resolve requested classpath for transformation [" +
-						classpath[i] + "] : " + e.getMessage()
+								classpath + "] : " + e.getMessage()
 				);
 			}
 		}
 	}
 
+	@Override
 	protected Class findClass(String name) throws ClassNotFoundException {
-        try {
-            CtClass cc = classPool.get( name );
-	        // todo : modify the class definition if not already transformed...
-            byte[] b = cc.toBytecode();
-            return defineClass( name, b, 0, b.length );
-        }
-        catch ( NotFoundException e ) {
-            throw new ClassNotFoundException();
-        }
-        catch ( IOException e ) {
-            throw new ClassNotFoundException();
-        }
-        catch ( CannotCompileException e ) {
-            throw new ClassNotFoundException();
-        }
-    }
+		try {
+			final CtClass cc = classPool.get( name );
+			// todo : modify the class definition if not already transformed...
+			byte[] b = cc.toBytecode();
+			return defineClass( name, b, 0, b.length );
+		}
+		catch (NotFoundException e) {
+			throw new ClassNotFoundException();
+		}
+		catch (IOException e) {
+			throw new ClassNotFoundException();
+		}
+		catch (CannotCompileException e) {
+			throw new ClassNotFoundException();
+		}
+	}
 
+	/**
+	 * Used to release resources.  Call when done with the ClassLoader
+	 */
 	public void release() {
 		classPool = null;
 		parent = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/package-info.java
new file mode 100644
index 0000000000..7cddc225e3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Javassist support internals
+ */
+package org.hibernate.bytecode.internal.javassist;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
index b310ac2207..a1d98cbffb 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
@@ -1,69 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 import java.security.ProtectionDomain;
 
 import org.hibernate.bytecode.buildtime.spi.ClassFilter;
 import org.hibernate.bytecode.buildtime.spi.FieldFilter;
 
 /**
  * Basic implementation of the {@link ClassTransformer} contract.
  *
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public abstract class AbstractClassTransformerImpl implements ClassTransformer {
-
 	protected final ClassFilter classFilter;
 	protected final FieldFilter fieldFilter;
 
 	protected AbstractClassTransformerImpl(ClassFilter classFilter, FieldFilter fieldFilter) {
 		this.classFilter = classFilter;
 		this.fieldFilter = fieldFilter;
 	}
 
+	@Override
 	public byte[] transform(
 			ClassLoader loader,
 			String className,
 			Class classBeingRedefined,
 			ProtectionDomain protectionDomain,
 			byte[] classfileBuffer) {
 		// to be safe...
 		className = className.replace( '/', '.' );
 		if ( classFilter.shouldInstrumentClass( className ) ) {
 			return doTransform( loader, className, classBeingRedefined, protectionDomain, classfileBuffer );
 		}
 		else {
 			return classfileBuffer;
 		}
 	}
 
+	/**
+	 * Delegate the transformation call from {@link #transform}
+	 *
+	 * @param loader The class loader to use
+	 * @param className The name of the class to transform
+	 * @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
+	 * @param protectionDomain The protection domain of the class being (re)defined
+	 * @param classfileBuffer The bytes of the class file.
+	 *
+	 * @return The transformed (enhanced/instrumented) bytes.
+	 */
 	protected abstract byte[] doTransform(
 			ClassLoader loader,
 			String className,
 			Class classBeingRedefined,
 			ProtectionDomain protectionDomain,
 			byte[] classfileBuffer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
index 41c6d136ab..c8447b7b30 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
@@ -1,38 +1,38 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 /**
- * A proxy factory for "basic proxy" generation
+ * A proxy factory for "basic proxy" generation.
  *
  * @author Steve Ebersole
  */
 public interface BasicProxyFactory {
 	/**
-	 * Get a proxy reference.
+	 * Get a proxy reference..
 	 *
 	 * @return A proxy reference.
 	 */
 	public Object getProxy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
index 623f1079fd..92784286a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
@@ -1,126 +1,129 @@
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
 package org.hibernate.bytecode.spi;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.zip.ZipInputStream;
 
 /**
  * A helper for reading byte code from various input sources.
  *
  * @author Steve Ebersole
  */
 public class ByteCodeHelper {
+	/**
+	 * Disallow instantiation (its a helper)
+	 */
 	private ByteCodeHelper() {
 	}
 
 	/**
 	 * Reads class byte array info from the given input stream.
-	 * <p/>
+	 *
 	 * The stream is closed within this method!
 	 *
 	 * @param inputStream The stream containing the class binary; null will lead to an {@link IOException}
 	 *
 	 * @return The read bytes
 	 *
 	 * @throws IOException Indicates a problem accessing the given stream.
 	 */
 	public static byte[] readByteCode(InputStream inputStream) throws IOException {
 		if ( inputStream == null ) {
 			throw new IOException( "null input stream" );
 		}
 
-		byte[] buffer = new byte[409600];
+		final byte[] buffer = new byte[409600];
 		byte[] classBytes = new byte[0];
 
 		try {
 			int r = inputStream.read( buffer );
 			while ( r >= buffer.length ) {
-				byte[] temp = new byte[ classBytes.length + buffer.length ];
+				final byte[] temp = new byte[ classBytes.length + buffer.length ];
 				// copy any previously read bytes into the temp array
 				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
 				// copy the just read bytes into the temp array (after the previously read)
 				System.arraycopy( buffer, 0, temp, classBytes.length, buffer.length );
 				classBytes = temp;
 				// read the next set of bytes into buffer
 				r = inputStream.read( buffer );
 			}
 			if ( r != -1 ) {
-				byte[] temp = new byte[ classBytes.length + r ];
+				final byte[] temp = new byte[ classBytes.length + r ];
 				// copy any previously read bytes into the temp array
 				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
 				// copy the just read bytes into the temp array (after the previously read)
 				System.arraycopy( buffer, 0, temp, classBytes.length, r );
 				classBytes = temp;
 			}
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch (IOException ignore) {
 				// intentionally empty
 			}
 		}
 
 		return classBytes;
 	}
 
 	/**
 	 * Read class definition from a file.
 	 *
 	 * @param file The file to read.
 	 *
 	 * @return The class bytes
 	 *
 	 * @throws IOException Indicates a problem accessing the given stream.
 	 */
 	public static byte[] readByteCode(File file) throws IOException {
 		return ByteCodeHelper.readByteCode( new FileInputStream( file ) );
 	}
 
 	/**
 	 * Read class definition a zip (jar) file entry.
 	 *
 	 * @param zip The zip entry stream.
-	 * 
+	 *
 	 * @return The class bytes
 	 *
 	 * @throws IOException Indicates a problem accessing the given stream.
 	 */
 	public static byte[] readByteCode(ZipInputStream zip) throws IOException {
-        ByteArrayOutputStream bout = new ByteArrayOutputStream();
-        InputStream in = new BufferedInputStream( zip );
-        int b;
-        while ( ( b = in.read() ) != -1 ) {
-            bout.write( b );
-        }
-        return bout.toByteArray();
-    }
+		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
+		final InputStream in = new BufferedInputStream( zip );
+		int b;
+		while ( ( b = in.read() ) != -1 ) {
+			bout.write( b );
+		}
+		return bout.toByteArray();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
index 0480c9b8f8..4fc0a56b38 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
@@ -1,56 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 import java.security.ProtectionDomain;
 
 /**
  * A persistence provider provides an instance of this interface
  * to the PersistenceUnitInfo.addTransformer method.
  * The supplied transformer instance will get called to transform
  * entity class files when they are loaded and redefined.  The transformation
  * occurs before the class is defined by the JVM
  *
- *
  * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
  * @author Emmanuel Bernard
  */
-public interface ClassTransformer
-{
-   /**
-	* Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation
-	*
-	* @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
-	* @param classname The name of the class being transformed
-	* @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
-	* @param protectionDomain ProtectionDomain of the class being (re)-defined
-	* @param classfileBuffer The input byte buffer in class file format
-	* @return A well-formed class file that can be loaded
-	*/
-   public byte[] transform(ClassLoader loader,
-					String classname,
-					Class classBeingRedefined,
-					ProtectionDomain protectionDomain,
-					byte[] classfileBuffer);
+public interface ClassTransformer {
+	/**
+	 * Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation.
+	 *
+	 * @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
+	 * @param classname The name of the class being transformed
+	 * @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
+	 * @param protectionDomain ProtectionDomain of the class being (re)-defined
+	 * @param classfileBuffer The input byte buffer in class file format
+	 * @return A well-formed class file that can be loaded
+	 */
+	public byte[] transform(
+			ClassLoader loader,
+			String classname,
+			Class classBeingRedefined,
+			ProtectionDomain protectionDomain,
+			byte[] classfileBuffer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java
index 3c9ceb7611..005d4026e5 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java
@@ -1,80 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 import java.util.Set;
 
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Encapsulates bytecode instrumentation information about a particular entity.
  *
  * @author Steve Ebersole
  */
 public interface EntityInstrumentationMetadata {
 	/**
 	 * The name of the entity to which this metadata applies.
 	 *
 	 * @return The entity name
 	 */
 	public String getEntityName();
 
 	/**
-     * Has the entity class been bytecode instrumented?
+	 * Has the entity class been bytecode instrumented?
 	 *
 	 * @return {@code true} indicates the entity class is instrumented for Hibernate use; {@code false}
 	 * indicates it is not
 	 */
-    public boolean isInstrumented();
+	public boolean isInstrumented();
 
-    /**
-     * Build and inject a field interceptor instance into the instrumented entity.
+	/**
+	 * Build and inject a field interceptor instance into the instrumented entity.
 	 *
 	 * @param entity The entity into which built interceptor should be injected
 	 * @param entityName The name of the entity
 	 * @param uninitializedFieldNames The name of fields marked as lazy
 	 * @param session The session to which the entity instance belongs.
 	 *
 	 * @return The built and injected interceptor
 	 *
 	 * @throws NotInstrumentedException Thrown if {@link #isInstrumented()} returns {@code false}
-     */
-    public FieldInterceptor injectInterceptor(
-            Object entity,
-            String entityName,
-            Set uninitializedFieldNames,
-            SessionImplementor session) throws NotInstrumentedException;
+	 */
+	public FieldInterceptor injectInterceptor(
+			Object entity,
+			String entityName,
+			Set uninitializedFieldNames,
+			SessionImplementor session) throws NotInstrumentedException;
 
-    /**
-     * Extract the field interceptor instance from the instrumented entity.
+	/**
+	 * Extract the field interceptor instance from the instrumented entity.
 	 *
 	 * @param entity The entity from which to extract the interceptor
 	 *
 	 * @return The extracted interceptor
 	 *
 	 * @throws NotInstrumentedException Thrown if {@link #isInstrumented()} returns {@code false}
 	 */
-    public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
+	public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
index 033530527b..a293ec9c91 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
@@ -1,75 +1,81 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 import java.io.InputStream;
 
 /**
- * A specialized classloader which performs bytecode enhancement on class
- * definitions as they are loaded into the classloader scope.
+ * A specialized ClassLoader which performs bytecode enhancement on class definitions as they are loaded
+ * into the ClassLoader scope.
  *
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class InstrumentedClassLoader extends ClassLoader {
+	private final ClassTransformer classTransformer;
 
-	private ClassTransformer classTransformer;
-
+	/**
+	 * Constructs an InstrumentedClassLoader.
+	 *
+	 * @param parent The parent ClassLoader
+	 * @param classTransformer The transformer to use for applying enhancement
+	 */
 	public InstrumentedClassLoader(ClassLoader parent, ClassTransformer classTransformer) {
 		super( parent );
 		this.classTransformer = classTransformer;
 	}
 
+	@Override
 	public Class loadClass(String name) throws ClassNotFoundException {
 		if ( name.startsWith( "java." ) || classTransformer == null ) {
 			return getParent().loadClass( name );
 		}
 
-		Class c = findLoadedClass( name );
+		final Class c = findLoadedClass( name );
 		if ( c != null ) {
 			return c;
 		}
 
-		InputStream is = this.getResourceAsStream( name.replace( '.', '/' ) + ".class" );
+		final InputStream is = this.getResourceAsStream( name.replace( '.', '/' ) + ".class" );
 		if ( is == null ) {
 			throw new ClassNotFoundException( name + " not found" );
 		}
 
 		try {
-			byte[] originalBytecode = ByteCodeHelper.readByteCode( is );
-			byte[] transformedBytecode = classTransformer.transform( getParent(), name, null, null, originalBytecode );
+			final byte[] originalBytecode = ByteCodeHelper.readByteCode( is );
+			final byte[] transformedBytecode = classTransformer.transform( getParent(), name, null, null, originalBytecode );
 			if ( originalBytecode == transformedBytecode ) {
 				// no transformations took place, so handle it as we would a
 				// non-instrumented class
 				return getParent().loadClass( name );
 			}
 			else {
 				return defineClass( name, transformedBytecode, 0, transformedBytecode.length );
 			}
 		}
 		catch( Throwable t ) {
 			throw new ClassNotFoundException( name + " not found", t );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/NotInstrumentedException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/NotInstrumentedException.java
index 19e7eb08ce..8bb788b2cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/NotInstrumentedException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/NotInstrumentedException.java
@@ -1,39 +1,43 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 import org.hibernate.HibernateException;
 
 /**
+ * Indicates a condition where an instrumented/enhanced class was expected, but the class was not
+ * instrumented/enhanced.
+ *
  * @author Steve Ebersole
  */
 public class NotInstrumentedException extends HibernateException {
+	/**
+	 * Constructs a NotInstrumentedException
+	 *
+	 * @param message Message explaining the exception condition
+	 */
 	public NotInstrumentedException(String message) {
 		super( message );
 	}
-
-	public NotInstrumentedException(String message, Throwable root) {
-		super( message, root );
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
index 8205ede743..53361b28c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
@@ -1,58 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.bytecode.spi;
 
 /**
  * Represents reflection optimization for a particular class.
  *
  * @author Steve Ebersole
  */
 public interface ReflectionOptimizer {
-
+	/**
+	 * Retrieve the optimizer for calling an entity's constructor via reflection.
+	 *
+	 * @return The optimizer for instantiation
+	 */
 	public InstantiationOptimizer getInstantiationOptimizer();
+
+	/**
+	 * Retrieve the optimizer for accessing the entity's persistent state.
+	 *
+	 * @return The optimizer for persistent state access
+	 */
 	public AccessOptimizer getAccessOptimizer();
 
 	/**
 	 * Represents optimized entity instantiation.
 	 */
 	public static interface InstantiationOptimizer {
 		/**
 		 * Perform instantiation of an instance of the underlying class.
 		 *
 		 * @return The new instance.
 		 */
 		public Object newInstance();
 	}
 
 	/**
 	 * Represents optimized entity property access.
 	 *
 	 * @author Steve Ebersole
 	 */
 	public interface AccessOptimizer {
+		/**
+		 * Get the name of all properties.
+		 *
+		 * @return The name of all properties.
+		 */
 		public String[] getPropertyNames();
+
+		/**
+		 * Get the value of all properties from the given entity
+		 *
+		 * @param object The entity from which to extract values.
+		 *
+		 * @return The values.
+		 */
 		public Object[] getPropertyValues(Object object);
+
+		/**
+		 * Set all property values into an entity instance.
+		 *
+		 * @param object The entity instance
+		 * @param values The values to inject
+		 */
 		public void setPropertyValues(Object object, Object[] values);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/package-info.java
new file mode 100644
index 0000000000..7c085649b2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Package defining bytecode code enhancement (instrumentation) support.
+ */
+package org.hibernate.bytecode.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/CacheException.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheException.java
index 3505e41ec8..83c66cbe09 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/CacheException.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/CacheException.java
@@ -1,45 +1,60 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.cache;
+
 import org.hibernate.HibernateException;
 
 /**
  * Something went wrong in the cache
  */
 public class CacheException extends HibernateException {
-	
-	public CacheException(String s) {
-		super(s);
+	/**
+	 * Constructs a CacheException.
+	 *
+	 * @param message Message explaining the exception condition
+	 */
+	public CacheException(String message) {
+		super( message );
 	}
 
-	public CacheException(String s, Throwable e) {
-		super(s, e);
+	/**
+	 * Constructs a CacheException.
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param cause The underlying cause
+	 */
+	public CacheException(String message, Throwable cause) {
+		super( message, cause );
 	}
-	
-	public CacheException(Throwable e) {
-		super(e);
+
+	/**
+	 * Constructs a CacheException.
+	 *
+	 * @param cause The underlying cause
+	 */
+	public CacheException(Throwable cause) {
+		super( cause );
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/NoCacheRegionFactoryAvailableException.java b/hibernate-core/src/main/java/org/hibernate/cache/NoCacheRegionFactoryAvailableException.java
index a59e7f5956..66492c8d22 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/NoCacheRegionFactoryAvailableException.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/NoCacheRegionFactoryAvailableException.java
@@ -1,41 +1,50 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.cache;
+
 import org.hibernate.cfg.Environment;
 
 /**
- * Implementation of NoCacheRegionFactoryAvailableException.
+ * Indicates a condition where a second-level cache implementation was expected to be to available, but
+ * none was found on the classpath.
  *
  * @author Steve Ebersole
  */
 public class NoCacheRegionFactoryAvailableException extends CacheException {
-	private static final String MSG = "Second-level cache is used in the application, but property "
-			+ Environment.CACHE_REGION_FACTORY + " is not given, please either disable second level cache" +
-			" or set correct region factory class name to property "+Environment.CACHE_REGION_FACTORY+
-			" (and make sure the second level cache provider, hibernate-infinispan, for example, is available in the classpath).";
+	private static final String MSG = String.format(
+			"Second-level cache is used in the application, but property %s is not given; " +
+					"please either disable second level cache or set correct region factory using the %s setting " +
+					"and make sure the second level cache provider (hibernate-infinispan, e.g.) is available on the " +
+					"classpath.",
+			Environment.CACHE_REGION_FACTORY,
+			Environment.CACHE_REGION_FACTORY
+	);
+
+	/**
+	 * Constructs a NoCacheRegionFactoryAvailableException with a standard message.
+	 */
 	public NoCacheRegionFactoryAvailableException() {
 		super( MSG );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
index 8934f2d6f0..e0d3b07130 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
@@ -1,33 +1,35 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache;
 
 /**
+ * Legacy (deprecated) namespace for the RegionFactory contract.
+ *
  * @author Steve Ebersole
  *
  * @deprecated Moved, but still need this definition for ehcache 
  */
 @Deprecated
 public interface RegionFactory extends org.hibernate.cache.spi.RegionFactory {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
index b12222ed06..f24ab650db 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
@@ -1,116 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.type.Type;
 
 /**
  * Allows multiple entity classes / collection roles to be stored in the same cache region. Also allows for composite
  * keys which do not properly implement equals()/hashCode().
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CacheKey implements Serializable {
 	private final Serializable key;
 	private final Type type;
 	private final String entityOrRoleName;
 	private final String tenantId;
 	private final int hashCode;
 
 	/**
 	 * Construct a new key for a collection or entity instance.
 	 * Note that an entity name should always be the root entity
 	 * name, not a subclass entity name.
 	 *
 	 * @param id The identifier associated with the cached data
 	 * @param type The Hibernate type mapping
 	 * @param entityOrRoleName The entity or collection-role name.
 	 * @param tenantId The tenant identifier associated this data.
 	 * @param factory The session factory for which we are caching
 	 */
 	public CacheKey(
 			final Serializable id,
 			final Type type,
 			final String entityOrRoleName,
 			final String tenantId,
 			final SessionFactoryImplementor factory) {
 		this.key = id;
 		this.type = type;
 		this.entityOrRoleName = entityOrRoleName;
 		this.tenantId = tenantId;
 		this.hashCode = calculateHashCode( type, factory );
 	}
 
 	private int calculateHashCode(Type type, SessionFactoryImplementor factory) {
 		int result = type.getHashCode( key, factory );
 		result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
 		return result;
 	}
 
 	public Serializable getKey() {
 		return key;
 	}
 
 	public String getEntityOrRoleName() {
 		return entityOrRoleName;
 	}
 
 	public String getTenantId() {
 		return tenantId;
 	}
 
 	@Override
 	public boolean equals(Object other) {
 		if ( other == null ) {
 			return false;
 		}
 		if ( this == other ) {
 			return true;
 		}
 		if ( hashCode != other.hashCode() || !( other instanceof CacheKey ) ) {
 			//hashCode is part of this check since it is pre-calculated and hash must match for equals to be true
 			return false;
 		}
-		CacheKey that = (CacheKey) other;
-		return EqualsHelper.equals( entityOrRoleName, that.entityOrRoleName ) &&
-				type.isEqual( key, that.key ) &&
-				EqualsHelper.equals( tenantId, that.tenantId );
+		final CacheKey that = (CacheKey) other;
+		return EqualsHelper.equals( entityOrRoleName, that.entityOrRoleName )
+				&& type.isEqual( key, that.key )
+				&& EqualsHelper.equals( tenantId, that.tenantId );
 	}
 
 	@Override
 	public int hashCode() {
 		return hashCode;
 	}
 
 	@Override
 	public String toString() {
 		// Used to be required for OSCache
-		return entityOrRoleName + '#' + key.toString();//"CacheKey#" + type.toString(key, sf);
+		return entityOrRoleName + '#' + key.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
index dd5ecea569..084f8f6de4 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
@@ -1,88 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.Filter;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.type.Type;
 
 /**
  * Allows cached queries to be keyed by enabled filters.
  * 
  * @author Gavin King
  */
 public final class FilterKey implements Serializable {
-	private String filterName;
-	private Map<String,TypedValue> filterParameters = new HashMap<String,TypedValue>();
-	
-	public FilterKey(String name, Map<String,?> params, Map<String,Type> types) {
+	private final String filterName;
+	private final Map<String,TypedValue> filterParameters = new HashMap<String,TypedValue>();
+
+	FilterKey(String name, Map<String,?> params, Map<String,Type> types) {
 		filterName = name;
 		for ( Map.Entry<String, ?> paramEntry : params.entrySet() ) {
-			Type type = types.get( paramEntry.getKey() );
+			final Type type = types.get( paramEntry.getKey() );
 			filterParameters.put( paramEntry.getKey(), new TypedValue( type, paramEntry.getValue() ) );
 		}
 	}
-	
+
+	@Override
 	public int hashCode() {
 		int result = 13;
 		result = 37 * result + filterName.hashCode();
 		result = 37 * result + filterParameters.hashCode();
 		return result;
 	}
-	
+
+	@Override
 	public boolean equals(Object other) {
-		if ( !(other instanceof FilterKey) ) return false;
-		FilterKey that = (FilterKey) other;
-		if ( !that.filterName.equals(filterName) ) return false;
-		if ( !that.filterParameters.equals(filterParameters) ) return false;
-		return true;
+		if ( !(other instanceof FilterKey) ) {
+			return false;
+		}
+
+		final FilterKey that = (FilterKey) other;
+		return that.filterName.equals( filterName )
+				&& that.filterParameters.equals( filterParameters );
 	}
-	
+
+	@Override
 	public String toString() {
 		return "FilterKey[" + filterName + filterParameters + ']';
 	}
-	
+
+	/**
+	 * Constructs a number of FilterKey instances, given the currently enabled filters
+	 *
+	 * @param enabledFilters The currently enabled filters
+	 *
+	 * @return The filter keys, one per enabled filter
+	 */
 	public static Set<FilterKey> createFilterKeys(Map<String,Filter> enabledFilters) {
-		if ( enabledFilters.size()==0 ) {
+		if ( enabledFilters.size() == 0 ) {
 			return null;
 		}
-		Set<FilterKey> result = new HashSet<FilterKey>();
+		final Set<FilterKey> result = new HashSet<FilterKey>();
 		for ( Filter filter : enabledFilters.values() ) {
-			FilterKey key = new FilterKey(
+			final FilterKey key = new FilterKey(
 					filter.getName(),
 					( (FilterImpl) filter ).getParameters(),
 					filter.getFilterDefinition().getParameterTypes()
 			);
 			result.add( key );
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
index 61c1ed9a6c..bd0a7624ea 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
@@ -1,163 +1,163 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.util.Arrays;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Defines a key for caching natural identifier resolutions into the second level cache.
  *
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class NaturalIdCacheKey implements Serializable {
 	private final Serializable[] naturalIdValues;
 	private final String entityName;
 	private final String tenantId;
 	private final int hashCode;
 	private transient ValueHolder<String> toString;
 
 	/**
 	 * Construct a new key for a caching natural identifier resolutions into the second level cache.
 	 * Note that an entity name should always be the root entity name, not a subclass entity name.
 	 *
 	 * @param naturalIdValues The naturalIdValues associated with the cached data
 	 * @param persister The persister for the entity
 	 * @param session The originating session
 	 */
 	public NaturalIdCacheKey(
 			final Object[] naturalIdValues,
 			final EntityPersister persister,
 			final SessionImplementor session) {
 
 		this.entityName = persister.getRootEntityName();
 		this.tenantId = session.getTenantIdentifier();
 
 		this.naturalIdValues = new Serializable[naturalIdValues.length];
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Type[] propertyTypes = persister.getPropertyTypes();
 
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ( ( this.entityName == null ) ? 0 : this.entityName.hashCode() );
 		result = prime * result + ( ( this.tenantId == null ) ? 0 : this.tenantId.hashCode() );
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			final int naturalIdPropertyIndex = naturalIdPropertyIndexes[i];
-            final Type type = propertyTypes[naturalIdPropertyIndex];
+			final Type type = propertyTypes[naturalIdPropertyIndex];
 			final Object value = naturalIdValues[i];
-			
+
 			result = prime * result + (value != null ? type.getHashCode( value, factory ) : 0);
-			
+
 			this.naturalIdValues[i] = type.disassemble( value, session, null );
 		}
-		
+
 		this.hashCode = result;
 		initTransients();
 	}
-	
+
 	private void initTransients() {
-	    this.toString = new ValueHolder<String>(
-                new ValueHolder.DeferredInitializer<String>() {
-                    @Override
-                    public String initialize() {
-                        //Complex toString is needed as naturalIds for entities are not simply based on a single value like primary keys
-                        //the only same way to differentiate the keys is to included the disassembled values in the string.
-                        final StringBuilder toStringBuilder = new StringBuilder( entityName ).append( "##NaturalId[" );
-                        for ( int i = 0; i < naturalIdValues.length; i++ ) {
-                            toStringBuilder.append( naturalIdValues[i] );
-                            if ( i + 1 < naturalIdValues.length ) {
-                                toStringBuilder.append( ", " );
-                            }
-                        }
-                        toStringBuilder.append( "]" );
-
-                        return toStringBuilder.toString();
-                    }
-                }
-        );
+		this.toString = new ValueHolder<String>(
+				new ValueHolder.DeferredInitializer<String>() {
+					@Override
+					public String initialize() {
+						//Complex toString is needed as naturalIds for entities are not simply based on a single value like primary keys
+						//the only same way to differentiate the keys is to included the disassembled values in the string.
+						final StringBuilder toStringBuilder = new StringBuilder( entityName ).append( "##NaturalId[" );
+						for ( int i = 0; i < naturalIdValues.length; i++ ) {
+							toStringBuilder.append( naturalIdValues[i] );
+							if ( i + 1 < naturalIdValues.length ) {
+								toStringBuilder.append( ", " );
+							}
+						}
+						toStringBuilder.append( "]" );
+
+						return toStringBuilder.toString();
+					}
+				}
+		);
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getTenantId() {
 		return tenantId;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public Serializable[] getNaturalIdValues() {
 		return naturalIdValues;
 	}
 
 	@Override
 	public String toString() {
 		return toString.getValue();
 	}
-	
+
 	@Override
 	public int hashCode() {
 		return this.hashCode;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( o == null ) {
 			return false;
 		}
 		if ( this == o ) {
 			return true;
 		}
 
 		if ( hashCode != o.hashCode() || !( o instanceof NaturalIdCacheKey ) ) {
 			//hashCode is part of this check since it is pre-calculated and hash must match for equals to be true
 			return false;
 		}
 
 		final NaturalIdCacheKey other = (NaturalIdCacheKey) o;
 		return EqualsHelper.equals( entityName, other.entityName )
 				&& EqualsHelper.equals( tenantId, other.tenantId )
 				&& Arrays.deepEquals( this.naturalIdValues, other.naturalIdValues );
 	}
-	
-    private void readObject(ObjectInputStream ois)
-            throws ClassNotFoundException, IOException {
-        ois.defaultReadObject();
-        initTransients();
-    }
+
+	private void readObject(ObjectInputStream ois)
+			throws ClassNotFoundException, IOException {
+		ois.defaultReadObject();
+		initTransients();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java
index a0d7cfdce7..ebbbc4e719 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java
@@ -1,54 +1,92 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Defines the contract for caches capable of storing query results.  These
  * caches should only concern themselves with storing the matching result ids.
  * The transactional semantics are necessarily less strict than the semantics
  * of an item cache.
  * 
  * @author Gavin King
  */
 public interface QueryCache {
-
+	/**
+	 * Clear items from the query cache.
+	 *
+	 * @throws CacheException Indicates a problem delegating to the underlying cache.
+	 */
 	public void clear() throws CacheException;
-	
+
+	/**
+	 * Put a result into the query cache.
+	 *
+	 * @param key The cache key
+	 * @param returnTypes The result types
+	 * @param result The results to cache
+	 * @param isNaturalKeyLookup Was this a natural id lookup?
+	 * @param session The originating session
+	 *
+	 * @return Whether the put actually happened.
+	 *
+	 * @throws HibernateException Indicates a problem delegating to the underlying cache.
+	 */
 	public boolean put(QueryKey key, Type[] returnTypes, List result, boolean isNaturalKeyLookup, SessionImplementor session) throws HibernateException;
 
+	/**
+	 * Get results from the cache.
+	 *
+	 * @param key The cache key
+	 * @param returnTypes The result types
+	 * @param isNaturalKeyLookup Was this a natural id lookup?
+	 * @param spaces The query spaces (used in invalidation plus validation checks)
+	 * @param session The originating session
+	 *
+	 * @return The cached results; may be null.
+	 *
+	 * @throws HibernateException Indicates a problem delegating to the underlying cache.
+	 */
 	public List get(QueryKey key, Type[] returnTypes, boolean isNaturalKeyLookup, Set spaces, SessionImplementor session) throws HibernateException;
 
+	/**
+	 * Destroy the cache.
+	 */
 	public void destroy();
 
+	/**
+	 * The underlying cache factory region being used.
+	 *
+	 * @return The cache region.
+	 */
 	public QueryResultsRegion getRegion();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCacheFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCacheFactory.java
index 4e83677995..5b61ee39da 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCacheFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCacheFactory.java
@@ -1,43 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.util.Properties;
 
-import org.hibernate.HibernateException;
 import org.hibernate.cfg.Settings;
 
 /**
  * Defines a factory for query cache instances.  These factories are responsible for
  * creating individual QueryCache instances.
  *
  * @author Steve Ebersole
  */
 public interface QueryCacheFactory {
+	/**
+	 * Builds a named query cache.
+	 *
+	 * @param regionName The cache region name
+	 * @param updateTimestampsCache The cache of timestamp values to use to perform up-to-date checks.
+	 * @param settings The Hibernate SessionFactory settings.
+	 * @param props Any properties.
+	 *
+	 * @return The cache.
+	 */
 	public QueryCache getQueryCache(
-	        String regionName,
-	        UpdateTimestampsCache updateTimestampsCache,
+			String regionName,
+			UpdateTimestampsCache updateTimestampsCache,
 			Settings settings,
-	        Properties props) throws HibernateException;
+			Properties props);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
index c1fc20395d..b00d885ac2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
@@ -1,299 +1,299 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A key that identifies a particular query with bound parameter values.  This is the object Hibernate uses
  * as its key into its query cache.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class QueryKey implements Serializable {
 	private final String sqlQueryString;
 	private final Type[] positionalParameterTypes;
 	private final Object[] positionalParameterValues;
 	private final Map namedParameters;
 	private final Integer firstRow;
 	private final Integer maxRows;
 	private final String tenantIdentifier;
 	private final Set filterKeys;
 
 	// the explicit user-provided result transformer, not the one used with "select new". Here to avoid mangling
 	// transformed/non-transformed results.
 	private final CacheableResultTransformer customTransformer;
 
 	/**
 	 * For performance reasons, the hashCode is cached; however, it is marked transient so that it can be
 	 * recalculated as part of the serialization process which allows distributed query caches to work properly.
 	 */
 	private transient int hashCode;
 
 	/**
 	 * Generates a QueryKey.
 	 *
 	 * @param queryString The sql query string.
 	 * @param queryParameters The query parameters
 	 * @param filterKeys The keys of any enabled filters.
 	 * @param session The current session.
-	 * @param customTransformer The result transformer; should be
-	 *            null if data is not transformed before being cached.
+	 * @param customTransformer The result transformer; should be null if data is not transformed before being cached.
 	 *
 	 * @return The generate query cache key.
 	 */
 	public static QueryKey generateQueryKey(
 			String queryString,
 			QueryParameters queryParameters,
 			Set filterKeys,
 			SessionImplementor session,
 			CacheableResultTransformer customTransformer) {
 		// disassemble positional parameters
 		final int positionalParameterCount = queryParameters.getPositionalParameterTypes().length;
 		final Type[] types = new Type[positionalParameterCount];
 		final Object[] values = new Object[positionalParameterCount];
 		for ( int i = 0; i < positionalParameterCount; i++ ) {
 			types[i] = queryParameters.getPositionalParameterTypes()[i];
 			values[i] = types[i].disassemble( queryParameters.getPositionalParameterValues()[i], session, null );
 		}
 
 		// disassemble named parameters
 		final Map<String,TypedValue> namedParameters;
 		if ( queryParameters.getNamedParameters() == null ) {
 			namedParameters = null;
 		}
 		else {
 			namedParameters = CollectionHelper.mapOfSize( queryParameters.getNamedParameters().size() );
 			for ( Map.Entry<String,TypedValue> namedParameterEntry : queryParameters.getNamedParameters().entrySet() ) {
 				namedParameters.put(
 						namedParameterEntry.getKey(),
 						new TypedValue(
 								namedParameterEntry.getValue().getType(),
 								namedParameterEntry.getValue().getType().disassemble(
 										namedParameterEntry.getValue().getValue(),
 										session,
 										null
 								)
 						)
 				);
 			}
 		}
 
 		// decode row selection...
 		final RowSelection selection = queryParameters.getRowSelection();
 		final Integer firstRow;
 		final Integer maxRows;
 		if ( selection != null ) {
 			firstRow = selection.getFirstRow();
 			maxRows = selection.getMaxRows();
 		}
 		else {
 			firstRow = null;
 			maxRows = null;
 		}
 
 		return new QueryKey(
 				queryString,
 				types,
 				values,
 				namedParameters,
 				firstRow,
 				maxRows,
 				filterKeys,
 				session.getTenantIdentifier(),
 				customTransformer
 		);
 	}
 
 	/**
 	 * Package-protected constructor.
 	 *
 	 * @param sqlQueryString The sql query string.
 	 * @param positionalParameterTypes Positional parameter types.
 	 * @param positionalParameterValues Positional parameter values.
 	 * @param namedParameters Named parameters.
 	 * @param firstRow First row selection, if any.
 	 * @param maxRows Max-rows selection, if any.
 	 * @param filterKeys Enabled filter keys, if any.
 	 * @param customTransformer Custom result transformer, if one.
 	 * @param tenantIdentifier The tenant identifier in effect for this query, or {@code null}
 	 */
 	QueryKey(
 			String sqlQueryString,
 			Type[] positionalParameterTypes,
 			Object[] positionalParameterValues,
 			Map namedParameters,
 			Integer firstRow,
 			Integer maxRows,
 			Set filterKeys,
 			String tenantIdentifier,
 			CacheableResultTransformer customTransformer) {
 		this.sqlQueryString = sqlQueryString;
 		this.positionalParameterTypes = positionalParameterTypes;
 		this.positionalParameterValues = positionalParameterValues;
 		this.namedParameters = namedParameters;
 		this.firstRow = firstRow;
 		this.maxRows = maxRows;
 		this.tenantIdentifier = tenantIdentifier;
 		this.filterKeys = filterKeys;
 		this.customTransformer = customTransformer;
 		this.hashCode = generateHashCode();
 	}
 
 	/**
 	 * Provides access to the explicitly user-provided result transformer.
 	 *
 	 * @return The result transformer.
 	 */
 	public CacheableResultTransformer getResultTransformer() {
 		return customTransformer;
 	}
 
 	/**
 	 * Provide (unmodifiable) access to the named parameters that are part of this query.
 	 *
 	 * @return The (unmodifiable) map of named parameters
 	 */
 	@SuppressWarnings("unchecked")
 	public Map getNamedParameters() {
 		return Collections.unmodifiableMap( namedParameters );
 	}
 
 	/**
 	 * Deserialization hook used to re-init the cached hashcode which is needed for proper clustering support.
 	 *
 	 * @param in The object input stream.
 	 *
 	 * @throws IOException Thrown by normal deserialization
 	 * @throws ClassNotFoundException Thrown by normal deserialization
 	 */
 	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
 		in.defaultReadObject();
 		this.hashCode = generateHashCode();
 	}
 
 	private int generateHashCode() {
 		int result = 13;
 		result = 37 * result + ( firstRow==null ? 0 : firstRow.hashCode() );
 		result = 37 * result + ( maxRows==null ? 0 : maxRows.hashCode() );
 		for ( int i=0; i< positionalParameterValues.length; i++ ) {
 			result = 37 * result + ( positionalParameterValues[i]==null ? 0 : positionalParameterTypes[i].getHashCode( positionalParameterValues[i] ) );
 		}
 		result = 37 * result + ( namedParameters==null ? 0 : namedParameters.hashCode() );
 		result = 37 * result + ( filterKeys ==null ? 0 : filterKeys.hashCode() );
 		result = 37 * result + ( customTransformer==null ? 0 : customTransformer.hashCode() );
 		result = 37 * result + ( tenantIdentifier==null ? 0 : tenantIdentifier.hashCode() );
 		result = 37 * result + sqlQueryString.hashCode();
 		return result;
 	}
 
 	@Override
-    public boolean equals(Object other) {
+	public boolean equals(Object other) {
 		if ( !( other instanceof QueryKey ) ) {
 			return false;
 		}
-		QueryKey that = ( QueryKey ) other;
+
+		final QueryKey that = (QueryKey) other;
 		if ( !sqlQueryString.equals( that.sqlQueryString ) ) {
 			return false;
 		}
 		if ( !EqualsHelper.equals( firstRow, that.firstRow ) || !EqualsHelper.equals( maxRows, that.maxRows ) ) {
 			return false;
 		}
 		if ( !EqualsHelper.equals( customTransformer, that.customTransformer ) ) {
 			return false;
 		}
 		if ( positionalParameterTypes == null ) {
 			if ( that.positionalParameterTypes != null ) {
 				return false;
 			}
 		}
 		else {
 			if ( that.positionalParameterTypes == null ) {
 				return false;
 			}
 			if ( positionalParameterTypes.length != that.positionalParameterTypes.length ) {
 				return false;
 			}
 			for ( int i = 0; i < positionalParameterTypes.length; i++ ) {
 				if ( positionalParameterTypes[i].getReturnedClass() != that.positionalParameterTypes[i].getReturnedClass() ) {
 					return false;
 				}
 				if ( !positionalParameterTypes[i].isEqual( positionalParameterValues[i], that.positionalParameterValues[i] ) ) {
 					return false;
 				}
 			}
 		}
 
 		return EqualsHelper.equals( filterKeys, that.filterKeys )
 				&& EqualsHelper.equals( namedParameters, that.namedParameters )
 				&& EqualsHelper.equals( tenantIdentifier, that.tenantIdentifier );
 	}
 
 	@Override
-    public int hashCode() {
+	public int hashCode() {
 		return hashCode;
 	}
 
 	@Override
-    public String toString() {
-		StringBuilder buffer = new StringBuilder( "sql: " ).append( sqlQueryString );
+	public String toString() {
+		final StringBuilder buffer = new StringBuilder( "sql: " ).append( sqlQueryString );
 		if ( positionalParameterValues != null ) {
 			buffer.append( "; parameters: " );
 			for ( Object positionalParameterValue : positionalParameterValues ) {
 				buffer.append( positionalParameterValue ).append( ", " );
 			}
 		}
 		if ( namedParameters != null ) {
 			buffer.append( "; named parameters: " ).append( namedParameters );
 		}
 		if ( filterKeys != null ) {
 			buffer.append( "; filterKeys: " ).append( filterKeys );
 		}
 		if ( firstRow != null ) {
 			buffer.append( "; first row: " ).append( firstRow );
 		}
 		if ( maxRows != null ) {
 			buffer.append( "; max rows: " ).append( maxRows );
 		}
 		if ( customTransformer != null ) {
 			buffer.append( "; transformer: " ).append( customTransformer );
 		}
 		return buffer.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/Region.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/Region.java
index f409f627e7..61ba0743b1 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/Region.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/Region.java
@@ -1,103 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.util.Map;
 
 import org.hibernate.cache.CacheException;
 
 /**
  * Defines a contract for accessing a particular named region within the 
  * underlying cache implementation.
  *
  * @author Steve Ebersole
  */
 public interface Region {
 	/**
 	 * Retrieve the name of this region.
 	 *
 	 * @return The region name
 	 */
 	public String getName();
 
 	/**
 	 * The "end state" contract of the region's lifecycle.  Called
 	 * during {@link org.hibernate.SessionFactory#close()} to give
 	 * the region a chance to cleanup.
 	 *
 	 * @throws org.hibernate.cache.CacheException Indicates problem shutting down
 	 */
 	public void destroy() throws CacheException;
 
 	/**
 	 * Determine whether this region contains data for the given key.
 	 * <p/>
 	 * The semantic here is whether the cache contains data visible for the
 	 * current call context.  This should be viewed as a "best effort", meaning
 	 * blocking should be avoid if possible.
 	 *
 	 * @param key The cache key
 	 *
 	 * @return True if the underlying cache contains corresponding data; false
 	 * otherwise.
 	 */
 	public boolean contains(Object key);
 
 	/**
 	 * The number of bytes is this cache region currently consuming in memory.
 	 *
 	 * @return The number of bytes consumed by this region; -1 if unknown or
 	 * unsupported.
 	 */
 	public long getSizeInMemory();
 
 	/**
 	 * The count of entries currently contained in the regions in-memory store.
 	 *
 	 * @return The count of entries in memory; -1 if unknown or unsupported.
 	 */
 	public long getElementCountInMemory();
 
 	/**
 	 * The count of entries currently contained in the regions disk store.
 	 *
 	 * @return The count of entries on disk; -1 if unknown or unsupported.
 	 */
 	public long getElementCountOnDisk();
 
 	/**
 	 * Get the contents of this region as a map.
 	 * <p/>
 	 * Implementors which do not support this notion
 	 * should simply return an empty map.
 	 *
 	 * @return The content map.
 	 */
 	public Map toMap();
 
+	/**
+	 * Get the next timestamp according to the underlying cache implementor.
+	 *
+	 * @todo Document the usages of this method so providers know exactly what is expected.
+	 *
+	 * @return The next timestamp
+	 */
 	public long nextTimestamp();
 
-	//we really should change this return type to `long` instead of `int`
+	/**
+	 * Get a timeout value.
+	 *
+	 * @todo Again, document the usages of this method so providers know exactly what is expected.
+	 *
+	 * @return The time out value
+	 */
 	public int getTimeout();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
index 65f0b217c5..9d7d97f06e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
@@ -1,160 +1,160 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 import org.hibernate.service.Service;
 
 /**
  * Contract for building second level cache regions.
  * <p/>
  * Implementors should define a constructor in one of two forms:<ul>
  * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
  * <li>MyRegionFactoryImpl()</li>
  * </ul>
  * Use the first when we need to read config properties prior to
  * {@link #start(Settings, Properties)} being called.
  *
  * @author Steve Ebersole
  */
 public interface RegionFactory extends Service {
 
 	/**
 	 * Lifecycle callback to perform any necessary initialization of the
 	 * underlying cache implementation(s).  Called exactly once during the
 	 * construction of a {@link org.hibernate.internal.SessionFactoryImpl}.
 	 *
 	 * @param settings The settings in effect.
 	 * @param properties The defined cfg properties
 	 *
 	 * @throws org.hibernate.cache.CacheException Indicates problems starting the L2 cache impl;
 	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
 	 * building.
 	 */
 	public void start(Settings settings, Properties properties) throws CacheException;
 
 	/**
 	 * Lifecycle callback to perform any necessary cleanup of the underlying
 	 * cache implementation(s).  Called exactly once during
 	 * {@link org.hibernate.SessionFactory#close}.
 	 */
 	public void stop();
 
 	/**
 	 * By default should we perform "minimal puts" when using this second
 	 * level cache implementation?
 	 *
 	 * @return True if "minimal puts" should be performed by default; false
 	 *         otherwise.
 	 */
 	public boolean isMinimalPutsEnabledByDefault();
 
 	/**
 	 * Get the default access type for {@link EntityRegion entity} and
 	 * {@link CollectionRegion collection} regions.
 	 *
 	 * @return This factory's default access type.
 	 */
 	public AccessType getDefaultAccessType();
 
 	/**
 	 * Generate a timestamp.
 	 * <p/>
 	 * This is generally used for cache content locking/unlocking purposes
 	 * depending upon the access-strategy being used.
 	 *
 	 * @return The generated timestamp.
 	 */
 	public long nextTimestamp();
 
 	/**
 	 * Build a cache region specialized for storing entity data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing NaturalId to Primary Key mappings.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing collection data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException;
 
 	/**
-	 * Build a cache region specialized for storing query results
+	 * Build a cache region specialized for storing query results.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing update-timestamps data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalDataRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalDataRegion.java
index 17380f04b1..da12232caa 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalDataRegion.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalDataRegion.java
@@ -1,54 +1,60 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 /**
  * Defines contract for regions which hold transactionally-managed data.
  * <p/>
  * The data is not transactionally managed within the region; merely it is
  * transactionally-managed in relation to its association with a particular
  * {@link org.hibernate.Session}.
  *
  * @author Steve Ebersole
  */
 public interface TransactionalDataRegion extends Region {
 	/**
 	 * Is the underlying cache implementation aware of (and "participating in")
 	 * ongoing JTA transactions?
 	 * <p/>
 	 * Regions which report that they are transaction-aware are considered
 	 * "synchronous", in that we assume we can immediately (i.e. synchronously)
 	 * write the changes to the cache and that the cache will properly manage
 	 * application of the written changes within the bounds of ongoing JTA
 	 * transactions.  Conversely, regions reporting false are considered
 	 * "asynchronous", where it is assumed that changes must be manually
 	 * delayed by Hibernate until we are certain that the current transaction
 	 * is successful (i.e. maintaining READ_COMMITTED isolation).
 	 *
 	 * @return True if transaction aware; false otherwise.
 	 */
 	public boolean isTransactionAware();
 
+	/**
+	 * Get the description of the type of data to be stored here, which would have been given to the RegionFactory
+	 * when creating this region
+	 *
+	 * @return The data descriptor.
+	 */
 	public CacheDataDescription getCacheDataDescription();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
index 6a72c0ac3a..59d7c637cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
@@ -1,167 +1,223 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Tracks the timestamps of the most recent updates to particular tables. It is
  * important that the cache timeout of the underlying cache implementation be set
  * to a higher value than the timeouts of any of the query caches. In fact, we
  * recommend that the the underlying cache not be configured for expiry at all.
  * Note, in particular, that an LRU cache expiry policy is never appropriate.
  *
  * @author Gavin King
  * @author Mikheil Kapanadze
  */
 public class UpdateTimestampsCache {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, UpdateTimestampsCache.class.getName() );
 
+	/**
+	 * The region name of the update-timestamps cache.
+	 */
 	public static final String REGION_NAME = UpdateTimestampsCache.class.getName();
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, UpdateTimestampsCache.class.getName() );
+
 
 	private final SessionFactoryImplementor factory;
 	private final TimestampsRegion region;
 
-	public UpdateTimestampsCache(Settings settings, Properties props, final SessionFactoryImplementor factory) throws HibernateException {
+	/**
+	 * Constructs an UpdateTimestampsCache.
+	 *
+	 * @param settings The SessionFactory settings
+	 * @param props Any properties
+	 * @param factory The SessionFactory
+	 */
+	public UpdateTimestampsCache(Settings settings, Properties props, final SessionFactoryImplementor factory) {
 		this.factory = factory;
 		final String prefix = settings.getCacheRegionPrefix();
 		final String regionName = prefix == null ? REGION_NAME : prefix + '.' + REGION_NAME;
 
 		LOG.startingUpdateTimestampsCache( regionName );
 		this.region = settings.getRegionFactory().buildTimestampsRegion( regionName, props );
 	}
 
-    @SuppressWarnings({"UnusedDeclaration"})
-    public UpdateTimestampsCache(Settings settings, Properties props) throws HibernateException {
-        this( settings, props, null );
-    }
+	/**
+	 * Constructs an UpdateTimestampsCache.
+	 *
+	 * @param settings The SessionFactory settings
+	 * @param props Any properties
+	 */
+	@SuppressWarnings({"UnusedDeclaration"})
+	public UpdateTimestampsCache(Settings settings, Properties props) {
+		this( settings, props, null );
+	}
 
+	/**
+	 * Perform pre-invalidation.
+	 *
+	 * @param spaces The spaces to pre-invalidate
+	 *
+	 * @throws CacheException Indicated problem delegating to underlying region.
+	 */
 	@SuppressWarnings({"UnnecessaryBoxing"})
 	public void preinvalidate(Serializable[] spaces) throws CacheException {
 		final boolean debug = LOG.isDebugEnabled();
 		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
 		final Long ts = region.nextTimestamp() + region.getTimeout();
 
 		for ( Serializable space : spaces ) {
 			if ( debug ) {
 				LOG.debugf( "Pre-invalidating space [%s], timestamp: %s", space, ts );
 			}
 			//put() has nowait semantics, is this really appropriate?
 			//note that it needs to be async replication, never local or sync
 			region.put( space, ts );
 			if ( stats ) {
 				factory.getStatisticsImplementor().updateTimestampsCachePut();
 			}
 		}
 	}
 
+	/**
+	 * Perform invalidation.
+	 *
+	 * @param spaces The spaces to pre-invalidate
+	 *
+	 * @throws CacheException Indicated problem delegating to underlying region.
+	 */
 	@SuppressWarnings({"UnnecessaryBoxing"})
 	public void invalidate(Serializable[] spaces) throws CacheException {
-		 final boolean debug = LOG.isDebugEnabled();
-		 final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
+		final boolean debug = LOG.isDebugEnabled();
+		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
-		 final Long ts = region.nextTimestamp();
+		final Long ts = region.nextTimestamp();
 
 		for (Serializable space : spaces) {
 			if ( debug ) {
 				LOG.debugf( "Invalidating space [%s], timestamp: %s", space, ts );
 			}
 			//put() has nowait semantics, is this really appropriate?
 			//note that it needs to be async replication, never local or sync
 			region.put( space, ts );
 			if ( stats ) {
 				factory.getStatisticsImplementor().updateTimestampsCachePut();
 			}
 		}
 	}
 
+	/**
+	 * Perform an up-to-date check for the given set of query spaces.
+	 *
+	 * @param spaces The spaces to check
+	 * @param timestamp The timestamp against which to check.
+	 *
+	 * @return Whether all those spaces are up-to-date
+	 *
+	 * @throws CacheException Indicated problem delegating to underlying region.
+	 */
 	@SuppressWarnings({"unchecked", "UnnecessaryUnboxing"})
-	public boolean isUpToDate(Set spaces, Long timestamp) throws HibernateException {
+	public boolean isUpToDate(Set spaces, Long timestamp) throws CacheException {
 		final boolean debug = LOG.isDebugEnabled();
 		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
 		for ( Serializable space : (Set<Serializable>) spaces ) {
-			Long lastUpdate = (Long) region.get( space );
+			final Long lastUpdate = (Long) region.get( space );
 			if ( lastUpdate == null ) {
 				if ( stats ) {
 					factory.getStatisticsImplementor().updateTimestampsCacheMiss();
 				}
 				//the last update timestamp was lost from the cache
 				//(or there were no updates since startup!)
 				//updateTimestamps.put( space, new Long( updateTimestamps.nextTimestamp() ) );
 				//result = false; // safer
 			}
 			else {
 				if ( debug ) {
 					LOG.debugf(
 							"[%s] last update timestamp: %s",
 							space,
 							lastUpdate + ", result set timestamp: " + timestamp
 					);
 				}
 				if ( stats ) {
 					factory.getStatisticsImplementor().updateTimestampsCacheHit();
 				}
 				if ( lastUpdate >= timestamp ) {
 					return false;
 				}
 			}
 		}
 		return true;
 	}
 
+	/**
+	 * Clear the update-timestamps data.
+	 *
+	 * @throws CacheException Indicates problem delegating call to underlying region.
+	 */
 	public void clear() throws CacheException {
 		region.evictAll();
 	}
 
+	/**
+	 * Destroys the cache.
+	 *
+	 * @throws CacheException Indicates problem delegating call to underlying region.
+	 */
 	public void destroy() {
 		try {
 			region.destroy();
 		}
 		catch (Exception e) {
 			LOG.unableToDestroyUpdateTimestampsCache( region.getName(), e.getMessage() );
 		}
 	}
 
+	/**
+	 * Get the underlying cache region where data is stored..
+	 *
+	 * @return The underlying region.
+	 */
 	public TimestampsRegion getRegion() {
 		return region;
 	}
 
 	@Override
-    public String toString() {
+	public String toString() {
 		return "UpdateTimestampsCache";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/AccessType.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/AccessType.java
index 336a6295f6..ff7aa633a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/AccessType.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/AccessType.java
@@ -1,62 +1,93 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.access;
 
 /**
  * The types of access strategies available.
  *
  * @author Steve Ebersole
  */
 public enum AccessType {
+	/**
+	 * Read-only access.  Data may be added and removed, but not mutated.
+	 */
 	READ_ONLY( "read-only" ),
+	/**
+	 * Read and write access (strict).  Data may be added, removed and mutated.
+	 */
 	READ_WRITE( "read-write" ),
+	/**
+	 * Read and write access (non-strict).  Data may be added, removed and mutated.  The non-strictness comes from
+	 * the fact that locks are not maintained as tightly as in {@link #READ_WRITE}, which leads to better throughput
+	 * but may also lead to inconsistencies.
+	 */
 	NONSTRICT_READ_WRITE( "nonstrict-read-write" ),
+	/**
+	 * A read and write strategy where isolation/locking is maintained in conjunction with a JTA transaction.
+	 */
 	TRANSACTIONAL( "transactional" );
 
 	private final String externalName;
 
 	private AccessType(String externalName) {
 		this.externalName = externalName;
 	}
 
+	/**
+	 * Get the corresponding externalized name for this value.
+	 *
+	 * @return The corresponding externalized name.
+	 */
 	public String getExternalName() {
 		return externalName;
 	}
 
+	@Override
 	public String toString() {
 		return "AccessType[" + externalName + "]";
 	}
 
+	/**
+	 * Resolve an AccessType from its external name.
+	 *
+	 * @param externalName The external representation to resolve
+	 *
+	 * @return The access type.
+	 *
+	 * @throws UnknownAccessTypeException If the externalName was not recognized.
+	 *
+	 * @see #getExternalName()
+	 */
 	public static AccessType fromExternalName(String externalName) {
 		if ( externalName == null ) {
 			return null;
 		}
 		for ( AccessType accessType : AccessType.values() ) {
 			if ( accessType.getExternalName().equals( externalName ) ) {
 				return accessType;
 			}
 		}
 		throw new UnknownAccessTypeException( externalName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
index dee0edcfe5..2f4988b82b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
@@ -1,153 +1,155 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.access;
 
 import org.hibernate.cache.CacheException;
 
 /**
+ * Base access strategy for all regions.
+ *
  * @author Gail Badner
  */
 public interface RegionAccessStrategy {
 	/**
 	 * Attempt to retrieve an object from the cache. Mainly used in attempting
 	 * to resolve entities/collections from the second level cache.
 	 *
 	 * @param key The key of the item to be retrieved.
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @return the cached object or <tt>null</tt>
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	Object get(Object key, long txTimestamp) throws CacheException;
 
 	/**
 	 * Attempt to cache an object, after loading from the database.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @param version the item version number
 	 * @return <tt>true</tt> if the object was successfully cached
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	boolean putFromLoad(
 			Object key,
 			Object value,
 			long txTimestamp,
 			Object version) throws CacheException;
 
 	/**
 	 * Attempt to cache an object, after loading from the database, explicitly
 	 * specifying the minimalPut behavior.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @param version the item version number
 	 * @param minimalPutOverride Explicit minimalPut flag
 	 * @return <tt>true</tt> if the object was successfully cached
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	boolean putFromLoad(
 			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride) throws CacheException;
 
 	/**
 	 * We are going to attempt to update/delete the keyed object. This
 	 * method is used by "asynchronous" concurrency strategies.
 	 * <p/>
 	 * The returned object must be passed back to {@link #unlockItem}, to release the
 	 * lock. Concurrency strategies which do not support client-visible
 	 * locks may silently return null.
 	 *
 	 * @param key The key of the item to lock
 	 * @param version The item's current version value
 	 * @return A representation of our lock on the item; or null.
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	SoftLock lockItem(Object key, Object version) throws CacheException;
 
 	/**
 	 * Lock the entire region
 	 *
 	 * @return A representation of our lock on the item; or null.
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	SoftLock lockRegion() throws CacheException;
 
 	/**
 	 * Called when we have finished the attempted update/delete (which may or
 	 * may not have been successful), after transaction completion.  This method
 	 * is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param lock The lock previously obtained from {@link #lockItem}
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void unlockItem(Object key, SoftLock lock) throws CacheException;
 
 	/**
 	 * Called after we have finished the attempted invalidation of the entire
 	 * region
 	 *
 	 * @param lock The lock previously obtained from {@link #lockRegion}
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void unlockRegion(SoftLock lock) throws CacheException;
 
 	/**
 	 * Called after an item has become stale (before the transaction completes).
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void remove(Object key) throws CacheException;
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void removeAll() throws CacheException;
 
 	/**
 	 * Forcibly evict an item from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void evict(Object key) throws CacheException;
 
 	/**
 	 * Forcibly evict all items from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void evictAll() throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/UnknownAccessTypeException.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/UnknownAccessTypeException.java
index 26753d2d5b..53e05fd5df 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/UnknownAccessTypeException.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/UnknownAccessTypeException.java
@@ -1,35 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.access;
 
 import org.hibernate.HibernateException;
 
 /**
+ * Indicates that an unknown AccessType external name was encountered
+ *
  * @author Steve Ebersole
+ *
+ * @see AccessType#fromExternalName(String)
  */
 public class UnknownAccessTypeException extends HibernateException {
+	/**
+	 * Constructs the UnknownAccessTypeException.
+	 *
+	 * @param accessTypeName The external name that could not be resolved.
+	 */
 	public UnknownAccessTypeException(String accessTypeName) {
 		super( "Unknown access type [" + accessTypeName + "]" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
index 63d847563a..d73d3a67f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
@@ -1,70 +1,75 @@
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 
-import org.hibernate.Interceptor;
-import org.hibernate.event.spi.EventSource;
-import org.hibernate.persister.entity.EntityPersister;
-
 /**
  * A cached instance of a persistent class
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface CacheEntry extends Serializable {
+	/**
+	 * Does this entry represent a direct entity reference (rather than disassembled state)?
+	 *
+	 * @return true/false
+	 */
 	public boolean isReferenceEntry();
 
 	/**
 	 * Hibernate stores all entries pertaining to a given entity hierarchy in a single region.  This attribute
 	 * tells us the specific entity type represented by the cached data.
 	 *
 	 * @return The entry's exact entity type.
 	 */
 	public String getSubclass();
 
 	/**
 	 * Retrieves the version (optimistic locking) associated with this cache entry.
 	 *
 	 * @return The version of the entity represented by this entry
 	 */
 	public Object getVersion();
 
+	/**
+	 * Does the represented data contain any un-fetched attribute values?
+	 *
+	 * @return true/false
+	 */
 	public boolean areLazyPropertiesUnfetched();
 
-
-	// todo: this was added to support initializing an entity's EntityEntry snapshot during reattach;
-	// this should be refactored to instead expose a method to assemble a EntityEntry based on this
-	// state for return.
+	/**
+	 * Get the underlying disassembled state
+	 *
+	 * todo : this was added to support initializing an entity's EntityEntry snapshot during reattach;
+	 * this should be refactored to instead expose a method to assemble a EntityEntry based on this
+	 * state for return.
+	 *
+	 * @return The disassembled state
+	 */
 	public Serializable[] getDisassembledState();
 
 }
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java
index 5bc5414846..0fac0bb25c 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java
@@ -1,34 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
+ * Strategy for how cache entries are "structured" for storing into the cache.
+ *
  * @author Gavin King
  */
 public interface CacheEntryStructure {
+	/**
+	 * Convert the cache item into its "structured" form.  Perfectly valid to return the item as-is.
+	 *
+	 * @param item The item to structure.
+	 *
+	 * @return The structured form.
+	 */
 	public Object structure(Object item);
-	public Object destructure(Object map, SessionFactoryImplementor factory);
+
+	/**
+	 * Convert the previous structured form of the item back into its item form.
+	 *
+	 * @param structured The structured form.
+	 * @param factory The session factory.
+	 *
+	 * @return The item
+	 */
+	public Object destructure(Object structured, SessionFactoryImplementor factory);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
index 2b8bb3661c..8ceab7f0b4 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
@@ -1,65 +1,84 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
+ * Cacheable representation of persistent collections
+ *
  * @author Gavin King
  */
 public class CollectionCacheEntry implements Serializable {
-
 	private final Serializable state;
-	
-	public Serializable[] getState() {
-		//TODO: assumes all collections disassemble to an array!
-		return (Serializable[]) state;
-	}
 
+	/**
+	 * Constructs a CollectionCacheEntry
+	 *
+	 * @param collection The persistent collection instance
+	 * @param persister The collection persister
+	 */
 	public CollectionCacheEntry(PersistentCollection collection, CollectionPersister persister) {
-		this.state = collection.disassemble(persister);
+		this.state = collection.disassemble( persister );
 	}
-	
+
 	CollectionCacheEntry(Serializable state) {
 		this.state = state;
 	}
-	
+
+	/**
+	 * Retrieve the cached collection state.
+	 *
+	 * @return The cached collection state.
+	 */
+	public Serializable[] getState() {
+		//TODO: assumes all collections disassemble to an array!
+		return (Serializable[]) state;
+	}
+
+	/**
+	 * Assembles the collection from the cached state.
+	 *
+	 * @param collection The persistent collection instance being assembled
+	 * @param persister The collection persister
+	 * @param owner The collection owner instance
+	 */
 	public void assemble(
-		final PersistentCollection collection, 
-		final CollectionPersister persister,
-		final Object owner
-	) {
-		collection.initializeFromCache(persister, state, owner);
+			final PersistentCollection collection,
+			final CollectionPersister persister,
+			final Object owner) {
+		collection.initializeFromCache( persister, state, owner );
 		collection.afterInitialize();
 	}
-	
+
+	@Override
 	public String toString() {
 		return "CollectionCacheEntry" + ArrayHelper.toString( getState() );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/ReferenceCacheEntryImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/ReferenceCacheEntryImpl.java
index 95d00ed8db..6babb78bb8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/ReferenceCacheEntryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/ReferenceCacheEntryImpl.java
@@ -1,75 +1,84 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 
-import org.hibernate.Interceptor;
-import org.hibernate.event.spi.EventSource;
-import org.hibernate.persister.entity.EntityPersister;
-
 /**
+ * Specialized CacheEntry for storing direct references to entity instances.
+ *
  * @author Steve Ebersole
  */
 public class ReferenceCacheEntryImpl implements CacheEntry {
 	private final Object reference;
 	private final String subclass;
 
+	/**
+	 * Constructs a ReferenceCacheEntryImpl
+	 *
+	 * @param reference The reference entity instance
+	 * @param subclass The specific subclass
+	 */
 	public ReferenceCacheEntryImpl(Object reference, String subclass) {
 		this.reference = reference;
 		this.subclass = subclass;
 	}
 
+	/**
+	 * Provides access to the stored reference.
+	 *
+	 * @return The stored reference
+	 */
+	public Object getReference() {
+		return reference;
+	}
+
 	@Override
 	public boolean isReferenceEntry() {
 		return true;
 	}
 
 	@Override
 	public String getSubclass() {
 		return subclass;
 	}
 
 	@Override
 	public Object getVersion() {
 		// reference data cannot be versioned
 		return null;
 	}
 
 	@Override
 	public boolean areLazyPropertiesUnfetched() {
 		// reference data cannot define lazy attributes
 		return false;
 	}
 
 	@Override
 	public Serializable[] getDisassembledState() {
 		// reference data is not disassembled into the cache
 		return null;
 	}
-
-	public Object getReference() {
-		return reference;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
index 51f9fceacd..1e3ef07f38 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
@@ -1,170 +1,196 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.TypeHelper;
 
 /**
+ * Standard representation of entity cached data using the "disassembled state".
+ *
  * @author Steve Ebersole
  */
 public class StandardCacheEntryImpl implements CacheEntry {
 	private final Serializable[] disassembledState;
 	private final String subclass;
 	private final boolean lazyPropertiesAreUnfetched;
 	private final Object version;
 
+	/**
+	 * Constructs a StandardCacheEntryImpl
+	 *
+	 * @param state The extracted state
+	 * @param persister The entity persister
+	 * @param unfetched Are any values present in state unfetched?
+	 * @param version The current version (if versioned)
+	 * @param session The originating session
+	 * @param owner The owner
+	 *
+	 * @throws HibernateException Generally indicates a problem performing the dis-assembly.
+	 */
+	public StandardCacheEntryImpl(
+			final Object[] state,
+			final EntityPersister persister,
+			final boolean unfetched,
+			final Object version,
+			final SessionImplementor session,
+			final Object owner)
+			throws HibernateException {
+		// disassembled state gets put in a new array (we write to cache by value!)
+		this.disassembledState = TypeHelper.disassemble(
+				state,
+				persister.getPropertyTypes(),
+				persister.isLazyPropertiesCacheable() ? null : persister.getPropertyLaziness(),
+				session,
+				owner
+		);
+		subclass = persister.getEntityName();
+		lazyPropertiesAreUnfetched = unfetched || !persister.isLazyPropertiesCacheable();
+		this.version = version;
+	}
+
+	StandardCacheEntryImpl(Serializable[] state, String subclass, boolean unfetched, Object version) {
+		this.disassembledState = state;
+		this.subclass = subclass;
+		this.lazyPropertiesAreUnfetched = unfetched;
+		this.version = version;
+	}
+
+
+
 	@Override
 	public boolean isReferenceEntry() {
 		return false;
 	}
 
 	@Override
 	public Serializable[] getDisassembledState() {
 		// todo: this was added to support initializing an entity's EntityEntry snapshot during reattach;
 		// this should be refactored to instead expose a method to assemble a EntityEntry based on this
 		// state for return.
 		return disassembledState;
 	}
 
 	@Override
 	public String getSubclass() {
 		return subclass;
 	}
 
 	@Override
 	public boolean areLazyPropertiesUnfetched() {
 		return lazyPropertiesAreUnfetched;
 	}
 
 	@Override
 	public Object getVersion() {
 		return version;
 	}
 
-	public StandardCacheEntryImpl(
-			final Object[] state,
-			final EntityPersister persister,
-			final boolean unfetched,
-			final Object version,
-			final SessionImplementor session,
-			final Object owner)
-			throws HibernateException {
-		//disassembled state gets put in a new array (we write to cache by value!)
-		this.disassembledState = TypeHelper.disassemble(
-				state,
-				persister.getPropertyTypes(),
-				persister.isLazyPropertiesCacheable() ?
-						null : persister.getPropertyLaziness(),
-				session,
-				owner
-		);
-		subclass = persister.getEntityName();
-		lazyPropertiesAreUnfetched = unfetched || !persister.isLazyPropertiesCacheable();
-		this.version = version;
-	}
-
-	StandardCacheEntryImpl(Serializable[] state, String subclass, boolean unfetched, Object version) {
-		this.disassembledState = state;
-		this.subclass = subclass;
-		this.lazyPropertiesAreUnfetched = unfetched;
-		this.version = version;
-	}
-
+	/**
+	 * After assembly, is a copy of the array needed?
+	 *
+	 * @return true/false
+	 */
 	public boolean isDeepCopyNeeded() {
 		// for now always return true.
 		// todo : See discussion on HHH-7872
 		return true;
 	}
 
+	/**
+	 * Assemble the previously disassembled state represented by this entry into the given entity instance.
+	 *
+	 * Additionally manages the PreLoadEvent callbacks.
+	 *
+	 * @param instance The entity instance
+	 * @param id The entity identifier
+	 * @param persister The entity persister
+	 * @param interceptor (currently unused)
+	 * @param session The session
+	 *
+	 * @return The assembled state
+	 *
+	 * @throws HibernateException Indicates a problem performing assembly or calling the PreLoadEventListeners.
+	 *
+	 * @see org.hibernate.type.Type#assemble
+	 * @see org.hibernate.type.Type#disassemble
+	 */
 	public Object[] assemble(
 			final Object instance,
 			final Serializable id,
 			final EntityPersister persister,
 			final Interceptor interceptor,
-			final EventSource session)
-			throws HibernateException {
-
-		if ( !persister.getEntityName().equals(subclass) ) {
-			throw new AssertionFailure("Tried to assemble a different subclass instance");
-		}
-
-		return assemble(disassembledState, instance, id, persister, interceptor, session);
-	}
-
-	private static Object[] assemble(
-			final Serializable[] values,
-			final Object result,
-			final Serializable id,
-			final EntityPersister persister,
-			final Interceptor interceptor,
 			final EventSource session) throws HibernateException {
+		if ( !persister.getEntityName().equals( subclass ) ) {
+			throw new AssertionFailure( "Tried to assemble a different subclass instance" );
+		}
 
 		//assembled state gets put in a new array (we read from cache by value!)
-		Object[] assembledProps = TypeHelper.assemble(
-				values,
+		final Object[] assembledProps = TypeHelper.assemble(
+				disassembledState,
 				persister.getPropertyTypes(),
-				session, result
+				session, instance
 		);
 
-		//persister.setIdentifier(result, id); //before calling interceptor, for consistency with normal load
+		//persister.setIdentifier(instance, id); //before calling interceptor, for consistency with normal load
 
 		//TODO: reuse the PreLoadEvent
 		final PreLoadEvent preLoadEvent = new PreLoadEvent( session )
-				.setEntity( result )
+				.setEntity( instance )
 				.setState( assembledProps )
 				.setId( id )
 				.setPersister( persister );
 
 		final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PRE_LOAD );
 		for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreLoad( preLoadEvent );
 		}
 
-		persister.setPropertyValues( result, assembledProps );
+		persister.setPropertyValues( instance, assembledProps );
 
 		return assembledProps;
 	}
 
+	@Override
 	public String toString() {
 		return "CacheEntry(" + subclass + ')' + ArrayHelper.toString( disassembledState );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
index 90e1f86952..e9107c4601 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
@@ -1,74 +1,82 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Structured CacheEntry format for entities.  Used to store the entry into the second-level cache
  * as a Map so that users can more easily see the cached state.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StructuredCacheEntry implements CacheEntryStructure {
-
 	private EntityPersister persister;
 
+	/**
+	 * Constructs a StructuredCacheEntry strategy
+	 *
+	 * @param persister The persister whose data needs to be structured.
+	 */
 	public StructuredCacheEntry(EntityPersister persister) {
 		this.persister = persister;
 	}
-	
-	public Object destructure(Object item, SessionFactoryImplementor factory) {
-		Map map = (Map) item;
-		boolean lazyPropertiesUnfetched = ( (Boolean) map.get("_lazyPropertiesUnfetched") ).booleanValue();
-		String subclass = (String) map.get("_subclass");
-		Object version = map.get("_version");
-		EntityPersister subclassPersister = factory.getEntityPersister(subclass);
-		String[] names = subclassPersister.getPropertyNames();
-		Serializable[] state = new Serializable[names.length];
-		for ( int i=0; i<names.length; i++ ) {
+
+	@Override
+	@SuppressWarnings("UnnecessaryUnboxing")
+	public Object destructure(Object structured, SessionFactoryImplementor factory) {
+		final Map map = (Map) structured;
+		final boolean lazyPropertiesUnfetched = ( (Boolean) map.get( "_lazyPropertiesUnfetched" ) ).booleanValue();
+		final String subclass = (String) map.get( "_subclass" );
+		final Object version = map.get( "_version" );
+		final EntityPersister subclassPersister = factory.getEntityPersister( subclass );
+		final String[] names = subclassPersister.getPropertyNames();
+		final Serializable[] state = new Serializable[names.length];
+		for ( int i = 0; i < names.length; i++ ) {
 			state[i] = (Serializable) map.get( names[i] );
 		}
 		return new StandardCacheEntryImpl( state, subclass, lazyPropertiesUnfetched, version );
 	}
 
+	@Override
+	@SuppressWarnings("unchecked")
 	public Object structure(Object item) {
-		CacheEntry entry = (CacheEntry) item;
-		String[] names = persister.getPropertyNames();
-		Map map = new HashMap(names.length+2);
+		final CacheEntry entry = (CacheEntry) item;
+		final String[] names = persister.getPropertyNames();
+		final Map map = new HashMap(names.length+2);
 		map.put( "_subclass", entry.getSubclass() );
 		map.put( "_version", entry.getVersion() );
 		map.put( "_lazyPropertiesUnfetched", entry.areLazyPropertiesUnfetched() );
 		for ( int i=0; i<names.length; i++ ) {
 			map.put( names[i], entry.getDisassembledState()[i] );
 		}
 		return map;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java
index da3009c3a8..e929043009 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java
@@ -1,49 +1,57 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.List;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Structured CacheEntry format for persistent collections (other than Maps, see {@link StructuredMapCacheEntry}).
  *
  * @author Gavin King
  */
 public class StructuredCollectionCacheEntry implements CacheEntryStructure {
+	/**
+	 * Access to the singleton reference.
+	 */
+	public static final StructuredCollectionCacheEntry INSTANCE = new StructuredCollectionCacheEntry();
 
+	@Override
 	public Object structure(Object item) {
-		CollectionCacheEntry entry = (CollectionCacheEntry) item;
+		final CollectionCacheEntry entry = (CollectionCacheEntry) item;
 		return Arrays.asList( entry.getState() );
 	}
-	
-	public Object destructure(Object item, SessionFactoryImplementor factory) {
-		List list = (List) item;
+
+	@Override
+	public Object destructure(Object structured, SessionFactoryImplementor factory) {
+		final List list = (List) structured;
 		return new CollectionCacheEntry( list.toArray( new Serializable[list.size()] ) );
 	}
 
+	private StructuredCollectionCacheEntry() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java
index 4a93cf3a6d..a896e09b44 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java
@@ -1,63 +1,69 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 import java.util.HashMap;
-import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Structured CacheEntry format for persistent Maps.
  *
  * @author Gavin King
  */
 public class StructuredMapCacheEntry implements CacheEntryStructure {
+	/**
+	 * Access to the singleton reference
+	 */
+	public static final StructuredMapCacheEntry INSTANCE = new StructuredMapCacheEntry();
 
+	@Override
+	@SuppressWarnings("unchecked")
 	public Object structure(Object item) {
-		CollectionCacheEntry entry = (CollectionCacheEntry) item;
-		Serializable[] state = entry.getState();
-		Map map = new HashMap(state.length);
+		final CollectionCacheEntry entry = (CollectionCacheEntry) item;
+		final Serializable[] state = entry.getState();
+		final Map map = new HashMap(state.length);
 		for ( int i=0; i<state.length; ) {
 			map.put( state[i++], state[i++] );
 		}
 		return map;
 	}
-	
-	public Object destructure(Object item, SessionFactoryImplementor factory) {
-		Map map = (Map) item;
-		Serializable[] state = new Serializable[ map.size()*2 ];
-		int i=0;
-		Iterator iter = map.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
+
+	@Override
+	public Object destructure(Object structured, SessionFactoryImplementor factory) {
+		final Map<?,?> map = (Map<?,?>) structured;
+		final Serializable[] state = new Serializable[ map.size()*2 ];
+		int i = 0;
+		for ( Map.Entry me : map.entrySet() ) {
 			state[i++] = (Serializable) me.getKey();
 			state[i++] = (Serializable) me.getValue();
 		}
 		return new CollectionCacheEntry(state);
 	}
 
+	private StructuredMapCacheEntry() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java
index b9221e3816..31e7b9f240 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java
@@ -1,45 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.entry;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Unstructured CacheEntry format (used to store entities and collections).
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class UnstructuredCacheEntry implements CacheEntryStructure {
+	/**
+	 * Access to the singleton instance.
+	 */
 	public static final UnstructuredCacheEntry INSTANCE = new UnstructuredCacheEntry();
 
+	@Override
 	public Object structure(Object item) {
 		return item;
 	}
 
-	public Object destructure(Object map, SessionFactoryImplementor factory) {
-		return map;
+	@Override
+	public Object destructure(Object structured, SessionFactoryImplementor factory) {
+		return structured;
 	}
 
+	private UnstructuredCacheEntry() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
index 8676f63d12..bccd2d9a36 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
@@ -1,390 +1,401 @@
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
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.AssociationOverride;
 import javax.persistence.AssociationOverrides;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Column;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.MappedSuperclass;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.internal.util.StringHelper;
 
 /**
+ * No idea.
+ *
  * @author Emmanuel Bernard
  */
 public abstract class AbstractPropertyHolder implements PropertyHolder {
 	protected AbstractPropertyHolder parent;
 	private Map<String, Column[]> holderColumnOverride;
 	private Map<String, Column[]> currentPropertyColumnOverride;
 	private Map<String, JoinColumn[]> holderJoinColumnOverride;
 	private Map<String, JoinColumn[]> currentPropertyJoinColumnOverride;
 	private Map<String, JoinTable> holderJoinTableOverride;
 	private Map<String, JoinTable> currentPropertyJoinTableOverride;
 	private String path;
 	private Mappings mappings;
 	private Boolean isInIdClass;
 
 
-	public AbstractPropertyHolder(
+	AbstractPropertyHolder(
 			String path,
 			PropertyHolder parent,
 			XClass clazzToProcess,
 			Mappings mappings) {
 		this.path = path;
 		this.parent = (AbstractPropertyHolder) parent;
 		this.mappings = mappings;
 		buildHierarchyColumnOverride( clazzToProcess );
 	}
 
-
+	@Override
 	public boolean isInIdClass() {
 		return isInIdClass != null ? isInIdClass : parent != null ? parent.isInIdClass() : false;
 	}
 
+	@Override
 	public void setInIdClass(Boolean isInIdClass) {
 		this.isInIdClass = isInIdClass;
 	}
 
+	@Override
 	public String getPath() {
 		return path;
 	}
 
+	/**
+	 * Get the mappings
+	 *
+	 * @return The mappings
+	 */
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	/**
-	 * property can be null
+	 * Set the property be processed.  property can be null
+	 *
+	 * @param property The property
 	 */
 	protected void setCurrentProperty(XProperty property) {
 		if ( property == null ) {
 			this.currentPropertyColumnOverride = null;
 			this.currentPropertyJoinColumnOverride = null;
 			this.currentPropertyJoinTableOverride = null;
 		}
 		else {
 			this.currentPropertyColumnOverride = buildColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyColumnOverride.size() == 0 ) {
 				this.currentPropertyColumnOverride = null;
 			}
 			this.currentPropertyJoinColumnOverride = buildJoinColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinColumnOverride.size() == 0 ) {
 				this.currentPropertyJoinColumnOverride = null;
 			}
 			this.currentPropertyJoinTableOverride = buildJoinTableOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinTableOverride.size() == 0 ) {
 				this.currentPropertyJoinTableOverride = null;
 			}
 		}
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
-	 *
 	 */
+	@Override
 	public Column[] getOverriddenColumn(String propertyName) {
 		Column[] result = getExactOverriddenColumn( propertyName );
 		if (result == null) {
 			//the commented code can be useful if people use the new prefixes on old mappings and vice versa
 			// if we enable them:
 			// WARNING: this can conflict with user's expectations if:
 	 		//  - the property uses some restricted values
 	 		//  - the user has overridden the column
 			// also change getOverriddenJoinColumn and getOverriddenJoinTable as well
 	 		
 //			if ( propertyName.contains( ".key." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".key.", ".index."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".key" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".key".length() ) + ".index"
 //						);
 //			}
 //			if ( result == null && propertyName.contains( ".value." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".value.", ".element."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".value" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".value".length() ) + ".element"
 //						);
 //			}
 			if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 				//support for non map collections where no prefix is needed
 				//TODO cache the underlying regexp
 				result = getExactOverriddenColumn( propertyName.replace( ".collection&&element.", "."  ) );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * find the overridden rules from the exact property name.
 	 */
 	private Column[] getExactOverriddenColumn(String propertyName) {
 		Column[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenColumn( propertyName );
 		}
 		if ( override == null && currentPropertyColumnOverride != null ) {
 			override = currentPropertyColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderColumnOverride != null ) {
 			override = holderColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
-	 *
 	 */
+	@Override
 	public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] result = getExactOverriddenJoinColumn( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinColumn( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinColumn[] getExactOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinColumn( propertyName );
 		}
 		if ( override == null && currentPropertyJoinColumnOverride != null ) {
 			override = currentPropertyJoinColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinColumnOverride != null ) {
 			override = holderJoinColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
-	 *
 	 */
+	@Override
 	public JoinTable getJoinTable(XProperty property) {
 		final String propertyName = StringHelper.qualify( getPath(), property.getName() );
 		JoinTable result = getOverriddenJoinTable( propertyName );
 		if (result == null) {
 			result = property.getAnnotation( JoinTable.class );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
-	 *
 	 */
 	public JoinTable getOverriddenJoinTable(String propertyName) {
 		JoinTable result = getExactOverriddenJoinTable( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinTable( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinTable getExactOverriddenJoinTable(String propertyName) {
 		JoinTable override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinTable( propertyName );
 		}
 		if ( override == null && currentPropertyJoinTableOverride != null ) {
 			override = currentPropertyJoinTableOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinTableOverride != null ) {
 			override = holderJoinTableOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	private void buildHierarchyColumnOverride(XClass element) {
 		XClass current = element;
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		Map<String, JoinColumn[]> joinColumnOverride = new HashMap<String, JoinColumn[]>();
 		Map<String, JoinTable> joinTableOverride = new HashMap<String, JoinTable>();
 		while ( current != null && !mappings.getReflectionManager().toXClass( Object.class ).equals( current ) ) {
 			if ( current.isAnnotationPresent( Entity.class ) || current.isAnnotationPresent( MappedSuperclass.class )
 					|| current.isAnnotationPresent( Embeddable.class ) ) {
 				//FIXME is embeddable override?
 				Map<String, Column[]> currentOverride = buildColumnOverride( current, getPath() );
 				Map<String, JoinColumn[]> currentJoinOverride = buildJoinColumnOverride( current, getPath() );
 				Map<String, JoinTable> currentJoinTableOverride = buildJoinTableOverride( current, getPath() );
 				currentOverride.putAll( columnOverride ); //subclasses have precedence over superclasses
 				currentJoinOverride.putAll( joinColumnOverride ); //subclasses have precedence over superclasses
 				currentJoinTableOverride.putAll( joinTableOverride ); //subclasses have precedence over superclasses
 				columnOverride = currentOverride;
 				joinColumnOverride = currentJoinOverride;
 				joinTableOverride = currentJoinTableOverride;
 			}
 			current = current.getSuperclass();
 		}
 
 		holderColumnOverride = columnOverride.size() > 0 ? columnOverride : null;
 		holderJoinColumnOverride = joinColumnOverride.size() > 0 ? joinColumnOverride : null;
 		holderJoinTableOverride = joinTableOverride.size() > 0 ? joinTableOverride : null;
 	}
 
 	private static Map<String, Column[]> buildColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		if ( element == null ) return columnOverride;
 		AttributeOverride singleOverride = element.getAnnotation( AttributeOverride.class );
 		AttributeOverrides multipleOverrides = element.getAnnotation( AttributeOverrides.class );
 		AttributeOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AttributeOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AttributeOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						new Column[] { depAttr.column() }
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinColumn[]> buildJoinColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinColumn[]> columnOverride = new HashMap<String, JoinColumn[]>();
 		if ( element == null ) return columnOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						depAttr.joinColumns()
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinTable> buildJoinTableOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinTable> tableOverride = new HashMap<String, JoinTable>();
 		if ( element == null ) return tableOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden tables
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				if ( depAttr.joinColumns().length == 0 ) {
 					tableOverride.put(
 							StringHelper.qualify( path, depAttr.name() ),
 							depAttr.joinTable()
 					);
 				}
 			}
 		}
 		return tableOverride;
 	}
 
+	@Override
 	public void setParentProperty(String parentProperty) {
 		throw new AssertionFailure( "Setting the parent property to a non component" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java b/hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java
index 8e9c1d7ea4..da06603d24 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java
@@ -1,84 +1,103 @@
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
 
 /**
  * Enum defining different access strategies for accessing entity values.
  *
  * @author Hardy Ferentschik
  */
 public enum AccessType {
 	/**
 	 * Default access strategy is property
 	 */
 	DEFAULT( "property" ),
 
 	/**
 	 * Access to value via property
 	 */
 	PROPERTY( "property" ),
 
 	/**
 	 * Access to value via field
 	 */
 	FIELD( "field" );
 
 	private final String accessType;
 
-	AccessType(String type) {
+	private AccessType(String type) {
 		this.accessType = type;
 	}
 
+	/**
+	 * Retrieves the external name for this access type
+	 *
+	 * @return The external name
+	 */
 	public String getType() {
 		return accessType;
 	}
 
-	public static AccessType getAccessStrategy(String type) {
-		if ( type == null ) {
+	/**
+	 * Resolve an externalized name to the AccessType enum value it names.
+	 *
+	 * @param externalName The external name
+	 *
+	 * @return The matching AccessType; {@link #DEFAULT} is returned rather than {@code null}
+	 */
+	public static AccessType getAccessStrategy(String externalName) {
+		if ( externalName == null ) {
 			return DEFAULT;
 		}
-		else if ( FIELD.getType().equals( type ) ) {
+		else if ( FIELD.getType().equals( externalName ) ) {
 			return FIELD;
 		}
-		else if ( PROPERTY.getType().equals( type ) ) {
+		else if ( PROPERTY.getType().equals( externalName ) ) {
 			return PROPERTY;
 		}
 		else {
-			// TODO historically if the type string could not be matched default access was used. Maybe this should be an exception though!?
+			// TODO historically if the externalName string could not be matched default access was used. Maybe this should be an exception though!?
 			return DEFAULT;
 		}
 	}
 
+	/**
+	 * Convert the JPA access type enum to the corresponding AccessType enum value.
+	 *
+	 * @param type The JPA enum value
+	 *
+	 * @return The Hibernate AccessType
+	 */
 	public static AccessType getAccessStrategy(javax.persistence.AccessType type) {
 		if ( javax.persistence.AccessType.PROPERTY.equals( type ) ) {
 			return PROPERTY;
 		}
 		else if ( javax.persistence.AccessType.FIELD.equals( type ) ) {
 			return FIELD;
 		}
 		else {
 			return DEFAULT;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index f68c7457bb..b4c043de2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,779 +1,775 @@
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
 package org.hibernate.internal.util;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.BitSet;
 import java.util.Iterator;
 import java.util.StringTokenizer;
 import java.util.UUID;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 	
 	/*public static boolean containsDigits(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			if ( Character.isDigit( string.charAt(i) ) ) return true;
 		}
 		return false;
 	}*/
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			char character = string.charAt(i);
 			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
 		}
 		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String joinWithQualifier(String[] values, String qualifier, String deliminator) {
 		int length = values.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * values[0].length() )
 				.append( qualify( qualifier, values[0] ) );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( deliminator ).append( qualify( qualifier, values[i] ) );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuilder buf = new StringBuilder();
 		if ( objects.hasNext() ) buf.append( objects.next() );
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
 		String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuilder buf = new StringBuilder( string.length() * times );
 		for ( int i = 0; i < times; i++ ) buf.append( string );
 		return buf.toString();
 	}
 
 	public static String repeat(String string, int times, String deliminator) {
 		StringBuilder buf = new StringBuilder(  ( string.length() * times ) + ( deliminator.length() * (times-1) ) )
 				.append( string );
 		for ( int i = 1; i < times; i++ ) {
 			buf.append( deliminator ).append( string );
 		}
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i =0; i<templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
 		return replace( template, placeholder, replacement, wholeWords, false );
 	}
 
 	public static String replace(String template,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return template;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			String beforePlaceholder = template.substring( 0, loc );
 			String afterPlaceholder = template.substring( loc + placeholder.length() );
 			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
 		}
 	}
 
 
 	public static String replace(String beforePlaceholder,
 								 String afterPlaceholder,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		final boolean actuallyReplace =
 				! wholeWords ||
 				afterPlaceholder.length() == 0 ||
 				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
 		boolean encloseInParens =
 				actuallyReplace &&
 				encloseInParensIfNecessary &&
 				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
 				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
 		StringBuilder buf = new StringBuilder( beforePlaceholder );
 		if ( encloseInParens ) {
 			buf.append( '(' );
 		}
 		buf.append( actuallyReplace ? replacement : placeholder );
 		if ( encloseInParens ) {
 			buf.append( ')' );
 		}
 		buf.append(
 				replace(
 						afterPlaceholder,
 						placeholder,
 						replacement,
 						wholeWords,
 						encloseInParensIfNecessary
 				)
 		);
 		return buf.toString();
 	}
 
 	public static char getLastNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static char getFirstNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = 0 ; i < str.length() ; i++ ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return template; // returnign null!
 		}
         int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return new StringBuilder( template.substring( 0, loc ) )
 					.append( replacement )
 					.append( template.substring( loc + placeholder.length() ) )
 					.toString();
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[ tokens.countTokens() ];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) return columns;
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase();
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) return "";
 		StringBuilder buf = new StringBuilder( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append(", ");
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[]{string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( int i = 0; i < replacements.length; i++ ) {
 			for ( int j = 0; j < strings.length; j++ ) {
 				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null)
 			return 0;
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( indx );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
-			throw new NullPointerException();
+			throw new NullPointerException( "prefix or name were null attempting to build qualified name" );
 		}
-		return new StringBuilder( prefix.length() + name.length() + 1 )
-				.append(prefix)
-				.append('.')
-				.append(name)
-				.toString();
+		return prefix + '.' + name;
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) return names;
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 	public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
 		for ( int i = startindex, size = sqlString.length(); i < size; i++ ) {
 			if ( keys.get( sqlString.charAt( i ) ) ) {
 				return i;
 			}
 		}
 		return -1;
 
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		BitSet keys = new BitSet();
 		for ( int i = 0, size = string.length(); i < size; i++ ) {
 			keys.set( string.charAt( i ) );
 		}
 		return firstIndexOfChar( sqlString, keys, startindex );
 
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot(description) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot(description) +
 			Integer.toString(unique) +
 			'_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 * @return The generated root alias.
 	 */
 	private static String generateAliasRoot(String description) {
 		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
 				.toLowerCase()
 		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
 				.replace( '$', '_' ); //classname may be an inner class
 		result = cleanAlias( result );
 		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
 			return result + "x"; //ick!
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
 	private static String cleanAlias(String alias) {
 		char[] chars = alias.toCharArray();
 		// short cut check...
 		if ( !Character.isLetter( chars[0] ) ) {
 			for ( int i = 1; i < chars.length; i++ ) {
 				// as soon as we encounter our first letter, return the substring
 				// from that position
 				if ( Character.isLetter( chars[i] ) ) {
 					return alias.substring( i );
 				}
 			}
 		}
 		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify(entityName);
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 	
 	public static String toUpperCase(String str) {
 		return str==null ? null : str.toUpperCase();
 	}
 	
 	public static String toLowerCase(String str) {
 		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length()>0 ){
 			filter += " and ";
 			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
 		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
 // Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
         else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
             name = name.substring( 1, name.length() - 1 );
         }
 
 		return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * 		AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null
 				&&
 					name.length() != 0
 				&& (
 					name.charAt( 0 ) == '`'
 					&&
 					name.charAt( name.length() - 1 ) == '`'
 					||
 					name.charAt( 0 ) == dialect.openQuote()
 					&&
 					name.charAt( name.length() - 1 ) == dialect.closeQuote()
 				);
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		return isQuoted( name, dialect ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[ names.length ];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 
 
 	public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
 
 	public static StringBuilder buildBatchFetchRestrictionFragment(
 			String alias,
 			String[] columnNames,
 			Dialect dialect) {
 		// the general idea here is to just insert a placeholder that we can easily find later...
 		if ( columnNames.length == 1 ) {
 			// non-composite key
 			return new StringBuilder( StringHelper.qualify( alias, columnNames[0] ) )
 					.append( " in (" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 		}
 		else {
 			// composite key - the form to use here depends on what the dialect supports.
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				// use : (col1, col2) in ( (?,?), (?,?), ... )
 				StringBuilder builder = new StringBuilder();
 				builder.append( "(" );
 				boolean firstPass = true;
 				String deliminator = "";
 				for ( String columnName : columnNames ) {
 					builder.append( deliminator ).append( StringHelper.qualify( alias, columnName ) );
 					if ( firstPass ) {
 						firstPass = false;
 						deliminator = ",";
 					}
 				}
 				builder.append( ") in (" );
 				builder.append( BATCH_ID_PLACEHOLDER );
 				builder.append( ")" );
 				return builder;
 			}
 			else {
 				// use : ( (col1 = ? and col2 = ?) or (col1 = ? and col2 = ?) or ... )
 				//		unfortunately most of this building needs to be held off until we know
 				//		the exact number of ids :(
 				return new StringBuilder( "(" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 			}
 		}
 	}
 
 	public static String expandBatchIdPlaceholder(
 			String sql,
 			Serializable[] ids,
 			String alias,
 			String[] keyColumnNames,
 			Dialect dialect) {
 		if ( keyColumnNames.length == 1 ) {
 			// non-composite
 			return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( "?", ids.length, "," ) );
 		}
 		else {
 			// composite
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				final String tuple = "(" + StringHelper.repeat( "?", keyColumnNames.length, "," );
 				return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( tuple, ids.length, "," ) );
 			}
 			else {
 				final String keyCheck = joinWithQualifier( keyColumnNames, alias, " and " );
 				return replace( sql, BATCH_ID_PLACEHOLDER, repeat( keyCheck, ids.length, " or " ) );
 			}
 		}
 	}
 	
 	/**
 	 * Takes a String s and returns a new String[1] with s as the only element.
 	 * If s is null or "", return String[0].
 	 * 
 	 * @param s
 	 * @return String[]
 	 */
 	public static String[] toArrayElement(String s) {
 		return ( s == null || s.length() == 0 ) ? new String[0] : new String[] { s };
 	}
 
 	// Oracle restricts identifier lengths to 30.  Rather than tie this to
 	// Dialect, simply restrict randomly-generated constrain names across
 	// the board.
 	private static final int MAX_NAME_LENGTH = 30;
 	public static String randomFixedLengthHex(String prefix) {
 		int length = MAX_NAME_LENGTH - prefix.length();
 		String s = UUID.randomUUID().toString();
 		s = s.replace( "-", "" );
 		if (s.length() > length) {
 			s = s.substring( 0, length );
 		}
 		return prefix + s;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 96cf05b129..819ad1b0a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1253 +1,1253 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.sql.ordering.antlr.ColumnReference;
 import org.hibernate.sql.ordering.antlr.FormulaReference;
 import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
 import org.hibernate.sql.ordering.antlr.OrderByTranslation;
 import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  * 
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
 			AbstractCollectionPersister.class.getName() );
 
 	// TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	// SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final boolean hasWhere;
 	protected final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	private final boolean hasOrder;
 	private final OrderByTranslation orderByTranslation;
 
 	private final boolean hasManyToManyOrder;
 	private final OrderByTranslation manyToManyOrderByTranslation;
 
 	private final int baseIndex;
 
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	// types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	// columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	// private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	protected final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	// extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	protected final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
-			cacheEntryStructure = collection.isMap() ?
-					new StructuredMapCacheEntry() :
-					new StructuredCollectionCacheEntry();
+			cacheEntryStructure = collection.isMap()
+					? StructuredMapCacheEntry.INSTANCE
+					: StructuredCollectionCacheEntry.INSTANCE;
 		}
 		else {
-			cacheEntryStructure = new UnstructuredCacheEntry();
+			cacheEntryStructure = UnstructuredCacheEntry.INSTANCE;
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		// isSet = collection.isSet();
 		// isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 				);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, collection.getOwner().getRootTable() );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
 			if ( elemNode == null ) {
 				elemNode = cfg.getClassMapping( entityName ).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias( dialect, table );
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName( dialect );
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr( dialect );
 				elementColumnReaderTemplates[j] = col.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		// workaround, for backward compatibility of sets with no
 		// not-null columns, assume all columns are used in the
 		// row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if ( hasIndex ) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias( dialect );
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName( dialect );
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName();
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 
 		hasIdentifier = collection.isIdentified();
 		if ( hasIdentifier ) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 					);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			// unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		// GENERATE THE SQL:
 
 		// sqlSelectString = sqlSelectString();
 		// sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 					: collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 					: collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString( collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; // elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 					);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 					);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { // not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 						);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			orderByTranslation = Template.translateOrderBy(
 					collection.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilters(), factory);
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collection.getManyToManyOrdering(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	private class ColumnMapperImpl implements ColumnMapper {
 		@Override
 		public SqlValueReference[] map(String reference) {
 			final String[] columnNames;
 			final String[] formulaTemplates;
 
 			// handle the special "$element$" property name...
 			if ( "$element$".equals( reference ) ) {
 				columnNames = elementColumnNames;
 				formulaTemplates = elementFormulaTemplates;
 			}
 			else {
 				columnNames = elementPropertyMapping.toColumns( reference );
 				formulaTemplates = formulaTemplates( reference, columnNames.length );
 			}
 
 			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
 			int i = 0;
 			for ( final String columnName : columnNames ) {
 				if ( columnName == null ) {
 					// if the column name is null, it indicates that this index in the property value mapping is
 					// actually represented by a formula.
 //					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 					final String formulaTemplate = formulaTemplates[i];
 					result[i] = new FormulaReference() {
 						@Override
 						public String getFormulaFragment() {
 							return formulaTemplate;
 						}
 					};
 				}
 				else {
 					result[i] = new ColumnReference() {
 						@Override
 						public String getColumnName() {
 							return columnName;
 						}
 					};
 				}
 				i++;
 			}
 			return result;
 		}
 	}
 
 	private String[] formulaTemplates(String reference, int expectedSize) {
 		try {
 			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
 		}
 		catch (Exception e) {
 			return new String[expectedSize];
 		}
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for collection: %s", getRole() );
 			if ( getSQLInsertRowString() != null ) LOG.debugf( " Row insert: %s", getSQLInsertRowString() );
 			if ( getSQLUpdateRowString() != null ) LOG.debugf( " Row update: %s", getSQLUpdateRowString() );
 			if ( getSQLDeleteRowString() != null ) LOG.debugf( " Row delete: %s", getSQLDeleteRowString() );
 			if ( getSQLDeleteString() != null ) LOG.debugf( " One-shot delete: %s", getSQLDeleteString() );
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			// if there is a user-specified loader, return that
 			// TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if ( subselect == null ) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { // needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
             index = (Integer)index - baseIndex;
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role ); // an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( indexColumnIsSettable );
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
             index = (Integer)index + baseIndex;
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( elementIsPureFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based element in the where condition" );
 		}
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsInPrimaryKey, session );
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( indexContainsFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based index in the where condition" );
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); // strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 				"max(" + getIndexColumnNames()[0] + ") + 1" : // lists, arrays
 				"count(" + getElementColumnNames()[0] + ")"; // sets, maps, bags
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn( selectValue )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	protected BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			try {
 				// create all the new entries
 				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 											);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							try {
 								offset += expectation.prepare( st );
 
 								// TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
index 42d10e1c66..05014bccb0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
@@ -1,308 +1,307 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.bytecode.enhancement;
 
 import java.io.ByteArrayInputStream;
 import java.lang.reflect.Method;
 import java.util.Arrays;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtField;
 import javassist.LoaderClassPath;
 
 import org.hibernate.EntityMode;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
-import org.hibernate.cfg.Configuration;
+import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.Status;
-import org.hibernate.mapping.PersistentClass;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Steve Ebersole
  */
 public class EnhancerTest extends BaseUnitTestCase {
 	private static EnhancementContext enhancementContext = new EnhancementContext() {
 		@Override
 		public ClassLoader getLoadingClassLoader() {
 			return getClass().getClassLoader();
 		}
 
 		@Override
 		public boolean isEntityClass(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean isCompositeClass(CtClass classDescriptor) {
 			return false;
 		}
 
 		@Override
 		public boolean doDirtyCheckingInline(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean hasLazyLoadableAttributes(CtClass classDescriptor) {
 			return true;
 		}
 
 		@Override
 		public boolean isLazyLoadable(CtField field) {
 			return true;
 		}
 
 		@Override
 		public boolean isPersistentField(CtField ctField) {
 			return true;
 		}
 
 		@Override
 		public CtField[] order(CtField[] persistentFields) {
 			return persistentFields;
 		}
 	};
 
 	@Test
 	public void testEnhancement() throws Exception {
 		testFor( SimpleEntity.class );
 		testFor( SubEntity.class );
 	}
 
 	private void testFor(Class entityClassToEnhance) throws Exception {
 		Enhancer enhancer = new Enhancer( enhancementContext );
 		CtClass entityCtClass = generateCtClassForAnEntity( entityClassToEnhance );
 		byte[] original = entityCtClass.toBytecode();
 		byte[] enhanced = enhancer.enhance( entityCtClass.getName(), original );
 		assertFalse( "entity was not enhanced", Arrays.equals( original, enhanced ) );
 
 		ClassLoader cl = new ClassLoader() { };
 		ClassPool cp = new ClassPool( false );
 		cp.appendClassPath( new LoaderClassPath( cl ) );
 		CtClass enhancedCtClass = cp.makeClass( new ByteArrayInputStream( enhanced ) );
 		Class entityClass = enhancedCtClass.toClass( cl, this.getClass().getProtectionDomain() );
 		Object entityInstance = entityClass.newInstance();
 
 		assertTyping( ManagedEntity.class, entityInstance );
 
 		// call the new methods
 		//
-		Method setter = entityClass.getMethod( Enhancer.ENTITY_ENTRY_SETTER_NAME, EntityEntry.class );
-		Method getter = entityClass.getMethod( Enhancer.ENTITY_ENTRY_GETTER_NAME );
+		Method setter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME, EntityEntry.class );
+		Method getter = entityClass.getMethod( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME );
 		assertNull( getter.invoke( entityInstance ) );
 		setter.invoke( entityInstance, makeEntityEntry() );
 		assertNotNull( getter.invoke( entityInstance ) );
 		setter.invoke( entityInstance, new Object[] {null} );
 		assertNull( getter.invoke( entityInstance ) );
 
-		Method entityInstanceGetter = entityClass.getMethod( Enhancer.ENTITY_INSTANCE_GETTER_NAME );
+		Method entityInstanceGetter = entityClass.getMethod( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME );
 		assertSame( entityInstance, entityInstanceGetter.invoke( entityInstance ) );
 
-		Method previousGetter = entityClass.getMethod( Enhancer.PREVIOUS_GETTER_NAME );
-		Method previousSetter = entityClass.getMethod( Enhancer.PREVIOUS_SETTER_NAME, ManagedEntity.class );
+		Method previousGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
+		Method previousSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
 		previousSetter.invoke( entityInstance, entityInstance );
 		assertSame( entityInstance, previousGetter.invoke( entityInstance ) );
 
-		Method nextGetter = entityClass.getMethod( Enhancer.PREVIOUS_GETTER_NAME );
-		Method nextSetter = entityClass.getMethod( Enhancer.PREVIOUS_SETTER_NAME, ManagedEntity.class );
+		Method nextGetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_GETTER_NAME );
+		Method nextSetter = entityClass.getMethod( EnhancerConstants.PREVIOUS_SETTER_NAME, ManagedEntity.class );
 		nextSetter.invoke( entityInstance, entityInstance );
 		assertSame( entityInstance, nextGetter.invoke( entityInstance ) );
 
 		// add an attribute interceptor...
-		Method interceptorGetter = entityClass.getMethod( Enhancer.INTERCEPTOR_GETTER_NAME );
-		Method interceptorSetter = entityClass.getMethod( Enhancer.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
+		Method interceptorGetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_GETTER_NAME );
+		Method interceptorSetter = entityClass.getMethod( EnhancerConstants.INTERCEPTOR_SETTER_NAME, PersistentAttributeInterceptor.class );
 
 		assertNull( interceptorGetter.invoke( entityInstance ) );
 		entityClass.getMethod( "getId" ).invoke( entityInstance );
 
 		interceptorSetter.invoke( entityInstance, new LocalPersistentAttributeInterceptor() );
 		assertNotNull( interceptorGetter.invoke( entityInstance ) );
 
 		// dirty checking is unfortunately just printlns for now... just verify the test output
 		entityClass.getMethod( "getId" ).invoke( entityInstance );
 		entityClass.getMethod( "setId", Long.class ).invoke( entityInstance, entityClass.getMethod( "getId" ).invoke( entityInstance ) );
 		entityClass.getMethod( "setId", Long.class ).invoke( entityInstance, 1L );
 
 		entityClass.getMethod( "isActive" ).invoke( entityInstance );
 		entityClass.getMethod( "setActive", boolean.class ).invoke( entityInstance, entityClass.getMethod( "isActive" ).invoke( entityInstance ) );
 		entityClass.getMethod( "setActive", boolean.class ).invoke( entityInstance, true );
 
 		entityClass.getMethod( "getSomeNumber" ).invoke( entityInstance );
 		entityClass.getMethod( "setSomeNumber", long.class ).invoke( entityInstance, entityClass.getMethod( "getSomeNumber" ).invoke( entityInstance ) );
 		entityClass.getMethod( "setSomeNumber", long.class ).invoke( entityInstance, 1L );
 	}
 
 	private CtClass generateCtClassForAnEntity(Class entityClassToEnhance) throws Exception {
 		ClassPool cp = new ClassPool( false );
 		return cp.makeClass(
 				getClass().getClassLoader().getResourceAsStream(
 						entityClassToEnhance.getName().replace( '.', '/' ) + ".class"
 				)
 		);
 	}
 
 	private EntityEntry makeEntityEntry() {
 		return new EntityEntry(
 				Status.MANAGED,
 				null,
 				null,
 				new Long(1),
 				null,
 				LockMode.NONE,
 				false,
 				null,
 				EntityMode.POJO,
 				null,
 				false,
 				false,
 				null
 		);
 	}
 
 
 	private class LocalPersistentAttributeInterceptor implements PersistentAttributeInterceptor {
 		@Override
 		public boolean readBoolean(Object obj, String name, boolean oldValue) {
 			System.out.println( "Reading boolean [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
 			System.out.println( "Writing boolean [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public byte readByte(Object obj, String name, byte oldValue) {
 			System.out.println( "Reading byte [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
 			System.out.println( "Writing byte [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public char readChar(Object obj, String name, char oldValue) {
 			System.out.println( "Reading char [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public char writeChar(Object obj, String name, char oldValue, char newValue) {
 			System.out.println( "Writing char [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public short readShort(Object obj, String name, short oldValue) {
 			System.out.println( "Reading short [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public short writeShort(Object obj, String name, short oldValue, short newValue) {
 			System.out.println( "Writing short [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public int readInt(Object obj, String name, int oldValue) {
 			System.out.println( "Reading int [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public int writeInt(Object obj, String name, int oldValue, int newValue) {
 			System.out.println( "Writing int [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public float readFloat(Object obj, String name, float oldValue) {
 			System.out.println( "Reading float [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public float writeFloat(Object obj, String name, float oldValue, float newValue) {
 			System.out.println( "Writing float [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public double readDouble(Object obj, String name, double oldValue) {
 			System.out.println( "Reading double [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public double writeDouble(Object obj, String name, double oldValue, double newValue) {
 			System.out.println( "Writing double [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public long readLong(Object obj, String name, long oldValue) {
 			System.out.println( "Reading long [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public long writeLong(Object obj, String name, long oldValue, long newValue) {
 			System.out.println( "Writing long [" + name + "]" );
 			return newValue;
 		}
 
 		@Override
 		public Object readObject(Object obj, String name, Object oldValue) {
 			System.out.println( "Reading Object [" + name + "]" );
 			return oldValue;
 		}
 
 		@Override
 		public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
 			System.out.println( "Writing Object [" + name + "]" );
 			return newValue;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index ba4ff61bf5..a4b56b0193 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,692 +1,692 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
-		return new UnstructuredCacheEntry();
+		return UnstructuredCacheEntry.INSTANCE;
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
 }
diff --git a/shared/config/checkstyle/checkstyle.xml b/shared/config/checkstyle/checkstyle.xml
index 930d7cbfbd..a595af07b1 100644
--- a/shared/config/checkstyle/checkstyle.xml
+++ b/shared/config/checkstyle/checkstyle.xml
@@ -1,147 +1,251 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
   ~ Copyright (c) 2013, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
   ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
   -->
 <!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.1//EN" "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">
 <module name="Checker">
 
-    <!--
-        Note that checkstyle is used to validate contributed code and generally used to fail builds if
-        the checks fail.
+    <module name="TreeWalker">
 
-        Note also that some checks apply a warning severity, rather than error.
-        The commented-out checks are ones I would ultimately like to (re)enable.
-    -->
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            General regex checks as part of the TreeWalker
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
+<!--
+        <module name="Regexp">
+            <property name="format"
+                      value="\A(/\*\n \* Hibernate, Relational Persistence for Idiomatic Java\n \*\n \* Copyright \(c\) \d{4}(-\d{4})?, Red Hat(,)? Inc. (and/or its affiliates )?or third-party contributors as\n \* indicated by the @author tags or express copyright attribution\n \* statements applied by the authors. All third-party contributions are\n \* distributed under license by Red Hat(,)? Inc.\n \*\n \* This copyrighted material is made available to anyone wishing to use, modify,\n \* copy, or redistribute it subject to the terms and conditions of the GNU\n \* Lesser General Public License, as published by the Free Software Foundation.\n \*\n \* This program is distributed in the hope that it will be useful,\n \* but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY\n \* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License\n \* for more details.\n \*\n \* You should have received a copy of the GNU Lesser General Public License\n \* along with this distribution; if not, write to:\n \* Free Software Foundation, Inc.\n \* 51 Franklin Street, Fifth Floor\n \* Boston, MA 02110-1301 USA\n \*/)|(/\* ?\n \* Hibernate, Relational Persistence for Idiomatic Java\n \* ?\n \* JBoss, Home of Professional Open Source\n \* Copyright \d{4} Red Hat Inc. and/or its affiliates and other contributors\n \* as indicated by the @authors tag. All rights reserved.\n \* See the copyright.txt in the distribution for a\n \* full listing of individual contributors.\n \*\n \* This copyrighted material is made available to anyone wishing to use,\n \* modify, copy, or redistribute it subject to the terms and conditions\n \* of the GNU Lesser General Public License, v. 2.1.\n \* This program is distributed in the hope that it will be useful, but WITHOUT A\n \* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A\n \* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.\n \* You should have received a copy of the GNU Lesser General Public License,\n \* v.2.1 along with this distribution; if not, write to the Free Software\n \* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,\n \* MA 02110-1301, USA.\n \*/)|(/\*\n \* Hibernate, Relational Persistence for Idiomatic Java\n \*\n \* JBoss, Home of Professional Open Source\n \* Copyright \d{4}(-\d{4})? Red Hat Inc. and/or its affiliates and other contributors\n \* as indicated by the @authors tag. All rights reserved.\n \* See the copyright.txt in the distribution for a\n \* full listing of individual contributors.\n \*\n \* This copyrighted material is made available to anyone wishing to use,\n \* modify, copy, or redistribute it subject to the terms and conditions\n \* of the GNU Lesser General Public License, v. 2.1.\n \* This program is distributed in the hope that it will be useful, but WITHOUT A\n \* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A\n \* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.\n \* You should have received a copy of the GNU Lesser General Public License,\n \* v.2.1 along with this distribution; if not, write to the Free Software\n \* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,\n \* MA 02110-1301, USA.\n \*/)|(/\* ?\n \* JBoss, Home of Professional Open Source\n \* Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors\n \* as indicated by the @authors tag. All rights reserved.\n \* See the copyright.txt in the distribution for a\n \* full listing of individual contributors.\n \*\n \* This copyrighted material is made available to anyone wishing to use,\n \* modify, copy, or redistribute it subject to the terms and conditions\n \* of the GNU Lesser General Public License, v. 2.1.\n \* This program is distributed in the hope that it will be useful, but WITHOUT A\n \* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A\n \* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.\n \* You should have received a copy of the GNU Lesser General Public License,\n \* v.2.1 along with this distribution; if not, write to the Free Software\n \* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,\n \* MA 02110-1301, USA.\n \*/)" />
+            <property name="message" value="Correct header not found" />
+        </module>
+-->
+        <module name="RegexpSinglelineJava">
+            <property name="ignoreComments" value="true" />
+            <property name="format" value="^\t* +\t*\S" />
+            <property name="message" value="Line has leading space characters; indentation should be performed with tabs only." />
+        </module>
 
-    <module name="TreeWalker">
+<!--
+    For now this is disabled to minimize false conflicts with metamodel branch.
+
+        <module name="RegexpSinglelineJava">
+            <property name="ignoreComments" value="true" />
+            <property name="format" value="\s+$" />
+            <property name="message" value="White spaces at the end of line" />
+        </module>
+-->
 
-        <!-- Annotation checks : http://checkstyle.sourceforge.net/config_annotation.html -->
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Annotation checks
+
+            See http://checkstyle.sourceforge.net/config_annotation.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="MissingDeprecated" />
         <module name="MissingOverride" />
         <module name="PackageAnnotation" />
 
-        <!-- Block checks : http://checkstyle.sourceforge.net/config_blocks.html -->
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Block checks
+
+            See http://checkstyle.sourceforge.net/config_blocks.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="AvoidNestedBlocks">
             <property name="allowInSwitchCase" value="true" />
             <property name="severity" value="warning" />
         </module>
         <module name="NeedBraces" />
+        <module name="LeftCurly">
+            <property name="option" value="eol" />
+        </module>
+        <module name="RightCurly">
+            <property name="option" value="alone" />
+        </module>
+
 
-        <!-- Design checks : http://checkstyle.sourceforge.net/config_design.html -->
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Design checks
+
+            See http://checkstyle.sourceforge.net/config_design.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="HideUtilityClassConstructor" />
         <module name="MutableException" />
 
-        <!-- Coding checks : http://checkstyle.sourceforge.net/config_coding.html -->
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Coding checks
+
+            See http://checkstyle.sourceforge.net/config_coding.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="EmptyStatement" />
         <module name="EqualsHashCode" />
         <module name="FinalLocalVariable" />
         <module name="MissingSwitchDefault" />
         <module name="ModifiedControlVariable" />
         <module name="SimplifyBooleanExpression" />
         <module name="SimplifyBooleanReturn" />
         <module name="StringLiteralEquality" />
         <module name="NoFinalizer" />
         <module name="ExplicitInitialization" />
         <module name="MissingSwitchDefault" />
         <module name="DefaultComesLast" />
         <module name="FallThrough" />
         <module name="OneStatementPerLine" />
 
-        <!-- Import checks : http://checkstyle.sourceforge.net/config_imports.html -->
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Import checks
+
+            See http://checkstyle.sourceforge.net/config_imports.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="AvoidStarImport" />
         <module name="RedundantImport" />
         <module name="UnusedImports" />
 
-        <!-- Javadoc checks : http://checkstyle.sourceforge.net/config_javadoc.html -->
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Javadoc checks
+
+            See http://checkstyle.sourceforge.net/config_javadoc.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="JavadocType">
             <property name="scope" value="public"/>
             <property name="allowUnknownTags" value="true" />
         </module>
         <module name="JavadocMethod">
             <property name="scope" value="public" />
             <property name="allowUndeclaredRTE" value="true" />
             <property name="allowMissingPropertyJavadoc" value="true" />
         </module>
         <module name="JavadocVariable">
             <property name="scope" value="public" />
         </module>
         <module name="JavadocStyle">
             <property name="scope" value="public" />
             <property name="checkEmptyJavadoc" value="true" />
+            <property name="checkFirstSentence" value="false" />
         </module>
 
-        <!-- Metric checks : http://checkstyle.sourceforge.net/config_metrics.html -->
-        <module name="BooleanExpressionComplexity" />
 
-        <!-- Misc checks : http://checkstyle.sourceforge.net/config_misc.html-->
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Misc checks
+
+            See http://checkstyle.sourceforge.net/config_misc.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="UpperEll" />
         <module name="ArrayTypeStyle" />
-<!--
-        <module name="FinalParameters">
-            <property name="severity" value="info" />
-        </module>
--->
         <module name="TrailingComment">
             <property name="severity" value="warning" />
         </module>
-<!--
-        <module name="TodoComment">
-            <property name="format" value="[Tt][Oo][Dd][Oo]"/>
-            <property name="severity" value="info" />
-        </module>
--->
-        <!-- Modifier checks : http://checkstyle.sourceforge.net/config_modifier.html -->
+
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Modifier checks
+
+            See http://checkstyle.sourceforge.net/config_modifier.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="ModifierOrder"/>
 
-        <!-- Naming checks : http://checkstyle.sourceforge.net/config_naming.html -->
-        <module name="AbstractClassName" />
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Naming checks
+
+            See http://checkstyle.sourceforge.net/config_naming.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
+        <module name="AbstractClassName">
+            <!-- we are just using this to make sure that classes matching the pattern (Abstract*) have the abstract modifier -->
+            <property name="format" value="^Abstract.*$" />
+            <property name="ignoreName" value="true" />
+        </module>
         <module name="ClassTypeParameterName" />
         <module name="ConstantName">
             <property name="format" value="^[A-Z](_?[A-Z0-9]+)*$|log" />
         </module>
         <module name="LocalFinalVariableName" />
         <module name="LocalVariableName" />
         <module name="MemberName" />
         <module name="MethodName" />
         <module name="MethodTypeParameterName" />
         <module name="PackageName" />
         <module name="ParameterName" />
         <module name="StaticVariableName" />
         <module name="TypeName" />
 
-        <!-- Whitespace checks : http://checkstyle.sourceforge.net/config_whitespace.html -->
+
+        <!--
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+            Whitespace checks
+
+            See http://checkstyle.sourceforge.net/config_whitespace.html
+            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        -->
         <module name="MethodParamPad" />
         <module name="TypecastParenPad" />
+        <module name="ParenPad">
+            <property name="tokens" value="CTOR_CALL, METHOD_CALL, SUPER_CTOR_CALL" />
+            <property name="option" value="space" />
+        </module>
 
     </module>
 
 
-    <!-- Javadoc checks : http://checkstyle.sourceforge.net/config_javadoc.html -->
+    <!--
+        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        Javadoc checks
+
+        See http://checkstyle.sourceforge.net/config_javadoc.html
+        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+    -->
     <module name="JavadocPackage">
         <property name="allowLegacy" value="true" />
     </module>
 
-    <!-- Misc checks : http://checkstyle.sourceforge.net/config_misc.html-->
+
+    <!--
+        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+        Misc checks
+
+        See http://checkstyle.sourceforge.net/config_misc.html
+        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+    -->
     <module name="NewlineAtEndOfFile" />
 
 </module>
diff --git a/shared/config/checkstyle/public_checks.xml b/shared/config/checkstyle/public_checks.xml
deleted file mode 100644
index 8cdad56c57..0000000000
--- a/shared/config/checkstyle/public_checks.xml
+++ /dev/null
@@ -1,145 +0,0 @@
-<?xml version="1.0" encoding="UTF-8"?>
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ Copyright (c) 2013, Red Hat Inc. or third-party contributors as
-  ~ indicated by the @author tags or express copyright attribution
-  ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Inc.
-  ~
-  ~ This copyrighted material is made available to anyone wishing to use, modify,
-  ~ copy, or redistribute it subject to the terms and conditions of the GNU
-  ~ Lesser General Public License, as published by the Free Software Foundation.
-  ~
-  ~ This program is distributed in the hope that it will be useful,
-  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
-  ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
-  ~ for more details.
-  ~
-  ~ You should have received a copy of the GNU Lesser General Public License
-  ~ along with this distribution; if not, write to:
-  ~ Free Software Foundation, Inc.
-  ~ 51 Franklin Street, Fifth Floor
-  ~ Boston, MA  02110-1301  USA
-  -->
-<!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.1//EN" "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">
-
-<module name="Checker">
-
-    <!--
-        This check config file is used as normal part of the build process.  It is intended to make sure that
-        certain base source standards are maintained.  This set of checks is limited explicitly to API sources.
-    -->
-
-    <module name="TreeWalker">
-
-        <!-- Annotation checks : http://checkstyle.sourceforge.net/config_annotation.html -->
-        <module name="MissingDeprecated" />
-        <module name="MissingOverride" />
-        <module name="PackageAnnotation" />
-
-        <!-- Block checks : http://checkstyle.sourceforge.net/config_blocks.html -->
-        <module name="AvoidNestedBlocks">
-            <property name="allowInSwitchCase" value="true" />
-            <property name="severity" value="warning" />
-        </module>
-        <module name="NeedBraces" />
-
-        <!-- Design checks : http://checkstyle.sourceforge.net/config_design.html -->
-        <module name="HideUtilityClassConstructor" />
-        <module name="MutableException" />
-
-        <!-- Coding checks : http://checkstyle.sourceforge.net/config_coding.html -->
-        <module name="EmptyStatement" />
-        <module name="EqualsHashCode" />
-        <module name="FinalLocalVariable" />
-        <module name="MissingSwitchDefault" />
-        <module name="ModifiedControlVariable" />
-        <module name="SimplifyBooleanExpression" />
-        <module name="SimplifyBooleanReturn" />
-        <module name="StringLiteralEquality" />
-        <module name="NoFinalizer" />
-        <module name="ExplicitInitialization" />
-        <module name="MissingSwitchDefault" />
-        <module name="DefaultComesLast" />
-        <module name="FallThrough" />
-        <module name="OneStatementPerLine" />
-
-        <!-- Import checks : http://checkstyle.sourceforge.net/config_imports.html -->
-        <module name="AvoidStarImport" />
-        <module name="RedundantImport" />
-        <module name="UnusedImports" />
-
-        <!-- Javadoc checks : http://checkstyle.sourceforge.net/config_javadoc.html -->
-        <module name="JavadocType">
-            <property name="scope" value="public"/>
-            <property name="allowUnknownTags" value="true" />
-        </module>
-        <module name="JavadocMethod">
-            <property name="scope" value="public" />
-            <property name="allowUndeclaredRTE" value="true" />
-            <property name="allowMissingPropertyJavadoc" value="true" />
-        </module>
-        <module name="JavadocVariable">
-            <property name="scope" value="public" />
-        </module>
-        <module name="JavadocStyle">
-            <property name="scope" value="public" />
-            <property name="checkEmptyJavadoc" value="true" />
-        </module>
-
-        <!-- Metric checks : http://checkstyle.sourceforge.net/config_metrics.html -->
-        <module name="BooleanExpressionComplexity" />
-
-        <!-- Misc checks : http://checkstyle.sourceforge.net/config_misc.html-->
-        <module name="UpperEll" />
-        <module name="ArrayTypeStyle" />
-        <!--
-                <module name="FinalParameters">
-                    <property name="severity" value="info" />
-                </module>
-        -->
-        <module name="TrailingComment">
-            <property name="severity" value="warning" />
-        </module>
-        <!--
-                <module name="TodoComment">
-                    <property name="format" value="[Tt][Oo][Dd][Oo]"/>
-                    <property name="severity" value="info" />
-                </module>
-        -->
-        <!-- Modifier checks : http://checkstyle.sourceforge.net/config_modifier.html -->
-        <module name="ModifierOrder"/>
-
-        <!-- Naming checks : http://checkstyle.sourceforge.net/config_naming.html -->
-        <module name="AbstractClassName" />
-        <module name="ClassTypeParameterName" />
-        <module name="ConstantName">
-            <property name="format" value="^[A-Z](_?[A-Z0-9]+)*$|log" />
-        </module>
-        <module name="LocalFinalVariableName" />
-        <module name="LocalVariableName" />
-        <module name="MemberName" />
-        <module name="MethodName" />
-        <module name="MethodTypeParameterName" />
-        <module name="PackageName" />
-        <module name="ParameterName" />
-        <module name="StaticVariableName" />
-        <module name="TypeName" />
-
-        <!-- Whitespace checks : http://checkstyle.sourceforge.net/config_whitespace.html -->
-        <module name="MethodParamPad" />
-        <module name="TypecastParenPad" />
-
-    </module>
-
-
-    <!-- Javadoc checks : http://checkstyle.sourceforge.net/config_javadoc.html -->
-    <module name="JavadocPackage">
-        <property name="allowLegacy" value="true" />
-    </module>
-
-    <!-- Misc checks : http://checkstyle.sourceforge.net/config_misc.html-->
-    <module name="NewlineAtEndOfFile" />
-
-</module>
diff --git a/shared/config/checkstyle/todo-checks.xml b/shared/config/checkstyle/todo-checks.xml
new file mode 100644
index 0000000000..9ba45b4189
--- /dev/null
+++ b/shared/config/checkstyle/todo-checks.xml
@@ -0,0 +1,15 @@
+<?xml version="1.0" encoding="UTF-8"?>
+
+<!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.1//EN" "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">
+
+<module name="Checker">
+    <!--
+        Used to collect "todo" comments into a single location
+    -->
+    <module name="TreeWalker">
+        <module name="TodoComment">
+            <property name="format" value="[Tt][Oo][Dd][Oo]"/>
+            <property name="severity" value="info" />
+        </module>
+    </module>
+</module>
\ No newline at end of file
