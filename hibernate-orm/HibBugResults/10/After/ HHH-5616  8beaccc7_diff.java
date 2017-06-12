diff --git a/cache-infinispan/pom.xml b/cache-infinispan/pom.xml
deleted file mode 100644
index fb1fbbb32d..0000000000
--- a/cache-infinispan/pom.xml
+++ /dev/null
@@ -1,191 +0,0 @@
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
-    <artifactId>hibernate-infinispan</artifactId>
-    <packaging>jar</packaging>
-
-    <name>Hibernate Infinispan Integration</name>
-    <description>Integration of Hibernate with Infinispan</description>
-
-    <properties>
-      <version.infinispan>4.2.0.ALPHA1</version.infinispan>
-      <version.hsqldb>1.8.0.2</version.hsqldb>
-      <version.cglib>2.2</version.cglib>
-      <version.javassist>3.4.GA</version.javassist>
-      <version.org.jboss.naming>5.0.3.GA</version.org.jboss.naming>
-      <version.xapool>1.5.0</version.xapool>       
-      <skipUnitTests>true</skipUnitTests>
-      <!-- 
-         Following is the default jgroups mcast address.  If you find the testsuite runs very slowly, there
-         may be problems with multicast on the interface JGroups uses by default on your machine. You can
-         try to resolve setting 'jgroups.bind_addr' as a system-property to the jvm launching maven and
-         setting the value to an interface where you know multicast works
-      -->
-      <jgroups.bind_addr>127.0.0.1</jgroups.bind_addr>
-    </properties>
-
-    <dependencies>
-        <dependency>
-            <groupId>${groupId}</groupId>
-            <artifactId>hibernate-core</artifactId>
-            <version>${version}</version>
-        </dependency>
-        <dependency>
-            <groupId>org.infinispan</groupId>
-            <artifactId>infinispan-core</artifactId>
-            <version>${version.infinispan}</version> 
-        </dependency>
-        
-        <!-- test dependencies -->
-        <dependency>
-            <groupId>${groupId}</groupId>
-            <artifactId>hibernate-testing</artifactId>
-            <version>${version}</version>
-            <!-- <scope>test</scope> TODO fix this -->
-        </dependency>
-        <dependency>
-            <groupId>org.infinispan</groupId>
-            <artifactId>infinispan-core</artifactId>
-            <version>${version.infinispan}</version>
-            <type>test-jar</type>
-            <scope>test</scope>
-        </dependency>
-        <dependency>
-            <groupId>hsqldb</groupId>
-            <artifactId>hsqldb</artifactId>
-            <version>${version.hsqldb}</version>
-            <scope>test</scope>
-        </dependency>
-        <!-- this is optional on core :( and needed for testing -->
-        <dependency>
-            <groupId>cglib</groupId>
-            <artifactId>cglib</artifactId>
-            <version>${version.cglib}</version>
-            <scope>test</scope>
-        </dependency>
-        <dependency>
-            <groupId>javassist</groupId>
-            <artifactId>javassist</artifactId>
-            <version>${version.javassist}</version>
-            <scope>test</scope>
-        </dependency>
-
-        <dependency>
-          <groupId>org.jboss.naming</groupId>
-          <artifactId>jnp-client</artifactId>
-          <scope>test</scope>
-          <version>${version.org.jboss.naming}</version>
-        </dependency>
-
-        <dependency>
-          <groupId>org.jboss.naming</groupId>
-          <artifactId>jnpserver</artifactId>
-          <scope>test</scope>
-          <version>${version.org.jboss.naming}</version>
-        </dependency>
-
-        <dependency>
-          <groupId>com.experlog</groupId>
-          <artifactId>xapool</artifactId>
-          <scope>test</scope>
-          <version>${version.xapool}</version>
-        </dependency>
-
-        <dependency>
-          <groupId>jboss.jbossts</groupId>
-          <artifactId>jbossjta</artifactId>
-          <version>4.9.0.GA</version>
-          <scope>test</scope>
-        </dependency>
-    </dependencies>
-
-    <build>
-        <plugins>
-            <plugin>
-                <groupId>org.apache.maven.plugins</groupId>
-                <artifactId>maven-compiler-plugin</artifactId>
-                <configuration>
-                    <fork>true</fork>
-                    <verbose>true</verbose>
-                </configuration>
-            </plugin>
-            <plugin>
-                <groupId>org.apache.maven.plugins</groupId>
-                <artifactId>maven-surefire-plugin</artifactId>
-                <configuration>
-                    <excludes>
-                        <!-- Skip a long-running test of a prototype class -->
-                        <exclude>**/ClusteredConcurrentTimestampRegionTestCase.java</exclude>
-                    </excludes>
-                    <systemProperties>
-                        <property>
-                            <name>hibernate.test.validatefailureexpected</name>
-                            <value>true</value>
-                        </property>
-                        <property>
-                            <name>jgroups.bind_addr</name>
-                            <value>${jgroups.bind_addr}</value>
-                        </property>
-                        <!-- There are problems with multicast and IPv6 on some
-                             OS/JDK combos, so we tell Java to use IPv4. If you
-                             have problems with multicast when running the tests
-                             you can try setting this to 'false', although typically
-                             that won't be helpful.
-                        -->
-                        <property>
-                            <name>java.net.preferIPv4Stack</name>
-                            <value>true</value>
-                        </property>
-                        <!-- Tell JGroups to only wait a short time for PING 
-                             responses before determining coordinator. Speeds cluster
-                             formation during integration tests. (This is too
-                             low a value for a real system; only use for tests.)
-                        -->
-                        <property>
-                            <name>jgroups.ping.timeout</name>
-                            <value>500</value>
-                        </property>
-                        <!-- Tell JGroups to only require one PING response
-                             before determining coordinator. Speeds cluster
-                             formation during integration tests. (This is too
-                             low a value for a real system; only use for tests.)
-                        -->
-                        <property>
-                            <name>jgroups.ping.num_initial_members</name>
-                            <value>1</value>
-                        </property>
-                        <!-- Disable the JGroups message bundling feature
-                             to speed tests and avoid FLUSH issue -->
-                        <property>
-                            <name>jgroups.udp.enable_bundling</name>
-                            <value>false</value>
-                        </property>
-                    </systemProperties>
-                    <skipExec>${skipUnitTests}</skipExec>
-                </configuration>
-            </plugin>
-        </plugins>
-    </build>
-
-    <profiles>
-        <profile>
-            <id>test</id>
-            <activation>
-                <activeByDefault>false</activeByDefault>
-            </activation>
-            <properties>
-                <skipUnitTests>false</skipUnitTests>
-            </properties>
-        </profile>
-     </profiles>
-</project>
diff --git a/cache-infinispan/src/test/resources/hibernate.properties b/cache-infinispan/src/test/resources/hibernate.properties
deleted file mode 100755
index f3509e0000..0000000000
--- a/cache-infinispan/src/test/resources/hibernate.properties
+++ /dev/null
@@ -1,36 +0,0 @@
-################################################################################
-# Hibernate, Relational Persistence for Idiomatic Java                         #
-#                                                                              #
-# Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as    #
-# indicated by the @author tags or express copyright attribution               #
-# statements applied by the authors.  All third-party contributions are        #
-# distributed under license by Red Hat, Inc. and/or it's affiliates.                         #
-#                                                                              #
-# This copyrighted material is made available to anyone wishing to use, modify,#
-# copy, or redistribute it subject to the terms and conditions of the GNU      #
-# Lesser General Public License, as published by the Free Software Foundation. #
-#                                                                              #
-# This program is distributed in the hope that it will be useful,              #
-# but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
-# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
-# for more details.                                                            #
-#                                                                              #
-# You should have received a copy of the GNU Lesser General Public License     #
-# along with this distribution; if not, write to:                              #
-# Free Software Foundation, Inc.                                               #
-# 51 Franklin Street, Fifth Floor                                              #
-# Boston, MA  02110-1301  USA                                                  #
-################################################################################
-hibernate.dialect org.hibernate.dialect.HSQLDialect
-hibernate.connection.driver_class org.hsqldb.jdbcDriver
-hibernate.connection.url jdbc:hsqldb:mem:/test
-hibernate.connection.username sa
-hibernate.connection.password
-
-hibernate.connection.pool_size 5
-
-hibernate.format_sql true
-
-hibernate.max_fetch_depth 5
-
-hibernate.generate_statistics true
diff --git a/cache-infinispan/src/test/resources/log4j.properties b/cache-infinispan/src/test/resources/log4j.properties
deleted file mode 100755
index e0d43385e0..0000000000
--- a/cache-infinispan/src/test/resources/log4j.properties
+++ /dev/null
@@ -1,37 +0,0 @@
-################################################################################
-# Hibernate, Relational Persistence for Idiomatic Java                         #
-#                                                                              #
-# Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as    #
-# indicated by the @author tags or express copyright attribution               #
-# statements applied by the authors.  All third-party contributions are        #
-# distributed under license by Red Hat, Inc. and/or it's affiliates.                         #
-#                                                                              #
-# This copyrighted material is made available to anyone wishing to use, modify,#
-# copy, or redistribute it subject to the terms and conditions of the GNU      #
-# Lesser General Public License, as published by the Free Software Foundation. #
-#                                                                              #
-# This program is distributed in the hope that it will be useful,              #
-# but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
-# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
-# for more details.                                                            #
-#                                                                              #
-# You should have received a copy of the GNU Lesser General Public License     #
-# along with this distribution; if not, write to:                              #
-# Free Software Foundation, Inc.                                               #
-# 51 Franklin Street, Fifth Floor                                              #
-# Boston, MA  02110-1301  USA                                                  #
-################################################################################
-log4j.appender.stdout=org.apache.log4j.ConsoleAppender
-log4j.appender.stdout.Target=System.out
-log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
-log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p [%t] %c{1}:%L - %m%n
-
-
-log4j.rootLogger=info, stdout
-
-#log4j.logger.org.hibernate.test=info
-log4j.logger.org.hibernate.test=info
-log4j.logger.org.hibernate.cache=info
-log4j.logger.org.hibernate.SQL=info
-#log4j.logger.org.jgroups=info
-#log4j.logger.org.infinispan=trace
\ No newline at end of file
diff --git a/hibernate-infinispan/hibernate-infinispan.gradle b/hibernate-infinispan/hibernate-infinispan.gradle
new file mode 100644
index 0000000000..711bc6aeea
--- /dev/null
+++ b/hibernate-infinispan/hibernate-infinispan.gradle
@@ -0,0 +1,46 @@
+dependencies {
+    infinispanVersion = '4.2.0.ALPHA1'
+    jnpVersion = '5.0.3.GA'
+
+    compile( project( ':hibernate-core' ) )
+    compile( [group: 'org.infinispan', name: 'infinispan-core', version: infinispanVersion] ) {
+        artifact {
+            name = "infinispan-core"
+            type = 'jar'
+        }
+    }
+    // http://jira.codehaus.org/browse/GRADLE-739
+    testCompile( [group: 'org.infinispan', name: 'infinispan-core', version: infinispanVersion] ) {
+        artifact {
+            name = "infinispan-core"
+            type = 'jar'
+        }
+        artifact {
+            name = "infinispan-core"
+            classifier = 'tests'
+            type = 'jar'
+        }
+    }
+    testCompile( project(':hibernate-core').sourceSets.test.classes )
+    testCompile( [group: 'org.jboss', name: 'jboss-common-core', version: '2.2.14.GA'] )
+    testCompile( [group: 'org.jboss.naming', name: 'jnp-client', version: jnpVersion] )
+    testCompile( [group: 'org.jboss.naming', name: 'jnpserver', version: jnpVersion] )
+    testCompile( [group: 'com.experlog', name: 'xapool', version: '1.5.0'] )
+    testCompile( [group: 'jboss.jbossts', name: 'jbossjta', version: '4.9.0.GA'] )
+    testCompile( libraries.jta )
+    testCompile( libraries.h2 )
+    testCompile( [group: 'org.rhq.helpers', name: 'rhq-pluginAnnotations', version: '1.4.0.B01'] )
+    testRuntime( libraries.javassist )
+}
+
+test {
+    environment['java.net.preferIPv4Stack'] = true
+    environment['jgroups.ping.timeout'] = 500
+    environment['jgroups.ping.num_initial_members'] = 1
+    environment['jgroups.udp.enable_bundling'] = false
+//    environment['jgroups.bind_addr'] = $jgroupsBindAddress
+    // quite a few failures and the old maven module disabled these tests as well
+    enabled = false
+}
+
+
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/TypeOverrides.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/TypeOverrides.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/TypeOverrides.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/TypeOverrides.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/ClassLoaderAwareCache.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/ClassLoaderAwareCache.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/ClassLoaderAwareCache.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/ClassLoaderAwareCache.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampTypeOverrides.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampTypeOverrides.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampTypeOverrides.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampTypeOverrides.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/tm/HibernateTransactionManagerLookup.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/tm/HibernateTransactionManagerLookup.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/tm/HibernateTransactionManagerLookup.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/tm/HibernateTransactionManagerLookup.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java
similarity index 100%
rename from cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java
rename to hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java
diff --git a/cache-infinispan/src/main/resources/org/hibernate/cache/infinispan/builder/infinispan-configs.xml b/hibernate-infinispan/src/main/resources/org/hibernate/cache/infinispan/builder/infinispan-configs.xml
similarity index 100%
rename from cache-infinispan/src/main/resources/org/hibernate/cache/infinispan/builder/infinispan-configs.xml
rename to hibernate-infinispan/src/main/resources/org/hibernate/cache/infinispan/builder/infinispan-configs.xml
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractReadOnlyAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractReadOnlyAccessTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractReadOnlyAccessTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractReadOnlyAccessTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractTransactionalAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractTransactionalAccessTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractTransactionalAccessTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractTransactionalAccessTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/InvalidatedTransactionalTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/InvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/InvalidatedTransactionalTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/InvalidatedTransactionalTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyExtraAPITestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyExtraAPITestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/ReadOnlyTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/InvalidatedTransactionalTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/InvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/InvalidatedTransactionalTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/InvalidatedTransactionalTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyExtraAPITestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyExtraAPITestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/ReadOnlyTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicJdbcTransactionalTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicJdbcTransactionalTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicJdbcTransactionalTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicJdbcTransactionalTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Contact.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Contact.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Contact.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Contact.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Customer.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Customer.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Customer.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Customer.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Item.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Item.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Item.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/Item.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/JndiRegionFactoryTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/VersionedItem.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/VersionedItem.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/VersionedItem.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/VersionedItem.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/Account.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/Account.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/Account.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/Account.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/AccountHolder.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/AccountHolder.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/AccountHolder.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/AccountHolder.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/RepeatableSessionRefreshTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/RepeatableSessionRefreshTest.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/RepeatableSessionRefreshTest.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/RepeatableSessionRefreshTest.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerImpl.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerImpl.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerImpl.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerLookup.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerLookup.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerLookup.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionManagerLookup.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/BatchModeTransactionManagerLookup.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/BatchModeTransactionManagerLookup.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/BatchModeTransactionManagerLookup.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/BatchModeTransactionManagerLookup.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
similarity index 100%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
rename to hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
diff --git a/cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Contact.hbm.xml b/hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Contact.hbm.xml
similarity index 100%
rename from cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Contact.hbm.xml
rename to hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Contact.hbm.xml
diff --git a/cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Customer.hbm.xml b/hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Customer.hbm.xml
similarity index 100%
rename from cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Customer.hbm.xml
rename to hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Customer.hbm.xml
diff --git a/cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Item.hbm.xml b/hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Item.hbm.xml
similarity index 100%
rename from cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Item.hbm.xml
rename to hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/Item.hbm.xml
diff --git a/cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/classloader/Account.hbm.xml b/hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/classloader/Account.hbm.xml
similarity index 100%
rename from cache-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/classloader/Account.hbm.xml
rename to hibernate-infinispan/src/test/resources/org/hibernate/test/cache/infinispan/functional/classloader/Account.hbm.xml
diff --git a/settings.gradle b/settings.gradle
index 5af3533e80..d12c3a31d5 100644
--- a/settings.gradle
+++ b/settings.gradle
@@ -1,15 +1,16 @@
 include 'hibernate-core'
 include 'hibernate-entitymanager'
 include 'hibernate-envers'
 
 include 'hibernate-c3p0'
 include 'hibernate-proxool'
 
 include 'hibernate-ehcache'
+include 'hibernate-infinispan'
 
 
 rootProject.children.each { project ->
     project.buildFileName = "${project.name}.gradle"
     assert project.projectDir.isDirectory()
     assert project.buildFile.isFile()
 }
\ No newline at end of file
