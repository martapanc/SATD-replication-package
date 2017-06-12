74/report.java
Satd-method: 
********************************************
********************************************
74/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- /dev/null
+++ b/build.gradle
+apply plugin: 'eclipse'
+apply plugin: 'idea'
+
+allprojects {
+    repositories {
+        mavenCentral()
+        mavenRepo name: 'jboss-nexus', urls: "https://repository.jboss.org/nexus/content/groups/public/"
+        mavenRepo name: "jboss-snapshots", urls: "http://snapshots.jboss.org/maven2/"
+        mavenRepo urls: "file://" + System.getProperty('user.home') + "/.m2/repository/"
+    }
+}
+
+ideaProject {
+    javaVersion = "1.6"
+}
+
+
+// build a map of the dependency artifacts to use.  Allows centralized definition of the version of artifacts to
+// use.  In that respect it serves a role similar to <dependencyManagement> in Maven
+slf4jVersion = '1.5.8'
+libraries = [
+        // Ant
+        ant:            'ant:ant:1.6.5',
+
+        // Antlr
+        antlr:          'antlr:antlr:2.7.7',
+
+        // Annotations
+        commons_annotations:
+                        'org.hibernate:hibernate-commons-annotations:3.2.0.Final',
+
+        // CGLIB
+        cglib:          'cglib:cglib:2.2',
+
+        // Jakarta commons-collections  todo : get rid of commons-collections dependency
+        commons_collections:
+                        'commons-collections:commons-collections:3.1',
+
+        // Dom4J
+        dom4j:          'dom4j:dom4j:1.6.1@jar',
+
+        // h2
+        h2:             'com.h2database:h2:1.2.134',
+
+        // Javassist
+        javassist:      'javassist:javassist:3.12.0.GA',
+
+        // javax
+        jpa:            'org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.0.Final',
+        jta:            'javax.transaction:jta:1.1',
+        validation:     'javax.validation:validation-api:1.0.0.GA',
+        validator:      'org.hibernate:hibernate-validator:4.0.2.GA',
+        jacc:           'org.jboss.javaee:jboss-jacc-api:1.1.0.GA',
+
+        // logging
+        slf4j_api:      'org.slf4j:slf4j-api:' + slf4jVersion,
+        slf4j_simple:   'org.slf4j:slf4j-simple:' + slf4jVersion,
+        jcl_slf4j:      'org.slf4j:jcl-over-slf4j:' + slf4jVersion,
+        jcl_api:        'commons-logging:commons-logging-api:99.0-does-not-exist',
+        jcl:            'commons-logging:commons-logging:99.0-does-not-exist',
+
+        // testing
+        junit:          'junit:junit:3.8.2',
+        testng:         'org.testng:testng:5.8:jdk15',
+        jpa_modelgen:   'org.hibernate:hibernate-jpamodelgen:1.0.0.Final',
+        shrinkwrap_api: 'org.jboss.shrinkwrap:shrinkwrap-api:1.0.0-alpha-6',
+        shrinkwrap:     'org.jboss.shrinkwrap:shrinkwrap-impl-base:1.0.0-alpha-6'
+]
+
+
+subprojects { subProject ->
+    group = 'org.hibernate'
+    version = '4.0.0-SNAPSHOT'
+
+    // minimize changes, at least for now (gradle uses 'build' by default)..
+    buildDirName = "target"
+
+    if ( 'hibernate-release' == subProject.name ) {
+        apply plugin : 'base'
+    }
+    else {
+        apply plugin: 'java'
+        apply plugin: 'maven' // for install task as well as deploy dependencies
+
+        defaultTasks 'build'
+
+        configurations {
+            provided {
+                // todo : need to make sure these are non-exported
+                description = 'Non-exported compile-time dependencies.'
+            }
+        }
+
+        // appropriately inject the common dependencies into each sub-project
+        dependencies {
+            compile( libraries.slf4j_api )
+            testCompile( libraries.junit )
+            testRuntime( libraries.slf4j_simple )
+            testRuntime( libraries.jcl_slf4j )
+            testRuntime( libraries.jcl_api )
+            testRuntime( libraries.jcl )
+            testRuntime( libraries.h2 )
+        }
+
+        sourceSets {
+            main {
+                compileClasspath += configurations.provided
+            }
+        }
+
+        manifest.mainAttributes(
+                provider: 'gradle',
+                'Implementation-Url': 'http://hibernate.org',
+                'Implementation-Version': version,
+                'Implementation-Vendor': 'Hibernate.org',
+                'Implementation-Vendor-Id': 'org.hibernate'
+        )
+
+        test {
+//            ignoreFailures = true
+            environment['hibernate.test.validatefailureexpected'] = true
+            maxHeapSize = "1024m"
+        }
+
+        processTestResources.doLast(
+                {
+                    copy {
+                        from( sourceSets.test.java.srcDirs ) {
+                            include '**/*.properties'
+                            include '**/*.xml'
+                        }
+                        into sourceSets.test.classesDir
+                    }
+                }
+        )
+
+        assemble.doLast( { install } )
+        uploadArchives.dependsOn install
+
+        targetCompatibility = "1.6"
+        sourceCompatibility = "1.6"
+
+        ideaModule {
+            downloadJavadoc = false
+            scopes.COMPILE.plus.add( configurations.provided )
+            outputDir = subProject.sourceSets.main.classesDir
+            testOutputDir = subProject.sourceSets.test.classesDir
+            whenConfigured { module ->
+                module.dependencies*.exported = true
+            }
+        }
+    }
+}
+
+dependsOnChildren()
--- a/core/pom.xml
+++ /dev/null
-<?xml version="1.0"?>
-<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
-
-    <modelVersion>4.0.0</modelVersion>
-
-    <parent>
-        <groupId>org.hibernate</groupId>
-        <artifactId>hibernate-parent</artifactId>
-        <version>3.6.0-SNAPSHOT</version>
-        <relativePath>../parent/pom.xml</relativePath>
-    </parent>
-
-    <groupId>org.hibernate</groupId>
-    <artifactId>hibernate-core</artifactId>
-    <packaging>jar</packaging>
-
-    <name>Hibernate Core</name>
-    <description>The core functionality of Hibernate</description>
-
-    <dependencies>
-        <dependency>
-            <groupId>antlr</groupId>
-            <artifactId>antlr</artifactId>
-        </dependency>
-        <dependency>
-            <groupId>commons-collections</groupId>
-            <artifactId>commons-collections</artifactId>
-        </dependency>
-        <dependency>
-            <groupId>dom4j</groupId>
-            <artifactId>dom4j</artifactId>
-            <exclusions>
-                <exclusion>
-                    <groupId>xml-apis</groupId>
-                    <artifactId>xml-apis</artifactId>
-                </exclusion>
-            </exclusions>
-        </dependency>
-        <dependency>
-            <groupId>org.hibernate</groupId>
-            <artifactId>hibernate-commons-annotations</artifactId>
-        </dependency>
-        <dependency>
-            <groupId>org.hibernate.javax.persistence</groupId>
-            <artifactId>hibernate-jpa-2.0-api</artifactId>
-        </dependency>
-        <dependency>
-            <groupId>javax.validation</groupId>
-            <artifactId>validation-api</artifactId>
-            <scope>provided</scope>
-        </dependency>
-        <dependency>
-            <groupId>org.hibernate</groupId>
-            <artifactId>hibernate-validator</artifactId>
-            <scope>test</scope>
-        </dependency>
-        <dependency>
-            <groupId>javax.xml.bind</groupId>
-            <artifactId>jaxb-api</artifactId>
-            <version>2.2</version>
-            <scope>test</scope>
-            <exclusions>
-                <exclusion>
-                    <artifactId>stax-api</artifactId>
-                    <groupId>javax.xml.stream</groupId>
-                </exclusion>
-                <exclusion>
-                    <artifactId>activation</artifactId>
-                    <groupId>javax.activation</groupId>
-                </exclusion>
-            </exclusions>
-        </dependency>
-        <dependency>
-            <groupId>com.sun.xml.bind</groupId>
-            <artifactId>jaxb-impl</artifactId>
-            <version>2.1.12</version>
-            <scope>test</scope>
-        </dependency>
-
-        <!-- optional deps for bytecode providers until those are finally properly scoped -->
-        <dependency>
-            <groupId>javassist</groupId>
-            <artifactId>javassist</artifactId>
-            <optional>true</optional>
-        </dependency>
-        <dependency>
-            <groupId>cglib</groupId>
-            <artifactId>cglib</artifactId>
-            <optional>true</optional>
-        </dependency>
-
-        <dependency>
-            <!-- YUCK, YUCK, YUCK!!!! -->
-            <groupId>javax.transaction</groupId>
-            <artifactId>jta</artifactId>
-            <version>1.1</version>
-        </dependency>
-        <dependency>
-            <groupId>org.jboss.javaee</groupId>
-            <artifactId>jboss-jacc-api_JDK4</artifactId>
-            <version>1.1.0</version>
-            <scope>provided</scope>
-            <exclusions>
-                <exclusion>
-                    <groupId>org.jboss.javaee</groupId>
-                    <artifactId>jboss-servlet-api_3.0</artifactId>
-                </exclusion>
-                <exclusion>
-                    <groupId>org.jboss.logging</groupId>
-                    <artifactId>jboss-logging-spi</artifactId>
-                </exclusion>
-                <exclusion>
-                    <groupId>org.jboss</groupId>
-                    <artifactId>jboss-common-core</artifactId>
-                </exclusion>
-            </exclusions>
-        </dependency>
-        <dependency>
-            <groupId>ant</groupId>
-            <artifactId>ant</artifactId>
-            <version>1.6.5</version>
-            <scope>provided</scope>
-        </dependency>
-        <dependency>
-            <groupId>com.h2database</groupId>
-            <artifactId>h2</artifactId>
-            <scope>test</scope>
-        </dependency>
-    </dependencies>
-
-    <build>
-        <testResources>
-            <testResource>
-                <filtering>false</filtering>
-                <directory>src/test/java</directory>
-                <includes>
-                    <include>**/*.xml</include>
-                </includes>
-            </testResource>
-            <testResource>
-                <filtering>true</filtering>
-                <directory>src/test/resources</directory>
-            </testResource>
-        </testResources>
-        <plugins>
-            <plugin>
-                <groupId>org.codehaus.mojo</groupId>
-                <artifactId>antlr-maven-plugin</artifactId>
-                <version>${antlrPluginVersion}</version>
-                <configuration>
-                    <grammars>hql.g,hql-sql.g,sql-gen.g,order-by.g,order-by-render.g</grammars>
-                    <traceParser>true</traceParser>
-                    <traceTreeParser>true</traceTreeParser>
-                </configuration>
-                <executions>
-                    <execution>
-                        <goals>
-                            <goal>generate</goal>
-                        </goals>
-                    </execution>
-                </executions>
-            </plugin>
-            <plugin>
-                <groupId>org.jboss.maven.plugins</groupId>
-                <artifactId>maven-injection-plugin</artifactId>
-                <configuration>
-                    <bytecodeInjections>
-                        <bytecodeInjection>
-                            <expression>${pom.version}</expression>
-                            <targetMembers>
-                            	<methodBodyReturn>
-                            	    <className>org.hibernate.Version</className>
-                            	    <methodName>getVersionString</methodName>
-                            	</methodBodyReturn>
-                            </targetMembers>
-                        </bytecodeInjection>
-                    </bytecodeInjections>
-                </configuration>
-            </plugin>
-            <plugin>
-                <artifactId>maven-jar-plugin</artifactId>
-                <configuration>
-                    <archive>
-                        <manifest>
-                            <mainClass>org.hibernate.Version</mainClass>
-                        </manifest>
-                    </archive>
-                </configuration>
-            </plugin>
-        </plugins>
-    </build>
-
-    <reporting>
-        <plugins>
-            <plugin>
-                <groupId>org.codehaus.mojo</groupId>
-                <artifactId>antlr-maven-plugin</artifactId>
-                <version>${antlrPluginVersion}</version>
-                <configuration>
-                    <!-- eventually should be based on the second phase grammar -->
-                    <grammars>hql.g</grammars>
-                </configuration>
-            </plugin>
-            <plugin>
-                <groupId>org.apache.maven.plugins</groupId>
-                <artifactId>maven-javadoc-plugin</artifactId>
-                <configuration>
-                    <!-- 
-                    for the time being, gonna ignore the custom stylesheet (what did it do anyway???) 
-                    <stylesheetfile>xyz</stylesheetfile>
-                    -->
-                    <groups>
-                        <group>
-                            <title>Core API</title>
-                            <packages>org.hibernate:org.hibernate.classic:org.hibernate.criterion:org.hibernate.metadata:org.hibernate.cfg:org.hibernate.usertype</packages>
-                        </group>
-                        <group>
-                            <title>Extension API</title>
-                            <packages>org.hibernate.id:org.hibernate.connection:org.hibernate.transaction:org.hibernate.type:org.hibernate.dialect*:org.hibernate.cache*:org.hibernate.event*:org.hibernate.action:org.hibernate.property:org.hibernate.loader*:org.hibernate.persister*:org.hibernate.proxy:org.hibernate.tuple:org.hibernate.transform:org.hibernate.collection:org.hibernate.jdbc</packages>
-                        </group>
-                        <group>
-                            <title>Miscellaneous API</title>
-                            <packages>org.hibernate.stat:org.hibernate.tool.hbm2ddl:org.hibernate.jmx:org.hibernate.mapping:org.hibernate.tool.instrument</packages>
-                        </group>
-                        <group>
-                            <title>Internal Implementation</title>
-			    <packages>org.hibernate.engine:org.hibernate.impl:org.hibernate.sql:org.hibernate.lob:org.hibernate.util:org.hibernate.exception:org.hibernate.hql:org.hibernate.hql.ast:org.hibernate.hql.antlr:org.hibernate.hql.classic:org.hibernate.intercept:org.hibernate.secure:org.hibernate.pretty</packages>
-                        </group>
-                    </groups>
-                </configuration>
-            </plugin>
-        </plugins>
-    </reporting>
-
-    <properties>
-        <antlrPluginVersion>2.1</antlrPluginVersion>
-    </properties>
-</project>
--- a/core/src/test/resources/log4j.properties
+++ /dev/null
-#
-# Hibernate, Relational Persistence for Idiomatic Java
-#
-# Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
-# indicated by the @author tags or express copyright attribution
-# statements applied by the authors.  All third-party contributions are
-# distributed under license by Red Hat Middleware LLC.
-#
-# This copyrighted material is made available to anyone wishing to use, modify,
-# copy, or redistribute it subject to the terms and conditions of the GNU
-# Lesser General Public License, as published by the Free Software Foundation.
-#
-# This program is distributed in the hope that it will be useful,
-# but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
-# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
-# for more details.
-#
-# You should have received a copy of the GNU Lesser General Public License
-# along with this distribution; if not, write to:
-# Free Software Foundation, Inc.
-# 51 Franklin Street, Fifth Floor
-# Boston, MA  02110-1301  USA
-#
-log4j.appender.stdout=org.apache.log4j.ConsoleAppender
-log4j.appender.stdout.Target=System.out
-log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
-log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
-
-log4j.rootLogger=info, stdout
-
-log4j.logger.org.hibernate.test=info
-log4j.logger.org.hibernate.tool.hbm2ddl=debug
-log4j.logger.org.hibernate.sql.ordering.antlr.OrderByFragmentTranslator=trace
--- /dev/null
+++ b/settings.gradle
+include 'hibernate-core'
+
+
+rootProject.children.each { project ->
+    project.buildFileName = "${project.name}.gradle"
+    assert project.projectDir.isDirectory()
+    assert project.buildFile.isFile()
+}

Lines added containing method: 167. Lines removed containing method: 275. Tot = 442
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
74/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-core/src/main/java/org/hibernate/Criteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/Criteria.java
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 * @see org.hibernate.engine.PersistenceContext#setDefaultReadOnly(boolean)
+	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
--- a/hibernate-core/src/main/java/org/hibernate/Filter.java
+++ b/hibernate-core/src/main/java/org/hibernate/Filter.java
-import org.hibernate.engine.FilterDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-		return getLobCreator( ( SessionImplementor ) session );
+		return getLobCreator( (SessionImplementor) session );
--- a/hibernate-core/src/main/java/org/hibernate/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/Query.java
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 * @see org.hibernate.engine.PersistenceContext#setDefaultReadOnly(boolean)
+	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
--- a/hibernate-core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
--- a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
-import org.hibernate.engine.FilterDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
- * An {@link org.hibernate.engine.ActionQueue} {@link org.hibernate.action.spi.Executable} for ensuring
+ * An {@link org.hibernate.engine.spi.ActionQueue} {@link org.hibernate.action.spi.Executable} for ensuring
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/DelayedPostInsertIdentifier.java
- * The stand-in is only used within the {@link org.hibernate.engine.PersistenceContext}
+ * The stand-in is only used within the {@link org.hibernate.engine.spi.PersistenceContext}
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIncrementVersionProcess.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-					version = Versioning.getVersion(state, persister);
+					version = Versioning.getVersion( state, persister );
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!=Status.MANAGED ) {
+			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!= Status.MANAGED ) {
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityVerifyVersionProcess.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/spi/AfterTransactionCompletionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/spi/AfterTransactionCompletionProcess.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/action/spi/BeforeTransactionCompletionProcess.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/spi/BeforeTransactionCompletionProcess.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryCache.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntryStructure.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCollectionCacheEntry.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredMapCacheEntry.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/UnstructuredCacheEntry.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+import org.hibernate.engine.internal.Versioning;
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.NamedQueryDefinition;
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinition;
-		return ExecuteUpdateResultCheckStyle.parse( attr.getValue() );
+		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
-import org.hibernate.engine.query.sql.NativeSQLQueryCollectionReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
-					ExecuteUpdateResultCheckStyle.parse( sqlInsert.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlUpdate.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlDelete.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlDeleteAll.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+import org.hibernate.engine.internal.Versioning;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.FilterDefinition;
-					ExecuteUpdateResultCheckStyle.parse( sqlInsert.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlUpdate.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlDelete.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
-					ExecuteUpdateResultCheckStyle.parse( sqlDeleteAll.check().toString().toLowerCase() )
+					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
-						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlInsert().check().toString().toLowerCase() )
+						ExecuteUpdateResultCheckStyle.fromExternalName(
+								matchingTable.sqlInsert().check().toString().toLowerCase()
+						)
-						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlUpdate().check().toString().toLowerCase() )
+						ExecuteUpdateResultCheckStyle.fromExternalName(
+								matchingTable.sqlUpdate().check().toString().toLowerCase()
+						)
-						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlDelete().check().toString().toLowerCase() )
+						ExecuteUpdateResultCheckStyle.fromExternalName(
+								matchingTable.sqlDelete().check().toString().toLowerCase()
+						)
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.engine.spi.TypedValue;
-				Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, old, session);
+				Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, old, session );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentListElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentListElementHolder.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/context/spi/CurrentSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/spi/CurrentSessionContext.java
- * {@link org.hibernate.engine.SessionFactoryImplementor}
+ * {@link org.hibernate.engine.spi.SessionFactoryImplementor}
--- a/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.TypedValue;
-	public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value) 
+	public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value)
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Criterion.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Criterion.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import org.hibernate.Criteria;
-import org.hibernate.HibernateException;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.TypedValue;
-
-/**
- * A criterion representing a "like" expression
- *
- * @author Scott Marlow
- * @author Steve Ebersole
- */
-public class LikeExpression implements Criterion {
-	private final String propertyName;
-	private final Object value;
-	private final Character escapeChar;
-	private final boolean ignoreCase;
-
-	protected LikeExpression(
-			String propertyName,
-			String value,
-			Character escapeChar,
-			boolean ignoreCase) {
-		this.propertyName = propertyName;
-		this.value = value;
-		this.escapeChar = escapeChar;
-		this.ignoreCase = ignoreCase;
-	}
-
-	protected LikeExpression(
-			String propertyName,
-			String value) {
-		this( propertyName, value, null, false );
-	}
-
-	protected LikeExpression(
-			String propertyName,
-			String value,
-			MatchMode matchMode) {
-		this( propertyName, matchMode.toMatchString( value ) );
-	}
-
-	protected LikeExpression(
-			String propertyName,
-			String value,
-			MatchMode matchMode,
-			Character escapeChar,
-			boolean ignoreCase) {
-		this( propertyName, matchMode.toMatchString( value ), escapeChar, ignoreCase );
-	}
-
-	public String toSqlString(
-			Criteria criteria,
-			CriteriaQuery criteriaQuery) throws HibernateException {
-		Dialect dialect = criteriaQuery.getFactory().getDialect();
-		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
-		if ( columns.length != 1 ) {
-			throw new HibernateException( "Like may only be used with single-column properties" );
-		}
-		String lhs = ignoreCase
-				? dialect.getLowercaseFunction() + '(' + columns[0] + ')'
-	            : columns[0];
-		return lhs + " like ?" + ( escapeChar == null ? "" : " escape \'" + escapeChar + "\'" );
-
-	}
-
-	public TypedValue[] getTypedValues(
-			Criteria criteria,
-			CriteriaQuery criteriaQuery) throws HibernateException {
-		return new TypedValue[] {
-				criteriaQuery.getTypedValue( criteria, propertyName, ignoreCase ? value.toString().toLowerCase() : value.toString() )
-		};
-	}
-}
+import org.hibernate.Criteria;
+import org.hibernate.HibernateException;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.spi.TypedValue;
+
+/**
+ * A criterion representing a "like" expression
+ *
+ * @author Scott Marlow
+ * @author Steve Ebersole
+ */
+public class LikeExpression implements Criterion {
+	private final String propertyName;
+	private final Object value;
+	private final Character escapeChar;
+	private final boolean ignoreCase;
+
+	protected LikeExpression(
+			String propertyName,
+			String value,
+			Character escapeChar,
+			boolean ignoreCase) {
+		this.propertyName = propertyName;
+		this.value = value;
+		this.escapeChar = escapeChar;
+		this.ignoreCase = ignoreCase;
+	}
+
+	protected LikeExpression(
+			String propertyName,
+			String value) {
+		this( propertyName, value, null, false );
+	}
+
+	protected LikeExpression(
+			String propertyName,
+			String value,
+			MatchMode matchMode) {
+		this( propertyName, matchMode.toMatchString( value ) );
+	}
+
+	protected LikeExpression(
+			String propertyName,
+			String value,
+			MatchMode matchMode,
+			Character escapeChar,
+			boolean ignoreCase) {
+		this( propertyName, matchMode.toMatchString( value ), escapeChar, ignoreCase );
+	}
+
+	public String toSqlString(
+			Criteria criteria,
+			CriteriaQuery criteriaQuery) throws HibernateException {
+		Dialect dialect = criteriaQuery.getFactory().getDialect();
+		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
+		if ( columns.length != 1 ) {
+			throw new HibernateException( "Like may only be used with single-column properties" );
+		}
+		String lhs = ignoreCase
+				? dialect.getLowercaseFunction() + '(' + columns[0] + ')'
+	            : columns[0];
+		return lhs + " like ?" + ( escapeChar == null ? "" : " escape \'" + escapeChar + "\'" );
+
+	}
+
+	public TypedValue[] getTypedValues(
+			Criteria criteria,
+			CriteriaQuery criteriaQuery) throws HibernateException {
+		return new TypedValue[] {
+				criteriaQuery.getTypedValue( criteria, propertyName, ignoreCase ? value.toString().toLowerCase() : value.toString() )
+		};
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.TypedValue;
-		return new TypedValue[] { 
+		return new TypedValue[] {
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/LobMergeStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/LobMergeStrategy.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.sql.Types;
-import org.hibernate.MappingException;
-import org.hibernate.QueryException;
-import org.hibernate.engine.Mapping;
-import org.hibernate.type.StandardBasicTypes;
-import org.hibernate.type.Type;
-
-/**
- * Classic AVG sqlfunction that return types as it was done in Hibernate 3.1 
- * 
- * @author Max Rydahl Andersen
- *
- */
-public class ClassicAvgFunction extends StandardSQLFunction {
-	public ClassicAvgFunction() {
-		super( "avg" );
-	}
-
-	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
-		int[] sqlTypes;
-		try {
-			sqlTypes = columnType.sqlTypes( mapping );
-		}
-		catch ( MappingException me ) {
-			throw new QueryException( me );
-		}
-		if ( sqlTypes.length != 1 ) throw new QueryException( "multi-column type in avg()" );
-		int sqlType = sqlTypes[0];
-		if ( sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.TINYINT ) {
-			return StandardBasicTypes.FLOAT;
-		}
-		else {
-			return columnType;
-		}
-	}
+import java.sql.Types;
+import org.hibernate.MappingException;
+import org.hibernate.QueryException;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * Classic AVG sqlfunction that return types as it was done in Hibernate 3.1 
+ * 
+ * @author Max Rydahl Andersen
+ *
+ */
+public class ClassicAvgFunction extends StandardSQLFunction {
+	public ClassicAvgFunction() {
+		super( "avg" );
+	}
+
+	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
+		int[] sqlTypes;
+		try {
+			sqlTypes = columnType.sqlTypes( mapping );
+		}
+		catch ( MappingException me ) {
+			throw new QueryException( me );
+		}
+		if ( sqlTypes.length != 1 ) throw new QueryException( "multi-column type in avg()" );
+		int sqlType = sqlTypes[0];
+		if ( sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.TINYINT ) {
+			return StandardBasicTypes.FLOAT;
+		}
+		else {
+			return columnType;
+		}
+	}
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.engine.Mapping;
-import org.hibernate.type.StandardBasicTypes;
-import org.hibernate.type.Type;
-
-/**
- * Classic COUNT sqlfunction that return types as it was done in Hibernate 3.1 
- * 
- * @author Max Rydahl Andersen
- *
- */
-public class ClassicCountFunction extends StandardSQLFunction {
-	public ClassicCountFunction() {
-		super( "count" );
-	}
-
-	public Type getReturnType(Type columnType, Mapping mapping) {
-		return StandardBasicTypes.INTEGER;
-	}
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * Classic COUNT sqlfunction that return types as it was done in Hibernate 3.1 
+ * 
+ * @author Max Rydahl Andersen
+ *
+ */
+public class ClassicCountFunction extends StandardSQLFunction {
+	public ClassicCountFunction() {
+		super( "count" );
+	}
+
+	public Type getReturnType(Type columnType, Mapping mapping) {
+		return StandardBasicTypes.INTEGER;
+	}
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.util.List;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.Type;
-
-/**
- * Essentially the same as {@link org.hibernate.dialect.function.StandardSQLFunction},
- * except that here the parentheses are not included when no arguments are given.
- *
- * @author Jonathan Levinson
- */
-public class ConditionalParenthesisFunction extends StandardSQLFunction {
-
-	public ConditionalParenthesisFunction(String name) {
-		super( name );
-	}
-
-	public ConditionalParenthesisFunction(String name, Type type) {
-		super( name, type );
-	}
-
-	public boolean hasParenthesesIfNoArguments() {
-		return false;
-	}
-
-	public String render(List args, SessionFactoryImplementor factory) {
-		final boolean hasArgs = !args.isEmpty();
-		StringBuffer buf = new StringBuffer();
-		buf.append( getName() );
-		if ( hasArgs ) {
-			buf.append( "(" );
-			for ( int i = 0; i < args.size(); i++ ) {
-				buf.append( args.get( i ) );
-				if ( i < args.size() - 1 ) {
-					buf.append( ", " );
-				}
-			}
-			buf.append( ")" );
-		}
-		return buf.toString();
-	}
-}
+import java.util.List;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.type.Type;
+
+/**
+ * Essentially the same as {@link org.hibernate.dialect.function.StandardSQLFunction},
+ * except that here the parentheses are not included when no arguments are given.
+ *
+ * @author Jonathan Levinson
+ */
+public class ConditionalParenthesisFunction extends StandardSQLFunction {
+
+	public ConditionalParenthesisFunction(String name) {
+		super( name );
+	}
+
+	public ConditionalParenthesisFunction(String name, Type type) {
+		super( name, type );
+	}
+
+	public boolean hasParenthesesIfNoArguments() {
+		return false;
+	}
+
+	public String render(List args, SessionFactoryImplementor factory) {
+		final boolean hasArgs = !args.isEmpty();
+		StringBuffer buf = new StringBuffer();
+		buf.append( getName() );
+		if ( hasArgs ) {
+			buf.append( "(" );
+			for ( int i = 0; i < args.size(); i++ ) {
+				buf.append( args.get( i ) );
+				if ( i < args.size() - 1 ) {
+					buf.append( ", " );
+				}
+			}
+			buf.append( ")" );
+		}
+		return buf.toString();
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.util.List;
-import org.hibernate.QueryException;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.StandardBasicTypes;
-import org.hibernate.type.Type;
-
-/**
- * A Cach&eacute; defintion of a convert function.
- *
- * @author Jonathan Levinson
- */
-public class ConvertFunction implements SQLFunction {
-
-	public boolean hasArguments() {
-		return true;
-	}
-
-	public boolean hasParenthesesIfNoArguments() {
-		return true;
-	}
-
-	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
-		return StandardBasicTypes.STRING;
-	}
-
-	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
-		if ( args.size() != 2 && args.size() != 3 ) {
-			throw new QueryException( "convert() requires two or three arguments" );
-		}
-		String type = ( String ) args.get( 1 );
-
-		if ( args.size() == 2 ) {
-			return "{fn convert(" + args.get( 0 ) + " , " + type + ")}";
-		}
-		else {
-			return "convert(" + args.get( 0 ) + " , " + type + "," + args.get( 2 ) + ")";
-		}
-	}
-
-}
+import java.util.List;
+import org.hibernate.QueryException;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * A Cach&eacute; defintion of a convert function.
+ *
+ * @author Jonathan Levinson
+ */
+public class ConvertFunction implements SQLFunction {
+
+	public boolean hasArguments() {
+		return true;
+	}
+
+	public boolean hasParenthesesIfNoArguments() {
+		return true;
+	}
+
+	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
+		return StandardBasicTypes.STRING;
+	}
+
+	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
+		if ( args.size() != 2 && args.size() != 3 ) {
+			throw new QueryException( "convert() requires two or three arguments" );
+		}
+		String type = ( String ) args.get( 1 );
+
+		if ( args.size() == 2 ) {
+			return "{fn convert(" + args.get( 0 ) + " , " + type + ")}";
+		}
+		else {
+			return "convert(" + args.get( 0 ) + " , " + type + "," + args.get( 2 ) + ")";
+		}
+	}
+
+}
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
-import org.hibernate.Hibernate;
+
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.util.List;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.Type;
-
-/**
- * Analogous to {@link org.hibernate.dialect.function.StandardSQLFunction}
- * except that standard JDBC escape sequences (i.e. {fn blah}) are used when
- * rendering the SQL.
- *
- * @author Steve Ebersole
- */
-public class StandardJDBCEscapeFunction extends StandardSQLFunction {
-	public StandardJDBCEscapeFunction(String name) {
-		super( name );
-	}
-
-	public StandardJDBCEscapeFunction(String name, Type typeValue) {
-		super( name, typeValue );
-	}
-
-	public String render(Type argumentType, List args, SessionFactoryImplementor factory) {
-		return "{fn " + super.render( argumentType, args, factory ) + "}";
-	}
-
-	public String toString() {
-		return "{fn " + getName() + "...}";
-	}
-}
+import java.util.List;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.type.Type;
+
+/**
+ * Analogous to {@link org.hibernate.dialect.function.StandardSQLFunction}
+ * except that standard JDBC escape sequences (i.e. {fn blah}) are used when
+ * rendering the SQL.
+ *
+ * @author Steve Ebersole
+ */
+public class StandardJDBCEscapeFunction extends StandardSQLFunction {
+	public StandardJDBCEscapeFunction(String name) {
+		super( name );
+	}
+
+	public StandardJDBCEscapeFunction(String name, Type typeValue) {
+		super( name, typeValue );
+	}
+
+	public String render(Type argumentType, List args, SessionFactoryImplementor factory) {
+		return "{fn " + super.render( argumentType, args, factory ) + "}";
+	}
+
+	public String toString() {
+		return "{fn " + getName() + "...}";
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
+
- * An iterator that may be "closed"
+ * Hibernate-specific iterator that may be closed
+ *
+ * @see org.hibernate.Query#iterate()
+ *
+	/**
+	 * Close the Hibernate query result iterator
+	 *
+	 * @throws JDBCException Indicates a problem releasing the underlying JDBC resources.
+	 */
--- a/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
+
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
-	private final List /*NativeSQLQueryReturn*/ queryReturns = new ArrayList();
+	private final List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>();
-		return ( NativeSQLQueryReturn[] ) queryReturns.toArray( new NativeSQLQueryReturn[0] );
+		return queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] );
--- a/hibernate-core/src/main/java/org/hibernate/engine/Status.java
+++ /dev/null
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
-package org.hibernate.engine;
-import java.io.InvalidObjectException;
-import java.io.ObjectStreamException;
-import java.io.Serializable;
-
-/**
- * Represents the status of an entity with respect to
- * this session. These statuses are for internal
- * book-keeping only and are not intended to represent
- * any notion that is visible to the _application_.
- */
-public final class Status implements Serializable {
-
-	public static final Status MANAGED = new Status( "MANAGED" );
-	public static final Status READ_ONLY = new Status( "READ_ONLY" );
-	public static final Status DELETED = new Status( "DELETED" );
-	public static final Status GONE = new Status( "GONE" );
-	public static final Status LOADING = new Status( "LOADING" );
-	public static final Status SAVING = new Status( "SAVING" );
-
-	private String name;
-
-	private Status(String name) {
-		this.name = name;
-	}
-
-	public String toString() {
-		return name;
-	}
-
-	private Object readResolve() throws ObjectStreamException {
-		return parse( name );
-	}
-
-	public static Status parse(String name) throws InvalidObjectException {
-		if ( name.equals(MANAGED.name) ) return MANAGED;
-		if ( name.equals(READ_ONLY.name) ) return READ_ONLY;
-		if ( name.equals(DELETED.name) ) return DELETED;
-		if ( name.equals(GONE.name) ) return GONE;
-		if ( name.equals(LOADING.name) ) return LOADING;
-		if ( name.equals(SAVING.name) ) return SAVING;
-		throw new InvalidObjectException( "invalid Status" );
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/engine/ValueInclusion.java
+++ /dev/null
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
-package org.hibernate.engine;
-import java.io.ObjectStreamException;
-import java.io.Serializable;
-import java.io.StreamCorruptedException;
-
-/**
- * An enum of the different ways a value might be "included".
- * <p/>
- * This is really an expanded true/false notion with "PARTIAL" being the
- * expansion.  PARTIAL deals with components in the cases where
- * parts of the referenced component might define inclusion, but the
- * component overall does not.
- *
- * @author Steve Ebersole
- */
-public class ValueInclusion implements Serializable {
-
-	public static final ValueInclusion NONE = new ValueInclusion( "none" );
-	public static final ValueInclusion FULL = new ValueInclusion( "full" );
-	public static final ValueInclusion PARTIAL = new ValueInclusion( "partial" );
-
-	private final String name;
-
-	public ValueInclusion(String name) {
-		this.name = name;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public String toString() {
-		return "ValueInclusion[" + name + "]";
-	}
-
-	private Object readResolve() throws ObjectStreamException {
-		if ( name.equals( NONE.name ) ) {
-			return NONE;
-		}
-		else if ( name.equals( FULL.name ) ) {
-			return FULL;
-		}
-		else if ( name.equals( PARTIAL.name ) ) {
-			return PARTIAL;
-		}
-		else {
-			throw new StreamCorruptedException( "unrecognized value inclusion [" + name + "]" );
-		}
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
- * {@link CascadingAction actions}, implementing cascade processing.
+ * {@link org.hibernate.engine.spi.CascadingAction actions}, implementing cascade processing.
- * @see CascadingAction
+ * @see org.hibernate.engine.spi.CascadingAction
--- a/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
-			final SessionImplementor session) 
+			final SessionImplementor session)
--- a/hibernate-core/src/main/java/org/hibernate/engine/JoinHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinHelper.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/JoinSequence.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.SessionImplementor;
- * Implements the algorithm for validating property values
- * for illegal null values
+ * Implements the algorithm for validating property values for illegal null values
+ * 
-				Iterator iter = CascadingAction.getLoadedElementsIterator(session, collectionType, value);
+				Iterator iter = CascadingAction.getLoadedElementsIterator( session, collectionType, value );
--- a/hibernate-core/src/main/java/org/hibernate/engine/ParameterBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ParameterBinder.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.TypedValue;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.jboss.logging.Logger;
- * Centralizes the commonality regarding binding of parameter values into
- * PreparedStatements as this logic is used in many places.
+ * Centralizes the commonality regarding binding of parameter values into PreparedStatements as this logic is
+ * used in many places.
- * Ideally would like to move to the parameter handling as it is done in
- * the hql.ast package.
+ * Ideally would like to move to the parameter handling as it is done in the hql.ast package.
-				TypedValue typedval = ( TypedValue ) e.getValue();
+				TypedValue typedval = (TypedValue) e.getValue();
--- a/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+import org.hibernate.engine.loading.internal.LoadContexts;
+import org.hibernate.engine.spi.AssociationKey;
+import org.hibernate.engine.spi.BatchFetchQueue;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.loading.LoadContexts;
-			( ( EntityKey ) entry.getKey() ).serialize( oos );
+			( (EntityKey) entry.getKey() ).serialize( oos );
-	 * @see org.hibernate.engine.PersistenceContext#addChildParent(java.lang.Object, java.lang.Object)
+	 * @see org.hibernate.engine.spi.PersistenceContext#addChildParent(java.lang.Object, java.lang.Object)
-	 * @see org.hibernate.engine.PersistenceContext#removeChildParent(java.lang.Object)
+	 * @see org.hibernate.engine.spi.PersistenceContext#removeChildParent(java.lang.Object)
--- a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
-import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.cache.spi.CacheKey;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.event.service.spi.EventListenerGroup;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-		Object version = Versioning.getVersion(values, persister);
+		Object version = Versioning.getVersion( values, persister );
--- a/hibernate-core/src/main/java/org/hibernate/engine/UnsavedValueFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/UnsavedValueFactory.java
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+
+import org.hibernate.engine.spi.IdentifierValue;
+import org.hibernate.engine.spi.VersionValue;
--- a/hibernate-core/src/main/java/org/hibernate/engine/Versioning.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.internal;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.SessionImplementor;
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
-package org.hibernate.engine.loading;
+package org.hibernate.engine.loading.internal;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.CollectionKey;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/EntityLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/EntityLoadContext.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine.loading;
+package org.hibernate.engine.loading.internal;
+
-import org.hibernate.internal.CoreMessageLogger;
-
+import org.hibernate.internal.CoreMessageLogger;
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine.loading;
+package org.hibernate.engine.loading.internal;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.engine.CollectionKey;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
-
-			final CollectionKey entryKey = ( CollectionKey ) itr.next();
+			final CollectionKey entryKey = (CollectionKey) itr.next();
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadingCollectionEntry.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine.loading;
+package org.hibernate.engine.loading.internal;
+
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/profile/FetchProfile.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/profile/FetchProfile.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/FilterQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/FilterQueryPlan.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/NamedParameterDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NamedParameterDescriptor.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeSQLQueryPlan.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TypedValue;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/OrdinalParameterDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/OrdinalParameterDescriptor.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParamLocationRecognizer.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterMetadata.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterParser.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryMetadata.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryPlanCache.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ReturnMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ReturnMetadata.java
-package org.hibernate.engine.query;
+package org.hibernate.engine.query.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryCollectionReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryCollectionReturn.java
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
-package org.hibernate.engine.query.sql;
-import java.util.Map;
-import org.hibernate.LockMode;
-
-/**
- * Represents a return defined as part of a native sql query which
- * names a collection role in the form {classname}.{collectionrole}; it
- * is used in defining a custom sql query for loading an entity's
- * collection in non-fetching scenarios (i.e., loading the collection
- * itself as the "root" of the result).
- *
- * @author Steve Ebersole
- */
-public class NativeSQLQueryCollectionReturn extends NativeSQLQueryNonScalarReturn {
-	private final String ownerEntityName;
-	private final String ownerProperty;
-	private final int hashCode;
-
-	/**
-	 * Construct a native-sql return representing a collection initializer
-	 *
-	 * @param alias The result alias
-	 * @param ownerEntityName The entity-name of the entity owning the collection
-	 * to be initialized.
-	 * @param ownerProperty The property name (on the owner) which represents
-	 * the collection to be initialized.
-	 * @param propertyResults Any user-supplied column->property mappings
-	 * @param lockMode The lock mode to apply to the collection.
-	 */
-	public NativeSQLQueryCollectionReturn(
-			String alias,
-			String ownerEntityName,
-			String ownerProperty,
-			Map propertyResults,
-			LockMode lockMode) {
-		super( alias, propertyResults, lockMode );
-		this.ownerEntityName = ownerEntityName;
-		this.ownerProperty = ownerProperty;
-		this.hashCode = determineHashCode();
-	}
-
-	/**
-	 * Returns the class owning the collection.
-	 *
-	 * @return The class owning the collection.
-	 */
-	public String getOwnerEntityName() {
-		return ownerEntityName;
-	}
-
-	/**
-	 * Returns the name of the property representing the collection from the {@link #getOwnerEntityName}.
-	 *
-	 * @return The name of the property representing the collection on the owner class.
-	 */
-	public String getOwnerProperty() {
-		return ownerProperty;
-	}
-
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-		if ( !super.equals( o ) ) {
-			return false;
-		}
-
-		NativeSQLQueryCollectionReturn that = ( NativeSQLQueryCollectionReturn ) o;
-
-		if ( ownerEntityName != null ? !ownerEntityName.equals( that.ownerEntityName ) : that.ownerEntityName != null ) {
-			return false;
-		}
-		if ( ownerProperty != null ? !ownerProperty.equals( that.ownerProperty ) : that.ownerProperty != null ) {
-			return false;
-		}
-
-		return true;
-	}
-
-	public int hashCode() {
-		return hashCode;
-	}
-	
-	private int determineHashCode() {
-		int result = super.hashCode();
-		result = 31 * result + ( ownerEntityName != null ? ownerEntityName.hashCode() : 0 );
-		result = 31 * result + ( ownerProperty != null ? ownerProperty.hashCode() : 0 );
-		return result;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+import java.util.Map;
+import org.hibernate.LockMode;
+
+/**
+ * Represents a return defined as part of a native sql query which
+ * names a collection role in the form {classname}.{collectionrole}; it
+ * is used in defining a custom sql query for loading an entity's
+ * collection in non-fetching scenarios (i.e., loading the collection
+ * itself as the "root" of the result).
+ *
+ * @author Steve Ebersole
+ */
+public class NativeSQLQueryCollectionReturn extends NativeSQLQueryNonScalarReturn {
+	private final String ownerEntityName;
+	private final String ownerProperty;
+	private final int hashCode;
+
+	/**
+	 * Construct a native-sql return representing a collection initializer
+	 *
+	 * @param alias The result alias
+	 * @param ownerEntityName The entity-name of the entity owning the collection
+	 * to be initialized.
+	 * @param ownerProperty The property name (on the owner) which represents
+	 * the collection to be initialized.
+	 * @param propertyResults Any user-supplied column->property mappings
+	 * @param lockMode The lock mode to apply to the collection.
+	 */
+	public NativeSQLQueryCollectionReturn(
+			String alias,
+			String ownerEntityName,
+			String ownerProperty,
+			Map propertyResults,
+			LockMode lockMode) {
+		super( alias, propertyResults, lockMode );
+		this.ownerEntityName = ownerEntityName;
+		this.ownerProperty = ownerProperty;
+		this.hashCode = determineHashCode();
+	}
+
+	/**
+	 * Returns the class owning the collection.
+	 *
+	 * @return The class owning the collection.
+	 */
+	public String getOwnerEntityName() {
+		return ownerEntityName;
+	}
+
+	/**
+	 * Returns the name of the property representing the collection from the {@link #getOwnerEntityName}.
+	 *
+	 * @return The name of the property representing the collection on the owner class.
+	 */
+	public String getOwnerProperty() {
+		return ownerProperty;
+	}
+
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+		if ( !super.equals( o ) ) {
+			return false;
+		}
+
+		NativeSQLQueryCollectionReturn that = ( NativeSQLQueryCollectionReturn ) o;
+
+		if ( ownerEntityName != null ? !ownerEntityName.equals( that.ownerEntityName ) : that.ownerEntityName != null ) {
+			return false;
+		}
+		if ( ownerProperty != null ? !ownerProperty.equals( that.ownerProperty ) : that.ownerProperty != null ) {
+			return false;
+		}
+
+		return true;
+	}
+
+	public int hashCode() {
+		return hashCode;
+	}
+	
+	private int determineHashCode() {
+		int result = super.hashCode();
+		result = 31 * result + ( ownerEntityName != null ? ownerEntityName.hashCode() : 0 );
+		result = 31 * result + ( ownerProperty != null ? ownerProperty.hashCode() : 0 );
+		return result;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryJoinReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryJoinReturn.java
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
-package org.hibernate.engine.query.sql;
-import java.util.Map;
-import org.hibernate.LockMode;
-
-/**
- * Represents a return defined as part of a native sql query which
- * names a fetched role.
- *
- * @author Steve Ebersole
- */
-public class NativeSQLQueryJoinReturn extends NativeSQLQueryNonScalarReturn {
-	private final String ownerAlias;
-	private final String ownerProperty;
-	private final int hashCode;
-	/**
-	 * Construct a return descriptor representing some form of fetch.
-	 *
-	 * @param alias The result alias
-	 * @param ownerAlias The owner's result alias
-	 * @param ownerProperty The owner's property representing the thing to be fetched
-	 * @param propertyResults Any user-supplied column->property mappings
-	 * @param lockMode The lock mode to apply
-	 */
-	public NativeSQLQueryJoinReturn(
-			String alias,
-			String ownerAlias,
-			String ownerProperty,
-			Map propertyResults,
-			LockMode lockMode) {
-		super( alias, propertyResults, lockMode );
-		this.ownerAlias = ownerAlias;
-		this.ownerProperty = ownerProperty;
-		this.hashCode = determineHashCode();
-	}
-
-	/**
-	 * Retrieve the alias of the owner of this fetched association.
-	 *
-	 * @return The owner's alias.
-	 */
-	public String getOwnerAlias() {
-		return ownerAlias;
-	}
-
-	/**
-	 * Retrieve the property name (relative to the owner) which maps to
-	 * the association to be fetched.
-	 *
-	 * @return The property name.
-	 */
-	public String getOwnerProperty() {
-		return ownerProperty;
-	}
-
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-		if ( !super.equals( o ) ) {
-			return false;
-		}
-
-		NativeSQLQueryJoinReturn that = ( NativeSQLQueryJoinReturn ) o;
-
-		if ( ownerAlias != null ? !ownerAlias.equals( that.ownerAlias ) : that.ownerAlias != null ) {
-			return false;
-		}
-		if ( ownerProperty != null ? !ownerProperty.equals( that.ownerProperty ) : that.ownerProperty != null ) {
-			return false;
-		}
-
-		return true;
-	}
-
-	public int hashCode() {
-		return hashCode;
-	}
-
-	private int determineHashCode() {
-		int result = super.hashCode();
-		result = 31 * result + ( ownerAlias != null ? ownerAlias.hashCode() : 0 );
-		result = 31 * result + ( ownerProperty != null ? ownerProperty.hashCode() : 0 );
-		return result;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+import java.util.Map;
+import org.hibernate.LockMode;
+
+/**
+ * Represents a return defined as part of a native sql query which
+ * names a fetched role.
+ *
+ * @author Steve Ebersole
+ */
+public class NativeSQLQueryJoinReturn extends NativeSQLQueryNonScalarReturn {
+	private final String ownerAlias;
+	private final String ownerProperty;
+	private final int hashCode;
+	/**
+	 * Construct a return descriptor representing some form of fetch.
+	 *
+	 * @param alias The result alias
+	 * @param ownerAlias The owner's result alias
+	 * @param ownerProperty The owner's property representing the thing to be fetched
+	 * @param propertyResults Any user-supplied column->property mappings
+	 * @param lockMode The lock mode to apply
+	 */
+	public NativeSQLQueryJoinReturn(
+			String alias,
+			String ownerAlias,
+			String ownerProperty,
+			Map propertyResults,
+			LockMode lockMode) {
+		super( alias, propertyResults, lockMode );
+		this.ownerAlias = ownerAlias;
+		this.ownerProperty = ownerProperty;
+		this.hashCode = determineHashCode();
+	}
+
+	/**
+	 * Retrieve the alias of the owner of this fetched association.
+	 *
+	 * @return The owner's alias.
+	 */
+	public String getOwnerAlias() {
+		return ownerAlias;
+	}
+
+	/**
+	 * Retrieve the property name (relative to the owner) which maps to
+	 * the association to be fetched.
+	 *
+	 * @return The property name.
+	 */
+	public String getOwnerProperty() {
+		return ownerProperty;
+	}
+
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+		if ( !super.equals( o ) ) {
+			return false;
+		}
+
+		NativeSQLQueryJoinReturn that = ( NativeSQLQueryJoinReturn ) o;
+
+		if ( ownerAlias != null ? !ownerAlias.equals( that.ownerAlias ) : that.ownerAlias != null ) {
+			return false;
+		}
+		if ( ownerProperty != null ? !ownerProperty.equals( that.ownerProperty ) : that.ownerProperty != null ) {
+			return false;
+		}
+
+		return true;
+	}
+
+	public int hashCode() {
+		return hashCode;
+	}
+
+	private int determineHashCode() {
+		int result = super.hashCode();
+		result = 31 * result + ( ownerAlias != null ? ownerAlias.hashCode() : 0 );
+		result = 31 * result + ( ownerProperty != null ? ownerProperty.hashCode() : 0 );
+		return result;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryNonScalarReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryNonScalarReturn.java
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
-package org.hibernate.engine.query.sql;
-import java.io.Serializable;
-import java.util.Collections;
-import java.util.HashMap;
-import java.util.Map;
-import org.hibernate.HibernateException;
-import org.hibernate.LockMode;
-
-/**
- * Represents the base information for a non-scalar return defined as part of
- * a native sql query.
- *
- * @author Steve Ebersole
- */
-public abstract class NativeSQLQueryNonScalarReturn implements NativeSQLQueryReturn, Serializable {
-	private final String alias;
-	private final LockMode lockMode;
-	private final Map propertyResults = new HashMap();
-	private final int hashCode;
-
-	/**
-	 * Constructs some form of non-scalar return descriptor
-	 *
-	 * @param alias The result alias
-	 * @param propertyResults Any user-supplied column->property mappings
-	 * @param lockMode The lock mode to apply to the return.
-	 */
-	protected NativeSQLQueryNonScalarReturn(String alias, Map propertyResults, LockMode lockMode) {
-		this.alias = alias;
-		if ( alias == null ) {
-			throw new HibernateException("alias must be specified");
-		}
-		this.lockMode = lockMode;
-		if ( propertyResults != null ) {
-			this.propertyResults.putAll( propertyResults );
-		}
-		this.hashCode = determineHashCode();
-	}
-
-	/**
-	 * Retrieve the defined result alias
-	 *
-	 * @return The result alias.
-	 */
-	public String getAlias() {
-		return alias;
-	}
-
-	/**
-	 * Retrieve the lock-mode to apply to this return
-	 *
-	 * @return The lock mode
-	 */
-	public LockMode getLockMode() {
-		return lockMode;
-	}
-
-	/**
-	 * Retrieve the user-supplied column->property mappings.
-	 *
-	 * @return The property mappings.
-	 */
-	public Map getPropertyResultsMap() {
-		return Collections.unmodifiableMap( propertyResults );
-	}
-
-	public int hashCode() {
-		return hashCode;
-	}
-
-	private int determineHashCode() {
-		int result = alias != null ? alias.hashCode() : 0;
-		result = 31 * result + ( getClass().getName().hashCode() );
-		result = 31 * result + ( lockMode != null ? lockMode.hashCode() : 0 );
-		result = 31 * result + ( propertyResults != null ? propertyResults.hashCode() : 0 );
-		return result;
-	}
-
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-
-		NativeSQLQueryNonScalarReturn that = ( NativeSQLQueryNonScalarReturn ) o;
-
-		if ( alias != null ? !alias.equals( that.alias ) : that.alias != null ) {
-			return false;
-		}
-		if ( lockMode != null ? !lockMode.equals( that.lockMode ) : that.lockMode != null ) {
-			return false;
-		}
-		if ( propertyResults != null ? !propertyResults.equals( that.propertyResults ) : that.propertyResults != null ) {
-			return false;
-		}
-
-		return true;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+import java.io.Serializable;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+
+/**
+ * Represents the base information for a non-scalar return defined as part of
+ * a native sql query.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class NativeSQLQueryNonScalarReturn implements NativeSQLQueryReturn, Serializable {
+	private final String alias;
+	private final LockMode lockMode;
+	private final Map propertyResults = new HashMap();
+	private final int hashCode;
+
+	/**
+	 * Constructs some form of non-scalar return descriptor
+	 *
+	 * @param alias The result alias
+	 * @param propertyResults Any user-supplied column->property mappings
+	 * @param lockMode The lock mode to apply to the return.
+	 */
+	protected NativeSQLQueryNonScalarReturn(String alias, Map propertyResults, LockMode lockMode) {
+		this.alias = alias;
+		if ( alias == null ) {
+			throw new HibernateException("alias must be specified");
+		}
+		this.lockMode = lockMode;
+		if ( propertyResults != null ) {
+			this.propertyResults.putAll( propertyResults );
+		}
+		this.hashCode = determineHashCode();
+	}
+
+	/**
+	 * Retrieve the defined result alias
+	 *
+	 * @return The result alias.
+	 */
+	public String getAlias() {
+		return alias;
+	}
+
+	/**
+	 * Retrieve the lock-mode to apply to this return
+	 *
+	 * @return The lock mode
+	 */
+	public LockMode getLockMode() {
+		return lockMode;
+	}
+
+	/**
+	 * Retrieve the user-supplied column->property mappings.
+	 *
+	 * @return The property mappings.
+	 */
+	public Map getPropertyResultsMap() {
+		return Collections.unmodifiableMap( propertyResults );
+	}
+
+	public int hashCode() {
+		return hashCode;
+	}
+
+	private int determineHashCode() {
+		int result = alias != null ? alias.hashCode() : 0;
+		result = 31 * result + ( getClass().getName().hashCode() );
+		result = 31 * result + ( lockMode != null ? lockMode.hashCode() : 0 );
+		result = 31 * result + ( propertyResults != null ? propertyResults.hashCode() : 0 );
+		return result;
+	}
+
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		NativeSQLQueryNonScalarReturn that = ( NativeSQLQueryNonScalarReturn ) o;
+
+		if ( alias != null ? !alias.equals( that.alias ) : that.alias != null ) {
+			return false;
+		}
+		if ( lockMode != null ? !lockMode.equals( that.lockMode ) : that.lockMode != null ) {
+			return false;
+		}
+		if ( propertyResults != null ? !propertyResults.equals( that.propertyResults ) : that.propertyResults != null ) {
+			return false;
+		}
+
+		return true;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryReturn.java
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
-package org.hibernate.engine.query.sql;
-
-
-/**
- * Describes a return in a native SQL query.
- * <p/>
- * IMPL NOTE : implementations should be immutable as they are used as part of cache keys for result caching.
- *
- * @author Steve Ebersole
- */
-public interface NativeSQLQueryReturn {
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+
+
+/**
+ * Describes a return in a native SQL query.
+ * <p/>
+ * IMPL NOTE : implementations should be immutable as they are used as part of cache keys for result caching.
+ *
+ * @author Steve Ebersole
+ */
+public interface NativeSQLQueryReturn {
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryRootReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryRootReturn.java
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
-package org.hibernate.engine.query.sql;
-import java.util.Map;
-import org.hibernate.LockMode;
-
-/**
- * Represents a return defined as part of a native sql query which
- * names a "root" entity.  A root entity means it is explicitly a
- * "column" in the result, as opposed to a fetched relationship or role.
- *
- * @author Steve Ebersole
- */
-public class NativeSQLQueryRootReturn extends NativeSQLQueryNonScalarReturn {
-	private final String returnEntityName;
-	private final int hashCode;
-
-	/**
-	 * Construct a return representing an entity returned at the root
-	 * of the result.
-	 *
-	 * @param alias The result alias
-	 * @param entityName The entity name.
-	 * @param lockMode The lock mode to apply
-	 */
-	public NativeSQLQueryRootReturn(String alias, String entityName, LockMode lockMode) {
-		this(alias, entityName, null, lockMode);
-	}
-
-	/**
-	 *
-	 * @param alias The result alias
-	 * @param entityName The entity name.
-	 * @param propertyResults Any user-supplied column->property mappings
-	 * @param lockMode The lock mode to apply
-	 */
-	public NativeSQLQueryRootReturn(String alias, String entityName, Map propertyResults, LockMode lockMode) {
-		super( alias, propertyResults, lockMode );
-		this.returnEntityName = entityName;
-		this.hashCode = determineHashCode();
-	}
-
-	/**
-	 * The name of the entity to be returned.
-	 *
-	 * @return The entity name
-	 */
-	public String getReturnEntityName() {
-		return returnEntityName;
-	}
-
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-		if ( ! super.equals( o ) ) {
-			return false;
-		}
-
-		NativeSQLQueryRootReturn that = ( NativeSQLQueryRootReturn ) o;
-
-		if ( returnEntityName != null ? !returnEntityName.equals( that.returnEntityName ) : that.returnEntityName != null ) {
-			return false;
-		}
-
-		return true;
-	}
-
-	public int hashCode() {
-		return hashCode;
-	}
-
-	private int determineHashCode() {
-		int result = super.hashCode();
-		result = 31 * result + ( returnEntityName != null ? returnEntityName.hashCode() : 0 );
-		return result;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+import java.util.Map;
+import org.hibernate.LockMode;
+
+/**
+ * Represents a return defined as part of a native sql query which
+ * names a "root" entity.  A root entity means it is explicitly a
+ * "column" in the result, as opposed to a fetched relationship or role.
+ *
+ * @author Steve Ebersole
+ */
+public class NativeSQLQueryRootReturn extends NativeSQLQueryNonScalarReturn {
+	private final String returnEntityName;
+	private final int hashCode;
+
+	/**
+	 * Construct a return representing an entity returned at the root
+	 * of the result.
+	 *
+	 * @param alias The result alias
+	 * @param entityName The entity name.
+	 * @param lockMode The lock mode to apply
+	 */
+	public NativeSQLQueryRootReturn(String alias, String entityName, LockMode lockMode) {
+		this(alias, entityName, null, lockMode);
+	}
+
+	/**
+	 *
+	 * @param alias The result alias
+	 * @param entityName The entity name.
+	 * @param propertyResults Any user-supplied column->property mappings
+	 * @param lockMode The lock mode to apply
+	 */
+	public NativeSQLQueryRootReturn(String alias, String entityName, Map propertyResults, LockMode lockMode) {
+		super( alias, propertyResults, lockMode );
+		this.returnEntityName = entityName;
+		this.hashCode = determineHashCode();
+	}
+
+	/**
+	 * The name of the entity to be returned.
+	 *
+	 * @return The entity name
+	 */
+	public String getReturnEntityName() {
+		return returnEntityName;
+	}
+
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+		if ( ! super.equals( o ) ) {
+			return false;
+		}
+
+		NativeSQLQueryRootReturn that = ( NativeSQLQueryRootReturn ) o;
+
+		if ( returnEntityName != null ? !returnEntityName.equals( that.returnEntityName ) : that.returnEntityName != null ) {
+			return false;
+		}
+
+		return true;
+	}
+
+	public int hashCode() {
+		return hashCode;
+	}
+
+	private int determineHashCode() {
+		int result = super.hashCode();
+		result = 31 * result + ( returnEntityName != null ? returnEntityName.hashCode() : 0 );
+		return result;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryScalarReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQueryScalarReturn.java
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
-package org.hibernate.engine.query.sql;
-import org.hibernate.type.Type;
-
-/**
- * Describes a scalar return in a native SQL query.
- *
- * @author gloegl
- */
-public class NativeSQLQueryScalarReturn implements NativeSQLQueryReturn {
-	private final Type type;
-	private final String columnAlias;
-	private final int hashCode;
-
-	public NativeSQLQueryScalarReturn(String alias, Type type) {
-		this.type = type;
-		this.columnAlias = alias;
-		this.hashCode = determineHashCode();
-	}
-
-	public String getColumnAlias() {
-		return columnAlias;
-	}
-
-	public Type getType() {
-		return type;
-	}
-
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-
-		NativeSQLQueryScalarReturn that = ( NativeSQLQueryScalarReturn ) o;
-
-		if ( columnAlias != null ? !columnAlias.equals( that.columnAlias ) : that.columnAlias != null ) {
-			return false;
-		}
-		if ( type != null ? !type.equals( that.type ) : that.type != null ) {
-			return false;
-		}
-
-		return true;
-	}
-
-	public int hashCode() {
-		return hashCode;
-	}
-
-	private int determineHashCode() {
-		int result = type != null ? type.hashCode() : 0;
-		result = 31 * result + ( getClass().getName().hashCode() );
-		result = 31 * result + ( columnAlias != null ? columnAlias.hashCode() : 0 );
-		return result;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+import org.hibernate.type.Type;
+
+/**
+ * Describes a scalar return in a native SQL query.
+ *
+ * @author gloegl
+ */
+public class NativeSQLQueryScalarReturn implements NativeSQLQueryReturn {
+	private final Type type;
+	private final String columnAlias;
+	private final int hashCode;
+
+	public NativeSQLQueryScalarReturn(String alias, Type type) {
+		this.type = type;
+		this.columnAlias = alias;
+		this.hashCode = determineHashCode();
+	}
+
+	public String getColumnAlias() {
+		return columnAlias;
+	}
+
+	public Type getType() {
+		return type;
+	}
+
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		NativeSQLQueryScalarReturn that = ( NativeSQLQueryScalarReturn ) o;
+
+		if ( columnAlias != null ? !columnAlias.equals( that.columnAlias ) : that.columnAlias != null ) {
+			return false;
+		}
+		if ( type != null ? !type.equals( that.type ) : that.type != null ) {
+			return false;
+		}
+
+		return true;
+	}
+
+	public int hashCode() {
+		return hashCode;
+	}
+
+	private int determineHashCode() {
+		int result = type != null ? type.hashCode() : 0;
+		result = 31 * result + ( getClass().getName().hashCode() );
+		result = 31 * result + ( columnAlias != null ? columnAlias.hashCode() : 0 );
+		return result;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/sql/NativeSQLQuerySpecification.java
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
-package org.hibernate.engine.query.sql;
-
-import java.util.Arrays;
-import java.util.Collection;
-import java.util.Collections;
-import java.util.HashSet;
-import java.util.Set;
-import org.hibernate.internal.util.collections.ArrayHelper;
-
-/**
- * Defines the specification or blue-print for a native-sql query.
- * Essentially a simple struct containing the information needed to "translate"
- * a native-sql query and cache that translated representation.  Also used as
- * the key by which the native-sql query plans are cached.
- *
- * @author Steve Ebersole
- */
-public class NativeSQLQuerySpecification {
-	private final String queryString;
-	private final NativeSQLQueryReturn[] queryReturns;
-	private final Set querySpaces;
-	private final int hashCode;
-
-	public NativeSQLQuerySpecification(
-			String queryString,
-	        NativeSQLQueryReturn[] queryReturns,
-	        Collection querySpaces) {
-		this.queryString = queryString;
-		this.queryReturns = queryReturns;
-		if ( querySpaces == null ) {
-			this.querySpaces = Collections.EMPTY_SET;
-		}
-		else {
-			Set tmp = new HashSet();
-			tmp.addAll( querySpaces );
-			this.querySpaces = Collections.unmodifiableSet( tmp );
-		}
-
-		// pre-determine and cache the hashcode
-		int hashCode = queryString.hashCode();
-		hashCode = 29 * hashCode + this.querySpaces.hashCode();
-		if ( this.queryReturns != null ) {
-			hashCode = 29 * hashCode + ArrayHelper.toList( this.queryReturns ).hashCode();
-		}
-		this.hashCode = hashCode;
-	}
-
-	public String getQueryString() {
-		return queryString;
-	}
-
-	public NativeSQLQueryReturn[] getQueryReturns() {
-		return queryReturns;
-	}
-
-	public Set getQuerySpaces() {
-		return querySpaces;
-	}
-
-	@Override
-    public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-
-		final NativeSQLQuerySpecification that = ( NativeSQLQuerySpecification ) o;
-
-		return querySpaces.equals( that.querySpaces ) &&
-		       queryString.equals( that.queryString ) &&
-		       Arrays.equals( queryReturns, that.queryReturns );
-	}
-
-
-	@Override
-    public int hashCode() {
-		return hashCode;
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.engine.query.spi.sql;
+
+import java.util.Arrays;
+import java.util.Collection;
+import java.util.Collections;
+import java.util.HashSet;
+import java.util.Set;
+import org.hibernate.internal.util.collections.ArrayHelper;
+
+/**
+ * Defines the specification or blue-print for a native-sql query.
+ * Essentially a simple struct containing the information needed to "translate"
+ * a native-sql query and cache that translated representation.  Also used as
+ * the key by which the native-sql query plans are cached.
+ *
+ * @author Steve Ebersole
+ */
+public class NativeSQLQuerySpecification {
+	private final String queryString;
+	private final NativeSQLQueryReturn[] queryReturns;
+	private final Set querySpaces;
+	private final int hashCode;
+
+	public NativeSQLQuerySpecification(
+			String queryString,
+	        NativeSQLQueryReturn[] queryReturns,
+	        Collection querySpaces) {
+		this.queryString = queryString;
+		this.queryReturns = queryReturns;
+		if ( querySpaces == null ) {
+			this.querySpaces = Collections.EMPTY_SET;
+		}
+		else {
+			Set tmp = new HashSet();
+			tmp.addAll( querySpaces );
+			this.querySpaces = Collections.unmodifiableSet( tmp );
+		}
+
+		// pre-determine and cache the hashcode
+		int hashCode = queryString.hashCode();
+		hashCode = 29 * hashCode + this.querySpaces.hashCode();
+		if ( this.queryReturns != null ) {
+			hashCode = 29 * hashCode + ArrayHelper.toList( this.queryReturns ).hashCode();
+		}
+		this.hashCode = hashCode;
+	}
+
+	public String getQueryString() {
+		return queryString;
+	}
+
+	public NativeSQLQueryReturn[] getQueryReturns() {
+		return queryReturns;
+	}
+
+	public Set getQuerySpaces() {
+		return querySpaces;
+	}
+
+	@Override
+    public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		final NativeSQLQuerySpecification that = ( NativeSQLQuerySpecification ) o;
+
+		return querySpaces.equals( that.querySpaces ) &&
+		       queryString.equals( that.queryString ) &&
+		       Arrays.equals( queryReturns, that.queryReturns );
+	}
+
+
+	@Override
+    public int hashCode() {
+		return hashCode;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.action.internal.CollectionRemoveAction;
+import org.hibernate.action.internal.CollectionUpdateAction;
+import org.hibernate.action.internal.EntityDeleteAction;
+import org.hibernate.action.internal.EntityIdentityInsertAction;
-import org.hibernate.action.internal.CollectionRemoveAction;
-import org.hibernate.action.internal.CollectionUpdateAction;
-import org.hibernate.action.internal.EntityDeleteAction;
-import org.hibernate.action.internal.EntityIdentityInsertAction;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/engine/AssociationKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/AssociationKey.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
-final class AssociationKey implements Serializable {
+public final class AssociationKey implements Serializable {
--- a/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
- * Copyright (c) 2--8-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
--- a/hibernate-core/src/main/java/org/hibernate/engine/CascadeStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadeStyle.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-	/**
-	 * package-protected constructor
-	 */
-	CascadeStyle() {
+	public CascadeStyle() {
--- a/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.collection.spi.PersistentCollection;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
-	/**
-	 * protected constructor
-	 */
-	CascadingAction() {
+	public CascadingAction() {
--- a/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionEntry.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.MappingException;
-import org.hibernate.MappingException;
-import org.jboss.logging.Logger;
-	CollectionEntry(PersistentCollection collection, SessionFactoryImplementor factory)
-	throws MappingException {
+	public CollectionEntry(PersistentCollection collection, SessionFactoryImplementor factory) throws MappingException {
-	void serialize(ObjectOutputStream oos) throws IOException {
+	public void serialize(ObjectOutputStream oos) throws IOException {
-	static CollectionEntry deserialize(
+	public static CollectionEntry deserialize(
--- a/hibernate-core/src/main/java/org/hibernate/engine/CollectionKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-
-	void serialize(ObjectOutputStream oos) throws IOException {
+	public void serialize(ObjectOutputStream oos) throws IOException {
-	static CollectionKey deserialize(
+	public static CollectionKey deserialize(
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
-	EntityEntry(
+	public EntityEntry(
-	void serialize(ObjectOutputStream oos) throws IOException {
+	public void serialize(ObjectOutputStream oos) throws IOException {
-		oos.writeObject( status.toString() );
-		oos.writeObject( (previousStatus == null ? "" : previousStatus.toString()) );
+		oos.writeObject( status.name() );
+		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
-	static EntityEntry deserialize(
+	public static EntityEntry deserialize(
-				Status.parse( (String) ois.readObject() ),
+				Status.valueOf( (String) ois.readObject() ),
-							Status.parse( previousStatusString ) 
+							Status.valueOf( previousStatusString )
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
- * Copyright (c) 20082011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
-	void serialize(ObjectOutputStream oos) throws IOException {
+	public void serialize(ObjectOutputStream oos) throws IOException {
-	static EntityKey deserialize(
+	public static EntityKey deserialize(
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityUniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityUniqueKey.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-	void serialize(ObjectOutputStream oos) throws IOException {
+	public void serialize(ObjectOutputStream oos) throws IOException {
-	static EntityUniqueKey deserialize(
+	public static EntityUniqueKey deserialize(
--- a/hibernate-core/src/main/java/org/hibernate/engine/ExecuteUpdateResultCheckStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/ExecuteUpdateResultCheckStyle.java
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
-package org.hibernate.engine;
-import java.io.InvalidObjectException;
-import java.io.ObjectStreamException;
-import java.io.Serializable;
-
-/**
- * For persistence operations (INSERT, UPDATE, DELETE) what style of determining
- * results (success/failure) is to be used.
- *
- * @author Steve Ebersole
- */
-public class ExecuteUpdateResultCheckStyle implements Serializable {
-	/**
-	 * Do not perform checking.  Either user simply does not want checking, or is
-	 * indicating a {@link java.sql.CallableStatement} execution in which the
-	 * checks are being performed explicitly and failures are handled through
-	 * propogation of {@link java.sql.SQLException}s.
-	 */
-	public static final ExecuteUpdateResultCheckStyle NONE = new ExecuteUpdateResultCheckStyle( "none" );
-	/**
-	 * Perform row-count checking.  Row counts are the int values returned by both
-	 * {@link java.sql.PreparedStatement#executeUpdate()} and
-	 * {@link java.sql.Statement#executeBatch()}.  These values are checked
-	 * against some expected count.
-	 */
-	public static final ExecuteUpdateResultCheckStyle COUNT = new ExecuteUpdateResultCheckStyle( "rowcount" );
-	/**
-	 * Essentially the same as {@link #COUNT} except that the row count actually
-	 * comes from an output parameter registered as part of a
-	 * {@link java.sql.CallableStatement}.  This style explicitly prohibits
-	 * statement batching from being used...
-	 */
-	public static final ExecuteUpdateResultCheckStyle PARAM = new ExecuteUpdateResultCheckStyle( "param" );
-
-	private final String name;
-
-	private ExecuteUpdateResultCheckStyle(String name) {
-		this.name = name;
-	}
-
-	private Object readResolve() throws ObjectStreamException {
-		Object resolved = parse( name );
-		if ( resolved == null ) {
-			throw new InvalidObjectException( "unknown result style [" + name + "]" );
-		}
-		return resolved;
-	}
-
-	public static ExecuteUpdateResultCheckStyle parse(String name) {
-		if ( name.equals( NONE.name ) ) {
-			return NONE;
-		}
-		else if ( name.equals( COUNT.name ) ) {
-			return COUNT;
-		}
-		else if ( name.equals( PARAM.name ) ) {
-			return PARAM;
-		}
-		else {
-			return null;
-		}
-	}
-
-	public static ExecuteUpdateResultCheckStyle determineDefault(String customSql, boolean callable) {
-		if ( customSql == null ) {
-			return COUNT;
-		}
-		else {
-			return callable ? PARAM : COUNT;
-		}
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * For persistence operations (INSERT, UPDATE, DELETE) what style of determining
+ * results (success/failure) is to be used.
+ *
+ * @author Steve Ebersole
+ */
+public enum ExecuteUpdateResultCheckStyle {
+	/**
+	 * Do not perform checking.  Either user simply does not want checking, or is
+	 * indicating a {@link java.sql.CallableStatement} execution in which the
+	 * checks are being performed explicitly and failures are handled through
+	 * propagation of {@link java.sql.SQLException}s.
+	 */
+	NONE( "none" ),
+
+	/**
+	 * Perform row-count checking.  Row counts are the int values returned by both
+	 * {@link java.sql.PreparedStatement#executeUpdate()} and
+	 * {@link java.sql.Statement#executeBatch()}.  These values are checked
+	 * against some expected count.
+	 */
+	COUNT( "rowcount" ),
+
+	/**
+	 * Essentially the same as {@link #COUNT} except that the row count actually
+	 * comes from an output parameter registered as part of a
+	 * {@link java.sql.CallableStatement}.  This style explicitly prohibits
+	 * statement batching from being used...
+	 */
+	PARAM( "param" );
+
+	private final String name;
+
+	private ExecuteUpdateResultCheckStyle(String name) {
+		this.name = name;
+	}
+
+	public static ExecuteUpdateResultCheckStyle fromExternalName(String name) {
+		if ( name.equals( NONE.name ) ) {
+			return NONE;
+		}
+		else if ( name.equals( COUNT.name ) ) {
+			return COUNT;
+		}
+		else if ( name.equals( PARAM.name ) ) {
+			return PARAM;
+		}
+		else {
+			return null;
+		}
+	}
+
+	public static ExecuteUpdateResultCheckStyle determineDefault(String customSql, boolean callable) {
+		if ( customSql == null ) {
+			return COUNT;
+		}
+		else {
+			return callable ? PARAM : COUNT;
+		}
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/FilterDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/FilterDefinition.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/IdentifierValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/IdentifierValue.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
-import java.io.Serializable;
+package org.hibernate.engine.spi;
-import org.hibernate.internal.CoreMessageLogger;
+import java.io.Serializable;
+import org.hibernate.internal.CoreMessageLogger;
+
-public class IdentifierValue {
+public class IdentifierValue implements UnsavedValueStrategy {
-        public final Boolean isUnsaved(Serializable id) {
+        public final Boolean isUnsaved(Object id) {
-        public Serializable getDefaultValue(Serializable currentValue) {
-			return currentValue;
+        public Serializable getDefaultValue(Object currentValue) {
+			return (Serializable) currentValue;
-        public final Boolean isUnsaved(Serializable id) {
+        public final Boolean isUnsaved(Object id) {
-        public Serializable getDefaultValue(Serializable currentValue) {
-			return currentValue;
+        public Serializable getDefaultValue(Object currentValue) {
+			return (Serializable) currentValue;
-        public final Boolean isUnsaved(Serializable id) {
+        public final Boolean isUnsaved(Object id) {
-        public Serializable getDefaultValue(Serializable currentValue) {
+        public Serializable getDefaultValue(Object currentValue) {
-        public final Boolean isUnsaved(Serializable id) {
+        public final Boolean isUnsaved(Object id) {
-        public Serializable getDefaultValue(Serializable currentValue) {
+        public Serializable getDefaultValue(Object currentValue) {
-	public Boolean isUnsaved(Serializable id) {
+	public Boolean isUnsaved(Object id) {
-	public Serializable getDefaultValue(Serializable currentValue) {
+	public Serializable getDefaultValue(Object currentValue) {
--- a/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+import java.util.Collections;
-import java.util.Iterator;
+
-	private Map enabledFilters;
-	private Set enabledFetchProfileNames;
+	private Map<String,Filter> enabledFilters;
+	private Set<String> enabledFetchProfileNames;
-		this( null, java.util.Collections.EMPTY_MAP, java.util.Collections.EMPTY_SET );
+		this( null, Collections.<String, Filter>emptyMap(), Collections.<String>emptySet() );
-		this( sessionFactory, new HashMap(), new HashSet() );
+		this( sessionFactory, new HashMap<String,Filter>(), new HashSet<String>() );
-	private LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Map enabledFilters, Set enabledFetchProfileNames) {
+	private LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Map<String,Filter> enabledFilters, Set<String> enabledFetchProfileNames) {
-	public Map getEnabledFilters() {
+	public Map<String,Filter> getEnabledFilters() {
-		Iterator itr = enabledFilters.values().iterator();
-		while ( itr.hasNext() ) {
-			final Filter filter = ( Filter ) itr.next();
+		for ( Filter filter : enabledFilters.values() ) {
-	public Set getEnabledFilterNames() {
+	public Set<String> getEnabledFilterNames() {
-		return ( Filter ) enabledFilters.get( filterName );
+		return enabledFilters.get( filterName );
--- a/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/Mapping.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/NamedQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-
-			Map parameterTypes
-	) {
+			Map parameterTypes) {
-			Map parameterTypes
-	) {
+			Map parameterTypes) {
--- a/hibernate-core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
--- a/hibernate-core/src/main/java/org/hibernate/engine/NonFlushedChanges.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NonFlushedChanges.java
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-
-	 * @param source
+	 * @param source The session
--- a/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
-import org.hibernate.engine.loading.LoadContexts;
+import org.hibernate.engine.loading.internal.LoadContexts;
--- a/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
-	 * @see QueryParameters#isReadOnly(SessionImplementor)
+	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
-	 * @see QueryParameters#isReadOnly(SessionImplementor)
+	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 * @see QueryParameters#isReadOnly(SessionImplementor)
+	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
-	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
--- a/hibernate-core/src/main/java/org/hibernate/engine/RowSelection.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/RowSelection.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
-
+package org.hibernate.engine.spi;
- * Represents a selection of rows in a JDBC <tt>ResultSet</tt>
+ * Represents a selection criteria for rows in a JDBC {@link java.sql.ResultSet}
+ *
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+import org.hibernate.engine.ResultSetMappingDefinition;
-import org.hibernate.engine.query.QueryPlanCache;
+import org.hibernate.engine.query.spi.QueryPlanCache;
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/Status.java
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * Represents the status of an entity with respect to
+ * this session. These statuses are for internal
+ * book-keeping only and are not intended to represent
+ * any notion that is visible to the _application_.
+ */
+public enum Status {
+	MANAGED,
+	READ_ONLY,
+	DELETED,
+	GONE,
+	LOADING,
+	SAVING
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/SubselectFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SubselectFetch.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/TypedValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
+
+
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/UnsavedValueStrategy.java
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * The base contract for determining transient status versus detached status.
+ *
+ * @author Steve Ebersole
+ */
+public interface UnsavedValueStrategy {
+	/**
+	 * Make the transient/detached determination
+	 *
+	 * @param test The value to be tested
+	 *
+	 * @return {@code true} indicates the value corresponds to unsaved data (aka, transient state; {@code false}
+	 * indicates the value does not corresponds to unsaved data (aka, detached state); {@code null} indicates that
+	 * this strategy was not able to determine conclusively.
+	 */
+	public Boolean isUnsaved(Object test);
+
+	/**
+	 * Get a default value meant to indicate transience.
+	 *
+	 * @param currentValue The current state value.
+	 *
+	 * @return The default transience value.
+	 */
+	public Object getDefaultValue(Object currentValue);
+}
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/ValueInclusion.java
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * An enum of the different ways a value might be "included".
+ * <p/>
+ * This is really an expanded true/false notion with "PARTIAL" being the
+ * expansion.  PARTIAL deals with components in the cases where
+ * parts of the referenced component might define inclusion, but the
+ * component overall does not.
+ *
+ * @author Steve Ebersole
+ */
+public enum ValueInclusion {
+	NONE,
+	FULL,
+	PARTIAL
+}
--- a/hibernate-core/src/main/java/org/hibernate/engine/VersionValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/VersionValue.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.engine;
-import org.hibernate.internal.CoreMessageLogger;
+package org.hibernate.engine.spi;
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
+import org.hibernate.internal.CoreMessageLogger;
-public class VersionValue {
+public class VersionValue implements UnsavedValueStrategy {
-	/**
-	 * Does the given version belong to a new instance?
-	 *
-	 * @param version version to check
-	 * @return true is unsaved, false is saved, null is undefined
-	 */
+	@Override
+	@Override
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/EventSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/EventSource.java
-import org.hibernate.engine.ActionQueue;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.ActionQueue;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/FlushEntityEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/FlushEntityEvent.java
-import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java
-import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.internal.Collections;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.ActionQueue;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.CollectionKey;
-import org.hibernate.engine.Collections;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.ActionQueue;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
+import org.hibernate.engine.internal.Versioning;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Nullability;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.internal.Nullability;
+import org.hibernate.engine.internal.Versioning;
-		if (ForeignKeys.isTransient(entityName, entity, getAssumedUnsaved(), source)) {
+		if ( ForeignKeys.isTransient( entityName, entity, getAssumedUnsaved(), source )) {
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.internal.Nullability;
+import org.hibernate.engine.spi.EntityEntry;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Nullability;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.Status;
-	 * {@link org.hibernate.engine.ActionQueue} for execution during flush.
+	 * {@link org.hibernate.engine.spi.ActionQueue} for execution during flush.
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.PersistenceContext;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.Nullability;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.internal.Nullability;
-					)
+				)
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
-import org.hibernate.engine.TwoPhaseLoad;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.internal.TwoPhaseLoad;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityEntry;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java
-import org.hibernate.engine.CascadingAction;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java
-import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.EntityEntry;
-		new Cascade(CascadingAction.REFRESH, Cascade.BEFORE_REFRESH, source)
+		new Cascade( CascadingAction.REFRESH, Cascade.BEFORE_REFRESH, source)
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.Cascade;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.internal.Cascade;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
-			if ( entry.getStatus()==Status.DELETED ) {
+			if ( entry.getStatus()== Status.DELETED ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
+import org.hibernate.engine.spi.CollectionKey;
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.CollectionKey;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
-import org.hibernate.engine.Collections;
+import org.hibernate.engine.internal.Collections;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
+import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
-import org.hibernate.engine.JoinSequence;
+import org.hibernate.engine.internal.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/NameGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/NameGenerator.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.JoinSequence;
-import org.hibernate.engine.ParameterBinder;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.internal.JoinSequence;
+import org.hibernate.engine.internal.ParameterBinder;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
+import org.hibernate.engine.internal.JoinSequence;
-import org.hibernate.engine.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
+import org.hibernate.engine.internal.JoinSequence;
-import org.hibernate.engine.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
+import org.hibernate.engine.internal.JoinSequence;
-import org.hibernate.engine.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
+import org.hibernate.engine.internal.JoinSequence;
-import org.hibernate.engine.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java
+
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.JoinSequence;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.JoinSequence;
+import org.hibernate.engine.spi.QueryParameters;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
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
-package org.hibernate.hql.ast.tree;
-
-import org.hibernate.QueryException;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.hql.QueryTranslator;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.type.LiteralType;
-import org.hibernate.type.Type;
-
-/**
- * A node representing a static Java constant.
- *
- * @author Steve Ebersole
- */
-public class JavaConstantNode extends Node implements ExpectedTypeAwareNode, SessionFactoryAwareNode {
-
-	private SessionFactoryImplementor factory;
-
-	private String constantExpression;
-	private Object constantValue;
-	private Type heuristicType;
-
-	private Type expectedType;
-
-	@Override
-    public void setText(String s) {
-		// for some reason the antlr.CommonAST initialization routines force
-		// this method to get called twice.  The first time with an empty string
-		if ( StringHelper.isNotEmpty( s ) ) {
-			constantExpression = s;
-			constantValue = ReflectHelper.getConstantValue( s );
-			heuristicType = factory.getTypeResolver().heuristicType( constantValue.getClass().getName() );
-			super.setText( s );
-		}
-	}
-
-	public void setExpectedType(Type expectedType) {
-		this.expectedType = expectedType;
-	}
-
-	public Type getExpectedType() {
-		return expectedType;
-	}
-
-	public void setSessionFactory(SessionFactoryImplementor factory) {
-		this.factory = factory;
-	}
-
-	@Override
-    public String getRenderText(SessionFactoryImplementor sessionFactory) {
-		Type type = expectedType == null
-				? heuristicType
-				: Number.class.isAssignableFrom( heuristicType.getReturnedClass() )
-						? heuristicType
-						: expectedType;
-		try {
-			LiteralType literalType = ( LiteralType ) type;
-			Dialect dialect = factory.getDialect();
-			return literalType.objectToSQLString( constantValue, dialect );
-		}
-		catch ( Throwable t ) {
-			throw new QueryException( QueryTranslator.ERROR_CANNOT_FORMAT_LITERAL + constantExpression, t );
-		}
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.hql.ast.tree;
+
+import org.hibernate.QueryException;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.hql.QueryTranslator;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.type.LiteralType;
+import org.hibernate.type.Type;
+
+/**
+ * A node representing a static Java constant.
+ *
+ * @author Steve Ebersole
+ */
+public class JavaConstantNode extends Node implements ExpectedTypeAwareNode, SessionFactoryAwareNode {
+
+	private SessionFactoryImplementor factory;
+
+	private String constantExpression;
+	private Object constantValue;
+	private Type heuristicType;
+
+	private Type expectedType;
+
+	@Override
+    public void setText(String s) {
+		// for some reason the antlr.CommonAST initialization routines force
+		// this method to get called twice.  The first time with an empty string
+		if ( StringHelper.isNotEmpty( s ) ) {
+			constantExpression = s;
+			constantValue = ReflectHelper.getConstantValue( s );
+			heuristicType = factory.getTypeResolver().heuristicType( constantValue.getClass().getName() );
+			super.setText( s );
+		}
+	}
+
+	public void setExpectedType(Type expectedType) {
+		this.expectedType = expectedType;
+	}
+
+	public Type getExpectedType() {
+		return expectedType;
+	}
+
+	public void setSessionFactory(SessionFactoryImplementor factory) {
+		this.factory = factory;
+	}
+
+	@Override
+    public String getRenderText(SessionFactoryImplementor sessionFactory) {
+		Type type = expectedType == null
+				? heuristicType
+				: Number.class.isAssignableFrom( heuristicType.getReturnedClass() )
+						? heuristicType
+						: expectedType;
+		try {
+			LiteralType literalType = ( LiteralType ) type;
+			Dialect dialect = factory.getDialect();
+			return literalType.objectToSQLString( constantValue, dialect );
+		}
+		catch ( Throwable t ) {
+			throw new QueryException( QueryTranslator.ERROR_CANNOT_FORMAT_LITERAL + constantExpression, t );
+		}
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java
- * internal {@link org.hibernate.engine.JoinSequence join handling} be able to either:<ul>
+ * internal {@link org.hibernate.engine.internal.JoinSequence join handling} be able to either:<ul>
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import org.hibernate.engine.SessionFactoryImplementor;
-
-/**
- * Interface for nodes which require access to the SessionFactory
- *
- * @author Steve Ebersole
- */
-public interface SessionFactoryAwareNode {
-	public void setSessionFactory(SessionFactoryImplementor sessionFactory);
-}
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+
+/**
+ * Interface for nodes which require access to the SessionFactory
+ *
+ * @author Steve Ebersole
+ */
+public interface SessionFactoryAwareNode {
+	public void setSessionFactory(SessionFactoryImplementor sessionFactory);
+}
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
+import org.hibernate.engine.internal.JoinSequence;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.engine.JoinSequence;
-import org.hibernate.engine.LoadQueryInfluencers;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java
-import org.hibernate.engine.JoinSequence;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.internal.JoinSequence;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java
-import org.hibernate.engine.JoinSequence;
+import org.hibernate.engine.internal.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+import org.hibernate.engine.internal.JoinSequence;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.JoinSequence;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
-import org.hibernate.engine.JoinSequence;
+import org.hibernate.engine.internal.JoinSequence;
--- a/hibernate-core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/Assigned.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/Assigned.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.SessionImplementor;
-					associatedObject, 
+					associatedObject,
--- a/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
+
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.ValueInclusion;
--- a/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/TableHiLoGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/TableHiLoGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/UUIDHexGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/UUIDHexGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.SessionImplementor;
-
-/**
- * Encapsulates definition of the underlying data structure backing a
- * sequence-style generator.
- *
- * @author Steve Ebersole
- */
-public interface DatabaseStructure {
-	/**
-	 * The name of the database structure (table or sequence).
-	 * @return The structure name.
-	 */
-	public String getName();
-
-	/**
-	 * How many times has this structure been accessed through this reference?
-	 * @return The number of accesses.
-	 */
-	public int getTimesAccessed();
-
-	/**
-	 * The configured initial value
-	 * @return The configured initial value
-	 */
-	public int getInitialValue();
-
-	/**
-	 * The configured increment size
-	 * @return The configured increment size
-	 */
-	public int getIncrementSize();
-
-	/**
-	 * A callback to be able to get the next value from the underlying
-	 * structure as needed.
-	 *
-	 * @param session The session.
-	 * @return The next value.
-	 */
-	public AccessCallback buildCallback(SessionImplementor session);
-
-	/**
-	 * Prepare this structure for use.  Called sometime after instantiation,
-	 * but before first use.
-	 *
-	 * @param optimizer The optimizer being applied to the generator.
-	 */
-	public void prepare(Optimizer optimizer);
-
-	/**
-	 * Commands needed to create the underlying structures.
-	 * @param dialect The database dialect being used.
-	 * @return The creation commands.
-	 */
-	public String[] sqlCreateStrings(Dialect dialect);
-
-	/**
-	 * Commands needed to drop the underlying structures.
-	 * @param dialect The database dialect being used.
-	 * @return The drop commands.
-	 */
-	public String[] sqlDropStrings(Dialect dialect);
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Encapsulates definition of the underlying data structure backing a
+ * sequence-style generator.
+ *
+ * @author Steve Ebersole
+ */
+public interface DatabaseStructure {
+	/**
+	 * The name of the database structure (table or sequence).
+	 * @return The structure name.
+	 */
+	public String getName();
+
+	/**
+	 * How many times has this structure been accessed through this reference?
+	 * @return The number of accesses.
+	 */
+	public int getTimesAccessed();
+
+	/**
+	 * The configured initial value
+	 * @return The configured initial value
+	 */
+	public int getInitialValue();
+
+	/**
+	 * The configured increment size
+	 * @return The configured increment size
+	 */
+	public int getIncrementSize();
+
+	/**
+	 * A callback to be able to get the next value from the underlying
+	 * structure as needed.
+	 *
+	 * @param session The session.
+	 * @return The next value.
+	 */
+	public AccessCallback buildCallback(SessionImplementor session);
+
+	/**
+	 * Prepare this structure for use.  Called sometime after instantiation,
+	 * but before first use.
+	 *
+	 * @param optimizer The optimizer being applied to the generator.
+	 */
+	public void prepare(Optimizer optimizer);
+
+	/**
+	 * Commands needed to create the underlying structures.
+	 * @param dialect The database dialect being used.
+	 * @return The creation commands.
+	 */
+	public String[] sqlCreateStrings(Dialect dialect);
+
+	/**
+	 * Commands needed to drop the underlying structures.
+	 * @param dialect The database dialect being used.
+	 * @return The drop commands.
+	 */
+	public String[] sqlDropStrings(Dialect dialect);
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.id.PostInsertIdentityPersister;
-import org.hibernate.pretty.MessageHelper;
-
-/**
- * Abstract InsertGeneratedIdentifierDelegate implementation where the
- * underlying strategy causes the enerated identitifer to be returned as an
- * effect of performing the insert statement.  Thus, there is no need for an
- * additional sql statement to determine the generated identitifer.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractReturningDelegate implements InsertGeneratedIdentifierDelegate {
-	private final PostInsertIdentityPersister persister;
-
-	public AbstractReturningDelegate(PostInsertIdentityPersister persister) {
-		this.persister = persister;
-	}
-
-	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
-		try {
-			// prepare and execute the insert
-			PreparedStatement insert = prepare( insertSQL, session );
-			try {
-				binder.bindValues( insert );
-				return executeAndExtract( insert );
-			}
-			finally {
-				releaseStatement( insert, session );
-			}
-		}
-		catch ( SQLException sqle ) {
-			throw session.getFactory().getSQLExceptionHelper().convert(
-			        sqle,
-			        "could not insert: " + MessageHelper.infoString( persister ),
-			        insertSQL
-				);
-		}
-	}
-
-	protected PostInsertIdentityPersister getPersister() {
-		return persister;
-	}
-
-	protected abstract PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException;
-
-	protected abstract Serializable executeAndExtract(PreparedStatement insert) throws SQLException;
-
-	protected void releaseStatement(PreparedStatement insert, SessionImplementor session) throws SQLException {
-		insert.close();
-	}
-}
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.id.PostInsertIdentityPersister;
+import org.hibernate.pretty.MessageHelper;
+
+/**
+ * Abstract InsertGeneratedIdentifierDelegate implementation where the
+ * underlying strategy causes the enerated identitifer to be returned as an
+ * effect of performing the insert statement.  Thus, there is no need for an
+ * additional sql statement to determine the generated identitifer.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractReturningDelegate implements InsertGeneratedIdentifierDelegate {
+	private final PostInsertIdentityPersister persister;
+
+	public AbstractReturningDelegate(PostInsertIdentityPersister persister) {
+		this.persister = persister;
+	}
+
+	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
+		try {
+			// prepare and execute the insert
+			PreparedStatement insert = prepare( insertSQL, session );
+			try {
+				binder.bindValues( insert );
+				return executeAndExtract( insert );
+			}
+			finally {
+				releaseStatement( insert, session );
+			}
+		}
+		catch ( SQLException sqle ) {
+			throw session.getFactory().getSQLExceptionHelper().convert(
+			        sqle,
+			        "could not insert: " + MessageHelper.infoString( persister ),
+			        insertSQL
+				);
+		}
+	}
+
+	protected PostInsertIdentityPersister getPersister() {
+		return persister;
+	}
+
+	protected abstract PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException;
+
+	protected abstract Serializable executeAndExtract(PreparedStatement insert) throws SQLException;
+
+	protected void releaseStatement(PreparedStatement insert, SessionImplementor session) throws SQLException {
+		insert.close();
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.id.PostInsertIdentityPersister;
-import org.hibernate.pretty.MessageHelper;
-
-/**
- * Abstract InsertGeneratedIdentifierDelegate implementation where the
- * underlying strategy requires an subsequent select after the insert
- * to determine the generated identifier.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractSelectingDelegate implements InsertGeneratedIdentifierDelegate {
-	private final PostInsertIdentityPersister persister;
-
-	protected AbstractSelectingDelegate(PostInsertIdentityPersister persister) {
-		this.persister = persister;
-	}
-
-	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
-		try {
-			// prepare and execute the insert
-			PreparedStatement insert = session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.getStatementPreparer()
-					.prepareStatement( insertSQL, PreparedStatement.NO_GENERATED_KEYS );
-			try {
-				binder.bindValues( insert );
-				insert.executeUpdate();
-			}
-			finally {
-				insert.close();
-			}
-		}
-		catch ( SQLException sqle ) {
-			throw session.getFactory().getSQLExceptionHelper().convert(
-			        sqle,
-			        "could not insert: " + MessageHelper.infoString( persister ),
-			        insertSQL
-				);
-		}
-
-		final String selectSQL = getSelectSQL();
-
-		try {
-			//fetch the generated id in a separate query
-			PreparedStatement idSelect = session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.getStatementPreparer()
-					.prepareStatement( selectSQL, false );
-			try {
-				bindParameters( session, idSelect, binder.getEntity() );
-				ResultSet rs = idSelect.executeQuery();
-				try {
-					return getResult( session, rs, binder.getEntity() );
-				}
-				finally {
-					rs.close();
-				}
-			}
-			finally {
-				idSelect.close();
-			}
-
-		}
-		catch ( SQLException sqle ) {
-			throw session.getFactory().getSQLExceptionHelper().convert(
-			        sqle,
-			        "could not retrieve generated id after insert: " + MessageHelper.infoString( persister ),
-			        insertSQL
-			);
-		}
-	}
-
-	/**
-	 * Get the SQL statement to be used to retrieve generated key values.
-	 *
-	 * @return The SQL command string
-	 */
-	protected abstract String getSelectSQL();
-
-	/**
-	 * Bind any required parameter values into the SQL command {@link #getSelectSQL}.
-	 *
-	 * @param session The session
-	 * @param ps The prepared {@link #getSelectSQL SQL} command
-	 * @param entity The entity being saved.
-	 * @throws SQLException
-	 */
-	protected void bindParameters(
-			SessionImplementor session,
-	        PreparedStatement ps,
-	        Object entity) throws SQLException {
-	}
-
-	/**
-	 * Extract the generated key value from the given result set.
-	 *
-	 * @param session The session
-	 * @param rs The result set containing the generated primay key values.
-	 * @param entity The entity being saved.
-	 * @return The generated identifier
-	 * @throws SQLException
-	 */
-	protected abstract Serializable getResult(
-			SessionImplementor session,
-	        ResultSet rs,
-	        Object entity) throws SQLException;
-
-}
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.id.PostInsertIdentityPersister;
+import org.hibernate.pretty.MessageHelper;
+
+/**
+ * Abstract InsertGeneratedIdentifierDelegate implementation where the
+ * underlying strategy requires an subsequent select after the insert
+ * to determine the generated identifier.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractSelectingDelegate implements InsertGeneratedIdentifierDelegate {
+	private final PostInsertIdentityPersister persister;
+
+	protected AbstractSelectingDelegate(PostInsertIdentityPersister persister) {
+		this.persister = persister;
+	}
+
+	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
+		try {
+			// prepare and execute the insert
+			PreparedStatement insert = session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getStatementPreparer()
+					.prepareStatement( insertSQL, PreparedStatement.NO_GENERATED_KEYS );
+			try {
+				binder.bindValues( insert );
+				insert.executeUpdate();
+			}
+			finally {
+				insert.close();
+			}
+		}
+		catch ( SQLException sqle ) {
+			throw session.getFactory().getSQLExceptionHelper().convert(
+			        sqle,
+			        "could not insert: " + MessageHelper.infoString( persister ),
+			        insertSQL
+				);
+		}
+
+		final String selectSQL = getSelectSQL();
+
+		try {
+			//fetch the generated id in a separate query
+			PreparedStatement idSelect = session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getStatementPreparer()
+					.prepareStatement( selectSQL, false );
+			try {
+				bindParameters( session, idSelect, binder.getEntity() );
+				ResultSet rs = idSelect.executeQuery();
+				try {
+					return getResult( session, rs, binder.getEntity() );
+				}
+				finally {
+					rs.close();
+				}
+			}
+			finally {
+				idSelect.close();
+			}
+
+		}
+		catch ( SQLException sqle ) {
+			throw session.getFactory().getSQLExceptionHelper().convert(
+			        sqle,
+			        "could not retrieve generated id after insert: " + MessageHelper.infoString( persister ),
+			        insertSQL
+			);
+		}
+	}
+
+	/**
+	 * Get the SQL statement to be used to retrieve generated key values.
+	 *
+	 * @return The SQL command string
+	 */
+	protected abstract String getSelectSQL();
+
+	/**
+	 * Bind any required parameter values into the SQL command {@link #getSelectSQL}.
+	 *
+	 * @param session The session
+	 * @param ps The prepared {@link #getSelectSQL SQL} command
+	 * @param entity The entity being saved.
+	 * @throws SQLException
+	 */
+	protected void bindParameters(
+			SessionImplementor session,
+	        PreparedStatement ps,
+	        Object entity) throws SQLException {
+	}
+
+	/**
+	 * Extract the generated key value from the given result set.
+	 *
+	 * @param session The session
+	 * @param rs The result set containing the generated primay key values.
+	 * @param entity The entity being saved.
+	 * @return The generated identifier
+	 * @throws SQLException
+	 */
+	protected abstract Serializable getResult(
+			SessionImplementor session,
+	        ResultSet rs,
+	        Object entity) throws SQLException;
+
+}
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.io.Serializable;
-import org.hibernate.engine.SessionImplementor;
-
-/**
- * Responsible for handling delegation relating to variants in how
- * insert-generated-identifier generator strategies dictate processing:<ul>
- * <li>building the sql insert statement
- * <li>determination of the generated identifier value
- * </ul>
- *
- * @author Steve Ebersole
- */
-public interface InsertGeneratedIdentifierDelegate {
-
-	/**
-	 * Build a {@link org.hibernate.sql.Insert} specific to the delegate's mode
-	 * of handling generated key values.
-	 *
-	 * @return The insert object.
-	 */
-	public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert();
-
-	/**
-	 * Perform the indicated insert SQL statement and determine the identifier value
-	 * generated.
-	 *
-	 * @param insertSQL The INSERT statement string
-	 * @param session The session in which we are operating
-	 * @param binder The param binder
-	 * @return The generated identifier value.
-	 */
-	public Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder);
-
-}
+import java.io.Serializable;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Responsible for handling delegation relating to variants in how
+ * insert-generated-identifier generator strategies dictate processing:<ul>
+ * <li>building the sql insert statement
+ * <li>determination of the generated identifier value
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public interface InsertGeneratedIdentifierDelegate {
+
+	/**
+	 * Build a {@link org.hibernate.sql.Insert} specific to the delegate's mode
+	 * of handling generated key values.
+	 *
+	 * @return The insert object.
+	 */
+	public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert();
+
+	/**
+	 * Perform the indicated insert SQL statement and determine the identifier value
+	 * generated.
+	 *
+	 * @param insertSQL The INSERT statement string
+	 * @param session The session in which we are operating
+	 * @param binder The param binder
+	 * @return The generated identifier value.
+	 */
+	public Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder);
+
+}
--- a/hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TypedValue;
-import org.hibernate.engine.query.ParameterMetadata;
+import org.hibernate.engine.query.spi.ParameterMetadata;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.NativeSQLQueryPlan;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
-public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
+public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract,
+													 SessionImplementor, TransactionContext {
--- a/hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.query.ParameterMetadata;
+import org.hibernate.engine.query.spi.ParameterMetadata;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
-import org.hibernate.engine.CollectionKey;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.loading.CollectionLoadContext;
-import org.hibernate.engine.loading.EntityLoadContext;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.loading.internal.CollectionLoadContext;
+import org.hibernate.engine.loading.internal.EntityLoadContext;
--- a/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
-import org.hibernate.engine.FilterDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
-import org.hibernate.engine.ActionQueue;
-import org.hibernate.engine.NonFlushedChanges;
-import org.hibernate.engine.StatefulPersistenceContext;
+import org.hibernate.engine.internal.StatefulPersistenceContext;
+import org.hibernate.engine.spi.ActionQueue;
+import org.hibernate.engine.spi.NonFlushedChanges;
--- a/hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.query.ParameterMetadata;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.query.spi.ParameterMetadata;
--- a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
-import org.hibernate.engine.NamedSQLQueryDefinition;
-import org.hibernate.engine.QueryParameters;
+import org.hibernate.engine.query.spi.ParameterMetadata;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.query.ParameterMetadata;
-import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
-import org.hibernate.engine.FilterDefinition;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
+import org.hibernate.engine.query.spi.QueryPlanCache;
+import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.query.QueryPlanCache;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
-import org.hibernate.engine.ActionQueue;
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.NonFlushedChanges;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.StatefulPersistenceContext;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.internal.StatefulPersistenceContext;
+import org.hibernate.engine.query.spi.FilterQueryPlan;
+import org.hibernate.engine.spi.ActionQueue;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.NonFlushedChanges;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Status;
-import org.hibernate.engine.query.FilterQueryPlan;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.NativeSQLQueryPlan;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
- *     <li>{@link org.hibernate.engine.SessionImplementor} to other Hibernate components (SPI)</li>
+ *     <li>{@link org.hibernate.engine.spi.SessionImplementor} to other Hibernate components (SPI)</li>
-		loadQueryInfluencers = ( LoadQueryInfluencers ) ois.readObject();
+		loadQueryInfluencers = (LoadQueryInfluencers) ois.readObject();
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.NonFlushedChanges;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.StatefulPersistenceContext;
-import org.hibernate.engine.Versioning;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.NativeSQLQueryPlan;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.NonFlushedChanges;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.internal.StatefulPersistenceContext;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
-			boolean substitute = Versioning.seedVersion(state, persister.getVersionProperty(), persister.getVersionType(), this);
+			boolean substitute = Versioning.seedVersion(
+					state, persister.getVersionProperty(), persister.getVersionType(), this
+			);
--- a/hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
-import org.hibernate.engine.FilterDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/BasicLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/BasicLoader.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.JoinHelper;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.internal.JoinHelper;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+import org.hibernate.engine.internal.TwoPhaseLoad;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SubselectFetch;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.EntityUniqueKey;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.SubselectFetch;
-import org.hibernate.engine.TwoPhaseLoad;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.TypedValue;
-			);
+		);
--- a/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
-import org.hibernate.engine.JoinHelper;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.internal.JoinHelper;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.RowSelection;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.ParameterParser;
+import org.hibernate.engine.query.spi.ParameterParser;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.sql.NativeSQLQueryCollectionReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryNonScalarReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
- * Responsible for processing the series of {@link org.hibernate.engine.query.sql.NativeSQLQueryReturn returns}
- * defined by a {@link org.hibernate.engine.query.sql.NativeSQLQuerySpecification} and
+ * Responsible for processing the series of {@link org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn returns}
+ * defined by a {@link org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification} and
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-	public CascadeEntityJoinWalker(OuterJoinLoadable persister, CascadingAction action, SessionFactoryImplementor factory) 
+	public CascadeEntityJoinWalker(OuterJoinLoadable persister, CascadingAction action, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityLoader.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/IdentifierCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/IdentifierCollection.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Join.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Join.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/JoinedSubclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/JoinedSubclass.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/RelationalModel.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/RelationalModel.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
+import org.hibernate.engine.spi.Mapping;
-import org.hibernate.engine.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Set.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Set.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UnionSubclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UnionSubclass.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-	 * @deprecated Use {@link #instantiate(Serializable, SessionImplementor)} instead
+	 * @deprecated Use {@link #instantiate(Serializable, org.hibernate.engine.spi.SessionImplementor)} instead
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CustomSQL.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CustomSQL.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
-import java.util.Iterator;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
-		return ExecuteUpdateResultCheckStyle.parse( check );
+		return ExecuteUpdateResultCheckStyle.fromExternalName( check );
--- a/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.type.Type;
-
-/**
- * A specialized ParameterSpecification impl for dealing with a collection-key as part of a collection filter
- * compilation.
- *
- * @author Steve Ebersole
- */
-public class CollectionFilterKeyParameterSpecification implements ParameterSpecification {
-	private final String collectionRole;
-	private final Type keyType;
-	private final int queryParameterPosition;
-
-	/**
-	 * Creates a specialized collection-filter collection-key parameter spec.
-	 *
-	 * @param collectionRole The collection role being filtered.
-	 * @param keyType The mapped collection-key type.
-	 * @param queryParameterPosition The position within {@link org.hibernate.engine.QueryParameters} where
-	 * we can find the appropriate param value to bind.
-	 */
-	public CollectionFilterKeyParameterSpecification(String collectionRole, Type keyType, int queryParameterPosition) {
-		this.collectionRole = collectionRole;
-		this.keyType = keyType;
-		this.queryParameterPosition = queryParameterPosition;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int bind(
-			PreparedStatement statement,
-			QueryParameters qp,
-			SessionImplementor session,
-			int position) throws SQLException {
-		Object value = qp.getPositionalParameterValues()[queryParameterPosition];
-		keyType.nullSafeSet( statement, value, position, session );
-		return keyType.getColumnSpan( session.getFactory() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Type getExpectedType() {
-		return keyType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setExpectedType(Type expectedType) {
-		// todo : throw exception?
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String renderDisplayInfo() {
-		return "collection-filter-key=" + collectionRole;
-	}
-}
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.type.Type;
+
+/**
+ * A specialized ParameterSpecification impl for dealing with a collection-key as part of a collection filter
+ * compilation.
+ *
+ * @author Steve Ebersole
+ */
+public class CollectionFilterKeyParameterSpecification implements ParameterSpecification {
+	private final String collectionRole;
+	private final Type keyType;
+	private final int queryParameterPosition;
+
+	/**
+	 * Creates a specialized collection-filter collection-key parameter spec.
+	 *
+	 * @param collectionRole The collection role being filtered.
+	 * @param keyType The mapped collection-key type.
+	 * @param queryParameterPosition The position within {@link org.hibernate.engine.spi.QueryParameters} where
+	 * we can find the appropriate param value to bind.
+	 */
+	public CollectionFilterKeyParameterSpecification(String collectionRole, Type keyType, int queryParameterPosition) {
+		this.collectionRole = collectionRole;
+		this.keyType = keyType;
+		this.queryParameterPosition = queryParameterPosition;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int bind(
+			PreparedStatement statement,
+			QueryParameters qp,
+			SessionImplementor session,
+			int position) throws SQLException {
+		Object value = qp.getPositionalParameterValues()[queryParameterPosition];
+		keyType.nullSafeSet( statement, value, position, session );
+		return keyType.getColumnSpan( session.getFactory() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Type getExpectedType() {
+		return keyType;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void setExpectedType(Type expectedType) {
+		// todo : throw exception?
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public String renderDisplayInfo() {
+		return "collection-filter-key=" + collectionRole;
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import java.util.Collection;
-import java.util.Iterator;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.type.Type;
-
-/**
- * A specialized ParameterSpecification impl for dealing with a dynamic filter parameters.
- * 
- * @see org.hibernate.Session#enableFilter(String)
- *
- * @author Steve Ebersole
- */
-public class DynamicFilterParameterSpecification implements ParameterSpecification {
-	private final String filterName;
-	private final String parameterName;
-	private final Type definedParameterType;
-
-	/**
-	 * Constructs a parameter specification for a particular filter parameter.
-	 *
-	 * @param filterName The name of the filter
-	 * @param parameterName The name of the parameter
-	 * @param definedParameterType The paremeter type specified on the filter metadata
-	 */
-	public DynamicFilterParameterSpecification(
-			String filterName,
-			String parameterName,
-			Type definedParameterType) {
-		this.filterName = filterName;
-		this.parameterName = parameterName;
-		this.definedParameterType = definedParameterType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int bind(
-			PreparedStatement statement,
-			QueryParameters qp,
-			SessionImplementor session,
-			int start) throws SQLException {
-		final int columnSpan = definedParameterType.getColumnSpan( session.getFactory() );
-		final Object value = session.getLoadQueryInfluencers().getFilterParameterValue( filterName + '.' + parameterName );
-		if ( Collection.class.isInstance( value ) ) {
-			int positions = 0;
-			Iterator itr = ( ( Collection ) value ).iterator();
-			while ( itr.hasNext() ) {
-				definedParameterType.nullSafeSet( statement, itr.next(), start + positions, session );
-				positions += columnSpan;
-			}
-			return positions;
-		}
-		else {
-			definedParameterType.nullSafeSet( statement, value, start, session );
-			return columnSpan;
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Type getExpectedType() {
-		return definedParameterType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setExpectedType(Type expectedType) {
-		// todo : throw exception?  maybe warn if not the same?
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String renderDisplayInfo() {
-		return "dynamic-filter={filterName=" + filterName + ",paramName=" + parameterName + "}";
-	}
-}
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.util.Collection;
+import java.util.Iterator;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.type.Type;
+
+/**
+ * A specialized ParameterSpecification impl for dealing with a dynamic filter parameters.
+ * 
+ * @see org.hibernate.Session#enableFilter(String)
+ *
+ * @author Steve Ebersole
+ */
+public class DynamicFilterParameterSpecification implements ParameterSpecification {
+	private final String filterName;
+	private final String parameterName;
+	private final Type definedParameterType;
+
+	/**
+	 * Constructs a parameter specification for a particular filter parameter.
+	 *
+	 * @param filterName The name of the filter
+	 * @param parameterName The name of the parameter
+	 * @param definedParameterType The paremeter type specified on the filter metadata
+	 */
+	public DynamicFilterParameterSpecification(
+			String filterName,
+			String parameterName,
+			Type definedParameterType) {
+		this.filterName = filterName;
+		this.parameterName = parameterName;
+		this.definedParameterType = definedParameterType;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int bind(
+			PreparedStatement statement,
+			QueryParameters qp,
+			SessionImplementor session,
+			int start) throws SQLException {
+		final int columnSpan = definedParameterType.getColumnSpan( session.getFactory() );
+		final Object value = session.getLoadQueryInfluencers().getFilterParameterValue( filterName + '.' + parameterName );
+		if ( Collection.class.isInstance( value ) ) {
+			int positions = 0;
+			Iterator itr = ( ( Collection ) value ).iterator();
+			while ( itr.hasNext() ) {
+				definedParameterType.nullSafeSet( statement, itr.next(), start + positions, session );
+				positions += columnSpan;
+			}
+			return positions;
+		}
+		else {
+			definedParameterType.nullSafeSet( statement, value, start, session );
+			return columnSpan;
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Type getExpectedType() {
+		return definedParameterType;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void setExpectedType(Type expectedType) {
+		// todo : throw exception?  maybe warn if not the same?
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public String renderDisplayInfo() {
+		return "dynamic-filter={filterName=" + filterName + ",paramName=" + parameterName + "}";
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
--- a/hibernate-core/src/main/java/org/hibernate/param/ParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/ParameterSpecification.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SubselectFetch;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.SubselectFetch;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.SubselectFetch;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SubselectFetch;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java
-import org.hibernate.engine.Mapping;
+import org.hibernate.engine.spi.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.SubselectFetch;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SubselectFetch;
-    protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) 
+    protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.ValueInclusion;
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.ValueInclusion;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
+import org.hibernate.engine.spi.Mapping;
-import org.hibernate.engine.Mapping;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.ValueInclusion;
- * matching the signature of: {@link org.hibernate.mapping.PersistentClass}, {@link org.hibernate.engine.SessionFactoryImplementor}
+ * matching the signature of: {@link org.hibernate.mapping.PersistentClass}, {@link org.hibernate.engine.spi.SessionFactoryImplementor}
-	 * {@link org.hibernate.engine.PersistenceContext}.
+	 * {@link org.hibernate.engine.spi.PersistenceContext}.
-	 * {@link org.hibernate.engine.PersistenceContext}.
+	 * {@link org.hibernate.engine.spi.PersistenceContext}.
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[jk], insertCallable[jk] )
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault(
+					customSQLInsert[jk], insertCallable[jk]
+			)
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java
-import org.hibernate.engine.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyle;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
+import org.hibernate.engine.spi.TypedValue;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.TypedValue;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) 
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session)
--- a/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/Getter.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/Getter.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/property/Setter.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/Setter.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/sql/Template.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Template.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
-	 * @see org.hibernate.engine.EntityKey
+	 * @see org.hibernate.engine.spi.EntityKey
-	 * @see org.hibernate.engine.CollectionKey
+	 * @see org.hibernate.engine.spi.CollectionKey
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/SessionStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/SessionStatisticsImpl.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsFactory.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.stat.Statistics;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
-import org.hibernate.engine.IdentifierValue;
+import org.hibernate.engine.spi.IdentifierValue;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
-import org.hibernate.engine.IdentifierValue;
-import org.hibernate.engine.UnsavedValueFactory;
-import org.hibernate.engine.VersionValue;
+import org.hibernate.engine.spi.IdentifierValue;
+import org.hibernate.engine.internal.UnsavedValueFactory;
+import org.hibernate.engine.spi.VersionValue;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
-import org.hibernate.engine.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyle;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.VersionValue;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.VersionValue;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.lang.reflect.Method;
-import java.util.Iterator;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.Setter;
-import org.hibernate.tuple.Instantiator;
-
-/**
- * Support for tuplizers relating to components.
- *
- * @author Gavin King
- * @author Steve Ebersole
- */
-public abstract class AbstractComponentTuplizer implements ComponentTuplizer {
-	protected final Getter[] getters;
-	protected final Setter[] setters;
-	protected final int propertySpan;
-	protected final Instantiator instantiator;
-	protected final boolean hasCustomAccessors;
-
-	protected abstract Instantiator buildInstantiator(Component component);
-	protected abstract Getter buildGetter(Component component, Property prop);
-	protected abstract Setter buildSetter(Component component, Property prop);
-
-	protected AbstractComponentTuplizer(Component component) {
-		propertySpan = component.getPropertySpan();
-		getters = new Getter[propertySpan];
-		setters = new Setter[propertySpan];
-
-		Iterator iter = component.getPropertyIterator();
-		boolean foundCustomAccessor=false;
-		int i = 0;
-		while ( iter.hasNext() ) {
-			Property prop = ( Property ) iter.next();
-			getters[i] = buildGetter( component, prop );
-			setters[i] = buildSetter( component, prop );
-			if ( !prop.isBasicPropertyAccessor() ) {
-				foundCustomAccessor = true;
-			}
-			i++;
-		}
-		hasCustomAccessors = foundCustomAccessor;
-		instantiator = buildInstantiator( component );
-	}
-
-	public Object getPropertyValue(Object component, int i) throws HibernateException {
-		return getters[i].get( component );
-	}
-
-	public Object[] getPropertyValues(Object component) throws HibernateException {
-		Object[] values = new Object[propertySpan];
-		for ( int i = 0; i < propertySpan; i++ ) {
-			values[i] = getPropertyValue( component, i );
-		}
-		return values;
-	}
-
-	public boolean isInstance(Object object) {
-		return instantiator.isInstance(object);
-	}
-
-	public void setPropertyValues(Object component, Object[] values) throws HibernateException {
-		for ( int i = 0; i < propertySpan; i++ ) {
-			setters[i].set( component, values[i], null );
-		}
-	}
-
-	/**
-	* This method does not populate the component parent
-	*/
-	public Object instantiate() throws HibernateException {
-		return instantiator.instantiate();
-	}
-
-	public Object getParent(Object component) {
-		return null;
-	}
-
-	public boolean hasParentProperty() {
-		return false;
-	}
-
-	public boolean isMethodOf(Method method) {
-		return false;
-	}
-
-	public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
-		throw new UnsupportedOperationException();
-	}
-
-	public Getter getGetter(int i) {
-		return getters[i];
-	}
-}
+import java.lang.reflect.Method;
+import java.util.Iterator;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.mapping.Component;
+import org.hibernate.mapping.Property;
+import org.hibernate.property.Getter;
+import org.hibernate.property.Setter;
+import org.hibernate.tuple.Instantiator;
+
+/**
+ * Support for tuplizers relating to components.
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public abstract class AbstractComponentTuplizer implements ComponentTuplizer {
+	protected final Getter[] getters;
+	protected final Setter[] setters;
+	protected final int propertySpan;
+	protected final Instantiator instantiator;
+	protected final boolean hasCustomAccessors;
+
+	protected abstract Instantiator buildInstantiator(Component component);
+	protected abstract Getter buildGetter(Component component, Property prop);
+	protected abstract Setter buildSetter(Component component, Property prop);
+
+	protected AbstractComponentTuplizer(Component component) {
+		propertySpan = component.getPropertySpan();
+		getters = new Getter[propertySpan];
+		setters = new Setter[propertySpan];
+
+		Iterator iter = component.getPropertyIterator();
+		boolean foundCustomAccessor=false;
+		int i = 0;
+		while ( iter.hasNext() ) {
+			Property prop = ( Property ) iter.next();
+			getters[i] = buildGetter( component, prop );
+			setters[i] = buildSetter( component, prop );
+			if ( !prop.isBasicPropertyAccessor() ) {
+				foundCustomAccessor = true;
+			}
+			i++;
+		}
+		hasCustomAccessors = foundCustomAccessor;
+		instantiator = buildInstantiator( component );
+	}
+
+	public Object getPropertyValue(Object component, int i) throws HibernateException {
+		return getters[i].get( component );
+	}
+
+	public Object[] getPropertyValues(Object component) throws HibernateException {
+		Object[] values = new Object[propertySpan];
+		for ( int i = 0; i < propertySpan; i++ ) {
+			values[i] = getPropertyValue( component, i );
+		}
+		return values;
+	}
+
+	public boolean isInstance(Object object) {
+		return instantiator.isInstance(object);
+	}
+
+	public void setPropertyValues(Object component, Object[] values) throws HibernateException {
+		for ( int i = 0; i < propertySpan; i++ ) {
+			setters[i].set( component, values[i], null );
+		}
+	}
+
+	/**
+	* This method does not populate the component parent
+	*/
+	public Object instantiate() throws HibernateException {
+		return instantiator.instantiate();
+	}
+
+	public Object getParent(Object component) {
+		return null;
+	}
+
+	public boolean hasParentProperty() {
+		return false;
+	}
+
+	public boolean isMethodOf(Method method) {
+		return false;
+	}
+
+	public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
+		throw new UnsupportedOperationException();
+	}
+
+	public Getter getGetter(int i) {
+		return getters[i];
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.io.Serializable;
-import java.lang.reflect.Method;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.tuple.Tuplizer;
-
-/**
- * Defines further responsibilities regarding tuplization based on
- * a mapped components.
- * </p>
- * ComponentTuplizer implementations should have the following constructor signature:
- *      (org.hibernate.mapping.Component)
- * 
- * @author Gavin King
- * @author Steve Ebersole
- */
-public interface ComponentTuplizer extends Tuplizer, Serializable {
-	/**
-	 * Retreive the current value of the parent property.
-	 *
-	 * @param component The component instance from which to extract the parent
-	 * property value.
-	 * @return The current value of the parent property.
-	 */
-	public Object getParent(Object component);
-
-    /**
-     * Set the value of the parent property.
-     *
-     * @param component The component instance on which to set the parent.
-     * @param parent The parent to be set on the comonent.
-     * @param factory The current session factory.
-     */
-	public void setParent(Object component, Object parent, SessionFactoryImplementor factory);
-
-	/**
-	 * Does the component managed by this tuuplizer contain a parent property?
-	 *
-	 * @return True if the component does contain a parent property; false otherwise.
-	 */
-	public boolean hasParentProperty();
-
-	/**
-	 * Is the given method available via the managed component as a property getter?
-	 *
-	 * @param method The method which to check against the managed component.
-	 * @return True if the managed component is available from the managed component; else false.
-	 */
-	public boolean isMethodOf(Method method);
-}
+import java.io.Serializable;
+import java.lang.reflect.Method;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.tuple.Tuplizer;
+
+/**
+ * Defines further responsibilities regarding tuplization based on
+ * a mapped components.
+ * </p>
+ * ComponentTuplizer implementations should have the following constructor signature:
+ *      (org.hibernate.mapping.Component)
+ * 
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public interface ComponentTuplizer extends Tuplizer, Serializable {
+	/**
+	 * Retreive the current value of the parent property.
+	 *
+	 * @param component The component instance from which to extract the parent
+	 * property value.
+	 * @return The current value of the parent property.
+	 */
+	public Object getParent(Object component);
+
+    /**
+     * Set the value of the parent property.
+     *
+     * @param component The component instance on which to set the parent.
+     * @param parent The parent to be set on the comonent.
+     * @param factory The current session factory.
+     */
+	public void setParent(Object component, Object parent, SessionFactoryImplementor factory);
+
+	/**
+	 * Does the component managed by this tuuplizer contain a parent property?
+	 *
+	 * @return True if the component does contain a parent property; false otherwise.
+	 */
+	public boolean hasParentProperty();
+
+	/**
+	 * Is the given method available via the managed component as a property getter?
+	 *
+	 * @param method The method which to check against the managed component.
+	 * @return True if the managed component is available from the managed component; else false.
+	 */
+	public boolean isMethodOf(Method method);
+}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.ValueInclusion;
-import org.hibernate.engine.Versioning;
+import org.hibernate.engine.spi.ValueInclusion;
+import org.hibernate.engine.internal.Versioning;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-	 * todo : look at fully encapsulating {@link org.hibernate.engine.PersistenceContext#narrowProxy} here,
+	 * todo : look at fully encapsulating {@link org.hibernate.engine.spi.PersistenceContext#narrowProxy} here,
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.InputStream;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Comparator;
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
-import org.hibernate.cfg.Environment;
-import org.hibernate.engine.SessionImplementor;
-
-/**
- * Logic to bind stream of byte into a VARBINARY
- *
- * @author Gavin King
- * @author Emmanuel Bernard
- *
- * @deprecated Use the {@link AbstractStandardBasicType} approach instead
- */
-public abstract class AbstractBynaryType extends MutableType implements VersionType, Comparator {
-
-	/**
-	 * Convert the byte[] into the expected object type
-	 */
-	abstract protected Object toExternalFormat(byte[] bytes);
-
-	/**
-	 * Convert the object into the internal byte[] representation
-	 */
-	abstract protected byte[] toInternalFormat(Object bytes);
-
-	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
-		byte[] internalValue = toInternalFormat( value );
-		if ( Environment.useStreamsForBinary() ) {
-			st.setBinaryStream( index, new ByteArrayInputStream( internalValue ), internalValue.length );
-		}
-		else {
-			st.setBytes( index, internalValue );
-		}
-	}
-
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
-
-		if ( Environment.useStreamsForBinary() ) {
-
-			InputStream inputStream = rs.getBinaryStream(name);
-
-			if (inputStream==null) return toExternalFormat( null ); // is this really necessary?
-
-			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
-			byte[] buffer = new byte[2048];
-
-			try {
-				while (true) {
-					int amountRead = inputStream.read(buffer);
-					if (amountRead == -1) {
-						break;
-					}
-					outputStream.write(buffer, 0, amountRead);
-				}
-
-				inputStream.close();
-				outputStream.close();
-			}
-			catch (IOException ioe) {
-				throw new HibernateException( "IOException occurred reading a binary value", ioe );
-			}
-
-			return toExternalFormat( outputStream.toByteArray() );
-
-		}
-		else {
-			return toExternalFormat( rs.getBytes(name) );
-		}
-	}
-
-	public int sqlType() {
-		return Types.VARBINARY;
-	}
-
-	// VersionType impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	//      Note : simply returns null for seed() and next() as the only known
-	//      application of binary types for versioning is for use with the
-	//      TIMESTAMP datatype supported by Sybase and SQL Server, which
-	//      are completely db-generated values...
-	public Object seed(SessionImplementor session) {
-		return null;
-	}
-
-	public Object next(Object current, SessionImplementor session) {
-		return current;
-	}
-
-	public Comparator getComparator() {
-		return this;
-	}
-
-	public int compare(Object o1, Object o2) {
-		return compare( o1, o2, null );
-	}
-	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean isEqual(Object x, Object y) {
-		return x==y || ( x!=null && y!=null && java.util.Arrays.equals( toInternalFormat(x), toInternalFormat(y) ) );
-	}
-
-	public int getHashCode(Object x, EntityMode entityMode) {
-		byte[] bytes = toInternalFormat(x);
-		int hashCode = 1;
-		for ( int j=0; j<bytes.length; j++ ) {
-			hashCode = 31 * hashCode + bytes[j];
-		}
-		return hashCode;
-	}
-
-	public int compare(Object x, Object y, EntityMode entityMode) {
-		byte[] xbytes = toInternalFormat(x);
-		byte[] ybytes = toInternalFormat(y);
-		if ( xbytes.length < ybytes.length ) return -1;
-		if ( xbytes.length > ybytes.length ) return 1;
-		for ( int i=0; i<xbytes.length; i++ ) {
-			if ( xbytes[i] < ybytes[i] ) return -1;
-			if ( xbytes[i] > ybytes[i] ) return 1;
-		}
-		return 0;
-	}
-
-	public abstract String getName();
-
-	public String toString(Object val) {
-		byte[] bytes = toInternalFormat(val);
-		StringBuffer buf = new StringBuffer();
-		for ( int i=0; i<bytes.length; i++ ) {
-			String hexStr = Integer.toHexString( bytes[i] - Byte.MIN_VALUE );
-			if ( hexStr.length()==1 ) buf.append('0');
-			buf.append(hexStr);
-		}
-		return buf.toString();
-	}
-
-	public Object deepCopyNotNull(Object value) {
-		byte[] bytes = toInternalFormat(value);
-		byte[] result = new byte[bytes.length];
-		System.arraycopy(bytes, 0, result, 0, bytes.length);
-		return toExternalFormat(result);
-	}
-
-	public Object fromStringValue(String xml) throws HibernateException {
-		if (xml == null)
-			return null;
-		if (xml.length() % 2 != 0)
-			throw new IllegalArgumentException("The string is not a valid xml representation of a binary content.");
-		byte[] bytes = new byte[xml.length() / 2];
-		for (int i = 0; i < bytes.length; i++) {
-			String hexStr = xml.substring(i * 2, (i + 1) * 2);
-			bytes[i] = (byte) (Integer.parseInt(hexStr, 16) + Byte.MIN_VALUE);
-		}
-		return toExternalFormat(bytes);
-	}
-
-}
+import java.io.ByteArrayInputStream;
+import java.io.ByteArrayOutputStream;
+import java.io.IOException;
+import java.io.InputStream;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Comparator;
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.Environment;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Logic to bind stream of byte into a VARBINARY
+ *
+ * @author Gavin King
+ * @author Emmanuel Bernard
+ *
+ * @deprecated Use the {@link AbstractStandardBasicType} approach instead
+ */
+public abstract class AbstractBynaryType extends MutableType implements VersionType, Comparator {
+
+	/**
+	 * Convert the byte[] into the expected object type
+	 */
+	abstract protected Object toExternalFormat(byte[] bytes);
+
+	/**
+	 * Convert the object into the internal byte[] representation
+	 */
+	abstract protected byte[] toInternalFormat(Object bytes);
+
+	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
+		byte[] internalValue = toInternalFormat( value );
+		if ( Environment.useStreamsForBinary() ) {
+			st.setBinaryStream( index, new ByteArrayInputStream( internalValue ), internalValue.length );
+		}
+		else {
+			st.setBytes( index, internalValue );
+		}
+	}
+
+	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
+
+		if ( Environment.useStreamsForBinary() ) {
+
+			InputStream inputStream = rs.getBinaryStream(name);
+
+			if (inputStream==null) return toExternalFormat( null ); // is this really necessary?
+
+			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
+			byte[] buffer = new byte[2048];
+
+			try {
+				while (true) {
+					int amountRead = inputStream.read(buffer);
+					if (amountRead == -1) {
+						break;
+					}
+					outputStream.write(buffer, 0, amountRead);
+				}
+
+				inputStream.close();
+				outputStream.close();
+			}
+			catch (IOException ioe) {
+				throw new HibernateException( "IOException occurred reading a binary value", ioe );
+			}
+
+			return toExternalFormat( outputStream.toByteArray() );
+
+		}
+		else {
+			return toExternalFormat( rs.getBytes(name) );
+		}
+	}
+
+	public int sqlType() {
+		return Types.VARBINARY;
+	}
+
+	// VersionType impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	//      Note : simply returns null for seed() and next() as the only known
+	//      application of binary types for versioning is for use with the
+	//      TIMESTAMP datatype supported by Sybase and SQL Server, which
+	//      are completely db-generated values...
+	public Object seed(SessionImplementor session) {
+		return null;
+	}
+
+	public Object next(Object current, SessionImplementor session) {
+		return current;
+	}
+
+	public Comparator getComparator() {
+		return this;
+	}
+
+	public int compare(Object o1, Object o2) {
+		return compare( o1, o2, null );
+	}
+	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean isEqual(Object x, Object y) {
+		return x==y || ( x!=null && y!=null && java.util.Arrays.equals( toInternalFormat(x), toInternalFormat(y) ) );
+	}
+
+	public int getHashCode(Object x, EntityMode entityMode) {
+		byte[] bytes = toInternalFormat(x);
+		int hashCode = 1;
+		for ( int j=0; j<bytes.length; j++ ) {
+			hashCode = 31 * hashCode + bytes[j];
+		}
+		return hashCode;
+	}
+
+	public int compare(Object x, Object y, EntityMode entityMode) {
+		byte[] xbytes = toInternalFormat(x);
+		byte[] ybytes = toInternalFormat(y);
+		if ( xbytes.length < ybytes.length ) return -1;
+		if ( xbytes.length > ybytes.length ) return 1;
+		for ( int i=0; i<xbytes.length; i++ ) {
+			if ( xbytes[i] < ybytes[i] ) return -1;
+			if ( xbytes[i] > ybytes[i] ) return 1;
+		}
+		return 0;
+	}
+
+	public abstract String getName();
+
+	public String toString(Object val) {
+		byte[] bytes = toInternalFormat(val);
+		StringBuffer buf = new StringBuffer();
+		for ( int i=0; i<bytes.length; i++ ) {
+			String hexStr = Integer.toHexString( bytes[i] - Byte.MIN_VALUE );
+			if ( hexStr.length()==1 ) buf.append('0');
+			buf.append(hexStr);
+		}
+		return buf.toString();
+	}
+
+	public Object deepCopyNotNull(Object value) {
+		byte[] bytes = toInternalFormat(value);
+		byte[] result = new byte[bytes.length];
+		System.arraycopy(bytes, 0, result, 0, bytes.length);
+		return toExternalFormat(result);
+	}
+
+	public Object fromStringValue(String xml) throws HibernateException {
+		if (xml == null)
+			return null;
+		if (xml.length() % 2 != 0)
+			throw new IllegalArgumentException("The string is not a valid xml representation of a binary content.");
+		byte[] bytes = new byte[xml.length() / 2];
+		for (int i = 0; i < bytes.length; i++) {
+			String hexStr = xml.substring(i * 2, (i + 1) * 2);
+			bytes[i] = (byte) (Integer.parseInt(hexStr, 16) + Byte.MIN_VALUE);
+		}
+		return toExternalFormat(bytes);
+	}
+
+}
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
-import java.sql.ResultSet;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.jdbc.NonContextualLobCreator;
-import org.hibernate.type.descriptor.WrapperOptions;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-			Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( 
-					entityName, 
-					original, 
-					session 
-				);
+			Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
+					entityName,
+					original,
+					session
+			);
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
- * @see org.hibernate.engine.Cascade
+ * @see org.hibernate.engine.internal.Cascade
--- a/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ClobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ClobType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
-import org.hibernate.engine.CollectionKey;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CollectionKey;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
+	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-import org.hibernate.engine.EntityUniqueKey;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
-import org.hibernate.engine.Cascade;
+import org.hibernate.engine.internal.Cascade;
-	 * @see org.hibernate.engine.Cascade
+	 * @see org.hibernate.engine.internal.Cascade
-			return cascadePoint!=Cascade.AFTER_INSERT_BEFORE_DELETE;
+			return cascadePoint!= Cascade.AFTER_INSERT_BEFORE_DELETE;
--- a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/LongType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/LongType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionImplementor;
-			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved( 
-					getAssociatedEntityName(), 
-					value, 
+			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
+					getAssociatedEntityName(),
+					value,
--- a/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/MutableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MutableType.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
+	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-	 * A convenience form of {@link #sqlTypes(org.hibernate.engine.Mapping)}, returning
+	 * A convenience form of {@link #sqlTypes(org.hibernate.engine.spi.Mapping)}, returning
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/VersionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/VersionType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/type/XmlRepresentableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/XmlRepresentableType.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/usertype/CompositeUserType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/CompositeUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/usertype/LoggableUserType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/LoggableUserType.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import org.hibernate.engine.SessionFactoryImplementor;
-
-/**
- * Marker interface for user types which want to perform custom
- * logging of their corresponding values
- *
- * @author Steve Ebersole
- */
-public interface LoggableUserType {
-	/**
-	 * Generate a loggable string representation of the collection (value).
-	 *
-	 * @param value The collection to be logged; guarenteed to be non-null and initialized.
-	 * @param factory The factory.
-	 * @return The loggable string representation.
-	 */
-	public String toLoggableString(Object value, SessionFactoryImplementor factory);
-}
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+
+/**
+ * Marker interface for user types which want to perform custom
+ * logging of their corresponding values
+ *
+ * @author Steve Ebersole
+ */
+public interface LoggableUserType {
+	/**
+	 * Generate a loggable string representation of the collection (value).
+	 *
+	 * @param value The collection to be logged; guarenteed to be non-null and initialized.
+	 * @param factory The factory.
+	 * @return The loggable string representation.
+	 */
+	public String toLoggableString(Object value, SessionFactoryImplementor factory);
+}
--- a/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.metamodel.relational.Size;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/main/java/org/hibernate/usertype/UserVersionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/UserVersionType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java
+import org.hibernate.engine.query.spi.ParameterParser;
+
-		assertFalse( ParameterParser.startsWithEscapeCallTemplate( "from User u where u.userName = ? and u.userType = 'call'" ) );
+		assertFalse( ParameterParser.startsWithEscapeCallTemplate(
+				"from User u where u.userName = ? and u.userType = 'call'"
+		) );
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/CasterStringType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/CasterStringType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/PhoneNumberType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/PhoneNumberType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/generics/StateType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/generics/StateType.java
-//$Id$
+//$Id$
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.usertype.UserType;
-
-/**
- * @author Emmanuel Bernard
- */
-public class StateType implements UserType {
-	public int[] sqlTypes() {
-		return new int[] {
-			Types.INTEGER
-		};
-	}
-
-	public Class returnedClass() {
-		return State.class;
-	}
-
-	public boolean equals(Object x, Object y) throws HibernateException {
-		return x == y;
-	}
-
-	public int hashCode(Object x) throws HibernateException {
-		return x.hashCode();
-	}
-
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
-		int result = rs.getInt( names[0] );
-		if ( rs.wasNull() ) return null;
-		return State.values()[result];
-	}
-
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
-		if (value == null) {
-			st.setNull( index, Types.INTEGER );
-		}
-		else {
-			st.setInt( index, ( (State) value ).ordinal() );
-		}
-	}
-
-	public Object deepCopy(Object value) throws HibernateException {
-		return value;
-	}
-
-	public boolean isMutable() {
-		return false;
-	}
-
-	public Serializable disassemble(Object value) throws HibernateException {
-		return (Serializable) value;
-	}
-
-	public Object assemble(Serializable cached, Object owner) throws HibernateException {
-		return cached;
-	}
-
-	public Object replace(Object original, Object target, Object owner) throws HibernateException {
-		return original;
-	}
-}
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.usertype.UserType;
+
+/**
+ * @author Emmanuel Bernard
+ */
+public class StateType implements UserType {
+	public int[] sqlTypes() {
+		return new int[] {
+			Types.INTEGER
+		};
+	}
+
+	public Class returnedClass() {
+		return State.class;
+	}
+
+	public boolean equals(Object x, Object y) throws HibernateException {
+		return x == y;
+	}
+
+	public int hashCode(Object x) throws HibernateException {
+		return x.hashCode();
+	}
+
+	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+		int result = rs.getInt( names[0] );
+		if ( rs.wasNull() ) return null;
+		return State.values()[result];
+	}
+
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+		if (value == null) {
+			st.setNull( index, Types.INTEGER );
+		}
+		else {
+			st.setInt( index, ( (State) value ).ordinal() );
+		}
+	}
+
+	public Object deepCopy(Object value) throws HibernateException {
+		return value;
+	}
+
+	public boolean isMutable() {
+		return false;
+	}
+
+	public Serializable disassemble(Object value) throws HibernateException {
+		return (Serializable) value;
+	}
+
+	public Object assemble(Serializable cached, Object owner) throws HibernateException {
+		return cached;
+	}
+
+	public Object replace(Object original, Object target, Object owner) throws HibernateException {
+		return original;
+	}
+}
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/UUIDGenerator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/UUIDGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/UUIDGenerator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/UUIDGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/persister/CollectionPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/persister/CollectionPersister.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/persister/EntityPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/persister/EntityPersister.java
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidGenerator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/batch/BatchTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/batch/BatchTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.ValueInclusion;
-								   org.hibernate.engine.SessionFactoryImplementor sf,
-								   org.hibernate.engine.Mapping mapping) {
+								   SessionFactoryImplementor sf,
+								   Mapping mapping) {
-									   org.hibernate.engine.SessionFactoryImplementor sf) {
+									   SessionFactoryImplementor sf) {
--- a/hibernate-core/src/test/java/org/hibernate/test/cid/PurchaseRecordIdGenerator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cid/PurchaseRecordIdGenerator.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/connections/ThreadLocalCurrentSessionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/connections/ThreadLocalCurrentSessionTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/accessors/Dom4jAccessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/accessors/Dom4jAccessorTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/fetchprofiles/join/JoinFetchProfileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/fetchprofiles/join/JoinFetchProfileTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNameAsColumnTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/FunctionNameAsColumnTest.java
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.testing.DialectCheck;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.ReturnMetadata;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.query.spi.ReturnMetadata;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/QueryTranslatorTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/QueryTranslatorTestCase.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.HQLQueryPlan;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
-import org.hibernate.Hibernate;
+
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/txn/TransactionJoiningTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/txn/TransactionJoiningTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TwoPhaseLoad;
-import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.internal.TwoPhaseLoad;
+import org.hibernate.engine.spi.ValueInclusion;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/DoubleStringType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/DoubleStringType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
-import org.hibernate.Hibernate;
+
-import org.hibernate.engine.ForeignKeys;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.internal.ForeignKeys;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
-import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.spi.EntityEntry;
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/AbstractOperationTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/AbstractOperationTestCase.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.NonFlushedChanges;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.StatefulPersistenceContext;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.NonFlushedChanges;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.internal.StatefulPersistenceContext;
--- a/hibernate-core/src/test/java/org/hibernate/test/queryplan/GetHqlQueryPlanTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/queryplan/GetHqlQueryPlanTest.java
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.QueryPlanCache;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.query.spi.QueryPlanCache;
+import org.hibernate.engine.spi.SessionImplementor;
-		QueryPlanCache cache = ( ( SessionImplementor ) s ).getFactory().getQueryPlanCache();
+		QueryPlanCache cache = ( (SessionImplementor) s ).getFactory().getQueryPlanCache();
--- a/hibernate-core/src/test/java/org/hibernate/test/queryplan/NativeSQLQueryPlanEqualsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/queryplan/NativeSQLQueryPlanEqualsTest.java
-import org.hibernate.engine.query.NativeSQLQueryPlan;
-import org.hibernate.engine.query.QueryPlanCache;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
+import org.hibernate.engine.query.spi.QueryPlanCache;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-core/src/test/java/org/hibernate/test/queryplan/NativeSQLQueryReturnEqualsAndHashCodeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/queryplan/NativeSQLQueryReturnEqualsAndHashCodeTest.java
-import org.hibernate.engine.query.sql.NativeSQLQueryCollectionReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
--- a/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlyProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlyProxyTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-		assertTrue( ( ( SessionImplementor ) s ).isClosed() );
+		assertTrue( ( (SessionImplementor) s ).isClosed() );
--- a/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlySessionLazyNonLazyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlySessionLazyNonLazyTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-		SessionImplementor si = ( SessionImplementor ) s;
+		SessionImplementor si = (SessionImplementor) s;
--- a/hibernate-core/src/test/java/org/hibernate/test/rowid/RowIdType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/rowid/RowIdType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/sql/check/ExceptionCheckingEntity.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/check/ExceptionCheckingEntity.java
-
-
-/**
- * An entity which is expected to be mapped to each database using stored
- * procedures which throw exceptions on their own; in other words, using
- * {@link org.hibernate.engine.ExecuteUpdateResultCheckStyle#NONE}.
- *
- * @author Steve Ebersole
- */
-public class ExceptionCheckingEntity {
-	private Long id;
-	private String name;
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-}
-
+
+
+/**
+ * An entity which is expected to be mapped to each database using stored
+ * procedures which throw exceptions on their own; in other words, using
+ * {@link org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle#NONE}.
+ *
+ * @author Steve Ebersole
+ */
+public class ExceptionCheckingEntity {
+	private Long id;
+	private String name;
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
+
--- a/hibernate-core/src/test/java/org/hibernate/test/sql/check/ParamCheckingEntity.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/check/ParamCheckingEntity.java
-
-
-/**
- * An entity which is expected to be mapped to each database using stored
- * procedures which return "affected row counts"; in other words, using
- * {@link org.hibernate.engine.ExecuteUpdateResultCheckStyle#PARAM}.
- *
- * @author Steve Ebersole
- */
-public class ParamCheckingEntity {
-	private Long id;
-	private String name;
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-}
+
+
+/**
+ * An entity which is expected to be mapped to each database using stored
+ * procedures which return "affected row counts"; in other words, using
+ * {@link org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle#PARAM}.
+ *
+ * @author Steve Ebersole
+ */
+public class ParamCheckingEntity {
+	private Long id;
+	private String name;
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
--- a/hibernate-core/src/test/java/org/hibernate/test/sql/hand/MonetaryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/hand/MonetaryAmountUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/typeparameters/DefaultValueIntegerType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/typeparameters/DefaultValueIntegerType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+
--- a/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
-import org.hibernate.engine.NamedSQLQueryDefinition;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.query.HQLQueryPlan;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
-		return ( SessionFactoryImplementor ) getRawSession().getSessionFactory();
+		return (SessionFactoryImplementor) getRawSession().getSessionFactory();
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
-import org.hibernate.engine.FilterDefinition;
+import org.hibernate.engine.spi.FilterDefinition;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.NamedParameterDescriptor;
-import org.hibernate.engine.query.OrdinalParameterDescriptor;
+import org.hibernate.engine.query.spi.NamedParameterDescriptor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/MapKeyHelpers.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/MapKeyHelpers.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/PluralAttributePath.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/PluralAttributePath.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
-import org.hibernate.engine.CascadingAction;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.Status;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
-import org.hibernate.engine.CascadingAction;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java
-import org.hibernate.engine.CascadingAction;
-import org.hibernate.engine.EJB3CascadeStyle;
-import org.hibernate.engine.EJB3CascadingAction;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.EJB3CascadeStyle;
+import org.hibernate.engine.spi.EJB3CascadingAction;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java
-import org.hibernate.engine.CascadingAction;
+import org.hibernate.engine.spi.CascadingAction;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.Status;
+import org.hibernate.engine.spi.EntityEntry;
+import org.hibernate.engine.spi.Status;
-		if (Status.DELETED != entry.getStatus()) {
+		if ( Status.DELETED != entry.getStatus()) {
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaIntegrator.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaIntegrator.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetadataContext.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetadataContext.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetamodelImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetamodelImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/engine/EJB3CascadeStyle.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/EJB3CascadeStyle.java
-package org.hibernate.engine;
-
+package org.hibernate.engine.spi;
-			return action==EJB3CascadingAction.PERSIST_SKIPLAZY
+			return action== EJB3CascadingAction.PERSIST_SKIPLAZY
--- a/hibernate-entitymanager/src/main/java/org/hibernate/engine/EJB3CascadingAction.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/engine/spi/EJB3CascadingAction.java
-package org.hibernate.engine;
+package org.hibernate.engine.spi;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.ValueInclusion;
-								   org.hibernate.engine.SessionFactoryImplementor sf,
-								   org.hibernate.engine.Mapping mapping) {
+								   SessionFactoryImplementor sf,
+								   Mapping mapping) {
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/metadata/MetadataTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/metadata/MetadataTest.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/transaction/TransactionJoiningTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/transaction/TransactionJoiningTest.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/RevisionTypeType.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/RevisionTypeType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ExtendedPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ExtendedPropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.LoadQueryInfluencers;
-import org.hibernate.engine.NonFlushedChanges;
-import org.hibernate.engine.PersistenceContext;
-import org.hibernate.engine.QueryParameters;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.NonFlushedChanges;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
-import org.hibernate.engine.CollectionEntry;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversEventListener.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversEventListener.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.SessionFactoryImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostCollectionRecreateEventListenerImpl.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostCollectionRecreateEventListenerImpl.java
-import org.hibernate.engine.CollectionEntry;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionRemoveEventListenerImpl.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionRemoveEventListenerImpl.java
-import org.hibernate.engine.CollectionEntry;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionUpdateEventListenerImpl.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionUpdateEventListenerImpl.java
-import org.hibernate.engine.CollectionEntry;
+import org.hibernate.engine.spi.CollectionEntry;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/reader/AuditReaderImpl.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/reader/AuditReaderImpl.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
- * @author Hern&aacute;n Chanfreau
+ * @author Hern&aacute;n Chanfreau
-    	return this.isEntityNameAudited(entityClass.getName());
-    }
-
-
-	public boolean isEntityNameAudited(String entityName) {
-        checkNotNull(entityName, "Entity name");
-        checkSession();
-        return (verCfg.getEntCfg().isVersioned(entityName));
-    }	
-
-
-	public String getEntityName(Object primaryKey, Number revision ,Object entity) throws HibernateException{
-        checkNotNull(primaryKey, "Primary key");
-        checkNotNull(revision, "Entity revision");
-        checkPositive(revision, "Entity revision");
-        checkNotNull(entity, "Entity");
+    	return this.isEntityNameAudited(entityClass.getName());
+    }
+
+
+	public boolean isEntityNameAudited(String entityName) {
+        checkNotNull(entityName, "Entity name");
+        checkSession();
+        return (verCfg.getEntCfg().isVersioned(entityName));
+    }	
+
+
+	public String getEntityName(Object primaryKey, Number revision ,Object entity) throws HibernateException{
+        checkNotNull(primaryKey, "Primary key");
+        checkNotNull(revision, "Entity revision");
+        checkPositive(revision, "Entity revision");
+        checkNotNull(entity, "Entity");
-		// Unwrap if necessary
-		if(entity instanceof HibernateProxy) {
-			entity = ((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
-		}
-		if(firstLevelCache.containsEntityName(primaryKey, revision, entity)) {
-			// it's on envers FLC!
-			return firstLevelCache.getFromEntityNameCache(primaryKey, revision, entity);
-		} else {
-			throw new HibernateException(
-						"Envers can't resolve entityName for historic entity. The id, revision and entity is not on envers first level cache.");
+		// Unwrap if necessary
+		if(entity instanceof HibernateProxy) {
+			entity = ((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
+		}
+		if(firstLevelCache.containsEntityName(primaryKey, revision, entity)) {
+			// it's on envers FLC!
+			return firstLevelCache.getFromEntityNameCache(primaryKey, revision, entity);
+		} else {
+			throw new HibernateException(
+						"Envers can't resolve entityName for historic entity. The id, revision and entity is not on envers first level cache.");
--- a/hibernate-envers/src/main/java/org/hibernate/envers/reader/AuditReaderImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/reader/AuditReaderImplementor.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/AuditProcess.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/AuditProcess.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/AuditProcessManager.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/AuditProcessManager.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/AbstractAuditWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/AbstractAuditWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/AddWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/AddWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/CollectionChangeWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/CollectionChangeWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/DelWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/DelWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/FakeBidirectionalRelationWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/FakeBidirectionalRelationWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/ModWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/ModWorkUnit.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java
-import org.hibernate.engine.CollectionEntry;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.CollectionEntry;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/tools/Tools.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/tools/Tools.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/customtype/CompositeTestUserType.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/customtype/CompositeTestUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/customtype/ParametrizedTestUserType.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/customtype/ParametrizedTestUserType.java
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/ids/CustomEnumUserType.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/entities/ids/CustomEnumUserType.java
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.type.StringType;
+import org.hibernate.engine.spi.SessionImplementor;
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;

Lines added containing method: 4599. Lines removed containing method: 4548. Tot = 9147
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
74/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-core/src/main/java/org/hibernate/EntityMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/EntityMode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-	POJO("pojo"),
-	DOM4J("dom4j"),
-	MAP("dynamic-map");
+	POJO( "pojo" ),
+	MAP( "dynamic-map" );
+
+
+	private static final String DYNAMIC_MAP_NAME = MAP.name.toUpperCase();
+
+	/**
+	 * Legacy-style entity-mode name parsing.  <b>Case insensitive</b>
+	 *
+	 * @param entityMode The entity mode name to evaluate
+	 *
+	 * @return The appropriate entity mode; {@code null} for incoming {@code entityMode} param is treated by returning
+	 * {@link #POJO}.
+	 */
-		if(entityMode == null){
+		if ( entityMode == null ) {
-		if ( entityMode.equals( "DYNAMIC-MAP" ) ) {
+		if ( DYNAMIC_MAP_NAME.equals( entityMode ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
-	 * Retrieve the entity mode in effect for this session.
-	 *
-	 * @return The entity mode for this session.
-	 */
-	public EntityMode getEntityMode();
-
-	/**
-	 * Starts a new Session with the given entity mode in effect. This secondary
-	 * Session inherits the connection, transaction, and other context
-	 * information from the primary Session. It doesn't need to be flushed
-	 * or closed by the developer.
-	 * 
-	 * @param entityMode The entity mode to use for the new session.
-	 * @return The new session
-	 * @deprecated
-	 */
-	@Deprecated
-	public Session getSession(EntityMode entityMode);
-
-	/**
--- a/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java
-	 * Use a specific entity mode for these session options
-	 *
-	 * @param entityMode The entity mode to use.
-	 *
-	 * @return {@code this}, for method chaining
-	 */
-	public SessionBuilder entityMode(EntityMode entityMode);
-
-	/**
--- a/hibernate-core/src/main/java/org/hibernate/SharedSessionBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/SharedSessionBuilder.java
-	 * Signifies that the entity mode from the original session should be used to create the new session
-	 *
-	 * @return {@code this}, for method chaining
-	 */
-	public SharedSessionBuilder entityMode();
-
-	/**
-	SharedSessionBuilder entityMode(EntityMode entityMode);
-
-	@Override
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
-					.compare( key, action.key, session.getEntityMode() );
+					.compare( key, action.key );
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
-			return persister.getIdentifierType().compare( id, action.id, session.getEntityMode() );
+			return persister.getIdentifierType().compare( id, action.id );
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
-			version = persister.getVersion( instance, session.getEntityMode() );
+			version = persister.getVersion( instance );
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
-					persister.hasUninitializedLazyProperties( instance, session.getEntityMode() ),
+					persister.hasUninitializedLazyProperties( instance ),
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
-			previousVersion = persister.getVersion( instance, session.getEntityMode() );
+			previousVersion = persister.getVersion( instance );
-						persister.hasUninitializedLazyProperties( instance, session.getEntityMode() ), 
+						persister.hasUninitializedLazyProperties( instance ),
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
-import org.hibernate.EntityMode;
-	private final EntityMode entityMode;
-	 * @param entityMode The entity mode of the originating session
-			final EntityMode entityMode,
-		this.entityMode = entityMode;
-		this.hashCode = type.getHashCode( key, entityMode, factory );
+		this.hashCode = type.getHashCode( key, factory );
-				type.isEqual( key, that.key, entityMode ) &&
+				type.isEqual( key, that.key ) &&
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/FilterKey.java
-import java.util.Iterator;
-import org.hibernate.EntityMode;
+import org.hibernate.Filter;
-	private Map filterParameters = new HashMap();
+	private Map<String,TypedValue> filterParameters = new HashMap<String,TypedValue>();
-	public FilterKey(String name, Map params, Map types, EntityMode entityMode) {
+	public FilterKey(String name, Map<String,?> params, Map<String,Type> types) {
-		Iterator iter = params.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			Type type = (Type) types.get( me.getKey() );
-			filterParameters.put( me.getKey(), new TypedValue( type, me.getValue(), entityMode ) );
+		for ( Map.Entry<String, ?> paramEntry : params.entrySet() ) {
+			Type type = types.get( paramEntry.getKey() );
+			filterParameters.put( paramEntry.getKey(), new TypedValue( type, paramEntry.getValue() ) );
-	public static Set createFilterKeys(Map enabledFilters, EntityMode entityMode) {
-		if ( enabledFilters.size()==0 ) return null;
-		Set result = new HashSet();
-		Iterator iter = enabledFilters.values().iterator();
-		while ( iter.hasNext() ) {
-			FilterImpl filter = (FilterImpl) iter.next();
+	public static Set<FilterKey> createFilterKeys(Map<String,Filter> enabledFilters) {
+		if ( enabledFilters.size()==0 ) {
+			return null;
+		}
+		Set<FilterKey> result = new HashSet<FilterKey>();
+		for ( Filter filter : enabledFilters.values() ) {
-					filter.getName(), 
-					filter.getParameters(), 
-					filter.getFilterDefinition().getParameterTypes(), 
-					entityMode
-				);
-			result.add(key);
+					filter.getName(),
+					( (FilterImpl) filter ).getParameters(),
+					filter.getFilterDefinition().getParameterTypes()
+			);
+			result.add( key );
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/QueryKey.java
-import java.util.Iterator;
-import org.hibernate.EntityMode;
-	private final EntityMode entityMode;
-		final Map namedParameters;
+		final Map<String,TypedValue> namedParameters;
-			Iterator itr = queryParameters.getNamedParameters().entrySet().iterator();
-			while ( itr.hasNext() ) {
-				final Map.Entry namedParameterEntry = ( Map.Entry ) itr.next();
-				final TypedValue original = ( TypedValue ) namedParameterEntry.getValue();
+			for ( Map.Entry<String,TypedValue> namedParameterEntry : queryParameters.getNamedParameters().entrySet() ) {
-								original.getType(),
-								original.getType().disassemble( original.getValue(), session, null ),
-								session.getEntityMode()
+								namedParameterEntry.getValue().getType(),
+								namedParameterEntry.getValue().getType().disassemble(
+										namedParameterEntry.getValue().getValue(),
+										session,
+										null
+								)
-				session.getEntityMode(),
-	 * @param entityMode The entity mode.
+	 * @param tenantIdentifier The tenant identifier in effect for this query, or {@code null}
-			EntityMode entityMode,
-		this.entityMode = entityMode;
-			result = 37 * result + ( positionalParameterValues[i]==null ? 0 : positionalParameterTypes[i].getHashCode( positionalParameterValues[i], entityMode ) );
+			result = 37 * result + ( positionalParameterValues[i]==null ? 0 : positionalParameterTypes[i].getHashCode( positionalParameterValues[i] ) );
-				if ( !positionalParameterTypes[i].isEqual( positionalParameterValues[i], that.positionalParameterValues[i], entityMode ) ) {
+				if ( !positionalParameterTypes[i].isEqual( positionalParameterValues[i], that.positionalParameterValues[i] ) ) {
-				&& EqualsHelper.equals( entityMode, that.entityMode )
-		StringBuffer buf = new StringBuffer()
-				.append( "sql: " )
-				.append( sqlQueryString );
+		StringBuilder buffer = new StringBuilder( "sql: " ).append( sqlQueryString );
-			buf.append( "; parameters: " );
-			for ( int i = 0; i < positionalParameterValues.length; i++ ) {
-				buf.append( positionalParameterValues[i] ).append( ", " );
+			buffer.append( "; parameters: " );
+			for ( Object positionalParameterValue : positionalParameterValues ) {
+				buffer.append( positionalParameterValue ).append( ", " );
-			buf.append( "; named parameters: " ).append( namedParameters );
+			buffer.append( "; named parameters: " ).append( namedParameters );
-			buf.append( "; filterKeys: " ).append( filterKeys );
+			buffer.append( "; filterKeys: " ).append( filterKeys );
-			buf.append( "; first row: " ).append( firstRow );
+			buffer.append( "; first row: " ).append( firstRow );
-			buf.append( "; max rows: " ).append( maxRows );
+			buffer.append( "; max rows: " ).append( maxRows );
-			buf.append( "; transformer: " ).append( customTransformer );
+			buffer.append( "; transformer: " ).append( customTransformer );
-		return buf.toString();
+		return buffer.toString();
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
-		persister.setPropertyValues( 
-				result, 
-				assembledProps, 
-				session.getEntityMode() 
-			);
+		persister.setPropertyValues( result, assembledProps );
-		return "CacheEntry(" + subclass + ')' + 
-				ArrayHelper.toString(disassembledState);
+		return "CacheEntry(" + subclass + ')' + ArrayHelper.toString(disassembledState);
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
-		if ( tuplizer != null ) {
-			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
-		}
+//		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
+//		if ( tuplizer != null ) {
+//			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
+//		}
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
-				event.getEntity(), event.getSession().getEntityMode(), event.getPersister(),
+				event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(),
-				event.getEntity(), event.getSession().getEntityMode(), event.getPersister(),
+				event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(),
-				event.getEntity(), event.getSession().getEntityMode(), event.getPersister(),
+				event.getEntity(), event.getPersister().getEntityMode(), event.getPersister(),
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+import org.hibernate.persister.entity.EntityPersister;
-	private transient List operationQueue;
+	private transient List<DelayedOperation> operationQueue;
+
-	 * Called by the <tt>size()</tt> method
+	 * Called by the {@link Collection#size} method
+	@SuppressWarnings( {"JavaDoc"})
-	/**
-	 * Is the collection currently connected to an open session?
-	 */
-	private final boolean isConnectedToSession() {
+	private boolean isConnectedToSession() {
+
+	@SuppressWarnings( {"JavaDoc"})
+	@SuppressWarnings( {"JavaDoc"})
+	@SuppressWarnings( {"JavaDoc"})
+	@SuppressWarnings( {"JavaDoc"})
+	@SuppressWarnings( {"JavaDoc"})
+	@SuppressWarnings( {"JavaDoc"})
-	protected final void queueOperation(Object element) {
-		if (operationQueue==null) operationQueue = new ArrayList(10);
-		operationQueue.add(element);
+	@SuppressWarnings( {"JavaDoc"})
+	protected final void queueOperation(DelayedOperation operation) {
+		if (operationQueue==null) {
+			operationQueue = new ArrayList<DelayedOperation>(10);
+		}
+		operationQueue.add( operation );
-		for ( int i=0; i<operationQueue.size(); i++ ) {
-			( (DelayedOperation) operationQueue.get(i) ).operate();
+		for ( DelayedOperation operation : operationQueue ) {
+			operation.operate();
+	@SuppressWarnings( {"JavaDoc"})
-					return ( (DelayedOperation) operationQueue.get(i++) ).getAddedInstance();
+					return operationQueue.get(i++).getAddedInstance();
+	@SuppressWarnings( {"unchecked"})
-			for ( int i = 0; i < operationQueue.size(); i++ ) {
-				DelayedOperation op = (DelayedOperation) operationQueue.get(i);
-				additions.add( op.getAddedInstance() );
-				removals.add( op.getOrphan() );
+			for ( DelayedOperation operation : operationQueue ) {
+				additions.add( operation.getAddedInstance() );
+				removals.add( operation.getOrphan() );
-			return getOrphans(removals, additions, entityName, session);
+			return getOrphans( removals, additions, entityName, session );
+	@SuppressWarnings( {"JavaDoc"})
-		protected final Iterator iter;
+		protected final Iterator itr;
-		public IteratorProxy(Iterator iter) {
-			this.iter=iter;
+		public IteratorProxy(Iterator itr) {
+			this.itr = itr;
-			return iter.hasNext();
+			return itr.hasNext();
-			return iter.next();
+			return itr.next();
-			iter.remove();
+			itr.remove();
-		protected final ListIterator iter;
+		protected final ListIterator itr;
-		public ListIteratorProxy(ListIterator iter) {
-			this.iter = iter;
+		public ListIteratorProxy(ListIterator itr) {
+			this.itr = itr;
+
+		@SuppressWarnings( {"unchecked"})
-			iter.add(o);
+			itr.add(o);
-			return iter.hasNext();
+			return itr.hasNext();
-			return iter.hasPrevious();
+			return itr.hasPrevious();
-			return iter.next();
+			return itr.next();
-			return iter.nextIndex();
+			return itr.nextIndex();
-			return iter.previous();
+			return itr.previous();
-			return iter.previousIndex();
+			return itr.previousIndex();
-			iter.remove();
+			itr.remove();
+		@SuppressWarnings( {"unchecked"})
-			iter.set(o);
+			itr.set(o);
+
+		@SuppressWarnings( {"unchecked"})
+		@SuppressWarnings( {"unchecked"})
+		@SuppressWarnings( {"unchecked"})
-		protected final java.util.List list;
+		protected final List list;
-		public ListProxy(java.util.List list) {
+		public ListProxy(List list) {
+		@Override
+		@SuppressWarnings( {"unchecked"})
-		/**
-		 * @see java.util.Collection#add(Object)
-		 */
+		@Override
+		@SuppressWarnings( {"unchecked"})
-		/**
-		 * @see java.util.Collection#addAll(Collection)
-		 */
+		@Override
+		@SuppressWarnings( {"unchecked"})
-		/**
-		 * @see java.util.List#addAll(int, Collection)
-		 */
+		@Override
+		@SuppressWarnings( {"unchecked"})
-		/**
-		 * @see java.util.Collection#clear()
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#contains(Object)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#containsAll(Collection)
-		 */
+		@Override
-		/**
-		 * @see java.util.List#get(int)
-		 */
+		@Override
-		/**
-		 * @see java.util.List#indexOf(Object)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#isEmpty()
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#iterator()
-		 */
+		@Override
-		/**
-		 * @see java.util.List#lastIndexOf(Object)
-		 */
+		@Override
-		/**
-		 * @see java.util.List#listIterator()
-		 */
+		@Override
-		/**
-		 * @see java.util.List#listIterator(int)
-		 */
+		@Override
-		/**
-		 * @see java.util.List#remove(int)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#remove(Object)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#removeAll(Collection)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#retainAll(Collection)
-		 */
+		@Override
-		/**
-		 * @see java.util.List#set(int, Object)
-		 */
+		@Override
+		@SuppressWarnings( {"unchecked"})
-			return list.set(i, o);
+			return list.set( i, o );
-		/**
-		 * @see java.util.Collection#size()
-		 */
+		@Override
-		/**
-		 * @see java.util.List#subList(int, int)
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#toArray()
-		 */
+		@Override
-		/**
-		 * @see java.util.Collection#toArray(Object[])
-		 */
+		@Override
+		@SuppressWarnings( {"unchecked"})
-
+	/**
+	 * Contract for operations which are part of a collection's operation queue.
+	 */
+	@SuppressWarnings( {"JavaDoc", "unchecked"})
-			SessionImplementor session)
-	throws HibernateException {
+			SessionImplementor session) throws HibernateException {
-		if ( currentElements.size()==0 ) return oldElements; // no new elements, the old list contains only Orphans
-		if ( oldElements.size()==0) return oldElements; // no old elements, so no Orphans neither
-		
-		Type idType = session.getFactory().getEntityPersister(entityName).getIdentifierType();
+		if ( currentElements.size()==0 ) {
+			return oldElements; // no new elements, the old list contains only Orphans
+		}
+		if ( oldElements.size()==0) {
+			return oldElements; // no old elements, so no Orphans neither
+		}
+
+		final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
+		final Type idType = entityPersister.getIdentifierType();
-		for ( Iterator it=currentElements.iterator(); it.hasNext(); ) {
-			Object current = it.next();
-			if ( current!=null && ForeignKeys.isNotTransient(entityName, current, null, session) ) {
+		for ( Object current : currentElements ) {
+			if ( current != null && ForeignKeys.isNotTransient( entityName, current, null, session ) ) {
-					Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, current, session);
-					currentIds.add( new TypedValue( idType, currentId, session.getEntityMode() ) );
+					Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(
+							entityName,
+							current,
+							session
+					);
+					currentIds.add( new TypedValue( idType, currentId, entityPersister.getEntityMode() ) );
-		for ( Iterator it=oldElements.iterator(); it.hasNext(); ) {
-			Object old = it.next();
-			if ( ! currentSaving.contains( old ) ) {
+		for ( Object old : oldElements ) {
+			if ( !currentSaving.contains( old ) ) {
-				if ( !currentIds.contains( new TypedValue( idType, oldId, session.getEntityMode() ) ) ) {
-					res.add(old);
+				if ( !currentIds.contains( new TypedValue( idType, oldId, entityPersister.getEntityMode() ) ) ) {
+					res.add( old );
-			
-			Type idType = session.getFactory().getEntityPersister(entityName).getIdentifierType();
+			final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
+			Type idType = entityPersister.getIdentifierType();
-			Iterator iter = list.iterator();
-			while ( iter.hasNext() ) {
-				Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, iter.next(), session);
-				if ( idType.isEqual( idOfCurrent, idOfOld, session.getEntityMode(), session.getFactory() ) ) {
-					iter.remove();
+			Iterator itr = list.iterator();
+			while ( itr.hasNext() ) {
+				Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, itr.next(), session);
+				if ( idType.isEqual( idOfCurrent, idOfOld, session.getFactory() ) ) {
+					itr.remove();
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
-		EntityMode entityMode = getSession().getEntityMode();
-				Array.set( result, i, persister.getElementType().deepCopy(elt, entityMode, persister.getFactory()) );
+				Array.set( result, i, persister.getElementType().deepCopy(elt, persister.getFactory()) );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
-		EntityMode entityMode = getSession().getEntityMode();
-		Iterator iter = bag.iterator();
-		while ( iter.hasNext() ) {
-			Object elt = iter.next();
-			final boolean unequal = countOccurrences(elt, bag, elementType, entityMode) !=
-				countOccurrences(elt, sn, elementType, entityMode);
-			if ( unequal ) return false;
+		for ( Object elt : bag ) {
+			final boolean unequal = countOccurrences( elt, bag, elementType )
+					!= countOccurrences( elt, sn, elementType );
+			if ( unequal ) {
+				return false;
+			}
-	private int countOccurrences(Object element, List list, Type elementType, EntityMode entityMode)
+	private int countOccurrences(Object element, List list, Type elementType)
-			if ( elementType.isSame( element, iter.next(), entityMode ) ) result++;
+			if ( elementType.isSame( element, iter.next() ) ) result++;
-		EntityMode entityMode = getSession().getEntityMode();
-			clonedList.add( persister.getElementType().deepCopy( iter.next(), entityMode, persister.getFactory() ) );
+			clonedList.add( persister.getElementType().deepCopy( iter.next(), persister.getFactory() ) );
-		EntityMode entityMode = getSession().getEntityMode();
-			if ( bag.size()>i && elementType.isSame( old, bag.get(i++), entityMode ) ) {
+			if ( bag.size()>i && elementType.isSame( old, bag.get(i++) ) ) {
-					if ( elementType.isSame( old, newiter.next(), entityMode ) ) {
+					if ( elementType.isSame( old, newiter.next() ) ) {
-		final EntityMode entityMode = getSession().getEntityMode();
-		if ( sn.size()>i && elemType.isSame( sn.get(i), entry, entityMode ) ) {
+		if ( sn.size()>i && elemType.isSame( sn.get(i), entry ) ) {
-				if ( elemType.isSame( old, entry, entityMode ) ) return false;
+				if ( elemType.isSame( old, entry ) ) return false;
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
-			Object copy = elementType.deepCopy(value , getSession().getEntityMode(), persister.getFactory() );
+			Object copy = elementType.deepCopy(value , persister.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
-	public Serializable getSnapshot(CollectionPersister persister)
-		throws HibernateException {
-
-		EntityMode entityMode = getSession().getEntityMode();
-
+	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
-				persister.getElementType().deepCopy(value, entityMode, persister.getFactory())
+				persister.getElementType().deepCopy(value, persister.getFactory())
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
-			Object copy = elementType.deepCopy( value, getSession().getEntityMode(), persister.getFactory() );
+			Object copy = elementType.deepCopy( value, persister.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
-
+	@Override
+	@SuppressWarnings( {"unchecked"})
-
-		EntityMode entityMode = getSession().getEntityMode();
+		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
-		Iterator iter = list.iterator();
-		while ( iter.hasNext() ) {
-			Object deepCopy = persister.getElementType()
-					.deepCopy( iter.next(), entityMode, persister.getFactory() );
+		for ( Object element : list ) {
+			Object deepCopy = persister.getElementType().deepCopy( element, persister.getFactory() );
+	@Override
+	@Override
+	@Override
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
+	@SuppressWarnings( {"unchecked"})
-		EntityMode entityMode = getSession().getEntityMode();
-		Iterator iter = map.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry e = (Map.Entry) iter.next();
-			final Object copy = persister.getElementType()
-				.deepCopy( e.getValue(), entityMode, persister.getFactory() );
+		for ( Object o : map.entrySet() ) {
+			Entry e = (Entry) o;
+			final Object copy = persister.getElementType().deepCopy( e.getValue(), persister.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
-	public Serializable getSnapshot(CollectionPersister persister) 
-	throws HibernateException {
-		EntityMode entityMode = getSession().getEntityMode();
-		
-		//if (set==null) return new Set(session);
+	@SuppressWarnings( {"unchecked"})
+	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
-		Iterator iter = set.iterator();
-		while ( iter.hasNext() ) {
+		for ( Object aSet : set ) {
-					.deepCopy( iter.next(), entityMode, persister.getFactory() );
-			clonedSet.put(copied, copied);
+					.deepCopy( aSet, persister.getFactory() );
+			clonedSet.put( copied, copied );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
-			clonedMap.put( e.getKey(), persister.getElementType().deepCopy( e.getValue(), entityMode, persister.getFactory() ) );
+			clonedMap.put( e.getKey(), persister.getElementType().deepCopy( e.getValue(), persister.getFactory() ) );
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
-			Object copy = persister.getElementType().deepCopy( iter.next(), entityMode, persister.getFactory() );
+			Object copy = persister.getElementType().deepCopy( iter.next(), persister.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
-		Object[] propertyValues = meta.getPropertyValues( entity, getEntityMode(criteria, criteriaQuery) );
+		Object[] propertyValues = meta.getPropertyValues( entity );
-		Object[] values = meta.getPropertyValues( entity, getEntityMode(criteria, criteriaQuery) );
+		Object[] values = meta.getPropertyValues( entity );
-		EntityMode result = meta.guessEntityMode(entity);
-		if (result==null) {
+		EntityMode result = meta.getEntityMode();
+		if ( ! meta.getEntityMetamodel().getTuplizer().isInstance( entity ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
-import org.hibernate.EntityMode;
-			EntityMode entityMode = eventSource.getEntityMode();
-			boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent, entityMode );
+			boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent );
-					        persister.getPropertyValue( parent, i, entityMode ),
+					        persister.getPropertyValue( parent, i ),
-							persister.getPropertyValue( parent, i, entityMode ),
+							persister.getPropertyValue( parent, i ),
-	private String composePropertyPath(String propertyName) {
-		if ( componentPathStack.isEmpty() ) {
-			return propertyName;
-		}
-		else {
-			StringBuffer buffer = new StringBuffer();
-			Iterator itr = componentPathStack.iterator();
-			while ( itr.hasNext() ) {
-				buffer.append( itr.next() ).append( '.' );
-			}
-			buffer.append( propertyName );
-			return buffer.toString();
-		}
-	}
-
-		return associationType.getForeignKeyDirection().cascadeNow(cascadeTo) &&
-			( eventSource.getEntityMode()!=EntityMode.DOM4J || associationType.isEmbeddedInXML() );
+		return associationType.getForeignKeyDirection().cascadeNow(cascadeTo);
-		// we can't cascade to non-embedded elements
-		boolean embeddedElements = eventSource.getEntityMode()!=EntityMode.DOM4J ||
-				( (EntityType) collectionType.getElementType( eventSource.getFactory() ) ).isEmbeddedInXML();
-		boolean reallyDoCascade = style.reallyDoCascade(action) &&
-			embeddedElements && child!=CollectionType.UNFETCHED_COLLECTION;
+		boolean reallyDoCascade = style.reallyDoCascade(action) && child!=CollectionType.UNFETCHED_COLLECTION;
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
-import org.hibernate.EntityMode;
+import org.hibernate.type.Type;
+ *
-
-	private Collections() {}
+	private Collections() {
+	}
-	 * @param coll The collection to be updated by unreachability.
-	 * @throws HibernateException
+	 * @param coll The collection to be updated by un-reachability.
-	public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session)
-	throws HibernateException {
-
+	@SuppressWarnings( {"JavaDoc"})
+	public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session) {
-
-	private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session)
-	throws HibernateException {
-
+	private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session) {
-        if (LOG.isDebugEnabled() && loadedPersister != null) LOG.debugf("Collection dereferenced: %s",
-                                                                        MessageHelper.collectionInfoString(loadedPersister,
-                                                                                                           entry.getLoadedKey(),
-                                                                                                           session.getFactory()));
+        if (LOG.isDebugEnabled() && loadedPersister != null) {
+			LOG.debugf(
+					"Collection dereferenced: %s",
+					MessageHelper.collectionInfoString(
+							loadedPersister,
+							entry.getLoadedKey(),
+							session.getFactory()
+					)
+			);
+		}
-		boolean hasOrphanDelete = loadedPersister != null &&
-		                          loadedPersister.hasOrphanDelete();
+		boolean hasOrphanDelete = loadedPersister != null && loadedPersister.hasOrphanDelete();
-		prepareCollectionForUpdate( coll, entry, session.getEntityMode(), session.getFactory() );
+		prepareCollectionForUpdate( coll, entry, session.getFactory() );
-		prepareCollectionForUpdate( coll, entry, session.getEntityMode(), session.getFactory() );
+		prepareCollectionForUpdate( coll, entry, session.getFactory() );
-     * @throws HibernateException
+	 * @param session The session from which this request originates
-	        SessionImplementor session)
-	throws HibernateException {
+	        SessionImplementor session) {
-		prepareCollectionForUpdate( collection, ce, session.getEntityMode(), factory );
+		prepareCollectionForUpdate( collection, ce, factory );
+	@SuppressWarnings( {"JavaDoc"})
-	        EntityMode entityMode,
-	        SessionFactoryImplementor factory)
-	throws HibernateException {
+	        SessionFactoryImplementor factory) {
-		entry.setProcessed(true);
+		entry.setProcessed( true );
-			                                        entityMode, factory
+			                                        factory
-						);
+					);
-					entry.setDorecreate(true);											// we will need to create new entries
+					entry.setDorecreate( true );	// we will need to create new entries
-					entry.setDoremove(true);											// we will need to remove ye olde entries
+					entry.setDoremove( true );		// we will need to remove ye olde entries
-                        LOG.trace("Forcing collection initialization");
-						collection.forceInitialization();								// force initialize!
+                        LOG.trace( "Forcing collection initialization" );
+						collection.forceInitialization();
-
-			else if ( collection.isDirty() ) {											// else if it's elements changed
-				entry.setDoupdate(true);
+			else if ( collection.isDirty() ) {
+				// the collection's elements have changed
+				entry.setDoupdate( true );
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
+import org.hibernate.EntityMode;
-				if (substitute) actype.setPropertyValues( value, subvalues, session.getEntityMode() );
+				if ( substitute ) {
+					// todo : need to account for entity mode on the CompositeType interface :(
+					actype.setPropertyValues( value, subvalues, EntityMode.POJO );
+				}
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
+import org.hibernate.EntityMode;
-	 * @param isUpdate wether it is intended to be updated or saved
+	 * @param isUpdate whether it is intended to be updated or saved
-			final Object[] values = compType.getPropertyValues( value, session.getEntityMode() );
+			final Object[] values = compType.getPropertyValues( value, EntityMode.POJO );
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
-	private Map unownedCollections;
+	private Map<CollectionKey,PersistentCollection> unownedCollections;
-			unownedCollections = new HashMap(8);
+			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(8);
-		unownedCollections.put(key, collection);
+		unownedCollections.put( key, collection );
-		if (unownedCollections==null) {
+		if ( unownedCollections == null ) {
-			return (PersistentCollection) unownedCollections.remove(key);
+			return unownedCollections.remove(key);
-		Iterator itr = proxiesByKey.values().iterator();
-		while ( itr.hasNext() ) {
-			final LazyInitializer li = ( ( HibernateProxy ) itr.next() ).getHibernateLazyInitializer();
+		for ( Object o : proxiesByKey.values() ) {
+			final LazyInitializer li = ((HibernateProxy) o).getHibernateLazyInitializer();
-		for ( int i = 0; i < collectionEntryArray.length; i++ ) {
-			( ( PersistentCollection ) collectionEntryArray[i].getKey() ).unsetSession( getSession() );
+		for ( Map.Entry aCollectionEntryArray : collectionEntryArray ) {
+			((PersistentCollection) aCollectionEntryArray.getKey()).unsetSession( getSession() );
-		Iterator iter = entityEntries.values().iterator();
-		while ( iter.hasNext() ) {
-			( (EntityEntry) iter.next() ).setLockMode(LockMode.NONE);
+		for ( Object o : entityEntries.values() ) {
+			((EntityEntry) o).setLockMode( LockMode.NONE );
-				session.getEntityMode(),
+				persister.getEntityMode(),
-	throws HibernateException {
+			throws HibernateException {
-		boolean alreadyNarrow = persister.getConcreteProxyClass( session.getEntityMode() )
-				.isAssignableFrom( proxy.getClass() );
+		final Class concreteProxyClass = persister.getConcreteProxyClass();
+		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
-            if (LOG.isEnabled(WARN)) LOG.narrowingProxy(persister.getConcreteProxyClass(session.getEntityMode()));
+            if ( LOG.isEnabled(WARN) ) {
+				LOG.narrowingProxy( concreteProxyClass );
+			}
-		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key, session.getEntityMode() );
+		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
-		Object collection = persister.getPropertyValue(
-				potentialParent,
-				property,
-				session.getEntityMode()
-		);
+		Object collection = persister.getPropertyValue( potentialParent, property );
-		Object collection = persister.getPropertyValue( potentialParent, property, session.getEntityMode() );
+		Object collection = persister.getPropertyValue( potentialParent, property );
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
-		persister.setPropertyValues( entity, hydratedState, session.getEntityMode() );
+		persister.setPropertyValues( entity, hydratedState );
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
-		final EntityMode em = loadContexts.getPersistenceContext().getSession().getEntityMode();
+		final EntityMode em = persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode();
-						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING
-						&& em != EntityMode.DOM4J;
+						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING;
-							new CollectionKey( persister, lce.getKey(), session.getEntityMode() ),
+							new CollectionKey(
+									persister,
+									lce.getKey(),
+									persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode()
+							),
-		final EntityMode em = session.getEntityMode();
-		if ( persister.getCollectionType().hasHolder( em ) ) {
+		if ( persister.getCollectionType().hasHolder() ) {
-        if (LOG.isDebugEnabled()) LOG.debugf("Collection fully initialized: %s",
-                                             MessageHelper.collectionInfoString(persister, lce.getKey(), session.getFactory()));
-        if (session.getFactory().getStatistics().isStatisticsEnabled()) session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
+        if (LOG.isDebugEnabled()) {
+			LOG.debugf(
+					"Collection fully initialized: %s",
+					MessageHelper.collectionInfoString(persister, lce.getKey(), session.getFactory())
+			);
+		}
+        if (session.getFactory().getStatistics().isStatisticsEnabled()) {
+			session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
+		}
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
-import java.util.Iterator;
-import org.hibernate.EntityMode;
-	private Map collectionLoadContexts;
-	private Map entityLoadContexts;
+	private Map<ResultSet,CollectionLoadContext> collectionLoadContexts;
+	private Map<ResultSet,EntityLoadContext> entityLoadContexts;
-	private Map xrefLoadingCollectionEntries;
+	private Map<CollectionKey,LoadingCollectionEntry> xrefLoadingCollectionEntries;
-	private EntityMode getEntityMode() {
-		return getSession().getEntityMode();
-	}
-
-			CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) collectionLoadContexts.remove( resultSet );
+			CollectionLoadContext collectionLoadContext = collectionLoadContexts.remove( resultSet );
-			EntityLoadContext entityLoadContext = ( EntityLoadContext ) entityLoadContexts.remove( resultSet );
+			EntityLoadContext entityLoadContext = entityLoadContexts.remove( resultSet );
-			Iterator itr = collectionLoadContexts.values().iterator();
-			while ( itr.hasNext() ) {
-				CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) itr.next();
-                LOG.failSafeCollectionsCleanup(collectionLoadContext);
+			for ( CollectionLoadContext collectionLoadContext : collectionLoadContexts.values() ) {
+				LOG.failSafeCollectionsCleanup( collectionLoadContext );
-			Iterator itr = entityLoadContexts.values().iterator();
-			while ( itr.hasNext() ) {
-				EntityLoadContext entityLoadContext = ( EntityLoadContext ) itr.next();
-                LOG.failSafeEntitiesCleanup(entityLoadContext);
+			for ( EntityLoadContext entityLoadContext : entityLoadContexts.values() ) {
+				LOG.failSafeEntitiesCleanup( entityLoadContext );
-        if (collectionLoadContexts == null) collectionLoadContexts = IdentityMap.instantiate(8);
-        else context = (CollectionLoadContext)collectionLoadContexts.get(resultSet);
+        if ( collectionLoadContexts == null ) {
+			collectionLoadContexts = IdentityMap.instantiate( 8 );
+		}
+        else {
+			context = collectionLoadContexts.get(resultSet);
+		}
-                   if (LOG.isTraceEnabled()) {
-                      LOG.trace("Constructing collection load context for result set [" + resultSet + "]");
-                   }
+			if (LOG.isTraceEnabled()) {
+				LOG.trace("Constructing collection load context for result set [" + resultSet + "]");
+			}
-		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey, getEntityMode() ) );
+		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey ) );
-            if (LOG.isTraceEnabled()) LOG.trace("Returning loading collection: "
-                                                + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
+            if ( LOG.isTraceEnabled() ) {
+				LOG.tracef(
+						"Returning loading collection: %s",
+						MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() )
+				);
+			}
-        if (LOG.isTraceEnabled()) LOG.trace("Creating collection wrapper: "
-                                            + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
+        if ( LOG.isTraceEnabled() ) {
+			LOG.tracef(
+					"Creating collection wrapper: %s",
+					MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() )
+			);
+		}
-			xrefLoadingCollectionEntries = new HashMap();
+			xrefLoadingCollectionEntries = new HashMap<CollectionKey,LoadingCollectionEntry>();
-		LoadingCollectionEntry rtn = ( LoadingCollectionEntry ) xrefLoadingCollectionEntries.get( key );
+		LoadingCollectionEntry rtn = xrefLoadingCollectionEntries.get( key );
-	/*package*/void cleanupCollectionXRefs(Set entryKeys) {
-		Iterator itr = entryKeys.iterator();
-		while ( itr.hasNext() ) {
-			final CollectionKey entryKey = (CollectionKey) itr.next();
+	/*package*/void cleanupCollectionXRefs(Set<CollectionKey> entryKeys) {
+		for ( CollectionKey entryKey : entryKeys ) {
-			context = ( EntityLoadContext ) entityLoadContexts.get( resultSet );
+			context = entityLoadContexts.get( resultSet );
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
-			final int batchSize,
-			final EntityMode entityMode) {
+			final int batchSize) {
-						entityMode,
-				if ( persister.getIdentifierType().isEqual( id, key.getIdentifier(), entityMode ) ) {
+				if ( persister.getIdentifierType().isEqual( id, key.getIdentifier() ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CollectionKey.java
+	public CollectionKey(CollectionPersister persister, Serializable key) {
+		this(
+				persister.getRole(),
+				key,
+				persister.getKeyType(),
+				persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode(),
+				persister.getFactory()
+		);
+	}
+
-		       keyType.isEqual(that.key, key, entityMode, factory);
+		       keyType.isEqual(that.key, key, factory);
-		result = 37 * result + keyType.getHashCode(key, entityMode, factory);
+		result = 37 * result + keyType.getHashCode(key, factory);
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
-			cachedEntityKey = new EntityKey( getId(), getPersister(), entityMode, tenantId );
+			cachedEntityKey = new EntityKey( getId(), getPersister(), tenantId );
-			getPersister().setPropertyValue(
-					entity,
-					getPersister().getVersionProperty(), 
-					nextVersion, 
-					entityMode 
-			);
+			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
-		persister.setPropertyValue(
-				entity,
-		        getPersister().getVersionProperty(),
-		        nextVersion,
-		        entityMode
-		);
+		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
-			loadedState = getPersister().getPropertyValues( entity, entityMode );
+			loadedState = getPersister().getPropertyValues( entity );
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityKey.java
-import org.hibernate.EntityMode;
-	private final EntityMode entityMode;
-	 * @param entityMode The entity mode of the session to which this key belongs
-	public EntityKey(Serializable id, EntityPersister persister, EntityMode entityMode, String tenantId) {
+	public EntityKey(Serializable id, EntityPersister persister, String tenantId) {
-		this.entityMode = entityMode;
-	 * @param entityMode The entity's entity mode
-	        EntityMode entityMode,
-		this.entityMode = entityMode;
-		result = 37 * result + identifierType.getHashCode( identifier, entityMode, factory );
+		result = 37 * result + identifierType.getHashCode( identifier, factory );
-				identifierType.isEqual(otherKey.identifier, this.identifier, entityMode, factory) &&
+				identifierType.isEqual(otherKey.identifier, this.identifier, factory) &&
-		oos.writeObject( entityMode.toString() );
-		        EntityMode.parse( (String) ois.readObject() ),
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityUniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityUniqueKey.java
-		result = 37 * result + keyType.getHashCode(key, entityMode, factory);
+		result = 37 * result + keyType.getHashCode(key, factory);
-		       keyType.isEqual(that.key, key, entityMode);
+		       keyType.isEqual(that.key, key );
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/FilterDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/FilterDefinition.java
-	private final Map parameterTypes = new HashMap();
+	private final Map<String,Type> parameterTypes = new HashMap<String,Type>();
-	public FilterDefinition(String name, String defaultCondition, Map parameterTypes) {
+	public FilterDefinition(String name, String defaultCondition, Map<String,Type> parameterTypes) {
-	    return (Type) parameterTypes.get(parameterName);
+	    return parameterTypes.get(parameterName);
-	public Map getParameterTypes() {
+	public Map<String,Type> getParameterTypes() {
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/NonFlushedChanges.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NonFlushedChanges.java
-import org.hibernate.event.spi.EventSource;
-
-	 * Extracts the non-flushed Changes from an EventSource into this NonFlushedChanges object.
-	 * <p>
-	 * @param source The session
-	 */
-	void extractFromSession(EventSource source);
-
-	/**
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
-	private Map namedParameters;
+	private Map<String,TypedValue> namedParameters;
-			final Object[] postionalParameterValues,
+			final Object[] positionalParameterValues,
-		this( positionalParameterTypes, postionalParameterValues );
+		this( positionalParameterTypes, positionalParameterValues );
-			final Object[] postionalParameterValues) {
-		this( positionalParameterTypes, postionalParameterValues, null, null, false, false, false, null, null, false, null );
+			final Object[] positionalParameterValues) {
+		this( positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, false, null );
-			final Object[] postionalParameterValues,
+			final Object[] positionalParameterValues,
-		this( positionalParameterTypes, postionalParameterValues, null, collectionKeys );
+		this( positionalParameterTypes, positionalParameterValues, null, collectionKeys );
-			final Object[] postionalParameterValues,
-			final Map namedParameters,
+			final Object[] positionalParameterValues,
+			final Map<String,TypedValue> namedParameters,
-				postionalParameterValues,
+				positionalParameterValues,
-			final Map namedParameters,
+			final Map<String,TypedValue> namedParameters,
-			final Map namedParameters,
+			final Map<String,TypedValue> namedParameters,
-	public Map getNamedParameters() {
+	public Map<String,TypedValue> getNamedParameters() {
-	public void setNamedParameters(Map map) {
+	public void setNamedParameters(Map<String,TypedValue> map) {
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
-	public EntityMode getEntityMode();
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/TypedValue.java
+	public TypedValue(Type type, Object value) {
+		this( type, value, EntityMode.POJO );
+	}
+
-		return value==null ? 0 : type.getHashCode(value, entityMode);
+		return value==null ? 0 : type.getHashCode(value );
-			type.isEqual(that.value, value, entityMode);
+			type.isEqual(that.value, value );
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
-					persistenceContext.getEntitiesByKey().values().iterator(),
-					session.getEntityMode()
-				);
+					persistenceContext.getEntitiesByKey().values().iterator()
+			);
-						collectionEntry.getLoadedKey(),
-						session.getEntityMode()
-					);
-				persistenceContext.getCollectionsByKey()
-						.put(collectionKey, persistentCollection);
+						collectionEntry.getLoadedKey()
+				);
+				persistenceContext.getCollectionsByKey().put(collectionKey, persistentCollection);
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractReassociateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractReassociateEventListener.java
-		Object[] values = persister.getPropertyValues( object, source.getEntityMode() );
+		Object[] values = persister.getPropertyValues( object );
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
-		if ( persister.implementsLifecycle( source.getEntityMode() ) ) {
+		if ( persister.implementsLifecycle() ) {
-			persister.setPropertyValues( entity, values, source.getEntityMode() );
+			persister.setPropertyValues( entity, values );
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java
-			persister.getPropertyValues( object, getSession().getEntityMode() ),
+			persister.getPropertyValues( object ),
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
-			version = persister.getVersion( entity, source.getEntityMode() );
+			version = persister.getVersion( entity );
-					persister.getPropertyValues( entity, source.getEntityMode() ),
+					persister.getPropertyValues( entity ),
-			currentState = persister.getPropertyValues( entity, session.getEntityMode() );
+			currentState = persister.getPropertyValues( entity );
-		if ( persister.implementsLifecycle( session.getEntityMode() ) ) {
+		if ( persister.implementsLifecycle() ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
-import org.hibernate.EntityMode;
-	public void checkId(
-			Object object,
-			EntityPersister persister,
-			Serializable id,
-			EntityMode entityMode,
-			SessionImplementor session) throws HibernateException {
+	public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session)
+			throws HibernateException {
-			if ( !persister.getIdentifierType().isEqual( id, oid, entityMode, session.getFactory() ) ) {
+			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
-	        EntityMode entityMode,
- 					if ( !types[prop].isEqual( current[prop], loadedVal, entityMode ) ) {
+ 					if ( !types[prop].isEqual( current[prop], loadedVal ) ) {
-		final EntityMode entityMode = session.getEntityMode();
-		final Object[] values = getValues( entity, entry, entityMode, mightBeDirty, session );
+		final Object[] values = getValues( entity, entry, mightBeDirty, session );
-			if (substitute) persister.setPropertyValues( entity, values, entityMode );
+			if (substitute) persister.setPropertyValues( entity, values );
-	private Object[] getValues(
-			Object entity,
-			EntityEntry entry,
-			EntityMode entityMode,
-			boolean mightBeDirty,
-	        SessionImplementor session) {
+	private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
-			checkId( entity, persister, entry.getId(), entityMode, session );
+			checkId( entity, persister, entry.getId(), session );
-			values = persister.getPropertyValues( entity, entityMode );
+			values = persister.getPropertyValues( entity );
-			checkNaturalId( persister, entry, values, loadedState, entityMode, session );
+			checkNaturalId( persister, entry, values, loadedState, session );
-		final EntityMode entityMode = session.getEntityMode();
-								persister.getPropertyValues( entity, entityMode ) :
+								persister.getPropertyValues( entity ) :
-				final Object[] currentState =
-						persister.getPropertyValues( event.getEntity(), event.getSession().getEntityMode() );
+				final Object[] currentState = persister.getPropertyValues( event.getEntity() );
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
-import org.hibernate.EntityMode;
+import org.hibernate.LockMode;
+import org.hibernate.NonUniqueObjectException;
+import org.hibernate.PersistentObjectException;
+import org.hibernate.TypeMismatchException;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.cache.spi.entry.CacheEntry;
+import org.hibernate.engine.internal.TwoPhaseLoad;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.event.spi.EventType;
-import org.hibernate.LockMode;
-import org.hibernate.NonUniqueObjectException;
-import org.hibernate.PersistentObjectException;
-import org.hibernate.TypeMismatchException;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.cache.spi.entry.CacheEntry;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.internal.TwoPhaseLoad;
-import org.hibernate.event.spi.EventType;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-		if ( persister.getIdentifierType().isComponentType() && EntityMode.DOM4J == event.getSession().getEntityMode() ) {
-			// skip this check for composite-ids relating to dom4j entity-mode;
-			// alternatively, we could add a check to make sure the incoming id value is
-			// an instance of Element...
-		}
-		else {
-			if ( idClass != null && ! idClass.isInstance( event.getEntityId() ) ) {
-				// we may have the kooky jpa requirement of allowing find-by-id where
-				// "id" is the "simple pk value" of a dependent objects parent.  This
-				// is part of its generally goofy "derived identity" "feature"
-				if ( persister.getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
-					final EmbeddedComponentType dependentIdType =
-							(EmbeddedComponentType) persister.getEntityMetamodel().getIdentifierProperty().getType();
-					if ( dependentIdType.getSubtypes().length == 1 ) {
-						final Type singleSubType = dependentIdType.getSubtypes()[0];
-						if ( singleSubType.isEntityType() ) {
-							final EntityType dependentParentType = (EntityType) singleSubType;
-							final Type dependentParentIdType = dependentParentType.getIdentifierOrUniqueKeyType( source.getFactory() );
-							if ( dependentParentIdType.getReturnedClass().isInstance( event.getEntityId() ) ) {
-								// yep that's what we have...
-								loadByDerivedIdentitySimplePkValue(
-										event,
-										loadType,
-										persister,
-										dependentIdType,
-										source.getFactory().getEntityPersister( dependentParentType.getAssociatedEntityName() )
-								);
-								return;
-							}
+		if ( idClass != null && ! idClass.isInstance( event.getEntityId() ) ) {
+			// we may have the kooky jpa requirement of allowing find-by-id where
+			// "id" is the "simple pk value" of a dependent objects parent.  This
+			// is part of its generally goofy "derived identity" "feature"
+			if ( persister.getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
+				final EmbeddedComponentType dependentIdType =
+						(EmbeddedComponentType) persister.getEntityMetamodel().getIdentifierProperty().getType();
+				if ( dependentIdType.getSubtypes().length == 1 ) {
+					final Type singleSubType = dependentIdType.getSubtypes()[0];
+					if ( singleSubType.isEntityType() ) {
+						final EntityType dependentParentType = (EntityType) singleSubType;
+						final Type dependentParentIdType = dependentParentType.getIdentifierOrUniqueKeyType( source.getFactory() );
+						if ( dependentParentIdType.getReturnedClass().isInstance( event.getEntityId() ) ) {
+							// yep that's what we have...
+							loadByDerivedIdentitySimplePkValue(
+									event,
+									loadType,
+									persister,
+									dependentIdType,
+									source.getFactory().getEntityPersister( dependentParentType.getAssociatedEntityName() )
+							);
+							return;
-				throw new TypeMismatchException(
-						"Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass + ", got " + event.getEntityId().getClass()
-				);
+			throw new TypeMismatchException(
+					"Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass + ", got " + event.getEntityId().getClass()
+			);
-		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, event.getSession().getEntityMode() );
+		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, dependentPersister.getEntityMode() );
-//				EntityPersister persister = event.getSession().getFactory().getEntityPersister( event.getEntityClassName() );
-				EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
-				if ( ! persister.isInstance( old, event.getSession().getEntityMode() ) ) {
+				final EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
+				if ( ! persister.isInstance( old ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
-public class DefaultMergeEventListener extends AbstractSaveEventListener
-	implements MergeEventListener {
+public class DefaultMergeEventListener extends AbstractSaveEventListener implements MergeEventListener {
-			Object propertyFromCopy = persister.getPropertyValue( copy, propertyName, source.getEntityMode() );
-			Object propertyFromEntity = persister.getPropertyValue( entity, propertyName, source.getEntityMode() );
+			Object propertyFromCopy = persister.getPropertyValue( copy, propertyName );
+			Object propertyFromEntity = persister.getPropertyValue( entity, propertyName );
-                    if (propertyFromEntity != null && !copyCache.containsKey(propertyFromEntity)) LOG.trace("Property '"
-                                                                                                            + copyEntry.getEntityName()
-                                                                                                            + "."
-                                                                                                            + propertyName
-                                                                                                            + "' is not in copy cache");
+                    if (propertyFromEntity != null && !copyCache.containsKey(propertyFromEntity)) {
+						LOG.tracef(
+								"Property '%s.%s' is not in copy cache",
+								copyEntry.getEntityName(),
+								propertyName
+						);
+					}
-			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getEntityMode(), source.getFactory() ) ) {
+			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getFactory() ) ) {
-				.deepCopy( id, source.getEntityMode(), source.getFactory() );
+				.deepCopy( id, source.getFactory() );
-				persister.getVersion( target, source.getEntityMode() ),
-				persister.getVersion( entity, source.getEntityMode() ),
-				source.getEntityMode()
+				persister.getVersion( target ),
+				persister.getVersion( entity )
-				persister.getPropertyValues( entity, source.getEntityMode() ),
-				persister.getPropertyValues( target, source.getEntityMode() ),
+				persister.getPropertyValues( entity ),
+				persister.getPropertyValues( target ),
-		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
+		persister.setPropertyValues( target, copiedValues );
-					persister.getPropertyValues( entity, source.getEntityMode() ),
-					persister.getPropertyValues( target, source.getEntityMode() ),
+					persister.getPropertyValues( entity ),
+					persister.getPropertyValues( target ),
-					persister.getPropertyValues( entity, source.getEntityMode() ),
-					persister.getPropertyValues( target, source.getEntityMode() ),
+					persister.getPropertyValues( entity ),
+					persister.getPropertyValues( target ),
-		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
+		persister.setPropertyValues( target, copiedValues );
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPostLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPostLoadEventListener.java
-		if ( event.getPersister().implementsLifecycle( event.getSession().getEntityMode() ) ) {
+		if ( event.getPersister().implementsLifecycle() ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultReplicateEventListener.java
-					persister.getVersion( entity, source.getEntityMode() ),
+					persister.getVersion( entity ),
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
-						.isEqual( requestedId, entityEntry.getId(), event.getSession().getEntityMode(), factory );
+						.isEqual( requestedId, entityEntry.getId(), factory );
-		source.getPersistenceContext().addEntity(entity, (persister.isMutable() ? Status.MANAGED : Status.READ_ONLY), null, // cachedState,
-                                                 key,
-                                                 persister.getVersion(entity, source.getEntityMode()),
-                                                 LockMode.NONE,
-                                                 true,
-                                                 persister,
-                                                 false,
-                                                 true // assume true, since we don't really know, and it doesn't matter
+		source.getPersistenceContext().addEntity(
+				entity,
+				(persister.isMutable() ? Status.MANAGED : Status.READ_ONLY),
+				null, // cachedState,
+				key,
+				persister.getVersion( entity ),
+				LockMode.NONE,
+				true,
+				persister,
+				false,
+				true // assume true, since we don't really know, and it doesn't matter
-		if ( persister.implementsLifecycle( source.getEntityMode() ) ) {
+		if ( persister.implementsLifecycle() ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/EventCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/EventCache.java
-	private Map entityToCopyMap = IdentityMap.instantiate(10);
+	private IdentityMap entityToCopyMap = IdentityMap.instantiate(10);
-	public Map invertMap() {
+	public IdentityMap invertMap() {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java
-		if ( type.hasHolder( getSession().getEntityMode() ) ) {
+		if ( type.hasHolder() ) {
-					new CollectionKey( ce.getLoadedPersister(), ce.getLoadedKey(), getSession().getEntityMode() )
+					new CollectionKey( ce.getLoadedPersister(), ce.getLoadedKey() )
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/FlushVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/FlushVisitor.java
-			if ( type.hasHolder( getSession().getEntityMode() ) ) {
+			if ( type.hasHolder() ) {
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/ReattachVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/ReattachVisitor.java
-        if (role.getCollectionType().useLHSPrimaryKey()) return ownerIdentifier;
-        return (Serializable)role.getOwnerEntityPersister().getPropertyValue(owner,
-                                                                             role.getCollectionType().getLHSPropertyName(),
-                                                                             getSession().getEntityMode());
+        if ( role.getCollectionType().useLHSPrimaryKey() ) {
+			return ownerIdentifier;
+		}
+        return (Serializable)role.getOwnerEntityPersister().getPropertyValue(
+				owner,
+				role.getCollectionType().getLHSPropertyName()
+		);
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/WrapVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/WrapVisitor.java
-			if ( collectionType.hasHolder( session.getEntityMode() ) ) {
+			if ( collectionType.hasHolder() ) {
-				componentType.setPropertyValues( component, values, getSession().getEntityMode() );
+				componentType.setPropertyValues( component, values, EntityMode.POJO );
-		EntityMode entityMode = getSession().getEntityMode();
-		Object[] values = persister.getPropertyValues( object, entityMode );
-		Type[] types = persister.getPropertyTypes();
-		processEntityPropertyValues(values, types);
+		final Object[] values = persister.getPropertyValues( object );
+		final Type[] types = persister.getPropertyTypes();
+		processEntityPropertyValues( values, types );
-			persister.setPropertyValues( object, values, entityMode );
+			persister.setPropertyValues( object, values );
--- a/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
-		Object associatedObject = persister.getPropertyValue( object, propertyName, session.getEntityMode() );
+		Object associatedObject = persister.getPropertyValue( object, propertyName );
--- a/hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
-			Object uniqueKeyValue = persister.getPropertyValue( entity, uniqueKeyPropertyName, session.getEntityMode() );
+			Object uniqueKeyValue = persister.getPropertyValue( entity, uniqueKeyPropertyName );
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
-	private Map namedParameters = new HashMap(4);
+	private Map<String,TypedValue> namedParameters = new HashMap<String, TypedValue>(4);
-			 namedParameters.put( name, new TypedValue( type, val, session.getEntityMode() ) );
+			 namedParameters.put( name, new TypedValue( type, val  ) );
-		namedParameterLists.put( name, new TypedValue( type, vals, session.getEntityMode() ) );
+		namedParameterLists.put( name, new TypedValue( type, vals ) );
-			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next(), session.getEntityMode() ) );
+			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next() ) );
-			namedParamsCopy.put( alias, new TypedValue( type, iter.next(), session.getEntityMode() ) );
+			namedParamsCopy.put( alias, new TypedValue( type, iter.next() ) );
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
-		return new EntityKey( id, persister, getEntityMode(), getTenantIdentifier() );
+		return new EntityKey( id, persister, getTenantIdentifier() );
-		return new CacheKey( id, type, entityOrRoleName, getEntityMode(), getTenantIdentifier(), getFactory() );
+		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
-	private Map parameters = new HashMap();
+	private Map<String,Object> parameters = new HashMap<String, Object>();
-	public Map getParameters() {
+	public Map<String,?> getParameters() {
--- a/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
-public final class NonFlushedChangesImpl implements NonFlushedChanges {
+public final class NonFlushedChangesImpl implements NonFlushedChanges, Serializable {
+    private static final Logger LOG = Logger.getLogger( NonFlushedChangesImpl.class.getName() );
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NonFlushedChangesImpl.class.getName());
+	private transient ActionQueue actionQueue;
+	private transient StatefulPersistenceContext persistenceContext;
-	private static class SessionNonFlushedChanges implements Serializable {
-		private transient EntityMode entityMode;
-		private transient ActionQueue actionQueue;
-		private transient StatefulPersistenceContext persistenceContext;
-
-		public SessionNonFlushedChanges(EventSource session) {
-			this.entityMode = session.getEntityMode();
-			this.actionQueue = session.getActionQueue();
-			this.persistenceContext = ( StatefulPersistenceContext ) session.getPersistenceContext();
-		}
-
-		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
-			ois.defaultReadObject();
-			entityMode = EntityMode.parse( ( String ) ois.readObject() );
-			persistenceContext = StatefulPersistenceContext.deserialize( ois, null );
-			actionQueue = ActionQueue.deserialize( ois, null );
-		}
-
-		private void writeObject(ObjectOutputStream oos) throws IOException {
-            LOG.trace("Serializing SessionNonFlushedChanges");
-			oos.defaultWriteObject();
-			oos.writeObject( entityMode.toString() );
-			persistenceContext.serialize( oos );
-			actionQueue.serialize( oos );
-		}
+	public NonFlushedChangesImpl(EventSource session) {
+		this.actionQueue = session.getActionQueue();
+		this.persistenceContext = ( StatefulPersistenceContext ) session.getPersistenceContext();
-	private Map nonFlushedChangesByEntityMode = new HashMap();
-	public NonFlushedChangesImpl( EventSource session ) {
-		extractFromSession( session );
+	/* package-protected */
+	ActionQueue getActionQueue() {
+		return actionQueue;
-	public void extractFromSession(EventSource session) {
-		if ( nonFlushedChangesByEntityMode.containsKey( session.getEntityMode() ) ) {
-			throw new AssertionFailure( "Already has non-flushed changes for entity mode: " + session.getEntityMode() );
-		}
-		nonFlushedChangesByEntityMode.put( session.getEntityMode(), new SessionNonFlushedChanges( session ) );
+	/* package-protected */
+	StatefulPersistenceContext getPersistenceContext() {
+		return persistenceContext;
-	private SessionNonFlushedChanges getSessionNonFlushedChanges(EntityMode entityMode) {
-		return ( SessionNonFlushedChanges ) nonFlushedChangesByEntityMode.get( entityMode );
+	public void clear() {
-	/* package-protected */
-	ActionQueue getActionQueue(EntityMode entityMode) {
-		return getSessionNonFlushedChanges( entityMode ).actionQueue;
+	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
+		LOG.trace( "Deserializing NonFlushedChangesImpl" );
+		ois.defaultReadObject();
+		persistenceContext = StatefulPersistenceContext.deserialize( ois, null );
+		actionQueue = ActionQueue.deserialize( ois, null );
-	/* package-protected */
-	StatefulPersistenceContext getPersistenceContext(EntityMode entityMode) {
-		return getSessionNonFlushedChanges( entityMode ).persistenceContext;
+	private void writeObject(ObjectOutputStream oos) throws IOException {
+		LOG.trace( "Serializing NonFlushedChangesImpl" );
+		oos.defaultWriteObject();
+		persistenceContext.serialize( oos );
+		actionQueue.serialize( oos );
-	public void clear() {
-		nonFlushedChangesByEntityMode.clear();
-	}
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
-import java.util.LinkedHashSet;
-import org.hibernate.context.internal.ThreadLocalSessionContext;
-import org.hibernate.context.spi.CurrentSessionContext;
+import org.hibernate.context.internal.ThreadLocalSessionContext;
+import org.hibernate.context.spi.CurrentSessionContext;
-import org.hibernate.engine.query.spi.QueryPlanCache;
-import org.hibernate.engine.spi.FilterDefinition;
-import org.hibernate.engine.spi.Mapping;
-import org.hibernate.engine.spi.NamedQueryDefinition;
-import org.hibernate.engine.spi.NamedSQLQueryDefinition;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.query.spi.QueryPlanCache;
+import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.internal.util.collections.EmptyIterator;
-import org.hibernate.integrator.spi.IntegratorService;
-	private final transient HashMap entityNameResolvers = new HashMap();
+	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
-		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizerMapping() == null ) {
+		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
-		Iterator itr = persister.getEntityMetamodel().getTuplizerMapping().iterateTuplizers();
-		while ( itr.hasNext() ) {
-			final EntityTuplizer tuplizer = ( EntityTuplizer ) itr.next();
-			registerEntityNameResolvers( tuplizer );
-		}
+		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
-		for ( int i = 0; i < resolvers.length; i++ ) {
-			registerEntityNameResolver( resolvers[i], tuplizer.getEntityMode() );
+		for ( EntityNameResolver resolver : resolvers ) {
+			registerEntityNameResolver( resolver );
-	public void registerEntityNameResolver(EntityNameResolver resolver, EntityMode entityMode) {
-		LinkedHashSet resolversForMode = ( LinkedHashSet ) entityNameResolvers.get( entityMode );
-		if ( resolversForMode == null ) {
-			resolversForMode = new LinkedHashSet();
-			entityNameResolvers.put( entityMode, resolversForMode );
-		}
-		resolversForMode.add( resolver );
+	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
+
+	public void registerEntityNameResolver(EntityNameResolver resolver) {
+		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
-	public Iterator iterateEntityNameResolvers(EntityMode entityMode) {
-		Set actualEntityNameResolvers = ( Set ) entityNameResolvers.get( entityMode );
-		return actualEntityNameResolvers == null
-				? EmptyIterator.INSTANCE
-				: actualEntityNameResolvers.iterator();
+	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
+		return entityNameResolvers.keySet();
-						final Class mappedClass = testQueryable.getMappedClass( EntityMode.POJO );
+						final Class mappedClass = testQueryable.getMappedClass();
-								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass( EntityMode.POJO);
+								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass();
-					EntityMode.POJO,			// we have to assume POJO
-					null, 						// and also assume non tenancy
+					null, 						// have to assume non tenancy
-					EntityMode.POJO,			// we have to assume POJO
-					null,						// and also assume non tenancy
+					null,						// have to assume non tenancy
-		private EntityMode entityMode;
-			this.entityMode = settings.getDefaultEntityMode();
-					entityMode,
-		public SessionBuilder entityMode(EntityMode entityMode) {
-			this.entityMode = entityMode;
-			return this;
-		}
-
-		@Override
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
-import java.util.HashMap;
-import org.hibernate.EntityMode;
-import org.hibernate.SessionBuilder;
+import org.hibernate.SessionBuilder;
+import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.engine.query.spi.HQLQueryPlan;
+import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
+import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
-import org.hibernate.engine.jdbc.LobCreationContext;
-import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.query.spi.HQLQueryPlan;
-import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
-import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.event.service.spi.EventListenerGroup;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-	private transient EntityMode entityMode = EntityMode.POJO;
-	private transient Session rootSession;
-	private transient Map childSessionsByEntityMode;
-
-	/**
-	 * Constructor used in building "child sessions".
-	 *
-	 * @param parent The parent session
-	 * @param entityMode
-	 */
-	private SessionImpl(SessionImpl parent, EntityMode entityMode) {
-		super( parent.factory, parent.getTenantIdentifier() );
-		this.rootSession = parent;
-		this.timestamp = parent.timestamp;
-		this.transactionCoordinator = parent.transactionCoordinator;
-		this.interceptor = parent.interceptor;
-		this.actionQueue = new ActionQueue( this );
-		this.entityMode = entityMode;
-		this.persistenceContext = new StatefulPersistenceContext( this );
-		this.flushBeforeCompletionEnabled = false;
-		this.autoCloseSessionEnabled = false;
-		this.connectionReleaseMode = null;
-
-		loadQueryInfluencers = new LoadQueryInfluencers( factory );
-
-        if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
-
-        LOG.debugf("Opened session [%s]", entityMode);
-	}
-
-	 * @param entityMode The entity-mode for this session
+	 * @param tenantIdentifier The tenant identifier to use.  May be null
-			final EntityMode entityMode,
-		this.rootSession = null;
-		this.entityMode = entityMode;
-	public Session getSession(EntityMode entityMode) {
-		if ( this.entityMode == entityMode ) {
-			return this;
-		}
-
-		if ( rootSession != null ) {
-			return rootSession.getSession( entityMode );
-		}
-
-		errorIfClosed();
-		checkTransactionSynchStatus();
-
-		SessionImpl rtn = null;
-		if ( childSessionsByEntityMode == null ) {
-			childSessionsByEntityMode = new HashMap();
-		}
-		else {
-			rtn = (SessionImpl) childSessionsByEntityMode.get( entityMode );
-		}
-
-		if ( rtn == null ) {
-			rtn = new SessionImpl( this, entityMode );
-			childSessionsByEntityMode.put( entityMode, rtn );
-		}
-
-		return rtn;
-	}
-
-			try {
-				if ( childSessionsByEntityMode != null ) {
-					Iterator childSessions = childSessionsByEntityMode.values().iterator();
-					while ( childSessions.hasNext() ) {
-						final SessionImpl child = ( SessionImpl ) childSessions.next();
-						child.close();
-					}
-				}
-			}
-			catch( Throwable t ) {
-				// just ignore
-			}
-
-			if ( rootSession == null ) {
-				return transactionCoordinator.close();
-			}
-			else {
-				return null;
-			}
+			return transactionCoordinator.close();
-
-		if ( childSessionsByEntityMode != null ) {
-			Iterator iter = childSessionsByEntityMode.values().iterator();
-			while ( iter.hasNext() ) {
-				( (Session) iter.next() ).flush();
-			}
-		}
-		NonFlushedChanges nonFlushedChanges = new NonFlushedChangesImpl( this );
-		if ( childSessionsByEntityMode != null ) {
-			Iterator it = childSessionsByEntityMode.values().iterator();
-			while ( it.hasNext() ) {
-				nonFlushedChanges.extractFromSession( ( EventSource ) it.next() );
-			}
-		}
-		return nonFlushedChanges;
+		return new NonFlushedChangesImpl( this );
-		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext( entityMode) );
-		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue( entityMode ) );
-		if ( childSessionsByEntityMode != null ) {
-			for ( Iterator it = childSessionsByEntityMode.values().iterator(); it.hasNext(); ) {
-				( ( SessionImpl ) it.next() ).applyNonFlushedChanges( nonFlushedChanges );
-			}
-		}
+		// todo : why aren't these just part of the NonFlushedChanges API ?
+		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext() );
+		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue() );
-        LOG.debugf("Disconnecting session");
+        LOG.debugf( "Disconnecting session" );
-		if ( rootSession == null ) {
-			try {
-				interceptor.beforeTransactionCompletion( hibernateTransaction );
-			}
-			catch (Throwable t) {
-                LOG.exceptionInBeforeTransactionCompletionInterceptor(t);
-			}
+		try {
+			interceptor.beforeTransactionCompletion( hibernateTransaction );
+		}
+		catch (Throwable t) {
+			LOG.exceptionInBeforeTransactionCompletionInterceptor(t);
-		if ( rootSession == null && hibernateTransaction != null ) {
+		if ( hibernateTransaction != null ) {
-		fireLock( new LockEvent(entityName, object, lockMode, this) );
+		fireLock( new LockEvent( entityName, object, lockMode, this ) );
-		fireDelete( new DeleteEvent(object, this) );
+		fireDelete( new DeleteEvent( object, this ) );
-		fireRefresh( new RefreshEvent( entityName,object,this ) );
+		fireRefresh( new RefreshEvent( entityName, object, this ) );
-		fireRefresh( new RefreshEvent(entityName, object, lockOptions, this) );
+		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
-		fireReplicate( new ReplicateEvent(obj, replicationMode, this) );
+		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
-		Object result = interceptor.instantiate( persister.getEntityName(), entityMode, id );
+		Object result = interceptor.instantiate( persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id );
-	public EntityMode getEntityMode() {
-		checkTransactionSynchStatus();
-		return entityMode;
-	}
-
-        // todo : should seriously consider not allowing a txn to begin from a child session
-        // can always route the request to the root session...
-        if (rootSession != null) LOG.transactionStartedOnNonRootSession();
-
-				return factory.getEntityPersister( entityName )
-						.getSubclassEntityPersister( object, getFactory(), entityMode );
+				return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
-		boolean isRootSession = ois.readBoolean();
-		entityMode = EntityMode.parse( ( String ) ois.readObject() );
-		if ( isRootSession ) {
-			transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
-		}
+		transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
-		childSessionsByEntityMode = ( Map ) ois.readObject();
-
-		Iterator iter = loadQueryInfluencers.getEnabledFilterNames().iterator();
-		while ( iter.hasNext() ) {
-			String filterName = ( String ) iter.next();
-			 ( ( FilterImpl ) loadQueryInfluencers.getEnabledFilter( filterName )  )
-					.afterDeserialize( factory );
-		}
-
-		if ( isRootSession && childSessionsByEntityMode != null ) {
-			iter = childSessionsByEntityMode.values().iterator();
-			while ( iter.hasNext() ) {
-				final SessionImpl child = ( ( SessionImpl ) iter.next() );
-				child.rootSession = this;
-				child.transactionCoordinator = this.transactionCoordinator;
-			}
+		for ( String filterName : loadQueryInfluencers.getEnabledFilterNames() ) {
+			((FilterImpl) loadQueryInfluencers.getEnabledFilter( filterName )).afterDeserialize( factory );
-        LOG.trace("Serializing session");
+        LOG.trace( "Serializing session" );
-		oos.writeBoolean( rootSession == null );
-		oos.writeObject( entityMode.toString() );
-		if ( rootSession == null ) {
-			transactionCoordinator.serialize( oos );
-		}
+		transactionCoordinator.serialize( oos );
-		oos.writeObject( childSessionsByEntityMode );
-		public SharedSessionBuilder entityMode() {
-			return entityMode( session.entityMode );
-		}
-
-		@Override
-		public SharedSessionBuilder entityMode(EntityMode entityMode) {
-			return (SharedSessionBuilder) super.entityMode( entityMode );
-		}
-
-		@Override
-			Iterator itr = factory.iterateEntityNameResolvers( entityMode );
-			while ( itr.hasNext() ) {
-				final EntityNameResolver resolver = ( EntityNameResolver ) itr.next();
+			for ( EntityNameResolver resolver : factory.iterateEntityNameResolvers() ) {
+
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
-		EntityPersister persister = getEntityPersister(entityName, entity);
-		Serializable id = persister.getIdentifierGenerator().generate(this, entity);
-		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
+		EntityPersister persister = getEntityPersister( entityName, entity );
+		Serializable id = persister.getIdentifierGenerator().generate( this, entity );
+		Object[] state = persister.getPropertyValues( entity );
-				persister.setPropertyValues( entity, state, EntityMode.POJO );
+				persister.setPropertyValues( entity, state );
-		Object version = persister.getVersion(entity, EntityMode.POJO);
+		Object version = persister.getVersion( entity );
-		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
+		Object[] state = persister.getPropertyValues( entity );
-			oldVersion = persister.getVersion(entity, EntityMode.POJO);
+			oldVersion = persister.getVersion( entity );
-			persister.setPropertyValues(entity, state, EntityMode.POJO);
+			persister.setPropertyValues( entity, state );
-			return factory.getEntityPersister( entityName )
-					.getSubclassEntityPersister( object, getFactory(), EntityMode.POJO );
+			return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
-	public static Map mapOfSize(int size) {
-		return new HashMap( determineProperSizing( size ), LOAD_FACTOR );
+	public static <K,V> Map<K,V> mapOfSize(int size) {
+		return new HashMap<K,V>( determineProperSizing( size ), LOAD_FACTOR );
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/IdentityMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/IdentityMap.java
-public final class IdentityMap implements Map {
+public final class IdentityMap<K,V> implements Map<K,V> {
-	private final Map map;
-	private transient Map.Entry[] entryArray = new Map.Entry[0];
+	private final Map<IdentityKey<K>,V> map;
+	@SuppressWarnings( {"unchecked"})
+	private transient Entry<IdentityKey<K>,V>[] entryArray = new Entry[0];
-	public static Map instantiate(int size) {
-		return new IdentityMap( new HashMap( size ) );
+	public static <K,V> IdentityMap<K,V> instantiate(int size) {
+		return new IdentityMap<K,V>( new HashMap<IdentityKey<K>,V>( size ) );
-	 * @return
+	 * @return The map
-	public static Map instantiateSequenced(int size) {
-		return new IdentityMap( new LinkedHashMap( size ) );
+	public static <K,V> IdentityMap<K,V> instantiateSequenced(int size) {
+		return new IdentityMap<K,V>( new LinkedHashMap<IdentityKey<K>,V>( size ) );
-	private IdentityMap(Map underlyingMap) {
+	private IdentityMap(Map<IdentityKey<K>,V> underlyingMap) {
-	 * @param map
+	 * @param map The map of entries
-	public static final class IdentityMapEntry implements java.util.Map.Entry {
-		IdentityMapEntry(Object key, Object value) {
+	public static final class IdentityMapEntry<K,V> implements java.util.Map.Entry<K,V> {
+		private K key;
+		private V value;
+
+		IdentityMapEntry(K key, V value) {
-		private Object key;
-		private Object value;
-		public Object getKey() {
+
+		public K getKey() {
-		public Object getValue() {
+		public V getValue() {
-		public Object setValue(Object value) {
-			Object result = this.value;
+		public V setValue(V value) {
+			V result = this.value;
-	public static final class IdentityKey implements Serializable {
-		private Object key;
+	public static final class IdentityKey<K> implements Serializable {
+		private K key;
-		IdentityKey(Object key) {
+		IdentityKey(K key) {
+
+		@SuppressWarnings( {"EqualsWhichDoesntCheckParameterClass"})
+
+
-		public Object getRealKey() {
+
+		public K getRealKey() {
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	@Override
-	public Object get(Object key) {
-		IdentityKey k = new IdentityKey(key);
-		return map.get(k);
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public V get(Object key) {
+		return map.get( new IdentityKey(key) );
-	public Object put(Object key, Object value) {
+	@Override
+	public V put(K key, V value) {
-		return map.put( new IdentityKey(key), value );
+		return map.put( new IdentityKey<K>(key), value );
-	public Object remove(Object key) {
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public V remove(Object key) {
-		IdentityKey k = new IdentityKey(key);
-		return map.remove(k);
+		return map.remove( new IdentityKey(key) );
-	public void putAll(Map otherMap) {
-		Iterator iter = otherMap.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			put( me.getKey(), me.getValue() );
+	@Override
+	public void putAll(Map<? extends K, ? extends V> otherMap) {
+		for ( Entry<? extends K, ? extends V> entry : otherMap.entrySet() ) {
+			put( entry.getKey(), entry.getValue() );
+	@Override
-	public Set keySet() {
+	@Override
+	public Set<K> keySet() {
-	public Collection values() {
+	@Override
+	public Collection<V> values() {
-	public Set entrySet() {
-		Set set = new HashSet( map.size() );
-		Iterator iter = map.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			set.add( new IdentityMapEntry( ( (IdentityKey) me.getKey() ).key, me.getValue() ) );
+	@Override
+	public Set<Entry<K,V>> entrySet() {
+		Set<Entry<K,V>> set = new HashSet<Entry<K,V>>( map.size() );
+		for ( Entry<IdentityKey<K>, V> entry : map.entrySet() ) {
+			set.add( new IdentityMapEntry<K,V>( entry.getKey().getRealKey(), entry.getValue() ) );
-	public List entryList() {
-		ArrayList list = new ArrayList( map.size() );
-		Iterator iter = map.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			list.add( new IdentityMapEntry( ( (IdentityKey) me.getKey() ).key, me.getValue() ) );
+	public List<Entry<K,V>> entryList() {
+		ArrayList<Entry<K,V>> list = new ArrayList<Entry<K,V>>( map.size() );
+		for ( Entry<IdentityKey<K>, V> entry : map.entrySet() ) {
+			list.add( new IdentityMapEntry<K,V>( entry.getKey().getRealKey(), entry.getValue() ) );
+	@SuppressWarnings( {"unchecked"})
-			Iterator iter = map.entrySet().iterator();
+			Iterator itr = map.entrySet().iterator();
-			while ( iter.hasNext() ) {
-				Map.Entry me = (Map.Entry) iter.next();
+			while ( itr.hasNext() ) {
+				Map.Entry me = (Map.Entry) itr.next();
-	 * @param map
+	 * @param map The map to serialize
-	 * @param o
-	 * @return Map
+	 * @param o the serialized map data
+	 * @return The deserialized map
-	public static Map deserialize(Object o) {
-		return new IdentityMap( (Map) o );
+	@SuppressWarnings( {"unchecked"})
+	public static <K,V> Map<K,V> deserialize(Object o) {
+		return new IdentityMap<K,V>( (Map<IdentityKey<K>,V>) o );
-	public static Map invert(Map map) {
-		Map result = instantiate( map.size() );
-		Iterator iter = map.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			result.put( me.getValue(), me.getKey() );
+	public static <K,V> IdentityMap<V,K> invert(IdentityMap<K,V> map) {
+		IdentityMap<V,K> result = instantiate( map.size() );
+		for ( Entry<K, V> entry : map.entrySet() ) {
+			result.put( entry.getValue(), entry.getKey() );
-	static final class KeyIterator implements Iterator {
+	static final class KeyIterator<K> implements Iterator<K> {
+		private final Iterator<IdentityKey<K>> identityKeyIterator;
-		private KeyIterator(Iterator iter) {
-			identityKeyIterator = iter;
+		private KeyIterator(Iterator<IdentityKey<K>> iterator) {
+			identityKeyIterator = iterator;
-		private final Iterator identityKeyIterator;
-
-		public Object next() {
-			return ( (IdentityKey) identityKeyIterator.next() ).key;
+		public K next() {
+			return identityKeyIterator.next().getRealKey();
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
-		final boolean idIsResultId = optionalId != null
-				&& resolvedId != null
-				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
-		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
-		return resultId;
-	}
-
-					idType.isEqual( id, resultId, session.getEntityMode(), factory );
+					idType.isEqual( id, resultId, factory );
-	        final ResultSet rs,
+			final ResultSet rs,
-	throws HibernateException, SQLException {
-		if ( !persister.isInstance( object, session.getEntityMode() ) ) {
+			throws HibernateException, SQLException {
+		if ( !persister.isInstance( object ) ) {
-						session.getEntityMode(), session.getFactory()
-					);
+						persister.getEntityMode(),
+						session.getFactory()
+				);
-				FilterKey.createFilterKeys(
-						session.getLoadQueryInfluencers().getEnabledFilters(),
-						session.getEntityMode()
-				),
+				FilterKey.createFilterKeys( session.getLoadQueryInfluencers().getEnabledFilters() ),
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
-			.getCollectionBatch( collectionPersister, id, batchSizes[0], session.getEntityMode() );
+				.getCollectionBatch( collectionPersister, id, batchSizes[0] );
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
-					session.getEntityMode(),
-			.getBatchFetchQueue()
-			.getEntityBatch( persister, id, batchSizes[0], session.getEntityMode() );
+				.getBatchFetchQueue()
+				.getEntityBatch( persister, id, batchSizes[0], persister.getEntityMode() );
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
-		if ( mode == EntityMode.DOM4J ) {
-			return nodeName;
-		}
-		else {
-			return getName();
-		}
+		return getName();
--- a/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
-import org.hibernate.EntityMode;
+
-	public Class getMappedClass(EntityMode entityMode);
-
-	/**
-	 * Create a class instance initialized with the given identifier
-	 *
-	 * @deprecated Use {@link #instantiate(Serializable, org.hibernate.engine.spi.SessionImplementor)} instead
-	 * @noinspection JavaDoc
-	 */
-	public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException;
+	public Class getMappedClass();
-	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException;
+	public Object getPropertyValue(Object object, String propertyName) throws HibernateException;
-	 * @param entityMode The entity-mode of the given entity
-	public Object[] getPropertyValues(Object entity, EntityMode entityMode) throws HibernateException;
+	public Object[] getPropertyValues(Object entity) throws HibernateException;
-	public void setPropertyValue(Object object, String propertyName, Object value, EntityMode entityMode) throws HibernateException;
+	public void setPropertyValue(Object object, String propertyName, Object value) throws HibernateException;
-	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException;
+	public void setPropertyValues(Object object, Object[] values) throws HibernateException;
+	 *
-	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException;
+	public Serializable getIdentifier(Object object) throws HibernateException;
-	 * </p>
-	 * Has no effect if the entity does not define an identifier property
-	 *
-	 * @param entity The entity to inject with the identifier value.
-	 * @param id The value to be injected as the identifier.
-	 * @param entityMode The entity mode
-	 *
-	 * @deprecated Use {@link #setIdentifier(Object, Serializable, SessionImplementor)} instead.
-	 * @noinspection JavaDoc
-	 */
-	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException;
-
-	/**
-	 * Inject the identifier value into the given entity.
-	public boolean implementsLifecycle(EntityMode entityMode);
+	public boolean implementsLifecycle();
-	public Object getVersion(Object object, EntityMode entityMode) throws HibernateException;
+	public Object getVersion(Object object) throws HibernateException;
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
-	private final Dom4jEntitySpecifics dom4jEntitySpecifics = new Dom4jEntitySpecifics();
-	public Dom4jEntitySpecifics getDom4jEntitySpecifics() {
-		return dom4jEntitySpecifics;
-	}
-
-	public static class Dom4jEntitySpecifics implements EntityModeEntitySpecifics {
-		private String tuplizerClassName;
-		private String nodeName;
-
-		@Override
-		public EntityMode getEntityMode() {
-			return EntityMode.DOM4J;
-		}
-
-		public String getTuplizerClassName() {
-			return tuplizerClassName;
-		}
-
-		public void setTuplizerClassName(String tuplizerClassName) {
-			this.tuplizerClassName = tuplizerClassName;
-		}
-
-		public String getNodeName() {
-			return nodeName;
-		}
-
-		public void setNodeName(String nodeName) {
-			this.nodeName = nodeName;
-		}
-	}
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
-		bindDom4jRepresentation( entityClazz, entityBinding );
-	private void bindDom4jRepresentation(XMLHibernateMapping.XMLClass entityClazz,
-										 EntityBinding entityBinding) {
-		String nodeName = entityClazz.getNode();
-		if ( nodeName == null ) {
-			nodeName = StringHelper.unqualify( entityBinding.getEntity().getName() );
-		}
-		entityBinding.getEntity().getDom4jEntitySpecifics().setNodeName( nodeName );
-
-		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.DOM4J );
-		if ( tuplizer != null ) {
-			entityBinding.getEntity().getDom4jEntitySpecifics().setTuplizerClassName( tuplizer.getClazz() );
-		}
-	}
-
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
-import org.hibernate.tuple.Tuplizer;
-		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
+		final boolean lazyAvailable = isInstrumented();
-		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
-		if (snapshot != null) {
+		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
+		if ( snapshot != null ) {
-			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
+			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
-					hasUninitializedLazyProperties( object, session.getEntityMode() )
-				);
-			propsToUpdate = getPropertyUpdateability( object, session.getEntityMode() );
+					hasUninitializedLazyProperties( object )
+			);
+			propsToUpdate = getPropertyUpdateability( object );
-				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
+				hasUninitializedLazyProperties( entity ),
-				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
+				hasUninitializedLazyProperties( entity ),
-	protected boolean[] getPropertyUpdateability(Object entity, EntityMode entityMode) {
-		return hasUninitializedLazyProperties( entity, entityMode ) ?
-				getNonLazyPropertyUpdateability() :
-				getPropertyUpdateability();
+	protected boolean[] getPropertyUpdateability(Object entity) {
+		return hasUninitializedLazyProperties( entity )
+				? getNonLazyPropertyUpdateability()
+				: getPropertyUpdateability();
-	protected EntityTuplizer getTuplizer(SessionImplementor session) {
-		return getTuplizer( session.getEntityMode() );
-	}
-
-	protected EntityTuplizer getTuplizer(EntityMode entityMode) {
-		return entityMetamodel.getTuplizer( entityMode );
-	}
-
-		final Object version = getVersion( entity, session.getEntityMode() );
+		final Object version = getVersion( entity );
-		return propertyMapping.toType(propertyName);
+		return propertyMapping.toType( propertyName );
-		return entityMetamodel.getTuplizer( session.getEntityMode() )
-				.createProxy( id, session );
+		return entityMetamodel.getTuplizer().createProxy( id, session );
-	public boolean isInstrumented(EntityMode entityMode) {
-		EntityTuplizer tuplizer = entityMetamodel.getTuplizerOrNull(entityMode);
-		return tuplizer!=null && tuplizer.isInstrumented();
+	public boolean isInstrumented() {
+		return getEntityTuplizer().isInstrumented();
-		getTuplizer( session ).afterInitialize( entity, lazyPropertiesAreUnfetched, session );
+		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
-	public final Class getMappedClass(EntityMode entityMode) {
-		Tuplizer tup = entityMetamodel.getTuplizerOrNull(entityMode);
-		return tup==null ? null : tup.getMappedClass();
+	public final Class getMappedClass() {
+		return getEntityTuplizer().getMappedClass();
-	public boolean implementsLifecycle(EntityMode entityMode) {
-		return getTuplizer( entityMode ).isLifecycleImplementor();
+	public boolean implementsLifecycle() {
+		return getEntityTuplizer().isLifecycleImplementor();
-	public Class getConcreteProxyClass(EntityMode entityMode) {
-		return getTuplizer( entityMode ).getConcreteProxyClass();
+	public Class getConcreteProxyClass() {
+		return getEntityTuplizer().getConcreteProxyClass();
-	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode)
-			throws HibernateException {
-		getTuplizer( entityMode ).setPropertyValues( object, values );
+	public void setPropertyValues(Object object, Object[] values) {
+		getEntityTuplizer().setPropertyValues( object, values );
-	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
-			throws HibernateException {
-		getTuplizer( entityMode ).setPropertyValue( object, i, value );
+	public void setPropertyValue(Object object, int i, Object value) {
+		getEntityTuplizer().setPropertyValue( object, i, value );
-	public Object[] getPropertyValues(Object object, EntityMode entityMode)
-			throws HibernateException {
-		return getTuplizer( entityMode ).getPropertyValues( object );
+	public Object[] getPropertyValues(Object object) {
+		return getEntityTuplizer().getPropertyValues( object );
-	public Object getPropertyValue(Object object, int i, EntityMode entityMode)
-			throws HibernateException {
-		return getTuplizer( entityMode ).getPropertyValue( object , i );
+	@Override
+	public Object getPropertyValue(Object object, int i) {
+		return getEntityTuplizer().getPropertyValue( object, i );
-	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
-			throws HibernateException {
-		return getTuplizer( entityMode ).getPropertyValue( object, propertyName );
+	@Override
+	public Object getPropertyValue(Object object, String propertyName) {
+		return getEntityTuplizer().getPropertyValue( object, propertyName );
-	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
-		return getTuplizer( entityMode ).getIdentifier( object, null );
+	@Override
+	public Serializable getIdentifier(Object object) {
+		return getEntityTuplizer().getIdentifier( object, null );
+	@Override
-		return getTuplizer( session.getEntityMode() ).getIdentifier( entity, session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode)
-			throws HibernateException {
-		getTuplizer( entityMode ).setIdentifier( entity, id, null );
+		return getEntityTuplizer().getIdentifier( entity, session );
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-		getTuplizer( session ).setIdentifier( entity, id, session );
+		getEntityTuplizer().setIdentifier( entity, id, session );
-	public Object getVersion(Object object, EntityMode entityMode)
-			throws HibernateException {
-		return getTuplizer( entityMode ).getVersion( object );
+	@Override
+	public Object getVersion(Object object) {
+		return getEntityTuplizer().getVersion( object );
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object instantiate(Serializable id, EntityMode entityMode)
-			throws HibernateException {
-		return getTuplizer( entityMode ).instantiate( id, null );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object instantiate(Serializable id, SessionImplementor session)
-			throws HibernateException {
-		return getTuplizer( session ).instantiate( id, session );
+	@Override
+	public Object instantiate(Serializable id, SessionImplementor session) {
+		return getEntityTuplizer().instantiate( id, session );
-	public boolean isInstance(Object object, EntityMode entityMode) {
-		return getTuplizer( entityMode ).isInstance( object );
+	@Override
+	public boolean isInstance(Object object) {
+		return getEntityTuplizer().isInstance( object );
-	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
-		return getTuplizer( entityMode ).hasUninitializedLazyProperties( object );
-	}
-
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
-		getTuplizer( entityMode ).resetIdentifier( entity, currentId, currentVersion, null );
+	@Override
+	public boolean hasUninitializedLazyProperties(Object object) {
+		return getEntityTuplizer().hasUninitializedLazyProperties( object );
+	@Override
-		getTuplizer( session ).resetIdentifier( entity, currentId, currentVersion, session );
+		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityPersister getSubclassEntityPersister(
-			Object instance,
-			SessionFactoryImplementor factory,
-			EntityMode entityMode) {
+	@Override
+	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
-			final String concreteEntityName = getTuplizer( entityMode )
-					.determineConcreteSubclassEntityName( instance, factory );
+			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
+					instance,
+					factory
+			);
-	public EntityMode guessEntityMode(Object object) {
-		return entityMetamodel.guessEntityMode(object);
-	}
-
-		return getTuplizer( session.getEntityMode() ).getPropertyValuesToInsert( object, mergeMap, session );
+		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
-							setPropertyValue( entity, i, state[i], session.getEntityMode() );
+							setPropertyValue( entity, i, state[i] );
+
-	public void setPropertyValue(Object object, String propertyName, Object value, EntityMode entityMode)
-			throws HibernateException {
-		getTuplizer( entityMode ).setPropertyValue( object, propertyName, value );
+	public void setPropertyValue(Object object, String propertyName, Object value) {
+		getEntityTuplizer().setPropertyValue( object, propertyName, value );
+	}
+
+	@Override
+	public EntityMode getEntityMode() {
+		return entityMetamodel.getEntityMode();
+	}
+
+	@Override
+	public EntityTuplizer getEntityTuplizer() {
+		return entityMetamodel.getTuplizer();
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
-		if ( EntityMode.POJO.equals( session.getEntityMode() ) ) {
-			return session.getEntityPersister( entityName, null ).getMappedClass( session.getEntityMode() );
+		final EntityPersister entityPersister = session.getEntityPersister( entityName, null );
+		if ( EntityMode.POJO == entityPersister.getEntityMode() ) {
+			return entityPersister.getMappedClass();
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
+
+import org.hibernate.tuple.entity.EntityTuplizer;
-	 * Try to discover the entity mode from the entity instance
-	 */
-	public EntityMode guessEntityMode(Object object);
-
-	/**
-	public boolean isInstrumented(EntityMode entityMode);
+	public boolean isInstrumented();
-	public Class getMappedClass(EntityMode entityMode);
+	public Class getMappedClass();
-	 * Does the class implement the <tt>Lifecycle</tt> interface.
+	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
-	public boolean implementsLifecycle(EntityMode entityMode);
+	public boolean implementsLifecycle();
-	public Class getConcreteProxyClass(EntityMode entityMode);
+	public Class getConcreteProxyClass();
-	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException;
+	public void setPropertyValues(Object object, Object[] values);
-	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException;
+	public void setPropertyValue(Object object, int i, Object value);
-	public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException;
+	public Object[] getPropertyValues(Object object);
-	public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException;
+	public Object getPropertyValue(Object object, int i) throws HibernateException;
-	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException;
+	public Object getPropertyValue(Object object, String propertyName);
+	 *
-	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException;
+	public Serializable getIdentifier(Object object) throws HibernateException;
-     * </p>
-     * Has no effect if the entity does not define an identifier property
-     *
-     * @param entity The entity to inject with the identifier value.
-     * @param id The value to be injected as the identifier.
-	 * @param entityMode The entity mode
-	 *
-	 * @deprecated Use {@link #setIdentifier(Object, Serializable, SessionImplementor)} instead.
-	 * @noinspection JavaDoc
-     */
-	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException;
-
-    /**
-     * Inject the identifier value into the given entity.
-	public Object getVersion(Object object, EntityMode entityMode) throws HibernateException;
-
-	/**
-	 * Create a class instance initialized with the given identifier
-	 *
-	 * @deprecated Use {@link #instantiate(Serializable, SessionImplementor)} instead
-	 * @noinspection JavaDoc
-	 */
-	public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException;
+	public Object getVersion(Object object) throws HibernateException;
-	public boolean isInstance(Object object, EntityMode entityMode);
+	public boolean isInstance(Object object);
-	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode);
-
-	/**
-	 * Set the identifier and version of the given instance back to its "unsaved" value.
-	 *
-	 * @param entity The entity instance
-	 * @param currentId The currently assigned identifier value.
-	 * @param currentVersion The currently assigned version value.
-	 * @param entityMode The entity mode represented by the entity instance.
-	 *
-	 * @deprecated Use {@link #resetIdentifier(Object, Serializable, Object, SessionImplementor)} instead
-	 */
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode);
+	public boolean hasUninitializedLazyProperties(Object object);
-	 * @param entityMode The entity mode represented by the entity instance.
-	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode);
+	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
+
+	public EntityMode getEntityMode();
+	public EntityTuplizer getEntityTuplizer();
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
-		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
+		final boolean lazyAvailable = isInstrumented();
--- a/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
-import java.util.ArrayList;
+
-import java.util.List;
-import org.hibernate.EntityMode;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
-import org.hibernate.metadata.ClassMetadata;
+import org.hibernate.persister.entity.EntityPersister;
-import org.jboss.logging.Logger;
-	public String toString(Object entity, EntityMode entityMode) throws HibernateException {
-
+	public String toString(Object entity) throws HibernateException {
-		ClassMetadata cm = factory.getClassMetadata( entity.getClass() );
+		// need a means to access the entity name resolver(s).  problem is accounting for session interceptor from here...
+		EntityPersister entityPersister = factory.getEntityPersister( entity.getClass().getName() );
-		if ( cm==null ) return entity.getClass().getName();
+		if ( entityPersister == null ) {
+			return entity.getClass().getName();
+		}
-		Map result = new HashMap();
+		Map<String,String> result = new HashMap<String,String>();
-		if ( cm.hasIdentifierProperty() ) {
+		if ( entityPersister.hasIdentifierProperty() ) {
-				cm.getIdentifierPropertyName(),
-				cm.getIdentifierType().toLoggableString( cm.getIdentifier( entity, entityMode ), factory )
+				entityPersister.getIdentifierPropertyName(),
+				entityPersister.getIdentifierType().toLoggableString( entityPersister.getIdentifier( entity ), factory )
-		Type[] types = cm.getPropertyTypes();
-		String[] names = cm.getPropertyNames();
-		Object[] values = cm.getPropertyValues( entity, entityMode );
+		Type[] types = entityPersister.getPropertyTypes();
+		String[] names = entityPersister.getPropertyNames();
+		Object[] values = entityPersister.getPropertyValues( entity );
-		return cm.getEntityName() + result.toString();
+		return entityPersister.getEntityName() + result.toString();
-		List list = new ArrayList( types.length * 5 );
+		StringBuilder buffer = new StringBuilder();
-			if ( types[i]!=null ) list.add( types[i].toLoggableString( values[i], factory ) );
+			if ( types[i]!=null ) {
+				buffer.append( types[i].toLoggableString( values[i], factory ) ).append( ", " );
+			}
-		return list.toString();
+		return buffer.toString();
-	public String toString(Map namedTypedValues) throws HibernateException {
-		Map result = new HashMap();
-		Iterator iter = namedTypedValues.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			TypedValue tv = (TypedValue) me.getValue();
-			result.put( me.getKey(), tv.getType().toLoggableString( tv.getValue(), factory ) );
+	public String toString(Map<String,TypedValue> namedTypedValues) throws HibernateException {
+		Map<String,String> result = new HashMap<String,String>();
+		for ( Map.Entry<String, TypedValue> entry : namedTypedValues.entrySet() ) {
+			result.put(
+					entry.getKey(), entry.getValue().getType().toLoggableString(
+					entry.getValue().getValue(),
+					factory
+			)
+			);
-	public void toString(Iterator iter, EntityMode entityMode) throws HibernateException {
-        if (!LOG.isDebugEnabled() || !iter.hasNext()) return;
-        LOG.debugf("Listing entities:");
+	public void toString(Iterator iterator) throws HibernateException {
+        if (!LOG.isDebugEnabled() || !iterator.hasNext()) return;
+        LOG.debugf( "Listing entities:" );
-		while ( iter.hasNext() ) {
+		while ( iterator.hasNext() ) {
-            LOG.debugf(toString(iter.next(), entityMode));
+            LOG.debugf( toString( iterator.next() ) );
--- a/hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
- * third-party contributors as indicated by either @author tags or express
- * copyright attribution statements applied by the authors.  All
- * third-party contributions are distributed under license by Red Hat Inc.
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
-package org.hibernate.property;
-import java.lang.reflect.Member;
-import java.lang.reflect.Method;
-import java.util.Map;
-import org.dom4j.Attribute;
-import org.dom4j.Element;
-import org.dom4j.Node;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.PropertyNotFoundException;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.type.CollectionType;
-import org.hibernate.type.Type;
-
-/**
- * Responsible for accessing property values represented as a dom4j Element
- * or Attribute.
- *
- * @author Steve Ebersole
- */
-public class Dom4jAccessor implements PropertyAccessor {
-	private String nodeName;
-	private Type propertyType;
-	private final SessionFactoryImplementor factory;
-
-	public Dom4jAccessor(String nodeName, Type propertyType, SessionFactoryImplementor factory) {
-		this.factory = factory;
-		this.nodeName = nodeName;
-		this.propertyType = propertyType;
-	}
-
-	/**
-	 * Create a "getter" for the named attribute
-	 */
-	public Getter getGetter(Class theClass, String propertyName) 
-	throws PropertyNotFoundException {
-		if (nodeName==null) {
-			throw new MappingException("no node name for property: " + propertyName);
-		}
-		if ( ".".equals(nodeName) ) {
-			return new TextGetter(propertyType, factory);
-		}
-		else if ( nodeName.indexOf('/')>-1 ) {
-			return new ElementAttributeGetter(nodeName, propertyType, factory);
-		}
-		else if ( nodeName.indexOf('@')>-1 ) {
-			return new AttributeGetter(nodeName, propertyType, factory);
-		}
-		else {
-			return new ElementGetter(nodeName, propertyType, factory);
-		}
-	}
-
-	/**
-	 * Create a "setter" for the named attribute
-	 */
-	public Setter getSetter(Class theClass, String propertyName) 
-	throws PropertyNotFoundException {
-		if (nodeName==null) {
-			throw new MappingException("no node name for property: " + propertyName);
-		}
-		if ( ".".equals(nodeName) ) {
-			return new TextSetter(propertyType);
-		}
-		else if ( nodeName.indexOf('/')>-1 ) {
-			return new ElementAttributeSetter(nodeName, propertyType);
-		}
-		else if ( nodeName.indexOf('@')>-1 ) {
-			return new AttributeSetter(nodeName, propertyType);
-		}
-		else {
-			return new ElementSetter(nodeName, propertyType);
-		}
-	}
-
-	/**
-	 * Defines the strategy for getting property values out of a dom4j Node.
-	 */
-	public abstract static class Dom4jGetter implements Getter {
-		protected final Type propertyType;
-		protected final SessionFactoryImplementor factory;
-		
-		Dom4jGetter(Type propertyType, SessionFactoryImplementor factory) {
-			this.propertyType = propertyType;
-			this.factory = factory;
-		}
-		
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session)
-		throws HibernateException {
-			return get( owner );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Class getReturnType() {
-			return Object.class;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Member getMember() {
-			return null;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public String getMethodName() {
-			return null;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Method getMethod() {
-			return null;
-		}
-	}
-
-	public abstract static class Dom4jSetter implements Setter {
-		protected final Type propertyType;
-
-		Dom4jSetter(Type propertyType) {
-			this.propertyType = propertyType;
-		}
-		
-		/**
-		 * {@inheritDoc}
-		 */
-		public String getMethodName() {
-			return null;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Method getMethod() {
-			return null;
-		}
-	}
-	
-	/**
-	 * For nodes like <tt>"."</tt>
-	 * @author Gavin King
-	 */
-	public static class TextGetter extends Dom4jGetter {
-		TextGetter(Type propertyType, SessionFactoryImplementor factory) {
-			super(propertyType, factory);
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Object get(Object owner) throws HibernateException {
-			Element ownerElement = (Element) owner;
-			return super.propertyType.fromXMLNode(ownerElement, super.factory);
-		}	
-	}
-	
-	/**
-	 * For nodes like <tt>"@bar"</tt>
-	 * @author Gavin King
-	 */
-	public static class AttributeGetter extends Dom4jGetter {
-		private final String attributeName;
-
-		AttributeGetter(String name, Type propertyType, SessionFactoryImplementor factory) {
-			super(propertyType, factory);
-			attributeName = name.substring(1);
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Object get(Object owner) throws HibernateException {
-			Element ownerElement = (Element) owner;
-			Node attribute = ownerElement.attribute(attributeName);
-			return attribute==null ? null : 
-				super.propertyType.fromXMLNode(attribute, super.factory);
-		}	
-	}
-
-	/**
-	 * For nodes like <tt>"foo"</tt>
-	 * @author Gavin King
-	 */
-	public static class ElementGetter extends Dom4jGetter {
-		private final String elementName;
-		
-		ElementGetter(String name, Type propertyType, SessionFactoryImplementor factory) {
-			super(propertyType, factory);
-			elementName = name;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Object get(Object owner) throws HibernateException {
-			Element ownerElement = (Element) owner;
-			Node element = ownerElement.element(elementName);
-			return element==null ? 
-					null : super.propertyType.fromXMLNode(element, super.factory);
-		}	
-	}
-	
-	/**
-	 * For nodes like <tt>"foo/@bar"</tt>
-	 * @author Gavin King
-	 */
-	public static class ElementAttributeGetter extends Dom4jGetter {
-		private final String elementName;
-		private final String attributeName;
-
-		ElementAttributeGetter(String name, Type propertyType, SessionFactoryImplementor factory) {
-			super(propertyType, factory);
-			elementName = name.substring( 0, name.indexOf('/') );
-			attributeName = name.substring( name.indexOf('/')+2 );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public Object get(Object owner) throws HibernateException {
-			Element ownerElement = (Element) owner;
-			
-			Element element = ownerElement.element(elementName);
-			
-			if ( element==null ) {
-				return null;
-			}
-			else {
-				Attribute attribute = element.attribute(attributeName);
-				if (attribute==null) {
-					return null;
-				}
-				else {
-					return super.propertyType.fromXMLNode(attribute, super.factory);
-				}
-			}
-		}
-	}
-	
-	
-	/**
-	 * For nodes like <tt>"."</tt>
-	 * @author Gavin King
-	 */
-	public static class TextSetter extends Dom4jSetter {
-		TextSetter(Type propertyType) {
-			super(propertyType);
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		throws HibernateException {
-			Element owner = ( Element ) target;
-			if ( !super.propertyType.isXMLElement() ) { //kinda ugly, but needed for collections with a "." node mapping
-				if (value==null) {
-					owner.setText(null); //is this ok?
-				}
-				else {
-					super.propertyType.setToXMLNode(owner, value, factory);
-				}
-			}
-		}
-	}
-	
-	/**
-	 * For nodes like <tt>"@bar"</tt>
-	 * @author Gavin King
-	 */
-	public static class AttributeSetter extends Dom4jSetter {
-		private final String attributeName;
-
-		AttributeSetter(String name, Type propertyType) {
-			super(propertyType);
-			attributeName = name.substring(1);
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		throws HibernateException {
-			Element owner = ( Element ) target;
-			Attribute attribute = owner.attribute(attributeName);
-			if (value==null) {
-				if (attribute!=null) attribute.detach();
-			}
-			else {
-				if (attribute==null) {
-					owner.addAttribute(attributeName, "null");
-					attribute = owner.attribute(attributeName);
-				}
-				super.propertyType.setToXMLNode(attribute, value, factory);
-			}
-		}
-	}
-	
-	/**
-	 * For nodes like <tt>"foo"</tt>
-	 * @author Gavin King
-	 */
-	public static class ElementSetter extends Dom4jSetter {
-		private final String elementName;
-		
-		ElementSetter(String name, Type propertyType) {
-			super(propertyType);
-			elementName = name;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		throws HibernateException {
-			if (value!=CollectionType.UNFETCHED_COLLECTION) {
-				Element owner = ( Element ) target;
-				Element existing = owner.element(elementName);
-				if (existing!=null) existing.detach();
-				if (value!=null) {
-					Element element = owner.addElement(elementName);
-					super.propertyType.setToXMLNode(element, value, factory);
-				}
-			}
-		}
-	}
-	
-	/**
-	 * For nodes like <tt>"foo/@bar"</tt>
-	 * @author Gavin King
-	 */
-	public static class ElementAttributeSetter extends Dom4jSetter {
-		private final String elementName;
-		private final String attributeName;
-		
-		ElementAttributeSetter(String name, Type propertyType) {
-			super(propertyType);
-			elementName = name.substring( 0, name.indexOf('/') );
-			attributeName = name.substring( name.indexOf('/')+2 );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		throws HibernateException {
-			Element owner = ( Element ) target;
-			Element element = owner.element(elementName);
-			if (value==null) {
-				if (element!=null) element.detach();
-			}
-			else {
-				Attribute attribute;
-				if (element==null) {
-					element = owner.addElement(elementName);
-					attribute = null;
-				}
-				else {
-					attribute = element.attribute(attributeName);
-				}
-				
-				if (attribute==null) {
-					element.addAttribute(attributeName, "null");
-					attribute = element.attribute(attributeName);
-				}				
-				super.propertyType.setToXMLNode(attribute, value, factory);
-			}
-		}
-	}
-	
-
-}
--- a/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	    else if ( EntityMode.DOM4J.equals( mode ) ) {
-	    	//TODO: passing null here, because this method is not really used for DOM4J at the moment
-	    	//      but it is still a bug, if we don't get rid of this!
-		    return getDom4jPropertyAccessor( property.getAccessorPropertyName( mode ), property.getType(), null );
-	    }
-	public static PropertyAccessor getDom4jPropertyAccessor(String nodeName, Type type, SessionFactoryImplementor factory)
-	throws MappingException {
-		//TODO: need some caching scheme? really comes down to decision
-		//      regarding amount of state (if any) kept on PropertyAccessors
-		return new Dom4jAccessor( nodeName, type, factory );
-	}
-
--- a/hibernate-core/src/main/java/org/hibernate/tuple/EntityModeToTuplizerMapping.java
+++ /dev/null
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
-package org.hibernate.tuple;
-import java.io.Serializable;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.concurrent.ConcurrentHashMap;
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
-
-/**
- * Centralizes handling of {@link EntityMode} to {@link Tuplizer} mappings.
- *
- * @author Steve Ebersole
- */
-public abstract class EntityModeToTuplizerMapping implements Serializable {
-	private final Map<EntityMode,Tuplizer> tuplizers;
-
-	public EntityModeToTuplizerMapping() {
-		tuplizers = new ConcurrentHashMap<EntityMode,Tuplizer>();
-	}
-
-	@SuppressWarnings({ "unchecked", "UnusedDeclaration" })
-	public EntityModeToTuplizerMapping(Map tuplizers) {
-		this.tuplizers = tuplizers;
-	}
-
-	protected void addTuplizer(EntityMode entityMode, Tuplizer tuplizer) {
-		tuplizers.put( entityMode, tuplizer );
-	}
-
-	/**
-	 * Allow iteration over all defined {@link Tuplizer Tuplizers}.
-	 *
-	 * @return Iterator over defined tuplizers
-	 */
-	public Iterator iterateTuplizers() {
-		return tuplizers.values().iterator();
-	}
-
-	/**
-	 * Given a supposed instance of an entity/component, guess its entity mode.
-	 *
-	 * @param object The supposed instance of the entity/component.
-	 * @return The guessed entity mode.
-	 */
-	public EntityMode guessEntityMode(Object object) {
-		for ( Map.Entry<EntityMode, Tuplizer> entityModeTuplizerEntry : tuplizers.entrySet() ) {
-			if ( entityModeTuplizerEntry.getValue().isInstance( object ) ) {
-				return entityModeTuplizerEntry.getKey();
-			}
-		}
-		return null;
-	}
-
-	/**
-	 * Locate the contained tuplizer responsible for the given entity-mode.  If
-	 * no such tuplizer is defined on this mapping, then return null.
-	 *
-	 * @param entityMode The entity-mode for which the caller wants a tuplizer.
-	 * @return The tuplizer, or null if not found.
-	 */
-	public Tuplizer getTuplizerOrNull(EntityMode entityMode) {
-		return tuplizers.get( entityMode );
-	}
-
-	/**
-	 * Locate the tuplizer contained within this mapping which is responsible
-	 * for the given entity-mode.  If no such tuplizer is defined on this
-	 * mapping, then an exception is thrown.
-	 *
-	 * @param entityMode The entity-mode for which the caller wants a tuplizer.
-	 * @return The tuplizer.
-	 * @throws HibernateException Unable to locate the requested tuplizer.
-	 */
-	public Tuplizer getTuplizer(EntityMode entityMode) {
-		Tuplizer tuplizer = getTuplizerOrNull( entityMode );
-		if ( tuplizer == null ) {
-			throw new HibernateException( "No tuplizer found for entity-mode [" + entityMode + "]");
-		}
-		return tuplizer;
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentEntityModeToTuplizerMapping.java
+++ /dev/null
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
-package org.hibernate.tuple.component;
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import org.hibernate.EntityMode;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.tuple.EntityModeToTuplizerMapping;
-import org.hibernate.tuple.Tuplizer;
-
-/**
- * Handles mapping {@link EntityMode}s to {@link ComponentTuplizer}s.
- * <p/>
- * Most of the handling is really in the super class; here we just create
- * the tuplizers and add them to the superclass
- *
- * @author Steve Ebersole
- */
-class ComponentEntityModeToTuplizerMapping extends EntityModeToTuplizerMapping implements Serializable {
-
-	// todo : move this to SF per HHH-3517; also see HHH-1907 and ComponentMetamodel
-	private ComponentTuplizerFactory componentTuplizerFactory = new ComponentTuplizerFactory();
-
-	public ComponentEntityModeToTuplizerMapping(Component component) {
-		PersistentClass owner = component.getOwner();
-
-		// create our own copy of the user-supplied tuplizer impl map
-		Map userSuppliedTuplizerImpls = new HashMap();
-		if ( component.getTuplizerMap() != null ) {
-			userSuppliedTuplizerImpls.putAll( component.getTuplizerMap() );
-		}
-
-		// Build the dynamic-map tuplizer...
-		Tuplizer dynamicMapTuplizer;
-		String tuplizerClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.MAP );
-		if ( tuplizerClassName == null ) {
-			dynamicMapTuplizer = componentTuplizerFactory.constructDefaultTuplizer( EntityMode.MAP, component );
-		}
-		else {
-			dynamicMapTuplizer = componentTuplizerFactory.constructTuplizer( tuplizerClassName, component );
-		}
-
-		// then the pojo tuplizer, using the dynamic-map tuplizer if no pojo representation is available
-		tuplizerClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.POJO );
-		Tuplizer pojoTuplizer;
-		if ( owner.hasPojoRepresentation() && component.hasPojoRepresentation() ) {
-			if ( tuplizerClassName == null ) {
-				pojoTuplizer = componentTuplizerFactory.constructDefaultTuplizer( EntityMode.POJO, component );
-			}
-			else {
-				pojoTuplizer = componentTuplizerFactory.constructTuplizer( tuplizerClassName, component );
-			}
-		}
-		else {
-			pojoTuplizer = dynamicMapTuplizer;
-		}
-
-		// then dom4j tuplizer, if dom4j representation is available
-		Tuplizer dom4jTuplizer;
-		tuplizerClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.DOM4J );
-		if ( owner.hasDom4jRepresentation() ) {
-			if ( tuplizerClassName == null ) {
-				dom4jTuplizer = componentTuplizerFactory.constructDefaultTuplizer( EntityMode.DOM4J, component );
-			}
-			else {
-				dom4jTuplizer = componentTuplizerFactory.constructTuplizer( tuplizerClassName, component );
-			}
-		}
-		else {
-			dom4jTuplizer = null;
-		}
-
-		// put the "standard" tuplizers into the tuplizer map first
-		if ( pojoTuplizer != null ) {
-			addTuplizer( EntityMode.POJO, pojoTuplizer );
-		}
-		if ( dynamicMapTuplizer != null ) {
-			addTuplizer( EntityMode.MAP, dynamicMapTuplizer );
-		}
-		if ( dom4jTuplizer != null ) {
-			addTuplizer( EntityMode.DOM4J, dom4jTuplizer );
-		}
-
-		// then handle any user-defined entity modes...
-		if ( !userSuppliedTuplizerImpls.isEmpty() ) {
-			Iterator itr = userSuppliedTuplizerImpls.entrySet().iterator();
-			while ( itr.hasNext() ) {
-				final Map.Entry entry = ( Map.Entry ) itr.next();
-				final EntityMode entityMode = ( EntityMode ) entry.getKey();
-				final String userTuplizerClassName = ( String ) entry.getValue();
-				ComponentTuplizer tuplizer = componentTuplizerFactory.constructTuplizer( userTuplizerClassName, component );
-				addTuplizer( entityMode, tuplizer );
-			}
-		}
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
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
-package org.hibernate.tuple.component;
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import org.hibernate.HibernateException;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.Property;
-import org.hibernate.tuple.PropertyFactory;
-import org.hibernate.tuple.StandardProperty;
-
-/**
- * Centralizes metamodel information about a component.
- *
- * @author Steve Ebersole
- */
-public class ComponentMetamodel implements Serializable {
-
-	// TODO : will need reference to session factory to fully complete HHH-1907
-
-//	private final SessionFactoryImplementor sessionFactory;
-	private final String role;
-	private final boolean isKey;
-	private final StandardProperty[] properties;
-	private final ComponentEntityModeToTuplizerMapping tuplizerMapping;
-
-	// cached for efficiency...
-	private final int propertySpan;
-	private final Map propertyIndexes = new HashMap();
-
-//	public ComponentMetamodel(Component component, SessionFactoryImplementor sessionFactory) {
-	public ComponentMetamodel(Component component) {
-//		this.sessionFactory = sessionFactory;
-		this.role = component.getRoleName();
-		this.isKey = component.isKey();
-		propertySpan = component.getPropertySpan();
-		properties = new StandardProperty[propertySpan];
-		Iterator itr = component.getPropertyIterator();
-		int i = 0;
-		while ( itr.hasNext() ) {
-			Property property = ( Property ) itr.next();
-			properties[i] = PropertyFactory.buildStandardProperty( property, false );
-			propertyIndexes.put( property.getName(), i );
-			i++;
-		}
-
-		tuplizerMapping = new ComponentEntityModeToTuplizerMapping( component );
-	}
-
-	public boolean isKey() {
-		return isKey;
-	}
-
-	public int getPropertySpan() {
-		return propertySpan;
-	}
-
-	public StandardProperty[] getProperties() {
-		return properties;
-	}
-
-	public StandardProperty getProperty(int index) {
-		if ( index < 0 || index >= propertySpan ) {
-			throw new IllegalArgumentException( "illegal index value for component property access [request=" + index + ", span=" + propertySpan + "]" );
-		}
-		return properties[index];
-	}
-
-	public int getPropertyIndex(String propertyName) {
-		Integer index = ( Integer ) propertyIndexes.get( propertyName );
-		if ( index == null ) {
-			throw new HibernateException( "component does not contain such a property [" + propertyName + "]" );
-		}
-		return index.intValue();
-	}
-
-	public StandardProperty getProperty(String propertyName) {
-		return getProperty( getPropertyIndex( propertyName ) );
-	}
-
-	public ComponentEntityModeToTuplizerMapping getTuplizerMapping() {
-		return tuplizerMapping;
-	}
-
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.tuple.component;
+import java.io.Serializable;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.mapping.Component;
+import org.hibernate.mapping.Property;
+import org.hibernate.tuple.PropertyFactory;
+import org.hibernate.tuple.StandardProperty;
+import org.hibernate.tuple.Tuplizer;
+
+/**
+ * Centralizes metamodel information about a component.
+ *
+ * @author Steve Ebersole
+ */
+public class ComponentMetamodel implements Serializable {
+
+	// TODO : will need reference to session factory to fully complete HHH-1907
+
+//	private final SessionFactoryImplementor sessionFactory;
+	private final String role;
+	private final boolean isKey;
+	private final StandardProperty[] properties;
+
+	private final EntityMode entityMode;
+	private final ComponentTuplizer componentTuplizer;
+
+	// cached for efficiency...
+	private final int propertySpan;
+	private final Map propertyIndexes = new HashMap();
+
+//	public ComponentMetamodel(Component component, SessionFactoryImplementor sessionFactory) {
+	public ComponentMetamodel(Component component) {
+//		this.sessionFactory = sessionFactory;
+		this.role = component.getRoleName();
+		this.isKey = component.isKey();
+		propertySpan = component.getPropertySpan();
+		properties = new StandardProperty[propertySpan];
+		Iterator itr = component.getPropertyIterator();
+		int i = 0;
+		while ( itr.hasNext() ) {
+			Property property = ( Property ) itr.next();
+			properties[i] = PropertyFactory.buildStandardProperty( property, false );
+			propertyIndexes.put( property.getName(), i );
+			i++;
+		}
+
+		entityMode = component.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
+
+		// todo : move this to SF per HHH-3517; also see HHH-1907 and ComponentMetamodel
+		final ComponentTuplizerFactory componentTuplizerFactory = new ComponentTuplizerFactory();
+		final String tuplizerClassName = component.getTuplizerImplClassName( entityMode );
+		if ( tuplizerClassName == null ) {
+			componentTuplizer = componentTuplizerFactory.constructDefaultTuplizer( entityMode, component );
+		}
+		else {
+			componentTuplizer = componentTuplizerFactory.constructTuplizer( tuplizerClassName, component );
+		}
+	}
+
+	public boolean isKey() {
+		return isKey;
+	}
+
+	public int getPropertySpan() {
+		return propertySpan;
+	}
+
+	public StandardProperty[] getProperties() {
+		return properties;
+	}
+
+	public StandardProperty getProperty(int index) {
+		if ( index < 0 || index >= propertySpan ) {
+			throw new IllegalArgumentException( "illegal index value for component property access [request=" + index + ", span=" + propertySpan + "]" );
+		}
+		return properties[index];
+	}
+
+	public int getPropertyIndex(String propertyName) {
+		Integer index = ( Integer ) propertyIndexes.get( propertyName );
+		if ( index == null ) {
+			throw new HibernateException( "component does not contain such a property [" + propertyName + "]" );
+		}
+		return index.intValue();
+	}
+
+	public StandardProperty getProperty(String propertyName) {
+		return getProperty( getPropertyIndex( propertyName ) );
+	}
+
+	public EntityMode getEntityMode() {
+		return entityMode;
+	}
+
+	public ComponentTuplizer getComponentTuplizer() {
+		return componentTuplizer;
+	}
+
+}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
-		map.put( EntityMode.DOM4J, Dom4jComponentTuplizer.class );
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/Dom4jComponentTuplizer.java
+++ /dev/null
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
-package org.hibernate.tuple.component;
-import org.dom4j.Element;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessor;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-import org.hibernate.tuple.Dom4jInstantiator;
-import org.hibernate.tuple.Instantiator;
-
-/**
- * A {@link ComponentTuplizer} specific to the dom4j entity mode.
- *
- * @author Gavin King
- * @author Steve Ebersole
- */
-public class Dom4jComponentTuplizer extends AbstractComponentTuplizer  {
-
-	public Class getMappedClass() {
-		return Element.class;
-	}
-
-	public Dom4jComponentTuplizer(Component component) {
-		super(component);
-	}
-
-	protected Instantiator buildInstantiator(Component component) {
-		return new Dom4jInstantiator( component );
-	}
-
-	private PropertyAccessor buildPropertyAccessor(Property property) {
-		//TODO: currently we don't know a SessionFactory reference when building the Tuplizer
-		//      THIS IS A BUG (embedded-xml=false on component)
-		// TODO : fix this after HHH-1907 is complete
-		return PropertyAccessorFactory.getDom4jPropertyAccessor( property.getNodeName(), property.getType(), null );
-	}
-
-	protected Getter buildGetter(Component component, Property prop) {
-		return buildPropertyAccessor(prop).getGetter( null, prop.getName() );
-	}
-
-	protected Setter buildSetter(Component component, Property prop) {
-		return buildPropertyAccessor(prop).getSetter( null, prop.getName() );
-	}
-
-}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
-			virtualIdComponent.setPropertyValues( entity, injectionValues, session.getEntityMode() );
+			virtualIdComponent.setPropertyValues( entity, injectionValues, entityMode );
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
+++ /dev/null
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
-package org.hibernate.tuple.entity;
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Map;
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.EntityNameResolver;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessor;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.proxy.dom4j.Dom4jProxyFactory;
-import org.hibernate.tuple.Dom4jInstantiator;
-import org.hibernate.tuple.Instantiator;
-import org.hibernate.type.CompositeType;
-import org.jboss.logging.Logger;
-
-/**
- * An {@link EntityTuplizer} specific to the dom4j entity mode.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public class Dom4jEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dom4jEntityTuplizer.class.getName());
-
-	private Map inheritenceNodeNameMap = new HashMap();
-
-	Dom4jEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
-		super( entityMetamodel, mappedEntity );
-		inheritenceNodeNameMap.put( mappedEntity.getNodeName(), mappedEntity.getEntityName() );
-		Iterator itr = mappedEntity.getSubclassClosureIterator();
-		while( itr.hasNext() ) {
-			final PersistentClass mapping = ( PersistentClass ) itr.next();
-			inheritenceNodeNameMap.put( mapping.getNodeName(), mapping.getEntityName() );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityMode getEntityMode() {
-		return EntityMode.DOM4J;
-	}
-
-	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
-		if ( mappedProperty.isBackRef() ) {
-			return mappedProperty.getPropertyAccessor(null);
-		}
-		else {
-			return PropertyAccessorFactory.getDom4jPropertyAccessor(
-					mappedProperty.getNodeName(),
-					mappedProperty.getType(),
-					getEntityMetamodel().getSessionFactory()
-				);
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Instantiator buildInstantiator(PersistentClass persistentClass) {
-		return new Dom4jInstantiator( persistentClass );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Serializable getIdentifier(Object entityOrId) throws HibernateException {
-		return getIdentifier( entityOrId, null );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Serializable getIdentifier(Object entityOrId, SessionImplementor session) {
-		if ( entityOrId instanceof Element ) {
-			return super.getIdentifier( entityOrId, session );
-		}
-		else {
-			//it was not embedded, so the argument is just an id
-			return (Serializable) entityOrId;
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
-		HashSet proxyInterfaces = new HashSet();
-		proxyInterfaces.add( HibernateProxy.class );
-		proxyInterfaces.add( Element.class );
-
-		ProxyFactory pf = new Dom4jProxyFactory();
-		try {
-			pf.postInstantiate(
-					getEntityName(),
-					Element.class,
-					proxyInterfaces,
-					null,
-					null,
-					mappingInfo.hasEmbeddedIdentifier() ?
-			                (CompositeType) mappingInfo.getIdentifier().getType() :
-			                null
-			);
-		}
-		catch ( HibernateException he ) {
-            LOG.unableToCreateProxyFactory(getEntityName(), he);
-			pf = null;
-		}
-		return pf;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getMappedClass() {
-		return Element.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getConcreteProxyClass() {
-		return Element.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isInstrumented() {
-		return false;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityNameResolver[] getEntityNameResolvers() {
-		return new EntityNameResolver[] { new BasicEntityNameResolver( getEntityName(), inheritenceNodeNameMap ) };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
-		return ( String ) inheritenceNodeNameMap.get( extractNodeName( ( Element ) entityInstance ) );
-	}
-
-	public static String extractNodeName(Element element) {
-		return element.getName();
-	}
-
-	public static class BasicEntityNameResolver implements EntityNameResolver {
-		private final String rootEntityName;
-		private final Map nodeNameToEntityNameMap;
-
-		public BasicEntityNameResolver(String rootEntityName, Map nodeNameToEntityNameMap) {
-			this.rootEntityName = rootEntityName;
-			this.nodeNameToEntityNameMap = nodeNameToEntityNameMap;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public String resolveEntityName(Object entity) {
-		return ( String ) nodeNameToEntityNameMap.get( extractNodeName( ( Element ) entity ) );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public boolean equals(Object obj) {
-			return rootEntityName.equals( ( ( BasicEntityNameResolver ) obj ).rootEntityName );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public int hashCode() {
-			return rootEntityName.hashCode();
-		}
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+			if ( ! Map.class.isInstance( entity ) ) {
+				return null;
+			}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityEntityModeToTuplizerMapping.java
+++ /dev/null
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
-package org.hibernate.tuple.entity;
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import org.hibernate.EntityMode;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.tuple.EntityModeToTuplizerMapping;
-import org.hibernate.tuple.Tuplizer;
-
-/**
- * Handles mapping {@link EntityMode}s to {@link EntityTuplizer}s.
- * <p/>
- * Most of the handling is really in the super class; here we just create
- * the tuplizers and add them to the superclass
- *
- * @author Steve Ebersole
- */
-public class EntityEntityModeToTuplizerMapping extends EntityModeToTuplizerMapping implements Serializable {
-
-	/**
-	 * Instantiates a EntityEntityModeToTuplizerMapping based on the given
-	 * entity mapping and metamodel definitions.
-	 *
-	 * @param mappedEntity The entity mapping definition.
-	 * @param em The entity metamodel definition.
-	 */
-	public EntityEntityModeToTuplizerMapping(PersistentClass mappedEntity, EntityMetamodel em) {
-		final EntityTuplizerFactory entityTuplizerFactory = em.getSessionFactory()
-				.getSettings()
-				.getEntityTuplizerFactory();
-
-		// create our own copy of the user-supplied tuplizer impl map
-		Map userSuppliedTuplizerImpls = new HashMap();
-		if ( mappedEntity.getTuplizerMap() != null ) {
-			userSuppliedTuplizerImpls.putAll( mappedEntity.getTuplizerMap() );
-		}
-
-		// Build the dynamic-map tuplizer...
-		Tuplizer dynamicMapTuplizer;
-		String tuplizerImplClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.MAP );
-		if ( tuplizerImplClassName == null ) {
-			dynamicMapTuplizer = entityTuplizerFactory.constructDefaultTuplizer( EntityMode.MAP, em, mappedEntity );
-		}
-		else {
-			dynamicMapTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerImplClassName, em, mappedEntity );
-		}
-
-		// then the pojo tuplizer, using the dynamic-map tuplizer if no pojo representation is available
-		Tuplizer pojoTuplizer;
-		tuplizerImplClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.POJO );
-		if ( mappedEntity.hasPojoRepresentation() ) {
-			if ( tuplizerImplClassName == null ) {
-				pojoTuplizer = entityTuplizerFactory.constructDefaultTuplizer( EntityMode.POJO, em, mappedEntity );
-			}
-			else {
-				pojoTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerImplClassName, em, mappedEntity );
-			}
-		}
-		else {
-			pojoTuplizer = dynamicMapTuplizer;
-		}
-
-		// then dom4j tuplizer, if dom4j representation is available
-		Tuplizer dom4jTuplizer;
-		tuplizerImplClassName = ( String ) userSuppliedTuplizerImpls.remove( EntityMode.DOM4J );
-		if ( mappedEntity.hasDom4jRepresentation() ) {
-			if ( tuplizerImplClassName == null ) {
-				dom4jTuplizer = entityTuplizerFactory.constructDefaultTuplizer( EntityMode.DOM4J, em, mappedEntity );
-			}
-			else {
-				dom4jTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerImplClassName, em, mappedEntity );
-			}
-		}
-		else {
-			dom4jTuplizer = null;
-		}
-
-		// put the "standard" tuplizers into the tuplizer map first
-		if ( pojoTuplizer != null ) {
-			addTuplizer( EntityMode.POJO, pojoTuplizer );
-		}
-		if ( dynamicMapTuplizer != null ) {
-			addTuplizer( EntityMode.MAP, dynamicMapTuplizer );
-		}
-		if ( dom4jTuplizer != null ) {
-			addTuplizer( EntityMode.DOM4J, dom4jTuplizer );
-		}
-
-		// then handle any user-defined entity modes...
-		if ( !userSuppliedTuplizerImpls.isEmpty() ) {
-			Iterator itr = userSuppliedTuplizerImpls.entrySet().iterator();
-			while ( itr.hasNext() ) {
-				final Map.Entry entry = ( Map.Entry ) itr.next();
-				final EntityMode entityMode = ( EntityMode ) entry.getKey();
-				final String tuplizerClassName = ( String ) entry.getValue();
-				final EntityTuplizer tuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, em, mappedEntity );
-				addTuplizer( entityMode, tuplizer );
-			}
-		}
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.MappingException;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.engine.internal.Versioning;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.MappingException;
-import org.hibernate.engine.internal.Versioning;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.internal.CoreMessageLogger;
-import org.jboss.logging.Logger;
-	private final EntityEntityModeToTuplizerMapping tuplizerMapping;
+	private final EntityMode entityMode;
+	private final EntityTuplizer entityTuplizer;
-		tuplizerMapping = new EntityEntityModeToTuplizerMapping( persistentClass, this );
+		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
+		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
+		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
+		if ( tuplizerClassName == null ) {
+			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
+		}
+		else {
+			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
+		}
-	public EntityEntityModeToTuplizerMapping getTuplizerMapping() {
-		return tuplizerMapping;
-	}
-
-	public EntityTuplizer getTuplizer(EntityMode entityMode) {
-		return (EntityTuplizer) tuplizerMapping.getTuplizer( entityMode );
-	}
-
-	public EntityTuplizer getTuplizerOrNull(EntityMode entityMode) {
-		return ( EntityTuplizer ) tuplizerMapping.getTuplizerOrNull( entityMode );
-	}
-
-	public EntityMode guessEntityMode(Object object) {
-		return tuplizerMapping.guessEntityMode( object );
+	public EntityTuplizer getTuplizer() {
+		return entityTuplizer;
+
+	public EntityMode getEntityMode() {
+		return entityMode;
+	}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
+
+
-import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.PersistentClass;
-		map.put( EntityMode.DOM4J, Dom4jEntityTuplizer.class );
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
-import org.hibernate.EntityMode;
+
-	public int compare(Object o1, Object o2) {
-		return compare( o1, o2, null );
-	}
+
-	public int getHashCode(Object x, EntityMode entityMode) {
+	public int getHashCode(Object x) {
-	public int compare(Object x, Object y, EntityMode entityMode) {
+	public int compare(Object x, Object y) {
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractCharArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractCharArrayType.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.io.CharArrayReader;
-import java.io.CharArrayWriter;
-import java.io.IOException;
-import java.io.Reader;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.HibernateException;
-import org.hibernate.dialect.Dialect;
-
-/**
- * Logic to bind stream of char into a VARCHAR
- *
- * @author Emmanuel Bernard
- *
- * @deprecated Use the {@link AbstractStandardBasicType} approach instead
- */
-public abstract class AbstractCharArrayType extends MutableType {
-
-	/**
-	 * Convert the char[] into the expected object type
-	 */
-	abstract protected Object toExternalFormat(char[] chars);
-
-	/**
-	 * Convert the object into the internal char[] representation
-	 */
-	abstract protected char[] toInternalFormat(Object chars);
-
-	public Object get(ResultSet rs, String name) throws SQLException {
-		Reader stream = rs.getCharacterStream(name);
-		if ( stream == null ) return toExternalFormat( null );
-		CharArrayWriter writer = new CharArrayWriter();
-		for(;;) {
-			try {
-				int c = stream.read();
-				if ( c == -1) return toExternalFormat( writer.toCharArray() );
-				writer.write( c );
-			}
-			catch (IOException e) {
-				throw new HibernateException("Unable to read character stream from rs");
-			}
-		}
-	}
-
-	public abstract Class getReturnedClass();
-
-	public void set(PreparedStatement st, Object value, int index) throws SQLException {
-		char[] chars = toInternalFormat( value );
-		st.setCharacterStream(index, new CharArrayReader(chars), chars.length);
-	}
-
-	public int sqlType() {
-		return Types.VARCHAR;
-	}
-
-	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
-
-		return '\'' + new String( toInternalFormat( value ) ) + '\'';
-	}
-
-	public Object stringToObject(String xml) throws Exception {
-		if (xml == null) return toExternalFormat( null );
-		int length = xml.length();
-		char[] chars = new char[length];
-		for (int index = 0 ; index < length ; index++ ) {
-			chars[index] = xml.charAt( index );
-		}
-		return toExternalFormat( chars );
-	}
-
-	public String toString(Object value) {
-		if (value == null) return null;
-		return new String( toInternalFormat( value ) );
-	}
-
-	public Object fromStringValue(String xml) {
-		if (xml == null) return null;
-		int length = xml.length();
-		char[] chars = new char[length];
-		for (int index = 0 ; index < length ; index++ ) {
-			chars[index] = xml.charAt( index );
-		}
-		return toExternalFormat( chars );
-	}
-
-	protected Object deepCopyNotNull(Object value) throws HibernateException {
-		char[] chars = toInternalFormat(value);
-		char[] result = new char[chars.length];
-		System.arraycopy(chars, 0, result, 0, chars.length);
-		return toExternalFormat(result);
-	}
-}
+
+import java.io.CharArrayReader;
+import java.io.CharArrayWriter;
+import java.io.IOException;
+import java.io.Reader;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.HibernateException;
+import org.hibernate.dialect.Dialect;
+
+/**
+ * Logic to bind stream of char into a VARCHAR
+ *
+ * @author Emmanuel Bernard
+ *
+ * @deprecated Use the {@link AbstractStandardBasicType} approach instead
+ */
+public abstract class AbstractCharArrayType extends MutableType {
+
+	/**
+	 * Convert the char[] into the expected object type
+	 */
+	abstract protected Object toExternalFormat(char[] chars);
+
+	/**
+	 * Convert the object into the internal char[] representation
+	 */
+	abstract protected char[] toInternalFormat(Object chars);
+
+	public Object get(ResultSet rs, String name) throws SQLException {
+		Reader stream = rs.getCharacterStream(name);
+		if ( stream == null ) return toExternalFormat( null );
+		CharArrayWriter writer = new CharArrayWriter();
+		for(;;) {
+			try {
+				int c = stream.read();
+				if ( c == -1) return toExternalFormat( writer.toCharArray() );
+				writer.write( c );
+			}
+			catch (IOException e) {
+				throw new HibernateException("Unable to read character stream from rs");
+			}
+		}
+	}
+
+	public abstract Class getReturnedClass();
+
+	public void set(PreparedStatement st, Object value, int index) throws SQLException {
+		char[] chars = toInternalFormat( value );
+		st.setCharacterStream(index, new CharArrayReader(chars), chars.length);
+	}
+
+	public int sqlType() {
+		return Types.VARCHAR;
+	}
+
+	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
+
+		return '\'' + new String( toInternalFormat( value ) ) + '\'';
+	}
+
+	public Object stringToObject(String xml) throws Exception {
+		if (xml == null) return toExternalFormat( null );
+		int length = xml.length();
+		char[] chars = new char[length];
+		for (int index = 0 ; index < length ; index++ ) {
+			chars[index] = xml.charAt( index );
+		}
+		return toExternalFormat( chars );
+	}
+
+	public String toString(Object value) {
+		if (value == null) return null;
+		return new String( toInternalFormat( value ) );
+	}
+
+	public Object fromStringValue(String xml) {
+		if (xml == null) return null;
+		int length = xml.length();
+		char[] chars = new char[length];
+		for (int index = 0 ; index < length ; index++ ) {
+			chars[index] = xml.charAt( index );
+		}
+		return toExternalFormat( chars );
+	}
+
+	protected Object deepCopyNotNull(Object value) throws HibernateException {
+		char[] chars = toInternalFormat(value);
+		char[] result = new char[chars.length];
+		System.arraycopy(chars, 0, result, 0, chars.length);
+		return toExternalFormat(result);
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
-import org.hibernate.EntityMode;
+
-		return checkable[0] ? ! isEqual( old, current, session.getEntityMode() ) : false;
+		return checkable[0] ? ! isEqual( old, current ) : false;
-	public boolean isEqual(Object x, Object y, EntityMode entityMode) {
-		return isEqual( x, y, entityMode, null );
+	public boolean isEqual(Object x, Object y) {
+		return isEqual( x, y, null );
-	public int getHashCode(Object x, EntityMode entityMode) {
-		return getHashCode( x, entityMode, null );
+	public int getHashCode(Object x) {
+		return getHashCode( x, null );
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractLongStringType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractLongStringType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+
+
-import org.hibernate.EntityMode;
+
+import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.internal.util.collections.ArrayHelper;
-	public final boolean isSame(Object x, Object y, EntityMode entityMode) {
-		return isSame( x, y );
-	}
-
-	protected final boolean isSame(Object x, Object y) {
-		return isEqual( (T) x, (T) y );
+	public final boolean isSame(Object x, Object y) {
+		return isEqual( x, y );
-	public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
-		return isEqual( (T) x, (T) y );
+	public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
+		return isEqual( x, y );
-	public final boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
-		return isEqual( (T) x, (T) y );
+	public final boolean isEqual(Object one, Object another) {
+		return javaTypeDescriptor.areEqual( (T) one, (T) another );
-	public final boolean isEqual(T one, T another) {
-		return javaTypeDescriptor.areEqual( one, another );
-	}
-
-	public final int getHashCode(Object x, EntityMode entityMode) {
-		return getHashCode( x );
+	public final int getHashCode(Object x) {
+		return javaTypeDescriptor.extractHashCode( (T) x );
-	public final int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public final int getHashCode(Object x, SessionFactoryImplementor factory) {
-	protected final int getHashCode(Object x) {
-		return javaTypeDescriptor.extractHashCode( (T) x );
-	}
-
-	@SuppressWarnings({ "unchecked" })
-	public final int compare(Object x, Object y, EntityMode entityMode) {
+	public final int compare(Object x, Object y) {
-	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+
+
-import org.hibernate.EntityMode;
+
-	public int compare(Object x, Object y, EntityMode entityMode) {
+	public int compare(Object x, Object y) {
-			return (Serializable) deepCopy( value, session.getEntityMode(), session.getFactory() );
+			return (Serializable) deepCopy( value, session.getFactory() );
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner) 
+	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
-			return deepCopy( cached, session.getEntityMode(), session.getFactory() );
+			return deepCopy( cached, session.getFactory() );
-	public boolean isDirty(Object old, Object current, SessionImplementor session) 
-	throws HibernateException {
-		return !isSame( old, current, session.getEntityMode() );
+	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException {
+		return !isSame( old, current );
-	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
-		return isEqual(x, y, entityMode);
+	public boolean isSame(Object x, Object y) throws HibernateException {
+		return isEqual(x, y );
-	public boolean isEqual(Object x, Object y, EntityMode entityMode) {
+	public boolean isEqual(Object x, Object y) {
-	public int getHashCode(Object x, EntityMode entityMode) {
+	public int getHashCode(Object x) {
-	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
-		return isEqual(x, y, entityMode);
+	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
+		return isEqual(x, y );
-	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
-		return getHashCode(x, entityMode);
+	public int getHashCode(Object x, SessionFactoryImplementor factory) {
+		return getHashCode(x );
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
+	public boolean isSame(Object x, Object y) throws HibernateException {
-	public int compare(Object x, Object y, EntityMode entityMode) {
+	public int compare(Object x, Object y) {
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+
-import org.hibernate.EntityMode;
+
-	protected boolean initializeImmediately(EntityMode entityMode) {
+	@Override
+	protected boolean initializeImmediately() {
-	public boolean hasHolder(EntityMode entityMode) {
+	@Override
+	public boolean hasHolder() {
-	
--- a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
+
-import org.hibernate.collection.internal.PersistentElementHolder;
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder(session, persister, key);
-		}
-		else {
-			return new PersistentBag(session);
-		}
+		return new PersistentBag(session);
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentBag( session, (Collection) collection );
-		}
+		return new PersistentBag( session, (Collection) collection );
--- a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
+
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
+
+
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
--- a/hibernate-core/src/main/java/org/hibernate/type/BooleanType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BooleanType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
+
+
-import org.hibernate.EntityMode;
+
-	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
-	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public int getHashCode(Object x, SessionFactoryImplementor factory) {
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-		if ( isEqual( original, target, session.getEntityMode() ) ) return original;
-		return deepCopy( original, session.getEntityMode(), session.getFactory() );
+		if ( isEqual( original, target ) ) return original;
+		return deepCopy( original, session.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
+
+
-
--- a/hibernate-core/src/main/java/org/hibernate/type/CalendarDateType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CalendarDateType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
+
+
-
--- a/hibernate-core/src/main/java/org/hibernate/type/CharacterType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CharacterType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+
+
+
-	public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
+	public final boolean isEqual(Object x, Object y) {
-	public int compare(Object x, Object y, EntityMode entityMode) {
+	public int compare(Object x, Object y) {
-	public int getHashCode(Object x, EntityMode entityMode) {
+	public int getHashCode(Object x) {
-	protected String renderLoggableString(Object value, SessionFactoryImplementor factory)
-			throws HibernateException {
-		if ( Element.class.isInstance( value ) ) {
-			// for DOM4J "collections" only
-			// TODO: it would be better if this was done at the higher level by Printer
-			return ( ( Element ) value ).asXML();
-		}
-		else {
-			List list = new ArrayList();
-			Type elemType = getElementType( factory );
-			Iterator iter = getElementsIterator( value );
-			while ( iter.hasNext() ) {
-				list.add( elemType.toLoggableString( iter.next(), factory ) );
-			}
-			return list.toString();
+	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
+		final List<String> list = new ArrayList<String>();
+		Type elemType = getElementType( factory );
+		Iterator itr = getElementsIterator( value );
+		while ( itr.hasNext() ) {
+			list.add( elemType.toLoggableString( itr.next(), factory ) );
+		return list.toString();
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			final SessionFactoryImplementor factory = session.getFactory();
-			final CollectionPersister persister = factory.getCollectionPersister( getRole() );
-			final Type elementType = persister.getElementType();
-			
-			List elements = ( (Element) collection ).elements( persister.getElementNodeName() );
-			ArrayList results = new ArrayList();
-			for ( int i=0; i<elements.size(); i++ ) {
-				Element value = (Element) elements.get(i);
-				results.add( elementType.fromXMLNode( value, factory ) );
-			}
-			return results.iterator();
-		}
-		else {
-			return getElementsIterator(collection);
-		}
+		return getElementsIterator(collection);
-				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName, session.getEntityMode() );
+				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
-			Class ownerMappedClass = ownerPersister.getMappedClass( session.getEntityMode() );
+			Class ownerMappedClass = ownerPersister.getMappedClass();
-		final EntityMode entityMode = session.getEntityMode();
+		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
-		if (entityMode==EntityMode.DOM4J && !isEmbeddedInXML) {
-			return UNFETCHED_COLLECTION;
-		}
-		
-				if ( initializeImmediately( entityMode ) ) {
+				if ( initializeImmediately() ) {
-				if ( hasHolder( entityMode ) ) {
+				if ( hasHolder() ) {
-	public boolean hasHolder(EntityMode entityMode) {
-		return entityMode == EntityMode.DOM4J;
+	public boolean hasHolder() {
+		return false;
-	protected boolean initializeImmediately(EntityMode entityMode) {
-		return entityMode == EntityMode.DOM4J;
+	protected boolean initializeImmediately() {
+		return false;
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
-import org.hibernate.tuple.EntityModeToTuplizerMapping;
-	protected final EntityModeToTuplizerMapping tuplizerMapping;
+	protected final EntityMode entityMode;
+	protected final ComponentTuplizer componentTuplizer;
-		this.tuplizerMapping = metamodel.getTuplizerMapping();
+		this.entityMode = metamodel.getEntityMode();
+		this.componentTuplizer = metamodel.getComponentTuplizer();
-	public EntityModeToTuplizerMapping getTuplizerMapping() {
-		return tuplizerMapping;
+	public EntityMode getEntityMode() {
+		return entityMode;
+	}
+
+	public ComponentTuplizer getComponentTuplizer() {
+		return componentTuplizer;
-		return tuplizerMapping.getTuplizer( EntityMode.POJO ).getMappedClass(); //TODO
+		return componentTuplizer.getMappedClass();
-    public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
+    public boolean isSame(Object x, Object y) throws HibernateException {
-			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i], entityMode ) ) {
+			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
-    public boolean isEqual(Object x, Object y, EntityMode entityMode)
+    public boolean isEqual(Object x, Object y)
-			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], entityMode ) ) {
+			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i] ) ) {
-    public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory)
+    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
-			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], entityMode, factory ) ) {
+			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], factory ) ) {
-    public int compare(Object x, Object y, EntityMode entityMode) {
+    public int compare(Object x, Object y) {
-			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i], entityMode );
+			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i] );
-    public int getHashCode(Object x, EntityMode entityMode) {
+    public int getHashCode(Object x) {
-				result += propertyTypes[i].getHashCode( y, entityMode );
+				result += propertyTypes[i].getHashCode( y );
-    public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
+    public int getHashCode(Object x, SessionFactoryImplementor factory) {
-				result += propertyTypes[i].getHashCode( y, entityMode, factory );
+				result += propertyTypes[i].getHashCode( y, factory );
-		EntityMode entityMode = session.getEntityMode();
-		EntityMode entityMode = session.getEntityMode();
-		Object[] subvalues = nullSafeGetValues( value, session.getEntityMode() );
+		Object[] subvalues = nullSafeGetValues( value, entityMode );
-		Object[] subvalues = nullSafeGetValues( value, session.getEntityMode() );
+		Object[] subvalues = nullSafeGetValues( value, entityMode );
-		return getPropertyValue( component, i, session.getEntityMode() );
+		return getPropertyValue( component, i, entityMode );
-		return tuplizerMapping.getTuplizer( entityMode ).getPropertyValue( component, i );
+		return componentTuplizer.getPropertyValue( component, i );
-		return getPropertyValues( component, session.getEntityMode() );
+		return getPropertyValues( component, entityMode );
-		return tuplizerMapping.getTuplizer( entityMode ).getPropertyValues( component );
+		return componentTuplizer.getPropertyValues( component );
-		tuplizerMapping.getTuplizer( entityMode ).setPropertyValues( component, values );
+		componentTuplizer.setPropertyValues( component, values );
-		EntityMode entityMode = tuplizerMapping.guessEntityMode( value );
-	public Object deepCopy(Object component, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object component, SessionFactoryImplementor factory)
-			values[i] = propertyTypes[i].deepCopy( values[i], entityMode, factory );
+			values[i] = propertyTypes[i].deepCopy( values[i], factory );
-		ComponentTuplizer ct = ( ComponentTuplizer ) tuplizerMapping.getTuplizer( entityMode );
-		if ( ct.hasParentProperty() ) {
-			ct.setParent( result, ct.getParent( component ), factory );
+		if ( componentTuplizer.hasParentProperty() ) {
+			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
-		final EntityMode entityMode = session.getEntityMode();
-		final EntityMode entityMode = session.getEntityMode();
-		return tuplizerMapping.getTuplizer( entityMode ).instantiate();
+		return componentTuplizer.instantiate();
-		Object result = instantiate( session.getEntityMode() );
+		Object result = instantiate( entityMode );
-		ComponentTuplizer ct = ( ComponentTuplizer ) tuplizerMapping.getTuplizer( session.getEntityMode() );
-		if ( ct.hasParentProperty() && parent != null ) {
-			ct.setParent(
+		if ( componentTuplizer.hasParentProperty() && parent != null ) {
+			componentTuplizer.setParent(
-			Object[] values = getPropertyValues( value, session.getEntityMode() );
+			Object[] values = getPropertyValues( value, entityMode );
-			setPropertyValues( result, assembled, session.getEntityMode() );
+			setPropertyValues( result, assembled, entityMode );
-			setPropertyValues( result, resolvedValues, session.getEntityMode() );
+			setPropertyValues( result, resolvedValues, entityMode );
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+
+
+
-	public Object[] getPropertyValues(Object component, SessionImplementor session)
-		throws HibernateException {
-		return getPropertyValues( component, session.getEntityMode() );
+	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
+		return getPropertyValues( component, EntityMode.POJO );
-	public Object[] getPropertyValues(Object component, EntityMode entityMode)
-		throws HibernateException {
+	public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
-	public Object getPropertyValue(Object component, int i)
-		throws HibernateException {
-		return userType.getPropertyValue(component, i);
+	public Object getPropertyValue(Object component, int i) throws HibernateException {
+		return userType.getPropertyValue( component, i );
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-		return userType.deepCopy(value);
+		return userType.deepCopy( value );
-		return userType.assemble(cached, session, owner);
+		return userType.assemble( cached, session, owner );
-	
+
-	public boolean isEqual(Object x, Object y, EntityMode entityMode) 
+	public boolean isEqual(Object x, Object y)
-	public int getHashCode(Object x, EntityMode entityMode) {
+	public int getHashCode(Object x) {
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/CurrencyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CurrencyType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+
+
-import org.hibernate.EntityMode;
+
-		return userType.equals(x, y);
-	}
-
-	public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException {
-		return isEqual(x, y);
+		return userType.equals( x, y );
-	public int getHashCode(Object x, EntityMode entityMode) {
+	public int getHashCode(Object x) {
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/type/DateType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DateType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.Dialect;
-
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/DoubleType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DoubleType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
+
-import org.hibernate.EntityMode;
+
-import org.hibernate.tuple.component.ComponentTuplizer;
+	public EmbeddedComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
+		super( typeScope, metamodel );
+	}
-	public EmbeddedComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
-		super( typeScope, metamodel );
-	}
-
-		return ( ( ComponentTuplizer ) tuplizerMapping.getTuplizer(EntityMode.POJO) ).isMethodOf(method);
+		return componentTuplizer.isMethodOf( method );
-	public Object instantiate(Object parent, SessionImplementor session)
-	throws HibernateException {
+	public Object instantiate(Object parent, SessionImplementor session) throws HibernateException {
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+
+
+
-import org.hibernate.proxy.LazyInitializer;
+	 *
-	 * @param entityMode The entity mode.
-	public final boolean isSame(Object x, Object y, EntityMode entityMode) {
+	public final boolean isSame(Object x, Object y) {
-	public int compare(Object x, Object y, EntityMode entityMode) {
+	public int compare(Object x, Object y) {
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
-	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public int getHashCode(Object x, SessionFactoryImplementor factory) {
-			return super.getHashCode(x, entityMode);
+			return super.getHashCode( x );
-			final Class mappedClass = persister.getMappedClass( entityMode );
+			final Class mappedClass = persister.getMappedClass();
-				id = persister.getIdentifier(x, entityMode);
+				id = persister.getIdentifier( x );
-		return persister.getIdentifierType().getHashCode(id, entityMode, factory);
+		return persister.getIdentifierType().getHashCode( id, factory );
-	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
-			return super.isEqual(x, y, entityMode);
+			return super.isEqual(x, y );
-		final Class mappedClass = persister.getMappedClass( entityMode );
+		final Class mappedClass = persister.getMappedClass();
-				xid = persister.getIdentifier(x, entityMode);
+				xid = persister.getIdentifier( x );
-				yid = persister.getIdentifier(y, entityMode);
+				yid = persister.getIdentifier( y );
-				.isEqual(xid, yid, entityMode, factory);
+				.isEqual(xid, yid, factory);
-			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName, session.getEntityMode() );
+			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
-		return !isEmbeddedInXML && session.getEntityMode()==EntityMode.DOM4J;
-	}
-
-	/**
-	 * Get the identifier value of an instance or proxy.
-	 * <p/>
-	 * Intended only for loggin purposes!!!
-	 *
-	 * @param object The object from which to extract the identifier.
-	 * @param persister The entity persister
-	 * @param entityMode The entity mode
-	 * @return The extracted identifier.
-	 */
-	private static Serializable getIdentifier(Object object, EntityPersister persister, EntityMode entityMode) {
-		if (object instanceof HibernateProxy) {
-			HibernateProxy proxy = (HibernateProxy) object;
-			LazyInitializer li = proxy.getHibernateLazyInitializer();
-			return li.getIdentifier();
-		}
-		else {
-			return persister.getIdentifier( object, entityMode );
-		}
+//		return !isEmbeddedInXML;
+		return false;
-			final EntityMode entityMode = persister.guessEntityMode( value );
+			final EntityMode entityMode = persister.getEntityMode();
-				id = getIdentifier( value, persister, entityMode );
+				id = persister.getIdentifier( value );
-						.isInstrumented( session.getEntityMode() );
+						.isInstrumented();
-				session.getEntityMode(), 
+				persister.getEntityMode(),
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/FloatType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/FloatType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
-import org.hibernate.EntityMode;
+
-	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
--- a/hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.collection.internal.PersistentListElementHolder;
-import org.hibernate.collection.spi.PersistentCollection;
+
+import org.hibernate.collection.spi.PersistentCollection;
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentListElementHolder(session, persister, key);
-		}
-		else {
-			return new PersistentList(session);
-		}
+		return new PersistentList(session);
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentListElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentList( session, (List) collection );
-		}
+		return new PersistentList( session, (List) collection );
--- a/hibernate-core/src/main/java/org/hibernate/type/LocaleType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/LocaleType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/LongType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/LongType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
-		if ( isSame( old, current, session.getEntityMode() ) ) {
+		if ( isSame( old, current ) ) {
-			if ( isSame( old, current, session.getEntityMode() ) ) {
+			if ( isSame( old, current ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
+
-import org.hibernate.collection.internal.PersistentMapElementHolder;
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentMapElementHolder(session, persister, key);
-		}
-		else {
-			return new PersistentMap(session);
-		}
+		return new PersistentMap(session);
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentMapElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentMap( session, (java.util.Map) collection );
-		}
+		return new PersistentMap( session, (java.util.Map) collection );
--- a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
+
+
-import org.hibernate.EntityMode;
+
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
--- a/hibernate-core/src/main/java/org/hibernate/type/MutableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MutableType.java
-import org.hibernate.EntityMode;
+
-	public final Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
-	throws HibernateException {
+	public final Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
-		Object original,
-		Object target,
-		SessionImplementor session,
-		Object owner, 
-		Map copyCache)
-	throws HibernateException {
-		if ( isEqual( original, target, session.getEntityMode() ) ) return original;
-		return deepCopy( original, session.getEntityMode(), session.getFactory() );
+			Object original,
+			Object target,
+			SessionImplementor session,
+			Object owner,
+			Map copyCache) throws HibernateException {
+		if ( isEqual( original, target ) ) {
+			return original;
+		}
+		return deepCopy( original, session.getFactory() );
--- a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
-import org.hibernate.EntityMode;
+import org.hibernate.MappingException;
-import org.hibernate.MappingException;
-		return nullSafeGet(rs, name);
+		return nullSafeGet( rs, name );
-    public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
-		return isEqual(x, y);
-	}
-
--- a/hibernate-core/src/main/java/org/hibernate/type/NumericBooleanType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NumericBooleanType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+
+
-import org.hibernate.EntityMode;
+
-	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory) {
+	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
-	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor session) {
-		return type.getHashCode( x, null );
+	public int getHashCode(Object x, SessionFactoryImplementor session) {
+		return type.getHashCode( x );
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-		return type.deepCopy( value, null, null );
+		return type.deepCopy( value, null );
--- a/hibernate-core/src/main/java/org/hibernate/type/SerializableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SerializableType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.collection.internal.PersistentElementHolder;
-import org.hibernate.collection.spi.PersistentCollection;
+
+import org.hibernate.collection.spi.PersistentCollection;
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder(session, persister, key);
-		}
-		else {
-			return new PersistentSet(session);
-		}
+		return new PersistentSet(session);
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentSet( session, (java.util.Set) collection );
-		}
+		return new PersistentSet( session, (java.util.Set) collection );
--- a/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
+
+
-
-
-
-
-
--- a/hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.collection.internal.PersistentElementHolder;
+
-import org.hibernate.collection.internal.PersistentMapElementHolder;
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentMapElementHolder(session, persister, key);
-		}
-		else {
-			PersistentSortedMap map = new PersistentSortedMap(session);
-			map.setComparator(comparator);
-			return map;
-		}
+		PersistentSortedMap map = new PersistentSortedMap(session);
+		map.setComparator(comparator);
+		return map;
+	@SuppressWarnings( {"unchecked"})
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentSortedMap( session, (java.util.SortedMap) collection );
-		}
+		return new PersistentSortedMap( session, (java.util.SortedMap) collection );
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.collection.internal.PersistentElementHolder;
+
-
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder(session, persister, key);
-		}
-		else {
-			PersistentSortedSet set = new PersistentSortedSet(session);
-			set.setComparator(comparator);
-			return set;
-		}
+		PersistentSortedSet set = new PersistentSortedSet(session);
+		set.setComparator(comparator);
+		return set;
+	@SuppressWarnings( {"unchecked"})
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			return new PersistentElementHolder( session, (Element) collection );
-		}
-		else {
-			return new PersistentSortedSet( session, (java.util.SortedSet) collection );
-		}
+		return new PersistentSortedSet( session, (java.util.SortedSet) collection );
-
-
-
-
-
-
--- a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
+
--- a/hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/TimeType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TimeType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/TimeZoneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TimeZoneType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
+
+
-
--- a/hibernate-core/src/main/java/org/hibernate/type/TrueFalseType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TrueFalseType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
-import org.hibernate.EntityMode;
-	 * @param entityMode The entity mode of the values.
-	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException;
+	public boolean isSame(Object x, Object y) throws HibernateException;
-	 * @param entityMode The entity mode of the values.
-	public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException;
+	public boolean isEqual(Object x, Object y) throws HibernateException;
-	 * @param entityMode The entity mode of the values.
-	public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory)
-			throws HibernateException;
+	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException;
-	 * @param entityMode The entity mode of the value.
-	 *
-	public int getHashCode(Object x, EntityMode entityMode) throws HibernateException;
+	public int getHashCode(Object x) throws HibernateException;
-	 * @param entityMode The entity mode of the value.
-	public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory) throws HibernateException;
+	public int getHashCode(Object x, SessionFactoryImplementor factory) throws HibernateException;
-	 * @param entityMode The entity mode of the values.
-	public int compare(Object x, Object y, EntityMode entityMode);
+	public int compare(Object x, Object y);
+	 *
-	 * @param entityMode 
-	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory) 
+	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-
-
-
-
-
-
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
-					target[i] = types[i].deepCopy( values[i], session.getEntityMode(), session
+					target[i] = types[i].deepCopy( values[i], session
--- a/hibernate-core/src/main/java/org/hibernate/type/UUIDBinaryType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/UUIDBinaryType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/UUIDCharType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/UUIDCharType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/UrlType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/UrlType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/VersionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/VersionType.java
+
+
-
-	/**
-	 * Are the two version values considered equal?
-	 *
-	 * @param x One value to check.
-	 * @param y The other value to check.
-	 * @return true if the values are equal, false otherwise.
-	 */
-	public boolean isEqual(T x, T y);
--- a/hibernate-core/src/main/java/org/hibernate/type/YesNoType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/YesNoType.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
+
-import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java
+
+import org.hibernate.type.descriptor.WrapperOptions;
+
-import org.hibernate.type.descriptor.WrapperOptions;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
+
+
-import org.hibernate.cfg.Environment;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+
+import org.jboss.logging.Logger;
+
-import org.jboss.logging.Logger;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
+
+
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
+
+
--- a/hibernate-core/src/test/java/org/hibernate/cache/spi/QueryKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/cache/spi/QueryKeyTest.java
-				EntityMode.POJO,					// entity mode
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
-//$Id$
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
-import junit.framework.TestCase;
-import org.hibernate.EntityMode;
+
+import org.hibernate.tuple.entity.EntityTuplizer;
+
+import junit.framework.TestCase;
-import org.hibernate.tuple.entity.EntityMetamodel;
-import org.hibernate.tuple.entity.PojoEntityTuplizer;
+import org.hibernate.testing.TestForIssue;
+@SuppressWarnings( {"deprecation"})
-
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		EntityMetamodel metaModel = factory.getEntityPersister( Animal.class.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( Animal.class.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-		metaModel = factory.getEntityPersister( Horse.class.getName() )
-				.getEntityMetamodel();
-		tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		tuplizer = factory.getEntityPersister( Horse.class.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
-	/**
-	 * HHH-5004
-	 */
+	@TestForIssue( jiraKey = "HHH-5004")
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
-import org.hibernate.EntityMode;
-import org.hibernate.tuple.entity.EntityMetamodel;
-import org.hibernate.tuple.entity.PojoEntityTuplizer;
+import org.hibernate.tuple.entity.EntityTuplizer;
-		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
-				.getEntityMetamodel();
-		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
+		EntityTuplizer tuplizer = factory.getEntityPersister( classUnderTest.getName() )
+				.getEntityMetamodel()
+				.getTuplizer();
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+import org.hibernate.tuple.entity.EntityTuplizer;
+		@Override
+		public EntityMode getEntityMode() {
+			return null;
+		}
+
+		@Override
+		public EntityTuplizer getEntityTuplizer() {
+			return null;
+		}
+
+		@Override
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Serializable[0];
+		@Override
-			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Serializable[0];
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return 0;  //To change body of implemented methods use File | Settings | File Templates.
+			return 0;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Object[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
-		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
+			return null;
-		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
+			return null;
-		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
-		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
-		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
-		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
+			return null;
-		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
-		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
+		@Override
-			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Type[0];
+		@Override
-			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new String[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new ValueInclusion[0];
+		@Override
-			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new ValueInclusion[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new CascadeStyle[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Object[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
+			return null;
-		public EntityMode guessEntityMode(Object object) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public boolean isInstrumented(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean isInstrumented() {
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
-				throws HibernateException {
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
+			return new Object[0];
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public Class getMappedClass(EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Class getMappedClass() {
+			return null;
-		public boolean implementsLifecycle(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean implementsLifecycle() {
+			return false;
-		public boolean implementsValidatable(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Class getConcreteProxyClass() {
+			return null;
-		public Class getConcreteProxyClass(EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void setPropertyValues(Object object, Object[] values) {
-		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void setPropertyValue(Object object, int i, Object value) {
-		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object[] getPropertyValues(Object object) {
+			return new Object[0];
-		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getPropertyValue(Object object, int i) {
+			return null;
-		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getPropertyValue(Object object, String propertyName) {
+			return null;
-		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Serializable getIdentifier(Object object) {
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getVersion(Object object) {
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public boolean isInstance(Object object, EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean isInstance(Object object) {
+			return false;
-		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean hasUninitializedLazyProperties(Object object) {
+			return false;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
+			return null;
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/accessors/Dom4jAccessorTest.java
+++ /dev/null
-// $Id: Dom4jAccessorTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
-package org.hibernate.test.entitymode.dom4j.accessors;
-import junit.framework.Test;
-import junit.framework.TestCase;
-import junit.framework.TestSuite;
-import org.dom4j.DocumentFactory;
-import org.dom4j.Element;
-import org.dom4j.util.NodeComparator;
-import org.hibernate.EntityMode;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Mappings;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.mapping.Property;
-import org.hibernate.mapping.SimpleValue;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-
-/**
- * Unit test of dom4j-based accessors
- *
- * @author Steve Ebersole
- */
-public class Dom4jAccessorTest extends TestCase {
-
-	public static final Element DOM = generateTestElement();
-
-	private Mappings mappings;
-
-	public Dom4jAccessorTest(String name) {
-		super( name );
-	}
-
-	@Override
-	protected void setUp() throws Exception {
-		mappings = new Configuration().createMappings();
-	}
-
-	public void testStringElementExtraction() throws Throwable {
-		Property property = generateNameProperty();
-		Getter getter = PropertyAccessorFactory.getPropertyAccessor( property, EntityMode.DOM4J )
-				.getGetter( null, null );
-		String name = ( String ) getter.get( DOM );
-		assertEquals( "Not equals", "JBoss", name );
-	}
-
-	public void testStringTextExtraction() throws Throwable {
-		Property property = generateTextProperty();
-		Getter getter = PropertyAccessorFactory.getPropertyAccessor( property, EntityMode.DOM4J )
-				.getGetter( null, null );
-		String name = ( String ) getter.get( DOM );
-		assertEquals( "Not equals", "description...", name );
-	}
-
-	public void testLongAttributeExtraction() throws Throwable {
-		Property property = generateIdProperty();
-		Getter getter = PropertyAccessorFactory.getPropertyAccessor( property, EntityMode.DOM4J )
-				.getGetter( null, null );
-		Long id = ( Long ) getter.get( DOM );
-		assertEquals( "Not equals", new Long( 123 ), id );
-	}
-
-	public void testLongElementAttributeExtraction() throws Throwable {
-		Property property = generateAccountIdProperty();
-		Getter getter = PropertyAccessorFactory.getPropertyAccessor( property, EntityMode.DOM4J )
-				.getGetter( null, null );
-		Long id = ( Long ) getter.get( DOM );
-		assertEquals( "Not equals", new Long( 456 ), id );
-	}
-
-	public void testCompanyElementGeneration() throws Throwable {
-		Setter idSetter = PropertyAccessorFactory.getPropertyAccessor( generateIdProperty(), EntityMode.DOM4J )
-				.getSetter( null, null );
-		Setter nameSetter = PropertyAccessorFactory.getPropertyAccessor( generateNameProperty(), EntityMode.DOM4J )
-				.getSetter( null, null );
-		Setter textSetter = PropertyAccessorFactory.getPropertyAccessor( generateTextProperty(), EntityMode.DOM4J )
-				.getSetter( null, null );
-		Setter accountIdSetter = PropertyAccessorFactory.getPropertyAccessor(
-				generateAccountIdProperty(), EntityMode.DOM4J
-		)
-				.getSetter( null, null );
-
-		Element root = generateRootTestElement();
-
-		idSetter.set( root, new Long( 123 ), getSFI() );
-		textSetter.set( root, "description...", getSFI() );
-		nameSetter.set( root, "JBoss", getSFI() );
-		accountIdSetter.set( root, new Long( 456 ), getSFI() );
-
-		assertTrue( "DOMs not equal", new NodeComparator().compare( DOM, root ) == 0 );
-	}
-
-	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	private static Element generateTestElement() {
-		Element company = generateRootTestElement();
-		company.addAttribute( "id", "123" );
-		company.setText( "description..." );
-		company.addElement( "name" ).setText( "JBoss" );
-		company.addElement( "account" ).addAttribute( "num", "456" );
-
-		return company;
-	}
-
-	private static Element generateRootTestElement() {
-		return DocumentFactory.getInstance().createElement( "company" );
-	}
-
-	public static Test suite() {
-		return new TestSuite( Dom4jAccessorTest.class );
-	}
-
-	private SessionFactoryImplementor getSFI() {
-		return null;
-	}
-
-	private Property generateIdProperty() {
-		SimpleValue value = new SimpleValue( mappings );
-		value.setTypeName( "long" );
-
-		Property property = new Property();
-		property.setName( "id" );
-		property.setNodeName( "@id" );
-		property.setValue( value );
-
-		return property;
-	}
-
-	private Property generateTextProperty() {
-		SimpleValue value = new SimpleValue(mappings);
-		value.setTypeName( "string" );
-
-		Property property = new Property();
-		property.setName( "text" );
-		property.setNodeName( "." );
-		property.setValue( value );
-
-		return property;
-	}
-
-	private Property generateAccountIdProperty() {
-		SimpleValue value = new SimpleValue(mappings);
-		value.setTypeName( "long" );
-
-		Property property = new Property();
-		property.setName( "number" );
-		property.setNodeName( "account/@num" );
-		property.setValue( value );
-
-		return property;
-	}
-
-	private Property generateNameProperty() {
-		SimpleValue value = new SimpleValue(mappings);
-		value.setTypeName( "string" );
-
-		Property property = new Property();
-		property.setName( "name" );
-		property.setNodeName( "name" );
-		property.setValue( value );
-
-		return property;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/AB.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping PUBLIC 
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<hibernate-mapping >
-
-    <class entity-name="A" table="AX" node="a">
-        <id name="aId" type="int" column="aId" node="@id"/>
-        <property name="x"  type="string"/>
-        <set name="bs" node="." embed-xml="true" cascade="all" inverse="true">
-            <key column="aId"/>
-            <one-to-many class="B"/>
-        </set>
-    </class>
-
-
-    <class entity-name="B" table="BX" node="b">
-        <composite-id>
-            <key-property name="bId" column="bId" type="int" node="@bId"/>
-            <key-property name="aId" column="aId" type="int" node="@aId"/>
-        </composite-id>
-        <property name="y" type="string" node="."/>
-    </class>
-
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/Account.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping PUBLIC
-        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<hibernate-mapping>
-
-    <class entity-name="Customer" node="customer">
-        <id name="customerId" type="string" node="@id"/>
-        <component name="name">
-            <property name="first" column="`first`" type="string"/>
-            <property name="last" type="string"/>
-        </component>
-        <property name="address" type="string" node="address"/>
-        <map name="stuff">
-            <key column="customerId"/>
-            <map-key type="string" column="bar" node="@bar"/>
-            <element type="string" node="foo" column="foo"/>
-        </map>
-        <bag name="morestuff" node=".">
-            <key column="customerId"/>
-            <element type="integer" node="amount" column="amount"/>
-        </bag>
-        <list name="accounts" cascade="all">
-            <key column="customerId2"/>
-            <list-index column="acctno" base="1"/>
-            <one-to-many entity-name="Account" node="account"/>
-        </list>
-        <many-to-one name="location" node="location/@id" entity-name="Location" embed-xml="false"/>
-        <property name="description" node="." type="string"/>
-        <set name="unembedded" embed-xml="false">
-            <key column="x"/>
-            <element type="string" column="y" not-null="true"/>
-        </set>
-    </class>
-
-    <class entity-name="Account" table="`Account`" node="account">
-        <id name="accountId" type="string" node="@id"/>
-        <many-to-one name="customer" column="customerId" entity-name="Customer" cascade="all" embed-xml="true" />
-        <!--not-null="true"-->
-        <property name="balance" type="big_decimal" node="balance" precision="10" scale="2" />
-    </class>
-
-    <class entity-name="Location" node="location">
-        <id name="id" node="@id" type="long">
-            <generator class="increment"/>
-        </id>
-        <property name="address" type="string"/>
-    </class>
-
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/Dom4jTest.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.test.entitymode.dom4j.basic;
-import java.util.Map;
-
-import org.dom4j.DocumentFactory;
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
-import org.hibernate.criterion.Example;
-import org.hibernate.internal.util.xml.XMLHelper;
-import org.hibernate.transform.Transformers;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertNotNull;
-
-/**
- * @author Gavin King
- */
-public class Dom4jTest extends BaseCoreFunctionalTestCase {
-
-	public String[] getMappings() {
-		return new String[] {
-				"entitymode/dom4j/basic/Account.hbm.xml",
-				"entitymode/dom4j/basic/AB.hbm.xml",
-				"entitymode/dom4j/basic/Employer.hbm.xml"
-		};
-	}
-
-	public void configure(Configuration cfg) {
-		cfg.setProperty( Environment.DEFAULT_ENTITY_MODE, EntityMode.DOM4J.toString() );
-	}
-
-	@Test
-	public void testCompositeId() throws Exception {
-		Element a = DocumentFactory.getInstance().createElement( "a" );
-		a.addAttribute("id", "1");
-		a.addElement("x").setText("foo bar");
-		//Element bs = a.addElement("bs");
-		Element b = a.addElement("b");
-		//b.addElement("bId").setText("1");
-		//b.addElement("aId").setText("1");
-		b.addAttribute("bId", "1");
-		b.addAttribute("aId", "1");
-		b.setText("foo foo");
-		b = a.addElement("b");
-		//b.addElement("bId").setText("2");
-		//b.addElement("aId").setText("1");
-		b.addAttribute("bId", "2");
-		b.addAttribute("aId", "1");
-		b.setText("bar bar");
-		
-		Session s = openSession();
-		Transaction t = s.beginTransaction();
-		s.persist("A", a);
-		t.commit();
-		s.close();
-		
-		s = openSession();
-		t = s.beginTransaction();
-		a = (Element) s.createCriteria("A").uniqueResult();
-		assertEquals( a.elements("b").size(), 2 );
-		print(a);
-		s.delete("A", a);
-		t.commit();
-		s.close();
-	}
-
-	@Test
-	public void testDom4j() throws Exception {
-		Element acct = DocumentFactory.getInstance().createElement( "account" );
-		acct.addAttribute( "id", "abc123" );
-		acct.addElement( "balance" ).setText( "123.45" );
-		Element cust = acct.addElement( "customer" );
-		cust.addAttribute( "id", "xyz123" );
-		Element foo1 = cust.addElement( "stuff" ).addElement( "foo" );
-		foo1.setText( "foo" );
-		foo1.addAttribute("bar", "x");
-		Element foo2 = cust.element( "stuff" ).addElement( "foo" );
-		foo2.setText( "bar" );
-		foo2.addAttribute("bar", "y");
-		cust.addElement( "amount" ).setText( "45" );
-		cust.setText( "An example customer" );
-		Element name = cust.addElement( "name" );
-		name.addElement( "first" ).setText( "Gavin" );
-		name.addElement( "last" ).setText( "King" );
-
-		Element loc = DocumentFactory.getInstance().createElement( "location" );
-		loc.addElement( "address" ).setText( "Karbarook Avenue" );
-
-		print( acct );
-
-		Session s = openSession();
-		Transaction t = s.beginTransaction();
-		s.persist( "Location", loc );
-		cust.addElement( "location" ).addAttribute( "id", loc.attributeValue( "id" ) );
-		s.persist( "Account", acct );
-		t.commit();
-		s.close();
-
-		print( loc );
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.get( "Customer", "xyz123" );
-		print( cust );
-		acct = (Element) s.get( "Account", "abc123" );
-		print( acct );
-		assertEquals( acct.element( "customer" ), cust );
-		cust.element( "name" ).element( "first" ).setText( "Gavin A" );
-		Element foo3 = cust.element("stuff").addElement("foo");
-		foo3.setText("baz");
-		foo3.addAttribute("bar", "z");
-		cust.element("amount").setText("3");
-		cust.addElement("amount").setText("56");
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		acct.element( "balance" ).setText( "3456.12" );
-		cust.addElement( "address" ).setText( "Karbarook Ave" );
-
-		assertEquals( acct.element( "customer" ), cust );
-
-		cust.setText( "Still the same example!" );
-
-		s = openSession();
-		t = s.beginTransaction();
-		s.saveOrUpdate( "Account", acct );
-		t.commit();
-		s.close();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.get( "Customer", "xyz123" );
-		print( cust );
-		acct = (Element) s.get( "Account", "abc123" );
-		print( acct );
-		assertEquals( acct.element( "customer" ), cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createCriteria( "Customer" )
-				.add( Example.create( cust ) )
-				.uniqueResult();
-		print( cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		acct = (Element) s.createQuery( "from Account a left join fetch a.customer" )
-				.uniqueResult();
-		print( acct );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		Map m = (Map) s.createQuery( "select a as acc from Account a left join fetch a.customer" )
-			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
-		acct = (Element)m.get("acc"); 
-		print( acct );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		acct = (Element) s.createQuery( "from Account" ).uniqueResult();
-		print( acct );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.stuff" ).uniqueResult();
-		print( cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.morestuff" ).uniqueResult();
-		print( cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.morestuff" ).uniqueResult();
-		print( cust );
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.stuff" ).uniqueResult();
-		print( cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.accounts" ).uniqueResult();
-		Element a1 = cust.element( "accounts" ).addElement( "account" );
-		a1.addElement( "balance" ).setText( "12.67" );
-		a1.addAttribute( "id", "lkj345" );
-		a1.addAttribute("acnum", "0");
-		Element a2 = cust.element( "accounts" ).addElement( "account" );
-		a2.addElement( "balance" ).setText( "10000.00" );
-		a2.addAttribute( "id", "hsh987" );
-		a2.addAttribute("acnum", "1");
-		print( cust );
-		t.commit();
-		s.close();
-
-		System.out.println();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = (Element) s.createQuery( "from Customer c left join fetch c.accounts" ).uniqueResult();
-		print( cust );
-		t.commit();
-		s.close();
-
-		// clean up
-		s = openSession();
-		t = s.beginTransaction();
-		s.delete( "Account", acct );
-		s.delete( "Location", loc );
-		s.createQuery( "delete from Account" ).executeUpdate();
-		t.commit();
-		s.close();
-	}
-
-	@Test
-	public void testMapIndexEmision() throws Throwable {
-		Element acct = DocumentFactory.getInstance().createElement( "account" );
-		acct.addAttribute( "id", "abc123" );
-		acct.addElement( "balance" ).setText( "123.45" );
-		Element cust = acct.addElement( "customer" );
-		cust.addAttribute( "id", "xyz123" );
-		Element foo1 = cust.addElement( "stuff" ).addElement( "foo" );
-		foo1.setText( "foo" );
-		foo1.addAttribute("bar", "x");
-		Element foo2 = cust.element( "stuff" ).addElement( "foo" );
-		foo2.setText( "bar" );
-		foo2.addAttribute("bar", "y");
-		cust.addElement( "amount" ).setText( "45" );
-		cust.setText( "An example customer" );
-		Element name = cust.addElement( "name" );
-		name.addElement( "first" ).setText( "Gavin" );
-		name.addElement( "last" ).setText( "King" );
-
-		print( acct );
-
-		Element loc = DocumentFactory.getInstance().createElement( "location" );
-		loc.addElement( "address" ).setText( "Karbarook Avenue" );
-
-		Session s = openSession();
-		Transaction t = s.beginTransaction();
-		s.persist( "Location", loc );
-		cust.addElement( "location" ).addAttribute( "id", loc.attributeValue( "id" ) );
-		s.persist( "Account", acct );
-		t.commit();
-		s.close();
-
-		s = openSession();
-		t = s.beginTransaction();
-		cust = ( Element ) s.get( "Customer", "xyz123" );
-		print( cust );
-		assertEquals( "Incorrect stuff-map size", 2, cust.element( "stuff" ).elements( "foo" ).size() );
-		Element stuffElement = ( Element ) cust.element( "stuff" ).elements(  "foo" ).get( 0 );
-		assertNotNull( "No map-key value present", stuffElement.attribute( "bar" ) );
-		t.commit();
-		s.close();
-
-		s = openSession();
-		t = s.beginTransaction();
-		s.delete( "Account", acct );
-		s.delete( "Location", loc );
-		t.commit();
-		s.close();
-	}
-
-	public static void print(Element elt) throws Exception {
-		XMLHelper.dump( elt );
-	}
-
-
-// TODO : still need to figure out inheritence support within the DOM4J entity-mode
-//
-//	public void testSubtyping() throws Exception {
-//		Element employer = DocumentFactory.getInstance().createElement( "employer" );
-//		employer.addAttribute( "name", "JBoss" );
-//		Element gavin = employer.addElement( "techie" );
-//		gavin.addAttribute( "name", "Gavin" );
-//		Element ben = employer.addElement( "sales-dude" );
-//		ben.addAttribute( "name", "Ben" );
-//		print( employer );
-//
-//		Session s = openSession();
-//		Transaction t = s.beginTransaction();
-//		s.persist( "Employer", employer );
-//		Long eid = new Long( employer.attributeValue( "id" ) );
-//		t.commit();
-//		s.close();
-//
-//		s = openSession();
-//		t = s.beginTransaction();
-//		employer = (Element) s.get( "Employer", eid );
-//		print( employer );
-//		s.delete( "Employer", employer );
-//		t.commit();
-//		s.close();
-//
-//		Element dept = DocumentFactory.getInstance().createElement( "department" );
-//		dept.addAttribute( "name", "engineering" );
-//		Element steve = dept.addElement( "manager" ).addElement( "techie" );
-//		steve.addAttribute( "name", "Steve" );
-//		print( dept );
-//
-//		s = openSession();
-//		t = s.beginTransaction();
-//		s.persist( "Department", dept );
-//		Long did = new Long( dept.attributeValue( "id" ) );
-//		t.commit();
-//		s.close();
-//
-//		s = openSession();
-//		t = s.beginTransaction();
-//		dept = ( Element ) s.load( "Department", did );
-//		print( dept );
-//		s.delete( "Department", dept );
-//		t.commit();
-//		s.close();
-//	}
-
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/Employer.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping PUBLIC 
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-<hibernate-mapping>
-
-	<class entity-name="Employer" node="employer">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-		<property name="name" node="@name" type="string"/>
-		<set name="employees" node="." cascade="all,delete-orphan" fetch="join" lazy="false">
-			<key not-null="true" column="employerId"/>
-			<one-to-many entity-name="Employee" />
-		</set>
-	</class>
-
-	<class entity-name="Employee" node="employee">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-		<discriminator column="`role`" type="string" length="10"/>
-		<property name="name" node="@name" type="string"/>
-		<subclass entity-name="Techie" node="techie" />
-		<subclass entity-name="Salesdude" node="sales-dude"/>
-	</class>
-
-    <class entity-name="Department" node="department">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-        <property name="name" node="@name" type="string"/>
-        <many-to-one name="manager" entity-name="Employee" cascade="all" fetch="join" lazy="false" embed-xml="true" node="manager" />
-    </class>
-	
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/component/Component.java
+++ /dev/null
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
-package org.hibernate.test.entitymode.dom4j.component;
-
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-public class Component {
-	private ComponentReference reference;
-
-	public Component() {
-	}
-
-	public Component(ComponentReference reference) {
-		this.reference = reference;
-	}
-
-	public ComponentReference getReference() {
-		return reference;
-	}
-
-	public void setReference(ComponentReference reference) {
-		this.reference = reference;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/component/ComponentOwner.java
+++ /dev/null
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
-package org.hibernate.test.entitymode.dom4j.component;
-
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-public class ComponentOwner {
-	private Long id;
-	private Component component;
-
-	public ComponentOwner() {
-	}
-
-	public ComponentOwner(Component component) {
-		this.component = component;
-	}
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public Component getComponent() {
-		return component;
-	}
-
-	public void setComponent(Component component) {
-		this.component = component;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/component/ComponentReference.java
+++ /dev/null
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
-package org.hibernate.test.entitymode.dom4j.component;
-
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-public class ComponentReference {
-	private Long id;
-
-	public ComponentReference() {
-	}
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/component/Dom4jComponentTest.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.test.entitymode.dom4j.component;
-
-import org.hibernate.EntityMode;
-import org.hibernate.Session;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
-
-import org.junit.Test;
-
-import org.hibernate.testing.FailureExpected;
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-
-/**
- * @author Steve Ebersole
- */
-public class Dom4jComponentTest extends BaseCoreFunctionalTestCase {
-	@Override
-	public String[] getMappings() {
-		return new String[] { "entitymode/dom4j/component/Mapping.hbm.xml" };
-	}
-
-	@Override
-	public void configure(Configuration cfg) {
-		cfg.setProperty( Environment.DEFAULT_ENTITY_MODE, EntityMode.DOM4J.toString() );
-	}
-
-	@Test
-	@FailureExpected( jiraKey = "HHH-1907" )
-	public void testSetAccessorsFailureExpected() {
-		// An example of part of the issue discussed in HHH-1907
-		Session session = openSession();
-		session.beginTransaction();
-		session.getSession( EntityMode.POJO ).save( new ComponentOwner( new Component( new ComponentReference() ) ) );
-		session.getTransaction().commit();
-		session.close();
-
-		session = openSession();
-		session.beginTransaction();
-		session.createQuery( "from ComponentOwner" ).list();
-		session.getTransaction().commit();
-		session.close();
-
-		session = openSession();
-		session.beginTransaction();
-		session.createQuery( "delete ComponentOwner" ).executeUpdate();
-		session.createQuery( "delete ComponentReference" ).executeUpdate();
-		session.getTransaction().commit();
-		session.close();
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/component/Mapping.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-
-<!DOCTYPE hibernate-mapping PUBLIC
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
-  ~ indicated by the @author tags or express copyright attribution
-  ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
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
-  ~
-  -->
-
-<hibernate-mapping package="org.hibernate.test.entitymode.dom4j.component">
-
-    <class name="ComponentOwner">
-        <id name="id" type="long" node="@id">
-            <generator class="increment"/>
-        </id>
-        <component name="component" class="Component">
-            <many-to-one name="reference" class="ComponentReference" embed-xml="false" cascade="all"/>
-        </component>
-    </class>
-
-    <class name="ComponentReference">
-        <id name="id" type="long" node="@id">
-            <generator class="increment"/>
-        </id>
-    </class>
-
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/many2one/Car.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping PUBLIC 
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-	
-<hibernate-mapping package="org.hibernate.test.entitymode.dom4j.many2one">
-
-	<class name="Car" lazy="false" node="car">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-		<property name="model"  type="string" node="model"/>
-		<many-to-one name="carType" node="carType" class="CarType"/>
-		<set name="carParts" node="." cascade="all">
-			<key column="car" not-null="true"/>
-			<one-to-many class="CarPart" node="carPart" embed-xml="false"/>
-		</set>
-	</class>
-	
-	<class name="CarType" lazy="true" node="carType">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-		<property name="typeName" type="string" node="typeName"/>
-	</class>
-	
-	<class name="CarPart" node="carPart">
-		<id name="id" node="@id" type="long">
-			<generator class="increment"/>
-		</id>
-		<property name="partName" type="string" node="partName"/>
-	</class>
-	
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/many2one/Car.java
+++ /dev/null
-package org.hibernate.test.entitymode.dom4j.many2one;
-import java.util.HashSet;
-import java.util.Set;
-
-/**
- * @author Paco Hern�ndez
- */
-public class Car implements java.io.Serializable {
-	private long id;
-	private String model;
-	private CarType carType;
-	private Set carParts = new HashSet();
-	
-	/**
-	 * @return Returns the carType.
-	 */
-	public CarType getCarType() {
-		return carType;
-	}
-	/**
-	 * @param carType The carType to set.
-	 */
-	public void setCarType(CarType carType) {
-		this.carType = carType;
-	}
-	/**
-	 * @return Returns the id.
-	 */
-	public long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the model.
-	 */
-	public String getModel() {
-		return model;
-	}
-	/**
-	 * @param model The model to set.
-	 */
-	public void setModel(String model) {
-		this.model = model;
-	}
-	public Set getCarParts() {
-		return carParts;
-	}
-	public void setCarParts(Set carParts) {
-		this.carParts = carParts;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/many2one/CarPart.java
+++ /dev/null
-package org.hibernate.test.entitymode.dom4j.many2one;
-
-
-/**
- * @author Paco Hern�ndez
- */
-public class CarPart implements java.io.Serializable {
-	private long id;
-	private String partName;
-
-	/**
-	 * @return Returns the id.
-	 */
-	public long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the typeName.
-	 */
-	public String getPartName() {
-		return partName;
-	}
-	/**
-	 * @param typeName The typeName to set.
-	 */
-	public void setPartName(String typeName) {
-		this.partName = typeName;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/many2one/CarType.java
+++ /dev/null
-package org.hibernate.test.entitymode.dom4j.many2one;
-
-
-/**
- * @author Paco Hern�ndez
- */
-public class CarType implements java.io.Serializable {
-	private long id;
-	private String typeName;
-
-	/**
-	 * @return Returns the id.
-	 */
-	public long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the typeName.
-	 */
-	public String getTypeName() {
-		return typeName;
-	}
-	/**
-	 * @param typeName The typeName to set.
-	 */
-	public void setTypeName(String typeName) {
-		this.typeName = typeName;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/many2one/Dom4jManyToOneTest.java
+++ /dev/null
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
-package org.hibernate.test.entitymode.dom4j.many2one;
-import java.util.List;
-
-import org.dom4j.Element;
-import org.dom4j.io.OutputFormat;
-import org.dom4j.io.XMLWriter;
-import org.hibernate.EntityMode;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-
-import static org.junit.Assert.assertTrue;
-
-/**
- * @author Paco Hern�ndez
- */
-public class Dom4jManyToOneTest extends BaseCoreFunctionalTestCase {
-	@Override
-	public String[] getMappings() {
-		return new String[] { "entitymode/dom4j/many2one/Car.hbm.xml" };
-	}
-
-	@Test
-	public void testDom4jOneToMany() throws Exception {
-		Session s = openSession();
-		Transaction t = s.beginTransaction();
-
-		CarType carType = new CarType();
-		carType.setTypeName("Type 1");
-		s.save(carType);
-
-		Car car = new Car();
-		car.setCarType(carType);
-		car.setModel("Model 1");
-		s.save(car);
-		
-		CarPart carPart1 = new CarPart();
-		carPart1.setPartName("chassis");
-		car.getCarParts().add(carPart1);
-		
-		t.commit();
-		s.close();
-
-		s = openSession();
-		Session dom4jSession = s.getSession( EntityMode.DOM4J );
-		t = s.beginTransaction();
-
-		Element element = (Element) dom4jSession.createQuery( "from Car c join fetch c.carParts" ).uniqueResult();
-
-		String expectedResult = "<car id=\"" + 
-			car.getId() + 
-			"\"><carPart>" + 
-			carPart1.getId() +
-			"</carPart><model>Model 1</model><carType id=\"" +
-			carType.getId() +
-			"\"><typeName>Type 1</typeName></carType></car>";
-				
-		print(element);
-		assertTrue(element.asXML().equals(expectedResult));
-		
-		s.createQuery("delete from CarPart").executeUpdate();
-		s.createQuery("delete from Car").executeUpdate();
-		s.createQuery("delete from CarType").executeUpdate();
-		
-		t.commit();
-		s.close();
-	}
-
-	@Test
-	public void testDom4jManyToOne() throws Exception {
-
-		Session s = openSession();
-		Transaction t = s.beginTransaction();
-
-		CarType carType = new CarType();
-		carType.setTypeName("Type 1");
-		s.save(carType);
-
-		Car car1 = new Car();
-		car1.setCarType(carType);
-		car1.setModel("Model 1");
-		s.save(car1);
-		
-		Car car2 = new Car();
-		car2.setCarType(carType);
-		car2.setModel("Model 2");
-		s.save(car2);
-		
-		t.commit();
-		s.close();
-
-		s = openSession();
-		Session dom4jSession = s.getSession( EntityMode.DOM4J );
-		t = s.beginTransaction();
-
-		List list = dom4jSession.createQuery( "from Car c join fetch c.carType order by c.model asc" ).list();
-
-		String[] expectedResults = new String[] {
-				"<car id=\"" + 
-				car1.getId() +
-				"\"><model>Model 1</model><carType id=\"" + 
-				carType.getId() +
-				"\"><typeName>Type 1</typeName></carType></car>",
-				"<car id=\"" + 
-				car2.getId() +
-				"\"><model>Model 2</model><carType id=\"" +
-				carType.getId() +
-				"\"><typeName>Type 1</typeName></carType></car>"
-		};
-				
-		for (int i = 0; i < list.size(); i++) {
-			Element element = (Element) list.get(i);
-
-			print(element);
-			assertTrue(element.asXML().equals(expectedResults[i]));
-		}
-		
-		s.createQuery("delete from Car").executeUpdate();
-		s.createQuery("delete from CarType").executeUpdate();
-		
-		t.commit();
-		s.close();
-	}
-
-	public static void print(Element elt) throws Exception {
-		OutputFormat outformat = OutputFormat.createPrettyPrint();
-		// outformat.setEncoding(aEncodingScheme);
-		XMLWriter writer = new XMLWriter( System.out, outformat );
-		writer.write( elt );
-		writer.flush();
-		// System.out.println( elt.asXML() );
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/map/basic/DynamicClassTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/entitymode/map/basic/DynamicClassTest.java
-		assertTrue( "Incorrectly handled default_entity_mode", s.getEntityMode() == EntityMode.MAP );
-		Session other = s.getSession( EntityMode.MAP );
-		assertEquals( "openSession() using same entity-mode returned new session", s, other );
-
-		other = s.getSession( EntityMode.POJO );
-		other.close();
-		assertTrue( !other.isOpen() );
-
-		s.close();
-
-		s = openSession();
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/multi/MultiRepresentationTest.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.test.entitymode.multi;
-
-import java.sql.Date;
-import java.util.Iterator;
-import java.util.List;
-
-import org.dom4j.DocumentFactory;
-import org.dom4j.Element;
-import org.dom4j.io.OutputFormat;
-import org.dom4j.io.XMLWriter;
-
-import org.hibernate.EntityMode;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertTrue;
-
-/**
- * Implementation of MultiRepresentationTest.
- *
- * @author Steve Ebersole
- */
-public class MultiRepresentationTest extends BaseCoreFunctionalTestCase {
-	@Override
-	public String[] getMappings() {
-		return new String[] { "entitymode/multi/Stock.hbm.xml", "entitymode/multi/Valuation.hbm.xml" };
-	}
-
-	@Test
-	public void testPojoRetreival() {
-		TestData testData = new TestData();
-		testData.create();
-
-		Session session = openSession();
-		Transaction txn = session.beginTransaction();
-
-		Stock stock = ( Stock ) session.get( Stock.class, new Long( 1 ) );
-		assertEquals( "Something wrong!", new Long( 1 ), stock.getId() );
-
-		txn.commit();
-		session.close();
-
-		testData.destroy();
-	}
-
-	@Test
-	public void testDom4jRetreival() {
-		TestData testData = new TestData();
-		testData.create();
-
-		Session session = openSession();
-		Transaction txn = session.beginTransaction();
-		org.hibernate.Session dom4j = session.getSession( EntityMode.DOM4J );
-
-		Object rtn = dom4j.get( Stock.class.getName(), testData.stockId );
-		Element element = ( Element ) rtn;
-
-		assertEquals( "Something wrong!", testData.stockId, Long.valueOf( element.attributeValue( "id" ) ) );
-
-		System.out.println( "**** XML: ****************************************************" );
-		prettyPrint( element );
-		System.out.println( "**************************************************************" );
-
-		Element currVal = element.element( "currentValuation" );
-
-		System.out.println( "**** XML: ****************************************************" );
-		prettyPrint( currVal );
-		System.out.println( "**************************************************************" );
-
-		txn.rollback();
-		session.close();
-
-		testData.destroy();
-	}
-
-	@Test
-	public void testDom4jSave() {
-		TestData testData = new TestData();
-		testData.create();
-
-		Session pojos = openSession();
-		Transaction txn = pojos.beginTransaction();
-		org.hibernate.Session dom4j = pojos.getSession( EntityMode.DOM4J );
-
-		Element stock = DocumentFactory.getInstance().createElement( "stock" );
-		stock.addElement( "tradeSymbol" ).setText( "IBM" );
-
-		Element val = stock.addElement( "currentValuation" ).addElement( "valuation" );
-		val.appendContent( stock );
-		val.addElement( "valuationDate" ).setText( new java.util.Date().toString() );
-		val.addElement( "value" ).setText( "121.00" );
-
-		dom4j.save( Stock.class.getName(), stock );
-		dom4j.flush();
-
-		txn.rollback();
-		pojos.close();
-
-		assertTrue( !pojos.isOpen() );
-		assertTrue( !dom4j.isOpen() );
-
-		prettyPrint( stock );
-
-		testData.destroy();
-	}
-
-	@Test
-	public void testDom4jHQL() {
-		TestData testData = new TestData();
-		testData.create();
-
-		Session session = openSession();
-		Transaction txn = session.beginTransaction();
-		org.hibernate.Session dom4j = session.getSession( EntityMode.DOM4J );
-
-		List result = dom4j.createQuery( "from Stock" ).list();
-
-		assertEquals( "Incorrect result size", 1, result.size() );
-		Element element = ( Element ) result.get( 0 );
-		assertEquals( "Something wrong!", testData.stockId, Long.valueOf( element.attributeValue( "id" ) ) );
-
-		System.out.println( "**** XML: ****************************************************" );
-		prettyPrint( element );
-		System.out.println( "**************************************************************" );
-
-		txn.rollback();
-		session.close();
-
-		testData.destroy();
-	}
-
-	private class TestData {
-		private Long stockId;
-
-		private void create() {
-			Session session = sessionFactory().openSession();
-			session.beginTransaction();
-			Stock stock = new Stock();
-			stock.setTradeSymbol( "JBOSS" );
-			Valuation valuation = new Valuation();
-			valuation.setStock( stock );
-			valuation.setValuationDate( new Date( new java.util.Date().getTime() ) );
-			valuation.setValue( new Double( 200.0 ) );
-			stock.setCurrentValuation( valuation );
-			stock.getValuations().add( valuation );
-
-			session.save( stock );
-			session.save( valuation );
-
-			session.getTransaction().commit();
-			session.close();
-
-			stockId = stock.getId();
-		}
-
-		private void destroy() {
-			Session session = sessionFactory().openSession();
-			session.beginTransaction();
-			Iterator stocks = session.createQuery( "from Stock" ).list().iterator();
-			while ( stocks.hasNext() ) {
-				final Stock stock = ( Stock ) stocks.next();
-				stock.setCurrentValuation( null );
-				session.flush();
-				Iterator valuations = stock.getValuations().iterator();
-				while ( valuations.hasNext() ) {
-					session.delete( valuations.next() );
-				}
-				session.delete( stock );
-			}
-			session.getTransaction().commit();
-			session.close();
-		}
-	}
-
-	private void prettyPrint(Element element) {
-		//System.out.println( element.asXML() );
-		try {
-			OutputFormat format = OutputFormat.createPrettyPrint();
-			new XMLWriter( System.out, format ).write( element );
-			System.out.println();
-		}
-		catch ( Throwable t ) {
-			System.err.println( "Unable to pretty print element : " + t );
-		}
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/multi/Stock.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping
-        SYSTEM
-        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
-
-<hibernate-mapping package="org.hibernate.test.entitymode.multi">
-
-	<class table="STOCK" name="Stock" node="stock">
-
-		<id name="id" column="STOCK_ID" node="@id">
-			<generator class="increment"/>
-		</id>
-
-		<property name="tradeSymbol" type="string" column="SYMBOL"/>
-
-		<many-to-one name="currentValuation" class="Valuation" column="CURR_VAL_ID" cascade="all" />
-
-		<set name="valuations" cascade="all" lazy="true">
-			<key column="STOCK_ID"/>
-			<one-to-many class="Valuation"/>
-		</set>
-	</class>
-
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/multi/Stock.java
+++ /dev/null
-// $Id: Stock.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
-package org.hibernate.test.entitymode.multi;
-import java.util.HashSet;
-import java.util.Set;
-
-/**
- * POJO implementation of Stock entity.
- *
- * @author Steve Ebersole
- */
-public class Stock {
-	private Long id;
-	private String tradeSymbol;
-	private Valuation currentValuation;
-	private Set valuations = new HashSet();
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getTradeSymbol() {
-		return tradeSymbol;
-	}
-
-	public void setTradeSymbol(String tradeSymbol) {
-		this.tradeSymbol = tradeSymbol;
-	}
-
-	public Valuation getCurrentValuation() {
-		return currentValuation;
-	}
-
-	public void setCurrentValuation(Valuation currentValuation) {
-		this.currentValuation = currentValuation;
-	}
-
-	public Set getValuations() {
-		return valuations;
-	}
-
-	public void setValuations(Set valuations) {
-		this.valuations = valuations;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/multi/Valuation.hbm.xml
+++ /dev/null
-<?xml version="1.0"?>
-<!DOCTYPE hibernate-mapping
-        SYSTEM
-        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
-
-<hibernate-mapping package="org.hibernate.test.entitymode.multi">
-
-	<class table="STOCK_VAL" name="Valuation" node="valuation">
-
-		<id name="id" column="VAL_ID" node="@id">
-			<generator class="increment"/>
-		</id>
-
-		<many-to-one name="stock" embed-xml="false"
-			class="Stock" column="STOCK_ID" cascade="none" />
-
-		<property name="valuationDate" type="date" column="DT"/>
-		<property name="value" type="double" column="VAL"/>
-
-	</class>
-
-</hibernate-mapping>
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/multi/Valuation.java
+++ /dev/null
-// $Id: Valuation.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
-package org.hibernate.test.entitymode.multi;
-import java.util.Date;
-
-/**
- * Implementation of Valuation.
- *
- * @author Steve Ebersole
- */
-public class Valuation {
-	private Long id;
-	private Stock stock;
-	private Date valuationDate;
-	private Double value;
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public Stock getStock() {
-		return stock;
-	}
-
-	public void setStock(Stock stock) {
-		this.stock = stock;
-	}
-
-	public Date getValuationDate() {
-		return valuationDate;
-	}
-
-	public void setValuationDate(Date valuationDate) {
-		this.valuationDate = valuationDate;
-	}
-
-	public Double getValue() {
-		return value;
-	}
-
-	public void setValue(Double value) {
-		this.value = value;
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+import org.hibernate.engine.internal.TwoPhaseLoad;
-import org.hibernate.engine.internal.TwoPhaseLoad;
-import org.hibernate.sql.QuerySelect;
-import org.hibernate.sql.Select;
+import org.hibernate.tuple.entity.EntityTuplizer;
-	private void checkEntityMode(EntityMode entityMode) {
-		if ( EntityMode.POJO != entityMode ) {
-			throw new IllegalArgumentException( "Unhandled EntityMode : " + entityMode );
-		}
-	}
-
-	private void checkEntityMode(SessionImplementor session) {
-		checkEntityMode( session.getEntityMode() );
-	}
-
+	@Override
-	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
-	throws HibernateException {
-		return getPropertyValues( object, session.getEntityMode() );
+	@Override
+	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
+		return getPropertyValues( object );
-	public Class getMappedClass(EntityMode entityMode) {
-		checkEntityMode( entityMode );
-		return Custom.class;
-	}
-
-	public boolean implementsLifecycle(EntityMode entityMode) {
-		checkEntityMode( entityMode );
-		return false;
-	}
-
-	public boolean implementsValidatable(EntityMode entityMode) {
-		checkEntityMode( entityMode );
+	@Override
+	public boolean implementsLifecycle() {
-	public Class getConcreteProxyClass(EntityMode entityMode) {
-		checkEntityMode( entityMode );
+	@Override
+	public Class getConcreteProxyClass() {
-	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
-		setPropertyValue( object, 0, values[0], entityMode );
+	@Override
+	public void setPropertyValues(Object object, Object[] values) {
+		setPropertyValue( object, 0, values[0] );
-	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public void setPropertyValue(Object object, int i, Object value) {
-	public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public Object[] getPropertyValues(Object object) throws HibernateException {
-	public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public Object getPropertyValue(Object object, int i) throws HibernateException {
-	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
-	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public Serializable getIdentifier(Object object) throws HibernateException {
+	@Override
-		checkEntityMode( session );
-	public void setIdentifier(Object object, Serializable id, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
-		( (Custom) object ).id = (String) id;
-	}
-
+	@Override
-		checkEntityMode( session );
-	public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
+	@Override
+	public Object getVersion(Object object) throws HibernateException {
-	public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
-		checkEntityMode( entityMode );
-		return instantiate( id );
-	}
-
-	private Object instantiate(Serializable id) {
+	@Override
+	public Object instantiate(Serializable id, SessionImplementor session) {
-	public Object instantiate(Serializable id, SessionImplementor session) {
-		checkEntityMode( session );
-		return instantiate( id );
-	}
-
-	public boolean isInstance(Object object, EntityMode entityMode) {
-		checkEntityMode( entityMode );
+	@Override
+	public boolean isInstance(Object object) {
-	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
-		checkEntityMode( entityMode );
+	@Override
+	public boolean hasUninitializedLazyProperties(Object object) {
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
-		checkEntityMode( entityMode );
-		( ( Custom ) entity ).id = ( String ) currentId;
-	}
-
+	@Override
-		checkEntityMode( session );
-	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
-		checkEntityMode( entityMode );
+	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
-		SessionImplementor session
-	) throws HibernateException {
+		SessionImplementor session) throws HibernateException {
-		SessionImplementor session
-	) throws HibernateException {
+		SessionImplementor session) throws HibernateException {
-	public Object getPropertyValue(Object object, String propertyName)
-		throws HibernateException {
-		throw new UnsupportedOperationException();
-	}
-
+	@Override
-	public EntityMode guessEntityMode(Object object) {
-		if ( !isInstance(object, EntityMode.POJO) ) {
-			return null;
-		}
-		else {
-			return EntityMode.POJO;
-		}
-	}
-
+	@Override
-	public boolean isDynamic() {
-		return false;
-	}
-
+	@Override
-	public void applyFilters(QuerySelect select, String alias, Map filters) {
-	}
-
-	public void applyFilters(Select select, String alias, Map filters) {
-	}
-
-
+	@Override
+	@Override
-	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
-	throws HibernateException {
+	@Override
+	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
+	@Override
+	@Override
+	@Override
+	@Override
-	public Type[] getNaturalIdentifierTypes() {
-		return null;
-	}
-
+	@Override
+	@Override
-	public boolean isInstrumented(EntityMode entityMode) {
+	@Override
+	public boolean isInstrumented() {
+	@Override
+	@Override
+	@Override
+	@Override
-	public boolean hasGeneratedProperties() {
-		return false;
-	}
-
+	@Override
-	public String[] getOrphanRemovalOneToOnePaths() {
-		return null;
-	}
-
+	@Override
+	@Override
+	@Override
+	@Override
+	public EntityMode getEntityMode() {
+		return EntityMode.POJO;
+	}
+
+	@Override
+	public EntityTuplizer getEntityTuplizer() {
+		return null;
+	}
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/MapTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/MapTest.java
-		Session s = openSession().getSession(EntityMode.MAP);
+		Session s = openSession();
-		s = openSession().getSession(EntityMode.MAP);
+		s = openSession();
-		s = openSession().getSession(EntityMode.MAP);
+		s = openSession();
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
-		if ( session.getEntityMode()==EntityMode.DOM4J ) {
-			throw new IllegalStateException("dom4j not supported");
-		}
-		else {
-			return new PersistentMyList( session, (IMyList) collection );
-		}
+		return new PersistentMyList( session, (IMyList) collection );
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
-		if ( session.getEntityMode() == EntityMode.DOM4J ) {
-			throw new IllegalStateException( "dom4j not supported" );
-		}
-		else {
-			return new PersistentDefaultableList( session, ( List ) collection );
-		}
+		return new PersistentDefaultableList( session, ( List ) collection );
--- a/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
-		assertTrue( type.isSame( original, copy, EntityMode.POJO ) );
+		assertTrue( type.isSame( original, copy ) );
-		assertTrue( type.isEqual( original, copy, EntityMode.POJO ) );
-		assertTrue( type.isEqual( original, copy, EntityMode.POJO, null ) );
+		assertTrue( type.isEqual( original, copy ) );
+		assertTrue( type.isEqual( original, copy, null ) );
-		assertFalse( type.isSame( original, different, EntityMode.POJO ) );
+		assertFalse( type.isSame( original, different ) );
+		assertFalse( type.isEqual( original, different ) );
-		assertFalse( type.isEqual( original, different, EntityMode.POJO ) );
-		assertFalse( type.isEqual( original, different, EntityMode.POJO, null ) );
+		assertFalse( type.isEqual( original, different, null ) );
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
-					? CollectionHelper.mapOfSize( tupleSize )
+					? CollectionHelper.<String, HqlTupleElementImpl>mapOfSize( tupleSize )
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
-import org.hibernate.EntityMode;
-			return classMetadata.getIdentifier( entity, EntityMode.POJO );
+			return classMetadata.getIdentifier( entity );
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
-import org.hibernate.EntityMode;
-		Object[] newState = metadata.getPropertyValues( entity, EntityMode.POJO );
+		Object[] newState = metadata.getPropertyValues( entity );
-			if ( !types[index].isEqual( state[index], newState[index], EntityMode.POJO ) ) {
+			if ( !types[index].isEqual( state[index], newState[index] ) ) {
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AttributeFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AttributeFactory.java
-			return embeddableType.getHibernateType().getTuplizerMapping()
-					.getTuplizer( EntityMode.POJO )
+			return embeddableType.getHibernateType()
+					.getComponentTuplizer()
-			return componentType.getTuplizerMapping()
-					.getTuplizer( EntityMode.POJO )
+			return componentType.getComponentTuplizer()
-					return entityMetamodel.getTuplizer( EntityMode.POJO )
+					return entityMetamodel.getTuplizer()
-			return entityMetamodel.getTuplizer( EntityMode.POJO ).getIdentifierGetter().getMember();
+			return entityMetamodel.getTuplizer().getIdentifierGetter().getMember();
-			return entityMetamodel.getTuplizer( EntityMode.POJO ).getVersionGetter().getMember();
+			return entityMetamodel.getTuplizer().getVersionGetter().getMember();
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+import org.hibernate.tuple.entity.EntityTuplizer;
-		public GoofyProvider(org.hibernate.mapping.PersistentClass persistentClass,
-								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
-								   SessionFactoryImplementor sf,
-								   Mapping mapping) {
+		@SuppressWarnings( {"UnusedParameters"})
+		public GoofyProvider(
+				org.hibernate.mapping.PersistentClass persistentClass,
+				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
+				SessionFactoryImplementor sf,
+				Mapping mapping) {
+		@Override
+		public EntityMode getEntityMode() {
+			return null;
+		}
+
+		@Override
+		public EntityTuplizer getEntityTuplizer() {
+			return null;
+		}
+
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Serializable[0];
+		@Override
-			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Serializable[0];
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return 0;  //To change body of implemented methods use File | Settings | File Templates.
+			return 0;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new int[0];
+		@Override
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Object[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
-		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
+			return null;
-		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
+			return null;
-		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
-		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
-		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
-		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
+			return null;
-		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
-		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
+		@Override
-			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Type[0];
+		@Override
-			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new String[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new ValueInclusion[0];
+		@Override
-			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new ValueInclusion[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new boolean[0];
+		@Override
-			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new CascadeStyle[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+			return new Object[0];
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public EntityMode guessEntityMode(Object object) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
+			return null;
-		public boolean isInstrumented(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean isInstrumented() {
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return false;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
-				throws HibernateException {
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
+			return new Object[0];
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public Class getMappedClass(EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public boolean implementsLifecycle(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Class getMappedClass() {
+			return null;
-		public boolean implementsValidatable(EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean implementsLifecycle() {
+			return false;
-		public Class getConcreteProxyClass(EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Class getConcreteProxyClass() {
+			return null;
-		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void setPropertyValues(Object object, Object[] values) {
-		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
-				throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public void setPropertyValue(Object object, int i, Object value) {
-		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
-			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object[] getPropertyValues(Object object) {
+			return new Object[0];
-		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getPropertyValue(Object object, int i) {
+			return null;
-		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
-				throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getPropertyValue(Object object, String propertyName) {
+			return null;
-		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Serializable getIdentifier(Object object) {
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
-			//To change body of implemented methods use File | Settings | File Templates.
+			return null;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public Object getVersion(Object object) {
+			return null;
+		@Override
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
-		}
-
-		public boolean isInstance(Object object, EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+			return null;
-		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
-			return false;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean isInstance(Object object) {
+			return false;
-		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
-			//To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public boolean hasUninitializedLazyProperties(Object object) {
+			return false;
+		@Override
-			//To change body of implemented methods use File | Settings | File Templates.
-		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		@Override
+		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
+			return null;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
-import org.hibernate.EntityMode;
+@SuppressWarnings( {"deprecation"})
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
-    public EntityMode getEntityMode() {
-        return delegate.getEntityMode();
-    }
-
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
+	@Override
--- a/hibernate-envers/src/main/java/org/hibernate/envers/tools/Tools.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/tools/Tools.java
-        return entityPersister.getClassMetadata().getMappedClass(session.getEntityMode());
+        return entityPersister.getMappedClass();

Lines added containing method: 2444. Lines removed containing method: 5458. Tot = 7902
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
