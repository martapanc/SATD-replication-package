diff --git a/build.gradle b/build.gradle
index d170b7e402..431ddbaffd 100644
--- a/build.gradle
+++ b/build.gradle
@@ -1,424 +1,422 @@
 apply plugin: 'eclipse'
 apply plugin: 'idea'
 apply from: "./libraries.gradle"
 
 allprojects {
     repositories {
         mavenCentral()
         mavenLocal()
 
         maven {
             name 'jboss-nexus'
             url "http://repository.jboss.org/nexus/content/groups/public/"
         }
         maven {
             name "jboss-snapshots"
             url "http://snapshots.jboss.org/maven2/"
         }
     }
 }
 
 buildscript {
     repositories {
         mavenCentral()
         mavenLocal()
 		jcenter()
 
         maven {
             name 'jboss-nexus'
             url "http://repository.jboss.org/nexus/content/groups/public/"
         }
         maven {
             name "jboss-snapshots"
             url "http://snapshots.jboss.org/maven2/"
         }
     }
     dependencies {
         classpath 'org.hibernate.build.gradle:gradle-maven-publish-auth:2.0.1'
         classpath 'org.hibernate.build.gradle:hibernate-matrix-testing:1.0.0-SNAPSHOT'
         classpath 'org.hibernate.build.gradle:version-injection-plugin:1.0.0'
     }
 }
 
 ext {
     expectedGradleVersion = '2.2'
     hibernateTargetVersion = '5.0.0-SNAPSHOT'
     javaLanguageLevel = '1.6'
 
     osgiExportVersion = hibernateTargetVersion.replaceAll( "-SNAPSHOT", ".SNAPSHOT" )
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
 // Used in MANIFEST.MF for OSGi Bundles
 def osgiDescription() {
 	// by default just reuse the pomDescription
 	return pomDescription()
 }
 
 
 // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 subprojects { subProject ->
     apply plugin: 'idea'
     apply plugin: 'eclipse'
 
     defaultTasks 'build'
 
     group = 'org.hibernate'
     version = rootProject.hibernateTargetVersion
     ext.exportPackageVersion = rootProject.osgiExportVersion
 
     // minimize changes, at least for now (gradle uses 'build' by default)..
     buildDir = "target"
 
 	if ( subProject.name.startsWith( 'release' ) || subProject.name.startsWith( 'documentation' ) ) {
 		return;
 	}
 
 	// everything below here in the closure applies to java projects
 	apply plugin: 'java'
 	apply plugin: 'maven-publish'
 	apply plugin: 'maven-publish-auth'
 	apply plugin: 'osgi'
 
 	apply from: "${rootProject.projectDir}/source-generation.gradle"
 
 	apply plugin: 'findbugs'
 	apply plugin: 'checkstyle'
 	apply plugin: 'build-dashboard'
 	apply plugin: 'project-report'
 
 	configurations {
 		provided {
 			// todo : need to make sure these are non-exported
 			description = 'Non-exported compile-time dependencies.'
 		}
 		jaxb {
 			description = 'Dependencies for running ant xjc (jaxb class generation)'
 		}
 		configurations {
 			all*.exclude group: 'xml-apis', module: 'xml-apis'
 		}
 		animalSniffer
 		javaApiSignature
 	}
 
 	// appropriately inject the common dependencies into each sub-project
 	dependencies {
 		compile( libraries.logging )
 		compile( libraries.logging_annotations )
 		compile( libraries.logging_processor )
 
 		testCompile( libraries.junit )
 		testCompile( libraries.byteman )
 		testCompile( libraries.byteman_install )
 		testCompile( libraries.byteman_bmunit )
 
 		testRuntime( libraries.log4j )
 		testRuntime( libraries.javassist )
 		testRuntime( libraries.h2 )
 		testRuntime( libraries.woodstox )
 
 		jaxb( libraries.jaxb ){
 			exclude group: "javax.xml.stream"
 		}
 		jaxb( libraries.jaxb2_basics )
 		jaxb( libraries.jaxb2_ant )
 		jaxb( libraries.jaxb2_jaxb )
 		jaxb( libraries.jaxb2_jaxb_xjc )
 
 		javaApiSignature ( libraries.java16_signature )
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
 
 	tasks.withType( JavaCompile.class ).all { task->
 		task.options.compilerArgs += [
 				"-nowarn",
 				"-encoding", "UTF-8",
 				"-source", rootProject.javaLanguageLevel,
 				"-target", rootProject.javaLanguageLevel
 		]
 	}
 
 	jar {
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
 
 			instruction 'Bundle-Vendor', 'Hibernate.org'
 			instruction 'Bundle-Description', subProject.osgiDescription()
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
-		// Not strictly needed but useful to attach a profiler:
-		jvmArgs '-XX:MaxPermSize=256m'
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
 			scopes.COMPILE.plus += [configurations.provided]
 		}
 	}
 
 	eclipse {
 		classpath {
 			plusConfigurations.add( configurations.provided )
 		}
 	}
 
 	// eclipseClasspath will not add sources to classpath unless the dirs actually exist.
 	// TODO: Eclipse's annotation processor handling is also fairly stupid (and completely lacks in the
 	// Gradle plugin).  For now, just compile first in order to get the logging classes.
 	eclipseClasspath.dependsOn generateSources
 
 	if ( subProject.name != 'hibernate-java8' ) {
 		apply plugin: org.hibernate.build.animalsniffer.AnimalSnifferPlugin
 		animalsniffer {
 			signature = "org.codehaus.mojo.signature:java16:+@signature"
 		}
 	}
 
 	// specialized API/SPI checkstyle tasks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	task checkstylePublicSources(type: Checkstyle) {
 		checkstyleClasspath = checkstyleMain.checkstyleClasspath
 		classpath = checkstyleMain.classpath
 		configFile = rootProject.file( 'shared/config/checkstyle/checkstyle.xml' )
 		source subProject.sourceSets.main.java.srcDirs
 		// exclude generated sources
 		exclude '**/generated-src/**'
 		// because cfg package is a mess mainly from annotation stuff
 		exclude '**/org/hibernate/cfg/**'
 		exclude '**/org/hibernate/cfg/*'
 		// because this should only report on api/spi
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
 		sourceSets = [ subProject.sourceSets.main ]
 		configFile = rootProject.file( 'shared/config/checkstyle/checkstyle.xml' )
 		showViolations = false
 		ignoreFailures = true
 	}
 	// exclude generated sources
 	// unfortunately this nice easy approach does not seem to work : http://forums.gradle.org/gradle/topics/specify_excludes_to_checkstyle_task
 	//checkstyleMain.exclude '**/generated-src/**'
 	checkstyleMain.exclude '**/org/hibernate/hql/internal/antlr/**'
 	checkstyleMain.exclude '**/org/hibernate/hql/internal/antlr/*'
 	checkstyleMain.exclude '**/org/hibernate/sql/ordering/antlr/*'
 	checkstyleMain.exclude '**/*_$logger*'
 	checkstyleMain.exclude '**/org/hibernate/internal/jaxb/**'
 	// because cfg package is a mess mainly from annotation stuff
 	checkstyleMain.exclude '**/org/hibernate/cfg/**'
 	checkstyleMain.exclude '**/org/hibernate/cfg/*'
 
 	findbugs {
 		sourceSets = [ subProject.sourceSets.main, subProject.sourceSets.test ]
 		ignoreFailures = true
 	}
 	// exclude generated sources
 	// unfortunately this nice easy approach does not seem to work : http://forums.gradle.org/gradle/topics/specify_excludes_to_checkstyle_task
 	//findbugsMain.exclude '**/generated-src/**'
 	findbugsMain.exclude '**/org/hibernate/hql/internal/antlr/**'
 	findbugsMain.exclude '**/org/hibernate/hql/internal/antlr/*'
 	findbugsMain.exclude '**/org/hibernate/sql/ordering/antlr/*'
 	findbugsMain.exclude '**/*_$logger*'
 	findbugsMain.exclude '**/org/hibernate/internal/jaxb/**'
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	publishing {
 		publications {
 			mavenJava(MavenPublication) {
 				from components.java
 
 				artifact sourcesJar {
 					classifier "sources"
 				}
 
 				pom.withXml {
 					// append additional metadata
 					asNode().children().last() + {
 						resolveStrategy = Closure.DELEGATE_FIRST
 
 						name subProject.pomName()
 						description subProject.pomDescription()
 						url 'http://hibernate.org'
 						organization {
 							name 'Hibernate.org'
 							url 'http://hibernate.org'
 						}
 						issueManagement {
 							system 'jira'
 							url 'https://hibernate.atlassian.net/browse/HHH'
 						}
 						scm {
 							url 'http://github.com/hibernate/hibernate-orm'
 							connection 'scm:git:http://github.com/hibernate/hibernate-orm.git'
 							developerConnection 'scm:git:git@github.com:hibernate/hibernate-orm.git'
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
 							developer {
 								id 'hibernate-team'
 								name 'The Hibernate Development Team'
 								organization 'Hibernate.org'
 								organizationUrl 'http://hibernate.org'
 							}
 						}
 					}
 
 					// TEMPORARY : currently Gradle Publishing feature is exporting dependencies as 'runtime' scope,
 					//      rather than 'compile'; fix that.
 					asNode().dependencies[0].dependency.each {
 						it.scope[0].value = 'compile'
 					}
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
 	}
 
 	model {
 		tasks.generatePomFileForMavenJavaPublication {
 			destination = file( "$project.buildDir/generated-pom.xml" )
 		}
 	}
 
 	task sourcesJar(type: Jar, dependsOn: compileJava) {
 		from sourceSets.main.allSource
 		classifier = 'sources'
 	}
 }
 
 task release(type: Task, dependsOn: 'release:release')
 
 task wrapper(type: Wrapper) {
     gradleVersion = expectedGradleVersion
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
index 9516ec1fa8..faefe33f1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
@@ -1,322 +1,417 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import java.util.List;
+import javax.persistence.AttributeConverter;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.scan.spi.Scanner;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
+import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
+import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.BasicType;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Contract for specifying various overrides to be used in metamodel building.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public interface MetadataBuilder {
 	/**
-	 * Specific the implicit schema name to apply to any unqualified database names
+	 * Specify the implicit schema name to apply to any unqualified database names
 	 *
 	 * @param implicitSchemaName The implicit schema name
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder withImplicitSchemaName(String implicitSchemaName);
+	MetadataBuilder applyImplicitSchemaName(String implicitSchemaName);
 
 	/**
-	 * Specific the implicit catalog name to apply to any unqualified database names
+	 * Specify the implicit catalog name to apply to any unqualified database names
 	 *
 	 * @param implicitCatalogName The implicit catalog name
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder withImplicitCatalogName(String implicitCatalogName);
+	MetadataBuilder applyImplicitCatalogName(String implicitCatalogName);
 
-	public MetadataBuilder with(ImplicitNamingStrategy namingStrategy);
-	public MetadataBuilder with(PhysicalNamingStrategy namingStrategy);
+	/**
+	 * Specify the ImplicitNamingStrategy to use in building the Metadata
+	 *
+	 * @param namingStrategy The ImplicitNamingStrategy to apply
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy);
+
+	/**
+	 * Specify the PhysicalNamingStrategy to use in building the Metadata
+	 *
+	 * @param namingStrategy The PhysicalNamingStrategy to apply
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy);
 
 	/**
 	 * Defines the Hibernate Commons Annotations ReflectionManager to use
 	 *
 	 * @param reflectionManager The ReflectionManager to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
-	 * @deprecated Deprecated (with no current replacement) to indicate that this will
-	 * go away as we migrate away from Hibernate Commons Annotations to Jandex for annotation
-	 * handling and XMl->annotation merging.
+	 * @deprecated Deprecated (with no replacement) to indicate that this will go away as
+	 * we migrate away from Hibernate Commons Annotations to Jandex for annotation handling
+	 * and XMl->annotation merging.
 	 */
 	@Deprecated
-	public MetadataBuilder with(ReflectionManager reflectionManager);
+	MetadataBuilder applyReflectionManager(ReflectionManager reflectionManager);
 
 	/**
 	 * Specify the second-level cache mode to be used.  This is the cache mode in terms of whether or
 	 * not to cache.
 	 *
 	 * @param cacheMode The cache mode.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
-	 * @see #with(org.hibernate.cache.spi.access.AccessType)
+	 * @see #applyAccessType(org.hibernate.cache.spi.access.AccessType)
 	 */
-	public MetadataBuilder with(SharedCacheMode cacheMode);
+	MetadataBuilder applySharedCacheMode(SharedCacheMode cacheMode);
 
 	/**
 	 * Specify the second-level access-type to be used by default for entities and collections that define second-level
 	 * caching, but do not specify a granular access-type.
 	 *
 	 * @param accessType The access-type to use as default.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
-	 * @see #with(javax.persistence.SharedCacheMode)
+	 * @see #applySharedCacheMode(javax.persistence.SharedCacheMode)
 	 */
-	public MetadataBuilder with(AccessType accessType);
+	MetadataBuilder applyAccessType(AccessType accessType);
 
 	/**
 	 * Allows specifying a specific Jandex index to use for reading annotation information.
 	 * <p/>
 	 * It is <i>important</i> to understand that if a Jandex index is passed in, it is expected that
 	 * this Jandex index already contains all entries for all classes.  No additional indexing will be
 	 * done in this case.
 	 *
 	 * @param jandexView The Jandex index to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(IndexView jandexView);
+	MetadataBuilder applyIndexView(IndexView jandexView);
 
 	/**
 	 * Specify the options to be used in performing scanning.
 	 *
 	 * @param scanOptions The scan options.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(ScanOptions scanOptions);
+	MetadataBuilder applyScanOptions(ScanOptions scanOptions);
 
 	/**
 	 * Consider this temporary as discussed on {@link ScanEnvironment}
 	 *
 	 * @param scanEnvironment The environment for scanning
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(ScanEnvironment scanEnvironment);
+	MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment);
 
 	/**
 	 * Specify a particular Scanner instance to use.
 	 *
 	 * @param scanner The scanner to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(Scanner scanner);
+	MetadataBuilder applyScanner(Scanner scanner);
 
 	/**
 	 * Specify a particular ArchiveDescriptorFactory instance to use in scanning.
 	 *
 	 * @param factory The ArchiveDescriptorFactory to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(ArchiveDescriptorFactory factory);
+	MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory);
 
 	/**
-	 * Should the new (well "new" since 3.2) identifier generators be used for
-	 * {@link javax.persistence.GenerationType#SEQUENCE},
-	 * {@link javax.persistence.GenerationType#IDENTITY},
-	 * {@link javax.persistence.GenerationType#TABLE} and
-	 * {@link javax.persistence.GenerationType#AUTO} handling?
+	 * Should we enable support for the "new" (since 3.2) identifier generator mappings for
+	 * handling:<ul>
+	 *     <li>{@link javax.persistence.GenerationType#SEQUENCE}</li>
+	 *     <li>{@link javax.persistence.GenerationType#IDENTITY}</li>
+	 *     <li>{@link javax.persistence.GenerationType#TABLE}</li>
+	 *     <li>{@link javax.persistence.GenerationType#AUTO}</li>
+	 * </ul>
 	 *
-	 * @param enabled {@code true} says to use the new generator mappings; {@code false} says to use the legacy
-	 * generator mappings.
+	 * @param enable {@code true} to enable; {@code false} to disable;don't call for
+	 * default.
 	 *
 	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS
 	 */
-	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);
+	MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enable);
 
 	/**
 	 * Should we process or ignore explicitly defined discriminators in the case
 	 * of joined-subclasses.  The legacy behavior of Hibernate was to ignore the
 	 * discriminator annotations because Hibernate (unlike some providers) does
 	 * not need discriminators to determine the concrete type when it comes to
 	 * joined inheritance.  However, for portability reasons we do now allow using
 	 * explicit discriminators along with joined inheritance.  It is configurable
 	 * though to support legacy apps.
 	 *
 	 * @param enabled Should processing (not ignoring) explicit discriminators be
 	 * enabled?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
-	public MetadataBuilder withExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
+	MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
-	 * Similarly to {@link #withExplicitDiscriminatorsForJoinedSubclassSupport},
+	 * Similarly to {@link #enableExplicitDiscriminatorsForJoinedSubclassSupport},
 	 * but here how should we treat joined inheritance when there is no explicitly
 	 * defined discriminator annotations?  If enabled, we will handle joined
 	 * inheritance with no explicit discriminator annotations by implicitly
 	 * creating one (following the JPA implicit naming rules).
 	 * <p/>
 	 * Again the premise here is JPA portability, bearing in mind that some
 	 * JPA provider need these discriminators.
 	 *
 	 * @param enabled Should we implicitly create discriminator for joined
 	 * inheritance if one is not explicitly mentioned?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
-	public MetadataBuilder withImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
+	MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
 	 * For entities which do not explicitly say, should we force discriminators into
 	 * SQL selects?  The (historical) default is {@code false}
 	 *
 	 * @param supported {@code true} indicates we will force the discriminator into the select;
 	 * {@code false} indicates we will not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
 	 */
-	public MetadataBuilder withImplicitForcingOfDiscriminatorsInSelect(boolean supported);
+	MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported);
 
 	/**
 	 * Should nationalized variants of character data be used in the database types?
 	 *
 	 * For example, should {@code NVARCHAR} be used instead of {@code VARCHAR}?
 	 * {@code NCLOB} instead of {@code CLOB}?
 	 *
 	 * @param enabled {@code true} says to use nationalized variants; {@code false}
 	 * says to use the non-nationalized variants.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
 	 */
-	public MetadataBuilder withNationalizedCharacterData(boolean enabled);
+	MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled);
 
 	/**
 	 * Specify an additional or overridden basic type mapping.
 	 *
 	 * @param type The type addition or override.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(BasicType type);
+	MetadataBuilder applyBasicType(BasicType type);
 
 	/**
 	 * Register an additional or overridden custom type mapping.
 	 *
 	 * @param type The custom type
 	 * @param keys The keys under which to register the custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(UserType type, String[] keys);
+	MetadataBuilder applyBasicType(UserType type, String[] keys);
 
 	/**
 	 * Register an additional or overridden composite custom type mapping.
 	 *
 	 * @param type The composite custom type
 	 * @param keys The keys under which to register the composite custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(CompositeUserType type, String[] keys);
+	MetadataBuilder applyBasicType(CompositeUserType type, String[] keys);
 
 	/**
 	 * Apply an explicit TypeContributor (implicit application via ServiceLoader will still happen too)
 	 *
 	 * @param typeContributor The contributor to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(TypeContributor typeContributor);
+	MetadataBuilder applyTypes(TypeContributor typeContributor);
 
 	/**
 	 * Apply a CacheRegionDefinition to be applied to an entity, collection or query while building the
 	 * Metadata object.
 	 *
 	 * @param cacheRegionDefinition The cache region definition to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(CacheRegionDefinition cacheRegionDefinition);
+	MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition);
 
 	/**
 	 * Apply a ClassLoader for use while building the Metadata.
 	 * <p/>
 	 * Ideally we should avoid accessing ClassLoaders when perform 1st phase of bootstrap.  This
 	 * is a ClassLoader that can be used in cases when we have to.  IN EE managed environments, this
 	 * is the ClassLoader mandated by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}.  This ClassLoader
 	 * is thrown away by the container afterwards.  The idea being that the Class can still be enhanced
 	 * in the application ClassLoader.  In other environments, pass a ClassLoader that performs the
 	 * same function if desired.
 	 *
 	 * @param tempClassLoader ClassLoader for use during building the Metadata
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public MetadataBuilder with(ClassLoader tempClassLoader);
+	MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader);
+
+	/**
+	 * Apply a specific ordering to the processing of sources.  Note that unlike most
+	 * of the methods on this contract that deal with multiple values internally, this
+	 * one *replaces* any already set (its more a setter) instead of adding to.
+	 *
+	 * @param sourceTypes The types, in the order they should be processed
+	 *
+	 * @return {@code this} for method chaining
+	 */
+	MetadataBuilder applySourceProcessOrdering(MetadataSourceType... sourceTypes);
+
+	MetadataBuilder applySqlFunction(String functionName, SQLFunction function);
+
+	MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
-	public MetadataBuilder setSourceProcessOrdering(List<MetadataSourceType> ordering);
+
+	/**
+	 * Adds an AttributeConverter by a AttributeConverterDefinition
+	 *
+	 * @param definition The definition
+	 *
+	 * @return {@code this} for method chaining
+	 */
+	MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition);
+
+	/**
+	 * Adds an AttributeConverter by its Class.
+	 *
+	 * @param attributeConverterClass The AttributeConverter class.
+	 *
+	 * @return {@code this} for method chaining
+	 *
+	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class)
+	 */
+	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass);
+
+	/**
+	 * Adds an AttributeConverter by its Class plus a boolean indicating whether to auto apply it.
+	 *
+	 * @param attributeConverterClass The AttributeConverter class.
+	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
+	 * by its "entity attribute" parameterized type?
+	 *
+	 * @return {@code this} for method chaining
+	 *
+	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class, boolean)
+	 */
+	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply);
+
+	/**
+	 * Adds an AttributeConverter instance.
+	 *
+	 * @param attributeConverter The AttributeConverter instance.
+	 *
+	 * @return {@code this} for method chaining
+	 *
+	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter)
+	 */
+	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter);
+
+	/**
+	 * Adds an AttributeConverter instance, explicitly indicating whether to auto-apply.
+	 *
+	 * @param attributeConverter The AttributeConverter instance.
+	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
+	 * by its "entity attribute" parameterized type?
+	 *
+	 * @return {@code this} for method chaining
+	 *
+	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter, boolean)
+	 */
+	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter, boolean autoApply);
 
 //	/**
 //	 * Specify the resolve to be used in identifying the backing members of a
 //	 * persistent attributes.
 //	 *
 //	 * @param resolver The resolver to use
 //	 *
 //	 * @return {@code this}, for method chaining
 //	 */
 //	public MetadataBuilder with(PersistentAttributeMemberResolver resolver);
 
+
 	/**
 	 * Actually build the metamodel
 	 *
 	 * @return The built metadata.
 	 */
 	public Metadata build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java b/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
index 35d13e8f98..d775d8e7b1 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
@@ -1,619 +1,488 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
-import java.util.Collections;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
-import java.util.Map;
-import java.util.concurrent.ConcurrentHashMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
-import javax.persistence.AttributeConverter;
-import javax.persistence.Converter;
 import javax.xml.transform.dom.DOMSource;
 
-import org.hibernate.AssertionFailure;
 import org.hibernate.boot.archive.spi.InputStreamAccess;
-import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
 import org.hibernate.boot.internal.MetadataBuilderImpl;
 import org.hibernate.boot.jaxb.Origin;
 import org.hibernate.boot.jaxb.SourceType;
 import org.hibernate.boot.jaxb.internal.CacheableFileXmlSource;
 import org.hibernate.boot.jaxb.internal.FileXmlSource;
 import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
 import org.hibernate.boot.jaxb.internal.JarFileEntryXmlSource;
 import org.hibernate.boot.jaxb.internal.JaxpSourceXmlSource;
 import org.hibernate.boot.jaxb.internal.MappingBinder;
 import org.hibernate.boot.jaxb.internal.UrlXmlSource;
 import org.hibernate.boot.jaxb.spi.Binder;
 import org.hibernate.boot.jaxb.spi.Binding;
-import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.cfg.AttributeConverterDefinition;
-import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.SerializationException;
 
 import org.w3c.dom.Document;
 
 /**
  * Entry point into working with sources of metadata information (mapping XML, annotations).   Tell Hibernate
  * about sources and then call {@link #buildMetadata()}, or use {@link #getMetadataBuilder()} to customize
  * how sources are processed (naming strategies, etc).
  *
  * @author Steve Ebersole
  */
 public class MetadataSources implements Serializable {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( MetadataSources.class );
 
 	private final ServiceRegistry serviceRegistry;
 
-	// todo : the boolean here indicates whether or not to perform validation as we load XML documents; devise a way to expose setting that.
-	//		one option is to make this a service
-	//		another is to simply define an AvailableSetting and suck that in here.
+	// NOTE : The boolean here indicates whether or not to perform validation as we load XML documents.
+	// Should we expose this setting?  Disabling would speed up JAXP and JAXB at runtime, but potentially
+	// at the cost of less obvious errors when a document is not valid.
 	private Binder mappingsBinder = new MappingBinder( true );
 
 	private List<Binding> xmlBindings = new ArrayList<Binding>();
 	private LinkedHashSet<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
 	private LinkedHashSet<String> annotatedClassNames = new LinkedHashSet<String>();
 	private LinkedHashSet<String> annotatedPackages = new LinkedHashSet<String>();
 
-	private ConcurrentHashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
-
-	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjectList;
-	private Map<String, SQLFunction> sqlFunctions;
-
 	public MetadataSources() {
 		this( new BootstrapServiceRegistryBuilder().build() );
 	}
 
 	/**
 	 * Create a metadata sources using the specified service registry.
 	 *
 	 * @param serviceRegistry The service registry to use.
 	 */
 	public MetadataSources(ServiceRegistry serviceRegistry) {
 		// service registry really should be either BootstrapServiceRegistry or StandardServiceRegistry type...
 		if ( ! isExpectedServiceRegistryType( serviceRegistry ) ) {
 			LOG.debugf(
 					"Unexpected ServiceRegistry type [%s] encountered during building of MetadataSources; may cause " +
 							"problems later attempting to construct MetadataBuilder",
 					serviceRegistry.getClass().getName()
 			);
 		}
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	protected static boolean isExpectedServiceRegistryType(ServiceRegistry serviceRegistry) {
 		return BootstrapServiceRegistry.class.isInstance( serviceRegistry )
 				|| StandardServiceRegistry.class.isInstance( serviceRegistry );
 	}
 
 	public List<Binding> getXmlBindings() {
 		return xmlBindings;
 	}
 
 	public Collection<String> getAnnotatedPackages() {
 		return annotatedPackages;
 	}
 
 	public Collection<Class<?>> getAnnotatedClasses() {
 		return annotatedClasses;
 	}
 
 	public Collection<String> getAnnotatedClassNames() {
 		return annotatedClassNames;
 	}
 
-	public Collection<AttributeConverterDefinition> getAttributeConverters() {
-		return attributeConverterDefinitionsByClass == null
-				? Collections.<AttributeConverterDefinition>emptyList()
-				: attributeConverterDefinitionsByClass.values();
-	}
-
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
-	public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
-		return auxiliaryDatabaseObjectList == null
-				? Collections.<AuxiliaryDatabaseObject>emptyList()
-				: auxiliaryDatabaseObjectList;
-	}
-
-	public Map<String, SQLFunction> getSqlFunctions() {
-		return sqlFunctions == null ? Collections.<String,SQLFunction>emptyMap() : sqlFunctions;
-	}
-
 	/**
 	 * Get a builder for metadata where non-default options can be specified.
 	 *
 	 * @return The built metadata.
 	 */
 	public MetadataBuilder getMetadataBuilder() {
 		return new MetadataBuilderImpl( this );
 	}
 
 	/**
 	 * Get a builder for metadata where non-default options can be specified.
 	 *
 	 * @return The built metadata.
 	 */
 	public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
 		return new MetadataBuilderImpl( this, serviceRegistry );
 	}
 
 	/**
 	 * Short-hand form of calling {@link #getMetadataBuilder()} and using its
 	 * {@link org.hibernate.boot.MetadataBuilder#build()} method in cases where the application wants
 	 * to accept the defaults.
 	 *
 	 * @return The built metadata.
 	 */
 	public Metadata buildMetadata() {
 		return getMetadataBuilder().build();
 	}
 
 	public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
 		return getMetadataBuilder( serviceRegistry ).build();
 	}
 
 	/**
 	 * Read metadata from the annotations attached to the given class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addAnnotatedClass(Class annotatedClass) {
 		annotatedClasses.add( annotatedClass );
 		return this;
 	}
 
 	/**
 	 * Read metadata from the annotations attached to the given class.  The important
 	 * distinction here is that the {@link Class} will not be accessed until later
 	 * which is important for on-the-fly bytecode-enhancement
 	 *
 	 * @param annotatedClassName The name of a class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addAnnotatedClassName(String annotatedClassName) {
 		annotatedClassNames.add( annotatedClassName );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name without trailing '.', cannot be {@code null}
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addPackage(String packageName) {
 		if ( packageName == null ) {
 			throw new IllegalArgumentException( "The specified package name cannot be null" );
 		}
 		if ( packageName.endsWith( "." ) ) {
 			packageName = packageName.substring( 0, packageName.length() - 1 );
 		}
 		annotatedPackages.add( packageName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup).
 	 *
 	 * @param name The resource name
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addResource(String name) {
 		LOG.tracef( "reading mappings from resource : %s", name );
 
 		final Origin origin = new Origin( SourceType.RESOURCE, name );
 		final URL url = classLoaderService().locateResource( name );
 		if ( url == null ) {
 			throw new MappingNotFoundException( origin );
 		}
 
 		xmlBindings.add( new UrlXmlSource( origin, url ).doBind( mappingsBinder ) );
 
 		return this;
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class named {@code foo.bar.Foo} is
 	 * mapped by a file named {@code foo/bar/Foo.hbm.xml} which can be resolved as a classpath resource.
 	 *
 	 * @param entityClass The mapped class. Cannot be {@code null} null.
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @deprecated hbm.xml is a legacy mapping format now considered deprecated.
 	 */
 	@Deprecated
 	public MetadataSources addClass(Class entityClass) {
 		if ( entityClass == null ) {
 			throw new IllegalArgumentException( "The specified class cannot be null" );
 		}
 		LOG.debugf( "adding resource mappings from class convention : %s", entityClass.getName() );
 		final String mappingResourceName = entityClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		addResource( mappingResourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link java.io.File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addFile(java.io.File)
 	 */
-	public MetadataSources addFile(String path) {;
+	public MetadataSources addFile(String path) {
 		addFile(
 				new Origin( SourceType.FILE, path ),
 				new File( path )
 		);
 		return this;
 	}
 
 	private void addFile(Origin origin, File file) {
 		LOG.tracef( "reading mappings from file : %s", origin.getName() );
 
 		if ( !file.exists() ) {
 			throw new MappingNotFoundException( origin );
 		}
 
 		xmlBindings.add( new FileXmlSource( origin, file ).doBind( mappingsBinder ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param file The reference to the XML file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addFile(File file) {
 		addFile(
 				new Origin( SourceType.FILE, file.getPath() ),
 				file
 		);
 		return this;
 	}
 
 	/**
 	 * See {@link #addCacheableFile(java.io.File)} for description
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link java.io.File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public MetadataSources addCacheableFile(String path) {
 		final Origin origin = new Origin( SourceType.FILE, path );
 		addCacheableFile( origin, new File( path ) );
 		return this;
 	}
 
 	private void addCacheableFile(Origin origin, File file) {
 		xmlBindings.add( new CacheableFileXmlSource( origin, file, false ).doBind( mappingsBinder ) );
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation of the DOM structure of a
 	 * particular mapping.  It is saved from a previous call as a file with the name {@code {xmlFile}.bin}
 	 * where {@code {xmlFile}} is the name of the original mapping file.
 	 * </p>
 	 * If a cached {@code {xmlFile}.bin} exists and is newer than {@code {xmlFile}}, the {@code {xmlFile}.bin}
 	 * file will be read directly. Otherwise {@code {xmlFile}} is read and then serialized to {@code {xmlFile}.bin} for
 	 * use the next time.
 	 *
 	 * @param file The cacheable mapping file to be added, {@code {xmlFile}} in above discussion.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addCacheableFile(File file) {
 		final Origin origin = new Origin( SourceType.FILE, file.getName() );
 		addCacheableFile( origin, file );
 		return this;
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(java.io.File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param file The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws org.hibernate.type.SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws java.io.FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public MetadataSources addCacheableFileStrictly(File file) throws SerializationException, FileNotFoundException {
 		final Origin origin = new Origin( SourceType.FILE, file.getAbsolutePath() );
 		xmlBindings.add( new CacheableFileXmlSource( origin, file, true ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read metadata from an {@link java.io.InputStream} access
 	 *
 	 * @param xmlInputStreamAccess Access to an input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStreamAccess xmlInputStreamAccess) {
 		final Origin origin = new Origin( SourceType.INPUT_STREAM, xmlInputStreamAccess.getStreamName() );
 		InputStream xmlInputStream = xmlInputStreamAccess.accessInputStream();
 		try {
 			xmlBindings.add( new InputStreamXmlSource( origin, xmlInputStream, false ).doBind( mappingsBinder ) );
 		}
 		finally {
 			try {
 				xmlInputStream.close();
 			}
 			catch (IOException e) {
 				LOG.debugf( "Unable to close InputStream obtained from InputStreamAccess : " + xmlInputStreamAccess.getStreamName() );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Read metadata from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStream xmlInputStream) {
 		final Origin origin = new Origin( SourceType.INPUT_STREAM, null );
 		xmlBindings.add( new InputStreamXmlSource( origin, xmlInputStream, false ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a {@link java.net.URL}
 	 *
 	 * @param url The url for the mapping document to be read.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addURL(URL url) {
 		final String urlExternalForm = url.toExternalForm();
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
 		xmlBindings.add( new UrlXmlSource( origin, url ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM {@link org.w3c.dom.Document}
 	 *
 	 * @param document The DOM document
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	@Deprecated
 	public MetadataSources addDocument(Document document) {
 		final Origin origin = new Origin( SourceType.DOM, Origin.UNKNOWN_FILE_PATH );
 		xmlBindings.add( new JaxpSourceXmlSource( origin, new DOMSource( document ) ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read all mappings from a jar file.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addJar(File jar) {
 		LOG.debugf( "Seeking mapping documents in jar file : %s", jar.getName() );
 		final Origin origin = new Origin( SourceType.JAR, jar.getAbsolutePath() );
 		try {
 			JarFile jarFile = new JarFile( jar );
 			try {
 				Enumeration jarEntries = jarFile.entries();
 				while ( jarEntries.hasMoreElements() ) {
 					final ZipEntry zipEntry = (ZipEntry) jarEntries.nextElement();
 					if ( zipEntry.getName().endsWith( ".hbm.xml" ) ) {
 						LOG.tracef( "found mapping document : %s", zipEntry.getName() );
 						xmlBindings.add(
 								new JarFileEntryXmlSource( origin, jarFile, zipEntry ).doBind( mappingsBinder )
 						);
 					}
 				}
 			}
 			finally {
 				try {
 					jarFile.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public MetadataSources addDirectory(File dir) {
 		File[] files = dir.listFiles();
 		if ( files != null && files.length > 0 ) {
 			for ( File file : files ) {
 				if ( file.isDirectory() ) {
 					addDirectory( file );
 				}
 				else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 					addFile( file );
 				}
 			}
 		}
 		return this;
 	}
-
-	/**
-	 * Adds an AttributeConverter by its Class plus a boolean indicating whether to auto apply it.
-	 *
-	 * @param attributeConverterClass The AttributeConverter class.
-	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
-	 * by its "entity attribute" parameterized type?
-	 */
-	public MetadataSources addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
-		addAttributeConverter(
-				instantiateAttributeConverter( attributeConverterClass ),
-				autoApply
-		);
-		return this;
-	}
-
-	private AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> converterClass) {
-		return InFlightMetadataCollectorImpl.instantiateAttributeConverter( converterClass );
-	}
-
-	/**
-	 * Adds an AttributeConverter by its Class.  The indicated class is instantiated and
-	 * passed off to {@link #addAttributeConverter(javax.persistence.AttributeConverter)}.
-	 * See the javadocs on that method in regards to determination of auto-apply.
-	 *
-	 * @param attributeConverterClass The AttributeConverter class.
-	 */
-	public MetadataSources addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
-		addAttributeConverter( instantiateAttributeConverter( attributeConverterClass ) );
-		return this;
-	}
-
-	/**
-	 * Adds an AttributeConverter instance.
-	 * <p/>
-	 * The converter is searched for a {@link Converter} annotation	 to determine whether it
-	 * should be treated as auto-apply.  If the annotation is present, {@link Converter#autoApply()}
-	 * is used to make that determination.  If the annotation is not present, {@code false} is
-	 * assumed.
-	 *
-	 * @param attributeConverter The AttributeConverter instance.
-	 */
-	public MetadataSources addAttributeConverter(AttributeConverter attributeConverter) {
-		addAttributeConverter(
-				InFlightMetadataCollectorImpl.toAttributeConverterDefinition( attributeConverter )
-		);
-		return this;
-	}
-
-	/**
-	 * Adds an AttributeConverter instance, explicitly indicating whether to auto-apply.
-	 *
-	 * @param attributeConverter The AttributeConverter instance.
-	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
-	 * by its "entity attribute" parameterized type?
-	 */
-	public MetadataSources addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
-		addAttributeConverter( new AttributeConverterDefinition( attributeConverter, autoApply ) );
-		return this;
-	}
-
-	public MetadataSources addAttributeConverter(AttributeConverterDefinition definition) {
-		if ( attributeConverterDefinitionsByClass == null ) {
-			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
-		}
-
-		final Object old = attributeConverterDefinitionsByClass.put( definition.getAttributeConverter().getClass(), definition );
-
-		if ( old != null ) {
-			throw new AssertionFailure(
-					String.format(
-							"AttributeConverter class [%s] registered multiple times",
-							definition.getAttributeConverter().getClass()
-					)
-			);
-		}
-		return this;
-	}
-
-	public MetadataSources addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
-		if ( auxiliaryDatabaseObjectList == null ) {
-			auxiliaryDatabaseObjectList = new ArrayList<AuxiliaryDatabaseObject>();
-		}
-		auxiliaryDatabaseObjectList.add( auxiliaryDatabaseObject );
-
-		return this;
-	}
-
-	public void addSqlFunction(String functionName, SQLFunction function) {
-		if ( sqlFunctions == null ) {
-			// need to use this form as we want to specify the "concurrency level" as 1
-			// since only one thread will ever (should) be updating this
-			sqlFunctions = new ConcurrentHashMap<String, SQLFunction>( 16, .75f, 1 );
-		}
-
-		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
-		// just in case a user's customer dialect uses mixed cases.
-		sqlFunctions.put( functionName.toLowerCase(), function );
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
index 98a20d16c9..d04983f754 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
@@ -1,160 +1,162 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * The contract for building a {@link org.hibernate.SessionFactory} given a number of options.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public interface SessionFactoryBuilder {
 	/**
 	 * Names an interceptor to be applied to the SessionFactory, which in turn means it will be used by all
 	 * Sessions unless one is explicitly specified in {@link org.hibernate.SessionBuilder#interceptor}
 	 *
 	 * @param interceptor The interceptor
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#INTERCEPTOR
 	 */
-	public SessionFactoryBuilder with(Interceptor interceptor);
+	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor);
 
 	/**
 	 * Specifies a custom entity dirtiness strategy to be applied to the SessionFactory.  See the contract
 	 * of {@link org.hibernate.CustomEntityDirtinessStrategy} for details.
 	 *
-	 * @param customEntityDirtinessStrategy The custom strategy to be used.
+	 * @param strategy The custom strategy to be used.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#CUSTOM_ENTITY_DIRTINESS_STRATEGY
 	 */
-	public SessionFactoryBuilder with(CustomEntityDirtinessStrategy customEntityDirtinessStrategy);
+	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy);
 
 	/**
 	 * Specifies a strategy for resolving the notion of a "current" tenant-identifier when using multi-tenancy
 	 * together with current sessions
 	 *
-	 * @param currentTenantIdentifierResolver The resolution strategy to use.
+	 * @param resolver The resolution strategy to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER
 	 */
-	public SessionFactoryBuilder with(CurrentTenantIdentifierResolver currentTenantIdentifierResolver);
+	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver);
 
 	/**
 	 * Specifies one or more observers to be applied to the SessionFactory.  Can be called multiple times to add
 	 * additional observers.
 	 *
 	 * @param observers The observers to add
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder add(SessionFactoryObserver... observers);
+	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers);
 
 	/**
 	 * Specifies one or more entity name resolvers to be applied to the SessionFactory (see the {@link org.hibernate.EntityNameResolver}
 	 * contract for more information..  Can be called multiple times to add additional resolvers..
 	 *
 	 * @param entityNameResolvers The entityNameResolvers to add
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder add(EntityNameResolver... entityNameResolvers);
+	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers);
 
 	/**
 	 * Names the {@link org.hibernate.proxy.EntityNotFoundDelegate} to be applied to the SessionFactory.  EntityNotFoundDelegate is a
 	 * strategy that accounts for different exceptions thrown between Hibernate and JPA when an entity cannot be found.
 	 *
 	 * @param entityNotFoundDelegate The delegate/strategy to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate);
+	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate);
 
 	/**
 	 * Specify the EntityTuplizerFactory to use.
 	 *
 	 * @param entityTuplizerFactory The EntityTuplizerFactory to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder with(EntityTuplizerFactory entityTuplizerFactory);
+	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory);
 
 	/**
 	 * Register the default {@link org.hibernate.tuple.entity.EntityTuplizer} to be applied to the SessionFactory.
 	 *
 	 * @param entityMode The entity mode that which this tuplizer will be applied.
 	 * @param tuplizerClass The custom tuplizer class.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder with(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass);
+	public SessionFactoryBuilder applyEntityTuplizer(
+			EntityMode entityMode,
+			Class<? extends EntityTuplizer> tuplizerClass);
 
 	/**
 	 * Apply a Bean Validation ValidatorFactory to the SessionFactory being built.
 	 *
 	 * NOTE : De-typed to avoid hard dependency on Bean Validation jar at runtime.
 	 *
 	 * @param validatorFactory The Bean Validation ValidatorFactory to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder withValidatorFactory(Object validatorFactory);
+	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory);
 
 	/**
 	 * Apply a CDI BeanManager to the SessionFactory being built.
 	 *
 	 * NOTE : De-typed to avoid hard dependency on CDI jar at runtime.
 	 *
 	 * @param beanManager The CDI BeanManager to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder withBeanManager(Object beanManager);
+	public SessionFactoryBuilder applyBeanManager(Object beanManager);
 
-	public SessionFactoryBuilder with(String registrationName, SQLFunction sqlFunction);
+	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction);
 
 	/**
 	 * After all options have been set, build the SessionFactory.
 	 *
 	 * @return The built SessionFactory.
 	 */
 	public SessionFactory build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
index efd3483b82..4c0bfeb44a 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
@@ -1,1199 +1,1201 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.UUID;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.persistence.AttributeConverter;
 import javax.persistence.Converter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
 import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
 import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.ExportableProducer;
 import org.hibernate.boot.model.relational.Schema;
 import org.hibernate.boot.model.source.internal.ConstraintSecondPass;
 import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
 import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
 import org.hibernate.boot.spi.InFlightMetadataCollector;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.boot.spi.MetadataBuildingOptions;
 import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.CopyIdentifierComponentSecondPass;
 import org.hibernate.cfg.CreateKeySecondPass;
 import org.hibernate.cfg.FkSecondPass;
 import org.hibernate.cfg.JPAIndexHolder;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.QuerySecondPass;
 import org.hibernate.cfg.RecoverableException;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.cfg.SecondaryTableSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.type.TypeResolver;
 
 /**
  * The implementation of the in-flight Metadata collector contract.
  *
  * The usage expectation is that this class is used until all Metadata info is
  * collected and then {@link #buildMetadataInstance} is called to generate
  * the complete (and immutable) Metadata object.
  *
  * @author Steve Ebersole
  */
 public class InFlightMetadataCollectorImpl implements InFlightMetadataCollector {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( InFlightMetadataCollectorImpl.class );
 
 	private final MetadataBuildingOptions options;
 	private final TypeResolver typeResolver;
 
 	private final UUID uuid;
 	private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Map<String,PersistentClass> entityBindingMap = new HashMap<String, PersistentClass>();
 	private final Map<String,Collection> collectionBindingMap = new HashMap<String,Collection>();
 
 	private final Map<String, TypeDefinition> typeDefinitionMap = new HashMap<String, TypeDefinition>();
 	private final Map<String, FilterDefinition> filterDefinitionMap = new HashMap<String, FilterDefinition>();
 	private final Map<String, String> imports = new HashMap<String, String>();
 
 	private Database database;
 
 	private final Map<String, NamedQueryDefinition> namedQueryMap = new HashMap<String, NamedQueryDefinition>();
 	private final Map<String, NamedSQLQueryDefinition> namedNativeQueryMap = new HashMap<String, NamedSQLQueryDefinition>();
 	private final Map<String, NamedProcedureCallDefinition> namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>();
 	private final Map<String, ResultSetMappingDefinition> sqlResultSetMappingMap = new HashMap<String, ResultSetMappingDefinition>();
 
 	private final Map<String, NamedEntityGraphDefinition> namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
 	private final Map<String, FetchProfile> fetchProfileMap = new HashMap<String, FetchProfile>();
 	private final Map<String, IdentifierGeneratorDefinition> idGeneratorDefinitionMap = new HashMap<String, IdentifierGeneratorDefinition>();
 
 	private Map<Class, AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 	private Map<String, SQLFunction> sqlFunctionMap;
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// All the annotation-processing-specific state :(
 	private final Set<String> defaultIdentifierGeneratorNames = new HashSet<String>();
 	private final Set<String> defaultNamedQueryNames = new HashSet<String>();
 	private final Set<String> defaultNamedNativeQueryNames = new HashSet<String>();
 	private final Set<String> defaultSqlResultSetMappingNames = new HashSet<String>();
 	private final Set<String> defaultNamedProcedureNames = new HashSet<String>();
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private Map<Class, MappedSuperclass> mappedSuperClasses;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Set<DelayedPropertyReferenceHandler> delayedPropertyReferenceHandlers;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
 
 	public InFlightMetadataCollectorImpl(
 			MetadataBuildingOptions options,
 			MetadataSources sources,
 			TypeResolver typeResolver) {
 		this.uuid = UUID.randomUUID();
 		this.options = options;
 		this.typeResolver = typeResolver;
 
 		this.identifierGeneratorFactory = options.getServiceRegistry().getService( MutableIdentifierGeneratorFactory.class );
 
-		for ( AttributeConverterDefinition attributeConverterDefinition : sources.getAttributeConverters() ) {
+		for ( AttributeConverterDefinition attributeConverterDefinition : options.getAttributeConverters() ) {
 			addAttributeConverter( attributeConverterDefinition );
 		}
 
-		for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : sources.getSqlFunctions().entrySet() ) {
+		for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : options.getSqlFunctions().entrySet() ) {
 			if ( sqlFunctionMap == null ) {
+				// we need this to be a ConcurrentHashMap for the one we ultimately pass along to the SF
+				// but is this the reference that gets passed along?
 				sqlFunctionMap = new ConcurrentHashMap<String, SQLFunction>( 16, .75f, 1 );
 			}
 			sqlFunctionMap.put( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 		}
 
-		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : sources.getAuxiliaryDatabaseObjectList() ) {
+		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : options.getAuxiliaryDatabaseObjectList() ) {
 			getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 
 	}
 
 	@Override
 	public UUID getUUID() {
 		return null;
 	}
 
 	@Override
 	public MetadataBuildingOptions getMetadataBuildingOptions() {
 		return options;
 	}
 
 	@Override
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public Database getDatabase() {
 		// important to delay this instantiation until as late as possible.
 		if ( database == null ) {
 			this.database = new Database( options );
 		}
 		return database;
 	}
 
 	@Override
 	public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
 		throw new UnsupportedOperationException( "#buildNamedQueryRepository should not be called on InFlightMetadataCollector" );
 	}
 
 	@Override
 	public Map<String, SQLFunction> getSqlFunctionMap() {
 		return sqlFunctionMap;
 	}
 
 	@Override
 	public void validate() throws MappingException {
 		// nothing to do
 	}
 
 	@Override
 	public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
 		return new HashSet<MappedSuperclass>( mappedSuperClasses.values() );
 	}
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		throw new UnsupportedOperationException(
 				"You should not be building a SessionFactory from an in-flight metadata collector; and of course " +
 						"we should better segment this in the API :)"
 		);
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		throw new UnsupportedOperationException(
 				"You should not be building a SessionFactory from an in-flight metadata collector; and of course " +
 						"we should better segment this in the API :)"
 		);
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Entity handling
 
 	@Override
 	public java.util.Collection<PersistentClass> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	@Override
 	public Map<String, PersistentClass> getEntityBindingMap() {
 		return entityBindingMap;
 	}
 
 	@Override
 	public PersistentClass getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException {
 		final String entityName = persistentClass.getEntityName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, persistentClass );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Collection handling
 
 	@Override
 	public java.util.Collection<Collection> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	@Override
 	public Collection getCollectionBinding(String role) {
 		return collectionBindingMap.get( role );
 	}
 
 	@Override
 	public void addCollectionBinding(Collection collection) throws DuplicateMappingException {
 		final String collectionRole = collection.getRole();
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.COLLECTION, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, collection );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Hibernate Type handling
 
 	@Override
 	public TypeDefinition getTypeDefinition(String registrationKey) {
 		return typeDefinitionMap.get( registrationKey );
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDefinition typeDefinition) {
 		if ( typeDefinition == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 
 		// Need to register both by name and registration keys.
 		if ( !StringHelper.isEmpty( typeDefinition.getName() ) ) {
 			addTypeDefinition( typeDefinition.getName(), typeDefinition );
 		}
 
 		if ( typeDefinition.getRegistrationKeys() != null ) {
 			for ( String registrationKey : typeDefinition.getRegistrationKeys() ) {
 				addTypeDefinition( registrationKey, typeDefinition );
 			}
 		}
 	}
 
 	private void addTypeDefinition(String registrationKey, TypeDefinition typeDefinition) {
 		final TypeDefinition previous = typeDefinitionMap.put(
 				registrationKey, typeDefinition );
 		if ( previous != null ) {
 			log.debugf(
 					"Duplicate typedef name [%s] now -> %s",
 					registrationKey,
 					typeDefinition.getTypeImplementorClass().getName()
 			);
 		}
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// attribute converters
 
 	@Override
 	public void addAttributeConverter(AttributeConverterDefinition definition) {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
 		}
 
 		final Object old = attributeConverterDefinitionsByClass.put(
 				definition.getAttributeConverter().getClass(),
 				definition
 		);
 
 		if ( old != null ) {
 			throw new AssertionFailure(
 					String.format(
 							Locale.ENGLISH,
 							"AttributeConverter class [%s] registered multiple times",
 							definition.getAttributeConverter().getClass()
 					)
 			);
 		}
 	}
 
 	public static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
 		try {
 			return attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]",
 					e
 			);
 		}
 	}
 
 	public static AttributeConverterDefinition toAttributeConverterDefinition(AttributeConverter attributeConverter) {
 		boolean autoApply = false;
 		Converter converterAnnotation = attributeConverter.getClass().getAnnotation( Converter.class );
 		if ( converterAnnotation != null ) {
 			autoApply = converterAnnotation.autoApply();
 		}
 
 		return new AttributeConverterDefinition( attributeConverter, autoApply );
 	}
 
 	@Override
 	public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
 		addAttributeConverter(
 				toAttributeConverterDefinition(
 						instantiateAttributeConverter( converterClass )
 				)
 		);
 	}
 
 	@Override
 	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			return Collections.emptyList();
 		}
 		return attributeConverterDefinitionsByClass.values();
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// filter definitions
 
 	@Override
 	public Map<String, FilterDefinition> getFilterDefinitions() {
 		return filterDefinitionMap;
 	}
 
 	@Override
 	public FilterDefinition getFilterDefinition(String name) {
 		return filterDefinitionMap.get( name );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		if ( filterDefinition == null || filterDefinition.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + filterDefinition );
 		}
 		filterDefinitionMap.put( filterDefinition.getFilterName(), filterDefinition );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// fetch profiles
 
 	@Override
 	public java.util.Collection<FetchProfile> getFetchProfiles() {
 		return fetchProfileMap.values();
 	}
 
 	@Override
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfileMap.get( name );
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		FetchProfile old = fetchProfileMap.put( profile.getName(), profile );
 		if ( old != null ) {
 			log.warn( "Duplicated fetch profile with same name [" + profile.getName() + "] found." );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// identifier generators
 
 	@Override
 	public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGeneratorDefinitionMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<Table> collectTableMappings() {
 		ArrayList<Table> tables = new ArrayList<Table>();
 		for ( Schema schema : getDatabase().getSchemas() ) {
 			tables.addAll( schema.getTables() );
 		}
 		return tables;
 	}
 
 	@Override
 	public void addIdentifierGenerator(IdentifierGeneratorDefinition generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 
 		if ( defaultIdentifierGeneratorNames.contains( generator.getName() ) ) {
 			return;
 		}
 
 		final IdentifierGeneratorDefinition old = idGeneratorDefinitionMap.put( generator.getName(), generator );
 		if ( old != null ) {
 			log.duplicateGeneratorName( old.getName() );
 		}
 	}
 
 	@Override
 	public void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generator) {
 		this.addIdentifierGenerator( generator );
 		defaultIdentifierGeneratorNames.add( generator.getName() );
 	}
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named EntityGraph handling
 
 	@Override
 	public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
 		return namedEntityGraphMap.get( name );
 	}
 
 	@Override
 	public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
 		return namedEntityGraphMap;
 	}
 
 	@Override
 	public void addNamedEntityGraph(NamedEntityGraphDefinition definition) {
 		final String name = definition.getRegisteredName();
 		final NamedEntityGraphDefinition previous = namedEntityGraphMap.put( name, definition );
 		if ( previous != null ) {
 			throw new DuplicateMappingException(
 					DuplicateMappingException.Type.NAMED_ENTITY_GRAPH, name );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named query handling
 
 	public NamedQueryDefinition getNamedQueryDefinition(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryMap.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 
 		if ( defaultNamedQueryNames.contains( def.getName() ) ) {
 			return;
 		}
 
 		applyNamedQuery( def.getName(), def );
 	}
 
 	private void applyNamedQuery(String name, NamedQueryDefinition query) {
 		checkQueryName( name );
 		namedQueryMap.put( name.intern(), query );
 	}
 
 	private void checkQueryName(String name) throws DuplicateMappingException {
 		if ( namedQueryMap.containsKey( name ) || namedNativeQueryMap.containsKey( name ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.QUERY, name );
 		}
 	}
 
 	@Override
 	public void addDefaultQuery(NamedQueryDefinition queryDefinition) {
 		applyNamedQuery( queryDefinition.getName(), queryDefinition );
 		defaultNamedQueryNames.add( queryDefinition.getName() );
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named native-query handling
 
 	@Override
 	public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
 		return namedNativeQueryMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryMap.values();
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named native query definition object is null" );
 		}
 		if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition name is null: " + def.getQueryString() );
 		}
 
 		if ( defaultNamedNativeQueryNames.contains( def.getName() ) ) {
 			return;
 		}
 
 		applyNamedNativeQuery( def.getName(), def );
 	}
 
 	private void applyNamedNativeQuery(String name, NamedSQLQueryDefinition query) {
 		checkQueryName( name );
 		namedNativeQueryMap.put( name.intern(), query );
 	}
 
 	@Override
 	public void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query) {
 		applyNamedNativeQuery( query.getName(), query );
 		defaultNamedNativeQueryNames.add( query.getName() );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named stored-procedure handling
 
 	@Override
 	public java.util.Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
 		return namedProcedureCallMap.values();
 	}
 
 	@Override
 	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
 		if ( definition == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 
 		final String name = definition.getRegisteredName();
 
 		if ( defaultNamedProcedureNames.contains( name ) ) {
 			return;
 		}
 
 		final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
 		if ( previous != null ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.PROCEDURE, name );
 		}
 	}
 
 	@Override
 	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
 		addNamedProcedureCallDefinition( definition );
 		defaultNamedProcedureNames.add( definition.getRegisteredName() );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// result-set mapping handling
 
 	@Override
 	public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return sqlResultSetMappingMap;
 	}
 
 	@Override
 	public ResultSetMappingDefinition getResultSetMapping(String name) {
 		return sqlResultSetMappingMap.get( name );
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null ) {
 			throw new IllegalArgumentException( "Result-set mapping was null" );
 		}
 
 		final String name = resultSetMappingDefinition.getName();
 		if ( name == null ) {
 			throw new IllegalArgumentException( "Result-set mapping name is null: " + resultSetMappingDefinition );
 		}
 
 		if ( defaultSqlResultSetMappingNames.contains( name ) ) {
 			return;
 		}
 
 		applyResultSetMapping( resultSetMappingDefinition );
 	}
 
 	public void applyResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		final ResultSetMappingDefinition old = sqlResultSetMappingMap.put(
 				resultSetMappingDefinition.getName(),
 				resultSetMappingDefinition
 		);
 		if ( old != null ) {
 			throw new DuplicateMappingException(
 					DuplicateMappingException.Type.RESULT_SET_MAPPING,
 					resultSetMappingDefinition.getName()
 			);
 		}
 	}
 
 	@Override
 	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 		final String name = definition.getName();
 		if ( !defaultSqlResultSetMappingNames.contains( name ) && sqlResultSetMappingMap.containsKey( name ) ) {
 			sqlResultSetMappingMap.remove( name );
 		}
 		applyResultSetMapping( definition );
 		defaultSqlResultSetMappingNames.add( name );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// imports
 
 	@Override
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	@Override
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		log.tracev( "Import: {0} -> {1}", importName, entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			log.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Table handling
 
 	@Override
 	public Table addTable(
 			String schemaName,
 			String catalogName,
 			String name,
 			String subselectFragment,
 			boolean isAbstract) {
 		final Schema schema = getDatabase().locateSchema(
 				getDatabase().toIdentifier( catalogName ),
 				getDatabase().toIdentifier( schemaName )
 		);
 
 		// annotation binding depends on the "table name" for @Subselect bindings
 		// being set into the generated table (mainly to avoid later NPE), but for now we need to keep that :(
 		final Identifier logicalName;
 		if ( name != null ) {
 			logicalName = getDatabase().toIdentifier( name );
 		}
 		else {
 			logicalName = null;
 		}
 
 		if ( subselectFragment != null ) {
 			return new Table( schema, logicalName, subselectFragment, isAbstract );
 		}
 		else {
 			Table table = schema.locateTable( logicalName );
 			if ( table != null ) {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 				return table;
 			}
 			return schema.createTable( logicalName, isAbstract );
 		}
 	}
 
 	@Override
 	public Table addDenormalizedTable(
 			String schemaName,
 			String catalogName,
 			String name,
 			boolean isAbstract,
 			String subselectFragment,
 			Table includedTable) throws DuplicateMappingException {
 		final Schema schema = getDatabase().locateSchema(
 				getDatabase().toIdentifier( catalogName ),
 				getDatabase().toIdentifier( schemaName )
 		);
 
 		// annotation binding depends on the "table name" for @Subselect bindings
 		// being set into the generated table (mainly to avoid later NPE), but for now we need to keep that :(
 		final Identifier logicalName;
 		if ( name != null ) {
 			logicalName = getDatabase().toIdentifier( name );
 		}
 		else {
 			logicalName = null;
 		}
 
 		if ( subselectFragment != null ) {
 			return new DenormalizedTable( schema, logicalName, subselectFragment, isAbstract, includedTable );
 		}
 		else {
 			Table table = schema.locateTable( logicalName );
 			if ( table != null ) {
 				throw new DuplicateMappingException( DuplicateMappingException.Type.TABLE, logicalName.toString() );
 			}
 			else {
 				table = schema.createDenormalizedTable( logicalName, isAbstract, includedTable );
 			}
 			return table;
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Mapping impl
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
 		if ( pc == null ) {
 			throw new MappingException( "persistent class not known: " + entityName );
 		}
 		return pc.getIdentifier().getType();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
 		if ( pc == null ) {
 			throw new MappingException( "persistent class not known: " + entityName );
 		}
 		if ( !pc.hasIdentifierProperty() ) {
 			return null;
 		}
 		return pc.getIdentifierProperty().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
 		if ( pc == null ) {
 			throw new MappingException( "persistent class not known: " + entityName );
 		}
 		Property prop = pc.getReferencedProperty( propertyName );
 		if ( prop == null ) {
 			throw new MappingException(
 					"property not known: " +
 							entityName + '.' + propertyName
 			);
 		}
 		return prop.getType();
 	}
 
 
 	private Map<Identifier,Identifier> logicalToPhysicalTableNameMap = new HashMap<Identifier, Identifier>();
 	private Map<Identifier,Identifier> physicalToLogicalTableNameMap = new HashMap<Identifier, Identifier>();
 
 	@Override
 	public void addTableNameBinding(Identifier logicalName, Table table) {
 		logicalToPhysicalTableNameMap.put( logicalName, table.getNameIdentifier() );
 		physicalToLogicalTableNameMap.put( table.getNameIdentifier(), logicalName );
 	}
 
 	@Override
 	public void addTableNameBinding(String schema, String catalog, String logicalName, String realTableName, Table denormalizedSuperTable) {
 		final Identifier logicalNameIdentifier = getDatabase().toIdentifier( logicalName );
 		final Identifier physicalNameIdentifier = getDatabase().toIdentifier( realTableName );
 
 		logicalToPhysicalTableNameMap.put( logicalNameIdentifier, physicalNameIdentifier );
 		physicalToLogicalTableNameMap.put( physicalNameIdentifier, logicalNameIdentifier );
 	}
 
 	@Override
 	public String getLogicalTableName(Table ownerTable) {
 		final Identifier logicalName = physicalToLogicalTableNameMap.get( ownerTable.getNameIdentifier() );
 		if ( logicalName == null ) {
 			throw new MappingException( "Unable to find physical table: " + ownerTable.getName() );
 		}
 		return logicalName.render();
 	}
 
 	@Override
 	public String getPhysicalTableName(Identifier logicalName) {
 		final Identifier physicalName = logicalToPhysicalTableNameMap.get( logicalName );
 		return physicalName == null ? null : physicalName.render();
 	}
 
 	@Override
 	public String getPhysicalTableName(String logicalName) {
 		return getPhysicalTableName( getDatabase().toIdentifier( logicalName ) );
 	}
 
 	/**
 	 * Internal struct used to maintain xref between physical and logical column
 	 * names for a table.  Mainly this is used to ensure that the defined NamingStrategy
 	 * is not creating duplicate column names.
 	 */
 	private class TableColumnNameBinding implements Serializable {
 		private final String tableName;
 		private Map<Identifier, String> logicalToPhysical = new HashMap<Identifier,String>();
 		private Map<String, Identifier> physicalToLogical = new HashMap<String,Identifier>();
 
 		private TableColumnNameBinding(String tableName) {
 			this.tableName = tableName;
 		}
 
 		public void addBinding(Identifier logicalName, Column physicalColumn) {
 			final String physicalNameString = physicalColumn.getQuotedName( getDatabase().getJdbcEnvironment().getDialect() );
 
 			bindLogicalToPhysical( logicalName, physicalNameString );
 			bindPhysicalToLogical( logicalName, physicalNameString );
 		}
 
 		private void bindLogicalToPhysical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
 			final String existingPhysicalNameMapping = logicalToPhysical.put( logicalName, physicalName );
 			if ( existingPhysicalNameMapping != null ) {
 				final boolean areSame = logicalName.isQuoted()
 						? physicalName.equals( existingPhysicalNameMapping )
 						: physicalName.equalsIgnoreCase( existingPhysicalNameMapping );
 				if ( !areSame ) {
 					throw new DuplicateMappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Table [%s] contains logical column name [%s] referring to multiple physical " +
 											"column names: [%s], [%s]",
 									tableName,
 									logicalName,
 									existingPhysicalNameMapping,
 									physicalName
 							),
 							DuplicateMappingException.Type.COLUMN_BINDING,
 							tableName + "." + logicalName
 					);
 				}
 			}
 		}
 
 		private void bindPhysicalToLogical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
 			final Identifier existingLogicalName = physicalToLogical.put( physicalName, logicalName );
 			if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 				throw new DuplicateMappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Table [%s] contains physical column name [%s] referred to by multiple physical " +
 										"column names: [%s], [%s]",
 								tableName,
 								physicalName,
 								logicalName,
 								existingLogicalName
 						),
 						DuplicateMappingException.Type.COLUMN_BINDING,
 						tableName + "." + physicalName
 				);
 			}
 		}
 	}
 
 	private Map<Table,TableColumnNameBinding> columnNameBindingByTableMap;
 
 	@Override
 	public void addColumnNameBinding(Table table, String logicalName, Column column) throws DuplicateMappingException {
 		addColumnNameBinding( table, getDatabase().toIdentifier( logicalName ), column );
 	}
 
 	@Override
 	public void addColumnNameBinding(Table table, Identifier logicalName, Column column) throws DuplicateMappingException {
 		TableColumnNameBinding binding = null;
 
 		if ( columnNameBindingByTableMap == null ) {
 			columnNameBindingByTableMap = new HashMap<Table, TableColumnNameBinding>();
 		}
 		else {
 			binding = columnNameBindingByTableMap.get( table );
 		}
 
 		if ( binding == null ) {
 			binding = new TableColumnNameBinding( table.getName() );
 			columnNameBindingByTableMap.put( table, binding );
 		}
 
 		binding.addBinding( logicalName, column );
 	}
 
 	@Override
 	public String getPhysicalColumnName(Table table, String logicalName) throws MappingException {
 		return getPhysicalColumnName( table, getDatabase().toIdentifier( logicalName ) );
 	}
 
 	@Override
 	public String getPhysicalColumnName(Table table, Identifier logicalName) throws MappingException {
 		if ( logicalName == null ) {
 			throw new MappingException( "Logical column name cannot be null" );
 		}
 
 		Table currentTable = table;
 		String physicalName = null;
 
 		while ( currentTable != null ) {
 			final TableColumnNameBinding binding = columnNameBindingByTableMap.get( currentTable );
 			if ( binding != null ) {
 				physicalName = binding.logicalToPhysical.get( logicalName );
 				if ( physicalName != null ) {
 					break;
 				}
 			}
 
 			if ( DenormalizedTable.class.isInstance( currentTable ) ) {
 				currentTable = ( (DenormalizedTable) currentTable ).getIncludedTable();
 			}
 			else {
 				currentTable = null;
 			}
 		}
 
 		if ( physicalName == null ) {
 			throw new MappingException(
 					"Unable to find column with logical name " + logicalName.render() + " in table " + table.getName()
 			);
 		}
 		return physicalName;
 	}
 
 	@Override
 	public String getLogicalColumnName(Table table, String physicalName) throws MappingException {
 		return getLogicalColumnName( table, getDatabase().toIdentifier( physicalName ) );
 	}
 
 
 	@Override
 	public String getLogicalColumnName(Table table, Identifier physicalName) throws MappingException {
 		final String physicalNameString = physicalName.render( getDatabase().getJdbcEnvironment().getDialect() );
 		Identifier logicalName = null;
 
 		Table currentTable = table;
 		while ( currentTable != null ) {
 			final TableColumnNameBinding binding = columnNameBindingByTableMap.get( currentTable );
 
 			if ( binding != null ) {
 				logicalName = binding.physicalToLogical.get( physicalNameString );
 				if ( logicalName != null ) {
 					break;
 				}
 			}
 
 			if ( DenormalizedTable.class.isInstance( currentTable ) ) {
 				currentTable = ( (DenormalizedTable) currentTable ).getIncludedTable();
 			}
 			else {
 				currentTable = null;
 			}
 		}
 
 		if ( logicalName == null ) {
 			throw new MappingException(
 					"Unable to find column with physical name " + physicalNameString + " in table " + table.getName()
 			);
 		}
 		return logicalName.render();
 	}
 
 	@Override
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 	}
 
 	private final Map<String,AnnotatedClassType> annotatedClassTypeMap = new HashMap<String, AnnotatedClassType>();
 
 	@Override
 	public AnnotatedClassType getClassType(XClass clazz) {
 		AnnotatedClassType type = annotatedClassTypeMap.get( clazz.getName() );
 		if ( type == null ) {
 			return addClassType( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	@Override
 	public AnnotatedClassType addClassType(XClass clazz) {
 		AnnotatedClassType type;
 		if ( clazz.isAnnotationPresent( Entity.class ) ) {
 			type = AnnotatedClassType.ENTITY;
 		}
 		else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 			type = AnnotatedClassType.EMBEDDABLE;
 		}
 		else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 			type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 		}
 		else {
 			type = AnnotatedClassType.NONE;
 		}
 		annotatedClassTypeMap.put( clazz.getName(), type );
 		return type;
 	}
 
 	@Override
 	public void addAnyMetaDef(AnyMetaDef defAnn) {
 		if ( anyMetaDefs == null ) {
 			anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		}
 		else {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 		}
 
 		anyMetaDefs.put( defAnn.name(), defAnn );
 	}
 
 	@Override
 	public AnyMetaDef getAnyMetaDef(String name) {
 		if ( anyMetaDefs == null ) {
 			return null;
 		}
 		return anyMetaDefs.get( name );
 	}
 
 
 	@Override
 	public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 		if ( mappedSuperClasses == null ) {
 			mappedSuperClasses = new HashMap<Class, MappedSuperclass>();
 		}
 		mappedSuperClasses.put( type, mappedSuperclass );
 	}
 
 	@Override
 	public MappedSuperclass getMappedSuperclass(Class type) {
 		if ( mappedSuperClasses == null ) {
 			return null;
 		}
 		return mappedSuperClasses.get( type );
 	}
 
 	@Override
 	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			return null;
 		}
 
 		final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		return map == null ? null : map.get( propertyName );
 	}
 
 	@Override
 	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithMapsId.put( entityType, map );
 		}
 		map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 	}
 
 	@Override
 	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithMapsId.put( entityType, map );
 		}
 		map.put( mapsIdValue, property );
 	}
 
 	@Override
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 		if ( propertiesAnnotatedWithIdAndToOne == null ) {
 			return null;
 		}
 
 		final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 		return map == null ? null : map.get( propertyName );
 	}
 
 	@Override
 	public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 		if ( propertiesAnnotatedWithIdAndToOne == null ) {
 			propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 		}
 		map.put( property.getPropertyName(), property );
 	}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuilderImpl.java
index fdee0b3958..3a29efc62f 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuilderImpl.java
@@ -1,776 +1,882 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.util.ArrayList;
 import java.util.Arrays;
+import java.util.Collections;
+import java.util.HashMap;
 import java.util.List;
+import java.util.Map;
+import javax.persistence.AttributeConverter;
 import javax.persistence.SharedCacheMode;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.archive.scan.internal.StandardScanOptions;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.scan.spi.Scanner;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.cfgxml.spi.MappingReference;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
+import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MappingDefaults;
 import org.hibernate.boot.spi.MetadataBuilderContributor;
 import org.hibernate.boot.spi.MetadataBuildingOptions;
 import org.hibernate.boot.spi.MetadataSourcesContributor;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.MetadataSourceType;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
+import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.CompositeCustomType;
 import org.hibernate.type.CustomType;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import org.jboss.jandex.IndexView;
 
 import static org.hibernate.internal.log.DeprecationLogger.DEPRECATION_LOGGER;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataBuilderImpl implements MetadataBuilder, TypeContributions {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( MetadataBuilderImpl.class );
 
 	private final MetadataSources sources;
 	private final MetadataBuildingOptionsImpl options;
 
 	public MetadataBuilderImpl(MetadataSources sources) {
 		this(
 				sources,
 				getStandardServiceRegistry( sources.getServiceRegistry() )
 		);
 	}
 
 	private static StandardServiceRegistry getStandardServiceRegistry(ServiceRegistry serviceRegistry) {
 		if ( serviceRegistry == null ) {
 			throw new HibernateException( "ServiceRegistry passed to MetadataBuilder cannot be null" );
 		}
 
 		if ( StandardServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			return ( StandardServiceRegistry ) serviceRegistry;
 		}
 		else if ( BootstrapServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			log.debugf(
 					"ServiceRegistry passed to MetadataBuilder was a BootstrapServiceRegistry; this likely wont end well" +
 							"if attempt is made to build SessionFactory"
 			);
 			return new StandardServiceRegistryBuilder( (BootstrapServiceRegistry) serviceRegistry ).build();
 		}
 		else {
 			throw new HibernateException(
 					String.format(
 							"Unexpected type of ServiceRegistry [%s] encountered in attempt to build MetadataBuilder",
 							serviceRegistry.getClass().getName()
 					)
 			);
 		}
 	}
 
 	public MetadataBuilderImpl(MetadataSources sources, StandardServiceRegistry serviceRegistry) {
 		this.sources = sources;
 		this.options = new MetadataBuildingOptionsImpl( serviceRegistry );
 
 		for ( MetadataSourcesContributor contributor :
 				sources.getServiceRegistry()
 						.getService( ClassLoaderService.class )
 						.loadJavaServices( MetadataSourcesContributor.class ) ) {
 			contributor.contribute( sources );
 		}
 
 		applyCfgXmlValues( serviceRegistry.getService( CfgXmlAccessService.class ) );
 
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		for ( MetadataBuilderContributor contributor : classLoaderService.loadJavaServices( MetadataBuilderContributor.class ) ) {
 			contributor.contribute( this );
 		}
 	}
 
 	private void applyCfgXmlValues(CfgXmlAccessService service) {
 		final LoadedConfig aggregatedConfig = service.getAggregatedConfig();
 		if ( aggregatedConfig == null ) {
 			return;
 		}
 
 		for ( CacheRegionDefinition cacheRegionDefinition : aggregatedConfig.getCacheRegionDefinitions() ) {
-			with( cacheRegionDefinition );
+			applyCacheRegionDefinition( cacheRegionDefinition );
 		}
 	}
 
 	@Override
-	public MetadataBuilder withImplicitSchemaName(String implicitSchemaName) {
+	public MetadataBuilder applyImplicitSchemaName(String implicitSchemaName) {
 		options.mappingDefaults.implicitSchemaName = implicitSchemaName;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withImplicitCatalogName(String implicitCatalogName) {
+	public MetadataBuilder applyImplicitCatalogName(String implicitCatalogName) {
 		options.mappingDefaults.implicitCatalogName = implicitCatalogName;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ImplicitNamingStrategy namingStrategy) {
+	public MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy) {
 		this.options.implicitNamingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(PhysicalNamingStrategy namingStrategy) {
+	public MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy) {
 		this.options.physicalNamingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ReflectionManager reflectionManager) {
+	public MetadataBuilder applyReflectionManager(ReflectionManager reflectionManager) {
 		this.options.reflectionManager = reflectionManager;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
+	public MetadataBuilder applySharedCacheMode(SharedCacheMode sharedCacheMode) {
 		this.options.sharedCacheMode = sharedCacheMode;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(AccessType implicitCacheAccessType) {
+	public MetadataBuilder applyAccessType(AccessType implicitCacheAccessType) {
 		this.options.mappingDefaults.implicitCacheAccessType = implicitCacheAccessType;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(IndexView jandexView) {
+	public MetadataBuilder applyIndexView(IndexView jandexView) {
 		this.options.jandexView = jandexView;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ScanOptions scanOptions) {
+	public MetadataBuilder applyScanOptions(ScanOptions scanOptions) {
 		this.options.scanOptions = scanOptions;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ScanEnvironment scanEnvironment) {
+	public MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment) {
 		this.options.scanEnvironment = scanEnvironment;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(Scanner scanner) {
+	public MetadataBuilder applyScanner(Scanner scanner) {
 		this.options.scannerSetting = scanner;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ArchiveDescriptorFactory factory) {
+	public MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory) {
 		this.options.archiveDescriptorFactory = factory;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
+	public MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enabled) {
 		this.options.useNewIdentifierGenerators = enabled;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withExplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
+	public MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
 		options.explicitDiscriminatorsForJoinedInheritanceSupported = supported;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withImplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
+	public MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
 		options.implicitDiscriminatorsForJoinedInheritanceSupported = supported;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withImplicitForcingOfDiscriminatorsInSelect(boolean supported) {
+	public MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported) {
 		options.implicitlyForceDiscriminatorInSelect = supported;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder withNationalizedCharacterData(boolean enabled) {
+	public MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled) {
 		options.useNationalizedCharacterData = enabled;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(BasicType type) {
+	public MetadataBuilder applyBasicType(BasicType type) {
 		options.basicTypeRegistrations.add( type );
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(UserType type, String[] keys) {
+	public MetadataBuilder applyBasicType(UserType type, String[] keys) {
 		options.basicTypeRegistrations.add( new CustomType( type, keys ) );
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(CompositeUserType type, String[] keys) {
+	public MetadataBuilder applyBasicType(CompositeUserType type, String[] keys) {
 		options.basicTypeRegistrations.add( new CompositeCustomType( type, keys ) );
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(TypeContributor typeContributor) {
+	public MetadataBuilder applyTypes(TypeContributor typeContributor) {
 		typeContributor.contribute( this, options.serviceRegistry );
 		return this;
 	}
 
 	@Override
 	public void contributeType(BasicType type) {
 		options.basicTypeRegistrations.add( type );
 	}
 
 	@Override
 	public void contributeType(UserType type, String[] keys) {
 		options.basicTypeRegistrations.add( new CustomType( type, keys ) );
 	}
 
 	@Override
 	public void contributeType(CompositeUserType type, String[] keys) {
 		options.basicTypeRegistrations.add( new CompositeCustomType( type, keys ) );
 	}
 
 	@Override
-	public MetadataBuilder with(CacheRegionDefinition cacheRegionDefinition) {
+	public MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
 		if ( options.cacheRegionDefinitions == null ) {
 			options.cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 		}
 		options.cacheRegionDefinitions.add( cacheRegionDefinition );
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(ClassLoader tempClassLoader) {
+	public MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader) {
 		options.tempClassLoader = tempClassLoader;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder setSourceProcessOrdering(List<MetadataSourceType> sourceProcessOrdering) {
-		options.sourceProcessOrdering = sourceProcessOrdering;
+	public MetadataBuilder applySourceProcessOrdering(MetadataSourceType... sourceTypes) {
+		options.sourceProcessOrdering.addAll( Arrays.asList( sourceTypes ) );
 		return this;
 	}
 
 	public MetadataBuilder allowSpecjSyntax() {
 		this.options.specjProprietarySyntaxEnabled = true;
 		return this;
 	}
 
+
+	@Override
+	public MetadataBuilder applySqlFunction(String functionName, SQLFunction function) {
+		if ( this.options.sqlFunctionMap == null ) {
+			// need to use this form as we want to specify the "concurrency level" as 1
+			// since only one thread will ever (should) be updating this
+			this.options.sqlFunctionMap = new HashMap<String, SQLFunction>();
+		}
+
+		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
+		// just in case a user's customer dialect uses mixed cases.
+		this.options.sqlFunctionMap.put( functionName.toLowerCase(), function );
+
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+		if ( this.options.auxiliaryDatabaseObjectList == null ) {
+			this.options.auxiliaryDatabaseObjectList = new ArrayList<AuxiliaryDatabaseObject>();
+		}
+		this.options.auxiliaryDatabaseObjectList.add( auxiliaryDatabaseObject );
+
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition) {
+		this.options.addAttributeConverterDefinition( definition );
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
+		applyAttributeConverter( AttributeConverterDefinition.from( attributeConverterClass ) );
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
+		applyAttributeConverter( AttributeConverterDefinition.from( attributeConverterClass, autoApply ) );
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter) {
+		applyAttributeConverter( AttributeConverterDefinition.from( attributeConverter ) );
+		return this;
+	}
+
+	@Override
+	public MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
+		applyAttributeConverter( AttributeConverterDefinition.from( attributeConverter, autoApply ) );
+		return this;
+	}
+
+
 //	public MetadataBuilder with(PersistentAttributeMemberResolver resolver) {
 //		options.persistentAttributeMemberResolver = resolver;
 //		return this;
 //	}
 
 	@Override
 	public MetadataImpl build() {
 		final CfgXmlAccessService cfgXmlAccessService = options.serviceRegistry.getService( CfgXmlAccessService.class );
 		if ( cfgXmlAccessService.getAggregatedConfig() != null ) {
 			if ( cfgXmlAccessService.getAggregatedConfig().getMappingReferences() != null ) {
 				for ( MappingReference mappingReference : cfgXmlAccessService.getAggregatedConfig().getMappingReferences() ) {
 					mappingReference.apply( sources );
 				}
 			}
 		}
 
 		return MetadataBuildingProcess.build( sources, options );
 	}
 
 	public static class MappingDefaultsImpl implements MappingDefaults {
 		private String implicitSchemaName;
 		private String implicitCatalogName;
 		private boolean implicitlyQuoteIdentifiers;
 
 		private AccessType implicitCacheAccessType;
 
 		public MappingDefaultsImpl(StandardServiceRegistry serviceRegistry) {
 			final ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			this.implicitSchemaName = configService.getSetting(
 					AvailableSettings.DEFAULT_SCHEMA,
 					StandardConverters.STRING,
 					null
 			);
 
 			this.implicitCatalogName = configService.getSetting(
 					AvailableSettings.DEFAULT_CATALOG,
 					StandardConverters.STRING,
 					null
 			);
 
 			this.implicitlyQuoteIdentifiers = configService.getSetting(
 					AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS,
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			this.implicitCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							return AccessType.fromExternalName( value.toString() );
 						}
 					}
 			);
 		}
 
 		@Override
 		public String getImplicitSchemaName() {
 			return implicitSchemaName;
 		}
 
 		@Override
 		public String getImplicitCatalogName() {
 			return implicitCatalogName;
 		}
 
 		@Override
 		public boolean shouldImplicitlyQuoteIdentifiers() {
 			return implicitlyQuoteIdentifiers;
 		}
 
 		@Override
 		public String getImplicitIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getImplicitTenantIdColumnName() {
 			return DEFAULT_TENANT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getImplicitDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getImplicitPackageName() {
 			return null;
 		}
 
 		@Override
 		public boolean isAutoImportEnabled() {
 			return true;
 		}
 
 		@Override
 		public String getImplicitCascadeStyleName() {
 			return DEFAULT_CASCADE_NAME;
 		}
 
 		@Override
 		public String getImplicitPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS_NAME;
 		}
 
 		@Override
 		public boolean areEntitiesImplicitlyLazy() {
 			// for now, just hard-code
 			return false;
 		}
 
 		@Override
 		public boolean areCollectionsImplicitlyLazy() {
 			// for now, just hard-code
 			return true;
 		}
 
 		@Override
 		public AccessType getImplicitCacheAccessType() {
 			return implicitCacheAccessType;
 		}
 	}
 
 	public static class MetadataBuildingOptionsImpl implements MetadataBuildingOptions {
 		private final StandardServiceRegistry serviceRegistry;
 		private final MappingDefaultsImpl mappingDefaults;
 
-		private List<BasicType> basicTypeRegistrations = new ArrayList<BasicType>();
+		private ArrayList<BasicType> basicTypeRegistrations = new ArrayList<BasicType>();
 
 		private IndexView jandexView;
 		private ClassLoader tempClassLoader;
 
 		private ScanOptions scanOptions;
 		private ScanEnvironment scanEnvironment;
 		private Object scannerSetting;
 		private ArchiveDescriptorFactory archiveDescriptorFactory;
 
 		private ImplicitNamingStrategy implicitNamingStrategy;
 		private PhysicalNamingStrategy physicalNamingStrategy;
 
 		private ReflectionManager reflectionManager = generateDefaultReflectionManager();
 
 		private SharedCacheMode sharedCacheMode;
 		private AccessType defaultCacheAccessType;
 		private boolean useNewIdentifierGenerators;
 		private MultiTenancyStrategy multiTenancyStrategy;
-		private List<CacheRegionDefinition> cacheRegionDefinitions;
+		private ArrayList<CacheRegionDefinition> cacheRegionDefinitions;
 		private boolean explicitDiscriminatorsForJoinedInheritanceSupported;
 		private boolean implicitDiscriminatorsForJoinedInheritanceSupported;
 		private boolean implicitlyForceDiscriminatorInSelect;
 		private boolean useNationalizedCharacterData;
 		private boolean specjProprietarySyntaxEnabled;
-		private List<MetadataSourceType> sourceProcessOrdering;
+		private ArrayList<MetadataSourceType> sourceProcessOrdering;
+
+		private HashMap<String,SQLFunction> sqlFunctionMap;
+		private ArrayList<AuxiliaryDatabaseObject> auxiliaryDatabaseObjectList;
+		private HashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 
 		private static ReflectionManager generateDefaultReflectionManager() {
 			final JavaReflectionManager reflectionManager = new JavaReflectionManager();
 			reflectionManager.setMetadataProvider( new JPAMetadataProvider() );
 			return reflectionManager;
 		}
 //		private PersistentAttributeMemberResolver persistentAttributeMemberResolver =
 //				StandardPersistentAttributeMemberResolver.INSTANCE;
 
 		public MetadataBuildingOptionsImpl(StandardServiceRegistry serviceRegistry) {
 			this.serviceRegistry = serviceRegistry;
 
 			final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 			final ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			this.mappingDefaults = new MappingDefaultsImpl( serviceRegistry );
 
 //			jandexView = (IndexView) configService.getSettings().get( AvailableSettings.JANDEX_INDEX );
 
 			scanOptions = new StandardScanOptions(
 					(String) configService.getSettings().get( AvailableSettings.SCANNER_DISCOVERY ),
 					false
 			);
 			// ScanEnvironment must be set explicitly
 			scannerSetting = configService.getSettings().get( AvailableSettings.SCANNER );
 			if ( scannerSetting == null ) {
 				scannerSetting = configService.getSettings().get( AvailableSettings.SCANNER_DEPRECATED );
 				if ( scannerSetting != null ) {
 					DEPRECATION_LOGGER.logDeprecatedScannerSetting();
 				}
 			}
 			archiveDescriptorFactory = strategySelector.resolveStrategy(
 					ArchiveDescriptorFactory.class,
 					configService.getSettings().get( AvailableSettings.SCANNER_ARCHIVE_INTERPRETER )
 			);
 
 			useNewIdentifierGenerators = configService.getSetting(
 					AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS,
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			multiTenancyStrategy =  MultiTenancyStrategy.determineMultiTenancyStrategy( configService.getSettings() );
 
 			implicitDiscriminatorsForJoinedInheritanceSupported = configService.getSetting(
 					AvailableSettings.IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS,
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			explicitDiscriminatorsForJoinedInheritanceSupported = !configService.getSetting(
 					AvailableSettings.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS,
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			implicitlyForceDiscriminatorInSelect = configService.getSetting(
 					AvailableSettings.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT,
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			sharedCacheMode = configService.getSetting(
 					"javax.persistence.sharedCache.mode",
 					new ConfigurationService.Converter<SharedCacheMode>() {
 						@Override
 						public SharedCacheMode convert(Object value) {
 							if ( value == null ) {
 								return null;
 							}
 
 							if ( SharedCacheMode.class.isInstance( value ) ) {
 								return (SharedCacheMode) value;
 							}
 
 							return SharedCacheMode.valueOf( value.toString() );
 						}
 					},
 					SharedCacheMode.UNSPECIFIED
 			);
 
 			defaultCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							if ( value == null ) {
 								return null;
 							}
 
 							if ( CacheConcurrencyStrategy.class.isInstance( value ) ) {
 								return ( (CacheConcurrencyStrategy) value ).toAccessType();
 							}
 
 							if ( AccessType.class.isInstance( value ) ) {
 								return (AccessType) value;
 							}
 
 							return AccessType.fromExternalName( value.toString() );
 						}
 					},
 					// by default, see if the defined RegionFactory (if one) defines a default
 					serviceRegistry.getService( RegionFactory.class ) == null
 							? null
 							: serviceRegistry.getService( RegionFactory.class ).getDefaultAccessType()
 			);
 
 			specjProprietarySyntaxEnabled = configService.getSetting(
 					"hibernate.enable_specj_proprietary_syntax",
 					StandardConverters.BOOLEAN,
 					false
 			);
 
 			implicitNamingStrategy = strategySelector.resolveDefaultableStrategy(
 					ImplicitNamingStrategy.class,
 					configService.getSettings().get( AvailableSettings.IMPLICIT_NAMING_STRATEGY ),
 					ImplicitNamingStrategyLegacyJpaImpl.INSTANCE
 			);
 
 			physicalNamingStrategy = strategySelector.resolveDefaultableStrategy(
 					PhysicalNamingStrategy.class,
 					configService.getSettings().get( AvailableSettings.PHYSICAL_NAMING_STRATEGY ),
 					PhysicalNamingStrategyStandardImpl.INSTANCE
 			);
 
 			sourceProcessOrdering = resolveInitialSourceProcessOrdering( configService );
 		}
 
-		private List<MetadataSourceType> resolveInitialSourceProcessOrdering(ConfigurationService configService) {
-			List<MetadataSourceType> initialSelections = null;
+		private ArrayList<MetadataSourceType> resolveInitialSourceProcessOrdering(ConfigurationService configService) {
+			final ArrayList<MetadataSourceType> initialSelections = new ArrayList<MetadataSourceType>();
 
 			final String sourceProcessOrderingSetting = configService.getSetting(
 					AvailableSettings.ARTIFACT_PROCESSING_ORDER,
 					StandardConverters.STRING
 			);
 			if ( sourceProcessOrderingSetting != null ) {
 				final String[] orderChoices = StringHelper.split( ",; ", sourceProcessOrderingSetting, false );
-				initialSelections = CollectionHelper.arrayList( orderChoices.length );
+				initialSelections.addAll( CollectionHelper.<MetadataSourceType>arrayList( orderChoices.length ) );
 				for ( String orderChoice : orderChoices ) {
 					initialSelections.add( MetadataSourceType.parsePrecedence( orderChoice ) );
 				}
 			}
-			if ( initialSelections == null || initialSelections.isEmpty() ) {
-				initialSelections = Arrays.asList(  MetadataSourceType.HBM, MetadataSourceType.CLASS );
+			if ( initialSelections.isEmpty() ) {
+				initialSelections.add( MetadataSourceType.HBM );
+				initialSelections.add( MetadataSourceType.CLASS );
 			}
 
 			return initialSelections;
 		}
 
 		@Override
 		public StandardServiceRegistry getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		@Override
 		public MappingDefaults getMappingDefaults() {
 			return mappingDefaults;
 		}
 
 		@Override
 		public List<BasicType> getBasicTypeRegistrations() {
 			return basicTypeRegistrations;
 		}
 
 		@Override
 		public IndexView getJandexView() {
 			return jandexView;
 		}
 
 		@Override
 		public ScanOptions getScanOptions() {
 			return scanOptions;
 		}
 
 		@Override
 		public ScanEnvironment getScanEnvironment() {
 			return scanEnvironment;
 		}
 
 		@Override
 		public Object getScanner() {
 			return scannerSetting;
 		}
 
 		@Override
 		public ArchiveDescriptorFactory getArchiveDescriptorFactory() {
 			return archiveDescriptorFactory;
 		}
 
 		@Override
 		public ClassLoader getTempClassLoader() {
 			return tempClassLoader;
 		}
 
 		@Override
 		public ImplicitNamingStrategy getImplicitNamingStrategy() {
 			return implicitNamingStrategy;
 		}
 
 		@Override
 		public PhysicalNamingStrategy getPhysicalNamingStrategy() {
 			return physicalNamingStrategy;
 		}
 
 		@Override
 		public ReflectionManager getReflectionManager() {
 			return reflectionManager;
 		}
 
 		@Override
 		public SharedCacheMode getSharedCacheMode() {
 			return sharedCacheMode;
 		}
 
 		@Override
 		public AccessType getImplicitCacheAccessType() {
 			return defaultCacheAccessType;
 		}
 
 		@Override
 		public boolean isUseNewIdentifierGenerators() {
 			return useNewIdentifierGenerators;
 		}
 
 		@Override
 		public MultiTenancyStrategy getMultiTenancyStrategy() {
 			return multiTenancyStrategy;
 		}
 
 		@Override
 		public List<CacheRegionDefinition> getCacheRegionDefinitions() {
 			return cacheRegionDefinitions;
 		}
 
 		@Override
 		public boolean ignoreExplicitDiscriminatorsForJoinedInheritance() {
 			return !explicitDiscriminatorsForJoinedInheritanceSupported;
 		}
 
 		@Override
 		public boolean createImplicitDiscriminatorsForJoinedInheritance() {
 			return implicitDiscriminatorsForJoinedInheritanceSupported;
 		}
 
 		@Override
 		public boolean shouldImplicitlyForceDiscriminatorInSelect() {
 			return implicitlyForceDiscriminatorInSelect;
 		}
 
 		@Override
 		public boolean useNationalizedCharacterData() {
 			return useNationalizedCharacterData;
 		}
 
 		@Override
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		@Override
 		public List<MetadataSourceType> getSourceProcessOrdering() {
 			return sourceProcessOrdering;
 		}
 
+		@Override
+		public Map<String, SQLFunction> getSqlFunctions() {
+			return sqlFunctionMap == null ? Collections.<String, SQLFunction>emptyMap() : sqlFunctionMap;
+		}
+
+		@Override
+		public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
+			return auxiliaryDatabaseObjectList == null
+					? Collections.<AuxiliaryDatabaseObject>emptyList()
+					: auxiliaryDatabaseObjectList;
+		}
+
+		@Override
+		public List<AttributeConverterDefinition> getAttributeConverters() {
+			return attributeConverterDefinitionsByClass == null
+					? Collections.<AttributeConverterDefinition>emptyList()
+					: new ArrayList<AttributeConverterDefinition>( attributeConverterDefinitionsByClass.values() );
+		}
+
+		public void addAttributeConverterDefinition(AttributeConverterDefinition definition) {
+			if ( this.attributeConverterDefinitionsByClass == null ) {
+				this.attributeConverterDefinitionsByClass = new HashMap<Class, AttributeConverterDefinition>();
+			}
+
+			final Object old = this.attributeConverterDefinitionsByClass.put( definition.getAttributeConverter().getClass(), definition );
+
+			if ( old != null ) {
+				throw new AssertionFailure(
+						String.format(
+								"AttributeConverter class [%s] registered multiple times",
+								definition.getAttributeConverter().getClass()
+						)
+				);
+			}
+		}
+
 		public static interface JpaOrmXmlPersistenceUnitDefaults {
 			public String getDefaultSchemaName();
 			public String getDefaultCatalogName();
 			public boolean shouldImplicitlyQuoteIdentifiers();
 		}
 
 		/**
 		 * Yuck.  This is needed because JPA lets users define "global building options"
 		 * in {@code orm.xml} mappings.  Forget that there are generally multiple
 		 * {@code orm.xml} mappings if using XML approach...  Ugh
 		 */
 		public void apply(JpaOrmXmlPersistenceUnitDefaults jpaOrmXmlPersistenceUnitDefaults) {
 			if ( !mappingDefaults.shouldImplicitlyQuoteIdentifiers() ) {
 				mappingDefaults.implicitlyQuoteIdentifiers = jpaOrmXmlPersistenceUnitDefaults.shouldImplicitlyQuoteIdentifiers();
 			}
 
 			if ( mappingDefaults.getImplicitCatalogName() == null ) {
 				mappingDefaults.implicitCatalogName = StringHelper.nullIfEmpty(
 						jpaOrmXmlPersistenceUnitDefaults.getDefaultCatalogName()
 				);
 			}
 
 			if ( mappingDefaults.getImplicitSchemaName() == null ) {
 				mappingDefaults.implicitSchemaName = StringHelper.nullIfEmpty(
 						jpaOrmXmlPersistenceUnitDefaults.getDefaultSchemaName()
 				);
 			}
 		}
 
 		//		@Override
 //		public PersistentAttributeMemberResolver getPersistentAttributeMemberResolver() {
 //			return persistentAttributeMemberResolver;
 //		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuildingProcess.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuildingProcess.java
index 2d904393f6..e0b5b24589 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuildingProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/MetadataBuildingProcess.java
@@ -1,526 +1,530 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.lang.reflect.Constructor;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Set;
 import javax.persistence.Converter;
 
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.archive.internal.StandardArchiveDescriptorFactory;
 import org.hibernate.boot.archive.scan.internal.StandardScanner;
 import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
 import org.hibernate.boot.archive.scan.spi.JandexInitializer;
 import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
 import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
 import org.hibernate.boot.archive.scan.spi.ScanParameters;
 import org.hibernate.boot.archive.scan.spi.ScanResult;
 import org.hibernate.boot.archive.scan.spi.Scanner;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.internal.DeploymentResourcesInterpreter.DeploymentResources;
+import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
 import org.hibernate.boot.jaxb.internal.MappingBinder;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.source.internal.annotations.AnnotationMetadataSourceProcessorImpl;
 import org.hibernate.boot.model.source.internal.hbm.EntityHierarchyBuilder;
 import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
 import org.hibernate.boot.model.source.internal.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
 import org.hibernate.boot.model.source.internal.hbm.ModelBinder;
 import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.AdditionalJaxbMappingProducer;
 import org.hibernate.boot.spi.ClassLoaderAccess;
 import org.hibernate.boot.spi.MetadataBuildingOptions;
 import org.hibernate.boot.spi.MetadataContributor;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.type.BasicTypeRegistry;
 import org.hibernate.type.TypeFactory;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import org.jboss.jandex.IndexView;
 import org.jboss.logging.Logger;
 
 /**
  * Represents the process of building a Metadata object.  The main entry point is the
  * static {@link #build}
  *
  * @author Steve Ebersole
  */
 public class MetadataBuildingProcess {
 	private static final Logger log = Logger.getLogger( MetadataBuildingProcess.class );
 
 	public static MetadataImpl build(
 			final MetadataSources sources,
-			final MetadataBuildingOptions options) {
+			final MetadataBuildingOptionsImpl options) {
 		final ClassLoaderService classLoaderService = options.getServiceRegistry().getService( ClassLoaderService.class );
 
 		final ClassLoaderAccess classLoaderAccess = new ClassLoaderAccessImpl(
 				options.getTempClassLoader(),
 				classLoaderService
 		);
 
 //		final JandexInitManager jandexInitializer = buildJandexInitializer( options, classLoaderAccess );
 		
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// scanning - Jandex initialization and source discovery
 		if ( options.getScanEnvironment() != null ) {
 			final Scanner scanner = buildScanner( options, classLoaderAccess );
 			final ScanResult scanResult = scanner.scan(
 					options.getScanEnvironment(),
 					options.getScanOptions(),
 					new ScanParameters() {
 						@Override
 						public JandexInitializer getJandexInitializer() {
 //							return jandexInitializer;
 							return null;
 						}
 					}
 			);
 
 			// Add to the MetadataSources any classes/packages/mappings discovered during scanning
 			addScanResultsToSources( sources, options, scanResult );
 		}
 
 //		// todo : add options.getScanEnvironment().getExplicitlyListedClassNames() to jandex?
 //		//		^^ - another option is to make sure that they are added to sources
 //
 //		if ( !jandexInitializer.wasIndexSupplied() ) {
 //			// If the Jandex Index(View) was supplied, we consider that supplied
 //			// one "complete".
 //			// Here though we were NOT supplied an index; in this case we want to
 //			// additionally ensure that any-and-all "known" classes are added to
 //			// the index we are building
 //			sources.indexKnownClasses( jandexInitializer );
 //		}
 		
 		// It's necessary to delay the binding of XML resources until now.  ClassLoaderAccess is needed for
 		// reflection, etc.
 //		sources.buildBindResults( classLoaderAccess );
 
 //		final IndexView jandexView = augmentJandexFromMappings( jandexInitializer.buildIndex(), sources, options );
 		final IndexView jandexView = options.getJandexView();
 
 		final BasicTypeRegistry basicTypeRegistry = handleTypes( options );
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// prep to start handling binding in earnest
 
 //		final JandexAccessImpl jandexAccess = new JandexAccessImpl(
 //				jandexView,
 //				classLoaderAccess
 //
 //		);
 		final InFlightMetadataCollectorImpl metadataCollector = new InFlightMetadataCollectorImpl(
 				options,
 				sources,
 				new TypeResolver( basicTypeRegistry, new TypeFactory() )
 		);
 
 		final MetadataBuildingContextRootImpl rootMetadataBuildingContext = new MetadataBuildingContextRootImpl(
 				options,
 				classLoaderAccess,
 				metadataCollector
 		);
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Set up the processors and start binding
 		//		NOTE : this becomes even more simplified after we move purely
 		// 		to unified model
 
 		final MetadataSourceProcessor processor = new MetadataSourceProcessor() {
 			private final HbmMetadataSourceProcessorImpl hbmProcessor = new HbmMetadataSourceProcessorImpl(
 					sources,
 					rootMetadataBuildingContext
 			);
 
 			private final AnnotationMetadataSourceProcessorImpl annotationProcessor = new AnnotationMetadataSourceProcessorImpl(
 					sources,
 					rootMetadataBuildingContext,
 					jandexView
 			);
 
 			@Override
 			public void prepare() {
 				hbmProcessor.prepare();
 				annotationProcessor.prepare();
 			}
 
 			@Override
 			public void processTypeDefinitions() {
 				hbmProcessor.processTypeDefinitions();
 				annotationProcessor.processTypeDefinitions();
 			}
 
 			@Override
 			public void processQueryRenames() {
 				hbmProcessor.processQueryRenames();
 				annotationProcessor.processQueryRenames();
 			}
 
 			@Override
 			public void processNamedQueries() {
 				hbmProcessor.processNamedQueries();
 				annotationProcessor.processNamedQueries();
 			}
 
 			@Override
 			public void processAuxiliaryDatabaseObjectDefinitions() {
 				hbmProcessor.processAuxiliaryDatabaseObjectDefinitions();
 				annotationProcessor.processAuxiliaryDatabaseObjectDefinitions();
 			}
 
 			@Override
 			public void processIdentifierGenerators() {
 				hbmProcessor.processIdentifierGenerators();
 				annotationProcessor.processIdentifierGenerators();
 			}
 
 			@Override
 			public void processFilterDefinitions() {
 				hbmProcessor.processFilterDefinitions();
 				annotationProcessor.processFilterDefinitions();
 			}
 
 			@Override
 			public void processFetchProfiles() {
 				hbmProcessor.processFetchProfiles();
 				annotationProcessor.processFetchProfiles();
 			}
 
 			@Override
 			public void prepareForEntityHierarchyProcessing() {
 				for ( MetadataSourceType metadataSourceType : options.getSourceProcessOrdering() ) {
 					if ( metadataSourceType == MetadataSourceType.HBM ) {
 						hbmProcessor.prepareForEntityHierarchyProcessing();
 					}
 
 					if ( metadataSourceType == MetadataSourceType.CLASS ) {
 						annotationProcessor.prepareForEntityHierarchyProcessing();
 					}
 				}
 			}
 
 			@Override
 			public void processEntityHierarchies(Set<String> processedEntityNames) {
 				for ( MetadataSourceType metadataSourceType : options.getSourceProcessOrdering() ) {
 					if ( metadataSourceType == MetadataSourceType.HBM ) {
 						hbmProcessor.processEntityHierarchies( processedEntityNames );
 					}
 
 					if ( metadataSourceType == MetadataSourceType.CLASS ) {
 						annotationProcessor.processEntityHierarchies( processedEntityNames );
 					}
 				}
 			}
 
 			@Override
 			public void postProcessEntityHierarchies() {
 				for ( MetadataSourceType metadataSourceType : options.getSourceProcessOrdering() ) {
 					if ( metadataSourceType == MetadataSourceType.HBM ) {
 						hbmProcessor.postProcessEntityHierarchies();
 					}
 
 					if ( metadataSourceType == MetadataSourceType.CLASS ) {
 						annotationProcessor.postProcessEntityHierarchies();
 					}
 				}
 			}
 
 			@Override
 			public void processResultSetMappings() {
 				hbmProcessor.processResultSetMappings();
 				annotationProcessor.processResultSetMappings();
 			}
 
 			@Override
 			public void finishUp() {
 				hbmProcessor.finishUp();
 				annotationProcessor.finishUp();
 			}
 		};
 
 		processor.prepare();
 
 		processor.processTypeDefinitions();
 		processor.processQueryRenames();
 		processor.processAuxiliaryDatabaseObjectDefinitions();
 
 		processor.processIdentifierGenerators();
 		processor.processFilterDefinitions();
 		processor.processFetchProfiles();
 
 		final Set<String> processedEntityNames = new HashSet<String>();
 		processor.prepareForEntityHierarchyProcessing();
 		processor.processEntityHierarchies( processedEntityNames );
 		processor.postProcessEntityHierarchies();
 
 		processor.processResultSetMappings();
 		processor.processNamedQueries();
 
 		processor.finishUp();
 
 		for ( MetadataContributor contributor : classLoaderService.loadJavaServices( MetadataContributor.class ) ) {
 			log.tracef( "Calling MetadataContributor : %s", contributor );
 			contributor.contribute( metadataCollector, jandexView );
 		}
 
 		metadataCollector.processSecondPasses( rootMetadataBuildingContext );
 
 		LinkedHashSet<AdditionalJaxbMappingProducer> producers = classLoaderService.loadJavaServices( AdditionalJaxbMappingProducer.class );
 		if ( producers != null && !producers.isEmpty() ) {
 			final EntityHierarchyBuilder hierarchyBuilder = new EntityHierarchyBuilder();
 //			final MappingBinder mappingBinder = new MappingBinder( true );
 			// We need to disable validation here.  It seems Envers is not producing valid (according to schema) XML
 			final MappingBinder mappingBinder = new MappingBinder( false );
 			for ( AdditionalJaxbMappingProducer producer : producers ) {
 				log.tracef( "Calling AdditionalJaxbMappingProducer : %s", producer );
 				Collection<MappingDocument> additionalMappings = producer.produceAdditionalMappings(
 						metadataCollector,
 						jandexView,
 						mappingBinder,
 						rootMetadataBuildingContext
 				);
 				for ( MappingDocument mappingDocument : additionalMappings ) {
 					hierarchyBuilder.indexMappingDocument( mappingDocument );
 				}
 			}
 
 			ModelBinder binder = ModelBinder.prepare( rootMetadataBuildingContext );
 			for ( EntityHierarchySourceImpl entityHierarchySource : hierarchyBuilder.buildHierarchies() ) {
 				binder.bindEntityHierarchy( entityHierarchySource );
 			}
 		}
 
 		return metadataCollector.buildMetadataInstance( rootMetadataBuildingContext );
 	}
 
 //	private static JandexInitManager buildJandexInitializer(
 //			MetadataBuildingOptions options,
 //			ClassLoaderAccess classLoaderAccess) {
 //		final boolean autoIndexMembers = ConfigurationHelper.getBoolean(
 //				org.hibernate.cfg.AvailableSettings.ENABLE_AUTO_INDEX_MEMBER_TYPES,
 //				options.getServiceRegistry().getService( ConfigurationService.class ).getSettings(),
 //				false
 //		);
 //
 //		return new JandexInitManager( options.getJandexView(), classLoaderAccess, autoIndexMembers );
 //	}
 
 	private static final Class[] SINGLE_ARG = new Class[] { ArchiveDescriptorFactory.class };
 
 	private static Scanner buildScanner(MetadataBuildingOptions options, ClassLoaderAccess classLoaderAccess) {
 		final Object scannerSetting = options.getScanner();
 		final ArchiveDescriptorFactory archiveDescriptorFactory = options.getArchiveDescriptorFactory();
 
 		if ( scannerSetting == null ) {
 			// No custom Scanner specified, use the StandardScanner
 			if ( archiveDescriptorFactory == null ) {
 				return new StandardScanner();
 			}
 			else {
 				return new StandardScanner( archiveDescriptorFactory );
 			}
 		}
 		else {
 			if ( Scanner.class.isInstance( scannerSetting ) ) {
 				if ( archiveDescriptorFactory != null ) {
 					throw new IllegalStateException(
 							"A Scanner instance and an ArchiveDescriptorFactory were both specified; please " +
 									"specify one or the other, or if you need to supply both, Scanner class to use " +
 									"(assuming it has a constructor accepting a ArchiveDescriptorFactory).  " +
 									"Alternatively, just pass the ArchiveDescriptorFactory during your own " +
 									"Scanner constructor assuming it is statically known."
 					);
 				}
 				return (Scanner) scannerSetting;
 			}
 
 			final Class<? extends  Scanner> scannerImplClass;
 			if ( Class.class.isInstance( scannerSetting ) ) {
 				scannerImplClass = (Class<? extends Scanner>) scannerSetting;
 			}
 			else {
 				scannerImplClass = classLoaderAccess.classForName( scannerSetting.toString() );
 			}
 
 
 			if ( archiveDescriptorFactory != null ) {
 				// find the single-arg constructor - its an error if none exists
 				try {
 					final Constructor<? extends Scanner> constructor = scannerImplClass.getConstructor( SINGLE_ARG );
 					try {
 						return constructor.newInstance( archiveDescriptorFactory );
 					}
 					catch (Exception e) {
 						throw new IllegalStateException(
 								"Error trying to instantiate custom specified Scanner [" +
 										scannerImplClass.getName() + "]",
 								e
 						);
 					}
 				}
 				catch (NoSuchMethodException e) {
 					throw new IllegalArgumentException(
 							"Configuration named a custom Scanner and a custom ArchiveDescriptorFactory, but " +
 									"Scanner impl did not define a constructor accepting ArchiveDescriptorFactory"
 					);
 				}
 			}
 			else {
 				// could be either ctor form...
 				// find the single-arg constructor - its an error if none exists
 				try {
 					final Constructor<? extends Scanner> constructor = scannerImplClass.getConstructor( SINGLE_ARG );
 					try {
 						return constructor.newInstance( StandardArchiveDescriptorFactory.INSTANCE );
 					}
 					catch (Exception e) {
 						throw new IllegalStateException(
 								"Error trying to instantiate custom specified Scanner [" +
 										scannerImplClass.getName() + "]",
 								e
 						);
 					}
 				}
 				catch (NoSuchMethodException e) {
 					try {
 						final Constructor<? extends Scanner> constructor = scannerImplClass.getConstructor();
 						try {
 							return constructor.newInstance();
 						}
 						catch (Exception e2) {
 							throw new IllegalStateException(
 									"Error trying to instantiate custom specified Scanner [" +
 											scannerImplClass.getName() + "]",
 									e2
 							);
 						}
 					}
 					catch (NoSuchMethodException ignore) {
 						throw new IllegalArgumentException(
 								"Configuration named a custom Scanner, but we were unable to locate " +
 										"an appropriate constructor"
 						);
 					}
 				}
 			}
 		}
 	}
 
 	private static void addScanResultsToSources(
 			MetadataSources sources,
-			MetadataBuildingOptions options,
+			MetadataBuildingOptionsImpl options,
 			ScanResult scanResult) {
 		final ClassLoaderService cls = options.getServiceRegistry().getService( ClassLoaderService.class );
 
 		DeploymentResources deploymentResources = DeploymentResourcesInterpreter.INSTANCE.buildDeploymentResources(
 				options.getScanEnvironment(),
 				scanResult,
 				options.getServiceRegistry()
 		);
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			final String className = classDescriptor.getName();
 
 			// todo : leverage Jandex calls after we fully integrate Jandex...
 			try {
 				final Class classRef = cls.classForName( className );
 
 				// logic here assumes an entity is not also a converter...
 				final Converter converter = (Converter) classRef.getAnnotation( Converter.class );
 				if ( converter != null ) {
 					//noinspection unchecked
-					sources.addAttributeConverter( classRef, converter.autoApply() );
+					options.addAttributeConverterDefinition(
+							AttributeConverterDefinition.from( classRef, converter.autoApply() )
+					);
 				}
 				else {
 					sources.addAnnotatedClass( classRef );
 				}
 			}
 			catch (ClassLoadingException e) {
 				// Not really sure what this means...
 				sources.addAnnotatedClassName( className );
 			}
 		}
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			sources.addPackage( packageDescriptor.getName() );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : deploymentResources.getMappingFileDescriptors() ) {
 			sources.addInputStream( mappingFileDescriptor.getStreamAccess() );
 		}
 	}
 
 
 	private static BasicTypeRegistry handleTypes(MetadataBuildingOptions options) {
 		final ClassLoaderService classLoaderService = options.getServiceRegistry().getService( ClassLoaderService.class );
 
 		// ultimately this needs to change a little bit to account for HHH-7792
 		final BasicTypeRegistry basicTypeRegistry = new BasicTypeRegistry();
 
 		final TypeContributions typeContributions = new TypeContributions() {
 			@Override
 			public void contributeType(org.hibernate.type.BasicType type) {
 				basicTypeRegistry.register( type );
 			}
 
 			@Override
 			public void contributeType(UserType type, String[] keys) {
 				basicTypeRegistry.register( type, keys );
 			}
 
 			@Override
 			public void contributeType(CompositeUserType type, String[] keys) {
 				basicTypeRegistry.register( type, keys );
 			}
 		};
 
 		// add Dialect contributed types
 		final Dialect dialect = options.getServiceRegistry().getService( JdbcServices.class ).getDialect();
 		dialect.contributeTypes( typeContributions, options.getServiceRegistry() );
 
 		// add TypeContributor contributed types.
 		for ( TypeContributor contributor : classLoaderService.loadJavaServices( TypeContributor.class ) ) {
 			contributor.contribute( typeContributions, options.getServiceRegistry() );
 		}
 
 		// add explicit application registered types
 		for ( org.hibernate.type.BasicType basicType : options.getBasicTypeRegistrations() ) {
 			basicTypeRegistry.register( basicType );
 		}
 
 		return basicTypeRegistry;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
index 30f188ee3c..dfd9b112fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
@@ -1,259 +1,261 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
 	private final MetadataImplementor metadata;
 	private final SessionFactoryOptionsImpl options;
 
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 		options = new SessionFactoryOptionsImpl( metadata.getMetadataBuildingOptions().getServiceRegistry() );
 
 		if ( metadata.getSqlFunctionMap() != null ) {
 			for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : metadata.getSqlFunctionMap().entrySet() ) {
-				with( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
+				applySqlFunction( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 			}
 		}
 	}
 
 	@Override
-	public SessionFactoryBuilder with(Interceptor interceptor) {
+	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor) {
 		this.options.interceptor = interceptor;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(CustomEntityDirtinessStrategy dirtinessStrategy) {
-		this.options.customEntityDirtinessStrategy = dirtinessStrategy;
+	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
+		this.options.customEntityDirtinessStrategy = strategy;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
-		this.options.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
+	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
+		this.options.currentTenantIdentifierResolver = resolver;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder add(SessionFactoryObserver... observers) {
+	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers) {
 		this.options.sessionFactoryObserverList.addAll( Arrays.asList( observers ) );
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder add(EntityNameResolver... entityNameResolvers) {
+	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers) {
 		this.options.entityNameResolvers.addAll( Arrays.asList( entityNameResolvers ) );
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
+	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(EntityTuplizerFactory entityTuplizerFactory) {
+	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		options.settings.setEntityTuplizerFactory( entityTuplizerFactory );
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
+	public SessionFactoryBuilder applyEntityTuplizer(
+			EntityMode entityMode,
+			Class<? extends EntityTuplizer> tuplizerClass) {
 		if ( options.settings.getEntityTuplizerFactory() == null ) {
 			options.settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 		}
 		this.options.settings.getEntityTuplizerFactory().registerDefaultTuplizerClass( entityMode, tuplizerClass );
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder withValidatorFactory(Object validatorFactory) {
+	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory) {
 		this.options.validatorFactoryReference = validatorFactory;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder withBeanManager(Object beanManager) {
+	public SessionFactoryBuilder applyBeanManager(Object beanManager) {
 		this.options.beanManagerReference = beanManager;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder with(String registrationName, SQLFunction sqlFunction) {
+	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction) {
 		if ( this.options.sqlFunctions == null ) {
 			this.options.sqlFunctions = new HashMap<String, SQLFunction>();
 		}
 		this.options.sqlFunctions.put( registrationName, sqlFunction );
 		return this;
 	}
 
 	@Override
 	public SessionFactory build() {
 		metadata.validate();
 		return new SessionFactoryImpl( metadata, options );
 	}
 
 	private static class SessionFactoryOptionsImpl implements SessionFactory.SessionFactoryOptions {
 		private final StandardServiceRegistry serviceRegistry;
 
 		private Interceptor interceptor;
 		private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 		private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 		private List<SessionFactoryObserver> sessionFactoryObserverList = new ArrayList<SessionFactoryObserver>();
 		private List<EntityNameResolver> entityNameResolvers = new ArrayList<EntityNameResolver>();
 		private EntityNotFoundDelegate entityNotFoundDelegate;
 		private Settings settings;
 		private Object beanManagerReference;
 		private Object validatorFactoryReference;
 
 		private Map<String, SQLFunction> sqlFunctions;
 
 		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
 			this.serviceRegistry = serviceRegistry;
 
 			final Map configurationSettings = serviceRegistry.getService( ConfigurationService.class ).getSettings();
 
 			final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 
 			this.interceptor = strategySelector.resolveDefaultableStrategy(
 					Interceptor.class,
 					configurationSettings.get( AvailableSettings.INTERCEPTOR ),
 					EmptyInterceptor.INSTANCE
 			);
 
 			this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
 
 			this.customEntityDirtinessStrategy = strategySelector.resolveDefaultableStrategy(
 					CustomEntityDirtinessStrategy.class,
 					configurationSettings.get( AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY ),
 					DefaultCustomEntityDirtinessStrategy.INSTANCE
 			);
 
 			this.currentTenantIdentifierResolver = strategySelector.resolveStrategy(
 					CurrentTenantIdentifierResolver.class,
 					configurationSettings.get( AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER )
 			);
 
 			this.beanManagerReference = configurationSettings.get( "javax.persistence.bean.manager" );
 			this.validatorFactoryReference = configurationSettings.get( "javax.persistence.validation.factory" );
 
 			Properties properties = new Properties();
 			properties.putAll( configurationSettings );
 			this.settings = new SettingsFactory().buildSettings( properties, serviceRegistry );
 
 			this.settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 		}
 
 		@Override
 		public StandardServiceRegistry getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		@Override
 		public Interceptor getInterceptor() {
 			return interceptor;
 		}
 
 		@Override
 		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 			return customEntityDirtinessStrategy;
 		}
 
 		@Override
 		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 			return currentTenantIdentifierResolver;
 		}
 
 		@Override
 		public Settings getSettings() {
 			return settings;
 		}
 
 		@Override
 		public SessionFactoryObserver[] getSessionFactoryObservers() {
 			return sessionFactoryObserverList.toArray( new SessionFactoryObserver[sessionFactoryObserverList.size()] );
 		}
 
 		@Override
 		public EntityNameResolver[] getEntityNameResolvers() {
 			return entityNameResolvers.toArray( new EntityNameResolver[entityNameResolvers.size()] );
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 
 		@Override
 		public Map<String, SQLFunction> getCustomSqlFunctionMap() {
 			return sqlFunctions;
 		}
 
 		@Override
 		public Object getBeanManagerReference() {
 			return beanManagerReference;
 		}
 
 		@Override
 		public Object getValidatorFactoryReference() {
 			return validatorFactoryReference;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/model/TypeDefinition.java b/hibernate-core/src/main/java/org/hibernate/boot/model/TypeDefinition.java
index 7785c53f6e..fb815dddb3 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/model/TypeDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/model/TypeDefinition.java
@@ -1,154 +1,154 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.model;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.internal.util.compare.EqualsHelper;
 
 /**
  * Models the information pertaining to a custom type definition supplied by the user.  Used
  * to delay instantiation of the actual {@link org.hibernate.type.Type} instance.
  *
  * Generally speaking this information would come from annotations
  * ({@link org.hibernate.annotations.TypeDef}) or XML mappings.  An alternative form of
  * supplying custom types is programatically via one of:<ul>
- *     <li>{@link org.hibernate.boot.MetadataBuilder#with(org.hibernate.type.BasicType)}</li>
- *     <li>{@link org.hibernate.boot.MetadataBuilder#with(org.hibernate.usertype.UserType, String[])}</li>
- *     <li>{@link org.hibernate.boot.MetadataBuilder#with(TypeContributor)}</li>
+ *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.type.BasicType)}</li>
+ *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.usertype.UserType, String[])}</li>
+ *     <li>{@link org.hibernate.boot.MetadataBuilder#applyTypes(TypeContributor)}</li>
  * </ul>
  *
  * @author Steve Ebersole
  * @author John Verhaeg
  */
 public class TypeDefinition implements Serializable {
 	private final String name;
     private final Class typeImplementorClass;
 	private final String[] registrationKeys;
     private final Map<String, String> parameters;
 
 	public TypeDefinition(
 			String name,
 			Class typeImplementorClass,
 			String[] registrationKeys,
 			Map<String, String> parameters) {
 		this.name = name;
 		this.typeImplementorClass = typeImplementorClass;
 		this.registrationKeys= registrationKeys;
 		this.parameters = parameters == null
 				? Collections.<String, String>emptyMap()
 				: Collections.unmodifiableMap( parameters );
 	}
 
 	public TypeDefinition(
 			String name,
 			Class typeImplementorClass,
 			String[] registrationKeys,
 			Properties parameters) {
 		this.name = name;
 		this.typeImplementorClass = typeImplementorClass;
 		this.registrationKeys= registrationKeys;
 		this.parameters = parameters == null
 				? Collections.<String, String>emptyMap()
 				: extractStrings( parameters );
 	}
 
 	private Map<String, String> extractStrings(Properties properties) {
 		final Map<String, String> parameters = new HashMap<String, String>();
 
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( String.class.isInstance( entry.getKey() )
 					&& String.class.isInstance( entry.getValue() ) ) {
 				parameters.put(
 						(String) entry.getKey(),
 						(String) entry.getValue()
 				);
 			}
 		}
 
 		return parameters;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class getTypeImplementorClass() {
 		return typeImplementorClass;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public Map<String, String> getParameters() {
         return parameters;
     }
 
 	public Properties getParametersAsProperties() {
 		Properties properties = new Properties();
 		properties.putAll( parameters );
 		return properties;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( !( o instanceof TypeDefinition ) ) {
 			return false;
 		}
 
 		final TypeDefinition that = (TypeDefinition) o;
 		return EqualsHelper.equals( this.name, that.name )
 				&& EqualsHelper.equals( this.typeImplementorClass, that.typeImplementorClass )
 				&& Arrays.equals( this.registrationKeys, that.registrationKeys )
 				&& EqualsHelper.equals( this.parameters, that.parameters );
 	}
 
 	@Override
 	public int hashCode() {
 		int result = name != null ? name.hashCode() : 0;
 		result = 31 * result + ( typeImplementorClass != null ? typeImplementorClass.hashCode() : 0 );
 		result = 31 * result + ( registrationKeys != null ? Arrays.hashCode( registrationKeys ) : 0 );
 		result = 31 * result + ( parameters != null ? parameters.hashCode() : 0 );
 		return result;
 	}
 
 	@Override
 	public String toString() {
 		return "TypeDefinition{" +
 				"name='" + name + '\'' +
 				", typeImplementorClass=" + typeImplementorClass +
 				", registrationKeys=" + Arrays.toString( registrationKeys ) +
 				", parameters=" + parameters +
 				'}';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/package-info.java b/hibernate-core/src/main/java/org/hibernate/boot/package-info.java
new file mode 100644
index 0000000000..903a99a73a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/package-info.java
@@ -0,0 +1,31 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+
+/**
+ * This package contains the contracts that make up the Hibernate native
+ * bootstrapping API (building a SessionFactory).
+ *
+ * See the <i>Native Bootstrapping</i> guide for details.
+ */
+package org.hibernate.boot;
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
index 81fe81f99d..a17af4a658 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
@@ -1,235 +1,251 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import java.util.List;
+import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
+import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
+import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.BasicType;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Describes the options used while building the Metadata object (during
  * {@link org.hibernate.boot.MetadataBuilder#build()} processing).
  *
  * @author Steve Ebersole
  */
 public interface MetadataBuildingOptions {
 	/**
 	 * Access to the service registry.
 	 *
 	 * @return The service registry
 	 */
 	StandardServiceRegistry getServiceRegistry();
 
 	/**
 	 * Access to the mapping defaults.
 	 *
 	 * @return The mapping defaults
 	 */
 	MappingDefaults getMappingDefaults();
 
 	/**
 	 * Access the list of BasicType registrations.  These are the BasicTypes explicitly
 	 * registered via calls to:<ul>
-	 *     <li>{@link org.hibernate.boot.MetadataBuilder#with(org.hibernate.type.BasicType)}</li>
-	 *     <li>{@link org.hibernate.boot.MetadataBuilder#with(org.hibernate.usertype.UserType, java.lang.String[])}</li>
-	 *     <li>{@link org.hibernate.boot.MetadataBuilder#with(org.hibernate.usertype.CompositeUserType, java.lang.String[])}</li>
+	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.type.BasicType)}</li>
+	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.usertype.UserType, java.lang.String[])}</li>
+	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.usertype.CompositeUserType, java.lang.String[])}</li>
 	 * </ul>
 	 *
 	 * @return The BasicType registrations
 	 */
 	List<BasicType> getBasicTypeRegistrations();
 
 	/**
 	 * Access to the Jandex index passed by call to
-	 * {@link org.hibernate.boot.MetadataBuilder#with(org.jboss.jandex.IndexView)}, if any.
+	 * {@link org.hibernate.boot.MetadataBuilder#applyIndexView(org.jboss.jandex.IndexView)}, if any.
 	 *
 	 * @return The Jandex index
 	 */
 	IndexView getJandexView();
 
 	/**
 	 * Access to the options to be used for scanning
 	 *
 	 * @return The scan options
 	 */
 	ScanOptions getScanOptions();
 
 	/**
 	 * Access to the environment for scanning.  Consider this temporary; see discussion on
 	 * {@link ScanEnvironment}
 	 *
 	 * @return The scan environment
 	 */
 	ScanEnvironment getScanEnvironment();
 
 	/**
 	 * Access to the Scanner to be used for scanning.  Can be:<ul>
 	 *     <li>A Scanner instance</li>
 	 *     <li>A Class reference to the Scanner implementor</li>
 	 *     <li>A String naming the Scanner implementor</li>
 	 * </ul>
 	 *
 	 * @return The scanner
 	 */
 	Object getScanner();
 
 	/**
 	 * Access to the ArchiveDescriptorFactory to be used for scanning
 	 *
 	 * @return The ArchiveDescriptorFactory
 	 */
 	ArchiveDescriptorFactory getArchiveDescriptorFactory();
 
 	/**
 	 * Access the temporary ClassLoader passed to us as defined by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}, if any.
 	 *
 	 * @return The tempo ClassLoader
 	 */
 	ClassLoader getTempClassLoader();
 
 	ImplicitNamingStrategy getImplicitNamingStrategy();
 	PhysicalNamingStrategy getPhysicalNamingStrategy();
 
 	ReflectionManager getReflectionManager();
 
 	/**
 	 * Access to the SharedCacheMode for determining whether we should perform second level
 	 * caching or not.
 	 *
 	 * @return The SharedCacheMode
 	 */
 	SharedCacheMode getSharedCacheMode();
 
 	/**
 	 * Access to any implicit cache AccessType.
 	 *
 	 * @return The implicit cache AccessType
 	 */
 	AccessType getImplicitCacheAccessType();
 
 	/**
 	 * Access to whether we should be using the new identifier generator scheme.
 	 * {@code true} indicates to use the new schema, {@code false} indicates to use the
 	 * legacy scheme.
 	 *
 	 * @return Whether to use the new identifier generator scheme
 	 */
 	boolean isUseNewIdentifierGenerators();
 
 	/**
 	 * Access to the MultiTenancyStrategy for this environment.
 	 *
 	 * @return The MultiTenancyStrategy
 	 */
 	MultiTenancyStrategy getMultiTenancyStrategy();
 
 	/**
 	 * Access to all explicit cache region mappings.
 	 *
 	 * @return Explicit cache region mappings.
 	 */
 	List<CacheRegionDefinition> getCacheRegionDefinitions();
 
 	/**
 	 * Whether explicit discriminator declarations should be ignored for joined
 	 * subclass style inheritance.
 	 *
 	 * @return {@code true} indicates they should be ignored; {@code false}
 	 * indicates they should not be ignored.
 	 *
-	 * @see org.hibernate.boot.MetadataBuilder#withExplicitDiscriminatorsForJoinedSubclassSupport
+	 * @see org.hibernate.boot.MetadataBuilder#enableExplicitDiscriminatorsForJoinedSubclassSupport
 	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	boolean ignoreExplicitDiscriminatorsForJoinedInheritance();
 
 	/**
 	 * Whether we should do discrimination implicitly joined subclass style inheritance when no
 	 * discriminator info is provided.
 	 *
 	 * @return {@code true} indicates we should do discrimination; {@code false} we should not.
 	 *
-	 * @see org.hibernate.boot.MetadataBuilder#withImplicitDiscriminatorsForJoinedSubclassSupport
+	 * @see org.hibernate.boot.MetadataBuilder#enableImplicitDiscriminatorsForJoinedSubclassSupport
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	boolean createImplicitDiscriminatorsForJoinedInheritance();
 
 	/**
 	 * Whether we should implicitly force discriminators into SQL selects.  By default,
 	 * Hibernate will not.  This can be specified per discriminator in the mapping as well.
 	 *
 	 * @return {@code true} indicates we should force the discriminator in selects for any mappings
 	 * which do not say explicitly.
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
 	 */
 	boolean shouldImplicitlyForceDiscriminatorInSelect();
 
 	/**
 	 * Should we use nationalized variants of character data (e.g. NVARCHAR rather than VARCHAR)
 	 * by default?
 	 *
-	 * @see org.hibernate.boot.MetadataBuilder#withNationalizedCharacterData
+	 * @see org.hibernate.boot.MetadataBuilder#enableGlobalNationalizedCharacterDataSupport
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
 	 *
 	 * @return {@code true} if nationalized character data should be used by default; {@code false} otherwise.
 	 */
 	public boolean useNationalizedCharacterData();
 
+	boolean isSpecjProprietarySyntaxEnabled();
+
 	/**
+	 * Retrieve the ordering in which sources should be processed.
 	 *
-	 * @return
+	 * @return The order in which sources should be processed.
+	 */
+	List<MetadataSourceType> getSourceProcessOrdering();
+
+	/**
+	 * Access to any SQL functions explicitly registered with the MetadataBuilder.  This
+	 * does not include Dialect defined functions, etc.
 	 *
-	 * @see org.hibernate.cfg.AvailableSettings#
+	 * @return The SQLFunctions registered through MetadataBuilder
 	 */
-	boolean isSpecjProprietarySyntaxEnabled();
+	Map<String,SQLFunction> getSqlFunctions();
 
 	/**
-	 * Retrieve the ordering in which sources should be processed.
+	 * Access to any AuxiliaryDatabaseObject explicitly registered with the MetadataBuilder.  This
+	 * does not include AuxiliaryDatabaseObject defined in mappings.
 	 *
-	 * @return
+	 * @return The AuxiliaryDatabaseObject registered through MetadataBuilder
 	 */
-	List<MetadataSourceType> getSourceProcessOrdering();
+	List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList();
+
+	List<AttributeConverterDefinition> getAttributeConverters();
 
 //	/**
 //	 * Obtain the selected strategy for resolving members identifying persistent attributes
 //	 *
 //	 * @return The select resolver strategy
 //	 */
 //	PersistentAttributeMemberResolver getPersistentAttributeMemberResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
index 5df92c5b71..73facab33a 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
@@ -1,135 +1,211 @@
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
 package org.hibernate.cfg;
 
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 import javax.persistence.AttributeConverter;
+import javax.persistence.Converter;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class AttributeConverterDefinition {
 	private static final Logger log = Logger.getLogger( AttributeConverterDefinition.class );
 
 	private final AttributeConverter attributeConverter;
 	private final boolean autoApply;
 	private final Class entityAttributeType;
 	private final Class databaseColumnType;
 
+	/**
+	 * Build an AttributeConverterDefinition from the AttributeConverter Class reference and
+	 * whether or not to auto-apply it.
+	 *
+	 * @param attributeConverterClass The AttributeConverter Class
+	 * @param autoApply Should the AttributeConverter be auto-applied?
+	 *
+	 * @return The constructed definition
+	 */
+	public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
+		return new AttributeConverterDefinition(
+				instantiateAttributeConverter( attributeConverterClass ),
+				autoApply
+		);
+	}
+
+	private static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
+		try {
+			return attributeConverterClass.newInstance();
+		}
+		catch (Exception e) {
+			throw new AnnotationException(
+					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]",
+					e
+			);
+		}
+	}
+
+	/**
+	 * Build an AttributeConverterDefinition from the AttributeConverter Class reference.  The
+	 * converter is searched for a {@link Converter} annotation	 to determine whether it should
+	 * be treated as auto-apply.  If the annotation is present, {@link Converter#autoApply()} is
+	 * used to make that determination.  If the annotation is not present, {@code false} is assumed.
+	 *
+	 * @param attributeConverterClass The converter class
+	 *
+	 * @return The constructed definition
+	 */
+	public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass) {
+		return from( instantiateAttributeConverter( attributeConverterClass ) );
+	}
+
+	/**
+	 * Build an AttributeConverterDefinition from an AttributeConverter instance.  The
+	 * converter is searched for a {@link Converter} annotation	 to determine whether it should
+	 * be treated as auto-apply.  If the annotation is present, {@link Converter#autoApply()} is
+	 * used to make that determination.  If the annotation is not present, {@code false} is assumed.
+	 *
+	 * @param attributeConverter The AttributeConverter instance
+	 *
+	 * @return The constructed definition
+	 */
+	public static AttributeConverterDefinition from(AttributeConverter attributeConverter) {
+		boolean autoApply = false;
+		Converter converterAnnotation = attributeConverter.getClass().getAnnotation( Converter.class );
+		if ( converterAnnotation != null ) {
+			autoApply = converterAnnotation.autoApply();
+		}
+
+		return new AttributeConverterDefinition( attributeConverter, autoApply );
+	}
+
+	/**
+	 * Build an AttributeConverterDefinition from the AttributeConverter instance and
+	 * whether or not to auto-apply it.
+	 *
+	 * @param attributeConverter The AttributeConverter instance
+	 * @param autoApply Should the AttributeConverter be auto-applied?
+	 *
+	 * @return The constructed definition
+	 */
+	public static AttributeConverterDefinition from(AttributeConverter attributeConverter, boolean autoApply) {
+		return new AttributeConverterDefinition( attributeConverter, autoApply );
+	}
+
 	public AttributeConverterDefinition(AttributeConverter attributeConverter, boolean autoApply) {
 		this.attributeConverter = attributeConverter;
 		this.autoApply = autoApply;
 
 		final Class attributeConverterClass = attributeConverter.getClass();
 		final ParameterizedType attributeConverterSignature = extractAttributeConverterParameterizedType( attributeConverterClass );
 
 		if ( attributeConverterSignature.getActualTypeArguments().length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterSignature.getActualTypeArguments().length > 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		entityAttributeType = (Class) attributeConverterSignature.getActualTypeArguments()[0];
 		if ( entityAttributeType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 
 		databaseColumnType = (Class) attributeConverterSignature.getActualTypeArguments()[1];
 		if ( databaseColumnType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'database column' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 	}
 
 	private ParameterizedType extractAttributeConverterParameterizedType(Class attributeConverterClass) {
 		for ( Type type : attributeConverterClass.getGenericInterfaces() ) {
 			if ( ParameterizedType.class.isInstance( type ) ) {
 				final ParameterizedType parameterizedType = (ParameterizedType) type;
 				if ( AttributeConverter.class.equals( parameterizedType.getRawType() ) ) {
 					return parameterizedType;
 				}
 			}
 		}
 
 		throw new AssertionFailure(
 				"Could not extract ParameterizedType representation of AttributeConverter definition " +
 						"from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]"
 		);
 	}
 
 	public AttributeConverter getAttributeConverter() {
 		return attributeConverter;
 	}
 
 	public boolean isAutoApply() {
 		return autoApply;
 	}
 
 	public Class getEntityAttributeType() {
 		return entityAttributeType;
 	}
 
 	public Class getDatabaseColumnType() {
 		return databaseColumnType;
 	}
 
 	private static Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	@Override
 	public String toString() {
 		return String.format(
 				"%s[converterClass=%s, domainType=%s, jdbcType=%s]",
 				this.getClass().getName(),
 				attributeConverter.getClass().getName(),
 				entityAttributeType.getName(),
 				databaseColumnType.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 25921052a5..4e201515a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,913 +1,939 @@
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
 
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.CompositeCustomType;
 import org.hibernate.type.CustomType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.SharedCacheMode;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 /**
  * Represents one approach for bootstrapping Hibernate.  In fact, historically this was
  * <b>the</b> way to bootstrap Hibernate.
  * <p/>
  * The approach here is to define all configuration and mapping sources in one API
  * and to then build the {@link org.hibernate.SessionFactory} in one-shot.  The configuration
  * and mapping sources defined here are just held here until the SessionFactory is built.  This
  * is an important distinction from the legacy behavior of this class, where we would try to
  * incrementally build the mappings from sources as they were added.  The ramification of this
  * change in behavior is that users can add configuration and mapping sources here, but they can
  * no longer query the in-flight state of mappings ({@link org.hibernate.mapping.PersistentClass},
  * {@link org.hibernate.mapping.Collection}, etc) here.
  * <p/>
  * Note: Internally this class uses the new bootstrapping approach when asked to build the
  * SessionFactory.
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @see org.hibernate.SessionFactory
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration {
     private static final CoreMessageLogger log = CoreLogging.messageLogger( Configuration.class );
 
 	public static final String ARTEFACT_PROCESSING_ORDER = AvailableSettings.ARTIFACT_PROCESSING_ORDER;
 
 	private final BootstrapServiceRegistry bootstrapServiceRegistry;
 	private final MetadataSources metadataSources;
 
 	// used during processing mappings
 	private ImplicitNamingStrategy implicitNamingStrategy;
 	private PhysicalNamingStrategy physicalNamingStrategy;
 	private List<BasicType> basicTypes = new ArrayList<BasicType>();
 	private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
 	private Map<String, NamedQueryDefinition> namedQueries;
 	private Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
 	private Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
 
+	private Map<String, SQLFunction> sqlFunctions;
+	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjectList;
+	private HashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
+
 	// used to build SF
 	private StandardServiceRegistryBuilder standardServiceRegistryBuilder;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 	private EntityTuplizerFactory entityTuplizerFactory;
 	private Interceptor interceptor;
 	private SessionFactoryObserver sessionFactoryObserver;
 	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 	private Properties properties;
 	private SharedCacheMode sharedCacheMode;
 
 	public Configuration() {
 		this( new BootstrapServiceRegistryBuilder().build() );
 	}
 
 	public Configuration(BootstrapServiceRegistry serviceRegistry) {
 		this.bootstrapServiceRegistry = serviceRegistry;
 		this.metadataSources = new MetadataSources( serviceRegistry );
 		reset();
 	}
 
 	public Configuration(MetadataSources metadataSources) {
 		this.bootstrapServiceRegistry = getBootstrapRegistry( metadataSources.getServiceRegistry() );
 		this.metadataSources = metadataSources;
 		reset();
 	}
 
 	private static BootstrapServiceRegistry getBootstrapRegistry(ServiceRegistry serviceRegistry) {
 		if ( BootstrapServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			return (BootstrapServiceRegistry) serviceRegistry;
 		}
 		else if ( StandardServiceRegistry.class.isInstance( serviceRegistry ) ) {
 			final StandardServiceRegistry ssr = (StandardServiceRegistry) serviceRegistry;
 			return (BootstrapServiceRegistry) ssr.getParentServiceRegistry();
 		}
 
 		throw new HibernateException(
 				"No ServiceRegistry was passed to Configuration#buildSessionFactory " +
 						"and could not determine how to locate BootstrapServiceRegistry " +
 						"from Configuration instantiation"
 		);
 	}
 
 	protected void reset() {
 		implicitNamingStrategy = ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
 		physicalNamingStrategy = PhysicalNamingStrategyStandardImpl.INSTANCE;
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
 		namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>(  );
 
 		standardServiceRegistryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry );
 		entityTuplizerFactory = new EntityTuplizerFactory();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = new Properties(  );
 		properties.putAll( standardServiceRegistryBuilder.getSettings());
 	}
 
 
 	// properties/settings ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value currently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		Object o = properties.get( propertyName );
 		return o instanceof String ? (String) o : null;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param properties The properties to add.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration addProperties(Properties properties) {
 		this.properties.putAll( properties );
 		return this;
 	}
 
 	public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
 		this.implicitNamingStrategy = implicitNamingStrategy;
 	}
 
 	public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
 		this.physicalNamingStrategy = physicalNamingStrategy;
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		return configure( StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		standardServiceRegistryBuilder.configure( resource );
 		// todo : still need to have StandardServiceRegistryBuilder handle the "other cfg.xml" elements.
 		//		currently it just reads the config properties
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
 	}
 
 	/**
 	 * Intended for internal testing use only!!!
 	 */
 	public StandardServiceRegistryBuilder getStandardServiceRegistryBuilder() {
 		return standardServiceRegistryBuilder;
 	}
 
 	//	private void doConfigure(JaxbHibernateConfiguration jaxbHibernateConfiguration) {
 //		standardServiceRegistryBuilder.configure( jaxbHibernateConfiguration );
 //
 //		for ( JaxbMapping jaxbMapping : jaxbHibernateConfiguration.getSessionFactory().getMapping() ) {
 //			if ( StringHelper.isNotEmpty( jaxbMapping.getClazz() ) ) {
 //				addResource( jaxbMapping.getClazz().replace( '.', '/' ) + ".hbm.xml" );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getFile() ) ) {
 //				addFile( jaxbMapping.getFile() );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getJar() ) ) {
 //				addJar( new File( jaxbMapping.getJar() ) );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getPackage() ) ) {
 //				addPackage( jaxbMapping.getPackage() );
 //			}
 //			else if ( StringHelper.isNotEmpty( jaxbMapping.getResource() ) ) {
 //				addResource( jaxbMapping.getResource() );
 //			}
 //		}
 //	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		standardServiceRegistryBuilder.configure( url );
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		standardServiceRegistryBuilder.configure( configFile );
 		properties.putAll( standardServiceRegistryBuilder.getSettings() );
 		return this;
 	}
 
 	/**
 	 * @deprecated No longer supported.
 	 */
 	@Deprecated
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		return this;
 	}
 
 
 	// MetadataSources ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Configuration registerTypeContributor(TypeContributor typeContributor) {
 		typeContributorRegistrations.add( typeContributor );
 		return this;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public Configuration registerTypeOverride(BasicType type) {
 		basicTypes.add( type );
 		return this;
 	}
 
 
 	public Configuration registerTypeOverride(UserType type, String[] keys) {
 		basicTypes.add( new CustomType( type, keys ) );
 		return this;
 	}
 
 	public Configuration registerTypeOverride(CompositeUserType type, String[] keys) {
 		basicTypes.add( new CompositeCustomType( type, keys ) );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates inability to locate or parse
 	 * the specified mapping file.
 	 * @see #addFile(java.io.File)
 	 */
 	public Configuration addFile(String xmlFile) throws MappingException {
 		metadataSources.addFile( xmlFile );
 		return this;
 	}
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.
 	 */
 	public Configuration addFile(File xmlFile) throws MappingException {
 		metadataSources.addFile( xmlFile );
 		return this;
 	}
 
 	/**
 	 * @deprecated No longer supported.
 	 */
 	@Deprecated
 	public void add(XmlDocument metadataXml) {
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation
 	 * of the DOM structure of a particular mapping.  It is saved from a previous
 	 * call as a file with the name <tt>xmlFile + ".bin"</tt> where xmlFile is
 	 * the name of the original mapping file.
 	 * </p>
 	 * If a cached <tt>xmlFile + ".bin"</tt> exists and is newer than
 	 * <tt>xmlFile</tt> the <tt>".bin"</tt> file will be read directly. Otherwise
 	 * xmlFile is read and then serialized to <tt>xmlFile + ".bin"</tt> for use
 	 * the next time.
 	 *
 	 * @param xmlFile The cacheable mapping file to be added.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 */
 	public Configuration addCacheableFile(File xmlFile) throws MappingException {
 		metadataSources.addCacheableFile( xmlFile );
 		return this;
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param xmlFile The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
 		metadataSources.addCacheableFileStrictly( xmlFile );
 		return this;
 	}
 
 	/**
 	 * Add a cacheable mapping file.
 	 *
 	 * @param xmlFile The name of the file to be added.  This must be in a form
 	 * useable to simply construct a {@link java.io.File} instance.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public Configuration addCacheableFile(String xmlFile) throws MappingException {
 		metadataSources.addCacheableFile( xmlFile );
 		return this;
 	}
 
 
 	/**
 	 * @deprecated No longer supported
 	 */
 	@Deprecated
 	public Configuration addXML(String xml) throws MappingException {
 		return this;
 	}
 
 	/**
 	 * Read mappings from a <tt>URL</tt>
 	 *
 	 * @param url The url for the mapping document to be read.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the URL or processing
 	 * the mapping document.
 	 */
 	public Configuration addURL(URL url) throws MappingException {
 		metadataSources.addURL( url );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 *
 	 * @deprecated Use addURL, addResource, addFile, etc. instead
 	 */
 	@Deprecated
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		metadataSources.addDocument( doc );
 		return this;
 	}
 
 	/**
 	 * Read mappings from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the stream, or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		metadataSources.addInputStream( xmlInputStream );
 		return this;
 	}
 
 	/**
 	 * @deprecated This form (accepting a ClassLoader) is no longer supported.  Instead, add the ClassLoader
 	 * to the ClassLoaderService on the ServiceRegistry associated with this Configuration
 	 */
 	@Deprecated
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		return addResource( resourceName );
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup)
 	 * trying different class loaders.
 	 *
 	 * @param resourceName The resource name
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName) throws MappingException {
 		metadataSources.addResource( resourceName );
 		return this;
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class
 	 * named <tt>foo.bar.Foo</tt> is mapped by a file <tt>foo/bar/Foo.hbm.xml</tt>
 	 * which can be resolved as a classpath resource.
 	 *
 	 * @param persistentClass The mapped class
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addClass(Class persistentClass) throws MappingException {
 		metadataSources.addClass( persistentClass );
 		return this;
 	}
 
 	/**
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		metadataSources.addAnnotatedClass( annotatedClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws MappingException in case there is an error in the mapping data
 	 */
 	public Configuration addPackage(String packageName) throws MappingException {
 		metadataSources.addPackage( packageName );
 		return this;
 	}
 
 	/**
 	 * Read all mappings from a jar file
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addJar(File jar) throws MappingException {
 		metadataSources.addJar( jar );
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addDirectory(File dir) throws MappingException {
 		metadataSources.addDirectory( dir );
 		return this;
 	}
 
 
 	// SessionFactory building ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Retrieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	/**
 	 * Retrieve the user-supplied delegate to handle non-existent entity
 	 * scenarios.  May be null.
 	 *
 	 * @return The user-supplied delegate
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	/**
 	 * Specify a user-supplied delegate to be used to handle scenarios where an entity could not be
 	 * located by specified id.  This is mainly intended for EJB3 implementations to be able to
 	 * control how proxy initialization errors should be handled...
 	 *
 	 * @param entityNotFoundDelegate The delegate to use
 	 */
 	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
 		this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * SessionFactory will be immutable, so changes made to this Configuration after building the
 	 * SessionFactory will not affect it.
 	 *
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		log.debug( "Building session factory using provided StandardServiceRegistry" );
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder( (StandardServiceRegistry) serviceRegistry );
 		if ( implicitNamingStrategy != null ) {
-			metadataBuilder.with( implicitNamingStrategy );
+			metadataBuilder.applyImplicitNamingStrategy( implicitNamingStrategy );
 		}
 		if ( physicalNamingStrategy != null ) {
-			metadataBuilder.with( physicalNamingStrategy );
+			metadataBuilder.applyPhysicalNamingStrategy( physicalNamingStrategy );
 		}
 		if ( sharedCacheMode != null ) {
-			metadataBuilder.with( sharedCacheMode );
+			metadataBuilder.applySharedCacheMode( sharedCacheMode );
 		}
 		if ( !typeContributorRegistrations.isEmpty() ) {
 			for ( TypeContributor typeContributor : typeContributorRegistrations ) {
-				metadataBuilder.with( typeContributor );
+				metadataBuilder.applyTypes( typeContributor );
 			}
 		}
 		if ( !basicTypes.isEmpty() ) {
 			for ( BasicType basicType : basicTypes ) {
-				metadataBuilder.with( basicType );
+				metadataBuilder.applyBasicType( basicType );
+			}
+		}
+		if ( sqlFunctions != null ) {
+			for ( Map.Entry<String, SQLFunction> entry : sqlFunctions.entrySet() ) {
+				metadataBuilder.applySqlFunction( entry.getKey(), entry.getValue() );
+			}
+		}
+		if ( auxiliaryDatabaseObjectList != null ) {
+			for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjectList ) {
+				metadataBuilder.applyAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
+			}
+		}
+		if ( attributeConverterDefinitionsByClass != null ) {
+			for ( AttributeConverterDefinition attributeConverterDefinition : attributeConverterDefinitionsByClass.values() ) {
+				metadataBuilder.applyAttributeConverter( attributeConverterDefinition );
 			}
 		}
 
+
 		final Metadata metadata = metadataBuilder.build();
 
 		final SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
 		if ( interceptor != null && interceptor != EmptyInterceptor.INSTANCE ) {
-			sessionFactoryBuilder.with( interceptor );
+			sessionFactoryBuilder.applyInterceptor( interceptor );
 		}
 		if ( getSessionFactoryObserver() != null ) {
-			sessionFactoryBuilder.add( getSessionFactoryObserver() );
+			sessionFactoryBuilder.addSessionFactoryObservers( getSessionFactoryObserver() );
 		}
 		if ( entityNotFoundDelegate != null ) {
-			sessionFactoryBuilder.with( entityNotFoundDelegate );
+			sessionFactoryBuilder.applyEntityNotFoundDelegate( entityNotFoundDelegate );
 		}
 		if ( entityTuplizerFactory != null ) {
-			sessionFactoryBuilder.with( entityTuplizerFactory );
+			sessionFactoryBuilder.applyEntityTuplizerFactory( entityTuplizerFactory );
 		}
 
 		return sessionFactoryBuilder.build();
 	}
 
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		log.debug( "Building session factory using internal StandardServiceRegistryBuilder" );
 		standardServiceRegistryBuilder.applySettings( properties );
 		return buildSessionFactory( standardServiceRegistryBuilder.build() );
 	}
 
 
 
-	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	// these just "pass through" to MetadataSources
-
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
-		metadataSources.addAuxiliaryDatabaseObject( object );
+	public Map<String,SQLFunction> getSqlFunctions() {
+		return sqlFunctions;
 	}
 
-	public Map getSqlFunctions() {
-		return metadataSources.getSqlFunctions();
+	public void addSqlFunction(String functionName, SQLFunction function) {
+		if ( sqlFunctions == null ) {
+			sqlFunctions = new HashMap<String, SQLFunction>();
+		}
+		sqlFunctions.put( functionName, function );
 	}
 
-	public void addSqlFunction(String functionName, SQLFunction function) {
-		metadataSources.addSqlFunction( functionName, function );
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
+		if ( auxiliaryDatabaseObjectList == null ) {
+			auxiliaryDatabaseObjectList = new ArrayList<AuxiliaryDatabaseObject>();
+		}
+		auxiliaryDatabaseObjectList.add( object );
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
-		metadataSources.addAttributeConverter( attributeConverterClass, autoApply );
+		addAttributeConverter( AttributeConverterDefinition.from( attributeConverterClass, autoApply ) );
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
-		metadataSources.addAttributeConverter( attributeConverterClass );
+		addAttributeConverter( AttributeConverterDefinition.from( attributeConverterClass ) );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter) {
-		metadataSources.addAttributeConverter( attributeConverter );
+		addAttributeConverter( AttributeConverterDefinition.from( attributeConverter ) );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
-		metadataSources.addAttributeConverter( attributeConverter, autoApply );
+		addAttributeConverter( AttributeConverterDefinition.from( attributeConverter, autoApply ) );
 	}
 
 	public void addAttributeConverter(AttributeConverterDefinition definition) {
-		metadataSources.addAttributeConverter( definition );
+		if ( attributeConverterDefinitionsByClass == null ) {
+			attributeConverterDefinitionsByClass = new HashMap<Class, AttributeConverterDefinition>();
+		}
+		attributeConverterDefinitionsByClass.put( definition.getAttributeConverter().getClass(), definition );
 	}
 
 	/**
 	 * Sets the SharedCacheMode to use.
 	 *
 	 * Note that at the moment, only {@link javax.persistence.SharedCacheMode#ALL} has
 	 * any effect in terms of {@code hbm.xml} binding.
 	 *
 	 * @param sharedCacheMode The SharedCacheMode to use
 	 */
 	public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
 		this.sharedCacheMode = sharedCacheMode;
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// todo : decide about these
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public java.util.Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
 		return namedEntityGraphMap == null
 				? Collections.<NamedEntityGraphDefinition>emptyList()
 				: namedEntityGraphMap.values();
 	}
 
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
 		return namedProcedureCallMap;
 	}
 
 	/**
 	 * @deprecated Does nothing
 	 */
 	@Deprecated
 	public void buildMappings() {
 	}
 
 
 
 
 
 
 
 
 
 
 
 	/**
 	 * Generate DDL for dropping tables
 	 *
 	 * @param dialect The dialect for which to generate the drop script
 
 	 * @return The sequence of DDL commands to drop the schema objects
 
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
 		return new String[0];
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 *
 	 * @return The sequence of DDL commands to create the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
 		return new String[0];
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
index d241d768a4..9bf9fb456d 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
@@ -1,180 +1,180 @@
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
  */
 package org.hibernate.internal.util.collections;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 /**
  * Various help for handling collections.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class CollectionHelper {
     public static final int MINIMUM_INITIAL_CAPACITY = 16;
 	public static final float LOAD_FACTOR = 0.75f;
 
 	/**
 	 * @deprecated use  {@link java.util.Collections#EMPTY_LIST} or {@link java.util.Collections#emptyList()}  instead
 	 */
 	@Deprecated
 	public static final List EMPTY_LIST = Collections.EMPTY_LIST;
 	/**
 	 * @deprecated use {@link java.util.Collections#EMPTY_LIST} or {@link java.util.Collections#emptyList()}  instead
 	 */
 	@Deprecated
 	public static final Collection EMPTY_COLLECTION = Collections.EMPTY_LIST;
 	/**
 	 * @deprecated use {@link java.util.Collections#EMPTY_MAP} or {@link java.util.Collections#emptyMap()}  instead
 	 */
 	@Deprecated
 	public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
 
 	private CollectionHelper() {
 	}
 
 	/**
 	 * Build a properly sized map, especially handling load size and load factor to prevent immediate resizing.
 	 * <p/>
 	 * Especially helpful for copy map contents.
 	 *
 	 * @param size The size to make the map.
 	 * @return The sized map.
 	 */
 	public static <K,V> Map<K,V> mapOfSize(int size) {
 		return new HashMap<K,V>( determineProperSizing( size ), LOAD_FACTOR );
 	}
 
 	/**
 	 * Given a map, determine the proper initial size for a new Map to hold the same number of values.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param original The original map
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(Map original) {
 		return determineProperSizing( original.size() );
 	}
 
 	/**
 	 * Given a set, determine the proper initial size for a new set to hold the same number of values.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param original The original set
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(Set original) {
 		return determineProperSizing( original.size() );
 	}
 
 	/**
 	 * Determine the proper initial size for a new collection in order for it to hold the given a number of elements.
 	 * Specifically we want to account for load size and load factor to prevent immediate resizing.
 	 *
 	 * @param numberOfElements The number of elements to be stored.
 	 * @return The proper size.
 	 */
 	public static int determineProperSizing(int numberOfElements) {
 		int actual = ( (int) (numberOfElements / LOAD_FACTOR) ) + 1;
 		return Math.max( actual, MINIMUM_INITIAL_CAPACITY );
 	}
 
 	/**
 	 * Create a properly sized {@link ConcurrentHashMap} based on the given expected number of elements.
 	 *
 	 * @param expectedNumberOfElements The expected number of elements for the created map
 	 * @param <K> The map key type
 	 * @param <V> The map value type
 	 *
 	 * @return The created map.
 	 */
 	public static <K,V> ConcurrentHashMap<K,V> concurrentMap(int expectedNumberOfElements) {
 		return concurrentMap( expectedNumberOfElements, LOAD_FACTOR );
 	}
 
 	/**
 	 * Create a properly sized {@link ConcurrentHashMap} based on the given expected number of elements and an
 	 * explicit load factor
 	 *
 	 * @param expectedNumberOfElements The expected number of elements for the created map
 	 * @param loadFactor The collection load factor
 	 * @param <K> The map key type
 	 * @param <V> The map value type
 	 *
 	 * @return The created map.
 	 */
 	public static <K,V> ConcurrentHashMap<K,V> concurrentMap(int expectedNumberOfElements, float loadFactor) {
 		final int size = expectedNumberOfElements + 1 + (int) ( expectedNumberOfElements * loadFactor );
 		return new ConcurrentHashMap<K, V>( size, loadFactor );
 	}
 
-	public static <T> List<T> arrayList(int anticipatedSize) {
+	public static <T> ArrayList<T> arrayList(int anticipatedSize) {
 		return new ArrayList<T>( anticipatedSize );
 	}
 
 	public static <T> Set<T> makeCopy(Set<T> source) {
 		if ( source == null ) {
 			return null;
 		}
 
 		final int size = source.size();
 		final Set<T> copy = new HashSet<T>( size + 1 );
 		copy.addAll( source );
 		return copy;
 	}
 
     public static boolean isEmpty(Collection collection) {
         return collection == null || collection.isEmpty();
     }
 
     public static boolean isEmpty(Map map) {
         return map == null || map.isEmpty();
     }
 
     public static boolean isNotEmpty(Collection collection) {
         return !isEmpty( collection );
     }
 
     public static boolean isNotEmpty(Map map) {
         return !isEmpty( map );
     }
 
 	public static boolean isEmpty(Object[] objects){
 		return objects == null || objects.length==0;
 	}
 
 	public static <X,Y> Map<X, Y> makeCopy(Map<X, Y> map) {
 		final Map<X,Y> copy = mapOfSize( map.size() + 1 );
 		copy.putAll( map );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 404b592884..002ddff67e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,705 +1,711 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.schema.spi.SchemaManagementTool;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class SchemaExport {
     private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SchemaExport.class );
 
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	public static enum Type {
 		CREATE,
 		DROP,
 		NONE,
 		BOTH;
 
 		public boolean doCreate() {
 			return this == BOTH || this == CREATE;
 		}
 
 		public boolean doDrop() {
 			return this == BOTH || this == DROP;
 		}
 	}
 
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final String[] dropSQL;
 	private final String[] createSQL;
 	private final String importFiles;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 	private ImportSqlCommandExtractor importSqlCommandExtractor = ImportSqlCommandExtractorInitiator.DEFAULT_EXTRACTOR;
 
 	private String outputFile;
 	private String delimiter;
 	private boolean haltOnError;
 
 	/**
 	 * Builds a SchemaExport object.
 	 * @param metadata The metadata object holding the mapping info to be exported
 	 */
 	public SchemaExport(MetadataImplementor metadata) {
 		this( metadata.getMetadataBuildingOptions().getServiceRegistry(), metadata );
 	}
 
 	/**
 	 * Builds a SchemaExport object.
 	 *
 	 * @param serviceRegistry The registry of services available for use.  Should, at a minimum, contain
 	 * the JdbcServices service.
 	 * @param metadata The metadata object holding the mapping info to be exported
 	 */
 	public SchemaExport(ServiceRegistry serviceRegistry, MetadataImplementor metadata) {
 		this(
 				new SuppliedConnectionProviderConnectionHelper(
 						serviceRegistry.getService( ConnectionProvider.class )
 				),
 				serviceRegistry,
 				metadata
 		);
 	}
 
 	private SchemaExport(
 			ConnectionHelper connectionHelper,
 			ServiceRegistry serviceRegistry,
 			MetadataImplementor metadata) {
 		this.connectionHelper = connectionHelper;
 		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = serviceRegistry.getService( JdbcEnvironment.class ).getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				serviceRegistry.getService( ConfigurationService.class ).getSettings(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		// uses the schema management tool service to generate the create/drop scripts
 		// longer term this class should instead just leverage the tool for its execution phase.
 		// That is part of the larger task to consolidate Hibernate and JPA schema management
 
 		SchemaManagementTool schemaManagementTool = serviceRegistry.getService( SchemaManagementTool.class );
 		final List<String> commands = new ArrayList<String>();
 		final org.hibernate.tool.schema.spi.Target target = new org.hibernate.tool.schema.spi.Target() {
 			@Override
 			public boolean acceptsImportScriptActions() {
 				return false;
 			}
 
 			@Override
 			public void prepare() {
 				commands.clear();
 			}
 
 			@Override
 			public void accept(String command) {
 				commands.add( command );
 			}
 
 			@Override
 			public void release() {
 			}
 		};
 
 		final Map settings = serviceRegistry.getService( ConfigurationService.class ).getSettings();
 
 		schemaManagementTool.getSchemaDropper( settings ).doDrop( metadata, false, target );
 		this.dropSQL = commands.toArray( new String[commands.size()] );
 
 		schemaManagementTool.getSchemaCreator( settings ).doCreation( metadata, false, target );
 		this.createSQL = commands.toArray( new String[commands.size()] );
 	}
 
 	/**
 	 * Intended for testing use
 	 *
 	 * @param connectionHelper Access to the JDBC Connection
 	 * @param metadata The metadata object holding the mapping info to be exported
 	 */
 	public SchemaExport(
 			ConnectionHelper connectionHelper,
 			MetadataImplementor metadata) {
 		this(
 				connectionHelper,
 				metadata.getMetadataBuildingOptions().getServiceRegistry(),
 				metadata
 		);
 	}
 
 	/**
 	 * Create a SchemaExport for the given Metadata, using the supplied connection for connectivity.
 	 *
 	 * @param metadata The metadata object holding the mapping info to be exported
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(MetadataImplementor metadata, Connection connection) throws HibernateException {
 		this( new SuppliedConnectionHelper( connection ), metadata );
 	}
 
 	/**
 	 * @deprecated Use one of the forms accepting {@link MetadataImplementor}, rather
 	 * than {@link Configuration}, instead.
 	 */
 	@Deprecated
 	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
 		throw new UnsupportedOperationException(
 				"Attempt to use unsupported SchemaExport constructor accepting org.hibernate.cfg.Configuration; " +
 						"one of the forms accepting org.hibernate.boot.spi.MetadataImplementor should be used instead"
 		);
 	}
 
 	/**
 	 * @deprecated Use one of the forms accepting {@link MetadataImplementor}, rather
 	 * than {@link Configuration}, instead.
 	 */
 	@Deprecated
 	public SchemaExport(Configuration configuration) {
 		throw new UnsupportedOperationException(
 				"Attempt to use unsupported SchemaExport constructor accepting org.hibernate.cfg.Configuration; " +
 						"one of the forms accepting org.hibernate.boot.spi.MetadataImplementor should be used instead"
 		);
 	}
 
 	/**
 	 * @deprecated Use one of the forms accepting {@link MetadataImplementor}, rather
 	 * than {@link Configuration}, instead.
 	 */
 	@Deprecated
 	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
 		throw new UnsupportedOperationException(
 				"Attempt to use unsupported SchemaExport constructor accepting org.hibernate.cfg.Configuration; " +
 						"one of the forms accepting org.hibernate.boot.spi.MetadataImplementor should be used instead"
 		);
 	}
 
 	public SchemaExport(
 			ConnectionHelper connectionHelper,
 			String[] dropSql,
 			String[] createSql) {
 		this.connectionHelper = connectionHelper;
 		this.dropSQL = dropSql;
 		this.createSQL = createSql;
 		this.importFiles = "";
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	/**
 	 * For generating a export script file, this is the file which will be written.
 	 *
 	 * @param filename The name of the file to which to write the export script.
 	 * @return this
 	 */
 	public SchemaExport setOutputFile(String filename) {
 		outputFile = filename;
 		return this;
 	}
 
 	/**
 	 * Set the end of statement delimiter
 	 *
 	 * @param delimiter The delimiter
 	 * @return this
 	 */
 	public SchemaExport setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 		return this;
 	}
 
 	/**
 	 * Should we format the sql strings?
 	 *
 	 * @param format Should we format SQL strings
 	 * @return this
 	 */
 	public SchemaExport setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		return this;
 	}
 
 	/**
 	 * Set <i>import.sql</i> command extractor. By default {@link SingleLineSqlCommandExtractor} is used.
 	 *
 	 * @param importSqlCommandExtractor <i>import.sql</i> command extractor.
 	 * @return this
 	 */
 	public SchemaExport setImportSqlCommandExtractor(ImportSqlCommandExtractor importSqlCommandExtractor) {
 		this.importSqlCommandExtractor = importSqlCommandExtractor;
 		return this;
 	}
 
 	/**
 	 * Should we stop once an error occurs?
 	 *
 	 * @param haltOnError True if export should stop after error.
 	 * @return this
 	 */
 	public SchemaExport setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 		return this;
 	}
 
 	/**
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		create( Target.interpret( script, export ) );
 	}
 
 	/**
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param output the target of the script.
 	 */
 	public void create(Target output) {
 		// need to drop tables before creating so need to specify Type.BOTH
 		execute( output, Type.BOTH );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		drop( Target.interpret( script, export ) );
 	}
 
 	public void drop(Target output) {
 		execute( output, Type.DROP );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 		execute( Target.interpret( script, export ), interpretType( justDrop, justCreate ) );
 	}
 
 	private Type interpretType(boolean justDrop, boolean justCreate) {
 		if ( justDrop ) {
 			return Type.DROP;
 		}
 		else if ( justCreate ) {
 			return Type.CREATE;
 		}
 		else {
 			return Type.BOTH;
 		}
 	}
 
 	public void execute(Target output, Type type) {
 		if ( (outputFile == null && output == Target.NONE) || type == SchemaExport.Type.NONE ) {
 			return;
 		}
 		exceptions.clear();
 
 		LOG.runningHbm2ddlSchemaExport();
 
 		final List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		for ( String currentFile : importFiles.split(",") ) {
 			try {
 				final String resourceName = currentFile.trim();
 				InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
 				importFileReaders.add( new NamedReader( resourceName, stream ) );
 			}
 			catch ( HibernateException e ) {
 				LOG.debugf("Import file not found: %s", currentFile);
 			}
 		}
 
 		final List<Exporter> exporters = new ArrayList<Exporter>();
 		try {
 			// prepare exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( output.doScript() ) {
 				exporters.add( new ScriptExporter() );
 			}
 			if ( outputFile != null ) {
 				exporters.add( new FileExporter( outputFile ) );
 			}
 			if ( output.doExport() ) {
 				exporters.add( new DatabaseExporter( connectionHelper, sqlExceptionHelper ) );
 			}
 
 			// perform exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( type.doDrop() ) {
 				perform( dropSQL, exporters );
 			}
 			if ( type.doCreate() ) {
 				perform( createSQL, exporters );
 				if ( ! importFileReaders.isEmpty() ) {
 					for ( NamedReader namedReader : importFileReaders ) {
 						importScript( namedReader, exporters );
 					}
 				}
 			}
 		}
 		catch (Exception e) {
 			exceptions.add( e );
 			LOG.schemaExportUnsuccessful( e );
 		}
 		finally {
 			// release exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.release();
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			// release the named readers from import scripts
 			for ( NamedReader namedReader : importFileReaders ) {
 				try {
 					namedReader.getReader().close();
 				}
 				catch (Exception ignore) {
 				}
 			}
             LOG.schemaExportComplete();
 		}
 	}
 
 	private void perform(String[] sqlCommands, List<Exporter> exporters) {
 		for ( String sqlCommand : sqlCommands ) {
 			String formatted = formatter.format( sqlCommand );
 	        if ( delimiter != null ) {
 				formatted += delimiter;
 			}
 			sqlStatementLogger.logStatement( sqlCommand, formatter );
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.export( formatted );
 				}
 				catch (Exception e) {
 					if ( haltOnError ) {
 						throw new HibernateException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
 					LOG.unsuccessfulCreate( sqlCommand );
 					LOG.error( e.getMessage() );
 				}
 			}
 		}
 	}
 
 	private void importScript(NamedReader namedReader, List<Exporter> exporters) throws Exception {
 		BufferedReader reader = new BufferedReader( namedReader.getReader() );
 		String[] statements = importSqlCommandExtractor.extractCommands( reader );
 		if (statements != null) {
 			for ( String statement : statements ) {
 				if ( statement != null ) {
 					String trimmedSql = statement.trim();
 					if ( trimmedSql.endsWith( ";" )) {
 						trimmedSql = trimmedSql.substring( 0, statement.length() - 1 );
 					}
 					if ( !StringHelper.isEmpty( trimmedSql ) ) {
 						try {
 							for ( Exporter exporter : exporters ) {
 								if ( exporter.acceptsImportScripts() ) {
 									exporter.export( trimmedSql );
 								}
 							}
 						}
 						catch ( Exception e ) {
 						  	if (haltOnError) {
 						  		throw new ImportScriptException( "Error during statement execution (file: '"
 						  				+ namedReader.getName() + "'): " + trimmedSql, e );
 							}
 						  	exceptions.add(e);
 						  	LOG.unsuccessful(trimmedSql);
 						  	LOG.error(e.getMessage());
 						}
 					}
 				}
 			}
 		}
 	}
 
 	private static class NamedReader {
 		private final Reader reader;
 		private final String name;
 
 		public NamedReader(String name, InputStream stream) {
 			this.name = name;
 			this.reader = new InputStreamReader( stream );
 		}
 
 		public Reader getReader() {
 			return reader;
 		}
 
 		public String getName() {
 			return name;
 		}
 	}
 
 	public static void main(String[] args) {
 		try {
 			final CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs( args );
 			StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( commandLineArgs );
 			try {
 				final MetadataImplementor metadata = buildMetadata( commandLineArgs, serviceRegistry );
 
 				SchemaExport schemaExport = new SchemaExport( serviceRegistry, metadata )
 						.setHaltOnError( commandLineArgs.halt )
 						.setOutputFile( commandLineArgs.outputFile )
 						.setDelimiter( commandLineArgs.delimiter )
 						.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 						.setFormat( commandLineArgs.format );
 				schemaExport.execute( commandLineArgs.script, commandLineArgs.export, commandLineArgs.drop, commandLineArgs.create );
 			}
 			finally {
 				StandardServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToCreateSchema( e );
 			e.printStackTrace();
 		}
 	}
 
 	private static StandardServiceRegistry buildStandardServiceRegistry(CommandLineArgs commandLineArgs)
 			throws Exception {
 		final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 
 		if ( commandLineArgs.cfgXmlFile != null ) {
 			ssrBuilder.configure( commandLineArgs.cfgXmlFile );
 		}
 
 		Properties properties = new Properties();
 		if ( commandLineArgs.propertiesFile != null ) {
 			properties.load( new FileInputStream( commandLineArgs.propertiesFile ) );
 		}
 		ssrBuilder.applySettings( properties );
 
 		if ( commandLineArgs.importFile != null ) {
 			ssrBuilder.applySetting( AvailableSettings.HBM2DDL_IMPORT_FILES, commandLineArgs.importFile );
 		}
 
 		return ssrBuilder.build();
 	}
 
 	private static MetadataImplementor buildMetadata(
 			CommandLineArgs parsedArgs,
 			StandardServiceRegistry serviceRegistry) throws Exception {
 		final MetadataSources metadataSources = new MetadataSources( serviceRegistry );
 
 		for ( String filename : parsedArgs.hbmXmlFiles ) {
 			metadataSources.addFile( filename );
 		}
 
 		for ( String filename : parsedArgs.jarFiles ) {
 			metadataSources.addJar( new File( filename ) );
 		}
 
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		if ( parsedArgs.implicitNamingStrategyImplName != null ) {
-			metadataBuilder.with(
-					strategySelector.resolveStrategy( ImplicitNamingStrategy.class, parsedArgs.implicitNamingStrategyImplName )
+			metadataBuilder.applyImplicitNamingStrategy(
+					strategySelector.resolveStrategy(
+							ImplicitNamingStrategy.class,
+							parsedArgs.implicitNamingStrategyImplName
+					)
 			);
 		}
 		if ( parsedArgs.physicalNamingStrategyImplName != null ) {
-			metadataBuilder.with(
-					strategySelector.resolveStrategy( PhysicalNamingStrategy.class, parsedArgs.physicalNamingStrategyImplName )
+			metadataBuilder.applyPhysicalNamingStrategy(
+					strategySelector.resolveStrategy(
+							PhysicalNamingStrategy.class,
+							parsedArgs.physicalNamingStrategyImplName
+					)
 			);
 		}
 
 		return (MetadataImplementor) metadataBuilder.build();
 	}
 
 	/**
 	 * Intended for test usage only.  Builds a Metadata using the same algorithm  as
 	 * {@link #main}
 	 *
 	 * @param args The "command line args"
 	 *
 	 * @return The built Metadata
 	 *
 	 * @throws Exception Problems building the Metadata
 	 */
 	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
 		final CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs( args );
 		StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( commandLineArgs );
 		try {
 			return buildMetadata( commandLineArgs, serviceRegistry );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 	private static class CommandLineArgs {
 		boolean script = true;
 		boolean drop = false;
 		boolean create = false;
 		boolean halt = false;
 		boolean export = true;
 		boolean format = false;
 
 		String delimiter = null;
 
 		String outputFile = null;
 		String importFile = DEFAULT_IMPORT_FILE;
 
 		String propertiesFile = null;
 		String cfgXmlFile = null;
 		String implicitNamingStrategyImplName = null;
 		String physicalNamingStrategyImplName = null;
 
 		List<String> hbmXmlFiles = new ArrayList<String>();
 		List<String> jarFiles = new ArrayList<String>();
 
 		public static CommandLineArgs parseCommandLineArgs(String[] args) {
 			CommandLineArgs parsedArgs = new CommandLineArgs();
 
 			for ( String arg : args ) {
 				if ( arg.startsWith( "--" ) ) {
 					if ( arg.equals( "--quiet" ) ) {
 						parsedArgs.script = false;
 					}
 					else if ( arg.equals( "--drop" ) ) {
 						parsedArgs.drop = true;
 					}
 					else if ( arg.equals( "--create" ) ) {
 						parsedArgs.create = true;
 					}
 					else if ( arg.equals( "--haltonerror" ) ) {
 						parsedArgs.halt = true;
 					}
 					else if ( arg.equals( "--text" ) ) {
 						parsedArgs.export = false;
 					}
 					else if ( arg.startsWith( "--output=" ) ) {
 						parsedArgs.outputFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--import=" ) ) {
 						parsedArgs.importFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--properties=" ) ) {
 						parsedArgs.propertiesFile = arg.substring( 13 );
 					}
 					else if ( arg.equals( "--format" ) ) {
 						parsedArgs.format = true;
 					}
 					else if ( arg.startsWith( "--delimiter=" ) ) {
 						parsedArgs.delimiter = arg.substring( 12 );
 					}
 					else if ( arg.startsWith( "--config=" ) ) {
 						parsedArgs.cfgXmlFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--naming=" ) ) {
 						DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyArgument();
 					}
 					else if ( arg.startsWith( "--implicit-naming=" ) ) {
 						parsedArgs.implicitNamingStrategyImplName = arg.substring( 18 );
 					}
 					else if ( arg.startsWith( "--physical-naming=" ) ) {
 						parsedArgs.physicalNamingStrategyImplName = arg.substring( 18 );
 					}
 				}
 				else {
 					if ( arg.endsWith( ".jar" ) ) {
 						parsedArgs.jarFiles.add( arg );
 					}
 					else {
 						parsedArgs.hbmXmlFiles.add( arg );
 					}
 				}
 			}
 
 			return parsedArgs;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
index 5cd0f4ebf4..d3efb9140a 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
@@ -1,281 +1,281 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.types.FileSet;
 
 /**
  * An Ant task for <tt>SchemaExport</tt>.
  *
  * <pre>
  * &lt;taskdef name="schemaexport"
  *     classname="org.hibernate.tool.hbm2ddl.SchemaExportTask"
  *     classpathref="class.path"/&gt;
  *
  * &lt;schemaexport
  *     properties="${build.classes.dir}/hibernate.properties"
  *     quiet="no"
  *     text="no"
  *     drop="no"
  *     delimiter=";"
  *     output="${build.dir}/schema-export.sql"&gt;
  *     &lt;fileset dir="${build.classes.dir}"&gt;
  *         &lt;include name="*.hbm.xml"/&gt;
  *     &lt;/fileset&gt;
  * &lt;/schemaexport&gt;
  * </pre>
  *
  * @see SchemaExport
  * @author Rong C Ou
  */
 public class SchemaExportTask extends MatchingTask {
 	private List<FileSet> fileSets = new LinkedList<FileSet>();
 	private File propertiesFile;
 	private File configurationFile;
 	private File outputFile;
 	private boolean quiet;
 	private boolean text;
 	private boolean drop;
 	private boolean create;
 	private boolean haltOnError;
 	private String delimiter;
 	private String implicitNamingStrategy;
 	private String physicalNamingStrategy;
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void addFileset(FileSet set) {
 		fileSets.add(set);
 	}
 
 	/**
 	 * Set a properties file
 	 * @param propertiesFile the properties file name
 	 */
 	public void setProperties(File propertiesFile) {
 		if ( !propertiesFile.exists() ) {
 			throw new BuildException("Properties file: " + propertiesFile + " does not exist.");
 	}
 
 		log("Using properties file " + propertiesFile, Project.MSG_DEBUG);
 		this.propertiesFile = propertiesFile;
 	}
 
 	/**
 	 * Set a <literal>.cfg.xml</literal> file, which will be
 	 * loaded as a resource, from the classpath
 	 * @param configurationFile the path to the resource
 	 */
 	public void setConfig(File configurationFile) {
 		this.configurationFile = configurationFile;
 	}
 
 	/**
 	 * Enable "quiet" mode. The schema will not be
 	 * written to standard out.
 	 * @param quiet true to enable quiet mode
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public void setQuiet(boolean quiet) {
 		this.quiet = quiet;
 	}
 
 	/**
 	 * Enable "text-only" mode. The schema will not
 	 * be exported to the database.
 	 * @param text true to enable text-only mode
 	 */
 	public void setText(boolean text) {
 		this.text = text;
 	}
 
 	/**
 	 * Enable "drop" mode. Database objects will be
 	 * dropped but not recreated.
 	 * @param drop true to enable drop mode
 	 */
 	public void setDrop(boolean drop) {
 		this.drop = drop;
 	}
 
 	/**
 	 * Enable "create" mode. Database objects will be
 	 * created but not first dropped.
 	 * @param create true to enable create mode
 	 */
 	public void setCreate(boolean create) {
 		this.create = create;
 	}
 
 	/**
 	 * Set the end of statement delimiter for the generated script
 	 * @param delimiter the delimiter
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 
 	/**
 	 * Set the script output file
 	 * @param outputFile the file name
 	 */
 	public void setOutput(File outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	/**
 	 * @deprecated Use {@link #setImplicitNamingStrategy} or {@link #setPhysicalNamingStrategy}
 	 * instead
 	 */
 	@Deprecated
 	public void setNamingStrategy(String namingStrategy) {
 		DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyAntArgument();
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
 		this.implicitNamingStrategy = implicitNamingStrategy;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
 		this.physicalNamingStrategy = physicalNamingStrategy;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setHaltonerror(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	/**
 	 * Execute the task
 	 */
 	@Override
     public void execute() throws BuildException {
 		try {
 			buildSchemaExport().execute( !quiet, !text, drop, create );
 		}
 		catch (HibernateException e) {
 			throw new BuildException("Schema text failed: " + e.getMessage(), e);
 		}
 		catch (FileNotFoundException e) {
 			throw new BuildException("File not found: " + e.getMessage(), e);
 		}
 		catch (IOException e) {
 			throw new BuildException("IOException : " + e.getMessage(), e);
 		}
 		catch (Exception e) {
 			throw new BuildException(e);
 		}
 	}
 
 	private String[] getFiles() {
 		List<String> files = new LinkedList<String>();
 		for ( FileSet fileSet : fileSets ) {
 			final DirectoryScanner ds = fileSet.getDirectoryScanner( getProject() );
 			final String[] dsFiles = ds.getIncludedFiles();
 			for ( String dsFileName : dsFiles ) {
 				File f = new File( dsFileName );
 				if ( !f.isFile() ) {
 					f = new File( ds.getBasedir(), dsFileName );
 				}
 
 				files.add( f.getAbsolutePath() );
 			}
 		}
 
 		return ArrayHelper.toStringArray(files);
 	}
 
 	private SchemaExport buildSchemaExport() throws Exception {
 		final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
 
 		final MetadataSources metadataSources = new MetadataSources( bsr );
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 
 		if ( configurationFile != null ) {
 			ssrBuilder.configure( configurationFile );
 		}
 		if ( propertiesFile != null ) {
 			ssrBuilder.loadProperties( propertiesFile );
 		}
 		ssrBuilder.applySettings( getProject().getProperties() );
 
 		for ( String fileName : getFiles() ) {
 			if ( fileName.endsWith(".jar") ) {
 				metadataSources.addJar( new File( fileName ) );
 			}
 			else {
 				metadataSources.addFile( fileName );
 			}
 		}
 
 
 		final StandardServiceRegistryImpl ssr = (StandardServiceRegistryImpl) ssrBuilder.build();
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder( ssr );
 
 		ClassLoaderService classLoaderService = bsr.getService( ClassLoaderService.class );
 		if ( implicitNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyImplicitNamingStrategy(
 					(ImplicitNamingStrategy) classLoaderService.classForName( implicitNamingStrategy ).newInstance()
 			);
 		}
 		if ( physicalNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyPhysicalNamingStrategy(
 					(PhysicalNamingStrategy) classLoaderService.classForName( physicalNamingStrategy ).newInstance()
 			);
 		}
 
 		return new SchemaExport( (MetadataImplementor) metadataBuilder.build() )
 				.setHaltOnError( haltOnError )
 				.setOutputFile( outputFile.getPath() )
 				.setDelimiter( delimiter );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index 351b6d9eb8..e151207227 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,338 +1,343 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.schema.extract.internal.legacy.DatabaseInformationImpl;
 import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
 import org.hibernate.tool.schema.internal.TargetDatabaseImpl;
 import org.hibernate.tool.schema.internal.TargetFileImpl;
 import org.hibernate.tool.schema.internal.TargetStdoutImpl;
 import org.hibernate.tool.schema.spi.SchemaManagementTool;
 import org.hibernate.tool.schema.spi.SchemaMigrator;
 
 /**
  * A commandline tool to update a database schema. May also be called from inside an application.
  *
  * @author Christoph Sturm
  * @author Steve Ebersole
  */
 public class SchemaUpdate {
     private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SchemaUpdate.class );
 
 	private final MetadataImplementor metadata;
 	private final ServiceRegistry serviceRegistry;
 
 	private final JdbcConnectionAccess jdbcConnectionAccess;
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 	private String outputFile;
 
 	/**
 	 * Creates a SchemaUpdate object.  This form is intended for use from tooling
 	 *
 	 * @param metadata The metadata defining the schema as it should be after update
 	 *
 	 * @throws HibernateException
 	 */
 	public SchemaUpdate(MetadataImplementor metadata) {
 		this( metadata.getMetadataBuildingOptions().getServiceRegistry(), metadata );
 	}
 
 	/**
 	 * Creates a SchemaUpdate object.  This form is intended for use from
 	 * {@code hibernate.hbm2ddl.auto} handling, generally from within the SessionFactory
 	 * ctor.
 	 * <p/>
 	 * Note that the passed ServiceRegistry is expected to be of type
 	 * {@link org.hibernate.service.spi.SessionFactoryServiceRegistry}, although
 	 * any ServiceRegistry type will work as long as it has access to the
 	 * {@link org.hibernate.engine.jdbc.spi.JdbcServices} service.
 	 *
 	 * @param serviceRegistry The ServiceRegistry to use.
 	 * @param metadata The metadata defining the schema as it should be after update
 	 *
 	 * @throws HibernateException
 	 */
 	public SchemaUpdate(ServiceRegistry serviceRegistry, MetadataImplementor metadata) throws HibernateException {
 		this.metadata = metadata;
 		this.serviceRegistry = serviceRegistry;
 		this.jdbcConnectionAccess = serviceRegistry.getService( JdbcServices.class ).getBootstrapJdbcConnectionAccess();
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 		execute( Target.interpret( script, doUpdate ) );
 	}
 	
 	public void execute(Target target) {
         LOG.runningHbm2ddlSchemaUpdate();
 
 		exceptions.clear();
 
 		List<org.hibernate.tool.schema.spi.Target> toolTargets = buildToolTargets( target );
 
 		final ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
 		final SchemaMigrator schemaMigrator = serviceRegistry.getService( SchemaManagementTool.class )
 				.getSchemaMigrator( cfgService.getSettings() );
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		final DatabaseInformation databaseInformation;
 		try {
 			databaseInformation = new DatabaseInformationImpl(
 					serviceRegistry,
 					serviceRegistry.getService( JdbcEnvironment.class ),
 					jdbcConnectionAccess,
 					metadata.getDatabase().getDefaultSchema().getPhysicalName().getCatalog(),
 					metadata.getDatabase().getDefaultSchema().getPhysicalName().getSchema()
 			);
 		}
 		catch (SQLException e) {
 			throw jdbcServices.getSqlExceptionHelper().convert(
 					e,
 					"Error creating DatabaseInformation for schema migration"
 			);
 		}
 
 		schemaMigrator.doMigration( metadata, databaseInformation, true, toolTargets );
 	}
 
 	private List<org.hibernate.tool.schema.spi.Target> buildToolTargets(Target target) {
 		List<org.hibernate.tool.schema.spi.Target> toolTargets = new ArrayList<org.hibernate.tool.schema.spi.Target>();
 
 		if ( target.doScript() ) {
 			toolTargets.add( new TargetStdoutImpl() );
 		}
 
 		if ( target.doExport() ) {
 			toolTargets.add( new TargetDatabaseImpl( jdbcConnectionAccess ) );
 		}
 
 		if ( outputFile != null ) {
 			LOG.writingGeneratedSchemaToFile( outputFile );
 			toolTargets.add( new TargetFileImpl( outputFile ) );
 		}
 
 		return toolTargets;
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 	public void setHaltOnError(boolean haltOnError) {
 	}
 
 	public void setFormat(boolean format) {
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 	}
 
 	public static void main(String[] args) {
 		try {
 			final CommandLineArgs parsedArgs = CommandLineArgs.parseCommandLineArgs( args );
 			final StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( parsedArgs );
 
 			try {
 				final MetadataImplementor metadata = buildMetadata( parsedArgs, serviceRegistry );
 
 				final SchemaUpdate schemaUpdate = new SchemaUpdate( metadata );
 				schemaUpdate.setOutputFile( parsedArgs.outFile );
 				schemaUpdate.execute( parsedArgs.script, parsedArgs.doUpdate );
 			}
 			finally {
 				StandardServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 		catch ( Exception e ) {
 			LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	private static StandardServiceRegistry buildStandardServiceRegistry(CommandLineArgs parsedArgs) throws Exception {
 		final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 
 		if ( parsedArgs.cfgXmlFile != null ) {
 			ssrBuilder.configure( parsedArgs.cfgXmlFile );
 		}
 
 		if ( parsedArgs.propertiesFile != null ) {
 			Properties props = new Properties();
 			props.load( new FileInputStream( parsedArgs.propertiesFile ) );
 			ssrBuilder.applySettings( props );
 		}
 
 		return ssrBuilder.build();
 	}
 
 	private static MetadataImplementor buildMetadata(CommandLineArgs parsedArgs, ServiceRegistry serviceRegistry)
 			throws Exception {
 		final MetadataSources metadataSources = new MetadataSources( serviceRegistry );
 
 		for ( String filename : parsedArgs.hbmXmlFiles ) {
 			metadataSources.addFile( filename );
 		}
 
 		for ( String filename : parsedArgs.jarFiles ) {
 			metadataSources.addJar( new File( filename ) );
 		}
 
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		if ( parsedArgs.implicitNamingStrategyImplName != null ) {
-			metadataBuilder.with(
-					strategySelector.resolveStrategy( ImplicitNamingStrategy.class, parsedArgs.implicitNamingStrategyImplName )
+			metadataBuilder.applyImplicitNamingStrategy(
+					strategySelector.resolveStrategy(
+							ImplicitNamingStrategy.class,
+							parsedArgs.implicitNamingStrategyImplName
+					)
 			);
 		}
 		if ( parsedArgs.physicalNamingStrategyImplName != null ) {
-			metadataBuilder.with(
-					strategySelector.resolveStrategy( PhysicalNamingStrategy.class, parsedArgs.physicalNamingStrategyImplName )
+			metadataBuilder.applyPhysicalNamingStrategy(
+					strategySelector.resolveStrategy(
+							PhysicalNamingStrategy.class,
+							parsedArgs.physicalNamingStrategyImplName
+					)
 			);
 		}
 
 		return (MetadataImplementor) metadataBuilder.build();
 	}
 
 	private static class CommandLineArgs {
 		boolean script = true;
 		// If true then execute db updates, otherwise just generate and display updates
 		boolean doUpdate = true;
 
 		String propertiesFile = null;
 		String cfgXmlFile = null;
 		String outFile = null;
 
 		String implicitNamingStrategyImplName = null;
 		String physicalNamingStrategyImplName = null;
 
 		List<String> hbmXmlFiles = new ArrayList<String>();
 		List<String> jarFiles = new ArrayList<String>();
 
 		public static CommandLineArgs parseCommandLineArgs(String[] args) {
 			final CommandLineArgs parsedArgs = new CommandLineArgs();
 
 			for ( String arg : args ) {
 				if ( arg.startsWith( "--" ) ) {
 					if ( arg.equals( "--quiet" ) ) {
 						parsedArgs.script = false;
 					}
 					else if ( arg.startsWith( "--properties=" ) ) {
 						parsedArgs.propertiesFile = arg.substring( 13 );
 					}
 					else if ( arg.startsWith( "--config=" ) ) {
 						parsedArgs.cfgXmlFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--text" ) ) {
 						parsedArgs.doUpdate = false;
 					}
 					else if ( arg.startsWith( "--output=" ) ) {
 						parsedArgs.outFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--naming=" ) ) {
 						DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyArgument();
 					}
 					else if ( arg.startsWith( "--implicit-naming=" ) ) {
 						parsedArgs.implicitNamingStrategyImplName = arg.substring( 18 );
 					}
 					else if ( arg.startsWith( "--physical-naming=" ) ) {
 						parsedArgs.physicalNamingStrategyImplName = arg.substring( 18 );
 					}
 				}
 				else {
 					if ( arg.endsWith( ".jar" ) ) {
 						parsedArgs.jarFiles.add( arg );
 					}
 					else {
 						parsedArgs.hbmXmlFiles.add( arg );
 					}
 				}
 			}
 
 			return parsedArgs;
 		}
 	}
 
 	/**
 	 * Intended for test usage only.  Builds a Metadata using the same algorithm  as
 	 * {@link #main}
 	 *
 	 * @param args The "command line args"
 	 *
 	 * @return The built Metadata
 	 *
 	 * @throws Exception Problems building the Metadata
 	 */
 	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
 		final CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs( args );
 		StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( commandLineArgs );
 		try {
 			return buildMetadata( commandLineArgs, serviceRegistry );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
index e1618002a8..f1fbf50cd4 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
@@ -1,292 +1,292 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.types.FileSet;
 
 /**
  * An Ant task for <tt>SchemaUpdate</tt>.
  *
  * <pre>
  * &lt;taskdef name="schemaupdate"
  *     classname="org.hibernate.tool.hbm2ddl.SchemaUpdateTask"
  *     classpathref="class.path"/&gt;
  *
  * &lt;schemaupdate
  *     properties="${build.classes.dir}/hibernate.properties"
  *     quiet="no"
  *     &lt;fileset dir="${build.classes.dir}"&gt;
  *         &lt;include name="*.hbm.xml"/&gt;
  *     &lt;/fileset&gt;
  * &lt;/schemaupdate&gt;
  * </pre>
  *
  * @see SchemaUpdate
  * @author Rong C Ou, Gavin King
  */
 public class SchemaUpdateTask extends MatchingTask {
 	private List<FileSet> fileSets = new LinkedList<FileSet>();
 	private File propertiesFile;
 	private File configurationFile;
 	private File outputFile;
 	private boolean quiet;
 	private boolean text = true;
 	private boolean haltOnError;
 	private String delimiter;
 
 	private String implicitNamingStrategy = null;
 	private String physicalNamingStrategy = null;
 	
 	@SuppressWarnings("UnusedDeclaration")
 	public void addFileset(FileSet fileSet) {
 		fileSets.add( fileSet );
 	}
 
 	/**
 	 * Set a properties file
 	 *
 	 * @param propertiesFile the properties file name
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public void setProperties(File propertiesFile) {
 		if ( !propertiesFile.exists() ) {
 			throw new BuildException("Properties file: " + propertiesFile + " does not exist.");
 		}
 
 		log("Using properties file " + propertiesFile, Project.MSG_DEBUG);
 		this.propertiesFile = propertiesFile;
 	}
 
 	/**
 	 * Set a {@code cfg.xml} file
 	 *
 	 * @param configurationFile the file name
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public void setConfig(File configurationFile) {
 		this.configurationFile = configurationFile;
 	}
 
 	/**
      * Enable "text-only" mode. The schema will not be updated in the database.
 	 *
 	 * @param text true to enable text-only mode
      */
 	@SuppressWarnings("UnusedDeclaration")
     public void setText(boolean text) {
         this.text = text;
     }
 
 	/**
 	 * Enable "quiet" mode. The schema will not be written to standard out.
 	 *
 	 * @param quiet true to enable quiet mode
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public void setQuiet(boolean quiet) {
 		this.quiet = quiet;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setNamingStrategy(String namingStrategy) {
 		DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyAntArgument();
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
 		this.implicitNamingStrategy = implicitNamingStrategy;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
 		this.physicalNamingStrategy = physicalNamingStrategy;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public File getOutputFile() {
 		return outputFile;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setOutputFile(File outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean isHaltOnError() {
 		return haltOnError;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public String getDelimiter() {
 		return delimiter;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 
 	/**
 	 * Execute the task
 	 */
 	@Override
     public void execute() throws BuildException {
 		log("Running Hibernate Core SchemaUpdate.");
 		log("This is an Ant task supporting only mapping files, if you want to use annotations see http://tools.hibernate.org.");
 
 		try {
 			final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder();
 			configure( ssrBuilder );
 
 			final MetadataSources metadataSources = new MetadataSources( ssrBuilder.build() );
 			configure( metadataSources );
 
 			final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 			configure( metadataBuilder );
 
 			final MetadataImplementor metadata = (MetadataImplementor) metadataBuilder.build();
 
 			final SchemaUpdate su = new SchemaUpdate( metadata );
 			su.setOutputFile( outputFile.getPath() );
 			su.setDelimiter( delimiter );
 			su.setHaltOnError( haltOnError );
 			su.execute( !quiet, !text );
 		}
 		catch (HibernateException e) {
 			throw new BuildException("Schema text failed: " + e.getMessage(), e);
 		}
 		catch (FileNotFoundException e) {
 			throw new BuildException("File not found: " + e.getMessage(), e);
 		}
 		catch (IOException e) {
 			throw new BuildException("IOException : " + e.getMessage(), e);
 		}
 		catch (BuildException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new BuildException(e);
 		}
 	}
 
 	private void configure(StandardServiceRegistryBuilder registryBuilder) throws IOException {
 		if ( configurationFile != null ) {
 			registryBuilder.configure( configurationFile );
 		}
 
 		Properties properties = new Properties();
 		if ( propertiesFile == null ) {
 			properties.putAll( getProject().getProperties() );
 		}
 		else {
 			properties.load( new FileInputStream( propertiesFile ) );
 		}
 
 		registryBuilder.applySettings( properties );
 	}
 
 	private void configure(MetadataSources metadataSources) {
 		for ( String filename : collectFiles() ) {
 			if ( filename.endsWith(".jar") ) {
 				metadataSources.addJar( new File( filename ) );
 			}
 			else {
 				metadataSources.addFile( filename );
 			}
 		}
 	}
 
 	private String[] collectFiles() {
 		List<String> files = new LinkedList<String>();
 		for ( FileSet fileSet : fileSets ) {
 			final DirectoryScanner ds = fileSet.getDirectoryScanner( getProject() );
 			final String[] dsFiles = ds.getIncludedFiles();
 			for ( String dsFileName : dsFiles ) {
 				File f = new File( dsFileName );
 				if ( !f.isFile() ) {
 					f = new File( ds.getBasedir(), dsFileName );
 				}
 
 				files.add( f.getAbsolutePath() );
 			}
 		}
 		return ArrayHelper.toStringArray( files );
 	}
 
 	@SuppressWarnings("deprecation")
 	private void configure(MetadataBuilder metadataBuilder) {
 		if ( implicitNamingStrategy != null ) {
 			try {
-				metadataBuilder.with(
+				metadataBuilder.applyImplicitNamingStrategy(
 						(ImplicitNamingStrategy) ReflectHelper.classForName( implicitNamingStrategy ).newInstance()
 				);
 			}
 			catch (Exception e) {
 				throw new BuildException( "Unable to instantiate specified ImplicitNamingStrategy [" + implicitNamingStrategy + "]", e );
 			}
 		}
 
 		if ( physicalNamingStrategy != null ) {
 			try {
-				metadataBuilder.with(
+				metadataBuilder.applyPhysicalNamingStrategy(
 						(PhysicalNamingStrategy) ReflectHelper.classForName( physicalNamingStrategy ).newInstance()
 				);
 			}
 			catch (Exception e) {
 				throw new BuildException( "Unable to instantiate specified PhysicalNamingStrategy [" + physicalNamingStrategy + "]", e );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
index e4f239b9dd..b8a8ccb9a3 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
@@ -1,242 +1,242 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.schema.extract.internal.legacy.DatabaseInformationImpl;
 import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
 import org.hibernate.tool.schema.spi.SchemaManagementTool;
 
 import org.jboss.logging.Logger;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaValidator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SchemaValidator.class.getName()
 	);
 
 	private final ServiceRegistry serviceRegistry;
 	private final MetadataImplementor metadata;
 	private final JdbcConnectionAccess jdbcConnectionAccess;
 
 	public SchemaValidator(MetadataImplementor metadata) {
 		this( metadata.getMetadataBuildingOptions().getServiceRegistry(), metadata );
 	}
 
 	public SchemaValidator(ServiceRegistry serviceRegistry, MetadataImplementor metadata) {
 		this.serviceRegistry = serviceRegistry;
 		this.metadata = metadata;
 		this.jdbcConnectionAccess = serviceRegistry.getService( JdbcServices.class ).getBootstrapJdbcConnectionAccess();
 	}
 
 	/**
 	 * Perform the validations.
 	 */
 	public void validate() {
 		LOG.runningSchemaValidator();
 
 		final ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		final DatabaseInformation databaseInformation;
 		try {
 			databaseInformation = new DatabaseInformationImpl(
 					serviceRegistry,
 					serviceRegistry.getService( JdbcEnvironment.class ),
 					jdbcConnectionAccess,
 					metadata.getDatabase().getDefaultSchema().getPhysicalName().getCatalog(),
 					metadata.getDatabase().getDefaultSchema().getPhysicalName().getSchema()
 			);
 		}
 		catch (SQLException e) {
 			throw jdbcServices.getSqlExceptionHelper().convert(
 					e,
 					"Error creating DatabaseInformation for schema validation"
 			);
 		}
 
 		serviceRegistry.getService( SchemaManagementTool.class ).getSchemaValidator( cfgService.getSettings() )
 				.doValidation( metadata, databaseInformation );
 	}
 
 	public static void main(String[] args) {
 		try {
 			final CommandLineArgs parsedArgs = CommandLineArgs.parseCommandLineArgs( args );
 			final StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( parsedArgs );
 
 			try {
 				final MetadataImplementor metadata = buildMetadata( parsedArgs, serviceRegistry );
 				new SchemaValidator( serviceRegistry, metadata ).validate();
 			}
 			finally {
 				StandardServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 		catch (Exception e) {
 			LOG.unableToRunSchemaUpdate( e );
 			e.printStackTrace();
 		}
 	}
 
 	private static class CommandLineArgs {
 		String implicitNamingStrategy = null;
 		String physicalNamingStrategy = null;
 
 		String propertiesFile = null;
 		String cfgXmlFile = null;
 		List<String> hbmXmlFiles = new ArrayList<String>();
 		List<String> jarFiles = new ArrayList<String>();
 
 		public static CommandLineArgs parseCommandLineArgs(String[] args) {
 			final CommandLineArgs parsedArgs = new CommandLineArgs();
 
 			for ( String arg : args ) {
 				if ( arg.startsWith( "--" ) ) {
 					if ( arg.startsWith( "--properties=" ) ) {
 						parsedArgs.propertiesFile = arg.substring( 13 );
 					}
 					else if ( arg.startsWith( "--config=" ) ) {
 						parsedArgs.cfgXmlFile = arg.substring( 9 );
 					}
 					else if ( arg.startsWith( "--naming=" ) ) {
 						DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyArgument();
 					}
 					else if ( arg.startsWith( "--implicit-naming=" ) ) {
 						parsedArgs.implicitNamingStrategy = arg.substring( 18 );
 					}
 					else if ( arg.startsWith( "--physical-naming=" ) ) {
 						parsedArgs.physicalNamingStrategy = arg.substring( 18 );
 					}
 				}
 				else {
 					if ( arg.endsWith( ".jar" ) ) {
 						parsedArgs.jarFiles.add( arg );
 					}
 					else {
 						parsedArgs.hbmXmlFiles.add( arg );
 					}
 				}
 			}
 
 			return parsedArgs;
 		}
 	}
 
 	private static StandardServiceRegistry buildStandardServiceRegistry(CommandLineArgs parsedArgs) throws Exception {
 		final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 
 		if ( parsedArgs.cfgXmlFile != null ) {
 			ssrBuilder.configure( parsedArgs.cfgXmlFile );
 		}
 
 		if ( parsedArgs.propertiesFile != null ) {
 			Properties properties = new Properties();
 			properties.load( new FileInputStream( parsedArgs.propertiesFile ) );
 			ssrBuilder.applySettings( properties );
 		}
 
 		return ssrBuilder.build();
 	}
 
 	private static MetadataImplementor buildMetadata(
 			CommandLineArgs parsedArgs,
 			StandardServiceRegistry serviceRegistry) throws Exception {
 
 		final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
 
 		for ( String filename : parsedArgs.hbmXmlFiles ) {
 			metadataSources.addFile( filename );
 		}
 
 		for ( String filename : parsedArgs.jarFiles ) {
 			metadataSources.addJar( new File( filename ) );
 		}
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		if ( parsedArgs.implicitNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyImplicitNamingStrategy(
 					strategySelector.resolveStrategy( ImplicitNamingStrategy.class, parsedArgs.implicitNamingStrategy )
 			);
 		}
 		if ( parsedArgs.physicalNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyPhysicalNamingStrategy(
 					strategySelector.resolveStrategy( PhysicalNamingStrategy.class, parsedArgs.physicalNamingStrategy )
 			);
 		}
 
 		return (MetadataImplementor) metadataBuilder.build();
 
 	}
 
 	/**
 	 * Intended for test usage only.  Builds a Metadata using the same algorithm  as
 	 * {@link #main}
 	 *
 	 * @param args The "command line args"
 	 *
 	 * @return The built Metadata
 	 *
 	 * @throws Exception Problems building the Metadata
 	 */
 	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
 		final CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs( args );
 		StandardServiceRegistry serviceRegistry = buildStandardServiceRegistry( commandLineArgs );
 		try {
 			return buildMetadata( commandLineArgs, serviceRegistry );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
index 0ac159c365..aadda53256 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
@@ -1,232 +1,231 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.internal.log.DeprecationLogger;
-import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.MatchingTask;
 import org.apache.tools.ant.types.FileSet;
 
 /**
  * An Ant task for <tt>SchemaUpdate</tt>.
  *
  * <pre>
  * &lt;taskdef name="schemavalidator"
  *     classname="org.hibernate.tool.hbm2ddl.SchemaValidatorTask"
  *     classpathref="class.path"/&gt;
  *
  * &lt;schemaupdate
  *     properties="${build.classes.dir}/hibernate.properties"
  *     &lt;fileset dir="${build.classes.dir}"&gt;
  *         &lt;include name="*.hbm.xml"/&gt;
  *     &lt;/fileset&gt;
  * &lt;/schemaupdate&gt;
  * </pre>
  *
  * @see SchemaValidator
  * @author Gavin King
  */
 public class SchemaValidatorTask extends MatchingTask {
 	private List<FileSet> fileSets = new LinkedList<FileSet>();
 
 	private File propertiesFile;
 	private File configurationFile;
 
 	private String implicitNamingStrategy = null;
 	private String physicalNamingStrategy = null;
 
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void addFileset(FileSet fileSet) {
 		fileSets.add( fileSet );
 	}
 
 	/**
 	 * Set a properties file
 	 * @param propertiesFile the properties file name
 	 */
 	public void setProperties(File propertiesFile) {
 		if ( !propertiesFile.exists() ) {
 			throw new BuildException("Properties file [" + propertiesFile + "] does not exist.");
 		}
 
 		log( "Using properties file " + propertiesFile, Project.MSG_DEBUG );
 		this.propertiesFile = propertiesFile;
 	}
 
 	/**
 	 * Set a <literal>.cfg.xml</literal> file
 	 * @param configurationFile the file name
 	 */
 	public void setConfig(File configurationFile) {
 		if ( !configurationFile.exists() ) {
 			throw new BuildException("Configuration file [" + configurationFile + "] does not exist.");
 		}
 
 		log( "Using configuration file " + propertiesFile, Project.MSG_DEBUG );
 		this.configurationFile = configurationFile;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setNamingStrategy(String namingStrategy) {
 		DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyAntArgument();
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
 		this.implicitNamingStrategy = implicitNamingStrategy;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
 		this.physicalNamingStrategy = physicalNamingStrategy;
 	}
 
 	/**
 	 * Execute the task
 	 */
 	@Override
     public void execute() throws BuildException {
 		try {
 			final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder();
 			configure( ssrBuilder );
 
 			final StandardServiceRegistry ssr = ssrBuilder.build();
 
 			try {
 				final MetadataSources metadataSources = new MetadataSources( ssrBuilder.build() );
 				configure( metadataSources );
 
 				final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 				configure( metadataBuilder, ssr );
 
 				final MetadataImplementor metadata = (MetadataImplementor) metadataBuilder.build();
 
 				new SchemaValidator( ssr, metadata ).validate();
 			}
 			finally {
 				StandardServiceRegistryBuilder.destroy( ssr );
 			}
 		}
 		catch (HibernateException e) {
 			throw new BuildException("Schema text failed: " + e.getMessage(), e);
 		}
 		catch (FileNotFoundException e) {
 			throw new BuildException("File not found: " + e.getMessage(), e);
 		}
 		catch (IOException e) {
 			throw new BuildException("IOException : " + e.getMessage(), e);
 		}
 		catch (BuildException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new BuildException(e);
 		}
 	}
 	private void configure(StandardServiceRegistryBuilder registryBuilder) throws IOException {
 		if ( configurationFile != null ) {
 			registryBuilder.configure( configurationFile );
 		}
 
 		Properties properties = new Properties();
 		if ( propertiesFile == null ) {
 			properties.putAll( getProject().getProperties() );
 		}
 		else {
 			properties.load( new FileInputStream( propertiesFile ) );
 		}
 
 		registryBuilder.applySettings( properties );
 	}
 
 	private void configure(MetadataSources metadataSources) {
 		for ( String filename : collectFiles() ) {
 			if ( filename.endsWith(".jar") ) {
 				metadataSources.addJar( new File( filename ) );
 			}
 			else {
 				metadataSources.addFile( filename );
 			}
 		}
 	}
 
 	private String[] collectFiles() {
 		List<String> files = new ArrayList<String>();
 
 		for ( Object fileSet : fileSets ) {
 			final FileSet fs = (FileSet) fileSet;
 			final DirectoryScanner ds = fs.getDirectoryScanner( getProject() );
 
 			for ( String dsFile : ds.getIncludedFiles() ) {
 				File f = new File( dsFile );
 				if ( !f.isFile() ) {
 					f = new File( ds.getBasedir(), dsFile );
 				}
 				files.add( f.getAbsolutePath() );
 			}
 		}
 
 		return ArrayHelper.toStringArray( files );
 	}
 
 	@SuppressWarnings("deprecation")
 	private void configure(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		if ( implicitNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyImplicitNamingStrategy(
 					strategySelector.resolveStrategy( ImplicitNamingStrategy.class, implicitNamingStrategy )
 			);
 		}
 		if ( physicalNamingStrategy != null ) {
-			metadataBuilder.with(
+			metadataBuilder.applyPhysicalNamingStrategy(
 					strategySelector.resolveStrategy( PhysicalNamingStrategy.class, physicalNamingStrategy )
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
index 646199896d..c4e8a8444e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
@@ -1,458 +1,458 @@
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
 package org.hibernate.test.annotations;
 
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.List;
 import java.util.TimeZone;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.StaleStateException;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.dialect.Oracle10gDialect;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EntityTest extends BaseNonConfigCoreFunctionalTestCase {
 	private DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with ( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 
 	@Test
 	public void testLoad() throws Exception {
 		//put an object in DB
 		assertEquals( "Flight", metadata().getEntityBinding( Flight.class.getName() ).getTable().getName() );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight firstOne = new Flight();
 		firstOne.setId( Long.valueOf( 1 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setDuration( new Long( 1000000 ) );
 		firstOne.setDurationInSec( 2000 );
 		s.save( firstOne );
 		s.flush();
 		tx.commit();
 		s.close();
 
 		//read it
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, Long.valueOf( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( Long.valueOf( 1 ), firstOne.getId() );
 		assertEquals( "AF3202", firstOne.getName() );
 		assertEquals( Long.valueOf( 1000000 ), firstOne.getDuration() );
 		assertFalse( "Transient is not working", 2000l == firstOne.getDurationInSec() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testColumn() throws Exception {
 		//put an object in DB
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight firstOne = new Flight();
 		firstOne.setId( Long.valueOf( 1 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setDuration( Long.valueOf( 1000000 ) );
 		firstOne.setDurationInSec( 2000 );
 		s.save( firstOne );
 		s.flush();
 		tx.commit();
 		s.close();
 		
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = new Flight();
 		firstOne.setId( Long.valueOf( 1 ) );
 		firstOne.setName( null );
 
 		try {
 			s.save( firstOne );
 			tx.commit();
 			fail( "Name column should be not null" );
 		}
 		catch (HibernateException e) {
 			//fine
 		}
 		finally {
 			s.close();
 		}
 
 		//insert an object and check that name is not updatable
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = new Flight();
 		firstOne.setId( Long.valueOf( 1 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setTriggeredData( "should not be insertable" );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, Long.valueOf( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( Long.valueOf( 1 ), firstOne.getId() );
 		assertEquals( "AF3202", firstOne.getName() );
 		assertFalse( "should not be insertable".equals( firstOne.getTriggeredData() ) );
 		firstOne.setName( "BA1234" );
 		firstOne.setTriggeredData( "should not be updatable" );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, Long.valueOf( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( Long.valueOf( 1 ), firstOne.getId() );
 		assertEquals( "AF3202", firstOne.getName() );
 		assertFalse( "should not be updatable".equals( firstOne.getTriggeredData() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testColumnUnique() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Sky sky = new Sky();
 		sky.id = Long.valueOf( 2 );
 		sky.color = "blue";
 		sky.day = "monday";
 		sky.month = "January";
 
 		Sky sameSky = new Sky();
 		sameSky.id = Long.valueOf( 3 );
 		sameSky.color = "blue";
 		sky.day = "tuesday";
 		sky.month = "January";
 
 		try {
 			s.save( sky );
 			s.flush();
 			s.save( sameSky );
 			tx.commit();
 			fail( "unique constraints not respected" );
 		}
 		catch (HibernateException e) {
 			//success
 		}
 		finally {
 			if ( tx != null ) tx.rollback();
 			s.close();
 		}
 	}
 
 	@Test
 	public void testUniqueConstraint() throws Exception {
 		int id = 5;
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Sky sky = new Sky();
 		sky.id = Long.valueOf( id++ );
 		sky.color = "green";
 		sky.day = "monday";
 		sky.month = "March";
 
 		Sky otherSky = new Sky();
 		otherSky.id = Long.valueOf( id++ );
 		otherSky.color = "red";
 		otherSky.day = "friday";
 		otherSky.month = "March";
 
 		Sky sameSky = new Sky();
 		sameSky.id = Long.valueOf( id++ );
 		sameSky.color = "green";
 		sameSky.day = "monday";
 		sameSky.month = "March";
 
 		s.save( sky );
 		s.flush();
 
 		s.save( otherSky );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		try {
 			s.save( sameSky );
 			tx.commit();
 			fail( "unique constraints not respected" );
 		}
 		catch (HibernateException e) {
 			//success
 			if ( tx != null ) {
 				tx.rollback();
 			}
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Test
 	public void testVersion() throws Exception {
 //		put an object in DB
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight firstOne = new Flight();
 		firstOne.setId( Long.valueOf( 2 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setDuration( Long.valueOf( 500 ) );
 		s.save( firstOne );
 		s.flush();
 		tx.commit();
 		s.close();
 
 		//read it
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, Long.valueOf( 2 ) );
 		tx.commit();
 		s.close();
 
 		//read it again
 		s = openSession();
 		tx = s.beginTransaction();
 		Flight concurrentOne = (Flight) s.get( Flight.class, Long.valueOf( 2 ) );
 		concurrentOne.setDuration( Long.valueOf( 1000 ) );
 		s.update( concurrentOne );
 		tx.commit();
 		s.close();
 		assertFalse( firstOne == concurrentOne );
 		assertFalse( firstOne.getVersion().equals( concurrentOne.getVersion() ) );
 
 		//reattach the first one
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne.setName( "Second access" );
 		s.update( firstOne );
 		try {
 			tx.commit();
 			fail( "Optimistic locking should work" );
 		}
 		catch (StaleStateException e) {
 			//fine
 		}
 		finally {
 			if ( tx != null ) tx.rollback();
 			s.close();
 		}
 	}
 
 	@Test
 	public void testFieldAccess() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Sky sky = new Sky();
 		sky.id = Long.valueOf( 1 );
 		sky.color = "black";
 		sky.area = "Paris";
 		sky.day = "23";
 		sky.month = "1";
 		s.save( sky );
 		tx.commit();
 		s.close();
 		sky.area = "London";
 
 		s = openSession();
 		tx = s.beginTransaction();
 		sky = (Sky) s.get( Sky.class, sky.id );
 		assertNotNull( sky );
 		assertEquals( "black", sky.color );
 		assertFalse( "Paris".equals( sky.area ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEntityName() throws Exception {
 		assertEquals( "Corporation", metadata().getEntityBinding( Company.class.getName() ).getTable().getName() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Company comp = new Company();
 		s.persist( comp );
 		comp.setName( "JBoss Inc" );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		List result = s.createQuery( "from Corporation" ).list();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 		tx.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testNonGetter() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight airFrance = new Flight();
 		airFrance.setId( Long.valueOf( 747 ) );
 		airFrance.setName( "Paris-Amsterdam" );
 		airFrance.setDuration( Long.valueOf( 10 ) );
 		airFrance.setFactor( 25 );
 		s.persist( airFrance );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		airFrance = (Flight) s.get( Flight.class, airFrance.getId() );
 		assertNotNull( airFrance );
 		assertEquals( Long.valueOf( 10 ), airFrance.getDuration() );
 		assertFalse( 25 == airFrance.getFactor( false ) );
 		s.delete( airFrance );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(value = Oracle10gDialect.class, comment = "oracle12c returns time in getDate.  For now, skip.")
 	public void testTemporalType() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight airFrance = new Flight();
 		airFrance.setId( Long.valueOf( 747 ) );
 		airFrance.setName( "Paris-Amsterdam" );
 		airFrance.setDuration( Long.valueOf( 10 ) );
 		airFrance.setDepartureDate( new Date( 05, 06, 21, 10, 0, 0 ) );
 		airFrance.setAlternativeDepartureDate( new GregorianCalendar( 2006, 02, 03, 10, 00 ) );
 		airFrance.getAlternativeDepartureDate().setTimeZone( TimeZone.getTimeZone( "GMT" ) );
 		airFrance.setBuyDate( new java.sql.Timestamp(122367443) );
 		airFrance.setFactor( 25 );
 		s.persist( airFrance );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "from Flight f where f.departureDate = :departureDate" );
 		q.setParameter( "departureDate", airFrance.getDepartureDate(), StandardBasicTypes.DATE );
 		Flight copyAirFrance = (Flight) q.uniqueResult();
 		assertNotNull( copyAirFrance );
 		assertEquals(
 				df.format(new Date( 05, 06, 21 )).toString(),
 				df.format(copyAirFrance.getDepartureDate()).toString()
 		);
 		assertEquals( df.format(airFrance.getBuyDate()), df.format(copyAirFrance.getBuyDate()));
 
 		s.delete( copyAirFrance );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testBasic() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight airFrance = new Flight();
 		airFrance.setId( Long.valueOf( 747 ) );
 		airFrance.setName( "Paris-Amsterdam" );
 		airFrance.setDuration( null );
 		try {
 			s.persist( airFrance );
 			tx.commit();
 			fail( "Basic(optional=false) fails" );
 		}
 		catch (Exception e) {
 			//success
 			if ( tx != null ) tx.rollback();
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Flight.class,
 				Company.class,
 				Sky.class
 		};
 	}
 
 	// tests are leaving data around, so drop/recreate schema for now.  this is wha the old tests did
 
 	@Override
 	protected boolean createSchema() {
 		return false;
 	}
 
 	@Before
 	public void runCreateSchema() {
 		schemaExport().create( false, true );
 	}
 
 	private SchemaExport schemaExport() {
 		return new SchemaExport( serviceRegistry(), metadata() );
 	}
 
 	@After
 	public void runDropSchema() {
 		schemaExport().drop( false, true );
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
index 64ec0b09c3..208c6efd63 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
@@ -1,132 +1,132 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.collectionelement;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
 import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 
 import org.hibernate.testing.TestForIssue;
 import org.junit.Test;
 
 /**
  * @author Gail Badner
  */
 public class CustomImprovedNamingCollectionElementTest extends ImprovedNamingCollectionElementTest {
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( new MyImprovedNamingStrategy() );
+		metadataBuilder.applyImplicitNamingStrategy( new MyImprovedNamingStrategy() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// MyNamingStrategyDelegator will use the owner primary table name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// MyNamingStrategyDelegator will use owner primary table name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_TABLE_elements" );
 	}
 
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// MyNamingStrategyDelegator will use owner primary table name, which will default to the JPA entity name
 		// in generated join column.
 		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_TABLE_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Boy.class, "hatedNames", "tbl_Boys_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultCollectionTableName( Boy.class, "hatedNames", "tbl_Boys_hatedNames" );
 	}
 
 	static class MyImprovedNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
 		@Override
 		public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
 			// This impl uses the owner entity table name instead of the JPA entity name when
 			// generating the implicit name.
 			final String name = source.getOwningPhysicalTableName().getText()
 					+ '_'
 					+ transformAttributePath( source.getOwningAttributePath() );
 
 			return toIdentifier( name, source.getBuildingContext() );
 		}
 
 		@Override
 		public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
 			final String name = source.getReferencedTableName() + "_" + source.getReferencedColumnName();
 			return toIdentifier( name, source.getBuildingContext() );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
index 9739f4f8f9..09bced161b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
@@ -1,421 +1,421 @@
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
 package org.hibernate.test.annotations.collectionelement;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 
 import org.hibernate.Filter;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.ForeignKey;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.hibernate.test.annotations.Country;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests @ElementCollection using the default "legacy" NamingStrategyDelegator which does not
  * comply with JPA spec in some cases. See HHH-9387 and HHH-9389 for more information..
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 @SuppressWarnings("unchecked")
 public class DefaultNamingCollectionElementTest extends BaseNonConfigCoreFunctionalTestCase {
 
 	@Test
 	public void testSimpleElement() throws Exception {
 		assertEquals(
 				"BoyFavoriteNumbers",
 				metadata().getCollectionBinding( Boy.class.getName() + '.' + "favoriteNumbers" )
 						.getCollectionTable().getName()
 		);
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		boy.getNickNames().add( "Johnny" );
 		boy.getNickNames().add( "Thing" );
 		boy.getScorePerNickName().put( "Johnny", 3 );
 		boy.getScorePerNickName().put( "Thing", 5 );
 		int[] favNbrs = new int[4];
 		for (int index = 0; index < favNbrs.length - 1; index++) {
 			favNbrs[index] = index * 3;
 		}
 		boy.setFavoriteNumbers( favNbrs );
 		boy.getCharacters().add( Character.GENTLE );
 		boy.getCharacters().add( Character.CRAFTY );
 
 		HashMap<String,FavoriteFood> foods = new HashMap<String,FavoriteFood>();
 		foods.put( "breakfast", FavoriteFood.PIZZA);
 		foods.put( "lunch", FavoriteFood.KUNGPAOCHICKEN);
 		foods.put( "dinner", FavoriteFood.SUSHI);
 		boy.setFavoriteFood(foods);
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy.getNickNames() );
 		assertTrue( boy.getNickNames().contains( "Thing" ) );
 		assertNotNull( boy.getScorePerNickName() );
 		assertTrue( boy.getScorePerNickName().containsKey( "Thing" ) );
 		assertEquals( Integer.valueOf( 5 ), boy.getScorePerNickName().get( "Thing" ) );
 		assertNotNull( boy.getFavoriteNumbers() );
 		assertEquals( 3, boy.getFavoriteNumbers()[1] );
 		assertTrue( boy.getCharacters().contains( Character.CRAFTY ) );
 		assertTrue( boy.getFavoriteFood().get("dinner").equals(FavoriteFood.SUSHI));
 		assertTrue( boy.getFavoriteFood().get("lunch").equals(FavoriteFood.KUNGPAOCHICKEN));
 		assertTrue( boy.getFavoriteFood().get("breakfast").equals(FavoriteFood.PIZZA));
 		List result = s.createQuery( "select boy from Boy boy join boy.nickNames names where names = :name" )
 				.setParameter( "name", "Thing" ).list();
 		assertEquals( 1, result.size() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeElement() throws Exception {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		Toy toy = new Toy();
 		toy.setName( "Balloon" );
 		toy.setSerial( "serial001" );
 		toy.setBrand( new Brand() );
 		toy.getBrand().setName( "Bandai" );
 		boy.getFavoriteToys().add( toy );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy );
 		assertNotNull( boy.getFavoriteToys() );
 		assertTrue( boy.getFavoriteToys().contains( toy ) );
 		assertEquals( "@Parent is failing", boy, boy.getFavoriteToys().iterator().next().getOwner() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testAttributedJoin() throws Exception {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Country country = new Country();
 		country.setName( "Australia" );
 		s.persist( country );
 
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		CountryAttitude attitude = new CountryAttitude();
 		// TODO: doesn't work
 		attitude.setBoy( boy );
 		attitude.setCountry( country );
 		attitude.setLikes( true );
 		boy.getCountryAttitudes().add( attitude );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertTrue( boy.getCountryAttitudes().contains( attitude ) );
 		s.delete( boy );
 		s.delete( s.get( Country.class, country.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testLazyCollectionofElements() throws Exception {
 		assertEquals(
 				"BoyFavoriteNumbers",
 				metadata().getCollectionBinding( Boy.class.getName() + '.' + "favoriteNumbers" )
 						.getCollectionTable().getName()
 		);
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		boy.getNickNames().add( "Johnny" );
 		boy.getNickNames().add( "Thing" );
 		boy.getScorePerNickName().put( "Johnny", 3 );
 		boy.getScorePerNickName().put( "Thing", 5 );
 		int[] favNbrs = new int[4];
 		for (int index = 0; index < favNbrs.length - 1; index++) {
 			favNbrs[index] = index * 3;
 		}
 		boy.setFavoriteNumbers( favNbrs );
 		boy.getCharacters().add( Character.GENTLE );
 		boy.getCharacters().add( Character.CRAFTY );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy.getNickNames() );
 		assertTrue( boy.getNickNames().contains( "Thing" ) );
 		assertNotNull( boy.getScorePerNickName() );
 		assertTrue( boy.getScorePerNickName().containsKey( "Thing" ) );
 		assertEquals( new Integer( 5 ), boy.getScorePerNickName().get( "Thing" ) );
 		assertNotNull( boy.getFavoriteNumbers() );
 		assertEquals( 3, boy.getFavoriteNumbers()[1] );
 		assertTrue( boy.getCharacters().contains( Character.CRAFTY ) );
 		List result = s.createQuery( "select boy from Boy boy join boy.nickNames names where names = :name" )
 				.setParameter( "name", "Thing" ).list();
 		assertEquals( 1, result.size() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchEagerAndFilter() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		TestCourse test = new TestCourse();
 
 		LocalizedString title = new LocalizedString( "title in english" );
 		title.getVariations().put( Locale.FRENCH.getLanguage(), "title en francais" );
 		test.setTitle( title );
 		s.save( test );
 
 		s.flush();
 		s.clear();
 
 		Filter filter = s.enableFilter( "selectedLocale" );
 		filter.setParameter( "param", "fr" );
 
 		Query q = s.createQuery( "from TestCourse t" );
 		List l = q.list();
 		assertEquals( 1, l.size() );
 
 		TestCourse t = (TestCourse) s.get( TestCourse.class, test.getTestCourseId() );
 		assertEquals( 1, t.getTitle().getVariations().size() );
 
 		tx.rollback();
 
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyType() throws Exception {
 		Matrix m = new Matrix();
 		m.getMvalues().put( 1, 1.1f );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( m );
 		s.flush();
 		s.clear();
 		m = (Matrix) s.get( Matrix.class, m.getId() );
 		assertEquals( 1.1f, m.getMvalues().get( 1 ), 0.01f );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testDefaultValueColumnForBasic() throws Exception {
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "hatedNames" );
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "preferredNames" );
 		isCollectionColumnPresent( Boy.class.getName(), "nickNames", "nickNames" );
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "scorePerPreferredName");
 	}
 
 	private void isDefaultValueCollectionColumnPresent(String collectionOwner, String propertyName) {
 		isCollectionColumnPresent( collectionOwner, propertyName, propertyName );
 	}
 
 	private void isCollectionColumnPresent(String collectionOwner, String propertyName, String columnName) {
 		final Collection collection = metadata().getCollectionBinding( collectionOwner + "." + propertyName );
 		final Iterator columnIterator = collection.getCollectionTable().getColumnIterator();
 		boolean hasDefault = false;
 		while ( columnIterator.hasNext() ) {
 			Column column = (Column) columnIterator.next();
 			if ( columnName.equals( column.getName() ) ) hasDefault = true;
 		}
 		assertTrue( "Could not find " + columnName, hasDefault );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameNoOverrides() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Products has @Entity (no @Table)
 		checkDefaultCollectionTableName( BugSystem.class, "bugs", "BugSystem_bugs" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		checkDefaultCollectionTableName( Boy.class, "hatedNames", "Boy_hatedNames" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Matrix_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Owner.class, "elements", "Owner_id" );
 	}
 
 	protected void checkDefaultCollectionTableName(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String expectedCollectionTableName) {
 		final org.hibernate.mapping.Collection collection = metadata().getCollectionBinding(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		final org.hibernate.mapping.Table table = collection.getCollectionTable();
 		assertEquals( expectedCollectionTableName, table.getName() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnNoOverrides() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Products has @Entity (no @Table)
 		checkDefaultJoinColumnName( BugSystem.class, "bugs", "BugSystem_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		checkDefaultJoinColumnName( Boy.class, "hatedNames", "Boy_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Matrix_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Owner.class, "elements", "Owner_elements" );
 	}
 
 	protected void checkDefaultJoinColumnName(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String ownerForeignKeyNameExpected) {
 		final org.hibernate.mapping.Collection ownerCollection = metadata().getCollectionBinding(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		// The default owner join column can only be computed if it has a PK with 1 column.
 		assertEquals ( 1, ownerCollection.getOwner().getKey().getColumnSpan() );
 		assertEquals( ownerForeignKeyNameExpected, ownerCollection.getKey().getColumnIterator().next().getText() );
 
 		boolean hasOwnerFK = false;
 		for ( Iterator it=ownerCollection.getCollectionTable().getForeignKeyIterator(); it.hasNext(); ) {
 			final ForeignKey fk = (ForeignKey) it.next();
 			assertSame( ownerCollection.getCollectionTable(), fk.getTable() );
 			if ( fk.getColumnSpan() > 1 ) {
 				continue;
 			}
 			if ( fk.getColumn( 0 ).getText().equals( ownerForeignKeyNameExpected ) ) {
 				assertSame( ownerCollection.getOwner().getTable(), fk.getReferencedTable() );
 				hasOwnerFK = true;
 			}
 		}
 		assertTrue( hasOwnerFK );
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
-		metadataBuilder.with( ImplicitNamingStrategyLegacyHbmImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyLegacyHbmImpl.INSTANCE );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Boy.class,
 				Country.class,
 				TestCourse.class,
 				Matrix.class,
 				Owner.class,
 				BugSystem.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
index b6283f5c62..f9700b9402 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
@@ -1,83 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.collectionelement;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 
 import org.hibernate.testing.TestForIssue;
 import org.junit.Test;
 
 /**
  * Tests @ElementCollection using the "improved" NamingStrategyDelegator which complies
  * with JPA spec.
  *
  * @author Gail Badner
  */
 public class ImprovedNamingCollectionElementTest extends DefaultNamingCollectionElementTest {
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
-		metadataBuilder.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_elements" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_id" );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
index 610c094242..fc59ee426a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
@@ -1,619 +1,619 @@
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
 package org.hibernate.test.annotations.embedded;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import java.util.Set;
 import java.util.UUID;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.hibernate.test.annotations.embedded.FloatLeg.RateIndex;
 import org.hibernate.test.annotations.embedded.Leg.Frequency;
 import org.hibernate.test.util.SchemaUtil;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EmbeddedTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testSimple() throws Exception {
 		Session s;
 		Transaction tx;
 		Person p = new Person();
 		Address a = new Address();
 		Country c = new Country();
 		Country bornCountry = new Country();
 		c.setIso2( "DM" );
 		c.setName( "Matt Damon Land" );
 		bornCountry.setIso2( "US" );
 		bornCountry.setName( "United States of America" );
 
 		a.address1 = "colorado street";
 		a.city = "Springfield";
 		a.country = c;
 		p.address = a;
 		p.bornIn = bornCountry;
 		p.name = "Homer";
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( p );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		p = (Person) s.get( Person.class, p.id );
 		assertNotNull( p );
 		assertNotNull( p.address );
 		assertEquals( "Springfield", p.address.city );
 		assertNotNull( p.address.country );
 		assertEquals( "DM", p.address.country.getIso2() );
 		assertNotNull( p.bornIn );
 		assertEquals( "US", p.bornIn.getIso2() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeId() throws Exception {
 		Session s;
 		Transaction tx;
 		RegionalArticlePk pk = new RegionalArticlePk();
 		pk.iso2 = "FR";
 		pk.localUniqueKey = "1234567890123";
 		RegionalArticle reg = new RegionalArticle();
 		reg.setName( "Je ne veux pes rester sage - Dolly" );
 		reg.setPk( pk );
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( reg );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		reg = (RegionalArticle) s.get( RegionalArticle.class, reg.getPk() );
 		assertNotNull( reg );
 		assertNotNull( reg.getPk() );
 		assertEquals( "Je ne veux pes rester sage - Dolly", reg.getName() );
 		assertEquals( "FR", reg.getPk().iso2 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneInsideComponent() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Person p = new Person();
 		Country bornIn = new Country();
 		bornIn.setIso2( "FR" );
 		bornIn.setName( "France" );
 		p.bornIn = bornIn;
 		p.name = "Emmanuel";
 		AddressType type = new AddressType();
 		type.setName( "Primary Home" );
 		s.persist( type );
 		Country currentCountry = new Country();
 		currentCountry.setIso2( "US" );
 		currentCountry.setName( "USA" );
 		Address add = new Address();
 		add.address1 = "4 square street";
 		add.city = "San diego";
 		add.country = currentCountry;
 		add.type = type;
 		p.address = add;
 		s.persist( p );
 		tx.commit();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "select p from Person p where p.address.city = :city" );
 		q.setString( "city", add.city );
 		List result = q.list();
 		Person samePerson = (Person) result.get( 0 );
 		assertNotNull( samePerson.address.type );
 		assertEquals( type.getName(), samePerson.address.type.getName() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedSuperclass() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		VanillaSwap swap = new VanillaSwap();
 		swap.setInstrumentId( "US345421" );
 		swap.setCurrency( VanillaSwap.Currency.EUR );
 		FixedLeg fixed = new FixedLeg();
 		fixed.setPaymentFrequency( Leg.Frequency.SEMIANNUALLY );
 		fixed.setRate( 5.6 );
 		FloatLeg floating = new FloatLeg();
 		floating.setPaymentFrequency( Leg.Frequency.QUARTERLY );
 		floating.setRateIndex( FloatLeg.RateIndex.LIBOR );
 		floating.setRateSpread( 1.1 );
 		swap.setFixedLeg( fixed );
 		swap.setFloatLeg( floating );
 		s.persist( swap );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		swap = (VanillaSwap) s.get( VanillaSwap.class, swap.getInstrumentId() );
 		// All fields must be filled with non-default values
 		fixed = swap.getFixedLeg();
 		assertNotNull( "Fixed leg retrieved as null", fixed );
 		floating = swap.getFloatLeg();
 		assertNotNull( "Floating leg retrieved as null", floating );
 		assertEquals( Leg.Frequency.SEMIANNUALLY, fixed.getPaymentFrequency() );
 		assertEquals( Leg.Frequency.QUARTERLY, floating.getPaymentFrequency() );
 		s.delete( swap );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDottedProperty() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		// Create short swap
 		Swap shortSwap = new Swap();
 		shortSwap.setTenor( 2 );
 		FixedLeg shortFixed = new FixedLeg();
 		shortFixed.setPaymentFrequency( Frequency.SEMIANNUALLY );
 		shortFixed.setRate( 5.6 );
 		FloatLeg shortFloating = new FloatLeg();
 		shortFloating.setPaymentFrequency( Frequency.QUARTERLY );
 		shortFloating.setRateIndex( RateIndex.LIBOR );
 		shortFloating.setRateSpread( 1.1 );
 		shortSwap.setFixedLeg( shortFixed );
 		shortSwap.setFloatLeg( shortFloating );
 		// Create medium swap
 		Swap swap = new Swap();
 		swap.setTenor( 7 );
 		FixedLeg fixed = new FixedLeg();
 		fixed.setPaymentFrequency( Frequency.MONTHLY );
 		fixed.setRate( 7.6 );
 		FloatLeg floating = new FloatLeg();
 		floating.setPaymentFrequency( Frequency.MONTHLY );
 		floating.setRateIndex( RateIndex.TIBOR );
 		floating.setRateSpread( 0.8 );
 		swap.setFixedLeg( fixed );
 		swap.setFloatLeg( floating );
 		// Create long swap
 		Swap longSwap = new Swap();
 		longSwap.setTenor( 7 );
 		FixedLeg longFixed = new FixedLeg();
 		longFixed.setPaymentFrequency( Frequency.MONTHLY );
 		longFixed.setRate( 7.6 );
 		FloatLeg longFloating = new FloatLeg();
 		longFloating.setPaymentFrequency( Frequency.MONTHLY );
 		longFloating.setRateIndex( RateIndex.TIBOR );
 		longFloating.setRateSpread( 0.8 );
 		longSwap.setFixedLeg( longFixed );
 		longSwap.setFloatLeg( longFloating );
 		// Compose a curve spread deal
 		SpreadDeal deal = new SpreadDeal();
 		deal.setId( "FX45632" );
 		deal.setNotional( 450000.0 );
 		deal.setShortSwap( shortSwap );
 		deal.setSwap( swap );
 		deal.setLongSwap( longSwap );
 		s.persist( deal );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		deal = (SpreadDeal) s.get( SpreadDeal.class, deal.getId() );
 		// All fields must be filled with non-default values
 		assertNotNull( "Short swap is null.", deal.getShortSwap() );
 		assertNotNull( "Swap is null.", deal.getSwap() );
 		assertNotNull( "Long swap is null.", deal.getLongSwap() );
 		assertEquals( 2, deal.getShortSwap().getTenor() );
 		assertEquals( 7, deal.getSwap().getTenor() );
 		assertEquals( 7, deal.getLongSwap().getTenor() );
 		assertNotNull( "Short fixed leg is null.", deal.getShortSwap().getFixedLeg() );
 		assertNotNull( "Short floating leg is null.", deal.getShortSwap().getFloatLeg() );
 		assertNotNull( "Fixed leg is null.", deal.getSwap().getFixedLeg() );
 		assertNotNull( "Floating leg is null.", deal.getSwap().getFloatLeg() );
 		assertNotNull( "Long fixed leg is null.", deal.getLongSwap().getFixedLeg() );
 		assertNotNull( "Long floating leg is null.", deal.getLongSwap().getFloatLeg() );
 		assertEquals( Frequency.SEMIANNUALLY, deal.getShortSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.QUARTERLY, deal.getShortSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getLongSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getLongSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( 5.6, deal.getShortSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( 7.6, deal.getSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( 7.6, deal.getLongSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( RateIndex.LIBOR, deal.getShortSwap().getFloatLeg().getRateIndex() );
 		assertEquals( RateIndex.TIBOR, deal.getSwap().getFloatLeg().getRateIndex() );
 		assertEquals( RateIndex.TIBOR, deal.getLongSwap().getFloatLeg().getRateIndex() );
 		assertEquals( 1.1, deal.getShortSwap().getFloatLeg().getRateSpread(), 0.01 );
 		assertEquals( 0.8, deal.getSwap().getFloatLeg().getRateSpread(), 0.01 );
 		assertEquals( 0.8, deal.getLongSwap().getFloatLeg().getRateSpread(), 0.01 );
 		s.delete( deal );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedInSecondaryTable() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		Book book = new Book();
 		book.setIsbn( "1234" );
 		book.setName( "HiA Second Edition" );
 		Summary summary = new Summary();
 		summary.setText( "This is a HiA SE summary" );
 		summary.setSize( summary.getText().length() );
 		book.setSummary( summary );
 		s.persist( book );
 		s.getTransaction().commit();
 
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		Book loadedBook = (Book) s.get( Book.class, book.getIsbn() );
 		assertNotNull( loadedBook.getSummary() );
 		assertEquals( book.getSummary().getText(), loadedBook.getSummary().getText() );
 		s.delete( loadedBook );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParent() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		Book book = new Book();
 		book.setIsbn( "1234" );
 		book.setName( "HiA Second Edition" );
 		Summary summary = new Summary();
 		summary.setText( "This is a HiA SE summary" );
 		summary.setSize( summary.getText().length() );
 		book.setSummary( summary );
 		s.persist( book );
 		s.getTransaction().commit();
 
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		Book loadedBook = (Book) s.get( Book.class, book.getIsbn() );
 		assertNotNull( loadedBook.getSummary() );
 		assertEquals( loadedBook, loadedBook.getSummary().getSummarizedBook() );
 		s.delete( loadedBook );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedAndMultipleManyToOne() throws Exception {
 		Session s;
 		s = openSession();
 		Transaction tx = s.beginTransaction();
 		CorpType type = new CorpType();
 		type.setType( "National" );
 		s.persist( type );
 		Nationality nat = new Nationality();
 		nat.setName( "Canadian" );
 		s.persist( nat );
 		InternetProvider provider = new InternetProvider();
 		provider.setBrandName( "Fido" );
 		LegalStructure structure = new LegalStructure();
 		structure.setCorporationType( type );
 		structure.setCountry( "Canada" );
 		structure.setName( "Rogers" );
 		provider.setOwner( structure );
 		structure.setOrigin( nat );
 		s.persist( provider );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
 		assertNotNull( provider.getOwner() );
 		assertNotNull( "Many to one not set", provider.getOwner().getCorporationType() );
 		assertEquals( "Wrong link", type.getType(), provider.getOwner().getCorporationType().getType() );
 		assertNotNull( "2nd Many to one not set", provider.getOwner().getOrigin() );
 		assertEquals( "Wrong 2nd link", nat.getName(), provider.getOwner().getOrigin().getName() );
 		s.delete( provider );
 		s.delete( provider.getOwner().getCorporationType() );
 		s.delete( provider.getOwner().getOrigin() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedAndOneToMany() throws Exception {
 		Session s;
 		s = openSession();
 		Transaction tx = s.beginTransaction();
 		InternetProvider provider = new InternetProvider();
 		provider.setBrandName( "Fido" );
 		LegalStructure structure = new LegalStructure();
 		structure.setCountry( "Canada" );
 		structure.setName( "Rogers" );
 		provider.setOwner( structure );
 		s.persist( provider );
 		Manager manager = new Manager();
 		manager.setName( "Bill" );
 		manager.setEmployer( provider );
 		structure.getTopManagement().add( manager );
 		s.persist( manager );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
 		assertNotNull( provider.getOwner() );
 		Set<Manager> topManagement = provider.getOwner().getTopManagement();
 		assertNotNull( "OneToMany not set", topManagement );
 		assertEquals( "Wrong number of elements", 1, topManagement.size() );
 		manager = topManagement.iterator().next();
 		assertEquals( "Wrong element", "Bill", manager.getName() );
 		s.delete( manager );
 		s.delete( provider );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9642")
 	public void testEmbeddedAndOneToManyHql() throws Exception {
 		Session s;
 		s = openSession();
 		Transaction tx = s.beginTransaction();
 		InternetProvider provider = new InternetProvider();
 		provider.setBrandName( "Fido" );
 		LegalStructure structure = new LegalStructure();
 		structure.setCountry( "Canada" );
 		structure.setName( "Rogers" );
 		provider.setOwner( structure );
 		s.persist( provider );
 		Manager manager = new Manager();
 		manager.setName( "Bill" );
 		manager.setEmployer( provider );
 		structure.getTopManagement().add( manager );
 		s.persist( manager );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		InternetProvider internetProviderQueried =
 				(InternetProvider) s.createQuery( "from InternetProvider" ).uniqueResult();
 		assertFalse( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		internetProviderQueried =
 				(InternetProvider) s.createQuery( "from InternetProvider i join fetch i.owner.topManagement" )
 						.uniqueResult();
 		assertTrue( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		internetProviderQueried =
 				(InternetProvider) s.createQuery( "from InternetProvider i join fetch i.owner o join fetch o.topManagement" )
 						.uniqueResult();
 		assertTrue( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
 		manager = provider.getOwner().getTopManagement().iterator().next();
 		s.delete( manager );
 		s.delete( provider );
 		tx.commit();
 		s.close();
 	}
 
 
 	@Test
 	public void testDefaultCollectionTable() throws Exception {
 		//are the tables correct?
 		assertTrue( SchemaUtil.isTablePresent("WealthyPerson_vacationHomes", metadata() ) );
 		assertTrue( SchemaUtil.isTablePresent("WealthyPerson_legacyVacationHomes", metadata() ) );
 		assertTrue( SchemaUtil.isTablePresent("WelPers_VacHomes", metadata() ) );
 
 		//just to make sure, use the mapping
 		Session s;
 		Transaction tx;
 		WealthyPerson p = new WealthyPerson();
 		Address a = new Address();
 		Address vacation = new Address();
 		Country c = new Country();
 		Country bornCountry = new Country();
 		c.setIso2( "DM" );
 		c.setName( "Matt Damon Land" );
 		bornCountry.setIso2( "US" );
 		bornCountry.setName( "United States of America" );
 
 		a.address1 = "colorado street";
 		a.city = "Springfield";
 		a.country = c;
 		vacation.address1 = "rock street";
 		vacation.city = "Plymouth";
 		vacation.country = c;
 		p.vacationHomes.add(vacation);
 		p.address = a;
 		p.bornIn = bornCountry;
 		p.name = "Homer";
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( p );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		p = (WealthyPerson) s.get( WealthyPerson.class, p.id );
 		assertNotNull( p );
 		assertNotNull( p.address );
 		assertEquals( "Springfield", p.address.city );
 		assertNotNull( p.address.country );
 		assertEquals( "DM", p.address.country.getIso2() );
 		assertNotNull( p.bornIn );
 		assertEquals( "US", p.bornIn.getIso2() );
 		tx.commit();
 		s.close();
 	}
 
 	// make sure we support collection of embeddable objects inside embeddable objects
 	@Test
 	public void testEmbeddableInsideEmbeddable() throws Exception {
 		Session s;
 		Transaction tx;
 
 		Collection<URLFavorite> urls = new ArrayList<URLFavorite>();
 		URLFavorite urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://highscalability.com/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.jboss.org/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.hibernate.org/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.jgroups.org/" );
 		urls.add( urlFavorite );
 
  		Collection<String>ideas = new ArrayList<String>();
 		ideas.add( "lionheart" );
 		ideas.add( "xforms" );
 		ideas.add( "dynamic content" );
 		ideas.add( "http" );
 
 		InternetFavorites internetFavorites = new InternetFavorites();
 		internetFavorites.setLinks( urls );
 		internetFavorites.setIdeas( ideas );
 
 		FavoriteThings favoriteThings = new FavoriteThings();
 		favoriteThings.setWeb( internetFavorites );
 
 		s = openSession();
 
 		tx = s.beginTransaction();
 		s.persist(favoriteThings);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.flush();
 		favoriteThings = (FavoriteThings) s.get( FavoriteThings.class,  favoriteThings.getId() );
 		assertTrue( "has web", favoriteThings.getWeb() != null );
 		assertTrue( "has ideas", favoriteThings.getWeb().getIdeas() != null );
 		assertTrue( "has favorite idea 'http'",favoriteThings.getWeb().getIdeas().contains("http") );
 		assertTrue( "has favorite idea 'http'",favoriteThings.getWeb().getIdeas().contains("dynamic content") );
 
 		urls = favoriteThings.getWeb().getLinks();
 		assertTrue( "has urls", urls != null);
 		URLFavorite[] favs = new URLFavorite[4];
 		urls.toArray(favs);
 		assertTrue( "has http://www.hibernate.org url favorite link",
 			"http://www.hibernate.org/".equals( favs[0].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[1].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[2].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[3].getUrl() ));
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-3868")
 	public void testTransientMergeComponentParent() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Book b = new Book();
 		b.setIsbn( UUID.randomUUID().toString() );
 		b.setSummary( new Summary() );
 		b = (Book) s.merge( b );
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Person.class,
 				WealthyPerson.class,
 				RegionalArticle.class,
 				AddressType.class,
 				VanillaSwap.class,
 				SpreadDeal.class,
 				Book.class,
 				InternetProvider.class,
 				CorpType.class,
 				Nationality.class,
 				Manager.class,
 				FavoriteThings.class
 		};
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/AbstractJPAIndexTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/AbstractJPAIndexTest.java
index 3b53c87b74..5373124b5a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/AbstractJPAIndexTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/index/jpa/AbstractJPAIndexTest.java
@@ -1,149 +1,149 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.test.annotations.index.jpa;
 
 import java.util.Iterator;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Set;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UniqueKey;
 
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 public abstract class AbstractJPAIndexTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 
 	@Test
 	public void testTableIndex() {
 		PersistentClass entity = metadata().getEntityBinding( Car.class.getName() );
 		Iterator itr = entity.getTable().getUniqueKeyIterator();
 		assertTrue( itr.hasNext() );
 		UniqueKey uk = (UniqueKey) itr.next();
 		assertFalse( itr.hasNext() );
 		assertTrue( StringHelper.isNotEmpty( uk.getName() ) );
 		assertEquals( 2, uk.getColumnSpan() );
 		Column column = (Column) uk.getColumns().get( 0 );
 		assertEquals( "brand", column.getName() );
 		column = (Column) uk.getColumns().get( 1 );
 		assertEquals( "producer", column.getName() );
 		assertSame( entity.getTable(), uk.getTable() );
 
 
 		itr = entity.getTable().getIndexIterator();
 		assertTrue( itr.hasNext() );
 		Index index = (Index)itr.next();
 		assertFalse( itr.hasNext() );
 		assertEquals( "Car_idx", index.getName() );
 		assertEquals( 1, index.getColumnSpan() );
 		column = index.getColumnIterator().next();
 		assertEquals( "since", column.getName() );
 		assertSame( entity.getTable(), index.getTable() );
 	}
 
 	@Test
 	public void testSecondaryTableIndex(){
 		PersistentClass entity = metadata().getEntityBinding( Car.class.getName() );
 
 		Join join = (Join)entity.getJoinIterator().next();
 		Iterator<Index> itr = join.getTable().getIndexIterator();
 		assertTrue( itr.hasNext() );
 		Index index = itr.next();
 		assertFalse( itr.hasNext() );
 		assertTrue( "index name is not generated", StringHelper.isNotEmpty( index.getName() ) );
 		assertEquals( 2, index.getColumnSpan() );
 		Iterator<Column> columnIterator = index.getColumnIterator();
 		Column column = columnIterator.next();
 		assertEquals( "dealer_name", column.getName() );
 		column = columnIterator.next();
 		assertEquals( "rate", column.getName() );
 		assertSame( join.getTable(), index.getTable() );
 
 	}
 
 	@Test
 	public void testCollectionTableIndex(){
 		PersistentClass entity = metadata().getEntityBinding( Car.class.getName() );
 		Property property = entity.getProperty( "otherDealers" );
 		Set set = (Set)property.getValue();
 		Table collectionTable = set.getCollectionTable();
 
 		Iterator<Index> itr = collectionTable.getIndexIterator();
 		assertTrue( itr.hasNext() );
 		Index index = itr.next();
 		assertFalse( itr.hasNext() );
 		assertTrue( "index name is not generated", StringHelper.isNotEmpty( index.getName() ) );
 		assertEquals( 1, index.getColumnSpan() );
 		Iterator<Column> columnIterator = index.getColumnIterator();
 		Column column = columnIterator.next();
 		assertEquals( "name", column.getName() );
 		assertSame( collectionTable, index.getTable() );
 
 	}
 
 	@Test
 	public void testJoinTableIndex(){
 		PersistentClass entity = metadata().getEntityBinding( Importer.class.getName() );
 		Property property = entity.getProperty( "cars" );
 		Bag set = (Bag)property.getValue();
 		Table collectionTable = set.getCollectionTable();
 
 		Iterator<Index> itr = collectionTable.getIndexIterator();
 		assertTrue( itr.hasNext() );
 		Index index = itr.next();
 		assertFalse( itr.hasNext() );
 		assertTrue( "index name is not generated", StringHelper.isNotEmpty( index.getName() ) );
 		assertEquals( 1, index.getColumnSpan() );
 		Iterator<Column> columnIterator = index.getColumnIterator();
 		Column column = columnIterator.next();
 		assertEquals( "importers_id", column.getName() );
 		assertSame( collectionTable, index.getTable() );
 	}
 
 	@Test
 	public void testTableGeneratorIndex(){
 		//todo
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/discriminatoroptions/DiscriminatorOptionsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/discriminatoroptions/DiscriminatorOptionsTest.java
index 8a6652fcd9..1de1036db1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/discriminatoroptions/DiscriminatorOptionsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/inheritance/discriminatoroptions/DiscriminatorOptionsTest.java
@@ -1,114 +1,114 @@
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
 package org.hibernate.test.annotations.inheritance.discriminatoroptions;
 
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test for the @DiscriminatorOptions annotations.
  *
  * @author Hardy Ferentschik
  */
 public class DiscriminatorOptionsTest extends BaseUnitTestCase {
 	@Test
 	public void testNonDefaultOptions() throws Exception {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			Metadata metadata = new MetadataSources( ssr )
 					.addAnnotatedClass( BaseClass.class )
 					.addAnnotatedClass( SubClass.class )
 					.buildMetadata();
 
 			PersistentClass persistentClass = metadata.getEntityBinding( BaseClass.class.getName() );
 			assertNotNull( persistentClass );
 			assertTrue( persistentClass instanceof RootClass );
 
 			RootClass root = (RootClass) persistentClass;
 			assertTrue( "Discriminator should be forced", root.isForceDiscriminator() );
 			assertFalse( "Discriminator should not be insertable", root.isDiscriminatorInsertable() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testBaseline() throws Exception {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			Metadata metadata = new MetadataSources( ssr )
 					.addAnnotatedClass( BaseClass2.class )
 					.addAnnotatedClass( SubClass2.class )
 					.buildMetadata();
 
 			PersistentClass persistentClass = metadata.getEntityBinding( BaseClass2.class.getName() );
 			assertNotNull( persistentClass );
 			assertTrue( persistentClass instanceof RootClass );
 
 			RootClass root = ( RootClass ) persistentClass;
 			assertFalse( "Discriminator should not be forced by default", root.isForceDiscriminator() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testPropertyBasedDiscriminatorForcing() throws Exception {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			Metadata metadata = new MetadataSources( ssr )
 					.addAnnotatedClass( BaseClass2.class )
 					.addAnnotatedClass( SubClass2.class )
 					.getMetadataBuilder()
-					.withImplicitForcingOfDiscriminatorsInSelect( true )
+					.enableImplicitForcingOfDiscriminatorsInSelect( true )
 					.build();
 
 			PersistentClass persistentClass = metadata.getEntityBinding( BaseClass2.class.getName() );
 			assertNotNull( persistentClass );
 			assertTrue( persistentClass instanceof RootClass );
 
 			RootClass root = ( RootClass ) persistentClass;
 			assertTrue( "Discriminator should be forced by property", root.isForceDiscriminator() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/join/JoinTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/join/JoinTest.java
index ae428571bd..8643bc0a93 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/join/JoinTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/join/JoinTest.java
@@ -1,265 +1,265 @@
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
 package org.hibernate.test.annotations.join;
 
 import java.util.ArrayList;
 import java.util.Date;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.mapping.Join;
 
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class JoinTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testDefaultValue() throws Exception {
 		Join join = (Join) metadata().getEntityBinding( Life.class.getName() ).getJoinClosureIterator().next();
 		assertEquals( "ExtendedLife", join.getTable().getName() );
 		org.hibernate.mapping.Column owner = new org.hibernate.mapping.Column();
 		owner.setName( "LIFE_ID" );
 		assertTrue( join.getTable().getPrimaryKey().containsColumn( owner ) );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Life life = new Life();
 		life.duration = 15;
 		life.fullDescription = "Long long description";
 		s.persist( life );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "from " + Life.class.getName() );
 		life = (Life) q.uniqueResult();
 		assertEquals( "Long long description", life.fullDescription );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositePK() throws Exception {
 		Join join = (Join) metadata().getEntityBinding( Dog.class.getName() ).getJoinClosureIterator().next();
 		assertEquals( "DogThoroughbred", join.getTable().getName() );
 		org.hibernate.mapping.Column owner = new org.hibernate.mapping.Column();
 		owner.setName( "OWNER_NAME" );
 		assertTrue( join.getTable().getPrimaryKey().containsColumn( owner ) );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Dog dog = new Dog();
 		DogPk id = new DogPk();
 		id.name = "Thalie";
 		id.ownerName = "Martine";
 		dog.id = id;
 		dog.weight = 30;
 		dog.thoroughbredName = "Colley";
 		s.persist( dog );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "from Dog" );
 		dog = (Dog) q.uniqueResult();
 		assertEquals( "Colley", dog.thoroughbredName );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testExplicitValue() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Death death = new Death();
 		death.date = new Date();
 		death.howDoesItHappen = "Well, haven't seen it";
 		s.persist( death );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "from " + Death.class.getName() );
 		death = (Death) q.uniqueResult();
 		assertEquals( "Well, haven't seen it", death.howDoesItHappen );
 		s.delete( death );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOne() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Life life = new Life();
 		Cat cat = new Cat();
 		cat.setName( "kitty" );
 		cat.setStoryPart2( "and the story continues" );
 		life.duration = 15;
 		life.fullDescription = "Long long description";
 		life.owner = cat;
 		s.persist( life );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Criteria crit = s.createCriteria( Life.class );
 		crit.createCriteria( "owner" ).add( Restrictions.eq( "name", "kitty" ) );
 		life = (Life) crit.uniqueResult();
 		assertEquals( "Long long description", life.fullDescription );
 		s.delete( life.owner );
 		s.delete( life );
 		tx.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testReferenceColumnWithBacktics() throws Exception {
 		Session s=openSession();
 		s.beginTransaction();
 		SysGroupsOrm g=new SysGroupsOrm();
 		SysUserOrm u=new SysUserOrm();
 		u.setGroups( new ArrayList<SysGroupsOrm>() );
 		u.getGroups().add( g );
 		s.save( g );
 		s.save( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 	
 	@Test
 	public void testUniqueConstaintOnSecondaryTable() throws Exception {
 		Cat cat = new Cat();
 		cat.setStoryPart2( "My long story" );
 		Cat cat2 = new Cat();
 		cat2.setStoryPart2( "My long story" );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		try {
 			s.persist( cat );
 			s.persist( cat2 );
 			tx.commit();
 			fail( "unique constraints violation on secondary table" );
 		}
 		catch (HibernateException e) {
 			//success
 			tx.rollback();
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Test
 	public void testFetchModeOnSecondaryTable() throws Exception {
 		Cat cat = new Cat();
 		cat.setStoryPart2( "My long story" );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		s.persist( cat );
 		s.flush();
 		s.clear();
 		
 		s.get( Cat.class, cat.getId() );
 		//Find a way to test it, I need to define the secondary table on a subclass
 
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testCustomSQL() throws Exception {
 		Cat cat = new Cat();
 		String storyPart2 = "My long story";
 		cat.setStoryPart2( storyPart2 );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		s.persist( cat );
 		s.flush();
 		s.clear();
 
 		Cat c = (Cat) s.get( Cat.class, cat.getId() );
 		assertEquals( storyPart2.toUpperCase(), c.getStoryPart2() );
 
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testMappedSuperclassAndSecondaryTable() throws Exception {
 		Session s = openSession( );
 		s.getTransaction().begin();
 		C c = new C();
 		c.setAge( 12 );
 		c.setCreateDate( new Date() );
 		c.setName( "Bob" );
 		s.persist( c );
 		s.flush();
 		s.clear();
 		c= (C) s.get( C.class, c.getId() );
 		assertNotNull( c.getCreateDate() );
 		assertNotNull( c.getName() );
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Life.class,
 				Death.class,
 				Cat.class,
 				Dog.class,
 				A.class,
 				B.class,
 				C.class,
 				SysGroupsOrm.class,
 				SysUserOrm.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/JpaCompliantManyToManyImplicitNamingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/JpaCompliantManyToManyImplicitNamingTest.java
index 56533cb912..925b87d9b4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/JpaCompliantManyToManyImplicitNamingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/JpaCompliantManyToManyImplicitNamingTest.java
@@ -1,83 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.manytomany.defaults;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 
 import org.hibernate.testing.TestForIssue;
 import org.junit.Test;
 
 /**
  * Tests names generated for @JoinTable and @JoinColumn for unidirectional and bidirectional
  * many-to-many associations using the JPA-compliant naming strategy.
  *
  * @author Gail Badner
  */
 public class JpaCompliantManyToManyImplicitNamingTest extends ManyToManyImplicitNamingTest {
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerPrimaryTableAssocEntityNamePKOverride() {
 		// City.stolenItems; associated entity: Item
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// Item has @Entity(name="ITEM") and no @Table
 		// PK column for City.id: id (default)
 		// PK column for Item: iId
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				City.class,
 				"stolenItems",
 				null,
 				"tbl_city_ITEM",
 				"City_id",
 				"stolenItems_iId"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
 		// Category.clients: associated entity: KnownClient
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// KnownClient has @Entity with no name configured and no @Table
 		// PK column for Category.id: id (default)
 		// PK column for KnownClient.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Category.class,
 				"clients",
 				null,
 				"CATEGORY_TAB_KnownClient",
 				"CATEGORY_id",
 				"clients_id"
 
 		);
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyImplicitNamingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyImplicitNamingTest.java
index 0bf27e70d8..a6ea9564c7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyImplicitNamingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyImplicitNamingTest.java
@@ -1,253 +1,253 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.manytomany.defaults;
 
 import java.util.Iterator;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.type.EntityType;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests names generated for @JoinTable and @JoinColumn for unidirectional and bidirectional
  * many-to-many associations using the "legacy JPA" naming strategy, which does not comply
  * with JPA spec in all cases.  See HHH-9390 for more information.
  *
  * NOTE: expected primary table names and join columns are explicit here to ensure that
  * entity names/tables and PK columns are not changed (which would invalidate these test cases).
  *
  * @author Gail Badner
  */
 public class ManyToManyImplicitNamingTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 
 	@Test
 	public void testBidirNoOverrides() {
 		// Employee.contactInfo.phoneNumbers: associated entity: PhoneNumber
 		// both have @Entity with no name configured and default primary table names;
 		// Primary table names default to unqualified entity classes.
 		// PK column for Employee.id: id (default)
 		// PK column for PhoneNumber.phNumber: phNumber (default)
 		// bidirectional association
 		checkDefaultJoinTablAndJoinColumnNames(
 				Employee.class,
 				"contactInfo.phoneNumbers",
 				"employees",
 				"Employee_PhoneNumber",
 				"employees_id",
 				"phoneNumbers_phNumber"
 		);
 	}
 
 	@Test
 	public void testBidirOwnerPKOverride() {
 		// Store.customers; associated entity: KnownClient
 		// both have @Entity with no name configured and default primary table names
 		// Primary table names default to unqualified entity classes.
 		// PK column for Store.id: sId
 		// PK column for KnownClient.id: id (default)
 		// bidirectional association
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"customers",
 				"stores",
 				"Store_KnownClient",
 				"stores_sId",
 				"customers_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocEntityNamePKOverride() {
 		// Store.items; associated entity: Item
 		// Store has @Entity with no name configured and no @Table
 		// Item has @Entity(name="ITEM") and no @Table
 		// PK column for Store.id: sId
 		// PK column for Item: iId
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"items",
 				null,
 				"Store_ITEM",
 				"Store_sId",
 				"items_iId"
 
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocPrimaryTableNameOverride() {
 		// Store.implantedIn; associated entity: City
 		// Store has @Entity with no name configured and no @Table
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// PK column for Store.id: sId
 		// PK column for City.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"implantedIn",
 				null,
 				"Store_tbl_city",
 				"Store_sId",
 				"implantedIn_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocEntityNamePrimaryTableOverride() {
 		// Store.categories; associated entity: Category
 		// Store has @Entity with no name configured and no @Table
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// PK column for Store.id: sId
 		// PK column for Category.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"categories",
 				null,
 				"Store_CATEGORY_TAB",
 				"Store_sId",
 				"categories_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerEntityNamePKAssocPrimaryTableOverride() {
 		// Item.producedInCities: associated entity: City
 		// Item has @Entity(name="ITEM") and no @Table
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// PK column for Item: iId
 		// PK column for City.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Item.class,
 				"producedInCities",
 				null,
 				"ITEM_tbl_city",
 				"ITEM_iId",
 				"producedInCities_id"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
 		// Category.clients: associated entity: KnownClient
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// KnownClient has @Entity with no name configured and no @Table
 		// PK column for Category.id: id (default)
 		// PK column for KnownClient.id: id (default)
 		// unidirectional
 		// legacy behavior would use the table name in the generated join column.
 		checkDefaultJoinTablAndJoinColumnNames(
 				Category.class,
 				"clients",
 				null,
 				"CATEGORY_TAB_KnownClient",
 				"CATEGORY_TAB_id",
 				"clients_id"
 
 		);
 	}
 
 	protected void checkDefaultJoinTablAndJoinColumnNames(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String inverseCollectionPropertyName,
 			String expectedCollectionTableName,
 			String ownerForeignKeyNameExpected,
 			String inverseForeignKeyNameExpected) {
 		final org.hibernate.mapping.Collection collection = metadata().getCollectionBinding( ownerEntityClass.getName() + '.' + ownerCollectionPropertyName );
 		final org.hibernate.mapping.Table table = collection.getCollectionTable();
 		assertEquals( expectedCollectionTableName, table.getName() );
 
 		final org.hibernate.mapping.Collection ownerCollection = metadata().getCollectionBinding(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		// The default owner and inverse join columns can only be computed if they have PK with 1 column.
 		assertEquals ( 1, ownerCollection.getOwner().getKey().getColumnSpan() );
 		assertEquals( ownerForeignKeyNameExpected, ownerCollection.getKey().getColumnIterator().next().getText() );
 
 		final EntityType associatedEntityType =  (EntityType) ownerCollection.getElement().getType();
 		final PersistentClass associatedPersistentClass =
 				metadata().getEntityBinding( associatedEntityType.getAssociatedEntityName() );
 		assertEquals( 1, associatedPersistentClass.getKey().getColumnSpan() );
 		if ( inverseCollectionPropertyName != null ) {
 			final org.hibernate.mapping.Collection inverseCollection = metadata().getCollectionBinding(
 					associatedPersistentClass.getEntityName() + '.' + inverseCollectionPropertyName
 			);
 			assertEquals(
 					inverseForeignKeyNameExpected,
 					inverseCollection.getKey().getColumnIterator().next().getText()
 			);
 		}
 		boolean hasOwnerFK = false;
 		boolean hasInverseFK = false;
 		for ( Iterator it=ownerCollection.getCollectionTable().getForeignKeyIterator(); it.hasNext(); ) {
 			final ForeignKey fk = (ForeignKey) it.next();
 			assertSame( ownerCollection.getCollectionTable(), fk.getTable() );
 			if ( fk.getColumnSpan() > 1 ) {
 				continue;
 			}
 			if ( fk.getColumn( 0 ).getText().equals( ownerForeignKeyNameExpected ) ) {
 				assertSame( ownerCollection.getOwner().getTable(), fk.getReferencedTable() );
 				hasOwnerFK = true;
 			}
 			else  if ( fk.getColumn( 0 ).getText().equals( inverseForeignKeyNameExpected ) ) {
 				assertSame( associatedPersistentClass.getTable(), fk.getReferencedTable() );
 				hasInverseFK = true;
 			}
 		}
 		assertTrue( hasOwnerFK );
 		assertTrue( hasInverseFK );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Category.class,
 				City.class,
 				Employee.class,
 				Item.class,
 				KnownClient.class,
 				PhoneNumber.class,
 				Store.class,
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
index 728c200a56..2a578e7af1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
@@ -1,76 +1,76 @@
 // $Id$
 package org.hibernate.test.annotations.namingstrategy;
 
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.mapping.Collection;
 import org.hibernate.service.ServiceRegistry;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.jboss.logging.Logger;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * Test harness for ANN-716.
  *
  * @author Hardy Ferentschik
  */
 public class NamingStrategyTest extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( NamingStrategyTest.class );
 
 	private ServiceRegistry serviceRegistry;
 
 	@Before
     public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@After
     public void tearDown() {
         if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
     @Test
 	public void testWithCustomNamingStrategy() throws Exception {
 		new MetadataSources( serviceRegistry )
 				.addAnnotatedClass(Address.class)
 				.addAnnotatedClass(Person.class)
 				.getMetadataBuilder()
-				.with( new DummyNamingStrategy() )
+				.applyPhysicalNamingStrategy( new DummyNamingStrategy() )
 				.build();
 	}
 
     @Test
 	public void testWithJpaCompliantNamingStrategy() throws Exception {
 		Metadata metadata = new MetadataSources( serviceRegistry )
 				.addAnnotatedClass( A.class )
 				.addAnnotatedClass( AddressEntry.class )
 				.getMetadataBuilder()
-				.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
+				.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
 				.build();
 
 		Collection collectionBinding = metadata.getCollectionBinding( A.class.getName() + ".address" );
 		assertEquals(
 				"Expecting A#address collection table name (implicit) to be [A_address] per JPA spec (section 11.1.8)",
 				"A_ADDRESS",
 				collectionBinding.getCollectionTable().getQuotedName().toUpperCase()
 		);
 	}
 
     @Test
 	public void testWithoutCustomNamingStrategy() throws Exception {
 		new MetadataSources( serviceRegistry )
 				.addAnnotatedClass( Address.class )
 				.addAnnotatedClass( Person.class )
 				.buildMetadata();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/override/AssociationOverrideTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/override/AssociationOverrideTest.java
index 07ea7de88b..f31db80b9e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/override/AssociationOverrideTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/override/AssociationOverrideTest.java
@@ -1,120 +1,120 @@
 package org.hibernate.test.annotations.override;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.hibernate.test.util.SchemaUtil;
 import org.junit.Test;
 
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class AssociationOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testOverriding() throws Exception {
 		Location paris = new Location();
 		paris.setName( "Paris" );
 		Location atlanta = new Location();
 		atlanta.setName( "Atlanta" );
 		Trip trip = new Trip();
 		trip.setFrom( paris );
 		//trip.setTo( atlanta );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( paris );
 		s.persist( atlanta );
 		try {
 			s.persist( trip );
 			s.flush();
 			fail( "Should be non nullable" );
 		}
 		catch (HibernateException e) {
 			//success
 		}
 		finally {
 			tx.rollback();
 			s.close();
 		}
 	}
 
 	@Test
 	public void testDottedNotation() throws Exception {
 		assertTrue( SchemaUtil.isTablePresent( "Employee", metadata() ) );
 		assertTrue( "Overridden @JoinColumn fails",
 				SchemaUtil.isColumnPresent( "Employee", "fld_address_fk", metadata() ) );
 
 		assertTrue( "Overridden @JoinTable name fails", SchemaUtil.isTablePresent( "tbl_empl_sites", metadata() ) );
 		assertTrue( "Overridden @JoinTable with default @JoinColumn fails",
 				SchemaUtil.isColumnPresent( "tbl_empl_sites", "employee_id", metadata() ) );
 		assertTrue( "Overridden @JoinTable.inverseJoinColumn fails",
 				SchemaUtil.isColumnPresent( "tbl_empl_sites", "to_website_fk", metadata() ) );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ContactInfo ci = new ContactInfo();
 		Addr address = new Addr();
 		address.setCity("Boston");
 		address.setCountry("USA");
 		address.setState("MA");
 		address.setStreet("27 School Street");
 		address.setZipcode("02108");
 		ci.setAddr(address);
 		List<PhoneNumber> phoneNumbers = new ArrayList();
 		PhoneNumber num = new PhoneNumber();
 		num.setNumber(5577188);
 		Employee e = new Employee();
 		Collection employeeList = new ArrayList();
 		employeeList.add(e);
 		e.setContactInfo(ci);
 		num.setEmployees(employeeList);
 		phoneNumbers.add(num);
 		ci.setPhoneNumbers(phoneNumbers);
 		SocialTouchPoints socialPoints = new SocialTouchPoints();
 		List<SocialSite> sites = new ArrayList<SocialSite>();
 		SocialSite site = new SocialSite();
 		site.setEmployee(employeeList);
 		site.setWebsite("www.jboss.org");
 		sites.add(site);
 		socialPoints.setWebsite(sites);
 		ci.setSocial(socialPoints);
 		s.persist(e);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.clear();
 		e = (Employee) s.get(Employee.class,e.getId());
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Employee.class,
 				Location.class,
 				Move.class,
 				Trip.class,
 				PhoneNumber.class,
 				Addr.class,
 				SocialSite.class,
 				SocialTouchPoints.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/strategy/StrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/strategy/StrategyTest.java
index 79665b273f..870e95c1b4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/strategy/StrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/strategy/StrategyTest.java
@@ -1,67 +1,67 @@
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
 package org.hibernate.test.annotations.strategy;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
 
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * @author Emmanuel Bernard
  */
 public class StrategyTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testComponentSafeStrategy() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Location start = new Location();
 		start.setCity( "Paris" );
 		start.setCountry( "France" );
 		Location end = new Location();
 		end.setCity( "London" );
 		end.setCountry( "UK" );
 		Storm storm = new Storm();
 		storm.setEnd( end );
 		storm.setStart( start );
 		s.persist( storm );
 		s.flush();
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
-		metadataBuilder.with( ImplicitNamingStrategyComponentPathImpl.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyComponentPathImpl.INSTANCE );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] { Storm.class };
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/CurrentTenantResolverMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/CurrentTenantResolverMultiTenancyTest.java
index a50717efc2..be73f268ff 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/CurrentTenantResolverMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/CurrentTenantResolverMultiTenancyTest.java
@@ -1,76 +1,76 @@
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
 package org.hibernate.test.multitenancy.schema;
 
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.junit.Assert;
 
 /**
  * SessionFactory has to use the {@link CurrentTenantIdentifierResolver} when
  * {@link SessionFactory#openSession()} is called.
  *
  * @author Stefan Schulze
  * @author Steve Ebersole
  */
 @TestForIssue(jiraKey = "HHH-7306")
 @RequiresDialectFeature( value = ConnectionProviderBuilder.class )
 public class CurrentTenantResolverMultiTenancyTest extends SchemaBasedMultiTenancyTest {
 
 	private TestCurrentTenantIdentifierResolver currentTenantResolver = new TestCurrentTenantIdentifierResolver();
 
 	@Override
 	protected void configure(SessionFactoryBuilder sfb) {
-		sfb.with( currentTenantResolver );
+		sfb.applyCurrentTenantIdentifierResolver( currentTenantResolver );
 	}
 
 	@Override
 	protected Session getNewSession(String tenant) {
 		currentTenantResolver.currentTenantIdentifier = tenant;
 		Session session = sessionFactory.openSession();
 		Assert.assertEquals( tenant, session.getTenantIdentifier() );
 		return session;
 	}
 
 
 	private static class TestCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
 		private String currentTenantIdentifier;
 
 		@Override
 		public boolean validateExistingCurrentSessions() {
 			return false;
 		}
 
 		@Override
 		public String resolveCurrentTenantIdentifier() {
 			return currentTenantIdentifier;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/FullyQualifiedEntityNameNamingStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/FullyQualifiedEntityNameNamingStrategyTest.java
index 158122d4da..cab7144dfb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/FullyQualifiedEntityNameNamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/FullyQualifiedEntityNameNamingStrategyTest.java
@@ -1,170 +1,170 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.namingstrategy;
 
 import java.util.Iterator;
 
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.EntityNaming;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
 import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.PersistentClass;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 public class FullyQualifiedEntityNameNamingStrategyTest extends BaseUnitTestCase {
 	private StandardServiceRegistry ssr;
 	private MetadataImplementor metadata;
 
 	@BeforeClassOnce
 	public void setUp() {
 		ssr = new StandardServiceRegistryBuilder().build();
 		metadata = (MetadataImplementor) new MetadataSources( ssr )
 				.addAnnotatedClass( Category.class )
 				.addAnnotatedClass( Item.class )
 				.addAnnotatedClass( Workflow.class )
 				.getMetadataBuilder()
-				.with( new MyNamingStrategy() )
+				.applyImplicitNamingStrategy( new MyNamingStrategy() )
 				.build();
 	}
 
 	@AfterClassOnce
 	public void tearDown() {
 		if ( ssr != null ) {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-4312")
 	public void testEntityTable() throws Exception {
 		final PersistentClass classMapping = metadata.getEntityBinding( Workflow.class.getName() );
 		final String expectedTableName = transformEntityName( Workflow.class.getName() );
 		assertEquals( expectedTableName, classMapping.getTable().getName() );
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-9327")
 	public void testElementCollectionTable() {
 		final Collection collectionMapping = metadata.getCollectionBinding(
 				Workflow.class.getName() + ".localized"
 		);
 		final String expectedTableName = transformEntityName( Workflow.class.getName() ) + "_localized";
 		assertEquals( expectedTableName, collectionMapping.getCollectionTable().getName() );
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-9327")
 	public void testManyToManyCollectionTable() {
 		final Collection collectionMapping = metadata.getCollectionBinding(
 				Category.class.getName() + "." + "items"
 		);
 		final String expectedTableName = transformEntityName( Category.class.getName() ) + "_" + transformEntityName( Item.class.getName() );
 		assertEquals( expectedTableName, collectionMapping.getCollectionTable().getName() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9327")
 	public void testManyToManyForeignKeys() {
 		final Collection ownerCollectionMapping = metadata.getCollectionBinding(
 				Category.class.getName() + "." + "items"
 		);
 		final String expectedOwnerFK = transformEntityName( Category.class.getName() ) + "_id";
 		final String expectedInverseFK = transformEntityName( Item.class.getName() ) + "_items_id";
 
 		boolean ownerFKFound = false;
 		boolean inverseFKFound = false;
 		for ( Iterator it = ownerCollectionMapping.getCollectionTable().getForeignKeyIterator(); it.hasNext(); ) {
 			final String fkColumnName = ( (ForeignKey) it.next() ).getColumn( 0 ).getName();
 			if ( expectedOwnerFK.equals( fkColumnName ) ) {
 				ownerFKFound = true;
 			}
 			else if ( expectedInverseFK.equals( fkColumnName ) ) {
 				inverseFKFound = true;
 			}
 		}
 		assertTrue( ownerFKFound );
 		assertTrue( inverseFKFound );
 	}
 
 	static String transformEntityName(String entityName) {
 		return entityName.replaceAll( "\\.", "_" );
 	}
 
 	public static class MyNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
 
 		private static final long serialVersionUID = -5713413771290957530L;
 
 		@Override
 		protected String transformEntityName(EntityNaming entityNaming) {
 			if ( entityNaming.getClassName() != null ) {
 				return FullyQualifiedEntityNameNamingStrategyTest.transformEntityName( entityNaming.getClassName() );
 			}
 			return super.transformEntityName( entityNaming );
 		}
 
 		@Override
 		public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
 			final String ownerPortion = transformEntityName( source.getOwningEntityNaming() );
 			final String ownedPortion;
 			if ( source.getNonOwningEntityNaming() != null ) {
 				ownedPortion = transformEntityName( source.getNonOwningEntityNaming() );
 			}
 			else {
 				ownedPortion = transformAttributePath( source.getAssociationOwningAttributePath() );
 			}
 
 			return toIdentifier( ownerPortion + "_" + ownedPortion, source.getBuildingContext() );
 		}
 
 		@Override
 		public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
 			final String entityPortion = transformEntityName( source.getEntityNaming() );
 			final String name;
 			if ( source.getAttributePath() == null ) {
 				name = entityPortion + "_" + source.getReferencedColumnName();
 			}
 			else {
 				name = entityPortion + "_"
 						+ transformAttributePath( source.getAttributePath() )
 						+ "_" + source.getReferencedColumnName();
 			}
 			return toIdentifier( name, source.getBuildingContext() );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/NamingStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/NamingStrategyTest.java
index 1b7fa7f369..ba73e75a48 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/NamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/NamingStrategyTest.java
@@ -1,79 +1,79 @@
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
 package org.hibernate.test.namingstrategy;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Emmanuel Bernard
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class NamingStrategyTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
-		metadataBuilder.with( (ImplicitNamingStrategy) TestNamingStrategy.INSTANCE );
-		metadataBuilder.with( ( PhysicalNamingStrategy) TestNamingStrategy.INSTANCE );
+		metadataBuilder.applyImplicitNamingStrategy( (ImplicitNamingStrategy) TestNamingStrategy.INSTANCE );
+		metadataBuilder.applyPhysicalNamingStrategy( (PhysicalNamingStrategy) TestNamingStrategy.INSTANCE );
 	}
 
     @Override
     protected Class<?>[] getAnnotatedClasses() {
         return new Class<?>[] {
                 Item.class
         };
     }
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"namingstrategy/Customers.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testDatabaseColumnNames() {
 		PersistentClass classMapping = metadata().getEntityBinding( Customers.class.getName() );
 		Column stateColumn = (Column) classMapping.getProperty( "specified_column" ).getColumnIterator().next();
 		assertEquals( "CN_specified_column", stateColumn.getName() );
 	}
 
     @Test
     @TestForIssue(jiraKey = "HHH-5848")
     public void testDatabaseTableNames() {
         PersistentClass classMapping = metadata().getEntityBinding( Item.class.getName() );
         Column secTabColumn = (Column) classMapping.getProperty( "specialPrice" ).getColumnIterator().next();
         assertEquals( "TAB_ITEMS_SEC", secTabColumn.getValue().getTable().getName() );
         Column tabColumn = (Column) classMapping.getProperty( "price" ).getColumnIterator().next();
         assertEquals( "TAB_ITEMS", tabColumn.getValue().getTable().getName() );
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/complete/BaseNamingTests.java b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/complete/BaseNamingTests.java
index 3dd85b27a2..969d1d85bd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/complete/BaseNamingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/complete/BaseNamingTests.java
@@ -1,265 +1,265 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.namingstrategy.complete;
 
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class BaseNamingTests extends BaseUnitTestCase {
 	@Test
 	public void doTest() {
 		final MetadataSources metadataSources = new MetadataSources();
 		applySources( metadataSources );
 
 		final Metadata metadata = metadataSources.getMetadataBuilder()
-				.with( getImplicitNamingStrategyToUse() )
+				.applyImplicitNamingStrategy( getImplicitNamingStrategyToUse() )
 				.build();
 
 		validateCustomer( metadata );
 		validateOrder( metadata );
 		validateZipCode( metadata );
 
 		validateCustomerRegisteredTrademarks( metadata );
 		validateCustomerAddresses( metadata );
 		validateCustomerOrders( metadata );
 		validateCustomerIndustries( metadata );
 	}
 
 	protected abstract void applySources(MetadataSources metadataSources);
 
 	protected abstract ImplicitNamingStrategy getImplicitNamingStrategyToUse();
 
 	protected void validateCustomer(Metadata metadata) {
 		final PersistentClass customerBinding = metadata.getEntityBinding( Customer.class.getName() );
 		assertNotNull( customerBinding );
 
 		validateCustomerPrimaryTableName( customerBinding.getTable().getQuotedName() );
 
 		assertEquals( 1, customerBinding.getIdentifier().getColumnSpan() );
 		validateCustomerPrimaryKeyColumn( (Column) customerBinding.getIdentifier().getColumnIterator().next() );
 
 		assertNotNull( customerBinding.getVersion() );
 		assertEquals( 1, customerBinding.getVersion().getColumnSpan() );
 		validateCustomerVersionColumn( (Column) customerBinding.getVersion().getColumnIterator().next() );
 
 		final Property nameBinding = customerBinding.getProperty( "name" );
 		assertNotNull( nameBinding );
 		assertEquals( 1, nameBinding.getColumnSpan() );
 		validateCustomerNameColumn( (Column) nameBinding.getColumnIterator().next() );
 
 		final Property hqAddressBinding = customerBinding.getProperty( "hqAddress" );
 		assertNotNull( hqAddressBinding );
 		assertEquals( 3, hqAddressBinding.getColumnSpan() );
 		validateCustomerHqAddressComponent( assertTyping( Component.class, hqAddressBinding.getValue() ) );
 	}
 
 	protected abstract void validateCustomerPrimaryTableName(String name);
 
 	protected abstract void validateCustomerPrimaryKeyColumn(Column column);
 
 	protected abstract void validateCustomerVersionColumn(Column column);
 
 	protected abstract void validateCustomerNameColumn(Column column);
 
 	protected abstract void validateCustomerHqAddressComponent(Component component);
 
 
 	protected void validateOrder(Metadata metadata) {
 		final PersistentClass orderBinding = metadata.getEntityBinding( Order.class.getName() );
 		assertNotNull( orderBinding );
 
 		validateOrderPrimaryTableName( orderBinding.getTable().getQuotedName() );
 
 		assertEquals( 1, orderBinding.getIdentifier().getColumnSpan() );
 		validateOrderPrimaryKeyColumn( (Column) orderBinding.getIdentifier().getColumnIterator().next() );
 
 		final Property referenceCodeBinding = orderBinding.getProperty( "referenceCode" );
 		assertNotNull( referenceCodeBinding );
 		assertEquals( 1, referenceCodeBinding.getColumnSpan() );
 		validateOrderReferenceCodeColumn( (Column) referenceCodeBinding.getColumnIterator().next() );
 
 		final Property placedBinding = orderBinding.getProperty( "placed" );
 		assertNotNull( placedBinding );
 		assertEquals( 1, placedBinding.getColumnSpan() );
 		validateOrderPlacedColumn( (Column) placedBinding.getColumnIterator().next() );
 
 		final Property fulfilledBinding = orderBinding.getProperty( "fulfilled" );
 		assertNotNull( fulfilledBinding );
 		assertEquals( 1, fulfilledBinding.getColumnSpan() );
 		validateOrderFulfilledColumn( (Column) fulfilledBinding.getColumnIterator().next() );
 
 		final Property customerBinding = orderBinding.getProperty( "customer" );
 		assertNotNull( customerBinding );
 		assertEquals( 1, customerBinding.getColumnSpan() );
 		validateOrderCustomerColumn( (Column) customerBinding.getColumnIterator().next() );
 	}
 
 	protected abstract void validateOrderPrimaryTableName(String name);
 
 	protected abstract void validateOrderPrimaryKeyColumn(Column column);
 
 	protected abstract void validateOrderReferenceCodeColumn(Column column);
 
 	protected abstract void validateOrderFulfilledColumn(Column column);
 
 	protected abstract void validateOrderPlacedColumn(Column column);
 
 	protected abstract void validateOrderCustomerColumn(Column column);
 
 
 
 	protected void validateZipCode(Metadata metadata) {
 		final PersistentClass zipCodeBinding = metadata.getEntityBinding( ZipCode.class.getName() );
 		assertNotNull( zipCodeBinding );
 
 		validateZipCodePrimaryTableName( zipCodeBinding.getTable().getQuotedName() );
 
 		assertEquals( 1, zipCodeBinding.getIdentifier().getColumnSpan() );
 		validateZipCodePrimaryKeyColumn( (Column) zipCodeBinding.getIdentifier().getColumnIterator().next() );
 
 		final Property codeBinding = zipCodeBinding.getProperty( "code" );
 		assertNotNull( codeBinding );
 		assertEquals( 1, codeBinding.getColumnSpan() );
 		validateZipCodeCodeColumn( (Column) codeBinding.getColumnIterator().next() );
 
 		final Property cityBinding = zipCodeBinding.getProperty( "city" );
 		assertNotNull( cityBinding );
 		assertEquals( 1, cityBinding.getColumnSpan() );
 		validateZipCodeCityColumn( (Column) cityBinding.getColumnIterator().next() );
 
 		final Property stateBinding = zipCodeBinding.getProperty( "state" );
 		assertNotNull( stateBinding );
 		assertEquals( 1, stateBinding.getColumnSpan() );
 		validateZipCodeStateColumn( (Column) stateBinding.getColumnIterator().next() );
 	}
 
 	protected abstract void validateZipCodePrimaryTableName(String name);
 
 	protected abstract void validateZipCodePrimaryKeyColumn(Column column);
 
 	protected abstract void validateZipCodeCodeColumn(Column column);
 
 	protected abstract void validateZipCodeCityColumn(Column column);
 
 	protected abstract void validateZipCodeStateColumn(Column column);
 
 
 	protected void validateCustomerRegisteredTrademarks(Metadata metadata) {
 		final Collection collectionBinding = metadata.getCollectionBinding( Customer.class.getName() + ".registeredTrademarks" );
 		assertNotNull( collectionBinding );
 
 		validateCustomerRegisteredTrademarksTableName( collectionBinding.getCollectionTable().getQuotedName() );
 
 		assertEquals( 1, collectionBinding.getKey().getColumnSpan() );
 		validateCustomerRegisteredTrademarksKeyColumn( (Column) collectionBinding.getKey().getColumnIterator().next() );
 
 		assertEquals( 1, collectionBinding.getElement().getColumnSpan() );
 		validateCustomerRegisteredTrademarksElementColumn(
 				(Column) collectionBinding.getElement()
 						.getColumnIterator()
 						.next()
 		);
 	}
 
 	protected abstract void validateCustomerRegisteredTrademarksTableName(String name);
 
 	protected abstract void validateCustomerRegisteredTrademarksKeyColumn(Column column);
 
 	protected abstract void validateCustomerRegisteredTrademarksElementColumn(Column column);
 
 
 	protected void validateCustomerAddresses(Metadata metadata) {
 		final Collection collectionBinding = metadata.getCollectionBinding( Customer.class.getName() + ".addresses" );
 		assertNotNull( collectionBinding );
 
 		validateCustomerAddressesTableName( collectionBinding.getCollectionTable().getQuotedName() );
 
 		assertEquals( 1, collectionBinding.getKey().getColumnSpan() );
 		validateCustomerAddressesKeyColumn( (Column) collectionBinding.getKey().getColumnIterator().next() );
 
 		assertEquals( 3, collectionBinding.getElement().getColumnSpan() );
 		validateCustomerAddressesElementComponent( assertTyping( Component.class, collectionBinding.getElement() ) );
 	}
 
 	protected abstract void validateCustomerAddressesTableName(String name);
 
 	protected abstract void validateCustomerAddressesKeyColumn(Column column);
 
 	protected abstract void validateCustomerAddressesElementComponent(Component component);
 
 
 	protected void validateCustomerOrders(Metadata metadata) {
 		final Collection collectionBinding = metadata.getCollectionBinding( Customer.class.getName() + ".orders" );
 		assertNotNull( collectionBinding );
 
 		validateCustomerOrdersTableName( collectionBinding.getCollectionTable().getQuotedName() );
 
 		assertEquals( 1, collectionBinding.getKey().getColumnSpan() );
 		validateCustomerOrdersKeyColumn( (Column) collectionBinding.getKey().getColumnIterator().next() );
 
 		assertEquals( 1, collectionBinding.getElement().getColumnSpan() );
 		validateCustomerOrdersElementColumn( (Column) collectionBinding.getElement().getColumnIterator().next() );
 	}
 
 	protected abstract void validateCustomerOrdersTableName(String name);
 
 	protected abstract void validateCustomerOrdersKeyColumn(Column column);
 
 	protected abstract void validateCustomerOrdersElementColumn(Column column);
 
 	protected void validateCustomerIndustries(Metadata metadata) {
 		final Collection collectionBinding = metadata.getCollectionBinding( Customer.class.getName() + ".industries" );
 		assertNotNull( collectionBinding );
 
 		validateCustomerIndustriesTableName( collectionBinding.getCollectionTable().getQuotedName() );
 
 		assertEquals( 1, collectionBinding.getKey().getColumnSpan() );
 		validateCustomerIndustriesKeyColumn( (Column) collectionBinding.getKey().getColumnIterator().next() );
 
 		assertEquals( 1, collectionBinding.getElement().getColumnSpan() );
 		validateCustomerIndustriesElementColumn( (Column) collectionBinding.getElement().getColumnIterator().next() );
 	}
 
 	protected abstract void validateCustomerIndustriesTableName(String name);
 
 	protected abstract void validateCustomerIndustriesKeyColumn(Column column);
 
 	protected abstract void validateCustomerIndustriesElementColumn(Column column);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/components/ComponentNamingStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/components/ComponentNamingStrategyTest.java
index 5d48a728fc..86880fcd55 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/components/ComponentNamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/namingstrategy/components/ComponentNamingStrategyTest.java
@@ -1,99 +1,99 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.namingstrategy.components;
 
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 public class ComponentNamingStrategyTest extends BaseUnitTestCase {
 	@Test
 	public void testDefaultNamingStrategy() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			final MetadataSources ms = new MetadataSources( ssr );
 			ms.addAnnotatedClass( Container.class ).addAnnotatedClass( Item.class );
 
 			final Metadata metadata = ms.getMetadataBuilder()
-					.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
+					.applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
 					.build();
 
 			final PersistentClass pc = metadata.getEntityBinding( Container.class.getName() );
 			Property p = pc.getProperty( "items" );
 			Bag value = assertTyping( Bag.class, p.getValue() );
 			SimpleValue elementValue = assertTyping( SimpleValue.class, value.getElement() );
 			assertEquals( 1, elementValue.getColumnSpan() );
 			Column column = assertTyping( Column.class, elementValue.getColumnIterator().next() );
 			assertEquals( column.getName(), "name" );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-6005" )
 	public void testComponentSafeNamingStrategy() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			final MetadataSources ms = new MetadataSources( ssr );
 			ms.addAnnotatedClass( Container.class ).addAnnotatedClass( Item.class );
 
 			final Metadata metadata = ms.getMetadataBuilder()
-					.with( ImplicitNamingStrategyComponentPathImpl.INSTANCE )
+					.applyImplicitNamingStrategy( ImplicitNamingStrategyComponentPathImpl.INSTANCE )
 					.build();
 
 			final PersistentClass pc = metadata.getEntityBinding( Container.class.getName() );
 			Property p = pc.getProperty( "items" );
 			Bag value = assertTyping( Bag.class, p.getValue() );
 			SimpleValue elementValue = assertTyping(  SimpleValue.class, value.getElement() );
 			assertEquals( 1, elementValue.getColumnSpan() );
 			Column column = assertTyping( Column.class, elementValue.getColumnIterator().next() );
 			assertEquals( column.getName(), "items_name" );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
index de0f7920c9..d98944ff7d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/type/AttributeConverterTest.java
@@ -1,621 +1,624 @@
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
 package org.hibernate.test.type;
 
 import java.io.Serializable;
 import java.sql.Timestamp;
 import java.sql.Types;
-import java.util.List;
 import javax.persistence.AttributeConverter;
 import javax.persistence.Column;
 import javax.persistence.Convert;
 import javax.persistence.Converter;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.IrrelevantEntity;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.ast.tree.JavaConstantNode;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.StringTypeDescriptor;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
-import org.hibernate.test.legacy.J;
 import org.junit.Test;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.fail;
 
 /**
  * Tests the principle of adding "AttributeConverter" to the mix of {@link org.hibernate.type.Type} resolution
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterTest extends BaseUnitTestCase {
 	@Test
 	public void testErrorInstantiatingConverterClass() {
 		Configuration cfg = new Configuration();
 		try {
 			cfg.addAttributeConverter( BlowsUpConverter.class );
 			fail( "expecting an exception" );
 		}
 		catch (AnnotationException e) {
 			assertNotNull( e.getCause() );
 			assertTyping( BlewUpException.class, e.getCause() );
 		}
 	}
 
 	public static class BlewUpException extends RuntimeException {
 	}
 
 	public static class BlowsUpConverter implements AttributeConverter<String,String> {
 		public BlowsUpConverter() {
 			throw new BlewUpException();
 		}
 
 		@Override
 		public String convertToDatabaseColumn(String attribute) {
 			return null;
 		}
 
 		@Override
 		public String convertToEntityAttribute(String dbData) {
 			return null;
 		}
 	}
 
 	@Test
 	public void testBasicOperation() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr ).buildMetadata();
 			SimpleValue simpleValue = new SimpleValue( metadata );
 			simpleValue.setJpaAttributeConverterDefinition(
 					new AttributeConverterDefinition( new StringClobConverter(), true )
 			);
 			simpleValue.setTypeUsingReflection( IrrelevantEntity.class.getName(), "name" );
 
 			Type type = simpleValue.getType();
 			assertNotNull( type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testNonAutoApplyHandling() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
-					.addAttributeConverter( NotAutoAppliedConverter.class, false )
 					.getMetadataBuilder()
+					.applyAttributeConverter( NotAutoAppliedConverter.class, false )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter with autoApply=false was auto applied" );
 			}
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 
 	}
 
 	@Test
 	public void testBasicConverterApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
-					.addAttributeConverter( StringClobConverter.class, true )
 					.getMetadataBuilder()
+					.applyAttributeConverter( StringClobConverter.class, true )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8462")
 	public void testBasicOrmXmlConverterApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
 					.addURL( ConfigHelper.findAsResource( "org/hibernate/test/type/orm.xml" ) )
 					.getMetadataBuilder()
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AttributeConverterTypeAdapter basicType = assertTyping( AttributeConverterTypeAdapter.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testBasicConverterDisableApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester2.class )
-					.addAttributeConverter( StringClobConverter.class, true )
 					.getMetadataBuilder()
+					.applyAttributeConverter( StringClobConverter.class, true )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester2.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter applied (should not have been)" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testBasicUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( IntegerToVarcharConverter.class, false );
 		cfg.addAnnotatedClass( Tester4.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new Tester4( 1L, "steve", 200 ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			session.get( Tester4.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			Tester4 t4 = (Tester4) session.get( Tester4.class, 1L );
 			t4.code = 300;
 			session.getTransaction().commit();
 			session.close();
 
 			session = sf.openSession();
 			session.beginTransaction();
 			t4 = (Tester4) session.get( Tester4.class, 1L );
 			assertEquals( 300, t4.code.longValue() );
 			session.delete( t4 );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testBasicTimestampUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( InstantConverter.class, false );
 		cfg.addAnnotatedClass( IrrelevantInstantEntity.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new IrrelevantInstantEntity( 1L ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			IrrelevantInstantEntity e = (IrrelevantInstantEntity) session.get( IrrelevantInstantEntity.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			session.delete( e );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8866")
 	public void testEnumConverter() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
 				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
 				.build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( EntityWithConvertibleField.class )
-					.addAttributeConverter( ConvertibleEnumConverter.class, true )
 					.getMetadataBuilder()
+					.applyAttributeConverter( ConvertibleEnumConverter.class, true )
 					.build();
 
 			// first lets validate that the converter was applied...
 			PersistentClass tester = metadata.getEntityBinding( EntityWithConvertibleField.class.getName() );
 			Property nameProp = tester.getProperty( "convertibleEnum" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertTyping( EnumJavaTypeDescriptor.class, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 
 			// then lets build the SF and verify its use...
 			final SessionFactory sf = metadata.buildSessionFactory();
 			try {
 				Session s = sf.openSession();
 				s.getTransaction().begin();
 				EntityWithConvertibleField entity = new EntityWithConvertibleField();
 				entity.setId( "ID" );
 				entity.setConvertibleEnum( ConvertibleEnum.VALUE );
 				String entityID = entity.getId();
 				s.persist( entity );
 				s.getTransaction().commit();
 				s.close();
 
 				s = sf.openSession();
 				s.beginTransaction();
 				entity = (EntityWithConvertibleField) s.load( EntityWithConvertibleField.class, entityID );
 				assertEquals( ConvertibleEnum.VALUE, entity.getConvertibleEnum() );
 				s.getTransaction().commit();
 				s.close();
 
 				JavaConstantNode javaConstantNode = new JavaConstantNode();
 				javaConstantNode.setExpectedType( type );
 				javaConstantNode.setSessionFactory( (SessionFactoryImplementor) sf );
 				javaConstantNode.setText( "org.hibernate.test.type.AttributeConverterTest$ConvertibleEnum.VALUE" );
 				final String outcome = javaConstantNode.getRenderText( (SessionFactoryImplementor) sf );
 				assertEquals( "'VALUE'", outcome );
 
 				s = sf.openSession();
 				s.beginTransaction();
 				s.createQuery( "FROM EntityWithConvertibleField e where e.convertibleEnum = org.hibernate.test.type.AttributeConverterTest$ConvertibleEnum.VALUE" )
 						.list();
 				s.getTransaction().commit();
 				s.close();
 
 				s = sf.openSession();
 				s.beginTransaction();
 				s.delete( entity );
 				s.getTransaction().commit();
 				s.close();
 			}
 			finally {
 				try {
 					sf.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 	
 	
 
 	// Entity declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Entity(name = "T1")
+	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester {
 		@Id
 		private Long id;
 		private String name;
 
 		public Tester() {
 		}
 
 		public Tester(Long id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 	}
 
 	@Entity(name = "T2")
+	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester2 {
 		@Id
 		private Long id;
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T3")
+	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester3 {
 		@Id
 		private Long id;
 		@org.hibernate.annotations.Type( type = "string" )
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T4")
+	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester4 {
 		@Id
 		private Long id;
 		private String name;
 		@Convert( converter = IntegerToVarcharConverter.class )
 		private Integer code;
 
 		public Tester4() {
 		}
 
 		public Tester4(Long id, String name, Integer code) {
 			this.id = id;
 			this.name = name;
 			this.code = code;
 		}
 	}
 
 	// This class is for mimicking an Instant from Java 8, which a converter might convert to a java.sql.Timestamp
 	public static class Instant implements Serializable {
 		private static final long serialVersionUID = 1L;
 
 		private long javaMillis;
 
 		public Instant(long javaMillis) {
 			this.javaMillis = javaMillis;
 		}
 
 		public long toJavaMillis() {
 			return javaMillis;
 		}
 
 		public static Instant fromJavaMillis(long javaMillis) {
 			return new Instant( javaMillis );
 		}
 
 		public static Instant now() {
 			return new Instant( System.currentTimeMillis() );
 		}
 	}
 
 	@Entity
+	@SuppressWarnings("UnusedDeclaration")
 	public static class IrrelevantInstantEntity {
 		@Id
 		private Long id;
 		private Instant dateCreated;
 
 		public IrrelevantInstantEntity() {
 		}
 
 		public IrrelevantInstantEntity(Long id) {
 			this( id, Instant.now() );
 		}
 
 		public IrrelevantInstantEntity(Long id, Instant dateCreated) {
 			this.id = id;
 			this.dateCreated = dateCreated;
 		}
 
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		public Instant getDateCreated() {
 			return dateCreated;
 		}
 
 		public void setDateCreated(Instant dateCreated) {
 			this.dateCreated = dateCreated;
 		}
 	}
 
 	public static enum ConvertibleEnum {
 		VALUE,
 		DEFAULT;
 
 		public String convertToString() {
 			switch ( this ) {
 				case VALUE: {
 					return "VALUE";
 				}
 				default: {
 					return "DEFAULT";
 				}
 			}
 		}
 	}
 
 	@Converter(autoApply = true)
 	public static class ConvertibleEnumConverter implements AttributeConverter<ConvertibleEnum, String> {
 		@Override
 		public String convertToDatabaseColumn(ConvertibleEnum attribute) {
 			return attribute.convertToString();
 		}
 
 		@Override
 		public ConvertibleEnum convertToEntityAttribute(String dbData) {
 			return ConvertibleEnum.valueOf( dbData );
 		}
 	}
 
 	@Entity( name = "EntityWithConvertibleField" )
 	@Table( name = "EntityWithConvertibleField" )
 	public static class EntityWithConvertibleField {
 		private String id;
 		private ConvertibleEnum convertibleEnum;
 
 		@Id
 		@Column(name = "id")
 		public String getId() {
 			return id;
 		}
 
 		public void setId(String id) {
 			this.id = id;
 		}
 
 		@Column(name = "testEnum")
 
 		public ConvertibleEnum getConvertibleEnum() {
 			return convertibleEnum;
 		}
 
 		public void setConvertibleEnum(ConvertibleEnum convertibleEnum) {
 			this.convertibleEnum = convertibleEnum;
 		}
 	}
 
 
 	// Converter declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Converter(autoApply = false)
 	public static class NotAutoAppliedConverter implements AttributeConverter<String,String> {
 		@Override
 		public String convertToDatabaseColumn(String attribute) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
 		}
 
 		@Override
 		public String convertToEntityAttribute(String dbData) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
 		}
 	}
 
 	@Converter( autoApply = true )
 	public static class IntegerToVarcharConverter implements AttributeConverter<Integer,String> {
 		@Override
 		public String convertToDatabaseColumn(Integer attribute) {
 			return attribute == null ? null : attribute.toString();
 		}
 
 		@Override
 		public Integer convertToEntityAttribute(String dbData) {
 			return dbData == null ? null : Integer.valueOf( dbData );
 		}
 	}
 
 
 	@Converter( autoApply = true )
 	public static class InstantConverter implements AttributeConverter<Instant, Timestamp> {
 		@Override
 		public Timestamp convertToDatabaseColumn(Instant attribute) {
 			return new Timestamp( attribute.toJavaMillis() );
 		}
 
 		@Override
 		public Instant convertToEntityAttribute(Timestamp dbData) {
 			return Instant.fromJavaMillis( dbData.getTime() );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index 62632ce987..c29afd64c5 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,911 +1,929 @@
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
 package org.hibernate.jpa.boot.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.persistence.AttributeConverter;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 
 import org.hibernate.Interceptor;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.archive.scan.internal.StandardScanOptions;
 import org.hibernate.boot.cfgxml.internal.ConfigLoader;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 import org.hibernate.jpa.boot.spi.StrategyRegistrationProviderList;
 import org.hibernate.jpa.boot.spi.TypeContributorList;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.AvailableSettings.JACC_CONTEXT_ID;
 import static org.hibernate.cfg.AvailableSettings.JACC_PREFIX;
 import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME;
 import static org.hibernate.jpa.AvailableSettings.CFG_FILE;
 import static org.hibernate.jpa.AvailableSettings.CLASS_CACHE_PREFIX;
 import static org.hibernate.jpa.AvailableSettings.COLLECTION_CACHE_PREFIX;
 import static org.hibernate.jpa.AvailableSettings.DISCARD_PC_ON_CLOSE;
 import static org.hibernate.jpa.AvailableSettings.PERSISTENCE_UNIT_NAME;
 import static org.hibernate.jpa.AvailableSettings.SHARED_CACHE_MODE;
 import static org.hibernate.jpa.AvailableSettings.VALIDATION_MODE;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryBuilderImpl implements EntityManagerFactoryBuilder {
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			EntityManagerFactoryBuilderImpl.class.getName()
 	);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// New settings
 
 	/**
 	 * Names a {@link IntegratorProvider}
 	 */
 	public static final String INTEGRATOR_PROVIDER = "hibernate.integrator_provider";
 	
 	/**
 	 * Names a {@link StrategyRegistrationProviderList}
 	 */
 	public static final String STRATEGY_REGISTRATION_PROVIDERS = "hibernate.strategy_registration_provider";
 	
 	/**
 	 * Names a {@link TypeContributorList}
 	 */
 	public static final String TYPE_CONTRIBUTORS = "hibernate.type_contributors";
 
 	/**
 	 * Names a Jandex {@link Index} instance to use.
 	 */
 	public static final String JANDEX_INDEX = "hibernate.jandex_index";
 
 
 	private final PersistenceUnitDescriptor persistenceUnit;
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// things built in first phase, needed for second phase..
 	private final Map configurationValues;
 	private final StandardServiceRegistry standardServiceRegistry;
 	private final MetadataImplementor metadata;
 	private final SettingsImpl settings;
 
 	/**
 	 * Intended for internal testing only...
 	 */
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		/**
 		 * Singleton access
 		 */
 		public static final JpaEntityNotFoundDelegate INSTANCE = new JpaEntityNotFoundDelegate();
 
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		this( persistenceUnit, integrationSettings, null );
 	}
 
 	public EntityManagerFactoryBuilderImpl(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			ClassLoader providedClassLoader ) {
 		
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 
 		// Build the boot-strap service registry, which mainly handles class loader interactions
 		final BootstrapServiceRegistry bsr = buildBootstrapServiceRegistry( integrationSettings, providedClassLoader );
 
 		// merge configuration sources and build the "standard" service registry
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 		final MergedSettings mergedSettings = mergeSettings( persistenceUnit, integrationSettings, ssrBuilder );
 		this.configurationValues = mergedSettings.getConfigurationValues();
 
 		// Build the "standard" service registry
 		ssrBuilder.applySettings( configurationValues );
 		this.settings = configure( ssrBuilder );
 		this.standardServiceRegistry = ssrBuilder.build();
 		configure( standardServiceRegistry, mergedSettings );
 
 		// Build the Metadata object
 		// 	NOTE : because we still use hibernate-commons-annotations, we still need to
 		//	use the TCCL hacks because hibernate-commons-annotations uses TCCL
 		final ClassLoaderService classLoaderService = bsr.getService( ClassLoaderService.class );
 		this.metadata = ( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work<MetadataImplementor>() {
 					@Override
 					public MetadataImplementor perform() {
 						final MetadataSources metadataSources = new MetadataSources( bsr );
-						populate( metadataSources, mergedSettings, standardServiceRegistry );
+						List<AttributeConverterDefinition> attributeConverterDefinitions = populate( metadataSources, mergedSettings, standardServiceRegistry );
 						final MetadataBuilder metamodelBuilder = metadataSources.getMetadataBuilder( standardServiceRegistry );
-						populate( metamodelBuilder, mergedSettings, standardServiceRegistry );
+						populate( metamodelBuilder, mergedSettings, standardServiceRegistry, attributeConverterDefinitions );
 						return (MetadataImplementor) metamodelBuilder.build();
 					}
 				}
 		);
 
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( collectNamesOfClassesToEnhance( metadata ) );
 		}
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// temporary!
 	@SuppressWarnings("unchecked")
 	public Map getConfigurationValues() {
 		return Collections.unmodifiableMap( configurationValues );
 	}
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Builds the {@link BootstrapServiceRegistry} used to eventually build the {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}; mainly
 	 * used here during instantiation to define class-loading behavior.
 	 *
 	 * @param integrationSettings Any integration settings passed by the EE container or SE application
 	 *
 	 * @return The built BootstrapServiceRegistry
 	 */
 	private BootstrapServiceRegistry buildBootstrapServiceRegistry(
 			Map integrationSettings,
 			ClassLoader providedClassLoader) {
 		final BootstrapServiceRegistryBuilder bsrBuilder = new BootstrapServiceRegistryBuilder();
 		bsrBuilder.with( new JpaIntegrator() );
 
 		final IntegratorProvider integratorProvider = (IntegratorProvider) integrationSettings.get( INTEGRATOR_PROVIDER );
 		if ( integratorProvider != null ) {
 			for ( Integrator integrator : integratorProvider.getIntegrators() ) {
 				bsrBuilder.with( integrator );
 			}
 		}
 		
 		final StrategyRegistrationProviderList strategyRegistrationProviderList
 				= (StrategyRegistrationProviderList) integrationSettings.get( STRATEGY_REGISTRATION_PROVIDERS );
 		if ( strategyRegistrationProviderList != null ) {
 			for ( StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviderList
 					.getStrategyRegistrationProviders() ) {
 				bsrBuilder.withStrategySelectors( strategyRegistrationProvider );
 			}
 		}
 
 
 		// ClassLoaders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		if ( persistenceUnit.getClassLoader() != null ) {
 			bsrBuilder.with( persistenceUnit.getClassLoader() );
 		}
 
 		if ( providedClassLoader != null ) {
 			bsrBuilder.with( providedClassLoader );
 		}
 
 		final ClassLoader appClassLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( appClassLoader != null ) {
 			LOG.debugf(
 					"Found use of deprecated `%s` setting; use `%s` instead.",
 					org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER,
 					org.hibernate.cfg.AvailableSettings.CLASSLOADERS
 			);
 		}
 		final Object classLoadersSetting = integrationSettings.get( org.hibernate.cfg.AvailableSettings.CLASSLOADERS );
 		if ( classLoadersSetting != null ) {
 			if ( java.util.Collection.class.isInstance( classLoadersSetting ) ) {
 				for ( ClassLoader classLoader : (java.util.Collection<ClassLoader>) classLoadersSetting ) {
 					bsrBuilder.with( classLoader );
 				}
 			}
 			else if ( classLoadersSetting.getClass().isArray() ) {
 				for ( ClassLoader classLoader : (ClassLoader[]) classLoadersSetting ) {
 					bsrBuilder.with( classLoader );
 				}
 			}
 			else if ( ClassLoader.class.isInstance( classLoadersSetting ) ) {
 				bsrBuilder.with( (ClassLoader) classLoadersSetting );
 			}
 		}
 
 		return bsrBuilder.build();
 	}
 
 	@SuppressWarnings("unchecked")
 	private MergedSettings mergeSettings(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map<?,?> integrationSettings,
 			StandardServiceRegistryBuilder ssrBuilder) {
 		final MergedSettings mergedSettings = new MergedSettings();
 
 		// first, apply persistence.xml-defined settings
 		if ( persistenceUnit.getProperties() != null ) {
 			mergedSettings.configurationValues.putAll( persistenceUnit.getProperties() );
 		}
 
 		mergedSettings.configurationValues.put( PERSISTENCE_UNIT_NAME, persistenceUnit.getName() );
 
 		final ConfigLoader configLoader = new ConfigLoader( ssrBuilder.getBootstrapServiceRegistry() );
 
 		// see if the persistence.xml settings named a Hibernate config file....
 		final String cfgXmlResourceName1 = (String) mergedSettings.configurationValues.remove( CFG_FILE );
 		if ( StringHelper.isNotEmpty( cfgXmlResourceName1 ) ) {
 			final LoadedConfig loadedCfg = configLoader.loadConfigXmlResource( cfgXmlResourceName1 );
 			processConfigXml( loadedCfg, mergedSettings, ssrBuilder );
 		}
 
 		// see if integration settings named a Hibernate config file....
 		final String cfgXmlResourceName2 = (String) integrationSettings.get( CFG_FILE );
 		if ( StringHelper.isNotEmpty( cfgXmlResourceName2 ) ) {
 			integrationSettings.remove( CFG_FILE );
 			final LoadedConfig loadedCfg = configLoader.loadConfigXmlResource( cfgXmlResourceName2 );
 			processConfigXml( loadedCfg, mergedSettings, ssrBuilder );
 		}
 
 		// finally, apply integration-supplied settings (per JPA spec, integration settings should override other sources)
 		for ( Map.Entry<?,?> entry : integrationSettings.entrySet() ) {
 			if ( entry.getKey() == null ) {
 				continue;
 			}
 
 			if ( entry.getValue() == null ) {
 				mergedSettings.configurationValues.remove( entry.getKey() );
 			}
 			else {
 				mergedSettings.configurationValues.put( entry.getKey(), entry.getValue() );
 			}
 		}
 
 		if ( !mergedSettings.configurationValues.containsKey( VALIDATION_MODE ) ) {
 			if ( persistenceUnit.getValidationMode() != null ) {
 				mergedSettings.configurationValues.put( VALIDATION_MODE, persistenceUnit.getValidationMode() );
 			}
 		}
 
 		if ( !mergedSettings.configurationValues.containsKey( SHARED_CACHE_MODE ) ) {
 			if ( persistenceUnit.getSharedCacheMode() != null ) {
 				mergedSettings.configurationValues.put( SHARED_CACHE_MODE, persistenceUnit.getSharedCacheMode() );
 			}
 		}
 
 		final String jaccContextId = (String) mergedSettings.configurationValues.get( JACC_CONTEXT_ID );
 
 		// here we are going to iterate the merged config settings looking for:
 		//		1) additional JACC permissions
 		//		2) additional cache region declarations
 		//
 		// we will also clean up an references with null entries
 		Iterator itr = mergedSettings.configurationValues.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			if ( entry.getValue() == null ) {
 				// remove entries with null values
 				itr.remove();
 				break;
 			}
 
 			if ( String.class.isInstance( entry.getKey() ) && String.class.isInstance( entry.getValue() ) ) {
 				final String keyString = (String) entry.getKey();
 				final String valueString = (String) entry.getValue();
 
 				if ( keyString.startsWith( JACC_PREFIX ) ) {
 					if ( jaccContextId == null ) {
 						LOG.debug(
 								"Found JACC permission grant [%s] in properties, but no JACC context id was specified; ignoring"
 						);
 					}
 					else {
 						mergedSettings.getJaccPermissions( jaccContextId ).addPermissionDeclaration(
 								parseJaccConfigEntry( keyString, valueString )
 						);
 					}
 				}
 				else if ( keyString.startsWith( CLASS_CACHE_PREFIX ) ) {
 					mergedSettings.addCacheRegionDefinition(
 							parseCacheRegionDefinitionEntry(
 									keyString.substring( CLASS_CACHE_PREFIX.length() + 1 ),
 									valueString,
 									CacheRegionDefinition.CacheRegionType.ENTITY
 							)
 					);
 				}
 				else if ( keyString.startsWith( COLLECTION_CACHE_PREFIX ) ) {
 					mergedSettings.addCacheRegionDefinition(
 							parseCacheRegionDefinitionEntry(
 									keyString.substring( COLLECTION_CACHE_PREFIX.length() + 1 ),
 									(String) entry.getValue(),
 									CacheRegionDefinition.CacheRegionType.COLLECTION
 							)
 					);
 				}
 			}
 
 		}
 
 		return mergedSettings;
 	}
 
 	@SuppressWarnings("unchecked")
 	private void processConfigXml(
 			LoadedConfig loadedConfig,
 			MergedSettings mergedSettings,
 			StandardServiceRegistryBuilder ssrBuilder) {
 		if ( ! mergedSettings.configurationValues.containsKey( SESSION_FACTORY_NAME ) ) {
 			// there is not already a SF-name in the merged settings
 			final String sfName = loadedConfig.getSessionFactoryName();
 			if ( sfName != null ) {
 				// but the cfg.xml file we are processing named one..
 				mergedSettings.configurationValues.put( SESSION_FACTORY_NAME, sfName );
 			}
 		}
 
 		mergedSettings.configurationValues.putAll( loadedConfig.getConfigurationValues() );
 		ssrBuilder.configure( loadedConfig );
 	}
 
 	private GrantedPermission parseJaccConfigEntry(String keyString, String valueString) {
 		try {
 			final int roleStart = JACC_PREFIX.length() + 1;
 			final String role = keyString.substring( roleStart, keyString.indexOf( '.', roleStart ) );
 			final int classStart = roleStart + role.length() + 1;
 			final String clazz = keyString.substring( classStart, keyString.length() );
 			return new GrantedPermission( role, clazz, valueString );
 		}
 		catch ( IndexOutOfBoundsException e ) {
 			throw persistenceException( "Illegal usage of " + JACC_PREFIX + ": " + keyString );
 		}
 	}
 
 	private CacheRegionDefinition parseCacheRegionDefinitionEntry(String role, String value, CacheRegionDefinition.CacheRegionType cacheType) {
 		final StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			if ( cacheType == CacheRegionDefinition.CacheRegionType.ENTITY ) {
 				error.append( CLASS_CACHE_PREFIX )
 						.append( ": " )
 						.append( CLASS_CACHE_PREFIX );
 			}
 			else {
 				error.append( COLLECTION_CACHE_PREFIX )
 						.append( ": " )
 						.append( COLLECTION_CACHE_PREFIX );
 			}
 			error.append( '.' )
 					.append( role )
 					.append( ' ' )
 					.append( value )
 					.append( ".  Was expecting configuration (usage[,region[,lazy]]), but found none" );
 			throw persistenceException( error.toString() );
 		}
 
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		boolean lazyProperty = true;
 		if ( cacheType == CacheRegionDefinition.CacheRegionType.ENTITY ) {
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 		}
 		else {
 			lazyProperty = false;
 		}
 
 		return new CacheRegionDefinition( cacheType, role, usage, region, lazyProperty );
 	}
 
 	private SettingsImpl configure(StandardServiceRegistryBuilder ssrBuilder) {
 		final SettingsImpl settings = new SettingsImpl();
 
 		applyJdbcConnectionProperties( ssrBuilder );
 		applyTransactionProperties( ssrBuilder, settings );
 
 		// flush before completion validation
 		if ( "true".equals( configurationValues.get( Environment.FLUSH_BEFORE_COMPLETION ) ) ) {
 			ssrBuilder.applySetting( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 			LOG.definingFlushBeforeCompletionIgnoredInHem( Environment.FLUSH_BEFORE_COMPLETION );
 		}
 
 		final Object value = configurationValues.get( DISCARD_PC_ON_CLOSE );
 		if ( value != null ) {
 			settings.setReleaseResourcesOnCloseEnabled( "true".equals( value ) );
 		}
 
 		final StrategySelector strategySelector = ssrBuilder.getBootstrapServiceRegistry().getService( StrategySelector.class );
 		final Object interceptorSetting = configurationValues.remove( AvailableSettings.SESSION_INTERCEPTOR );
 		if ( interceptorSetting != null ) {
 			settings.setSessionInterceptorClass(
 					loadSessionInterceptorClass( interceptorSetting, strategySelector )
 			);
 		}
 
 		return settings;
 	}
 
 	private void applyJdbcConnectionProperties(StandardServiceRegistryBuilder ssrBuilder) {
 		if ( dataSource != null ) {
 			ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, dataSource );
 		}
 		else if ( persistenceUnit.getJtaDataSource() != null ) {
 			if ( ! ssrBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.JTA_DATASOURCE, persistenceUnit.getJtaDataSource() );
 			}
 		}
 		else if ( persistenceUnit.getNonJtaDataSource() != null ) {
 			if ( ! ssrBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.NON_JTA_DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 			}
 		}
 		else {
 			final String driver = (String) configurationValues.get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DRIVER, driver );
 			}
 			final String url = (String) configurationValues.get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.URL, url );
 			}
 			final String user = (String) configurationValues.get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.USER, user );
 			}
 			final String pass = (String) configurationValues.get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				ssrBuilder.applySetting( org.hibernate.cfg.AvailableSettings.PASS, pass );
 			}
 		}
 	}
 
 	private void applyTransactionProperties(StandardServiceRegistryBuilder ssrBuilder, SettingsImpl settings) {
 		PersistenceUnitTransactionType txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(
 				configurationValues.get( AvailableSettings.TRANSACTION_TYPE )
 		);
 		if ( txnType == null ) {
 			txnType = persistenceUnit.getTransactionType();
 		}
 		if ( txnType == null ) {
 			// is it more appropriate to have this be based on bootstrap entry point (EE vs SE)?
 			txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		settings.setTransactionType( txnType );
 		boolean hasTxStrategy = configurationValues.containsKey( Environment.TRANSACTION_STRATEGY );
 		if ( hasTxStrategy ) {
 			LOG.overridingTransactionStrategyDangerous( Environment.TRANSACTION_STRATEGY );
 		}
 		else {
 			if ( txnType == PersistenceUnitTransactionType.JTA ) {
 				ssrBuilder.applySetting( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class );
 			}
 			else if ( txnType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 				ssrBuilder.applySetting( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class );
 			}
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private Class<? extends Interceptor> loadSessionInterceptorClass(Object value, StrategySelector strategySelector) {
 		if ( value == null ) {
 			return null;
 		}
 
 		return Class.class.isInstance( value )
 				? (Class<? extends Interceptor>) value
 				: strategySelector.selectStrategyImplementor( Interceptor.class, value.toString() );
 	}
 
 	private void configure(StandardServiceRegistry ssr, MergedSettings mergedSettings) {
 		final StrategySelector strategySelector = ssr.getService( StrategySelector.class );
 
 		// apply id generators
 		final Object idGeneratorStrategyProviderSetting = configurationValues.remove( AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER );
 		if ( idGeneratorStrategyProviderSetting != null ) {
 			final IdentifierGeneratorStrategyProvider idGeneratorStrategyProvider =
 					strategySelector.resolveStrategy( IdentifierGeneratorStrategyProvider.class, idGeneratorStrategyProviderSetting );
 			final MutableIdentifierGeneratorFactory identifierGeneratorFactory = ssr.getService( MutableIdentifierGeneratorFactory.class );
 			if ( identifierGeneratorFactory == null ) {
 				throw persistenceException(
 						"Application requested custom identifier generator strategies, " +
 								"but the MutableIdentifierGeneratorFactory could not be found"
 				);
 			}
 			for ( Map.Entry<String,Class<?>> entry : idGeneratorStrategyProvider.getStrategies().entrySet() ) {
 				identifierGeneratorFactory.register( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	@SuppressWarnings("unchecked")
-	private void populate(
+	private List<AttributeConverterDefinition> populate(
 			MetadataSources metadataSources,
 			MergedSettings mergedSettings,
 			StandardServiceRegistry ssr) {
 //		final ClassLoaderService classLoaderService = ssr.getService( ClassLoaderService.class );
 //
 //		// todo : make sure MetadataSources/Metadata are capable of handling duplicate sources
 //
 //		// explicit persistence unit mapping files listings
 //		if ( persistenceUnit.getMappingFileNames() != null ) {
 //			for ( String name : persistenceUnit.getMappingFileNames() ) {
 //				metadataSources.addResource( name );
 //			}
 //		}
 //
 //		// explicit persistence unit managed class listings
 //		//		IMPL NOTE : managed-classes can contain class or package names!!!
 //		if ( persistenceUnit.getManagedClassNames() != null ) {
 //			for ( String managedClassName : persistenceUnit.getManagedClassNames() ) {
 //				// try it as a class name first...
 //				final String classFileName = managedClassName.replace( '.', '/' ) + ".class";
 //				final URL classFileUrl = classLoaderService.locateResource( classFileName );
 //				if ( classFileUrl != null ) {
 //					// it is a class
 //					metadataSources.addAnnotatedClassName( managedClassName );
 //					continue;
 //				}
 //
 //				// otherwise, try it as a package name
 //				final String packageInfoFileName = managedClassName.replace( '.', '/' ) + "/package-info.class";
 //				final URL packageInfoFileUrl = classLoaderService.locateResource( packageInfoFileName );
 //				if ( packageInfoFileUrl != null ) {
 //					// it is a package
 //					metadataSources.addPackage( managedClassName );
 //					continue;
 //				}
 //
 //				LOG.debugf(
 //						"Unable to resolve class [%s] named in persistence unit [%s]",
 //						managedClassName,
 //						persistenceUnit.getName()
 //				);
 //			}
 //		}
 
+		List<AttributeConverterDefinition> attributeConverterDefinitions = null;
+
 		// add any explicit Class references passed in
 		final List<Class> loadedAnnotatedClasses = (List<Class>) configurationValues.remove( AvailableSettings.LOADED_CLASSES );
 		if ( loadedAnnotatedClasses != null ) {
 			for ( Class cls : loadedAnnotatedClasses ) {
 				if ( AttributeConverter.class.isAssignableFrom( cls ) ) {
-					metadataSources.addAttributeConverter( (Class<? extends AttributeConverter>) cls );
+					if ( attributeConverterDefinitions == null ) {
+						attributeConverterDefinitions = new ArrayList<AttributeConverterDefinition>();
+					}
+					attributeConverterDefinitions.add( AttributeConverterDefinition.from( (Class<? extends AttributeConverter>) cls ) );
 				}
 				else {
 					metadataSources.addAnnotatedClass( cls );
 				}
 			}
 		}
 
 		// add any explicit hbm.xml references passed in
 		final String explicitHbmXmls = (String) configurationValues.remove( AvailableSettings.HBXML_FILES );
 		if ( explicitHbmXmls != null ) {
 			for ( String hbmXml : StringHelper.split( ", ", explicitHbmXmls ) ) {
 				metadataSources.addResource( hbmXml );
 			}
 		}
 
 		// add any explicit orm.xml references passed in
 		final List<String> explicitOrmXmlList = (List<String>) configurationValues.remove( AvailableSettings.XML_FILE_NAMES );
 		if ( explicitOrmXmlList != null ) {
 			for ( String ormXml : explicitOrmXmlList ) {
 				metadataSources.addResource( ormXml );
 			}
 		}
+
+		return attributeConverterDefinitions;
 	}
 
-	private void populate(MetadataBuilder metamodelBuilder, MergedSettings mergedSettings, StandardServiceRegistry ssr) {
+	private void populate(
+			MetadataBuilder metamodelBuilder,
+			MergedSettings mergedSettings,
+			StandardServiceRegistry ssr,
+			List<AttributeConverterDefinition> attributeConverterDefinitions) {
 		if ( persistenceUnit.getTempClassLoader() != null ) {
-			metamodelBuilder.with( persistenceUnit.getTempClassLoader() );
+			metamodelBuilder.applyTempClassLoader( persistenceUnit.getTempClassLoader() );
 		}
 
-		metamodelBuilder.with( new StandardJpaScanEnvironmentImpl( persistenceUnit ) );
-		metamodelBuilder.with(
+		metamodelBuilder.applyScanEnvironment( new StandardJpaScanEnvironmentImpl( persistenceUnit ) );
+		metamodelBuilder.applyScanOptions(
 				new StandardScanOptions(
 						(String) configurationValues.get( org.hibernate.cfg.AvailableSettings.SCANNER_DISCOVERY ),
 						persistenceUnit.isExcludeUnlistedClasses()
 				)
 		);
 
 		if ( mergedSettings.cacheRegionDefinitions != null ) {
 			for ( CacheRegionDefinition localCacheRegionDefinition : mergedSettings.cacheRegionDefinitions ) {
-				metamodelBuilder.with( localCacheRegionDefinition );
+				metamodelBuilder.applyCacheRegionDefinition( localCacheRegionDefinition );
 			}
 		}
 
 		final Object namingStrategySetting = configurationValues.remove( AvailableSettings.NAMING_STRATEGY );
 		if ( namingStrategySetting != null ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategySetting(
 					AvailableSettings.NAMING_STRATEGY,
 					org.hibernate.cfg.AvailableSettings.IMPLICIT_NAMING_STRATEGY,
 					org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY
 			);
 		}
 
 		final TypeContributorList typeContributorList = (TypeContributorList) configurationValues.remove(
 				TYPE_CONTRIBUTORS
 		);
 		if ( typeContributorList != null ) {
 			for ( TypeContributor typeContributor : typeContributorList.getTypeContributors() ) {
-				metamodelBuilder.with( typeContributor );
+				metamodelBuilder.applyTypes( typeContributor );
+			}
+		}
+
+		if ( attributeConverterDefinitions != null ) {
+			for ( AttributeConverterDefinition attributeConverterDefinition : attributeConverterDefinitions ) {
+				metamodelBuilder.applyAttributeConverter( attributeConverterDefinition );
 			}
 		}
 	}
 
 	private List<String> collectNamesOfClassesToEnhance(MetadataImplementor metadata) {
 		final List<String> entityClassNames = new ArrayList<String>();
 		for ( PersistentClass persistentClass : metadata.getEntityBindings() ) {
 			if ( persistentClass.getClassName() != null ) {
 				entityClassNames.add( persistentClass.getClassName() );
 			}
 		}
 		return entityClassNames;
 	}
 
 
 
 
 	// Phase 2 concerns ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private Object validatorFactory;
 	private Object cdiBeanManager;
 	private DataSource dataSource;
 
 	@Override
 	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
 		this.validatorFactory = validatorFactory;
 
 		if ( validatorFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validatorFactory );
 		}
 		return this;
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
 		this.dataSource = dataSource;
 
 		return this;
 	}
 
 	@Override
 	public void cancel() {
 		// todo : close the bootstrap registry (not critical, but nice to do)
 	}
 
 	@Override
 	public void generateSchema() {
 		// This seems overkill, but building the SF is necessary to get the Integrators to kick in.
 		// Metamodel will clean this up...
 		try {
 			SessionFactoryBuilder sfBuilder = metadata.getSessionFactoryBuilder();
 			populate( sfBuilder, standardServiceRegistry );
 			sfBuilder.build();
 
 			JpaSchemaGenerator.performGeneration( metadata, configurationValues, standardServiceRegistry );
 		}
 		catch (Exception e) {
 			throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 		}
 
 
 		// release this builder
 		cancel();
 	}
 
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory build() {
 		SessionFactoryBuilder sfBuilder = metadata.getSessionFactoryBuilder();
 		populate( sfBuilder, standardServiceRegistry );
 
 		SessionFactoryImplementor sessionFactory;
 		try {
 			sessionFactory = (SessionFactoryImplementor) sfBuilder.build();
 		}
 		catch (Exception e) {
 			throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 		}
 
 		JpaSchemaGenerator.performGeneration( metadata, configurationValues, standardServiceRegistry );
 
 		return new EntityManagerFactoryImpl(
 				persistenceUnit.getName(),
 				sessionFactory,
 				metadata,
 				settings,
 				configurationValues
 		);
 	}
 
 	private void populate(SessionFactoryBuilder sfBuilder, StandardServiceRegistry ssr) {
 		final StrategySelector strategySelector = ssr.getService( StrategySelector.class );
 
 		// Locate and apply the requested SessionFactory-level interceptor (if one)
 		final Object sessionFactoryInterceptorSetting = configurationValues.remove( AvailableSettings.INTERCEPTOR );
 		if ( sessionFactoryInterceptorSetting != null ) {
 			final Interceptor sessionFactoryInterceptor =
 					strategySelector.resolveStrategy( Interceptor.class, sessionFactoryInterceptorSetting );
-			sfBuilder.with( sessionFactoryInterceptor );
+			sfBuilder.applyInterceptor( sessionFactoryInterceptor );
 		}
 
 		// Locate and apply any requested SessionFactoryObserver
 		final Object sessionFactoryObserverSetting = configurationValues.remove( AvailableSettings.SESSION_FACTORY_OBSERVER );
 		if ( sessionFactoryObserverSetting != null ) {
 			final SessionFactoryObserver suppliedSessionFactoryObserver =
 					strategySelector.resolveStrategy( SessionFactoryObserver.class, sessionFactoryObserverSetting );
-			sfBuilder.add( suppliedSessionFactoryObserver );
+			sfBuilder.addSessionFactoryObservers( suppliedSessionFactoryObserver );
 		}
 
-		sfBuilder.add( ServiceRegistryCloser.INSTANCE );
+		sfBuilder.addSessionFactoryObservers( ServiceRegistryCloser.INSTANCE );
 
-		sfBuilder.with( JpaEntityNotFoundDelegate.INSTANCE );
+		sfBuilder.applyEntityNotFoundDelegate( JpaEntityNotFoundDelegate.INSTANCE );
 
 		if ( this.validatorFactory != null ) {
-			sfBuilder.withValidatorFactory( validatorFactory );
+			sfBuilder.applyValidatorFactory( validatorFactory );
 		}
 		if ( this.cdiBeanManager != null ) {
-			sfBuilder.withBeanManager( cdiBeanManager );
+			sfBuilder.applyBeanManager( cdiBeanManager );
 		}
 	}
 
 
 	public static class ServiceRegistryCloser implements SessionFactoryObserver {
 		/**
 		 * Singleton access
 		 */
 		public static final ServiceRegistryCloser INSTANCE = new ServiceRegistryCloser();
 
 		@Override
 		public void sessionFactoryCreated(SessionFactory sessionFactory) {
 			// nothing to do
 		}
 
 		@Override
 		public void sessionFactoryClosed(SessionFactory sessionFactory) {
 			SessionFactoryImplementor sfi = ( (SessionFactoryImplementor) sessionFactory );
 			sfi.getServiceRegistry().destroy();
 			ServiceRegistry basicRegistry = sfi.getServiceRegistry().getParentServiceRegistry();
 			( (ServiceRegistryImplementor) basicRegistry ).destroy();
 		}
 	}
 
 	private PersistenceException persistenceException(String message) {
 		return persistenceException( message, null );
 	}
 
 	private PersistenceException persistenceException(String message, Exception cause) {
 		return new PersistenceException(
 				getExceptionHeader() + message,
 				cause
 		);
 	}
 
 	private String getExceptionHeader() {
 		return "[PersistenceUnit: " + persistenceUnit.getName() + "] ";
 	}
 
 	public static class MergedSettings {
 		private final Map configurationValues = new ConcurrentHashMap( 16, 0.75f, 1 );
 
 		private Map<String, JaccPermissionDeclarations> jaccPermissionsByContextId;
 		private List<CacheRegionDefinition> cacheRegionDefinitions;
 
 		public MergedSettings() {
 		}
 
 		public Map getConfigurationValues() {
 			return configurationValues;
 		}
 
 		public JaccPermissionDeclarations getJaccPermissions(String jaccContextId) {
 			if ( jaccPermissionsByContextId == null ) {
 				jaccPermissionsByContextId = new HashMap<String, JaccPermissionDeclarations>();
 			}
 
 			JaccPermissionDeclarations jaccPermissions = jaccPermissionsByContextId.get( jaccContextId );
 			if ( jaccPermissions == null ) {
 				jaccPermissions = new JaccPermissionDeclarations( jaccContextId );
 				jaccPermissionsByContextId.put( jaccContextId, jaccPermissions );
 			}
 			return jaccPermissions;
 		}
 
 		public void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
 			if ( this.cacheRegionDefinitions == null ) {
 				this.cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 			}
 			this.cacheRegionDefinitions.add( cacheRegionDefinition );
 		}
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java
index a5cfc1f5cf..49f0c0d7de 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/converter/BasicModelingTest.java
@@ -1,70 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.envers.test.entities.converter;
 
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.internal.MetadataImpl;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.envers.test.AbstractEnversTest;
 import org.hibernate.mapping.PersistentClass;
 
 import org.hibernate.testing.TestForIssue;
 import org.junit.Test;
 
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicModelingTest extends AbstractEnversTest {
 	@Test
 	@TestForIssue( jiraKey = "HHH-9042" )
 	public void testMetamodelBuilding() {
 		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
 				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
 				.build();
 		try {
 			Metadata metadata = new MetadataSources( ssr )
-					.addAttributeConverter( SexConverter.class )
 					.addAnnotatedClass( Person.class )
-					.buildMetadata();
+					.getMetadataBuilder()
+					.applyAttributeConverter( SexConverter.class )
+					.build();
 
 			( (MetadataImpl) metadata ).validate();
 
 			PersistentClass personBinding = metadata.getEntityBinding( Person.class.getName() );
 			assertNotNull( personBinding );
 
 			PersistentClass personAuditBinding = metadata.getEntityBinding( Person.class.getName() + "_AUD" );
 			assertNotNull( personAuditBinding );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 }
diff --git a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
index 1985a7f6a9..dc1c778007 100644
--- a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
+++ b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
@@ -1,141 +1,141 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.osgi;
 
 import java.util.Collection;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.jboss.logging.Logger;
 
 import org.osgi.framework.Bundle;
 import org.osgi.framework.ServiceFactory;
 import org.osgi.framework.ServiceRegistration;
 import org.osgi.framework.wiring.BundleWiring;
 
 /**
  * Hibernate 4.2 and 4.3 still heavily rely on TCCL for ClassLoading.  Although
  * our ClassLoaderService removed some of the reliance, access to the proper ClassLoader
  * via TCCL is still required in a few cases where we call out to external libs.  An OSGi
  * bundle manually creating a SessionFactory would require numerous ClassLoader
  * tricks (or may be impossible altogether).
  * <p/>
  * In order to fully control the TCCL issues and shield users from the
  * knowledge, we're requiring that bundles use this OSGi ServiceFactory.  It
  * configures and provides a SessionFactory as an OSGi service.
  * <p/>
  * Note that an OSGi ServiceFactory differs from a Service.  The ServiceFactory
  * allows individual instances of Services to be created and provided to
  * multiple client Bundles.
  *
  * @author Brett Meyer
  * @author Tim Ward
  */
 public class OsgiSessionFactoryService implements ServiceFactory {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
 			OsgiSessionFactoryService.class.getName());
 	
 	private OsgiClassLoader osgiClassLoader;
 	private OsgiJtaPlatform osgiJtaPlatform;
 	private OsgiServiceUtil osgiServiceUtil;
 
 	/**
 	 * Constructs a OsgiSessionFactoryService
 	 *
 	 * @param osgiClassLoader The OSGi-specific ClassLoader created in HibernateBundleActivator
 	 * @param osgiJtaPlatform The OSGi-specific JtaPlatform created in HibernateBundleActivator
 	 * @param osgiServiceUtil Util object built in HibernateBundleActivator
 	 */
 	public OsgiSessionFactoryService(
 			OsgiClassLoader osgiClassLoader,
 			OsgiJtaPlatform osgiJtaPlatform,
 			OsgiServiceUtil osgiServiceUtil) {
 		this.osgiClassLoader = osgiClassLoader;
 		this.osgiJtaPlatform = osgiJtaPlatform;
 		this.osgiServiceUtil = osgiServiceUtil;
 	}
 
 	@Override
 	public Object getService(Bundle requestingBundle, ServiceRegistration registration) {
 		osgiClassLoader.addBundle( requestingBundle );
 
 		final BootstrapServiceRegistryBuilder bsrBuilder = new BootstrapServiceRegistryBuilder();
 		bsrBuilder.with( new OSGiClassLoaderServiceImpl( osgiClassLoader, osgiServiceUtil ) );
 
 		final Integrator[] integrators = osgiServiceUtil.getServiceImpls( Integrator.class );
 		for ( Integrator integrator : integrators ) {
 			bsrBuilder.with( integrator );
 		}
 
 		final StrategyRegistrationProvider[] strategyRegistrationProviders
 				= osgiServiceUtil.getServiceImpls( StrategyRegistrationProvider.class );
 		for ( StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviders ) {
 			bsrBuilder.withStrategySelectors( strategyRegistrationProvider );
 		}
 
 		final BootstrapServiceRegistry bsr = bsrBuilder.build();
 		final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );
 
 		// Allow bundles to put the config file somewhere other than the root level.
 		final BundleWiring bundleWiring = (BundleWiring) requestingBundle.adapt( BundleWiring.class );
 		final Collection<String> cfgResources = bundleWiring.listResources( "/", "hibernate.cfg.xml",
 																			BundleWiring.LISTRESOURCES_RECURSE );
 		if (cfgResources.size() == 0) {
 			ssrBuilder.configure();
 		}
 		else {
 			if (cfgResources.size() > 1) {
 				LOG.warn( "Multiple hibernate.cfg.xml files found in the persistence bundle.  Using the first one discovered." );
 			}
 			String cfgResource = "/" + cfgResources.iterator().next();
 			ssrBuilder.configure( cfgResource );
 		}
 
 		ssrBuilder.applySetting( AvailableSettings.JTA_PLATFORM, osgiJtaPlatform );
 
 		final StandardServiceRegistry ssr = ssrBuilder.build();
 
 		final MetadataBuilder metadataBuilder = new MetadataSources( ssr ).getMetadataBuilder();
 		final TypeContributor[] typeContributors = osgiServiceUtil.getServiceImpls( TypeContributor.class );
 		for ( TypeContributor typeContributor : typeContributors ) {
-			metadataBuilder.with( typeContributor );
+			metadataBuilder.applyTypes( typeContributor );
 		}
 
 		return metadataBuilder.build().buildSessionFactory();
 	}
 
 	@Override
 	public void ungetService(Bundle requestingBundle, ServiceRegistration registration, Object service) {
 		((SessionFactory) service).close();
 	}
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseNonConfigCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseNonConfigCoreFunctionalTestCase.java
index b5d6951f3d..c4909b81e2 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseNonConfigCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseNonConfigCoreFunctionalTestCase.java
@@ -1,537 +1,537 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.testing.junit4;
 
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.NClob;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.BlobType;
 import org.hibernate.type.ClobType;
 import org.hibernate.type.NClobType;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.cache.CachingRegionFactory;
 import org.junit.After;
 import org.junit.Before;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}.
  * Much like {@link org.hibernate.testing.junit4.BaseCoreFunctionalTestCase}, except that
  * this form uses the new bootstrapping APIs while BaseCoreFunctionalTestCase continues to
  * use (the neutered form of) Configuration.
  *
  * @author Steve Ebersole
  */
 public class BaseNonConfigCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 
 	private StandardServiceRegistry serviceRegistry;
 	private MetadataImplementor metadata;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected Dialect getDialect() {
 		if ( serviceRegistry != null ) {
 			return serviceRegistry.getService( JdbcEnvironment.class ).getDialect();
 		}
 		else {
 			return BaseCoreFunctionalTestCase.getDialect();
 		}
 	}
 
 	protected StandardServiceRegistry serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected MetadataImplementor metadata() {
 		return metadata;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
 		return session;
 	}
 
 	protected Session getSession() {
 		return session;
 	}
 
 	protected void rebuildSessionFactory() {
 		releaseResources();
 		buildResources();
 	}
 
 	protected void cleanupCache() {
 		if ( sessionFactory != null ) {
 			sessionFactory.getCache().evictAllRegions();
 		}
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// JUNIT hooks
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected void startUp() {
 		buildResources();
 	}
 
 	protected void buildResources() {
 		final StandardServiceRegistryBuilder ssrb = constructStandardServiceRegistryBuilder();
 
 		serviceRegistry = ssrb.build();
 		afterStandardServiceRegistryBuilt( serviceRegistry );
 
 		final MetadataSources metadataSources = new MetadataSources( serviceRegistry );
 		applyMetadataSources( metadataSources );
 		afterMetadataSourcesApplied( metadataSources );
 
 		final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
 		initialize( metadataBuilder );
 		configureMetadataBuilder( metadataBuilder );
 
 		metadata = (MetadataImplementor) metadataBuilder.build();
 		applyCacheSettings( metadata );
 		afterMetadataBuilt( metadata );
 
 		final SessionFactoryBuilder sfb = metadata.getSessionFactoryBuilder();
 		initialize( sfb, metadata );
 		configureSessionFactoryBuilder( sfb );
 
 		sessionFactory = (SessionFactoryImplementor) sfb.build();
 		afterSessionFactoryBuilt( sessionFactory );
 	}
 
 	protected final StandardServiceRegistryBuilder constructStandardServiceRegistryBuilder() {
 		final BootstrapServiceRegistryBuilder bsrb = new BootstrapServiceRegistryBuilder();
 		// by default we do not share the BootstrapServiceRegistry nor the StandardServiceRegistry,
 		// so we want the BootstrapServiceRegistry to be automatically closed when the
 		// StandardServiceRegistry is closed.
 		bsrb.enableAutoClose();
 		configureBootstrapServiceRegistryBuilder( bsrb );
 
 		final BootstrapServiceRegistry bsr = bsrb.build();
 		afterBootstrapServiceRegistryBuilt( bsr );
 
 		final Map settings = new HashMap();
 		addSettings( settings );
 
 		final StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder( bsr );
 		initialize( ssrb );
 		ssrb.applySettings( settings );
 		configureStandardServiceRegistryBuilder( ssrb );
 		return ssrb;
 	}
 
 	protected void addSettings(Map settings) {
 	}
 
 	/**
 	 * Apply any desired config to the BootstrapServiceRegistryBuilder to be incorporated
 	 * into the built BootstrapServiceRegistry
 	 *
 	 * @param bsrb The BootstrapServiceRegistryBuilder
 	 */
 	@SuppressWarnings({"SpellCheckingInspection", "UnusedParameters"})
 	protected void configureBootstrapServiceRegistryBuilder(BootstrapServiceRegistryBuilder bsrb) {
 	}
 
 	/**
 	 * Hook to allow tests to use the BootstrapServiceRegistry if they wish
 	 *
 	 * @param bsr The BootstrapServiceRegistry
 	 */
 	@SuppressWarnings("UnusedParameters")
 	protected void afterBootstrapServiceRegistryBuilt(BootstrapServiceRegistry bsr) {
 	}
 
 	@SuppressWarnings("SpellCheckingInspection")
 	private void initialize(StandardServiceRegistryBuilder ssrb) {
 		final Dialect dialect = BaseCoreFunctionalTestCase.getDialect();
 
 		ssrb.applySetting( AvailableSettings.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
 		ssrb.applySetting( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			ssrb.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 			final String secondSchemaName = createSecondSchema();
 			if ( StringHelper.isNotEmpty( secondSchemaName ) ) {
 				if ( !H2Dialect.class.isInstance( dialect ) ) {
 					// while it may be true that only H2 supports creation of a second schema via
 					// URL (no idea whether that is accurate), every db should support creation of schemas
 					// via DDL which SchemaExport can create for us.  See how this is used and
 					// whether that usage could not just leverage that capability
 					throw new UnsupportedOperationException( "Only H2 dialect supports creation of second schema." );
 				}
 				Helper.createH2Schema( secondSchemaName, ssrb.getSettings() );
 			}
 		}
 		ssrb.applySetting( AvailableSettings.DIALECT, dialect.getClass().getName() );
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected String createSecondSchema() {
 		// poorly named, yes, but to keep migration easy for existing BaseCoreFunctionalTestCase
 		// impls I kept the same name from there
 		return null;
 	}
 
 	/**
 	 * Apply any desired config to the StandardServiceRegistryBuilder to be incorporated
 	 * into the built StandardServiceRegistry
 	 *
 	 * @param ssrb The StandardServiceRegistryBuilder
 	 */
 	@SuppressWarnings({"SpellCheckingInspection", "UnusedParameters"})
 	protected void configureStandardServiceRegistryBuilder(StandardServiceRegistryBuilder ssrb) {
 	}
 
 	/**
 	 * Hook to allow tests to use the StandardServiceRegistry if they wish
 	 *
 	 * @param ssr The StandardServiceRegistry
 	 */
 	@SuppressWarnings("UnusedParameters")
 	protected void afterStandardServiceRegistryBuilt(StandardServiceRegistry ssr) {
 	}
 
 	protected void applyMetadataSources(MetadataSources metadataSources) {
 		for ( String mapping : getMappings() ) {
 			metadataSources.addResource( getBaseForMappings() + mapping );
 		}
 
 		for ( Class annotatedClass : getAnnotatedClasses() ) {
 			metadataSources.addAnnotatedClass( annotatedClass );
 		}
 
 		for ( String annotatedPackage : getAnnotatedPackages() ) {
 			metadataSources.addPackage( annotatedPackage );
 		}
 
 		for ( String ormXmlFile : getXmlFiles() ) {
 			metadataSources.addInputStream( Thread.currentThread().getContextClassLoader().getResourceAsStream( ormXmlFile ) );
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class[] NO_CLASSES = new Class[0];
 
 	protected Class[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		return NO_MAPPINGS;
 	}
 
 	protected void afterMetadataSourcesApplied(MetadataSources metadataSources) {
 	}
 
 	private void initialize(MetadataBuilder metadataBuilder) {
-		metadataBuilder.withNewIdentifierGeneratorsEnabled( true );
-		metadataBuilder.with( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
+		metadataBuilder.enableNewIdentifierGeneratorSupport( true );
+		metadataBuilder.applyImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
 	}
 
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected final void applyCacheSettings(Metadata metadata) {
 		if ( !overrideCacheStrategy() ) {
 			return;
 		}
 
 		if ( getCacheConcurrencyStrategy() == null ) {
 			return;
 		}
 
 		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isInherited() ) {
 				continue;
 			}
 
 			boolean hasLob = false;
 
 			final Iterator props = entityBinding.getPropertyClosureIterator();
 			while ( props.hasNext() ) {
 				final Property prop = (Property) props.next();
 				if ( prop.getValue().isSimpleValue() ) {
 					if ( isLob( ( (SimpleValue) prop.getValue() ).getTypeName() ) ) {
 						hasLob = true;
 						break;
 					}
 				}
 			}
 
 			if ( !hasLob ) {
 				( ( RootClass) entityBinding ).setCacheConcurrencyStrategy( getCacheConcurrencyStrategy() );
 			}
 		}
 
 		for ( Collection collectionBinding : metadata.getCollectionBindings() ) {
 			boolean isLob = false;
 
 			if ( collectionBinding.getElement().isSimpleValue() ) {
 				isLob = isLob( ( (SimpleValue) collectionBinding.getElement() ).getTypeName() );
 			}
 
 			if ( !isLob ) {
 				collectionBinding.setCacheConcurrencyStrategy( getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	private boolean isLob(String typeName) {
 		return "blob".equals( typeName )
 				|| "clob".equals( typeName )
 				|| "nclob".equals( typeName )
 				|| Blob.class.getName().equals( typeName )
 				|| Clob.class.getName().equals( typeName )
 				|| NClob.class.getName().equals( typeName )
 				|| BlobType.class.getName().equals( typeName )
 				|| ClobType.class.getName().equals( typeName )
 				|| NClobType.class.getName().equals( typeName );
 	}
 
 	protected void afterMetadataBuilt(Metadata metadata) {
 	}
 
 	private void initialize(SessionFactoryBuilder sfb, Metadata metadata) {
 		// todo : this is where we need to apply cache settings to be like BaseCoreFunctionalTestCase
 		//		it reads the class/collection mappings and creates corresponding
 		//		CacheRegionDescription references.
 		//
 		//		Ultimately I want those to go on MetadataBuilder, and in fact MetadataBuilder
 		//		already defines the needed method.  But for the [pattern used by the
 		//		tests we need this as part of SessionFactoryBuilder
 	}
 
 	protected void configureSessionFactoryBuilder(SessionFactoryBuilder sfb) {
 	}
 
 	protected void afterSessionFactoryBuilt(SessionFactoryImplementor sessionFactory) {
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected void shutDown() {
 		releaseResources();
 	}
 
 	protected void releaseResources() {
 		if ( sessionFactory != null ) {
 			try {
 				sessionFactory.close();
 			}
 			catch (Exception e) {
 				System.err.println( "Unable to release SessionFactory : " + e.getMessage() );
 				e.printStackTrace();
 			}
 		}
 		sessionFactory = null;
 
 		if ( serviceRegistry != null ) {
 			try {
 				StandardServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 			catch (Exception e) {
 				System.err.println( "Unable to release StandardServiceRegistry : " + e.getMessage() );
 				e.printStackTrace();
 			}
 		}
 		serviceRegistry=null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		if ( isCleanupTestDataRequired() ) {
 			cleanupTestData();
 		}
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 
 	}
 
 	protected boolean isCleanupTestDataRequired() { return false; }
 
 	protected void cleanupTestData() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete from java.lang.Object" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = 0;
 					}
 					l = l + 1 ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 }
