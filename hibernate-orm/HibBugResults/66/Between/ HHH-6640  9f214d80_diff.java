diff --git a/documentation/src/main/docbook/devguide/en-US/Services.xml b/documentation/src/main/docbook/devguide/en-US/Services.xml
index 3120cfe0af..92deeb0c03 100644
--- a/documentation/src/main/docbook/devguide/en-US/Services.xml
+++ b/documentation/src/main/docbook/devguide/en-US/Services.xml
@@ -1,913 +1,913 @@
 <?xml version='1.0' encoding='utf-8' ?>
 <!DOCTYPE chapter PUBLIC "-//OASIS//DTD DocBook XML V4.5//EN" "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd" [
 <!ENTITY % BOOK_ENTITIES SYSTEM "Hibernate_Development_Guide.ent">
 %BOOK_ENTITIES;
 ]>
 
 <chapter>
     <title>Services</title>
 
     <section>
         <title>What are services?</title>
         <para>
             Services are classes that provide Hibernate with pluggable implementations of various types of
             functionality.  Specifically they are implementations of certain service contract interfaces.  The interface
             is known as the service role; the implementation class is know as the service implementation.  Generally
             speaking, users can plug in alternate implementations of all standard service roles (overriding); they can
             also define additional services beyond the base set of service roles (extending).
         </para>
     </section>
 
     <section>
         <title>Service contracts</title>
         <para>
             The basic requirement for a service is to implement the marker interface
             <interfacename>org.hibernate.service.Service</interfacename>.  Hibernate uses this internally for some
             basic type safety.
         </para>
         <para>
             Optionally, the service can also implement the
             <interfacename>org.hibernate.service.spi.Startable</interfacename> and
             <interfacename>org.hibernate.service.spi.Stoppable</interfacename> interfaces to receive notifications
             of when they are becoming active and becoming deactivated.
         </para>
         <para>
             Another optional service contract is <interfacename>org.hibernate.service.spi.Manageable</interfacename>
             which marks the service as manageable in JMX provided the JMX integration is enabled.
         </para>
     </section>
 
     <section>
         <title>ServiceRegistry</title>
         <para>
             The central service API, aside from the services themselves, is the
             <interfacename>org.hibernate.service.ServiceRegistry</interfacename> interface, the main purpose of which
             is hold, manage and provide access to services.
         </para>
     </section>
 
     <section>
         <title>ServiceRegistryBuilder</title>
         <para>
             Building a <interfacename>org.hibernate.service.ServiceRegistry</interfacename> is the purpose of the
             <classname>org.hibernate.service.ServiceRegistryBuilder</classname>.
         </para>
     </section>
 
     <section>
         <title>Service dependencies</title>
         <para>
             Services are allowed to declare dependencies on other services using either of 2 approaches.
         </para>
         <section>
             <title>@<interfacename>org.hibernate.service.spi.InjectService</interfacename></title>
             <para>
                 Any method on the service implementation class accepting a single parameter and annotated with
                 @<interfacename>InjectService</interfacename> is considered requesting injection of another service.
             </para>
             <para>
                 By default the type of the method parameter is expected to be the service role to be injected.  If the
                 parameter type is different than the service role, the <methodname>serviceRole</methodname> attribute
                 of the <interfacename>InjectService</interfacename> should be used to explicitly name the role.
             </para>
             <para>
                 By default injected services are considered required, that is the start up will fail if a named
                 dependent service is missing.  If the service to be injected is optional, the
                 <methodname>required</methodname> attribute of the <interfacename>InjectService</interfacename>
                 should be declared as <literal>false</literal> (default is <literal>true</literal>).
             </para>
         </section>
         <section>
             <title><interfacename>org.hibernate.service.spi.ServiceRegistryAwareService</interfacename></title>
             <para>
                 The second approach is a pull approach where the service implements the optional service interface
                 <interfacename>org.hibernate.service.spi.ServiceRegistryAwareService</interfacename> which declares
                 a single <methodname>injectServices</methodname> method.  During startup, Hibernate will inject the
                 <interfacename>org.hibernate.service.ServiceRegistry</interfacename> itself into services which
                 implement this interface.  The service can then use the <interfacename>ServiceRegistry</interfacename>
                 reference to locate any additional services it needs.
             </para>
         </section>
     </section>
 
     <section>
         <title>Custom services</title>
         <para>
             Once a <interfacename>org.hibernate.service.ServiceRegistry</interfacename> is built it is considered
             immutable; the services themselves might accept re-configuration, but immutability here means
             adding/replacing services.  So another role provided by the
             <classname>org.hibernate.service.ServiceRegistryBuilder</classname> is to allow tweaking of the services
             that will be contained in the <interfacename>org.hibernate.service.ServiceRegistry</interfacename>
             generated from it.
         </para>
         <para>
             There are 2 means to tell a <classname>org.hibernate.service.ServiceRegistryBuilder</classname> about
             custom services.
         </para>
         <itemizedlist>
             <listitem>
                 <para>
                     Implement a <interfacename>org.hibernate.service.spi.BasicServiceInitiator</interfacename> class
                     to control on-demand construction of the service class and add it to the
                     <classname>org.hibernate.service.ServiceRegistryBuilder</classname> via its
                     <methodname>addInitiator</methodname> method.
                 </para>
             </listitem>
             <listitem>
                 <para>
                     Just instantiate the service class and add it to the
                     <classname>org.hibernate.service.ServiceRegistryBuilder</classname> via its
                     <methodname>addService</methodname> method.
                 </para>
             </listitem>
         </itemizedlist>
         <para>
             Either approach the adding a service approach or the adding an initiator approach are valid for extending a
             registry (adding new service roles) and overriding services (replacing service implementations).
         </para>
     </section>
 
     <section>
         <title>Standard services</title>
 
         <section id="services-BatchBuilder">
             <title><interfacename>org.hibernate.engine.jdbc.batch.spi.BatchBuilder</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Defines strategy for how Hibernate manages JDBC statement batching
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
-        <section id="services-ConfigurationService">
-            <title><interfacename>org.hibernate.service.config.spi.ConfigurationService</interfacename></title>
-            <variablelist>
-                <varlistentry>
-                    <term>Notes</term>
-                    <listitem>
-                        <para>
-                            Provides access to the initial raw, user-provided configuration values
-                        </para>
-                    </listitem>
-                </varlistentry>
-                <varlistentry>
-                    <term>Initiator</term>
-                    <listitem>
-                        <para>
-                            <classname>org.hibernate.service.config.internal.ConfigurationServiceInitiator</classname>
-                        </para>
-                    </listitem>
-                </varlistentry>
-                <varlistentry>
-                    <term>Implementations</term>
-                    <listitem>
-                        <para>
-                            <classname>org.hibernate.service.config.internal.ConfigurationServiceImpl</classname>
-                        </para>
-                    </listitem>
-                </varlistentry>
-            </variablelist>
-        </section>
-
         <section id="services-ConnectionProvider">
             <title><interfacename>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Defines the means in which Hibernate can obtain and release
                             <interfacename>java.sql.Connection</interfacename> instances for its use.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <itemizedlist>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider</classname> -
                                     provides connection pooling based on integration with the C3P0 connection pooling library
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl</classname> -
                                     provides connection managed delegated to a
                                     <interfacename>javax.sql.DataSource</interfacename>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl</classname> -
                                     provides rudimentary connection pooling based on simple custom pool.  Note intended
                                     production use!
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jdbc.connections.internal.ProxoolConnectionProvider</classname> -
                                     provides connection pooling based on integration with the proxool connection pooling library
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jdbc.connections.internal.UserSuppliedConnectionProviderImpl</classname> -
                                     Provides no connection support.  Indicates the user will supply connections to Hibernate directly.
                                     Not recommended for use.
                                 </para>
                             </listitem>
                         </itemizedlist>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-DialectFactory">
             <title><interfacename>org.hibernate.service.jdbc.dialect.spi.DialectFactory</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Contract for Hibernate to obtain <classname>org.hibernate.dialect.Dialect</classname>
                             instance to use.  This is either explicitly defined by the
                             <property>hibernate.dialect</property> property or determined by the
                             <xref id="service-DialectResolver"/> service which is a delegate to this service.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jdbc.dialect.internal.DialectFactoryImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-DialectResolver">
             <title><interfacename>org.hibernate.service.jdbc.dialect.spi.DialectResolver</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Provides resolution of <classname>org.hibernate.dialect.Dialect</classname> to use based on
                             information extracted from JDBC metadata.
                         </para>
                         <para>
                             The standard resolver implementation acts as a chain, delegating to a series of individual
                             resolvers.  The standard Hibernate resolution behavior is contained in
                             <classname>org.hibernate.service.jdbc.dialect.internal.StandardDialectResolver</classname>.
                             <classname>org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator</classname>
                             also consults with the <property>hibernate.dialect_resolvers</property> setting for any
                             custom resolvers.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jdbc.dialect.internal.DialectResolverSet</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-JdbcServices">
             <title><interfacename>org.hibernate.engine.jdbc.spi.JdbcServices</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Special type of service that aggregates together a number of other services and provides
                             a higher-level set of functionality.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.engine.jdbc.internal.JdbcServicesInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.engine.jdbc.internal.JdbcServicesImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-JmxService">
             <title><interfacename>org.hibernate.service.jmx.spi.JmxService</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Provides simplified access to JMX related features needed by Hibernate.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jmx.internal.JmxServiceInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <itemizedlist>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jmx.internal.DisabledJmxServiceImpl</classname> -
                                     A no-op implementation when JMX functionality is disabled.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jmx.internal.JmxServiceImpl</classname> -
                                     Standard implementation of JMX handling
                                 </para>
                             </listitem>
                         </itemizedlist>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-JndiService">
             <title><interfacename>org.hibernate.service.jndi.spi.JndiService</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Provides simplified access to JNDI related features needed by Hibernate.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jndi.internal.JndiServiceInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jndi.internal.JndiServiceImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-JtaPlatform">
             <title><interfacename>org.hibernate.service.jta.platform.spi.JtaPlatform</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Provides an abstraction from the underlying JTA platform when JTA features are used.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.jta.platform.internal.JtaPlatformInitiator</classname>
                         </para>
                         <important>
                             <para>
                                 <classname>JtaPlatformInitiator</classname> provides mapping against the legacy,
                                 now-deprecated <interfacename>org.hibernate.transaction.TransactionManagerLookup</interfacename>
                                 names internally for the Hibernate-provided
                                 <interfacename>org.hibernate.transaction.TransactionManagerLookup</interfacename>
                                 implementations.
                             </para>
                         </important>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <itemizedlist>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.BitronixJtaPlatform</classname> -
                                     Integration with the Bitronix stand-alone transaction manager.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.BorlandEnterpriseServerJtaPlatform</classname> -
                                     Integration with the transaction manager as deployed within a Borland Enterprise Server
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform</classname> -
                                     Integration with the transaction manager as deployed within a JBoss Application Server
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform</classname> -
                                     Integration with the JBoss Transactions stand-alone transaction manager
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.JOTMJtaPlatform</classname> -
                                     Integration with the JOTM stand-alone transaction manager
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.JOnASJtaPlatform</classname> -
                                     Integration with the JOnAS transaction manager.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.JRun4JtaPlatform</classname> -
                                     Integration with the transaction manager as deployed in a JRun 4 application server.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.NoJtaPlatform</classname> -
                                     No-op version when no JTA set up is configured
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.OC4JJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in an OC4J (Oracle) application
                                     server.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.OrionJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in an Orion application server.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.ResinJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in a Resin application server.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.SunOneJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in a Sun ONE (7 and above)
                                     application server.
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.TransactionManagerLookupBridge</classname> -
                                     Provides a bridge to legacy (and deprecated)
                                     <interfacename>org.hibernate.transaction.TransactionManagerLookup</interfacename>
                                     implementations
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.WebSphereExtendedJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in a WebSphere Application Server
                                     (6 and above).
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.WebSphereJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in a WebSphere Application Server
                                     (4, 5.0 and 5.1).
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform</classname> -
                                     Integration with transaction manager as deployed in a Weblogic application server.
                                 </para>
                             </listitem>
                         </itemizedlist>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-MultiTenantConnectionProvider">
             <title><interfacename>org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             A variation of <xref linkend="services-ConnectionProvider"/> providing access to JDBC
                             connections in multi-tenant environments.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             N/A
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             Intended that users provide appropriate implementation if needed.
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-PersisterClassResolver">
             <title><interfacename>org.hibernate.persister.spi.PersisterClassResolver</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Contract for determining the appropriate
                             <interfacename>org.hibernate.persister.entity.EntityPersister</interfacename>
                             or <interfacename>org.hibernate.persister.collection.CollectionPersister</interfacename>
                             implementation class to use given an entity or collection mapping.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.persister.internal.PersisterClassResolverInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.persister.internal.StandardPersisterClassResolver</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-PersisterFactory">
             <title><interfacename>org.hibernate.persister.spi.PersisterFactory</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Factory for creating
                             <interfacename>org.hibernate.persister.entity.EntityPersister</interfacename>
                             and <interfacename>org.hibernate.persister.collection.CollectionPersister</interfacename>
                             instances.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.persister.internal.PersisterFactoryInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.persister.internal.PersisterFactoryImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-RegionFactory">
             <title><interfacename>org.hibernate.cache.spi.RegionFactory</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Integration point for Hibernate's second level cache support.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.cache.internal.RegionFactoryInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <itemizedlist>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.cache.ehcache.EhCacheRegionFactory</classname>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.cache.infinispan.InfinispanRegionFactory</classname>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.cache.infinispan.JndiInfinispanRegionFactory</classname>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.cache.internal.NoCachingRegionFactory</classname>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory</classname>
                                 </para>
                             </listitem>
                         </itemizedlist>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-SessionFactoryServiceRegistryFactory">
             <title><interfacename>org.hibernate.service.spi.SessionFactoryServiceRegistryFactory</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Factory for creating
                             <interfacename>org.hibernate.service.spi.SessionFactoryServiceRegistry</interfacename>
                             instances which acts as a specialized
                             <interfacename>org.hibernate.service.ServiceRegistry</interfacename> for
                             <interfacename>org.hibernate.SessionFactory</interfacename> scoped services.  See
                             <xref linkend="services-SessionFactoryServices"/> for more details.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-Statistics">
             <title><interfacename>org.hibernate.stat.Statistics</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Contract for exposing collected statistics.  The statistics are collected through the
                             <interfacename>org.hibernate.stat.spi.StatisticsImplementor</interfacename> contract.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.stat.internal.StatisticsInitiator</classname>
                         </para>
                         <para>
                             Defines a <property>hibernate.stats.factory</property> setting to allow
                             configuring the
                             <interfacename>org.hibernate.stat.spi.StatisticsFactory</interfacename> to use internally
                             when building the actual
                             <interfacename>org.hibernate.stat.Statistics</interfacename> instance.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.stat.internal.ConcurrentStatisticsImpl</classname>
                         </para>
                         <para>
                             The default <interfacename>org.hibernate.stat.spi.StatisticsFactory</interfacename>
                             implementation builds a
                             <classname>org.hibernate.stat.internal.ConcurrentStatisticsImpl</classname> instance.
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
         <section id="services-TransactionFactory">
             <title><interfacename>org.hibernate.engine.transaction.spi.TransactionFactory</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Strategy defining how Hibernate's <interfacename>org.hibernate.Transaction</interfacename>
                             API maps to the underlying transaction approach.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.stat.internal.StatisticsInitiator</classname>
                         </para>
                         <para>
                             Defines a <property>hibernate.stats.factory</property> setting to allow
                             configuring the
                             <interfacename>org.hibernate.stat.spi.StatisticsFactory</interfacename> to use internally
                             when building the actual
                             <interfacename>org.hibernate.stat.Statistics</interfacename> instance.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <itemizedlist>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory</classname> -
                                     A JTA-based strategy in which Hibernate is not controlling the transactions.  An
                                     important distinction here is that interaction with the underlying JTA implementation
                                     is done through the
                                     <interfacename>javax.transaction.TransactionManager</interfacename>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory</classname> -
                                     A non-JTA strategy in which the transactions are managed using the JDBC
                                     <interfacename>java.sql.Connection</interfacename>
                                 </para>
                             </listitem>
                             <listitem>
                                 <para>
                                     <classname>org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory</classname> -
                                     A JTA-based strategy in which Hibernate *may* be controlling the transactions.  An
                                     important distinction here is that interaction with the underlying JTA
                                     implementation is done through the
                                     <interfacename>javax.transaction.UserTransaction</interfacename>
                                 </para>
                             </listitem>
                         </itemizedlist>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
     </section>
 
     <section id="services-SessionFactoryServices">
         <title>SessionFactory services</title>
 
         <para>
             There are also a number of services that are specific to each
             <interfacename>org.hibernate.SessionFactory</interfacename> that are maintained as part of
             <interfacename>org.hibernate.service.spi.SessionFactoryServiceRegistry</interfacename>
         </para>
 
         <section id="service-EventListenerRegistry">
             <title><interfacename>org.hibernate.event.service.spi.EventListenerRegistry</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Service for managing event listeners.
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.event.service.internal.EventListenerServiceInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.event.service.internal.EventListenerRegistryImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
     </section>
 
     <section>
