diff --git a/buildSrc/src/main/groovy/org/hibernate/build/HibernateBuildPlugin.groovy b/buildSrc/src/main/groovy/org/hibernate/build/HibernateBuildPlugin.groovy
index 7b7ee7a168..3af1931e06 100644
--- a/buildSrc/src/main/groovy/org/hibernate/build/HibernateBuildPlugin.groovy
+++ b/buildSrc/src/main/groovy/org/hibernate/build/HibernateBuildPlugin.groovy
@@ -1,291 +1,293 @@
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
 package org.hibernate.build
 
 import org.gradle.api.GradleException
 import org.gradle.api.JavaVersion
 import org.gradle.api.Plugin
 import org.gradle.api.Project
 import org.gradle.api.plugins.JavaPluginConvention
 import org.gradle.api.publish.PublishingExtension
 import org.gradle.api.publish.maven.MavenPublication
 import org.gradle.api.tasks.SourceSet
 import org.gradle.api.tasks.compile.JavaCompile
 import org.gradle.api.tasks.testing.Test
 import org.gradle.internal.jvm.Jvm
 
 import org.hibernate.build.gradle.animalsniffer.AnimalSnifferExtension
 
 /**
  * @author Steve Ebersole
  */
 class HibernateBuildPlugin implements Plugin<Project> {
 	@Override
 	void apply(Project project) {
 		if ( !JavaVersion.current().java8Compatible ) {
 			throw new GradleException( "Gradle must be run with Java 8" )
 		}
 
 		project.apply( plugin: 'org.hibernate.build.gradle.animalSniffer' )
 
 		final Jvm java6Home;
 		if ( project.rootProject.extensions.extraProperties.has( 'java6Home' ) ) {
 			java6Home = project.rootProject.extensions.extraProperties.get( 'java6Home' ) as Jvm
 		}
 		else {
 			String java6HomeDirSetting = null;
 			if ( project.hasProperty( "JAVA6_HOME" ) ) {
 				java6HomeDirSetting = project.property( "JAVA6_HOME" ) as String;
 			}
 			if ( java6HomeDirSetting == null ) {
 				java6HomeDirSetting = System.getProperty( "JAVA6_HOME" );
 			}
 
 			if ( java6HomeDirSetting != null ) {
 				project.logger.info( "Using JAVA6_HOME setting [${java6HomeDirSetting}]" )
 
 				final File specifiedJava6Home = project.file( java6HomeDirSetting );
 				if ( specifiedJava6Home == null ) {
 					throw new GradleException( "Could not resolve specified java home ${java6HomeDirSetting}" )
 				}
 				if ( !specifiedJava6Home.exists() ) {
 					throw new GradleException( "Specified java home [${java6HomeDirSetting}] does not exist" )
 				}
 				if ( !specifiedJava6Home.isDirectory() ) {
 					throw new GradleException( "Specified java home [${java6HomeDirSetting}] is not a directory" )
 				}
 
 				java6Home = Jvm.forHome( specifiedJava6Home ) as Jvm;
 
 				if ( java6Home == null ) {
 					throw new GradleException( "Could not resolve JAVA6_HOME [${java6HomeDirSetting}] to proper JAVA_HOME" );
 				}
 
 				project.rootProject.extensions.extraProperties.set( 'java6Home', java6Home )
 			}
 			else {
 				project.logger.warn( "JAVA6_HOME setting not specified, some build features will be disabled" )
 				java6Home = null;
 			}
 		}
 
 		JavaTargetExtension javaTargetExtension = project.extensions.create( "javaTarget", JavaTargetExtension, project )
 		MavenPublishingExtension publishingExtension = project.extensions.create( "mavenPom", MavenPublishingExtension )
 
 		project.afterEvaluate {
 			applyJavaTarget( javaTargetExtension, project, java6Home )
 			applyPublishing( publishingExtension, project )
 		}
 	}
 
 	def applyJavaTarget(JavaTargetExtension javaTargetExtension, Project project, Jvm java6Home) {
 
 		project.logger.info( "Setting target Java version : ${javaTargetExtension.version} (${project.name})" )
 		project.properties.put( 'sourceCompatibility', "${javaTargetExtension.version}" )
 		project.properties.put( 'targetCompatibility', "${javaTargetExtension.version}" )
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// apply AnimalSniffer
 
 		if ( javaTargetExtension.version.java8Compatible ) {
 			AnimalSnifferExtension animalSnifferExtension = project.extensions.findByType( AnimalSnifferExtension )
 			if ( animalSnifferExtension == null ) {
 				throw new GradleException( "Unable to locate AnimalSniffer extension" )
 			}
 			animalSnifferExtension.skip = true
 		}
 		else {
 			// todo : we could really disable this if we set executable/bootClasspath below
 			def sigConfig = project.configurations.animalSnifferSignature
 			sigConfig.incoming.beforeResolve {
 				sigConfig.dependencies.add( project.dependencies.create( 'org.codehaus.mojo.signature:java16:1.0@signature' ) )
 			}
 		}
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Apply to compile task
 
 		project.getConvention().findPlugin( JavaPluginConvention.class ).sourceSets.each { sourceSet ->
 			JavaCompile javaCompileTask = project.tasks.findByName( sourceSet.compileJavaTaskName ) as JavaCompile
 
 			// NOTE : this aptDir stuff is needed until we can have IntelliJ run annotation processors for us
 			//		which cannot happen until we can fold hibernate-testing back into hibernate-core/src/test
 			//		which cannot happen until... ugh
 			File aptDir = project.file( "${project.buildDir}/generated-src/apt/main" )
 			sourceSet.allJava.srcDir( aptDir )
 
 			javaCompileTask.options.compilerArgs += [
 					"-nowarn",
 					"-encoding", "UTF-8",
 					"-s", "${aptDir.absolutePath}"
 			]
 			javaCompileTask.doFirst {
 				aptDir.mkdirs()
 			}
 
 
-			if ( javaTargetExtension.version.java8Compatible ) {
-				javaCompileTask.options.compilerArgs += [
-						"-source", '1.8',
-						"-target", '1.8'
-				]
-			}
-			else {
-				javaCompileTask.options.compilerArgs += [
-						"-source", '1.6',
-						"-target", '1.6'
-				]
-
-				if ( java6Home != null ) {
-					if ( javaTargetExtension.shouldApplyTargetToCompile ) {
-						// Technically we need only one here between:
-						//      1) setting the javac executable
-						//      2) setting the bootClasspath
-						// However, (1) requires fork=true whereas (2) does not.
-//					javaCompileTask.options.fork = true
-//					javaCompileTask.options.forkOptions.executable = java6Home.javacExecutable
-						javaCompileTask.options.bootClasspath = java6Home.runtimeJar.absolutePath
+			if ( sourceSet.name == 'main' ) {
+				if ( javaTargetExtension.version.java8Compatible ) {
+					javaCompileTask.options.compilerArgs += [
+							"-source", '1.8',
+							"-target", '1.8'
+					]
+				}
+				else {
+					javaCompileTask.options.compilerArgs += [
+							"-source", '1.6',
+							"-target", '1.6'
+					]
+
+					if ( java6Home != null ) {
+						if ( javaTargetExtension.shouldApplyTargetToCompile ) {
+							// Technically we need only one here between:
+							//      1) setting the javac executable
+							//      2) setting the bootClasspath
+							// However, (1) requires fork=true whereas (2) does not.
+							//					javaCompileTask.options.fork = true
+							//					javaCompileTask.options.forkOptions.executable = java6Home.javacExecutable
+							javaCompileTask.options.bootClasspath = java6Home.runtimeJar.absolutePath
+						}
 					}
 				}
 			}
 		}
-
-
-		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		// Apply to test compile task
-
-		SourceSet testSourceSet = project.getConvention().findPlugin( JavaPluginConvention.class ).sourceSets.findByName( "test" )
-		JavaCompile compileTestTask = project.tasks.findByName( testSourceSet.compileJavaTaskName ) as JavaCompile
-
-		// NOTE : see the note abovewrt aptDir
-		File testAptDir = project.file( "${project.buildDir}/generated-src/apt/test" )
-		testSourceSet.allJava.srcDir( testAptDir )
-
-		compileTestTask.options.compilerArgs += [
-				"-nowarn",
-				"-encoding", "UTF-8",
-				"-s", "${testAptDir.absolutePath}"
-		]
-		compileTestTask.doFirst {
-			testAptDir.mkdirs()
-		}
+//
+//
+//		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+//		// Apply to test compile task
+//
+//		SourceSet testSourceSet = project.getConvention().findPlugin( JavaPluginConvention.class ).sourceSets.findByName( "test" )
+//		JavaCompile compileTestTask = project.tasks.findByName( testSourceSet.compileJavaTaskName ) as JavaCompile
+//
+//		// NOTE : see the note above wrt aptDir
+//		File testAptDir = project.file( "${project.buildDir}/generated-src/apt/test" )
+//		testSourceSet.allJava.srcDir( testAptDir )
+//
+//		compileTestTask.options.compilerArgs += [
+//				"-nowarn",
+//				"-encoding", "UTF-8",
+//				"-s", "${testAptDir.absolutePath}"
+//		]
+//		compileTestTask.doFirst {
+//			testAptDir.mkdirs()
+//		}
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Apply to test tasks
 
 		project.tasks.withType( Test.class ).all { task->
 			task.jvmArgs += ['-XX:+HeapDumpOnOutOfMemoryError', "-XX:HeapDumpPath=${project.file("${project.buildDir}/OOM-dump.hprof").absolutePath}"]
 
 //			if ( !javaTargetExtension.version.java8Compatible && javaTargetExtension.shouldApplyTargetToTest ) {
 //				// use Java 6 settings
 //				task.executable = java6Home.javaExecutable
 //				task.maxHeapSize = '2G'
 //				task.jvmArgs += ['-XX:MaxPermGen=512M']
 //			}
 //			else {
 				// use Java 8 settings
 				task.maxHeapSize = '2G'
 				task.jvmArgs += ['-XX:MetaspaceSize=512M']
 //			}
 		}
 	}
 
 	def applyPublishing(MavenPublishingExtension publishingExtension, Project project) {
 		PublishingExtension gradlePublishingExtension = project.extensions.getByType( PublishingExtension )
 
 		// repos
 		if ( gradlePublishingExtension.repositories.empty ) {
 			if ( project.version.endsWith( 'SNAPSHOT' ) ) {
 				gradlePublishingExtension.repositories.maven {
 					name 'jboss-snapshots-repository'
 					url 'https://repository.jboss.org/nexus/content/repositories/snapshots'
 				}
 			}
 			else {
 				gradlePublishingExtension.repositories.maven {
 					name 'jboss-releases-repository'
 					url 'https://repository.jboss.org/nexus/service/local/staging/deploy/maven2/'
 				}
 			}
 		}
 
 		// pom
 		gradlePublishingExtension.publications.withType( MavenPublication ).all { pub->
 			final boolean applyExtensionValues = publishingExtension.publications == null || publishingExtension.publications.length == 0 || pub in publishingExtension.publications;
 
 			pom.withXml {
 				if ( applyExtensionValues ) {
 					asNode().appendNode( 'name', publishingExtension.name )
 					asNode().appendNode( 'description', publishingExtension.description )
 					Node licenseNode = asNode().appendNode( "licenses" ).appendNode( "license" )
 					if ( publishingExtension.license == MavenPublishingExtension.License.APACHE2 ) {
 						licenseNode.appendNode( 'name', 'Apache License, Version 2.0' )
 						licenseNode.appendNode( 'url', 'http://www.apache.org/licenses/LICENSE-2.0.txt' )
 					}
 					else {
 						licenseNode.appendNode( 'name', 'GNU Lesser General Public License' )
 						licenseNode.appendNode( 'url', 'http://www.gnu.org/licenses/lgpl-2.1.html' )
 						licenseNode.appendNode( 'comments', 'See discussion at http://hibernate.org/license for more details.' )
 					}
 					licenseNode.appendNode( 'distribution', 'repo' )
 				}
 
 				asNode().children().last() + {
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
 				if ( asNode().dependencies != null ) {
 					asNode().dependencies[0].dependency.each {
 						it.scope[0].value = 'compile'
 					}
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
index 72c2e12cee..cd70d6433e 100644
--- a/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
@@ -1,68 +1,68 @@
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
 
 import java.io.Serializable;
 
 /**
  * Loads an entity by its primary identifier.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
-public interface IdentifierLoadAccess {
+public interface IdentifierLoadAccess<T> {
 	/**
 	 * Specify the {@link LockOptions} to use when retrieving the entity.
 	 *
 	 * @param lockOptions The lock options to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public IdentifierLoadAccess with(LockOptions lockOptions);
+	public IdentifierLoadAccess<T> with(LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance with the given identifier, assuming that the instance exists. This method
 	 * might return a proxied instance that is initialized on-demand, when a non-identifier method is accessed.
 	 *
 	 * You should not use this method to determine if an instance exists; to check for existence, use {@link #load}
 	 * instead.  Use this only to retrieve an instance that you assume exists, where non-existence would be an
 	 * actual error.
 	 *
 	 * @param id The identifier for which to obtain a reference
 	 *
 	 * @return the persistent instance or proxy
 	 */
-	public Object getReference(Serializable id);
+	public T getReference(Serializable id);
 
 	/**
 	 * Return the persistent instance with the given identifier, or null if there is no such persistent instance.
 	 * If the instance is already associated with the session, return that instance, initializing it if needed.  This
 	 * method never returns an uninitialized instance.
 	 *
 	 * @param id The identifier
 	 *
 	 * @return The persistent instance or {@code null}
 	 */
-	public Object load(Serializable id);
+	public T load(Serializable id);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
index e5c1e62356..528b3cf489 100644
--- a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
@@ -1,93 +1,93 @@
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
 
 /**
  * Loads an entity by its natural identifier.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  *
  * @see org.hibernate.annotations.NaturalId
  */
-public interface NaturalIdLoadAccess {
+public interface NaturalIdLoadAccess<T> {
 	/**
 	 * Specify the {@link LockOptions} to use when retrieving the entity.
 	 *
 	 * @param lockOptions The lock options to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public NaturalIdLoadAccess with(LockOptions lockOptions);
+	public NaturalIdLoadAccess<T> with(LockOptions lockOptions);
 
 	/**
 	 * Add a NaturalId attribute value.
 	 * 
 	 * @param attributeName The entity attribute name that is marked as a NaturalId
 	 * @param value The value of the attribute
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public NaturalIdLoadAccess using(String attributeName, Object value);
+	public NaturalIdLoadAccess<T> using(String attributeName, Object value);
 
 	/**
 	 * For entities with mutable natural ids, should Hibernate perform "synchronization" prior to performing
 	 * lookups?  The default is to perform "synchronization" (for correctness).
 	 * <p/>
 	 * "synchronization" here indicates updating the natural-id -> pk cross reference maintained as part of the
 	 * session.  When enabled, prior to performing the lookup, Hibernate will check all entities of the given
 	 * type associated with the session to see if its natural-id values have changed and, if so, update the
 	 * cross reference.  There is a performance impact associated with this, so if application developers are
 	 * certain the natural-ids in play have not changed, this setting can be disabled to circumvent that impact.
 	 * However, disabling this setting when natural-ids values have changed can result in incorrect results!
 	 *
 	 * @param enabled Should synchronization be performed?  {@code true} indicates synchronization will be performed;
 	 * {@code false} indicates it will be circumvented.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public NaturalIdLoadAccess setSynchronizationEnabled(boolean enabled);
+	public NaturalIdLoadAccess<T> setSynchronizationEnabled(boolean enabled);
 
 	/**
 	 * Return the persistent instance with the natural id value(s) defined by the call(s) to {@link #using}.  This
 	 * method might return a proxied instance that is initialized on-demand, when a non-identifier method is accessed.
 	 *
 	 * You should not use this method to determine if an instance exists; to check for existence, use {@link #load}
 	 * instead.  Use this only to retrieve an instance that you assume exists, where non-existence would be an
 	 * actual error.
 	 *
 	 * @return the persistent instance or proxy
 	 */
-	public Object getReference();
+	public T getReference();
 
 	/**
 	 * Return the persistent instance with the natural id value(s) defined by the call(s) to {@link #using}, or
 	 * {@code null} if there is no such persistent instance.  If the instance is already associated with the session,
 	 * return that instance, initializing it if needed.  This method never returns an uninitialized instance.
 	 *
 	 * @return The persistent instance or {@code null} 
 	 */
-	public Object load();
+	public T load();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
index 94c651c7b2..949e5c8be1 100644
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
@@ -1,1107 +1,1115 @@
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
 
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.stat.SessionStatistics;
 
 /**
  * The main runtime interface between a Java application and Hibernate. This is the
  * central API class abstracting the notion of a persistence service.<br>
  * <br>
  * The lifecycle of a <tt>Session</tt> is bounded by the beginning and end of a logical
  * transaction. (Long transactions might span several database transactions.)<br>
  * <br>
  * The main function of the <tt>Session</tt> is to offer create, read and delete operations
  * for instances of mapped entity classes. Instances may exist in one of three states:<br>
  * <br>
  * <i>transient:</i> never persistent, not associated with any <tt>Session</tt><br>
  * <i>persistent:</i> associated with a unique <tt>Session</tt><br>
  * <i>detached:</i> previously persistent, not associated with any <tt>Session</tt><br>
  * <br>
  * Transient instances may be made persistent by calling <tt>save()</tt>,
  * <tt>persist()</tt> or <tt>saveOrUpdate()</tt>. Persistent instances may be made transient
  * by calling<tt> delete()</tt>. Any instance returned by a <tt>get()</tt> or
  * <tt>load()</tt> method is persistent. Detached instances may be made persistent
  * by calling <tt>update()</tt>, <tt>saveOrUpdate()</tt>, <tt>lock()</tt> or <tt>replicate()</tt>. 
  * The state of a transient or detached instance may also be made persistent as a new
  * persistent instance by calling <tt>merge()</tt>.<br>
  * <br>
  * <tt>save()</tt> and <tt>persist()</tt> result in an SQL <tt>INSERT</tt>, <tt>delete()</tt>
  * in an SQL <tt>DELETE</tt> and <tt>update()</tt> or <tt>merge()</tt> in an SQL <tt>UPDATE</tt>. 
  * Changes to <i>persistent</i> instances are detected at flush time and also result in an SQL
  * <tt>UPDATE</tt>. <tt>saveOrUpdate()</tt> and <tt>replicate()</tt> result in either an
  * <tt>INSERT</tt> or an <tt>UPDATE</tt>.<br>
  * <br>
  * It is not intended that implementors be threadsafe. Instead each thread/transaction
  * should obtain its own instance from a <tt>SessionFactory</tt>.<br>
  * <br>
  * A <tt>Session</tt> instance is serializable if its persistent classes are serializable.<br>
  * <br>
  * A typical transaction should use the following idiom:
  * <pre>
  * Session sess = factory.openSession();
  * Transaction tx;
  * try {
  *     tx = sess.beginTransaction();
  *     //do some work
  *     ...
  *     tx.commit();
  * }
  * catch (Exception e) {
  *     if (tx!=null) tx.rollback();
  *     throw e;
  * }
  * finally {
  *     sess.close();
  * }
  * </pre>
  * <br>
  * If the <tt>Session</tt> throws an exception, the transaction must be rolled back
  * and the session discarded. The internal state of the <tt>Session</tt> might not
  * be consistent with the database after the exception occurs.
  *
  * @see SessionFactory
  * @author Gavin King
  */
 public interface Session extends SharedSessionContract, java.io.Closeable {
 	/**
 	 * Obtain a {@link Session} builder with the ability to grab certain information from this session.
 	 *
 	 * @return The session builder
 	 */
 	public SharedSessionBuilder sessionWithOptions();
 
 	/**
 	 * Force this session to flush. Must be called at the end of a
 	 * unit of work, before committing the transaction and closing the
 	 * session (depending on {@link #setFlushMode(FlushMode)},
 	 * {@link Transaction#commit()} calls this method).
 	 * <p/>
 	 * <i>Flushing</i> is the process of synchronizing the underlying persistent
 	 * store with persistable state held in memory.
 	 *
 	 * @throws HibernateException Indicates problems flushing the session or
 	 * talking to the database.
 	 */
 	public void flush() throws HibernateException;
 
 	/**
 	 * Set the flush mode for this session.
 	 * <p/>
 	 * The flush mode determines the points at which the session is flushed.
 	 * <i>Flushing</i> is the process of synchronizing the underlying persistent
 	 * store with persistable state held in memory.
 	 * <p/>
 	 * For a logically "read only" session, it is reasonable to set the session's
 	 * flush mode to {@link FlushMode#MANUAL} at the start of the session (in
 	 * order to achieve some extra performance).
 	 *
 	 * @param flushMode the new flush mode
 	 * @see FlushMode
 	 */
 	public void setFlushMode(FlushMode flushMode);
 
 	/**
 	 * Get the current flush mode for this session.
 	 *
 	 * @return The flush mode
 	 */
 	public FlushMode getFlushMode();
 
 	/**
 	 * Set the cache mode.
 	 * <p/>
 	 * Cache mode determines the manner in which this session can interact with
 	 * the second level cache.
 	 *
 	 * @param cacheMode The new cache mode.
 	 */
 	public void setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Get the current cache mode.
 	 *
 	 * @return The current cache mode.
 	 */
 	public CacheMode getCacheMode();
 
 	/**
 	 * Get the session factory which created this session.
 	 *
 	 * @return The session factory.
 	 * @see SessionFactory
 	 */
 	public SessionFactory getSessionFactory();
 
 	/**
 	 * End the session by releasing the JDBC connection and cleaning up.  It is
 	 * not strictly necessary to close the session but you must at least
 	 * {@link #disconnect()} it.
 	 *
 	 * @throws HibernateException Indicates problems cleaning up.
 	 */
 	public void close() throws HibernateException;
 
 	/**
 	 * Cancel the execution of the current query.
 	 * <p/>
 	 * This is the sole method on session which may be safely called from
 	 * another thread.
 	 *
 	 * @throws HibernateException There was a problem canceling the query
 	 */
 	public void cancelQuery() throws HibernateException;
 
 	/**
 	 * Check if the session is still open.
 	 *
 	 * @return boolean
 	 */
 	public boolean isOpen();
 
 	/**
 	 * Check if the session is currently connected.
 	 *
 	 * @return boolean
 	 */
 	public boolean isConnected();
 
 	/**
 	 * Does this session contain any changes which must be synchronized with
 	 * the database?  In other words, would any DML operations be executed if
 	 * we flushed this session?
 	 *
 	 * @return True if the session contains pending changes; false otherwise.
 	 * @throws HibernateException could not perform dirtying checking
 	 */
 	public boolean isDirty() throws HibernateException;
 
 	/**
 	 * Will entities and proxies that are loaded into this session be made 
 	 * read-only by default?
 	 *
 	 * To determine the read-only/modifiable setting for a particular entity 
 	 * or proxy:
 	 * @see Session#isReadOnly(Object)
 	 *
 	 * @return true, loaded entities/proxies will be made read-only by default; 
 	 *         false, loaded entities/proxies will be made modifiable by default. 
 	 */
 	public boolean isDefaultReadOnly();
 
 	/**
 	 * Change the default for entities and proxies loaded into this session
 	 * from modifiable to read-only mode, or from modifiable to read-only mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * To change the read-only/modifiable setting for a particular entity
 	 * or proxy that is already in this session:
 	 * @see Session#setReadOnly(Object,boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see Query#setReadOnly(boolean)
 	 *
 	 * @param readOnly true, the default for loaded entities/proxies is read-only;
 	 *                 false, the default for loaded entities/proxies is modifiable
 	 */
 	public void setDefaultReadOnly(boolean readOnly);
 
 	/**
 	 * Return the identifier value of the given entity as associated with this
 	 * session.  An exception is thrown if the given entity instance is transient
 	 * or detached in relation to this session.
 	 *
 	 * @param object a persistent instance
 	 * @return the identifier
 	 * @throws TransientObjectException if the instance is transient or associated with
 	 * a different session
 	 */
 	public Serializable getIdentifier(Object object);
 
 	/**
 	 * Check if this instance is associated with this <tt>Session</tt>.
 	 *
 	 * @param object an instance of a persistent class
 	 * @return true if the given instance is associated with this <tt>Session</tt>
 	 */
 	public boolean contains(Object object);
 
 	/**
 	 * Remove this instance from the session cache. Changes to the instance will
 	 * not be synchronized with the database. This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="evict"</tt>.
 	 *
 	 * @param object The entity to evict
 	 *
 	 * @throws NullPointerException if the passed object is {@code null}
 	 * @throws IllegalArgumentException if the passed object is not defined as an entity
 	 */
 	public void evict(Object object);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
+	 * <p/>
+	 * Convenient form of {@link #load(Class, Serializable, LockOptions)}
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
-	 * @deprecated LockMode parameter should be replaced with LockOptions
+	 * @see #load(Class, Serializable, LockOptions)
 	 */
-	@Deprecated
-	public Object load(Class theClass, Serializable id, LockMode lockMode);
+	public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
 	 */
-	public Object load(Class theClass, Serializable id, LockOptions lockOptions);
+	public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
+	 * <p/>
+	 * Convenient form of {@link #load(String, Serializable, LockOptions)}
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
-	 * @deprecated LockMode parameter should be replaced with LockOptions
+	 * @see #load(String, Serializable, LockOptions)
 	 */
-	@Deprecated
 	public Object load(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * assuming that the instance exists. This method might return a proxied instance that
 	 * is initialized on-demand, when a non-identifier method is accessed.
 	 * <br><br>
 	 * You should not use this method to determine if an instance exists (use <tt>get()</tt>
 	 * instead). Use this only to retrieve an instance that you assume exists, where non-existence
 	 * would be an actual error.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 *
 	 * @return the persistent instance or proxy
 	 */
-	public Object load(Class theClass, Serializable id);
+	public <T> T load(Class<T> theClass, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * assuming that the instance exists. This method might return a proxied instance that
 	 * is initialized on-demand, when a non-identifier method is accessed.
 	 * <br><br>
 	 * You should not use this method to determine if an instance exists (use <tt>get()</tt>
 	 * instead). Use this only to retrieve an instance that you assume exists, where non-existence
 	 * would be an actual error.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(String entityName, Serializable id);
 
 	/**
 	 * Read the persistent state associated with the given identifier into the given transient
 	 * instance.
 	 *
 	 * @param object an "empty" instance of the persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 */
 	public void load(Object object, Serializable id);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	public void replicate(Object object, ReplicationMode replicationMode);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) ;
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.) This operation cascades to associated instances if the
 	 * association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a transient instance of a persistent class
 	 *
 	 * @return the generated identifier
 	 */
 	public Serializable save(Object object);
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.)  This operation cascades to associated instances if the
 	 * association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient instance of a persistent class
 	 *
 	 * @return the generated identifier
 	 */
 	public Serializable save(String entityName, Object object);
 
 	/**
 	 * Either {@link #save(Object)} or {@link #update(Object)} the given
 	 * instance, depending upon resolution of the unsaved-value checks (see the
 	 * manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="save-update"}
 	 *
 	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(java.lang.Object)
 	 * @see Session#update(Object object)
 	 */
 	public void saveOrUpdate(Object object);
 
 	/**
 	 * Either {@link #save(String, Object)} or {@link #update(String, Object)}
 	 * the given instance, depending upon resolution of the unsaved-value checks
 	 * (see the manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(String,Object)
 	 * @see Session#update(String,Object)
 	 */
 	public void saveOrUpdate(String entityName, Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a detached instance containing updated state
 	 */
 	public void update(Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance containing updated state
 	 */
 	public void update(String entityName, Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="merge"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a detached instance with state to be copied
 	 *
 	 * @return an updated persistent instance
 	 */
 	public Object merge(Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="merge"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance with state to be copied
 	 *
 	 * @return an updated persistent instance
 	 */
 	public Object merge(String entityName, Object object);
 
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(Object object);
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(String entityName, Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="delete"}
 	 *
 	 * @param object the instance to be removed
 	 */
 	public void delete(Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The <b>object</b> argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="delete"}
 	 *
 	 * @param entityName The entity name for the instance to be removed.
 	 * @param object the instance to be removed
 	 */
 	public void delete(String entityName, Object object);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
+	 * <p/>
+	 * Convenient form of {@link LockRequest#lock(Object)} via {@link #buildLockRequest(LockOptions)}
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
-	 * @deprecated instead call buildLockRequest(LockMode).lock(object)
+	 * @see #buildLockRequest(LockOptions)
+	 * @see LockRequest#lock(Object)
 	 */
-	@Deprecated
 	public void lock(Object object, LockMode lockMode);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.OPTIMISTIC</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
+	 * <p/>
+	 * Convenient form of {@link LockRequest#lock(String, Object)} via {@link #buildLockRequest(LockOptions)}
 	 *
 	 * @param entityName The name of the entity
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
-	 * @deprecated instead call buildLockRequest(LockMode).lock(entityName, object)
+	 * @see #buildLockRequest(LockOptions)
+	 * @see LockRequest#lock(String, Object)
 	 */
-	@SuppressWarnings( {"JavaDoc"})
-	@Deprecated
 	public void lock(String entityName, Object object, LockMode lockMode);
 
 	/**
 	 * Build a LockRequest that specifies the LockMode, pessimistic lock timeout and lock scope.
 	 * timeout and scope is ignored for optimistic locking.  After building the LockRequest,
 	 * call LockRequest.lock to perform the requested locking. 
 	 * <p/>
 	 * Example usage:
 	 * {@code session.buildLockRequest().setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(60000).lock(entity);}
 	 *
 	 * @param lockOptions contains the lock level
 	 *
 	 * @return a lockRequest that can be used to lock the passed object.
 	 */
 	public LockRequest buildLockRequest(LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>after executing direct SQL (eg. a mass update) in the same session
 	 * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param object a persistent or detached instance
 	 */
 	public void refresh(Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>after executing direct SQL (eg. a mass update) in the same session
 	 * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 */
 	public void refresh(String entityName, Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
+	 * <p/>
+	 * Convenient form of {@link #refresh(Object, LockOptions)}
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockMode the lock mode to use
 	 *
-	 * @deprecated LockMode parameter should be replaced with LockOptions
+	 * @see #refresh(Object, LockOptions)
 	 */
-	@Deprecated
 	public void refresh(Object object, LockMode lockMode);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 */
 	public void refresh(Object object, LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 */
 	public void refresh(String entityName, Object object, LockOptions lockOptions);
 
 	/**
 	 * Determine the current lock mode of the given object.
 	 *
 	 * @param object a persistent instance
 	 *
 	 * @return the current lock mode
 	 */
 	public LockMode getCurrentLockMode(Object object);
 
 	/**
 	 * Create a {@link Query} instance for the given collection and filter string.  Contains an implicit {@code FROM}
 	 * element named {@code this} which refers to the defined table for the collection elements, as well as an implicit
 	 * {@code WHERE} restriction for this particular collection instance's key value.
 	 *
 	 * @param collection a persistent collection
 	 * @param queryString a Hibernate query fragment.
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query createFilter(Object collection, String queryString);
 
 	/**
 	 * Completely clear the session. Evict all loaded instances and cancel all pending
 	 * saves, updates and deletions. Do not close open iterators or instances of
 	 * <tt>ScrollableResults</tt>.
 	 */
 	public void clear();
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
-	 * @param clazz a persistent class
+	 * @param entityType The entity type
 	 * @param id an identifier
 	 *
 	 * @return a persistent instance or null
 	 */
-	public Object get(Class clazz, Serializable id);
+	public <T> T get(Class<T> entityType, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
+	 * <p/>
+	 * Convenient form of {@link #get(Class, Serializable, LockOptions)}
 	 *
-	 * @param clazz a persistent class
+	 * @param entityType The entity type
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
-	 * @deprecated LockMode parameter should be replaced with LockOptions
+	 * @see #get(Class, Serializable, LockOptions)
 	 */
-	@Deprecated
-	public Object get(Class clazz, Serializable id, LockMode lockMode);
+	public <T> T get(Class<T> entityType, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
-	 * @param clazz a persistent class
+	 * @param entityType The entity type
 	 * @param id an identifier
 	 * @param lockOptions the lock mode
 	 *
 	 * @return a persistent instance or null
 	 */
-	public Object get(Class clazz, Serializable id, LockOptions lockOptions);
+	public <T> T get(Class<T> entityType, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given named entity with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(String entityName, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
+	 * <p/>
+	 * Convenient form of {@link #get(String, Serializable, LockOptions)}
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
-	 * @deprecated LockMode parameter should be replaced with LockOptions
+	 * @see #get(String, Serializable, LockOptions)
 	 */
-	@Deprecated
 	public Object get(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockOptions contains the lock mode
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the entity name for a persistent entity.
 	 *   
 	 * @param object a persistent entity
 	 *
 	 * @return the entity name
 	 */
 	public String getEntityName(Object object);
 	
 	/**
 	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity type by
 	 * primary key.
 	 * 
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key
 	 *
 	 * @throws HibernateException If the specified entity name cannot be resolved as an entity name
 	 */
 	public IdentifierLoadAccess byId(String entityName);
 
 	/**
 	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity by
 	 * primary key.
 	 *
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key
 	 *
 	 * @throws HibernateException If the specified Class cannot be resolved as a mapped entity
 	 */
-	public IdentifierLoadAccess byId(Class entityClass);
+	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass);
 
 	/**
 	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 * 
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entity name cannot be resolved as an entity name
 	 */
 	public NaturalIdLoadAccess byNaturalId(String entityName);
 
 	/**
 	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 * 
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified Class cannot be resolved as a mapped entity
 	 */
-	public NaturalIdLoadAccess byNaturalId(Class entityClass);
+	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass);
 
 	/**
 	 * Create an {@link SimpleNaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 *
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entityClass cannot be resolved as a mapped entity, or if the
 	 * entity does not define a natural-id or if its natural-id is made up of multiple attributes.
 	 */
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName);
 
 	/**
 	 * Create an {@link SimpleNaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its simple (single attribute) natural id.
 	 *
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entityClass cannot be resolved as a mapped entity, or if the
 	 * entity does not define a natural-id or if its natural-id is made up of multiple attributes.
 	 */
-	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass);
+	public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass);
 
 	/**
 	 * Enable the named filter for this current session.
 	 *
 	 * @param filterName The name of the filter to be enabled.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter enableFilter(String filterName);
 
 	/**
 	 * Retrieve a currently enabled filter by name.
 	 *
 	 * @param filterName The name of the filter to be retrieved.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter getEnabledFilter(String filterName);
 
 	/**
 	 * Disable the named filter for the current session.
 	 *
 	 * @param filterName The name of the filter to be disabled.
 	 */
 	public void disableFilter(String filterName);
 	
 	/**
 	 * Get the statistics for this session.
 	 *
 	 * @return The session statistics being collected for this session
 	 */
 	public SessionStatistics getStatistics();
 
 	/**
 	 * Is the specified entity or proxy read-only?
 	 *
 	 * To get the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#isDefaultReadOnly()
 	 *
 	 * @param entityOrProxy an entity or HibernateProxy
 	 * @return {@code true} if the entity or proxy is read-only, {@code false} if the entity or proxy is modifiable.
 	 */
 	public boolean isReadOnly(Object entityOrProxy);
 
 	/**
 	 * Set an unmodified persistent object to read-only mode, or a read-only
 	 * object to modifiable mode. In read-only mode, no snapshot is maintained,
 	 * the instance is never dirty checked, and changes are not persisted.
 	 *
 	 * If the entity or proxy already has the specified read-only/modifiable
 	 * setting, then this method does nothing.
 	 * 
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see Query#setReadOnly(boolean)
 	 * 
 	 * @param entityOrProxy an entity or HibernateProxy
 	 * @param readOnly {@code true} if the entity or proxy should be made read-only; {@code false} if the entity or
 	 * proxy should be made modifiable
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.
 	 *
 	 * @param work The work to be performed.
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public void doWork(Work work) throws HibernateException;
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.  After
 	 * execution returns the result of the {@link ReturningWork#execute} call.
 	 *
 	 * @param work The work to be performed.
 	 * @param <T> The type of the result returned from the work
 	 *
 	 * @return the result from calling {@link ReturningWork#execute}.
 	 *
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException;
 
 	/**
 	 * Disconnect the session from its underlying JDBC connection.  This is intended for use in cases where the
 	 * application has supplied the JDBC connection to the session and which require long-sessions (aka, conversations).
 	 * <p/>
 	 * It is considered an error to call this method on a session which was not opened by supplying the JDBC connection
 	 * and an exception will be thrown.
 	 * <p/>
 	 * For non-user-supplied scenarios, normal transaction management already handles disconnection and reconnection
 	 * automatically.
 	 *
 	 * @return the application-supplied connection or {@code null}
 	 *
 	 * @see #reconnect(Connection)
 	 */
 	Connection disconnect();
 
 	/**
 	 * Reconnect to the given JDBC connection.
 	 *
 	 * @param connection a JDBC connection
 	 * 
 	 * @see #disconnect()
 	 */
 	void reconnect(Connection connection);
 
 	/**
 	 * Is a particular fetch profile enabled on this session?
 	 *
 	 * @param name The name of the profile to be checked.
 	 * @return True if fetch profile is enabled; false if not.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException;
 
 	/**
 	 * Enable a particular fetch profile on this session.  No-op if requested
 	 * profile is already enabled.
 	 *
 	 * @param name The name of the fetch profile to be enabled.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public void enableFetchProfile(String name) throws UnknownProfileException;
 
 	/**
 	 * Disable a particular fetch profile on this session.  No-op if requested
 	 * profile is already disabled.
 	 *
 	 * @param name The name of the fetch profile to be disabled.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public void disableFetchProfile(String name) throws UnknownProfileException;
 
 	/**
 	 * Convenience access to the {@link TypeHelper} associated with this session's {@link SessionFactory}.
 	 * <p/>
 	 * Equivalent to calling {@link #getSessionFactory()}.{@link SessionFactory#getTypeHelper getTypeHelper()}
 	 *
 	 * @return The {@link TypeHelper} associated with this session's {@link SessionFactory}
 	 */
 	public TypeHelper getTypeHelper();
 
 	/**
 	 * Retrieve this session's helper/delegate for creating LOB instances.
 	 *
 	 * @return This session's LOB helper
 	 */
 	public LobHelper getLobHelper();
 
 	/**
 	 * Contains locking details (LockMode, Timeout and Scope).
 	 */
 	public interface LockRequest {
 		/**
 		 * Constant usable as a time out value that indicates no wait semantics should be used in
 		 * attempting to acquire locks.
 		 */
 		static final int PESSIMISTIC_NO_WAIT = 0;
 		/**
 		 * Constant usable as a time out value that indicates that attempting to acquire locks should be allowed to
 		 * wait forever (apply no timeout).
 		 */
 		static final int PESSIMISTIC_WAIT_FOREVER = -1;
 
 		/**
 		 * Get the lock mode.
 		 *
 		 * @return the lock mode.
 		 */
 		LockMode getLockMode();
 
 		/**
 		 * Specify the LockMode to be used.  The default is LockMode.none.
 		 *
 		 * @param lockMode The lock mode to use for this request
 		 *
 		 * @return this LockRequest instance for operation chaining.
 		 */
 		LockRequest setLockMode(LockMode lockMode);
 
 		/**
 		 * Get the timeout setting.
 		 *
 		 * @return timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 		 */
 		int getTimeOut();
 
 		/**
 		 * Specify the pessimistic lock timeout (check if your dialect supports this option).
 		 * The default pessimistic lock behavior is to wait forever for the lock.
 		 *
 		 * @param timeout is time in milliseconds to wait for lock.  -1 means wait forever and 0 means no wait.
 		 *
 		 * @return this LockRequest instance for operation chaining.
 		 */
 		LockRequest setTimeOut(int timeout);
 
 		/**
 		 * Check if locking is cascaded to owned collections and relationships.
 		 *
 		 * @return true if locking will be extended to owned collections and relationships.
 		 */
 		boolean getScope();
 
 		/**
 		 * Specify if LockMode should be cascaded to owned collections and relationships.
 		 * The association must be mapped with {@code cascade="lock"} for scope=true to work.
 		 *
 		 * @param scope {@code true} to cascade locks; {@code false} to not.
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		LockRequest setScope(boolean scope);
 
 		/**
 		 * Perform the requested locking.
 		 *
 		 * @param entityName The name of the entity to lock
 		 * @param object The instance of the entity to lock
 		 */
 		void lock(String entityName, Object object);
 
 		/**
 		 * Perform the requested locking.
 		 *
 		 * @param object The instance of the entity to lock
 		 */
 		void lock(Object object);
 	}
 
 	/**
 	 * Add one or more listeners to the Session
 	 *
 	 * @param listeners The listener(s) to add
 	 */
 	public void addEventListeners(SessionEventListener... listeners);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
index 3cc6d544a1..fd12ac8d52 100644
--- a/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
@@ -1,83 +1,83 @@
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
 
 /**
  * Loads an entity by its natural identifier.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  *
  * @see org.hibernate.annotations.NaturalId
  * @see NaturalIdLoadAccess
  */
-public interface SimpleNaturalIdLoadAccess {
+public interface SimpleNaturalIdLoadAccess<T> {
 	/**
 	 * Specify the {@link org.hibernate.LockOptions} to use when retrieving the entity.
 	 *
 	 * @param lockOptions The lock options to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SimpleNaturalIdLoadAccess with(LockOptions lockOptions);
+	public SimpleNaturalIdLoadAccess<T> with(LockOptions lockOptions);
 
 	/**
 	 * For entities with mutable natural ids, should Hibernate perform "synchronization" prior to performing 
 	 * lookups?  The default is to perform "synchronization" (for correctness).
 	 * <p/>
 	 * See {@link NaturalIdLoadAccess#setSynchronizationEnabled} for detailed discussion.
 	 *
 	 * @param enabled Should synchronization be performed?  {@code true} indicates synchronization will be performed;
 	 * {@code false} indicates it will be circumvented.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SimpleNaturalIdLoadAccess setSynchronizationEnabled(boolean enabled);
+	public SimpleNaturalIdLoadAccess<T> setSynchronizationEnabled(boolean enabled);
 
 	/**
 	 * Return the persistent instance with the given natural id value, assuming that the instance exists. This method
 	 * might return a proxied instance that is initialized on-demand, when a non-identifier method is accessed.
 	 *
 	 * You should not use this method to determine if an instance exists; to check for existence, use {@link #load}
 	 * instead.  Use this only to retrieve an instance that you assume exists, where non-existence would be an
 	 * actual error.
 	 *
 	 * @param naturalIdValue The value of the natural-id for the entity to retrieve
 	 *
 	 * @return The persistent instance or proxy, if an instance exists.  Otherwise, {@code null}.
 	 */
-	public Object getReference(Object naturalIdValue);
+	public T getReference(Object naturalIdValue);
 
 	/**
 	 * Return the persistent instance with the given natural id value, or {@code null} if there is no such persistent
 	 * instance.  If the instance is already associated with the session, return that instance, initializing it if
 	 * needed.  This method never returns an uninitialized instance.
 	 *
 	 * @param naturalIdValue The value of the natural-id for the entity to retrieve
 	 * 
 	 * @return The persistent instance or {@code null}
 	 */
-	public Object load(Object naturalIdValue);
+	public T load(Object naturalIdValue);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index 5091a751af..5ed74799ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,400 +1,400 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Represents state associated with the processing of a given {@link ResultSet}
  * in regards to loading collections.
  * <p/>
  * Another implementation option to consider is to not expose {@link ResultSet}s
  * directly (in the JDBC redesign) but to always "wrap" them and apply a
  * [series of] context[s] to that wrapper.
  *
  * @author Steve Ebersole
  */
 public class CollectionLoadContext {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( CollectionLoadContext.class );
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set<CollectionKey> localLoadingCollectionKeys = new HashSet<CollectionKey>();
 
 	/**
 	 * Creates a collection load context for the given result set.
 	 *
 	 * @param loadContexts Callback to other collection load contexts.
 	 * @param resultSet The result set this is "wrapping".
 	 */
 	public CollectionLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
 		this.loadContexts = loadContexts;
 		this.resultSet = resultSet;
 	}
 
 	public ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	public LoadContexts getLoadContext() {
 		return loadContexts;
 	}
 
 	/**
 	 * Retrieve the collection that is being loaded as part of processing this
 	 * result set.
 	 * <p/>
 	 * Basically, there are two valid return values from this method:<ul>
 	 * <li>an instance of {@link org.hibernate.collection.spi.PersistentCollection} which indicates to
 	 * continue loading the result set row data into that returned collection
 	 * instance; this may be either an instance already associated and in the
 	 * midst of being loaded, or a newly instantiated instance as a matching
 	 * associated collection was not found.</li>
 	 * <li><i>null</i> indicates to ignore the corresponding result set row
 	 * data relating to the requested collection; this indicates that either
 	 * the collection was found to already be associated with the persistence
 	 * context in a fully loaded state, or it was found in a loading state
 	 * associated with another result set processing context.</li>
 	 * </ul>
 	 *
 	 * @param persister The persister for the collection being requested.
 	 * @param key The key of the collection being requested.
 	 *
 	 * @return The loading collection (see discussion above).
 	 */
 	public PersistentCollection getLoadingCollection(final CollectionPersister persister, final Serializable key) {
 		final EntityMode em = persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode();
 		final CollectionKey collectionKey = new CollectionKey( persister, key, em );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Starting attempt to find loading collection [{0}]",
 					MessageHelper.collectionInfoString( persister.getRole(), key ) );
 		}
 		final LoadingCollectionEntry loadingCollectionEntry = loadContexts.locateLoadingCollectionEntry( collectionKey );
 		if ( loadingCollectionEntry == null ) {
 			// look for existing collection as part of the persistence context
 			PersistentCollection collection = loadContexts.getPersistenceContext().getCollection( collectionKey );
 			if ( collection != null ) {
 				if ( collection.wasInitialized() ) {
 					LOG.trace( "Collection already initialized; ignoring" );
 					// ignore this row of results! Note the early exit
 					return null;
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				final Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
 				final boolean newlySavedEntity = owner != null
 						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING;
 				if ( newlySavedEntity ) {
 					// important, to account for newly saved entities in query
 					// todo : some kind of check for new status...
 					LOG.trace( "Owning entity already loaded; ignoring" );
 					return null;
 				}
 				// create one
 				LOG.tracev( "Instantiating new collection [key={0}, rs={1}]", key, resultSet );
 				collection = persister.getCollectionType().instantiate(
 						loadContexts.getPersistenceContext().getSession(), persister, key );
 			}
 			collection.beforeInitialize( persister, -1 );
 			collection.beginRead();
 			localLoadingCollectionKeys.add( collectionKey );
 			loadContexts.registerLoadingCollectionXRef( collectionKey, new LoadingCollectionEntry( resultSet, persister, key, collection ) );
 			return collection;
 		}
 		if ( loadingCollectionEntry.getResultSet() == resultSet ) {
 			LOG.trace( "Found loading collection bound to current result set processing; reading row" );
 			return loadingCollectionEntry.getCollection();
 		}
 		// ignore this row, the collection is in process of
 		// being loaded somewhere further "up" the stack
 		LOG.trace( "Collection is already being initialized; ignoring row" );
 		return null;
 	}
 
 	/**
 	 * Finish the process of collection-loading for this bound result set.  Mainly this
 	 * involves cleaning up resources and notifying the collections that loading is
 	 * complete.
 	 *
 	 * @param persister The persister for which to complete loading.
 	 */
 	public void endLoadingCollections(CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		if ( !loadContexts.hasLoadingCollectionEntries()
 				&& localLoadingCollectionKeys.isEmpty() ) {
 			return;
 		}
 
 		// in an effort to avoid concurrent-modification-exceptions (from
 		// potential recursive calls back through here as a result of the
 		// eventual call to PersistentCollection#endRead), we scan the
 		// internal loadingCollections map for matches and store those matches
 		// in a temp collection.  the temp collection is then used to "drive"
 		// the #endRead processing.
 		List<LoadingCollectionEntry> matches = null;
 		final Iterator itr = localLoadingCollectionKeys.iterator();
 		while ( itr.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) itr.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList<LoadingCollectionEntry>();
 				}
 				matches.add( lce );
 				if ( lce.getCollection().getOwner() == null ) {
 					session.getPersistenceContext().addUnownedCollection(
 							new CollectionKey(
 									persister,
 									lce.getKey(),
 									persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode()
 							),
 							lce.getCollection()
 					);
 				}
 				LOG.tracev( "Removing collection load entry [{0}]", lce );
 
 				// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister,LoadingCollectionEntry)...
 				loadContexts.unregisterLoadingCollectionXRef( collectionKey );
 				itr.remove();
 			}
 		}
 
 		endLoadingCollections( persister, matches );
 		if ( localLoadingCollectionKeys.isEmpty() ) {
 			// todo : hack!!!
 			// NOTE : here we cleanup the load context when we have no more local
 			// LCE entries.  This "works" for the time being because really
 			// only the collection load contexts are implemented.  Long term,
 			// this cleanup should become part of the "close result set"
 			// processing from the (sandbox/jdbc) jdbc-container code.
 			loadContexts.cleanup( resultSet );
 		}
 	}
 
 	private void endLoadingCollections(CollectionPersister persister, List<LoadingCollectionEntry> matchedCollectionEntries) {
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( matchedCollectionEntries == null ) {
 			if ( debugEnabled ) {
 				LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			}
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections were found in result set for role: %s", count, persister.getRole() );
 		}
 
 		for ( LoadingCollectionEntry matchedCollectionEntry : matchedCollectionEntries ) {
 			endLoadingCollection( matchedCollectionEntry, persister );
 		}
 
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 		}
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		// warning: can cause a recursive calls! (proxy initialization)
 		final boolean hasNoQueuedAdds = lce.getCollection().endRead();
 
 		if ( persister.getCollectionType().hasHolder() ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 //			if (ce.getLoadedPersister().getBatchSize() > 1) { // not the best place for doing this, moved into ce.postInitialize
 //				getLoadContext().getPersistenceContext().getBatchFetchQueue().removeBatchLoadableCollection(ce); 
 //			}
 		}
 
 
 		// add to cache if:
 		boolean addToCache =
 				// there were no queued additions
 				hasNoQueuedAdds
 				// and the role has a cache
 				&& persister.hasCache()
 				// and this is not a forced initialization during flush
 				&& session.getCacheMode().isPutEnabled() && !ce.isDoremove();
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
 					MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session )
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection( persister.getRole() );
 		}
 	}
 
 	/**
 	 * Add the collection to the second-level cache
 	 *
 	 * @param lce The entry representing the collection to add
 	 * @param persister The persister
 	 */
 	private void addCollectionToCache(LoadingCollectionEntry lce, CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) );
 		}
 
-		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
+		if ( !session.getLoadQueryInfluencers().getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
 			if ( debugEnabled ) {
 				LOG.debug( "Refusing to add to cache due to enabled filters" );
 			}
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			// EARLY EXIT!!!!!
 			return;
 		}
 
 		final Object version;
 		if ( persister.isVersioned() ) {
 			Object collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( lce.getKey(), persister );
 			if ( collectionOwner == null ) {
 				// generally speaking this would be caused by the collection key being defined by a property-ref, thus
 				// the collection key and the owner key would not match up.  In this case, try to use the key of the
 				// owner instance associated with the collection itself, if one.  If the collection does already know
 				// about its owner, that owner should be the same instance as associated with the PC, but we do the
 				// resolution against the PC anyway just to be safe since the lookup should not be costly.
 				if ( lce.getCollection() != null ) {
 					final Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		final CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		final CacheKey cacheKey = session.generateCacheKey( lce.getKey(), persister.getKeyType(), persister.getRole() );
 
 		boolean isPutFromLoad = true;
 		if ( persister.getElementType().isAssociationType() ) {
 			for ( Serializable id : entry.getState() ) {
 				EntityPersister entityPersister = ( (QueryableCollection) persister ).getElementPersister();
 				if ( session.getPersistenceContext().wasInsertedDuringTransaction( entityPersister, id ) ) {
 					isPutFromLoad = false;
 					break;
 				}
 			}
 		}
 
 		// CollectionRegionAccessStrategy has no update, so avoid putting uncommitted data via putFromLoad
 		if (isPutFromLoad) {
 			try {
 				session.getEventListenerManager().cachePutStart();
 				final boolean put = persister.getCacheAccessStrategy().putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 			finally {
 				session.getEventListenerManager().cachePutEnd();
 			}
 		}
 	}
 
 	void cleanup() {
 		if ( !localLoadingCollectionKeys.isEmpty() ) {
 			LOG.localLoadingCollectionKeysCount( localLoadingCollectionKeys.size() );
 		}
 		loadContexts.cleanupCollectionXRefs( localLoadingCollectionKeys );
 		localLoadingCollectionKeys.clear();
 	}
 
 
 	@Override
 	public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
index 5bb4aa96e4..01a17bd25e 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
@@ -1,819 +1,793 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
-import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Query;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.type.Type;
 
 /**
  * This class is meant to be extended.
  * 
  * Wraps and delegates all methods to a {@link SessionImplementor} and
  * a {@link Session}. This is useful for custom implementations of this
  * API so that only some methods need to be overridden
  * (Used by Hibernate Search).
  * 
  * @author Sanne Grinovero <sanne@hibernate.org> (C) 2012 Red Hat Inc.
  */
 public class SessionDelegatorBaseImpl implements SessionImplementor, Session {
 
 	protected final SessionImplementor sessionImplementor;
 	protected final Session session;
 
 	public SessionDelegatorBaseImpl(SessionImplementor sessionImplementor, Session session) {
 		if ( sessionImplementor == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null sessionImplementor object" );
 		}
 		if ( session == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null session object" );
 		}
 		this.sessionImplementor = sessionImplementor;
 		this.session = session;
 	}
 
 	// Delegates to SessionImplementor
 
 	@Override
 	public <T> T execute(Callback<T> callback) {
 		return sessionImplementor.execute( callback );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return sessionImplementor.getTenantIdentifier();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return sessionImplementor.getJdbcConnectionAccess();
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return sessionImplementor.generateEntityKey( id, persister );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return sessionImplementor.generateCacheKey( id, type, entityOrRoleName );
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return sessionImplementor.getInterceptor();
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		sessionImplementor.setAutoClear( enabled );
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		sessionImplementor.disableTransactionAutoJoin();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return sessionImplementor.isTransactionInProgress();
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
 		sessionImplementor.initializeCollection( collection, writing );
 	}
 
 	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		return sessionImplementor.internalLoad( entityName, id, eager, nullable );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.immediateLoad( entityName, id );
 	}
 
 	@Override
 	public long getTimestamp() {
 		return sessionImplementor.getTimestamp();
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return sessionImplementor.getFactory();
 	}
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( query, queryParameters );
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterate( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		return sessionImplementor.scroll( criteria, scrollMode );
 	}
 
 	@Override
 	public List list(Criteria criteria) {
 		return sessionImplementor.list( criteria );
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterateFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
 		return sessionImplementor.getEntityPersister( entityName, object );
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		return sessionImplementor.getEntityUsingInterceptor( key );
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		return sessionImplementor.getContextEntityIdentifier( object );
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		return sessionImplementor.bestGuessEntityName( object );
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		return sessionImplementor.guessEntityName( entity );
 	}
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.instantiate( entityName, id );
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scrollCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( spec, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( spec, queryParameters );
 	}
 
 	@Override
-	public Object getFilterParameterValue(String filterParameterName) {
-		return sessionImplementor.getFilterParameterValue( filterParameterName );
-	}
-
-	@Override
-	public Type getFilterParameterType(String filterParameterName) {
-		return sessionImplementor.getFilterParameterType( filterParameterName );
-	}
-
-	@Override
-	public Map getEnabledFilters() {
-		return sessionImplementor.getEnabledFilters();
-	}
-
-	@Override
 	public int getDontFlushFromFind() {
 		return sessionImplementor.getDontFlushFromFind();
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return sessionImplementor.getPersistenceContext();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeUpdate( query, queryParameters );
 	}
 
 	@Override
 	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeNativeUpdate( specification, queryParameters );
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return sessionImplementor.getCacheMode();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		sessionImplementor.setCacheMode( cm );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return sessionImplementor.isOpen();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return sessionImplementor.isConnected();
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return sessionImplementor.getFlushMode();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		sessionImplementor.setFlushMode( fm );
 	}
 
 	@Override
 	public Connection connection() {
 		return sessionImplementor.connection();
 	}
 
 	@Override
 	public void flush() {
 		sessionImplementor.flush();
 	}
 
 	@Override
 	public Query getNamedQuery(String name) {
 		return sessionImplementor.getNamedQuery( name );
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String name) {
 		return sessionImplementor.getNamedSQLQuery( name );
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return sessionImplementor.isEventSource();
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		sessionImplementor.afterScrollOperation();
 	}
 
 	@Override
-	public String getFetchProfile() {
-		return sessionImplementor.getFetchProfile();
-	}
-
-	@Override
-	public void setFetchProfile(String name) {
-		sessionImplementor.setFetchProfile( name );
-	}
-
-	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return sessionImplementor.getTransactionCoordinator();
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return sessionImplementor.getJdbcCoordinator();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return sessionImplementor.isClosed();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return sessionImplementor.shouldAutoClose();
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return sessionImplementor.isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return sessionImplementor.getLoadQueryInfluencers();
 	}
 
 	@Override
 	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
 		return sessionImplementor.createQuery( namedQueryDefinition );
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
 		return sessionImplementor.createSQLQuery( namedQueryDefinition );
 	}
 
 	@Override
 	public SessionEventListenerManager getEventListenerManager() {
 		return sessionImplementor.getEventListenerManager();
 	}
 
 	// Delegates to Session
 
 	@Override
 	public Transaction beginTransaction() {
 		return session.beginTransaction();
 	}
 
 	@Override
 	public Transaction getTransaction() {
 		return session.getTransaction();
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		return session.createQuery( queryString );
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String queryString) {
 		return session.createSQLQuery( queryString );
 	}
 
 	@Override
 	public ProcedureCall getNamedProcedureCall(String name) {
 		return session.getNamedProcedureCall( name );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		return session.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		return session.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		return session.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		return session.createCriteria( persistentClass );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		return session.createCriteria( persistentClass, alias );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		return session.createCriteria( entityName );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		return session.createCriteria( entityName, alias );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return session.sessionWithOptions();
 	}
 
 	@Override
 	public SessionFactory getSessionFactory() {
 		return session.getSessionFactory();
 	}
 
 	@Override
 	public void close() throws HibernateException {
 		session.close();
 	}
 
 	@Override
 	public void cancelQuery() throws HibernateException {
 		session.cancelQuery();
 	}
 
 	@Override
 	public boolean isDirty() throws HibernateException {
 		return session.isDirty();
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return session.isDefaultReadOnly();
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean readOnly) {
 		session.setDefaultReadOnly( readOnly );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return session.getIdentifier( object );
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		return session.contains( object );
 	}
 
 	@Override
 	public void evict(Object object) {
 		session.evict( object );
 	}
 
 	@Override
-	public Object load(Class theClass, Serializable id, LockMode lockMode) {
+	public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) {
 		return session.load( theClass, id, lockMode );
 	}
 
 	@Override
-	public Object load(Class theClass, Serializable id, LockOptions lockOptions) {
+	public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) {
 		return session.load( theClass, id, lockOptions );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) {
 		return session.load( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.load( entityName, id, lockOptions );
 	}
 
 	@Override
-	public Object load(Class theClass, Serializable id) {
+	public <T> T load(Class<T> theClass, Serializable id) {
 		return session.load( theClass, id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id) {
 		return session.load( entityName, id );
 	}
 
 	@Override
 	public void load(Object object, Serializable id) {
 		session.load( object, id );
 	}
 
 	@Override
 	public void replicate(Object object, ReplicationMode replicationMode) {
 		session.replicate( object, replicationMode );
 	}
 
 	@Override
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
 		session.replicate( entityName, object, replicationMode );
 	}
 
 	@Override
 	public Serializable save(Object object) {
 		return session.save( object );
 	}
 
 	@Override
 	public Serializable save(String entityName, Object object) {
 		return session.save( entityName, object );
 	}
 
 	@Override
 	public void saveOrUpdate(Object object) {
 		session.saveOrUpdate( object );
 	}
 
 	@Override
 	public void saveOrUpdate(String entityName, Object object) {
 		session.saveOrUpdate( entityName, object );
 	}
 
 	@Override
 	public void update(Object object) {
 		session.update( object );
 	}
 
 	@Override
 	public void update(String entityName, Object object) {
 		session.update( entityName, object );
 	}
 
 	@Override
 	public Object merge(Object object) {
 		return session.merge( object );
 	}
 
 	@Override
 	public Object merge(String entityName, Object object) {
 		return session.merge( entityName, object );
 	}
 
 	@Override
 	public void persist(Object object) {
 		session.persist( object );
 	}
 
 	@Override
 	public void persist(String entityName, Object object) {
 		session.persist( entityName, object );
 	}
 
 	@Override
 	public void delete(Object object) {
 		session.delete( object );
 	}
 
 	@Override
 	public void delete(String entityName, Object object) {
 		session.delete( entityName, object );
 	}
 
 	@Override
 	public void lock(Object object, LockMode lockMode) {
 		session.lock( object, lockMode );
 	}
 
 	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) {
 		session.lock( entityName, object, lockMode );
 	}
 
 	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return session.buildLockRequest( lockOptions );
 	}
 
 	@Override
 	public void refresh(Object object) {
 		session.refresh( object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) {
 		session.refresh( entityName, object );
 	}
 
 	@Override
 	public void refresh(Object object, LockMode lockMode) {
 		session.refresh( object, lockMode );
 	}
 
 	@Override
 	public void refresh(Object object, LockOptions lockOptions) {
 		session.refresh( object, lockOptions );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) {
 		session.refresh( entityName, object, lockOptions );
 	}
 
 	@Override
 	public LockMode getCurrentLockMode(Object object) {
 		return session.getCurrentLockMode( object );
 	}
 
 	@Override
 	public Query createFilter(Object collection, String queryString) {
 		return session.createFilter( collection, queryString );
 	}
 
 	@Override
 	public void clear() {
 		session.clear();
 	}
 
 	@Override
-	public Object get(Class clazz, Serializable id) {
-		return session.get( clazz, id );
+	public <T> T get(Class<T> theClass, Serializable id) {
+		return session.get( theClass, id );
 	}
 
 	@Override
-	public Object get(Class clazz, Serializable id, LockMode lockMode) {
-		return session.get( clazz, id, lockMode );
+	public <T> T get(Class<T> theClass, Serializable id, LockMode lockMode) {
+		return session.get( theClass, id, lockMode );
 	}
 
 	@Override
-	public Object get(Class clazz, Serializable id, LockOptions lockOptions) {
-		return session.get( clazz, id, lockOptions );
+	public <T> T get(Class<T> theClass, Serializable id, LockOptions lockOptions) {
+		return session.get( theClass, id, lockOptions );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return session.get( entityName, id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		return session.get( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.get( entityName, id, lockOptions );
 	}
 
 	@Override
 	public String getEntityName(Object object) {
 		return session.getEntityName( object );
 	}
 
 	@Override
 	public IdentifierLoadAccess byId(String entityName) {
 		return session.byId( entityName );
 	}
 
 	@Override
-	public IdentifierLoadAccess byId(Class entityClass) {
+	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
 		return session.byId( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return session.byNaturalId( entityName );
 	}
 
 	@Override
-	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
+	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
 		return session.byNaturalId( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return session.bySimpleNaturalId( entityName );
 	}
 
 	@Override
-	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
+	public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
 		return session.bySimpleNaturalId( entityClass );
 	}
 
 	@Override
 	public Filter enableFilter(String filterName) {
 		return session.enableFilter( filterName );
 	}
 
 	@Override
 	public Filter getEnabledFilter(String filterName) {
 		return session.getEnabledFilter( filterName );
 	}
 
 	@Override
 	public void disableFilter(String filterName) {
 		session.disableFilter( filterName );
 	}
 
 	@Override
 	public SessionStatistics getStatistics() {
 		return session.getStatistics();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		return session.isReadOnly( entityOrProxy );
 	}
 
 	@Override
 	public void setReadOnly(Object entityOrProxy, boolean readOnly) {
 		session.setReadOnly( entityOrProxy, readOnly );
 	}
 
 	@Override
 	public void doWork(Work work) throws HibernateException {
 		session.doWork( work );
 	}
 
 	@Override
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
 		return session.doReturningWork( work );
 	}
 
 	@Override
 	public Connection disconnect() {
 		return session.disconnect();
 	}
 
 	@Override
 	public void reconnect(Connection connection) {
 		session.reconnect( connection );
 	}
 
 	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return session.isFetchProfileEnabled( name );
 	}
 
 	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		session.enableFetchProfile( name );
 	}
 
 	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		session.disableFetchProfile( name );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return session.getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		return session.getLobHelper();
 	}
 
 	@Override
 	public void addEventListeners(SessionEventListener... listeners) {
 		session.addEventListeners( listeners );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index d4cc0c1012..25ed52c104 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,298 +1,322 @@
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
 package org.hibernate.engine.spi;
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	@Override
 	public SessionBuilderImplementor withOptions();
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	public Map<String,EntityPersister> getEntityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	public Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 *
 	 * @deprecated Access to connections via {@link org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess} should
 	 * be preferred over access via {@link ConnectionProvider}, whenever possible.
 	 * {@link org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess} is tied to the Hibernate Session to
 	 * properly account for contextual information.  See {@link SessionImplementor#getJdbcConnectionAccess()}
 	 */
 	@Deprecated
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	public NamedQueryDefinition getNamedQuery(String queryName);
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 	
 	/**
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getNaturalIdCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 */
 	public SqlExceptionHelper getSQLExceptionHelper();
 
 	/**
 	 * @deprecated Use {@link #getSessionFactoryOptions()} instead
 	 */
 	@Deprecated
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	/**
 	 * Provides access to the named query repository
 	 *
 	 * @return The repository for named query definitions
 	 */
 	public NamedQueryRepository getNamedQueryRepository();
 
 	Iterable<EntityNameResolver> iterateEntityNameResolvers();
+
+	/**
+	 * Locate an EntityPersister by the entity class.  The passed Class might refer to either
+	 * the entity name directly, or it might name a proxy interface for the entity.  This
+	 * method accounts for both, preferring the direct named entity name.
+	 *
+	 * @param byClass The concrete Class or proxy interface for the entity to locate the persister for.
+	 *
+	 * @return The located EntityPersister, never {@code null}
+	 *
+	 * @throws HibernateException If a matching EntityPersister cannot be located
+	 */
+	EntityPersister locateEntityPersister(Class byClass);
+
+	/**
+	 * Locate the entity persister by name.
+	 *
+	 * @param byName The entity name
+	 *
+	 * @return The located EntityPersister, never {@code null}
+	 *
+	 * @throws HibernateException If a matching EntityPersister cannot be located
+	 */
+	EntityPersister locateEntityPersister(String byName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
index cc44f51a19..1ac76a183c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
@@ -1,432 +1,373 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Transaction;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.type.Type;
 
 /**
  * Defines the internal contract between {@link org.hibernate.Session} / {@link org.hibernate.StatelessSession} and
  * other parts of Hibernate such as {@link Type}, {@link EntityPersister} and
  * {@link org.hibernate.persister.collection.CollectionPersister} implementors
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionImplementor extends Serializable, LobCreationContext {
 	/**
 	 * Match te method on {@link org.hibernate.Session} and {@link org.hibernate.StatelessSession}
 	 *
 	 * @return The tenant identifier of this session
 	 */
 	public String getTenantIdentifier();
 
 	/**
 	 * Provides access to JDBC connections
 	 *
 	 * @return The contract for accessing JDBC connections.
 	 */
 	public JdbcConnectionAccess getJdbcConnectionAccess();
 
 	/**
 	 * Hide the changing requirements of entity key creation
 	 *
 	 * @param id The entity id
 	 * @param persister The entity persister
 	 *
 	 * @return The entity key
 	 */
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister);
 
 	/**
 	 * Hide the changing requirements of cache key creation.
 	 *
 	 * @param id The entity identifier or collection key.
 	 * @param type The type
 	 * @param entityOrRoleName The entity name or collection role.
 	 *
 	 * @return The cache key
 	 */
 	public CacheKey generateCacheKey(Serializable id, final Type type, final String entityOrRoleName);
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	public Interceptor getInterceptor();
 
 	/**
 	 * Enable/disable automatic cache clearing from after transaction
 	 * completion (for EJB3)
 	 */
 	public void setAutoClear(boolean enabled);
 
 	/**
 	 * Disable automatic transaction joining.  The really only has any effect for CMT transactions.  The default
 	 * Hibernate behavior is to auto join any active JTA transaction (register {@link javax.transaction.Synchronization}).
 	 * JPA however defines an explicit join transaction operation.
 	 * <p/>
 	 * See javax.persistence.EntityManager#joinTransaction
 	 */
 	public void disableTransactionAutoJoin();
 
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	public boolean isTransactionInProgress();
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 			throws HibernateException;
 
 	/**
 	 * Load an instance without checking if it was deleted.
 	 * <p/>
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 * <p/>
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies
 	 * (but might return an existing proxy); if it does not exist, return
 	 * <tt>null</tt>.
 	 * <p/>
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
 			throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * System time before the start of the transaction
 	 */
 	public long getTimestamp();
 
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	public SessionFactoryImplementor getFactory();
 
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	public List list(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a criteria query
 	 */
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode);
 
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(Criteria criteria);
 
 	/**
 	 * Execute a filter
 	 */
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Iterate a filter
 	 */
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 *
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Return the identifier of the persistent object, or null if
 	 * not associated with the session
 	 */
 	public Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	public String bestGuessEntityName(Object object);
 
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	public String guessEntityName(Object entity) throws HibernateException;
 
 	/**
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	public Object instantiate(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 *
 	 * @return The result list.
 	 *
 	 * @throws HibernateException
 	 */
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 *
 	 * @return The resulting scrollable result.
 	 *
 	 * @throws HibernateException
 	 */
-	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
-			throws HibernateException;
-
-	/**
-	 * Retreive the currently set value for a filter parameter.
-	 *
-	 * @param filterParameterName The filter parameter name in the format
-	 * {FILTER_NAME.PARAMETER_NAME}.
-	 *
-	 * @return The filter parameter value.
-	 *
-	 * @deprecated use #getLoadQueryInfluencers instead
-	 */
-	@Deprecated
-	public Object getFilterParameterValue(String filterParameterName);
-
-	/**
-	 * Retrieve the type for a given filter parameter.
-	 *
-	 * @param filterParameterName The filter parameter name in the format
-	 * {FILTER_NAME.PARAMETER_NAME}.
-	 *
-	 * @return The filter param type
-	 *
-	 * @deprecated use #getLoadQueryInfluencers instead
-	 */
-	@Deprecated
-	public Type getFilterParameterType(String filterParameterName);
-
-	/**
-	 * Return the currently enabled filters.  The filter map is keyed by filter
-	 * name, with values corresponding to the {@link org.hibernate.internal.FilterImpl}
-	 * instance.
-	 *
-	 * @return The currently enabled filters.
-	 *
-	 * @deprecated use #getLoadQueryInfluencers instead
-	 */
-	@Deprecated
-	public Map getEnabledFilters();
+	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters);
 
 	public int getDontFlushFromFind();
 
 	//TODO: temporary
 
 	/**
 	 * Get the persistence context for this session
 	 */
 	public PersistenceContext getPersistenceContext();
 
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters)
 			throws HibernateException;
 
 
 	// copied from Session:
 
 	public CacheMode getCacheMode();
 
 	public void setCacheMode(CacheMode cm);
 
 	public boolean isOpen();
 
 	public boolean isConnected();
 
 	public FlushMode getFlushMode();
 
 	public void setFlushMode(FlushMode fm);
 
 	public Connection connection();
 
 	public void flush();
 
 	/**
 	 * Get a Query instance for a named query or named native SQL query
 	 */
 	public Query getNamedQuery(String name);
 
 	/**
 	 * Get a Query instance for a named native SQL query
 	 */
 	public Query getNamedSQLQuery(String name);
 
 	public boolean isEventSource();
 
 	public void afterScrollOperation();
 
 	/**
-	 * Get the <i>internal</i> fetch profile currently associated with this session.
-	 *
-	 * @return The current internal fetch profile, or null if none currently associated.
-	 *
-	 * @deprecated use #getLoadQueryInfluencers instead
-	 */
-	@Deprecated
-	public String getFetchProfile();
-
-	/**
-	 * Set the current <i>internal</i> fetch profile for this session.
-	 *
-	 * @param name The internal fetch profile name to use
-	 *
-	 * @deprecated use {@link #getLoadQueryInfluencers} instead
-	 */
-	@Deprecated
-	public void setFetchProfile(String name);
-
-	/**
 	 * Retrieve access to the session's transaction coordinator.
 	 *
 	 * @return The transaction coordinator.
 	 */
 	public TransactionCoordinator getTransactionCoordinator();
 
 	public JdbcCoordinator getJdbcCoordinator();
 
 	/**
 	 * Determine whether the session is closed.  Provided separately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synchronization
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return True if the session is closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	public boolean shouldAutoClose();
 
 	public boolean isAutoCloseSessionEnabled();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 *         should never be null.
 	 */
 	public LoadQueryInfluencers getLoadQueryInfluencers();
 
 	/**
 	 * Used from EntityManager
 	 *
 	 * @param namedQueryDefinition The named query definition
 	 *
 	 * @return The basic HQL/JPQL query (without saved settings applied)
 	 */
 	Query createQuery(NamedQueryDefinition namedQueryDefinition);
 
 	/**
 	 * Used from EntityManager
 	 *
 	 * @param namedQueryDefinition The named query definition
 	 *
 	 * @return The basic SQL query (without saved settings applied)
 	 */
 	SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition);
 
 	public SessionEventListenerManager getEventListenerManager();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
index 321e769b90..91048e3666 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
@@ -1,514 +1,514 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.CascadePoint;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EntityCopyObserver;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener implements MergeEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultMergeEventListener.class );
 
 	private String entityCopyObserverStrategy;
 
 	@Override
 	protected Map getMergeMap(Object anything) {
 		return ( (MergeContext) anything ).invertMap();
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event) throws HibernateException {
 		final EntityCopyObserver entityCopyObserver = createEntityCopyObserver( event.getSession().getFactory() );
 		final MergeContext mergeContext = new MergeContext( event.getSession(), entityCopyObserver );
 		try {
 			onMerge( event, mergeContext );
 			entityCopyObserver.topLevelMergeComplete( event.getSession() );
 		}
 		finally {
 			entityCopyObserver.clear();
 			mergeContext.clear();
 		}
 	}
 
 	private EntityCopyObserver createEntityCopyObserver(SessionFactoryImplementor sessionFactory) {
 		final ServiceRegistry serviceRegistry = sessionFactory.getServiceRegistry();
 		if ( entityCopyObserverStrategy == null ) {
 			final ConfigurationService configurationService
 					= serviceRegistry.getService( ConfigurationService.class );
 			entityCopyObserverStrategy = configurationService.getSetting(
 					"hibernate.event.merge.entity_copy_observer",
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					EntityCopyNotAllowedObserver.SHORT_NAME
 			);
 			LOG.debugf( "EntityCopyObserver strategy: %s", entityCopyObserverStrategy );
 		}
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		return strategySelector.resolveStrategy( EntityCopyObserver.class, entityCopyObserverStrategy );
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
 
 		final MergeContext copyCache = (MergeContext) copiedAlready;
 		final EventSource source = event.getSession();
 		final Object original = event.getOriginal();
 
 		if ( original != null ) {
 
 			final Object entity;
 			if ( original instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) original ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
 					LOG.trace( "Ignoring uninitialized proxy" );
 					event.setResult( source.load( li.getEntityName(), li.getIdentifier() ) );
 					return; //EARLY EXIT!
 				}
 				else {
 					entity = li.getImplementation();
 				}
 			}
 			else {
 				entity = original;
 			}
 
 			if ( copyCache.containsKey( entity ) &&
 					( copyCache.isOperatedOn( entity ) ) ) {
 				LOG.trace( "Already in merge process" );
 				event.setResult( entity );
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
 					LOG.trace( "Already in copyCache; setting in merge process" );
 					copyCache.setOperatedOn( entity, true );
 				}
 				event.setEntity( entity );
 				EntityState entityState = null;
 
 				// Check the persistence context for an entry relating to this
 				// entity to be merged...
 				EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 				if ( entry == null ) {
 					EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 					Serializable id = persister.getIdentifier( entity, source );
 					if ( id != null ) {
 						final EntityKey key = source.generateEntityKey( id, persister );
 						final Object managedEntity = source.getPersistenceContext().getEntity( key );
 						entry = source.getPersistenceContext().getEntry( managedEntity );
 						if ( entry != null ) {
 							// we have specialized case of a detached entity from the
 							// perspective of the merge operation.  Specifically, we
 							// have an incoming entity instance which has a corresponding
 							// entry in the current persistence context, but registered
 							// under a different entity instance
 							entityState = EntityState.DETACHED;
 						}
 					}
 				}
 
 				if ( entityState == null ) {
 					entityState = getEntityState( entity, event.getEntityName(), entry, source );
 				}
 
 				switch ( entityState ) {
 					case DETACHED:
 						entityIsDetached( event, copyCache );
 						break;
 					case TRANSIENT:
 						entityIsTransient( event, copyCache );
 						break;
 					case PERSISTENT:
 						entityIsPersistent( event, copyCache );
 						break;
 					default: //DELETED
 						throw new ObjectDeletedException(
 								"deleted instance passed to merge",
 								null,
 								getLoggableName( event.getEntityName(), entity )
 						);
 				}
 			}
 
 		}
 
 	}
 
 	protected void entityIsPersistent(MergeEvent event, Map copyCache) {
 		LOG.trace( "Ignoring persistent instance" );
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		( (MergeContext) copyCache ).put( entity, entity, true );  //before cascade!
 
 		cascadeOnMerge( source, persister, entity, copyCache );
 		copyValues( persister, entity, entity, source, copyCache );
 
 		event.setResult( entity );
 	}
 
 	protected void entityIsTransient(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging transient instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final String entityName = event.getEntityName();
 		final EntityPersister persister = source.getEntityPersister( entityName, entity );
 
 		final Serializable id = persister.hasIdentifierProperty() ?
 				persister.getIdentifier( entity, source ) :
 				null;
 		if ( copyCache.containsKey( entity ) ) {
 			persister.setIdentifier( copyCache.get( entity ), id, source );
 		}
 		else {
 			( (MergeContext) copyCache ).put( entity, source.instantiate( persister, id ), true ); //before cascade!
 		}
 		final Object copy = copyCache.get( entity );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		//cascadeOnMerge(event, persister, entity, copyCache, Cascades.CASCADE_BEFORE_MERGE);
 		super.cascadeBeforeSave( source, persister, entity, copyCache );
 		copyValues( persister, entity, copy, source, copyCache, ForeignKeyDirection.FROM_PARENT );
 
 		saveTransientEntity( copy, entityName, event.getRequestedId(), source, copyCache );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		super.cascadeAfterSave( source, persister, entity, copyCache );
 		copyValues( persister, entity, copy, source, copyCache, ForeignKeyDirection.TO_PARENT );
 
 		event.setResult( copy );
 	}
 
 	private void saveTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache) {
 		//this bit is only *really* absolutely necessary for handling
 		//requestedId, but is also good if we merge multiple object
 		//graphs, since it helps ensure uniqueness
 		if ( requestedId == null ) {
 			saveWithGeneratedId( entity, entityName, copyCache, source, false );
 		}
 		else {
 			saveWithRequestedId( entity, requestedId, entityName, copyCache, source );
 		}
 	}
 
 	protected void entityIsDetached(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging detached instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		Serializable id = event.getRequestedId();
 		if ( id == null ) {
 			id = persister.getIdentifier( entity, source );
 		}
 		else {
 			// check that entity id = requestedId
 			Serializable entityId = persister.getIdentifier( entity, source );
 			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getFactory() ) ) {
 				throw new HibernateException( "merge requested with id not matching id of passed entity" );
 			}
 		}
 
-		String previousFetchProfile = source.getFetchProfile();
-		source.setFetchProfile( "merge" );
+		String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
+		source.getLoadQueryInfluencers().setInternalFetchProfile( "merge" );
 		//we must clone embedded composite identifiers, or
 		//we will get back the same instance that we pass in
 		final Serializable clonedIdentifier = (Serializable) persister.getIdentifierType()
 				.deepCopy( id, source.getFactory() );
 		final Object result = source.get( entityName, clonedIdentifier );
-		source.setFetchProfile( previousFetchProfile );
+		source.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 
 		if ( result == null ) {
 			//TODO: we should throw an exception if we really *know* for sure
 			//      that this is a detached instance, rather than just assuming
 			//throw new StaleObjectStateException(entityName, id);
 
 			// we got here because we assumed that an instance
 			// with an assigned id was detached, when it was
 			// really persistent
 			entityIsTransient( event, copyCache );
 		}
 		else {
 			( (MergeContext) copyCache ).put( entity, result, true ); //before cascade!
 
 			final Object target = source.getPersistenceContext().unproxy( result );
 			if ( target == entity ) {
 				throw new AssertionFailure( "entity was not detached" );
 			}
 			else if ( !source.getEntityName( target ).equals( entityName ) ) {
 				throw new WrongClassException(
 						"class of the given object did not match class of persistent copy",
 						event.getRequestedId(),
 						entityName
 				);
 			}
 			else if ( isVersionChanged( entity, source, persister, target ) ) {
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor()
 							.optimisticFailure( entityName );
 				}
 				throw new StaleObjectStateException( entityName, id );
 			}
 
 			// cascade first, so that all unsaved objects get their
 			// copy created before we actually copy
 			cascadeOnMerge( source, persister, entity, copyCache );
 			copyValues( persister, entity, target, source, copyCache );
 
 			//copyValues works by reflection, so explicitly mark the entity instance dirty
 			markInterceptorDirty( entity, target, persister );
 
 			event.setResult( result );
 		}
 
 	}
 
 	private void markInterceptorDirty(final Object entity, final Object target, EntityPersister persister) {
 		if ( persister.getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = persister.getInstrumentationMetadata().extractInterceptor( target );
 			if ( interceptor != null ) {
 				interceptor.dirty();
 			}
 		}
 	}
 
 	private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
 		if ( !persister.isVersioned() ) {
 			return false;
 		}
 		// for merging of versioned entities, we consider the version having
 		// been changed only when:
 		// 1) the two version values are different;
 		//      *AND*
 		// 2) The target actually represents database state!
 		//
 		// This second condition is a special case which allows
 		// an entity to be merged during the same transaction
 		// (though during a seperate operation) in which it was
 		// originally persisted/saved
 		boolean changed = !persister.getVersionType().isSame(
 				persister.getVersion( target ),
 				persister.getVersion( entity )
 		);
 
 		// TODO : perhaps we should additionally require that the incoming entity
 		// version be equivalent to the defined unsaved-value?
 		return changed && existsInDatabase( target, source, persister );
 	}
 
 	private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
 		EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			Serializable id = persister.getIdentifier( entity, source );
 			if ( id != null ) {
 				final EntityKey key = source.generateEntityKey( id, persister );
 				final Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
 		return entry != null && entry.isExistsInDatabase();
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity ),
 				persister.getPropertyValues( target ),
 				persister.getPropertyTypes(),
 				source,
 				target,
 				copyCache
 		);
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 
 		final Object[] copiedValues;
 
 		if ( foreignKeyDirection == ForeignKeyDirection.TO_PARENT ) {
 			// this is the second pass through on a merge op, so here we limit the
 			// replacement to associations types (value types were already replaced
 			// during the first pass)
 			copiedValues = TypeHelper.replaceAssociations(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 		else {
 			copiedValues = TypeHelper.replace(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	/**
 	 * Perform any cascades needed as part of this copy event.
 	 *
 	 * @param source The merge event being processed.
 	 * @param persister The persister of the entity being copied.
 	 * @param entity The entity being copied.
 	 * @param copyCache A cache of already copied instance.
 	 */
 	protected void cascadeOnMerge(
 			final EventSource source,
 			final EntityPersister persister,
 			final Object entity,
 			final Map copyCache
 	) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			Cascade.cascade(
 					getCascadeAction(),
 					CascadePoint.BEFORE_MERGE,
 					source,
 					persister,
 					entity,
 					copyCache
 			);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 
 	@Override
 	protected CascadingAction getCascadeAction() {
 		return CascadingActions.MERGE;
 	}
 
 	@Override
 	protected Boolean getAssumedUnsaved() {
 		return Boolean.FALSE;
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
 	protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 			throws HibernateException {
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
 	protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 			throws HibernateException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index da02915f55..0d96d4800a 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,615 +1,615 @@
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
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.UUID;
 
 import org.hibernate.ConnectionAcquisitionMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.Transaction;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionImpl;
 import org.hibernate.id.uuid.StandardRandomStrategy;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.internal.ProcedureCallImpl;
 import org.hibernate.resource.jdbc.spi.JdbcObserver;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinatorBuilder;
 import org.hibernate.resource.transaction.TransactionCoordinatorBuilder.TransactionCoordinatorOptions;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl
 		implements Serializable, SharedSessionContract, SessionImplementor, JdbcSessionOwner, TransactionCoordinatorOptions {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed;
 
 	protected transient Transaction currentHibernateTransaction;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public abstract boolean shouldAutoJoinTransaction();
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
 	@Override
 	public boolean isClosed() {
 		return closed || factory.isClosed();
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	@Override
 	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
 		String queryString = namedQueryDefinition.getQueryString();
 		final Query query = new QueryImpl(
 				queryString,
 				namedQueryDefinition.getFlushMode(),
 				this,
 				getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( "named HQL query " + namedQueryDefinition.getName() );
 		if ( namedQueryDefinition.getLockOptions() != null ) {
 			query.setLockOptions( namedQueryDefinition.getLockOptions() );
 		}
 
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
 		final ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata(
 				namedQueryDefinition.getQueryString()
 		);
 		final SQLQuery query = new SQLQueryImpl(
 				namedQueryDefinition,
 				this,
 				parameterMetadata
 		);
 		query.setComment( "named native SQL query " + namedQueryDefinition.getName() );
 		return query;
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			query = createQuery( nqd );
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 
 			query = createSQLQuery( nsqlqd );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize() );
 		}
 		if ( nqd.getCacheMode() != null ) {
 			query.setCacheMode( nqd.getCacheMode() );
 		}
 		if ( nqd.getComment() != null ) {
 			query.setComment( nqd.getComment() );
 		}
 		if ( nqd.getFirstResult() != null ) {
 			query.setFirstResult( nqd.getFirstResult() );
 		}
 		if ( nqd.getMaxResults() != null ) {
 			query.setMaxResults( nqd.getMaxResults() );
 		}
 		if ( nqd.getFlushMode() != null ) {
 			query.setFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		final QueryImpl query = new QueryImpl(
 				queryString,
 				this,
 				getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		final SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 				this,
 				factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall getNamedProcedureCall(String name) {
 		errorIfClosed();
 
 		final ProcedureCallMemento memento = factory.getNamedQueryRepository().getNamedProcedureCallMemento( name );
 		if ( memento == null ) {
 			throw new IllegalArgumentException(
 					"Could not find named stored procedure call with that registration name : " + name
 			);
 		}
 		final ProcedureCall procedureCall = memento.makeProcedureCall( this );
 //		procedureCall.setComment( "Named stored procedure call [" + name + "]" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
-		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
+		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		if ( this.currentHibernateTransaction == null || this.currentHibernateTransaction.getStatus() != TransactionStatus.ACTIVE ) {
 			this.currentHibernateTransaction = new TransactionImpl( getTransactionCoordinator() );
 		}
 		getTransactionCoordinator().pulse();
 		return currentHibernateTransaction;
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						getEventListenerManager(),
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						getEventListenerManager(),
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private UUID sessionIdentifier;
 
 	public UUID getSessionIdentifier() {
 		if ( sessionIdentifier == null ) {
 			sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID( this );
 		}
 		return sessionIdentifier;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final SessionEventListener listener;
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(
 				SessionEventListener listener,
 				ConnectionProvider connectionProvider) {
 			this.listener = listener;
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			try {
 				listener.jdbcConnectionAcquisitionStart();
 				return connectionProvider.getConnection();
 			}
 			finally {
 				listener.jdbcConnectionAcquisitionEnd();
 			}
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			try {
 				listener.jdbcConnectionReleaseStart();
 				connectionProvider.closeConnection( connection );
 			}
 			finally {
 				listener.jdbcConnectionReleaseEnd();
 			}
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final SessionEventListener listener;
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(
 				SessionEventListener listener,
 				MultiTenantConnectionProvider connectionProvider) {
 			this.listener = listener;
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 
 			try {
 				listener.jdbcConnectionAcquisitionStart();
 				return connectionProvider.getConnection( tenantIdentifier );
 			}
 			finally {
 				listener.jdbcConnectionAcquisitionEnd();
 			}
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 
 			try {
 				listener.jdbcConnectionReleaseStart();
 				connectionProvider.releaseConnection( tenantIdentifier, connection );
 			}
 			finally {
 				listener.jdbcConnectionReleaseEnd();
 			}
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	public class JdbcSessionContextImpl implements JdbcSessionContext {
 		private final SessionFactoryImpl sessionFactory;
 		private final StatementInspector inspector;
 		private final transient ServiceRegistry serviceRegistry;
 		private final transient JdbcObserver jdbcObserver;
 
 		public JdbcSessionContextImpl(SessionFactoryImpl sessionFactory, StatementInspector inspector) {
 			this.sessionFactory = sessionFactory;
 			this.inspector = inspector;
 			this.serviceRegistry = sessionFactory.getServiceRegistry();
 			this.jdbcObserver = new JdbcObserverImpl();
 
 			if ( inspector == null ) {
 				throw new IllegalArgumentException( "StatementInspector cannot be null" );
 			}
 		}
 
 		@Override
 		public boolean isScrollableResultSetsEnabled() {
 			return settings().isScrollableResultSetsEnabled();
 		}
 
 		@Override
 		public boolean isGetGeneratedKeysEnabled() {
 			return settings().isGetGeneratedKeysEnabled();
 		}
 
 		@Override
 		public int getFetchSize() {
 			return settings().getJdbcFetchSize();
 		}
 
 		@Override
 		public ConnectionReleaseMode getConnectionReleaseMode() {
 			return settings().getConnectionReleaseMode();
 		}
 
 		@Override
 		public ConnectionAcquisitionMode getConnectionAcquisitionMode() {
 			return ConnectionAcquisitionMode.DEFAULT;
 		}
 
 		@Override
 		public StatementInspector getStatementInspector() {
 			return inspector;
 		}
 
 		@Override
 		public JdbcObserver getObserver() {
 			return this.jdbcObserver;
 		}
 
 		@Override
 		public SessionFactoryImplementor getSessionFactory() {
 			return this.sessionFactory;
 		}
 
 		@Override
 		public ServiceRegistry getServiceRegistry() {
 			return this.serviceRegistry;
 		}
 
 		private SessionFactoryOptions settings() {
 			return this.sessionFactory.getSessionFactoryOptions();
 		}
 	}
 
 	public class JdbcObserverImpl implements JdbcObserver {
 
 		private final transient List<ConnectionObserver> observers;
 
 		public JdbcObserverImpl() {
 			this.observers = new ArrayList<ConnectionObserver>();
 			this.observers.add( new ConnectionObserverStatsBridge( factory ) );
 		}
 
 		@Override
 		public void jdbcConnectionAcquisitionStart() {
 
 		}
 
 		@Override
 		public void jdbcConnectionAcquisitionEnd(Connection connection) {
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionObtained( connection );
 			}
 		}
 
 		@Override
 		public void jdbcConnectionReleaseStart() {
 
 		}
 
 		@Override
 		public void jdbcConnectionReleaseEnd() {
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionReleased();
 			}
 		}
 
 		@Override
 		public void jdbcPrepareStatementStart() {
 			getEventListenerManager().jdbcPrepareStatementStart();
 		}
 
 		@Override
 		public void jdbcPrepareStatementEnd() {
 			for ( ConnectionObserver observer : observers ) {
 				observer.statementPrepared();
 			}
 			getEventListenerManager().jdbcPrepareStatementEnd();
 		}
 
 		@Override
 		public void jdbcExecuteStatementStart() {
 			getEventListenerManager().jdbcExecuteStatementStart();
 		}
 
 		@Override
 		public void jdbcExecuteStatementEnd() {
 			getEventListenerManager().jdbcExecuteStatementEnd();
 		}
 
 		@Override
 		public void jdbcExecuteBatchStart() {
 			getEventListenerManager().jdbcExecuteBatchStart();
 		}
 
 		@Override
 		public void jdbcExecuteBatchEnd() {
 			getEventListenerManager().jdbcExecuteBatchEnd();
 		}
 	}
 
 	@Override
 	public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder() {
 		return factory.getServiceRegistry().getService( TransactionCoordinatorBuilder.class );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 20e0695493..623ffb6f2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1480 +1,1540 @@
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
 package org.hibernate.internal;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
+import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SessionFactoryImpl.class.getName()
 	);
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
+	private final transient Map<Class,String> entityProxyInterfaceMap;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	public SessionFactoryImpl(final MetadataImplementor metadata, SessionFactoryOptions options) {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = options;
 		this.settings = new Settings( options, metadata );
 
 		this.serviceRegistry = options.getServiceRegistry()
 				.getService( SessionFactoryServiceRegistryFactory.class )
 				.buildServiceRegistry( this, options );
 
 		final CfgXmlAccessService cfgXmlAccessService = serviceRegistry.getService( CfgXmlAccessService.class );
 
 		String sfName = settings.getSessionFactoryName();
 		if ( cfgXmlAccessService.getAggregatedConfig() != null ) {
 			if ( sfName == null ) {
 				sfName = cfgXmlAccessService.getAggregatedConfig().getSessionFactoryName();
 			}
 			applyCfgXmlValues( cfgXmlAccessService.getAggregatedConfig(), serviceRegistry );
 		}
 
 		this.name = sfName;
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		this.properties = new Properties();
 		this.properties.putAll( serviceRegistry.getService( ConfigurationService.class ).getSettings() );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), options.getCustomSqlFunctionMap() );
 
 		for ( SessionFactoryObserver sessionFactoryObserver : options.getSessionFactoryObservers() ) {
 			this.observer.addObserver( sessionFactoryObserver );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( metadata.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 				integrators.clear();
 			}
 		}
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( metadata, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		this.identifierGenerators = new HashMap<String, IdentifierGenerator>();
 		for ( PersistentClass model : metadata.getEntityBindings() ) {
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						metadata.getIdentifierGeneratorFactory(),
 						getDialect(),
 						settings.getDefaultCatalogName(),
 						settings.getDefaultSchemaName(),
 						(RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 		this.imports = new HashMap<String,String>( metadata.getImports() );
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final PersisterCreationContext persisterCreationContext = new PersisterCreationContext() {
 			@Override
 			public SessionFactoryImplementor getSessionFactory() {
 				return SessionFactoryImpl.this;
 			}
 
 			@Override
 			public MetadataImplementor getMetadata() {
 				return metadata;
 			}
 		};
 
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		// todo : consider removing this silliness and just have EntityPersister directly implement ClassMetadata
 		//		EntityPersister.getClassMetadata() for the internal impls simply "return this";
 		//		collapsing those would allow us to remove this "extra" Map
 		//
 		// todo : similar for CollectionPersister/CollectionMetadata
 
 		this.entityPersisters = new HashMap<String,EntityPersister>();
 		Map cacheAccessStrategiesMap = new HashMap();
 		Map<String,ClassMetadata> inFlightClassMetadataMap = new HashMap<String,ClassMetadata>();
+		this.entityProxyInterfaceMap = CollectionHelper.concurrentMap( metadata.getEntityBindings().size() );
 		for ( final PersistentClass model : metadata.getEntityBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			final EntityRegionAccessStrategy accessStrategy = determineEntityRegionAccessStrategy(
 					regionFactory,
 					cacheAccessStrategiesMap,
 					model,
 					cacheRegionName
 			);
 
 			final NaturalIdRegionAccessStrategy naturalIdAccessStrategy = determineNaturalIdRegionAccessStrategy(
 					regionFactory,
 					cacheRegionPrefix,
 					cacheAccessStrategiesMap,
 					model
 			);
 
 			final EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					persisterCreationContext
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			inFlightClassMetadataMap.put( model.getEntityName(), cp.getClassMetadata() );
+
+			if ( cp.getConcreteProxyClass() != null
+					&& cp.getConcreteProxyClass().isInterface()
+					&& !Map.class.isAssignableFrom( cp.getConcreteProxyClass() )
+					&& cp.getMappedClass() != cp.getConcreteProxyClass() ) {
+				// IMPL NOTE : we exclude Map based proxy interfaces here because that should
+				//		indicate MAP entity mode.0
+
+				if ( cp.getMappedClass().equals( cp.getConcreteProxyClass() ) ) {
+					// this part handles an odd case in the Hibernate test suite where we map an interface
+					// as the class and the proxy.  I cannot think of a real life use case for that
+					// specific test, but..
+					LOG.debugf( "Entity [%s] mapped same interface [%s] as class and proxy", cp.getEntityName(), cp.getMappedClass() );
+				}
+				else {
+					final String old = entityProxyInterfaceMap.put( cp.getConcreteProxyClass(), cp.getEntityName() );
+					if ( old != null ) {
+						throw new HibernateException(
+								String.format(
+										Locale.ENGLISH,
+										"Multiple entities [%s, %s] named the same interface [%s] as their proxy which is not supported",
+										old,
+										cp.getEntityName(),
+										cp.getConcreteProxyClass().getName()
+								)
+						);
+					}
+				}
+			}
 		}
 		this.classMetadata = Collections.unmodifiableMap( inFlightClassMetadataMap );
 
 		this.collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,Set<String>> inFlightEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		for ( final Collection model : metadata.getCollectionBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			final CollectionRegionAccessStrategy accessStrategy;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion(
 						cacheRegionName,
 						properties,
 						CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			else {
 				accessStrategy = null;
 			}
 
 			final CollectionPersister persister = persisterFactory.createCollectionPersister(
 					model,
 					accessStrategy,
 					persisterCreationContext
 			);
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		this.collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 
 		for ( Map.Entry<String,Set<String>> entityToCollectionRoleMapEntry : inFlightEntityToCollectionRoleMap.entrySet() ) {
 			entityToCollectionRoleMapEntry.setValue(
 					Collections.unmodifiableSet( entityToCollectionRoleMapEntry.getValue() )
 			);
 		}
 		this.collectionRolesByEntityParticipant = Collections.unmodifiableMap( inFlightEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = metadata.buildNamedQueryRepository( this );
 
 		// after *all* persisters and named queries are registered
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.generateEntityDefinition();
 		}
 
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 		}
 		for ( CollectionPersister persister : collectionPersisters.values() ) {
 			persister.postInstantiate();
 		}
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				metadata,
 				sessionFactoryOptions
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, metadata ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, metadata ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.mapping.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.observer.sessionFactoryCreated( this );
 
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 	}
 
 	private void applyCfgXmlValues(LoadedConfig aggregatedConfig, SessionFactoryServiceRegistry serviceRegistry) {
 		final JaccService jaccService = serviceRegistry.getService( JaccService.class );
 		if ( jaccService.getContextId() != null ) {
 			final JaccPermissionDeclarations permissions = aggregatedConfig.getJaccPermissions( jaccService.getContextId() );
 			if ( permissions != null ) {
 				for ( GrantedPermission grantedPermission : permissions.getPermissionDeclarations() ) {
 					jaccService.addPermission( grantedPermission );
 				}
 			}
 		}
 
 		if ( aggregatedConfig.getEventListenerMap() != null ) {
 			final ClassLoaderService cls = serviceRegistry.getService( ClassLoaderService.class );
 			final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 			for ( Map.Entry<EventType, Set<String>> entry : aggregatedConfig.getEventListenerMap().entrySet() ) {
 				final EventListenerGroup group = eventListenerRegistry.getEventListenerGroup( entry.getKey() );
 				for ( String listenerClassName : entry.getValue() ) {
 					try {
 						group.appendListener( cls.classForName( listenerClassName ).newInstance() );
 					}
 					catch (Exception e) {
 						throw new ConfigurationException( "Unable to instantiate event listener class : " + listenerClassName, e );
 					}
 				}
 			}
 		}
 	}
 
 	private NaturalIdRegionAccessStrategy determineNaturalIdRegionAccessStrategy(
 			RegionFactory regionFactory,
 			String cacheRegionPrefix,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model) {
 		NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 		if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 			final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 			naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) cacheAccessStrategiesMap.get( naturalIdCacheRegionName );
 
 			if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 
 				NaturalIdRegion naturalIdRegion = null;
 				try {
 					naturalIdRegion = regionFactory.buildNaturalIdRegion(
 							naturalIdCacheRegionName,
 							properties,
 							cacheDataDescription
 					);
 				}
 				catch ( UnsupportedOperationException e ) {
 					LOG.warnf(
 							"Shared cache region factory [%s] does not support natural id caching; " +
 									"shared NaturalId caching will be disabled for not be enabled for %s",
 							regionFactory.getClass().getName(),
 							model.getEntityName()
 					);
 				}
 
 				if (naturalIdRegion != null) {
 					naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 					cacheAccessStrategiesMap.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 					cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 				}
 			}
 		}
 		return naturalIdAccessStrategy;
 	}
 
 	private EntityRegionAccessStrategy determineEntityRegionAccessStrategy(
 			RegionFactory regionFactory,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model,
 			String cacheRegionName) {
 		EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) cacheAccessStrategiesMap.get( cacheRegionName );
 		if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			if ( accessType != null ) {
 				LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 				EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 																					 .decode( model ) );
 				accessStrategy = entityRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 			}
 		}
 		return accessStrategy;
 	}
 
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilderImplementor withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
+	@Override
+	public Map<String, EntityPersister> getEntityPersisters() {
+		return entityPersisters;
+	}
+
+	@Override
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
-		EntityPersister result = entityPersisters.get(entityName);
+		EntityPersister result = entityPersisters.get( entityName );
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
-	public Map<String, CollectionPersister> getCollectionPersisters() {
-		return collectionPersisters;
+	public EntityPersister locateEntityPersister(Class byClass) {
+		EntityPersister entityPersister = entityPersisters.get( byClass.getName() );
+		if ( entityPersister == null ) {
+			String mappedEntityName = entityProxyInterfaceMap.get( byClass );
+			if ( mappedEntityName != null ) {
+				entityPersister = entityPersisters.get( mappedEntityName );
+			}
+		}
+
+		if ( entityPersister == null ) {
+			throw new HibernateException( "Unable to locate persister: " + byClass.getName() );
+		}
+
+		return entityPersister;
 	}
 
 	@Override
-	public Map<String, EntityPersister> getEntityPersisters() {
-		return entityPersisters;
+	public EntityPersister locateEntityPersister(String byName) {
+		final EntityPersister entityPersister = entityPersisters.get( byName );
+		if ( entityPersister == null ) {
+			throw new HibernateException( "Unable to locate persister: " + byName );
+		}
+		return entityPersister;
+	}
+
+	@Override
+	public Map<String, CollectionPersister> getCollectionPersisters() {
+		return collectionPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	@Deprecated
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
-		return collectionMetadata.get(roleName);
+		return collectionMetadata.get( roleName );
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
      * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister( className ).getPropertyType( propertyName );
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return jdbcServices.getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionFactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return identifierGenerators.get(rootEntityName);
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 //			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 //				LOG.autoFlushWillNotWork();
 //			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
 				return null;
 			}
 		}
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private StatementInspector statementInspector;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 		private List<SessionEventListener> listeners;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.statementInspector = sessionFactory.getSessionFactoryOptions().getStatementInspector();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = settings.getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 		protected TransactionCoordinator getTransactionCoordinator() {
 			return null;
 		}
 
 		protected JdbcCoordinatorImpl getJdbcCoordinator() {
 			return null;
 		}
 
 		protected Transaction getTransaction() {
 			return null;
 		}
 
 		protected ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			log.tracef( "Opening Hibernate Session.  tenant=%s, owner=%s", tenantIdentifier, sessionOwner );
 			final SessionImpl session = new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					getJdbcCoordinator(),
 					getTransaction(),
 					getTransactionCompletionProcesses(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					statementInspector,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 
 			for ( SessionEventListener listener : listeners ) {
 				session.getEventListenerManager().addListener( listener );
 			}
 
 			return session;
 		}
 
 		@Override
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder statementInspector(StatementInspector statementInspector) {
 			this.statementInspector = statementInspector;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder eventListeners(SessionEventListener... listeners) {
 			Collections.addAll( this.listeners, listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			listeners.clear();
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory,
 					sessionFactory.settings.getRegionFactory().nextTimestamp() );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return getSessionFactoryOptions().getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return getSessionFactoryOptions().getCurrentTenantIdentifierResolver();
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index d8edf850c7..2e809b6a79 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2884 +1,2857 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2005-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal;
 
-import javax.persistence.EntityNotFoundException;
-import javax.transaction.SystemException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.NClob;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+import javax.persistence.EntityNotFoundException;
+import javax.transaction.SystemException;
 
-import org.jboss.logging.Logger;
-
-import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.NonContextualLobCreator;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.TransactionObserver;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.ClearEvent;
 import org.hibernate.event.spi.ClearEventListener;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.DirtyCheckEvent;
 import org.hibernate.event.spi.DirtyCheckEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.EvictEvent;
 import org.hibernate.event.spi.EvictEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.LoadEventListener.LoadType;
 import org.hibernate.event.spi.LockEvent;
 import org.hibernate.event.spi.LockEventListener;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.event.spi.RefreshEvent;
 import org.hibernate.event.spi.RefreshEventListener;
 import org.hibernate.event.spi.ReplicateEvent;
 import org.hibernate.event.spi.ReplicateEventListener;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CriteriaImpl.CriterionEntry;
-import org.hibernate.internal.util.StringHelper;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
-import org.hibernate.type.Type;
+
+import org.jboss.logging.Logger;
 
 /**
  * Concrete implementation of a Session.
  *
  * Exposes two interfaces:<ul>
  *     <li>{@link Session} to the application</li>
  *     <li>{@link org.hibernate.engine.spi.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  *
  * This class is not thread-safe.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public final class SessionImpl extends AbstractSessionImpl implements EventSource {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SessionImpl.class.getName()
 	);
 
 	private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
 
 	private transient long timestamp;
 
 	private transient SessionOwner sessionOwner;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinator transactionCoordinator;
 	private transient JdbcCoordinatorImpl jdbcCoordinator;
 	private transient Interceptor interceptor;
 	private StatementInspector statementInspector;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient int dontFlushFromFind;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private final transient boolean isTransactionCoordinatorShared;
 	private transient TransactionObserver transactionObserver;
 
 	private SessionEventListenerManagerImpl sessionEventsManager = new SessionEventListenerManagerImpl();
 
 	private transient JdbcSessionContext jdbcSessionContext;
 
 	/**
 	 * Constructor used for openSession(...) processing, as well as construction
 	 * of sessions for getCurrentSession().
 	 *
 	 * @param connection The user-supplied connection to use for this session.
 	 * @param factory The factory from which this session was obtained
 	 * @param transactionCoordinator The transaction coordinator to use, may be null to indicate that a new transaction
 	 * coordinator should get created.
 	 * @param autoJoinTransactions Should the session automatically join JTA transactions?
 	 * @param timestamp The timestamp for this session
 	 * @param interceptor The interceptor to be applied to this session
 	 * @param flushBeforeCompletionEnabled Should we auto flush before completion of transaction
 	 * @param autoCloseSessionEnabled Should we auto close after completion of transaction
 	 * @param connectionReleaseMode The mode by which we should release JDBC connections.
 	 * @param tenantIdentifier The tenant identifier to use.  May be null
 	 */
 	SessionImpl(
 			final Connection connection,
 			final SessionFactoryImpl factory,
 			final SessionOwner sessionOwner,
 			final TransactionCoordinator transactionCoordinator,
 			final JdbcCoordinatorImpl jdbcCoordinator,
 			final Transaction transaction,
 			final ActionQueue.TransactionCompletionProcesses transactionCompletionProcesses,
 			final boolean autoJoinTransactions,
 			final long timestamp,
 			final Interceptor interceptor,
 			final StatementInspector statementInspector,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode,
 			final String tenantIdentifier) {
 		super( factory, tenantIdentifier );
 		this.timestamp = timestamp;
 		this.sessionOwner = sessionOwner;
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 
 		if ( statementInspector == null ) {
 			this.statementInspector = new StatementInspector() {
 				@Override
 				public String inspect(String sql) {
 					return SessionImpl.this.interceptor.onPrepareStatement( sql );
 				}
 			};
 		}
 		else {
 			this.statementInspector = statementInspector;
 		}
 		this.jdbcSessionContext = new JdbcSessionContextImpl( factory, this.statementInspector );
 
 		if ( transactionCoordinator == null ) {
 			this.isTransactionCoordinatorShared = false;
 			this.connectionReleaseMode = connectionReleaseMode;
 			this.autoJoinTransactions = autoJoinTransactions;
 
 			this.jdbcCoordinator = new JdbcCoordinatorImpl( connection, this );
 			this.transactionCoordinator = getTransactionCoordinatorBuilder().buildTransactionCoordinator( this.jdbcCoordinator, this );
 			this.currentHibernateTransaction = getTransaction();
 		}
 		else {
 			if ( connection != null ) {
 				throw new SessionException( "Cannot simultaneously share transaction context and specify connection" );
 			}
 			this.transactionCoordinator = transactionCoordinator;
 			this.jdbcCoordinator = jdbcCoordinator;
 			this.currentHibernateTransaction = transaction;
 			this.isTransactionCoordinatorShared = true;
 			this.autoJoinTransactions = false;
 			if ( transactionCompletionProcesses != null ) {
 				actionQueue.setTransactionCompletionProcesses( transactionCompletionProcesses, true );
 			}
 			if ( autoJoinTransactions ) {
 				LOG.debug(
 						"Session creation specified 'autoJoinTransactions', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			if ( connectionReleaseMode != this.jdbcCoordinator.getConnectionReleaseMode() ) {
 				LOG.debug(
 						"Session creation specified 'getConnectionReleaseMode', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			this.connectionReleaseMode = this.jdbcCoordinator.getConnectionReleaseMode();
 
 			transactionObserver = new TransactionObserver() {
 				@Override
 				public void afterBegin() {
 				}
 
 				@Override
 				public void beforeCompletion() {
 					if ( isOpen() && flushBeforeCompletionEnabled ) {
 						SessionImpl.this.managedFlush();
 					}
 					actionQueue.beforeTransactionCompletion();
 					try {
 						interceptor.beforeTransactionCompletion( currentHibernateTransaction );
 					}
 					catch (Throwable t) {
 						LOG.exceptionInBeforeTransactionCompletionInterceptor( t );
 					}
 				}
 
 				@Override
 				public void afterCompletion(boolean successful, boolean delayed) {
 					afterTransactionCompletion( successful, delayed );
 					if ( !isClosed() && autoCloseSessionEnabled ) {
 						managedClose();
 					}
 				}
 			};
 
 			transactionCoordinator.addObserver( transactionObserver );
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
 		if ( TRACE_ENABLED ) {
 			LOG.tracef( "Opened session at timestamp: %s", timestamp );
 		}
 
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	@Override
 	public void clear() {
 		errorIfClosed();
 		// Do not call checkTransactionSynchStatus() here -- if a delayed
 		// afterCompletion exists, it can cause an infinite loop.
 		pulseTransactionCoordinator();
 		internalClear();
 	}
 
 	private void internalClear() {
 		persistenceContext.clear();
 		actionQueue.clear();
 
 		final ClearEvent event = new ClearEvent( this );
 		for ( ClearEventListener listener : listeners( EventType.CLEAR ) ) {
 			listener.onClear( event );
 		}
 	}
 
 	@Override
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
 	@Override
 	public void close() throws HibernateException {
 		LOG.trace( "Closing session" );
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 		getEventListenerManager().end();
 
 		try {
 			if ( !isTransactionCoordinatorShared ) {
 				jdbcCoordinator.close();
 				return;
 			}
 			else {
 				if ( getActionQueue().hasBeforeTransactionActions() || getActionQueue().hasAfterTransactionActions() ) {
 					LOG.warn(
 							"On close, shared Session had before / after transaction actions that have not yet been processed"
 					);
 				}
 				return;
 			}
 		}
 		finally {
 			setClosed();
 			cleanup();
 		}
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
 	@Override
 	public boolean isOpen() {
 		checkTransactionSynchStatus();
 		return !isClosed();
 	}
 
 	private boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getFlushMode() );
 	}
 
 	private void managedFlush() {
 		if ( isClosed() ) {
 			LOG.trace( "Skipping auto-flush due to session closed" );
 			return;
 		}
 		LOG.trace( "Automatically flushing session" );
 		flush();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		if ( isClosed() ) {
 			return false;
 		}
 		else if ( sessionOwner != null ) {
 			return sessionOwner.shouldAutoCloseSession();
 		}
 		else {
 			return isAutoCloseSessionEnabled();
 		}
 	}
 
 	private void managedClose() {
 		LOG.trace( "Automatically closing session" );
 		close();
 	}
 
 	@Override
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return this.jdbcCoordinator.getLogicalConnection().getPhysicalConnection();
 	}
 
 	@Override
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && this.jdbcCoordinator.getLogicalConnection().isOpen();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.getTransactionDriverControl()
 				.getStatus() == TransactionStatus.ACTIVE && transactionCoordinator.isJoined();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Disconnecting session" );
 		return this.jdbcCoordinator.getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Reconnecting session" );
 		checkTransactionSynchStatus();
 		this.jdbcCoordinator.getLogicalConnection().manualReconnect( conn );
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		errorIfClosed();
 		autoClear = enabled;
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		errorIfClosed();
 		autoJoinTransactions = false;
 	}
 
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and,
 	 * if there is not, flush if necessary, make sure the connection has
 	 * been committed (if it is not in autocommit mode) and run the after
 	 * completion processing
 	 *
 	 * @param success Was the operation a success
 	 */
 	public void afterOperation(boolean success) {
 		if ( ! isTransactionInProgress() ) {
 			jdbcCoordinator.afterTransaction();
 		}
 	}
 
 	@Override
 	public SessionEventListenerManagerImpl getEventListenerManager() {
 		return sessionEventsManager;
 	}
 
 	@Override
 	public void addEventListeners(SessionEventListener... listeners) {
 		getEventListenerManager().addListener( listeners );
 	}
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	private void cleanup() {
 		persistenceContext.clear();
 	}
 
 	@Override
 	public LockMode getCurrentLockMode(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object == null ) {
 			throw new NullPointerException( "null object passed to getCurrentLockMode()" );
 		}
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation(this);
 			if ( object == null ) {
 				return LockMode.NONE;
 			}
 		}
 		EntityEntry e = persistenceContext.getEntry(object);
 		if ( e == null ) {
 			throw new TransientObjectException( "Given object not associated with the session" );
 		}
 		if ( e.getStatus() != Status.MANAGED ) {
 			throw new ObjectDeletedException(
 					"The given object was deleted",
 					e.getId(),
 					e.getPersister().getEntityName()
 				);
 		}
 		return e.getLockMode();
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		// todo : should this get moved to PersistentContext?
 		// logically, is PersistentContext the "thing" to which an interceptor gets attached?
 		final Object result = persistenceContext.getEntity(key);
 		if ( result == null ) {
 			final Object newObject = interceptor.getEntity( key.getEntityName(), key.getIdentifier() );
 			if ( newObject != null ) {
 				lock( newObject, LockMode.NONE );
 			}
 			return newObject;
 		}
 		else {
 			return result;
 		}
 	}
 
 	private void checkNoUnresolvedActionsBeforeOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 && actionQueue.hasUnresolvedEntityInsertActions() ) {
 			throw new IllegalStateException( "There are delayed insert actions before operation as cascade level 0." );
 		}
 	}
 
 	private void checkNoUnresolvedActionsAfterOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 ) {
 			actionQueue.checkNoUnresolvedActionsAfterOperation();
 		}
 		delayedAfterCompletion();
 	}
 
 	private void delayedAfterCompletion() {
 		if(transactionCoordinator instanceof JtaTransactionCoordinatorImpl) {
 			((JtaTransactionCoordinatorImpl)transactionCoordinator).getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
 		}
 	}
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
 	@Override
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 	private <T> Iterable<T> listeners(EventType<T> type) {
 		return eventListenerGroup( type ).listeners();
 	}
 
 	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
 		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
 	@Override
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object obj) throws HibernateException {
 		update( null, obj );
 	}
 
 	@Override
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( entityName, object, lockMode, this ) );
 	}
 
 	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl(lockOptions);
 	}
 
 	@Override
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( object, lockMode, this ) );
 	}
 
 	private void fireLock(String entityName, Object object, LockOptions options) {
 		fireLock( new LockEvent( entityName, object, options, this ) );
 	}
 
 	private void fireLock( Object object, LockOptions options) {
 		fireLock( new LockEvent( object, options, this ) );
 	}
 
 	private void fireLock(LockEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
 			listener.onLock( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	@Override
 	public void persist(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// persistOnFlush() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persistOnFlush(String entityName, Object object)
 			throws HibernateException {
 		firePersistOnFlush( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	@Override
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersistOnFlush(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
 	@Override
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( object, this ) );
 	}
 
 	@Override
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
 		if ( TRACE_ENABLED && persistenceContext.isRemovingOrphanBeforeUpates() ) {
 			logRemoveOrphanBeforeUpdates( "before continuing", entityName, object );
 		}
 		fireDelete(
 				new DeleteEvent(
 						entityName,
 						object,
 						isCascadeDeleteEnabled,
 						persistenceContext.isRemovingOrphanBeforeUpates(),
 						this
 				),
 				transientEntities
 		);
 		if ( TRACE_ENABLED && persistenceContext.isRemovingOrphanBeforeUpates() ) {
 			logRemoveOrphanBeforeUpdates( "after continuing", entityName, object );
 		}
 	}
 
 	@Override
 	public void removeOrphanBeforeUpdates(String entityName, Object child) {
 		// TODO: The removeOrphan concept is a temporary "hack" for HHH-6484.  This should be removed once action/task
 		// ordering is improved.
 		if ( TRACE_ENABLED ) {
 			logRemoveOrphanBeforeUpdates( "begin", entityName, child );
 		}
 		persistenceContext.beginRemoveOrphanBeforeUpdates();
 		try {
 			fireDelete( new DeleteEvent( entityName, child, false, true, this ) );
 		}
 		finally {
 			persistenceContext.endRemoveOrphanBeforeUpdates();
 			if ( TRACE_ENABLED ) {
 				logRemoveOrphanBeforeUpdates( "end", entityName, child );
 			}
 		}
 	}
 
 	private void logRemoveOrphanBeforeUpdates(String timing, String entityName, Object entity) {
 		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		LOG.tracef(
 				"%s remove orphan before updates: [%s]",
 				timing,
 				entityEntry == null ? entityName : MessageHelper.infoString( entityName, entityEntry.getId() )
 		);
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, object, this);
 		fireLoad( event, LoadEventListener.RELOAD );
 	}
 
 	@Override
 	public Object load(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).getReference( id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).load( id );
 	}
 
 	/**	
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
 			LOG.debugf( "Initializing proxy: %s", MessageHelper.infoString( persister, id, getFactory() ) );
 		}
 
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, LoadEventListener.IMMEDIATE_LOAD);
 		return event.getResult();
 	}
 
 	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 						? LoadEventListener.INTERNAL_LOAD_EAGER
 						: LoadEventListener.INTERNAL_LOAD_LAZY;
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad( event, type );
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( event.getResult(), id, entityName );
 		}
 		return event.getResult();
 	}
 
 	@Override
 	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	@Override
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).load( id );
 	}
 	
 	@Override
 	public IdentifierLoadAccessImpl byId(String entityName) {
 		return new IdentifierLoadAccessImpl( entityName );
 	}
 
 	@Override
-	public IdentifierLoadAccessImpl byId(Class entityClass) {
+	public <T> IdentifierLoadAccessImpl<T> byId(Class<T> entityClass) {
 		return new IdentifierLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return new NaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
 		return new NaturalIdLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return new SimpleNaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
 		return new SimpleNaturalIdLoadAccessImpl( entityClass );
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent( object, lockMode, this ) );
 	}
 
 	@Override
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		refresh( null, object, lockOptions );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( entityName, object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
 	}
 
 	@Override
 	public void replicate(String entityName, Object obj, ReplicationMode replicationMode)
 	throws HibernateException {
 		fireReplicate( new ReplicateEvent( entityName, obj, replicationMode, this ) );
 	}
 
 	private void fireReplicate(ReplicateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ReplicateEventListener listener : listeners( EventType.REPLICATE ) ) {
 			listener.onReplicate( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistent instances are okay)
 	 */
 	@Override
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
 	private void fireEvict(EvictEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( EvictEventListener listener : listeners( EventType.EVICT ) ) {
 			listener.onEvict( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	/**
 	 * detect in-memory changes, determine if the changes are to tables
 	 * named in the query and, if so, complete execution the flush
 	 */
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		errorIfClosed();
 		if ( ! isTransactionInProgress() ) {
 			// do not auto-flush while outside a transaction
 			return false;
 		}
 		AutoFlushEvent event = new AutoFlushEvent( querySpaces, this );
 		listeners( EventType.AUTO_FLUSH );
 		for ( AutoFlushEventListener listener : listeners( EventType.AUTO_FLUSH ) ) {
 			listener.onAutoFlush( event );
 		}
 		return event.isFlushRequired();
 	}
 
 	@Override
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			LOG.debug( "Session dirty (scheduled updates and insertions)" );
 			return true;
 		}
 		DirtyCheckEvent event = new DirtyCheckEvent( this );
 		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
 			listener.onDirtyCheck( event );
 		}
 		delayedAfterCompletion();
 		return event.isDirty();
 	}
 
 	@Override
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
 		FlushEvent flushEvent = new FlushEvent( this );
 		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 			listener.onFlush( flushEvent );
 		}
 		delayedAfterCompletion();
 	}
 
 	@Override
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		errorIfClosed();
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Flushing to force deletion of re-saved object: %s",
 					MessageHelper.infoString( entityEntry.getPersister(), entityEntry.getId(), getFactory() ) );
 		}
 
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new ObjectDeletedException(
 				"deleted object would be re-saved by cascade (remove deleted object from associations)",
 				entityEntry.getId(),
 				entityEntry.getPersister().getEntityName()
 			);
 		}
 
 		flush();
 	}
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if (plan == null) {
 			plan = getHQLQueryPlan( query, false );
 		}
 		
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
-			afterOperation(success);
+			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return result;
 	}
 
 	@Override
     public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification,
             QueryParameters queryParameters) throws HibernateException {
         errorIfClosed();
         checkTransactionSynchStatus();
         queryParameters.validateParameters();
         NativeSQLQueryPlan plan = getNativeSQLQueryPlan( nativeQuerySpecification );
 
 
         autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
 
         boolean success = false;
         int result = 0;
         try {
             result = plan.performExecuteUpdate(queryParameters, this);
             success = true;
         } finally {
             afterOperation( success );
     		delayedAfterCompletion();
         }
         return result;
     }
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, true );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return plan.performIterate( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return plan.performScroll( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	@Override
 	public Query createFilter(Object collection, String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		CollectionFilterImpl filter = new CollectionFilterImpl(
 				queryString,
 		        collection,
 		        this,
 		        getFilterQueryPlan( collection, queryString, null, false ).getParameterMetadata()
 		);
 		filter.setComment( queryString );
 		delayedAfterCompletion();
 		return filter;
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Query query = super.getNamedQuery( queryName );
 		delayedAfterCompletion();
 		return query;
 	}
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	@Override
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		delayedAfterCompletion();
 		return result;
 	}
 
 	@Override
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting flush mode to: {0}", flushMode );
 		this.flushMode = flushMode;
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting cache mode to: {0}", cacheMode );
 		this.cacheMode= cacheMode;
 	}
 
 	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		errorIfClosed();
 		if (entityName==null) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 			}
 			catch( HibernateException e ) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch( HibernateException e2 ) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			if ( entry == null ) {
 				throw new TransientObjectException( "The instance was not associated with this session" );
 			}
 			return entry.getId();
 		}
 	}
 
 	/**
 	 * Get the id value for an object that is actually associated with the session. This
 	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
 	 */
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		if ( object instanceof HibernateProxy ) {
 			return getProxyIdentifier( object );
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			return entry != null ? entry.getId() : null;
 		}
 	}
 
 	private Serializable getProxyIdentifier(Object proxy) {
 		return ( (HibernateProxy) proxy ).getHibernateLazyInitializer().getIdentifier();
 	}
 
 	private FilterQueryPlan getFilterQueryPlan(
 			Object collection,
 			String filter,
 			QueryParameters parameters,
 			boolean shallow) throws HibernateException {
 		if ( collection == null ) {
 			throw new NullPointerException( "null collection passed to filter" );
 		}
 
 		CollectionEntry entry = persistenceContext.getCollectionEntryOrNull( collection );
 		final CollectionPersister roleBeforeFlush = (entry == null) ? null : entry.getLoadedPersister();
 
 		FilterQueryPlan plan = null;
 		if ( roleBeforeFlush == null ) {
 			// if it was previously unreferenced, we need to flush in order to
 			// get its state into the database in order to execute query
 			flush();
 			entry = persistenceContext.getCollectionEntryOrNull( collection );
 			CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 			if ( roleAfterFlush == null ) {
 				throw new QueryException( "The collection was unreferenced" );
 			}
-			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
+			plan = factory.getQueryPlanCache().getFilterQueryPlan(
+					filter,
+					roleAfterFlush.getRole(),
+					shallow,
+					getLoadQueryInfluencers().getEnabledFilters()
+			);
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
-			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleBeforeFlush.getRole(), shallow, getEnabledFilters() );
+			plan = factory.getQueryPlanCache().getFilterQueryPlan(
+					filter,
+					roleBeforeFlush.getRole(),
+					shallow,
+					getLoadQueryInfluencers().getEnabledFilters()
+			);
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely after the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
-					plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
+					plan = factory.getQueryPlanCache().getFilterQueryPlan(
+							filter,
+							roleAfterFlush.getRole(),
+							shallow,
+							getLoadQueryInfluencers().getEnabledFilters()
+					);
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = Collections.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		Iterator itr = plan.performIterate( queryParameters, this );
 		delayedAfterCompletion();
 		return itr;
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	@Override
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 				
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteriaImpl );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 				);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired(spaces);
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 
 		return results;
 	}
 
 	/**
 	 * Checks to see if the CriteriaImpl is a naturalId lookup that can be done via
 	 * NaturalIdLoadAccess
 	 *
 	 * @param criteria The criteria to check as a complete natural identifier lookup.
 	 *
 	 * @return A fully configured NaturalIdLoadAccess or null, if null is returned the standard CriteriaImpl execution
 	 *         should be performed
 	 */
 	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
 		// See if the criteria lookup is by naturalId
 		if ( !criteria.isLookupByNaturalKey() ) {
 			return null;
 		}
 
 		final String entityName = criteria.getEntityOrClassName();
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
 		// queries did no natural id validation
 		if ( !entityPersister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will
 		// return an instanceof NaturalIdentifier
 		final CriterionEntry criterionEntry = (CriterionEntry) criteria.iterateExpressionEntries().next();
 		final NaturalIdentifier naturalIdentifier = (NaturalIdentifier) criterionEntry.getCriterion();
 
 		final Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
 		final int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
 
 		// Verify the NaturalIdentifier criterion includes all naturalId properties, first check that the property counts match
 		if ( naturalIdentifierProperties.length != naturalIdValues.size() ) {
 			return null;
 		}
 
 		final String[] propertyNames = entityPersister.getPropertyNames();
 		final NaturalIdLoadAccess naturalIdLoader = this.byNaturalId( entityName );
 
 		// Build NaturalIdLoadAccess and in the process verify all naturalId properties were specified
 		for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 			final String naturalIdProperty = propertyNames[naturalIdentifierProperties[i]];
 			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
 
 			if ( naturalIdValue == null ) {
 				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
 				return null;
 			}
 
 			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
 		}
 
 		// Critera query contains a valid naturalId, use the new API
 		LOG.warn( "Session.byNaturalId(" + entityName
 				+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria" );
 
 		return naturalIdLoader;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			//do not use proxiesByKey, since not all
 			//proxies that point to this session's
 			//instances are in that collection!
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				//if it is an uninitialized proxy, pointing
 				//with this session, then when it is accessed,
 				//the underlying instance will be "contained"
 				return li.getSession()==this;
 			}
 			else {
 				//if it is initialized, see if the underlying
 				//instance is contained, since we need to
 				//account for the fact that it might have been
 				//evicted
 				object = li.getImplementation();
 			}
 		}
 		// A session is considered to contain an entity only if the entity has
 		// an entry in the session's persistence context and the entry reports
 		// that the entity has not been removed
 		EntityEntry entry = persistenceContext.getEntry( object );
 		delayedAfterCompletion();
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Scroll SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list(this, queryParameters);
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			delayedAfterCompletion();
 			afterOperation(success);
 		}
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			LazyInitializer initializer = ( ( HibernateProxy ) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidentally initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if (entry==null) {
 			return guessEntityName(object);
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 
 	@Override
 	public String getEntityName(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if (object instanceof HibernateProxy) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException("proxy was not associated with the session");
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance before flushing: " +
 				guessEntityName(object)
 			);
 	}
 
 	@Override
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	@Override
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		this.jdbcCoordinator.cancelLastQuery();
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	@Override
 	public String toString() {
 		StringBuilder buf = new StringBuilder(500)
 			.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append(persistenceContext)
 				.append(";")
 				.append(actionQueue);
 		}
 		else {
 			buf.append("<closed>");
 		}
 		return buf.append(')').toString();
 	}
 
 	@Override
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	@Override
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	@Override
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	@Override
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly( entity, readOnly );
 	}
 
 	@Override
 	public void doWork(final Work work) throws HibernateException {
 		WorkExecutorVisitable<Void> realWork = new WorkExecutorVisitable<Void>() {
 			@Override
 			public Void accept(WorkExecutor<Void> workExecutor, Connection connection) throws SQLException {
 				workExecutor.executeWork( work, connection );
 				return null;
 			}
 		};
 		doWork( realWork );
 	}
 
 	@Override
 	public <T> T doReturningWork(final ReturningWork<T> work) throws HibernateException {
 		WorkExecutorVisitable<T> realWork = new WorkExecutorVisitable<T>() {
 			@Override
 			public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 				return workExecutor.executeReturningWork( work, connection );
 			}
 		};
 		return doWork( realWork );
 	}
 
 	private <T> T doWork(WorkExecutorVisitable<T> work) throws HibernateException {
 		return this.jdbcCoordinator.coordinateWork( work );
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return this.jdbcCoordinator;
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	@Override
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	@Override
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
-	@Override
-	public Object getFilterParameterValue(String filterParameterName) {
-		errorIfClosed();
-		checkTransactionSynchStatus();
-		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
-	}
-
-	@Override
-	public Type getFilterParameterType(String filterParameterName) {
-		errorIfClosed();
-		checkTransactionSynchStatus();
-		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
-	}
-
-	@Override
-	public Map getEnabledFilters() {
-		errorIfClosed();
-		checkTransactionSynchStatus();
-		return loadQueryInfluencers.getEnabledFilters();
-	}
-
-
-	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Override
-	public String getFetchProfile() {
-		checkTransactionSynchStatus();
-		return loadQueryInfluencers.getInternalFetchProfile();
-	}
-
-	@Override
-	public void setFetchProfile(String fetchProfile) {
-		errorIfClosed();
-		checkTransactionSynchStatus();
-		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
-	}
-
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 	private void checkTransactionSynchStatus() {
 		pulseTransactionCoordinator();
 		delayedAfterCompletion();
 	}
 
 	private void pulseTransactionCoordinator() {
 		if ( !isClosed() ) {
 			transactionCoordinator.pulse();
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
 		LOG.trace( "Deserializing session" );
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		connectionReleaseMode = ConnectionReleaseMode.parse( (String) ois.readObject() );
 		autoClear = ois.readBoolean();
 		autoJoinTransactions = ois.readBoolean();
 		flushMode = FlushMode.valueOf( (String) ois.readObject() );
 		cacheMode = CacheMode.valueOf( (String) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = (Interceptor) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		this.jdbcSessionContext = new JdbcSessionContextImpl( factory, statementInspector );
 		sessionOwner = (SessionOwner) ois.readObject();
 
 		jdbcCoordinator = JdbcCoordinatorImpl.deserialize( ois, this );
 
 		this.transactionCoordinator = getTransactionCoordinatorBuilder().buildTransactionCoordinator( jdbcCoordinator, this );
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = (LoadQueryInfluencers) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		for ( String filterName : loadQueryInfluencers.getEnabledFilterNames() ) {
 			((FilterImpl) loadQueryInfluencers.getEnabledFilter( filterName )).afterDeserialize( factory );
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( !jdbcCoordinator.isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
 		LOG.trace( "Serializing session" );
 
 		oos.defaultWriteObject();
 
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeBoolean( autoJoinTransactions );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.name() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 		oos.writeObject( sessionOwner );
 
 		jdbcCoordinator.serialize( oos );
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	@Override
 	public JdbcSessionContext getJdbcSessionContext() {
 		return this.jdbcSessionContext;
 	}
 
 	@Override
 	public void beforeTransactionCompletion() {
 		LOG.tracef( "SessionImpl#beforeTransactionCompletion()" );
 		flushBeforeTransactionCompletion();
 		actionQueue.beforeTransactionCompletion();
 		try {
 			interceptor.beforeTransactionCompletion( currentHibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInBeforeTransactionCompletionInterceptor( t );
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		LOG.tracef( "SessionImpl#afterTransactionCompletion(successful=%s, delayed=%s)", successful, delayed );
 
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 
 		getEventListenerManager().transactionCompletion( successful );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().endTransaction( successful );
 		}
 
 		try {
 			interceptor.afterTransactionCompletion( currentHibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInAfterTransactionCompletionInterceptor( t );
 		}
 
 		if ( !delayed ) {
 			if ( shouldAutoClose() && !isClosed() ) {
 				managedClose();
 			}
 		}
 
 		if ( autoClear ) {
 			internalClear();
 		}
 	}
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		@Override
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			// Always use NonContextualLobCreator.  If ContextualLobCreator is
 			// used both here and in WrapperOptions, 
 			return NonContextualLobCreator.INSTANCE;
 		}
 
 		@Override
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		@Override
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		@Override
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		@Override
 		public NClob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		@Override
 		public NClob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private static class SharedSessionBuilderImpl extends SessionFactoryImpl.SessionBuilderImpl implements SharedSessionBuilder {
 		private final SessionImpl session;
 		private boolean shareTransactionContext;
 
 		private SharedSessionBuilderImpl(SessionImpl session) {
 			super( session.factory );
 			this.session = session;
 			super.owner( session.sessionOwner );
 			super.tenantIdentifier( session.getTenantIdentifier() );
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			// todo : is this always true?  Or just in the case of sharing JDBC resources?
 			throw new SessionException( "Cannot redefine tenant identifier on child session" );
 		}
 
 		@Override
 		protected TransactionCoordinator getTransactionCoordinator() {
 			return shareTransactionContext ? session.transactionCoordinator : super.getTransactionCoordinator();
 		}
 
 		@Override
 		protected JdbcCoordinatorImpl getJdbcCoordinator() {
 			return shareTransactionContext ? session.jdbcCoordinator : super.getJdbcCoordinator();
 		}
 
 		@Override
 		protected Transaction getTransaction() {
 			return shareTransactionContext ? session.currentHibernateTransaction : super.getTransaction();
 		}
 
 		@Override
 		protected ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
 			return shareTransactionContext ?
 					session.getActionQueue().getTransactionCompletionProcesses() :
 					super.getTransactionCompletionProcesses();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor() {
 			return interceptor( session.interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder connection() {
 			this.shareTransactionContext = true;
 			return this;
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode() {
 			return connectionReleaseMode( session.connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions() {
 			return autoJoinTransactions( session.autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose() {
 			return autoClose( session.autoCloseSessionEnabled );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion() {
 			return flushBeforeCompletion( session.flushBeforeCompletionEnabled );
 		}
 
 		/**
 		 * @deprecated Use {@link #connection()} instead
 		 */
 		@Override
 		@Deprecated
 		public SharedSessionBuilder transactionContext() {
 			return connection();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor(Interceptor interceptor) {
 			return (SharedSessionBuilder) super.interceptor( interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder noInterceptor() {
 			return (SharedSessionBuilder) super.noInterceptor();
 		}
 
 		@Override
 		public SharedSessionBuilder statementInspector(StatementInspector statementInspector) {
 			return (SharedSessionBuilder) super.statementInspector( statementInspector );
 		}
 
 		@Override
 		public SharedSessionBuilder connection(Connection connection) {
 			return (SharedSessionBuilder) super.connection( connection );
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			return (SharedSessionBuilder) super.connectionReleaseMode( connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			return (SharedSessionBuilder) super.autoJoinTransactions( autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose(boolean autoClose) {
 			return (SharedSessionBuilder) super.autoClose( autoClose );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			return (SharedSessionBuilder) super.flushBeforeCompletion( flushBeforeCompletion );
 		}
 
 		@Override
 		public SharedSessionBuilder eventListeners(SessionEventListener... listeners) {
 			super.eventListeners( listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			super.clearEventListeners();
 			return this;
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		@Override
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			for ( EntityNameResolver resolver : factory.iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			// the old-time stand-by...
 			return entity.getClass().getName();
 		}
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy(lo, lockOptions);
 		}
 
 		@Override
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		@Override
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		@Override
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		@Override
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		@Override
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		@Override
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		@Override
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 
 		@Override
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 
-	private class IdentifierLoadAccessImpl implements IdentifierLoadAccess {
+	private class IdentifierLoadAccessImpl<T> implements IdentifierLoadAccess<T> {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 
 		private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		private IdentifierLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
-		private IdentifierLoadAccessImpl(Class entityClass) {
-			this( entityClass.getName() );
+		private IdentifierLoadAccessImpl(Class<T> entityClass) {
+			this( locateEntityPersister( entityClass ) );
 		}
 
 		@Override
-		public final IdentifierLoadAccessImpl with(LockOptions lockOptions) {
+		public final IdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
-		public final Object getReference(Serializable id) {
+		@SuppressWarnings("unchecked")
+		public final T getReference(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.LOAD );
-				return event.getResult();
+				return (T) event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.LOAD );
 				if ( event.getResult() == null ) {
 					getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityPersister.getEntityName(), id );
 				}
 				success = true;
-				return event.getResult();
+				return (T) event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 
 		@Override
-		public final Object load(Serializable id) {
+		@SuppressWarnings("unchecked")
+		public final T load(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.GET );
-				return event.getResult();
+				return (T) event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.GET );
 				success = true;
 			}
 			catch (ObjectNotFoundException e) {
 				// if session cache contains proxy for non-existing object
 			}
 			finally {
 				afterOperation( success );
 			}
-			return event.getResult();
+			return (T) event.getResult();
 		}
 	}
 
+	private EntityPersister locateEntityPersister(Class entityClass) {
+		return factory.locateEntityPersister( entityClass );
+	}
+
 	private EntityPersister locateEntityPersister(String entityName) {
-		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
-		if ( entityPersister == null ) {
-			throw new HibernateException( "Unable to locate persister: " + entityName );
-		}
-		return entityPersister;
+		return factory.locateEntityPersister( entityName );
 	}
 
-	private abstract class BaseNaturalIdLoadAccessImpl  {
+	private abstract class BaseNaturalIdLoadAccessImpl<T>  {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private boolean synchronizationEnabled = true;
 
 		private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
-		private BaseNaturalIdLoadAccessImpl(String entityName) {
-			this( locateEntityPersister( entityName ) );
-		}
-
-		private BaseNaturalIdLoadAccessImpl(Class entityClass) {
-			this( entityClass.getName() );
-		}
-
-		public BaseNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
+		public BaseNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		protected void synchronizationEnabled(boolean synchronizationEnabled) {
 			this.synchronizationEnabled = synchronizationEnabled;
 		}
 
 		protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
 			performAnyNeededCrossReferenceSynchronizations();
 
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 
 			if ( event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE ) {
 				return null;
 			}
 			else {
 				return event.getEntityId();
 			}
 		}
 
 		protected void performAnyNeededCrossReferenceSynchronizations() {
 			if ( ! synchronizationEnabled ) {
 				// synchronization (this process) was disabled
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing
 				return;
 			}
 			if ( ! isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
 				if ( entry == null ) {
 					if ( debugEnabled ) {
 						LOG.debug(
 								"Cached natural-id/pk resolution linked to null EntityEntry in persistence context : "
 										+ MessageHelper.infoString( entityPersister, pk, getFactory() )
 						);
 					}
 					continue;
 				}
 
 				if ( !entry.requiresDirtyCheck( entity ) ) {
 					continue;
 				}
 
 				// MANAGED is the only status we care about here...
 				if ( entry.getStatus() != Status.MANAGED ) {
 					continue;
 				}
 
 				getPersistenceContext().getNaturalIdHelper().handleSynchronization(
 						entityPersister,
 						pk,
 						entity
 				);
 			}
 		}
 
 		protected final IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 
 		protected EntityPersister entityPersister() {
 			return entityPersister;
 		}
 	}
 
-	private class NaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements NaturalIdLoadAccess {
+	private class NaturalIdLoadAccessImpl<T> extends BaseNaturalIdLoadAccessImpl<T> implements NaturalIdLoadAccess<T> {
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
-			this( entityClass.getName() );
+			this( locateEntityPersister( entityClass ) );
 		}
 		
 		@Override
-		public NaturalIdLoadAccessImpl with(LockOptions lockOptions) {
-			return (NaturalIdLoadAccessImpl) super.with( lockOptions );
+		public NaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
+			return (NaturalIdLoadAccessImpl<T>) super.with( lockOptions );
 		}
 
 		@Override
-		public NaturalIdLoadAccess using(String attributeName, Object value) {
+		public NaturalIdLoadAccess<T> using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		@Override
-		public NaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
+		public NaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
-		public final Object getReference() {
+		@SuppressWarnings("unchecked")
+		public final T getReference() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
-			return this.getIdentifierLoadAccess().getReference( entityId );
+			return (T) this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
-		public final Object load() {
+		@SuppressWarnings("unchecked")
+		public final T load() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
-				return this.getIdentifierLoadAccess().load( entityId );
+				return (T) this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 
-	private class SimpleNaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements SimpleNaturalIdLoadAccess {
+	private class SimpleNaturalIdLoadAccessImpl<T> extends BaseNaturalIdLoadAccessImpl<T> implements SimpleNaturalIdLoadAccess<T> {
 		private final String naturalIdAttributeName;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 
 			if ( entityPersister.getNaturalIdentifierProperties().length != 1 ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a simple natural id", entityPersister.getEntityName() )
 				);
 			}
 
 			final int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
 			this.naturalIdAttributeName = entityPersister.getPropertyNames()[ naturalIdAttributePosition ];
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
-			this( entityClass.getName() );
+			this( locateEntityPersister( entityClass ) );
 		}
 
 		@Override
-		public final SimpleNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
-			return (SimpleNaturalIdLoadAccessImpl) super.with( lockOptions );
+		public final SimpleNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
+			return (SimpleNaturalIdLoadAccessImpl<T>) super.with( lockOptions );
 		}
 		
 		private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
 			return Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 		}
 
 		@Override
-		public SimpleNaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
+		public SimpleNaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
-		public Object getReference(Object naturalIdValue) {
+		@SuppressWarnings("unchecked")
+		public T getReference(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
-			return this.getIdentifierLoadAccess().getReference( entityId );
+			return (T) this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
-		public Object load(Object naturalIdValue) {
+		@SuppressWarnings("unchecked")
+		public T load(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
-				return this.getIdentifierLoadAccess().load( entityId );
+				return (T) this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 
 	@Override
 	public void afterTransactionBegin() {
 		errorIfClosed();
 		interceptor.afterTransactionBegin( currentHibernateTransaction );
 	}
 
 	@Override
 	public void flushBeforeTransactionCompletion() {
 		boolean flush = false;
 		try {
 			flush = (!isFlushModeNever() &&
 					!flushBeforeCompletionEnabled) || (
 					!isClosed()
 							&& !isFlushModeNever()
 							&& flushBeforeCompletionEnabled
 							&& !JtaStatusHelper.isRollback(
 							getSessionFactory().getSettings()
 									.getJtaPlatform()
 									.getCurrentStatus()
 					));
 		}
 		catch (SystemException se) {
 			throw new HibernateException( "could not determine transaction status in beforeCompletion()", se );
 		}
 		if ( flush ) {
 			managedFlush();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 7ffafc20a0..28803ead90 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,803 +1,787 @@
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
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
-import java.util.Map;
 import javax.transaction.SystemException;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.Transaction;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
-import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StatelessSessionImpl extends AbstractSessionImpl implements StatelessSession {
     private static final CoreMessageLogger LOG = CoreLogging.messageLogger( StatelessSessionImpl.class );
 
 	private TransactionCoordinator transactionCoordinator;
 
 	private transient JdbcCoordinator jdbcCoordinator;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 	private long timestamp;
 	private JdbcSessionContext jdbcSessionContext;
 
+	private LoadQueryInfluencers statelessLoadQueryInfluencers = new LoadQueryInfluencers( null ) {
+		@Override
+		public String getInternalFetchProfile() {
+			return null;
+		}
+
+		@Override
+		public void setInternalFetchProfile(String internalFetchProfile) {
+		}
+	};
+
 	StatelessSessionImpl(
 			Connection connection,
 			String tenantIdentifier,
 			SessionFactoryImpl factory) {
 		this( connection, tenantIdentifier, factory, factory.getSettings().getRegionFactory().nextTimestamp() );
 	}
 
 	StatelessSessionImpl(
 			Connection connection,
 			String tenantIdentifier,
 			SessionFactoryImpl factory,
 			long timestamp) {
 		super( factory, tenantIdentifier );
 		this.jdbcSessionContext = new JdbcSessionContextImpl(
 				factory,
 				new StatementInspector() {
 					@Override
 					public String inspect(String sql) {
 						return null;
 					}
 				}
 		);
 		this.jdbcCoordinator = new JdbcCoordinatorImpl( connection, this );
 
 		this.transactionCoordinator = getTransactionCoordinatorBuilder().buildTransactionCoordinator( jdbcCoordinator, this );
 		this.currentHibernateTransaction = getTransaction();
 		this.timestamp = timestamp;
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return this.jdbcCoordinator;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	// inserts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert(null, entity);
 	}
 
 	@Override
 	public Serializable insert(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifierGenerator().generate( this, entity );
 		Object[] state = persister.getPropertyValues( entity );
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(
 					state, persister.getVersionProperty(), persister.getVersionType(), this
 			);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state );
 			}
 		}
 		if ( id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			id = persister.insert(state, entity, this);
 		}
 		else {
 			persister.insert(id, state, entity, this);
 		}
 		persister.setIdentifier( entity, id, this );
 		return id;
 	}
 
 
 	// deletes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete( null, entity );
 	}
 
 	@Override
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion( entity );
 		persister.delete( id, version, entity, this );
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object entity) {
 		errorIfClosed();
 		update(null, entity);
 	}
 
 	@Override
 	public void update(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues( entity );
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion( entity );
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion(state, newVersion, persister);
 			persister.setPropertyValues( entity, state );
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update(id, state, null, false, null, oldVersion, entity, null, this);
 	}
 
 
 	// loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return get(entityName, id, LockMode.NONE);
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister(entityName)
 				.load(id, null, lockMode, this);
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
 	@Override
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity, LockMode lockMode) {
 		final EntityPersister persister = this.getEntityPersister( entityName, entity );
 		final Serializable id = persister.getIdentifier( entity, this );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Refreshing transient {0}", MessageHelper.infoString( persister, id, this.getFactory() ) );
 		}
 		// TODO : can this ever happen???
 //		EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 //		if ( source.getPersistenceContext().getEntry( key ) != null ) {
 //			throw new PersistentObjectException(
 //					"attempted to refresh transient instance when persistent " +
 //					"instance was already associated with the Session: " +
 //					MessageHelper.infoString( persister, id, source.getFactory() )
 //			);
 //		}
 
 		if ( persister.hasCache() ) {
 			final CacheKey ck = generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 			persister.getCacheAccessStrategy().evict( ck );
 		}
-
-		String previousFetchProfile = this.getFetchProfile();
+		String previousFetchProfile = this.getLoadQueryInfluencers().getInternalFetchProfile();
 		Object result = null;
 		try {
-			this.setFetchProfile( "refresh" );
+			this.getLoadQueryInfluencers().setInternalFetchProfile( "refresh" );
 			result = persister.load( id, entity, lockMode, this );
 		}
 		finally {
-			this.setFetchProfile( previousFetchProfile );
+			this.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 		}
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException("proxies cannot be fetched by a stateless session");
 	}
 
 	@Override
 	public void initializeCollection(
 			PersistentCollection collection,
 	        boolean writing) throws HibernateException {
 		throw new SessionException("collections cannot be fetched by a stateless session");
 	}
 
 	@Override
 	public Object instantiate(
 			String entityName,
 	        Serializable id) throws HibernateException {
 		errorIfClosed();
 		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
 	}
 
 	@Override
 	public Object internalLoad(
 			String entityName,
 	        Serializable id,
 	        boolean eager,
 	        boolean nullable) throws HibernateException {
 		errorIfClosed();
 		EntityPersister persister = getFactory().getEntityPersister( entityName );
 		// first, try to load it from the temp PC associated to this SS
 		Object loaded = temporaryPersistenceContext.getEntity( generateEntityKey( id, persister ) );
 		if ( loaded != null ) {
 			// we found it in the temp PC.  Should indicate we are in the midst of processing a result set
 			// containing eager fetches via join fetch
 			return loaded;
 		}
 		if ( !eager && persister.hasProxy() ) {
 			// if the metadata allowed proxy creation and caller did not request forceful eager loading,
 			// generate a proxy
 			return persister.createProxy( id, this );
 		}
 		// otherwise immediately materialize it
 		return get( entityName, id );
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	@Override
 	public void close() {
 		managedClose();
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 
 	private boolean isFlushModeNever() {
 		return false;
 	}
 
 	private void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		jdbcCoordinator.close();
 		setClosed();
 	}
 
 	private void managedFlush() {
 		errorIfClosed();
 		jdbcCoordinator.executeBatch();
 	}
 
 	private SessionEventListenerManagerImpl sessionEventsManager;
 
 	@Override
 	public SessionEventListenerManager getEventListenerManager() {
 		if ( sessionEventsManager == null ) {
 			sessionEventsManager = new SessionEventListenerManagerImpl();
 		}
 		return sessionEventsManager;
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName(object);
 	}
 
 	@Override
 	public Connection connection() {
 		errorIfClosed();
 		return jdbcCoordinator.getLogicalConnection().getPhysicalConnection();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters)
 			throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	@Override
-	public Map getEnabledFilters() {
-		return Collections.EMPTY_MAP;
-	}
-
-	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		errorIfClosed();
 		if ( entityName==null ) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 		}
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
 	@Override
-	public Type getFilterParameterType(String filterParameterName) {
-		throw new UnsupportedOperationException();
-	}
-
-	@Override
-	public Object getFilterParameterValue(String filterParameterName) {
-		throw new UnsupportedOperationException();
-	}
-
-	@Override
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	@Override
 	public long getTimestamp() {
 		return timestamp;
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return jdbcCoordinator.getLogicalConnection().isPhysicallyConnected();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return !isClosed() && transactionCoordinator.isJoined() && transactionCoordinator.getTransactionDriverControl()
 				.getStatus() == TransactionStatus.ACTIVE;
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return false;
 	}
 
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
 		if ( readOnly ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		List results = Collections.EMPTY_LIST;
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public void afterOperation(boolean success) {
 		if ( ! isTransactionInProgress() ) {
 			jdbcCoordinator.afterTransaction();
 		}
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 		        factory,
 		        criteriaImpl,
 		        entityName,
 		        getLoadQueryInfluencers()
 		);
 		return loader.scroll(this, scrollMode);
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for( int i=0; i <size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 			        factory,
 			        criteriaImpl,
 			        implementors[i],
 			        getLoadQueryInfluencers()
 			);
 		}
 
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		boolean success = false;
 		List results;
 		try {
 			results = loader.list(this, queryParameters);
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
 	@Override
 	public void flush() {
 	}
 
 	@Override
-	public String getFetchProfile() {
-		return null;
-	}
-
-	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
-		return LoadQueryInfluencers.NONE;
-	}
-
-	@Override
-	public void setFetchProfile(String name) {
+		return statelessLoadQueryInfluencers;
 	}
 
 	@Override
 	public int executeNativeUpdate(NativeSQLQuerySpecification nativeSQLQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeSQLQueryPlan(nativeSQLQuerySpecification);
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate(queryParameters, this);
 			success = true;
 		} finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public JdbcSessionContext getJdbcSessionContext() {
 		return this.jdbcSessionContext;
 	}
 
 	@Override
 	public void afterTransactionBegin() {
 
 	}
 
 	@Override
 	public void beforeTransactionCompletion() {
 		flushBeforeTransactionCompletion();
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		if ( shouldAutoClose() && !isClosed() ) {
 			managedClose();
 		}
 	}
 
 	@Override
 	public void flushBeforeTransactionCompletion() {
 		boolean flush = false;
 		try {
 			flush = (
 					!isClosed()
 							&& !isFlushModeNever()
 							&& !JtaStatusHelper.isRollback(
 							factory.getSettings()
 									.getJtaPlatform()
 									.getCurrentStatus()
 					));
 		}
 		catch (SystemException se) {
 			throw new HibernateException( "could not determine transaction status in beforeCompletion()", se );
 		}
 		if ( flush ) {
 			managedFlush();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 802f944980..ae3cc03e07 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2260 +1,2260 @@
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
 import java.util.Set;
 
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
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
 import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
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
 import org.hibernate.type.AnyType;
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
 	private String mappedByProperty;
 
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
 			Collection collectionBinding,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			PersisterCreationContext creationContext) throws MappingException, CacheException {
 
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collectionBinding.isMap()
 					? StructuredMapCacheEntry.INSTANCE
 					: StructuredCollectionCacheEntry.INSTANCE;
 		}
 		else {
 			cacheEntryStructure = UnstructuredCacheEntry.INSTANCE;
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collectionBinding.getCollectionType();
 		role = collectionBinding.getRole();
 		entityName = collectionBinding.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collectionBinding.getLoaderName();
 		nodeName = collectionBinding.getNodeName();
 		isMutable = collectionBinding.isMutable();
 		mappedByProperty = collectionBinding.getMappedByProperty();
 
 		Table table = collectionBinding.getCollectionTable();
 		fetchMode = collectionBinding.getElement().getFetchMode();
 		elementType = collectionBinding.getElement().getType();
 		// isSet = collectionBinding.isSet();
 		// isSorted = collectionBinding.isSorted();
 		isPrimitiveArray = collectionBinding.isPrimitiveArray();
 		isArray = collectionBinding.isArray();
 		subselectLoadable = collectionBinding.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 				);
 
 		int spacesSize = 1 + collectionBinding.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collectionBinding.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collectionBinding.getWhere() ) ? "( " + collectionBinding.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
 				null;
 
 		hasOrphanDelete = collectionBinding.hasOrphanDelete();
 
 		int batch = collectionBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collectionBinding.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collectionBinding.getKey().getType();
 		iter = collectionBinding.getKey().getColumnIterator();
 		int keySpan = collectionBinding.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, collectionBinding.getOwner().getRootTable() );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
 		String elemNode = collectionBinding.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
 			if ( elemNode == null ) {
 				elemNode = creationContext.getMetadata().getEntityBinding( entityName ).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collectionBinding.getElement().getColumnSpan();
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
 		iter = collectionBinding.getElement().getColumnIterator();
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
 
 		hasIndex = collectionBinding.isIndexed();
 		if ( hasIndex ) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collectionBinding;
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
 
 		hasIdentifier = collectionBinding.isIdentified();
 		if ( hasIdentifier ) {
 			if ( collectionBinding.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collectionBinding;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					creationContext.getMetadata().getIdentifierGeneratorFactory(),
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
 
 		if ( collectionBinding.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collectionBinding.getCustomSQLInsert();
 			insertCallable = collectionBinding.isCustomInsertCallable();
 			insertCheckStyle = collectionBinding.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLInsert(), insertCallable )
 					: collectionBinding.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collectionBinding.getCustomSQLUpdate();
 			updateCallable = collectionBinding.isCustomUpdateCallable();
 			updateCheckStyle = collectionBinding.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collectionBinding.getCustomSQLUpdate(), insertCallable )
 					: collectionBinding.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collectionBinding.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collectionBinding.getCustomSQLDelete();
 			deleteCallable = collectionBinding.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collectionBinding.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collectionBinding.getCustomSQLDeleteAll();
 			deleteAllCallable = collectionBinding.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString( collectionBinding.isIndexed() && !collectionBinding.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collectionBinding.isLazy();
 		isExtraLazy = collectionBinding.isExtraLazy();
 
 		isInverse = collectionBinding.isInverse();
 
 		if ( collectionBinding.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collectionBinding ).getElementClass();
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
 
 		hasOrder = collectionBinding.getOrderBy() != null;
 		if ( hasOrder ) {
 			orderByTranslation = Template.translateOrderBy(
 					collectionBinding.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collectionBinding
 		filterHelper = new FilterHelper( collectionBinding.getFilters(), factory);
 
 		// Handle any filters applied to this collectionBinding for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collectionBinding.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collectionBinding.getManyToManyWhere() ) ?
 				"( " + collectionBinding.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collectionBinding.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collectionBinding.getManyToManyOrdering(),
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
 
 	@Override
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
 
 	@Override
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
-		else if ( session.getEnabledFilters().isEmpty() ) {
+		else if ( session.getLoadQueryInfluencers().getEnabledFilters().isEmpty() ) {
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
 
 	@Override
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	@Override
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	@Override
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	@Override
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	@Override
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
 
 	@Override
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	@Override
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	@Override
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise.  needed by arrays
 	 */
 	@Override
 	public Class getElementClass() {
 		return elementClass;
 	}
 
 	@Override
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	@Override
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
 
 	@Override
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	@Override
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
 
 	@Override
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	@Override
 	public boolean isArray() {
 		return isArray;
 	}
 
 	@Override
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	@Override
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	@Override
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
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
 	@Override
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
 
 	@Override
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	@Override
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	@Override
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 	}
 
 	@Override
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
 
 	@Override
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	@Override
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	@Override
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	@Override
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	@Override
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	@Override
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
 					st = session
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( st );
 						session.getJdbcCoordinator().afterStatementExecution();
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
 
 	@Override
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
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
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( recreateBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 
 							// TODO: copy/paste from insertRows()
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 							loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
 								session
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getJdbcCoordinator().getResourceRegistry().release( st );
 								session.getJdbcCoordinator().afterStatementExecution();
 							}
 						}
 
 					}
 					i++;
 				}
 
 				LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 			}
 			else {
 				LOG.debug( "Collection was empty" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not insert collection: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	@Override
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowDeleteEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Deleting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
 		boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 		final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 		try {
 			// delete all the deleted entries
 			Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 			if ( deletes.hasNext() ) {
 				int offset = 1;
 				int count = 0;
 				while ( deletes.hasNext() ) {
 					PreparedStatement st = null;
 					boolean callable = isDeleteCallable();
 					boolean useBatch = expectation.canBeBatched();
 					String sql = getSQLDeleteRowString();
 
 					if ( useBatch ) {
 						if ( deleteBatchKey == null ) {
 							deleteBatchKey = new BasicBatchKey(
 									getRole() + "#DELETE",
 									expectation
 									);
 						}
 						st = session
 								.getJdbcCoordinator()
 								.getBatch( deleteBatchKey )
 								.getBatchStatement( sql, callable );
 					}
 					else {
 						st = session
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						expectation.prepare( st );
 
 						Object entry = deletes.next();
 						int loc = offset;
 						if ( hasIdentifier ) {
 							writeIdentifier( st, entry, loc, session );
 						}
 						else {
 							loc = writeKey( st, id, loc, session );
 							if ( deleteByIndex ) {
 								writeIndexToWhere( st, entry, loc, session );
 							}
 							else {
 								writeElementToWhere( st, entry, loc, session );
 							}
 						}
 
 						if ( useBatch ) {
 							session
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
 						}
 					}
 
 					LOG.debugf( "Done deleting collection rows: %s deleted", count );
 				}
 			}
 			else {
 				LOG.debug( "No rows to delete" );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not delete collection rows: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLDeleteRowString()
 			);
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	@Override
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( isInverse ) {
 			return;
 		}
 
 		if ( !isRowInsertEnabled() ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session )
 			);
 		}
 
 		try {
 			// insert all the new entries
 			collection.preInsert( this );
 			Iterator entries = collection.entries( this );
 			Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 			boolean callable = isInsertCallable();
 			boolean useBatch = expectation.canBeBatched();
 			String sql = getSQLInsertRowString();
 			int i = 0;
 			int count = 0;
 			while ( entries.hasNext() ) {
 				int offset = 1;
 				Object entry = entries.next();
 				PreparedStatement st = null;
 				if ( collection.needsInserting( entry, i, elementType ) ) {
 
 					if ( useBatch ) {
 						if ( insertBatchKey == null ) {
 							insertBatchKey = new BasicBatchKey(
 									getRole() + "#INSERT",
 									expectation
 									);
 						}
 						if ( st == null ) {
 							st = session
 									.getJdbcCoordinator()
 									.getBatch( insertBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 					}
 					else {
 						st = session
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						offset += expectation.prepare( st );
 						// TODO: copy/paste from recreate()
 						offset = writeKey( st, id, offset, session );
 						if ( hasIdentifier ) {
 							offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 						}
 						if ( hasIndex /* && !indexIsFormula */) {
 							offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 						}
 						writeElement( st, collection.getElement( entry ), offset, session );
 
 						if ( useBatch ) {
 							session.getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 						}
 						collection.afterRowInsert( this, entry, i );
 						count++;
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							session.getJdbcCoordinator().getResourceRegistry().release( st );
 							session.getJdbcCoordinator().afterStatementExecution();
 						}
 					}
 				}
 				i++;
 			}
 			LOG.debugf( "Done inserting rows: %s inserted", count );
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper.convert(
 					sqle,
 					"could not insert collection rows: " +
 							MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	@Override
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	@Override
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	@Override
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	@Override
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	@Override
 	public abstract boolean isManyToMany();
 
 	@Override
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	@Override
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	@Override
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	@Override
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	@Override
 	public String getName() {
 		return getRole();
 	}
 
 	@Override
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
 	@Override
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	@Override
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	@Override
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	/**
 	 * Process queued operations within the PersistentCollection.
 	 *
 	 * @param collection The collection
 	 * @param key The collection key
 	 * @param nextIndex The next index to write
 	 * @param session The session
 	 * @throws HibernateException
 	 *
 	 * @deprecated Use {@link #doProcessQueuedOps(org.hibernate.collection.spi.PersistentCollection, java.io.Serializable, org.hibernate.engine.spi.SessionImplementor)}
 	 */
 	@Deprecated
 	protected void doProcessQueuedOps(PersistentCollection collection, Serializable key,
 			int nextIndex, SessionImplementor session)
 			throws HibernateException {
 		doProcessQueuedOps( collection, key, session );
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException;
 
 	@Override
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(
 			String alias,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	@Override
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	@Override
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	@Override
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	@Override
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	@Override
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
-		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
-				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
+		return filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) ||
+				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	@Override
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String[] rawAliases = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String[] result = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	@Override
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	@Override
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 			);
 		}
 	}
 
 	@Override
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 *
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as composite" );
 				}
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "index";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getIndexType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes( this );
 					}
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new IllegalStateException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getElementType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java b/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java
index cf5bbd8481..f52df4d546 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java
@@ -1,760 +1,766 @@
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
 package org.hibernate.test.dialect.functional.cache;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.Query;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.Cache71Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.test.legacy.Blobber;
 import org.hibernate.test.legacy.Broken;
 import org.hibernate.test.legacy.Fixed;
 import org.hibernate.test.legacy.Simple;
 import org.hibernate.test.legacy.Single;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests for function support on CacheSQL...
  *
  * @author Jonathan Levinson
  */
 @RequiresDialect( value = Cache71Dialect.class )
 public class SQLFunctionsInterSystemsTest extends BaseCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( SQLFunctionsInterSystemsTest.class );
 
 	public String[] getMappings() {
 		return new String[] {
 				"legacy/AltSimple.hbm.xml",
 				"legacy/Broken.hbm.xml",
 				"legacy/Blobber.hbm.xml",
 				"dialect/functional/cache/TestInterSystemsFunctionsClass.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testDialectSQLFunctions() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Simple simple = new Simple( Long.valueOf( 10 ) );
 		simple.setName("Simple Dialect Function Test");
 		simple.setAddress("Simple Address");
 		simple.setPay(new Float(45.8));
 		simple.setCount(2);
 		s.save( simple );
 
 		// Test to make sure allocating an specified object operates correctly.
 		assertTrue(
 				s.createQuery( "select new org.hibernate.test.legacy.S(s.count, s.address) from Simple s" ).list().size() == 1
 		);
 
 		// Quick check the base dialect functions operate correctly
 		assertTrue(
 				s.createQuery( "select max(s.count) from Simple s" ).list().size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select count(*) from Simple s" ).list().size() == 1
 		);
 
 		List rset = s.createQuery( "select s.name, sysdate, floor(s.pay), round(s.pay,0) from Simple s" ).list();
 		assertNotNull("Name string should have been returned",(((Object[])rset.get(0))[0]));
 		assertNotNull("Todays Date should have been returned",(((Object[])rset.get(0))[1]));
 		assertEquals("floor(45.8) result was incorrect ", new Integer(45), ( (Object[]) rset.get(0) )[2] );
 		assertEquals("round(45.8) result was incorrect ", new Float(46), ( (Object[]) rset.get(0) )[3] );
 
 		simple.setPay(new Float(-45.8));
 		s.update(simple);
 
 		// Test type conversions while using nested functions (Float to Int).
 		rset = s.createQuery( "select abs(round(s.pay,0)) from Simple s" ).list();
 		assertEquals("abs(round(-45.8)) result was incorrect ", new Float(46), rset.get(0));
 
 		// Test a larger depth 3 function example - Not a useful combo other than for testing
 		assertTrue(
 				s.createQuery( "select floor(round(sysdate,1)) from Simple s" ).list().size() == 1
 		);
 
 		// Test the oracle standard NVL funtion as a test of multi-param functions...
 		simple.setPay(null);
 		s.update(simple);
 		Double value = (Double) s.createQuery("select mod( nvl(s.pay, 5000), 2 ) from Simple as s where s.id = 10").list().get(0);
 		assertTrue( 0 == value.intValue() );
 
 		// Test the hsql standard MOD funtion as a test of multi-param functions...
 		value = (Double) s.createQuery( "select MOD(s.count, 2) from Simple as s where s.id = 10" )
 				.list()
 				.get(0);
 		assertTrue( 0 == value.intValue() );
 
         s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testSetProperties() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf( 10 ) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		Query q = s.createQuery("from Simple s where s.name=:name and s.count=:count");
 		q.setProperties(simple);
 		assertTrue( q.list().get(0)==simple );
 		//misuse of "Single" as a propertyobject, but it was the first testclass i found with a collection ;)
 		Single single = new Single() { // trivial hack to test properties with arrays.
 			@SuppressWarnings( {"unchecked"})
 			String[] getStuff() { 
 				return (String[]) getSeveral().toArray(new String[getSeveral().size()]);
 			}
 		};
 
 		List l = new ArrayList();
 		l.add("Simple 1");
 		l.add("Slimeball");
 		single.setSeveral(l);
 		q = s.createQuery("from Simple s where s.name in (:several)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
 
 		q = s.createQuery("from Simple s where s.name in (:stuff)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testBroken() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Broken b = new Fixed();
 		b.setId( Long.valueOf( 123 ));
 		b.setOtherId("foobar");
 		s.save(b);
 		s.flush();
 		b.setTimestamp( new Date() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(b);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		b = (Broken) s.load( Broken.class, b );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete(b);
 		t.commit();
 		s.close();
 	}
 
 	public void testNothinToUpdate() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testCachedQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setString("name", "Simple 2");
 		q.setCacheable(true);
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	public void testCachedQueryRegion() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	public void testSQLFunctions() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save(simple );
 
 		s.createQuery( "from Simple s where repeat('foo', 3) = 'foofoofoo'" ).list();
 		s.createQuery( "from Simple s where repeat(s.name, 3) = 'foofoofoo'" ).list();
 		s.createQuery( "from Simple s where repeat( lower(s.name), (3 + (1-1)) / 2) = 'foofoofoo'" ).list();
 
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.name ) ='SIMPLE 1'" ).list().size()==1
 		);
 		assertTrue(
 				s.createQuery(
 						"from Simple s where not( upper( s.name ) ='yada' or 1=2 or 'foo'='bar' or not('foo'='foo') or 'foo' like 'bar' )"
 				).list()
 						.size()==1
 		);
 
 		assertTrue(
 				s.createQuery( "from Simple s where lower( s.name || ' foo' ) ='simple 1 foo'" ).list().size()==1
 		);
 		assertTrue(
 				s.createQuery( "from Simple s where lower( concat(s.name, ' foo') ) ='simple 1 foo'" ).list().size()==1
 		);
 
 		Simple other = new Simple( Long.valueOf(20) );
 		other.setName( "Simple 2" );
 		other.setCount( 12 );
 		simple.setOther( other );
 		s.save( other );
 		//s.find("from Simple s where s.name ## 'cat|rat|bag'");
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.other.name ) ='SIMPLE 2'" ).list().size()==1
 		);
 		assertTrue(
 				s.createQuery( "from Simple s where not ( upper( s.other.name ) ='SIMPLE 2' )" ).list().size()==0
 		);
 		assertTrue(
 				s.createQuery(
 						"select distinct s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2"
 				).list()
 						.size()==1
 		);
 		assertTrue(
 				s.createQuery(
 						"select s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2 order by s.other.count"
 				).list()
 						.size()==1
 		);
 		Simple min = new Simple( Long.valueOf(30) );
 		min.setCount( -1 );
 		s.save(min );
 
 		assertTrue(
 				s.createQuery( "from Simple s where s.count > ( select min(sim.count) from Simple sim )" )
 						.list()
 						.size()==2
 		);
 		t.commit();
 		t = s.beginTransaction();
 		assertTrue(
 				s.createQuery(
 						"from Simple s where s = some( select sim from Simple sim where sim.count>=0 ) and s.count >= 0"
 				).list()
 						.size()==2
 		);
 		assertTrue(
 				s.createQuery(
 						"from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count ) and s.other.count > 0"
 				).list()
 						.size()==1
 		);
 
 		Iterator iter = s.createQuery( "select sum(s.count) from Simple s group by s.count having sum(s.count) > 10" )
 				.iterate();
 		assertTrue( iter.hasNext() );
 		assertEquals( Long.valueOf( 12 ), iter.next() );
 		assertTrue( !iter.hasNext() );
 		iter = s.createQuery( "select s.count from Simple s group by s.count having s.count = 12" ).iterate();
 		assertTrue( iter.hasNext() );
 
 		s.createQuery(
 				"select s.id, s.count, count(t), max(t.date) from Simple s, Simple t where s.count = t.count group by s.id, s.count order by s.count"
 		).iterate();
 
 		Query q = s.createQuery("from Simple s");
 		q.setMaxResults( 10 );
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Simple s");
 		q.setMaxResults( 1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s");
 		assertTrue( q.list().size() == 3 );
 		q = s.createQuery("from Simple s where s.name = ?");
 		q.setString( 0, "Simple 1" );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name = ? and upper(s.name) = ?");
 		q.setString(1, "SIMPLE 1");
 		q.setString( 0, "Simple 1" );
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("from Simple s where s.name = :foo and upper(s.name) = :bar or s.count=:count or s.count=:count + 1");
 		q.setParameter( "bar", "SIMPLE 1" );
 		q.setString( "foo", "Simple 1" );
 		q.setInteger("count", 69);
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("select s.id from Simple s");
 		q.setFirstResult(1);
 		q.setMaxResults( 2 );
 		iter = q.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Long );
 			i++;
 		}
 		assertTrue( i == 2 );
 		q = s.createQuery("select all s, s.other from Simple s where s = :s");
 		q.setParameter("s", simple);
 		assertTrue( q.list().size()==1 );
 
 
 		q = s.createQuery("from Simple s where s.name in (:name_list) and s.count > :count");
 		HashSet set = new HashSet();
 		set.add("Simple 1");
 		set.add("foo");
 		q.setParameterList( "name_list", set );
 		q.setParameter("count", new Integer(-1) );
 		assertTrue( q.list().size()==1 );
 
 		ScrollableResults sr = s.createQuery("from Simple s").scroll();
 		sr.next();
 		sr.get(0);
 		sr.close();
 
 		s.delete( other );
 		s.delete( simple );
 		s.delete( min );
 		t.commit();
 		s.close();
 
 	}
 
 	public void testBlobClob() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Blobber b = new Blobber();
 		b.setBlob( s.getLobHelper().createBlob( "foo/bar/baz".getBytes() ) );
 		b.setClob( s.getLobHelper().createClob("foo/bar/baz") );
 		s.save(b);
 		//s.refresh(b);
 		//assertTrue( b.getClob() instanceof ClobImpl );
 		s.flush();
 		s.refresh(b);
 		//b.getBlob().setBytes( 2, "abc".getBytes() );
         log.debug("levinson: just bfore b.getClob()");
         b.getClob().getSubString(2, 3);
 		//b.getClob().setString(2, "abc");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		Blobber b2 = new Blobber();
 		s.save(b2);
 		b2.setBlob( b.getBlob() );
 		b.setBlob(null);
 		//assertTrue( b.getClob().getSubString(1, 3).equals("fab") );
 		b.getClob().getSubString(1, 6);
 		//b.getClob().setString(1, "qwerty");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		b.setClob( s.getLobHelper().createClob("xcvfxvc xcvbx cvbx cvbx cvbxcvbxcvbxcvb") );
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		assertTrue( b.getClob().getSubString(1, 7).equals("xcvfxvc") );
 		//b.getClob().setString(5, "1234567890");
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testSqlFunctionAsAlias() throws Exception {
 		String functionName = locateAppropriateDialectFunctionNameForAliasTest();
 		if (functionName == null) {
             log.info("Dialect does not list any no-arg functions");
 			return;
 		}
 
         log.info("Using function named [" + functionName + "] for 'function as alias' test");
 		String query = "select " + functionName + " from Simple as " + functionName + " where " + functionName + ".id = 10";
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List result = s.createQuery( query ).list();
 		assertTrue( result.size() == 1 );
 		assertTrue(result.get(0) instanceof Simple);
 		s.delete( result.get(0) );
 		t.commit();
 		s.close();
 	}
 
 	@SuppressWarnings( {"ForLoopReplaceableByForEach"})
 	private String locateAppropriateDialectFunctionNameForAliasTest() {
 		for (Iterator itr = getDialect().getFunctions().entrySet().iterator(); itr.hasNext(); ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final SQLFunction function = (SQLFunction) entry.getValue();
 			if ( !function.hasArguments() && !function.hasParenthesesIfNoArguments() ) {
 				return (String) entry.getKey();
 			}
 		}
 		return null;
 	}
 
 	public void testCachedQueryOnInsert() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setName("Simple 1");
 		s.save( simple );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s");
 		List list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Simple simple2 = new Simple( Long.valueOf(12) );
 		simple2.setCount(133);
 		s.save( simple2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		for ( Object o : list ) {
 			s.delete( o );
 		}
 		t.commit();
 		s.close();
 
 	}
 
 	public void testInterSystemsFunctions() throws Exception {
         Calendar cal = new GregorianCalendar();
         cal.set(1977,6,3,0,0,0);
         java.sql.Timestamp testvalue = new java.sql.Timestamp(cal.getTimeInMillis());
         testvalue.setNanos(0);
         Calendar cal3 = new GregorianCalendar();
         cal3.set(1976,2,3,0,0,0);
         java.sql.Timestamp testvalue3 = new java.sql.Timestamp(cal3.getTimeInMillis());
         testvalue3.setNanos(0);
 
         final Session s = openSession();
         s.beginTransaction();
         try {
 			s.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							Statement stmt = ((SessionImplementor)s).getJdbcCoordinator().getStatementPreparer().createStatement();
 							((SessionImplementor)s).getJdbcCoordinator().getResultSetReturn().executeUpdate( stmt, "DROP FUNCTION spLock FROM TestInterSystemsFunctionsClass" );
 						}
 					}
 			);
         }
         catch (Exception ex) {
             System.out.println("as we expected stored procedure sp does not exist when we drop it");
 
         }
 		s.getTransaction().commit();
 
         s.beginTransaction();
 		s.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
-						Statement stmt = ((SessionImplementor)s).getJdbcCoordinator().getStatementPreparer().createStatement();
+						Statement stmt = ( (SessionImplementor) s ).getJdbcCoordinator()
+								.getStatementPreparer()
+								.createStatement();
 						String create_function = "CREATE FUNCTION SQLUser.TestInterSystemsFunctionsClass_spLock\n" +
 								"     ( INOUT pHandle %SQLProcContext, \n" +
 								"       ROWID INTEGER \n" +
 								" )\n" +
 								" FOR User.TestInterSystemsFunctionsClass " +
 								"    PROCEDURE\n" +
 								"    RETURNS INTEGER\n" +
 								"    LANGUAGE OBJECTSCRIPT\n" +
 								"    {\n" +
 								"        q 0\n" +
 								"     }";
-						((SessionImplementor)s).getJdbcCoordinator().getResultSetReturn().executeUpdate( stmt, create_function );
+						( (SessionImplementor) s ).getJdbcCoordinator().getResultSetReturn().executeUpdate(
+								stmt,
+								create_function
+						);
 					}
 				}
 		);
         s.getTransaction().commit();
 
         s.beginTransaction();
 
         TestInterSystemsFunctionsClass object = new TestInterSystemsFunctionsClass( Long.valueOf( 10 ) );
-        object.setDateText("1977-07-03");
+        object.setDateText( "1977-07-03" );
         object.setDate1( testvalue );
         object.setDate3( testvalue3 );
         s.save( object );
         s.getTransaction().commit();
         s.close();
 
         Session s2 = openSession();
         s2.beginTransaction();
-        TestInterSystemsFunctionsClass test = (TestInterSystemsFunctionsClass) s2.get(TestInterSystemsFunctionsClass.class, Long.valueOf(10));
+        TestInterSystemsFunctionsClass test = s2.get(TestInterSystemsFunctionsClass.class, 10L );
         assertTrue( test.getDate1().equals(testvalue));
-        test = (TestInterSystemsFunctionsClass) s2.get(TestInterSystemsFunctionsClass.class, Long.valueOf(10), LockMode.UPGRADE);
+        test = (TestInterSystemsFunctionsClass) s2.byId( TestInterSystemsFunctionsClass.class ).with( LockOptions.NONE ).load( 10L );
         assertTrue( test.getDate1().equals(testvalue));
         Date value = (Date) s2.createQuery( "select nvl(o.date,o.dateText) from TestInterSystemsFunctionsClass as o" )
 				.list()
 				.get(0);
         assertTrue( value.equals(testvalue));
         Object nv = s2.createQuery( "select nullif(o.dateText,o.dateText) from TestInterSystemsFunctionsClass as o" )
 				.list()
 				.get(0);
         assertTrue( nv == null);
         String dateText = (String) s2.createQuery(
 				"select nvl(o.dateText,o.date) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue( dateText.equals("1977-07-03"));
         value = (Date) s2.createQuery( "select ifnull(o.date,o.date1) from TestInterSystemsFunctionsClass as o" )
 				.list()
 				.get(0);
         assertTrue( value.equals(testvalue));
         value = (Date) s2.createQuery( "select ifnull(o.date3,o.date,o.date1) from TestInterSystemsFunctionsClass as o" )
 				.list()
 				.get(0);
         assertTrue( value.equals(testvalue));
         Integer pos = (Integer) s2.createQuery(
 				"select position('07', o.dateText) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue(pos.intValue() == 6);
         String st = (String) s2.createQuery( "select convert(o.date1, SQL_TIME) from TestInterSystemsFunctionsClass as o" )
 				.list()
 				.get(0);
         assertTrue( st.equals("00:00:00"));
         java.sql.Time tm = (java.sql.Time) s2.createQuery(
 				"select cast(o.date1, time) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue( tm.toString().equals("00:00:00"));
         Double diff = (Double) s2.createQuery(
 				"select timestampdiff(SQL_TSI_FRAC_SECOND, o.date3, o.date1) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue(diff.doubleValue() != 0.0);
         diff = (Double) s2.createQuery(
 				"select timestampdiff(SQL_TSI_MONTH, o.date3, o.date1) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue(diff.doubleValue() == 16.0);
         diff = (Double) s2.createQuery(
 				"select timestampdiff(SQL_TSI_WEEK, o.date3, o.date1) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue(diff.doubleValue() >= 16*4);
         diff = (Double) s2.createQuery(
 				"select timestampdiff(SQL_TSI_YEAR, o.date3, o.date1) from TestInterSystemsFunctionsClass as o"
 		).list()
 				.get(0);
         assertTrue(diff.doubleValue() == 1.0);
 
         s2.getTransaction().commit();
         s2.close();
     }
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/FooBarCopy.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/hql/FooBarCopy.hbm.xml
index 16010fc19f..d52271b632 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/FooBarCopy.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/FooBarCopy.hbm.xml
@@ -1,137 +1,137 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 <hibernate-mapping default-lazy="false" package="org.hibernate.test.legacy">
 
     <!-- a slightly modified copy of FooBar.hbm.xml from the legacy test package -->
 
     <class
 		name="Foo"
 		table="`foos`"
 		proxy="FooProxy"
 		discriminator-value="F"
 		batch-size="4"
 		dynamic-insert="true"
 		dynamic-update="true"
 		select-before-update="true">
 
 		<id name="key" type="string">
 			<column name="`foo_idcolumnname123`" length="36"/>
 			<generator class="uuid.hex">
 				<param name="seperator">:</param>
 			</generator>
 		</id>
 
 		<discriminator column="`foo_subclass_1234`" type="character" force="true"/>
 		<version name="version"/>
 
 		<many-to-one name="foo" class="Foo">
 				<column name="foo" length="36" index="fbmtoidx"/>
 		</many-to-one>
 
 		<property name="long">
 			<column name="long_" index="fbmtoidx" unique-key="abc" not-null="true"/>
 		</property>
 		<property name="integer">
 				<column name="`integer__`" unique-key="abc" not-null="true"/>
 		</property>
 		<property name="float">
 				<column name="float_" unique-key="abc" not-null="true" check="float_ > 0.0"/>
 		</property>
 		<property name="x"/>
 		<property name="double" column="double_"/>
 
 		<primitive-array name="bytes" table="foobytes">
 			<key column="id"/>
 			<index column="i"/>
 			<element column="byte_" type="byte"/>
 		</primitive-array>
 
 		<property name="date" type="date" column="date_"/>
 		<property name="timestamp" type="timestamp" column="timestamp_"/>
 		<property name="boolean" column="boolean_"/>
 		<property name="bool" column="bool_"/>
 		<property name="null" column="null_"/>
 		<property name="short" column="short_"/>
 		<property name="char" column="char_"/>
 		<property name="zero" column="zero_"/>
 		<property name="int" column="int_"/>
 		<property name="string">
             <column name="string_" length="48" index="fbstridx"/>
 		</property>
 		<property name="byte" column="byte_"/>
 		<property name="yesno" type="yes_no"/>
 		<property name="blob" type="org.hibernate.test.legacy.Foo$Struct" column="blobb_"/>
 		<property name="nullBlob" type="serializable"/>
 		<property name="binary" column="bin_"/>
 		<property name="theLocale" access="field" column="`localeayzabc123`"/>
 
 		<property name="formula" formula="int_/2"/>
 
 		<property name="custom" type="org.hibernate.test.legacy.DoubleStringType" access="field">
 			<column name="first_name" length="66"/>
 			<column name="surname" length="66"/>
 		</property>
 		<component name="nullComponent">
 			<property name="name" column="null_cmpnt_"/>
 		</component>
 
 		<join table="jointable">
 			<key column="fooid" on-delete="cascade"/>
 			<property name="joinedProp"/>
 		</join>
 
 		<subclass
 			name="Trivial"
-			proxy="FooProxy"
+			proxy="TrivialProxy"
 			discriminator-value="T"/>
 
 		<subclass
 			name="Abstract"
 			proxy="AbstractProxy"
 			discriminator-value="null">
 				<set name="abstracts" batch-size="2">
 					<key column="abstract_id"/>
 					<one-to-many class="Abstract"/>
 				</set>
 				<property name="time" column="the_time"/>
 
 				<subclass
 					name="Bar"
 					proxy="BarProxy"
 					discriminator-value="B">
 					<property name="barString">
 						<column name="bar_string" length="24"/>
 					</property>
 					<any name="object" meta-type="character" id-type="long" cascade="all">
 						<meta-value value="O" class="One"/>
 						<meta-value value="M" class="Many"/>
 						<column name="clazz" length="100"/>
 						<column name="gen_id"/>
 					</any>
 					<join table="bar_join_table">
 						<key column="bar_id"/>
 						<property name="name" column="name_name"/>
 					</join>
 				</subclass>
 		</subclass>
 	</class>
 
 	<class name="One" table="one">
 		<id name="key" column="one_key">
 			<generator class="native" />
 		</id>
 		<property name="x"/>
 		<property column="one_value" name="value"/>
 	</class>
 
     <class name="Many" table="many">
         <id name="key" column="many_key">
             <generator class="native" />
         </id>
         <property name="x"/>
     </class>
 
 </hibernate-mapping>
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBar.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBar.hbm.xml
index b281b60e16..edddd8ad6f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBar.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBar.hbm.xml
@@ -1,178 +1,178 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 <hibernate-mapping default-lazy="false" package="org.hibernate.test.legacy">
 
 	<import class="Result"/>
 	<import class="Named"/>
 
 	<class
 		name="Foo"
 		table="`foos`"
 		proxy="FooProxy"
 		discriminator-value="F"
 		batch-size="4"
 		dynamic-insert="true"
 		dynamic-update="true"
 		select-before-update="true">
 
 		<!--cache-->
 
 		<id name="key" type="string">
 			<column name="`foo_idcolumnname123`" length="36"/>
 			<generator class="uuid.hex">
 				<param name="seperator">:</param>
 			</generator>
 		</id>
 		<discriminator column="`foo_subclass_1234`" type="character" force="true"/>
 		<version name="version"/>
 		<!--<version name="versionCalendar" type="calendar"/>-->
 		<!--<timestamp name="versionTimestamp"/>-->
 		<many-to-one name="foo" class="Foo">
 				<column name="foo" length="36" index="fbmtoidx"/>
 		</many-to-one>
 		<property name="long">
 			<column name="long_" index="fbmtoidx" unique-key="abc" not-null="true"/>
 		</property>
 		<property name="integer">
 				<column name="`integer__`" unique-key="abc" not-null="true"/>
 		</property>
 		<property name="float">
 				<column name="float_" unique-key="abc" not-null="true" check="float_ > 0.0"/>
 		</property>
 		<property name="x"/>
 		<property name="double" column="double_"/>
 
 		<primitive-array name="bytes" table="foobytes">
 			<key column="id"/>
 			<index column="i"/>
 			<element column="byte_" type="byte"/>
 		</primitive-array>
 
 		<property name="date" type="date" column="date_"/>
 		<property name="timestamp" type="timestamp" column="timestamp_"/>
 		<property name="boolean" column="boolean_"/>
 		<property name="bool" column="bool_"/>
 		<property name="null" column="null_"/>
 		<property name="short" column="short_"/>
 		<property name="char" column="char_"/>
 		<property name="zero" column="zero_"/>
 		<property name="int" column="int_"/>
 		<property name="string">
 				<column name="string_" length="48" index="fbstridx"/>
 		</property>
 		<property name="byte" column="byte_"/>
 		<property name="yesno" type="yes_no"/>
 		<property name="blob" type="org.hibernate.test.legacy.Foo$Struct" column="blobb_"/>
 		<property name="nullBlob" type="serializable"/>
 		<property name="binary" column="bin_"/>
 		<property name="theLocale" access="field" column="`localeayzabc123`"/>
 
 		<property name="formula" formula="int_/2"/>
 
 		<property name="custom" type="org.hibernate.test.legacy.DoubleStringType" access="field">
 				<column name="first_name" length="66"/>
 				<column name="surname" length="66"/>
 		</property>
 
 		<component name="component">
 			<property name="count" column="count_" type="int" not-null="true"/>
 			<property name="name">
 				<column name="name_" length="32" not-null="true"/>
 			</property>
 			<many-to-one name="glarch"
 				column="g__"
 				cascade="all"
 				class="org.hibernate.test.legacy.Glarch"
 				lazy="proxy"
 				outer-join="true"/>
 			<property name="null" column="cmpnt_null_"/>
 			<component name="subcomponent">
 				<!--property name="count" column="subcount"/-->
 				<property name="name" column="subname"/>
 				<array name="importantDates" table="foo_times">
 					<key column="foo_id"/>
 					<index column="i"/>
 					<element column="date_" type="time"/>
 				</array>
 				<many-to-one name="fee"
 					column="fee_sub"
 					cascade="all"
 					class="Fee"
 					outer-join="false"
 					access="field"/>
 			</component>
 			<array name="importantDates" table="foo_dates">
 				<key column="foo_id"/>
 				<index column="i"/>
 				<element column="date_" type="date"/>
 			</array>
 		</component>
 		<component name="nullComponent">
 			<property name="name" column="null_cmpnt_"/>
 		</component>
 
 		<join table="jointable">
 			<key column="fooid" on-delete="cascade"/>
 			<property name="joinedProp"/>
 		</join>
 
 		<join table="foo_dep_table">
 			<key column="fooid"/>
 			<many-to-one name="dependent"
 				class="org.hibernate.test.legacy.Fee"
 				cascade="all"
 				not-null="true"/>
 		</join>
 
 		<subclass
 			name="Trivial"
-			proxy="FooProxy"
+			proxy="TrivialProxy"
 			discriminator-value="T"/>
 
 		<subclass
 			name="Abstract"
 			proxy="AbstractProxy"
 			discriminator-value="null">
 				<set name="abstracts" batch-size="2">
 					<key column="abstract_id"/>
 					<one-to-many class="Abstract"/>
 				</set>
 				<property name="time" column="the_time"/>
 
 				<subclass
 					name="Bar"
 					proxy="BarProxy"
 					discriminator-value="B">
 					<many-to-one name="baz"/>
 					<property name="barString">
 						<column name="bar_string" length="24"/>
 					</property>
 					<component name="barComponent" class="FooComponent">
 						<parent name="parent"/>
 						<property name="count" column="bar_count"/>
 						<property name="name" length="64"/>
 						<array name ="importantDates">
 								<key column="id" />
 								<index column="i"/>
 							 <element column="date_" type="date"/>
 						</array>
 					</component>
 					<any name="object" meta-type="character" id-type="long" cascade="all">
 						<meta-value value="O" class="One"/>
 						<meta-value value="M" class="Many"/>
 						<column name="clazz" length="100"/>
 						<column name="gen_id"/>
 					</any>
 					<join table="bar_join_table">
 						<key column="bar_id"/>
 						<property name="name" column="name_name"/>
 					</join>
 				</subclass>
 		</subclass>
 	</class>
 
 
 </hibernate-mapping>
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/IJTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/IJTest.java
index 7cdf8771e1..70af62ce19 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/IJTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/IJTest.java
@@ -1,78 +1,79 @@
 //$Id: IJTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 
 import org.junit.Test;
 
 import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.dialect.HSQLDialect;
 
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 public class IJTest extends LegacyTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "legacy/IJ.hbm.xml" };
 	}
 
 	@Test
 	public void testFormulaDiscriminator() throws Exception {
 		if ( getDialect() instanceof HSQLDialect ) return;
 		Session s = sessionFactory().openSession();
 		s.beginTransaction();
 		I i = new I();
 		i.setName( "i" );
 		i.setType( 'a' );
 		J j = new J();
 		j.setName( "j" );
 		j.setType( 'x' );
 		j.setAmount( 1.0f );
 		Serializable iid = s.save(i);
 		Serializable jid = s.save(j);
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getCache().evictEntityRegion( I.class );
 
 		s = sessionFactory().openSession();
 		s.beginTransaction();
 		j = (J) s.get(I.class, jid);
 		i = (I) s.get(I.class, iid);
-		assertTrue( i.getClass()==I.class );
+		assertTrue( i.getClass() == I.class );
 		j.setAmount( 0.5f );
-		s.lock(i, LockMode.UPGRADE);
+		s.lock( i, LockMode.UPGRADE );
 		s.getTransaction().commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		s.beginTransaction();
-		j = (J) s.get(I.class, jid, LockMode.UPGRADE);
-		i = (I) s.get(I.class, iid, LockMode.UPGRADE);
+		j = (J) s.byId( I.class ).with( LockOptions.UPGRADE ).load( jid );
+		i = (I) s.byId( I.class ).with( LockOptions.UPGRADE ).load( iid );
 		s.getTransaction().commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		s.beginTransaction();
 		assertTrue( s.createQuery( "from I" ).list().size()==2 );
 		assertTrue( s.createQuery( "from J" ).list().size()==1 );
 		assertTrue( s.createQuery( "from I i where i.class = 0" ).list().size()==1 );
 		assertTrue( s.createQuery( "from I i where i.class = 1" ).list().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		s.beginTransaction();
 		j = (J) s.get(J.class, jid);
 		i = (I) s.get(I.class, iid);
 		s.delete(j);
 		s.delete(i);
 		s.getTransaction().commit();
 		s.close();
 
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
index 29d3af3eb4..059d031d85 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
@@ -1,1260 +1,1261 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.testing.SkipForDialect;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.type.StandardBasicTypes;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 
 public class ParentChildTest extends LegacyTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/ParentChild.hbm.xml",
 			"legacy/FooBar.hbm.xml",
 		 	"legacy/Baz.hbm.xml",
 		 	"legacy/Qux.hbm.xml",
 		 	"legacy/Glarch.hbm.xml",
 		 	"legacy/Fum.hbm.xml",
 		 	"legacy/Fumm.hbm.xml",
 		 	"legacy/Fo.hbm.xml",
 		 	"legacy/One.hbm.xml",
 		 	"legacy/Many.hbm.xml",
 		 	"legacy/Immutable.hbm.xml",
 		 	"legacy/Fee.hbm.xml",
 		 	"legacy/Vetoer.hbm.xml",
 		 	"legacy/Holder.hbm.xml",
 		 	"legacy/Simple.hbm.xml",
 		 	"legacy/Container.hbm.xml",
 		 	"legacy/Circular.hbm.xml",
 		 	"legacy/Stuff.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testReplicate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Container baz = new Container();
 		Contained f = new Contained();
 		List list = new ArrayList();
 		list.add(baz);
 		f.setBag(list);
 		List list2 = new ArrayList();
 		list2.add(f);
 		baz.setBag(list2);
 		s.save(f);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.OVERWRITE);
 		// HHH-2378
 		SessionImpl x = (SessionImpl)s;
 		EntityEntry entry = x.getPersistenceContext().getEntry( baz );
 		assertNull(entry.getVersion());
 		// ~~~~~~~
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.IGNORE);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.delete(f);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryOneToOne() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable id = s.save( new Parent() );
 		assertTrue( s.createQuery( "from Parent p left join fetch p.child" ).list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Parent p = (Parent) s.createQuery("from Parent p left join fetch p.child").uniqueResult();
 		assertTrue( p.getChild()==null );
 		s.createQuery( "from Parent p join p.child c where c.x > 0" ).list();
 		s.createQuery( "from Child c join c.parent p where p.x > 0" ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.get(Parent.class, id) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testProxyReuse() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		FooProxy foo = new Foo();
 		FooProxy foo2 = new Foo();
 		Serializable id = s.save(foo);
 		Serializable id2 = s.save(foo2);
 		foo2.setInt( 1234567 );
 		foo.setInt(1234);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		foo2 = (FooProxy) s.load(Foo.class, id2);
 		assertFalse( Hibernate.isInitialized( foo ) );
 		Hibernate.initialize( foo2 );
 		Hibernate.initialize(foo);
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		assertTrue( foo2.getComponent().getImportantDates().length == 4 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo.setKey( "xyzid" );
 		foo.setFloat( new Float( 1.2f ) );
 		foo2.setKey( (String) id ); //intentionally id, not id2!
 		foo2.setFloat( new Float( 1.3f ) );
 		foo2.getDependent().setKey( null );
 		foo2.getComponent().getSubcomponent().getFee().setKey(null);
 		assertFalse( foo2.getKey().equals( id ) );
 		s.save( foo );
 		s.update( foo2 );
 		assertEquals( foo2.getKey(), id );
 		assertTrue( foo2.getInt() == 1234567 );
 		assertEquals( foo.getKey(), "xyzid" );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		assertTrue( foo.getInt() == 1234567 );
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		String feekey = foo.getDependent().getKey();
 		String fookey = foo.getKey();
 		s.delete( foo );
 		s.delete( s.get(Foo.class, id2) );
 		s.delete( s.get(Foo.class, "xyzid") );
 // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
 		assertEquals( 2, doDelete( s, "from java.lang.Object" ) );
 		t.commit();
 		s.close();
 		
 		//to account for new id rollback shit
 		foo.setKey(fookey);
 		foo.getDependent().setKey( feekey );
 		foo.getComponent().setGlarch( null );
 		foo.getComponent().setSubcomponent(null);
 		
 		s = openSession();
 		t = s.beginTransaction();
 		//foo.getComponent().setGlarch(null); //no id property!
 		s.replicate( foo, ReplicationMode.OVERWRITE );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Foo refoo = (Foo) s.get(Foo.class, id);
 		assertEquals( feekey, refoo.getDependent().getKey() );
 		s.delete(refoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplexCriteria() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		Map topGlarchez = new HashMap();
 		baz.setTopGlarchez(topGlarchez);
 		Glarch g1 = new Glarch();
 		g1.setName("g1");
 		s.save(g1);
 		Glarch g2 = new Glarch();
 		g2.setName("g2");
 		s.save(g2);
 		g1.setProxyArray( new GlarchProxy[] {g2} );
 		topGlarchez.put( new Character('1'),g1 );
 		topGlarchez.put( new Character('2'), g2);
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		s.save(foo1);
 		s.save(foo2);
 		baz.getFooSet().add(foo1);
 		baz.getFooSet().add(foo2);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		LockMode lockMode = supportsLockingNullableSideOfJoin( getDialect() ) ? LockMode.UPGRADE : LockMode.READ;
 
 		Criteria crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") )
 			.createCriteria("proxyArray")
 				.add( Restrictions.eqProperty("name", "name") )
 				.add( Restrictions.eq("name", "g2") )
 				.add( Restrictions.gt("x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") )
 			.add( Restrictions.eq("string", "a string") )
 			.add( Restrictions.lt("integer", new Integer(-665) ) );
 		crit.createCriteria("fooArray")
 				// this is the bit causing the problems; creating the criteria on fooArray does not add it to FROM,
 				// and so restriction below leads to an invalid reference.
 			.add( Restrictions.eq("string", "a string") )
 			.setLockMode(lockMode);
 
 		List list = crit.list();
 		assertTrue( list.size()==2 );
 		
 		s.createCriteria(Glarch.class).setLockMode(LockMode.UPGRADE).list();
 		s.createCriteria(Glarch.class).setLockMode(Criteria.ROOT_ALIAS, LockMode.UPGRADE).list();
 		
 		g2.setName(null);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		
 		list = s.createCriteria(Baz.class).add( Restrictions.isEmpty("fooSet") ).list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.isNotEmpty("fooSet") ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.sizeEq("fooSet", 2) ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 		
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class)
 			.setLockMode(lockMode);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.gt( "x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 		list = crit.list();
 
 		assertTrue( list.size()==4 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 
 		list = s.createCriteria(Baz.class)
 			.createCriteria("fooSet")
 				.createCriteria("foo")
 					.createCriteria("component.glarch")
 						.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		list = s.createCriteria(Baz.class)
 			.createAlias("fooSet", "foo")
 			.createAlias("foo.foo", "foo2")
 			.setLockMode("foo2", lockMode)
 			.add( Restrictions.isNull("foo2.component.glarch") )
 			.createCriteria("foo2.component.glarch")
 				.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 
 		list = crit.list();
 		assertTrue( list.size()==2 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 		
 		s.createCriteria(Child.class).setFetchMode("parent", FetchMode.JOIN).list();
 
 		doDelete( s, "from Glarch g" );
 		s.delete( s.get(Foo.class, foo1.getKey() ) );
 		s.delete( s.get(Foo.class, foo2.getKey() ) );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testArrayHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b left join fetch b.fooArray").uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testArrayCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria(Baz.class).createCriteria( "fooArray" ).uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b").uniqueResult();
 		assertFalse( Hibernate.isInitialized( baz.getFoo() ) );
 		assertTrue( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria( Baz.class ).uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneGet() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.get( Baz.class, baz.getCode() );
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhere() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getParts().add(p1);
 		baz.getParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.parts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhereManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setMoreParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getMoreParts().add(p1);
 		baz.getMoreParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.moreParts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getMoreParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setName("s");
 		s1.setCount(0);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		Container c = new Container();
 		Contained cd = new Contained();
 		List bag = new ArrayList();
 		bag.add(cd);
 		c.setBag(bag);
 		List l = new ArrayList();
 		l.add(s1);
 		l.add(s3);
 		l.add(s2);
 		c.setOneToMany(l);
 		l = new ArrayList();
 		l.add(s1);
 		l.add(null);
 		l.add(s2);
 		c.setManyToMany(l);
 		s.save(c);
 		Container cx = new Container();
 		s.save(cx);
 		Simple sx = new Simple( Long.valueOf(5) );
 		sx.setCount(5);
 		sx.setName("s");
 		s.save( sx );
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.oneToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.oneToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.manyToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.oneToMany[2 - 2].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.manyToMany[(3+1)/4-1].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		if ( ! ( getDialect() instanceof MySQLDialect ) && !(getDialect() instanceof org.hibernate.dialect.TimesTenDialect) ) {
 			assertTrue(
 					s.createQuery( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" )
 							.list()
 							.size() == 1
 			);
 		}
 		assertTrue( s.contains(cd) );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) )  {
 			s.createFilter( c.getBag(), "where 0 in elements(this.bag)" ).list();
 			s.createFilter( c.getBag(), "where 0 in elements(this.lazyBag)" ).list();
 		}
 		s.createQuery( "select count(comp.name) from ContainerX c join c.components comp" ).list();
 		s.delete(cd);
 		s.delete(c);
 		s.delete(s1);
 		s.delete(s2);
 		s.delete(s3);
 		s.delete(cx);
 		s.delete(sx);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		Child c = new Child();
 		c.setParent(p);
 		p.setChild(c);
 		s.save(p);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 parent", p!=null );
 		c.setCount(32);
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 update", p.getCount()==66 );
 		assertTrue( "1-1 update", c.getCount()==32 );
 		assertTrue(
 			"1-1 query",
 				s.createQuery( "from Child c where c.parent.count=66" ).list().size()==1
 		);
 		assertTrue(
 			"1-1 query",
 			( (Object[]) s.createQuery( "from Parent p join p.child c where p.count=66" ).list().get(0) ).length==2
 		);
 		s.createQuery( "select c, c.parent from Child c order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent from Child c where c.parent.count=66 order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent, c.parent.count from Child c order by c.parent.count" ).iterate();
 		List result = s.createQuery( "FROM Parent AS p WHERE p.count = ?" )
 				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
 				.list();
 		assertEquals( "1-1 query", 1, result.size() );
 		s.delete(c); s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentNullChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		s.save(p);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( p.getChild()==null );
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( "null 1-1 update", p.getCount()==66 );
 		assertTrue( p.getChild()==null );
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		c.setManyToMany( new ArrayList() );
 		c.setBag( new ArrayList() );
 		Simple s1 = new Simple( Long.valueOf(12) );
 		Simple s2 = new Simple( Long.valueOf(-1) );
 		s1.setCount(123); s2.setCount(654);
 		Contained c1 = new Contained();
 		c1.setBag( new ArrayList() );
 		c1.getBag().add(c);
 		c.getBag().add(c1);
 		c.getManyToMany().add(s1);
 		c.getManyToMany().add(s2);
 		Serializable cid = s.save(c);
 		s.save( s1 );
 		s.save( s2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==1 );
 		assertTrue( c.getManyToMany().size()==2 );
 		c1 = (Contained) c.getBag().iterator().next();
 		assertTrue( c.getBag().size()==1 );
 		c.getBag().remove(c1);
 		c1.getBag().remove(c);
 		assertTrue( c.getManyToMany().remove(0)!=null );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==0 );
 		assertTrue( c.getManyToMany().size()==1 );
 		c1 = (Contained) s.load( Contained.class, new Long(c1.getId()) );
 		assertTrue( c1.getBag().size()==0 );
 		assertEquals( 1, doDelete( s, "from ContainerX c" ) );
 		assertEquals( 1, doDelete( s, "from Contained" ) );
 		assertEquals( 2, doDelete( s, "from Simple" ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testContainer() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Simple x = new Simple( Long.valueOf(1) );
 		x.setCount(123);
 		Simple y = new Simple( Long.valueOf(0) );
 		y.setCount(456);
 		s.save( x );
 		s.save( y );
 		List o2m = new ArrayList();
 		o2m.add(x); o2m.add(null); o2m.add(y);
 		List m2m = new ArrayList();
 		m2m.add(x); m2m.add(null); m2m.add(y);
 		c.setOneToMany(o2m); c.setManyToMany(m2m);
 		List comps = new ArrayList();
 		Container.ContainerInnerClass ccic = new Container.ContainerInnerClass();
 		ccic.setName("foo");
 		ccic.setSimple(x);
 		comps.add(ccic);
 		comps.add(null);
 		ccic = new Container.ContainerInnerClass();
 		ccic.setName("bar");
 		ccic.setSimple(y);
 		comps.add(ccic);
 		HashSet compos = new HashSet();
 		compos.add(ccic);
 		c.setComposites(compos);
 		c.setComponents(comps);
 		One one = new One();
 		Many many = new Many();
 		HashSet manies = new HashSet();
 		manies.add(many);
 		one.setManies(manies);
 		many.setOne(one);
 		ccic.setMany(many);
 		ccic.setOne(one);
 		s.save(one);
 		s.save(many);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Long count = (Long) s.createQuery("select count(*) from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").uniqueResult();
 		assertTrue( count.intValue()==1 );
 		List res = s.createQuery(
 				"select c, s from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'"
 		).list();
 		assertTrue(res.size()==1);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		ccic = (Container.ContainerInnerClass) c.getComponents().get(2);
 		assertTrue( ccic.getMany().getOne()==ccic.getOne() );
 		assertTrue( c.getComponents().size()==3 );
 		assertTrue( c.getComposites().size()==1 );
 		assertTrue( c.getOneToMany().size()==3 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(2)!=null );
 		for ( int i=0; i<3; i++ ) {
 			assertTrue( c.getManyToMany().get(i) == c.getOneToMany().get(i) );
 		}
 		Object o1 = c.getOneToMany().get(0);
 		Object o2 = c.getOneToMany().remove(2);
 		c.getOneToMany().set(0, o2);
 		c.getOneToMany().set(1, o1);
 		o1 = c.getComponents().remove(2);
 		c.getComponents().set(0, o1);
 		c.getManyToMany().set( 0, c.getManyToMany().get(2) );
 		Container.ContainerInnerClass ccic2 = new Container.ContainerInnerClass();
 		ccic2.setName("foo");
 		ccic2.setOne(one);
 		ccic2.setMany(many);
 		ccic2.setSimple( (Simple) s.load(Simple.class, new Long(0) ) );
 		c.getComposites().add(ccic2);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( c.getOneToMany().size()==2 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(1)!=null );
 		( (Container.ContainerInnerClass) c.getComponents().get(0) ).setName("a different name");
 		( (Container.ContainerInnerClass) c.getComposites().iterator().next() ).setName("once again");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( ( (Container.ContainerInnerClass) c.getComponents().get(0) ).getName().equals("a different name") );
 		Iterator iter = c.getComposites().iterator();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( ( (Container.ContainerInnerClass) iter.next() ).getName().equals("once again") ) found = true;
 		}
 		assertTrue(found);
 		c.getOneToMany().clear();
 		c.getManyToMany().clear();
 		c.getComposites().clear();
 		c.getComponents().clear();
 		doDelete( s, "from Simple" );
 		doDelete( s, "from Many" );
 		doDelete( s, "from One" );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		assertTrue( c.getComponents().size()==0 );
 		assertTrue( c.getComposites().size()==0 );
 		assertTrue( c.getOneToMany().size()==0 );
 		assertTrue( c.getManyToMany().size()==0 );
 		s.delete(c);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeCompositeElements() throws Exception {
 		Container c = new Container();
 		List list = new ArrayList();
 		c.setCascades(list);
 		Container.ContainerInnerClass cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		Session s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 
 		c = new Container();
 		s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		list = new ArrayList();
 		c.setCascades(list);
 		cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBag() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Contained c1 = new Contained();
 		Contained c2 = new Contained();
 		c.setBag( new ArrayList() );
 		c.getBag().add(c1);
 		c.getBag().add(c2);
 		c1.getBag().add(c);
 		c2.getBag().add(c);
 		s.save(c);
 		c.getBag().add(c2);
 		c2.getBag().add(c);
 		c.getLazyBag().add(c1);
 		c1.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		c.getLazyBag().size();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c3 = new Contained();
 		//c.getBag().add(c3);
 		//c3.getBag().add(c);
 		c.getLazyBag().add(c3);
 		c3.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c4 = new Contained();
 		c.getLazyBag().add(c4);
 		c4.getLazyBag().add(c);
 		assertTrue( c.getLazyBag().size()==3 ); //forces initialization
 		//s.save(c4);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Iterator i = c.getBag().iterator();
 		int j=0;
 		while ( i.hasNext() ) {
 			assertTrue( i.next()!=null );
 			j++;
 		}
 		assertTrue(j==3);
 		assertTrue( c.getLazyBag().size()==3 );
 		s.delete(c);
 		c.getBag().remove(c2);
 		Iterator iter = c.getBag().iterator();
 		j=0;
 		while ( iter.hasNext() ) {
 			j++;
 			s.delete( iter.next() );
 		}
 		assertTrue(j==2);
 		s.delete( s.load(Contained.class, new Long( c4.getId() ) ) );
 		s.delete( s.load(Contained.class, new Long( c3.getId() ) ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCircularCascade() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Circular c = new Circular();
 		c.setClazz(Circular.class);
 		c.setOther( new Circular() );
 		c.getOther().setOther( new Circular() );
 		c.getOther().getOther().setOther(c);
 		c.setAnyEntity( c.getOther() );
 		String id = (String) s.save(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		c.getOther().getOther().setClazz(Foo.class);
 		tx.commit();
 		s.close();
 		c.getOther().setClazz(Qux.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		c.getOther().getOther().setClazz(Bar.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		assertTrue( c.getOther().getOther().getClazz()==Bar.class);
 		assertTrue( c.getOther().getClazz()==Qux.class);
 		assertTrue( c.getOther().getOther().getOther()==c);
 		assertTrue( c.getAnyEntity()==c.getOther() );
 		assertEquals( 3, doDelete( s, "from Universe" ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteEmpty() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		assertEquals( 0, doDelete( s, "from Simple" ) );
 		assertEquals( 0, doDelete( s, "from Universe" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLocking() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setCount(1);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		Simple s4 = new Simple( Long.valueOf(4) );
 		s4.setCount(4);
 		Simple s5 = new Simple( Long.valueOf(5) );
-		s5.setCount(5);
+		s5.setCount( 5 );
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		s.save( s4 );
 		s.save( s5 );
-		assertTrue( s.getCurrentLockMode(s1)==LockMode.WRITE );
+		assertTrue( s.getCurrentLockMode( s1 ) == LockMode.WRITE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ || s.getCurrentLockMode(s1)==LockMode.NONE ); //depends if cache is enabled
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
-		s4 = (Simple) s.get(Simple.class, new Long(4), LockMode.UPGRADE_NOWAIT);
+		s4 = (Simple) s.byId( Simple.class ).with( new LockOptions( LockMode.UPGRADE_NOWAIT ) ).load( 4L );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
-		s5 = (Simple) s.get(Simple.class, new Long(5), LockMode.UPGRADE_SKIPLOCKED);
+		s5 = (Simple) s.byId( Simple.class ).with( new LockOptions( LockMode.UPGRADE_SKIPLOCKED ) ).load( 5L );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.UPGRADE );
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.load(Simple.class, new Long(4), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 		s5 = (Simple) s.load(Simple.class, new Long(5), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s.lock(s1, LockMode.UPGRADE_NOWAIT);
 		s.lock(s4, LockMode.NONE);
 		s.lock(s5, LockMode.UPGRADE_SKIPLOCKED);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		tx.commit();
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
 		s.lock(s1, LockMode.READ); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ );
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE_NOWAIT); //upgrade
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE_NOWAIT );
 		s.lock(s4, LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 
 		s4.setName("s4");
 		s.flush();
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
 		s.delete(s1); s.delete(s2); s.delete(s3); s.delete(s4); s.delete(s5);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testObjectType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Parent g = new Parent();
 		Foo foo = new Foo();
 		g.setAny(foo);
 		s.save(g);
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (Parent) s.load( Parent.class, new Long( g.getId() ) );
 		assertTrue( g.getAny()!=null && g.getAny() instanceof FooProxy );
 		s.delete( g.getAny() );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	 @Test
 	public void testLoadAfterNonExists() throws HibernateException, SQLException {
 		Session session = openSession();
 		if ( ( getDialect() instanceof MySQLDialect ) || ( getDialect() instanceof IngresDialect ) ) {
 			session.doWork(
 					new AbstractWork() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							connection.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
 						}
 					}
 			);
 		}
 		session.getTransaction().begin();
 
 		// First, prime the fixture session to think the entity does not exist
 		try {
 			session.load( Simple.class, new Long(-1) );
 			fail();
 		}
 		catch(ObjectNotFoundException onfe) {
 			if (  getDialect() instanceof TeradataDialect ){
 				session.getTransaction().rollback();
 				session.getTransaction().begin();
 			}
 			// this is correct
 		}
 
 		// Next, lets create that entity "under the covers"
 		Session anotherSession = sessionFactory().openSession();
 		anotherSession.beginTransaction();
 		Simple myNewSimple = new Simple( Long.valueOf(-1) );
 		myNewSimple.setName("My under the radar Simple entity");
 		myNewSimple.setAddress("SessionCacheTest.testLoadAfterNonExists");
 		myNewSimple.setCount(1);
 		myNewSimple.setDate( new Date() );
 		myNewSimple.setPay( Float.valueOf( 100000000 ) );
 		anotherSession.save( myNewSimple );
 		anotherSession.getTransaction().commit();
 		anotherSession.close();
 
 		// Now, lets make sure the original session can see the created row...
 		session.clear();
 		try {
 			Simple dummy = (Simple) session.get( Simple.class, Long.valueOf(-1) );
 			assertNotNull("Unable to locate entity Simple with id = -1", dummy);
 			session.delete( dummy );
 		}
 		catch(ObjectNotFoundException onfe) {
 			fail("Unable to locate entity Simple with id = -1");
 		}
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/TrivialProxy.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/TrivialProxy.java
new file mode 100644
index 0000000000..26f066dab1
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/TrivialProxy.java
@@ -0,0 +1,30 @@
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
+package org.hibernate.test.legacy;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface TrivialProxy extends FooProxy {
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java b/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
index f00944be9f..d97bfb286c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
@@ -1,203 +1,204 @@
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
 package org.hibernate.test.lob;
-import java.sql.Blob;
 
-import junit.framework.AssertionFailedError;
-import org.hibernate.dialect.TeradataDialect;
-import org.hibernate.testing.SkipForDialect;
-import org.junit.Assert;
-import org.junit.Test;
+import java.sql.Blob;
 
 import org.hibernate.Hibernate;
-import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.Session;
+import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
+
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
+import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Assert;
+import org.junit.Test;
+import junit.framework.AssertionFailedError;
 
 import static org.junit.Assert.assertNotNull;
 
 /**
  * Tests lazy materialization of data mapped by
  * {@link org.hibernate.type.BlobType}, as well as bounded and unbounded
  * materialization and mutation.
  *
  * @author Steve Ebersole
  */
 @RequiresDialectFeature( DialectChecks.SupportsExpectedLobUsagePattern.class )
 public class BlobLocatorTest extends BaseCoreFunctionalTestCase {
 	private static final long BLOB_SIZE = 10000L;
 
 	public String[] getMappings() {
 		return new String[] { "lob/LobMappings.hbm.xml" };
 	}
 
 	@Test
 	@SkipForDialect(
 			value = TeradataDialect.class,
 			jiraKey = "HHH-6637",
 			comment = "Teradata requires locator to be used in same session where it was created/retrieved"
 	)
 	public void testBoundedBlobLocatorAccess() throws Throwable {
 		byte[] original = buildByteArray( BLOB_SIZE, true );
 		byte[] changed = buildByteArray( BLOB_SIZE, false );
 		byte[] empty = new byte[] {};
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setBlobLocator( s.getLobHelper().createBlob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test mutation via setting the new clob data...
 		if ( getDialect().supportsLobValueChangePropogation() ) {
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+			entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 			entity.getBlobLocator().truncate( 1 );
 			entity.getBlobLocator().setBytes( 1, changed );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+			entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 			assertNotNull( entity.getBlobLocator() );
 			Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 			assertEquals( changed, extractData( entity.getBlobLocator() ) );
 			entity.getBlobLocator().truncate( 1 );
 			entity.getBlobLocator().setBytes( 1, original );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// test mutation via supplying a new clob locator instance...
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+		entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 		assertNotNull( entity.getBlobLocator() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 		entity.setBlobLocator( s.getLobHelper().createBlob( changed ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test empty blob
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( changed, extractData( entity.getBlobLocator() ) );
 		entity.setBlobLocator( s.getLobHelper().createBlob( empty ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		if ( entity.getBlobLocator() != null) {
 			Assert.assertEquals( empty.length, entity.getBlobLocator().length() );
 			assertEquals( empty, extractData( entity.getBlobLocator() ) );
 		}
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck.class,
 			comment = "database/driver does not support materializing a LOB locator outside the owning transaction"
 	)
 	public void testUnboundedBlobLocatorAccess() throws Throwable {
 		// Note: unbounded mutation of the underlying lob data is completely
 		// unsupported; most databases would not allow such a construct anyway.
 		// Thus here we are only testing materialization...
 
 		byte[] original = buildByteArray( BLOB_SIZE, true );
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setBlobLocator( Hibernate.getLobCreator( s ).createBlob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		// load the entity with the clob locator, and close the session/transaction;
 		// at that point it is unbounded...
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		Assert.assertEquals( BLOB_SIZE, entity.getBlobLocator().length() );
 		assertEquals( original, extractData( entity.getBlobLocator() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public static byte[] extractData(Blob blob) throws Exception {
 		return blob.getBytes( 1, ( int ) blob.length() );
 	}
 
 
 	public static byte[] buildByteArray(long size, boolean on) {
 		byte[] data = new byte[(int)size];
 		data[0] = mask( on );
 		for ( int i = 0; i < size; i++ ) {
 			data[i] = mask( on );
 			on = !on;
 		}
 		return data;
 	}
 
 	private static byte mask(boolean on) {
 		return on ? ( byte ) 1 : ( byte ) 0;
 	}
 
 	public static void assertEquals(byte[] val1, byte[] val2) {
 		if ( !ArrayHelper.isEquals( val1, val2 ) ) {
 			throw new AssertionFailedError( "byte arrays did not match" );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java b/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
index 38757534ea..a9c9644403 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
@@ -1,195 +1,195 @@
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
 package org.hibernate.test.lob;
 
 import java.sql.Clob;
 
-import org.hibernate.dialect.TeradataDialect;
-import org.hibernate.testing.SkipForDialect;
-import org.junit.Test;
-
-import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.dialect.SybaseASE157Dialect;
+import org.hibernate.dialect.TeradataDialect;
+import org.hibernate.type.descriptor.java.DataHelper;
+
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
+import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-import org.hibernate.type.descriptor.java.DataHelper;
+import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * Tests lazy materialization of data mapped by
  * {@link org.hibernate.type.ClobType} as well as bounded and unbounded
  * materialization and mutation.
  *
  * @author Steve Ebersole
  */
 @RequiresDialectFeature(
 		value = DialectChecks.SupportsExpectedLobUsagePattern.class,
 		comment = "database/driver does not support expected LOB usage pattern"
 )
 public class ClobLocatorTest extends BaseCoreFunctionalTestCase {
 	private static final int CLOB_SIZE = 10000;
 
 	public String[] getMappings() {
 		return new String[] { "lob/LobMappings.hbm.xml" };
 	}
 
 	@Test
 	@SkipForDialect(
 			value = TeradataDialect.class,
 			jiraKey = "HHH-6637",
 			comment = "Teradata requires locator to be used in same session where it was created/retrieved"
 	)
 	public void testBoundedClobLocatorAccess() throws Throwable {
 		String original = buildString( CLOB_SIZE, 'x' );
 		String changed = buildString( CLOB_SIZE, 'y' );
 		String empty = "";
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setClobLocator( s.getLobHelper().createClob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test mutation via setting the new clob data...
 		if ( getDialect().supportsLobValueChangePropogation() ) {
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+			entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 			entity.getClobLocator().truncate( 1 );
 			entity.getClobLocator().setString( 1, changed );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+			entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 			assertNotNull( entity.getClobLocator() );
 			assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 			assertEquals( changed, extractData( entity.getClobLocator() ) );
 			entity.getClobLocator().truncate( 1 );
 			entity.getClobLocator().setString( 1, original );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// test mutation via supplying a new clob locator instance...
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId(), LockMode.UPGRADE );
+		entity = ( LobHolder ) s.byId( LobHolder.class ).with( LockOptions.UPGRADE ).load( entity.getId() );
 		assertNotNull( entity.getClobLocator() );
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 		entity.setClobLocator( s.getLobHelper().createClob( changed ) );
 		s.getTransaction().commit();
 		s.close();
 
 		// test empty clob
 		if ( !(getDialect() instanceof SybaseASE157Dialect) ) { // Skip for Sybase. HHH-6425
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+			entity = s.get( LobHolder.class, entity.getId() );
 			assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 			assertEquals( changed, extractData( entity.getClobLocator() ) );
 			entity.setClobLocator( s.getLobHelper().createClob( empty ) );
 			s.getTransaction().commit();
 			s.close();
 
 			s = openSession();
 			s.beginTransaction();
-			entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+			entity = s.get( LobHolder.class, entity.getId() );
 			if ( entity.getClobLocator() != null) {
 				assertEquals( empty.length(), entity.getClobLocator().length() );
 				assertEquals( empty, extractData( entity.getClobLocator() ) );
 			}
 			s.delete( entity );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck.class,
 			comment = "database/driver does not support materializing a LOB locator outside the owning transaction"
 	)
 	public void testUnboundedClobLocatorAccess() throws Throwable {
 		// Note: unbounded mutation of the underlying lob data is completely
 		// unsupported; most databases would not allow such a construct anyway.
 		// Thus here we are only testing materialization...
 
 		String original = buildString( CLOB_SIZE, 'x' );
 
 		Session s = openSession();
 		s.beginTransaction();
 		LobHolder entity = new LobHolder();
 		entity.setClobLocator( s.getLobHelper().createClob( original ) );
 		s.save( entity );
 		s.getTransaction().commit();
 		s.close();
 
 		// load the entity with the clob locator, and close the session/transaction;
 		// at that point it is unbounded...
 		s = openSession();
 		s.beginTransaction();
-		entity = ( LobHolder ) s.get( LobHolder.class, entity.getId() );
+		entity = s.get( LobHolder.class, entity.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( CLOB_SIZE, entity.getClobLocator().length() );
 		assertEquals( original, extractData( entity.getClobLocator() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( entity );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public static String extractData(Clob clob) throws Exception {
 		return DataHelper.extractString( clob.getCharacterStream() );
 	}
 
 	public static String buildString(int size, char baseChar) {
 		StringBuilder buff = new StringBuilder();
 		for( int i = 0; i < size; i++ ) {
 			buff.append( baseChar );
 		}
 		return buff.toString();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java b/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
index 620b2b1645..d091534eeb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/locking/LockModeTest.java
@@ -1,250 +1,247 @@
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
 package org.hibernate.test.locking;
 
 import java.util.concurrent.TimeoutException;
 
-import org.hibernate.dialect.TeradataDialect;
-import org.hibernate.testing.SkipForDialects;
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.Session;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.exception.GenericJDBCException;
 import org.hibernate.exception.LockAcquisitionException;
+
 import org.hibernate.testing.SkipForDialect;
+import org.hibernate.testing.SkipForDialects;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.async.Executable;
 import org.hibernate.testing.async.TimedExecutor;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Test;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * Make sure that directly specifying lock modes, even though deprecated, continues to work until removed.
  *
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-5275")
 @SkipForDialects( {
 @SkipForDialect(value=SybaseASE15Dialect.class, strictMatching=true,
-		comment = "skip this test on Sybase ASE 15.5, but run it on 15.7, see HHH-6820"),
+		comment = "skip this test on Sybase ASE 15.5, but run it on 15.7, see HHH-6820")
 })
 public class LockModeTest extends BaseCoreFunctionalTestCase {
 	private Long id;
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return  new Class[] { A.class };
 	}
 
 	@Override
 	public void prepareTest() throws Exception {
 		Session session = sessionFactory().openSession();
 		session.beginTransaction();
 		id = (Long) session.save( new A( "it" ) );
 		session.getTransaction().commit();
 		session.close();
 	}
 	@Override
 	protected boolean isCleanupTestDataRequired(){return true;}
 
 	@Test
 	@SuppressWarnings( {"deprecation"})
 	public void testLoading() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
-			A it = (A) s1.get( A.class, id, LockMode.PESSIMISTIC_WRITE );
+			A it = (A) s1.byId( A.class ).with( LockOptions.UPGRADE ).load( id );
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testLegacyCriteria() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createCriteria( A.class )
 					.setLockMode( LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testLegacyCriteriaAliasSpecific() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createCriteria( A.class )
 					.setLockMode( "this", LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testQuery() {
 		// open a session, begin a transaction and lock row
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		try {
 			A it = (A) s1.createQuery( "from A a" )
 					.setLockMode( "a", LockMode.PESSIMISTIC_WRITE )
 					.uniqueResult();
 			// make sure we got it
 			assertNotNull( it );
 
 			// that initial transaction is still active and so the lock should still be held.
 			// Lets open another session/transaction and verify that we cannot update the row
 			nowAttemptToUpdateRow();
 		}
 		finally {
 			s1.getTransaction().commit();
 			s1.close();
 		}
 	}
 
 	@Test
 	public void testQueryUsingLockOptions() {
 		// todo : need an association here to make sure the alias-specific lock modes are applied correctly
 		Session s1 = sessionFactory().openSession();
 		s1.beginTransaction();
 		s1.createQuery( "from A a" )
 				.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
 				.uniqueResult();
 		s1.createQuery( "from A a" )
 				.setLockOptions( new LockOptions().setAliasSpecificLockMode( "a", LockMode.PESSIMISTIC_WRITE ) )
 				.uniqueResult();
 		s1.getTransaction().commit();
 		s1.close();
 	}
 
 	private void nowAttemptToUpdateRow() {
 		// here we just need to open a new connection (database session and transaction) and make sure that
 		// we are not allowed to acquire exclusive locks to that row and/or write to that row.  That may take
 		// one of two forms:
 		//		1) either the get-with-lock or the update fails immediately with a sql error
 		//		2) either the get-with-lock or the update blocks indefinitely (in real world, it would block
 		//			until the txn in the calling method completed.
 		// To be able to cater to the second type, we run this block in a separate thread to be able to "time it out"
 
 		try {
 			new TimedExecutor( 10*1000, 1*1000 ).execute(
 					new Executable() {
 						Session s;
 
 						@Override
 						public void execute() {
 							s = sessionFactory().openSession();
 							s.beginTransaction();
 							try {
 								// load with write lock to deal with databases that block (wait indefinitely) direct attempts
 								// to write a locked row
 								A it = (A) s.get(
 										A.class,
 										id,
 										new LockOptions( LockMode.PESSIMISTIC_WRITE ).setTimeOut( LockOptions.NO_WAIT )
 								);
 								it.setValue( "changed" );
 								s.flush();
 								fail( "Pessimistic lock not obtained/held" );
 							}
 							catch ( Exception e ) {
 								// grr, exception can be any number of types based on database
 								// 		see HHH-6887
 								if ( LockAcquisitionException.class.isInstance( e )
 										|| GenericJDBCException.class.isInstance( e )
 										|| PessimisticLockException.class.isInstance( e ) ) {
 									// "ok"
 								}
 								else {
 									fail( "Unexpected error type testing pessimistic locking : " + e.getClass().getName() );
 								}
 							}
 							finally {
 								shutDown();
 							}
 						}
 
 						private void shutDown() {
 							try {
 								s.getTransaction().rollback();
 								s.close();
 							}
 							catch (Exception ignore) {
 							}
 							s = null;
 						}
 
 						@Override
 						public void timedOut() {
 							s.cancelQuery();
 							shutDown();
 						}
 					}
 			);
 		}
 		catch (TimeoutException e) {
 			// timeout is ok, see rule #2 above
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ops/GetLoadTest.java b/hibernate-core/src/test/java/org/hibernate/test/ops/GetLoadTest.java
index b72b7a3c71..cf3faf8f33 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/ops/GetLoadTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/ops/GetLoadTest.java
@@ -1,150 +1,150 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.ops;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 
 /**
  * @author Gavin King
  */
 public class GetLoadTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
 		cfg.setProperty(Environment.STATEMENT_BATCH_SIZE, "0");
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] { "ops/Node.hbm.xml", "ops/Employer.hbm.xml" };
 	}
 
 	@Test
 	public void testGetLoad() {
 		clearCounts();
 		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Employer emp = new Employer();
 		s.persist(emp);
 		Node node = new Node("foo");
 		Node parent = new Node("bar");
 		parent.addChild(node);
 		s.persist(parent);
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		emp = (Employer) s.get(Employer.class, emp.getId());
 		assertTrue( Hibernate.isInitialized(emp) );
 		assertFalse( Hibernate.isInitialized(emp.getEmployees()) );
 		node = (Node) s.get(Node.class, node.getName());
 		assertTrue( Hibernate.isInitialized(node) );
 		assertFalse( Hibernate.isInitialized(node.getChildren()) );
 		assertFalse( Hibernate.isInitialized(node.getParent()) );
 		assertNull( s.get(Node.class, "xyz") );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		emp = (Employer) s.load(Employer.class, emp.getId());
 		emp.getId();
 		assertFalse( Hibernate.isInitialized(emp) );
 		node = (Node) s.load(Node.class, node.getName());
 		assertEquals( node.getName(), "foo" );
 		assertFalse( Hibernate.isInitialized(node) );
 		tx.commit();
 		s.close();
 	
 		s = openSession();
 		tx = s.beginTransaction();
 		emp = (Employer) s.get("org.hibernate.test.ops.Employer", emp.getId());
 		assertTrue( Hibernate.isInitialized(emp) );
 		node = (Node) s.get("org.hibernate.test.ops.Node", node.getName());
 		assertTrue( Hibernate.isInitialized(node) );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		emp = (Employer) s.load("org.hibernate.test.ops.Employer", emp.getId());
 		emp.getId();
 		assertFalse( Hibernate.isInitialized(emp) );
 		node = (Node) s.load("org.hibernate.test.ops.Node", node.getName());
 		assertEquals( node.getName(), "foo" );
 		assertFalse( Hibernate.isInitialized(node) );
 		tx.commit();
 		s.close();
 		
 		assertFetchCount(0);
 	}
 
 	@Test
 	public void testGetAfterDelete() {
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 		Employer emp = new Employer();
 		s.persist( emp );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( emp );
-		emp = ( Employer ) s.get( Employee.class, emp.getId() );
+		emp = s.get( Employer.class, emp.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertNull( "get did not return null after delete", emp );
 	}
 
 	private void clearCounts() {
 		sessionFactory().getStatistics().clear();
 	}
 	
 	private void assertFetchCount(int count) {
 		int fetches = (int) sessionFactory().getStatistics().getEntityFetchCount();
 		assertEquals(count, fetches);
 	}
 
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/BasicGetLoadAccessTest.java b/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/BasicGetLoadAccessTest.java
new file mode 100644
index 0000000000..4f7e9670df
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/BasicGetLoadAccessTest.java
@@ -0,0 +1,158 @@
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
+package org.hibernate.test.ops.genericApi;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
+import org.hibernate.Session;
+import org.hibernate.annotations.GenericGenerator;
+import org.hibernate.boot.MetadataSources;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicGetLoadAccessTest extends BaseNonConfigCoreFunctionalTestCase {
+	@Override
+	protected void applyMetadataSources(MetadataSources metadataSources) {
+		super.applyMetadataSources( metadataSources );
+		metadataSources.addAnnotatedClass( User.class );
+	}
+
+	@Entity( name = "User" )
+	@Table( name = "user" )
+	public static class User {
+		private Integer id;
+		private String name;
+
+		public User() {
+		}
+
+		public User(String name) {
+			this.name = name;
+		}
+
+		@Id
+		@GeneratedValue( generator = "increment" )
+		@GenericGenerator( name = "increment", strategy = "increment" )
+		public Integer getId() {
+			return id;
+		}
+
+		public void setId(Integer id) {
+			this.id = id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+	}
+
+	@Test
+	public void testIt() {
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// create a row
+		Session s = openSession();
+		s.beginTransaction();
+		s.save( new User( "steve" ) );
+		s.getTransaction().commit();
+		s.close();
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test `get` access
+		s = openSession();
+		s.beginTransaction();
+		User user = s.get( User.class, 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.get( User.class, 1, LockMode.PESSIMISTIC_WRITE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.get( User.class, 1, LockOptions.UPGRADE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( User.class ).load( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( User.class ).with( LockOptions.UPGRADE ).load( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test `load` access
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( User.class, 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( User.class, 1, LockMode.PESSIMISTIC_WRITE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( User.class, 1, LockOptions.UPGRADE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( User.class ).getReference( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( User.class ).with( LockOptions.UPGRADE ).getReference( 1 );
+		s.getTransaction().commit();
+		s.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/ProxiedGetLoadAccessTest.java b/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/ProxiedGetLoadAccessTest.java
new file mode 100644
index 0000000000..fceb7ae82b
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/ops/genericApi/ProxiedGetLoadAccessTest.java
@@ -0,0 +1,180 @@
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
+package org.hibernate.test.ops.genericApi;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
+import org.hibernate.Session;
+import org.hibernate.annotations.GenericGenerator;
+import org.hibernate.annotations.Proxy;
+import org.hibernate.boot.MetadataSources;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ProxiedGetLoadAccessTest extends BaseNonConfigCoreFunctionalTestCase {
+	@Override
+	protected void applyMetadataSources(MetadataSources metadataSources) {
+		super.applyMetadataSources( metadataSources );
+		metadataSources.addAnnotatedClass( UserImpl.class );
+	}
+
+	public static interface User {
+		public Integer getId();
+		public String getName();
+		public void setName(String name);
+	}
+
+	@Entity( name = "User" )
+	@Table( name = "user" )
+	@Proxy( proxyClass = User.class )
+	public static class UserImpl implements User {
+		private Integer id;
+		private String name;
+
+		public UserImpl() {
+		}
+
+		public UserImpl(String name) {
+			this.name = name;
+		}
+
+		@Id
+		@GeneratedValue( generator = "increment" )
+		@GenericGenerator( name = "increment", strategy = "increment" )
+		@Override
+		public Integer getId() {
+			return id;
+		}
+
+		public void setId(Integer id) {
+			this.id = id;
+		}
+
+		@Override
+		public String getName() {
+			return name;
+		}
+
+		@Override
+		public void setName(String name) {
+			this.name = name;
+		}
+	}
+
+	@Test
+	public void testIt() {
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// create a row
+		Session s = openSession();
+		s.beginTransaction();
+		s.save( new UserImpl( "steve" ) );
+		s.getTransaction().commit();
+		s.close();
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test `get` access
+		s = openSession();
+		s.beginTransaction();
+		// THis technically works
+		User user = s.get( UserImpl.class, 1 );
+		user = s.get( User.class, 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.get( UserImpl.class, 1, LockMode.PESSIMISTIC_WRITE );
+		user = s.get( User.class, 1, LockMode.PESSIMISTIC_WRITE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.get( UserImpl.class, 1, LockOptions.UPGRADE );
+		user = s.get( User.class, 1, LockOptions.UPGRADE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( UserImpl.class ).load( 1 );
+		user = s.byId( User.class ).load( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( UserImpl.class ).with( LockOptions.UPGRADE ).load( 1 );
+		user = s.byId( User.class ).with( LockOptions.UPGRADE ).load( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// test `load` access
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( UserImpl.class, 1 );
+		user = s.load( User.class, 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( UserImpl.class, 1, LockMode.PESSIMISTIC_WRITE );
+		user = s.load( User.class, 1, LockMode.PESSIMISTIC_WRITE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.load( UserImpl.class, 1, LockOptions.UPGRADE );
+		user = s.load( User.class, 1, LockOptions.UPGRADE );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( UserImpl.class ).getReference( 1 );
+		user = s.byId( User.class ).getReference( 1 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		user = s.byId( UserImpl.class ).with( LockOptions.UPGRADE ).getReference( 1 );
+		user = s.byId( User.class ).with( LockOptions.UPGRADE ).getReference( 1 );
+		s.getTransaction().commit();
+		s.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
index 9b3a098290..764082abe9 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
@@ -1,510 +1,510 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.proxy;
 
 import java.math.BigDecimal;
 import java.util.List;
 
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.SessionImpl;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class ProxyTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "proxy/DataPoint.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" ); // problem on HSQLDB (go figure)
 	}
 
 	@Test
 	public void testFinalizeFiltered() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 
 		try {
 			dp.getClass().getDeclaredMethod( "finalize", (Class[]) null );
 			fail();
 
 		}
 		catch (NoSuchMethodException e) {}
 
 		s.delete(dp);
 		t.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testProxyException() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 
 		try {
 			dp.exception();
 			fail();
 		}
 		catch (Exception e) {
 			assertTrue( e.getClass()==Exception.class );
 		}
 		s.delete(dp);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxySerializationAfterSessionClosed() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		s.close();
 		SerializationHelper.clone( dp );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testInitializedProxySerializationAfterSessionClosed() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		Hibernate.initialize( dp );
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.close();
 		SerializationHelper.clone( dp );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxySerialization() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp.getId();
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp.getDescription();
 		assertTrue( Hibernate.isInitialized(dp) );
 		Object none = s.load( DataPoint.class, new Long(666));
 		assertFalse( Hibernate.isInitialized(none) );
 
 		t.commit();
 		s.disconnect();
 
 		Object[] holder = new Object[] { s, dp, none };
 
 		holder = (Object[]) SerializationHelper.clone(holder);
 		Session sclone = (Session) holder[0];
 		dp = (DataPoint) holder[1];
 		none = holder[2];
 
 		//close the original:
 		s.close();
 
 		t = sclone.beginTransaction();
 
 		DataPoint sdp = (DataPoint) sclone.load( DataPoint.class, new Long( dp.getId() ) );
 		assertSame(dp, sdp);
 		assertFalse(sdp instanceof HibernateProxy);
 		Object snone = sclone.load( DataPoint.class, new Long(666) );
 		assertSame(none, snone);
 		assertTrue(snone instanceof HibernateProxy);
 
 		sclone.delete(dp);
 
 		t.commit();
 		sclone.close();
 
 	}
 
 	@Test
 	public void testProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long(dp.getId() ));
 		assertFalse( Hibernate.isInitialized(dp) );
 		DataPoint dp2 = (DataPoint) s.get( DataPoint.class, new Long(dp.getId()) );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.NONE );
 		assertSame(dp, dp2);
 		assertFalse( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.READ );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long (dp.getId() ));
 		assertFalse( Hibernate.isInitialized(dp) );
-		dp2 = (DataPoint) s.get( DataPoint.class, new Long ( dp.getId() ) , LockMode.READ );
+		dp2 = (DataPoint) s.byId( DataPoint.class ).with( LockOptions.READ ).load( dp.getId() );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long  ( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.createQuery("from DataPoint").uniqueResult();
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSubsequentNonExistentProxyAccess() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		DataPoint proxy = ( DataPoint ) s.load( DataPoint.class, new Long(-1) );
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		try {
 			proxy.getDescription();
 			fail( "proxy access did not fail on non-existent proxy" );
 		}
 		catch( ObjectNotFoundException onfe ) {
 			// expected
 		}
 		catch( Throwable e ) {
 			fail( "unexpected exception type on non-existent proxy access : " + e );
 		}
 		// try it a second (subsequent) time...
 		try {
 			proxy.getDescription();
 			fail( "proxy access did not fail on non-existent proxy" );
 		}
 		catch( ObjectNotFoundException onfe ) {
 			// expected
 		}
 		catch( Throwable e ) {
 			fail( "unexpected exception type on non-existent proxy access : " + e );
 		}
 
 		t.commit();
 		s.close();
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	@Test
 	public void testProxyEviction() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container container = new Container( "container" );
 		container.setOwner( new Owner( "owner" ) );
 		container.setInfo( new Info( "blah blah blah" ) );
 		container.getDataPoints().add( new DataPoint( new BigDecimal( 1 ), new BigDecimal( 1 ), "first data point" ) );
 		container.getDataPoints().add( new DataPoint( new BigDecimal( 2 ), new BigDecimal( 2 ), "second data point" ) );
 		s.save( container );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Container c = ( Container ) s.load( Container.class, container.getId() );
 		assertFalse( Hibernate.isInitialized( c ) );
 		s.evict( c );
 		try {
 			c.getName();
 			fail( "expecting LazyInitializationException" );
 		}
 		catch( LazyInitializationException e ) {
 			// expected result
 		}
 
 		c = ( Container ) s.load( Container.class, container.getId() );
 		assertFalse( Hibernate.isInitialized( c ) );
 		Info i = c.getInfo();
 		assertTrue( Hibernate.isInitialized( c ) );
 		assertFalse( Hibernate.isInitialized( i ) );
 		s.evict( c );
 		try {
 			i.getDetails();
 			fail( "expecting LazyInitializationException" );
 		}
 		catch( LazyInitializationException e ) {
 			// expected result
 		}
 
 		s.delete( c );
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFullyLoadedPCSerialization() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Long lastContainerId = null;
 		int containerCount = 10;
 		int nestedDataPointCount = 5;
 		for ( int c_indx = 0; c_indx < containerCount; c_indx++ ) {
 			Owner owner = new Owner( "Owner #" + c_indx );
 			Container container = new Container( "Container #" + c_indx );
 			container.setOwner( owner );
 			for ( int dp_indx = 0; dp_indx < nestedDataPointCount; dp_indx++ ) {
 				DataPoint dp = new DataPoint();
 				dp.setDescription( "data-point [" + c_indx + ", " + dp_indx + "]" );
 // more HSQLDB fun...
 //				dp.setX( new BigDecimal( c_indx ) );
 				dp.setX( new BigDecimal( c_indx + dp_indx ) );
 				dp.setY( new BigDecimal( dp_indx ) );
 				container.getDataPoints().add( dp );
 			}
 			s.save( container );
 			lastContainerId = container.getId();
 		}
 		t.commit();
 		s.close();
 
 		s = openSession();
 		s.setFlushMode( FlushMode.MANUAL );
 		t = s.beginTransaction();
 		// load the last container as a proxy
 		Container proxy = ( Container ) s.load( Container.class, lastContainerId );
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		// load the rest back into the PC
 		List all = s.createQuery( "from Container as c inner join fetch c.owner inner join fetch c.dataPoints where c.id <> :last" )
 				.setLong( "last", lastContainerId.longValue() )
 				.list();
 		Container container = ( Container ) all.get( 0 );
 		s.delete( container );
 		// force a snapshot retrieval of the proxied container
 		SessionImpl sImpl = ( SessionImpl ) s;
 		sImpl.getPersistenceContext().getDatabaseSnapshot(
 				lastContainerId,
 		        sImpl.getFactory().getEntityPersister( Container.class.getName() )
 		);
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		t.commit();
 
 //		int iterations = 50;
 //		long cumulativeTime = 0;
 //		long cumulativeSize = 0;
 //		for ( int i = 0; i < iterations; i++ ) {
 //			final long start = System.currentTimeMillis();
 //			byte[] bytes = SerializationHelper.serialize( s );
 //			SerializationHelper.deserialize( bytes );
 //			final long end = System.currentTimeMillis();
 //			cumulativeTime += ( end - start );
 //			int size = bytes.length;
 //			cumulativeSize += size;
 ////			System.out.println( "Iteration #" + i + " took " + ( end - start ) + " ms : size = " + size + " bytes" );
 //		}
 //		System.out.println( "Average time : " + ( cumulativeTime / iterations ) + " ms" );
 //		System.out.println( "Average size : " + ( cumulativeSize / iterations ) + " bytes" );
 
 		byte[] bytes = SerializationHelper.serialize( s );
 		SerializationHelper.deserialize( bytes );
 
 		t = s.beginTransaction();
 		int count = s.createQuery( "delete DataPoint" ).executeUpdate();
 		assertEquals( "unexpected DP delete count", ( containerCount * nestedDataPointCount ), count );
 		count = s.createQuery( "delete Container" ).executeUpdate();
 		assertEquals( "unexpected container delete count", containerCount, count );
 		count = s.createQuery( "delete Owner" ).executeUpdate();
 		assertEquals( "unexpected owner delete count", containerCount, count );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testRefreshLockInitializedProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = newPersistentDataPoint( s );
 
 		dp = ( DataPoint ) s.load( DataPoint.class, new Long( dp.getId() ) );
 		dp.getX();
 		assertTrue( Hibernate.isInitialized( dp ) );
 
 		s.refresh( dp, LockOptions.UPGRADE );
 		assertSame( LockOptions.UPGRADE.getLockMode(), s.getCurrentLockMode( dp ) );
 
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-1645", message = "Session.refresh with LockOptions does not work on uninitialized proxies" )
 	public void testRefreshLockUninitializedProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = newPersistentDataPoint( s );
 
 		dp = ( DataPoint ) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized( dp ) );
 
 		s.refresh( dp, LockOptions.UPGRADE );
 		assertSame( LockOptions.UPGRADE.getLockMode(), s.getCurrentLockMode( dp ) );
 
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	private static DataPoint newPersistentDataPoint(Session s) {
 		DataPoint dp = new DataPoint();
 		dp.setDescription( "a data point" );
 		dp.setX( new BigDecimal( 1.0 ) );
 		dp.setY( new BigDecimal( 2.0 ) );
 		s.persist( dp );
 		s.flush();
 		s.clear();
 		return dp;
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-1645", message = "Session.refresh with LockOptions does not work on uninitialized proxies" )
 	public void testRefreshLockUninitializedProxyThenRead() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = newPersistentDataPoint( s );
 
 		dp = ( DataPoint ) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized( dp ) );
 		s.refresh( dp, LockOptions.UPGRADE );
 		dp.getX();
 		assertSame( LockOptions.UPGRADE.getLockMode(), s.getCurrentLockMode( dp ) );
 
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testLockUninitializedProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = newPersistentDataPoint( s );
 
 		dp = ( DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized( dp ) );
 		s.buildLockRequest( LockOptions.UPGRADE ).lock( dp );
 		assertSame( LockOptions.UPGRADE.getLockMode(), s.getCurrentLockMode( dp ) );
 
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ClassLoaderServiceImplTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ClassLoaderServiceImplTest.java
index 1f83785fa7..6119cc9578 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ClassLoaderServiceImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ClassLoaderServiceImplTest.java
@@ -1,156 +1,157 @@
 package org.hibernate.test.service;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.Enumeration;
 import javax.persistence.Entity;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.ConfigHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.TestForIssue;
 import org.junit.Assert;
 import org.junit.Test;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Artem V. Navrotskiy
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class ClassLoaderServiceImplTest {
     /**
      * Test for bug: HHH-7084
      */
     @Test
     public void testSystemClassLoaderNotOverriding() throws IOException, ClassNotFoundException {
         Class<?> testClass = Entity.class;
 
         // Check that class is accessible by SystemClassLoader.
         ClassLoader.getSystemClassLoader().loadClass(testClass.getName());
 
         // Create ClassLoader with overridden class.
         TestClassLoader anotherLoader = new TestClassLoader();
         anotherLoader.overrideClass(testClass);
         Class<?> anotherClass = anotherLoader.loadClass(testClass.getName());
         Assert.assertNotSame( testClass, anotherClass );
 
         // Check ClassLoaderServiceImpl().classForName() returns correct class (not from current ClassLoader).
         ClassLoaderServiceImpl loaderService = new ClassLoaderServiceImpl(anotherLoader);
         Class<Object> objectClass = loaderService.classForName(testClass.getName());
         Assert.assertSame("Should not return class loaded from the parent classloader of ClassLoaderServiceImpl",
 				objectClass, anotherClass);
     }
     
     /**
      * HHH-8363 discovered multiple leaks within CLS.  Most notably, it wasn't getting GC'd due to holding
      * references to ServiceLoaders.  Ensure that the addition of Stoppable functionality cleans up properly.
      * 
      * TODO: Is there a way to test that the ServiceLoader was actually reset?
      */
     @Test
     @TestForIssue(jiraKey = "HHH-8363")
     public void testStoppableClassLoaderService() {
     	final BootstrapServiceRegistryBuilder bootstrapBuilder = new BootstrapServiceRegistryBuilder();
     	bootstrapBuilder.applyClassLoader( new TestClassLoader() );
     	final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder( bootstrapBuilder.build() ).build();
     	final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
     	
     	TestIntegrator testIntegrator1 = findTestIntegrator( classLoaderService );
     	assertNotNull( testIntegrator1 );
     	
     	TestIntegrator testIntegrator2 = findTestIntegrator( classLoaderService );
     	assertNotNull( testIntegrator2 );
     	
     	assertSame( testIntegrator1, testIntegrator2 );
     	
     	StandardServiceRegistryBuilder.destroy( serviceRegistry );
     	
     	try {
     		findTestIntegrator( classLoaderService );
     		Assert.fail("Should have thrown an HibernateException -- the ClassLoaderService instance was closed.");
     	}
     	catch (HibernateException e) {
     		String message = e.getMessage();
     		Assert.assertEquals( "HHH000469: The ClassLoaderService can not be reused. This instance was stopped already.", message);
     	}
     }
 
 	private TestIntegrator findTestIntegrator(ClassLoaderService classLoaderService) {
 		for ( Integrator integrator : classLoaderService.loadJavaServices( Integrator.class ) ) {
 			if ( integrator instanceof TestIntegrator ) {
 				return (TestIntegrator) integrator;
 			}
 		}
 		return null;
 	}
 
     private static class TestClassLoader extends ClassLoader {
     	
     	/**
     	 * testStoppableClassLoaderService() needs a custom JDK service implementation.  Rather than using a real one
     	 * on the test classpath, force it in here.
     	 */
     	@Override
         protected Enumeration<URL> findResources(String name) throws IOException {
     		if (name.equals( "META-INF/services/org.hibernate.integrator.spi.Integrator" )) {
     			final URL serviceUrl = ConfigHelper.findAsResource(
     					"org/hibernate/test/service/org.hibernate.integrator.spi.Integrator" );
     			return new Enumeration<URL>() {
         			boolean hasMore = true;
         			
     				@Override
     				public boolean hasMoreElements() {
     					return hasMore;
     				}
 
     				@Override
     				public URL nextElement() {
     					hasMore = false;
     					return serviceUrl;
     				}
     			};
     		}
     		else {
-    			return java.util.Collections.emptyEnumeration();
+    			return java.util.Collections.enumeration( java.util.Collections.<URL>emptyList() );
     		}
         }
     	
         /**
          * Reloading class from binary file.
          *
          * @param originalClass Original class.
          * @throws IOException .
          */
         public void overrideClass(final Class<?> originalClass) throws IOException {
             String originalPath = "/" + originalClass.getName().replaceAll("\\.", "/") + ".class";
             InputStream inputStream = originalClass.getResourceAsStream(originalPath);
             Assert.assertNotNull(inputStream);
             try {
                 byte[] data = toByteArray( inputStream );
                 defineClass(originalClass.getName(), data, 0, data.length);
             } finally {
                 inputStream.close();
             }
         }
 
 		private byte[] toByteArray(InputStream inputStream) throws IOException {
 			ByteArrayOutputStream out = new ByteArrayOutputStream();
 			int read;
 			byte[] slice = new byte[2000];
 			while ( (read = inputStream.read(slice, 0, slice.length) ) != -1) {
 			  out.write( slice, 0, read );
 			}
 			out.flush();
 			return out.toByteArray();
 		}
     }
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/QueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/QueryImpl.java
index 036121fafd..0a95568e88 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/QueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/QueryImpl.java
@@ -1,576 +1,581 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.jpa.internal;
 
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.ParameterMode;
 import javax.persistence.PersistenceException;
 import javax.persistence.Query;
 import javax.persistence.TemporalType;
 import javax.persistence.TypedQuery;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NamedParameterDescriptor;
 import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.internal.SQLQueryImpl;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateQuery;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.jpa.spi.AbstractEntityManagerImpl;
 import org.hibernate.jpa.spi.AbstractQueryImpl;
 import org.hibernate.jpa.spi.ParameterBind;
 import org.hibernate.jpa.spi.ParameterRegistration;
 import org.hibernate.type.CompositeCustomType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 import static javax.persistence.TemporalType.DATE;
 import static javax.persistence.TemporalType.TIME;
 import static javax.persistence.TemporalType.TIMESTAMP;
 
 /**
  * Hibernate implementation of both the {@link Query} and {@link TypedQuery} contracts.
  *
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class QueryImpl<X> extends AbstractQueryImpl<X> implements TypedQuery<X>, HibernateQuery, org.hibernate.ejb.HibernateQuery {
 
     public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class, QueryImpl.class.getName());
 
 	private org.hibernate.Query query;
 
 	public QueryImpl(org.hibernate.Query query, AbstractEntityManagerImpl em) {
 		this( query, em, Collections.<String, Class>emptyMap() );
 	}
 
 	public QueryImpl(
 			org.hibernate.Query query,
 			AbstractEntityManagerImpl em,
 			Map<String,Class> namedParameterTypeRedefinitions) {
 		super( em );
 		this.query = query;
 		extractParameterInfo( namedParameterTypeRedefinitions );
 	}
 
 	@Override
 	protected boolean isNativeSqlQuery() {
 		return SQLQuery.class.isInstance( query );
 	}
 
 	@Override
 	protected boolean isSelectQuery() {
 		if ( isNativeSqlQuery() ) {
 			throw new IllegalStateException( "Cannot tell if native SQL query is SELECT query" );
 		}
 
 		return org.hibernate.internal.QueryImpl.class.cast( query ).isSelect();
 	}
 
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	private void extractParameterInfo(Map<String,Class> namedParameterTypeRedefinition) {
 		if ( ! org.hibernate.internal.AbstractQueryImpl.class.isInstance( query ) ) {
 			throw new IllegalStateException( "Unknown query type for parameter extraction" );
 		}
 
 		boolean hadJpaPositionalParameters = false;
 
 		final ParameterMetadata parameterMetadata = org.hibernate.internal.AbstractQueryImpl.class.cast( query ).getParameterMetadata();
 
 		// extract named params
 		for ( String name : (Set<String>) parameterMetadata.getNamedParameterNames() ) {
 			final NamedParameterDescriptor descriptor = parameterMetadata.getNamedParameterDescriptor( name );
 			Class javaType = namedParameterTypeRedefinition.get( name );
 			if ( javaType != null && mightNeedRedefinition( javaType, descriptor.getExpectedType() ) ) {
 				descriptor.resetExpectedType(
 						sfi().getTypeResolver().heuristicType( javaType.getName() )
 				);
 			}
 			else if ( descriptor.getExpectedType() != null ) {
 				javaType = descriptor.getExpectedType().getReturnedClass();
 			}
 
 			if ( descriptor.isJpaStyle() ) {
 				hadJpaPositionalParameters = true;
 				final Integer position = Integer.valueOf( name );
 				registerParameter( new JpaPositionalParameterRegistrationImpl( this, query, position, javaType ) );
 			}
 			else {
 				registerParameter( new ParameterRegistrationImpl( this, query, name, javaType ) );
 			}
 		}
 
 		if ( hadJpaPositionalParameters ) {
 			if ( parameterMetadata.getOrdinalParameterCount() > 0 ) {
 				throw new IllegalArgumentException(
 						"Cannot mix JPA positional parameters and native Hibernate positional/ordinal parameters"
 				);
 			}
 		}
 
 		// extract Hibernate native positional parameters
 		for ( int i = 0, max = parameterMetadata.getOrdinalParameterCount(); i < max; i++ ) {
 			final OrdinalParameterDescriptor descriptor = parameterMetadata.getOrdinalParameterDescriptor( i + 1 );
 			Class javaType = descriptor.getExpectedType() == null ? null : descriptor.getExpectedType().getReturnedClass();
 			registerParameter( new ParameterRegistrationImpl( this, query, i+1, javaType ) );
 		}
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) getEntityManager().getFactory().getSessionFactory();
 	}
 
 	private boolean mightNeedRedefinition(Class javaType, Type expectedType) {
 		// only redefine dates/times/timestamps that are not wrapped in a CompositeCustomType
 		if ( expectedType == null )
 			return java.util.Date.class.isAssignableFrom( javaType );
 		else
 			return java.util.Date.class.isAssignableFrom( javaType )
 					&& !CompositeCustomType.class.isAssignableFrom( expectedType.getClass() );
 	}
 
 	private static class ParameterRegistrationImpl<T> implements ParameterRegistration<T> {
 		private final Query jpaQuery;
 		private final org.hibernate.Query nativeQuery;
 
 		private final String name;
 		private final Integer position;
 		private final Class<T> javaType;
 
 		private ParameterBind<T> bind;
 
 		protected ParameterRegistrationImpl(
 				Query jpaQuery,
 				org.hibernate.Query nativeQuery,
 				String name,
 				Class<T> javaType) {
 			this.jpaQuery = jpaQuery;
 			this.nativeQuery = nativeQuery;
 			this.name = name;
 			this.javaType = javaType;
 			this.position = null;
 		}
 
 		protected ParameterRegistrationImpl(
 				Query jpaQuery,
 				org.hibernate.Query nativeQuery,
 				Integer position,
 				Class<T> javaType) {
 			this.jpaQuery = jpaQuery;
 			this.nativeQuery = nativeQuery;
 			this.position = position;
 			this.javaType = javaType;
 			this.name = null;
 		}
 
 		@Override
 		public boolean isJpaPositionalParameter() {
 			return false;
 		}
 
 		@Override
 		public Query getQuery() {
 			return jpaQuery;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 
 		@Override
 		public Integer getPosition() {
 			return position;
 		}
 
 		@Override
 		public Class<T> getParameterType() {
 			return javaType;
 		}
 
 		@Override
 		public ParameterMode getMode() {
 			// implicitly
 			return ParameterMode.IN;
 		}
 
 		@Override
 		public boolean isBindable() {
 			// again, implicitly
 			return true;
 		}
 
 		@Override
 		public void bindValue(T value) {
 			validateBinding( getParameterType(), value, null );
 
 			if ( name != null ) {
 				if ( value instanceof Collection ) {
 					nativeQuery.setParameterList( name, (Collection) value );
 				}
 				else {
 					nativeQuery.setParameter( name, value );
 				}
 			}
 			else {
 				nativeQuery.setParameter( position - 1, value );
 			}
 
 			bind = new ParameterBindImpl<T>( value, null );
 		}
 
 		@Override
 		public void bindValue(T value, TemporalType specifiedTemporalType) {
 			validateBinding( getParameterType(), value, specifiedTemporalType );
 
 			if ( Date.class.isInstance( value ) ) {
 				if ( name != null ) {
 					if ( specifiedTemporalType == DATE ) {
 						nativeQuery.setDate( name, (Date) value );
 					}
 					else if ( specifiedTemporalType == TIME ) {
 						nativeQuery.setTime( name, (Date) value );
 					}
 					else if ( specifiedTemporalType == TIMESTAMP ) {
 						nativeQuery.setTimestamp( name, (Date) value );
 					}
 				}
 				else {
 					if ( specifiedTemporalType == DATE ) {
 						nativeQuery.setDate( position - 1, (Date) value );
 					}
 					else if ( specifiedTemporalType == TIME ) {
 						nativeQuery.setTime( position - 1, (Date) value );
 					}
 					else if ( specifiedTemporalType == TIMESTAMP ) {
 						nativeQuery.setTimestamp( position - 1, (Date) value );
 					}
 				}
 			}
 			else if ( Calendar.class.isInstance( value ) ) {
 				if ( name != null ) {
 					if ( specifiedTemporalType == DATE ) {
 						nativeQuery.setCalendarDate( name, (Calendar) value );
 					}
 					else if ( specifiedTemporalType == TIME ) {
 						throw new IllegalArgumentException( "not yet implemented" );
 					}
 					else if ( specifiedTemporalType == TIMESTAMP ) {
 						nativeQuery.setCalendar( name, (Calendar) value );
 					}
 				}
 				else {
 					if ( specifiedTemporalType == DATE ) {
 						nativeQuery.setCalendarDate( position - 1, (Calendar) value );
 					}
 					else if ( specifiedTemporalType == TIME ) {
 						throw new IllegalArgumentException( "not yet implemented" );
 					}
 					else if ( specifiedTemporalType == TIMESTAMP ) {
 						nativeQuery.setCalendar( position - 1, (Calendar) value );
 					}
 				}
 			}
 			else {
 				throw new IllegalArgumentException(
 						"Unexpected type [" + value + "] passed with TemporalType; expecting Date or Calendar"
 				);
 			}
 
 			bind = new ParameterBindImpl<T>( value, specifiedTemporalType );
 		}
 
 		@Override
 		public ParameterBind<T> getBind() {
 			return bind;
 		}
 	}
 
 	/**
 	 * Specialized handling for JPA "positional parameters".
 	 *
 	 * @param <T> The parameter type type.
 	 */
 	public static class JpaPositionalParameterRegistrationImpl<T> extends ParameterRegistrationImpl<T> {
 		final Integer position;
 
 		protected JpaPositionalParameterRegistrationImpl(
 				Query jpaQuery,
 				org.hibernate.Query nativeQuery,
 				Integer position,
 				Class<T> javaType) {
 			super( jpaQuery, nativeQuery, position.toString(), javaType );
 			this.position = position;
 		}
 
 		@Override
 		public String getName() {
 			return null;
 		}
 
 		@Override
 		public Integer getPosition() {
 			return position;
 		}
 
 		@Override
 		public boolean isJpaPositionalParameter() {
 			return true;
 		}
 	}
 
 	public org.hibernate.Query getHibernateQuery() {
 		return query;
 	}
 
 	@Override
     protected int internalExecuteUpdate() {
 		return query.executeUpdate();
 	}
 
 	@Override
     protected void applyMaxResults(int maxResults) {
 		query.setMaxResults( maxResults );
 	}
 
 	@Override
     protected void applyFirstResult(int firstResult) {
 		query.setFirstResult( firstResult );
 	}
 
 	@Override
     protected boolean applyTimeoutHint(int timeout) {
 		query.setTimeout( timeout );
 		return true;
 	}
 
 	@Override
     protected boolean applyCommentHint(String comment) {
 		query.setComment( comment );
 		return true;
 	}
 
 	@Override
     protected boolean applyFetchSizeHint(int fetchSize) {
 		query.setFetchSize( fetchSize );
 		return true;
 	}
 
 	@Override
     protected boolean applyCacheableHint(boolean isCacheable) {
 		query.setCacheable( isCacheable );
 		return true;
 	}
 
 	@Override
     protected boolean applyCacheRegionHint(String regionName) {
 		query.setCacheRegion( regionName );
 		return true;
 	}
 
 	@Override
     protected boolean applyReadOnlyHint(boolean isReadOnly) {
 		query.setReadOnly( isReadOnly );
 		return true;
 	}
 
 	@Override
     protected boolean applyCacheModeHint(CacheMode cacheMode) {
 		query.setCacheMode( cacheMode );
 		return true;
 	}
 
 	@Override
     protected boolean applyFlushModeHint(FlushMode flushMode) {
 		query.setFlushMode( flushMode );
 		return true;
 	}
 
 	@Override
 	protected boolean canApplyAliasSpecificLockModeHints() {
 		return org.hibernate.internal.QueryImpl.class.isInstance( query ) || SQLQueryImpl.class.isInstance( query );
 	}
 
 	@Override
 	protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
 		query.getLockOptions().setAliasSpecificLockMode( alias, lockMode );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	public List<X> getResultList() {
 		getEntityManager().checkOpen( true );
 		checkTransaction();
 		beforeQuery();
 		try {
 			return list();
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException(he);
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException(e);
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * For JPA native SQL queries, we may need to perform a flush before executing the query.
 	 */
 	private void beforeQuery() {
 		final org.hibernate.Query query = getHibernateQuery();
 		if ( ! SQLQuery.class.isInstance( query ) ) {
 			// this need only exists for native SQL queries, not JPQL or Criteria queries (both of which do
 			// partial auto flushing already).
 			return;
 		}
 
 		final SQLQuery sqlQuery = (SQLQuery) query;
 		if ( sqlQuery.getSynchronizedQuerySpaces() != null && ! sqlQuery.getSynchronizedQuerySpaces().isEmpty() ) {
 			// The application defined query spaces on the Hibernate native SQLQuery which means the query will already
 			// perform a partial flush according to the defined query spaces, no need to do a full flush.
 			return;
 		}
 
 		// otherwise we need to flush.  the query itself is not required to execute in a transaction; if there is
 		// no transaction, the flush would throw a TransactionRequiredException which would potentially break existing
 		// apps, so we only do the flush if a transaction is in progress.
 		if ( getEntityManager().isTransactionInProgress() ) {
 			getEntityManager().flush();
 		}
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	public X getSingleResult() {
 		getEntityManager().checkOpen( true );
 		checkTransaction();
 		beforeQuery();
 		try {
 			final List<X> result = list();
 
 			if ( result.size() == 0 ) {
 				NoResultException nre = new NoResultException( "No entity found for query" );
 				getEntityManager().handlePersistenceException( nre );
 				throw nre;
 			}
 			else if ( result.size() > 1 ) {
 				final Set<X> uniqueResult = new HashSet<X>(result);
 				if ( uniqueResult.size() > 1 ) {
 					NonUniqueResultException nure = new NonUniqueResultException( "result returns more than one elements" );
 					getEntityManager().handlePersistenceException( nure );
 					throw nure;
 				}
 				else {
 					return uniqueResult.iterator().next();
 				}
 			}
 			else {
 				return result.get( 0 );
 			}
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException(he);
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException(e);
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <T> T unwrap(Class<T> tClass) {
 		if ( org.hibernate.Query.class.isAssignableFrom( tClass ) ) {
 			return (T) query;
 		}
 		if ( QueryImpl.class.isAssignableFrom( tClass ) ) {
 			return (T) this;
 		}
 		if ( HibernateQuery.class.isAssignableFrom( tClass ) ) {
 			return (T) this;
 		}
 
 		throw new PersistenceException(
 				String.format(
 						"Unsure how to unwrap %s impl [%s] as requested type [%s]",
 						Query.class.getSimpleName(),
 						this.getClass().getName(),
 						tClass.getName()
 				)
 		);
 	}
 
 	@Override
 	protected void internalApplyLockMode(javax.persistence.LockModeType lockModeType) {
 		query.getLockOptions().setLockMode( LockModeTypeHelper.getLockMode( lockModeType ) );
 		if ( getHints() != null && getHints().containsKey( AvailableSettings.LOCK_TIMEOUT ) ) {
 			applyLockTimeoutHint( ConfigurationHelper.getInteger( getHints().get( AvailableSettings.LOCK_TIMEOUT ) ) );
 		}
 	}
 
 	@Override
 	protected boolean applyLockTimeoutHint(int timeout) {
 		query.getLockOptions().setTimeOut( timeout );
 		return true;
 	}
 
 	private List<X> list() {
 		if (getEntityGraphQueryHint() != null) {
 			SessionImplementor sessionImpl = (SessionImplementor) getEntityManager().getSession();
-			HQLQueryPlan entityGraphQueryPlan = new HQLQueryPlan( getHibernateQuery().getQueryString(), false,
-					sessionImpl.getEnabledFilters(), sessionImpl.getFactory(), getEntityGraphQueryHint() );
+			HQLQueryPlan entityGraphQueryPlan = new HQLQueryPlan(
+					getHibernateQuery().getQueryString(),
+					false,
+					sessionImpl.getLoadQueryInfluencers().getEnabledFilters(),
+					sessionImpl.getFactory(),
+					getEntityGraphQueryHint()
+			);
 			// Safe to assume QueryImpl at this point.
 			unwrap( org.hibernate.internal.QueryImpl.class ).setQueryPlan( entityGraphQueryPlan );
 		}
 		return query.list();
 	}
 
 }
