diff --git a/build.gradle b/build.gradle
new file mode 100644
index 0000000000..83f17af666
--- /dev/null
+++ b/build.gradle
@@ -0,0 +1,155 @@
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
\ No newline at end of file
diff --git a/core/pom.xml b/core/pom.xml
deleted file mode 100644
index daee9a10dc..0000000000
--- a/core/pom.xml
+++ /dev/null
@@ -1,238 +0,0 @@
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
diff --git a/core/src/test/resources/log4j.properties b/core/src/test/resources/log4j.properties
deleted file mode 100644
index 3522e064dd..0000000000
--- a/core/src/test/resources/log4j.properties
+++ /dev/null
@@ -1,33 +0,0 @@
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
\ No newline at end of file
diff --git a/hibernate-core/hibernate-core.gradle b/hibernate-core/hibernate-core.gradle
new file mode 100644
index 0000000000..e69de29bb2
diff --git a/core/src/main/antlr/hql-sql.g b/hibernate-core/src/main/antlr/hql-sql.g
similarity index 100%
rename from core/src/main/antlr/hql-sql.g
rename to hibernate-core/src/main/antlr/hql-sql.g
diff --git a/core/src/main/antlr/hql.g b/hibernate-core/src/main/antlr/hql.g
similarity index 100%
rename from core/src/main/antlr/hql.g
rename to hibernate-core/src/main/antlr/hql.g
diff --git a/core/src/main/antlr/order-by-render.g b/hibernate-core/src/main/antlr/order-by-render.g
similarity index 100%
rename from core/src/main/antlr/order-by-render.g
rename to hibernate-core/src/main/antlr/order-by-render.g
diff --git a/core/src/main/antlr/order-by.g b/hibernate-core/src/main/antlr/order-by.g
similarity index 100%
rename from core/src/main/antlr/order-by.g
rename to hibernate-core/src/main/antlr/order-by.g
diff --git a/core/src/main/antlr/sql-gen.g b/hibernate-core/src/main/antlr/sql-gen.g
similarity index 100%
rename from core/src/main/antlr/sql-gen.g
rename to hibernate-core/src/main/antlr/sql-gen.g
diff --git a/core/src/main/java/org/hibernate/AnnotationException.java b/hibernate-core/src/main/java/org/hibernate/AnnotationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/AnnotationException.java
rename to hibernate-core/src/main/java/org/hibernate/AnnotationException.java
diff --git a/core/src/main/java/org/hibernate/AssertionFailure.java b/hibernate-core/src/main/java/org/hibernate/AssertionFailure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/AssertionFailure.java
rename to hibernate-core/src/main/java/org/hibernate/AssertionFailure.java
diff --git a/core/src/main/java/org/hibernate/Cache.java b/hibernate-core/src/main/java/org/hibernate/Cache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Cache.java
rename to hibernate-core/src/main/java/org/hibernate/Cache.java
diff --git a/core/src/main/java/org/hibernate/CacheMode.java b/hibernate-core/src/main/java/org/hibernate/CacheMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/CacheMode.java
rename to hibernate-core/src/main/java/org/hibernate/CacheMode.java
diff --git a/core/src/main/java/org/hibernate/CallbackException.java b/hibernate-core/src/main/java/org/hibernate/CallbackException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/CallbackException.java
rename to hibernate-core/src/main/java/org/hibernate/CallbackException.java
diff --git a/core/src/main/java/org/hibernate/ConnectionReleaseMode.java b/hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ConnectionReleaseMode.java
rename to hibernate-core/src/main/java/org/hibernate/ConnectionReleaseMode.java
diff --git a/core/src/main/java/org/hibernate/Criteria.java b/hibernate-core/src/main/java/org/hibernate/Criteria.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Criteria.java
rename to hibernate-core/src/main/java/org/hibernate/Criteria.java
diff --git a/core/src/main/java/org/hibernate/DuplicateMappingException.java b/hibernate-core/src/main/java/org/hibernate/DuplicateMappingException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/DuplicateMappingException.java
rename to hibernate-core/src/main/java/org/hibernate/DuplicateMappingException.java
diff --git a/core/src/main/java/org/hibernate/EmptyInterceptor.java b/hibernate-core/src/main/java/org/hibernate/EmptyInterceptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/EmptyInterceptor.java
rename to hibernate-core/src/main/java/org/hibernate/EmptyInterceptor.java
diff --git a/core/src/main/java/org/hibernate/EntityMode.java b/hibernate-core/src/main/java/org/hibernate/EntityMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/EntityMode.java
rename to hibernate-core/src/main/java/org/hibernate/EntityMode.java
diff --git a/core/src/main/java/org/hibernate/EntityNameResolver.java b/hibernate-core/src/main/java/org/hibernate/EntityNameResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/EntityNameResolver.java
rename to hibernate-core/src/main/java/org/hibernate/EntityNameResolver.java
diff --git a/core/src/main/java/org/hibernate/FetchMode.java b/hibernate-core/src/main/java/org/hibernate/FetchMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/FetchMode.java
rename to hibernate-core/src/main/java/org/hibernate/FetchMode.java
diff --git a/core/src/main/java/org/hibernate/Filter.java b/hibernate-core/src/main/java/org/hibernate/Filter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Filter.java
rename to hibernate-core/src/main/java/org/hibernate/Filter.java
diff --git a/core/src/main/java/org/hibernate/FlushMode.java b/hibernate-core/src/main/java/org/hibernate/FlushMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/FlushMode.java
rename to hibernate-core/src/main/java/org/hibernate/FlushMode.java
diff --git a/core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Hibernate.java
rename to hibernate-core/src/main/java/org/hibernate/Hibernate.java
diff --git a/core/src/main/java/org/hibernate/HibernateException.java b/hibernate-core/src/main/java/org/hibernate/HibernateException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/HibernateException.java
rename to hibernate-core/src/main/java/org/hibernate/HibernateException.java
diff --git a/core/src/main/java/org/hibernate/InstantiationException.java b/hibernate-core/src/main/java/org/hibernate/InstantiationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/InstantiationException.java
rename to hibernate-core/src/main/java/org/hibernate/InstantiationException.java
diff --git a/core/src/main/java/org/hibernate/Interceptor.java b/hibernate-core/src/main/java/org/hibernate/Interceptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Interceptor.java
rename to hibernate-core/src/main/java/org/hibernate/Interceptor.java
diff --git a/core/src/main/java/org/hibernate/InvalidMappingException.java b/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/InvalidMappingException.java
rename to hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
diff --git a/core/src/main/java/org/hibernate/JDBCException.java b/hibernate-core/src/main/java/org/hibernate/JDBCException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/JDBCException.java
rename to hibernate-core/src/main/java/org/hibernate/JDBCException.java
diff --git a/core/src/main/java/org/hibernate/LazyInitializationException.java b/hibernate-core/src/main/java/org/hibernate/LazyInitializationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/LazyInitializationException.java
rename to hibernate-core/src/main/java/org/hibernate/LazyInitializationException.java
diff --git a/core/src/main/java/org/hibernate/LobHelper.java b/hibernate-core/src/main/java/org/hibernate/LobHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/LobHelper.java
rename to hibernate-core/src/main/java/org/hibernate/LobHelper.java
diff --git a/core/src/main/java/org/hibernate/LockMode.java b/hibernate-core/src/main/java/org/hibernate/LockMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/LockMode.java
rename to hibernate-core/src/main/java/org/hibernate/LockMode.java
diff --git a/core/src/main/java/org/hibernate/LockOptions.java b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/LockOptions.java
rename to hibernate-core/src/main/java/org/hibernate/LockOptions.java
diff --git a/core/src/main/java/org/hibernate/MappingException.java b/hibernate-core/src/main/java/org/hibernate/MappingException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/MappingException.java
rename to hibernate-core/src/main/java/org/hibernate/MappingException.java
diff --git a/core/src/main/java/org/hibernate/MappingNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/MappingNotFoundException.java
rename to hibernate-core/src/main/java/org/hibernate/MappingNotFoundException.java
diff --git a/core/src/main/java/org/hibernate/NonUniqueObjectException.java b/hibernate-core/src/main/java/org/hibernate/NonUniqueObjectException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/NonUniqueObjectException.java
rename to hibernate-core/src/main/java/org/hibernate/NonUniqueObjectException.java
diff --git a/core/src/main/java/org/hibernate/NonUniqueResultException.java b/hibernate-core/src/main/java/org/hibernate/NonUniqueResultException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/NonUniqueResultException.java
rename to hibernate-core/src/main/java/org/hibernate/NonUniqueResultException.java
diff --git a/core/src/main/java/org/hibernate/ObjectDeletedException.java b/hibernate-core/src/main/java/org/hibernate/ObjectDeletedException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ObjectDeletedException.java
rename to hibernate-core/src/main/java/org/hibernate/ObjectDeletedException.java
diff --git a/core/src/main/java/org/hibernate/ObjectNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ObjectNotFoundException.java
rename to hibernate-core/src/main/java/org/hibernate/ObjectNotFoundException.java
diff --git a/core/src/main/java/org/hibernate/OptimisticLockException.java b/hibernate-core/src/main/java/org/hibernate/OptimisticLockException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/OptimisticLockException.java
rename to hibernate-core/src/main/java/org/hibernate/OptimisticLockException.java
diff --git a/core/src/main/java/org/hibernate/PersistentObjectException.java b/hibernate-core/src/main/java/org/hibernate/PersistentObjectException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/PersistentObjectException.java
rename to hibernate-core/src/main/java/org/hibernate/PersistentObjectException.java
diff --git a/core/src/main/java/org/hibernate/PessimisticLockException.java b/hibernate-core/src/main/java/org/hibernate/PessimisticLockException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/PessimisticLockException.java
rename to hibernate-core/src/main/java/org/hibernate/PessimisticLockException.java
diff --git a/core/src/main/java/org/hibernate/PropertyAccessException.java b/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/PropertyAccessException.java
rename to hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
diff --git a/core/src/main/java/org/hibernate/PropertyNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/PropertyNotFoundException.java
rename to hibernate-core/src/main/java/org/hibernate/PropertyNotFoundException.java
diff --git a/core/src/main/java/org/hibernate/PropertyValueException.java b/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/PropertyValueException.java
rename to hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
diff --git a/core/src/main/java/org/hibernate/Query.java b/hibernate-core/src/main/java/org/hibernate/Query.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Query.java
rename to hibernate-core/src/main/java/org/hibernate/Query.java
diff --git a/core/src/main/java/org/hibernate/QueryException.java b/hibernate-core/src/main/java/org/hibernate/QueryException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/QueryException.java
rename to hibernate-core/src/main/java/org/hibernate/QueryException.java
diff --git a/core/src/main/java/org/hibernate/QueryParameterException.java b/hibernate-core/src/main/java/org/hibernate/QueryParameterException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/QueryParameterException.java
rename to hibernate-core/src/main/java/org/hibernate/QueryParameterException.java
diff --git a/core/src/main/java/org/hibernate/QueryTimeoutException.java b/hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/QueryTimeoutException.java
rename to hibernate-core/src/main/java/org/hibernate/QueryTimeoutException.java
diff --git a/core/src/main/java/org/hibernate/ReplicationMode.java b/hibernate-core/src/main/java/org/hibernate/ReplicationMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ReplicationMode.java
rename to hibernate-core/src/main/java/org/hibernate/ReplicationMode.java
diff --git a/core/src/main/java/org/hibernate/SQLQuery.java b/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/SQLQuery.java
rename to hibernate-core/src/main/java/org/hibernate/SQLQuery.java
diff --git a/core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java b/hibernate-core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/SQLQueryResultMappingBuilder.java
diff --git a/core/src/main/java/org/hibernate/ScrollMode.java b/hibernate-core/src/main/java/org/hibernate/ScrollMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ScrollMode.java
rename to hibernate-core/src/main/java/org/hibernate/ScrollMode.java
diff --git a/core/src/main/java/org/hibernate/ScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/ScrollableResults.java
similarity index 100%
rename from core/src/main/java/org/hibernate/ScrollableResults.java
rename to hibernate-core/src/main/java/org/hibernate/ScrollableResults.java
diff --git a/core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Session.java
rename to hibernate-core/src/main/java/org/hibernate/Session.java
diff --git a/core/src/main/java/org/hibernate/SessionException.java b/hibernate-core/src/main/java/org/hibernate/SessionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/SessionException.java
rename to hibernate-core/src/main/java/org/hibernate/SessionException.java
diff --git a/core/src/main/java/org/hibernate/SessionFactory.java b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/SessionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/SessionFactory.java
diff --git a/core/src/main/java/org/hibernate/SessionFactoryObserver.java b/hibernate-core/src/main/java/org/hibernate/SessionFactoryObserver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/SessionFactoryObserver.java
rename to hibernate-core/src/main/java/org/hibernate/SessionFactoryObserver.java
diff --git a/core/src/main/java/org/hibernate/StaleObjectStateException.java b/hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/StaleObjectStateException.java
rename to hibernate-core/src/main/java/org/hibernate/StaleObjectStateException.java
diff --git a/core/src/main/java/org/hibernate/StaleStateException.java b/hibernate-core/src/main/java/org/hibernate/StaleStateException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/StaleStateException.java
rename to hibernate-core/src/main/java/org/hibernate/StaleStateException.java
diff --git a/core/src/main/java/org/hibernate/StatelessSession.java b/hibernate-core/src/main/java/org/hibernate/StatelessSession.java
similarity index 100%
rename from core/src/main/java/org/hibernate/StatelessSession.java
rename to hibernate-core/src/main/java/org/hibernate/StatelessSession.java
diff --git a/core/src/main/java/org/hibernate/Transaction.java b/hibernate-core/src/main/java/org/hibernate/Transaction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Transaction.java
rename to hibernate-core/src/main/java/org/hibernate/Transaction.java
diff --git a/core/src/main/java/org/hibernate/TransactionException.java b/hibernate-core/src/main/java/org/hibernate/TransactionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/TransactionException.java
rename to hibernate-core/src/main/java/org/hibernate/TransactionException.java
diff --git a/core/src/main/java/org/hibernate/TransientObjectException.java b/hibernate-core/src/main/java/org/hibernate/TransientObjectException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/TransientObjectException.java
rename to hibernate-core/src/main/java/org/hibernate/TransientObjectException.java
diff --git a/core/src/main/java/org/hibernate/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/TypeHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/TypeHelper.java
rename to hibernate-core/src/main/java/org/hibernate/TypeHelper.java
diff --git a/core/src/main/java/org/hibernate/TypeMismatchException.java b/hibernate-core/src/main/java/org/hibernate/TypeMismatchException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/TypeMismatchException.java
rename to hibernate-core/src/main/java/org/hibernate/TypeMismatchException.java
diff --git a/core/src/main/java/org/hibernate/UnknownProfileException.java b/hibernate-core/src/main/java/org/hibernate/UnknownProfileException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/UnknownProfileException.java
rename to hibernate-core/src/main/java/org/hibernate/UnknownProfileException.java
diff --git a/core/src/main/java/org/hibernate/UnresolvableObjectException.java b/hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/UnresolvableObjectException.java
rename to hibernate-core/src/main/java/org/hibernate/UnresolvableObjectException.java
diff --git a/core/src/main/java/org/hibernate/Version.java b/hibernate-core/src/main/java/org/hibernate/Version.java
similarity index 100%
rename from core/src/main/java/org/hibernate/Version.java
rename to hibernate-core/src/main/java/org/hibernate/Version.java
diff --git a/core/src/main/java/org/hibernate/WrongClassException.java b/hibernate-core/src/main/java/org/hibernate/WrongClassException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/WrongClassException.java
rename to hibernate-core/src/main/java/org/hibernate/WrongClassException.java
diff --git a/core/src/main/java/org/hibernate/action/AfterTransactionCompletionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/AfterTransactionCompletionProcess.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/AfterTransactionCompletionProcess.java
rename to hibernate-core/src/main/java/org/hibernate/action/AfterTransactionCompletionProcess.java
diff --git a/core/src/main/java/org/hibernate/action/BeforeTransactionCompletionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/BeforeTransactionCompletionProcess.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/BeforeTransactionCompletionProcess.java
rename to hibernate-core/src/main/java/org/hibernate/action/BeforeTransactionCompletionProcess.java
diff --git a/core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java b/hibernate-core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
diff --git a/core/src/main/java/org/hibernate/action/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/CollectionAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/CollectionAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/CollectionAction.java
diff --git a/core/src/main/java/org/hibernate/action/CollectionRecreateAction.java b/hibernate-core/src/main/java/org/hibernate/action/CollectionRecreateAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/CollectionRecreateAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/CollectionRecreateAction.java
diff --git a/core/src/main/java/org/hibernate/action/CollectionRemoveAction.java b/hibernate-core/src/main/java/org/hibernate/action/CollectionRemoveAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/CollectionRemoveAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/CollectionRemoveAction.java
diff --git a/core/src/main/java/org/hibernate/action/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/CollectionUpdateAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/CollectionUpdateAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/CollectionUpdateAction.java
diff --git a/core/src/main/java/org/hibernate/action/DelayedPostInsertIdentifier.java b/hibernate-core/src/main/java/org/hibernate/action/DelayedPostInsertIdentifier.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/DelayedPostInsertIdentifier.java
rename to hibernate-core/src/main/java/org/hibernate/action/DelayedPostInsertIdentifier.java
diff --git a/core/src/main/java/org/hibernate/action/EntityAction.java b/hibernate-core/src/main/java/org/hibernate/action/EntityAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityAction.java
diff --git a/core/src/main/java/org/hibernate/action/EntityDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/action/EntityDeleteAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityDeleteAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityDeleteAction.java
diff --git a/core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java
diff --git a/core/src/main/java/org/hibernate/action/EntityIncrementVersionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/EntityIncrementVersionProcess.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityIncrementVersionProcess.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityIncrementVersionProcess.java
diff --git a/core/src/main/java/org/hibernate/action/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/EntityInsertAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityInsertAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityInsertAction.java
diff --git a/core/src/main/java/org/hibernate/action/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/EntityUpdateAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityUpdateAction.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityUpdateAction.java
diff --git a/core/src/main/java/org/hibernate/action/EntityVerifyVersionProcess.java b/hibernate-core/src/main/java/org/hibernate/action/EntityVerifyVersionProcess.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/EntityVerifyVersionProcess.java
rename to hibernate-core/src/main/java/org/hibernate/action/EntityVerifyVersionProcess.java
diff --git a/core/src/main/java/org/hibernate/action/Executable.java b/hibernate-core/src/main/java/org/hibernate/action/Executable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/action/Executable.java
rename to hibernate-core/src/main/java/org/hibernate/action/Executable.java
diff --git a/core/src/main/java/org/hibernate/action/package.html b/hibernate-core/src/main/java/org/hibernate/action/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/action/package.html
rename to hibernate-core/src/main/java/org/hibernate/action/package.html
diff --git a/core/src/main/java/org/hibernate/annotations/AccessType.java b/hibernate-core/src/main/java/org/hibernate/annotations/AccessType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/AccessType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/AccessType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Any.java b/hibernate-core/src/main/java/org/hibernate/annotations/Any.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Any.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Any.java
diff --git a/core/src/main/java/org/hibernate/annotations/AnyMetaDef.java b/hibernate-core/src/main/java/org/hibernate/annotations/AnyMetaDef.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/AnyMetaDef.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/AnyMetaDef.java
diff --git a/core/src/main/java/org/hibernate/annotations/AnyMetaDefs.java b/hibernate-core/src/main/java/org/hibernate/annotations/AnyMetaDefs.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/AnyMetaDefs.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/AnyMetaDefs.java
diff --git a/core/src/main/java/org/hibernate/annotations/BatchSize.java b/hibernate-core/src/main/java/org/hibernate/annotations/BatchSize.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/BatchSize.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/BatchSize.java
diff --git a/core/src/main/java/org/hibernate/annotations/Cache.java b/hibernate-core/src/main/java/org/hibernate/annotations/Cache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Cache.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Cache.java
diff --git a/core/src/main/java/org/hibernate/annotations/CacheConcurrencyStrategy.java b/hibernate-core/src/main/java/org/hibernate/annotations/CacheConcurrencyStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/CacheConcurrencyStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/CacheConcurrencyStrategy.java
diff --git a/core/src/main/java/org/hibernate/annotations/CacheModeType.java b/hibernate-core/src/main/java/org/hibernate/annotations/CacheModeType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/CacheModeType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/CacheModeType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Cascade.java b/hibernate-core/src/main/java/org/hibernate/annotations/Cascade.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Cascade.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Cascade.java
diff --git a/core/src/main/java/org/hibernate/annotations/CascadeType.java b/hibernate-core/src/main/java/org/hibernate/annotations/CascadeType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/CascadeType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/CascadeType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Check.java b/hibernate-core/src/main/java/org/hibernate/annotations/Check.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Check.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Check.java
diff --git a/core/src/main/java/org/hibernate/annotations/CollectionId.java b/hibernate-core/src/main/java/org/hibernate/annotations/CollectionId.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/CollectionId.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/CollectionId.java
diff --git a/core/src/main/java/org/hibernate/annotations/CollectionOfElements.java b/hibernate-core/src/main/java/org/hibernate/annotations/CollectionOfElements.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/CollectionOfElements.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/CollectionOfElements.java
diff --git a/core/src/main/java/org/hibernate/annotations/ColumnTransformer.java b/hibernate-core/src/main/java/org/hibernate/annotations/ColumnTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ColumnTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ColumnTransformer.java
diff --git a/core/src/main/java/org/hibernate/annotations/ColumnTransformers.java b/hibernate-core/src/main/java/org/hibernate/annotations/ColumnTransformers.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ColumnTransformers.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ColumnTransformers.java
diff --git a/core/src/main/java/org/hibernate/annotations/Columns.java b/hibernate-core/src/main/java/org/hibernate/annotations/Columns.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Columns.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Columns.java
diff --git a/core/src/main/java/org/hibernate/annotations/DiscriminatorFormula.java b/hibernate-core/src/main/java/org/hibernate/annotations/DiscriminatorFormula.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/DiscriminatorFormula.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/DiscriminatorFormula.java
diff --git a/core/src/main/java/org/hibernate/annotations/DiscriminatorOptions.java b/hibernate-core/src/main/java/org/hibernate/annotations/DiscriminatorOptions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/DiscriminatorOptions.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/DiscriminatorOptions.java
diff --git a/core/src/main/java/org/hibernate/annotations/Entity.java b/hibernate-core/src/main/java/org/hibernate/annotations/Entity.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Entity.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Entity.java
diff --git a/core/src/main/java/org/hibernate/annotations/Fetch.java b/hibernate-core/src/main/java/org/hibernate/annotations/Fetch.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Fetch.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Fetch.java
diff --git a/core/src/main/java/org/hibernate/annotations/FetchMode.java b/hibernate-core/src/main/java/org/hibernate/annotations/FetchMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FetchMode.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FetchMode.java
diff --git a/core/src/main/java/org/hibernate/annotations/FetchProfile.java b/hibernate-core/src/main/java/org/hibernate/annotations/FetchProfile.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FetchProfile.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FetchProfile.java
diff --git a/core/src/main/java/org/hibernate/annotations/FetchProfiles.java b/hibernate-core/src/main/java/org/hibernate/annotations/FetchProfiles.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FetchProfiles.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FetchProfiles.java
diff --git a/core/src/main/java/org/hibernate/annotations/Filter.java b/hibernate-core/src/main/java/org/hibernate/annotations/Filter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Filter.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Filter.java
diff --git a/core/src/main/java/org/hibernate/annotations/FilterDef.java b/hibernate-core/src/main/java/org/hibernate/annotations/FilterDef.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FilterDef.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FilterDef.java
diff --git a/core/src/main/java/org/hibernate/annotations/FilterDefs.java b/hibernate-core/src/main/java/org/hibernate/annotations/FilterDefs.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FilterDefs.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FilterDefs.java
diff --git a/core/src/main/java/org/hibernate/annotations/FilterJoinTable.java b/hibernate-core/src/main/java/org/hibernate/annotations/FilterJoinTable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FilterJoinTable.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FilterJoinTable.java
diff --git a/core/src/main/java/org/hibernate/annotations/FilterJoinTables.java b/hibernate-core/src/main/java/org/hibernate/annotations/FilterJoinTables.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FilterJoinTables.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FilterJoinTables.java
diff --git a/core/src/main/java/org/hibernate/annotations/Filters.java b/hibernate-core/src/main/java/org/hibernate/annotations/Filters.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Filters.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Filters.java
diff --git a/core/src/main/java/org/hibernate/annotations/FlushModeType.java b/hibernate-core/src/main/java/org/hibernate/annotations/FlushModeType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/FlushModeType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/FlushModeType.java
diff --git a/core/src/main/java/org/hibernate/annotations/ForceDiscriminator.java b/hibernate-core/src/main/java/org/hibernate/annotations/ForceDiscriminator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ForceDiscriminator.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ForceDiscriminator.java
diff --git a/core/src/main/java/org/hibernate/annotations/ForeignKey.java b/hibernate-core/src/main/java/org/hibernate/annotations/ForeignKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ForeignKey.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ForeignKey.java
diff --git a/core/src/main/java/org/hibernate/annotations/Formula.java b/hibernate-core/src/main/java/org/hibernate/annotations/Formula.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Formula.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Formula.java
diff --git a/core/src/main/java/org/hibernate/annotations/Generated.java b/hibernate-core/src/main/java/org/hibernate/annotations/Generated.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Generated.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Generated.java
diff --git a/core/src/main/java/org/hibernate/annotations/GenerationTime.java b/hibernate-core/src/main/java/org/hibernate/annotations/GenerationTime.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/GenerationTime.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/GenerationTime.java
diff --git a/core/src/main/java/org/hibernate/annotations/GenericGenerator.java b/hibernate-core/src/main/java/org/hibernate/annotations/GenericGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/GenericGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/GenericGenerator.java
diff --git a/core/src/main/java/org/hibernate/annotations/GenericGenerators.java b/hibernate-core/src/main/java/org/hibernate/annotations/GenericGenerators.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/GenericGenerators.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/GenericGenerators.java
diff --git a/core/src/main/java/org/hibernate/annotations/Immutable.java b/hibernate-core/src/main/java/org/hibernate/annotations/Immutable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Immutable.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Immutable.java
diff --git a/core/src/main/java/org/hibernate/annotations/Index.java b/hibernate-core/src/main/java/org/hibernate/annotations/Index.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Index.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Index.java
diff --git a/core/src/main/java/org/hibernate/annotations/IndexColumn.java b/hibernate-core/src/main/java/org/hibernate/annotations/IndexColumn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/IndexColumn.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/IndexColumn.java
diff --git a/core/src/main/java/org/hibernate/annotations/JoinColumnOrFormula.java b/hibernate-core/src/main/java/org/hibernate/annotations/JoinColumnOrFormula.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/JoinColumnOrFormula.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/JoinColumnOrFormula.java
diff --git a/core/src/main/java/org/hibernate/annotations/JoinColumnsOrFormulas.java b/hibernate-core/src/main/java/org/hibernate/annotations/JoinColumnsOrFormulas.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/JoinColumnsOrFormulas.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/JoinColumnsOrFormulas.java
diff --git a/core/src/main/java/org/hibernate/annotations/JoinFormula.java b/hibernate-core/src/main/java/org/hibernate/annotations/JoinFormula.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/JoinFormula.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/JoinFormula.java
diff --git a/core/src/main/java/org/hibernate/annotations/LazyCollection.java b/hibernate-core/src/main/java/org/hibernate/annotations/LazyCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/LazyCollection.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/LazyCollection.java
diff --git a/core/src/main/java/org/hibernate/annotations/LazyCollectionOption.java b/hibernate-core/src/main/java/org/hibernate/annotations/LazyCollectionOption.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/LazyCollectionOption.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/LazyCollectionOption.java
diff --git a/core/src/main/java/org/hibernate/annotations/LazyToOne.java b/hibernate-core/src/main/java/org/hibernate/annotations/LazyToOne.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/LazyToOne.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/LazyToOne.java
diff --git a/core/src/main/java/org/hibernate/annotations/LazyToOneOption.java b/hibernate-core/src/main/java/org/hibernate/annotations/LazyToOneOption.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/LazyToOneOption.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/LazyToOneOption.java
diff --git a/core/src/main/java/org/hibernate/annotations/Loader.java b/hibernate-core/src/main/java/org/hibernate/annotations/Loader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Loader.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Loader.java
diff --git a/core/src/main/java/org/hibernate/annotations/ManyToAny.java b/hibernate-core/src/main/java/org/hibernate/annotations/ManyToAny.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ManyToAny.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ManyToAny.java
diff --git a/core/src/main/java/org/hibernate/annotations/MapKey.java b/hibernate-core/src/main/java/org/hibernate/annotations/MapKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/MapKey.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/MapKey.java
diff --git a/core/src/main/java/org/hibernate/annotations/MapKeyManyToMany.java b/hibernate-core/src/main/java/org/hibernate/annotations/MapKeyManyToMany.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/MapKeyManyToMany.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/MapKeyManyToMany.java
diff --git a/core/src/main/java/org/hibernate/annotations/MapKeyType.java b/hibernate-core/src/main/java/org/hibernate/annotations/MapKeyType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/MapKeyType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/MapKeyType.java
diff --git a/core/src/main/java/org/hibernate/annotations/MetaValue.java b/hibernate-core/src/main/java/org/hibernate/annotations/MetaValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/MetaValue.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/MetaValue.java
diff --git a/core/src/main/java/org/hibernate/annotations/NamedNativeQueries.java b/hibernate-core/src/main/java/org/hibernate/annotations/NamedNativeQueries.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NamedNativeQueries.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NamedNativeQueries.java
diff --git a/core/src/main/java/org/hibernate/annotations/NamedNativeQuery.java b/hibernate-core/src/main/java/org/hibernate/annotations/NamedNativeQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NamedNativeQuery.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NamedNativeQuery.java
diff --git a/core/src/main/java/org/hibernate/annotations/NamedQueries.java b/hibernate-core/src/main/java/org/hibernate/annotations/NamedQueries.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NamedQueries.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NamedQueries.java
diff --git a/core/src/main/java/org/hibernate/annotations/NamedQuery.java b/hibernate-core/src/main/java/org/hibernate/annotations/NamedQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NamedQuery.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NamedQuery.java
diff --git a/core/src/main/java/org/hibernate/annotations/NaturalId.java b/hibernate-core/src/main/java/org/hibernate/annotations/NaturalId.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NaturalId.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NaturalId.java
diff --git a/core/src/main/java/org/hibernate/annotations/NotFound.java b/hibernate-core/src/main/java/org/hibernate/annotations/NotFound.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NotFound.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NotFound.java
diff --git a/core/src/main/java/org/hibernate/annotations/NotFoundAction.java b/hibernate-core/src/main/java/org/hibernate/annotations/NotFoundAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/NotFoundAction.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/NotFoundAction.java
diff --git a/core/src/main/java/org/hibernate/annotations/OnDelete.java b/hibernate-core/src/main/java/org/hibernate/annotations/OnDelete.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/OnDelete.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/OnDelete.java
diff --git a/core/src/main/java/org/hibernate/annotations/OnDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/annotations/OnDeleteAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/OnDeleteAction.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/OnDeleteAction.java
diff --git a/core/src/main/java/org/hibernate/annotations/OptimisticLock.java b/hibernate-core/src/main/java/org/hibernate/annotations/OptimisticLock.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/OptimisticLock.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/OptimisticLock.java
diff --git a/core/src/main/java/org/hibernate/annotations/OptimisticLockType.java b/hibernate-core/src/main/java/org/hibernate/annotations/OptimisticLockType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/OptimisticLockType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/OptimisticLockType.java
diff --git a/core/src/main/java/org/hibernate/annotations/OrderBy.java b/hibernate-core/src/main/java/org/hibernate/annotations/OrderBy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/OrderBy.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/OrderBy.java
diff --git a/core/src/main/java/org/hibernate/annotations/ParamDef.java b/hibernate-core/src/main/java/org/hibernate/annotations/ParamDef.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ParamDef.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ParamDef.java
diff --git a/core/src/main/java/org/hibernate/annotations/Parameter.java b/hibernate-core/src/main/java/org/hibernate/annotations/Parameter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Parameter.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Parameter.java
diff --git a/core/src/main/java/org/hibernate/annotations/Parent.java b/hibernate-core/src/main/java/org/hibernate/annotations/Parent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Parent.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Parent.java
diff --git a/core/src/main/java/org/hibernate/annotations/Persister.java b/hibernate-core/src/main/java/org/hibernate/annotations/Persister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Persister.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Persister.java
diff --git a/core/src/main/java/org/hibernate/annotations/PolymorphismType.java b/hibernate-core/src/main/java/org/hibernate/annotations/PolymorphismType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/PolymorphismType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/PolymorphismType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Proxy.java b/hibernate-core/src/main/java/org/hibernate/annotations/Proxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Proxy.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Proxy.java
diff --git a/core/src/main/java/org/hibernate/annotations/ResultCheckStyle.java b/hibernate-core/src/main/java/org/hibernate/annotations/ResultCheckStyle.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/ResultCheckStyle.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/ResultCheckStyle.java
diff --git a/core/src/main/java/org/hibernate/annotations/SQLDelete.java b/hibernate-core/src/main/java/org/hibernate/annotations/SQLDelete.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SQLDelete.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SQLDelete.java
diff --git a/core/src/main/java/org/hibernate/annotations/SQLDeleteAll.java b/hibernate-core/src/main/java/org/hibernate/annotations/SQLDeleteAll.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SQLDeleteAll.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SQLDeleteAll.java
diff --git a/core/src/main/java/org/hibernate/annotations/SQLInsert.java b/hibernate-core/src/main/java/org/hibernate/annotations/SQLInsert.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SQLInsert.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SQLInsert.java
diff --git a/core/src/main/java/org/hibernate/annotations/SQLUpdate.java b/hibernate-core/src/main/java/org/hibernate/annotations/SQLUpdate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SQLUpdate.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SQLUpdate.java
diff --git a/core/src/main/java/org/hibernate/annotations/Sort.java b/hibernate-core/src/main/java/org/hibernate/annotations/Sort.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Sort.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Sort.java
diff --git a/core/src/main/java/org/hibernate/annotations/SortType.java b/hibernate-core/src/main/java/org/hibernate/annotations/SortType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SortType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SortType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Source.java b/hibernate-core/src/main/java/org/hibernate/annotations/Source.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Source.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Source.java
diff --git a/core/src/main/java/org/hibernate/annotations/SourceType.java b/hibernate-core/src/main/java/org/hibernate/annotations/SourceType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/SourceType.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/SourceType.java
diff --git a/core/src/main/java/org/hibernate/annotations/Subselect.java b/hibernate-core/src/main/java/org/hibernate/annotations/Subselect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Subselect.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Subselect.java
diff --git a/core/src/main/java/org/hibernate/annotations/Synchronize.java b/hibernate-core/src/main/java/org/hibernate/annotations/Synchronize.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Synchronize.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Synchronize.java
diff --git a/core/src/main/java/org/hibernate/annotations/Table.java b/hibernate-core/src/main/java/org/hibernate/annotations/Table.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Table.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Table.java
diff --git a/core/src/main/java/org/hibernate/annotations/Tables.java b/hibernate-core/src/main/java/org/hibernate/annotations/Tables.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Tables.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Tables.java
diff --git a/core/src/main/java/org/hibernate/annotations/Target.java b/hibernate-core/src/main/java/org/hibernate/annotations/Target.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Target.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Target.java
diff --git a/core/src/main/java/org/hibernate/annotations/Tuplizer.java b/hibernate-core/src/main/java/org/hibernate/annotations/Tuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Tuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Tuplizer.java
diff --git a/core/src/main/java/org/hibernate/annotations/Tuplizers.java b/hibernate-core/src/main/java/org/hibernate/annotations/Tuplizers.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Tuplizers.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Tuplizers.java
diff --git a/core/src/main/java/org/hibernate/annotations/Type.java b/hibernate-core/src/main/java/org/hibernate/annotations/Type.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Type.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Type.java
diff --git a/core/src/main/java/org/hibernate/annotations/TypeDef.java b/hibernate-core/src/main/java/org/hibernate/annotations/TypeDef.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/TypeDef.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/TypeDef.java
diff --git a/core/src/main/java/org/hibernate/annotations/TypeDefs.java b/hibernate-core/src/main/java/org/hibernate/annotations/TypeDefs.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/TypeDefs.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/TypeDefs.java
diff --git a/core/src/main/java/org/hibernate/annotations/Where.java b/hibernate-core/src/main/java/org/hibernate/annotations/Where.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/Where.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/Where.java
diff --git a/core/src/main/java/org/hibernate/annotations/WhereJoinTable.java b/hibernate-core/src/main/java/org/hibernate/annotations/WhereJoinTable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/annotations/WhereJoinTable.java
rename to hibernate-core/src/main/java/org/hibernate/annotations/WhereJoinTable.java
diff --git a/core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java b/hibernate-core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java
diff --git a/core/src/main/java/org/hibernate/bytecode/ClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/ClassTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/ClassTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/ClassTransformer.java
diff --git a/core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java
diff --git a/core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java
diff --git a/core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/CGLIBInstrumenter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/AccessOptimizerAdapter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/CglibClassTransformer.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/InstantiationOptimizerAdapter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ReflectionOptimizerImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java
diff --git a/core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
diff --git a/core/src/main/java/org/hibernate/bytecode/package.html b/hibernate-core/src/main/java/org/hibernate/bytecode/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/package.html
rename to hibernate-core/src/main/java/org/hibernate/bytecode/package.html
diff --git a/core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java
diff --git a/core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java
diff --git a/core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java
diff --git a/core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java
diff --git a/core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java
rename to hibernate-core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java
diff --git a/core/src/main/java/org/hibernate/cache/Cache.java b/hibernate-core/src/main/java/org/hibernate/cache/Cache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/Cache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/Cache.java
diff --git a/core/src/main/java/org/hibernate/cache/CacheConcurrencyStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheConcurrencyStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CacheConcurrencyStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CacheConcurrencyStrategy.java
diff --git a/core/src/main/java/org/hibernate/cache/CacheDataDescription.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheDataDescription.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CacheDataDescription.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CacheDataDescription.java
diff --git a/core/src/main/java/org/hibernate/cache/CacheException.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CacheException.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CacheException.java
diff --git a/core/src/main/java/org/hibernate/cache/CacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CacheKey.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CacheKey.java
diff --git a/core/src/main/java/org/hibernate/cache/CacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/CacheProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CacheProvider.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CacheProvider.java
diff --git a/core/src/main/java/org/hibernate/cache/CollectionRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/CollectionRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/CollectionRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/CollectionRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/EntityRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/EntityRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/EntityRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/EntityRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/FilterKey.java b/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/FilterKey.java
rename to hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
diff --git a/core/src/main/java/org/hibernate/cache/GeneralDataRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/GeneralDataRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/GeneralDataRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/GeneralDataRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/HashtableCache.java b/hibernate-core/src/main/java/org/hibernate/cache/HashtableCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/HashtableCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/HashtableCache.java
diff --git a/core/src/main/java/org/hibernate/cache/HashtableCacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/HashtableCacheProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/HashtableCacheProvider.java
rename to hibernate-core/src/main/java/org/hibernate/cache/HashtableCacheProvider.java
diff --git a/core/src/main/java/org/hibernate/cache/NoCacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/NoCacheProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/NoCacheProvider.java
rename to hibernate-core/src/main/java/org/hibernate/cache/NoCacheProvider.java
diff --git a/core/src/main/java/org/hibernate/cache/NoCachingEnabledException.java b/hibernate-core/src/main/java/org/hibernate/cache/NoCachingEnabledException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/NoCachingEnabledException.java
rename to hibernate-core/src/main/java/org/hibernate/cache/NoCachingEnabledException.java
diff --git a/core/src/main/java/org/hibernate/cache/NonstrictReadWriteCache.java b/hibernate-core/src/main/java/org/hibernate/cache/NonstrictReadWriteCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/NonstrictReadWriteCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/NonstrictReadWriteCache.java
diff --git a/core/src/main/java/org/hibernate/cache/OptimisticCache.java b/hibernate-core/src/main/java/org/hibernate/cache/OptimisticCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/OptimisticCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/OptimisticCache.java
diff --git a/core/src/main/java/org/hibernate/cache/OptimisticCacheSource.java b/hibernate-core/src/main/java/org/hibernate/cache/OptimisticCacheSource.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/OptimisticCacheSource.java
rename to hibernate-core/src/main/java/org/hibernate/cache/OptimisticCacheSource.java
diff --git a/core/src/main/java/org/hibernate/cache/QueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/QueryCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/QueryCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/QueryCache.java
diff --git a/core/src/main/java/org/hibernate/cache/QueryCacheFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/QueryCacheFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/QueryCacheFactory.java
rename to hibernate-core/src/main/java/org/hibernate/cache/QueryCacheFactory.java
diff --git a/core/src/main/java/org/hibernate/cache/QueryKey.java b/hibernate-core/src/main/java/org/hibernate/cache/QueryKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/QueryKey.java
rename to hibernate-core/src/main/java/org/hibernate/cache/QueryKey.java
diff --git a/core/src/main/java/org/hibernate/cache/QueryResultsRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/QueryResultsRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/QueryResultsRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/QueryResultsRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/ReadOnlyCache.java b/hibernate-core/src/main/java/org/hibernate/cache/ReadOnlyCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/ReadOnlyCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/ReadOnlyCache.java
diff --git a/core/src/main/java/org/hibernate/cache/ReadWriteCache.java b/hibernate-core/src/main/java/org/hibernate/cache/ReadWriteCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/ReadWriteCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/ReadWriteCache.java
diff --git a/core/src/main/java/org/hibernate/cache/Region.java b/hibernate-core/src/main/java/org/hibernate/cache/Region.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/Region.java
rename to hibernate-core/src/main/java/org/hibernate/cache/Region.java
diff --git a/core/src/main/java/org/hibernate/cache/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/RegionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
diff --git a/core/src/main/java/org/hibernate/cache/StandardQueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/StandardQueryCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/StandardQueryCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/StandardQueryCache.java
diff --git a/core/src/main/java/org/hibernate/cache/StandardQueryCacheFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/StandardQueryCacheFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/StandardQueryCacheFactory.java
rename to hibernate-core/src/main/java/org/hibernate/cache/StandardQueryCacheFactory.java
diff --git a/core/src/main/java/org/hibernate/cache/Timestamper.java b/hibernate-core/src/main/java/org/hibernate/cache/Timestamper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/Timestamper.java
rename to hibernate-core/src/main/java/org/hibernate/cache/Timestamper.java
diff --git a/core/src/main/java/org/hibernate/cache/TimestampsRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/TimestampsRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/TimestampsRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/TimestampsRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/TransactionAwareCache.java b/hibernate-core/src/main/java/org/hibernate/cache/TransactionAwareCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/TransactionAwareCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/TransactionAwareCache.java
diff --git a/core/src/main/java/org/hibernate/cache/TransactionalCache.java b/hibernate-core/src/main/java/org/hibernate/cache/TransactionalCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/TransactionalCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/TransactionalCache.java
diff --git a/core/src/main/java/org/hibernate/cache/TransactionalDataRegion.java b/hibernate-core/src/main/java/org/hibernate/cache/TransactionalDataRegion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/TransactionalDataRegion.java
rename to hibernate-core/src/main/java/org/hibernate/cache/TransactionalDataRegion.java
diff --git a/core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java b/hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
rename to hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
diff --git a/core/src/main/java/org/hibernate/cache/access/AccessType.java b/hibernate-core/src/main/java/org/hibernate/cache/access/AccessType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/access/AccessType.java
rename to hibernate-core/src/main/java/org/hibernate/cache/access/AccessType.java
diff --git a/core/src/main/java/org/hibernate/cache/access/CollectionRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/access/CollectionRegionAccessStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/access/CollectionRegionAccessStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cache/access/CollectionRegionAccessStrategy.java
diff --git a/core/src/main/java/org/hibernate/cache/access/EntityRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/access/EntityRegionAccessStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/access/EntityRegionAccessStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cache/access/EntityRegionAccessStrategy.java
diff --git a/core/src/main/java/org/hibernate/cache/access/SoftLock.java b/hibernate-core/src/main/java/org/hibernate/cache/access/SoftLock.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/access/SoftLock.java
rename to hibernate-core/src/main/java/org/hibernate/cache/access/SoftLock.java
diff --git a/core/src/main/java/org/hibernate/cache/access/package.html b/hibernate-core/src/main/java/org/hibernate/cache/access/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/access/package.html
rename to hibernate-core/src/main/java/org/hibernate/cache/access/package.html
diff --git a/core/src/main/java/org/hibernate/cache/entry/CacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/CacheEntryStructure.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntryStructure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/CacheEntryStructure.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntryStructure.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/StructuredCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredCacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/StructuredCacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredCacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/StructuredCollectionCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredCollectionCacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/StructuredCollectionCacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredCollectionCacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/StructuredMapCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredMapCacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/StructuredMapCacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/StructuredMapCacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/UnstructuredCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/UnstructuredCacheEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/UnstructuredCacheEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/UnstructuredCacheEntry.java
diff --git a/core/src/main/java/org/hibernate/cache/entry/package.html b/hibernate-core/src/main/java/org/hibernate/cache/entry/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/entry/package.html
rename to hibernate-core/src/main/java/org/hibernate/cache/entry/package.html
diff --git a/core/src/main/java/org/hibernate/cache/impl/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/CacheDataDescriptionImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/CacheDataDescriptionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/CacheDataDescriptionImpl.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/NoCachingRegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/NoCachingRegionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/NoCachingRegionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/NoCachingRegionFactory.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/BaseGeneralDataRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseGeneralDataRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/BaseGeneralDataRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseGeneralDataRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/BaseRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/BaseRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/BaseTransactionalDataRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseTransactionalDataRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/BaseTransactionalDataRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/BaseTransactionalDataRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/CollectionAccessStrategyAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/CollectionAccessStrategyAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/CollectionAccessStrategyAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/CollectionAccessStrategyAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/CollectionRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/CollectionRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/CollectionRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/CollectionRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/EntityAccessStrategyAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/EntityAccessStrategyAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/EntityAccessStrategyAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/EntityAccessStrategyAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/EntityRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/EntityRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/EntityRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/EntityRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/OptimisticCacheSourceAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/OptimisticCacheSourceAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/OptimisticCacheSourceAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/OptimisticCacheSourceAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/QueryResultsRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/QueryResultsRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/QueryResultsRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/QueryResultsRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java
diff --git a/core/src/main/java/org/hibernate/cache/impl/bridge/TimestampsRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/TimestampsRegionAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/impl/bridge/TimestampsRegionAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/TimestampsRegionAdapter.java
diff --git a/core/src/main/java/org/hibernate/cache/package.html b/hibernate-core/src/main/java/org/hibernate/cache/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/cache/package.html
rename to hibernate-core/src/main/java/org/hibernate/cache/package.html
diff --git a/core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/AccessType.java b/hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/AccessType.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/AccessType.java
diff --git a/core/src/main/java/org/hibernate/cfg/AnnotatedClassType.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotatedClassType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/AnnotatedClassType.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/AnnotatedClassType.java
diff --git a/core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
diff --git a/core/src/main/java/org/hibernate/cfg/BinderHelper.java b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/BinderHelper.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
diff --git a/core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ClassPropertyHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java
diff --git a/core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ComponentPropertyHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Configuration.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
diff --git a/core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/CreateKeySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CreateKeySecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/CreateKeySecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/CreateKeySecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
diff --git a/core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
diff --git a/core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java b/hibernate-core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java
diff --git a/core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java
diff --git a/core/src/main/java/org/hibernate/cfg/Ejb3Column.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Ejb3Column.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
diff --git a/core/src/main/java/org/hibernate/cfg/Ejb3DiscriminatorColumn.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3DiscriminatorColumn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Ejb3DiscriminatorColumn.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Ejb3DiscriminatorColumn.java
diff --git a/core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
diff --git a/core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Environment.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
diff --git a/core/src/main/java/org/hibernate/cfg/ExtendedMappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/ExtendedMappings.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ExtendedMappings.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ExtendedMappings.java
diff --git a/core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java b/hibernate-core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java
diff --git a/core/src/main/java/org/hibernate/cfg/FkSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/FkSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/FkSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/FkSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/HbmBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
diff --git a/core/src/main/java/org/hibernate/cfg/IndexColumn.java b/hibernate-core/src/main/java/org/hibernate/cfg/IndexColumn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/IndexColumn.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/IndexColumn.java
diff --git a/core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/IndexOrUniqueKeySecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/InheritanceState.java b/hibernate-core/src/main/java/org/hibernate/cfg/InheritanceState.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/InheritanceState.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/InheritanceState.java
diff --git a/core/src/main/java/org/hibernate/cfg/JoinedSubclassFkSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/JoinedSubclassFkSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/JoinedSubclassFkSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/JoinedSubclassFkSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/Mappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Mappings.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
diff --git a/core/src/main/java/org/hibernate/cfg/MetadataSourceType.java b/hibernate-core/src/main/java/org/hibernate/cfg/MetadataSourceType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/MetadataSourceType.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/MetadataSourceType.java
diff --git a/core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/NamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/NamingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
diff --git a/core/src/main/java/org/hibernate/cfg/NotYetImplementedException.java b/hibernate-core/src/main/java/org/hibernate/cfg/NotYetImplementedException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/NotYetImplementedException.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/NotYetImplementedException.java
diff --git a/core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
diff --git a/core/src/main/java/org/hibernate/cfg/ObjectNameSource.java b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameSource.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ObjectNameSource.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameSource.java
diff --git a/core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/PkDrivenByDefaultMapsIdSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/PkDrivenByDefaultMapsIdSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PkDrivenByDefaultMapsIdSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PkDrivenByDefaultMapsIdSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyContainer.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyContainer.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyData.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyData.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyData.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyData.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyHolderBuilder.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolderBuilder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyHolderBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolderBuilder.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyInferredData.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyInferredData.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyInferredData.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyInferredData.java
diff --git a/core/src/main/java/org/hibernate/cfg/PropertyPreloadedData.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyPreloadedData.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/PropertyPreloadedData.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/PropertyPreloadedData.java
diff --git a/core/src/main/java/org/hibernate/cfg/QuerySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/QuerySecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/QuerySecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/QuerySecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/RecoverableException.java b/hibernate-core/src/main/java/org/hibernate/cfg/RecoverableException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/RecoverableException.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/RecoverableException.java
diff --git a/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/ResultSetMappingSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ResultSetMappingSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/SecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/SecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/SecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/SecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/SecondaryTableSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/SecondaryTableSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/SecondaryTableSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/SecondaryTableSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/SetSimpleValueTypeSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/SetSimpleValueTypeSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/SetSimpleValueTypeSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/SetSimpleValueTypeSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/Settings.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
diff --git a/core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/SettingsFactory.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
diff --git a/core/src/main/java/org/hibernate/cfg/ToOneBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ToOneBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ToOneBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/UniqueConstraintHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/UniqueConstraintHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/UniqueConstraintHolder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/UniqueConstraintHolder.java
diff --git a/core/src/main/java/org/hibernate/cfg/VerifyFetchProfileReferenceSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/VerifyFetchProfileReferenceSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/VerifyFetchProfileReferenceSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/VerifyFetchProfileReferenceSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/WrappedInferredData.java b/hibernate-core/src/main/java/org/hibernate/cfg/WrappedInferredData.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/WrappedInferredData.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/WrappedInferredData.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/ArrayBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ArrayBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/ArrayBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/ArrayBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/BagBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/BagBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/BagBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/BagBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/CustomizableColumns.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CustomizableColumns.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/CustomizableColumns.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/CustomizableColumns.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/MapKeyColumnDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapKeyColumnDelegator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/MapKeyColumnDelegator.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapKeyColumnDelegator.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/MapKeyJoinColumnDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapKeyJoinColumnDelegator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/MapKeyJoinColumnDelegator.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapKeyJoinColumnDelegator.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/Nullability.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/Nullability.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/Nullability.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/Nullability.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/PrimitiveArrayBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PrimitiveArrayBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/PrimitiveArrayBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/PrimitiveArrayBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/Version.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/Version.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/Version.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/Version.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java
diff --git a/core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java
diff --git a/core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
diff --git a/core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationEventListener.java
diff --git a/core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java
diff --git a/core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/HibernateTraversableResolver.java
diff --git a/core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
diff --git a/core/src/main/java/org/hibernate/cfg/package.html b/hibernate-core/src/main/java/org/hibernate/cfg/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/package.html
rename to hibernate-core/src/main/java/org/hibernate/cfg/package.html
diff --git a/core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
diff --git a/core/src/main/java/org/hibernate/classic/Lifecycle.java b/hibernate-core/src/main/java/org/hibernate/classic/Lifecycle.java
similarity index 100%
rename from core/src/main/java/org/hibernate/classic/Lifecycle.java
rename to hibernate-core/src/main/java/org/hibernate/classic/Lifecycle.java
diff --git a/core/src/main/java/org/hibernate/classic/Session.java b/hibernate-core/src/main/java/org/hibernate/classic/Session.java
similarity index 100%
rename from core/src/main/java/org/hibernate/classic/Session.java
rename to hibernate-core/src/main/java/org/hibernate/classic/Session.java
diff --git a/core/src/main/java/org/hibernate/classic/Validatable.java b/hibernate-core/src/main/java/org/hibernate/classic/Validatable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/classic/Validatable.java
rename to hibernate-core/src/main/java/org/hibernate/classic/Validatable.java
diff --git a/core/src/main/java/org/hibernate/classic/ValidationFailure.java b/hibernate-core/src/main/java/org/hibernate/classic/ValidationFailure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/classic/ValidationFailure.java
rename to hibernate-core/src/main/java/org/hibernate/classic/ValidationFailure.java
diff --git a/core/src/main/java/org/hibernate/classic/package.html b/hibernate-core/src/main/java/org/hibernate/classic/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/classic/package.html
rename to hibernate-core/src/main/java/org/hibernate/classic/package.html
diff --git a/core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
rename to hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentBag.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentBag.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentBag.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentBag.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentCollection.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentCollection.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentList.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentList.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentList.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentList.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentMap.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentMap.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentMap.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentMap.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentSet.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentSet.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentSet.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentSet.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentSortedMap.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedMap.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentSortedMap.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedMap.java
diff --git a/core/src/main/java/org/hibernate/collection/PersistentSortedSet.java b/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedSet.java
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/PersistentSortedSet.java
rename to hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedSet.java
diff --git a/core/src/main/java/org/hibernate/collection/package.html b/hibernate-core/src/main/java/org/hibernate/collection/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/collection/package.html
rename to hibernate-core/src/main/java/org/hibernate/collection/package.html
diff --git a/core/src/main/java/org/hibernate/connection/ConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/connection/ConnectionProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/ConnectionProvider.java
rename to hibernate-core/src/main/java/org/hibernate/connection/ConnectionProvider.java
diff --git a/core/src/main/java/org/hibernate/connection/ConnectionProviderFactory.java b/hibernate-core/src/main/java/org/hibernate/connection/ConnectionProviderFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/ConnectionProviderFactory.java
rename to hibernate-core/src/main/java/org/hibernate/connection/ConnectionProviderFactory.java
diff --git a/core/src/main/java/org/hibernate/connection/DatasourceConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/connection/DatasourceConnectionProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/DatasourceConnectionProvider.java
rename to hibernate-core/src/main/java/org/hibernate/connection/DatasourceConnectionProvider.java
diff --git a/core/src/main/java/org/hibernate/connection/DriverManagerConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/connection/DriverManagerConnectionProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/DriverManagerConnectionProvider.java
rename to hibernate-core/src/main/java/org/hibernate/connection/DriverManagerConnectionProvider.java
diff --git a/core/src/main/java/org/hibernate/connection/UserSuppliedConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/connection/UserSuppliedConnectionProvider.java
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/UserSuppliedConnectionProvider.java
rename to hibernate-core/src/main/java/org/hibernate/connection/UserSuppliedConnectionProvider.java
diff --git a/core/src/main/java/org/hibernate/connection/package.html b/hibernate-core/src/main/java/org/hibernate/connection/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/connection/package.html
rename to hibernate-core/src/main/java/org/hibernate/connection/package.html
diff --git a/core/src/main/java/org/hibernate/context/CurrentSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/CurrentSessionContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/context/CurrentSessionContext.java
rename to hibernate-core/src/main/java/org/hibernate/context/CurrentSessionContext.java
diff --git a/core/src/main/java/org/hibernate/context/JTASessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/JTASessionContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/context/JTASessionContext.java
rename to hibernate-core/src/main/java/org/hibernate/context/JTASessionContext.java
diff --git a/core/src/main/java/org/hibernate/context/ManagedSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/ManagedSessionContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/context/ManagedSessionContext.java
rename to hibernate-core/src/main/java/org/hibernate/context/ManagedSessionContext.java
diff --git a/core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
rename to hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
diff --git a/core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/AggregateProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/AggregateProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/AliasedProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/AliasedProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/AvgProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/AvgProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/BetweenExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/BetweenExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Conjunction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Conjunction.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java
diff --git a/core/src/main/java/org/hibernate/criterion/CountProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/CountProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/CriteriaQuery.java b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
diff --git a/core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
diff --git a/core/src/main/java/org/hibernate/criterion/Criterion.java b/hibernate-core/src/main/java/org/hibernate/criterion/Criterion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Criterion.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Criterion.java
diff --git a/core/src/main/java/org/hibernate/criterion/DetachedCriteria.java b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
diff --git a/core/src/main/java/org/hibernate/criterion/Disjunction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Disjunction.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java
diff --git a/core/src/main/java/org/hibernate/criterion/Distinct.java b/hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Distinct.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java
diff --git a/core/src/main/java/org/hibernate/criterion/EmptyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/EmptyExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/EnhancedProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/Example.java b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Example.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Example.java
diff --git a/core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Expression.java b/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Expression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
diff --git a/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/IdentifierProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/IlikeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/IlikeExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/InExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/InExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Junction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Junction.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
diff --git a/core/src/main/java/org/hibernate/criterion/LikeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/LikeExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/LogicalExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/LogicalExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/MatchMode.java b/hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/MatchMode.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java
diff --git a/core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
diff --git a/core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/NotExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/NotExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/NotNullExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/NotNullExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/NullExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/NullExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Order.java b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Order.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Order.java
diff --git a/core/src/main/java/org/hibernate/criterion/Projection.java b/hibernate-core/src/main/java/org/hibernate/criterion/Projection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Projection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Projection.java
diff --git a/core/src/main/java/org/hibernate/criterion/ProjectionList.java b/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/ProjectionList.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
diff --git a/core/src/main/java/org/hibernate/criterion/Projections.java b/hibernate-core/src/main/java/org/hibernate/criterion/Projections.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Projections.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Projections.java
diff --git a/core/src/main/java/org/hibernate/criterion/Property.java b/hibernate-core/src/main/java/org/hibernate/criterion/Property.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Property.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Property.java
diff --git a/core/src/main/java/org/hibernate/criterion/PropertyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/PropertyExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/PropertyProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/PropertyProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Restrictions.java b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Restrictions.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
diff --git a/core/src/main/java/org/hibernate/criterion/RowCountProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/RowCountProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/SQLCriterion.java b/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SQLCriterion.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
diff --git a/core/src/main/java/org/hibernate/criterion/SQLProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SQLProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/SimpleExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SimpleExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/SimpleProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SimpleProjection.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java
diff --git a/core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/SizeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SizeExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/Subqueries.java b/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/Subqueries.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
diff --git a/core/src/main/java/org/hibernate/criterion/SubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
rename to hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
diff --git a/core/src/main/java/org/hibernate/criterion/package.html b/hibernate-core/src/main/java/org/hibernate/criterion/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/criterion/package.html
rename to hibernate-core/src/main/java/org/hibernate/criterion/package.html
diff --git a/core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/DB2390Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/DB2400Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/DB2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/DB2Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/DataDirectOracle9Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/DerbyDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/DerbyDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/FirebirdDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/FrontBaseDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/H2Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/HSQLDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/InformixDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/IngresDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/InterbaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MckoiDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MckoiDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MckoiDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MySQL5Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MySQL5InnoDBDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MySQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MySQLDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MySQLInnoDBDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/MySQLMyISAMDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/OracleDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/OracleDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/OracleDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/PointbaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/PointbaseDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/PostgresPlusDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/ProgressDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/ProgressDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/ProgressDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/ResultColumnReferenceStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/SAPDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2008Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/Sybase11Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/SybaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/SybaseDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/SybaseDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/TeradataDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/TeradataDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
diff --git a/core/src/main/java/org/hibernate/dialect/TypeNames.java b/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/TypeNames.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/AnsiTrimFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/AvgWithArgumentCastFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/CastFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/CastFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/CharIndexFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicAvgFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicCountFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/ClassicSumFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/ConditionalParenthesisFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/ConvertFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/NoArgSQLFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/NvlFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/NvlFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/PositionSubstringFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/SQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionRegistry.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/SQLFunctionTemplate.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/StandardJDBCEscapeFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/StandardSQLFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/TemplateRenderer.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/TrimFunctionTemplate.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/VarArgsSQLFunction.java
diff --git a/core/src/main/java/org/hibernate/dialect/function/package.html b/hibernate-core/src/main/java/org/hibernate/dialect/function/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/function/package.html
rename to hibernate-core/src/main/java/org/hibernate/dialect/function/package.html
diff --git a/core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/LockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticForceIncrementLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/OptimisticLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticForceIncrementLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
diff --git a/core/src/main/java/org/hibernate/dialect/package.html b/hibernate-core/src/main/java/org/hibernate/dialect/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/package.html
rename to hibernate-core/src/main/java/org/hibernate/dialect/package.html
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/AbstractDialectResolver.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicDialectResolver.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectFactory.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolver.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/DialectResolverSet.java
diff --git a/core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java
rename to hibernate-core/src/main/java/org/hibernate/dialect/resolver/StandardDialectResolver.java
diff --git a/core/src/main/java/org/hibernate/engine/ActionQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ActionQueue.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
diff --git a/core/src/main/java/org/hibernate/engine/AssociationKey.java b/hibernate-core/src/main/java/org/hibernate/engine/AssociationKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/AssociationKey.java
rename to hibernate-core/src/main/java/org/hibernate/engine/AssociationKey.java
diff --git a/core/src/main/java/org/hibernate/engine/BatchFetchQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
rename to hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
diff --git a/core/src/main/java/org/hibernate/engine/Cascade.java b/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Cascade.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
diff --git a/core/src/main/java/org/hibernate/engine/CascadeStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/CascadeStyle.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/CascadeStyle.java
rename to hibernate-core/src/main/java/org/hibernate/engine/CascadeStyle.java
diff --git a/core/src/main/java/org/hibernate/engine/CascadingAction.java b/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/CascadingAction.java
rename to hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
diff --git a/core/src/main/java/org/hibernate/engine/CollectionEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/CollectionEntry.java
rename to hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
diff --git a/core/src/main/java/org/hibernate/engine/CollectionKey.java b/hibernate-core/src/main/java/org/hibernate/engine/CollectionKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/CollectionKey.java
rename to hibernate-core/src/main/java/org/hibernate/engine/CollectionKey.java
diff --git a/core/src/main/java/org/hibernate/engine/Collections.java b/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Collections.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Collections.java
diff --git a/core/src/main/java/org/hibernate/engine/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/EntityEntry.java
rename to hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
diff --git a/core/src/main/java/org/hibernate/engine/EntityKey.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/EntityKey.java
rename to hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
diff --git a/core/src/main/java/org/hibernate/engine/EntityUniqueKey.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityUniqueKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/EntityUniqueKey.java
rename to hibernate-core/src/main/java/org/hibernate/engine/EntityUniqueKey.java
diff --git a/core/src/main/java/org/hibernate/engine/ExecuteUpdateResultCheckStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/ExecuteUpdateResultCheckStyle.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ExecuteUpdateResultCheckStyle.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ExecuteUpdateResultCheckStyle.java
diff --git a/core/src/main/java/org/hibernate/engine/FilterDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/FilterDefinition.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/FilterDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/engine/FilterDefinition.java
diff --git a/core/src/main/java/org/hibernate/engine/ForeignKeys.java b/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ForeignKeys.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
diff --git a/core/src/main/java/org/hibernate/engine/HibernateIterator.java b/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/HibernateIterator.java
rename to hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
diff --git a/core/src/main/java/org/hibernate/engine/IdentifierValue.java b/hibernate-core/src/main/java/org/hibernate/engine/IdentifierValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/IdentifierValue.java
rename to hibernate-core/src/main/java/org/hibernate/engine/IdentifierValue.java
diff --git a/core/src/main/java/org/hibernate/engine/JoinHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/JoinHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/JoinHelper.java
rename to hibernate-core/src/main/java/org/hibernate/engine/JoinHelper.java
diff --git a/core/src/main/java/org/hibernate/engine/JoinSequence.java b/hibernate-core/src/main/java/org/hibernate/engine/JoinSequence.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/JoinSequence.java
rename to hibernate-core/src/main/java/org/hibernate/engine/JoinSequence.java
diff --git a/core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java b/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
rename to hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
diff --git a/core/src/main/java/org/hibernate/engine/Mapping.java b/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Mapping.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
diff --git a/core/src/main/java/org/hibernate/engine/NamedQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/NamedQueryDefinition.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/NamedQueryDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/engine/NamedQueryDefinition.java
diff --git a/core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
diff --git a/core/src/main/java/org/hibernate/engine/NonFlushedChanges.java b/hibernate-core/src/main/java/org/hibernate/engine/NonFlushedChanges.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/NonFlushedChanges.java
rename to hibernate-core/src/main/java/org/hibernate/engine/NonFlushedChanges.java
diff --git a/core/src/main/java/org/hibernate/engine/Nullability.java b/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Nullability.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
diff --git a/core/src/main/java/org/hibernate/engine/ParameterBinder.java b/hibernate-core/src/main/java/org/hibernate/engine/ParameterBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ParameterBinder.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ParameterBinder.java
diff --git a/core/src/main/java/org/hibernate/engine/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/PersistenceContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
diff --git a/core/src/main/java/org/hibernate/engine/QueryParameters.java b/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/QueryParameters.java
rename to hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
diff --git a/core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
diff --git a/core/src/main/java/org/hibernate/engine/RowSelection.java b/hibernate-core/src/main/java/org/hibernate/engine/RowSelection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/RowSelection.java
rename to hibernate-core/src/main/java/org/hibernate/engine/RowSelection.java
diff --git a/core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
diff --git a/core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/SessionImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
diff --git a/core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
diff --git a/core/src/main/java/org/hibernate/engine/Status.java b/hibernate-core/src/main/java/org/hibernate/engine/Status.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Status.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Status.java
diff --git a/core/src/main/java/org/hibernate/engine/SubselectFetch.java b/hibernate-core/src/main/java/org/hibernate/engine/SubselectFetch.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/SubselectFetch.java
rename to hibernate-core/src/main/java/org/hibernate/engine/SubselectFetch.java
diff --git a/core/src/main/java/org/hibernate/engine/TransactionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/TransactionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
diff --git a/core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
rename to hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
diff --git a/core/src/main/java/org/hibernate/engine/TypedValue.java b/hibernate-core/src/main/java/org/hibernate/engine/TypedValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/TypedValue.java
rename to hibernate-core/src/main/java/org/hibernate/engine/TypedValue.java
diff --git a/core/src/main/java/org/hibernate/engine/UnsavedValueFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/UnsavedValueFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/UnsavedValueFactory.java
rename to hibernate-core/src/main/java/org/hibernate/engine/UnsavedValueFactory.java
diff --git a/core/src/main/java/org/hibernate/engine/ValueInclusion.java b/hibernate-core/src/main/java/org/hibernate/engine/ValueInclusion.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/ValueInclusion.java
rename to hibernate-core/src/main/java/org/hibernate/engine/ValueInclusion.java
diff --git a/core/src/main/java/org/hibernate/engine/VersionValue.java b/hibernate-core/src/main/java/org/hibernate/engine/VersionValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/VersionValue.java
rename to hibernate-core/src/main/java/org/hibernate/engine/VersionValue.java
diff --git a/core/src/main/java/org/hibernate/engine/Versioning.java b/hibernate-core/src/main/java/org/hibernate/engine/Versioning.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/Versioning.java
rename to hibernate-core/src/main/java/org/hibernate/engine/Versioning.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/BlobImplementer.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobImplementer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/BlobImplementer.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobImplementer.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ClobImplementer.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobImplementer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ClobImplementer.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobImplementer.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ContextualLobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ContextualLobCreator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ContextualLobCreator.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ContextualLobCreator.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/LobCreationContext.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreationContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/LobCreationContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreationContext.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/NClobImplementer.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobImplementer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/NClobImplementer.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobImplementer.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/NonContextualLobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NonContextualLobCreator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/NonContextualLobCreator.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/NonContextualLobCreator.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ReaderInputStream.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ReaderInputStream.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ReaderInputStream.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ReaderInputStream.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/StreamUtils.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/StreamUtils.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/StreamUtils.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/StreamUtils.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/WrappedBlob.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedBlob.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/WrappedBlob.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedBlob.java
diff --git a/core/src/main/java/org/hibernate/engine/jdbc/WrappedClob.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedClob.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/jdbc/WrappedClob.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedClob.java
diff --git a/core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
diff --git a/core/src/main/java/org/hibernate/engine/loading/EntityLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/EntityLoadContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/loading/EntityLoadContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/loading/EntityLoadContext.java
diff --git a/core/src/main/java/org/hibernate/engine/loading/LoadContexts.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
rename to hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
diff --git a/core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
rename to hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
diff --git a/core/src/main/java/org/hibernate/engine/package.html b/hibernate-core/src/main/java/org/hibernate/engine/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/package.html
rename to hibernate-core/src/main/java/org/hibernate/engine/package.html
diff --git a/core/src/main/java/org/hibernate/engine/profile/Association.java b/hibernate-core/src/main/java/org/hibernate/engine/profile/Association.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/profile/Association.java
rename to hibernate-core/src/main/java/org/hibernate/engine/profile/Association.java
diff --git a/core/src/main/java/org/hibernate/engine/profile/Fetch.java b/hibernate-core/src/main/java/org/hibernate/engine/profile/Fetch.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/profile/Fetch.java
rename to hibernate-core/src/main/java/org/hibernate/engine/profile/Fetch.java
diff --git a/core/src/main/java/org/hibernate/engine/profile/FetchProfile.java b/hibernate-core/src/main/java/org/hibernate/engine/profile/FetchProfile.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/profile/FetchProfile.java
rename to hibernate-core/src/main/java/org/hibernate/engine/profile/FetchProfile.java
diff --git a/core/src/main/java/org/hibernate/engine/query/FilterQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/FilterQueryPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/FilterQueryPlan.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/FilterQueryPlan.java
diff --git a/core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
diff --git a/core/src/main/java/org/hibernate/engine/query/NamedParameterDescriptor.java b/hibernate-core/src/main/java/org/hibernate/engine/query/NamedParameterDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/NamedParameterDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/NamedParameterDescriptor.java
diff --git a/core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
diff --git a/core/src/main/java/org/hibernate/engine/query/OrdinalParameterDescriptor.java b/hibernate-core/src/main/java/org/hibernate/engine/query/OrdinalParameterDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/OrdinalParameterDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/OrdinalParameterDescriptor.java
diff --git a/core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java b/hibernate-core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
diff --git a/core/src/main/java/org/hibernate/engine/query/ParameterMetadata.java b/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/ParameterMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/ParameterMetadata.java
diff --git a/core/src/main/java/org/hibernate/engine/query/ParameterParser.java b/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/ParameterParser.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/ParameterParser.java
diff --git a/core/src/main/java/org/hibernate/engine/query/QueryMetadata.java b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/QueryMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/QueryMetadata.java
diff --git a/core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
diff --git a/core/src/main/java/org/hibernate/engine/query/ReturnMetadata.java b/hibernate-core/src/main/java/org/hibernate/engine/query/ReturnMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/ReturnMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/ReturnMetadata.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryCollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryCollectionReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryCollectionReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryCollectionReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryJoinReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryJoinReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryJoinReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryJoinReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryNonScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryNonScalarReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryNonScalarReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryNonScalarReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryRootReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryRootReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryRootReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryRootReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryScalarReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryScalarReturn.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQueryScalarReturn.java
diff --git a/core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
rename to hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
diff --git a/core/src/main/java/org/hibernate/engine/transaction/IsolatedWork.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/IsolatedWork.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/transaction/IsolatedWork.java
rename to hibernate-core/src/main/java/org/hibernate/engine/transaction/IsolatedWork.java
diff --git a/core/src/main/java/org/hibernate/engine/transaction/Isolater.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/Isolater.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/transaction/Isolater.java
rename to hibernate-core/src/main/java/org/hibernate/engine/transaction/Isolater.java
diff --git a/core/src/main/java/org/hibernate/engine/transaction/NullSynchronizationException.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/NullSynchronizationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/transaction/NullSynchronizationException.java
rename to hibernate-core/src/main/java/org/hibernate/engine/transaction/NullSynchronizationException.java
diff --git a/core/src/main/java/org/hibernate/engine/transaction/SynchronizationRegistry.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/SynchronizationRegistry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/engine/transaction/SynchronizationRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/engine/transaction/SynchronizationRegistry.java
diff --git a/core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java b/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
diff --git a/core/src/main/java/org/hibernate/event/AbstractEvent.java b/hibernate-core/src/main/java/org/hibernate/event/AbstractEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/AbstractEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/AbstractEvent.java
diff --git a/core/src/main/java/org/hibernate/event/AbstractPreDatabaseOperationEvent.java b/hibernate-core/src/main/java/org/hibernate/event/AbstractPreDatabaseOperationEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/AbstractPreDatabaseOperationEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/AbstractPreDatabaseOperationEvent.java
diff --git a/core/src/main/java/org/hibernate/event/AutoFlushEvent.java b/hibernate-core/src/main/java/org/hibernate/event/AutoFlushEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/AutoFlushEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/AutoFlushEvent.java
diff --git a/core/src/main/java/org/hibernate/event/AutoFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/AutoFlushEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/AutoFlushEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/AutoFlushEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/DeleteEvent.java b/hibernate-core/src/main/java/org/hibernate/event/DeleteEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/DeleteEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/DeleteEvent.java
diff --git a/core/src/main/java/org/hibernate/event/DeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/DeleteEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/DeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/DeleteEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/Destructible.java b/hibernate-core/src/main/java/org/hibernate/event/Destructible.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/Destructible.java
rename to hibernate-core/src/main/java/org/hibernate/event/Destructible.java
diff --git a/core/src/main/java/org/hibernate/event/DirtyCheckEvent.java b/hibernate-core/src/main/java/org/hibernate/event/DirtyCheckEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/DirtyCheckEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/DirtyCheckEvent.java
diff --git a/core/src/main/java/org/hibernate/event/DirtyCheckEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/DirtyCheckEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/DirtyCheckEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/DirtyCheckEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/EventListeners.java b/hibernate-core/src/main/java/org/hibernate/event/EventListeners.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/EventListeners.java
rename to hibernate-core/src/main/java/org/hibernate/event/EventListeners.java
diff --git a/core/src/main/java/org/hibernate/event/EventSource.java b/hibernate-core/src/main/java/org/hibernate/event/EventSource.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/EventSource.java
rename to hibernate-core/src/main/java/org/hibernate/event/EventSource.java
diff --git a/core/src/main/java/org/hibernate/event/EvictEvent.java b/hibernate-core/src/main/java/org/hibernate/event/EvictEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/EvictEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/EvictEvent.java
diff --git a/core/src/main/java/org/hibernate/event/EvictEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/EvictEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/EvictEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/EvictEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/FlushEntityEvent.java b/hibernate-core/src/main/java/org/hibernate/event/FlushEntityEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/FlushEntityEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/FlushEntityEvent.java
diff --git a/core/src/main/java/org/hibernate/event/FlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/FlushEntityEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/FlushEntityEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/FlushEntityEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/FlushEvent.java b/hibernate-core/src/main/java/org/hibernate/event/FlushEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/FlushEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/FlushEvent.java
diff --git a/core/src/main/java/org/hibernate/event/FlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/FlushEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/FlushEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/FlushEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/Initializable.java b/hibernate-core/src/main/java/org/hibernate/event/Initializable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/Initializable.java
rename to hibernate-core/src/main/java/org/hibernate/event/Initializable.java
diff --git a/core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java b/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
diff --git a/core/src/main/java/org/hibernate/event/InitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/InitializeCollectionEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/LoadEvent.java b/hibernate-core/src/main/java/org/hibernate/event/LoadEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/LoadEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/LoadEvent.java
diff --git a/core/src/main/java/org/hibernate/event/LoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/LoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/LoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/LoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/LockEvent.java b/hibernate-core/src/main/java/org/hibernate/event/LockEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/LockEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/LockEvent.java
diff --git a/core/src/main/java/org/hibernate/event/LockEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/LockEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/LockEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/LockEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/MergeEvent.java b/hibernate-core/src/main/java/org/hibernate/event/MergeEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/MergeEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/MergeEvent.java
diff --git a/core/src/main/java/org/hibernate/event/MergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/MergeEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/MergeEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/MergeEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PersistEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PersistEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PersistEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PersistEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PersistEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PersistEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PersistEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PersistEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionRecreateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionRecreateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionRemoveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionRemoveEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostCollectionUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostCollectionUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostDeleteEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostDeleteEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostDeleteEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostDeleteEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostDeleteEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostDeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostDeleteEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostInsertEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostInsertEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostInsertEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostInsertEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostInsertEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostInsertEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostInsertEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostInsertEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostLoadEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostLoadEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostLoadEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostLoadEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PostUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostUpdateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostUpdateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostUpdateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PostUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PostUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PostUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PostUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionRecreateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionRecreateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionRemoveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionRemoveEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreCollectionUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreCollectionUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreDeleteEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreDeleteEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreDeleteEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreDeleteEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreDeleteEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreDeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreDeleteEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreInsertEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreInsertEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreInsertEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreInsertEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreInsertEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreInsertEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreInsertEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreInsertEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreLoadEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreLoadEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreLoadEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreLoadEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/PreUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreUpdateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreUpdateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreUpdateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/PreUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/PreUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/PreUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/PreUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/RefreshEvent.java b/hibernate-core/src/main/java/org/hibernate/event/RefreshEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/RefreshEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/RefreshEvent.java
diff --git a/core/src/main/java/org/hibernate/event/RefreshEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/RefreshEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/RefreshEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/RefreshEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/ReplicateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/ReplicateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/ReplicateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/ReplicateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/ReplicateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/ReplicateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/ReplicateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/ReplicateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java
rename to hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEvent.java
diff --git a/core/src/main/java/org/hibernate/event/SaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/SaveOrUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/SaveOrUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/AbstractLockUpgradeEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/AbstractVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultAutoFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultAutoFlushEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultAutoFlushEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultAutoFlushEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultDirtyCheckEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDirtyCheckEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultDirtyCheckEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultDirtyCheckEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultFlushEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultLockEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistOnFlushEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultPostLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultPreLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPreLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultPreLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultPreLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateCopyEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateCopyEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateCopyEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateCopyEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DefaultUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/EventCache.java b/hibernate-core/src/main/java/org/hibernate/event/def/EventCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/EventCache.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/EventCache.java
diff --git a/core/src/main/java/org/hibernate/event/def/EvictVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/EvictVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/FlushVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/FlushVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/OnLockVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/ProxyVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/ReattachVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/ReattachVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/ReattachVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/ReattachVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/WrapVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/WrapVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
diff --git a/core/src/main/java/org/hibernate/event/def/package.html b/hibernate-core/src/main/java/org/hibernate/event/def/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/event/def/package.html
rename to hibernate-core/src/main/java/org/hibernate/event/def/package.html
diff --git a/core/src/main/java/org/hibernate/event/package.html b/hibernate-core/src/main/java/org/hibernate/event/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/event/package.html
rename to hibernate-core/src/main/java/org/hibernate/event/package.html
diff --git a/core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
diff --git a/core/src/main/java/org/hibernate/exception/Configurable.java b/hibernate-core/src/main/java/org/hibernate/exception/Configurable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/Configurable.java
rename to hibernate-core/src/main/java/org/hibernate/exception/Configurable.java
diff --git a/core/src/main/java/org/hibernate/exception/ConstraintViolationException.java b/hibernate-core/src/main/java/org/hibernate/exception/ConstraintViolationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/ConstraintViolationException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/ConstraintViolationException.java
diff --git a/core/src/main/java/org/hibernate/exception/DataException.java b/hibernate-core/src/main/java/org/hibernate/exception/DataException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/DataException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/DataException.java
diff --git a/core/src/main/java/org/hibernate/exception/GenericJDBCException.java b/hibernate-core/src/main/java/org/hibernate/exception/GenericJDBCException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/GenericJDBCException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/GenericJDBCException.java
diff --git a/core/src/main/java/org/hibernate/exception/JDBCConnectionException.java b/hibernate-core/src/main/java/org/hibernate/exception/JDBCConnectionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/JDBCConnectionException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/JDBCConnectionException.java
diff --git a/core/src/main/java/org/hibernate/exception/JDBCExceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/exception/JDBCExceptionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/JDBCExceptionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/exception/JDBCExceptionHelper.java
diff --git a/core/src/main/java/org/hibernate/exception/LockAcquisitionException.java b/hibernate-core/src/main/java/org/hibernate/exception/LockAcquisitionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/LockAcquisitionException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/LockAcquisitionException.java
diff --git a/core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java
diff --git a/core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java b/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
rename to hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
diff --git a/core/src/main/java/org/hibernate/exception/SQLGrammarException.java b/hibernate-core/src/main/java/org/hibernate/exception/SQLGrammarException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/SQLGrammarException.java
rename to hibernate-core/src/main/java/org/hibernate/exception/SQLGrammarException.java
diff --git a/core/src/main/java/org/hibernate/exception/SQLStateConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/SQLStateConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
diff --git a/core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java b/hibernate-core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java
diff --git a/core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java b/hibernate-core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java
diff --git a/core/src/main/java/org/hibernate/exception/package.html b/hibernate-core/src/main/java/org/hibernate/exception/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/exception/package.html
rename to hibernate-core/src/main/java/org/hibernate/exception/package.html
diff --git a/core/src/main/java/org/hibernate/hql/CollectionProperties.java b/hibernate-core/src/main/java/org/hibernate/hql/CollectionProperties.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/CollectionProperties.java
rename to hibernate-core/src/main/java/org/hibernate/hql/CollectionProperties.java
diff --git a/core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/FilterTranslator.java b/hibernate-core/src/main/java/org/hibernate/hql/FilterTranslator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/FilterTranslator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/FilterTranslator.java
diff --git a/core/src/main/java/org/hibernate/hql/HolderInstantiator.java b/hibernate-core/src/main/java/org/hibernate/hql/HolderInstantiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/HolderInstantiator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/HolderInstantiator.java
diff --git a/core/src/main/java/org/hibernate/hql/NameGenerator.java b/hibernate-core/src/main/java/org/hibernate/hql/NameGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/NameGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/NameGenerator.java
diff --git a/core/src/main/java/org/hibernate/hql/ParameterTranslations.java b/hibernate-core/src/main/java/org/hibernate/hql/ParameterTranslations.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ParameterTranslations.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ParameterTranslations.java
diff --git a/core/src/main/java/org/hibernate/hql/QueryExecutionRequestException.java b/hibernate-core/src/main/java/org/hibernate/hql/QueryExecutionRequestException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/QueryExecutionRequestException.java
rename to hibernate-core/src/main/java/org/hibernate/hql/QueryExecutionRequestException.java
diff --git a/core/src/main/java/org/hibernate/hql/QuerySplitter.java b/hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/QuerySplitter.java
rename to hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
diff --git a/core/src/main/java/org/hibernate/hql/QueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/QueryTranslator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
diff --git a/core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/QueryTranslatorFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/antlr/package.html b/hibernate-core/src/main/java/org/hibernate/hql/antlr/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/antlr/package.html
rename to hibernate-core/src/main/java/org/hibernate/hql/antlr/package.html
diff --git a/core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/ASTQueryTranslatorFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/DetailedSemanticException.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/DetailedSemanticException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/DetailedSemanticException.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/DetailedSemanticException.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/ErrorCounter.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/ErrorCounter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/ErrorCounter.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/ErrorCounter.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/ErrorReporter.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/ErrorReporter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/ErrorReporter.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/ErrorReporter.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/HqlASTFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlASTFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/HqlASTFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/HqlASTFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/HqlLexer.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlLexer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/HqlLexer.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/HqlLexer.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/HqlParser.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/HqlParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/HqlParser.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/HqlToken.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlToken.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/HqlToken.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/HqlToken.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/InvalidPathException.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/InvalidPathException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/InvalidPathException.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/InvalidPathException.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/InvalidWithClauseException.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/InvalidWithClauseException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/InvalidWithClauseException.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/InvalidWithClauseException.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/ParseErrorHandler.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/ParseErrorHandler.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/ParseErrorHandler.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/ParseErrorHandler.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/QuerySyntaxException.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/QuerySyntaxException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/QuerySyntaxException.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/QuerySyntaxException.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/SqlASTFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlASTFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/SqlASTFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/SqlASTFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/TypeDiscriminatorMetadata.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/TypeDiscriminatorMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/TypeDiscriminatorMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/TypeDiscriminatorMetadata.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/exec/StatementExecutor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/package.html b/hibernate-core/src/main/java/org/hibernate/hql/ast/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/package.html
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/package.html
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AbstractRestrictableStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractRestrictableStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AbstractRestrictableStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractRestrictableStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AbstractSelectExpression.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractSelectExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AbstractSelectExpression.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractSelectExpression.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AbstractStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AbstractStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AggregateNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregateNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AggregateNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregateNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/BinaryOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/BinaryOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/Case2Node.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Case2Node.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/Case2Node.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Case2Node.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/CaseNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CaseNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/CaseNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CaseNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/CollectionFunction.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CollectionFunction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/CollectionFunction.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CollectionFunction.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/CountNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CountNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/CountNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/CountNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/DeleteStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DeleteStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/DeleteStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DeleteStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/DisplayableNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DisplayableNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/DisplayableNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DisplayableNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ExpectedTypeAwareNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ExpectedTypeAwareNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ExpectedTypeAwareNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ExpectedTypeAwareNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FromClause.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromClause.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FromClause.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromClause.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FromReferenceNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromReferenceNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FromReferenceNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromReferenceNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/FunctionNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FunctionNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/FunctionNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FunctionNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/HqlSqlWalkerNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/HqlSqlWalkerNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/HqlSqlWalkerNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/HqlSqlWalkerNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ImpliedFromElement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ImpliedFromElement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ImpliedFromElement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ImpliedFromElement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InLogicOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IndexNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/InitializeableNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InitializeableNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/InitializeableNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InitializeableNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/InsertStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InsertStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/InsertStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/InsertStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/IsNotNullLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IsNotNullLogicOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/IsNotNullLogicOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IsNotNullLogicOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/IsNullLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IsNullLogicOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/IsNullLogicOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IsNullLogicOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/MapKeyNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapKeyNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/MapKeyNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapKeyNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/MapValueNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapValueNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/MapValueNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapValueNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/MethodNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MethodNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/MethodNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MethodNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/Node.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/Node.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/OperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/OperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/OperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/OperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/OrderByClause.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/OrderByClause.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/OrderByClause.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/OrderByClause.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterContainer.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ParameterNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/PathNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/PathNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/PathNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/PathNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/QueryNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/QueryNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/QueryNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/QueryNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ResolvableNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResolvableNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ResolvableNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResolvableNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/RestrictableStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/RestrictableStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/RestrictableStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/RestrictableStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SelectClause.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectClause.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SelectClause.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectClause.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SelectExpression.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SelectExpression.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpression.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionImpl.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionList.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionList.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionList.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SelectExpressionList.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SessionFactoryAwareNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SqlFragment.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SqlFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SqlFragment.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SqlFragment.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/SqlNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SqlNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/SqlNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/SqlNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/Statement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Statement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/Statement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Statement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/UnaryArithmeticNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryArithmeticNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/UnaryArithmeticNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryArithmeticNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/UnaryOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryOperatorNode.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/UnaryOperatorNode.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryOperatorNode.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/tree/UpdateStatement.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UpdateStatement.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/tree/UpdateStatement.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UpdateStatement.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ASTAppender.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTAppender.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ASTAppender.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTAppender.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ASTIterator.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ASTIterator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTIterator.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ASTParentsFirstIterator.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTParentsFirstIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ASTParentsFirstIterator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTParentsFirstIterator.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ASTUtil.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTUtil.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ASTUtil.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTUtil.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/ColumnHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ColumnHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/ColumnHelper.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/ColumnHelper.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/NodeTraverser.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/NodeTraverser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/NodeTraverser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/NodeTraverser.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/SessionFactoryHelper.java
diff --git a/core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/ClassicQueryTranslatorFactory.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/ClauseParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/ClauseParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/ClauseParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/ClauseParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/FromParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/FromParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/FromParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/FromParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/FromPathExpressionParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/FromPathExpressionParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/FromPathExpressionParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/FromPathExpressionParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/GroupByParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/GroupByParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/GroupByParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/GroupByParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/HavingParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/HavingParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/HavingParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/HavingParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/OrderByParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/OrderByParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/OrderByParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/OrderByParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/Parser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/Parser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/Parser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/Parser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/ParserHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/ParserHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/ParserHelper.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/ParserHelper.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/PathExpressionParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/SelectParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/SelectParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/SelectPathExpressionParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectPathExpressionParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/SelectPathExpressionParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/SelectPathExpressionParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/WhereParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/WhereParser.java
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
diff --git a/core/src/main/java/org/hibernate/hql/classic/package.html b/hibernate-core/src/main/java/org/hibernate/hql/classic/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/classic/package.html
rename to hibernate-core/src/main/java/org/hibernate/hql/classic/package.html
diff --git a/core/src/main/java/org/hibernate/hql/package.html b/hibernate-core/src/main/java/org/hibernate/hql/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/hql/package.html
rename to hibernate-core/src/main/java/org/hibernate/hql/package.html
diff --git a/core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/AbstractPostInsertGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/Assigned.java b/hibernate-core/src/main/java/org/hibernate/id/Assigned.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/Assigned.java
rename to hibernate-core/src/main/java/org/hibernate/id/Assigned.java
diff --git a/core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/Configurable.java b/hibernate-core/src/main/java/org/hibernate/id/Configurable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/Configurable.java
rename to hibernate-core/src/main/java/org/hibernate/id/Configurable.java
diff --git a/core/src/main/java/org/hibernate/id/ForeignGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/ForeignGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/GUIDGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/GUIDGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/IdentifierGenerationException.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IdentifierGenerationException.java
rename to hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerationException.java
diff --git a/core/src/main/java/org/hibernate/id/IdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IdentifierGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/IdentifierGeneratorAggregator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorAggregator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IdentifierGeneratorAggregator.java
rename to hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorAggregator.java
diff --git a/core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
rename to hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
diff --git a/core/src/main/java/org/hibernate/id/IdentityGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IdentityGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/IncrementGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IncrementGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/IntegralDataTypeHolder.java b/hibernate-core/src/main/java/org/hibernate/id/IntegralDataTypeHolder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/IntegralDataTypeHolder.java
rename to hibernate-core/src/main/java/org/hibernate/id/IntegralDataTypeHolder.java
diff --git a/core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/PostInsertIdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/PostInsertIdentifierGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/PostInsertIdentifierGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/PostInsertIdentifierGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/PostInsertIdentityPersister.java b/hibernate-core/src/main/java/org/hibernate/id/PostInsertIdentityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/PostInsertIdentityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/id/PostInsertIdentityPersister.java
diff --git a/core/src/main/java/org/hibernate/id/ResultSetIdentifierConsumer.java b/hibernate-core/src/main/java/org/hibernate/id/ResultSetIdentifierConsumer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/ResultSetIdentifierConsumer.java
rename to hibernate-core/src/main/java/org/hibernate/id/ResultSetIdentifierConsumer.java
diff --git a/core/src/main/java/org/hibernate/id/SelectGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/SelectGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/SelectGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/SequenceGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/SequenceGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/SequenceHiLoGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/TableGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/TableHiLoGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/TableHiLoGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/TableHiLoGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/TableHiLoGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java b/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/id/UUIDGenerationStrategy.java
diff --git a/core/src/main/java/org/hibernate/id/UUIDGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/UUIDGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/UUIDHexGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/UUIDHexGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/UUIDHexGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/UUIDHexGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/AccessCallback.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/AccessCallback.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/AccessCallback.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/AccessCallback.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/DatabaseStructure.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/Optimizer.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/Optimizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/Optimizer.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/Optimizer.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
diff --git a/core/src/main/java/org/hibernate/id/enhanced/TableStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
rename to hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
diff --git a/core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java b/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
diff --git a/core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java b/hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
diff --git a/core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
diff --git a/core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
diff --git a/core/src/main/java/org/hibernate/id/insert/Binder.java b/hibernate-core/src/main/java/org/hibernate/id/insert/Binder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/Binder.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/Binder.java
diff --git a/core/src/main/java/org/hibernate/id/insert/IdentifierGeneratingInsert.java b/hibernate-core/src/main/java/org/hibernate/id/insert/IdentifierGeneratingInsert.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/IdentifierGeneratingInsert.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/IdentifierGeneratingInsert.java
diff --git a/core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java b/hibernate-core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/InsertGeneratedIdentifierDelegate.java
diff --git a/core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java b/hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
rename to hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
diff --git a/core/src/main/java/org/hibernate/id/package.html b/hibernate-core/src/main/java/org/hibernate/id/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/id/package.html
rename to hibernate-core/src/main/java/org/hibernate/id/package.html
diff --git a/core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java b/hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
diff --git a/core/src/main/java/org/hibernate/id/uuid/Helper.java b/hibernate-core/src/main/java/org/hibernate/id/uuid/Helper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/uuid/Helper.java
rename to hibernate-core/src/main/java/org/hibernate/id/uuid/Helper.java
diff --git a/core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java b/hibernate-core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/id/uuid/StandardRandomStrategy.java
diff --git a/core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
rename to hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
diff --git a/core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/CriteriaImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/CriteriaImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/FilterImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/FilterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/IteratorImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/IteratorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/QueryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/QueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/SQLQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
rename to hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
diff --git a/core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/SessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
diff --git a/core/src/main/java/org/hibernate/impl/package.html b/hibernate-core/src/main/java/org/hibernate/impl/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/impl/package.html
rename to hibernate-core/src/main/java/org/hibernate/impl/package.html
diff --git a/core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java
diff --git a/core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
diff --git a/core/src/main/java/org/hibernate/intercept/FieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/FieldInterceptor.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptor.java
diff --git a/core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java b/hibernate-core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java
diff --git a/core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/cglib/CGLIBHelper.java
diff --git a/core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/cglib/FieldInterceptorImpl.java
diff --git a/core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java
diff --git a/core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java
rename to hibernate-core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java
diff --git a/core/src/main/java/org/hibernate/intercept/package.html b/hibernate-core/src/main/java/org/hibernate/intercept/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/intercept/package.html
rename to hibernate-core/src/main/java/org/hibernate/intercept/package.html
diff --git a/core/src/main/java/org/hibernate/internal/util/beans/BeanInfoHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/beans/BeanInfoHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/beans/BeanInfoHelper.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/beans/BeanInfoHelper.java
diff --git a/core/src/main/java/org/hibernate/internal/util/beans/BeanIntrospectionException.java b/hibernate-core/src/main/java/org/hibernate/internal/util/beans/BeanIntrospectionException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/beans/BeanIntrospectionException.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/beans/BeanIntrospectionException.java
diff --git a/core/src/main/java/org/hibernate/internal/util/config/ConfigurationException.java b/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/config/ConfigurationException.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationException.java
diff --git a/core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jndi/JndiException.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jndi/JndiException.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiException.java
diff --git a/core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
diff --git a/core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BatchFailedException.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchFailedException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BatchFailedException.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BatchFailedException.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BatchedTooManyRowsAffectedException.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchedTooManyRowsAffectedException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BatchedTooManyRowsAffectedException.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BatchedTooManyRowsAffectedException.java
diff --git a/core/src/main/java/org/hibernate/jdbc/Batcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/Batcher.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
diff --git a/core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
diff --git a/core/src/main/java/org/hibernate/jdbc/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
diff --git a/core/src/main/java/org/hibernate/jdbc/ConnectionWrapper.java b/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionWrapper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/ConnectionWrapper.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionWrapper.java
diff --git a/core/src/main/java/org/hibernate/jdbc/Expectation.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Expectation.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/Expectation.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/Expectation.java
diff --git a/core/src/main/java/org/hibernate/jdbc/Expectations.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/Expectations.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
diff --git a/core/src/main/java/org/hibernate/jdbc/JDBCContext.java b/hibernate-core/src/main/java/org/hibernate/jdbc/JDBCContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/JDBCContext.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/JDBCContext.java
diff --git a/core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
diff --git a/core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
diff --git a/core/src/main/java/org/hibernate/jdbc/TooManyRowsAffectedException.java b/hibernate-core/src/main/java/org/hibernate/jdbc/TooManyRowsAffectedException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/TooManyRowsAffectedException.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/TooManyRowsAffectedException.java
diff --git a/core/src/main/java/org/hibernate/jdbc/Work.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Work.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/Work.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/Work.java
diff --git a/core/src/main/java/org/hibernate/jdbc/package.html b/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/package.html
rename to hibernate-core/src/main/java/org/hibernate/jdbc/package.html
diff --git a/core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java
diff --git a/core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java
diff --git a/core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java
diff --git a/core/src/main/java/org/hibernate/jdbc/util/Formatter.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/Formatter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/util/Formatter.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/util/Formatter.java
diff --git a/core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java
rename to hibernate-core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java
diff --git a/core/src/main/java/org/hibernate/jmx/HibernateService.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/HibernateService.java
rename to hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
diff --git a/core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
rename to hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
diff --git a/core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
rename to hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
diff --git a/core/src/main/java/org/hibernate/jmx/StatisticsService.java b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/StatisticsService.java
rename to hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
diff --git a/core/src/main/java/org/hibernate/jmx/StatisticsServiceMBean.java b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsServiceMBean.java
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/StatisticsServiceMBean.java
rename to hibernate-core/src/main/java/org/hibernate/jmx/StatisticsServiceMBean.java
diff --git a/core/src/main/java/org/hibernate/jmx/package.html b/hibernate-core/src/main/java/org/hibernate/jmx/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/jmx/package.html
rename to hibernate-core/src/main/java/org/hibernate/jmx/package.html
diff --git a/core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/AbstractEntityJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/BasicLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/BasicLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/BasicLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/BasicLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/CollectionAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/CollectionAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/CollectionAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/CollectionAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/ColumnEntityAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/ColumnEntityAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/ColumnEntityAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/ColumnEntityAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/EntityAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/EntityAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/EntityAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/EntityAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/JoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/JoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/Loader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/Loader.java
diff --git a/core/src/main/java/org/hibernate/loader/MultipleBagFetchException.java b/hibernate-core/src/main/java/org/hibernate/loader/MultipleBagFetchException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/MultipleBagFetchException.java
rename to hibernate-core/src/main/java/org/hibernate/loader/MultipleBagFetchException.java
diff --git a/core/src/main/java/org/hibernate/loader/OuterJoinLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/OuterJoinLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/OuterJoinLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java b/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
rename to hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
diff --git a/core/src/main/java/org/hibernate/loader/PropertyPath.java b/hibernate-core/src/main/java/org/hibernate/loader/PropertyPath.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/PropertyPath.java
rename to hibernate-core/src/main/java/org/hibernate/loader/PropertyPath.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionInitializer.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/collection/package.html b/hibernate-core/src/main/java/org/hibernate/loader/collection/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/collection/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/collection/package.html
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
rename to hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
diff --git a/core/src/main/java/org/hibernate/loader/criteria/package.html b/hibernate-core/src/main/java/org/hibernate/loader/criteria/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/criteria/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/criteria/package.html
diff --git a/core/src/main/java/org/hibernate/loader/custom/CollectionFetchReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CollectionFetchReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/CollectionFetchReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/CollectionFetchReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CollectionReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/CollectionReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/CollectionReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/CustomQuery.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/EntityFetchReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/EntityFetchReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/EntityFetchReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/EntityFetchReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/FetchReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/FetchReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/FetchReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/FetchReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/NonScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/NonScalarReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/NonScalarReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/NonScalarReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/Return.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/Return.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/Return.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/Return.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/RootReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/RootReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/RootReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/RootReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/ScalarReturn.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/ScalarReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/ScalarReturn.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/package.html b/hibernate-core/src/main/java/org/hibernate/loader/custom/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/package.html
diff --git a/core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryParser.java
diff --git a/core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/AbstractEntityLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/EntityJoinWalker.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/EntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/EntityLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/EntityLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/EntityLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/entity/package.html b/hibernate-core/src/main/java/org/hibernate/loader/entity/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/entity/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/entity/package.html
diff --git a/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
diff --git a/core/src/main/java/org/hibernate/loader/hql/package.html b/hibernate-core/src/main/java/org/hibernate/loader/hql/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/hql/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/hql/package.html
diff --git a/core/src/main/java/org/hibernate/loader/package.html b/hibernate-core/src/main/java/org/hibernate/loader/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/loader/package.html
rename to hibernate-core/src/main/java/org/hibernate/loader/package.html
diff --git a/core/src/main/java/org/hibernate/lob/ReaderInputStream.java b/hibernate-core/src/main/java/org/hibernate/lob/ReaderInputStream.java
similarity index 100%
rename from core/src/main/java/org/hibernate/lob/ReaderInputStream.java
rename to hibernate-core/src/main/java/org/hibernate/lob/ReaderInputStream.java
diff --git a/core/src/main/java/org/hibernate/mapping/AbstractAuxiliaryDatabaseObject.java b/hibernate-core/src/main/java/org/hibernate/mapping/AbstractAuxiliaryDatabaseObject.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/AbstractAuxiliaryDatabaseObject.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/AbstractAuxiliaryDatabaseObject.java
diff --git a/core/src/main/java/org/hibernate/mapping/Any.java b/hibernate-core/src/main/java/org/hibernate/mapping/Any.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Any.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Any.java
diff --git a/core/src/main/java/org/hibernate/mapping/Array.java b/hibernate-core/src/main/java/org/hibernate/mapping/Array.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Array.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Array.java
diff --git a/core/src/main/java/org/hibernate/mapping/AuxiliaryDatabaseObject.java b/hibernate-core/src/main/java/org/hibernate/mapping/AuxiliaryDatabaseObject.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/AuxiliaryDatabaseObject.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/AuxiliaryDatabaseObject.java
diff --git a/core/src/main/java/org/hibernate/mapping/Backref.java b/hibernate-core/src/main/java/org/hibernate/mapping/Backref.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Backref.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Backref.java
diff --git a/core/src/main/java/org/hibernate/mapping/Bag.java b/hibernate-core/src/main/java/org/hibernate/mapping/Bag.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Bag.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Bag.java
diff --git a/core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Collection.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
diff --git a/core/src/main/java/org/hibernate/mapping/Column.java b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Column.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Column.java
diff --git a/core/src/main/java/org/hibernate/mapping/Component.java b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Component.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Component.java
diff --git a/core/src/main/java/org/hibernate/mapping/Constraint.java b/hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Constraint.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Constraint.java
diff --git a/core/src/main/java/org/hibernate/mapping/DenormalizedTable.java b/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
diff --git a/core/src/main/java/org/hibernate/mapping/DependantValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/DependantValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/DependantValue.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/DependantValue.java
diff --git a/core/src/main/java/org/hibernate/mapping/FetchProfile.java b/hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/FetchProfile.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java
diff --git a/core/src/main/java/org/hibernate/mapping/Fetchable.java b/hibernate-core/src/main/java/org/hibernate/mapping/Fetchable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Fetchable.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Fetchable.java
diff --git a/core/src/main/java/org/hibernate/mapping/Filterable.java b/hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Filterable.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java
diff --git a/core/src/main/java/org/hibernate/mapping/ForeignKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/ForeignKey.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java
diff --git a/core/src/main/java/org/hibernate/mapping/Formula.java b/hibernate-core/src/main/java/org/hibernate/mapping/Formula.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Formula.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Formula.java
diff --git a/core/src/main/java/org/hibernate/mapping/IdGenerator.java b/hibernate-core/src/main/java/org/hibernate/mapping/IdGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/IdGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/IdGenerator.java
diff --git a/core/src/main/java/org/hibernate/mapping/IdentifierBag.java b/hibernate-core/src/main/java/org/hibernate/mapping/IdentifierBag.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/IdentifierBag.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/IdentifierBag.java
diff --git a/core/src/main/java/org/hibernate/mapping/IdentifierCollection.java b/hibernate-core/src/main/java/org/hibernate/mapping/IdentifierCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/IdentifierCollection.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/IdentifierCollection.java
diff --git a/core/src/main/java/org/hibernate/mapping/Index.java b/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Index.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Index.java
diff --git a/core/src/main/java/org/hibernate/mapping/IndexBackref.java b/hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/IndexBackref.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java
diff --git a/core/src/main/java/org/hibernate/mapping/IndexedCollection.java b/hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/IndexedCollection.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/IndexedCollection.java
diff --git a/core/src/main/java/org/hibernate/mapping/Join.java b/hibernate-core/src/main/java/org/hibernate/mapping/Join.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Join.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Join.java
diff --git a/core/src/main/java/org/hibernate/mapping/JoinedSubclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/JoinedSubclass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/JoinedSubclass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/JoinedSubclass.java
diff --git a/core/src/main/java/org/hibernate/mapping/KeyValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/KeyValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/KeyValue.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/KeyValue.java
diff --git a/core/src/main/java/org/hibernate/mapping/List.java b/hibernate-core/src/main/java/org/hibernate/mapping/List.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/List.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/List.java
diff --git a/core/src/main/java/org/hibernate/mapping/ManyToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/ManyToOne.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
diff --git a/core/src/main/java/org/hibernate/mapping/Map.java b/hibernate-core/src/main/java/org/hibernate/mapping/Map.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Map.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Map.java
diff --git a/core/src/main/java/org/hibernate/mapping/MappedSuperclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/MappedSuperclass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/MappedSuperclass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/MappedSuperclass.java
diff --git a/core/src/main/java/org/hibernate/mapping/MetaAttributable.java b/hibernate-core/src/main/java/org/hibernate/mapping/MetaAttributable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/MetaAttributable.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/MetaAttributable.java
diff --git a/core/src/main/java/org/hibernate/mapping/MetaAttribute.java b/hibernate-core/src/main/java/org/hibernate/mapping/MetaAttribute.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/MetaAttribute.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/MetaAttribute.java
diff --git a/core/src/main/java/org/hibernate/mapping/MetadataSource.java b/hibernate-core/src/main/java/org/hibernate/mapping/MetadataSource.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/MetadataSource.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/MetadataSource.java
diff --git a/core/src/main/java/org/hibernate/mapping/OneToMany.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/OneToMany.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
diff --git a/core/src/main/java/org/hibernate/mapping/OneToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/OneToOne.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java
diff --git a/core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/PersistentClass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
diff --git a/core/src/main/java/org/hibernate/mapping/PersistentClassVisitor.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClassVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/PersistentClassVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/PersistentClassVisitor.java
diff --git a/core/src/main/java/org/hibernate/mapping/PrimaryKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/PrimaryKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/PrimaryKey.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/PrimaryKey.java
diff --git a/core/src/main/java/org/hibernate/mapping/PrimitiveArray.java b/hibernate-core/src/main/java/org/hibernate/mapping/PrimitiveArray.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/PrimitiveArray.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/PrimitiveArray.java
diff --git a/core/src/main/java/org/hibernate/mapping/Property.java b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Property.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Property.java
diff --git a/core/src/main/java/org/hibernate/mapping/PropertyGeneration.java b/hibernate-core/src/main/java/org/hibernate/mapping/PropertyGeneration.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/PropertyGeneration.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/PropertyGeneration.java
diff --git a/core/src/main/java/org/hibernate/mapping/RelationalModel.java b/hibernate-core/src/main/java/org/hibernate/mapping/RelationalModel.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/RelationalModel.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/RelationalModel.java
diff --git a/core/src/main/java/org/hibernate/mapping/RootClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/RootClass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
diff --git a/core/src/main/java/org/hibernate/mapping/Selectable.java b/hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Selectable.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java
diff --git a/core/src/main/java/org/hibernate/mapping/Set.java b/hibernate-core/src/main/java/org/hibernate/mapping/Set.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Set.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Set.java
diff --git a/core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
diff --git a/core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/SimpleValue.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
diff --git a/core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
diff --git a/core/src/main/java/org/hibernate/mapping/Subclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Subclass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
diff --git a/core/src/main/java/org/hibernate/mapping/SyntheticProperty.java b/hibernate-core/src/main/java/org/hibernate/mapping/SyntheticProperty.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/SyntheticProperty.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/SyntheticProperty.java
diff --git a/core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Table.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Table.java
diff --git a/core/src/main/java/org/hibernate/mapping/TableOwner.java b/hibernate-core/src/main/java/org/hibernate/mapping/TableOwner.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/TableOwner.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/TableOwner.java
diff --git a/core/src/main/java/org/hibernate/mapping/ToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/ToOne.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
diff --git a/core/src/main/java/org/hibernate/mapping/TypeDef.java b/hibernate-core/src/main/java/org/hibernate/mapping/TypeDef.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/TypeDef.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/TypeDef.java
diff --git a/core/src/main/java/org/hibernate/mapping/UnionSubclass.java b/hibernate-core/src/main/java/org/hibernate/mapping/UnionSubclass.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/UnionSubclass.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/UnionSubclass.java
diff --git a/core/src/main/java/org/hibernate/mapping/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/UniqueKey.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
diff --git a/core/src/main/java/org/hibernate/mapping/Value.java b/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/Value.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/Value.java
diff --git a/core/src/main/java/org/hibernate/mapping/ValueVisitor.java b/hibernate-core/src/main/java/org/hibernate/mapping/ValueVisitor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/ValueVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/mapping/ValueVisitor.java
diff --git a/core/src/main/java/org/hibernate/mapping/package.html b/hibernate-core/src/main/java/org/hibernate/mapping/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/mapping/package.html
rename to hibernate-core/src/main/java/org/hibernate/mapping/package.html
diff --git a/core/src/main/java/org/hibernate/metadata/ClassMetadata.java b/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/metadata/ClassMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
diff --git a/core/src/main/java/org/hibernate/metadata/CollectionMetadata.java b/hibernate-core/src/main/java/org/hibernate/metadata/CollectionMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/metadata/CollectionMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/metadata/CollectionMetadata.java
diff --git a/core/src/main/java/org/hibernate/metadata/package.html b/hibernate-core/src/main/java/org/hibernate/metadata/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/metadata/package.html
rename to hibernate-core/src/main/java/org/hibernate/metadata/package.html
diff --git a/core/src/main/java/org/hibernate/package.html b/hibernate-core/src/main/java/org/hibernate/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/package.html
rename to hibernate-core/src/main/java/org/hibernate/package.html
diff --git a/core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/ExplicitParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/ExplicitParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/ExplicitParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/ExplicitParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/NamedParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/ParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/ParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/ParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/ParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
diff --git a/core/src/main/java/org/hibernate/persister/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/PersisterFactory.java
rename to hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/CollectionPropertyNames.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyNames.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/CollectionPropertyNames.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyNames.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/CompositeElementPropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/QueryableCollection.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/QueryableCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/QueryableCollection.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/QueryableCollection.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/SQLLoadableCollection.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/SQLLoadableCollection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/SQLLoadableCollection.java
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/SQLLoadableCollection.java
diff --git a/core/src/main/java/org/hibernate/persister/collection/package.html b/hibernate-core/src/main/java/org/hibernate/persister/collection/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/collection/package.html
rename to hibernate-core/src/main/java/org/hibernate/persister/collection/package.html
diff --git a/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/BasicEntityPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/BasicEntityPropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/BasicEntityPropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/BasicEntityPropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/DiscriminatorMetadata.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/DiscriminatorMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorMetadata.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/Joinable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/Joinable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/Loadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/Loadable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/Lockable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Lockable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/Lockable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/Lockable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/OuterJoinLoadable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/Queryable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/Queryable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/SQLLoadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SQLLoadable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/SQLLoadable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/SQLLoadable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/UniqueKeyLoadable.java
diff --git a/core/src/main/java/org/hibernate/persister/entity/package.html b/hibernate-core/src/main/java/org/hibernate/persister/entity/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/entity/package.html
rename to hibernate-core/src/main/java/org/hibernate/persister/entity/package.html
diff --git a/core/src/main/java/org/hibernate/persister/package.html b/hibernate-core/src/main/java/org/hibernate/persister/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/persister/package.html
rename to hibernate-core/src/main/java/org/hibernate/persister/package.html
diff --git a/core/src/main/java/org/hibernate/pretty/MessageHelper.java b/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/pretty/MessageHelper.java
rename to hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
diff --git a/core/src/main/java/org/hibernate/pretty/Printer.java b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/pretty/Printer.java
rename to hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
diff --git a/core/src/main/java/org/hibernate/pretty/package.html b/hibernate-core/src/main/java/org/hibernate/pretty/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/pretty/package.html
rename to hibernate-core/src/main/java/org/hibernate/pretty/package.html
diff --git a/core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/ChainedPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/ChainedPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/ChainedPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/ChainedPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/Dom4jAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/Dom4jAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/Getter.java b/hibernate-core/src/main/java/org/hibernate/property/Getter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/Getter.java
rename to hibernate-core/src/main/java/org/hibernate/property/Getter.java
diff --git a/core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/MapAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/MapAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/NoopAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/NoopAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/PropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/PropertyAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/property/PropertyAccessor.java
diff --git a/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
diff --git a/core/src/main/java/org/hibernate/property/Setter.java b/hibernate-core/src/main/java/org/hibernate/property/Setter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/property/Setter.java
rename to hibernate-core/src/main/java/org/hibernate/property/Setter.java
diff --git a/core/src/main/java/org/hibernate/property/package.html b/hibernate-core/src/main/java/org/hibernate/property/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/property/package.html
rename to hibernate-core/src/main/java/org/hibernate/property/package.html
diff --git a/core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
diff --git a/core/src/main/java/org/hibernate/proxy/EntityNotFoundDelegate.java b/hibernate-core/src/main/java/org/hibernate/proxy/EntityNotFoundDelegate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/EntityNotFoundDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/EntityNotFoundDelegate.java
diff --git a/core/src/main/java/org/hibernate/proxy/HibernateProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/HibernateProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/HibernateProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/HibernateProxy.java
diff --git a/core/src/main/java/org/hibernate/proxy/HibernateProxyHelper.java b/hibernate-core/src/main/java/org/hibernate/proxy/HibernateProxyHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/HibernateProxyHelper.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/HibernateProxyHelper.java
diff --git a/core/src/main/java/org/hibernate/proxy/LazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/LazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/ProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/ProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxy.java
diff --git a/core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/dom4j/Dom4jProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/map/MapLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/map/MapProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/map/MapProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxy.java
diff --git a/core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/map/MapProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/proxy/package.html b/hibernate-core/src/main/java/org/hibernate/proxy/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/package.html
rename to hibernate-core/src/main/java/org/hibernate/proxy/package.html
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/SerializableProxy.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
diff --git a/core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
similarity index 100%
rename from core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
rename to hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
diff --git a/core/src/main/java/org/hibernate/secure/HibernatePermission.java b/hibernate-core/src/main/java/org/hibernate/secure/HibernatePermission.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/HibernatePermission.java
rename to hibernate-core/src/main/java/org/hibernate/secure/HibernatePermission.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCConfiguration.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCConfiguration.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCConfiguration.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCConfiguration.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCPermissions.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCPermissions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCPermissions.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCPermissions.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCPreDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCPreDeleteEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCPreDeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCPreDeleteEventListener.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCPreInsertEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCPreInsertEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCPreInsertEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCPreInsertEventListener.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCPreLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCPreLoadEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCPreLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCPreLoadEventListener.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCPreUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCPreUpdateEventListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCPreUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCPreUpdateEventListener.java
diff --git a/core/src/main/java/org/hibernate/secure/JACCSecurityListener.java b/hibernate-core/src/main/java/org/hibernate/secure/JACCSecurityListener.java
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/JACCSecurityListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/JACCSecurityListener.java
diff --git a/core/src/main/java/org/hibernate/secure/package.html b/hibernate-core/src/main/java/org/hibernate/secure/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/secure/package.html
rename to hibernate-core/src/main/java/org/hibernate/secure/package.html
diff --git a/core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
diff --git a/core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
diff --git a/core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
rename to hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
diff --git a/core/src/main/java/org/hibernate/service/classloading/spi/ClassLoadingException.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoadingException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/classloading/spi/ClassLoadingException.java
rename to hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoadingException.java
diff --git a/core/src/main/java/org/hibernate/service/internal/ServiceDependencyException.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceDependencyException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/internal/ServiceDependencyException.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/ServiceDependencyException.java
diff --git a/core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
diff --git a/core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java
diff --git a/core/src/main/java/org/hibernate/service/jmx/internal/DisabledJmxServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/DisabledJmxServiceImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jmx/internal/DisabledJmxServiceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/jmx/internal/DisabledJmxServiceImpl.java
diff --git a/core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
diff --git a/core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
diff --git a/core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
rename to hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
diff --git a/core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java
diff --git a/core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
diff --git a/core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
rename to hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
diff --git a/core/src/main/java/org/hibernate/service/spi/Configurable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Configurable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/Configurable.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/Configurable.java
diff --git a/core/src/main/java/org/hibernate/service/spi/InjectService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/InjectService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/InjectService.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/InjectService.java
diff --git a/core/src/main/java/org/hibernate/service/spi/Manageable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/Manageable.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
diff --git a/core/src/main/java/org/hibernate/service/spi/Service.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Service.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/Service.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/Service.java
diff --git a/core/src/main/java/org/hibernate/service/spi/ServiceException.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/ServiceException.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServiceException.java
diff --git a/core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
diff --git a/core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java
diff --git a/core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java
diff --git a/core/src/main/java/org/hibernate/service/spi/Startable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/Startable.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java
diff --git a/core/src/main/java/org/hibernate/service/spi/Stoppable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/Stoppable.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java
diff --git a/core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java b/hibernate-core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java
diff --git a/core/src/main/java/org/hibernate/sql/ANSICaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ANSICaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ANSICaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ANSICaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/ANSIJoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ANSIJoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ANSIJoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ANSIJoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/Alias.java b/hibernate-core/src/main/java/org/hibernate/sql/Alias.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Alias.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Alias.java
diff --git a/core/src/main/java/org/hibernate/sql/AliasGenerator.java b/hibernate-core/src/main/java/org/hibernate/sql/AliasGenerator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/AliasGenerator.java
rename to hibernate-core/src/main/java/org/hibernate/sql/AliasGenerator.java
diff --git a/core/src/main/java/org/hibernate/sql/CacheJoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/CacheJoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/CacheJoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/CacheJoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/CaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/CaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/CaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/CaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/ConditionFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ConditionFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ConditionFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ConditionFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/DecodeCaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/DecodeCaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/DecodeCaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/DecodeCaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/Delete.java b/hibernate-core/src/main/java/org/hibernate/sql/Delete.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Delete.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Delete.java
diff --git a/core/src/main/java/org/hibernate/sql/DerbyCaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/DerbyCaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/DerbyCaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/DerbyCaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/DisjunctionFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/DisjunctionFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/DisjunctionFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/DisjunctionFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/ForUpdateFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/HSQLCaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/HSQLCaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/HSQLCaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/HSQLCaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/InFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/InFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/Insert.java b/hibernate-core/src/main/java/org/hibernate/sql/Insert.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Insert.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Insert.java
diff --git a/core/src/main/java/org/hibernate/sql/InsertSelect.java b/hibernate-core/src/main/java/org/hibernate/sql/InsertSelect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/InsertSelect.java
rename to hibernate-core/src/main/java/org/hibernate/sql/InsertSelect.java
diff --git a/core/src/main/java/org/hibernate/sql/JoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/JoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/MckoiCaseFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/MckoiCaseFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/MckoiCaseFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/MckoiCaseFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/OracleJoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/OracleJoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/OracleJoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/OracleJoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/QueryJoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/QueryJoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/QueryJoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/QueryJoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/QuerySelect.java b/hibernate-core/src/main/java/org/hibernate/sql/QuerySelect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/QuerySelect.java
rename to hibernate-core/src/main/java/org/hibernate/sql/QuerySelect.java
diff --git a/core/src/main/java/org/hibernate/sql/Select.java b/hibernate-core/src/main/java/org/hibernate/sql/Select.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Select.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Select.java
diff --git a/core/src/main/java/org/hibernate/sql/SelectExpression.java b/hibernate-core/src/main/java/org/hibernate/sql/SelectExpression.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/SelectExpression.java
rename to hibernate-core/src/main/java/org/hibernate/sql/SelectExpression.java
diff --git a/core/src/main/java/org/hibernate/sql/SelectFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/SelectFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/SelectFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/SelectFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/SimpleSelect.java b/hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/SimpleSelect.java
rename to hibernate-core/src/main/java/org/hibernate/sql/SimpleSelect.java
diff --git a/core/src/main/java/org/hibernate/sql/Sybase11JoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/Sybase11JoinFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Sybase11JoinFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Sybase11JoinFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/Template.java b/hibernate-core/src/main/java/org/hibernate/sql/Template.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Template.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Template.java
diff --git a/core/src/main/java/org/hibernate/sql/Update.java b/hibernate-core/src/main/java/org/hibernate/sql/Update.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/Update.java
rename to hibernate-core/src/main/java/org/hibernate/sql/Update.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
diff --git a/core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
rename to hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
diff --git a/core/src/main/java/org/hibernate/sql/package.html b/hibernate-core/src/main/java/org/hibernate/sql/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/sql/package.html
rename to hibernate-core/src/main/java/org/hibernate/sql/package.html
diff --git a/core/src/main/java/org/hibernate/stat/CategorizedStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/CategorizedStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/CategorizedStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/CategorizedStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/CollectionStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/CollectionStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/EntityStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/EntityStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/QueryStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/QueryStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/SessionStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/SessionStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
diff --git a/core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/Statistics.java b/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/Statistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
diff --git a/core/src/main/java/org/hibernate/stat/StatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/StatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
diff --git a/core/src/main/java/org/hibernate/stat/StatisticsImplementor.java b/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImplementor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/StatisticsImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/stat/StatisticsImplementor.java
diff --git a/core/src/main/java/org/hibernate/stat/package.html b/hibernate-core/src/main/java/org/hibernate/stat/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/stat/package.html
rename to hibernate-core/src/main/java/org/hibernate/stat/package.html
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/ColumnMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ColumnMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/ColumnMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ColumnMetadata.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/ForeignKeyMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ForeignKeyMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/ForeignKeyMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ForeignKeyMetadata.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/IndexMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/IndexMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/IndexMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/IndexMetadata.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java
diff --git a/core/src/main/java/org/hibernate/tool/hbm2ddl/package.html b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/hbm2ddl/package.html
rename to hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/package.html
diff --git a/core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
diff --git a/core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/instrument/cglib/InstrumentTask.java
diff --git a/core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
rename to hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
diff --git a/core/src/main/java/org/hibernate/tool/instrument/package.html b/hibernate-core/src/main/java/org/hibernate/tool/instrument/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/tool/instrument/package.html
rename to hibernate-core/src/main/java/org/hibernate/tool/instrument/package.html
diff --git a/core/src/main/java/org/hibernate/transaction/BESTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/BESTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/BESTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/BESTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/BTMTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/BTMTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/BTMTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/BTMTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/CMTTransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/CMTTransaction.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java
diff --git a/core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/CacheSynchronization.java b/hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
diff --git a/core/src/main/java/org/hibernate/transaction/JBossTSStandaloneTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JBossTSStandaloneTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JBossTSStandaloneTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JBossTSStandaloneTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JBossTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JBossTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JBossTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JBossTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JDBCTransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
diff --git a/core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/JNDITransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JNDITransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JNDITransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JNDITransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JOTMTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JOTMTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JOTMTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JOTMTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JOnASTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JOnASTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JOnASTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JOnASTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JRun4TransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/JRun4TransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JRun4TransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JRun4TransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/JTATransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JTATransaction.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java
diff --git a/core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/OC4JTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/OC4JTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/OC4JTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/OC4JTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/OrionTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/OrionTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/OrionTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/OrionTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/ResinTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/ResinTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/ResinTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/ResinTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/SunONETransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/SunONETransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/SunONETransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/SunONETransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/TransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/TransactionFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/TransactionFactoryFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactoryFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/TransactionFactoryFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactoryFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/TransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/TransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/TransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/TransactionManagerLookupFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionManagerLookupFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/TransactionManagerLookupFactory.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/TransactionManagerLookupFactory.java
diff --git a/core/src/main/java/org/hibernate/transaction/WebSphereExtendedJTATransactionLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/WebSphereExtendedJTATransactionLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/WebSphereExtendedJTATransactionLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/WebSphereExtendedJTATransactionLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/WebSphereTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/WebSphereTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/WebSphereTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/WebSphereTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/WeblogicTransactionManagerLookup.java b/hibernate-core/src/main/java/org/hibernate/transaction/WeblogicTransactionManagerLookup.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/WeblogicTransactionManagerLookup.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/WeblogicTransactionManagerLookup.java
diff --git a/core/src/main/java/org/hibernate/transaction/package.html b/hibernate-core/src/main/java/org/hibernate/transaction/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/package.html
rename to hibernate-core/src/main/java/org/hibernate/transaction/package.html
diff --git a/core/src/main/java/org/hibernate/transaction/synchronization/AfterCompletionAction.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/AfterCompletionAction.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/synchronization/AfterCompletionAction.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/synchronization/AfterCompletionAction.java
diff --git a/core/src/main/java/org/hibernate/transaction/synchronization/BeforeCompletionManagedFlushChecker.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/BeforeCompletionManagedFlushChecker.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/synchronization/BeforeCompletionManagedFlushChecker.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/synchronization/BeforeCompletionManagedFlushChecker.java
diff --git a/core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
diff --git a/core/src/main/java/org/hibernate/transaction/synchronization/ExceptionMapper.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/ExceptionMapper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/synchronization/ExceptionMapper.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/synchronization/ExceptionMapper.java
diff --git a/core/src/main/java/org/hibernate/transaction/synchronization/HibernateSynchronizationImpl.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/HibernateSynchronizationImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transaction/synchronization/HibernateSynchronizationImpl.java
rename to hibernate-core/src/main/java/org/hibernate/transaction/synchronization/HibernateSynchronizationImpl.java
diff --git a/core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/BasicTransformerAdapter.java b/hibernate-core/src/main/java/org/hibernate/transform/BasicTransformerAdapter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/BasicTransformerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/transform/BasicTransformerAdapter.java
diff --git a/core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/ResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/ResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/ResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/ResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/ToListResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/Transformers.java b/hibernate-core/src/main/java/org/hibernate/transform/Transformers.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/Transformers.java
rename to hibernate-core/src/main/java/org/hibernate/transform/Transformers.java
diff --git a/core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java
diff --git a/core/src/main/java/org/hibernate/transform/package.html b/hibernate-core/src/main/java/org/hibernate/transform/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/transform/package.html
rename to hibernate-core/src/main/java/org/hibernate/transform/package.html
diff --git a/core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java
diff --git a/core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
diff --git a/core/src/main/java/org/hibernate/tuple/ElementWrapper.java b/hibernate-core/src/main/java/org/hibernate/tuple/ElementWrapper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/ElementWrapper.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/ElementWrapper.java
diff --git a/core/src/main/java/org/hibernate/tuple/EntityModeToTuplizerMapping.java b/hibernate-core/src/main/java/org/hibernate/tuple/EntityModeToTuplizerMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/EntityModeToTuplizerMapping.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/EntityModeToTuplizerMapping.java
diff --git a/core/src/main/java/org/hibernate/tuple/IdentifierProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
diff --git a/core/src/main/java/org/hibernate/tuple/Instantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/Instantiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/Instantiator.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/Instantiator.java
diff --git a/core/src/main/java/org/hibernate/tuple/PojoInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
diff --git a/core/src/main/java/org/hibernate/tuple/Property.java b/hibernate-core/src/main/java/org/hibernate/tuple/Property.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/Property.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/Property.java
diff --git a/core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/PropertyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
diff --git a/core/src/main/java/org/hibernate/tuple/StandardProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/StandardProperty.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
diff --git a/core/src/main/java/org/hibernate/tuple/Tuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/Tuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/Tuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/Tuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/VersionProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/VersionProperty.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractComponentTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/ComponentEntityModeToTuplizerMapping.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentEntityModeToTuplizerMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/ComponentEntityModeToTuplizerMapping.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentEntityModeToTuplizerMapping.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/Dom4jComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/Dom4jComponentTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/Dom4jComponentTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/Dom4jComponentTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/DynamicMapComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/DynamicMapComponentTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/DynamicMapComponentTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/DynamicMapComponentTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/EntityEntityModeToTuplizerMapping.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityEntityModeToTuplizerMapping.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/EntityEntityModeToTuplizerMapping.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityEntityModeToTuplizerMapping.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
diff --git a/core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
diff --git a/core/src/main/java/org/hibernate/tuple/package.html b/hibernate-core/src/main/java/org/hibernate/tuple/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/tuple/package.html
rename to hibernate-core/src/main/java/org/hibernate/tuple/package.html
diff --git a/core/src/main/java/org/hibernate/type/AbstractBynaryType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractBynaryType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractCharArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractCharArrayType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractCharArrayType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractCharArrayType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractComponentType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractComponentType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractComponentType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractLobType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractLobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractLobType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractLongBinaryType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractLongBinaryType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractLongBinaryType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractLongBinaryType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractLongStringType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractLongStringType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractLongStringType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractLongStringType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractSingleColumnStandardBasicType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
diff --git a/core/src/main/java/org/hibernate/type/AbstractType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AbstractType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
diff --git a/core/src/main/java/org/hibernate/type/AdaptedImmutableType.java b/hibernate-core/src/main/java/org/hibernate/type/AdaptedImmutableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AdaptedImmutableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AdaptedImmutableType.java
diff --git a/core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AnyType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AnyType.java
diff --git a/core/src/main/java/org/hibernate/type/ArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ArrayType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
diff --git a/core/src/main/java/org/hibernate/type/AssociationType.java b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/AssociationType.java
rename to hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
diff --git a/core/src/main/java/org/hibernate/type/BagType.java b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BagType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BagType.java
diff --git a/core/src/main/java/org/hibernate/type/BasicType.java b/hibernate-core/src/main/java/org/hibernate/type/BasicType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BasicType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BasicType.java
diff --git a/core/src/main/java/org/hibernate/type/BasicTypeRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
diff --git a/core/src/main/java/org/hibernate/type/BigDecimalType.java b/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BigDecimalType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
diff --git a/core/src/main/java/org/hibernate/type/BigIntegerType.java b/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BigIntegerType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
diff --git a/core/src/main/java/org/hibernate/type/BinaryType.java b/hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BinaryType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BinaryType.java
diff --git a/core/src/main/java/org/hibernate/type/BlobType.java b/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BlobType.java
diff --git a/core/src/main/java/org/hibernate/type/BooleanType.java b/hibernate-core/src/main/java/org/hibernate/type/BooleanType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/BooleanType.java
rename to hibernate-core/src/main/java/org/hibernate/type/BooleanType.java
diff --git a/core/src/main/java/org/hibernate/type/ByteArrayBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
diff --git a/core/src/main/java/org/hibernate/type/ByteType.java b/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ByteType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ByteType.java
diff --git a/core/src/main/java/org/hibernate/type/CalendarDateType.java b/hibernate-core/src/main/java/org/hibernate/type/CalendarDateType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CalendarDateType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CalendarDateType.java
diff --git a/core/src/main/java/org/hibernate/type/CalendarType.java b/hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CalendarType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CalendarType.java
diff --git a/core/src/main/java/org/hibernate/type/CharArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/CharArrayType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CharArrayType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CharArrayType.java
diff --git a/core/src/main/java/org/hibernate/type/CharBooleanType.java b/hibernate-core/src/main/java/org/hibernate/type/CharBooleanType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CharBooleanType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CharBooleanType.java
diff --git a/core/src/main/java/org/hibernate/type/CharacterArrayClobType.java b/hibernate-core/src/main/java/org/hibernate/type/CharacterArrayClobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CharacterArrayClobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CharacterArrayClobType.java
diff --git a/core/src/main/java/org/hibernate/type/CharacterArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/CharacterArrayType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CharacterArrayType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CharacterArrayType.java
diff --git a/core/src/main/java/org/hibernate/type/CharacterType.java b/hibernate-core/src/main/java/org/hibernate/type/CharacterType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CharacterType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CharacterType.java
diff --git a/core/src/main/java/org/hibernate/type/ClassType.java b/hibernate-core/src/main/java/org/hibernate/type/ClassType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ClassType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ClassType.java
diff --git a/core/src/main/java/org/hibernate/type/ClobType.java b/hibernate-core/src/main/java/org/hibernate/type/ClobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ClobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ClobType.java
diff --git a/core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CollectionType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
diff --git a/core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ComponentType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
diff --git a/core/src/main/java/org/hibernate/type/CompositeCustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CompositeCustomType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
diff --git a/core/src/main/java/org/hibernate/type/CompositeType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CompositeType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
diff --git a/core/src/main/java/org/hibernate/type/CurrencyType.java b/hibernate-core/src/main/java/org/hibernate/type/CurrencyType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CurrencyType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CurrencyType.java
diff --git a/core/src/main/java/org/hibernate/type/CustomCollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CustomCollectionType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
diff --git a/core/src/main/java/org/hibernate/type/CustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/CustomType.java
rename to hibernate-core/src/main/java/org/hibernate/type/CustomType.java
diff --git a/core/src/main/java/org/hibernate/type/DateType.java b/hibernate-core/src/main/java/org/hibernate/type/DateType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/DateType.java
rename to hibernate-core/src/main/java/org/hibernate/type/DateType.java
diff --git a/core/src/main/java/org/hibernate/type/DbTimestampType.java b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/DbTimestampType.java
rename to hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
diff --git a/core/src/main/java/org/hibernate/type/DiscriminatorType.java b/hibernate-core/src/main/java/org/hibernate/type/DiscriminatorType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/DiscriminatorType.java
rename to hibernate-core/src/main/java/org/hibernate/type/DiscriminatorType.java
diff --git a/core/src/main/java/org/hibernate/type/DoubleType.java b/hibernate-core/src/main/java/org/hibernate/type/DoubleType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/DoubleType.java
rename to hibernate-core/src/main/java/org/hibernate/type/DoubleType.java
diff --git a/core/src/main/java/org/hibernate/type/EmbeddedComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
rename to hibernate-core/src/main/java/org/hibernate/type/EmbeddedComponentType.java
diff --git a/core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/EntityType.java
rename to hibernate-core/src/main/java/org/hibernate/type/EntityType.java
diff --git a/core/src/main/java/org/hibernate/type/EnumType.java b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/EnumType.java
rename to hibernate-core/src/main/java/org/hibernate/type/EnumType.java
diff --git a/core/src/main/java/org/hibernate/type/FloatType.java b/hibernate-core/src/main/java/org/hibernate/type/FloatType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/FloatType.java
rename to hibernate-core/src/main/java/org/hibernate/type/FloatType.java
diff --git a/core/src/main/java/org/hibernate/type/ForeignKeyDirection.java b/hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
rename to hibernate-core/src/main/java/org/hibernate/type/ForeignKeyDirection.java
diff --git a/core/src/main/java/org/hibernate/type/IdentifierBagType.java b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/IdentifierBagType.java
rename to hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
diff --git a/core/src/main/java/org/hibernate/type/IdentifierType.java b/hibernate-core/src/main/java/org/hibernate/type/IdentifierType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/IdentifierType.java
rename to hibernate-core/src/main/java/org/hibernate/type/IdentifierType.java
diff --git a/core/src/main/java/org/hibernate/type/ImageType.java b/hibernate-core/src/main/java/org/hibernate/type/ImageType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ImageType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ImageType.java
diff --git a/core/src/main/java/org/hibernate/type/ImmutableType.java b/hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ImmutableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ImmutableType.java
diff --git a/core/src/main/java/org/hibernate/type/IntegerType.java b/hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/IntegerType.java
rename to hibernate-core/src/main/java/org/hibernate/type/IntegerType.java
diff --git a/core/src/main/java/org/hibernate/type/ListType.java b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ListType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ListType.java
diff --git a/core/src/main/java/org/hibernate/type/LiteralType.java b/hibernate-core/src/main/java/org/hibernate/type/LiteralType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/LiteralType.java
rename to hibernate-core/src/main/java/org/hibernate/type/LiteralType.java
diff --git a/core/src/main/java/org/hibernate/type/LocaleType.java b/hibernate-core/src/main/java/org/hibernate/type/LocaleType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/LocaleType.java
rename to hibernate-core/src/main/java/org/hibernate/type/LocaleType.java
diff --git a/core/src/main/java/org/hibernate/type/LongType.java b/hibernate-core/src/main/java/org/hibernate/type/LongType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/LongType.java
rename to hibernate-core/src/main/java/org/hibernate/type/LongType.java
diff --git a/core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ManyToOneType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
diff --git a/core/src/main/java/org/hibernate/type/MapType.java b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/MapType.java
rename to hibernate-core/src/main/java/org/hibernate/type/MapType.java
diff --git a/core/src/main/java/org/hibernate/type/MaterializedBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/MaterializedBlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/MaterializedBlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/MaterializedBlobType.java
diff --git a/core/src/main/java/org/hibernate/type/MaterializedClobType.java b/hibernate-core/src/main/java/org/hibernate/type/MaterializedClobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/MaterializedClobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/MaterializedClobType.java
diff --git a/core/src/main/java/org/hibernate/type/MetaType.java b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/MetaType.java
rename to hibernate-core/src/main/java/org/hibernate/type/MetaType.java
diff --git a/core/src/main/java/org/hibernate/type/MutableType.java b/hibernate-core/src/main/java/org/hibernate/type/MutableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/MutableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/MutableType.java
diff --git a/core/src/main/java/org/hibernate/type/NullableType.java b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/NullableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/NullableType.java
diff --git a/core/src/main/java/org/hibernate/type/NumericBooleanType.java b/hibernate-core/src/main/java/org/hibernate/type/NumericBooleanType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/NumericBooleanType.java
rename to hibernate-core/src/main/java/org/hibernate/type/NumericBooleanType.java
diff --git a/core/src/main/java/org/hibernate/type/ObjectType.java b/hibernate-core/src/main/java/org/hibernate/type/ObjectType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ObjectType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ObjectType.java
diff --git a/core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/OneToOneType.java
rename to hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
diff --git a/core/src/main/java/org/hibernate/type/OrderedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/OrderedMapType.java
rename to hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
diff --git a/core/src/main/java/org/hibernate/type/OrderedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/OrderedSetType.java
rename to hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
diff --git a/core/src/main/java/org/hibernate/type/PostgresUUIDType.java b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/PostgresUUIDType.java
rename to hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
diff --git a/core/src/main/java/org/hibernate/type/PrimitiveByteArrayBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/PrimitiveByteArrayBlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/PrimitiveByteArrayBlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/PrimitiveByteArrayBlobType.java
diff --git a/core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayClobType.java b/hibernate-core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayClobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayClobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayClobType.java
diff --git a/core/src/main/java/org/hibernate/type/PrimitiveType.java b/hibernate-core/src/main/java/org/hibernate/type/PrimitiveType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/PrimitiveType.java
rename to hibernate-core/src/main/java/org/hibernate/type/PrimitiveType.java
diff --git a/core/src/main/java/org/hibernate/type/SerializableToBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SerializableToBlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
diff --git a/core/src/main/java/org/hibernate/type/SerializableType.java b/hibernate-core/src/main/java/org/hibernate/type/SerializableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SerializableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SerializableType.java
diff --git a/core/src/main/java/org/hibernate/type/SerializationException.java b/hibernate-core/src/main/java/org/hibernate/type/SerializationException.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SerializationException.java
rename to hibernate-core/src/main/java/org/hibernate/type/SerializationException.java
diff --git a/core/src/main/java/org/hibernate/type/SetType.java b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SetType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SetType.java
diff --git a/core/src/main/java/org/hibernate/type/ShortType.java b/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/ShortType.java
rename to hibernate-core/src/main/java/org/hibernate/type/ShortType.java
diff --git a/core/src/main/java/org/hibernate/type/SingleColumnType.java b/hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SingleColumnType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SingleColumnType.java
diff --git a/core/src/main/java/org/hibernate/type/SortedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SortedMapType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
diff --git a/core/src/main/java/org/hibernate/type/SortedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SortedSetType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
diff --git a/core/src/main/java/org/hibernate/type/SpecialOneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
rename to hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
diff --git a/core/src/main/java/org/hibernate/type/StandardBasicTypes.java b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/StandardBasicTypes.java
rename to hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
diff --git a/core/src/main/java/org/hibernate/type/StringClobType.java b/hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/StringClobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/StringClobType.java
diff --git a/core/src/main/java/org/hibernate/type/StringRepresentableType.java b/hibernate-core/src/main/java/org/hibernate/type/StringRepresentableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/StringRepresentableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/StringRepresentableType.java
diff --git a/core/src/main/java/org/hibernate/type/StringType.java b/hibernate-core/src/main/java/org/hibernate/type/StringType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/StringType.java
rename to hibernate-core/src/main/java/org/hibernate/type/StringType.java
diff --git a/core/src/main/java/org/hibernate/type/TextType.java b/hibernate-core/src/main/java/org/hibernate/type/TextType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TextType.java
rename to hibernate-core/src/main/java/org/hibernate/type/TextType.java
diff --git a/core/src/main/java/org/hibernate/type/TimeType.java b/hibernate-core/src/main/java/org/hibernate/type/TimeType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TimeType.java
rename to hibernate-core/src/main/java/org/hibernate/type/TimeType.java
diff --git a/core/src/main/java/org/hibernate/type/TimeZoneType.java b/hibernate-core/src/main/java/org/hibernate/type/TimeZoneType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TimeZoneType.java
rename to hibernate-core/src/main/java/org/hibernate/type/TimeZoneType.java
diff --git a/core/src/main/java/org/hibernate/type/TimestampType.java b/hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TimestampType.java
rename to hibernate-core/src/main/java/org/hibernate/type/TimestampType.java
diff --git a/core/src/main/java/org/hibernate/type/TrueFalseType.java b/hibernate-core/src/main/java/org/hibernate/type/TrueFalseType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TrueFalseType.java
rename to hibernate-core/src/main/java/org/hibernate/type/TrueFalseType.java
diff --git a/core/src/main/java/org/hibernate/type/Type.java b/hibernate-core/src/main/java/org/hibernate/type/Type.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/Type.java
rename to hibernate-core/src/main/java/org/hibernate/type/Type.java
diff --git a/core/src/main/java/org/hibernate/type/TypeFactory.java b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TypeFactory.java
rename to hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
diff --git a/core/src/main/java/org/hibernate/type/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TypeHelper.java
rename to hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
diff --git a/core/src/main/java/org/hibernate/type/TypeResolver.java b/hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/TypeResolver.java
rename to hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
diff --git a/core/src/main/java/org/hibernate/type/UUIDBinaryType.java b/hibernate-core/src/main/java/org/hibernate/type/UUIDBinaryType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/UUIDBinaryType.java
rename to hibernate-core/src/main/java/org/hibernate/type/UUIDBinaryType.java
diff --git a/core/src/main/java/org/hibernate/type/UUIDCharType.java b/hibernate-core/src/main/java/org/hibernate/type/UUIDCharType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/UUIDCharType.java
rename to hibernate-core/src/main/java/org/hibernate/type/UUIDCharType.java
diff --git a/core/src/main/java/org/hibernate/type/UrlType.java b/hibernate-core/src/main/java/org/hibernate/type/UrlType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/UrlType.java
rename to hibernate-core/src/main/java/org/hibernate/type/UrlType.java
diff --git a/core/src/main/java/org/hibernate/type/VersionType.java b/hibernate-core/src/main/java/org/hibernate/type/VersionType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/VersionType.java
rename to hibernate-core/src/main/java/org/hibernate/type/VersionType.java
diff --git a/core/src/main/java/org/hibernate/type/WrappedMaterializedBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/WrappedMaterializedBlobType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/WrappedMaterializedBlobType.java
rename to hibernate-core/src/main/java/org/hibernate/type/WrappedMaterializedBlobType.java
diff --git a/core/src/main/java/org/hibernate/type/WrapperBinaryType.java b/hibernate-core/src/main/java/org/hibernate/type/WrapperBinaryType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/WrapperBinaryType.java
rename to hibernate-core/src/main/java/org/hibernate/type/WrapperBinaryType.java
diff --git a/core/src/main/java/org/hibernate/type/XmlRepresentableType.java b/hibernate-core/src/main/java/org/hibernate/type/XmlRepresentableType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/XmlRepresentableType.java
rename to hibernate-core/src/main/java/org/hibernate/type/XmlRepresentableType.java
diff --git a/core/src/main/java/org/hibernate/type/YesNoType.java b/hibernate-core/src/main/java/org/hibernate/type/YesNoType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/YesNoType.java
rename to hibernate-core/src/main/java/org/hibernate/type/YesNoType.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/BinaryStream.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/BinaryStream.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/BinaryStream.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/BinaryStream.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/CharacterStream.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/CharacterStream.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/CharacterStream.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/CharacterStream.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/ValueBinder.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/ValueBinder.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueBinder.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/WrapperOptions.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/WrapperOptions.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/WrapperOptions.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/WrapperOptions.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ArrayMutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ArrayMutabilityPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ArrayMutabilityPlan.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ArrayMutabilityPlan.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigDecimalTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BinaryStreamImpl.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BlobTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BooleanTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteArrayTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ByteTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ByteTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ByteTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterArrayTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterStreamImpl.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CharacterTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CharacterTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CharacterTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClobTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CurrencyTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DateTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DoubleTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/FloatTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ImmutableMutabilityPlan.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IncomparableComparator.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/IntegerTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JavaTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcDateTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimeTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LocaleTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/LongTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutabilityPlan.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/MutableMutabilityPlan.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveByteArrayTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/PrimitiveCharacterArrayTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/ShortTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ShortTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/ShortTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ShortTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/StringTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/TimeZoneTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BinaryTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/CharTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/FloatTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarbinaryTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/LongVarcharTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/NumericTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
diff --git a/core/src/main/java/org/hibernate/type/descriptor/sql/package.html b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/type/descriptor/sql/package.html
rename to hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/package.html
diff --git a/core/src/main/java/org/hibernate/type/package.html b/hibernate-core/src/main/java/org/hibernate/type/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/type/package.html
rename to hibernate-core/src/main/java/org/hibernate/type/package.html
diff --git a/core/src/main/java/org/hibernate/usertype/CompositeUserType.java b/hibernate-core/src/main/java/org/hibernate/usertype/CompositeUserType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/CompositeUserType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/CompositeUserType.java
diff --git a/core/src/main/java/org/hibernate/usertype/EnhancedUserType.java b/hibernate-core/src/main/java/org/hibernate/usertype/EnhancedUserType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/EnhancedUserType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/EnhancedUserType.java
diff --git a/core/src/main/java/org/hibernate/usertype/LoggableUserType.java b/hibernate-core/src/main/java/org/hibernate/usertype/LoggableUserType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/LoggableUserType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/LoggableUserType.java
diff --git a/core/src/main/java/org/hibernate/usertype/ParameterizedType.java b/hibernate-core/src/main/java/org/hibernate/usertype/ParameterizedType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/ParameterizedType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/ParameterizedType.java
diff --git a/core/src/main/java/org/hibernate/usertype/UserCollectionType.java b/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/UserCollectionType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
diff --git a/core/src/main/java/org/hibernate/usertype/UserType.java b/hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/UserType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/UserType.java
diff --git a/core/src/main/java/org/hibernate/usertype/UserVersionType.java b/hibernate-core/src/main/java/org/hibernate/usertype/UserVersionType.java
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/UserVersionType.java
rename to hibernate-core/src/main/java/org/hibernate/usertype/UserVersionType.java
diff --git a/core/src/main/java/org/hibernate/usertype/package.html b/hibernate-core/src/main/java/org/hibernate/usertype/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/usertype/package.html
rename to hibernate-core/src/main/java/org/hibernate/usertype/package.html
diff --git a/core/src/main/java/org/hibernate/util/ArrayHelper.java b/hibernate-core/src/main/java/org/hibernate/util/ArrayHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/ArrayHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/ArrayHelper.java
diff --git a/core/src/main/java/org/hibernate/util/BytesHelper.java b/hibernate-core/src/main/java/org/hibernate/util/BytesHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/BytesHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/BytesHelper.java
diff --git a/core/src/main/java/org/hibernate/util/CalendarComparator.java b/hibernate-core/src/main/java/org/hibernate/util/CalendarComparator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/CalendarComparator.java
rename to hibernate-core/src/main/java/org/hibernate/util/CalendarComparator.java
diff --git a/core/src/main/java/org/hibernate/util/Cloneable.java b/hibernate-core/src/main/java/org/hibernate/util/Cloneable.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/Cloneable.java
rename to hibernate-core/src/main/java/org/hibernate/util/Cloneable.java
diff --git a/core/src/main/java/org/hibernate/util/CollectionHelper.java b/hibernate-core/src/main/java/org/hibernate/util/CollectionHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/CollectionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/CollectionHelper.java
diff --git a/core/src/main/java/org/hibernate/util/ComparableComparator.java b/hibernate-core/src/main/java/org/hibernate/util/ComparableComparator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/ComparableComparator.java
rename to hibernate-core/src/main/java/org/hibernate/util/ComparableComparator.java
diff --git a/core/src/main/java/org/hibernate/util/ConfigHelper.java b/hibernate-core/src/main/java/org/hibernate/util/ConfigHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/ConfigHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/ConfigHelper.java
diff --git a/core/src/main/java/org/hibernate/util/DTDEntityResolver.java b/hibernate-core/src/main/java/org/hibernate/util/DTDEntityResolver.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/DTDEntityResolver.java
rename to hibernate-core/src/main/java/org/hibernate/util/DTDEntityResolver.java
diff --git a/core/src/main/java/org/hibernate/util/EmptyIterator.java b/hibernate-core/src/main/java/org/hibernate/util/EmptyIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/EmptyIterator.java
rename to hibernate-core/src/main/java/org/hibernate/util/EmptyIterator.java
diff --git a/core/src/main/java/org/hibernate/util/EqualsHelper.java b/hibernate-core/src/main/java/org/hibernate/util/EqualsHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/EqualsHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/EqualsHelper.java
diff --git a/core/src/main/java/org/hibernate/util/ExternalSessionFactoryConfig.java b/hibernate-core/src/main/java/org/hibernate/util/ExternalSessionFactoryConfig.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/ExternalSessionFactoryConfig.java
rename to hibernate-core/src/main/java/org/hibernate/util/ExternalSessionFactoryConfig.java
diff --git a/core/src/main/java/org/hibernate/util/FilterHelper.java b/hibernate-core/src/main/java/org/hibernate/util/FilterHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/FilterHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/FilterHelper.java
diff --git a/core/src/main/java/org/hibernate/util/IdentityMap.java b/hibernate-core/src/main/java/org/hibernate/util/IdentityMap.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/IdentityMap.java
rename to hibernate-core/src/main/java/org/hibernate/util/IdentityMap.java
diff --git a/core/src/main/java/org/hibernate/util/IdentitySet.java b/hibernate-core/src/main/java/org/hibernate/util/IdentitySet.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/IdentitySet.java
rename to hibernate-core/src/main/java/org/hibernate/util/IdentitySet.java
diff --git a/core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java b/hibernate-core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
rename to hibernate-core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
diff --git a/core/src/main/java/org/hibernate/util/JTAHelper.java b/hibernate-core/src/main/java/org/hibernate/util/JTAHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/JTAHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/JTAHelper.java
diff --git a/core/src/main/java/org/hibernate/util/JoinedIterator.java b/hibernate-core/src/main/java/org/hibernate/util/JoinedIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/JoinedIterator.java
rename to hibernate-core/src/main/java/org/hibernate/util/JoinedIterator.java
diff --git a/core/src/main/java/org/hibernate/util/LRUMap.java b/hibernate-core/src/main/java/org/hibernate/util/LRUMap.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/LRUMap.java
rename to hibernate-core/src/main/java/org/hibernate/util/LRUMap.java
diff --git a/core/src/main/java/org/hibernate/util/LazyIterator.java b/hibernate-core/src/main/java/org/hibernate/util/LazyIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/LazyIterator.java
rename to hibernate-core/src/main/java/org/hibernate/util/LazyIterator.java
diff --git a/core/src/main/java/org/hibernate/util/MarkerObject.java b/hibernate-core/src/main/java/org/hibernate/util/MarkerObject.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/MarkerObject.java
rename to hibernate-core/src/main/java/org/hibernate/util/MarkerObject.java
diff --git a/core/src/main/java/org/hibernate/util/ReflectHelper.java b/hibernate-core/src/main/java/org/hibernate/util/ReflectHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/ReflectHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/ReflectHelper.java
diff --git a/core/src/main/java/org/hibernate/util/SerializationHelper.java b/hibernate-core/src/main/java/org/hibernate/util/SerializationHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/SerializationHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/SerializationHelper.java
diff --git a/core/src/main/java/org/hibernate/util/SimpleMRUCache.java b/hibernate-core/src/main/java/org/hibernate/util/SimpleMRUCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/SimpleMRUCache.java
rename to hibernate-core/src/main/java/org/hibernate/util/SimpleMRUCache.java
diff --git a/core/src/main/java/org/hibernate/util/SingletonIterator.java b/hibernate-core/src/main/java/org/hibernate/util/SingletonIterator.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/SingletonIterator.java
rename to hibernate-core/src/main/java/org/hibernate/util/SingletonIterator.java
diff --git a/core/src/main/java/org/hibernate/util/SoftLimitMRUCache.java b/hibernate-core/src/main/java/org/hibernate/util/SoftLimitMRUCache.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/SoftLimitMRUCache.java
rename to hibernate-core/src/main/java/org/hibernate/util/SoftLimitMRUCache.java
diff --git a/core/src/main/java/org/hibernate/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/StringHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
diff --git a/core/src/main/java/org/hibernate/util/XMLHelper.java b/hibernate-core/src/main/java/org/hibernate/util/XMLHelper.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/XMLHelper.java
rename to hibernate-core/src/main/java/org/hibernate/util/XMLHelper.java
diff --git a/core/src/main/java/org/hibernate/util/package.html b/hibernate-core/src/main/java/org/hibernate/util/package.html
similarity index 100%
rename from core/src/main/java/org/hibernate/util/package.html
rename to hibernate-core/src/main/java/org/hibernate/util/package.html
diff --git a/core/src/main/java/org/hibernate/util/xml/ErrorLogger.java b/hibernate-core/src/main/java/org/hibernate/util/xml/ErrorLogger.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/ErrorLogger.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/ErrorLogger.java
diff --git a/core/src/main/java/org/hibernate/util/xml/MappingReader.java b/hibernate-core/src/main/java/org/hibernate/util/xml/MappingReader.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/MappingReader.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/MappingReader.java
diff --git a/core/src/main/java/org/hibernate/util/xml/Origin.java b/hibernate-core/src/main/java/org/hibernate/util/xml/Origin.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/Origin.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/Origin.java
diff --git a/core/src/main/java/org/hibernate/util/xml/OriginImpl.java b/hibernate-core/src/main/java/org/hibernate/util/xml/OriginImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/OriginImpl.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/OriginImpl.java
diff --git a/core/src/main/java/org/hibernate/util/xml/XmlDocument.java b/hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocument.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/XmlDocument.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocument.java
diff --git a/core/src/main/java/org/hibernate/util/xml/XmlDocumentImpl.java b/hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocumentImpl.java
similarity index 100%
rename from core/src/main/java/org/hibernate/util/xml/XmlDocumentImpl.java
rename to hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocumentImpl.java
diff --git a/core/src/main/javadoc/images/bkg_blkheader.png b/hibernate-core/src/main/javadoc/images/bkg_blkheader.png
similarity index 100%
rename from core/src/main/javadoc/images/bkg_blkheader.png
rename to hibernate-core/src/main/javadoc/images/bkg_blkheader.png
diff --git a/core/src/main/javadoc/images/bkg_gradient.gif b/hibernate-core/src/main/javadoc/images/bkg_gradient.gif
similarity index 100%
rename from core/src/main/javadoc/images/bkg_gradient.gif
rename to hibernate-core/src/main/javadoc/images/bkg_gradient.gif
diff --git a/core/src/main/javadoc/images/bkgheader.png b/hibernate-core/src/main/javadoc/images/bkgheader.png
similarity index 100%
rename from core/src/main/javadoc/images/bkgheader.png
rename to hibernate-core/src/main/javadoc/images/bkgheader.png
diff --git a/core/src/main/javadoc/images/h1_hdr.png b/hibernate-core/src/main/javadoc/images/h1_hdr.png
similarity index 100%
rename from core/src/main/javadoc/images/h1_hdr.png
rename to hibernate-core/src/main/javadoc/images/h1_hdr.png
diff --git a/core/src/main/javadoc/package.html b/hibernate-core/src/main/javadoc/package.html
similarity index 100%
rename from core/src/main/javadoc/package.html
rename to hibernate-core/src/main/javadoc/package.html
diff --git a/core/src/main/javadoc/stylesheet.css b/hibernate-core/src/main/javadoc/stylesheet.css
similarity index 100%
rename from core/src/main/javadoc/stylesheet.css
rename to hibernate-core/src/main/javadoc/stylesheet.css
diff --git a/core/src/main/resources/org/hibernate/checkstyle_checks.xml b/hibernate-core/src/main/resources/org/hibernate/checkstyle_checks.xml
similarity index 100%
rename from core/src/main/resources/org/hibernate/checkstyle_checks.xml
rename to hibernate-core/src/main/resources/org/hibernate/checkstyle_checks.xml
diff --git a/core/src/main/resources/org/hibernate/ejb/orm_1_0.xsd b/hibernate-core/src/main/resources/org/hibernate/ejb/orm_1_0.xsd
similarity index 100%
rename from core/src/main/resources/org/hibernate/ejb/orm_1_0.xsd
rename to hibernate-core/src/main/resources/org/hibernate/ejb/orm_1_0.xsd
diff --git a/core/src/main/resources/org/hibernate/ejb/orm_2_0.xsd b/hibernate-core/src/main/resources/org/hibernate/ejb/orm_2_0.xsd
similarity index 100%
rename from core/src/main/resources/org/hibernate/ejb/orm_2_0.xsd
rename to hibernate-core/src/main/resources/org/hibernate/ejb/orm_2_0.xsd
diff --git a/core/src/main/resources/org/hibernate/hibernate-configuration-3.0.dtd b/hibernate-core/src/main/resources/org/hibernate/hibernate-configuration-3.0.dtd
similarity index 100%
rename from core/src/main/resources/org/hibernate/hibernate-configuration-3.0.dtd
rename to hibernate-core/src/main/resources/org/hibernate/hibernate-configuration-3.0.dtd
diff --git a/core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
similarity index 100%
rename from core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
rename to hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
diff --git a/core/src/test/java/org/hibernate/TestingDatabaseInfo.java b/hibernate-core/src/test/java/org/hibernate/TestingDatabaseInfo.java
similarity index 100%
rename from core/src/test/java/org/hibernate/TestingDatabaseInfo.java
rename to hibernate-core/src/test/java/org/hibernate/TestingDatabaseInfo.java
diff --git a/core/src/test/java/org/hibernate/cache/QueryKeyTest.java b/hibernate-core/src/test/java/org/hibernate/cache/QueryKeyTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/cache/QueryKeyTest.java
rename to hibernate-core/src/test/java/org/hibernate/cache/QueryKeyTest.java
diff --git a/core/src/test/java/org/hibernate/connection/PropertiesTest.java b/hibernate-core/src/test/java/org/hibernate/connection/PropertiesTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/connection/PropertiesTest.java
rename to hibernate-core/src/test/java/org/hibernate/connection/PropertiesTest.java
diff --git a/core/src/test/java/org/hibernate/dialect/DerbyDialectTestCase.java b/hibernate-core/src/test/java/org/hibernate/dialect/DerbyDialectTestCase.java
similarity index 100%
rename from core/src/test/java/org/hibernate/dialect/DerbyDialectTestCase.java
rename to hibernate-core/src/test/java/org/hibernate/dialect/DerbyDialectTestCase.java
diff --git a/core/src/test/java/org/hibernate/dialect/Mocks.java b/hibernate-core/src/test/java/org/hibernate/dialect/Mocks.java
similarity index 100%
rename from core/src/test/java/org/hibernate/dialect/Mocks.java
rename to hibernate-core/src/test/java/org/hibernate/dialect/Mocks.java
diff --git a/core/src/test/java/org/hibernate/dialect/TestingDialects.java b/hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java
similarity index 100%
rename from core/src/test/java/org/hibernate/dialect/TestingDialects.java
rename to hibernate-core/src/test/java/org/hibernate/dialect/TestingDialects.java
diff --git a/core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
rename to hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectFactoryTest.java
diff --git a/core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java b/hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
rename to hibernate-core/src/test/java/org/hibernate/dialect/resolver/DialectResolverTest.java
diff --git a/core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java b/hibernate-core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java
rename to hibernate-core/src/test/java/org/hibernate/engine/query/ParameterParserTest.java
diff --git a/core/src/test/java/org/hibernate/id/AbstractHolderTest.java b/hibernate-core/src/test/java/org/hibernate/id/AbstractHolderTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/AbstractHolderTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/AbstractHolderTest.java
diff --git a/core/src/test/java/org/hibernate/id/BigDecimalHolderTest.java b/hibernate-core/src/test/java/org/hibernate/id/BigDecimalHolderTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/BigDecimalHolderTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/BigDecimalHolderTest.java
diff --git a/core/src/test/java/org/hibernate/id/BigIntegerHolderTest.java b/hibernate-core/src/test/java/org/hibernate/id/BigIntegerHolderTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/BigIntegerHolderTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/BigIntegerHolderTest.java
diff --git a/core/src/test/java/org/hibernate/id/LongHolderTest.java b/hibernate-core/src/test/java/org/hibernate/id/LongHolderTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/LongHolderTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/LongHolderTest.java
diff --git a/core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
diff --git a/core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
diff --git a/core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
diff --git a/core/src/test/java/org/hibernate/id/enhanced/OptimizerUnitTest.java b/hibernate-core/src/test/java/org/hibernate/id/enhanced/OptimizerUnitTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/enhanced/OptimizerUnitTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/enhanced/OptimizerUnitTest.java
diff --git a/core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
diff --git a/core/src/test/java/org/hibernate/id/uuid/CustomVersionOneStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/id/uuid/CustomVersionOneStrategyTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/id/uuid/CustomVersionOneStrategyTest.java
rename to hibernate-core/src/test/java/org/hibernate/id/uuid/CustomVersionOneStrategyTest.java
diff --git a/core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java b/hibernate-core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java
rename to hibernate-core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java
diff --git a/core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java b/hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
rename to hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
diff --git a/core/src/test/java/org/hibernate/jmx/Entity.hbm.xml b/hibernate-core/src/test/java/org/hibernate/jmx/Entity.hbm.xml
similarity index 100%
rename from core/src/test/java/org/hibernate/jmx/Entity.hbm.xml
rename to hibernate-core/src/test/java/org/hibernate/jmx/Entity.hbm.xml
diff --git a/core/src/test/java/org/hibernate/jmx/Entity.java b/hibernate-core/src/test/java/org/hibernate/jmx/Entity.java
similarity index 100%
rename from core/src/test/java/org/hibernate/jmx/Entity.java
rename to hibernate-core/src/test/java/org/hibernate/jmx/Entity.java
diff --git a/core/src/test/java/org/hibernate/jmx/TrivialTest.java b/hibernate-core/src/test/java/org/hibernate/jmx/TrivialTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/jmx/TrivialTest.java
rename to hibernate-core/src/test/java/org/hibernate/jmx/TrivialTest.java
diff --git a/core/src/test/java/org/hibernate/property/BasicPropertyAccessorTest.java b/hibernate-core/src/test/java/org/hibernate/property/BasicPropertyAccessorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/property/BasicPropertyAccessorTest.java
rename to hibernate-core/src/test/java/org/hibernate/property/BasicPropertyAccessorTest.java
diff --git a/core/src/test/java/org/hibernate/sql/TemplateTest.java b/hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/sql/TemplateTest.java
rename to hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java
diff --git a/core/src/test/java/org/hibernate/subclassProxyInterface/Doctor.java b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Doctor.java
similarity index 100%
rename from core/src/test/java/org/hibernate/subclassProxyInterface/Doctor.java
rename to hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Doctor.java
diff --git a/core/src/test/java/org/hibernate/subclassProxyInterface/IDoctor.java b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/IDoctor.java
similarity index 100%
rename from core/src/test/java/org/hibernate/subclassProxyInterface/IDoctor.java
rename to hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/IDoctor.java
diff --git a/core/src/test/java/org/hibernate/subclassProxyInterface/Person.hbm.xml b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Person.hbm.xml
similarity index 100%
rename from core/src/test/java/org/hibernate/subclassProxyInterface/Person.hbm.xml
rename to hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Person.hbm.xml
diff --git a/core/src/test/java/org/hibernate/subclassProxyInterface/Person.java b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Person.java
similarity index 100%
rename from core/src/test/java/org/hibernate/subclassProxyInterface/Person.java
rename to hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/Person.java
diff --git a/core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
rename to hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
diff --git a/core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java b/hibernate-core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/BasicTypeRegistryTest.java
diff --git a/core/src/test/java/org/hibernate/type/TypeTest.java b/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/TypeTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/AbstractDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/AbstractDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/AbstractDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/AbstractDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/BigDecimalDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BigDecimalDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/BigDecimalDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BigDecimalDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/BigIntegerDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BigIntegerDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/BigIntegerDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BigIntegerDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/BlobDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BlobDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/BlobDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BlobDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/BooleanDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BooleanDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/BooleanDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/BooleanDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/java/StringDescriptorTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/java/StringDescriptorTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/java/StringDescriptorTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/java/StringDescriptorTest.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/sql/PreparedStatementProxy.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/PreparedStatementProxy.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/sql/PreparedStatementProxy.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/PreparedStatementProxy.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/sql/ResultSetProxy.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/ResultSetProxy.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/sql/ResultSetProxy.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/ResultSetProxy.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/sql/StringClobImpl.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/StringClobImpl.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/sql/StringClobImpl.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/StringClobImpl.java
diff --git a/core/src/test/java/org/hibernate/type/descriptor/sql/StringValueMappingTest.java b/hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/StringValueMappingTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/type/descriptor/sql/StringValueMappingTest.java
rename to hibernate-core/src/test/java/org/hibernate/type/descriptor/sql/StringValueMappingTest.java
diff --git a/core/src/test/java/org/hibernate/util/SerializableThing.java b/hibernate-core/src/test/java/org/hibernate/util/SerializableThing.java
similarity index 100%
rename from core/src/test/java/org/hibernate/util/SerializableThing.java
rename to hibernate-core/src/test/java/org/hibernate/util/SerializableThing.java
diff --git a/core/src/test/java/org/hibernate/util/SerializationHelperTest.java b/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/util/SerializationHelperTest.java
rename to hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
diff --git a/core/src/test/java/org/hibernate/util/StringHelperTest.java b/hibernate-core/src/test/java/org/hibernate/util/StringHelperTest.java
similarity index 100%
rename from core/src/test/java/org/hibernate/util/StringHelperTest.java
rename to hibernate-core/src/test/java/org/hibernate/util/StringHelperTest.java
diff --git a/settings.gradle b/settings.gradle
new file mode 100644
index 0000000000..66809454fa
--- /dev/null
+++ b/settings.gradle
@@ -0,0 +1,8 @@
+include 'hibernate-core'
+
+
+rootProject.children.each { project ->
+    project.buildFileName = "${project.name}.gradle"
+    assert project.projectDir.isDirectory()
+    assert project.buildFile.isFile()
+}
\ No newline at end of file