-        <title>Special services</title>
+        <title>Boot-strap services</title>
+
+        <section id="services-ConfigurationService">
+            <title><interfacename>org.hibernate.service.config.spi.ConfigurationService</interfacename></title>
+            <variablelist>
+                <varlistentry>
+                    <term>Notes</term>
+                    <listitem>
+                        <para>
+                            Provides access to the initial raw, user-provided configuration values
+                        </para>
+                    </listitem>
+                </varlistentry>
+                <varlistentry>
+                    <term>Initiator</term>
+                    <listitem>
+                        <para>
+                            N/A
+                        </para>
+                    </listitem>
+                </varlistentry>
+                <varlistentry>
+                    <term>Implementations</term>
+                    <listitem>
+                        <para>
+                            <classname>org.hibernate.service.config.internal.ConfigurationServiceImpl</classname>
+                        </para>
+                    </listitem>
+                </varlistentry>
+            </variablelist>
+        </section>
 
         <section id="services-ClassLoaderService">
         </section>
 
         <section id="services-IntegratorService">
         </section>
     </section>
 </chapter>
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index f609d6ad76..08240d6218 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -746,2001 +746,2003 @@ public class Configuration implements Serializable {
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		XClass xClass = reflectionManager.toXClass( annotatedClass );
 		metadataSourceQueue.add( xClass );
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
         LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
             LOG.unableToParseMetadata(packageName);
 			throw me;
 		}
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
         LOG.searchingForMappingDocuments(jar.getName());
 		JarFile jarFile = null;
 		try {
 			try {
 				jarFile = new JarFile( jar );
 			}
 			catch (IOException ioe) {
 				throw new InvalidMappingException(
 						"Could not read mapping documents from jar: " + jar.getName(), "jar", jar.getName(),
 						ioe
 				);
 			}
 			Enumeration jarEntries = jarFile.entries();
 			while ( jarEntries.hasMoreElements() ) {
 				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
 				if ( ze.getName().endsWith( ".hbm.xml" ) ) {
                     LOG.foundMappingDocument(ze.getName());
 					try {
 						addInputStream( jarFile.getInputStream( ze ) );
 					}
 					catch (Exception e) {
 						throw new InvalidMappingException(
 								"Could not read mapping documents from jar: " + jar.getName(),
 								"jar",
 								jar.getName(),
 								e
 						);
 					}
 				}
 			}
 		}
 		finally {
 			try {
 				if ( jarFile != null ) {
 					jarFile.close();
 				}
 			}
 			catch (IOException ioe) {
                 LOG.unableToCloseJar(ioe.getMessage());
 			}
 		}
 
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
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Create a new <tt>Mappings</tt> to add class and collection mappings to.
 	 *
 	 * @return The created mappings
 	 */
 	public Mappings createMappings() {
 		return new MappingsImpl();
 	}
 
 
 	@SuppressWarnings({ "unchecked" })
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
 		TreeMap generators = new TreeMap();
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		for ( PersistentClass pc : classes.values() ) {
 			if ( !pc.isInherited() ) {
 				IdentifierGenerator ig = pc.getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						(RootClass) pc
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 				else if ( ig instanceof IdentifierGeneratorAggregator ) {
 					( (IdentifierGeneratorAggregator) ig ).registerPersistentGenerators( generators );
 				}
 			}
 		}
 
 		for ( Collection collection : collections.values() ) {
 			if ( collection.isIdentified() ) {
 				IdentifierGenerator ig = ( ( IdentifierCollection ) collection ).getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						null
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 			}
 		}
 
 		return generators.values().iterator();
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
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		// drop them in reverse order in case db needs it done that way...
 		{
 			ListIterator itr = auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 			while ( itr.hasPrevious() ) {
 				AuxiliaryDatabaseObject object = (AuxiliaryDatabaseObject) itr.previous();
 				if ( object.appliesToDialect( dialect ) ) {
 					script.add( object.sqlDropString( dialect, defaultCatalog, defaultSchema ) );
 				}
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			Iterator itr = getTableMappings();
 			while ( itr.hasNext() ) {
 				Table table = (Table) itr.next();
 				if ( table.isPhysicalTable() ) {
 					Iterator subItr = table.getForeignKeyIterator();
 					while ( subItr.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subItr.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlDropString(
 											dialect,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 			}
 		}
 
 
 		Iterator itr = getTableMappings();
 		while ( itr.hasNext() ) {
 
 			Table table = (Table) itr.next();
 			if ( table.isPhysicalTable() ) {
 
 				/*Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
 						script.add( index.sqlDropString(dialect) );
 					}
 				}*/
 
 				script.add(
 						table.sqlDropString(
 								dialect,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 
 			}
 
 		}
 
 		itr = iterateGenerators( dialect );
 		while ( itr.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) itr.next() ).sqlDropStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		return ArrayHelper.toStringArray( script );
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
 		secondPassCompile();
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 				script.add(
 						table.sqlCreateString(
 								dialect,
 								mapping,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					Iterator subIter = table.getUniqueKeyIterator();
 					while ( subIter.hasNext() ) {
 						UniqueKey uk = (UniqueKey) subIter.next();
 						String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 						if (constraintString != null) script.add( constraintString );
 					}
 				}
 
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlCreateString(
 											dialect, mapping,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				script.add( auxiliaryDatabaseObject.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												defaultCatalog,
 												defaultSchema
 											)
 									);
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
         LOG.trace("Starting secondPassCompile() processing");
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
         LOG.debugf("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = classMapping.getTable().getQuotedName();
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = sp.getValue().getTable().getQuotedName();
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
 			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
 			while ( it.hasNext() ) {
 				final FkSecondPass pass = it.next();
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch ( RecoverableException e ) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = ( RuntimeException ) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
         LOG.debugf("Processing extends queue");
 		processExtendsQueue();
 
         LOG.debugf("Processing collection mappings");
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
         LOG.debugf("Processing native query and ResultSetMapping mappings");
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
         LOG.debugf("Processing association property references");
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
         LOG.debugf("Processing foreign key constraints");
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
         LOG.debugf("Processing extends queue");
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
 	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
                 LOG.debugf("Resolving reference to class: %s", referencedEntityName);
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
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
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
         LOG.debugf("Preparing to build session factory with filters : %s", filterDefinitions);
 
 		secondPassCompile();
         if (!metadataSourceQueue.isEmpty()) LOG.incompleteMappingMetadataCacheProcessing();
 
 		validate();
 
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder( properties ).buildServiceRegistry();
+		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder()
+				.applySettings( properties )
+				.buildServiceRegistry();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
 	 * Rterieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value curently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
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
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
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
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
             LOG.debugf("%s=%s", name, value);
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
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
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
         LOG.configuringFromResource(resource);
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
         LOG.configurationResource(resource);
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
         LOG.configuringFromUrl(url);
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
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
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
         LOG.configuringFromFile(configFile.getName());
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
                 LOG.unableToCloseInputStreamForResource(resourceName, ioe);
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
         LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
         LOG.configuredSessionFactory(name);
         LOG.debugf("Properties: %s", properties);
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named resource [%s] for mapping", name, resourceName);
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named file [%s] for mapping", name, fileName);
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName);
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named package [%s] for mapping", name, packageName);
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named class [%s] for mapping", name, className);
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
         setProperty(Environment.JACC_CONTEXTID, contextId);
         LOG.jaccContextId(contextId);
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
 	}
 
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
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
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		sqlFunctions.put( functionName, function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
 			return tables.get(key);
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
             LOG.debugf("Added %s with class %s", typeName, typeClass);
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
index 156c3355e0..022b1bceb8 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
@@ -1,68 +1,65 @@
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
 package org.hibernate.integrator.internal;
 
 import java.util.LinkedHashSet;
 import org.jboss.logging.Logger;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
-
+import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
 public class IntegratorServiceImpl implements IntegratorService {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			 CoreMessageLogger.class, IntegratorServiceImpl.class.getName());
-	private final ServiceRegistryImplementor serviceRegistry;
-	private LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
+	private static final Logger LOG = Logger.getLogger( IntegratorServiceImpl.class.getName() );
+
+	private final LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
 
-	public IntegratorServiceImpl(ServiceRegistryImplementor serviceRegistry) {
-		this.serviceRegistry = serviceRegistry;
-		// Standard integrators nameable from here.  Envers and JPA, for example, need to be handled by discovery
-		// because in separate project/jars
+	public IntegratorServiceImpl(LinkedHashSet<Integrator> providedIntegrators, ClassLoaderService classLoaderService) {
+		// register standard integrators.  Envers and JPA, for example, need to be handled by discovery because in
+		// separate project/jars.
 		addIntegrator( new BeanValidationIntegrator() );
+
+		// register provided integrators
+		for ( Integrator integrator : providedIntegrators ) {
+			addIntegrator( integrator );
+		}
+
+		for ( Integrator integrator : classLoaderService.loadJavaServices( Integrator.class ) ) {
+			addIntegrator( integrator );
+		}
 	}
 
-	@Override
-	public void addIntegrator(Integrator integrator) {
+	private void addIntegrator(Integrator integrator) {
 		LOG.debugf( "Adding Integrator [%s].", integrator.getClass().getName() );
 		integrators.add( integrator );
 	}
 
 	@Override
 	public Iterable<Integrator> getIntegrators() {
-		LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
-		integrators.addAll( this.integrators );
-		for ( Integrator integrator : ServiceLoader.load( Integrator.class,serviceRegistry ) ) {
-			LOG.debugf( "Adding Integrator [%s].", integrator.getClass().getName() );
-			integrators.add( integrator );
-		}
-
 		return integrators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/internal/ServiceLoader.java b/hibernate-core/src/main/java/org/hibernate/integrator/internal/ServiceLoader.java
deleted file mode 100644
index 16c5051ee3..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/integrator/internal/ServiceLoader.java
+++ /dev/null
@@ -1,195 +0,0 @@
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
-
-package org.hibernate.integrator.internal;
-
-import java.io.BufferedReader;
-import java.io.IOException;
-import java.io.InputStream;
-import java.io.InputStreamReader;
-import java.net.URL;
-import java.util.ArrayList;
-import java.util.Enumeration;
-import java.util.Iterator;
-import java.util.LinkedHashMap;
-import java.util.List;
-import java.util.ServiceConfigurationError;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.service.ServiceRegistry;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
-
-/**
- * @author Strong Liu
- */
-final class ServiceLoader<S> implements Iterable<S> {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class, ServiceLoader.class.getName()
-	);
-	private static final String PREFIX = "META-INF/services/";
-	private LinkedHashMap<String, S> providers = new LinkedHashMap<String, S>();
-
-	private ServiceLoader(Class<S> svc, ServiceRegistry serviceRegistry) {
-		Class<S> service = svc;
-		ClassLoaderService loader = serviceRegistry.getService( ClassLoaderService.class );
-		String fullName = PREFIX + service.getName();
-		service.getName();
-
-		List<URL> configs = locateResources( loader, fullName );
-		for ( URL url : configs ) {
-			Iterator<String> names = parse( service, url );
-			while ( names.hasNext() ) {
-				String cn = names.next();
-				try {
-					S p = service.cast( loader.classForName( cn ).newInstance() );
-					providers.put( cn, p );
-				}
-				catch ( Throwable x ) {
-					fail( service, "Provider " + cn + " could not be instantiated: " + x, x );
-				}
-			}
-
-		}
-	}
-
-	private List<URL> locateResources(ClassLoaderService loader, String fullName) {
-		List<URL> urls = new ArrayList<URL>();
-		urls.addAll( loader.locateResources( fullName ) );
-		try {
-			Enumeration<URL> hibUrls = ServiceLoader.class.getClassLoader().getResources( fullName );
-			while ( hibUrls.hasMoreElements() ) {
-				URL u = hibUrls.nextElement();
-				if ( !urls.contains( u ) ) {
-					urls.add( u );
-				}
-
-			}
-		}
-		catch ( IOException e ) {
-			//ignore
-		}
-		return urls;
-	}
-
-	private static void fail(Class service, String msg, Throwable cause)
-			throws ServiceConfigurationError {
-		throw new ServiceConfigurationError(
-				service.getName() + ": " + msg,
-				cause
-		);
-	}
-
-	private static void fail(Class service, String msg)
-			throws ServiceConfigurationError {
-		throw new ServiceConfigurationError( service.getName() + ": " + msg );
-	}
-
-	private static void fail(Class service, URL u, int line, String msg)
-			throws ServiceConfigurationError {
-		fail( service, u + ":" + line + ": " + msg );
-	}
-
-	// Parse a single line from the given configuration file, adding the name
-	// on the line to the names list.
-	//
-	private int parseLine(Class service, URL u, BufferedReader r, int lc,
-						  List<String> names)
-			throws IOException, ServiceConfigurationError {
-		String ln = r.readLine();
-		if ( ln == null ) {
-			return -1;
-		}
-		int ci = ln.indexOf( '#' );
-		if ( ci >= 0 ) {
-			ln = ln.substring( 0, ci );
-		}
-		ln = ln.trim();
-		int n = ln.length();
-		if ( n != 0 ) {
-			if ( ( ln.indexOf( ' ' ) >= 0 ) || ( ln.indexOf( '\t' ) >= 0 ) ) {
-				fail( service, u, lc, "Illegal configuration-file syntax" );
-			}
-			int cp = ln.codePointAt( 0 );
-			if ( !Character.isJavaIdentifierStart( cp ) ) {
-				fail( service, u, lc, "Illegal provider-class name: " + ln );
-			}
-			for ( int i = Character.charCount( cp ); i < n; i += Character.charCount( cp ) ) {
-				cp = ln.codePointAt( i );
-				if ( !Character.isJavaIdentifierPart( cp ) && ( cp != '.' ) ) {
-					fail( service, u, lc, "Illegal provider-class name: " + ln );
-				}
-			}
-			if ( !providers.containsKey( ln ) && !names.contains( ln ) ) {
-				names.add( ln );
-			}
-		}
-		return lc + 1;
-	}
-
-	private Iterator<String> parse(Class service, URL u)
-			throws ServiceConfigurationError {
-		InputStream in = null;
-		BufferedReader r = null;
-		ArrayList<String> names = new ArrayList<String>();
-		try {
-			in = u.openStream();
-			r = new BufferedReader( new InputStreamReader( in, "utf-8" ) );
-			int lc = 1;
-			while ( ( lc = parseLine( service, u, r, lc, names ) ) >= 0 ) {
-				;
-			}
-		}
-		catch ( IOException x ) {
-			fail( service, "Error reading configuration file", x );
-		}
-		finally {
-			try {
-				if ( r != null ) {
-					r.close();
-				}
-				if ( in != null ) {
-					in.close();
-				}
-			}
-			catch ( IOException y ) {
-				fail( service, "Error closing configuration file", y );
-			}
-		}
-		return names.iterator();
-	}
-
-	public Iterator<S> iterator() {
-		return providers.values().iterator();
-
-	}
-
-	public static <S> ServiceLoader<S> load(Class<S> service,
-											ServiceRegistry serviceRegistry) {
-		return new ServiceLoader<S>( service, serviceRegistry );
-	}
-
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
index ed02bff472..e82d00c22a 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
@@ -1,77 +1,80 @@
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
 package org.hibernate.integrator.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Contract for stuff that integrates with Hibernate.
  * <p/>
  * IMPL NOTE: called during session factory initialization (constructor), so not all parts of the passed session factory
  * will be available.
  *
  * @todo : the signature here *will* change, guaranteed
  *
  * @todo : better name ?
  *
  * @author Steve Ebersole
  * @since 4.0
  * @jira HHH-5562
  * @jira HHH-6081
  */
 public interface Integrator {
+
 	/**
 	 * Perform integration.
 	 *
 	 * @param configuration The configuration used to create the session factory
 	 * @param sessionFactory The session factory being created
 	 * @param serviceRegistry The session factory's service registry
 	 */
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry);
 
 	/**
      * Perform integration.
      *
      * @param metadata The metadata used to create the session factory
      * @param sessionFactory The session factory being created
      * @param serviceRegistry The session factory's service registry
      */
     public void integrate( MetadataImplementor metadata,
                            SessionFactoryImplementor sessionFactory,
                            SessionFactoryServiceRegistry serviceRegistry );
 
 	/**
 	 * Tongue-in-cheek name for a shutdown callback.
 	 *
 	 * @param sessionFactory The session factory being closed.
 	 * @param serviceRegistry That session factory's service registry
 	 */
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java
index 662d44558e..0c1114d209 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java
@@ -1,47 +1,38 @@
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
 package org.hibernate.integrator.spi;
 
 import org.hibernate.service.Service;
 
 /**
  * @author Steve Ebersole
  */
 public interface IntegratorService extends Service {
 	/**
-	 * Manually add an integrator.  Added integrators supplement the set of discovered ones.
-	 * <p/>
-	 * This is mostly an internal contract used between modules.
-	 *
-	 * @param integrator The integrator
-	 */
-	public void addIntegrator(Integrator integrator);
-
-	/**
 	 * Retrieve all integrators.
 	 *
 	 * @return All integrators.
 	 */
 	public Iterable<Integrator> getIntegrators();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/ServiceContributingIntegrator.java
similarity index 60%
rename from hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/integrator/spi/ServiceContributingIntegrator.java
index 30654b0a9b..94154ef952 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/ServiceContributingIntegrator.java
@@ -1,47 +1,42 @@
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
-package org.hibernate.integrator.internal;
+package org.hibernate.integrator.spi;
 
-import java.util.Map;
-
-import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.service.spi.BasicServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.ServiceRegistryBuilder;
 
 /**
+ * Additional, optional contract for Integrators that wish to contribute {@link org.hibernate.service.Service services}
+ * to the Hibernate {@link org.hibernate.service.ServiceRegistry}.
+ *
  * @author Steve Ebersole
  */
-public class IntegratorServiceInitiator implements BasicServiceInitiator<IntegratorService> {
-	public static final IntegratorServiceInitiator INSTANCE = new IntegratorServiceInitiator();
-
-	@Override
-	public Class<IntegratorService> getServiceInitiated() {
-		return IntegratorService.class;
-	}
-
-	@Override
-	public IntegratorService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-		return new IntegratorServiceImpl( registry );
-	}
+public interface ServiceContributingIntegrator extends Integrator {
+	/**
+	 * Allow the integrator to alter the builder of {@link org.hibernate.service.ServiceRegistry}, presumably to
+	 * register services into it.
+	 *
+	 * @param serviceRegistryBuilder The build to prepare.
+	 */
+	public void prepareServices(ServiceRegistryBuilder serviceRegistryBuilder);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
index 976cfb6f47..cc8205809e 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
@@ -1,200 +1,200 @@
 //$Id: HibernateService.java 6100 2005-03-17 10:48:03Z turin42 $
 package org.hibernate.jmx;
 
 import javax.naming.InitialContext;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ExternalSessionFactoryConfig;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.jndi.JndiHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 
 /**
  * Implementation of <tt>HibernateServiceMBean</tt>. Creates a
  * <tt>SessionFactory</tt> and binds it to the specified JNDI name.<br>
  * <br>
  * All mapping documents are loaded as resources by the MBean.
  * @see HibernateServiceMBean
  * @see org.hibernate.SessionFactory
  * @author John Urberg, Gavin King
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
 public class HibernateService extends ExternalSessionFactoryConfig implements HibernateServiceMBean {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HibernateService.class.getName());
 
 	private String boundName;
 	private Properties properties = new Properties();
 
 	@Override
 	public void start() throws HibernateException {
 		boundName = getJndiName();
 		try {
 			buildSessionFactory();
 		}
 		catch (HibernateException he) {
             LOG.unableToBuildSessionFactoryUsingMBeanClasspath(he.getMessage());
             LOG.debug("Error was", he);
 			new SessionFactoryStub(this);
 		}
 	}
 
 	@Override
 	public void stop() {
         LOG.stoppingService();
 		try {
 			InitialContext context = JndiHelper.getInitialContext( buildProperties() );
 			( (SessionFactory) context.lookup(boundName) ).close();
 			//context.unbind(boundName);
 		}
 		catch (Exception e) {
             LOG.unableToStopHibernateService(e);
 		}
 	}
 
 	SessionFactory buildSessionFactory() throws HibernateException {
         LOG.startingServiceAtJndiName( boundName );
         LOG.serviceProperties( properties );
         return buildConfiguration().buildSessionFactory(
-				new ServiceRegistryBuilder( properties ).buildServiceRegistry()
+				new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry()
 		);
 	}
 
 	@Override
 	protected Map getExtraProperties() {
 		return properties;
 	}
 
 	@Override
 	public String getTransactionStrategy() {
 		return getProperty(Environment.TRANSACTION_STRATEGY);
 	}
 
 	@Override
 	public void setTransactionStrategy(String txnStrategy) {
 		setProperty(Environment.TRANSACTION_STRATEGY, txnStrategy);
 	}
 
 	@Override
 	public String getUserTransactionName() {
 		return getProperty(Environment.USER_TRANSACTION);
 	}
 
 	@Override
 	public void setUserTransactionName(String utName) {
 		setProperty(Environment.USER_TRANSACTION, utName);
 	}
 
 	@Override
 	public String getJtaPlatformName() {
 		return getProperty( AvailableSettings.JTA_PLATFORM );
 	}
 
 	@Override
 	public void setJtaPlatformName(String name) {
 		setProperty( AvailableSettings.JTA_PLATFORM, name );
 	}
 
 	@Override
 	public String getPropertyList() {
 		return buildProperties().toString();
 	}
 
 	@Override
 	public String getProperty(String property) {
 		return properties.getProperty(property);
 	}
 
 	@Override
 	public void setProperty(String property, String value) {
 		properties.setProperty(property, value);
 	}
 
 	@Override
 	public void dropSchema() {
 		new SchemaExport( buildConfiguration() ).drop(false, true);
 	}
 
 	@Override
 	public void createSchema() {
 		new SchemaExport( buildConfiguration() ).create(false, true);
 	}
 
 	public String getName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public String getDatasource() {
 		return getProperty(Environment.DATASOURCE);
 	}
 
 	@Override
 	public void setDatasource(String datasource) {
 		setProperty(Environment.DATASOURCE, datasource);
 	}
 
 	@Override
 	public String getJndiName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public void setJndiName(String jndiName) {
 		setProperty(Environment.SESSION_FACTORY_NAME, jndiName);
 	}
 
 	@Override
 	public String getUserName() {
 		return getProperty(Environment.USER);
 	}
 
 	@Override
 	public void setUserName(String userName) {
 		setProperty(Environment.USER, userName);
 	}
 
 	@Override
 	public String getPassword() {
 		return getProperty(Environment.PASS);
 	}
 
 	@Override
 	public void setPassword(String password) {
 		setProperty(Environment.PASS, password);
 	}
 
 	@Override
 	public void setFlushBeforeCompletionEnabled(String enabled) {
 		setProperty(Environment.FLUSH_BEFORE_COMPLETION, enabled);
 	}
 
 	@Override
 	public String getFlushBeforeCompletionEnabled() {
 		return getProperty(Environment.FLUSH_BEFORE_COMPLETION);
 	}
 
 	@Override
 	public void setAutoCloseSessionEnabled(String enabled) {
 		setProperty(Environment.AUTO_CLOSE_SESSION, enabled);
 	}
 
 	@Override
 	public String getAutoCloseSessionEnabled() {
 		return getProperty(Environment.AUTO_CLOSE_SESSION);
 	}
 
 	public Properties getProperties() {
 		return buildProperties();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
index de0028d0d5..07b7cbcc9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
@@ -1,171 +1,261 @@
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
 package org.hibernate.service;
 
+import java.io.IOException;
+import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
+import java.util.Properties;
+
+import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Environment;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.integrator.spi.ServiceContributingIntegrator;
+import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.source.SourceType;
+import org.hibernate.metamodel.source.hbm.jaxb.config.XMLHibernateConfiguration;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.internal.JaxbProcessor;
 import org.hibernate.service.internal.ProvidedService;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Builder for basic service registry instances.
  *
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
+	private static final Logger log = Logger.getLogger( ServiceRegistryBuilder.class );
+
 	public static final String DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml";
 
 	private final Map settings;
 	private final List<BasicServiceInitiator> initiators = standardInitiatorList();
 	private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();
 
+	private final BootstrapServiceRegistryImpl bootstrapServiceRegistry;
+
 	/**
 	 * Create a default builder
 	 */
 	public ServiceRegistryBuilder() {
-		this( Environment.getProperties() );
+		this( new BootstrapServiceRegistryImpl() );
 	}
 
 	/**
-	 * Create a builder with the specified settings
+	 * Create a builder with the specified bootstrap services.
 	 *
-	 * @param settings The initial set of settings to use.
+	 * @param bootstrapServiceRegistry Provided bootstrap registry to use.
 	 */
-	public ServiceRegistryBuilder(Map settings) {
-		this.settings = settings;
+	public ServiceRegistryBuilder(BootstrapServiceRegistryImpl bootstrapServiceRegistry) {
+		this.settings = Environment.getProperties();
+		this.bootstrapServiceRegistry = bootstrapServiceRegistry;
 	}
 
+	/**
+	 * Used from the {@link #initiators} variable initializer
+	 *
+	 * @return List of standard initiators
+	 */
 	private static List<BasicServiceInitiator> standardInitiatorList() {
 		final List<BasicServiceInitiator> initiators = new ArrayList<BasicServiceInitiator>();
 		initiators.addAll( StandardServiceInitiators.LIST );
 		return initiators;
 	}
 
 	/**
-	 * Read setting information from the standard resource location
+	 * Read settings from a {@link Properties} file.  Differs from {@link #configure()} and {@link #configure(String)}
+	 * in that here we read a {@link Properties} file while for {@link #configure} we read the XML variant.
+	 *
+	 * @param resourceName The name by which to perform a resource look up for the properties file.
+	 *
+	 * @return this, for method chaining
+	 *
+	 * @see #configure()
+	 * @see #configure(String)
+	 */
+	@SuppressWarnings( {"unchecked"})
+	public ServiceRegistryBuilder loadProperties(String resourceName) {
+		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
+		try {
+			Properties properties = new Properties();
+			properties.load( stream );
+			settings.putAll( properties );
+		}
+		catch (IOException e) {
+			throw new ConfigurationException( "Unable to apply settings from properties file [" + resourceName + "]", e );
+		}
+		finally {
+			try {
+				stream.close();
+			}
+			catch (IOException e) {
+				log.debug(
+						String.format( "Unable to close properties file [%s] stream", resourceName ),
+						e
+				);
+			}
+		}
+
+		return this;
+	}
+
+	/**
+	 * Read setting information from an XML file using the standard resource location
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #DEFAULT_CFG_RESOURCE_NAME
+	 * @see #configure(String)
+	 * @see #loadProperties(String)
 	 */
 	public ServiceRegistryBuilder configure() {
 		return configure( DEFAULT_CFG_RESOURCE_NAME );
 	}
 
 	/**
-	 * Read setting information from the named resource location
+	 * Read setting information from an XML file using the named resource location
 	 *
 	 * @param resourceName The named resource
 	 *
 	 * @return this, for method chaining
+	 *
+	 * @see #loadProperties(String)
 	 */
+	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistryBuilder configure(String resourceName) {
-		// todo : parse and apply XML
-		// we run into a chicken-egg problem here, in that we need the service registry in order to know how to do this
-		// resource lookup (ClassLoaderService)
+		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
+		XMLHibernateConfiguration configurationElement = jaxbProcessorHolder.getValue().unmarshal(
+				stream,
+				new Origin( SourceType.RESOURCE, resourceName )
+		);
+		for ( XMLHibernateConfiguration.XMLSessionFactory.XMLProperty xmlProperty : configurationElement.getSessionFactory().getProperty() ) {
+			settings.put( xmlProperty.getName(), xmlProperty.getValue() );
+		}
+
 		return this;
 	}
 
+	private Value<JaxbProcessor> jaxbProcessorHolder = new Value<JaxbProcessor>(
+			new Value.DeferredInitializer<JaxbProcessor>() {
+				@Override
+				public JaxbProcessor initialize() {
+					return new JaxbProcessor( bootstrapServiceRegistry.getService( ClassLoaderService.class ) );
+				}
+			}
+	);
+
 	/**
 	 * Apply a setting value
 	 *
 	 * @param settingName The name of the setting
 	 * @param value The value to use.
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked", "UnusedDeclaration"})
 	public ServiceRegistryBuilder applySetting(String settingName, Object value) {
 		settings.put( settingName, value );
 		return this;
 	}
 
 	/**
 	 * Apply a groups of setting values
 	 *
 	 * @param settings The incoming settings to apply
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked", "UnusedDeclaration"})
 	public ServiceRegistryBuilder applySettings(Map settings) {
 		this.settings.putAll( settings );
 		return this;
 	}
 
 	/**
 	 * Adds a service initiator.
 	 *
 	 * @param initiator The initiator to be added
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public ServiceRegistryBuilder addInitiator(BasicServiceInitiator initiator) {
 		initiators.add( initiator );
 		return this;
 	}
 
 	/**
 	 * Adds a user-provided service
 	 *
 	 * @param serviceRole The role of the service being added
 	 * @param service The service implementation
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistryBuilder addService(final Class serviceRole, final Service service) {
 		providedServices.add( new ProvidedService( serviceRole, service ) );
 		return this;
 	}
 
 	/**
 	 * Build the service registry accounting for all settings and service initiators and services.
 	 *
 	 * @return The built service registry
 	 */
 	public BasicServiceRegistry buildServiceRegistry() {
 		Map<?,?> settingsCopy = new HashMap();
 		settingsCopy.putAll( settings );
 		Environment.verifyProperties( settingsCopy );
 		ConfigurationHelper.resolvePlaceHolders( settingsCopy );
-		return new BasicServiceRegistryImpl( initiators, providedServices, settingsCopy );
+
+		for ( Integrator integrator : bootstrapServiceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
+			if ( ServiceContributingIntegrator.class.isInstance( integrator ) ) {
+				ServiceContributingIntegrator.class.cast( integrator ).prepareServices( this );
+			}
+		}
+
+		return new BasicServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
 	}
 
 	/**
 	 * Destroy a service registry.  Applications should only destroy registries they have explicitly created.
 	 *
 	 * @param serviceRegistry The registry to be closed.
 	 */
 	public static void destroy(BasicServiceRegistry serviceRegistry) {
 		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 830b500063..b7c46f0c09 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,86 +1,82 @@
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
 package org.hibernate.service;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.id.factory.internal.MutableIdentifierGeneratorFactoryInitiator;
-import org.hibernate.integrator.internal.IntegratorServiceInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
-import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
 import org.hibernate.service.config.internal.ConfigurationServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
 		serviceInitiators.add( ConfigurationServiceInitiator.INSTANCE );
 
-		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
 		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( MultiTenantConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( MutableIdentifierGeneratorFactoryInitiator.INSTANCE);
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
-		serviceInitiators.add( IntegratorServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( RegionFactoryInitiator.INSTANCE );
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
index 613076995d..bbe5c4ef75 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,189 +1,257 @@
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
 package org.hibernate.service.classloading.internal;
 
+import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
+import java.util.HashSet;
+import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
+import java.util.ServiceLoader;
 
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
-	private final LinkedHashSet<ClassLoader> classLoadingClassLoaders;
+	private final ClassLoader classClassLoader;
 	private final ClassLoader resourcesClassLoader;
 
-	public ClassLoaderServiceImpl(Map configVales) {
-		this( determineClassLoaders( configVales ) );
+	public ClassLoaderServiceImpl() {
+		this( ClassLoaderServiceImpl.class.getClassLoader() );
 	}
 
-	private ClassLoaderServiceImpl(ClassLoader... classLoaders) {
-		this( classLoaders[0], classLoaders[1], classLoaders[2], classLoaders[3] );
+	public ClassLoaderServiceImpl(ClassLoader classLoader) {
+		this( classLoader, classLoader, classLoader, classLoader );
 	}
 
-	private static ClassLoader[] determineClassLoaders(Map configVales) {
-		ClassLoader applicationClassLoader = (ClassLoader) configVales.get( AvailableSettings.APP_CLASSLOADER );
-		ClassLoader resourcesClassLoader = (ClassLoader) configVales.get( AvailableSettings.RESOURCES_CLASSLOADER );
-		ClassLoader hibernateClassLoader = (ClassLoader) configVales.get( AvailableSettings.HIBERNATE_CLASSLOADER );
-		ClassLoader environmentClassLoader = (ClassLoader) configVales.get( AvailableSettings.ENVIRONMENT_CLASSLOADER );
-
+	public ClassLoaderServiceImpl(
+			ClassLoader applicationClassLoader,
+			ClassLoader resourcesClassLoader,
+			ClassLoader hibernateClassLoader,
+			ClassLoader environmentClassLoader) {
+		// Normalize missing loaders
 		if ( hibernateClassLoader == null ) {
 			hibernateClassLoader = ClassLoaderServiceImpl.class.getClassLoader();
 		}
 
 		if ( environmentClassLoader == null || applicationClassLoader == null ) {
 			ClassLoader sysClassLoader = locateSystemClassLoader();
 			ClassLoader tccl = locateTCCL();
 			if ( environmentClassLoader == null ) {
 				environmentClassLoader = sysClassLoader != null ? sysClassLoader : hibernateClassLoader;
 			}
 			if ( applicationClassLoader == null ) {
 				applicationClassLoader = tccl != null ? tccl : hibernateClassLoader;
 			}
 		}
 
 		if ( resourcesClassLoader == null ) {
 			resourcesClassLoader = applicationClassLoader;
 		}
 
-		return new ClassLoader[] {
-			applicationClassLoader,
-			resourcesClassLoader,
-			hibernateClassLoader,
-			environmentClassLoader
+		final LinkedHashSet<ClassLoader> classLoadingClassLoaders = new LinkedHashSet<ClassLoader>();
+		classLoadingClassLoaders.add( applicationClassLoader );
+		classLoadingClassLoaders.add( hibernateClassLoader );
+		classLoadingClassLoaders.add( environmentClassLoader );
+
+		this.classClassLoader = new ClassLoader() {
+			@Override
+			protected Class<?> findClass(String name) throws ClassNotFoundException {
+				for ( ClassLoader loader : classLoadingClassLoaders ) {
+					try {
+						return loader.loadClass( name );
+					}
+					catch (Exception ignore) {
+					}
+				}
+				throw new ClassNotFoundException( "Could not load requested class : " + name );
+			}
 		};
+
+		this.resourcesClassLoader = resourcesClassLoader;
+	}
+
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public static ClassLoaderServiceImpl fromConfigSettings(Map configVales) {
+		return new ClassLoaderServiceImpl(
+				(ClassLoader) configVales.get( AvailableSettings.APP_CLASSLOADER ),
+				(ClassLoader) configVales.get( AvailableSettings.RESOURCES_CLASSLOADER ),
+				(ClassLoader) configVales.get( AvailableSettings.HIBERNATE_CLASSLOADER ),
+				(ClassLoader) configVales.get( AvailableSettings.ENVIRONMENT_CLASSLOADER )
+		);
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
 			return Thread.currentThread().getContextClassLoader();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
-	public ClassLoaderServiceImpl(ClassLoader classLoader) {
-		this( classLoader, classLoader, classLoader, classLoader );
-	}
-
-	public ClassLoaderServiceImpl(
-			ClassLoader applicationClassLoader,
-			ClassLoader resourcesClassLoader,
-			ClassLoader hibernateClassLoader,
-			ClassLoader environmentClassLoader) {
-		this.classLoadingClassLoaders = new LinkedHashSet<ClassLoader>();
-		classLoadingClassLoaders.add( applicationClassLoader );
-		classLoadingClassLoaders.add( hibernateClassLoader );
-		classLoadingClassLoaders.add( environmentClassLoader );
-
-		this.resourcesClassLoader = resourcesClassLoader;
-	}
-
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> classForName(String className) {
-		for ( ClassLoader classLoader : classLoadingClassLoaders ) {
-			try {
-				return (Class<T>) classLoader.loadClass( className );
-			}
-			catch ( Exception ignore) {
-			}
+		try {
+			return (Class<T>) classClassLoader.loadClass( className );
+		}
+		catch (Exception e) {
+			throw new ClassLoadingException( "Unable to load class [" + className + "]", e );
 		}
-		throw new ClassLoadingException( "Unable to load class [" + className + "]" );
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
 			return resourcesClassLoader.getResource( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name ).openStream();
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			return resourcesClassLoader.getResourceAsStream( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
 		ArrayList<URL> urls = new ArrayList<URL>();
 		try {
 			Enumeration<URL> urlEnumeration = resourcesClassLoader.getResources( name );
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
 
+	@Override
+	public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract) {
+		final ClassLoader serviceLoaderClassLoader = new ClassLoader() {
+			final ClassLoader[] classLoaderArray = new ClassLoader[] {
+					// first look on the hibernate class loader
+					getClass().getClassLoader(),
+					// next look on the resource class loader
+					resourcesClassLoader,
+					// finally look on the combined class class loader
+					classClassLoader
+			};
+
+			@Override
+			public Enumeration<URL> getResources(String name) throws IOException {
+				final HashSet<URL> resourceUrls = new HashSet<URL>();
+
+				for ( ClassLoader classLoader : classLoaderArray ) {
+					final Enumeration<URL> urls = classLoader.getResources( name );
+					while ( urls.hasMoreElements() ) {
+						resourceUrls.add( urls.nextElement() );
+					}
+				}
+
+				return new Enumeration<URL>() {
+					final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();
+					@Override
+					public boolean hasMoreElements() {
+						return resourceUrlIterator.hasNext();
+					}
+
+					@Override
+					public URL nextElement() {
+						return resourceUrlIterator.next();
+					}
+				};
+			}
+
+			@Override
+			protected Class<?> findClass(String name) throws ClassNotFoundException {
+				for ( ClassLoader classLoader : classLoaderArray ) {
+					try {
+						classLoader.loadClass( name );
+					}
+					catch (Exception ignore) {
+					}
+				}
+
+				throw new ClassNotFoundException( "Could not load requested class : " + name );
+			}
+		};
+
+		final ServiceLoader<S> loader = ServiceLoader.load( serviceContract, serviceLoaderClassLoader );
+		final LinkedHashSet<S> services = new LinkedHashSet<S>();
+		for ( S service : loader ) {
+			services.add( service );
+		}
+
+		return services;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
deleted file mode 100644
index 861f677cf1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
+++ /dev/null
@@ -1,49 +0,0 @@
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
-package org.hibernate.service.classloading.internal;
-
-import java.util.Map;
-
-import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
-import org.hibernate.service.spi.BasicServiceInitiator;
-
-/**
- * Standard initiator for the standard {@link ClassLoaderService} service.
- *
- * @author Steve Ebersole
- */
-public class ClassLoaderServiceInitiator implements BasicServiceInitiator<ClassLoaderService> {
-	public static final ClassLoaderServiceInitiator INSTANCE = new ClassLoaderServiceInitiator();
-
-	@Override
-	public Class<ClassLoaderService> getServiceInitiated() {
-		return ClassLoaderService.class;
-	}
-
-	@Override
-	public ClassLoaderService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-		return new ClassLoaderServiceImpl( configurationValues );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
index 7672f1ddcc..5bbd2a0555 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
@@ -1,76 +1,89 @@
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
 package org.hibernate.service.classloading.spi;
 
 import java.io.InputStream;
 import java.net.URL;
+import java.util.LinkedHashSet;
 import java.util.List;
 
 import org.hibernate.service.Service;
 
 /**
  * A service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public interface ClassLoaderService extends Service {
-
 	/**
 	 * Locate a class by name
 	 *
 	 * @param className The name of the class to locate
 	 *
 	 * @return The class reference
 	 *
 	 * @throws ClassLoadingException Indicates the class could not be found
 	 */
 	public <T> Class<T> classForName(String className);
 
 	/**
 	 * Locate a resource by name (classpath lookup)
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The located URL; may return {@code null} to indicate the resource was not found
 	 */
 	public URL locateResource(String name);
 
 	/**
 	 * Locate a resource by name (classpath lookup) and gets its stream
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The stream of the located resource; may return {@code null} to indicate the resource was not found
 	 */
 	public InputStream locateResourceStream(String name);
 
 	/**
 	 * Locate a series of resource by name (classpath lookup)
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The list of URL matching; may return {@code null} to indicate the resource was not found
 	 */
 	public List<URL> locateResources(String name);
+
+	/**
+	 * Discovers and instantiates implementations of the named service contract.
+	 * <p/>
+	 * NOTE : the terms service here is used differently than {@link Service}.  Instead here we are talking about
+	 * services as defined by {@link java.util.ServiceLoader}.
+	 *
+	 * @param serviceContract The java type defining the service contract
+	 * @param <S> The type of the service contract
+	 *     
+	 * @return The ordered set of discovered services.
+	 */
+	public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
index e34eb5aada..4229772048 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
@@ -1,84 +1,85 @@
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
 package org.hibernate.service.internal;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
 public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl implements BasicServiceRegistry {
-
 	private final Map configurationValues;
 
 	@SuppressWarnings( {"unchecked"})
 	public BasicServiceRegistryImpl(
-			final List<BasicServiceInitiator> serviceInitiators,
-			final List<ProvidedService> providedServices,
-			final Map configurationValues) {
-		super();
+			ServiceRegistryImplementor bootstrapServiceRegistry,
+			List<BasicServiceInitiator> serviceInitiators,
+			List<ProvidedService> providedServices,
+			Map<?, ?> configurationValues) {
+		super( bootstrapServiceRegistry );
 
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
 		return ( (BasicServiceInitiator<R>) serviceInitiator ).initiateService( configurationValues, this );
 	}
 
 	@Override
 	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 
 		if ( Configurable.class.isInstance( service ) ) {
 			( (Configurable) service ).configure( configurationValues );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java
new file mode 100644
index 0000000000..9a3872aaf4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java
@@ -0,0 +1,169 @@
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
+package org.hibernate.service.internal;
+
+import java.util.LinkedHashSet;
+
+import org.hibernate.integrator.internal.IntegratorServiceImpl;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.service.Service;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.classloading.internal.ClassLoaderServiceImpl;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.spi.ServiceBinding;
+import org.hibernate.service.spi.ServiceException;
+import org.hibernate.service.spi.ServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+/**
+ * {@link ServiceRegistry} implementation containing specialized "bootstrap" services, specifically:<ul>
+ * <li>{@link ClassLoaderService}</li>
+ * <li>{@link IntegratorService}</li>
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public class BootstrapServiceRegistryImpl implements ServiceRegistryImplementor, ServiceBinding.OwningRegistry {
+	private static final LinkedHashSet<Integrator> NO_INTEGRATORS = new LinkedHashSet<Integrator>();
+
+	private final ServiceBinding<ClassLoaderService> classLoaderServiceBinding;
+	private final ServiceBinding<IntegratorService> integratorServiceBinding;
+
+	public static Builder builder() {
+		return new Builder();
+	}
+
+	public BootstrapServiceRegistryImpl() {
+		this( new ClassLoaderServiceImpl(), NO_INTEGRATORS );
+	}
+
+	public BootstrapServiceRegistryImpl(
+			ClassLoaderService classLoaderService,
+			IntegratorService integratorService) {
+		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
+				this,
+				ClassLoaderService.class,
+				classLoaderService
+		);
+
+		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
+				this,
+				IntegratorService.class,
+				integratorService
+		);
+	}
+
+
+	public BootstrapServiceRegistryImpl(
+			ClassLoaderService classLoaderService,
+			LinkedHashSet<Integrator> providedIntegrators) {
+		this( classLoaderService, new IntegratorServiceImpl( providedIntegrators, classLoaderService ) );
+	}
+
+
+
+	@Override
+	public <R extends Service> R getService(Class<R> serviceRole) {
+		final ServiceBinding<R> binding = locateServiceBinding( serviceRole );
+		return binding == null ? null : binding.getService();
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
+		if ( ClassLoaderService.class.equals( serviceRole ) ) {
+			return (ServiceBinding<R>) classLoaderServiceBinding;
+		}
+		else if ( IntegratorService.class.equals( serviceRole ) ) {
+			return (ServiceBinding<R>) integratorServiceBinding;
+		}
+
+		return null;
+	}
+
+	@Override
+	public void destroy() {
+	}
+
+	@Override
+	public ServiceRegistry getParentServiceRegistry() {
+		return null;
+	}
+
+	@Override
+	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
+		// the bootstrap registry should currently be made up of only directly built services.
+		throw new ServiceException( "Boot-strap registry should only contain directly built services" );
+	}
+
+	public static class Builder {
+		private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet<Integrator>();
+		private ClassLoader applicationClassLoader;
+		private ClassLoader resourcesClassLoader;
+		private ClassLoader hibernateClassLoader;
+		private ClassLoader environmentClassLoader;
+
+		public Builder with(Integrator integrator) {
+			providedIntegrators.add( integrator );
+			return this;
+		}
+
+		public Builder withApplicationClassLoader(ClassLoader classLoader) {
+			this.applicationClassLoader = classLoader;
+			return this;
+		}
+
+		public Builder withResourceClassLoader(ClassLoader classLoader) {
+			this.resourcesClassLoader = classLoader;
+			return this;
+		}
+
+		public Builder withHibernateClassLoader(ClassLoader classLoader) {
+			this.hibernateClassLoader = classLoader;
+			return this;
+		}
+
+		public Builder withEnvironmentClassLoader(ClassLoader classLoader) {
+			this.environmentClassLoader = classLoader;
+			return this;
+		}
+
+		public BootstrapServiceRegistryImpl build() {
+			final ClassLoaderServiceImpl classLoaderService = new ClassLoaderServiceImpl(
+					applicationClassLoader,
+					resourcesClassLoader,
+					hibernateClassLoader,
+					environmentClassLoader
+			);
+
+			final IntegratorServiceImpl integratorService = new IntegratorServiceImpl(
+					providedIntegrators,
+					classLoaderService
+			);
+
+			return new BootstrapServiceRegistryImpl( classLoaderService, integratorService );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/JaxbProcessor.java b/hibernate-core/src/main/java/org/hibernate/service/internal/JaxbProcessor.java
new file mode 100644
index 0000000000..63e2a50b70
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/JaxbProcessor.java
@@ -0,0 +1,195 @@
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
+package org.hibernate.service.internal;
+
+import javax.xml.XMLConstants;
+import javax.xml.bind.JAXBContext;
+import javax.xml.bind.JAXBException;
+import javax.xml.bind.Unmarshaller;
+import javax.xml.bind.ValidationEvent;
+import javax.xml.bind.ValidationEventHandler;
+import javax.xml.bind.ValidationEventLocator;
+import javax.xml.stream.XMLInputFactory;
+import javax.xml.stream.XMLStreamException;
+import javax.xml.stream.XMLStreamReader;
+import javax.xml.transform.stream.StreamSource;
+import javax.xml.validation.Schema;
+import javax.xml.validation.SchemaFactory;
+import java.io.IOException;
+import java.io.InputStream;
+import java.net.URL;
+
+import org.jboss.logging.Logger;
+import org.xml.sax.SAXException;
+
+import org.hibernate.internal.util.config.ConfigurationException;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.source.XsdException;
+import org.hibernate.metamodel.source.hbm.jaxb.config.XMLHibernateConfiguration;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JaxbProcessor {
+	private static final Logger log = Logger.getLogger( JaxbProcessor.class );
+
+	private final ClassLoaderService classLoaderService;
+
+	public JaxbProcessor(ClassLoaderService classLoaderService) {
+		this.classLoaderService = classLoaderService;
+	}
+
+	public XMLHibernateConfiguration unmarshal(InputStream stream, Origin origin) {
+		try {
+			XMLStreamReader staxReader = staxFactory().createXMLStreamReader( stream );
+			try {
+				return unmarshal( staxReader, origin );
+			}
+			finally {
+				try {
+					staxReader.close();
+				}
+				catch ( Exception ignore ) {
+				}
+			}
+		}
+		catch ( XMLStreamException e ) {
+			throw new MappingException( "Unable to create stax reader", e, origin );
+		}
+	}
+
+	private XMLInputFactory staxFactory;
+
+	private XMLInputFactory staxFactory() {
+		if ( staxFactory == null ) {
+			staxFactory = buildStaxFactory();
+		}
+		return staxFactory;
+	}
+
+	@SuppressWarnings( { "UnnecessaryLocalVariable" })
+	private XMLInputFactory buildStaxFactory() {
+		XMLInputFactory staxFactory = XMLInputFactory.newInstance();
+		return staxFactory;
+	}
+
+	@SuppressWarnings( { "unchecked" })
+	private XMLHibernateConfiguration unmarshal(XMLStreamReader staxReader, final Origin origin) {
+		final Object target;
+		final ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
+		try {
+			JAXBContext jaxbContext = JAXBContext.newInstance( XMLHibernateConfiguration.class );
+			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
+			unmarshaller.setSchema( schema() );
+			unmarshaller.setEventHandler( handler );
+			target = unmarshaller.unmarshal( staxReader );
+			return (XMLHibernateConfiguration) target;
+		}
+		catch ( JAXBException e ) {
+			StringBuilder builder = new StringBuilder();
+			builder.append( "Unable to perform unmarshalling at line number " )
+					.append( handler.getLineNumber() )
+					.append( " and column " )
+					.append( handler.getColumnNumber() )
+					.append( " in " ).append( origin.getType().name() ).append( " " ).append( origin.getName() )
+					.append( ". Message: " )
+					.append( handler.getMessage() );
+			throw new ConfigurationException( builder.toString(), e );
+		}
+	}
+
+	private Schema schema;
+
+	private Schema schema() {
+		if ( schema == null ) {
+			schema = resolveLocalSchema( "/org/hibernate/hibernate-configuration-4.0.xsd" );
+		}
+		return schema;
+	}
+
+	private Schema resolveLocalSchema(String schemaName) {
+		return resolveLocalSchema( schemaName, XMLConstants.W3C_XML_SCHEMA_NS_URI );
+	}
+
+	private Schema resolveLocalSchema(String schemaName, String schemaLanguage) {
+		URL url = classLoaderService.locateResource( schemaName );
+		if ( url == null ) {
+			throw new XsdException( "Unable to locate schema [" + schemaName + "] via classpath", schemaName );
+		}
+		try {
+			InputStream schemaStream = url.openStream();
+			try {
+				StreamSource source = new StreamSource( url.openStream() );
+				SchemaFactory schemaFactory = SchemaFactory.newInstance( schemaLanguage );
+				return schemaFactory.newSchema( source );
+			}
+			catch ( SAXException e ) {
+				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
+			}
+			catch ( IOException e ) {
+				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
+			}
+			finally {
+				try {
+					schemaStream.close();
+				}
+				catch ( IOException e ) {
+					log.debugf( "Problem closing schema stream [%s]", e.toString() );
+				}
+			}
+		}
+		catch ( IOException e ) {
+			throw new XsdException( "Stream error handling schema url [" + url.toExternalForm() + "]", schemaName );
+		}
+	}
+
+	static class ContextProvidingValidationEventHandler implements ValidationEventHandler {
+		private int lineNumber;
+		private int columnNumber;
+		private String message;
+
+		@Override
+		public boolean handleEvent(ValidationEvent validationEvent) {
+			ValidationEventLocator locator = validationEvent.getLocator();
+			lineNumber = locator.getLineNumber();
+			columnNumber = locator.getColumnNumber();
+			message = validationEvent.getMessage();
+			return false;
+		}
+
+		public int getLineNumber() {
+			return lineNumber;
+		}
+
+		public int getColumnNumber() {
+			return columnNumber;
+		}
+
+		public String getMessage() {
+			return message;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 95a3569ea8..cd12c24417 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,609 +1,609 @@
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
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class SchemaExport {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
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
 
 	private String outputFile = null;
 	private String delimiter;
 	private boolean haltOnError = false;
 
 	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
 		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = serviceRegistry.getService( JdbcServices.class ).getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(MetadataImplementor metadata) {
 		ServiceRegistry serviceRegistry = metadata.getServiceRegistry();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
         JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				serviceRegistry.getService( ConfigurationService.class ).getSettings(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = jdbcServices.getDialect();
 		this.dropSQL = metadata.getDatabase().generateDropSchemaScript( dialect );
 		this.createSQL = metadata.getDatabase().generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration) {
 		this( configuration, configuration.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	@Deprecated
     public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
 		final Dialect dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				properties,
 				DEFAULT_IMPORT_FILE
 		);
 
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param configuration The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = Dialect.getDialect( configuration.getProperties() );
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
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
 		if ( output == Target.NONE || type == SchemaExport.Type.NONE ) {
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
 		long lineNo = 0;
 		for ( String sql = reader.readLine(); sql != null; sql = reader.readLine() ) {
 			try {
 				lineNo++;
 				String trimmedSql = sql.trim();
 				if ( trimmedSql.length() == 0 ||
 						trimmedSql.startsWith( "--" ) ||
 						trimmedSql.startsWith( "//" ) ||
 						trimmedSql.startsWith( "/*" ) ) {
 					continue;
 				}
                 if ( trimmedSql.endsWith(";") ) {
 					trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
 				}
                 LOG.debugf( trimmedSql );
 				for ( Exporter exporter: exporters ) {
 					if ( exporter.acceptsImportScripts() ) {
 						exporter.export( trimmedSql );
 					}
 				}
 			}
 			catch ( Exception e ) {
 				throw new ImportScriptException( "Error during import script execution at line " + lineNo, e );
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
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 		String formatted = formatter.format( sql );
         if (delimiter != null) formatted += delimiter;
         if (script) System.out.println(formatted);
         LOG.debugf(formatted);
 		if ( outputFile != null ) {
 			fileOutput.write( formatted + "\n" );
 		}
 		if ( export ) {
 
 			statement.executeUpdate( sql );
 			try {
 				SQLWarning warnings = statement.getWarnings();
 				if ( warnings != null) {
 					sqlExceptionHelper.logAndClearWarnings( connectionHelper.getConnection() );
 				}
 			}
 			catch( SQLException sqle ) {
                 LOG.unableToLogSqlWarnings(sqle);
 			}
 		}
 
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			boolean drop = false;
 			boolean create = false;
 			boolean halt = false;
 			boolean export = true;
 			String outFile = null;
 			String importFile = DEFAULT_IMPORT_FILE;
 			String propFile = null;
 			boolean format = false;
 			String delim = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].equals( "--drop" ) ) {
 						drop = true;
 					}
 					else if ( args[i].equals( "--create" ) ) {
 						create = true;
 					}
 					else if ( args[i].equals( "--haltonerror" ) ) {
 						halt = true;
 					}
 					else if ( args[i].equals( "--text" ) ) {
 						export = false;
 					}
 					else if ( args[i].startsWith( "--output=" ) ) {
 						outFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--import=" ) ) {
 						importFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].equals( "--format" ) ) {
 						format = true;
 					}
 					else if ( args[i].startsWith( "--delimiter=" ) ) {
 						delim = args[i].substring( 12 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) )
 										.newInstance()
 						);
 					}
 				}
 				else {
 					String filename = args[i];
 					if ( filename.endsWith( ".jar" ) ) {
 						cfg.addJar( new File( filename ) );
 					}
 					else {
 						cfg.addFile( filename );
 					}
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			if (importFile != null) {
 				cfg.setProperty( Environment.HBM2DDL_IMPORT_FILES, importFile );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				SchemaExport se = new SchemaExport( serviceRegistry, cfg )
 						.setHaltOnError( halt )
 						.setOutputFile( outFile )
 						.setDelimiter( delim );
 				if ( format ) {
 					se.setFormat( true );
 				}
 				se.execute( script, export, drop, create );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToCreateSchema(e);
 			e.printStackTrace();
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
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index 44de7b0060..d2d0624087 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,295 +1,295 @@
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
 
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from inside an application.
  *
  * @author Christoph Sturm
  * @author Steve Ebersole
  */
 public class SchemaUpdate {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
 
 	private final Configuration configuration;
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final Dialect dialect;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
 		this.configuration = configuration;
 		this.dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			// If true then execute db updates, otherwise just generate and display updates
 			boolean doUpdate = true;
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--text" ) ) {
 						doUpdate = false;
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaUpdate( serviceRegistry, cfg ).execute( script, doUpdate );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
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
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
             LOG.updatingSchema();
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile( outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			String[] sqlStrings = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( String sql : sqlStrings ) {
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( target.doScript() ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( target.doExport() ) {
                         LOG.debugf( sql );
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
                     LOG.unsuccessful(sql);
                     LOG.error(e.getMessage());
 				}
 			}
 
             LOG.schemaUpdateComplete();
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.unableToCompleteSchemaUpdate(e);
 		}
 		finally {
 
 			try {
 				if ( stmt != null ) {
 					stmt.close();
 				}
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
                 LOG.unableToCloseConnection(e);
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
                 LOG.unableToCloseConnection(e);
 			}
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
 
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	public void setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
index 980bfff2ef..666ed76ed2 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
@@ -1,172 +1,172 @@
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
 
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaValidator {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaValidator.class.getName());
 
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 
 	public SchemaValidator(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaValidator(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 	}
 
 	public SchemaValidator(ServiceRegistry serviceRegistry, Configuration cfg ) throws HibernateException {
 		this.configuration = cfg;
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaValidator( serviceRegistry, cfg ).validate();
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Perform the validations.
 	 */
 	public void validate() {
 
         LOG.runningSchemaValidator();
 
 		Connection connection = null;
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( false );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect, false );
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
 			configuration.validateSchema( dialect, meta );
 
 		}
 		catch ( SQLException e ) {
             LOG.unableToCompleteSchemaValidation(e);
 		}
 		finally {
 
 			try {
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
                 LOG.unableToCloseConnection(e);
 			}
 
 		}
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
index 0977b583c4..47b5f60269 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,91 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.test.cfg.persister;
 
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends BaseUnitTestCase {
 	@Test
 	public void testPersisterClassProvider() throws Exception {
 
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
-		BasicServiceRegistry serviceRegistry = new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry();
+		BasicServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
+				.applySettings( cfg.getProperties() )
+				.buildServiceRegistry();
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 
-		serviceRegistry = new ServiceRegistryBuilder( cfg.getProperties() )
+		serviceRegistry = new ServiceRegistryBuilder()
+				.applySettings( cfg.getProperties() )
 				.addService( PersisterClassResolver.class, new GoofyPersisterClassProvider() )
 				.buildServiceRegistry();
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The entity persister should be overridden",
 					GoofyPersisterClassProvider.NoopEntityPersister.class,
 					( (GoofyException) e.getCause() ).getValue()
 			);
 		}
 		finally {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Portal.class );
 		cfg.addAnnotatedClass( Window.class );
-		serviceRegistry = new ServiceRegistryBuilder( cfg.getProperties() )
+		serviceRegistry = new ServiceRegistryBuilder()
+				.applySettings( cfg.getProperties() )
 				.addService( PersisterClassResolver.class, new GoofyPersisterClassProvider() )
 				.buildServiceRegistry();
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The collection persister should be overridden but not the entity persister",
 					GoofyPersisterClassProvider.NoopCollectionPersister.class,
 					( (GoofyException) e.getCause() ).getValue() );
 		}
 		finally {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java
index ef220f929f..06b0468490 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java
@@ -1,146 +1,145 @@
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
 package org.hibernate.test.events;
 
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.integrator.spi.Integrator;
-import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 
 /**
  * CallbackTest implementation
  *
  * @author Steve Ebersole
  */
 public class CallbackTest extends BaseCoreFunctionalTestCase {
 	private TestingObserver observer = new TestingObserver();
 	private TestingListener listener = new TestingListener();
 
 	@Override
     public String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	@Override
     public void configure(Configuration cfg) {
 		cfg.setSessionFactoryObserver( observer );
 	}
 
 	@Override
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
-		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+		super.prepareBootstrapRegistryBuilder( builder );
+		builder.with(
 				new Integrator() {
 
 				    @Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
                         integrate(serviceRegistry);
 					}
 
                     @Override
 				    public void integrate( MetadataImplementor metadata,
 				                           SessionFactoryImplementor sessionFactory,
 				                           SessionFactoryServiceRegistry serviceRegistry ) {
                         integrate(serviceRegistry);
 				    }
 
                     private void integrate( SessionFactoryServiceRegistry serviceRegistry ) {
                         serviceRegistry.getService( EventListenerRegistry.class ).setListeners(EventType.DELETE, listener);
                         listener.initialize();
                     }
 
 					@Override
 					public void disintegrate(
 							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 						listener.cleanup();
 					}
 				}
 		);
 	}
 
 	@Test
 	public void testCallbacks() {
 		assertEquals( "observer not notified of creation", 1, observer.creationCount );
 		assertEquals( "listener not notified of creation", 1, listener.initCount );
 
 		sessionFactory().close();
 
 		assertEquals( "observer not notified of close", 1, observer.closedCount );
 		assertEquals( "listener not notified of close", 1, listener.destoryCount );
 	}
 
 	private static class TestingObserver implements SessionFactoryObserver {
 		private int creationCount = 0;
 		private int closedCount = 0;
 
 		public void sessionFactoryCreated(SessionFactory factory) {
 			creationCount++;
 		}
 
 		public void sessionFactoryClosed(SessionFactory factory) {
 			closedCount++;
 		}
 	}
 
 	private static class TestingListener implements DeleteEventListener {
 		private int initCount = 0;
 		private int destoryCount = 0;
 
 		public void initialize() {
 			initCount++;
 		}
 
 		public void cleanup() {
 			destoryCount++;
 		}
 
 		public void onDelete(DeleteEvent event) throws HibernateException {
 		}
 
 		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java b/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
index 8102fea136..deafccc854 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
@@ -1,114 +1,116 @@
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
 package org.hibernate.test.flush;
 
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.event.spi.EventType;
 import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+import org.hibernate.event.spi.EventType;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-2763" )
 public class TestCollectionInitializingDuringFlush extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] { Author.class, Book.class, Publisher.class };
 	}
 
 	@Override
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
-		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+		super.prepareBootstrapRegistryBuilder( builder );
+		builder.with(
 				new Integrator() {
 
 					@Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
-                        integrate(serviceRegistry);
+						integrate( serviceRegistry );
 					}
 
 					@Override
-					public void integrate( MetadataImplementor metadata,
-					                       SessionFactoryImplementor sessionFactory,
-					                       SessionFactoryServiceRegistry serviceRegistry ) {
-					    integrate(serviceRegistry);
+					public void integrate(
+							MetadataImplementor metadata,
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						integrate( serviceRegistry );
 					}
 
-					private void integrate( SessionFactoryServiceRegistry serviceRegistry ) {
-                        serviceRegistry.getService(EventListenerRegistry.class).getEventListenerGroup(EventType.PRE_UPDATE)
-                                       .appendListener(new InitializingPreUpdateEventListener());
+					private void integrate(SessionFactoryServiceRegistry serviceRegistry) {
+						serviceRegistry.getService( EventListenerRegistry.class )
+								.getEventListenerGroup( EventType.PRE_UPDATE )
+								.appendListener( new InitializingPreUpdateEventListener() );
 					}
 
 					@Override
 					public void disintegrate(
 							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-2763" )
 	public void testInitializationDuringFlush() {
 		Session s = openSession();
 		s.beginTransaction();
 		Publisher publisher = new Publisher( "acme" );
 		Author author = new Author( "john" );
 		author.setPublisher( publisher );
 		publisher.getAuthors().add( author );
 		author.getBooks().add( new Book( "Reflections on a Wimpy Kid", author ) );
 		s.save( author );
 		s.getTransaction().commit();
 		s.clear();
 
 		s = openSession();
 		s.beginTransaction();
 		publisher = (Publisher) s.get( Publisher.class, publisher.getId() );
 		publisher.setName( "random nally" );
 		s.flush();
 		s.getTransaction().commit();
 		s.clear();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( author );
 		s.getTransaction().commit();
 		s.clear();
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java
index 80149e6876..0b6b1794fb 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,193 +1,192 @@
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
 package org.hibernate.test.jpa;
 
 import javax.persistence.EntityNotFoundException;
 import java.io.Serializable;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.event.internal.DefaultAutoFlushEventListener;
+import org.hibernate.event.internal.DefaultFlushEntityEventListener;
+import org.hibernate.event.internal.DefaultFlushEventListener;
+import org.hibernate.event.internal.DefaultPersistEventListener;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.PersistEventListener;
-import org.hibernate.event.internal.DefaultAutoFlushEventListener;
-import org.hibernate.event.internal.DefaultFlushEntityEventListener;
-import org.hibernate.event.internal.DefaultFlushEventListener;
-import org.hibernate.event.internal.DefaultPersistEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * An abstract test for all JPA spec related tests.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractJPATest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/Part.hbm.xml", "jpa/Item.hbm.xml", "jpa/MyEntity.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		cfg.setEntityNotFoundDelegate( new JPAEntityNotFoundDelegate() );
 	}
 
 	@Override
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
-		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+		builder.with(
 				new Integrator() {
 
-				    @Override
+					@Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
-                        integrate(serviceRegistry);
+						integrate( serviceRegistry );
 					}
 
-                    @Override
-				    public void integrate( MetadataImplementor metadata,
-				                           SessionFactoryImplementor sessionFactory,
-				                           SessionFactoryServiceRegistry serviceRegistry ) {
-				        integrate(serviceRegistry);
-				    }
-
-				    private void integrate( SessionFactoryServiceRegistry serviceRegistry ) {
-                        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
-                        eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
-                        eventListenerRegistry.setListeners(
-                                EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners()
-                        );
-                        eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
-                        eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
-                        eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
-				    }
+					@Override
+					public void integrate(
+							MetadataImplementor metadata,
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						integrate( serviceRegistry );
+					}
+
+					private void integrate(SessionFactoryServiceRegistry serviceRegistry) {
+						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+						eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
+						eventListenerRegistry.setListeners(
+								EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners()
+						);
+						eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
+						eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
+						eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
+					}
 
 					@Override
 					public void disintegrate(
 							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		// no second level caching
 		return null;
 	}
 
 
 	// mimic specific exception aspects of the JPA environment ~~~~~~~~~~~~~~~~
 
 	private static class JPAEntityNotFoundDelegate implements EntityNotFoundDelegate {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	// mimic specific event aspects of the JPA environment ~~~~~~~~~~~~~~~~~~~~
 
 	protected PersistEventListener[] buildPersistEventListeners() {
 		return new PersistEventListener[] { new JPAPersistEventListener() };
 	}
 
 	protected PersistEventListener[] buildPersisOnFlushEventListeners() {
 		return new PersistEventListener[] { new JPAPersistOnFlushEventListener() };
 	}
 
 	protected AutoFlushEventListener[] buildAutoFlushEventListeners() {
 		return new AutoFlushEventListener[] { JPAAutoFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEventListener[] buildFlushEventListeners() {
 		return new FlushEventListener[] { JPAFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEntityEventListener[] buildFlushEntityEventListeners() {
 		return new FlushEntityEventListener[] { new JPAFlushEntityEventListener() };
 	}
 
 	public static class JPAPersistEventListener extends DefaultPersistEventListener {
 		// overridden in JPA impl for entity callbacks...
 	}
 
 	public static class JPAPersistOnFlushEventListener extends JPAPersistEventListener {
 		@Override
         protected CascadingAction getCascadeAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 	}
 
 	public static class JPAAutoFlushEventListener extends DefaultAutoFlushEventListener {
 		// not sure why EM code has this ...
 		public static final AutoFlushEventListener INSTANCE = new JPAAutoFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEventListener extends DefaultFlushEventListener {
 		// not sure why EM code has this ...
 		public static final FlushEventListener INSTANCE = new JPAFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEntityEventListener extends DefaultFlushEntityEventListener {
 		// in JPA, used mainly for preUpdate callbacks...
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
index 907fe9d53c..2b865d0915 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
@@ -1,212 +1,211 @@
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
 package org.hibernate.test.keymanytoone.bidir.component;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.event.internal.DefaultLoadEventListener;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
-import org.hibernate.event.internal.DefaultLoadEventListener;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
 import org.junit.Test;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"unchecked"})
 public class EagerKeyManyToOneTest extends BaseCoreFunctionalTestCase {
 	@Override
     public String[] getMappings() {
 		return new String[] { "keymanytoone/bidir/component/EagerMapping.hbm.xml" };
 	}
 
 	@Override
     public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Override
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
-		super.applyServices( serviceRegistry );
-
-		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+		super.prepareBootstrapRegistryBuilder( builder );
+		builder.with(
 				new Integrator() {
 
 				    @Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
                         integrate(serviceRegistry);
 					}
 
                     @Override
 				    public void integrate( MetadataImplementor metadata,
 				                           SessionFactoryImplementor sessionFactory,
 				                           SessionFactoryServiceRegistry serviceRegistry ) {
                         integrate(serviceRegistry);
 				    }
 
                     private void integrate( SessionFactoryServiceRegistry serviceRegistry ) {
                         serviceRegistry.getService( EventListenerRegistry.class ).prependListeners(EventType.LOAD,
                                                                                                    new CustomLoadListener());
                     }
 
 					@Override
 					public void disintegrate(
 							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Test
 	public void testSaveCascadedToKeyManyToOne() {
 		sessionFactory().getStatistics().clear();
 
 		// test cascading a save to an association with a key-many-to-one which refers to a
 		// just saved entity
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.flush();
 		assertEquals( 2, sessionFactory().getStatistics().getEntityInsertCount() );
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadingStrategies() {
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		cust = ( Customer ) s.createQuery( "from Customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders as o join fetch o.id.customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createCriteria( Customer.class ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2277")
 	public void testLoadEntityWithEagerFetchingToKeyManyToOneReferenceBackToSelf() {
 		sessionFactory().getStatistics().clear();
 
 		// long winded method name to say that this is a test specifically for HHH-2277 ;)
 		// essentially we have a bidirectional association where one side of the
 		// association is actually part of a composite PK.
 		//
 		// The way these are mapped causes the problem because both sides
 		// are defined as eager which leads to the infinite loop; if only
 		// one side is marked as eager, then all is ok.  In other words the
 		// problem arises when both pieces of instance data are coming from
 		// the same result set.  This is because no "entry" can be placed
 		// into the persistence context for the association with the
 		// composite key because we are in the process of trying to build
 		// the composite-id instance
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			cust = ( Customer ) s.get( Customer.class, cust.getId() );
 		}
 		catch( OverflowCondition overflow ) {
 			fail( "get()/load() caused overflow condition" );
 		}
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private static class OverflowCondition extends RuntimeException {
 	}
 
 	private static class CustomLoadListener extends DefaultLoadEventListener {
 		private int internalLoadCount = 0;
 		@Override
         public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
 			if ( LoadEventListener.INTERNAL_LOAD_EAGER.getName().equals( loadType.getName() ) ) {
 				internalLoadCount++;
 				if ( internalLoadCount > 10 ) {
 					throw new OverflowCondition();
 				}
 			}
 			super.onLoad( event, loadType );
 			internalLoadCount--;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 00a09106c8..4146da5e5e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
@@ -1,220 +1,221 @@
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
 package org.hibernate.test.jdbc.proxies;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.Statement;
 
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
 import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
 import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingBatchObserver;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class BatchingTest extends BaseUnitTestCase implements BatchKey {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( ConnectionProviderBuilder.getConnectionProviderProperties() )
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
 				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Override
 	public int getBatchedStatementCount() {
 		return 1;
 	}
 
 	@Override
 	public Expectation getExpectation() {
 		return Expectations.BASIC;
 	}
 
 	@Test
 	public void testNonBatchingUsage() throws Exception {
 		final TransactionContext transactionContext = new TransactionContextImpl(
 				new TransactionEnvironmentImpl( serviceRegistry )
 		);
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( -1 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		assertTrue( "unexpected Batch impl", NonBatchingBatch.class.isInstance( insertBatch ) );
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 	@Test
 	public void testBatchingUsage() throws Exception {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( 2 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 		assertTrue( "unexpected Batch impl", BatchingBatch.class.isInstance( insertBatch ) );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		PreparedStatement insert2 = insertBatch.getBatchStatement( insertSql, false );
 		assertSame( insert, insert2 );
 		insert = insert2;
 		insert.setLong( 1, 2 );
 		insert.setString( 2, "another name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
index 9cb6d6fa80..882c82e03a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -1,288 +1,289 @@
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
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.testing.cache.CachingRegionFactory;
 import org.hibernate.tool.hbm2ddl.ConnectionHelper;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
 	private DriverManagerConnectionProviderImpl acmeProvider;
 	private DriverManagerConnectionProviderImpl jbossProvider;
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private SessionFactoryImplementor sessionFactory;
 
 	@Before
 	public void setUp() {
 		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider( "acme" );
 		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider( "jboss" );
 		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = new AbstractMultiTenantConnectionProvider() {
 			@Override
 			protected ConnectionProvider getAnyConnectionProvider() {
 				return acmeProvider;
 			}
 
 			@Override
 			protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
 				if ( "acme".equals( tenantIdentifier ) ) {
 					return acmeProvider;
 				}
 				else if ( "jboss".equals( tenantIdentifier ) ) {
 					return jbossProvider;
 				}
 				throw new HibernateException( "Unknown tenant identifier" );
 			}
 		};
 
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.addAnnotatedClass( Customer.class );
 
 		cfg.buildMappings();
 		RootClass meta = (RootClass) cfg.getClassMapping( Customer.class.getName() );
 		meta.setCacheConcurrencyStrategy( "read-write" );
 
 		// do the acme export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = acmeProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						acmeProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute(		 // so stupid...
 						   false,	 // do not script the export (write it to file)
 						   true,	 // do run it against the database
 						   false,	 // do not *just* perform the drop
 						   false	// do not *just* perform the create
 		);
 
 		// do the jboss export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = jbossProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						jbossProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute( 		// so stupid...
 				false, 	// do not script the export (write it to file)
 				true, 	// do run it against the database
 				false, 	// do not *just* perform the drop
 				false	// do not *just* perform the create
 		);
 
-		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder( cfg.getProperties() )
+		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder()
+				.applySettings( cfg.getProperties() )
 				.addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
 				.buildServiceRegistry();
 
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 		if ( jbossProvider != null ) {
 			jbossProvider.stop();
 		}
 		if ( acmeProvider != null ) {
 			acmeProvider.stop();
 		}
 	}
 
 	@Test
 	public void testBasicExpectedBehavior() {
 		Session session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		try {
 			session.beginTransaction();
 			Customer check = (Customer) session.get( Customer.class, steve.getId() );
 			Assert.assertNull( "tenancy not properly isolated", check );
 		}
 		finally {
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testSameIdentifiers() {
 		// create a customer 'steve' in jboss
 		Session session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		// now, create a customer 'john' in acme
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		session.beginTransaction();
 		Customer john = new Customer( 1L, "john" );
 		session.save( john );
 		session.getTransaction().commit();
 		session.close();
 
 		sessionFactory.getStatisticsImplementor().clear();
 
 		// make sure we get the correct people back, from cache
 		// first, jboss
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		// make sure the same works from datastore too
 		sessionFactory.getStatisticsImplementor().clear();
 		sessionFactory.getCache().evictEntityRegions();
 		// first jboss
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		session.beginTransaction();
 		session.delete( john );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
index 6e5b8cc7f4..d2f55507a8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
@@ -1,100 +1,104 @@
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
 package org.hibernate.test.service;
 
 import java.util.Properties;
 
 import org.junit.Test;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceBootstrappingTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicBuild() {
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder(
-				ConnectionProviderBuilder.getConnectionProviderProperties()
-		).buildServiceRegistry();
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
+				.buildServiceRegistry();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertFalse( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithLogging() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.put( Environment.SHOW_SQL, "true" );
 
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( props ).buildServiceRegistry();
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( props )
+				.buildServiceRegistry();
 
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithServiceOverride() {
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( ConnectionProviderBuilder.getConnectionProviderProperties() )
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
 				.buildServiceRegistry();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.setProperty( Environment.DIALECT, H2Dialect.class.getName() );
 
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( props )
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( props )
 				.addService( ConnectionProvider.class, new UserSuppliedConnectionProviderImpl() )
 				.buildServiceRegistry();
 		jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( UserSuppliedConnectionProviderImpl.class ) );
 
 		serviceRegistry.destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index c0f77af919..7490ed38e3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
@@ -1,141 +1,142 @@
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
 package org.hibernate.test.transaction.jdbc;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 public class TestExpectedUsage extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( ConnectionProviderBuilder.getConnectionProviderProperties() )
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
 				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		};
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
index ba5a43246e..23100122b7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
@@ -1,164 +1,166 @@
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
 package org.hibernate.test.transaction.jta;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.internal.ServiceProxy;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testing transaction handling when the JTA transaction facade is the driver.
  *
  * @author Steve Ebersole
  */
 public class BasicDrivingTest extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
 		TestingJtaBootstrap.prepare( configValues );
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configValues ).buildServiceRegistry();
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( configValues )
+				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		assertTrue( txn.isInitiator() );
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
index 7c106b6e9f..e0a38ec07c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
@@ -1,176 +1,178 @@
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
 package org.hibernate.test.transaction.jta;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 import javax.transaction.TransactionManager;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testing transaction facade handling when the transaction is being driven by something other than the facade.
  *
  * @author Steve Ebersole
  */
 public class ManagedDrivingTest extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configValues ).buildServiceRegistry();
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( configValues )
+				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_STATEMENT;
 			}
 		};
 
 		final TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		final JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		JtaPlatform instance = serviceRegistry.getService( JtaPlatform.class );
 		TransactionManager transactionManager = instance.retrieveTransactionManager();
 
 		// start the cmt
 		transactionManager.begin();
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 		assertFalse( txn.isInitiator() );
 		connection = logicalConnection.getShareableConnectionProxy();
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// since txn is not a driver, nothing should have changed...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 0, transactionObserver.getBeforeCompletions() );
 			assertEquals( 0, transactionObserver.getAfterCompletions() );
 
 			transactionManager.commit();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 1, transactionObserver.getBeforeCompletions() );
 			assertEquals( 1, transactionObserver.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 50232ef06a..86ae02486f 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1628 +1,1633 @@
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
 package org.hibernate.ejb;
 
 import javax.naming.BinaryRefAddr;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.Referenceable;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitInfo;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectOutput;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.annotations.reflection.XMLContext;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.ejb.cfg.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.ejb.connection.InjectedDataSourceConnectionProvider;
 import org.hibernate.ejb.event.JpaIntegrator;
 import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
 import org.hibernate.ejb.packaging.JarVisitorFactory;
 import org.hibernate.ejb.packaging.NamedInputStream;
 import org.hibernate.ejb.packaging.NativeScanner;
 import org.hibernate.ejb.packaging.PersistenceMetadata;
 import org.hibernate.ejb.packaging.PersistenceXmlLoader;
 import org.hibernate.ejb.packaging.Scanner;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LogHelper;
 import org.hibernate.ejb.util.NamingHelper;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
-import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 
 /**
  * Allow a fine tuned configuration of an EJB 3.0 EntityManagerFactory
  *
  * A Ejb3Configuration object is only guaranteed to create one EntityManagerFactory.
  * Multiple usage of {@link #buildEntityManagerFactory()} is not guaranteed.
  *
  * After #buildEntityManagerFactory() has been called, you no longer can change the configuration
  * state (no class adding, no property change etc)
  *
  * When serialized / deserialized or retrieved from the JNDI, you no longer can change the
  * configuration state (no class adding, no property change etc)
  *
  * Putting the configuration in the JNDI is an expensive operation that requires a partial
  * serialization
  *
  * @author Emmanuel Bernard
  *
  * @deprecated Direct usage of this class has never been supported.  Instead, the application should obtain reference
  * to the {@link EntityManagerFactory} as outlined in the JPA specification, section <i>7.3 Obtaining an Entity
  * Manager Factory</i> based on runtime environment.  Additionally this class will be removed in Hibernate release
  * 5.0 for the same reasoning outlined on {@link Configuration} due to move towards new
  * {@link org.hibernate.SessionFactory} building methodology.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6181">HHH-6181</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6159">HHH-6159</a> for details
  */
 @Deprecated
+@SuppressWarnings( {"JavaDoc"})
 public class Ejb3Configuration implements Serializable, Referenceable {
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			Ejb3Configuration.class.getName()
 	);
 	private static final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 	private static final String PARSED_MAPPING_DOMS = "hibernate.internal.mapping_doms";
 
 	private static EntityNotFoundDelegate ejb3EntityNotFoundDelegate = new Ejb3EntityNotFoundDelegate();
 	private static Configuration DEFAULT_CONFIGURATION = new Configuration();
 
 	private static class Ejb3EntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	private String persistenceUnitName;
 	private String cfgXmlResource;
 
 	private Configuration cfg;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private PersistenceUnitTransactionType transactionType;
 	private boolean discardOnClose;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient ClassLoader overridenClassLoader;
 	private boolean isConfigurationProcessed = false;
 
 
 	public Ejb3Configuration() {
 		cfg = new Configuration();
 		cfg.setEntityNotFoundDelegate( ejb3EntityNotFoundDelegate );
 	}
 
 	/**
 	 * Used to inject a datasource object as the connection provider.
 	 * If used, be sure to <b>not override</b> the hibernate.connection.provider_class
 	 * property
 	 */
 	@SuppressWarnings({ "JavaDoc", "unchecked" })
 	public void setDataSource(DataSource ds) {
 		if ( ds != null ) {
 			cfg.getProperties().put( Environment.DATASOURCE, ds );
 			this.setProperty( Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName() );
 		}
 	}
 
 	/**
 	 * create a factory from a parsed persistence.xml
 	 * Especially the scanning of classes and additional jars is done already at this point.
 	 * <p/>
 	 * NOTE: public only for unit testing purposes; not a public API!
 	 *
 	 * @param metadata The information parsed from the persistence.xml
 	 * @param overridesIn Any explicitly passed config settings
 	 *
 	 * @return this
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceMetadata metadata, Map overridesIn) {
         LOG.debugf("Creating Factory: %s", metadata.getName());
 
 		Map overrides = new HashMap();
 		if ( overridesIn != null ) {
 			overrides.putAll( overridesIn );
 		}
 
 		Map workingVars = new HashMap();
 		workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, metadata.getName() );
 		this.persistenceUnitName = metadata.getName();
 
 		if ( StringHelper.isNotEmpty( metadata.getJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getJtaDatasource() );
 		}
 		else if ( StringHelper.isNotEmpty( metadata.getNonJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getNonJtaDatasource() );
 		}
 		else {
 			final String driver = (String) metadata.getProps().get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				this.setProperty( Environment.DRIVER, driver );
 			}
 			final String url = (String) metadata.getProps().get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				this.setProperty( Environment.URL, url );
 			}
 			final String user = (String) metadata.getProps().get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				this.setProperty( Environment.USER, user );
 			}
 			final String pass = (String) metadata.getProps().get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				this.setProperty( Environment.PASS, pass );
 			}
 		}
 		defineTransactionType( metadata.getTransactionType(), workingVars );
 		if ( metadata.getClasses().size() > 0 ) {
 			workingVars.put( AvailableSettings.CLASS_NAMES, metadata.getClasses() );
 		}
 		if ( metadata.getPackages().size() > 0 ) {
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, metadata.getPackages() );
 		}
 		if ( metadata.getMappingFiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, metadata.getMappingFiles() );
 		}
 		if ( metadata.getHbmfiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.HBXML_FILES, metadata.getHbmfiles() );
 		}
 
 		Properties props = new Properties();
 		props.putAll( metadata.getProps() );
 
 		// validation factory
 		final Object validationFactory = overrides.get( AvailableSettings.VALIDATION_FACTORY );
 		if ( validationFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validationFactory );
 			props.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 		}
 		overrides.remove( AvailableSettings.VALIDATION_FACTORY );
 
 		// validation-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.VALIDATION_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getValidationMode() != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, metadata.getValidationMode() );
 			}
 			overrides.remove( AvailableSettings.VALIDATION_MODE );
 		}
 
 		// shared-cache-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.SHARED_CACHE_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getSharedCacheMode() != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, metadata.getSharedCacheMode() );
 			}
 			overrides.remove( AvailableSettings.SHARED_CACHE_MODE );
 		}
 
 		for ( Map.Entry entry : (Set<Map.Entry>) overrides.entrySet() ) {
 			Object value = entry.getValue();
 			props.put( entry.getKey(), value == null ? "" :  value ); //alter null, not allowed in properties
 		}
 
 		configure( props, workingVars );
 		return this;
 	}
 
 	/**
 	 * Build the configuration from an entity manager name and given the
 	 * appropriate extra properties. Those properties override the one get through
 	 * the persistence.xml file.
 	 * If the persistence unit name is not found or does not match the Persistence Provider, null is returned
 	 *
 	 * This method is used in a non managed environment
 	 *
 	 * @param persistenceUnitName persistence unit name
 	 * @param integration properties passed to the persistence provider
 	 *
 	 * @return configured Ejb3Configuration or null if no persistence unit match
 	 *
 	 * @see HibernatePersistence#createEntityManagerFactory(String, java.util.Map)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(String persistenceUnitName, Map integration) {
 		try {
             LOG.debugf("Look up for persistence unit: %s", persistenceUnitName);
 			integration = integration == null ?
 					CollectionHelper.EMPTY_MAP :
 					Collections.unmodifiableMap( integration );
 			Enumeration<URL> xmls = Thread.currentThread()
 					.getContextClassLoader()
 					.getResources( "META-INF/persistence.xml" );
             if (!xmls.hasMoreElements()) LOG.unableToFindPersistenceXmlInClasspath();
 			while ( xmls.hasMoreElements() ) {
 				URL url = xmls.nextElement();
                 LOG.trace("Analyzing persistence.xml: " + url);
 				List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy(
 						url,
 						integration,
 						cfg.getEntityResolver(),
 						PersistenceUnitTransactionType.RESOURCE_LOCAL );
 				for ( PersistenceMetadata metadata : metadataFiles ) {
                     LOG.trace(metadata);
 
 					if ( metadata.getProvider() == null || IMPLEMENTATION_NAME.equalsIgnoreCase(
 							metadata.getProvider()
 					) ) {
 						//correct provider
 
 						//lazy load the scanner to avoid unnecessary IOExceptions
 						Scanner scanner = null;
 						URL jarURL = null;
 						if ( metadata.getName() == null ) {
 							scanner = buildScanner( metadata.getProps(), integration );
 							jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							metadata.setName( scanner.getUnqualifiedJarName(jarURL) );
 						}
 						if ( persistenceUnitName == null && xmls.hasMoreElements() ) {
 							throw new PersistenceException( "No name provided and several persistence units found" );
 						}
 						else if ( persistenceUnitName == null || metadata.getName().equals( persistenceUnitName ) ) {
 							if (scanner == null) {
 								scanner = buildScanner( metadata.getProps(), integration );
 								jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							}
 							//scan main JAR
 							ScanningContext mainJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.url( jarURL )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( mainJarScanCtx, metadata.getProps(), integration,
 																				metadata.getExcludeUnlistedClasses() );
 							addMetadataFromScan( mainJarScanCtx, metadata );
 
 							ScanningContext otherJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( otherJarScanCtx, metadata.getProps(), integration,
 																				false );
 							for ( String jarFile : metadata.getJarFiles() ) {
 								otherJarScanCtx.url( JarVisitorFactory.getURLFromPath( jarFile ) );
 								addMetadataFromScan( otherJarScanCtx, metadata );
 							}
 							return configure( metadata, integration );
 						}
 					}
 				}
 			}
 			return null;
 		}
 		catch (Exception e) {
 			if ( e instanceof PersistenceException) {
 				throw (PersistenceException) e;
 			}
 			else {
 				throw new PersistenceException( getExceptionHeader() + "Unable to configure EntityManagerFactory", e );
 			}
 		}
 	}
 
 	private Scanner buildScanner(Properties properties, Map<?,?> integration) {
 		//read the String or Instance from the integration map first and use the properties as a backup.
 		Object scanner = integration.get( AvailableSettings.SCANNER );
 		if (scanner == null) {
 			scanner = properties.getProperty( AvailableSettings.SCANNER );
 		}
 		if (scanner != null) {
 			Class<?> scannerClass;
 			if ( scanner instanceof String ) {
 				try {
 					scannerClass = ReflectHelper.classForName( (String) scanner, this.getClass() );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new PersistenceException(  "Cannot find scanner class. " + AvailableSettings.SCANNER + "=" + scanner, e );
 				}
 			}
 			else if (scanner instanceof Class) {
 				scannerClass = (Class<? extends Scanner>) scanner;
 			}
 			else if (scanner instanceof Scanner) {
 				return (Scanner) scanner;
 			}
 			else {
 				throw new PersistenceException(  "Scanner class configuration error: unknown type on the property. " + AvailableSettings.SCANNER );
 			}
 			try {
 				return (Scanner) scannerClass.newInstance();
 			}
 			catch ( InstantiationException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 			catch ( IllegalAccessException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 		}
 		else {
 			return new NativeScanner();
 		}
 	}
 
 	private static class ScanningContext {
 		//boolean excludeUnlistedClasses;
 		private Scanner scanner;
 		private URL url;
 		private List<String> explicitMappingFiles;
 		private boolean detectClasses;
 		private boolean detectHbmFiles;
 		private boolean searchOrm;
 
 		public ScanningContext scanner(Scanner scanner) {
 			this.scanner = scanner;
 			return this;
 		}
 
 		public ScanningContext url(URL url) {
 			this.url = url;
 			return this;
 		}
 
 		public ScanningContext explicitMappingFiles(List<String> explicitMappingFiles) {
 			this.explicitMappingFiles = explicitMappingFiles;
 			return this;
 		}
 
 		public ScanningContext detectClasses(boolean detectClasses) {
 			this.detectClasses = detectClasses;
 			return this;
 		}
 
 		public ScanningContext detectHbmFiles(boolean detectHbmFiles) {
 			this.detectHbmFiles = detectHbmFiles;
 			return this;
 		}
 
 		public ScanningContext searchOrm(boolean searchOrm) {
 			this.searchOrm = searchOrm;
 			return this;
 		}
 	}
 
 	private static void addMetadataFromScan(ScanningContext scanningContext, PersistenceMetadata metadata) throws IOException {
 		List<String> classes = metadata.getClasses();
 		List<String> packages = metadata.getPackages();
 		List<NamedInputStream> hbmFiles = metadata.getHbmfiles();
 		List<String> mappingFiles = metadata.getMappingFiles();
 		addScannedEntries( scanningContext, classes, packages, hbmFiles, mappingFiles );
 	}
 
 	private static void addScannedEntries(ScanningContext scanningContext, List<String> classes, List<String> packages, List<NamedInputStream> hbmFiles, List<String> mappingFiles) throws IOException {
 		Scanner scanner = scanningContext.scanner;
 		if (scanningContext.detectClasses) {
 			Set<Class<? extends Annotation>> annotationsToExclude = new HashSet<Class<? extends Annotation>>(3);
 			annotationsToExclude.add( Entity.class );
 			annotationsToExclude.add( MappedSuperclass.class );
 			annotationsToExclude.add( Embeddable.class );
 			Set<Class<?>> matchingClasses = scanner.getClassesInJar( scanningContext.url, annotationsToExclude );
 			for (Class<?> clazz : matchingClasses) {
 				classes.add( clazz.getName() );
 			}
 
 			Set<Package> matchingPackages = scanner.getPackagesInJar( scanningContext.url, new HashSet<Class<? extends Annotation>>(0) );
 			for (Package pkg : matchingPackages) {
 				packages.add( pkg.getName() );
 			}
 		}
 		Set<String> patterns = new HashSet<String>();
 		if (scanningContext.searchOrm) {
 			patterns.add( META_INF_ORM_XML );
 		}
 		if (scanningContext.detectHbmFiles) {
 			patterns.add( "**/*.hbm.xml" );
 		}
 		if ( mappingFiles != null) patterns.addAll( mappingFiles );
 		if (patterns.size() !=0) {
 			Set<NamedInputStream> files = scanner.getFilesInJar( scanningContext.url, patterns );
 			for (NamedInputStream file : files) {
 				hbmFiles.add( file );
 				if (mappingFiles != null) mappingFiles.remove( file.getName() );
 			}
 		}
 	}
 
 	/**
 	 * Process configuration from a PersistenceUnitInfo object; typically called by the container
 	 * via {@link javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory}.
 	 * In Hibernate EM, this correlates to {@link HibernatePersistence#createContainerEntityManagerFactory}
 	 *
 	 * @param info The persistence unit info passed in by the container (usually from processing a persistence.xml).
 	 * @param integration The map of integration properties from the container to configure the provider.
 	 *
 	 * @return this
 	 *
 	 * @see HibernatePersistence#createContainerEntityManagerFactory
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceUnitInfo info, Map integration) {
         if (LOG.isDebugEnabled()) LOG.debugf("Processing %s", LogHelper.logPersistenceUnitInfo(info));
         else LOG.processingPersistenceUnitInfoName(info.getPersistenceUnitName());
 
 		// Spec says the passed map may be null, so handle that to make further processing easier...
 		integration = integration != null ? Collections.unmodifiableMap( integration ) : CollectionHelper.EMPTY_MAP;
 
 		// See if we (Hibernate) are the persistence provider
 		String provider = (String) integration.get( AvailableSettings.PROVIDER );
 		if ( provider == null ) {
 			provider = info.getPersistenceProviderClassName();
 		}
 		if ( provider != null && ! provider.trim().startsWith( IMPLEMENTATION_NAME ) ) {
             LOG.requiredDifferentProvider(provider);
 			return null;
 		}
 
 		// set the classloader, passed in by the container in info, to set as the TCCL so that
 		// Hibernate uses it to properly resolve class references.
 		if ( info.getClassLoader() == null ) {
 			throw new IllegalStateException(
 					"[PersistenceUnit: " + info.getPersistenceUnitName() == null ? "" : info.getPersistenceUnitName()
 							+ "] " + "PersistenceUnitInfo.getClassLoader() id null" );
 		}
 		Thread thread = Thread.currentThread();
 		ClassLoader contextClassLoader = thread.getContextClassLoader();
 		boolean sameClassLoader = info.getClassLoader().equals( contextClassLoader );
 		if ( ! sameClassLoader ) {
 			overridenClassLoader = info.getClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		else {
 			overridenClassLoader = null;
 		}
 
 		// Best I can tell, 'workingVars' is some form of additional configuration contract.
 		// But it does not correlate 1-1 to EMF/SF settings.  It really is like a set of de-typed
 		// additional configuration info.  I think it makes better sense to define this as an actual
 		// contract if that was in fact the intent; the code here is pretty confusing.
 		try {
 			Map workingVars = new HashMap();
 			workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, info.getPersistenceUnitName() );
 			this.persistenceUnitName = info.getPersistenceUnitName();
 			List<String> entities = new ArrayList<String>( 50 );
 			if ( info.getManagedClassNames() != null ) entities.addAll( info.getManagedClassNames() );
 			List<NamedInputStream> hbmFiles = new ArrayList<NamedInputStream>();
 			List<String> packages = new ArrayList<String>();
 			List<String> xmlFiles = new ArrayList<String>( 50 );
 			List<XmlDocument> xmlDocuments = new ArrayList<XmlDocument>( 50 );
 			if ( info.getMappingFileNames() != null ) {
 				xmlFiles.addAll( info.getMappingFileNames() );
 			}
 			//Should always be true if the container is not dump
 			boolean searchForORMFiles = ! xmlFiles.contains( META_INF_ORM_XML );
 
 			ScanningContext context = new ScanningContext();
 			final Properties copyOfProperties = (Properties) info.getProperties().clone();
 			ConfigurationHelper.overrideProperties( copyOfProperties, integration );
 			context.scanner( buildScanner( copyOfProperties, integration ) )
 					.searchOrm( searchForORMFiles )
 					.explicitMappingFiles( null ); //URLs provided by the container already
 
 			//context for other JARs
 			setDetectedArtifactsOnScanningContext(context, info.getProperties(), null, false );
 			for ( URL jar : info.getJarFileUrls() ) {
 				context.url(jar);
 				scanForClasses( context, packages, entities, hbmFiles );
 			}
 
 			//main jar
 			context.url( info.getPersistenceUnitRootUrl() );
 			setDetectedArtifactsOnScanningContext( context, info.getProperties(), null, info.excludeUnlistedClasses() );
 			scanForClasses( context, packages, entities, hbmFiles );
 
 			Properties properties = info.getProperties() != null ? info.getProperties() : new Properties();
 			ConfigurationHelper.overrideProperties( properties, integration );
 
 			//FIXME entities is used to enhance classes and to collect annotated entities this should not be mixed
 			//fill up entities with the on found in xml files
 			addXMLEntities( xmlFiles, info, entities, xmlDocuments );
 
 			//FIXME send the appropriate entites.
 			if ( "true".equalsIgnoreCase( properties.getProperty( AvailableSettings.USE_CLASS_ENHANCER ) ) ) {
 				info.addTransformer( new InterceptFieldClassFileTransformer( entities ) );
 			}
 
 			workingVars.put( AvailableSettings.CLASS_NAMES, entities );
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, packages );
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, xmlFiles );
 			workingVars.put( PARSED_MAPPING_DOMS, xmlDocuments );
 
 			if ( hbmFiles.size() > 0 ) {
 				workingVars.put( AvailableSettings.HBXML_FILES, hbmFiles );
 			}
 
 			// validation factory
 			final Object validationFactory = integration.get( AvailableSettings.VALIDATION_FACTORY );
 			if ( validationFactory != null ) {
 				BeanValidationIntegrator.validateFactory( validationFactory );
 				properties.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 			}
 
 			// validation-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.VALIDATION_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 				}
 				else if ( info.getValidationMode() != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, info.getValidationMode().name() );
 				}
 			}
 
 			// shared-cache-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.SHARED_CACHE_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 				}
 				else if ( info.getSharedCacheMode() != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, info.getSharedCacheMode().name() );
 				}
 			}
 
 			//datasources
 			Boolean isJTA = null;
 			boolean overridenDatasource = false;
 			if ( integration.containsKey( AvailableSettings.JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				isJTA = Boolean.TRUE;
 			}
 			if ( integration.containsKey( AvailableSettings.NON_JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.NON_JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				if (isJTA == null) isJTA = Boolean.FALSE;
 			}
 
 			if ( ! overridenDatasource && ( info.getJtaDataSource() != null || info.getNonJtaDataSource() != null ) ) {
 				isJTA = info.getJtaDataSource() != null ? Boolean.TRUE : Boolean.FALSE;
 				this.setDataSource(
 						isJTA ? info.getJtaDataSource() : info.getNonJtaDataSource()
 				);
 				this.setProperty(
 						Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName()
 				);
 			}
 			/*
 			 * If explicit type => use it
 			 * If a JTA DS is used => JTA transaction,
 			 * if a non JTA DS is used => RESOURCe_LOCAL
 			 * if none, set to JavaEE default => JTA transaction
 			 */
 			PersistenceUnitTransactionType transactionType = info.getTransactionType();
 			if (transactionType == null) {
 				if (isJTA == Boolean.TRUE) {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 				else if ( isJTA == Boolean.FALSE ) {
 					transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 				}
 				else {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 			}
 			defineTransactionType( transactionType, workingVars );
 			configure( properties, workingVars );
 		}
 		finally {
 			//After EMF, set the CCL back
 			if ( ! sameClassLoader ) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Processes {@code xmlFiles} argument and populates:<ul>
 	 * <li>the {@code entities} list with encountered classnames</li>
 	 * <li>the {@code xmlDocuments} list with parsed/validated {@link XmlDocument} corrolary to each xml file</li>
 	 * </ul>
 	 *
 	 * @param xmlFiles The XML resource names; these will be resolved by classpath lookup and parsed/validated.
 	 * @param info The PUI
 	 * @param entities (output) The names of all encountered "mapped" classes
 	 * @param xmlDocuments (output) The list of {@link XmlDocument} instances of each entry in {@code xmlFiles}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	private void addXMLEntities(
 			List<String> xmlFiles,
 			PersistenceUnitInfo info,
 			List<String> entities,
 			List<XmlDocument> xmlDocuments) {
 		//TODO handle inputstream related hbm files
 		ClassLoader classLoaderToUse = info.getNewTempClassLoader();
 		if ( classLoaderToUse == null ) {
             LOG.persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 			return;
 		}
 		for ( final String xmlFile : xmlFiles ) {
 			final InputStream fileInputStream = classLoaderToUse.getResourceAsStream( xmlFile );
 			if ( fileInputStream == null ) {
                 LOG.unableToResolveMappingFile(xmlFile);
 				continue;
 			}
 			final InputSource inputSource = new InputSource( fileInputStream );
 
 			XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument(
 					cfg.getEntityResolver(),
 					inputSource,
 					new OriginImpl( "persistence-unit-info", xmlFile )
 			);
 			xmlDocuments.add( metadataXml );
 			try {
 				final Element rootElement = metadataXml.getDocumentTree().getRootElement();
 				if ( rootElement != null && "entity-mappings".equals( rootElement.getName() ) ) {
 					Element element = rootElement.element( "package" );
 					String defaultPackage = element != null ? element.getTextTrim() : null;
 					List<Element> elements = rootElement.elements( "entity" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "mapped-superclass" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "embeddable" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 				}
 				else if ( rootElement != null && "hibernate-mappings".equals( rootElement.getName() ) ) {
 					//FIXME include hbm xml entities to enhance them but entities is also used to collect annotated entities
 				}
 			}
 			finally {
 				try {
 					fileInputStream.close();
 				}
 				catch (IOException ioe) {
                     LOG.unableToCloseInputStream(ioe);
 				}
 			}
 		}
 		xmlFiles.clear();
 	}
 
 	private void defineTransactionType(Object overridenTxType, Map workingVars) {
 		if ( overridenTxType == null ) {
 //			if ( transactionType == null ) {
 //				transactionType = PersistenceUnitTransactionType.JTA; //this is the default value
 //			}
 			//nothing to override
 		}
 		else if ( overridenTxType instanceof String ) {
 			transactionType = PersistenceXmlLoader.getTransactionType( (String) overridenTxType );
 		}
 		else if ( overridenTxType instanceof PersistenceUnitTransactionType ) {
 			transactionType = (PersistenceUnitTransactionType) overridenTxType;
 		}
 		else {
 			throw new PersistenceException( getExceptionHeader() +
 					AvailableSettings.TRANSACTION_TYPE + " of the wrong class type"
 							+ ": " + overridenTxType.getClass()
 			);
 		}
 
 	}
 
 	public Ejb3Configuration setProperty(String key, String value) {
 		cfg.setProperty( key, value );
 		return this;
 	}
 
 	/**
 	 * Set ScanningContext detectClasses and detectHbmFiles according to context
 	 */
 	private void setDetectedArtifactsOnScanningContext(ScanningContext context,
 													   Properties properties,
 													   Map overridenProperties,
 													   boolean excludeIfNotOverriden) {
 
 		boolean detectClasses = false;
 		boolean detectHbm = false;
 		String detectSetting = overridenProperties != null ?
 				(String) overridenProperties.get( AvailableSettings.AUTODETECTION ) :
 				null;
 		detectSetting = detectSetting == null ?
 				properties.getProperty( AvailableSettings.AUTODETECTION) :
 				detectSetting;
 		if ( detectSetting == null && excludeIfNotOverriden) {
 			//not overriden through HibernatePersistence.AUTODETECTION so we comply with the spec excludeUnlistedClasses
 			context.detectClasses( false ).detectHbmFiles( false );
 			return;
 		}
 
 		if ( detectSetting == null){
 			detectSetting = "class,hbm";
 		}
 		StringTokenizer st = new StringTokenizer( detectSetting, ", ", false );
 		while ( st.hasMoreElements() ) {
 			String element = (String) st.nextElement();
 			if ( "class".equalsIgnoreCase( element ) ) detectClasses = true;
 			if ( "hbm".equalsIgnoreCase( element ) ) detectHbm = true;
 		}
         LOG.debugf("Detect class: %s; detect hbm: %s", detectClasses, detectHbm);
 		context.detectClasses( detectClasses ).detectHbmFiles( detectHbm );
 	}
 
 	private void scanForClasses(ScanningContext scanningContext, List<String> packages, List<String> entities, List<NamedInputStream> hbmFiles) {
 		if (scanningContext.url == null) {
             LOG.containerProvidingNullPersistenceUnitRootUrl();
 			return;
 		}
 		try {
 			addScannedEntries( scanningContext, entities, packages, hbmFiles, null );
 		}
 		catch (RuntimeException e) {
 			throw new RuntimeException( "error trying to scan <jar-file>: " + scanningContext.url.toString(), e );
 		}
 		catch( IOException e ) {
 			throw new RuntimeException( "Error while reading " + scanningContext.url.toString(), e );
 		}
 	}
 
 	/**
 	 * create a factory from a list of properties and
 	 * HibernatePersistence.CLASS_NAMES -> Collection<String> (use to list the classes from config files
 	 * HibernatePersistence.PACKAGE_NAMES -> Collection<String> (use to list the mappings from config files
 	 * HibernatePersistence.HBXML_FILES -> Collection<InputStream> (input streams of hbm files)
 	 * HibernatePersistence.LOADED_CLASSES -> Collection<Class> (list of loaded classes)
 	 * <p/>
 	 * <b>Used by JBoss AS only</b>
 	 * @deprecated use the Java Persistence API
 	 */
 	// This is used directly by JBoss so don't remove until further notice.  bill@jboss.org
 	@Deprecated
     public EntityManagerFactory createEntityManagerFactory(Map workingVars) {
 		configure( workingVars );
 		return buildEntityManagerFactory();
 	}
 
 	/**
 	 * Process configuration and build an EntityManagerFactory <b>when</b> the configuration is ready
 	 * @deprecated
 	 */
 	@Deprecated
-    public EntityManagerFactory createEntityManagerFactory() {
+	public EntityManagerFactory createEntityManagerFactory() {
 		configure( cfg.getProperties(), new HashMap() );
 		return buildEntityManagerFactory();
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory() {
-		return buildEntityManagerFactory( new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry() );
+		return buildEntityManagerFactory( BootstrapServiceRegistryImpl.builder() );
 	}
 
-	public EntityManagerFactory buildEntityManagerFactory(ServiceRegistry serviceRegistry) {
+	public EntityManagerFactory buildEntityManagerFactory(BootstrapServiceRegistryImpl.Builder builder) {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
-		if (overridenClassLoader != null) {
+
+		if ( overridenClassLoader != null ) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
+
 		try {
-			configure( (Properties)null, null );
-			NamingHelper.bind(this);
-			serviceRegistry.getService( IntegratorService.class ).addIntegrator( new JpaIntegrator() );
+			final ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder(
+					builder.with( new JpaIntegrator() ).build()
+			);
+			serviceRegistryBuilder.applySettings( cfg.getProperties() );
+			configure( (Properties) null, null );
+			NamingHelper.bind( this );
 			return new EntityManagerFactoryImpl(
 					transactionType,
 					discardOnClose,
 					getSessionInterceptorClass( cfg.getProperties() ),
 					cfg,
-					serviceRegistry
+					serviceRegistryBuilder.buildServiceRegistry()
 			);
 		}
 		catch (HibernateException e) {
 			throw new PersistenceException( getExceptionHeader() + "Unable to build EntityManagerFactory", e );
 		}
 		finally {
 			if (thread != null) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 	}
 
 	private Class getSessionInterceptorClass(Properties properties) {
 		String sessionInterceptorClassname = (String) properties.get( AvailableSettings.SESSION_INTERCEPTOR );
 		if ( StringHelper.isNotEmpty( sessionInterceptorClassname ) ) {
 			try {
 				Class interceptorClass = ReflectHelper.classForName(
 						sessionInterceptorClassname, Ejb3Configuration.class
 				);
 				interceptorClass.newInstance();
 				return interceptorClass;
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to load "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
         }
         return null;
 	}
 
 	public Reference getReference() throws NamingException {
         LOG.debugf( "Returning a Reference to the Ejb3Configuration" );
 		ByteArrayOutputStream stream = new ByteArrayOutputStream();
 		ObjectOutput out = null;
 		byte[] serialized;
 		try {
 			out = new ObjectOutputStream( stream );
 			out.writeObject( this );
 			out.close();
 			serialized = stream.toByteArray();
 			stream.close();
 		}
 		catch (IOException e) {
 			NamingException namingException = new NamingException( "Unable to serialize Ejb3Configuration" );
 			namingException.setRootCause( e );
 			throw namingException;
 		}
 
 		return new Reference(
 				Ejb3Configuration.class.getName(),
 				new BinaryRefAddr("object", serialized ),
 				Ejb3ConfigurationObjectFactory.class.getName(),
 				null
 		);
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Ejb3Configuration configure(Map configValues) {
 		Properties props = new Properties();
 		if ( configValues != null ) {
 			props.putAll( configValues );
 			//remove huge non String elements for a clean props
 			props.remove( AvailableSettings.CLASS_NAMES );
 			props.remove( AvailableSettings.PACKAGE_NAMES );
 			props.remove( AvailableSettings.HBXML_FILES );
 			props.remove( AvailableSettings.LOADED_CLASSES );
 		}
 		return configure( props, configValues );
 	}
 
 	/**
 	 * Configures this configuration object from 2 distinctly different sources.
 	 *
 	 * @param properties These are the properties that came from the user, either via
 	 * a persistence.xml or explicitly passed in to one of our
 	 * {@link javax.persistence.spi.PersistenceProvider}/{@link HibernatePersistence} contracts.
 	 * @param workingVars Is collection of settings which need to be handled similarly
 	 * between the 2 main bootstrap methods, but where the values are determine very differently
 	 * by each bootstrap method.  todo eventually make this a contract (class/interface)
 	 *
 	 * @return The configured configuration
 	 *
 	 * @see HibernatePersistence
 	 */
 	private Ejb3Configuration configure(Properties properties, Map workingVars) {
 		//TODO check for people calling more than once this method (except buildEMF)
 		if (isConfigurationProcessed) return this;
 		isConfigurationProcessed = true;
 		Properties preparedProperties = prepareProperties( properties, workingVars );
 		if ( workingVars == null ) workingVars = CollectionHelper.EMPTY_MAP;
 
 		if ( preparedProperties.containsKey( AvailableSettings.CFG_FILE ) ) {
 			String cfgFileName = preparedProperties.getProperty( AvailableSettings.CFG_FILE );
 			cfg.configure( cfgFileName );
 		}
 
 		cfg.addProperties( preparedProperties ); //persistence.xml has priority over hibernate.cfg.xml
 
 		addClassesToSessionFactory( workingVars );
 
 		//processes specific properties
 		List<String> jaccKeys = new ArrayList<String>();
 
 
 		Interceptor defaultInterceptor = DEFAULT_CONFIGURATION.getInterceptor();
 		NamingStrategy defaultNamingStrategy = DEFAULT_CONFIGURATION.getNamingStrategy();
 
 		Iterator propertyIt = preparedProperties.keySet().iterator();
 		while ( propertyIt.hasNext() ) {
 			Object uncastObject = propertyIt.next();
 			//had to be safe
 			if ( uncastObject != null && uncastObject instanceof String ) {
 				String propertyKey = (String) uncastObject;
 				if ( propertyKey.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, true, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, false, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( propertyKey.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| propertyKey.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					jaccKeys.add( propertyKey );
 				}
 			}
 		}
 		final Interceptor interceptor = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultInterceptor,
 				cfg.getInterceptor(),
 				AvailableSettings.INTERCEPTOR,
 				"interceptor",
 				Interceptor.class
 		);
 		if ( interceptor != null ) {
 			cfg.setInterceptor( interceptor );
 		}
 		final NamingStrategy namingStrategy = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultNamingStrategy,
 				cfg.getNamingStrategy(),
 				AvailableSettings.NAMING_STRATEGY,
 				"naming strategy",
 				NamingStrategy.class
 		);
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		final SessionFactoryObserver observer = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				null,
 				cfg.getSessionFactoryObserver(),
 				AvailableSettings.SESSION_FACTORY_OBSERVER,
 				"SessionFactory observer",
 				SessionFactoryObserver.class
 		);
 		if ( observer != null ) {
 			cfg.setSessionFactoryObserver( observer );
 		}
 
 		final IdentifierGeneratorStrategyProvider strategyProvider = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				null,
 				null,
 				AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER,
 				"Identifier generator strategy provider",
 				IdentifierGeneratorStrategyProvider.class
 		);
 		if ( strategyProvider != null ) {
 			final MutableIdentifierGeneratorFactory identifierGeneratorFactory = cfg.getIdentifierGeneratorFactory();
 			for ( Map.Entry<String,Class<?>> entry : strategyProvider.getStrategies().entrySet() ) {
 				identifierGeneratorFactory.register( entry.getKey(), entry.getValue() );
 			}
 		}
 
 		if ( jaccKeys.size() > 0 ) {
 			addSecurity( jaccKeys, preparedProperties, workingVars );
 		}
 
 		//some spec compliance checking
 		//TODO centralize that?
         if (!"true".equalsIgnoreCase(cfg.getProperty(Environment.AUTOCOMMIT))) LOG.jdbcAutoCommitFalseBreaksEjb3Spec(Environment.AUTOCOMMIT);
         discardOnClose = preparedProperties.getProperty(AvailableSettings.DISCARD_PC_ON_CLOSE).equals("true");
 		return this;
 	}
 
 	private <T> T instantiateCustomClassFromConfiguration(
 			Properties preparedProperties,
 			T defaultObject,
 			T cfgObject,
 			String propertyName,
 			String classDescription,
 			Class<T> objectClass) {
 		if ( preparedProperties.containsKey( propertyName )
 				&& ( cfgObject == null || cfgObject.equals( defaultObject ) ) ) {
 			//cfg.setXxx has precedence over configuration file
 			String className = preparedProperties.getProperty( propertyName );
 			try {
 				Class<T> clazz = classForName( className );
 				return clazz.newInstance();
 				//cfg.setInterceptor( (Interceptor) instance.newInstance() );
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to find " + classDescription + " class: " + className, e
 				);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to access " + classDescription + " class: " + className, e
 				);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to instantiate " + classDescription + " class: " + className, e
 				);
 			}
 			catch (ClassCastException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + classDescription + " class does not implement " + objectClass + " interface: "
 								+ className, e
 				);
 			}
 		}
 		return null;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addClassesToSessionFactory(Map workingVars) {
 		if ( workingVars.containsKey( AvailableSettings.CLASS_NAMES ) ) {
 			Collection<String> classNames = (Collection<String>) workingVars.get(
 					AvailableSettings.CLASS_NAMES
 			);
 			addNamedAnnotatedClasses( this, classNames, workingVars );
 		}
 
 		if ( workingVars.containsKey( PARSED_MAPPING_DOMS ) ) {
 			Collection<XmlDocument> xmlDocuments = (Collection<XmlDocument>) workingVars.get( PARSED_MAPPING_DOMS );
 			for ( XmlDocument xmlDocument : xmlDocuments ) {
 				cfg.add( xmlDocument );
 			}
 		}
 
 		//TODO apparently only used for Tests, get rid of it?
 		if ( workingVars.containsKey( AvailableSettings.LOADED_CLASSES ) ) {
 			Collection<Class> classes = (Collection<Class>) workingVars.get( AvailableSettings.LOADED_CLASSES );
 			for ( Class clazz : classes ) {
 				cfg.addAnnotatedClass( clazz );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.PACKAGE_NAMES ) ) {
 			Collection<String> packages = (Collection<String>) workingVars.get(
 					AvailableSettings.PACKAGE_NAMES
 			);
 			for ( String pkg : packages ) {
 				cfg.addPackage( pkg );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.XML_FILE_NAMES ) ) {
 			Collection<String> xmlFiles = (Collection<String>) workingVars.get( AvailableSettings.XML_FILE_NAMES );
 			for ( String xmlFile : xmlFiles ) {
 				Boolean useMetaInf = null;
 				try {
 					if ( xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						useMetaInf = true;
 					}
 					cfg.addResource( xmlFile );
 				}
 				catch( MappingNotFoundException e ) {
 					if ( ! xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						throw new PersistenceException( getExceptionHeader()
 								+ "Unable to find XML mapping file in classpath: " + xmlFile);
 					}
 					else {
 						useMetaInf = false;
 						//swallow it, the META-INF/orm.xml is optional
 					}
 				}
 				catch( MappingException me ) {
 					throw new PersistenceException( getExceptionHeader()
 								+ "Error while reading JPA XML file: " + xmlFile, me);
 				}
                 if (Boolean.TRUE.equals(useMetaInf)) {
 					LOG.exceptionHeaderFound(getExceptionHeader(), META_INF_ORM_XML);
 				}
                 else if (Boolean.FALSE.equals(useMetaInf)) {
 					LOG.exceptionHeaderNotFound(getExceptionHeader(), META_INF_ORM_XML);
 				}
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.HBXML_FILES ) ) {
 			Collection<NamedInputStream> hbmXmlFiles = (Collection<NamedInputStream>) workingVars.get(
 					AvailableSettings.HBXML_FILES
 			);
 			for ( NamedInputStream is : hbmXmlFiles ) {
 				try {
 					//addInputStream has the responsibility to close the stream
 					cfg.addInputStream( new BufferedInputStream( is.getStream() ) );
 				}
 				catch (MappingException me) {
 					//try our best to give the file name
 					if ( StringHelper.isEmpty( is.getName() ) ) {
 						throw me;
 					}
 					else {
 						throw new MappingException("Error while parsing file: " + is.getName(), me );
 					}
 				}
 			}
 		}
 	}
 
 	private String getExceptionHeader() {
         return (StringHelper.isNotEmpty(persistenceUnitName)) ? "[PersistenceUnit: " + persistenceUnitName + "] " : "";
 	}
 
 	private Properties prepareProperties(Properties properties, Map workingVars) {
 		Properties preparedProperties = new Properties();
 
 		//defaults different from Hibernate
 		preparedProperties.setProperty( Environment.RELEASE_CONNECTIONS, "auto" );
 		preparedProperties.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		//settings that always apply to a compliant EJB3
 		preparedProperties.setProperty( Environment.AUTOCOMMIT, "true" );
 		preparedProperties.setProperty( Environment.USE_IDENTIFIER_ROLLBACK, "false" );
 		preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 		preparedProperties.setProperty( AvailableSettings.DISCARD_PC_ON_CLOSE, "false" );
 		if (cfgXmlResource != null) {
 			preparedProperties.setProperty( AvailableSettings.CFG_FILE, cfgXmlResource );
 			cfgXmlResource = null;
 		}
 
 		//override the new defaults with the user defined ones
 		//copy programmatically defined properties
 		if ( cfg.getProperties() != null ) preparedProperties.putAll( cfg.getProperties() );
 		//copy them coping from configuration
 		if ( properties != null ) preparedProperties.putAll( properties );
 		//note we don't copy cfg.xml properties, since they have to be overriden
 
 		if (transactionType == null) {
 			//if it has not been set, the user use a programmatic way
 			transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		defineTransactionType(
 				preparedProperties.getProperty( AvailableSettings.TRANSACTION_TYPE ),
 				workingVars
 		);
 		boolean hasTxStrategy = StringHelper.isNotEmpty(
 				preparedProperties.getProperty( Environment.TRANSACTION_STRATEGY )
 		);
 		if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.JTA ) {
 			preparedProperties.setProperty(
 					Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName()
 			);
 		}
 		else if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName() );
 		}
         if (hasTxStrategy) LOG.overridingTransactionStrategyDangerous(Environment.TRANSACTION_STRATEGY);
 		if ( preparedProperties.getProperty( Environment.FLUSH_BEFORE_COMPLETION ).equals( "true" ) ) {
 			preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
             LOG.definingFlushBeforeCompletionIgnoredInHem(Environment.FLUSH_BEFORE_COMPLETION);
 		}
 		return preparedProperties;
 	}
 
 	private Class classForName(String className) throws ClassNotFoundException {
 		return ReflectHelper.classForName( className, this.getClass() );
 	}
 
 	private void setCacheStrategy(String propertyKey, Map properties, boolean isClass, Map workingVars) {
 		String role = propertyKey.substring(
 				( isClass ? AvailableSettings.CLASS_CACHE_PREFIX
 						.length() : AvailableSettings.COLLECTION_CACHE_PREFIX.length() )
 						+ 1
 		);
 		//dot size added
 		String value = (String) properties.get( propertyKey );
 		StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			error.append(
 					isClass ? AvailableSettings.CLASS_CACHE_PREFIX : AvailableSettings.COLLECTION_CACHE_PREFIX
 			);
 			error.append( ": " ).append( propertyKey ).append( " " ).append( value );
 			throw new PersistenceException( getExceptionHeader() + error.toString() );
 		}
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		if ( isClass ) {
 			boolean lazyProperty = true;
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 			cfg.setCacheConcurrencyStrategy( role, usage, region, lazyProperty );
 		}
 		else {
 			cfg.setCollectionCacheConcurrencyStrategy( role, usage, region );
 		}
 	}
 
 	private void addSecurity(List<String> keys, Map properties, Map workingVars) {
         LOG.debugf("Adding security");
 		if ( !properties.containsKey( AvailableSettings.JACC_CONTEXT_ID ) ) {
 			throw new PersistenceException( getExceptionHeader() +
 					"Entities have been configured for JACC, but "
 							+ AvailableSettings.JACC_CONTEXT_ID
 							+ " has not been set"
 			);
 		}
 		String contextId = (String) properties.get( AvailableSettings.JACC_CONTEXT_ID );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 
 		int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 
 		for ( String key : keys ) {
 			JACCConfiguration jaccCfg = new JACCConfiguration( contextId );
 			try {
 				String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 				int classStart = roleStart + role.length() + 1;
 				String clazz = key.substring( classStart, key.length() );
 				String actions = (String) properties.get( key );
 				jaccCfg.addPermission( role, clazz, actions );
 			}
 			catch (IndexOutOfBoundsException e) {
 				throw new PersistenceException( getExceptionHeader() +
 						"Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 			}
 		}
 	}
 
 	private void addNamedAnnotatedClasses(
 			Ejb3Configuration cfg, Collection<String> classNames, Map workingVars
 	) {
 		for ( String name : classNames ) {
 			try {
 				Class clazz = classForName( name );
 				cfg.addAnnotatedClass( clazz );
 			}
 			catch (ClassNotFoundException cnfe) {
 				Package pkg;
 				try {
 					pkg = classForName( name + ".package-info" ).getPackage();
 				}
 				catch (ClassNotFoundException e) {
 					pkg = null;
 				}
                 if (pkg == null) throw new PersistenceException(getExceptionHeader() + "class or package not found", cnfe);
                 else cfg.addPackage(name);
 			}
 		}
 	}
 
 	public Ejb3Configuration addProperties(Properties props) {
 		cfg.addProperties( props );
 		return this;
 	}
 
 	public Ejb3Configuration addAnnotatedClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addAnnotatedClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration configure(String resource) throws HibernateException {
 		//delay the call to configure to allow proper addition of all annotated classes (EJB-330)
 		if (cfgXmlResource != null)
 			throw new PersistenceException("configure(String) method already called for " + cfgXmlResource);
 		this.cfgXmlResource = resource;
 		return this;
 	}
 
 	public Ejb3Configuration addPackage(String packageName) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addPackage( packageName );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(String xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(File xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public void buildMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.buildMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Iterator getClassMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			return cfg.getClassMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Iterator getTableMappings() {
 		return cfg.getTableMappings();
 	}
 
 	public PersistentClass getClassMapping(String persistentClass) {
 		return cfg.getClassMapping( persistentClass );
 	}
 
 	public org.hibernate.mapping.Collection getCollectionMapping(String role) {
 		return cfg.getCollectionMapping( role );
 	}
 
 	public void setEntityResolver(EntityResolver entityResolver) {
 		cfg.setEntityResolver( entityResolver );
 	}
 
 	public Map getNamedQueries() {
 		return cfg.getNamedQueries();
 	}
 
 	public Interceptor getInterceptor() {
 		return cfg.getInterceptor();
 	}
 
 	public Properties getProperties() {
 		return cfg.getProperties();
 	}
 
 	public Ejb3Configuration setInterceptor(Interceptor interceptor) {
 		cfg.setInterceptor( interceptor );
 		return this;
 	}
 
 	public Ejb3Configuration setProperties(Properties properties) {
 		cfg.setProperties( properties );
 		return this;
 	}
 
 	public Map getFilterDefinitions() {
 		return cfg.getFilterDefinitions();
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		cfg.addFilterDefinition( definition );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		cfg.addAuxiliaryDatabaseObject( object );
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return cfg.getNamingStrategy();
 	}
 
 	public Ejb3Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		cfg.setNamingStrategy( namingStrategy );
 		return this;
 	}
 
 	public Ejb3Configuration setSessionFactoryObserver(SessionFactoryObserver observer) {
 		cfg.setSessionFactoryObserver( observer );
 		return this;
 	}
 
 	/**
 	 * This API is intended to give a read-only configuration.
 	 * It is sueful when working with SchemaExport or any Configuration based
 	 * tool.
 	 * DO NOT update configuration through it.
 	 */
 	public Configuration getHibernateConfiguration() {
 		//TODO make it really read only (maybe through proxying)
 		return cfg;
 	}
 
 	public Ejb3Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addInputStream( xmlInputStream );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addResource( path );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path, ClassLoader classLoader) throws MappingException {
 		cfg.addResource( path, classLoader );
 		return this;
 	}
 
 	private enum XML_SEARCH {
 		HBM,
 		ORM_XML,
 		BOTH,
 		NONE;
 
 		public static XML_SEARCH getType(boolean searchHbm, boolean searchOrm) {
 			return searchHbm ?
 					searchOrm ? XML_SEARCH.BOTH : XML_SEARCH.HBM :
 					searchOrm ? XML_SEARCH.ORM_XML : XML_SEARCH.NONE;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
index 632d22087f..5bb70b01c2 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
@@ -1,287 +1,286 @@
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
 package org.hibernate.ejb.test;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Persistence;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.Ejb3Configuration;
-import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.ejb.EntityManagerFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * A base class for all ejb tests.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public abstract class BaseEntityManagerFunctionalTestCase extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( BaseEntityManagerFunctionalTestCase.class );
 
 	// IMPL NOTE : Here we use @Before and @After (instead of @BeforeClassOnce and @AfterClassOnce like we do in
 	// BaseCoreFunctionalTestCase) because the old HEM test methodology was to create an EMF for each test method.
 
 	private static final Dialect dialect = Dialect.getDialect();
 
 	private Ejb3Configuration ejb3Configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
-	private EntityManagerFactory entityManagerFactory;
+	private EntityManagerFactoryImpl entityManagerFactory;
 
 	private EntityManager em;
 	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	protected EntityManagerFactory entityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Before
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void buildEntityManagerFactory() throws Exception {
 		log.trace( "Building session factory" );
 		ejb3Configuration = buildConfiguration();
 		ejb3Configuration.configure( getConfig() );
 		afterConfigurationBuilt( ejb3Configuration );
-		serviceRegistry = buildServiceRegistry( ejb3Configuration.getHibernateConfiguration() );
-		applyServices( serviceRegistry );
-		entityManagerFactory = ejb3Configuration.buildEntityManagerFactory( serviceRegistry );
+
+		entityManagerFactory = (EntityManagerFactoryImpl) ejb3Configuration.buildEntityManagerFactory( bootstrapRegistryBuilder() );
+		serviceRegistry = (BasicServiceRegistryImpl) ( (SessionFactoryImpl) entityManagerFactory.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
+
 		afterEntityManagerFactoryBuilt();
 	}
 
+	private BootstrapServiceRegistryImpl.Builder bootstrapRegistryBuilder() {
+		return BootstrapServiceRegistryImpl.builder();
+	}
+
 	protected Ejb3Configuration buildConfiguration() {
 		Ejb3Configuration ejb3Cfg = constructConfiguration();
 		addMappings( ejb3Cfg.getHibernateConfiguration() );
 		return ejb3Cfg;
 	}
 
 	protected Ejb3Configuration constructConfiguration() {
 		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
 		if ( createSchema() ) {
 			ejb3Configuration.getHibernateConfiguration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return ejb3Configuration;
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource( mapping, getClass().getClassLoader() );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected Map getConfig() {
 		Map<Object, Object> config = loadProperties();
 		ArrayList<Class> classes = new ArrayList<Class>();
 
 		classes.addAll( Arrays.asList( getAnnotatedClasses() ) );
 		config.put( AvailableSettings.LOADED_CLASSES, classes );
 		for ( Map.Entry<Class, String> entry : getCachedClasses().entrySet() ) {
 			config.put( AvailableSettings.CLASS_CACHE_PREFIX + "." + entry.getKey().getName(), entry.getValue() );
 		}
 		for ( Map.Entry<String, String> entry : getCachedCollections().entrySet() ) {
 			config.put( AvailableSettings.COLLECTION_CACHE_PREFIX + "." + entry.getKey(), entry.getValue() );
 		}
 		if ( getEjb3DD().length > 0 ) {
 			ArrayList<String> dds = new ArrayList<String>();
 			dds.addAll( Arrays.asList( getEjb3DD() ) );
 			config.put( AvailableSettings.XML_FILE_NAMES, dds );
 		}
 
 		addConfigOptions( config );
 		return config;
 	}
 
 	protected void addConfigOptions(Map options) {
 	}
 
 	private Properties loadProperties() {
 		Properties props = new Properties();
 		InputStream stream = Persistence.class.getResourceAsStream( "/hibernate.properties" );
 		if ( stream != null ) {
 			try {
 				props.load( stream );
 			}
 			catch ( Exception e ) {
 				throw new RuntimeException( "could not load hibernate.properties" );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch ( IOException ignored ) {
 				}
 			}
 		}
 		return props;
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	public Map<Class, String> getCachedClasses() {
 		return new HashMap<Class, String>();
 	}
 
 	public Map<String, String> getCachedCollections() {
 		return new HashMap<String, String>();
 	}
 
 	public String[] getEjb3DD() {
 		return new String[] { };
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void afterConfigurationBuilt(Ejb3Configuration ejb3Configuration) {
 	}
 
-	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
-		Properties properties = new Properties();
-		properties.putAll( configuration.getProperties() );
-		Environment.verifyProperties( properties );
-		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
-	}
-
 	@SuppressWarnings( {"UnusedParameters"})
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
+	protected void applyServices(ServiceRegistryBuilder registryBuilder) {
 	}
 
 	protected void afterEntityManagerFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 
 	@After
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void releaseResources() {
 		releaseUnclosedEntityManagers();
 
 		if ( entityManagerFactory != null ) {
 			entityManagerFactory.close();
 		}
 
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 
 	private void releaseUnclosedEntityManagers() {
 		releaseUnclosedEntityManager( this.em );
 
 		for ( EntityManager isolatedEm : isolatedEms ) {
 			releaseUnclosedEntityManager( isolatedEm );
 		}
 	}
 
 	private void releaseUnclosedEntityManager(EntityManager em) {
 		if ( em == null ) {
 			return;
 		}
 		if ( em.getTransaction().isActive() ) {
 			em.getTransaction().rollback();
             log.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
 		}
 		if ( em.isOpen() ) {
 			// as we open an EM before the test runs, it will still be open if the test uses a custom EM.
 			// or, the person may have forgotten to close. So, do not raise a "fail", but log the fact.
 			em.close();
             log.warn("The EntityManager is not closed. Closing it.");
 		}
 	}
 
 	protected EntityManager getOrCreateEntityManager() {
 		if ( em == null || !em.isOpen() ) {
 			em = entityManagerFactory.createEntityManager();
 		}
 		return em;
 	}
 
 	protected EntityManager createIsolatedEntityManager() {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager();
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createIsolatedEntityManager(Map props) {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager(props);
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createEntityManager(Map properties) {
 		// always reopen a new EM and close the existing one
 		if ( em != null && em.isOpen() ) {
 			em.close();
 		}
 		em = entityManagerFactory.createEntityManager( properties );
 		return em;
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
index bbfc128705..318b0e3c1d 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
@@ -1,139 +1,139 @@
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
 package org.hibernate.envers.test;
 
+import javax.persistence.EntityManager;
+import java.io.IOException;
+import java.util.Properties;
+
 import org.hibernate.cfg.Environment;
 import org.hibernate.ejb.Ejb3Configuration;
+import org.hibernate.ejb.EntityManagerFactoryImpl;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
 import org.hibernate.envers.event.EnversIntegrator;
-import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.testing.AfterClassOnce;
-import org.hibernate.testing.BeforeClassOnce;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+
 import org.junit.Before;
 
-import javax.persistence.EntityManager;
-import javax.persistence.EntityManagerFactory;
-import java.io.IOException;
-import java.util.Properties;
+import org.hibernate.testing.AfterClassOnce;
+import org.hibernate.testing.BeforeClassOnce;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractEntityTest extends AbstractEnversTest {
-    private EntityManagerFactory emf;
+    private EntityManagerFactoryImpl emf;
     private EntityManager entityManager;
     private AuditReader auditReader;
     private Ejb3Configuration cfg;
 	private BasicServiceRegistryImpl serviceRegistry;
     private boolean audited;
 
     public abstract void configure(Ejb3Configuration cfg);
 
     public void addConfigurationProperties(Properties configuration) { }
 
     private void closeEntityManager() {
         if (entityManager != null) {
             entityManager.close();
             entityManager = null;
         }
     }
 
     @Before
     public void newEntityManager() {
         closeEntityManager();
         
         entityManager = emf.createEntityManager();
 
         if (audited) {
             auditReader = AuditReaderFactory.get(entityManager);
         }
     }
 
     @BeforeClassOnce
     public void init() throws IOException {
         init(true, getAuditStrategy());
     }
 
     protected void init(boolean audited, String auditStrategy) throws IOException {
         this.audited = audited;
 
         Properties configurationProperties = new Properties();
         if (!audited) {
 			configurationProperties.setProperty(EnversIntegrator.AUTO_REGISTER, "false");
         }
 
         configurationProperties.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
         configurationProperties.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
         configurationProperties.setProperty(Environment.DRIVER, "org.h2.Driver");
         configurationProperties.setProperty(Environment.USER, "sa");
 
         // Separate database for each test class
         configurationProperties.setProperty(Environment.URL, "jdbc:h2:mem:" + this.getClass().getName() + ";DB_CLOSE_DELAY=-1");
 
         if (auditStrategy != null && !"".equals(auditStrategy)) {
             configurationProperties.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
         }
 
         addConfigurationProperties(configurationProperties);
 
         cfg = new Ejb3Configuration();
         configure(cfg);
         cfg.configure(configurationProperties);
 
-        serviceRegistry = createServiceRegistry(cfg);
+        emf = (EntityManagerFactoryImpl) cfg.buildEntityManagerFactory( createBootstrapRegistryBuilder() );
 
-        emf = cfg.buildEntityManagerFactory( serviceRegistry );
+		serviceRegistry = (BasicServiceRegistryImpl) ( (SessionFactoryImpl) emf.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
 
         newEntityManager();
     }
 
-    private BasicServiceRegistryImpl createServiceRegistry(Ejb3Configuration configuration) {
-        Properties properties = new Properties();
-		properties.putAll(configuration.getHibernateConfiguration().getProperties());
-		ConfigurationHelper.resolvePlaceHolders(properties);
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder(properties).buildServiceRegistry();
-    }
+	private BootstrapServiceRegistryImpl.Builder createBootstrapRegistryBuilder() {
+		return BootstrapServiceRegistryImpl.builder();
+	}
+
 
-    @AfterClassOnce
+	@AfterClassOnce
     public void close() {
         closeEntityManager();
         emf.close();
 		serviceRegistry.destroy();
     }
 
     public EntityManager getEntityManager() {
         return entityManager;
     }
 
     public AuditReader getAuditReader() {
         return auditReader;
     }
 
     public Ejb3Configuration getCfg() {
         return cfg;
     }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index d14652454e..dab82e9937 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,226 +1,226 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Set;
 
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.spi.GeneralDataRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.service.ServiceRegistryBuilder;
 
 import org.junit.Test;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
 	private static final Logger log = Logger.getLogger( AbstractGeneralDataRegionTestCase.class );
 
 	protected static final String KEY = "Key";
 
 	protected static final String VALUE1 = "value1";
 	protected static final String VALUE2 = "value2";
 
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, false, true );
 	}
 
 	@Override
 	protected void putInRegion(Region region, Object key, Object value) {
 		((GeneralDataRegion) region).put( key, value );
 	}
 
 	@Override
 	protected void removeFromRegion(Region region, Object key) {
 		((GeneralDataRegion) region).evict( key );
 	}
 
 	@Test
 	public void testEvict() throws Exception {
 		evictOrRemoveTest();
 	}
 
 	private void evictOrRemoveTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 		boolean invalidation = localCache.isClusteredInvalidation();
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ), cfg.getProperties(), null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// allow async propagation
 		sleep( 250 );
 		Object expected = invalidation ? null : VALUE1;
 		assertEquals( expected, remoteRegion.get( KEY ) );
 
 		localRegion.evict( KEY );
 
 		// allow async propagation
 		sleep( 250 );
 		assertEquals( null, localRegion.get( KEY ) );
 		assertEquals( null, remoteRegion.get( KEY ) );
 	}
 
 	protected abstract String getStandardRegionName(String regionPrefix);
 
 	/**
 	 * Test method for {@link QueryResultsRegion#evictAll()}.
 	 * <p/>
 	 * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
 	 * CollectionRegionAccessStrategy API.
 	 */
 	public void testEvictAll() throws Exception {
 		evictOrRemoveAllTest( "entity" );
 	}
 
 	private void evictOrRemoveAllTest(String configName) throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter remoteCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		Set keys = localCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		keys = remoteCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		remoteRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, remoteRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		localRegion.evictAll();
 
 		// allow async propagation
 		sleep( 250 );
 		// This should re-establish the region root node in the optimistic case
 		assertNull( localRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( localCache.keySet() ) );
 
 		// Re-establishing the region root on the local node doesn't
 		// propagate it to other nodes. Do a get on the remote node to re-establish
 		// This only adds a node in the case of optimistic locking
 		assertEquals( null, remoteRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( remoteCache.keySet() ) );
 
 		assertEquals( "local is clean", null, localRegion.get( KEY ) );
 		assertEquals( "remote is clean", null, remoteRegion.get( KEY ) );
 	}
 
 	protected void rollback() {
 		try {
 			BatchModeTransactionManager.getInstance().rollback();
 		}
 		catch (Exception e) {
 			log.error( e.getMessage(), e );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
index 037b26ca90..98544a28ff 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
@@ -1,139 +1,141 @@
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Defines the environment for a node.
  *
  * @author Steve Ebersole
  */
 public class NodeEnvironment {
 	private final Configuration configuration;
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private InfinispanRegionFactory regionFactory;
 
 	private Map<String,EntityRegionImpl> entityRegionMap;
 	private Map<String,CollectionRegionImpl> collectionRegionMap;
 
 	public NodeEnvironment(Configuration configuration) {
 		this.configuration = configuration;
 	}
 
 	public Configuration getConfiguration() {
 		return configuration;
 	}
 
 	public BasicServiceRegistryImpl getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public EntityRegionImpl getEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( entityRegionMap == null ) {
 			entityRegionMap = new HashMap<String, EntityRegionImpl>();
 			return buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		EntityRegionImpl region = entityRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		return region;
 	}
 
 	private EntityRegionImpl buildAndStoreEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		EntityRegionImpl region = (EntityRegionImpl) regionFactory.buildEntityRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		entityRegionMap.put( name, region );
 		return region;
 	}
 
 	public CollectionRegionImpl getCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( collectionRegionMap == null ) {
 			collectionRegionMap = new HashMap<String, CollectionRegionImpl>();
 			return buildAndStoreCollectionRegion( name, cacheDataDescription );
 		}
 		CollectionRegionImpl region = collectionRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreCollectionRegion( name, cacheDataDescription );
 			collectionRegionMap.put( name, region );
 		}
 		return region;
 	}
 
 	private CollectionRegionImpl buildAndStoreCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		CollectionRegionImpl region;
 		region = (CollectionRegionImpl) regionFactory.buildCollectionRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		return region;
 	}
 
 	public void prepare() throws Exception {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configuration.getProperties() ).buildServiceRegistry();
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+				.applySettings( configuration.getProperties() )
+				.buildServiceRegistry();
 		regionFactory = CacheTestUtil.startRegionFactory( serviceRegistry, configuration );
 	}
 
 	public void release() throws Exception {
 		if ( entityRegionMap != null ) {
 			for ( EntityRegionImpl region : entityRegionMap.values() ) {
 				region.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				region.getCacheAdapter().stop();
 			}
 			entityRegionMap.clear();
 		}
 		if ( collectionRegionMap != null ) {
 			for ( CollectionRegionImpl collectionRegion : collectionRegionMap.values() ) {
 				collectionRegion.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				collectionRegion.getCacheAdapter().stop();
 			}
 			collectionRegionMap.clear();
 		}
 		if ( regionFactory != null ) {
 // Currently the RegionFactory is shutdown by its registration with the CacheTestSetup from CacheTestUtil when built
 			regionFactory.stop();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index cc3954a01d..749558c308 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,334 +1,334 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.query;
 
 import java.util.Properties;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.internal.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.service.ServiceRegistryBuilder;
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests of QueryResultRegionImpl.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 	private static final Logger log = Logger.getLogger( QueryRegionImplTestCase.class );
 
 	@Override
 	protected Region createRegion(
 			InfinispanRegionFactory regionFactory,
 			String regionName,
 			Properties properties,
 			CacheDataDescription cdd) {
 		return regionFactory.buildQueryResultsRegion( regionName, properties );
 	}
 
 	@Override
 	protected String getStandardRegionName(String regionPrefix) {
 		return regionPrefix + "/" + StandardQueryCache.class.getName();
 	}
 
 	@Override
 	protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
 		return CacheAdapterImpl.newInstance( regionFactory.getCacheManager().getCache( "local-query" ) );
 	}
 
 	@Override
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildCustomQueryCacheConfiguration( "test", "replicated-query" );
 	}
 
 	private void putDoesNotBlockGetTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		final CountDownLatch readerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread reader = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Transaction began, get value for key" );
 					assertTrue( VALUE2.equals( region.get( KEY ) ) == false );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (AssertionFailedError e) {
 					holder.a1 = e;
 					rollback();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					readerLatch.countDown();
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Put value2" );
 					region.put( KEY, VALUE2 );
 					log.debug( "Put finished for value2, await writer latch" );
 					writerLatch.await();
 					log.debug( "Writer latch finished" );
 					BatchModeTransactionManager.getInstance().commit();
 					log.debug( "Transaction committed" );
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		reader.setDaemon( true );
 		writer.setDaemon( true );
 
 		writer.start();
 		assertFalse( "Writer is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		// Start the reader
 		reader.start();
 		assertTrue( "Reader finished promptly", readerLatch.await( 1000000000, TimeUnit.MILLISECONDS ) );
 
 		writerLatch.countDown();
 		assertTrue( "Reader finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		assertEquals( VALUE2, region.get( KEY ) );
 
 		if ( holder.a1 != null ) {
 			throw holder.a1;
 		}
 		else if ( holder.a2 != null ) {
 			throw holder.a2;
 		}
 
 		assertEquals( "writer saw no exceptions", null, holder.e1 );
 		assertEquals( "reader saw no exceptions", null, holder.e2 );
 	}
 
 	public void testGetDoesNotBlockPut() throws Exception {
 		getDoesNotBlockPutTest();
 	}
 
 	private void getDoesNotBlockPutTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+				new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		// final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
 		final CacheAdapter jbc = getInfinispanCache( regionFactory );
 
 		final CountDownLatch blockerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread blocker = new Thread() {
 
 			@Override
 			public void run() {
 				// Fqn toBlock = new Fqn(rootFqn, KEY);
 				GetBlocker blocker = new GetBlocker( blockerLatch, KEY );
 				try {
 					jbc.addListener( blocker );
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.get( KEY );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					jbc.removeListener( blocker );
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 
 			@Override
 			public void run() {
 				try {
 					writerLatch.await();
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.put( KEY, VALUE2 );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		blocker.setDaemon( true );
 		writer.setDaemon( true );
 
 		boolean unblocked = false;
 		try {
 			blocker.start();
 			writer.start();
 
 			assertFalse( "Blocker is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 			// Start the writer
 			writerLatch.countDown();
 			assertTrue( "Writer finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 			blockerLatch.countDown();
 			unblocked = true;
 
 			if ( IsolationLevel.REPEATABLE_READ.equals( jbc.getConfiguration().getIsolationLevel() ) ) {
 				assertEquals( VALUE1, region.get( KEY ) );
 			}
 			else {
 				assertEquals( VALUE2, region.get( KEY ) );
 			}
 
 			if ( holder.a1 != null ) {
 				throw holder.a1;
 			}
 			else if ( holder.a2 != null ) {
 				throw holder.a2;
 			}
 
 			assertEquals( "blocker saw no exceptions", null, holder.e1 );
 			assertEquals( "writer saw no exceptions", null, holder.e2 );
 		}
 		finally {
 			if ( !unblocked ) {
 				blockerLatch.countDown();
 			}
 		}
 	}
 
 	@Listener
 	public class GetBlocker {
 
 		private CountDownLatch latch;
 		// private Fqn fqn;
 		private Object key;
 
 		GetBlocker(
 				CountDownLatch latch,
 				Object key
 		) {
 			this.latch = latch;
 			this.key = key;
 		}
 
 		@CacheEntryVisited
 		public void nodeVisisted(CacheEntryVisitedEvent event) {
 			if ( event.isPre() && event.getKey().equals( key ) ) {
 				try {
 					latch.await();
 				}
 				catch (InterruptedException e) {
 					log.error( "Interrupted waiting for latch", e );
 				}
 			}
 		}
 	}
 
 	private class ExceptionHolder {
 		Exception e1;
 		Exception e2;
 		AssertionFailedError a1;
 		AssertionFailedError a2;
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index 164d998dda..9a23d279eb 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,212 +1,212 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.timestamp;
 
 import java.util.Properties;
 
 import org.infinispan.AdvancedCache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.Event;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.ClassLoaderAwareCache;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.service.ServiceRegistryBuilder;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.AccountHolder;
 import org.hibernate.test.cache.infinispan.functional.classloader.SelectedClassnameClassLoader;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
     @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("timestamps"));
    }
 
    public void testClearTimestampsRegionInIsolated() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+			  new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 			  cfg,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       Configuration cfg2 = createConfiguration();
       InfinispanRegionFactory regionFactory2 = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
+			  new ServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
 			  cfg2,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       TimestampsRegionImpl region = (TimestampsRegionImpl) regionFactory.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg.getProperties());
       TimestampsRegionImpl region2 = (TimestampsRegionImpl) regionFactory2.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 //      QueryResultsRegion region2 = regionFactory2.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 
 //      ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //      Thread.currentThread().setContextClassLoader(cl.getParent());
 //      log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
       region.getCacheAdapter().withFlags(FlagAdapter.FORCE_SYNCHRONOUS).put(acct, "boo");
 
 //      region.put(acct, "boo");
 //
 //      region.evictAll();
 
 //      Account acct = new Account();
 //      acct.setAccountHolder(new AccountHolder());
 
 
 
    }
 
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildConfiguration("test", MockInfinispanRegionFactory.class, false, true);
    }
 
    public static class MockInfinispanRegionFactory extends InfinispanRegionFactory {
 
       public MockInfinispanRegionFactory() {
       }
 
       public MockInfinispanRegionFactory(Properties props) {
          super(props);
       }
 
 //      @Override
 //      protected TimestampsRegionImpl createTimestampsRegion(CacheAdapter cacheAdapter, String regionName) {
 //         return new MockTimestampsRegionImpl(cacheAdapter, regionName, getTransactionManager(), this);
 //      }
 
       @Override
       protected ClassLoaderAwareCache createCacheWrapper(AdvancedCache cache) {
          return new ClassLoaderAwareCache(cache, Thread.currentThread().getContextClassLoader()) {
             @Override
             public void addListener(Object listener) {
                super.addListener(new MockClassLoaderAwareListener(listener, this));
             }
          };
       }
 
       //      @Override
 //      protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
 //         try {
 //            EmbeddedCacheManager manager = new DefaultCacheManager(InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
 //            org.infinispan.config.Configuration ispnCfg = new org.infinispan.config.Configuration();
 //            ispnCfg.setCacheMode(org.infinispan.config.Configuration.CacheMode.REPL_SYNC);
 //            manager.defineConfiguration("timestamps", ispnCfg);
 //            return manager;
 //         } catch (IOException e) {
 //            throw new CacheException("Unable to create default cache manager", e);
 //         }
 //      }
 
       @Listener      
       public static class MockClassLoaderAwareListener extends ClassLoaderAwareCache.ClassLoaderAwareListener {
          MockClassLoaderAwareListener(Object listener, ClassLoaderAwareCache cache) {
             super(listener, cache);
          }
 
          @CacheEntryActivated
          @CacheEntryCreated
          @CacheEntryEvicted
          @CacheEntryInvalidated
          @CacheEntryLoaded
          @CacheEntryModified
          @CacheEntryPassivated
          @CacheEntryRemoved
          @CacheEntryVisited
          public void event(Event event) throws Throwable {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
             String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
             SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
             Thread.currentThread().setContextClassLoader(visible);
             super.event(event);
             Thread.currentThread().setContextClassLoader(cl);            
          }
       }
    }
 
 //   @Listener
 //   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
 //
 //      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
 //         super(cacheAdapter, name, transactionManager, factory);
 //      }
 //
 //      @CacheEntryModified
 //      public void nodeModified(CacheEntryModifiedEvent event) {
 ////         ClassLoader cl = Thread.currentThread().getContextClassLoader();
 ////         String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
 ////         String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
 ////         SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
 ////         Thread.currentThread().setContextClassLoader(visible);
 //         super.nodeModified(event);
 ////         Thread.currentThread().setContextClassLoader(cl);
 //      }
 //   }
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index 9345614245..96d9626575 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
@@ -1,47 +1,49 @@
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
 package org.hibernate.testing;
 
 import java.util.Map;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
 	public static BasicServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
 	public static BasicServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
-		return (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( serviceRegistryConfig ).buildServiceRegistry();
+		return (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder()
+				.applySettings( serviceRegistryConfig )
+				.buildServiceRegistry();
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index aac161c36e..60a356ad4e 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,490 +1,504 @@
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
 package org.hibernate.testing.junit4;
 
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 import org.hibernate.testing.cache.CachingRegionFactory;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"deprecation"} )
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final String USE_NEW_METADATA_MAPPINGS = "hibernate.test.new_metadata_mappings";
 
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private boolean isMetadataUsed;
 	private Configuration configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
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
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void buildSessionFactory() {
 		// for now, build the configuration to get all the property settings
 		configuration = constructAndConfigureConfiguration();
 		serviceRegistry = buildServiceRegistry( configuration );
 		isMetadataUsed = serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				USE_NEW_METADATA_MAPPINGS,
 				new ConfigurationService.Converter<Boolean>() {
 					@Override
 					public Boolean convert(Object value) {
 						return Boolean.parseBoolean( ( String ) value );
 					}
 				},
 				false
 		);
 		if ( isMetadataUsed ) {
 			sessionFactory = ( SessionFactoryImplementor ) buildMetadata( serviceRegistry ).buildSessionFactory();
 		}
 		else {
 			// this is done here because Configuration does not currently support 4.0 xsd
 			afterConstructAndConfigureConfiguration( configuration );
 			sessionFactory = ( SessionFactoryImplementor ) configuration.buildSessionFactory( serviceRegistry );
 		}
 		afterSessionFactoryBuilt();
 	}
 
 	private MetadataImplementor buildMetadata(BasicServiceRegistry serviceRegistry) {
 		 	MetadataSources sources = new MetadataSources( serviceRegistry );
 			addMappings( sources );
 			return (MetadataImplementor) sources.buildMetadata();
 	}
 
 	// TODO: is this still needed?
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructAndConfigureConfiguration();
 		afterConstructAndConfigureConfiguration( cfg );
 		return cfg;
 	}
 
 	private Configuration constructAndConfigureConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		return cfg;
 	}
 
 	private void afterConstructAndConfigureConfiguration(Configuration cfg) {
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
 				.setProperty(Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName()  );
 		configuration.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected void addMappings(MetadataSources sources) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				sources.addResource(
 						getBaseForMappings() + mapping
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				sources.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				sources.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				sources.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( properties ).buildServiceRegistry();
-		applyServices( serviceRegistry );
-		return serviceRegistry;
+
+		final BootstrapServiceRegistryImpl bootstrapServiceRegistry = generateBootstrapRegistry( properties );
+		ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder( bootstrapServiceRegistry )
+				.applySettings( properties );
+		prepareBasicRegistryBuilder( registryBuilder );
+		return (BasicServiceRegistryImpl) registryBuilder.buildServiceRegistry();
+	}
+
+	protected BootstrapServiceRegistryImpl generateBootstrapRegistry(Properties properties) {
+		final BootstrapServiceRegistryImpl.Builder builder = BootstrapServiceRegistryImpl.builder();
+		prepareBootstrapRegistryBuilder( builder );
+		return builder.build();
+	}
+
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
 	}
 
-	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
+	protected void prepareBasicRegistryBuilder(ServiceRegistryBuilder serviceRegistryBuilder) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		serviceRegistry.destroy();
 
 		serviceRegistry = buildServiceRegistry( configuration );
 		if ( isMetadataUsed ) {
 			// need to rebuild metadata because serviceRegistry was recreated
 			sessionFactory = ( SessionFactoryImplementor ) buildMetadata( serviceRegistry ).buildSessionFactory();
 		}
 		else {
 			sessionFactory = ( SessionFactoryImplementor ) configuration.buildSessionFactory( serviceRegistry );
 		}
 		afterSessionFactoryBuilt();
 	}
 
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
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
 						l = Integer.valueOf( 0 );
 					}
 					l = Integer.valueOf( l.intValue() + 1 ) ;
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
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.doReturningWork(
 					new AbstractReturningWork<Integer>() {
 						@Override
 						public Integer execute(Connection connection) throws SQLException {
 							return connection.getTransactionIsolation();
 						}
 					}
 			);
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 }
